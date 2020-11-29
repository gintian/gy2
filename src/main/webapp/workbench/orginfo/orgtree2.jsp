<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%!
	private static String org_expand_level;
	static{
		org_expand_level=com.hrms.struts.constant.SystemConfig.getPropertyValue("org_expand_level");
	}
 %>
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
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script type="text/javascript">
<!--
	function add(uid,text,action,target,title,imgurl,xml){
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		if(currnode.load){
			tmp.expand();
			Global.selectedItem = currnode;
		}
	}
	function addtrs(transfercodeitemidall,uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		var root = currnode.root();
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		a(root,transfercodeitemidall.toUpperCase());
		function a(root,name)
		{
			for(var z=0;z<=root.childNodes.length-1;z++){
				if(name==root.childNodes[z].uid){
					if(root.childNodes[z].load){
						root.childNodes[z].add(tmp);
						tmp.expand();
						Global.selectedItem = currnode;
					}
					//else
					//	root.childNodes[z].expand();
				}
				else
					a(root.childNodes[z],name);
			}
		}
	}
//-->
</script>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes></hrms:themes>
<body style="margin:0px;padding:0px;"> 
<html:form action="/workbench/orginfo/searchorginfo" style="width:94%"><%--底部去掉滚动条 修改width样式  wangb 20190315 --%> 
            <div id="treemenu" style="margin:0px; padding:0 5px;width:100%;"> 
               <SCRIPT LANGUAGE=javascript>    
               <bean:write name="orgInfoForm" property="treeCode" filter="false"/>
               <%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
             </SCRIPT>		
             </div>             
</html:form>
</body>
