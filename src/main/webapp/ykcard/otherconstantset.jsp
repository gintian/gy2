<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardConstantForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
%>
<style type="text/css">
.RecordRowC {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 12px;
}
</style>
<hrms:themes></hrms:themes>
<script LANGUAGE=javascript>
function sub()
{
  if(confirm("请确认保存数据吗？"))
  {
    return true;
  }
  return false;
}
</script>
<script language="javascript">
 
</script>
<html:form action="/ykcard/otherconstantset" onsubmit="return sub();">
	<table width="80%" border="0"  cellspacing="0" align="center" cellpadding="0" class="">
		<thead>
			<tr>
				<td align="left" style="margin-left:5px;" class="TableRow" colspan="2" nowrap>
					薪酬表其他设置&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
		     <td height="30" class="RecordRowC common_border_color" nowrap align="center">
		         <!-- <table width="100%" border="0" cellpadding="0" cellspacing="0">
		           <tr>
		             <td  width="15%" nowrap>
		               &nbsp;
		              </td>
		              <td width="20%" align="right"> -->
		              	 只显示
		              <!-- </td>
			       <td width="65%" nowrap> -->
				     <html:select name="cardConstantForm" property="year_restrict" size="1">
					    <html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
				    </html:select>
				    年以后的薪酬
			     <!-- </td>			     
		           </tr>
		         </table> -->
		        </td>
		        
		</tr>
		<tr>
			<td align="center" colspan="2" style="height: 35px" class="RecordRow" nowrap>
				&nbsp;&nbsp;
				<input type="submit" name="b_save" class="mybutton" value="&nbsp;确定&nbsp;">
			</td>
		</tr>
	</table>

</html:form>
