<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.duty.DutyInfoForm,java.util.*,com.hrms.hjsj.sys.FieldSet" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.frame.utility.AdminCode"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
       DutyInfoForm dutyInfoForm=(DutyInfoForm)session.getAttribute("dutyInfoForm");
       ArrayList infoSetList = (ArrayList)dutyInfoForm.getInfoSetList();
       String kind=dutyInfoForm.getKind();
       String code = dutyInfoForm.getCode();
       String edit_flag=dutyInfoForm.getEdit_flag();  
       String postable=request.getParameter("postable");  //xuj 编制管理编辑岗位相应子集 2010-5-12  
 	   String codeitemid=request.getParameter("codeitemid");
 	   String name="";
	   if(code!=null&&code.length()>0){
			name=AdminCode.getCodeName("@K",code);
	   }
	   String return_code = dutyInfoForm.getReturn_codeid();
	   String isself = request.getParameter("isself");
       isself = isself==null?"":isself;
 %>
<html:form action="/workbench/dutyinfo/editorginfodata" style="margin-left:-2px">
<% if(infoSetList!=null&&infoSetList.size()>0)
   {
   %>
<hrms:tabset name="pageset" width="100%" height="100%" type="true" align="center"> 
<%
for(int i=0;i<infoSetList.size();i++)
{
          FieldSet set = (FieldSet)infoSetList.get(i);
          String setid=set.getFieldsetid();
          if(postable!=null&&postable.length()>0){
          	if(!setid.equalsIgnoreCase(postable))
          		continue;
          }
          String setfesc=set.getCustomdesc();
          String url="";
          if(set.getFieldsetid().equalsIgnoreCase("K01"))
          {
              url="/workbench/dutyinfo/editorginfodata.do?b_query=link&setname="+setid+"&treetype=duty";
           }
           else if(set.getFieldsetid().equalsIgnoreCase("K00")){
             session.setAttribute("code","@"+return_code);
             url="/general/inform/emp/view/multimedia_tree.do?b_query=link&isvisible=1&kind="+kind+"&a0100="+code+"&multimediaflag=&isself="+isself
            		 +"&editType="+edit_flag;
           }
           else
           {
           		 if(postable!=null&&postable.length()>0){
		          		url="/workbench/dutyinfo/searchdetailinfolist.do?b_search=link&setname="+setid+"&treetype=duty&codeitemid="+code;
		          }else{
               			url="/workbench/dutyinfo/searchdetailinfolist.do?b_search=link&setname="+setid+"&treetype=duty";
           	      }
           }
  if(set.getFieldsetid().equalsIgnoreCase("K01"))
  {
  %>
	  <hrms:tab name='<%="tab"+i%>' label='<%=setfesc%>' visible="true" url='<%=url%>'>
      </hrms:tab>
  <%}
  else if(set.getFieldsetid().equalsIgnoreCase("K00")){
  %>	
     <hrms:priv func_id="23110109"> 
	  <hrms:tab name='<%="tab"+i%>' label='<%=setfesc%>' visible="true" url='<%=url%>'>
      </hrms:tab>	
     </hrms:priv>
  <%}else{ 
  		if(postable!=null&&postable.length()>0){
  %>
	  <hrms:tab name='<%="tab"+i%>' label='<%=setfesc+"("+name+")" %>'  visible="true" url='<%=url%>'>
      </hrms:tab>
      
  <%
    }else{
    	
    %>
    <hrms:tab name='<%="tab"+i%>' label='<%=setfesc%>'  visible="true" url='<%=url%>'>
      </hrms:tab>
    <%
    }
}
}    
%>	
</hrms:tabset>
<% } %>
</html:form>