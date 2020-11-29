<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
	//  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11	
	String operOrg =userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
	
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<%
	SetUnderlingObjectiveForm setUnderlingObjectiveForm=(SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm");
	
	
	
%>


<script language="JavaScript">

//展现 "MBO目标设定及审批统计表"/"MBO目标总结考评进度统计表" 页面
function showGradePage(obj)
{
	document.setUnderlingObjectiveForm.convertPageEntry.value=obj;
	setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=1";
	setUnderlingObjectiveForm.target="mil_body";
   	setUnderlingObjectiveForm.submit();	
}

</script>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list"> 

	  <table width="600" border="0" cellspacing="0"  align="center" cellpadding="1" class="mainbackground" style="position:absolute;left:0px;top:0px;">
	  
		  	 <tr align="left" class="toolbar" style="padding-left:2px;">  
			      <td valign="middle" width="100%" >			      
					  &nbsp;	
					  <hrms:priv func_id="06070603">
					  		<!--img图片提示信息 chrome下 title属性提示  wangb 20180105 -->
				  	  		<a href="javascript:showGradePage('2')" > <img src="/images/groups.gif" border="0" align="middle" title="<bean:message key='jx.selfHelp.lookTargetCardType' />" alt="<bean:message key='jx.selfHelp.lookTargetCardType' />" /></a>   				 	 				 	 
					  </hrms:priv>
					  <hrms:priv func_id="06070604">
					  		<!--img图片提示信息 chrome下 title属性提示  wangb 20180105 -->
					  		<a href="javascript:showGradePage('3')" > <img src="/images/prop_ps.gif" border="0" align="middle" title="<bean:message key='jx.selfHelp.lookLastTargetCardType' />" alt="<bean:message key='jx.selfHelp.lookLastTargetCardType' />" /></a>
					  </hrms:priv>	  
				  </td>
			 </tr>
	         
		     <tr>        
		     	  <td align="left"> 
		     	  
		     	  	  <% if (operOrg.length() > 2){ %>
            	 	  	  <hrms:orgtree action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=1" target="mil_body" flag="0"  loadtype="1" priv="1" showroot="false" viewunit="1" nmodule="5" dbpre="" rootaction="1" rootPriv="0"/>				           
 				              			
				 	  <% }else{ %>	
				 	      <hrms:orgtree action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=1" target="mil_body" flag="0"  loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1" rootPriv="0"/>				           
				 	  <% } %>					 		                 		           
		          </td>
		     </tr>            
	  </table>

<input type='hidden' name='convertPageEntry'  value='' />
	  
</html:form>
<script>
	root.openURL();
</script>
