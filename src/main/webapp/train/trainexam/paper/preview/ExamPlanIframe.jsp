<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.utility.AdminDb"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<%
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);

	String etoken =(String) request.getParameter("etoken");
	if(etoken != null && etoken.length() >0) {
		etoken = SafeCode.convert64BaseToString(SafeCode.keyWord_reback(etoken), "GBK");
		
		String up[] = etoken.split(",");
		String ssoUsername = up[0];
		String pwd = "";
		if(up.length==2){
			pwd = up[1];
		}
		// 登录用户对象
		UserView userViewa = (UserView)session.getAttribute("userView");
		// 是否登录标志
		Boolean islogon = (Boolean) session.getAttribute("islogon");
		if (userViewa != null && islogon != null && islogon) { 
			// System.out.println("--已经存在userview，无需再登陆,直接进入系统--");
			
		} else{
		    Connection conn = null;
		// 如果登录用户对象不存在，可以创建登录用户对象
			try {
			    conn = AdminDb.getConnection();
				userViewa = new UserView(ssoUsername, conn);
				// 调用canLogin方法后，可以直接获取该用户的权限
				if (userViewa.canLogin()) { 
					// 将用户对象保存到session中
					session.setAttribute("username", ssoUsername);
					// 将登录标志保存到session中
					session.setAttribute("islogon", true);
					// username是业务系统本身的登录用户属性名,
					session.setAttribute("userView", userViewa);
				} else { // 用户不存在或被禁用
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(conn);
			}
		}
	}
	
	String date = DateStyle.getSystemDate().getDateString();
	//20140901  dengcan
	String sSourceURL = (String) request.getParameter("src");
	String isThird = (String) request.getParameter("isThird");
	sSourceURL = sSourceURL.replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=");
	sSourceURL = sSourceURL.replaceAll("`", "&");
	//20170717 dengcan
	if ((sSourceURL.toLowerCase().indexOf("script") != -1 && sSourceURL.toLowerCase().indexOf("<") != -1)
			|| sSourceURL.toLowerCase().indexOf("javascript:") != -1)
		throw new Exception("error page!");
	//此处存在安全问题，如果src是危险网站，会泄漏信息。此处判断如果是重定向的连接，
	//判断域名端口是否一致，否则是引入外部连接，禁止访问 guodd 2016-12-19
	if (sSourceURL.toLowerCase().indexOf("http") != -1 || sSourceURL.indexOf("HTTP") != -1) {
		StringBuffer reUrl = request.getRequestURL();
		String local = reUrl.substring(0, reUrl.indexOf("/general"));
		if (!sSourceURL.toLowerCase().startsWith(local.toLowerCase()))
			throw new Exception("error page!");
	}
		
	if (sSourceURL.trim().startsWith("//"))
		throw new Exception("error page!");

	String[] parameters = sSourceURL.split("&");
	String r5300 = "";
	String r5400 = "";
	for (int i = 0; i < parameters.length; i++) {
		String parameter = parameters[i];
		if (parameter.startsWith("r5300"))
			r5300 = parameter.substring(6);
		
		if (parameter.startsWith("plan_id") || parameter.startsWith("paper_id"))
			r5400 = parameter.substring(parameter.indexOf("=") + 1);
	}
			
%>
<script type="text/javascript">
		function test(type, r5300, r5400) {
			var map = new HashMap();
			map.put("type", type + "");
			map.put("r5300", r5300 + "");
			map.put("r5400", r5400 + "");
			map.put("isPendTask", "1");
			Rpc({
				functionId : '2020030191',
				success : checkSucc
			}, map);
		}

		function checkSucc(response) {
			var value = response.responseText;
			map = Ext.decode(value);
			if (getDecodeStr(map.biaozhi) == "ok") {
				var url = "<%=sSourceURL%>";
				var overDate = map.overDate;
				if("1" == overDate) {
					document.getElementById("examPaper").src = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300=<%=r5300%>&exam_type=2&flag=8&returnId=5&paper_id=<%=r5400%>&home=5";
				} else {
					try {
						var newwindow = window.open(url, "myexam",
										"channelmode=yes,fullscreen=yes,scrollbars=yes,resizable=no,location=no,toolbar=no,menubar=no,status=no");
						goPortal();
					} catch (e) {
					}
				}

		} else {
			alert(getDecodeStr(map.biaozhi));
			goPortal();
		}
	}
		
	function goPortal() {
	<%
		if("1".equals(isThird)){
	%>
		window.opener=null;
		window.open('','_self');
		window.close();
	<%	    
		} else {
	%>
		var tar='<%=userView.getBosflag()%>';
		if("hl"==tar)//6.0首页
			document.location="/templates/index/portal.do?b_query=link";
		else if("hcm"==tar)//7.0首页
			document.location="/templates/index/hcm_portal.do?b_query=link";
	<%
		}
	%>
			
	}
	
</script>
<body>
<iframe src="" id="examPaper" name="examPaper" scrolling="auto" height="100%" width="100%" frameborder="0"></iframe>
</body>
</html>
<script type="text/javascript">
	test("", "<%=r5300%>", "<%=r5400%>");
</script>