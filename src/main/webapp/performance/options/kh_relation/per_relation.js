var select_objectid;
var isExistjoinedObj='0';//选中的考核对象中是否存在参与分发和结束之间的考核计划的考核对象
function selectRow(object_id,canEdit) {
	if(canEdit=='0')
		window.status=KH_RELATION_INFO3;
	else
		window.status='';
	select_objectid=object_id;
	parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_queryBody=link&objectid=" + select_objectid;
}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
//条件选择考核对象
function conditionselect() {
	var theurl = "/performance/implement/kh_object/condition_select.do?b_query=link`db=Usr`showdb=0";
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(theurl);
	/*if(isIE6()){
		var sql_str = window.showModalDialog(iframe_url, "template_win", "dialogWidth:555px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
	}else{
		var sql_str = window.showModalDialog(iframe_url, "template_win", "dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
	}*/
	
	var config = {
	    width:560,
	    height:450,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"conditionselect_win",config,conditionselect_ok);
}

function conditionselect_ok(sql_str){
	if (sql_str != null) {
		perRelationForm.paramStr.value = sql_str.sql;
		perRelationForm.action = "/performance/options/kh_relation.do?b_selObj=link&opt=conditionselect&a_code=" + a_code;
		perRelationForm.submit();
	}
}
//手工选择考核对象
function handSelect(objectType) {
/*
	var right_fields = "";
	var infor = "1";
	if (objectType == "1") {
		infor = "5";
	}
	var obj_value = handwork_selectObject2(infor, "usr");
	if (obj_value.length > 0) {
		for (var i = 0; i < obj_value.length; i++) {
			right_fields += "/" + obj_value[i];
		}
		perRelationForm.action = "/performance/options/kh_relation.do?b_selObj=link&opt=handselect&right_fields=" + right_fields.substring(1) + "&a_code=" + a_code;
		perRelationForm.submit();
	}
*/
	var right_fields="";	
	var aplanid='';
	var opt = 6;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;

    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+'`callbackFunc=select_ok_han';
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
	//var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=610px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
    
	var config = {
	    width:650,
	    height:480,
	    type:'1',
	    dialogArguments:infos,
	    id:"handSelect_win"
	}
	if(!window.showModalDialog)
		window.dialogArguments = infos;
	
	modalDialog.showModalDialogs(iframe_url,"handSelect_win",config,select_ok_han);
}
function selectWinClose(){
	Ext.getCmp("handSelect_win").close();
}

function select_ok_han(objList){
	var right_fields = "";
	if(objList==null)
		return false;	

	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
		    right_fields+= "/"+objList[i];		   		
		   		
		perRelationForm.action = "/performance/options/kh_relation.do?b_selObj=link&opt=handselect&right_fields=" + right_fields.substring(1) + "&a_code=" + a_code;
		perRelationForm.submit();
	}
}
var objectIDs_bat = "";
//批量设置考核对象类别
function batchSetObjType()
{
	testIsExistjoinedObj();
    if(isExistjoinedObj.length>0)
    {
        alert(KH_RELATION_INFO7.replace("{0}","【"+isExistjoinedObj+"】"));
        return;
    }
	
	var objs=eval("document.perRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{		
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+="`"+objs[i].value;	
			}
		}
		else
		{
			if(objs.checked==true)
				objectIDs+="`"+objs.value;	
		}
	}	
	if(objectIDs=="")
	{
		alert(SELECT_KHOBJ);
			return;
	}
	objectIDs_bat = objectIDs;
	var target_url="/performance/options/kh_relation.do?br_showObjType=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	//var return_vo= window.showModalDialog(iframe_url, "",  
	//   "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:no;status:no"); 
	
	var config = {
	    width:400,
	    height:220,
	    type:'1',
	    id:"batchSetObjType_win"
	}

	modalDialog.showModalDialogs(iframe_url,"batchSetObjType_win",config,batchSetObjType_ok);
	
}

