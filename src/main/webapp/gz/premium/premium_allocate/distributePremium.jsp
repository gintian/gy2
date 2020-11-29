<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.premium.premium_allocate.*"%>
<%
	MonthPremiumForm form=(MonthPremiumForm)session.getAttribute("monthPremiumForm");
	String cardid = (String)form.getCardid();	
	
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
	
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
  <link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
  <hrms:themes />
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
	<%if(cardid.equals("")){%>
		alert('没有设置单位登记表！');
	<%}%>
</script>

<html:form action="/hire/demandPlan/positionDemand/unit_card">
<table>
<tr>
<td align="center">
<div id="card">
  <table>
     <tr>
       <td align="center">  
       <%if(!cardid.equals("")){%>    
         <hrms:ykcard name="monthPremiumForm" property="cardparam" istype="3"  nid="${monthPremiumForm.operOrg}" tabid="${monthPremiumForm.cardid}" cardtype="no" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="0" queryflag="5" infokind="2"   browser="<%=browser %>" />
         <%}%>
       </td>
    </tr>
  </table>
</div> 
</td>
</tr>
</table>
 </html:form>
