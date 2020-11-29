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
		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_query=query";
  		document.gzStandardPackageForm.submit();
	}
	
	
	
    
    function imports()
    {
    	 var fileEx = gzStandardPackageForm.file.value;
        if(fileEx == ""){
        	alert(GZ_TAX_INFO1);
        	return ;
        }
       
        if(!validateUploadFilePath(fileEx))
           return;
        
        var fileName = fileEx.lastIndexOf(".");//取到文件名开始到最后一个点的长度
        var fileNameLength = fileEx.length;//取到文件名长度
        var fileFormat = fileEx.substring(fileName + 1, fileNameLength);//截
        //导入的格式必须为zip格式
        if("zip" != fileFormat.toLowerCase()) {
        	alert(GZ_TAX_INFO2.replace("excel","zip"));
        	return;
        }
        
   		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_validateImport=validate";
  		document.gzStandardPackageForm.submit();
    
    }

  </script>
  <body>
  <form name="gzStandardPackageForm" style="text-align: center" method="post" action="/gz/templateset/standard/standardPackage.do" enctype="multipart/form-data" >
  
	<table style="width:700px;margin-top:60px;"><tr></tr></table>
    <fieldset align="center" style="width:700px;margin:0 auto" >
        <legend ><bean:message key="label.gz.selectImportSalaryTable"/></legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td width="400"> <Br><bean:message key="menu.file"/> 
              <input type="file" name="file" size="40"  class="inputtext"></td>
          </tr>
          <tr > 
            <td>&nbsp; </td>
          </tr>		  
          <tr> 
            <td align="center"> <input type="button" name="b_update" value="<bean:message key="hire.jp.apply.upload"/>" class="mybutton" onClick="imports()"> 
			<input type="reset" value="<bean:message key="options.reset"/>" class="mybutton">
             <input type="button" name="b_update" value="<bean:message key="reportcheck.return"/>" class="mybutton" onClick="goback()">			
               </td>
          </tr>
        </table>
        <br>
	</fieldset>
	
  </form>
  </body>
</html>