function batchSetObjType_ok(return_vo){
	if(!return_vo)
		return false;	   
    if(return_vo.flag=="true")  
    { 
    	document.perRelationForm.action="/performance/options/kh_relation.do?b_batchSetObjType=link&objTypeId="+return_vo.objTypeId+"&objectIDs="+$URL.encode(objectIDs_bat);
		document.perRelationForm.submit();
   	}
}
//设置考核对象类型
function setType(object_id,mainbody_id,type,obj,oldvalue,selfBodyId) {
	if(type=='body' && object_id!=mainbody_id && obj.value==selfBodyId)
	{
		alert(KH_RELATION_INFO1);
		obj.value=oldvalue;
		return;
	}
	if(type=='body' && object_id==mainbody_id && obj.value!=selfBodyId)	
	{
		alert(KH_RELATION_INFO2);
		obj.value=selfBodyId;
		return;
	}
	var hashvo = new ParameterSet();
	hashvo.setValue("object_id", object_id);
	hashvo.setValue("mainbody_id", mainbody_id);
	hashvo.setValue("type", type);
	hashvo.setValue("typeid", obj.value);
	hashvo.setValue("opt", "6");
	var request = new Request({method:"post", asynchronous:false, onSuccess:setTypeOk,functionId:"9023000003"}, hashvo);
}
function setTypeOk(outparamters){//依照RX的排序顺序进行的更改，如果不需要更改可以去掉
	//window.location.reload();
	//【6031】绩效管理/参数设置/考核关系，制定考核对象类别，每次都会显示刷新页面    jingq upd 2014.12.23
	var object_id = outparamters.getValue("object_id");
	var type_id = outparamters.getValue("typeid");
	parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_queryBody=link&objectid=" + object_id+"&typeid="+type_id;
}
//删除考核对象
function delObjects() {
	testIsExistjoinedObj();
    if(isExistjoinedObj.length>0)
    {
        alert(KH_RELATION_INFO7.replace("{0}","【"+isExistjoinedObj+"】"));
        return;
    }

	var objs = eval("document.perRelationForm.objectID");
	var objectIDs = "";
	if (objs) {
		if (objs.length) {
			for (var i = 0; i < objs.length; i++) {
				if (objs[i].checked == true) {
					objectIDs += "`" + objs[i].value;
				}
			}
		} else {
			if (objs.checked == true) {
				objectIDs += "`" + objs.value;
			}
		}
	}
	if (objectIDs == "") {
		alert(PLEASESELOBJ);
		return;
	}
	if (confirm("确认删除所选考核对象吗？")) {
		document.perRelationForm.action = "/performance/options/kh_relation.do?b_delObj=link" + "&a_code=" + a_code;
		document.perRelationForm.submit();
	}
}
function testIsExistjoinedObj()
{
	isExistjoinedObj='';
	var objs = eval("document.perRelationForm.objectID");
	var objectIDs = "";
	if (objs) {
		if (objs.length) {
			for (var i = 0; i < objs.length; i++) {
				if (objs[i].checked == true) {
					objectIDs += "@" + objs[i].value;
				}
			}
		} else {
			if (objs.checked == true) {
				objectIDs += "@" + objs.value;
			}
		}
	}
	if (objectIDs == "") 
		return;

		var hashvo=new ParameterSet();
		hashvo.setValue("opt", "11");	
		hashvo.setValue("objs",objectIDs);	
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnIsExistjoinedObj,functionId:'9023000003'},hashvo);
}
function returnIsExistjoinedObj(outparamters)
{
	isExistjoinedObj=outparamters.getValue("isExistjoinedObj");	
}
//同步人员顺序
function sortman()
{
	document.perRelationForm.action = "/performance/options/kh_relation.do?b_sortObj=link" + "&a_code=" + a_code;
	document.perRelationForm.submit();
}
function searchKhMainBody()
{
  	perRelationForm.action="/performance/options/kh_relation/mainBodyList.do?b_query=link&code="+$F('bodyType');
	perRelationForm.submit();	
}
//条件选人
 function condiSelPeop()
 {
 	if($F('bodyType')=='all' || $F('bodyType')=='')
 	{
 		alert(SELETE_MAINBODYTYPE);
 		return;
 	}	
	var theurl="/performance/implement/kh_object/condition_select.do?b_query=link`db=Usr`accordByDepartmentFlag=0`callbackfunc=conditionselect_condiSelPeop_ok";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	//var return_vo= window.showModalDialog(iframe_url, 'template_win', 
    //  				"dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
   	
   	var config = {
	    width:550,
	    height:450,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"condiSelPeop_win",config,conditionselect_condiSelPeop_ok);
 }
 
 function conditionselect_condiSelPeop_ok(return_vo){
	if(return_vo==null)
		return ;	   
	if(return_vo.flag=="true") 
	{
		var sql_str = return_vo.sql;
		perRelationForm.paramStr.value=sql_str;
		perRelationForm.action="/performance/options/kh_relation/mainBodyList.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1";     
	    perRelationForm.submit(); 
	}
 }
