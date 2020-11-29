<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script type="text/javascript" src="/train/resource/course/gmsearch.js"></script>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<script type="text/javascript" >
	function returnback1(){
		location.href="/train/resource/course.do?b_query=link&a_code=${courseStudentForm.a_code}";
	}
</script>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%int i = 0; %>
  <body>
  <html:form action="/train/resource/course/showdetail">
  <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
  	<tr>
  	<td  style="border-bottom: 0px;border-left: 0px;">
  	  <div class="fixedDiv2">
    	<table width="100%" border="0"  cellspacing="0" cellpadding="0">
    		<tr align="center" class="fixedHeaderTr">
    			<td align="center"  class="TableRow" style="border-left: none;border-top: none;" nowrap>单位名称</td>
    			<td align="center"  class="TableRow" style="border-left: none;border-top: none;"  nowrap>部门</td>
    			<td align="center"  class="TableRow" style="border-left: none;border-top: none;"  nowrap>岗位名称</td>
    			<td align="center"  class="TableRow" style="border-left: none;border-top: none;"  nowrap>姓名</td>
    			<td align="center"  class="TableRow" style="border-left: none;border-top: none;"  nowrap>起始日期</td>
    			<td align="center"  class="TableRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>结束日期</td>
    		</tr>
    		<hrms:paginationdb id="element" name="courseStudentForm" sql_str="courseStudentForm.strsql" table="" 
			where_str="courseStudentForm.strwhere" columns="courseStudentForm.columns" 
			 page_id="pagination" pagerows="${courseStudentForm.pagerows}">
				<%
          if(i%2==0)
          {
          %>
				<tr class="trShallow">
					<%}
          else
          {%>
				
				
				<tr class="trDeep">
				
					<%
          }%>
          <td align="left" class="RecordRow"  style="border-left: none;border-top: none;"  nowrap>
         		<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>         
          		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
         	</td>
         	<td align="left" class="RecordRow"  style="border-left: none;border-top: none;"  nowrap>
         		<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>         
                &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
         	</td>
         	<td align="left" class="RecordRow"  style="border-left: none;border-top: none;"  nowrap>
         		<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>         
                &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
         	</td>
         	<td align="left" class="RecordRow"  style="border-left: none;border-top: none;"  nowrap>
         		&nbsp;<bean:write name="element" property="a0101" filter="true"/>
         	</td>
         	<td align="left" class="RecordRow"  style="border-left: none;border-top: none;"  nowrap>
         		&nbsp;<bean:write name="element" property="start_date" filter="true" format="yyyy-MM-dd"/>
         	</td>
         	<td align="left" class="RecordRow"  style="border-left: none;border-top: none;border-right: none;"  nowrap>
         		 &nbsp;<bean:write name="element" property="end_date" filter="true" format="yyyy-MM-dd"/>
         	</td>
        <% i++;%>
         	</tr>
          </hrms:paginationdb>
				</table>
				</div>
				</td>
				</tr>
		<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="courseStudentForm"
								pagerows="${courseStudentForm.pagerows}" property="pagination"
								 scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="courseStudentForm"
									property="pagination" nameId="courseStudentForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			
			<td align="left" style="padding-top: 5px;">
				<input type="button" class="mybutton" value='<bean:message key='reportcheck.return'/>' onclick="returnback1();"/>
			</td>
		</tr>
		</table>		
		</html:form>
  </body>
</html>
