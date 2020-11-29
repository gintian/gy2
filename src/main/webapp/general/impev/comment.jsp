<%@ page contentType="text/html; charset=UTF-8"%>
<%@page
	import="com.hjsj.hrms.actionform.general.impev.ImpEvCommentForm,
	com.hrms.struts.constant.WebConstant,
	com.hrms.struts.valueobject.UserView,
	java.util.List,com.hrms.frame.dao.RecordVo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="/general/impev/important.js"></script>
<%
	ImpEvCommentForm impEvCommentForm = (ImpEvCommentForm) session
			.getAttribute("impEvCommentForm");
	List fieldlist = (List) impEvCommentForm.getFieldlist();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>
<html:form action="/general/impev/importantevcomment">
<%if("hl".equals(hcmflag)){ %>
<br><br>
<table align="center" width="600" border="0" cellpadding="0" cellspacing="0">
<center>
<%}else{ %>
<table align="center" width="700" border="0" cellpadding="0" cellspacing="0" style="margin-top:60px">
<center>
<%} %>
		
			
			<tr height="10">
				<!--  <td width=30 valign="top" class="tableft"></td>
				<td width=130 align=center class="tabcenter">
					&nbsp;<bean:message key="general.impev.viewcomment" />&nbsp;
				</td>
				<td width=10 valign="top" class="tabright"></td>
				<td valign="top" class="tabremain" width="680"></td>-->
				<td align=center class="TableRow">
					&nbsp;<bean:message key="general.impev.viewcomment" />&nbsp;
				</td>
			</tr>
			<tr>
				<%
					if (fieldlist.size() == 0) {
				%>
				<td  class="framestyle9" width="600" height="100"
					align="center" valign="top">
					<br>
					<bean:message key="general.impev.message"/>
				</td>
				<%
					} else {
				%>
				<td  class="framestyle9" width="600"
					align="left" valign="top">
					<br>
					<%
						RecordVo vo = null;
								for (int i = 0; i < fieldlist.size(); i++) {
									vo = (RecordVo) fieldlist.get(i);
									String str = vo.getString("b0110")+"&nbsp;&nbsp;&nbsp;"+vo.getString("e0122")+"&nbsp;&nbsp;&nbsp;"+vo.getString("a0101")+"&nbsp;&nbsp;&nbsp;"+vo.getString("commentary_date");
					%>
					<table>
						<tr>
							<td colspan="2" width="600">
								<%=str%>
							</td>
						</tr>
						<tr>
							<td width="20"></td>
							<td width="580">
								<%=vo.getString("content")%>
							</td>
						</tr>
					</table>
					<br>
					<%
						}
					%>
				</td>
				<%
					}
				%>
				
			</tr>
			<tr>
			<td  width="600"
					 valign="top" align="left" class="RecordRow_lr">
					&nbsp;<bean:message key="general.impev.comment"/>:
			</td>
			</tr>
			<tr >
					 
			<td class="framestyle9" width="600" height="100"
					 align="center">
					<html:textarea name="impEvCommentForm" property="content" styleClass="" rows="11" cols="90" style="width:98%;margin-bottom:5px;resize:none;"></html:textarea><!--add by xiegh date20180307 bug35110  -->
				</td>
			</tr>
			<tr><td  align="center" style="height:35px;">
			<html:hidden name="impEvCommentForm" property="a0100"/>
			
				<INPUT type="button" value="<bean:message key="general.impev.deliver" />"
						Class="mybutton" onclick="deliver('${impEvCommentForm.flag}');">
			
			<INPUT type="button" value="<bean:message key="button.leave" />"
					Class="mybutton" onclick="back2('${impEvCommentForm.flag}','${impEvCommentForm.a_code}');"></td></tr>
		</table>
	</center>
</html:form>