//手工选人
 function handSelPeop(objectType)
 {   
  	if($F('bodyType')=='all' || $F('bodyType')=='')
 	{
 		alert(SELETE_MAINBODYTYPE);
 		return;
 	}		
 	/*
	 var right_fields="";
	 var infor="1";	
	 var obj_value=handwork_selectObject(infor,"usr")
	 if(obj_value.length>0)
	 {
		 for(var i=0;i<obj_value.length;i++)		  
		   	right_fields+=",'"+obj_value[i]+"'";
		 if(right_fields!=null && right_fields!='')
		 {   	alert(right_fields.substring(1));
			 perRelationForm.paramStr.value=right_fields.substring(1);
			 perRelationForm.action="/performance/options/kh_relation/mainBodyList.do?b_selEmp=link&code="+$F('bodyType');        
		   	 perRelationForm.submit(); 		 
		 }
     }
     */
    var right_fields="";	
	var aplanid='';
	var opt = 9;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;

    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+'`callbackFunc=select_ok_handSelPeop';
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
	//var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=610px;dialogHeight=460px;resizable=yes;scroll=no;status=no;"); 
	if(!window.showModalDialog){
        dialogArguments = infos;
    }

	var config = {
	    width:750,
	    height:480,
	    type:'2',
        dialogArguments:infos,
	    id:"handSelect_win"
	}

	modalDialog.showModalDialogs(iframe_url,'',config,select_ok_handSelPeop);
 }
 
 function select_ok_handSelPeop(objList){
	var right_fields = "";
	
	if(objList==null)
		return false;	

	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
		    right_fields+= ",'"+objList[i]+"'";		  
		     		
		perRelationForm.paramStr.value=right_fields.substring(1);
		perRelationForm.action="/performance/options/kh_relation/mainBodyList.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1&sel=1";    
		perRelationForm.submit(); 		
	}
 }
 function alertInfo(theFlag)
 { 
 	if(theFlag=='1')
 	  alert(MAINBODY_SET);
 }
   function mainBodySel()
  {
  		testIsExistjoinedObj();
		if(isExistjoinedObj.length>0)
		{
            alert(KH_RELATION_INFO7.replace("{0}","【"+isExistjoinedObj+"】"));
			return;
		}
  
  	var objs=eval("document.perRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
			alert(SELECT_KHOBJ);
			return;
	}
  	var target_url="/performance/options/kh_relation/mainBodyList.do?b_tree=link`objIDs="+objectIDs;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	//var return_vo= window.showModalDialog(iframe_url, "mainBodyType", 
	//              "dialogWidth:800px; dialogHeight:600px;resizable:no;center:yes;scroll:no;status:no");	
	
	var config = {
	    width:800,
	    height:600,
	    type:'2',
	    id:"mainBodySel_win"
	}

	modalDialog.showModalDialogs(iframe_url,"mainBodySel_win",config,mainBodySel_ok);
  }
   
  function mainBodySel_ok(return_vo){
	if(!return_vo)
		return;
	if(return_vo.flag=="true")
	{
       parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_queryBody=link&objectid=" + select_objectid;
	}
  }
  function myClose(theFlag,delFlag)
  {
	if(theFlag=='1' || delFlag=='1')
	{
		var thevo=new Object();
		thevo.flag="true";

		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else {
			parent.parent.opener.mainBodySel_ok(thevo);
		}
	}
 }
 function myCancel(theFlag,delFlag)
 {
	if(theFlag=='1' || delFlag=='1')
	{
		var thevo=new Object();
		thevo.flag="true";

		if(window.showModalDialog){
            parent.parent.window.returnValue=thevo;
		}else {
		    if(parent.parent.opener.mainBodySel_ok)
                parent.parent.opener.mainBodySel_ok(thevo);
		}
        parent.window.close();
	}
	if(window.showModalDialog){
        parent.window.close();
    }else{
        parent.parent.window.close();
    }

 }
  function delMainBody()
  {
  	var khObj = $F('khObject');
  	var mainBodyIDs="";
  	var tablevos=document.getElementsByTagName("input");
  	var flag=false;
	for(var i=0;i<tablevos.length;i++)  
	{
	   if(tablevos[i].type=="checkbox" && tablevos[i].checked==true && tablevos[i].name!='selbox')	    
	 	{
	    	var theVal = tablevos[i].value;	    	
	    	mainBodyIDs +=theVal+"@";	
	    }
   	}	
   	if(mainBodyIDs=='')
   	{
   		alert(P_I_INFO4+'!');
   		return;
   	}
	if(confirm(Del_MAINBODYS))
	{	
		perRelationForm.paramStr.value=mainBodyIDs;
		perRelationForm.action="/performance/options/kh_relation/mainBodyList.do?b_del=link&code="+$F('bodyType')+"&khObj="+khObj+"&delFlag=1";
		perRelationForm.submit();
		if(flag)
			alert(NOTDELSELF);
	}	
  }
  function selOneObj()
 {
 	var objs=eval("document.perRelationForm.objectID");
	var objectID="";
	var count = 0;

	if(objs)
	{		
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
				{
					objectID=objs[i].value;	
					count++;
				}					
			}
		}
		else
		{
			if(objs.checked==true)
			{
				objectID=objs.value;	
				count++;
			}
		}
	}
	var thevo=new Object();
	thevo.count=count;
	thevo.objectID=objectID;
	return thevo;
 }
 //复制考核主体
 function copyKhMainBody()
 { 	
 	var obj = selOneObj();
	if(obj.count==1)
	{
		parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_copyBody=link&objectid=" + select_objectid+"&objectID="+obj.objectID;
	}
	else	
		alert(SELECT_ONE);		
 }
  //粘贴考核主体(可以给多个考核对象粘贴考核主体)
 function pasteKhMainBody()
 {
 	testIsExistjoinedObj();
    if(isExistjoinedObj.length>0)
    {
        alert(KH_RELATION_INFO7.replace("{0}","【"+isExistjoinedObj+"】"));
        return;
    }
 
 	var objs=eval("document.perRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
			alert(SELECT_KHOBJ);
			return;
	} 
	parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_pastBody=link&objectid=" + select_objectid+"&objectIDs="+objectIDs;
 }
 //查询
