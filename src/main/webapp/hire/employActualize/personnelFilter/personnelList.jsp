<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.employActualize.EmployActualizeForm,org.apache.commons.beanutils.LazyDynaBean" %>
<html>
<head>
<title></title>
</head>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script language='javascript'>

	function selects()
	{
		var num=0;
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
				num++;
		}
		if(num==0)
		{
			alert(SELECT_PERSON+"!");
			return;
		}
		
		if(employActualizeForm.z0301.value==0)
		{
			alert(SELECT_EMPLOY_POSITION+"!");
			return;
		}
		employActualizeForm.action="/hire/employActualize/personnelFilter/personnelList.do?b_selectPosition=select";
		employActualizeForm.submit();
	}
	
</script>
<body>
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/personnelFilter/personnelList">
	<br>
	
	<table id='ta' width="85%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	  	  
           <tr>      	
           <td align="center" class="TableRow" nowrap>
           &nbsp;
           </td>
         	<logic:iterate id="element" name="employActualizeForm" property="tableColumnsList"  offset="0"> 
	      		<td align="center" class="TableRow" nowrap>
	      		&nbsp;&nbsp;<bean:write name="element" property="itemdesc"   filter="false"/>&nbsp;&nbsp;
	      		</td>
            </logic:iterate>               
         </tr>
   	  </thead>

   	     	   <% int i=0; String className="trShallow"; %>
   	   <hrms:paginationdb id="element" name="employActualizeForm" sql_str="${employActualizeForm.select_str}" table="" where_str="${employActualizeForm.from_str}"  columns="${employActualizeForm.columns}"  page_id="pagination" pagerows="14" indexes="indexes">
			  <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
            <td width='40' align="center"  class="RecordRow" nowrap>
             	 	<hrms:checkmultibox name="employActualizeForm" property="pagination.select" value="true" indexes="indexes"/>
            </td>
      	   
      	   
      
                <%
           		   	 
           		   	 EmployActualizeForm employActualizeForm=(EmployActualizeForm)session.getAttribute("employActualizeForm");
           		   	 ArrayList tableColumnsList=employActualizeForm.getTableColumnsList();
           		   	 String dbName=employActualizeForm.getDbName();
           		   	 for(int a=0;a<tableColumnsList.size();a++)
           		   	 {
           		   	 	LazyDynaBean aBean=(LazyDynaBean)tableColumnsList.get(a);
           		   	 	String itemid=(String)aBean.get("itemid");
           		   	 	String codesetid=(String)aBean.get("codesetid");
           		   	 	if(codesetid.equals("0"))
           		   	 	{
           		    %>
	           		    <td align="center" class="RecordRow" nowrap>
	           		    	<%
	           		    		if(a==0){ 
	           		    	%>
	           		    		<script language='javascript'>
	           		    			var a0100='<bean:write  name="element" property="a0100" filter="true"/>'
	           		    			document.write("<a href='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_desc=desc&dbName=<%=dbName%>&id="+a0100+"' >");
	           		    		</script>
	           		    	<% } %>
	           		    	<bean:write  name="element" property="<%=itemid%>" filter="true"/>
	           		    	<%
	           		    		if(a==0) out.print("</a>");
	           		    	%>
	           		    </td>
           		    
           		    <%
           		    	}
           		    	else
           		    	{
           		    %>
           		    	 <td align="center" class="RecordRow" nowrap>
				          	<hrms:codetoname codeid="<%=codesetid%>" name="element" codevalue="<%=itemid%>" codeitem="codeitem" scope="page"/>  	      
				          	<bean:write name="codeitem" property="codename" />&nbsp;
					        </td> 
           		    	
           		    <%
           		    	}
           		      }
           		    %> 	
           		  	        	        
          </tr>
        </hrms:paginationdb>
   	  
 	</table>  	
 	
 	
<table  width="80%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.item"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="employActualizeForm" property="pagination" nameId="employActualizeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   	  
</table> 
<table  width="85%" align="center">
<tr><td  align='left' >
<bean:message key="hire.employActualize.interviewPosition"/>：</td></tr>
<tr><td align='left'><hrms:optioncollection name="employActualizeForm" property="positionList" collection="list" />
					             <html:select name="employActualizeForm" property="z0301" size="1"      >
					             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
					             </html:select>
					             
<input type='button' class='myButton' value='<bean:message key="kq.formula.true"/>' onclick='selects()' />
</td>
</tr></table>                            


</html:form>
</body>
</html>