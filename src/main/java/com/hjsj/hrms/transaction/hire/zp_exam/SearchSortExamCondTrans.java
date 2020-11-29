/*
 * Created on 2005-9-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchSortExamCondTrans</p>
 * <p>Description:查询排序条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchSortExamCondTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
	    strsql.append("select * from zp_exam_report");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
	    ArrayList tempList = new ArrayList();
	    try
	    {
	      ResultSet rs = dao.search(strsql.toString(),list);
	      int count = rs.getMetaData().getColumnCount();
	      for(int i=1;i<=count;i++){
	      	if(!"A0100".equals((rs.getMetaData().getColumnName(i)).toUpperCase())){
	      	   HashMap wf=new HashMap();
	      	   wf.put("columnname",rs.getMetaData().getColumnName(i));
	      	   if(rs.getMetaData().getColumnName(i).indexOf("K_") != -1){
	      	      String[] strArray = rs.getMetaData().getColumnName(i).split("_");
	      	      String sql = "select subject_name from zp_exam_subject where subject_id = '"+strArray[1]+"'";
	      	      this.frowset = dao.search(sql);
	      	      while(this.frowset.next()){
	      	   	     wf.put("columndesc",this.getFrowset().getString("subject_name"));
	      	      }
	           }else if("READ_SCORE".equals((rs.getMetaData().getColumnName(i)).toUpperCase())){
	      		   wf.put("columndesc","面试");
	      	   }else if("WRITTEN_SCORE".equals((rs.getMetaData().getColumnName(i)).toUpperCase())){
	      		   wf.put("columndesc","笔试");
	      	   }else if("SUM_SCORE".equals((rs.getMetaData().getColumnName(i)).toUpperCase())){
	      		   wf.put("columndesc","总分");
	         	}
	      	   tempList.add(wf);
	      	}
	      }
	    }catch(SQLException sqle){
	    	sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
	    }finally{
	    	this.getFormHM().put("sortCondList",tempList);
	    }

	}

}
