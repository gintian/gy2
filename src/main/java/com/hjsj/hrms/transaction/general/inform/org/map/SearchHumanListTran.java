/*
 * Created on 2006-4-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchHumanListTran extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String code=(String)hm.get("code");
		String kind=(String)hm.get("kind");
		StringBuffer sqlstr=new StringBuffer();
		String dbname=(String)hm.get("dbname");
		ArrayList personlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			if(code!=null && code.length()>0)
			{
				sqlstr.delete(0, sqlstr.length());
				sqlstr.append("select a0100,a0101  from ");
				sqlstr.append(dbname);
				sqlstr.append("A01 where ");
				if ("UN".equalsIgnoreCase(kind)) {
					sqlstr.append("b0110 like '");
					sqlstr.append(code);
					sqlstr.append("%'");
				} else if ("UM".equalsIgnoreCase(kind)) {
					sqlstr.append("e0122 like '");
					sqlstr.append(code);
					sqlstr.append("%'");
				} else {
					sqlstr.append("e01a1 like '");
					sqlstr.append(code);
					sqlstr.append("%'");
				}
				sqlstr.append(" order by a0000");
				//System.out.println(sqlstr.toString());
				this.frowset = dao.search(sqlstr.toString());
				while (this.frowset.next()) {
					LazyDynaBean personbean = new LazyDynaBean();
					personbean.set("grade", "");
					personbean.set("codeitemid", this.frowset.getString("a0100"));
					personbean.set("codeitemdesc", this.frowset.getString("a0101"));
					personbean.set("parentid", "parentid");
					personbean.set("childid", this.frowset.getString("a0100"));
					personbean.set("personcount", "0");
					personbean.set("infokind", "1");
					personlist.add(personbean);
				}
			} else {
				sqlstr.delete(0, sqlstr.length());
				sqlstr.append("select a0100,a0101 from ");
				sqlstr.append(dbname);
				sqlstr.append("A01");
				sqlstr.append(" order by a0000");
				//System.out.println(sqlstr.toString());
				this.frowset = dao.search(sqlstr.toString());
				while (this.frowset.next()) {
					LazyDynaBean personbean = new LazyDynaBean();
					personbean.set("grade", "");
					personbean.set("codeitemid", this.frowset.getString("a0100"));
					personbean.set("codeitemdesc", this.frowset.getString("a0101"));
					personbean.set("parentid", "parentid");
					personbean.set("childid", this.frowset.getString("a0100"));
					personbean.set("personcount", "0");
					personbean.set("infokind", "1");
					personlist.add(personbean);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("personlist",personlist);
		//StringBuffer orderby=new StringBuffer();
		//orderby.append(" order by ");
		//orderby.append("a0000");	
	}

}
