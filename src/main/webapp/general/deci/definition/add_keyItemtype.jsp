<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<hrms:themes></hrms:themes>
<script language='javascript' >
function saves()
  {
    	 var return_vo = new Object();
  		 if(!document.dsKeyItemtypeForm.status.checked)
  		 {
  		 	return_vo.sel = "0";
  		 } else {
  		 	return_vo.sel = document.dsKeyItemtypeForm.status.value;
  		 }
    	 return_vo.type = document.dsKeyItemtypeForm.name.value;
    	 return_vo.typeid = document.dsKeyItemtypeForm.typeid.value;
    	 window.returnValue=return_vo;
         window.close();
	  
  }
  
  function returns()
  {
   		 window.open("/general/deci/definition/statCutline/searchItemtype.do?b_query=link",'il_body');
         window.close();
  }
  function init()
  {
    var status="${dsKeyItemtypeForm.status}";
    if(status==null||status=="")
    {
      var ob=document.getElementById("status"); 
      ob.checked=true;
    }
  }	
</script>


<html:form  action="/general/deci/definition/statCutline/searchItemtype">
	<table width="320" border="0" cellpadding="0" cellspacing="0" align="left" style="margin-top:4px;">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="general.defini.cutlineSort"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>--> 
       		<td align="left" class="TableRow"><bean:message key="general.defini.cutlineSort"/>&nbsp;</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	      <td align="right" height='50' nowrap style="padding-left:5px;"><bean:message key="general.defini.sortName"/></td>
                	      <td align="left" nowrap style="padding-left:5px;">
                	      	<html:text name="dsKeyItemtypeForm" property="name"  maxlength="30" size="20" styleClass="text4" style="width:150px;"/>   
                	      	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 	      
                	      	<html:checkbox name="dsKeyItemtypeForm" property="status" styleId="status" value="1"/><bean:message key="column.law_base.status"/>
                        </td>
                      </tr>
                    
                </table>     
              </td>
          </tr>
		     <html:hidden name="dsKeyItemtypeForm" property="typeid" />     
                                                     
          <tr class="list3">
            <td align="center" style="height:35px;">
         	  	 <input type="button"  value="<bean:message key="button.save"/>" class="mybutton" onclick="saves()"> 
            	 <input type="button"  value="<bean:message key="button.return"/>" class="mybutton" onclick="window.close();">  
	         	
            </td>
          </tr>          
      </table>
</html:form>
<script language='javascript' >
init();
</script>