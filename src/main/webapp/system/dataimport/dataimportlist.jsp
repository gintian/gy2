<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.dataimport.DataImportForm"%>
<script language="javascript" src="/js/dict.js"></script>
<style>
body{text-align: center;padding-left: 5px;}
.tbl-container
{  
	overflow:auto; 
	height:100%;
	width:100%; 
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
}
.t_cell_locked 
{
	border: inset 1px #C4D8EE;
	BACKGROUND-COLOR: #ffffff;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-size: 12px;
	border-collapse:collapse; 
	
	background-position : center left;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;
	
}
.t_cell_locked_b {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-size: 12px;
	border-collapse:collapse; 
}

.t_header_locked
{
	/*background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	background-color:#f4f7f7;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	valign:middle;
	font-weight: bold;	
	text-align:center;
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 15;
}
	 		
.t_cell_locked2 
{
	/*  background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	background-color:#f4f7f7;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 20;
	
}
</style>
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
	// 编辑
	function edit(id) {
		var form1 = document.getElementById("form1");
		form1.action = "/sys/import/dataimport.do?b_addlink=link&opt=editlink&id=" + id;
		form1.submit();
	}
	
	// 新增
	function add() {
		var form1 = document.getElementById("form1");
		form1.action = "/sys/import/dataimport.do?b_addlink=link&encryptParam=<%=PubFunc.encrypt("opt=addlink")%>";
		form1.submit();
	}
	
	// 删除
	function del() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		} else {
			if (confirm(CONFIRMATION_DEL)) {
				var form1 = document.getElementById("form1");
				form1.action = "/sys/import/dataimport.do?b_del=link&opt=del&id=" + getSelectId();
				form1.submit();
			}
		}
	}
	
	function checkSelect() {
		check = false;
		input = document.getElementsByTagName("input");
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].checked==true && input[i].name != "selbox") {
				check =true;
				break;
			} 	
		}
		return check;
	}
	
	function getSelectId() {
		check = false;
		input = document.getElementsByTagName("input");
		var str = "";
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].checked==true && input[i].name != "selbox") {
				str += input[i].value + ",";
			} 	
		}
		return str;
	}
	
	// 导入数据
	function importData() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		} else {
			var hashvo=new ParameterSet();
	    	hashvo.setValue("id", getSelectId());
	    	
	    	var request=new Request({method:'post',asynchronous:true,onSuccess:showdownloadfile,functionId:'1010100139'},hashvo);
	    	//Rpc({functionId:'1010100139',success:showdownloadfile},hashvo);
	    	document.getElementById("btnImport").disabled="true";
	    	document.getElementById("wait").style.display="block";
		}
	}
	
	function showdownloadfile(outparamters) {
		document.getElementById("wait").style.display="none";
		var fileStr = outparamters.getValue("flag");
		document.getElementById("wait").style.display="none";
		if ("true" == fileStr) {
			alert("导入成功！");
		} else {
			alert("导入失败");
		}
		document.getElementById("btnImport").removeAttribute("disabled");
	}
	
	function submitUpOrder(id)
	{
		var form1 = document.getElementById("form1");
        form1.action = "/sys/import/dataimport.do?b_order=link&opt=up&id=" + id;
        form1.submit();
	}
	
	function submitDownOrder(id)
	{
		var form1 = document.getElementById("form1");
        form1.action = "/sys/import/dataimport.do?b_order=link&opt=down&id=" + id;
        form1.submit();		
	}
//-->
</script>

<html:form action="/sys/import/dataimport" method="post" styleId="form1">
	
	<%
		int i = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0" width="80%" align="center">
	<tr>
	<td height="450px;">
	<div class="tbl-container" id="tbl-container">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable" style="border-left:none;border-right:none;">
		<thead>
			<tr><!-- 全选 -->
				<td align="center" class="TableRow" style="border-left-width: 0px;border-top:none;" width="5%">
					<input type="checkbox" name="selbox" onclick="batch_select(this,'chek');" title='<bean:message key="label.query.selectall"/>' />
				</td>
				<td align="center" class="TableRow" nowrap width="20%" style="border-top:none;">
						<bean:message key="column.name"/>
				</td>
				<td align="center" class="TableRow" nowrap width="10%" style="border-top:none;">
						<bean:message key="config.sys.info.dbtype"/>
				</td>
				<td align="center" class="TableRow" nowrap width="30%" style="border-top:none;">
						<bean:message key="config.sys.info.dburl"/>
				</td>
				<td align="center" class="TableRow" nowrap width="10%" style="border-top:none;">
						<bean:message key="sys.export.jobclass"/>
				</td>
				<td align="center" class="TableRow" nowrap width="10%" style="border-top:none;">
						<bean:message key="sys.ortions.param.startup"/>
				</td>
				<td align="center" class="TableRow" nowrap width="10%" style="border-top:none;">
						<bean:message key="label.commend.edit"/>
				</td>
				<td align="center" class="TableRow" nowrap width="5%" style="border-right-width: 0px;border-top:none;">
				        <bean:message key="train.examplan.order"/>
				</td>
			</tr>
		</thead>
		<bean:define id="maxOrder" name="dataImportForm" property="maxOrder"></bean:define>
		<hrms:extenditerate id="element2" name="dataImportForm" property="dataImportForm.list" indexes="indexes"  pagination="dataImportForm.pagination" pageCount="21" scope="session">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onclick="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" onclick="javascript:tr_onclick(this,'E4F2FC')">
				<%
					}								
				%>
				<bean:define id="id" name="element2" property="id"></bean:define>
				<td align="center" class="RecordRow" nowrap style="border-left-width: 0px;">
					<logic:equal name="element2" property="enable" value="1">
					&nbsp;<input type="checkbox" name="chek" id="chek_${id}" value='${id}' />&nbsp;<!-- title='${id}' -->
					</logic:equal>	
					<logic:notEqual name="element2" property="enable" value="1">
					&nbsp;<input type="checkbox" name="chek" id="chek_${id}" value='${id}' disabled="disabled"/>&nbsp;<!-- title='${id}' -->
					</logic:notEqual>	
										
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element2" property="name"/>&nbsp;				
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element2" property="dbtype"/>&nbsp;				
				</td>
				
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element2" property="dbtype"/>&nbsp;				
				</td>
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element2" property="jobclass"/>&nbsp;				
				</td>
				<td align="center" class="RecordRow" nowrap>
					<logic:equal name="element2" property="enable" value="1">
					&nbsp;<bean:message key="system.sms.ywable2"/>&nbsp;
					</logic:equal>	
					<logic:notEqual name="element2" property="enable" value="1">
					&nbsp;<bean:message key="system.sms.ywdisable"/>&nbsp;
					</logic:notEqual>	
					<input type="hidden" name="isenable" id="isenable_${id }" value="<bean:write name="element2" property="enable"/>">	
				</td>
				
				<td align="center" class="RecordRow" nowrap>
					<a href="###" onclick="edit('${id }')"><IMG alt="<bean:message key="label.commend.edit"/>" src="/images/edit.gif" border="0"/></a>
				</td>	
				
				<td align="center" class="RecordRow" nowrap width="5%" style="border-right-width: 0px;">
                        <% if(i>0){ %>            
                       <a href="###" onclick="submitUpOrder('<bean:write name="element2" property="id" filter="true"/>');">
                            <img src="/images/up01.gif" alt="<bean:message key="button.previous" />" width="12" height="17" border=0>
                       </a> 
                       <% }else {%>
                         &nbsp;&nbsp;
                       <% } %>
                       
                       <logic:notEqual name="element2" property="id" value="${maxOrder}">     
                         <a href="###" onclick="submitDownOrder('<bean:write name="element2" property="id" filter="true"/>');">
                          <img src="/images/down01.gif" alt="<bean:message key="button.next" />" width="12" height="17" border=0>
                         </a>
                       </logic:notEqual>
                       <logic:equal name="element2" property="id" value="${maxOrder}">                  
                         &nbsp;&nbsp;
                       </logic:equal>
                </td>					
			</tr>
			<% i++; %>
		</hrms:extenditerate>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td>
				<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
		         			<bean:message key="label.page.serial"/>
							<bean:write name="dataImportForm" property="dataImportForm.pagination.current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="dataImportForm" property="dataImportForm.pagination.count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="dataImportForm" property="dataImportForm.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>   
						</td>
						<td align="right" nowrap class="tdFontcolor">

							<p align="right">
								<hrms:paginationlink name="dataImportForm" property="dataImportForm.pagination" nameId="dataImportForm" propertyId="roleListProperty">
								</hrms:paginationlink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>			
			<td align="center" height="35px;">		
				<input type="button" class="mybutton" value='<bean:message key="button.new.add" />' onclick="add();" />		   				    
				&nbsp;&nbsp;<input type="button" class="mybutton" value='<bean:message key="button.delete" />' onclick="del();"/>
				&nbsp;&nbsp;<input type="button" class="mybutton" value="<bean:message key="import.tempData" />" id="btnImport" onclick="importData();"/>				
			</td>
		</tr>
	</table>
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在导入数据，请稍候...</td>
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
</html:form>
<script>
	if(getBrowseVersion() == 10){// ie11 浏览器样式修改 wangb 20190323
		var form1 = document.getElementById('form1');
		var table = form1.getElementsByTagName('table')[0];
		table.style.marginTop = '1px';
	}
</script>