<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.train.b_plan.PlanTrainForm,com.hrms.struts.valueobject.PaginationForm,org.apache.commons.beanutils.LazyDynaBean"%>

<%
	//PlanTrainForm selfInfoForm = (PlanTrainForm)session.getAttribute("topicForm");
	//PaginationForm msgPageForm = selfInfoForm.getMsgPageForm();
	int i=0;
	//int c=msgPageForm.getPagination().getCurrent();
	//int size=selfInfoForm.getPagerows();
	//int[] counts = selfInfoForm.getCounts();
 %>
<html>
	<head>
		<style>
		.RecordRowl {
			border: inset 1px #C4D8EE;
			BORDER-BOTTOM: #C4D8EE 1pt solid; 
			BORDER-LEFT: 0pt; 
			BORDER-RIGHT: #C4D8EE 1pt solid; 
			BORDER-TOP: #C4D8EE 1pt solid;
			font-size: 12px;
			border-collapse:collapse; 
			height:22px;
		}
		.RecordRowr {
			border: inset 1px #C4D8EE;
			BORDER-BOTTOM: #C4D8EE 1pt solid; 
			BORDER-LEFT: #C4D8EE 1pt solid; 
			BORDER-RIGHT: 0pt; 
			BORDER-TOP: #C4D8EE 1pt solid;
			font-size: 12px;
			border-collapse:collapse; 
			height:22px;
		}
		</style>
	</head>
	<script language='javascript'>
	function reimport()
	{
		 topicForm.action='/selfservice/infomanager/askinv/import.do?br_selectfile=link';
      	 topicForm.submit();
	}
    function exedata()
    {
    	window.returnValue="aaa";
      	 window.close();
    }
	function cancel(){
		window.close();
	}
  </script>
	<body>
	
		<form name="topicForm" method="post"
			action="/train/trainexam/question/import.do"
			enctype="multipart/form-data">
			<div
				style="width: 488px; height: 22px; BORDER-BOTTOM: none; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid; background-color: #F4F7F7; text-align: center; padding-top: 4px;">
				<b>导入问卷调查</b>
			</div>
			<div
				style="width: 488px; height: 20px; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid; BORDER-bottom: #C4D8EE 1pt solid; text-align: left; padding: 4 4 4 4;">

				&nbsp;&nbsp;&nbsp;&nbsp;<%
				%>因如下提示信息不满足而不被导入，请点击取消或者改正后重新导入。
			</div>
			<div style="width: 488px; height: 340px;">
				<table width="100%" border="0" cellspacing="0" align="left"
					cellpadding="0">
					<tr>
						<td>
						
							<div
								style="overflow: auto; width: 488px; height: 320px !important; height: expression(document . body . clientHeight-110); BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-BOTTOM: #C4D8EE 1pt solid;">
								
								<table width="100%" border=0 cellspacing=0 align=center
									cellpadding=0 class=ListTable style="margin-top: -1;">
								 	<hrms:extenditerate id="element" name="topicForm"
										property="msgPageForm.list" indexes="indexes"
										pagination="msgPageForm.pagination"
										 scope="session"> 
										
										<%
										if (i % 2 == 0) {
										%>
										<tr class="trShallow">
											<%
											} else {
											%>
										
										<tr class="trDeep">
											<%
													}
													i++;
											%>
											<td align="center" class="RecordRowl" nowrap>
											</td>
											<td align="left" class="RecordRowr"
												style="word-break: break-all;">
										 	<bean:write name="element" property="content" filter="false" /> 
											</td>
										</tr>
								 	</hrms:extenditerate> 
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<table width="100%" align="center" class="RecordRowP" style="">
								<tr>
									<td valign="bottom" class="tdFontcolor">
										<hrms:paginationtag name="topicForm"
											pagerows="${topicForm.pagerows}"
											property="msgPageForm.pagination" scope="session"
											refresh="true"></hrms:paginationtag>
									</td>
									<td align="right" nowrap class="tdFontcolor">
										<p align="right">
											<hrms:paginationlink name="topicForm"
												property="msgPageForm.pagination" nameId="msgPageForm">
											</hrms:paginationlink>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0"
				style="width: 50%;">
				<tr>
					<td align="center">
						<input type="button" name="b_update"
							value="<bean:message key='button.reimport'/>" class="mybutton"
							onClick="reimport();">
						<input type="button" name="b_update"
							value="<bean:message key='button.cancel'/>" class="mybutton"
							onClick="cancel();">
					</td>
				</tr>
			</table>
		</form>
	</body>
	<logic:empty name="topicForm" property="msg">
		<script type="text/javascript">
			exedata();
		</script>
	</logic:empty>

</html>
