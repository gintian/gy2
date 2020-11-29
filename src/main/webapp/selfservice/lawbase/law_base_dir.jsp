<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
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
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
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

<HTML>
	<HEAD>
		<TITLE></TITLE>
       	<link href="/css/xtree.css" rel="stylesheet" type="text/css">
       	<hrms:themes cssName="content.css"/>
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript>
	basetype = "<bean:write name="lawbaseForm" property="basetype" />";
	var caption = "";
	if (basetype == 1) {
	    caption = "<bean:message key="menu.rule"/>";
	}
	if (basetype == 4) {
	    caption = "<bean:message key="law_maintenance.peoplecode"/>";
	}if (basetype == 5) {
	    caption = "<bean:message key="law_maintenance.file"/>";
	}
	function rebuildIndex() {
	
        if(confirm('<bean:message key="law_maintenance.rebuildindex.confirm"/>')) {
    	    var currnode,base_id,target_url;
    		currnode=Global.selectedItem;
    		if(currnode==null)
    	   		return;
    		base_id=currnode.uid;
	    	target_url="/selfservice/lawbase/rebuildindex.do?b_index=link&a_base_id="+base_id + "&basetype=<bean:write name="lawbaseForm" property="basetype" filter="true" />" ;
	    	parent.location.href=target_url;
    		//parent.location.href="/servlet/LawbaseFileOutput?b_query=link&base_id="
    	         //        + base_id;
    	}
	}
	    
	 /*******************************
   	 *新增库huaitao
   	 *******************************/
    	function add_base1()
    	{
    	   var currnode,base_id,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   var theArr=new Array(currnode);
    	   var winFeatures = "dialogHeight:300px; dialogLeft:200px;";
    	   target_url="/selfservice/lawbase/add_law_base.do?b_add=link&a_base_id="+base_id+"&basetype="+basetype;
    	   //var return_vo=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=500,height=354'); 
    	    var return_vo= window.showModalDialog(target_url, false, 
        		"dialogWidth:520px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
    	   var law_base_bean = new Object();
    	   if(return_vo!=null){
	    	   law_base_bean.name = getEncodeStr(return_vo.name);
	    	   law_base_bean.description = getEncodeStr(return_vo.description);
	    	   law_base_bean.check = return_vo.check;
	    	   law_base_bean.up_base_id = return_vo.up_base_id;
	    	   law_base_bean.status = return_vo.status;
	    	   law_base_bean.basetype=basetype;
	    	   var hashvo = new ParameterSet();
    	   	   hashvo.setValue("law_base_bean",law_base_bean);
    	   	   //alert("name:"+law_base_bean.name+" des "+law_base_bean.description+" che "+law_base_bean.check+" id "+law_base_bean.up_base_id+" sta "+law_base_bean.status+" base"+law_base_bean.basetype);
    	   	   var request=new Request({asynchronous:false,onSuccess:add_user_ok,functionId:'10400201040'},hashvo); 
    	   }
    	   
    	   
	    }
	    function add_user_ok(outparamters)
	  {
	  	var currnode=Global.selectedItem;
	     var groupid=outparamters.getValue("new_base_id");
	     var groupname=getDecodeStr(outparamters.getValue("note_name"));
	     var encryptParam = outparamters.getValue("encryptParam");
	     var new_groupid = outparamters.getValue("new_groupid");
	     var status = outparamters.getValue("now_status");
	     var params = outparamters.getValue("params");
		     //alert("groupid "+groupid+" groupname "+groupname+" status "+status );
	     //alert(new_groupid);
	 	 if(currnode.load)
	     {
			var imgurl;
			if(status=='1')
				imgurl="/images/book1.gif";
			else
				imgurl="/images/book.gif";
			var tmp = new xtreeItem(groupid,groupname,"/selfservice/lawbase/law_maintenance.do?b_query=link&encryptParam="+encryptParam,"mil_body",groupname,imgurl,"/selfservice/lawbase/get_lawbase_strut_tree.jsp?params="+params+"&basetype="+basetype);
		 	currnode.add(tmp);
	     }
	     else
	     	currnode.expand();
	  
	  }   
    	
    	/*******************************
   	 *修改库huaitao
   	 *******************************/
    	function modify_base1()
    	{
    	   var currnode,base_id,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	  
    	   if(base_id=="root")
    	   {
    	     alert("不能修改根节点!");
    	   }
    	   else
    	   {
    	   	 var theArr=new Array(currnode);
    	  	 target_url="/selfservice/lawbase/add_law_base.do?b_query=link&a_base_id="+base_id+"&basetype="+basetype;
    	   	 //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=580,height=354'); 
    	   	 var return_vo= window.showModalDialog(target_url, false, "dialogWidth:520px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
			 var law_base_bean = new Object();
			 //currname=currnode.text;
			 if(return_vo!=null){
				 law_base_bean.name = getEncodeStr(return_vo.name);
				 law_base_bean.description = getEncodeStr(return_vo.description);
				 law_base_bean.check = return_vo.check;
				 law_base_bean.base_id = return_vo.base_id;
				 //currnode.setText(law_base_bean.name);
			 //}
			 //var currnode=Global.selectedItem;
			 var hashvo = new ParameterSet();
			 hashvo.setValue("law_base_bean",law_base_bean);
			 //alert(currnode.uid+" "+currnode.text+" "+currnode.action+" "+currnode.target+" "+currnode.title+" "+currnode.Icon+currnode.xml);
			 //alert("id :"+base_id+" name:"+law_base_bean.name+" des:"+law_base_bean.description+" check:"+law_base_bean.check);
			 var request=new Request({asynchronous:false,onSuccess:modify_base1_ok,functionId:'10400201006'},hashvo);
			 }
	       }
    	}
    	
    	function modify_base1_ok(outparamters)
    	{
    		var currnode=Global.selectedItem;
    		var text = outparamters.getValue("name");
    		var status = outparamters.getValue("status");
    		currnode.setText(getDecodeStr(text));
    		if(status=='1')
    			imgurl="/images/book1.gif";
			else
				imgurl="/images/book.gif";

    		currnode.setIcon(imgurl);
			if(currnode.load){
				while(currnode.childNodes.length){
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.reload(1);
				currnode.loadChildren();
			}
			//不用重新加载了
				//location.reload(true);
			//currnode.openURL();
        		
    	}
    	
    /*******************************
   	 *删除库-huaitao
   	 *******************************/
    	function delete_base1()
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
	  
    	     var theArr=new Array(currnode);
    	     //currname=currnode.text;
    	     var hashvo=new ParameterSet();
     		 //hashvo.setValue("currname",currname);	        
     		 hashvo.setValue("a_base_id",base_id);
     		 hashvo.setValue("basetype",basetype);
     		 parent.frames["mil_body"].location.reload();
			 var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'10400201005'},hashvo);
			 //target_url="/selfservice/lawbase/add_law_base.do?b_delete=link&a_base_id="+base_id;
    	     //parent.location.href=target_url;
    	     }
    	     else
    	    {
    	     return false;
    	    }
    	  }
    	}
    	
		  function delete_ok(outparamters)
		  {
		     var currnode=Global.selectedItem;
		     var preitem=currnode.getPreviousSibling();
		     currnode.remove();
		     //preitem.select();
		     function delay()   
			 {   
				preitem.select();
			 } 
			 window.setTimeout(delay,500);
		  }
    	
    
	/*******************************
   	 *调整显示顺序
   	 *******************************/
   	 function adjust_order() {
              var currnode,base_id,target_url;
    	      currnode=Global.selectedItem;
    	      if(currnode==null)
    	    	    return;
    	      base_id=currnode.uid;  
    	      var theArr=new Array(currnode);
    	      var winFeatures = "dialogHeight:400px; dialogLeft:300px;";
    	      target_url="/selfservice/lawbase/adjust_order.do?b_query=link`base_id="+base_id+"`basetype="+basetype;
    	      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    	      var return_vo= window.showModalDialog(iframe_url, false, "dialogWidth:520px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
    	      if(return_vo=="aaa"){
    	      	document.location.reload();
    	      }
    	      //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=300,width=596,height=354'); 
	}
	/*******************************
   	 *规章制度导出
   	 *******************************/
   	 function fileOutput() {
              var currnode,base_id,target_url;
    	      currnode=Global.selectedItem;
    	      if(currnode==null)
    	    	    return;
    	      base_id=currnode.uid;  
    	      var text = currnode.title;
    	      if(base_id=="root"&&currnode.open&&currnode.childNodes.length<1){
                  alert(text+"<bean:message key='train.check.error.empty'/>");
                  return;
                  
        	  }
    	      var theArr=new Array(currnode);
    	      var winFeatures = "dialogHeight:400px; dialogLeft:300px;"; 
    	      
    	      target_url="/servlet/LawbaseFileOutput?b_query=link&base_id="
    	                 + base_id+"&basetype="+basetype;
    	      window.location = target_url;

    	      //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=300,width=596,height=354'); 
	}
	function fileInput() {
    	currnode=Global.selectedItem;
    		if(currnode==null)
    	   		return;
    		base_id=currnode.uid;
    	parent.mil_body.location = "/selfservice/lawbase/file_input.jsp?b_query=link&base_id="+ base_id+"&basetype="+basetype;
	}
	function role_assign()
	{
	   var currnode,base_id,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   if(base_id=="root")
    	     alert("根目录下不能授权！");
    	   else
    	  {
    	  <%LawBaseForm lawbaseForm = (LawBaseForm) session.getAttribute("lawbaseForm");
            lawbaseForm.setSp_result("");
            session.setAttribute("lawbaseForm", lawbaseForm);%>
    	      target_url="/selfservice/lawbase/add_law_base_role.do?b_add=link`close=0`a_base_id="+base_id;
    	      //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,resizable=no,top=40,left=220,width=550,height=550'); 
    	  	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		  		var return_vo= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		        if(return_vo!=null){
		        }
    	  }
	}
	function delete_role()
	{
	   var currnode,base_id,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   base_id=currnode.uid;
    	   if(base_id=="root")
    	     alert("根目录下不能清除授权！");
    	   else
    	  {
    	      target_url="/selfservice/lawbase/update_law_base_role.do?b_query=link`a_base_id="+base_id;
    	     // newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=500,height=450'); 
    	  	  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		  		var return_vo= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		        if(return_vo!=null){
		        }
    	  }
	}
	function transfer(){
		var currnode=Global.selectedItem;	
	    if(currnode==null)
	    	return;  
	    var id = currnode.uid;
	    if(id=="root")
		{
			alert("请选择要划转的目录，不能选择根节点");
			return false;
		}
		var aa="1";
		if(basetype=="1"){
			aa="5";
		}
		var dw=400,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     	var return_vo= window.showModalDialog("Rangetree.jsp?path=transfer",aa, 
     	   "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:400px; dialogHeight:350px;resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
     	if(!return_vo||return_vo==null)
     		return;
		var hashvo=new ParameterSet();
     	hashvo.setValue("src_base_id",id);
     	hashvo.setValue("target_base_id",return_vo);
     	hashvo.setValue("basetype",basetype);
		var request=new Request({asynchronous:false,onSuccess:transfer_ok,functionId:'10400201019'},hashvo);
		function transfer_ok(outparamters){
			var flag=outparamters.getValue("flag");
			if(flag="ok"){
				var preitem=currnode.getPreviousSibling();
			     currnode.remove();
			     function delay()   
				 {   
					preitem.select();
				 } 
				 window.setTimeout(delay,500);
			}
		}
	}
	
	/************分类指标设置************/
	function setIndex(){
		var currnode,base_id,target_url;
   	    currnode=Global.selectedItem;
   	    if(currnode==null)
   	     	return;
   	    base_id=currnode.uid;
   	    if(base_id=="root"){
    	     alert("不可以设置根目录指标，请选择要设置的目录！");
    	     return false;
   	    }
   	    var dh=290;
		if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh=320;
		}
		var dw=600,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
   	    target_url = "/selfservice/lawbase/setIndex.do?b_search=link&baseId="+base_id+"&basetype="+basetype;
	    var return_vo = window.showModalDialog(target_url,1,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogHeight:"+dh+"px;dialogWidth:600px;center:yes;scroll:no;status:no");
	    if(return_vo == null)
	    	return false;
	    if(return_vo != null){
	    	alert("设置成功！");
	    	var base_id = return_vo.base_id;
	    	parent.mil_body.location = "/selfservice/lawbase/law_maintenance.do?b_query=link&base_id="+base_id;
	    }
	}
	
   </SCRIPT>
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
	<hrms:themes />
	<body style="margin-left:0px;margin-top:0px;margin-right: 0px;padding-right: 0px;">
		<table align="left" width="106%" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
			<tr align="left" class="toolbar">
				<td style="padding-left:5px;width: 100%" valign="middle" align="left" nowrap="nowrap">
					<INPUT type="hidden" id="flg">
					<logic:equal name="lawbaseForm" property="basetype" value="1">
				    <hrms:priv func_id="28020">  
					   &nbsp;<input type="image" name="b_new" style="padding-right:5px;" align="middle" src="/images/add.gif" title="新增" onclick="add_base1();">
                    </hrms:priv> 
				    <hrms:priv func_id="28021">                      
						&nbsp;<input type="image" name="b_delete" style="padding-right:5px;" align="middle" src="/images/del.gif" title="删除" onclick="delete_base1();">
                    </hrms:priv> 
				    <hrms:priv func_id="28022">                 
						&nbsp;<input type="image" name="b_update" style="padding-right:5px;" align="middle" src="/images/edit.gif" title="修改" onclick="modify_base1();">
                    </hrms:priv>
				    <hrms:priv func_id="28023">                     
						&nbsp;<input type="image" name="b_order" style="padding-right:5px;" align="middle" src="/images/sort.gif" title="调整显示顺序" onclick="adjust_order();">
                    </hrms:priv>
                    <hrms:priv func_id="28032">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/add_del.gif" title="目录划转" onclick="transfer()">
                    </hrms:priv>
				    <hrms:priv func_id="28024">                      
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/add_all.gif" title="重建索引" onclick="rebuildIndex()">
                    </hrms:priv>	
				    <hrms:priv func_id="28025">                     				
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/index.gif" title="导出" onclick="fileOutput()">
                    </hrms:priv>	
				    <hrms:priv func_id="28026">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/compute.gif" title="导入" onclick="fileInput()">
                    </hrms:priv>	
                    <hrms:priv func_id="28027">                     					
					<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/role_assign.gif" title="授权" onclick="role_assign()">
                    </hrms:priv>
                    <hrms:priv func_id="28028">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/check.gif" title="清除授权" onclick="delete_role()">
                    </hrms:priv>
                    <hrms:priv func_id="28034">
                   	&nbsp; <input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/sys_config.gif" title="分类指标设置" onclick="setIndex()">
                    </hrms:priv>
                    </logic:equal>	
					<logic:equal name="lawbaseForm" property="basetype" value="4">
				    <hrms:priv func_id="0711010">  
					   &nbsp;<input type="image" name="b_new" style="padding-right:5px;" align="middle" src="/images/add.gif" title="新增" onclick="add_base1();">
                    </hrms:priv> 
				    <hrms:priv func_id="0711011">                      
						&nbsp;<input type="image" name="b_delete" style="padding-right:5px;" align="middle" src="/images/del.gif" title="删除" onclick="delete_base1();">
                    </hrms:priv> 
				    <hrms:priv func_id="0711012">                 
						&nbsp;<input type="image" name="b_update" style="padding-right:5px;" align="middle" src="/images/edit.gif" title="修改" onclick="modify_base1();">
                    </hrms:priv>
				    <hrms:priv func_id="0711013">                     
						&nbsp;<input type="image" name="b_order" style="padding-right:5px;" align="middle" src="/images/sort.gif" title="调整显示顺序" onclick="adjust_order();">
                    </hrms:priv>
				    <hrms:priv func_id="0711014">                      
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/add_all.gif" title="重建索引" onclick="rebuildIndex()">
                    </hrms:priv>	
				    <hrms:priv func_id="0711015">                     				
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/index.gif" title="导出" onclick="fileOutput()">
                    </hrms:priv>	
				    <hrms:priv func_id="0711016">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/compute.gif" title="导入" onclick="fileInput()">
                    </hrms:priv>	
                    <hrms:priv func_id="0711017">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/role_assign.gif" title="授权" onclick="role_assign()">
                    </hrms:priv>
                    <hrms:priv func_id="0711018">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/check.gif" title="清除授权" onclick="delete_role()">&nbsp;&nbsp;&nbsp;
                    </hrms:priv>
                    </logic:equal>
                    <logic:equal name="lawbaseForm" property="basetype" value="5">
				    <hrms:priv func_id="34020">  
					   &nbsp;<input type="image" name="b_new" style="padding-right:5px;" align="middle" src="/images/add.gif" title="新增" onclick="add_base1();">
                    </hrms:priv> 
				    <hrms:priv func_id="34021">                      
						&nbsp;<input type="image" name="b_delete" style="padding-right:5px;" align="middle" src="/images/del.gif" title="删除" onclick="delete_base1();">
                    </hrms:priv> 
				    <hrms:priv func_id="34022">                 
						&nbsp;<input type="image" name="b_update" style="padding-right:5px;" align="middle" src="/images/edit.gif" title="修改" onclick="modify_base1();">
                    </hrms:priv>
				    <hrms:priv func_id="34023">                     
						&nbsp;<input type="image" name="b_order" style="padding-right:5px;" align="middle" src="/images/sort.gif" title="调整显示顺序" onclick="adjust_order();">
                    </hrms:priv>
	                <hrms:priv func_id="34033">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/add_del.gif" title="目录划转" onclick="transfer()">
	                </hrms:priv>
                    <hrms:priv func_id="34024">                      
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/add_all.gif" title="重建索引" onclick="rebuildIndex()">
                    </hrms:priv>
                    <hrms:priv func_id="34025">                     				
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/index.gif" title="导出" onclick="fileOutput()">
                    </hrms:priv>	
				    <hrms:priv func_id="34026">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/compute.gif" title="导入" onclick="fileInput()">
                    </hrms:priv>
                    <hrms:priv func_id="34027">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/role_assign.gif" title="授权" onclick="role_assign()">
                    </hrms:priv>
                    <hrms:priv func_id="34028">                     					
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/check.gif" title="清除授权" onclick="delete_role()">
                    </hrms:priv>
					<hrms:priv func_id="34035">
						&nbsp;<input type="image" name="b_indexf" style="padding-right:5px;" align="middle" src="/images/sys_config.gif" title="分类指标设置" onclick="setIndex()">
					</hrms:priv>
                    </logic:equal>			
				</td>
			</tr>
			<tr>
				<td valign="top">
					<div id="treemenu" ondragend="dragend('law_base_struct','base_id','up_base_id');" ></div>
				</td>
			</tr>
		</table>

	</BODY>
</HTML>

<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile	= "/selfservice/lawbase/get_lawbase_strut_tree.jsp?params=<%=PubFunc.encrypt("base_id=up_base_id")%>&basetype=<bean:write name="lawbaseForm" property="basetype" filter="true"/>";
	var root=new xtreeItem("root",caption,"/selfservice/lawbase/law_maintenance.do?b_query=link&a_base_id=","mil_body",caption,"/images/add_all.gif",m_sXMLFile);
	
	root.setup(document.getElementById("treemenu"));
	//if(newwindow!=null) {
	//    newwindow.focus();
	//}
</SCRIPT>
<script language="javascript">
  initDocument();
  setDrag(true);
</script>