<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
	if(session.getAttribute("perIndexForm")==null)
	{
		
	}
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    int status=0;
	status=userView.getStatus();    
	if(userView != null){
	  userName = userView.getUserId();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
  
	}
	String date = DateStyle.getSystemDate().getDateString();
%>
<STYLE type=text/css>.alpha {
	FILTER: Alpha(Opacity=80)
}
.td1 {
	FONT-SIZE: 14px
}
.td2 {
	BACKGROUND-COLOR: #ccffff; CURSOR: hand; FONT-SIZE: 14px
}
.maskl {
	OVERFLOW: hidden
}
.cardtitle {
	BORDER-BOTTOM: black 1px solid; BORDER-LEFT: black 0px solid; BORDER-RIGHT: black 1px solid; BORDER-TOP: black 1px solid; CURSOR: default; FONT-SIZE: 14px; TEXT-INDENT: 10pt
}
.cardbottom {
	BACKGROUND-COLOR: #99ccff; BORDER-BOTTOM: black 1px solid; BORDER-LEFT: black 1px solid; BORDER-RIGHT: black 1px solid; BORDER-TOP: black 0px solid; FILTER: Alpha(Opacity=90)
}
.font1 {
	font-family: "宋体";
	font-size: 14px;
}
A:link {
	COLOR: #1B4A98; TEXT-DECORATION: none;font-size: 14px	
}
A:visited {
	COLOR: #1B4A98; TEXT-DECORATION: none;font-size: 14px
}
A:active {
	COLOR: #1B4A98;TEXT-DECORATION: none;font-size: 14px
}
A:hover {
	COLOR: #E39E19; TEXT-DECORATION:none;font-size: 14px
}
</STYLE>

<SCRIPT language=Jscript>
//Copyright (C) 2001 DarkVn. /Mail:darkvn@blueidea.com
//建议使用IE5.0以上应用本代码.
//****************************************************
//用数组来存储多个timeOut标识.
tBack=new Array(5);
tOut=new Array(5);
//激活当前选项卡.
function menuOut(whichMenu){
var curMenu=eval("menu"+whichMenu);
	curMenu.runtimeStyle.zIndex=6;
	clearTimeout(tBack[whichMenu]);
	moveOut(whichMenu);
}
//恢复初始状态.
function menuBack(whichMenu){
var curMenu=eval("menu"+whichMenu);
	curMenu.runtimeStyle.zIndex=curMenu.style.zIndex;
	clearTimeout(tOut[whichMenu]);
	moveBack(whichMenu);
}
//移动当前选项卡
function moveOut(curNum){
var	curMenu=eval("menu"+curNum);
	if(curMenu.style.posLeft<0) {
		curMenu.style.posLeft+=5;
		tOut[curNum]=setTimeout("moveOut('"+curNum+"')",1);
		}
}
//移回选项卡.
function moveBack(curNum){
var	curMenu=eval("menu"+curNum);
//alert(curMenu.style.posLeft);
	if(curMenu.style.posLeft>-200) {
		curMenu.style.posLeft-=5;
		tBack[curNum]=setTimeout("moveBack('"+curNum+"')",1);
		}

}
//鼠标移过时改变表格单元式样。
function swapClass(){
var o=event.srcElement;
	if(o.className=="td1") o.className="td2"
		else if(o.className=="td2") o.className="td1";
}
document.onmouseover=swapClass;
document.onmouseout=swapClass;
function openWindowMax(target_url)
{	
   newwindow=window.open(target_url,'big',"top=0,left=0,width=" + window.screen.width + ",height=" + window.screen.height); 
}
function openWindowMax1(target_url)
{	
   newwindow=window.open(target_url,'big',"top=0,left=0,width=" + (window.screen.width-100) + ",height=" + (window.screen.height-140)); 
}
</SCRIPT>
<html>
<head>

