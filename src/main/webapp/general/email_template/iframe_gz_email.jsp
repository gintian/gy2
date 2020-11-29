<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
%>
<%

	String isEncode = (String)request.getParameter("isEncode");//获取加密参数
	String src = (String)request.getParameter("src");

	if(isEncode != null && "1".equals(isEncode)){//加密参数存在且值为1时,需要进行解密 wangb 20180308
		src = SafeCode.decode(src);
		src = PubFunc.hireKeyWord_filter_reback(src);
		src = src.replaceAll("[+]", "%2B");// + 号字符 进行转码
	}
	String sSourceURL = src.replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;  //20140901  dengcan
	if(isEncode == null)//不加密,走原来
		sSourceURL=sSourceURL.replaceAll("`","&");
	if((sSourceURL.toLowerCase().indexOf("script")!=-1 && sSourceURL.toLowerCase().indexOf("<")!=-1)
			|| sSourceURL.toLowerCase().indexOf("javascript:")!=-1)//20170717 dengcan
		throw new Exception("error page!");
	//此处存在安全问题，如果src是危险网站，会泄漏信息。此处判断如果是重定向的连接，
	//判断域名端口是否一致，否则是引入外部连接，禁止访问 guodd 2016-12-19
	if(sSourceURL.toLowerCase().indexOf("http")!=-1 || sSourceURL.indexOf("HTTP")!=-1){
		StringBuffer reUrl = request.getRequestURL();
		String local = reUrl.substring(0, reUrl.indexOf("/general"));
		if(!sSourceURL.toLowerCase().startsWith(local.toLowerCase()))
			throw new Exception("error page!");

	}

	if(sSourceURL.trim().startsWith("//"))
		throw new Exception("error page!");
	//参数加密处理，防止高位字符导致请求被掐断 guodd 2019-06-28
	if(sSourceURL.indexOf("?")>0){
		sSourceURL = sSourceURL.substring(0, sSourceURL.indexOf("?")+1)+"encryptParam="+PubFunc.encrypt(sSourceURL.substring(sSourceURL.indexOf("?")+1));
	}
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>

</head>
<hrms:themes/>
<body style="margin: 0px;overflow: auto;">
<iframe name="childFrame" id="childFrame" frameborder="0" height="100%" width="100%" src="<%=sSourceURL%>"></iframe>

</body>
</html>
<script>
	/**取非兼容性ie11版本*/
	function getIE11Version(){
		if(navigator.appName.indexOf("Netscape") != -1){
			var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
			var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
			if(isIE11)
				return true;
		}

		return false;
	}
	if(getIE11Version()){//wangz 2019-03-04 解决ie11出现滚动条问题
		document.getElementById("childFrame").style.height = "99%";
	}
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isEdge = userAgent.indexOf("Edge") > -1;
	if(isEdge){// bug 49015 edge 登记表界面出现双层滚动条修改
		document.getElementById('childFrame').height="99%";
	}
</script>