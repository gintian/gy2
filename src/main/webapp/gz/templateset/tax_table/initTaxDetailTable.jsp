<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>

<script type="text/javascript">
<!--

//-->
</script>
<script type="text/javascript">
<!--
var n=0;
function addRows(){
var tab=document.getElementById("tab");
var i=tab.rows.length;

var v="";
var newRow=tab.insertRow(tab.rows.length);
 newRow.id="cc"+i;
var myNewCell = newRow.insertCell(0);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='6%' align='center' class='RecordRow' nowrap> <input type='checkbox'  name='bb'  value='"+i+"'></td>";   
myNewCell=newRow.insertCell(1);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='14%' align='center' class='RecordRow' nowrap> <input style='text-align:right' type='text' class='inputtext' onkeydown='ctrl_key(this);' size='12' id='a"+i+"_1' name='aa"+i+"' value=''></td>";
myNewCell=newRow.insertCell(2);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='12%' align='center' class='RecordRow' nowrap><input style='text-align:right' type='text' class='inputtext' onkeydown='ctrl_key(this);' size='12' id='a"+i+"_2' name='aa"+i+"' value=''></td>";
myNewCell=newRow.insertCell(3);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='15%' align='center' class='RecordRow' nowrap><input style='text-align:right' type='text' class='inputtext' onkeydown='ctrl_key(this);' id='a"+i+"_3' size='8' name='aa"+i+"' value=''></td>";
myNewCell=newRow.insertCell(4);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='3%' align='center' class='RecordRow' nowrap><select onkeydown='ctrl_key(this);' id='a"+i+"_4' name='aa"+i+"'><option value='0'>上限封闭</option><option value='1'>下限封闭</option></td>";
myNewCell=newRow.insertCell(5);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='1%' align='center' class='RecordRow'  nowrap><input style='text-align:right' onkeydown='ctrl_key(this);' id='a"+i+"_5' type='text' class='inputtext' size='12' name='aa"+i+"' value=''></td>";
myNewCell=newRow.insertCell(6);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='1%' align='center' class='RecordRow' nowrap><input style='text-align:right' onkeydown='ctrl_key(this);' type='text' class='inputtext' id='a"+i+"_6' size='12' name='aa"+i+"' value=''></td>";
myNewCell=newRow.insertCell(7);
myNewCell.setAttribute("align","center");  
myNewCell.className = "RecordRow";
myNewCell.innerHTML ="<td width='10%' align='center' class='RecordRow' nowrap><input type='text' class='inputtext' size='15' onkeydown='ctrl_key(this);' name='aa"+i+"' id='a"+i+"_7' value=''></td>";
var obj=document.getElementById("cc"+i);
//if(i%2==0){
// obj.style.backgroundColor="#F3F5FC"; 
// }else{
// obj.style.backgroundColor="#DDEAFE";
//}
 }
 function sav(){
 var myReg =/^(-?\d+)(\.\d+)?(e|E)?((-|\+)?\d+)?$/
 var kBase=taxDetailTableForm.k_base.value;
 if(kBase !=null && kBase!=''){
 
 if(!myReg.test(kBase)){
  alert("基数请输入数字!");
  return;
  }
   if(trim(kBase).substring(0,kBase.lastIndexOf(".")).length>15)
  {
     alert("基数的长度超出范围!");
     return;
  }
  } 
 var v="";
 var tab=document.getElementById("tab");
var i=tab.rows.length;
for(var j=2;j<=i;j++){
var obj=document.getElementsByName("aa"+j);
if(obj.length==0){
continue;
}
for(var h=0;h<obj.length;h++){
if(h==0||h==1||h==2||h==4||h==5){
if(obj[h].value!=null && obj[h].value!=''){
  if(!myReg.test(obj[h].value)){
   alert("请输入数字!");
   return;
   }
   if(obj[h].value.substring(0,obj[h].value.indexOf(".")).length>11)
   {
        alert("第["+(j-1)+"]行第["+(h+1)+"]列值输入过大，不予保存");
        return;
   }
}
}
if(h==6)
{
  if(obj[h].value.length>250)
  {
      alert("[说明]项输入内容超出长度范围!");
      return;
  }
}
if(obj[h].value==null||obj[h].value==''){
  v+="#"+"/";
}else{
  v += obj[h].value+"/";
  }
 }
 v += ",";
 }
  var income = "";
  var salaryid = document.getElementsByName("salaryid")[0].value;
  var itemid = document.getElementsByName("itemid")[0].value;
   var mode = "0";
  if(salaryid!=null&&salaryid.length>0){
  	income = document.getElementsByName("income")[0].value;
  	mode = document.getElementsByName("mode")[0].value;
  }

  var hashVo=new ParameterSet();
  var taxid="${taxDetailTableForm.taxid}";
  hashVo.setValue("recordStr",v);
  hashVo.setValue("income",income);
  hashVo.setValue("itemid",itemid);
  hashVo.setValue("mode",mode);
  hashVo.setValue("salaryid",salaryid);
  hashVo.setValue("taxid",taxid);
  hashVo.setValue("k_base",taxDetailTableForm.k_base.value);
  hashVo.setValue("param",taxDetailTableForm.param.value);
  
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:add_ok,functionId:'3020020006'},hashVo);			
       		 

 }
 function add_ok(outparamters){
  var taxid=outparamters.getValue("taxid");
  var salaryid = '${taxDetailTableForm.salaryid}';
  var itemid = '${taxDetailTableForm.itemid}';
  taxDetailTableForm.action="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid="+taxid
       				+"&salaryid="+salaryid+"&itemid="+itemid;
   taxDetailTableForm.submit();
  }
