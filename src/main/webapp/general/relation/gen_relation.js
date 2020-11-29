var select_objectid;
var isExistjoinedObj='0';//选中的考核对象中是否存在参与分发和结束之间的考核计划的考核对象
function selectRow(object_id,canEdit,selectid) {
	
	select_objectid=object_id;
	select_objectid = getEncodeStr(select_objectid);
	if(window.$URL){
		select_objectid = $URL.encode(select_objectid);
	}
	parent.ril_body2.location = "/general/relation/relationmainbodylist.do?b_queryBody=link&objectid=" + select_objectid+"&selectid="+selectid;
}
	/**指定审批主体    actor_type  =4:业务用户  =1:自助用户*/
   function mainBodySel(relation_id,dbpre,actor_type)
  {
  	var objs=eval("document.genRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{	
		if(objs.length)
		{   
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"#";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"#";	
		}
	}
	
	if(objectIDs=="")
	{
			alert(SELECT_SPOBJ);
			return;
	}
	objectIDs = getEncodeStr(objectIDs);
	var hashvo = new ParameterSet();//xcs 尝试解决人员过多无法通过url地址栏传递的问题。
	hashvo.setValue("flag", "1");
	hashvo.setValue("objectIDs", objectIDs);
	hashvo.setValue("dbpre", dbpre);
	hashvo.setValue("actor_type", actor_type);
	hashvo.setValue("relation_id", relation_id)
	var request = new Request({method:"post", asynchronous:false, onSuccess:mainBodySelReturn,functionId:"1010070045"}, hashvo);
	
  	
  }
   
   function mainBodySelReturn(outparamters){
	   var dbpre=outparamters.getValue("dbpre");
	   var actor_type=outparamters.getValue("actor_type");
	   var relation_id=outparamters.getValue("relation_id");
	   var flag=outparamters.getValue("flag");
	   var target_url="/general/relation/relationmainbodylist.do?b_tree=link`relation_id="+relation_id+"`dbpre="+dbpre+"`+actor_type="+actor_type;
	   if(flag=="2"){
		   var approvalRelation=outparamters.getValue("approvalRelation");
		   target_url="/general/relation/relationmainbodylist.do?b_tree=link`relation_id="+relation_id+"`dbpre="+dbpre+"`+actor_type="+actor_type+"`approvalRelation="+approvalRelation;
	   }
	 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		var return_vo= window.showModalDialog(iframe_url, "mainBodyType", 
		              "dialogWidth:800px; dialogHeight:600px;resizable:no;center:yes;scroll:no;status:no;");
		if(!return_vo)
			return;	   
		if(return_vo.flag=="true")
		{
//			alert(SElMAINBODYINFO);
			window.location = "/general/relation/relationobjectlist.do?b_query=link_query&objectid=" + select_objectid+"&operation=alertcotent";
		}
   }
   
   //田野新定义一个 mainBodySel(relation_id,dbpre,actor_type，approvalRelation)为了传审批标记
   function mainBodySelNew(relation_id,dbpre,actor_type,approvalRelation)
   {
   	var objs=eval("document.genRelationForm.objectID");
 	var objectIDs="";
 	if(objs)
 	{
 		if(objs.length)
 		{
 			for(var i=0;i<objs.length;i++)
 			{
 				if(objs[i].checked==true)
 					objectIDs+=objs[i].value+"#";	
 			}
 		}
 		else
 		{
 			if(objs.checked==true)
 					objectIDs+=objs.value+"#";	
 		}
 	}
 	if(objectIDs=="")
 	{
 			alert(SELECT_KHOBJ);
 			return;
 	}
 	objectIDs = getEncodeStr(objectIDs);
 	var hashvo = new ParameterSet();//xcs 尝试解决人员过多无法通过url地址栏传递的问题。
	hashvo.setValue("flag", "2");
	hashvo.setValue("objectIDs", objectIDs);
	hashvo.setValue("dbpre", dbpre);
	hashvo.setValue("actor_type", actor_type);
	hashvo.setValue("relation_id", relation_id)
	hashvo.setValue("approvalRelation", approvalRelation);
	var request = new Request({method:"post", asynchronous:false, onSuccess:mainBodySelReturn,functionId:"1010070045"}, hashvo);
//   	var target_url="/general/relation/relationmainbodylist.do?b_tree=link`objIDs="+objectIDs+"`relation_id="+relation_id+"`dbpre="+dbpre+"`+actor_type="+actor_type+"`approvalRelation="+approvalRelation;
//  	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
// 	var return_vo= window.showModalDialog(iframe_url, "mainBodyType", 
// 	              "dialogWidth:800px; dialogHeight:600px;resizable:no;center:yes;scroll:no;status:yes");
// 	if(!return_vo)
// 		return;	   
// 	if(return_vo.flag=="true")
// 	{
//// 		alert(SElMAINBODYINFO);
// 		window.location = "/general/relation/relationobjectlist.do?b_query=link&objectid=" + select_objectid+"&operation=alertcotent";
// 	}
   }
