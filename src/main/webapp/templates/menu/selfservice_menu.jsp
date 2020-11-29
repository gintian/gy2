<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
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
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -180;
   var flag=0;
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
<style>
.menu_table{

   background-color:#DEEAF5;

   BORDER-RIGHT: #C4D8EE 1pt solid;      
}

.menuBodySet{

	background-color:#DEEAF5;

    margin:0 4 0 0 ; 
}  
</style>
<body class="menuBodySet" style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class="menu_title"  id="menuTitle1" >HR服务台</td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:expression(document.body.offsetHeight);filter:alpha(Opacity=100);display=block;"  id=menu1> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
     	<hrms:priv func_id="1111"> 
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=4" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=4" target="il_body"  function_id="xxx"><font id="a001" class="menu_a">知识中心</font></hrms:link>
              </td>            
            </tr>
        </hrms:priv>      	
     	<hrms:priv func_id="1107"> 
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=1" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=1" target="il_body"  function_id="xxx"><font id="a001" class="menu_a">规章制度</font></hrms:link>
              </td>            
            </tr>
        </hrms:priv>  
     	<hrms:priv func_id="1108">                    
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/downfile/downfilelist.do?b_query=link&fileflag=2" target="il_body" function_id="xxx"><img src="/images/investigate.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/downfile/downfilelist.do?b_query=link&fileflag=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a">办事流程</font></hrms:link>
              </td>            
            </tr>
        </hrms:priv>   
     	<hrms:priv func_id="1109">                     
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/downfile/downfilelist.do?b_query=link&fileflag=1" target="il_body" function_id="xxx"><img src="/images/investigate.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/downfile/downfilelist.do?b_query=link&fileflag=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">人事表格</font></hrms:link>
              </td>            
            </tr>
        </hrms:priv>  
     	<hrms:priv func_id="1105">                      
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/searchpropose.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/addrnote_set.gif" border=0></hrms:link></td>
            </tr>              
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/propose/searchpropose.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">意见箱</font></hrms:link>
              </td>                
            </tr>
        </hrms:priv>  
     	<hrms:priv func_id="1106">                      
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/searchconsulant.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
            </tr>              
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/propose/searchconsulant.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">咨询台</font></hrms:link>
              </td>                 
            </tr>
        </hrms:priv> 
        <hrms:priv func_id="1112">   
             <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/general/kanban/kanban.do?b_query=link&clearwhere=1" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/general/kanban/kanban.do?b_query=link&clearwhere=1" target="il_body"  function_id="xxx"><font id="a001" class="menu_a">看板管理</font></hrms:link>
              </td>            
            </tr> 
          </hrms:priv>         
          <hrms:priv func_id="1113">   
             <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/addressbook/initqueryaddressbook.do?b_init=link&action=queryaddressbook.do&target=mil_body&issuperuser=1" target="il_body" function_id="xxx"><img src="/images/investigate.gif" border=0></hrms:link></td>
            </tr>     
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/selfservice/addressbook/initqueryaddressbook.do?b_init=link&action=queryaddressbook.do&target=mil_body&issuperuser=1" target="il_body"  function_id="xxx"><font id="a001" class="menu_a">通讯录</font></hrms:link>
              </td>            
            </tr> 
          </hrms:priv>     
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>
		       
     

<script language="javascript">
   <hrms:priv func_id="11" module_id="0">     		
         flag=1;
   </hrms:priv>  
//  var i;
//  for(i=0;i<parent.frames.length;i++)
//  {
//  	alert(parent.frames[i].name);
//  }
  parent.frames[1].name= "il_body"; 
  if(flag==0)
    turn();
</script>  



                                                                              