<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style type="text/css"> 
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
  border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
</style>
<script language='javascript'>
function styleRoster(obj){
	var hashvo=new ParameterSet();
	hashvo.setValue("styleid",obj.value);				
	var request=new Request({method:'post',asynchronous:false,onSuccess:setTab,functionId:'1010095000'},hashvo);
}
function setTab(outparamters){
	var tablist=outparamters.getValue("tablist");
	AjaxBind.bind(outPrintForm.tabid,tablist);
}
function delStyleRoster(){
	var styleid = document.getElementById("styleid").value;
	var tabid = document.getElementById("tabid").value;
	if(tabid==null||tabid.length<1){
		return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);	
	hashvo.setValue("styleid",styleid);	
	hashvo.setValue("a_inforkind","${outPrintForm.inforkind}");	
	hashvo.setValue("check","del");					
	var request=new Request({method:'post',asynchronous:false,onSuccess:setTab,functionId:'1010095000'},hashvo);
}
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function viewDel(){
	toggles("delview");
	toggles("openview");
	toggles("resetview");
	hides("delhide");
	hides("openhide");
	hides("resethide");
}
function hideDel(){
	hides("delview");
	hides("openview");
	hides("resetview");
	toggles("delhide");
	toggles("openhide");
	toggles("resethide");
}
function openStyleRoster(){
	var tabid = document.getElementById("tabid").value;
	if(tabid==null||tabid.length<1){
		return;
	}
	var dbname = "${outPrintForm.dbname}"; 
	var thecodeurl ="/general/inform/emp/output/iframroster.do?b_query=link&tabid="+tabid+"&dbpre="+dbname+"&a_inforkind=${outPrintForm.inforkind}&flag=${outPrintForm.result}&checktype=open"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:900px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;");
}
function setStyleRoster(){
	var thecodeurl ="/general/inform/emp/output/iframroster.do?b_query=link&dbpre=${outPrintForm.dbname}&a_inforkind=${outPrintForm.inforkind}&flag=${outPrintForm.result}&checktype=set"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:900px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;");
}
function resetStyleRoster(){
	var tabid = document.getElementById("tabid").value;
	if(tabid==null||tabid.length<1){
		return;
	}
	var dbname = "${outPrintForm.dbname}"; 
	var thecodeurl ="/general/inform/emp/output/iframroster.do?b_query=link&tabid=";
	thecodeurl+=tabid+"&dbpre="+dbname
	thecodeurl+="&a_inforkind=${outPrintForm.inforkind}&flag=${outPrintForm.result}&checktype=reset&a_code=${outPrintForm.a_code}"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:900px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;");
}
</script>
<html:form action="/general/inform/emp/output/printroster">
<table width="100%" border="0">
<tr><td>&nbsp;</td></tr>
</table>
<fieldset align="center" style="width:90%;">
<legend><bean:message key="label.muster.title"/></legend>
<table width="100%" border="0">
  <tr> 
    <td align="center">
     	<html:select name="outPrintForm" onclick="hideDel();" onchange="styleRoster(this);" multiple="multiple" property="styleid" style="height:220px;width:80%;font-size:9pt"> 
      		<html:optionsCollection property="stylelist" value="dataValue" label="dataName"/> 
      	</html:select>
    </td>
    <td width="50%" align="center">
		<hrms:optioncollection name="outPrintForm" property="tablist" collection="list" /> 
      	<html:select name="outPrintForm" property="tabid" onclick="viewDel();" ondblclick="openStyleRoster();" multiple="multiple" style="height:220px;width:90%;font-size:9pt"> 
      		<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
      	</html:select>
	</td>
  </tr>
</table>
</fieldset>
<table width="30%" height="30" border="0" align="center">
<tr> 
    <td height="30" colspan="2" align="center"> 
      <table width="35%" border="0">
        <tr> 
          <td align="center"> 
          		<input type="button" name="newbutton" onclick="setStyleRoster();" class="mybutton" value="<bean:message key='lable.tz_template.new'/>">
          </td>
          <td align="center"> 
          	<span id="openview" style="display=none">
          		<input type="button" name="Submit" onclick="openStyleRoster();" class="mybutton" value="<bean:message key='button.open'/>">
          	</span>
          	<span id="openhide">	
          		<input type="button" name="Submit" class="btn3" value="<bean:message key='button.open'/>">
          	</span>
          </td>
           <td align="center"> 
           		<span id="resetview" style="display=none">
          			<input type="button" name="rebutton" onclick="resetStyleRoster();" class="mybutton" value="<bean:message key='button.fillout'/>">
          		</span>
          		<span id="resethide">
          			<input type="button" name="rebutton"  class="btn3" value="<bean:message key='button.fillout'/>">
          		</span>
          </td>
          <td align="center">
            <span id="delview" style="display=none">
          		<input type="button" name="Submit3" onclick="delStyleRoster();" class="mybutton" value="<bean:message key='button.setfield.delfield'/>">
          	</span>
          	<span id="delhide">
          		<input type="button" name="Submit4" class="btn3" value="<bean:message key='button.setfield.delfield'/>">
          	</span>
          </td>
          <td align="center"><input name="Submit2" onclick="window.close();" class="mybutton" type="button" value="<bean:message key='lable.welcomeboard.close'/>"></td>
        </tr>
      </table></td>
  </tr>
</table>
</html:form>
