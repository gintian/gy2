<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
//-->
</script>
<body bgColor='#F7F7F7' scroll='no' topMargin='0' leftMargin='0' >
<html:form action="/general/inform/emp/e_archive/view_e_archive">
   <jsp:plugin  type="applet" name="earchive" archive="hj_client.jar,struts_extends.jar,hessian-3.1.3.jar,command.jar,rowset.jar,jai_codec.jar,jai_core.jar" code="com.hjsj.hrms.client.e_archive.BrowseArchiveApplet.class"  width="${archiveForm.width}"  height="${archiveForm.height}" 
   codebase="/client">  
       <jsp:params>  
           <jsp:param  name="filename"  value="${archiveForm.filename}"/>  
           </jsp:params>  
           <jsp:fallback>
                 <p>Unable to start plugin.</p>
           </jsp:fallback>	           
   </jsp:plugin>
</html:form>
</body>

