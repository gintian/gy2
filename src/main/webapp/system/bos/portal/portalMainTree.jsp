<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	String css_url="/css/css1.css";	
%>

<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<hrms:themes></hrms:themes>
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script> 
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/codetree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
		<script type="text/javascript">
	var portalflag = false;
	var height='400';
function openAdd()
  {
  	     var currnode,base_id;
    	   currnode=Global.selectedItem;
    	   
    	   if(currnode==null)
    	    	return;
    	    var base_ids;	
    	   base_ids=currnode.uid;
    	   var opt;
    	   if(base_ids.indexOf("'")!=-1){
    	   base_id=base_ids.substr(0,base_ids.indexOf("'"));
    	   opt =base_ids.substr(base_ids.indexOf("'")+1,base_ids.length);
    //	   alert(base_id+"opt:"+opt);
    	   }else{
    	   base_id =base_ids;
    	   opt=0;
    	   }
    	   var theArr=new Array(currnode);
    	    var theurl;
    		
    	    if(opt==3){
    	     return;
    	   }else if(opt==2){
    	     theurl="/system/bos/portal/portalMain.do?b_newPanel=new`portalid="+base_id+"`opt=2";
    	      opt=3;
    	     height='330'; 
    	   }else if(opt==1){
    	    currnode.clearChildren();
	 		currnode.loadChildren();
    		 if(currnode.childNodes.length>2){
    		 alert("门户面板列数最大支持三列，不允许再添加！");
    		 return;
    		 }
    	     theurl="/system/bos/portal/portalMain.do?b_newColumn=new`portalid="+base_id+"`opt=1";
    	      opt=2;
    	      height='200';
    	   }else{
    	   theurl="/system/bos/portal/portalMain.do?b_newPortal=new`portalid="+base_id+"`opt=1";
    	   opt=1;
    	   height='180';
    	   }
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var dw=400,dl=(screen.width-dw)/2;dt=(screen.height-height)/2;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		// var portal_base_bean = new Object();
    	   if(retvo!=null){
	    	   var portal_name = retvo.portal_name;
	    	   var portal_id = retvo.portal_id;
	    	   var parentid=retvo.parentid;
	    	  // var hashvo = new ParameterSet();
    	   	  // hashvo.setValue("portal_base_bean",portal_base_bean);
    	   	   	 parent.frames["mil_body"].location.reload();
    	   	   //alert("name:"+law_base_bean.name+" des "+law_base_bean.description+" che "+law_base_bean.check+" id "+law_base_bean.up_base_id+" sta "+law_base_bean.status+" base"+law_base_bean.basetype);
    	   	  // var request=new Request({asynchronous:false,onSuccess:add_user_ok,portalId:'1010800104'},hashvo); 
    	      //var type ="2&portal_name="+portal_name+"&portal_id="+portal_id+"&parentid="+parentid;
    	      //dealwith(type);
    	      add_user_ok(portal_id,portal_name,opt);
    	      
    	   }
  }

  function add_user_ok(portal_id,portal_name,opt){
  var currnode=Global.selectedItem;
	     var groupid=portal_id;
	     var groupname=portal_name;
//	     if(currnode.load)
//	     {
//	     }
//	     else
//	     	currnode.expand();
	 	if(currnode.load)
	     {
			var imgurl;
				imgurl="/images/close.png";
				groupid = groupid+"'"+opt;
			var tmp = new xtreeItem(groupid,groupname,"/system/bos/portal/portalMain.do?b_search=query&opt="+opt+"&parentid="+groupid,"mil_body",groupname,imgurl,"/system/bos/portal/portal_main_tree.jsp?opt="+opt+"&codeid=0&portal_id="+groupid);
		 	currnode.add(tmp);
			currnode.clearChildren();
	 		currnode.loadChildren();
			currnode.expand();
		 
	     }
	     else
	     	currnode.expand();
  }
  function rename(){
   var currnode,base_id;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	    
    	   base_id=currnode.uid;
    	//   alert(currnode.id);
    	
    	 
    	   if(base_id=="root")
    	   {
    	   	alert('不能编辑根目录!');
    	   }
    	   else
    	   {
    	     var base_ids;	
    	   base_ids=currnode.uid;
    	   var opt=1;
    	   if(base_ids.indexOf("'")!=-1){
    	   base_id=base_ids.substr(0,base_ids.indexOf("'"));
    	   opt =base_ids.substr(base_ids.indexOf("'")+1,base_ids.length);
    	   }else{
    	   base_id =base_ids;
    	   opt=0;
    	   }
    	   
    	   var theurl;
    	   //xus 18/8/10 如果是工作桌面、服务大厅 则不走其他的
    	 if(base_id=='024'||base_id=='025'){
    		 theurl="/system/bos/portal/portalMain.do?b_editSinglePanel=new`portalid="+base_id+"`opt=2";
    		 height='210';
    	 }else{
    	    if(opt==3){
    	    theurl="/system/bos/portal/portalMain.do?b_editPanel=new`portalid="+base_id+"`opt=3";
    	      height='330';
    	   }else if(opt==2){
    	    theurl="/system/bos/portal/portalMain.do?b_editColumn=new`portalid="+base_id+"`opt=2";
    	     height='210';
    	   }else if(opt==1){
    	    theurl="/system/bos/portal/portalMain.do?b_editPortal=new`portalid="+base_id+"`opt=1";
    	     height='180';
    	   }else{
    	    theurl="/system/bos/portal/portalMain.do?b_editPortal=new`portalid="+base_id+"`opt=1";
    	      height='250';
    	   }
    	 }
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var dw=400,dl=(screen.width-dw)/2;dt=(screen.height-height)/2;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:yes;status:no");
  		
    	   
    	   if(retvo!=null){
	    	 
    	      edit_user_ok();
    	   }
    	   
    	  }
  }
  function edit_user_ok(){
  var currnode=Global.selectedItem;
	   
	    //currnode.setText(getDecodeStr(groupname));
	    var p = currnode.parent;
	   
	    //p = Global.selectedItem;
	    if(!p){ return };
		
			p.clearChildren();
			p.loadChildren();
			p.expand();
	    
  }
  function valide(){
    var type ="5";
    	     dealwith(type,5);
    	alert("生效完成，请您重新启动服务器中间件！");   
  
  }
  
