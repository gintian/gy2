<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
<html>
<head>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<LINK href="<%=css_url%>" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
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
<script language="javascript" src="/js/constant.js"></script>
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
<SCRIPT LANGUAGE="javascript">
function setStyleRoster(){
	self.parent.nil_body.location ="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=${musterForm.infor_Flag}&refleshtree=1"; 
}
function setRosterType(){
	var thecodeurl="/general/muster/opertype.do?b_query=link&flag=add&a_inforkind=${musterForm.infor_Flag}";
    var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:420px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no;");
   self.parent.location ="/general/muster/hmuster/searchroster.do?b_search=link&a_hflag=${musterForm.hflag}&a_inforkind=${musterForm.infor_Flag}&result=0";  
}
function delRosterType(){
	var thecodeurl="/general/muster/opertype.do?b_query=link&flag=del&a_inforkind=${musterForm.infor_Flag}";
    var  return_vo= window.showModalDialog(thecodeurl, "", 
            "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no;");
    self.parent.location ="/general/muster/hmuster/searchroster.do?b_search=link&a_hflag=${musterForm.hflag}&a_inforkind=${musterForm.infor_Flag}&result=0"; 
}
function delStyleRoster(){
	var obj=Global.selectedItem;
	if(obj==null||obj.uid==null||obj.uid.length<1||obj.uid=='root'){
		alert("请选择您将要删除花名册!");
		return;
	}
	//获取参数方式不对 改为 name 属性获取  wangb 20190308
	if(!parent.nil_body.document.getElementsByName("currid")[0])//if(!parent.nil_body.document.getElementById("currid"))
		return false;
	var tabid = parent.nil_body.document.getElementsByName("currid")[0].value;//var tabid = parent.nil_body.document.getElementById("currid").value;
	if(obj.uid!=tabid){
		alert("请选择您将要删除花名册!");
		return false;
	}
	if(confirm(DEL_INFO)){
		var inforkind = "${musterForm.infor_Flag}";
		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("infor_kind",inforkind);					
		var request=new Request({method:'post',asynchronous:false,functionId:'1010095004'},hashvo);
		self.parent.location ="/general/muster/hmuster/searchroster.do?b_search=link&a_hflag=${musterForm.hflag}&a_inforkind="+inforkind+"&result=0"; 
	}
}
function setCommonQueryCond()
{
    var obj=Global.selectedItem;
    if(obj==null||obj.uid==null||obj.uid.length<1||obj.uid=='root'){
		alert("请选择花名册!");
		return false;
	}
    var inforkind = "${musterForm.infor_Flag}";
    var ids=obj.uid;
	
	//获取参数方式不对 改为 name 属性获取  wangb 20190308
	if(!parent.nil_body.document.getElementsByName("currid")[0])//if(!parent.nil_body.document.getElementById("currid"))
		return false;
	var tabid = parent.nil_body.document.getElementsByName("currid")[0].value;//var tabid = parent.nil_body.document.getElementById("currid").value;
	if(obj.uid!=tabid){
		alert("请选择花名册!");
		return false;
	}
	var thecodeurl="/general/muster/hmuster/searchroster.do?b_searchquery=link&tableid="+obj.uid+"&info_flag=${musterForm.infor_Flag}";
   //window.open(thecodeurl,"_blank");
    var h = "200";
    if(isIE6())
    	h = "220";   
    var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:"+h+"px;resizable:no;center:yes;scroll:no;status:no;");
	if(return_vo)
	{
	    //self.parent.nil_body.location ="/general/muster/fillout_musterdata.do?b_search=link&tabid="+obj.uid; 
	    //self.parent.location ="/general/muster/hmuster/searchroster.do?b_search=link&a_hflag=${musterForm.hflag}&a_inforkind="+inforkind+"&result=0"; 
	   var currnode=Global.selectedItem; 
	    var parentp=currnode.parent;
	   if(parentp!=null)
       {
         parentp.clearChildren();
         parentp.loadChildren();
         parentp.expand();
         for(var i=0;i<parentp.childNodes.length;i++)
         {
           if(parentp.childNodes[i].uid==ids)
           {
              if(return_vo=='0')
              {
                 parentp.childNodes[i].select();
              }
              selectedClass("treeItem-text-"+parentp.childNodes[i].id);
           }
        }     
       }
	 }  
}
function resetStyleRoster(){
	
	var obj=Global.selectedItem;
    if(obj==null||obj.uid==null||obj.uid.length<1||obj.uid=='root'){
		alert("请选择花名册!");
		return false;
	}
    //获取参数方式不对 改为 name 属性获取  wangb 20190308
	if(!parent.nil_body.document.getElementsByName("currid")[0])//if(!parent.nil_body.document.getElementById("currid"))
		return false;
	var tabid = parent.nil_body.document.getElementsByName("currid")[0].value;//var tabid = parent.nil_body.document.getElementById("currid").value;
	if(obj.uid!=tabid){
		alert("请选择花名册!");
		return false;
	}
	if(!confirm(REFILL_EMPTY_RECORD)){
		return false;
	}
    //获取参数方式不对 改为 name 属性获取  wangb 20190308
	if(!parent.nil_body.document.getElementsByName("currid")[0])//if(!parent.nil_body.document.getElementById("currid"))
		return false;
	var tabid = parent.nil_body.document.getElementsByName("currid")[0].value;//var tabid = parent.nil_body.document.getElementById("currid").value;
	var dbpre ="";
	if(parent.nil_body.document.getElementById("dbpre"))
		dbpre= parent.nil_body.document.getElementById("dbpre").value;
	var inforkind = "${musterForm.infor_Flag}";
	var a_code = "${musterForm.a_code}";

	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);	
	hashvo.setValue("dbpre",dbpre);	
	hashvo.setValue("inforkind",inforkind);	
	hashvo.setValue("a_code",a_code);					
	var request=new Request({method:'post',asynchronous:false,functionId:'0540000010'},hashvo);
	self.parent.nil_body.location ="/general/muster/fillout_musterdata.do?b_search=link&tabid="+tabid;
}
function add(uid,text,action,target,title,imgurl,xml){
	var currnode=Global.selectedItem;
    var iconurl=currnode.icon;
    if(iconurl=="/images/overview_obj.gif")
         currnode=currnode.parent; 
    var badd=1;
    if(currnode.childNodes.length>0){
    	for(var i=currnode.childNodes.length-1;i>=0;i--){
			var curruid=currnode.childNodes[i].uid;
			if(curruid.length<=2){
				badd=3;
				break;
			}else{
				if(curruid.substring(2,4)!=uid){
					badd=3;
					break;
				}
			}
			if(curruid==uid){
				badd=2;
				break;
			}		
		}
		if(badd==1){
			action=replaceAll(action, "amp;","");
			var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
			currnode.add(tmp);
		}else if(badd==3)
			document.location.reload();
	}else{
		document.location.reload();
	}
}
function dragendSort(table,primarykey_column_name,father_column_name){
    //'lname','tabid','ModuleFlag'
	var currnode=Global.selectedItem; 
	if(currnode.dragFrom.uid=='root')
		return false;
	if(currnode.dragbool){
		if(currnode.dragFrom.uid.indexOf("X")!=-1||currnode.dragFrom.uid=='root')
			return false;
		var hashvo=new ParameterSet();
		hashvo.setValue("fromid",currnode.dragFrom.uid);
		hashvo.setValue("toid",currnode.uid);
		hashvo.setValue("table",table);
		hashvo.setValue("primarykey_column_name",primarykey_column_name);
		hashvo.setValue("father_column_name",father_column_name);
		hashvo.setValue("enteryType","muster");
		var request=new Request({method:'post',asynchronous:false,functionId:'0520000004'},hashvo);
		if(currnode.uid.indexOf("X")!=-1||currnode.uid=='root'){
			currnode.dragFrom.remove();
			currnode.clearChildren();
			currnode.expand();
		}else{
			currnode.dragFrom.remove();
			currnode.parent.clearChildren();
			currnode.parent.load=true;
	  		currnode.parent.loadChildren();
	  		currnode.parent.reload(1);
		}
	}
}
function reMusterNmae() {
	var currnode=Global.selectedItem; 
	if(currnode==null||currnode.uid==null||currnode.uid.length<1||currnode.uid=='root'){
		alert("请选择花名册!");
		return false;
	}
	var mustername = currnode.text;
	if(currnode.uid.indexOf("X")==-1){
		mustername = mustername.substring(mustername.indexOf(".")+1);
		var return_vo=prompt("花名册名称",mustername);
	}else{
		var return_vo=prompt("花名册分类名称",mustername);
	}
	//var thecodeurl = "/general/muster/alert_name.jsp?mustername="+getEncodeStr(mustername);
  /// 	var return_vo= window.showModalDialog(thecodeurl,"window2",
   	//					"dialogWidth:300px;dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:yes");
   	if(return_vo!=null&&return_vo.length>0){
   		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",currnode.uid);
		    return_vo=replaceAll(return_vo,"'","’");
			return_vo=replaceAll(return_vo,"\"","”");
		hashvo.setValue("mustername",return_vo);
   		var request=new Request({method:'post',asynchronous:false,functionId:'0520000005'},hashvo);
   		currnode.parent.clearChildren();
		currnode.parent.load=true;
	  	currnode.parent.loadChildren();
	  	currnode.parent.reload(1);
	  	currnode.openURL();
   	}
}
/* 功能介绍：替换方法 */
function replaceAll( str, from, to ) {
	var idx = str.indexOf( from );
	while ( idx > -1 ) {
	   str = str.replace( from, to ); 
	   idx = str.indexOf( from );
	}
	return str;
}
</SCRIPT>
  </head>
