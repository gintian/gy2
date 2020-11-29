<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	    // 在标题栏显示当前用户和日期 2004-5-10 
	    String userName = null;
	    String css_url = "/css/css1.css";
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    if (userView != null)
	    {
			css_url = userView.getCssurl();
			if (css_url == null || css_url.equals(""))
			    css_url = "/css/css1.css";
	    }
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script>
<hrms:themes />
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script language="javascript" src="/performance/achivement/kpiOriginalData/kpiOriginalData.js"></script>
<script language="JavaScript"src="../../../js/showModalDialog.js"></script> 
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>

<style>

.keyMatterDiv 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10);
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
}
.textInterface 
{
	BACKGROUND-COLOR:transparent;
	font-size: 12px;
	height:22;
	border: 1pt solid #94B6E6;
}

</style>

<script language="JavaScript">

function selectobject()
{
	 var return_vo = select_org_dialog1(0,2,1,1,0,1,5);
	 var affB0110Desc = document.getElementById("affB0110Desc");
	 var affB0110 = document.getElementById("affB0110");
	 if(return_vo){
		  if(return_vo==null)
		    return false;
		 
		  affB0110Desc.value=return_vo.title;
		  affB0110.value=return_vo.content;
	 }else{
		 var interval = setInterval(function(){
				if(window.returnValue){
					 affB0110Desc.value=window.returnValue.title;
					 affB0110.value=window.returnValue.content;
					 //clearInterval(interval);
				}else
					return;
	     },300);
	 }
	 
}

// 返回KPI原始数据录入主界面
function gobackData()
{
	kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/orgTree.do?b_query=link&action=kpiOriginalDataList.do&treetype=duty";
	kpiOriginalDataForm.target="il_body";
	kpiOriginalDataForm.submit();		
}

</script>

<html:form action="/performance/achivement/kpiOriginalData/kpiTargetAssertList">
		
	<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">

		<tr>
			<td align="left" style="position:relative;height:20px;line-height:25px;">
											
				<bean:message key="kpi.originalData.targetName" />:
				<html:text name="kpiOriginalDataForm" property="targetName" styleClass="inputtext"/>				
			
				&nbsp;&nbsp;
				<bean:message key="kpi.originalData.targetType" />:
				<html:select name="kpiOriginalDataForm" property="targetType" size="1"
					onchange="" >					
					<html:optionsCollection property="targetTypeList" value="dataValue" label="dataName" />
				</html:select>
				
				&nbsp;&nbsp;
				<bean:message key="kpi.originalData.targetAffiliationB0110" />:		
				
				<html:text maxlength="50" size="30" 
						name="kpiOriginalDataForm" styleId="affB0110Desc"
						property="affB0110Desc" readonly="true" styleClass="inputtext"/>
						<span>
						<a onclick="selectobject();" style="position:absolute;top:4px;">							
							<img src="/images/code.gif" border=0>																					
						</a>
						</span>
						<html:hidden name="kpiOriginalDataForm" styleId="affB0110" property="affB0110"/>				
				<input type='button' style='margin-left:20px;' class="mybutton" property="checked" onclick='searchKpiTarget();'
						value='<bean:message key="infor.menu.query"/>' />
			</td>
			<%
			int i = 0;
			%>
		</tr>
		<tr><td width='100%'    >		
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<thead>
        			<tr>	
					
						<td align="center" class="TableRow" width='25px' nowrap>
							<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'setlistform.select');">
						</td>						
						<td align="center" class="TableRow" width='40px' nowrap>
							<bean:message key="kpi.originalData.targetXuhao" />
						</td>																		
						<td align="center" class="TableRow" width='13%' nowrap>
							<bean:message key="kpi.originalData.targetNumber" />
						</td>
						<td align="center" class="TableRow" width='13%' nowrap>
							<bean:message key="kpi.originalData.targetName" />
						</td>						
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kpi.originalData.targetType" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="jx.khplan.cycle" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kpi.originalData.targetDateBegin" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kpi.originalData.targetDateEnd" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kpi.originalData.targetAffiliationB0110" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kpi.originalData.targetDescription" />
						</td>	
						
						<hrms:priv func_id="0608030102">
							<td align="center" class="TableRow" nowrap>
								<bean:message key="kpi.originalData.targetEdit" />
							</td>											
						</hrms:priv>							
					</tr>
				</thead>
					<hrms:extenditerate id="element" name="kpiOriginalDataForm"
						property="setlistform.list" indexes="indexes"
						pagination="setlistform.pagination" pageCount="16" scope="session">
						<bean:define id="nid" name="element" property="id" /> 
						<%
							if (i % 2 == 0)
							{
						%>
						<tr class="trShallow">
						<%
							}else{
						%>						
						<tr class="trDeep">
						<%
							}
							i++;
						%>
							<td align="center" class="RecordRow" >
								<Input type='hidden' id="${nid}" />
								<hrms:checkmultibox name="kpiOriginalDataForm"
										property="setlistform.select" value="true" indexes="indexes" />
								<Input type='hidden'
										value='<bean:write name="element" property="id" filter="true"/>' />
							</td>
							
							<td align="center" class="RecordRow" nowrap>								
								<bean:write name="element" property="numbers" filter="true" />								
							</td>								
	
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<bean:write name="element" property="item_id" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
																							
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<bean:write name="element" property="item_type_desc" filter="true" />
							</td>
							<td align="center" class="RecordRow" nowrap>								
								<logic:equal name="element" property="cycle" value="0">
									年度
								</logic:equal>							
								<logic:equal name="element" property="cycle" value="1">
									半年
								</logic:equal>
								<logic:equal name="element" property="cycle" value="2">
									季度
								</logic:equal>							
								<logic:equal name="element" property="cycle" value="3">
									月度
								</logic:equal>
							</td>
							<td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="start_date" filter="true" />
								&nbsp;
							</td>
							<td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="end_date" filter="true" />
								&nbsp;
							</td>							
							
							<bean:define id="eventen" name="element" property="b0110desc" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${eventen}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext}  nowrap>&nbsp;
									${showtext}&nbsp;
								</td>
							
							<bean:define id="event" name="element" property="description" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${event}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext} nowrap>&nbsp;
									${showtext}&nbsp;
								</td>
							
							<hrms:priv func_id="0608030102">														
								<td align="center" class="RecordRow" >									
									<a onclick="kpiTargetEdit('<bean:write name="element" property="item_id" filter="true"/>');"><img
												src="/images/edit.gif" border=0> 
									</a>									
								</td>								
							</hrms:priv>
