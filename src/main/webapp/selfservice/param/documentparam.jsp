<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
%>
<script language=JavaScript>   

function searchFieldList(idv)
{
           var setname="";
	   if(idv==="bz")
	      setname="bz_fieldsetid";
	   else if(idv=="hb")
	      setname="hb_fieldsetid";
	   else if(idv=="unit")
	      setname="unit_fieldsetid";
	   var tablename=$F(setname);		   
	   var hashvo=new ParameterSet();   
	   hashvo.setValue("tablename",tablename);
	   hashvo.setValue("idv",idv);
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3970004013'},hashvo);   	   
}
function showFieldList(outparamters)
{
      var codesetlist=outparamters.getValue("codesetlist");
      var idv=outparamters.getValue("idv"); 
      var obj;
      if(idv==="bz")
        obj=documentParamForm.bz_codesetid;
      else if(idv=="hb")
        obj=documentParamForm.hb_codesetid; 
      else if(idv=="unit")
        obj=documentParamForm.unit_codesetid;
      AjaxBind.bind(obj,codesetlist);	
      //var tablename=outparamters.getValue("tablename");
      //getSetnameValue(idv,tablename)
}
 function getSetnameValue(idv,tablename)
 {
     var setfiledname="";
     if(idv==="bz")
	setfiledname="bz_codesetid";
     else if(idv=="hb")
	setfiledname="hb_codesetid";
	else if(idv=="unit")
	setfiledname="unit_codesetid";
     var filedname=$F(setfiledname);
     var hashvo=new ParameterSet();
     hashvo.setValue("tablename",tablename);
     hashvo.setValue("filedname",filedname);
     hashvo.setValue("idv",idv);
     var request=new Request({method:'post',asynchronous:false,onSuccess:getCodeList,functionId:'05603000011'},hashvo);
 }
 function getCodeList(outparamters)
 {
      var codeitemlist=outparamters.getValue("codeitemlist");
      var idv=outparamters.getValue("idv");
      var obj;
      if(idv==="bz")
        obj=documentParamForm.bz_codeitemid;
      else if(idv=="hb")
        obj=documentParamForm.hb_codeitemid; 
      else if(idv=="unit")
        obj=documentParamForm.unit_codeitemid;
      AjaxBind.bind(obj,codeitemlist);	
 }
 function saveCode()
 {
    documentParamForm.action="/selfservice/param/documentparam.do?b_save=link";
    documentParamForm.submit();
 }
</script> 
<html:form action="/selfservice/param/documentparam"><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="2">
			<bean:message key="wd.lawbase.docrelate"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  
   	  <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="menu.table"/>
   	   </td>
   	   <td width="80%" class="RecordRow" >
   	      <hrms:optioncollection name="documentParamForm" property="user_field_list" collection="list" />
	      <html:select name="documentParamForm" property="bz_fieldsetid" styleId="bz_fieldsetid" style="width:200" onchange="searchFieldList('bz');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
           </td>
      </tr>
      
      <tr>
      	<td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="menu.field"/>
   	  	</td>
      	<td width="40%" class="RecordRow">
        	<hrms:optioncollection name="documentParamForm" property="bz_codesetlist" collection="list" />
      		<html:select name="documentParamForm" property="bz_codesetid" styleId="bz_codesetid" style="width:200">
        		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
       		</html:select> 
        	<font color="black"><bean:message key="wd.lawbase.lengthdescription"/></font>       		
        </td>
      </tr>
      <!--  
      <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="menu.priv"/>:
   	   </td>
   	   <td width="80%" class="RecordRow" >
   	      <hrms:optioncollection name="documentParamForm" property="bz_privlist" collection="list" />
	      <html:select name="documentParamForm" property="law_file_priv" styleId="law_file_priv" style="width:200">
          <html:options collection="list" property="dataValue" labelProperty="dataName"/>
          </html:select>
          <font color="black"><bean:message key="wd.lawbase.restart"/></font>
       </td>
      </tr>
      -->   
      <tr>
      	<td align="center" style="height: 35px" class="RecordRow" nowrap colspan="2">
        	<input type="button" name="btnreturn" value='保存' class="mybutton" onclick=" saveCode();">
      	</td>
      </tr>   
</table>
</html:form>
