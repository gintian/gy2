<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
				//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
			}
%>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
	
	function submit1(){	
		if(check()){
			searchReportUnitForm.action="/report/org_maintenance/addreportunit.do?b_save=link";
			searchReportUnitForm.submit();
		}else{
			searchReportUnitForm.addUnitCode.focus();
			return false;
		}
	}
	// 返回
	function goback()
	{
		searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_query=link&code=${searchReportUnitForm.codeFlag}&backdate=${searchReportUnitForm.backdate}";
		searchReportUnitForm.submit();	
	}
	
	function check(){
		var uc = searchReportUnitForm.addUnitCode.value;
		var n = ${searchReportUnitForm.mlen};
		
		if(n == "-1"){
			if(uc.length == 0){
				alert(REPORT_INFO34+"！");
				searchReportUnitForm.addUnitCode.focus();
				return false;
			}			
			var Letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" 
			for (i=0; i < uc.length; i++) {
				var CheckChar = uc.charAt(i); 
				CheckChar = CheckChar.toUpperCase();		  
				if (Letters.indexOf(CheckChar) == -1) { 
					alert (REPORT_INFO35+"！"); 
					return false; 
				} 
			} 
		}else{
			if(uc.length == 0){
				alert(REPORT_INFO34+"！");
				searchReportUnitForm.addUnitCode.focus();
				return false;
			}
			if(uc.length < n){
				alert(REPORT_INFO36+ n +REPORT_CHAR+"！");
				searchReportUnitForm.addUnitCode.focus();
				return false;
			}
			var Letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" 
			for (i=0; i < uc.length; i++) {
				var CheckChar = uc.charAt(i); 
				CheckChar = CheckChar.toUpperCase();
				if (Letters.indexOf(CheckChar) == -1) { 
					alert (REPORT_INFO35+"！"); 
					return false; 
				} 
			} 
			
		}
		if(trim(searchReportUnitForm.start_date.value).length==0||trim(searchReportUnitForm.end_date.value).length==0)
		{
			alert("需填写有效日期!");
			return;
		}
		
		//wangcq 2015-1-4 有效日期起不能大于有效日期止
		var arr=searchReportUnitForm.start_date.value.split("-");
		var starttime=new Date(arr[0],arr[1],arr[2]);
		var starttimes=starttime.getTime();
		var arr1=searchReportUnitForm.end_date.value.split("-");  
		var endtime=new Date(arr1[0],arr1[1],arr1[2]);
		var endtimes=endtime.getTime();   
		if(starttimes>endtimes){
		    alert(START_END_DATETIME_ERROR+"！");
		    return;
		}
		
		searchReportUnitForm.addUnitName.value=replaceAll( searchReportUnitForm.addUnitName.value,"'", "‘");
		
		//kangkai start delete 去掉下面语句以免刷新
		//parent.mil_menu.document.location = "reportunittree.jsp";  
		//kangkai  end
		
		return true;
	}
	
	function cc(){
		var e = event.srcElement; 
		var r =e.createTextRange(); 
		r.moveStart('character',e.value.length); 
		r.collapse(true); 
		r.select(); 
	}
	function load(){
		searchReportUnitForm.addUnitCode.focus();
	}
</script>


<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<body onLoad="load()">
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes/>
<form name="searchReportUnitForm" method="post" action="/report/org_maintenance/addreportunit.do"  >
	<br>
	<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				&nbsp;
				<bean:message key="addunitinfo.reportunit.title" />
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td -->
			<td align="left" colspan="4" class="TableRow"><bean:message key="addunitinfo.reportunit.title"/>&nbsp;</td>             	      
		</tr>
		<tr>
			<td colspan="4" class="framestyle3" width="100%" align="center">
				<table width="100%" border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" align="center">
					<tr align="center" class="list3" height="25px;">
						<td colspan="2">
							<br>
							<div align="center">
								${searchReportUnitForm.lenInfo}							
								<bean:write name="searchReportUnitForm" property="len" filter="false" />
								<br>
						</td>
					</tr>
					<tr align="right" class="list3" height="25px;">
						<td>
                            &nbsp;
							<bean:message key="addunitinfo.reportunit.parentcode" />
							&nbsp;
						</td>
						<td align="left">
							<html:text name="searchReportUnitForm" property="parentCode" readonly="true" styleClass="textColorRead" />
						</td>
					</tr>
					<tr align="right" class="list3" height="25px;">
						<td>
                            &nbsp;
							<bean:message key="addunitinfo.reportunit.unitcode" />
							&nbsp;
						</td>
						<td align="left">
							<html:text name="searchReportUnitForm" property="addUnitCode" styleClass="textColorWrite" maxlength="${searchReportUnitForm.len}" onfocus="cc()"/>
						</td>
					</tr>
					<tr align="right" class="list3" height="25px;">
						<td>
                            &nbsp;
							<bean:message key="addunitinfo.reportunit.codename" />
							&nbsp;
						</td>
						<td align="left">
							<html:text name="searchReportUnitForm" property="addUnitName" styleClass="textColorWrite" maxlength="50" />
						</td>
					</tr>
					
					<tr align="right" class="list3" height="25px;">
						<td>
                            &nbsp;
							<bean:message key="conlumn.codeitemid.start_date"/>
							&nbsp;
						</td>
						<td align="left">
						 <input type="text" name="start_date" value="${searchReportUnitForm.start_date}" maxlength="50" style="width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='${searchReportUnitForm.start_date}'; }"/>
						</td>
					</tr>
					
					<tr align="right" class="list3" height="25px;">
						<td>
                            &nbsp;
							<bean:message key="conlumn.codeitemid.end_date"/>
							&nbsp;
						</td>
						<td align="left">
							<input type="text" name="end_date" value="${searchReportUnitForm.end_date}" maxlength="50" style="width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='9999-12-31'; }"/>
						</td>
					</tr>
					
					
					
				</table>
				<Br>&nbsp;
			</td>
		</tr>
	</table>
	<table  align="center" >
		<tr class="list3">
						<td   align="center">
							  
							<input type="hidden" name="len" value="<bean:write name='searchReportUnitForm' property='len' filter='false'/> " />
							<input type="button" Class="mybutton" name="b_save"  value="<bean:message key='addunitinfo.reportunit.save'/>" 
							onClick="submit1()" onKeyDown="if (event.keyCode==13)  submit1();" />
 							
							<input type='button' id="button_goback" value='<bean:message key='button.return' />'
								onclick='goback();' class="mybutton">
						</td>
					</tr>
	</table>
</form>
</body>
