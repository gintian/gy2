<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<body onload="toNext()">
<%
	String menuname = "";
%>
	<html:form action="/ht/inform/data_table">
		<%
		int i = 0;
		%>
		<hrms:tabset name="cardset" width="100%" height="98%" type="true">
			<logic:iterate id="element" name="contractForm" property="setlist"
				indexId="index">
				<%
				CommonData item=(CommonData)pageContext.getAttribute("element");
            	String fieldid=item.getDataValue();
            	String fielddesc=item.getDataName();
				++i;
				if(!fieldid.equalsIgnoreCase("A01")){	
				%>
				<logic:equal name="contractForm" property="defitem" value="<%=fieldid%>">
					<%menuname = "mitem"+i;%>
				</logic:equal>
				<bean:define id="nid" value="<%=fieldid%>"/>
				<hrms:tab name='<%="mitem" + i%>' label="<%=fielddesc %>"
					function_id="" visible="true"
					url="/ht/inform/data_table.do?b_item=link&a0100=${contractForm.a0100}&dbname=${contractForm.dbname}&fieldid=${nid}&ctflag=${contractForm.ctflag}">
				</hrms:tab>
				<%} %>
			</logic:iterate>
		</hrms:tabset>
	</html:form>
</body>
<script language="javascript">
function toNext(){
	var tabid="<%=menuname%>";
	if(tabid!=null&&tabid.length>0){
		var tab=$('cardset');
		tab.setSelectedTab("<%=menuname%>");
	}
}
</script>
