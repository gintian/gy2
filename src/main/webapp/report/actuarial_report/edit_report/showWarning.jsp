<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script> 
<hrms:themes />
<style type="text/css"> 
#dis_sort_table {
           border: 1px solid #eee;
           height: 230px;    
           width: 230px;            
           overflow: auto;            
           margin: 1em 1;
}
</style>
<%int i=0;
 %>
<html:form action="/report/actuarial_report/edit_report/editreportlist">
<div class='fixedDiv2'>
	<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
				<tr>
					
					<td align="center" class="TableRow" nowrap style="border-left:none;border-top:none;">
						<bean:message key="report.b0110" />
						&nbsp;
					</td>
					<logic:equal name="editReport_actuaialForm" property="report_id" value="U03">
					<td align="center" class="TableRow" nowrap style="border-right:none;border-top:none;">
						<bean:message key="report.warning" />
						&nbsp;
					</td>
					</logic:equal>
						<logic:equal name="editReport_actuaialForm" property="report_id" value="U05">
					<td align="center" class="TableRow" nowrap style="border-right:none;border-top:none;">
						<bean:message key="report.warning" />
						&nbsp;
					</td>
					</logic:equal>
					<logic:equal name="editReport_actuaialForm" property="report_id" value="U01">
					<td align="center" class="TableRow" nowrap style="border-right:none;border-top:none;">
						<bean:message key="report.u01.u0101" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap style="border-right:none;border-top:none;">
						<bean:message key="report.u01.u0103" />
						&nbsp;
					</td>
					</logic:equal>
				</tr>
			</thead>
				<logic:iterate id="element" name="editReport_actuaialForm" property="warninglist" >
				
				<%
					if (i % 2 == 0) {
				%>
				<tr class="trShallow">
					<%
						} else {
					%>
				
				<tr class="trDeep">
					<%
						}
									i++;
					%>
				<%
				 LazyDynaBean a_bean=(LazyDynaBean)pageContext.getAttribute("element");
				 %>
					<td align="left" class="RecordRow" nowrap width="100"
						style="word-break: break-all;border-left:none;border-top:none;">
						&nbsp;<bean:write name="element" property="unitname" filter="false" />
						&nbsp;

					</td>
					<logic:equal name="editReport_actuaialForm" property="report_id" value="U03">
					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all;border-right:none;border-top:none;">
						&nbsp;<bean:write name="element" property="t3_desc" filter="false" />
						&nbsp;

					</td>
					</logic:equal>
					<logic:equal name="editReport_actuaialForm" property="report_id" value="U05">
					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all;border-right:none;border-top:none;">
						&nbsp;<bean:write name="element" property="t5_desc" filter="false" />
						&nbsp;

					</td>
					</logic:equal>
					<logic:equal name="editReport_actuaialForm" property="report_id" value="U01">
					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all;border-right:none;border-top:none;">
						&nbsp;<bean:write name="element" property="u0101" filter="false" />
						&nbsp;

					</td>
				   <td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all;border-right:none;border-top:none;">
						&nbsp;<bean:write name="element" property="u0103" filter="false" />
						&nbsp;

					</td>
					
					</logic:equal>

				</tr>
			</logic:iterate>
			
		</table>
		 </div>
<table  width="70%" align="center">
		  <tr>
		  	<td height="0px"></td>
		  </tr>
          <tr>
            <td align="center" style="border-right:none;border-top:none;">	           
  	       <input type='button' id="button_goback" value='输出excel' onclick='exportExcel2();' class="mybutton">   
  	       <input type='button' id="button_goback" value='&nbsp;关闭&nbsp;' onclick='window.close();' class="mybutton">    	          	   	 	  
            </td>
          </tr>          
</table>
</html:form>

<script language='javascript' >
function exportExcel2(){
	var hashvo=new ParameterSet();	
	hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
	hashvo.setValue("id",'${editReport_actuaialForm.id}');  
	hashvo.setValue("report_id",'${editReport_actuaialForm.report_id}'); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile2,functionId:'03060000237'},hashvo);
}
function showfile2(outparamters)
{
	var fileName=outparamters.getValue("fileName");
		//var name=fileName.substring(0,fileName.length-1)+".xls";
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName,"excel");
}
</script>
