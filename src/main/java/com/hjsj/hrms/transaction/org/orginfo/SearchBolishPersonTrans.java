/*
 * Created on 2006-1-11
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
public class SearchBolishPersonTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	  String orgid=(String)this.getFormHM().get("orgid");
	  ArrayList personlist=new ArrayList();
	  ArrayList personlists=new ArrayList();
	  String person = (String) this.getFormHM().get("dbpres");
	  String form = (String) this.getFormHM().get("form");
	  String pre=(String)this.getFormHM().get("dbpre");
	  String movedpersonsstr =(String)this.getFormHM().get("movedpersonsstr");
	  try{    
		    if(form==null||("1".equals(form) || form == "1")){   //zhangcq 2016/7/15  获取各个人员库的人数
		    	ContentDAO dao1=new ContentDAO(this.getFrameconn());
				StringBuffer sqlstrone=new StringBuffer();
				if(orgid!=null && orgid.length()>=2){
					sqlstrone.append("select a0100,a0101 from ");
					sqlstrone.append(pre);
					sqlstrone.append("A01 where ");
					if("UN".equalsIgnoreCase(orgid.substring(0,2)))
						sqlstrone.append("B0110 like '");
					else if("UM".equalsIgnoreCase(orgid.substring(0,2)))
						sqlstrone.append("E0122 like '");
					else
						sqlstrone.append("E01A1 like '");
					sqlstrone.append(orgid.substring(2));
					sqlstrone.append("%'");
					this.frowset=dao1.search(sqlstrone.toString());
					while(this.frowset.next())
					{
						if(movedpersonsstr.indexOf(pre+this.frowset.getString("a0100"))==-1){
							 CommonData dataobj = new CommonData(this.frowset.getString("a0100"),this.frowset.getString("a0101"));
							 personlist.add(dataobj);
						}
					}
				}
		    }else{
			    String[] pers = person.split(",");
				ContentDAO dao=new ContentDAO(this.getFrameconn());		
				for (int i = 0; i < pers.length; i++) {
				    if (orgid != null && orgid.length() >= 2) {
				        StringBuffer sqlstr = new StringBuffer();
				        sqlstr.append("select a0100,a0101 from ");
				        sqlstr.append(pers[i]);
				        sqlstr.append("A01 where ");
				        if ("UN".equalsIgnoreCase(orgid.substring(0, 2)))
				            sqlstr.append("B0110 like '");
				        else if ("UM".equalsIgnoreCase(orgid.substring(0, 2)))
				            sqlstr.append("E0122 like '");
				        else
				            sqlstr.append("E01A1 like '");
				        sqlstr.append(orgid.substring(2));
				        sqlstr.append("%'");
				        this.frowset = dao.search(sqlstr.toString());
				        while (this.frowset.next()) {
				            if (movedpersonsstr.indexOf(pre + this.frowset.getString("a0100")) == -1) {
				                CommonData dataobjs = new CommonData(this.frowset.getString("a0100"), this.frowset.getString("a0101"));
				                personlists.add(dataobjs);
				            }
				        }
				    }
				}
		    }
			
		  this.getFormHM().put("personsize",personlists.size());
		  this.getFormHM().put("personlist",personlist);
	  }catch(Exception e)
	  {
	  	 e.printStackTrace();
	  	 throw GeneralExceptionHandler.Handle(e);
	  }
	}

}
