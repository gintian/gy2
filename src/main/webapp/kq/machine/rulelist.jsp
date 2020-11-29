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
<TITLE>
</TITLE>
 <script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<script language="javascript" src="/js/constant.js"></script>
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
      	function delete_base()
    	{
    	 
    	  
    	   var currnode,codeitemid,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	    
    	   codeitemid=currnode.uid;
    	   if(codeitemid=="root")
    	   {
    	   	alert('不能删除根目录!');
    	   }
    	   else
    	   {
    	    if(confirm(CONFIRMATION_DEL))
	      {
    	     var theArr=new Array(currnode);
    	     var hashvo=new ParameterSet();
			 hashvo.setValue("rule_id",codeitemid);
			 var request=new Request({asynchronous:false,onSuccess:delete_base_ok,functionId:'15211002104'},hashvo)
    	     //target_url="/kq/machine/kq_rule.do?b_delete=link&rule_id="+codeitemid;
    	  
    	     //parent.location.href=target_url;
    	     }
    	     else
    	     {
    	       return false;
    	     }
    	    }
    	}
function delete_base_ok(outparamters)
{
	var currnode = Global.selectedItem; 
	var preitem=currnode.getPreviousSibling();
	currnode.remove();
	function delay()   
	{   
		preitem.select();
	} 
	window.setTimeout(delay,500);
}
    
//修改 
function update_base(){
	var currnode,codeitemid,target_url;
	currnode=Global.selectedItem;
	if(currnode==null)
	   	return;
	   
	codeitemid=currnode.uid;
    if(codeitemid=="root")
	{
	  	alert('不能修改根目录!');
	  	return ;
	}
	target_url="/kq/machine/kq_rule_data.do?op=edit&b_add=link&rule_id="+codeitemid;
	var return_vo=window.showModalDialog(target_url,null,"dialogWidth:459px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no"); 
	if(return_vo==null)
		return ;
	var codesetvo=new Object();
	codesetvo.rule_name = return_vo.name;
	codesetvo.tran_flag = return_vo.tran_flag;
    var hashvo = new ParameterSet();
	hashvo.setValue("codesetvo",codesetvo);
	hashvo.setValue("opt","edit");
	hashvo.setValue("id",codeitemid);
	var request=new Request({asynchronous:false,onSuccess:edit_item_ok,functionId:'15211002103'},hashvo); 
}

function edit_item_ok(outparamters){
	var currnode = Global.selectedItem; 
	var rule_id  = outparamters.getValue("rule_id");
	var rule_name  = outparamters.getValue("rule_name");
	currnode.openURL();
	currnode.setText(rule_name);
	self.parent.mil_body.location="/kq/machine/kq_rule_data.do?b_query=link&rule_id="+rule_id;
}

// 添加
function add_item()
{
	var currnode,codeitemid,target_url;
	currnode = Global.selectedItem;
	if(currnode==null)
		return;
	codeitemid=currnode.uid;
	target_url="/kq/machine/kq_rule_data.do?op=add&b_add=link";
	var return_vo=window.showModalDialog(target_url,null,"dialogWidth:459px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no"); 
	if(return_vo==null)
		return ;
	var codesetvo=new Object();
	codesetvo.rule_name = return_vo.name;
	codesetvo.tran_flag = return_vo.tran_flag;
    var hashvo = new ParameterSet();
	hashvo.setValue("codesetvo",codesetvo);
	hashvo.setValue("opt","add");
	hashvo.setValue("id","");
	var request=new Request({asynchronous:false,onSuccess:add_item_ok,functionId:'15211002103'},hashvo); 
} 
function add_item_ok(outparamters)
{
	var currnode = Global.selectedItem; 
	var rule_id  = outparamters.getValue("rule_id");
	var rule_name = outparamters.getValue("rule_name");
	add(rule_id,rule_name,"/kq/machine/kq_rule_data.do?b_query=link&rule_id="+rule_id,"mil_body",rule_name,
		"/images/icon_wsx.gif","");
}
function add(uid,text,action,target,title,imgurl,xml)
{
	var currnode=Global.selectedItem;
	if(currnode.uid!="root")
		currnode = currnode.root();
	var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
	currnode.add(tmp);
	tmp.openURL();
}

function go_add()
{
  
   var target_url;
   var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
   target_url="/kq/machine/kq_rule_data.do?b_add=link";
   newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=356,height=274'); 
} 
   </SCRIPT>   
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" style="padding:0px;margin:0px;">
<table width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
  <tr align="left" class="toolbar" style="padding:0px;padding-left: 5px">  
    <td valign="middle" align="left">
         <hrms:priv func_id="270610"> 
        	 <input type="image" name="b_add" src="/images/add.gif" alt="<bean:message key="button.setfield.addfield"/>" onclick="add_item()">
         </hrms:priv>
         <hrms:priv func_id="270611">
        	 <input type="image" name="b_update" src="/images/edit.gif" alt="<bean:message key="label.edit"/>" onclick="update_base()">
    	 </hrms:priv>
       	 <hrms:priv func_id="270612">
         	<input type="image" name="b_delete" src="/images/del.gif" alt="<bean:message key="button.setfield.delfield"/>" onclick="delete_base()">
    	 </hrms:priv>
    	</td>
    </tr>
    <tr>
    	<td>
	    <div id="treemenu"></div>
    </td>
  </tr>
</table>	
          
</body>
<SCRIPT LANGUAGE=javascript>
var m_sXMLFile	= "/kq/machine/kq_rule_tree.jsp";	 
var newwindow;
var root=new xtreeItem("root",FILE_RULE,"/kq/machine/kq_rule_data.jsp?b_query=link&rule_id","mil_body",FILE_RULE,"/images/icon_wsx.gif",m_sXMLFile);
root.setup(document.getElementById("treemenu"));
if(newwindow!=null)
{
newwindow.focus();
}
if(parent.parent.myNewBody!=null)
 {
	parent.parent.myNewBody.cols="*,0"
 }

</SCRIPT>
<script language="javascript">
  initDocument();
</script>
</HTML>