<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript">
function statdata()
{
	var stat_type=1;

	var kq_end = document.ambiquityFrom.kq_end.value;

	if(stat_type==1||stat_type==0)
	{
	   if(stat_type==1)
	   {
	      var stat_start=document.ambiquityFrom.stat_start.value;
	      var stat_end=document.ambiquityFrom.stat_end.value;

	      stat_start = replaceAll(stat_start, '-', '.');
	      stat_end = replaceAll(stat_end, '-', '.');
	      
	      //统计的开始时间
	      var arr = stat_start.split(".");
	      var starttime = new Date(arr[0], parseInt(arr[1]-1), arr[2]);
	      var starttimes = starttime.getTime();
	      //统计的结束时间
	      var arrs = stat_end.split(".");
	      var endtime = new Date(arrs[0], parseInt(arrs[1]-1), arrs[2]);
	      var endtimes = endtime.getTime();
		  //考勤期间的结束时间
	      var arrss = kq_end.split(".");
	      var kqendtime = new Date(arrss[0], parseInt(arrss[1]-1), arrss[2]);
	      var kqendtimes = kqendtime.getTime();

	      if (endtimes > kqendtimes) {
		      alert(KQ_DAILY_AMBIQUITY_NOTMORNTHAN_KQENDTIME);
		          return;
		  }

	      if (starttimes > endtimes) {
		      alert(KQ_DAILY_AMBIQUITY_STARTTIEM_NOTMORNTHAN_ENDTIME);
		          return;
		  }
	  	      
	      if(!isDate(stat_start,"yyyy.MM.dd"))
	      {
	         alert(KQ_DAILY_AMBIQUITY_SELECT_START);
                 return;
	      }else if(!isDate(stat_end,"yyyy.MM.dd"))
	      {
	          alert(KQ_DAILY_AMBIQUITY_SELECT_END);
                  return;
	      }else
	      {
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
	      ambiquityFrom.action="/kq/register/ambiquity/select_ambiquity.do?b_stat=link&action=select_ambiquitydata.do&target=mil_body&stat_start="+stat_start+"&stat_end="+stat_end;
	      ambiquityFrom.target="il_body";
	      ambiquityFrom.submit();
	      var thevo=new Object();
          thevo.flag=true;
	      window.returnValue=thevo;
	      window.close();
	   }
	}else
	{
	   alert("请选择统计方式！");
           return;
	}
	
}
</script>
<html:form action="/kq/register/ambiquity/select_ambiquity">
 	<div class="fixedDiv2" style="height: 100%;border: none">
 <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="middle" >   
                             <tr height="20">
       		                <!--  <td width=1 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter">统计范围</td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="300"></td> --> 
       		                <td colspan="4" align=center class="TableRow">统计范围</td>      		           	      
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
		                	  <td width="100%" height="60" align="center">
		                	       <html:hidden name="ambiquityFrom" property="count_duration" styleClass="text"/> 
		                	       <html:hidden name="ambiquityFrom" property="kq_end" />
		                	      &nbsp;&nbsp;<bean:message key="label.query.from"/>
		                	      &nbsp;<input type="text" name="ambiquityFrom" value="${ambiquityFrom.stat_start}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="stat_start" 
		                	               onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'
		                	               onchange="rep_dateValue(this);" Class="TEXT4">
		                	      &nbsp;<bean:message key="label.query.to"/>
		                	     	<input type="text" name="ambiquityFrom" value="${ambiquityFrom.stat_end}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="stat_end" 
		                	     	   onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'
		                	     	    onchange="rep_dateValue(this);" Class="TEXT4">
		                	  </td>
		                	</tr>		                	
		                     </table>		                  
		             </td>
		            
		         </tr>
		         <tr>
		            <td  height="40" align="center" style="height:35px;">		                
	                         <input type="button" name="btnreturn" value='<bean:message key="reporttypelist.confirm"/>' onclick="statdata();" class="mybutton">
		                &nbsp;&nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">
		             </td>
		         </tr>
		       </table>      
		       </div>          
</html:form>