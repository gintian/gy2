<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	String path = request.getParameter("path");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><% if(userName!=null){ %>　用户名：<%=userName%> <% } %>　当前日期：<%=date%></title>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language=javascript src="/js/xtree.js"></script> 
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


//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*oncontextmenu = "return false"*/

function saverange()
{
	 var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    
 	if(currnode.icon=="/images/book.gif") {
 		alert("不能划转到无效目录");
 		return;
 	}
 	var id = currnode.uid;
    <%if(!"transfer".equals(path)){%>
    if(id=="root")
	{
		alert("不能选择根节点");
		return false;
	}
	<%}%>
	if(navigator.appName.indexOf("Microsoft")!= -1){
	    window.returnValue = id;
	    window.close();
    }else{
    	top.returnValue = id;
	    top.close();
    }
}
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">

<style>
<!--
.RecordRow1 {
	border: inset 1px;
	BORDER-BOTTOM:  1pt solid;
	BORDER-LEFT: 1pt solid; 
	BORDER-RIGHT: 1pt solid; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
-->
</style>
</head>
<div class="fixedDiv3">
<hrms:themes cssName="content.css"></hrms:themes>
<body onKeyDown="return pf_ChangeFocus(event);" >
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0"  >
  <tr>  
    <td valign="top">
		<html:form action="/selfservice/lawbase/law_maintenance">
			<table width="100%" border="0" cellspacing="0" style="margin-top: 10px;" align="center" cellpadding="0" class="ListTable">
				<thead>
		        	<tr>
		        		<td align="center" class="TableRow" nowrap>
		        		<%if(!"transfer".equals(path)){%>
							<bean:message key="wd.lawbase.afreshrange"/>&nbsp;&nbsp;
						<%}else{ %>
							目录划转
						<%} %>
		        		</td>
		        	</tr>
				</thead>
				<tr>
					<td align="left" class="RecordRow1" style="border-top:none;" nowrap>
						<div id="treemenu" style="height:200px;width:expression(document.body.clientWidth-22);overflow: auto;"></div>
					</td>
				</tr>
				<tr>
					<td align="center"  nowrap style="height: 35">
						<input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" saverange()">
						<input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick=" window.close();">
					</td>
				</tr>
			</table>
		</html:form>
    </td>
  </tr>
</table>

</body>
</div>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
<SCRIPT LANGUAGE=javascript>
var caption = "";
var basetype = window.dialogArguments;
if (basetype == 1) {
    caption = "<bean:message key="menu.rule"/>";
}
if (basetype == 4) {
    caption = "<bean:message key="law_maintenance.peoplecode"/>";
}if (basetype == 5) {
    caption = "<bean:message key="law_maintenance.file"/>";
}

	var m_sXMLFile	= "/selfservice/lawbase/get_lawbase_strut_tree.jsp?params=<%=PubFunc.encrypt("base_id=up_base_id")%>&flag=1&basetype="+basetype;
	var root=new xtreeItem("root",caption,"","mil_body",caption,"/images/add_all.gif",m_sXMLFile);
	
	root.setup(document.getElementById("treemenu"));
</SCRIPT>
</html>