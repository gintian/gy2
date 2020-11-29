<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.Enumeration"%>
<%
	/*Enumeration headers = request.getHeaderNames() ;
	while(headers.hasMoreElements())
	{
	     String head = (String)headers.nextElement();
	     System.out.println(head+":"+request.getHeader(head));
	}
	System.out.println("UserPrincipal:"+request.getRemoteAddr());*/
 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>

	 <link rel="stylesheet" href="../phone-app/jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../phone-app/jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../phone-app/jquery/jquery.mobile-1.0a2.min.js"></script>	
	 <script type="text/javascript" src="./jquery/rpc_command.js"></script>
	 <script type="text/javascript">
		function netsingin(singin_flag)
		{
			var map = new HashMap();
        	map.put("singin_flag",singin_flag);
        	map.put("isMobile","1");
		   　Rpc({functionId:'15502110200',success:showReturn},map);	
		}
		function showReturn(html)
			{
				var map=JSON.parse(html);
				//alert(html);
				//alert(map.html);
				if(map.succeed.toString()=='false')
				{
					alert(map.message);
				}else{
					var mess=map.mess;
					alert(mess);
			    }
			}
	 </script>
  	 <style>
			 
	 </style>
</head>
<body>
<html:form action="/phone-app/mainpanel">
<div data-role="page" data-fullscreen="true" id="mainbar" >	
	<div data-role="header" data-position="fixed" data-position="inline">
		<a href="index.jsp" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">退出</a>
		<h1>移动eHR</h1>
		<a href="#about" data-role="button" data-icon="star" >关于</a>	
	</div>	
	<div data-role="content" style="margin-top: 40px">
		<div class="ui-grid-c">
		    <hrms:extmenu moduleid="50" mobile_app="true"/>	
		</div>	
	</div>
</div>
<div data-role="page" id="about">
	<div data-role="header" data-position="fixed" data-position="inline">
		<a href="#mainbar" data-role="button" data-icon="forward">返回</a>
		<h1>关于</h1>
	</div>	
	<div data-role="content">	
	  <table width="90%" border="0"  align="center" cellpadding="0" cellspacing="0" class="ListTable">
    	<tr>
      		<td height="18" nowrap class="TableRow"  align="left" colspan="2">移动eHR</td>           
    	</tr>
  		<tr>
    		<td width="30%" class="RecordRow"><font color="#0066FF">版本号：</font></td>
    		<td width="70%" class="RecordRow">2.0</td>
 		</tr>
	  </table>																		
	</div>	
</div>	
</html:form>
</body>
</html>