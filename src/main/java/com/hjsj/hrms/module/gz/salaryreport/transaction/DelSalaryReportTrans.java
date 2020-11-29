package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:DelSalaryReportTrans.java</p>
 * <p>Description>:删除工资报表</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 18, 2016 4:10:17 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class DelSalaryReportTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			String rsid=(String)this.getFormHM().get("rsid");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.delete("delete from reportdetail where rsid="+rsid+"  and rsdtlid="+rsdtlid,new ArrayList());
			dao.delete("delete from reportitem where  rsdtlid="+rsdtlid,new ArrayList());
			
			//删除查询方案
			dao.delete("delete from t_sys_table_query_plan where subModuleId='salaryreport_"+rsdtlid+"'",new ArrayList());
			
			//删除栏目设置
			dao.delete("DELETE from t_sys_table_scheme_item where t_sys_table_scheme_item.SCHEME_ID="
					+ "(select max(SCHEME_ID) from t_sys_table_scheme where subModuleId='salaryreport_"+rsdtlid+"')",new ArrayList());
			dao.delete("delete from t_sys_table_scheme where subModuleId='salaryreport_"+rsdtlid+"'",new ArrayList());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
