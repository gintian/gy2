<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript" language="javascript">
function isNum(i_value)
  {
  	re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
  }
function validateData(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
 		return;
 	}
}
function mincrease1(obj_name){
	var obj=eval("document.dbinitForm."+obj_name);
	var d = obj.value;
	var s = isNaN(d);
	if(s){
		alert(KJG_ZBTX_INFO2);
		return;
	}
	if(obj.value<9999){
	obj.value = obj.value*1+1;
	}
	
}
 function msubtract1(obj_name)
  { 
  		var obj=eval("document.dbinitForm."+obj_name);
  		if(obj.value>1)
			obj.value = obj.value*1-1;
		
  }
function sub(chk){
	n=document.all.itemtype.options.length;
	if (n>0){
		for (i=0;i<n;i++){
			document.all.itemtype.options[i].selected=chk;
		} 
	}
	return;
}
function subs(sourcebox_id){
	var cole= document.getElementById("usedata").value;
	 if(cole==null||cole==0){
	 	alert(KJG_ZBTX_INFO3);
	 	return;
	 }
	var left_vo,vos;
	vos= document.getElementsByName(sourcebox_id);
	  if(vos==null)
	  		return false;
	  left_vo=vos[0];
	  var set="";
	  var num=0;
	  	for(i=0;i<left_vo.options.length;i++)
	  		{
	  	 		if(left_vo.options[i].selected)
		    		{
		    			set+="/"+left_vo.options[i].value;
		    			num++;
		    			
		    		}
	  		}
	  		
	  		if(num==0)
	  		{
	  		 alert(KJG_ZBTX_INFO4); 
	  		 return;     
	  		}
	  var usename = "B";
	  var udata  = document.getElementById("usedata").value;
	  var usefy = document.getElementsByName("usefy")[0].checked;
	  if(usefy==true){
	  	usefy='1';
	  }
	  if(usefy==false){
	  	usefy='0';
	  }
	  var hashvo=new ParameterSet();
	  hashvo.setValue("set",set.substring(1));
	  hashvo.setValue("usename",usename);
	  hashvo.setValue("udata",udata); 
	  hashvo.setValue("num",num);
	  hashvo.setValue("usefy",usefy);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk,functionId:'1020010129'},hashvo);
}
function returnExportOk(outparameters)
	{
		var outName=outparameters.getValue("outName");

		var name=outName.substring(0,outName.length-1)+".xls";
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
	}
function back(){
	window.location="/system/dbinit/fieldset_tree.jsp";
}
</script>
<html:form action="/system/dbinit/unitsgather">
<table width="100%" height='100%' align="center">
	<table>
	<tr><td width='400' >
		<tr> <td class="framestyle" valign="top"><Br>
		<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
  								<tr>
  								&nbsp&nbsp&nbsp&nbsp&nbsp<bean:message key="kjg.gather.xuanzeziji"/>:
  								<td>
  									<fieldset align="center" style="width:90%;">
  										<html:select styleId="itemtype" name="dbinitForm" property="unitsid" multiple="multiple" style="height:140px;width:90%;font-size:10pt">
  											<html:optionsCollection property="unitslist"
													value="dataValue" label="dataName" />
  								
  										</html:select>
  									</fieldset>
  								</td>
  								</tr>
  								<tr>
  									<td align="left"  nowrap>
  										&nbsp&nbsp&nbsp&nbsp<input type="checkbox" name="usefy"
  											<logic:equal name="dbinitForm" property="usefy" value="1">
													checked="true"
											</logic:equal>
  										>
  										<bean:message key="kjg.gather.anzijifenye"/>
  									</td>
  								</tr>
  								<tr><td width="100%" height="100%">
  									<table><tr>
  									<td>&nbsp&nbsp&nbsp&nbsp<bean:message key="kjg.gather.codegeshu"/></td>
  									<td valign="middle">
  										<html:text property="usedata" name="dbinitForm"  onkeyup='validateData(this)' maxlength="4" size="5" styleId="usedata"/>
  									</td>
  									<td valign="middle" align="left">
  										<table border="0" cellspacing="2" cellpadding="0">
											<tr><td><button id="m_up" class="m_arrow" onclick="mincrease1('usedata');">5</button></td></tr>
											<tr><td><button id="m_down" class="m_arrow" onclick="msubtract1('usedata');">6</button></td></tr>
										</table>		
  									</td>
  									<td><bean:message key="kjg.gather.chaoguo"/></td>
  									</tr></table>
  								</td></tr>
	</table>
	</td>
	<td valign='bottom' ><br><br>
  				&nbsp; &nbsp;<input type='button' value='<bean:message key="kjg.title.selectall"/>' onclick='sub(true)'  class="mybutton"  >
   			<br>
   				&nbsp; &nbsp;<input type='button' value='<bean:message key="kjg.title.run"/>' onclick=subs('unitsid')  class="mybutton"  >
   			<br>
  				&nbsp;&nbsp; <input type='button' value='<bean:message key="kjg.title.cancel"/>' onclick='back()'  class="mybutton"  >
   			</td>
	</tr>
	</table>
</html:form>
