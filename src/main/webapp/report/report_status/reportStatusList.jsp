<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.report.retport_status.ReportStatusForm,
				 com.hrms.frame.dao.RecordVo,
				 org.apache.commons.beanutils.LazyDynaBean" %>
				 
				 
		  <%
  	      ReportStatusForm reportStatusForm=(ReportStatusForm)session.getAttribute("reportStatusForm");	
  	      String unitCode=reportStatusForm.getUnitCode();
  	      ArrayList reportSetList=reportStatusForm.getReportSetList();
  	      ArrayList subUnitList=reportStatusForm.getSubUnitList();
  	      ArrayList tabDataList=reportStatusForm.getTabDataList();
  	      HashMap setTabCountMap=reportStatusForm.getSetTabCountMap();
  	      String selfUnitcode=reportStatusForm.getSelfUnitcode();
  	     String width=String.valueOf((reportSetList.size())*200);
  	     
  	      
  	      %>
<html>
  <head>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language="JavaScript" src="/js/validate.js"></script>
  <script language="JavaScript" src="/js/function.js"></script>
   <script language='javascript'>
  	 var srcobj;	
	function goback()
	{
	  var hashvo=new ParameterSet();
	  hashvo.setValue("opt","back"); 
	  var In_paramters="unitcode=${reportStatusForm.unitCode}"; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'0305000044'},hashvo);
	
	}
	
	function returnInfo2(outparamters)
	{
	     var unitcode=outparamters.getValue("parent_unitcode");
		 document.reportStatusForm.action="/report/report_status.do?b_query=query&opt="+unitcode;
		 document.reportStatusForm.submit();
	}

	function rdl_doClick(unitcode,obj){
	  this.srcobj=obj;
	  var hashvo=new ParameterSet();
	  hashvo.setValue("opt","desc"); 
	  var In_paramters="unitcode="+unitcode; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'0305000044'},hashvo);
	}
	
	function returnInfo(outparamters)
	{
	     var context=outparamters.getValue("context");
		 var op=eval('date_panel');
		 op.innerHTML=getDecodeStr(context);
		 var pos=getAbsPosition(srcobj);
		  with($('date_panel'))
		  {
		  	//	style.backgroundColor="#FFFDC5";
		  	
		        style.position="absolute";
				//style.posLeft=window.event.x+10;	    		
				//style.posTop=window.event.y-10;	
				 if((pos[0]+650)<window.screen.availWidth)
		       		style.posLeft=pos[0]+20;
		        else
			        style.posLeft=pos[0]-480;
			       
			    
			    if((pos[1]+500)<window.screen.availHeight)
		       		style.posTop=pos[1]+20;
		        else
			        style.posTop=pos[1]-80; 	  
	      }  
		  Element.show('date_panel');   
	}

	
	var IVersion=getBrowseVersion();

	if(IVersion>=8)
	{
	  	document.writeln("<link href=\"/report/report_status/reportStatusTableLocked_8.css\" rel=\"stylesheet\" type=\"text/css\">");
	}else
	{
	  	document.writeln("<link href=\"/report/report_status/reportStatusTableLocked.css\" rel=\"stylesheet\" type=\"text/css\">");
	}
	</script>
    <link href="../../css/css1.css" rel="stylesheet" type="text/css">
    <hrms:themes />  
    <style>
      body {TEXT-ALIGN: center;}
    </style>
  </head>
  <body>

   <form name="reportStatusForm" method="post" action="/report/report_status.do">
     <table  width="100%">
     <tr><td>
         <bean:message key="report.appealUnit"/>:&nbsp;${reportStatusForm.unitName}
     </td></tr>
     <tr><td>
   	<table width="<%=width %>" border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable">
					 ${reportStatusForm.tableHtml} 
   	</table>
   	</td></tr>
   	<tr><td>
   	<% if(!selfUnitcode.equals(unitCode)){ %>
   	
    <input type="button" class="mybutton" name="back" value="<bean:message key="kq.emp.button.return"/>" onclick="goback()" class="mybutton" >
   	
   	<% }else{ %>
   	<hrms:tipwizardbutton flag="report" target="il_body" formname="reportStatusForm"/>
   	<%} %>
   	</td></tr>
   	</table>
   	<div id="date_panel">
   			
    </div>
   	
   	
   </form>
  </body>
</html>
