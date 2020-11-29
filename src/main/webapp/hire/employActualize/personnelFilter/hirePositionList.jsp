<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>


<script language='javascript'>

  function saves()
  {
  	    var id=""; 
  		var obj=eval('document.hirePositionForm.id');
  		if(obj.length)
  		{
	  		for(var i=0;i<obj.length;i++)
	  		{
	  			if(obj[i].checked==true)
	  				id=obj[i].value;
	  		}
	  	}
	  	else
	  	{
	  		if(obj.checked==true)
	  				id=obj.value;
	  	}
  		if(id=="")
  		{
  			alert(SELECT_TO_APPLY_POSITION+"!");
  			return;
  		}  	
  		
		var hashvo=new ParameterSet();
		hashvo.setValue("a0100","${hirePositionForm.a0100}");	
		var In_paramters="posID="+id;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000153'},hashvo);
  }

  function returnInfo(outparamters)
  {
	  alert(SAVESUCCESS+"!");
  }


  function goback()
  {
  		hirePositionForm.action="/templates/menu/hire_m_menu2.do?b_query=link&module=7";
  		hirePositionForm.target="i_body";
  		hirePositionForm.submit();
  	
  }

</script>




<html:form action="/hire/employActualize/personnelFilter/hirePositionList">

	<Br>

	<table id='ta'  width="95%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">

		<thead>
			<tr>
				 <td align="center" class="TableRow" nowrap>
					&nbsp;
				 </td>
				  <td align="center" class="TableRow" nowrap>
					&nbsp;<bean:message key="column.sys.pos"/>&nbsp;
				 </td>
				  <td align="center" class="TableRow" nowrap>
					&nbsp;<bean:message key="workbench.pos.posname"/>&nbsp;
				 </td>
				  <td align="center" class="TableRow" nowrap>
					&nbsp;<bean:message key="lable.resource_plan.org_id"/>&nbsp;
				 </td>
				  <td align="center" class="TableRow" nowrap>
					&nbsp;<bean:message key="lable.zp_plan.name"/>&nbsp;
				 </td>
				
			</tr>	
		</thead>
		
		<% int i=0; String className="trShallow"; %>	
		<hrms:extenditerate id="element" name="hirePositionForm" property="positionListform.list" indexes="indexes" pagination="positionListform.pagination" pageCount="15" scope="session">
			 <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
			<tr class='<%=className%>' >
				
	           		    <td align="center" class="RecordRow"  nowrap>
	           		    	<bean:write name="element" property="id" filter="false" />
	           		    </td>
	           		    <td align="center" class="RecordRow"  nowrap>
	           		    	<bean:write name="element" property="pos" filter="false" />
	           		    </td>
	           		    <td align="center" class="RecordRow"  nowrap>
	           		    	<bean:write name="element" property="um" filter="false" />
						</td>
						<td align="center" class="RecordRow"  nowrap>
	           		    	<bean:write name="element" property="un" filter="false" />
	           		    </td>
	           		    <td align="center" class="RecordRow"  nowrap>	
	           		    	<bean:write name="element" property="z0103" filter="false" />
	           		    </td>
           		    
           		  
						
			</tr>
		
		</hrms:extenditerate>	
	</table>	
	
	<table width="90%" align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="hmuster.label.d"/>
				<bean:write name="hirePositionForm" property="positionListform.pagination.current" filter="true" />
				<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
				<bean:write name="hirePositionForm" property="positionListform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
				<bean:write name="hirePositionForm" property="positionListform.pagination.pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="hirePositionForm" property="positionListform.pagination" nameId="positionListform">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>		
		
		

<table  width="70%" align="center">
          <tr>
            <td align="center"> 
            
              <input type="button" name="save" value="<bean:message key="kq.kq_rest.submit"/>" class="mybutton" onClick="saves()">
         	
         	  <input type="button" name="goBack" value="<bean:message key="kq.search_feast.back"/>" onclick="goback()" class="mybutton">
             
            </td>
          </tr>          
</table>
		
</html:form>