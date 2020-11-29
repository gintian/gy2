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
  		returnValue="1";
	    window.close();
  	}
  
  </script>
   
<hrms:themes />
  <body>
  <form name='form1'>
    <table>
    <tr><td colspan='3'>
    <script language='javascript' >
    	document.write(info[1]);
    </script>
    </td></tr>
    <tr>
    <td> 
    	<TEXTAREA name='cause' rows="18" cols="60"> </TEXTAREA>
    </td>
    </tr>
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
