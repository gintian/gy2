<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm,
                 com.hrms.struts.taglib.CommonData,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	String css_url="/css/css1.css";	
	UserView userview=(UserView)session.getAttribute(WebConstant.userView);
	
	PerAnalyseForm perAnalyseForm=(PerAnalyseForm)session.getAttribute("perAnalyseForm");
	String returnflag = perAnalyseForm.getReturnflag();	
	ArrayList pointToNameList=perAnalyseForm.getPointToNameList();
	HashMap dataMap=(HashMap)perAnalyseForm.getDataMap();
	String isShowPercentVal = perAnalyseForm.getIsShowPercentVal();
	String busitype = perAnalyseForm.getBusitype();
%>
<html>
<head>
    
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</head>
<style>

.TableTitltRow {
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	background-color:#f4f7f7;	
	font-weight: bold;	
	valign:middle;
}
.RecordBodyRow {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
}
</style>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT> 
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<script language='javascript' >

function personStationAnalyse(a0100)
{
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_personStation=query&opt=1&a0100="+a0100;
	document.perAnalyseForm.submit();  
}
  			
function changePlan()
{
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_personStation0=query";
	document.perAnalyseForm.target="il_body";
	document.perAnalyseForm.submit(); 
}

// 人岗匹配or岗人匹配
function checkPerStion(objType)
{
	var returnURL = "/performance/perAnalyse.do?b_personStation=query&opt=1&a0100=${perAnalyseForm.object_id}";
	personPostMatchingForm.action="/competencymodal/person_post_matching/person_post_matching.do?b_init=init&plan_id=${perAnalyseForm.planIds}&objE01A1=${perAnalyseForm.objE01A1}&object_id=${perAnalyseForm.object_id}&objType="+objType+"&returnURL="+returnURL;
//  personPostMatchingForm.target="il_body";
    personPostMatchingForm.submit();
    		 
}	

/*
// 关联的能力课程
function selectAbilityClass(point_id,pointsetid,subsys_id)
{		 
	khFieldForm.action="/performance/kh_system/kh_field/init_kh_field.do?b_search=search&personStation=ps&point_id="+point_id+"&pointsetid="+pointsetid+"&subsys_id="+subsys_id;
//  khFieldForm.target="mil_body";
//  khFieldForm.target="il_body";    
    khFieldForm.submit();    
}
*/

// 能力素质模型推送考核对象考核不合格的指标关联的课程:单人单指标的推送
function sendLessons(point_id)
{		
	var hashvo=new ParameterSet();
	hashvo.setValue("hjsoft","singlePoint");
	hashvo.setValue("plan_id","${perAnalyseForm.planIds}");
	hashvo.setValue("object_id","${perAnalyseForm.object_id}");
	hashvo.setValue("point_id",point_id);
	var request=new Request({method:'post',asynchronous:true,onSuccess:propellingSuc,functionId:'9023000297'},hashvo);		
}
function propellingSuc(outparamters)
{	
	var flag = outparamters.getValue("flag");
	if(flag=='ok')
		alert("推送成功！");
	else if(flag=='hjsoft')
		alert("课程已存在，不允许重复推送！");
	else if(flag=='nook')
		alert("没有要推送的课程！");
	else if(flag=='good')
		alert("成绩合格无需推送！");
	else
		alert("推送失败！");	
}

function set()
{
	var title=""
	var str="";
	var w_l="";
	if(document.perAnalyseForm.chartParameterStr.value.length>0)
	{	
		var temps=document.perAnalyseForm.chartParameterStr.value.split("`");
		title=temps[0];
		str=document.perAnalyseForm.chartParameterStr.value.substring(title.length+1);
			
		if(temps.length>=10&&temps[9].length>0)
		{
			var wl=temps[9].split(",");
			w_l=wl[0]+","+wl[1];
		}
	}
	var chart = jfreechartSet2("","${perAnalyseForm.scoreGradeStr}",str,0,w_l);
	if(chart!=null&&chart!='undefined')
	{
		document.perAnalyseForm.chartParameterStr.value=chart;		
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_personStation=query";
		document.perAnalyseForm.submit();
	}
	//var url="/general/deci/browser/chartset/chartset.do?br_chartset=link`opt=0";
   // var return_vo=window.showModalDialog(url,"","dialogWidth:459px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no"); 
	
}
function showPercentVal()
{		
	<%if("1".equals(busitype)){ %>
		if(document.getElementById('isShowPercent').checked){
			perAnalyseForm.isShowPercentVal.value="1";
			document.getElementById('isShowLevel').checked=false;
		}else
			perAnalyseForm.isShowPercentVal.value="0";
	<%}else{%>
		if(document.getElementById('isShowPercent').checked)
			perAnalyseForm.isShowPercentVal.value="1";
		else
			perAnalyseForm.isShowPercentVal.value="0";
	<%}%>
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_personStation=query";
	document.perAnalyseForm.submit();
}

