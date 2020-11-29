/**
 * 
 */
package com.hjsj.hrms.interfaces.certificate;

/**
 * <p>Title:对LADP服务器的操作</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-12:15:23:21</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class LdapOperator {
	public static final String SUNLDAPCONTEXT="com.sun.jndi.ldap.LdapCtxFactory";
	//Active Directory 路径
	public static final int HOST=0; 
	/**端口*/
	public static final int PORT=1;
	/**域名*/
	public static final int DOMAIN_NAME=2;
	/**域*/
	public static final int DOMAIN=3;
	/**验证对象*/
	public static final int GROUP = 4;
	/***/   
	public static final int AUTH_TYPE = 5; //验证方式
	
}
