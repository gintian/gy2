package com.hjsj.hrms.transaction.pos.posparameter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveDBTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 2, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SaveDBTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String strsql = "select * from dbname ";
		ArrayList dblist = new ArrayList();
		try {
			this.frowset = dao.search(strsql.toString());
			while(this.frowset.next()){
				dblist.add(this.frowset.getString("pre").toString());
			}
		} catch (SQLException e) {e.printStackTrace();}
        ArrayList dbture = (ArrayList)this.getFormHM().get("dbtrue");
        StringBuffer dbpres = new StringBuffer();
        dbpres.append("");
        for(int i=0;i<dbture.size();i++){
        	String bool = (String)dbture.get(i);
        	if("true".equals(bool)){
        		dbpres.append(""+dblist.get(i)+",");
        	}
        }
        if(dbpres.length()>=1)
        	dbpres.setLength(dbpres.length()-1);
		this.getFormHM().put("mess",dbpres.toString());
	}

}
