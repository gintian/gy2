<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hjsj.hrms.actionform.train.resource.course.CoursewareForm"%>
<%@ page
	import="com.hrms.hjsj.sys.FieldItem,com.hrms.frame.codec.SafeCode"%>

<%
	String url = null;
	String leibie = null;
	CoursewareForm cf = (CoursewareForm) session
			.getAttribute("coursewareForm");
	List itemlist = cf.getItemlist();
	for (int i = 0; i < itemlist.size(); i++) {
		FieldItem fielditem = (FieldItem) itemlist.get(i);
		if (fielditem.getItemid().equalsIgnoreCase("r5113")) {
			url = fielditem.getValue().replace("\\", "'");
			url = SafeCode.encode(url);
		}
		if (fielditem.getItemid().equalsIgnoreCase("r5105")) {
			leibie = fielditem.getValue().trim();
		}
	}
%>
<html>
	<head>
	</head>
	<body>
		<html:form action="/train/resource/courseware">
			<table>
				<logic:iterate id="element" name="coursewareForm"
					property="itemlist" indexId="index">
					<logic:notEqual value="r5100" name="element" property="itemid">
						<logic:notEqual value="r5113" name="element" property="itemid">
							<logic:notEqual value="r5115" name="element" property="itemid">
								<tr>
									<td width="100" valign="top">
										<bean:write name="element" property="itemdesc"></bean:write>
										：
									</td>
									<td>
										&nbsp;
										<bean:write name="element" property="value" filter="false"></bean:write>
									</td>
								</tr>
							</logic:notEqual>
						</logic:notEqual>
						<logic:equal value="r5113" name="element" property="itemid">
							<%
								if (!leibie.equalsIgnoreCase("文本课件")) {
							%>
							<tr>
								<td width="100" valign="top">
									<bean:message key="train.course.downcourseware" />
									：
								</td>
								<td>
									&nbsp;
									<a
										href="<%=request.getContextPath()%>/DownLoadCourseware?url=<%=url%>"><bean:message
											key="train.course.loadcourseware"></bean:message> </a>
								</td>
							</tr>
							<%
								}
							%>
						</logic:equal>
						<logic:equal value="r5115" name="element" property="itemid">
							<%
								if (leibie.equalsIgnoreCase("文本课件")) {
							%>
							<tr>
								<td width="100" valign="top">
									<bean:write name="element" property="itemdesc"></bean:write>
									：
								</td>
								<td>
									&nbsp;
									<bean:write name="element" property="value" filter="false"></bean:write>
								</td>
							</tr>
							<%
								}
							%>
						</logic:equal>
					</logic:notEqual>
				</logic:iterate>
			</table>
		</html:form>
	</body>
</html>