function delete_base1()
    	{
    	   var currnode,base_id,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   if(base_id=="root")
    	   {
    	   	alert('不能删除根目录!');
    	   }
    	   else
    	   {
    	
    	    if(confirm('确认删除吗?'))
	    {
	  
    	   //  var theArr=new Array(currnode);
    	     //currname=currnode.text;
    	    // var hashvo=new ParameterSet();
     		 //hashvo.setValue("currname",currname);
     		 var pars="a_base_id="+base_id; 	        
     		// hashvo.setValue("a_base_id",base_id);
     		 //hashvo.setValue("basetype",basetype);
     		 
     		 parent.frames["mil_body"].location.reload();
     		 //ajax传不了request对象
     		 //  var request=new Request({method:'post',asynchronous:false,parameters:pars,
              //                onSuccess:delete_ok,portalId:'1010800108'});
			//var request=new Request({asynchronous:false,onSuccess:delete_ok,portalId:'1010800108'},hashvo);
			 //target_url="/selfservice/lawbase/add_law_base.do?b_delete=link&a_base_id="+base_id;
    	     //parent.location.href=target_url;
    	     var type ="4&portal_id="+base_id;
    	     dealwith(type);
    	     delete_ok();
    	     }
    	     else
    	    {
    	     return false;
    	    }
    	  }
    	}
    	
		  function delete_ok()
		  {
		     var currnode=Global.selectedItem;
		     var preitem=currnode.getPreviousSibling();
		     currnode.remove();
		     //preitem.select();
		     function delay()   
			 {   
				preitem.select();
			 } 
			 window.setTimeout(delay,500);
		  }
    	
    
		function copy(){
		window.location.href="/servlet/DownLoadPortal";
		}
		function fileInput() {
    	currnode=Global.selectedItem;
    		if(currnode==null)
    	   		return;
    		base_id=currnode.uid;
    	parent.mil_body.location = "/system/bos/portal/portal_file_input.jsp?b_query=link&base_id="+ base_id;
	}
	
 function dealwith(type,typeid){
  var xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");  
	var url = "/system/bos/portal/portal_dw_tree.jsp?type="+type;
xmlhttp.open("POST",url, false);
	xmlhttp.onreadystatechange=function(){
	   if(xmlhttp!=null)
		if(xmlhttp.readyState==4)
		{
			//alert(xmlhttp.responseXML.xml);
			if(xmlhttp.status==200)
			{
			
			}
		}
	}
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");		 
    xmlhttp.send(null);
    }
  
	
		</script>
</HEAD>
<style> 
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>


<!-- 【5857】工具箱-平台接口-菜单定制（在改变菜单结构区域宽度时，滚动条不能够随着改变）
	需要给body添加事件expression才触发，不知道为什么。
	jingq add 2014.12.12
 -->
<body onclick="">
<table width="100%" height="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
		<tr align="left"   class="toolbar"  style="padding-left:2px;width:100%;overflow: auto;">
				<td valign="middle" align="left">
					<INPUT type="hidden" id="flg">
					&nbsp;<input type="image" name="b_new" src="/images/add.gif" alt="新增功能" onclick="openAdd();">
					<input type="image" name="b_delete" src="/images/del.gif" alt="删除功能" onclick="delete_base1();">
					<input type="image" name="b_update" src="/images/edit.gif" alt="修改功能" onclick="rename();">
					<input type="image" name="b_indexf" src="/images/index.gif" alt="备份" onclick="copy()">
					<input type="image" name="b_indexf" src="/images/index2.gif" alt="还原" onclick="fileInput()">
					<input type="image" name="b_indexf" src="/images/compute.gif" alt="生效" onclick="valide()">
				</td>
			</tr>
	<tr>  
		<td valign="top" >
			<div id="treeportal" style="height:100%;width:100%;overflow-x: auto;overflow-y:auto;"></div>
		</td>
	 
	</tr>
</table>	







	<script language='javascript' >
	
	var m_sXMLFile	= "/system/bos/portal/portal_main_tree.jsp?opt=0&codeid=0&portal_id=-1";
	var href ="/system/bos/portal/portalMain.do?b_search=query&parentid=-1&opt=0";		
	var newwindow;
	var root=new xtreeItem("root",PORTALREGISTER,href,"mil_body",PORTALREGISTER,"/images/add_all.gif",m_sXMLFile);
	//Global.defaultInput=0;
	root.setup(document.getElementById("treeportal"));	
	//root.expand();
//	if(newwindow!=null)
//	{
//		newwindow.focus();
//	}
//	if(parent.parent.myNewBody!=null)
//	{
//		parent.parent.myNewBody.cols="*,0";
//	}
	//autoSelectNode();
	
	
	
	
	
	
	
	</script>
	
<script language="javascript">
  initDocument();

  parent.mil_body.location=href;
</script>

</body>
</HTML>
	
	

