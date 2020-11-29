<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import=" com.hjsj.hrms.actionform.report.edit_report.EditReportForm"%>
<%
	String selfUnitcode="";
	String unitcode="";
	if(request.getParameter("selfUnitcode")!=null)
	{
		selfUnitcode=request.getParameter("selfUnitcode");
		unitcode=request.getParameter("unitcode");
	}
	String flag=request.getParameter("flag");
	String tsort=request.getParameter("tsort");
	EditReportForm editReportForm=(EditReportForm)session.getAttribute("editReportForm"); 
	String status=editReportForm.getStatus();
%>

	<SCRIPT LANGUAGE=javascript>
	
	 var info='<%=(request.getParameter("info"))%>';

	 if(info!=null&&info=='success')
	 {
	 	returnValue='b';
	 	parent.win1 = 'b';
	 	var win = parent.Ext.getCmp("reportApprove");
		if(win)
			win.close();
		else
			window.close();	
	 }
	
	
	function reportGoBack()
	{
		editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_writeDesc2=write&info=success&type=2&flag=<%=flag%>&selfUnitcode=<%=selfUnitcode%>&unitcode=<%=unitcode%>";
		var o=eval("document.editReportForm.desc");
		var s2=o.value.replace( /^\s*/g, '' ) ;	
		if(s2.length==0){
			alert("请输入驳回原因");
			return;
		}
		 while(s2.indexOf("\r\n")!=-1) 
	     { 
	    	s2=s2.replace('\r\n','&&');
	     }
	     
	     while(s2.indexOf("\"")!=-1) 
	     { 
	    	s2=s2.replace('\"','“');
	     }
	     
	     while(s2.indexOf("\'")!=-1) 
	     { 
	    	s2=s2.replace('\'','‘');
	     }
	     o.value=s2;	     
		 editReportForm.submit();
	}

	function closeWindow()
	{	
			var flag="<%=flag%>";
			if(flag=="1"){
				var win = parent.Ext.getCmp("reportApprove");
				if(win)
					win.close();
				else
					window.close();	
			 }else{
			 	window.location.href="/report/report_collect/reportOrgCollecttree.do?b_selr=init&unitcode=<%=unitcode%>&tsort=<%=tsort%>&selfUnitcode=<%=selfUnitcode%>";
			 }
	}
	
	
	function init()
	{
		var o=eval("document.editReportForm.desc");
		var s2="${editReportForm.desc}";	
		 while(s2.indexOf("&&")!=-1) 
	     { 
	    	s2=s2.replace('&&','\r\n');
	     }
	     o.value=s2;
	}

	</script>
<html>
<HEAD>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
</HEAD>
<hrms:themes />
<body bgcolor="#F7FAFF" onload="init()" >
<base id="mybase" target="_self">
<html:form action="/report/edit_report/editReport">
<table width="100%" align="center" style="margin:auto" border="0" cellpadding="0" cellspacing="0">
  <tr>  
 	<td>
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="2" style="border-bottom: none;" >
       	<bean:message key="edit_report.goBackDescription"/>&nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
        <td width="100%" align="center" class="RecordRow" nowrap>

		<table  width="100%"  height="100%" border="0" cellpmoding="1" cellspacing="1"  class="DetailTable"  cellpadding="0">   
                     <tr>
		        		<td height="2px"></td>
		        	</tr>
		        <tr>  
		         <td width="98%" height="100%" align='center' >
						<TEXTAREA   name='desc' rows='15' cols='90' type="_moz" <%  if(!unitcode.equals(selfUnitcode)){%><%}else{ %> readonly="readonly"<%} %>>
						${editReportForm.desc}
						</TEXTAREA>
		       
		         </td>
		         </tr>
		 </table>



   </td>
        </tr>   
     </table>
     
   </td>
  </tr>
   <tr>
       		<td height="2px"></td>
       	</tr>
  <tr  align="center">
  <td align="center" colspan="2">
  	<%if(flag==null||flag.equals("1")){ %>
						<% if(!unitcode.equals(selfUnitcode)){ %>
							<INPUT type='button' value=' <bean:message key="button.ok"/> '  class='mybutton' onclick='reportGoBack()'  >&nbsp;				
							<INPUT type='button' value=' <bean:message key="button.cancel"/> ' class='mybutton' onclick='closeWindow()'  >
		       			<% }}else{ %>
		       			<INPUT type='button' value=' <bean:message key="button.ok"/> '  class='mybutton' onclick='reportGoBack()'  >&nbsp;				
							<INPUT type='button' value=' <bean:message key="button.leave"/> ' class='mybutton' onclick='closeWindow()'  >
		       			<%} %>
  </td>
  </tr>
</table>
   
   




</html:form>

</body>
</html>
