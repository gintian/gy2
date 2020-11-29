<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.options.SalaryInfoForm"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hjsj.hrms.transaction.sys.warn.ColumnBean" %>
<%@ page import="com.hjsj.hrms.actionform.sys.options.SalaryInfoForm" %>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<style type="text/css">
.trDeep3 {  
	background-color: #95CFF9; 
	}
</style>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript">dateFormat='yyyy-mm-dd'</script>
<%
	SalaryInfoForm sif = (SalaryInfoForm)session.getAttribute("salaryInfoForm");
	ArrayList columnList = sif.getColumnList();
	String salary=sif.getSalary();
	int k=0;
	int n =0;
	if(columnList == null){
	}else{n = columnList.size();}
	Calendar c = Calendar.getInstance();int year = c.get(Calendar.YEAR);int month = c.get(Calendar.MONTH);
%>
<style id="iframeCss">
	div{
		cursor:hand;font-size:12px;
	   }
	a{
	text-decoration:none;color:black;font-size:12px;
	}
	
	a.a1:active {
		color: #003100;
		text-decoration: none;
	}
	a.a1:hover {
		color: #FFCC00;
		text-decoration: none;
	}
	a.a1:visited {	
		text-decoration: none;
	}
	a.a1:link {
		color: #003100;
		text-decoration: none;
	}
