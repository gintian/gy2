<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
<!--
var arr = new Array();
var tabid="";
var row = 0;
var tablestr="";
var ForStr = new Array();
var trid=0;
var tridArr = new Array();
function addRow()
{
   var tab=document.getElementById("tt");
   var i=tab.rows.length;
   tablestr=tablestr.replace("</table>","").replace("</TABLE>","");
   var tabstr="";
   var v="";
   arr.push(" # # # # # # ");
   var newRow=tab.insertRow(tab.rows.length);
    newRow.id="cc"+i;
    newRow.onclick=function(){
       configId(i);
    }
    myNewCell=newRow.insertCell(0);
    //tabstr+="<tr onclick=\"configId('"+i+"');\">";
         tabstr+="<td width=\"6%\" align=\"center\" class=\"RecordRow\" nowrap>";
         tabstr+="<div id=\"a_"+(i)+"_0\" align=\"center\">";
         tabstr+="<input type=\"text\" size=\"20\" style=\"text-align:right;\" onclick=\"change('a_"+(i)+"_0');\" value=\"\"/>";
		 tabstr+="</div>";		
		 tabstr+="<input type=\"hidden\" name=\"plan\" id=\"a_"+(i)+"_0_0\" value=\"\"/>";
		 tabstr+="</td>";
		 myNewCell.className = "RecordRow";
myNewCell.innerHTML =tabstr;
tabstr="";
myNewCell=newRow.insertCell(1);
tabstr+="<td width=\"14%\" align=\"center\" class=\"RecordRow\" nowrap>";
		 tabstr+="<div id=\"a_"+(i)+"_1\" align=\"center\">";		
		 tabstr+="<input type=\"text\" size=\"20\" style=\"text-align:right;\" class='inputtext' onclick=\"change('a_"+(i)+"_1');\" value=\"\"/>";
		 tabstr+="</div>";
		 tabstr+="<input type=\"hidden\" name=\"real\" id=\"a_"+(i)+"_1_0\" value=\"\"/>";
	     tabstr+="</td>";
	     myNewCell.className = "RecordRow";
myNewCell.innerHTML =tabstr;
tabstr="";
myNewCell=newRow.insertCell(2);
  tabstr+="<td width=\"12%\" align=\"center\" class=\"RecordRow\" nowrap>";
	     tabstr+="<div id=\"a_"+(i)+"_2\" align=\"center\">";
		 tabstr+="<input type=\"text\" size=\"20\" style=\"text-align:right;\" class='inputtext' onclick=\"change('a_"+(i)+"_2');\" value=\"\"/>";		
		 tabstr+="</div>";	
		 tabstr+="<input type=\"hidden\" name=\"balance\" id=\"a_"+(i)+"_2_0\" value=\"\"/>";	
		 tabstr+="</td>";
		 myNewCell.className = "RecordRow";
myNewCell.innerHTML =tabstr;
tabstr="";
myNewCell=newRow.insertCell(3);
 tabstr+="<td width=\"6%\" align=\"center\" class=\"RecordRow\" nowrap>";
		 tabstr+="<div id=\"a_"+(i)+"_3\" align=\"center\">";
	     tabstr+="</div>";
	     tabstr+="</td>";
	     myNewCell.className = "RecordRow";
 myNewCell.innerHTML=tabstr;
 tabstr="";
 myNewCell=newRow.insertCell(4);
 tabstr+="<td width=\"6%\" align=\"center\" class=\"RecordRow\" nowrap>";
		 tabstr+="<div id=\"a_"+(i)+"_4\" align=\"center\">";
	     tabstr+="</div>";
	     tabstr+="</td>";
	     myNewCell.className = "RecordRow";
 myNewCell.innerHTML=tabstr;
 tabstr="";
 myNewCell=newRow.insertCell(5);
  tabstr+="<td width=\"12%\" align=\"center\" class=\"RecordRow\" nowrap>";
	     tabstr+="<div id=\"a_"+(i)+"_5\" align=\"center\">";
		 tabstr+="<input type=\"text\" name=\"classname\" class='inputtext' value=\"\"";
		 tabstr+="/></div></td>";
		 myNewCell.className = "RecordRow";
myNewCell.innerHTML=tabstr;	 
 tabstr="";
 myNewCell=newRow.insertCell(6);
  tabstr+="<td width=\"12%\" align=\"center\" class=\"RecordRow\" nowrap>";
	     tabstr+="<div id=\"a_"+(i)+"_6\" align=\"center\">";
		 tabstr+="<input type=\"checkbox\" name=\"flag\" value=\"0\"";
		 tabstr+="/></div></td></tr>";
		 myNewCell.className = "RecordRow";
myNewCell.innerHTML=tabstr;	 
	   
		
		
   
}
function change(id)
{
   var obj=document.getElementById(id);
   obj.innerHTML=croPayMentForm.table.value.replace("#",id);
   tr_bgcolor(id);
   var temp=id.split("_");
   tabid = temp[1];
   trid=temp[1];
   document.getElementById("selectid").value=document.getElementById(id+"_0").value;
   document.getElementById("selectid").focus();	
}
function onLeave(id)
{
    var obj=document.getElementById("selectid");
    obj.focus();
    var itemid;
    var text ="";
    var name;
     var temp=id.split("_");
    tabid = temp[1];
	document.getElementById("selectid").onblur=function(){
	for(var i=0;i<obj.options.length;i++)
	{
	   if(obj.options[i].selected)
	   {
	      document.getElementById(id+"_0").value=obj.options[i].value
	      text=obj.options[i].text;
	   }
	}
	if(text == "undefined")
	{
	   text = "";
	}
	var arr=id.split("_");
	if(arr[2] == '0')
	 name="plan";
	 else if(arr[2] =='1')
	 name="real";
	 else
	 name="balance";
	var t="<input type=\"text\" name=\"aa_"+arr[1]+"\" value=\""+text+"\" onclick=\"";
	t+="change('"+id+"');\" style=\"width:150px;text-align:right\">";
	document.getElementById(id).innerHTML=t;
	}	
}

