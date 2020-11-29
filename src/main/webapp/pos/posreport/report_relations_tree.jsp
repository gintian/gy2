<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.inform.org.OrgMapForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	OrgMapForm orgMapForm = (OrgMapForm)session.getAttribute("orgMapForm");//add by wangchaoqun on 2014-11-2 
	String seprartor = orgMapForm.getSeprartor();
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<html:form action="/general/inform/org/map/searchorgmap"> 
<html:hidden name="orgMapForm" property="constant" /> 
	<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       <tr>
           <td align="left"> 
           <div id="postree" ></div>
   				<SCRIPT LANGUAGE=javascript>
	             var m_sXMLFile="/pos/posreport/pos_report_relations_tree.jsp?position=first&sep=<%=seprartor%>";	                          
	             var root=new xtreeItem("root","汇报关系","/pos/posreport/search_report_relations.do?b_search=link&kind=0&code=","mil_body","汇报关系","/images/pos_l.gif",m_sXMLFile);
	             root.setup(document.getElementById("postree"));	     
   				</SCRIPT>    
           </td>
           </tr>           
    </table>
</html:form>
