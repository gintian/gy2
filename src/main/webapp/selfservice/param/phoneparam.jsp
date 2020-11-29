<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
function validate()
{
  if(confirm("确定保存吗？"))
     return true;
  else 
     return false;
}
</script>

<html:form action="/selfservice/param/phoneparam" onsubmit="return validate();"> 
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="1" class="RecordRow" style="margin-top: 7px;">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" colspan="2" style="border-right:none;">
		电话邮箱参数&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
                <td align="right"  width="36%" class="RecordRow">
                     <bean:message key="selfservice.param.otherparam.email_title"/>&nbsp;
                   </td>
                   <td width="64%" align="left" class="RecordRow" style="border-right:none;">
    	              <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="false" paraValue="" 
                         sql="otherParamForm.fieldcond" collection="list" scope="page"/>
                       <html:select name="otherParamForm" property="email" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>&nbsp;	     
                    </td>  
                  </tr>
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     <bean:message key="selfservice.param.otherparam.phone_title"/>&nbsp;
                   </td>
                   <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                       <html:select name="otherParamForm" property="phone" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>&nbsp;	     
                    </td>  
                  </tr>   
                  <!-- 
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     <bean:message key="selfservice.param.otherparam.telephone_title"/>&nbsp;
                   </td>
                   <td width="64%" align="left" class="RecordRow">
                       <html:select name="otherParamForm" property="telephone" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>&nbsp;       
                    </td>  
                  </tr>     
                   -->  
                   <tr>          
               <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35px">
                  <hrms:submit styleClass="mybutton" property="b_save">
                     <bean:message key="button.ok"/>
	          </hrms:submit> 	         
              </td>
          </tr>   
</table>
</html:form>