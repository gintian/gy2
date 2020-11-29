<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//String viewunit = "1";
	/*if(userView != null){
		if(userView.getStatus()==4||userView.isSuper_admin()){
			viewunit="0";
		}
		liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm
		if(userView.getStatus()==0&&!userView.isSuper_admin()){
			String codeall = userView.getUnit_id();
			if(codeall==null||codeall.length()<3)
				viewunit="0";
		}
		
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}*/
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/train/attendance/orgAtteTree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>   
     	<logic:equal value="1" name="trainAtteForm" property="type">     
           <td align="left">
                 <hrms:orgtree action="/train/attendance/trainAtteCourse.do?b_query=link&queryflag=&classplan=" target="mil_body" flag="0" nmodule="6" loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1"/>
           </td>
        </logic:equal>
        <logic:equal value="2" name="trainAtteForm" property="type">     
           <td align="left"> 
                 <hrms:orgtree action="/train/attendance/registration.do?b_query=link&query=&classplan=" target="mil_body" flag="0" nmodule="6" loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1"/>
           </td>
        </logic:equal>
        <logic:equal value="3" name="trainAtteForm" property="type">     
           <td align="left"> 
                 <hrms:orgtree action="/train/signCollect/signcollect.do?b_query=link&query=" target="mil_body" flag="0" nmodule="6" loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1"/>
           </td>
        </logic:equal>
      </tr>            
   </table>
</html:form>
<script>
	root.openURL();
</script>
