<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">
<!--
.fixedDiv2 
{ 
	overflow:auto; 
	height:374px;
	width:750px;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
-->
</style>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=fflag%>;
	document.title="编辑试题";
	
	var dels="";//删除的对象
	
	function isShow(obj,flag){
		var detail = document.getElementById("details"+flag);
		if("显示"==obj.title){
			obj.title="隐藏";
			obj.src="/images/tree_collapse.gif";
			detail.style.display="";
		}else{
			obj.title="显示";
			obj.src="/images/tree_expand.gif";
			detail.style.display="none";
		}
	}
	
	function delNodes(r5200,flag){
		var tmp=1;
		dels+=r5200+",";
		if("${paperQuestionTypeForm.type_id}"=="1"||"${paperQuestionTypeForm.type_id}"=="2"||"${paperQuestionTypeForm.type_id}"=="3"){
			//document.getElementById("details"+flag).style.display="none";
			document.getElementById("details"+flag).removeNode(true)
			tmp=2;
		}
		//document.getElementById("sel"+flag).style.display="none";
		document.getElementById("sel"+flag).removeNode(true)
		
		
		var _table=document.getElementById("tableid");
		for(var i=0;i<_table.rows.length;i++){
			if(i==0){
				var _html = _table.rows[i].cells[3].innerHTML;
				var _start = _html.indexOf("<A");
				var _end = _html.indexOf("</A>");
				var tmpHtml = _html.substr(_start,_end-_start+4);
				if(tmpHtml.indexOf("up01")!=-1){
					_table.rows[i].cells[3].innerHTML=_html.replace(tmpHtml,"&nbsp;&nbsp;&nbsp;");
				}
			}
			if(i==_table.rows.length-tmp){
				var _html = _table.rows[i].cells[3].innerHTML;
				var _start = _html.lastIndexOf("<A");
				var _end = _html.lastIndexOf("</A>");
				var tmpHtml = _html.substr(_start,_end-_start+4);
				if(tmpHtml.indexOf("down01")!=-1){
					_table.rows[i].cells[3].innerHTML=_html.replace(tmpHtml,"&nbsp;&nbsp;&nbsp;");
				}
			}
		}
	}
	
	
	
	function sortNodes(flag,rowid,obj){
		var _rowid=obj.parentNode.parentNode.rowIndex;
		var _table=document.getElementById("tableid");
		var _row1=_table.rows[_rowid];
		var _row2;
		if("up"==flag){
			if("${paperQuestionTypeForm.type_id}"=="1"||"${paperQuestionTypeForm.type_id}"=="2"||"${paperQuestionTypeForm.type_id}"=="3")
				_row2=_table.rows[_rowid-2];
			else
				_row2=_table.rows[_rowid-1];
		}else{
			if("${paperQuestionTypeForm.type_id}"=="1"||"${paperQuestionTypeForm.type_id}"=="2"||"${paperQuestionTypeForm.type_id}"=="3")
				_row2=_table.rows[_rowid+2];
			else
				_row2=_table.rows[_rowid+1];
		}
		if(_table.rows.length<3||_rowid<=1){
			var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
			var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
			_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
			_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
		}else{
			var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
			var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
			_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
			_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
		}
		if("up"==flag){
			if("${paperQuestionTypeForm.type_id}"=="1"||"${paperQuestionTypeForm.type_id}"=="2"||"${paperQuestionTypeForm.type_id}"=="3"){
				_table.moveRow(_rowid-2,_rowid+1);
				_table.moveRow(_rowid-2,_rowid+1);
			}else
				_table.moveRow(_rowid-1,_rowid);
		}else{
			if("${paperQuestionTypeForm.type_id}"=="1"||"${paperQuestionTypeForm.type_id}"=="2"||"${paperQuestionTypeForm.type_id}"=="3"){
				_table.moveRow(_rowid+2,_rowid);
				_table.moveRow(_rowid+3,_rowid+1);
			}else
				_table.moveRow(_rowid+1,_rowid);
		}
	}
	
	function saveQeuestEdit(){
		var r5200s="";
		var norders="";
		var r5200str = document.getElementsByName("tmpr5200");
		var norderstr = document.getElementsByName("tmpnorder");
		for(var i=0;i<r5200str.length;i++){
			r5200s+=r5200str[i].value+",";
			norders+=norderstr[i].value+",";
		}
		//if(!window.confirm("确认要修改吗？"))
		//	return;
		var hashvo=new ParameterSet();
		hashvo.setValue("r5200s",r5200s);
		hashvo.setValue("norders", norders);
        hashvo.setValue("dels", dels);
        hashvo.setValue("r5300", "${paperQuestionTypeForm.r5300}");
        hashvo.setValue("type_id", "${paperQuestionTypeForm.type_id}");
        var request=new Request({method:'post',onSuccess:ajaxok,functionId:'2020070007'},hashvo);
	}
	function ajaxok(outparamters){
		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			//returnValue="ok";
			//window.close();
			renturnUrl();
		}else{
			alert("操作失败！");
		}
	}
	
	function renturnUrl(){
		paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
		paperQuestionTypeForm.submit();
	}
