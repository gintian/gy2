<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc,com.hjsj.hrms.utils.ResourceFactory"%>
<%
	String flag = (String)request.getAttribute("flag");
	String content = "";
	if("1".equals(flag)) {
		content = "<span style='color:red;font-size: 22px;'>" + ResourceFactory.getProperty("jx.confirm.error") + "</span>";
	}else if("0".equals(flag)){
		content = "<span style='font-size: 22px;'>" + ResourceFactory.getProperty("jx.confirm.success") + "</span>";
	}
%>
<head>
  <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
  <title>绩效考核确认</title>
</head>
<style>
  .main {
    background: #f8f8f8;
    padding: 0px 0 150px 0;
   	border-top: 1px solid #e9e9e9;
   	border-bottom: 1px solid #e9e9e9;
   	font-size: 12px;
   	color: #333;
    font-family: 'Microsoft YaHei';
  }
  .register-info {
	background: #fff;
    padding: 5px 59px 140px 20px;
    overflow: hidden;
    width: 960px;
  }
  .activation-email {
    text-align: center;
    padding-top: 43px;
    vertical-align: middle;
    text-align: center; 
  }
  .container-custom {
    padding: 30px 0 0 280px;
    margin-top: 30px;
  }
</style>
<body>
<div class="main">
  <div class="container-custom">
    <div class="register-info">
      <h1>温馨提示</h1>
      <div class="activation-email">
        <%=content %>
      </div>
    </div>
  </div>
</div>
</body>