<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.train.request.CourseTrainForm,com.hrms.struts.valueobject.PaginationForm,org.apache.commons.beanutils.LazyDynaBean"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	CourseTrainForm courseTrainForm = (CourseTrainForm)session.getAttribute("courseTrainForm");
	PaginationForm msgPageForm = courseTrainForm.getMsgPageForm();
	ArrayList l=courseTrainForm.getMsglist();
	String flag = courseTrainForm.getFlag();
	if(l==null)
		l=new ArrayList();
	int i=0;
	int c=msgPageForm.getPagination().getCurrent();
	int size=courseTrainForm.getPagerows();
%>
<style type="text/css">
<!--
.fixedDiv7 
{ 
	overflow:auto; 
	height:350px!important;
	height:expression(document.body.clientHeight-110);
	width:598px; 
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
}
-->

div{
	cursor:hand;font-size:12px;
   }

</style>
<script language="javascript">
function tabler31(){
	var height=window.top.document.body.clientHeight;
	window.parent.document.getElementById("panel-1011-body").style.backgroundColor="#FFFFFF";;
	parent.frames['ril_body2'].location="/templates/welcome/welcome.html";
	window.parent.document.getElementById("panel-1011-body").style.height=height-68;
	window.parent.document.getElementById("panel-1011").style.height=height-68;
	window.parent.document.getElementById("panel-1012-splitter").style.display='none';
	window.parent.document.getElementById("panel-1012").style.display='none';
}
tabler31();
function importdata(){
	courseTrainForm.action="/train/request/trainsData.do?b_exedata=link&way=importdate";
	courseTrainForm.submit();
	}


function returnback(){
	courseTrainForm.action="/train/request/trainsData.do?br_selectfile=link";
	courseTrainForm.submit();
}

function backclass(){
	courseTrainForm.action="/train/request/trainsData.do?b_query=link&model=1&a_code=${courseTrainForm.a_code}";
	courseTrainForm.submit();
}
</script>
<html:form action="/train/request/trainsData" method="post">
	<br>
	<table border="0" cellspacing="0" align="center" cellpadding="0">
		<tr>
			<td>
				<logic:equal name="courseTrainForm" property="flag" value="improt">
				<table width="598" border="0" cellpadding="0" cellspacing="0">
					<tr height="20">

						<td align="left" class="TableRow">
							&nbsp;
							<bean:message key='train.info.import.lebal' />
						</td>
					</tr>
				</table>
				</logic:equal>
				<logic:notEqual name="courseTrainForm" property="flag" value="improt">
				<table width="598" border="0" cellpadding="0" cellspacing="0">
					<tr height="20">

						<td align="left" class="TableRow" style="border-bottom: none;">
							&nbsp;
							<bean:message key='workbench.info.msg.lebal' />
						</td>
					</tr>
				</table>
				</logic:notEqual>
			</td>
		</tr>
		<tr>
			<td>
			<logic:equal name="courseTrainForm" property="flag" value="improt">
			<div class="fixedDiv7 common_border_color" style="border-top:none;">
						<table style='width: 100%;' border=0 cellspacing=0 align=center
							cellpadding=0 class=ListTable style="margin-top:0;">
							<tr class="trShallow">
								<td align=center nowrap>
									<logic:equal name="courseTrainForm" property="number" value="0">
										<bean:message key="train.info.import.no.lebal"/>
									</logic:equal>
									<logic:notEqual name="courseTrainForm" property="number" value="0">
										<bean:message key="train.info.import.have"/>
										${courseTrainForm.number}
										<bean:message key="train.info.import.have.lebal"/>
									</logic:notEqual>
								</td>
							</tr>
						</table>
					</div>
					<table width="100%" border="0" cellspacing="0" align="left"
					cellpadding="0">
					</br>
					<tr>
						<td align="center" nowrap>
							<html:button property="b_abolish" styleClass="mybutton" onclick="backclass();">&nbsp;<bean:message key='button.ok' />&nbsp;</html:button>
						</td>
					</tr>
				</table>
			</logic:equal>
			<logic:notEqual name="courseTrainForm" property="flag" value="improt">
				<div class="fixedDiv7 common_border_color">
					<table style='width: 100%;' style='position:absolute' border=0
						cellspacing=0 align=center cellpadding=0 style=margin-top:0>
						<thead>
							<tr class=fixedHeaderTr>
								<td align=center class="TableRow noleft" style="border-top: none;" width=35 nowrap>
									<bean:message key='train.job.serial' />
								</td>
								<td align=center class="TableRow noleft" style="border-top: none;" width="150" nowrap>
									${courseTrainForm.primarykeyLabel }&nbsp;
								</td>
								<td align=center class="TableRow noleft noright" style="border-top: none;" width="410" >
									<bean:message key='workbench.info.content.lebal' />
								</td>
							</tr>
						</thead>
						<hrms:extenditerate id="element" name="courseTrainForm"
							property="msgPageForm.list" indexes="indexes"
							pagination="msgPageForm.pagination"
							pageCount="999999" scope="session">
							<%
					          if(i%2==0)
					          {
					          %>
							<tr class="trShallow">
								<%}
						          else
						          {%>
								<tr class="trDeep">
									<%}
							       i++;          
							    %>
									<td align="center" class="RecordRow noleft" style="border-top: none;" nowrap>
										<%=i+(c-1)*size%>
									</td>
									<td align="left" class="RecordRow noleft"
										style="word-break: break-all; border-top: none;" width="150" nowrap>
										&nbsp;
										<bean:write name="element" property="keyid" />
									</td>
									<td align="left" class="RecordRow noleft noright" style="word-break: break-all; border-top: none;" width="410">
										<bean:write name="element" property="content" filter="false" />
									</td>
								</tr>
						</hrms:extenditerate>
					</table>
				</div>
				
				<table width="100%" border="0" cellspacing="0" align="left"
					cellpadding="0">
					<tr>
						<td align="center" nowrap style="padding-top: 5px;">
							<html:button property="b_abolish" styleClass="mybutton" onclick="importdata();">&nbsp;<bean:message key='button.import' />&nbsp;</html:button>
							<html:button property="b_abolish" styleClass="mybutton" onclick="returnback();">&nbsp;<bean:message key='button.return' />&nbsp;</html:button>
						</td>
					</tr>
				</table>
				</logic:notEqual>
			</td>
		</tr>
	</table>
</html:form>
<%
	if((!flag.equalsIgnoreCase("improt"))&& l.size()==0){
%>
	<script type="text/javascript">
<!--
	importdata();
	//-->
</script>
<%
	}else{
%>
	<script type="text/javascript">
<!--

	//-->
</script>
<%
}		
%>

