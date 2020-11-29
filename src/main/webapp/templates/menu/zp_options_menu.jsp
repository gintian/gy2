<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

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
%>
<head>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight- window.screenTop -100;
   function turn()
   {
	if(parent.myBody.cols != '0,*')
	{
		parent.myBody.cols = '0,*';
	}
	else
	{
		parent.myBody.cols = '170,*';
	}
   }     
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0">         

 
<table cellpadding=0 cellspacing=0 width=169  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="../../images/darrow.gif" border=0></span>参数设置</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu1> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	  <hrms:priv func_id="080101">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/basesetfield.do?b_query=link&a_tab=dbpriv" target="il_body" function_id="xxx"><img src="/images/employee.gif" border=0></a></td>
            </tr>
            <tr>
               <td  align="center" class="loginFont" ><a href="/hire/zp_options/basesetfield.do?b_query=link&a_tab=dbpriv" target="il_body" ><font id="a005" class="menu_a" >人才库</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="080201">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/query_template.do?a_query=1&b_query=link" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/query_template.do?a_query=1&b_query=link" target="il_body" ><font id="a006" class="menu_a" >查询模板</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="080301">
             <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/test_process.do?b_query=link" target="il_body" ><img src="/images/employee.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/test_process.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >面试环节定义</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="080401">
            <tr>
               <td  align="center" class="loginFont" ><a href="/hire/zp_options/poslistlogin.do" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
             <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/poslistlogin.do" target="il_body" ><font id="a006" class="menu_a" >面试材料维护</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="080501">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_filter_login.do" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
             <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_filter_login.do" target="il_body" ><font id="a006" class="menu_a" >人员过滤条件</font></a></td>
            </tr> 
	    </hrms:priv>
	    <hrms:priv func_id="080601">		
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_template.do" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
             <tr>
                <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_template.do" target="il_body" ><font id="a006" class="menu_a" >岗位职责说明书</font></a></td>
            </tr>
            </hrms:priv> 		
          </table>
        
     </div>
   </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width=169  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="../../images/darrow.gif" border=0></span>招聘资源</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
          <hrms:priv func_id="080701">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_resource/search_zp_resource.do?b_query=link" target="il_body" ><img src="/images/employee.gif" border=0 ></a></td>
            </tr>
            <tr>
             <td  align="center" class="loginFont" ><a href="/hire/zp_resource/search_zp_resource.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >招聘渠道</font></a></td>
            </tr>
            </hrms:priv>                                        
          </table>
     </div>
   </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width=169  class="menu_table">
   <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="../../images/darrow.gif" border=0></span>考试科目</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	  <hrms:priv func_id="080801">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_subject.do?b_query=link" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
            <tr>
             <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_subject.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >考试科目维护</font></a></td>
            </tr>
            </hrms:priv> 
            <hrms:priv func_id="080901">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_report.do?b_query=link" target="il_body" ><img src="/images/employee.gif" border=0 ></a></td>
            </tr>
            <tr>
             <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_report.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >考试成绩录入</font></a></td>
            </tr>
            </hrms:priv>           
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

                                                                              