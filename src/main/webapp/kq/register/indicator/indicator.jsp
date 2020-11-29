<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
  function add()
  {
      var re_flag="${kqIndicatorForm.re_flag}";
      if(re_flag=="1")
      {
         kqIndicatorForm.action="/kq/register/indicator/indicator.do?b_save1=link";
         window.returnValue = true;
         kqIndicatorForm.submit();    
      }else if(re_flag=="2")
      {
         kqIndicatorForm.action="/kq/register/indicator/indicator.do?b_save2=link";
         window.returnValue = true;
         kqIndicatorForm.submit();    
      }else if(re_flag=="3")
      {
         kqIndicatorForm.action="/kq/register/indicator/indicator.do?b_save3=link";
         window.returnValue = true;
         kqIndicatorForm.submit(); 
      }else
      {
         kqIndicatorForm.action="/kq/register/indicator/indicator.do?b_save1=link";
         window.opener.location.reload();   
      }
      window.close();
  }
 
</script>
<%
	int i=0;
%>
<div class="fixedDiv3">
<html:form action="/kq/register/indicator/indicator">
<table border="0" cellspacing="0"  align="center" cellpadding="0" width="100%" >

 <tr>
   <td width="100%">
	<div class="common_border_color fixedDiv" style="border: 1px solid;height:expression(document.body.clientHeight-60);width: 100%;">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	     <thead>
              <tr class="fixedHeaderTr">      
               <td align="center" class="TableRow" style="border-top: none;border-left: none;border-right: none;" nowrap>
		指标名称&nbsp;
               </td>  
               <td align="center" class="TableRow" style="border-top: none; none;border-right: none;" nowrap>
		状态&nbsp;
               </td>  
                                          
   	     </thead>
   	    <hrms:extenditerate id="element" name="kqIndicatorForm" property="recordListForm.list"  pagination="recordListForm.pagination" scope="session" indexes="indexes" pageCount="100">     		  	 	 
         
	     <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>
                          
              <td align="center" class="RecordRow" style="border-top: none;border-left: none;border-right: none;" nowrap>               
               <bean:write name="element" property="itemdesc" filter="true"/>
              </td>   
              <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;" nowrap>   
               <hrms:optioncollection name="kqIndicatorForm" property="v_h_list" collection="list" />
	       <html:select name="element" property="state" size="1" >
               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
               </html:select>                
              </td>  
             
             <%i++;%>  
	     </tr>	     
             </hrms:extenditerate> 
     </table>      
   </td>
 </div>
 </tr>
 <tr>
 <td align="center" style="height:35px;">    
     <input type="button" name="tt" value="<bean:message key="button.save"/>"  class="mybutton" onclick="add();">
     <input type="button" name="tdf" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
      
     
 </td>
 </tr>
 </table>    
</html:form>
</div>