//信息同步
function messagesyn(relation_id,actor_type){
    var hashvo = new ParameterSet();
	hashvo.setValue("relation_id", relation_id);
	hashvo.setValue("actor_type", actor_type);
	var request = new Request({method:"post", asynchronous:false, onSuccess:return_right,functionId:"1010070050"}, hashvo);
    
}
function return_right(outparamters){
   var flag = outparamters.getValue("flag");
   if(flag==1){
    window.location = "/general/relation/relationobjectlist.do?b_query=link&objectid=" + select_objectid+"&operation=alertcotent";
   }else{
     alert("信息同步失败");
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
	var request = new Request({method:"post", asynchronous:false, functionId:"9023000003"}, hashvo);
}
function searchKhMainBody()
{
  	genRelationForm.action="/general/relation/relationmainbodylist.do?b_query=link&code="+$F('bodyType');
	genRelationForm.submit();	
}

//条件选人
 function condiSelPeop(db)
 {
 	if($F('bodyType')=='all' || $F('bodyType')=='')
 	{
 		alert(SELETE_MAINBODYTYPE);
 		return;
 	}	
	var theurl="/performance/implement/kh_object/condition_select.do?b_query=link`db="+db+"`accordByDepartmentFlag=0`datebase=1";//datebase=1  条件选人加上人员库
	if(window.$URL){
		theurl = $URL.encode(theurl);
    }
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no;");
 	if(return_vo==null){
 		return ;
 	}
			   
	if(return_vo.flag=="true") 
	{
		var sql_str = return_vo.sql;
		var db=return_vo.dbpre;
		genRelationForm.paramStr.value=sql_str;
		genRelationForm.action="/general/relation/relationmainbodylist.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1&db="+db;     
	    genRelationForm.submit(); 
	}	
 }
//手工选人
 function handSelPeop(flag,objectType,db)
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
			 genRelationForm.paramStr.value=right_fields.substring(1);
			 genRelationForm.action="/general/relation/relationobjectlist.do?b_selEmp=link&code="+$F('bodyType');        
		   	 genRelationForm.submit(); 		 
		 }
     }
     */
     if(flag=="1")
     {
	    var right_fields="";	
		var aplanid='';
		var opt = 9;
		var infos=new Array();
		infos[0]=aplanid;
		infos[1]=opt;
		//defaultradiolevel:0=all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现 设置审批主体时，手工选人 只能选人
	    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`db="+db+"`flag0=1`defaultradiolevel=3";//flag0=1,支持按认证人员库多库选择审批主体
	    if(window.$URL){
	    	strurl = $URL.encode(strurl);
	    }
		var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl;
		var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=610px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	    if(objList==null)
			return false;	
	   
		if(objList.length>0)
		{
			for(var i=0;i<objList.length;i++)
			    //right_fields+= ",'"+objList[i]+"'";	
				right_fields+="`"+objList[i];
			     		
			genRelationForm.paramStr.value=right_fields.substring(1);
			genRelationForm.action="/general/relation/relationmainbodylist.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1";    
			genRelationForm.submit(); 		
		}
     
    }
    else
    {
    	var return_vo=select_user_dialog('1','1','1');
	 	if(return_vo)
	 	{
	 		var content = getEncodeStr(return_vo.content);	
	 		if(window.$URL){
	 			content = $URL.encode(content);
		    }
	 		genRelationForm.action="/general/relation/relationmainbodylist.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1&content="+content;    
			genRelationForm.submit(); 	
	 	}
    }       
 }
 //田野添加手工选人处理方法
 function handSelPeopNew(flag,objectType,db,approvalRelation)
 {   
  	if($F('bodyType')=='all' || $F('bodyType')=='')
 	{
 		alert(SELETE_MAINBODYTYPE);
 		return;
 	}		
 
     if(flag=="1"){
    var right_fields="";	
	var aplanid='';
	var opt = 9;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;
    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`db="+db+"`approvalRelation="+approvalRelation;
    if(window.$URL){
    	strurl = $URL.encode(strurl);
    }
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl;
	//手动选人后返回值处理
	var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
    if(objList==null)
		return false;	
	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
		    right_fields+= ",'"+objList[i]+"'";		  
		genRelationForm.paramStr.value=right_fields.substring(1);
		genRelationForm.action="/general/relation/relationmainbodylist.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1";    
		genRelationForm.submit(); 		
	}
     
    }else{
    
    var return_vo=select_user_dialog('1','1','1');
	 	if(return_vo)
	 	{
	 	
	 		var content = getEncodeStr(return_vo.content);
	 		if(window.$URL){
	 			content = $URL.encode(content);
	 		}
	 		genRelationForm.action="/general/relation/relationmainbodylist.do?b_selEmp=link&code="+$F('bodyType')+"&flag=1&content="+content;    
			genRelationForm.submit(); 	
	 	}
    }
 }

  function myClose(theFlag,delFlag)
  {
	if(theFlag=='1' || delFlag=='1')
	{
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
	}
 }
 function myCancel(theFlag,delFlag)
 {
	if(theFlag=='1' || delFlag=='1')
	{
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
	}
	window.close();
 }

  function selOneObj()
 {
 	var objs=eval("document.genRelationForm.objectID");
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
 function copyGenMainBody()
 { 	
 	var obj = selOneObj();
	if(obj.count==1)
	{
		var objectID = obj.objectID;
		objectID = getEncodeStr(objectID);
		if(window.$URL){
			objectID = $URL.encode(objectID);
		}
		parent.ril_body2.location = "/general/relation/relationobjectlist.do?b_copyBody=link&objectid=" + select_objectid+"&objectID="+objectID+"&operation=alertcotent";
	}
	else	
		alert(SELECT_GEN_ONE);		
 }
  //粘贴考核主体(可以给多个考核对象粘贴考核主体)
 function pasteGenMainBody()
 {
 	var objs=eval("document.genRelationForm.objectID");
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
			alert(SELECT_SPOBJ);
			return;
	} 
	objectIDs = getEncodeStr(objectIDs);
	if(window.$URL){
		objectIDs = $URL.encode(objectIDs);
	}
	window.location = "/general/relation/relationobjectlist.do?b_pastBody=link&objectid=" + select_objectid+"&objectIDs="+objectIDs+"&operation=alertcotent";
 }

