package com.hjsj.hrms.interfaces.adauthenticate;

import com.hjsj.hrms.interfaces.decryptor.TripleDES;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.DataDictionary;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

public class NYBywAuthUser {
	private String username;
	private String password;
	private String ywusername;
	private String ywpassword;
	public NYBywAuthUser(String username,String password)
	{
		this.username=username;
		this.password=password;
	}
	public void getYwUserAuthUser(String UniqueField)
	{
    	StringBuffer sql=new StringBuffer();
    	try{
		    sql.delete(0,sql.length());
			sql.append("select UserName,PassWord from OperUser where A0100='");
			sql.append(getUserA0100(UniqueField));
			sql.append("'");
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			if(rs!=null && rs.size()>0)
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				this.ywusername=rec.get("username").toString();
				this.ywpassword=rec.get("password").toString();
			}
		}catch(Exception e)
		{
		    e.printStackTrace();	
		}
	}
	private String getUserA0100(String UniqueField) throws Exception
	{
        String a0100="-1";
		StringBuffer sql=new StringBuffer();
		List dblist=DataDictionary.getDbpreList();
		for(int i=0;i<dblist.size();i++)
		{
			sql.delete(0,sql.length());
			sql.append("select a0100");
			sql.append(" from ");
			sql.append(dblist.get(i));
			sql.append("a01 where ");
			sql.append(UniqueField);
			sql.append("='");
			sql.append(new TripleDES("EncryptionString").DecryptionStringData(this.username.trim()));
			sql.append("'");
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			if(rs!=null && rs.size()>0)
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				a0100=rec.get("a0100").toString();
				break;
			}
		}	
		return a0100;
	
	}
	/**
	 * @return Returns the ywpassword.
	 */
	public String getYwpassword() {
		return ywpassword;
	}
	/**
	 * @param ywpassword The ywpassword to set.
	 */
	public void setYwpassword(String ywpassword) {
		this.ywpassword = ywpassword;
	}
	/**
	 * @return Returns the ywusername.
	 */
	public String getYwusername() {
		return ywusername;
	}
	/**
	 * @param ywusername The ywusername to set.
	 */
	public void setYwusername(String ywusername) {
		this.ywusername = ywusername;
	}

}
