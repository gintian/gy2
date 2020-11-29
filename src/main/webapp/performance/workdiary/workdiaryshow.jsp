
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%@ page import="com.hjsj.hrms.actionform.performance.workdiary.WorkdiaryForm"%>
<%
String logo = request.getParameter("logo");
if(logo==null)
	logo="";
if(logo.equals("1")){
	logo = "10";
}
%>

<DIV id="overDiv" class="RecordRow" style="display:none; POSITION: absolute; Z-INDEX: 1;background-color:#FFFFCC;overflow:visible;background-image:../images/mainbg.jpg"></DIV> 
<style type="text/css">
#scroll_box {
    border: 1px solid #eee;
    height: 300px;    
    width: 390px;
    overflow: auto;
    margin: 1em 1;
}
</style>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="workdiary.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" language="javascript">
<!--
logo="<%=logo%>";//全局变量

function boks(){
var str="";
	for(var i=0;i<document.workdiaryForm.elements.length;i++)
			{
				if(document.workdiaryForm.elements[i].type=="checkbox")
				{
					if(document.workdiaryForm.elements[i].checked==true)
					{
						if(document.workdiaryForm.elements[i+1].value!="true")
							str+=document.workdiaryForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert(SELECT_APPROVAL_LOG+"！");
				return;
			}else{
			if(confirm(APPROVAL_OK+"?")){
	    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_ok=link&action=ok";	 
    	workdiaryForm.submit();
    	}}
}
function b_backs(){
var str="";
	for(var i=0;i<document.workdiaryForm.elements.length;i++)
			{
				if(document.workdiaryForm.elements[i].type=="checkbox")
				{
					if(document.workdiaryForm.elements[i].checked==true)
					{
						if(document.workdiaryForm.elements[i+1].value!="true")
							str+=document.workdiaryForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert(SELECT_DISMISSED_LOG+"！");
				return;
			}else{
			if(confirm(DISMISSED_OK+"?")){
	    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_back=link&action=back";
    	workdiaryForm.submit();
    	}}
}
function b_dels(){
var str="";
	for(var i=0;i<document.workdiaryForm.elements.length;i++){
		if(document.workdiaryForm.elements[i].type=="checkbox"){
			if(document.workdiaryForm.elements[i].checked==true){
				if(document.workdiaryForm.elements[i+1].value!="true")
					str+=document.workdiaryForm.elements[i+1].value+"/";
			}
		}
	}
	if(str.length==0){
		alert(SELECT_DEL_LOG+"！");
		return;
	}else{
		if(confirm(DEL_INFO)){
	    	workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_del=link&action=del";
    		workdiaryForm.submit();
    	}
   	}
}

function serch(){
	workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_query=link&search=ok&timefield=1&logo="+logo;
    workdiaryForm.submit();
}
function serch1(vv){
	var v1="nameh",v2="namehvalue";
	v1 = v1.replace("h",vv);
	v2=v2.replace("h",vv);
	var name1=document.getElementById(v1).value;
	var namevalue=document.getElementById(v2);
	if(namevalue){
		namevalue=namevalue.value;
	}else{
		namevalue="";
	}
	workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_query=link&search=ok&timefield=1&name1="+name1+"&namevalue="+namevalue+"&logo="+logo;
    workdiaryForm.submit();
}

function serch2(vv){
	var v1="nameh",v2="namehvalue";
	var name1=document.getElementById(v1.replace("h",vv)).value;
	var namevalue=document.getElementById(v2.replace("h",vv)).value;
	workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_query=link&search=ok&timefield=1&name1="+name1+"&namevalue="+namevalue+"&logo="+logo;
    workdiaryForm.submit();
}

function serch3(vv){
	var v1="nameh",namevalue="";
	v1 = v1.replace("h",vv);
	var name1=document.getElementById(v1).value;
	if(name1!=""&&!/\d/.test(name1)){
		alert("必须输入数值类型！");
		return;
	}
	workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_query=link&search=ok&timefield=1&name1="+name1+"&namevalue="+namevalue+"&logo="+logo;
    workdiaryForm.submit();
}
function outContent(p0100,pid){
	var hashvo=new ParameterSet();
	hashvo.setValue("p0100",p0100);	
	hashvo.setValue("pid",pid);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'9020010011'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	var titles=outparamters.getValue("titles");
	config.FontSize='10pt';//hint提示信息中的字体大小
	Tip(getDecodeStr(content),STICKY,true);
	return true; 
}
function getWeekList(){
	var yearnum = document.getElementById("yearnum").value;
	var monthnum = document.getElementById("monthnum").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("yearnum",yearnum);	
	hashvo.setValue("monthnum",monthnum);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:setWeelList,functionId:'9020010012'},hashvo);
}
function setWeelList(outparamters){
	var weeklist=outparamters.getValue("weeklist");
	if(weeklist.length>0){
		AjaxBind.bind(workdiaryForm.weeknum_arr,weeklist);
	}
}
function viewTimeFlag(obj,viewname){
	if(obj.value=="1")
		toggles(viewname);
	else
		hides(viewname);
}
function printExcel(){
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid","P01"); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,
		functionId:'2020050033'},hashvo);
}
function showFieldList(outparamters){
	var outName=outparamters.getValue("outName");
	//var name=outName.substring(0,outName.length-1)+".xls";
	//window.location.target="_blank";
	//window.location.href="/servlet/DisplayOleContent?filename="+name;
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
function commentEvWk(p0100){
	// 去掉所有的勾选
	var check = document.getElementsByTagName("input");
	for (i = 0; i < check.length; i++) {
		if (check[i].type == "checkbox" && check[i].checked==true) {
			check[i].checked = false;
		}
	}
	workdiaryForm.action="/general/impev/importantevcomment.do?b_comment=link&flag=1&a_code=${workdiaryForm.a_code}&a0100=${workdiaryForm.a0100}&p0600="+p0100;
	workdiaryForm.submit();
}
function selAll(selall)
{
	var tablevos=document.getElementsByTagName("input");
	
	for(var i=1;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			tablevos[i].checked=selall.checked==true?"checked":"";
		}
	} 
}
function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
-->
</script>

