/*
 * File：Env.java
 * Author：wangzhongjun
 * Create time：2004-02-02
 * 维护人员：
 * 维护日期：
 * 维护原因：
 * 维护人员：
 * 维护日期：
 * 维护原因：
*/
package com.hjsj.hrms.service.ladp;

/**
 * <p>Title:Env</p>
 * <p>Description:连接LDAP的连接属性</p>
 * <p>Company:hjsj</p>
 * @author wangzhongjun
 * @version 1.0
 *  
 */ 
public class Env { 

    // 无论用什么LDAP服务器的固定写法，指定了JNDI服务提供者中工厂类 
    public String factory ; 
    // 服务连接地址 
    public String url ; 
    // 登陆LDAP的用户名和密码 
    public String adminUID ; 
    // 登陆LDAP用户密码 
    public String adminPWD ; 
    // 安全访问需要的证书库 
    public String sslTrustStore; 
    // 安全通道访问 
    public boolean isSSL = false;
    // 证书导入密码
    public String certficationPwd;
    // 连接TimeOut 
    public String timeOut; 

    /** 
     * 构造函数 
     */ 
    public Env() { 
    } 

    /** 
     * 构造函数 
     * @param factory LDAP工厂类 
     * @param url     LDAP URL 
     * @param adminUID LDAP 用户 
     * @param adminPWD LDAP 密码 
     */ 
    public Env(String factory, String url, String adminUID, String adminPWD) { 
        this.factory = factory; 
        this.url = url; 
        this.adminUID = adminUID; 
        this.adminPWD = adminPWD; 
    } 

    /** 
     * 构造函数 
     * @param factory LDAP 工厂类名 
     * @param url     LDAP URL 
     * @param adminUID LDAP 用户 
     * @param adminPWD LDAP 密码 
     * @param sslTrustStore  安全访问需要的证书 
     * @param securityProtocol 安全通道访问 
     */ 
    public Env(String factory, String url, String adminUID, String adminPWD, 
               String sslTrustStore, 
               boolean isSSL, String certficationPwd) { 
        this.factory = factory; 
        this.url = url; 
        this.adminUID = adminUID; 
        this.adminPWD = adminPWD; 
        this.sslTrustStore = sslTrustStore; 
        this.isSSL = isSSL; 
        this.certficationPwd = certficationPwd;
    } 

    /** 
     * 构造函数 
     * @param factory LDAP 工厂类名 
     * @param url     LDAP URL 
     * @param adminUID LDAP 用户 
     * @param adminPWD LDAP 密码 
     * @param timeOut  超时时间
     * @param sslTrustStore  安全访问需要的证书 
     * @param securityProtocol 安全通道访问 
     */ 
    public Env(String factory, String url, String adminUID, String adminPWD, 
               String timeOut, 
               String sslTrustStore, 
               boolean isSSL, String certficationPwd) { 
        this.factory = factory; 
        this.url = url; 
        this.adminUID = adminUID; 
        this.adminPWD = adminPWD; 
        this.timeOut = timeOut; 
        this.sslTrustStore = sslTrustStore; 
        this.isSSL = isSSL; 
        this.certficationPwd = certficationPwd;
    } 
} 

