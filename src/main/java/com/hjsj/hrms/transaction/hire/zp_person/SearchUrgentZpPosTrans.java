/*
 * Created on 2005-11-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_person;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchUrgentZpPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
		HashMap ahm=(HashMap)this.getFormHM().get("requestPamaHM");
		String edition=(String)ahm.get("edition");
		if(edition==null)
			strsql.append("select codeitemid,codeitemdesc from organization  where codeitemid in (select pos_id from zp_position where status = '1')");
		else if("2".equals(edition))   //2版目前没有紧急招聘这一说，所以先舍弃
		{
			//strsql.append("select z03.z0301  codeitemid,org.codeitemdesc from z03,organization  org ,z01  where z03.z0311=org.codeitemid and z03.z0101=z01.z0101 and z01.z0129='04'");
			strsql.append("select * from organization where 1=2 ");
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      { 
	      	 HashMap hm = new HashMap();
	      	 hm.put("codeitemid",this.getFrowset().getString("codeitemid"));
	      	 hm.put("codeitemdesc",this.getFrowset().getString("codeitemdesc"));
	         list.add(hm);
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("urgentzpposlist",list);
	    }

	}

}
