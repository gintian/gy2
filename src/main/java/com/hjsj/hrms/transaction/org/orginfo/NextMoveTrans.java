/*
 * Created on 2006-1-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

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
public class NextMoveTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList moveorglist=(ArrayList)this.getFormHM().get("selectedlist");
	    if(moveorglist==null||moveorglist.size()==0)
            return;
		int a0000;
		String codeitemid="";
		String parentid="";
		int  pria0000=1;
		String pricodeitemid="";
		boolean isroot=false;
		try{
		     StringBuffer strsql=new StringBuffer();
		     ContentDAO dao=new ContentDAO(this.getFrameconn());
			 if(moveorglist!=null && moveorglist.size()>0)
			 {
				RecordVo vo=(RecordVo)moveorglist.get(0);
				parentid=vo.getString("parentid");
				codeitemid=vo.getString("codeitemid");
			 }
			 if(codeitemid.equalsIgnoreCase(parentid))
			 {
			 	isroot=true;
			 }
			 else
			 {
			 	isroot=false;
			 }
			for(int i=0;i<moveorglist.size();i++)
			{
				RecordVo vo=(RecordVo)moveorglist.get(i);
				a0000=vo.getInt("a0000");
				codeitemid=vo.getString("codeitemid");
				strsql.delete(0,strsql.length());
				if(isroot)
				{
					strsql.append("select * from organization where codeitemid=parentid and a0000=(select min(a0000) from organization where codeitemid=parentid and a0000>");
					strsql.append(a0000);
					strsql.append(") order by codeitemid");
				}
				else
				{
					strsql.append("select * from organization where codeitemid<>parentid and  parentid='");
					strsql.append(parentid);
					strsql.append("' and a0000=(select min(a0000) from organization where  codeitemid<>parentid and parentid='");
					strsql.append(parentid);
					strsql.append("' and a0000>");
					strsql.append(a0000);
					strsql.append(") order by codeitemid");
				}
				//System.out.println(strsql.toString());
				this.frowset=dao.search(strsql.toString());
				if(this.frowset.next())
				{
					pria0000=this.frowset.getInt("a0000");
					pricodeitemid=this.frowset.getString("codeitemid");
					strsql.delete(0,strsql.length());
					strsql.append("update organization set a0000=");
					strsql.append(pria0000);
					strsql.append(" where codeitemid='");
					strsql.append(codeitemid);
					strsql.append("'");
					dao.update(strsql.toString());					
					strsql.delete(0,strsql.length());
					strsql.append("update organization set a0000=");
					strsql.append(a0000);
					strsql.append(" where codeitemid='");
					strsql.append(pricodeitemid);
					strsql.append("'");		
					dao.update(strsql.toString());
				}
			}
			//this.getFormHM().put("selectedlist",moveorglist);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
