<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.report_state.ReportStatePanelForm,com.hjsj.hrms.actionform.report.report_state.ReportStateForm"%>
<%
	ReportStatePanelForm rspf = (ReportStatePanelForm)session.getAttribute("reportStatePanelForm");
	ReportStateForm rsf = (ReportStateForm)session.getAttribute("reportStateForm");
	String code = rspf.getCode();
	String returnflag = rsf.getReturnflag();
	String dmltab="";
	String sorturl = "/report/report_state/reportstatesort.do?b_query=link&code="+code+"&returnvalue="+returnflag;
	String tnameurl = "/report/report_state/reportstate.do?b_query=link&code="+code+"&returnvalue="+returnflag;
	
	if(session.getAttribute("dmltab")!=null)
	{
		dmltab=(String)session.getAttribute("dmltab");
	}
%>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script type="text/javascript">
		function refresh(){
			parent.mil_menu.refresh1();
		}
</script>
<body>

</body>
<script langugage='javascript' >

Ext.onReady(function(){
  		Ext.widget('viewport',{
	 		layout:'fit',
	 		renderTo : Ext.getBody(),
	 		items:{
	 			xtype:'tabpanel',
	 				id:'cardTabPanel',
	 				height:'98%',
	 				activeTab:<%= "report_tname".equals(dmltab)?1:0 %>,
	 				margin:'6 0 0 4',
	 				items:[{title:'<bean:message key="report.report_type" />',
	 					html:'<iframe src="<%=sorturl%>" width="100%" height="100%" frameborder=0 />'
	 					},{
	 						title:'<bean:message key="report.report_name" />',
	 						html:'<iframe src="<%=tnameurl%>" width="100%" height="100%" frameborder=0 />'
	 					}]
	 		  }
  		});
	});
</script>