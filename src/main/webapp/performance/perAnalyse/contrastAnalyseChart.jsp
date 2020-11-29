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
	
	PerAnalyseForm perAnalyseForm=(PerAnalyseForm)session.getAttribute("perAnalyseForm");
	ArrayList pointToNameList=perAnalyseForm.getPointToNameList();
	String isShow3D=perAnalyseForm.getIsShow3D();
	String isShowScore=perAnalyseForm.getIsShowScore();
	String chart_type=perAnalyseForm.getChart_type();
	String busitype = perAnalyseForm.getBusitype();
	String isShowPercentVal = perAnalyseForm.getIsShowPercentVal();
	HashMap dataMap=(HashMap)perAnalyseForm.getDataMap();
	UserView userview=(UserView)session.getAttribute(WebConstant.userView);
	String opt = "-1";
	if(userview.getVersion()>=50)	
		opt = "0";
%>
<html>
<head>
<hrms:themes />
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</head>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT>
<%--AnyChart.js已过时，页面已经不再使用拉 haosl 2019-6-25--%>
<%--<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>--%>
<script language='javascript' >

function singleContrastAnalyse(a0100)
{
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_contrastAnalyse=query&opt=1&a0100="+a0100;
	document.perAnalyseForm.submit();  
}
  
function alertOption(chartType)
{
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_contrastAnalyse=query";
	<%if(userview.getVersion()<50){%>
		if(!document.perAnalyseForm.isShowScore.checked)
		{
			document.perAnalyseForm.isShowScore.value="0";
			document.perAnalyseForm.isShowScore.checked=true;
		}
	<%}%>
/* 	if(!document.perAnalyseForm.isShow3D.checked)
	{
		document.perAnalyseForm.isShow3D.value="0";
		document.perAnalyseForm.isShow3D.checked=true;
	} */
		
	if(chartType!=0)
	{
		document.perAnalyseForm.chart_type.value=chartType;
	}
	document.perAnalyseForm.submit();
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
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_contrastAnalyse=query";
	document.perAnalyseForm.submit();
}

function showLevel()
{
	if(document.getElementById('isShowLevel').checked){
		perAnalyseForm.isShowPercentVal.value="2";
		document.getElementById('isShowPercent').checked=false;
	}else
		perAnalyseForm.isShowPercentVal.value="0";
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_contrastAnalyse=query";
	document.perAnalyseForm.submit();
}
		
function changePlan()
{
	document.perAnalyseForm.action="/performance/perAnalyse.do?b_contrastAnalyse0=query";
	document.perAnalyseForm.target="detail";
	document.perAnalyseForm.submit(); 
}
	
function set()
{
	var obj=document.getElementById('menu_');
	obj.style.display="none";
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
	var chart = jfreechartSet2(title,"${perAnalyseForm.scoreGradeStr}",str,<%=opt%>,w_l);
	if(chart!=null&&chart!='undefined')
	{
		document.perAnalyseForm.chartParameterStr.value=chart;
		<%if(userview.getVersion()<50){%>
			if(!document.perAnalyseForm.isShowScore.checked)
			{
				document.perAnalyseForm.isShowScore.value="0";
				document.perAnalyseForm.isShowScore.checked=true;
			}
		<%}%>
	/* 	if(!document.perAnalyseForm.isShow3D.checked)
		{
			document.perAnalyseForm.isShow3D.value="0";
			document.perAnalyseForm.isShow3D.checked=true;
		} */
			
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_contrastAnalyse=query";
		document.perAnalyseForm.submit();
	}
	
}

