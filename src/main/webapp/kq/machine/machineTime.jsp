<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>				
<%
	String url_p=SystemConfig.getCsClientServerURL(request); 
%>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
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
	width: 12px;
	height: 14px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
	/*BACKGROUND-COLOR: #F7FAFF;*/
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
 function take_time()
  {
    var num=$F('machine_num');  
    if(num==""||num==null)
    {
       alert("请选择考勤机！");
       return false;
    }
    var hashvo=new ParameterSet(); 
    hashvo.setValue("machine_num",num);
    var request=new Request({method:'post',onSuccess:showSelect,functionId:'15211001110'},hashvo);    
      
  }
  function showSelect(outparamters)
  {
      var machine_no=outparamters.getValue("machine_no");   
      var baud_rate=outparamters.getValue("baud_rate");  
      var port=outparamters.getValue("port");   
      var type_id=outparamters.getValue("type_id");  
      var ip_address=outparamters.getValue("ip_address");   
      var cardno_len=outparamters.getValue("cardno_len"); 
      getTimeOBj(type_id,machine_no,port,baud_rate,ip_address,cardno_len);  
  }
  function getTimeOBj(type_id,machine_no,port,baud_rate,ip_address,cardno_len)
  {
  	if(!AxManager.setup(null, "KqMachine", 0, 0, null, AxManager.kqmachPkgName, null, false, "<%=url_p%>"))
  		return;  	
    var obj=document.getElementById('KqMachine'); 
    //alert(type_id+"-"+machine_no+"-"+port+"-"+baud_rate);  
    obj.SetParam(type_id,machine_no,port,baud_rate,ip_address,cardno_len);
    var time_v=obj.GetTime(); 
    if(time_v.length>=12)
    {
      var fObj=document.getElementById("date");
      var year=time_v.substring(0,4);
      var MM=time_v.substring(4,6);
      var dd=time_v.substring(6,8);
      fObj.value=year+"-"+MM+"-"+dd;
      fObj=document.getElementById("hh");
      fObj.value=time_v.substring(8,10);
      fObj=document.getElementById("mm");
      fObj.value=time_v.substring(10,12);
    }
    else
      alert("获取考勤机时间失败！");
  }
  function write_time()
  {
    var num=$F('machine_num');  
    if(num==""||num==null)
    {
       alert("请选择考勤机！");
       return false;
    }
    var hashvo=new ParameterSet(); 
    hashvo.setValue("machine_num",num);
    var request=new Request({method:'post',onSuccess:setSelect,functionId:'15211001110'},hashvo);    
  }
  function setSelect(outparamters)
  {
      var machine_no=outparamters.getValue("machine_no");   
      var baud_rate=outparamters.getValue("baud_rate");  
      var port=outparamters.getValue("port");
      var type_id=outparamters.getValue("type_id");   
      var ip_address=outparamters.getValue("ip_address");   
      var cardno_len=outparamters.getValue("cardno_len");   
      setTimeOBj(type_id,machine_no,port,baud_rate,ip_address,cardno_len);  
  }
  function setTimeOBj(type_id,machine_no,port,baud_rate,ip_address,cardno_len)
  {
  	 if(!AxManager.setup(null, "KqMachine", 0, 0, null, AxManager.kqmachPkgName, null, false, "<%=url_p%>"))
  		return;  	
     var obj=document.getElementById('KqMachine');   
     obj.SetParam(type_id,machine_no,port,baud_rate,ip_address,cardno_len);
     var isCorrect=obj.SetTime();
     if(isCorrect)
     {
        var objDate = new Date();
        var datestr=objDate.getYear()+"-"+(objDate.getMonth()+1)+"-"+objDate.getDate();
        var date_obj= document.getElementById('machine_date');
        date_obj.value=datestr;
        var fObj=document.getElementById("hh");
        fObj.value=objDate.getHours();
        fObj=document.getElementById("mm");
        fObj.value=objDate.getMinutes();
        alert("写时间成功！");
     }else
     {
       alert("写时间失败！");
     }
  }
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
</script>
<html:form action="/kq/machine/search_card_data">
 <div class="fixedDiv3">
 <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="top" >   
                             <tr height="20">
       		                <!--  <td width=10 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter"><bean:message key="kq.machine.set.time"/></td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="300"></td> -->  
       		                <td align=center class="TableRow"><bean:message key="kq.machine.set.time"/></td>        		           	      
                               </tr>                                         
                               	<tr>                               	
		                  <td width="100%" height="120"  class="framestyle9" valign="top">
		                  <br>
		                    <fieldset align="center" style="width:90%;">
    	                             <legend ><bean:message key="time.revise"/></legend>
		                        <table width="100%">
		                	   <tr>
		                	     <td height="30" width="30%">
		                	      &nbsp;<bean:message key="kq.machine.name"/>&nbsp;		                	     
		                	     </td>
		                	     <td>		                	      
		                	      &nbsp;
		                	      <hrms:optioncollection name="kqCardDataForm" property="machinelist" collection="list" />
	                                      <html:select name="kqCardDataForm" property="machine_num" size="1">
                                              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                              </html:select>
		                	     </td>
		                	     <tr>
		                	      <tr>
		                	     <td height="30">
		                	      &nbsp;<bean:message key="kq.machine.time"/>&nbsp;		                	     
		                	     </td>
		                	     <td>	
		                	      <table border="0" cellspacing="0" cellpadding="0">
		                	        <tr>
		                	          <td>
		                	            &nbsp;
		                	            <html:text name="kqCardDataForm" property='machine_date' size="10" styleId='date' styleClass="text4" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' readonly="true"/>                             
		                	          </td>	
		                	          <td>
		                	            &nbsp;
		                	          </td>	                	          
		                	          <td>
		                	            <table border="0" cellspacing="0" cellpadding="0">
		                	             <tr>
		                	              <td>
		                	                <div class="m_frameborder inputtext" >
		                	                  <input type="text" radix="24" class="m_input" name="machine_hh" id="hh" maxlength="2" size="2" value="${kqCardDataForm.machine_hh}" onkeypress="event.returnValue=IsDigit();" onfocus="setFocusObj(this);">:
		                	                  <input type="text" radix="60" class="m_input" name="machine_mm" id="mm" maxlength="2" size="2" value="${kqCardDataForm.machine_mm}" onkeypress="event.returnValue=IsDigit();" onfocus="setFocusObj(this);"> 
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
		                	     
		                	     <tr>
		                	   </table>  
		                	  </fieldset>
		                	  <br>		                	 		                  
		             </td>
		         </tr>
		         <tr>
		            <td align="center" height="40">		                
	                         <input type="button" name="btnreturn" value='<bean:message key="kq.machine.read.time"/>' onclick="take_time();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="kq.machine.write.time"/>' onclick="write_time();" class="mybutton">
		             </td>
		         </tr>
		       </table>  
		       </div>              
</html:form>
