package com.hjsj.hrms.interfaces.adauthenticate;

import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.novell.ldap.*;
import com.novell.ldap.util.Base64;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;



public class ADAuthenticate implements ADConstant{
    // the user's name
	private String name;                                         //验证用户
	// the user's password;	
	private String password;                                     //验证的密码
	// singleton
	//private String ladpusername;                                 //登陆ladp的用户
	private String ldapbase;
	private String loginDN;
	private String userDN;
	private static ADAuthenticate myInstance = null;
	//初始化默认参数
	private String[] params = new String[]{"127.0.0.1",                //域服务器IP
										   "389",                      //端口
										   "yksj",                     //AD名称
										   "com",                      //
										   "Administrator",            //登陆LADP的验证用户
										   "",                         //登陆LADP的验证密码
										   "Users",                    //默认的Users用户组
										   "simple",
										   "1"
										  };	
    private  ADAuthenticate(){		
		super();
	}
	/**
	 * private the construct
	 * @param name
	 * @param password
	 */
	private ADAuthenticate(String name,
						  String password){
		setName(name);
		this.password = password;
	}
	/**
	 * Get the singleton
	 * @param name
	 * @param password
	 * @return
	 */
	
	public static ADAuthenticate getInstance(){
		return getInstance("","");
	}
	public static ADAuthenticate getInstance(String name, String password){
		if(myInstance == null) {
			myInstance = new ADAuthenticate(name, password);
			myInstance.init();                              //初始化域的所有参数
		}
		else{
			myInstance.setName(name);
			myInstance.setPassword(password);
		}
		return myInstance;
		
	}
	/**
	 * 从数据库读取初始化系统设置参数
	 *
	 */
	public void init() {
		Connection conn=null;
		try{
			 conn=AdminDb.getConnection();	
			 RecordVo constant_vo=ConstantParamter.getRealConstantVo("ADPARAMETER",conn);     
			 if(constant_vo!=null)
			 {
				String ad=constant_vo.getString("str_value"); 
				saveParam(ad);
			 }			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try{
				if(conn!=null)
					conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * Set the user's name which to authenticate
	 * @param name
	 * @设置初始化所有用户名.登陆ladp用户等参数
	 */
	public void setName(String name){
		String domains=get(DOMAIN);
		if(domains.indexOf(".")==-1)
		{
			this.name = name;
			//this.loginDN=get(USERNAME)+ "@" +  get(DOMAIN_NAME) + "." + get(DOMAIN);
			this.loginDN="CN=" + get(USERNAME) + ",CN="+get(GROUP)
			+",DC="+get(DOMAIN_NAME)+",DC="+get(DOMAIN);
			this.ldapbase="CN="+get(GROUP)
				+",DC="+get(DOMAIN_NAME)+",DC="+get(DOMAIN);
			this.userDN="cn=" + name + ",ou=" + get(GROUP) + "o=" + get(DOMAIN_NAME);
		}
		else
		{
			this.name =name;
			this.loginDN="CN=" + get(USERNAME) + ",CN="+get(GROUP) +
			",DC=" + get(DOMAIN_NAME)+",DC="+ domains.substring(0,domains.indexOf(".")) +",DC="+ domains.substring(domains.indexOf(".")+ 1);
			this.ldapbase="CN="+get(GROUP) +
			",DC=" + get(DOMAIN_NAME)+",DC="+ domains.substring(0,domains.indexOf(".")) +",DC="+ domains.substring(domains.indexOf(".")+ 1);
			//this.loginDN=get(USERNAME)+ "@" +  get(DOMAIN_NAME) + "." + get(DOMAIN);
			this.userDN="cn=" + name + ",ou=" + get(GROUP) + ",o=" + get(DOMAIN_NAME);
		}
	}	
	/**
	 * the user's password which to authenticate
	 * @param password
	 */
	public void setPassword(String password){
		this.password = password;
	}
	
	/**
	 * 获取参数
	 * @param field
	 * @return
	 */	
	public String get(int field) {
		String retVal = "";		
		if(field < params.length){
			retVal = params[field];
   	    }
		return retVal;
	}	
	public void saveParam(String params){
		//System.out.println(" ddd" + params);
		StringTokenizer token=new StringTokenizer(params,"|");
		if(token.hasMoreTokens())
			this.params[0]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[1]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[2]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[3]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[4]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[5]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[6]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[7]=token.nextToken();
		if(token.hasMoreTokens())
			this.params[8]=token.nextToken();
	}
	public void getAuthUser(String validatestr)
	{

        int ldapPort = LDAPConnection.DEFAULT_PORT;
        int searchScope = LDAPConnection.SCOPE_ONE;
        int ldapVersion  = LDAPConnection.LDAP_V3;
        String ldapHost = get(HOST);                      //服务器地址
        String loginDN  = get(USERNAME);                  //登陆ladp用户
        String password =get(USERPASSWORD);               //登陆ladp密码
        String searchBase = this.ldapbase;                //基域字符串
        String searchFilter = validatestr;                //过滤字符串
        LDAPConnection lc = new LDAPConnection();

        try {
            // connect to the server
            lc.connect( ldapHost, ldapPort );             //连接AD服务器
            // bind to the server
            lc.bind( ldapVersion, loginDN, password.getBytes("UTF8") );        //登陆验证

            LDAPSearchResults searchResults =                                  //过滤搜索节点
                lc.search(  searchBase,
                            searchScope,
                            searchFilter,
                            null,          // return all attributes
                            false);        // return attrs and values

            /* To print out the search results,
             *   -- The first while loop goes through all the entries
             *   -- The second while loop goes through all the attributes
             *   -- The third while loop goes through all the attribute values
             */
            while ( searchResults.hasMore()) {
                LDAPEntry nextEntry = null;
                try {
                    nextEntry = searchResults.next();
                }
                catch(LDAPException e) {
                    System.out.println("Error: " + e.toString());

                    // Exception is thrown, go for next entry
                    continue;
                }

                System.out.println("\n" + nextEntry.getDN());
                System.out.println("  Attributes: ");

                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
                Iterator allAttributes = attributeSet.iterator();

                while(allAttributes.hasNext()) {
                    LDAPAttribute attribute =
                                (LDAPAttribute)allAttributes.next();
                    String attributeName = attribute.getName();

                    System.out.println("    " + attributeName);

                    Enumeration allValues = attribute.getStringValues();

                    if( allValues != null) {
                        while(allValues.hasMoreElements()) {
                            String Value = (String) allValues.nextElement();
                            if (Base64.isLDIFSafe(Value)) {
                                // is printable
                                System.out.println("      " + Value);
                            }
                            else {
                                // base64 encode and then print out
                                Value = Base64.encode(Value.getBytes());
                                System.out.println("      " + Value);
                            }
                        }
                    }
                }
            }
            // disconnect with the server
            lc.disconnect();
        }
        catch( LDAPException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        catch( UnsupportedEncodingException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        System.exit(0);
    
	}
	public boolean getAuthUser(String validatestr,String fieldname,String fieldpw)
	{
        boolean isLogin=false;
        int ldapPort = LDAPConnection.DEFAULT_PORT;
        int ldapVersion  = LDAPConnection.LDAP_V3;
        String ldapHost = get(HOST);                      //服务器地址
        String password =get(USERPASSWORD);               //登陆ladp密码
        LDAPConnection lc = new LDAPConnection();

        try {
            // connect to the server
            lc.connect( ldapHost, ldapPort );
            // bind to the server
            lc.bind( ldapVersion, this.loginDN, password.getBytes("UTF8"));
          
            String userDN="cn=" + this.name + ",ou=Employee,o=" + get(DOMAIN_NAME);
            isLogin=CheckBindUser.GetBindInfo( lc, userDN, this.password );
            // disconnect with the server
            lc.disconnect();
        }
        catch( LDAPException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        catch( UnsupportedEncodingException e ) {
            System.out.println( "Error: " + e.toString() );
        }
      return isLogin;
	}
	public boolean getAuthUser(String fieldname,String fieldpw)
	{
        boolean isLogin=false;
        int ldapPort = LDAPConnection.DEFAULT_PORT;
        int ldapVersion  = LDAPConnection.LDAP_V3;
        String ldapHost = get(HOST);                      //服务器地址
        String password =get(USERPASSWORD);               //登陆ladp密码
        LDAPConnection lc = new LDAPConnection();

        try {
            // connect to the server
            lc.connect( ldapHost, ldapPort );
            // bind to the server
            System.out.println("login LoginDN" + this.loginDN);
            System.out.println("login password.getBytes(\"UTF8\")" + this.password.getBytes("UTF8"));
            lc.bind( ldapVersion, this.loginDN, password.getBytes("UTF8"));
          
            //String userDN="cn=" + this.name + ",ou=users,o=" + get(DOMAIN_NAME);
            System.out.println("login userDN" + this.userDN);
            System.out.println("user password" + this.password);
            isLogin=CheckBindUser.GetBindInfo( lc,  this.userDN, this.password );
            // disconnect with the server
            lc.disconnect();
        }
        catch( LDAPException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        catch( UnsupportedEncodingException e ) {
            System.out.println( "Error: " + e.toString() );
        }
      return isLogin;
	}
	
    // 索验证User
	public boolean getAuthUser(String[] attributes,String validatestr,String fieldname,String fieldpw) 
	{
		
		Enumeration searchResults = null;		
		LDAPEntry findEntry = null;
        boolean isName=false;
        boolean isPw=false;
		try
		{
	        int ldapPort = LDAPConnection.DEFAULT_PORT;
	        int searchScope = LDAPConnection.SCOPE_ONE;
	        int ldapVersion  = LDAPConnection.LDAP_V3;
	        String ldapHost = get(HOST);                      //服务器地址
	        String loginDN  = get(USERNAME);                  //登陆ladp用户
	        String password =get(USERPASSWORD);               //登陆ladp密码
	        String searchBase = this.ldapbase;                //基域字符串
	        String searchFilter = validatestr;                //过滤字符串
	        LDAPConnection lc = new LDAPConnection();

		    // connect to the server
	        lc.connect( ldapHost, ldapPort );
	        // bind to the server
	        lc.bind( ldapVersion, this.loginDN, password.getBytes("UTF8") );

  
			String getAttrs[] =attributes;			
			LDAPSearchConstraints searchConstraints = lc.getSearchConstraints();
			searchConstraints.setMaxResults( 5000 );
			LDAPSearchResults res = lc.search(searchBase, searchScope,
					searchFilter, getAttrs, false, searchConstraints);
			
			//System.out.println(res.getCount());
			while (res.hasMore())
			{
				try
				{
					findEntry = res.next();
				} catch (LDAPException ee)
				{
					System.out.println("Error: " + ee.toString());
				}
				 /*Print the DN of the entry.*/ 
				//System.out.println( findEntry.getDN() );
				 /*Get the attributes of the entry  */
				//CFTEUser userEntry = new CFTEUser( findEntry.getDN() );
				LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
				 Iterator allAttributes = findAttrs.iterator();
				//System.out.println( "\tAttributes: " );

				/* Loop on attributes*/ 
				while (allAttributes.hasNext())
				{
			           LDAPAttribute attribute =
			                        (LDAPAttribute)allAttributes.next();
			            String attributeName = attribute.getName();
			           if(fieldname.equalsIgnoreCase(attributeName))
			           {
			        	   String returnValue="";
			        	   System.out.println("    " + attributeName);
			               Enumeration allValues = attribute.getStringValues();
			
			               if( allValues != null) {
			                   while(allValues.hasMoreElements()) {
			                       String Value = (String) allValues.nextElement();
			                       if (Base64.isLDIFSafe(Value)) {
			                           // is printable
			                    	   returnValue+=Value.trim();
			                           System.out.println("      " + Value);
			                       }
			                       else {
			                           // base64 encode and then print out
			                           Value = Base64.encode(Value.getBytes());
			                           returnValue+=Value.trim();
			                           System.out.println("      " + Value);
			                       }
			                   }
			               }
			               if(returnValue.equals(this.name))
	                    	   isName=true;
			           } 
			           if(fieldpw.equalsIgnoreCase(attributeName))
			           {
			        	   String returnValue="";
			        	   System.out.println("    " + attributeName);
			               Enumeration allValues = attribute.getStringValues();
			
			               if( allValues != null) {
			                   while(allValues.hasMoreElements()) {
			                       String Value = (String) allValues.nextElement();
			                       if (Base64.isLDIFSafe(Value)) {
			                           // is printable
			                    	   returnValue+=Value.trim();
			                           System.out.println("      " + Value);
			                       }
			                       else {
			                           // base64 encode and then print out
			                           Value = Base64.encode(Value.getBytes());
			                           returnValue+=Value.trim();
			                           System.out.println("      " + Value);
			                       }
			                   }
			               }
			               if(returnValue.equals(this.password))
	                    	   isPw=true;
			           }     
			        }
				  if(isName && isPw)
			            return true;
			        else
			        	return false;
			}
			
		} catch (LDAPException e)
		{
			  System.out.println(e.toString() );
		}
	    catch( UnsupportedEncodingException e ) {
	           System.out.println( "Error: " + e.toString() );
	    }
		//return searchResults;
	    if(isName && isPw)
            return true;
        else
        	return false;
	}
	 // 索验证User
	public boolean getAuthUser(String[] attributes,String validatestr) 
	{
		LDAPEntry findEntry = null;
        boolean isName=false;
        boolean isPw=false;
		try
		{
	        int ldapPort = LDAPConnection.DEFAULT_PORT;
	        int searchScope = LDAPConnection.SCOPE_ONE;
	        int ldapVersion  = LDAPConnection.LDAP_V3;
	        String ldapHost = get(HOST);                      //服务器地址
	        String password =get(USERPASSWORD);               //登陆ladp密码
	        String searchBase = this.ldapbase;                //基域字符串
	        String searchFilter = validatestr;                //过滤字符串
	        LDAPConnection lc = new LDAPConnection();

		    // connect to the server
	        lc.connect( ldapHost, ldapPort );
	        // bind to the server
	        lc.bind( ldapVersion, this.loginDN, password.getBytes("UTF8") );

  
			String getAttrs[] =attributes;			
			LDAPSearchConstraints searchConstraints = lc.getSearchConstraints();
			searchConstraints.setMaxResults( 5000 );
			LDAPSearchResults res = lc.search(searchBase, searchScope,
					searchFilter, getAttrs, false, searchConstraints);
			
			//System.out.println(res.getCount());
			while (res.hasMore())
			{
				try
				{
					findEntry = res.next();
				} catch (LDAPException ee)
				{
					System.out.println("Error: " + ee.toString());
				}
				 /*Print the DN of the entry.*/ 
				//System.out.println( findEntry.getDN() );
				 /*Get the attributes of the entry  */
				//CFTEUser userEntry = new CFTEUser( findEntry.getDN() );
				 LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
				 Iterator allAttributes = findAttrs.iterator();
				//System.out.println( "\tAttributes: " );

				/* Loop on attributes*/ 
				while (allAttributes.hasNext())
				{
			           LDAPAttribute attribute =
			                        (LDAPAttribute)allAttributes.next();
			           String attributeName = attribute.getName();			            
			    }
		       return true;
			}
			
		} catch (LDAPException e)
		{
			  System.out.println(e.toString() );
		}
	    catch( UnsupportedEncodingException e ) {
	           System.out.println( "Error: " + e.toString() );
	    }
		//return searchResults;
	    if(isName && isPw)
            return true;
        else
        	return false;
	}
	
	/*
	 * test  方法
	 * */
	public void getADInfo(){
		    String host = get(HOST);
	        int port = Integer.parseInt(get(PORT));
	        String authid = get(USERNAME);
	        String authpw = get(USERPASSWORD);
	        String base = "dc=" + get(DOMAIN_NAME) + ",dc=" + get(DOMAIN);
	        String filter = "name=w*";
	        String[] ATTRS = {"name"};	       
	        int status = -1;
	        LDAPConnection ld = new LDAPConnection();
	          
	        System.out.println(host + port + authid + authpw + base);
	        try {
	            int ldapPort = LDAPConnection.DEFAULT_PORT;
	            //////int searchScope = LDAPConnection.SCOPE_ONE;
	            int ldapVersion  = LDAPConnection.LDAP_V3;
	            // Connect to server and authenticate
	       ///     ld.connect( host, port, authid, authpw);
	            // connect to the server
	            ld.connect( get(HOST), ldapPort );
	            // bind to the server
	            ld.bind( ldapVersion, this.name, password.getBytes("UTF8") );
	            //LDAPAttributeSet m_attrs = new LDAPAttributeSet();
	            //String DN = "ou=People,o=yksj.com";
	            //DN = "uid=" + "wlh" + "," + DN;
	            //LDAPAttribute attr = new LDAPAttribute("xry", "ILoveYou");
	            //m_attrs.add(attr);
	            //DN="CN=wl,CN=Users,DC=yksj,DC=com";
	            //DN="yksj.com";
	            //LDAPEntry en=new LDAPEntry(DN,m_attrs);
	            //ld.add(en);
	            System.out.println( "Search filter=" + filter );
	            System.out.println("<<----------------search--------------->>");
	            LDAPSearchResults res = ld.search(base,
	            		     LDAPConnection.SCOPE_SUB,
	                                            filter,
	                                             ATTRS,
	                                             false );	          
	            // Loop on results until complete
	            while ( res.hasMore()) {
	                try {
	                    // Next directory entry
	                    LDAPEntry entry = res.next();
	                    System.out.println("test by tim");
	                    prettyPrint( entry, ATTRS );	                    
	                    status = 0;	               
	                } catch ( LDAPException e ) {
	                    //System.out.println( e.toString() );
	                    continue;
	                }
	            }
	            ld.disconnect();
	            
	        } catch( LDAPException e ) {
	          // System.out.println( e.toString() );
	        }
	        catch( UnsupportedEncodingException e ) {
	            System.out.println( "Error: " + e.toString() );
	        }
	        // Done, so disconnect
	        if ( (ld != null) && ld.isConnected() ) {
	            try {
	                ld.disconnect();
	            } catch ( LDAPException e ) {
	                //System.out.println( e.toString() );
	            }
	        }
	        System.out.println("test");
	       // System.exit( status );
	
	}
	
	public static void prettyPrint(LDAPEntry entry, String[] attrs) {
		
		System.out.println("DN: " + entry.getDN());
		for (int i = 0; i < attrs.length; i++) {
			LDAPAttribute attr = entry.getAttribute(attrs[i]);
			if (attr == null) {
				System.out.println(attrs[i] + " not present");
				continue;
			}
			Enumeration enumVals = attr.getStringValues();

			boolean hasVals = false;
			while ((enumVals != null) && enumVals.hasMoreElements()) {

				String val = (String) enumVals.nextElement();
				// 中文亂碼問題
				String s;
				try {
					s = new String(val.getBytes("Unicode"), "BIG5");
					System.out.println("转码后的值 : " + s);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(attrs[i] + ": " + val);
				hasVals = true;
			}
			if (!hasVals) {
				System.out.println(attrs[i] + " has no values");
			}
		}
		System.out.println("-------------------");
	}
/*	public boolean searchUsers(String base, String filter) throws LDAPException
	{
		Enumeration searchResults = null;
		if (m_ld == null || !m_ld.isConnected())
		{
			// 抛出异常
			throw new LDAPException();
		}

		LDAPEntry findEntry = null;

		try
		{
			String getAttrs[] =
			{ "cn", "cfBirthday", "cfBusiness", "cfCheckInDate", "cfDegree",
					"cfDepartment", "cfName", "cfID",
					"cfEmployeeNumber", "cfHoldPostPeriod", "cfInDutyPeriod",
					"cfIsUsingFlag", "cfLocation", "cfRetireDate", "cfSex", "cfStation",
					"cfTelephoneNumber", "cfTimeStamp", "cfTitleOfPost", "cfTopDegree",
					"cfTypeOfWork", "cfUniPeriod", "cfWorkPeriod", "cfMail" };

			LDAPSearchConstraints searchConstraints = m_ld.getSearchConstraints();
			searchConstraints.setMaxResults( 5000 );
			LDAPSearchResults res = m_ld.search(base, LDAPv2.SCOPE_SUB,
					filter, getAttrs, false, searchConstraints);
			
			//System.out.println(res.getCount());
			while (res.hasMoreElements())
			{
				try
				{
					findEntry = res.next();
				} catch (LDAPException ee)
				{
					System.out.println("Error: " + ee.toString());
				}
				 Print the DN of the entry. 
				//System.out.println( findEntry.getDN() );
				 Get the attributes of the entry  
				//CFTEUser userEntry = new CFTEUser( findEntry.getDN() );
				LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
				Enumeration enumAttrs = findAttrs.getAttributes();
				//System.out.println( "\tAttributes: " );

				 Loop on attributes 
				while (enumAttrs.hasMoreElements())
				{
					LDAPAttribute anAttr = (LDAPAttribute) enumAttrs
							.nextElement();
					String attrName = anAttr.getName();
					//System.out.println( "\t\t" + attrName );
					 Loop on values for this attribute 
					Enumeration enumVals = anAttr.getStringValues();
					if (enumVals != null)
					{
						while (enumVals.hasMoreElements())
						{
							String aVal = (String) enumVals.nextElement();
							//System.out.println( "\t\t\t" + aVal );
							//userEntry.setInfo(attrName, aVal);
						}
					}
				}
			   return true;
			}
			
		} catch (LDAPException e)
		{
			throw new LDAPException();
		}
		//return searchResults;
		return false;
	}*/
	    
	/**
	 * 验证方法
	 * @return  true /false
	 */
	public boolean validate(){
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,SUNLDAPCONTEXT);
		env.put(Context.PROVIDER_URL, "ldap://"+get(HOST)+":"+get(PORT));
		env.put(Context.SECURITY_AUTHENTICATION, get(AUTH_TYPE));//
		env.put(Context.SECURITY_PRINCIPAL,this.loginDN);
		String pw=get(USERPASSWORD);
		if("null".equals(pw))
			pw="";
		env.put(Context.SECURITY_CREDENTIALS, pw);
		System.out.println(get(USERPASSWORD));
		try{
			/*String dn = "DC="+get(DOMAIN_NAME)+",DC="+get(DOMAIN);
			String searchFilter = "&(objectclass=CFTEUserObj)(cn="
					+ "com" + ")";
			String getAttrs[] =
			{ "cn", "cfBirthday", "cfBusiness", "cfCheckInDate", "cfDegree",
					"cfDepartment", "cfName", "cfID",
					"cfEmployeeNumber", "cfHoldPostPeriod", "cfInDutyPeriod",
					"cfIsUsingFlag", "cfLocation", "cfRetireDate", "cfSex", "cfStation",
					"cfTelephoneNumber", "cfTimeStamp", "cfTitleOfPost", "cfTopDegree",
					"cfTypeOfWork", "cfUniPeriod", "cfWorkPeriod", "cfMail" };*/
			//LDAPConnection s=new LDAPConnection();
			//LDAPSearchResults res = s.search(dn, LDAPv2.SCOPE_BASE,
			//searchFilter, getAttrs, false);
			DirContext ctx = new InitialDirContext(env);
			//Attributes as =ctx.getAttributes("CN=wlh,CN=Users" + "," +  "DC="+get(DOMAIN_NAME)+",DC="+get(DOMAIN));
			/*if("1".equals(get1(validetype1)))
			{
				Attributes a =ctx.getAttributes(name);
		          if(a.get("CN") ==  null){
					return false;
			    }
			    return true;
			}	*/		
				String searchBase="";
				String domains=get(DOMAIN);
				if(domains.indexOf(".")==-1){
					searchBase="DC="+get(DOMAIN_NAME)+",DC="+get(DOMAIN);
				}
				else
				{
					searchBase=",DC="+get(DOMAIN_NAME) + "DC="+ domains.substring(0,domains.indexOf(".")) +",DC="+  domains.substring(domains.indexOf(".")+1);
				}
				//searchBase="DC="+get(DOMAIN_NAME)+",DC="+get(DOMAIN);
				String searchContents="telephonenumber=" + "*";
				Attributes matchattribs = new BasicAttributes(true);
				matchattribs.put(new BasicAttribute("telephonenumber", "13121941290"));
                //search for objects with those matching attributes
				//NamingEnumeration answer = ctx.search("ou=People,o=javacourses.com", matchattribs);
			    //	while (answer.hasMore()) {
			        // SearchResult sr = (SearchResult)answer.next();
				    // print the results you need
				//}
				SearchControls constraints=new SearchControls();
				constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
				NamingEnumeration results=ctx.search(searchBase,searchContents,constraints);
				
				if(results!=null && results.hasMoreElements())
				{
					SearchResult sr=(SearchResult)results.next();				    
					Hashtable envsub = new Hashtable();
					envsub.put(Context.INITIAL_CONTEXT_FACTORY,SUNLDAPCONTEXT);
					envsub.put(Context.PROVIDER_URL, "ldap://"+get(HOST)+":"+get(PORT));
					envsub.put(Context.SECURITY_AUTHENTICATION, get(AUTH_TYPE));
					envsub.put(Context.SECURITY_PRINCIPAL,sr.getName() + "," +  searchBase);
					envsub.put(Context.SECURITY_CREDENTIALS, password);
					ctx.close();
					DirContext ctxsub = new InitialDirContext(envsub);	
					Attributes a =ctxsub.getAttributes(name);					
					if(a.get("CN")==null)
					{
					 return false;
					} 
					return true;	
				}				
		}
		catch(AuthenticationException ae){
			//return false;
			//ae.printStackTrace();
	
		}
		catch(NameNotFoundException ne){
			//return false;
			//ne.printStackTrace();
		}
		catch(Exception e){				
			e.printStackTrace();
			//return false;
		}	
		return false;	

	}
}
