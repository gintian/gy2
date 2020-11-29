<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.actionform.browse.SynthesisBrowseForm"%>
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
	SynthesisBrowseForm synthesisBrowseForm=(SynthesisBrowseForm)session.getAttribute("synthesisBrowseForm");
	String a0100=synthesisBrowseForm.getA0100();//未加密的a0100加密处理
	a0100=PubFunc.encrypt(a0100);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String url="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+synthesisBrowseForm.getDbpre()+"&a0100="+a0100+"&inforkind="+synthesisBrowseForm.getInforkind()+"&tabid="+synthesisBrowseForm.getTabid();
%>
<script type="text/javascript">
   function test()
	{
	  var syn_flag="${synthesisBrowseForm.syn_flag}";
	  if(syn_flag=="true")
	  {
	     var obj=$('synthesis_browse');
	      obj.setSelectedTab('<%=session.getAttribute("changtab_synthesis")%>');
	  }
	}
</script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >

<body onload="test();">
<html:form action="/general/inform/synthesisbrowse/synthesisbrowseinfo"> 
<div style="padding-left: 5px;width: 100%;">
<logic:equal name="synthesisBrowseForm" property="syn_flag" value="true">		
  <hrms:tabset name="synthesis_browse" width="100%" height="100%" type="true"> 
        <hrms:tab name="card" label="tab.synthesis.card" visible="true" url="<%=url %>">
        </hrms:tab>  
	<hrms:tab name="browse" label="tab.synthesis.info" visible="true" url="/general/inform/synthesisbrowse/synthesisdetailinfo.do?b_search=link">
        </hrms:tab> 
  </hrms:tabset>
 </logic:equal>
</div>  
</html:form>
</body>
