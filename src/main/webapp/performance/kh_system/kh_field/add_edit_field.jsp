<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.kh_system.kh_field.KhFieldForm,
				 com.hrms.hjsj.sys.VersionControl,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient" %>

<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	/*int versionFlag = 1;
	if (userView != null)
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版*/
	//是否有目标管理的功能
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	boolean flag = false;
	if(lockclient!=null)
	{
		if(lockclient.isHaveBM(29)){
			flag=true;
		}
	}
    // 定量指标计算公式选项卡添加版本控制  JinChunhai 2011.08.17
	VersionControl version = new VersionControl();
	
	KhFieldForm khFieldForm=(KhFieldForm)session.getAttribute("khFieldForm");
	String subsys_id = khFieldForm.getSubsys_id();	//  =20 培训模块 =33 绩效模块 =32 招聘模块 =35 能力素质
			
	String nameDesc="名称";
	String scaleDesc="标度";
	if(subsys_id.equalsIgnoreCase("35"))
	{
		nameDesc="基本属性";
		scaleDesc="能力标度";		
	}
		
%>

<html>
<head>
    <script language="JavaScript"src="../../../js/showModalDialog.js"></script>
</head>
<body onload="toNext();checkvisibleButton();">
<style>
<!--
.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
-->
.smallbutton{
    width:25px;
}

.paddinginput{
    padding-right:30px;
}
</style>
<script type="text/javascript">

function visibleBox(type)
{
     var obj = document.getElementById("box");
     if(obj!=null)
     {
	     if(type=='0')
	     {
	       	obj.style.display="none";
	     }
	     else
	     {
	        obj.style.display="block";
	     }
     }
}
function checkvisibleButton()
{
		var obj = document.getElementById("st");
		visibleButton(obj);
}
function visibleButton(objec)
{
   	 var obj = document.getElementById("bbuu");
   	 if(obj!=null)
     {
	     if(!objec.checked)
	     {
	       obj.style.display="none";
	     }
	     else
	     {
	        obj.style.display="block";
	     }
     }
}
function addRows()
{
   	var tab=document.getElementById("tab");
	var i=tab.rows.length;

	var v="";
	var newRow=tab.insertRow(tab.rows.length);
 	newRow.id="cc"+i;
	var myNewCell = newRow.insertCell(0);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML ="<td width='6%' align='center' class='RecordRow' nowrap> <input type='checkbox'  name='bb'  value='"+i+"'></td>";
	myNewCell = newRow.insertCell(1);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML="<td width='10%' align='center' class='RecordRow' id='g_"+i+"' nowrap>"+i+"</td>";
	myNewCell = newRow.insertCell(2);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML="<td width='10%' align='center' class='RecordRow' nowrap><input style='text-align:left;width:35px;' type='text' class='inputtext' size='3' maxlength='1' onkeydown='ctrl_key(this);' id='a"+(i+1)+"_2' name='aa' value=''/></td>";
	myNewCell = newRow.insertCell(3);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML=" <td width='40%' align='center' class='RecordRow' nowrap><input style='text-align:left;width:140px;'  type='text' class='inputtext' size='17' onkeydown='ctrl_key(this);' id='a"+(i+1)+"_3' name='ff' value=''/></td>";
	myNewCell = newRow.insertCell(4);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML="<td width='10%' align='center' class='RecordRow'  nowrap><input style='text-align:right;width:50px;padding-right:3px;' type='text' class='inputtext' maxlength='10' onkeydown='ctrl_key(this);' size='4' id='a"+(i+1)+"_4' name='cc' value=''/></td>";
	myNewCell = newRow.insertCell(5);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML="<td width='10%' align='center' class='RecordRow' nowrap><input  style='text-align:right;width:50px;padding-right:3px;' type='text' class='inputtext' maxlength='10' onkeydown='ctrl_key(this);'  size='7' id='a"+(i+1)+"_5' name='dd' value=''/></td>";
	myNewCell = newRow.insertCell(6);
	myNewCell.className = "RecordRow";
	myNewCell.align="center";
	myNewCell.innerHTML="<td width='10%' align='center' class='RecordRow' nowrap><input style='text-align:right;width:50px;padding-right:3px;' type='text' class='inputtext' maxlength='10' size='7' onkeydown='ctrl_key(this);' name='ee' id='a"+(i+1)+"_6' value=''/></td>";
	var obj=document.getElementById("cc"+i);

	if((i-1)%2==0)
	{
		obj.style.backgroundColor=""; //#F3F5FC
 	}else
 	{
 		obj.style.backgroundColor="";//#DDEAFE
 	}
}
function  deleteRow()
{
   	var ids = "";
   	var num=0;
   	var obj=document.getElementsByName("bb");
   	for(var i=0;i<obj.length;i++)
   	{
     	if(obj[i].checked)
     	{
       		ids+=","+obj[i].value;
       		num++;
     	}
   	}
   	if(num==0)
   	{
     	alert("请选择要删除的标度！");
     	return;
   	}
   	if(confirm("确认删除标度？"))
    {
       var tab=document.getElementById("tab");
       var i=tab.rows.length;
       var temp=ids.substring(1).split(",");
       for(var j=0;j<temp.length;j++)
       {
           tab.deleteRow(temp[j]-j);
           var bj=document.getElementsByName("bb");
           for(var s=0;s<bj.length;s++)
           {
               if((parseInt(bj[s].value)+j)>=parseInt(temp[j]))
                {
                    bj[s].value=bj[s].value-1;
                }
            }
       }
       var ta=document.getElementById("tab");
        var tr = ta.rows;
       var h=0;
       for(var t=1;t<tr.length;t++)
       {
          var tds = tr[t].cells;
          if(tds[1]!=null)
          {
              tds[1].innerHTML=t-h;
          }
          else
          {
             h++;
          }
       }
     }
  }

