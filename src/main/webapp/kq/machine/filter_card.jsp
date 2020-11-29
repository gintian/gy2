<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<style type="text/css">
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #FFFFFF;
	border-bottom: 1px inset #FFFFFF;
	width: 50px;
	height: 22px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 12px;
}
.m_input {
	width: 18px;
	height: 14px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
	BACKGROUND-COLOR: #F7FAFF;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
</style>
<script language="javascript">

  this.fObj=null;
  function setFocusObj(obj) 
  {
	this.fObj = obj;
  }
  function IsDigit() 
  { 
           return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
  function IsInputValue() 
  {	     
	event.cancelBubble = true;
	if (!fObj) return;		
	
	var cmd = event.srcElement.innerText=="5"?true:false;
	var i = parseInt(fObj.value,10);
	var radix = parseInt(this.fObj.radix,10)-1;		
	if (i==radix&&cmd) {
			i = 0;
	} else if (i==0&&!cmd) {
			i = radix;
	} else {
			cmd?i++:i--;
	}		
	fObj.value = formatTime(i);
	fObj.select();
} 
function formatTime (sTime)
 {
		sTime = ("0"+sTime);
		return sTime.substr(sTime.length-2);
 }
 function take_filt()
 {
 	var count_start=document.kqCardDataForm.start_date.value;
	var count_end=document.kqCardDataForm.end_date.value;
	if(!isDate(count_start,"yyyy.MM.dd"))
	{
	    alert("请选择计算开始时间！");
        return;
	}

	if(!isDate(count_end,"yyyy.MM.dd"))
	{
	    alert("请选择计算结束时间！");
        return;
    }
    
   	var obj = new Object();
   	obj.count_start = count_start;
   	obj.count_end = count_end;
	obj.filter_hh_s = document.getElementById("filter_hh_s").value;
   	obj.filter_hh_e = document.getElementById("filter_hh_e").value;
   	obj.filter_mm_s = document.getElementById("filter_mm_s").value;
   	obj.filter_mm_e = document.getElementById("filter_mm_e").value;
   	obj.filter_card = document.getElementById("filter_card").value;
   	window.returnValue = obj;
    window.close();      	
 }
</script>
<html:form action="/kq/machine/search_card_data">
<div  class="fixedDiv2" style="height: 100%;border: none">
 <table  width="100%"  border="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">   
    <tr height="20">
         <!--  <td width=10 valign="top" class="tableft1"></td>
        <td width=130 align=center class="tabcenter"><bean:message key="kq.card.filtrate"/></td>   
        <td width=10 valign="top" class="tabright"></td>
        <td valign="top" class="tabremain" width="300"></td>-->
        <td align=center class="TableRow"><bean:message key="kq.card.filtrate"/></td>   
    </tr>                                         
    <tr>                               	
	    <td width="100%" height="130"   class="framestyle9" valign="top" >
	    <br>
	          <table width="100%">
		  	   <tr>
		  	     <td height="30" width="30%" align="right">
		  	        &nbsp;<bean:message key="kq.card.filtrate.start"/>&nbsp;		                	     
		  	     </td>
		  	     <td>		                	      
		  	        <table border="0" cellspacing="0" cellpadding="0">
		  	        <tr>
		  	          <td>
		  	            &nbsp;
		  	            <input type="text" name="kqCardDataForm" value="${kqCardDataForm.filter_date_s}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="start_date" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' extra="editor" dataType="simpledate">                              
		  	          </td>	
		  	          <td>
		  	            &nbsp;
		  	          </td>	                	          
		  	          <td>
		  	            <table border="0" cellspacing="0" cellpadding="0">
		  	             <tr>
		  	              <td>
		  	                <div class="m_frameborder inputtext" >
		  	                  <input type="text" radix="24" class="m_input" id="filter_hh_s" size="2" value="${kqCardDataForm.filter_hh_s}" onkeypress="event.returnValue=IsDigit();" onfocus="setFocusObj(this);">:
		  	                  <input type="text" radix="60" class="m_input" id="filter_mm_s" size="2" value="${kqCardDataForm.filter_mm_s}" onkeypress="event.returnValue=IsDigit();" onfocus="setFocusObj(this);"> 
		  	                </div>		                	                
		  	              </td>
		  	              <td>
		  	               <table border="0" cellspacing="2" cellpadding="0">
                             <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue();">5</button></td></tr>
                             <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue();">6</button></td></tr>
                           </table>
		  	              </td>
		  	            </tr>		                	            
		  	           </table>	
		  	          </td>
		  	        </tr>
		  	      </table>
		  	     </td>
		  	     </tr>
		  	      <tr>
		  	     <td height="30" align="right">
		  	      &nbsp;<bean:message key="kq.card.filtrate.end"/>&nbsp;		                	     
		  	     </td>
		  	     <td>	
		  	      <table border="0" cellspacing="0" cellpadding="0">
		  	        <tr>
		  	          <td>
		  	            &nbsp;
		  	            <input type="text" name="kqCardDataForm" value="${kqCardDataForm.filter_date_e}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="end_date" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' extra="editor" dataType="simpledate">                             
		  	          </td>	
		  	          <td>
		  	            &nbsp;
		  	          </td>	                	          
		  	          <td>
		  	            <table border="0" cellspacing="0" cellpadding="0">
		  	              <tr>
			  	              <td>
			  	                <div class="m_frameborder inputtext" >
			  	                  <input type="text" radix="24" class="m_input" id="filter_hh_e" size="2" value="${kqCardDataForm.filter_hh_e}" onkeypress="event.returnValue=IsDigit();" onfocus="setFocusObj(this);">:
			  	                  <input type="text" radix="60" class="m_input" id="filter_mm_e" size="2" value="${kqCardDataForm.filter_mm_e}" onkeypress="event.returnValue=IsDigit();" onfocus="setFocusObj(this);"> 
			  	                </div>		                	                
			  	              </td>
			  	              <td>
			  	               <table border="0" cellspacing="2" cellpadding="0">
	                               <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue();">5</button></td></tr>
	                               <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue();">6</button></td></tr>
			                    </table>
			  	              </td>
		  	              </tr>		                	            
		  	            </table>	
		  	          </td>
		  	        </tr>
		  	      </table>
		  	     </td>		                	     
		  	     </tr>
		  	     <tr>
		  	       <td height="30" align="right">
		  	         &nbsp;<bean:message key="kq.card.filtrate.cardno"/>&nbsp;		                	     
		  	       </td>
		  	       <td>&nbsp;
		  	         <html:text name="kqCardDataForm" property='filter_card' styleId="filter_card" size="15" styleClass="text4"/> 
		       	   </td>
		       	 </tr>
	       	   </table>  
	    </td>
    </tr>
    <tr>
       <td align="center" height="40">		                
           <input type="button" name="btnreturn" value='<bean:message key="kq.formula.true"/>' onclick="take_filt();" class="mybutton">
           <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">
      </td>
    </tr>
</table>    
</div>            
</html:form>
<script language="javascript">
window.focus();
</script>