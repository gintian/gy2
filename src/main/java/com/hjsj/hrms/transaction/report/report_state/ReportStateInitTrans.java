/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 28, 2006:2:06:13 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportStateInitTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		ArrayList reportStateList=(ArrayList)this.getFormHM().get("selectedList");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String isSub=(String)hm.get("isSub");
		if(isSub==null)
			isSub="false";
		if(reportStateList==null||reportStateList.size()==0){
			//return;
		    Exception e = new Exception(ResourceFactory.getProperty("report_collect.info7")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
				
		for(Iterator t=reportStateList.iterator();t.hasNext();)
    	{
    		LazyDynaBean a=(LazyDynaBean)t.next();
    		String temp = (String) a.get("id");
    		String [] tt = temp.split("/");
    		String unitCode = tt[0];
    		String tabid = tt[1];
    		String usql ="";
    		if("false".equals(isSub))
    			usql = "update  treport_ctrl  set status = -1 , description =null ,username =null,currappuser =null,appuser =null where unitcode = '"+unitCode+"' and tabid = " + tabid;
    		else
    			usql = "update  treport_ctrl  set status = -1 , description =null ,username =null,currappuser =null,appuser =null where unitcode like '"+unitCode+"%' and tabid = " + tabid;
    		this.updateSQL(usql);
    		
    	}
		
	}
	
	public void updateSQL(String sql) throws GeneralException{
		//System.out.println(sql);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql);
		} catch (Exception  e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}
	
}
