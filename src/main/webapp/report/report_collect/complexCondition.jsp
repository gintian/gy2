<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<SCRIPT LANGUAGE=javascript>
	
	function goback()
	{
		var valWin = parent.Ext.getCmp('editCollect');
		if(valWin)
			valWin.close();
		else
			window.close();
	}
	
	
	function next()
	{
		if(document.reportCollectForm.right_fields.options.length==0)
		{
			alert(REPORT_INFO41+"！");
			return;
		}
		
		 setselectitem('right_fields');
     	 reportCollectForm.action="/report/edit_collect/reportCollect.do?b_addfield=link";
      	 reportCollectForm.submit();
	}
	
	
	</script>
	
<base id="mybase" target="_self">	
<hrms:themes />
<html:form action="/report/edit_collect/reportCollect">	
   
   <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top:-40px;">
  <tr>  
    <td valign="top" align="center"  >  
    <br><br><br> 
 &nbsp;
     <table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="edit_report.selectParam"/> &nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
   
        <td width="100%" align="center" class="RecordRow" style="border-top:none;" nowrap>
          <table style="border-top:none;">
            <tr>
             <td align="center"  width="46%">
              
               <table align="center" width="100%">
                <tr>
                 <td align="left">                  
                    <bean:message key="eidt_report.standbyParam"/>&nbsp;&nbsp;
                  </td>
                 </tr>         
                <tr>
                 <td align="center">
                  <select name="left_fields" multiple="multiple" size="10"  ondblclick="additem('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
                  		<logic:iterate id="element" name="reportCollectForm" property="commonsParamList"  > 
		             		<OPTION  value='<bean:write name="element" property="paramename" />§§<bean:write name="element" property="paramname" />§§<bean:write name="element" property="paramCode" />§§<bean:write name="element" property="paramscope" />§§<bean:write name="element" property="sortid" />' ><bean:write name="element" property="paramname" /></OPTION>
		         		</logic:iterate>
                   </select>
                   </td>
                 </tr>
                 </table>
                
                </td>
               
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button>
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button>	
                </td>         
                <td width="46%" align="center">

                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                  
                    <bean:message key="edit_report.selectedParam"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	            
 		     <select name="right_fields" multiple="multiple" size="10"  ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
                     <logic:iterate id="element" name="reportCollectForm" property="rightFieldsList"  > 
		             		<OPTION  value='<bean:write name="element" property="paramename" />§§<bean:write name="element" property="paramname" />§§<bean:write name="element" property="paramCode" />§§<bean:write name="element" property="paramscope" />§§<bean:write name="element" property="sortid" />' ><bean:write name="element" property="paramname" /></OPTION>
		         		</logic:iterate>
                     
                     </select>            
 		                 
                   </td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          
     </table>
   </td>
  </tr>
  <tr>      
          <td align="center">
				<br>
	             <html:button  styleClass="mybutton" property="b_addfield" onclick="next()">
            		     <bean:message key="button.query.next"/>
	            </html:button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            
	              <html:button  styleClass="mybutton" property="b_addfield" onclick="goback()">
            		      <bean:message key="button.close"/>
	            </html:button>
				
				    	  	
         </td>
        </tr>   
</table>
   
   
   
   
</html:form>
	