<BODY onresize="resize();"><!-- 【7001】员工管理，组织机构，界面样式问题  jingq upd 2015.01.28 --> 
  <hrms:themes />
<html:form action="/general/muster/hmuster/searchroster">
<logic:equal name="musterForm" property="infor_Flag" value="3">
 <table id="tabID" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;width:expression(document.body.clientWidth);">
	<tr id="trID" align="left" class="toolbar" style="padding-left:2px;width:expression(document.body.clientWidth);overflow:auto;">
		<td valign="middle" align="left" width="100%">
			<hrms:priv func_id="2503103">
			<img src="/images/add_all.gif" alt="<bean:message key='inform.muster.add.classification'/>" style="cursor:pointer" border="0" onclick="setRosterType();"></img>
			</hrms:priv>
			 <hrms:priv func_id="2503101">	
			<img src="/images/add.gif" alt="<bean:message key='inform.muster.add.roster'/>" style="cursor:pointer" border="0" onclick="setStyleRoster();"></img>                    
			</hrms:priv>
			<hrms:priv func_id="2503104">	
			<img src="/images/delete_all.gif" alt="<bean:message key='inform.muster.del.classification'/>" style="cursor:pointer" border="0" onclick="delRosterType();"></img> 
			</hrms:priv>
			<hrms:priv func_id="2503102">	
			<img src="/images/del.gif" alt="<bean:message key='inform.muster.del.roster'/>" style="cursor:pointer" border="0" onclick="delStyleRoster();"></img>               
			</hrms:priv>
			<hrms:priv func_id="2503105">
			<img src="/images/refillout.gif" alt="<bean:message key='button.fillout'/>" style="cursor:pointer" border="0" onclick="resetStyleRoster();"></img>
			</hrms:priv>
			<hrms:priv func_id="2503106">
			<img src="/images/edit.gif" alt="<bean:message key='button.rename'/>" style="cursor:pointer" border="0" onclick="reMusterNmae();"></img>                  
			</hrms:priv>
			<hrms:priv func_id="2503107">
			<img src="/images/sys_config.gif" alt="设置自动取数条件" border="0" style="cursor:pointer" onclick="setCommonQueryCond();"/>
		    </hrms:priv>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<div id="treemenu" ondragend="dragendSort('lname','tabid','ModuleFlag');"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="musterForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div> 
		</td>
	</tr>   
