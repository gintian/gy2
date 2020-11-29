<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%  
String aurl = (String)request.getServerName();
String port=request.getServerPort()+"";
String prl=request.getProtocol();
int idx=prl.indexOf("/");
prl=prl.substring(0,idx);
String url_p=SystemConfig.getCsClientServerURL(request);
UserView userView=(UserView)session.getAttribute(WebConstant.userView);

String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
//主题皮肤
String themes = "default";
if(userView != null){ 
    themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());   
 }

%>
<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
    <head>
        <title>Simple Tasks</title>
        <!-- 
        <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />

        <script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script>
        <script type="text/javascript" src="../../ext/ext-all.js"></script>
        <script type="text/javascript" src="../../ext/rpc_command.js"></script>    
<link rel="stylesheet" type="text/css" href="/css/hcm/themes/<%=themes %>/menu.css" />
<link rel="stylesheet" type="text/css" href="/css/hcm/themes/<%=themes %>/content.css" />
<style type="text/css">
#ext-gen25{margin-top:-1px; margin-bottom:2px;}
#iframebox{width:100%;height:100%;overflow:auto;-webkit-overflow-scrolling:touch;}/*for ipad iframe scroll*/
.x-panel-header{
    border-width:0 0px 0 0;
}
.x-panel-body{
    border-width:0 0px 0 0;
}
.x-tree-node a span, .x-dd-drag-ghost a span{
    padding-right:20px;
}
.x-tree-node{
    text-align:right;
    border-bottom:0;
}
.x-tree-node-expanded{
    border-bottom:0;
}
.x-tree-node-leaf .x-tree-node-icon{
    background-image:none;
}

.x-tree-node-expanded .x-tree-node-icon{
    background-image:none;
}
.x-tree-node-collapsed .x-tree-node-icon{
    background-image:none;
}
</style>
 -->
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 
 function SetIEOpt()
 {
    var obj=document.getElementById('SetIE'); 
    if (obj != null)
    {
       obj.SetIEOptions('<%=url_p%>');      
    }     
 }
 </script>
</head>
<body onload='InitAx();'>
      <hrms:hcmmenu menu_id="9003" menutype="menuitem" themes="<%=themes %>" name="mbobj" />
      <hrms:priv func_id="000107,3017,0B4,0B401,0B405" module_id="">
        <div id='axc' style='display:none'/>
      </hrms:priv>
<script type="text/javascript">

/*
	var viewport;
	Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
	var curr_module,curr_pnl,curr_node;
	var il_body='<iframe src="" name="" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';

	if(navigator.userAgent.match(/iPad|iPhone/i)) 
	    il_body='<div id="iframebox">'+il_body+'</div>';
        
Ext.onReady(function()
{
	var hcmcenter = new Ext.Panel({
        title: '',
        border:false,
        region:'center',
        html: il_body,
        cls:'empty'
    });
    
    var hcmtree=new Ext.tree.TreePanel({
        loader: new Ext.tree.TreeLoader(),
        rootVisible:false,
        lines:false,
        cls:'empty',
        border:false,
        listeners: {expand: handle}
       }); 
           
    hcmtree.on("click",function(node,event){
           
           });
    hcmtree.on("expandnode",function(node){
            if(node.attributes["mod_id"]!=undefined)
            {
                    curr_pnl=this;
                    curr_node=node;
                    var map = new HashMap();
                    map.put("module",node.attributes["mod_id"]);
                    map.put("auth_lock","true");
                    Rpc({functionId:'1010010206',success:authorize},map);
            } 
           });
                       
       var myroot=new Ext.tree.AsyncTreeNode({
                   children:mbobj
               });             
       hcmtree.setRootNode(myroot);
       
       var hcmmenu = new Ext.Panel({
           title: '<h1 style="text-align:center;font-color:#fff;">个人设置</h1>',
           split:false,
           width: 130,
           region:'west',
           minSize: 130,
           maxSize: 130,
           collapsible: false,
           collapseMode:'mini', 
           margins:'0 0 0 0',
           layoutConfig:{
               animate:true
           },
           items:[hcmtree],
           cls:'empty',
           border:true,
           autoScroll:true
       }); 
    viewport = new Ext.Viewport({
        layout:'border',
        items:[hcmmenu,hcmcenter]
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
         if(map.message=="会话超时,请重新登录!")
         {
             var newwin=window.open(window.location,"_top","toolbar=no,location=0,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=yes");
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

*/
</script>  

</body>
</html>