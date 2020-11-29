
//查找登录页面路径(当父窗口的路径和子窗口一致时，即找到了当前浏览器中的地址（登录URL）)
var temPath = window.location.href;
var tem  = "window";
var temObj;
function temfun(){
	try{//temObj.location异常
		for(var i = 0;i<100;i++){
			tem += ".parent";
			temObj = Ext.decode(tem);
				if(temObj.location.href != temPath)
					temPath = temObj.location.href;
				else
					break;
		}
	}catch(ex){
		return;
	}
}
temfun();
//登录页面路径
var loginLocation = temPath.replace(".do",".jsp");
//防止请求完成后已经提示超时，而此时页面js报错导致同时触发requestexception而引起弹出两次超时提示
var gloFlag = false;

Ext.Ajax.on({
	requestcomplete:{//请求完成(主要针对点击链接进入页面)
		fn:function(connection,response,params){
			try{
				var result = Ext.decode(response.responseText);
				if(!Ext.isEmpty(result) && result.message == "未登录---->重新登录!"){
					alert("会话超时,请重新登录!");
					var newwin=window.open(loginLocation,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
		             window.opener=null;
		             self.close();
		             gloFlag = true;
		             return;
				}
			}catch(ex){//排除掉调用冬冬写的  codeselector.js或其他Ext公用控件时重写responseText而导致的页面报错问题
				return;
			}
		}
	},
	requestexception:{//请求异常(针对在页面上进行的一系列异步操作) 
		fn:function(connection,response,params){
			var result = response.responseText;
			if(!Ext.isEmpty(result) && result.indexOf("<!DOCTYPE html PUBLIC ") != -1 && !gloFlag){
				alert("会话超时,请重新登录!");
				var newwin=window.open(loginLocation,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
	             window.opener=null;
	             self.close();
	             return;
			}
		}
	}
});