<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>

<script language='javascript'>
	function enter()
	{
		var obj=eval('document.personnelEmployForm.ids');
		var id='';
		
		for(var i=0;i<obj.length;i++)
		{
			if(obj[i].checked)
			{
				id=obj[i].value;				
				break;
			}
		}
		if(id!='')
		{
			returnValue=id;
	        window.close();	
		}
		
		
	}


</script>


<body>

<html:form action="/hire/employSummarise/personnelEmploy">

	<table border='0' width='100%' hight='100%' >
		<tr>
			<td>
						<fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="hire.employSummarize.personnelEmploy.info"/></legend>
		                      			
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<% int i=0; %>
		                      				<logic:iterate id="element" name="personnelEmployForm" property="dbnameList"  offset="0"> 
												<tr>
												 <td width='20%' align="center"  nowrap>
												    <input type="radio" name="ids"   <%=(i==0?"checked":"")%>   value='<bean:write name="element" property="pre"   filter="false"/>'  />			
												 </td>
												 <td>
												 	<bean:write name="element" property="dbname"   filter="false"/>
												 </td>
												</tr>
											<% i++; %>
											</logic:iterate>		                 	
		                      			</table>
		                 </fieldset>
		                 <br>
		                &nbsp;&nbsp;<br>&nbsp; <input type='button' class='mybutton' value='<bean:message key="kq.formula.true"/>' onclick='enter()' />
			
			</td>
		</tr>
	</table>

</html:form>
	

</body>

</html>



