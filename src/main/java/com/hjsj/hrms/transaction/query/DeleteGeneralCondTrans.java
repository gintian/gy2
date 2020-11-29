/**
 * 
 */
package com.hjsj.hrms.transaction.query;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteGeneralCondTrans</p>
 * <p>Description:删除常用条件交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-5-15:11:07:48</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteGeneralCondTrans extends IBusiness {

	public void execute() throws GeneralException {
        String curr_id[]=(String[])this.getFormHM().get("curr_id");
        if(curr_id==null||curr_id.length==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        StringBuffer strsql=new StringBuffer();
        ArrayList paramlist=new ArrayList();
        for(int i=0;i<curr_id.length;i++)
        {
            ArrayList list=new ArrayList();        	
        	list.add(curr_id[i]);
        	paramlist.add(list);
        }
        strsql.append("delete from lexpr where id=?");
        try
        {
        	dao.batchUpdate(strsql.toString(),paramlist);
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }
	}

}
