<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script LANGUAGE=javascript src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript">
<!--
var opttype='-1';
var beforeitemid="-1";
var beforepointid="-1";
var beforetype=0;//=1是项目=2是指标
var isHaveItem = "${khTemplateForm.isHaveItem}";//模板是否已经有项目=0有项目=1还没有项目 
var isUsed="${khTemplateForm.isUsed}";//模板是否被使用=0已经使用=1未使用
var isVisible="${khTemplateForm.isVisible}";
var score_str="${khTemplateForm.score_str}";
var t_type="${khTemplateForm.t_type}";
var beforeTaskContent='';
function editTask(theObj)
{
	if(ltrim(rtrim(theObj.value))=='')
	{
		alert('任务内容不能为空！');
		theObj.value=beforeTaskContent;
		return;
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${implementForm.planid}');
	hashvo.setValue("p0401_value",theObj.id.substring(2));
	hashvo.setValue("objCode",'${implementForm.objCode}');
	hashvo.setValue("opt",'20');
	hashvo.setValue("theValue",getEncodeStr(theObj.value));
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);	
}
function saveTargetPointScore(theObj)
{		
	var arrayVal = theObj.id.split(':');
	if(arrayVal[1]=='p0419' && parseFloat(theObj.value)>100)
	{
		alert('完成进度不能大于100！');
		theObj.value="0";
		theObj.focus();
		return;
	}		

	// 数字字段(fieldType=N)是否输入了其他字符 lium
	if (theObj.getAttribute("fieldType") === "N" && !checkIsNum2(theObj.value)) {
		alert("请输入数字");
		theObj.select();
		return;
	}
	
	// 判断字符是否超过长度 lium
	var m = theObj.maxLength || 10;
	if (IsOverStrLength(theObj.value, m)) {
		alert("输入长度不能超过" + m + "个字符\n注：中文占两个字符，英文占一个字符");
		theObj.select();
		return;
	}

    var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${implementForm.planid}');
	hashvo.setValue("p0401_value",arrayVal[0]);
	hashvo.setValue("targetPointCol",arrayVal[1]);
	hashvo.setValue("objCode",'${implementForm.objCode}');
	hashvo.setValue("opt",'21');
	hashvo.setValue("theValue",getEncodeStr(theObj.value));
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);	
	
}
function saveCodeValue(field)
{
	var hiddenobj;
	var hiddenInputs=document.getElementsByName(field+'.value');    
    if(hiddenInputs!=null)    
    	hiddenobj=hiddenInputs[0];  

	var arrayVal = field.split(':');
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${implementForm.planid}');
	hashvo.setValue("p0401_value",arrayVal[0]);
	hashvo.setValue("targetPointCol",arrayVal[1]);
	hashvo.setValue("objCode",'${implementForm.objCode}');
	hashvo.setValue("opt",'21');
	hashvo.setValue("theValue",hiddenobj.value);
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
function updateBigField(theObj,theVal)
{
	window.updateBigObj = {};
	var arrayVal = theObj.id.split(':');
	window.updateBigObj.arrayVal = arrayVal;
	var strurl="/performance/implement/performanceImplement/targetCardSet.do?b_updateBigField=link`p0401_value="+arrayVal[0]+"`targetPointCol="+arrayVal[1];
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var width = 490;
	var height = 440;
	if(!window.showModalDialog){
		height = 465;
	}
	var config = {
		width:width,
		height:height,
		title:'修改',
		id:'updateBigFieldWin'
	}

	modalDialog.showModalDialogs(iframe_url,"updateBigFieldWin",config,updateBigField_ok);

}

function updateBigField_ok (return_vo){
	if(return_vo!=null && return_vo.flag==true)
	{
		var theVal=return_vo.theValue;
		var hashvo=new ParameterSet();
		hashvo.setValue("planid",'${implementForm.planid}');
		hashvo.setValue("p0401_value",window.updateBigObj.arrayVal[0]);
		hashvo.setValue("targetPointCol",window.updateBigObj.arrayVal[1]);
		hashvo.setValue("objCode",'${implementForm.objCode}');
		hashvo.setValue("opt",'21');
		hashvo.setValue("theValue",theVal);
		var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	}
}
function saveTargetPointScore_Date(theObj,theId)
{
	var theVal=theObj.value;
	var arrayVal = theId.split(':');
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${implementForm.planid}');
	hashvo.setValue("p0401_value",arrayVal[0]);
	hashvo.setValue("targetPointCol",arrayVal[1]);
	hashvo.setValue("objCode",'${implementForm.objCode}');
	hashvo.setValue("opt",'21');
	hashvo.setValue("theValue",theVal);
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
function newtask()
{
	if(beforeitemid=="-1")
 	{
 		alert(QXZGXHXMMC+"!")
 		return;
 	}
	var target_url="/performance/implement/performanceImplement/targetCardSet.do?b_addNewTask=link`beforeitemid="+beforeitemid;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

 	var config = {
 	    width:400,
        height:200,
        type:'2'
    }
 	modalDialog.showModalDialogs(iframe_url,"newtask_win",config,newtask_ok);
}
function newtask_ok(return_vo){
    if(return_vo!=null && return_vo.flag=="true")
    {
        implementForm.action="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid=${implementForm.objCode}";
        implementForm.submit();

        //var taskcontent = return_vo.taskcontent;
        //implementForm.action="/performance/implement/performanceImplement/targetCardSet.do?b_addTask=link&planid=${implementForm.planid}&codeid=${implementForm.objCode}&taskcontent="+getEncodeStr(taskcontent)+"&itemid="+beforeitemid;
        //implementForm.submit();
    }
}
function check()
{
	var strurl="/performance/implement/performanceImplement/targetCardSet.do?b_test=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
    var width=490;
    if (!window.showModalDialog){
        width=500;
    }

    var config={
	    width:width,
        height:440,
        title:"目标卡校验",
        id:'targetCardCheck',
		type:'1'

    }
	modalDialog.showModalDialogs(iframe_url,"checkWin",config)
}
function beforeEditTask(theObj)
{
	beforeTaskContent=theObj.value;
}
function changeBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="#98C2E8";
  
}
function goBackBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="white";
}
function changeColor(id,type,itemid)//type=1是项目，=2是指标
{
     if(trim(beforeitemid).length>0&&document.getElementById(beforeitemid)!=null)
     {
          document.getElementById(beforeitemid).className='RecordRow';
     }
     if(trim(beforepointid).length>0&&document.getElementById(beforepointid)!=null)
     {
          document.getElementById(beforepointid).className='RecordRow';
     }
     beforetype=type;
     if(type=='1')
     {
     	beforeitemid=id;
     	beforepointid="-1";
     }        
     else
     {
   		 beforepointid=id;
   		 beforeitemid=itemid;
     }
        
     var e = document.getElementById(id);
     opttype=type;
//      e.className="selectedBackGroud";
     e.style.backgroundColor="#FFF8D2";
}
function delPoint()
{
	if(beforetype==2 && beforepointid!=-1)
	{
		if(confirm(OBJECTCARDINFO6+'?'))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("planid",'${implementForm.planid}');
			hashvo.setValue("p0401_value",beforepointid);
			hashvo.setValue("item_id",beforeitemid);
			hashvo.setValue("objCode",'${implementForm.objCode}');
			hashvo.setValue("opt",'23');
			var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'9023000003'},hashvo);	
		}
	}else
		alert('请选择个性项目对应的指标或者任务！');
}
function refreshPage(outparameters)
{
    implementForm.action="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid=${implementForm.objCode}";
	implementForm.submit();
}
 //引入绩效指标
