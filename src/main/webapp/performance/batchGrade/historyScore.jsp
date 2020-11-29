<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page
	import="java.util.*,com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm,com.hrms.struts.taglib.CommonData,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,com.hrms.struts.constant.SystemConfig"%>
<%
	BatchGradeForm batchGradeForm = (BatchGradeForm) session
			.getAttribute("batchGradeForm");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<link href="/gz/templateset/standard/tableLocked.css" rel="stylesheet" type="text/css">
<script>
	function validate_self(obj, aitemdesc) {
		var dd = true;
		var itemdesc = "";
		if (aitemdesc == null || aitemdesc == undefined)
			itemdesc = "日期";
		else
			itemdesc = aitemdesc;

		if (trim(obj.value).length != 0) {
			var myReg = /^(-?\d+)(\.\d+)?$/
			if (IsOverStrLength(obj.value, 10)) {
				alert(itemdesc + " 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			} else {
				if (trim(obj.value).length != 10) {
					alert(itemdesc + " 格式不正确,正确格式为yyyy-mm-dd ！");
					return false;
				}
				var year = obj.value.substring(0, 4);
				var month = obj.value.substring(5, 7);
				var day = obj.value.substring(8, 10);
				if (!myReg.test(year) || !myReg.test(month) || !myReg.test(day)) {
					alert(itemdesc + " 格式不正确,正确格式为yyyy-mm-dd ！");
					return false;
				}
				if (year<1900||year>2100) {
					alert(itemdesc + " 年范围为1900~2100！");
					return false;
				}

				if (!isValidDate(day, month, year)) {
					alert(itemdesc + "错误，无效时间！");
					return false;
				}
			}
		}
		return dd
	}
	function search_kh_data(obj) {
		var timeInter = document.getElementById("timeInterval").value;
		if (timeInter == '4' && obj.id == 'timeInterval') {
			Element.show('datepnl');
			document.getElementById("editor1").value = '';
			document.getElementById("editor2").value = '';
			return;
		}

		var startTime = document.getElementById("editor1").value;
		var endTime = document.getElementById("editor2").value;

		if (trim(startTime) != '') {
			if (!validate_self(document.getElementById("editor1"), '起始日期'))
				return false;
		}
		if (trim(endTime) != '') {
			if (!validate_self(document.getElementById("editor2"), '结束日期'))
				return false;
		}

		document.getElementById("startDate").value = startTime;
		document.getElementById("endDate").value = endTime;

		if (startTime != '' && endTime != '')
			if (startTime > endTime) {
				alert(KHPLAN_INFO1);
				return;
			}
		var planid="${batchGradeForm.plan_id}";
		document.batchGradeForm.action = "/selfservice/performance/batchGrade.do?b_historyScore=query&planid="+planid;
		document.batchGradeForm.submit();
	}

	function excecuteExcel()
	   {
		var hashvo=new ParameterSet();			
		hashvo.setValue("timeInterval","${batchGradeForm.timeInterval}");
		hashvo.setValue("startDate","${batchGradeForm.startDate}");
		hashvo.setValue("endDate","${batchGradeForm.endDate}");
		hashvo.setValue("plan_id","${batchGradeForm.plan_id}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'90100150034'},hashvo);
	   }	
	   function showExcel(outparamters)
	   {
		var url=outparamters.getValue("excelfile");	
//		var win=open("/servlet/DisplayOleContent?filename="+url,"excel");
		//20/3/6 xus vfs改造
		var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true");
	   }
	   
</script>
<style>
   .tl_locked{
      position: relative;
      top: expression(document.getElementById("tbl-container").scrollTop);
      left: expression(document.getElementById("tbl-container").scrollLeft);
      border-width:0 1 1 0;
      z-index: 10;
   }

   .top_locked{
      position: relative;
      top: expression(document.getElementById("tbl-container").scrollTop);
      border-top:0px;
      z-index: 2;
   }
   .left_locked{
     position: relative;
     left: expression(document.getElementById("tbl-container").scrollLeft);
     border-top:0px;
     border-left:0px;
     background-color:white;
     z-index: 2;
   }
   .no_right_border{
      border-right-style:none;
   }
   .no_top_border{
      border-top-style:none;
   }
</style>
<html:form action="/selfservice/performance/batchGrade">
	<html:hidden name="batchGradeForm" property="startDate"
		styleId="startDate" />
	<html:hidden name="batchGradeForm" property="endDate" styleId="endDate" />
	<div style="position:absolute;margin-left:10px;top:0px;" >
	
		<table border="0" cellspacing="0" cellpadding="0" width="90%" style="margin-left:5px;">
		<tr>
			<td align="left" nowrap style="height: 35">
				<bean:message key="jx.khplan.timeframe" />
				<html:select name="batchGradeForm" property="timeInterval" size="1"
					onchange="search_kh_data(this);" styleId="timeInterval">
					<html:option value="">

					</html:option>
					<html:option value="1">
						<bean:message key="jx.khplan.currentyear" />
					</html:option>
					<html:option value="2">
						<bean:message key="jx.khplan.currentquarter" />
					</html:option>
					<html:option value="3">
									本月
								</html:option>
					<html:option value="4">
						<bean:message key="jx.khplan.timeduan" />
					</html:option>
				</html:select>
				<span id="datepnl"> <bean:message key="label.from" />
					<input type="text" name="start_date" value="${batchGradeForm.startDate}" extra="editor"
							style="width:100px;font-size:10pt;text-align:left;vertical-align:top;"
							id="editor1" dropDown="dropDownDate">
					<bean:message key="label.to" />
					<input type="text" name="end_date" value="${batchGradeForm.endDate}" extra="editor"
							style="width:100px;font-size:10pt;text-align:left;vertical-align:top;"
							id="editor2" dropDown="dropDownDate"> &nbsp;
					<input type="button" onclick="search_kh_data(this);"
						class="mybutton" value="<bean:message key="button.query"/>">
				</span>
			</td>
		</tr>
	</table>
	
	
	
	</div>
	<div  id="tbl-container"  style='position:absolute;left:10;top:33;height:380px;'   class="common_border_color"  >
	<%
	  ArrayList planNames = batchGradeForm.getPlanNames();
	  ArrayList objectNames = batchGradeForm.getObjectNames();
	  HashMap historyMap = batchGradeForm.getHistoryMap();
	  ArrayList objectAvg = batchGradeForm.getObjectAvg();
	  ArrayList objectTotal = batchGradeForm.getObjectTotal();
	  if(planNames.size()>0){
	%>
	<table align="right" border="0" cellspacing="0"  cellpadding="0" class="ListTable"  style="width:100%;border-collapse: separate;">
		<tr>
	    <td align="center"  class='tablerow tl_locked'     nowrap > 考核对象 </td>
	    <%
		for(int i=0;i<planNames.size();i++) {
	    %>
	    <td align="center" class='tablerow_right top_locked'    width="120px" ><%=planNames.get(i) %>&nbsp;</td>
	    <%} %>
	    <td align="center"  class='tablerow_right top_locked' nowrap width="100px">合计得分</td>
	    <td align="center"  class='tablerow_right top_locked no_right_border' nowrap width="100px">平均得分</td>
	  </tr>
	  <%
	  	for(int i=0;i<objectNames.size();i++){
	  %>	
	  <tr>
	  <td class='RecordRow left_locked'   align='center'   nowrap ><%=objectNames.get(i) %> &nbsp;</td>
	  <%
	  		for(int j=0;j<planNames.size();j++) {
	  			String score = "";
	  			score = ( (String)((ArrayList)historyMap.get(j+"")).get(i)) ;
	  %>
	  <td class='RecordRow_right no_top_border'    align='center'   width="120px"  nowrap ><%=score %>&nbsp;</td>
	  <%} %>
	  <td class='RecordRow_right no_top_border'  align='center' width="100px"  nowrap ><%=objectTotal.get(i) %>&nbsp;</td>
	  <td class='RecordRow_right no_top_border no_right_border' width="100px"  align='center'   nowrap ><%=objectAvg.get(i) %>&nbsp;</td>
	  </tr>
	  <%} %>
	  
	  
	</table>
	<br>
	
	<%} else { %>
		<span style="margin-left:5px;">没有与该查找条件相关的计划！</span>
	<%} %>
	</div>
	
	<span style="position:absolute;margin-left:10px;top:415px;"><input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
	</span>
	
	<script type="text/javascript">
	var timeInterval = document.getElementById('timeInterval').value;
	if (timeInterval != '4') {
		Element.hide('datepnl');
	}
	</script>
</html:form>


