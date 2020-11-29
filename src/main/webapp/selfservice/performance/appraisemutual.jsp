<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="appraiseMutualForm" scope="session" class="com.hjsj.hrms.actionform.performance.AppraiseMutualForm"/>

<%
  int i=0;
 
 %>

<br>
<br>
<br>
<center>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>

<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript">
        var ViewProperties=new ParameterSet();
	function doSubmitTest(value)
	{
	   location.href="/selfservice/performance/appraisemutual.do?b_query=link&planId="+value+"&planFlag=1";
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
//		if(isNaN(InString))
//		{
//		  alert(" 必须填写数字！");
//		  xname.value='';
//		  xname.select();
//		  return (true);
//		}
		return (false);
	}

	function doSearch(value)
	{
		location.href="/selfservice/performance/appraisemutual.do?b_search=link&objectId="+value+"&planFlag=1";	
	}
	
</script>
<html:form action="/selfservice/performance/appraisemutual">
      <table border="0" cellpadding="0" cellspacing="0" align="center">
      <tr>
            <td colspan="4" class="framestyle">
				 <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
					<tr class="list3" >
						<td align="center" nowrap width="100" valign="top"><bean:message key="lable.appraiseself.plan"/></td>
							<td align="left" nowrap valign="top">
							
								<hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="4"
									sql="appraiseMutualForm.strSQL2" collection="list" scope="page"/> 
									
									<html:select name="appraiseMutualForm" property="planNum" size="1" onchange="doSubmitTest(this.value);"> 
									<html:option value=""><bean:message key="label.select"/></html:option>
									<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
									</html:select>
								
							</td>
					</tr>
					<tr class="list3" >
						<td align="center" nowrap width="100" valign="top"><bean:message key="lable.appraisemutual.examineobject"/></td>
							<td align="left" nowrap valign="top">
								
															
									 <hrms:importgeneraldata showColumn="a0101" valueColumn="object_id" flag="true" paraValue="" 
											sql="appraiseMutualForm.strSQL" collection="list2" scope="page"/>
										 <html:select name="appraiseMutualForm" property="objectId" size="1" onchange="doSearch(this.value);">
										 <html:option value=""><bean:message key="label.select"/></html:option>
										 <html:options collection="list2" property="dataValue" labelProperty="dataName"/>
										</html:select>
       				   					<logic:equal name="appraiseMutualForm" property="summaryflag" value="True">
				   						<a href="/selfservice/performance/view_summary.do?b_query=link" target="_blank"><bean:message key="lable.performance.viewsummary"/></a>
                		  				 	</logic:equal>
                         	     			   <logic:equal name="appraiseMutualForm" property="status" value="0">
                    						<bean:message key="lable.performance.notedit"/>     	     			   
                         	     			   </logic:equal>
                         	     			   <logic:equal name="appraiseMutualForm" property="status" value="1">
                    						<bean:message key="lable.performance.editing"/>      	     			   
                         	     			   </logic:equal>
                         	     			   <logic:equal name="appraiseMutualForm" property="status" value="2">
                    						<bean:message key="lable.performance.edited"/>      	     			   
                         	     			   </logic:equal>                  		  				 												
							</td>
					</tr>
					<tr class="list3">
							<td align="center" nowrap width="100" valign="top">&nbsp;</td>
							<td align="center" nowrap class="framestyle" width="700">&nbsp;
							<bean:write name="appraiseMutualForm" property="outHtml" filter="false"/>
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
     	     <logic:notEqual name="appraiseMutualForm" property="status" value="2">            	
         	<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	</hrms:submit>	 
         	<hrms:submit styleClass="mybutton" property="b_submit">
            	   <bean:message key="lable.enterfor.submit"/>
	 	</hrms:submit> 	 		
 	     </logic:notEqual>        		 	           
            </td>
          </tr>          
      </table>
   
</html:form>
<%
	if(appraiseMutualForm.getMessage().equals(""))
	{
	}
	else
	{
	%>
	<script>
		alert('<%=appraiseMutualForm.getMessage()%>');
	</script>
	<%
	appraiseMutualForm.setMessage("");
	appraiseMutualForm.messageClear();
	}
%>
  </center>