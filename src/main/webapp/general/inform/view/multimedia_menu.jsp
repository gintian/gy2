<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
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
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag="";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
<script language="JavaScript" src="/module/utils/js/template.js"></script>	
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script language="javascript">

 	function createfolder()
 	{
 		
 		var hashvo=new HashMap();
		var kind = ${mInformForm.kind};  	
		hashvo.put("checkFlag","1");   
		Rpc({functionId:'1010094007',async:false,success:addfolder},hashvo);
 	}
 	
 	function addfolder(outparamters){
 		var result = Ext.decode(outparamters.responseText);
 		if("useUp" == result.flag) {
 			alert(MEDIASORT_IS_FULL);
 			return false;
 		}
 		
 		var currname;
     	var currnode=Global.selectedItem; 
     	currname=currnode.uid;
 		var thecodeurl ="/general/inform/view/save_multimedia_folder.jsp?callback=closeAction"; 
        window.open(thecodeurl,'_blank',"width=280,height=150,top="+screen.height/3+"px,left="+screen.width/3+"px,toolbar=no,location=no,resizable=no");
 	}
 	
 	//add by xiegh on 20171205 多媒体编辑文件夹名称
 	function closeAction(outparameter){
		var currnode = Global.selectedItem;
		var foldername  = outparameter[0];
		var root = currnode.root();
		for(var i=0;i<=root.childNodes.length-1;i++){
			if(root.childNodes[i].text==foldername){
				alert(REINPUT_FIELD_TYPE+"！");
				return;
			}
		}
		document.getElementById("foldername").value =  outparameter[1];
		var hashvo=new HashMap();
		var kind = ${mInformForm.kind};  	
		hashvo.put("foldername",foldername);   
		hashvo.put("currname",currnode.uid);   
		hashvo.put("kind",kind);   
		Rpc({method:'post',asynchronous:false,functionId:'1010094007',success:create_folder_ok},hashvo);
	}
 	function create_folder_ok(outparamters)
 	{
 		var res=Ext.decode(outparamters.responseText);
 		if("useUp" == res.flag) {
 			alert(MEDIASORT_IS_FULL);
 			return false;
 		}
 		
 		var currnode=Global.selectedItem;
 		var iconurl=currnode.uid;
     	if(iconurl!="root")
         currnode=currnode.parent;
 		var id=res.id;
     	var sortname=res.sortname;
     	var multimediaflag=res.multimediaflag;
     	if(currnode.load)
     	{
     		var kind = '${mInformForm.kind}';  	
     		var m_sXMLFile="/general/inform/view/multimedia_tree.jsp?a0100=${mInformForm.a0100}&dbname=${mInformForm.dbname}&kind="+kind+"&multimediaflag="+multimediaflag;	 //【5244】自助服务/员工信息，多媒体，新增多媒体分类后，右侧界面刷新不对。 jingq upd 2014.11.21                         
            var tmp=new xtreeItem(id,sortname,"/general/inform/emp/view/opermultimedia.do?b_query=link&a0100=${mInformForm.a0100}&dbname=${mInformForm.dbname}&multimediaflag="+multimediaflag+"&kind="+kind+"&multimediaflag="+multimediaflag,"il_new_body",sortname,"/images/open.png",m_sXMLFile);  			 
     		currnode.add(tmp);
     		//liuy 2014-12-22 4938：自助服务-员工信息-员工信息-多媒体（新建一个分类后，右面的附件不刷新）  （组织机构也有此问题） start
     		var lastChild=currnode.getLastChild();
     		lastChild.select();
     		//liuy end
     	}else
     	{
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
	     	hashvo.put("kind","${mInformForm.kind}");
	     	hashvo.put("dbname","${mInformForm.dbname}");
	     	/* var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'1010094009'},hashvo); */
	     	Rpc({method:'post',asynchronous:false,functionId:'1010094009',success:delete_ok},hashvo);
 		}
		
 	}
 	function delete_ok(outparamters)
  	{
 		var res=Ext.decode(outparamters.responseText);
	     var currnode=Global.selectedItem;
	     var preitem=currnode.getPreviousSibling();
	     if(res.result=="true"){
	     	alert("该分类下面有附件记录不允许删除！");
	     	return;
	     }
	     currnode.remove();
	     //删除弹窗左侧文件分类时,界面显示到父页面 修改target跳转页面   bug 36132  wangb 20180402
	     preitem.target = 'il_new_body';
	     preitem.select();
	     //window.parent.frames.il_new_body.location="/general/inform/emp/view/opermultimedia.do?b_query=link&a0100=${mInformForm.a0100}&dbname=${mInformForm.dbname}&multimediaflag=${mInformForm.multimediaflag}&kind=${mInformForm.kind}";
		 //window.parent.frames.il_new_body.location.reload();
     	 
  	}
 	function ifdel()
	{
		return ( confirm(DELETE_TYPE_MULTIMEDIA_FIELD+'？') );	
	}
	function editfolder()
	{
		var currnode=Global.selectedItem;
 		id=currnode.uid;
		if(id=="root")
		{	
			alert(SELECT_UPDATE_FOLDER+"!");
			return ;
		}
 		var thecodeurl ="/general/inform/view/edit_multimedia_folder.jsp?callback=editcloseaction&sortname="+$URL.encode(getEncodeStr(currnode.text)); 
        window.open(thecodeurl,'_blank',"width=280,height=150,top="+screen.height/3+"px,left="+screen.width/3+"px,toolbar=no,location=no,resizable=no");
	}
	function editcloseaction(outparamters){
		var currnode=Global.selectedItem;
		var hashvo=new HashMap();
	 	hashvo.put("id",currnode.uid);
	 	hashvo.put("foldername",outparamters[0]);
	 	var root = currnode.root();
		for(var i=0;i<=root.childNodes.length-1;i++){
			if(root.childNodes[i].text==outparamters[0]){
				alert(REINPUT_FIELD_TYPE+"！");
				return;
			}
		}
	 	//var request=new Request({asynchronous:false,onSuccess:edit_ok,functionId:'1010094013'},hashvo);
		Rpc({method:'post',asynchronous:false,functionId:'1010094013',success:edit_ok},hashvo);
	}
	function edit_ok(outparamters)
	{
		var res=Ext.decode(outparamters.responseText);
		var currnode=Global.selectedItem;
		var text = res.name;
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
   <hrms:themes />
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>  
</TITLE> 
</HEAD>
<body  style="margin:0px;<%if("Firefox".equals(browser)){ %>margin-top:-13px;<%} %>padding:0px;" topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" >
<html:form action="/general/inform/emp/view/multimedia_tree">
<table align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" id="tableId">
	<tr align="left" valign="middle">
		<td valign="middle"  align="left" class="toolbar" style="border-bottom:0px;">
		<logic:equal name="mInformForm" property="is_yewu" value="all">
		                <logic:equal value="6"  name="mInformForm" property="kind">
				            <hrms:priv func_id="2606601,03050101"> 
			               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
					 		</hrms:priv> 
					 		<hrms:priv func_id="2606603,03050103"> 
			               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
					 		</hrms:priv>
					 		 <hrms:priv func_id="2606602,03050102">          	  
						 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
						    </hrms:priv>
						</logic:equal>
						<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2506301,07090101"> 
				               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
						 		</hrms:priv> 
						 		<hrms:priv func_id="2506303,07090103"> 
				               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
						 		</hrms:priv>
						 		<hrms:priv func_id="2506302,07090102">          	  
							 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
							    </hrms:priv>
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2306301,050104010101"> 
				               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
						 		</hrms:priv> 
						 		<hrms:priv func_id="2306303,050104010103"> 
				               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
						 		</hrms:priv>
						 		<hrms:priv func_id="2306302,050104010102">          	  
							 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
							    </hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
						
		</logic:equal>	
		<logic:notEqual name="mInformForm" property="is_yewu" value="all">
			<logic:equal name="mInformForm" property="is_yewu" value="yes">	 	
						<logic:equal value="6"  name="mInformForm" property="kind">
				            <hrms:priv func_id="2606601,03050101"> 
			               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
					 		</hrms:priv> 
						</logic:equal>
						<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2506301"> 
				               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
						 		</hrms:priv> 
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2306301"> 
				               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
						 		</hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
			 </logic:equal>	
			 <logic:notEqual name="mInformForm" property="is_yewu" value="yes">
						<logic:equal value="6"  name="mInformForm" property="kind">
				            <hrms:priv func_id="03050101"> 
			               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
					 		</hrms:priv> 
						</logic:equal>
						<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="07090101"> 
				               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
						 		</hrms:priv> 
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="050104010101"> 
				               	   <img id="new" src="/images/add.gif" title='<bean:message key="general.mediainfo.folder.create"/>' onclick="createfolder()">  
						 		</hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
			 </logic:notEqual>
			 <logic:equal name="mInformForm" property="is_yewu" value="yes">	 	
						<logic:equal value="6"  name="mInformForm" property="kind">
				            <hrms:priv func_id="2606603,03050103"> 
			               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
					 		</hrms:priv> 
						</logic:equal>
						<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2506303"> 
				               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
						 		</hrms:priv> 
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2306303"> 
				               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
						 		</hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
			 </logic:equal>	
			 <logic:notEqual name="mInformForm" property="is_yewu" value="yes">
						<logic:equal value="6"  name="mInformForm" property="kind">
				            <hrms:priv func_id="03050103"> 
			               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
					 		</hrms:priv> 
						</logic:equal>
						<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="07090103"> 
				               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
						 		</hrms:priv> 
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="050104010103"> 
				               	   <img id="edit" src="/images/edit.gif" title='<bean:message key="general.mediainfo.folder.rework"/>' onclick="editfolder()">  
						 		</hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
			 </logic:notEqual>
			 	
	
			 <logic:equal name="mInformForm" property="is_yewu" value="yes">
				 	<logic:equal value="6"  name="mInformForm" property="kind">
				             <hrms:priv func_id="2606602,03050102">          	  
						 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
						    </hrms:priv> 
						</logic:equal>
					<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2506302">          	  
							 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
							    </hrms:priv> 
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="2306302">          	  
							 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
							    </hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
			 </logic:equal>	
			 <logic:notEqual name="mInformForm" property="is_yewu" value="yes">	 	
	
				 	<logic:equal value="6"  name="mInformForm" property="kind">
				             <hrms:priv func_id="03050102">          	  
						 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
						    </hrms:priv> 
						</logic:equal>
					<logic:notEqual value="6"  name="mInformForm" property="kind">
							<logic:equal value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="07090102">          	  
							 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
							    </hrms:priv> 
							</logic:equal>
							<logic:notEqual value="0"  name="mInformForm" property="kind">
					            <hrms:priv func_id="050104010102">          	  
							 	 	<img id="delete" src="/images/del.gif" title='<bean:message key="general.mediainfo.folder.delete"/>'  onclick="deletefolder()"> 
							    </hrms:priv> 
							</logic:notEqual>
						</logic:notEqual>
	
			 </logic:notEqual>
		 </logic:notEqual>
	<!-- </div> -->
		</td>
	</tr>
	<tr>
		<td valign="top">
   				<div id="treemenu" ></div>
		</td>
	</tr>

</table>	 

   <SCRIPT LANGUAGE=javascript>
   			 var kind = ${mInformForm.kind};  	
   			 Global.target = "il_new_body";
   			 var m_sXMLFile="/general/inform/view/multimedia_tree.jsp?a0100=${mInformForm.a0100}&dbname=${mInformForm.dbname}&kind="+kind+"&isvisible=${mInformForm.isvisible}";	                          
             var root=new xtreeItem("root",ALL_FILE,"/general/inform/emp/view/opermultimedia.do?b_query=link&a0100=${mInformForm.a0100}&dbname=${mInformForm.dbname}&multimediaflag=&kind="+kind+"&isvisible=${mInformForm.isvisible}","il_new_body",ALL_FILE,"/images/open.png",m_sXMLFile); 
             //var root=new xtreeItem("root","所有文件","javascript:void(0)","il_new_body","所有文件","/images/open.png",m_sXMLFile);
             root.setup(document.getElementById("treemenu")); 
     
     	//所有a标签 target值都改为 il_new_body   wangb 20180126
     	var divTreeMenu =  document.getElementById("treemenu");
     	var as = divTreeMenu.getElementsByTagName('a');
     	for(var i=0 ; i<as.length;i++){
     		as[i].setAttribute('target','il_new_body');
     	}       
     	window.onresize = function(){
     		setDivStyle();
     	}

     	function setDivStyle(){
     		if(isCompatibleIE())
     	    	document.getElementById("tableId").style.width = (document.body.clientWidth) + "px";
     		else
     	    	document.getElementById("tableId").style.width = "100%";
     	} 
     	
     	setDivStyle();
   </SCRIPT> 
   <html:hidden name="mInformForm" property="foldername" styleId="foldername"/>
   <html:hidden name="mInformForm" property="multimediaflag" styleId="multimediaflag"/>
	<html:hidden name="mInformForm" property="i9999" styleId="i9999"/>
</html:form> 
<BODY>
</HTML>



