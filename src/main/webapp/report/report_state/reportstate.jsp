<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	int i=0;
	String userName = null;
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	userName=userView.getUserName();
	String unitcode=request.getParameter("code");
	session.removeAttribute("dmltab");
	session.setAttribute("dmltab","report_tname");
	
  
%>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript">
		function pf_ChangeFocus() 
		{
		   key = window.event.keyCode;
		   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
		   {
		   	window.event.keyCode=9;
		   }
		   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
		   if ( key==116)
		   {
		   	window.event.keyCode=0;	
			window.event.returnValue=false;
		   }   
		   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
		   {    
		        window.event.keyCode=0;	
			window.event.returnValue=false;
		   } 
		}
				
		function check(){
			var b = false;
			 for(var i=0;i<document.forms[0].elements.length;i++){			
				if(document.forms[0].elements[i].type=='checkbox'){	
					if(document.forms[0].elements[i].checked == true && document.forms[0].elements[i].name !="sfull"){
						b=true;
						return b;
					}
				}
			 }
			 if(b==false){
			 	alert(REPORT_INFO60+"！");
			 	return b;
			 }
		}
		
		function stateinit(){
			 var b = check();
			 if(b==true){
			  	if(confirm(REPORT_INFO74))
			  	{
			  		var isSub="false";
			  		if(confirm(REPORT_INFO61+"？")){
			  			isSub="true";
			  		}
			  		reportStateForm.action="/report/report_state/reportstate.do?b_init=init&isSub="+isSub;
			        reportStateForm.submit(); 
			  		
	       		 }	
			 }else{
			 	return;
			 }
		}
		
		function dbinit(flag){
			 var b = check();
			 if(b==true){
			  	if(confirm(REPORT_INFO62+"？")){
			  		var isSub="false";
			  		if(confirm(REPORT_INFO63+"？")){
			  			isSub="true";
			  		}
				 	reportStateForm.action="/report/report_state/reportstate.do?b_dbinit=dbinit&flag="+flag+"&isSub="+isSub;
			        reportStateForm.submit(); 
	       		
	       		 }	
			 }else{
			 	return;
			 }
		}
		
		//全选功能
		function full(){ 
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =reportStateForm.sfull.checked;
					}
				}
		}
		
		//报表状态检索
		function changeReportState(){
			var v = document.reportStateForm.reportStateSearchFlag.value;
			var c = document.reportStateForm.reportUnitCode.value;
			//alert(c);
			//alert(v);
			document.reportStateForm.action="/report/report_state/reportstate.do?b_query=link&code="+c+"&rssf="+v;
			document.reportStateForm.submit();
		}
		function refsh(){
			parent.refresh();
		}
		function setTime(){
			var thecodeurl="/report/report_state/reportstate2.do?b_set=init`unitcode=<%= unitcode%>";
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
            Ext.create("Ext.window.Window",{
				id:"setTimeWin",
				title:'设置报表属期',
				width:800,
				height:420,
				resizable:false,
				modal:true,
				autoScroll:false,
				autoShow:true,
				autoDestroy:true,
				renderTo:Ext.getBody(),
				html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
			});
		}
		function reportinfor(){
			var thecodeurl="/report/report_state/reportstate.do?b_info=init`unitcode=<%= unitcode%>";
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
            Ext.create("Ext.window.Window",{
                id:"sendInfoWin",
                title:'发送通知',
                width:800,
                height:500,
                resizable:false,
                modal:true,
                autoScroll:false,
                autoShow:true,
                autoDestroy:true,
                renderTo:Ext.getBody(),
                html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
            });
		}
