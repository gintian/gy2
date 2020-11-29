<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/browse_single">
<script language="javascript">

    var bean_value
    function setbean(workdate)
   {
     var hashvo=new ParameterSet();
     hashvo.setValue("workdate",workdate);	
     
     hashvo.setValue("restdate","${singleRegisterForm.rest_date}");
     hashvo.setValue("b0110","${singleRegisterForm.b0110_value}");        	
     var request=new Request({method:'post',onSuccess:getBean,functionId:'15301110999'},hashvo);
   }
    function getBean(outparamters)
    {
      bean_value=outparamters.getValue("onedate");      
    }
    function showBean()
    {      
      return bean_value;
    }
function change_print() {
    //singleRegisterForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&kqtable=Q03_arc&relatTableid=${singleRegisterForm.relatTableid}";
    //singleRegisterForm.submit();
    document.mysearchform.submit();
	var returnURL = getEncodeStr("${singleRegisterForm.returnURL}");
	var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&kqtable=Q03_arc&relatTableid=${singleRegisterForm.relatTableid}&closeWindow=1";
		urlstr+="&returnURL="+returnURL;
	window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}

   function kqreport()
   {	
      //singleRegisterForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&userbase=${dailyRegisterForm.userbase}&code=${dailyRegisterForm.code}&coursedate=${dailyRegisterForm.kq_duration}&kind=${dailyRegisterForm.kind}&self_flag=tran";
      //singleRegisterForm.target="il_body";
      //singleRegisterForm.submit();
      singleRegisterForm.action="/kq/register/historical/print_kqreport.do?b_search=link";
      singleRegisterForm.submit();
   }
 </script> 
<table border="0" cellspacing="0"  cellpadding="0">
 <tr>
  <td>
   <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
      <td  align= "left" nowrap> 
       <bean:write name="singleRegisterForm" property="b0110" />&nbsp;
       <bean:write name="singleRegisterForm" property="e0122" />&nbsp;
       <bean:write name="singleRegisterForm" property="e01a1" />&nbsp;
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
    <div class="fixedDiv2">
      <%int i=0;%>
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="border:none;" >
      <thead>
         <tr>
         <logic:iterate id="element"    name="singleRegisterForm"  property="singfielditemlist"> 
             <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" style="border-left:none;" nowrap>
                 &nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                </td>
              </logic:equal>
           </logic:iterate>         	        
           </tr>
      </thead>          
      <hrms:paginationdb id="element" name="singleRegisterForm" sql_str="singleRegisterForm.sqlstr" table="" where_str="singleRegisterForm.strwhere" columns="singleRegisterForm.columns" order_by="singleRegisterForm.orderby" pagerows="31" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++; 
            
          %>  
            
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
                             <html:hidden name="element" property="${info.itemid}"/> 
                          </td>  
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                          <logic:notEqual name="info" property="itemid" value="q03z0">
                             <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                                <html:hidden name="element" property="${info.itemid}"/> 
                             </td>
                           </logic:notEqual> 
                           <logic:equal name="info" property="itemid" value="q03z0">
                                <td align="left" class="RecordRow" style="border-left:none;" nowrap>&nbsp; 
                                   <script language="javascript"> 
                                  
                                 setbean('<bean:write name="element" property="${info.itemid}" filter="true"/>'); 
                                 document.writeln(showBean());
                                </script> 
                                  
                              &nbsp; 
                              <html:hidden name="element" property="${info.itemid}" styleClass="text"/>&nbsp; 
                             </td>
                           </logic:equal> 
                       </logic:equal>
                                            
                   </logic:equal>
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                      <td align="left" class="RecordRow" style="border-left:none;" nowrap> 
                           <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                         </logic:greaterThan> 
                      </td>
                   </logic:equal>
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
     <table  width="100%" class="RecordRowP" align="center">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="80%" align="left" nowrap class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="singleRegisterForm" property="pagination" nameId="singleRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>
     </table>
   </td>
 </tr>
 <tr>
   <td width="60%" align="left"  nowrap>    
	   <table>
	   	<tr>
	   		<td>
		      <input type="button" name="btnreturn" value='<bean:message key="button.print"/>' onclick="change_print();" class="mybutton">	     	                 	                 
	          <input type="button" name="btnreturn" value="返回" onclick="kqreport();" class="mybutton">
	   		</td>
	   	</tr>
	   </table>
      </td>
   <td width="40%"></td>
 </tr>
</table>
</html:form>
<form name="mysearchform" action="/general/muster/hmuster/searchHroster.do?b_inValue=link" method="post" target="mysearchframe">
	<input type="hidden" name="condition" value="${singleRegisterForm.condition}">
</form>
<iframe name="mysearchframe" style="display: none;"></iframe>