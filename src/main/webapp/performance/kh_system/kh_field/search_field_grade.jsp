<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
-->
</style>
<script type="text/javascript">
<!--
function searchDesc(theurl,point_id)
{
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    point_id_win=point_id;

    if (window.showModalDialog) {
        var return_vo= window.showModalDialog(iframe_url, arguments,
            "dialogWidth:560px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
        searchDesc_ok(return_vo);

    } else {
    	var top= window.screen.availHeight-320>0?window.screen.availHeight-320:0;
        var left= window.screen.availWidth-510>0?window.screen.availWidth-510:0;
        top = top/2;
        left = left/2;
        
        window.open(iframe_url, arguments, "top="+top+",left="+left+",width=560; height=410;resizable=no;center=yes;scroll=no;status=no");
    }
}
function searchDesc_ok(return_vo) {
    if(return_vo)
    {
        khFieldForm.action="/performance/kh_system/kh_field/search_field_grade.do?b_init=init&point_id="+point_id_win;
        khFieldForm.target="ril_body2";
        khFieldForm.submit();
    }
}
//-->
</script>
<html:form action="/performance/kh_system/kh_field/search_field_grade">

<table width="99%%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:10px;margin-left:5px;">
<tr>
	<td width="100%" align="left">
<table width="99%" border="0" cellpmoding="0" cellspacing="0" cellpadding="0" class="ListTable">
<thead>
<tr>
<td class="TableRow" align="center" width="5%" nowrap>
<bean:message key="kh.field.seq"/>
</td>
<td class="TableRow" align="center" width="22%" nowrap>
<bean:message key="kh.field.bzbd"/>
</td>
<td class="TableRow" align="center"  nowrap>
<bean:message key="kh.field.content"/>
</td>
<td class="TableRow" align="center" width="7%" nowrap>
<bean:message key="kh.field.scale"/>
</td>
<td class="TableRow" align="center" width="7%" nowrap>
<bean:message key="kh.field.topv"/>
</td>
<td class="TableRow" align="center" width="7%" nowrap>
<bean:message key="kh.field.bottomv"/>
</td>
</tr>
</thead>
<% int i=0; %>
<logic:iterate id="element" name="khFieldForm" property="fieldGradeList" offset="0" indexId="index">
<%if(i%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}%>
	     <td class="RecordRow" align="right" width="5%">
	     	<bean:write name="element" property="seq"/>
	     </td>
	     <td class="RecordRow" align="left" width="22%">
	     	<bean:write name="element" property="bz"/>
	     </td>
	     <td class="RecordRow" align="left" >
	     	<a href="javascript:searchDesc('/performance/kh_system/kh_field/search_field_grade.do?b_desc=desc`gradeid=<bean:write name="element" property="grade_id"/>','${khFieldForm.grade_point_id}')"><bean:write name="element" property="gradedesc" filter="false"/></a>
	     </td>
	     <td class="RecordRow" align="right" width="7%">
	     	<bean:write name="element" property="gradevalue"/>
	     </td>
	     <td class="RecordRow" align="right" width="7%">
	     	<bean:write name="element" property="top_value"/>
	     </td>
	     <td class="RecordRow" align="right" width="7%">
	     	<bean:write name="element" property="bottom_value"/>
	     </td>
  </tr>
 <%i++; %>
</logic:iterate>
</table>

</td>
</tr>
</table>
</html:form>