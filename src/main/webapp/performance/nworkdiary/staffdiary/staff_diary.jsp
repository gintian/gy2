<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.staff.StaffForm" %>
<%@ page import="java.util.ArrayList" %>
<%
StaffForm staffForm = (StaffForm)session.getAttribute("staffForm");
String year = staffForm.getStaff_year();
String month = staffForm.getStaff_month();
String week = staffForm.getStaff_week();
String day = "-1";
String tmpday = staffForm.getStaff_day();
if(!tmpday.equals("-1")){
	String[] array = tmpday.split("-");
	year = array[0];
	month = array[1];
	day = array[2];
}
String staff_name = staffForm.getStaff_name();
%>
<html>
  <head>
  <link href="/performance/nworkdiary/calendar.css" rel="stylesheet" type="text/css">
  </head>
  
  <body>
  <html:form action="/performance/nworkdiary/myworkdiary/weekwork">
  	<html:hidden property="staff_url" name="calendarWeekForm"/>
  </html:form>
  <html:form action="/performance/nworkdiary/myworkdiary/daywork">
  	<html:hidden property="staff_url" name="calendarDayForm"/>
  </html:form>
  <html:form action="/performance/nworkdiary/myworkdiary/monthwork">
  	<html:hidden property="returnUrl" name="monthWorkForm"/>
  </html:form>
  <html:form action="/performance/nworkdiary/myworkdiary/yearwork">
  	<html:hidden property="returnUrl" name="yearWorkForm"/>
  </html:form>
  <html:form action="/performance/nworkdiary/staffdiary/staff_diary">
  		<html:hidden property="a0100" name="staffForm"/>
  		<html:hidden property="nbase" name="staffForm"/>
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
			<tr><td height="2"></td></tr>
			<tr>
				<td width="80%" align="left">
					&nbsp;年份
					<hrms:optioncollection name="staffForm" property="yearList" collection="list" />
						<html:select name="staffForm" property="staff_year" size="1" onchange="changeYear();">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
					&nbsp;&nbsp;月份
					<hrms:optioncollection name="staffForm" property="monthList" collection="list" />
						<html:select name="staffForm" property="staff_month" size="1" onchange="changeMonth();">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
					&nbsp;&nbsp;周
					<hrms:optioncollection name="staffForm" property="weekList" collection="list" />
						<html:select name="staffForm" property="staff_week" size="1" onchange="changeWeek();">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
					&nbsp;&nbsp;日
				<hrms:optioncollection name="staffForm" property="dayList" collection="list" />
						<html:select name="staffForm" property="staff_day" size="1" onchange="changeDay();">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
				
				<div style="position:absolute;z-index:1;left:456px;top:7px;">        
					姓名
				</div>

				<div style="position:absolute;z-index:2;left:485px;top:2px;width:121px;height:20px">
					<html:text name="staffForm" property="staff_name" size="10" styleId="selectname" onkeyup="showDateSelectDiv('selectname')" style="position:absolute;width:121px; height:20px;left:0"/>
				</div>
				<div style="position:absolute;z-index:1;left:485px;top:2px;width:137px;height:20px"> 
				<hrms:optioncollection name="staffForm" property="staff_namelist" collection="list" />
						<html:select styleId="objectbox" name="staffForm" property="staff_name" size="1" style="position:absolute;width:137px;height:20px;clip:rect(0 137 20 121);left:0" onchange="executeChange();">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
				</div>
				<div style="position:absolute;z-index:1;left:655px;">
					<input type="button" value="查找" class="mybutton" onclick="sub();">
				</div>
				<logic:notEqual name="staffForm" property="a0100" value="">
				<div style="position:absolute;z-index:1;left:710px;">
					<input type="button" value="返回" class="mybutton" onclick="returnDepart('<bean:write  name="staffForm" property="a0100" />','<bean:write  name="staffForm" property="nbase" />')">
				</div>
				</logic:notEqual>
				</td>
			</tr>
			<tr><td height="5"></td></tr>
		</table>
		
  		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  				<thead>
  					<td class="TableRow" align="center" width="5%">
  						<input type="checkbox" name="selbox" onclick="operateCheckBox(this);" title="全选">
					</td>
  					<td class="TableRow" align="center" width="5%">年份</td>
  					<logic:notEqual name="staffForm" property="staff_month" value="-1">
			        <td class="TableRow" align="center" width="5%">月份</td>
			        </logic:notEqual>
			        <logic:notEqual name="staffForm" property="staff_week" value="-1">
			        <td class="TableRow" align="center" width="10%">周</td>
			        </logic:notEqual>
			        <logic:notEqual name="staffForm" property="staff_day" value="-1">
			        <td class="TableRow" align="center" width="10%">日</td>
			        </logic:notEqual>
			        <td class="TableRow" align="center" width="10%">单位</td>
			        <td class="TableRow" align="center" width="10%">部门</td>
			        <td class="TableRow" align="center" width="10%">姓名</td>
			        <td class="TableRow" align="center" width="5%">浏览</td>
  				</thead>
  				
  				<hrms:paginationdb id="element" name="staffForm" sql_str="${staffForm.strSelect}" table="" where_str="${staffForm.strWhere}"  order_by="${staffForm.strOrder}" columns="${staffForm.strColumns}" page_id="pagination" pagerows="${staffForm.pagerows}"  indexes="indexes">
			         <tr>
			         		<td align="center" class="RecordRow" nowrap>
					   			<hrms:checkmultibox name="staffForm" property="pagination.select" value="true" indexes="indexes"/>
						    </td>
						    <logic:notEqual name="staffForm" property="staff_year" value="">
						    <td align="left" class="RecordRow">
			                    &nbsp;${staffForm.staff_year}
			            	</td>
			            	</logic:notEqual>
			            	<logic:notEqual name="staffForm" property="staff_month" value="-1">
			            	<td align="left" class="RecordRow">
			                    &nbsp;${staffForm.staff_month}
			            	</td>
			            	</logic:notEqual>
			            	<logic:notEqual name="staffForm" property="staff_week" value="-1">
			            	<td align="left" class="RecordRow">
			                    &nbsp;${staffForm.staff_week_show}
			            	</td>
			            	</logic:notEqual>
			            	<logic:notEqual name="staffForm" property="staff_day" value="-1">
			            	<td align="left" class="RecordRow">
			                    &nbsp;${staffForm.staff_day_show}
			            	</td>	
			            	</logic:notEqual>
			         		<td align="left" class="RecordRow">&nbsp;
			                   <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>     
				        	<bean:write name="codeitem" property="codename" />
			            	</td>
			         		<td align="left" class="RecordRow">&nbsp;
			                    <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>     
				        		<bean:write name="codeitem" property="codename" />
			            	</td>
			            	<td align="left" class="RecordRow">
			                    &nbsp;<bean:write  name="element" property="a0101" />&nbsp;
			            	</td>
			            	<td align="center" class="RecordRow">
				            	<%
				            		if(month.equals("-1")){
				            	 %>
				            	<a href="javascript:navagation('<bean:write  name="element" property="a0100" />','<bean:write  name="element" property="nbase" />');"><img src="/images/view.gif" border=0></a>
				            	<%}else if(week.equals("-1")){ %>
				            	<a href="javascript:navagation('<bean:write  name="element" property="a0100" />','<bean:write  name="element" property="nbase" />');"><img src="/images/view.gif" border=0></a>
				            	<%}else if(day.equals("-1")){ %>
				            	<a href="javascript:navagation('<bean:write  name="element" property="a0100" />','<bean:write  name="element" property="nbase" />');"><img src="/images/view.gif" border=0></a>
				            	<%}else{ %>
				            	<a href="javascript:navagation('<bean:write  name="element" property="a0100" />','<bean:write  name="element" property="nbase" />');"><img src="/images/view.gif" border=0></a>
				            	<%} %>
				            	
			            	</td>
			          </tr>  
			        </hrms:paginationdb>
  		</table>
  				<table  width="89%" cellspacing="0"  align="center" cellpadding="0" class='RecordRowPer' >
					<tr>
					    <td align="left" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
								<bean:write name="pagination" property="current" filter="true" />
								<bean:message key="hmuster.label.paper"/>
								<bean:message key="hmuster.label.total"/>
								<bean:write name="pagination" property="count" filter="true" />
								<bean:message key="label.item"/>
								<bean:message key="hmuster.label.total"/>
								<bean:write name="pagination" property="pages" filter="true" />
								<bean:message key="hmuster.label.paper"/>
								
						</td>	
				        <td  align="right" nowrap class="tdFontcolor">
					        <hrms:paginationdblink name="staffForm" property="pagination" nameId="staffForm" scope="page">
							</hrms:paginationdblink>
						</td>
					</tr>
				</table>
				<div id="date_panel2" style="display:none;z-index:50">
					<select id="date_box" name="contenttype"  onblur="Element.hide('date_panel2');"  multiple="multiple"  style="width:138" size="6"  ondblclick="setSelectValue();">
			        </select>
			    </div>
    </html:form>
    
  </body>
