<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<html:form action="/general/inform/inform_interface">
${ginformForm.sql}
<hrms:dataset name="ginformForm" property="fieldlist" scope="session" setname="${ginformForm.tablename}"  setalias="test_set" readonly="false" editable="true" select="true" sql="${ginformForm.sql}" buttons="movefirst,prevpage,moveprev,movenext,nextpage,movelast,appendrecord,deleterecord">
   <hrms:commandbutton name="deletebtn" functionId="0521010001" refresh="true" type="selected" setname="${ginformForm.tablename}">
     <bean:message key="button.setfield.delfield"/>
   </hrms:commandbutton>
</hrms:dataset> 
</html:form>