//自动生成审批关系对象
function autoGetBody(oper)
{
	//批量自动生成是对所有审批对象来说的在后台交易类中判断是否允许调整了
	var objs=eval("document.genRelationForm.objectID");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"#";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"#";	
		}
	}
	if(oper=='individual')
	{
		if(objectIDs=="")
		{
			alert(SELECT_SPOBJ);
			return;
		}
	}	
	objectIDs = getEncodeStr(objectIDs);
	window.location = "/general/relation/relationobjectlist.do?b_autogetBody=link&objectid=" + select_objectid+"&objectIDs="+objectIDs+"&oper="+oper+"&operation=alertcotent";
}
function cleanMainBody()
{
	var objs=eval("document.genRelationForm.objectID");
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
			alert(SELECT_SPOBJ);
			return;
	} 
	objectIDs = getEncodeStr(objectIDs);
	if(window.$URL){
		objectIDs = $URL.encode(objectIDs);
	}
	if(confirm("确认清除所选审批对象的审批关系吗？"))	
		window.location = "/general/relation/relationobjectlist.do?b_clearBody=link&operflag=clearBody&objectid=" + select_objectid+"&objectIDs="+objectIDs+"&operation=alertcotent";
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
	    	mainBodyIDs +=theVal+"`";	
	    }
   	}	
   	if(mainBodyIDs=='')
   	{
   		alert(P_I_INFO4+'!');
   		return;
   	}
	if(confirm(Del_MAINBODYS))
	{
		if(window.$URL){
			khObj = $URL.encode(khObj);
		}
		genRelationForm.paramStr.value=mainBodyIDs;
		genRelationForm.action="/general/relation/relationmainbodylist.do?b_del=link&code="+$F('bodyType')+"&khObj="+khObj+"&delFlag=1";
		genRelationForm.submit();
		if(flag)
			alert(NOTDELSELF);
	}	
  }
function delMainBody2(objSelected,codeset,code,operate)
{
	var objs=eval("document.genRelationForm.mainbodyID");
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
			alert(P_I_GEN_INFO4+'!');
			return;
	} 
	
	if(confirm("确认执行删除操作？"))
	{
		document.genRelationForm.isDelMainbody.value="1";
		objectIDs = getEncodeStr(objectIDs);
		objSelected = getEncodeStr(objSelected);
		if(window.$URL){
			objectIDs = $URL.encode(objectIDs);
			objSelected = $URL.encode(objSelected);
		}
		genRelationForm.action = "/general/relation/relationmainbodylist.do?b_delBody=link&operflag=delBody&objectid=" + objSelected + "&mainBodyIds="+objectIDs;
		genRelationForm.submit();
		//parent.ril_body1.location.reload();这种方法在程序执行了审批关系的定义中方法并且改变了ril_body1的请求路径后会出错
		parent.ril_body1.location="/general/relation/relationobjectlist.do?b_query=link&codeset="+codeset+"&code="+code+"&operate="+operate;
	}
}
