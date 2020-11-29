package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class GetFullnameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String usrname=(String)this.getFormHM().get("usrname");
			String name="";
			String sql="select fullname from operuser where username='"+usrname+"'";
			String _sql="select a0101 from usra01 where username='"+usrname+"'";
			RowSet rs=dao.search(sql);
			RowSet rs1=dao.search(_sql);
			if(rs.next()){
				if(rs.getString("fullname")!=null&&!"".equals(rs.getString("fullname"))){//有fullname优先fullname
					name=rs.getString("fullname");
				}else if(rs1.next()){
					if(rs1.getString("a0101")!=null&&!"".equals(rs1.getString("a0101"))){//其次是a0101
						name=rs.getString("a0101");
					}else{//都没有就是登录名
						name=usrname;
					}
				}else{
					name=usrname;
				}
			}else{
				name=usrname;
			}
			this.getFormHM().put("name", name);
		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
