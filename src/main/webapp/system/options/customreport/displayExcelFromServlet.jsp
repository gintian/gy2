<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
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
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
	request.setCharacterEncoding("GBK");	

	
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
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
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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

<style media="print">
<!--
	.noprint{display:none;};
-->
</style>
<style>
<!--
	.lin{border-bottom:1px groove black} 
-->
</style>
<script type="text/javascript">
<!--
	<%String html = request.getParameter("html");
	if (html == null || html.length() <=0 ) {
		html = (String)request.getAttribute("html");
	} else {
		html = SafeCode.decode(html);
	}
	
	%>
//-->
</script>
<body style="margin:0px;padding:0px;overflow: hidden;">
<div id="htmlparamdiv">
<%=html %>
</div>
<%//request.getAttribute("html") %>
<%	String filename = request.getParameter("filename");
	if (filename == null || filename.length() <= 0) {
		filename = (String) request.getAttribute("filename");
	}
	//filename = URLEncoder.encode(filename);
%>
<iframe id="fram" name="bb" frameborder="0" scrolling="no" style="border: 0px;margin:0px;padding:0px;overflow: hidden;" src="/system/options/customreport/displayExcel4.jsp?filename=<%=filename %>" width="100%"></iframe>
<object id="WebBrowser" width=0 height=0 classid="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></object>
<script>
	function downLoadExcel () {
		window.open("/servlet/DisplayOleFile?filename=<%=SafeCode.encode(PubFunc.encrypt(filename)) %>");
	}
	setHight();
	function setHight() { 
		var obj = document.getElementById("fram");
		var bodyHeight = document.body.offsetHeight;
		var divHeight = document.getElementById("div")==null?0:document.getElementById("div").offsetHeight;//changxy 20161026 23746 薪资管理 薪资分析 薪资分析表 打开用户自定义表 ‘员工薪资带宽表’报错 后台拼html为空时没有div元素 无法计算高度
		obj.height = bodyHeight - divHeight;
	}
</script>

<div id='wait' style='position:absolute;display:none;z-index: 999999;'>
	<iframe style="border-width: 0px; width: 311; height: 88;" src="scroll.html" frameborder="no" border="0" >
  
        </iframe>
</div>
</body>
<script>
	
	function openwin(){
		var tabid = document.getElementById("customid");
	    var hashvo=new ParameterSet();
	    hashvo.setValue("ispriv","1");	
       	hashvo.setValue("id",tabid.value);
       	
       	var frmID=document.getElementById("form1"); 
		var i, and = "";
		var item; // for each form's object
		var itemValue;// store each form object's value
		for( i=0;i<frmID.length;i++ ) {
			item = frmID[i];// get form's each object
			if ( item.name!='' ) {
				if ( item.type == 'select-one' ) {
					itemValue = item.options[item.selectedIndex].value;
				} else if ( item.type=='checkbox' || item.type=='radio') {
					if ( item.checked == false ) {
						continue;    
					}
					itemValue = item.value;
				} else if ( item.type == 'button' || item.type == 'submit' || item.type == 'reset' || item.type == 'image') {// ignore this type
					continue;
				} else	{
					itemValue = item.value;
				}

				//itemValue = encodeURIComponent(itemValue);
				//queryString += and + item.name + '=' + itemValue;
				hashvo.setValue(item.name,itemValue);

              }

       }   		
     	var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        var top = document.getElementById("div");   
        waitInfo.style.display="block";
        waitInfo.style.top=(screen.availHeight - top.clientHeight-100)/2;
        waitInfo.style.left=(screen.availWidth-waitInfo.clientWidth)/2;
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelectexcel,functionId:'10100103414'},hashvo);
	   
	}
	
	function showSelectexcel(outparamters) {
	     var waitInfo=eval("wait");	   
	     waitInfo.style.display="none";
	     var url = outparamters.getValue("url");
	     var filename = outparamters.getValue("filename");
	     url = url + "?filename=" + filename;
	     var html = document.getElementById("html");
	     html.value = outparamters.getValue("htmlparam");
	     var form1 = document.getElementById("form1");
	     form1.action = url;
	     form1.submit();
	     //var divObj = document.getElementById("htmlparamdiv");
	     //divObj.innerHTML = getDecodeStr(outparamters.getValue("htmlparam"));
	     //var fraObj = document.getElementById("fram");
	     //fraObj.src = "/system/options/customreport/displayExcel.jsp?filename=" + filename;
	   
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
</script>