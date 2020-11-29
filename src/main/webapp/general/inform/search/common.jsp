<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript">
var id="${searchInformForm.id}";
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
			//判断ie11以下版本
var isIE = (((userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1) 
		//判断ie11
		|| (userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1))&& !isOpera); 
var paraArray; 
if(!isIE){
	if(window.parent.window.opener&&window.parent.window.opener.window)		
		paraArray=window.parent.window.opener.window.theArr;
}else{
	paraArray=dialogArguments;
}
function change(obj){
	var item = obj.value;
	if(item!=null&&item.length>0){
		var arr = item.split(":");
		if(arr.length==2){
			document.getElementsByName("id")[0].value=arr[0];
			document.getElementsByName("name")[0].value=arr[1];
		}
	}
}
function setId(obj){
	obj.value=id;
	document.getElementsByName("name")[0].value="";
}
function saveCond(){
	var ids = document.getElementsByName("id")[0].value;
	var name = document.getElementsByName("name")[0].value;
	if(name==null||name.length<1){
		alert("请输入条件名称!");
		return false;
	}
	if(paraArray.length!=5)
		return;
	
	var hashvo=new ParameterSet();
	hashvo.setValue("lexpr",paraArray[0]);
	hashvo.setValue("factor",getEncodeStr(paraArray[1]));
	hashvo.setValue("type",paraArray[2]);
	hashvo.setValue("like",paraArray[3]);
	hashvo.setValue("history",paraArray[4]);
	hashvo.setValue("id",ids);
	hashvo.setValue("name",name);
	if(ids==id){
		hashvo.setValue("flag","insert");
	}else{
		hashvo.setValue("flag","alert");
	}
	var request=new Request({method:'post',asynchronous:false,onSuccess:checkSave,functionId:'3020110076'},hashvo);		
}
function checkSave(outparamters){
	var check=outparamters.getValue("check");
	var id=outparamters.getValue("id");
	if(check=='ok'){
		if(isIE){
			window.returnValue=id;
			window.close();	
		}else{
			if(parent.opener){
				parent.opener.returnVo(id);
				window.close();	
			}
		}
		
	}
}
function IsDigitStr() {
	if(event.keyCode==34||event.keyCode==39){
		return false;
	}
}
function checkData(obj) {
	var CheckData = /<|>|'|"|'/;
	if ( CheckData.test(obj.value) ) {
        obj.value="";   
   	}
} 
</script>
<html:form action="/general/inform/search/common">
<table width="100%" height="250" border="0">
  <tr> 
    <td height="30">
		<table width="100%" border="0">
        	<tr> 
          		<td width="30%" height="26" align="left">
          			<bean:message key='general.inform.search.cond'/>
          			<html:text style="vertical-align:middle;" styleClass="TEXT4" name="searchInformForm" property="id" onclick="setId(this);" size="8" maxlength="5" readonly="true"/>
          		</td>
          		<td width="70%" align="right"><bean:message key='general.inform.search.condname'/>&nbsp;<input style="vertical-align:middle;" width="200px" class="textColorWrite" type="text" name="name" size="20" maxlength="30" onblur="checkData(this);" onkeypress="event.returnValue=IsDigitStr();" ></td>
        	</tr>
      </table>
	</td>
  </tr>
  <tr>
    <td height="210" valign="top">
		<table width="100%" border="0">
        <tr> 
          <td height="200" valign="top">
          	<html:select name="searchInformForm" property="titleid" multiple="multiple" onchange="change(this);" style="height:220px;width:100%;font-size:9pt"> 
            	<html:optionsCollection property="titlelist" value="dataValue" label="dataName" /> 
            </html:select>
          </td>
        </tr>
      </table>
	</td>
  </tr>
  <tr> 
    <td height="30" align="center">
		<input type="button" name="button1" onclick="saveCond();" value="<bean:message key='button.ok'/>" Class="mybutton"> 
        <input type="button" name="button2" onclick="window.close();" value="<bean:message key='button.cancel'/>" Class="mybutton">
	</td>
  </tr>
</table>
</html:form>
