<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.achivement.kpiOriginalData.KpiOriginalDataForm,
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

	KpiOriginalDataForm kpiOriginalDataForm=(KpiOriginalDataForm)session.getAttribute("kpiOriginalDataForm");	
	
%>

<script language="JavaScript">

//展现 "KPI指标维护" 页面
function showKpiTargetPage()
{
	kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_query=link";
	kpiOriginalDataForm.target="il_body";
   	kpiOriginalDataForm.submit();	
}

//新建 "KPI原始数据录入" 弹出窗
function newBuiltTarget()
{
	var onlyFild="${kpiOriginalDataForm.onlyFild}";
	var objecType="${kpiOriginalDataForm.objecType}";
	var currnode=Global.selectedItem; 		
	var acode= currnode.uid;
	if(objecType!=null && objecType!='undefined' && objecType=='2')
	{
		if(onlyFild==null || onlyFild.length<=0 || onlyFild=='undefined')
		{
			alert("系统没有指定唯一性指标！请指定！");
			return;
		}
	}

	var target_url="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?br_add=add";
 //	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var top= window.screen.availHeight-200>0?window.screen.availHeight-200:0;
    var left= window.screen.availWidth-350>0?window.screen.availWidth-350:0;
    top = top/2;
    left=left/2;
 	window.open(target_url, "", //【5832】绩效管理：自助服务/绩效考评/任务业绩/KPI原始数据录入，点击新增按钮，考核周期选择“月度”，页面出现横竖滚动条  jingq upd 2014.12.19
	       "width=400,height=200,top="+top+",left="+left+",modal=yes,resizable=no,scrollbars=no,status=no,location=no");	
	/* if(!return_vo)
		return;	   
	if(return_vo.flag=="true")
	{
		var khcycle=return_vo.cycle;
		var khTheyear=return_vo.theyear;
		var khThemonth=return_vo.themonth;
		
		var khthequarter;
		if(return_vo.cycle=='1')
			khthequarter=return_vo.thehalfyear;
		else if(return_vo.cycle=='2')
			khthequarter=return_vo.thequarter;		
		
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_insert=add&opt=1&acode="+acode+"&khcycle="+khcycle+"&khTheyear="+khTheyear+"&khthequarter="+khthequarter+"&khThemonth="+khThemonth;
		kpiOriginalDataForm.target="mil_body";
		kpiOriginalDataForm.submit();
	}	 */
}
function newBuiltTarget_callBack(return_vo){
	var currnode=Global.selectedItem; 		
	var acode= currnode.uid;
    if(!return_vo)
		return;	   
	if(return_vo.flag=="true")
	{
		var khcycle=return_vo.cycle;
		var khTheyear=return_vo.theyear;
		var khThemonth=return_vo.themonth;
		
		var khthequarter;
		if(return_vo.cycle=='1')
			khthequarter=return_vo.thehalfyear;
		else if(return_vo.cycle=='2')
			khthequarter=return_vo.thequarter;		
		
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_insert=add&opt=1&acode="+acode+"&khcycle="+khcycle+"&khTheyear="+khTheyear+"&khthequarter="+khthequarter+"&khThemonth="+khThemonth;
		kpiOriginalDataForm.target="mil_body";
		kpiOriginalDataForm.submit();
	}	
}

// 重新引入"KPI指标" 
function againInduction()
{
	var onlyFild="${kpiOriginalDataForm.onlyFild}";
	var objecType="${kpiOriginalDataForm.objecType}";
	
	if(objecType!=null && objecType!='undefined' && objecType=='2')
	{
		if(onlyFild==null || onlyFild.length<=0 || onlyFild=='undefined')
		{
			alert("系统没有指定唯一性指标！请指定！");
			return;
		}
	}
	if (confirm("您确认重新引入KPI指标吗？"))
	{
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_insert=add&opt=2";
		kpiOriginalDataForm.target="mil_body";
	   	kpiOriginalDataForm.submit();
	}	
}
</script>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 

<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes />
<html:form action="/performance/achivement/kpiOriginalData/orgTree"> 

	  <table width="600" border="0" cellspacing="0"  align="center" cellpadding="1" class="mainbackground" style="position:absolute;left:0px;top:0px;">
	  		 
	  		 <tr align="left" class="toolbar" style="padding-left:2px;width:expression(document.body.clientWidth);overflow: auto;">		  	   
			      <td valign="middle" width="100%" >			      	
					  <hrms:priv func_id="06080301">	
				  	  		<a href="javascript:showKpiTargetPage()" > <img src="/images/sys_config.gif" border="0" align="middle" title="<bean:message key='kpi.originalData.KpiTarget' />" /></a>   				 	 				 	 
					  </hrms:priv>
					  <hrms:priv func_id="06080302">
					  		<a href="javascript:newBuiltTarget()" > <img src="/images/add.gif" border="0" align="middle" title="<bean:message key='kpi.originalData.newBuiltTarget' />" /></a>
					  </hrms:priv>	
					  <hrms:priv func_id="06080303">
					  		<a href="javascript:againInduction()" > <img src="/images/goto_input.gif" border="0" align="middle" title="<bean:message key='kpi.originalData.againInduction' />" /></a>
					  </hrms:priv>	  
				  </td>
			 </tr>
	         
		     <tr>        
		     	  <td align="left"> 
		     	  
		     	  	  <% if (operOrg.length() > 2){ %>
            	 	  	  <hrms:orgtree action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=link" target="mil_body" flag="${kpiOriginalDataForm.flag}"  loadtype="${kpiOriginalDataForm.loadtype}" priv="1" showroot="false" viewunit="1" nmodule="5" dbpre="" rootaction="1" />				           
 				              			
				 	  <% }else{ %>	
				 	      <hrms:orgtree action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=link" target="mil_body" flag="${kpiOriginalDataForm.flag}"  loadtype="${kpiOriginalDataForm.loadtype}" priv="1" showroot="false" dbpre="" rootaction="1" />				           
				 	  <% } %>					 	  
				 				 		                 		           
		          </td>
		     </tr>            
	  </table>
	  
</html:form>
<script>
	root.openURL();
</script>