</style>
<script type="text/javascript">
	function initdb(){
		var flag = "${salaryInfoForm.flag}";//操作标识
		var changeflag="${salaryInfoForm.changeflag}";
		if(changeflag=="0"||changeflag=="")
		  return false;
		for(var i = 0 ; i< document.salaryInfoForm.flag.options.length; i++){
			if(document.salaryInfoForm.flag.options[i].value == flag){
				document.salaryInfoForm.flag.options[i].selected = true;
			}
		}
		changeDiv(flag);
		
		if(flag == '1'){//年
			var yearflag = "${salaryInfoForm.yearFlag}";
			for(var i = 0 ; i< document.salaryInfoForm.year.options.length; i++){
				if(document.salaryInfoForm.year.options[i].value == yearflag){
					document.salaryInfoForm.year.options[i].selected = true;
				}
			}
		}else if(flag == '2'){//季度
			var yearflag = "${salaryInfoForm.yearFlag}";
			for(var i = 0 ; i< document.salaryInfoForm.year.options.length; i++){
				if(document.salaryInfoForm.year.options[i].value == yearflag){
					document.salaryInfoForm.year.options[i].selected = true;
				}
			}
			var quarterflag = "${salaryInfoForm.quarterFlag}";
			for(var i = 0 ; i< document.salaryInfoForm.quarter.options.length; i++){
				if(document.salaryInfoForm.quarter.options[i].value == quarterflag){
					document.salaryInfoForm.quarter.options[i].selected = true;
				}
			}
		}else if(flag == '3'){//月
			var yearflag = "${salaryInfoForm.yearFlag}";
			for(var i = 0 ; i< document.salaryInfoForm.year.options.length; i++){
				if(document.salaryInfoForm.year.options[i].value == yearflag){
					document.salaryInfoForm.year.options[i].selected = true;
				}
			}
			var monthflag = "${salaryInfoForm.monthFlag}";
			for(var i = 0 ; i< document.salaryInfoForm.month.options.length; i++){
				if(document.salaryInfoForm.month.options[i].value == monthflag){
					document.salaryInfoForm.month.options[i].selected = true;
				}
			}
			
		}else if(flag == '4'){//时间段
			
		}
	}
	
	function changeDiv(flag){
		if(flag == "3"){//月
			document.getElementById("month").style.display="inline";
			document.getElementById("quarter").style.display="none";
			document.getElementById("time").style.display="none";
		}else if(flag == "2"){//季度
			document.getElementById("quarter").style.display="inline";
			document.getElementById("month").style.display="none";
			document.getElementById("time").style.display="none";
		}else if(flag == "1"){//年
			document.getElementById("year").style.display="inline";
			document.getElementById("quarter").style.display="none";
			document.getElementById("month").style.display="none";
			document.getElementById("time").style.display="none";
		}else if(flag == "4"||flag == "5"){//时间段
			document.getElementById("time").style.display="inline";
			document.getElementById("quarter").style.display="none";
			document.getElementById("year").style.display="none";
			document.getElementById("month").style.display="none";
		}
	}
	
	function changeFlag(){
		var flag = document.salaryInfoForm.flag.value;
		changeDiv(flag);
		var a0100 = document.salaryInfoForm.a0100.value;
		var emppre = document.salaryInfoForm.empPre.value;
		
		var year = document.salaryInfoForm.year.value;
		var quarter = document.salaryInfoForm.quarter.value;
		var month = document.salaryInfoForm.month.value;
		var startDate = document.salaryInfoForm.startDate.value;
		var endDate =document.salaryInfoForm.endDate.value;
	        var query_field=document.salaryInfoForm.query_field.value;
		document.salaryInfoForm.action="/system/options/salaryinfo.do?b_changeflag=link&flag="+flag
			+"&a0100="+a0100+"&emppre="+emppre+"&year="+year+"&quarter="+quarter+"&month="+month
			+"&startdate="+startDate +"&enddate="+endDate+"&query_field="+query_field
			+"&fieldsetid=${salaryInfoForm.fieldsetid}&title="+$URL.encode("${salaryInfoForm.title}");
			
			document.salaryInfoForm.submit();
	}
	
	function changeYear(){
		changeFlag();
	}
	
	function changeQuarter(){
		changeFlag();
	}
	
	function changeMonth(){
		changeFlag();
	}
	
	function changeStartDate(){
		<logic:equal name="salaryInfoForm" property="isMobile" value="1">					
			var startDate = document.salaryInfoForm.startDate.value;
			var pattern=/^(19|20)\d{2}-(0[123456789]|1[012])-(0[123456789]|[12]\d|3[01])$/;
			if(!pattern.test(startDate)){
				alert('请输入正确的日期,如2011-01-30');
				return false;
			}
		</logic:equal>
		var b = checkDate();
		var a=check();	
		if(b){
			if(a)
		    {
		      changeFlag();
		    }else
		       return false;
		}else{
			alert("起始时间不能大于终止时间");
		}
	}
	
	function changeEndDate(){
		<logic:equal name="salaryInfoForm" property="isMobile" value="1">					
			var endDate =document.salaryInfoForm.endDate.value;	
			var pattern=/^(19|20)\d{2}-(0[123456789]|1[012])-(0[123456789]|[12]\d|3[01])$/;
			if(!pattern.test(endDate)){
				alert('请输入正确的日期,如2011-01-30');
				return false;
			}
		</logic:equal>
		var b = checkDate();
		var a=check();	
		if(b){
			if(a)
		    {
		      changeFlag();
		    }else
		       return false;
		}else{
			alert("起始时间不能大于终止时间");
		}
	}
	
	function changeDate(){ //时间段
		<logic:equal name="salaryInfoForm" property="isMobile" value="1">					
			var startDate = document.salaryInfoForm.startDate.value;
			var endDate =document.salaryInfoForm.endDate.value;	
			var pattern=/^(19|20)\d{2}-(0[123456789]|1[012])-(0[123456789]|[12]\d|3[01])$/;
			if(!pattern.test(startDate)||!pattern.test(endDate)){
				alert('请输入正确的日期,如2011-01-30');
				return false;
			}
		</logic:equal>
		var b = checkDate();
		var a=check();	
		if(b){
		    if(a)
		    {
		      changeFlag();
		    }else
		       return false;			
		}else{
			alert("起始时间不能大于终止时间");
			return false;	
		}
	}
	
	function checkDate(){
		var startDate = document.salaryInfoForm.startDate.value;
		var endDate =document.salaryInfoForm.endDate.value;		
		var sy = parseInt(startDate.substring(0,4),10);
		var ey = parseInt(endDate.substring(0,4),10);		
		if(sy > ey){
			return false;
		}else{
			if(sy == ey){//年相同 比较月份
				var sm = parseInt(startDate.substring(5,7),10);				
				var em = parseInt(endDate.substring(5,7),10);				
				if(sm > em){
					return false;
				}else{
					if(sm == em){//月相同 比较日期
						var sd = parseInt(startDate.substring(8,startDate.length),10);
						var ed = parseInt(endDate.substring(8,endDate.length),10);
						if(sd > ed){
							return false;
						}else{
							return true;
						}
					}else{
						return true;
					}
				}
			}else{
				return true;
			}
		}
		return true;
	}

	
	function back(){
		history.back();
		
		document.salaryInfoForm.target="mil_body";
		document.salaryInfoForm.action="/workbench/ykcard/showinfodata.jsp";
		document.salaryInfoForm.submit();
		
	}
	function query(salary)
	{
	   var waitInfo=eval("wait");	   
	   waitInfo.style.display="block";
	   var changeflag="${salaryInfoForm.changeflag}";
	   var flag="";
	   var year="";
	   var quarter ="";
	   var month ="";
	   var startDate="";
	   var endDate="";
	   if(changeflag!="0")
	   {
	     flag = document.salaryInfoForm.flag.value;
	     year = document.salaryInfoForm.year.value;
	     quarter = document.salaryInfoForm.quarter.value;
	     month = document.salaryInfoForm.month.value;
	     startDate = document.salaryInfoForm.startDate.value;
	     endDate =document.salaryInfoForm.endDate.value;
	   }
	   
	   document.salaryInfoForm.action="/system/options/salaryinfo.do?b_query=link&flag="+$URL.encode(flag)+"&salary="+$URL.encode(salary)+"&year="+$URL.encode(year)+"&quarter="+$URL.encode(quarter)+"&month="+$URL.encode(month)
	   +"&startdate="+$URL.encode(startDate) +"&enddate="+$URL.encode(endDate);
           document.salaryInfoForm.submit();
	}
	 function MusterInitData()
        {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
        }
        function InitQueryData()
        {
           <%
              if(salary==null||salary.length()<=0)
              {
                 ArrayList fieldsetlist=sif.getFieldSetList();
                 if(fieldsetlist!=null&&fieldsetlist.size()>0)
		 {
		     CommonData dataobj =(CommonData)fieldsetlist.get(0);
		     salary=dataobj.getDataValue();
		     %>	     
		     query("<%=salary%>");
		     <%	
		  }
              }
           %>
        } 
