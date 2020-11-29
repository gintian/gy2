<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
  function submitDEL()
  {
  	var len=document.kqMachineForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.kqMachineForm.elements[i].type=="checkbox")
           {
              if(document.kqMachineForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert("请选择要删除的记录！");
          return false;
       }
  	if(confirm(DEL_INFO))
  	{
      kqMachineForm.action="/kq/options/machine/machine_location.do?b_delete=link";
      kqMachineForm.submit();  
    }
  }
  function submitUp(id)
  {
      kqMachineForm.action="/kq/options/machine/machine_location.do?b_edit=link&e_flag=up&location_id="+id;
      kqMachineForm.submit();  
  }
  function submitNew()
  {
      kqMachineForm.action="/kq/options/machine/machine_location.do?b_edit=link&e_flag=add";
      kqMachineForm.submit();  
  }
</script>
<%
	int i=0;
%>
<html:form action="/kq/options/machine/machine_location">
<table border="0" cellspacing="0"  align="center" cellpadding="0" width="90%" style="margin-top: 10px">
 <tr>
   <td width="100%">   
      
    <td>
 </td>
 <tr>
   <td width="100%">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	     <thead>
              <tr>      
               <td align="center" class="TableRow" nowrap>
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.machine.machine_no"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.machine.name"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.machine.location"/>&nbsp;
               </td>     
               <td align="center" class="TableRow" nowrap>
               出入标志          
               </td>    
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.machine.port"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.machine.baud_rate"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.machine.ip_address"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
			 考勤卡号长度&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
		<bean:message key="button.edit"/>&nbsp;
               </td>                           
   	     </thead>
   	       		  	 	 
          <hrms:paginationdb id="element" name="kqMachineForm" sql_str="kqMachineForm.sqlstr" table="" where_str="kqMachineForm.where" columns="kqMachineForm.column" order_by="" pagerows="20" page_id="pagination">
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
              <td align="center" class="RecordRow" nowrap>               
               <hrms:checkmultibox name="kqMachineForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
              </td>                 
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="machine_no" filter="true"/>
              </td>   
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="name" filter="true"/>
              </td>  
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="location" filter="true"/>
              </td>  
              <td align="center" class="RecordRow" nowrap>               
                <logic:equal name="element" property="inout_flag" value="-1">
                  出
                </logic:equal>
                <logic:equal name="element" property="inout_flag" value="0">
                  不限
                </logic:equal>
                <logic:equal name="element" property="inout_flag" value="1">
                  进
                </logic:equal>
              </td>  
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="port" filter="true"/>
              </td> 
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="baud_rate" filter="true"/>
              </td> 
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="ip_address" filter="true"/>
              </td>
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="card_len" filter="true"/>
              </td>
               <td class="RecordRow" nowrap align="center">
                 <a href="###" onclick="submitUp('<bean:write name="element" property="location_id" filter="true"/>');">
                  <img src="/images/edit.gif" border="0">
                 </a> 
               </td> 
             <%i++;%>  
	     </tr>	     
          </hrms:paginationdb>
     </table>
     <table width="100%" class="RecordRowP"  align="center">
      <tr>
       <td valign="bottom" class="tdFontcolor">
          第<bean:write name="pagination" property="current" filter="true" />页
          共<bean:write name="pagination" property="count" filter="true" />条
          共<bean:write name="pagination" property="pages" filter="true" />页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationdblink name="kqMachineForm" property="pagination" nameId="kqMachineForm" scope="page">
             </hrms:paginationdblink>
       </td>
      </tr>
    </table>  
   </td>
 </tr>
 <tr>
 <td align="center" style="height:35px;">
     <input type="button" name="tt" value="<bean:message key="button.insert"/>"  class="mybutton" onclick="submitNew();">
     <input type="button" name="tdf" value="<bean:message key="button.delete"/>"  class="mybutton" onclick="submitDEL();">
     <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqMachineForm"/> 
     
 </td>
 </tr>
 </table>    
</html:form>