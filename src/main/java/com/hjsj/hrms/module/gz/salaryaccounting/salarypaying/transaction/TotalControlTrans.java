package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTotalBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：TotalControlTrans 
 * 类描述： 报批和提交必走的数据验证方法 包括总额校验、可操作数据条数验证
 * 创建人：zhaoxg
 * 创建时间：Sep 10, 2015 4:58:39 PM
 * 修改人：zhaoxg
 * 修改时间：Sep 10, 2015 4:58:39 PM
 * 修改备注： 
 * @version
 */
public class TotalControlTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{ 
			String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别号
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		    String viewtype = (String)this.getFormHM().get("viewtype"); // 页面区分 0:薪资发放  1:审批  2:上报
		    viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
		    String type=(String)this.getFormHM().get("type");//1:报批 2：提交
		    
		    String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
			accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
			String accountingcount = (String)this.getFormHM().get("count"); //次数
			accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
			
		    SalaryTotalBo totalbo = new SalaryTotalBo(this.frameconn,this.userView,Integer.parseInt(salaryid));

		    SalaryTemplateBo gzbo=totalbo.getSalaryTemplateBo();
		    SalaryCtrlParamBo ctrlparam=gzbo.getCtrlparam(); 
			String ctrlType = ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type"); //控制方式，=1强制控制，=0仅提示。
			if(ctrlType==null||ctrlType.trim().length()==0)
				ctrlType="1";
		    StringBuffer filtersql = new StringBuffer();

		    if("1".equals(viewtype)){//薪资审批
				filtersql.append(gzbo.getfilter("salaryhistory"));
		    	String collectPoint = (String) this.getFormHM().get("collectPoint");
		    	String b0110 = "b0110";
				String e0122 = "e0122";
				if("UNUM".equals(collectPoint)){//单位+部门
					SalaryCtrlParamBo ctrl_par=gzbo.getCtrlparam();
					String orgid = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
					String deptid = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
					if(orgid.length()>0){
						b0110 = orgid;
					}
					if(deptid.length()>0){
						e0122 = deptid;
					}
					GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
					collectPoint = spbo.getCollectPointSql(b0110, e0122,"");
				}else //可能出现指标为空和null的
					collectPoint = "nullif("+collectPoint+",'')";

		    	String selectID = (String) this.getFormHM().get("selectID");
		    	String cound = (String) this.getFormHM().get("cound");//人员筛选
		    	StringBuffer buf=new StringBuffer();
				String[] records = selectID.split("#");
				for(int i=0;i<records.length;i++){
					if(records[i].trim().length()>0)
					{
						if("null".equals(records[i])){
							buf.append(" or  "+collectPoint+" is null");
						}else{
							buf.append(" or  "+collectPoint+" like '");
							buf.append(records[i]);
							buf.append("%'");
						}
					}
				}
				if(buf.length()>3)
				{
					if(!"sum".equalsIgnoreCase(selectID))
						filtersql.append(" and ("+buf.substring(3)+")");
				}
				if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
					filtersql.append(" and UserFlag='"+cound+"'");                                                
				}
				
				filtersql.append(" and ( curr_user='"+this.userView.getUserName()+"') and (sp_flag='02' or sp_flag='07')");
				accountingdate=accountingdate.replaceAll("\\.","-");
				filtersql.append(" and salaryid='"+salaryid+"' and a00z2="+Sql_switcher.dateValue(accountingdate)+" and a00z3="+accountingcount+"");
		    }else {
		    	SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
				boolean isComputeTax = bo.isComputeTax(accountingdate, accountingcount);
				if(!isComputeTax) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.reCompute")));
				}
				
				filtersql.append(gzbo.getfilter(""));
			}

		    String info = totalbo.calculateTotal(filtersql.toString(), Integer.parseInt(viewtype)+1, "是否继续？");
		    if(info!=null&&info.trim().length()>0&& "1".equals(ctrlType))
		    {
		    	info=info.replaceAll("是否继续？", "");
		    }
			this.getFormHM().put("info", info);
			this.getFormHM().put("ctrlType",ctrlType);

			String filterWhl=gzbo.getFilterAndPrivSql_ff();
			int dataCount=0;
		    //可提交数量验证
		    if("2".equals(type)&&!"1".equals(viewtype)) {
				dataCount=getCanSubmitDataCount(gzbo.getGz_tablename(),filterWhl);
			}

		    //可报批数量验证
		    if("1".equals(type)&&!"1".equals(viewtype)) {
				dataCount=getIsAppealData(gzbo.getGz_tablename(),filterWhl);
			}
			this.getFormHM().put("dataCount", dataCount);

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 是否有可报批的数据
	 * @return  0:无可报批的数据  1：有可报批的数据
	 */
	private int getIsAppealData(String tableName,String filterSql)throws GeneralException
	{
		int isData=0;
		RowSet rs=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo(tableName.toLowerCase());
			if(vo.hasAttribute("sp_flag"))
			{
				rs=dao.search("select count(A0100) from "+tableName+" where (Sp_flag='01' or Sp_flag='07') "+filterSql);
				if(rs.next())
				{
					isData=rs.getInt(1);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return isData;
	}

	/**
	 * 获取可提交数据条数
	 * @param tableName
	 * @param filterSql
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 10:55 2018/6/1
	 */
	private int getCanSubmitDataCount(String tableName,String filterSql) throws GeneralException {
		int num=0;
		RowSet rs=null;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			rs=dao.search("select count(a0100)  from "+tableName+" where 1=1 "+filterSql);
			if(rs.next())
			{
				num=rs.getInt(1);
			}
		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return num;
	}

}
