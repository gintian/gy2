<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				 java.util.*,
				 com.hrms.hjsj.utils.Sql_switcher,
                 com.hjsj.hrms.actionform.report.report_analyse.ReportAnalyseForm"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>   
<%@ page import="com.hrms.struts.constant.SystemConfig"%>              
<%
    UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	String name=userView.getUserFullName();
	 ReportAnalyseForm reportAnalyseForm=(ReportAnalyseForm)session.getAttribute("reportAnalyseForm");	
	 String[] right_fields=reportAnalyseForm.getRight_fields();
	 StringBuffer temp_str=new StringBuffer("");
	 if(right_fields.length<2&&
	 right_fields[0].indexOf("@@")!=-1){
	 String[] temp = right_fields[0].split("@@");
	 for(int i=0;i<temp.length;i++){
	 temp_str.append("~"+temp[i]);
	 }
	 
	 }else{
	 
	 for(int i=0;i<right_fields.length;i++)
	 { 
	 	temp_str.append("~"+right_fields[i]);
	 }
	 }
	 /**
	 String aurl = (String)request.getServerName();
	String port=request.getServerPort()+"";
	String prl=request.getProtocol();
	int idx=prl.indexOf("/");
	prl=prl.substring(0,idx);    
	String url_s=prl+"://"+aurl+":"+port;
	**/
	String url_p=SystemConfig.getServerURL(request); 
	String dbtype=String.valueOf(Sql_switcher.searchDbServer());
	String fields=userView.getFieldpriv().toString();
	String tables=userView.getTablepriv().toString();
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
  	String license=lockclient.getLicenseCount();
   	int version=userView.getVersion();
  	 if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
	
%>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script language="JavaScript" src="/js/meizzDate.js"></script>
	<SCRIPT LANGUAGE=javascript>

	  
	  
	  function exportExcel()
	 {
	 	var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",'${reportAnalyseForm.reportTabid}');
	    hashvo.setValue("unitcode" ,'${reportAnalyseForm.unitcode}');
	    hashvo.setValue("nums",'${reportAnalyseForm.nums}');
	    hashvo.setValue("cols",'${reportAnalyseForm.cols}');
	    hashvo.setValue("temp_str","<%=(temp_str.substring(1))%>");
	    hashvo.setValue("yearid",'${reportAnalyseForm.yearid}');
	    hashvo.setValue("countid" ,'${reportAnalyseForm.countid}');
	    hashvo.setValue("weekid2",'${reportAnalyseForm.weekid2}');
	    hashvo.setValue("totalnum",'${reportAnalyseForm.totalnum}');
		var request=new Request({method:'post',asynchronous:false,onSuccess:outFile,functionId:'03040000018'},hashvo);			
	 }
	 
	 
	 
	 function outFile(outparamters)
	 {
		 var outName=outparamters.getValue("outName");
	    window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;	
	 }
	  
	  
	  
	
	
	function scolse(){
	window.close();
	}
	function closeParent(){
		opener.window.close();
	}
	</SCRIPT>
	<link href="/css/css1_report.css" rel="stylesheet" type="text/css">
	<hrms:themes />
	<body  style="overflow:auto;" onload="closeParent();" >
 
 <html:form action="/report/report_analyse/reportanalyse">	
 <table   border='0' cellspacing='0'  align='center' cellpadding='1' style='position:absolute;top:1;left:12;height:30'> 
  <tr  valign='middle' align='center'> <td>&nbsp;</td>
  <td  valign='middle' align='left' width='99.0%' >
  <hrms:priv func_id='2904002'>
   <input type="button" name="p01" value="<bean:message key="general.inform.muster.output.excel"/>" class="mybutton" onclick="exportExcel()"/>
   </hrms:priv>
    <input type="button" name="p21" value="<bean:message key="button.close"/>" class="mybutton" onclick="scolse()"/>
  </td></tr></table>
  <br>
 
 
 
 
 
 
 	${reportAnalyseForm.html}
 
 </html:form>
 
 </body>
	