/*
 * Created on 2005-9-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:InputExamReportTrans</p>
 * <p>Description:修改表的字段</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class InputExamReportTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
	    strsql.append("select subject_id,subject_name from zp_exam_subject");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    ArrayList tempList = new ArrayList();
	    ArrayList nameList = new ArrayList();
	    try
	    {
	      ResultSet rs = dao.search(strsql.toString(),list);
	      while(rs.next())
	      {
	      	RecordVo vo=new RecordVo("zp_exam_subject");
	        vo.setString("subject_id",rs.getString("subject_id"));
	        vo.setString("subject_name",rs.getString("subject_name"));
	        nameList.add(rs.getString("subject_name"));
	        tempList.add(vo);
	      }
	      int rsCount = tempList.size()+4;
	      String sqle = "select * from zp_exam_report";
	      ResultSet rst = dao.search(sqle,list);
	      int count = rst.getMetaData().getColumnCount();
	      if(rsCount >= count){
	      	 String sql = "select subject_id from zp_exam_subject";
	      	 this.frowset = dao.search(sql);
	      	 while(this.frowset.next()){
	      	    String flag = "0";
	      	    for(int i=1;i<=count;i++){  
	      	       if(("K_"+this.getFrowset().getString("subject_id")).equals(rst.getMetaData().getColumnName(i))){
	      	   	      flag = "1"; 
	      	       }
	      	    }
	      	    if("0".equals(flag)){
	      	    	String ssql = "";
	      	    	switch(Sql_switcher.searchDbServer())
					{
					   case Constant.DB2:
	      	    	      ssql = "alter table zp_exam_report add column K_"+this.frowset.getString("subject_id")+" float";
					      break;
					   default:  
					   	  ssql = "alter table zp_exam_report add K_"+this.frowset.getString("subject_id")+" float";
					      break;
	      	    	}
		      	    dao.update(ssql,list);
	      	    }
	         }
	      }else if(rsCount < count){
	         for(int i=1;i<=count;i++){  	
	        	if(rst.getMetaData().getColumnName(i).indexOf("K_") != -1){
	        	    String flag = "0";
	        	    String sql = "select subject_id from zp_exam_subject";
		            this.frowset = dao.search(sql);
		            while(this.frowset.next()){
		      	   	    if(rst.getMetaData().getColumnName(i).equals("K_"+this.getFrowset().getString("subject_id"))){
		      	   	   	   flag="1";
		      	   	    }
		      	    }
		            if("0".equals(flag)){
		            	switch(Sql_switcher.searchDbServer())
						{
						   case Constant.DB2:
						   	   break;
						   default: 
						   	  String ssql = "alter table zp_exam_report drop column "+rst.getMetaData().getColumnName(i);
						      dao.update(ssql,list);   
						      break;
						}
		            }
	        	 }
	          }
              switch(Sql_switcher.searchDbServer())
			  {
				  case Constant.DB2:
				   	 ArrayList idList = new ArrayList();
				   	 String sssql = "select subject_id from zp_exam_subject";
				      this.frowset = dao.search(sssql);
				      while(this.frowset.next()){
				      	idList.add(this.getFrowset().getString("subject_id"));
				      }
				   	  StringBuffer strValue = new StringBuffer();
				   	  if(idList == null || "".equals(idList)){
				   	      strValue.append("read_score,written_score,sum_score");
				   	  }else{
				   	    strValue.append("a0100,read_score,written_score,");
				   	  	for(int j=0;j<idList.size();j++){
				   	  	  strValue.append("K_"+(String)idList.get(j));
				   	  	  strValue.append(",");
				   	  	}
				   	    strValue.append("sum_score");
				   	  }
				   	  String sqlsql = "create table linshi AS (select "+strValue.toString()+" from zp_exam_report) definition only";
				   	  dao.update(sqlsql,list);
				      sqlsql = "ALTER TABLE linshi ADD PRIMARY KEY (a0100)";
				      dao.update(sqlsql,list);
				      sqlsql = "insert into linshi ("+strValue.toString()+") select "+strValue.toString()+" from zp_exam_report";
				      dao.update(sqlsql,list);
				      sqlsql = "drop table zp_exam_report";
				      dao.update(sqlsql,list);
				      sqlsql = "rename table linshi to zp_exam_report";
				      dao.update(sqlsql,list);
				      break;
				   default:   
				      break;
				}  
            }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }finally{
	    	this.getFormHM().put("zpExamSubjectlist",tempList);
	    }

	}

}
