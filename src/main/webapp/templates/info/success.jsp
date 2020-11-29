<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes></hrms:themes>
<html:form action="/templates/info/success">
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr>
       		<td align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
          </tr> 
              
                    <tr >

              	      <td align="left" valign="top" nowrap style="height:150"><br>&nbsp;&nbsp;<bean:message key="label.save.success"/></td>
                    </tr> 

                    <tr >
                      <td align="center" style="height:35">
              		<!-- <input type="button" name="btnreturn" value="返回" onclick="history.back();" class="mybutton">-->
              		<!-- 【8014】邮件服务器密码，设置完以后，点击返回，界面刷新不对。 jingq upd 2015.03.13 -->
              		<input type="button" name="btnreturn" value="返回" onclick="self.location=document.referrer;" class="mybutton">
                      </td>
                    </tr>   
          
  </table>
 
</html:form>
