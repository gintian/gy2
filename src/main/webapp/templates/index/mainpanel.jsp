<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%	
String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=SystemConfig.getCsClientServerURL(request);
%>
<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
	<head>
		<title>Simple Tasks</title>
		<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs frameDegradeId="framedegrade"/>
	    <script type="text/javascript" src="../../ext/rpc_command.js"></script> 
<%if("1".equals(session.getAttribute("isSSO"))){// 单点登录进来才检查 %>          
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<%}%>
<style type="text/css">
.x-panel-header {
    line-height: 15px;
    background: transparent url(/images/shu_bg_bg.gif) repeat-x 0 ;/*模块条底图*/
    font-size:12px;
}		
.x-tool{overflow:hidden;width:15px;height:15px;float:right;cursor:pointer;background:transparent url(/images/tool-sprites.gif) no-repeat;margin-left:2px;}
.x-tree-icon-leaf{background-image:url(/images/wjj_c.gif)}
.x-tree-node-text{
   font-size: 12;
   padding-top:2px;
}
A.x-tree-node-text{
   color:black;
}
A.x-tree-node-text:visited{
   color:black;
}
A.x-tree-node-text:hover{
   color:black;
}

.iconClass{
   width: 18px !important;
   height: 18px !important;
   margin-top:1px !important;
}
/*ext4.2 start*/
.x-grid-body .x-grid-table-focused-first{
    border-top:none;
}
.x-grid-row-focused .x-grid-td{
    border-bottom:none;
}
.x-grid-row-before-focused .x-grid-td{
    border-bottom:none;
}
/*ext4.2 end*/
</style>

