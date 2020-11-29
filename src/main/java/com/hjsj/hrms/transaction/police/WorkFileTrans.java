package com.hjsj.hrms.transaction.police;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.InputStream;
import java.sql.SQLException;

public class WorkFileTrans extends IBusiness {

	public void execute() throws GeneralException {
         String type=(String)this.getFormHM().get("type");
         String flag=(String)this.getFormHM().get("flag");
         if(flag==null)
        	 flag="";
         boolean isCorrect=false;
         if(type!=null&& "police".equalsIgnoreCase(type))
         {
        	 StringBuffer sql= new StringBuffer();
        	 sql.append("select content from resource_list where ");
        	 if("1".equalsIgnoreCase(flag))
        	 {
        		 sql.append(" name='分监区工作流程'");
        	 }else if("2".equalsIgnoreCase(flag))
        	 {
        		 sql.append(" name='独立工作环节'");
        	 }else if("3".equalsIgnoreCase(flag))
             {
        		 sql.append(" name='专项教育活动'");
             }
        	 sql.append("order by contentid desc");
        	 ContentDAO dao=new ContentDAO(this.getFrameconn());
        	 
        	 try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					InputStream in=this.frowset.getBinaryStream("content");
					if(in!=null)
						isCorrect=true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
         }
         if(isCorrect)
        	 this.getFormHM().put("file", "yes");
         else
        	 this.getFormHM().put("file", "no");
	}

}
