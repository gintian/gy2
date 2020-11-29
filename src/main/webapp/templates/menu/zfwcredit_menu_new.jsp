<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter"%>
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
function winopen()
{
   window.open("/templates/attestation/zfw/lzpj_index.html","_blank");
}
</SCRIPT>
<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="188" id="menucol">  
  <hrms:priv func_id="5101,030401,5100">    
  <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span><font size='3'>基础信息</font></span></td>
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
	            <%
	            Connection conn = null;
	            try {
	            	conn = AdminDb.getConnection();
	            Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
				String approveflag=sysoth.getValue(Sys_Oth_Parameter.APPROVE_FLAG); 
				if ("1".equals(approveflag)) {
				%>
	            <hrms:priv func_id="26063">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/approve/personinfo/sum.do?b_query=link" target="il_body" ><img src="/images/investigate.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/approve/personinfo/sum.do?b_query=link" target="il_body" ><font id="a013" class="menu_a" >信息审核</font></a></td>
	            </tr> 
	          </hrms:priv>
	            <%} } catch (Exception e) {
	            	e.printStackTrace();
	            } finally {
	            	if (conn != null) {
	            		conn.close();
	            	}
	            }
	            %> 
	          </table>
	      </div>
	    </td>
	  </tr>
	</table>
    </hrms:priv>
  
  <hrms:priv func_id="52">   
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span><font size='3'>职位说明</font></span></td>
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
	            <hrms:priv func_id="5202">	
	            <tr>
	              <td  align="center" class="loginFont" >
	               <a href="/pos/roleinfo/taskbookform_tree.do?b_search=link" target="il_body"><img src="/images/hmuster.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/pos/roleinfo/taskbookform_tree.do?b_search=link" target="il_body"><font id="a001" class="menu_a">全员工作任务书</font></a>
	              </td>
	            </tr>
	            </hrms:priv>	
	            <hrms:priv func_id="5203">	
	            <tr>
	              <td  align="center" class="loginFont" >
	               <a href="/pos/roleinfo/pos_priv_info.do?b_search=link&table=a00" target="il_body"><img src="/images/hmuster.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/pos/roleinfo/pos_priv_info.do?b_search=link&table=a00" target="il_body"><font id="a001" class="menu_a">我的工作任务书</font></a>
	              </td>
	            </tr> 
	            </hrms:priv>	            
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
    </hrms:priv>    
    
  <hrms:priv func_id="53">     
    <table cellpadding=0 cellspacing=0 width="185" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span><font size='3'>绩效考评</font></span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
               <hrms:priv func_id="5301">    	          	
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <a href="javascript:openPerlogon()"  ><img src="/images/edit_info.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="javascript:openPerlogon()" ><font id="a001" class="menu_a">业绩考核评价系统</font></a>
	              </td>
	            </tr>
	            </hrms:priv>		            
                <hrms:priv func_id="5302">  	            
	             <tr>
	              <td  align="center" class="loginFont" >
	                 <a href="###" onclick="winopen();"><img src="/images/edit_info.gif" border=0></a>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="###" onclick="winopen();"><font id="a001" class="menu_a">廉政信用评价系统</font></a>
	              </td>
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
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);closediv();"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span><font size='3'>职业信用</font></span></td>
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
	                        <a href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a" color="#CC3333">决策参考信息</font></a>
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
	     <div class=sec_menu style="width:185;height:0;filter:alpha(Opacity=100);display=block;"  id=menu5> 
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
		window.open('/performance/totalrank/totalrank.do?br_perLogon=query','new','fullscreen=1,status=no,menubar=no,resizable=no,toolbar=no,location=no');
	
	}
</script>   


                                                                              