<style type="text/css">
.x-tree-node .x-tree-selected{background-color:#FFF8D2;}
.x-tree-node .x-tree-node-over{background-color:#FFF8D2;}
.x-tree-node-leaf .x-tree-node-icon{background-image:url(/images/wjj_c.gif);}
#ext-gen25{margin-top:-1px; margin-bottom:2px;}
#iframebox{width:100%;height:100%;overflow:auto;-webkit-overflow-scrolling:touch;}/*for ipad iframe scroll*/
</style>


<link href="../../ext/hr6.css" rel="stylesheet" type="text/css" />
</head>
<body>
<html:form action="/templates/index/mainpanel">
  <input type="hidden" id="cs_app" value="${sysForm.cs_app_str}" name="cs">
  <input type="hidden" id="unit" value="${sysForm.unit}">
  <hrms:hr5menu name="modulemenu"/>
  <a href="" style="display:none" id="hostname">wizard</a>  
</html:form>
<script type="text/javascript">
//防止打开多个页面造成数据混乱 guodd 2015-12-18
window.document.oncontextmenu = function(){return false;};
Ext.BLANK_IMAGE_URL="/images/s.gif";
	var unit=document.getElementById("unit").value;
	var curr_module,curr_pnl,curr_node;
	/**cmq changed at 20121111 for ipad iframe scroll*/
	//if(unit=="4")
	//{
		//自助用户
		//var il_body='<iframe src="/general/tipwizard/tipwizard.do?br_selfinfo=link" name="il_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
	//}else{
		//业务用户
		var il_body='<iframe src="/templates/index/portal.do?b_query=link" name="il_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
	//} 
    if(navigator.userAgent.match(/iPad|iPhone/i)) 
		il_body='<div id="iframebox">'+il_body+'</div>';
<%if("true".equals(framedegrade)){ //如果是70以下版本,使用ext 2.x版本 框架%>
Ext.onReady(function()
		{
		            var item1 = new Ext.Panel({
		                title: '日常业务',
		                html: '&lt;empty panel&gt;',
		                cls:'empty'
		            });
		            
		            var item2 = new Ext.Panel({
		                title: '',
		                region:'center',

		                html: il_body,
		                cls:'empty'
		            });
		                        
		            accordion = new Ext.Panel({
		                region:'west',
		                margins:'0 0 0 0',
		                width: 190,
		                minSize: 100,
		                maxSize: 244,
						collapseMode:'mini',                 
		                border:false,
		                collapsible: true,  
		                split:true,
		                layout:'accordion',
		                items:[item1]
		            });
		         
					/**function module*/
					accordion.items.clear();
					var title;
					var icon;
					for(var i=0;i<modulemenu.length;i++)
					{
						 var mbobj=modulemenu[i];
						 icon="";
						 if(mbobj.icon)
						 	icon=mbobj.icon;
						 if(icon.length>0)
						 {				
							title="<img style=\"vertical-align:middle\" src=\"/images/"+icon+"\">&nbsp&nbsp"+mbobj.text;
						 }
						 else
						 {
						 	title=mbobj.text;
						 }
					     var item6=new Ext.tree.TreePanel({
		        		 id:mbobj.text,
		                 title: title,
		                 loader: new Ext.tree.TreeLoader(),
		                 rootVisible:false,
		                 lines:false,
		                 listeners: {expand: handle},
		                 autoScroll:true
						});	
							
						item6.on("click",function(node,event){
						    <%if(isturn==null||!isturn.equals("false")) {%>   
						        if(node.attributes["hide"])
									accordion.toggleCollapse(false);
							<%}%>
							}
						);
						item6.on("expandnode",function(node){
		                     //license control.
		                     
						     if(node.attributes["mod_id"]!=undefined)
						     {
						             curr_pnl=this;
						             curr_node=node;
						             var map = new HashMap();
		        					 map.put("module",node.attributes["mod_id"]);
		                             map.put("auth_lock","true");
		                             Rpc({functionId:'1010010206',success:authorize},map);
		                     } 
							}
						);
										
					    var myroot=new Ext.tree.AsyncTreeNode({
		                            children:mbobj.children
		                        });				
						item6.setRootNode(myroot);
						accordion.items.add("item"+i,item6);
					}
							     
		            viewport = new Ext.Viewport({
		                layout:'border',
		                items:[
		                    accordion,item2]
		            });
		            /*xuj add 20140424 修复默认展开模块未占点问题*/
		            if(accordion.items.length>0){
		            	var openedPanel = accordion.items.first();
		            	_handle(openedPanel);
		            }
		});	
<% }else{ // 如果是70或以上版本 使用ext 4.x版本%>	
	var viewport;
	var title;
	var item1 = new Ext.Panel({
           title: '日常业务',
           html: '&lt;empty panel&gt;',
           cls:'empty'
        });

Ext.onReady(function(){
	
	Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
            var item2 = new Ext.Panel({
                title: '',
                region:'center',

                html: il_body,
                cls:'empty'
            });
var items =[];
for(var i=0;i<modulemenu.length;i++)
{
	var mbobj=modulemenu[i];
	if(mbobj.icon)
		mbobj.icon="/images/"+mbobj.icon;
				 var icon="";
				 if(mbobj.icon)
				 	icon=mbobj.icon;
				 if(icon.length>0)
				 {				
					title="<div style=\"font-size:12px;margin-top:3px;\">&nbsp;&nbsp;"+mbobj.text+"</div>";
				 }
				 else
				 {
				 	title=mbobj.text;
				 }
		// 创建Ext.data.TreeStore
	window.treeStore = Ext.create('Ext.data.TreeStore', {
		fields:fields,
		// 定义根节点
		root: {
			// 根节点的文本
			text: '根节点',
			expanded: true,
			// 指定根节点包含的所有子节点
			children:mbobj.children
		}
	});
	var tree = Ext.create('Ext.tree.Panel', {

		id:mbobj.text,
		title: title,
		icon:mbobj.icon,
		href:mbobj.href,
		// 不使用Vista风格的箭头代表节点的展开/折叠状态
		useArrows: false,
		href:mbobj.href,
		hrefTarget:mbobj.hrefTarget,
		mod_id:mbobj.mod_id,
		hide:mbobj.hide,
		collapsed:false,
		listeners: {
			'itemclick':function(node,event){
				    <%if(isturn==null||!isturn.equals("false")) {%>   
				        if(node.hide=='true'){
							accordion.toggleCollapse(false);
						}
					<%}%>
              }, 
              'beforeitemclick': function(view, record, item, index, e,obj){
  				var target_to_use = record.data.targetToUse;
  				var url_to_use = record.data.urlToUse;
  				var isCollapse=record.data.hide;
  				var validateType = record.data.validateType;
  				
  				if("1"==validateType)
  					return check(isCollapse,target_to_use,url_to_use);
  				
  				if("2"==validateType || "1,2"==validateType){
  					var thecodeurl ='/general/sys/validate/secondValidate.do?b_init=link';
  					var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:yes');
  					if(return_vo==null)
  						return false;
  					
  					var content=return_vo.content;
  					var inputcode=return_vo.inputcode;
  					if(content!=inputcode){
  						alert("校验码不正确!");
  						return false;
  					}
  					
  					if("1,2"==validateType)
  						return check(isCollapse,target_to_use,url_to_use);
  						
  					if('true'==isCollapse)
  						accordion.toggleCollapse(false);

  					if(target_to_use != null && target_to_use != "")
  						eval("document."+target_to_use+".location='"+url_to_use+"'");
  						
  				}
  			},            
			'expand':function(node){
					handle(this);
				     /*if(node.mod_id!=undefined)
				     {
				             curr_pnl=this;
				             curr_node=node;
				             var map = new HashMap();
        					 map.put("module",node.mod_id);
                             map.put("auth_lock","true");
                             Rpc({functionId:'1010010206',success:authorize},map);
                     } */
              },
              'itemexpand':function(node){
            	  	if(node.data.mod_id && node.data.mod_id.length>0){
      	            curr_pnl=this;
      	            curr_node=node;
      	            var map = new HashMap();
      	            map.put("module",parseInt(node.data.mod_id));
      	            map.put("auth_lock","true");
      	            Rpc({functionId:'1010010206',
      	            	success:function(response){
      	            		var value=response.responseText;
      	                var map=Ext.decode(value);
      	                
      	                if(map.succeed==false)
      	                {
      		                	alert(map.message); 
      		                	if(map.message.indexOf("重新登录")!=-1||map.message.indexOf("会话超时")!=-1)
      	                    {
      		                		var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
      		                      window.opener=null;
      		                      self.close();
      		                      return;
      	                    }
      	                    
      	                		setTimeout(function(){ node.collapse();},500);
      	                }
      	            	
      	            }},map);
      	    		}
            	  
            	  
              }
		},
		store: treeStore, // 指定该树所使用的TreeStore
		rootVisible: false,// 指定根节点可见
		iconCls:'iconClass'
	});

	items[i]=tree;
}

function check(isCollapse,target_to_use,url_to_use){
	var thecodeurl ='/general/sys/validate/validatePassword.jsp'; 
	var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:yes');
	if(return_vo==null)
		return false;
	
		var content=return_vo.content;
		var inputcode=return_vo.inputcode;
		if(content!=inputcode){
			alert("密码不正确!");
			return false;
		}
		
		if(isCollapse=='true')
			accordion.toggleCollapse(false);

		if(target_to_use != null && target_to_use != "")
			eval("document."+target_to_use+".location='"+url_to_use+"'");
}

var accordion = Ext.create('Ext.panel.Panel',{
    region:'west',
    width: 190,
    minWidth: 110,
    maxWidth: 244,
    border:false,
    header:false,
    margins:'0 0 0 0',
    collapseMode:'mini',                 
    collapsible: true,  
    split:true,
    layout:'accordion',
    layoutConfig:{
        titleCollapse:false,
        animate:false,
        activeOnTop:false
    },
    items:items
});
            viewport = new Ext.Viewport({
                layout:'border',
                items:[accordion,item2]
            });
  
  /**展开第一个菜单*/
  var root = items[0].store.getRootNode( ).childNodes[0];
  while(!root.isLeaf() && root.childNodes.length>1){
  		root.expand();
  		root = root.childNodes[0];
  }
<%if("1".equals(session.getAttribute("isSSO"))){// 单点登录进来才检查 %>          
     AxManager.checkBrowserSettings('<%=url_p%>');
<%}%>
});
	
	item1.hide();//隐藏头部panel  当其他都折叠了  头部的打开了  但是隐藏 所以还是看不见，达到所以panel都折叠的假象

<%}%>


   function getModuleNode(name)
   {
	 var node,mobj;
	 for(var i=0;i<modulemenu.length;i++)
	 {
	    var mobj=modulemenu[i];
	    if(mobj.text==name)
	    {
	    	node=mobj;
	    	break;
	    }
	 }
	 return node;   	
   }
   

   
   function handle(pnl)
   {
	 var name=pnl.id;
	 var title=pnl.title;
	 var node=getModuleNode(name);	
	 curr_module=node;
	 curr_pnl=pnl;
	 /**
	 var href,target;
	 href=node.href;
	 target=node.hrefTarget;	
	 if(href)
	 {
	 	window.open(href,target);
	 }
	 **/
	 if(node.mod_id)
	 {
        var map = new HashMap();
        map.put("module",node.mod_id);
        map.put("auth_lock","true");
        Rpc({functionId:'1010010206',success:authorize},map); 
	 }else{
		 var href=node.href;
         var target=node.hrefTarget; 
         if(href)
         {
            window.open(href,target);
         }
	 }
   }	
   function authorize(response)
   {
		var value=response.responseText;
		var map=Ext.decode(value);
		if(map.succeed==false)
		{
			alert(map.message);	
			/**20110725*/
            if(map.message.indexOf("重新登录")!=-1||map.message.indexOf("会话超时")!=-1)
            {
                var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
                window.opener=null;
                self.close();
                return;
            }
			if(curr_pnl.id=='自助服务'||curr_pnl.id=='绩效自助')
			{
				curr_node.collapse(true);
			}
			else
			{
				curr_pnl.collapse(true);
			}
							
		}
		else
		{
			 var href,target;
			 if(curr_module)
			 {
				 href=curr_module.href;
				 target=curr_module.hrefTarget;	
				 if(href)
				 {
				 	window.open(href,target);
				 }		
			 }	
		}
   }
   
   /*xuj add 20140424 修复默认展开模块未占点问题*/
   function _handle(pnl)
   {
     var name=pnl.id;
     var title=pnl.title;
     var node=getModuleNode(name);  
     curr_module=node;
     curr_pnl=pnl;
     if(node.mod_id)
     {
        var map = new HashMap();
        map.put("module",node.mod_id);
        map.put("auth_lock","true");
        Rpc({functionId:'1010010206',success:_authorize},map); 
     }
   }
   
   /*xuj add 20140424 修复默认展开模块未占点问题*/
   function _authorize(response)
   {
        var value=response.responseText;
        var map=Ext.decode(value);
        if(map.succeed==false)
        {
            //alert(map.message);  
            /**20110725*/
            if(map.message.indexOf("会话超时")!=-1)
            {
                var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
                window.opener=null;
                self.close();
                return;
            }
            if(curr_pnl.id=='自助服务'||curr_pnl.id=='绩效自助')
            {
                curr_node.collapse(true);
            }
            else
            {
                curr_pnl.collapse(true);
            }
                           
        }
   }

</script>  
</body>
</html>