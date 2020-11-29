/*
 * Created on 2006-1-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchTarOrglistTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 StringBuffer strsql=new StringBuffer();
		 String orgid=(String)this.getFormHM().get("orgid");
		ArrayList tarorglist=new ArrayList();
		if(orgid!=null && orgid.length()>=2)
		{
		   strsql.append("select codesetid,codeitemid,codeitemdesc from organization where parentid<>codeitemid and parentid='");
		   strsql.append(orgid.substring(2));
		   strsql.append("'");
		}else
		{
			strsql.append("select codeitemid,codeitemdesc from organization where parentid=codeitemid");
		}
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				
				 CommonData dataobj = new CommonData(this.frowset.getString("codesetid")+this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				 tarorglist.add(dataobj);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("tarorglist",tarorglist);

	}

}
