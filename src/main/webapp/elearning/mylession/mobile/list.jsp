<%@ page language="java" import="java.util.*,com.hrms.struts.valueobject.UserView,java.sql.Connection,com.hrms.frame.utility.AdminDb" pageEncoding="UTF-8" %>
<%@ page import="com.hjsj.weixin.utils.Token,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.weixin.utils.JsapiSign,com.hrms.hjsj.sys.ResourceFactory,com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.PubFunc" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String etoken =(String) request.getParameter("etoken");
etoken = SafeCode.convert64BaseToString(
										SafeCode.keyWord_reback(etoken), "GBK");

String up[] = etoken.split(",");
String ssoUsername = up[0];
String pwd = "";
if(up.length==2){
	pwd = up[1];
}
// 登录用户对象
UserView userView = (UserView)session.getAttribute("userView");
// 是否登录标志
Boolean islogon = (Boolean) session.getAttribute("islogon");
if (userView != null && islogon != null && islogon) { 
	// System.out.println("--已经存在userview，无需再登陆,直接进入系统--");
	
} 

// 如果登录用户对象不存在，可以创建登录用户对象
Connection conn = null;
try {
	conn = AdminDb.getConnection();
	userView = new UserView(ssoUsername, conn);
	// 调用canLogin方法后，可以直接获取该用户的权限
if (userView.canLogin()) { 
	// 将用户对象保存到session中
	session.setAttribute("username", ssoUsername);
// 将登录标志保存到session中
session.setAttribute("islogon", true);
// username是业务系统本身的登录用户属性名,
session.setAttribute("userView", userView);

	} else { // 用户不存在或被禁用
		
	}
} catch (Exception e) {
	e.printStackTrace();
} finally {
	try {
		if (conn != null) {
			conn.close();
		}
	} catch (Exception sql) {
		sql.printStackTrace();
	}
}


String r5000 = request.getParameter("r5000");

String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+(80==(request.getServerPort())?"":(":"+request.getServerPort()))+path+"/";

String apppath = request.getRequestURI() + (request.getQueryString()!=null?("?"
								+ request.getQueryString()):"");
								//System.out.println(apppath);

Map<String,String> map=JsapiSign.sign((basePath+apppath),true);


String signature= map.get("signature");
String timestamp = map.get("timestamp");
String nonceStr= map.get("nonceStr");
String corpid = map.get("corpid");

String filepath = request.getSession().getServletContext().getRealPath("/");
	if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
	{
		filepath = session.getServletContext().getResource("/").getPath();
		//filepath=session.getServletContext().getResource("").getPath();//.substring(0);
	   if(filepath.indexOf(':')!=-1)
		  {
		   filepath=filepath.substring(1);   
		  }
		  else
		  {
			  filepath=filepath.substring(0);      
		  }
	   int nlen=filepath.length();
		  StringBuffer buf=new StringBuffer();
		  buf.append(filepath);
		  buf.setLength(nlen-1);
		  filepath=buf.toString();
	}

filepath = SafeCode.encode(filepath);

