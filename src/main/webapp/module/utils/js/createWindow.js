var me = new Object();
me.callBack;//返回值
me.flag;//返回值中的标志位
me.methodName;//回掉函数的方法名
me.extraParam;//除了returnValue之外的其他参数
me.count=0;
/**
 *    参数说明:
 *          id:  			弹窗的id,默认是window
 *          width：    		弹窗的宽度
 *          height：			弹窗的高度
 *          title：			弹窗的标题
 *          callBack：		弹窗关闭后回掉的处理函数（方法名）；需注意：默认会添加returnValue这个参数，所以只需要传其他参数
 *                          第一个参数为returnValue，第二个是返回值中的标志位 可有可无
 *          url：			弹窗的页面地址
 */
me.openWindow=function(configObj){
	if(Ext.isString(configObj)){
		me.config = Ext.decode(configObj);
	}else{
		me.config = configObj;
	}
	/**
	 * 创建window
	 */
	var id = me.config.id?me.config.id:"window";//me.count==0?"window":"window"+me.count++;
	var resizable = me.config.resizable ? me.config.resizable : false;
	Ext.create('Ext.window.Window',{
		id:id,
		title:me.config.title,
		height:me.config.height,
		width:me.config.width,
		resizable:resizable,//调整窗口大小
		modal:true,//覆盖弹窗背后的一切
		autoScroll:false,
		autoShow:true,
		autoDestroy:true, 
		html:'<iframe frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+me.config.url+'"></iframe>',
		listeners:{
			beforeshow:{
				fn:function(){
					var temIndex = me.config.callBack.indexOf("(");
					me.methodName = me.config.callBack.substring(0,temIndex);
					me.extraParam = me.config.callBack.substring(temIndex+1,me.config.callBack.length-1);
				}
			},
			beforeclose:{
				fn:function(){
					if(!Ext.isEmpty(me.callBack)){//排除点击窗口关闭按钮关闭窗口的情况
						if(Ext.isEmpty(me.flag)){
							if(Ext.isEmpty(me.extraParam))//判断是否还有其他参数
								eval(me.methodName)(me.callBack);//回调函数 
							else
								eval(me.methodName)(me.callBack,me.extraParam);
						}
						else{
							if(Ext.isEmpty(me.extraParam))
								eval(me.methodName)(me.callBack,me.flag);
							else
								eval(me.methodName)(me.callBack,me.flag,me.extraParam);
						}
					}
					//回调方法执行完成后，将返回值置空，防止同一页面返回值拼接在一起
					me.callBack = "";
			  }
			}
		}
	});
}
/**
 * 设置窗口关闭后的回调函数参数   returnValue是弹窗的返回值   flag是弹框返回值中的标志位字段
 */
me.setCallBack = function(config){
	var tem;
	if(Ext.isString(config)){
		tem = Ext.decode(config);
	}else{
		tem = config;
	}
	var returnValue = tem.returnValue;
	if(returnValue==null){
		return;
	}
	me.callBack = returnValue;

	var flag = tem.flag;
	me.flag=flag;
}
