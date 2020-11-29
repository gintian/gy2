<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%

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
   var  divHeight = window.screen.availHeight - window.screenTop -235;
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0">    
<hrms:priv func_id="24001"> 
<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="1">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>用工需求</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
     <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
           <hrms:priv func_id="2400101">
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link&gather_type=0" target="il_body" function_id="xxx"><img src="/images/lpublic_info.gif" border=0></hrms:link>
              </td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                 <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link&gather_type=0" target="il_body" function_id="xxx"><font id="a001" class="menu_a">计划内用工</font></hrms:link>
              </td>
            </tr>
            </hrms:priv> 
            <hrms:priv func_id="2400102">            
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link&gather_type=1" target="il_body" function_id="xxx"><img src="/images/lpublic_info.gif" border=0></hrms:link>
              </td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                 <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link&gather_type=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">计划外用工</font></hrms:link>
              </td>
            </tr>
            </hrms:priv>  
            <hrms:priv func_id="2400103">  
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/resource_plan/search_resource_list.do?b_query=link" target="il_body"><img src="/images/apply.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/resource_plan/search_resource_list.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >人力规划</font></a></td>
            </tr> 
           </hrms:priv>
          </table>
	</form>
      </div>
    </td>
  </tr>
</table>
</hrms:priv>
<hrms:priv func_id="24002">
<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="2">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>招聘计划</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
            <hrms:priv func_id="2400201">
            <tr>
              <td  align="center" class="loginFont" >
               <hrms:link href="/hire/zp_plan/search_zp_plan.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/aaa.gif" border=0></hrms:link>
              </td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/zp_plan/search_zp_plan.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">招聘计划</font></hrms:link>
              </td>
            </tr>
            </hrms:priv>  
            <hrms:priv func_id="2400202">     
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_job/search_zp_joblist.do?b_query=link" target="il_body"><img src="/images/zd.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_job/search_zp_joblist.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >招聘活动</font></a></td>
            </tr> 
             </hrms:priv>
             <hrms:priv func_id="2400206">    
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_release_pos/search_release_poslist.do?b_query=link" target="il_body"><img src="/images/ld.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_release_pos/search_release_poslist.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >招聘岗位发布</font></a></td>
            </tr> 
            </hrms:priv>
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>
</hrms:priv>
<hrms:priv func_id="24003">
<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="3">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>面试筛选</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
           <hrms:priv func_id="2400301">
            <tr>
              <td  align="center" class="loginFont" >
                 <hrms:link href="/hire/zp_filter/query.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/browser.gif" border=0></hrms:link>
              </td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/zp_filter/query.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">面试甄选</font></hrms:link>
              </td>
            </tr>
            </hrms:priv>  
            <hrms:priv func_id="2400302">    
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_interview/search_dept_poslist.do?b_query=link" target="il_body"><img src="/images/employ_data.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_interview/search_dept_poslist.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >人员面试</font></a></td>
            </tr> 
           </hrms:priv>           
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>
</hrms:priv>
<hrms:priv func_id="24004">
<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="4">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>录用总结</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
           <hrms:priv func_id="2400401">
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/hire/zp_employ/search_hire_employee.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/djx.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/zp_employ/search_hire_employee.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">员工录用</font></hrms:link>
              </td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="2400402">     
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_sumup/sum_up.do?b_query=link" target="il_body"><img src="/images/edit_info.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_sumup/sum_up.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >招聘总结</font></a></td>
            </tr> 
            </hrms:priv>           
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>
</hrms:priv>
<hrms:priv func_id="24005">
<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="5">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
           <hrms:priv func_id="2400501">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/basesetfield.do?b_query=link&a_tab=dbpriv" target="il_body" function_id="xxx"><img src="/images/jgbm.gif" border=0></a></td>
            </tr>
            <tr>
               <td  align="center" class="loginFont" ><a href="/hire/zp_options/basesetfield.do?b_query=link&a_tab=dbpriv" target="il_body" ><font id="a005" class="menu_a" >应聘人才库</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="2400502">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/query_template.do?a_query=1&b_query=link" target="il_body" ><img src="/images/cx.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/query_template.do?a_query=1&b_query=link" target="il_body" ><font id="a006" class="menu_a" >查询模板</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="2400503">
             <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/test_process.do?b_query=link" target="il_body" ><img src="/images/dmwh.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/test_process.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >面试环节定义</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="2400504">
            <tr>
               <td  align="center" class="loginFont" ><a href="/hire/zp_options/poslistlogin.do" target="il_body" ><img src="/images/hmc.gif" border=0 ></a></td>
            </tr>
             <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/poslistlogin.do" target="il_body" ><font id="a006" class="menu_a" >招聘资料</font></a></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="2400505">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_filter_login.do" target="il_body" ><img src="/images/browser.gif" border=0 ></a></td>
            </tr>
             <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_filter_login.do" target="il_body" ><font id="a006" class="menu_a" >人员过滤条件</font></a></td>
            </tr> 
	    </hrms:priv>
            <hrms:priv func_id="2400506">	
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_template.do" target="il_body" ><img src="/images/card.gif" border=0 ></a></td>
            </tr>
             <tr>
                <td  align="center" class="loginFont" ><a href="/hire/zp_options/pos_template.do" target="il_body" ><font id="a006" class="menu_a" >岗位职责说明书</font></a></td>
            </tr>
            </hrms:priv>
	    <!--
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_person/search_zp_position.do?b_query=link" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
             <tr>
                <td  align="center" class="loginFont" ><a href="/hire/zp_person/search_zp_position.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >人才招聘</font></a></td>
            </tr>
                -->             
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>
</hrms:priv>
<hrms:priv func_id="24006">
<table cellpadding=0 cellspacing=0 width=169  class="menu_table" index="6">
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>招聘资源</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu6> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
          <hrms:priv func_id="2400601">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_resource/search_zp_resource.do?b_query=link" target="il_body" ><img src="/images/rz.gif" border=0 ></a></td>
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
</hrms:priv>
<hrms:priv func_id="24007">
<table cellpadding=0 cellspacing=0 width=169  class="menu_table" index="7">
   <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>考试科目</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu7> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	  <hrms:priv func_id="2400701">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_subject.do?b_query=link" target="il_body" ><img src="/images/bjbb.gif" border=0 ></a></td>
            </tr>
            <tr>
             <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_subject.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >考试科目维护</font></a></td>
            </tr>
            </hrms:priv> 
            <hrms:priv func_id="2400702">
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_exam/search_exam_report.do?b_query=link" target="il_body" ><img src="/images/edit_info.gif" border=0 ></a></td>
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
</hrms:priv>






<script language="javascript">
	showFirst();
</script>  



                                                                                                                                                       