<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="/train/traincourse/traindata.js"></script>

<html:form action="/train/b_plan/train.do?b_query=link&r2501=${param.r2501}&model=${param.model}&b0110=${param.b0110}&e0122=${param.e0122}&spflag=${param.spflag}">
<%int i=0; int j = 1;%>
<table border="0" cellspacing="0"  align="left" cellpadding="0">
<tr><td>

</td></tr>
<tr><td>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable1">
	<thead>
	<tr>
    		<td width="30" align="center" class="TableRow" nowrap>
    			序号                             
	      	</td> 
			<td align="center"class="TableRow" nowrap>
                 培训班名称
	       </td>
	       
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="courseTrainForm" sql_str="courseTrainForm.sql" table="" 
	where_str="courseTrainForm.wherestr" columns="courseTrainForm.columns" 
	order_by="order by r3101" page_id="pagination" pagerows="${courseTrainForm.pagerows}">
    <%
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
          i++;          
          %>  
    	<logic:iterate id="fielditem"  name="courseTrainForm"  property="itemlist" indexId="index">
    		<td align="center" class="RecordRow" nowrap>
    			<%=j %>                         
	      	</td> 
			<td align="left" class="RecordRow" nowrap>
		        &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;                 
			</td> 
    	</logic:iterate>
    </tr>
    <%j++; %>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0" class="RecordRowP" cellpadding="0" cellspacing="0">
	<tr>
		<td width="300" valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="courseTrainForm" pagerows="${courseTrainForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td width="300" align="right" nowrap class="tdFontcolor">
		     <hrms:paginationdblink name="courseTrainForm" property="pagination" nameId="courseTrainForm" scope="page">
			</hrms:paginationdblink>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>
</td></tr>
</table>		
</html:form>
