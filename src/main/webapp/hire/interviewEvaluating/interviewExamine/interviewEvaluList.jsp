<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="java.util.*,
				com.hjsj.hrms.actionform.hire.interviewEvaluating.InterviewExamineForm,
				org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<style>
.myfixedDiv
{ 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<script language="JavaScript"
	src="/hire/interviewEvaluating/interviewExamine/interviewEvalu.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<hrms:themes></hrms:themes>
<html:form action="/hire/interviewEvaluating/interviewExamine">
	<%
		int i=0; 
		InterviewExamineForm interviewExamineForm=(InterviewExamineForm)session.getAttribute("interviewExamineForm");	
		String commentUserFild = interviewExamineForm.getCommentUserFild();
		String userName = ""; 
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		if(userView != null)
	 		 userName = userView.getUserFullName();  
	%>
	<table width="100%" align="center" id="layouttable">
		<tr>
			<td align="center">
				<div class="myfixedDiv common_border_color" id="myfixed">
					<table width="99%" border="0" cellspacing="0" cellpadding="0" class="ListTableF" style="margin-top:5px;">
						<tr class="fixedHeaderTr">
							<td align="center" class="TableRow" nowrap width="2%">
								<input type="checkbox" name="selbox"
									onclick="batch_select(this, 'i9999');">
							</td>
							<td align="center" class="TableRow" nowrap width="3%">
								<bean:message key="label.edit.user" />
							</td>
							<logic:iterate id="element3" name="interviewExamineForm"
								property="fieldName">
								<td align="center" class="TableRow" nowrap  >
									<bean:write name="element3" property="itemdesc" filter="true" />
								</td>
							</logic:iterate>
						</tr>
						<logic:iterate id="element" name="interviewExamineForm"
							property="fieldset">
							<%
				 if (i % 2 == 0)
					{
			%>
							<tr class="trShallow">
								<%
						} else
						{
				%>
							
							<tr class="trDeep">
								<%
						}
						i++;
						LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
						String commentUser=(String)bean.get(commentUserFild.toLowerCase());
						boolean isEdit=false;
						if(commentUser!=null && commentUser.trim().equals(userName.trim()))
							isEdit=true;						
				%>
								<td align="center" class="RecordRow" nowrap width="3%">
									<% if(isEdit){%>
									<input type='checkbox' name='i9999<%=i%>'
										value='<bean:write name="element" property="i9999" filter="true"/>' />
									<% }%>
								</td>
								<td align="center" class="RecordRow" nowrap width="3%">
									<% if(isEdit){%>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
										onclick="editEval('${interviewExamineForm.commentUserFild}','${interviewExamineForm.commentdateFild}','${interviewExamineForm.a0100}','${interviewExamineForm.dbName}','${interviewExamineForm.examineNeedRecordSet}','<bean:write name="element" property="i9999" filter="true"/>')">
									<% }%>
								</td>
								<logic:iterate id="element2" name="interviewExamineForm"
									property="fieldName">
									<%
		     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element2");
		     		String itemid=(String)abean.get("itemid");	
		     		String itemtype=(String)abean.get("itemtype");
		     		String align="center";
		     		if(itemtype.equals("N") || itemtype.equals("D"))
		     			align="center";	  
		     			if(!itemtype.equalsIgnoreCase("M")){
		     	  %>
									<td align="<%= align%>" class="RecordRow" nowrap width="10%" >
										<bean:write name="element" property="<%= itemid%>"
											filter="true" />
									</td>
									
						<%}else{ %>	
							<bean:define id="event" name="element" property="<%= itemid%>" />
							<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
								tiptext="tiptext" text="${event}"></hrms:showitemmemo>
							<td align="center" class="RecordRow" ${tiptext}  nowrap width="8%">
								${showtext}&nbsp;
							</td>
							<%}%>	
								</logic:iterate>
							</tr>
						</logic:iterate>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td align="center">
				<input type='button' class="mybutton" name="b_add"
					onclick='editEval("${interviewExamineForm.commentUserFild}","${interviewExamineForm.commentdateFild}","${interviewExamineForm.a0100}","${interviewExamineForm.dbName}","${interviewExamineForm.examineNeedRecordSet}","0")'
					value='<bean:message key="button.insert"/>' />
				<input type='button' class="mybutton"
					onclick='delEval("${interviewExamineForm.a0100}")'
					value='<bean:message key="button.delete"/>' />
					<input type='button' class="mybutton"
					onclick='window.close();'
					value='<bean:message key="button.close"/>' />
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
   var fixdiv=document.getElementById("myfixed"); 
   fixdiv.style.width=document.body.clientWidth-20;
   fixdiv.style.height=document.body.clientHeight-100;
    fixdiv.style.marginLeft="0px";
   var layouttable=document.getElementById("layouttable");
   layouttable.style.width=window.screen.width-310;
</script>
