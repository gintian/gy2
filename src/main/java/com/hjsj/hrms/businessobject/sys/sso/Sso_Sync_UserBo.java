/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.sso;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *<p>Title:Sso_Sync_UserBo</p> 
 *<p>Description:用户由统一认证平台统一管理，对用户信息维护时，
 *   中间表名：T_SSO_USER
 *   PK_ACCOUNTSYNCH    int NOT NULL IDENTITY (1, 1),-- 顺序号
 *	 User_id	VARCHAR(50)	认证服务器中的用户帐号	
 *   User_name	VARCHAR(50)	用户姓名	
 *	 Cert_id	VARCHAR(18)	用户编号（身份证号）	
 *	 password	VARCHAR(50)	用户口令	
 * 	 state	    VARCHAR(1)	用来判断统一认证服务器用户的帐户的状态	=1启用 =2禁用
 *   Sync_State     varchar(1) NULL--用来判断该记录和SSO系统同步的情况
 *		新增用户：在中间表中增加记录
 *		用户禁用及解禁：	
 *</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-13:上午11:40:17</p> 
 *@author cmq
 *@version 4.0
 */
public class Sso_Sync_UserBo {
	private static Connection conn=null;
	
	private Connection hr_conn=null;
	/**登录库前缀*/
	private String[] str_dbs=null;
	/**账号同步关联字段*/
	private String linkfield="A0177";
	/**账号及口令字段*/
	private String user_field="username";
	private String user_pwd_field="userpassword";
	private Connection getConnection()
	{
		Connection conn=null;
		try
		{
			String url=SystemConfig.getProperty("sso_db_url");
			String user_id=SystemConfig.getProperty("sso_db_user");
			String pwd=SystemConfig.getProperty("sso_db_pwd");
//			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
//			conn=DriverManager.getConnection("jdbc:microsoft:sqlserver://127.0.0.1:1433;databasename=ykchr","sa","");
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			conn=DriverManager.getConnection("jdbc:oracle:thin:@192.192.100.124:1521:ykchr","yksoft","yksoft1919");
//			Class.forName("com.ibm.db2.jcc.DB2Driver");
//			conn=DriverManager.getConnection("jdbc:db2://kf:50000/webykchr","db2admin","db2admin");
			Class.forName(SystemConfig.getProperty("sso_db_driver"));
			conn=DriverManager.getConnection(url,user_id,pwd);
		}
		catch(ClassNotFoundException ee)
		{
			ee.printStackTrace();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return conn;		
	}
	/**
	 * 两边账号开始同步方法，通过定时器扫描
	 */
	public void sync_logon_user()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			if(conn==null) {
                conn=getConnection();
            }
			String user_id=null;
			String cert_id=null;
			String pwd=null;
			String state=null;
//			if(ConstantParamter.isEncPwd())
//			{
//				Des des=new Des();
//				pwd=des.EncryPwdStr("ssss");				
//			}
			buf.append("select User_id,Cert_id,password,state from T_SSO_USER order by PK_ACCOUNTSYNCH");
			
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				user_id=rset.getString("user_id");
				cert_id=rset.getString("cert_id");
				state=rset.getString("state");  //=1
				pwd=rset.getString("password");
				sync_update(user_id,cert_id,state,pwd);
				sync_update_operuser(user_id,cert_id,state,pwd);
			}
			/**清空中间表的数据*/
			buf.setLength(0);
			buf.append("delete from T_SSO_USER");
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 同步更新登录用户库中的账号
	 * @param user_id 账号
	 * @param cert_id　身份证
	 * @param state　　禁用状态
	 * @param pwd　　　口令
	 */
	private void sync_update(String user_id,String cert_id,String state,String pwd )
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			if(str_dbs==null||str_dbs.length==0) {
                return;
            }
			/**如果禁用，则把账号清空*/
			if("2".equals(state)) {
                user_id="";
            }
			/**分析口令是否要加密*/
			if(pwd==null) {
                pwd="";
            }
			if(ConstantParamter.isEncPwd())
			{
				Des des=new Des();
				pwd=des.EncryPwdStr(pwd);				
			}
			ContentDAO dao=new ContentDAO(this.hr_conn);
			for(int i=0;i<str_dbs.length;i++)
			{
				buf.append("update ");
				buf.append(str_dbs[i]);
				buf.append("A01 set ");
				buf.append(this.user_field);
				buf.append("='");
				buf.append(user_id);
				buf.append("'");
				if(pwd!=null){
					buf.append(",");
					buf.append(this.user_pwd_field);
					buf.append("='");
					buf.append(pwd);
					buf.append("'");
				}
				buf.append(" where ");
				buf.append(this.linkfield);
				buf.append("=");
				buf.append("'");
				buf.append(cert_id);
				buf.append("'");
				dao.update(buf.toString());
				buf.setLength(0);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	
	/**
	 * 更新同步operuser中的用户,对此用户则暂时同步密码
	 * @param user_id
	 * @param cert_id
	 * @param state
	 * @param pwd
	 */
	private void sync_update_operuser(String user_id,String cert_id,String state,String pwd )
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			/**分析口令是否要加密*/
			if(pwd==null) {
                pwd="";
            }
			if(ConstantParamter.isEncPwd())
			{
				Des des=new Des();
				pwd=des.EncryPwdStr(pwd);				
			}
			ContentDAO dao=new ContentDAO(this.hr_conn);
			buf.append("update operuser ");
			buf.append(" set  password ");
			buf.append("='");
			buf.append(pwd);
			buf.append("'");
			buf.append(" where username='");
			buf.append(user_id);
			buf.append("'");
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
		
	}
	/**
	 * 初始化数据,取得登录库前缀
	 *
	 */
	private void initdata()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("select str_value from constant where constant='SS_LOGIN'");
		ContentDAO dao=new ContentDAO(hr_conn);
		String db_str="";
		try
		{
			RowSet set=dao.search(buf.toString());
			if(set.next()) {
                db_str=Sql_switcher.readMemo(set,"str_value");
            }
			linkfield=SystemConfig.getProperty("sso_link_field");
			DbNameBo dbbo=new DbNameBo(hr_conn);
			this.user_field=dbbo.getLogonUserNameField();
			this.user_pwd_field=dbbo.getLogonPassWordField();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		str_dbs=StringUtils.split(db_str, ",");
	}

	public Sso_Sync_UserBo(Connection hr_conn) {
		super();
		this.hr_conn = hr_conn;
		initdata();
	}
	
	/**
	 * 两边账号开始同步方法，通过定时器扫描
	 * Sync_State=3,SSO修改帐户.修改到hrp账户管理里面
	 */
	public void sylg_logon_user()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			if(conn==null) {
                conn=getConnection();
            }
			String user_id=null;
			String cert_id=null;
			String pwd=null;
			String state=null;

			buf.append("select User_id,Cert_id,password,state from T_SSO_USER ");
			buf.append(" where Sync_State='3' ");
			buf.append(" order by PK_ACCOUNTSYNCH");
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				user_id=rset.getString("user_id");
				cert_id=rset.getString("cert_id");
				state=rset.getString("state");  //=1
				pwd=rset.getString("password");
				sync_update(user_id,cert_id,state,pwd);
				sync_update_operuser(user_id,cert_id,state,pwd);
			}
			/**清空中间表的数据*/
			buf.setLength(0);
			buf.append("delete from T_SSO_USER");
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
