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
<hrms:themes />
<body>
<html:form action="/gz/gz_budget/budget_examination" >
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

<table  >
<tr><td   nowrap>
 ${budgetExaminationForm.b0110_desc}  <bean:message key="gz.budget.budget_examination.budgetTable"/>：${budgetExaminationForm.tabName}   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message key="gz.budget.budgeting.currentys"/>：${budgetExaminationForm.currentBudgetDesc}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 ${budgetExaminationForm.appealStatusDesc}
</td>
</tr>
<tr><td  >
 <hrms:dataset name="budgetExaminationForm" property="fieldList" scope="session" setname="${budgetExaminationForm.tab_name}"  setalias="budget_set"
 pagerows="50" readonly="false" rowlock="true"  buttons=""   editable="true" select="false" sql="${budgetExaminationForm.sql}"  >
</td></tr>    
</hrms:dataset>

</table>

</html:form>
</body>
</html>