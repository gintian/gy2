<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<style type="text/css">
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 13px; 
 PADDING-BOTTOM: 13px;
 BORDER-BOTTOM: #7b9ebd 1px solid
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 2px; 
 BORDER-BOTTOM: #7b9ebd 1px solid
}
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 line-height:17px;
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid
}
</style>
<script language="javascript">
var targetArr="";
function change(){
    var fieldname=document.getElementById("fieldname").value;
	var in_paramters="fieldname="+getEncodeStr("3-"+fieldname);
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1602010219'});
	var itemid = checkitemid();
	targetArr="";
	if(itemid!=null){
		toggles("delformula_brilliant");	
		hides("delformula_dark");
		toggles("saveformula_dark");	
    	hides("saveformula_brilliant");
	}else{
		hides("delformula_brilliant");	
		toggles("delformula_dark");
		toggles("saveformula_dark");	
    	hides("saveformula_brilliant");
	}
}
function showFieldList(outparamters){
		var usedlist=outparamters.getValue("usedlist");
		var itemid = outparamters.getValue("itemid");
		AjaxBind.bind(projectForm.left_fields,usedlist);
		var lefts= document.getElementsByName("left_fields");
		if(lefts==null){
			return;
		}else{
			var left_vo = lefts[0];
			for(var i=0;i<left_vo.options.length;i++){
				if(left_vo.options[i].value==itemid){
					left_vo.options[i].selected=true;
					break;
				}
			}
		}
}
function showDelFieldList(outparamters){
	var usedlist=outparamters.getValue("usedlist");
	var itemid = outparamters.getValue("itemid");
	AjaxBind.bind(projectForm.left_fields,usedlist);
	var lefts= document.getElementsByName("left_fields");
	toggles("saveformula_dark");	
    hides("saveformula_brilliant");
    targetArr="";
	if(lefts==null){
		return;
	}else{
		var left_vo = lefts[0];
		if(left_vo.options.length>0){
			for(var i=0;i<left_vo.options.length;i++){
				left_vo.options[i].selected=true;
				break;
			}
		}else{
			hides("delformula_brilliant");	
			toggles("delformula_dark");
		}
	}
}
function addproject(){
    var fieldsetid = document.getElementById("fieldname").value;
    var thecodeurl ="/org/autostatic/mainp/addProject.do?b_query=link&fieldsetid="+fieldsetid+"&type=3"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:350px; dialogHeight:230px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo.length>1){
		var fieldname = document.getElementById("fieldname").value;
		targetArr=return_vo;
		var itemArr = return_vo.split("::");
		
		var fileitem ="fieldname="+getEncodeStr(fieldname+"-"+return_vo+"-3");
		var request=new Request({method:'post',asynchronous:false,parameters:fileitem,onSuccess:showFieldList,functionId:'1602010212'});
  		toggles("saveformula_brilliant");	
		hides("saveformula_dark");
		toggles("delformula_brilliant");	
		hides("delformula_dark");
  	}else{
  		return ;
  	}
}
function delProject(){
	var fielditemid=checkfieldid();
    if(fielditemid==null){
		alert("<bean:message key='org.autostatic.mainp.select.project'/>");
		return;
	}
	var desc=checkfielddesc();	
	if(confirm("<bean:message key='org.autostatic.mainp.del.summary.pro'/>"+desc+"?")){
		var arr = targetArr.split("::");
		var checkflag="1";
		if(arr.length==5){
			if(arr[0].toLowerCase()==fielditemid.toLowerCase()){
				checkflag="0";
			}
		}
		if(checkflag=="1"){
    		var fieldname=document.getElementById("fieldname").value;
			var in_paramters="fieldname="+getEncodeStr(fieldname+"-"+fielditemid+"-3");
   			var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showDelFieldList,functionId:'1602010206'});
		}else{
			change();
		}
	}
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
function selectmethod(){
	var fielditemid=checkitemid();
    if(fielditemid==null){
		hides("delformula_brilliant");	
		toggles("delformula_dark");
	}else{
		toggles("delformula_brilliant");	
		hides("delformula_dark");
    }
} 	
function savemula(){
	var fielditemid=checkfieldid();
	if(fielditemid==null){
		alert("<bean:message key='org.autostatic.mainp.select.project'/>");
		return;
	}
	var infor = fielditemid+",3::-" ;
	if(targetArr.length>5){
		infor = fielditemid+",3::-"+targetArr ;
	}
	var pars="formula="+getEncodeStr(infor);  
    var request=new Request({method:'post',asynchronous:false,parameters:pars,functionId:'1602010211'});
    toggles("saveformula_dark");	
    hides("saveformula_brilliant");
    targetArr="";
} 
function checkfieldid(){
	var fielditemid;
	var lefts= document.getElementsByName("left_fields");
	if(lefts==null){
		return;
	}else{
		var left_vo = lefts[0];
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				fielditemid =left_vo.options[i].value;
				break;
			}
		}
		if(fielditemid!=null){
			return fielditemid;
		}else{
			return;
		}
	}
}
function checkitemid(){
	var fielditemid;
	var lefts= document.getElementsByName("left_fields");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else{
		for(var i=0;i<left_vo.options.length;i++){
			fielditemid =left_vo.options[i].value;
			break;
		}
		if(fielditemid!=null){
			return fielditemid;
		}else{
			return;
		}
	}
}	
function checkfielddesc(){
	var desc;
	var lefts= document.getElementsByName("left_fields");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else{
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				desc =left_vo.options[i].text;
				continue;
			}
		}
		if(desc!=null){
			return desc;
		}else{
			return;
		}
	}
}  	
function sortItem(chsort){
	var fielditemid="";
	var nextitemid="";
	var affteritemid="";
	var lefts= document.getElementsByName("left_fields");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else if(left_vo.options.length<2){
		return;
	}else{
		var chflag="0";
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				fielditemid =left_vo.options[i].value;
				chflag="1";
			}else{
				if(chflag=="1"){
					nextitemid = left_vo.options[i].value;
					break;
				}
				affteritemid = left_vo.options[i].value;
			}
		}
		if(chsort=="down"&&(nextitemid==null||nextitemid.length<1)){
			return;
		}
		if(chsort=="up"&&(affteritemid==null||affteritemid.length<1)){
			return;
		}
		
		var hashvo=new ParameterSet();
		hashvo.setValue("itemid",fielditemid);
		hashvo.setValue("sortitem",chsort);
		hashvo.setValue("nextitemid",nextitemid);
		hashvo.setValue("affteritemid",affteritemid);
		var request=new Request({method:'post',asynchronous:false,functionId:'1602010227'},hashvo);		
	}
}		 	  	 			
</script>

