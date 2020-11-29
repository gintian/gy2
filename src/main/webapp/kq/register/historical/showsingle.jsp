 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript" src="/js/validate.js"></script>
 <style>
<!--
.divStyle{

	border:1px solid #C4D8EE;border-top:none;margin-top:3px;overflow: auto;left:5;
	height:532px;
	width:expression(document.body.clientWidth-11);
}
.divStyle1{
	overflow: auto;left:5;
	width:expression(document.body.clientWidth-11);
}
-->
</style> 
<hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/historical/showsingle_month">
<script language="javascript">
 function couser()
   {
      singleRegisterForm.action="/kq/register/historical/showsingle_month.do?b_browse=link&a0100=${singleRegisterForm.a0100}&kind=${singleRegisterForm.userbase}";
      singleRegisterForm.submit();
   }  
   function change_print()
   {
       singleRegisterForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&kqtable=Q05_arc&relatTableid=${singleRegisterForm.relatTableid}";
       singleRegisterForm.submit();
   }
   function returnC()
   {   
       singleRegisterForm.action="/kq/register/historical/showsingle_month.do?br_browse=link";
       singleRegisterForm.submit();
   }
   </script>
<table>
 <tr>
  <td>
   <table width="50%" border="0" cellspacing="1"  align="left" cellpadding="1">
    <tr>
      <td align= "left" nowrap>
       <html:select name="singleRegisterForm" property="cur_year" size="0" onchange="javascript:couser();">
       <html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
        </html:select>
      </td>     
      <td  align= "left" nowrap> 
       <hrms:codetoname name="singleRegisterForm" codevalue='b0110' codeid="UN" codeitem="codeitem"/>
       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
       <hrms:codetoname name="singleRegisterForm" codevalue='e0122' codeid="UM" codeitem="codeitem"/>
       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
       <hrms:codetoname name="singleRegisterForm" codevalue='e01a1' codeid="@K" codeitem="codeitem"/>
       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
       <bean:write name="singleRegisterForm" property="a0101" />&nbsp;
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
      <div class="divStyle common_border_color"  >
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
                    <!--数字结束-->                                             
               </logic:equal> 
                                    
            </logic:iterate>  
                   
          </tr>
           
        </hrms:paginationdb>        	                           	    		        	        	        
      </table>
      </div>
      </td></tr>
      <tr><td>
      <div class="divStyle1">
     <table  width="100%" align="center" class="RecordRowP">
       <tr>
          <td width="40%" valign="bottom"  class="tdFontcolor" nowrap>
		         <bean:message key="label.page.serial"/>
           <bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
           <bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
           <bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
	  </td>
	  <td  width="60%" align="right" nowrap class="tdFontcolor">
	     <hrms:paginationdblink name="singleRegisterForm" property="pagination" nameId="singleRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>
     </table>
     </div>
     </td></tr>
	<tr>
	   <td align="center"  nowrap>
	   <!-- <input type="button" name="btnreturn" value='<bean:message key="button.print"/>' onclick="change_print();" class="mybutton"> -->	   	     	                 
       <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="returnC();" class="mybutton">						      
     </td>
   </tr> 

</table>
</html:form>
