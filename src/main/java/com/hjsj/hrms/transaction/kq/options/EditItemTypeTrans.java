package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class EditItemTypeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String mkey=(String)this.getFormHM().get("codeitemid");
		StringBuffer stsql=new StringBuffer();
	     ContentDAO dao=new ContentDAO(this.getFrameconn());
	     RecordVo vo=new RecordVo("codeitem");
	     String nn="";

	         try
	         {
	        	 if(!(mkey==null || "".equals(mkey)))
	   		     {
	        	     stsql.append("select codesetid,codeitemdesc,codeitemid from codeitem where codeitemid='");
	            	 stsql.append(mkey.toString());
	            	 stsql.append("' and codesetid ='27'");
	      
	    		      this.frowset = dao.search(stsql.toString());
	    		      while(this.frowset.next())
	    		      {
	    		        nn=this.frowset.getString("codesetid");
	        		     vo.setString("codesetid",nn);
	        		     vo.setString("codeitemid",this.frowset.getString("codeitemid"));
	        		     vo.setString("codeitemdesc",this.frowset.getString("codeitemdesc"));
		                 vo=dao.findByPrimaryKey(vo);
	    		       
	    		      }
	   		        }
	            }
	            catch(SQLException sqle)
	            {
	  	          sqle.printStackTrace();
		          throw GeneralExceptionHandler.Handle(sqle);            
	            }
		if(nn==null|| "".equals(nn))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.kq.notitem"),"",""));

		// this.getFormHM().put("code",vo.getString("codeitemid"));
		this.getFormHM().put("code",mkey);
		this.getFormHM().put("name",vo.getString("codeitemdesc"));
		this.getFormHM().put("mes","4");
	   }

}
