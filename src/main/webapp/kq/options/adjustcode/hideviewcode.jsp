<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 430px;height: 300px;
 line-height:15px; 
 /*border-width:0px; 
 border-style: groove;
 border-width :thin ;*/
 border-color:#C4D8EE;

}
</STYLE>
<script language="javascript">
  function add(flag)
  {
       adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?b_savehideview=link";         
       adjustcodeFrom.submit();  
       window.returnValue="ok";
       window.close();
   }
   function return_brack()
   {
       adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?br_search=link";
       adjustcodeFrom.submit();  
   }
</script>
<%
	int i=0;
%>
<html:form action="/kq/options/adjustcode/adjustcode">
	<div id="d" class="fixedDiv3">
		 <html:hidden name="adjustcodeFrom" property="table" styleClass="text"/> 
<table border="0" cellspacing="0" align="center" cellpadding="0" width="100%" >

 <tr>
   <td width="100%">
   	<div class="fixedDiv5" style="width:expression(document.body.clientWidth-10);height:expression(document.body.clientHeight-60); ">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	     <thead>              
              <tr class="fixedHeaderTr">     
               <td align="center" class="TableRow" style="border-top: none;border-left: none;border-right: none;" nowrap> 
                序号
               </td>
               <td align="center" class="TableRow" style="border-right: none;border-top: none;" nowrap>
		指标名称&nbsp;
               </td>  
               <td align="center" class="TableRow" style="border-right: none;border-top: none;" nowrap>
		状态&nbsp;
               </td>  
               </tr>                           
   	     </thead>
   	    <hrms:extenditerate id="element" name="adjustcodeFrom" property="recordListForm.list"  pagination="recordListForm.pagination" scope="session" indexes="indexes" pageCount="100">     		  	 	 
         
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
             <%i++;%>  
              <td align="center" class="RecordRow" style="border-right: none;border-top: none;border-left: none;" nowrap width="10%">   
              <%=i%>
              </td>        
              <td align="center" class="RecordRow" style="border-right: none;border-top: none;" nowrap width="45%">               
               <bean:write name="element" property="itemdesc" filter="true"/>
              </td>   
              <td align="center" class="RecordRow" style="border-right: none;border-top: none;" nowrap>   
               <hrms:optioncollection name="adjustcodeFrom" property="v_h_list" collection="list" />
	       <html:select name="element" property="state" size="1" >
               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
               </html:select>                
              </td>  
             
             
	     </tr>	     
             </hrms:extenditerate> 
     </table>
     </div>      
   </td>
 </tr>
 </table>
 </div>
 <table border="0" cellspacing="0" align="center" cellpadding="0" width="100%" style="margin-top: 10px;">
 <tr>
 <td align="center">    
     <input type="button" name="tt" value="<bean:message key="button.save"/>"  class="mybutton" onclick="add('${adjustcodeFrom.flag}');">
    <logic:notEqual name="adjustcodeFrom" property="flag" value="1">
     <input type="button" name="tdf" value="<bean:message key="button.return"/>"  class="mybutton" onclick="return_brack();">
    </logic:notEqual>  
    <logic:equal name="adjustcodeFrom" property="flag" value="1">
     <input type="button" name="tdf" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
    </logic:equal>  
 </td>
 </tr>
 </table>    
</html:form>