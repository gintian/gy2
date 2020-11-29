<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%> 
<head>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script type="text/javascript">
<!--
var hascode="";
var idLength=0;
function sub()
{
   var idvalue=document.getElementById("schoolPositionId").value;
   var orgID=document.getElementById("schoolPositionOrg").value;
   if(trim(orgID).length==0||orgID=='ot')
    {
       alert("请选择所属机构！");
       return;
    }
     //dumeilong
     var schoolPositionDesc=document.getElementById("posdesc").value;
     if(hascode=="false"){
    	if(schoolPositionDesc.length>0){
    		document.getElementById("schoolPositionDesc").value=schoolPositionDesc;
    	}
    	}
    else{
    
    	var obj=document.getElementById("posdesc")
    	for(var i=0;i<obj.length;i++){
			if(obj.options[i].selected){
						schoolPositionDesc=obj.options[i].text;
						break;
			}
		}
    	if(schoolPositionDesc.length>0){
    		document.getElementById("schoolPositionDesc").value=schoolPositionDesc;
    	}
    }
    if(trim(schoolPositionDesc).length==0)
    {
        alert("岗位名称不能为空！");
        return;
    }
    if(trim(idvalue)=='')
    {
        alert("岗位编码不能为空!");
        return;
    }
   if(idvalue.length>idLength&&idLength!=0)
   {
      alert("岗位编码长度不符合要求，长度应该为【"+idLength+"】位！");
      return;
   }
   var oldid="${parameterForm2.oldID}";
   if(oldid=='1')
   {
     var hashvo=new ParameterSet();
     hashvo.setValue("code",idvalue);
   	 var In_paramters="opt=1"; 	
	 var request=new Request({method:'post',asynchronous:false,
		 parameters:In_paramters,onSuccess:check_ok,functionId:'0202001024'},hashvo);
   }
   else
   {
      parameterForm2.action="/hire/parameterSet/configureParameter/new_school_position.do?b_save=save&isclose=1";
      parameterForm2.submit();
   }	
  
}
function check_ok(outparameters)
{
    var flag=outparameters.getValue("flag");
    if(flag=='2')
    {
       alert("岗位编码已经存在！");
       return;
    }
    parameterForm2.action="/hire/parameterSet/configureParameter/new_school_position.do?b_save=save&isclose=1";
    parameterForm2.submit();
}
function getAutoCode()
{
    var orgID=document.getElementById("schoolPositionOrg").value;
    var hashvo=new ParameterSet();
    hashvo.setValue("orgID",orgID);
   	var In_paramters="opt=2"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:change_ok,functionId:'0202001024'},hashvo);					
    
}
//dumeilong
function changecode(){
	var obj=document.getElementById("posdesc");
	var pointid="";
	for(var i=0;i<obj.length;i++){
		if(obj.options[i].selected){
					pointid=obj.options[i].value;
					break;
		}
	}
	var orgpointid=document.getElementById("schoolPositionOrg").value;
	var orgcodid=pointid.substring(orgpointid.length);
	document.getElementById("schoolPositionId").value=orgcodid;
}
function change_ok(outparameters)//dumeilong
{
    var newValue=outparameters.getValue("newValue");
    var oldId=outparameters.getValue("orgID");
    hascode=outparameters.getValue("hascode");
    if(hascode=='false'){
    	var df=document.getElementById("posdesc").type;
    	if(df!='text'){
    		var obj=document.getElementById("posname")
    		var _str="<input type=\"text\" name=\"posdesc\" value=\"\"/>";
    		_str+="<input type=\"hidden\" name=\"schoolPositionDesc\" ";
    		_str+="value=\"\"/>";
    		obj.innerHTML=_str;
    		document.getElementById("posdesc").enabled="true";
    		document.getElementById("schoolPositionId").value=newValue;
    	}else{
		    idLength=outparameters.getValue("allowLength");
		    document.getElementById("schoolPositionId").value=newValue;
		    document.getElementById("posdesc").enabled="true";
		    document.getElementById("posdesc").value="";
	    }
	    document.getElementById("schoolPositionId").readOnly="true";
    }else{
    	var obj=document.getElementById("posname")
    	var innerhtml=outparameters.getValue("innerhtml");
    	var _str=getDecodeStr(innerhtml);
    	obj.innerHTML=_str;
    	document.getElementById("schoolPositionId").readOnly="true";
    	document.getElementById("schoolPositionId").value="";
    }
}
function openInputCodeDialogSelf(codeid,mytarget,hidden_name) 
{	
    var codevalue,thecodeurl,target_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(popwin==null||popwin!=null)
    {
       getAutoCode();
    }
}
function clearParam()
{
    var obj=new Object();
     obj.pid="";
     obj.pdesc="";
     returnValue=obj;
     window.close();
}
function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
//-->
</script>
 <style type="text/css">
