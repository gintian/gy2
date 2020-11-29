<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
				 com.hrms.frame.dao.RecordVo,
				 com.hjsj.hrms.actionform.report.edit_report.PrintReportForm"%>
<%
   PrintReportForm printReportForm=(PrintReportForm)session.getAttribute("printReportForm");	
   UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
   String print =printReportForm.getPrint();  //  request.getParameter("print");
   String printFlg = request.getParameter("printFlg");
   String path = request.getParameter("path");
   String isCheck=printReportForm.getIsCheck();
   String a_print=printReportForm.getPrint();
   String rowPage="20";
   if((print!=null&&print.equals("1"))||(a_print!=null&&a_print.equals("1")))
   		rowPage="18";
   request.setAttribute("rowPage",rowPage);
   String url="/system/home.do?b_query=link";
   String target="i_body";
   String tar=userView.getBosflag();
   if(tar=="hl4")
  	  target="il_body";
   if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
   {
   		url="/templates/index/portal.do?b_query=link";
   		target="il_body";
   }
   //System.out.println("----------   " +path);
   int i=0;
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
		var print1 = "${printReportForm.print}";
		var checkflag = "${printReportForm.checkFlag}";
		var checkunitcode = "${printReportForm.checkUnitCode}";
		
		//alert(checkflag);
		
		//全选功能
		function full(){ 
			//alert(printReportForm.sfull.checked);
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =printReportForm.sfull.checked;
					}
				}
		}
		
		
		//报表类别改变后的数据联动
		function change()
		{
			printReportForm.action="/report/edit_report/printReport.do?b_query=link&sortId=" + printReportForm.sortId.value+"&reptype=1" ;
			printReportForm.submit();
		}
		

		
		
	

		
	
	
		
		
	
		function print() {
			var obj1=eval("printReportForm.b_print1");
		//	var obj2=eval("printReportForm.b_print2");
			//obj1.disabled=true;
			//obj2.disabled=true;
		//    printReportForm.action="/report/edit_report/reportPrint.do?b_query=link&exportFashion="+flag;
		//    printReportForm.target="_blank";
		//	printReportForm.submit();
			var tableids ='';
			var a=0;
			var b=0;
			var selectid=new Array();
			var a_IDs=eval("document.forms[0].IDs");	
			var nums=0;		
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
		   		{		   			
		   			nums++;
		   		}
			}
			if(nums>1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			   		{	
						if(document.forms[0].elements[i].name!='selbox'&&document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs[b].value;						
						}
						b++;
					}
				}
			}
			if(nums==1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			   		{	
						if(document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs.value;						
						}
					}
				}
			}
				if(parent.mil_menu.root.getSelected()=="")
		{
				alert("请选择单位！");
				return;
		}	
		var temp_str=parent.mil_menu.root.getSelected();
				for(var i=0;i<selectid.length;i++){
			tableids+=selectid[i]+",";
			}
			if(selectid.length==0)
			{
				alert("请选择报表！");
				
				obj1.disabled=false;
				return ;
			}
			if(tableids.length>0){
			tableids =tableids.substring(0,tableids.length-1);
			}	
			if(temp_str.length>0){
			temp_str =temp_str.substring(0,temp_str.length-1);
			}	
			 var menu_vo = new Object();
		    menu_vo.menu_unit = temp_str;
		    menu_vo.menu_table = tableids;
		    parent.parent.window.returnValue=menu_vo;
		   window.close();
		
		
			
		}
		
		
		
		
		
		function goback()
		{
			
			window.close();
		}
		
		document.body.focus();
		
	</script>
<style>
	.fixedtab 
	{ 
		overflow:auto; 
		height:310;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
	    BORDER-LEFT: #94B6E6 1pt solid; 
	    BORDER-RIGHT: #94B6E6 1pt solid; 
	    BORDER-TOP: #94B6E6 1pt solid ; 	
	}
</style>
<hrms:themes/>
<form name="printReportForm" method="post" action="/report/edit_report/printReport.do" style="margin-left: 4px;margin-top: 5px;">
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="report.reportlist.reportqushu"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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

<div class='fixedtab' style='border-left:none;width:533px; height:400px; margin-top: 4px;overflow:auto;'>
	<table width="100%" style="border-top:none;border-bottom:none;" border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow">
			<tr height="25">
				<td colspan="4" >
					<bean:message key="report.reportlist.reportsort" />
					<hrms:importgeneraldata showColumn="name" valueColumn="tsortid" flag="true" paraValue="" sql="printReportForm.dbsql" collection="list" scope="page" />
					<html:select name="printReportForm" property="sortId" size="1" onchange="javascript:change()">
						<html:option value="-1">
							<bean:message key="report.reportlist.fullreport" />
						</html:option>
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td align="center" class="TableRow" nowrap width="10%">
            <input type="checkbox" name="selbox" onclick="batch_select(this,'printReportForm.select');" title='<bean:message key="label.query.selectall"/>'>
				</td>
				<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="report.reportlist.reportid" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap width="60%">
					<bean:message key="report.reportlist.reportname" />
					&nbsp;
				</td>
				
			</tr>
		<hrms:extenditerate id="element" name="printReportForm" property="printReportForm.list" indexes="indexes" pagination="printReportForm.pagination" pageCount="1000" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
				<td align="center" class="RecordRow" nowrap>
				<% if(print!=null&&print.equals("5")&&isCheck.equalsIgnoreCase("hidden")){
						RecordVo vo=(RecordVo)pageContext.getAttribute("element");
						int status=vo.getInt("paper");
						if(status==-1||status==0||status==2)
						{
						%>
						<hrms:checkmultibox name="printReportForm" property="printReportForm.select" value="true" indexes="indexes" />	
						<% 
						}
						else
						{
						%>
						<input type="checkbox"  disabled='false'  name="printReportForm.select[${indexes}]" value="true">	
						<%
						}
					}
					else
					{
				 %>
					<hrms:checkmultibox name="printReportForm" property="printReportForm.select" value="true" indexes="indexes" />	
				<%  } %>
				</td>

				<td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="string(tabid)" filter="false" />
					&nbsp;
					<input type="hidden" name="IDs" value="<bean:write name="element" property="string(tabid)" filter="false"/>" />

				</td>
				<td align="left" class="RecordRow" wrap>
					<bean:write name="element" property="string(name)" filter="false" />
					&nbsp;
				</td>
				
			</tr>
		</hrms:extenditerate>

	</table>
</div>	
<table style='width:520px;margin-top: 1px;' align="center">
		<tr>
			<td align="center">
					<input type="button" name="b_print1" value="<bean:message key="button.ok"/>" class="mybutton" onclick="javascript:print()">
					<input type="button"  class="mybutton" value="<bean:message key="reportcheck.return"/>"  onclick='goback()' style="margin-left: -3px;" >
			</td>
		</tr>
	</table>
</form>