</script>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<body>
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">

			<form name="reportStateForm" method="post" action="/report/report_state/reportstate.do" style="margin-top: 8px;">
				
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow">
				<thead>
					<tr  height="25" >
						<td colspan="5"  nowrap>
						<bean:message key="report_collect.lookupReportState"/>
							<select name="reportStateSearchFlag" onChange="changeReportState()" style="vertical-align: middle;">
								<OPTION value="-2"><bean:message key="task.state.all"/></OPTION>
								<OPTION value="-1"><bean:message key="edit_report.status.wt"/></OPTION>
								<OPTION value="0"><bean:message key="edit_report.status.zzbj"/></OPTION>
								<OPTION value="1"><bean:message key="edit_report.status.ysb"/></OPTION>
								<OPTION value="2"><bean:message key="edit_report.status.dh"/></OPTION>
								<OPTION value="3"><bean:message key="edit_report.status.fc"/></OPTION>
							</select>
						</td>
						<input type="hidden" name="reportUnitCode" value="${reportStateForm.reportUnitCode}">
					</tr>
					<tr>
						<td align="center" class="TableRow" nowrap width="5%">
							<input type="checkbox" name="sfull" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
						</td>
						<td align="center" class="TableRow" nowrap width="10%">
							<bean:message key="column.sys.org"/>&nbsp;
						</td>
						<td align="center" class="TableRow" nowrap width="40%">
							<bean:message key="report.reportlist.reportname"/>&nbsp;
						</td>
						<td align="center" class="TableRow" nowrap width="25%">
							<bean:message key="kq.class.explain"/>&nbsp;
						</td>
						<td align="center" class="TableRow" nowrap width="5%">
							<bean:message key="column.sys.status"/>&nbsp;
						</td>

					</tr>
					</thead>
					<hrms:paginationdb id="element" name="reportStateForm" sql_str="reportStateForm.sql_str" table="" where_str="reportStateForm.where_str" columns="id,unitname,name,description,a_status" page_id="pagination" pagerows="15" indexes="indexes">
					          <%
					          if(i%2==0)
					          {
					          %>
					          <tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");' >
					          <%}
					          else
					          {%>
					          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");' >
					          <%
					          }
					          i++;          
					          %>  
							<td align="center" class="RecordRow"  nowrap>
								<hrms:checkmultibox name="reportStateForm" property="pagination.select" value="true" indexes="indexes" />
							</td>
							<td align="left"  class="RecordRow" style="word-wrap:break-word; word-break:break-all;" nowrap>
								<bean:write name="element" property="unitname" filter="false" />
								&nbsp;
							</td>
							<td align="left"  class="RecordRow" width="" style="word-wrap:break-word; word-break:break-all;" nowrap>
								<bean:write name="element" property="name" filter="false" />
								&nbsp;
							</td>
							<td align="left"  class="RecordRow" style="word-wrap:break-word; word-break:break-all;" nowrap>
								<bean:write name="element" property="description" filter="false" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" style="word-wrap:break-word; word-break:break-all;" nowrap>
								&nbsp;<bean:write name='element' property='a_status' filter='false' />
								&nbsp;
							</td>

						</tr>
					</hrms:paginationdb>

				</table>

				<table width="100%"  class='RecordRowP'   align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<bean:message key="label.page.serial"/>
							<bean:write name="pagination" property="current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="pagination" property="count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="pagination" property="pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="reportStateForm" property="pagination" nameId="reportStateForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>

				<table width="75%" align="center">
					<tr>
						<td align="center" nowrap colspan="4">
							<hrms:priv func_id="2905201">
							<input type="button" name="b_set" value="<bean:message key="report.setTime"/>" class="mybutton" onclick="setTime()">
							</hrms:priv>
							<hrms:priv func_id="2905203">
							<input type="button" name="b_init" value="<bean:message key="report.stateinit"/>" class="mybutton" onclick="stateinit(),refsh()">
							</hrms:priv>
							<!-- 
							<input type="button" name="b_dbinit" value="<bean:message key="report.datainit"/>" class="mybutton" onclick="dbinit()">
							-->
							<hrms:priv func_id="2905204">
							<input type="button" name="b_dbinit" value="<bean:message key="report_collect.info2"/>" class="mybutton" onclick="dbinit(1)">
							</hrms:priv>
							<hrms:priv func_id="2905205">
							<input type="button" name="b_dbinit" value="<bean:message key="report_collect.info3"/>" class="mybutton" onclick="dbinit(2)">
							</hrms:priv>
							<hrms:priv func_id="2905202">
							<input type="button" name="b_info" value="发送通知" class="mybutton" onclick="reportinfor()">
							</hrms:priv>
							<hrms:tipwizardbutton flag="report" target="3" formname="reportStateForm"/>
						</td>
					</tr>
				</table>
			</form>

		</td>
	</tr>
</table>
</body>
<script language="javaScript">
<!--
	var flag = "${reportStateForm.reportStateSearchFlag}";
	if(flag ==""){
	}else{		
		for(var i = 0 ; i< document.reportStateForm.reportStateSearchFlag.options.length; i++){
			if(document.reportStateForm.reportStateSearchFlag.options[i].value==flag){
				document.reportStateForm.reportStateSearchFlag.options[i].selected = true;
			}
		}
	}
//-->
</script>