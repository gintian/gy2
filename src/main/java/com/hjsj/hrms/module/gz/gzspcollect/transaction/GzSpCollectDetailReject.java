package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GzSpCollectDetailReject 
 * 类描述： 明细界面个别驳回
 * 创建人：zhaoxg
 * 创建时间：Dec 29, 2015 11:36:42 AM
 * 修改人：zhaoxg
 * 修改时间：Dec 29, 2015 11:36:42 AM
 * 修改备注： 
 * @version
 */
public class GzSpCollectDetailReject extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			String appealObject=(String)this.getFormHM().get("appealObject");//报批给appealObject
			appealObject = PubFunc.decrypt(SafeCode.decode(appealObject));
			String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别号
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		    String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
		    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
		    String accountingcount = (String)this.getFormHM().get("count"); //次数
		    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
		    String rejectCause = (String) this.getFormHM().get("rejectCause");//驳回原因

			String selectGzRecords=(String) this.getFormHM().get("selectGzRecords");
			Boolean doSelectAll=(Boolean) this.getFormHM().get("doSelectAll");
			StringBuffer filtersql = new StringBuffer();//前台过滤条件
			filtersql.append(" and ");
			filtersql.append(this.getfilterSql(doSelectAll,selectGzRecords));
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			SalaryTemplateBo gzbo=bo.getSalaryTemplateBo();
			
	    	String collectPoint = (String) this.getFormHM().get("collectPoint");//汇总指标
	    	String selectID = (String) this.getFormHM().get("selectID");//选择的记录
	    	String cound = (String) this.getFormHM().get("cound");//人员筛选
	    	GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
	    	filtersql.append(spbo.getCollectSPPriv(gzbo, selectID,cound, collectPoint,salaryid,accountingdate,accountingcount));
		    
	    	ContentDAO dao = new ContentDAO(this.frameconn);
	    	RowSet rs = dao.search("select count(salaryid) as num from salaryhistory where 1=1 "+filtersql);
	    	if(rs.next()){
	    		int t = rs.getInt("num");
	    		if(t==0){
	    			throw GeneralExceptionHandler.Handle(new Exception("请选择可操作的数据！"));
	    		}
	    	}
		    accountingdate = accountingdate.replaceAll("\\.", "-");
		    String[] temps=accountingdate.split("-");
		    LazyDynaBean busiDate = new LazyDynaBean();
		    busiDate.set("year", temps[0]);
		    busiDate.set("month", temps[1]);
		    busiDate.set("day", "01");
		    busiDate.set("count", accountingcount);
		    bo.gzSp(appealObject, "reject", rejectCause, "", filtersql.toString(), busiDate);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 全选时返回的是没选择的记录
	 * 此处根据没选择的记录生成sql条件
	 * @param datalist
	 * @return
	 */
	private String getfilterSql(Boolean doSelectAll,String selectRecords){
		String str = "";
		try{
			String tableName = "salaryhistory";
			String[] selects=selectRecords.split("#");
			StringBuffer whl=new StringBuffer("");
			
			for(int i=0;i<selects.length;i++)
			{
				String[] a_selects=selects[i].split("/");
				whl.append(" or (A0100 = '"+PubFunc.decrypt(a_selects[0])+"' and upper(nbase) = '"+PubFunc.decrypt(a_selects[1]).toUpperCase()+"' and A00Z0 = "+Sql_switcher.dateValue(a_selects[2].replaceAll("\\.","-"))+" and A00z1 = '"+a_selects[3]+"' )");
			}
			if("true".equals(doSelectAll)){
				str =" not exists (select null from "+tableName+" b where "+tableName+".A0100=b.A0100 and "+tableName+".nbase=b.nbase and "+tableName+".A00Z0=b.A00Z0 and "+tableName+".A00z1=b.A00z1 and";
				str += "("+whl.substring(3)+")";
				str +=")";
			}else{
				str = "("+whl.substring(3)+")";
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
}
