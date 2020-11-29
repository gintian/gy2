<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="/ext/resources/css/slate.css" />
<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all.js"></script>
<script type="text/javascript" src="/ext/TreeCheckNodeUI.js"></script>
<script type="text/javascript" src="/ext/rpc_command.js"></script>    
<script type="text/javascript" src="/ajax/basic.js"></script>    
<script language="jscript">
var hrtree;
var content="<div id='mycontent'></div>";
     function openpriv(flag,role_id)
     {
    	var target_url="/workbench/query/hquery_interface.do?b_query=link&a_query=3&a_flag="+flag+"&role_id="+role_id;
    	window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=no,width=780,height=500'); 
     }
      
	function saveok(response)
	{
	    alert("操作成功！");
		var value=response.responseText;
		var map=Ext.util.JSON.decode(value);
		if(map.succeed)
		{
		}
		
	}

    function immediatelysave(selstr,checked)
    {
          var map = new HashMap();
    	  map.put("user_flag", "<bean:write name="resourceForm" property="flag"/>");
    	  map.put("role_id","<bean:write name="resourceForm" property="roleid"/>");
    	  map.put("res_flag","<bean:write name="resourceForm" property="res_flag"/>");
    	  map.put("selstr", getEncodeStr(selstr));
    	  map.put("imme", "1");
    	  map.put("checked", checked);
   　       Rpc({functionId:'10400201023',success:saveok},map);        
    }
    
    function save(selstr)
    {
          var map = new HashMap();
    	  map.put("user_flag", "<bean:write name="resourceForm" property="flag"/>");
    	  map.put("role_id","<bean:write name="resourceForm" property="roleid"/>");
    	  map.put("res_flag","<bean:write name="resourceForm" property="res_flag"/>");
    	  map.put("selstr", selstr);
    	  map.put("imme", "0");
    	  alert("22---77"+selstr);
   　     // Rpc({functionId:'1010010048',success:saveok},map);        
    }    
	Ext.BLANK_IMAGE_URL="/ext/resources/images/default/s.gif"; 
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
    function onItemClick(item)
   	{
			saveSelect();
   	}
    var nodeObj=null;
    var checkObj=false;
    function chkchg(node,checked)
    {
      nodeObj=node;
      checkObj=checked;
    }
    function saveSelect()
    {
        var checkedNodes = hrtree.getChecked();
		var s = [];
	    var tmp;
		for(var i=0;i<checkedNodes.length;i++){
			 tmp=checkedNodes[i].id;
			 s.push(tmp)
		}
		var temp=","+s.toString()+",";
    	immediatelysave(temp,"1");
    }
    
	Ext.onReady(function()
	{
   			    var chkmode="childCascade";
   			    var condvisible=true;
   			    var bfun=false;
   			    var openc=true;
   			    var savec=false;
                 condvisible=false;    
                 //bfun=true;  
                 savec=true;
    			 function onCondClick(item){
					openpriv('<bean:write name="resourceForm" property="flag"/>','<bean:write name="resourceForm" property="roleid"/>');
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
    			 var myloader=new Ext.tree.TreeLoader({dataUrl:'/system/fieldortemplateservlet?roleid=<bean:write name="resourceForm" property="roleid" />&flag=<bean:write name="resourceForm" property="flag"/>&res_flag=<bean:write name="resourceForm" property="res_flag"/>',
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

                 
			     hrtree=new Ext.tree.TreePanel({
			     el:'treemenu',
        		 id:"func",
        		 <logic:equal name="resourceForm" property="res_flag" value="22">
                 title:'<bean:message key="sys.res.khmodule"/>',
                 </logic:equal>
                 <logic:equal name="resourceForm" property="res_flag" value="23">
                 title:'<bean:message key="sys.res.khfield"/>',
                 </logic:equal>
                 tbar: [{
		            id:'save',
		            text: '<bean:message key="button.save"/>',
		            handler:onItemClick,
		            cls:'mybutton',		 		            
		            scope: this}	            		            		            
		         ],  	        
       			 checkModel: chkmode, 
                 loader:myloader,                              
				 rootVisible:false,
                 autoScroll:true,
                 listeners: {checkchange: chkchg} 
				});		
				
			    var myroot=new Ext.tree.AsyncTreeNode({
			                id:"root",
       					    text:"根节点"
                });				
				hrtree.setRootNode(myroot);
           	    hrtree.render();
				myroot.expand();				
	});
</script>	    
	    
<html:form action="/selfservice/lawbase/lawtext/assign_law_dir">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
         <tr>
           <td align="left"> 
            <div id="treemenu"> 
            </div>             
           </td>
           </tr>           
    </table>  
</html:form>