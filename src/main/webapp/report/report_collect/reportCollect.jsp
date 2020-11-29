<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/report/report_collect/reportCollect.js"></SCRIPT>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript'>
var unitcodelist=new Array();
var unitcode="${reportCollectForm.unitcode}";


function change()
{
	var a_object=eval("document.reportCollectForm.sortid");
	var In_paramters="sortid="+a_object.value; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnChangeSort,functionId:'03030000023'});

}



function returnChangeSort(outparamters)
{
	var tab_str=getDecodeStr(outparamters.getValue("tab_str"));
	var unit_str=getDecodeStr(outparamters.getValue("unit_str"));
	var tabArray=new Array();
	if(tab_str.length>0)
	{
		if(tab_str.indexOf("#")==-1)
		{
			tabArray[0]=tab_str;
		}
		else
			tabArray=tab_str.split("#");
		vos=eval("document.reportCollectForm.tabid");;
		for(var i=vos.options.length-1;i>=0;i--)
		{
			vos.options.remove(i);
		}
		for(var i=0;i<tabArray.length;i++)
		{
			var temp=tabArray[i].split("~");
			var o=new Option();
			o.value=temp[0]+"§"+temp[2]+"§"+temp[1];
			o.text=temp[0]+"."+temp[2];
			vos.options[vos.options.length]=o;
		}

	}
	
	var unitArray=new Array();
	if(unit_str.length>0)
	{
		if(unit_str.indexOf("#")==-1)
		{
			unitArray[0]=unit_str;
		}
		else
			unitArray=unit_str.split("#");
			
		vos=eval("document.reportCollectForm.unitCode");;
		for(var i=vos.options.length-1;i>=0;i--)
		{
			vos.options.remove(i);
		}
		for(var i=0;i<unitArray.length;i++)
		{
			var temp=unitArray[i].split("~");
			var o=new Option();
			o.value=temp[0]+"§"+temp[1];
			o.text=temp[0]+":"+temp[1];
			vos.options[vos.options.length]=o;
		}
	}
	else
	{
		vos=eval("document.reportCollectForm.unitCode");;
		for(var i=vos.options.length-1;i>=0;i--)
		{
			vos.options.remove(i);
		}
	}
	
}






function reportInnerCheck(){
	var unitcodeArray=new Array();
	var tabArray = new Array();
	var unitcodes = "";
	var tabids = "";
	
	unitcodeArray=getSelectInfos("unitCode");
	if(unitcodeArray.length==0)
	{
		alert(REPORT_INFO37+"！");
		return;
	}else{
		for(var i = 0 ; i< unitcodeArray.length; i++){
			var temp = unitcodeArray[i];
			utarray = temp.split("§");
			unitcodes += utarray[0];
			unitcodes += ",";
		}	
	}
	
	tabArray=getSelectInfos("tabid");
	if(tabArray.length==0)
	{	
		alert(REPORT_INFO42+"！");
		return;
	}else{
		for(var i = 0 ; i< tabArray.length; i++){
			var temp1 = tabArray[i];
			tbarray = temp1.split("§");
			tabids += tbarray[0];
			tabids += ",";
		}	
	}
	reportCollectForm.action="/report/edit_collect/reportCollect.do?b_inneranalyse=link&unitcodes="+unitcodes+"&tabids="+tabids;
	reportCollectForm.submit();
}

function reportSpaceCheck(){
	var unitcodeArray=new Array();
	var tabArray = new Array();
	var unitcodes = "";
	var tabids = "";
	
	unitcodeArray=getSelectInfos("unitCode");
	if(unitcodeArray.length==0)
	{
		alert(REPORT_INFO37+"！");
		return;
	}else{
		for(var i = 0 ; i< unitcodeArray.length; i++){
			var temp = unitcodeArray[i];
			utarray = temp.split("§");
			unitcodes += utarray[0];
			unitcodes += ",";
		}	
	}
	
	tabArray=getSelectInfos("tabid");
	if(tabArray.length==0)
	{	
		alert(REPORT_INFO42+"！");
		return;
	}else{
		for(var i = 0 ; i< tabArray.length; i++){
			var temp1 = tabArray[i];
			tbarray = temp1.split("§");
			tabids += tbarray[0];
			tabids += ",";
		}	
	}
	reportCollectForm.action="/report/edit_collect/reportCollect.do?b_spaceanalyse=link&unitcodes="+unitcodes+"&tabids="+tabids;
	reportCollectForm.submit();
}

function hiddencheck(){
	var waitInfo=eval("reportcheck");
	waitInfo.style.display="none";
	var obj=$('operater');
	var waitInfo=eval("posbutton");
	for(var i=0;i<obj.length;i++){
		if(obj[i].checked){
			if(obj[i].value==2||obj[i].value==6){
				if(obj[i].value==2){
					waitInfo.innerHTML="<input type=\"button\" name=\"position\" value=\"设置汇总条件\" onclick=\"simpleCollect()\" class=\"mybutton\">";
				}else{
					waitInfo.innerHTML="<input type=\"button\" name=\"position\" value=\"设置汇总条件\" onclick=\"complexCollect()\" class=\"mybutton\">";
				}
				if(waitInfo.style.display=="none")
					waitInfo.style.display="inline";
			}else{
				if(waitInfo.style.display!="none"){
					waitInfo.style.display="none";
				}
			}
		}
	}
}

function showcheck(){
	var waitInfo=eval("reportcheck");
	waitInfo.style.display="inline";
	var waitInfo=eval("posbutton");
	if(waitInfo.style.display!="none"){
		waitInfo.style.display="none";
	}
}



