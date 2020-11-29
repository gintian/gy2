<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript">
function selphoto()
{
	var target_url="/general/inform/emp/view/displaypicture.do?br_selphoto=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(iframe_url, "ss", 
	              "dialogWidth:400px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no;");

	mInformForm.action="/general/inform/emp/view/displaypicture.do?b_query2=link"; 
	mInformForm.submit(); 
}	
</script>
<style type="text/css"> 
.viewPhoto{
     position:absolute;
     top:-10px;
     overflow:visible;
     left:0px;
}
</style>
<html:form  action="/general/inform/emp/view/displaypicture">
<div class="viewPhoto">
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<hrms:priv func_id="260641501,3233113"> 	
		<td align="center" id="picture"	 ondblclick="selphoto()"> 	
		<hrms:priv func_id="2606415,3233113">
			<hrms:ole name="mInformForm" dbpre="mInformForm.dbname"  a0100="a0100" scope="session" height="120" width="85" title="双击可以上传照片"/>
				</hrms:priv>
		</td>
		</hrms:priv>	
	</tr>
</table>
</div>
</html:form>
