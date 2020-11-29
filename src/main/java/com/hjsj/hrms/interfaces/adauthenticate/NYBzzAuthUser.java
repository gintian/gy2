/**
 * 
 */
package com.hjsj.hrms.interfaces.adauthenticate;

import com.hjsj.hrms.interfaces.decryptor.TripleDES;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.DataDictionary;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

/**
 * @author Owner
 *
 */
public class NYBzzAuthUser {
	private String username;
	private String password;
	private String zzusername;
	private String zzpassword;
	public NYBzzAuthUser(String username,String password)
	{
		this.username=username;
		this.password=password;
	}
	public void getZzUserAuthUser(String namefield,String pwfield,String UniqueField)
	{
		try{
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
				sql.append("a01 where ");
				sql.append(UniqueField);
				sql.append("='");
				sql.append(new TripleDES("EncryptionString").DecryptionStringData(this.username.trim()));
				sql.append("'");
				List rs=ExecuteSQL.executeMyQuery(sql.toString());
				if(rs!=null && rs.size()>0)
				{
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					zzusername=rec.get(namefield.toLowerCase()).toString();
					zzpassword=rec.get(pwfield.toLowerCase()).toString();
					break;
				}
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String getZzpassword() {
		return zzpassword;
	}
	public void setZzpassword(String zzpassword) {
		this.zzpassword = zzpassword;
	}
	public String getZzusername() {
		return zzusername;
	}
	public void setZzusername(String zzusername) {
		this.zzusername = zzusername;
	}
	
}