<hrms:themes />
<html:form action="/performance/workdiary/workdiaryshow">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" >
	<%
	WorkdiaryForm wdf=(WorkdiaryForm)session.getAttribute("workdiaryForm");
	int len=wdf.getFieldlist().size()+2;
	int i = 0;
	%>
		<tr>
			<td   nowrap colspan="<%=(len+1)%>">
			<table border="0" cellspacing="0"  cellpadding="0">
			<tr>
			<td width="130" nowrap height="20">
				&nbsp;&nbsp;<bean:message key="lable.zp_plan.status"/>
				<html:select name="workdiaryForm" property="appstate" onchange="serch();" style="width:60">
			 		<html:optionsCollection property="statelist" value="dataValue" label="dataName" />
				</html:select>
			</td>
			<td width="15">&nbsp;</td>
			<logic:notEqual name="workdiaryForm" property="ymd" value="0">
			<td width="350" nowrap>
				<bean:message key="workdiary.message.search.way"/>
				<html:select name="workdiaryForm" property="ymd" onchange="serch();" style="width:50">
			 		<html:optionsCollection property="ymdlist" value="dataValue" label="dataName" />
				</html:select>
				<html:select name="workdiaryForm" property="yearnum" onchange="serch();" style="width:55">
			 		<html:optionsCollection property="yearlist" value="dataValue" label="dataName" />
				</html:select><bean:message key="datestyle.year"/> 
				<html:select name="workdiaryForm" property="monthnum" onchange="serch();" style="width:40">
			 		<html:optionsCollection property="monthlist" value="dataValue" label="dataName" />
				</html:select><bean:message key="datestyle.month"/>
				<logic:equal name="workdiaryForm" property="ymd" value="1"><!-- 周报 -->
					<html:select name="workdiaryForm" property="weeknum" onchange="serch();" style="width:70">
			 			<html:optionsCollection property="weeklist" value="dataValue" label="dataName" />
					</html:select>
				</logic:equal>
			</td>
			</logic:notEqual>
			<logic:equal name="workdiaryForm" property="ymd" value="0"><!-- 日报 -->
			<td nowrap>
				<bean:message key="workdiary.message.search.way"/>
				<html:select name="workdiaryForm" property="ymd" onchange="serch();" style="width:50">
			 		<html:optionsCollection property="ymdlist" value="dataValue" label="dataName" />
				</html:select>
			  <logic:equal value="" name="workdiaryForm" property="a0100">
				<html:radio name="workdiaryForm" property="timeflag" onclick="viewTimeFlag(this,'timeflagview');" value="0"/><bean:message key="kq.wizard.bmonth"/>
				<html:radio name="workdiaryForm" property="timeflag" onclick="viewTimeFlag(this,'timeflagview');" value="2"/>本周
				<html:radio name="workdiaryForm" property="timeflag" onclick="viewTimeFlag(this,'timeflagview');" value="3"/>本日
				<html:radio name="workdiaryForm" property="timeflag" onclick="viewTimeFlag(this,'timeflagview');" value="1"/><bean:message key="jx.khplan.timeframe"/>
			  </logic:equal>
			  <logic:notEqual value="" name="workdiaryForm" property="a0100">
			  	<html:hidden name="workdiaryForm" property="timeflag"/>
				&nbsp;&nbsp;<bean:message key="jx.khplan.timeframe"/>
			  </logic:notEqual>
			</td>
			<td nowrap width="15">&nbsp;</td>
			<td nowrap>
					<logic:equal name="workdiaryForm" property="timeflag" value="1">
					<span id="timeflagview">
					<bean:message key="label.from"/>
					<input type="text" id='startime' name="startime" onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:80px;font-size:10pt;text-align:left;" id="editor1"  dropDown="dropDownDate">
					<bean:message key="kq.init.tand"/>
					<input type="text" id='endtime' name="endtime"  onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:80px;font-size:10pt;text-align:left;" id="editor1"  dropDown="dropDownDate">	
					</span>
					</logic:equal>
					<logic:notEqual name="workdiaryForm" property="timeflag" value="1">
					<span id="timeflagview" style="display:none">
					<bean:message key="label.from"/>
					<input type="text" id='startime' name="startime"  onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:90px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" id="editor1"  dropDown="dropDownDate">
					<bean:message key="kq.init.tand"/>
					<input type="text" id='endtime' name="endtime"  onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:90px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" id="editor1"  dropDown="dropDownDate">	
					</span>
					</logic:notEqual>
				</td>
			</logic:equal>
			<td nowrap width="15">&nbsp;&nbsp;按&nbsp;</td>
			<td width="132" nowrap="nowrap" align="left">
				
				<html:select name="workdiaryForm" property="colum" onchange="changstyle(this);" style="width:110">
			 		<html:optionsCollection property="columlist" value="dataValue" label="dataName" style="width:90" />
				</html:select>&nbsp;
				
			</td>
			<td align="left" nowrap="nowrap" style='padding-bottom:2px;'>
				<span id="generalstyle" style="display:none;">
				<input type="text" name="name1" value="" id="nameid" class="textColorWrite"> &nbsp;
				<BUTTON name="bdel" class="mybutton"  onclick="serch1('id');" style="vertical-align: middle;"><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="numberstyle" style="display:none">
				<input type="text" name="name1" value="" id="nameid4" class="textColorWrite"> 
				&nbsp;<BUTTON name="bdel" class="mybutton"  onclick="serch3('id4');"  style="vertical-align: middle;"><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="unstyle" style="display:none">
					<input type="hidden" name="namevalue" id="name1value" value="">  
                     &nbsp;<input type="text" name="name1" id="name1" value="${workdiaryForm.name1 }" readonly="readonly" class="textColorWrite"> 
                     <img  src="/images/code.gif" align="absmiddle" onclick='javascript:openInputCodeDialogOrgInputPos3("UN","name1","","1");'/>&nbsp;
					<BUTTON name="bdel" class="mybutton"  onclick="serch1('1');" style="vertical-align: middle;" ><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="umstyle" style="display:none">
					<input type="hidden" name="namevalue" id="name2value" value="">  
                     &nbsp;<input type="text" name="name1" id="name2" value="${workdiaryForm.name1 }"  class="textColorWrite" readonly="readonly"> 
                     <img  src="/images/code.gif" align="absmiddle" onclick='javascript:openInputCodeDialogOrgInputPos3("UM","name2","","2");'/>&nbsp;
					<BUTTON name="bdel" class="mybutton"  onclick="serch1('2');"  style="vertical-align: middle;"><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="@kstyle" style="display:none">
					<input type="hidden" name="namevalue" id="name3value" value="">  
                     &nbsp;<input type="text" name="name1" id="name3" value="${workdiaryForm.name1 }" readonly="readonly"  class="textColorWrite"> 
                     <img  src="/images/code.gif" align="absmiddle" onclick='javascript:openInputCodeDialogOrgInputPos3("@K","name3","","2");'/>&nbsp;
					<BUTTON name="bdel" class="mybutton"  onclick="serch1('3');"  style="vertical-align: middle;"><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="timestyle" style="display:none">
					<bean:message key="label.from"/>
					<input type="text" name="name1" value="${workdiaryForm.name1 }"  onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:90px;font-size:10pt;text-align:left;" id="nameid2"  dropDown="dropDownDate">
					<bean:message key="kq.init.tand"/>
					<input type="text" name="namevalue" value="${workdiaryForm.namevalue }"  onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:90px;font-size:10pt;text-align:left;" id="nameid2value"  dropDown="dropDownDate">	
					&nbsp;<BUTTON name="bdel" class="mybutton"  onclick="serch2('id2');" ><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="predbnamestyle" style="display:none">
					<html:select name="workdiaryForm" property="name1" onchange=""  styleId="nameid1" style="width:110" >
			 			<html:optionsCollection property="predbnamelist" value="dataValue" label="dataName" style="width:90" />
					</html:select>
					&nbsp;<BUTTON name="bdel" class="mybutton"  onclick="serch1('id1');" style="vertical-align: middle;" ><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="codestyle" style="display:none">
					<input type="hidden" name="namevalue" id="name4value" value="">  
                     &nbsp;<input type="text" name="name1" id="name4" value="${workdiaryForm.name1 }" readonly="readonly"  class="textColorWrite"> 
                     <img src="/images/code.gif" onclick='javascript:showcode();' />&nbsp;
					<BUTTON name="bdel" class="mybutton"  onclick="serch1('4');" style="vertical-align: middle;" ><bean:message key="button.query"/></BUTTON>
				</span>
				<span id="oldbefore" style="display:none">
					<BUTTON name="bdel" class="mybutton"  onclick="serch();" ><bean:message key="button.query"/></BUTTON>
				</span>
			</td>
			</tr>
			</table>
			</td>
		</TR>
		</table>
		
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
			<tr>
			<td align="center" width="5%" class="TableRow" nowrap>
				<input type="checkbox" name="0" value="true" title="全选" onclick="selAll(this)">
			</td>
			<td align="center" width="5%" class="TableRow" nowrap>	
				<bean:message key="kq.wizard.wise"/>
			</td>
			<td align="center" width="5%" class="TableRow" nowrap>	
				<bean:message key="column.operation"/>
			</td>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist">
				<bean:define id="fids" name="info" property="itemid"/>
				<bean:define id="ftype" name="info" property="itemtype"/>
				<bean:define id="fcodesetids" name="info" property="codesetid"/>
				<logic:notEqual value="p0100" name="fids">
					<logic:notEqual value="p0115" name="fids">
						<logic:equal value="M" name="ftype">
					 		<td align="center" width="15%" class="TableRow" nowrap>
								<bean:write name="info" property="itemdesc"/>
							</td>
					 	</logic:equal>
					 	<logic:notEqual value="M" name="ftype">
					 			<td align="center" width="5%" class="TableRow" nowrap>
									<bean:write name="info" property="itemdesc"/>
								</td>
					 	</logic:notEqual>
					</logic:notEqual>
					
					<logic:equal value="p0115" name="fids">
						<logic:equal value="true" name="info" property="visible">
							<td align="center" width="5%" class="TableRow" nowrap>
								<bean:write name="info" property="itemdesc"/>
							</td>
						</logic:equal>
					</logic:equal>
					
				</logic:notEqual>
			</logic:iterate>
			 <td align="center" width="5%" class="TableRow" nowrap>评论</td>
			</TR>

		<hrms:extenditerate id="element" name="workdiaryForm" property="paginationForm.list" indexes="indexes"
		pagination="paginationForm.pagination" pageCount="${workdiaryForm.pagerows}" scope="session">
			<bean:define id="p0100" name="element" property="p0100"/>
			<%if(i%2==0){%>
          	<tr class="trShallow">
          	<%}else{%>
          	<tr class="trDeep">
         	<% }%>  
			<td align="center" class="RecordRow" nowrap>
				<logic:notEqual value="03" name="element" property="p0115">
					<logic:notEqual value="07" name="element" property="p0115">
						<hrms:operateworkdiary p0100="${p0100}">
							<hrms:checkmultibox name="workdiaryForm" property="paginationForm.select" value="true" indexes="indexes" />
							<INPUT type="hidden" name="<%=i%>" value="${itemname}">
						</hrms:operateworkdiary>
					</logic:notEqual>
				</logic:notEqual>
			</td>
			<td align="center" class="RecordRow" nowrap>
				<logic:equal value="0" name="element" property="state">
					<bean:message key="performance.workdiary.daily"/>
				</logic:equal>
				<logic:equal value="1" name="element" property="state">
					<bean:message key="performance.workdiary.weekly"/>
				</logic:equal>
				<logic:equal value="2" name="element" property="state">
					<bean:message key="performance.workdiary.monthly"/>
				</logic:equal>
			</td>
			<td align="center" class="RecordRow" nowrap>
				<logic:notEqual value="07" name="element" property="p0115">
					<logic:notEqual value="03" name="element" property="p0115">
						<a href="/performance/workdiary/workdiaryshow.do?b_search=link&home=&p0100=${p0100}"><bean:message key="button.apply"/></a>		
					</logic:notEqual>
				</logic:notEqual>
				<logic:equal value="03" name="element" property="p0115">
					<a href="/performance/workdiary/workdiaryshow.do?b_search=link&home=&query=own&p0100=${p0100}"><bean:message key="label.view"/></a>		
				</logic:equal>
				<logic:equal value="07" name="element" property="p0115">
					<a href="/performance/workdiary/workdiaryshow.do?b_search=link&home=&query=own&p0100=${p0100}"><bean:message key="label.view"/></a>		
				</logic:equal>
			</td>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist">
					<bean:define id="fid" name="info" property="itemid"></bean:define>
					<bean:define id="ftype" name="info" property="itemtype"/>
					<logic:notEqual value="p0100" name="fid">
						<logic:equal value="M" name="ftype">
							<td align="left" class="RecordRow" nowrap onmouseout="tt_HideInit();" onmouseover='outContent("${p0100}","${fid}");'>
								<bean:write name="element" property="${fid}" filter="false"/>&nbsp;
							</td>
						</logic:equal>
						<logic:equal value="N" name="ftype">
						    <td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="${fid}"/>
							</td>
						</logic:equal>	
						<logic:notEqual value="N" name="ftype">					
						<logic:notEqual value="M" name="ftype">
								<logic:equal value="b0110" name="fid" >
									<td align="left" class="RecordRow" nowrap>
									<hrms:codetoname name="element" codeid="UN" codeitem="codeitem" codevalue="b0110" scope="page"/>
									<bean:write name="codeitem" property="codename" />&nbsp; 
									</td>
								</logic:equal>
								<logic:equal value="e0122" name="fid" >
								<td align="left" class="RecordRow" nowrap>
									<hrms:codetoname name="element" codeid="UM" codeitem="codeitem" codevalue="e0122" scope="page"/>
									<bean:write name="codeitem" property="codename" />&nbsp; 
									</td>
								</logic:equal>
								<logic:equal value="e01a1" name="fid" >
								<td align="left" class="RecordRow" nowrap>
									<hrms:codetoname name="element" codeid="@K" codeitem="codeitem" codevalue="e01a1" scope="page"/>
									<bean:write name="codeitem" property="codename" />&nbsp; 
									</td>
								</logic:equal>
								<logic:equal value="p0115" name="fid" >
								<logic:equal value="true" name="info" property="visible">
									<td align="center" class="RecordRow" nowrap>
										<hrms:codetoname name="element" codeid="23" codeitem="codeitem" codevalue="p0115" scope="page"/>
										<bean:write name="codeitem" property="codename" />&nbsp; 
									</td>
								</logic:equal>
								</logic:equal>
								<logic:equal value="nbase" name="fid" >
								<td align="left" class="RecordRow" nowrap>
									<hrms:codetoname name="element" codeid="@@" codeitem="codeitem" codevalue="nbase" scope="page"/>
									<bean:write name="codeitem" property="codename" />&nbsp; 
									</td>
								</logic:equal>
								<logic:notEqual value="b0110" name="fid">
									<logic:notEqual value="e0122" name="fid">
										<logic:notEqual value="e01a1" name="fid">
											<logic:notEqual value="p0115" name="fid">
												<logic:notEqual value="nbase" name="fid">
													<td align="left" class="RecordRow" nowrap>
														<bean:write name="element" property="${fid}"/>
													</td>
												</logic:notEqual>
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
								</logic:notEqual>
						 </logic:notEqual>
						</logic:notEqual>
					</logic:notEqual>
			</logic:iterate>
 				<td class="RecordRow" nowrap>&nbsp;
    				<img src="/images/edit.gif" style="cursor:p" border=0 onclick="commentEvWk('${p0100}');">               
	    		</td>
			</tr>
			<%i++;%>
		</hrms:extenditerate>
	</table>
	
	<table width="100%"  align="center" class="RecordRowP">
		<td valign="bottom" class="tdFontcolor">
			<hrms:paginationtag name="workdiaryForm" pagerows="${workdiaryForm.pagerows}" property="paginationForm.pagination" refresh="true"></hrms:paginationtag>
		</td>
	    <td align="right" nowrap class="tdFontcolor">
		     <hrms:paginationlink  name="workdiaryForm" property="paginationForm.pagination" nameId="paginationForm" >
			</hrms:paginationlink>
		</td>
	</table>
	
	<table width="80%" align="center">
		<tr>
			<td align="center">
			     <hrms:priv func_id="03061">	
			   	    <BUTTON name="bok" class="mybutton"  onclick="boks();" ><bean:message key="approve.personinfo.oks"/></BUTTON>&nbsp;
			   	</hrms:priv>   
			     <hrms:priv func_id="03062">   	 
					<BUTTON name="back" class="mybutton"  onclick="b_backs();" ><bean:message key="info.appleal.state2"/></BUTTON>&nbsp;
			   	</hrms:priv>  
			     <hrms:priv func_id="03063">    	 
					<BUTTON name="bdel" class="mybutton"  onclick="b_dels();" ><bean:message key="button.delete"/></BUTTON>&nbsp;
			   	</hrms:priv> 
			   	
			   	
			   	<hrms:priv func_id="03064">    	
			   	<BUTTON name="bdel" class="mybutton" onclick="printExcel();">导出Excel</BUTTON>&nbsp;
			   	</hrms:priv>
			   	
			   	<logic:notEqual value="" name="workdiaryForm" property="a0100">
			   	<BUTTON name="bcol" class="mybutton" onclick="javascript:window.close();">关闭</BUTTON>
			   	</logic:notEqual>
			</td>
		</tr>
	</table>

