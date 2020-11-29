<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/kq/kq.js"></script>
<%
	int i = 0;
%>
<script type="text/javascript">
	function change(){
		feastForm.action = "/kq/feast_manage/managerdata_overForLeave.do?b_search2=link";
		feastForm.submit();
	}
	function selectquery(){
		feastForm.action = "/kq/feast_manage/managerdata_overForLeave.do?b_search2=link&select_sturt=1";
		feastForm.submit();
	}
	
</script>
<html:form action="/kq/feast_manage/managerdata_overForLeave">
	<table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" >
		<tr>
			<td>
				<table>
					<tr>
						
						<td>
							<html:select name="feastForm" property="select_pre" styleId="select_pre" size="1" onchange="change();">
			                	<html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
			                </html:select>
						</td>
						<td align= "right" nowrap>
              				姓名   
              			</td>             
		                <td align= "left" nowrap>
		                    <input type="text" name="select_name" value="${feastForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
		                    &nbsp;<button extra="button" onclick="javascript:selectquery();">查询</button> 
		             	</td>  
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td width="100%" >
            	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	     			<thead>
              		<tr>   
               			<logic:iterate id="element"    name="feastForm"  property="fieldlist" indexId="index"> 
	                 		<logic:equal name="element" property="visible" value="true">
			                    <td align="center" class="TableRow" nowrap>
			                    	<bean:write  name="element" property="itemdesc"/>&nbsp; 
			                    </td>
	                 		</logic:equal>
               			</logic:iterate>
   	     			</thead>
   	     		<hrms:paginationdb id="element" name="feastForm" sql_str="feastForm.strsql" table="" where_str="" fromdict="1"
   	     		    columns="feastForm.columns" order_by="order by i,b0110,e0122,a0100" 
   	     		    page_id="pagination" pagerows="20" indexes="indexes">
				<%
				  if(i%2==0){ 
				%>
				<tr class="trShallow">
				<%
				  }else{
				%>
				<tr class="trDeep">
				<%
				  }
				%>  
	           <logic:iterate id="info"  name="feastForm"  property="fieldlist" indexId="index">
	           <%
            	FieldItem item=(FieldItem)pageContext.getAttribute("info");
               %> 
                  <logic:equal name="info" property="visible" value="true">
		                    <logic:equal name="info" property="codesetid" value="0">
		                    	<logic:equal name="info" property="itemtype" value="N">
		                    		<td align="left" class="RecordRow" nowrap>
		                            	&nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
		                        	</td>
		                    	</logic:equal>
		                    	<logic:equal name="info" property="itemtype" value="A">
			                    	<td align="left" class="RecordRow" nowrap>
			                            &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
			                        </td>
		                    	</logic:equal>
		                    </logic:equal>
		                  	
		                    <logic:notEqual name="info" property="codesetid" value="0">
		                    	<td align="left" class="RecordRow" nowrap>
		                     		<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
		                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
		                        </td>
		                    </logic:notEqual>
                  </logic:equal>
              </logic:iterate>           
		     <%i++;%>  
		     </tr>	     
             </hrms:paginationdb>
             </table>
             <table  width="100%" class="RecordRowTop0" align="left">
       			<tr>        
					<td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
		    			<bean:write name="pagination" property="current" filter="true" />
						<bean:message key="label.page.sum"/>
						<bean:write name="pagination" property="count" filter="true" />
						<bean:message key="label.page.row"/>
						<bean:write name="pagination" property="pages" filter="true" />
						<bean:message key="label.page.page"/>
					</td>
	        		<td  align="right" nowrap class="tdFontcolor">
		     			<p align="right"><hrms:paginationdblink name="feastForm" property="pagination" nameId="feastForm" scope="page">
						</hrms:paginationdblink>
					</td>
	      		</tr>
          	</table> 
   	     	</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
hide_nbase_select('select_pre');
</script>