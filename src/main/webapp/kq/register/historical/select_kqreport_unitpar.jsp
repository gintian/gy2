<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language=JavaScript> 
function IsDigit() 
{ 
  return ((event.keyCode >= 46) && (event.keyCode <= 57)&&(event.keyCode!=47)); 
} 
function show()
{
	var bb=eval("b");
	bb.style.display="block";
}
function backpage()
{
	var username="${printKqInfoForm.username}";
    var report_unitid="${printKqInfoForm.report_unitid}"
    printKqInfoForm.action="/kq/register/historical/print_kqreport_unittable.do?b_viewtable=link&report_unitid=${printKqInfoForm.report_unitid}&userbaseunit=${printKqInfoForm.userbaseunit}&unita0100=${printKqInfoForm.unita0100}&fileunit=2&username="+getEncodeStr(username);
    printKqInfoForm.submit();
    //history.back();
}
function closes()
{
	var bb=eval("b");
	bb.style.display="none"; 

}
function SetDownMsg(s) {
   var pageMode, w_str,h_str;
   pageMode = s.options[s.selectedIndex].value;
   
   if (pageMode == "A3") 
   {
     document.printKqInfoForm.widthP.readOnly=true;
     document.printKqInfoForm.heightP.readOnly=true;
     w_str="297";
     h_str="420";
   }
   if (pageMode == "A4") 
   {
     document.printKqInfoForm.widthP.readOnly=true;
     document.printKqInfoForm.heightP.readOnly=true;
     w_str="210";
     h_str="297";
   }
   if (pageMode == "A5") 
   {   
     document.printKqInfoForm.widthP.readOnly=true;
     document.printKqInfoForm.heightP.readOnly=true; 
     w_str="148";
     h_str="210";
   }
   if (pageMode == "B5") 
   {
     document.printKqInfoForm.widthP.readOnly=true;
     document.printKqInfoForm.heightP.readOnly=true;
     w_str="182";
     h_str="257";
   }
   if (pageMode == "16开") 
   {
     document.printKqInfoForm.widthP.readOnly=true;
     document.printKqInfoForm.heightP.readOnly=true;
     w_str="184";
     h_str="260";
   }
   if (pageMode == "32开") 
   {
     document.printKqInfoForm.widthP.readOnly=true;
     document.printKqInfoForm.heightP.readOnly=true;
     w_str="130";
     h_str="184";
   }   
   if (pageMode == "self") 
   {
     document.printKqInfoForm.widthP.readOnly=false;
     document.printKqInfoForm.heightP.readOnly=false;
     w_str="";
     h_str="";
   }
   if (s.selectedIndex != 0)
   {
     
     document.printKqInfoForm.widthP.value = w_str;
     document.printKqInfoForm.heightP.value = h_str;
   }else
   {
     document.printKqInfoForm.widthP.readOnly=false;
     document.printKqInfoForm.heightP.readOnly=false;
     document.printKqInfoForm.widthP.value = "";
     document.printKqInfoForm.heightP.value = "";
   }
   return 1;
}
  function oplidata()
  {
     var width_str=$F('parsevo.width');
     var hieght_str=$F('parsevo.height');        
     if(width_str=="")
     {
        alert("页面宽度不能为空！");
        return false;
     }
     if(hieght_str=="")
     {
        alert("页面高度不能为空！");
        return false;
     }
     var l_width=parseInt($F('parsevo.left'));
     var r_width=parseInt($F('parsevo.right'));
     var rl_w=l_width+r_width;
     var t_h=parseInt($F('parsevo.top'));
     var b_h=parseInt($F('parsevo.bottom'));
     var tb_h=t_h+b_h;
     if(parseInt(width_str)<16)
     {
        alert("页面设置页面的宽不能小于16！");
        return false;
     }
     if(parseInt(hieght_str)<16)
     {
        alert("页面设置页面的高不能小于16！");
        return false;
     }
     if(document.printKqInfoForm.topp.value>80)     
     {
        alert("页面上边距,边距不能大于80")
         return false;
     }else if(document.printKqInfoForm.bottom.value>80)
     {
       alert("页面下边距,边距不能大于80")
       return false;
     }else if(document.printKqInfoForm.left.value>80)     
     {
        alert("页面左边距,边距不能大于80")
         return false;
     }else if(document.printKqInfoForm.right.value>80)
     {
       alert("页面右边距,边距不能大于80")
       return false;
     }
     if(rl_w>=parseInt(width_str))
     {
        alert("页面设置左右页面边距不能大于页面的宽！");
        return false;
     }else if(tb_h>=parseInt(hieght_str))
     {
        alert("页面设置上下页面边距不能大于页面的高！");
        return false;
     }
     printKqInfoForm.action="/kq/register/historical/print_kqreport_unittable.do?b_parupdate=link";
     printKqInfoForm.submit();
  }
