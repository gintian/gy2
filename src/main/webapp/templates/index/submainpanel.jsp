<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.sys.SysForm" %>
<%	
SysForm sysForm = (SysForm)session.getAttribute("sysForm");
String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
String fromModid = sysForm.getFromModid();
String fromUrl = sysForm.getFromUrl();
sysForm.setFromModid("");//先清除form中该属性的值
sysForm.setFromUrl("");
String module = sysForm.getModule();
module+="";
boolean isCollapse = false;
if(fromModid==null || fromModid.equals("")){
	fromModid="-99999";
	fromUrl = "/html2/index.jsp";
	isCollapse = true;
}
if(fromModid.equals("-99999"))
{
%>
<script type="text/javascript">
	window.location.href="/templates/index/subportal.do?b_query=link";
	////window.location.href="/html2/index.jsp";
</script>
<%} %>

<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
	<head>
		<title>Simple Tasks</title>
	    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />

	    <!--link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" / -->
	    <script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="../../ext/ext-all.js"></script>
	    <script type="text/javascript" src="../../ext/rpc_command.js"></script> 	    
<style type="text/css">

.x-panel-header {
    line-height: 15px;
    background: transparent url(/images/shu_bg_bg.gif) repeat-x 0 ;/*模块条底图*/
    font-size:12px;
    height:24px;
    color:#3E8982;
    font-weight:bolder;
}		


.x-tool{overflow:hidden;width:15px;height:15px;float:right;cursor:pointer;background:transparent url(/images/tool-sprites.gif) no-repeat;margin-left:2px;}
.x-tree-node .x-tree-selected{background-color:#FFF8D2;}
.x-tree-node .x-tree-node-over{background-color:#FFF8D2;}
.x-tree-node-leaf .x-tree-node-icon{background-image:url(/images/wjj_c.gif);}
#ext-gen25{margin-top:-1px; margin-bottom:2px;}
#iframebox{width:100%;height:100%;overflow:auto;-webkit-overflow-scrolling:touch;}/*for ipad iframe scroll*/
</style>
</head>
<body>
<html:form action="/templates/index/submainpanel">
  <input type="hidden" id="cs_app" value="${sysForm.cs_app_str}" name="cs">
  <hrms:hr5menu name="modulemenu" fromnode="20"/>
  <a href="" style="display:none" id="hostname">wizard</a>  
</html:form>
<script type="text/javascript">

	var viewport;
	var accordion ;
	/**空白图片*/
		Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
	var curr_module,curr_pnl,curr_node;
	/**cmq changed at 20121111 for ipad iframe scroll*/
	var il_body='<iframe src="<%=fromUrl%>" name="il_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
    if(navigator.userAgent.match(/iPad|iPhone/i)) 
		il_body='<div id="iframebox">'+il_body+'</div>';
		
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
				collapsible:true,
				collapsed:<%=isCollapse%>,
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
				 var curr_mod_id=mbobj.mod_id;
				 var a=true;
				 var modId=<%=fromModid%>;
				 if(curr_mod_id==modId)
				 	a=false;
			     var item6=new Ext.tree.TreePanel({
        		 id:mbobj.text,
                 title: title,
                 loader: new Ext.tree.TreeLoader(),
                 rootVisible:false,
                 lines:false,
                 collapsed:a,
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
});	
  
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
	 /*
	 var href,target;
	 href=node.href;
	 target=node.hrefTarget;	
	 if(href)
	 {
	 	window.open(href,target);
	 }
	 */
	 /***/
	 if(node.mod_id)
	 {
        var map = new HashMap();
        map.put("module",node.mod_id);
        map.put("auth_lock","true");
        Rpc({functionId:'1010010206',success:authorize},map); 
	 }
   }	
   
   function authorize(response)
   {
		var value=response.responseText;
		var map=Ext.util.JSON.decode(value);
		if(map.succeed==false)
		{
			alert(map.message);	
			if(curr_pnl.id=='自助服务'||curr_pnl.id=='绩效自助')
			{
				curr_node.collapse(true);
			}
			else
			{
				curr_pnl.collapse(true);
			}
			/**20110725*/
			if(map.message=="会话超时,请重新登录!")
			{
				var newwin=window.open("http://localhost:8081/templates/index/epmlogon.jsp","_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
				window.opener=null;
   	    		self.close();
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
   


</script>  
</body>
</html>