<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.hjsj.sys.Des"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String pwd=null;
    boolean bexchange=false;
    int status=0;
       
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
<style>
A:link {
	COLOR:black; TEXT-DECORATION: none;font-size: 12px	
}
A:visited {
	COLOR: black; TEXT-DECORATION: none;font-size: 12px
}
A:active {
	COLOR: black;TEXT-DECORATION: none;font-size: 12px
}
A:hover {
	COLOR: black; TEXT-DECORATION:none;font-size: 12px
}
</style>

<script language="JavaScript">
var preId="";//一级菜单
var preTopId="";//按钮

function show(id)
{
   eval("window."+id+".style.backgroundImage='url(/images/top_title_a.gif)'");   
   if(id!=preId&&preId!="")
   {
     eval("window."+preId+".style.backgroundImage='url(/images/top_title_v.gif)'");   
   } 
   preId=id;	   
}

function changbutton(id) 
{
   
   eval("window."+id+".style.backgroundImage='url(/images/top_button_a.gif)'");  
   if(preTopId!="") 
   {      
      if(id!=preTopId) 
      {
         eval("window."+preTopId+".style.backgroundImage='url(/images/top_button_v.gif)'");      	
      }   
   }
   preTopId=id;
   //
}
	
   
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
  function download_cs()
  {
  	 parent.location.href='/sys/downjnlp?app=1&ctrl=-1';
  }
  
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/UserLogon.jsp";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
  
  
  function clearStatus()
  {
  	this.status="";
  }
</script>