</script>
<body  <%=(pointToNameList.size()>0?"oncontextmenu='showMenu();return false;'":"")%>   >

  <html:form action="performance/perAnalyse">  
  <table width="100%"><tr><td>
  	<!-- 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 start -->
  	<logic:notEqual value="1" name="perAnalyseForm" property="lastplan">
	    &nbsp;&nbsp;&nbsp;&nbsp;<font size='2'> <bean:message key="kh.field.plan"/>:</font>&nbsp;<!-- 计划 -->
		<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changePlan()">
	  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
		</html:select>
	</logic:notEqual>
	<!-- 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 end -->
	<% if(pointToNameList.size()>0){ %>
	<!-- 由于用了flash控件 在此不再输出图表了 -->
	<!--<input type='button' value='<bean:message key="general.inform.muster.output.excel"/>' onclick="executeExcel(2)" class="mybutton" />-->
	<% } %>
	<logic:equal name="perAnalyseForm" property="fromModule" value="analyse">
								<logic:equal name="perAnalyseForm" property="busitype" value="0">	
								<hrms:tipwizardbutton flag="performance" target="il_body" formname="perAnalyseForm"/> 
								</logic:equal>	
								<logic:equal name="perAnalyseForm" property="busitype" value="1">	
								<hrms:tipwizardbutton flag="capability" target="il_body" formname="perAnalyseForm"/> 
								</logic:equal>	

	</logic:equal>
	</td></tr>
	<tr><td id='chart1' ><!-- 4：折线图 41： 雷达图 29：柱状图 -->
	<% if(chart_type.equals("4") || chart_type.equals("30") || chart_type.equals("41")){ %>
		<%if(isShowPercentVal.equals("0")&&"1".equals(busitype)){ %>
    	<hrms:chart name="perAnalyseForm" title="" scope="session" xangle="30" 	isneedsum="false" numDecimals="0"  legends="dataMap" data="" width="1200" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1">
   		</hrms:chart>
   		<%}else{ %>
   		<hrms:chart name="perAnalyseForm" title="" scope="session" xangle="30" 	isneedsum="false" numDecimals="2"  legends="dataMap" data="" width="1200" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1">
   		</hrms:chart>
   		<%} %>
	<% } 
		if(chart_type.equals("29") || chart_type.equals("31")){
	 %>	
	 	<%if(isShowPercentVal.equals("0")&&"1".equals(busitype)){ %>
    	<hrms:chart name="perAnalyseForm" title="" scope="session" xangle="30" isneedsum="false" numDecimals="0"  legends="dataList" data="" width="1200" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1" >
   		</hrms:chart>
   		<%}else{ %>
   		<hrms:chart name="perAnalyseForm" title="" scope="session" xangle="30" isneedsum="false" numDecimals="2"  legends="dataList" data="" width="1200" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1" >
   		</hrms:chart>
   		<%} %>
		<% 
		}
		%>
	</td></tr></table>	

		<%if(userview.getVersion()<50){
		if(request.getParameter("int0")==null||!request.getParameter("int0").equals("init"))
		{
			out.println("<br><table align='center' ><tr>");
			int i=1;
			for(Iterator t=pointToNameList.iterator();t.hasNext();)
			{
				CommonData data=(CommonData)t.next();
				out.println("<td>"+data.getDataValue()+":"+data.getDataName()+"&nbsp; &nbsp; </td>");
				if(i%3==0)
					out.println("</tr><tr>");
				i++;
			}
			if(i%3!=0)
			{
				out.println("</tr>");
			}
			out.println("</table>");
		
		}
		}	
	 %>
	 
	  <div id='menu_' tabindex='1' hidefocus="true"  style="background:#ffffff;border:1px groove black;outline:0;width:130;height:150 " >
	<table>
	<%-- <tr><td align='left'><input type='checkbox' value="1" onclick='alertOption(0)' <%=(isShow3D.equals("1")?"checked":"")%>  name='isShow3D' /></td><td><bean:message key="lable.performance.3DChart"/></td></tr> --%><!-- 3D立体图 --><!-- 陈总的意思这个3D效果不需要了 -->
	<%if(userview.getVersion()<50){%>
		<tr><td align='left'><input type='checkbox' value="1" onclick='alertOption(0)' name='isShowScore' <%=(isShowScore.equals("1")?"checked":"")%>  /></td><td><bean:message key="lable.performance.showScore"/></td></tr><!-- 显示分值 -->
	<%}%>	
	
	<tr style='cursor:pointer;'><td align='left' onclick='alertOption(4)' >&nbsp;<Img src='/images/45.bmp' /></td><td onclick='alertOption(4)' ><bean:message key="lable.performance.graph"/></td></tr><!-- 折线图 -->
	<tr style='cursor:pointer;'><td align='left' onclick='alertOption(29)' >&nbsp;<Img src='/images/42.bmp' /></td><td onclick='alertOption(29)' ><bean:message key="lable.performance.histogram"/></td></tr><!-- 柱状图 -->
	<tr style='cursor:pointer;'><td align='left' onclick='alertOption(41)' >&nbsp;<Img src='/images/47.bmp' /></td><td onclick='alertOption(41)' ><bean:message key="lable.performance.radar"/></td></tr><!-- 雷达图 -->
	<%if("1".equals(busitype)){ %>	
		<tr><td><input style='cursor:pointer;' type='checkbox' id='isShowPercent'  onclick='showPercentVal()' <%=(isShowPercentVal.equals("1")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.percentval"/></td></tr><!-- 按百分制分值显示 -->
		<tr><td><input style='cursor:pointer;' type='checkbox' id='isShowLevel'  onclick='showLevel()' <%=(isShowPercentVal.equals("2")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.levelval"/></td></tr><!-- 按级别显示 -->
	<%
	  }else{
	%>
	<tr><td><input style='cursor:pointer;' type='checkbox' id='isShowPercent'  onclick='showPercentVal()' <%=(isShowPercentVal.equals("1")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.percentval"/></td></tr><!-- 按百分制分值显示 -->
	<%} %>
	<tr><td colspan=2 align='left' onclick='set()' style="cursor:pointer;">&nbsp;<bean:message key="conlumn.investigate.questionItem"/>...</td></tr><!-- 选项 -->
	</table>
	</div>

<input type='hidden' name='chart_type'  value="${perAnalyseForm.chart_type}" />
<input type='hidden' name='chartParameterStr'  value="${perAnalyseForm.chartParameterStr}" />
<html:hidden name="perAnalyseForm" property="isShowPercentVal"/>
<script language='javascript'>
	document.getElementById('menu_').style.display="none";
	//不需要判断是否是IE拉，统一命名也没问题的，这么判断 IE非兼容不能兼容 haosl 2019年6月25日
	//if(!getBrowseVersion()) {//非IE，将iframe的il_body置为和ie一样的name  detail
    if(parent.parent.frames["il_body"])
        parent.parent.frames["il_body"].name = "detail";
	//}
</script>
	 
    
  </html:form>
  </body>
</html>