function importPerPoint(planid,object_type,object_id)
{
	if(beforeitemid=="-1")
 	{
 		alert(QXZGXHXMMC+"!")
 		return;
 	}

 	var infos=new Array();
	infos[0]=object_id;
	infos[1]=object_type;
	infos[2]=planid;
	var thecodeurl="/performance/objectiveManage/objectiveCard.do?br_selectpoint=query"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 

    var dialogWidth=430;
    var dialogHeight=380;
    if (!window.showModalDialog){
        window.dialogArguments = infos;
    }
    var config = {
        width:dialogWidth,
        height:dialogHeight,
        dialogArguments:infos,
        type:"1",
        title:'引入绩效指标',
        id:"perpoint_win"
    }
    modalDialog.showModalDialogs(iframe_url,infos, config,importPerPoint_ok);

}
function importPerPoint_closeWin(){
    Ext.getCmp('perpoint_win').close();
}
function importPerPoint_ok(points){
    if(points==undefined)
        points="";
    if(points.length>0)
    {
        implementForm.importPoint_value.value=points;
        implementForm.action="/performance/implement/performanceImplement/targetCardSet.do?b_importPoint=link&planid=${implementForm.planid}&codeid=${implementForm.objCode}&itemid="+beforeitemid;
        implementForm.submit();
    }
}
// type=0 同级项目，=1下级项目，2插入项目
function addItem(type)
{
   Element.hide('date_panel');
   if(isUsed=='0'&&type!='3')
   {
      alert("模板已被使用，不能修改");
      return;
   }
   if(type=='3')
   {
      if(opttype!='1')
      {
         alert("请选择考核项目后进行操作");
         return;
      }
    var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?b_rank=query`itemid="+beforeitemid+"`temp_id=${khTemplateForm.templateid}"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var points= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:500px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");	
   }
   else
   {
      if(beforeitemid=='-1'&&beforepointid=='-1'&&isHaveItem=='0')
      {
    
        alert("请选择考核项目或者考核指标后进行操作");
       return;
      }
      var newName=prompt("请输入考核项目:","");
      if(newName==null)
          return;
      if(trim(newName)=='')
      {
         alert("项目名称不能为空！");
         return;
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("name",getEncodeStr(newName));
      hashvo.setValue("isHaveItem","${khTemplateForm.isHaveItem}");
      hashvo.setValue("opt",type);
      hashvo.setValue("type",beforetype);
      hashvo.setValue("itemid",beforeitemid);
      hashvo.setValue("pointid",beforepointid);
      hashvo.setValue("id","${khTemplateForm.templateid}");
      hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
      var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'9021001032'},hashvo); 
   }
}
function additem_ok(outparameters)
{
   var subsys_id =outparameters.getValue("subsys_id");
   var templateid = outparameters.getValue("templateid");
   khTemplateForm.action="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+templateid+"&subsys_id="+subsys_id+"&isVisible="+isVisible;
   khTemplateForm.submit();
}
function del()
{
     Element.hide('date_panel');
    if(isUsed=='0')
    {
      alert("模板已被使用，不能修改");
      return;
    }
    if(beforeitemid=='-1'&&beforepointid=='-1'&&isHaveItem=='0')
    {
       alert("请选择考核项目或者考核指标后进行操作");
       return;
    }
     var flag=false;
     if(beforetype=='1')
     {
        if(confirm("确认要删除模板项目\""+document.getElementById(beforeitemid).innerHTML+"\"及其包含的指标吗？")){
            flag=true;
        }
     }
     else if(beforetype=='2')
     {
       if(confirm("确认要删除指标\""+document.getElementById(beforepointid).innerHTML+"\"吗？")){      
           flag=true;
        }
     }
     if(flag)
     {
         var hashvo=new ParameterSet();
         hashvo.setValue("type",beforetype);
         hashvo.setValue("itemid",beforeitemid);
         hashvo.setValue("pointid",beforepointid);
         hashvo.setValue("id","${khTemplateForm.templateid}");
         hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
         var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'9021001034'},hashvo); 
     }   
}
//type=1增加=2插入
function addPoint(type)
{
    Element.hide('date_panel');
    if(isUsed=='0')
    {
      alert("模板已被使用，不能修改");
      return;
    }
    if(type=='1'&&beforeitemid=='-1')
    {
      alert("请选择考核项目！");
      return;
    }
    if((beforetype=='0'||beforetype=='1')&&type=='2')
    {
        alert("请选择指标，以确定要插入指标的位置！");
        return;
    }
    var infos=new Array();
	infos[0]="${khTemplateForm.templateid}";
	infos[1]="${khTemplateForm.subsys_id}";
	var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?br_selectpoint=query"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
	var points= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");	
    if(points)
    {		        		
			
	if(points=='undefined'||points=='')
	{			
	     return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("beforetype",beforetype);
    hashvo.setValue("type",type);
    hashvo.setValue("itemid",beforeitemid);
    hashvo.setValue("point",beforepointid);
    hashvo.setValue("id","${khTemplateForm.templateid}");
    hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
    hashvo.setValue("pointids",points);
    var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'9021001033'},hashvo); 
   }
	
}
function save(str,status,item_rank)
{
  var s_str="";
  var r_str="";
  var arr = str.split(",");
  for(var i=0;i<arr.length;i++)
  {
    if(arr[i]==null||trim(arr[i]).length==0)
     continue;
     if(document.getElementById("s_"+arr[i])!=null)
     {
         s_str+=","+arr[i]+"/"+document.getElementById("s_"+arr[i]).value;
     }
     if(status=='1')
     {
         if(document.getElementById("r_"+arr[i])!=null)
         {
             r_str+=","+arr[i]+"/"+document.getElementById("r_"+arr[i]).value;
        }
     }
  }
  var i_s_str="";
  var i_r_str="";
  if(trim(item_rank).length>0)
  {
        var i_arr=item_rank.split(",");
        for(var j=0;j<i_arr.length;j++)
        {
            if(i_arr[j]==null||trim(i_arr[j]).length==0)
               continue;
            if(document.getElementById("si_"+i_arr[j])!=null)
            {
                i_s_str+=","+i_arr[j]+"/"+document.getElementById("si_"+i_arr[j]).value;
            }
             if(status=='1')
             {
                 if(document.getElementById("ri_"+i_arr[j])!=null)
                 {
                    i_r_str+=","+i_arr[j]+"/"+document.getElementById("ri_"+i_arr[j]).value;
                 }
             }
        }
  }
  var hashvo=new ParameterSet();
  hashvo.setValue("s_str",s_str);
  hashvo.setValue("r_str",r_str);
  hashvo.setValue("i_s_str",i_s_str);
  hashvo.setValue("i_r_str",i_r_str);
  hashvo.setValue("status",status);
  hashvo.setValue("id","${khTemplateForm.templateid}");
  hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
  var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9021001037'},hashvo); 
}
function save_ok(outparameters)
{  
   var subsys_id =outparameters.getValue("subsys_id");
   var templateid = outparameters.getValue("templateid");
   var msg = getDecodeStr(outparameters.getValue("msg"));
   if(msg=='ok')
   {
     khTemplateForm.action="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+templateid+"&subsys_id="+subsys_id+"&isVisible="+isVisible;
     khTemplateForm.submit();
   } else
   {
      alert(msg);
      return;
   }
   
}
function checkKeyCode()
{
   var code=window.event.keyCode;
    var ret=true;
    if(code==8||code==46||code==9||code==190||code==110||code==13)
    {
        if(code==13)
        window.event.keyCode=9;
    }
   else if(96<=code&&code<=105)
   {
      
   }else if(48<=code&&code<=57)
   {
   }
   else
   { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else if(window.event.shiftKey&&code==189)
        {
           window.event.returnValue=false;
        }
        else if(code==189||code==109)
        {
        }
        else
        {
           window.event.returnValue=false;
        }
     
   }   
}
var beforeValue;
function saveBeforeValue(obj)
{
  beforeValue=obj.value;
}
function checkValue(obj,type,p0401)
{
	if(obj.value.length>0)
  	{
  		if(!checkIsNum(obj.value))
  		{
  			alert('请输入数值！');
  			obj.value='0';
  			obj.focus();
  		}
  	}	
    if(type=='rank' && parseInt(obj.value)>1)
    {
    	alert("权重值无效!");
        obj.value=beforeValue;
        return;
    }
     
    var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${implementForm.planid}');
	hashvo.setValue("type",type);
	hashvo.setValue("p0401_value",p0401);
	hashvo.setValue("objCode",'${implementForm.objCode}');
	hashvo.setValue("opt",'22');
	hashvo.setValue("theValue",obj.value);
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);	     
}
function gaibian(itemid,type)
{
   var TDElement=document.getElementById(itemid);
   if(document.getElementById("T_"+itemid)==null)
   {
       TDElement.innerHTML="<input type='text' onblur='savedesc(\""+itemid+"\");' name='tt' id='T_"+itemid+"' value='"+TDElement.innerHTML+"'/>";
       document.getElementById("T_"+itemid).focus();
   }
       
}
function savedesc(itemid)
{
     
      var TDElement=document.getElementById(itemid);
      var InputElement=document.getElementById("T_"+itemid);
       if(getEncodeStr(InputElement.value).length==0)
      {
         alert("项目名称不能为空！");
         return;
      }
      //TDElement.innerHTML=InputElement.value;
      var hashvo=new ParameterSet();
    hashvo.setValue("itemid",itemid);
    hashvo.setValue("itemdesc",getEncodeStr(InputElement.value));
    var request=new Request({asynchronous:false,onSuccess:edit_ok,functionId:'9021001042'},hashvo); 
}
function edit_ok(outparameters)
{
   var itemid=outparameters.getValue("itemid");
   var itemdesc=outparameters.getValue("itemdesc");
  var TDElement=document.getElementById(itemid);
   TDElement.innerHTML=getDecodeStr(itemdesc);
}
function clearValue(value,id)
{
    if(value=='0')
    {
       document.getElementById(id).value='';
    }
}
function returnFlowPhoto()
{
   khTemplateForm.action="/general/tipwizard/tipwizard.do?br_performance=link";
   khTemplateForm.target="il_body";
   khTemplateForm.submit();
}
function buttonOK(name,id)
{
    window.returnValue=name+","+id;
    window.close();
}

