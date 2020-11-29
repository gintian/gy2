package com.hjsj.hrms.interfaces.adauthenticate;

public interface ADConstant {
	//对象工厂
	public static final String SUNLDAPCONTEXT="com.sun.jndi.ldap.LdapCtxFactory";
	//Active Directory 路径
	public static final int HOST=0; 
	//端口
	public static final int PORT=1;
	//域名
	public static final int DOMAIN_NAME=2;
	//域
	public static final int DOMAIN=3;
	//用户名
	public static final int USERNAME=4; 
	//用户密码
	public static final int USERPASSWORD=5;
	//验证对象
	public static final int GROUP = 6;	
    //验证方式     
	public static final int AUTH_TYPE = 7; 
    //预留参数
	public static final int ISVALIDE = 8; 
	//一级域
	public static int firdomain=0;  
	//二级域
	public static int secdomain=1;   
                               

}
