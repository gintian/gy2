<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language='javascript'>

function checkSelect()
{
	var a=0;
	for(var i=0;i<document.printKqInfoForm.tabid.options.length;i++)
	{
		if(document.printKqInfoForm.tabid.options[i].selected )
			a++;
	}
	return a;
}


function sub()
{
	var num=checkSelect();
	if(num==0)
	{
		alert("请选择花名册！");	
		return;
	}
	else if(num>1)
	{
		alert("只能选择一项!");
		return;
	}
	else
	{
		
			printKqInfoForm.action="/kq/register/select_kqreportdata.do?b_save=query";
			printKqInfoForm.submit();
	}
}
</script>




<html:form action="/kq/register/select_kqreportdata">
  <br>
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter"><bean:message key="muster.label.label"/></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td align=center class="TableRow"><bean:message key="muster.label.label"/></td>            	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" >      
               	    <tr><td height="25" colspan="2">
               	     <logic:equal name="printKqInfoForm" property="flaginfo" value="1">
                       名称&nbsp;<html:text name="printKqInfoForm" property="report_name" size="30" styleClass="text"/>&nbsp;
                     </logic:equal> 
               	    </td></tr>   
                      <tr> 
                         <td></td>
                         <td>
                            <html:select name="printKqInfoForm" property="tabid" size="1" multiple="false"  style="height:209px;width:250px;">
                              <html:optionsCollection property="musterlist" value="dataValue" label="dataName"/>
                            </html:select>   
                         </td>
                      </tr> 
	       </table>	            	
            </td>
          </tr>
          <tr class="list3">
            <td align="center" >
		&nbsp;           
            </td>
          </tr>            
          <tr class="list3">
            <td height="35">   
                  <html:hidden name="printKqInfoForm" property="flaginfo"/>   
                  <html:hidden name="printKqInfoForm" property="report_id"/>   
            	<html:button  styleClass="mybutton" property="b_save" onclick="sub()">
            	<bean:message key="button.save"/>
	 	</html:button>   
            
            
            	 <input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>' onclick="history.back();" class="mybutton">						      
         	
                      	
            </td>
          </tr>  
  </table>
 
</html:form>
