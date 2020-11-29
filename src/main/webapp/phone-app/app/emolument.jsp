<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.options.EmpCardSalaryShowForm"%>
<%
	EmpCardSalaryShowForm cssf = (EmpCardSalaryShowForm)session.getAttribute("empCardSalaryShowForm");
	String a0100 = cssf.getA0100();
	String flag = cssf.getFlag();
	String pre = cssf.getPre();
    String b0110=cssf.getB0110();  
    String recardconstant=cssf.getRecardconstant();     
	String recordUrl = "/ykcard/employeeselfcard.do?b_card=infoself&userbase="+pre+"&flag="+flag+"&b0110="+b0110+"&pre="+pre+"&isMobile=1";
	String tableUrl = "/system/options/salaryinfo.do?b_search=link&a0100="+a0100+"&pre="+pre+"&isMobile=1&flag="+flag;
	String musterUrl="/general/muster/hmuster/executeStipendHmuster.do?b_query=link&groupCount=1&a0100="+a0100+"&dbpre="+pre+"&isMobile=1&flag="+flag;
        String rd_url="";       
        if(recardconstant.equals("0"))
        {
           rd_url=tableUrl;
        }else if(recardconstant.equals("1"))
        {
           rd_url=musterUrl;
        }          
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>

	 <link rel="stylesheet" href="../jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>	
  	 <style>
		.ui-icon-myicon {background: url(/phone-app/images/myicon.png) center top no-repeat !important;}	 
	 </style>
<script type="text/javascript">
	function locacitonto(url){
		empCardSalaryShowForm.action=url;
		empCardSalaryShowForm.submit();
	}
</script>
</head>
<body>
<html:form action="/phone-app/app/emolument">
<div data-role="page" data-fullscreen="true" id="mainbar">	
	<div data-role="header" data-position="fixed" data-position="inline">
		<a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		<h1>我的薪酬</h1>
	</div>	
	<div data-role="content" style="margin-top: 40px">
		    <ul data-role="listview" data-inset="true">
		            <li>
		                    <a href="javascript:locacitonto('<%=recordUrl%>');">卡片方式</a>
		                  <span class="ui-icon ui-icon-arrow-r"></span></li> 
		            <li>
		               <a href="javascript:locacitonto('<%=rd_url%>');">列表方式</a>
		               <span class="ui-icon ui-icon-arrow-r"></span></li>           
            </ul>
	</div>
</div>
</html:form>
</body>
</html>