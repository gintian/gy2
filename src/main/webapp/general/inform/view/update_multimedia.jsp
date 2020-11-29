<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">

	function to_save()
	{
		var title = mInformForm.filetitle.value;
		 if(title == "")
		 {
	    	return ;
	     }
		document.mInformForm.action="/general/inform/emp/view/updatemultimedia.do?b_query=link";
		document.mInformForm.submit(); 
	}
	
	function return_to()
	{
		document.getElementById("filetitle").value="";
		document.mInformForm.action="/general/inform/emp/view/opermultimedia.do?b_query=link&isvisible=${mInformForm.isvisible}&a0100=${mInformForm.a0100}&multimediaflag=${mInformForm.multimediaflag}";
		document.mInformForm.submit(); 
	}
	function   NoExec()   
  	{   
          if(event.keyCode==13||event.keyCode==222)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	}  
</script>
<%
    response.setContentType("text/xml;charset=UTF-8");
	String i9999=(String)request.getParameter("i9999");
	
%>
<html:form action="/general/inform/emp/view/opermultimedia">
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="left" class="ListTable">
   <tr height="20">
  	   <td align="left" colspan="2" class="TableRow"><bean:message key="general.mediainfo.mediaedit"/></td>            	      
   </tr>  
   <tr>
       <td align="right" width="45%" class="RecordRow_left">
           <bean:message key="general.mediainfo.title"/>
       </td>
       <td align="left" class="RecordRow_right">
           <html:text name="mInformForm" property="filetitle"  styleId="filetitle" styleClass="filetitle text4" maxlength="20" size="35" onkeydown="NoExec()"/>
       </td>
   </tr>  
   <tr>
       <td colspan="2" align="center" style="height:35px;" class="RecordRow">
       	   <html:button styleClass="mybutton" property="b_save" onclick="to_save();">
			     <bean:message key="button.save"/></html:button>
           <html:button styleClass="mybutton" property="b_return" onclick="return_to();">
			     <bean:message key="button.return"/></html:button>	
       </td>
   </tr>
</table>
</html:form>

