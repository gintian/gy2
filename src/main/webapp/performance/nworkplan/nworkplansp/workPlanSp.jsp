<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
 com.hjsj.hrms.actionform.performance.nworkplan.nworkplansp.WorkPlanSpForm"%>
<%@ page import="java.util.Date"%>

<%
String css_url="/css/css1.css";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String username=userView.getUserName();
Date date=new Date();

%>
<style>
div#tbl-container 
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
.myfixedDiv2
{ 
	overflow:auto; 
	width:100%; 
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
} 
.myfixedDiv3
{ 
	overflow:auto; 
	width:100%; 
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
} 
.myfixedDiv
{ 
	overflow:auto; 
	width:100%; 
	height:expression(document.body.clientHeight-180);
	BORDER-BOTTOM:#94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid; 
} 
</style>
<script type="text/javascript">
<!-- p0100主键，isRead 2审批，1查看--!>
	function operate(p0100,isRead,belong_type){

		var returnurl="";
		var content=document.workPlanSpForm.content.value;
		content=getEncodeStr(content);
		var name=document.workPlanSpForm.name.value;
		name=getEncodeStr(name);
		var sp_type="all";
		var state=document.workPlanSpForm.state.value;
		var year=document.workPlanSpForm.year.value;
		var season="";
		var month="";
		var week="";
		var date="<%=date%>";
		if(state=="1"){
			week=document.workPlanSpForm.week.value;
			week=getEncodeStr(week);
			returnurl="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=query`state="+state+"`content="+content+"`name="+name+"`sp_type="+sp_type+"`year="+year+"`season="+season+"`month="+month+"`week="+week;
			if(belong_type=='0'||belong_type=='1')
				///performance/nworkplan/week/searchWeekWorkplan.do?b_query=link13&personPage=0&opt=${param.opt}&isRead=${param.isRead}&p0100=${param.p0100}&returnurl=${param.returnurl}&date=${param.date}
				document.workPlanSpForm.action="/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&personPage=0&p0100="+p0100+"&opt=2&isRead="+isRead+"&returnurl="+returnurl+"&belong_type="+belong_type+"&date="+date;
			else 
		    	document.workPlanSpForm.action="/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&personPage=1&p0100="+p0100+"&opt=2&isRead="+isRead+"&returnurl="+returnurl+"&belong_type="+belong_type+"&date="+date;
			document.workPlanSpForm.submit();
		}else if(state=="2"){
			month=document.workPlanSpForm.month.value;
			returnurl="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=query`state="+state+"`content="+content+"`name="+name+"`sp_type="+sp_type+"`year="+year+"`season="+season+"`month="+month+"`week="+week;
			if(belong_type=='0'||belong_type=='1')
		     	document.workPlanSpForm.action="/performance/nworkplan/searchMonthWorkplan.do?b_query=link&personPage=0&p0100="+p0100+"&opt=2&isRead="+isRead+"&returnurl="+returnurl+"&belong_type="+belong_type+"&date="+date;
		     else
		    	 document.workPlanSpForm.action="/performance/nworkplan/searchMonthWorkplan.do?b_query=link&personPage=1&p0100="+p0100+"&opt=2&isRead="+isRead+"&returnurl="+returnurl+"&belong_type="+belong_type+"&date="+date;
		     document.workPlanSpForm.submit();
		}else if(state=="3"){
			season=document.workPlanSpForm.season.value;
			returnurl="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=query`state="+state+"`content="+content+"`name="+name+"`sp_type="+sp_type+"`year="+year+"`season="+season+"`month="+month+"`week="+week;
			if(belong_type=='0'||belong_type=='1')
				document.workPlanSpForm.action="/performance/nworkplan/searchquarters.do?b_query=link&p0100="+p0100+"&opt=2&type=1&isread="+isRead+"&returnUrl="+returnurl+"&isdept=1&belong_type="+belong_type+"&date="+date;
			else
		    	document.workPlanSpForm.action="/performance/nworkplan/searchquarters.do?b_query=link&p0100="+p0100+"&opt=2&type=1&isread="+isRead+"&returnUrl="+returnurl+"&isdept=2&belong_type="+belong_type+"&date="+date;
			document.workPlanSpForm.submit();
		}else if(state=="4"){
			year=document.workPlanSpForm.year.value;
			returnurl="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=query`state="+state+"`content="+content+"`name="+name+"`sp_type="+sp_type+"`year="+year+"`season="+season+"`month="+month+"`week="+week;
			if(belong_type=='0'||belong_type=='1')
		    	document.workPlanSpForm.action="/performance/nworkplan/searchquarters.do?b_query=link&p0100="+p0100+"&opt=2&type=2&isread="+isRead+"&returnUrl="+returnurl+"&isdept=1&belong_type="+belong_type+"&date="+date;
		    else
		    	document.workPlanSpForm.action="/performance/nworkplan/searchquarters.do?b_query=link&p0100="+p0100+"&opt=2&type=2&isread="+isRead+"&returnUrl="+returnurl+"&isdept=2&belong_type="+belong_type+"&date="+date;
			document.workPlanSpForm.submit();
		}

	}
	function query(){
		var content=document.workPlanSpForm.content.value;
		content=getEncodeStr(content);
		var name=document.workPlanSpForm.name.value;
		name=getEncodeStr(name);
		var sp_type="all";
		var state=document.workPlanSpForm.state.value;
		var year=document.workPlanSpForm.year.value;
		var season="";
		var month="";
		var week="";
		if(state=="1"){
			//week=document.workPlanSpForm.week.value;
			//week=getEncodeStr(week);
			week="all";
		}else if(state=="2"){
			//month=document.workPlanSpForm.month.value;
			month="all";
		}else if(state=="3"){
			//season=document.workPlanSpForm.season.value;
			season="all";
		}else if(state=="4"){
			year=document.workPlanSpForm.year.value;
		}
		document.workPlanSpForm.action="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=query&state="+state+"&content="+content+"&name="+name+"&sp_type="+sp_type+"&year="+year+"&season="+season+"&month="+month+"&week="+week;
		document.workPlanSpForm.submit();
	}
	function changetime(){
		var content=document.workPlanSpForm.content.value;
		content=getEncodeStr(content);
		var name=document.workPlanSpForm.name.value;
		name=getEncodeStr(name);
		var sp_type="all";
		var state=document.workPlanSpForm.state.value;
		var year=document.workPlanSpForm.year.value;
		var season="";
		var month="";
		var week="";
		if(state=="1"){
			week=document.workPlanSpForm.week.value;
			week=getEncodeStr(week);
		}else if(state=="2"){
			month=document.workPlanSpForm.month.value;
		}else if(state=="3"){
			season=document.workPlanSpForm.season.value;
		}else if(state=="4"){
			year=document.workPlanSpForm.year.value;
		}
		document.workPlanSpForm.action="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=query&state="+state+"&content="+content+"&name="+name+"&sp_type="+sp_type+"&year="+year+"&season="+season+"&month="+month+"&week="+week;
		document.workPlanSpForm.submit();
	}

