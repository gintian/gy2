<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>

  <script language='javascript'>
  	//var info=dialogArguments;

  	function enter()
  	{
  		var thevo=new Object();
		thevo.ok=1;
		if (window.showModalDialog){
            parent.window.returnValue=thevo;
        }else{
		    if (parent.opener.batchExportReport_ok)
                parent.opener.batchExportReport_ok(thevo);
        }
	    parent.window.close();
  	}
  
  </script>
   
<hrms:themes />
  <body>
  <form name='form1'>
    <table>
    <tr><td>
		<bean:message key="jx.Report.empList"/>
    </td></tr>
    <tr>
    <td> 
    	<html:textarea name="evaluationForm" property="jxReportInfo" style="width:500px;height:400px;"/>
    </td>
    </tr>
    <tr>

    <td align='center' >
    
     <input type='button' class="mybutton"   onclick='enter(1)' value='<bean:message key="lable.tz_template.enter"/>'  />
 	 &nbsp;
 	 <input type='button' class="mybutton"   onclick='parent.window.close();' value='<bean:message key="lable.tz_template.cancel"/>'  />
    </td>
    </tr>
    
    </table>
  </form>
  
  
  </body>
</html>
