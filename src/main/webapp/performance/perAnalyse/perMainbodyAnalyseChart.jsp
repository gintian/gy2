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
	String objectType=perAnalyseForm.getObjectType();
	String isShow3D=perAnalyseForm.getIsShow3D();
	String isShowScore=perAnalyseForm.getIsShowScore();
	String chart_type=perAnalyseForm.getChart_type();
	String isShowPercentVal = perAnalyseForm.getIsShowPercentVal();
	HashMap dataMap=(HashMap)perAnalyseForm.getDataMap();
	UserView userview=(UserView)session.getAttribute(WebConstant.userView);
	String opt = "-1";
	if(userview.getVersion()>=50)	
		opt = "0";
%>

<html>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
  <SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT>   
  <script type="text/javascript" src="/js/constant.js"></script>
  <script language='javascript' >
  	function mainbodyAnalyse(codeitemid)
	{
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_perMainbodyAnalyse=query&codeitemid="+codeitemid;
		document.perAnalyseForm.submit();
	}
  
  
  	function changeType()
  	{
  		document.perAnalyseForm.action="/performance/perAnalyse.do?b_perMainbodyAnalyse0=query";
		document.perAnalyseForm.target="detail";
		document.perAnalyseForm.submit();
  	
  	}
  	
  	
  	  function executeChart()
	  {
	  		var selectNode=parent.mil_menu.multipleContrastAnalyse();
			if(selectNode.length>0)
			{
				var temps=selectNode.split(",");
			  	for(var i=0;i<temps.length;i++)
			  	{
			  		if(temps[i].length>0)
			  		{
			  			if(temps[i].length<=3||temps[i].substring(0,3)!='usr')
			  			{
			  			   alert(P_A_INFO1+"!");
			  				return ;
			  			}
			  		}
			  	}
			  	document.perAnalyseForm.action="/performance/perAnalyse.do?b_perMainbodyAnalyse=query&objects="+selectNode;
			    document.perAnalyseForm.submit();
			 }
			 else
			 {
			 	alert(P_A_INFO2+"!");
			 }
	  }
  	
  	
  	 function alertOption(chartType)
	{
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_perMainbodyAnalyse=query";
		<%if(userview.getVersion()<50){%>
		if(!document.perAnalyseForm.isShowScore.checked)
		{
			document.perAnalyseForm.isShowScore.value="0";
			document.perAnalyseForm.isShowScore.checked=true;
		}
		<%}%>
		/* if(!document.perAnalyseForm.isShow3D.checked)
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
			/* if(!document.perAnalyseForm.isShow3D.checked)
			{
				document.perAnalyseForm.isShow3D.value="0";
				document.perAnalyseForm.isShow3D.checked=true;
			} */
			
			document.perAnalyseForm.action="/performance/perAnalyse.do?b_perMainbodyAnalyse=query";
			document.perAnalyseForm.submit();
		}
	}
	function showPercentVal()
	{		
		if(document.getElementById('isShowPercent').checked)
			perAnalyseForm.isShowPercentVal.value="1";
		else
			perAnalyseForm.isShowPercentVal.value="0";
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_perMainbodyAnalyse=query";
		document.perAnalyseForm.submit();
	}
  </script>
  <head>
    <link href="/css/css1.css" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
  </head>
<hrms:themes />
  
  <body  <%=(dataMap.size()>0?"oncontextmenu='showMenu();return false;'":"")%>  >
 
