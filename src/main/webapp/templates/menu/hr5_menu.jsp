<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>    
	<head>
		<title>Simple Tasks</title>
<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs/>

</head>
<body>
  <hrms:hr5menu name="modulemenu"/>

<script type="text/javascript">

var viewport;
/**空白图片*/
Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
   function turn()
   {
	if(parent.myBody.cols != '30,*')
	{
		parent.myBody.cols = '30,*';
	}
	else
	{
		parent.myBody.cols = '170,*';
	}
   }    

Ext.onReady(function()
{
            var item1 = new Ext.Panel({
                title: '日常业务',
                html: '&lt;empty panel&gt;',
                cls:'empty'
            });
            
            var accordion = new Ext.Panel({
                region:'center',
                margins:'0 0 0 0',
                split:true,
                width: 180,
                title:'Hr\'s ToolBox',
                layout:'accordion',
                items:[item1]
            });
         
			/**function module*/
			accordion.items.clear();
			for(var i=0;i<modulemenu.length;i++)
			{
				 var mbobj=modulemenu[i];
			     var item6=new Ext.tree.TreePanel({
        		 id:mbobj.text,
                 title: mbobj.text,
                 loader: new Ext.tree.TreeLoader(),
                 rootVisible:false,
                 lines:false,
                 autoScroll:true
				});		
				item6.on("click",function(node,event){
						alert("您点击了"+node.text);
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
                    accordion]
            });
});	


</script>
</body>
</html>
