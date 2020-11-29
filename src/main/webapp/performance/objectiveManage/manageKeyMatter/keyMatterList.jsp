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

<style>

.keyMatterDiv 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10);
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid ; 
}

</style>

<script LANGUAGE=javascript src="/js/xtree.js"></script>
<hrms:themes />
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script language="javascript" src="/performance/objectiveManage/manageKeyMatter/keyMatter.js"></script>

<html:form action="/performance/objectiveManage/manageKeyMatter/keyMatterList">

	<html:hidden name="manageKeyMatterForm" property="objecType" styleId="objecType" />
	<html:hidden name="manageKeyMatterForm" property="objecType" styleId="object_Types" />
	<html:hidden name="manageKeyMatterForm" property="kind" styleId="kind" />
	<html:hidden name="manageKeyMatterForm" property="code" styleId="code" />
	<html:hidden name="manageKeyMatterForm" property="dbname" styleId="dbname" />
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<tr>
			<td align="left" style="height:20px">
				&nbsp;&nbsp;
				<bean:message key="jx.objectiveManage.manageKeyMatter.objectType" />:
				<html:select name="manageKeyMatterForm" property="objecType" size="1"
					onchange="refreshTree(this);" style="width:80px">					
					<html:optionsCollection property="objecTypeList" value="dataValue" label="dataName" />
				</html:select>
				
				&nbsp;&nbsp;
				<bean:message key="jx.khplan.yeardu" />:
				<html:select name="manageKeyMatterForm" property="year" size="1"
					onchange="search();" style="width:80px">
					<html:option value="">
							&nbsp;
						</html:option>
					<html:optionsCollection property="yearList" value="dataValue"
						label="dataName" />
				</html:select>
				&nbsp;&nbsp;
				
				<logic:equal name="manageKeyMatterForm" property="objecType" value="1">
					<bean:message key="b0110.label" />:
					<html:text name="manageKeyMatterForm" property="checkName" styleClass="inputtext"/>
				</logic:equal>
				<logic:equal name="manageKeyMatterForm" property="objecType" value="2">
					<bean:message key="label.title.name" />:
					<html:text name="manageKeyMatterForm" property="checkName" styleClass="inputtext" style="height:20px;"/>
				</logic:equal>
				&nbsp;
				<input type='button' class="mybutton" property="checked" onclick='checkObjectKey()' style="margin-bottom:2px;"
						value='<bean:message key="infor.menu.query"/>' />
			</td>
			<%
			int i = 0;
			%>
		</tr>
		<tr>
			<td>
			   <div id='keyMatterDiv' class="keyMatterDiv common_border_color">
				<table width="100%" border="0"  align="center"
					 class="ListTable">
					<tr id="myFixedTr">
						<td align="center" class="TableRow_right common_background_color common_border_color" nowrap>
							<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'setlistform.select');">
						</td>
						<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
							<bean:message key="jx.jifen.period" />
						</td>
						<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
							
							<logic:equal name="manageKeyMatterForm" property="objecType" value="1">
								<bean:message key="org.performance.unorum"/>
							</logic:equal>
							<logic:equal name="manageKeyMatterForm" property="objecType" value="2">
								<bean:message key="org.performance.unorum"/>
							</logic:equal>
							
						</td>
						<logic:equal name="manageKeyMatterForm" property="objecType"
							value="2">
							<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
								<bean:message key="hire.employActualize.name" />
							</td>
						</logic:equal>
						<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
							<bean:message key="jx.jifen.matter" />
						</td>
						<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
							<bean:message key="jx.key_event.keyevent" />
						</td>
						<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
							<bean:message key="lable.performance.singleGrade.value" />
						</td>
						<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
							<bean:message key="label.zp_resource.status" />
						</td>						
							
						<hrms:priv func_id="06070505">	
							<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
								<bean:message key="kh.field.opt" />
							</td>
						</hrms:priv>	
						
					</tr>

					<hrms:extenditerate id="element" name="manageKeyMatterForm"
						property="setlistform.list" indexes="indexes"
						pagination="setlistform.pagination" pageCount="20" scope="session">
						<bean:define id="nid" name="element" property="string(event_id)" />
						<%
								if (i % 2 == 0)
								{
						%>
						<tr class="trShallow">
							<%
									} else
									{
							%>
						
						<tr class="trDeep">
							<%
									}
									i++;
							%>
							<td align="center" class="RecordRow_right" nowrap>
								<Input type='hidden' id="${nid}" />
								<hrms:checkmultibox name="manageKeyMatterForm"
									property="setlistform.select" value="true" indexes="indexes" />
								<Input type='hidden'
									value='<bean:write name="element" property="string(event_id)" filter="true"/>' />
							</td>
							<td align="right" class="RecordRow" nowrap>							
								<bean:write name="element" property="string(busi_date)"
									filter="true" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<logic:equal name="manageKeyMatterForm" property="objecType"
									value="2">
									<hrms:codetoname codeid="UN" name="element"
										codevalue="string(b0110)" codeitem="codeitem" scope="page" />
									<bean:write name="codeitem" property="codename" />
									/<hrms:codetoname codeid="UM" name="element"
										codevalue="string(e0122)" codeitem="codeitem" scope="page" />
									<bean:write name="codeitem" property="codename" />
								</logic:equal>
								<logic:equal name="manageKeyMatterForm" property="objecType"
									value="1">
									<!--  
									<logic:equal name="manageKeyMatterForm" property="kind"
										value="1">
										<hrms:codetoname codeid="UM" name="element"
											codevalue="string(e0122)" codeitem="codeitem" scope="page" />
										<bean:write name="codeitem" property="codename" />
									</logic:equal>
									<logic:equal name="manageKeyMatterForm" property="kind"
										value="2">
										<hrms:codetoname codeid="UN" name="element"
											codevalue="string(b0110)" codeitem="codeitem" scope="page" />
										<bean:write name="codeitem" property="codename" />
									</logic:equal>
									<logic:equal name="manageKeyMatterForm" property="kind"
										value="0">
										<logic:equal name="element" property="string(e0122)" value="">
											<hrms:codetoname codeid="UN" name="element"
												codevalue="string(b0110)" codeitem="codeitem" scope="page" />
											<bean:write name="codeitem" property="codename" />
										</logic:equal>
										<logic:notEqual name="element" property="string(e0122)"
											value="">
											<hrms:codetoname codeid="UM" name="element"
												codevalue="string(e0122)" codeitem="codeitem" scope="page" />
											<bean:write name="codeitem" property="codename" />
										</logic:notEqual>
									</logic:equal>
									-->
										<bean:write name="element" property="string(a0101)"
										filter="true" />
								</logic:equal>
							</td>
							<logic:equal name="manageKeyMatterForm" property="objecType"
								value="2">
								<td align="left" class="RecordRow" nowrap>&nbsp;
									<bean:write name="element" property="string(a0101)"
										filter="true" />
								</td>
							</logic:equal>

							<bean:define id="event" name="element"
								property="string(key_event)" />
							<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
								tiptext="tiptext" text="${event}"></hrms:showitemmemo>
							<td align="left" class="RecordRow" ${tiptext}  nowrap>&nbsp;
								${showtext}&nbsp;
							</td>

							<td align="left" class="RecordRow" nowrap>&nbsp;
								<bean:write name="element" property="string(key_set)"
									filter="true" />
							</td>
							
							<td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="string(score)"
									filter="true" />&nbsp;
							</td>
							
							<td align="center" class="RecordRow_left" nowrap>&nbsp;															
								<logic:notEqual name="element" property="string(status)" value="01">
									生效
								</logic:notEqual>							
								<logic:equal name="element" property="string(status)" value="01">
									起草
								</logic:equal>	
							</td>
							
							<hrms:priv func_id="06070505">
								<td align="center" class="RecordRow_left" nowrap>
									<logic:equal name="element" property="string(status)" value="01">
										<a onclick="edit('<bean:write name="element" property="string(event_id)" filter="true"/>');"><img
												src="/images/edit.gif" border=0> 
										</a>
									</logic:equal>
								</td>
							</hrms:priv>
							
						</tr>
					</hrms:extenditerate>
				</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				第
				<bean:write name="manageKeyMatterForm"
					property="setlistform.pagination.current" filter="true" />
				页 共
				<bean:write name="manageKeyMatterForm"
					property="setlistform.pagination.count" filter="true" />
				条 共
				<bean:write name="manageKeyMatterForm"
					property="setlistform.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="manageKeyMatterForm"
						property="setlistform.pagination" nameId="setlistform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<table width="100%" align="center">
		<tr>
			<td align="center" style="height:20px"> 			
				<hrms:priv func_id="06070501">	
					<input type='button' class="mybutton" property="b_add"
						onclick='add()'
						value='<bean:message key="button.insert"/>' />
				</hrms:priv>			
				<hrms:priv func_id="06070502">		
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()'
					value='<bean:message key="button.delete"/>' />
				</hrms:priv>	
				<hrms:priv func_id="06070503">					  	  					 	 				 	 					  
					<input type='button' class="mybutton" property="b_compare"
						onclick='compare()'
						value='<bean:message key="kq.emp.change.compare"/>' />
				</hrms:priv>	
				<hrms:priv func_id="06070504">	
					<input type='button' class="mybutton" property="b_back"
						onclick='spBack()'
						value='<bean:message key="performance.spflag.bh"/>' />
				</hrms:priv>	
			</td>
		</tr>
	</table>
	<script>
	if(/msie/i.test(navigator.userAgent)){//该样式只在ie下生效
		document.getElementById("myFixedTr").className="fixedHeaderTr";
	} else {
		document.getElementById("keyMatterDiv").style.height = document.body.clientHeight-150;
		document.getElementById("keyMatterDiv").style.width = document.body.clientWidth-10;
	}
	
	</script>
</html:form>
