package com.hjsj.hrms.service.ladp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Properties;

/**
 * 功能描述：ldap连接工厂，提供初始化ldap连接的方法。
 * 
 * @author liaowufeng
 * @version 1.0
 */
public class LdapConnectionFactory {
	// 初始化日志处理类
	private static Category log = Category.getInstance(Env.class.getName());

	/**
	 * 构造函数私有，防止实例化
	 */
	private LdapConnectionFactory() {
	}

	/**
	 * 获取 LDAP 服务器连接的方法
	 * 
	 * @param env
	 *            连接LDAP的连接信息
	 * @return DirContext - LDAP server的连接
	 */
	public static DirContext getDirContext(Env env) {
		DirContext ctx = null;
		try {
			// 初始化Properties对象
			Properties mEnv = new Properties();
			// 使用LDAP/AD的认证方式
			mEnv.put(Context.AUTHORITATIVE, "true");
			// 设定LDAP/AD的连接工厂
			mEnv.put(Context.INITIAL_CONTEXT_FACTORY, env.factory);
			// 设定LDAP/AD的url地址
			mEnv.put(Context.PROVIDER_URL, env.url);
			// 设定连接TimeOut
			if (!StringUtils.isEmpty(env.timeOut)) {
				mEnv.put("com.sun.jndi.ldap.connect.timeout", env.timeOut);
			}
			// 设定安全模式为simple方式
			mEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			// ssl通道访问
			if (env.isSSL) {
				// 设定访问协议为ssl
				mEnv.put(Context.SECURITY_PROTOCOL, "ssl");
				// 设置访问证书属性，若没有此证书将无法通过ssl访问AD
				System.setProperty("javax.net.ssl.trustStore",
						env.sslTrustStore);
				// 证书导入时的密码
				System.setProperty("javax.net.ssl.trustStorePassword",
						env.certficationPwd);
			}
			// 读取可以登陆ldap的帐号、密码
			mEnv.put(Context.SECURITY_PRINCIPAL, env.adminUID);
			mEnv.put(Context.SECURITY_CREDENTIALS, env.adminPWD);

			// 通过参数连接LDAP/AD
			ctx = new InitialDirContext(mEnv);

		} catch (NamingException ex) {
			ex.printStackTrace();
			log.error("创建LDAP连接失败，请检查参数是否正确！");

		}

		return ctx;
	}

	/**
	 * 关闭LDAP连接
	 * 
	 * @param dirContext
	 *            DirContext 已连接的LDAP的Context实例
	 */
	public static void closeDirContext(DirContext dirContext) {
		try {
			if (dirContext != null)
				dirContext.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("LDAP连接关闭失败！");
		}
	}
}
