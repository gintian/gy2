<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.actionform.general.deci.leader.LeaderForm"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
LeaderForm leaderForm=(LeaderForm)session.getAttribute("leaderForm");
String a0100=PubFunc.encrypt(leaderForm.getCode());
String tabid=leaderForm.getCard_id();
String inforkind="2";
String returnvalue="";

%>

<%
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";

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
 function closeFunc(){
	 var returnvalue='<%=returnvalue%>';
	 if(returnvalue=='dxts'){
		 hrbreturn('selfinfo','il_body','');
	 }else{
		 top.close();
	 }
 }
 Ext.onReady(function(){
	 var cardFormProty={
	    'fieldpurv':'0',//指标权限是否按管理范围 0 否 1 是
	    'a0100':'<%=a0100%>',
	    'inforkind':'<%=inforkind%>',//模块类型
	    'Callbackfunc':'',
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
