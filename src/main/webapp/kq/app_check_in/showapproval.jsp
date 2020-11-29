<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

  </head>
  
  <body>
 <html:form action="/kq/app_check_in/all_app_data"><br>
 <table border="0" cellspacing="0" align="center" cellpadding="0" width="90%" >
    <tr>
     <td nowrap>  
	<hrms:kqcourse />
      </td>
     </tr>
    </table> 
    <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
      <tr >
          <td height="20" class="TableRow" nowrap colspan="8" align="center">
		   待批加班记录
          </td>            	        	        	        
       </tr>
      <tr>   
       <td align="center" class="TableRow" align="center"  nowrap>序号</td>
       <td align="center" class="TableRow" align="center"  nowrap>姓名</td>
       <td align="center" class="TableRow" align="center" nowrap>加班类型</td>
       <td align="center" class="TableRow" align="center" nowrap>起始时间</td>
       <td align="center" class="TableRow" align="center" nowrap>结束时间</td>
       <td align="center" class="TableRow" align="center" nowrap>时长(小时)</td>
       <td align="center" class="TableRow" align="center" nowrap>请假事由</td>
       <td align="center" class="TableRow" align="center" nowrap>审批状态</td>
     </tr> 
     <%int i=0; %>
      <hrms:paginationdb id="element" name="appForm"
			sql_str="appForm.sql_str" table="" where_str="appForm.cond_str"
			columns="${appForm.columns}" order_by="appForm.cond_order"
			pagerows="10" page_id="pagination" indexes="indexes">
			<%
			LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		    String re=(String)abean.get("q1107"); //事由 
	          String ree = re;
	          if(re.length() > 8)
	        	  ree= re.substring(0,8)+"...";
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
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<%=i%>
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="a0101" filter="false" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<hrms:codetoname codeid="27" name="element" codevalue="q1103"
						codeitem="codeitem" scope="page"  />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="q11z1" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="q11z3" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="q11z4" filter="false" />
					&nbsp;
				</td>
				<td align="left" title="<%= re %>" class="RecordRow" nowrap>
					&nbsp;
					<%= ree %>
					&nbsp;
				</td>
				
				<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="23" name="element" codevalue="q11z5"
						codeitem="codeitem" scope="page" />
               	     &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                 
					
				</td>
				
			</tr>
		</hrms:paginationdb>
		<tr>
			<td colspan="8">
				<table width="100%" align="center" class="RecordRowTop0">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<!--  
							<hrms:paginationtag name="appForm" pagerows="${appForm.pagerows}"
								property="pagination" scope="page" refresh="true"></hrms:paginationtag>
							-->	
							      第
							     <bean:write name="pagination" property="current" filter="true" />
							     页
							     共
							     <bean:write name="pagination" property="count" filter="true" />
							     条
							     共
							     <bean:write name="pagination" property="pages" filter="true" />
							     页
															
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="appForm" property="pagination"
									nameId="appForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
     </table>
     <br>
   <table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="90%" >
    <tr>
     <td align="center">   
       <input type="button" name="tdf" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
      </td>
     </tr>
    </table> 
</html:form>
  </body>
</html>
