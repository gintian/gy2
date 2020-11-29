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
//url特殊字符还原处理 dengcan 2014-9-26 end 
//  String sSourceURL = PubFunc.hireKeyWord_filter_reback((String)request.getParameter("src"));  //20140901  dengcan
//  sSourceURL=sSourceURL.replaceAll("`","&").replaceAll("\"","＂").replaceAll("%22","＂");
  String isEncode = (String)request.getParameter("isEncode");//获取加密参数
  String src = (String)request.getParameter("src");
  
  if(isEncode != null && "1".equals(isEncode)){//加密参数存在且值为1时,需要进行解密 wangb 20180308
  	  src = SafeCode.decode(src);
  	  src = PubFunc.hireKeyWord_filter_reback(src);
  	  src = src.replaceAll("[+]", "%2B");// + 号字符 进行转码
  }
//  String sSourceURL = ((String)request.getParameter("src")).replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;  //20140901  dengcan
  String sSourceURL = src.replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;  //20140901  dengcan
  if(isEncode == null)//不加密,走原来
  	sSourceURL=sSourceURL.replaceAll("`","&");
  //haosl sSourceURL.toLowerCase().indexOf("script")这句话会误杀一些链接，比如传参fieldName=descript，会被拦截掉，先暂时这么改
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
String titlename = (String)request.getParameter("titlename");
%>
<html>
<head>
	<title><%=com.hrms.frame.codec.SafeCode.decode(titlename)%></title>
</head>
<style type="text/css">
.loading-indicator{
        background:white;
        color:#444;
        font:bold 13px tahoma,arial,helvetica;
        padding:10px;
        margin:0;
        height:auto;
    }
</style>
<body>
<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
	<div class="loading-indicator">
		<img src="/images/blue-loading.gif"/>
		<span style="font:normal 12px arial,tahoma,sans-serif">Loading</span>
		<span id="port_bar">.</span>
	</div>
</div>
<iframe name="childFrame" id="childFrame" height="100%" width="100%" src="<%=sSourceURL%>"></iframe>
</body>
</html> 
<script type="text/javascript"> 
var pawidth = window.screen.width;
var paheight = window.screen.height;
function jindu(){
	var waitInfo=eval("wait");
	waitInfo.style.top=paheight/2-100;
	waitInfo.style.left=pawidth/2-100;
	waitInfo.style.display="block";
	scrollUp();
}
var i=1;	
function scrollUp(){
	var portBar=document.getElementById("port_bar");
	portBar.innerHTML+=".";
	if(i==10){ 
		portBar.innerHTML='';
		i=0;
	}
	i++;
	setTimeout("scrollUp()",100);	
}

jindu();
</script>