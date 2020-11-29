<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
 <script language="javascript">
 function showOpetion(state,tid)
   {
        oageoptions_selete(state,tid);	
   }  
    function add_report()
   {
      printKqInfoForm.action="/kq/register/select_kqreportdata.do?b_add=link&flaginfo=0";
      printKqInfoForm.submit();
   }   
   function go_must(relatTableid)
   {
      //printKqInfoForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&relatTableid=${printKqInfoForm.relatTableid}";
     // alert("${printKqInfoForm.condition}");
     //printKqInfoForm.submit();
     document.mysearchform.submit();
      var url="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&a_inforkind=1&relatTableid=${printKqInfoForm.relatTableid}&closeWindow=1";//&condition=${printKqInfoForm.condition}";
      //window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=yes,menubar=yes,location=yes,resizable=no,status=yes");
      window.showModalDialog(url,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
   }  
     
</script>
<%
int i=0;
DailyRegisterForm daily=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
String sortitem = "";
if (daily != null) {
	sortitem = daily.getSortitem();
}
if (sortitem == null || sortitem.length() <= 0) {
	sortitem = "";
}

%>

<html:form action="/kq/register/select_kqreportdata">
<br/>
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTableF">
    <tr height="20">
       <!--  <td width=10 valign="top" class="tableft"></td>
          
          <td width=10 valign="top" class="tabright"></td>
          <td valign="top" class="tabremain" width="500"></td> -->  
          <td  align=center class="TableRow">&nbsp;<bean:message key="kq.report.type"/>&nbsp;</td>           	      
   </tr> 
   <tr>
   <td  class="framestyle9">
   <br>
   <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
     <tr> 
   <!--    <td align="center" class="TableRow" nowrap><bean:message key="kq.report.select"/></td> --> 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.report.id"/></td>	  
      <td align="center" class="TableRow" nowrap><bean:message key="kq.report.name"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.report.pagesetup"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.report.print"/></td>	 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.report.update"/></td>  
     		 
    </tr>
    
    <hrms:extenditerate id="element" name="printKqInfoForm" property="printKqInfoForm.list" indexes="indexes"  pagination="printKqInfoForm.pagination" pageCount="20" scope="session">
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
         <!--     <td align="left" class="RecordRow" nowrap>
               <logic:equal name="element" property="string(flag)" value="0">
                  <hrms:checkmultibox name="printKqInfoForm" property="printKqInfoForm.select" value="true" indexes="indexes"/>&nbsp;
               </logic:equal>
             </td>   --> 
             <td align="center" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(report_id)" filter="true"/>&nbsp;
             </td>  
             <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
             </td>  
             <td align="center" class="RecordRow" nowrap>  
             	<logic:equal name="element" property="string(flag)" value="-1">
             		<hrms:priv func_id="270202801">
                   <a href="/kq/register/select_kqreportpar.do?b_par=link&report_id=<bean:write name="element" property="string(report_id)" filter="true"/>&userbase=<bean:write name="printKqInfoForm" property="userbase" filter="true"/>&code=<bean:write name="printKqInfoForm" property="code" filter="true"/>&coursedate=<bean:write name="printKqInfoForm" property="coursedate" filter="true"/>&kind=<bean:write name="printKqInfoForm" property="kind" filter="true"/>&relatTableid=<bean:write name="printKqInfoForm" property="relatTableid" filter="true"/>&self_flag=back">
                   <img src="/images/edit.gif" border=0>
                   </a></hrms:priv>
                </logic:equal>
                <logic:equal name="element" property="string(flag)" value="0">
                   <a href="###">
                   
                   </a>
                </logic:equal>               
              </td>      
              <td align="center" class="RecordRow" nowrap> 
               	<logic:equal name="element" property="string(flag)" value="-1">
              	   <a href="/kq/register/print_kqreport.do?b_view=link&report_id=<bean:write name="element" property="string(report_id)" filter="true"/>&userbase=<bean:write name="printKqInfoForm" property="userbase" filter="true"/>&code=<bean:write name="printKqInfoForm" property="code" filter="true"/>&coursedate=<bean:write name="printKqInfoForm" property="coursedate" filter="true"/>&kind=<bean:write name="printKqInfoForm" property="kind" filter="true"/>&self_flag=back&sortitem=<%=SafeCode.encode(sortitem)  %>">
                   <img src="/images/edit.gif" border=0>
                   </a>   
                 </logic:equal>  
                  <logic:equal name="element" property="string(flag)" value="0">
                   <a href="###" onclick="go_must('<bean:write name="printKqInfoForm" property="relatTableid" filter="true"/>');">
                     <img src="/images/edit.gif" border=0>
                   </a>
                </logic:equal>              
              </td> 
              <td align="center" class="RecordRow" nowrap> 
                 <logic:equal name="element" property="string(flag)" value="-1">
                   <a href="/kq/register/select_kqreportdata.do?b_update1=link&report_id=<bean:write name="element" property="string(report_id)" filter="true"/>">
                   <img src="/images/edit.gif" border=0>
                   </a>
                 </logic:equal>
                 <logic:equal name="element" property="string(flag)" value="0">
                   <a href="/kq/register/select_kqreportdata.do?b_update2=link&report_id=<bean:write name="element" property="string(report_id)" filter="true"/>&flaginfo=1">
                   <img src="/images/edit.gif" border=0>
                   </a>
                 </logic:equal>    
              </td>             	
                                                	    
      	    
          </tr>
        </hrms:extenditerate> 
    </table>
    <!-- <table width="85%" align="center">
    <tr>
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="printKqInfoForm" property="printKqInfoForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="printKqInfoForm" property="printKqInfoForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="printKqInfoForm" property="printKqInfoForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="printKqInfoForm" property="printKqInfoForm.pagination"
                   nameId="printKqInfoForm">
           </hrms:paginationlink>
       </td>
    </tr>
    </table>-->
    <br>
    
   </td>
 </tr>          
</table>
<table  width="50%" align="center">
          <tr>
           <td align="center" style="height:35px;"> 
           <html:hidden name="printKqInfoForm" property="returnURL" styleClass="text"/>
           <html:hidden name="printKqInfoForm" property="condition" styleClass="text"/>
                   <!--<input type="button" name="btnreturn" value='<bean:message key="button.insert"/>' onclick="add_report();" class="mybutton">						      
                   <hrms:submit styleClass="mybutton" property="b_delete"><bean:message key="button.delete"/></hrms:submit>-->
                   <input type="button" name="btnreturn" value='<bean:message key="lable.welcomeboard.themore"/>' onclick="go_must();" class="mybutton">
            </td>
          </tr>          
    </table>
</html:form>
<form name="mysearchform" action="/general/muster/hmuster/searchHroster.do?b_search=link" method="post" target="mysearchframe">
	<input type="hidden" name="condition" value="${printKqInfoForm.condition}">
</form>
<iframe name="mysearchframe" style="display: none;"></iframe>