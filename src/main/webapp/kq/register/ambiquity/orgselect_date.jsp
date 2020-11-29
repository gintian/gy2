<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<html:form action="/kq/register/ambiquity/select_ambiquity">
<script language="javascript">
function statdata()
{
	var stat_type=1;
	//for(var i=0;i<document.ambiquityFrom.stat_type.length;i++)
	//{
		//if(document.ambiquityFrom.stat_type[i].checked)
		//{
		//	stat_type=document.ambiquityFrom.stat_type[i].value;
		//}
	//}
		
	if(stat_type==1||stat_type==0)
	{
	   if(stat_type==1)
	   {
	      var stat_start=document.ambiquityFrom.stat_start.value;
	      var stat_end=document.ambiquityFrom.stat_end.value;
	      if(stat_start=="")
	      {
	         alert("请选择统计开始时间！");
                 return;
	      }else if(stat_end=="")
	      {
	          alert("请选择统计结束时间！");
                  return;
	      }else
	      {
	         //ambiquityFrom.action="/kq/register/ambiquity/orgselect_ambiquity.do?b_stat=link&action=orgselect_ambiquitydata.do&target=mil_body";
		     //ambiquityFrom.target="il_body";
		     //ambiquityFrom.submit();
		     var thevo=new Object();
		     thevo.stat_start=stat_start;
		     thevo.stat_end=stat_end;
	         thevo.flag=true;
	         window.returnValue=thevo;
		     window.close();
	      }		
	   }	
	   else
	   {
		ambiquityFrom.action="/kq/register/ambiquity/orgselect_ambiquity.do?b_stat=link&action=orgselect_ambiquitydata.do&target=mil_body";
		ambiquityFrom.target="il_body";
		ambiquityFrom.submit();
	        window.close();
	   }
	}else
	{
	   alert("请选择统计方式！");
           return;
	}
	
}
</script>
	<div class="fixedDiv2" style="height: 100%;border: none">
  
 <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="middle" >   
                             <tr height="20">
       		                <!--  <td width=1 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter">统计范围</td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="300"></td>  -->   
       		               <td colspan="4"  align=center class="TableRow">统计范围</td>   		           	      
                             </tr>           
                              <tr>
		                 <td width="100%" valign="middle" class="framestyle9" >		              
		                      <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	<!--<tr>
		                          <td width="100%" height="50" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<html:radio name="ambiquityFrom" property="stat_type" value="0"/> 
		                	      &nbsp;<bean:message key="kq.session"/>：&nbsp;
		                	     </td>
		                	     <td>
		                	        <html:select name="ambiquityFrom" property="coursedate" size="0">
                                                 <html:optionsCollection property="courselist" value="dataValue" label="dataName"/>
                                                </html:select> 
                                              </td>
		                	     <tr>
		                	   </table>  
		                                    
		                	  </td>
		                      	</tr>-->
		                      	<tr>
		                	  <td width="100%" height="60" >
		                	  <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<!--<html:radio name="ambiquityFrom" property="stat_type" value="1"/>-->
		                	      &nbsp;<bean:message key="kq.datewidth"/>&nbsp;
		                	      <html:hidden name="ambiquityFrom" property="count_duration" styleClass="text"/> 
		                	     </td>
		                	     </tr>
		                	     <tr>
		                	     <td>
		                	      &nbsp;&nbsp;<bean:message key="label.query.from"/>&nbsp;
		                	      &nbsp;<input type="text" name="ambiquityFrom" value="${ambiquityFrom.stat_start}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="stat_start"  dropDown="dropDownDate">
		                	      &nbsp;<bean:message key="label.query.to"/>&nbsp;
		                	     	<input type="text" name="ambiquityFrom" value="${ambiquityFrom.stat_end}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="stat_end"  dropDown="dropDownDate">
		                	     </td>
		                	     <tr>
		                	   </table>  
		                	  </td>
		                	</tr>		                	
		                     </table>		                  
		             </td>
		            
		         </tr>
		         <tr>
		            <td  height="40" align="center">		                
	                         <input type="button" name="btnreturn" value='确定' onclick="statdata();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.cancel"/>' onclick="window.close();" class="mybutton">
		             </td>
		         </tr>
		       </table>  
		      </div>              
</html:form>