

<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.workdiary.WorkdiaryForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.utils.PubFunc" %>
<%	
	WorkdiaryForm wf = (WorkdiaryForm)session.getAttribute("workdiaryForm");
	if(!"diary".equals(wf.getSearchflag())){
		wf.setSearchterm("");
	}
	wf.setSearchflag("");
	String a0 = PubFunc.encryption("0");
	
	String startime_old = wf.getStartime();
	String endtime_old = wf.getEndtime();
%>				 

<DIV id="overDiv" style="POSITION: absolute; Z-INDEX: 1;background-color:#FFFFCC;overflow:visible;background-image:../images/mainbg.jpg"></DIV><!-- LiWeichao 去掉  class="RecordRow" -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="workdiary.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<link href='/ext/ext6/resources/ext-theme.css' rel='stylesheet' type='text/css'><link>
<script language="JavaScript" src="/ext/ext6/ext-all.js"></script>
<script language="JavaScript" src="/components/personPicker/PersonPicker.js"></script>
<script type="text/javascript" language="javascript">

var zxgflag = "${workdiaryForm.zxgflag}";
var startime_old = '<%=startime_old%>';
var endtime_old = '<%=endtime_old%>';
<!--
function addiary(){
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_search=link";
   	workdiaryForm.submit();
}
function updatediary(){
	    workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state=<%=a0 %>";
    	workdiaryForm.submit();
}

