package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchRelating.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Feb 23, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchRelating extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList persons=(ArrayList)this.getFormHM().get("persons");
		this.getFormHM().put("personlist",persons);
		ArrayList personname = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql= "";
		try {
			for(int i=0;i<persons.size();i++){
				sql = "select A0101 from "+persons.get(i).toString().substring(0,3)+"A01 where A0100 = '"+persons.get(i).toString().substring(3)+"'";
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					personname.add(this.frowset.getString("A0101"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("personname",personname);
	}

}
