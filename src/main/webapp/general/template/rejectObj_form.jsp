<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.struts.valueobject.UserView,
				com.hrms.struts.taglib.CommonData,
				java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>

<%
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	ArrayList rejectObjList=templateForm.getRejectObjList();
%>

<html>
  <head>
    <script language='javascript' >
    	function enter()
    	{
    		var rejectObj="";
    		var objs=document.getElementsByName("a0100");
    		for(var i=0;i<objs.length;i++)
    		{
    			if(objs[i].checked)
					rejectObj+=objs[i].value+"/";    				
    		}
    		if(rejectObj.length==0)
    		{
    			alert("请选择需驳回的对象!");
    			return ;
    		}
    		 returnValue=rejectObj;
	  		 window.close();	
    	}
    
    
    </script>

  </head>
  
  <body>
  <html:form action="/general/template/apply_form">
  <table width='100%' ><Tr><Td>&nbsp;&nbsp;&nbsp;</Td><Td>
  <br>

	<fieldset align="center" style="width:90%;">
    							 <legend >驳回对象</legend>
			
			<table width='200' ><tr><td align='center'>
			
  				<table border="0" cellpmoding="0" cellspacing="0" class="ListTable3" cellpadding="0" width="180">
  					<thead>
				       <tr>
				         <td align="center" class="TableRow" nowrap>
						    <input type="checkbox" name="selbox" onclick="batch_select(this,'a0100');" title='<bean:message key="label.query.selectall"/>'>
				         </td>         
				         <td align="center" class="TableRow" nowrap >
						   <bean:message key="hire.employActualize.name"/>
					     </td>   
  					   </tr>
  					<% 
  					
  					for(int i=0;i<rejectObjList.size();i++)
  					{
  						LazyDynaBean abean=(LazyDynaBean)rejectObjList.get(i);
			          if(i%2==0)
			          {
			          %>
			          <tr class="trShallow">
			          <%}
			          else
			          {%>
			          <tr class="trDeep">
			          <%
			          }
			               
			          %>      
			          	<td  align="center"  class="RecordRow" nowrap><input type='checkbox' name='a0100' value='<%=((String)abean.get("a0100"))%>@<%=((String)abean.get("dbname"))%>' /></td>
			          	<td align="left" class="RecordRow" nowrap>&nbsp;<%=((String)abean.get("a0101"))%></td>
			          	
          		      </tr>	
          		   <% } %>
				</table>
				<br>
				</td></tr>
			</table>
				
		</fieldset>		
  		</td></tr>
  		
  		<tr><td   ></td><td align='left' >
  		<br>
  				<button extra="button" onclick="enter();">
            		<bean:message key="button.ok"/>
	 	        </button>
				<button extra="button" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	        </button>	
		</td></tr>
  		
  		</table>
  
  
  </html:form>
  </body>
</html>
