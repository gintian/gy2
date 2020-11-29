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
<script language="javascript">
	function saveOk(){
	  var back = document.getElementById("back").value;
	  browseHistoryForm.action="/kq/register/history/statfx/statfxdata.do?b_search=link&code="+back;
	  browseHistoryForm.submit();
	}
	
	function hr(link){
		//alert(link);
		document.location.href=link;
	}
</script>
<html:form action="/kq/register/history/statfx/statfxname">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr>
			<td align= "left" nowrap>
				人数共&nbsp; <bean:write  name="browseHistoryForm" property="sanshu"/> 人
			</td>
			<td>
				<html:hidden name="browseHistoryForm" property="backy" styleId="back"/> 
			</td>
		</tr>
		<tr>
		<td>
		 <%
		 int i=0;
		 String name=null;
 		 %>
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
			<thead>
         		<tr>
         			<logic:iterate id="element"    name="browseHistoryForm"  property="kqnamelsit" indexId="index">
                		<td align="center" class="TableRow" nowrap>
                		   <logic:notEqual name="element" property="itemid" value="b0110">
                		     <logic:notEqual name="element" property="itemid" value="e0122">
                		     	<logic:notEqual name="element" property="itemid" value="a0101">
                 			     <bean:write  name="element" property="itemdesc"/>次数
                 			    </logic:notEqual>
                 			 </logic:notEqual>
                 		   </logic:notEqual>
                 		   <logic:equal name="element" property="itemid" value="b0110">
                		     	<bean:write  name="element" property="itemdesc"/>
                 		   </logic:equal>
                 		   <logic:equal name="element" property="itemid" value="e0122">
                 		   		<bean:write  name="element" property="itemdesc"/>
                 		   </logic:equal>
                 		   <logic:equal name="element" property="itemid" value="a0101">
                 			     <bean:write  name="element" property="itemdesc"/>
                 		   </logic:equal>  
                		</td>
         			</logic:iterate>
         		</tr>
         	</thead>
       		<%
       			BrowseHistoryForm rr = (BrowseHistoryForm)session.getAttribute("browseHistoryForm");
       			//out.println("1-- = "+rr.getSqlstrs());
       			//out.println("2-- = "+rr.getStrwheres());
       			//out.println("3-- = "+rr.getOrderbys());
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
          		<%}i++; 
          		%>
          		<logic:iterate id="info" name="browseHistoryForm"  property="kqnamelsit">
          			<!--字符型-->
          			<% 
          				//out.println("wo de 123");
          				BrowseHistoryForm browseHistoryForm=(BrowseHistoryForm)session.getAttribute("browseHistoryForm");
               			FieldItem item=(FieldItem)pageContext.getAttribute("info");
               			name=item.getItemid(); 
          			%>
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap> 
                            <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseHistoryForm.uplevel}"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                          </td>  
                       </logic:notEqual>
                       	<logic:equal name="info" property="itemid" value="a0101">
                       		 <td align="left" class="RecordRow" nowrap>  
                       		 &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                       		 </td>
                      	</logic:equal>
                   </logic:equal>
                   <!-- 数字 -->
                   <logic:equal name="info" property="itemtype" value="N">
                   		<td align="left" class="RecordRow" nowrap>
                   		<a href="###" onclick="hr('/kq/register/history/statfx/statfxuserinfo.do?b_userinfo=link&b0110=<bean:write name="element" property="b0110" filter="true"/>&itemid=<bean:write  name="info" property="itemid"/>&a0100=<bean:write  name="element" property="a0100" filter="true"/>&scope=<bean:write  name="element" property="scope" filter="true"/>&usernbase=<bean:write  name="element" property="nbase" filter="true"/>')">
                   		   &nbsp;&nbsp;<hrms:kqanalysis  value='<%=abean.get(name)+""%>'/>
                   		</a> 
                   			   
                   		</td>
                   </logic:equal>
                 
          		</logic:iterate>
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
								onclick="saveOk()">

						</td>
					</tr>
	</table>
        <td>
        </td>
        </tr>
	</table>
	
	
</html:form>