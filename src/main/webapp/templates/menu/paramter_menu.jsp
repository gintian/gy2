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
    String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
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
   var  divHeight = window.screen.availHeight- window.screenTop -140;
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
</head>
<body class=menuBodySet style="margin:0 0 0 0">         

 
<table cellpadding=0 cellspacing=0 width=169  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>参数管理</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu1> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	  <hrms:priv func_id="070101">            
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/query/queryfieldset.do" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/query/queryfieldset.do?a_query=1&b_query=link" target="il_body" ><font id="a005" class="menu_a" >查询设置</font></a></td>
            </tr>
          </hrms:priv>
	  <hrms:priv func_id="070201">            
            <tr>
              <td  align="center" class="loginFont" ><a href="/ykcard/cardconstantset.do?b_cardset=set" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/ykcard/cardconstantset.do?b_cardset=set" target="il_body" ><font id="a006" class="menu_a" >薪酬表设置</font></a></td>
            </tr> 
          </hrms:priv>    
          <hrms:priv func_id="070301">            
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/param/otherparam.do?b_other=link" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
               <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/param/otherparam.do?b_other=link" target="il_body" ><font id="a006" class="menu_a" >其它参数</font></a></td>
            </tr> 
          </hrms:priv>  
          <hrms:priv func_id="070902">           
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/param/friend.do?b_query=link" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
               <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/param/friend.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >友情链接</font></a></td>
            </tr>
          </hrms:priv>  
          <hrms:priv func_id="070903">                                   
            <tr>
              <td  align="center" class="loginFont" ><a href="/system/options/stmp_options.do?b_query=link" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/system/options/stmp_options.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >邮件服务器</font></a></td>
            </tr>           
          </hrms:priv>         
         <!--
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/addrnote_set.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/addressbook/addressbookset.do" target="il_body" ><font id="a007" class="menu_a" >通讯录设置</font></a></td>
            </tr> 
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/card_set.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/cardingcard/cardingcardset.do?b_cardset=set" target="il_body" ><font id="a008" class="menu_a" >名片夹设置</font></a></td>
            </tr> 
	     -->
          </table>
        
     </div>
   </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width=169  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>信息维护</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
           <hrms:priv func_id="070801">  
             <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=update&treetype=org&target=mil_body" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/account.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">单位信息</font></hrms:link>
              </td>
            </tr>
           </hrms:priv>   
           <hrms:priv func_id="070901">  
             <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/workbench/dutyinfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/account.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/workbench/dutyinfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">职位信息</font></hrms:link>
              </td>
            </tr>
           </hrms:priv>  
            <hrms:priv func_id="070301">                     
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=2" target="il_body" function_id="xxx"><img src="/images/flow_upload.gif" border=0 ></hrms:link></td>
            </tr>
            <tr>
            <td  align="center" class="loginFont" >
             <hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >流程上传</font></hrms:link>
            </td>
            </tr> 
          </hrms:priv>             
	  <hrms:priv func_id="070401">           
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=1" target="il_body" function_id="xxx"><img src="/images/table_upload.gif" border=0 ></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
               <hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >表格上传</font></hrms:link>
              </td>
            </tr>
          </hrms:priv>   
	 
	  <hrms:priv func_id="070501">                    
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/infomanager/board/searchboard.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/public_info.gif" border=0 ></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/infomanager/board/searchboard.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >公告栏维护</font></hrms:link>
              </td>
            </tr> 
          </hrms:priv>   
	  <hrms:priv func_id="070601">                     
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/infomanager/askinv/searchtopic.do?b_query=link" target="il_body" ><img src="/images/investigate.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/infomanager/askinv/searchtopic.do?b_query=link" target="il_body" ><font id="a013" class="menu_a" >问卷调查表定义</font></a></td>
            </tr> 
          </hrms:priv>                                               
          </table>
     </div>
   </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width=169  class="menu_table">
  <tr style="cursor:hand;">
    <td  class=menu_title align="center" id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>规章制度</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	  <hrms:priv func_id="070701">           
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?basetype=1" target="il_body" ><img src="/images/public_info.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?basetype=1" target="il_body" ><font id="a014" class="menu_a" >规章制度</font></a></td>
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

                                                                              