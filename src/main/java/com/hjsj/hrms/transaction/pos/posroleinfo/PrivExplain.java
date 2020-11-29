package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 *<p>Title:PrivExplain.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:6:21:13 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class PrivExplain extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String table = (String)hm.get("table");
		ContentDAO dao = new ContentDAO(this.frameconn);
		if("k00".equalsIgnoreCase(table)){
			if(this.userView.getStatus()==4){
				String	codesetid = userView.getUserPosId();
				String sql = "select i9999 from k00 where flag='k' and e01a1 = '"+codesetid+"'";
				try {
					this.frowset = dao.search(sql);
					if(this.frowset.next())
						this.getFormHM().put("i9999",this.frowset.getString("i9999"));
					else
						this.getFormHM().put("i9999","no");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				this.getFormHM().put("usertable","k00");
				this.getFormHM().put("usernumber",codesetid);
			}else if(this.userView.getStatus()==0){
				if(this.userView.getA0100()==null|| "".equalsIgnoreCase(this.userView.getA0100()))
					this.getFormHM().put("i9999","no");
				else{
					String	codesetid = userView.getUserPosId();
					String sql = "select i9999 from k00 where flag='k' and e01a1 = '"+codesetid+"'";
					try {
						this.frowset = dao.search(sql);
						if(this.frowset.next())
							this.getFormHM().put("i9999",this.frowset.getString("i9999"));
						else
							this.getFormHM().put("i9999","no");
					} catch (SQLException e) {
						e.printStackTrace();
					}
					this.getFormHM().put("usertable","k00");
					this.getFormHM().put("usernumber",codesetid);
				}
			}
		}else if("a00".equalsIgnoreCase(table)){
			if(this.userView.getStatus()==4){
				String a0100 = this.userView.getA0100();
				String dbname = this.userView.getDbname();
				String sql = "select i9999 from "+dbname+"a00 where flag='t' and a0100 = '"+a0100+"' order by createtime desc";
				try {
					this.frowset = dao.search(sql);
					if(this.frowset.next())
						this.getFormHM().put("i9999",this.frowset.getString("i9999"));
					else
						this.getFormHM().put("i9999","no");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				this.getFormHM().put("usertable",dbname+"a00");
				this.getFormHM().put("usernumber",a0100);
			}else if(this.userView.getStatus()==0){
				if(this.userView.getA0100()==null|| "".equalsIgnoreCase(this.userView.getA0100()))
					this.getFormHM().put("i9999","no");
				else{
					String a0100 = this.userView.getA0100();
					String dbname = this.userView.getDbname();
					String sql = "select i9999 from "+dbname+"a00 where flag='t' and a0100 = '"+a0100+"' order by createtime desc";
					try {
						this.frowset = dao.search(sql);
						if(this.frowset.next())
							this.getFormHM().put("i9999",this.frowset.getString("i9999"));
						else
							this.getFormHM().put("i9999","no");
					} catch (SQLException e) {
						e.printStackTrace();
					}
					this.getFormHM().put("usertable",dbname+"a00");
					this.getFormHM().put("usernumber",a0100);
				}
			}
		}
	}

}
