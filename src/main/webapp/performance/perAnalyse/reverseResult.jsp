<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,				 
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<html>
  <head>
     <hrms:themes />
  </head>
  <script type="text/javascript" src="/js/constant.js"></script>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes />
  <html:form action="/performance/perAnalyse">
  <table width="100%" cellspacing="0" ><tr><td>
   	<bean:write name="perAnalyseForm" property="statTitle" filter="false"/>&nbsp;</td></tr>
	<tr><td  align="left" nowrap>
	<div class="myfixedDiv complex_border_color">
   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
   	  <thead>
   	  <%
			 	FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  			 	
	  %>
           <tr>
            <td align="center"  class="TableRow_right" nowrap style="border-top:0px;">
            	<bean:message key="conlumn.mediainfo.info_id"/>
             </td>
            <td align="center"  class="TableRow" nowrap style="border-top:0px;">
        		&nbsp;<bean:message key="b0110.label"/>
            </td>       
   		 <logic:equal name="perAnalyseForm" property="object_type"  value="1">
   			<td align="center"  class="TableRow" nowrap style="border-top:0px;">
        		&nbsp;<bean:message key="org.performance.unorum"/>
            </td> 
   		 </logic:equal>
   		 <logic:equal name="perAnalyseForm" property="object_type"  value="2">
   			<td align="center"   class="TableRow" nowrap style="border-top:0px;">
        		&nbsp;<%=fielditem.getItemdesc()%>
            </td> 
            <td align="center"   class="TableRow" nowrap style="border-top:0px;">
        		&nbsp;<bean:message key="e01a1.label"/>
            </td> 
            <td align="center"  class="TableRow" nowrap style="border-top:0px;">
        		&nbsp;<bean:message key="kq.card.emp.name"/>
            </td> 
         </logic:equal>
         <logic:equal name="perAnalyseForm" property="object_type"  value="3">
   			
         </logic:equal>
         <logic:equal name="perAnalyseForm" property="object_type"  value="4">
   			<td align="center"  class="TableRow" nowrap style="border-top:0px;">
        		&nbsp;<%=fielditem.getItemdesc()%>
            </td> 
         </logic:equal>        
            <td align="center"  class="TableRow_left" nowrap style="border-top:0px;">
        		&nbsp;<bean:message key="jx.param.mark"/>
            </td> 
            </tr>
        </thead>
   
   	<% int i=1; %>
     <logic:iterate id="element"  name="perAnalyseForm" property="reverseDataList" >
   		<%
          if(i%2==1)
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
            <td align="center" class="RecordRow_right" nowrap><%=i%></td>
            <td align="left" class="RecordRow" nowrap><bean:write name="element" property="b0110" filter="false"/>&nbsp;</td>
            
         <logic:equal name="perAnalyseForm" property="object_type"  value="1">
   			<td align="left" class="RecordRow" nowrap>
        		&nbsp;<bean:write name="element" property="a0101" filter="false"/>&nbsp;
            </td> 
   		 </logic:equal>
   		 <logic:equal name="perAnalyseForm" property="object_type"  value="2">
   			<td align="left" class="RecordRow" nowrap>
        		&nbsp;<bean:write name="element" property="e0122" filter="false"/>&nbsp;
            </td> 
            <td align="left" class="RecordRow" nowrap>
        		&nbsp;<bean:write name="element" property="e01a1" filter="false"/>&nbsp;
            </td> 
            <td align="left" class="RecordRow" nowrap>
        		&nbsp;<bean:write name="element" property="a0101" filter="false"/>&nbsp;
            </td> 
         </logic:equal>
         <logic:equal name="perAnalyseForm" property="object_type"  value="3">

         </logic:equal>
         <logic:equal name="perAnalyseForm" property="object_type"  value="4">
   			<td align="left" class="RecordRow" nowrap>
        		&nbsp;<bean:write name="element" property="e0122" filter="false"/>&nbsp;
            </td> 
         </logic:equal>
            <td align="right" class="RecordRow_left" nowrap>
        		<bean:write name="element" property="score" filter="false"/>&nbsp;
            </td> 
         </tr>
         
         <% i++; %>
   	  </logic:iterate>
    </table> 
    </div>
   <table width="100%" align="center" class="RecordRowP" cellspacing="0">
		<tr>
			<td valign="bottom" align="left" class="tdFontcolor">
				第
				<bean:write name="perAnalyseForm"
					property="setlistform.pagination.current" filter="true" />
				页 共
				<bean:write name="perAnalyseForm"
					property="setlistform.pagination.count" filter="true" />
				条 共
				<bean:write name="perAnalyseForm"
					property="setlistform.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="perAnalyseForm"
						property="setlistform.pagination" nameId="setlistform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
				</td>
		</tr>
	</table>
	  <table width="100%" ><tr><td>
      <input type="button" name="addbutton"  value="<bean:message key="kq.search_feast.back"/>" class="mybutton" onclick="javascript:history.go(-1)"> 
   </td>
   </tr>
   </table>
   
   
   
   </html:form>
</html>
