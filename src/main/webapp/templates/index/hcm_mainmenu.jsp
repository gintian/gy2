<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%  
String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
//主题皮肤
String themes = "default";
%>
<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
    <head>
        
        <link href="/css/hcm/themes/<%=themes %>/layout.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all.css" />

        <!--link rel="stylesheet" type="text/css" href="/ext/resources/css/slate.css" />
        <script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script> -->
        <script type="text/javascript" src="/ext/ext-all.js"></script>
        <script type="text/javascript" src="/ext/rpc_command.js"></script>      
<style type="text/css">

.x-panel-header {
    line-height: 15px;
    background: transparent url(/images/shu_bg_bg.gif) repeat-x 0 ;/*模块条底图*/
    font-size:12px;
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

    <div class="leftbar">
        
        <div id="treemenu" style="height: 100%;width:800px"></div>
    </div> 
    <hrms:hcmmenu menu_id="13" target="il_menu" max_menu="5" themes="<%=themes %>" name="mbobj" />
<script type="text/javascript">

    var viewport;
    var accordion ;
    /**空白图片*/
        Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
    var curr_module,curr_pnl,curr_node;
        
Ext.onReady(function()
{
        var hrtree=new Ext.tree.TreePanel({
        	el:"treemenu",
            loader: new Ext.tree.TreeLoader(),
            rootVisible:false,
            lines:false,
            listeners: {expand: handle},
            autoScroll:true
           }); 
               
        hrtree.on("click",function(node,event){
               
               });
        hrtree.on("expandnode",function(node){
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
               });
                           
           var myroot=new Ext.tree.AsyncTreeNode({
                       children:mbobj
                   });             
           hrtree.setRootNode(myroot);
           hrtree.render();
      
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
                var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
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