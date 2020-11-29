<%@page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="com.hjsj.hrms.module.serviceclient.utils.ServiceUtils" %>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%
 
	EncryptLockClient lock = (EncryptLockClient)request.getSession().getServletContext().getAttribute("lock");
	String msg = ResourceFactory.getProperty("error.function.nopriv");
	if(lock==null){
		response.getWriter().write("<table width='100%' height='100%'><tr><td align='center' valign='middle'>"+msg+"</td></tr></table>");
		return;
	}
	if(!lock.isBmodule(50, "")){
		response.getWriter().write("<table width='100%' height='100%'><tr><td align='center' valign='middle'>"+msg+"</td></tr></table>");
		return;
	} 


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>自助终端服务登录</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=10;IE=9;IE=8;IE=7">
<!-- 打印服务使用插件js，勿删 -->
<script language="JavaScript" src="../../../pdfjs/build/pdf.js"></script>
<script type="text/javascript">
<!--加入框架以后要指定这个否则,退出系统再重新加载的时候会有问题-->
PDFJS.workerSrc ='../../../pdfjs/build/pdf.worker.js';
</script>
<!-- Ext 框架文件 -->
<script type="text/javascript" src="../../../jquery/jquery-3.5.1.min.js"></script>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../../../module/serviceclient/serviceHome/ext-funcs.js"></script>
<script language="JavaScript" src="../../../jquery/JQuery.md5.js"></script>
<script language="JavaScript" src="../../../module/serviceclient/serviceHome/security.js"></script>
<script type="text/javascript" src="../../../module/serviceclient/SoftKey/vk_loader.js?vk_layout=US%20US&vk_skin=flat_gray" ></script>
<link rel="stylesheet" href="../../../ext/ext6/resources/ext-theme.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="../../../module/serviceclient/css/keyboardStyle.css" />
<link rel="stylesheet" type="text/css" href="../../../module/serviceclient/css/index.css" />
</head>
<body>
<%
ServiceUtils serviceUtils = new ServiceUtils();//自助终端工具类
String cardIdField = serviceUtils.getCardIdField();//身份证指标
String icCardField = serviceUtils.getIcCardField();//系统中配置的工卡指标
cardIdField = StringUtils.trimToEmpty(cardIdField);
icCardField = StringUtils.trimToEmpty(icCardField);
boolean needCheckPwFlag = serviceUtils.needCheckPw();//是否需要校验密码
// 获取ip地址
String ip = request.getParameter("ip");
String uri = request.getRequestURL().toString();
String params = request.getQueryString();
String url = uri+"?"+ params;
// 身份证登入
String login_card = request.getParameter("login_card");
//身份证&工卡登录
String login_jobid = request.getParameter("login_jobid");
//账号登入
String login_accountid = request.getParameter("login_accountid"); 
String cardStyle = "noVisible";
String jobIdStyle ="noVisible";
String idAndIcStyle = "noVisible";
String accountIdStyle = "noVisible";
boolean isValidateCode = SystemConfig.isValidateCode();
boolean isLogin_card = "1".equals(login_card);//身份证登录
boolean isLogin_jobid ="1".equals(login_jobid);//身份证&工卡登录
boolean isLogin_accountid = "1".equals(login_accountid);//账号登录
Calendar c = Calendar.getInstance();
int hour = c.get(Calendar.HOUR_OF_DAY); 
if(isLogin_card){//身份证
    cardStyle="cardStyle";
}
if(isLogin_jobid){//身份证&工卡
    jobIdStyle="cardStyle";
    idAndIcStyle = "cardStyle";
}
if(isLogin_accountid){//帐号
    accountIdStyle="cardStyle";
}
%>
<script type="text/javascript">


