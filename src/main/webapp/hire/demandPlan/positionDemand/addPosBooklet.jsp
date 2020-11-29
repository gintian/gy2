<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript' >
    <%
	if(request.getParameter("b_upFile")!=null&&request.getParameter("b_upFile").equals("up"))
	{
	%>
		window.close();
	<%
	}
	else
	{
	%>


	var info=dialogArguments;
	function upload()
	{
		if(document.positionDemandForm.file.value.length==0)
		{
			alert(PLEASE_SELECT_UPLOAD_EXPLANATION+"！");
			return;
		}
		//2014.12.05 xxd上传岗位说明书时在页面进行校验大小
		//更换为后台验证大小
		//var fso=new ActiveXObject("Scripting.FileSystemObject");
		//var fileSize=fso.GetFile(document.positionDemandForm.file.value);
		//if(fileSize.size/1024>512){
		//    alert("岗位说明书大小不能超过512K!");
		//    return;
		//}
		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_upFile=up&opt=u&posID="+info;
		document.positionDemandForm.submit();
	}
	
	<% } %>
	
	
	function del()
	{
		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_upFile=del&opt=d&posID=<bean:write name="positionDemandForm" property="e01a1" />";
		document.positionDemandForm.submit();
	}
	

</script>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><bean:message key="hire.parameterSet.positionDescrible"/></title>
	</head>
	<hrms:themes></hrms:themes>
	<body>
	<form name="positionDemandForm" method="post" action="/hire/demandPlan/positionDemand/positionDemandTree"  enctype="multipart/form-data"  >
		<Br>
		<table align="center">
			 <tr class="list3">
                	  	 <td align="left" nowrap >&nbsp;<Br>&nbsp;<bean:message key="hire.attach.size"/>
                	  	   <logic:equal name="positionDemandForm" property="isPosBooklet" value="1">
                 	 	   		 &nbsp; &nbsp;
                 	 	   		 <bean:define id="e01a1s" name="positionDemandForm" property="e01a1"></bean:define>
                 	 	   		 <%
                 	 	          String e01a1 = PubFunc.encrypt(e01a1s.toString());
                 	 	   		 %>
                 	 	   		 <a href='/servlet/performance/fileDownLoad?e01a1=<%=e01a1 %>&opt=hire'  target="_blank"  border='0' >  
                 	 	   		 <bean:message key="hire.parameterSet.positionDescrible"/>
                 	 	   		 </a>
                 	 	   </logic:equal>
                	  	 <Br><Br>
                 	 	   &nbsp;<input name="file" type="file" size="40" class="text4">  
                 	 	   <br>
                 	 	   
                 	 	 </td>
             </tr>
             <tr class="list3">
             			 <td align='center' height="35px;">
             			 	&nbsp;<input type='button' value="<bean:message key="hire.upload"/>" onclick='upload()' class="mybutton" /> 
             			 	<logic:equal name="positionDemandForm" property="isPosBooklet" value="1">
                 	 	   		&nbsp;<input type='button' value="<bean:message key="button.delete"/>" onclick='del()' class="mybutton" /> 
                 	 	   </logic:equal>
             			 </td>
             </tr>
             
             
		</table>
	</form>	
	</body>
</html>