<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	} 
%>

<HTML>
<HEAD>
 <script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes/>
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>

<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
  <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
   <SCRIPT LANGUAGE=javascript>
    	var newwindow=null;
    	function add_base()
    	{
    	  var target_url;
          var winFeatures = "dialogHeight:400px; dialogLeft:350px;"; 
          target_url="/kq/options/class/kq_class_tree.do?b_edit=link&class_flag=add";
          var return_vo=window.showModalDialog(target_url,null,"dialogWidth:356px; dialogHeight:130px;resizable:no;center:yes;scroll:no;status:no"); 
          //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=300,left=320,width=306,height=204'); 
          if(return_vo==null)
			return ;
		  if(return_vo.class_flag=="add")
		  {
		  	var class_id  = return_vo.class_id;
 			var class_name = return_vo.name+'-'+class_id;
 			var currnode=Global.selectedItem;
 			var root = currnode.root();
 			var tmp = new xtreeItem(class_id+"_2_1",class_name,"/kq/options/class/kq_class_data.do?b_query=link&class_id="+class_id,"mil_body",class_name,
	 				"/images/table.gif","/kq/options/class/class_list.jsp?params=1<2 and class_id<'0'&class_id="+class_id);
			root.add(tmp);
			currnode.openURL();
		  }
		}
    	//修改
    	
    	function modify_base()
    	{
    	   var currnode,codeitemid,target_url,test;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   if(currnode.uid=="root")
    	   {
        	   alert("请选择班次！");
        	   return;
    	   }
    	   codeitemid=currnode.uid.substring(0,currnode.uid.length-4);
    	   var classType = currnode.uid.substring(currnode.uid.length-3,currnode.uid.length-2);
    	   var changePublicRight = currnode.uid.substring(currnode.uid.length-1);
    	   if(classType=="1"||(changePublicRight=="0"&&classType=="0")){
    	   		alert('上级机构班次不能修改');
    	   }else if((changePublicRight=="0"&&classType=="0")){
    	   		alert('公共班次不能修改!');
    	   		return;
    	   }else{
				var theArr=new Array(currnode);
    	  	 	target_url="/kq/options/class/kq_class_tree.do?b_edit=link&class_flag=up&class_id="+codeitemid+"&classType=2";
    	  		var return_vo=window.showModalDialog(target_url,null,"dialogWidth:356px; dialogHeight:130px;resizable:no;center:yes;scroll:no;status:no"); 
    	   	//newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
    	   	if(return_vo==null)
				return ;
			if(return_vo.class_flag=="up"){
				var name = return_vo.name;
				currnode.openURL();
				currnode.setText(name+'-'+codeitemid);
				currnode.setTitle(name);
				}
	   		}
    	}
    	//删除
    	function delete_base()
    	{
    	   var currnode,codeitemid,target_url;
    	   currnode=Global.selectedItem;     	   
    	   if(currnode==null)
    	    	return;    	    
    	   if(currnode.uid=="root")
    	   {
    	   	alert("请选择班次!");
    	   	return;
    	   }
    	   codeitemid=currnode.uid.substring(0,currnode.uid.length-4);
    	   var classType = currnode.uid.substring(currnode.uid.length-3,currnode.uid.length-2);
    	   var changePublicRight = currnode.uid.substring(currnode.uid.length-1);  
    	   if(classType=="1"){
    	   	alert('上级机构班次不能删除');
    	   	return;
    	   }else if((changePublicRight=="0"&&classType=="0")){
    	   		alert('公共班次不能删除!');
    	   		return;
    	   }else{
				if(confirm('是否删除选择的记录？'))
	      		{
      		var theArr=new Array(currnode);
			//target_url="/kq/options/class/kq_class_tree.do?b_delete=link&class_flag=del&class_id="+codeitemid;
			var hashvo=new ParameterSet();
			hashvo.setValue("class_id",codeitemid);
			hashvo.setValue("class_flag","del");
			var request=new Request({asynchronous:false,onSuccess:delete_item_ok,functionId:'15211000005'},hashvo)
			//parent.location.href=target_url;
    	  		}
    	  	 }
    	 }
function delete_item_ok(outparamters)
{
	var err_message = outparamters.getValue("err_message");
	if(err_message != null){
		alert(err_message);
		return false;
	}else{
		var currnode = Global.selectedItem; 
		var preitem=currnode.getPreviousSibling();
		currnode.remove();
		function delay()   
		{   
			preitem.select();
		} 
		window.setTimeout(delay,500);
	}
}
	function adjust_order() {
    	var winFeatures = "dialogHeight:400px; dialogLeft:300px;";
    	var target_url="/kq/options/class/kq_class_data.do?br_order=link";
    	      //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=300,width=596,height=354'); 
		window.showModalDialog(target_url,1, 
        winFeatures + "resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        window.parent.frames[0].location.reload();
	}
   </SCRIPT>  
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<hrms:priv func_id="27038">	
<table id="classTab" width="112%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
  <tr align="left" class="toolbar" style="padding-left:2px;">  
    <td valign="middle">
       &nbsp;
       <input type="image" name="b_add" src="/images/add.gif" alt="新增班次" onclick="add_base();">&nbsp;
       
       <input type="image" name="b_delete" src="/images/del.gif" alt="删除班次" onclick="delete_base();">&nbsp;
       
       <input type="image" name="b_edit" src="/images/edit.gif" alt="修改班次" onclick="modify_base();">&nbsp;
       
       <input type="image" name="b_order" src="/images/sort.gif" alt="调整显示顺序" onclick="adjust_order();">
    </td>
  </tr>
  <tr>  
    <td valign="top">
	<div id="treemenu"></div>
    </td>
  </tr>
</table>
</hrms:priv>
<BODY>
</HTML>

<script language="javascript" src="/kq/kq.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
  var m_sXMLFile= "/kq/options/class/class_list.jsp?params=1%3D1";	 
  var root=new xtreeItem("root","基本班次","/kq/options/class/kq_class_data.do?b_query=link&class_id=","mil_body","基本班次","/images/book.gif",m_sXMLFile);
  root.setup(document.getElementById("treemenu"));
  
  tabWidthForEdge("classTab");
</SCRIPT>
<script language="javascript">
  initDocument();
</script>