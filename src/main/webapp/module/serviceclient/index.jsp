<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String ip = request.getParameter("ip");
ip = ip == null? "":ip;
//身份证登入
String login_card = request.getParameter("login_card");
login_card=login_card == null? "":login_card;
//身份证&工卡登录
String login_jobid = request.getParameter("login_jobid");
login_jobid = login_jobid == null? "":login_jobid;
//账号登入
String login_accountid = request.getParameter("login_accountid"); 
login_accountid = login_accountid == null? "":login_accountid;
String url = "serviceclientmain.jsp?ip="+ip+"&login_card="+login_card+"&login_jobid="+login_jobid+"&login_accountid="+login_accountid;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>自助终端服务登录</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=10;IE=9;IE=8;IE=7">
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<style type="text/css">
	html{
		height:100%;
	}
	body{
		margin:0px;
		padding:0px;
		height:100%;
		overflow:hidden;
	}
</style>
<script type="text/javascript">
Ext.onReady(function(){
	var height = document.body.clientHeight;
	var width = document.body.clientWidth;
	var percent = parseInt(height)/parseInt(width);//屏幕占比
	if(percent > 1.2){
		var top = document.getElementById('top');
		top.style.display = 'block';
		top.style.height = ((parseInt(height)-parseInt(width))/2)+"px";
		var bottom = document.getElementById('bottom');
		bottom.style.display = 'block';
		bottom.style.height = ((parseInt(height)-parseInt(width))/2)+"px";
		var main = document.getElementById('main');
		main.style.height = height;
		var mainFrame = document.getElementById('mainFrame');
		mainFrame.setAttribute('width',width);
		mainFrame.setAttribute('height',width);
	}else{
		var main = document.getElementById('main');
		main.style.height = height;
		var mainFrame = document.getElementById('mainFrame');
		mainFrame.setAttribute('width',width);
		mainFrame.setAttribute('height',height);
	}
	setTimeout("reloop()",5000);   
	document.oncontextmenu=new Function("event.returnValue=false;");                                                                                                                                                                                                                          
});
var inum=1;
function reloop(){
	for(var bIndex = 1;bIndex<=3;bIndex++ ){
		if(bIndex==(inum%3)+1)
			document.getElementById("bannerimg"+bIndex).style.display="block";
		else
			document.getElementById("bannerimg"+bIndex).style.display="none";
	}
    inum++;
    setTimeout("reloop()",5000);
}
</script>
</head>
<body>
<div id="top" style="display:none;">
	<div id="title" style="width:100%;height:100px;display:block;">
		<marquee style="color:#ea1a27;font-size:60px;margin-top:10px;font-family:微软雅黑;">欢迎使用北京世纪自助打印服务系统！</marquee>
	</div>
	<div class="banner" style="width:100%;height:320px;">
		<img id="bannerimg1" src="images/banner1_top.jpg" width="100%" height="100%"/>
		<img id="bannerimg2" style="display:none;" src="images/banner2_top.jpg" width="100%" height="100%"/>
		<img id="bannerimg3" style="display:none;" src="images/banner3_top.jpg" width="100%" height="100%"/>
	</div>
</div>
<div id="main">
	<iframe src="<%=url %>" name="centerFrame" scrolling="no" width="100%" frameborder="0"
            id="mainFrame" title="mainFrame"></iframe>
</div>
<div id="bottom" style="display:none;">
	<img src="images/banner_bottom.jpg" width="100%" height="100%"/>
</div>
</body>
</html>