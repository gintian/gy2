<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<HEAD>
</HEAD>

<script language="javascript">

function writeDesc()
{
	document.editReportForm.desc.value="";
	 var s2="${editReportForm.desc}";	
	 while(s2.indexOf("&&")!=-1) 
     { 
    	s2=s2.replace('&&','\r\n');
     }
	document.editReportForm.desc.value=s2;
}

</script>

<body bgcolor="#F7FAFF"  onload="writeDesc()" >

<html:form action="/report/edit_report/editReport">
<br>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" align="center"  >  
	 &nbsp;&nbsp;&nbsp;
 	</td>
 	<td>
     <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="edit_report.goBackDescription"/> &nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
        <td width="100%" align="center" class="RecordRow" style="border-top:none;" nowrap>

		<table  width="100%"  height="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
 
		        <tr>  
		         <td width="98%" height="100%" align='center' >
						&nbsp;&nbsp;<TEXTAREA   name='desc' rows='14' cols='45' readonly="readonly">
						
						</TEXTAREA>					
		       
		         </td>
		         </tr>

		 </table>



   </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>
   
   




</html:form>

</body>
</html>