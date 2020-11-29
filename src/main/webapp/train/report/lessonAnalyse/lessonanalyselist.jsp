<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all.js" ></script>
<script type="text/javascript" src="/ext/ext-lang-zh_CN.js" ></script> 
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript" src="/components/codeSelector/codeSelector.js"></script>
<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />
<script type="text/javascript">
	function seach(){
		var classValue = document.getElementById("classValue").value;
		var classViewvalue = document.getElementById("classViewvalue").value;
		lessonAnalyseForm.action = "/train/report/lessonAnalyse.do?b_query=link&a_code=${lessonAnalyseForm.a_code}&classValue="+$URL.encode(classValue)+"&classViewvalue="+$URL.encode(classViewvalue);
		lessonAnalyseForm.submit();
	}
	function sendMess(){
		var sel = "";
		var sels = document.getElementsByName("id");
		for (var i = 0; i < sels.length; i++) {
			if (sels[i].checked&&sel.indexOf(sels[i].value)==-1) {//避免重复发送(由于是用模版发送邮件没法具体提示到某个课程)
				sel += sels[i].value + ",";
			}
		}
		if (sel != "" && sel.length > 0) {
			document.getElementById("wait").style.display="";
			var hashvo = new ParameterSet();
			hashvo.setValue("users", sel.substring(0, sel.length - 1));
			var request = new Request({method:"post", asynchronous:true, onSuccess:sendmessinfo, functionId:"2020051013"}, hashvo);
		}else{
			alert("\u8BF7\u9009\u62E9\u8981\u53D1\u9001\u7684\u5BF9\u8C61\uFF01");//请选择要发送的对象！
			return null;
		}
	}
	function sendmessinfo(outparamters){
		if(outparamters){
			document.getElementById("wait").style.display="none";
			var flag=outparamters.getValue("flag");
			if("ok"==flag)
				alert("\u53D1\u9001\u6210\u529F\uFF01");//发送成功！
			else
				alert(flag);
		}
	}
	//导出Excel
	function exportExcel()
	{
		var hashvo=new ParameterSet();
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020051012'},hashvo);
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var name=outName.substring(0,outName.length);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
	}
	function getLesCode(){
	if(typeof(eval("document.all.r4117_value"))== "undefined") {
		return orgid="";
	} else 
		orgid= document.getElementById("r4117_value").value;
}
	
