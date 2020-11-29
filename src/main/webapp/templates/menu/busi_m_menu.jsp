<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bos="";
	int wboc=0;
	try
	{
		 wboc=lockclient.getWboc();
		 bos=SystemConfig.getProperty("bos_online");
	}
	catch(Exception ex)
	{
	}
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<script language="javascript" src="/ajax/common.js"></script>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight- window.screenTop -80;

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
   
   function winexec(app_flag,ver_flag)
   {
   		  var bos='<%=bos%>';
   		  if(bos=="false")
   		  {
			alert("后台业务需要下载插件，系统不支持在线演示!\n请联系开发商!谢谢");
			return;
   		  }
   		  if(ver_flag=="0")
   		  {
   		    alert("授权模块超过实际购买数量!\n请联系开发商,谢谢!");
   		    return;
   		  }
    	  var cs_str=$F('cs_app');
    	  var com='C:/hrp2000/hrms.exe '+cs_str+' '+app_flag+' '+'<%=wboc%>';
          try
          {
    		var wsh = new ActiveXObject('WScript.Shell');
    		if (wsh)
    		{
      			wsh.Run(com);
    		}
    	 }
    	 catch(ex)
    	 {
    	 	alert("您的计算机未下载“后台业务”模块，\n请进入“系统管理”，点击资源下载中的“后台业务下载”即可!\n如果IE设置中未启用ActiveX时，则运行后台业务模块以前，须先设置IE环境！");
    	 }
   } 
   
   function validate(vermodule,cs_module)
   {
   		sysForm.target="il_body";
		sysForm.action="/templates/menu/busi_m_menu.do?b_query2=link&module="+vermodule+"&cs_module="+cs_module;
		sysForm.submit();    
   }
   
    function validate2(vermodule,cs_module)
   {
   		sysForm.target="il_body";
		sysForm.action="/templates/menu/busi_m_menu.do?b_repDesign=link&module=16&cs_module=5";
		sysForm.submit();    
   }
   
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0"> 
      
<html:form action="/templates/menu/busi_m_menu">
<input type="hidden" id="cs_app" value="${sysForm.cs_app_str}" name="cs">

<table cellpadding=0 cellspacing=0 width=162  class="menu_table" index="1">
  <tr style="cursor:hand;">
    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>后台业务</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;overflow-y:auto"   id=menu1> 
          <table cellpadding=2 cellspacing=3 align=center width=159  class="DetailTable" style="position:relative;top:10px;">

		   <hrms:priv func_id="9902" module_id="9">     	               
             <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('9','3'); "><img src="/images/tx.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('9','3'); "><font id="a014" class="menu_a" >绩效管理</font></a></td>
            </tr>
    	   </hrms:priv> 
		   <hrms:priv func_id="9903" module_id="8">     	               
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('8','4'); "><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('8','4');"><font id="a014" class="menu_a" >薪资管理</font></a></td>
            </tr>
    	   </hrms:priv> 
		   <hrms:priv func_id="9905" module_id="14">     	   
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('14','6');"><img src="/images/salary_set.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('14','6');"><font id="a014" class="menu_a" >保险管理</font></a></td>
            </tr>
    	   </hrms:priv>              
		   <hrms:priv func_id="9906" module_id="15">     	   
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('15','7');"><img src="/images/tx.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('15','7');"><font id="a014" class="menu_a" >合同管理</font></a></td>
            </tr>                 	   
    	   </hrms:priv>              

		   <hrms:priv func_id="9904" module_id="10">      	   
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('10','2');"><img src="/images/px.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('10','2');"><font id="a014" class="menu_a" >培训管理</font></a></td>
            </tr>
    	   </hrms:priv>
  	   
		   <hrms:priv func_id="9908" module_id="17">      	   
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('17','8');"><img src="/images/lpublic_info.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('17','8');"><font id="a014" class="menu_a" >任免管理</font></a></td>
            </tr>
    	   </hrms:priv>
	    	     
		   <hrms:priv func_id="9907" module_id="16">      	       	     	   
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate2('16','5');"><img src="/images/rz.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate2('16','5');"><font id="a014" class="menu_a" >表格工具</font></a></td>
            </tr>
    	   </hrms:priv>  
		   <hrms:priv func_id="9901" module_id="11">               
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('11','1');"><img src="/images/jgbm.gif" border=0 ></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><A href="javascript:validate('11','1');" ><font id="a014" class="menu_a" >系统构建</font></a></td>
            </tr>
    	   </hrms:priv>     	         
          </table>
     </div>
   </td>
  </tr>
</table>
	<logic:notEqual name="sysForm" property="module" value="-1">
		<script language="javascript">
     		  winexec("${sysForm.cs_module}","${sysForm.license}");
 	   	</script>
	</logic:notEqual>
</html:form>
 


<script language="javascript">
	showFirst();	
/*
  var whichOpen=menuTitle1;
  var whichContinue="";
  document.all.menu1.style.height =divHeight;
  document.all.menu1.style.display="block";
  parent.frames[1].name = "il_body"; 
  */
    // parent.location.href='/sys/downjnlp?app=1&ctrl=-1';
</script>  

                                                                              