function IsDigit(obj) 
{
	if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
	{
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
			return false;
		if((event.keyCode == 46) && (values.length==0))//首位是.
			return false;	
		return true;
	}
	return false;	
}
//-->
</script>
<style>

.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
 }   

.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.trDeep_self {  
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	background-color: #DDEAFE; 
	}
.Input_self{                                                                    
  font-size:   12px;                                              
  font-weight:   bold;                                                          
  background-color:   #FFFFFF;         
  letter-spacing:   1px;                      
  text-align:   right;                                                      
  width:   90%;                                    
  border:   1px   solid   #94B6E6;           
  cursor:   hand;                                     
  }   
 .TEXT_NB {
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM:  medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
}
.Input_self2{                                                                                                                           
  background-color:   #FFFFFF;         
  letter-spacing:   1px;                                                                                                           
  border:   1px   solid   #94B6E6;           
  cursor:   hand;                                     
  } 

div#tbl-container {	
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
</style>
<hrms:themes></hrms:themes>
<html:form action="/performance/implement/performanceImplement/targetCardSet"> 
<script language='javascript' >
var theHeight = document.body.clientHeight-24;
var theWidth = document.body.clientWidth-20;
var planstatus='${implementForm.planStatus}';
if(planstatus=='3' || planstatus=='5' || planstatus=='8')
	theHeight=theHeight-100;
	document.write("<div id=\"tbl-container\"  style='height:"+theHeight+"px;width:"+theWidth+"px;' >");
	</script>	
					${implementForm.targetCardHtml}
<script language='javascript' >
		document.write("</div>");
		
		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}		
</script>
<html:hidden name="implementForm" property="importPoint_value"/>
</html:form>