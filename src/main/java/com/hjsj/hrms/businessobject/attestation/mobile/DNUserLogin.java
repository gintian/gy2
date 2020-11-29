package com.hjsj.hrms.businessobject.attestation.mobile;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

public class DNUserLogin {
	private String ldaphost="ldap://10.4.41.1";//服务器地址	
	private String ldapport ="389";  
	private String version="3"; 
	private String ssoServerUrl="http://kmapp01.bmcc.com.cn/mvnforum/token.jsp";
	private String baseDn="DC=bmcc,DC=com,DC=cn";//域
	private String adUserID="hrbase";//
	private String adPassword="ZXCasd,123";//
	private String testName="";
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	/**
	 * 链接AD的用户名，密码
	 * @param adUserID 
	 * @param adPassword
	 */
	public DNUserLogin(String adUserID,String adPassword)
	{
		this.adUserID=adUserID;
		this.adPassword=adPassword;
	}
	public DNUserLogin()
	{}
	/**
	 * 从SMSESSION中得到userid，可以认为该userid是登陆AD用户中目录结构的一个节点
	 * @param request
	 * @return
	 */
	private String getUserId(HttpServletRequest request) {
		String userId = null;
        //String smsession=request.getParameter("smsession");//IBM提供的
		String smsession=request.getParameter("SMSESSION");//IBM提供的
		String ltpaToken = request.getParameter("LtpaToken");
		if (ltpaToken.endsWith("==")) {
			
		} else if (ltpaToken.endsWith("=")) {
			ltpaToken = ltpaToken + "=";
		} else {
			ltpaToken = ltpaToken + "==";
		}
		
		//System.out.println("smsession in queryAD=" + smsession);
		//System.out.println("ltpaToken in queryAD=" + ltpaToken);
		if (smsession != null && smsession.length() > 0 && !"null".equalsIgnoreCase(smsession)) {
			InputStream is = null;
	        try {
	        	String	serverUrl = SystemConfig.getPropertyValue("ssoServerUrl");
				if(serverUrl!=null&&serverUrl.length()>0) {
                    ssoServerUrl=serverUrl;
                }
	    		URL url = new URL(ssoServerUrl);
	    		HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	    		conn.setDefaultUseCaches(false);
	    		conn.setUseCaches(false);
	    		conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
			    //使用SMSESSION为cookie
	    		conn.setRequestProperty("Cookie", "SMSESSION="+smsession+"; path=/; domain=.bmcc.com.cn");
	    		is = conn.getInputStream();
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
	    		String line = reader.readLine();
	    		reader.close();
	    		conn.disconnect();
			   //解析用户id
	    		//System.out.println("line=" + line);
	    		if(line!=null && line.length()>0 && line.indexOf("SSO_KM_USERID")!=-1){
	    			userId=line.substring("SSO_KM_USERID".length()+1);
	    			if("null".equals(userId.toLowerCase())) {
                        System.out.println("userId return null!");
                    }
	    		}else{
	    			System.out.println("SSO KM_USERID　error!");
	    		}		
	        } catch (MalformedURLException e) {
	        	System.out.println("token url error!");
	        } catch (IOException e) {
	        	System.out.println(e);
	        	System.out.println("read token error!");
	        }catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeIoResource(is);
			}
		}
		
		if(ltpaToken!=null && ltpaToken.length()>0 && !"null".equalsIgnoreCase(ltpaToken)){
			InputStream is = null;
			try {
				//连接超时时间
				System.setProperty("sun.net.client.defaultConnectTimeout","20000");
				//读取超时时间
				System.setProperty("sun.net.client.defaultReadTimeout","20000");
				
				//取SSO 应用保护的userToken的页面地址
				URL url = new URL("http://kmpassport.bmcc.com.cn/kmsso/index.jsp");

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDefaultUseCaches(false);
				conn.setUseCaches(false);
				conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
				//使用LtpaToken为cookie
				conn.setRequestProperty("Cookie", "LtpaToken=" + ltpaToken + "; path=/; domain=.bmcc.com.cn");
				is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
				String line = reader.readLine();
				reader.close();
				conn.disconnect();
				//解析用户id
				if (line != null && line.length() > 0
						&& line.indexOf("SSO_KM_USERID") != -1) {
					userId = line.substring("SSO_KM_USERID".length() + 1);
					if ("null".equals(userId.toLowerCase())) {
                        throw new Exception("userId return null!");
                    }
				} else {
					throw new Exception("wrong token format file!");
				}

			} catch (MalformedURLException e) {
				System.out.println("LtpaToken url error!");
			} catch (IOException e) {
				System.out.println("Read LTPA Token error!");				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeIoResource(is);
			}
		}

