<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%!
	private static String org_expand_level;
	static{
		org_expand_level=com.hrms.struts.constant.SystemConfig.getPropertyValue("org_expand_level");
	}
 %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>		     
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script LANGUAGE="javascript">
function onchanges(obj){
	var codeitem = obj.value;
	self.parent.location="/org/orgdata/org_tree.do?b_org=link&infor=${orgDataForm.infor}&codeitem="+codeitem;
}
</script> 
<html:form action="/org/orgdata/org_tree"> 
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr>
		<td valign="top" nowrap><bean:message key="general.inform.search.navigation"/>
			<html:select name="orgDataForm" property="codeitem"  onchange="onchanges(this);" style="width:150">
    			<html:optionsCollection property="codeitemlist" value="dataValue" label="dataName" />
 			</html:select> 
		</td>
	 </tr>              
</table>
 <logic:equal name="orgDataForm" property="codeitem" value="UN">
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="/org/orgdata/orgdata.do?b_rmain=link&infor=${orgDataForm.infor}" target="ril_body1" flag="0"  loadtype="${orgDataForm.loadtype}" priv="1" showroot="false" lv="1" dbpre="" rootaction="1"/>			           
           </td>
      </tr>            
   </table>
</logic:equal>
 <logic:equal name="orgDataForm" property="checkorg" value="UN">
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="/org/orgdata/orgdata.do?b_rmain=link&infor=${orgDataForm.infor}" target="ril_body1" flag="0"  loadtype="${orgDataForm.loadtype}" priv="1" showroot="false" lv="1" dbpre="" rootaction="1"/>			           
           </td>
      </tr>            
   </table>
</logic:equal>
<logic:notEqual name="orgDataForm" property="codeitem" value="UN">
<logic:notEqual name="orgDataForm" property="checkorg" value="UN">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>   			
   		    var objs=document.getElementsByName("codeitem");
   		    var obj = objs[0];
   		    var theName='';
			for(var i=0;i<obj.options.length;i++)
	        {
	 		  if(obj.options[i].selected)
	   		  {
   				theName=obj.options[i].text;
   				break;
   			  }
   			}
             var m_sXMLFile="/org/orgdata/get_code_tree.jsp?codesetid=${orgDataForm.codeitem}&infor=${orgDataForm.infor}";
                 m_sXMLFile +="&codeitemid="; 
                 m_sXMLFile +="&parentid=";       
             var root=new xtreeItem("root",theName,"/org/orgdata/orgdata.do?b_rmain=link&infor=${orgDataForm.infor}&a_code=all","ril_body1",theName,"/images/unit.gif",m_sXMLFile);
             root.setup(document.getElementById("treemenu"));
   </SCRIPT> 
</logic:notEqual>
</logic:notEqual>
</html:form>
<script>
	root.openURL();
	<%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
</script>
