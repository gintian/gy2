<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
	    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all-old.css" />

	    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" />
 
	    <script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="../../ext/ext-all-old.js"></script>
	    <script type="text/javascript" src="../../ext/TreeCheckNodeUI.js"></script>
	    <script type="text/javascript" src="../../ext/rpc_command.js"></script> 
	    <script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<logic:notEqual name="privForm" property="current_tab" value="managepriv"> 	  
<style type="text/css">
.x-tree-node-icon
{
	display:none;
}
</style>	
</logic:notEqual>    
<hrms:themes></hrms:themes>
<script type="text/javascript">
	function pegging(name,id){
		//alert(name+"  "+id);
		var flag = 'func';
		var theurl="/system/security/pegging.do?b_query=link`name="+name+"`id="+id+"`flag="+flag;
		var return_vo;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
		var dw=660,dh=540,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		if(window.showModalDialog){
			 window.showModalDialog(iframe_url,1, 
		        	  "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	     }else{
	    	 window.open(iframe_url,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
	     }
	    
		
	}
     function openpriv(flag,role_id)
     {
    	var target_url="/workbench/query/hquery_interface.do?b_query=link&a_query=3&a_flag="+flag+"&role_id="+$URL.encode(role_id);
    	var iWidth=730; //弹出窗口的宽度;
    	var iHeight=460; //弹出窗口的高度;
    	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
    	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
    	window.open(target_url,'_blank',"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft); 
     }
      
	function saveok(response)
	{
		var value=response.responseText;
		var map=Ext.util.JSON.decode(value);
		if(map.succeed)
		{
		   <logic:notEqual name="privForm" property="current_tab" value="funcpriv"> 	  		
			alert(map.message);
           </logic:notEqual> 			
		}
		
	}

    function immediatelysave(selstr,checked,parentid,moduleMenuId)
    {
          var map = new HashMap();
    	  map.put("user_flag", "<bean:write name="privForm" property="user_flag"/>");
    	  map.put("role_id","<bean:write name="privForm" property="role_id"/>");
    	  map.put("tab_name","<bean:write name="privForm" property="current_tab"/>");
    	  map.put("selstr", selstr);
    	  map.put("parentid",parentid);
    	  //保存权限时，根据模块id和当前构想节点id查找节点，防止不同模块下相同功能id导致授权多了 guodd 2019-07-08
    	  if(moduleMenuId)
    	  	map.put("moduleMenuId",moduleMenuId);
    	  map.put("imme", "1");
    	  map.put("checked", checked);
   　       Rpc({functionId:'1010010048',success:saveok},map);        
    }
    
    function save(selstr)
    {
          var map = new HashMap();
    	  map.put("user_flag", "<bean:write name="privForm" property="user_flag"/>");
    	  map.put("role_id","<bean:write name="privForm" property="role_id"/>");
    	  map.put("tab_name","<bean:write name="privForm" property="current_tab"/>");
    	  map.put("selstr", selstr);
    	  map.put("imme", "0");
   　      Rpc({functionId:'1010010048',success:saveok},map);        
    }    
	Ext.BLANK_IMAGE_URL="/images/s.gif";
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
		//alert(tmp);  	
    	var flag="0";
    	if(checked)
    	  flag="1";
    	// 添加父节点id，用于解决不同模块下使用相同功能id造成父节全部选中的问题，只保存parentid匹配的那条线
    	var parentid = node.parentNode.id;
    	//针对260112??功能号要特殊处理。他的parentid也有相同的。需要往上追两级判断 guodd 2016-11-28
    	if(node.id.indexOf('260112')==0 && node.id.length>6)
    		parentid = node.parentNode.parentNode.id;
    	immediatelysave(tmp,flag,parentid,node.attributes.moduleMenuId);
    }
    
     
    
	Ext.onReady(function()
	{
   			    var chkmode="cascade";//childCascade
   			    var condvisible=true;
   			    var bfun=false;
   			    var openc=true;
   			    var savec=false;
   			    var peggingc=true;
   			    var isCheckChange = false; // 监控是树选择框是否有变动
                <logic:equal name="privForm" property="current_tab" value="managepriv">
                      chkmode="single";  
                      condvisible=false; 
                </logic:equal> 
                <logic:equal name="privForm" property="current_tab" value="partymanagepriv">
                      chkmode=""; 
                </logic:equal>
                <logic:equal name="privForm" property="current_tab" value="membermanagepriv">
                      chkmode="";  
                </logic:equal>  
                <logic:equal name="privForm" property="current_tab" value="funcpriv">
                      bfun=true;  
                      savec=true;   
                      peggingc=false;
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
					
					//没有做任何修改，并且是人员范围(或者党团范围、公会范围)树的时候，不走后台保存了，直接提示保存成功了
					if(!isCheckChange && ('<bean:write name="privForm" property="current_tab"/>'=='managepriv'
									  || '<bean:write name="privForm" property="current_tab"/>'=='partymanagepriv'
						              || '<bean:write name="privForm" property="current_tab"/>'=='membermanagepriv')
					  )
						alert("操作执行成功!");
					else
						save(tmp);
    			 }
    			 function onPeggingClick(item){
					var selectedNode = hrtree.getSelectionModel().getSelectedNode();
					if(selectedNode==null){
						alert("请选择要查看的功能节点!");
						return;
					}
					pegging(selectedNode.text,selectedNode.id);
					//alert(selectedNode.id+" "+selectedNode.text);
    			 }
    			 function onCondClick(item){
					openpriv('<bean:write name="privForm" property="user_flag"/>','<bean:write name="privForm" property="role_id"/>');
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
    			  function chksigle(node,checked)
			    {
					if(checked){
						hrtree.loader.dataUrl='/system/functreeservlet?role_id='+$URL.encode('<bean:write name="privForm" property="role_id" />')+'&flag=<bean:write name="privForm" property="user_flag"/>&type=<bean:write name="privForm" property="current_tab"/>&havechecked=1';
					}
					isCheckChange = true;
			    }
    			  function chkparty(node,checked){
    			  	isCheckChange = true;
    			  } 
    			  function chkmember(node,checked){
    			  	isCheckChange = true;
    			  }   			     			 
    			 var type='<bean:write name="privForm" property="current_tab"/>';
    			 var myloader=new Ext.tree.TreeLoader({dataUrl:'/system/functreeservlet?role_id='+$URL.encode('<bean:write name="privForm" property="role_id" />')+'&flag=<bean:write name="privForm" property="user_flag"/>&type=<bean:write name="privForm" property="current_tab"/>&havechecked=0',
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
    			 //展开节点之前动态添加nodeName参数，因为权限号有重复的情况，通过name进行二次匹配 guodd 2018-12-12
    			 myloader.on('beforeload',function(loader,node){
    				loader.baseParams.nodeName = encodeURI(node.text);
    				//加载下级节点时带上所属模块id guodd 2019-07-08
    				if(node.attributes.moduleMenuId)
    					loader.baseParams.moduleMenuId = node.attributes.moduleMenuId;
    				else
    					delete loader.baseParams.moduleMenuId;
    			 });

                 
			     var hrtree=new Ext.tree.TreePanel({
			     el:'treemenu',
        		 id:"func",
        		 height:"100%",
                 //title: "<span style=\"font-size: 12px;font-weight: bold;color:black;\"><bean:write name="privForm" property="tabtitle"/></span>", //bug 48797 wangb 20180614
       			 checkModel: chkmode, 
                 loader:myloader,
                 animate:true,  
                <logic:equal name="privForm" property="current_tab" value="funcpriv">                  
                 listeners: {checkchange: chkchg}, 
                </logic:equal>   
                <logic:equal name="privForm" property="current_tab" value="managepriv">                  
                 listeners: {checkchange: chksigle}, 
                </logic:equal>
                <logic:equal name="privForm" property="current_tab" value="partymanagepriv">                  
                 listeners: {checkchange: chkparty}, 
                </logic:equal>  
                <logic:equal name="privForm" property="current_tab" value="membermanagepriv">                  
                 listeners: {checkchange: chkmember}, 
                </logic:equal>              
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
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="button.save"/><span></font>',
		            handler:onItemClick,
		            hidden:savec,		            
		            cls:'mybutton',
		            scope: this},
					{
		            id:'cond',
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="button.sys.cond"/><span></font>',
		            handler:onCondClick,
		            hidden:condvisible,
		            cls:'mybutton',		            
		            scope: this},
		            {
		            id:'pegging',
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="report.reportlist.reverse"/><span></font>',
		            handler:onPeggingClick,
		            hidden:peggingc,
		            cls:'mybutton',		            
		            scope: this}
		            
		         ],     
				 rootVisible:false,
                 autoScroll:true
				});		
				
			    var myroot=new Ext.tree.AsyncTreeNode({
			                id:"root",
       					    text:"根节点"
                });				
				hrtree.setRootNode(myroot);
           	    hrtree.render();
                <logic:equal name="privForm" property="current_tab" value="managepriv"> 
                   myroot.expand();//异步加载，点击时才加载,取消了递归展开                
                </logic:equal>   
			    <logic:notEqual name="privForm" property="current_tab" value="managepriv"> 	  
                   myroot.expand(false,false);//不加载时,无法选中递归展开	
                </logic:notEqual> 
								
	});
</script>
<html:form action="/system/security/assign_func" >
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="2" style="margin:7px 0 0 0;">
         <tr>
           <td align="left"> 
            <div id="treemenu">
            </div>             
           </td>
           </tr>           
    </table>  
</html:form>
