<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
	    <link rel="stylesheet" type="text/css" href="../../../ext/resources/css/ext-all-old.css" />

	    <link rel="stylesheet" type="text/css" href="../../../ext/resources/css/slate.css" />
 
	    <script type="text/javascript" src="../../../ext/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="../../../ext/ext-all-old.js"></script>
	    <script type="text/javascript" src="../../../ext/TreeCheckNodeUI.js"></script>
	    <script type="text/javascript" src="../../../ext/rpc_command.js"></script> 
<logic:notEqual name="agentForm" property="operate" value="2">  
<style type="text/css">
.x-tree-node-icon
{
	display:none;
}
</style>	
</logic:notEqual>
<logic:equal name="agentForm" property="operate" value="2">  
<style type="text/css">
.x-grid-cell.first-level .x-tree-node-icon
{
	display:none;
}
.x-grid-cell.second-level .x-tree-node-icon
{
	display:none;
}
</style>	
</logic:equal>
<script language="jscript">
     function openpriv(flag,role_id)
     {
    	var target_url="/workbench/query/hquery_interface.do?b_query=link&a_query=3&a_flag="+flag+"&role_id="+role_id;
    	window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=no,width=780,height=500'); 
     }
      
	function saveok(response)
	{
		var value=response.responseText;
		var map=Ext.util.JSON.decode(value);
		if(map.succeed)
		{		    		
			alert(map.message);           			
		}
		
	}
    
    function immediatelysave(selstr,checked)
    {
          var map = new HashMap();    	  
    	  map.put("id","<bean:write name="agentForm" property="id"/>");    	
    	  map.put("selstr", selstr);
    	  map.put("imme", "1");
    	  map.put("checked", checked);    	
   　       Rpc({functionId:'1101300005',success:saveok},map);        
    }
    
    function save(selstr)
    {
          var map = new HashMap();    	 
    	  map.put("id","<bean:write name="agentForm" property="id"/>");    	  
    	  map.put("selstr", selstr);
    	  map.put("imme", "0"); 
    	  <logic:equal name="agentForm" property="operate" value="2"> 
    	  Rpc({functionId:'1101300010',success:saveok},map);  
    	  </logic:equal>    	  
    	  <logic:notEqual name="agentForm" property="operate" value="2"> 
    	  Rpc({functionId:'1101300005',success:saveok},map);  
    	  </logic:notEqual>   
   　             
    }    
    
	Ext.BLANK_IMAGE_URL="../../../ext/resources/images/default/s.gif"; 
   /*ext*/
    function getChildId(node)
    {
		var s = [];
		var cs;
		var cnode;
		var tmp="";
	    if(node.childNodes.length==0)
	       return "";
    	for(var i=0;i<node.childNodes.length;i++)
    	{
    	    cnode=node.childNodes[i];
			s.push(node.childNodes[i].id);
			cs=	getChildId(cnode);
			if(cs.length!=0)
			{
				s.push(cs);
			}
    	}    	
    	return s.toString();   
    }
    function chkchg(node,checked)
    {
		var s = [];
		var cs="";
		var cnode;
		s.push(node.id);
    	for(var i=0;i<node.childNodes.length;i++)
    	{
    	    cnode=node.childNodes[i];
    	    cs=getChildId(cnode);
			s.push(node.childNodes[i].id);   
			if(cs.length!=0)
			{
				s.push(cs);
			}			 		
    	}
		var tmp=","+s.toString()+",";    	
    	var flag="0";
    	if(checked)
    	  flag="1";
    	immediatelysave(tmp,flag);
    }
    
	Ext.onReady(function()
	{
   			    var chkmode="childCascade";
   			    var condvisible=true;
   			    var bfun=false;
   			    var openc=true;
   			    var savec=false;
                bfun=true;  
                savec=true;   
                <logic:equal name="agentForm" property="operate" value="2"> 
                      //chkmode="";
                      bfun = false;
                      savec = false;
                </logic:equal> 		
    			 function onItemClick(item){
    			 	if(bfun)
    			 	{
						if(!confirm(SYS_LBL_MOPEN))    			 	
    			 		//myroot.expand(true,false);
    			 			return ;
    			 	}
					var checkedNodes = hrtree.getChecked();
					var s = [];
					var tmp;
					for(var i=0;i<checkedNodes.length;i++){
					    tmp=checkedNodes[i].id;
					    if(!condvisible)
					    {
					    	if(tmp=="ALL")
					    	  tmp="UN";
					    }
						s.push(tmp)
					}
					var tmp=","+s.toString()+",";
					save(tmp);
    			 }
    			 function onCondClick(item){
					openpriv('<bean:write name="agentForm" property="user_flag"/>','<bean:write name="agentForm" property="role_id"/>');
    			 }

    			 function onOpenClick(item){
    			    if(item.text==SYS_BTN_OPEN)
    			    {
						myroot.expandChildNodes(true);
						item.setText(SYS_BTN_CLOSE);
					}
					else
					{
						myroot.collapseChildNodes(true);
						item.setText(SYS_BTN_OPEN);						
					}

    			 }
    			     			     			 
    			 var type='funcpriv';
    			 var myloader=new Ext.tree.TreeLoader({dataUrl:'/agent/agentfunctreeservlet?role_id=<bean:write name="agentForm" property="role_id" />&flag=<bean:write name="agentForm" property="user_flag"/>&id=<bean:write name="agentForm" property="id"/>&operate=<bean:write name="agentForm" property="operate"/>',
				 baseAttrs:{uiProvider:Ext.ux.TreeCheckNodeUI},
                 listeners:{
                 "loadexception":function(loader,node,response){
                        //加载服务器数据,直到成功
                        //node.loaded = false;
                        //node.reload.defer(10,node);
                        //alert(response.status);
                    	}
					}
                 });

                 
			     var hrtree=new Ext.tree.TreePanel({
			     el:'treemenu',
        		 id:"func",
        		 <logic:equal name="agentForm" property="operate" value="2"> 
                    title: "业务授权",
                    //onlyLeafCheckable: true,               
                </logic:equal> 
                 <logic:notEqual name="agentForm" property="operate" value="2"> 
                    title: "功能",	  
                </logic:notEqual>
       			 checkModel: chkmode, 
                 loader:myloader,
                 animate:true,   
                 <logic:notEqual name="agentForm" property="operate" value="2"> 
                     listeners: {checkchange: chkchg},	  
                </logic:notEqual>                          
		         tbar: [
					{
		            id:'open',
		            text: SYS_BTN_OPEN,
		            handler:onOpenClick,
		            hidden:openc,
		            cls:'mybutton',		            
		            scope: this},		            
		            {
		            id:'save',
		            text: '<bean:message key="button.save"/>',
		            handler:onItemClick,
		            hidden:savec,		            
		            cls:'mybutton',
		            scope: this},
					{
		            id:'cond',
		            text: '<bean:message key="button.sys.cond"/>',
		            handler:onCondClick,
		            hidden:condvisible,
		            cls:'mybutton',		            
		            scope: this}
		            		            
		         ],                  
				 rootVisible:false,
                 autoScroll:true
				});		
				
			    var myroot=new Ext.tree.AsyncTreeNode({
			                id:"0",
       					    text:"根节点"
                });				
				hrtree.setRootNode(myroot);
           	    hrtree.render();
                myroot.expand(false,false);//不加载时,无法选中递归展开	
               
								
	});
</script>
<hrms:themes />
<html:form action="/selfservice/selfinfo/agent/purviewagent">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
         <tr>
           <td align="left"> 
            <div id="treemenu"> 
            </div>             
           </td>
           </tr>           
    </table>  
    <!--  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
         <tr>
           <td align="left"> 
            <input type="button" name="bc_btn1" value="返回" onclick="window.location.replace('/selfservice/selfinfo/agent/agentinfo.do?b_search=link');" class="mybutton">           
           </td>
           </tr>           
    </table> -->
</html:form>
