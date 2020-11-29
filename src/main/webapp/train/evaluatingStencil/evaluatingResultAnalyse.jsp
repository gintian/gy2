<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<head>
<style type="text/css">
<!--
.back_css {
	background-color: #FFFFFF;
	border: solid 1px;
}

.mytd {
			border: solid 1px #000000;
			BORDER-BOTTOM: #000000 1pt solid; 
			BORDER-LEFT: #000000 1pt solid; 
			BORDER-RIGHT: #000000 1pt solid; 
			BORDER-TOP: #000000 1pt solid;
		}
		
		
		.mylefttd {
			border-right-width: 1px;
			border-bottom-width: 1px;
			border-right-style: solid;
			border-bottom-style: solid;
			border-right-color: #000000;
			border-bottom-color: #000000;
		}
		.myUnToptd {
			border-right-width: 1px;
			border-bottom-width: 1px;
			border-left-width: 1px;
			border-right-style: solid;
			border-bottom-style: solid;
			border-left-style: solid;
			border-right-color: #000000;
			border-bottom-color: #000000;
			border-left-color: #000000;
		}
		.myUnLefttd {
			border-right-width: 1px;
			border-bottom-width: 1px;
			border-top-width: 1px;
			border-right-style: solid;
			border-bottom-style: solid;
			border-top-style: solid;
			border-right-color: #000000;
			border-bottom-color: #000000;
			border-top-color: #000000;
		}
		
		.TEXT_NB {
			BACKGROUND-COLOR:transparent;
			BORDER-BOTTOM: #000000 1pt solid; 
			BORDER-LEFT: medium none; 
			BORDER-RIGHT: medium none; 
			BORDER-TOP: medium none;
		}
-->
</style>
<script language='javascript'>

    function showfile(outparamters)
	{
		
		var outName=outparamters.getValue("outName");
	    var name=outName.substring(0,outName.length);
	    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
	}

	function excel()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("r3101",'<%=(request.getParameter("r3101"))%>');
		hashvo.setValue("templateid",'<%=(request.getParameter("templateid"))%>');
	    var In_paramters="opt=1";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'10300130015'},hashvo);
	}
	function rt(){
		//document.trainResourceForm.action="/train/request/trainsData.do?b_query=link&model=1";
		//document.trainResourceForm.submit();
		history.go(-1);
		//window.history.go(-1);
	}
</script>

</head>
<body>
<html:form action="/train/evaluatingStencil">


<table border='0' width='100%'>


<tr><td  >&nbsp;&nbsp; </td><td>
	
	<table border='0' width='95%'  class='back_css common_border_color' >
		<tr><td>&nbsp;</td></tr>
		<tr><td align='center'>
	${tranEvaluationFrame.analyseHtml}
		</td></tr>
		<tr><td hight='50'><br><br>&nbsp;</td></tr>
	</table>

</td></tr>

<tr align="center"><td  >&nbsp;&nbsp; </td>
<td>
&nbsp;<Input type='button' value="<bean:message key='button.createescel'/>" class="mybutton"  onclick="excel()" /> 
<% if(request.getParameter("type")==null||!request.getParameter("type").equals("direct")){ 
    if(request.getParameter("rerurn")!=null && request.getParameter("rerurn").length() > 0){%>
    <Input type='button' value='<bean:message key="kq.search_feast.back"/>' class="mybutton"  onclick="rt();" /> 
    <%} else { %>
	<Input type='button' value='<bean:message key="lable.welcomeboard.close"/>' class="mybutton"  onclick="window.close();" /> 
<%   }
   } %>

<% if(request.getParameter("type")!=null&&request.getParameter("type").equals("direct")){  %>
<Input type='button' value="<bean:message key='lable.welcomeboard.close'/>" class="mybutton"  onclick="rt();" /> 
<%  } %>
</td></tr>

</table>
</html:form>
</body>
</html>