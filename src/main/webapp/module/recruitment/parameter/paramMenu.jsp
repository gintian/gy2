<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%  
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
//主题皮肤
String themes = "default";
if(userView != null){ 
    themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());   
 }
%>
<hrms:hcmmenu menu_id="180600" menutype="menuitem" themes="<%=themes%>" name="mbobj" />