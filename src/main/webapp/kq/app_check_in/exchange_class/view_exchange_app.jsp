<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/popcalendar2.js"></script>
<script type="text/javascript">
  
</script>
<html:form action="/kq/app_check_in/exchange_class/app_exchange">
<div id="d" class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" style="border-style:solid;border-width:1px;border-color:#C4D8EE;" class="ListTable common_border_color" >
 <thead>
   <tr>
    <td align="left" class="TableRow" nowrap>
	    <bean:message key="kq.exchange.app.name"/>&nbsp;&nbsp;	
    </td>            	        	        	        
   </tr>
  </thead>
  <tr>
   <td width="100%" align="center" valign="Top" nowrap>
     <table border="0" cellspacing="1" cellpadding="1" width="90%">
        <tr> 
          <td width="42%"> <fieldset align="center" style="width:100%;">
                  <legend ><bean:message key="kq.exchange.emp.nonce"/></legend>
                  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.a0101"/></td>
                      <td height="30" > 
                      <bean:write name="exchangeAppForm" property="ex_vo.string(a0101)" filter="false"/>&nbsp;  
                      
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(a0100)" styleId="a0100" styleClass="text"/> 
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(nbase)" styleId="nbase" styleClass="text"/>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(b0110)" styleId="b0110" styleClass="text"/> 
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(e0122)" styleId="e0122" styleClass="text"/>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(e01a1)" styleId="e01a1" styleClass="text"/>
                     </td>
                    <tr> 
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.class"/></td>
                      <td height="30" > 
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(q19z7)" styleId="q19z7"/>  
                      <bean:write name="exchangeAppForm" property="class_name" filter="false"/>
                      </td>
                    <tr> 
                  </table>
                  </fieldset>
           </td>
           <td width="6%">&nbsp;</td>
           <td width="42%"> <fieldset align="center" style="width:100%;">
                  <legend ><bean:message key="kq.exchange.emp.exchange"/></legend>
                  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.a0101"/></td>
                      <td height="30" >                       
                      
                      <bean:write name="exchangeAppForm" property="ex_vo.string(q19a1)" filter="false"/>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(q19a0)" styleId="q19a0" styleClass="text"/> 
                      <input name="ex_nbase" type="hidden" styleId="ex_nbase">
                      </td>
                    <tr> 
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.class"/></td>
                      <td height="30" >                       
                      <bean:write name="exchangeAppForm" property="exclass_name" filter="false"/>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(q19z9)" styleId="q19z9"/>  
                      </td>
                    <tr> 
                  </table>
                  </fieldset>
           </td>
        </tr>        
     </table>        
   </td>
  </tr>
  <tr>
    <td width="100%" align="center" valign="Top" nowrap>
    <br>
       <fieldset align="center" style="width:90%;">
          <legend ><bean:message key="kq.exchange.move.date"/></legend>
          <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
              <td height="30" align="center" > <bean:message key="kq.exchange.nonce.date"/> </td>
              <td height="30" >
              <bean:write name="exchangeAppForm" property="ex_vo.string(q19z1)" filter="false"/>
              
              </td>
              <td height="30" align="center" > <bean:message key="kq.exchange.move.date"/> </td>
              <td height="30" >
               <bean:write name="exchangeAppForm" property="ex_vo.string(q19z3)" filter="false"/>              
              </td>
            <tr> 
          </table>
       </fieldset>
    </td>
  </tr>
  <tr align="center"> 
     <td height="20">
	<fieldset align="center" style="width:90%;">
        <legend ><bean:message key="kq.exchange.reason"/></legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td height="70" align="center" >                 
                  <html:textarea name="exchangeAppForm" property="ex_vo.string(q1907)" cols="50" rows="5" styleClass="text5"/>
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
   <tr align="left"> 
     <td height="20">
	<fieldset align="center" style="width:90%;">
        <legend >意见</legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  部门领导&nbsp;  
               </td>
               <td class="tdFontcolor" nowrap >                 
                  &nbsp;<bean:write name="exchangeAppForm" property="ex_vo.string(q1909)" filter="false"/>              
               </td>
            <tr> 
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  部门领导意见&nbsp;  
               </td>
               <td  class="tdFontcolor" nowrap >                 
                  &nbsp;<bean:write name="exchangeAppForm" property="ex_vo.string(q1911)" filter="false"/>              
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  单位领导&nbsp; 
               </td>
               <td  class="tdFontcolor" nowrap >                 
                  &nbsp;<bean:write name="exchangeAppForm" property="ex_vo.string(q1913)" filter="false"/>              
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  单位领导意见&nbsp; 
               </td>
               <td class="tdFontcolor" nowrap >                 
                  &nbsp;<bean:write name="exchangeAppForm" property="ex_vo.string(q1915)" filter="false"/>              
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
  <tr><td height="10"></td></tr>
</table>
</div>
<table  width="100%" align="center">
  <tr> 
    <td align="center" height="35">       
      <input type="button" name="b_next" value="<bean:message key="button.close"/>" onclick="window.close();" class="mybutton" > 
    </td>
  </tr>
</table>
</html:form> 