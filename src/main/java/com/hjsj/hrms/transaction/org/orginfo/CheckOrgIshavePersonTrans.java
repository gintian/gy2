package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

public class CheckOrgIshavePersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList orgcodeitemid=(ArrayList)this.getFormHM().get("orgcodeitemid");
		StringBuffer orgitem=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    String chekperson="false";
	    StringBuffer sql=new StringBuffer();
        try
        {
        	List dblist=DataDictionary.getDbpreList();
        	for(int i=0;i<orgcodeitemid.size();i++){
        		boolean b=false;
        	   for(int k=0;k<dblist.size();k++)
        	   {
        		   DbWizard dbw = new DbWizard(this.frameconn);
        		   if(!dbw.isExistTable(dblist.get(k)+"a01",false))
        			   continue;
        		   sql.delete(0,sql.length());
        		   sql.append("select a0100 from ");
        		   sql.append(dblist.get(k));
        		   sql.append("a01 where b0110 like '");
        		   sql.append(orgcodeitemid.get(i));
        		   sql.append("%' or e0122 like '");
        		   sql.append(orgcodeitemid.get(i));
        		   sql.append("%' or e01a1 like '");
        		   sql.append(orgcodeitemid.get(i));
        		   sql.append("%'");
        		   this.frowset=dao.search(sql.toString());
        		   if(this.frowset.next())
        		   {
        			   chekperson="true";
        		       b=true;
        			   break;
        		   }
        	   }
        	   if(b)
        		   break;
        	}
        	for(int i=0;i<orgcodeitemid.size();i++)
        	{
        		orgitem.append(orgcodeitemid.get(i));
        		orgitem.append("`");
        	}
        	if(orgitem!=null&&orgitem.length()>0)
        	{
        		orgitem.setLength(orgitem.length()-1);
            }
        }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
		this.getFormHM().put("checkperson",chekperson);
        this.getFormHM().put("orgitem",orgitem.toString());
	}

}
