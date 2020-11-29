<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<!-- 
xus 20/4/28 vfs改造 首页公告弹窗背景图片
<body style="background-image:url(/UserFiles/Image/${sysMessageForm.backgroudimage }); background-repeat:no-repeat;">
 -->
 <body id="messageBody" style="background-repeat:no-repeat;">
<html:form action="/system/options/message/sys_manager">
<div class="fixedDiv3">
<table align="center" width="100%" height="100%">
  <tr>
     <td valign='top'>
        
	   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="RecordRow">
            <tr height="20"> 
            <logic:empty name="sysMessageForm" property="backgroudimage">
            	<td align=left class="TableRow" colspan="4" >
            </logic:empty>
            <logic:notEmpty name="sysMessageForm" property="backgroudimage">
            	<td align=left class="TableRow" colspan="4" style="FILTER:alpha(opacity=50);-moz-opacity:0.5;opacity: 0.5;">
			</logic:notEmpty>
				<bean:message key="system.options.message.register" />
			</td>
            </tr>
            <tr>
            <logic:empty name="sysMessageForm" property="backgroudimage">
            	<td align="middle" height="270" valign="top" colspan="4"  bgcolor="#F7FAFF">
            </logic:empty>
            <logic:notEmpty  name="sysMessageForm" property="backgroudimage">
            	<td align="middle" height="270" valign="top" colspan="4" style="border-collapse: collapse;FILTER:alpha(opacity=50);-moz-opacity:0.5;opacity: 0.5;" bgcolor="#F7FAFF">
			</logic:notEmpty>
             <div style="height: 260px;overflow:auto;">
                <table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="95%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr class="list3">
						<td align="left" valign="top" style="border-collapse: collapse" bgcolor="#F7FAFF"><br>
							<bean:write name="sysMessageForm" property="constant" filter="false"/>
						</td>
					</tr>              
                 </table>
              </div>
             </td>           
           </tr>          
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center">     
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
  <html:hidden name="sysMessageForm" property="backgroudimage" />
</table>
</div>
</html:form>
<script type="text/javascript">
/**xus 20/4/28 vfs改造 首页公告弹窗背景图片**/
var fileid = document.getElementsByName("backgroudimage")[0].value;
if(fileid != ''){
	document.getElementById('messageBody').style.backgroundImage = "url(/servlet/vfsservlet?fileid="+fileid+")";
}
</script>
</body>