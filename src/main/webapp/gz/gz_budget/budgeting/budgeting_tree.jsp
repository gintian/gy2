<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.BudgetingForm" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
	VersionControl ver = new VersionControl();
%>

<html>
<head>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<LINK href="<%=css_url%>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
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
<script language="javascript" src="/gz/gz_budget/budgeting/budgeting.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
</script>
<script type="text/javascript">
function initFirst()
{
     initDocument();
    if(root.getFirstChild())
    {
       if(root.getFirstChild().uid=='-1'){
          root.getFirstChild().expand();
	      var objfirst=root.getFirstChild().childNodes[0];
	      objfirst.select();
          selectedClass("treeItem-text-"+objfirst.id); 
       }else{
          root.getFirstChild().select();
          selectedClass("treeItem-text-"+root.getFirstChild().id);
       }
    }
}
</script>
</head>
<body>
<html:form action="/gz/gz_budget/budgeting/budgeting_tree">  
    <table align="left" width="800" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
	<tr align="left"  class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left">
		

			&nbsp;<hrms:priv func_id="324200201">
			<img src="/images/compute.gif" alt="<bean:message key='button.computer'/>" style="cursor:pointer" border="0" onclick="computer();"></img>			
			</hrms:priv>


			<%-- 
			 <hrms:priv func_id="324200202">	
			&nbsp;<img src="/images/export.gif" alt="<bean:message key='button.export'/>" style="cursor:pointer" border="0" onclick="exportD();"></img>			                
			</hrms:priv>
			--%> 
			<hrms:priv func_id="324200203">	
			&nbsp;<img src="/images/sb.gif" alt="<bean:message key='label.hiremanage.status2'/>" style="cursor:pointer" border="0" onclick="reportSp();"></img> 
			</hrms:priv>
			
			</td>
			</tr>
			<tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE="javascript">                 
               <bean:write name="budgetingForm" property="treeJs" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>   
</html:form>
<script language="javascript">
  initFirst();
</script>
</body>
</html>