</table>
</logic:equal>
<logic:equal name="musterForm" property="infor_Flag" value="2">
 <table id="tabID" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;width:expression(document.body.clientWidth);">
	<tr id="trID" align="left" class="toolbar" style="padding-left:2px;width:expression(document.body.clientWidth);overflow:auto;">
		<td valign="middle" align="left" width="100%">
			<hrms:priv func_id="2303103">
			<img src="/images/add_all.gif" alt="<bean:message key='inform.muster.add.classification'/>" style="cursor:pointer" border="0" onclick="setRosterType();"></img>
			</hrms:priv>
			 <hrms:priv func_id="2303101">	
			<img src="/images/add.gif" alt="<bean:message key='inform.muster.add.roster'/>" style="cursor:pointer" border="0" onclick="setStyleRoster();"></img>                    
			</hrms:priv>
			<hrms:priv func_id="2303104">	
			<img src="/images/delete_all.gif" alt="<bean:message key='inform.muster.del.classification'/>" style="cursor:pointer" border="0" onclick="delRosterType();"></img> 
			</hrms:priv>
			<hrms:priv func_id="2303102">	
			<img src="/images/del.gif" alt="<bean:message key='inform.muster.del.roster'/>" style="cursor:pointer" border="0" onclick="delStyleRoster();"></img>               
			</hrms:priv>
			<hrms:priv func_id="2303105">
			<img src="/images/refillout.gif" alt="<bean:message key='button.fillout'/>" style="cursor:pointer" border="0" onclick="resetStyleRoster();"></img>
			</hrms:priv>
			<hrms:priv func_id="2303106">
			<img src="/images/edit.gif" alt="<bean:message key='button.rename'/>" style="cursor:pointer" border="0" onclick="reMusterNmae();"></img>                  
			</hrms:priv>
			<hrms:priv func_id="2303107">
			<img src="/images/sys_config.gif" alt="设置自动取数条件" border="0" style="cursor:pointer" onclick="setCommonQueryCond();"/>
		  </hrms:priv>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<div id="treemenu" ondragend="dragendSort('lname','tabid','ModuleFlag');"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="musterForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div> 
		</td>
	</tr>   
