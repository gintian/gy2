<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page
	import="java.util.*,org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.valueobject.UserView,com.hrms.hjsj.utils.Sql_switcher,com.hjsj.hrms.actionform.report.edit_report.StaticStatementForm"%>
<%
	String css_url = "/css/css1.css";
	StaticStatementForm staticStatementForm = (StaticStatementForm) session
			.getAttribute("staticStatementForm");
	String scopeid = staticStatementForm.getScopeid();
%>

<HTML>
	<HEAD>
		<TITLE></TITLE>
		<link href="<%=css_url%>" rel="stylesheet" type="text/css">
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
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
		<script type="text/javascript">
	var functionflag = false;
function openAdd()
  {
  		var currnode,base_id,target_url;
    	   currnode=Global.selectedItem;
    	  // if(currnode==null||currnode.uid=="root")
    	  // {
  	    var theurl="/report/edit_report/editReport/staticStatement.do?b_addStatic=new";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:500px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		// var func_base_bean = new Object();
    	   if(retvo!=null){
    	   	var scopeid = retvo.scopeid;
    	   	var scopename = retvo.scopename;
    	       imgurl="/images/open.png";
			var tmp = new xtreeItem(scopeid,scopename,"/report/edit_report/editReport/staticStatement.do?b_queryStatic=init&scopeid="+scopeid,"mil_body",scopename,imgurl);
		 	root.add(tmp);
		 	var obj=root.childNodes[root.childNodes.length-1];
		 	if(obj){
			 selectedClass("treeItem-text-"+obj.id);
	     	 obj.select();
	     	 }
		 	
    	   }
    	 //  }else{
    	//   alert("当前选中的节点为子节点，子节点下不能新增节点！");
    	 //  }
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
    var currnode,base_id,target_url;
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
    var theurl="/report/edit_report/editReport/staticStatement.do?b_editStatic=new`scopeid="+base_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:500px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		// var func_base_bean = new Object();
    	   if(retvo!=null){
    	   		root.clearChildren();
			root.loadChildren();
			root.expand();
			var scopeid2 = retvo.scopeid;
    	        for(var j=0;j<root.childNodes.length;j++)
				{
				
				var obj=root.childNodes[j];
					if(obj.uid==scopeid2)
					{
						var obj=root.childNodes[j];
						  selectedClass("treeItem-text-"+obj.id);
	     				  obj.select();
						break;
					}
				}
    	   }
  }
  }
  
function delete_base()
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
	  
     		 var pars="a_base_id="+base_id; 	        
     		var hashvo=new ParameterSet();
			hashvo.setValue("scopeid",base_id);
			var In_paramters="flag=1"; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:delete_ok,functionId:'03020000045'},hashvo);
		
    	     }
    	     else
    	    {
    	     return false;
    	    }
    	  }
    	}
    	
function delete_ok()
	  {
			root.clearChildren();
			root.loadChildren();
			root.expand();
			initTreeNode();
	// var obj=root.childNodes[0];
	//   if(obj)
	//  {
	 //   selectedClass("treeItem-text-"+obj.id);
	 //    obj.select();
	 //    }
	}
    	
    
	
  
function payrollSort(){
    	   var currnode,base_id;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   var theurl="/report/edit_report/editReport/staticStatement.do?b_order=new";
    	   var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		   var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
      	   if(retvo!=null){
    	      root.clearChildren();
			root.loadChildren();
			root.expand();
	 var obj=root.childNodes[0];
	   if(obj)
	  {
	    selectedClass("treeItem-text-"+obj.id);
	     obj.select();
	     }
    	   }
}
function initTreeNode()
{
	var scopeid = "";
	scopeid = "<%=scopeid%>";
	  if(scopeid=="0"||scopeid==""||scopeid=="ok"){
	
	   var obj=root.childNodes[0];
	   if(obj)
	  {
	    selectedClass("treeItem-text-"+obj.id);
	     obj.select();
	     }else{
	     parent.mil_body.location="/report/edit_report/editReport/staticStatement.do?b_queryStatic=init&startscopeid=-1";
	     }
	  }else{
	   if(root){
	     for(var j=0;j<root.childNodes.length;j++)
				{
				
				var obj=root.childNodes[j];
					if(obj.uid==scopeid)
					{
						var obj=root.childNodes[j];
						  selectedClass("treeItem-text-"+obj.id);
	     				  obj.select();
						break;
					}
				}
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



	<body>

		<table width="600" align="left" border="0" cellpadding="0"
			cellspacing="0" class="mainbackground">
			<tr align="left">
				<td valign="top" align="left">
					<div class="toolbar"
						style="width: expression(document .         body .         clientWidth); overflow: auto;">
						<INPUT type="hidden" id="flg">
						<input type="image" name="b_new" src="/images/add.gif" alt="新增"
							onclick="openAdd();">
						<input type="image" name="b_update" src="/images/edit.gif"
							alt="修改" onclick="rename();">
						<input type="image" name="b_sort" src="/images/sort.gif"
							alt="调整顺序" onclick="payrollSort();">
						<input type="image" name="b_delete" src="/images/del.gif" alt="删除"
							onclick="delete_base();">
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top">
					<div id="treemenu"
						style="height: expression(document .         body .         clientHeight-25); width: expression(document .         body .         clientWidth); overflow-x: auto; overflow-y: auto;">
					</div>
				</td>

			</tr>
		</table>

		<script language='javascript'>
	var m_sXMLFile	= "report_setStatic_tree.jsp?flag=1&codeid=0";		
	var href ="/report/edit_report/editReport/staticStatement.do?b_queryStatic=init&startscopeid=-1";		
	var newwindow;  
	var root=new xtreeItem("root",STATICSTATEMENT,href,"mil_body",STATICSTATEMENT,"/images/add_all.gif",m_sXMLFile);
	//Global.defaultInput=0;
	root.setup(document.getElementById("treemenu"));	
	root.expand();
</script>

		<script language="javascript">
  initDocument();
  initTreeNode();
  //setDrag(true);
  //parent.mil_body.location=href;
</script>

	</body>
</HTML>



