package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DeleteSortTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:4:03:56 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DeleteSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = (ArrayList) this.getFormHM().get("selectedlist");
		String operationid = (String)this.getFormHM().get("operationid");
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		for(int i=0;i<list.size();i++){
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String modulename = (String)bean.get("sortname");
			so.deleteTag(operationid,modulename);
		}
		this.getFormHM().put("errmes","");
	}

}
