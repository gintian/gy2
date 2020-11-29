<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.showkhresult.ShowKhResultForm"%>
<%@ page import="com.hrms.struts.valueobject.Pagination"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<link href="../../css/locked-column-new.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--
function onscore(){
	showKhResultForm.action="/performance/showkhresult/show_kh.do?b_query=link&opertor=2";
    showKhResultForm.submit();
}
function showDeductMark(object_id,mainbody_id)
{
 var tplan_id="${showKhResultForm.plan_id}";
 var plan_id="-1";
 if(tplan_id&&tplan_id.indexOf(",")!=-1)
 {
   var arr=tplan_id.split(",");
   plan_id=arr[0];
 }
   var thecodeurl="/performance/showkhresult/show_kh.do?b_deduct=query`opt=1`planid="+plan_id+"`objectid="+object_id+"`mainbodyid="+mainbody_id; 
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");			
 
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
	BACKGROUND-COLOR: #ffffff; 
	font-size: 12px;  
	border: inset 1px #94B6E6;
	COLOR : #103B82;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;
}
.cell_locked_Shallow2 {
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #F3F5FC; 
	border: inset 1px #94B6E6;
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
	border-left:none;
	border-top:none;
}
</style>
<html:form action="/performance/showkhresult/show_kh">
	<br />
	<%ShowKhResultForm s = (ShowKhResultForm) session
					.getAttribute("showKhResultForm");
			Pagination p = s.getPagination();
			ArrayList objectList = s.getObjectList();
			ArrayList mainbodyList = s.getMainbodyList();
			ArrayList pointname=(ArrayList)s.getPointname();
			ArrayList fieldlist=s.getFieldlist();
			int tablewidth=(pointname.size()+4)*60;
			int objectlen = objectList.size();
			int mainbodylen = mainbodyList.size();
			ArrayList templist = new ArrayList();
			if (p != null) {
				templist = p.getCurr_page_list();
			}
			int rowspan = mainbodylen;
			int j = 0;
			int i = 0;
			int pagec = (14 / mainbodylen);
			int pagenum = pagec * mainbodylen;
			%>
	<table border="0" width="100%">
		<tr>
			<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap>
							<bean:message key="lable.performance.perPlan"/>：
							<bean:write name="showKhResultForm" property="selstr" filter="false" />
						</td>
						<td nowrap>
						<html:hidden name="showKhResultForm" property="modelType"/>
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
		<tr>
			<td width="100%">
			<script language='javascript' >
		 document.write("<div id=\"tbl-container\"  style='BORDER-BOTTOM:1pt solid;BORDER-LEFT:1pt solid;BORDER-RIGHT:1pt solid;BORDER-TOP:1pt solid;position:absolute;left:5;height:"+down_height+";width:100%' class='common_border_color'>");
          </script>	
				<table  width="<%=tablewidth%>" border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable">
					<bean:write name="showKhResultForm" property="header" filter="false"/>
					<hrms:paginationdb id="element" name="showKhResultForm" sql_str="showKhResultForm.sql" table="" where_str="showKhResultForm.where" columns="showKhResultForm.column" order_by="showKhResultForm.orderby" pagerows="15" page_id="pagination"
						indexes="indexes">
						<bean:define id="aocname" name="element" property="ocname"></bean:define>
						<bean:define id="aodname" name="element" property="odname"></bean:define>
						<bean:define id="emp_id" name="element" property="object_id"></bean:define>
						<bean:define id="memp_id" name="element" property="mainbody_id"></bean:define>
						 <%if(j%2==0){ %>
	                  <tr class="trShallow">
	                  <logic:notEqual value="UN" name="showKhResultForm" property="modelType">
	                  <td align="left" class="RecordRow" nowrap>
									<bean:write name="element" property="ocname" />
								</td>							
								<td align="left" class="RecordRow" nowrap>
									<bean:write name="element" property="odname" />
								</td>
								</logic:notEqual>
								<td align="center" class="RecordRow" nowrap>
									<A href='/performance/showkhresult/objectkh.do?b_query=link&object_id=${emp_id}'> <bean:write name="element" property="objectname" /> </A>
								</td>
							<td align="center" class="RecordRow" nowrap>
								<A href='/performance/showkhresult/mainbodykh.do?b_query=link&mainbody_id=${memp_id}'> <bean:write name="element" property="mainame" /> </A>
							</td>
							<logic:equal value="1" property="isShowDeductMark" name="showKhResultForm">
							  <td align="center" class="RecordRow" nowrap>
							  <a href="javascript:showDeductMark('${emp_id}','${memp_id}');">
							     <img src="/images/view.gif" border="0"/>
							     </a>
							  </td>
							</logic:equal>
	                   <%} else { %>
	                          <tr class="trDeep">
	        				 <logic:notEqual value="UN" name="showKhResultForm" property="modelType">			
								<td align="left" class="RecordRow" nowrap>
									<bean:write name="element" property="ocname" />
								</td>							
								<td align="left" class="RecordRow" nowrap>
									<bean:write name="element" property="odname" />
								</td>
								</logic:notEqual>
								<td align="center" class="RecordRow" nowrap>
									<A href='/performance/showkhresult/objectkh.do?b_query=link&object_id=${emp_id}'> <bean:write name="element" property="objectname" /> </A>
								</td>
							<td align="center" class="RecordRow" nowrap>
								<A href='/performance/showkhresult/mainbodykh.do?b_query=link&mainbody_id=${memp_id}'> <bean:write name="element" property="mainame" /> </A>
							</td>
							<logic:equal value="1" property="isShowDeductMark" name="showKhResultForm">
							  <td align="center" class="RecordRow" nowrap>
							  <a href="javascript:showDeductMark('${emp_id}','${memp_id}');">
							     <img src="/images/view.gif" border="0"/>
							     </a>
							  </td>
							</logic:equal>
							<%
							}
							  for(int k=0;k<fieldlist.size();k++)
							  {
							    FieldItem item = (FieldItem)fieldlist.get(k);
							    String itemid="aa"+item.getItemid();
							 %>
							 <td align="center" class="RecordRow" >
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
						<td width="60%" valign="bottom" align="left" class="tdFontcolor" nowrap>
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
               </td></tr>
	</table>
</html:form>
