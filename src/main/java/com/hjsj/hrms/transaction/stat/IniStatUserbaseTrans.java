package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class IniStatUserbaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbnameini="usr";
		ArrayList Dblist=userView.getPrivDbList();
	
		if(Dblist!=null && Dblist.size()>0){
			dbnameini=Dblist.get(0).toString();
		}else
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.stat.noprivdbname"),"",""));
		}
		this.getFormHM().put("userbase",dbnameini);
		//String tmpdbpre=(String)this.getFormHM().get("userbases");
		//if(tmpdbpre==null||tmpdbpre.length()==0)
			this.getFormHM().put("userbases", dbnameini);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search("select dbname from DBName where upper(pre)='"+dbnameini.toUpperCase()+"'");
			if(this.frowset.next()){
				this.getFormHM().put("viewuserbases", this.frowset.getString("dbname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);		

	}

}
