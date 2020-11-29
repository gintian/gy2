<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>		     
<script type="text/javascript" src="exchange.js"></script>
<script type="text/javascript" src="/js/validate.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes/>
<html:form action="/train/exchange/exchangemanage.do?b_import=link" method="post" enctype="multipart/form-data"> 
  <br/>
  <table width="600" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">   
	 <tr>
		<td align="left" class="TableRow">&nbsp;导入奖品信息</td>
	 </tr>          
     <tr>        
           <td align="center" class="RecordRow"  style="height: 200px;" >
           	  提示：请用下载的Excel模板来导入数据！模板格式不允许修改！<br/><br/><br/>
	          <fieldset style="width: 500px;height: 70px;">
	          	<legend>&nbsp;选择Excel文件&nbsp;</legend>
	          	<br/>
	          	<input type="file" class="text6" style="width: 350px;" name="excelfile">
	          </fieldset>
           </td>
      </tr> 
      <tr>
		<td align="center" style="height: 35px;">
			<input type="submit" class="mybutton" value="<bean:message key='menu.gz.import'/>" onClick="return validatefilepath('excelfile');" />&nbsp;
			<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onClick="exchange();">
		</td>
	 </tr>           
   </table>
</html:form>
