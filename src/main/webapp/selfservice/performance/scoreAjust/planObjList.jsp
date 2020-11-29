<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<style>

.myfixedDiv
{ 
	overflow:auto; 
	width:100%; 
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
} 
.myfixedDiv2
{ 
	overflow:auto; 
	width:100%; 
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
} 
.RecoRowConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
.TableRow{
	border-color:#C4D8EE !important;
}
</style>
 <hrms:themes />
<script type="text/javascript">

	function scoreAjust(oper,planid,object_id)
	{
		scoreAjustForm.action="/selfservice/performance/scoreAjust.do?b_ajust="+oper+"&plan_id="+planid+"&object_id="+object_id;
		scoreAjustForm.submit();
	}
	function query()
	{
		scoreAjustForm.action="/selfservice/performance/scoreAjust.do?b_query=query";
		scoreAjustForm.submit();
	}
	
</script>

<html:form action="/selfservice/performance/scoreAjust">

		<table border="0" cellspacing="0" align="center" width="90%"  cellpadding="0">
		<tr>
			<td  align="left" nowrap>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left" nowrap height="20">
							&nbsp;  <font class="ttNomal3"><bean:message key="jx.khplan.objectype" /></font>
							<html:select name="scoreAjustForm" property="object_type" size="1"	onchange="query();">
								<html:option value="1">
									<bean:message key="jx.khplan.team" />
								</html:option>
								<html:option value="2">
									<bean:message key="label.query.employ" />
								</html:option>								
							</html:select>														
							&nbsp;
							<font class="ttNomal3"><bean:message key="org.performance.evaluate"/></font>:
							<html:select name="scoreAjustForm" property="year" size="1" onchange="query();">
								<html:option value="all">
									<bean:message key="label.all" />
								</html:option>
								<html:optionsCollection property="yearList" value="dataValue" label="dataName"/>
		   					</html:select>
		 					 <font class="ttNomal3"><bean:message key="org.performance.year"/></font>
		    				&nbsp;
		  				  <html:select name="scoreAjustForm" property="quarter" size="1" onchange="query();">
								<html:option value="all">
									<bean:message key="label.all" />
								</html:option>
								<html:option value="01">
									<bean:message key="report.pigionhole.oneQuarter" />
								</html:option>
								<html:option value="02">
									<bean:message key="report.pigionhole.twoQuarter" />
								</html:option>
								<html:option value="03">
									<bean:message key="report.pigionhole.threeQuarter" />
								</html:option>
								<html:option value="04">
									<bean:message key="report.pigionhole.fourQuarter" />
								</html:option>
		  				  </html:select>		  				  
		    			 <font class="ttNomal3"><bean:message key="org.performance.quarter"/></font>
		   				 &nbsp;
		   				 <html:select name="scoreAjustForm" property="month" size="1" onchange="query();">
								<html:option value="all">
									<bean:message key="label.all" />
								</html:option>
								<html:option value="01">
									<bean:message key="date.month.january" />
								</html:option>
								<html:option value="02">
									<bean:message key="date.month.february" />
								</html:option>
								<html:option value="03">
									<bean:message key="date.month.march" />
								</html:option>
								<html:option value="04">
									<bean:message key="date.month.april" />
								</html:option>
								<html:option value="05">
									<bean:message key="date.month.may" />
								</html:option>
								<html:option value="06">
									<bean:message key="date.month.june" />
								</html:option>
								<html:option value="07">
									<bean:message key="date.month.july" />
								</html:option>
								<html:option value="08">
									<bean:message key="date.month.auguest" />
								</html:option>
								<html:option value="09">
									<bean:message key="date.month.september" />
								</html:option>
								<html:option value="10">
									<bean:message key="date.month.october" />
								</html:option>
								<html:option value="11">
									<bean:message key="date.month.november" />
								</html:option>
								<html:option value="12">
									<bean:message key="date.month.december" />
								</html:option>							
		    			</html:select>
		     			<font class="ttNomal3"><bean:message key="org.performance.month"/></font>
		   				&nbsp;
		   				<font class="ttNomal3">名称</font>
		   				&nbsp;
		   				 <html:text name="scoreAjustForm" property='objectname' />
		   				<input type="button" name="" value='查询' onclick="query();" class="mybutton"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td  nowrap >
				<div id='myfixedDiv' class="myfixedDiv common_border_color" style='margin-top:5px;'>
					<table width="100%" border="0" cellspacing="0" align="center" id='a_table'
						cellpadding="0" class="ListTable">
						<%
						int i = 0;
						%>

						<tr id='myFixedTr'>
							<td align="center" class="TableRow_right common_background_color common_border_color" nowrap style="border-top:0px;">
								<font class="ttNomal4"><bean:message key="org.performance.khname"/></font>
							</td>
							<td align="center" class="TableRow" nowrap  style="border-top:0px;">
								<font class="ttNomal4"><bean:message key="org.performance.unorum"/></font>
							</td>
							<logic:equal name="scoreAjustForm" property="object_type" value="2">
								<td align="center" class="TableRow" nowrap style="border-top:0px;">
									<font class="ttNomal4"><bean:message key="column.name" /></font>
								</td>
							</logic:equal>						
							<td align="center" class="TableRow" nowrap style="border-top:0px;">
								<font class="ttNomal4"><bean:message key="org.performance.evaluate" /></font>
							</td>							
							<td align="center" class="TableRow" nowrap style="border-top:0px;">
								<font class="ttNomal4"><bean:message key="jx.param.degreepro" /></font>
							</td>							
							<td align="center" class="TableRow" nowrap style="border-top:0px;">
								<font class="ttNomal4"><bean:message key="label.zp_exam.sum_score" /></font>
							</td>
							<logic:equal name="scoreAjustForm" property="showGrpOrder" value="1">
								<td align="center" class="TableRow" nowrap style="border-top:0px;">
									<font class="ttNomal4"><bean:message key="jx.import.plandata.order" /></font>
								</td>
							</logic:equal>	
							<td align="center" class="TableRow_left common_background_color common_border_color" nowrap style="border-top:0px;">
								<font class="ttNomal4"><bean:message key="column.operation" /></font>
							</td>
						</tr>
						<hrms:extenditerate id="element" name="scoreAjustForm"
							property="setlistform.list" indexes="indexes"
							pagination="setlistform.pagination" pageCount="25"
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
								<td align="left" class="RecordRow_right common_border_color" nowrap>
										&nbsp;<bean:write name="element" property="name" filter="true" /> 								
								</td>
								<logic:equal name="scoreAjustForm" property="object_type" value="2">
									<td align="left" class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="b0110_e0122" filter="true" /> 								
									</td>
								</logic:equal>		
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="a0101" filter="true" /> 								
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="khPeriod" filter="true" /> 								
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="resultdesc" filter="true" /> 								
								</td>
								<td align="right" class="RecordRow" nowrap>
									<bean:write name="element" property="score" filter="true" /> 	&nbsp;							
								</td>
								<logic:equal name="scoreAjustForm" property="showGrpOrder" value="1">
									<td align="right" class="RecordRow" nowrap>
										<bean:write name="element" property="ordering" filter="true" /> &nbsp;								
									</td>
								</logic:equal>			
								<td align="center" class="RecordRow_left common_border_color" nowrap>
								    <logic:equal value="adjust" name="element" property="score_adjust">
	       								<a href="javascript:scoreAjust('adjust','<bean:write name="element" property="plan_id"/>','<bean:write name="element" property="object_id"/>');"><font class="ttNomal3">调整</font></a>
	      							</logic:equal>
									<logic:equal value="view" name="element" property="score_adjust">
	       								<a href="javascript:scoreAjust('view','<bean:write name="element" property="plan_id"/>','<bean:write name="element" property="object_id"/>');"><font class="ttNomal3">查看</font></a>
	      							</logic:equal>							
								</td>
								</tr>
						</hrms:extenditerate>
					</table>
				</div>
				
				<div class="myfixedDiv2 complex_border_color">
					<table width="100%">
						<tr>
							<td valign="bottom" align="left" class="tdFontcolor ">
								第
								<bean:write name="scoreAjustForm"
									property="setlistform.pagination.current" filter="true" />
								页 共
								<bean:write name="scoreAjustForm"
									property="setlistform.pagination.count" filter="true" />
								条 共
								<bean:write name="scoreAjustForm"
									property="setlistform.pagination.pages" filter="true" />
								页
							</td>
							<td align="right" nowrap class="tdFontcolor ">
								<p align="right">
									<hrms:paginationlink name="scoreAjustForm"
										property="setlistform.pagination" nameId="setlistform"
										propertyId="roleListProperty">
									</hrms:paginationlink>
							</td>
						</tr>
					</table>				
				</div>
				
			</td>
		</tr>
	</table>
	<script type="text/javascript">
		var myfixedDiv = document.getElementById("myfixedDiv");
		var height = document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight;
		myfixedDiv.style.height = (height-130)+"px";
		if(navigator.appName.indexOf("Microsoft")!=-1)//该样式只在ie下生效
			document.getElementById("myFixedTr").className="fixedHeaderTr";
	</script>
</html:form>