</table>
</logic:equal>
<logic:equal name="musterForm" property="infor_Flag" value="1">
 <table id="tabID" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;width:expression(document.body.clientWidth);">
	<tr id="trID" align="left" class="toolbar" style="padding-left:2px;width:expression(document.body.clientWidth);overflow:auto;">
		<td valign="middle" align="left" width="100%">
			<hrms:priv func_id="2603103,030903">
			<img src="/images/add_all.gif" alt="<bean:message key='inform.muster.add.classification'/>" style="cursor:pointer" border="0" onclick="setRosterType();"></img>
			</hrms:priv>
			 <hrms:priv func_id="2603101,030901">	
			<img src="/images/add.gif" alt="<bean:message key='inform.muster.add.roster'/>" style="cursor:pointer" border="0" onclick="setStyleRoster();"></img>                    
			</hrms:priv>
			<hrms:priv func_id="2603104,030904">	
			<img src="/images/delete_all.gif" alt="<bean:message key='inform.muster.del.classification'/>" style="cursor:pointer" border="0" onclick="delRosterType();"></img> 
			</hrms:priv>
			<hrms:priv func_id="2603102,030902">	
			<img src="/images/del.gif" alt="<bean:message key='inform.muster.del.roster'/>" style="cursor:pointer" border="0" onclick="delStyleRoster();"></img>               
			</hrms:priv>
			<hrms:priv func_id="2603105,030905">
			<img src="/images/refillout.gif" alt="<bean:message key='button.fillout'/>" style="cursor:pointer" border="0" onclick="resetStyleRoster();"></img>
			</hrms:priv>
			<hrms:priv func_id="2603106,030906">
			<img src="/images/edit.gif" alt="<bean:message key='button.rename'/>" style="cursor:pointer" border="0" onclick="reMusterNmae();"></img>                 
			</hrms:priv>
			<hrms:priv func_id="2603107,030907">
			<img src="/images/sys_config.gif" alt="设置自动取数条件" border="0" style="cursor:pointer" onclick="setCommonQueryCond();"/>
		   </hrms:priv>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<div id="treemenu" ondragend="dragendSort('lname','tabid','ModuleFlag');"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="musterForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div> 
		</td>
	</tr>   
</table>
</logic:equal> 
</html:form>
<script language="javascript">
if(document.getElementById("tabID")){
	document.getElementById("tabID").style.width=document.body.clientWidth-1;
}
if(document.getElementById("trID")){
	document.getElementById("trID").style.width=document.body.clientWidth-1;
}	
  initDocument();
  setDrag(true);
  function resize(){
	  if(document.getElementById("tabID"))
		  document.getElementById("tabID").style.width=document.body.clientWidth-1;
	  if(document.getElementById("trID"))
		  document.getElementById("trID").style.width=document.body.clientWidth-1;
  }
</script>
</body>
</html>