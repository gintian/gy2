<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
 function read_sp_result()
 {
   var sp_result="${browseRegisterForm.sp_result}"; 
    if(sp_result!=null&&sp_result!="")
        alert(sp_result);
      
    var url_str="${browseRegisterForm.re_url}";  
    document.form.action=url_str;    
    document.form.submit();
 }
 window.onload = read_sp_result;
</script>
<form name="form" method="post" action="">
</form>