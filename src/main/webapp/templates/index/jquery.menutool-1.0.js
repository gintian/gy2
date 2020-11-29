/**
 * 动态创建jquery menutool
 */
  
(function($){
	
	/**
	 * 依据菜单id查找对应菜单json对象
	 * for href等相关信息
	 */
	function findMenuItem(id,value)
	{
		var menuitem,child;
		for (var i=0;i<value.length;i++)
		{
			menuitem=value[i];
			if(id==menuitem.id)
				break;
			child=value[i].children;
			if(!(typeof(child)=="undefined"))
			{
				menuitem=findMenuItem(id,child);
				if(id==menuitem.id)
					break;				
			}
		}
		return menuitem;
	}
	/**
	 * 生成二级菜单项
	 * {id:'sss',text:'',iconCls:'',href:'',disabled:true|false,onclick:''}
	 * 如果text内容为"-"时，则为菜单分隔符
	 * 1.<div id="mm" class="easyui-menu" style="width:120px;">  
   <div>New</div>  
   <div>  
        <span>Open</span>  
        <div style="width:150px;">  
           <div><b>Word</b></div>  
            <div>Excel</div>  
            <div>PowerPoint</div>  
        </div>  
    </div>  
    <div data-options="iconCls:'icon-save'">Save</div>  
    <div class="menu-sep"></div>  
    <div>Exit</div>  
   </div>  
	 */
	function outMenuItem(child,parent){
		var content;
		var menuid="menu_"+parent.id;
		var cchild;
		/*
		 *<div id="mm" class="easyui-menu" style="width:120px;">  
		 *</div>
		 */
		content="<div id=\""+menuid+"\" class=\"easyui-menu\" style=\"width:120px;\">";
		for(var i=0;i<child.length;i++)
		{

			if(child[i].text=="-")
			{
				content=content+"<div class=\"menu-sep\">";
			}
			else
			{
				content=content+"<div id=\""+child[i].id+"\" data-options=\"iconCls:'"+child[i].iconCls+"',text:'"+child[i].text+"',id:'"+child[i].id+"',href:'#'\">";
				content=content+child[i].text;
			}
			/**
			 *<div data-options="iconCls:'icon-save'">Save</div> 
			 */
			cchild=child[i].children;				
			if(!(typeof(cchild)=="undefined"))
			{
				content=content+outSubMenuItem(cchild);
			}
			content=content+"</div>";
		}
		content=content+"</div>";
		return content;
	}
	/**
	 * 子菜单递归
	 * <div style="width:150px;"> 
     *  ...  
     * </div> 
	 */
	function outSubMenuItem(menuitem){
		var content;
		var child;
		content="<div style=\"width:150px;\">";

		for(var i=0;i<menuitem.length;i++)
		{
			if(menuitem[i].text=="-")
			{
				content=content+"<div class=\"menu-sep\">";
			}
			else
			{			
				content=content+"<div id=\""+menuitem[i].id+"\" data-options=\"iconCls:'"+menuitem[i].iconCls+"',text:'"+menuitem[i].text+"',id:'"+menuitem[i].id+"',href:'#'\" >";
				content=content+menuitem[i].text;
			}
			child=menuitem[i].children;				
			if(!(typeof(child)=="undefined"))
			{
				content=content+outSubMenuItem(child);
			}
			content=content+"</div>";			
		}
		content=content+"</div>";		
		return content;
	}
	
	$.fn.addMenuTool= function(value) {
		if(typeof(value)=="undefined")
			return;		
		var menuitem;
		var content;
		content="";
		for(var i=0;i<value.length;i++)
		{
			menuitem=value[i];
			var cchild=menuitem.children;
			content=content+"\r\n"+"<a href=\""+menuitem.href+"\"  id=\"";
			content=content+menuitem.id+"\" plain=\"true\" target=\""+menuitem.target+"\">"+menuitem.text+"</a>";
			if(!(typeof(cchild)=="undefined"))
			{
				$(this).append(outMenuItem(cchild,menuitem));				
			}
		}
		$(this).append(content);
		/**设置菜单样式
		 * $('#btn').linkbutton({  
				iconCls: 'icon-search'  
			});  
		 */
		for(var i=0;i<value.length;i++)
		{
			menuitem=value[i];
			var menuitemid='#'+menuitem.id;
			var cchild=menuitem.children;
			if(typeof(cchild)=="undefined")
			{
				$(menuitemid).linkbutton({iconCls:menuitem.iconCls});
		        $(menuitemid).data(menuitem);
			}			
		    else
		    {
				var menuid="#menu_"+menuitem.id;		    	
				$(menuitemid).menubutton({iconCls: menuitem.iconCls,menu:menuid}); 
		        $(menuitemid).data(menuitem);
		        $(menuid).data(menuitem);	
				/**菜单事件绑定*/
				$(menuid).menu({  
					onClick:function(item){
						var menus=$(this).data();
						
						var menuitemid=item.id;
						var child=menus.children;
						if(!(typeof(child)=="undefined"))
						{
					    	var obj=findMenuItem(menuitemid,menus.children);
							var href=obj.href;
							if(!(typeof(href)=="undefined"))
							{							
								$('#il_body').attr("src",href);
							}
						}
					}
				}); 
			    /**绑定click事件*/
	            $(menuitemid).bind('click', function(){ 
	            	var menuitem=$(this).data();
	            	var href=menuitem.href;
	            	/*固定一个iframe*/
					$('#il_body').attr("src",href);
				});  				
		    }
		}
	};	
	/**
	 * 主界面两级菜单
	 * 菜单样式
	 *  <div id="hj-ui-menutools">  
		    <a href="#" class="easyui-menubutton" menu="#mm" iconCls="icon-add" plain="true" onclick="alert('hello world!!');">a</a>  
		    <a href="#" class="easyui-menubutton" iconCls="icon-edit" plain="true" >b</a>  
		    <a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" >c</a>  
		</div>
	 * tab:选项卡对象
	 */
	$.fn.addTabMenuTool= function(value) {
		if(typeof(value)=="undefined")
			return;		
		/**此选项卡未定义菜单项*/
		var child=value.children;
		if(typeof(child)=="undefined")
		    return;
		var menuitem;
		var content;
		content="<div class=\"hj-ui-menutools\">";
		for(var i=0;i<child.length;i++)
		{
			menuitem=child[i];
			var cchild=menuitem.children;
			content=content+"\r\n"+"<a href=\""+menuitem.href+"\"  id=\"";
			content=content+menuitem.id+"\" plain=\"true\" target=\""+menuitem.target+"\">"+menuitem.text+"</a>";
			if(!(typeof(cchild)=="undefined"))
			{
				$(this).append(outMenuItem(cchild,menuitem));				
			}
		}
		content=content+"</div>";
		$(this).append(content);
		/**设置菜单样式
		 * $('#btn').linkbutton({  
				iconCls: 'icon-search'  
			});  
		 */
		for(var i=0;i<child.length;i++)
		{
			menuitem=child[i];
			var menuitemid='#'+menuitem.id;
			var cchild=menuitem.children;
			if(typeof(cchild)=="undefined")
			{
				$(menuitemid).linkbutton({iconCls:menuitem.iconCls});
		        $(menuitemid).data(menuitem);
			}			
		    else
		    {
				var menuid="#menu_"+menuitem.id;		    	
				$(menuitemid).menubutton({iconCls: menuitem.iconCls,menu:menuid}); 
		        $(menuitemid).data(menuitem);
		        $(menuid).data(menuitem);	
				/**菜单事件绑定*/
				$(menuid).menu({  
					onClick:function(item){
						var menus=$(this).data();
						
						var menuitemid=item.id;
						var child=menus.children;
						if(!(typeof(child)=="undefined"))
						{
					    	var obj=findMenuItem(menuitemid,menus.children);
							var href=obj.href;
							if(!(typeof(href)=="undefined"))
							{							
								$('#il_body').attr("src",href);
							}
						}
					}
				}); 
			    /**绑定click事件*/
	            $(menuitemid).bind('click', function(){ 
	            	var menuitem=$(this).data();
	            	var href=menuitem.href;
	            	/*固定一个iframe*/
					$('#il_body').attr("src",href);
				});  				
		    }
		}
	};
	
	/*
	 * 动态增加选项卡
	 * 选项卡片属性值
	 * {id:'aaa',title:'',content:'',href:'',cache:true|false,iconCls:'',
	 * width:xx,height:yy,closable:true|false,selected:true|false}
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
				//var content = '<iframe scrolling="auto" frameborder="0" src="'+url+'" style="width:100%;height:100%;"></iframe>'; 
				$(this).tabs('add',{title:title,id:tabobj.id,iconCls:tabobj.iconCls,closable:tabobj.closable});//.data(tabobj);
				$("#"+tabobj.id+"").addTabMenuTool(tabobj);
				$("#"+tabobj.id+"").data(tabobj);
			} 					
		}
		//end.
		/**
		 * 选项卡切换
		 */
		$(this).tabs({
			onSelect: function(title,index){
				var target = this;
				var tab = $(this).tabs('getSelected');
			    var tabid="#"+tab.panel('options').id;
			    var tabitem=$(tabid).data();
			    if((typeof(tabitem.href)=="undefined"))
			    	return;
				var child=tabitem.children;				
				if(!(typeof(child)=="undefined"))
				{
					var height=$(document.body).height()-159;				
				}
				else
				{
					var height=$(document.body).height()-127;						
				}
				$("#il_body").remove();
				$(tabid).append("<iframe id=\"il_body\" scrolling=\"auto\" frameborder=\"0\"  name=\"il_body\" src=\""+tabitem.href+"\" height=\""+height+"px\" width=\"100%\"></iframe>");
			  }
		});		
	};
	/**
	 * 动态创建门户面板
	 * [
	 *  {
	 *    id:'xxx',
	 *    colwidth:20,
	 *    name:'',
	 *    children:[
	 *    {
	 *         id:'',
	 *         name:'',
	 *         height:200,
	 *         url:'',
	 *         iconCls:''
	 *    },
	 *       ......
	 *    ]
	 *  },
	 *  {
	 *   ......
	 *  } 
	 * ]	 
	 */
	$.fn.addPortal=function(value){
		if(typeof(value)=="undefined")
			return;
		/**门户列*/
	    

		
		var col=0;
		var lastheight=0;
		for(var i=0;i<value.length;i++)
		{
		   var child=value[i].children;
		   if(typeof(child)=="undefined")
		       return;

		   var width=value[i].colwidth*100;
		   //<div style="width:33%"></div>  
		   var columnitem=$('<div id=\"portal-column\" style=\"width:'+width+'%\"></div>').appendTo(this); 
		   var sheight=0;
		   for(var j=0;j<child.length;j++)
		   {
		   	   var panelitem=child[j];
		   	   sheight=sheight+panelitem.height+10;
			   var content="<iframe id=\"il_body\" scrolling=\"auto\" frameborder=\"0\"  name=\"il_body\" src=\""+panelitem.url+"\" height=\"100%\" width=\"100%\"></iframe>";
			   var pnl = $('<div></div>').appendTo(columnitem);  
			   pnl.panel({
			   	   id:panelitem.id,
			       title: panelitem.name,  
			       height:panelitem.height,
			       iconCls:panelitem.iconCls,
			       border:false,
			       content:content,
			       /*
				   tools:[{  
							iconCls:'icon-add',  
							handler:function(){alert('new');}  
						}],*/        
			       collapsible: true 
			   });
			   $(this).portal('add',{  
			       panel: pnl, 
			       columnIndex:col 
			   });  			   
		   }//for j
		   col++;
		   lastheight=Math.max(sheight,lastheight);
		}//for i
		
		$(this).portal({
			border:false,
			fit:true
	    }); 
		
		/**设置面板高度*/
		//$(this).height(lastheight);
	    $(this).portal('resize');   

	};
  	
})(jQuery);

  
  
  