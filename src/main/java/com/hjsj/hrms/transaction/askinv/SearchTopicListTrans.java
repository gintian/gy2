/*
 * Created on 2005-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchTopicListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	  public void execute() throws GeneralException {
	    StringBuffer strsql=new StringBuffer();
	    
	    strsql.append("select * from investigate order by id desc");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	      	 
	          RecordVo vo=new RecordVo("investigate");
	          vo.setString("id",this.getFrowset().getString("id"));
	         
	          String temp=this.getFrowset().getString("content");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("content","");
	          }
	          else
	          {
	            vo.setString("content",this.getFrowset().getString("content"));
	          }
	          vo.setDate("releasedate",PubFunc.FormatDate(this.getFrowset().getDate("releasedate")));
	          vo.setDate("enddate",PubFunc.FormatDate(this.getFrowset().getDate("enddate")));
	          
	          int tempInt=this.getFrowset().getInt("days");
	          if(new Integer(tempInt)==null )
	          {
	          	vo.setInt("days",0);
	          }
	          else
	          {	
	            vo.setInt("days",this.getFrowset().getInt("days"));
	           }
	          
	          tempInt=this.getFrowset().getInt("flag");
	          if(new Integer(tempInt)==null || tempInt==0)
	          {
	          	vo.setString("flag","0");
	          }
	          else
	          {
	          	if(tempInt==1)
	          	{
	            vo.setString("flag","1");
	          	}
	          	else
	          	{
	          		vo.setString("flag","0");
	          	}
	          }
	          
	          tempInt=this.getFrowset().getInt("status");
	          if(new Integer(tempInt)==null )
	          {
	          	vo.setString("status","0");
	          }
	          else
	          {	
	          	if(tempInt==1)
	          	{
	            vo.setString("status","1");
	          	}
	          	else
	          	{
	          		vo.setString("status","0");
	          	}
	           }
	        
	       
	          list.add(vo);
	      }
	      
	      this.getFormHM().put("topiclist",list);
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	       
	    }
        

     }

}
