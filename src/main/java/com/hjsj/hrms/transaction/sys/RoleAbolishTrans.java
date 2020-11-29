package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 撤销角色关联人员列表
 * @author xujian
 *Mar 31, 2010
 */
public class RoleAbolishTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selected=(ArrayList)this.getFormHM().get("selected");
		ContentDAO dao = new ContentDAO(this.frameconn);
		String msg="no";
		String rs[]=null;
		try{
			if(selected!=null&&selected.size()>0){
				for(int i=0;i<selected.size();i++){
					String roleandstaff=(String)selected.get(i);
					rs=roleandstaff.split("`");
					if(rs.length==2){
						String sql="delete from t_sys_staff_in_role where staff_id='"+rs[1]+"' and role_id='"+rs[0]+"'";
						dao.update(sql);
					}
				}
				msg="ok";
			}
			
		}catch(Exception e){
			msg="no";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(rs!=null&&rs.length==2&&"ok".equalsIgnoreCase(msg)){
				this.getFormHM().put("roleid", rs[0]);
			}
			this.getFormHM().put("msg", msg);
		}
	}
}
