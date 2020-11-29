<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.details.KqDetailsForm"%> 
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>

<logic:equal name="kqDetailsForm" property="mess" value="2">
<script language="JavaScript">
function change()
   {
      kqDetailsForm.action="/kq/kqself/details/month_details.do?b_more=link";
      kqDetailsForm.submit();
   }
</script>
</logic:equal>
<logic:equal name="kqDetailsForm" property="mess" value="1">
<script language="JavaScript">
function change()
   {
      kqDetailsForm.action="/kq/kqself/details/month_details_hour.do?b_hour=link";
      kqDetailsForm.submit();
   }
function changeys(dd)
{
	if(dd==2){
 		kqDetailsForm.action="/kq/kqself/details/month_details_hour.do?b_hour=link&dtable=Q03";
    	kqDetailsForm.submit();
 	}else if(dd==1){
 		kqDetailsForm.action="/kq/kqself/details/month_details.do?b_query=link&dtable=Q03";
    	kqDetailsForm.submit();
 	}
}
</script>
</logic:equal>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<%
int i=0;
String name=null;
int num_s=0;
%>
<html:form action="/kq/kqself/details/month_details_hour">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
    <tr>
    	  <logic:equal name="kqDetailsForm" property="mess" value="1">
	     <td align="left" nowrap valign="center">        
            <bean:message key="kq.deration_details.kqnd"/>        
	         <hrms:optioncollection name="kqDetailsForm" property="slist" collection="list" />
	          <html:select name="kqDetailsForm" property="kq_years" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
            </html:select> 
             &nbsp;
             <hrms:optioncollection name="kqDetailsForm" property="tlist" collection="list" />
	          <html:select name="kqDetailsForm" property="tem" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
            </html:select>
      		时间显示方式:
      		<select size="1"   name="selectys"   onchange="changeys(this.value);">
      		 	<option   value="2">HH:mm</option>
      		 	<option   value="1">默认</option>   
      		</select>
           </td>
           </logic:equal>
          <logic:equal name="kqDetailsForm" property="mess" value="2">
         </logic:equal>
          </tr>
          <tr><td height="3px"></td></tr>
         </table>
<div class="fixedDiv2">
       <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   
     <thead>
    <tr>
     
      <logic:iterate id="element" name="kqDetailsForm"  property="flist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" style="border-top:none;border-left: none;" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
     
    </tr>  
  </thead >
<hrms:paginationdb id="element" name="kqDetailsForm" sql_str="kqDetailsForm.sql" table="" where_str="kqDetailsForm.where" columns="${kqDetailsForm.com}" order_by="${kqDetailsForm.orderby}" page_id="pagination"  indexes="indexes">
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
            <logic:iterate id="flist" name="kqDetailsForm"  property="flist" indexId="index">
            <%
               KqDetailsForm kqDetailsForm=(KqDetailsForm)session.getAttribute("kqDetailsForm");
               FieldItem item=(FieldItem)pageContext.getAttribute("flist");
               name=item.getItemid(); 
              %>
             <logic:equal name="flist" property="visible" value="true">
                <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                  <logic:notEqual name="flist" property="itemtype" value="N">
                        <logic:notEqual name="flist" property="codesetid" value="0">
                           <hrms:codetoname codeid="${flist.codesetid}" name="element" codevalue="${flist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;                    
                        </logic:notEqual>
                        <logic:equal name="flist" property="codesetid" value="0">
                            <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                  
                  </logic:notEqual>
                  <logic:equal name="flist" property="itemtype" value="N">
                  		<%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                         HashMap infoMap=(HashMap)kqDetailsForm.getKqItem_hash();
                         //out.println("d = "+abean.get(name));
                       %>
                       <div style="text-align:right;">
                        <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${flist.itemid}" value='<%=abean.get(name)+""%>'/>&nbsp;
                       </div>
                       <%
                                inNum++;
                        %>  
                  </logic:equal>  
              </td>
                       
                      
            </logic:equal>    
          </logic:iterate>
       
         </tr>
    </hrms:paginationdb>
</table>    
</div>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor" >
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
  <logic:equal name="kqDetailsForm" property="mess" value="2">
  <table  width="50%" align="center">
          <tr>  
           <td align="center">
            <hrms:submit styleClass="mybutton" property="b_back">	<bean:message key="button.return"/> </hrms:submit>
           </td>
          </tr>          
</table>
</logic:equal>
  <logic:equal name="kqDetailsForm" property="mess" value="1">
  </logic:equal>
</html:form>