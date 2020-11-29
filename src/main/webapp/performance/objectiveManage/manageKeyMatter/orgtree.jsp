<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
	//  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11	
	String operOrg =userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
	
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script language="javascript">
/*
function refreshTree()
{
	manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/orgTree.do?b_query=link&action=keyMatterList.do&treetype=duty&kind=0";
	manageKeyMatterForm.target="il_body";
	manageKeyMatterForm.submit();
}
*/
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">

<html:form action="/performance/objectiveManage/manageKeyMatter/orgTree"> 
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
    	         
       <tr>
            <td align="left"> 
            
            	 <% if (operOrg.length() > 2){ %>
            	 	<hrms:orgtree action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_query=link" target="mil_body" flag="${manageKeyMatterForm.flag}" viewunit="1" nmodule="5" loadtype="${manageKeyMatterForm.loadtype}" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
 				              			
				 <% }else{ %>	
				 	<hrms:orgtree action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_query=link" target="mil_body" flag="${manageKeyMatterForm.flag}"  loadtype="${manageKeyMatterForm.loadtype}" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
				 <% } %>		           
           </td>
       </tr>           
    </table>
</html:form>
