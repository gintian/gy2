
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	 <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
	 <link href="/css/diary.css" rel="stylesheet" type="text/css">
	 <!--link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" / -->
	<script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="../../ext/ext-all.js"></script>
	<script type="text/javascript" src="../../ext/rpc_command.js"></script> 
	<script type="text/javascript" src="Portal.js"></script>
	<script type="text/javascript" src="Portal2.js"></script>
    <script type="text/javascript" src="/js/constant.js"></script>
	<link rel="stylesheet" type="text/css" href="portal.css" />	  	
	<style type="text/css">
		.x-panel-tl{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) repeat-x 0 0;padding-left:6px;zoom:1;border-bottom:1px solid #99bbe8;}
		.x-panel-tr{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) no-repeat right 0;zoom:1;padding-right:6px;}
		.x-panel-tc{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) repeat-x 0 0;overflow:hidden;}
		.x-panel-tl{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) repeat-x 0 0;padding-left:6px;zoom:1;border-bottom:1px solid #99bbe8;}
		.x-panel-tr{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) no-repeat right 0;zoom:1;padding-right:6px;}
	
    </style>	 	
	<style type="text/css">
        .x-panel-mc .x-panel-body {
            background:white;
            /*background-color:#F4F7F7;
            background-image:url(/images/tal_jb.png);*/
            line-height: 50px;          
            border:0;  
            background-repeat:repeat-x;  
			text-align: left;			
			background-position:top right; 
        }
       .my-icon{ background-image: url(../../images/hmc.gif) 0 6px no-repeat !important; }  
       .x-panel-header-text {
       	font-size:12px
       	font:bold 12px tahoma,arial,verdana,sans-serif;
       }  
       body a:link{color:#999;}
    </style>	
<script type="text/javascript">
<!--
	function openlink(url)
	{
    	//var dbpre=Ext.get('dbpre').value //$F("dbpre");
    	var hidobj=document.getElementById('dbpre');
    	var dbpre=hidobj.value;    	
    	url=url+"&dbpre="+dbpre;
    	window.open(url,"_self","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	}
//-->
</script>
<html:form action="/templates/index/submainpanel">
	<html:hidden property="fromUrl" name="sysForm"/>
	<html:hidden property="fromModid" name="sysForm"/> 
</html:form>   
<html:form action="/templates/index/subportal">
<br>
<input id="htmlparam" type="hidden" name="html_param"/>
<html:hidden name="homeForm" property="dbpre"/>

<script type="text/javascript">
function expendChildMenu(imgobject,id){
	var obj=document.getElementById(id);
	if(obj.style.display=="block")
	{
		obj.style.display="none";
		imgobject.src="/images/tree_expand.gif";
    }
	else
	{
		obj.style.display="block";
		imgobject.src="/images/tree_collapse.gif";
	}
}
</script>
<table border="0" width="100%" cellspacing="0" cellpadding="0" align="center">
<tr><td align="center">
<hrms:extportal portalid="04"/>	
  	<hrms:sysmessage></hrms:sysmessage>
</td></tr></table>
	
</html:form>
<script language="javascript">
	function setNavigation(fromurl,frommodeid)
	{
		document.sysForm.fromUrl.value=fromurl;
		document.sysForm.fromModid.value=frommodeid;
		document.sysForm.action="/templates/index/submainpanel.do?b_query=link&amp;module=-1";
		document.sysForm.submit();
	}

</script>