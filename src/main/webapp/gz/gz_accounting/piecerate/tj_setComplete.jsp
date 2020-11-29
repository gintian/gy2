<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script type="text/javascript">
<!--
var value="";

function this_isClose(){
	var needClose ="${pieceRateTjDefineForm.needClose}";
	var reportId ="${pieceRateTjDefineForm.reportId}";
	var reportName ="${pieceRateTjDefineForm.reportName}";
	var reportSortId ="${pieceRateTjDefineForm.reportSortId}";
	var obj=new Object();
	if(needClose=="true")
	{
		obj.defid=reportId;
		obj.defname=reportName; 
		obj.sortid=reportSortId; 
	    returnValue=obj;
	   	window.close();
	}

}


function ok()
{	
   if (pieceRateTjDefineForm.reportName.value==""){
     alert("没有输入统计表名！")
	 return;
   }
		

	pieceRateTjDefineForm.action="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setComplete=link&bClose=true";
	pieceRateTjDefineForm.submit();

}


//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">

	<fieldset style="width: 100%; height: 95%" align="center">
		<legend>
			定义计件统计表
		</legend>
		<br>
		<br>

		<fieldset style="width: 90%; height: 95%" align="center">

			<div id="scroll_box" style="width: 90%">
			<table width="100%" border="0">
			<tr >
				<td align="left"  nowrap style="height:35">
					&nbsp;&nbsp;
					作业类别<hrms:optioncollection name="pieceRateTjDefineForm" property="tasktypelist" collection="list"/>
					<html:select name="pieceRateTjDefineForm" property="reportKind" onchange="" style="width:140">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
				</td>
			</tr>
			<tr >
				<td align="left"  nowrap style="height:35">
					&nbsp;&nbsp;
					统计表名<html:text name="pieceRateTjDefineForm"  property="reportName" style="width:250" styleClass="inputtext"/>	
				</td>
			</tr>			
			</table>
			
			
		</div>
		</fieldset>
        <div id="scroll_box" style="width: 100%">
		    <table width="100%"  border="0" align="center">
				<tr height="35px">
					<td colspan="4" align="center" style="padding-top:2px;padding-bottom:2px;">
						<hrms:submit styleClass="mybutton" property="br_resetOrderFld">
						  <bean:message key="button.query.pre"/>
						</hrms:submit>  					         
						<input type="button" name="query" class="mybutton" value="<bean:message key="button.muster.finished"/>" onclick="ok();">
		
					</td>
				</tr>
    		</table>
 	     </div>
	</fieldset>

</html:form>

<script type="text/javascript">
<!--
 this_isClose();
//-->
</script>