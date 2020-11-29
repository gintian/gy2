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
	if(operater==1||operater==4)
	{
		markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=search";
		markStatusForm.submit();
	}
	else
		history.go(-1);
		//markStatusForm.action="/selfservice/performance/singleGrade.do?b_query=link";

}

function resets()
{
	markStatusForm.description.value="";
	markStatusForm.isNoMark.checked=false;
}

function save()
{
	//markStatusForm.description.value=replaceAll(markStatusForm.description.value,'\r\n','@#@');
	if(operater==1||operater==4)
		markStatusForm.action="/performance/markStatus/markStatusList.do?b_saveDesc=save";
	else
		markStatusForm.action="/performance/markStatus/markStatusList.do?b_saveDesc2=save";
	if(markStatusForm.isNoMark.checked==false)
	{
		markStatusForm.isNoMark.value="0";
		markStatusForm.isNoMark.checked=true;
	}
	markStatusForm.submit();
}


function window_close()
{
	window.close();
}

//添加这个方法，目的是如果不勾选不让写内容，没有意义
//因为现在后台都是根据这个进行判断的，所以必须得有这个不勾选选项2018-06-19
function changeTextareaStatus() {
	if(markStatusForm.isNoMark.checked==false)
    {
		document.getElementsByName("description")[0].disabled = "true";
	}else {
		document.getElementsByName("description")[0].removeAttribute('disabled');
	}
}

</script>


<html:form action="/performance/markStatus/markStatusList">

      <table width="570" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:60px">
          <tr height="20">
       		<td align='left' class="TableRow_lrt">
       		
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
			 			
						<html:textarea name="markStatusForm" property="description" cols="60" rows="16"/>		 						 			
			 			</td>
			 		</tr>
		          <tr class="list3">
		            <td align="left" colspan="2">
					<br>
						<table id='isMark' ><tr><td>
							
						<logic:equal  name="markStatusForm"  property="isNoMark"  value="7">
							&nbsp;<html:checkbox name="markStatusForm" onclick="changeTextareaStatus()" property="isNoMark"  value="7" /> </td><td>不打分</td>
						</logic:equal>
						<logic:notEqual  name="markStatusForm"  property="isNoMark"  value="7" >
						   &nbsp;<html:checkbox name="markStatusForm"  onclick="changeTextareaStatus()" property="isNoMark"  value="4" /> </td><td>不打分</td>
						</logic:notEqual>					
						</tr></table> 
		            </td>
		          </tr>   
                                                
    		  </table>
    		</td>
    	 </tr>
     </table>

     <table width="570" border="0" cellpadding="0" cellspacing="0" align="center" style="padding:5px">
     <tr height="30">
     <td  align="center">
     
     <logic:equal  name="markStatusForm"  property="status"  value="1">
	      <html:button  styleClass="mybutton" property="b_next" onclick="save()">
	            		<bean:message key="kq.kq_rest.submit"/>
		 		</html:button> 
		  <html:button  styleClass="mybutton" property="b_next" onclick="resets()">
	            		<bean:message key="kq.kq_rest.reset"/>
		 		</html:button> 
	 </logic:equal>	 		
		 		
		  <html:button  styleClass="mybutton" property="b_next" onclick="goback()">
	            		<bean:message key="kq.search_feast.back"/>
		 		</html:button> 
	</td>
	</tr>
	</table>
</html:form>

<script language='javascript'>
	
	
	
	
if(operater==3)
{
	var obj=eval("isMark");
	obj.style.display='none';
}
changeTextareaStatus();	
	
</script>