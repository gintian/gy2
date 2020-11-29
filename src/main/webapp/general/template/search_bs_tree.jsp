<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.HashMap"%>
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes></hrms:themes>
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<logic:equal name="templateForm" property="bostype" value="true">
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

/*权限控制时，可能menuTitle1第一个不出现，js会出错*/
function showFirst(frmname)
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

  if((window.screenTop==128||window.screenTop==127)||window.screenTop==165||window.screenTop==91)//fullscreen status
  {
	   divHeight = window.screen.availHeight - window.screenTop -(j*25+21);  
	   if(ver=="6.0")
	      divHeight=divHeight+10;
  }
  else
  {
	   divHeight = window.screen.availHeight - window.screenTop -(j*25+21+20); //add other height
	   if(ver=="6.0")
	      divHeight=divHeight-10;
  }
  /*一个都没有*/
  if(idx=="0")
  	return;
  whichOpen=eval("menuTitle"+idx); //menuTitle1
  whichContinue="";
  var menu=eval("menu"+idx);
  var tab=document.getElementById("DetailTable");
  var rows = tab.rows.length;
  var externalheight=0;///如果文字过多，就会换行，那么高度就不够。需要算出有几行换行了。  郭峰
  var re=/<a[^>]*href=['"]([^"]*)['"].*?[^>]*>(.*?)<\/a>/g;
  for(var n=1;n<rows;n=n+2)
  {
		var str=tab.rows[n].cells[0].innerHTML.toLowerCase();
		while(re.exec(str)!=null)
		{  
	      var innervalue=RegExp.$2;///超链接中的文本
	      var valuelength=parseInt(innervalue.length);///文本的长度
	      var linecount=parseInt(valuelength/12);///1行最多11个字符。
	      externalheight+=parseInt(linecount);
	    }
  }
  
  menu.style.height =rows/2*71+externalheight*21+12;
  menu.style.display="block";
  if(frmname!="hl"&&frmname!="hcm")
     parent.frames[1].name = "il_body";

}
</SCRIPT>
</logic:equal>

<html:form action="/general/template/search_bs_tree">
<logic:equal name="templateForm" property="bostype" value="true">
<body style="margin:0 0 0 0;" leftmargin="0" topmargin="0" marginwidth="0" >
  <table cellpadding=0 cellspacing=0 width="111%">
   <tr>
    <td id="menucol">  
      <hrms:opensealmodule staticid="${templateForm.openseal}"></hrms:opensealmodule>
    </td>
   </tr>
 </table>
 </body>
</logic:equal>
<logic:notEqual name="templateForm" property="bostype" value="true">
<body leftmargin="0" topmargin="0" marginwidth="0" >
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
		 	            
         <tr>
           <td align="left"> 
             <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="templateForm" property="bs_tree" filter="false"/>
             </SCRIPT>
             </div> 
          
           </td>
           </tr>           
    </table> 
   <script type="text/javascript">
	root.expandAll();
	
	// v:  T->显示机构树  N->不显示机构树
	function showTemplateList(v,tabid)
	{
		  parent.menupnl.toggleCollapse(false);
		  parent.mil_body.location='/general/template/templatelist.do?b_init=init&returnflag=list&tabid='+tabid;	
	}
	
   </script>
   </body>
</logic:notEqual>
</html:form>

<logic:equal name="templateForm" property="bostype" value="true">
  <script language="javascript">
	showFirst("<%=flag%>");
</script>   
</logic:equal>