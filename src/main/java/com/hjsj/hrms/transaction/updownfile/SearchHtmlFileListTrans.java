/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.updownfile;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchHtmlFileListTrans extends IBusiness {

	  /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
    	/*
	    StringBuffer strsql=new StringBuffer();
	    strsql.append("select contentid,id,name,description,createdate,status from resource_list");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("resource_list");
	          vo.setString("contentid",this.getFrowset().getString("contentid"));
	         
	          vo.setString("id",this.getFrecset().getString("id"));
	          vo.setString("name",this.getFrowset().getString("name"));
	          vo.setString("description",this.getFrowset().getString("description"));
	          String temp=this.getFrowset().getString("createdate");
	          if(!(temp==null||temp.equals("")))
	          {
		          vo.setString("createdate","...");	              
	          }
	          temp=this.getFrowset().getString("status");
	          if(!(temp==null||temp.equals("")))
	          {
		          vo.setString("status","...");	              
	          }
	          
	          list.add(vo);
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("filelistlist",list);
	    }
        */

    }

}