function showLevel()
{
	if(document.getElementById('isShowLevel').checked){
		perAnalyseForm.isShowPercentVal.value="2";
		document.getElementById('isShowPercent').checked=false;
	}else
		perAnalyseForm.isShowPercentVal.value="0";
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_personStation=query";
	document.perAnalyseForm.submit();
}
	
</script>
<hrms:themes />
<body  <%=(pointToNameList.size()>0?"oncontextmenu='showMenu();return false;'":"")%>   >

  <html:form action="performance/perAnalyse">  
  <table width="97%" border="0" cellspacing="0"  align="center" cellpadding="0">
  		<logic:notEqual  name="perAnalyseForm"  property="isfromKhResult"   value="1">
  		<tr>
   		
	  		<td style="height:35px;white-space:nowrap;">
	  		
	  		<% if(returnflag!=null && returnflag.trim().length()>0 && returnflag.equalsIgnoreCase("9")){}else{ %>
			    <font size='2'> <bean:message key="kh.field.plan"/>:</font>&nbsp;
				<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changePlan()">
			  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
				</html:select>
			<% } %>	
				&nbsp;&nbsp;
				<!--  
				 <hrms:priv func_id='36040201'> 
				 <input type="button" name="b_search" value="岗人匹配" onclick="checkPerStion('1')" class="mybutton"> 		
				 </hrms:priv> 
				-->
				<logic:equal name="perAnalyseForm" property="fromModule" value="analyse">
									<logic:equal name="perAnalyseForm" property="busitype" value="0">	
									<hrms:tipwizardbutton flag="performance" target="il_body" formname="perAnalyseForm"/> 
									</logic:equal>	
									<logic:equal name="perAnalyseForm" property="busitype" value="1">	
									<hrms:tipwizardbutton flag="capability" target="il_body" formname="perAnalyseForm"/> 
									</logic:equal>	
				</logic:equal>
			</td>
		</tr>
		</logic:notEqual>
		<tr>	
			<logic:equal  name="perAnalyseForm"  property="isfromKhResult"   value="1">
				<td style="padding:0 0 0 200" width="700px" align="center" id='chart1' valign="top"><%-- bug 38865 wangb 20180725 统计图宽度固定700px--%>
		    	<hrms:chart name="perAnalyseForm" title="" scope="session" xangle="30" numDecimals="2" isneedsum="false" legends="dataMap" data="" width="700" height="550" chart_type="41" labelIsPercent="0" chartParameter="chartParam" chartpnl="chart1">
		   		</hrms:chart>
				</td>	
			</logic:equal>
			<logic:equal  name="perAnalyseForm"  property="isfromKhResult"   value="0">
				<td width="40%" align="left" valign="top">	
				<br>
			    	${perAnalyseForm.gradeResultHtml}	
				</td>	
				<td width="60%" align="center" id='chart1' valign="top"><%-- bug 38865 wangb 20180725 统计图宽度固定700px--%>
		    	<hrms:chart name="perAnalyseForm" title="" scope="session" xangle="30" numDecimals="2" isneedsum="false" legends="dataMap" data="" width="700" height="550" chart_type="41" labelIsPercent="0" chartParameter="chartParam" chartpnl="chart1">
		   		</hrms:chart>
				</td>	
			</logic:equal>
		
	</tr>
  </table>		
    <input type='hidden' name='chartParameterStr'  value="${perAnalyseForm.chartParameterStr}" />
     <input type='hidden' name='isShowPercentVal'  value="${perAnalyseForm.isShowPercentVal}" />
    <div id='menu_' tabindex='1' onblur='hiddenElement()' hidefocus="true" style="background:#ffffff;outline:0;width:130;height:100 " class="complex_border_color" >
	<table>	
	<tr><td><input type='checkbox' id='isShowPercent'  onclick='showPercentVal()' <%=(isShowPercentVal.equals("1")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.percentval"/></td></tr><!-- 按百分制分值显示 -->
	<%if("1".equals(busitype)){ %>	
		<tr><td><input type='checkbox' id='isShowLevel'  onclick='showLevel()' <%=(isShowPercentVal.equals("2")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.levelval"/></td></tr><!-- 按级别显示 -->
	<%}%>
	<tr><td colspan=2 align='left' onclick='set()' style="cursor:default;">&nbsp;<bean:message key="conlumn.investigate.questionItem"/>...</td></tr><!-- 选项 -->
	</table>
	</div>
	<script language='javascript'>
		document.getElementById('menu_').style.display="none";
	
	</script>
</html:form>


<html:form action="/competencymodal/person_post_matching/person_post_matching">
<input type="hidden" name="returnURL" value="/performance/perAnalyse.do?b_personStation=query&opt=1&a0100=${perAnalyseForm.object_id}"/>
<input type="hidden" name="target" value="il_body"/>
</html:form>

<html:form action="/performance/kh_system/kh_field/init_kh_field">
<input type="hidden" name="returnURL" value="/performance/perAnalyse.do?b_personStation=query&opt=1&a0100=${perAnalyseForm.object_id}"/>
<input type="hidden" name="target" value="il_body"/>
</html:form>


</body>
</html>
