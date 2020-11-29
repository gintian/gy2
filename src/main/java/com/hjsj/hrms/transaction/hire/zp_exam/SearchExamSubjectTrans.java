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

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:SearchExamSubjectTrans</p>
 * <p>Description:查询考试科目</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchExamSubjectTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
	        String subject_id=(String)hm.get("a_id");
	        String flag=(String)this.getFormHM().get("flag");
	        /**
	         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
	         * 用户的使用习惯。
	         */
	        if("1".equals(flag))
	            return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        RecordVo vo=new RecordVo("zp_exam_subject");
	        try
	        {
	            vo.setString("subject_id",subject_id);
	            vo=dao.findByPrimaryKey(vo);
	        }
	        catch(SQLException sqle)
	        {
	  	      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);            
	        }
	        finally
	        {
	            this.getFormHM().put("zpExamvo",vo);
	        }

	}

}
