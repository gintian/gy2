/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-2:下午05:47:45</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchChangeA01Z0Trans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		try
		{
			//SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			//String tablename=templatebo.createA01Z0ChangeManTable();
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn());
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			String tablename="t#"+this.userView.getUserName()+"_gz_Tf";
			StringBuffer buf=new StringBuffer();
			buf.append("select * ");
			this.getFormHM().put("strsql", buf.toString());
			buf.setLength(0);
			buf.append("dbname,b0110,e0122,a0101,state,a0100,a01z0,");
			if(uniquenessvalid!=null&&!"".equals(uniquenessvalid)&&!"0".equals(uniquenessvalid)&&templatebo.isAddColumn(onlyname, "dbname,b0110,e0122,a0101,state,a0100"))
			{
				buf.append(onlyname+",");
				this.getFormHM().put("onlyitem", DataDictionary.getFieldItem(onlyname));
			}
			else
			{
				this.getFormHM().put("onlyitem",null);
			}
			this.getFormHM().put("columns", buf.toString());
			buf.setLength(0);
			buf.append(" from ");
			buf.append(tablename);
			this.getFormHM().put("strwhere", buf.toString());	
			this.getFormHM().put("displayE0122", display_e0122);
			this.getFormHM().put("checkall", "0");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
