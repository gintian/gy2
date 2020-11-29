package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:修改薪资发放主键(a00z0,a00z1)</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 23, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class AlterSalaryPrimaryKeyTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String field_name=(String)this.getFormHM().get("field_name");
			String newvalue=(String)this.getFormHM().get("newvalue");
			String oldvalue=(String)this.getFormHM().get("oldvalue");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String other_value="";

			if("a00z0".equalsIgnoreCase(field_name))
				other_value=(String)this.getFormHM().get("A00Z1");
			else
				other_value=(String)this.getFormHM().get("A00Z0");
			String NBASE=(String)this.getFormHM().get("NBASE");
			String A0100=(String)this.getFormHM().get("A0100");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String gz_tablename="";
			if(manager.length()==0)
				gz_tablename=this.userView.getUserName()+"_salary_"+salaryid;
			else
				gz_tablename=manager+"_salary_"+salaryid;
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("a00z0".equalsIgnoreCase(field_name))
			{
				Calendar d=Calendar.getInstance();
				d.setTimeInMillis(Long.parseLong(oldvalue));
				String sql="update "+gz_tablename+" set a00z0=? where a00z1="+other_value+" and a0100='"+A0100+"'"
						  +" and lower(nbase)='"+NBASE.toLowerCase()+"' and "+Sql_switcher.year("a00z0")+"="+d.get(Calendar.YEAR)
						  +"  and "+Sql_switcher.month("a00z0")+"="+(d.get(Calendar.MONTH)+1);
				ArrayList paramList = new ArrayList();
				paramList.add(new java.sql.Date(Long.parseLong(newvalue)));
				dao.update(sql,paramList);
			}
			else
			{
				Calendar d=Calendar.getInstance();
				d.setTimeInMillis(Long.parseLong(other_value));
				String sql="update "+gz_tablename+" set a00z1="+newvalue+" where a00z1="+oldvalue+" and a0100='"+A0100+"'"
						  +" and lower(nbase)='"+NBASE.toLowerCase()+"' and "+Sql_switcher.year("a00z0")+"="+d.get(Calendar.YEAR)
						  +"  and "+Sql_switcher.month("a00z0")+"="+(d.get(Calendar.MONTH)+1);
				
				dao.update(sql);
			}
			
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("更新失败!"));
		}
	}

}
