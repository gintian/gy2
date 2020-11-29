<!DOCTYPE html>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">

<%
String isMobile=(String)request.getParameter("isMobile");
	   isMobile=StringUtils.isNotEmpty(isMobile)?isMobile:"";
String userbase=(String)request.getParameter("userbase");
	   userbase=StringUtils.isNotEmpty(userbase)?userbase:"";
String inforkind=(String)request.getParameter("inforkind");
	   inforkind=StringUtils.isNotEmpty(inforkind)?("3".equals(inforkind)?"4":inforkind):"";
String userpriv=(String)request.getParameter("userpriv");//selfinfo 	
String flag=(String)request.getParameter("flag");//infoself 只查自己

String a0100=(String)request.getParameter("a0100");
	   a0100=StringUtils.isNotEmpty(a0100)?a0100:"";
	   if(a0100.startsWith("~")){//SafeCode.encode(PubFunc.convertTo64Base(a0100.toString()))
		   a0100=PubFunc.convert64BaseToString(SafeCode.decode(a0100.substring(1))); 
	   }else{
		   a0100=PubFunc.decrypt(a0100);
	   }
	   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	   if(StringUtils.isNotEmpty(userpriv)&&"selfinfo".equals(userpriv)
		||StringUtils.isNotEmpty(flag)&&"infoself".equals(flag)){
		   if(StringUtils.isEmpty(a0100)){
			   a0100=userView.getA0100();
		   }
	   }
	   if(StringUtils.isNotEmpty(a0100))
		   a0100=PubFunc.encrypt(userbase+"`"+a0100);
String tabid=(String)request.getParameter("tabid");
	   tabid=StringUtils.isNotEmpty(tabid)?tabid:"";
	   
String returnFlag=(String)request.getParameter("returnFlag");
	   returnFlag=StringUtils.isNotEmpty(returnFlag)?returnFlag:"";
String closeFlag=(String)request.getParameter("closeFlag");	   
	   closeFlag=StringUtils.isNotEmpty(closeFlag)?closeFlag:"";
%>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script language='JavaScript' src='../../../components/tableFactory/tableFactory.js'></script>
<script language="javascript" src="../../../general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="../../../module/card/card_property.js"></script>
<script language="JavaScript" src="../../../module/card/card_resource_zh_CN.js"></script>
<link rel="stylesheet" href="../../../css/css1_brokenline.css" type="text/css"><!-- 引用css样式 -->

<style>
.x-window-header-default-top{
	border-top-left-radius: 5px;
    border-top-right-radius: 5px;
    border-bottom-right-radius: 0px;
    border-bottom-left-radius: 0px;
    background-color: #ffffff;
    padding: 4px 5px 0px;
    border-width: 1px 1px 1px 0px;
    border-style: solid;
}
.x-message-box .x-window-body{
    background-color: #fafbfd;
    border-width: 0;
}
.x-btn-default-toolbar-small .x-frame-tl{
	background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-tc{
	background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-tr{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}

.x-btn-default-toolbar-small .x-frame-bl{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-br{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-bc{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}

.x-btn-over .x-btn-default-toolbar-small-ml,.x-btn-over .x-btn-default-toolbar-small-mr
	{
	background-image:
		url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-sides.gif)
}

.x-btn-over .x-btn-default-toolbar-small-mc {
	background-image: url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-fbg.gif)
}
</style>

<script>
Ext.Loader.setConfig({
         enabled: true,
         paths: {
             'Card': '/module/card'
         }
     });	
var isMobile='<%=isMobile%>';
var returnFlag='<%=returnFlag%>';
var closeFlag='<%=closeFlag%>';
 function closeFunc(){
	 if(isMobile=='1'){
			window.location.href="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&isGetData=1&clears=1&operateMethod=direct&modelFlag=3&returnflag=mobile";
	 }else if(isMobile=='2'){
			window.history.back(-1);
	 }
 }
 Ext.onReady(function(){
	 var cardFormProty={
	    'fieldpurv':'1',//指标权限是否按管理范围 0 否 1 是
	    'a0100':'<%=a0100%>',
	    'tabid':'<%=tabid%>',
	    'inforkind':'<%=inforkind%>',//模块类型
	    'Callbackfunc':closeFlag!=''?closeFlag:((isMobile!=''||returnFlag!='')?closeFunc:null),
	    'cardFlag':'1',
	    'isFitFlag':true
	 };
	 Ext.apply(cardGlobalBeanDefault,cardFormProty);
	 Ext.require('Card.SearchCards',function(){
          Ext.create("Card.SearchCards",{
        	    cardFormProty:cardGlobalBeanDefault//,//页面展现需要对象
         });
     });

	 
	 
});
</script> 
