
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.sys.cms.ChannelForm,com.hrms.hjsj.sys.EncryptLockClient"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	 boolean isFive=false;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null){
       if(lockclient.getVersion()>=50)
           isFive=true;
   }
    request.setAttribute("isFive",isFive);
	String version_flag="1";//zxj 20160613 此处不再区分标准版专业版userView.getVersion_flag()+
	request.setAttribute("version_flag",version_flag);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&bosflag.equals("hcm")){
%>
<style>
<!--
.menubar{
    margin-top:11px;
}

-->
</style>
<%
}
%>
<html>
<head>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<LINK href="<%=css_url%>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
<link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
<script type="text/javascript" src="../../ext/ext-all.js" ></script>
<script type="text/javascript" src="../../ext/ext-lang-zh_CN.js" ></script> 
<script type="text/javascript" src="../../ext/rpc_command.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 --> 
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="../../js/constant.js"></script>
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
<style type="text/css">
button{background-image:none}
.x-btn-button{padding-top:2px;vertical-align: middle;}
.x-btn-default-toolbar-small .x-btn-inner{font-size: 12px;}
.x-menu-item-text{font-size: 12px;}
</style>
<SCRIPT language="javascript">
Ext.onReady(function(){
	var isFive = "${isFive}";
	var version_flag = "${version_flag}";
	var toolbar = Ext.create("Ext.Toolbar", {
        renderTo: 'abc',
        margin:'0 0 0 0',
        padding:'0 0 0 0',
        id:'toolbarId',
        height:25,
        cls:'background:none',
        border:0,
        width: 111
    });
	 
	<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
    %>
    	Ext.getDom('toolbarId').style.marginTop = "-6px";
    <%
    }
	%>

	 var file = new Ext.menu.Menu({
         shadow: "drop",
         allowOtherMenus: true,
         items: [
             new Ext.menu.Item({
                 text: '新增频道',
                 icon:'../../../images/add.gif',
                 handler: add_channel
             }),
             new Ext.menu.Item({
            	 text: '修改频道',
            	 icon:'../../../images/edit.gif',
                 handler: edit_channel
             }),
             new Ext.menu.Item({
            	 text: '删除频道',
            	 icon:'../../../images/del.gif',
                 handler: delete_channel
             }),
             new Ext.menu.Item({
            	 text: '上传招聘外网图片',
            	 icon:'../../../images/up01.gif',
                 handler: upload_logo
             }),
             new Ext.menu.Item({
            	 text: '频道顺序调整',
            	 icon:'../../../images/sort.gif',
                 handler: change_sort
             })
         ]
     });
	 if(isFive&&"0"!=version_flag)
	 {
			var items =  new Ext.menu.Item({
            	 text: '招聘公告',
            	 icon:'../../../images/img_wd.png',
                 handler: zpReport
               })
		 file.add(items);
	}
     toolbar.add({
         text: "编辑",
         width:50,
         border:false,
         style:'background:none',
         menu: file
     }, {
         text: "发布内容",
         width:60,
         border:false,
         style:'background:none;margin-left:-3;',
         handler: reload_cms
     });

})
//弹出层返回调用方法
function add_channel_return(return_vo){
	
     var currnode=Global.selectedItem; 
     var id=currnode.uid;
  	 if(return_vo==null)
  	 	return ;
  	 /*返回来的值类型怎不对,不能直接按Object对象用*/
     var channel_vo=new Object();
     channel_vo.name=return_vo.name;
     channel_vo.function_id=return_vo.function_id;
     channel_vo.visible=return_vo.visible;
     channel_vo.visible_type=return_vo.visible_type;
     channel_vo.icon_url=return_vo.icon_url;
     channel_vo.icon_width=return_vo.icon_width;
     channel_vo.icon_height=return_vo.icon_height;
     channel_vo.menu_width=return_vo.menu_width;
     if(currnode.uid =="root")
        id="-1";
     else
        id=currnode.uid;  
     var hashvo=new ParameterSet();
     hashvo.setValue("channel_vo",channel_vo);
     hashvo.setValue("parent_id",id);    
     var request=new Request({asynchronous:false,onSuccess:add_channel_ok,functionId:'1010021103'},hashvo);        
}

