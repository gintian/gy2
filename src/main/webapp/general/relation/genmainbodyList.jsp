<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.general.relation.GenRelationForm"%>
<script language="JavaScript" src="/general/relation/gen_relation.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<style>
.myfixedDiv
{ 
	overflow:auto; 
/*	height:expression(document.body.clientHeight-100);  */
/*	width:expression(document.body.clientWidth-10);     */
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
</style>
<%
GenRelationForm genRelationForm = (GenRelationForm)session.getAttribute("genRelationForm");
String select_copy="";
if(genRelationForm!=null){

	 select_copy = genRelationForm.getSelect_copy();
	 
}
%>
<body onload="check();" onbeforeunload="myClose('${param.flag}','${param.delFlag}');">
	<html:form action="/general/relation/relationmainbodylist">
		<input type="hidden" id="bodyType" value="${param.code}">
		<html:hidden name="genRelationForm" property="paramStr"/>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<tr>
				<td class="RecordRow" align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="left" nowrap>
								审批对象
								<html:select name="genRelationForm" property="khObject"
									styleId="khObject" size="1" onchange="searchKhMainBody();">
									<option value="all"><bean:message key="edit_report.All"/></option>
									<html:optionsCollection property="khObjectList"
										value="dataValue" label="dataName" />
								</html:select>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td nowrap class="RecordRow">
					<div class="myfixedDiv">
						<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTableF" style="margin-top:5px;">
							<%
							int i = 0;
							%>

							<tr>
								<td align="center" class="TableRow" nowrap>
									<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'mainbodys');">
								</td>
								<td align="center" class="TableRow" nowrap>
									审批对象
								</td>
								<logic:equal name ="genRelationForm" property="actor_type" value="1"> 
								<td align="center" class="TableRow" nowrap>
									<bean:message key="workbench.org.orgname" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="tree.umroot.umdesc" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="label.codeitemid.kk" />
								</td>
								</logic:equal>
									<logic:equal name ="genRelationForm" property="actor_type" value="4"> 
								<td align="center" class="TableRow" nowrap>
									用户组
								</td>
								</logic:equal>
								<td align="center" class="TableRow" nowrap>
									审批主体
								</td>
								    <logic:equal name ="genRelationForm" property="actor_type" value="4"> 
                                <td align="center" class="TableRow" nowrap>
                                                                                               主体姓名
                                </td>
                                </logic:equal>
								<td align="center" class="TableRow" nowrap>
									审批层级
								</td>
							</tr>

							<logic:iterate id="element" name="genRelationForm"
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
											value='<bean:write name="element" property="mainbody_id" filter="true"/>:<bean:write name="element" property="objectID" filter="true"/>:<bean:write name="element" property="relation_id" filter="true"/>' />
									</td>
									<td align="center" class="RecordRow" nowrap>
										<bean:write name="element" property="objectName" filter="true" />
									</td>
									<td align="center" class="RecordRow" nowrap>
										<bean:write name="element" property="b0110" filter="true" />
									</td>
									<logic:equal name ="genRelationForm" property="actor_type" value="1"> 
									<td align="center" class="RecordRow" nowrap>
										<bean:write name="element" property="e0122" filter="true" />
									</td>
									<td align="center" class="RecordRow" nowrap>
										<bean:write name="element" property="e01a1" filter="true" />
									</td>
									</logic:equal>
									<td align="center" class="RecordRow" nowrap>
										<bean:write name="element" property="a0101" filter="true" />
									</td>
									<logic:equal name ="genRelationForm" property="actor_type" value="4"> 
                                    <td align="center" class="RecordRow" nowrap>
                                        <bean:write name="element" property="username" filter="true" />
                                    </td>
                                    </logic:equal>
									<td align="center" class="RecordRow" nowrap>
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
		<table width="100%" style="margin-top:3px;">
			<tr>
				<td align="center">
				<hrms:priv func_id="9A510701">
				<logic:equal name ="genRelationForm" property="actor_type" value="1"> 
					<input type='button' class="mybutton" name="b_condiSelEmp"
						onclick="condiSelPeop('${genRelationForm.dbpre}')"
						value='<bean:message key="performance.implement.condiSelEmp"/>' />
			   </logic:equal>
				</hrms:priv>
				<hrms:priv func_id="9A510702">
				<logic:equal name ="genRelationForm" property="actor_type" value="1"> 		
					<logic:equal name ="genRelationForm" property="approvalRelation" value="1"> 
						<input type='button' class="mybutton" name="b_handSelEmp"
						onclick="handSelPeopNew('1','2','${genRelationForm.dbpre}','1')"
						value='<bean:message key="performance.implement.handSelEmp"/>' />
					</logic:equal>
					<logic:notEqual name ="genRelationForm" property="approvalRelation" value="1"> 
						<input type='button' class="mybutton" name="b_handSelEmp"
						onclick="handSelPeop('1','2','${genRelationForm.dbpre}')"
						value='<bean:message key="performance.implement.handSelEmp"/>' />
					</logic:notEqual>
				</logic:equal>
				<logic:equal name ="genRelationForm" property="actor_type" value="4"> 		
					<input type='button' class="mybutton" name="b_handSelEmp"
						onclick="handSelPeop('4','2','${genRelationForm.dbpre}')"
						value='<bean:message key="performance.implement.handSelEmp"/>' />
				</logic:equal>
				</hrms:priv>
				<hrms:priv func_id="9A510703">		
					<input type='button' class="mybutton" onclick='delMainBody()'
						value='<bean:message key="button.delete"/>' />
				</hrms:priv>		
					<input type='button' class="mybutton" name="b_cancel"
						onclick='myCancel("${param.flag}","${param.delFlag}");'
						value='<bean:message key="button.leave"/>' />

				</td>
			</tr>
		</table>
	</html:form>
</body>
<script type="text/javascript">
    function check(){
        var select_copy ='<%=select_copy%>';
        if(select_copy!=null&&select_copy!="")
        alert(select_copy);
    }
	 
	// IE8不支持expression控制样式 modify by 刘蒙
	var divs = document.getElementsByTagName("DIV");
	for (var i = 0; i < divs.length; i++) {
		if (divs[i].className === "myfixedDiv") {
			divs[i].style.height = document.body.clientHeight - 100;
			divs[i].style.width = document.body.clientWidth - 20;
		}
	}
   
</script>
