<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript">
function chooseorg()
{

      var vos=document.getElementsByName("chooseed");  
      var vo=vos[0];
      var values="";
      for(i=0;i<vo.options.length;i++)
      {
        if(vo.options[i].selected)
        {
           values=vo.options[i].value;
        }
      }      
      var thevo=new Object();
      thevo.orgid=values;
      window.returnValue=thevo;
      window.close();
}

</script>
<html:form action="/org/orginfo/searchorglist"> 
 <table  width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="middle" >   
                             <tr height="20">
       		                <!--  <td width=1 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter"><bean:message key="org.orginfo.organname"/></td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="300"></td> -->
       		                <td align=center class="TableRow"><bean:message key="org.orginfo.organname"/></td>          		           	      
                               </tr>                                         
                               <tr>
		                 <td width="100%" valign="middle" class="framestyle9" >
		                 <br>	
		                 <br>	              
		                   <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                     <tr>
		                       <td width="100%" valign="middle" colspan="4" >		              
		                         <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	  <tr>
		                          <td width="100%" height="50" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	        <bean:message key="org.orginfo.info01"/>：
		                	     </td>
		                	     <td>		                	    
		                	        <hrms:optioncollection name="orgInformationForm" property="chooselist" collection="list" />
	                                        <html:select name="orgInformationForm" property="chooseed" size="1" >
                                                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                                </html:select> 
                                              </td>
		                	     <tr>
		                	   </table>  
		                                    
		                	  </td>
		                      	</tr>		                      			                	
		                     </table>		                  
		             </td>
		            
		         </tr>
		         <tr>
		            <td  height="40" align="center" >		                
	                         <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.ok"/>' onclick="chooseorg();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.cancel"/>' onclick="window.close();" class="mybutton">
		             </td>
		         </tr>
		       </table>                
</html:form>