
package com.hjsj.hrms.transaction.paramter;

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
public class SearchFriendListTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=null;
	    try
	    {		
		  this.getFormHM().put("userAdmin",Boolean.toString(userView.isSuper_admin()));
		  StringBuffer strsql=new StringBuffer();
		  String sql = "select site_id,name,url from hr_friend_website";
		  strsql.append(sql);
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  list=new ArrayList();
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          	RecordVo vo=new RecordVo("hr_friend_website");
	        	vo.setString("site_id",this.frowset.getString("site_id"));
	            vo.setString("name",this.frowset.getString("name"));
	            vo.setString("url",this.frowset.getString("url"));
		        list.add(vo);
	      }
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("friendlist",list);
	    }
	
    }
}
	
