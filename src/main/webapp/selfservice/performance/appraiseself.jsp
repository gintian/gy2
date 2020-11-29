<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="appraiseselfForm" scope="session" class="com.hjsj.hrms.actionform.performance.AppraiseselfForm"/>
<HTML>
<%
 
 int i=0;
 
 
 
%>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>

<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript">
        var ViewProperties=new ParameterSet();
	function doSubmit(value)
	{
	location.href="/selfservice/performance/appraiseself.do?b_query=link&planId="+value+"&planFlag=1";
	}
	//显示提示信息
	function showInformation(outparamters)
	{
	  var strcontent=outparamters.getValue("content");
	}
		
	function checkNum (xname)
	{
		var InString=xname.value;
		if(InString==null||InString=="")
		  return;
  	        var pars="objectid="+$F('objectId')+"&planId="+$F('planNum')+"&score="+InString+"&maxscore="+xname.title;
   	   	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showInformation,functionId:'90100130015'});
		return (false);
	}
	
</script>
<br>
<br>
<br>
<center>
<body> 
<html:form action="/selfservice/performance/appraiseself" method="get">
      <input type="hidden" name="objectId" value='<bean:write name="appraiseselfForm" property="userView.userId" filter="false"/>'>
      <table border="0" cellpadding="0" cellspacing="0" align="center">
      <tr>
            <td colspan="4" class="framestyle">
		 <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
			<tr class="list3" >
				<td align="center" nowrap width="100" valign="top">
				<bean:message key="lable.appraiseself.plan"/>
				</td>
				<td align="left" nowrap valign="top">
				   <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="4"
					sql="appraiseselfForm.strSQL" collection="list" scope="page"/> 
					<html:select name="appraiseselfForm" property="planNum" size="1" onchange="doSubmit(this.value);"> 
					<html:option value="#">请选择</html:option>
					<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
				   </html:select>
       				   <logic:equal name="appraiseselfForm" property="summaryflag" value="True">
				   	<a href="/selfservice/performance/summary_maintenance.do?b_query=link"><bean:message key="lable.performance.summary"/></a>
                		   </logic:equal>				   
     	     			   <logic:equal name="appraiseselfForm" property="status" value="0">
					<bean:message key="lable.performance.notedit"/>     	     			   
     	     			   </logic:equal>
     	     			   <logic:equal name="appraiseselfForm" property="status" value="1">
					<bean:message key="lable.performance.editing"/>      	     			   
     	     			   </logic:equal>
     	     			   <logic:equal name="appraiseselfForm" property="status" value="2">
					<bean:message key="lable.performance.edited"/>      	     			   
     	     			   </logic:equal>     	     			        	     			   				   
				</td>
			</tr>
			
			<tr class="list3">
				<td align="center" nowrap width="100" valign="top">&nbsp;</td>
				<td align="center" nowrap class="framestyle" width="700">&nbsp;
				    <bean:write name="appraiseselfForm" property="outHtml" filter="false"/>
				</td>
			</tr>
				
		</table>
    </td>
    </tr>
    </table>
        	<br>	
   <table>                                             
          <tr class="list3">
            <td align="center" >
     	     <logic:notEqual name="appraiseselfForm" property="status" value="2">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.appraiseselfForm.target='_self';validate('RS','planNum','活动计划');return (document.returnValue && ifqrbc());">
            	   <bean:message key="button.save"/>
	 	</hrms:submit>	            
         	<hrms:submit styleClass="mybutton" property="b_submit" onclick="document.appraiseselfForm.target='_self';validate('RS','planNum','活动计划');return (document.returnValue && ifqrtj());">
            	   <bean:message key="lable.enterfor.submit"/>
	 	</hrms:submit>
 	     </logic:notEqual>
            </td>
          </tr>          
      </table>
   
</html:form>
<%
	if(appraiseselfForm.getMessage().equals(""))
	{
	}
	else
	{
	%>
	<script>
		alert('<%=appraiseselfForm.getMessage()%>');
	</script>
	<%
	appraiseselfForm.setMessage("");
	appraiseselfForm.messageClear();
	}
%>
  </center>
  
  </body>
  </html>