<html:form action="performance/perAnalyse">  
<table width="100%"><tr><td>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size='2'><bean:message key="kh.field.plan"/>:</font>&nbsp;
	<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changeType()">
  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
	</html:select>&nbsp;
     
     <select name='objectType' onchange='changeType()' >
     	<Option value='0' <%=(objectType.equals("0")?"selected":"")%> ><bean:message key="lable.performance.singleObject"/></Option>
     	<Option value='1' <%=(objectType.equals("1")?"selected":"")%> ><bean:message key="lable.performance.mulObject"/></Option>
     </select>
	<logic:equal name="perAnalyseForm" property="objectType" value="1">
	&nbsp;
	<Input type='button' value='<bean:message key="lable.performance.executeChart"/>'  class="mybutton"  onclick='executeChart()'  />
	</logic:equal>
	<% if(dataMap.size()>0){ %>
		<!-- 由于用了flash控件 在此不再输出图表了 -->
		<!-- <input type='button' value='<bean:message key="general.inform.muster.output.excel"/>' onclick="executeExcel(6)" class="mybutton" />-->
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
<tr><td id='chart1' >	  
	
	<% if(chart_type.equals("4") || chart_type.equals("30") || chart_type.equals("41")){ %>
    	<hrms:chart name="perAnalyseForm" title="" scope="session" isneedsum="false" xangle="30" numDecimals="2" legends="dataMap" data="" width="${perAnalyseForm.chartWidth}" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1">
   		</hrms:chart>
	<% } 
		if(chart_type.equals("29") || chart_type.equals("31")){
	%>	
	<!-- 不需要显示总数 2013.12.05 pjf -->
	 	<hrms:chart name="perAnalyseForm" title="" scope="session" isneedsum="false" xangle="30" numDecimals="2" legends="dataList" data="" width="${perAnalyseForm.chartWidth}" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1" >
   		</hrms:chart>	
	<%}%>
</td></tr></table>	  		
   		
		<%if(userview.getVersion()<50){
		if(pointToNameList.size()>0&&objectType.equals("0"))
		{
			out.println("<br><table align='center' ><tr>");
			int i=1;
			for(Iterator t=pointToNameList.iterator();t.hasNext();)
			{
				LazyDynaBean data=(LazyDynaBean)t.next();
				out.println("<td>"+(String)data.get("point_id")+":"+(String)data.get("pointname")+"&nbsp; &nbsp; </td>");
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
	 
	 
	 <div id='menu_' tabindex='1' onblur='hiddenElement()' hidefocus="true"   style="background:#ffffff;border:1px groove black;outline:0;width:130;height:120; " >
	<table><%--去掉3D立体图  wangb 20180720 --%>
	<%--<tr><td><input type='checkbox' value="1" onclick='alertOption(0)' <%=(isShow3D.equals("1")?"checked":"")%>  name='isShow3D' /></td><td><bean:message key="lable.performance.3DChart"/></td></tr>--%>
	<%if(userview.getVersion()<50){%>
		<tr><td style='cursor:pointer;'><input type='checkbox' value="1" onclick='alertOption(0)' name='isShowScore' <%=(isShowScore.equals("1")?"checked":"")%>  /></td><td><bean:message key="lable.performance.showScore"/></td></tr>
	<%}%>
	
<!--	<tr><td><Img src='/images/45.bmp' /></td><td><font color='gray'><bean:message key="lable.performance.graph"/></font></td></tr>
	<tr><td><Img src='/images/42.bmp' /></td><td><font color='gray'><bean:message key="lable.performance.histogram"/></font></td></tr>
	<tr><td><Img src='/images/45.bmp' /></td><td><font color='gray'><bean:message key="lable.performance.radar"/></font></td></tr>  -->  

	<tr style='cursor:pointer;'><td align='left' onclick='alertOption(4)' >&nbsp;<Img src='/images/45.bmp' /></td><td onclick='alertOption(4)' ><bean:message key="lable.performance.graph"/></td></tr>
	<logic:equal name="perAnalyseForm" property="objectType" value="0">
		<tr style='cursor:pointer;'><td align='left' onclick='alertOption(29)' >&nbsp;<Img src='/images/42.bmp' /></td><td onclick='alertOption(29)' ><bean:message key="lable.performance.histogram"/></td></tr>    
	</logic:equal>
	<tr style='cursor:pointer;'><td align='left' onclick='alertOption(41)' >&nbsp;<Img src='/images/47.bmp' /></td><td onclick='alertOption(41)' ><bean:message key="lable.performance.radar"/></td></tr>	 
		
	<tr><td style='cursor:pointer;'><input type='checkbox' id='isShowPercent'  onclick='showPercentVal()' <%=(isShowPercentVal.equals("1")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.percentval"/></td></tr>
	<tr><td style='cursor:pointer;' colspan=2 align='left' onclick='set()' style="cursor:default;">&nbsp;<bean:message key="conlumn.investigate.questionItem"/>...</td></tr>
	</table>
	</div>

<input type='hidden' name='chart_type'  value="${perAnalyseForm.chart_type}" />
<input type='hidden' name='chartParameterStr'  value="${perAnalyseForm.chartParameterStr}" />
<html:hidden name="perAnalyseForm" property="isShowPercentVal"/>
<script language='javascript'>
	document.getElementById('menu_').style.display="none";
    //这么判断会把IE非兼容的情况排除了，个人觉得没有必要判断。haosl 2019年6月21日 bug 49426
	//if(!getBrowseVersion()) {//非IE，将iframe的il_body置为和ie一样的name  detail
    if(parent.parent.frames["il_body"])
		parent.parent.frames["il_body"].name = "detail";
	//}
</script>
</html:form>  
  </body>
</html>
