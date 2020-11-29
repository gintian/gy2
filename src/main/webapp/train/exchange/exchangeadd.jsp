<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hjsj.hrms.actionform.train.exchange.ExchangeForm,com.hjsj.hrms.valueobject.common.FieldItemView,java.util.List,com.hrms.hjsj.sys.FieldItem"%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="exchange.js"></script>

<script language="javascript">
	function fieldcode2(sourceobj)
	{
	
	　var　targetobj,target_name,hidden_name,hiddenobj;
   	  target_name=sourceobj.name;    
      hidden_name=target_name.replace(".viewvalue",".value");       	
      var hiddenInputs=document.getElementsByName(hidden_name);
      if(hiddenInputs!=null)    
    	hiddenobj=hiddenInputs[0];
     hiddenobj.value=sourceobj.value;	
	}
	
	//falg=1 保存    flag=2保存&继续
	function save(flag){
		<% int m=0; %>
		<logic:iterate  id="element1"    name="exchangeForm"  property="itemList" indexId="index"> 
			<%
				FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
			    boolean isFillable=abean1.isFillable();	
			%>		
			var aa<%=m%>=document.getElementsByName("itemList[<%=m%>].value")
			if(<%=isFillable%>)
			{
				if(aa<%=m%>[0].value=="")
				{
					alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
					return;						
				}
			}		
			<% m++; %>
		</logic:iterate>
		if(flag==2)
			exchangesavecontinue();
		else
			exchangesave();
	}
	
	function openOrgInfo(codeid,mytarget,orgparentcode,flag)
	{
		var managerstr ="";
		if(mytarget!=null)
		{
			managerstr=orgparentcode;
		}

	    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
	    if(mytarget==null)
	      return;
	    var oldInputs=document.getElementsByName(mytarget);
	    oldobj=oldInputs[0];
	    target_name=oldobj.name;
	    hidden_name=target_name.replace(".viewvalue",".value"); 
	    hidden_name=hidden_name.replace(".hzvalue",".value");
	       
	    var hiddenInputs=document.getElementsByName(hidden_name);
	      
	    if(hiddenInputs!=null)
	    {
	    	hiddenobj=hiddenInputs[0];
	    	codevalue=managerstr;
	    }
	    
	    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
	    thecodeurl="/system/untrain.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
	    var popwin= window.showModalDialog(thecodeurl, theArr, 
	        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}
</script>
<%
	ExchangeForm form = (ExchangeForm) session.getAttribute("exchangeForm");
	int len = form.getItemList().size();
%>
<html:form action="/train/exchange/exchangemanage.do?b_add=link">
	<bean:define id="id" name="exchangeForm" property="r5701" />
	<html:hidden name="exchangeForm" property="r5701" value="${id}"/>
	<html:hidden name="exchangeForm" property="a_code" />
	<table width="96%" align="center" border="0" cellpadding="0"
		cellspacing="0" style="margin-top: 8px;">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTable">
					<tr height="20">
					<logic:equal value="" name="exchangeForm" property="r5701">
						<td colspan="4" align="left" valigh="bottom" class="TableRow">
							&nbsp;&nbsp;新增奖品&nbsp;
						</td>
					</logic:equal>
					<logic:notEqual value="" name="exchangeForm" property="r5701">
						<td colspan="4" align="left" valigh="bottom" class="TableRow">
							&nbsp;&nbsp;编辑奖品&nbsp;
						</td>
					</logic:notEqual>
					</tr>
					<tr class="trDeep">
						<%
							int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="exchangeForm"
							property="itemList" indexId="index">
							<%
								FieldItemView abean = (FieldItemView) pageContext
														.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
												if (i == 2) {
							%>
							<%
								if (j % 2 == 0) {
							%>
						
					</tr>
					<tr class="trShallow">
						<%
							} else {
						%>
					</tr>
					<tr class="trDeep">
						<%
							}
												i = 0;
												j++;
											}
						%>
						<logic:notEqual name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow_left" nowrap >
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<% if(i==1){ %>
							<td align="left" class="RecordRow_right" nowrap >
							<%}else{ %>
							<td align="left" class="RecordRow_inside" nowrap >
							<%} %>
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
											<logic:equal name="element" property="decimalwidth" value="0">
											<logic:equal name="exchangeForm" property="editStatus" value="01">
												<html:text maxlength="8" size="30" styleClass="textColorWrite"
													onkeyup="value=value.replace(/[^\d]/g,'') "
													onblur='isNumber(this);' name="exchangeForm" disabled="false"
													styleId="${element.itemid}"
													property='<%="itemList["
														+ index + "].value"%>' />
											</logic:equal>
											
											<logic:notEqual name="exchangeForm" property="editStatus" value="01">
											<logic:equal name="element" property="itemid" value="r5707">
												<html:text maxlength="8" size="30" styleClass="textColorWrite"
													onkeyup="value=value.replace(/[^\d]/g,'') "
													onblur='isNumber(this);' name="exchangeForm" disabled="true"
													styleId="${element.itemid}"
													property='<%="itemList["
														+ index + "].value"%>' />
											</logic:equal>
											<logic:notEqual name="element" property="itemid" value="r5707" >
											<!--	<html:text maxlength="8" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit2(this);"
													onblur='isNumber(this);' name="exchangeForm" disabled="false"
													styleId="${element.itemid}"
													property='<%="itemList["
														+ index + "].value"%>' />-->
												<html:text maxlength="8" size="30" styleClass="textColorWrite"
													onkeyup="value=value.replace(/[^\d]/g,'') "
													onblur="return!clipboardData.getData('text').match(/[\u4e00-\u9fa5]/gi)" 
													styleId="${element.itemid}"
													property='<%="itemList["
														+ index + "].value"%>' />
											</logic:notEqual>
											</logic:notEqual>
											</logic:equal>
											<logic:notEqual name="element" property="decimalwidth"
												value="0">
												<html:text maxlength="8" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit(this);"
													onblur='isNumber(this);' name="exchangeForm"
													styleId="${element.itemid}"
													property='<%="itemList["
														+ index + "].value"%>' />
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
											<html:text maxlength="50" size="30" styleClass="textColorWrite"
												name="exchangeForm" styleId="${element.itemid}"
												property='<%="itemList[" + index
													+ "].value"%>' />
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="itemtype" value="D">
										<input type="text"
											name='<%="itemList[" + index
												+ "].value"%>'
											maxlength="50" size="29" id="${element.itemid}"
											extra="editor" class="textColorWrite"
											style="font-size: 10pt; text-align: left; width: 222px;"
											dropDown="dropDownDate" value="${element.value}"
											onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }">
									</logic:equal>
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
										<html:hidden name="exchangeForm"
											property='<%="itemList[" + index
												+ "].value"%>'
											onchange="fieldcode2(this)" />
										<html:text maxlength="50" size="30" styleClass="textColorWrite"
											name="exchangeForm" 
											property='<%="itemList[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
									</logic:equal>
									<logic:notEqual name="element" property="itemid" value="b0110">
										<html:hidden name="exchangeForm"
											property='<%="itemList[" + index
												+ "].value"%>' />
										<html:text maxlength="50" size="30" styleClass="textColorWrite"
											name="exchangeForm"
											property='<%="itemList[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
									</logic:notEqual>
								</logic:notEqual>
								<%
									i++;
								%>
								<logic:equal name="element" property="itemid" value="b0110">
									<!--  <img src="/images/code.gif"
										onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="itemList[" + index
											+ "].viewvalue"%>","${exchangeForm.orgparentcode }","1");'
										style="vertical-align: middle;" />-->
									  	<img align="absMiddle" src="/images/code.gif" onclick='javascript:openOrgInfo("${element.codesetid}","<%="itemList[" + index + "].viewvalue"%>","${exchangeForm.orgparentcode }","1");'/> 
								</logic:equal>
								<logic:equal name="element" property="itemtype" value="A">
									<logic:notEqual name="element" property="codesetid" value="0">
										<logic:notEqual name="element" property="itemid" value="b0110">
											<img src="/images/code.gif" align="absMiddle"
												onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemList["
															+ index
															+ "].viewvalue"%>");'
												style="vertical-align: middle;" />
										</logic:notEqual>
									</logic:notEqual>
								</logic:equal>
								<%
									if (isFillable1) {
								%>
								&nbsp;
								<font color='red'>*</font>&nbsp;<%
									}
								%>
							</td>
							<%
								if (index < len - 1) {
							%>
							<logic:equal name="exchangeForm"
								property='<%="itemList["
												+ Integer.toString(index
														.intValue() + 1)
												+ "].itemtype"%>'
								value="M">
								<%
									if (i < 2) {
								%>
								<td align="left" class="RecordRow_inside" nowrap ></td>
								<td align="left" class="RecordRow_right" nowrap ></td>
								<%
									i++;
																}
								%>

							</logic:equal>
							<%
								} else if (index == len - 1) {
							%>
							<%
								if (i < 2) {
							%>
							<td align="left" class="RecordRow_inside" nowrap ></td>
							<td align="left" class="RecordRow_right" nowrap ></td>
							<%
								i++;
														}
							%>
							<%
								}
							%>
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow_left" nowrap valign="top">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow_right" nowrap colspan="3" >
								<html:textarea name="exchangeForm"
									property='<%="itemList[" + index
											+ "].value"%>'
									cols="90" rows="6" styleClass="textboxMul"></html:textarea>
								<%
									if (isFillable1) {
								%>
								&nbsp;
								<font color='red'>*</font>&nbsp;<%
									}
								%>
							</td>
							<%
								i = 2;
							%>
						</logic:equal>

						</logic:iterate>

					</tr>
				</table>
				<table width='100%' align='center'>
					<tr>
						<td></td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" class="mybutton"
								value="<bean:message key='button.save'/>"
								onClick="save(1);">&nbsp;
							<logic:empty name="id">
								<input type="button" class="mybutton"
									value="<bean:message key='button.savereturn'/>"
									onClick="save(2);">&nbsp;
							</logic:empty>
							<input type="button" class="mybutton"
								value="<bean:message key='button.return'/>"
								onClick="exchange1('${exchangeForm.r5713}');">
						</td>
					</tr>
				</table>
				</html:form>