function query() {
	var theurl = "/performance/options/kh_relation/query.do?b_init=link`tablename=per_object_std";
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(theurl);
	//var sql_str = window.showModalDialog(iframe_url, "template_win", "dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	var config = {
	    width:550,
	    height:450,
	    type:'1',
	    id:"query_win"
	}

	modalDialog.showModalDialogs(iframe_url,"query_win",config,query_ok_);
}

function query_ok_(sql_str){
	if (sql_str != null && sql_str != "") {
		perRelationForm.paramStr.value = sql_str;
		perRelationForm.action = "/performance/options/kh_relation.do?b_queryObj=link&opt=query&a_code=" + a_code;
		perRelationForm.submit();
	}
}

function testIsExistjoinedObj2()
{
	isExistjoinedObj='';
	var objs = eval("document.perRelationForm.objectID");
	var objectIDs = "";
	if (objs) {
		if (objs.length) {
			for (var i = 0; i < objs.length; i++) {
				if (objs[i].checked == true) {
					objectIDs += "@" + objs[i].value;
				}
			}
		} else {
			if (objs.checked == true) {
				objectIDs += "@" + objs.value;
			}
		}
	}
	if(objectIDs=="")
	{
		alert(SELECT_KHOBJ);
		return;
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("opt", "11");	
	hashvo.setValue("objs",objectIDs);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnIsExistjoinedObj,functionId:'9023000003'},hashvo);
}



//自动生成考核主体
function autoGetBody(oper)
{
	//个别自动生成走ajax判断下是否选择了有的考核对象考核关系不允许调整
	//批量自动生成是对所有考核对象来说的在后台交易类中判断是否允许调整了
	if(oper=='individual')
	{
		testIsExistjoinedObj2();
        if(isExistjoinedObj.length>0)
        {
            alert(KH_RELATION_INFO7.replace("{0}","【"+isExistjoinedObj+"】"));
            return;
        }
	}	
	var objs=eval("document.perRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_autogetBody=link&objectid=" + select_objectid+"&objectIDs="+objectIDs+"&oper="+oper;
}
function cleanMainBody()
{
	testIsExistjoinedObj();
    if(isExistjoinedObj.length>0)
    {
        alert(KH_RELATION_INFO7.replace("{0}","【"+isExistjoinedObj+"】"));
        return;
    }

	var objs=eval("document.perRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
			alert(SELECT_KHOBJ);
			return;
	} 
	if(confirm("确认清除所选考核对象的主体信息吗？"))	
		parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_clearBody=link&operflag=clearBody&objectid=" + select_objectid+"&objectIDs="+objectIDs;
}
function delMainBody2(objSelected)
{
	var objs=eval("document.perRelationForm.mainbodyID");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
			alert(P_I_INFO4+'!');
			return;
	} 
	if(confirm(P_I_INF14))	
		parent.frames['ril_body2'].location = "/performance/options/kh_relation/mainBodyList.do?b_delBody=link&operflag=delBody&objectid=" + objSelected+"&mainBodyIds="+objectIDs;
}
