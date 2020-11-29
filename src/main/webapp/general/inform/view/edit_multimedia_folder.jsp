<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import=" com.hrms.frame.codec.SafeCode"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	
	String sortname=request.getParameter("sortname");
	if (sortname==null) sortname="";
	sortname=SafeCode.decode(sortname);
%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript">

function sub(){
    var foldername = document.getElementById("foldername").value
	if(foldername!=null && foldername.length>0)
	{
		if(foldername.indexOf('\"')>-1)
		{
			alert(MULTIMEDIA_ERRORINFO);
			return;
		}
		//add by xiegh on 20171205 多媒体编辑文件夹名称
		var  subcallback = '<%=request.getParameter("callback")%>';
		if(subcallback && subcallback.length>0 && "null"!=subcallback){
			var arrays = new Array();
			arrays.push(foldername);
			parent.window.opener[subcallback](arrays);
			parent.window.close();
			return;
		}
		window.returnValue = foldername;
  		window.close();
	}else
	{
		alert(INPUT_FIELD_TYPE);
	}    
    
}

function   NoExec()   
  	{   
          if(event.keyCode==13)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	}  
function IsDigit()
{
    	return (((event.keyCode > 47) && (event.keyCode <= 57))|| ((event.keyCode >= 65)&& (event.keyCode <= 90))|| ((event.keyCode >= 97)&& (event.keyCode <= 122))|| (event.keyCode == 95));
} 
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>　<bean:message key="hire.zp_persondb.username"/>：<%=userName%>　<bean:message key="workdiary.message.today"/>：<%=date%></title>
</head>
<hrms:themes/>
<html:form action="/gz/gz_accounting/gzprofilter">
<table  border="0" align="center">
<tr>
<td align="center">  
	<table  border="0" align="center">
	<tr>
	<td align="left">  
	<bean:message key="general.mediainfo.folder.save"/>
	</td>
	</tr>
	<tr>
	<td align="center">  
	<input type="text" name="foldername" id="foldername" class="text4" size="25" onkeydown="NoExec()" value="<%=sortname %>" onkeypress="event.returnValue=IsDigit(this)">
	</td>
	</tr>
	<tr>
	<td align="center">  
	<html:button styleClass="mybutton" property="b_next" onclick="sub();">
            		   <bean:message key="button.ok"/>
	      			</html:button> 
	 <html:button styleClass="mybutton" property="b_return" onclick="window.close();">
            		      <bean:message key="button.cancel"/>
	     			 </html:button> 
	</td>
	</tr>
	</table>
</td>
</tr>
</table>

</html:form>

