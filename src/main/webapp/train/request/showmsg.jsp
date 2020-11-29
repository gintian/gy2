<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.train.request.CourseTrainForm"%>
<%@page import="com.hrms.struts.valueobject.PaginationForm"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<%
	CourseTrainForm courseTrainForm = (CourseTrainForm)session.getAttribute("courseTrainForm");
	PaginationForm msgPageForm = courseTrainForm.getMsgPageForm();
	ArrayList l=courseTrainForm.getMsg();
	String flag = courseTrainForm.getFlag();
	if(l==null)
		l=new ArrayList();
	int i=0;
	int c=msgPageForm.getPagination().getCurrent();
	int size=courseTrainForm.getPagerows();
%>

<html>
	<head>
<style>
.fixedDiv3
{ 
	overflow:auto; 
	height:300px;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
}
-->

div{
	cursor:hand;font-size:12px;
   }
.TableRowm1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: medium none;
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;	
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:30px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm3 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm4 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm5 {
	width: 30px;
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm6 {
	width: 308px;
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	/*
	color:#336699;
	*/
	valign:middle;
}

</style>
	</head>
	<script language='javascript'>
	function reimport()
	{
		 courseTrainForm.action='/train/request/import.do?br_selectfile=link';
      	 courseTrainForm.submit();
	}
	
    function exedata()
    {
    	var hashvo=new ParameterSet();
		hashvo.setValue("classid",'${courseTrainForm.r3101}');
		hashvo.setValue("msg","6");
		hashvo.setValue("persons","${courseTrainForm.persons}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:successSave,functionId:'2020040012'},hashvo);
    }
    
    function successSave(outparamters){
		var flag=outparamters.getValue("flag");
		if(flag=="true"){
			window.returnValue="success";
	      	window.close();
		}else{
			alert(flag);
			reimport();
		}
	}
	
    function exedata1()
    {
    	window.returnValue="";
      	window.close();
    }
    
	function cancel(){
		window.close();
	}
  </script>
  <hrms:themes></hrms:themes>
<body>
		<form name="courseTrainForm" method="post" action="/train/trainexam/question/import.do" enctype="multipart/form-data">
			<div
				style="width: 488px; height: 22px; BORDER-BOTTOM: none; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid; background-color: #F4F7F7; text-align: center; padding-top: 4px;margin-top: 10px;"
				class="common_border_color common_background_color">
				<b>导入培训学员</b>
			</div>
			<div
				style="width: 488px; height: 20px; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid; BORDER-bottom: #C4D8EE 0pt solid; text-align: left; padding: 4 4 4 4;"
				class="common_border_color common_background_color">
				&nbsp;&nbsp;&nbsp;&nbsp;因如下提示信息不满足而不被导入，请点击取消或者改正后重新导入。
			</div>
			<div style="width: 488px; height: 300px;">
				<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
					
					<tr>
						<td>
							<div class="fixedDiv3 common_border_color" style="width: 488px;height: 350px;">
								
								<table width="100%" border=0 cellspacing=0 align=center
									cellpadding=0 style="margin-top: -1;">
									<thead>
										<tr class=fixedHeaderTr>
											<td align=center class="TableRow noleft" width=30px nowrap>
												<bean:message key='train.job.serial' />
											</td>
											<td align=center class="TableRow noleft" width="100px" nowrap>
												${courseTrainForm.primarykeyLabel }
											</td>
											<td align=center class="TableRow noleft noright" nowrap>
												<bean:message key='workbench.info.content.lebal' />
											</td>
										</tr>
									</thead>
									<hrms:extenditerate id="element" name="courseTrainForm" property="msg" indexes="indexes"
									pagination="msgPageForm.pagination" pageCount="999999" scope="session">
										<%
											if (i % 2 == 0) {
										%>
										<tr class="trShallow">
											<%} else {
											%>
											<tr class="trDeep">
												<%}
													i++;
												%>
												<td align="center" class="RecordRow noleft" style="width: 30px;border-top: none;" nowrap>
													<%=i + (c - 1) * size%>
												</td>
												<td align="left" class="RecordRow noleft"
													style="word-break: break-all;border-top: none;" width="100px" nowrap>
													&nbsp;
													<bean:write name="element" property="keyid" />
												</td>
												<td align="left" class="RecordRow noleft noright"
													style="word-break: break-all;border-top: none;" nowrap>
													<bean:write name="element" property="content"
														filter="false" />
												</td>
											</tr>
									</hrms:extenditerate> 
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
			<table border="0" cellspacing="0" align="center" cellpadding="0"
				style="width: 50%;">
				<tr>
					<td align="center" style="padding-top: 5px;">
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
<logic:empty name="courseTrainForm" property="msg">
		<script type="text/javascript">
			exedata();
		</script>
</logic:empty>
</html>
