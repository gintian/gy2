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
<script language="javascript" src="/ajax/menu.js"></script>
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
	var functionflag = false;
function openAdd()
  {
  	     var currnode,base_id;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   var theArr=new Array(currnode);
  	    var theurl="/system/bos/func/functionMain.do?b_newFunc=new`functionid="+base_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var dw=400,dh=180,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		// var func_base_bean = new Object();
    	   if(retvo!=null){
	    	   var function_name = retvo.function_name;
	    	   var function_id = retvo.function_id;
	    	   var parentid=retvo.parentid;
	    	  // var hashvo = new ParameterSet();
    	   	  // hashvo.setValue("func_base_bean",func_base_bean);
    	   	   	 parent.frames["mil_body"].location.reload();
    	   	   //alert("name:"+law_base_bean.name+" des "+law_base_bean.description+" che "+law_base_bean.check+" id "+law_base_bean.up_base_id+" sta "+law_base_bean.status+" base"+law_base_bean.basetype);
    	   	  // var request=new Request({asynchronous:false,onSuccess:add_user_ok,functionId:'1010800104'},hashvo); 
    	      //var type ="2&function_name="+function_name+"&function_id="+function_id+"&parentid="+parentid;
    	      //dealwith(type);
    	      add_user_ok(function_id,function_name);
    	      
    	   }
  }

  function add_user_ok(function_id,function_name){
  var currnode=Global.selectedItem;
	     var groupid=function_id;
	     var groupname=function_name;
	     
	 	 if(currnode.load)
	     {
			var imgurl;
			imgurl="/images/open.png";
			var tmp = new xtreeItem(groupid,groupname,"/system/bos/func/functionMain.do?b_search=query&parentid="+groupid,"mil_body",groupname,imgurl,"/system/bos/func/achivement_main_tree.jsp?opt=1&codeid=0&function_id="+groupid);
		 	currnode.add(tmp);
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
    	   if(base_id=="root")
    	   {
    	   	alert('不能编辑根目录!');
    	   }
    	   else
    	   {
    	 var theurl="/system/bos/func/functionMain.do?b_editFunc=new`functionid="+base_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var dw=400,dh=180,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  		
    	   
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
              //                onSuccess:delete_ok,functionId:'1010800108'});
			//var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'1010800108'},hashvo);
			 //target_url="/selfservice/lawbase/add_law_base.do?b_delete=link&a_base_id="+base_id;
    	     //parent.location.href=target_url;
    	     var type ="4&function_id="+base_id;
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
		window.location.href="/servlet/DownLoadFunction";
		}
		function fileInput() {
    	currnode=Global.selectedItem;
    		if(currnode==null)
    	   		return;
    		base_id=currnode.uid;
    	parent.mil_body.location = "/system/bos/func/file_input.jsp?b_query=link&base_id="+ base_id;
	}
	
 function dealwith(type,typeid){
  var xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");  
	var url = "/system/bos/func/func_dw_tree.jsp?type="+type;
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
  
function dragendFunc(){
	var currnode=Global.selectedItem; 
	
	if(currnode.dragbool){
	if(currnode.dragFrom.uid=='root')
	return;
		 var type ="6&fromfunc_id="+currnode.dragFrom.uid+"&tofunc_id="+currnode.uid;
    	     dealwith(type);
		if(currnode.uid=='root'){
			currnode.dragFrom.remove();
			currnode.clearChildren();
			currnode.loadChildren();
			parent.frames["mil_body"].location="/system/bos/func/functionMain.do?b_search=query&parentid=-1";
		}else{
		
		    currnode.dragFrom.remove();
		    currnode.load=true;
			currnode.clearChildren();
	  		currnode.loadChildren();
	  parent.frames["mil_body"].location="/system/bos/func/functionMain.do?b_search=query&parentid="+currnode.uid;
		}
	}
}
function payrollSort(){
    	   var currnode,base_id;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   var theurl="/system/bos/func/functionMain.do?b_payFunc=new`funcid="+base_id;
    	   //权限树节点 新增 extraparam 属性，用于节点定位 guodd 2018-09-07
    	   if(currnode.extraparam){
    		   theurl+="`ctrl_ver="+currnode.extraparam;
    	   }
    	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	   var dw=400,dh=320,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   		   var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  		
    	   
    	   if(retvo!=null){
	    	 
    	     if(currnode.uid=='root'){
			currnode.clearChildren();
			currnode.loadChildren();
			parent.frames["mil_body"].location="/system/bos/func/functionMain.do?b_search=query&parentid=-1";
		}else{
		    currnode.load=true;
			currnode.clearChildren();
	  		currnode.loadChildren();
	 	    parent.frames["mil_body"].location="/system/bos/func/functionMain.do?b_search=query&parentid="+currnode.uid;
		}
    }


}
		</script>
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
</HEAD>


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
					<input type="image" name="b_sort" src="/images/sort.gif" alt="调整顺序" onclick="payrollSort();">
					<input type="image" name="b_indexf" src="/images/index.gif" alt="备份" onclick="copy()">
					<input type="image" name="b_indexf" src="/images/index2.gif" alt="还原" onclick="fileInput()">
					<input type="image" name="b_indexf" src="/images/compute.gif" alt="生效" onclick="valide()">
				</td>
			</tr>
	<tr>  
		<td valign="top">
			<div id="treemenu" ondragend="dragendFunc();" style="height:100%;width:100%;overflow-x:auto;overflow-y:auto;">
			</div>
		</td>
	 
	</tr>
</table>	

<script language='javascript' >
	var m_sXMLFile	= "/system/bos/func/achivement_main_tree.jsp?opt=0&codeid=0&function_id=0";
	var href ="/system/bos/func/functionMain.do?b_search=query&parentid=-1";		
	var newwindow;  
	var root=new xtreeItem("root",FUNCTIONREGISTER,href,"mil_body",FUNCTIONREGISTER,"/images/add_all.gif",m_sXMLFile);
	//Global.defaultInput=0;
	root.setup(document.getElementById("treemenu"));	
	setDrag(false);
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
  setDrag(true);
  parent.mil_body.location=href;
</script>

</body>
</HTML>
	
	

