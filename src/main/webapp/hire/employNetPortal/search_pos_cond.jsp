<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.hire.ZppersonForm"%>
<%@ page import="java.util.HashMap"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";	 
	}
%>
<script language="javascript">

	function sub()
	{
		 employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_query=link";
		 employPortalForm.target="i_body";
		 employPortalForm.submit();
	}

</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<html:form action="/hire/employNetPortal/search_zp_position">

   <table border="0" cellspacing="1"  align="center" cellpadding="1" width="100%" class="ListTable">
         <tr class="trDeep">
  	    <td colspan="2" height="20" align="center"><img src="/images/forumme.gif"><bean:message key="tree.kkroot.kkdesc"/><bean:message key="lable.law_base_file_search.search"/><br></td>
      </tr>
      <tr>
        <td colspan="2" align="left" class="RecordRowinvestigate">
            <html:text name="employPortalForm" property="employArea" />
        </td> 
     </tr>               
     <tr>
          <td colspan="2" align="left" class="RecordRowinvestigate">
        		<hrms:optioncollection name="employPortalForm" property="issueDateScopeList" collection="list" />
					             <html:select name="employPortalForm" property="issueDate" size="1" style="width:150px"  >
					             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			   					 </html:select>	
     </td> 
     </tr> 
     <tr>
       <td colspan="2" align="left" class="RecordRowinvestigate">
       		<html:text name="employPortalForm" property="employPositionDest" />
      </td>
    </tr>
    <tr>
    <td align="center" colspan="2" class="RecordRowinvestigate">
       <input type="button" name="searchbutton"  value="<bean:message key="lable.law_base_file_search.search"/>" onclick="sub()">
    </td>
 </tr>
 </table>
 
</html:form>
