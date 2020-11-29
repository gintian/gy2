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
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<body leftmargin="0" topmargin="0" marginwidth="0" >
<html:form action="/general/template/search_bs_tree">

   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
		 	            
         <tr>
           <td align="left"> 
             <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="templateHistorydataForm" property="bs_tree" filter="false"/>
             </SCRIPT>
             </div> 
          
           </td>
           </tr>           
    </table> 
   <script type="text/javascript">
	//root.expandAll();  bug 23763 陈总提 人事异动_新 历史数据中业务模板不用自动展开  20161028 hej update
	var node=root.childNodes[0];
	if(node)
	{
		node.expand();
	}
	initTreeNode();
	// v:  T->显示机构树  N->不显示机构树
	function showTemplateList(v,tabid)
	{
		  parent.menupnl.toggleCollapse(false);
		  parent.mil_body.location='/general/template/historydata.do?b_query=link&tabid='+tabid;	
	}
	function initTreeNode()
	{
//  initDocument();
	  var obj=root.childNodes[0];
	  if(obj)
	  {

	    var objfirst=obj.childNodes[0];
	    selectedClass("treeItem-text-"+objfirst.id);
			var href="/general/template/historydata.do?b_query=link&tabid="+objfirst.uid;
			 parent.mil_body.location=href;
		
			
	   }else{
	   		var href="/general/template/historydata.do?b_query=link&tabid=laodonghetong";
			 parent.mil_body.location=href;
	   }

	}
   </script>

</html:form>
</body>
