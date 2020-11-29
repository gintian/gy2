package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:SaveDbTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Feb 25, 2009:7:22:41 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveDbTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String[] dbstr = (String[])this.getFormHM().get("dbstr");
		if(dbstr==null)
			dbstr = new String[0];
		String type = (String)this.getFormHM().get("field_falg");
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String sbstring = "";
		for(int i=0;i<dbstr.length;i++){
			sbstring += dbstr[i]+",";
		}
		if(sbstring.length()>0)
			sbstring = sbstring.substring(0,sbstring.length()-1);
		sysoth.setValue(Sys_Oth_Parameter.CHK_UNIQUENESS,type,"db",sbstring);
		sysoth.saveParameter();
	}

}
