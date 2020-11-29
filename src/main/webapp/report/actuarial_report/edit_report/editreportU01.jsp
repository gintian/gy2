<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="com.hjsj.hrms.actionform.report.actuarial_report.edit_report.EditReport_actuaialForm"%>
<%
	EditReport_actuaialForm editReport_actuaialForm=(EditReport_actuaialForm)session.getAttribute("editReport_actuaialForm");
    String selfUnitcode=editReport_actuaialForm.getSelfUnitcode();
	String unitcode=editReport_actuaialForm.getUnitcode();
	String flagSub=editReport_actuaialForm.getFlagSub();
	String rootUnit= editReport_actuaialForm.getRootUnit();
	String isCollectUnit= editReport_actuaialForm.getIsCollectUnit();
%>
<html>
  <head>
   
  </head>
  <script language="javascript">
  function saveq()
  {
  	 alert("保存成功！");
     editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU01.do?b_save=link&submit=false";
     editReport_actuaialForm.submit();
  }
  function submitq()
  {
  	 if(confirm("请确认执行提交操作!"))
  	 {
	     editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU01.do?b_submit=link&submit=true";
	     editReport_actuaialForm.submit();
	 }
  }
  function returnq()
  {
    <logic:equal name="editReport_actuaialForm" property="from_model" value="edit"> 
     editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_query=link";
    </logic:equal>
    <logic:equal name="editReport_actuaialForm" property="from_model" value="collect">
    document.editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}";
    </logic:equal>
      editReport_actuaialForm.submit();
  }
  function reject()
  {
      var arguments=new Array();
		arguments[0]="";
		arguments[1]="驳回原因";  
	    var strurl="/gz/gz_accounting/rejectCause.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    if(ss)
		{
			var hashvo=new ParameterSet();	
			hashvo.setValue("cycle_id",'${editReport_actuaialForm.id}');
			hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
			hashvo.setValue("report_id","U01");
			hashvo.setValue("cause",getEncodeStr(ss[0]));
			var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'03060000218'},hashvo);
		}
  }
  function success(outparamters)
{
			document.editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}";
			document.editReport_actuaialForm.submit();

}
function showwarning()
	{
		var thecodeurl ="/report/actuarial_report/edit_report/editreportlist.do?b_queryWarning=link";
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
				
	}
  </script>
  <hrms:themes />
  <body>
  <html:form action="/report/actuarial_report/edit_report/editreportU01">
    <html:hidden name="editReport_actuaialForm" property='flag' styleClass="text"/>
    <html:hidden name="editReport_actuaialForm" property='unitcode' styleClass="text"/>
    <html:hidden name="editReport_actuaialForm" property='id' styleClass="text"/>
  <table width="500" border="0" cellpadding="1" cellspacing="0" align="center">
    <tr height="20">
     <!-- td width=8 valign="top" class="tableft"></td>
     <td width=160 align=center class="tabcenter">表1-特别事项</td>
     <td width=8 valign="top" class="tabright"></td>
     <td valign="top" class="tabremain" width="500"></td -->  
     <td align="left" colspan="4" class="TableRow" style="padding-left: 5px;">表1-特别事项</td>             	      
   </tr> 
   <tr>
     <td colspan="4" class="framestyle3">
        <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
           <logic:iterate id="element" name="editReport_actuaialForm"  property="fieldlsitU01" indexId="index"> 
               <logic:equal name="element" property="visible" value="true">
                   <tr> 
                     <td valign="top">
                         <bean:write  name="element" property="itemdesc" filter="true"/>
                     </td>
                   </tr> 
                   <tr align="center"> 
                     <td height="80" width="490" align="center">
                      
                     <logic:equal name="editReport_actuaialForm" property="flag" value="1">
                        <html:textarea name="editReport_actuaialForm" property='<%="fieldlsitU01["+index+"].value"%>' styleId='${element.itemid}' cols="92" rows="8" styleClass="text5" readonly="true"  />
                     </logic:equal>
                     <logic:notEqual name="editReport_actuaialForm" property="flag" value="1">
                     	<logic:notEqual name="editReport_actuaialForm" property="opt" value="0">
                        	<html:textarea name="editReport_actuaialForm" property='<%="fieldlsitU01["+index+"].value"%>' styleId='${element.itemid}' cols="92" rows="8" styleClass="text5"/>
                        </logic:notEqual>
                     	<logic:equal name="editReport_actuaialForm" property="opt" value="0">
                    		<html:textarea name="editReport_actuaialForm" property='<%="fieldlsitU01["+index+"].value"%>' styleId='${element.itemid}' cols="92" rows="8" styleClass="text5" readonly="true"  />
                    	</logic:equal>
                     </logic:notEqual>
                     
                     </td>
                   </tr>
               </logic:equal>
           </logic:iterate>
        </table>
     </td>
   </tr>  
    <tr>
     <td colspan="4" align="center" style="padding-top: 4px;">
     <logic:equal name="editReport_actuaialForm" property="opt" value="1">
	     <logic:equal name="editReport_actuaialForm" property="flag" value="0">
	        <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.save"/>' onclick="saveq();">   
	     </logic:equal>
	     <logic:equal name="editReport_actuaialForm" property="flag" value="-1">
	        <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.save"/>' onclick="saveq();">   
	     </logic:equal>
	     <logic:equal name="editReport_actuaialForm" property="flag" value="2">
	       <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.save"/>' onclick="saveq();">   
	     </logic:equal>
	     </logic:equal>
        <logic:equal  name="editReport_actuaialForm"  property="reportStatus" value="1">
         <logic:equal  name="editReport_actuaialForm"  property="cycleStatus" value="04">	
 	           <% if(rootUnit.equals("1")||(!selfUnitcode.equals(unitcode)&&!flagSub.equals("1"))){ %>
 	           <input type='button' id="button_goback" value=' 驳回 ' onclick='reject();' class="mybutton">
 	             <% } %>
              </logic:equal>
          </logic:equal>
           <%if(isCollectUnit.equals("1")){ %>
<input type='button' id="reject1" value='信息列表'  onclick='showwarning();' class="mybutton">
<%} %>
       <input type="button" class="mybutton"  name="dd" value='<bean:message key="button.return"/>' onclick="returnq();">              
     </td>
   </tr>
  </table>
  
  </html:form>
  </body>
</html>
