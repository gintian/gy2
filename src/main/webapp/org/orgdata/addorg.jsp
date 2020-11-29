<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<script language="javascript">
function changenewkind(){
	orgInformationForm.action="/org/orginfo/searchorglist.do?b_kindorg=link";
	orgInformationForm.submit();
}
function validatelen() {
   <logic:equal name="orgInformationForm" property="first" value="0">
     if(orgInformationForm.codeitemid.value.length!="${orgInformationForm.len}"){
       alert("<bean:message key="error.org.codelength"/>" + "${orgInformationForm.len}" + "!");
       return false;
     }
   </logic:equal>
}
function submitsave(){
	var  key = window.event.keyCode;
	if(key==13 || key==0){
		if(validatelen()==false){
   			return false;
      	}
      	var reg=new RegExp("^[A-Za-z0-9]+$");
      	if(!reg.exec(orgInformationForm.codeitemid.value)){ 
        	alert(INPUT_CORRECT_ORG_CODE);
           	return false;
      	}
      	var codeitemid=document.getElementById("codeitemid").value;
      	var codeitemdesc=document.getElementById("codeitemdesc").value;
      	var codesetid=document.getElementById("codesetid").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("grade","${orgInformationForm.grade}");	
		hashvo.setValue("first","${orgInformationForm.first}");
		hashvo.setValue("code","${orgInformationForm.code}");
		hashvo.setValue("codeitemid",codeitemid);
		hashvo.setValue("codeitemdesc",codeitemdesc);	
		hashvo.setValue("codesetid",codesetid);				
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkSearchGeneral,functionId:'16010000031'},hashvo);
		
   }
}
function checkSearchGeneral(outparamters){
	var isrefresh = outparamters.getValue("isrefresh");
	var issuperuser = outparamters.getValue("issuperuser");
	var manageprive = outparamters.getValue("manageprive");
	var codeitemid=document.getElementById("codeitemid").value;
    var codeitemdesc=document.getElementById("codeitemdesc").value;
    var codesetid=document.getElementById("codesetid").value;
    var a_code = "${orgInformationForm.code}";
    window.returnValue = isrefresh+","+a_code+","+codesetid+","+codeitemid+","+codeitemdesc+","+issuperuser+","+manageprive;
	window.close();
}
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchorglist" onsubmit="return validatelen()"> 
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=100 align=center class="tabcenter">&nbsp;<bean:message key="label.org.maintenance"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width=330></td> --> 
       		<td align=center class="TableRow">&nbsp;<bean:message key="label.org.maintenance"/>&nbsp;</td>            	      
  </tr>  
   <tr>
      <td class="framestyle9" width="100%" align="center">
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"> 
             <tr  align="center" class="list3">
              <td colspan="2">
                <br>
                 <div align="center"><bean:write name="orgInformationForm" property="labelmessage"  filter="true"/></div>
                <br>
              </td>
             </tr>
             <tr  align="right" class="list3">
                <td>
                  &nbsp;<bean:message key="label.org.type_org"/>&nbsp;
                </td>
                <td align="left">
                <logic:equal name="orgInformationForm" property="kind" value="1">
                   <html:select name="orgInformationForm" property="codesetid" size="1" onchange="changenewkind()">
                       <html:option value="UM"><bean:message key="label.codeitemid.um"/></html:option>
                       <html:option value="@K"><bean:message key="label.codeitemid.kk"/></html:option>
                   </html:select>
                </logic:equal>
                <logic:equal name="orgInformationForm" property="kind" value="2">
                       <html:select name="orgInformationForm" property="codesetid" size="1" onchange="changenewkind()">
                         <html:option value="UN"><bean:message key="label.codeitemid.un"/></html:option>
                         <html:option value="UM"><bean:message key="label.codeitemid.um"/></html:option>
                       </html:select>               
                </logic:equal>
                </td>
             </tr> 
             <tr  align="right" class="list3">
                <td>
                  &nbsp;<bean:message key="label.org.superiorcode"/>&nbsp;
                </td>
                <td align="left">
                   <html:text   name="orgInformationForm" property="code" readonly="true" styleClass="textColorRead"/>
                </td>
             </tr> 
             <tr  align="right" class="list3">
               <td>
                  &nbsp;<bean:message key="label.org.curcode"/>&nbsp;
                </td>
               <td align="left">
                 <html:text   name="orgInformationForm" property="codeitemid"  styleClass="textColorWrite" maxlength="${orgInformationForm.len}"/>
               </td>
             </tr> 
             <tr  align="right" class="list3">
                <td>
                   &nbsp;<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                </td>
               <td align="left"> 
                  <html:text   name="orgInformationForm" property="codeitemdesc"  styleClass="textColorWrite" maxlength="50"/>
               </td>
             </tr> 
             <tr  align="center" class="list3">
                <td colspan="2">
                <br>
                 <input type="button" Class="mybutton" name="b_save"  value="<bean:message key='addunitinfo.reportunit.save'/>" onClick="submitsave()" onKeyDown="if (event.keyCode==13)  submitsave();" />                 
	         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
          </table>
       </td>
   </tr>   
  </table>
   
</html:form>
