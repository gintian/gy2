<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>

<%@ page import="com.hrms.struts.constant.SystemConfig" %>
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
	
	
	String etoken=PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userView.getUserName()+","+userView.getPassWord()));
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
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

 
<table cellpadding=0 cellspacing=0 width=169 class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle1 ><span>统计分析</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:expression(document.body.offsetHeight);filter:alpha(Opacity=100);overflow:hidden;"  id=menu1> 
   <form name="static" action="" method=post target="il_menu">
     <table cellpadding=2 cellspacing=3 align=center width=169  class="DetailTable" style="position:relative;top:10px;">
     	<hrms:priv func_id="040101">  
            <tr><!-- /workbench/stat/statshow.do?b_ini=link&infokind=1 -->
              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0" target="il_body" onclick="turn();"><img src="/images/lstatic.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0" target="il_body" onclick="turn();"><font id="a004" class="menu_a">统计分析</font></a></td>
            </tr>
        </hrms:priv> 
        <hrms:priv func_id="040102">  
           <%if(SystemConfig.getPropertyValue("historyDataAnalyseHostUrl")!=null&&SystemConfig.getPropertyValue("historyDataAnalyseHostUrl").length()>0){  %>
            <tr>
              <td  align="center" class="loginFont" ><a href="###" onclick="showHistoryData();"><img src="/images/bjbb.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="###" onclick="showHistoryData();"><font id="a004" class="menu_a">数据简报</font></a></td>
            </tr>
           <%}else{ %>
              <tr>
                <td  align="center" class="loginFont" ><a href="/general/static/history/statshow.do?b_tree=link&target=mil_body&action=" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0 ></a></td>
              </tr>
              <tr>
                <td  align="center" class="loginFont" ><a href="/general/static/history/statshow.do?b_tree=link&target=mil_body&action=" target="il_body" onclick="turn();"><font id="a004" class="menu_a">历史数据</font></a></td>
              </tr>
           <%} %>
        </hrms:priv>  
        
        
         <%
          //为中建加的 20100317
          if(SystemConfig.getPropertyValue("reportAnalyseHostUrl")!=null&&SystemConfig.getPropertyValue("reportAnalyseHostUrl").length()>0){ %> 
            <tr>
				<td align="center" class="loginFont">
											<hrms:link href="###" target="_self"   onclick="showReporAnalyse();">
												<img src="/images/mc.gif" border=0>
											</hrms:link>
				</td>
			</tr>
			<tr>
				<td align="center" class="loginFont">
											<hrms:link href="###" target="_self" onclick="showReporAnalyse();">
												<font id="a001" class="menu_a">历年报表</font>
											</hrms:link>
				</td>
			</tr>
            <% } %>                  

     </table>
     
     
     
   </form>
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
  
  
  function showReporAnalyse()
  {
  	url="<%=(SystemConfig.getPropertyValue("reportAnalyseHostUrl"))%>/report/report_analyse/reportunittree.do?validatepwd=false&appfwd=1&etoken=<%=etoken%>";
  	window.open(url,"_blank","status=no,resizable=no,toolbar=no,menubar=no,location=no,top=0,left=0,width="+(window.screen.availWidth-10)+",height="+(window.screen.availHeight-30));
  }
  function showHistoryData()
  {
     url="<%=(SystemConfig.getPropertyValue("historyDataAnalyseHostUrl"))%>/general/static/history/statshow.do?b_tree=link&target=mil_body&action=&validatepwd=false&appfwd=1&etoken=<%=etoken%>";
  	 window.open(url,"_blank","status=no,resizable=no,toolbar=no,menubar=no,location=no,top=0,left=0,width="+(window.screen.availWidth-10)+",height="+(window.screen.availHeight-30));
  }
  
</script>  

                                                                              