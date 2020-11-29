<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag=userView.getBosflag();
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
   
   function turn()
   {
    var menucolobj=document.getElementById("menucol"); 
	var menusplit=document.getElementById("split");     
	if(parent.forum.cols != '8,*')
	{
		parent.forum.cols = '8,*';
		menucolobj.style.display="none";
		menusplit.src="/images/right_arrow.gif";
		menusplit.alt='open';		
	}
	else
	{
		parent.forum.cols = '170,*';
		menucolobj.style.display="";
		menusplit.src="/images/left_arrow.gif";
		menusplit.alt='close';		
	}
   }   
   
function showFirstM()
{
  var subTables=document.getElementsByTagName("table");
  var idx="0";
  var j=0;
  for(var i=0;i<subTables.length;i++)
  {
   		if(subTables[i].className!="menu_table")
      		continue;
      	idx=subTables[i].getAttribute("index");
		break;
  }
  //get second menuitem count.
  for(var i=0;i<subTables.length;i++)
  {
   		if(subTables[i].className!="menu_table")
      		continue;
      	j=j+1;
  }  
  
  var ver=getIEVersion();

  if((window.screenTop==128||window.screenTop==127)||window.screenTop==165)//fullscreen status
  {
	   divHeight = window.screen.availHeight - window.screenTop -(j*28+21);  
	   if(ver=="6.0")
	      divHeight=divHeight+10;
  }
  else
  {
	   divHeight = window.screen.availHeight - window.screenTop -(j*28+21+20); //add other height
	   if(ver=="6.0")
	      divHeight=divHeight-10;
  }
  /*一个都没有*/
  if(idx=="0")
  	return;
  whichOpen=eval("menuTitle"+idx); //menuTitle1
  whichContinue="";
  var menu=eval("menu"+idx);
  menu.style.height =divHeight;
  menu.style.display="block";
}   
</SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<body class=menuBodySet style="margin:0 0 0 0" leftmargin="0" topmargin="0" marginwidth="0" >
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">  

<html:form action="/general/template/search_bs_tree">
   <hrms:opensealmodule staticid="${templateForm.openseal}"></hrms:opensealmodule>
</html:form>

</td>
</tr>
</table>
</body>
<script language="javascript">
	showFirst("<%=flag%>");
</script>   
