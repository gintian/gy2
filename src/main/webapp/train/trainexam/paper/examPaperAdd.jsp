<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.train.trainexam.paper.ExamPaperForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hjsj.hrms.actionform.train.resource.course.CourseForm,com.hjsj.hrms.valueobject.common.FieldItemView"%>
<script language="javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<!-- script type="text/javascript" src="/js/validate.js">
</script -->
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
	function openOrgInfo(codeid,mytarget,check,flag){
	var managerstr ="";
	if(check==2){
		managerstr=document.getElementById("companyid").value;
	}else if(check==3){
		managerstr=document.getElementById("depid").value;
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
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
}
function save0()
{
	<% int m=0; %>
	<logic:iterate  id="element1"    name="examPaperForm"  property="itemlist" indexId="index"> 
	<%
		FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
	    boolean isFillable=abean1.isFillable();	
	%>		
		var aa<%=m%>=document.getElementsByName("itemlist[<%=m%>].value")
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
	save1();
}
function save1(){
	var r5300 = document.getElementById("r5300").value;
	examPaperForm.action = "/train/trainexam/paper.do?b_save=link&r5300="+r5300;
	examPaperForm.submit();
}
function returnback0(){
	examPaperForm.action = "/train/trainexam/paper.do?b_query=back";
	examPaperForm.submit();
}
function isenable(){
	var r5315 = document.getElementById("r5315").value;
	if("1"==r5315)
		document.getElementById("imgr5317").style.display="none";
	else
		document.getElementById("imgr5317").style.display="";
}
document.body.onload=isenable;
</script>
<%
	ExamPaperForm form = (ExamPaperForm) session.getAttribute("examPaperForm");
	int len = form.getItemlist().size();
%>
<html:form action="/train/trainexam/paper">
	<html:hidden name="examPaperForm" property="r5300" styleId="r5300"/>
	<table width="96%" align="center" border="0" cellpadding="0"
		cellspacing="0" style="margin-top: 10px;">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTable">
					<tr height="20">
						<td colspan="4" align="left" valigh="bottom" class="TableRow">
							&nbsp;&nbsp;
							<logic:equal name="examPaperForm" property="r5300" value="">
							新增试卷
							</logic:equal>
							<logic:notEqual name="examPaperForm" property="r5300" value="">
                编辑试卷
              </logic:notEqual>
							&nbsp;
						</td>
					</tr>
					<tr class="trDeep">
						<%
							int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="examPaperForm" property="itemlist"
							indexId="index">
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
							<td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
											<logic:equal value="r5315" name="element" property="itemid">
												<html:select name="examPaperForm" property='<%="itemlist[" + index
														+ "].value"%>' styleId="${element.itemid}"  style="width:222px;" onchange="isenable()">
													<html:option value="1"><bean:message key="train.examplan.paperstyle.all"/></html:option>
													<html:option value="2"><bean:message key="train.examplan.paperstyle.single"/></html:option>
												</html:select>
										    </logic:equal>
										    <logic:notEqual value="r5315" name="element" property="itemid">
												<logic:equal name="element" property="decimalwidth" value="0">
													<html:text maxlength="50" size="30" styleClass="textColorWrite"
														onkeypress="event.returnValue=IsDigit2(this);"
														onblur='isNumber(this);' name="examPaperForm"
														styleId="${element.itemid}"
														property='<%="itemlist["
															+ index + "].value"%>' />
												</logic:equal>
												<logic:notEqual name="element" property="decimalwidth"
													value="0">
													<html:text maxlength="50" size="30" styleClass="textColorWrite"
														onkeypress="event.returnValue=IsDigit(this);"
														onblur='isNumber(this);' name="examPaperForm"
														styleId="${element.itemid}"
														property='<%="itemlist["
															+ index + "].value"%>' />
												</logic:notEqual>
												<logic:equal value="r5305" name="element" property="itemid">
												<bean:message key="train.trainexam.question.questiones.component"/>
												</logic:equal>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
										    <logic:equal value="r5307" name="element" property="itemid">
												<html:select name="examPaperForm" property='<%="itemlist[" + index
														+ "].value"%>' styleId="${element.itemid}" style="width:222px;">
													<html:option value="1">考试</html:option>
													<html:option value="2">作业</html:option>
												</html:select>
										    </logic:equal>
										    <logic:equal value="r5308" name="element" property="itemid">
												<html:select name="examPaperForm" property='<%="itemlist[" + index
														+ "].value"%>' styleId="${element.itemid}"  style="width:222px;">
													<html:option value="1">手工组卷</html:option>
													<html:option value="2">自由组卷</html:option>
												</html:select>
										    </logic:equal>
										    <logic:notEqual value="r5307" name="element" property="itemid">
										    <logic:notEqual value="r5308" name="element" property="itemid">
												<html:text maxlength="50" size="30" styleClass="textColorWrite"
													name="examPaperForm" styleId="${element.itemid}"
													property='<%="itemlist[" + index
														+ "].value"%>' />
											</logic:notEqual>
											</logic:notEqual>
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
									<logic:equal name="element" property="itemid" value="b0110">
										<html:hidden name="examPaperForm"
											property='<%="itemlist[" + index
												+ "].value"%>'
											onchange="fieldcode2(this)" />
										<html:text maxlength="50" size="30" styleClass="textColorRead"
											name="examPaperForm"
											property='<%="itemlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" style="border:1px solid;" readonly="true" />
									</logic:equal>
									<logic:notEqual name="element" property="itemid" value="b0110">
										<html:hidden name="examPaperForm"
											property='<%="itemlist[" + index
												+ "].value"%>'/>
										<html:text maxlength="50" size="30" styleClass="textColorRead"
											name="examPaperForm"
											property='<%="itemlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" style="border:1px solid;" readonly="true"/>
									</logic:notEqual>
								</logic:notEqual>
								<%
									i++;
								%>
								<logic:equal name="element" property="itemid" value="b0110">
									<img src="/images/code.gif" id='imgb0110'
											onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>","${examPaperForm.orgparentcode }","1");'  style="vertical-align: middle;"/>
								</logic:equal>
								<logic:equal name="element" property="itemtype" value="A">
								<logic:notEqual name="element" property="codesetid" value="0">
								<logic:notEqual name="element" property="itemid" value="b0110">
								<logic:notEqual name="element" property="itemid" value="r5311">
									<img src="/images/code.gif" id='img<bean:write name="element" property="itemid"/>'
											onclick='javascript:openKhTargetCardInputCode("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:notEqual>
								</logic:notEqual>
								</logic:notEqual>
								</logic:equal>
								<!-- 
								<logic:equal name="element" property="itemid" value="r5311">
									<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:equal>  -->
								<%
												if (isFillable1) {
											%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
							</td>
							<%
								if (index < len - 1) {
							%>
							<logic:equal name="examPaperForm"
								property='<%="itemlist["
												+ Integer.toString(index
														.intValue() + 1)
												+ "].itemtype"%>'
								value="M">
								<%
									if (i < 2) {
								%>
								<td align="left" class="RecordRow" nowrap></td>
								<td align="left" class="RecordRow" nowrap></td>
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
							<td align="left" class="RecordRow" nowrap></td>
							<td align="left" class="RecordRow" nowrap></td>
							<%
								i++;
														}
							%>
							<%
								}
							%>
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow" nowrap valign="top">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap colspan="3">
								<html:textarea name="examPaperForm"
									property='<%="itemlist[" + index
											+ "].value"%>'
									cols="90" rows="6" styleClass="textboxMul"></html:textarea>
								<%
									if (isFillable1) {
								%>
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
				<table width='100%' cellpadding="0" cellspacing="0" align='center'>
					<tr>
						<td align="left" style="padding-top: 5px;">
						<logic:notEqual value="04" name="examPaperForm" property="r5311">
							<input type='button' value='<bean:message key='button.save' />'
								class="mybutton" onclick="save0();">
						</logic:notEqual>
							<input type="button" class="mybutton"
								value="<bean:message key='button.return'/>"
								onClick="returnback0();">
						</td>
					</tr>
				</table>
				</html:form>
