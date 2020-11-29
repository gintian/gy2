<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.markStatus.markStatusForm,
				 com.hrms.struts.taglib.CommonData,
				com.hrms.struts.constant.SystemConfig" %>
				
<%
	markStatusForm _markStatusForm=(markStatusForm)session.getAttribute("markStatusForm");
	String isNoMark=_markStatusForm.getIsNoMark();
 %>
<hrms:themes />
<script language='javascript'>
function resets()
{
	markStatusForm.description.value="";	
}

function setDescctrl(obj)
{
	var hashvo=new ParameterSet();	
	hashvo.setValue("planID","<%=request.getParameter("planID")%>");	
	hashvo.setValue("objectID","<%=request.getParameter("objectID")%>");
	hashvo.setValue("mainbodyID","<%=request.getParameter("mainbodyID")%>");
	if(obj.checked)
		hashvo.setValue("descctrl","0");
	else
		hashvo.setValue("descctrl","1");
	var request=new Request({method:'post',asynchronous:false,functionId:'90100170005'},hashvo);

}

function save()
{
	var hashvo=new ParameterSet();			
		hashvo.setValue("planID","<%=request.getParameter("planID")%>");	
		hashvo.setValue("objectID","<%=request.getParameter("objectID")%>");
		hashvo.setValue("mainbodyID","<%=request.getParameter("mainbodyID")%>");
		hashvo.setValue("performanceType","${markStatusForm.performanceType}");	
		hashvo.setValue("body_id","<%=request.getParameter("body_id")%>");	
		var description=document.markStatusForm.description.value;
		var a_context=description;
		
	<%  
		if(SystemConfig.getPropertyValue("clientName")!=null && SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("bjga")) 
		{	
	%>
			if(a_context!=null && a_context!='' && a_context.length>220) // 考虑用户输入的空格和回车，所以判断时多加20个字符
			{
				alert("开放式意见过长，请控制在200字以内！");
				return;
			}	     			
	<%	}%>
					
		hashvo.setValue("description",getEncodeStr(a_context));
		var In_paramters="flag=flag"; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'90100170005'},hashvo);
}


function returnInfo3(outparamters)
{
	if(window.showModalDialog)	
		window.close();
	else if(window.opener)//保存没有关闭open弹窗  bug 35948  wangb 20180228 
		window.close();
	else
		parent.parent.Ext.getCmp("showWindowWin").close();
}


</script>


<html:form action="/performance/markStatus/markStatusList">
	
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td align='left' class='TableRow_lrt'>
       		
       		<%
        		 String type="0";
       			 if(request.getParameter("type")!=null)
       			 	type=request.getParameter("type");
       			 
       		
       			 if(type.equals("0")){ 
       		 %>
       			<bean:message key="lable.performnace.declareCause"/>
			<% } else { %>
				<bean:message key="performance.implement.otherinfo"/>
			<%  }  %>
       		</td>   	      
          </tr> 
          <tr>
            <td class="framestyle">
            	
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
			 		<tr>
			 			<td width="10%" vAlign='top' align="right" ><br>
			 			&nbsp;<bean:message key="report.conter"/>：&nbsp;
			 			</td>
			 			<td width="90%" style='padding-top:5px;' >

                            <% if(request.getParameter("opt")==null||!request.getParameter("opt").equals("read")){
                                if(!"2".equals(isNoMark) && !"7".equals(isNoMark)){%>
                                    <html:textarea name="markStatusForm" property="description" cols="60" rows="16"/>
                                <%}else{%>
                                    <html:textarea name="markStatusForm" property="description" readonly="true" cols="60" rows="16"/>
                            <%  }
                            }else{%>
                                <html:textarea name="markStatusForm" property="description" readonly="true" cols="60" rows="16"/>
                            <%}%>
			 			</td>
			 		</tr>
			 		
			 		<tr class="list3">
		            <td align="left" colspan="2"  <logic:equal name="markStatusForm" property="plan_type"  value="0"> style="display:none" </logic:equal>  >
					
					<logic:equal name="markStatusForm" property="method"  value="1">
						<%
					 
						
			       			 if(type.equals("0")){ 
			       			 	if((request.getParameter("opt")==null||!request.getParameter("opt").equals("read"))&&isNoMark!=null&&!(isNoMark.equals("2")||isNoMark.equals("7")))
			       			 	{
			       			 	%>
			       			 	匿名 <html:checkbox  name="markStatusForm"  property="descctrl" value='0'  onclick='setDescctrl(this)'   ></html:checkbox> 
			       			 	<%
			       			 	}
			       			 	else
			       			 	{
			       				 %>
								
								匿名 <html:checkbox  name="markStatusForm"  disabled='true'  property="descctrl" value='0'  onclick='setDescctrl(this)'   ></html:checkbox> 
								
								<%
								}
								
								 } 
						 
						 
						 %>
					</logic:equal>
					</td>
					</tr>         
                                                
    		  </table>
    		</td>
    	 </tr>
     </table>

     <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
     <tr height="30">
     <td  align="center">
    <% if(request.getParameter("opt")==null||!request.getParameter("opt").equals("read")){%>
    <logic:notEqual name="markStatusForm"  property="isNoMark"  value="2">
    	 <logic:notEqual name="markStatusForm"  property="isNoMark"  value="7">
	      <html:button  styleClass="mybutton" property="b_next" onclick="save()">
	            		<bean:message key="kq.kq_rest.submit"/>
		 		</html:button> 
		  <html:button  styleClass="mybutton" property="b_next" onclick="resets()">
	            		<bean:message key="kq.kq_rest.reset"/>
		 		</html:button> 
		 </logic:notEqual>
	</logic:notEqual>	
	<% } %>
	</td>
	</tr>
	</table>
</html:form>

<script language='javascript'>
	
	
	
</script>