<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ERROR PAGE INFO</title>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">
   <hrms:themes></hrms:themes>
</head>
<%
	Object msg=null;
	Object status_code=request.getAttribute("javax.servlet.error.status_code");
	msg=request.getAttribute("javax.servlet.error.message");
	String code=(status_code!=null?status_code.toString():"");
	String strmsg=(msg!=null?msg.toString():"");
	
	if(strmsg.indexOf("`^`")!=-1){//微信端传来的链接
		strmsg = strmsg.replace("`^`", "?");
				response.sendRedirect(strmsg);
				
	}
%>
<body>
<table  width="60%" align="center"  border="0" cellpadding="0" cellspacing="0">
  <tr class="list3">
    <td>
      <table width="100%" border="0" cellpadding="4" cellspacing="1" class="mainbackground">
          <tr class="list3">
    	      <td align="center" nowrap>信息提示：</td>
          </tr>
          <tr class="list3">
    	      <td align="left">输入网页不存在,请检查网页地址是否正确!</td>
          </tr> 
          <tr class="list3">
            <td align="center" colspan="4">
    		<input type="button" name="btnreturn" value="返回" onclick="history.back();" class="mybutton">
            </td>
          </tr>          
      </table>
    </td>
  </tr>
</table>   
</body>
</html>