<!-- 								
							<td align="left" class="RecordRow" nowrap>&nbsp;								
								<hrms:codetoname codeid="UN" name="element"
										codevalue="string(b0110)" codeitem="codeitem" scope="page" />
								<bean:write name="codeitem" property="codename" />
								<hrms:codetoname codeid="UM" name="element"
										codevalue="string(b0110)" codeitem="codeitem" scope="page" />
								<bean:write name="codeitem" property="codename" />																
							</td>																										
-->								
						</tr>
					</hrms:extenditerate>
				</table>

			</td>
		</tr>
	</table>
	<table width="95%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				第
				<bean:write name="kpiOriginalDataForm"
					property="setlistform.pagination.current" filter="true" />
				页 共
				<bean:write name="kpiOriginalDataForm"
					property="setlistform.pagination.count" filter="true" />
				条 共
				<bean:write name="kpiOriginalDataForm"
					property="setlistform.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="kpiOriginalDataForm"
						property="setlistform.pagination" nameId="setlistform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">	
		<tr>
			<td align="center" style="height:35px"> 			
				<hrms:priv func_id="0608030101">	
					<input type='button' class="mybutton" property="b_add"
						onclick='addTarget();'
						value='<bean:message key="button.insert"/>' />
				</hrms:priv>													
				<hrms:priv func_id="0608030103">	
					<input type='button' class="mybutton" property="b_abolish"
						onclick='delAboKpiTarget("abolish");'
						value='<bean:message key="button.abolish"/>' />
				</hrms:priv>
				<hrms:priv func_id="0608030104">		
					<input type='button' class="mybutton" property="b_delete"
						onclick='delAboKpiTarget("del");'
						value='<bean:message key="button.delete"/>' />
				</hrms:priv>
				
				<input type='button' class="mybutton" property="b_back"						
					onclick='gobackData();' 
				value='<bean:message key='button.return' />' />	
			</td>
		</tr>
	</table>
</html:form>
