 <%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.kq.register.SingleRegisterForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<% 
   SingleRegisterForm singleRegisterForm = (SingleRegisterForm)session.getAttribute("singleRegisterForm");
%>
<script language="javascript" src="/js/validate.js"></script>
  <style>
<!--
.divStyle{

	border:1px solid #C4D8EE;border-top:none;margin-top:3px;overflow: auto;left:5;
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-11);
}
.divStyle1{
	overflow: auto;left:5;
	border:1px solid;
	width:expression(document.body.clientWidth-11);
	border-top: none;
}
-->
</style>  
<html:form action="/kq/register/history/orgyearsingle">
<script language="javascript">
 function couser()
   {
      singleRegisterForm.action="/kq/register/history/orgyearsingle.do?b_browse=link&b0110=${singleRegisterForm.b0110}";
      singleRegisterForm.submit();
   }  
   function change_print()
   {
       singleRegisterForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&kqtable=&relatTableid=${singleRegisterForm.relatTableid}";
       singleRegisterForm.submit();
   }
   function fh(){
	 singleRegisterForm.action= "${singleRegisterForm.returnURL2}";
	 singleRegisterForm.target="il_body";
	 singleRegisterForm.submit();
   }
   </script>
   <hrms:themes /> <!-- 7.0css -->
<table>
 <tr>
  <td>
   <table width="50%" border="0" cellspacing="1"  align="left" cellpadding="1">
    <tr>
      <td align= "left" nowrap>      
       <html:select name="singleRegisterForm" property="cur_year" size="0" onchange="javascript:couser();">
       <html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
        </html:select>
        &nbsp;&nbsp;&nbsp;
        机构名称: <bean:write  name="singleRegisterForm" property="org_name"/>
      </td>     
      <td  align= "left" nowrap>  
      <html:hidden name="singleRegisterForm" property="returnURL" styleClass="text"/>
        <html:hidden name="singleRegisterForm" property="condition" styleClass="text"/>                        
          
       </td>
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td>
      
      <%int i=0;%>
      <%int s=0;%>
      <%int n=0;%>
      <div class="divStyle common_border_color">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="border:none;" >
      <thead>
         <tr>
           <logic:iterate id="element"    name="singleRegisterForm"  property="singfielditemlist"> 
             <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" style="border-left:none;" nowrap>
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
                </td>
              </logic:equal>
           </logic:iterate>         	        
         </tr>
      </thead>        
       
      <hrms:paginationdb id="element" name="singleRegisterForm" sql_str="singleRegisterForm.sqlstr" table="" where_str="singleRegisterForm.strwhere" columns="singleRegisterForm.columns" order_by="singleRegisterForm.orderby" pagerows="31" page_id="pagination">
         
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++; 
            
          %>  
            <% int  inNum=0;%>
           <logic:iterate id="info" name="singleRegisterForm"  property="singfielditemlist"> 
               
                  <logic:equal name="info" property="visible" value="false">
                     <html:hidden name="element" property="${info.itemid}"/>  
                  </logic:equal>  
                  <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                      <logic:equal name="info" property="itemtype" value="A">
                         <logic:notEqual name="info" property="codesetid" value="0">
                            <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                               <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                               &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                               
                            </td>  
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">
                           
                               <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                                 
                                </td>
                             
                          </logic:equal>
                      </logic:equal>
                      <!--字符型-->
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                         <td align="left" class="RecordRow" style="border-left:none;" nowrap>                        
                           <logic:greaterThan name="element" property="${info.itemid}" value="0">
                                  <bean:write name="element" property="${info.itemid}"/>
                            </logic:greaterThan>      
                         </td>
                    </logic:equal>
                    <logic:notEqual name="info" property="itemtype" value="N">
                   	<logic:notEqual name="info" property="itemtype" value="A">
                      <td align="center" class="RecordRow" style="border-left:none" nowrap>
                       &nbsp;
                      </td>
                      </logic:notEqual>
                   </logic:notEqual>
                    <!--数字结束-->                                             
               </logic:equal>  
            </logic:iterate>  
          </tr>
        </hrms:paginationdb>        	                           	    		        	        	        
      </table>
      </div>
     </td>
   </tr> 
   <tr>
   <td>
     <table width="100%" class="divStyle1 common_border_color" align="center">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
               <bean:message key="label.page.serial"/>
           <bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
           <bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
           <bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
	  </td>
	  <td  width="80%" align="right" nowrap class="tdFontcolor">
	     <hrms:paginationdblink name="singleRegisterForm" property="pagination" nameId="singleRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>
     </table>
   </td>
 </tr>
 <tr>
	   <td align="center"  nowrap>	
	    <input type="button" name="btnreturn" value='<bean:message key="button.print"/>' onclick="change_print();" class="mybutton">	     	                 
        <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="fh();" class="mybutton">	       					      					      
     </td>
 </tr>
</table>
</html:form>