<body style="margin:0 0 0 0"  onclick="clearStatus()">
<table  border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="banner">
   <tr>
	<td valign="top"> 
	  <table  border="0" align=left cellpadding="0" cellspacing="0" width="100%" >
           <tr> 
             <td class="bgToptitleLeft" width="185"></td>
             <td class="bgToptitleRight" valign="bottom">
          	  <table  border="0" align="right" cellpadding="0" cellspacing="0" width="400">
                 	 <tr align="left">
          	     	 	<td valign="center">
          	     	 	</td>
          	     	 </tr>
                     <tr align="left">
	          	      <td>
						<hrms:link href="/system/home.do?b_query=link" target="i_body" ><img src="/images/home_1.gif" border=0 title="<bean:message key="label.banner.home"/>"></hrms:link>
		        		<hrms:priv func_id="3012" module_id=""> 						
							<hrms:link href="/system/options/portaltailor.do?b_search=link" target="i_body" ><img src="/images/desktop.gif" border=0 title="<bean:message key="label.portal.options"/>"></hrms:link>			
    		  			</hrms:priv> 
		        		<hrms:priv func_id="000105,3015" module_id=""> 						
							<hrms:link href="/system/sms/send_sms.do" target="i_body" ><img src="/images/sms.gif" border=0 title="<bean:message key="label.sms.send"/>"></hrms:link>
    		  			</hrms:priv>     		  										
    		  			<hrms:priv func_id="000104,3014" module_id=""> 						
							<hrms:link href="/selfservice/app_news/appmessage.do?b_apptag=link" target="i_body" ><img src="/images/msg.gif" border=0 title="<bean:message key="tab.label.mymsg"/>"></hrms:link>
    		  			</hrms:priv>	
		        		<hrms:priv func_id="000101,3010" module_id=""> 						
							<hrms:link href="/system/security/resetup_password.do" target="i_body" ><img src="/images/keylock.gif" border=0 title="<bean:message key="label.mail.password"/>"></hrms:link>
    		  			</hrms:priv> 		
				
		        		<hrms:priv func_id="3011,000102" module_id=""> 						
							<hrms:link href="/system/security/about_hrp.do?b_query=link&status=1" target="i_body" ><img src="/images/version.gif" border=0 title="<bean:message key="label.banner.product"/>"></hrms:link>
    		  			</hrms:priv> 	
						<hrms:link href="../../help/ie_option.htm" target="_blank"><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
    	 				<!-- 
						<hrms:link href="javascript:winopenhelp();"  ><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
						<hrms:link href="/templates/index/UserLogon.jsp"  target="_blank" onclick="parent.window.close();"><img src="/images/reenter.gif" border=0 title="<bean:message key="label.banner.relogin"/>"></hrms:link>			
						-->
						<hrms:priv func_id="3013,000103" module_id=""> 							
						  <input name="reenter" type="image" src="/images/reenter.gif" title="<bean:message key="label.banner.relogin"/>" onclick="isclose();">
						</hrms:priv> 						  
						<input name="b_exit" type="image" src="/images/exit.gif" title="退出" onclick="parent.window.close();">
			          </td>
          	   		</tr>           	      
              </table>             
             </td>
           </tr> 
           <tr align="right" class="bgToptitle">
	       <td valign="bottom" colspan="2">
	      <%if(SystemConfig.isScrollWelcome()){%>
<MARQUEE id=m1 direction="left" BEHAVIOR="SCROLL" SCROLLAMOUNT="5" SCROLLDELAY="200" width="200" height=20>欢迎(<%=buf.toString().trim()%>)登录贵州银行人力资源系统</MARQUEE>
	      <%}%>	       
	       </td>
	     </tr> 
          </table>
        </td>
   </tr>
   <!--二级菜单-->   
   <tr class=bgTopmenu>
     <td valign="center">
	<div id="employee_menu" style="visibility: visible">
 	 <table  border="0"  align=right cellpadding="0" cellspacing="0"  width="100%">
  	 <tr>		
		          <td width=150 >&nbsp;</td>
     		      <td width="16"></td>
		      <hrms:priv func_id="230" module_id="11">     		      
                  <td class=top_button_v width="68" id="organization"  onclick="javascript:changbutton('organization')"><A href="/templates/menu/org_m_menu.do?b_query=link&module=11" target='i_body'>机构管理</A></td>
    		  </hrms:priv> 
		      <hrms:priv func_id="250" module_id="11">    		                    
                  <td class=top_button_v width="68" id="position"  onclick="javascript:changbutton('position')"><A href="/templates/menu/pos_m_menu.do?b_query=link&module=11" target='i_body'>职位管理</A></td>
    		  </hrms:priv> 
		      <hrms:priv func_id="260" module_id="11">      		                   
                  <td class=top_button_v width="68" id="employee" onclick="javascript:changbutton('employee')"><A href="/templates/menu/employ_m_menu.do?b_query=link&module=11" target='i_body'>人员管理</A></td>
    		  </hrms:priv>                   
                  <!-- 
                  <td class=top_title_v width="67" id="salary" onclick="show('salary')"><A href="/sys/downjnlp?app=4" target='i_body'>工资管理</A></td> 
                  -->
    		  <hrms:priv func_id="310" module_id="7">   
    		   <td class=top_button_v width="68" id="hire2" onclick="javascript:changbutton('hire2')"><A href="/templates/menu/hire_m_menu2.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
    		  </hrms:priv>                    
		      <hrms:priv func_id="320" module_id="20">                   
	              <td class=top_button_v width="68" id="rsbd" onclick="javascript:changbutton('rsbd')"><A href="/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20" target='i_body'>人事异动</A></td>                           
    		  </hrms:priv>    
		      <hrms:priv func_id="270" module_id="6">                   
                  <td class=top_button_v width="68" id="kq" onclick="javascript:changbutton('kq')"><A href="/templates/menu/kq_m_menu.do?b_query=link&module=6" target='i_body'>考勤休假</A></td>                           
    		  </hrms:priv>
		      <hrms:priv func_id="326" module_id="9">                    
                  <td class=top_button_v width="68" id="jx" onclick="javascript:changbutton('jx')"><A href="/templates/menu/per_m_menu.do?b_query=link&module=9" target='i_body'>绩效管理</A></td>                           
    		  </hrms:priv>	    		  
		      <hrms:priv func_id="324" module_id="8">                    
                  <td class=top_button_v width="68" id="gz" onclick="javascript:changbutton('gz')"><A href="/templates/menu/gz_m_menu.do?b_query=link&module=8" target='i_body'>薪资管理</A></td>                           
    		  </hrms:priv>	
		      <hrms:priv func_id="325" module_id="14">                    
                  <td class=top_button_v width="68" id="ins" onclick="javascript:changbutton('ins')"><A href="/templates/menu/ins_m_menu.do?b_query=link&module=14" target='i_body'>保险管理</A></td>                           
    		  </hrms:priv>	    		  	  
    		  <!-- 
		      <hrms:priv func_id="240" module_id="7">     		                      
                  <td class=top_button_v width="68" id="hire" onclick="javascript:changbutton('hire')"><A href="/templates/menu/hire_m_menu.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
    		  </hrms:priv> 
    		   -->


                  <!--                       
                  <td class=top_title_v width="68" id="performance" onclick="show('performance')"><A href="/sys/downjnlp?app=3" target='i_body'>绩效考核</A></td>                          
                  <td class=top_title_v width="68" id="train" onclick="show('train')"><A href="/sys/downjnlp?app=2" target='i_body'>培训管理</A></td>                          
                  -->
                 
                  <!-- 
                  <td class=top_title_v width="68" id="taskCenter" onclick="show('taskCenter')"><A href="/templates/menu/task_m_menu.do" target='i_body'>业务流程</A></td>
		      <hrms:priv func_id="280" module_id="12">                   
                  <td class=top_button_v width="68" id="rule" onclick="javascript:changbutton('rule')"><A href="/templates/menu/rule_m_menu.do?b_query=link&module=12" target='i_body'>制度政策</A></td>
    		  </hrms:priv>    
 
    		                    -->
   		                    
    		  <hrms:priv func_id="340,280" module_id="12">                   
                  <td class=top_button_v width="68" id="arch" onclick="javascript:changbutton('arch')"><A href="/templates/menu/document_m_menu.do?b_query=link&module=12" target='i_body'>文档管理</A></td>
    		  </hrms:priv>               

              
		      <hrms:priv func_id="321" module_id="20">                   
	              <td class=top_button_v width="68" id="abroad_menu" onclick="javascript:changbutton('abroad_menu')"><A href="/templates/menu/gri_abroad_m_menu.do?b_query=link&module=18" target='i_body'>出国政审</A></td>                           
    		  </hrms:priv> 
		      <hrms:priv func_id="322" module_id="19">                   
	              <td class=top_button_v width="68" id="deci_menu" onclick="javascript:changbutton('deci_menu')"><A href="/templates/menu/deci_m_menu.do?b_query=link&module=19" target='i_body'>领导决策</A></td>                           
    		  </hrms:priv> 
		      <hrms:priv func_id="290" module_id="13">                   
                  <td class=top_button_v width="68" id="reportCenter" onclick="javascript:changbutton('reportCenter')"><A href="/templates/menu/tjb_m_menu.do?b_query=link&module=13" target='i_body'>报表管理</A></td> 
    		  </hrms:priv>      		  
		      <hrms:priv func_id="990" module_id="">                   
                  <td class=top_button_v width="68" id="busiManage" onclick="javascript:changbutton('busiManage')"><A href="/templates/menu/busi_m_menu.do?b_query=link&module=-1"  target="i_body">后台业务</a></td>
    		  </hrms:priv>                  
		      <hrms:priv func_id="300" module_id="11">                   
                 <td class=top_button_v width="68" id="systemManage" onclick="javascript:changbutton('systemManage')"><A href="/templates/menu/sys_m_menu.do?b_query=link&module=11"  target="i_body">系统管理</a></td>
    		  </hrms:priv>                  
              
                   
				<%
				 if(bexchange)
				 { 
				%>                          
				  <td class=top_button_v width="68" id="selfCenter" onclick="javascript:changbutton('selfCenter')"><A href="javascript:winopen('<%=userName%>','<%=pwd%>');"><bean:message key="label.selfservice.os"/></A></td>
				<%}%>	
				
    		    <td>&nbsp;</td>
  	  </tr>	  	 	
 	  </table>
	</div>	
     </td>
  </tr>
</table>
</body>