function add_channel(){
	var return_vo;
	if(Ext.isIE)
    {
     	return_vo= window.showModalDialog("/sys/cms/addOrEditChannel.do?b_query=link", "", 
        "dialogWidth:520px; dialogHeight:310px;resizable:no;center:yes;scroll:yes;status:no");
	}else{
		return_vo= showModalDialogAsOpen("/sys/cms/addOrEditChannel.do?b_query=link", "", 
        "dialogWidth:520px; dialogHeight:310px;resizable:no;center:yes;scroll:yes;status:no");return;
	}
	var currnode=Global.selectedItem; 
    var id=currnode.uid;
 	 if(return_vo==null)
 	 	return ;
 	 /*返回来的值类型怎不对,不能直接按Object对象用*/
    var channel_vo=new Object();
    channel_vo.name=return_vo.name;
    channel_vo.function_id=return_vo.function_id;
    channel_vo.visible=return_vo.visible;
    channel_vo.visible_type=return_vo.visible_type;
    channel_vo.icon_url=return_vo.icon_url;
    channel_vo.icon_width=return_vo.icon_width;
    channel_vo.icon_height=return_vo.icon_height;
    channel_vo.menu_width=return_vo.menu_width;
    if(currnode.uid =="root")
       id="-1";
    else
       id=currnode.uid;  
    var hashvo=new ParameterSet();
    hashvo.setValue("channel_vo",channel_vo);
    hashvo.setValue("parent_id",id);    
  　      var request=new Request({asynchronous:false,onSuccess:add_channel_ok,functionId:'1010021103'},hashvo);      
}
function add_channel_ok(outparamters)
{
     var currnode=Global.selectedItem;
     var channel_id=outparamters.getValue("channel_id");
     var parent_id=outparamters.getValue("parent_id");
     var name = outparamters.getValue("name");
     var imgurl = "/images/lock_co.gif";	 
	 if(currnode.load)
	 {
  	    var tmp = new xtreeItem(channel_id,name,"/sys/cms/queryChannel.do?b_query=query&channel_id="+channel_id+"&parent_id="+channel_id,"mil_body",name,imgurl,"/system/channel/search_channel_servlet?parent_id="+channel_id);
        currnode.add(tmp);
     }
     else
     	currnode.expand();
        
}

function delete_channel(){
     var currnode=Global.selectedItem;
     var iconurl=currnode.icon;
	 if(iconurl!="/images/lock_co.gif"){
		 alert("该频道无法删除");
		 return;
	 }
	   
     if(!ifdelete(currnode.text))
       return;      
     var id=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("channel_id",id);
     var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'1010021105'},hashvo);
}

