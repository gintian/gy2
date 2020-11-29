<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";

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
<script language="javascript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
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
<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}


//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*
function document.oncontextmenu() 
{ 
  	return false; 
} 
*/
</script>
   
   
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
   
<script language="javascript">

 	function createfolder() {
 		var hashvo=new HashMap();
		var kind = ${multiMediaFileForm.kind};  	
		hashvo.put("checkFlag","1");   
		Rpc({functionId:'1010094007',async:false,success:addfolder},hashvo);
		
 	}
 	
 	function addfolder(outparamters){
 		var result = Ext.decode(outparamters.responseText);
 		if("useUp" == result.flag) {
 			alert(MEDIASORT_IS_FULL);
 			return false;
 		}
 		
 		var thecodeurl ="/general/inform/view/save_multimedia_folder.jsp?";
 		var dh = 150,dw = 280;
 		if(getBrowseVersion()){
    		  var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:280px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no;");
		      if(return_vo==null) return;
		      createCallBackAction(return_vo);
 		}else{
 			thecodeurl = thecodeurl + "callback=createCallBackAction";
 			var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
 			var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
 			window.open(thecodeurl,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
 		}
 	}
 	
 	function createCallBackAction(returnObj){
	 		var currname,foldername;
	     	var currnode=Global.selectedItem; 
	     	currname=currnode.uid;
    		var root = currnode.root();
    	    if(getBrowseVersion()){
    			foldername = returnObj;
    		}else{ 
    			foldername = returnObj[0];
    		} 
    		for(var i=0;i<=root.childNodes.length-1;i++){
    			if(root.childNodes[i].text == foldername){
    				alert(REINPUT_FIELD_TYPE+"！");
    				return;
    			}
    		}
    		document.getElementById("foldername").value = foldername;
    		var hashvo=new HashMap();
    		var kind = ${multiMediaFileForm.kind};  	
    		hashvo.put("foldername",foldername);   
    		hashvo.put("currname",currname);   
    		hashvo.put("kind",kind);   
    		Rpc({functionId:'1010094007',async:false,success:create_folder_ok},hashvo);
 	}
 	
 	function create_folder_ok(outparamters) {
 		var result = Ext.decode(outparamters.responseText);
 		if("useUp" == result.flag) {
 			alert(MEDIASORT_IS_FULL);
 			return false;
 		}
 			
 		var currnode=Global.selectedItem;
 		var iconurl=currnode.uid;
     	if(iconurl!="root")
        	currnode=currnode.parent;
     	
 		var id=result.id;
     	var sortname=result.sortname;
     	var multimediaflag=result.multimediaflag;
     	if(currnode.load) {
     		var kind = '${multiMediaFileForm.kind}';  	
     		var m_sXMLFile="/general/inform/multimedia/multimedia_tree.jsp?a0100=${multiMediaFileForm.a0100}&dbname=${multiMediaFileForm.nbase}&kind="+kind+"&multimediaflag="+multimediaflag;	                          
            var tmp=new xtreeItem(id,sortname,"/general/inform/multimedia/opermultimedia.do?b_query=link&multimediaflag="+multimediaflag,"il_new_body",sortname,"/images/open.png",m_sXMLFile);  			 
     		currnode.add(tmp);
     		//liuy 2014-12-23 6237：员工管理中新增附件时，不能自动定位到新增的文件夹上进行刷新 start
     		var lastChild=currnode.getLastChild();
     		lastChild.select();
     		//liuy end
     	} else {
     		currnode.expand();
     	}
 		
	}
 	function deletefolder()
 	{
 		var currnode=Global.selectedItem;
 		id=currnode.uid;
		if(id=="root")
		{	
			alert(SELECT_DELETE_FOLDER+"!");
			return ;
		}
		if(id=='9999'){
			alert("[岗位说明书]为系统内置多媒体分类,不允许删除!");
			return ;
		}
		var result = ifdel();
		if(result==true)
 		{
	 		var hashvo=new HashMap();
	     	hashvo.put("id",id);
	     	hashvo.put("kind","${multiMediaFileForm.kind}");
	     	hashvo.put("dbname","${multiMediaFileForm.nbase}");
	     	Rpc({functionId:'1010094009',async:false,success:delete_ok},hashvo);
 		}
		
 	}
 	function delete_ok(outparamters)
  	{
  		 var result1 = Ext.decode(outparamters.responseText);
  		 var result=result1.result;
  		 if(result=="true")
  		 {
  		 	alert("删除失败，该分类下有记录，不能删除！");
  		 	return;
  		 }else{
  		 	alert("删除成功！");
  		 	var currnode=Global.selectedItem;
		    var preitem=currnode.getPreviousSibling();
		    preitem.target = 'il_new_body';
		    currnode.remove();
		    preitem.select();
  		 }
	     //window.parent.frames.il_body.location="/general/inform/emp/view/opermultimedia.do?b_query=link&a0100=${multiMediaFileForm.a0100}&dbname=${multiMediaFileForm.nbase}&multimediaflag=${multiMediaFileForm.multimediaflag}&kind=${multiMediaFileForm.kind}";
		 //window.parent.frames.il_body.location.reload();
     	 
  	}
 	function ifdel()
	{
		return ( confirm(DELETE_TYPE_MULTIMEDIA_FIELD+'？') );	
	}
	function editfolder()
	{
		var currnode=Global.selectedItem;
 		id=currnode.uid;
 		var title = currnode.text;
		if(id=="root")
		{	
			alert(SELECT_UPDATE_FOLDER+"!");
			return ;
		}
 		
 		var dh = 150,dw = 280;
 		var thecodeurl ="/general/inform/view/edit_multimedia_folder.jsp?sortname="+$URL.encode(title); 
 		if(getBrowseVersion()){
	 		var return_vo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:280px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no;");
	        if(return_vo==null)
				return ; 
	        editCallBackAction(return_vo);
 		}else{
 			theurl = "/general/inform/view/edit_multimedia_folder.jsp?sortname="+title + "`callback=editCallBackAction"; 
 	 		thecodeurl = "/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
 			var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
 			var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
 			window.open(thecodeurl,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
 		}
 	
	}
	function editCallBackAction(returnObj){
		var currnode=Global.selectedItem;
 		var id=currnode.uid,foldername;
 	    if(getBrowseVersion()){
			foldername = returnObj;
		}else{ 
			foldername = returnObj[0];
		} 
		var hashvo=new HashMap();
     	hashvo.put("id",id);
     	hashvo.put("foldername",foldername);
     	var root = currnode.root();
		for(var i=0;i<=root.childNodes.length-1;i++){
			if(root.childNodes[i].text==foldername){
				alert(REINPUT_FIELD_TYPE+"！");
				return;
			}
		}
		Rpc({functionId:'1010094013',async:false,success:edit_ok},hashvo);
	}
	function edit_ok(outparamters)
	{
		var result = Ext.decode(outparamters.responseText);
		var currnode=Global.selectedItem;
		var text = result.name;
		currnode.setText(text);
		parent.frames["il_new_body"].location.reload();
	}
</script>


<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <hrms:themes></hrms:themes>
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>  
</HEAD>
<body style="margin:0px;<%if("Firefox".equals(browser)){ %>margin-top:-13px;<%} %>padding:0px;" topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" onclick="">
<html:form action="/general/inform/multimedia/multimedia_tree">
<table align="left" border="0" cellpadding="0" cellspacing="0">
<logic:equal value="true"  name="multiMediaFileForm" property="canEdit"><!-- 【6239】员工管理-信息维护-记录录入-点击附件时页面样式有问题   jingq upd 2014.12.25 -->
	<tr align="left" class="toolbar" style="padding-left:5px;width:expression(document.body.offsetHeight);overflow: auto;">
		<td valign="middle" width="1000px">
			<hrms:priv func_id="2606501,03050101,01030101"> 
				<img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
			</hrms:priv> 
			<hrms:priv func_id="2606503,03050101,01030101"> 
				<img id="new" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
			</hrms:priv> 
			<hrms:priv func_id="2606502,03050102,01030102">          	  
				<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
			</hrms:priv> 
		</td>
	</tr>
</logic:equal>
	<tr>
		<td valign="top">
   				<div id="treemenu" style="height: expression(document.body.offsetHeight-37);width:expression(document.body.offsetWidth);overflow:auto;"></div>
		</td>
	</tr>

</table>	 

   <SCRIPT LANGUAGE=javascript>
   			 var kind = ${multiMediaFileForm.kind};  
   		     Global.target = "il_new_body";
   			 var m_sXMLFile="/general/inform/multimedia/multimedia_tree.jsp?a0100=${multiMediaFileForm.a0100}&dbname=${multiMediaFileForm.nbase}&kind="+kind+"&isvisible=${multiMediaFileForm.isvisible}";	                          
             var root=new xtreeItem("root",ALL_FILE,"/general/inform/multimedia/opermultimedia.do?b_query=link&a0100=${multiMediaFileForm.a0100}&dbname=${multiMediaFileForm.nbase}&multimediaflag=&kind="+kind+"&isvisible=${multiMediaFileForm.isvisible}","il_new_body",ALL_FILE,"/images/open.png",m_sXMLFile); 
             root.setup(document.getElementById("treemenu"));   
             
          	// 带有树结构的小窗口  a标签 target值都改为 il_new_body 防止链接刷新到父页面  linbz 20180327
           	var divTreeMenu =  document.getElementById("treemenu");
           	var as = divTreeMenu.getElementsByTagName('a');
           	for(var i=0 ; i<as.length;i++){
           		as[i].setAttribute('target','il_new_body');
           	}    
   </SCRIPT> 
   <html:hidden name="multiMediaFileForm" property="foldername" styleId="foldername"/>
   <html:hidden name="multiMediaFileForm" property="multimediaflag" styleId="multimediaflag"/>
	<html:hidden name="multiMediaFileForm" property="i9999" styleId="i9999"/>
</html:form> 
<BODY>
</HTML>



