package com.hjsj.hrms.interfaces.adauthenticate;

public class SearchAuthUser {

	/**
	 * @param args
	 */
	public boolean AuthUser(String validatestr)
	{
		String filterstr;
	    filterstr="(&(objectclass=CFTEUserObj)(cn="
							+  validatestr + "*))";
        System.out.println("filter " + filterstr);
		// TODO Auto-generated method stub
        boolean userResults=false;
		try
		{
			//AdLdap ld = new AdLdap("192.168.2.228", 389);
			//ld.authenticate("cn=Administrator,cn=users,dc=ldap,dc=cnsi,dc=com",
			//		"123$%^qwe");
			//String base = "cn=users,cn=cfte,dc=ldap,dc=cnsi,dc=com";
			//String filter = "(&(objectclass=cfteuserobj)(cn=1*))";

			//CNSISearchUsers userResults = ld.searchCFTEUsers( base, "(&(objectclass=CFTEUserObj)(cn=0*))" );
			//CNSISearchUsers userResults = ld.searchAllCFTEUsers(base);
			 //userResults = ld.searchUsers(base, filterstr);
			// ld.disConnect();
			
		} catch (Exception e)
		{
			System.out.println(e);
		}
		 return userResults;
	}
   /*
    * @fieldname验证字段用户对应的属性名
    * @fieldpw验证的密码对应的属性名
    * @username 用户名
    * @password密码
    * */
    public boolean getAuthUser(String fieldname,String fieldpw,String username,String password)
    {
        return ADAuthenticate.getInstance(username,password).getAuthUser(fieldname,fieldpw);   	
    }
    /*
     * @fieldname验证字段用户对应的属性名
     * @fieldpw验证的密码对应的属性名
     * @username 用户名
     * @password密码
     * */
    public boolean getAuthUser630(String fieldname,String fieldpw,String username,String password)
    {
    	String filterstr="(&(objectclass=CFTEUserObj)(cn="                  //过滤字符串。按照属性名过滤
			+  username + "*))";
        String[] attributes={ "cn", "cfBirthday", "cfBusiness", "cfCheckInDate", "cfDegree",    //过滤查询的所有属性        		
        		"cfDepartment", "cfName", "cfID",
        		"cfEmployeeNumber", "cfHoldPostPeriod", "cfInDutyPeriod",
        		"cfIsUsingFlag", "cfLocation", "cfRetireDate", "cfSex", "cfStation",
        		"cfTelephoneNumber", "cfTimeStamp", "cfTitleOfPost", "cfTopDegree",
        		"cfTypeOfWork", "cfUniPeriod", "cfWorkPeriod", "cfMail" };
        return ADAuthenticate.getInstance(username,password).getAuthUser(attributes,filterstr,fieldname,fieldpw);   	
    }
    public boolean getAuthUser630(String username,String password)
    {
    	String filterstr="(&(objectclass=CFTEUserObj)(cn="                  //过滤字符串。按照属性名过滤
			+  username + "*))";
        String[] attributes={ "cn", "cfBirthday", "cfBusiness", "cfCheckInDate", "cfDegree",    //过滤查询的所有属性        		
        		"cfDepartment", "cfName", "cfID",
        		"cfEmployeeNumber", "cfHoldPostPeriod", "cfInDutyPeriod",
        		"cfIsUsingFlag", "cfLocation", "cfRetireDate", "cfSex", "cfStation",
        		"cfTelephoneNumber", "cfTimeStamp", "cfTitleOfPost", "cfTopDegree",
        		"cfTypeOfWork", "cfUniPeriod", "cfWorkPeriod", "cfMail" };
        return ADAuthenticate.getInstance(username,password).getAuthUser(attributes,filterstr);   	
    }
}