function delete_ok(){
     var currnode=Global.selectedItem;
     var preitem=currnode.getPreviousSibling();
     if(preitem!=null)
     {
        preitem.select();
        selectedClass("treeItem-text-"+preitem.id);
     }
     else
     {
        var parent =currnode.parent;
        if(parent!=null)
        {
            parent.select();
            selectedClass("treeItem-text-"+parent.id);
       }
     }
     currnode.remove();
}
//弹出层调用返回修改方法
function edit_channel_return(return_vo){
	 var currnode=Global.selectedItem; 
     if(currnode.uid == "root")
         return;
     var bflag=true;
  	 if(return_vo==null)
  	 	return ;
  	 /*返回来的值类型怎不对,不能直接按Object对象用*/
     var channel_vo=new Object();
     channel_vo.name=return_vo.name;
     channel_vo.function_id=return_vo.function_id;
     channel_vo.visible=return_vo.visible;
     channel_vo.visible_type=return_vo.visible_type;
     channel_vo.icon_url=return_vo.icon_url;
     channel_vo.icon_width=return_vo.icon_width;
     channel_vo.icon_height=return_vo.icon_height;
     channel_vo.menu_width=return_vo.menu_width;     
     channel_vo.parent_id=return_vo.parent_id;
     
     var channel_id=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("channel_vo",channel_vo);
     hashvo.setValue("channel_id",channel_id);
   　 var request=new Request({asynchronous:false,onSuccess:edit_channel_ok,functionId:'1010021106'},hashvo);        
}
function edit_channel(){
     var currnode=Global.selectedItem; 
      if(currnode.uid == "root")
           return;
     var theurl="/sys/cms/addOrEditChannel.do?b_query=query&channel_id="+currnode.uid;
     var return_vo;
     if(Ext.isIE)
     {
    	 return_vo= window.showModalDialog(theurl, "", 
	        "dialogWidth:520px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");
     }else{
	     return_vo= showModalDialogAsOpen(theurl, "", 
	        "dialogWidth:520px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");return;
     }
     var bflag=true;
  	 if(return_vo==null)
  	 	return ;
  	 /*返回来的值类型怎不对,不能直接按Object对象用*/
     var channel_vo=new Object();
     channel_vo.name=return_vo.name;
     channel_vo.function_id=return_vo.function_id;
     channel_vo.visible=return_vo.visible;
     channel_vo.visible_type=return_vo.visible_type;
     channel_vo.icon_url=return_vo.icon_url;
     channel_vo.icon_width=return_vo.icon_width;
     channel_vo.icon_height=return_vo.icon_height;
     channel_vo.menu_width=return_vo.menu_width;     
     channel_vo.parent_id=return_vo.parent_id;
     
     var channel_id=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("channel_vo",channel_vo);
     hashvo.setValue("channel_id",channel_id);
   　      var request=new Request({asynchronous:false,onSuccess:edit_channel_ok,functionId:'1010021106'},hashvo);        
}
function edit_channel_ok(outparamters){
 var currnode=Global.selectedItem;
 var name =outparamters.getValue("name");
  currnode.setText(name);

}
function reload_ok(outparamters)
{
	alert(outparamters.getValue("message"));
}
function reload_cms()
{
   var request=new Request({asynchronous:false,onSuccess:reload_ok,functionId:'1010021121'});        
}
function change_sort_return(return_vo){
	
  	 if(return_vo==null)
  	 	return ;
  	 var channel_vo = new Object();
  	 channel_vo.parent_id = return_vo.parent_id;
  	 channel_vo.move_list = return_vo.right_fields;	
  	 channel_vo.isTop = return_vo.isTop;
  	 var map = new HashMap();
  	 map.put("channel_vo",channel_vo);
  	Rpc( {
		functionId : '1010021114',
		success :change_sort_ok
	}, map);
}
function change_sort(){
     var currnode=Global.selectedItem; 
     if(currnode.uid == "root") {
    	 alert(NOT_SELECT_ROOT_SORT);
		return;
     }
     
     var bflag=true;
     var theurl="/sys/cms/channelMoveList.do?b_move=move&channel_id="+currnode.uid;
     var return_vo;
     var userAgent = navigator.userAgent;
     if(Ext.isChrome || userAgent.indexOf("Firefox") > -1)
     {
	     return_vo= showModalDialogAsOpen(theurl, "", 
	    	        "dialogWidth:520px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");return;
	 }else{
	     return_vo= window.showModalDialog(theurl, "", 
	        "dialogWidth:520px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");
	 }
     if(return_vo==null)
   	 	return ;
   	 var channel_vo = new Object();
   	 channel_vo.parent_id = return_vo.parent_id;
   	 channel_vo.move_list = return_vo.right_fields;	
   	 channel_vo.isTop = return_vo.isTop;
   	 var map = new HashMap();
   	 map.put("channel_vo",channel_vo);
   	Rpc( {
 		functionId : '1010021114',
 		success :change_sort_ok
 	}, map);
}
function change_sort_ok(outparamters){
	var value = outparamters.responseText;
	var map = Ext.decode(value);
   var currnode=Global.selectedItem; 
   var currnode=currnode.parent;
   currnode.select();
   currnode.clearChildren();
   currnode.loadChildren();

}
function upload_logo()
{ 
   var strurl="/sys/cms/uploadLogo.do?br_query=link`isClose=1";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl); 
   var flag;
   var userAgent = navigator.userAgent;
   if(Ext.isChrome || userAgent.indexOf("Firefox") > -1)
   {
	   flag= showModalDialogAsOpen(iframe_url,"","dialogWidth=730px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
   }else{
	   flag=window.showModalDialog(iframe_url,"","dialogWidth=730px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
	}
   
}
function zpReport(){
	channelForm.target="mil_body"
  	channelForm.action='/hire/parameterSet/zpReport.do?b_init=init&type=0';
 	channelForm.submit();
	
}
/**
所有showModalDialog替换为open
url 打开弹出框的url地址
obj 参数
sFeatures 用来描述对话框的外观等信息
*/
showModalDialogAsOpen = function(url,obj,sFeatures){
		 sFeatures = sFeatures.replace(/dialogHeight/gi,"height");
		 sFeatures = sFeatures.replace(/dialogWidth/gi,"width"); 
		 sFeatures = sFeatures.replace(/dialogTop/gi,"top"); 
		 sFeatures = sFeatures.replace(/dialogLeft/gi,"left");
		 sFeatures = sFeatures.replace(/:/gi, "=");
		 sFeatures = sFeatures.replace(/;/gi, ",");
	     window.open(url,obj, "top="+(window.screen.availHeight-350)/2+",left="+(window.screen.availWidth-500)/2+","+sFeatures);
}

</SCRIPT>

  </head>
  <BODY>
  <hrms:themes></hrms:themes>
  <style>
  .x-btn-default-toolbar-small .x-frame-bc {
	background-image: none;
  }
  </style>
<html:form action="/sys/cms/channelTree"><div  class="toolbar toolbar_color common_border_color" style="padding-left: 0px;">
	<div id="abc" style="padding-top:7px;margin-left:1px;width: 178"></div>
</div>
 <table align="left" class="mainbackground">
     <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="channelForm" property="contentChannelTree" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
    </tr>           
 </table>
</html:form>
<script language="javascript">
  initDocument();
  //加载页面时默认选中根节点下的第一个子节点
  var currnode=Global.selectedItem; 
  if(currnode.uid == "root") {
	  if(currnode.childNodes.length)
		  currnode.getFirstChild().select();
  }
</script>
</body>
</html>

