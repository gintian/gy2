/*
 * Created on 2005-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveExamSubjectTrans</p>
 * <p>Description:保存考试科目</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveExamSubjectTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo=(RecordVo)this.getFormHM().get("zpExamvo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增考试科目，进行保存处理
             */
        	try{
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String subject_id=idg.getId("zp_exam_subject.subject_id");
               vo.setString("subject_id",subject_id);    
               String sql = "insert into zp_exam_subject (subject_id,subject_name) values('"+subject_id+"','"+vo.getString("subject_name")+"')";
               ArrayList list = new ArrayList();
               dao.update(sql,list);
            }catch(Exception e){
    	        e.printStackTrace();
    	        throw GeneralExceptionHandler.Handle(e);
            }             
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
	        try
	        {
	         	
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_exam_subject set subject_name ='"+vo.getString("subject_name")+"' where subject_id ='"+vo.getString("subject_id")+"'";
	        	dao.update(sql,list);
	            
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