function resets()
{
	var obj=$('tabid');
	for(var i=0;i<obj.options.length;i++)
	{
		obj.options[i].selected=false;
	}
	
	var obj2=$('operater');
	var avalue;
	for(var i=0;i<obj2.length;i++)
	{
		if(obj2[i].checked)
			avalue=obj2[i].value;
	}
	if(avalue=='1')
	{
		var obj3=$('unitCode');
		for(var i=0;i<obj3.options.length;i++)
		{
			obj3.options[i].selected=false;
		}
	
	}
	
}

</script>

<style type="text/css">
	
</style>
<hrms:themes/>
<html:form action="/report/edit_collect/reportCollect">	
<logic:notEqual name="reportCollectForm" property="isLeafUnit" value="1">
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr> 
    <td valign="top"> <form action="/kq/options/kq_rest.do" method="post" enctype="multipart/form-data" name="kqRestForm">
 		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
 		<tr>
 		<td>
        <fieldset align="center" style="width:700px;margin:auto;">
        <legend ><bean:message key="menu.report"/><bean:message key="report_collect.collect"/></legend>
        <table width="93%" border="0"  align="center" cellpadding="0" cellspacing="0" style="padding-bottom:6px">
          <tr > 
            <td width="671">
                <input name="operater" type="radio" value="1" checked onClick="Element.show('sss');showcheck();"><bean:message key="report_collect.someDirectSubUnitCollect"/>
                <input type="radio" name="operater" value="2" onClick="Element.hide('sss');simpleCollect();hiddencheck();"><bean:message key="report_collect.simpleConditionCollect"/>
                <input type="radio" name="operater" value="3" onClick="Element.hide('sss');hiddencheck();"><bean:message key="report_collect.allLeafUnitCollect"/>
                <input type="radio" name="operater" value="4" onClick="Element.hide('sss');hiddencheck();"><bean:message key="report_collect.compare"/>
			</td>
          </tr>
          <tr > 
            <td>
                <input type="radio" name="operater" value="5" onClick="Element.hide('sss');hiddencheck();"><bean:message key="report_collect.allDirectSubUnitCollect"/>
                <input type="radio" name="operater" value="6" onClick="Element.hide('sss');complexCollect();hiddencheck();"><bean:message key="report_collect.complexConditionCollect"/>
                <input type="radio" name="operater" value="7" onClick="Element.hide('sss');hiddencheck();"><bean:message key="report_collect.layersCollect"/>
              </td>
          </tr>
          <tr id="sss"> 
            <td align="center"> 
            	<select name="unitCode" size="11" multiple style="width:100%">
	               <logic:iterate id="element" name="reportCollectForm" property="underUnitList"  > 
	               	 	<option value='<bean:write name="element" property="unitcode" />§<bean:write name="element" property="unitname" />' ><bean:write name="element" property="unitcode" />:<bean:write name="element" property="unitname" /></option>             
	               </logic:iterate>
             	 </select>
             	 
              </td>
             
             
             
          </tr>
          <tr>
          	<td align="left" height='40'width="10%">
          	<br>
          	 	<hrms:optioncollection name="reportCollectForm" property="sortIdList" collection="list" />
	             <html:select name="reportCollectForm" property="sortid" size="1" onchange="change();">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	       	 </html:select>&nbsp;&nbsp;
          	 <div id="posbutton" style="display:none">
             
              </div>
          	</td>
          </tr>
          <tr > 
            <td align="center">
             <select name="tabid" size="14" multiple style="width:100%">
              	<logic:iterate id="element" name="reportCollectForm" property="tableList"  > 
             		<OPTION  value='<bean:write name="element" property="tabid" />§<bean:write name="element" property="name" />§<bean:write name="element" property="tsortid" />' > <bean:write name="element" property="tabid" />: <bean:write name="element" property="name" /></OPTION>
         		</logic:iterate>
              </select> 
              <br/>
            </td>
          </tr>
          <tr>
          </tr>
        </table>
        </fieldset>
        </td>
        </tr>
        <tr> 
            <td align="center" valign="top" style="height:35px;padding-top: 3px;"> 
				<input type="button" name="select" value="<bean:message key="label.query.selectall"/>"  onclick='selectAll("tabid")'  class="mybutton">
				<input type="button" name="b_update2" value="<bean:message key="report_collect.collect"/>"   onclick="collect('aa')" style="margin-left: -2px;" class="mybutton"> 
				<input name="reset" type="button" onclick='resets()' class="mybutton" style="margin-left: -2px;" value="<bean:message key="button.clear"/>">          	
             	<div id="reportcheck" style="display:inline;">
             	<input type="button" name="reportinnercheck" value="<bean:message key="reportlist.reportinnercheck"/>"  class="mybutton" style="margin-left: -2px;" onClick="reportInnerCheck()">
             	<input type="button" name="reportspacecheck" value="<bean:message key="reportlist.reportspacecheck"/>" class="mybutton" style="margin-left: -2px;" onClick ="reportSpaceCheck()">
             	</div> 	
             	<hrms:priv func_id="2903101">
             	<input type="button" name="compute" value="<bean:message key="report.batchCompute"/>"  class="mybutton" style="margin-left: -2px;" onClick="batchCompute('${reportCollectForm.unitcode}')"> 
             	</hrms:priv>  
             	<hrms:tipwizardbutton flag="report" target="il_body" formname="reportCollectForm"/>
              </td>
          </tr>
        </table>
      </form></td>
  </tr>
</table>
</logic:notEqual>
<logic:notEqual name="reportCollectForm" property="isLeafUnit" value="0">
<br>
<br>
<table align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td align='center'>

	<bean:message key="report_collect.info"/>！
	</td>
	</tr>
</table>

</logic:notEqual>
</html:form>
