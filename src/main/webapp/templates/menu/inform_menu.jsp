<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.sql.*"%>
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
	String inputchinfor="";
	String approveflag="";
	Connection connection=null;
	try
	{
	   connection = (Connection) AdminDb.getConnection();
	   Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(connection);
	   inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
	   approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
	}catch(Exception e)
	{
	}finally
	{
	  if(connection!=null)
	   connection.close();	
	}
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse"); 
	 
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
   function turn()
   {
	   <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }   
   function winhrefOT(herf,target)
   {
   if(herf=="")
      return false;
   myinform.action=herf;
   myinform.target=target;
   myinform.submit();
   }
   function document.oncontextmenu() 
   { 
      return　false; 
   }    
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol"> 
	<table cellpadding=0 cellspacing=0 width=159 class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>我的信息</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	   <form name="myinform" action="" method=post target="il_menu">
	   <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
      		<hrms:priv func_id="010101"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="#01" onclick="winhrefOT('/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=<%=userView.getDbname()%>&flag=infoself','il_body');" function_id="xxx"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <a href="#02"  onclick="winhrefOT('/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=<%=userView.getDbname()%>&flag=infoself','il_body');" function_id="xxx"><font id="a001" class="menu_a">信息浏览</font></a>
	              </td>
	            </tr>
	        </hrms:priv> 
	       	<hrms:priv func_id="010201">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/empcardsalaryshow.do?b_cardshow=link&userbase=<%=userView.getDbname()%>&flag=infoself" target="il_body"><img src="/images/jhhs.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/empcardsalaryshow.do?b_cardshow=link&userbase=<%=userView.getDbname()%>&flag=infoself" target="il_body"><font id="a003" class="menu_a">我的薪酬</font></a></td>
	            </tr>
	        </hrms:priv>
	        <hrms:priv func_id="0107">                
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/general/template/myapply/busidesktop.do?br_query=link" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
            </tr>              
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/general/template/myapply/busidesktop.do?br_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">业务申请</font></hrms:link>
              </td>                 
            </tr>                       
	      </hrms:priv>	        
	       	<hrms:priv func_id="0106">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/gz/gz_self/tax/selftaxshow.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/dmwh.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <a href="/gz/gz_self/tax/selftaxshow.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">个人所得税</font></a>
	              </td>
	            </tr>
	        </hrms:priv>                    
	        <hrms:priv func_id="010301">           
	           <tr>
	              <td  align="center" class="loginFont" ><a href="#03"  onclick="winhrefOT('/selfservice/selfinfo/addselfinfo.do?b_add=add&a0100=A0100&userbase=<%=userView.getDbname()%>&i9999=I9999&actiontype=update&setname=A01&flag=infoself','il_body');"><img src="/images/account.gif" border=0></a></td>
	            </tr>
	             <tr>
	              <td  align="center" class="loginFont" >
	                <a href="#04"  onclick="winhrefOT('/selfservice/selfinfo/addselfinfo.do?b_add=add&a0100=A0100&userbase=<%=userView.getDbname()%>&i9999=I9999&actiontype=update&setname=A01&flag=infoself','il_body');"><font id="a002" class="menu_a" >信息维护</font></a>
	              </td>
	            </tr> 
	        </hrms:priv>   
	        <hrms:priv func_id="010401">         
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/searchpropose.do?b_add=link&ctrl_return=0" target="il_body" function_id="xxx"><img src="/images/addrnote_set.gif" border=0></hrms:link></td>
	            </tr>              
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/selfservice/propose/searchpropose.do?b_add=link&ctrl_return=0" target="il_body" function_id="xxx"><font id="a001" class="menu_a">发表意见</font></hrms:link>
	              </td>                
	            </tr>  
	        </hrms:priv>
	        <hrms:priv func_id="0105">                
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/workdiary/index.jsp" target="il_body" function_id="xxx"><img src="/images/jh.gif" border=0></hrms:link></td>
	            </tr>              
	            <tr>
	              <td  align="center" class="loginFont" >
	              	<hrms:link href="/performance/workdiary/index.jsp" target="il_body" function_id="xxx"><font id="a001" class="menu_a">我的日志</font></hrms:link> 
	              </td>                
	            </tr>                            
	      </hrms:priv>
		 <hrms:priv func_id="01030106">        
	      <%
	        inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
			approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
						  
			if(inputchinfor.equals("1")&&approveflag.equals("1")&&!userView.getUserName().equalsIgnoreCase("su")){
	      %>     
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/selfinfo/inforchange.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/jh.gif" border=0></hrms:link></td>
	            </tr>              
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/selfservice/selfinfo/inforchange.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">我的变动信息明细</font></hrms:link>
	              </td>                
	            </tr>                            
	       <%}%>
		</hrms:priv>
		<hrms:priv func_id="0108">   
		 		<tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/impev/importantev.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/jh.gif" border="0"></hrms:link></td>
	            </tr>              
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/general/impev/importantev.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">重要信息报告</font></hrms:link>
	              </td>                
	            </tr>  
	      </hrms:priv>     
	     </table>
	    </form>
	   </div>
	  </td>
	 </tr>
	</table>
</td>

</tr>
</table>


<script language="javascript">
	showFirst();
	/*
  var whichOpen=menuTitle1;
  var whichContinue="";
  document.all.menu1.style.height =divHeight;
  document.all.menu1.style.display="block";
  parent.frames[1].name = "il_body"; */
</script>  

                                                                              