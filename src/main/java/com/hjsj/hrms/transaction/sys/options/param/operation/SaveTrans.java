package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Mar 30, 2009:5:48:08 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		String id = (String)this.getFormHM().get("operationid");
		ArrayList sortlist = so.getView_tag(id);
		String[] typestr = (String[])this.getFormHM().get("typestr");
		if(typestr==null)
			typestr = new String[0];
		for(int i=0;i<sortlist.size();i++){
			String sortname = sortlist.get(i).toString();
			String valid = "";
			for(int x=0;x<typestr.length;x++){
				String s = typestr[x];
				if(s.indexOf(sortname)!=-1)
					valid += s.substring(s.indexOf("-")+1)+",";
			}
			if(valid.length()>1)
			   valid = valid.substring(0,valid.length()-1);
			else
				valid="#";
			so.saveView_Value(id,"valid",sortname,valid,this.frameconn);
		}
	}

}
