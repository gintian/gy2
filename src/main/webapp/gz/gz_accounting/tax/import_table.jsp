<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    
  </head>
  <script language='javascript' >
	function goback()
	{
		document.taxTableForm.target="il_body";
	    document.taxTableForm.action="/gz/gz_accounting/tax/gz_tax_org_tree.do?br_link=link";
		document.taxTableForm.submit();
	}
	
	
	
    
    function imports()
    {
    	 var fileEx = taxTableForm.importfile.value;
        if(fileEx == ""){
        	alert(SELECT_EXPORT_FILE+"!");
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
   			document.taxTableForm.action="/gz/gz_accounting/tax/import_tax_mx_excel.do?b_setImportList=link";
  			document.taxTableForm.submit();
    	}
    	else
    	{
    		alert(MUSTER_EXPORT_EXCEL_FILE);
    	}
    }

  </script>
  <body>
  <form name="taxTableForm" method="post" action="/gz/gz_accounting/import_tax_mx_excel.do" enctype="multipart/form-data" >

	 <table border="0" cellspacing="0"  align="center" cellpadding="0" style="width:700px;margin-top:60px;">	
	 <tr>
	 <td align="center">
    <fieldset align="center" >
        <legend ><bean:message key="jx.import.selectfile"/></legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td width="400"> <Br><bean:message key="jx.import.file"/>
              <input type="file" class="text4" name="importfile" size="40"  >
              </td>
          </tr>
          <tr > 
            <td>&nbsp; </td>
          </tr>		  
         
        </table>
	</fieldset>
	</td></tr>
	 <tr> 
            <td align="center" height="35px;"> 
            <input type="button" name="b_update" value="<bean:message key='hire.jp.apply.upload'/>" class="mybutton" onClick="imports()"> 
            <input type="button" name="b_update" value="<bean:message key='kq.register.kqduration.cancel'/>" class="mybutton" onClick="goback()">			
            </td>
          </tr>
  </form>
  </body>
</html>
