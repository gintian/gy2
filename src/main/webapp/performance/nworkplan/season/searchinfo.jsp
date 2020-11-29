<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
int i = 0;
%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'searchinfo.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <script type="text/javascript">
    //点击查询按钮
  	function searchseason(){
  		var content = document.getElementById("content").value;
  		if("" == content){
  		if("${newworkplanForm.type}" == 1){
  			newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_search=link&type=1";
  		  }else if("${newworkplanForm.type}" == 2){
  		  	newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_search=link&type=2";
  		  }
  		}else{
  		if("${newworkplanForm.type}" == 1){
  			newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_search=link&type=1&content="+content+"&islike=true";
  		   }else if("${newworkplanForm.type}" == 2){
  		    newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_search=link&type=2&content="+content+"&islike=true";
  		   }
  		}
  			newworkplanForm.submit();
  	}
  	//从季报进入查询时点击内容列跳转
  	function hrefseason(year,season){
  		newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_query=link&type=1&islike=true&year="+year+"&season="+season;
  		newworkplanForm.submit();
  	}
  	//从年报进入查询时点击内容列跳转
  	function hrefyear(year){
  		newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_query=link&type=2&islike=true&year="+year;
  		newworkplanForm.submit();
  	}
  	//季报 返回按钮
  	function returnseason(){
  		newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_query=link&type=1";
  		newworkplanForm.submit();
  	}
  	//年报 返回按钮
  	function returnyear(){
  		newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_query=link&type=2";
  		newworkplanForm.submit();
  	}
  </script>
  <body>

  	<html:form action="/performance/nworkplan/searchquarters" method="post">
    	<table class="ListTable" width="100%" cellpadding="0" cellspacing="0" >
    		<tr>
    			<td colspan="3">
    				附件标题: <html:text name="newworkplanForm" property="content" styleId="content" size="10"/>&nbsp;
    				<input type="button" value="查询" class="mybutton" onclick="searchseason();" />
    				<logic:equal name="newworkplanForm" property="type" value="1">
	    				<input type="button" value="返回" class="mybutton" onclick="returnseason();" />
    				</logic:equal>
    				<logic:equal name="newworkplanForm" property="type" value="2">
    					<input type="button" value="返回" class="mybutton" onclick="returnyear();" />
    				</logic:equal>
    				<logic:equal name="newworkplanForm" property="type" value="3">
    					<input type="button" value="返回" class="mybutton" onclick="returnseason();" />
    				</logic:equal>
    				<logic:equal name="newworkplanForm" property="type" value="4">
    					<input type="button" value="返回" class="mybutton" onclick="returnseason();" />
    				</logic:equal>
    			</td>    			
    		</tr>
    		<tr>
    			<td colspan="3">
    				&nbsp;
	    		</td>
    		</tr>
    		<tr>
    			<td class="TableRow" width="10%" align="center" nowrap>
    				序号
    			</td>
    			<td class="TableRow" width="60%" align="center" style="border-left:0;" nowrap>
    				附件标题
    			</td>
    			<td class="TableRow" width="60%" align="center" style="border-left:0;" nowrap>
    				周期
    			</td>
    		</tr>
    		<hrms:paginationdb id="element1" name="newworkplanForm" 
    				allmemo="1" sql_str="newworkplanForm.sql" table="" 
            		where_str="newworkplanForm.where"
            		columns="newworkplanForm.cols" 
           		    pagerows="${newworkplanForm.pagerows}" page_id="pagination" indexes="indexes" >           		
            		 <% if(i%2==0){ %>
		            	 <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'E4F2FC')" height="70px">
		             <%  }else{ %>
		            	 <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')" height="70px">
		             <%}
		             	i++;
		             %>
		             <td class="RecordRow" style="border-top:0px;width:15%;cursor:pointer;">
		             	&nbsp;${indexes + 1}
		             </td>
		             <logic:equal name="newworkplanForm" property="type" value="1">
			             <bean:define id="contents" name="element1" property="name" type="java.lang.String"></bean:define>
			             <td class="RecordRow" style="border-top:0px;border-left:0px;width:60%;cursor:pointer;" onclick="hrefseason(<bean:write name='element1' property='ayear'/>,<bean:write name='element1' property='time'/>);" title="<%=QuestionesBo.toHtml(contents) %>">
			             		<%  
			             			contents = NewWorkPlanBo.checkContent(contents);
			             		%>
			             		&nbsp;<%=QuestionesBo.toHtml(contents) %>
			             </td>
		             </logic:equal>
		             <logic:equal name="newworkplanForm" property="type" value="2">
		             	<bean:define id="contents" name="element1" property="name" type="java.lang.String"></bean:define>
		             	<td class="RecordRow" style="border-top:0px;border-left:0px;width:60%;cursor:pointer;" onclick="hrefyear(<bean:write name='element1' property='ayear'/>);" title="<%=QuestionesBo.toHtml(contents) %>">
			             		<%  
			             			contents = NewWorkPlanBo.checkContent(contents);
			             		%>
			             		&nbsp;<%=QuestionesBo.toHtml(contents) %>
			             </td>
		             </logic:equal>
		             <logic:equal name="newworkplanForm" property="type" value="4">
		             	 <bean:define id="contents" name="element1" property="content" type="java.lang.String"></bean:define>
		            	 <td class="RecordRow" style="border-top:0px;border-left:0px;width:60%" onclick="" title="<%=QuestionesBo.toHtml(contents) %>">
			             		<%  
			             			contents = NewWorkPlanBo.checkContent(contents);
			             		%>
			             		&nbsp;<%=QuestionesBo.toHtml(contents) %>
			             </td>
		             </logic:equal>
		             <logic:equal name="newworkplanForm" property="type" value="1">
			    		<td class="RecordRow" style="border-top:0px;border-left:0px;width:25%;cursor:pointer;" >
							&nbsp;<bean:write name="element1" property="ayear"/>年第<bean:write name="element1" property="time"/>季
							<logic:equal name="element1" property="log_type" value="1">计划</logic:equal>
							<logic:equal name="element1" property="log_type" value="2">总结</logic:equal>
			    			(<bean:write name="element1" property="startmonth"/>月-
			    			<bean:write name="element1" property="endmonth"/>月)
			    		</td>
		    		</logic:equal>
		    		<logic:equal name="newworkplanForm" property="type" value="2">
		    			<td class="RecordRow" style="border-top:0px;border-left:0px;width:30%;cursor:pointer;">
		    				&nbsp;<bean:write name="element1" property="ayear"/>年
		    				<logic:equal name="element1" property="log_type" value="1">计划</logic:equal>
							<logic:equal name="element1" property="log_type" value="2">总结</logic:equal>
		    			</td>
		    		</logic:equal>
		    		<logic:equal name="newworkplanForm" property="type" value="3">
		    			<td class="RecordRow" style="border-top:0px;border-left:0px;width:30%">
		    				&nbsp;<bean:write name="element1" property="ayear"/>年第
		    				<bean:write name="element1" property="time"/>周
		    				<logic:equal name="element1" property="log_type" value="1">计划</logic:equal>
							<logic:equal name="element1" property="log_type" value="2">总结</logic:equal>
							(<bean:write name="element1" property="startmonth"/>月
							 <bean:write name="element1" property="startday"/>日 - 
							 <bean:write name="element1" property="endmonth"/>月
							 <bean:write name="element1" property="endday"/>日)
		    			</td>
		    		</logic:equal>
		    		<logic:equal name="newworkplanForm" property="type" value="4">
		    			<td class="RecordRow" style="border-top:0px;border-left:0px;width:30%">
		    				&nbsp;<bean:write name="element1" property="ayear"/>年第
		    				<bean:write name="element1" property="amonth"/>月
		    				<logic:equal name="element1" property="log_type" value="1">计划</logic:equal>
							<logic:equal name="element1" property="log_type" value="2">总结</logic:equal>
		    			</td>
		    		</logic:equal>
		    		</tr>
		    </hrms:paginationdb>
		    <tr>
		    	<td colspan="3">
		    	<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="newworkplanForm"
								pagerows="${newworkplanForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="newworkplanForm"
									property="pagination" nameId="newworkplanForm" scope="page">
								</hrms:paginationdblink>
								</p>
						</td>
					</tr>
  				</table>
		    	</td>
		    </tr>
    	</table>
    </html:form>
  </body>
</html>
