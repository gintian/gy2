<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.LeaderTagParamForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData" %>
<%

	if(request.getParameter("model")!=null)
	{
		session.setAttribute("model",request.getParameter("model"));
	}

  	LeaderTagParamForm leaderTagParamForm=(LeaderTagParamForm)session.getAttribute("leaderTagParamForm");	
  	ArrayList columnsList=leaderTagParamForm.getColumnsList();
	

%>
<html:form action="/selfservice/performance/leaderexamine/showinfodata">
<script language="javascript">
   function change()
   {
      leaderTagParamForm.action="/selfservice/performance/leaderexamine/showinfodata.do?b_search=link&code=${leaderTagParamForm.code}&kind=${leaderTagParamForm.kind}";
      leaderTagParamForm.submit();
   }
</script>
<br>
<%  if(columnsList!=null){ %>

<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <tr>
    <td align="left"  nowrap>
     	     <bean:message key="label.query.dbpre"/>
    	        <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                  sql="leaderTagParamForm.dbcond" collection="list" scope="page"/>
                <html:select name="leaderTagParamForm" property="userbase" size="1" onchange="change()">
                     <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>         
   </tr>
   </table>
<br>
<table width="70%"  border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
           <%
           	for(int i=0;i<columnsList.size();i++)
           	{
           		LazyDynaBean  abean=(LazyDynaBean)columnsList.get(i);
           		String  name=(String)abean.get("name");
           	%>
           	<td align="center" class="TableRow" nowrap>
           		<%=name%>
           	</td> 
           	<%
           	}
           
           %>		        	        	        
           </tr>
   	  </thead>
   	 
          <hrms:paginationdb id="element" name="leaderTagParamForm" sql_str="leaderTagParamForm.strsql" table="" where_str="leaderTagParamForm.cond_str"  order_by=" order by b0110,e0122,A0000"      columns="${leaderTagParamForm.columns}" page_id="pagination">
	   
	    <tr>
	    	<%
           	for(int i=0;i<columnsList.size();i++)
           	{
           		LazyDynaBean  abean=(LazyDynaBean)columnsList.get(i);
           		String  name=(String)abean.get("name");
           		String  id=(String)abean.get("id");
           		String  type=(String)abean.get("type");
           		String  codesetid=(String)abean.get("codesetid");
           		if(type.equalsIgnoreCase("A")&&!codesetid.equals("0"))
           		{
           		%>
           			<td align="left" class="RecordRow" nowrap>
		          	<hrms:codetoname codeid="<%=codesetid%>" name="element" codevalue="<%=id%>" codeitem="codeitem" scope="page"/>  	      
		          	<bean:write name="codeitem" property="codename" />&nbsp;
		          	 </td>     
	   			<%
           		}
           		else
           		{
           		%>
           			<td align="left" class="RecordRow" nowrap>
           				<%if(i==columnsList.size()-1){ %>
           				<a href="/selfservice/performance/statistic.do?b_search=link&planFlag=1&objectId=<bean:write name="element" property="a0100" filter="true"/>&model=<%=((String)session.getAttribute("model"))%>&companyId=<bean:write name="element" property="b0110" filter="true"/>">
           				<% } %>
		          	    <bean:write name="element" property="<%=id%>" filter="true"/>&nbsp;
		          		<%if(i==columnsList.size()-1){ %>
           				</a>
           				<% } %>
		          	</td>   
           		<%
           		}
           	}
           	%>
	    
	    
	    
	        	    	    	    		        	        	        
          </tr>
         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="leaderTagParamForm" property="pagination" nameId="leaderTagParamForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

<%  }  %>

</html:form>
