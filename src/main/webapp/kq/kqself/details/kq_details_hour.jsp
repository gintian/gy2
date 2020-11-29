<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.details.KqDetailsForm"%> 
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>

<script language="JavaScript">
 function change()
   {
      kqDetailsForm.action="/kq/kqself/details/kq_details_hour.do?b_hour=link";
      kqDetailsForm.submit();
   }
 function changeys(dd)
 {
	if(dd==2){
 		kqDetailsForm.action="/kq/kqself/details/kq_details_hour.do?b_hour=link&dtable=Q03";
    	kqDetailsForm.submit();
 	}else if(dd==1){
 		kqDetailsForm.action="/kq/kqself/details/kq_details.do?b_query=link&dtable=Q03";
    	kqDetailsForm.submit();
 	}
 }
 function affirm(date){
	 kqDetailsForm.action="/kq/kqself/details/kq_details_hour.do?b_affirm=link&date=" + date;
	 kqDetailsForm.submit();
 }
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css --> 

<%
int i=0;
String name=null;
int num_s=0;
%>
<html:form action="/kq/kqself/details/kq_details_hour">
	<table  width="100%" align="center">
		 <tr >
          <td align="left" nowrap >        
            <bean:message key="kq.deration_details.kqnd"/>        
            <hrms:optioncollection name="kqDetailsForm" property="slist" collection="list" />
	          <html:select name="kqDetailsForm" property="kq_year" size="1" onchange="change();">
               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
             </html:select> 
             时间显示方式:
      		<select size="1"   name="selectys"   onchange="changeys(this.value);">
      			<option   value="2">HH:mm</option> 
      		 	<option   value="1">默认</option>
      		</select>
           </td>
         </tr>
  </table>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
     <thead>
    <tr>
    <logic:equal value="1" name="kqDetailsForm" property="have_accepted">
       <td align="center" class="TableRow" nowrap>
          <bean:message key="kq.emp.collect.data.affirm"/>
       </td>  
    </logic:equal>
      <logic:iterate id="element" name="kqDetailsForm"  property="flist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
      <td align="center" class="TableRow" nowrap>
        <bean:message key="conlumn.infopick.detailinfo"/>            	
      </td>
    </tr>  
  </thead>   
<hrms:paginationdb id="element" name="kqDetailsForm" sql_str="kqDetailsForm.sql" table="" where_str="kqDetailsForm.where" columns="${kqDetailsForm.com}" page_id="pagination"  indexes="indexes">
          <%
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
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
          <% int  inNum=0;%> 
          <logic:equal value="1" name="kqDetailsForm" property="have_accepted">
          <td align="center" class="RecordRow" nowrap>
          		<logic:equal value="03" name="element" property="q03z5">
	          		<logic:equal property="accepted" name="element" value="1">
		          		<hrms:codetoname name="element" codevalue="accepted" codeid="45" codeitem="codeitem" scope="page"/>
		          		&nbsp;<bean:write name="codeitem" property="codename"/>
	          		</logic:equal>
	          		<logic:notEqual property="accepted" name="element" value="1">
	          			<input type="button" name="button" value='<bean:message key="button.affirm"/>' class="mybutton" onclick="affirm('<bean:write name="element" property="q03z0"/>')"/>
	          		</logic:notEqual>
          		</logic:equal>
          	</td>
          	</logic:equal>                    
            <logic:iterate id="flist" name="kqDetailsForm"  property="flist" indexId="index">
            <%
               KqDetailsForm kqDetailsForm=(KqDetailsForm)session.getAttribute("kqDetailsForm");
               FieldItem item=(FieldItem)pageContext.getAttribute("flist");
               name=item.getItemid(); 
              %>
             <logic:equal name="flist" property="visible" value="true">
                     <td align="left" class="RecordRow" nowrap>
                        <logic:notEqual name="flist" property="codesetid" value="0">
                           <hrms:codetoname codeid="${flist.codesetid}" name="element" codevalue="${flist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;                    
                        </logic:notEqual>
                        <logic:equal name="flist" property="codesetid" value="0">
                            <logic:notEqual name="flist" property="itemtype" value="N">                              
                                  <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;                 
                            </logic:notEqual> 
                            <logic:equal name="flist" property="itemtype" value="N">
                            <%
                         		num_s++;
                         		request.setAttribute("num_s",num_s+""); 
                         		HashMap infoMap=(HashMap)kqDetailsForm.getKqItem_hash();
                         		//out.println("d = "+abean.get(name));
                       		%>
							<%--<logic:greaterThan name="element" property="${flist.itemid}" value="0">
                                  <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;                 
                              </logic:greaterThan>--%>
                              <div style="text-align:right;">
                               <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${flist.itemid}" value='<%=abean.get(name)+""%>'/>
                              </div>
                       			<%
                                inNum++;
                        		%>  
                           </logic:equal>  
                        </logic:equal>                   
                     </td>
              </logic:equal>    
            </logic:iterate>
          <td align="center" class="RecordRow" nowrap>
          	
            	<a href="/kq/kqself/details/month_details.do?b_more=link&kq_month=<bean:write name="element" property="q03z0" filter="true"/>&dtable=kq_emp_onduty"><img src="/images/edit.gif" border=0></a>
	         </td>
         </tr>
    </hrms:paginationdb>
</table>    
<table  width="100%" align="center" class="RecordRowP">
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
		     <p align="right"><hrms:paginationdblink name="kqDetailsForm" property="pagination" nameId="kqDetailsForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
  </table>
</html:form>