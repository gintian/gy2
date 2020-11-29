<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter"%>
<%@ page import="com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo"%>
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
	DirectUpperPosBo bo=new DirectUpperPosBo();
	String flag=bo.getGradeFashion("0");
	userView.getHm().put("gradeFashion",flag);
	String batchGradeUrl="";
	if(flag.equals("1"))
		batchGradeUrl="/selfservice/performance/batchGrade.do?b_query=link&model=0";
	else
		batchGradeUrl="/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=1&planContext=all";
	
	Connection connection=null;
	String browse_photo="";
	try
	{
	   connection = (Connection) AdminDb.getConnection();
	   Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(connection);
	   browse_photo=sysbo.getValue(Sys_Oth_Parameter.BROWSE_PHOTO);	
	   browse_photo=browse_photo!=null&&browse_photo.length()>0?browse_photo:"0";//0默认为表格信息，1照片显示	
	}catch(Exception e)
	{
	}finally
	{
	  if(connection!=null)
	   connection.close();	
	}
	 
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<script type="text/javascript" src="/js/constant.js"></script>
<style type="text/css">
<!--
.tabtext {
	font-family: "宋体";
	font-size: 14px;
	font-style: normal;
	font-weight: 900;
	text-decoration: none;
}
.menu_a
{
   font-family: "宋体";
	font-size: 14px;
	font-style: normal;	
	font-weight: 550;
	text-decoration: none;
}
-->
</style>
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
   
   function turn()
   {
	   parent.menupnl.toggleCollapse(false);
   }   
   
   function showdiv1()
   {
      var mybody = document.getElementById("menu4");
      if (!document.getElementById("popupAddr")){      
       //创建弹出内容层
       var popupDiv = document.createElement("div");
        //给这个元素设置属性与样式
       popupDiv.setAttribute("id","popupAddr")
       popupDiv.style.position = "relative";
       popupDiv.style.border = "1px solid #ccc";
       popupDiv.style.background = "#DEEAF5";
       popupDiv.style.zIndex = 99;
       //创建弹出背景层
       var bodyBack = document.createElement("div");
       bodyBack.setAttribute("id","bodybg")
       bodyBack.style.position = "relative";
       bodyBack.style.width = "100%";
       bodyBack.style.height = mybody.offsetHeight;
       bodyBack.style.zIndex = 98;
       bodyBack.style.top = 0-mybody.offsetHeight;
       bodyBack.style.left = 0;
       bodyBack.style.filter = "alpha(opacity=50)";
       bodyBack.style.opacity = 0.5;
       bodyBack.style.background = "#ddf";
       //实现弹出(插入到目标元素之后)
       insertAfter(popupDiv,mybody);//执行函数insertAfter()
       insertAfter(bodyBack,mybody);//执行函数insertAfter()
    }
    //显示背景层
    document.getElementById("bodybg").style.display = "";
    var popObj=document.getElementById("popupAddr");;
    popObj.innerHTML =document.getElementById("jcfx").innerHTML;
    popObj.style.display = "";
    popObj.style.height = mybody.offsetHeight;
    popObj.style.top=0-mybody.offsetHeight-mybody.offsetHeight;
    popObj.style.left=0;
}   
//关闭弹出层
function closeLayer(){
if(document.getElementById("popupAddr"))
  document.getElementById("popupAddr").style.display = "none";
if(document.getElementById("bodybg"))  
  document.getElementById("bodybg").style.display = "none";
return false;
}
function insertAfter(newElement,targetElement){//插入
var parent = targetElement.parentNode;
if(parent.lastChild == targetElement){
parent.appendChild(newElement);
}
else{
parent.insertBefore(newElement,targetElement.nextSibling);
}
}
function showdiv()
{
   var display=document.getElementById("jcfx").style.display;
   if(display=="none")
      document.getElementById("jcfx").style.display="block";
   else
      document.getElementById("jcfx").style.display="none";
}
function closediv()
{
   document.getElementById("jcfx").style.display="none";
}
function winopenTO(href,target)
{
   window.open(href,target);
}
function mouseOverDis(el,str) {
	e = document.getElementById(el);
	if (str == "ZYXY_BASEINFO") {
		str = ZYXY_BASEINFO;
	}
	if (str == "ZYXY_POSITIONINFO") {
		str = ZYXY_POSITIONINFO;
	}
	if (str == "ZYXY_JIXIAOINFO") {
		str = ZYXY_JIXIAOINFO;
	}
	if (str == "ZYXY_WORKINFO") {
		str = ZYXY_WORKINFO;
	}
	if (str == "ZYXY_PROFESSIONALCREDIT") {
		str = ZYXY_PROFESSIONALCREDIT;
	}
	window.parent.getMouseXY(e,str);
}
function mouseOutDis() {
	window.parent.hiddenDesc();
}
function menuChangeSelf(menu,menuTitle,arrow)
{
    var menu_obj=document.getElementById(menu);
    var menuTitle_obj=document.getElementById(menuTitle);
    var arrow_obj=document.getElementById(arrow);
    menuChange(menu_obj,divHeight,menuTitle_obj,arrow_obj)
}
</SCRIPT>
<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="188" id="menucol">  
  <hrms:priv func_id="5101,030401,5100,010201,030401,030201">    
  <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);winopenTO('/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=Usr&flag=infoself','il_body');">
	    <span id="baseinfo" onmouseover="mouseOverDis('baseinfo','ZYXY_BASEINFO')" onmouseout="mouseOutDis()"><span id=arrow1><img src="/images/darrow.gif" border=0></span><font size='3'>基础信息</font></span>
	    </td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="5100">
		        <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=Usr&flag=infoself" target="il_body"><img src="/images/browser.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=Usr&flag=infoself" target="il_body" ><font id="a001" class="menu_a">我的信息</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="5101">
	            <tr>
	            <td  align="center" class="loginFont" >
	             <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><img src="/images/cx.gif" border=0></hrms:link>
	            </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">队伍信息</font></hrms:link>
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
	            <hrms:priv func_id="030201">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/ykcard/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><img src="/images/jhhs.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/ykcard/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><font id="a002" class="menu_a" >员工薪酬</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>   
	            <hrms:priv func_id="030401">
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/info/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&flag=noself" target="il_body" onclick="turn();"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/info/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&flag=noself" target="il_body" onclick="turn();"><font id="a001" class="menu_a">信息维护</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	             
	          </table>
	      </div>
	    </td>
	  </tr>
	</table>
    </hrms:priv>
  
  <hrms:priv func_id="5200,5201">   
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);winopenTO('/pos/roleinfo/pos_priv_info.do?b_search=link&table=k00','il_body');"><span id="positioninfo" onmouseover="mouseOverDis('positioninfo','ZYXY_POSITIONINFO')" onmouseout="mouseOutDis()"><span id=arrow2><img src="/images/darrow.gif" border=0></span><font size='3'>职位说明</font></span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
               <hrms:priv func_id="5200">	                      	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <a href="/pos/roleinfo/pos_roleinfo_tree.do?b_search=link&modular=Z" target="il_body"><img src="/images/hmuster.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/pos/roleinfo/pos_roleinfo_tree.do?b_search=link&modular=Z" target="il_body"><font id="a001" class="menu_a">全员职位说明书</font></a>
	              </td>
	            </tr>
                </hrms:priv>	
               <hrms:priv func_id="5201">	                              
	            <tr>
	              <td  align="center" class="loginFont" >
	               <a href="/pos/roleinfo/pos_priv_info.do?b_search=link&table=k00" target="il_body"><img src="/images/hmuster.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/pos/roleinfo/pos_priv_info.do?b_search=link&table=k00" target="il_body"><font id="a001" class="menu_a">我的职位说明书</font></a>
	              </td>
	            </tr>
	            </hrms:priv>		                     
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
    </hrms:priv> 
    
    <hrms:priv func_id="57">   
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="7">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);winopenTO('/templates/attestation/police/wizard.do?br_work_wizard=link','il_body');">
	    <span id="workinfo" onmouseover="mouseOverDis('workinfo','ZYXY_WORKINFO')" onmouseout="mouseOutDis()"><span id=arrow7><img src="/images/darrow.gif" border=0></span><font size='3'>工作信息</font></span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display:none;"  id=menu7> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">	           
               <hrms:priv func_id="5701">  	           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/pos/police/work_system.do?br_search=link" target="il_body"><img src="/images/dmwh.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/pos/police/work_system.do?br_search=link" target="il_body"><font id="a001" class="menu_a">工作制度</font></a>
	              </td>
	            </tr>
	            </hrms:priv>
               <hrms:priv func_id="5702">	                      	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               	<a href="/pos/police/jqdt_tree.do?b_search2=link&cyclename=yqdt&backdate=&treetype=vorg&kind=2&target=mil_body&loadtype=1" target="il_body"><img src="/images/hmuster.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              	<a href="/pos/police/jqdt_tree.do?b_search2=link&cyclename=yqdt&backdate=&treetype=vorg&kind=2&target=mil_body&loadtype=1" target="il_body"><font id="a001" class="menu_a">狱情动态</font></a>
	              </td>
	            </tr>
                </hrms:priv>	
               	<hrms:priv func_id="5703">	                      	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               	<a href="/pos/police/bujqdt_tree.do?b_search=link&cyclename=dept" target="il_body"><img src="/images/hmc.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              	<a href="/pos/police/bujqdt_tree.do?b_search=link&cyclename=dept" target="il_body"><font id="a001" class="menu_a">部门工作任务书</font></a>
	              </td>
	            </tr>
                </hrms:priv>
               	<hrms:priv func_id="5704">	                      	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               	<a href="/pos/police/person.do?b_search=link&tofirst=yes" target="il_body"><img src="/images/edit_info.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              	<a href="/pos/police/person.do?b_search=link&tofirst=yes" target="il_body"><font id="a001" class="menu_a">个人工作任务书</font></a>
	              </td>
	            </tr>
                </hrms:priv>            
	             <hrms:priv func_id="5705">	
	            <tr>
	              <td  align="center" class="loginFont" >
	               <a href="/pos/police/jqdt_tree.do?b_search=link&cyclename=team" target="il_body"><img src="/images/hmc.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/pos/police/jqdt_tree.do?b_search=link&cyclename=team" target="il_body"><font id="a001" class="menu_a">队伍工作任务书</font></a>
	              </td>
	            </tr>
	            </hrms:priv> 
	             <hrms:priv func_id="5706">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/police/task_file.do?b_search=link&type=police&flag=3" target="il_body"><img src="/images/jhhs.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/police/task_file.do?b_search=link&type=police&flag=3" target="il_body"><font id="a002" class="menu_a" >专项教育活动</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>   
	            <hrms:priv func_id="5707">
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/roleinfo/pos_roleinfo_tree.do?b_search=link&modular=L" target="il_body"><img src="/images/jh.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/pos/roleinfo/pos_roleinfo_tree.do?b_search=link&modular=L" target="il_body"><font id="a001" class="menu_a">廉政风险防范</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	                      	            
	      </table>
	   </div>
	 </td>
	  </tr>
	</table>
    </hrms:priv>   
    
  <hrms:priv func_id="58">     
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);winopenTO('/templates/attestation/police/wizard.do?br_performance=link','il_body');">
	    <span id="jixiaoinfo" onmouseover="mouseOverDis('jixiaoinfo','ZYXY_JIXIAOINFO')" onmouseout="mouseOutDis()"><span id=arrow3><img src="/images/darrow.gif" border=0></span><font size='3'>绩效考评</font></span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
               <hrms:priv func_id="5801">              
	             <tr>
	              <td  align="center" class="loginFont" >
	            <hrms:link href="<%=batchGradeUrl%>" target="il_body" function_id="xxx"><img src="/images/flow_upload.gif" border=0 ></hrms:link> 
	             	 	  <!--	 <a href="javascript:fullwin()" ><img src="/images/flow_upload.gif" border=0 ></a>  -->
	              
	              </td>
	            </tr>
	            <tr>
	            　<td  align="center" class="loginFont" >
					 <!-- <a href="javascript:fullwin()" ><font id="a001" class="menu_a">多人考评</font></a>  -->
				      <hrms:link href="<%=batchGradeUrl%>" target="il_body" function_id="xxx"><font id="a001" class="menu_a">定性考核</font></hrms:link>
	            　</td>
	            </tr> 
	          </hrms:priv>
               <hrms:priv func_id="5802"> 
				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/achivement/achivementTask.do?br_init=int" target="il_body" ><img src="/images/hmuster.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/achivement/achivementTask.do?br_init=int" target="il_body" ><font id="a004" class="menu_a">定量考核</font></a></td>
	            </tr>
	            </hrms:priv>
	  			<hrms:priv func_id="5803"> 
		  			<tr>
					  <td  align="center" class="loginFont" ><a href="/performance/achivement/dataCollection/khplanMenu.do?b_query=link" target="il_body" ><img src="/images/jh.gif" border=0 ></a></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/performance/achivement/dataCollection/khplanMenu.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">定量目标</font></a></td>
		            </tr>
		        </hrms:priv>               
	       </table>
	   </div>
	 </td>
	  </tr>
	</table>
    </hrms:priv>        
    <hrms:priv func_id="54">     
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="4">      
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);closediv();winopenTO('/templates/attestation/police/wizard.do?br_postwizard=link','il_body');">
	    <span id="professinalcredit" onmouseover="mouseOverDis('professinalcredit','ZYXY_PROFESSIONALCREDIT')" onmouseout="mouseOutDis()"><span id=arrow4><img src="/images/darrow.gif" border=0></span><font size='3'>职业信用</font></span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
               <hrms:priv func_id="5401">  	           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/totalrank/totalrank.do?b_query=link&model=1" target="il_body" onclick="turn();"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/performance/totalrank/totalrank.do?b_query=link&model=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">综合评定</font></a>
	              </td>
	            </tr>
	            </hrms:priv>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="###" onclick="showdiv();"><img src="/images/organization.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="###" onclick="showdiv();"><font id="a001" class="menu_a">决策分析</font></a>
	              </td>
	            </tr>    
	            <tr>
	              <td>
	                <div id="jcfx" style="display:none">
                      <div id="tab">
                       <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable">
                        <hrms:priv func_id="5402">  	 
          		         <tr>
	                       <td  align="center" class="loginFont" ><a href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body" onclick="turn();"><img src="/images/cx.gif" border=0></a></td>
	                     </tr>
	                     <tr>
	                      <td  align="center" class="loginFont" >
	                        <a href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a" color="#CC3333">统计参考信息</font></a>
	                      </td>
	                     </tr>	
	                    </hrms:priv>	          	
                        <hrms:priv func_id="5403"> 	          	
	          	         <tr>
	                      <td  align="center" class="loginFont" ><a href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body" onclick="turn();"><img src="/images/per_result.gif" border=0></a></td>
	                     </tr>
	                     <tr>
	                      <td  align="center" class="loginFont" >
	                       <a href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a" color="#CC3333">全员图表分析</font></a>
	                      </td>
	                     </tr>	
	                    </hrms:priv>
                        <hrms:priv func_id="5404"> 		            		            
	                     <tr>
	                      <td  align="center" class="loginFont" ><a href="/performance/kh_result/kh_plan_list.do?b_init=link&model=0&distinctionFlag=0&opt=1" target="il_body" onclick="turn();" ><img src="/images/per_result.gif" border=0></a></td>
	                     </tr>
	                     <tr>
	                       <td  align="center" class="loginFont" >
	                        <a href="/performance/kh_result/kh_plan_list.do?b_init=link&model=0&distinctionFlag=0&opt=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a" color="#CC3333">我的图表分析</font></a>
	                       </td>
	                     </tr>
	                     </hrms:priv>
	                  </table>
                     </div>
                    </div>     
	              </td>
	              
	            </tr>          	          	
                <hrms:priv func_id="5405"> 		          	
	          	 <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/totalrank/totalrank.do?b_query=link&model=2" target="il_body" onclick="turn();"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/performance/totalrank/totalrank.do?b_query=link&model=2" target="il_body" onclick="turn();"><font id="a001" class="menu_a">查询使用</font></a>
	              </td>
	            </tr>	
	            </hrms:priv>		
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
    </hrms:priv> 
    <hrms:priv func_id="55">          
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span><font size='3'>客&nbsp;户&nbsp;化</font></span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	     	  <hrms:priv func_id="080101">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/system/security/rolesearch.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/system/security/rolesearch.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">角色管理</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv> 
	     	  <hrms:priv func_id="080201"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/logonuser/search_user_tree.do?b_query=link" target="il_body" onclick="turn();"><img src="/images/organization.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/logonuser/search_user_tree.do?b_query=link" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >用户管理</font></a></td>
	            </tr>
	          </hrms:priv> 
	     	  <hrms:priv func_id="080301">                          
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/system/security/assign_login0.do" target="il_body" ><img src="/images/account.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/system/security/assign_login0.do" target="il_body" ><font id="a002" class="menu_a" >帐号分配</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv>
	     	  <hrms:priv func_id="080701">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/assign_login1.do" target="il_body" ><img src="/images/admin_pwd.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/assign_login1.do" target="il_body" ><font id="a004" class="menu_a">角色快速分配</font></a></td>
	            </tr>
	          </hrms:priv>            
	     	  <hrms:priv func_id="080501">               
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_base_options.do?b_query=link" target="il_body" ><img src="/images/bx.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_base_options.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >认证应用库</font></a></td>
	            </tr>
	          </hrms:priv>
	     	  <hrms:priv func_id="080601">          
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_username_options.do?b_query=link" target="il_body" ><img src="/images/admin_pwd.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_username_options.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >认证用户名</font></a></td>
	            </tr>  
	          </hrms:priv>           
		      
	          <hrms:priv func_id="5501">
		        <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/totalrank/setfield.do?b_query=link" target="il_body"><img src="/images/rzfs.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/totalrank/setfield.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">参数设置</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="5502">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/setdate/setdate.do?b_query=link&save=search" target="il_body"><img src="/images/rzfs.gif" border="0"></hrms:link></td>
	             </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/setdate/setdate.do?b_query=link&save=search" target="il_body" ><font id="a001" class="menu_a">周期设置</font></hrms:link>
	              </td>
	             </tr>
	             </hrms:priv>
	             <hrms:priv func_id="5503">                     
	               <tr>
	                 <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=4" target="il_body" function_id="xxx"><img src="/images/flow_upload.gif" border=0 ></hrms:link></td>
	               </tr>
	               
	               <tr>
	               <td  align="center" class="loginFont" >
	                <hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=4" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >流程文件</font></hrms:link>
	               </td>
	              </tr> 
	          </hrms:priv> 
	          <hrms:priv func_id="5504">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/cardconstantset.do?b_cardset=set" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/cardconstantset.do?b_cardset=set" target="il_body" ><font id="a006" class="menu_a" >薪酬表设置</font></a></td>
	            </tr> 
	          </hrms:priv>
	          <hrms:priv func_id="5505">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/setyqdt/setyqdt.do?b_query=link&backdate=&action=searchorginfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1" target="il_body"><img src="/images/rzfs.gif" border="0"></hrms:link></td>
	             </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/setyqdt/setyqdt.do?b_query=link&backdate=&action=searchorginfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1" target="il_body" ><font id="a001" class="menu_a">狱情动态设置</font></hrms:link>
	              </td>
	             </tr>
	             </hrms:priv>
	          </table>
	      </div>
	    </td>
	  </tr>
	</table>    
    </hrms:priv>   
</td>
</tr>
</table>


<script language="javascript">
	showFirst();
	
	function openPerlogon()
	{
		window.open('/performance/totalrank/totalrank.do?br_perLogon=query','new','fullscreen=1,status=no,menubar=no,resizable=no,location=no,toolbar=no');
	
	}
</script>   


                                                                              