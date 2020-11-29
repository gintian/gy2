<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script type="text/javascript" src="/train/resource/course/gmsearch.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
body{text-align: center;}
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-120);
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
    border-collapse:collapse;
}

.listtablew
{
    width: 100%;
}
.mytop{border-top: none;}
</style>
<script type="text/javascript">
<!--
//备注字段
function editMemoFild(priFld,memoFldName)
{
	var target_url="/train/resource/memoFld.do?b_query=link`flag=1`type=7`priFld="+priFld+"`memoFldName="+memoFldName;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	if(navigator.appName=="Microsoft Internet Explorer"){
		var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
		              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
	}else
		window.open(iframe_url,"","height=360px,top=200,left=500,width=390px,status=no,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=no");
}
function courseselect(r5000,id){
	if(id&&!confirm("如果撤销课程会清空该课程的学习进度，确认要撤销吗？")){
		return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("r5000",r5000);
	hashvo.setValue("id",id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:onRefresh,functionId:'2020031012'},hashvo);
}
function onRefresh(outparamters){
	courseHierarchyForm.action="/train/hierarchy.do?b_query=link&a_code=${courseHierarchyForm.a_code}";
	courseHierarchyForm.submit();
}
//试听
function learn(courseid,classes) {
var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`piv=1`classes="+classes+"`lesson=" + courseid;
var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
//window.showModalDialog(fram, "", "dialogWidth:880px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:yes");
window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
}
//学习
function learn1(courseid,classes) {
	var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=me`classes="+classes+"`lesson=" + courseid;
	var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
	//window.showModalDialog(fram, "", "dialogWidth:880px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:yes");
	window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
	}
function searche(){
	courseHierarchyForm.action="/train/hierarchy.do?b_query=link&a_code=";
	courseHierarchyForm.submit();
}
//-->
</script>
<html:form action="/train/hierarchy.do?b_query=link">
	 <html:hidden name="courseHierarchyForm" property="a_code" />
	 <html:hidden name="courseHierarchyForm" property="a_code1" />
	<%
		int i = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
			课程名称&nbsp;<html:text name="courseHierarchyForm" styleClass="text4" property="searchstr"></html:text>&nbsp;
			<span style="vertical-align: middle;">
				<input type="button" value="查询" class="mybutton" onclick="searche()"/>
			</span>
		</td>
	</tr>
	<tr>
	<td>
	<div class="myfixedDiv" style="margin-top: 5px;">
	<table border="0" cellspacing="0" align="center" cellpadding="0" style="border-collapse: collapse;" width="100%">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="10%" class="TableRow mytop" style="border-left: none;">
					&nbsp;试听&nbsp;
				</td>
				<td align="center" class="TableRow mytop">
					&nbsp;课程名称&nbsp;
				</td>
				<td align="center" width="15%" class="TableRow mytop">
					&nbsp;课程简介&nbsp;
				</td>
				<td align="center" width="8%" class="TableRow mytop">
					&nbsp;学时&nbsp;
				</td>
				<td align="center" width="10%" class="TableRow mytop">
					&nbsp;选课&nbsp;
				</td>
				<td align="center" width="10%" class="TableRow mytop" style="border-right: none;">
					&nbsp;撤课&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="courseHierarchyForm"
			sql_str="courseHierarchyForm.strsql" table="" where_str="courseHierarchyForm.strwhere"
			columns="courseHierarchyForm.columns" page_id="pagination"
			pagerows="${courseHierarchyForm.pagerows}" order_by=" order by norder,r5000">
			<bean:define id="lessonid" name="element" property="r5000"/>
			<bean:define id="lescode" name="element" property="r5004"/>
			<%
				String r5000 = SafeCode.encode(PubFunc.encrypt(lessonid.toString()));
				String r5004 = SafeCode.encode(PubFunc.encrypt(lescode.toString()));
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'');">
				<%
					} else {
				%>
			
			<tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'');">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow"  style="border-left: none;" nowrap>
				<logic:equal value="" name="element" property="lesson_from">
					&nbsp;<img src="/images/lee.png" onclick="learn('<%=r5000 %>','<%=r5004 %>');" style="border: 0px;cursor:hand;" />&nbsp;
				</logic:equal>
				<logic:notEqual value="" name="element" property="lesson_from">
					&nbsp;<img src="/images/lee.png" onclick="learn1('<%=r5000 %>','<%=r5004 %>');" style="border: 0px;cursor:hand;" />&nbsp;
				</logic:notEqual>
				</td>
				<td class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="r5003"/>&nbsp;
					<logic:iterate id="it" name="courseHierarchyForm" property="ls" indexId="indexid">
						<logic:equal value="${it}" name="element" property="r5000">
							&nbsp;<img src="/images/hot.gif" style="border: 0px;cursor:hand;" />&nbsp;
						</logic:equal>
					</logic:iterate>
				</td>
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<img src="/images/view.gif" onclick="editMemoFild('<%=r5000 %>','r5012');" style="border: 0px;cursor:hand;"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="r5009"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<logic:equal value="1" name="element" property="r5016">
					<logic:equal value="" name="element" property="id">
						&nbsp;<img src="/images/img_wd.png" onclick="courseselect('<%=r5000 %>','');" style="border: 0px;cursor:hand;" />&nbsp;
					</logic:equal>
					</logic:equal>
				</td>
				<td align="center" class="RecordRow" style="border-right: none;" nowrap>
					<logic:notEqual value="" name="element" property="id">
					<logic:equal value="1" name="element" property="lesson_from">
					<bean:define id="courseid" name="element" property="id"/>
					<%String id = SafeCode.encode(PubFunc.encrypt(courseid.toString()));  %>
						&nbsp;<img src="/images/Undo.png" onclick="courseselect('','<%=id %>');" style="border: 0px;cursor:hand;" />&nbsp;
					</logic:equal>
					</logic:notEqual>
				</td>
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
							<hrms:paginationtag name="courseHierarchyForm"
								pagerows="${courseHierarchyForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="courseHierarchyForm"
									property="pagination" nameId="courseHierarchyForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>