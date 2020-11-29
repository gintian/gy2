
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<hrms:themes />
<script language="javascript">
var info=dialogArguments; 
function isDigit(s)   
{   
		var patrn=/^[0-9]{1,20}$/;   
		if (!patrn.exec(s)) return false  
			return true  
}  

function add(they,themon,salaryid,gz_module)
{
	var year=they.value;
	var month=themon.value;
	
	if(!isDigit(year)||!isDigit(month))
	{
		alert("日期格式不正确！");
		return;
	}
	if(year*1<=1900||year*1>=2100)
	{
		alert("年要大于1900,小于2100!");
		return;
	}
	if(month*1<1||month*1>12)
	{
		alert("月要大于等于1,小于等于12!");
		return;
	}
	
	var waitInfo=eval("wait");
	waitInfo.style.display="block";
				
	var hashvo=new ParameterSet();
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);	
	hashvo.setValue("fieldsetid",info[0]);	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:addOk,functionId:'3020073062'},hashvo);
	
}
function addOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
    parent.window.returnValue=1;
    parent.window.close();
}
</script>
<html:form action="/performance/data_collect/data_collect"> 
<br>
<br>
<br>

	<div id='wait' style='position:absolute;top:40;left:50;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
						<bean:message key="classdata.isnow.wiat"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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
<table align="center"  width="80%">
<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.defineAppdate"/></legend>
	<table border="0" cellspacing="0" align="left" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
	<tr>
	<td >
		<table border="0" cellspacing="0" cellpadding="0">
		<tr >
		<td >
			<table width="90%" border="0" cellspacing="0" cellpadding="0">
	         <tr> 
	          <td align="right"></td>                                 
	          <td valign="middle">&nbsp;&nbsp;
	          	<html:text name="data_collectForm"  property="theyear" style="width:40"/>                     
	          <td valign="middle">                      
	          </td>
	          <td valign="middle" align="left">
	             <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='inc_year($("theyear"));'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='dec_year($("theyear"));'>6</button></td></tr>
	             </table>
	          </td>
			  <td><bean:message key="hmuster.label.year"/></td>	          
	   				  <td valign="middle"> 
	       	  			<html:text name="data_collectForm" property="themonth" style="width:40"/>                     
	         		 </td>
	          		 <td valign="middle" align="left">
	             		<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='inc_month($("themonth"));'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='dec_month($("themonth"));'>6</button></td></tr>
	             		</table>
	                 </td>	
	                 <td><bean:message key="hmuster.label.month"/></td>	          
	          <td align="left"></td>
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
	<tr>
	<td>
		<table align="center">
    		<tr >
		  	  <td>
				<button name="new" Class="mybutton" onclick='add($("theyear"),$("themonth"));'><bean:message key="button.ok"/></button>&nbsp;&nbsp;&nbsp;&nbsp;
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>

</html:form>


  