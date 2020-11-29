<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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

<script language='javascript' >
function saves()
  {
  		 var return_vo = new Object();
  		 if(!document.keyDefinitionForm.sel.checked)
  		 {
  		 	return_vo.sel = "0";
  		 } else {
  		 	return_vo.sel = document.keyDefinitionForm.sel.value;
  		 }
    	 return_vo.type = document.keyDefinitionForm.type.value;
    	 return_vo.typeid = document.keyDefinitionForm.typeid.value;
    	 window.returnValue=return_vo;
         window.close();
  }
function dd()
{
   var yg=$F('sel');
   alert("ss");
   yg.checked=true;
}
	
</script>


<html:form  action="/general/deci/definition/add_definition_type">
	<table width="320" border="0" cellpadding="0" cellspacing="0" align="left" style="margin-top:3px;">
          <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="general.defini.zsort"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>  -->
       		<td  align="left" class="TableRow"><bean:message key="general.defini.zsort"/>&nbsp;</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	      <td align="right" width="25%" height='50' nowrap >&nbsp;<bean:message key="kq.wizard.target"/></td>
                	      <td align="left" nowrap style="padding-left:5px;">
                	      	<html:hidden name="keyDefinitionForm" property="typeid"/>
                	      	<html:text name="keyDefinitionForm" property="type"  maxlength="30" size="20" styleClass="text4"/>   
                	      	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 	      
                	      	<html:checkbox name="keyDefinitionForm" property="sel" value="1" /><bean:message key="column.law_base.status"/>
                        </td>
                      </tr>
                    
                </table>     
              </td>
          </tr>                                                     
          <tr class="list3">
            <td align="center" style="height:35px;">
         	  	 <input type="button"  value="<bean:message key="button.save"/>" class="mybutton" onclick="saves()"> 
            	 <input type="button"  value="<bean:message key="button.return"/>" class="mybutton" onclick="window.close();">  
	         	
            </td>
          </tr>          
      </table>
</html:form>
