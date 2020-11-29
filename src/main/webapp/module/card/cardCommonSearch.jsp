<!DOCTYPE html>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<%
	String fieldpriv=request.getParameter("fieldpriv");
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   String a0100=request.getParameter("a0100");
   String userbase=request.getParameter("userbase");
   String inforkind = request.getParameter("inforkind");
	if (StringUtils.isNotEmpty(a0100)) {
		//默认 PubFunc.encrypt(人员库+a0100);不处理 其余格式处理 
		if (a0100.startsWith("~")) {
			a0100 = PubFunc.convert64BaseToString(SafeCode.decode(a0100.substring(1)));
		} else if ("self".equals(a0100)) {
			a0100 = userView.getDbname() + "`" + userView.getA0100();
		}else {
			if(!"2".equals(inforkind)&&!"4".equals(inforkind)&&!"6".equals(inforkind)){
				a0100=PubFunc.decrypt(a0100);
			}else{
				//单位 岗位 岗位说明书登记表 a0100会有不加密的情况
				if(!StringUtils.isNumeric(a0100)){
					a0100=PubFunc.decrypt(a0100);
				}
			}
		}
	}

	String bizDate = request.getParameter("bizDate");

	String tabid = request.getParameter("tabid");

	

	String plan_id = request.getParameter("plan_id");

	String temp_id = request.getParameter("temp_id");

	String callbackfunc = request.getParameter("callbackfunc");

	String cardFlag = request.getParameter("cardFlag");//新增标记
	cardFlag = (cardFlag != null && cardFlag.length() > 0 ? cardFlag : "");
	String renderTo = request.getParameter("renderTo");
	try {
	    //配置链接查看操作人自己信息时或者查看我的薪酬时不校验指标权限 否则走指标权限
	    if(StringUtils.isEmpty(fieldpriv)){
		    if("self".equalsIgnoreCase(request.getParameter("a0100"))||"7".equals(inforkind)){
		        fieldpriv = "0";
		    }
	    }
		fieldpriv = PubFunc.encrypt((fieldpriv != null && fieldpriv.length() > 0 ? fieldpriv : "1"));
		a0100 = PubFunc.encrypt((a0100 != null && a0100.length() > 0 ? a0100 : ""));
		bizDate = (bizDate != null && bizDate.length() > 0 ? bizDate : "");
		tabid = (tabid != null && tabid.length() > 0 ? tabid : "");
		inforkind = (inforkind != null && inforkind.length() > 0 ? inforkind : "");
		plan_id = (plan_id != null && plan_id.length() > 0 ? plan_id : "");
		temp_id = (temp_id != null && temp_id.length() > 0 ? temp_id : "");
		callbackfunc = (callbackfunc != null && callbackfunc.length() > 0 ? callbackfunc : "");
		renderTo = (renderTo != null && renderTo.length() > 0 ? renderTo : "");
	} catch (Exception e) {

	}
	String url = SystemConfig.getCsClientServerURL(request);
	String hcmFlag = userView.getBosflag().toLowerCase();
%>
<script language="JavaScript" src="../utils/js/template.js"></script>
<script language="JavaScript" src="../gz/gz_resource_zh_CN.js"></script><!--未引入，工资资源文件 -->
<script language='JavaScript' src='../../../components/tableFactory/tableFactory.js'></script>
<script language="javascript" src="../../general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="card_property.js"></script>
<script language="JavaScript" src="../../jquery/jquery-3.5.1.min.js"></script>
<link rel="stylesheet" href="../../css/css1_brokenline.css" type="text/css"><!-- 引用css样式 -->
<script>
 Ext.Loader.setConfig({
         enabled: true,
         paths: {
             'Card': '/module/card'
         }
     });	 
	 
 Ext.onReady(function(){
	 var card_me=this;
	 var cardFormProty={
	    'fieldpurv':'<%=fieldpriv%>',//指标权限是否按管理范围 0 否 1 是
	    'a0100':'<%=a0100%>',
	    'bizDate':'<%=bizDate%>',//业务日期
	    'tabid':'<%=tabid%>',
	    'inforkind':'<%=inforkind%>',//模块类型
	    'plan_id':'<%=plan_id%>',//绩效所需参数
	    'temp_id':'<%=temp_id%>',//绩效所需参数
	    'Callbackfunc':'<%=callbackfunc%>',
	    'cardFlag':'<%=cardFlag%>',
	    'url':'<%=url%>',//打印预览所用参数
	    'hcmFlag':'<%=hcmFlag%>',
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
<body>
<div id="axContainer" style="display:none" ></div>
</body>

