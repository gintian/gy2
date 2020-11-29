<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,				 
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<head> 
<script language="JavaScript" src="../../../ext/adapter/prototype/prototype.js"></script>	 
<script language="JavaScript"src="../../../js/showModalDialog.js"></script>
<script language="JavaScript" src="../implement.js"></script>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../components/personPicker/PersonPicker.js"></script>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	/* IE8不支持expression用法 modify by 刘蒙 */
/* 	height:expression(document.body.clientHeight-100); */
/* 	width:expression(document.body.clientWidth-10);  */
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
.RecoRowConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}


</style>
</head>
<body style="overflow: hidden">
	<html:form action="/performance/implement/kh_mainbody/mainBodyList">
		<input type="hidden" id="bodyType" value="${param.code}">
		<html:hidden name="implementForm" property="str_sql" />
		<html:hidden name="implementForm" property="paramStr" />
		<html:hidden name="implementForm" property="planid" />
		<html:hidden name="implementForm" property="busitype" />
		<html:hidden name="implementForm" property="object_type"/>
		
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<tr>
				<td class="RecordRow" align="left"> 
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="left" nowrap>
								<bean:message key="lable.appraisemutual.examineobject" />:
								<html:select name="implementForm" property="khObject"
									styleId="khObject" size="1" onchange="searchKhMainBody();"><!-- style="width:80px" -->
									<html:option value="all">
										<bean:message key="edit_report.All" />
									</html:option>
									<html:optionsCollection property="khObjectList"
										value="dataValue" label="dataName" />
								</html:select>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td nowrap class="RecoRowConition common_border_color">
					<div class="myfixedDiv">
						<table style="margin:5px 0 0 5px;" width="99%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable">
							<%
							int i = 0;
							%>

						<tr class="fixedHeaderTr1">
								<td align="center" class="TableRow" nowrap >
									<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'mainbodys');">
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="jx.datacol.khobj" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="b0110.label" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<%
	         							FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         						%>	         
			 						<%=fielditem.getItemdesc()%>
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="e01a1.label" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="lable.performance.perMainBody" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="reporttypelist.sort" />
								</td>
							</tr>

							<logic:iterate id="element" name="implementForm"
								property="mainbodys">
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
									<td align="center" class="RecordRow" nowrap>
										<input type='checkbox' name='mainbodys<%=i%>'
											value='<bean:write name="element" property="mainbody_id" filter="true"/>:<bean:write name="element" property="objectID" filter="true"/>:<bean:write name="element" property="bodyid" filter="true"/>' />
									</td>
									<td  class="RecordRow" nowrap>
										<bean:write name="element" property="objectName" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										<bean:write name="element" property="b0110" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										<bean:write name="element" property="e0122" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										<bean:write name="element" property="e01a1" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										<bean:write name="element" property="a0101" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										<bean:write name="element" property="bodyTypeName"
											filter="true" />
									</td>
								</tr>
							</logic:iterate>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<table width="100%" >
			<tr>
				<td align="center" style="height:35px">  
				
				<logic:equal name="implementForm" property="busitype" value="0">	
					<hrms:priv func_id="32603011602,0605">
						<input type='button' class="mybutton" name="b_condiSelEmp"
							onclick='condiSelPeop()'
							value='<bean:message key="performance.implement.condiSelEmp"/>' />
					</hrms:priv>
					<hrms:priv func_id="32603011601,0605">
						<input type='button' class="mybutton" name="b_handSelEmp"
							onclick='handSelPeop("${implementForm.object_type}")'
							value='<bean:message key="performance.implement.handSelEmp"/>' />
					</hrms:priv>
					<hrms:priv func_id="32603011603,0605">
						<input type='button' class="mybutton" onclick='delMainBody()'
							value='<bean:message key="button.delete"/>' />
					</hrms:priv>
				</logic:equal>
				
				<!-- // 能力素质 模块的功能授权  -->
				<logic:equal name="implementForm" property="busitype" value="1">	
					<hrms:priv func_id="360302152">
						<input type='button' class="mybutton" name="b_condiSelEmp"
							onclick='condiSelPeop()'
							value='<bean:message key="performance.implement.condiSelEmp"/>' />
					</hrms:priv>
					<hrms:priv func_id="360302151">
						<input type='button' class="mybutton" name="b_handSelEmp"
							onclick='handSelPeop("${implementForm.object_type}")'
							value='<bean:message key="performance.implement.handSelEmp"/>' />
					</hrms:priv>
					<hrms:priv func_id="360302153">
						<input type='button' class="mybutton" onclick='delMainBody()'
							value='<bean:message key="button.delete"/>' />
					</hrms:priv>
				</logic:equal>
				
					<input type='button' class="mybutton" name="b_cancel"
						onclick="myClose('${param.flag}','${param.delFlag}')"
						value='<bean:message key="button.close"/>' />				

				</td>
			</tr>
		</table>
	</html:form>
</body>
<script>
	// IE8不支持expression控制样式 modify by 刘蒙
	var divs = document.getElementsByTagName("DIV");
	for (var i = 0; i < divs.length; i++) {
		if (divs[i].className === "myfixedDiv") {
			divs[i].style.height = parent.document.body.clientHeight - 100;
			divs[i].style.width = document.body.clientWidth - 10;
		}
	}
</script>
