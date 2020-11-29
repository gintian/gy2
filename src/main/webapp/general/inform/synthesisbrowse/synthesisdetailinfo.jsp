<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
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
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes />
<html:form action="/general/inform/synthesisbrowse/synthesisdetailinfo"> 
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin: 0 0 0 0;">   
         <tr>        
           <td align="left" valign="top"> 
            <logic:equal value="1" name="synthesisBrowseForm" property="inforkind">
                    <script>
                        document.location.href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${synthesisBrowseForm.browse_dbpre}&a0100=${synthesisBrowseForm.a0100}&flag=notself&returnvalue=1000000";
                    </script>
              </logic:equal>
              <logic:notEqual value="1" name="synthesisBrowseForm" property="inforkind">
                   <hrms:infobrowse nid="${synthesisBrowseForm.a0100}" infokind="${synthesisBrowseForm.inforkind}" pre="${synthesisBrowseForm.browse_dbpre}" isinfoself="1" setflag="1" /> 
              </logic:notEqual>
           </td>
         </tr>            
   </table>
</html:form>