</script>
  <script src="../../../general/sys/hjaxmanage.js"></script>
	<div class="banner" id ="banner" style="width:100%;height:100%;overflow:hidden;">
		<img id="bannerimg" src="images/banner_logon.jpg" width="100%" height="100%"/>
		<%--<ul class="tab01">
			<li><a id="banner1" href="###" onclick="changeBanerImg(1);"
				class="png current"></a></li>
			<li><a id="banner2" href="###" onclick="changeBanerImg(2);"
				class="png"></a></li>
			<li><a id="banner3" href="###" onclick="changeBanerImg(3);"
				class="png"></a></li>
		</ul>--%>
		<%--<img id="logo" src = "images/index/logon_logo.png" style="margin-top:30px; "/>--%>
		
		<%--<img src = "../../../module/serviceclient/images/index/logon_title.png" style="margin: 8px 0px 0px 340px "/>--%>
		<div class="logon">
		      <div style="margin-left:auto;margin-right:auto;" class="centerdiv">
		        <button id="card"; class="<%=cardStyle%>" style=" background:url(../../../module/serviceclient/images/index/card.png) no-repeat left top" onclick="idOrIcCardLogon('idCard')" ><span style='margin-left:20px '><font style='font-size:25px;'color='#ffffff'>身份证登录</font></span></button>
		        <button id="Work_card_registration"; class="<%=jobIdStyle%>" style=" background:url(../../../module/serviceclient/images/index/Work_card_registration.png) no-repeat left top" onclick="idOrIcCardLogon('idOrIcCard')" ><span style='margin-left:20px'><font style='font-size:25px;'color='#ffffff'>职工卡登录</font></span></button>
		        <!--  <button id="IdAndIc_login"; class="<%=idAndIcStyle%>" style="width:360px; background:url(../../../module/serviceclient/images/index/idAndic.png) no-repeat left top" onclick="idOrIcCardLogon('idOrIcCard')" ><span style='margin-left:28px'><font style='font-size:25px;'color='#ffffff'>身份证&职工卡登录</font></span></button>-->
		        <button id="Account_login"; class="<%=accountIdStyle%>" style=" background:url(../../../module/serviceclient/images/index/Account_login.png) no-repeat left top" onclick="doLogon()"><span style='margin-left:10px'><font style='font-size:25px;'color='#ffffff'>账号登录</font></span></button>
		      </div>
		 </div>
		 <div id='readCarddiv'></div>
	</div>
	
	<div id="softkey"></div>
    <div id="printpdf"></div>
	
	<script type="text/javascript">
			var logonHeight = 0;
			/*
			$(window).resize(function() {
				var totalH = $(window).height();
				var totalW = $(window).width();
				$(".banner").height(totalH);
				//$("#bannerimg").height(totalH);
				//$("#bannerimg").width(totalW);
				document.getElementById("bannerimg").src = "../../../module/serviceclient/images/index/logon_bg1.jpg";
				var height = -(totalH - $(".tab01").height()-10);
				//$(".tab01").css('bottom',height);
			});*/
			$(document).ready(function() {
				//reloop();
				init();
				 //禁止退格键 作用于Firefox、Opera   
			    document.onkeypress = banBackSpace;  
			    //禁止退格键 作用于IE、Chrome  
			    document.onkeydown = banBackSpace;  
			    document.oncontextmenu=new Function("event.returnValue=false;");
			});
			function init(){
				var totalH = $(window).height();
				var totalW = $(window).width();
				<%if(isValidateCode){%>
				logonHeight = totalH - 650;
				<%}else{%>
				logonHeight = totalH - 600;
				<%}%>
				var height = -(totalH -10);
				$(".banner").height(totalH);
				$("#bannerimg").height(totalH);
				$("#bannerimg").width(totalW);
			}
			 function hasIEPlugin(name) {  
		          try {  
		              new ActiveXObject(name);  
		              return true;  
		          } catch (ex) {  
		              return false;  
		          }  
			}
			<%--
			var inum = 1;
			function changeBanerImg(index) {
				inum = index;
				document.getElementById("bannerimg").src = "../../../module/serviceclient/images/index/logon_bg"
						+ index + ".jpg";
				document.getElementById("banner1").className = "png";
				document.getElementById("banner2").className = "png";
				document.getElementById("banner3").className = "png";
				document.getElementById("banner" + index).className = "png current";
			}
			function reloop() {
				var totalH = $(window).height();
				var totalW = $(window).width();
				<%if(isValidateCode){%>
				logonHeight = totalH - 650;
				<%}else{%>
				logonHeight = totalH - 600;
				<%}%>
				var height = -(totalH - $(".tab01").height()-10);
				$(".tab01").css('bottom',height);
				$(".banner").height(totalH);
				$("#bannerimg").height(totalH);
				$("#bannerimg").width(totalW);
				document.getElementById("bannerimg").src = "../../../module/serviceclient/images/index/logon_bg"
						+ ((inum % 3) + 1) + ".jpg";
				document.getElementById("banner" + (((inum - 1) % 3) + 1)).className = "png";
				document.getElementById("banner" + ((inum % 3) + 1)).className = "png current";
				inum++;
				setTimeout("reloop()", 15000);
			}--%>
			//处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外   
			function banBackSpace(e){  
			    var ev = e || window.event;//获取event对象     
			    var obj = ev.target || ev.srcElement;//获取事件源       
			    var t = obj.type || obj.getAttribute('type');//获取事件源类型       
			    //获取作为判断条件的事件类型   
			    var vReadOnly = obj.readOnly;  
			    var vDisabled = obj.disabled;  
			    //处理undefined值情况   
			    vReadOnly = (vReadOnly == undefined) ? false : vReadOnly;  
			    vDisabled = (vDisabled == undefined) ? true : vDisabled;  
			    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，    
			    //并且readOnly属性为true或disabled属性为true的，则退格键失效    
			    var flag1 = ev.keyCode == 8 && (t == "password" || t == "text" || t == "textarea") && (vReadOnly == true || vDisabled == true);  
			    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效      
			    var flag2 = ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea";  
			    //判断      
			    if (flag2 || flag1)   
			        return false;
			}  
	        //设置功能组件命名空间
	        Ext.Loader.setPath("ServiceClient", '.');
	        Ext.require([ 'ServiceClient.serviceHome.ServiceHome',
	                'ServiceClient.serviceHome.ServicePlatform',
	                'ServiceClient.serviceHome.PrintService',
	                'ServiceClient.serviceHome.ServiceLogin',
	                'ServiceClient.serviceHome.ViewService',
	                'ServiceClient.serviceHome.PasswordCheckWin',
	                'ServiceClient.serviceHome.FirstModifyPassword']);
	        //账号登录
	        function doLogon() {
	            /* document.getElementById("container").style.display=""; */
	            if(Ext.getCmp('userPwCheckWin')){
	            	Ext.getCmp('userPwCheckWin').destroy();
	            }
	        	var passwordTransEncrypt="<%=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt")%>";
	        	if(!Ext.getCmp('ServiceLogin')){
                    Ext.create("ServiceClient.serviceHome.ServiceLogin", {
                        ip:'<%=ip%>',
                        isValidateCode:<%= isValidateCode%>,
                        logonHeight:logonHeight,
                        passwordTransEncrypt:passwordTransEncrypt,
                        hour:'<%=hour%>'
                    }).show();
	        	}
	            /* window.$write = $("[name='userName']")[0]; */
	        }
	        //身份证、职工卡登录
            function idOrIcCardLogon(accessFlag){
                // ActiveXObject的对象名
                var activexObjectName ="readCardOcx.readCardX";
                var axobj = hasIEPlugin(activexObjectName);
                if(!axobj){
                    Ext.Msg.alert(sc.home.tip, sc.home.noReadCardPluginAreaTip);//没有安装控件
                    return;
                }
                var me = this;
                //身份证指标
                var cardIdField ='<%=cardIdField%>';
                if(accessFlag=="idCard" && !cardIdField){//点击的是身份证登录,则必须配置身份证
                    Ext.Msg.alert(sc.home.tip, sc.home.noCardIdFieldTip);//系统没有配置身份证指标,请联系系统管理员!
                    return;
                }
                
                if(accessFlag=="idOrIcCard"){//职工卡登录,身份证和工卡二选一
	                //工卡指标
	                var icCardField ='<%=icCardField%>';
	                if(!icCardField && !cardIdField){//两者都没有
	                    Ext.Msg.alert(sc.home.tip, sc.home.noIcCardFieldTip);//系统没有配置登录指标,请联系系统管理员
	                    return;
	                }
                }
                if(Ext.getCmp("ServiceLogin")){
                      Ext.getCmp("ServiceLogin").destroy();
                }
                var readCarddiv = document.getElementById('readCarddiv');
                while(readCarddiv.hasChildNodes()){
                    readCarddiv.removeChild(readCarddiv.firstChild);
                }
                readCarddiv.innerHTML = '<OBJECT id="readcard" classid="clsid:{29871C6B-E5E7-47D9-8FB4-1A3CB95084BF}"  codebase="readCardOcx.ocx#version=1,0,0,0" width="0" height="0" align="center" hspace="0" vspace="0"></OBJECT>'
                var obj = document.getElementById("readcard");
				var msg = sc.home.readYourData;
                if(accessFlag=="idCard"){
					msg = msg.replace("{$name}",sc.home.icCardName);
				}else{
					msg = msg.replace("{$name}",sc.home.cardName);
				}

				Ext.MessageBox.show({
					title:sc.home.tip,
					msg:msg, //正在读取您的信息,请将[身份证|职工卡]放至指定区域
					progress:true,
					width:300,
					wait:true,
					buttons:Ext.Msg.CANCEL,
					fn: function (b, t) {
						if(readCard){
							clearInterval(readCard);
						}
					},
					waitConfig:{interval:600}
				});

				var data;
				data = obj.readIdCardByCompany(3);
				data = data.replace(/\r\n/g, '');
				if(data){
					data = Ext.decode(data);
					if (data.cardid) {
						Ext.MessageBox.hide();
						doCardLogon(false,data,accessFlag,cardIdField,icCardField);
						return;
					}
				}
				var loopCount = 0;
				var reading = false;
				var readCard= setInterval(function () {
					if(reading){
						return;
					}
					if(loopCount==5){
						Ext.MessageBox.hide();
						clearInterval(readCard);
						doCardLogon(true);
						return;
					}
					reading = true;
					data = obj.readIdCardByCompany(3);
					reading = false;
					data = data.replace(/\r\n/g, '');
					if (data) {
						data = Ext.decode(data);
						if (data.cardid) {
							Ext.MessageBox.hide();
							clearInterval(readCard);
							doCardLogon(false,data,accessFlag,cardIdField,icCardField);
						}
					}
					loopCount++;
				}, 1000);
            }

			/**
			 * 无论是否读卡成功，都执行此方法
			 * @param dataError 读卡失败
			 * @param data 读卡信息
			 * @param accessFlag 按钮入口 身份证登录、职工卡登录
			 * @param cardIdField 身份证指标
			 * @param icCardField 职工卡指标
			 */
			function doCardLogon(dataError,data,accessFlag,cardIdField,icCardField) {
				if(dataError){
					Ext.Msg.alert(sc.home.tip, sc.home.readDataError);//信息读取失败，请重试!
					return;
				}
				var cardid = data.cardid;//身份证or工卡号
				var vo = new HashMap();
				vo.put('username',cardid);
				vo.put('logintype',2);
				vo.put('__type','byserviceclient');
				vo.put('transType','serve');
				vo.put('ip','<%=ip%>');
				vo.put('accessFlag',accessFlag);
				vo.put('cardIdField',cardIdField);
				if(accessFlag=="idOrIcCard"){
					vo.put('icCardField',icCardField);
				}
				vo.put('needInputPassword',<%=needCheckPwFlag%>);
				vo.put('isInputPassword',false);

				Rpc({functionId:'SC000000001',async:false,success:function(form){
						var result = Ext.decode(form.responseText);
						var cardIdValue = result.cardIdValue;//证件号真实值
						var flag = result.flag;
						var error_message = result.logon_error;
						var servicesData = result.serviceData;

						if(flag == 0){
							Ext.Msg.alert(sc.home.tip,sc.home.notAccessFromUnRegclientTip);//不允许从未注册服务终端登录!
						}else{
							//用户密码校验window
							if(Ext.getCmp('userPwCheckWin')){
								Ext.getCmp('userPwCheckWin').close();
							}
							if(error_message=="accountError") {
								Ext.Msg.alert(sc.home.tip, sc.home.noAccountErrorTip);//系统无您信息
								return;
							}else if(error_message=="account") {
								Ext.Msg.alert(sc.home.tip, sc.home.accountErrorTip);//用户名或密码错误
								return;
							}

							var passwordTransEncrypt="<%=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt")%>";
							var forgetPwdFlag = "<%=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "retrieving_password")%>";
							if(error_message=="needInputPassword"){
								Ext.create('ServiceClient.serviceHome.PasswordCheckWin',{
									needCheckPwFlag:result.needInputPassword,//是否需要密码校验
									ip:result.ip,//终端机ip
									cardid:cardid,//证件号
									logintype:2,
									forgetPwdFlag:forgetPwdFlag,//系统中是否配置显示忘记密码
									logonHeight:logonHeight,
									cardIdValue:cardIdValue,//证件号真实值
									cardIdField:cardIdField,//证件指标
									icCardField:accessFlag=="idOrIcCard" ? icCardField:"",//工卡指标
									accessFlag:accessFlag,//登录方式
									hour:'<%=hour%>',
									passwordTransEncrypt:passwordTransEncrypt//密码是否需要MD5加密
								}).show();
							}else{
								//定时
								ServiceClientSecurity.start();
								Ext.getDom("banner").style.display="none";
								Ext.widget('viewport',{
									layout:'fit',
									items:Ext.create("ServiceClient.serviceHome.ServiceHome",{
										servicesData:servicesData,
										ip:'<%=ip%>'
									})
								});
							}
						}
					}},vo);
			}
    </script>
</body>
</html>