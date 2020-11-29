package com.hjsj.hrms.businessobject.infor;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import java.sql.Connection;
import java.sql.ResultSet;

public class CleanPersonSetting {

	public static boolean cleanByA0100(String a0100,String pre,String newname){
		Connection con =null; 
		boolean result = false;
		String name = "username";
		try{
			con =AdminDb.getConnection();
			DbNameBo bo = new DbNameBo(con);
            name = bo.getLogonUserNameField();
			
			String sql =" select "+name+" from "+pre.toLowerCase()+"a01 where a0100='"+a0100+"'";
			ContentDAO dao = new ContentDAO(con);
			ResultSet rs = dao.search(sql);
			String username = "";
			while(rs.next()){
				username = rs.getString(name);
			}
			if(null==username||"".equals(username)) {
                result = true;
            } else {
				if(null!=newname&&!"".equals(newname)){//修改操作
					result = updatePortalSetting(username,newname);
				}else//删除操作
                {
                    result = cleanByUsername(username);
                }
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(result);
			PubFunc.closeDbObj(con);
		}
		return result;
	}
	
	
	public static boolean cleanByUsername(String username){
		cleanPortalSetting(username);
		return true;
	}
	
	private static void cleanPortalSetting(String username){
		Connection con =null; 
		try{
			con =AdminDb.getConnection();;
			ContentDAO dao = new ContentDAO(con);
			String sql = " delete from t_sys_table_portal where username ='"+username+"'";
			dao.update(sql);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(con);
		}
	}
	
	private static boolean updatePortalSetting(String oldname,String newname){
		Connection con =null; 
		boolean flag = false;
		try{
			con =AdminDb.getConnection();;
			ContentDAO dao = new ContentDAO(con);
			String sql = " update  t_sys_table_portal set username = '"+newname+"' where username ='"+oldname+"'";
			dao.update(sql);
			flag =  true;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(con);
		}
		return flag;
	}
	
	
}
