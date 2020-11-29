package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.org.orgpre.OrgPreBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

public class CondPersonJob implements Job {
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		Connection conn=null;
		RowSet rs =null;
		try {
			conn = (Connection) AdminDb.getConnection();
			OrgPreBo org = new OrgPreBo(conn);
			org.condRealPerson();
			
			ContentDAO dao = new ContentDAO(conn);
			String password = "";
			rs = dao.search("select Password from operuser where UserName='su'");
			String check="0";
			if(rs.next()){
				password = rs.getString("Password");
				password=password!=null?password:"";
				check="1";
			}
			UserView uv = new UserView("su",password,conn);
			uv.canLogin();
			org.doCount(conn, uv);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null) {
					rs.close();
				}
				if(conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
