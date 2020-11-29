<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String acode="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  //acode=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String org_tree_expand=SystemConfig.getPropertyValue("org_tree_expand");
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<script language="javascript">
  function getemp()
  {
     var targetobj,hiddenobj;
     var currnode=Global.selectedItem;
     if(currnode==null)
        	return;  
     var id = currnode.getSelected();    
     if(id!="")
     {
        statForm.action="/general/static/history/searchstaticdata.do?b_query=link&acode="+id;
        statForm.target="nil_body";
        statForm.submit();
     }else
     {
         alert("请先选择单位部门，再进行统计！");
     }
  }
</script>
<html:form action="/general/static/history/statshow"> 
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       <tr>
           <td align="left"> 
            <div id="treemenu" onclick="getemp();"> 
             <SCRIPT LANGUAGE="javascript">                  
              Global.showroot=false;              
              Global.defaultInput=1;
              Global.checkvalue=",${statForm.acode},";
              Global.showorg=1;
               <bean:write name="statForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
 <%if(org_tree_expand!=null&&org_tree_expand.equalsIgnoreCase("true")) {%>
    <script type="text/javascript">
	root.expandAll();
    </script>
  <%} %>
</html:form>
