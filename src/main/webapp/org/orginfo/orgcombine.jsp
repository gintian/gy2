<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript">
   	function savecode()
   	{
    	  alert("ddd");
   	}
   </SCRIPT>
<html:form action="/org/orginfo/orgcombine">
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">  
    
   <tr>
        <td>
           <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="orgInformationForm" property="selectTreeCode" filter="false"/>
             </SCRIPT>
            </div>      
	</td>
	 <td width="8%" align="center">
                   <hrms:submit styleClass="mybutton" property="b_addorg" onclick="document.orgInformationForm.target='_self'">
            		     <bean:message key="button.setfield.addfield"/> 
	           </hrms:submit>
	           <br>
	           <br>
	           <hrms:submit styleClass="mybutton" property="b_delorg" onclick="document.orgInformationForm.target='_self'">
            		     <bean:message key="button.setfield.delfield"/>    
	           </hrms:submit>
	           <br>
	           <br>	     
              <hrms:submit styleClass="mybutton"  property="b_update">
                   <bean:message key="button.ok"/>
	     </hrms:submit>
        </td>      
	 <td width="40%" align="left">
                   <hrms:optioncollection name="orgInformationForm" property="orglist" collection="selectedlist"/>
     	             <html:select property="combineorg" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
        </td>
  </tr>      
	
  </table>
     
</html:form>
