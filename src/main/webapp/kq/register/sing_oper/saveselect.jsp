<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo"%>
<script language="JavaScript" src="/js/meizzDate_saveop.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/sing_oper/sing_operation">
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
  function operation()
  {
    var waitInfo=eval("wait");	   
    waitInfo.style.display="block";    
    dailyRegisterForm.action="/kq/register/sing_oper/sing_operation.do?b_operation=link"; 
    dailyRegisterForm.submit();  
  }
  function MusterInitData()
  {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
  }
  function back()
  {
	  dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_search=link"; 
	    dailyRegisterForm.submit();  
	  }
</script>

<%
int i=0;
int r=0;
%>
<br>
<br>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable">
    	
    <tr>
    
      
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.nbase"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="b0110.label"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="e0122.label"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="e01a1.label"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="label.title.name"/></td>
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
                   &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;      
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/> 
                   &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;      
            </td>                    
            <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;             
             </td>  
              <td align="left" class="RecordRow" nowrap>              
                   <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
            </td> 
             <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="a0101" filter="true"/>&nbsp;
            </td> 
             <td align="left" class="RecordRow" nowrap> 
             <%
                 
             %>             
               <input type="text" name='<%="singListForm.pagination.curr_page_list["+r+"].q03z0"%>' size="12" value='${element.q03z0}' class="inputtext" style="width:100px;font-size:10pt;text-align:left" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>   
             </td>            
         </tr>
         <%
          r++;
         %>
  </hrms:extenditerate>
  <tr><td colspan="6" style="padding:0px;">    
 
  <table width="100%" class="RecordRowP">
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
   </tr>
 </table>
 </td>
 </tr>
  </table>
 <table width="70%" align="center">
 	<tr>
 		<td align="center" style="height:35px;">
        <input type="button" name="btnreturn" value='<bean:message key="reporttypelist.confirm"/>' onclick="operation();" class="mybutton">		
        <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="back();" class="mybutton">						      
      </td>
 	</tr>
 </table> 
 <div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在处理人员数据，请稍候...</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 
</html:form>
<script language="javascript">
 MusterInitData();	
</script>