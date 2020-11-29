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
function MusterInitData()
{
   var pick_type;
   for(var i=0;i<document.dailyRegisterForm.pick_type.length;i++)
   {
		if(document.dailyRegisterForm.pick_type[i].checked)
		{
			pick_type=document.dailyRegisterForm.pick_type[i].value;
		}
   }
   if(pick_type=="1")
   {
       document.dailyRegisterForm.start_date.disabled=true;
       document.dailyRegisterForm.end_date.disabled=true;
   }else
   {
      document.dailyRegisterForm.start_date.disabled=false;
      document.dailyRegisterForm.end_date.disabled=false;
   }

}
function countdata()
{
	var pick_type;
	for(var i=0;i<document.dailyRegisterForm.pick_type.length;i++)
	{
		if(document.dailyRegisterForm.pick_type[i].checked)
		{
			pick_type=document.dailyRegisterForm.pick_type[i].value;
		}
	}
		
	if(pick_type==1||pick_type==2)
	{
	   if(pick_type==2)
	   {
	      var count_start=document.dailyRegisterForm.start_date.value;
	      var count_end=document.dailyRegisterForm.end_date.value;
	      if(!isDate(count_start,"yyyy-MM-dd"))
	      {
	          alert("请选择计算开始时间！");
              return;
	      }else if(!isDate(count_end,"yyyy-MM-dd"))
	      {
	          alert("请选择计算结束时间！");
                  return;
	      }else
	      {
	    	  var arr = count_start.split("-");
			  var starttime = new Date(arr[0], parseInt(arr[1] - 1), arr[2]);
			  var starttimes = starttime.getTime();
			  //统计的结束时间
			  var arrs = count_end.split("-");
			  var endtime = new Date(arrs[0], parseInt(arrs[1] - 1), arrs[2]);
			  var endtimes = endtime.getTime();
			  if (starttimes > endtimes) {
			      alert(KQ_DAILY_AMBIQUITY_STARTTIEM_NOTMORNTHAN_ENDTIME);
				  return;
		      }
	          var thevo=new Object();
	          thevo.pick_type=pick_type;
		 	  thevo.start=count_start;
              thevo.end=count_end;
		 	  window.returnValue=thevo;
		 	  window.close();  
	      }		
	   }	
	   else
	   {
	       var thevo=new Object();
	       thevo.pick_type=pick_type;	
	       thevo.start="";
               thevo.end="";       
	       window.returnValue=thevo;
	      window.close();
	   }
	}else
	{
	   alert("请选择机算方式！");
           return;
	}
	
}
</script>
<html:form action="/kq/register/count_register"> 
<div class="fixedDiv2" style="height: 100%;border: none">
 <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="middle" >   
                             <tr height="20">
       		                <!--  <td width=1 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter">统计范围</td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="300"></td> --> 
       		               <td  align=center class="TableRow">统计范围</td>         		           	      
                               </tr>                                         
                               <tr>
		                 <td width="100%" valign="middle" class="framestyle9" colspan="4" >
		                 <br>	              
		                   <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                     <tr>
		                       <td width="100%" valign="middle" colspan="4" >		              
		                         <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	 
		                      	<tr>
		                	  <td width="100%" height="50" >
		                	  <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<html:radio name="dailyRegisterForm" property="pick_type" value="2" onclick="MusterInitData();"/>
		                	      &nbsp;<bean:message key="kq.datewidth"/>:
		                	     </td>
		                	     <td>
		                	      <bean:message key="label.query.from"/>:
		                	      &nbsp;<input type="text" name="dailyRegisterForm" value="${dailyRegisterForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="start_date" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'> 
		                	      &nbsp;<bean:message key="label.query.to"/>:
		                	      	<input type="text" name="dailyRegisterForm" value="${dailyRegisterForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="end_date" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
		                	     </td>
		                	     <tr>
		                	   </table>  
		                	  </td>
		                	</tr>
		                	 <tr>
		                          <td width="100%" height="50" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<html:radio name="dailyRegisterForm" property="pick_type" value="1" onclick="MusterInitData();"/> 
		                	      &nbsp;<bean:message key="kq.session"/>&nbsp; 
		                	     </td>
		                	     <td>		                	    
		                	        
                                              </td>
		                	     <tr>
		                	   </table>  
		                                    
		                	  </td>
		                      	</tr>		                	
		                     </table>		                  
		             </td>
		         </tr>		         
		       </table> 
		       </td>
		       </tr>
		       </table>
		        <table align="center">
		           <tr>
		            <td  height="40" align="center" style="height:35px;">		                
	                         <input type="button" name="btnreturn" value='<bean:message key="reporttypelist.confirm"/>' onclick="countdata();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.cancel"/>' onclick="window.close();" class="mybutton">
		             </td>
		         </tr>
		       </table>  
		       </div>
</html:form>
<script language="javascript">
 MusterInitData();	
</script>