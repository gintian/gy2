<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/competencymodal/postseq_commodal/postmodal.js"></script>
<html:form action="/competencymodal/postseq_commodal/post_modal_tree">

<table width="440px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td class="TableRow" align="center">历史时点查询
</td>
</tr>
<tr>
<td align="center" class="RecordRow">
<table><tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td align="center">
<input id="histime"  type="text"  size="20" value="${postModalForm.historyDate}" name="historyDate" extra="editor" id="editor2"  dropDown="dropDownDate"/>
</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
</table>
</td>
</tr>
<tr>
<td align="center" style="padding-top:3px;">
<input type="button" name="ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="queryHistory();"/>

<input type="button" name="cal" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();"/>
</td>
</tr>
</table>
</html:form>