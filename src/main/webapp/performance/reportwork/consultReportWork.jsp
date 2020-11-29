<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hrms.struts.constant.SystemConfig,
                 com.hjsj.hrms.actionform.performance.reportwork.ReportWorkForm,
                 com.hjsj.hrms.utils.ResourceFactory,
                 com.hrms.struts.constant.SystemConfig" %>

<%

	ReportWorkForm reportWorkForm=(ReportWorkForm)session.getAttribute("reportWorkForm");
	String summarystate=reportWorkForm.getSummaryState()!=null?reportWorkForm.getSummaryState():"0";
	String summarydesc="未提交";
	if(summarystate.equals("1"))
		summarydesc="已提交";
	else if(summarystate.equals("2"))
		summarydesc="已批准";
	else if(summarystate.equals("3"))
		summarydesc="驳回";	

	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
		userView.getHm().put("fckeditorAccessTime", new Date().getTime());
	}
 %>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/performance/reportwork/consultReportWork">
<br>
<br>
 

<table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
		         <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=180 align=center class="tabcenter">&nbsp;
					<bean:message key="lable.performance.personalReport"/>(<%=summarydesc%>)
					&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> -->
       		<td  align=center class="TableRow">&nbsp;
					<bean:message key="lable.performance.personalReport"/>(<%=summarydesc%>)
					&nbsp;</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
            
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	  	 <td align="left" nowrap >
                 	     <html:textarea name="reportWorkForm" property="content" cols="80" rows="40"   style="display:none;"  />
                 	     <script type="text/javascript">
					              var oldInputs = document.getElementsByName('content');                             
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					              oFCKeditor.BasePath	= '/fckeditor/';
					              
					              oFCKeditor.Height	= 500 ;			
					              oFCKeditor.Width	=590;			            
					              oFCKeditor.ToolbarSet='Apply';
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
           
                          </script>
                          
                          </td>
                      </tr>
                      
                      
                      
                      
                       <tr class="list3">
                	  	 <td align="left"  valign='top' nowrap >
                	  	  <table   width='100%' ><tr><Td width='40%' valign='top' >
                	  	 
                	  	 
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td></tr>
                 	   		  <tr>
                 	   		  <td>
                 	   		  
                 	   		  <table border="0" cellpmoding="0" cellspacing="0" cellpadding="0"  >
                 	   		  <logic:iterate id="element" name="reportWorkForm" property="summaryFileIdsList" >
                 	   		    <tr><td>&nbsp;&nbsp; 
                 	   		    <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   			 <bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>&nbsp;
                 	   		   </td></tr>
                 	   		  </logic:iterate>
                 	   		  </table>
                 	     	  </td>
                 	     	  </tr></table>
                 	     	  </td></tr></table>
                          </td>
             	  </tr>
		</table>
	</td></tr></table>





<table width="590" border="0" cellpadding="0" cellspacing="0" align="center">
<tr  >
     <td align='center' style="height:35px;" >
	      <html:button  styleClass="mybutton" property="b_next" onclick="window.history.back();">
	            		<bean:message key="button.return"/>
		 		</html:button> 
		 		</td>
		 		</tr>
</table>



</html:form>