function check()
{
    var year_restrict_str="${salaryInfoForm.year_restrict}";
    if(year_restrict_str=="")
    	return true;    
	var obj=document.getElementById("flag");
	var flag="";
	if(obj)
	{
	   for(var i=0;i<obj.options.length;i++)
	   { 
	      if(obj.options[i].selected)
	       {flag=obj.options[i].value;break;}
	    }
	}	
	if(flag!='4')
	  {return true;}
	else{
	  var year_restrict=parseInt(year_restrict_str);
      var obj=document.getElementById("startDate");      
	  if(obj)
	  {
	    var value=obj.value;	    
		if(value.length>5)
		{
		   var year=value.substring(0,4);
		   var yI=parseInt(year);
		   if(yI<year_restrict){		   
		      alert("起始时间年不能小于"+year_restrict+"年");
		      return false;
		    }
		}
	  }
	  obj=document.getElementById("endDate");
	  if(obj)
	  {
	    var value=obj.value;
		if(value.length>5)
		{
		   var year=value.substring(0,4);
		   var yI=parseInt(year);
		   if(yI<year_restrict){		   
		      alert("结束时间年不能小于"+year_restrict+"年");
		      return false;
		    }
		}
	  }
	return true;   
  }
  return true;  
}
</script>
<!-- <body oncontextmenu="return false" ondragstart="return false" <logic:notEqual name="salaryInfoForm" property="isMobile" value="1">onselectstart ="return false"</logic:notEqual> onselect="document.selection.empty()" oncopy="document.selection.empty()" onbeforecopy="return false"onmouseup="document.selection.empty()">    -->
<!-- onmouseup事件使分页不能输入 -->
<body oncontextmenu="return false" ondragstart="return false" <logic:notEqual name="salaryInfoForm" property="isMobile" value="1">onselectstart ="return false"</logic:notEqual> onselect="document.selection.empty()" oncopy="document.selection.empty()" onbeforecopy="return false">
<form name="salaryInfoForm" method="post" action="">
	<input type="hidden" name="a0100" value="${salaryInfoForm.a0100}"/>
	<input type="hidden" name="empPre" value="${salaryInfoForm.empPre}"/>
	<input type="hidden" name="query_field" value="${salaryInfoForm.query_field}"/>
