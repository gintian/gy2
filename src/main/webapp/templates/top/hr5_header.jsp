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
  
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/hrlogon.jsp";
   		url="/servler/sys/logout?flag=26";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
  function mustclose()
  {
   		var url = "/templates/index/hrlogon.jsp";
   		url="/servler/sys/logout?flag=26";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }
  function SetIEOpt()
   {
      obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
   }
</script>

<body style="margin:0 0 0 0" onbeforeunload="">
<div id="head-wrap">
<table width="100%" height="66" border="0" cellspacing="0" cellpadding="0"  id="top">
<tr>
  <td width="26%" valign="top"><table width="261" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td id="top_left">&nbsp;</td>
    </tr>
  </table></td>
    <td width="74%">
    <table width="100%" height="66" border="0" cellspacing="0" cellpadding="0" id="top_right">
      <tr >
        <td height="30"><table border="0" align="right" cellpadding="0" cellspacing="0">
            <tr>
			    <td width="40" style="color:black;valign:middle"><%=verdesc%></td> 
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
				<td width="23" align="left"><hrms:link href="javascript:SetIEOpt();"><img src="/images/pic_ie.gif"  width="18" height="18" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link></td>			
			    <td width="32" align="left"><hrms:link href="javascript:SetIEOpt();"><font color="#000000">设置</font></hrms:link></td>
			    <td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>				    
				<hrms:priv func_id="3013,000103" module_id=""> 	
			        <td width="23" align="left"><input name="b_exit" type="image" src="/images/pic_e.gif" width="18" height="18" title="注销" border=0 onclick="isclose();"></td>				
				    <td width="32" align="left"><hrms:link href="###" onclick="isclose();"><font color="#000000">注销</font></hrms:link></td>
				    <td width="12" align="left"><img src="/images/pic_k.gif" width="2" height="22" /></td>					
				</hrms:priv> 			    
			    <td width="32" align="left"><hrms:link href="###" onclick="parent.window.close();"><font color="#000000">退出</font></hrms:link></td>


			    <td>&nbsp;</td>
            </tr>
        </table></td>
      </tr>
      <tr>
        <td height="36">
		<table width="600" border="0" align="right" cellpadding="0" cellspacing="0" style="margin-right:10px">
          <tr>
            <td height="36">
    		  <%if(SystemConfig.isScrollWelcome()){%>
				<MARQUEE id=m1 SCROLLAMOUNT="5" SCROLLDELAY="200" >欢迎<%=buf.toString().trim()%>登录贵州银行人力资源系统</MARQUEE>
  			  <%}%>	  
        	</td>
          </tr>
        </table>
		</td>
      </tr>
    </table></td>
  </tr>
</table>
</div>
<div id='axc' style='display:none'/>
</body>
 
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 InitAx();
 </script>