		return userId;
	}
	/**
	 * 链接lDAP服务器
	 * @return
	 * @throws Exception
	 */
	private DirContext getContext() { 
		Hashtable table = new Hashtable(); 
		InitialDirContext cnt=null;
		String ldapURL="";
		try {
			String host;
			host = SystemConfig.getPropertyValue("ldaphost");
	    	String port=SystemConfig.getPropertyValue("ldapport");
	    	if(host!=null&&host.length()>0) {
                ldaphost=host;
            }
	    	if(port!=null&&port.length()>0) {
                ldapport=host;
            }
			ldapURL=ldaphost + ":" + ldapport; 
			table.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); 
			table.put(Context.PROVIDER_URL, ldaphost); 
			table.put("java.naming.ldap.version", this.version); 
			table.put(Context.REFERRAL, "follow"); 
			table.put(Context.SECURITY_AUTHENTICATION, "simple"); 
			//String DN= "CN="+adUserID +","+ baseDn.trim();
			//String DN= adUserID +"@"+ baseDn.trim();
			String userID = SystemConfig.getPropertyValue("adUserID");
			if(userID!=null&&userID.length()>0) {
                this.adUserID=userID;
            }
	    	String password=SystemConfig.getPropertyValue("adPassword");
	    	if(password!=null&&password.length()>0) {
                this.adPassword=password;
            }
			table.put(Context.SECURITY_PRINCIPAL, adUserID); 
			table.put(Context.SECURITY_CREDENTIALS, adPassword); 
			
				cnt = new InitialDirContext(table);
			}catch (NamingException e) {
				// TODO Auto-generated catch block
				 System.out.println("认证失败"); 	            
	             System.out.println("--url="+ldapURL);
				 e.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
		/*Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");//指定JNDI服务提供程序中工厂类的名称,该工厂负责为其服务创建一个合适的InitialContext 对象
		use simple authentication mechanism
		props.put(Context.SECURITY_AUTHENTICATION, "simple"); 
		props.put(Context.SECURITY_CREDENTIALS, adPassword);
		//String DN= "CN="+adUserID +","+ baseDn.trim();
		String DN= adUserID +"@"+ baseDn.trim();
		props.put(Context.SECURITY_PRINCIPAL, DN);
		props.put(Context.PROVIDER_URL, ldaphost.trim());
		InitialDirContext cnt = new InitialDirContext(props);//建立服务器的连接，如果链接上了则true，如果链接不上则false
*/		return cnt; 
	} 
	public String getLdaphost() {
		return ldaphost;
	}
	public void setLdaphost(String ldaphost) {
		this.ldaphost = ldaphost;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSsoServerUrl() {
		return ssoServerUrl;
	}
	public void setSsoServerUrl(String ssoServerUrl) {
		this.ssoServerUrl = ssoServerUrl;
	}
	public String getBaseDn() {
		return baseDn;
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	/**
	 * 通过从SMSESSION中得到userid，登陆到AD用户中去取的员工编号
	 * @param userId
	 * @return
	 */
	public  String getMisId(HttpServletRequest request){
		String misId = null;
		String userId=getUserId(request);		
		if(userId!=null&&userId.length()>0)
		{
			if(this.getTestName()!=null&&this.getTestName().length()>0) {
                userId=this.getTestName();
            }
		}else
		{
			System.out.println("not find userId");
			return "";
		}
		
		if ("hrtest1".equalsIgnoreCase(userId)) {
			return "T"+"27011848";//徐欣
		}
		/*else if (userId.equalsIgnoreCase("hrtest2")) {
			return "T"+"27000043";//王岩
		} else if (userId.equalsIgnoreCase("hrtest3")) {
			return "T"+"27000053";//高松
		} else if (userId.equalsIgnoreCase("hrtest4")) {
			return "T"+"27000068";//王文明
		}else if (userId.equalsIgnoreCase("hrtest5")) {
			return "T"+"27000080";//刘向力
		}*/
		
//		String[] atts = {"cn", "sAMAccountName", "DISPLAYName", "Description", "userPassword", "objectSID"}; 
		String[] atts = {"userPrincipalName", "displayName", "department", "title","mobile","postOfficeBox"};
		String filter =  "(&(objectClass=user)(userPrincipalName=" + userId + "@bmcc.com.cn))";
//		String name = "李增辉";
//		String filter =  "(&(objectClass=user)(displayName=" + name + "))";
		NamingEnumeration namingEnum = null; 
		try {
			DirContext cnt = this.getContext(); 
			if(cnt==null) {
                return "";
            }
			
			SearchControls searchCons = new SearchControls();  
			//searchCons.setSearchScope(SearchControls.SUBTREE_SCOPE); 
			searchCons.setSearchScope(2);
			searchCons.setCountLimit(0); 
			searchCons.setTimeLimit(0); 
			searchCons.setReturningAttributes(atts); 
			String rootorg=SystemConfig.getProperty("rootorg");
			if(rootorg!=null&&rootorg.length()>0) {
                baseDn=rootorg;
            }
			namingEnum = cnt.search(baseDn, filter, searchCons); 
			if (namingEnum != null && namingEnum.hasMore()) 
			{ 
				SearchResult result = (SearchResult) namingEnum.next(); 				
				if (result.getAttributes().size() == 0) 
				{ 
					return null;
				} 	
				for (NamingEnumeration namingEnum_1 = result.getAttributes().getAll(); namingEnum_1.hasMoreElements(); ) 
				{ 
					Attribute attribute = (Attribute) namingEnum_1.next(); 
					String attID = attribute.getID(); 
					//System.out.println("attID=" + attID);
					
					for (NamingEnumeration namingEnum_2 = attribute.getAll(); namingEnum_2.hasMoreElements(); ) 
					{ 
						Object attValue = namingEnum_2.nextElement(); 
						//System.out.println("attValue=" + attValue);
						if ("postOfficeBox".equalsIgnoreCase(attID)) {
							misId = (String) attValue;			
						}
				
					} 
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("ad error!");
		}		
		if(misId!=null&&misId.length()>0) {
            misId="T"+misId;//因为用户管理用户用户名开头必须是字母不能是数字
        }
		return misId;
	}
		
}
