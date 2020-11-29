<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateOthForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.valueobject.PaginationForm"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<% 
TemplateOthForm templateOthForm=(TemplateOthForm)session.getAttribute("templateOthForm");
ArrayList list=templateOthForm.getRelist();
out.println("<script language=\"javascript\">");
out.println("var objlist=new Array();");
if(list!=null)
{
   for(int i=0;i<list.size();i++)
   {
      RecordVo vo=(RecordVo)list.get(i);
      out.println("var thevo=new Object();");
      out.println("thevo.role_id='"+vo.getString("role_id")+"';");
	  out.println("thevo.role_name='"+vo.getString("role_name")+"';");
      out.println("objlist.push(thevo);");
   }
}
out.println("returnValue=objlist;");
out.println("parent.parent.window.opener.openRoleHistoryReturn(objlist);");
out.println("parent.parent.window.close();");
out.println("</script>");
%>