function check(saveandcontinue,type)
{
   	var code = khFieldForm.fieldnumber.value;
	if(code==null || trim(code).length<=0)
   	{
     	alert("指标编号不能为空！");
     	return;
   	}
	if(trim(code).length>=31)
   	{
     	alert("指标编号不能超过30位！");
     	return;
   	}
   	var chc=/^\w+$/;
   	if(!chc.test(code))
   	{
      	alert("指标编号含有非法字符！");
      	return;
   	}
   	var name=khFieldForm.fieldname.value;
   	if(name==null||trim(name).length<=0)
   	{
     	alert("指标名称不能为空！");
      	return;
   	}
//   	if(trim(name).length>=31)
//    {
//        alert("指标名称不能超过30位！");
//        return;
//    }
   	var obj=document.getElementsByName("aa");
   	var ids="";
   	for(var i=0;i<obj.length;i++)
   	{
      	if(obj[i].value==null||trim(obj[i].value).length<=0)
      	{
         	alert("标度代码不能为空！");
         	return;
      	}
      	if(!chc.test(obj[i].value))
      	{
          	alert("标度代码含有非法字符！");
          	return;
      	}
      	ids+="/"+obj[i].value;
   	}
    for(var i=0;i<obj.length;i++)
    {
       	for(var j=0;j<obj.length;j++)
       	{
          	if(i!=j&&obj[i].value.toLowerCase()==obj[j].value.toLowerCase())
          	{
             	alert("标度代码不能重复！");
             	return;
          	}
       	}
    }
   	var cobj=document.getElementsByName("ff");
   	for(var i=0;i<cobj.length;i++)
   	{
      	if(cobj[i].value==null||trim(cobj[i].value).length<=0)
      	{
        	alert("标度内容不能为空！");
        	return;
      	}
   	}
   	var valueobj=document.getElementsByName("cc");
   	for(var i=0;i<valueobj.length;i++)
   	{
      	if(valueobj[i].value==null||trim(valueobj[i].value).length<=0)
      	{
         	alert("标度比例不能为空！");
         	return;
      	}
      	if(parseFloat(valueobj[i].value)<0||parseFloat(valueobj[i].value)>1)
      	{
         	alert("标度比例的值应介于 [0] 和 [1] 之间！");
         	return;
      	}
   	}
    var checkFloat = /^\d+(\.\d+)?$/;
    var dd=document.getElementsByName("dd");
    for(var i=0;i<dd.length;i++)
    {
    	if(dd[i].value!=null&&dd[i].value!="")
    	{
      		if(!checkFloat.test(dd[i].value))
      		{
          		alert("上限值应输入数字！");
          		return;
      		}
     	}
    }
    var ee=document.getElementsByName("ee");
    for(var i=0;i<ee.length;i++)
    {
        if(ee[i].value!=null&&ee[i].value!="")
        {
          	if(!checkFloat.test(ee[i].value))
            {
              	alert("下限值应输入数字！");
              	return;
          	}
        }
    }

    //

    var hashvo=new ParameterSet();
    hashvo.setValue("fieldnumber",code);
    hashvo.setValue("type",type);
    <%  if((flag) && (!subsys_id.equalsIgnoreCase("35")) && (version.searchFunctionId("060803"))){   %>
    hashvo.setValue("formula",getEncodeStr(document.getElementById('computeFormula').value));
    <%  } %>
    hashvo.setValue("hiddennumber",khFieldForm.hiddennumber.value);
    hashvo.setValue("subsys_id",<%=subsys_id %>);
    hashvo.setValue("saveandcontinue",saveandcontinue);
    hashvo.setValue("ids",ids.substring(1));
    var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'9021001017'},hashvo);
}
function check_ok(outparameters)
{
   	var msg = outparameters.getValue("msg");
   	msg = getDecodeStr(msg);
   	var type=outparameters.getValue("type");
   	var saveandcontinue=outparameters.getValue("saveandcontinue");
   	if(msg=="1")
   	{
       	sav(type,saveandcontinue);
   	}
   	else
   	{
     	alert(msg);
     	return;
   	}
}
function sav(type,saveandcontinue)
{
  var aa="";
  var ff="";
  var cc="";
  var dd="";
  var ee="";
  var aobj=document.getElementsByName("aa");
  var fobj=document.getElementsByName("ff");
  var cobj=document.getElementsByName("cc");
  var dobj=document.getElementsByName("dd");
  var eobj=document.getElementsByName("ee");
  for(var i=0;i<aobj.length;i++)
  {
     aa+="∑"+((aobj[i].value==null||trim(aobj[i].value).length==0)?"#":aobj[i].value);
     ff+="∑"+((fobj[i].value==null||trim(fobj[i].value).length==0)?"#":fobj[i].value);
     cc+="∑"+((cobj[i].value==null||trim(cobj[i].value).length==0)?"#":cobj[i].value);
     dd+="∑"+((dobj[i].value==null||trim(dobj[i].value).length==0)?"#":dobj[i].value);
     ee+="∑"+((eobj[i].value==null||trim(eobj[i].value).length==0)?"#":eobj[i].value);
  }
  khFieldForm.aastr.value=aa.substring(1);
  khFieldForm.ffstr.value=ff.substring(1);
  khFieldForm.ccstr.value=cc.substring(1);
  khFieldForm.ddstr.value=dd.substring(1);
  khFieldForm.eestr.value=ee.substring(1);

  var status="0";
  <%  if((flag) && (!subsys_id.equalsIgnoreCase("35"))){   %>
	  var o=document.getElementById("st");
	  if(o!=null && o.checked)
	      status = "1";
  <%  } %>

  khFieldForm.action="/performance/kh_system/kh_field/add_edit_field.do?b_save=save&tabid=1&type="+type+"&saveandcontinue="+saveandcontinue+"&status="+status;
  khFieldForm.submit();
}
 function ctrl_key(obj)
  {
      var name=obj.id;

      var temp=name.indexOf("_");//a2_1 clo 上下，row左右
      var clo=name.substring(1,temp);
      var row=name.substring(temp+1);
      var newrow="";
      var newclo="";
      var newstr="";
      key=window.event.keyCode;
      if(key==38)
      {
         newclo=parseInt(clo)-1;
         newrow=row;
      }
      if(key==40)
      {
        newclo=parseInt(clo)+1;
        newrow=row;
      }
      if(key==37)
      {
         newrow=parseInt(row)-1;
         newclo=clo;
      }
      if(key==39)
      {
         newrow=parseInt(row)+1;
         newclo=clo;
      }
      newstr="a"+newclo+"_"+newrow;
      var new_object=document.getElementById(newstr);
      if(new_object!=null && new_object.type!='hidden')
	    	new_object.focus();


  }
  function initClose(isClose)
  {

      if(isClose=="1")
     {
         var obj = new Object();
         obj.refresh = "2";
         obj.pid="${khFieldForm.fieldnumber}";
         parent.window.returnValue=obj;
         if(window.showModalDialog) {
             parent.window.close();
         }else{
             parent.window.opener.window.SetFiled_win_ok(obj);
             window.open("about:blank","_top").close();
         }
     }
     else
     {
     }
  }

  function importBzbd(type)
  {
	  if(confirm("引用标准标度后将覆盖原有标度，是否继续？"))
	  {
		  var status="0";
		  var o=document.getElementById("st");
		  if(o!=null && o.checked)
		  {
		    status = "1";
		  }
		  khFieldForm.action="/performance/kh_system/kh_field/add_edit_field.do?b_import=import&tabid=2&type="+type+"&status="+status;
		  khFieldForm.submit();
	  }
  }

  function closee(refresh)
  {
	   var obj = new Object();
	   obj.refresh = refresh;
	   obj.pid="${khFieldForm.fieldnumber}";
      parent.window.returnValue=obj;

      if(window.showModalDialog) {
          parent.window.close();
      }else{
          parent.window.opener.window.SetFiled_win_ok(obj);
          window.open("about:blank","_top").close();
      }
  }
