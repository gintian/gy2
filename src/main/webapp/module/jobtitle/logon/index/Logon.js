/**
 * 资格评审_登录
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('JobtitleLogon.Logon',{
	constructor:function(config) {
		jobtitlelogon_me = this;
		this.init();
	},
	// 初始化函数
	init:function() {
		jobtitlelogon_me.isShowValidateCode();
		//屏蔽浏览器右键菜单
		Ext.getDoc().on("contextmenu", function(e){
			e.stopEvent();
		});
		// 加载自定义css
		jobtitlelogon_me.createSelfCss();
		
		// 定位		
		jobtitlelogon_me.initPosition();
		
		// 窗口resize重新定位
		Ext.EventManager.onWindowResize(function(w,h){ 
			jobtitlelogon_me.initPosition(); 
		},this,true); 
		
		
		// 初始化绑定事件
		jobtitlelogon_me.initEvent();
		
		var _username = document.getElementById('username');
		_username.focus();
		//jobtitlelogon_me.getView();
		
		// ie低版本下，初次进入页面时用户名的背景没有消失，导致输入的字和“用户名”重叠
		document.getElementById('name_hint').style.display='none';
	},
	// 定位
	initPosition:function(){
	    var _username = document.getElementById('username');
	    var _password = document.getElementById('password');
	    //var _vc = document.getElementById('vc');
	    
	    var _name_hint = document.getElementById('name_hint');
	    var _pwd_hint = document.getElementById('pwd_hint');
	    //var _vc_hint = document.getElementById('vc_hint');
	    
	    var namepos=jobtitlelogon_me.getAbsPosition(_username);
	    var pwdpos=jobtitlelogon_me.getAbsPosition(_password);
	    //var vcpos=jobtitlelogon_me.getAbsPosition(_vc);

			_name_hint.style.left=(namepos[0])+'px';
		    _name_hint.style.top=(namepos[1]+7)+'px';
	    if(_username.value.length==0){
		    _name_hint.style.display = "block";
	    }
	    	_pwd_hint.style.left=(pwdpos[0])+'px';
		    _pwd_hint.style.top=(pwdpos[1]+7)+'px';
	    if(_password.value.length==0){
		    _pwd_hint.style.display = "block";
	    }
	    	var browser = navigator.appName ; //浏览器名称
	    	/*if(Ext.isIE) {// IE
				_vc_hint.style.left=(vcpos[0])+'px';
			}else{
	    		_vc_hint.style.left=(vcpos[0]+10)+'px';
			}
		    _vc_hint.style.top=(vcpos[1]+7)+'px';
	    if(_vc.value.length==0){
		    _vc_hint.style.display = "block";
	    }*/
	},
	isShowValidateCode:function() {
		//隐藏验证码
		Ext.Ajax.request({
			url: 'LoginCheck',
			method:'post',
			params: {
				isShowValidatecode: "1"
		    },
			success: function(response, opts) {
				var obj = Ext.decode(response.responseText);
				jobtitlelogon_me.show_Validatecode = obj.show_Validatecode;
		        if(jobtitlelogon_me.show_Validatecode == "true") {
		        	Ext.getDom('hjValidateCode').style.display = "";
		        	
		        	//Ext.getDom('hjLogin').innerHTML = "<input id='logon' type='button'  TABINDEX='4' value='' class='hj-lg-login-buttom' style='cursor:pointer;'/>";
		        	var _vc = document.getElementById('vc');
		        	var _vc_hint = document.getElementById('vc_hint');
		        	var vcpos=jobtitlelogon_me.getAbsPosition(_vc);
		        	var browser = navigator.appName ; //浏览器名称
			    	if(Ext.isIE) {// IE
						_vc_hint.style.left=(vcpos[0])+'px';
					}else{
			    		_vc_hint.style.left=(vcpos[0]+10)+'px';
					}
				    _vc_hint.style.top=(vcpos[1]+7)+'px';
				    if(_vc.value.length==0){
					    _vc_hint.style.display = "block";
				    }
		        }else {
		        	//Ext.getDom('hjLogin').innerHTML = "<input id='logon' type='button'  TABINDEX='4' value='' class='hj-lg-login-buttom' style='cursor:pointer;'/>";
		        }
		    }
		});
	},
	// 获取高度
	getAbsPosition:function(obj, offsetObj){
	    var _offsetObj=(offsetObj)?offsetObj:document.body;
	    var x=obj.offsetLeft;
	    var y=obj.offsetTop;
	    var tmpObj=obj.offsetParent;
	
	    while ((tmpObj!=_offsetObj) && tmpObj){
	        x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
	        y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
	        tmpObj=tmpObj.offsetParent;
	    }
	    return ([x, y]);
	},
	// 复写样式，不影响总体Css
	createSelfCss:function(){
		
		if(Ext.isIE /**&& Ext.isIE8m */) {// IE8及以下，input框的背景提示位置兼容
//			Ext.util.CSS.createStyleSheet(".hj-lg-yonh{width:150px;height:29px;border:none;background:none;line-height:30px;font-family:'微软雅黑';font-size:12px;color:#a8a8a8;padding-left:0px;margin:1px 0 0 0px;}","card_css");
//			Ext.util.CSS.createStyleSheet(".hj-lg-mima{width:150px;height:29px;border:none;background:none;line-height:30px;font-family:'微软雅黑';font-size:12px;color:#a8a8a8;padding-left:0px;margin:1px 0 0 0px;}","card_css");
			Ext.util.CSS.createStyleSheet(".hj-lg-yzm{width:90px;height:32px;border:none;background:none;line-height:30px;font-family:'微软雅黑';font-size:12px;padding-left:10px;margin:1px 0 0 10px;}","card_css");
		}
	},
	// 初始化绑定事件
	initEvent:function(){
		// 登录按钮
		var logon = document.getElementById("logon");
		logon.onclick = function() {
			var username = document.getElementById("username").value;
			username = username.replace(/(^\s*)|(\s*$)/g, "");//删除左右两端的空格
			if(username == ""){
				Ext.showAlert('用户名不能为空，请重新输入用户名！');
				return ;
			}
			
			var password = document.getElementById("password").value;
			var vc = document.getElementById("vc").value;
			jobtitlelogon_me.logInCheck(username, "MD5`"+$.md5(password), vc);
		}
	},
	logInCheck:function(uid, pwd, vc){
		var bencrypt = false;
		if(Ext.isEmpty(vc) && jobtitlelogon_me.show_Validatecode == "true"){//二维码登录时，不需要验证码
			Ext.showAlert('请输入验证码！');
			return ;
			bencrypt = true;
		}
		Ext.Ajax.request({
			url: 'LoginCheck',
			method:'post',
			params: {
		        username: uid,
		        password: pwd,
		        validatecode: jobtitlelogon_me.show_Validatecode == "true"?vc:"",
		        bencrypt:bencrypt
		    },
			success: function(response, opts) {
				var obj = Ext.decode(response.responseText);
		        var errorCode = obj.errorCode;
		        if(errorCode == '1'){
		        	Ext.showAlert('用户名或密码错误，请重试！');
		        } else if(errorCode == '2') {
		        	Ext.showAlert('验证码输入错误！');
		        } else {
		        	var browser = {
					    versions:function(){
					           var u = navigator.userAgent, app = navigator.appVersion;
					           return {
					                trident: u.indexOf('Trident') > -1, 
					                presto: u.indexOf('Presto') > -1, 
					                webKit: u.indexOf('AppleWebKit') > -1, 
					                gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, 
					                mobile: !!u.match(/AppleWebKit.*Mobile.*/)||!!u.match(/AppleWebKit/), 
					                ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), 
					                android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, 
					                iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, 
					                iPad: u.indexOf('iPad') > -1, 
					                webApp: u.indexOf('Safari') == -1 
					            };
					         }(),
					   language:(navigator.browserLanguage || navigator.language).toLowerCase()
					}
					if((navigator.platform.indexOf("Win")!=0 && navigator.platform.indexOf("Mac")!=0) && browser.versions.mobile){//移动端
						window.location.href = "../../cardview/mobile/CardView.html";
					} else {//pc端
			        	window.location.href = "../../cardview/CardViewTemplate.html";
					}
		        }
		    }
		 });
	}
});