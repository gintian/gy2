<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.PaginationForm"%>
<%@ page import="com.hjsj.hrms.actionform.performance.showkhresult.ShowKhResultForm"%>
<%@ page import="com.hrms.struts.valueobject.Pagination"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<link href="../../css/locked-column-new.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<%ShowKhResultForm s = (ShowKhResultForm) session
					.getAttribute("showKhResultForm");
			Pagination p = s.getPagination();
			ArrayList objectList = s.getObjectList();
			ArrayList mainbodyList = s.getMainbodyList();
			ArrayList fieldlist=s.getFieldlist();
			ArrayList pointname=(ArrayList)s.getPointname();
			int tablewidth=pointname.size()*60;
			int objectlen = objectList.size();
			int mainbodylen = mainbodyList.size();
			ArrayList templist = new ArrayList();
			String mainbody_id=(String)mainbodyList.get(0);
			int rowspan = mainbodylen;
			int j = 0;
			int i = 0;
			int pagec = (14 / mainbodylen);
			int pagenum = pagec * mainbodylen;

			%>
<!--
function onscore(){
	showKhResultForm.action="/performance/showkhresult/mainbodykh.do?b_query=link&opertor=2&mainbody_id=<%=mainbody_id%>";
    showKhResultForm.submit();
}
var aclientHeight=document.body.clientHeight
var down_percent=420/490;
var down_height=down_percent*aclientHeight;
//-->
</script>
<style>

div#tbl-container {
width:100%;
overflow: auto;
 
}

.TEXT_NB {
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
}
.cell_locked_page {
     background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #FFFFFF; 
	font-size: 12px;  
	border: inset 1px #94B6E6;
	COLOR : #103B82;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;

}
.cell_locked_Deep2 {
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #ffffff; 
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;

}
/* 【6154】绩效自助/考核反馈/考评分数查询，界面改成7.0风格吧，目前按分值和标度，以及点人进去后，界面都不是7.0风格。
	jingq upd 2014.12.22
*/
.RecordRow{
	border-top:none;
	border-left:none;
}
</style>
<html:form action="/performance/showkhresult/mainbodykh">
	<br />
	<table border="0" width="100%">
	<!-- 
		<tr>
			<td align="left" width="100%">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap>
							<bean:message key="lable.performance.perPlan"/>：
							<bean:write name="showKhResultForm" property="selstr" filter="false" />
						</td>
						<td nowrap>
						<logic:equal value="s" name="showKhResultForm" property="sod">
							<input type="radio" name="flag" value="score" checked="checked" onclick="onscore();">
							<bean:message key="lable.performance.show.score"/>
							</input>
							&nbsp;
							<input type="radio" name="flag" value="degree_id" onclick="onscore();">
							<bean:message key="lable.performance.show.degreeid"/>
							</input>
						</logic:equal>
						<logic:equal value="d" name="showKhResultForm" property="sod">
							<input type="radio" name="flag" value="score"  onclick="onscore();">
							<bean:message key="lable.performance.show.score"/>
							</input>
							&nbsp;
							<input type="radio" name="flag" value="degree_id" checked="checked" onclick="onscore();">
							<bean:message key="lable.performance.show.degreeid"/>
							</input>
						</logic:equal>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		 -->
		<tr>
			<td align="left" width="100%">
			<html:hidden name="showKhResultForm" property="plan_id"/>
			<html:hidden name="showKhResultForm" property="flag"/>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
						<logic:notEqual value="${showKhResultForm.mainbodyname}"  name="showKhResultForm" property="mcname">
								<bean:message key="tree.unroot.undesc" />:<bean:write name="showKhResultForm" property="mcname"/>
								&nbsp;
								<logic:notEqual value="${showKhResultForm.mainbodyname}"  name="showKhResultForm" property="mdname">
								<%
									if(s.getMdname()!=null){
								%>
								<bean:message key="tree.umroot.umdesc" />:<bean:write name="showKhResultForm" property="mdname"/>
								&nbsp;
								<%}%>
								</logic:notEqual>
						</logic:notEqual>
								<bean:message key="lable.performance.perMainBody" />:<bean:write name="showKhResultForm" property="mainbodyname"/>
								<html:hidden name="showKhResultForm" property="orm"/>
						</td>
					</tr>
					</table>
					</td>
					</tr>
		
		<tr>
			<td width="100%">
			<script language='javascript' >
    		  document.write("<div id=\"tbl-container\"  style='BORDER-BOTTOM:1pt solid;BORDER-LEFT:1pt solid;BORDER-RIGHT:1pt solid;BORDER-TOP:1pt solid;position:absolute;left:5;height:"+down_height+";width:100%' class='common_border_color' >");
          </script>	
				<table width="<%=tablewidth%>" border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable">
					
						<bean:write name="showKhResultForm" property="header" filter="false"/>
					

					<hrms:paginationdb id="element" name="showKhResultForm" sql_str="showKhResultForm.sql" table="" where_str="showKhResultForm.where" columns="showKhResultForm.column" order_by="showKhResultForm.orderby" pagerows="15" page_id="pagination"
						indexes="indexes">
						<bean:define id="emp_id" name="element" property="object_id"></bean:define>
						
						 <%if(j%2==0){ %>
	                  <tr class="trShallow">	
							<td align="center" class="RecordRow" nowrap>
							<%}
							else{
							%>
							<tr class="trDeep">
							<td align="center" class="RecordRow" nowrap>
							<%} %>			
								<!-- <A href='/performance/showkhresult/objectkh.do?b_query=link&object_id=${emp_id}'> <bean:write name="element" property="objectname" /> </A>-->
							 <bean:write name="element" property="objectname" />
							</td>
							<%
							  for(int k=0;k<fieldlist.size();k++)
							  {
							    FieldItem item = (FieldItem)fieldlist.get(k);
							    String itemid="aa"+item.getItemid();
							 %>
							 <td align="center" class="RecordRow" style="border-right-width:2px;">
									<bean:write name="element" property="<%=itemid.toLowerCase()%>" />
								</td>
							<%} %>
						</tr>
						<%j++;%>
					</hrms:paginationdb>
		<tr>
			<td colspan="1000" class="RecordRowP" style="border-left:none;">
				<table width="100%">
					<tr>
						<td width="40%" valign="bottom" align="left" class="tdFontcolor" nowrap>
							<bean:message key="label.page.serial" />
							<bean:write name="pagination" property="current" filter="true" />
							<bean:message key="label.page.sum" />
							<bean:write name="pagination" property="count" filter="true" />
							<bean:message key="label.page.row" />
							<bean:write name="pagination" property="pages" filter="true" />
							<bean:message key="label.page.page" />
						</td>
						<td width="40%" align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="showKhResultForm" property="pagination" nameId="browseRegisterForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
					</table>
					</td>
					</tr>
					<tr>
					<td colspan="1000" style="padding:5 0 0 5;">
					<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 				 </hrms:submit>
					</td>
					</tr>
				</table>
				<script language='javascript' >
		                document.write("</div>");
		                var _d = document.getElementById("tbl-container");
		                var _thead = _d.getElementsByTagName("thead")[0];
		                var _tds = _thead.getElementsByTagName("td");
		                for (var i = 0; i < _tds.length; i++) {
		                	if (_tds[i]) {
		                		_tds[i].className += " common_background_color common_border_color";
		                	}
		                }
               </script>
			</td>
		</tr>
	</table>
</html:form>
