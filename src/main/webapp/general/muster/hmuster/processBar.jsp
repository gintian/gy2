<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hjsj.hrms.utils.PubFunc,com.hjsj.hrms.actionform.stat.StatForm"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>   
  	<%	String tabID = request.getParameter("tabID");
  		request.setAttribute("tabID", tabID);
  		String tabid = request.getParameter("tabid");
  		String init = request.getParameter("init");
	  	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	  	String bosflag = userView.getBosflag();
	  	String url = "";
	  	if(StringUtils.isEmpty((String)request.getParameter("url"))) {
	  		url = "";
	  	}else {
	  		url = ((String)request.getParameter("url")).replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;
	  	}
  		url=url.replaceAll("`","&");
  		if(StringUtils.isNotBlank(tabid)&&StringUtils.isNotBlank(init)){
  			url = url+"&init="+init+"&tabid="+tabid;
  		}
	 	if("/general/deci/statics/crosstab.do?b_show=link".equals(url)){//liuy 2014-12-5 限制url为这个链接的时候才加categories
	 		String crossshow=request.getParameter("crossshow");
		 	String categories = "";
		  	StatForm  statForm = (StatForm)session.getAttribute("statForm");
		  	if(statForm != null){  //wangcq 2014-12-04 
		  	    categories = statForm.getCategories();
		  	}
		  	if(StringUtils.isNotBlank(categories)){
		  		url = url+"encryptParam="+PubFunc.encrypt("categories="+categories);
		  	}
  			url = url+"&crossshow="+crossshow;
	 	}
	  	request.setAttribute("bosflag", bosflag);
  	%>
  	<hrms:themes />
	<script type="text/javascript">
		function send(){
			var form = document.getElementById("form_common");
			if(form!=null){
				form.submit();
				jinduo();
			}
		}
		
		function jinduo(){
			var x=document.body.clientWidth/2-300;
		    var y=document.body.clientHeight/2-125;
			var waitInfo;
			waitInfo=eval("wait");
			waitInfo.style.top=y;
			waitInfo.style.left=x;
			waitInfo.style.display="block";
		}
	</script>
  </head>
  
  <body onload="send();">
  	<center>
			<div id="wait"
				style='position: absolute; top: 285; left: 120; display: none; width: 500px; heigth: 250px'>
				<table border="1" width="50%" cellspacing="0" cellpadding="4"
					class="table_style" height="100" align="center">
					<tr>
						<td class="td_style" height=24>
							<bean:message key="hmuster.label.wait" />
						</td>
					</tr>
					<tr>
						<td style="font-size: 12px; line-height: 200%" align=center>
							<marquee class="marquee_style" direction="right" width="400"
								scrollamount="5" scrolldelay="10">
								<table cellspacing="1" cellpadding="0">
									<tr height=8>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
									</tr>
								</table>
							</marquee>
						</td>
					</tr>
				</table>
				<iframe src="javascript:false"
					style="position: absolute; visibility: inherit; top: 0px; left: 0px; width: 285px; height: 120px; z-index: -1; filter ='progid: DXImageTransform . Microsoft . Alpha(style = 0, opacity = 0) ';">
				</iframe>
			</div>
			<%if(!"".equals(url)){ %>
			<html:form styleId="form_common" action="<%=url%>">
			</html:form>
			<%}else{ %>
			<html>
			</html>
			<%} %>
		</center>
  </body>
  <script>
  	var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
  	var isOpera = userAgent.indexOf("Opera") > -1;
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览
  	if(!isIE){ //非IE浏览器里 iframe 要通过display 方式隐藏   wangb 20180124
  		var iframes = document.getElementsByTagName('iframe')[0];
  		iframes.style.display = 'none';
  	}
  </script>
</html>
