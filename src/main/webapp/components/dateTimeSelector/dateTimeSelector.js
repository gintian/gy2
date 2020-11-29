/**
 * 使用示例：
 * <input name="cccc" >
 * <img src="/images/add.gif" plugin="datetimeselector" id="abcde" inputname="cccc" format="Y-m-d" afterfunc="callback"/>
 * 
 * 必须的属性：plugin、inputname、format（日期格式）
 * 可选属性：afterfunc 选择日期后的回调函数，参数为所选日期
 *         spaceselect 空格选择系统时间 true/false 默认为true
 * 
 * format格式：
 * Y-m-d H:i:s
 * 
 * Y：年 4位  2001
 * y：年 2位  01
 * 
 * m：月 2位 01-12
 * 
 * d：日 2位 01-31
 * j：日 缩写，前面没有零 1-31
 * 
 * H：小时 24小时制
 * h：小时 12小时制
 * 
 * i：分钟
 * 
 * s：秒
 * 
 * 
 */

Ext.onReady(function(){
	Ext.Loader.setPath("EHR",'/components');
	var imgEles = Ext.query('img[plugin=datetimeselector]');//选择img元素，并且plugin属性为“datetimeselector”
	
	var selector = new DateTimeSelector();
	for(var i=0;i<imgEles.length;i++){
		 var ele       = imgEles[i],
		     inputName = ele.getAttribute("inputname"),
		     format    = ele.getAttribute("format"),
		     afterfunc = ele.getAttribute("afterfunc"),
		     spaceselect = ele.getAttribute("spaceselect"),
		     viewEles  = document.getElementsByName(inputName);
		     
		 if(viewEles.length<1)
			 continue;
		 ele.style.cursor='pointer';	 
		 //绑定鼠标点击事件
		 Ext.EventManager.addListener(ele,'click','showSelector',selector,[inputName,format,afterfunc,spaceselect,viewEles[0]]);
	 }
	 //初始化事件
	selector.initEvent(); 
	
	
});


function setDateEleConnect(idList){
	var selector = new DateTimeSelector();
	for(var index=0;index<idList.length;index++){
		 var ele    = document.getElementById(idList[index]);
		 if(!ele)continue;
		 var inputName = ele.getAttribute("inputname"),
		     format    = ele.getAttribute("format"),
		     afterfunc = ele.getAttribute("afterfunc"),
		     spaceselect = ele.getAttribute("spaceselect"),
		     viewEles  = document.getElementsByName(inputName);
		 if(viewEles.length<1)
			 continue;
		 ele.style.cursor='pointer';	 
		 //绑定鼠标点击事件
		 Ext.EventManager.addListener(ele,'click','showSelector',selector,[inputName,format,afterfunc,spaceselect,viewEles[0]]);
		}
		//初始化事件
	selector.initEvent(); 
}




function DateTimeSelector(){
	this.selector = undefined;
	this.datePicker = undefined;
}

/**
 * 初始化事件
 */
DateTimeSelector.prototype.initEvent = function(){
	var me = this;
	//页面单击事件
	Ext.getDoc().on("mousedown",function(e,t,o){
		 //如果selector存在 && 触发单击事件元素不是文本框输入框  && 触发单击事件元素 不是 selector和selector的子元素   则销毁selector
		 if(me.selector && t!=me.viewEle && !me.selector.owns(t)){
			 me.selector.close();
			 me.selector = undefined;
			 me.datePicker = undefined;
		  }
	 
	 });
	Ext.getDoc().on("mousewheel",function(a,b,c){if(c[0].selector)c[0].selector.close();},window,[me]);
};

DateTimeSelector.prototype.showSelector = function(evt,evtEle,opt){
	var me        = this,
	    inputname = opt[0],
	    format    = opt[1],
	    afterfunc    = opt[2],
	    spaceselect = opt[3],
	    viewEle   = opt[4],
	    position  = Ext.get(viewEle).getXY();//获取 文本框 坐标
	me.datePicker = Ext.create("EHR.extWidget.picker.DateTimePicker",{
			format:format,
			style:'z-index:1000000000',
			listeners:{
				select:function(){
					viewEle.value = Ext.util.Format.date(this.value,format);
					if(afterfunc)
						Ext.callback(eval(afterfunc),null,[viewEle.value]);
				    this.destroy();
					me.selector.close();
				}
			}
		});
	me.selector = Ext.widget('panel',{
		border:false,
		floating:true,
		shadow:false,
		//width:180,
		items:me.datePicker
		});

	me.selector.render(document.body);
	if(viewEle.value.length>0 && Ext.Date.parse(viewEle.value,format,true))
	   me.datePicker.setValue(Ext.Date.parse(viewEle.value,format),true);
	else
	   me.datePicker.setValue(new Date(),true);
	//定位  
	me.selector.alignTo(viewEle,'tl-bl?',undefined);
	if(!spaceselect || spaceselect=='true')
	  me.datePicker.focus();
}



