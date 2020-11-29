<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%
	String tabid="";
	String sp_flag="";
%>
<script type="text/javascript">
<!--
	function edit_inf(tabid)
	{
		abroadForm.action="/general/template/operation/show_explain.do?b_edit=link&tabid="+tabid;
		abroadForm.submit();
	}

	function fill_out(sp_flag,tabid)
	{
	    window.location.href="/general/template/edit_form.do?b_query=link&ins_id=0&returnflag=0&sp_flag="+sp_flag+"&tabid="+tabid;
	}	
//-->
</script>
<html:form action="/general/template/operation/show_explain">
	<br>
	<br>
	<table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">		
		<tr class="trShallow">
		  <logic:iterate id="element"  name="abroadForm"  property="templist" indexId="index">
			<td align="center" valign="top" class="RecordRow">
             <bean:write name="element" property="content" filter="false"/><br>
			</td>
   		  </logic:iterate>  			
		</tr>
		<tr class="trShallow">
		  <logic:iterate id="element"  name="abroadForm"  property="templist" indexId="index">
		    <%
            	LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            	tabid=(String)item.get("tabid");
            	sp_flag=(String)item.get("sp_flag");
            %>
			<td align="center" style="height:35px">
		     <hrms:priv func_id="32100" module_id=""> 				
	 		    <INPUT type="button" class="mybutton" onclick="edit_inf('<%=tabid%>');" name="b_edit" value='<bean:message key="button.edit"/>'>
    		 </hrms:priv> 		 		    
	 		 <INPUT type="button" class="mybutton" onclick="fill_out('<%=sp_flag%>','<%=tabid%>');" name="bc_btn1" value='<bean:message key="button.fill"/>'>
			</td>
   		 </logic:iterate>  			
		</tr>		
	</table>
</html:form>