function ret(){
taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_rename=rename";
taxDetailTableForm.submit();
}


  function  deleteRow(taxid)   
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
     alert("请选择要删除的税率明细表!");
     return;
   }
   if(confirm("确定删除税率明细表吗?"))
     { 
       var tab=document.getElementById("tab");
       var i=tab.rows.length;
       var temp=ids.substring(1).split(",");
       for(var j=0;j<temp.length;j++)
       {
           tab.deleteRow(temp[j]-j);
           var bj=document.getElementsByName("bb");
           for(var i=0;i<bj.length;i++)
           {
               if((parseInt(bj[i].value)+j)>=parseInt(temp[j]))
                {
                    bj[i].value=bj[i].value-1;
                }
            }
       }
     

       
     }
  } 
  function delete_ok(outparameters)
  {
       var taxid=outparameters.getValue("taxid");
       var salaryid = '${taxDetailTableForm.salaryid}';
       var itemid = '${taxDetailTableForm.itemid}';
       taxDetailTableForm.action="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid="+taxid
       				+"&salaryid="+salaryid+"&itemid="+itemid;
       taxDetailTableForm.submit();
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
      if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();
      
      
  }
  function onRadioChange(changeradio){
  		if(changeradio==0){
     		document.getElementById("radioSalayid").innerHTML="收入额:"; 
     	}else if(changeradio==1){
     		document.getElementById("radioSalayid").innerHTML="实发额:"; 
     	}
     	document.getElementById("mode").value=changeradio;
  } 
  function allSelectCheck(obj)
  {
     var arr=document.getElementsByName("bb");
     if(arr)
     {
        for(var i=0;i<arr.length;i++)
        {
           if(obj.checked)
                arr[i].checked=true;
           else
               arr[i].checked=false;
        }
     }
  }
  var beforevalue="";
  function saveBeforeValue(obj)
  {
     beforevalue=obj.value;
  }
  function ctrl_xs(obj)
  {
      var xx=obj.value;
      if(xx.indexOf(".")!=-1)
      {
          var xs=xx.substring(xx.indexOf(".")+1);
          if(xs.length>3)
          {
             alert("税率最多允许输入三位小数！");
             obj.value=beforevalue;
          }
      }
  }
