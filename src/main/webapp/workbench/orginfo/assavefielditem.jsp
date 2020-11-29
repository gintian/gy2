<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<hrms:themes></hrms:themes>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 100%;height: 250px;
 line-height:15px; 
 /*border-width:0px; 
 border-style: groove;
 border-width :thin ;*/
 border-color:#C4D8EE;

}
</STYLE>
<script language="javascript">  
   function asfield()
   {
        var len=document.orgInfoForm.elements.length;
        var fielditems="";
        var isCorrect=false;
        for (i=0;i<len;i++)
        {
           if (document.orgInfoForm.elements[i].type=="checkbox")
            {              
              if( document.orgInfoForm.elements[i].checked==true&&document.orgInfoForm.elements[i].value!="all")
                fielditems=fielditems+document.orgInfoForm.elements[i].value+",";
            }
        }
        if(fielditems=="")
        {
          alert("请选择指标！");
          return false;
        }else
        {
           if(confirm("确定对所选指标进行批量另存操作？"))
           {
              window.returnValue=fielditems;
              window.close();
           }
        }
   }
</script>
<%
	int i=0;
%>
<html:form action="/workbench/orginfo/assavefielditem">
<div class="fixedDiv3">
<table border="0" cellspacing="0" width="100%" align="center" cellpadding="0">
 <tr>
   <td width="100%" height="10"> 
<fieldset align="center" style="width:100%;height:280px;">
    	      <legend >批量另存</legend>  

<table border="0" cellspacing="0" align="center" cellpadding="0" width="98%" >
 <tr>
   <td width="100%" height="10">   
      
    <td>
 </td>
 <tr>
   <td width="100%">
     <div id="d" class="div2">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">  
       <tr>     
       <td align="center" class="TableRow" nowrap> 
        <input type="checkbox" name="selbox" value='all' onclick="batch_select(this,'selbox');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
       </td>
               <td align="center" class="TableRow" nowrap>
		指标名称&nbsp;
               </td>  
             
               </tr>                           
   	     
   	    <hrms:extenditerate id="element" name="orgInfoForm" property="assave_fieldlistForm.list"  pagination="assave_fieldlistForm.pagination" scope="session" indexes="indexes" pageCount="100">     		  	 	 
         
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
              <td align="center" class="RecordRow" nowrap width="10%">   
              <input type="checkbox" name="selbox" value='<bean:write name="element" property="itemid" filter="true"/>'>&nbsp;
              
              </td>        
              <td align="center" class="RecordRow" nowrap width="45%">               
               <bean:write name="element" property="itemdesc" filter="true"/>
              </td>  
             
             
	     </tr>	     
             </hrms:extenditerate> 
     </table>   
    </div>
   </td>
 </tr>
 </table>
 </fieldset>  
 </td></tr></table>
 <table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="100%" style="margin-top: 10px;">
 <tr>
 <td align="center">    
     <input type="button" name="tt" value="<bean:message key="button.ok"/>"  class="mybutton" onclick="asfield();">
     <input type="button" name="tt" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
 </td>
 </tr>
 </table>    
 </div>
</html:form>