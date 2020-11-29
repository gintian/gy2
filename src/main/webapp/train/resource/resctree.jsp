<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);

	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	String filepath = request.getSession().getServletContext().getRealPath("/");
	if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
	{
		filepath=session.getServletContext().getResource("/").getPath();//.substring(0);
	   if(filepath.indexOf(':')!=-1)
		  {
		   filepath=filepath.substring(1);   
		  }
		  else
		  {
			  filepath=filepath.substring(0);      
		  }
	   int nlen=filepath.length();
		  StringBuffer buf=new StringBuffer();
		  buf.append(filepath);
		  buf.setLength(nlen-1);
		  filepath=buf.toString();
	}
	filepath = filepath.replace("\\", "``");
%>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>


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
<HTML>
<HEAD>
<TITLE></TITLE>
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
<%String setid = SafeCode.encode(PubFunc.encrypt("54")); %>
function addCode(){
 	var currnode=Global.selectedItem;
   	if(currnode==null)
    	return;
    var codeitemid=currnode.uid;
    codeitemid=codeitemid=="root"?"":codeitemid;
	var target_url="/train/resource/trainRescList.do?b_addcode=link&flag=add&setid=<%=setid %>&itemid="+codeitemid;
    var return_vo= window.showModalDialog(target_url, false, 
        		"dialogWidth:520px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var beanvalue = return_vo;
    	var itemid = beanvalue.id;
	 	var codeitemdesc = getDecodeStr(beanvalue.desc);
	 	if(currnode.load){
			var imgurl;
			imgurl="/images/book.gif";
			var xmlstr	= "/train/resource/get_code_tree.jsp?codesetid=54&codeitemid="+itemid;	 
			var tmp=new xtreeItem(itemid,codeitemdesc,
				"/train/resource/trainRescList.do?b_query=link&type=5&a_code="+itemid,
				"mil_body",codeitemdesc,imgurl,xmlstr);
		 	currnode.add(tmp);
		 	tmp.expand();
	     }else
	     	currnode.expand();
    }
}
function updateCode(){
 	var currnode=Global.selectedItem;
   	if(currnode==null)
    	return;
     var codeitemid=currnode.uid;
     if(codeitemid=="root"){
    	alert("不能修改根节点!");
    	return false;
   	}	
     
     if(checkIsParent(codeitemid)){
     	alert(NOT_UPDATE_OUT_POWER);
    		return false;
     }
	var target_url="/train/resource/trainRescList.do?b_addcode=link&flag=update&setid=<%=setid %>&itemid="+codeitemid;
	target_url+="&codedesc="+$URL.encode(getEncodeStr(currnode.text));
    var return_vo= window.showModalDialog(target_url, false, 
        		"dialogWidth:520px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var beanvalue = return_vo;
	 	var codeitemdesc = getDecodeStr(beanvalue.desc);
	 	var currnode =currnode.parent;
	 	if(currnode.getFirstChild().uid==currnode.uid){
			currnode.loadChildren();
			currnode.expand();
		}else{
			currnode.clearChildren();
			currnode.loadChildren();
			currnode.expand();
		}
    }
}
function deleteCode(){
 	var currnode=Global.selectedItem;
   	if(currnode==null)
    	return;
     var codeitemid=currnode.uid;
     if(codeitemid=="root"){
    	alert("不能删除根节点!");
    	return false;
   	}	
     
    if(checkIsParent(codeitemid)){
    	alert(NOT_DELETE_OUT_POWER);
   		return false;
    }
    
	if(!confirm("确认要删除吗?")){
		return false;
	}
	var hashvo = new ParameterSet();
    hashvo.setValue("setid","<%=setid %>");
    hashvo.setValue("flag","delete");
    hashvo.setValue("itemid",codeitemid);
    hashvo.setValue("title",currnode.title);
    var request=new Request({method:'post',asynchronous:false,onSuccess:isRemove,functionId:'2020030019'},hashvo);
    function isRemove(outparamters){
      if(outparamters){
    	var temp1=outparamters.getValue("check");
    	if("yes" == temp1)
			currnode.remove();	
		else if(!temp1 || "no" == temp1)
    		alert("删除失败!");
    	else
    		alert(temp1);
      }
    } 
}
function createFile(){
 	var filepath = "<%=filepath%>";
	var hashvo = new ParameterSet();
   	hashvo.setValue("filepath",filepath);
    hashvo.setValue("flag","54");
    var request=new Request({method:'post',asynchronous:false,onSuccess:createFileOk,functionId:'2020030021'},hashvo);
}
function createFileOk(outparamters){
	alert("目录发布成功!");
}

function checkIsParent(codeitemid){
	var isp = false;
	var hashvo = new ParameterSet();
    hashvo.setValue("codeitemid",codeitemid);
    hashvo.setValue("id","");
    hashvo.setValue("setid","<%=setid %>");
    hashvo.setValue("flag","tree");
    var request=new Request({method:'post',asynchronous:false,onSuccess:isParent,functionId:'2020030070'},hashvo);
	function isParent(outparamters){
		if(outparamters){
		   var temp1=outparamters.getValue("isParent")
		   if("yes" == temp1)
				isp = true;
	   	}
	} 
   	return isp;
}

</SCRIPT>
</HEAD>
<hrms:themes />
<body style="margin-left:0px;margin-top:0px">
<table width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr align="left" class="toolbar">
		<td valign="middle" align="left" style="padding-left: 10px;padding-top: 5px;">
		  <hrms:priv func_id="3230501" module_id="">	
			<input type="image" name="b_new" style="margin-right: 5px;" src="/images/add.gif" alt="新增" onclick="addCode();"> 
		  </hrms:priv>           
		  <hrms:priv func_id="3230503" module_id="">       
			<input type="image" name="b_delete" style="margin-right: 5px;" src="/images/del.gif" alt="删除" onclick="deleteCode();"> 
		  </hrms:priv>     
		  <hrms:priv func_id="3230502" module_id="">       
			<input type="image" name="b_update" style="margin-right: 5px;" src="/images/edit.gif" alt="修改" onclick="updateCode();">                  
		  </hrms:priv>
		  <!-- 
		  <hrms:priv func_id="3230504" module_id="">
			<input type="image" name="b_order" src="/images/index.gif" alt="发布" onclick="createFile();">
		  </hrms:priv> -->
		</td>
	</tr>
	<tr>
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	</tr>
</table>
</BODY>
</HTML>
<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile	= "/train/resource/get_code_tree.jsp?codesetid=54&privflag=";	 
	var root=new xtreeItem("root","培训资料类型","/train/resource/trainRescList.do?b_query=link&type=5&a_code=all","mil_body","培训资料类","/images/add_all.gif",m_sXMLFile);
	root.setup(document.getElementById("treemenu"));
</SCRIPT>
<script>
	root.openURL();
</script>
 

