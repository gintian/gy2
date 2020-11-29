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
	HashMap dataMap=(HashMap)perAnalyseForm.getDataMap();
	UserView userview=(UserView)session.getAttribute(WebConstant.userView);
%>

<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<hrms:themes />
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
	<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
	<SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript>
	var busitype = '${perAnalyseForm.busitype}';
	var theObjSel = '';
	var plansSel=getCookie('plansSel');
	if(busitype!=null && busitype.length>0 && busitype=='1')
		plansSel =  getCookie('modalPlansSel');
	if(plansSel==null)
		plansSel="";	
	
	//多指标分析
	function multiplePointAnalyse(a0100)
	{
		/*var plansSel=getCookie('plansSel');
		if(plansSel==null)
		{
			alert(SEL_PLAN_INFO);
			return;
		}	
		document.perAnalyseForm.planIds.value=plansSel; */
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_multiplePointAnalyse=query0&a0100="+a0100+"&cooki_planids="+plansSel;
		document.perAnalyseForm.submit();
	}
	

	
	function hidenMenu()
	{
		// Element.hide('menu_');
	}
	
	function alertOption(chartType)
	{
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_multiplePointAnalyse=<%=request.getParameter("b_multiplePointAnalyse")%>&cooki_planids="+plansSel;
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
		var title=QSFX;
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
		var chart = jfreechartSet2(title,"",str,-2,w_l);
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
			
			document.perAnalyseForm.action="/performance/perAnalyse.do?b_multiplePointAnalyse=<%=request.getParameter("b_multiplePointAnalyse")%>&cooki_planids="+plansSel;
			document.perAnalyseForm.submit();
		}
	}
	function setPlan2(model)
	{
		var strurl="/performance/perAnalyse.do?b_intPlanList=query`opt=0`cookiFlag=<%=request.getParameter("b_multiplePointAnalyse")%>";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   		var dialogWidth="850";
   		var dialogHeight="710";
   		if(!window.showModalDialog){
   	  		window.dialogArguments = arguments;
   	    }
   	    var config = {
   			width:dialogWidth,
   	        height:dialogHeight,
   	        dialogArguments:arguments,
   	        type:'2'
   	    }
   		modalDialog.showModalDialogs(iframe_url,'',config,function(ss){
   			if(typeof ss =='object' && model == '2')
   			{	
   				//document.perAnalyseForm.planIds.value=ss[1];
   				document.perAnalyseForm.action="/performance/perAnalyse.do?b_multiplePointAnalyse=query&cooki_planids="+ss[1];
   				document.perAnalyseForm.submit();
   			}
   		})
   		
		
	}
	function setPlan_OK(ss){
		if(typeof ss =='object')
		{	
			document.perAnalyseForm.action="/performance/perAnalyse.do?b_multiplePointAnalyse=query&cooki_planids="+ss[1];
			document.perAnalyseForm.submit();
		}
	}
	
	</SCRIPT>     
</HEAD>
<body <%=(dataMap.size()>0?"oncontextmenu='showMenu();return false;'":"")%>  >

<html:form action="performance/perAnalyse">
	<table width="100%"><tr><td>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' value='<bean:message key="button.sys.cond"/>..' onclick="setPlan2('2')" class="mybutton" />
	<% if(dataMap.size()>0){ %>
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
	<tr><td id='chart1'>

		<hrms:chart name="perAnalyseForm" title=""   numDecimals="2" isneedsum="false" xangle="30" scope="session" legends="dataMap" data="" width="${perAnalyseForm.chartWidth}" height="${perAnalyseForm.chartHeight}" chart_type="${perAnalyseForm.chart_type}"  labelIsPercent="0"   chartParameter="chartParam"  chartpnl="chart1">
   		</hrms:chart>

	<%if(userview.getVersion()<50){
		if(request.getParameter("b_multiplePointAnalyse")!=null)
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
	
	</td></tr>
	</table>

	<div id='menu_'  tabindex='1' onblur='hiddenElement()' hidefocus="true"  style="background:#ffffff;border:1px groove black;outline:0;width:100;height:100 " >
	<table><%--去掉3D立体图  wangb  20180720 --%>
	<%--<tr><td><input type='checkbox' value="1" onclick='alertOption(0)' <%=(isShow3D.equals("1")?"checked":"")%>  name='isShow3D' /></td><td><bean:message key="lable.performance.3DChart"/></td></tr>--%>
	<%if(userview.getVersion()<50){%>
		<tr><td><input style='cursor:pointer;' type='checkbox' value="1" onclick='alertOption(0)' name='isShowScore' <%=(isShowScore.equals("1")?"checked":"")%>  /></td><td><bean:message key="lable.performance.showScore"/></td></tr>
	<%}%>
<!--
	<tr><td><Img src='/images/45.bmp' /></td><td><font color='gray'><bean:message key="lable.performance.graph"/></font></td></tr>
	<tr><td><Img src='/images/42.bmp' /></td><td><font color='gray'><bean:message key="lable.performance.histogram"/></font></td></tr>
-->
	<tr style='cursor:pointer;'><td align='left' onclick='alertOption(4)' >&nbsp;<Img src='/images/45.bmp' /></td><td onclick='alertOption(4)' ><bean:message key="lable.performance.graph"/></td></tr>
	
	<% if(userview.getVersion()<50)	 {%>
	<tr><td colspan=2 align='left' onclick='set()' style="cursor:poniter;">&nbsp;<bean:message key="conlumn.investigate.questionItem"/>...</td></tr>
	<% }%>
	</table>
	</div>

<input type='hidden' name='chart_type'  value="${perAnalyseForm.chart_type}" />
<input type='hidden' name='chartParameterStr'  value="${perAnalyseForm.chartParameterStr}" />
<!-- <input type='hidden' name='planIds'  value="${perAnalyseForm.planIds}" /> -->
<html:hidden name="perAnalyseForm" property="planIds"/>
	<script language='javascript'>
	document.getElementById('menu_').style.display="none";
	<%//System.out.println("--"+request.getParameter("b_singlePointAnalyse"));
	if(request.getParameter("b_multiplePointAnalyse")!=null && request.getParameter("b_multiplePointAnalyse").equalsIgnoreCase("query")){%>
		if(busitype==null || busitype.length<=0 || busitype=='0')
		{
			setCookie("plansSel","${perAnalyseForm.planIds}");//在高级中选中的计划就放到cooki中 这样以后的数据都根据选中的计划来
		}
		else
		{
			setCookie("modalPlansSel","${perAnalyseForm.planIds}");//在高级中选中的计划就放到cooki中 这样以后的数据都根据选中的计划来
		}
	<%}else if(request.getParameter("b_multiplePointAnalyse")!=null && request.getParameter("b_multiplePointAnalyse").equalsIgnoreCase("query0")){%>
		if(busitype==null || busitype.length<=0 || busitype=='0')
		{
			setCookie("plansSel2","${perAnalyseForm.planIds}");//由左边的考核对象和指标确定的考核计划
		}
		else
		{
			setCookie("modalPlansSel2","${perAnalyseForm.planIds}");//由左边的考核对象和指标确定的考核计划
		}				
	<%}%>
	</script>

</html:form>
</body>
</html>