<%@page import="java.net.URLEncoder"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
		String opt=request.getParameter("opt");
		if(opt==null){
			opt="1";
		}
		String etoken = request.getParameter("etoken");
%>
<style>
table.ftable td {
	word-break: normal;
}
	.titletd{
		width: 80px;
	}
</style>
<html:form action="/selfservice/infomanager/board/viewboard" method="get">
      <table width="800px" style="margin-top:6px;" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
		  <tr height="20">
			  <td align="left" colspan="2" class="TableRow"><bean:message key="lable.board.manager"/>&nbsp;</td>
		  </tr>
		  <tr>
			  <td class="titletd" align="right" nowrap valign="middle">
				  <bean:message key="conlumn.board.topic"/></td>
			  <td style="word-break:break-all"><bean:write name="boardForm" property="boardvo.string(topic)"
														   filter="true"/></td>
		  </tr>
		  <tr height="200">
			  <td align="right" class="titletd" nowrap valign="middle">
				  <bean:message key="conlumn.board.content"/></td>
			  <td>
				  <bean:write name="boardForm" property="boardvo.string(content)" filter="false"/>&nbsp;
			  </td>
		  </tr>
		  <%if (!opt.equalsIgnoreCase("2")) { %>
		  <tr>
			  <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.createuser"/></td>
			  <td align="left" nowrap valign="middle">
				  <bean:write name="boardForm" property="boardvo.string(createuser)" filter="true"/>&nbsp;
			  </td>
		  </tr>

		  <tr>
			  <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.createtime"/></td>
			  <td align="left" nowrap valign="middle">
				  <bean:write name="boardForm" property="boardvo.string(createtime)" filter="true"/>&nbsp;
			  </td>
		  </tr>
		  <tr>
			  <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.period"/></td>
			  <td align="left" nowrap valign="middle">
				  <bean:write name="boardForm" property="boardvo.string(period)" filter="true"/>&nbsp;
			  </td>
		  </tr>

		  <tr>
			  <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.approveuser"/></td>
			  <td align="left" nowrap valign="middle">
				  <bean:write name="boardForm" property="boardvo.string(approveuser)" filter="true"/>&nbsp;
			  </td>
		  </tr>

		  <tr>
			  <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.approvetime"/></td>
			  <td align="left" nowrap valign="middle">
				  <bean:write name="boardForm" property="boardvo.string(approvetime)" filter="true"/>&nbsp;
				  &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
				  <logic:notEmpty name="boardForm" property="boardvo.string(ext)">
					  <logic:notEqual name="boardForm" property="boardvo.string(ext)" value="">
						  <logic:notEqual name="boardForm" property="boardvo.string(ext)" value="null">
							  <bean:define id="boardid" name="boardForm" property="boardvo.string(id)"/>
							  <bean:define id="boardtopic" name="boardForm" property="boardvo.string(topic)"/>
							  <bean:define id="fileid" name="boardForm" property="boardvo.string(fileid)"/>							  
								 <a href='/servlet/vfsservlet?fileid=<%=fileid.toString() %>'
								 target="_blank">
								 <bean:message key="conlumn.baord.accessoriesview"/></a>
						  </logic:notEqual>
					  </logic:notEqual>
				  </logic:notEmpty>
			  </td>
		  </tr>

		  <%} %>
		  <%
			  //【59598】判断是否是单点进入的，如果是不显示返回按钮 guodd 2020-05-07
			  if(etoken==null){ %>
		  <tr>
			  <td align="center" colspan="2" style="height: 35px">
				  <hrms:submit styleClass="mybutton" property="br_return">
					  <bean:message key="button.return"/>
				  </hrms:submit>
			  </td>
		  </tr>
		  <%}%>
	  </table>
</html:form>
