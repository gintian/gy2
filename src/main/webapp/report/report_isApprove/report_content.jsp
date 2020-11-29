<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.report_isApprove.Report_isApproveForm" %>
<%
	Report_isApproveForm report_isApproveForm = (Report_isApproveForm)session.getAttribute("report_isApproveForm");
%>

<html>
<HEAD>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
</HEAD>
<!-- add by wangchaoqun on 2014-9-23 begin -->
<script language="javascript">

function writeDesc()
{ 
    var o=eval("document.report_isApproveForm.content");
	var s2=o.value.replace( /^\s*/g, '' ) ;	
	while(s2.indexOf("&&")!=-1) 
    { 
    	s2=s2.replace('&&','\r\n');
    }
    o.value=s2;	
}

function closeWindow()
{
	var valWin = parent.Ext.getCmp('reportIsApprove');
	if(valWin)
		valWin.close();
	else
		window.close();	

}

</script>
<!-- add by wangchaoqun on 2014-9-23 end -->
<body bgcolor="#F7FAFF" onload="writeDesc()" >
<html:form action="/report/report_isApprove/reportIsApprove" >
<table width="100%" height="370" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>
 	<td valign="top">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="2" style="border-bottom: none;">
        	<%if(report_isApproveForm.getFlag().equals("2")){ %>
        	<bean:message key="edit_report.goBackDescription"/> &nbsp;&nbsp;
        	<%}else if(report_isApproveForm.getFlag().equals("1")){%>
        	审批意见
        	<%} %>
        </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
        	<td width="100%" align="center" class="RecordRow" nowrap>
				<table  width="100%"  height="100%" border="0" cellpmoding="1" cellspacing="1"  class="DetailTable"  cellpadding="0" style="">   
		        	<tr>
		        		<td height="2px"></td>
		        	</tr>
		        	<tr>  
		         		<td width="98%" height="100%" align='center' >
							<html:textarea name="report_isApproveForm" style='width:100%'  property="content" rows='14' readonly="readonly">
						
										</html:textarea>						       
		         		</td>
		         	</tr>
		         	<tr>
		        		<td height="2px"></td>
		        	</tr>
		 		</table>
   			</td>
        </tr>
        <tr>
       		<td height="2px"></td>
       	</tr> 
          <tr>
  				<td  width="100%" align="center"  nowrap><input type="button" name="" value="关闭" onclick="closeWindow()" class="mybutton""></td>
 		 </tr>  
     </table>
     
   </td>

  </tr>

</table>

</html:form>

</body>
</html>
