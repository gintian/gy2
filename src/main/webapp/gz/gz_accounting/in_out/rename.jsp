<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>
  <title><bean:message key="jx.eval.notCalculate"/></title>
   
  </head>
  <script language='javascript'>
  	var info=dialogArguments;
  
  	function enter()
  	{
  		returnValue=document.form1.cause.value;
	    window.close();
  	}
  
  </script>
   
  <link href="/css/css1.css" rel="stylesheet" type="text/css">
  <hrms:themes />
  <body>
  <form name='form1'>
  		<table border="0" cellspacing="0" align="center" cellpadding="2">
			<tr>
						<td height='10' nowrap>
							&nbsp;
						</td>
			</tr>
			<tr>
						<td align="center" nowrap>
							<fieldset align="center" style="width:240;">
							<legend>
    <script language='javascript' >
    	document.write(info[1]);
    </script>
    	</legend>
    	  		<table border="0" cellspacing="0" align="center" cellpadding="2">
    				<tr>
						<td height='10' nowrap>
							 	<input type='text' name='cause' size='30'>
						</td>
					</tr>    	    
   				 </table>
   				 </fieldset>
    </td></tr>
  
    <tr>
    <td align='center' >
    
     <input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="lable.tz_template.enter"/>'  />
 	 &nbsp;
 	 <input type='button' class="mybutton"   onclick='window.close()' value='<bean:message key="lable.tz_template.cancel"/>'  />
    </td>
    </tr>
    
    </table>
  </form>
  
  
  <script language='javascript' >
  		document.form1.cause.value=info[0];
  </script>
  
  </body>
</html>