//-->
</script>
<html:form action="/gz/templateset/tax_table/initTaxDetailTable">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<html:hidden name="taxDetailTableForm" property="salaryid"/> 
<html:hidden name="taxDetailTableForm" property="itemid"/>
<html:hidden name="taxDetailTableForm" property="mode"/>
<table width="80%" border="0" cellspacing="0"  align="center" id='tab' cellpadding="0" class="ListTable">
   	  <thead>
	 <tr>
            <td align="left" class="TableRow" colspan='8' nowrap>
            	<table border="0" width="100%" cellspacing="0" cellpadding="0" height="100%">
            	<tr>
            		<td width="330" valign="bottom">
      					<bean:message key="gz.self.tax.basedata"/>&nbsp;<input type='text' name='k_base' value='${taxDetailTableForm.k_base}' style='width:110px;'/>
    					<bean:message key="gz.columns.taxmode"/>&nbsp;<hrms:optioncollection name="taxDetailTableForm" property="taxTypeList" collection="list" />
						<html:select name="taxDetailTableForm" property="param" size="1" style="width:110px">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>&nbsp;
					</td>
					<logic:notEqual name="taxDetailTableForm" property="salaryid" value="">
						<td valign="bottom">
							<html:radio name="taxDetailTableForm" property="mode" value="0" onclick="onRadioChange(0)"/>正算
							<html:radio name="taxDetailTableForm" property="mode" value="1" onclick="onRadioChange(1)"/>反算
						</td>
						<td  valign="bottom" id="radioSalayid">
							<logic:equal name="taxDetailTableForm" property="mode" value="0">
								<bean:message key="gz.columns.ynse1"/>:
							</logic:equal>
							<logic:equal name="taxDetailTableForm" property="mode" value="1">
								实发额&nbsp;
							</logic:equal>
						</td>
						<td valign="bottom">
							<hrms:optioncollection name="taxDetailTableForm" property="incomeList" collection="list1" />
							<html:select name="taxDetailTableForm" property="income" style="width:110px">
				 				<html:options collection="list1" property="dataValue" labelProperty="dataName"/>
							</html:select>&nbsp;
						</td>
					</logic:notEqual>
					<td>
						<input type='hidden' name='taxid' value='${taxDetailTableForm.taxid}'/> 
					</td>
				</tr>
				</table>
	   	 	 </td>
         </tr>
  
           <tr>
           			<td width='6%' align="center" class="TableRow" nowrap><input type="checkbox" name="allselect" onclick="allSelectCheck(this);"/> </td> 
           			<td width='14%' align="center" class="TableRow" nowrap><bean:message key="gz.columns.ynsd_dowm"/></td>
           			<td width='12%' align='center' class='TableRow' nowrap><bean:message key="gz.columns.ynsd_up"/></td>
           			<td width='8%' align='center' class='TableRow' nowrap><bean:message key="gz.columns.sl"/></td>
           			<td width='3%' align="center" class="TableRow" nowrap><bean:message key="gz.columns.taxflag"/></td>
           			<td width='1%' align="center" class="TableRow" nowrap><bean:message key="gz.columns.sskcs"/></td>
           			<td width='1%' align="center" class="TableRow" nowrap><bean:message key="gz.columns.kc_base"/></td>
           			<td width='10%' align="center" class="TableRow" nowrap><bean:message key="label.description"/></td>
           </tr>
      </thead>
   <% int i=0; %>
 <logic:iterate id="element" name="taxDetailTableForm" property="detailList" indexId="index"> 
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" id='<%="cc"+(index.intValue()+2)%>'>
          <%}
          else
          {%>
          <tr class="trDeep" id='<%="cc"+(index.intValue()+2)%>'>
          <%
          }
          %>
          <td width='6%' align="center" class="RecordRow" nowrap><input type='checkbox' name='bb' value='<%=i+2%>'/></td> 
          <td width='14%' align="center" class="RecordRow" nowrap><input style="text-align:right" type='text' size='12' onkeydown='ctrl_key(this);' id='<%="a"+(index.intValue()+2)+"_1"%>' name='<%="aa"+(index.intValue()+2)%>' value='<bean:write name='element' property='ynse_down'/>'/></td>
          <td width='12%' align="center" class="RecordRow" nowrap><input style="text-align:right" align="right" type='text' size='12' onkeydown='ctrl_key(this);' id='<%="a"+(index.intValue()+2)+"_2"%>' name='<%="aa"+(index.intValue()+2)%>' value='<bean:write name='element' property='ynse_up'/>'/></td>
          <td width='8%' align="center" class="RecordRow" nowrap><input onfocus="saveBeforeValue(this);" onblur="ctrl_xs(this);" style="text-align:right" align="right" type='text' size='8' onkeydown='ctrl_key(this);' id='<%="a"+(index.intValue()+2)+"_3"%>' name='<%="aa"+(index.intValue()+2)%>' value='<bean:write name='element' property='sl'/>'/></td>
          <td width='3%' align="center" class="RecordRow" nowrap>
			<select onkeydown='ctrl_key(this);' id='<%="a"+(index.intValue()+2)+"_4"%>' name='<%="aa"+(index.intValue()+2)%>'>
			<logic:equal name='element' property='flag' value='0'>
			<OPTION value='0' selected='selected'>上限封闭</OPTION>
			<OPTION value='1'>下限封闭</OPTION>
			</logic:equal>
			<logic:equal name='element' property='flag' value='1'>
			<OPTION value='0'>上限封闭</OPTION>
			<OPTION value='1' selected='selected'>下限封闭</OPTION>
			</logic:equal>
			</select>
			</td>
          <td width='1%' align="center" class="RecordRow"  nowrap><input style="text-align:right" type='text' onkeydown='ctrl_key(this);' size='12' id='<%="a"+(index.intValue()+2)+"_5"%>' name='<%="aa"+(index.intValue()+2)%>' value='<bean:write name='element' property='sskcs'/>'/></td>
          <td width='1%' align="center" class="RecordRow" nowrap><input  style="text-align:right" type='text'onkeydown='ctrl_key(this);'  size='12' id='<%="a"+(index.intValue()+2)+"_6"%>' name='<%="aa"+(index.intValue()+2)%>' value='<bean:write name='element' property='kc_base'/>'/></td>
          <td width='10%' align="center" class="RecordRow" nowrap><input type='text' size='15' onkeydown='ctrl_key(this);' name='<%="aa"+(index.intValue()+2)%>' id='<%="a"+(index.intValue()+2)+"_7"%>' value='<bean:write name='element' property='description'/>'/></td>
         <%
          i++;          
          %>
          
          </tr>
          </logic:iterate>
      </table>
  <table  width="80%" align="center">
<tr>
<td align="center">
<input type='button' name='new' value='<bean:message key="lable.tz_template.new"/>' class='mybutton' onclick='addRows();'>
<input type='button' name='save' value='<bean:message key="button.save"/>' class='mybutton' onclick='sav();'>
<input type="button" class="mybutton" value="删除" name="del" onclick="deleteRow('${taxDetailTableForm.taxid}');"/>
<logic:equal name="taxDetailTableForm" property="salaryid" value="">
<input type='button' name='return' value='<bean:message key="button.return"/>' class='mybutton' onclick='ret();'>
</logic:equal>
</td>
</tr>
</table>
<script type="text/javascript">
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
</script>  
</html:form>