<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
 <tr>
   <td>
     <table width="100%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
		
		    <logic:notEqual name="salaryInfoForm" property="changeflag" value="0">
		<tr>
		   <td class="TableRow_lrt" nowrap colspan="<%= n %>">
		   <logic:equal name="salaryInfoForm" property="isMobile" value="1">
					<input type="button" value="返回" onClick="window.location.href='/phone-app/app/emolument.do?b_init=link&flag=infoself';" class="mybutton">
		   </logic:equal>
				统计方式
				<select name="flag" onchange="changeFlag()" >
					<option value="1">年</option>
					<option value="2">季度</option>
					<option value="3">月</option>
					<option value="4">时间段</option>
				</select>
				
				<div id="year" style="display:inline" >
					
					<hrms:optioncollection name="salaryInfoForm" property="yearlist" collection="list" />
					 <html:select name="salaryInfoForm" property="year" size="1" onchange="changeYear()">
                                         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                         </html:select>
					
					<bean:message key="datestyle.year"/>
				</div>
				
				<div id="quarter" style="display:none" >
					<select name="quarter" onchange="changeQuarter()">
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
					</select>季度
				</div>
				
				<div id="month" style="display:none">
					<select name="month" onchange="changeMonth()" >
					<%for(int j =1; j<=12; j++){if(j == month+1){%><option value="<%=j%>" selected><%=j%></option>
					<%}else{%><option value="<%=j%>" ><%=j%></option><%}}%>
					</select><bean:message key="datestyle.month"/>
				</div>

				<div id="time" style="display:none" >
					<input  type="text"  name="startDate"   <logic:notEqual name="salaryInfoForm" property="isMobile" value="1">readonly  extra="editor" dropDown="dropDownDate"  onchange="changeStartDate()"</logic:notEqual> value="${salaryInfoForm.startDateFlag}" >
					--
					<input  type="text" name="endDate"   <logic:notEqual name="salaryInfoForm" property="isMobile" value="1">readonly  extra="editor" dropDown="dropDownDate"  onchange="changeEndDate()"</logic:notEqual>  value="${salaryInfoForm.endDateFlag}">
					<input type="button" value="查询" onClick="return changeDate();" class="mybutton">
				</div>
		   </td>
		</tr>
				</logic:notEqual>
		<%  int tt=0;%>
		<logic:iterate id="element" name="salaryInfoForm"  property="fieldSetList" indexId="index"> 
		<%
		if(tt%5==0)
		{
		%>
		<tr>
		   <td class="TableRow" nowrap colspan="<%= n %>">
		<%	           
		}
		%>
		  <input type="button" value="${element.dataName}" onclick='javascript:query("${element.dataValue}");' class="mybutton">
		<%
		 if(tt%5==4)
		 {
		 %>
		    </td>
		  </tr>
		 <%
		 }
		 tt++;
		%>
		</logic:iterate>
     </table>
   </td>
 </tr>
 <tr>
  <td>
     <table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr>
			<logic:iterate id="ColumnBean" name="salaryInfoForm" property="showColumnList" indexId="index">
				<td class="TableRow" align="center" nowrap>
					&nbsp;<bean:write name="ColumnBean" property="columnName" filter="true" />&nbsp;
				</td>
			</logic:iterate>
		</tr>
		<hrms:extenditerate id="element" name="salaryInfoForm" 
		property="pageListForm.list" indexes="indexes" 
		pagination="pageListForm.pagination" pageCount="15" scope="session">
		<%
		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		if (k % 2 == 0) {%>
			<tr class="trShallow">
				<%} else {%>
			<tr class="trDeep">
			<%}k++;%>
		 	<%for(int i=0;i<columnList.size();i++){
   		   	 	ColumnBean cb = (ColumnBean)columnList.get(i);
   		   	 	String column = cb.getColumnName();
   		   	 	
   		   	 	if(cb.getColumnType().equalsIgnoreCase("M"))
   		   	 	{
   		   	 	 String tx=(String)abean.get(column);
   		   	    %>
   		   	    
   		   	    <hrms:showitemmemo showtext="showtext" itemtype="M" setname="" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
   		   	      <td align="left" class="RecordRow" ${tiptext} nowrap>
   		   	       ${showtext}&nbsp;
   		   	      </td>
   		   	 	<%
   		   	 	}else
   		   	 	{
   		   	 	   if(cb.getColumnType().equalsIgnoreCase("A"))
   		   	 	   {	
   		   	 	%>
   		   	 	      <td align="left" class="RecordRow"  nowrap>
   		   	 	   <%}else{%>
   		   	 	      <td align="right" class="RecordRow"  nowrap>
   		   	 	   <%}%>  
   		   	 	    &nbsp;<bean:write name="element" property="<%=column%>" filter="false" />&nbsp;
       		     	</td>	
   		   	 	<%}
   		   	 }
   		   	 %>
   		   	 	 
       		    	
   		   	
   		</tr>
		</hrms:extenditerate>
	</table>	
  </td>
 </tr>
 <tr>
  <td>
    <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				第
				<bean:write name="salaryInfoForm" property="pageListForm.pagination.current" filter="true" />
				页 共
				<bean:write name="salaryInfoForm" property="pageListForm.pagination.count" filter="true" />
				条 共
				<bean:write name="salaryInfoForm" property="pageListForm.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="salaryInfoForm" property="pageListForm.pagination" nameId="pageListForm">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
  </td>
 </tr>
</table> 
	
	
	
	<!-- 
	<table width="50%" align="center">
		<tr>
			<td align="center">
				<html:button styleClass="mybutton" property="bc_btn1" onclick="back()">
					<bean:message key="button.return" />
				</html:button>
			</td>
		</tr>
	</table>
	-->
</form>
<script type="text/javascript">
	initdb();
</script>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>

             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
</div> 
</body>
<script type="text/javascript">
 MusterInitData(); 	
 //InitQueryData();
</script>
<script>
if(!getBrowseVersion()){//兼容非IE浏览器 样式 bug 34718  wangb 20180208
	var ListTable = document.getElementsByClassName('ListTable')[2];//table 边线太粗
	ListTable.style.marginTop ='-1px';
}
</script>