/*
 * Created on 2006-2-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SynchronizationDataExamTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
			String dbpre = "";
	        if(rv!=null)
	        {
	            dbpre=rv.getString("str_value");
	            if(dbpre==null || dbpre!=null &&dbpre.length()==0)
	            	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
	        }else
	        {
	        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
	        }
	        StringBuffer strsql=new StringBuffer();
	        //strsql.append("INSERT INTO zp_exam_report(a0100) select a0100 from ");
		    //strsql.append(dbpre);
		    //strsql.append("A01 where (a0100 not in (select a0100 from zp_exam_report))"); 
		    if(this.userView!=null && this.userView.getUserId()!=null && !"su".equals(this.userView.getUserId()))
		    {
		        ContentDAO dao=new ContentDAO(this.getFrameconn());
		    	this.frowset=dao.search("select * from zp_exam_report where a0100='" + this.userView.getUserId() + "'");
		    	if(!this.frowset.next()){
			        strsql.append("INSERT INTO zp_exam_report(a0100) values('");
				    strsql.append(this.userView.getUserId());
				    strsql.append("')");		    
				    cat.debug("SynchronizationDataExamTrans" + strsql);	
				    dao.insert(strsql.toString(),new ArrayList());	
			    }	  
			}
		}catch(SQLException sqle)
	    {
  	       sqle.printStackTrace();
  	       throw GeneralExceptionHandler.Handle(sqle);
  	    }
	}

}
