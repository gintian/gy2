<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript">
	function saveHour()
	{
		var theHour = $F('studyHour');
		if(theHour=='')
		{
			alert("<bean:message key='train.b_plan.request.daytime'/>");
			return;
		}
		var tr = 0;
		var theHour1 = theHour.split(".");
		var th = theHour1[0];
		var th1 = theHour1[1];
		if((parseInt(th)==0&&parseInt(th1)>0)||(parseInt(th)>0&&parseInt(th)<24)||(parseInt(th)==24&&parseInt(th1)==0))
			tr = 1;
		if(tr == 1 && isNumber($('studyHour')))
		{
			var thevo=new Object();
			thevo.flag="true";
			thevo.theHour=theHour;
			window.returnValue=thevo;
			window.close();
		}else {
			alert(INPUT_HOURNUMBER_VALUE+"!");
		}
	}
  function mincrease1()
  {
  		var obj =$('studyHour');
  		if(obj==null)
  		  return false;
  		if(parseInt(obj.value)>0&&parseInt(obj.value)<24){
		obj.value = (parseInt(obj.value)+1);
		}
  }
  function msubtract1()
  {
  		var obj =$('studyHour');
  		if(obj==null)
  		  return false;
  		if(parseInt(obj.value)>0&&parseInt(obj.value)<25){
		obj.value = (parseInt(obj.value)-1);
		}	
		if(obj.value=='0')
		obj.value='1';
  }
  //输入整数
  function IsDigit(obj) 
  {
	if((event.keyCode >46) && (event.keyCode <= 57))
		return true;
	else
	   return false;	
  }
  function isNumber(obj)
{
  		var checkOK = "0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		alert(INPUT_NANUMBER_VALUE+'!');
  		obj.value='';  
  		obj.focus();
  		return false;
  	}  	 
  	return true;  
}
</script>
<html:form action="/train/request/trainsData">
<div class="fixedDiv3">
	<table width="100%" height="100%" border="0" cellspacing="0" 	cellpadding="0" align="center">

		<tr>
			<td align="center" valign="bottom">			  
				<fieldset style="width:100%">
					<legend>
						<bean:message key="label.sys.warn.freq.everyday" />
					</legend>
					
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td height='10' colspan='3'></td>
						</tr>
						<tr>
							<td align='right'>
								<html:text name="courseTrainForm" property="studyHour" styleId="studyHour" styleClass="textColorWrite" onkeypress="event.returnValue=isNumber(this);" style="width:60px"/>
						    </td>
						    <!--
							<td>
								<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease1()">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract1()">6</button></td></tr>
								</table>
							</td>
							  -->
							<td align='right'>
								&nbsp;&nbsp;<bean:message key="train.job.time" />	
							</td>
						</tr>	
							<tr>
							<td height='10' colspan='3'>&nbsp;</td>
						</tr>				
					</table>					
					
                </fieldset>
			</td>
		</tr>
		<tr>
			<td align="center" style="padding-top: 5px;">
				<input type ='button' value="<bean:message key="button.ok"/>" class="mybutton" onclick='saveHour()'>
				<input type ='button' value="<bean:message key="button.cancel"/>" class="mybutton" onclick='window.close();'>
			</td>
		</tr>
	</table>
	</div>
</html:form>