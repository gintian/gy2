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
		function del()
{
		var num=0;
		for(var i=0;i<document.functionMainForm.elements.length;i++)
  		{
  			if(document.functionMainForm.elements[i].type=='checkbox'&&document.functionMainForm.elements[i].name!='selbox')
  			{
  				if(document.functionMainForm.elements[i].checked==true)
  				{
  				
  					num++;
  				}
  			}
  		}
  		if(num==0)
  		{
  			alert("请选择需要删除的功能！");
  		    return;
  		}
		
		if(confirm("你真的要删除选中功能?"))
		{
			document.functionMainForm.action="/system/bos/func/functionMain.do?b_delFunc=del";
			document.functionMainForm.submit();
		}
}
function rename()
{
		var num=0;
		var selectIndex=0;
		var checkNum=0;
		var salaryid;
		for(var i=0;i<document.functionMainForm.elements.length;i++)
  		{
  			if(document.functionMainForm.elements[i].type=='checkbox'&&document.functionMainForm.elements[i].name!='selbox')
  			{
  				if(document.functionMainForm.elements[i].checked==true)
  				{
  					selectIndex=checkNum;
  					num++;
  				}
  				checkNum++;
  			}
  		}
  		if(num==0)
  		{
  			alert("请选择要修改的功能！");
  		    return;
  		}
		
		if(num>1)
		{
  			alert("每次只能修改一个功能！");
  		    return;
  		}
		
		
		var functionid=eval("document.functionMainForm.codeitemid");
		
		if(checkNum==1)
		{
			
			functionid=functionid.value;

		
		}
		else
		{
			functionid=functionid[selectIndex].value;
		}
		 var theurl="/system/bos/func/functionMain.do?b_editFunc=new`functionid="+functionid;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:500px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		if(retvo!=null&&retvo!='undefined')
  		{
  			parent.mil_menu.alterTreeNode(retvo)
  		}
}
		function copy(){
		window.location.href="/servlet/DownLoadFunction";
		}
		</script>
	</HEAD>
	
	<html:form action="/system/bos/func/functionMain">
	<html:hidden name="functionMainForm" property="parentid"/>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable" style="margin-top:2px;">
			<thead>
				<tr>
					<!--td align="center" class="TableRow" nowrap>
						<input type="checkbox" name="selbox"
							onclick="batch_select(this,'pagination.select');"
							title='<bean:message key="label.query.selectall"/>'>
					</td-->
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.func.main.id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.func.main.name" />
						&nbsp;
					</td>

				</tr>
			</thead>
				<hrms:extenditerate id="element" name="functionMainForm" property="functionMainForm.list" indexes="indexes" pagination="functionMainForm.pagination" pageCount="${functionMainForm.pagerows}" scope="session">
				
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

					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all">

						&nbsp;<bean:write name="element" property="codeitemid" filter="true" />
						&nbsp;

					</td>

					<td align="left" class="RecordRow" nowrap width="300"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemdesc" filter="true" />
						&nbsp;

					</td>

				</tr>
			</hrms:extenditerate>
		<tr>	
		<td colspan="2">
		<table width="100%"align="center" class="RecordRowP">
		<tr>
			<td valign="bottom"  class="tdFontcolor"  nowrap>
		    	<hrms:paginationtag name="functionMainForm" pagerows="${functionMainForm.pagerows}" property="functionMainForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap="nowrap" class="tdFontcolor" >
				<p align="right">
					<hrms:paginationlink name="functionMainForm" property="functionMainForm.pagination" nameId="functionMainForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	</td></tr>	
	</table>
	</html:form>
	<script>



</script>
</HTML>