String title = SystemConfig.getPropertyValue("frame_logon_title");
title=(title!=null&&title.length()!=0)?title:ResourceFactory.getProperty("frame.logon.title");
%>
<html>
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%=title %>-培训课程</title>
	 <link rel="stylesheet" href="<%=basePath %>phone-app/jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="<%=basePath %>phone-app/jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="<%=basePath %>phone-app/jquery/jquery.mobile-1.0a2.min.js"></script>	
	 <script type="text/javascript" src="<%=basePath %>phone-app/jquery/rpc_command.js"></script>
	 <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
	 <script type="text/javascript">
	 var u = navigator.userAgent;
	 if(u.indexOf('MicroMessenger') > -1&&u.indexOf('Android')== -1){
	 	wx.config({
		    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: '<%=corpid %>', // 必填，企业号的唯一标识，此处填写企业号corpid
		    timestamp: '<%=timestamp %>', // 必填，生成签名的时间戳
		    nonceStr: '<%=nonceStr %>', // 必填，生成签名的随机串
		    signature: '<%=signature %>',// 必填，签名，见附录1
		    jsApiList: ['hideOptionMenu'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});
		
		wx.ready(function(){
			wx.hideOptionMenu();
		    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
		});
		wx.error(function(res){
			//alert(res.errMsg);
		    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
		
		});
		}
	 </script>
	 <style type="text/css">
.ui-li-desc {
	margin:0;
}
.descspan{
margin-left:5px;
height:20px;
line-height:20px;
text-align:center ;
vertical-align :middle ;
font-size:12px;
}

.ui-bar-c {
background: none;
    border: none;
    color: none;
    font-weight: none;
    text-shadow: none;
}
.ui-btn-up-c {
    background: none;
  }
  
  .ui-body-c {
    background: none;
    }
    
    
.ui-header .ui-title, .ui-footer .ui-title{
	margin: 0.6em 5px 0.8em;
	text-align: left;
}
	 </style>
	 <script type="text/javascript">
	 function showOffice(url){
	 	alert("DownLoadCourseware?url="+url);
	 	//window.location.href="/DownLoadCourseware?url="+url;
	 	window.location.href="http://www.baidu.com";
	 }
	 function changeHit(r5000,r5100,filepath){
	 	if(u.indexOf('MicroMessenger') > -1&&u.indexOf('Android') > -1)
	 		return false;
			var map = new HashMap();
		    map.put("r5000", r5000);
		    map.put("r5100", r5100);
		    map.put("filepath",filepath);
		    Rpc({functionId:'20200130015',success:checkResult},map);
		   
			   function checkResult(html){
					var value=html;
					var map=JSON.parse(value);
					if(map.succeed)
					{
						if("true"==map.flag)
							window.location.href="/DownLoadCourseware?url="+filepath+"&isFrom=mobile";
						else
							alert("课件资源貌似被移除啦~~~");
					}
			  }
		}
		
		function changeView(encryptParam){
				window.location.href="/train/resource/course/mobile/mylession.do?b_content=link&encryptParam="+encryptParam;
		}
		
		function searchCourse(){
			var map = new HashMap();
		    map.put("r5000", "<%=r5000 %>");
		    map.put("filepath","<%=filepath %>");
		   　Rpc({functionId:'20200130016',success:searchRusult},map);
		}
		
		
		function searchRusult(html)
		{
				var value=html;
				//alert(value);
				var map=JSON.parse(value);
				if(map.succeed)
				{
				//alert(map.html);
				  // $("#s").text(map.html);
				 // $("#s").append(map.html);
					//alert(document.getElementById("s"));
					//document.getElementById("s").innerHTML=map.html;
					
					//$("#s").html(map.html);
					//$("#smain").listview({"inset": true});
					//$.mobile.changePage($('#smain'));
					//document.write(map.html);
				}
		}
		
	 </script>
  </head>
  
<body> <!--  bgcolor="#DEDEDE"  -->
<hrms:searchMyCourse r5000="<%=r5000 %>" filepath="<%=filepath %>"></hrms:searchMyCourse>

<script type="text/javascript">
//wechat tips
document.writeln("<img class=\"wxtip\" name=\"wxtip\" style=\"width:90%;position:absolute;top:0;right:10px;display:none;z-index:999\" src=\"/images/wxtip.gif\"/>");
document.getElementsByName("wxtip")[0].addEventListener('click',function(){
    showtips(0);
},false);
function showtips(type){
    if(type==1){
        document.getElementsByName("wxtip")[0].style.display = "block"; 
    }
    else{
       document.getElementsByName("wxtip")[0].style.display = "none"; 
    }
}

//alert(u);
setTimeout(function(){
if(u.indexOf('MicroMessenger') > -1&&u.indexOf('Android') > -1){
    var obj = document.getElementsByTagName("a");
    for(var i=0;i<obj.length;i++)
    {
        var _name = String(obj[i].onclick);
        if( _name.indexOf("changeHit") > -1)
        {
            var getObj = obj[i];
            getObj.addEventListener('click',function(){
                showtips(1);
            },false);
        }
    }
}
},500);
//searchCourse();
</script>
</body>
</html>