</html>
<script language="javascript">
	function setSelectValue()
	{
		var temps=document.getElementById("date_box");
		
		for(i=0;i<temps.options.length;i++)
		{
		   
	        if(temps.options[i].selected)
	        { 
		    	document.getElementById("selectname").value=temps.options[i].text;
	            objectid = temps.options[i].value;
	            Element.hide('date_panel2');
		    }
		}
		
	} 
	function showDateSelectDiv(srcobj)
	{
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel2');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel2');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel2'))
	  {
        style.position="absolute";
        if(typeof(window.addEventListener)=="function")
        {
        	style.left=pos[0];
			style.top=pos[1]-date_desc.offsetHeight+42;
        }
        else
        {
        	style.posLeft=pos[0];
			style.posTop=pos[1]-date_desc.offsetHeight+42;
        }
		
		//alert(pos[1]);
		//alert(date_desc.offsetHeight);
		
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
    
	 
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.getElementById('selectname').value));
      hashVo.setValue("a0100",getEncodeStr(staffForm.a0100.value));
      hashVo.setValue("nbase",getEncodeStr(staffForm.nbase.value));
      hashVo.setValue("staff_year",getEncodeStr(staffForm.staff_year.value));
      hashVo.setValue("staff_month",getEncodeStr(staffForm.staff_month.value));
      hashVo.setValue("staff_week",getEncodeStr(staffForm.staff_week.value));
      hashVo.setValue("staff_day",getEncodeStr(staffForm.staff_day.value));
      
      var request=new Request({method:'post',asynchronous:false,onSuccess:shownamelist,functionId:'302001020616'},hashVo);
	}
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel2');
		}
		else{
		    if(namelist.length<6){
		      document.getElementById("date_box").size=namelist.length;
		    }else{
		      document.getElementById("date_box").size = 6;
		    }
			AjaxBind.bind(staffForm.contenttype,namelist);
		}
   }
   
	function navagation(a0100,nbase)
	{
				<%if(!staff_name.equals("")){%>
					var tmpa0101="<%=staff_name%>";
					tmpa0101=getEncodeStr(tmpa0101);
				<%}else{%>
					tmpa0101="";
				<%}%>
				
		<%if(month.equals("-1")){//自动定位到年报%>
				document.yearWorkForm.returnUrl.value="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=9&year=<bean:write  name="staffForm" property="staff_year" />&a0101="+tmpa0101;
				var year="<%=year%>";
				var str="/performance/nworkdiary/myworkdiary/yearwork.do?b_search=link&init=4&currentYear="+year+"&a0100="+a0100+"&nbase="+nbase;
				document.yearWorkForm.action=str;
				document.yearWorkForm.submit();
		<%}else if(week.equals("-1")){//自动定位到月报%>
				document.monthWorkForm.returnUrl.value="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=8&year=<bean:write  name="staffForm" property="staff_year" />&month=<bean:write  name="staffForm" property="staff_month" />&a0101="+tmpa0101;
				var year="<%=year%>";
				var month="<%=month%>";
				var str="/performance/nworkdiary/myworkdiary/monthwork.do?b_search=link&init=4&currentYear="+year+"&currentMonth="+month+"&a0100="+a0100+"&nbase="+nbase;
				document.monthWorkForm.action=str;
				document.monthWorkForm.submit();
		<%}else if(day.equals("-1")){ //自动定位到周报%>
				document.calendarWeekForm.staff_url.value="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=7&year=<bean:write  name="staffForm" property="staff_year" />&month=<bean:write  name="staffForm" property="staff_month" />&week=<bean:write  name="staffForm" property="staff_week" />&a0101="+tmpa0101;
				<%
					 String []arrayweek = week.split("/");
					 String []arrayday = arrayweek[0].split("-");
					 String temporayyear = arrayday[0];
					 String temporaymonth = arrayday[1];
					 String temporayday = arrayday[2];
				%>
				var year="<%=temporayyear%>";
				var month="<%=temporaymonth%>";
				var day="<%=temporayday%>";
				var str="/performance/nworkdiary/myworkdiary/weekwork.do?b_init=link&year="+year+"&month="+month+"&day="+day+"&a0100="+a0100+"&nbase="+nbase+"&frompage=1";
				document.calendarWeekForm.action=str;
				document.calendarWeekForm.submit();
		<%}else {//自动定位到日报%>
				var year="<%=year%>";
				var month="<%=month%>";
				var day="<%=day%>";
				document.calendarDayForm.staff_url.value="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=6&year="+year+"&month="+month+"&day=<bean:write  name="staffForm" property="staff_day" />&a0101="+tmpa0101;
				var str="/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&year="+year+"&month="+month+"&day="+day+"&a0100="+a0100+"&nbase="+nbase+"&frompage=4";
				document.calendarDayForm.action=str;
				document.calendarDayForm.submit();
		<%}%>
			
	}
	function changeYear()
	{
		document.staffForm.action="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=2";
		document.staffForm.submit();
	}
	function changeMonth()
	{
		document.staffForm.action="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=3";
		document.staffForm.submit();
	}
	function changeWeek()
	{
		document.staffForm.action="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=4";
		document.staffForm.submit();
	}
	function changeDay()
	{
		document.staffForm.action="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=5";
		document.staffForm.submit();
	}
	//提交
	function sub()
	{
		var obj = document.getElementById("staff_name");
		
		document.staffForm.action="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=10";
		document.staffForm.submit();
	}
	//如果是从部门进入员工日志，则还要返回。
	function returnDepart(a0100,nbase)
	{
		window.location.href="/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase;
	}
	//点击，则把所有的复选框全部选中
	function operateCheckBox(obj)
    {
   		var value=obj.checked;
   		for(var i=0;i<document.staffForm.elements.length;i++)
   		{
	   		
   			if(document.staffForm.elements[i].type=='checkbox')
   				document.staffForm.elements[i].checked=value;
   		
   		}

    }
    function executeChange()
    {
    	var objectid = "";
    	var temps = document.getElementById("objectbox");
        for(i=0;i<temps.options.length;i++)
		{
	        if(temps.options[i].selected)
	        { 
		    	document.getElementById("selectname").value=temps.options[i].text;
	            objectid = temps.options[i].value;
	            if(objectid)
	               break;
		    }
		}
		sub();
    }
</script>