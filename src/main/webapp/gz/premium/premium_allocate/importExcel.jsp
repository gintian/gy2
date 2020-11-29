<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    
  </head>
  <script language='javascript' >
  	if("${param.oper}"=='close')
  	{
  		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
		window.close();
  	}
    
    function imports()
    {
    	 var fileEx = monthPremiumForm.file.value;
        if(fileEx == ""){
        	alert("<bean:message key='jx.import.select'/>！");
        	return ;
        }
        if(!validateUploadFilePath(fileEx))
           return;
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='xls' || temp.toLowerCase()=='xlsx')
    	{
   			document.monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_importData=link&oper=close";
  			document.monthPremiumForm.submit();
    	}
    	else
    	{
    		alert("<bean:message key='jx.import.error'/>！");
    	}
    }

  </script>
  <body>
  <form name="monthPremiumForm" method="post" action="/gz/premium/premium_allocate/monthPremiumList.do" enctype="multipart/form-data" >
    <br>
	<br>	
    <fieldset align="center" style="width:90%;">
        <legend ><bean:message key='jx.import.selectfile'/></legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td> <Br><bean:message key='jx.import.file'/>
              <input type="file" name="file" size="30"  >
              <br>              
              </td>
          </tr>	  
        </table>
	</fieldset>
	<table width="90%" align="center">
		     <tr > 
            <td> </td>
          </tr>	
		  <tr> 
            <td align="center"> <input type="button" name="b_update" value='<bean:message key="button.import"/>' class="mybutton" onClick="imports()"> 
		
               </td>
          </tr>
	
	</table>
  </form>
  </body>
</html>
