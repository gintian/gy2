package com.hjsj.hrms.businessobject.attestation.zjz;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.admin.VerifyUser;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class AttestationFromOA implements VerifyUser {

	private String userid="";

	@Override
    public boolean isExist(String username, String password)
	{
		boolean isCorrect=false;		
		if(username==null|| "".equals(username)) {
			username="";
		}
		if(password==null|| "".equals(password)) {
			password="";
		}
		Connection conn=null;		
		try
		{
			conn=AdminDb.getConnection("ZJOAMSSQL");	
			ContentDAO oa_dao=new ContentDAO(conn);
			String pass_encry=SystemConfig.getPropertyValue("pass_encry");
	    	if(pass_encry!=null&& "true".equalsIgnoreCase(pass_encry))
	    	{
	    		Des des=new Des();
	    		password=des.DecryPwdStr(password);
	    	}
	    	password=encrypt(password);
			String sql="select sfzh  from QJUserInfo where UserNameEn='"+username+"' and isnull(PassWord,'')='"+password+"'";
			//System.out.println(sql);
			RowSet rs=oa_dao.search(sql);
			if(rs.next())
			{
				this.userid=getUserid(rs.getString("sfzh"));
				isCorrect=true;
			}
				
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeResource(conn);
		}
		return isCorrect;
	}	
	@Override
    public String getUserId()
	{
		return this.userid;
	}
	private String getUserid(String sfzh)
	{
		Connection conn=null;		
		if(sfzh==null||sfzh.length()<=0) {
			return "";
		}
		String userid="";
		try
		{
			conn=AdminDb.getConnection();	
			ContentDAO dao=new ContentDAO(conn);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String idCardField=sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");		
			ArrayList dblist=new ArrayList();
			String sql="select pre from dbname";
			RowSet rs=dao.search(sql);
			AttestationUtils utils=new AttestationUtils();
			LazyDynaBean fieldbean=utils.getUserNamePassField();
			String username_field=(String)fieldbean.get("name");
		    String password_field=(String)fieldbean.get("pass");
			while(rs.next())
			{
				dblist.add(rs.getString("pre"));
			}
			String nbase="";
			String a0100="";
			for(int i=0;i<dblist.size();i++)
			{
				String pre=dblist.get(i).toString();
				sql="select A0100,"+username_field+" as l_name,"+password_field+" as l_pass from "+pre+"A01 where "+idCardField+"='"+sfzh+"'";
				//System.out.println(idCardField+"---"+sql);
				rs=dao.search(sql);
				if(rs.next())
				{
					nbase=pre;
					a0100=rs.getString("A0100");
					//userid=rs.getString("l_name");
					break;
				}
			}
			sql="select username,password from operuser where a0100='"+a0100+"' and nbase='"+nbase+"'";
			//System.out.println("2---"+sql);
			rs=dao.search(sql);
			if(rs.next())
			{
				userid=rs.getString("username");				
			}
			//System.out.println("userid---"+userid);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeResource(conn);
		}
		return userid;
	}
	/**
	 * 中建总oa加密转码
	 * @param strUnEncrypt
	 * @return
	 */
	 public String encrypt(String strUnEncrypt)//232336;454855 ;#C!PVG
	 {
	    	int length=strUnEncrypt.length();
	    	String strEncrypt="";
	    	String strTemp="";
	    	int iTemp=0;
	    	for(int i=0;i<length;i++)
	    	{
	    		strTemp=strUnEncrypt.substring(i,i+1);
	    		int s=i+1;
	    		iTemp=s%5;
	    		char c=strUnEncrypt.charAt(i);
	    		int cc=(int)c;  
	    		int dd=0;
	    		char ch;
	    		switch(iTemp)
	    		{
	    		    case 0:
	    		      dd=c^1;    		      
	    		      strEncrypt=strEncrypt+(char)dd;
	    		      break;	
	    		    case 1:
	    		    	dd=c^2;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		    case 2:
	    		    	dd=c^3;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		    case 3:
	    		    	dd=c^2;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		    case 4:
	    		    	dd=c^1;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		}
	    	}
	    	return strEncrypt;
	    }
}
