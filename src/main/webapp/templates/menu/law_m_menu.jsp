<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
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
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight- window.screenTop -80;
function turn()
{
    <%if(isturn==null||!isturn.equals("false")){%>
	if(parent.myBody.cols != '0,*')
	{
		parent.myBody.cols = '0,*';
	}
	else
	{
		parent.myBody.cols = '170,*';
	}
	<%}%>
}     
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">         

<table cellpadding=0 cellspacing=0 width=169  class="menu_table">
  <tr style="cursor:hand;">
    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>人事法规</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu1> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?basetype=4" target="il_body" function_id="xxx"><img src="/images/query_set.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?basetype=4" target="il_body" function_id="xxx"><font id="a001" class="menu_a">法规浏览</font></hrms:link>
              </td>            
            </tr>
                      
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?basetype=4" target="il_body" ><img src="/images/request.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?basetype=4" target="il_body" ><font id="a014" class="menu_a" >法规维护</font></a></td>
            </tr>

          </table>
     </div>
   </td>
  </tr>
</table>


<script language="javascript">
  var whichOpen=menuTitle1;
  var whichContinue="";
  document.all.menu1.style.height =divHeight;
  document.all.menu1.style.display="block";
  parent.frames[1].name = "il_body"; 
</script>  

                                                                              