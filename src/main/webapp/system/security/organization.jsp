<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle,com.hjsj.hrms.utils.PubFunc"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String flag=request.getParameter("flag");
	String codeid=userView.getManagePrivCode();
	String codevalue=userView.getManagePrivCodeValue();

	String a_code=codeid+codevalue;
	if(userView.isSuper_admin()&&(!userView.isBThreeUser()))
	   a_code="UN";
	//System.out.println("---->"+a_code);		   
	String action="/system/security/assign_login.do?b_query=link&a_code="+a_code;
	//String action="/system/security/assign_login.do?b_query=link&a_code=UN";

	if(flag.equals("org"))
	  action="/system/security/assign_org_login.do?b_query=link&a_code="+a_code;
	
	if(flag.equals("perObject"))
	  action="/selfservice/performance/performanceImplement.do?b_query=link&a_code="+a_code; 
   
	//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
      int index = action.indexOf("&");
      if(index>-1){
          String allurl = action.substring(0,index);
          String allparam = action.substring(index);
          action=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
      }
      //将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
%>

<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
   <div id="treemenu" ></div>
<BODY>
</HTML>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
<%
 if(!a_code.equals("UN"))
 {
%>	//【7884】非超级用户授权了人员管理范围和操作单位，但是点开账号分配看不到组织机构，角色快速分配也是一样的。  jingq upd 2015.03.09
	var m_sXMLFile	= "/system/security/get_org_tree.jsp?encryptParam=<%=PubFunc.encrypt("flag="+flag+"&params=codeitemid%3D'"+codevalue+"'") %>";//
<%
 }
 else
 {
%>
	var m_sXMLFile	= "/system/security/get_org_tree.jsp?encryptParam=<%=PubFunc.encrypt("flag="+flag+"&params=codeitemid%3Dparentid") %>";	 //
<%}%>
//alert(m_sXMLFile);
var root=new xtreeItem("UN","组织机构",'<%=action%>',"mil_body","组织机构","/images/unit.gif",m_sXMLFile);
root.setup(document.getElementById("treemenu"));
root.openURL();

</SCRIPT>