<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.general.sprelationmap.RelationMapForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	RelationMapForm relationMapForm=(RelationMapForm)session.getAttribute("relationMapForm");
	String userbase=relationMapForm.getNbase();
	
	 String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
%>
<hrms:themes></hrms:themes>
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
    var paraArray=parent.dialogArguments; 
</script>

<html:form action="/general/sprelationmap/relation_map_drawable">
<table>
<tr>
<td align="center">
<div id="card">
  <table>
     <tr>
       <td align="center">      
         <hrms:ykcard name="relationMapForm" property="cardparam" istype="3" nid="${relationMapForm.objid}" tabid="${relationMapForm.tabid}"  userbase="<%=userbase%>" cardtype="no" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="1" queryflag="5" infokind="${relationMapForm.infokind}"   browser="<%=browser %>" />
       </td>
    </tr>
  </table>
</div> 
</td>
</tr>
<tr>
<td  align="left">
<input type="button" name="cc" onclick="window.close();" class="mybutton" value="<bean:message key="button.close"/>"/>
</td>

</tr>
</table>
 </html:form>