/**
 *  为了兼容google ff 等浏览器复写window的showdialog方法 
 *  使用前父页面需要提供全局的回调处理返回值
 *  haosl
 *  2017.02.23
 */
modalDialog = {};
/**加载配置**/
modalDialog.extend = function(dest, src) {
    var prop;
    for (prop in src) {
        if (src.hasOwnProperty(prop)) {
            dest[prop] = src[prop];
        }
    }
}
/**
 * 
 * @type  //1、Ext.window 2、原始的window.open 3、只使用Ext.window
 * @title  Ext.window下需要配置title
 */
modalDialog._default = {
        width:750,
        height:450,
        resizable:"no",
        location:"no",
        scrollbars:"no",
        status:"no",
        modal:"yes",
        title:"",
        type:'1',
        id:""
}

/**
 * @parameters theurl 
 *          iframe的路径
 * @parameters winName
 *          窗口名称
 *  @parameters config
 *          配置对象   
 */

modalDialog.showModalDialogs = function(iframe_url,winName,config,callBack){
    
//浏览器如果支持则showModalDialog建立模态化窗口，如果不支持则open一个窗口
    var configStr = "";
    modalDialog.iframe_url = iframe_url;
    window.returnValue = undefined;//首先清除掉returnValue全局变量
    modalDialog.extend(modalDialog._default,config);
    if(window.showModalDialog&&modalDialog._default.type!='3'){
        configStr+="dialogWidth:"+modalDialog._default.width+"px;";
        configStr+="dialogHeight:"+modalDialog._default.height+"px;";
        configStr+="resizable:"+modalDialog._default.resizable+";";
        configStr+="center:yes;"
        configStr+="scroll:"+modalDialog._default.scrollbars+";";
        configStr+="status:"+modalDialog._default.status+";";
        if(config && config.dialogArguments)
        	winName = config.dialogArguments;
        var returnVal = window.showModalDialog(iframe_url,winName,configStr);
        if(callBack)
            callBack(returnVal);
        if (returnVal)
        	return returnVal;
    }else{
		if(modalDialog._default.type=='1'||modalDialog._default.type=='3'){
			if(typeof window.Ext == 'undefined'){
				insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
					insertFile("/ext/ext6/ext-all.js","js" ,modalDialog.openWin);
				});
				
			} else {
				modalDialog.openWin();
			}
		}else{
	        var w = modalDialog._default.width;
	        var h = modalDialog._default.height
		    var top= window.screen.availHeight-h>0?window.screen.availHeight-h:0;
		    var left= window.screen.availWidth-w>0?window.screen.availWidth-w:0;
	        top = top/2;
	        left = left/2;
	        configStr+="width="+modalDialog._default.width+",";
	        configStr+="height="+modalDialog._default.height+",";
	        configStr+="resizable="+modalDialog._default.resizable+",";
	        configStr+="location="+modalDialog._default.location+",";
	        configStr+="top="+top+",";
	        configStr+="left="+left+",";
	        configStr+="scrollbars="+modalDialog._default.scrollbars+",";
	        configStr+="status="+modalDialog._default.status+",";
	        var sSourceURL = iframe_url;
	        if(iframe_url.indexOf("src=")>-1)
	         	sSourceURL = iframe_url.split("src=")[0]+"src="+iframe_url.split("src=")[1].replace(/／/g, "/").replace(/？/g, "?").replace(/＝/g, "=").replace(/`/g,"&");  //20140901  dengcan
	        var childWin = window.open(sSourceURL,winName,configStr);
		}
    }
}
modalDialog.openWin = function(){
			    Ext.create("Ext.window.Window",{
			    	id:modalDialog._default.id,
			    	width:parseInt(modalDialog._default.width)+20,
			    	height:modalDialog._default.height,
			    	title:modalDialog._default.title,
                    border:false,
			    	resizable:false,
			    	modal:modalDialog._default.modal,
			    	autoScroll:false,
			    	renderTo:Ext.getBody(),
			    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+modalDialog.iframe_url+"'></iframe>"
		 	    }).show();	
		  }


