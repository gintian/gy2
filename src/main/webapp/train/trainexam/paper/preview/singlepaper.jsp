<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">
<!--
.fixedDiv21
{  
	height:300px;
	width:100%;
	BORDER-TOP: #C4D8EE 1pt solid;
	padding: 0px;
	margin: 0px;
}
.divborder{
	border-bottom: 1px solid;
}
-->
</style>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="./paperspreview.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;                             
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=fflag%>;
</script>
<bean:define id="flag" name="papersPreviewForm" property="flag" />
<%int i=0; int m=0; Object t="";%>
<%if ("2".equals(flag.toString()) ) {%>
	<logic:notEqual value="-1" name="papersPreviewForm" property="r5413">
	<script type="text/javascript">
		var blurTimes = 0;
		window.focus();
		if(blurTimes == 0){
			var url = location.href;
			var paraString = url.substring(url.lastIndexOf("?")+1,url.length).split("&");
			for(var i = 0; i < paraString.length; i ++) {
				if(paraString[i].substring(0,paraString[i].indexOf("="))=="blurTimes"){
					blurTimes=paraString[i].substring(paraString[i].indexOf("=")+1,paraString[i].length);
				}
			}
			
			if(typeof(blurTimes) == "undefined" || blurTimes == null)
				blurTimes = 0;
		}
			
		window.onblur=function windowBlur() {

			if (blurTimes != 0 && (blurTimes - 1) != ${papersPreviewForm.r5413})
			sAlert("光标焦点第"+blurTimes+"次离开试卷页面，如果累计达到${papersPreviewForm.r5413}次，系统将强制交卷！");
			
			blurTimes++;
			if ((blurTimes - 1) >= ${papersPreviewForm.r5413}) {
				testFinished=1;
				sAlert("光标离开本页面已经达到" + (blurTimes - 1) + "次，系统强制交卷！");
				setTimeout("sss()",2000); 
				
			}
		}

		function sss(){
			noFinished('${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }');
			reflashPare();
			}
	</script>
	</logic:notEqual>
<%} %>
<hrms:themes/>
<%if ("2".equals(flag.toString()) ) {%>
	<script type="text/javascript">
		function reflashPare() {
			var msg=0;
			var url = location.href;
			var paraString = url.substring(url.indexOf("?")+1,url.length).split("&");
			for(var i = 0; i < paraString.length; i ++) {
				if(paraString[i].substring(0,paraString[i].indexOf("="))=="msg"){
					msg=paraString[i].substring(paraString[i].indexOf("=")+1,paraString[i].length);
				}
			}
			if(msg!=1){
				window.top.opener.location.href="/train/resource/myexam.do?b_query=link";
			}else{
				window.top.opener.location.href="/train/resource/mylessonsentrance.do?b_query=link";
			}
			window.top.close();
			if(msg==1){
				var WsShell = new ActiveXObject('WScript.Shell'); 
				WsShell.SendKeys('{F11}');
			}
		}
		
		/**window.onbeforeunload = function() {
			if (testFinished != 1) {
				event.returnValue="您确定要退出考试吗？退出后系统将自动交卷!";
			}
		};**/
		
		/**window.onunload = function (){
			
	var map = saveAnswer('${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }');
	if (flag == '2') {
		map["paperState"] = "1";
		window.opener.reflash();
	}
	Rpc({functionId:'2020030183',success:noFinishedSubmitSaveSucc},map);
		};**/
		
		
		
		window.onbeforeunload = function() {
				if (testFinished != 1) {
					event.returnValue="您确定要退出考试吗？退出后系统将自动交卷!";
				}
		};
		
		window.onunload = function (){
			// 保存
			var map = saveAnswer2('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }');
			if ('${papersPreviewForm.flag }' == '2' && isNext == '0') {
				//map["paperState"] = "1";
				var parameter = map._getParameter("paperState");
				if (!parameter) {
					parameter = map._addParameter("paperState");
				}
				if (parameter){
					parameter.value = "1";
				}
				
			}
		
		
      		var request=new Request({method:'post',asynchronous:false,onSuccess:beforeunloadsuccfunc,functionId:'2020030183'},map);
		
			//window.opener.location.href="/train/resource/myexam.do?b_query=link";
		}
		
		function beforeunloadsuccfunc(outparamters){
			if (testFinished != 1) {
				reflashPare();
			}
		}
	</script>
	<body style="margin:0px;padding:0px;">
<%} else if ("5".equals(flag.toString())){%>
<body onbeforeunload="beforeFinished(event)"  onunload="noFinished('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }')">
<%} else { %>
<body>
<%} %>
<html:form action="/train/trainexam/paper/preview/paperspreview" styleId="form1"> 
<input type="hidden" id="typeid" value="1"/>
	<bean:define id="r5300" name="papersPreviewForm" property="r5300"></bean:define>               
	<table cellpadding="0" cellspacing="0" border="0" width="90%" align="center" class="complex_border_color" style="border-collapse: collapse;">
		<tr>
			<td align="center">
				<h2 style="margin-top:30px; "><bean:write name="papersPreviewForm" property="title" filter="true"/></h2>
			</td>
		</tr>
		
		<tr>
			<td align="right" style="padding-right: 20px;height: 30px;">
				考试时间：&nbsp;<bean:write name="papersPreviewForm" property="examtime" filter="true"/>分钟&nbsp;&nbsp;&nbsp;
				总分：&nbsp;<bean:write name="papersPreviewForm" property="examscore" filter="true"/>
			</td>
		</tr>
		<tr>
			<td align="left" style="padding-left: 20px;height: 30px;">
				<span><font color="red">起止时间（${papersPreviewForm.startTime}至${papersPreviewForm.endTime }）&nbsp;&nbsp;</font></span>
				<span id="spanid" style="color:red;"></span>
				<input type="hidden" id="endtimeid" name="endtime" value="${papersPreviewForm.endTime }">
			</td>
		</tr>
		<bean:define id="map" name="papersPreviewForm" property="questionMap" />
		<bean:define name="map" property="r5200" id="r5200"/>
		<bean:define name="map" property="s_answer" id="s_answer"/>
		<bean:define id="type_id" name="map" property="type_id" />
		
		<tr>
			<td class="TableRow" style="padding: 10px;border-right:0px;">
				<bean:write name="map" property="typeTitle" filter="false"/>
			</td>
		</tr>
		<logic:equal value="4" name="type_id">
		<tr>
			<td class="RecordRow" style="padding: 10px;border-right:0px;">
				<bean:define name="map" property="r5205" id="r5205"/>
				<bean:define name="papersPreviewForm" property="flag" id="flag"/>
				<%
					String tmpr5205 = r5205.toString();
					if(tmpr5205!=null&&tmpr5205.indexOf("______")!=-1) {
						//tmpr5205=tmpr5205.replaceAll("______","<input type='text' name='answer_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;border:0px;border-bottom:1px solid #000;;'>");
						s_answer = s_answer == null ? "" : s_answer;
									String an[] = s_answer.toString().split("@,@");
									
									
									for (int j = 0; j < an.length; j++) {
										tmpr5205 = tmpr5205.replace("______", "<input type='text' value='"+an[j]+"' name='answer_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>");
									}
									String str = "<input type='text' name='answer_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>";
									tmpr5205=tmpr5205.replaceAll("______",str);	
					
					}
					out.println(QuestionesBo.toHtml(tmpr5205));
				%>
				 
				
				&nbsp;&nbsp;
				<logic:equal value="1" name="papersPreviewForm" property="isSingle">
					<span id="spansingle" style="color:red;"></span>
				</logic:equal>
			</td>
		</tr>
		</logic:equal>
		<logic:notEqual value="4" name="type_id">
		<tr>
			<td class="RecordRow" style="padding: 10px;border-right:0px;">
				<bean:define name="map" property="r5205" id="r5205"/>
				<bean:define name="map" property="o_answer" id="o_answer"/>
				
				
				<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
					<%out.println(QuestionesBo.toHtml(r5205.toString())); %>
				<%} else { 
					String tmpr5205 = r5205.toString();
					String str = "<input type='text' name='answer_o_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>";
					if ("2".equals(flag.toString()) || "3".equals(flag.toString()) || "4".equals(flag.toString()) ||"6".equals(flag.toString()) || "7".equals(flag.toString()) || "8".equals(flag.toString())) {
						o_answer = o_answer == null ? "" : o_answer;
						String an[] = o_answer.toString().split("@,@");
						
						
						for (int j = 0; j < an.length; j++) {
							if ("2".equals(flag.toString())) {
								tmpr5205 = tmpr5205.replaceFirst("______", "<input type='text' value='"+an[j]+"' name='answer_o_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>");
							} else {
								tmpr5205 = tmpr5205.replaceFirst("______", "<input type='text' value='"+an[j]+"' name='answer_o_"+r5200+"' disabled='disabled' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>");
							}
							
						}
						if ("2".equals(flag.toString())) {
							str = "<input type='text' name='answer_o_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>";
						} else {
							str = "<input type='text' name='answer_o_"+r5200+"' disabled='disabled' id='answer_"+r5200+"_"+flag+"_answer' style='width:120px;' class='text'>";
						}
						tmpr5205=tmpr5205.replaceAll("______",str);		
					} else {
						if(tmpr5205!=null&&tmpr5205.indexOf("______")!=-1)
						tmpr5205=tmpr5205.replaceAll("______",str);
					}
					out.println(QuestionesBo.toHtml(tmpr5205));
				 }%>
				
				&nbsp;&nbsp;
				<logic:equal value="1" name="papersPreviewForm" property="isSingle">
					<span id="spansingle" style="color:red;"></span>
				</logic:equal>
			</td>
		</tr>
		<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
		<tr>
			<td class="RecordRow" style="padding: 10px;border-right:0px;">
				<bean:define name="map" property="r5207" id="r5207"/>
				<bean:define name="map" property="o_answer" id="o_answer"></bean:define>
   				<hrms:examquestionsoptions xml="${r5207}" name_id="answer_${r5200}" type_id="${type_id}" flag="${papersPreviewForm.flag}" answer="${o_answer}"></hrms:examquestionsoptions>
			</td>
		</tr>
		<%}else{ %>
		<tr>
			<td class="RecordRow" style="border-right:0px;">
   				<textarea rows="4" cols="12" style="width: 100%;height: 150px;border: 0px;padding-left: 10px;" name="answer_${r5200}" id="answer_${r5200}_${papersPreviewForm.flag}_answer }"><logic:notEmpty name="map" property="s_answer"><bean:write name="map" property="s_answer"/></logic:notEmpty></textarea>
			</td>
		</tr>
		<%} %>
		</logic:notEqual>
	</table>
	<center style="margin-top: 5px;">
		<logic:equal value="5" name="papersPreviewForm" property="flag">
			<logic:greaterThan value="1" name="papersPreviewForm" property="current">
				<input type="button" value="上一题" class="mybutton" onclick="paperPaging(${papersPreviewForm.current },-1,'${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.r5000 }');"/>
			</logic:greaterThan>
			<logic:greaterThan value="0" name="papersPreviewForm" property="current">
			<logic:lessThan value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				<input type="button" value="下一题" class="mybutton" onclick="paperPaging(${papersPreviewForm.current },1,'${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.r5000 }');" />
			</logic:lessThan>
			</logic:greaterThan>
			<logic:equal value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				<input type="hidden" name="tomodify" id="tomodify" value="/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300=${papersPreviewForm.r5300 }&exam_type=${papersPreviewForm.exam_type }&flag=3&returnId=4&r5000=${papersPreviewForm.r5000 }"/>
				<input type="button" value="交卷" class="mybutton" onclick="submitAnswer('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.r5000 }','1');"/>
			</logic:equal>
		</logic:equal>
		
		<logic:equal value="2" name="papersPreviewForm" property="flag">
			<logic:greaterThan value="1" name="papersPreviewForm" property="current">
				<input type="button" id="upid" value="上一题" class="mybutton" onclick="paperPaging(${papersPreviewForm.current },-1,'${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.r5000 }',blurTimes);"/>
			</logic:greaterThan>
			<logic:greaterThan value="0" name="papersPreviewForm" property="current">
			<logic:lessThan value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				<input type="button" id="nextid" value="下一题" class="mybutton" onclick="paperPaging(${papersPreviewForm.current },1,'${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.r5000 }',blurTimes);" />
			</logic:lessThan>
			</logic:greaterThan>
			<logic:equal value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				<input type="hidden" name="tomodify" id="tomodify" value="/train/resource/myexam.do?b_query=link"/>
				<input type="button" id="submitid" value="交卷" class="mybutton" onclick="submitAnswer('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','','0');"/>
			</logic:equal>
		</logic:equal>
	</center>
