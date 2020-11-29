<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="java.util.Date"%>
<%@page import="com.hjsj.hrms.businessobject.kq.machine.KqParam"%>
<%@page import="com.hjsj.hrms.utils.OperateDate"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<%
  int i=0;
%>

<SCRIPT language=JavaScript>
 
   function change(id)
   {
      var year=id.value;
      myAnnualForm.action="/kq/kqself/annual/my_n_annual.do?b_query=link&table=${myAnnualForm.table}&year="+year;
      myAnnualForm.submit();
   } 
   function change1()
   {
      var year=document.getElementById("smid").value;
      myAnnualForm.action="/kq/kqself/annual/my_n_annual.do?b_query=link&table=${myAnnualForm.table}&year="+year;
      myAnnualForm.submit();
   }

 </SCRIPT>
 <%
 	Date current = new Date();
 	String overTimeValidate = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();
 	if(overTimeValidate == null  || "".equals(overTimeValidate))
 		overTimeValidate = "0";
 	int validate = Integer.parseInt(overTimeValidate);
 	Date date = OperateDate.addDay(current,0 - validate);
 	String from = OperateDate.dateToStr(date, "yyyy.MM.dd");
 	String to = OperateDate.dateToStr(current, "yyyy.MM.dd");
 %>
<html:form action="/kq/kqself/annual/my_n_annual">
<table id="tab" width="100%" style="padding-left: 8px;" border="0" cellspacing="0"  align="center" cellpadding="0">
  <thead>
	<tr height="25">  
		<td class="" colspan="${myAnnualForm.cols}">
             <logic:notEqual name="myAnnualForm" property="table" value="Q33">
		  		 <bean:message key="kq.deration_details.kqnd"/>        
	             <hrms:optioncollection name="myAnnualForm" property="slist" collection="list" />
		         <html:select name="myAnnualForm" property="kq_year" size="1" styleId="smid" onchange="change(this);">
	                 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </html:select>&nbsp;&nbsp;&nbsp;&nbsp;
             
	             <hrms:optioncollection name="myAnnualForm" property="typelist" collection="list" />
		         <html:select name="myAnnualForm" property="type" size="1" onchange="change1();">
	                 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </html:select>&nbsp;&nbsp;&nbsp;&nbsp;
	             <hrms:kqcourse/>
             </logic:notEqual>
             
             <logic:equal value="Q33" name="myAnnualForm" property="table">
				&nbsp;<bean:message key="kq.kqself.feast.duration"/>&nbsp;${myAnnualForm.leaveActiveTime }     
             </logic:equal>
		</td>
	</tr>  
    <tr> 
    	<td>
    		<table  class="RecordRow" width="100%">
    			<tr>
			      <logic:iterate id="element" name="myAnnualForm"  property="tlist" indexId="index">
			         <logic:equal name="element" property="visible" value="true">
			            <td align="center" class="TableRow" style="border-right: none;border-bottom: none;" nowrap>
			                <bean:write name="element" property="itemdesc" />&nbsp;
			            </td>
			         </logic:equal>    
			      </logic:iterate>      
    			</tr>
    			<tr>
					<hrms:paginationdb id="element" name="myAnnualForm" sql_str="myAnnualForm.sql" table="" where_str="" columns="${myAnnualForm.com}" page_id="pagination"  indexes="indexes">
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
					             
					            <logic:iterate id="tlist" name="myAnnualForm"  property="tlist" indexId="index">
					             <logic:equal name="tlist" property="visible" value="true">
					                  <logic:notEqual name="tlist" property="itemtype" value="D">
					                        <logic:notEqual name="tlist" property="codesetid" value="0">
					                           <td align="center" class="RecordRow" style="border-right: none" nowrap>
					                           <hrms:codetoname codeid="${tlist.codesetid}" name="element" codevalue="${tlist.itemid}" codeitem="codeitem" scope="page"/>  	      
					                           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;             
					                        </logic:notEqual>
					                        
					                        <logic:notEqual name="tlist" property="itemid" value="q1519">
						                        <logic:equal name="tlist" property="codesetid" value="0">
						                          <logic:notEqual name="tlist" property="itemtype" value="N">
						                             <td align="left" class="RecordRow" style="border-right: none" nowrap>
						                          </logic:notEqual>
						                          <logic:equal name="tlist" property="itemtype" value="N">
                                                     <td align="right" class="RecordRow" style="border-right: none" nowrap>
                                                  </logic:equal>
						                            &nbsp;<bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;
						                        </logic:equal>                   
					                        </logic:notEqual>
					                        <logic:equal name="tlist" property="itemid" value="q1519">
					                        	<td align="center" class="RecordRow" style="border-right: none" nowrap>
					                        	<logic:empty name="element" property="${tlist.itemid}">
					                        		&nbsp;<bean:message key="kq.feast.qj"/>&nbsp;
					                        	</logic:empty>
					                        	<logic:notEmpty name="element" property="${tlist.itemid}">
					                        		&nbsp;<bean:message key="kq.feast.xj"/>&nbsp;
					                        	</logic:notEmpty>
					                        </logic:equal>
					                     </td>
					                    </logic:notEqual>
					                    <logic:equal name="tlist" property="itemtype" value="D">
					                       <td align="center" class="RecordRow" style="border-right: none" nowrap>
					                           &nbsp;<bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;
					                       </td>
					                    </logic:equal>    
					               </logic:equal>
					   
					              </logic:iterate>        
					         </tr>
					    </hrms:paginationdb>
    			</tr>
    		</table>
    	</td>   
    </tr> 
    <tr>
    	<td>
    	
<table id="tabb" width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="myAnnualForm" property="pagination" nameId="myAnnualForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
                
  </table>
    	</td>
    </tr> 
  </thead>
</table>    


</html:form>
