<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo"%>
<script language="JavaScript" src="/js/meizzDate_saveop.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/sing_oper/singpickdata">
<script language="javascript">
   var outObject;
   function getdate(tt)
   {
     outObject=tt;     
     var hashvo=new ParameterSet();     
     hashvo.setValue("date",tt.value);       		
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'15310000003'},hashvo);
   }
   function showSelect(outparamters)
  { 
     var tes=outparamters.getValue("date");
     var flag=outparamters.getValue("flag");
     if(flag=="1")
     {
        alert("调用的时间不能在本考勤期间之前!");
     }else if(flag=="2")
     {
        alert("调用的时间不能在本考勤期间之后!");
     }
     outObject.value=tes;
  }
 
</script>

<%
int i=0;
int r=0;
%>
<br>
<br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    	
    <tr>
    
      
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.nbase"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.b0110"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.e0122"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.e01a1"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.emp.name"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.date"/></td>
   </tr>  
 
  <hrms:extenditerate id="element" name="dailyRegisterForm" property="singListForm.list" indexes="indexes"  pagination="singListForm.pagination" pageCount="20" scope="session">
          <%
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
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="@@" name="element" codevalue="nbase" codeitem="codeitem" scope="page"/> 
                   <bean:write name="codeitem" property="codename" />      
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/> 
                   <bean:write name="codeitem" property="codename" />      
            </td>                    
            <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	    <bean:write name="codeitem" property="codename" />             
             </td>  
              <td align="left" class="RecordRow" nowrap>              
                   <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	    <bean:write name="codeitem" property="codename" />  
            </td> 
             <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="a0101" filter="true"/>&nbsp;
            </td> 
             <td align="left" class="RecordRow" nowrap> 
             <%
                 
             %>             
                <html:text name="dailyRegisterForm" property='<%="singListForm.pagination.curr_page_list["+r+"].q03z0"%>' size="20" maxlength="20" styleClass="TEXT4" onfocus="setday(this);" onchange="getdate(this);"/>   
             </td>            
         </tr>
         <%
          r++;
         %>
  </hrms:extenditerate>    
  </table>
  <table width="70%" align="center">
    <tr>
       <td align="left">
		
       </td>	
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="dailyRegisterForm" property="recordListForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="dailyRegisterForm" property="recordListForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="dailyRegisterForm" property="recordListForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="dailyRegisterForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
    </tr>
   <tr>
   <td>
   </td>
     <td align="center">
        <hrms:submit styleClass="mybutton" property="b_pickdata">
            		<bean:message key="kq.register.kqduration.ok"/>
	</hrms:submit>
        <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
      </td>
   </tr>
  
 </table> 
</html:form>