<title>人力资源信息管理系统　用户名：<%=userName%>　当前日期：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
</head>
<body topmargin="0" bottommargin="0" marginheight="0" style="margin:0 0 0 0" bgcolor="#A3C7E1">
<html:form action="/selfservice/per/per_index" method="post">
<table width="998"  align="center" height="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="#A3C7E1">
  <tr>
	<td background="/images/062_02.jpg" width="100%" height="288" valign="top" align="left" style="background-repeat: no-repeat">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
       <td width="5%"></td>
        <td width="25%" height="288" valign="baseline">
		  <table width="100%" border="0" cellspacing="0" cellpadding="0">
           <tr>
           <td width="100" valign="bottom">
		   <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                      <td height="50">&nbsp;</td>
                 </tr>
                   <tr>
                <td height="35">&nbsp; 
		 		 <% if(status==4){%>                
                <hrms:priv func_id="010101" module_id="0">
                <a href="###" onclick="openWindowMax('/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=<%=userView.getDbname()%>&flag=infoself')"><img src="/images/06-04.jpg" width="100" height="32" border="0"></a> 
                </hrms:priv>
                <%}%>
                </td>
                 </tr>				
		<tr>
               <td>&nbsp;</td>
               </tr>
                <tr>
                <td height="35">&nbsp;
                <hrms:priv func_id="010301">
                  <a href="###" onclick="openWindowMax('/selfservice/selfinfo/addselfinfo.do?b_add=add&a0100=A0100&userbase=<%=userView.getDbname()%>&i9999=I9999&actiontype=update&setname=A01&flag=infoself');">
                    <img src="/images/06-056.jpg" width="100" height="32" border="0">
                  </a>
                </hrms:priv>
                </td>
               </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>  
                 <tr>
                   <td height="35">&nbsp;
                   <hrms:priv func_id="06" module_id="3">
                   <% if(status==4){%>   
					   <a href="###" onClick="menuOut(2)">
					   <img src="/images/06-05.jpg" width="100" height="32" border="0">
					   </a>
                   <%}%>	
                   </hrms:priv>				   
		   </td>
	         </tr>
	        
                 <tr>
                    <td>&nbsp;</td>
                 </tr>                
                <tr>
                <td  height="35">&nbsp;
                 <hrms:priv func_id="060604"> 
                  <a href="###" onclick="openWindowMax('/performance/markStatus/markStatusList.do?b_search=link&model=0');">
                    <img src="/images/06-06.jpg" width="100" height="32" border="0">
                  </a>
                  </hrms:priv>
                </td>
               </tr>
               
               <tr>
             <td>&nbsp;</td>
            </tr>
		  
                <tr>
                <td height="35">&nbsp;
                <hrms:priv func_id="010401">
                  <a href="###" onclick="openWindowMax('/selfservice/propose/searchpropose.do?b_add=link&ctrl_return=0');">
                    <img src="/images/06_055.jpg" width="100" height="32" border="0">
                  </a>
                </hrms:priv>
                </td>
               </tr>
                			
           </table></td>
           <td>
		   <!--层-->
<DIV class=maskl id=menuPos style="HEIGHT: 506px; LEFT: 160px; POSITION: absolute; TOP: 59px; WIDTH: 210px;">
  <DIV id=menu2 onmouseout=menuBack(2) onmouseover=menuOut(2) 
style="HEIGHT: 20px; LEFT: -200px; aling:left; POSITION: absolute; TOP: 100px; WIDTH: 140px; Z-INDEX: 1">

   <TABLE border=0 cellPadding=0 cellSpacing=0 width=200 class=cardtitle>
    <TBODY>
    <hrms:priv func_id="060601"> 
       <logic:iterate id="element" name="perIndexForm" property="dblist">
          <TR>    
           <TD bgColor=#99ccff  height=18 width=200>
           	<logic:notEqual name="element" property="dataValue" value="0">
           	  <a href="###" onclick="openWindowMax('/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&operate=aaa${element.dataValue}');">
              <bean:write  name="element" property="dataName"/>
              </a>
           	</logic:notEqual>
           	<logic:equal name="element" property="dataValue" value="0">
           	  <bean:write  name="element" property="dataName"/>
           	</logic:equal>
          </TD>
	</TR>
       </logic:iterate>	 
     </hrms:priv>        
      </TBODY>
    </TABLE>
   </DIV>   
  </DIV>
</td>
 </tr>
</table>

		</td>
		
        <td width="70%"></td>
       </tr>
     </table>
    </td>
	</tr>
	<tr>
	    <td background="/images/062_03.jpg" width="100%" height="202" valign="top" align="left" style="background-repeat: no-repeat;">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		   <tr>
		        <td width="5%"></td>
		         <td width="25%"  valign="baseline">
		          <table width="100%" border="0" cellspacing="0" cellpadding="0">
		           <tr>
               <td>&nbsp;</td>
               </tr>
		            <tr>
		             <td height="35">&nbsp;
                      <hrms:priv func_id="060802">
                       <a href="###" onclick="openWindowMax1('/performance/achivement/achivementTask.do?br_init=int&ctrl_return=0');">
                     <img src="/images/06-10.jpg" width="100" height="32" border="0"> 
                       <!--业绩任务书-->
                      </a>
                     </hrms:priv>
                    </td>
                   </tr>
                   <tr>
               <td>&nbsp;</td>
               </tr>
                   <tr>
                    <td height="35">&nbsp;
                     <hrms:priv func_id="060802">
                     <a href="###" onclick="openWindowMax1('/performance/achivement/dataCollection/khplanMenu.do?b_query=link&ctrl_return=0');">
                     <img src="/images/06-11.jpg" width="100" height="32" border="0"> 
                       <!--业绩数据录入-->
                     </a>
                    </hrms:priv>
                    </td>
                    </tr>
		         </table>
		       </td>
		      <td width="70%"></td>
           </tr>
		</table>
			</td>
	</tr>
</table>
</html:form >
</body>
</html>