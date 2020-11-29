<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@page import="com.hrms.frame.codec.SafeCode,
				com.hrms.struts.constant.SystemConfig"%>
<%@page import="org.apache.commons.lang.StringUtils"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import="com.hjsj.hrms.utils.*"%>

<html>
<head>
   <title></title>
   <script language="JavaScript" src="/js/rec.js"></script>
</head>
<%
	  	String bosflag="hcm";
	  	String username="";
	  	String password="";
	  	int ss=0;
	  	boolean flag=false;
	    /**获得所有COOKIE*/
	    Cookie[] cookies=request.getCookies();
	    /**要找单独的Cookie就要用循环遍历*/
	    Cookie cookie=null;
	    Cookie userkie=null;
	    Cookie pwdkie=null;
	  
	
	    if(cookies!=null)
	    {
	      /**
	       *第一步先通过cookies判断
	       *通过cookies传递登录用户及密码
	       */
	      for (int i = 0; i < cookies.length ; i++)
	      {
	        if (cookies[i].getName().equals("RecordName"))
	        {
	        	userkie=cookies[i];
	        	flag=true;
	        }     
	        if (cookies[i].getName().equals("RecordPwd"))
	        {
	        	pwdkie=cookies[i];
	            flag=true;
	        }     	        	    	      	
	      }
	      /**用户名和密码*/
	      if(userkie!=null)
	      {
	         username=userkie.getValue();
	         username=SafeCode.decode(username);
	      }
	      if(pwdkie!=null)
	      {
	         password=pwdkie.getValue();	
	         password=SafeCode.decode(password);	             	
	      }

	      /**登录门户*/
	      for (int i = 0; i < cookies.length ; i++)
	      {
     	    //cookie=  cookies[i];
	        if (cookies[i].getName().equals("bosflag"))
	        {
	          cookie = cookies[i];
	          break;
	        }
	        cookie=null;
	      }

	    }	 
	    if(cookie!=null)
	    {
	        bosflag=cookie.getValue();	    	
	    }
	     /**
	      *如果cookies未找到登录令牌,则第二步网页参数中是否传递令牌
	      */ 
	      if(!flag)
	      {
	    	    //liuyz 17531 没有设置用户帐号和密码，通过邮件打开报500错误
	    	    String _bosflag = SystemConfig.getPropertyValue("bosflag");
		        if(_bosflag == null || _bosflag.length() == 0)
		            _bosflag = "hl";   
		        String _logonaction="";
    			if(_bosflag.equalsIgnoreCase("ul"))
		        { 
		        	_logonaction="/templates/index/UserLogon.do";   
		        }else if(bosflag.equalsIgnoreCase("el"))
		        { 
		        	_logonaction="/templates/index/employLogon.do";          	
		        }                	
		        else if(bosflag.equalsIgnoreCase("hl"))
		        {  
		        	_logonaction="/templates/index/hrlogon.do";           	
		        }
		        else if(bosflag.equalsIgnoreCase("hl4"))
		        { 
		        	_logonaction="/templates/index/hrlogon4.do";           	
		        }
		        else if(bosflag.equalsIgnoreCase("el4"))
		        { 
		        	_logonaction="/templates/index/emlogon4.do";          	
		        }                     
		        else if(bosflag.equalsIgnoreCase("pl"))
		        { 
		        	_logonaction="/templates/index/perlogon.do";         	
		        }  
		        else if(bosflag.equalsIgnoreCase("bi"))
		        { 
		        	_logonaction="/templates/index/bilogon.do";         	
		        }
		        else if(bosflag.equalsIgnoreCase("il"))
		        {  
		        	_logonaction="/templates/index/ilearning.do";         	
		        }  
		        else if(bosflag.equalsIgnoreCase("epmgw"))
		        {   
		        	_logonaction="/templates/index/epmlogon.do";         	
		        }                    
		        else if(bosflag.equalsIgnoreCase("mp"))
		        {   
		        	_logonaction="/phone-app/index.do";         	
		        }else if(bosflag.equalsIgnoreCase("hcm"))
		        {
		        	_logonaction="/templates/index/hcmlogon.do";          
		        }                                     
		        else
		        { 
		        	_logonaction="/templates/index/hcmlogon.do";         	
		        }   
	      		String etoken=(String)session.getAttribute("etoken");
	      		if(etoken!=null&&etoken.length()!=0)
	      		{
	      			String[] tokens=StringUtils.split(etoken,",");
	      			if(tokens.length==0||tokens==null)//liuyz 17531 没有设置用户帐号和密码，通过邮件打开报500错误
	      			{
	      				request.getRequestDispatcher(_logonaction+"?logon.x=link&username=''&password=''&validatepwd=true").forward(request,response);
	      				return;
	      			}
	      			username=tokens[0];
	      			if(tokens.length>1)
	      				password=tokens[1];
	      			flag=true;
	      			password = password == null ? "": password;
					/****zzk 密码转明文*****/
					password=PubFunc.getConvertPassWord(password);
	      			session.removeAttribute("etoken");
	      			//String hh=(String)session.getAttribute("ehr_apply_path");
	      			String validatepwd=(String)session.getAttribute("validatepwd");
	      			if(validatepwd==null)
	      			   validatepwd="true";
	      			//密码中特殊号地址栏传值中会丢失，导致登录失败，此处转一下。 guodd 2018-01-31
	      			password = URLEncoder.encode(password,"GBK");//中间件编码格式为gbk 这里要一致  
	      			request.getRequestDispatcher(_logonaction+"?logon.x=query&username="+username+"&password="+password+"&validatepwd="+validatepwd).forward(request,response);
	      		}
	    }	    
	    String logon="";
	    String logonaction="";
        if(bosflag.equalsIgnoreCase("ul"))
        {
        	logon = "boslogon"; 
        	logonaction="/templates/index/UserLogon.do";   
        }else if(bosflag.equalsIgnoreCase("el"))
        {
        	logon = "logon"; 
        	logonaction="/templates/index/employLogon.do";          	
        }                	
        else if(bosflag.equalsIgnoreCase("hl"))
        {
        	logon ="hrlogon";  
        	logonaction="/templates/index/hrlogon.do";           	
        }
        else if(bosflag.equalsIgnoreCase("hl4"))
        {
        	logon = "hrlogon4"; 
        	logonaction="/templates/index/hrlogon4.do";           	
        }
        else if(bosflag.equalsIgnoreCase("el4"))
        {
        	logon = "emlogon4"; 
        	logonaction="/templates/index/emlogon4.do";          	
        }                     
        else if(bosflag.equalsIgnoreCase("pl"))
        {
        	logon = "perlogon";   
        	logonaction="/templates/index/perlogon.do";         	
        }  
        else if(bosflag.equalsIgnoreCase("bi"))
        {
        	logon = "bilogon";   
        	logonaction="/templates/index/bilogon.do";         	
        }
        else if(bosflag.equalsIgnoreCase("il"))
        {
        	logon = "ilearning";   
        	logonaction="/templates/index/ilearning.do";         	
        }  
        else if(bosflag.equalsIgnoreCase("epmgw"))
        {
        	logon = "epmlogon";   
        	logonaction="/templates/index/epmlogon.do";         	
        }                    
        else if(bosflag.equalsIgnoreCase("mp"))
        {
        	logon = "mplogon";   
        	logonaction="/phone-app/index.do";         	
        }else if(bosflag.equalsIgnoreCase("hcm"))
        {
            logon = "hcmlogon";   
            logonaction="/templates/index/hcmlogon.do";          
        }else
        {
        	logon = "hcmlogon";   
            logonaction="/templates/index/hcmlogon.do";          	
        }
%>

<body   bgcolor="#FFFFFF" text="#000000" style="margin:0 0 0 0">
	<%if(flag){ %>
	<form action="<%=logonaction%>" name="login" method="post">
	    <input type="hidden"name="logon.x"  value="link"/>	
	    <input type="hidden" name="username"  value="<%=username%>"/>
	    <input type="hidden" name="password"  value="<%=password%>"/>
	</form>
	<script type="text/javascript">
		document.all.login.submit();	
	</script>
	<%} else { %>
	 <logic:redirect forward="<%=logon%>" ></logic:redirect>	
	<%}%>
</body>
</html>