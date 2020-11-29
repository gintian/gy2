<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			
%>
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
		<hrms:themes></hrms:themes>
<script language="jscript">
       var caption = "<bean:message key="menu.rule"/>"
	   <logic:equal name="resourceForm" property="res_flag" value="19">
	      caption = "<bean:message key="law_maintenance.file"/>";
	   </logic:equal>
	   <logic:equal name="resourceForm" property="res_flag" value="21">
	      caption = "<bean:message key="sys.res.knowtype"/>";
	   </logic:equal>	
	    <logic:equal name="resourceForm" property="res_flag" value="22">
	      caption = "<bean:message key="lable.kh.template"/>";
	   </logic:equal> 
	   <logic:equal name="resourceForm" property="res_flag" value="23">
	      caption = "<bean:message key="kh.field.class"/>";
	   </logic:equal> 
	   Global.defaultInput=1;
	   Global.showroot=false;
	   Global.checkvalue="<bean:write name="resourceForm" property="law_dir" />";
	   function save()
	   {
	   	  var str_id=root.getSelected();
	   	  //if(str_id=="")
	   	  //  return;
          var hashvo=new ParameterSet();
          hashvo.setValue("flag","<bean:write name="resourceForm" property="flag" />");
          hashvo.setValue("roleid","<bean:write name="resourceForm" property="roleid" />");	        
          hashvo.setValue("res_flag","<bean:write name="resourceForm" property="res_flag" />");
          hashvo.setValue("law_dir",str_id);
   　      	  var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'10400201021'},hashvo);        
	   }
	   //操作成功提示    jingq   add    2014.5.7
	   function save_ok(outparamters){
	   	   var isCorrect = outparamters.getValue("isCorrect");
		   if("true"==isCorrect){
			   alert("保存成功!");
		   }
	   }
</script>		
<html:form action="/selfservice/lawbase/lawtext/assign_law_dir">
		<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
			<tr align="left">
				<td valign="middle" height="30px;">
					&nbsp;&nbsp;&nbsp;
					<html:button styleClass="mybutton" property="b_save" onclick="save();"><bean:message key="button.save"/></html:button>
					<html:button styleClass="mybutton" property="b_all" onclick="root.allSelect();"><bean:message key="label.query.selectall"/></html:button>
					<html:button styleClass="mybutton" property="b_clear" onclick="root.allClear();"><bean:message key="label.query.clearall"/></html:button>
				</td>
			</tr>	
			<tr>
				<td valign="top">
					<div id="treemenu"></div>
				</td>
			</tr>
	

		</table>

</html:form>

<SCRIPT LANGUAGE=javascript>

var m_sXMLFile	= "/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?params=<%=PubFunc.encrypt("base_id=up_base_id")%>&basetype=1&action=0";
	   <logic:equal name="resourceForm" property="res_flag" value="19">
	      m_sXMLFile	= "/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?params=<%=PubFunc.encrypt("base_id=up_base_id")%>&basetype=5&action=0";
	   </logic:equal> 
	   <logic:equal name="resourceForm" property="res_flag" value="21">
	      m_sXMLFile	= "/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?params=<%=PubFunc.encrypt("base_id=up_base_id")%>&basetype=4&action=0";
	   </logic:equal> 	 
	    <logic:equal name="resourceForm" property="res_flag" value="22">
	      m_sXMLFile	="/performance/kh_system/kh_template/priv_tree.jsp?parent_id=-1&res_flag=22&level=0&flag=<bean:write name="resourceForm" property="flag" />&roleid=<bean:write name="resourceForm" property="roleid" />";
	   </logic:equal> 
	    <logic:equal name="resourceForm" property="res_flag" value="23">
	      m_sXMLFile	="/performance/kh_system/kh_template/priv_tree.jsp?parent_id=-1&res_flag=23&level=0&flag=<bean:write name="resourceForm" property="flag" />&roleid=<bean:write name="resourceForm" property="roleid" />";
	   </logic:equal> 		     
var newwindow;
var root=new xtreeItem("root",caption,"","mil_body",caption,"/images/add_all.gif",m_sXMLFile);
root.setup(document.getElementById("treemenu"));
if(newwindow!=null)
{
newwindow.focus();
}
if(parent.parent.myNewBody!=null)
 {
	parent.parent.myNewBody.cols="*,0"
 }
  <logic:equal name="resourceForm" property="res_flag" value="22">
  root.expandAll();
  </logic:equal> 
   <logic:equal name="resourceForm" property="res_flag" value="23">
   root.expandAll();
    </logic:equal> 
</SCRIPT>
