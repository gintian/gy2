<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
<!--
	function goback()
	{
	  document.posTemplateForm.action="/system/sys_param_panel.do";
	  document.posTemplateForm.submit();  
	}
	
//-->
</script>
<html:form action="/hire/zp_options/pos_template">
<table width="50%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="2">
		<bean:message key="hire.trust.synopsis"/>&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
            <td align="right" class="RecordRow" nowrap>
     	     <bean:message key="hire.select.rollcall"/>
     	    </td>
     	    <td class="RecordRow" nowrap>
    	        <hrms:importgeneraldata showColumn="name" valueColumn="tabid" flag="true" paraValue="K" 
                  sql="select tabid,name from rname where flagA=?" collection="list" scope="page"/>
                <html:select name="posTemplateForm" property="posTemplatevo.string(str_value)" size="1">
                     <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	        </td>                  	        	        
       </tr>  
       <tr>
           <td align="right" class="RecordRow" nowrap> 
             <bean:message key="hire.manual.attachment"/>
           </td>
     	    <td class="RecordRow" nowrap>
     	           <html:radio name="posTemplateForm" property="ps_card_attach" value="true"/> <bean:message key="lable.channel.visible"/>
                   <html:radio name="posTemplateForm" property="ps_card_attach" value="false"/> <bean:message key="lable.channel.hide"/> 
     	     </td>
       </tr>
       <tr>
          <td align="center" style="height: 35px" class="RecordRow" nowrap colspan="2" >
              &nbsp;&nbsp;<input type="submit" name="b_possetsucceed" class="mybutton" value="<bean:message key="button.ok"/>">
              <logic:equal name="posTemplateForm" property="edition" value="4">
	              <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' onclick="goback();" class="mybutton">  
	          </logic:equal>
          </td>
          </tr>   
</table>
</html:form>