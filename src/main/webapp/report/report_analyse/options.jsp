<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/validateDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

   <link href="../../css/css1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<hrms:themes />
<script language="javascript" src="../../ajax/constant.js"></script>
<script language="javascript" src="../../ajax/basic.js"></script>
<script language="javascript" src="../../ajax/common.js"></script>
<script language="javascript" src="../../ajax/control.js"></script>
<script language="javascript" src="../../ajax/dataset.js"></script>
<script language="javascript" src="../../ajax/editor.js"></script>
<script language="javascript" src="../../ajax/dropdown.js"></script>
<script language="javascript" src="../../ajax/table.js"></script>
<script language="javascript" src="../../ajax/menu.js"></script>
<script language="javascript" src="../../ajax/tree.js"></script>
<script language="javascript" src="../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../ajax/command.js"></script>
<script language="javascript" src="../../ajax/format.js"></script>
<style>
<!--
	#scroll_box {
	          
	           height: 120px;    
	           width: 100%;            
	           overflow: auto;            
	           margin: 1em 0;
	       }
-->
</style>

<script language="javascript">
	reserve_close();
	function reserve_close(){
		var isclose ='${reportAnalyseForm.isclose}';
		var checkbase='${reportAnalyseForm.checkbase}';
		if(isclose==1){
		    //兼容谷歌浏览器 wangbs 20190319
			if(parent.window.opener.revertDataReturnWin){
                parent.window.opener.revertDataReturnWin(checkbase);
                parent.window.close();
            }else{
				window.returnValue=checkbase;
                window.close();
            }
		}
	}
     function check(){
     var dbs = document.getElementsByName('db');
	var dbstr='';
	if(dbs)
	{		
		if(dbs.length>0)
		{
				for(var i=0;i<dbs.length;i++)
				{
					if(dbs[i].checked==true)
						dbstr+=','+dbs[i].value;	
				}
		}
		else
		{
			if(dbs.checked==true)
				dbstr+=','+dbs.value;	
		}
		if(dbstr==""){
		alert(SELECT_PERSON_DATABASE);
		return;
		}
		
		reportAnalyseForm.action="/report/report_analyse/reportanalyse.do?b_queryBase=find&isclose=1&checkbase="+dbstr;
		reportAnalyseForm.submit();	
//		window.returnValue=dbstr;
//		window.close();
	}else{
	alert(SELECT_PERSON_DATABASE);
	}
		
   }    
	
</script>
<body >
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>  
			<td valign="top" align="center">
				<form name="reportAnalyseForm" method="post" action="/report/auto_fill_report/options.do">
					<br><fieldset align="center" style="width:80%;">
					<legend ><bean:message key="report.reportlist.dbname"/></legend>
					<table border="0" width="100%">
					<tr> 
						<td valign="top">
						<div id="scroll_box" text-align="center">
							<table border="0" cellspacing="0" align="center" cellpadding="0" >
								<logic:iterate id="element" name="reportAnalyseForm"
								property="dbnamelist">
								<tr>
								<td>
									<input name="db" type="checkbox" value="<bean:write name="element" property="pre" filter="true" />"
									<logic:notEqual name="element" property="dbsel"	value="0">checked</logic:notEqual> />
									<bean:write name="element" property="dbname" filter="true" />
								</td>
								</tr>
								</logic:iterate>
							</table>
						</div>
						</td>
					</tr>
					</table>
				</fieldset>
				<br>
				<table align='center' >
				<tr>
					<td align="center" >
						<input type="button" name="b_update" value="<bean:message key='button.ok'/>" class="mybutton" onClick="check()">     
						<input type="reset" value="<bean:message key='options.reset'/>" class="mybutton">
						<hrms:tipwizardbutton flag="report" target="il_body" formname="reportAnalyseForm"/> 
					</td>
				</tr>
				</table>
				
			</form>
		</td>
		</tr>
	</table>
</body>