</html:form>
</body>
<script>
	<logic:equal value="5" name="papersPreviewForm" property="flag">
		<logic:notEmpty name="papersPreviewForm" property="examtime">
			daojishi("spanid", ${papersPreviewForm.remaintime}, "1", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");//交卷操作void;
		</logic:notEmpty>
		<logic:equal value="1" name="papersPreviewForm" property="isSingle">
			<logic:equal value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				daojishi("spansingle", ${map.r5211}, "3", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");//最后一题 不再跳到下一题  交卷按总时间提示
			</logic:equal>
			<logic:notEqual value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				daojishi("spansingle", ${map.r5211}, "3", "paperPaging(${papersPreviewForm.current },1,\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\",\"${papersPreviewForm.r5000 }\")");
			</logic:notEqual>
		</logic:equal>
	</logic:equal>
	<logic:equal value="2" name="papersPreviewForm" property="flag">
		
		var endt = ${papersPreviewForm.remaintime};
		var times = 0;
		var realEndt= ${papersPreviewForm.remaintime};
		var returnEndt=${papersPreviewForm.remaintime};
		
		function dao() {
			endt = returnEndt;
			returnEndt = daojishi2("spanid", endt, "1", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");
			if (endt <= 0) {
				clearInterval(times);
			}
			
			if (returnEndt % 30 == 0) {
				try {
					xhr = new XMLHttpRequest();
				} catch(e) {
					try {
						xhr = new ActiveXObject("Msxml2.XMLHTTP");
					} catch(e) {
						try {
							xhr = new ActiveXObject("Microsoft.XMLHTTP");
						} catch(e) {
							alert("sorry,your browser do not suport AJAX!");
	         				return false;
						}
					}
				}
	 			xhr.open('get', 'testServer.txt', true); //这里的testServer.txt，其实我没有创建，完全可以不需要这个文件，我们只是要时间罢了   
	 			xhr.onreadystatechange = function(){  
	     			if(xhr.readyState == 3){ //状态3响应   
		      			var header = xhr.getAllResponseHeaders(); //获得所有的头信息         			  
		      			var ServerDate = new Date(xhr.getResponseHeader('Date')); ;//弹出时间，那么可以利用获得的时间做倒计时程序了。
		      			var endStr = document.getElementById("endtimeid").value;
		      			var twoend = endStr.split(" ");  
		      			var riqi = twoend[0].split("-");
		      			var ti = twoend[1].split(":");
		      			var endTime = new Date(riqi[0],parseInt(riqi[1])-1,riqi[2],ti[0],ti[1],ti[2]); 
		      			realEndt = parseInt((endTime - ServerDate)/(1000));
		      			if (returnEndt != realEndt) {
		      				returnEndt = realEndt;
		      			}
	     			}  
 				}  
 				xhr.send(null); 
			} 
			
			if (returnEndt % 60 == 0) {
				var map = new HashMap();
				map.put("planid","${papersPreviewForm.paper_id }");
				Rpc({functionId:'2020030192',success:checkAjaxState},map);
			}
		}
		times = setInterval(dao,1000);
		function checkAjaxState(response) {
			var value=response.responseText;
			var map=Ext.decode(value);
			if (map.state == "06") {
				forcSubmitPaper("${papersPreviewForm.flag }","${papersPreviewForm.paper_id }","${papersPreviewForm.r5300 }");
			}
		}
		//daojishi("spanid", ${papersPreviewForm.remaintime}, "1", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");//交卷操作void;
		
		<logic:equal value="1" name="papersPreviewForm" property="isSingle">
			<logic:equal value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				daojishi("spansingle", ${map.r5211}, "3", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");//最后一题 不再跳到下一题  交卷按总时间提示
			</logic:equal>
			<logic:notEqual value="${papersPreviewForm.count}" name="papersPreviewForm" property="current">
				daojishi("spansingle", ${map.r5211}, "3", "paperPaging(\"${papersPreviewForm.current }\",1,\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\",\"${papersPreviewForm.r5000 }\")");
			</logic:notEqual>
		</logic:equal>
	</logic:equal>
</script>