<html:form action="/org/autostatic/mainp/calculation">
<table border="0">
 <tr><td>&nbsp;</td></tr> 
</table>

<table width="100%" height="352" border="0">
  <tr> 
    <td width="80%" rowspan="4" align="center">
     <fieldset align="center" style="width:100%;height:100%;margin: 0 0 0 10;">
      <legend><bean:message key="org.maip.project.summary"/></legend>
      <table width="100%" height="334" border="0">
        <tr> 
          <td height="30" colspan="4">
			<html:select name="projectForm" property="fieldname" style="width:250" onchange="change();">
			 		<html:optionsCollection property="fieldlist" value="dataValue" label="dataName" />
			</html:select>
		</td>
		<td rowspan="2" valign="middle">
		  	 <table width="100%"  border="0">
		  	 <tr>
		  	 	<td>
		  			<input type="button" value="上移" onclick="sortItem('up');upItem($('left_fields'));" Class="mybutton">
		  		</td>
		  	</tr><tr><td>&nbsp;</td></tr>
		  	<tr>
		  	 	<td>
		  			<input type="button" value="下移" onclick="sortItem('down');downItem($('left_fields'));" Class="mybutton">
		  		</td>
		  	</tr>
		  	</table>
		  </td>
        </tr>
        <tr> 
          <td height="271" colspan="4">
          	<bean:message key="conlumn.investigate_item.name"/><br>
			<select name="left_fields" multiple="multiple" onclick="selectmethod();" style="height:270px;width:100%;font-size:9pt">
             </select>
		  </td>
        </tr>
        <tr>
        	<td width="53%" align="right">
        		<span id='saveformula_brilliant'>
          		<input name="saveformula" type="button" id="saveformula" value=' <bean:message key="button.save"/> ' onclick="savemula();" Class="mybutton">
            	</span>
          		<span id='saveformula_dark'> 
          		<input name="saveformula" type="button" id="saveformula" value=' <bean:message key="button.save"/> ' Class="mybutton" disabled="disabled"> 
          		</span>
        	</td>
        	<td align="center">
        		<input name="newproject" type="button" id="newproject" value=' <bean:message key="button.insert"/> ' onclick="addproject();" Class="mybutton">
        	</td>
        	<td>
        	<span id='delformula_brilliant'>
      			<input name="delproject" type="button" id="delproject" value=' <bean:message key="button.delete"/> ' onclick="delProject();" Class="mybutton">
     		</span>
          	<span id='delformula_dark'> 
          		<input name="delproject" type="button" id="delproject" value=' <bean:message key="button.delete"/> ' Class="mybutton" disabled="disabled">
     		</span>
     		</td>
     		<td width="31%">&nbsp;</td>
        </tr>
      </table>
      </fieldset>		
     </td>
  </tr>
</table>
<script language="javascript">
change();
hides("saveformula_brilliant");	
</script>
</html:form>