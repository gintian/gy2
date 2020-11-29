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

	function add_item()
	{
		var currnode,codeitemid,target_url;
		currnode = Global.selectedItem;
		if(currnode==null)
			return;
		
		codeitemid=currnode.uid;
		if("1"==codeitemid)
		{
			alert("系统项不可添加！ ");
			return;
		}
		target_url="/kq/options/item_tree.do?b_add=link&a_base_id="+codeitemid;
		var return_vo=window.showModalDialog(target_url,null,"dialogWidth:459px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no"); 
		if(return_vo==null)
			return ;
		
		var codesetvo=new Object();
		codesetvo.code = return_vo.code;
		codesetvo.name = return_vo.name;
		codesetvo.flag = return_vo.flag;
	    codesetvo.codeitemid = return_vo.codeitemid;
	    codesetvo.mes = return_vo.mes;
	    var hashvo = new ParameterSet();
		hashvo.setValue("codesetvo",codesetvo);
		var request=new Request({asynchronous:false,onSuccess:add_item_ok,functionId:'15204110003'},hashvo); 
	} 
	   	
	function add_item_ok(outparamters)
	{
		var currnode = Global.selectedItem; 
		var codesetid  = outparamters.getValue("codesetid");
	 	var codesetname = outparamters.getValue("codesetdesc");
	 	var codeitemid = outparamters.getValue("codeitemid");
	 	var sys = outparamters.getValue("sys");
	 	if(codeitemid==null)
	 		codeitemid = "";
	 	if(sys=="0"){
	 		add(codeitemid+codesetid,codesetname,"/kq/options/kq_item_details.do?b_query=link&codeitemid="+codeitemid+codesetid+"&returnFlag="+codeitemid+codesetid,"mil_body",codesetname,
		 				"/images/table.gif","/kq/options/item_list.jsp?params=parentId<>codeitemid and parentid%3D'"+codeitemid+codesetid+"'");
			currnode.openURL();
		}else if(sys="2"){
			alert('<bean:message key="error.kq.exist"/>');
		}
	}
	
	function modify_item()
	{
		var currnode,codeitemid,target_url;
		currnode=Global.selectedItem;
		if(currnode==null)
			return;
		
		codeitemid=currnode.uid;   
		if(codeitemid=="root" || 0==currnode.level)
		{
			alert('不能修改根目录!');
			return;
		}

		target_url="/kq/options/item_tree.do?b_edit=link&a_base_id="+codeitemid;
		var return_vo=window.showModalDialog(target_url,null,"dialogWidth:459px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no"); 
		if(return_vo==null)
		    return ;
	    
		var codesetvo=new Object();
		codesetvo.code = return_vo.code;
		codesetvo.name = return_vo.name;
		codesetvo.flag = return_vo.flag;
	    codesetvo.codeitemid = return_vo.codeitemid;
	    codesetvo.mes = return_vo.mes;
	    var hashvo = new ParameterSet();
		hashvo.setValue("codesetvo",codesetvo);
		var request=new Request({asynchronous:false,onSuccess:modify_item_ok,functionId:'15204110003'},hashvo); 
	}
	
	function modify_item_ok(outparamters)
	{
		var currnode = Global.selectedItem; 
		var codesetid  = outparamters.getValue("codesetid");
	 	var codesetname = outparamters.getValue("codesetdesc");
		currnode.openURL();
		currnode.setText(codesetname);
	}
	
	function delete_item()
	{
		var currnode=Global.selectedItem;
		if(currnode==null)
			return;
		
		if(!confirm(CONFIRMATION_DEL))
			return false;
		
		var hashvo=new ParameterSet();
		hashvo.setValue("codeitemid",currnode.uid);
		var request=new Request({asynchronous:false,onSuccess:delete_item_ok,functionId:'15204110004'},hashvo)
	}
	
	function delete_item_ok(outparamters)
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
	
	function add(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
	}
	
	function check_formula(){
		var target_url="/kq/options/kq_check_formula.do?b_query=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
		var winFeature="dialogWidth=840px;dialogHeight=510px;resizable=yes;scroll=no;status=no;";
		if (isIE6())
			winFeature="dialogWidth=840px;dialogHeight=550px;resizable=yes;scroll=no;status=no;";
		var return_vo= window.showModalDialog(iframe_url,"kq", winFeature);
	}
</SCRIPT>  
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<hrms:priv func_id="27033">	
<table id="itemTab" width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
 <tr align="left" class="toolbar" style="padding-left:2px;">  
    <td valign="middle">
       &nbsp;<input type="image" name="b_add" src="/images/add.gif" alt="新增考勤类别" onclick="add_item()">&nbsp;
       <input type="image" name="b_delete" src="/images/del.gif" alt="删除考勤类别" onclick="delete_item()">&nbsp;
       <input type="image" name="b_edit" src="/images/edit.gif" alt="修改考勤类别" onclick="modify_item()">&nbsp;
       <%
   		String flag = request.getParameter("flag");
       	if(flag == null)
       	{
       %>
       <input type="image" name="b_check" src="/images/check.gif" alt="审核公式定义" onclick="check_formula()">
       <%
       	}
       %>
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
  var m_sXMLFile= "/kq/options/item_list.jsp?params=codeitemid%3Dparentid";	 
  var root=new xtreeItem("27","考勤项目","/kq/options/kq_item_details.do?b_query=link&codeitemid=","mil_body","考勤项目","/images/table.gif",m_sXMLFile);
  root.setup(document.getElementById("treemenu"));
  
  tabWidthForEdge("itemTab");
</SCRIPT>
<script language="javascript">
  initDocument();
</script>