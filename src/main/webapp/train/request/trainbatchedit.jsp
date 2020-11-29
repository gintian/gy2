<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language="javascript" src="/train/resource/course/courseTrain.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="javascript">
	
function save0()
{
	<% int m=0; %>
	var itemids="";
	var values="";
	var sels = document.forms(0).sel;
	<logic:iterate  id="element1"    name="courseTrainForm"  property="itemlist" indexId="index"> 
	<%
		FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
	    boolean isFillable=abean1.isFillable();	
	%>		
		var vv<%=m%>=document.getElementsByName("itemlist[<%=m%>].itemid");
		var aa<%=m%>=document.getElementsByName("itemlist[<%=m%>].value");
		if(<%=isFillable%>)
		{
			if(aa<%=m%>[0].value=="")
			{
				alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
				return;						
			}
		}		
		
		if(sels[<%=m%>].checked){
			itemids+=vv<%=m%>[0].value+",";
			values+=aa<%=m%>[0].value+",";
		}
		<% m++; %>
	</logic:iterate>
	
	//alert("itemids:"+itemids+"--values:"+values);
	if(!itemids&&!values){
		alert("请选择修改项!");
		return;
	}
	var thevo=new Object();
	thevo.itemids=itemids;
	thevo.values=values;
	thevo.flag="true";
	window.returnValue=thevo;
	window.close();
}

function selectCheckbox(obj,type){
	var sels = document.forms(0).sel;
	for(var i=0;i<sels.length;i++){
		if(!sels[i].disabled){
			if(type==0)
				sels[i].checked=true;
			else
				sels[i].checked=false;
		}
	}
}
</script>
<html:form action="/train/request/trainRes">
	<table width="96%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td>
				<fieldset>
					<legend>批量修改</legend>&nbsp;
					<div style="height: 350px; width: 500px;border-collapse: collapse;overflow-y:auto;padding: 0px;margin-top: 0px;">
					<table class="ListTable1 noleft" border="0" cellpadding="0" cellspacing="0" style="width: 100%;border-right: 0px;">
						<thead>
							<tr>
								<td class="TableRow noleft">&nbsp;</td>
								<td class="TableRow" align="center">&nbsp;指标名称&nbsp;</td>
								<td class="TableRow" align="center">&nbsp;修改值&nbsp;</td>
								<td class="TableRow" align="center" style="border-right: 0px;">&nbsp;修改&nbsp;</td>
							</tr>
						</thead>
						<%
							int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="courseTrainForm" property="itemlist"
							indexId="index">
							<%
							FieldItemView abean = (FieldItemView) pageContext
														.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
										
							%>
							<tr>
								<td class="RecordRow noleft">
									<html:hidden name="courseTrainForm" property='<%="itemlist[" + index + "].itemid"%>' />
									&nbsp;<%=i+1 %>&nbsp;
								</td>
								<td class="RecordRow">
									&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
								</td>
								<td class="RecordRow" align="left" style="padding: 3px;">
									<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
											<logic:equal name="element" property="decimalwidth" value="0">
												<html:text maxlength="50" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit2(this);"
													onblur='isNumber(this);' name="courseTrainForm"
													styleId="${element.itemid}"
													property='<%="itemlist["
														+ index + "].value"%>' />
											</logic:equal>
											<logic:notEqual name="element" property="decimalwidth"
												value="0">
												<html:text maxlength="50" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit(this);"
													onblur='isNumber(this);' name="courseTrainForm"
													styleId="${element.itemid}"
													property='<%="itemlist["
														+ index + "].value"%>' />
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
											<html:text maxlength="50" size="30" styleClass="textColorWrite"
												name="courseTrainForm" styleId="${element.itemid}"
												property='<%="itemlist[" + index
													+ "].value"%>' />
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="itemtype" value="D">
										<input type="text"
											name='<%="itemlist[" + index
												+ "].value"%>'
											maxlength="50" size="29" id="${element.itemid}"
											extra="editor" class="textColorWrite"
											style="font-size: 10pt; text-align: left"
											dropDown="dropDownDate" value="${element.value}"
											onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }">
									</logic:equal>
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<html:hidden name="courseTrainForm"
										property='<%="itemlist[" + index
											+ "].value"%>' />
									<html:text maxlength="50" size="30" styleClass="textColorRead"
										name="courseTrainForm"
										property='<%="itemlist[" + index
											+ "].viewvalue"%>'
										onchange="fieldcode(this,2)" readonly="true"/>
									<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:notEqual>
								</td>
								<td class="RecordRow" align="center" style="border-right: 0px;">
									<%if(isFillable1){ %>
										&nbsp;<input type="checkbox" name="sel" disabled="disabled" checked="checked" value="<%=i %>"/>&nbsp;
									<%}else{ %>
										&nbsp;<input type="checkbox" name="sel" value="<%=i %>"/>&nbsp;
									<%} %>
								</td>
							</tr>
							<%
								i++;
							%>
						</logic:iterate>
					</table>
					</div>
				</fieldset>
			</td>
			<td align="center" valign="top" style="padding-left: 10px;padding-top: 10px;padding-bottom: 25px;">
				<input type="button" value="全选" class="mybutton" onclick="selectCheckbox(this,0);"/><br/><br/>
				<input type="button" value="全撤" class="mybutton" onclick="selectCheckbox(this,1);"/><br/><br/>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2" style="padding-top: 5px;">
				<input type="button" value="确定" class="mybutton" onclick="save0();"/>
				<input type="button" value="关闭" class="mybutton" onclick="window.close();"/>
			</td>
		</tr>
	</table>
</html:form>
