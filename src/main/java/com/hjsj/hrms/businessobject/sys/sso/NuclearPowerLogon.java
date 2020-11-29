package com.hjsj.hrms.businessobject.sys.sso;

import com.hjsj.hrms.businessobject.sys.LdapAccessBo;
import com.hrms.struts.admin.VerifyUser;

import java.sql.ResultSet;
/**
 * 核电接口
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 5, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class NuclearPowerLogon implements VerifyUser {
	
	@Override
    public String getUserId() {
		return "";
	}
	@Override
    public boolean isExist(String arg0, String arg1)
	{
		boolean isCorrect=false;		
		if(arg0==null|| "".equals(arg0)) {
			return isCorrect;
		}
		if(arg1==null|| "".equals(arg1)) {
			return isCorrect;
		}
		return getAuthorization(arg0,arg1);
	}
	/**
	 * 验证身份
	 * @param username
	 * @param password
	 * @return
	 */
    private boolean getAuthorization(String username,String password)
    {
    	boolean isCorrect=false;
    	if(LdapAccessBo.isHaveTheUser(username,password))//验证轻量级目录服务中是否有此用户
		{
    		SsoDbConnection.getStatement();
    		StringBuffer sql=new StringBuffer();
    		sql.append("select 1 from v_all_user where login='"+username+"'");
    		ResultSet rs=SsoDbConnection.getResultSet(sql.toString());
    		try
    		{
    			if(rs.next()) {
					isCorrect=true;
				}
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}finally
    		{
    			try
    			{
    				if(rs!=null) {
						rs.close();
					}
    				SsoDbConnection.close();
    			}catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
		}
    	return isCorrect;
    }
}
