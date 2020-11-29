package com.hjsj.hrms.businessobject.sys.sso;

import com.hjsj.hrms.businessobject.sys.LdapAccessBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.admin.VerifyUser;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
/**
 *<p>Title:IsHaveTheUserLogon.java</p> 
 *<p>Description:AD域认证</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 10, 2014</p> 
 *@author Administrator
 *@version 7.0
 */
public class IsHaveTheUserLogon implements VerifyUser 
{	
	/**
	 * 从外部系统传过来username可能需要转换
	 */
	private String userid="";
	/**
	 * 新增一个UserID接口,20090624
	 */
	@Override
    public String getUserId() {
		return this.userid;
	}

	@Override
    public boolean isExist(String username, String password)
	{
		boolean isCorrect = false;		
		if(username==null || username.trim().length()<=0) {
			return isCorrect;
		}
		if(password==null || password.trim().length()<=0) {
			return isCorrect;
		}

		password = PubFunc.keyWord_reback(password);
	//	System.out.println("账号："+username+"---密码："+password);
		
		String interiorcheck = SystemConfig.getPropertyValue("logoninteriorcheck");		
    	if(LdapAccessBo.isHaveTheUser(username,password))//验证轻量级目录服务中是否有此用户
		{
    		isCorrect = true;
    		this.userid = username;//.substring(0, 5); //eHR系统中的用户取自于LDAP用户前5位

		}else if(interiorcheck!=null && "true".equals(interiorcheck))
		{
			Connection conn = null;
			try
			{
				conn = AdminDb.getConnection();
				UserView userView = new UserView(username,password,conn);	
				if(userView.canLogin()) {
					isCorrect = true;
				}
			}catch(Exception e)
			{
				e.printStackTrace();	
			}finally
			{
				try{
				   if (conn != null){
					   conn.close();
				   }
			    }catch (SQLException sql){
				//sql.printStackTrace();
			    }
			}
		}
    	return isCorrect;
	}

}