function addrul(point_id,type)
{
         var theurl="/performance/achivement/standarditem/search_standarditem_list.do?b_init=init`point_id="+point_id+"`type="+type;
         var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
        var return_vo= window.showModalDialog(iframe_url, arguments,
        "dialogWidth:560px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");
}
function addBaseRule(point_id,type)
{
   var theurl="/performance/achivement/standarditem/search_standarditem_list.do?b_baserule=base`point_id="+point_id+"`type="+type;
         var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
        var return_vo= window.showModalDialog(iframe_url, arguments,
        "dialogWidth:560px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");
}
function changeDisplay(obj)
{
var value="";
     for(var i=0;i<obj.options.length;i++)
     {
         if(obj.options[i].selected)
         {
            value=obj.options[i].value;
            break;
         }
     }
     if(value=='0')
     {
        document.getElementById("addvalid").style.display="none";
        document.getElementById("all").style.display="none";
        document.getElementById("abut").style.display="none";
        document.getElementById("lt").style.display="none";
        if(document.getElementById("lt")!=null)
        {
          document.getElementById("lt").style.display="none";
        }
        if(document.getElementById("lt2")!=null)
        {
          document.getElementById("lt2").style.display="none";
        }
     }
     if(value=='1')
     {
        document.getElementById("addvalid").style.display="block";
        document.getElementById("all").style.display="block";
        document.getElementById("abut").style.display="none";
        document.getElementById("lt").style.display="block";
        var obj= document.getElementById("sid");
        baifen(obj);
     }
     if(value=='3')
     {
          document.getElementById("addvalid").style.display="none";
          document.getElementById("all").style.display="none";
          document.getElementById("abut").style.display="block";
          document.getElementById("lt").style.display="none";
          if(document.getElementById("lt")!=null)
         {
          document.getElementById("lt").style.display="none";
         }
         if(document.getElementById("lt2")!=null)
         {
          document.getElementById("lt2").style.display="none";
         }
     }
     if(value=='2')
     {
        document.getElementById("addvalid").style.display="none";
        document.getElementById("all").style.display="none";
        document.getElementById("abut").style.display="block";
        document.getElementById("lt").style.display="block";

     }
}
function baifen(obj)
{
    var ruleObj=document.getElementsByName("rule")[0];
    var ruleValue="";
    for(var i=0;i<ruleObj.options.length;i++)
    {
        if(ruleObj.options[i].selected)
           ruleValue=ruleObj.options[i].value;
    }
    if(ruleValue=="1")
    {
        var value="";
        for(var i=0;i<obj.options.length;i++)
        {
           if(obj.options[i].selected)
           {
              value=obj.options[i].value;
              break;
           }
        }
        if(value=='1')
        {
           document.getElementById("pp").style.display="block";
           document.getElementById("pp2").style.display="block";
        }
        else
        {
           document.getElementById("pp").style.display="none";
           document.getElementById("pp2").style.display="none";
        }
     }
}
function gradeConfig()
{
    var theurl = "/performance/kh_system/kh_field/init_grade_template.do?b_init=init";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    /* if(window.showModalDialog){
        window.showModalDialog(iframe_url, arguments,
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        window.open(iframe_url, "gradeConfig_window", "width=500; height=300;resizable=no;center=yes;scroll=no;status=no");
	} */
    
    var config = {
        width:500,
        height:300,
        type:'2',
        dialogArguments:arguments
    }
    if(!window.showModalDialog){
        window.dialogArguments = arguments;
    }
    modalDialog.showModalDialogs(iframe_url,'gradeConfig_window',config);
}
function point_rule()
{
    var theurl = "/performance/kh_system/kh_field/add_edit_field.do?br_rule=rule";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;

    var config = {
        width:570,
        height:370,
        type:'2',
        dialogArguments:arguments
    }
    if(!window.showModalDialog){
        window.dialogArguments = arguments;
    }
    modalDialog.showModalDialogs(iframe_url,'',config);
//    var return_vo= window.showModalDialog(iframe_url, arguments,
//        "dialogWidth:570px; dialogHeight:370px;resizable:no;center:yes;scroll:no;status:no");
}

