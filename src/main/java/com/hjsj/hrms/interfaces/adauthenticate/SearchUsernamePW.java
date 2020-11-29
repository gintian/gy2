package com.hjsj.hrms.interfaces.adauthenticate;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.DataDictionary;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

public class SearchUsernamePW {
	private String username;    //用户名
	private String password;    //密码
	public  void SearchUserPw(String UniqueID,String namefield,String pwfield)
	{
		
		StringBuffer sql=new StringBuffer();
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
			sql.append("a01 where UserName='");
			sql.append(UniqueID.trim());
			sql.append("'");
			System.out.println(sql.toString());
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
	public String getPassword() {
		return password;
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
