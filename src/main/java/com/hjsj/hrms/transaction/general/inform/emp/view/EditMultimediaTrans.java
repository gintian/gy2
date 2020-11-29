package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 
 *<p>Title:EditMultimediaTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 7, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class EditMultimediaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 String id = (String)this.getFormHM().get("id");
		 String foldername = (String)this.getFormHM().get("foldername");
		 String sql = "update mediasort set sortname ='"+foldername+"' where id = '" +id+"'";
		 ContentDAO dao = new ContentDAO(this.getFrameconn());
		 try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 this.getFormHM().put("name",foldername);
		 
	}

}
