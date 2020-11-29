<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
		
		//全选功能
		function full(){ 
			//alert(reportListForm.sfull.checked);
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =reportListForm.sfull.checked;
					}
				}
		}
		
		
		//报表类别改变后的数据联动
		function change()
		{
			reportListForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=" + reportListForm.sortId.value ;
			reportListForm.submit();
		}
		
		function reportInnerCheck()
		{
			reportListForm.action="/report/auto_fill_report/reportinnercheckresult.do?b_reportinnercheck=link";
			reportListForm.submit();
		}
		function reportSpaceCheck()
		{
			reportListForm.action="/report/auto_fill_report/reportspacecheckresult.do?b_reportspacecheck=link";
			reportListForm.submit();
		}
		
		function field_Result()
		{
			reportListForm.action="/report/auto_fill_report/field_result.do?b_field_result=link";
			reportListForm.submit();
		}
		
		function expr_Result()
		{
			reportListForm.action="/report/auto_fill_report/expr_result.do?b_expr_result=link";
			reportListForm.submit();
		}
		
		function returnInfo(outparamters)
		{
			//info 0:成功 1:指标没有构库  2.插入数据出错  3.批量取数错误			
			var info=outparamters.getValue("info");
			
			var waitInfo=eval("wait");			
			waitInfo.style.display="none";
			alert(info);
		}
		
		
		
		
		function batchGetData()
		{		
			
			var a=0;
			var b=0;
			var selectid=new Array();
			var a_IDs=eval("document.forms[0].IDs");	
			var nums=0;		
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='sfull')
		   		{		   			
		   			nums++;
		   		}
			}
			if(nums>1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='sfull')
			   		{	
						if(document.forms[0].elements[i].name!='sfull'&&document.forms[0].elements[i].checked==true)
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
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='sfull')
			   		{	
						if(document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs.value;						
						}
					}
				}
			}
			
			if(selectid.length==0)
			{
				alert(REPORT_INFO9+"！");
				return ;
			}	
			var waitInfo=eval("wait");
			waitInfo.style.display="block";
			
		
			var hashvo=new ParameterSet();
			hashvo.setValue("selectid",selectid);			
			var In_paramters="flag=1"; 		
			
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'03010000002'},hashvo);
	
		}
		
		
		
		
		
	</script>

<form name="reportListForm" method="post" action="/report/auto_fill_report/reportlist.do">

<div id='wait' style='position:absolute;top:200;left:250;display:none;'   >
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style"  height=24><bean:message key="report.reportlist.reportqushu" />......</td>
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

	<br>

	<table width="65%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">

		<thead>
			<tr>
				<td colspan="4" class="TableRow">
					<bean:message key="report.reportlist.reportsort" />
					：
					<hrms:importgeneraldata showColumn="name" valueColumn="tsortid" flag="true" paraValue="" sql="reportListForm.dbsql" collection="list" scope="page" />

					<html:select name="reportListForm" property="sortId" size="1" onchange="javascript:change()">
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
					<bean:message key="report.reportlist.select" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="report.reportlist.reportid" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap width="60%">
					<bean:message key="report.reportlist.reportname" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap width="15%">
					<bean:message key="report.reportlist.reportquery" />
					&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="reportListForm" property="reportListForm.list" indexes="indexes" pagination="reportListForm.pagination" pageCount="20" scope="session">

			<tr class="trShallow">
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="reportListForm" property="reportListForm.select" value="true" indexes="indexes" />
					&nbsp;
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
				<td align="center" class="RecordRow" nowrap>
				<hrms:priv func_id="290105">
					<a href="/report/edit_report/reportSettree.do?b_query2=query&operateObject=1&operates=1&code=<bean:write name="element" property="string(tabid)" filter="false"/>&status=1"><img src="../../images/edit.gif" border=0></a>
				</hrms:priv>
				&nbsp;
				</td>
			</tr>
		</hrms:extenditerate>

	</table>

	<table width="62%" align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
			<input   type="checkbox"   name="sfull"  onclick="javascript:full();"><bean:message key="label.query.selectall" />
				<bean:message key="label.page.serial" />
				<bean:write name="reportListForm" property="reportListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="reportListForm" property="reportListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="reportListForm" property="reportListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="reportListForm" property="reportListForm.pagination" nameId="reportListForm">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>

	<table width="50%" align="center">
		<tr>
			<td align="center">
				<hrms:priv func_id="290100">
				<input type="button" name="b_add" value="<bean:message key="reportlist.reportplqs"/>" class="mybutton" onclick="batchGetData()">
				</hrms:priv>
				<hrms:priv func_id="290101">
				<input type="button" name="b_expr_result" value="<bean:message key="reportlist.expranalyse"/>" class="mybutton" onclick="javascript:expr_Result()">
				</hrms:priv>
				<hrms:priv func_id="290102">
				<input type="button" name="b_field_result" value="<bean:message key="reportlist.fieldanalyse"/>" class="mybutton" onclick="javascript:field_Result()">
				</hrms:priv>
				<hrms:priv func_id="290103">
				<input type="button" name="b_reportinnercheck" value="<bean:message key="reportlist.reportinnercheck"/>" class="mybutton" onclick="javascript:reportInnerCheck()">
				</hrms:priv>
				<hrms:priv func_id="290104">
				<input type="button" name="b_reportspacecheck" value="<bean:message key="reportlist.reportspacecheck"/>" class="mybutton" onClick="javascript:reportSpaceCheck()">
				</hrms:priv>
			</td>
		</tr>
	</table>
</form>

