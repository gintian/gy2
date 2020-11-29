<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String browser="Trident";
	String bosflag ="";
	String themes="default";
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	/*cmq added at 20121201 for jquery easyui $函数冲突问题*/
		browser=userView.getBrower();
		bosflag =userView.getBosflag();
		/*xuj added at 2014-4-18 for hcm themes*/
     themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
String isMobile=request.getParameter("isMobile");
   isMobile = isMobile==null?"":isMobile;
   pageContext.setAttribute("isMobile",isMobile);
	String _return = request.getParameter("return");
		   
	
%>
<!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
 
<%--    <script type="text/javascript" src="/ext/ext-all.js" ></script>--%>
<%--    <script type="text/javascript" src="/ext/ext-lang-zh_CN.js" ></script> --%>
<%--    <script type="text/javascript" src="../../ext/ext-additional.js" ></script> --%>
<%--    <script type="text/javascript" src="/ext/rpc_command.js"></script>--%>
<logic:notEqual value="1" name="isMobile">
<logic:notEqual value="2" name="isMobile">
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
<script language="javascript" src="/js/constant.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>

 

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
	var webserver=<%=flag%>;
</script>
</logic:notEqual>
	</logic:notEqual>
<script language="javascript">
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
 
</script>

</head>
<body style="margin:<%if("hcm".equals(bosflag)){ %>10<%}else{%>0<%}%> 0 0 5;" onKeyDown="return pf_ChangeFocus(event);" >
<table width="100%" height="90%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top">
       <hrms:insert parameter="HtmlBody"/>
    </td>
  </tr>
</table>

</body>
<logic:notEqual value="1" name="isMobile">
<logic:notEqual value="2" name="isMobile">
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script>
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
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument(); 
  //document.oncontextmenu = function(e) {return false;}
</script>
</logic:notEqual>
	</logic:notEqual>
</html>