<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag ="";
    String themes="default";
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag =userView.getBosflag();
        /*xuj added at 2014-4-18 for hcm themes*/
     themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
	response.setHeader("Pragma","No-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0);	
	
%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<META HTTP-EQUIV="pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate"> 
<META HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><% if(userName!=null){ %>　用户名：<%=userName%> <% } %>　当前日期：<%=date%></title>

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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/js/constant.js"></script>
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
  document.oncontextmenu = function() {return false;}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*oncontextmenu = "return false"*/
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  <script language="javascript">
    hcm_tabset_root="/images/hcm/themes/<%=themes %>/content/";
  </script>
  <%} %>
</head>
<body style="margin:0 0 0 0" onKeyDown="return pf_ChangeFocus(event);" >
<table width="100%" height="90%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" style="overflow:auto">
       <hrms:insert parameter="HtmlBody" />
    </td>
  </tr>
</table>

</body>
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31R
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
  document.oncontextmenu=function() {  
		expr_editor=$('date_box');
		tyle=document.all.date_panel.style;
		if (startCell==null||startCell.name==null||endCell==null||endCell.name==null){
			document.getElementById("b_2").disabled="true";
			document.getElementById("b_3").disabled="true";
		}else{
			document.getElementById("b_2").disabled="";
			document.getElementById("b_3").disabled="";
		}
		tyle.position="absolute";
		tyle.display="";
		rightmouseflag=1;	
		//加上ie 9/8/7兼容模式下页面滚动后取不到光标正确的位置
		if(isCompatibleIE()){
			tyle.top=(document.documentElement.scrollTop+event.clientY)+"px";
			tyle.left=(document.documentElement.scrollLeft+event.clientX)+"px";
		}
		else{
			tyle.top=(document.body.scrollTop+event.clientY)+"px";
			tyle.left=(document.body.scrollLeft+event.clientX)+"px";
		}
		expr_editor.focus();
		return false;
	   
	}
</script>
</html>