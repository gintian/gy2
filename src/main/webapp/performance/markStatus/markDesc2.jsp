<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
<script language='javascript'>
var operater=<%=request.getParameter("operater")%>	
function goback()
{
	window.close();
}

function resets()
{
	markStatusForm.description.value="";
	markStatusForm.isNoMark.checked=false;
}



function returnInfo3(outparamters)
{
		window.close();
}


</script>

<body>
<html:form action="/performance/markStatus/markStatusList">
	
      <table width="580" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td class="TableRow_lrt" align='left'>
       		
       		
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
            	<br>
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
			 		<tr>
			 			<td width="10%" vAlign='top' align="right" ><br>
			 			&nbsp;<bean:message key="report.conter"/>：&nbsp;
			 			</td>
			 			<td width="90%"  >
			 			<br>
			 						 				
						<html:textarea name="markStatusForm" onblur="change()" property="description" cols="75" rows="16" style=""/>
			 			</td>
			 		</tr>
		          <tr class="list3">
		            <td align="left" colspan="2">
					<br>
						<table id='isMark' ><tr><td>
					&nbsp;<html:checkbox name="markStatusForm"   property="isNoMark"  value="4" /> </td><td>不打分</td>
						</tr></table> 
		            </td>
		          </tr>   
                                                
    		  </table>
    		</td>
    	 </tr>
     </table>

     <table width="580" border="0" cellpadding="0" cellspacing="0" align="center">
     <tr height="30">
     <td align="center">
     <% 
     //当领导查阅考核对象自我考评填的意见或建议时,不允许编辑其内容
     if(request.getParameter("edit")==null||!request.getParameter("edit").equals("false"))
     {
      %>
     
     <logic:equal  name="markStatusForm"  property="status"  value="1">
	      <html:button  styleClass="mybutton" property="b_next" onclick="save()">
	            		<bean:message key="kq.kq_rest.submit"/>
		 		</html:button> 
		  <html:button  styleClass="mybutton" property="b_next" onclick="resets()">
	            		<bean:message key="kq.kq_rest.reset"/>
		 		</html:button> 
	 </logic:equal>	 		
   <% } %>	
		  <html:button  styleClass="mybutton" property="b_next" onclick="goback()">
	            		<bean:message key="kq.search_feast.back"/>
		 		</html:button> 
	</td>
	</tr>
	</table>
</html:form>
</body>

<script language='javascript'>
	
	
	
	
if(operater==3)
{
	var obj=eval("isMark");
	obj.style.display='none';
}
	
	

function save()
{
	var description=document.markStatusForm.description.value;
	if(description.length>3000){
		alert(KH_TEXT_MAXLENGTH);
		return;
	}
	var hashvo=new ParameterSet();	
	hashvo.setValue("planID","<%=request.getParameter("planID")%>");	
	hashvo.setValue("objectID","<%=request.getParameter("objectID")%>");
	hashvo.setValue("mainbodyID","<%=request.getParameter("mainbodyID")%>");
	hashvo.setValue("operater",operater);
	hashvo.setValue("isNoMark","${markStatusForm.isNoMark}");
	hashvo.setValue("type","<%=request.getParameter("type")%>");
	hashvo.setValue("performanceType","${markStatusForm.performanceType}");	
	var a_context=description;	
	hashvo.setValue("description",getEncodeStr(a_context));
	var In_paramters="flag=flag"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'90100170004'},hashvo);
}

function change(){
	var description=document.markStatusForm.description.value;
	if(description.length>3000)
		alert(KH_TEXT_MAXLENGTH);
}	
	
</script>