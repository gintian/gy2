<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>
<%


boolean  str = false;	
String flag = (String)request.getParameter("flag");
if(flag!=null&&flag.equals("templet")){
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	if(templateForm!=null&&templateForm.getLimit_manage_priv()!=null){
	//获得变化后设置的管理范围
	String codesetid = (String)request.getParameter("codesetid");
	String limit_manage_priv = templateForm.getLimit_manage_priv();
	if(limit_manage_priv!=null&&limit_manage_priv.trim().length()>0&&codesetid!=null&&codesetid.trim().length()>0&&limit_manage_priv.indexOf(codesetid)!=-1)
		str = true;
	if(limit_manage_priv!=null&&codesetid!=null&&limit_manage_priv.indexOf("UN")!=-1&&(codesetid.equalsIgnoreCase("UM")||codesetid.equalsIgnoreCase("@K")))
			str = true;	
		
	}
}

%>

<html:form action="/general/muster/select_code_tree" style="margin:-7px 0 0 -5px;">
<hrms:codetree codesetid="${codeSelectForm.codesetid}" parent_id="${codeSelectForm.parent_id}"  setname="codeset1" pagerows="1000" dropdown="true" priv_ctrl="<%=str %>">
</hrms:codetree> 

</html:form>
<script language="javascript">

function test()
{
   var node=treecodeset.getCurrentNode();
	alert(node.getLabel());
   var rec=node.getRecord();
	alert(rec.getValue("codeitemid"));
}

</script>


