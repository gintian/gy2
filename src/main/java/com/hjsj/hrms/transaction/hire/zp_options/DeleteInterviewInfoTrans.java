/*
 * Created on 2005-9-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteInterviewInfoTrans</p>
 * <p>Description:删除面试资料</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class DeleteInterviewInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList testQuestionlist=(ArrayList)this.getFormHM().get("selectedlist");
		if(testQuestionlist==null||testQuestionlist.size()==0)
            return;
		ExecuteSQL executeSQL=new ExecuteSQL();
        String sql = "";
        try
        {
        	for(int i=0;i<testQuestionlist.size();i++)
        	{
        		RecordVo rv=(RecordVo)testQuestionlist.get(i);
        		String test_id = rv.getString("test_id");
        		sql="delete from zp_pos_test where test_id = " + test_id;
        		executeSQL.execUpdate(sql);
        	}
        }    
        catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