</script>


<div class="bh-one">
<html:form action="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans">	
<html:hidden property="belong_type" name="workPlanSpForm"/>
<br>
<table align="center" width="95%" border="0" cellSpacing="" cellPadding="0" >
	<tr>
		<td>
		<div class="myfixedDiv3">
			<table  border="0" cellspacing="0" cellpadding="0" width="100%" >
				 <tr>
				 	<td  height="25" >
				 	&nbsp;
				 	<strong >
					<logic:equal name="workPlanSpForm" property="state" value="1">	 
				 		<hrms:optioncollection name="workPlanSpForm" property="yearlist" collection="list" />
							<html:select name="workPlanSpForm" property="year" size="1" onchange="changetime();" style="width:60px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>年 &nbsp;
						<hrms:optioncollection name="workPlanSpForm" property="weeklist" collection="list" />
							<html:select name="workPlanSpForm" property="week" size="1" onchange="changetime();" style="width:190px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>周&nbsp;
						  
					</logic:equal>
					<logic:equal name="workPlanSpForm" property="state" value="2">	 
				 		<hrms:optioncollection name="workPlanSpForm" property="yearlist" collection="list" />
							<html:select name="workPlanSpForm" property="year" size="1" onchange="changetime();" style="width:60px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>年 &nbsp;
						<hrms:optioncollection name="workPlanSpForm" property="monthlist" collection="list" />
							<html:select name="workPlanSpForm" property="month" size="1" onchange="changetime();" style="width:60px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>月&nbsp;
						  
					</logic:equal>
					<logic:equal name="workPlanSpForm" property="state" value="3">	 
				 		<hrms:optioncollection name="workPlanSpForm" property="yearlist" collection="list" />
							<html:select name="workPlanSpForm" property="year" size="1" onchange="changetime();" style="width:60px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>年 &nbsp;
						<hrms:optioncollection name="workPlanSpForm" property="seasonlist" collection="list" />
							<html:select name="workPlanSpForm" property="season" size="1" onchange="changetime();" style="width:60px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>季&nbsp;
						 
					</logic:equal>	
					<logic:equal name="workPlanSpForm" property="state" value="4">	 
				 		<hrms:optioncollection name="workPlanSpForm" property="yearlist" collection="list" />
							<html:select name="workPlanSpForm" property="year" size="1" onchange="changetime();" style="width:60px; font-weight:bolder;font-size:12px;">
								  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							 </html:select>年 &nbsp;
						 
					</logic:equal>		
					</strong>		  
				 	
 	
				 	 &nbsp;
				 		<strong>工作内容:<html:text name="workPlanSpForm" property="content" style="width:100px; font-weight:bolder;font-size:12px;"></html:text></strong>
				 	
				 	&nbsp;
				 		<strong>姓名:<html:text name="workPlanSpForm" property="name" style="width:100px; font-weight:bolder;font-size:12px;"></html:text></strong>
				 	
				 	   
				 	&nbsp;
				 	  
				 	  	<html:hidden name="workPlanSpForm" property="state"/>
				 		<input type="button" class="mybutton" value="查询"  onclick="query();">
				 	</td>
				 </tr>
			
			</table>
		</div>
		</td>
		
	</tr>


 <tr>
 	<td >
 	<div class="myfixedDiv">
 		<table  width="100%" align="center" class="ListTable"  border="0" cellSpacing="0" cellPadding="0">
 					   <%
						int i = 0;
						%>
 		
 			<tr  class="fixedHeaderTr1">
 			 	 <td align="center"  class="TableRow" width="20%" nowrap>
 					周期
 				</td>
 				 <td align="center"  class="TableRow" nowrap>
 					单位名称
 				</td>
 				 <td align="center"  class="TableRow" nowrap>
 					部门名称
 				</td>
 				<logic:equal name="workPlanSpForm" property="belong_type" value="0">
 				     <td align="center"  class="TableRow" nowrap>
 					姓名
 				</td>
 				</logic:equal>
 				 <td align="center"  class="TableRow" nowrap>
 					状态
 				</td>
 				 <td align="center"  class="TableRow" nowrap>
 					操作
 				</td>
		</tr>
		<hrms:extenditerate id="element" name="workPlanSpForm"
			property="paginationForm.list" indexes="indexes"
			pagination="paginationForm.pagination" pageCount="20"
			scope="session">
			<%
					if (i % 2 == 0)
					{
			%>
			<tr class="trShallow"
				onclick='tr_onclick(this,"#F3F5FC");'>
				<%
						} else
						{
				%>
			
			<tr class="trDeep"
				onclick='tr_onclick(this,"#E4F2FC");'>
				<%
						}
						i++;
				%>
				<td align="left" class="RecordRow" nowrap>
			
						&nbsp;<bean:write name="element" property="zhouqi" filter="true" /> 								
				</td>
				<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="b0110" filter="true" /> 								
				</td>
				<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="e0122" filter="true" /> 								
				</td>
                 <logic:equal name="workPlanSpForm" property="belong_type" value="0">
                   <td align="left" class="RecordRow" nowrap>
 				     &nbsp;<bean:write name="element" property="a0101" filter="true" /> 	
 				     </td>	
 				</logic:equal>
				<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="p0115" filter="true" /> 								
				</td>
				
				
				
					<td align="center" class="RecordRow" nowrap>
							&nbsp;<img src="/images/view.gif" style="cursor:hand;" title="查看"
							 onclick="operate('<bean:write name="element" property="p0100" filter="true" />','1','<bean:write name="element" property="belong_type" filter="true" />');"/>										
					</td>
					  
					
							
 			</tr>
 		 </hrms:extenditerate>
 		</table>
 		</div>
 	</td>
 </tr>
 <tr>
 	<td>
 				<div class="myfixedDiv2">
					<table width="100%"	class="MyRerdRowP">
						<tr>
							<td valign="bottom" align="left" class="tdFontcolor">
								第
								<bean:write name="workPlanSpForm"
									property="paginationForm.pagination.current" filter="true" />
								页 共
								<bean:write name="workPlanSpForm"
									property="paginationForm.pagination.count" filter="true" />
								条 共
								<bean:write name="workPlanSpForm"
									property="paginationForm.pagination.pages" filter="true" />
								页
							</td>
							<td align="right" nowrap class="tdFontcolor">
								<p align="right">
									<hrms:paginationlink name="workPlanSpForm"
										property="paginationForm.pagination" nameId="paginationForm"
										propertyId="roleListProperty">
									</hrms:paginationlink>
							</td>
						</tr>
					</table>				
				</div>
		</td>		
 </tr>
</table>
</html:form>
</div>