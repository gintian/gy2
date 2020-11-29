<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
   
  </head>
  
  <body>
   <html:form action="/kq/month_kq/searchkqinfo.do">
   <div class="fixedDiv2" style="height: 100%;border: none">
   <table width='100%' border=0>
   <tr>   
   <td>
   <table width="99%"   border="0" cellspacing="1"  align="center" cellpadding="1" >
   	  <tr>
         <td align="left"  nowrap>        
            <html:textarea property="details" cols="57" rows="20" style="margin-left:30px;"></html:textarea>	      	
         </td>
        </tr>            
    </table>    
    </td>
   </tr>
   <tr>
     <td align="center">
        <html:button styleClass="mybutton" property="orgmapset" onclick="window.close();">
			<bean:message key="button.close"/>
		</html:button>	
     </td>
   </tr>
 </table>
 </div>
 </html:form>
  </body>
</html>
