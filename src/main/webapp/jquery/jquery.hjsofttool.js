  
(function($){
	
	/*
	 * 动态增加选项卡
	 * 选项卡片属性值
	 * [
	 *   {id:'aaa',text:'',content:'',href:'',cache:true|false,iconCls:'',width:xx,height:yy,closable:true|false,selected:true|false},
	 *   {id:'aaa',text:'',content:'',href:'',cache:true|false,iconCls:'',width:xx,height:yy,closable:true|false,selected:true|false}
	 * ]
	 */
	$.fn.addTab=function(value){
		var url="";
		var title="";
		var tabobj;
		if(typeof(value)=="undefined")
			return;
		/*数据绑定在选项卡对象上*/
		$(this).data(value);
		/*一级菜单显示*/
		for(var i=0;i<value.length;i++)
		{
			tabobj=value[i];
			title=tabobj.text;
			if ($(this).tabs('exists', title))
			{ 
				$(this).tabs('select', title); 
			} 
			else 
			{ 
				$(this).tabs('add',{title:title,id:tabobj.id,iconCls:tabobj.iconCls});//.data(tabobj);
				$("#"+tabobj.id+"").data(tabobj);				
			} 					
		}
		//end.
		/**
		 * 选项卡切换
		 */
		$(this).tabs({
			width: $(window).width()-10,  
			height: $(window).height()-5,  
			onSelect: function(title,index){
				var target = this;
				var tab = $(this).tabs('getSelected');
			    var tabid="#"+tab.panel('options').id;
			    var tabitem=$(tabid).data();
			    if((typeof(tabitem.href)=="undefined"))
			    	return;
  			    $("#il_body").remove();
				$(tabid).append("<iframe id=\"il_body\" scrolling=\"auto\" frameborder=\"0\"  name=\"il_body\" src=\""+tabitem.href+"\" height=\"100%\" width=\"100%\"></iframe>");
			  }
		});		
	};

})(jQuery);

  
  
  