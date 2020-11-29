<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %> 
   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>测试内容管理</title>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
</head>
<style  id="iframeCss">
div{
cursor:hand;font-size:12px;
}
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>



<script language="javascript">


function show(eventdiv,showdiv)
{
	with(eventdiv)
	{
		x=offsetLeft;
		y=offsetTop;
		objParent=offsetParent;
		while(objParent.tagName.toUpperCase()!= "BODY")
		{
			x+=objParent.offsetLeft;
			y+=objParent.offsetTop;
			objParent = objParent.offsetParent;
		}
		y+=offsetHeight-1;
	}

	with(showdiv.style)
	{
		pixelLeft=x;
		pixelTop=y;
		visibility='';
	}
}
function hide(hidediv)
{
	hidediv.style.visibility='hidden';
}

function getcontent(chl_no)
{    
     var hashvo=new ParameterSet();
	alert(chl_no);
     hashvo.setValue("chl_no",chl_no);	
      
   　 var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'1010021201'},hashvo);        
}

function isSuccess(outparamters)
{
	var cms_txt=outparamters.getValue("cms_txt");
	alert(cms_txt);
	AjaxBind.bind($('cms_pnl'),cms_txt);
}
</script>
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
<body topmargin="0" bottommargin="0" style="background: #fffff">
<hrms:cms_channel chl_no="8"></hrms:cms_channel>
	<DIV id="cms_pnl"></DIV>
</body>
<script language="javascript">
  initDocument();
</script>
</html>