<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-160);
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes></hrms:themes>
<html:form action="/org/orgpre/deptable">
<%int i=0;%>
<bean:message key='kq.report.name'/>：${orgPreForm.dpname}
<div class="myfixedDiv common_border_color" style="border-top:0px;">
<table  width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable1" style="border: none;">
	<tr class="fixedHeaderTr">
	   <td align="center" class="TableRow" style="border-left:none;" nowrap>序号</td>
		<logic:iterate id="element" name="orgPreForm"  property="resultlist" indexId="index">
		  <logic:equal name="element" property="itemtype"  value="D">
			<td align="center" class="TableRow" style="border-right:none;" nowrap width="150">
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	        </td> 
	      </logic:equal>
	      <logic:notEqual name="element" property="itemtype"  value="D">
	         <td align="center" class="TableRow" style="border-right:none;"  nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	         </td> 
	      </logic:notEqual>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="orgPreForm" sql_str="orgPreForm.sql" 
	table="" where_str="orgPreForm.wherestr" columns="orgPreForm.columns"
	 order_by="orgPreForm.orderby" 
	 page_id="pagination" pagerows="${orgPreForm.pagerows}">
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
          <td class="RecordRow" style="border-left:0px;" nowrap align="right"><%=i %>&nbsp;&nbsp;</td>
    	<logic:iterate id="fielditem"  name="orgPreForm"  property="resultlist" indexId="index">
    		
    			<logic:equal name="fielditem" property="itemid" value="b0110">
    			<td align="left" class="RecordRow" style="border-right:none;"  nowrap>
    				&nbsp;${orgPreForm.dpname}
    			</td> 
    			</logic:equal>
    			<logic:notEqual name="fielditem" property="itemid" value="b0110">
               <logic:notEqual name="fielditem" property="codesetid" value="0">
               		<td align="left" class="RecordRow" style="border-right:none;"  nowrap>
	          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;    
          	   		</td>                 
               </logic:notEqual>
               <logic:equal name="fielditem" property="codesetid" value="0">
               	<logic:notEqual value="N" name="fielditem" property="itemtype">
              	 <td align="right" class="RecordRow" style="border-right:none;"  nowrap>
                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp; 
                 </td>   
                </logic:notEqual>  
                <logic:equal value="N" name="fielditem" property="itemtype">
              	 <td align="right" class="RecordRow" style="border-right: none;"  nowrap>
                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp; 
                 </td>   
                </logic:equal>              
               </logic:equal> 
               </logic:notEqual>                               
	      
    	</logic:iterate>
    </tr>
    </hrms:paginationdb>      
</table>
</div>
<table   width="100%" border="0" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="orgPreForm" pagerows="${orgPreForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td   align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="orgPreForm" property="pagination" nameId="orgPreForm" scope="page">
			</hrms:paginationdblink>
		</td>
	</tr>
</table>
		<div align="left" style="width: 100%;margin-top: 5px;"><input type="button" value="<bean:message key='reporttypelist.cancel'/>" onclick="goBack();" class="mybutton"></div>
</html:form>
<script type="text/javascript">
function goBack(){
	orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}";
   	orgPreForm.submit();
}
</script>