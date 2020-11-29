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
		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_query=link";
		document.gztemplateSetForm.submit();
	}
	
	
	
    
    function imports()
    {
    	 var fileEx = gztemplateSetForm.file.value;
        if(fileEx == ""){
        	alert(GZ_TAX_INFO1);
        	return ;
        }
         if(!validateUploadFilePath(fileEx))
           return;
   		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_validateImport=validate";
  		document.gztemplateSetForm.submit();
    
    }

  </script>
  <body>
  <form name="gztemplateSetForm" method="post" action="/gz/templateset/gz_templatelist.do" enctype="multipart/form-data" >
  
    <br>
    <%
    if(request.getParameter("b_importZip")!=null&&request.getParameter("b_importZip").equals("import"))
    {
    %>
    <table  align='center'>
    <tr><td>
   <font color='red'><logic:equal name="gztemplateSetForm" property="gz_module" value="0"><bean:message key="gz.report.salary"/></logic:equal><logic:equal name="gztemplateSetForm" property="gz_module" value="1"><bean:message key="gz.report.welfare"/></logic:equal><bean:message key="gz.report.importSuccess"/>!</font>
    </td></tr>
    </table>
	<% } %>
	<br>	
    <fieldset align="center" style="width:50%;">
        <legend ><bean:message key="jx.import.select2"/><logic:equal name="gztemplateSetForm" property="gz_module" value="0"><bean:message key="gz.report.salary"/></logic:equal><logic:equal name="gztemplateSetForm" property="gz_module" value="1"><bean:message key="gz.report.welfare"/></logic:equal><bean:message key="gz.report.typeFile"/> </legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td width="400"> <Br><bean:message key="kh.field.file"/> 
              <input type="file" name="file" size="40" class="inputtext" ></td>
          </tr>
          <tr > 
            <td>&nbsp; </td>
          </tr>		  
         
        </table>
	</fieldset>
	<table border="0" cellspacing="0"  align="center" cellpadding="0" >
	 <tr> 
            <td align="center" style="padding-top:3px;"> <input type="button" name="b_update" value="<bean:message key="hire.jp.apply.upload"/>" class="mybutton" onClick="imports()"> 
			<input type="reset" value="<bean:message key="options.reset"/>" class="mybutton">
             <input type="button" name="b_update" value="<bean:message key="reportcheck.return"/>" class="mybutton" onClick="goback()">			
               </td>
          </tr>
          </table>
  </form>
  </body>
</html>
