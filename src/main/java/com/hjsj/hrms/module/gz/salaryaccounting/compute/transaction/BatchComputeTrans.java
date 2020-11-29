package com.hjsj.hrms.module.gz.salaryaccounting.compute.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：BatchComputeTrans 
 * 类描述：薪资批量计算
 * 创建人：zhaoxg
 * 创建时间：Jun 4, 2015 2:25:30 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 4, 2015 2:25:30 PM
 * 修改备注： 
 * @version
 */
public class BatchComputeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");	
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		ArrayList itemids=(ArrayList)this.getFormHM().get("itemids");
		String ym=(String)this.getFormHM().get("ym"); //薪资发放月份
		ym = PubFunc.decrypt(SafeCode.decode(ym));
		String count=(String)this.getFormHM().get("count"); //薪资发放次数
		count = PubFunc.decrypt(SafeCode.decode(count));
		String viewtype=(String)this.getFormHM().get("viewtype"); //0 薪资发放 1 薪资审批 2数据上报
		viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
		String detailsql = (String) this.getFormHM().get("detailsql");//薪资审批明细表的sql片段
		detailsql = PubFunc.decrypt(SafeCode.decode(detailsql));
		try
		{			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			StringBuffer where_str = new StringBuffer();
			
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.frameconn,Integer.parseInt(salaryid),this.userView);
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid),gzbo);
			SalaryCtrlParamBo ctrlparam=gzbo.getCtrlparam();
			String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");//是否需要审批1:需要审批  否则不需要审批
			
			String allowEditSubdata="";
			SalaryLProgramBo lpbo=new SalaryLProgramBo(gzbo.getTemplatevo().getString("lprogram"));
			allowEditSubdata=lpbo.getValue(SalaryLProgramBo.CONFIRM_TYPE,"allow_edit_subdata");//参数“允许修改发放结束已提交数据” 控制已提交的数据是否能计算"0"不可以修改，"1"可以修改
			 
			String tableName = "";
			String flag = "1";
			if("1".equalsIgnoreCase(viewtype)){ //薪资审批
				tableName ="salaryhistory";
				flag = "2";
				where_str.append(" where  (( lower("+tableName+".curr_user)='"+this.userView.getUserId().toLowerCase()+"' and ( "+tableName+".sp_flag='02' or "+tableName+".sp_flag='07' ) )");
				if ("1".equals(allowEditSubdata)) {
					String privWhlStr = gzbo.getWhlByUnits("salaryhistory",true);
					where_str.append(" or ( ( ("+tableName+".AppUser is null  "+privWhlStr+"  ) or "+tableName+".AppUser Like '%;"+this.userView.getUserName()+";%' ) and   "+tableName+".sp_flag='06' ) ");
				}				
				where_str.append(") ");
				if(ym==null||ym.trim().length()==0) //没有数据就无需计算了
					return;
				String[] temps=ym.replaceAll("\\.", "-").split("-");
				where_str.append(" and "+tableName+".salaryid="+salaryid+"  and "+Sql_switcher.year(tableName+".a00z2")+"="+temps[0]+"  and "+Sql_switcher.month(tableName+".a00z2")+"="+temps[1]+" and "+tableName+".a00z3="+count);  
				 
			}
			else if("0".equalsIgnoreCase(viewtype)){
				tableName = gzbo.getGz_tablename();
				//不走审批，并且允许修改已归档数据
				if ("0".equals(flow_flag) && "1".equals(allowEditSubdata)) {
                	where_str.append(" where  sp_flag in('01','06','07')");
                }
				else
					where_str.append(" where   sp_flag in('01','07')");   
			}
			else if("2".equalsIgnoreCase(viewtype)){
                	where_str.append(" where  sp_flag2 in('01','07')");  
                	String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
        			if(manager!=null&&manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))//薪资发放非管理员
        			{
        				tableName = gzbo.getGz_tablename();
        				String whl_str=gzbo.getWhlByUnits(tableName,true);
        				if(whl_str.length()>0)
        				{
        					if(where_str.toString().trim().length()>0)
        						where_str.append(whl_str);
        					else
        						where_str.append(" where  1=1 "+whl_str);
        				}
        			}
			}
			
			String selectGzRecords = (String)this.getFormHM().get("selectGzRecords");
			int num=0;
			if(selectGzRecords!=null&&selectGzRecords.trim().length()>0) //个别计算
			{
				 
				selectGzRecords = selectGzRecords.replaceAll("＃", "#").replaceAll("／", "/"); 
				String[] selects=selectGzRecords.split("#");
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<selects.length;i++)
				{
					String[] a_selects=selects[i].split("/");
					String[] temps=a_selects[2].split("-");
					whl.append(" or  ("+tableName+".A0100 ='"+PubFunc.decrypt(a_selects[0])+"' and upper("+tableName+".nbase) ='"+PubFunc.decrypt(a_selects[1]).toUpperCase()+"' and "+Sql_switcher.year(tableName+".A00Z0")+"="+temps[0]+" and "+Sql_switcher.month(tableName+".A00Z0")+"="+temps[1]+"  and "+tableName+".A00z1 ="+a_selects[3]+" )");	 
				}
				where_str.append(" and ( "+whl.substring(3)+" ) ");
				num++;
			}
			if("1".equalsIgnoreCase(viewtype)&&num==0){ //薪资审批
				String selectID = (String)this.getFormHM().get("selectID"); //选择的记录 
				if(selectID!=null&&selectID.trim().length()>0) //汇总审批 计算
				{
					String collectPoint = (String) this.getFormHM().get("collectPoint");//汇总指标 
					StringBuffer buf=new StringBuffer();
					String b0110 = "b0110";
					String e0122 = "e0122";
					if("UNUM".equals(collectPoint)){//单位+部门 
						String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
						String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
						if(orgid.length()>0){
							b0110 = orgid;
						}
						if(deptid.length()>0){
							e0122 = deptid;
						}
						GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
						collectPoint = spbo.getCollectPointSql(b0110, e0122,tableName);
					}else //可能出现指标为空和null的
						collectPoint = "nullif("+collectPoint+",'')";
					
					String[] records = selectID.split("#");
					for(int i=0;i<records.length;i++){
						if("null".equals(records[i])){
							buf.append(" or  "+collectPoint+" is null");
						}else{
							buf.append(" or  "+collectPoint+" like '");
							buf.append(records[i]);
							buf.append("%'");
						}
					}
					if(!"sum".equalsIgnoreCase(selectID))
					{
						String temp=buf.toString().substring(3);
						buf.setLength(0);
						buf.append(" and ("+temp+")");
					
					}
					if(buf.length()>0)
						where_str.append(buf.toString());
					
				}
				else if(detailsql!=null&&detailsql.length()>0){
					where_str.append(detailsql);
				} 
			} 
			HashMap paramMap = new HashMap();
			paramMap.put("ym", ym);
			paramMap.put("count", count);
			paramMap.put("where_str", where_str.toString());
			bo.computeGz(Integer.parseInt(flag), itemids, paramMap);
			this.getFormHM().put("viewtype", viewtype);
			
			
			
			/**个税明细表动态指标*/
			TaxMxBo mxbo=new TaxMxBo(this.getFrameconn(),this.userView);
			ArrayList taxfldlist=mxbo.searchDynaItemList();
			 /**取得个税明细表额外定义的字段*/
            StringBuffer extFlds=new StringBuffer();
            StringBuffer extValues=new StringBuffer();
			DbNameBo.getExtFlds(extFlds,extValues,this.getFrameconn(),tableName,taxfldlist);
			if(extFlds.length()>0)
			{
				String aa=extValues.toString().replaceAll(",S.", ","+tableName+".");
				StringBuffer update_str=new StringBuffer("");
				int db_type=Sql_switcher.searchDbServer();//数据库类型
				if(db_type==2)//oracle
				{
					update_str.append("update gz_tax_mx set ( "+extFlds.substring(1)+" ) = (select  "+aa.substring(1)+" from "+tableName);
					update_str.append("  where gz_tax_mx.a0100="+tableName+".A0100 AND gz_tax_mx.NBASE="+tableName+".NBASE and gz_tax_mx.a00z0="+tableName+".A00z0  and gz_tax_mx.a00z1="+tableName+".A00z1 ");
					update_str.append(" and "+where_str.substring(6)+" ) where salaryid="+salaryid+" and  exists (select null  from "+tableName);
					update_str.append("  where gz_tax_mx.a0100="+tableName+".A0100 AND gz_tax_mx.NBASE="+tableName+".NBASE and gz_tax_mx.a00z0="+tableName+".A00z0  and gz_tax_mx.a00z1="+tableName+".A00z1 ");
					update_str.append(" and "+where_str.substring(6)+" ) ");
				}
				else
				{
					String set="";
					String[] temps=extFlds.toString().split(",");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0)
							set+=",gz_tax_mx."+temps[i]+"="+tableName+"."+temps[i];
					}
					
					
					update_str.append("update gz_tax_mx set   "+set.substring(1)+"  from gz_tax_mx");
					update_str.append(" left join "+tableName+"  on gz_tax_mx.a0100="+tableName+".A0100 AND gz_tax_mx.NBASE="+tableName+".NBASE and gz_tax_mx.a00z0="+tableName+".A00z0  and gz_tax_mx.a00z1="+tableName+".A00z1 ");
					update_str.append(" where gz_tax_mx.salaryid="+salaryid+" and exists (select null  from "+tableName);
					update_str.append("  where gz_tax_mx.a0100="+tableName+".A0100 AND gz_tax_mx.NBASE="+tableName+".NBASE and gz_tax_mx.a00z0="+tableName+".A00z0  and gz_tax_mx.a00z1="+tableName+".A00z1 ");
					update_str.append(" and "+where_str.substring(6)+" ) ");
				}
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update(update_str.toString());
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{

		}
	}
}