</html:form>
<script type="text/javascript">
<!--
function showcode(){
	var obj=document.getElementsByName("colum")[0];
	var v = obj.value;
	var vs =v.split("`");
	openInputCodeDialog1(vs[2],"name4");
}
function changstyle(obj){
	var v = obj.value;
	var vs =v.split("`");
	if(vs.length>1){
		var itemid=vs[0];
		var itemtype=vs[1];
		var codesetid=vs[2];
		if(codesetid.toLowerCase()=="un"){//单位
			$("numberstyle").style.display="none";
			$("predbnamestyle").style.display="none";
			$("umstyle").style.display="none";
			$("@kstyle").style.display="none";
			$("timestyle").style.display="none";
			$("generalstyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("unstyle");
			o.style.display="block";
		}else if(codesetid.toLowerCase()=="um"){//部门
			$("numberstyle").style.display="none";
			$("predbnamestyle").style.display="none";
			$("unstyle").style.display="none";
			$("@kstyle").style.display="none";
			$("timestyle").style.display="none";
			$("generalstyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("umstyle");
			o.style.display="block";
		}else if(codesetid.toLowerCase()=="@k"){//职位
			$("numberstyle").style.display="none";
			$("predbnamestyle").style.display="none";
			$("umstyle").style.display="none";
			$("unstyle").style.display="none";
			$("timestyle").style.display="none";
			$("generalstyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("@kstyle");
			o.style.display="block";
		}else if(itemid.toLowerCase()=="nbase"){//人员库
			$("numberstyle").style.display="none";
			$("umstyle").style.display="none";
			$("unstyle").style.display="none";
			$("timestyle").style.display="none";
			$("generalstyle").style.display="none";
			$("@kstyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("predbnamestyle");
			o.style.display="block";
		}else if(itemtype=='D'){
			$("numberstyle").style.display="none";
			$("predbnamestyle").style.display="none";
			$("umstyle").style.display="none";
			$("@kstyle").style.display="none";
			$("unstyle").style.display="none";
			$("generalstyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("timestyle");
			o.style.display="block";
		}else if(itemtype=='N'){
			$("predbnamestyle").style.display="none";
			$("umstyle").style.display="none";
			$("@kstyle").style.display="none";
			$("unstyle").style.display="none";
			$("generalstyle").style.display="none";
			$("timestyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("numberstyle");
			o.style.display="block";
		}else if(itemtype=='A'){
			if(codesetid=='0'){
				$("numberstyle").style.display="none";
				$("predbnamestyle").style.display="none";
				$("umstyle").style.display="none";
				$("@kstyle").style.display="none";
				$("timestyle").style.display="none";
				$("unstyle").style.display="none";
				$("codestyle").style.display="none";
				$("oldbefore").style.display="none";
				var o = $("generalstyle");
				o.style.display="block";
			}else{
				$("numberstyle").style.display="none";
				$("predbnamestyle").style.display="none";
				$("umstyle").style.display="none";
				$("@kstyle").style.display="none";
				$("timestyle").style.display="none";
				$("unstyle").style.display="none";
				$("generalstyle").style.display="none";
				$("oldbefore").style.display="none";
				var o = $("codestyle");
				o.style.display="block";
			}
		}else if(itemtype=='M'){
			$("numberstyle").style.display="none";
			$("predbnamestyle").style.display="none";
			$("umstyle").style.display="none";
			$("@kstyle").style.display="none";
			$("timestyle").style.display="none";
			$("unstyle").style.display="none";
			$("codestyle").style.display="none";
			$("oldbefore").style.display="none";
			var o = $("generalstyle");
			o.style.display="block";
		}
	}else{
		$("numberstyle").style.display="none";
		$("predbnamestyle").style.display="none";
		$("unstyle").style.display="none";
		$("umstyle").style.display="none";
		$("@kstyle").style.display="none";
		$("timestyle").style.display="none";
		$("generalstyle").style.display="none";
		$("codestyle").style.display="none";
		$("oldbefore").style.display="block";
	}
}
changstyle(document.getElementsByName("colum")[0]);
//-->
</script>
<logic:equal name="workdiaryForm" property="ymd" value="0">
<script type="text/javascript" language="javascript">
if("<%=request.getParameter("sp_flag")%>"==1){
document.getElementById("startime").value = "${workdiaryForm.start_date}";
document.getElementById("endtime").value = "${workdiaryForm.end_date}";
}else{
document.getElementById("startime").value = "${workdiaryForm.startime}";
document.getElementById("endtime").value = "${workdiaryForm.endtime}";
}

</script>
</logic:equal>