.RecordRow_top {
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;

}
</style>
    </head>
 <base id="mybase" target="_self">
<body onload="setTPinput()">
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%> 
<hrms:themes></hrms:themes>
<html:form action="/hire/parameterSet/configureParameter/new_school_position">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<br/>
<%
}
%>
 <table width="440px" border="0" cellpadding="0" cellspacing="0" align="center">
 <tr>
 <td align=left class='TableRow'>
 新建校园招聘岗位
 </td>
 </tr>
 <tr>
 <td align="left" class="RecordRow_top common_border_color" nowrap>
  <table width="100%" border="0" cellpadding="0" cellspacing="1" align="center">
  <tr>
  <td width="40%" align="right" valign="bottom" nowrap>
  所属机构&nbsp;&nbsp;
  </td>
  <td align="left" width="60%" valign="bottom" nowrap>
  <logic:equal value="2" name="parameterForm2" property="oldID">
  <input type="text" id="spod" name="schoolPositionOrgDesc" value="${parameterForm2.schoolPositionOrgDesc}" disabled/>
  </logic:equal>
  <logic:equal value="1" name="parameterForm2" property="oldID">
  <input type="text" id="spod" name="schoolPositionOrgDesc" value="${parameterForm2.schoolPositionOrgDesc}" />
 <img  src="/images/code.gif" onclick='openInputCodeDialogSelf("UM","schoolPositionOrgDesc","schoolPositionOrg");'/>
  </logic:equal> 
  <html:hidden name="parameterForm2" property='schoolPositionOrg' />
  </td>
  </tr>
   <tr>
  <td width="40%" align="right" valign="bottom" nowrap>
 岗位名称&nbsp;&nbsp;
  </td>
  <td align="left" width="60%" nowrap>
  <div id="posname">
 
  <logic:equal value="2" name="parameterForm2" property="oldID">
   <input type="text" name="posdesc" value="${parameterForm2.schoolPositionDesc}">
  </logic:equal>
   <logic:equal value="1" name="parameterForm2" property="oldID">
   	<input type="text" name="posdesc" value="">
    </logic:equal>
  <input type="hidden" name="schoolPositionDesc" value="">
  </div>
  </td>
  <td>
  </td>
  </tr>
   <tr>
  <td width="40%" align="right" valign="bottom" nowrap>
 岗位编码&nbsp;&nbsp;
  </td>
  <td align="left" width="60%" nowrap>
  <logic:equal value="2" name="parameterForm2" property="oldID">
  <input type="text" name="schoolPositionId" value="${parameterForm2.schoolPositionId}" disabled/>
  </logic:equal>
  <logic:equal value="1" name="parameterForm2" property="oldID">
  <html:text name="parameterForm2" property='schoolPositionId' />
  </logic:equal>
  </td>
  </tr>
 </table>
 </td>
 </tr>
 <tr>
 <td class="RecordRow_top common_border_color" align="center" style="padding-top:5px;padding-bottom:3px;">
 <input type="button" class="mybutton" name="sv" value="<bean:message key="button.save"/>" onclick="sub();"/>
  <input type="button" class="mybutton" name="cle" value="清除参数" onclick="clearParam();"/>
 <input type="button" class="mybutton" name="ca" value="<bean:message key="button.cancel"/>" onclick="window.close();"/>
 </td>
 </tr>
 </table>
 </html:form>
 <script type="text/javascript">
 //dumeilong
  document.getElementById("schoolPositionId").readOnly="true";
   <logic:equal value="2" name="parameterForm2" property="oldID">
  	
	</logic:equal>
 
 <%if(request.getParameter("isclose")!=null&&request.getParameter("isclose").equals("1")){%>
 var obj=new Object();
 obj.pid="${parameterForm2.schoolPositionOrg}"+"${parameterForm2.schoolPositionId}";
 obj.pdesc="${parameterForm2.schoolPositionDesc}";
 returnValue=obj;
 window.close();
<%}%>
</script>