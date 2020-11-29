<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<SCRIPT language=JavaScript>
 
function test(name)
{
  if(name!=null&&name!=""&&name!="undefined"&&name.length>0)
   {
      var obj=$('cardset');
      obj.setSelectedTab(name);
   }		
}  
</SCRIPT>
<logic:notEqual name="empChangeForm" property="ishaveadd" value="1">
<logic:notEqual name="empChangeForm" property="ishavecut" value="1">
<logic:notEqual name="empChangeForm" property="ishavechange" value="1">
<logic:notEqual name="empChangeForm" property="ishaveexce" value="1">
<SCRIPT language=JavaScript>
  alert("<bean:message key='kq.change.nochange'/>");
  history.go(-1);
</SCRIPT>
</logic:notEqual>
</logic:notEqual>
</logic:notEqual>
</logic:notEqual>
<body onload="test('${empChangeForm.changestatus}')">	
<html:form action="/kq/register/empchange">
<table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" >
      <tr>
        <td width="100%" >
         <hrms:tabset name="cardset" width="100%" height="100%" type="true">  
         <logic:equal name="empChangeForm" property="ishaveadd" value="1">       
	         <hrms:tab name="1" label="kq.change.addmen" visible="true" url="/kq/register/empchange_add.do?b_empadd=link">
            </hrms:tab>	
         </logic:equal>
         <logic:equal name="empChangeForm" property="ishavecut" value="1">
            <hrms:tab name="0" label="kq.change.delmen" visible="true" url="/kq/register/empchange_leave.do?b_empleave=link">
            </hrms:tab>	 
        </logic:equal>
        <logic:equal name="empChangeForm" property="ishavechange" value="1">          
            <hrms:tab name="2" label="kq.change.info" visible="true" url="/kq/register/empchangebase.do?b_empbase=link">
            </hrms:tab>	
        </logic:equal>
        <logic:equal name="empChangeForm" property="ishaveexce" value="1">
            <hrms:tab name="3" label="kq.change.exception" visible="true" url="/kq/register/empchangeunusual.do?b_search=link">
            </hrms:tab>	
        </logic:equal>    
	 </hrms:tabset>
        <br><br></td>
      </tr>      
</table> 

</html:form>