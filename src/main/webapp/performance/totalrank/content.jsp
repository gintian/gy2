<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<body onload="toNext()">
<%
	String menuname = "";
%>
  	<html:form action="/performance/totalrank/totalrank">
  		<%
		int i = 0;
		%>
  		<hrms:tabset name="pagset" width="100%" height="120%" type="true">
  		<logic:iterate id="element" name="configParameterForm" property="setlist"
				indexId="index">
				<%
				CommonData item=(CommonData)pageContext.getAttribute("element");
            	String fieldid=item.getDataValue();
            	String fielddesc=item.getDataName();
				++i;
				if(!fieldid.equalsIgnoreCase("A01")){	
				%>
				<logic:equal name="configParameterForm" property="setid" value="<%=fieldid%>">
					<%menuname = "mitem"+i;%>
				</logic:equal>
				<bean:define id="nid" value="<%=fieldid%>"/>
				<hrms:tab name='<%="mitem" + i%>' label="<%=fielddesc %>"
					function_id="" visible="true"
					url="/performance/totalrank/totalrank.do?b_look=link&setid=${nid}&treeCode=${configParameterForm.treeCode}">
				</hrms:tab>
				<%}%>
  		  </logic:iterate>
  		</hrms:tabset>
  	</html:form>
</body>
<script language="javascript">
function toNext(){
	var tabid="<%=menuname%>";
	if(tabid!=null&&tabid.length>0){
		var tab=$('pagset');
		tab.setSelectedTab("<%=menuname%>");
	}
}
</script>