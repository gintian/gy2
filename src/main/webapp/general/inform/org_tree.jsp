<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.general.inform.MInformForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<bean:define id="ifg" name="mInformForm" property="inforflag"></bean:define>
<%!
	private static String org_expand_level;
	static{
		org_expand_level=com.hrms.struts.constant.SystemConfig.getPropertyValue("org_expand_level");
	}
 %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
	String viewunit="1";
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
	if("2".equals(ifg)&&userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  	 
	 	if(userView.getStatus()==4||userView.isSuper_admin()){
			viewunit="0";
		}
		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
		if(userView.getStatus()==0&&!userView.isSuper_admin()){
			String codeall = userView.getUnit_id();
			if(codeall==null||codeall.length()<3)
				viewunit="0";
		}
	}else
		viewunit="0";
%> 
<script LANGUAGE="javascript">	
function onchanges(obj){
	var codeitem = obj.value;
	self.parent.location="/general/inform/org_tree.do?b_query=link&codeitem="+codeitem+"&inforflag=${mInformForm.inforflag}&modleflag=${mInformForm.modleflag}";
}
</script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script LANGUAGE=javascript src="/js/xtree.js"></script>
<html:form action="/general/inform/get_org_tree"> 
<input type="hidden" name="photo_state">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1"  style="margin-top: 10px;">   
	 <tr>
		<td valign="top" nowrap><bean:message key="general.inform.search.navigation"/>
			<html:select name="mInformForm" property="codeitem"  onchange="onchanges(this);">
    			<html:optionsCollection property="codeitemlist" value="dataValue" label="dataName" />
 			</html:select> 
		</td>
	 </tr>              
   </table>
   <logic:notEqual name="mInformForm" property="codeitemid" value="">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >      
     <tr>        
           <td align="left"> <!-- viewunit="<%=viewunit%>" -->
                 <hrms:orgtree action="/general/inform/get_data_table.do?b_rmain=link&flag=1&inforflag=${mInformForm.inforflag}&codeitemid=${mInformForm.codeitemid}" target="ril_body1" flag="0" nmodule="6" loadtype="0" lv="1" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
           </td>
      </tr>            
   </table>
   </logic:notEqual>
    <logic:equal name="mInformForm" property="codeitemid" value="">
   <logic:equal name="mInformForm" property="codeitem" value="UN">
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >      
     <tr>        
           <td align="left"> <!-- viewunit="<%=viewunit%>" -->
                 <hrms:orgtree action="/general/inform/get_data_table.do?b_rmain=link&flag=1&inforflag=${mInformForm.inforflag}" target="ril_body1" flag="0" nmodule="6" loadtype="0" priv="1" showroot="false" lv="1" dbpre="" rootaction="1"/>			           
           </td>
      </tr>            
   </table>
   </logic:equal>
   <logic:notEqual name="mInformForm" property="codeitem" value="UN">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>   	
   		    var objs=document.getElementsByName("codeitem");
   		    var obj = objs[0];
   		    var theName='';//"<bean:message key='general.inform.search.org'/>"
			for(var i=0;i<obj.options.length;i++)
	        {
	 		  if(obj.options[i].selected)
	   		  {
   				theName=obj.options[i].text;
   				break;
   			  }
   			}
             var m_sXMLFile="/general/inform/orgcode_tree.jsp?codesetid=${mInformForm.codeitem}";
                 m_sXMLFile +="&codeitemid="; 
                 m_sXMLFile +="&parentid=";       
             var root=new xtreeItem("root",theName,"/general/inform/get_data_table.do?b_rmain=link&flag=1&a_code=all","ril_body1",theName,"/images/unit.gif",m_sXMLFile);
             root.setup(document.getElementById("treemenu"));
   </SCRIPT> 
   </logic:notEqual>
   </logic:equal>
</html:form>
<script LANGUAGE="javascript">
	root.openURL();
	<%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
</script>
