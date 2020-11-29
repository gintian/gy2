<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub(){
var plain=reportWorkForm.plain_id.value;
reportWorkForm.action="/performance/reportwork/searchReportWork.do?b_search=search&opt=2&plain="+plain;
reportWorkForm.submit();
}
function openSearch(n){
var plain=reportWorkForm.plain_id.value;
var theurl="/performance/reportwork/consultReportWork.do?b_init=init&id="+n+"&plain="+plain;
var returnValue=window.showModalDialog(theurl,null, 
		        "dialogWidth:600px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no");			
       		 
  }
  
function search(){
reportWorkForm.action="/performance/reportwork/searchReportWork.do?b_query=query";
reportWorkForm.submit();

}  



//-->
</script>
  <html:form action="/performance/reportwork/searchReportWork">
	<Br>
	<br>
	<logic:equal name="reportWorkForm" property="isnull" value="0">
	<p align="center">
	
	暂时没有执行中的测评计划！
	</p>
	</logic:equal>
	<logic:equal name="reportWorkForm" property="isnull" value="1">
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
	 <tr>
            <td align="left" class="TableRow" colspan='5' nowrap>
      <bean:message key="label.commend.plan"/>
            <hrms:optioncollection name="reportWorkForm" property="plainList" collection="list" />
			<html:select name="reportWorkForm" property="plain_id" size="1" onchange="sub();" >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>&nbsp;
	   	 	 <bean:message key="label.commend.p_name"/> <input type="text" name="name" value="${reportWorkForm.name}" style="width:100px"/>&nbsp;&nbsp;<input type="button" class="mybutton" name='' onclick="search()" value="查询"/>
	   	 	 </td>
         </tr>
  
           <tr>
           			<td align="center" class="TableRow" nowrap> <bean:message key="label.commend.unit"/></td> 
           			<td align="center" class="TableRow" nowrap><bean:message key="label.commend.um"/></td>
           			<td align="center" class="TableRow" nowrap><bean:message key="label.reportwork.job"/></td>
           			<td align="center" class="TableRow" nowrap><bean:message key="label.commend.p_name"/></td>
           			<td align="center" class="TableRow" nowrap><bean:message key="label.reportwork.report"/></td>
           </tr>
      </thead>
   
   <% int i=0; String className="trShallow"; %>
  	<hrms:paginationdb id="element" name="reportWorkForm" sql_str="${reportWorkForm.sql_str}" fromdict="1" where_str="${reportWorkForm.where_str}" columns="${reportWorkForm.columns}" order_by="${reportWorkForm.order_sql}" page_id="pagination" pagerows="10" indexes="indexes">
      
       
         <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
          <td align="left" class="RecordRow" width="30%" nowrap>
         <hrms:codetoname codeid="UN" name="element" codeitem="codeitem" codevalue="b0110" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
         </td>
          <td align="left" class="RecordRow" width="25%" nowrap>
         <hrms:codetoname codeid="UM" name="element" codeitem="codeitem" codevalue="e0122" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
         <hrms:codetoname codeid="@K" name="element" codeitem="codeitem" codevalue="e01a1" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
         </td>
          <td align="left" class="RecordRow" width="25%" nowrap>
          <bean:write name="element" property="a0101"/>&nbsp;
         </td>
         <td align="center" class="RecordRow" width="20%" nowrap>
         <a href="/performance/reportwork/consultReportWork.do?b_init=init&id=<bean:write name='element' property='id'/>&plain=${reportWorkForm.plain_id}&object_id=<bean:write name='element' property='object_id'/>"><img src="/images/edit.gif" border='0'></a>
         </td>
        
            </tr>		    
	</hrms:paginationdb>
    </table>
    <table  width="80%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					条
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="reportWorkForm" property="pagination" nameId="reportWorkForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   	  
</table> 
    
  
</logic:equal>
   	  </html:form>