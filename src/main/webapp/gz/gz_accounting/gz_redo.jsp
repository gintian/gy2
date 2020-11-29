
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,java.util.*,com.hrms.hjsj.sys.VersionControl"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
 <%
 	AcountingForm accountingForm=(AcountingForm)session.getAttribute("accountingForm"); 
  %>
<script type="text/javascript">
<!--

function searchdata(salaryid) 
{
    var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("bosdate",document.accountingForm.bosdate.value);	    
   	var request=new Request({asynchronous:false,onSuccess:change_bosdate_ok,functionId:'3020070117'},hashvo);	 
}

function change_bosdate_ok(outparameters)
{
 		  var countlist = outparameters.getValue("countlist");
		  AjaxBind.bind(accountingForm.count,countlist); 
}


//-->
</script> 

<html:form action="/gz/gz_accounting/gz_table"> 
<br>
<br>
<br> 
<div id='wait' style='position:absolute;top:40;left:40;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"  class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="label.gz.locate"/>
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
<%if(accountingForm.getDatelist().size()>0){ %>
  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.defineRedodate"/></legend>
	<table border="0" cellspacing="0" align="left" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
	<tr>
	<td >
		<table border="0" cellspacing="0" cellpadding="0">
		<tr >
		<td >
			<table width="90%" border="0" cellspacing="0" cellpadding="0">
	         <tr> 
	          <td align="right" nowrap >&nbsp;&nbsp;<bean:message key="label.gz.appdate" /></td>                                 
	          <td valign="middle">&nbsp; 
	          	<html:select name="accountingForm" property="bosdate" size="1" onchange="searchdata('${accountingForm.salaryid}');">
					<html:optionsCollection property="datelist" value="dataValue" label="dataName" />
				</html:select> 
	          </td>
			  <td  nowrap  >&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.gz.count" /></td>	          
	   			<td valign="middle"> &nbsp; 
	   			<html:select name="accountingForm" property="count" size="1" >
					<html:optionsCollection property="countlist" value="dataValue" label="dataName" />
				</html:select>
	       	  	</td>	           
	          </tr>
	          <tr>
	          	<td colspan='4' >&nbsp;</td>
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
				<button name="new" Class="mybutton" onclick="submit_app_date('${accountingForm.salaryid}');" ><bean:message key="button.ok"/></button>&nbsp;&nbsp;&nbsp;&nbsp;
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
<%}else{ %>
  <table width="400" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align="left" class="TableRow">&nbsp;信息提示&nbsp;</td>
          </tr> 
                    <tr >
              	      <td align="left" valign="middle" nowrap style="height:100">您还没有提交过薪资，不需要重发！</td>
                    </tr> 
 
                    <tr >
                      <td align="center" style="height:35">
                      
              				<input type="button" name="btnreturn" value="返回" onclick="window.close();" class="mybutton">
                      
                      </td>
                    </tr>   
          
  </table> 

<%} %>
</html:form>


  