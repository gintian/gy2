/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hrms.hjsj.sys.Des;
import com.hrms.struts.constant.SystemConfig;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Properties;

/**
 *<p>Title:LdapAccessBo</p> 
 *<p>Description:访问轻量级目录服务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-12:下午06:04:32</p> 
 *@author cmq
 *@version 4.0
 */
public class LdapAccessBo {
    private static String rootorg = ",CN=Users,DC=AJF,DC=com";
    private static String ldaphost =  "ldap://ldapserver";
    private static String ldapport ="389";                    
    //private static String ldapsslport =System .getProperty("ldapport");// "636";
    private static String ldapURL = ldaphost + ":" + ldapport; 
    //private static String ldapsslURL = ldaphost + ":" + ldapsslport ;
    //private static String keystore = "c:\\lib2\\cacerts";
    private static String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    /**
     * 查找轻量级目录服务中是否有此用户,秦山一期
     * @param user_id
     * @param password
     * @return
     */
    public static boolean isHaveTheUser(String user_id,String password)
    {
    	boolean bflag=false;
		/*
        String root = "qnpc.com";
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://docserver/");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, account + "@" + root);
        env.put(Context.SECURITY_CREDENTIALS, password);
        */
    	String DN=null;
		DirContext ctx = null;
		String ldaptype="";
		if(user_id==null|| "".equals(user_id)) {
            return false;
        }
		if(password==null|| "".equals(password)) {
            return false;
        }
		try
		{   
	    	String pass_encry=SystemConfig.getPropertyValue("pass_encry");
	    	if(pass_encry!=null&& "true".equalsIgnoreCase(pass_encry))
	    	{
	    		Des des=new Des();
	    		password=des.DecryPwdStr(password);
	    		
	    		if(password==null || "".equals(password.trim())) {
                    return false;
                }
	    	}
			rootorg=SystemConfig.getPropertyValue("rootorg");
	    	ldaphost=SystemConfig.getPropertyValue("ldaphost");//包含端口
	    	initialContextFactory=SystemConfig.getPropertyValue("initialContextFactory");
	    	ldaptype=SystemConfig.getPropertyValue("ldapserver");
	    	ldapport=SystemConfig.getProperty("ldapport");
	    	ldapURL=ldaphost + ":" + ldapport; 
	    	if(ldaptype==null||(!"AD".equalsIgnoreCase(ldaptype))) {
                DN= user_id +"@"+ rootorg.trim();
            } else {
                DN= "CN="+user_id +","+ rootorg.trim();//",qnpc.com";  // leidagan@ou=Employees,dc=shineerm,dc=cn ibm ldap userformat
            }
 		    								 //CN=FName LName,OU=OrgUnit_the_user_stored_in,DC=Domain_name,DC=Domain_suffix   AD userformat
	    	
			
			Properties props = new Properties();
			props.put(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory.trim());//指定JNDI服务提供程序中工厂类的名称,该工厂负责为其服务创建一个合适的InitialContext 对象
			/*use simple authentication mechanism*/
			props.put(Context.SECURITY_AUTHENTICATION, "simple"); 
			props.put(Context.SECURITY_CREDENTIALS, password);
			props.put(Context.SECURITY_PRINCIPAL, DN);
			props.put(Context.PROVIDER_URL, ldapURL.trim());
			
 		 	ctx = new InitialDirContext(props);//建立服务器的连接，如果链接上了则true，如果链接不上则false
 		    
            /*System.out.println("LDAP认证通过..."); 
            System.out.println("--dn="+DN);
            System.out.println("--url="+ldaphost);
            System.out.println("--initialContextFactory="+initialContextFactory);
            System.out.println("--password="+password);*/
 		 	bflag=true;
		}
        catch(javax.naming.AuthenticationException   e)   
        {   
               System.out.println("认证失败"); 
               System.out.println("--dn="+DN);
               System.out.println("--url="+ldaphost);
               System.out.println("--initialContextFactory="+initialContextFactory); 
               if(e.getMessage().contains("data 525")) {
                   System.out.println("用户不存在！");
               } else if(e.getMessage().contains("data 52e")) {
                   System.out.println("密码不正确！");
               } else {
                   e.printStackTrace();
               }
        }   
        catch(Exception   e)   
        {   
        	  System.out.println("认证出错：");  
              System.out.println("--dn="+DN);
              System.out.println("--url="+ldaphost);
              System.out.println("--initialContextFactory="+initialContextFactory);                     	  
              e.printStackTrace();  
        }   
		finally
		{
            try   
            {   
            	if(ctx!=null) {
                    ctx.close();
                }
            }   
            catch   (NamingException   e)   
            {   
            	e.printStackTrace();
            }   
		}
    	return bflag;
    }
    
    /**
     * 加上延时秒数控制,中建投
     * @param user_id
     * @param password
     * @param delay_secs
     * @return
     */
    public static boolean isHaveTheUser(String user_id,String password,long logon_secs)
    {
    	boolean bflag=true;

	 
		long start = System.currentTimeMillis()/1000;
		int del_secs=60;

		DirContext ctx = null;
		
		try
		{
	    	rootorg=SystemConfig.getProperty("rootorg");
	    	ldaphost=SystemConfig.getProperty("ldaphost");
	    	ldapport=SystemConfig.getProperty("ldapport");
	    	initialContextFactory=SystemConfig.getProperty("initialContextFactory");
	    	ldapURL=ldaphost + ":" + ldapport; 
			//String DN = "uid=" + user_id +","+ rootorg;//",cn=users,o=jic,c=cn";  // ibm ldap userformat 
	    	String DN="";
	    	String ldaptype=SystemConfig.getPropertyValue("ldapserver");
	    	if(ldaptype==null||(!"AD".equalsIgnoreCase(ldaptype))) {
                DN= user_id +"@"+ rootorg.trim();
            } else {
                DN= "CN="+user_id +","+ rootorg.trim();//",qnpc.com";
            }
			Properties props = new Properties();
			props.put(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory);
			/*use simple authentication mechanism*/
			props.put(Context.SECURITY_AUTHENTICATION, "simple"); 
			props.put(Context.SECURITY_CREDENTIALS, password);
			props.put(Context.SECURITY_PRINCIPAL, DN);
			props.put(Context.PROVIDER_URL, ldapURL);			
			try
			{
				String delay_secs=SystemConfig.getProperty("delay_time");
				if(!(delay_secs==null|| "".equals(delay_secs))) {
                    del_secs=Integer.parseInt(delay_secs);
                }
			}
			catch(Exception ex)
			{
				;
			}
			/**如果超过延时规定的秒数，则进行用户认证*/
			long delaytime=0;
			if(start-logon_secs>0){
				delaytime=start-logon_secs;
			}else{
				delaytime=logon_secs-start;
			}
			if(delaytime>del_secs) {
                return false;
            }
 		 	ctx = new InitialDirContext(props);
 		 	ctx.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	return bflag;
    }
    
}