</script>
<hrms:themes></hrms:themes>
<%int i=0;int m=0;%>
<html:form action="/train/trainexam/paper/questiontype" >
<bean:define id="type_id" name="paperQuestionTypeForm" property="type_id"></bean:define>
<table border="0" cellpadding="0" cellspacing="0" align="center" style="width: 567px;margin-left: 10px;margin-top: 10px;">
	<tr>
       <td align="left" class="TableRow" style="height: 30px;">
        &nbsp;&nbsp;&nbsp;<bean:write name="paperQuestionTypeForm" property="title"/>
       </td>
    </tr>
    <tr>
    	<td class="RecordRow" style="border-top:0px;">
    		<div style="overflow: auto;width: 800px;height: 380px;pading:0px;margin: 0px;margin-top:5px;">
	    		<table border="0" cellpadding="1" cellspacing="0" class="ListTableF" style="width: 100%;" id="tableid">
	    		 <hrms:paginationdb id="element" name="paperQuestionTypeForm" sql_str="paperQuestionTypeForm.strsql" table="" where_str="paperQuestionTypeForm.strwhere" columns="paperQuestionTypeForm.columns" order_by="paperQuestionTypeForm.order_by" page_id="pagination" allmemo="1" pagerows="${paperQuestionTypeForm.end}" indexes="indexes">
	    			<%i++; %>
	    			<tr id="sel<%=i %>" >
	    				<td width="16" height="35" style="border-top: 1px solid #C4D8EE;" class="common_border_color">
	    					<input type="hidden" name="tmpr5200" id="tmpr5200<%=i %>" value='<bean:write  name="element" property="r5200" filter="true"/>'/>
	    				<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
	    					<img src="/images/tree_expand.gif" title="显示" border="0" id="imgshow" onclick="isShow(this,<%=i %>);" style="vertical-align: top;"/>
	    				<%} %>
	    				</td>
	    				<td style="border-top: 1px solid #C4D8EE;" class="common_border_color">
	    					<bean:define id="strr5205" name="element" property="r5205" />
	    					第${indexes+1 }题(<bean:write name="element" property="r5213"/>分)&nbsp;&nbsp;<%=QuestionesBo.toHtml(strr5205.toString()) %>
	    				</td>
	    				<td align="center" width="22" style="border-top: 1px solid #C4D8EE;" class="common_border_color">
	    					<img src="/images/delete.gif" alt="删除" onclick="delNodes('<bean:write name="element" property="r5200" filter="false"/>',<%=i %>);"/>
	    				</td>
	    				<td align="center" width="35" style="border-top: 1px solid #C4D8EE;" class="common_border_color">
	    					<input type="hidden" name="tmpnorder" id="tmpnorder<%=i %>" value='<bean:write  name="element" property="norder" filter="true"/>'/>
							<bean:define id="end" name="paperQuestionTypeForm" property="end"/>
							<%if(i==1){ %>
							&nbsp;&nbsp;&nbsp;
							<%}else{ %>
							<a href="###" onclick="sortNodes('up',<%=m %>,this);">
								<img src="/images/up01.gif" width="12" height="17" border=0></a>
							<%}
							if(end.toString().equals(i+"")){ 
							%>
								&nbsp;&nbsp;&nbsp;
							<%} else{%>
							<a href="###" onclick="sortNodes('down',<%=m %>,this);">
								<img src="/images/down01.gif" width="12" height="17" border=0></a> 
							<%} %>
	    				</td>
	    			</tr>
	    			<%m++; 
	    			if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){
	    			%>
		    			<tr id="details<%=i %>" style="display:none; ">
		    				<td style="border-top: 1px solid #C4D8EE;" class="common_border_color">&nbsp;</td>
		    				<td colspan="3" style="border-top: 1px solid #C4D8EE;" class="common_border_color">
		    					<bean:define name="element" property="r5207" id="r5207"/>
		    					<hrms:examquestionsoptions xml="${r5207}" name_id="aaa${indexes}" type_id="${paperQuestionTypeForm.type_id}"></hrms:examquestionsoptions>
		    				</td>
		    			</tr>
		    			<%m++; 
		    		}%>
	    			
	    			
	    		</hrms:paginationdb>
	    		</table>
    		</div>
    	</td>
    </tr>
    <tr>
    	<td align="center" height="35">
    		<input type="button" value="确定" class="mybutton" onclick="saveQeuestEdit();"/>
    		<input type="button" value="返回" class="mybutton" onclick="renturnUrl();"/>
    	</td>
    </tr>
</table>
</html:form>