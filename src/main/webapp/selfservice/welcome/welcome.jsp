<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hjsj.hrms.actionform.welcome.WelcomeForm"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
	if(session.getAttribute("welcomeForm")==null)
	{
		%>
			<script>
			var target_url="/selfservice/lawbase/success.jsp";
			window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
			</script>
		<%
		return;
	}
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserId();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
  
	}
	String date = DateStyle.getSystemDate().getDateString();
    WelcomeForm welcomeForm=(WelcomeForm)session.getAttribute("welcomeForm");
    String welcome_marquee=welcomeForm.getWelcome_marquee();
    if((SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equals("zglt"))||SystemConfig.getPropertyValue("performancePanel").equalsIgnoreCase("true"))
    {
      if(SystemConfig.getPropertyValue("clientName").trim().equals("zglt"))
      {
       out.println("<script language=\"javascript\">");
       if(!SystemConfig.getPropertyValue("visiblePanel").equalsIgnoreCase(""))
           out.println("window.parent.location.href=\"/templates/attestation/unicom/per_result_panel.do?b_query=link\";");
       else
           out.println("window.parent.location.href=\"/templates/attestation/unicom/performance.do?b_query=link\";");
       out.println("</script>");
      }
      else
      {
          out.println("<script language=\"javascript\">");
       if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjga"))
       {
          out.println("window.parent.location.href=\"/templates/attestation/unicom/performance.do?b_score=link\"");
       }else{
          if(!SystemConfig.getPropertyValue("visiblePanel").equalsIgnoreCase(""))
              out.println("window.parent.location.href=\"/templates/attestation/unicom/per_result_panel.do?b_query=link\";");
          else
             out.println("window.parent.location.href=\"/templates/attestation/unicom/performance.do?b_query=link\";");
        }
       out.println("</script>");
      }
    }
%>
<script language="javascript">
function winopen(target_url,target)
{

  var sheight = screen.height-70;
  var swidth = screen.width-10;
  var winoption ="left=0,top=0,height="+sheight+",width="+swidth+",toolbar=no,menubar=no,location=no,status=no,scrollbars=yes,resizable=no";
  window.open(target_url,target,winoption); 
}
</script>
<html>
<head>

<title>人力资源信息管理系统　用户名：<%=userName%>　当前日期：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
</head>

<body style="background-color:#DEEAF5;">
<html:form action="/selfservice/welcome/welcome" method="get">

<center>
<table  border="0" cellpadding="0" cellspacing="0">
  <tr>
    	<td width="100" height="211" valign="center"  board="0">
        		
     			 <logic:notEqual name="welcomeForm" property="boardflag" value="1">
     			 <br>
     			 <br>

     			 <table width="500"  border="0" cellpadding="0" cellspacing="0">
     			 <tr>
     			 <td>
      			  <table height="90" border="0" cellpadding="0" cellspacing="0" align="center" >
        		   <tr style="height:30px">
    	  			  <td background="/images/board_top_s.gif">
    				 
    	  			  </td>
    	  		  </tr>	
  			
  			      <tr>
		    	 	<td valign="top" height="300"  background="/images/board_body.jpg" >
		    	 	<%
          		           if(welcome_marquee==null||welcome_marquee.equals("0")){
          		 	%>
          		 	    <marquee scrolldelay="350"  height="250" direction="up" onmouseover='this.stop()' onmouseout='this.start()'>
             			<%
             			  }
             			%>
             				<hrms:extenditerate id="element" name="welcomeForm" property="welcomeForm.list" pagination="welcomeForm.pagination" indexes="indexes">
              				 <table width="500" border="0" valign="top" cellpadding="0" cellspacing="0"  >
              				<tr>
             				 	<td width="500" align="left" valign="bottom" >
                       					<table>
                       					<tr>
                       						<td valign="top" width="25">
                       						<img src="/images/blobul2e.gif">&nbsp;
                       						</td>
                       						<td  style="word-break:break-all" width="220">
                       						<a href="###"  onclick="winopen('/selfservice/welcome/welcome.do?b_view=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>','_blank')">
                       						  <bean:write name="element" property="string(topic)" filter="false"/>&nbsp;</a>
                       						</td>
                       						<td align="left" width="70">
                       							<bean:write name="element" property="string(approvetime)" filter="true"/>
                       						</td>
                       						<td align="left" width="85">
	    	      	 						<logic:notEqual name="element" property="string(ext)" value="">
	    	      	 							<logic:notEqual name="element" property="string(ext)" value="null">
                       								<a href="downboard?id=<bean:write name="element" property="string(id)" filter="true"/>" class="a2">
            		         						&nbsp;&nbsp;<bean:message key="lable.welcomeboard.accessoriesdownload"/>
            								        </a>
            								     </logic:notEqual>
           		   						    </logic:notEqual>&nbsp;
            							   </td>
            								<td width="95">
            								   <bean:message key="label.sys.count"/><bean:write name="element" property="string(viewcount)" filter="true"/>
            								</td>
                       					</tr>
                       					</table>
              		 			</td>
	       				</tr>
            				<tr>
            				            				
            				<td width="400" align="left" class="board_body"><b><font size="1"></font></b>
            				</td>
            				</tr>
       					</table>
       	    				</hrms:extenditerate>
       	    				<%
          		                if(welcome_marquee==null||welcome_marquee.equals("0")){
          		 	        %>
        				</marquee>  
        				<%
             			        }
             			        %> 
	    			</td>
              	    		        	        	        
        	    	 </tr>
        	          <tr>
          			<td height="18"  align="right" background="/images/board_body.jpg"  >
          				<a href="/selfservice/welcome/boardTheMore.do?b_more=link" target="_blank">
         		 			<bean:message key="lable.welcomeboard.themore"/>&nbsp;>>(<bean:message key="label.sum"/><bean:write name="welcomeForm" property="totalNum"/><bean:message key="label.item"/>)
         		 		</a>&nbsp;
         		 	</td>
       		     	</tr>
       		    	 <tr>
        	     		<td width="100" height="2" align="center" ><img src="/images/board_bottom.jpg"></td>
        	     	</tr>
		   	 </table>
		    </td>
		   
		    </tr>
		    </table>
				</logic:notEqual>
   		     </td>
     		 	
  </tr>
  
 
  
</table>
</center>
<logic:equal name="welcomeForm" property="successmsg" value="1">
<script language="JavaScript">
   alert('<bean:message key="lable.welcomeinv.message"/>');
</script>
</logic:equal>
<table  align="center">
   <tr>
         <td valign="bottom" class="tdFontcolor">
                   <hrms:friendlink cols="11" rows="3" height="40" width="40">
                    </hrms:friendlink>
	</td>
    </tr>
</table>
</html:form>

</body>
</html>
