<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hrms.hjsj.sys.Des"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=prl+"://"+aurl+":"+port;
    String userName = null;
    String pwd=null;
    boolean bexchange=false;
    int status=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock"); 
    boolean bflag=lockclient.isBtest();
    String verdesc="";
    if(bflag)
    {
    	verdesc=ResourceFactory.getProperty("label.sys.about.test");
    }        
    String css_url="/css/css1.css";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    bexchange=userView.isBexchange();
	status=userView.getStatus(); 
    StringBuffer buf=new StringBuffer();
    if(SystemConfig.isScrollWelcome())
    {
    	String orgid=userView.getUserOrgId();
    	String deptid=userView.getUserDeptId();
    	String posid=userView.getUserPosId();
    	buf.append(AdminCode.getCodeName("UN",orgid));
    	buf.append(" ");
    	buf.append(AdminCode.getCodeName("UM",deptid));
    	buf.append(" ");
    	buf.append(AdminCode.getCodeName("@K",posid));
    	buf.append(" ");
    	buf.append(userView.getUserFullName());
    }	    
    if(status==0)
    {
    	userName=userView.getS_userName();
    	if(userView.isBEncryPwd())
    	{
    		Des des=new Des();
    		pwd=des.DecryPwdStr(userView.getS_pwd());
    	}
    	else
    		pwd=userView.getS_pwd();
    }
    else
    {
    	userName=userView.getUserName();
    	if(userView.isBEncryPwd())
    	{
    		Des des=new Des();
    		pwd=des.DecryPwdStr(userView.getPassWord());
    	}
    	else    	
    		pwd=userView.getPassWord();    
    }
    if(userView != null){
       css_url=userView.getCssurl();
       if(css_url==null||css_url.equals(""))
	  css_url="/css/css1.css";
       //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
    }
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">
#menu{
	position: absolute; 
	left: 170px; 
	top: 38px
}
</style>
<script language="JavaScript">

   
   function winopen(userID,password)
   {
   	var url = "/templates/index/employLogon.do?logon.x=link&username="+userID+"&password="+password;
	newwin=window.open(url,"_blank","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	window.opener=null;//不会出现提示信息
   	parent.window.close();	
  }

   function winopenhelp()
   {
   	var url = "/help/hrphelp.do?b_search=link";
	newwin=window.open(url,"_blank","height=500,width=700,toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }
  
  function logout()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/emLogon.jsp";
   		url="/servler/sys/logout?flag=27";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
  function isclose()
  {
  	if(confirm("确定要退出吗？"))
  	{
   		var url = "/templates/close.jsp";
   		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
   		//parent.window.close();	
  	}
  }
  function mustclose()
  {
   		var url = "/templates/close.jsp";
   		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }
 function SetIEOpt()
   {
     var obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
   }
   function submitWinopen()
  {
    logonForm.action="/templates/index/hrlogon.do?logon.x=link";
    logonForm.target="_blank";
	logonForm.submit();	
	window.opener=null;//不会出现提示信息
   	parent.window.close();	
  }
</script>

<body style="margin:0 0 0 0" onbeforeunload="">
<form name="logonForm" method="post" action="/templates/index/emlogon.do">
<div id="head-wrap">
<table width="100%" height="66" border="0" cellspacing="0" cellpadding="0"  id="top">
<tr>
  <td width="26%" valign="top"><table width="261" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td id="top_left">&nbsp;</td>
    </tr>
  </table>
   </td>
    <td width="74%">
    <table width="100%" height="66" border="0" cellspacing="0" cellpadding="0" id="top_right">
      <tr >
        <td height="30"><table border="0" align="right" cellpadding="0" cellspacing="0">
            <tr>
			    <td width="40" style="color:red;valign:middle"><%=verdesc%></td> 
			    <td width="23" align="left"><hrms:link href="/templates/index/portal.do?b_query=link" target="il_body" ><img src="/images/pic_a.gif" width="18" height="18" border=0 title="<bean:message key="label.banner.home"/>"></hrms:link></td>
			    <td width="33" align="left"><hrms:link href="/templates/index/portal.do?b_query=link" target="il_body" ><font color="#000000"><bean:message key="label.banner.home"/></font></hrms:link></td>
			    <td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>
				<hrms:priv func_id="3012" module_id=""> 							
				    <td width="23" align="left"><hrms:link href="/system/options/portaltailor.do?b_search=link" target="il_body" ><img src="/images/pic_b.gif" width="18" height="18" border=0 title="<bean:message key="label.portal.options"/>"></hrms:link></td>
			   		<td width="33" align="left"><hrms:link href="/system/options/portaltailor.do?b_search=link" target="il_body" ><font color="#000000"><bean:message key="label.portal.options"/></font></hrms:link></td>
			        <td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>					
			    </hrms:priv>     
			    <hrms:priv func_id="000101,3010" module_id=""> 						
					<td width="23" align="left"><hrms:link href="/system/security/resetup_password.do" target="il_body" ><img src="/images/pic_c.gif" width="18" height="18" border=0 title="<bean:message key="label.mail.password"/>"></hrms:link></td>
			    	<td width="33" align="left"><hrms:link href="/system/security/resetup_password.do" target="il_body" ><font color="#000000"><bean:message key="label.mail.password"/></font></hrms:link></td>
			    	<td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>		
				</hrms:priv> 	
				<hrms:priv func_id="3011,000102" module_id=""> 						
					<td width="23" align="left"><hrms:link href="/system/security/about_hrp.do?b_query=link&status=1" target="il_body" ><img src="/images/pic_d.gif" width="18" height="18" border=0 title="<bean:message key="label.banner.product"/>"></hrms:link></td>
				    <td width="32" align="left"><hrms:link href="/system/security/about_hrp.do?b_query=link&status=1" target="il_body" ><font color="#000000"><bean:message key="label.banner.product"/></font></hrms:link></td>
				    <td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>	
			    </hrms:priv> 	
			    
			    <td width="23" align="left"><input name="b_exit" type="image" src="/images/pic_e.gif" width="18" height="18" title="退出" border=0 onclick="parent.window.close();"></td>
			    <td width="32" align="left"><hrms:link href="###" onclick="parent.window.close();"><font color="#000000">退出</font></hrms:link></td>
			    <td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>	
				<td width="23" align="left"><hrms:link href="javascript:SetIEOpt();"><img src="/images/pic_ie.gif"  width="18" height="18" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link></td>			
			    <td width="32" align="left"><hrms:link href="javascript:SetIEOpt();"><font color="#000000">设置</font></hrms:link></td>
			    <td>&nbsp;</td>
            </tr>
        </table></td>
      </tr>
      <tr>
        <td height="36">
		<table width="600" border="0" align="left" cellpadding="0" cellspacing="0" style="margin-right:10px">
          <tr>
            <td valign="left">	
     </td>
          </tr>
        </table>
		</td>
      </tr>
    </table></td>
  </tr>
  <tr>
  </tr>
</table>
<div id="menu">
 	 <table  border="0"  align=right cellpadding="0" cellspacing="0"  width="100%">
  	 <tr>		
		<hrms:priv func_id="11" module_id="0">     		
    			<td class=top_button_v width="68" id="myself_servie"  onclick="javascript:changbutton('myself_servie')" ><A href="/templates/menu/selfservice_menu.do?b_query=link&module=0" target="i_body">HR服务台</a></td>
    		</hrms:priv>    			
		<hrms:priv func_id="" module_id="0">  
		  <% if(status==4){%>	   
		    <hrms:priv func_id="01" module_id="0">   		   		   	
    			<td class=top_button_v width="68" id="myself_inform"  onclick="javascript:changbutton('myself_inform')" ><A href="/templates/menu/inform_menu.do?b_query=link&module=0" target="i_body">我的信息</a></td>
    		    </hrms:priv>
                  <%}%>
    		</hrms:priv>
		<hrms:priv func_id="03" module_id="1">     		
    			<td class=top_button_v width="68" id="employee_inform"  onclick="javascript:changbutton('employee_inform')"><A href="/templates/menu/employee_menu.do?b_query=link&module=1"  target="i_body">员工信息</a></td>
    	</hrms:priv>
		<hrms:priv func_id="05" module_id="1">     		
    			<td class=top_button_v width="68" id="org_inform"  onclick="javascript:changbutton('org_inform')"><A href="/templates/menu/organization_menu.do?b_query=link&module=1"  target="i_body">机构信息</a></td>
    	</hrms:priv>    		
		<hrms:priv func_id="04" module_id="1">     			            			
	    		<td class=top_button_v width="68" id="static_menu"  onclick="javascript:changbutton('static_menu')"><A href="/templates/menu/all_static_menu.do?b_query=link&module=1"  target="i_body">统计分析</a></td>
	    	</hrms:priv>
		<hrms:priv func_id="290" module_id="13">                   
                  <td class=top_button_v width="68" id="reportCenter" onclick="javascript:changbutton('reportCenter')"><A href="/templates/menu/tjb_m_menu.do?b_query=link&module=13" target='i_body'>报表管理</A></td> 
    	</hrms:priv>  

    		  <hrms:priv func_id="310" module_id="7">   
    		   <td class=top_button_v width="68" id="hire2" onclick="javascript:changbutton('hire2')"><A href="/templates/menu/hire_m_menu2.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
    		  </hrms:priv>     		    	
    		  <hrms:priv func_id="323" module_id="21">                    
                  <td class=top_button_v width="68" id="train" onclick="javascript:changbutton('train')"><A href="/templates/menu/train_m_menu.do?b_query=link&module=21" target='i_body'>培训管理</A></td>                           
    		  </hrms:priv>	 
	        <% if(status==4){%>	    	
		  <hrms:priv func_id="09" module_id="2">
    			<td class=top_button_v width="68" id="train_button"  onclick="javascript:changbutton('train_button')" ><A href="/templates/menu/train_menu.do?b_query=link&module=2" target="i_body">培训自助</a></td>
    		  </hrms:priv>
		  <hrms:priv func_id="06" module_id="3">
    			<td class=top_button_v width="68" id="performance_menu"  onclick="javascript:changbutton('performance_menu')" ><A href="/templates/menu/performance_menu.do?b_query=link&module=3" target="i_body">绩效考评</a></td>
    			
    		  </hrms:priv>
  		  
		  <hrms:priv func_id="0A" module_id="4">
    			<td class=top_button_v width="68" id="zp_menu"  onclick="javascript:changbutton('zp_menu')" ><A href="/templates/menu/hire_employee_menu.do?b_query=link&module=4" target="i_body">招聘自助</a></td>
    		  </hrms:priv>    
		  <hrms:priv func_id="0B" module_id="5">
    			<td class=top_button_v width="68" id="kq_menu"  onclick="javascript:changbutton('kq_menu')" ><A href="/templates/menu/kq_employee_menu.do?b_query=link&module=5" target="i_body">考勤自助</a></td>
    		  </hrms:priv>
  		         		  
                  <%}%>  
		  <hrms:priv func_id="0D" module_id="3">
    			<td class=top_button_v width="68" id="review_menu"  onclick="javascript:changbutton('review_menu')" ><A href="/templates/menu/leader_review_menu.do?b_query=link&module=3" target="i_body">干部考察</a></td>
    			
    		  </hrms:priv>   
 		                   
		  <hrms:priv func_id="0C" module_id="22">
    			<td class=top_button_v width="68" id="ekq_menu"  onclick="javascript:changbutton('ekq_menu')" ><A href="/templates/menu/kq_dept_menu.do?b_query=link&module=22" target="i_body">部门考勤</a></td>
    	  </hrms:priv>                       		
		  <hrms:priv func_id="0E">
    			<td class=top_button_v width="68" id="elearning_menu"  onclick="javascript:changbutton('elearning_menu')" ><A href="/templates/menu/elearning_menu.do?b_query=link&module=22" target="i_body">在线培训</a></td>
    	  </hrms:priv>
		  <hrms:priv func_id="0F" >
    			<td class=top_button_v width="68" id="jp_menu"  onclick="javascript:changbutton('jp_menu')" ><A href="/templates/menu/jp_menu.do?b_query=link" target="i_body">竞聘上岗</a></td>
    	  </hrms:priv>        	    
		<hrms:priv func_id="08,07" >      		  			
    			<td class=top_button_v width="68" id="systemoption"  onclick="javascript:changbutton('systemoption')"><A href="/templates/menu/securitymenu.do?b_query=link"  target="i_body">系统管理</a></td>
    		</hrms:priv>    		
    	 <hrms:priv func_id="2" module_id="0">     		
				<%
				 if(bexchange)
				 { 
				%>   
				<td class="top_button_v" width="68" id="bussop"><A href="javascript:submitWinopen();"><bean:message key="label.hrms.os"/></a></td>
				<%}%>
    		</hrms:priv>
	 		   			
    		<td>&nbsp;</td>
  	  </tr>	  	 	
 	  </table>
	</div>	
</div>
</form>
    <div id='axc' style='display:none'/>
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 
 InitAx();
 </script>
</body>