function serch(){
	var startime=workdiaryForm.startime.value;
	var endtime=workdiaryForm.endtime.value;
	var sea = document.getElementsByName("sea")[1];
	if(sea && sea.checked){
		workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&timefield=1&startime="+startime+"&endtime="+endtime+"&searchflag=diary";
	}else{
		workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currmonth=1&searchflag=diary";
		}
    workdiaryForm.submit();
}
function searchday(){
	var currentDate   =   new   Date() ;
	var y=currentDate.getYear();
	var m=currentDate.getMonth()+1;
	if(m<=9){
		m="0"+m;
	}
	var d=currentDate.getDate();
	if(d<=9){
		d="0"+d;
	}
	var startime=y+"-"+m+"-"+d;
	workdiaryForm.ymd.value="2";
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currday=1&startime="+startime;
    workdiaryForm.submit();
}
function searchweek(){
	var currentDate   =   new   Date() ;
	var y=currentDate.getYear();
	var m=currentDate.getMonth()+1;
	workdiaryForm.ymd.value="1";
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currweek=1";
    workdiaryForm.submit();
}
function searchmonth(){
	var currentDate   =   new   Date() ;
	var y=currentDate.getFullYear();
	var m=currentDate.getMonth()+1;
	if(m<=9){
		m="0"+m;
	}
	var startime=y+"-"+m+"-01";
	workdiaryForm.ymd.value="0";
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&search=ok&currmonth=1&startime="+startime;
    workdiaryForm.submit();
}
function showsjfw(){
	var sjfw=$('sjfw');
	workdiaryForm.ymd.value="3";
	sjfw.style.display="block";
}
function outContent(p0100,pid){
	var hashvo=new ParameterSet();
	hashvo.setValue("p0100",p0100);	
	hashvo.setValue("pid",pid);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'9020010011'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	config.FontSize='10pt';//hint提示信息中的字体大小
	Tip(getDecodeStr(content),STICKY,true);
}
function returnblack(){
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_search=link&state=0&zxgflag="+zxgflag;
	workdiaryForm.submit();
}
function view(p0100){
	workdiaryForm.action = "/general/impev/importantevcomment.do?b_query=link&flag=1&p0600="+p0100;
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
<html:form action="/performance/workdiary/myworkdiaryshow">
<%int i = 0;%>
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:5px;">
		<tr>
			<td nowrap>
			<table>
			<tr>
			<html:hidden name="workdiaryForm" property="ymd"/>
			<logic:equal value="0" name="workdiaryForm" property="ymd">
			<td nowrap>
			<!-- 
			<input type="radio" name="sea" onclick="searchday();"/><bean:message key="kq.wizard.today"/>
			<input type="radio" name="sea" onclick="searchweek();"/><bean:message key="kq.wizard.bweek"/>
			-->
			<input type="radio" name="sea" onclick="searchmonth();" checked="checked"/><bean:message key="kq.wizard.bmonth"/>
			<input type="radio" name="sea" onclick="showsjfw();"/><bean:message key="jx.khplan.timeframe"/>
			
			</td>
			</logic:equal>
			<!-- 
			<logic:equal value="1" name="workdiaryForm" property="ymd">
			<td nowrap>
			<input type="radio" name="sea" onclick="searchday();"/><bean:message key="kq.wizard.today"/>
			<input type="radio" name="sea" onclick="searchweek();" checked="true"/><bean:message key="kq.wizard.bweek"/>
			<input type="radio" name="sea" onclick="searchmonth();"/><bean:message key="kq.wizard.bmonth"/>
			<input type="radio" name="sea" onclick="showsjfw();"/><bean:message key="kq.init.tscope"/>
			</td>
			</logic:equal>
			<logic:equal value="2" name="workdiaryForm" property="ymd">
			<td nowrap>
			<input type="radio" name="sea" onclick="searchday();"  checked="true"/><bean:message key="kq.wizard.today"/>
			<input type="radio" name="sea" onclick="searchweek();"/><bean:message key="kq.wizard.bweek"/>
			<input type="radio" name="sea" onclick="searchmonth();"/><bean:message key="kq.wizard.bmonth"/>
			<input type="radio" name="sea" onclick="showsjfw();"/><bean:message key="kq.init.tscope"/>
			</td>
			</logic:equal>
			 -->
			<logic:equal value="3" name="workdiaryForm" property="ymd">
			<td nowrap>
			<!-- 
			<input type="radio" name="sea" onclick="searchday();" /><bean:message key="kq.wizard.today"/>
			<input type="radio" name="sea" onclick="searchweek();"/><bean:message key="kq.wizard.bweek"/>
			 -->
			
			<input type="radio" name="sea" onclick="searchmonth();"/><bean:message key="kq.wizard.bmonth"/>
			<input type="radio" name="sea" onclick="showsjfw();" checked="checked"/><bean:message key="jx.khplan.timeframe"/>
			
			</td>
			</logic:equal>
			<td nowrap>
			<div id="sjfw" style="display:none">
				&nbsp;<bean:message key="label.from"/>
				<input type="text" name="startime" onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" id="editor1"  dropDown="dropDownDate">
				<bean:message key="kq.init.tand"/>
				<input type="text" name="endtime" onblur="timeCheck(this);" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" id="editor1"  dropDown="dropDownDate">	
				&nbsp;
			</div>
			</td>
			<td>
			<div style="margin-left:1">
			  <logic:equal value="" name="workdiaryForm" property="searchterm">
			  <html:text name="workdiaryForm" style="color:gray" value="请输入批示、内容"  onfocus="notext(this);"  onblur="addtext(this)" property="searchterm" size="25"/>	
			  &nbsp;
			  </logic:equal>
			  <logic:notEqual value="" name="workdiaryForm" property="searchterm">
			  <logic:equal value="请输入批示、内容" name="workdiaryForm" property="searchterm">
			  <html:text name="workdiaryForm" style="color:gray" onfocus="notext(this);"  onblur="addtext(this)" property="searchterm" size="25"/>
			  </logic:equal>
			  <logic:notEqual value="请输入批示、内容" name="workdiaryForm" property="searchterm">
			  <html:text name="workdiaryForm"  onfocus="notext(this);"  onblur="addtext(this)" property="searchterm" size="25"/>	
			  &nbsp;
			  </logic:notEqual>
			  </logic:notEqual>
			  &nbsp;
			  <BUTTON name="bdel" class="mybutton"  onclick="serch();"><bean:message key="button.query"/></BUTTON>
			  </div>
			</td>
			</tr>
			</table>
			</td>
		</tr>
</table>
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable1">
		<tr>
			<td align="center" width="4.5%" class="TableRow" nowrap><input type="checkbox" name="qx" value="true"  title="全选" onclick="selAll(this)"></td>
			<td align="center" width="4.5%" class="TableRow" nowrap><bean:message key="column.operation"/></td>
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
					 			<td align="center" width="4.5%" class="TableRow" nowrap>
									<bean:write name="info" property="itemdesc"/>
								</td>
					 	</logic:notEqual>
					</logic:notEqual>
					
					<logic:equal value="p0115" name="fids">
						<logic:equal value="true" name="info" property="visible">
							<td align="center" width="4.5%" class="TableRow" nowrap>
								<bean:write name="info" property="itemdesc"/>
							</td>
						</logic:equal>
					</logic:equal>
					
				</logic:notEqual>
			</logic:iterate>
			<td align="center" width="4.5%" class="TableRow" nowrap>
				评论
			</td>
		</TR>
		<hrms:paginationdb id="element" name="workdiaryForm" sql_str="workdiaryForm.sql" table="" where_str="workdiaryForm.where" columns="workdiaryForm.column" order_by="workdiaryForm.orderby" pagerows="15" page_id="pagination" indexes="indexes">
			<bean:define id="p0100" name="element" property="p0100"/>
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
				LazyDynaBean bean = (LazyDynaBean)pageContext.getAttribute("element");
				String p0600 = (String)bean.get("p0100");
				p0600 = PubFunc.encryption(p0600);			         
          %>  
			<td align="center" class="RecordRow" nowrap>
			<logic:equal value="01" name="element" property="p0115">
					<hrms:checkmultibox name="workdiaryForm" property="pagination.select" value="true" indexes="indexes" />
					<INPUT type="hidden" id="cs<%=i %>" name="<%=i%>" value="${p0100}">
			</logic:equal>
			<logic:equal value="07" name="element" property="p0115">
					<hrms:checkmultibox name="workdiaryForm" property="pagination.select" value="true" indexes="indexes"/>
					<INPUT type="hidden" id="cs<%=i %>" name="<%=i%>" value="${p0100}">
			</logic:equal>
			</td>
			<td align="center" class="RecordRow" nowrap>&nbsp;
			<logic:equal value="01" name="element" property="p0115">
			<a href="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state=<%=a0 %>&p0100=<%=p0600%>">
			<bean:message key="kq.deration_details.edit"/></a>		
			</logic:equal>
			<logic:notEqual value="01" name="element" property="p0115">
			<logic:notEqual value="07" name="element" property="p0115">
			<a href="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=own&state=<%=a0 %>&p0100=<%=p0600%>">
			<bean:message key="label.view"/></a>	
			</logic:notEqual>	
			</logic:notEqual>
			<logic:equal value="07" name="element" property="p0115">
			<a href="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state=<%=a0 %>&p0100=<%=p0600%>">
			<bean:message key="kq.deration_details.edit"/></a>
			</logic:equal>&nbsp;
			</td>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist">
					<bean:define id="fid" name="info" property="itemid"></bean:define>
					<bean:define id="ftype" name="info" property="itemtype"/>
					<logic:notEqual value="p0100" name="fid">
						<logic:equal value="M" name="ftype">
							<td align="left" onmouseout="tt_HideInit();" onmouseover='outContent("<%=p0600%>","${fid}");' class="RecordRow" nowrap>
								<bean:define id="fidtext" name="element"  property="${fid}"/>
								<%=fidtext%>
							</td>
						</logic:equal>
						<logic:notEqual value="M" name="ftype">
							<logic:notEqual value="p0115" name="fid" >
								<td align="left" class="RecordRow" nowrap>
									
									<logic:equal value="nbase" name="fid" >
										<hrms:codetoname name="element" codeid="@@" codeitem="codeitem" codevalue="nbase" scope="page"/>
										<bean:write name="codeitem" property="codename"/>&nbsp;  
									</logic:equal>
									<logic:equal value="b0110" name="fid" >
										<hrms:codetoname name="element" codeid="UN" codeitem="codeitem" codevalue="b0110" scope="page"/>
										<bean:write name="codeitem" property="codename" />&nbsp; 
									</logic:equal>
									<logic:equal value="e0122" name="fid" >
										<hrms:codetoname name="element" codeid="UM" codeitem="codeitem" codevalue="e0122" scope="page"/>
										<bean:write name="codeitem" property="codename" />&nbsp; 
									</logic:equal>
									<logic:equal value="e01a1" name="fid" >
										<hrms:codetoname name="element" codeid="@K" codeitem="codeitem" codevalue="e01a1" scope="page"/>
										<bean:write name="codeitem" property="codename" />&nbsp; 
									</logic:equal>
									<logic:notEqual value="nbase" name="fid" >
										<logic:notEqual value="b0110" name="fid" >
											<logic:notEqual value="e0122" name="fid" >
												<logic:notEqual value="e01a1" name="fid" >
													<bean:write name="element" property="${fid}" filter="false"/>&nbsp;
												</logic:notEqual>
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
								</td>
							</logic:notEqual>
							<logic:equal value="p0115" name="fid" >
								<logic:equal value="true" name="info" property="visible">
								<td align="center" class="RecordRow" nowrap>
									<hrms:codetoname name="element" codeid="23" codeitem="codeitem" codevalue="p0115" scope="page"/>
									<bean:write name="codeitem" property="codename"/>&nbsp; 
								</td>
								</logic:equal>
							</logic:equal>
						</logic:notEqual>
					</logic:notEqual>
			</logic:iterate>
			<logic:notEqual value="01" name="element" property="p0115">
			<logic:notEqual value="07" name="element" property="p0115">			
			<td align="center" class="RecordRow" nowrap>&nbsp;
    			<img src="/images/view.gif" border=0 onclick="view('<%=p0600%>');">                 
	    	</td>
	    	</logic:notEqual>
	    	</logic:notEqual>
	    	<logic:equal value="01" name="element" property="p0115">
			<td align="center" class="RecordRow" nowrap>&nbsp;
    			&nbsp;                
	    	</td>
	    	</logic:equal>
	    	<logic:equal value="07" name="element" property="p0115">
			<td align="center" class="RecordRow" nowrap>&nbsp;
    			&nbsp;                
	    	</td>
	    	</logic:equal>
			</tr>
			<%i++;%>
		</hrms:paginationdb>
	</table>
	<table width="100%" class="RecordRowP">
		<tr>
			<td width="40%" valign="bottom"  class="tdFontcolor" nowrap>
				<bean:message key="label.page.serial" />
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td width="60%" align="left" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="workdiaryForm" property="pagination" nameId="browseRegisterForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>
	</table>
	<table width="80%" align="center">
	<tr>
	<td align="center">
	<!-- 
	<BUTTON name="add" class="mybutton"  onclick="addiary();" ><bean:message key="performance.workdiary.no.wt.info"/></BUTTON>&nbsp;
	 -->
	<BUTTON name="sub" class="mybutton"  onclick="subs();" ><bean:message key="button.appeal"/>	</BUTTON>&nbsp;
	<BUTTON name="sub" class="mybutton"  onclick="dels();" ><bean:message key="button.delete"/>	</BUTTON>&nbsp;
	<BUTTON name="selecrper" class="mybutton"  onclick="chaosong();return false;">抄送</BUTTON>&nbsp;
	<BUTTON name="bdel" class="mybutton" onclick="printExcel();">导出Excel</BUTTON>&nbsp;
    <BUTTON name="return" class="mybutton"  onclick="returnblack();" ><bean:message key="button.return"/></BUTTON>&nbsp;
	</td>
	</tr>
	</table>
</html:form>
	<script type="text/javascript" language="javascript">
<!--
var ymds=workdiaryForm.ymd.value;
if(ymds=="3"){
	showsjfw();
}
function printExcel(){
	var hashvo=new ParameterSet();
	hashvo.setValue("state",${workdiaryForm.state});
	hashvo.setValue("ymd",${workdiaryForm.ymd});
	//按时间段查询
	<logic:equal value="3" name="workdiaryForm" property="ymd">
			var startime=workdiaryForm.startime.value;
			var endtime=workdiaryForm.endtime.value;
			
			if(startime.length<1&&endtime.length<1){
				if(startime_old.length>0 || endtime_old.length>0){
					startime = startime_old;
					endtime = endtime_old;
				} else {
					alert("时间不能为空！");
					return;
				}
				
			}
		hashvo.setValue("startime",startime);
		hashvo.setValue("endtime",endtime);
	</logic:equal>
	//本月
	<logic:equal value="0" name="workdiaryForm" property="ymd">
		//hashvo.setValue("yearnum",${workdiaryForm.yearnum})
		//hashvo.setValue("monthnum",${workdiaryForm.monthnum})
	</logic:equal>
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,
		functionId:'2020050051'},hashvo);
}
function showFieldList(outparamters){
	var outName=outparamters.getValue("filename");
	//window.location.target="_blank";
	//window.location.href="/servlet/DisplayOleContent?filename="+outName;
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
function subs(){
	var str="";
	var strname="";
	var strvalue="";
	var state ="${workdiaryForm.state}";
	//var startime = "${workdiaryForm.startime}";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<document.workdiaryForm.elements.length;i++){
		if(document.workdiaryForm.elements[i].type=="checkbox"){
			if(document.workdiaryForm.elements[i].checked==true&&document.workdiaryForm.elements[i].name!="qx"){
				if(document.workdiaryForm.elements[i+1].value!="true"){
					str+=document.workdiaryForm.elements[i+1].value+"/";
					strname+=document.workdiaryForm.elements[i+1].name+",";
					strvalue+=document.workdiaryForm.elements[i+1].value+",";
				}
				
			}
		}
	}
	if(str.length==0){
		alert(SELECT_APP_LOG+"!");
		return;
	}else{
		var hashvo=new ParameterSet();
		hashvo.setValue("state",state);
		hashvo.setValue("strname",strname);
		hashvo.setValue("strvalue",strvalue);
		//hashvo.setValue("startime",startime);
		var request=new Request({method:'post',asynchronous:false,onSuccess:computeIsOk,functionId:'9020010010'},hashvo);
	    ///var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'9020010015'},null);
	}    
}
function computeIsOk(outparamters){
	var check=outparamters.getValue("check");
	var strname=outparamters.getValue("strname");
	var fillcheck=outparamters.getValue("fillcheck");
	if(getDecodeStr(check)=='ok'&&getDecodeStr(fillcheck)=='ok'){
		var hashvo=new ParameterSet();
		hashvo.setValue("strname",strname);
		var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'9020010015'},null);
		
	}else{
		if(getDecodeStr(check)!='ok'){
			alert(getDecodeStr(check));
		}else{
			alert(getDecodeStr(fillcheck));
		}
		
	}
}
function getSuperiorUser(outparamters){
	var curr_user="";
	if(outparamters.getValue("outname").length==1){
		curr_user=(outparamters.getValue("outname")[0].split(":"))[0];
		appiary(curr_user);
	}else if(outparamters.getValue("outname").length>1){
		var thecodeurl="/performance/workdiary/cat.jsp?outname="+outparamters.getValue("outname");
    	var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:220px;dialogWidth:330px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
       if(return_vo!=null && return_vo.length>=0){
       		appiary(return_vo);
       }
	} else{
		appiary(curr_user);
	}
}
function appiary(curr_user){
	if(confirm(APP_OK+"?")){
		workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_submit=link&curr_user="+curr_user;
   		workdiaryForm.submit();
	}
}
function dels(){
	var str="";
	for(var i=0;i<document.workdiaryForm.elements.length;i++){
		if(document.workdiaryForm.elements[i].type=="checkbox"){
			if(document.workdiaryForm.elements[i].checked==true&&document.workdiaryForm.elements[i].name!="qx"){
				if(document.workdiaryForm.elements[i+1].value!="true")
					str+=document.workdiaryForm.elements[i+1].value+"/";
			}
		}
	}
	if(str.length==0){
		alert(SELECT_DEL_LOG+"!");
		return;
	}else{
		if(confirm(DEL_INFO)){
	    	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_del=link";
    		workdiaryForm.submit();
    	}
    }
}
var startime="${workdiaryForm.startime}";
var endtime="${workdiaryForm.endtime}";
document.getElementsByName("startime").value=startime.substring(0,10);
document.getElementsByName("endtime").value=endtime.substring(0,10);
function chaosong(){
	window.selecteds="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=1;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"&&tablevos[i].name!="qx"){
	    	if(tablevos[i].checked){
	    		var csid=tablevos[i].name;
			    selecteds+=document.getElementById("cs"+csid.substring(csid.length-2,csid.length-1)).value+",";
	    	}
		}
    }
	if(selecteds==""||selecteds=="0"){
		alert("请选择要抄送项！");
		return;
	}else{
		var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating1=link`pri=0`chkflag=11`p0100="+selecteds;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    	
		// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	    if(/msie/i.test(navigator.userAgent)){
	    	var return_vo= window.showModalDialog(target_url, 'trainClass_win2', "dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
		    chaosong_ok(return_vo);
			return ;
		} else {
			var picker = new PersonPicker({
				multiple : true,
				text : "确定",
				titleText : "选择抄送人",
				isPrivExpression:false,
				callback :function(c){
					var idArray = [];
					for (var i = 0; i < c.length; i++) {
						var staffId = c[i].id;
						idArray.push(staffId);
					}
					chaosong_ok(idArray, '1');
				} 
			}, this);
			picker.open();	
		}
	}
}
function chaosong_ok(return_vo, bencrypt){
	if(return_vo!=null){
		var recordstr = "";
		for(var i=0;i<return_vo.length;i++){
			if(return_vo[i]!=null&&return_vo[i].length>0){
				recordstr+=return_vo[i]+"`";
			}
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",recordstr);
		hashvo.setValue("selecteds",selecteds);
		if(bencrypt){
			hashvo.setValue("bencrypt", bencrypt);
		}
		var request=new Request({method:'post',onSuccess:showResult,functionId:'9020010016'},hashvo);
	}
}

function showResult(outparamters){ 
   	 if(outparamters.getValue("result")=="success"){
   	 	alert("抄送已完成！");
   	 } else{
   	 	alert("抄送失败！！！您选中的重要报告已有抄送给"+outparamters.getValue("result").substring(0,outparamters.getValue("result").length-1)+"这些人了，很抱歉不能重复抄送！");
   	 }
}

//2016/1/27 wangjl 
function notext(dd){
	 dd.value="";
	 dd.style.color="black";
}
function addtext(ad){
	 if(ad.value==""){
		 ad.value="请输入批示、内容";
		 ad.style.color="gray";
		 }
}
-->
</script>