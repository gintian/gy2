package com.hjsj.hrms.businessobject.attestation.ldap.conn;

import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class ConnFactory {

	private String factory = "com.sun.jndi.ldap.LdapCtxFactory";
	
	private String root;
	
	private String username;
	
	private String password;
	
	private String ipAddress;
	
	private String certficationPath;
	
	private String certficationPwd;
	
	public ConnFactory(){
		this.ipAddress = SystemConfig.getPropertyValue("LDAPIPADRESS");//ip地址
		this.username = SystemConfig.getPropertyValue("LDAPACCOUNT");//账户名称
		this.password = SystemConfig.getPropertyValue("LDAPPASSWORD");//账户密码
		this.root = SystemConfig.getPropertyValue("LDAPROOT");//根节点
		this.certficationPath = SystemConfig.getPropertyValue("certficationPath");//根节点
		this.certficationPwd = SystemConfig.getPropertyValue("certficationPwd");//根节点
	}
	
	public ConnFactory(String ipAddress,String username,String password,String root,String certficationPath,String certficationPwd){
		this.ipAddress = ipAddress;//ip地址
		this.username = username;//账户名称
		this.password = password;//账户密码
		this.root = root;//根节点
		this.certficationPath = certficationPath;//根节点
		this.certficationPwd = certficationPwd;//根节点
	}
	
	/**
	 * 建立LDAP连接的配置信息
	 * @param isSSL 值 true：SSL连接方式 false：普通连接方式
	 * @return
	 */
	private Hashtable getConfig(boolean isSSL) {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, this.factory);
		if(this.ipAddress == null || "".equals(this.ipAddress)){
			return null;
		}
		if(isSSL){
			if(this.certficationPath == null || "".equals(this.certficationPath) || this.certficationPwd == null || "".equals(this.certficationPwd)){
				return null;
			}
			env.put(Context.PROVIDER_URL, "ldap://" + this.ipAddress + ":636/" + this.root);
			System.setProperty("javax.net.ssl.trustStore", this.certficationPath);
			System.setProperty("javax.net.ssl.trustStorePassword",this.certficationPwd);
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}else{
			env.put(Context.PROVIDER_URL, "ldap://" + this.ipAddress + ":389/" + this.root);
		}
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		if(this.username != null && !"".equals(this.username) && this.password != null && !"".equals(this.password)){
			env.put(Context.SECURITY_PRINCIPAL, this.username);
			env.put(Context.SECURITY_CREDENTIALS, this.password);
		}
		return env;
	}
	
	private DirContext createConn(boolean isSSL){
		Hashtable env = null;
		DirContext ctx = null;
		try {
			env = getConfig(isSSL);
			if(env == null){
				return null;
			}
			ctx = new InitialDirContext(env);
		} catch (Exception e) {
			e.printStackTrace();
			Category.getInstance("com.hjsj.hrms.businessobject.attestation.ldap.conn.ConnFactory").error(
					"LADP 连接失败！" + env.get(Context.SECURITY_PRINCIPAL));
		}
		return ctx;
	}
	
	public DirContext getConn() {
		return createConn(false);
	}
	
	public DirContext getSSLConn() {
		return createConn(true);
	}
}
