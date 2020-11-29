<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.divStyle{

	border:1px solid #C4D8EE;margin-top:3px;overflow: auto;left:5;
	height:expression(document.body.clientHeight-140);
	width:expression(document.body.clientWidth-30); 
}
.divStyle1{
	overflow: auto;left:5;
	width:expression(document.body.clientWidth-30); 
}
-->
</style>
<html:form action="/kq/register/history/showsingle">
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
function change_print(){
//singleRegisterForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&kqtable=&relatTableid=${singleRegisterForm.relatTableid}";
//singleRegisterForm.submit();
	document.mysearchform.submit();
	var returnURL = getEncodeStr("${singleRegisterForm.returnURL}");
	var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&kqtable=&relatTableid=${singleRegisterForm.relatTableid}&closeWindow=1";
		urlstr+="&returnURL="+returnURL;
	window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}
    function returnC()
   {
       singleRegisterForm.action="/kq/register/history/showsingle.do?br_sing=link";
       singleRegisterForm.submit();
   }
 </script> 
 <hrms:themes /> <!-- 7.0css -->
<table border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td>
   <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
      <td  align= "left" nowrap> 
        <hrms:codetoname name="singleRegisterForm" codevalue='b0110' codeid="UN" codeitem="codeitem"/>
        &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
        <hrms:codetoname name="singleRegisterForm" codevalue='e0122' codeid="UM" codeitem="codeitem"/>
        &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
        <hrms:codetoname name="singleRegisterForm" codevalue='e01a1' codeid="@K" codeitem="codeitem"/>
        &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
        <bean:write name="singleRegisterForm" property="a0101" />&nbsp;
         <input type="hidden" name="returnURL" value="/kq/register/history/showsingle.do?b_sing=link" class="text">
        </td>
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td valign="top">
      <%int i=0;%>
      <div class="fixedDiv2">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>
         <tr>
         <logic:iterate id="element"    name="singleRegisterForm"  property="singfielditemlist"> 
             <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" style="border-left:none;border-top: none;" nowrap>
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
                      <td align="left" class="RecordRow" style="border-left:none;" nowrap> &nbsp; 
                           <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                         </logic:greaterThan> 
                      </td>
                   </logic:equal>
                   <logic:equal name="info" property="itemtype" value="M">
                       <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                           &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                           <html:hidden name="element" property="${info.itemid}"/> 
                       </td>
                   </logic:equal>
                   <logic:equal name="info" property="itemtype" value="D">
                       <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                           &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                           <html:hidden name="element" property="${info.itemid}"/> 
                       </td>
                   </logic:equal>
                </logic:equal> 
            </logic:iterate>   
          </tr>
        </hrms:paginationdb>    	                           	    		        	        	        
      </table>
      </div>
      </td></tr>
      <tr><td>
      <div style="*width:expression(document.body.clientWidth-10);">
     <table  width="100%" align="center" class="RecordRowP" border="0" cellpadding="0" cellspacing="0">
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
      </div>
    </td></tr>
	<tr style="padding-top: 5px">
	   <td align="center"  nowrap>    
	      	
	      <input type="button" name="btnreturn" value='<bean:message key="button.print"/>' onclick="change_print();" class="mybutton">	     	                 	                 
              
              <input type="button" name="btnreturn" value="<bean:message key="button.return"/>" onclick="returnC();" class="mybutton">			      
              
     </td>
   </tr> 
</table>
</html:form>
<form name="mysearchform" action="/general/muster/hmuster/searchHroster.do?b_inValue=link" method="post" target="mysearchframe">
</form>
<iframe name="mysearchframe" style="display: none;"></iframe>