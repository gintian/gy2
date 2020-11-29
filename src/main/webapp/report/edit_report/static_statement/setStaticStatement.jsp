<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<%
	int i = 0;
%>
<HTML>
	<HEAD>
		<TITLE></TITLE>
		<script LANGUAGE=javascript src="/js/function.js"></script>
		<script LANGUAGE=javascript src="/js/validate.js"></script>
		<script LANGUAGE=javascript src="/system/bos/func/funcment.js"></script>
		<script type="text/javascript">
	function returnorg(outparamters){
	var flag=outparamters.getValue("flag"); 
		var ret_vo=select_org_emp_dialog_report(0,1,0,1,0,1,0,flag);
		var scopeunitsids="${staticStatementForm.scopeunitsids}"
		if(ret_vo)
		{
			var	re=/,/g;
			var tmp=ret_vo.content;
			var str=tmp.replace(re,"`");	
			var hashvo=new ParameterSet();
			hashvo.setValue("scopeunitids",str);
			hashvo.setValue("scopeid","${staticStatementForm.scopeid}");			
			var In_paramters="flag=1"; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000053'},hashvo);
		
		}
	}
	function select_org_emp_dialog_report(flag,selecttype,dbtype,priv,isfilter,loadtype,level,viewunit)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"&selecttype="+selecttype+"&dbtype="+dbtype+
                "&priv="+priv + "&isfilter=" + isfilter+"&loadtype="+loadtype+"&level="+level+"&viewunit="+viewunit;
     var return_vo= window.showModalDialog(theurl,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
   function returnInfo(outparamters){
 	var info=outparamters.getValue("info");
	  	if(info=="ok")
	  	{
   				staticStatementForm.action="/report/edit_report/editReport/staticStatement.do?b_queryStatic=init&scopeid="+"${staticStatementForm.scopeid}";
	       		staticStatementForm.submit(); 
	  	}else{
	  	alert(SAVEFAILED+"!");
	  	}
  }	
  function addunits1(){
		var hashvo=new ParameterSet();
				var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnorg,functionId:'03020000073'},hashvo);
	
		      	
		}
		function delete1(){
			var n=0;
				for(var i=0;i<document.staticStatementForm.elements.length;i++)
	   		{
	   			if(document.staticStatementForm.elements[i].type=='checkbox'&&document.staticStatementForm.elements[i].checked&&document.staticStatementForm.elements[i].name!='selbox')
	   			{
	   				n++;
	   			}
	   		}
	   		if(n==0)
	   		{
	   			alert("请选择需删除的统计单位!");
	   			return;
	   		}
	   		 if(confirm("您确定要删除选中的记录?"))
      		 {
       	 	staticStatementForm.action="/report/edit_report/editReport/staticStatement.do?b_delete=del&scopeid="+"${staticStatementForm.scopeid}";
       	 	staticStatementForm.submit(); 
       		 }
		}
		function changeItem1(position,method){
			if(method=="up"){
				if(position==1){
					alert("已经是第一条记录,不允许上移！");
					return;
				}
			}
			var count=${staticStatementForm.count};
			if(method=="down"){
				if(position==count){
					
					alert("已经是最后一条记录,不允许下移！");
					return;
				}
			}
			document.staticStatementForm.position.value=position;
			document.staticStatementForm.method.value=method;
			staticStatementForm.action="/report/edit_report/editReport/staticStatement.do?b_change=change&scopeid="+"${staticStatementForm.scopeid}"+"&method="+method+"&position="+position;
			staticStatementForm.submit();
		}
		
		</script>
	</HEAD>	
	<style>
	#tbl-container {
			 
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		BORDER-right: #94B6E6 1pt solid;
		margin-left:5px;
		overflow:auto;
		height:500px;
		width:99% 	
		
	}	
</style>
	<html:form action="/report/edit_report/editReport/staticStatement">
		<div id="tbl-container">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
			<tr >
			<td align="left"  nowrap colspan="4" class="TableRow" style="border-left: none;border-right: none;border-top: none;">
			所属机构：<bean:write name="staticStatementForm" property="scopeownerunit" filter="true"/>
				<html:hidden name="staticStatementForm" property="scopeownerunitid"/>
			</td>
			</tr>
				<tr>
					<td align="center" class="RecordRow" width="10%" nowrap style="border-left: none;">
						<input type="checkbox" name="selbox"
							onclick="batch_select(this,'staticStatementForm.select');"
							title='<bean:message key="label.query.selectall"/>'>
					</td>
					<td align="center" class="RecordRow" width="40%"  nowrap>
						机构编码
						&nbsp;
					</td>
					<td align="center" class="RecordRow"  width="40%" nowrap>
						统计单位
						&nbsp;
					</td>
					<td align="center" class="RecordRow" width="10%" nowrap style="border-right: none;">
						排序
						&nbsp;
					</td>
				</tr>
			</thead>
				<hrms:extenditerate id="element" name="staticStatementForm" property="staticStatementForm.list" indexes="indexes" pagination="staticStatementForm.pagination" pageCount="${staticStatementForm.pagerows}" scope="session">
				<%
					if (i % 2 == 0) {
				%>
				<tr class="trShallow">
					<%
						} else {
					%>				
				<tr class="trDeep">
					<%
						}
									i++;
					%>
					<td align="center" class="RecordRow" nowrap width="10%"
						style="word-break: break-all;border-left: none;">
						<hrms:checkmultibox name="staticStatementForm" property="staticStatementForm.select" value="true" indexes="indexes"/>
					</td>
					<td align="left" class="RecordRow" nowrap width="40%"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="unitcode" filter="true" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap width="40%"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="units" filter="true" />
						&nbsp;
					</td>
					<td align="center" class="RecordRow" nowrap width="10%" style="word-break: break-all;border-right: none;">
					&nbsp;<a href="javaScript:changeItem1('<%=i%>','up')">
					<img src="../../../images/up01.gif"  border=0></a> 
					&nbsp;<a href="javaScript:changeItem1('<%=i%>','down')">
					<img src="../../../images/down01.gif"  border=0></a> 
					</td>
				</tr>
				<html:hidden name="staticStatementForm" property="scopeunitsids"/>  
               <html:hidden name="staticStatementForm" property="scopename"/>
               <html:hidden name="staticStatementForm" property="position"/>
               <html:hidden name="staticStatementForm" property="method"/>
			</hrms:extenditerate>
		</table>
	</div>
		<table width="70%"align="center" >
			<tr>
			<td align="center" nowrap colspan="2" >
			<input	type="button" name="add" value="<bean:message key='orglist.reportunitlist.addreportunit'/> "  class="mybutton"  onClick="addunits1()" >
			<input type="button" name="delete" value="<bean:message key='ortlist.reportunitlist.delete'/>"   class="mybutton"  onClick="delete1()" >
			</td>
			</tr>
		</table>
	</html:form>

</HTML>
