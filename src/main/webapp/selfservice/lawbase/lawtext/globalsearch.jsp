<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>HRPWEB3</title>
		<script language="JavaScript" src="/js/validate.js"></script>
		<script language="JavaScript">
		
			function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
			
			function exeAdd(addStr)
			{
			    target_url=addStr;
			    window.open(target_url, '_self'); 
			}
			
			//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
			/*
			function document.oncontextmenu() 
			{ 
			  	return false; 
			} 
			*/
        </script>
		<link href="/css/css1.css" rel="stylesheet" type="text/css">
		<hrms:themes cssName="content.css"></hrms:themes>
	</head>
	<body onKeyDown="return pf_ChangeFocus(event);">
		<div width="100%" align="center" style="margin-top: 50px;">
			<form name="law_term_queryForm" method="post" action="/selfservice/lawbase/lawtext/globalsearch.do">
				<table width="700px" border="0" cellpadding="0" cellspacing="0" align="center">
					<tr>
						
						<td align="center" class="TableRow">&nbsp;
							&nbsp;全文检索&nbsp;
						</td>
					</tr>
					<tr >
						<td class="framestyle3" height="100px" align="center">
								  检索内容
								<input type="text" name="term" maxlength="50" size="30" value="" class="text4">
							    <span style="vertical-align:middle;">
									<input type="submit" Class="mybutton" name="b_query" value="<bean:message key="button.ok"/>">
									<input type="button"  class="mybutton" onclick="exeAdd('/selfservice/lawbase/lawtext/law_maintenance.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_base_id=" + request.getParameter("a_base_id"))%>')" value="<bean:message key="button.return"/>">
								</span>
								<input type="hidden" name="base_id" value="<%=PubFunc.encrypt(request.getParameter("a_base_id"))%>">
								<INPUT type="hidden" name="basetype" value="<bean:write name="lawbaseForm" property="basetype" filter="true"/>">
					    </td>
					</tr>
				</table>
			</form>
		<fieldset style="width:700px;">
          <legend >说明</legend>
         <pre>  全文检索支持的文件类型包括文本文件(.txt)，网页文件(.html)，Word文件(.doc)，Excel文件(.xls)</pre>
         <!-- pre>  全文检索支持逻辑操作符例如:
  查询文档中包括世纪和规章制度的所有文档可以写成 世纪 AND 规章制度
  查询文档中包括世纪或者包括规章制度的所有文档可以写成 世纪 OR 规章制度</pre -->
    	</fieldset>
		</div>
	</body>
</html>