</script>
<html:form action="/train/report/lessonAnalyse">
<body>
	<%
		int i = 0;
	%>
		  
	<table border="0" cellpadding="0" cellspacing="0" style="margin-top:5px">
	<tr>
	<td >
	 	课程&nbsp;
	 	<span style="vertical-align: middle;">
	 	<input name = "a.value" id ="classValue" type="hidden"></input>
		<input name = "a.viewvalue" id ="classViewValue" class="text4" id ="classViewvalue"></input>
        <img src="/images/code.gif" ' onclick='getLesCode();openTrainLessonInputCodeDialog("55","a.viewvalue","1","");' align="absmiddle"/> </span>
		&nbsp;&nbsp;&nbsp;姓名&nbsp;
		<span style="vertical-align: middle;">
		<html:text name="lessonAnalyseForm" styleClass="text4" property="name" />&nbsp;
		</span>
		&nbsp;&nbsp;&nbsp;学习进度（%）&nbsp;
		<span style="vertical-align: middle;">
		<html:text name="lessonAnalyseForm" styleClass="text4" maxlength="3" property="lprogress_t" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);' style="height:20px;width: 30px;text-align: right;" />		
		
		至&nbsp;<html:text name="lessonAnalyseForm" property="lprogress_d" styleClass="text4" maxlength="3" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);' style="height:20px;width: 30px;text-align: right;" />&nbsp;
		</span>
		<span style="vertical-align: middle;">
		<input type="button" value="查询" class="mybutton" onclick="seach();"/>
		</span>
	</td>
	</tr>
	<tr style="padding-top: 5px;">
	<td>
	<div class="fixedDiv2">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="35" class="TableRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;<input type="checkbox" name="allsel" id="allsel" onclick="batch_select_all(this);"/>&nbsp;
				</td>
				<td align="center" width="11%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="b0110.label"/>&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="e0122.label"/>&nbsp;
				</td>
				<td align="center" width="12%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="e01a1.label"/>&nbsp;
				</td>
				<td align="center" width="6%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="label.title.name"/>&nbsp;
				</td>
				<td align="center" width="13%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;课程名称&nbsp;
				</td>
				<td align="center" width="9%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;起始时间&nbsp;
				</td>
				<td align="center" width="9%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;终止时间&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;时长&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;次数&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;进度&nbsp;
				</td>
				<td align="center" width="6%" class="TableRow" style="border-left:none;border-top: none;border-right: none;"  nowrap>
					&nbsp;考试成绩&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="lessonAnalyseForm"
			sql_str="lessonAnalyseForm.strsql" table="" where_str="lessonAnalyseForm.strwhere"
			columns="lessonAnalyseForm.columns" page_id="pagination"
			pagerows="${lessonAnalyseForm.pagerows}" order_by="lessonAnalyseForm.order_by">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow"  style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<input type="checkbox" name="id" value='<bean:write name="element" property="nbase"/><bean:write name="element" property="a0100"/>'/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${lessonAnalyseForm.uplevel}" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<bean:write name="element" property="a0101"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
					&nbsp;<bean:write name="element" property="r5003"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  align="center" nowrap>
					&nbsp;<bean:write name="element" property="start_date"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  align="center" nowrap>
					&nbsp;<bean:write name="element" property="end_date"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  align="right" nowrap>
					&nbsp;<bean:write name="element" property="learnedhour"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  align="right" nowrap>
					&nbsp;<bean:write name="element" property="learnednum"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;"  align="right" nowrap>
					&nbsp;<bean:write name="element" property="lprogress"/>%&nbsp;
				</td>
				<td class="RecordRow" style="border-left:none;border-top: none;border-right: none;"  align="right" nowrap>
					&nbsp;<bean:write name="element" property="tr_selfscore"/>&nbsp;
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="lessonAnalyseForm"
								pagerows="${lessonAnalyseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
								<hrms:paginationdblink name="lessonAnalyseForm"
									property="pagination" nameId="lessonAnalyseForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="left" style="padding-top: 5px;">
				<input type="button" value="发送通知" class="mybutton" onclick="sendMess();"/>
				<input type="button" value="导出Excel" onclick="exportExcel();" class="mybutton" />
			</td>
		</tr>
	</table>
	</body>
</html:form>
<div id='wait' style='position:absolute;top:180;left:300;display: none;'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style" height="40">正在发送通知，请稍等...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;" class="complex_border_color" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
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
<script>
	function IsDigit(obj) {
		if ((event.keyCode > 47) && (event.keyCode <= 57)) {
			return true;
		} else {
			return false;
		}
	}
	
	function isNumber(obj) {
		var checkOK = "-0123456789.";
		var checkStr = obj.value;
		var allValid = true;
		var decPoints = 0;
		var allNum = "";
		if (checkStr == "") {
			return;
		}
		var count = 0;
		var theIndex = 0;
		for (i = 0; i < checkStr.length; i++) {
			ch = checkStr.charAt(i);
			if (ch == "-") {
				count = count + 1;
				theIndex = i + 1;
			}
			for (j = 0; j < checkOK.length; j++) {
				if (ch == checkOK.charAt(j)) {
					break;
				}
			}
			if (j == checkOK.length) {
				allValid = false;
				break;
			}
			if (ch == ".") {
				allNum += ".";
				decPoints++;
			} else {
				if (ch != ",") {
					allNum += ch;
				}
			}
		}
		if (count > 1 || (count == 1 && theIndex > 1)) {
			allValid = false;
		}
		if (decPoints > 1 || !allValid) {
			alert("\u8bf7\u8f93\u5165\u6570\u503c\u7c7b\u578b\u7684\u503c\uff01");//请输入数值类型的值！
			obj.value = "";
			obj.focus();
		}
	}
</script>