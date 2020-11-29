<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.competencymodal.personPostModal.PersonPostModalForm,
                 com.hrms.struts.taglib.CommonData,
                 org.apache.commons.beanutils.LazyDynaBean" %>
                 
<%
	String css_url = "/css/css1.css";
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	
	PersonPostModalForm personPostModalForm = (PersonPostModalForm)session.getAttribute("personPostModalForm");
	ArrayList perDegreeList = personPostModalForm.getPerDegreeList();
	String isShow3D = personPostModalForm.getIsShow3D();
	String chart_type = personPostModalForm.getChart_type();	
	ArrayList codeItemList = personPostModalForm.getCodeItemList();
	
%>
<html>
<head>
    <link href="/css/css1.css" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT> 
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>

<script language='javascript' >
		
function changePlan()
{
	document.personPostModalForm.action="/competencymodal/personPostModal/personPostMatch.do?b_query=link&signLogo=changePlan";
	document.personPostModalForm.submit(); 
}

function changeSubSetMenu()
{
	document.personPostModalForm.action="/competencymodal/personPostModal/personPostMatch.do?b_query=link&signLogo=changeSubSetMenu";
	document.personPostModalForm.submit(); 
}	

function alertOption(chartType)
{
	document.personPostModalForm.action="/competencymodal/personPostModal/personPostMatch.do?b_query=link&signLogo=changePlan";	
	/* if(!document.personPostModalForm.isShow3D.checked)
	{
		document.personPostModalForm.isShow3D.value="0";
		document.personPostModalForm.isShow3D.checked=true;
	} */		
	if(chartType!=0)
	{
		document.personPostModalForm.chart_type.value=chartType;
	}
	document.personPostModalForm.submit();
}

// 点击图例反查结果
function reverseResult(e)
{
	var name=e.name;
	var greeName = e.seriesName;
 /* 	for(var s in e.data)
  	{
  		if(s=='Series')
  			greeName = e.data[s].Name;
	}	 */
/*	
 	for(var s in e.data)
  	{
  		if(s=='Series')
  		{
  			for(var ss in e.data[s])
  				alert(ss+" --- "+e.data[s][ss]);
  				
  		}
  		else
	  		alert(s+"  "+e.data[s]);
	}
*/			
//  if(name!="")
    {
    	name=$URL.encode(getEncodeStr(name));
    	greeName=$URL.encode(getEncodeStr(greeName));
      	personPostModalForm.action="/competencymodal/personPostModal/reverseResultList.do?b_reverse=link&degreeName="+name+"&flag=1&greeName="+greeName;
      	personPostModalForm.submit();
    }
}
       
</script>
<hrms:themes />
<body  <%=(perDegreeList.size()>0?"oncontextmenu='showMenu();return false;'":"")%>   >

	<html:form action="/competencymodal/personPostModal/personPostMatch">  
  		<table width="100%">
  			<tr><td style="height:35px">   
	    		&nbsp;&nbsp;&nbsp;&nbsp;<font size='2'> <bean:message key="kh.field.plan"/>:</font>&nbsp;
				<html:select name="personPostModalForm" property="plan_id" size="1" onchange="changePlan()">
	  	 			<html:optionsCollection property="planList" value="dataValue" label="dataName"/>
				</html:select>	
				
				&nbsp;&nbsp;&nbsp;&nbsp;<font size='2'> 按</font>&nbsp;
				<html:select name="personPostModalForm" property="subSetMenu" size="1" onchange="changeSubSetMenu()">
	  	 			<html:optionsCollection property="subSetMenuList" value="dataValue" label="dataName"/>
				</html:select>
				<font size='2'> 分析</font>
				
				<% if(codeItemList.size()>1){%>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<html:select name="personPostModalForm" property="layer" size="1" onchange="changeSubSetMenu()">
	  	 			<html:optionsCollection property="codeItemList" value="dataValue" label="dataName"/>
				</html:select>
				<% }%>

			</td></tr>
			<tr><td id='chart0' >
				
	 			<hrms:chart name="personPostModalForm" title="" scope="session" xangle="45" numDecimals="0" legends="dataList" data="" 
	 				        width="${personPostModalForm.chartWidth}" height="${personPostModalForm.chartHeight}" chart_type="${personPostModalForm.chart_type}" 
	 				        pointClick="reverseResult" labelIsPercent="0" chartParameter="chartParam" chartpnl="chart0" >
   				</hrms:chart>	
			
			</td></tr>
		</table>			
	 
		<div id='menu_' onblur='hiddenElement()'  style="background:#ffffff;border:1px groove black;width:100;height:60; " >
			<table>
			    <%-- 7x不支持3D --%>
				<%--<tr><td align='left'><input type='checkbox' value="1" onclick='alertOption(0)' <%=(isShow3D.equals("1")?"checked":"")%>  name='isShow3D' /></td>
					<td><bean:message key="lable.performance.3DChart"/></td>
				</tr>	--%>												
				<tr style='cursor:pointer;'><td align='left' onclick='alertOption(20)' >&nbsp;<Img src='/images/45.bmp' /></td>
					<td onclick='alertOption(20)' >饼图</td>
				</tr>
				<tr style='cursor:pointer;'><td align='left' onclick='alertOption(11)' >&nbsp;<Img src='/images/42.bmp' /></td>
					<td onclick='alertOption(11)' ><bean:message key="lable.performance.histogram"/></td>
				</tr>								
			</table>
		</div>

		<input type='hidden' name='chart_type' value="${personPostModalForm.chart_type}" />		
		<script language='javascript'>
			document.getElementById('menu_').style.display="none";			
		</script>	 
    
  	</html:form>
  	
</body>
</html>
