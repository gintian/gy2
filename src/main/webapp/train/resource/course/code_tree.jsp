<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
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
    if (SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
        filepath = session.getServletContext().getResource("/").getPath();//.substring(0);
        if (filepath.indexOf(':') != -1) {
            filepath = filepath.substring(1);
        } else {
            filepath = filepath.substring(0);
        }
        int nlen = filepath.length();
        StringBuffer buf = new StringBuffer();
        buf.append(filepath);
        buf.setLength(nlen - 1);
        filepath = buf.toString();
    }
    filepath = filepath.replace("\\", "``");
%>

<bean:define id="setId" name="courseForm" property="trainsetid"></bean:define>
<%
    if (setId == null || setId.toString().length() <= 0) {
        setId = "55";
    }
    String codesetid = SafeCode.encode(PubFunc.encrypt(setId.toString()));
%>
<HTML>
<HEAD>
<TITLE></TITLE>
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
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
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>


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
</HEAD>
<SCRIPT LANGUAGE=javascript>
function addcodeid(){
	if(<%=setId%>==55){
		var currnode=Global.selectedItem;
	   	if(currnode==null)
	    	return;
	    var codeitemid=currnode.uid;
	    codeitemid=codeitemid=="root"?"":codeitemid;
		var hashvo=new ParameterSet();
		hashvo.setValue("codeitemid",codeitemid);
		var request=new Request({method:'post',asynchronous:true,onSuccess:addcode1,functionId:'202003005102'},hashvo);
	}else{
		addCode();
	}
}
function addCode(){
 	var currnode=Global.selectedItem;
   	if(currnode==null)
    	return;
    var codeitemid=currnode.uid;
    codeitemid=codeitemid=="root"?"":codeitemid;
	var target_url="/train/resource/trainRescList.do?b_addcode=link&flag=add&setid=<%=codesetid%>&itemid="+codeitemid;
    var return_vo= window.showModalDialog(target_url, false, 
        		"dialogWidth:520px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var beanvalue = return_vo;
    	var itemid = beanvalue.id;
	 	var codeitemdesc = getDecodeStr(beanvalue.desc);
	 	if(currnode.load){
			var imgurl;
			imgurl="/images/book.gif";
			var xmlstr	= "/train/resource/course/get_code_tree.jsp?codesetid=<%=codesetid%>&codeitemid="+itemid;	
			var tmp=new xtreeItem(itemid,codeitemdesc,
				"/train/resource/course.do?b_query=link&a_code="+itemid,
				"mil_body",codeitemdesc,imgurl,xmlstr);
			<%if ("69".equals(setId)) {%>
				tmp=new xtreeItem(itemid,codeitemdesc,
				"/train/trainexam/question/questiones/questiones.do?b_query=link&a_code="+itemid,
				"mil_body",codeitemdesc,imgurl,xmlstr);
			<%}%>
			
		 	currnode.add(tmp);
		 	tmp.expand();
	     }else
	     	currnode.expand();
    }
}
//动态刷新树的节点
function addcode1(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=="true"){
		addCode();
	}else{
		alert("课程下不能添加课程与分类，请选择课程分类进行添加");
	}
}
function updateCodeid(){
	if(<%=setId%>==55){
		var currnode=Global.selectedItem;
	   	if(currnode==null)
	    	return;
	    var codeitemid=currnode.uid;
	    codeitemid=codeitemid=="root"?"":codeitemid;
		var hashvo=new ParameterSet();
		hashvo.setValue("codeitemid",codeitemid);
		var request=new Request({method:'post',asynchronous:true,onSuccess:updateCode1,functionId:'202003005102'},hashvo);
	}else{
		updateCode();
	}
}
function updateCode1(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=="true"){
		updateCode();
	}else{
		alert("此节点是一门课程，不允许修改！");
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
   		alert("只能修改管理范围下的分类!");
   		return false;
   	}
    codeitemid=codeitemid=="root"?"":codeitemid;
	var target_url="/train/resource/trainRescList.do?b_addcode=link&flag=update&setid=<%=codesetid%>&itemid="+codeitemid;
	target_url+="&codedesc="+$URL.encode(getEncodeStr(currnode.text));
    var return_vo= window.showModalDialog(target_url, false, 
        		"dialogWidth:520px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var beanvalue = return_vo;
	 	var codeitemdesc = getDecodeStr(beanvalue.desc);
	 	//var currnode =currnode.parent;
	 	currnode.setText(codeitemdesc);
	 	/**if(currnode.getFirstChild().text==currnode.text){
			currnode.loadChildren();
			currnode.expand();
		}else{
			currnode.clearChildren();
			currnode.loadChildren();
			currnode.expand();
		}**/
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
   		alert("只能删除管理范围下的分类!");
   		return false;
   	}
   	if(!confirm("确认要删除吗?")){
		return false;
	}
	var hashvo = new ParameterSet();
    hashvo.setValue("setid","<%=codesetid%>");
    hashvo.setValue("flag","delete");
    hashvo.setValue("itemid",codeitemid);
    hashvo.setValue("title",currnode.title);
    var request=new Request({method:'post',asynchronous:false,onSuccess:isRemove1,functionId:'2020030019'},hashvo);
	function isRemove1(outparamters){
		if(outparamters){
		   var temp1=outparamters.getValue("check")
		   if("yes" == temp1)
				currnode.remove();	
			else if(!temp1 || "no" == temp1)
	    		alert("删除失败!");
	    	else
	    		alert(temp1);
	   	}
	} 
}
function checkIsParent(codeitemid){
	var isp = false;
	var hashvo = new ParameterSet();
    hashvo.setValue("codeitemid",codeitemid);
    hashvo.setValue("id","");
    hashvo.setValue("setid","<%=codesetid%>");
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
function createFile(){
 	var filepath = "<%=filepath%>";
	var hashvo = new ParameterSet();
   	hashvo.setValue("filepath",filepath);
    hashvo.setValue("flag","55");
   	var request=new Request({asynchronous:false,functionId:'2020030021'},hashvo); 
   	alert("发布成功！");
}
function codeSort(){
	var currnode=Global.selectedItem;
   	if(currnode==null)
    	return;
    var codeitemid=currnode.uid;
    var thecodeurl = "/train/resource/course.do?b_codesort=link&codeitemid=" + codeitemid + "&codesetid=" + "<%=codesetid%>";
	var return_vo = window.showModalDialog(thecodeurl, "", "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
	if (return_vo != null) {
		if(currnode.load){
			while(currnode.childNodes.length){
				currnode.childNodes[0].remove();
			}
		}
		currnode.load=true;
		currnode.loadChildren();
		currnode.reload(1);
	}	
}

// 参数设置
function setParam() {

    var thecodeurl = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode("/train/resource/course.do?b_setparam=link`opt=search");
	var return_vo = window.showModalDialog(thecodeurl, "", "dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
		
}
//培训课程添加
function add(currnode,uid,text,action,target,title,imgurl,xml){
	var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
	currnode.add(tmp);
	tmp.expand();
}
</SCRIPT>
<hrms:themes />
	<body style="margin-left:0px;margin-top:0px">
<table width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr align="left"   class="toolbar">
		<td valign="middle" align="left" style="padding-left: 10px;padding-top: 6px;">
		<%
		    if (!"69".equals(setId)) {
		%>
		  <hrms:priv func_id="32306C01" module_id="">
			<input style="margin-right: 5px;" type="image" name="b_new" src="/images/add.gif" alt="新增" onclick="addcodeid();"> 
		  </hrms:priv>
		  <hrms:priv func_id="32306C03" module_id="">                   
			<input style="margin-right: 5px;" type="image" name="b_delete" src="/images/del.gif" alt="删除" onclick="deleteCode();"> 
		  </hrms:priv>
		  <hrms:priv func_id="32306C02" module_id="">            
			<input style="margin-right: 5px;" type="image" name="b_update" src="/images/edit.gif" alt="修改" onclick="updateCodeid();">  
		  </hrms:priv>
		  <!-- 
		  <hrms:priv func_id="32306C04" module_id=""> 
			<input type="image" name="b_order" src="/images/index.gif" alt="发布" onclick="createFile();"> 
		  </hrms:priv> -->		      
		  <hrms:priv func_id="32306C14" module_id=""> 
			<input style="margin-right: 5px;" type="image" name="b_order" src="/images/sort.gif" alt="排序" onclick="codeSort();"> 
		  </hrms:priv> 
		  <hrms:priv func_id="32306C15" module_id=""> 
			<input style="margin-right: 5px;" type="image" name="b_setparam" width="16" height="16" src="/images/img_o.gif" alt="<bean:message key='train.resource.course.setparam'/>" onclick="setParam();"> 
		  </hrms:priv>
		  <%
		      } else {
		  %>  
		  	<hrms:priv func_id="3238130201" module_id="">
			<input style="margin-right: 5px;" type="image" name="b_new" src="/images/add.gif" alt="新增" onclick="addCode();"> 
		  </hrms:priv>
		  <hrms:priv func_id="3238130203" module_id="">                   
			<input style="margin-right: 5px;" type="image" name="b_delete" src="/images/del.gif" alt="删除" onclick="deleteCode();"> 
		  </hrms:priv>
		  <hrms:priv func_id="3238130202" module_id="">            
			<input style="margin-right: 5px;" type="image" name="b_update" src="/images/edit.gif" alt="修改" onclick="updateCode();">  
		  </hrms:priv>	      
		  <hrms:priv func_id="3238130204" module_id=""> 
			<input style="margin-right: 5px;" type="image" name="b_order" src="/images/sort.gif" alt="排序" onclick="codeSort();"> 
		  </hrms:priv>  
		   
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
</BODY>
</HTML>
<SCRIPT LANGUAGE=javascript>
	<%if ("55".equals(setId)) {%>
	var m_sXMLFile	= "/train/resource/course/get_code_tree.jsp?codesetid=55";	
	var root=new xtreeItem("root","培训课程分类","/train/resource/course.do?b_query=link","mil_body","培训课程分类","/images/add_all.gif",m_sXMLFile);
	<%} else if ("69".equals(setId)) {%>
	var m_sXMLFile	= "/train/resource/course/get_code_tree.jsp?codesetid=69";	
	var root=new xtreeItem("root","试题分类","/train/trainexam/question/questiones/questiones.do?b_query=link&a_code=","mil_body","试题分类","/images/add_all.gif",m_sXMLFile);
	<%}%>
	root.setup(document.getElementById("treemenu"));
	

	curNode = root;
	while (1 == curNode.childNodes.length)
	{
		curNode.expand();
		
		//展开后，没有子节点或有多个子节点时，不再继续展开子节点了
		if(0 == curNode.childNodes.length || 1 < curNode.childNodes.length)
			break;
		
        curNode = curNode.childNodes[0];        
	}

	root.openURL();
</script>