function initArray(str)
{
    initFormular();
    
}
function initFormular()
{
   var formularStr=croPayMentForm.formularStr.value;
   ForStr = formularStr.split("`");
}
function save()
{
  var plan=document.getElementsByName("plan");
  var real = document.getElementsByName("real");
  var balance = document.getElementsByName("balance");
  var flag = document.getElementsByName("flag");
  var classname=document.getElementsByName("classname");
  var spflagid=croPayMentForm.spflagid.value;
  
  var fieldsetid="";
  var fieldsetidObj=document.getElementById("fieldsetid");
  for(var j=0;j<fieldsetidObj.options.length;j++)
  {
     if(fieldsetidObj.options[j].selected)
     {
        fieldsetid=fieldsetidObj.options[j].value;
        break;
     }
  }
  if((spflagid=="undifined"||spflagid.length==0)&&fieldsetid.length>1)
  {
    alert("请选择审批状态指标");
    return;
  }
  var adjustSetid="";
  var adjustObj=document.getElementById("amountAdjustSet");
  for(var j=0;j<adjustObj.options.length;j++)
  {
      if(adjustObj.options[j].selected)
      {
          adjustSetid=adjustObj.options[j].value;
          break;
      }
  }

  if(trim(adjustSetid).length!=0&&adjustSetid==fieldsetid)
  {
      alert("总额子集不能与总额调整子集选择同一个子集！");
      return;
  }
  var fc_field=document.getElementById("fc_flag").value;
  var ctrlAmountField=document.getElementById("ctrlAmountField").value;
  if(trim(fc_field)!=''&&trim(ctrlAmountField)!=''&&fc_field==ctrlAmountField)
  {
      alert("封存状态指标与启用总额控制指标，不能选择相同的指标！");
      return;
  }
  var amountPlanitemDescField="";
  var amountPlanitemDescFieldObj=document.getElementById("amountPlanitemDescField");
  for(var j=0;j<amountPlanitemDescFieldObj.options.length;j++)
  {
    if(amountPlanitemDescFieldObj.options[j].selected)
    {
           amountPlanitemDescField=amountPlanitemDescFieldObj.options[j].value;
    }
  }
  if(trim(adjustSetid)!=""&&amountPlanitemDescField=="")
  {
      alert("请选择计划项目或分类指标！");
      return;
  }
  var planitem="";
  var realitem="";
  var balanceitem="";
  var flagitem="";
  var classn="";
  var n=0;
  for(var j=0;j<plan.length;j++)
  {
     if(flag[j].checked)
     {
        flag[j].value='1';
     }
     else
     {
        flag[j].value='0';
     }
     if(plan[j].value==null||plan[j].value=='new'||plan[j].value=='')
        {
          alert("请为第[ "+(j+1)+" ]行的计划项目选择指标");
          return;
        }
        if(real[j].value==null||real[j].value=='new'||real[j].value=='')
        {
          alert("请为第[ "+(j+1)+" ]行的实发项目选择指标");
          return;
        }
         if(balance[j].value==null||balance[j].value=='new'||balance[j].value=='')
        {
          alert("请为第[ "+(j+1)+" ]行的剩余项目选择指标");
          return;
        }
        if(plan[j].value==real[j].value)
        {
           alert("第[ "+(j+1)+" ]行的计划项目和实发项目不能选择同一个指标");
           return;
        }
        if(plan[j].value==balance[j].value)
        {
           alert("第[ "+(j+1)+" ]行的计划项目和剩余项目不能选择同一个指标");
           return;
        }
        if(real[j].value==balance[j].value)
        {
           alert("第[ "+(j+1)+" ]行的实发项目和剩余项目不能选择同一个指标");
           return;
        }
         for(var i=0;i<plan.length;i++)
         {
            if(j!=i&&plan[j].value==plan[i].value)
            {
               alert("计划项目第["+(j+1)+"]行与第["+(i+1)+"]行选择了同一指标");
               return;
            }
            if(j!=i&&real[j].value==real[i].value)
            {
              alert("实发项目第["+(j+1)+"]行与第["+(i+1)+"]行选择了同一指标");
              return;
            }
            if(j!=i && balance[j].value==balance[i].value)
            {
                alert("剩余项目第["+(j+1)+"]行与第["+(i+1)+"]行选择了同一指标");
                return;
            }
            if(plan[j].value==real[i].value)
            {
                 alert("计划项目第["+(j+1)+"]行与实发项目第["+(i+1)+"]行选择了同一指标");
                 return;
            }
            if(plan[j].value==balance[i].value)
            {
                 alert("计划项目第["+(j+1)+"]行与剩余项目第["+(i+1)+"]行选择了同一指标");
                 return;
            }
            if(real[j].value==balance[i].value)
            {
                  alert("实发项目第["+(j+1)+"]行与剩余项目第["+(i+1)+"]行选择了同一指标");
                 return;
            }
         }
     planitem+="/"+plan[j].value;
     realitem+="/"+real[j].value;
     balanceitem+="/"+balance[j].value;
     flagitem+="/"+flag[j].value;
     classn+="`"+classname[j].value;
/**     if(classname[j].value!=""&&classn.indexOf("`"+classname[j].value+"`")!=-1){
     	alert("分类名称不能重复！");
     	return;
     }*/
     n++;
  }
  if(n>0)
  {
      var tep = planitem.substring(1).split("/");
      var tep2 = planitem.substring(1).split("/");
      for(var i=0;i<tep.length;i++)
      {
          for(var j=0;j<tep2.length;j++)
          {
             
              if(tep[i]==tep2[j])
              {
                 if(i==j)
                 {
                    continue;
                 }
                 else
                 {
                   alert("计划项目第["+(i+1)+"]行和第["+(j+1)+"]行选择了相同的指标");
                   return;
                 }
              }
          }
      }
  }
  if(n>0)
  {
     croPayMentForm.planitem.value = planitem.substring(1);
     croPayMentForm.realitem.value= realitem.substring(1);
     croPayMentForm.balanceitem.value = balanceitem.substring(1);
     croPayMentForm.flagitem.value=flagitem.substring(1);
     croPayMentForm.classitem.value=classn.substring(1);
  }
  if(croPayMentForm.ctrl_type.checked)
  {
      croPayMentForm.ctrl_type.value="0";
  }
  else
   {
       croPayMentForm.ctrl_type.value="1";
       croPayMentForm.ctrl_type.checked=true;
   }
   if(croPayMentForm.ctrl_by_level.checked)
   {
       croPayMentForm.ctrl_by_level.value="0";
       croPayMentForm.ctrl_by_level.checked=true;
   }
   else
   {
       croPayMentForm.ctrl_by_level.value="1";
       croPayMentForm.ctrl_by_level.checked=true;
   }
   if(croPayMentForm.surplus_compute.checked==true)
   {
      croPayMentForm.surplus_compute.value="1";
   }
  else{
      croPayMentForm.surplus_compute.value="0";
       croPayMentForm.surplus_compute.checked=true;
  }
   var peroid=croPayMentForm.ctrl_peroid.value;//=0 month.=1 year =2 season 1-->2--->0
   var oldperoid = croPayMentForm.oldctrl_peroid.value;
   var xx=0;
   //if(oldperoid=="0")
  // {
    //   if(peroid=="1"||peroid=="2")
    //   {
     //       xx=1;
     //  }
   //}
  // if(oldperoid=="2")
  // {
 ///     if(peroid=="1")
 //     {
  ///      xx=1;
 //     }
 //  }
 if(peroid!=''&&peroid!=oldperoid&&fieldsetid.length>1)
 {
   if(confirm("确认改变总额控制种类，薪资总额子集中审批状态将被重新初始化为[起草]"))
   {
      croPayMentForm.action ="/gz/gz_amount/init_parameter_config.do?b_save=save&optt=1";
      croPayMentForm.submit();
   }
   else
   {
      return;
   }
 }
 else
 {     
  croPayMentForm.action ="/gz/gz_amount/init_parameter_config.do?b_save=save&optt=2";
  croPayMentForm.submit();
  }
}
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
    }
	var c = document.getElementById(nid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
function changeFieldSet()
{
   croPayMentForm.action="/gz/gz_amount/init_parameter_config.do?b_query=query&opt=change";
   croPayMentForm.submit();
}
function checkid(setname)
{
if(trid==null||trid==0)
{
  alert("请选择要删除的记录");
  return;
}
var tab=document.getElementById("tt");
var i=tab.rows.length;
if(i==1)
{
   return;
}
  var planobj=document.getElementById("a_"+trid+"_0_0");
   var realobj=document.getElementById("a_"+trid+"_1_0");
    var balanceobj=document.getElementById("a_"+trid+"_2_0");
    if(planobj!=null&&realobj!=null&&balanceobj!=null)
    {
      var hashvo=new ParameterSet();
      hashvo.setValue("zeitemid",planobj.value);
      hashvo.setValue("seitemid",realobj.value);
      hashvo.setValue("sfitemid",balanceobj.value);
      hashvo.setValue("setname",setname);
      hashvo.setValue("opt","1");
      var request=new Request({method:'post',asynchronous:true,onSuccess:dele,functionId:'3020080018'},hashvo);
    }
    else
    {
      alert("请选择要删除的行！");
      return;
    }
}
function changeAdjust(obj)
{
   var setid="";
   for(var i=0;i<obj.options.length;i++)
   {
      if(obj.options[i].selected)
          setid=obj.options[i].value;
   }
   var hashvo=new ParameterSet();
   hashvo.setValue("opt","2");
    hashvo.setValue("setid",setid);
   var request=new Request({method:'post',asynchronous:true,onSuccess:changeAdjustOk,functionId:'3020080018'},hashvo);
}
function changeAdjustOk(outparameter)
{
   var fielditemlist=outparameter.getValue("list");
	AjaxBind.bind(croPayMentForm.amountPlanitemDescField,fielditemlist);
}
function dele(outparameters)
{
 var msg = outparameters.getValue("msg");
 var bool=false;
 if(msg=="no")
 {
    bool=confirm("该参数已经参与薪资总额的计算，确认删除？");
    if(!bool)
    {
       return;
    }
 }
 
if((msg!="no"&&ifdel())||(bool))
{
var num=0;
//删除操作
var tab=document.getElementById("tt");
if(tridArr.length==0)
{
}
else
{
    for(var j=0;j<tridArr.length;j++)
    {
        var tid = tridArr[j];
        if(parseInt(trid)>parseInt(tid))
        {
            trid=parseInt(trid)-1;
        }
        else
        {
          
        }
    }
}
 tab.deleteRow(trid);
tridArr.push(trid);
var tabh=document.getElementById("tt");
 var i=tabh.rows.length;
if(i<=1)
{
   tridArr = new Array();
}
  //save();
    }
}
function exebolishsubmit(src){
	var url="/gz/gz_amount/complexquery.do?b_query=link&itemid="+src; 
	var parameter = '';
	if(isIE6()){
	var obj= window.showModalDialog(url, parameter, "dialogWidth:585px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no"); 
	}else{
	var obj= window.showModalDialog(url, parameter, "dialogWidth:575px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no"); 
	}	     	
	var flag=0;
	if(obj != null){
	var temp_str = document.getElementById("fs").value;
	var temp_arr = temp_str.split("`");
	var a_arr=new Array();
	var ffll=false;
    for(var i=0;i<temp_arr.length;i++)
	  {
	     var temp=temp_arr[i];
	     var rr=temp.split("#");
	     if(rr[0]==src)
	     {
	         var str_temp=rr[0]+"#"+getEncodeStr(obj);
	         a_arr.push(str_temp);
	         ffll=true;
	     }
	     else
	     {
	        a_arr.push(temp);
	     }
	  }
	  if(!ffll)
	  {
	      a_arr.push(src+"#"+getEncodeStr(obj));
	  }
	  var fs="";
	  for(var j=0;j<a_arr.length;j++)
	  {
	    fs+="`"+a_arr[j];
	  }
	  document.getElementById("fs").value=fs.substring(1);
	var hashvo=new ParameterSet();
    hashvo.setValue("itemid",src);
	hashvo.setValue("formula",getEncodeStr(obj));
  var request=new Request({method:'post',asynchronous:true,onSuccess:save_ok,functionId:'3020080015'},hashvo);
	}
}
function save_ok(outparameters)
{
}
function selectSalary(src){
	var url="/gz/gz_amount/init_parameter_config.do?b_salary=link&itemid="+src; 
	var parameter = '';
	if(isIE6() ){
	var obj= window.showModalDialog(url, parameter, "dialogWidth:420px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");  
	}else{
	var obj= window.showModalDialog(url, parameter, "dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");  
	}	    	
    var flag=0;
	if(obj != null){
	var temp_str = document.getElementById("ss").value;
	var temp_arr = temp_str.split("`");
	var a_arr=new Array();
	var ffll=false;
    for(var i=0;i<temp_arr.length;i++)
	  {
	     var temp=temp_arr[i];
	     var rr=temp.split("#");
	     if(rr[0]==src)
	     {
	         var str_temp=rr[0]+"#"+getEncodeStr(obj);
	         a_arr.push(str_temp);
	         ffll=true;
	     }
	     else
	     {
	        a_arr.push(temp);
	     }
	  }
	  if(!ffll)
	  {
	      a_arr.push(src+"#"+getEncodeStr(obj));
	  }
	  var fs="";
	  for(var j=0;j<a_arr.length;j++)
	  {
	    fs+="`"+a_arr[j];
	  }
	  document.getElementById("ss").value=fs.substring(1);
}
}
function isVisible(obj)
{
     var umobj = document.getElementById("um");
     if(obj.checked)
     {
         umobj.style.display="block";
         document.getElementById("contrl").style.display="block";
     }
     else
     {
         umobj.style.display="none";
         document.getElementById("contrl").style.display="none";
     }
}
function initUm(ctrl_type)
{
   if(ctrl_type=="1")
   {
       var umobj = document.getElementById("um");
       umobj.style.display="none";
       document.getElementById("contrl").style.display="none";
   }
}
function configId(id)
{
     trid=id;
}
function init(ctrl_type)
{
    if(ctrl_type=='0')
    {
       document.getElementById("um").style.display="block";
       
       document.getElementById("contrl").style.display="block";
    }
    else
    {
        document.getElementById("um").style.display="none";
        document.getElementById("contrl").style.display="none";
    }
}
function showfc(object){
	var surplus_compute=document.getElementsByName("surplus_compute");
	var scp=document.getElementById("scp");
	var value=object.options[object.selectedIndex].value;
	if(value!=null&&value.length!=0){
		if(scp.currentStyle.display=='none'){
			scp.style.display='block';
			
			
		}
		surplus_compute[0].value=0;
		//surplus_compute[0].checked='false';
	}else{
			if(surplus_compute[0].checked=='true'){
				surplus_compute[0].checked='false';
			}
		scp.style.display='none';
		surplus_compute[0].value='0';
	}
}
//-->
</script>
<html:form action="/gz/gz_amount/init_parameter_config">
<html:hidden name="croPayMentForm" property="table"/>
<html:hidden name="croPayMentForm" property="formularStr" styleId="fs"/>
<html:hidden name="croPayMentForm" property="salarySet" styleId="ss"/>

<table width="100%" align="center" border="0" cellspacing="1" cellpadding="0" >
<tr>

<td> 
<fieldset>
<legend>总额设置</legend>
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
<tr>
<td style="padding-left:10px">
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0" >
<tr>
<td width="10%" align="right" nowrap>
薪资总额子集&nbsp;
</td>
<td align="left" width="40%">
<html:select name="croPayMentForm" property="fieldsetid" style="width:160" onchange="changeFieldSet();">
			 				<html:optionsCollection property="fieldsetlist" value="dataValue" label="dataName" />
						</html:select>(单位按月变化子集)
</td>
<td align="right" width="10%">
审批状态指标&nbsp;
</td>
<td align="left" width="40%">
<html:select name="croPayMentForm" property="spflagid" style="width:160">
			 				<html:optionsCollection property="spflaglist" value="dataValue" label="dataName" />
						</html:select>(单位按月变化子集关联代码类23)
</td>
</tr>
<tr>
<td align="right">
总额调整子集&nbsp;
</td>
<td>
<html:select name="croPayMentForm" property="amountAdjustSet" style="width:160" onchange="changeAdjust(this);">
			 				<html:optionsCollection property="amountAdjustSetList" value="dataValue" label="dataName" />
						</html:select>(单位按月变化子集)
</td>
<td align="right">
计划项目或分类指标&nbsp;
</td>
<td>
<html:select name="croPayMentForm" property="amountPlanitemDescField" style="width:160">
			 				<html:optionsCollection property="amountPlanitemDescFieldList" value="dataValue" label="dataName" />
						</html:select>
</td>
</tr>
<tr>
<td align="right" >
归属单位指标&nbsp;
</td>
<td>
<html:select name="croPayMentForm" property="orgid" style="width:160" >
			 				<html:optionsCollection property="orgList" value="dataValue" label="dataName" />
						</html:select>(薪资项目中除B0110外关联UN的指标)
						
	</td>
		<td align="right">	
				封存状态指标&nbsp;
				</td>
<td align="left">
<html:select name="croPayMentForm" property="fc_flag" style="width:160"  onchange="showfc(this);">
			 				<html:optionsCollection property="fc_flag_list" value="dataValue" label="dataName" />
						</html:select>
</td>
</tr>
<tr>
<td align="right">
控&nbsp;&nbsp;制&nbsp;&nbsp;种&nbsp;&nbsp;类&nbsp;
</td>
<td align="left">
<html:select name="croPayMentForm" property="ctrl_peroid" >
<html:option value="1">按年控制</html:option>
<html:option value="2">按季度控制</html:option>
<html:option value="0">按月控制</html:option>
</html:select>
</td>
<td>
&nbsp;
</td>
<td align="left">
<html:checkbox name="croPayMentForm" property="ctrl_type" value="0" onclick="isVisible(this);" style="margin-left:-4px;"></html:checkbox>是否进行部门总额控制&nbsp;&nbsp;
<html:checkbox name="croPayMentForm" property="ctrl_by_level" value="0"></html:checkbox>总额不按层级控制
</td>
</tr>
<tr>
<td align="right">
启用总额控制指标&nbsp;
</td>
<td>
<html:select name="croPayMentForm" property="ctrlAmountField" style="width:160">
			 				<html:optionsCollection property="ctrlAmountFieldList" value="dataValue" label="dataName" />
						</html:select>(单位基本情况子集，关联代码类45)	
</td>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;
</td>
<td align="left">
<table style="margin-left: -5px;">
<logic:equal value="1" name="croPayMentForm" property="hasFc">
<tr id="scp" >
</logic:equal>
<logic:equal value="0" name="croPayMentForm" property="hasFc">
<tr style="display:none" id="scp" >
</logic:equal>
<logic:equal value="1" name="croPayMentForm" property="hasFc">
<td align="left" colspan="2" style="padding-left: 0px;">
	<html:checkbox name="croPayMentForm" property="surplus_compute" value="1" style="margin-left:-1px;"></html:checkbox>封存总额结余参与计算
	</td>
</logic:equal>
 <logic:equal value="0" name="croPayMentForm" property="hasFc">
 <td align='left' colspan="2" style="padding-left: 0px;">
	<html:checkbox name="croPayMentForm" property="surplus_compute" value="0"  style="margin-left:-1px;"></html:checkbox>封存总额结余参与计算
	</td>
</logic:equal>
</tr>
</table>
</td>
</tr>
<tr id="um" style="display=block">
<td align="right">
归属部门指标&nbsp;
</td>
<td align="left">
<html:select name="croPayMentForm" property="deptid" style="width:160">
			 				<html:optionsCollection property="deptList" value="dataValue" label="dataName" />
						</html:select>(薪资项目中除E0122外关联UM的指标)
</td>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;
</td>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;
</td>
</tr>
<tr id="contrl" style="display=block">
<td align="right">
部门控制层级&nbsp;
</td>
<td>
<html:select name="croPayMentForm" property="contrlLevelId" style="width:160">
			 				<html:optionsCollection property="contrlLevelList" value="dataValue" label="dataName" />
						</html:select>
</td>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;
</td>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;
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
<td>
<fieldset>
<legend>项目,公式设置</legend>
<table width="98%" id ="tt" align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable">
 <thead>
    <tr class="TableRow">
   <td align="center" class="TableRow" nowrap>
 计划项目
  </td>
  <td align="center" class="TableRow" nowrap>
 实发项目
 </td>
 <td align="center" class="TableRow" nowrap>
 剩余项目
 </td>
 <td align="center" class="TableRow" nowrap>
 实发额计算方法
 </td>
 <td align="center" class="TableRow" nowrap>
 控制类别
 </td>
 <td align="center" class="TableRow" nowrap>
分类名称
 </td>
 <td align="center" class="TableRow" nowrap>
有效
 </td>
 </tr>
 </thead>
 <% int i=0; %>
<logic:iterate id="element" name="croPayMentForm" property="list" indexId="index">

  <tr onclick="configId('<%=(i+1)%>');">
        <td width="6%" align="center" class="RecordRow" nowrap>
         <div id="<%="a_"+(i+1)+"_0"%>">
         <input type="text" size="20" style="text-align:right;" class='inputtext' onclick="change('<%="a_"+(i+1)+"_0"%>');" value="<bean:write name="element" property="planitemdesc"/>"/>
		 </div>		
		 <input type="hidden" name="plan" id="<%="a_"+(i+1)+"_0_0"%>" value="<bean:write name="element" property="planitem"/>"/>
		 </td>
		 <td width="14%" align="center" class="RecordRow" nowrap>
		 <div id="<%="a_"+(i+1)+"_1"%>">		
		 <input type="text" size="20" style="text-align:right;" class='inputtext' onclick="change('<%="a_"+(i+1)+"_1"%>');" value="<bean:write name="element" property="realitemdesc"/>"/>
		 </div>
		 <input type="hidden" name="real" id="<%="a_"+(i+1)+"_1_0" %>" value="<bean:write name="element" property="realitem"/>"/>
	     </td>
	     <td width="12%" align="center" class="RecordRow" nowrap>
	     <div id="<%="a_"+(i+1)+"_2"%>">
		 <input type="text" size="20" style="text-align:right;" class='inputtext' onclick="change('<%="a_"+(i+1)+"_2"%>');" value="<bean:write name="element" property="balanceitemdesc"/>"/>		
		 </div>	
		 <input type="hidden" name="balance" id="<%="a_"+(i+1)+"_2_0"%>" value="<bean:write name="element" property="balanceitem"/>"/>	
		 </td>
		 <td width="6%" align="center" class="RecordRow" nowrap>
		 <div id="<%="a_"+(i+1)+"_3"%>">
		 <a href="javascript:exebolishsubmit('<bean:write name="element" property="planitem"/>');"><img src="/images/edit.gif" border="0"/></a>
	     </div>
	     </td>
	     <td width="6%" align="center" class="RecordRow" nowrap>
		 <div id="<%="a_"+(i+1)+"_4"%>">
		 <a href="javascript:selectSalary('<bean:write name="element" property="planitem"/>');"><img src="/images/edit.gif" border="0"/></a>
	     </div>
	     </td>
	      <td width="12%" align="center" class="RecordRow" nowrap>
	     <div id="<%="a_"+(i+1)+"_5"%>" align="center">
		 <input type="text" name="classname" class='inputtext' value="<bean:write name="element" property="classname"/>" />
		 </div>
		 </td>
		 <td width="12%" align="center" class="RecordRow" nowrap>
	     <div id="<%="a_"+(i+1)+"_6"%>" align="center">
		 <input type="checkbox" name="flag" value="0" <logic:equal name="element" property="flag" value="1">checked </logic:equal>/>
		 </div>
		 </td>
		 </tr>

<% i++; %>
</logic:iterate>
</table>
<br>
</fieldset>
</td>
</tr>
</table>

<table width="95%" align="center" border="0" cellspacing="0" cellpadding="0" >
<tr>
<td align="center" height="35">
<input type="button" class="mybutton" name="add" value="增加" onclick="addRow();"/>
<input type="button" class="mybutton" name="del" value="删除" onclick="checkid('${croPayMentForm.fieldsetid}');"/>
<input type="button" class="mybutton" name="sav" value="保存" onclick="save();"/>
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="croPayMentForm"/> 
</td>
</tr>
</table>
<input type="hidden" name="planitem" value=""/>
<input type="hidden" name="realitem" value=""/>
<input type="hidden" name="balanceitem" value=""/>
<input type="hidden" name="flagitem" value=""/>
<input type="hidden" name="classitem" value=""/>
<html:hidden name="croPayMentForm" property="oldctrl_peroid"/>
<script type="text/javascript">
<!--
init("${croPayMentForm.ctrl_type}");
-->
</script>
</html:form>