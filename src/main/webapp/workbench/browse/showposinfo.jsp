<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
<%String npage = (String)request.getParameter("npage");
	npage=StringUtils.isNotEmpty(npage)?npage:"";
	String infokind = (String)request.getParameter("infokind");
	String a0100=(String)request.getParameter("a0100");
	Pattern pattern = Pattern.compile("[0-9]*");
	Matcher isNum = pattern.matcher(a0100);
	boolean isNumberfalg=true;
	if( !isNum.matches() ){//判断是否是纯数字
		   isNumberfalg=false;
	}
	if(a0100.startsWith("~")){
		a0100=PubFunc.convert64BaseToString(SafeCode.decode(a0100.substring(1)));
	}else if(!isNumberfalg){
		a0100=PubFunc.decrypt(a0100);
	}
	String returnFLag=(String)request.getParameter("returnFlag");
	a0100=PubFunc.encrypt(a0100);
	String tabid = request.getParameter("tabid");
	tabid=StringUtils.isNotEmpty(tabid)?tabid:"";
%>

<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script language='JavaScript' src='../../../components/tableFactory/tableFactory.js'></script>
<script language="javascript" src="../../general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="../../module/card/card_property.js"></script>
<script language="JavaScript" src="../../module/card/card_resource_zh_CN.js"></script>
<link rel="stylesheet" href="../../css/css1_brokenline.css" type="text/css"><!-- 引用css样式 -->
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
	 var npage='<%=npage%>';
	 var returnFLag='<%=returnFLag%>';
	 if(npage=='1'){
		 top.close();
	 }else{
		 if(returnFLag=='1')
		    top.open('/workbench/browse/scaninfodata.do?b_query=link',"nil_body"); 
		 else if(returnFLag=='4'){
			 location.href='/workbench/browse/scaninfodata.do?br_return=link';
		 }
		 else
		    location.href='/workbench/browse/showinfodata.do?b_query=link';
	 }
 }
 Ext.onReady(function(){
	 var tabid='<%=tabid%>';
	 var cardFormProty=tabid!=''?{
	    'fieldpurv':'1',//指标权限是否按管理范围 0 否 1 是
	    'a0100':'<%=a0100%>',
	    'inforkind':'<%=infokind%>',//模块类型
	    'tabid':tabid,
	    'Callbackfunc':closeFunc,
	    'cardFlag':'1',
	    'isFitFlag':true
	 }:{
		  'fieldpurv':'1',//指标权限是否按管理范围 0 否 1 是
		  'a0100':'<%=a0100%>',
		  'inforkind':'<%=infokind%>',//模块类型
		  'Callbackfunc':closeFunc,
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
