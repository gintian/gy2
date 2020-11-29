package com.hjsj.hrms.interfaces.adauthenticate;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.DataDictionary;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

public class HjsjUserAuth {
	private String username;    //用户名
	private String password;    //密码
	public  void SearchUserPassWord(String UserID,String namefield,String pwfield,String AuthField)
	{
		
		StringBuffer sql=new StringBuffer();
		sql.append("select Username,PassWord from operuser where username='");
		sql.append(UserID.trim());
		sql.append("'");
		List rso=ExecuteSQL.executeMyQuery(sql.toString());
		boolean ishaveUser=false;
		if(rso!=null && rso.size()>0)
		{
			LazyDynaBean rec=(LazyDynaBean)rso.get(0);
			username=rec.get("username").toString();
			password=rec.get("password").toString();
			ishaveUser=true;
		}
		if(!ishaveUser)
		{
			List dblist=DataDictionary.getDbpreList();
			for(int i=0;i<dblist.size();i++)
			{
				sql.delete(0,sql.length());
				sql.append("select ");
				sql.append(namefield);
				sql.append(",");
				sql.append(pwfield);
				sql.append(" from ");
				sql.append(dblist.get(i));
				sql.append("a01 where ");
				sql.append(AuthField);
				sql.append("='");
				sql.append(UserID.trim());
				sql.append("'");
				List rs=ExecuteSQL.executeMyQuery(sql.toString());
				if(rs!=null && rs.size()>0)
				{
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					username=rec.get(namefield.toLowerCase()).toString();
					password=rec.get(pwfield.toLowerCase()).toString();
					break;
				}
			}	
		}	
	}
	public String getPassword() {
		return "";//password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	

}
