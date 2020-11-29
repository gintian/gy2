<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.ListTableF {
	border:1px solid #C4D8EE;
	BORDER-BOTTOM: medium none;
    BORDER-TOP: medium none; 
}
-->
</style>
<html:form action="/selfservice/lawbase/lawtext/affix_digest">
<table align="center" width="90%" valign='middle'>
  <tr>
     <td valign='top'>
        
	   <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center">
	   		<logic:notEqual name="lawbaseForm" property="digest" value=""> 
            <tr height="20"> 
            <td align="left" colspan="4" class="TableRow">
						&nbsp;
				<bean:write name="lawbaseForm" property="digest_desc"/>
				&nbsp;
			</td>
            </tr>   
            <tr>
             <td align="middle" valign="top" colspan="4" style="border-collapse: collapse" bgcolor="">
              <table border="0" cellpmoding="0" cellspacing="0" class="" cellpadding="0" width="100%">
					<tr >
						<td align="left"valign="top" class="ListTableF">
							<br>
							<bean:write name="lawbaseForm" property="digest" filter="false"/>
							<br>
						</td>
					</tr>              
                 </table>
                 
             </td>           
           </tr> 
           </logic:notEqual>
           <logic:equal name="lawbaseForm" property="law_ext_save" value="true"> 
           <tr>
             <td align="middle" height="80" valign="top" colspan="4" style="border-collapse: collapse" bgcolor="">
               <table border="0" cellpmoding="0" cellspacing="0" class="" cellpadding="0" width="100%">
	         <tr>
	          <td width='90%' align='center'>
	          <table width='100%' border="0" cellpmoding="0" cellspacing="0" class="ListTable">
	                 <tr>
				<td align="center" class="TableRow" nowrap>
					名 称
				</td>
				<td align="center" class="TableRow" nowrap>
					版本
				</td>
				<td align="center" class="TableRow" nowrap>
					创建日期
				</td>
				<td align="center" class="TableRow" nowrap>
					创建人
				</td>
				<td align="center" class="TableRow" nowrap>
					操作
				</td>
			</tr>
	             	<hrms:extenditerate id="element" name="lawbaseForm" property="paginationForm.list" indexes="indexes" pagination="paginationForm.pagination" pageCount="10" scope="session">
        
			<tr class="trShallow">
			
                <td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(name)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(version)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
     				<bean:write name="element" property="string(create_time)" filter="true" />
                    
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(create_user)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
				    <bean:define id="fileid" name="element" property="string(fileid)"></bean:define>
				    <% String encryptId = fileid.toString(); %>
					<a href="/servlet/vfsservlet?fileid=<%= encryptId %>" target=_blank>下载</a>
					&nbsp;
				</td>
                          </tr>
		          </hrms:extenditerate>
	            </table>
                   <table width="100%" align="center" class="RecordRowP">
		     <tr style="height:30px;">
			<td valign="bottom" class="tdFontcolor" align="left">
				第
				<bean:write name="lawbaseForm" property="paginationForm.pagination.current" filter="true" />
				页 共
				<bean:write name="lawbaseForm" property="paginationForm.pagination.count" filter="true" />
				条 共
				<bean:write name="lawbaseForm" property="paginationForm.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="lawbaseForm" property="paginationForm.pagination" nameId="paginationForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		    </tr>
	           </table>
                </table>
             </td>           
           </tr>     
          </logic:equal>
          <logic:equal name="lawbaseForm" property="law_ext_save" value="false"> 
           <tr>
             <td align="middle" height="30" valign="middle" colspan="4" style="border-collapse: collapse" bgcolor="">
               没有附件信息！
             </td>
           </tr>
           </logic:equal>
        </table>
     </td>     
  </tr>
   <tr>
    <td align="center"  colspan="4">     
      <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="returnback();" class="mybutton">						      
    </td>
  </tr> 
</table>
</html:form>
<script>
	function returnback(){
		<%if("term".equals(request.getParameter("result"))){%>
			window.location.href="/selfservice/lawbase/lawtext/law_term_query.do?b_query=link";
		<%}else if("global".equals(request.getParameter("result"))){%>
			window.location.href="/selfservice/lawbase/lawtext/globalsearch.do?b_query=link";
		<%}else{%>
			window.location.href="/selfservice/lawbase/lawtext/law_maintenance.do?b_query=link&isback=y";
		<%}%>
	}
</script>