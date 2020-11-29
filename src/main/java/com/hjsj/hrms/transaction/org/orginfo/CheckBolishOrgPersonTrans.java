/*
 * Created on 2006-1-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
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
public class CheckBolishOrgPersonTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dblist=DataDictionary.getDbpreList();
		if(dblist==null && dblist.size()==0)
			return;
		StringBuffer strsql=new StringBuffer();	
		StringBuffer wheresql=new StringBuffer();
		boolean ishaveperson=false;
		ArrayList bolishlist=(ArrayList)this.getFormHM().get("bolishlist");
		if(bolishlist==null && bolishlist.size()==0)
			return;
		  ArrayList movedpersons = (ArrayList)this.getFormHM().get("movepersons");
		  StringBuffer movedpersonsstr =new StringBuffer();
		  try{
				for(int i=0;i<movedpersons.size();i++){
					CommonData dataobj = (CommonData)movedpersons.get(i);
					movedpersonsstr.append(dataobj.getDataValue()+",");
				}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			for(int j=0;j<bolishlist.size();j++)
			{
				CommonData dataobj=(CommonData)bolishlist.get(j);
				if(dataobj.getDataValue()!=null && dataobj.getDataValue().length()>=2)
				{
					if("UN".equalsIgnoreCase(dataobj.getDataValue().substring(0,2)))
					{
						wheresql.append(" OR B0110 like '");
						wheresql.append(dataobj.getDataValue().substring(2));
						wheresql.append("%'");
					}else if("UM".equalsIgnoreCase(dataobj.getDataValue().substring(0,2)))
					{
						wheresql.append(" OR E0122 like '");
						wheresql.append(dataobj.getDataValue().substring(2));
						wheresql.append("%'");
					}else if("@K".equalsIgnoreCase(dataobj.getDataValue().substring(0,2)))
					{
						wheresql.append(" OR E01A1 like '");
						wheresql.append(dataobj.getDataValue().substring(2));
						wheresql.append("%'");
					}
				}
			}
			for(int i=0;i<dblist.size();i++)
			{
				strsql.delete(0,strsql.length());
				strsql.append("select a0100 from ");
				strsql.append(dblist.get(i).toString());
				strsql.append("A01 where 1=2");
				strsql.append(wheresql.toString());
				this.frowset=dao.search(strsql.toString());
				while(this.frowset.next())
				{
					if(movedpersonsstr.indexOf(dblist.get(i).toString()+this.frowset.getString("a0100"))==-1){
						ishaveperson=true;
						break;
					}
				}				
			}
			if(ishaveperson)
			   this.getFormHM().put("ishavepersonmessage",ResourceFactory.getProperty("label.org.haveperson"));
		    else
		       this.getFormHM().put("ishavepersonmessage",ResourceFactory.getProperty("label.org.noperson"));
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
