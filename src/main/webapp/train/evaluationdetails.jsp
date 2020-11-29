<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

  <script type="text/javascript" src="../ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../ext/ext-all.js"></script>
<script type="text/javascript" src="../ext/rpc_command.js"></script>
  <script language="javascript" src="/js/validate.js"></script>
  <script language="javascript" src="/js/constant.js"></script>
  <SCRIPT LANGUAGE=javascript src="/templates/index/Portal2.js"></SCRIPT><%
  UserView userView = (UserView) session.getAttribute(WebConstant.userView);
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	int i = 0;
%>
<script language="JavaScript">
	function learn(courseid,classes,flag) {
		var map = new HashMap();
		map.put("r5000",courseid);
		Rpc({functionId:'2020030198'},map);
		if("1" == flag)
			var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=me`classes="+classes+"`lesson=" + courseid;
		else
			var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`classes="+classes+"`lesson=" + courseid;
		var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
		window.open(fram,'fullscreen=yes,learnwindow','left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
	}
	function goback(){
		var tar='<%=userView.getBosflag()%>';
		   if(tar=="hl"){//6.0首页
			   document.forms[0].action="/templates/index/portal.do?b_query=link";
			   document.forms[0].submit();	
		   }else if(tar=="hcm"){//7.0首页
		       document.forms[0].action="/templates/index/hcm_portal.do?b_query=link";
			   document.forms[0].submit();	
		   }
	}
	
	function isclose(){
		var url = "/templates/cclose.jsp";
        newwin=window.open(url,"_self","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	}
</script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
  </head>
  
  <body>
  <html:form action="/train/evaluationdetails">
    	<table border = "0" style="margin-top: 10px;" cellpadding="0" cellspacing="0" align="center" width="60%" class="ListTableF">
    		<tr align="center" >
    			<td class="TableRow" align="center">
    			
    				<logic:notEqual name="evaluationDetailsForm" property="flag" value="hot">
                          <bean:message key="train.ilearning.learningHint"/>
                        </logic:notEqual>
    				<logic:equal name="evaluationDetailsForm" property="flag" value="hot">
    				    <bean:message key="train.resource.course.setparam.hotcourse"/>
    				</logic:equal>
    			</td>
    		</tr>

    		   <hrms:extenditerate id="element" name="evaluationDetailsForm"
					property="msgPageForm.list" indexes="indexes"
					pagination="msgPageForm.pagination" pageCount="${evaluationDetailsForm.pagerows}" 
					 scope="session">
					  <logic:equal name="element" property="keyid" value="tra">
					        <tr class="trShallow">
	    		                   <td class="RecordRow">
	    			                  &nbsp;&nbsp;&nbsp;<b><bean:message key="train.resource.course.setparam.evaluation"/>：</b>
				    		       </td>
				    		</tr>
					 </logic:equal>
					  <logic:equal name="element" property="keyid" value="exa">
					        <tr class="trShallow">
	    		                   <td class="RecordRow">
	    			                  &nbsp;&nbsp;&nbsp;<b><bean:message key="train.resource.course.setparam.exam"/>：</b>
				    		       </td>
				    		</tr>
					 </logic:equal>
					  <logic:equal name="element" property="keyid" value="cla">
					        <tr class="trShallow">
	    		                   <td class="RecordRow">
	    			                  &nbsp;&nbsp;&nbsp;<b><bean:message key="train.resource.course.setparam.lesson"/>：</b>
				    		       </td>
				    		</tr>
					 </logic:equal> 
					 <logic:equal name="element" property="keyid" value="hot">
					        <tr class="trShallow">
	    		                   <td class="RecordRow">
	    			                  &nbsp;&nbsp;&nbsp;<b><bean:message key="train.resource.course.setparam.hotcourse"/>：</b>
				    		       </td>
				    		</tr>
					 </logic:equal>
					<%
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
					<td class="RecordRow">&nbsp;&nbsp;&nbsp;<bean:write name="element" property="content" filter="false" /> </td>
					</tr>
				</hrms:extenditerate> 
				<tr>
					<td>
						<table width="100%" align="center" class="" style="">
								<tr>
									<td valign="bottom" class="tdFontcolor noleft noright" style="border-bottom: none;">
									 	<hrms:paginationtag name="evaluationDetailsForm"
											pagerows="${evaluationDetailsForm.pagerows}"
											property="msgPageForm.pagination" scope="session"
											refresh="true"></hrms:paginationtag> 
						<!--					   <bean:message key="label.page.serial"/>
						<bean:write name="evaluationDetailsForm" property="msgPageForm.pagination.current" filter="true" />
						<bean:message key="label.page.sum"/>
						<bean:write name="evaluationDetailsForm" property="msgPageForm.pagination.count" filter="true" />
						<bean:message key="label.page.row"/>
						<bean:write name="evaluationDetailsForm" property="msgPageForm.pagination.pages" filter="true" />
						<bean:message key="label.page.page"/> -->
									</td>
									<td align="right" nowrap class="tdFontcolor">
									 <p align="right">
											<hrms:paginationlink name="evaluationDetailsForm"
												property="msgPageForm.pagination" nameId="msgPageForm">
											</hrms:paginationlink>
									</td>
								</tr>
							</table>
					</td>
				</tr>
    	</table>
    	<table border = "0" style="margin-top: 5px;" cellpadding="0" cellspacing="0" align="center" width="60%">
    		<tr align="center">
				
					<td style="border: none;">
					    <logic:equal name="evaluationDetailsForm" property="flag" value="hot">
					      <input type="button" name="tdf" value="<bean:message key="button.close" />"  class="mybutton" onclick="isclose();">
					    </logic:equal>
					    <logic:notEqual name="evaluationDetailsForm" property="flag" value="hot">
						  <input type="button" name="tdf" value="<bean:message key="button.return" />"  class="mybutton" onclick="goback();">
						</logic:notEqual>
					</td>
				</tr>
    	</table>
    	</html:form>
  </body>
</html>
