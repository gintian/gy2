<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>
<html>
<head>
 
</head>
<style>

.top_line { 
	BORDER-TOP: #C4D8EE 1pt solid; 
}

</style>
<head>
<script language="javascript" src="/js/dict.js"></script>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script language="javascript" src="/gz/gz_budget/budget_execrate/execrate.js"></script>
<hrms:themes />
</head>
<script type="text/javascript">

function jinduo(){
	var x=document.body.clientWidth/2-300;
    var y=document.body.clientHeight/2-125;
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
function closejindu(){
  var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.display="none";
}

</script>
<body>
<html:form action="/gz/gz_budget/budget_execrate" >
 <div   id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24 id="hlw">
					请稍候,正在同步数据...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
<html:hidden property="b0110"/>

 
<table style="position:absolute;top:10px;z-index:10;">
	<tr>
		<td >
			 <hrms:dataset name="budgetExecRateForm" property="fieldList" scope="session" setname="${budgetExecRateForm.tab_name}"  setalias="budgetexec"
			 pagerows="50" readonly="false" rowlock="true"  buttons=""   editable="true" select="false" sql="${budgetExecRateForm.sql}"  >
				   
				   <hrms:commandbutton name="stat" hint=""  refresh="true" function_id="" type="selected" setname="${budgetExecRateForm.tab_name}" 
				          onclick="statistics('${budgetExecRateForm.tab_id}','是否要重新统计本期实际数？')">
				     统计
				   </hrms:commandbutton>
				   <hrms:commandbutton name="downloadTemplate" functionId="" function_id="" refresh="false" type="all-change" setname="${budgetExecRateForm.tab_name}"
				   onclick="downloadTemplate('${budgetExecRateForm.tab_id}',0);" >
				     下载模板
				   </hrms:commandbutton>
				   <hrms:commandbutton name="imp" functionId="" function_id="" refresh="true" type="all-change" setname="${budgetExecRateForm.tab_name}" 
				      onclick="imports('${budgetExecRateForm.tab_id}',0);">
				     导入数据
				   </hrms:commandbutton>
				   <hrms:commandbutton name="save" functionId="302001020227" function_id="" refresh="true" type="all-change" setname="${budgetExecRateForm.tab_name}">
				     <bean:message key="button.save"/>
				   </hrms:commandbutton>
				   <hrms:commandbutton name="export" hint=""  refresh="true" function_id="" type="selected" setname="${budgetExecRateForm.tab_name}" 
				          onclick="expbatch()">
				     导出
				   </hrms:commandbutton>   
				   
			
			</hrms:dataset>
		</td>
	</tr>    
</table>
 <table  id="selectprename"  style="position:absolute;left:380px;top:10px;z-index:10;">
	<tr>
		<td nowrap>
				&nbsp;&nbsp;&nbsp;预算年度：
				<hrms:optioncollection name="budgetExecRateForm"
					property="budgetYearList" collection="list" />
				<html:select name="budgetExecRateForm" property="budgetYear"
					onchange="selectYear(this);" style="width:80">
					<html:options collection="list" property="dataValue"
						labelProperty="dataName" />
				</html:select>
				&nbsp;&nbsp;&nbsp;月份：
				<hrms:optioncollection name="budgetExecRateForm"
					property="budgetMonthList" collection="list" />
				<html:select name="budgetExecRateForm" property="budgetMonth"
					onchange="selectMonth(this);" style="width:50">
					<html:options collection="list" property="dataValue"
						labelProperty="dataName" />
				</html:select>
		</td>
	</tr>
 </table>
</html:form>
</body>
</html>