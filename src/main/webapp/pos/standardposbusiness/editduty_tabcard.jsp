<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.pos.StandardPosForm,java.util.*,com.hrms.hjsj.sys.FieldSet" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.frame.utility.AdminCode"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag="";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  bosflag=userView.getBosflag(); 
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<%
       StandardPosForm standardPosForm=(StandardPosForm)session.getAttribute("standardPosForm");
       ArrayList infoSetList = (ArrayList)standardPosForm.getInfoSetList();
       String a_code=request.getParameter("a_code");
 %>
<html:form action="/pos/standardposbusiness/searchposlist">
<% if(infoSetList!=null&&infoSetList.size()>0)
   {
   %>
<hrms:tabset name="pageset" width="100%" height="100%" type="true" align="center"> 
<%
for(int i=0;i<infoSetList.size();i++)
{
          FieldSet set = (FieldSet)infoSetList.get(i);
          String setid=set.getFieldsetid();
          String setfesc=set.getCustomdesc();
          String url="";
          if(set.getFieldsetid().equalsIgnoreCase("H01"))
          {
              //url="/workbench/dutyinfo/editorginfodata.do?b_query=link&setname="+setid+"&treetype=duty";
           		url="/pos/standardposbusiness/searchposlist.do?b_add_edit=link";
           }
           else if(set.getFieldsetid().equalsIgnoreCase("H00")){
             url="/general/inform/emp/view/multimedia_tree.do?b_query=link&isvisible=1&a0100="+a_code+"&multimediaflag=";
           }
           else
           {
              url="/workbench/dutyinfo/searchdetailinfolist.do?b_search=link&setname="+setid+"&treetype=duty";
           }
  if(set.getFieldsetid().equalsIgnoreCase("K01"))
  {
  %>
	  <hrms:tab name='<%="tab"+i%>' label='<%=setfesc%>' visible="true" url='<%=url%>'>
      </hrms:tab>
  <%}
  else if(set.getFieldsetid().equalsIgnoreCase("K00")){
  %>	
     <hrms:priv func_id=""> 
	  <hrms:tab name='<%="tab"+i%>' label='<%=setfesc%>' visible="true" url='<%=url%>'>
      </hrms:tab>	
     </hrms:priv>
  <%}else{%>
    <hrms:tab name='<%="tab"+i%>' label='<%=setfesc%>'  visible="true" url='<%=url%>'>
      </hrms:tab>
    <%
    }
}    
%>	
</hrms:tabset>
<% } %>
</html:form>