<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<%
	int i =0;
%>
<STYLE type=text/css>
.div
{
 overflow:auto; 
 width: 700px;height: 440px;
 line-height:15px; 
 border-color:#C4D8EE;

}
</STYLE>
<html:form action="/kq/kqself/addkqAppSelf">
<div  class="fixedDiv2" style="height: 100%;border: none">
	<table border="0" cellspacing="0"  align="center" cellpadding="0" width="100%" >
		<tr>
			<td width="100%">
				<table border="0" cellspacing="0" width="100" align="center" cellpadding="0" width="90%" >
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							我的调休(<bean:write name="kqselfForm" property="start_time"/>-
								    <bean:write name="kqselfForm" property="end_time"/>):
						    可调休总时长<bean:write name="kqselfForm" property="usableTime"/>小时,
						    已调休总时长<bean:write name="kqselfForm" property="haveUsedTime"/>小时,
						    调休加班总时长<bean:write name="kqselfForm" property="allOverTime"/>小时
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td width="100%">
							<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
								<thead>
				            		<tr >
				            			<td height="20" class="TableRow" nowrap align="center">
									 		加班日期
				            			</td>
				            			<td height="20" class="TableRow" nowrap align="center">
									 		加班时长(小时)
				            			</td> 
				            			<td height="20" class="TableRow" nowrap align="center">
									 		已调休时长(小时)
				            			</td> 
				            			<td height="20" class="TableRow" nowrap align="center">
									 		可调休时长(小时)
				            			</td> 
				            			            	        	        	        
				           			</tr>
				   	  			</thead>
						          <logic:iterate id="element" name="kqselfForm" property="vo_list" indexId="index">
						          <%
						               if(i%2==0){ 
						             %>
						             <tr class="trShallow">
						             <%
						               }else{
						             %>
						             <tr class="trDeep">
									<%} %>
				 					   <td class="RecordRow" nowrap align="left">              
						                   &nbsp;<bean:write  name="element" property="string(q3303)" filter="true"/>&nbsp;
						               </td>
						               <td class="RecordRow" nowrap align="right">              
						                   &nbsp;<bean:write  name="element" property="String(q3305)" filter="true"/>&nbsp;
						               </td> 
						               <td class="RecordRow" nowrap align="right">              
						                   &nbsp;<bean:write  name="element" property="String(q3307)" filter="true"/>&nbsp;
						               </td>  
						               <td class="RecordRow" nowrap align="right">              
						                   &nbsp;<bean:write  name="element" property="String(q3309)" filter="true"/>&nbsp;
						               </td>   
							      </tr>
							      </logic:iterate>	     
							</table>
				   		</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							调休假记录(<bean:write name="kqselfForm" property="start_time"/>-
								      <bean:write name="kqselfForm" property="end_time"/>)
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
								<thead>
				            		<tr >
				            			<td height="20" class="TableRow" nowrap align="center">
									 		起始时间
				            			</td>
				            			<td height="20" class="TableRow" nowrap align="center">
									 		结束时间
				            			</td> 
				            			<td height="20" class="TableRow" nowrap align="center">
									 		事由
				            			</td> 
				            			<td height="20" class="TableRow" nowrap align="center">
									 		审批状态
				            			</td> 
				            			<td height="20" class="TableRow" nowrap align="center">
									 		审批时间
				            			</td>
				            			<td height="20" class="TableRow" nowrap align="center">
				            				请/销假
				            			</td>         	        	        	        
				           			</tr>
				   	  			</thead>
						          <logic:iterate id="element" name="kqselfForm" property="vo_list2" indexId="index">
							     <%
						               if(i%2==0){ 
						             %>
						             <tr class="trShallow">
						             <%
						               }else{
						             %>
						             <tr class="trDeep">
									<%} %>
				 					   <td align="left" class="RecordRow" nowrap>              
						                   &nbsp;<bean:write  name="element" property="string(q15z1)" filter="true"/>&nbsp;
						               </td>
						               <td align="left" class="RecordRow" nowrap>              
						                   &nbsp;<bean:write  name="element" property="string(q15z3)" filter="true"/>&nbsp;
						               </td>
						               <td align="left" class="RecordRow" nowrap>              
						                   &nbsp;<bean:write  name="element" property="string(q1507)" filter="true"/>&nbsp;
						               </td>
						               <td align="center" class="RecordRow" nowrap>
						                  <hrms:codetoname codeid="23" name="element" codevalue="string(q15z5)" codeitem="codeitem" scope="page"/> 
						                   &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;   
				               		   </td>
						               <td align="left" class="RecordRow" nowrap>              
						                   &nbsp;<bean:write  name="element" property="String(q15z7)" filter="true"/>&nbsp;
						               </td>  
						               <td align="center" class="RecordRow" nowrap>              
						                   <logic:equal name="element" property="String(q1519)" value="1">
						                   		&nbsp;<bean:message key="kq.feast.qj"/>&nbsp;
						                   </logic:equal>
						                   <logic:notEqual name="element" property="String(q1519)" value="1">
						                   		&nbsp;<bean:message key="kq.feast.xj"/>&nbsp;
						                   </logic:notEqual>
						               </td>
						             <%i++;%>  
							     </tr>	     
						          </logic:iterate>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="90%" style="margin-top: 10px;">
	<tr>
		<td align="center">    
			<input type="button" value="关闭" class="mybutton" onclick="window.close();"/>
		</td>
	</tr>
</table>
</html:form>