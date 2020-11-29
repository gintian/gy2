<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
.TableRow_self {
 
	background-position : center left;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
.TableRow_self_left {
 
	background-position : center left;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
.TableRow_self_right {
 
	background-position : center left;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
.div {
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	overflow: auto;
}
.RecordRow_left {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
.RecordRow_right {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}

</style>
<script type="text/javascript">
<!--
	function sub()
	{
	   var object=document.getElementsByName("select");
	   var objectvalue;
	   for(var i=0;i<object.length;i++){
		   if(object[i].checked==true){
			   objectvalue=object[i].value.split(",") ;
		 }
	   }
	     if(typeof(objectvalue)=="undefined"){
	    	 alert("请选择报送对象！");
	  	   	 return;
	     }
	
		 var obj = new Object();
		 obj.objecttype=objectvalue[0];
		 var title=objectvalue[1];
		 var content=objectvalue[2];
		 if(title==''||content=='')
		 {
		   alert("请选择报送对象！");
		   return;
		 }
		 obj.title=title;
		 obj.content=content;
		 returnValue=obj;
		 window.close();
	}

	function winClose()
	{
	  returnValue=null;
	  window.close();
	  
	}
	
//-->
</script>
<html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
<br/>
<table width="100%" cellpadding="0" cellspacing="0" align="center">
<tr>
<td align="center">
<div align="center" style="width: 340px;height: 135px;overflow:auto;" class="div common_border_color">
<table align="center" width="100%" class="ListTable" cellpadding="0" cellspacing="0">
<bean:define id="actortype" property="actortype" name="positionDemandForm"/>
<bean:define id="zparrplist" name="positionDemandForm" property="zparrplist"/>
<thead>
	<tr> 
	<td align="center" class="TableRow_self_left common_background_color" width="35" nowrap >
		<bean:message key="column.select"/>
	</td>
	<logic:equal value="1" property="actortype" name="positionDemandForm">
		<td align="center" class="TableRow_self common_background_color" nowrap >
			<bean:message key="column.sys.org"/>
		</td>
		<td align="center" class="TableRow_self common_background_color" nowrap >
			<bean:message key="column.sys.dept"/>
		</td>
		<td align="center" class="TableRow_self_right common_background_color" nowrap >
			<bean:message key="hire.employActualize.name"/>
		</td>
	</logic:equal>
	<logic:equal value="4" property="actortype" name="positionDemandForm">
		<td align="center" class="TableRow_self common_background_color" nowrap >
			<bean:message key="label.user.group"/>
		</td>
		<td align="center" class="TableRow_self_right common_background_color" nowrap >
			<bean:message key="column.submit.username"/>
		</td>
	</logic:equal>
	</tr>
</thead>
<%
	int i = 0;
%>
<logic:equal value="1" property="actortype" name="positionDemandForm">
		<logic:iterate id="element" name="positionDemandForm" property="zparrplist" indexId="index">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			<tr class="trDeep">
					<%
						}
								i++;
					%>
					<td align="center" class="RecordRow_left common_border_color" width="35px" nowrap>
						<input type="radio" name="select"
							value="${actortype},<bean:write name="element" property="a0101" />,<bean:write name="element" property="a0100" />">
					</td>

					<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
					</td>
					<td align="left" class="RecordRow_right common_border_color" nowrap>
						&nbsp;<bean:write name="element" property="a0101" />
						&nbsp;
					</td>
			</tr>
		</logic:iterate>
</logic:equal>
<logic:equal value="4" property="actortype" name="positionDemandForm">
		<logic:iterate id="element" name="positionDemandForm" property="zparrplist" indexId="index">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			<tr class="trDeep">
					<%
						}
								i++;
					%>
					<td align="center" class="RecordRow_left common_border_color" width="35px" nowrap>
						&nbsp;<input type="radio" name="select"
							value="${actortype},<bean:write  name="element" property="a0101"/>,<bean:write  name="element" property="username"/>">&nbsp;
					</td>

					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="groupName" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow_right common_border_color" nowrap>
						&nbsp;<bean:write name="element" property="a0101" />
						&nbsp;
					</td>
				</tr>
		</logic:iterate>
</logic:equal>
</table>
</div>
	<table align="center" width="90%" class="ListTable" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>

		<tr>
			<td align="center">
				<input type="button" class="mybutton" name="o" value="<bean:message key="button.ok"/>" onclick="sub();"/>
				&nbsp;&nbsp;
				<input type="button" class="mybutton" name="c" value="<bean:message key="button.close"/>" onclick="winClose();" />

			</td>
		</tr>
	</table>
	</td>
</tr>
</table>
</html:form>