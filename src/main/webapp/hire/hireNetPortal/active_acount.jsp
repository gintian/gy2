<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript">
<!--
function out(time){ //time:跳转时间； url:跳转地址
 var i = time ; 
 if(i==0) {
 employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_autoforward=forward&setID=0&opt=1&hireChannel=out";
 employPortalForm.submit();
 
 } 
 document.getElementById("time").innerHTML = i; 

 //递归调用自身，每次i自减1，直至i=0跳转至指定url
 i--; 
 setTimeout("out("+i+")",1000); //此处的1000表示：以1000毫秒（1秒）为基本单位
}

//-->
</script>
<html:form action="/hire/hireNetPortal/search_zp_position">
<div style="height:500px;align:center;color:#000000">
<strong>
 帐号激活成功，页面将在<span id="time">5</span>秒内自动跳转
 </strong>
</div>
<html:hidden name="employPortalForm" property="loginName"/>
<html:hidden name="employPortalForm" property="password"/>
<html:hidden name="employPortalForm" property="dbName"/>
<html:hidden name="employPortalForm" property="a0100"/>
</html:form>
<script type="text/javascript">
out(5);
</script>