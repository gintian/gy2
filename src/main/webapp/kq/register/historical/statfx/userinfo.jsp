<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.BrowseHistoryForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.HashMap" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script type="text/javascript">
<!--
	function back(){
		//alert($F('b01101')+" a "+$F('itemid')+" b  "+$F('codetj'));
		browseHistoryForm.action="/kq/register/history/statfx/statfxname.do?b_seename=link&b01101="+$F('b01101')+"&itemid="+$F('itemid')+"&codetj="+$F('codetj');
		browseHistoryForm.submit();
	}
//-->
</script>
<html:form action="/kq/register/history/statfx/statfxuserinfo">
	<html:hidden name="browseHistoryForm" property="b01101"/>
	<html:hidden name="browseHistoryForm" property="itemid"/>
	<html:hidden name="browseHistoryForm" property="codetj"/>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr>
			<td>
				<%
		 		int i=0;
		 		String name=null;
 		 		%>
 		 		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
 		 			<thead>
 		 				<tr>
 		 					<logic:iterate id="element"    name="browseHistoryForm"  property="searchfieldlist" indexId="index">
								<td align="center" class="TableRow" nowrap>
               					 &nbsp;<bean:write name="element" property="itemdesc" filter="true"/>&nbsp;
            					</td> 		 						
 		 					</logic:iterate>
 		 				</tr>
 		 			</thead>
 		 			<%
       					BrowseHistoryForm rr = (BrowseHistoryForm)session.getAttribute("browseHistoryForm");
       					String itemid = rr.getItemid();
       		 		%>
       		 		<hrms:paginationdb id="element" name="browseHistoryForm" sql_str="browseHistoryForm.sqlstrs" 
         			table="" where_str="browseHistoryForm.strwheres" columns="browseHistoryForm.columnss"
         	 		order_by="browseHistoryForm.orderbys" pagerows="${browseHistoryForm.pagerows}" page_id="pagination">
         	 		<%
         	 		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
 		         	if(i%2==0){
 		         	//out.println("我的0=-"); 
        		  %>
         	 		<tr class="trShallow">
          				<%
          				}else{
          				%>
          			<tr class="trDeep">
          				<%
          				}i++; 
          				%>
          			<logic:equal name="browseHistoryForm" property="tableValue" value="2">
          			<logic:iterate id="info" name="browseHistoryForm"  property="searchfieldlist">
          				<logic:notEqual name="info" property="itemtype" value="D">
          				<td align="left" class="RecordRow" nowrap >
          				<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
                         <logic:equal name="info" property="itemid" value="a0101">
                        	&nbsp;<bean:write name="element" property="${info.itemid}" filter="false"/>&nbsp;
                        </logic:equal>
                        </td>
                        </logic:notEqual>
                    	<logic:equal name="info" property="itemtype" value="D">
                       		<td align="left" class="RecordRow" nowrap>
                           		&nbsp;<bean:write name="element" property="${info.itemid}" filter="false"/>&nbsp;   
                       		</td>
                    	</logic:equal>    
          			</logic:iterate>
          			</logic:equal>
          			<logic:equal name="browseHistoryForm" property="tableValue" value="1">
          				<logic:iterate id="info" name="browseHistoryForm"  property="searchfieldlist">
          				<% 
          				//out.println("wo de 123");
          				BrowseHistoryForm browseHistoryForm=(BrowseHistoryForm)session.getAttribute("browseHistoryForm");
               			FieldItem item=(FieldItem)pageContext.getAttribute("info");
               			name=item.getItemid(); 
          				%>
          					<logic:equal name="info" property="itemtype" value="A">
          						<td align="left" class="RecordRow" nowrap> 
          						<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseHistoryForm.uplevel}"/>  	      
                             	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
                             	<logic:equal name="info" property="itemid" value="a0101">
                       		 		&nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                      			</logic:equal>
                      			</td> 
          					</logic:equal>
          					<!-- 数字 -->
                   			<logic:equal name="info" property="itemtype" value="N">
                   				<td align="left" class="RecordRow" nowrap>
                   				
                   				&nbsp;<bean:write name="element" property="q03z0" filter="true"/>&nbsp;&nbsp;(<hrms:kqanalysis2 v2="<%=itemid %>" v1='<%=abean.get(name)+""%>' />)
                   				</td>
                   			</logic:equal>
          				</logic:iterate>
          			</logic:equal>
          			</tr>
         	 		</hrms:paginationdb>	
 		 		</table>
			</td>
		</tr>
		<tr>
   		  <td>
     	   <table   width="100%" border="0" class="RecordRowP">
			<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="browseHistoryForm" pagerows="${browseHistoryForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td   align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="browseHistoryForm" property="pagination" nameId="browseHistoryForm" scope="page">
			</hrms:paginationdblink>
		</td>
		</tr>
		</table>
         </td>
 	   </tr>
 	   <tr>
        	<table width="100%" align="center">
			<tr>
						<td align="center" colspan="4">
							<input type="button" name="br_approve"
								value='<bean:message key="button.return"/>' class="mybutton"
								onclick="back();">

						</td>
					</tr>
	</table>
        <td>
        </td>
        </tr>
	</table>
	
</html:form>