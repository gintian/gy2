<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<hrms:themes />
 <%
 	String opt=(String)request.getParameter("opt");
 	String planid=(String)request.getParameter("plan_id");
 	EvaluationForm myForm=(EvaluationForm)session.getAttribute("evaluationForm");	
 	ArrayList alist=myForm.getImplist();
 	String object_type=myForm.getObject_type();
 	LazyDynaBean bean=myForm.getContBean();
 	String allcont = (String)bean.get("allcont");
 	if(allcont==null || allcont.trim().length()<=0)
 		allcont = "0";
 	String matchcont = (String)bean.get("matchcont");
 	if(matchcont==null || matchcont.trim().length()<=0)
 		matchcont = "0";
 	String dismatchcont = (String)bean.get("dismatchcont");
 	if(dismatchcont==null || dismatchcont.trim().length()<=0)
 		dismatchcont = "0";
 	
 	String canimport=myForm.getCanimport();
  %>
<script type="text/javascript">
	function closeParent(){
		//opener.window.close();
	}
	<%
	//if(opt!=null&&opt.equalsIgnoreCase("new")){
	//【5421】绩效管理：绩效评估页面在进行导入修正分值的时候，如果Excel中未填完整，提示窗口太大了 jingq add 2014.12.01
 	if(canimport!=null&&canimport.trim().length()==0){
	%>
        if(window.showModalDialog) {
            window.dialogHeight = 550 + "px";
            window.dialogWidth = 700 + "px";
            window.dialogTop = 200 + "px";
        }else{
            var top_= window.screen.availHeight-600>0?(window.screen.availHeight-600)/2:0;
            var left_= window.screen.availWidth-700>0?(window.screen.availWidth-700)/2:0;
            window.resizeTo(700,600);
            window.moveTo(left_,top_);
        }
	<%	
 	} else {%>
 	window.dialogHeight=180+"px";
	window.dialogWidth=400+"px";
 	<%}%>
 	function importtable(){
 		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_imported=init&plan_id=<%=planid%>&isclosed=1";
 		document.evaluationForm.submit();
 	}
 	function sucessfortrun(outparamters){
 		var sucess=outparamters.getValue("flag");
 		if(sucess=='ok'){
 			window.returnValue='ok';
 			window.close();
 		}else{
 			alert("456");
 		}
 	}
 	<%
 		if(request.getParameter("isclosed")!=null){
 		%>
 		window.returnValue='ok';
 		window.close();
 		<%
 		}
 	%>
</script>
<title>批量导入</title>
</head>

<body onload="closeParent();">
<form action="/performance/evaluation/performanceEvaluation.do" method="post" name="evaluationForm">
<%	if(canimport!=null&&canimport.trim().length()==0) {%>
<table width='100%' align='center' class="ListTable">
<tr>
<td width='80%' align='center' valign='top' colspan='3'>
<div style='border:1px solid #8EC2E6;height:400px;overflow-y:auto;overflow-x:hidden;'>

<table  class="ListTable">
	<tr>
	<td class='TableRow' style="border-left: 0px ;border-top: 0px" align='center' width="100px">
		&nbsp;&nbsp;
	</td>
	<% if(object_type!=null&&object_type.equalsIgnoreCase("2")){ %>
	<td class='TableRow' style="border-top: 0px ;" align='center' width="100px">
		姓名
	</td>
	<%} %>
	<% if(object_type!=null&&!object_type.equalsIgnoreCase("2")){ %>
	<td class='TableRow' style="border-top: 0px ;" align='center' width="100px">
		单位/部门名称
	</td>
	<%} %>
	<td class='TableRow' style="border-top: 0px;" align='center' width="100px">
		唯一标识
	</td>
	<td class='TableRow' style="border-top: 0px;" align='center'  width="100px">
		修正分值
	</td>
	<td class='TableRow' style="border-right: 0px ;border-top: 0px" align='center'  width="100px">
		修正原因
	</td>
	</tr>
	<%int i=0; %>
	<logic:iterate id="element" name="evaluationForm" property="implist">
	<%i++; %>
	<tr>
	<td class='RecordRow' style="border-left: 0px ;"  align='center'>
		<%=i %>
	</td>
	<td class='RecordRow'  align='center'>
		<bean:write name="element" property='a0101'/>
	</td>
	<td class='RecordRow' align='center'>
		<bean:write name="element" property='唯一标识'/>
	</td>
	<td class='RecordRow'  align='right'>
		<bean:write name="element" property='修正分值'/>
	</td>
	<td class='RecordRow' style="border-right: 0px ;" align='center'>
		<bean:write name="element" property='修正原因'/>
	</td>
	</tr>
	</logic:iterate>
</table>
</div>
</td>
<td align='center'  valign='top'>
<table>
	<tr>
	<% if(object_type!=null&&object_type.equalsIgnoreCase("2")){ %>
	<td  align='center' width="100px" nowrap style="text-align:left">
		Excel 中第二列为关联指标，关联指标为'系统维护-系统参数'中指定的唯一性指标。
	</td>
	<%} %>
	<% if(object_type!=null&&object_type.equalsIgnoreCase("1")){ %>
	<td class='TableRow' align='center' width="100px" nowrap style="text-align:left">
		Excel 中第二列为关联指标,关联指标为考核对象名称。
	</td>
	<%} %>
	</tr>
	
</table>
</td>
</tr>
<tr>
<td>
源数据共 <%=allcont %>条
</td>
<td>
对应上数据共<%=matchcont %>条
</td>
<td> 
未对应上数据共<%=dismatchcont %>条
</td>
</tr>
<tr>
<td align='center' colspan='3'><br>
<input type='button' class='MyButton' onclick='importtable();' value='导 入'>
<input type='button' class="MyButton" onclick='window.close();' value='关 闭'>
</td>
</tr>
</table>
<%}else{ %>
	<table width="100%" cellpadding="0" cellspacing="0" class="ListTable">
		<tr>
			<td class="TableRow">导入失败</td>
		</tr>
		<tr>
		<td valign='middle' width='100%' align=center height='100px;' class="RecordRow">
			<%=canimport %>
			<br>
		
			</td>
		</tr>
		<tr>
		<td  valign='middle' width='100%' align=center height="35px;">
		<input type='button' class="MyButton" onclick='window.close();' value='关 闭'>
		</td>
		</tr>
	</table>
	
<%} %>
</form>
</body>

</html>