/*
 * Created on 2005-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteExamSubjectTrans</p>
 * <p>Description:删除考试科目</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class DeleteExamSubjectTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ArrayList zpExamlist=(ArrayList)this.getFormHM().get("selectedlist");
		if(zpExamlist==null||zpExamlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        try
        {
        	for(int i=0;i<zpExamlist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpExamlist.get(i);
        		String subject_id = rv.getString("subject_id");
        		sql="delete from zp_exam_subject where subject_id = '" + subject_id + "'";
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        	}
        }
	   catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
