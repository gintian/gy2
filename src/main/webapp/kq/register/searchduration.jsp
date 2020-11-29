<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
    .option-hint {
        margin-left: 10px;
        color: #d36969;
    }
</style>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript">
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
function MusterInitData()
{
   var creat_type;
   for(var i=0;i<document.dailyRegisterForm.creat_type.length;i++)
   {
		if(document.dailyRegisterForm.creat_type[i].checked)
		{
			creat_type=document.dailyRegisterForm.creat_type[i].value;
		}
   }
   if(creat_type=="1")
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
	var creat_type;
	for(var i=0;i<document.dailyRegisterForm.creat_type.length;i++)
	{
		if(document.dailyRegisterForm.creat_type[i].checked)
		{
			creat_type=document.dailyRegisterForm.creat_type[i].value;
		}
	}
        var creat_pick=document.getElementById("creat_pick"); 
        var status_value="";      
        if(creat_pick.checked==true)
        {
          status_value="1";
          if(!confirm("您确认将申请单明细数据统计到考勤日明细中?"))
          {
            return false;
          }
        }else
        {
          status_value="0";
          if(!confirm("您确认不将申请单明细数据统计到考勤日明细中?"))
          {
            return false;
          }
        }
    var creat_state_ob=document.getElementById("creat_state");
    var creat_state="";
    if(creat_state_ob.checked==true)
    {
          creat_state="1";
          if(!confirm("您确认将已上报人员考勤数据重新生成?\n\r本考勤期间原有数据将被删除!"))
          {
            return false;
          }
        }else
        {
          creat_state="0";
          if(!confirm("您确认生成日明细数据时不重新生成已上报人员数据?"))
          {
            return false;
          }
        }
	if(creat_type==1||creat_type==2)
	{
	   if(creat_type==2)
	   {
	      var count_start=document.dailyRegisterForm.start_date.value;
	      var count_end=document.dailyRegisterForm.end_date.value;
	      if(!isDate(count_start,"yyyy-MM-dd"))
	      {
	         alert("申请日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                 return;
	      }else if(!isDate(count_end,"yyyy-MM-dd"))
	      {
	          alert("时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                  return;
	      }else
	      {
	         var thevo=new Object();
	         thevo.creat_type=creat_type;
		     thevo.start=count_start;
             thevo.end=count_end;
             thevo.creat_pick=status_value;
             thevo.creat_state=creat_state;
		     window.returnValue=thevo;
		     window.close();  
	      }		
	   }	
	   else
	   {
	       var thevo=new Object();
	       thevo.creat_type=creat_type;	
	       thevo.start="";
           thevo.end="";    
           thevo.creat_pick=status_value;   
           thevo.creat_state=creat_state;
	       window.returnValue=thevo;
	       window.close();
	   }	
	}else
	{
	   alert("请选择计算方式！");
           return;
	}
	
}
</script>
<html:form action="/kq/register/daily_registerdata">
	<div class="fixedDiv2" style="height: 100%;border: none">
<table align="center" width="100%" height="300" valign="top">
  <tr>
     <td valign='top'>
      <table border="0" width="100%" cellspacing="0" cellpadding="4" style="border-collapse: collapse;border-style: solid;border-width: 1px;border-color: #C4D8EE" height="87" class="common_border_color"  align="center">
        <tr>
           <td class="TableRow" height=24 align="center"><bean:message key="kq.register.creat1.success"/>
            </td>             
        </tr>
        <tr>
         <td>
           <table>
            <tr>
             <td>
             <table>
	        <tr>
		  <td>
                  &nbsp;<html:radio name="dailyRegisterForm" property="creat_type" value="1" onclick="MusterInitData();"/> 
                  </td>
                   <td>
                     &nbsp;<bean:message key="kq.register.kqduration"/>:&nbsp;&nbsp;
                     <font face='宋体' style='color: #0000FF;' > 
                     <bean:write name="dailyRegisterForm" property="kq_duration" />
                    </font>
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
                  &nbsp;<html:radio name="dailyRegisterForm" property="creat_type" value="2" onclick="MusterInitData();"/>
		         <bean:message key="kq.datewidth"/>:
		      </td>
		      <td>
		         <bean:message key="label.query.from"/>:
		         <input type="text" name="dailyRegisterForm" value="${dailyRegisterForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="start_date" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
		         <bean:message key="label.query.to"/>:
		         <input type="text" name="dailyRegisterForm" value="${dailyRegisterForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="end_date" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>                             
		      </td>
		      <tr>
		  </table>  
		</td>
	     </tr>
            </table>
          </td>  
        </tr>
        <tr>
          <td>
           <table cellspacing="2" cellpadding="4">
	     <tr>
              <td height="30">
                &nbsp;<html:checkbox name="dailyRegisterForm" property="creat_pick" styleId='creat_pick' value="1"/>
                &nbsp;将申请单明细数据统计到考勤日明细<br>
                  <span class="option-hint">注意：没有非机器考勤人员时，请勿选，否则，可能会影响生成速度！</span>
              </td>
	      <tr>
	      <tr>
              <td height="30">
                &nbsp;<html:checkbox name="dailyRegisterForm" property="creat_state" styleId='creat_state' value="1"/>
                &nbsp;将已上报人员考勤数据重新生成<br>
                  <span class="option-hint">注意：如果选定，那么本考勤期间原有数据将被删除！</span>
              </td>
	      <tr>
	    </table>   
          </td>
        </tr>
        	
      </table>
      <table align="center">
      <tr>
          <td align="center" style="height:35px;">
             <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="countdata();" class="mybutton">
	     &nbsp;&nbsp;<input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.cancel"/>' onclick="window.close();" class="mybutton"> 
          </td>
        </tr> 
        </table>
    </td> 
        
  </tr>
   
</table>
</div>
</html:form>