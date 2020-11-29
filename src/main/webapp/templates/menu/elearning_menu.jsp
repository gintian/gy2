<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
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
   var  divHeight = window.screen.availHeight - window.screenTop -140;
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
<table cellpadding=0 cellspacing=0 width=169  class="menu_table" index="1">
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>在线培训</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:block;"   id=menu1> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
           <tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/lession_dir/course_org.htm" target="il_body"><img src="../../images/card_set.gif" border=0></a></td>
            </tr>
            <tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/lession_dir/course_org.htm" target="il_body"><font class="menu_a">课件目录</font></a></td>
            </tr>
			 <tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/mylession/mycourse.htm" target="il_body" ><img src="../../images/public_info.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
               <a href="../../elearning/mylession/mycourse.htm" target="il_body"  ><font id="a001" class="menu_a">我的课程</font></a>
              </td>
            </tr>
			<tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/plant/search_plant.htm" target="il_body"><img src="../../images/yw.gif" width="32" height="32" border=0></a></td>
            </tr>
            <tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/plant/search_plant.htm" target="il_body"><font class="menu_a">浏览培训班</font></a></td>
            </tr>
			<tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/plant/my_plant.htm" target="il_body"><img src="../../images/cardset.gif" width="32" height="32" border=0></a></td>
            </tr>
            <tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/plant/my_plant.htm" target="il_body"><font class="menu_a">我的培训班</font></a></td>
            </tr>
			<tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/exam/my_exam.htm" target="il_body"><img src="../../images/zd.gif" width="32" height="32" border=0></a></td>
            </tr>
            <tr>
              
            <td  align="center" class="loginFont" ><a href="../../elearning/exam/my_exam.htm" target="il_body"><font class="menu_a">我的考试</font></a></td>
            </tr>		         

          </table>
     </div>
   </td>
  </tr>
</table>



<script language="javascript">
	showFirst();
</script>  

                                                                              