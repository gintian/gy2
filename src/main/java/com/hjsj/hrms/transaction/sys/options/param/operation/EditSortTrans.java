package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:EditSortTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:5:47:19 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class EditSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//String text = (String)this.getFormHM().get("text");
		String ids = (String)this.getFormHM().get("ids");
		String index = (String)this.getFormHM().get("index");
		//this.getFormHM().put("text",text);
		this.getFormHM().put("ids",ids);
		this.getFormHM().put("index",index);
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		String operationid = (String)this.getFormHM().get("operationid");
		String sortname = (String)this.getFormHM().get("sortname");
		//String[] texts = text.split(",");
		String[] tabids = ids.split(",");
		String[] value = new String[tabids.length];
		for(int i=0;i<tabids.length;i++){
			value[i] = tabids[i];
		}
		so.saveView_Value(operationid,sortname,value,this.frameconn);
		this.getFormHM().put("operationid",operationid);
	}

}