//  根据指标类别查指标
function checkTarget()
{
	//khFieldForm.action="/performance/kh_system/kh_field/add_edit_field.do?b_init=searchKpi&tabid=6&kpiTarget=selecTarget";
	//khFieldForm.submit();
	//【2913】绩效管理：新建指标，定量指标计算公式，切换指标类别时，界面总一闪一闪的 jingq add 2014.12.10
	var kpiTargetType = $F('kpiTargetType');
	var hashvo = new ParameterSet();
	hashvo.setValue("kpiTargetType", kpiTargetType);
	hashvo.setValue("tabid", "6");
	hashvo.setValue("kpiTarget", "selecTarget");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showparam,functionId:'9021001014'},hashvo);
}

function showparam(outparamters){
	var kpiTarget_idList = outparamters.getValue("kpiTarget_idList");
	AjaxBind.bind(khFieldForm.kpiTarget_id, kpiTarget_idList);
}

function symbol2(cal)
{
	var computeFormula=document.getElementById("computeFormula");
	computeFormula.focus();
	var element = document.selection;
	if(element){
        var rge = element.createRange();
        if (rge!=null)
            rge.text=cal;

	}else{
        var start =computeFormula.selectionStart;
        computeFormula.value = computeFormula.value.substring(0, start) + cal + computeFormula.value.substring(start, computeFormula.value.length);
        computeFormula.setSelectionRange(start + cal.length, start + cal.length);
//        computeFormula.selectionStart = computeFormula.selectionEnd=len;
	}

//    var len = computeFormula.value.length;
//    if (document.selection) {//ie识别
//        var sel = element.createTextRange();
//        sel.moveStart('character',len);
//        sel.collapse();
//        sel.select();
//    } else  {
//        computeFormula.selectionStart = computeFormula.selectionEnd = len;//ff和chrome
//    }
}
function addrelate(name,obj)
{
	var no = new Option();
	for(i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected)
		{
	    	no.value=obj.options[i].value;
	    	no.text=obj.options[i].text;
		}
	}
    var expr_editor=document.getElementById(name);
    expr_editor.focus();
	var element ;
    if(document.selection){
        element = document.selection;
        if (element!=null) {
            var rge = element.createRange();
            if (rge!=null)
                rge.text=no.value;
        }
    }else{
        //插入公式 浏览器兼容  wangbs 20190320
        element = window.getSelection();
        var start =expr_editor.selectionStart;
        expr_editor.value = expr_editor.value.substring(0,start)+no.value+expr_editor.value.substring(start,expr_editor.value.length);
        expr_editor.setSelectionRange(start+no.value.length,start+no.value.length);
    }
}
// 函数向导
function functionWizzard()
{
    var thecodeurl ="/org/funwd/function_Wizard.do?b_query=link&callBackFunc=functionWizzard_ok"//&flag=1&checktemp=jixiaoguanli&planid="+planid;
    /*var return_vo= window.showModalDialog(thecodeurl, "",
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");*/
    var config = {
        width:400,
        height:400,
        type:'2'
    }
    modalDialog.showModalDialogs(thecodeurl,"functionWizzardWin",config,functionWizzard_ok);
}
function functionWizzard_ok(return_vo){
    if(return_vo!=null)
    {
        var computeFormulaEl = document.getElementById("computeFormula")
        computeFormulaEl.focus();
        var element = document.selection;
        if (element!=null) {
            var rge = element.createRange();
            if (rge!=null)
                rge.text=return_vo;
        }else{
            var start =computeFormulaEl.selectionStart;
            computeFormulaEl.value = computeFormulaEl.value.substring(0, start) + return_vo + computeFormulaEl.value.substring(start, computeFormulaEl.value.length);
            computeFormulaEl.setSelectionRange(start + return_vo.length, start + return_vo.length);
        }
    }else
    {
        return ;
    }
}
// 公式检查
function checkFormula()
{
	var hashvo=new ParameterSet();
//	hashvo.setValue("type",'total_formula');
//	hashvo.setValue("planid",document.evaluationForm.planid.value);
	hashvo.setValue("formula",getEncodeStr(document.getElementById('computeFormula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula,functionId:'9021001286'},hashvo);
}
function resultCheckFormula(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok" || info.length==0)
		alert("公式通过检查！");
	else if(info=="noHave")
		alert("未定义计算公式！");
	else
		alert(info);
}

</script>

<html:form action="/performance/kh_system/kh_field/add_edit_field" style="width:590px;">

	<hrms:tabset name="pageset" width="100%" height="430" type="false">

 		<hrms:tab name="tab1" label="<%=nameDesc%>" visible="true">
  			<table width="100%" height='100%' align="center">
				<tr><td  valign="top" align='center'>
				  	<div style="overflow:auto;width:470px;height:380px;" >
						<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="DetailTable">
							<tr>
								<td colspan="3">&nbsp;&nbsp;</td>
							</tr>

							<tr height="30px">
								<td align="right" width='130' colspan='2' class="paddinginput">
									<bean:message key="kh.field.num"/>
								</td>
								<td>
									<html:text name="khFieldForm" styleClass="textColorWrite" property="fieldnumber" style="width:265"/>
								</td>
							</tr>

							<tr height="30px">
								<td align="right" width='130' colspan='2' class="paddinginput">
									<bean:message key="kh.field.field_n"/>
								</td>
								<td>
									<html:text name="khFieldForm" styleClass="textColorWrite"  property="fieldname" style="width:265" />
								</td>
							</tr>


							<logic:notEqual name="khFieldForm"  property="subsys_id" value="35">
								<tr>
									<td align="right" width='130' colspan='2' class="paddinginput">
										<bean:message key="kh.field.field_type"/>
									</td>
									<td nowrap>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td align="left">
													<html:radio name="khFieldForm" property="pointkind" value="0" onclick="visibleBox('0');"><bean:message key="kh.field.dxyd"/></html:radio>
												</td>
												<td align="right">
													<html:radio name="khFieldForm" property="pointkind" value="1" onclick="visibleBox('1');"><bean:message key="kh.field.dlyd"/></html:radio>
												</td>

												<% if(flag){%>
												<td align="right">
													<div id="box" style="${khFieldForm.display}">
														<html:checkbox styleId="st" name="khFieldForm" property="status" value="1" onclick="visibleButton(this);"><bean:message key="kh.field.unite_score"/></html:checkbox>
													</div>
												</td>
												<td align="right">
													<div id="bbuu" style="${khFieldForm.display}">
														<input type="button" class="mybutton" value="..." onclick="point_rule();"/>
													</div>
												</td>
												<% }%>
											</tr>
										</table>
									</td>
								</tr>
							</logic:notEqual>


							<tr>
								<td colspan="4">&nbsp;&nbsp;</td>
							</tr>

							<logic:notEqual name="khFieldForm" property="subsys_id" value="35">
								<tr>
									<td align="right" width='130' valign='middle' colspan='2' class="paddinginput">
										<bean:message key="kh.field.field_explain"/>
									</td>
									<td>
										<html:textarea name="khFieldForm" property="description" rows="8" style="width:265"></html:textarea>
									</td>
								</tr>
							</logic:notEqual>
							<logic:equal name="khFieldForm" property="subsys_id" value="35">
								<tr>
									<td align="right" width='130' valign='middle' colspan='2' class="paddinginput">
										<bean:message key="kh.field.field_explain"/>
									</td>
									<td>
										<html:textarea name="khFieldForm" property="description" rows="5" style="width:265"></html:textarea>
									</td>
								</tr>
								<tr>
									<td align="right" width='130' valign='middle' colspan='2' class="paddinginput">
										<bean:message key="kh.field.field_proposal"/>
									</td>
									<td>
										<html:textarea name="khFieldForm" property="proposal" rows="5" style="width:265"></html:textarea>
									</td>
								</tr>
							</logic:equal>

							<tr>
								<td colspan="3">&nbsp;&nbsp;</td>
							</tr>
							<tr height="30px">
								<td align="right" width='130' colspan='2' class="paddinginput">
									<logic:notEqual name="khFieldForm" property="subsys_id" value="35">
										<bean:message key="kh.field.bd_explain"/>
									</logic:notEqual>
									<logic:equal name="khFieldForm" property="subsys_id" value="35">
										<bean:message key="kh.field.scalebd_explain"/>
									</logic:equal>
								</td>
								<td>
									<html:radio name="khFieldForm" property="visible" value="3"><bean:message key="kh.field.novisible"/></html:radio>
									<%-- smk 2015.11.30 如果是能力素质指标，合并显示指标解释和标度 --%>
									<logic:notEqual name="khFieldForm" property="subsys_id" value="35">
										<html:radio name="khFieldForm" property="visible" value="2"><bean:message key="kh.field.visiblebd"/></html:radio>
									<html:radio name="khFieldForm" property="visible" value="1"><bean:message key="kh.field.visible_explain"/></html:radio>
									</logic:notEqual>

									<logic:equal name="khFieldForm" property="subsys_id" value="35">
										<html:radio name="khFieldForm" property="visible" value="1"><bean:message key="kh.field.visiblebd_explain"/></html:radio>
									</logic:equal>

								</td>
							</tr>
							<tr height="30px">
								<td align="right" width='130' colspan='2' class="paddinginput">
									<bean:message key="kh.field.yx_flag"/>
								</td>
								<td>
									<html:radio name="khFieldForm" property="fieldvlidflag" value="1"><bean:message key="kh.field.yx"/></html:radio>
									<html:radio name="khFieldForm" property="fieldvlidflag" value="0"><bean:message key="kh.field.wx"/></html:radio>
								</td>
							</tr>
						</table>
					</div>
 					<html:hidden name="khFieldForm" property="pointsetid"/>
				 	<html:hidden name="khFieldForm" property="hiddennumber"/>
				 	<html:hidden name="khFieldForm" property="type"/>
				 	<html:hidden name="khFieldForm" property="rulePointid"/>
				 </td>
				</tr>
			</table>
 		</hrms:tab>


	 	<hrms:tab name="tab2" label="<%=scaleDesc%>" visible="true">
	  		<table width="100%"  height='92%'   align="center">
				<tr height="90%"> <td  valign="center" align='center'>
				  	<div style="overflow:auto;width:100%;height:290px;" >
						<table width="100%" border="0" cellspacing="0"  align="center" id="tab" cellpadding="0" class="AutoTable">
							<thead>
								<tr>
									<td class="TableRow" align="center" width="40px" nowrap>
										<bean:message key="kh.field.select"/>
									</td>
									<td class="TableRow" align="center" width="45px" nowrap>
										<bean:message key="kh.field.seq"/>
									</td>
									<td class="TableRow" align="center" width="45px" nowrap>
										<bean:message key="kh.field.code"/>
									</td>
									<td class="TableRow" align="center" width="150px" nowrap>
										&nbsp;&nbsp;<bean:message key="kh.field.content"/>
									</td>
									<td class="TableRow" align="center" width="60px" nowrap>
										&nbsp;&nbsp;<bean:message key="kh.field.scale"/>
									</td>
									<td class="TableRow" align="center" width="60px" nowrap>
										<bean:message key="kh.field.topv"/>
									</td>
									<td class="TableRow" align="center" width="60px" nowrap>
										<bean:message key="kh.field.bottomv"/>
									</td>
								</tr>
							</thead>
							<%int i=0; %>
							<logic:iterate id="element" name="khFieldForm" property="newgradeList" offset="0" indexId="index">
					 		<%
					          if(i%2==0)
					          {
					        %>
					          	<tr class="trShallow" id='<%="cc"+(index.intValue()+1)%>'>
					        <%}
					          else
					          {
					        %>
					          	<tr class="trDeep" id='<%="cc"+(index.intValue()+1)%>'>
					        <%
					          }
					        %>
						          <td  align="center" class="RecordRow" nowrap><input type='checkbox' name='bb' value='<%=i+1%>'/></td>
						          <td  align="center" class="RecordRow" id="<%="g_"+(i+1)%>" nowrap><%=i+1%></td>
						          <td  align="center" class="RecordRow" nowrap><input style="text-align:center;width:35px;" type='text' size='3' maxlength="1" class="inputtext"  onkeydown='ctrl_key(this);' id='<%="a"+(index.intValue()+1)+"_2"%>' name='aa' value='<bean:write name='element' property='gradeid'/>'/></td>
						          <td  align="center" class="RecordRow" nowrap><input style="text-align:left;width:140px;"  type='text' size='17' onkeydown='ctrl_key(this);' class="inputtext"  id='<%="a"+(index.intValue()+1)+"_3"%>' name='ff' value='<bean:write name='element' property='gradedesc'/>'/></td>
						          <td  align="center" class="RecordRow" nowrap><input style="text-align:right;width:50;padding-right:3px;" type='text' onkeydown='ctrl_key(this);' maxlength="10" class="textColorWrite"  size='4' id='<%="a"+(index.intValue()+1)+"_4"%>' name='cc' value='<bean:write name='element' property='gradevalue'/>'/></td>
						          <td  align="center" class="RecordRow" nowrap><input style="text-align:right;width:50;padding-right:3px;" type='text' onkeydown='ctrl_key(this);' maxlength="10" class="textColorWrite"  size='7' id='<%="a"+(index.intValue()+1)+"_5"%>' name='dd' value='<bean:write name='element' property='top_value'/>'/></td>
						          <td  align="center" class="RecordRow" nowrap><input style="text-align:right;width:50;padding-right:3px;" type='text' size='7' onkeydown='ctrl_key(this);' class="textColorWrite"  maxlength="10" name='ee' id='<%="a"+(index.intValue()+1)+"_6"%>' value='<bean:write name='element' property='bottom_value'/>'/></td>

			  					</tr>
							<%i++; %>
							</logic:iterate>
						</table>
					</div>
				</td>
				</tr>
				<input type="hidden" value="" name="aastr"/>
				<input type="hidden" value="" name="ffstr"/>
				<input type="hidden" value="" name="ccstr"/>
				<input type="hidden" value="" name="ddstr"/>
				<input type="hidden" value="" name="eestr"/>
				<html:hidden name="khFieldForm" property="tabid"/>

				<tr height="10%">
					<td valign="baseline">
						<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:3px;">
							<tr>
								<td align="center">
									<html:hidden name="khFieldForm" property="type"/>
									<input type="button" name="ad" value="新建标准标度" class="mybutton" onclick="gradeConfig();"/>
									<input type="button" name="import" value="引用标准标度" class="mybutton" onclick='importBzbd("${khFieldForm.type}");'/>
									<input type="button" name="new" value="<bean:message key="kh.field.new"/>" class="mybutton" onclick="addRows();"/>
									<input type="button" name="del" value="<bean:message key="kh.field.delete"/>" class="mybutton" onclick="deleteRow();"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
	 	</hrms:tab>

	<% if(flag){%>
	<logic:notEqual name="khFieldForm" property="subsys_id" value="35">
 		<%  if(version.searchFunctionId("060803")){   %>
 		<hrms:tab name="tab6" label="定量指标计算公式" visible="true">
			<table width="100%" height="300" border="0" align="center">
				<tr>
					<td>
						<table width="100%" height="300" border="0">
							<tr>
								<td align="left">
									<table width="100%" border="0" align="left">
										<tr>
											<td valign='top'>
												<table width="100%" border="0">
													<tr>
														<td colspan="2" align="left">
                                                            <textarea id="computeFormula" name="computeFormula" rows="8" style="width:100%">${khFieldForm.computeFormula}</textarea>
														</td>
													</tr>
													<tr>
														<td width="55%">
															<fieldset width="100%">
																<legend align="center" style="text-align:center">
																	<bean:message key="gz.formula.operational.symbol" />
																</legend>
																<table width="100%" border="0">
																	<tr>
																		<td>
																			<table width="100%" border="0">
																				<tr>
																					<td height="22">
																						<input type="button" value=" 0 "
																							onclick="symbol2(0);" class="smallbutton">

																						<input type="button" value=" 1 "
																							onclick="symbol2(1);" class="smallbutton">

																						<input type="button" value=" 2 "
																							onclick="symbol2(2);" class="smallbutton">

																						<input type="button" value=" 3 "
																							onclick="symbol2(3);" class="smallbutton">

																						<input type="button" value=" 4 "
																							onclick="symbol2(4);" class="smallbutton">

																						<input type="button" value=" ( "
																							onclick="symbol2('(');" class="smallbutton">

																						<input type="button" value="如果"
																							onclick="symbol2('如果');" class="smallbutton" style="width:35px;">

																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value=" 5 "
																							onclick="symbol2(5);" class="smallbutton">

																						<input type="button" value=" 6 "
																							onclick="symbol2(6);" class="smallbutton">

																						<input type="button" value=" 7 "
																							onclick="symbol2(7);" class="smallbutton">

																						<input type="button" value=" 8 "
																							onclick="symbol2(8);" class="smallbutton">

																						<input type="button" value=" 9 "
																							onclick="symbol2(9);" class="smallbutton">

																						<input type="button" value=" ) "
																							onclick="symbol2(')');" class="smallbutton">

																						<input type="button" value="那么"
																							onclick="symbol2('那么');" class="smallbutton" style="width:35px;">
																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value=" + "
																							onclick="symbol2('+');" class="smallbutton">

																						<input type="button" value=" - "
																							onclick="symbol2('-');" class="smallbutton">

																						<input type="button" value=" * "
																							onclick="symbol2('*');" class="smallbutton">

																						<input type="button" value=" / "
																							onclick="symbol2('/');" class="smallbutton">

																						<input type="button" value=" = "
																							onclick="symbol2('=');" class="smallbutton">

																						<input type="button" value=" . "
																							onclick="symbol2('.');" class="smallbutton">

																						<input type="button" value="否则"
																							onclick="symbol2('否则');" class="smallbutton" style="width:35px;">
																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value=' > '
																							onclick="symbol2('>');" class="smallbutton">

																						<input type="button" value=' < '
																						 		onclick="symbol2('<');" class="smallbutton">

																						<input type="button" value="< >"
																							onclick="symbol2('<>');" class="smallbutton" style="width:35px;">

																						<input type="button" value="且"
																							onclick="symbol2('且');" class="smallbutton">

																						<input type="button" value="或"
																							onclick="symbol2('或');" class="smallbutton">

																						<input type="button" value="结束"
																							onclick="symbol2('结束');" class="smallbutton" style="width:35px;">

																						<input type="button" value="分情况"
																							onclick="symbol2('分情况');" class="smallbutton" style="width:50px;">
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
														<td align="center"  width="45%">
															<fieldset>
																<legend align="center" style="text-align:center" >
																	<bean:message key='org.maip.reference.projects' />
																</legend>
																<table width="100%" border="0" height="116">
																	<tr height="10">
																		<td valign="top">
																			<table width="100%" border="0">
																				<tr>
																					<td align="left" nowrap>
																						指标<br/>
																						类别
																					</td>
																					<td>																																																																	
																						<html:select name="khFieldForm"
																							property="kpiTargetType" size="1" style="width:168px"
																							onchange="checkTarget();" >
																							<html:optionsCollection property="kpiTargetTypeList" value="dataValue" label="dataName" />																						
																						</html:select>
																					</td>
																				</tr>
																				<tr>
																					<td align="left" nowrap>
																						指标
																					</td>
																					<td>																																										
																						<html:select name="khFieldForm"
																							property="kpiTarget_id" size="1" style="width:168px"
																							onchange="addrelate('computeFormula',this);" >																							
																							<html:optionsCollection property="kpiTarget_idList" value="dataValue" label="dataName" />																						
																						</html:select>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
													<tr>
														<td colspan="2" align="right">
															<input type='button' style="position:relative;left:-50px" value='向导' class="mybutton"
																onclick="functionWizzard();" />															
														
															<input type="button" name="formulaCheck" style="position:relative;left: -50px;"
																value="<bean:message key="performance.workdiary.check.formula"/>"
																class="mybutton" onclick="checkFormula();" />																																																									
														</td>
													</tr>													
												</table>
											</td>
										</tr>
									</table>
								</td>								
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</hrms:tab>
 		<%  } %>
 		
 			
		<hrms:tab name="tab4" label="考核内容" visible="true">
	        <table width="100%"  height='92%'   align="center"> 
				<tr height="90%"> 
				   <td valign="middle" align='center'>
				       <div style="overflow:auto;width:475px;height:380px;" >
				           <textarea name="kh_content" rows="18" cols="61">${khFieldForm.kh_content}</textarea>
				       </div>
				   </td>
				</tr>
			</table>
	    </hrms:tab>
    
    
		<hrms:tab name="tab5" label="评分原则" visible="true">
	         <table width="100%"  height='92%'   align="center"> 
				<tr height="90%"> 
				    <td valign="middle" align='center'>
				         <div style="overflow:auto;width:475px;height:380px;" >
				             <textarea name="gd_principle" rows="18" cols="61">${khFieldForm.gd_principle}</textarea>
				         </div>
				    </td>
			    </tr>
		     </table>
		</hrms:tab>				
	</logic:notEqual>
	<%  } %>
	
 	</hrms:tabset>

 	
 	<table style="margin-top:2px;" width="95%" border="0" cellspacing="0" align="center" cellpadding="0">
  		<tr style="height:35">			
			<td align="center" >
				<input type="button" name="sva" class="mybutton" value="<bean:message key="button.save"/>" onclick="check('1','${khFieldForm.type}');"/>
				<logic:equal value="1" property="type" name="khFieldForm">
					<input type="button" name="sc" class="mybutton" value="<bean:message key="button.savereturn"/>" onclick="check('2','${khFieldForm.type}');"/>
				</logic:equal>
				<input type="button" name="clo"  class="mybutton" value="<bean:message key="button.cancel"/>" onclick="closee('${khFieldForm.isrefresh}');"/>
			</td>
		</tr>
	</table>
	
<script type="text/javascript">

	initClose("${khFieldForm.isClose}");
	function toNext()
  	{
        var tabid=khFieldForm.tabid.value;
        if(tabid=='2')
        {
          	var tab=$('pageset');
          	if(tab.setSelectedTab){
	      	    tab.setSelectedTab("tab2");
            }else{
               $("#tabset_pageset").tabs('select', 1);
            }
	    }
	    if(tabid=='6')
        {
          	var tab=$('pageset');
            if(tab.setSelectedTab){
                tab.setSelectedTab("tab2");
            }
            else{
                $("#tabset_pageset").tabs('select', 5);
            }
	    }
  	}
  	
</script>

</html:form>
</body>
</html>