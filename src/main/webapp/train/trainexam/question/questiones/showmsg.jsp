<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page
	import="com.hjsj.hrms.actionform.train.b_plan.PlanTrainForm,com.hrms.struts.valueobject.PaginationForm,org.apache.commons.beanutils.LazyDynaBean"%>

<%
			PlanTrainForm selfInfoForm = (PlanTrainForm) session
			.getAttribute("planTrainForm");
			PaginationForm msgPageForm = selfInfoForm.getMsgPageForm();
			int i = 0;
			int c = msgPageForm.getPagination().getCurrent();
			int size = selfInfoForm.getPagerows();
			int[] counts = selfInfoForm.getCounts();
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
	border-collapse: collapse;
	height: 22px;
}

.RecordRowr {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 1pt solid;
	BORDER-RIGHT: 0pt;
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse: collapse;
	height: 22px;
}
</style>
	</head>
	<script language='javascript'>
	function reimport()
	{
		 planTrainForm.action='/train/trainexam/question/import.do?br_selectfile=link';
      	 planTrainForm.submit();
	}
    function exedata()
    {
    	window.returnValue="bbb";
      	window.close();
    }
    function exedata1()
    {
    	window.returnValue="";
      	window.close();
    }

  </script>
<body>
		<form name="planTrainForm" method="post"
			action="/train/trainexam/question/import.do"
			enctype="multipart/form-data">
			<div
				style="width: 488px; height: 22px; BORDER-BOTTOM: none; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid; background-color: #F4F7F7; text-align: center; padding-top: 4px;"
				class="common_border_color common_background_color">
				<b>导入考试试题</b>
			</div>
			<div
				style="width: 488px; height: 20px; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid; BORDER-bottom: #C4D8EE 1pt solid; text-align: left; padding: 4 4 4 4;"
				class="common_border_color">
				<%
					StringBuffer msg = new StringBuffer();
					if (counts[1] > 0)
						msg.append("、必填项为空(" + counts[1] + "条记录)");
					if (counts[2] > 0)
						msg.append("、(" + counts[2] + "条记录)");
					if (counts[3] > 0)
						msg.append("、(" + counts[3] + "条记录)");
					if (counts[4] > 0)
						msg.append("、(" + counts[4] + "条记录)");
				%>
				&nbsp;&nbsp;&nbsp;&nbsp;预计将有<%=counts[0]%>条记录被成功导入。<%
				if (msg.length() > 0) {
				%>因<%=msg.substring(1)%>而不能被导入,以及或<%
				}
				%>因如下提示信息不满足而部分指标值被导入默认值,必填项为空的默认此行不进行导入。
			</div>
			<div style="width: 488px; height: 340px;">
				<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
					<tr>
						<td>
							<div
								style="overflow: auto; width: 488px; height: 320px !important; height: expression(document . body . clientHeight-110); BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-BOTTOM: #C4D8EE 1pt solid;"
								class="common_border_color">
								<table width="100%" border=0 cellspacing=0 align=center
									cellpadding=0 class=ListTable style="margin-top: -1;">
									<hrms:extenditerate id="element" name="planTrainForm"
										property="msgPageForm.list" indexes="indexes"
										pagination="msgPageForm.pagination"
										pageCount="${planTrainForm.pagerows}" scope="session">
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
											<td align="center" class="RecordRow noleft" nowrap>
												<%=i + (c - 1) * size%>
												<!--<bean:write name="element" property="key2num" />-->
											</td>
											<td align="left" class="RecordRow noright"
												style="word-break: break-all;">
												<bean:write name="element" property="content" filter="false" />
											</td><!--
											<td align="left" class="RecordRow"
												style="word-break: break-all;">
												&nbsp;
											 	<bean:write name="element" property="keyid" /> 
											</td>-->
											
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
										<hrms:paginationtag name="planTrainForm"
											pagerows="${planTrainForm.pagerows}"
											property="msgPageForm.pagination" scope="session"
											refresh="true"></hrms:paginationtag>
									</td>
									<td align="right" nowrap class="tdFontcolor">
										<p align="right">
											<hrms:paginationlink name="planTrainForm"
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
							onClick="window.close()";">
					</td>
				</tr>
			</table>
		</form>

	</body>
	<logic:empty name="planTrainForm" property="msglist">
		<script type="text/javascript">
<!--
exedata();
//-->
</script>

	</logic:empty>
</html>