</script> 
<html:form action="/kq/register/historical/print_kqreport_unittable">
<br>	
      <html:hidden name="printKqInfoForm" property="report_unitid" styleClass="text"/>
      <html:hidden name="printKqInfoForm" property="username" styleClass="text"/>
      <html:hidden name="printKqInfoForm" property="unita0100" styleClass="text"/>      
      <html:hidden name="printKqInfoForm" property="userbaseunit" styleClass="text"/>
      <table width="550" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
                <!--  <td width=10 valign="top" class="tableft"></td>
         		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="kq.label.report_format"/>&nbsp;</td>
       		    <td width=10 valign="top" class="tabright"></td>
       		    <td valign="top" class="tabremain" width="500"></td> -->
       		    <td align=center  colspan="4" class="TableRow">&nbsp;<bean:message key="kq.label.report_format"/>&nbsp;</td> 
       	                  	      
         </tr>         
         <tr>
         <td>
           <table width="550" border="0" cellpadding="0" cellspacing="0" align="center" valign="top">
			<tr>
             <td class="framestyle9">
               <table align="center">
                 <tr align="center">
                   <td height="40" align="center">
                     <html:text name="printKqInfoForm" property="parsevo.name" size="50" styleClass="text4"/>
                     
                   </td>
                  </tr>
                </table>          
            	<!--纸张-->
            	<html:hidden name="printKqInfoForm" property="parsevo.value" styleClass="text"/>
            	<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top" >
            	<tr>
                <td>
                  <table align="center" width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                    <tr>  
                     <td width="50%"  valign="top" align="left">
                       <table  width="95%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                         <tr>
		            <td width="100%" valign="top" align="right">
		               <fieldset align="right" style="width:90%;">
    				  <legend ><bean:message key="report.parse.page"/></legend>
		                      <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	<tr>
		                          <td width="100%" height="30" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<bean:message key="report.parse.pagetype"/>&nbsp;&nbsp;
		                	     </td>
		                	     <td>
		                	        <html:select name="printKqInfoForm" property="parsevo.pagetype" size="1" onchange="SetDownMsg(this)">
                                                <html:optionsCollection property="parsevo.pagetypelist" value="dataValue" label="dataName"/>
                                                </html:select>
                                              </td>
		                	     <tr>
		                	   </table>  
		                                    
		                	  </td>
		                      	</tr>
		                      	<tr>
		                	  <td width="100%" height="30" >
		                	  <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<bean:message key="report.parse.pagewidth"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.width" styleId="widthP" size="5" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>&nbsp;mm 
		                	     </td>
		                	     <td>
		                	      &nbsp; <bean:message key="report.parse.pageheight"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.height" styleId="heightP" size="5" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>&nbsp; mm
		                	     </td>
		                	     <tr>
		                	   </table>  
		                	  </td>
		                	</tr>
		                	<tr>
		                	 <td width="100%" height="20">
		                	  <table>
		                	   <tr>
		                	     <td width="50%">	
		                	       &nbsp;<bean:message key="report.parse.orientation"/>&nbsp;               	     
		                	       <logic:equal name="printKqInfoForm" property="parsevo.orientation" value="0">                    
                                                 &nbsp;<html:radio name="printKqInfoForm" property="parsevo.orientation" value="${printKqInfoForm.parsevo.orientation}"/> <bean:message key="report.parse.orientation.erect"/> 
                                               </logic:equal>
                                               <logic:notEqual name="printKqInfoForm" property="parsevo.orientation" value="0">                    
                                                  &nbsp;<html:radio name="printKqInfoForm" property="parsevo.orientation" value="0"/><bean:message key="report.parse.orientation.erect"/> 
                                               </logic:notEqual>
		                	     </td>
		                	      <td width="50%">
		                	      <logic:equal name="printKqInfoForm" property="parsevo.orientation" value="1">                    
                                                 &nbsp;<html:radio name="printKqInfoForm" property="parsevo.orientation" value="${printKqInfoForm.parsevo.orientation}"/><bean:message key="report.parse.orientation.across"/>
                                               </logic:equal>
                                               <logic:notEqual name="printKqInfoForm" property="parsevo.orientation" value="1">                    
                                                  &nbsp;<html:radio name="printKqInfoForm" property="parsevo.orientation" value="1"/><bean:message key="report.parse.orientation.across"/> 
                                               </logic:notEqual>
		                	     </td>
		                	     <tr>
		                	   </table> 
		                	     
			                  </td>
		                      	</tr>
		                     </table>
		                   </fieldset>
		             </td>
		         </tr>
		       </table>
		      </td>
		      <td  width="50%"  valign="top" align="right">
		        <table  width="95%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                         <tr>
		           <td width="100%" align="left">
		                 <fieldset align="left" style="width:90%;">
    				  <legend ><bean:message key="report.parse.pageborder.name"/></legend>
		                      <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	<tr>
		                          <td width="100%" height="30" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	        <bean:message key="report.parse.pageborder.top"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.top" size="8" styleClass="text" styleId='topp' onkeypress="event.returnValue=IsDigit();"/>&nbsp; &nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.pageborder.bottom"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.bottom" size="8" styleClass="text" styleId='bottom' onkeypress="event.returnValue=IsDigit();"/>&nbsp; &nbsp;
                                              </td>
		                	     <tr>
		                	   </table> 		                                    
		                	  </td>
		                      	</tr>
		                      	<tr>
		                	  <td width="100%" height="30" >
		                	  <table>
		                	   <tr>
		                	     <td>
		                	       <bean:message key="report.parse.pageborder.left"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.left" size="8" styleClass="text" styleId='left' onkeypress="event.returnValue=IsDigit();"/>&nbsp; &nbsp;
		                	     </td>
		                	     <td>
		                	       <bean:message key="report.parse.pageborder.right"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.right" size="8" styleClass="text" styleId='right' onkeypress="event.returnValue=IsDigit();"/>&nbsp; &nbsp;
		                	     </td>
		                	     <tr>
		                	   </table>  
		                	  </td>
		                	</tr>	
		                	<tr>
		                	 <td width="100%" height="20">
		                	  <table>
		                	  
		                	   <tr>
		                	     <td width="50%">	
		                	       
		                	       <bean:message key="report.parse.len"/>&nbsp;               	     
		                	       <logic:equal name="printKqInfoForm" property="parsevo.unit" value="px">                    
                                                 &nbsp;<html:radio name="printKqInfoForm" property="parsevo.unit" value="${printKqInfoForm.parsevo.unit}"/> <bean:message key="report.parse.px"/>
                                               </logic:equal>
                                               <logic:notEqual name="printKqInfoForm" property="parsevo.unit" value="px">                    
                                                  &nbsp;<html:radio name="printKqInfoForm" property="parsevo.unit" value="px"/> <bean:message key="report.parse.px"/>
                                               </logic:notEqual>
		                	     </td>
		                	      <td width="50%">
		                	      <logic:equal name="printKqInfoForm" property="parsevo.unit" value="mm">                    
                                                 &nbsp;<html:radio name="printKqInfoForm" property="parsevo.unit" value="${printKqInfoForm.parsevo.unit}"/><bean:message key="report.parse.mm"/>
                                               </logic:equal>
                                               <logic:notEqual name="printKqInfoForm" property="parsevo.unit" value="mm">                    
                                                  &nbsp;<html:radio name="printKqInfoForm" property="parsevo.unit" value="mm"/> <bean:message key="report.parse.mm"/>
                                               </logic:notEqual>
		                	     </td>
		                	     <tr>
		                	   </table> 
		                	     
			                  </td>
		                      	</tr>	                	
		                     </table>
		                   </fieldset>
		            </td>
		          </tr>
		        </table>
		      </td>
		   </tr>		     
	          </table>
		 </td>
		</tr>	       
	        
	       <tr>
	         <td>
	           <!--表头信息-->
	           <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                     <tr>
		       <td width="100%">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.head.name"/></legend>
		                <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>
		                          <tr>
		                           <td>
		                             &nbsp;内容&nbsp;<html:text name="printKqInfoForm" property="parsevo.head_fw" size="50" maxlength="100" styleClass="text" />
		                           </td>
		                          </tr>
		                	  <tr>
		                	  <td>
                                           <table>
                                            <tr>
		                	     <td height="30">
		                	        <bean:message key="report.parse.fn"/>&nbsp;<html:select name="printKqInfoForm" property="parsevo.head_fn" size="1">
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.fz"/>&nbsp;
		                	        <html:select name="printKqInfoForm" property="parsevo.head_fz" size="1">
                                                <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                </html:select>
		                	         &nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.th"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.head_h" size="8" styleClass="text"  onkeypress="event.returnValue=IsDigit();"/> &nbsp;
                                             </td>
                                            </tr>
                                           </table>
                                          </td>
                                          </tr> 
                                          <tr>
                                           <td>
                                            <table>
                                             <tr>
                                             <td height="30">
		                	       <bean:message key="report.parse.fb"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_fb" value="#fb[1]"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.fu"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_fu" value="#fu[1]"/> &nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.fi"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_fi" value="#fi[1]"/> &nbsp;
                                             </td> 
                                             <td>
		                	       <bean:message key="report.parse.d"/>&nbsp; <html:multibox name="printKqInfoForm" property="parsevo.head_d" value="#d"/> &nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.t"/>&nbsp; <html:multibox name="printKqInfoForm" property="parsevo.head_t" value="#t"/> &nbsp;
		                	     </td> 
		                	     </tr>
		                	    </table>
                                           </td>                                           
		                	  </tr>
		                	  <tr>
                                           <td>
                                            <table>
                                            <tr>
                                             <td height="30">
		                	       <bean:message key="report.parse.p"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_p" value="#p"/>&nbsp;
		                	     </td>
		                	     <td>
		                	       <bean:message key="report.parse.c"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_c" value="#c"/> &nbsp;
		                	     </td>
                                             <td>
		                	       <bean:message key="report.parse.e"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_e" value="#e"/> &nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.u"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.head_u" value="#u"/> &nbsp;
                                             </td>
                                            </tr>
                                           </table>
                                          </td>
		                	 </tr>
		                        </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		           </td>
		        </tr>
		     </table>
	           <!--标题-->	             
	           <br>
	           <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                     <tr>
		       <td width="100%">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.title.name"/></legend>
		                <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                      <td width="100%" height="40" >
		                        <table>
		                         <tr>
		                           <td>
                                            <table>
                                             <tr>
		                               <td>
		                                 内容&nbsp;<html:text name="printKqInfoForm" property="parsevo.title_fw" size="50" maxlength="100" styleClass="text" />
		                               </td>
		                             </tr>
                                            </table>
                                           </td>
		                          </tr>
		                	  <tr>
		                	  <td>
                                           <table>
                                             <tr>
		                	      <td height="30">
		                	        <bean:message key="report.parse.fn"/>&nbsp;<html:select name="printKqInfoForm" property="parsevo.title_fn" size="1">
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	      </td>
		                	      <td>
		                	        <bean:message key="report.parse.fz"/>&nbsp;
		                	        <html:select name="printKqInfoForm" property="parsevo.title_fz" size="1">
                                                <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                </html:select>
		                	         &nbsp;
                                              </td>
                                              <td>
		                	        <bean:message key="report.parse.th"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.title_h" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/> &nbsp;
                                              </td>
                                            </tr>
                                           </table>
                                          </td>
                                          </tr> 
                                          <tr>
                                           <td>
                                            <table>
                                              <tr>
                                               <td height="30">
		                	         <bean:message key="report.parse.fb"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.title_fb" value="#fb[1]"/>&nbsp;
                                               </td>
                                               <td>
		                	         <bean:message key="report.parse.fu"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.title_fu" value="#fu[1]"/>&nbsp;
                                               </td>
                                               <td>
		                	         <bean:message key="report.parse.fi"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.title_fi" value="#fi[1]"/> &nbsp;
                                               </td>
                                              </tr>
                                             </table>
                                            </td>
		                	   </tr>
		                	 </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		           </td>
		        </tr>
		     </table>
		        <!--内容信息--->
		        <br>
		     <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                     <tr>
		       <td width="100%">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.body.name"/></legend>
		                <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>
		                	  <tr>
		                	     <td height="30">
		                	        <bean:message key="report.parse.fn"/>&nbsp;<html:select name="printKqInfoForm" property="parsevo.body_fn" size="1">
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.fz"/>&nbsp;
		                	        <html:select name="printKqInfoForm" property="parsevo.body_fz" size="1">
                                                <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                </html:select>
		                	         &nbsp;
                                             </td>                                             
                                           </tr>
                                           <tr>
                                             <td height="30">
		                	       <bean:message key="report.parse.fb"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.body_fb" value="#fb[1]"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.fu"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.body_fu" value="#fu[1]"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.fi"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.body_fi" value="#fi[1]"/> &nbsp;
                                             </td>                                            
		                	   <tr>
		                	   <tr>
                                             <td height="30" colspan="3">
                                               <table>
                                                 <tr>
                                                   <td>
                                                     <bean:message key="report.parse.body.rownum"/> &nbsp;&nbsp;
                                                   </td>
                                                   <td>
                                                     <logic:equal name="printKqInfoForm" property="parsevo.body_pr" value="#pr[0]">                    
                                                        &nbsp;<html:radio name="printKqInfoForm" property="parsevo.body_pr" value="${printKqInfoForm.parsevo.body_pr}" onclick="closes()"/>
                                                     </logic:equal>
                                                     <logic:notEqual name="printKqInfoForm" property="parsevo.body_pr" value="#pr[0]">                    
                                                        &nbsp;<html:radio name="printKqInfoForm" property="parsevo.body_pr" value="#pr[0]" onclick="closes()"/>
                                                     </logic:notEqual>
		                	             <bean:message key="report.parse.body.isAutorow"/>
                                                   </td>
                                                   <td>                                                                                                      
                                                     <logic:equal name="printKqInfoForm" property="parsevo.body_pr" value="#pr[1]">                    
                                                       &nbsp;<html:radio name="printKqInfoForm" property="parsevo.body_pr" value="${printKqInfoForm.parsevo.body_pr}" onclick="show()"/>
                                                     </logic:equal>
                                                     <logic:notEqual name="printKqInfoForm" property="parsevo.body_pr" value="#pr[1]">                    
                                                        &nbsp;<html:radio name="printKqInfoForm" property="parsevo.body_pr" value="#pr[1]" onclick="show()"/>
                                                     </logic:notEqual>
		                	             <bean:message key="report.parse.body.isUserrow"/> &nbsp;&nbsp;
                                                   </td>
                                                   <td>
                                                      <logic:equal name="printKqInfoForm" property="parsevo.body_pr" value="#pr[1]">    
                                                         <div id="b" style="display:none;" >             
                                                           &nbsp;<html:text name="printKqInfoForm" property="parsevo.body_rn" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>
                                                        </div>
                                                         <script language="javascript"> 
                                                          show();
                                                         </script> 
                                                      </logic:equal>
                                                      <logic:notEqual name="printKqInfoForm" property="parsevo.body_pr" value="#pr[1]"> 
                                                        <div id="b" style="display:none;" >
                                                            <html:text name="printKqInfoForm" property="parsevo.body_rn" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>
                                                        </div>
                                                      </logic:notEqual>
                                                      
                                                   </td>
                                                 </tr>
                                               </table>
		                	          
                                                  		                	         
		                	       
                                             </td>                                             
		                	   <tr>
		                	 </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		            </td>
		          </tr>
		        </table> 
		       <br>
		       <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                     <tr>
		       <td width="100%">
		       <!--表尾内容-->
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.teil.name"/></legend>
		                <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                     <td>
		                        &nbsp;内容&nbsp;<html:text name="printKqInfoForm" property="parsevo.tile_fw" size="50" maxlength="100" styleClass="text" />
		                     </td>
		                   </tr>
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>
		                	  <tr>
		                	    <td>
                                             <table>
                                              <tr>
		                	       <td height="30">
		                	         <bean:message key="report.parse.fn"/>&nbsp;<html:select name="printKqInfoForm" property="parsevo.tile_fn" size="1">
                                                 <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                 </html:select>&nbsp;
		                	       </td>
		                	       <td>
		                	         <bean:message key="report.parse.fz"/>&nbsp;
		                	         <html:select name="printKqInfoForm" property="parsevo.tile_fz" size="1">
                                                 <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                 </html:select>
		                	          &nbsp;
                                               </td>
                                               <td>
		                	        <bean:message key="report.parse.th"/>&nbsp;<html:text name="printKqInfoForm" property="parsevo.tile_h" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/> &nbsp;
                                               </td>
                                              </tr>
                                             </table>
                                            </td>
                                           </tr> 
                                           <tr>
                                            <td>
                                             <table>
                                             <tr>
                                              <td height="30">
		                	       <bean:message key="report.parse.fb"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_fb" value="#fb[1]"/>&nbsp;
                                              </td>
                                              <td>
		                	       <bean:message key="report.parse.fu"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_fu" value="#fu[1]"/> &nbsp;
                                              </td>
                                              <td>
		                	       <bean:message key="report.parse.fi"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_fi" value="#fi[1]"/> &nbsp;
                                              </td> 
                                              <td>
		                	       <bean:message key="report.parse.d"/>&nbsp; <html:multibox name="printKqInfoForm" property="parsevo.tile_d" value="#d"/> &nbsp;
                                              </td>
                                              <td>
		                	       <bean:message key="report.parse.t"/>&nbsp; <html:multibox name="printKqInfoForm" property="parsevo.tile_t" value="#t"/> &nbsp;
		                	      </td>
                                              </tr>
                                             </table>
                                            </td>                                             
		                	   </tr>
		                	   <tr>
                                             <td>
                                             <table><tr>
                                             <td height="30">
		                	        <bean:message key="report.parse.p"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_p" value="#p"/>&nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.c"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_c" value="#c"/> &nbsp;
		                	     </td>
                                             <td>
		                	        <bean:message key="report.parse.e"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_e" value="#e"/> &nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.u"/>&nbsp;<html:multibox name="printKqInfoForm" property="parsevo.tile_u" value="#u"/> &nbsp;
                                             </td>
                                             </tr></table>
                                             </td>
		                	   </tr>
		                	 </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		           </td>
		        </tr>
		     </table>
	         </td>
	       </tr>
	       <tr>
	         <td>
	           <br>
	          <!--   <hr>-->
	         </td>
	       </tr>
	      			     
	     </table>	    
	    </td>
         </tr>		   
       </table>		         
     </td>
    </tr>
    <tr>
    <td width="100%" height="30" align="center">
    <table width="50%">
      <tr>
        <td align="right" style="height:35px;">
           <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="oplidata();" class="mybutton">
           &nbsp;
        </td>
        <td align="left">
             &nbsp;<input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="backpage();" class="mybutton">
        </td>
      </tr>
    </table>
    </td>
    </tr>
  </table>      
                 
                 	
</html:form>