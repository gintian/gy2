<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<style type="text/css"> 
.btn {
    background-image:url(/images/button.jpg);	
	background-position : center;
	background-repeat:repeat; 
	font-size:12px;
	border:#0042A0 1px solid;
 	vertical-align : center;	
 	BORDER-RIGHT: url(/images/button.jpg) 12px solid;
 	PADDING-RIGHT: 12px;
 	PADDING-LEFT: 11px; FONT-SIZE: 12px; 
 	BORDER-LEFT: url(/images/button.jpg) 11px solid; 	
	cursor: hand ; 
}
#scroll_box {
           border: 1px solid #eee;
           height: 240px;    
           width: 260px;            
           overflow: auto;            
           margin: 1em 0;
       }
</style>
<script language="javascript"> 
function setFormuls(){
  	var thecodeurl="/general/inform/informcheck/setformula.do?b_query=link&infor=${auditForm.infor}";
    var return_vo= window.showModalDialog(thecodeurl, "", 
    	"dialogWidth:800px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	var infor="${auditForm.infor}";
    	var hashvo=new ParameterSet();
		hashvo.setValue("infor",infor);
		var request=new Request({method:'post',asynchronous:false,onSuccess:getTableStr,functionId:'1010090005'},hashvo);	
    }
}
function getTableStr(outparamters){
	var tablestr=outparamters.getValue("tablestr");
	document.getElementById("scroll_box").innerHTML=getDecodeStr(tablestr);
	var obj = document.getElementsByTagName("input");
	for(var i=0;i<obj.length;i++){
		if(obj[i].type=="radio"){
			if(obj[i].value=="1")
				obj[i].checked=true;
		}
	}
}
function selectAll(){
	var vo= document.getElementsByTagName("input");
	for(var i=0;i<vo.length;i++){
		if(vo[i].type=="checkbox")
			vo[i].checked=true;
	}
}
function clearAll(){
	var vo= document.getElementsByTagName("input");
	for(var i=0;i<vo.length;i++){
		if(vo[i].type=="checkbox")
			vo[i].checked=false;
	}
}
function selectId(){
	var itemid="";
	var vo= document.getElementsByTagName("input");
	for(var i=0;i<vo.length;i++){
		if(vo[i].type=="checkbox"){
			if(vo[i].checked==true){
				itemid+=vo[i].value+",";
			}
				
		}
	}
	return itemid;
}

function printWord(){
    var infor = "${auditForm.infor}";
    var dbname = "${auditForm.dbname}";
    var itemid = selectId();
    if(itemid.length<1){
    	alert(INFOR_APP_NO_RECORD+"!");
    	return false;
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("infor",infor);
    hashvo.setValue("dbname",dbname);
    hashvo.setValue("itemid",itemid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'1010090008'},hashvo);	
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	if(outName=='no'){
		var info=outparamters.getValue("info");
		alert(getDecodeStr(info));
		return;
	}
		
	window.location.target="_blank";
//	window.location.href="/servlet/DisplayOleContent?filename="+outName;
	//20/3/17 xus vfs改造
	window.location.href="/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
}
</script>
<html:form action="/general/inform/informcheck/view_check">
<table width="100%" border="0">
  <tr> 
    <td width="65%" height="270" valign="top">
    	<fieldset align="center" style="width:100%;">
		<legend><bean:message key='workdiary.message.select.app.formula'/></legend> 
      		<table width="100%" height="250" border="0">
      			<tr>
      				<td valign="top"><div id="scroll_box">${auditForm.tablestr}</div></td>
      			</tr>
      		</table>
      	</fieldset>
    </td>
    <td width="25%" align="center" valign="top"> 
      <table width="97%" border="0">
        <tr> 
          <td height="40" align="center"> 
            <input type="button" name="button1" value="<bean:message key='infor.menu.definition.formula'/>" onclick="setFormuls();" Class="mybutton" style="width: 73px;">
          </td>
        </tr>
        <tr>
          <td height="40" align="center"> 
            <input type="button" name="button2" value="<bean:message key='workdiary.message.app.start'/>" onclick="printWord();" Class="mybutton" style="width: 73px;">
          </td>
        </tr>
        <tr>
          <td height="40" align="center"> 
            <input type="button" name="button3" onclick="window.close();" value="<bean:message key='button.cancel'/>" Class="mybutton" style="width: 73px;">
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
  	<td colspan="2">
  		<input type="radio" name="selectall" onclick="selectAll();" value="1" checked><bean:message key='button.all.select'/>
  		<input type="radio" name="selectall" onclick="clearAll();" value="0"><bean:message key='button.all.reset'/>
  	</td>
  </tr>
</table>
</html:form>
