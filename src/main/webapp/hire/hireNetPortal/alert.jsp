<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<script language='javascript'>
	//1:为已申请 2: 申请成功   3:已超过了申请职位的最大数量3  4:简历资料必填项没填

	var infos=dialogArguments;
	function back()
	{
		window.close();
	}
	
	
	


</script>

<body>
<base id="mybase" target="_self">		
<html:form action="/hire/hireNetPortal/search_zp_position">
<table width="100%" height='100%' align="center" border="0" cellpadding="0" cellspacing="0" >
  <tr  height="240" >  
    <td width="100%"  height='100%'  valign="top"  style="background-image: url(/images/alertBack.jpg);background-repeat:no-repeat;background-color: #ffffff;background-position: top center;" align="center">
      	<% if(request.getParameter("infos")!=null&&request.getParameter("infos").equals("1")){ %>
      	<table border="0" width='80%' height='80%'>
      		<tr><td height='30%' align='center' valign='middle' ><img  src='/images/alert.jpg' />&nbsp;</td></tr>
      		<tr><td   height='30%'><font color='red'> <STRONG> 
      		<script language='javascript'>
      			document.write(infos[1]);
      		</script>
      		&nbsp;<bean:message key="hire.appledposition.norepeat"/></STRONG></font></td></tr>
      		
      	</table>
      	<% } else if(request.getParameter("infos")!=null&&request.getParameter("infos").equals("3")){ %>
      	<table border="0" width='80%' height='80%'>
      		<tr><td height='30%' align='center' valign='middle' ><img  src='/images/alert.jpg' /></td></tr>
      		<tr><td  height='30%'><font color='red'>  
      		<STRONG>
      		<script language='javascript'>
      			document.write(infos[1]);
      		</script>
      		&nbsp;<bean:message key="hire.overmax.noapply"/></STRONG></font></td></tr>
      		<tr><td  height='30%' align='right' ><a href='javascript:back()'> <img src="/images/hire/return.gif" border="0"/></a> </td></tr>
      	</table>
      	<% } else if(request.getParameter("infos")!=null&&request.getParameter("infos").equals("4")){ %>
      	<table border="0" width='80%' height='80%'>
      		<tr><td height='30%' align='center' valign='middle' ><img  src='/images/alert.jpg' /></td></tr>
      		<tr><td  height='30%'><font color='red'> 
      		 <strong><bean:message key="hire.fillresume.complete"/></strong></font></td></tr>
      		<tr><td  height='30%' align='right' ><a href='javascript:back()'><bean:message key="hire.fillresume.resume"/></a> </td></tr>
      	</table>
      	<% } %>
    </td>
  </tr>
  <% if(request.getParameter("infos")!=null&&request.getParameter("infos").equals("1")){ %>
  <tr><td  height='30%' align='right' ><a href='javascript:back()'> <img src="/images/hire/return.gif" border="0"/></a> </td></tr>
  <%} %>
</table>


</html:form>
</body>
</html>