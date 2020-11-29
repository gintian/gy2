package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.machine.SaveTurnOverTimeToOverTimeApp;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 延时加班转加班申请
 *<p>
 * Title:AddOverTimeAppTrans.java
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Oct 30, 2007
 * </p>
 * 
 * @author sunxin
 *@version 4.0
 * 
 *@author zhaoxj
 *@version 5.0
 */
public class AddOverTimeAppTrans extends IBusiness {
    public void execute() throws GeneralException 
	{
        try{ 
        	ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("emp_list");    	
        	if(selectedinfolist==null||selectedinfolist.size()==0)
                return;
        	
        	String temp_Table=(String)this.getFormHM().get("tranOverTimeTab");
        	if(temp_Table==null||temp_Table.length()<=0)
        		throw new GeneralException(ResourceFactory.getProperty("kq.analyse.no.tran.overtime.tab"));
        	
        	String templateId = "";
        	String overtimeSpState = "03";
    	    templateId = (String)this.getFormHM().get("templateid");
    	    templateId = null == templateId ? "" : templateId;
    	    
    	    overtimeSpState = (String)this.getFormHM().get("spstate");
    	    overtimeSpState = null == overtimeSpState ? "03" : overtimeSpState;
        	
        	String overtimeReason = "延时加班";
            if (!"03".equals(overtimeSpState))
                overtimeReason = "";
            
        	SaveTurnOverTimeToOverTimeApp sa = new SaveTurnOverTimeToOverTimeApp(this.frameconn,this.userView, temp_Table);
            //if(sa.saveToOverTimeApp(selectedinfolist, overtimeReason, templateId, overtimeSpState) != selectedinfolist.size()){
            	sa.saveToOverTimeApp(selectedinfolist, overtimeReason, templateId, overtimeSpState);
            	if (sa.getErr_message().length() > 0)
                    throw new GeneralException(sa.getErr_message());
                else if (sa.getErrorMess().length() > 0) 
					throw new GeneralException(sa.getErrorMess());
            //}
        } catch (GeneralException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
}
