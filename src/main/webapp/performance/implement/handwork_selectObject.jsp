<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes />
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script language="JavaScript">
  
	
	
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(performanceImpForm.left_fields,fieldlist);
	}


	function searchFieldList()
	{
	  
	   var codeItemID=$F("codeitem.value");	
	   var In_paramters="codeID="+codeItemID;  
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'90100140006'});
	}
	
	
 
</script>
<html:form action="/selfservice/performance/performanceImplement">
<html:hidden property="dbpre"/>

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top">  
    <br>
    <br>
    <br>   
     <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="button.manu"/>   &nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
        <td width="100%" align="center" class="RecordRow" nowrap>
          <table>
            <tr>
             <td align="center"  width="46%">
               <table align="center" width="100%">
                <tr>
                 <td align="left">
                 <logic:equal name="performanceImpForm" property="flag" value="1">	
                     <bean:message key="lable.performance.preparePerMainBody"/><bean:message key="lable.performance.perObject"/>&nbsp;&nbsp;
                 </logic:equal> 
                   <logic:equal name="performanceImpForm" property="flag" value="2">	
                     <bean:message key="lable.performance.preparePerMainBody"/><bean:message key="lable.performance.perMainBody"/>&nbsp;&nbsp;
                 </logic:equal> 
                  </td>
                 </tr>
                <tr>
                 <td align="center">
                  
                  	<input type="hidden" name="posparentcode" value="01">
                  	<input type="hidden" name="codeitem.value" value="01"  >
                        <input type="text" name='codeitem.viewvalue' readonly="true" onChange="searchFieldList()"    class="text6" size="25" />
                        &nbsp; 
                        <img  src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg("UM","codeitem.viewvalue","01","sdfad");'   /> 
  
                  
                  </td>
                 </tr>
                <tr>
                 <td align="center">
                  <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
                   </select>
                   </td>
                  </tr>
                 </table>
                </td>
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button >
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button >	
                </td>         
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                  <logic:equal name="performanceImpForm" property="flag" value="1">
                     <bean:message key="lable.performance.selectedPerMainBody"/><bean:message key="lable.performance.perObject"/>&nbsp;&nbsp;
                  </logic:equal>
                   <logic:equal name="performanceImpForm" property="flag" value="2">	
                      <bean:message key="lable.performance.selectedPerMainBody"/><bean:message key="lable.performance.perMainBody"/>&nbsp;&nbsp;
                 </logic:equal> 
                  
                  
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="performanceImpForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
 		                 </html:select>
                   </td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
          
          <logic:equal name="performanceImpForm" property="flag" value="1">
          	
	  	<hrms:submit styleClass="mybutton" property="b_save" onclick="setselectitem('right_fields');"  ><bean:message key="button.save"/></hrms:submit>      
	  	<hrms:submit styleClass="mybutton" property="br_return" ><bean:message key="button.return"/></hrms:submit>&nbsp;
	  </logic:equal>
	  <logic:equal name="performanceImpForm" property="flag" value="2">
          	
	  	 <hrms:submit styleClass="mybutton" property="b_save2" onclick="setselectitem('right_fields');"  ><bean:message key="button.save"/></hrms:submit>      
	  	<hrms:submit styleClass="mybutton" property="br_return2" ><bean:message key="button.return"/></hrms:submit>&nbsp;
	  </logic:equal>
	  
	  
	  
	  
	  
	         
         </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>
</html:form>
