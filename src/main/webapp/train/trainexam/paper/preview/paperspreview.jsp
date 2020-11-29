<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="java.util.Date"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo"%>
<html>
<head>

</head>


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
				
			MyTestBo bo = new MyTestBo();	
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

.answertab
{
    border: 0px;
    cellpadding: 0px;
    cellspacing: 0px;
    width: 100%;
    margin: 0 10 10 10px;
}

.answertd
{
    width: 80%; 
    word-break: break-all; 
    word-wrap:break-word; 
    padding:10px;
}

.analysetd
{
    colspan: 2; 
    padding:10px; 
    padding-right: 20px; 
    padding-left: 10px;
}

.finalscore
{
    widthwidth: 20%; 
    nowrap: nowrap; 
    valign: top; 
    align: right;
    padding-right:10px;
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
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
<bean:define name="papersPreviewForm" property="flag" id="flag"/> 
<%int i=0; int m=0; Object t="";
	String imgpath = "'"+request.getSession().getServletContext().getRealPath("/UserFiles")+"'";
	if (SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
		imgpath = "'"+request.getSession().getServletContext().getResource("/").getPath()+"'";
	}
	imgpath=imgpath.replace("\\","&quot;");
%>
 <script type="text/javascript">
 var imgurl = getEncodeStr(<%=imgpath%>);
 </script>
<%if ("2".equals(flag.toString()) ) {%>
	<logic:notEqual value="-1" name="papersPreviewForm" property="r5413">
	<script type="text/javascript">
		var blurTimes = 0;
		window.focus();
		window.onblur=function windowBlur() {
			if (blurTimes != 0 && (blurTimes - 1) != ${papersPreviewForm.r5413})
                sAlert("光标焦点第"+blurTimes+"次离开试卷页面，如果累计达到${papersPreviewForm.r5413}次，系统将强制交卷！");
			blurTimes++;
			if ((blurTimes - 1) >= ${papersPreviewForm.r5413}) {
				testFinished=1;
				sAlert("光标焦点离开试卷页面已经达到" + blurTimes + "次，系统强制交卷！");
				noFinished('${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }');
				reflashPare();
			}
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
			
			<logic:equal value="5" name="papersPreviewForm" property="home">
				var tar = '<%=userView.getBosflag()%>';
				if(tar=="hl")//6.0首页
					window.opener.location.href="/templates/index/portal.do?b_query=link";
				else if(tar=="hcm")//7.0首页
					window.opener.location.href="/templates/index/hcm_portal.do?b_query=link";
			</logic:equal>
			<logic:notEqual value="5" name="papersPreviewForm" property="home">
				if(msg!=1){
					window.top.opener.location.href="/train/resource/myexam.do?b_query=link";
				}else{
					window.top.opener.location.href="/train/resource/mylessonsentrance.do?b_query=link";
				}
			</logic:notEqual>
			
			//window.opener = null;
			window.top.close();
			if(msg==1){
				var WsShell = new ActiveXObject('WScript.Shell'); 
				WsShell.SendKeys('{F11}');
			}
		}

		window.onbeforeunload = function() {
			if (testFinished != 1) {
				event.returnValue="您确定要退出考试吗？退出后系统将自动交卷!";
			}
		};
		
		window.onunload = function (){
			// 保存
			var map = saveAnswer2('${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }');
			if ('${papersPreviewForm.flag }' == '2') {
			
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
			reflashPare();
		}
	</script>
	<body style="margin:0px;padding:0px;" oncontextmenu=self.event.returnValue=false>
<%} else if ("5".equals(flag.toString())){%>
<body oncontextmenu=self.event.returnValue=false onselectstart="return false">
<!--  <body onbeforeunload="beforeFinished(event)"  onunload="noFinished('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }')">-->
<%} else { %>
<body oncontextmenu=self.event.returnValue=false onselectstart="return false">
<%} %>
<html:form action="/train/trainexam/paper/preview/paperspreview" styleId="form1"> 
<input type="hidden" id="typeid" value="0"/>
	<bean:define id="r5300" name="papersPreviewForm" property="r5300"></bean:define> 
	<bean:define name="papersPreviewForm" property="enableArch" id="enableArch"/>
	<table cellpadding="0" cellspacing="0" border="0" width="90%" align="center" class="RecordRow" style="border-collapse: collapse;">
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
		<logic:notEmpty name="papersPreviewForm" property="examdescribe">
		<tr>
			<td align="left">
				<span style="margin-left:20px;"><font size="2" color="#999999"><bean:write name="papersPreviewForm" property="examdescribe" filter="false"/></font></span>
			</td>
		</tr>
		</logic:notEmpty>
		
		<logic:notEmpty name="papersPreviewForm" property="plandesc">
		<tr>
			<td align="left">
				<span style="margin-left:20px;"><font size="2" color="#999999"><bean:write name="papersPreviewForm" property="plandesc" filter="false"/></font></span>
			</td>
		</tr>
		</logic:notEmpty>
		
		<logic:equal name="papersPreviewForm" property="flag" value="5">
			<tr>
				<td align="left" style="height: 30px;margin-top:20px;margin-20px;">
					<span style="margin-left:20px;"><font color="red" size="3">起止时间（${papersPreviewForm.startTime}至${papersPreviewForm.endTime }）&nbsp;&nbsp;</font></span>
					<span id="spanid" style="color:red;"></span>
				</td>
			</tr>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="2">
			<tr>
				<td align="left" style="height: 30px;margin-top:20px;margin-20px;">
					<span style="margin-left:20px;"><font color="red" size="3">起止时间（${papersPreviewForm.startTime}至${papersPreviewForm.endTime}）&nbsp;&nbsp;</font></span>
					<logic:notEqual value="2" name="papersPreviewForm" property="r5415">
					<span id="spanid" style="color:red;"></span>
					</logic:notEqual>
					<input type="hidden" name="endtime" id="endtimeid" value="${papersPreviewForm.endTime}"/>
				</td>
			</tr>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="4">
			<tr>
				<td align="left" style="height: 30px;margin-top:20px;margin-20px;">
					<span style="margin-left:20px;"><font color="red" size="3">&nbsp;&nbsp;得分&nbsp;<bean:write name="papersPreviewForm" property="score"/></font></span>
				</td>
			</tr>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="6">
			<tr>
				<td align="left" style="height: 30px;margin-top:20px;margin-20px;">
				<!-- <span style="margin-left:20px;"><font color="red" size="3">&nbsp;&nbsp;得分&nbsp;<bean:write name="papersPreviewForm" property="score"/></font></span> -->
					<span style="margin-left:20px;"><font color="red" size="3"><font color="red" size="3">起止时间（${papersPreviewForm.startTime}至${papersPreviewForm.endTime }）&nbsp;&nbsp;总共用时${papersPreviewForm.examTimeLength}分钟&nbsp;&nbsp;得分&nbsp;</font></span>
					<span id="totalScoreId"><font color="red" size="3">${papersPreviewForm.score}</font></span>
				</td>
			</tr>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="7">
			<tr>
				<td align="left" height="40">
					<span style="margin-left:20px;"><font color="red" size="3">起止时间（${papersPreviewForm.startTime}至${papersPreviewForm.endTime }）&nbsp;&nbsp;总共用时${papersPreviewForm.examTimeLength}分钟&nbsp;&nbsp;得分&nbsp;</font></span>
					<span id="totalScoreId"><font color="red" size="3">${papersPreviewForm.score}</font></span>
				</td>
			</tr>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="8">
			<tr>
				<td align="left" height="40">
					<span style="margin-left:20px;"><font color="red" size="3">考试已结束&nbsp;&nbsp;总共用时${papersPreviewForm.examTimeLength}分钟&nbsp;&nbsp;</font></span>
					<span id="totalScoreId"></span>
				</td>
			</tr>
		</logic:equal>
		
		
		<tr>
			<td>
			
				<table cellpadding="0" cellspacing="0" border="0" class="ListTableF" style="width: 100%;margin-bottom: 5px;">
				<hrms:paginationdb id="element" name="papersPreviewForm" sql_str="papersPreviewForm.strsql" table="" where_str="papersPreviewForm.strwhere" columns="papersPreviewForm.columns" order_by="papersPreviewForm.order_by" page_id="pagination" allmemo="1" pagerows="5000" indexes="indexes">
					<% m++;%>
					<bean:define name="element" property="r5200" id="r5200"/>
					<bean:define id="type_id" name="element" property="type_id"/>
					<bean:define name="element" property="r5208" id="r5208"/><!-- 正确主观答案 -->
					<bean:define name="element" property="r5209" id="r5209"/><!-- 正确客观答案 -->
					<bean:define name="element" property="o_answer" id="o_answer"/><!-- 用户客观题答案 -->
					<bean:define name="element" property="s_answer" id="s_answer"/><!-- 用户主观题答案 -->
					<bean:define name="element" property="s_answer" id="r5213"/><!-- 用户主观题答案 -->
					<bean:define name="element" property="r5210" id="r5210"/><!-- 试题分析-->
					<%if(!t.equals(type_id)){ t=type_id;m=1;i++;%>
					<tr>
						<td class="TableRow" style="padding: 10px;border-right:0px;">
							<%out.println(QuestionesBo.getTitle(type_id.toString(),r5300.toString(),i+"")); %>
						</td>
					</tr>
					<%} %>
					
					
					<logic:equal value="4" name="type_id">
					<tr>
						<td class="RecordRow" style="border-right:0px;border-bottom:0px;">
							<bean:define name="element" property="r5205" id="r5205"/>
							<bean:define name="papersPreviewForm" property="flag" id="flag"/>
							<div style="padding: 10px;padding-right:0px;width: 100%">
							第<%=m %>题(<bean:write name="element" property="r5213" filter="true"/>分)&nbsp;
							<%
								String tmpr5205 = r5205.toString();
								String str = "<input type='text' name='answer_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:240px;' class='text'>";
								if ("3".equals(flag.toString()) || "4".equals(flag.toString()) ||"6".equals(flag.toString()) || "7".equals(flag.toString()) || "8".equals(flag.toString())) {
									s_answer = s_answer == null ? "" : s_answer;
									String an[] = s_answer.toString().split("@,@");
									for (int j = 0; j < an.length; j++) {
										tmpr5205 = tmpr5205.replaceFirst("______", "<input type='text' value=\""+QuestionesBo.filterSpecialStr(an[j].toString())+"\" name='answer_"+r5200+"' disabled='disabled' id='answer_"+r5200+"_"+flag+"_answer' style='width:240px;' class='text'>");
										
									}
									str = "<input type='text' name='answer_"+r5200+"' disabled='disabled' id='answer_"+r5200+"_"+flag+"_answer' style='width:240px;' class='text'>";
									tmpr5205=tmpr5205.replaceAll("______",str);		
								} else {
									if(tmpr5205!=null&&tmpr5205.indexOf("______")!=-1)
									tmpr5205=tmpr5205.replaceAll("______",str);
								}
								out.println(QuestionesBo.toHtml(tmpr5205));
							%>
							
							
							</div>
							<%if ("3".equals(flag.toString()) || "7".equals(flag.toString())|| "4".equals(flag.toString()) || "6".equals(flag.toString())) { %>
							<%} %>
							<%if ("3".equals(flag.toString()) || "7".equals(flag.toString())) { %>
								<table class="answertab">
		    					<tr>
									<td class="answertd">
									<%if("3".equals(flag.toString()) && "1".equals(enableArch.toString())) { %>
									&nbsp;
									<%} else { %>
									<B>正确答案：</B>
									    <SCRIPT type="text/javascript"> 
									        <%if(r5208 != null && !r5208.equals("")){%>
									            document.write(T("<%=QuestionesBo.toHtml(r5208.toString()) %>"));
									        <%}else{%>
									            document.write(T("<%=QuestionesBo.toHtml(r5209.toString()) %>"));
									        <%}%>
									    </SCRIPT>
									<%} %>
									</td>
									<td class="finalscore">&nbsp;得分&nbsp;<input type="text" size="10" name="score_<%=r5200.toString() %>" id="score_<%=r5200.toString() %>" class="text4" onchange="valueChange(this,'<bean:write name="element" property="r5213" filter="true"/>')" value="<bean:write name="element" property="single_score" format="#.00"/>"/></td>
								</tr>
								<%if("3".equals(flag.toString()) && !"1".equals(enableArch.toString())) { %>
								<logic:notEmpty name="element" property="r5210">
                                <tr>
                                    <td class="analysetd">
                                        <B>试题分析：</B><%=QuestionesBo.toHtml(r5210.toString()) %>
                                    </td>
                                </tr>
                                </logic:notEmpty>
                                <%} %>
								</table>
						
							<%} %>
							<%if ("4".equals(flag.toString()) || "6".equals(flag.toString())) { %>
						
								<table class="answertab">
		    					<tr>
									<td class="answertd">
									<%if("4".equals(flag.toString()) && "1".equals(enableArch.toString())) { %>
									&nbsp;
									<%} else { %>
									<B>正确答案：</B><SCRIPT type="text/javascript"> 
									<%if(r5208 != null && r5208.toString().length() > 0){%>
										document.write(T("<%=r5208.toString() %>"));
									<%}else{%>
									document.write(T("<%=r5209.toString() %>"));
									<%}%>
									</SCRIPT>
									<%} %>
									</td>
									<td class="finalscore">&nbsp;得分&nbsp;<bean:write name="element" property="single_score"/>&nbsp;&nbsp;</td>
								</tr>
								<%if("4".equals(flag.toString()) && !"1".equals(enableArch.toString())) { %>
								<logic:notEmpty name="element" property="r5210">
                                <tr>
                                    <td class="analysetd">
                                        <B>试题分析：</B><%=QuestionesBo.toHtml(r5210.toString()) %>
                                    </td>
                                </tr>
                                </logic:notEmpty>
                                <%} %>
								</table>
						
							
							<%} %>
						</td>
					</tr>
						
					</logic:equal>
					<logic:notEqual value="4" name="type_id">
					<tr>
						<td class="RecordRow" style="padding: 10px;border-right:0px;border-bottom:0px;">
							
							<%if ("3".equals(flag.toString()) || "4".equals(flag.toString()) ||"6".equals(flag.toString()) ||"7".equals(flag.toString())) { %>								
								<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
								<%if (!bo.judge(o_answer.toString(),r5209.toString())){ %>
									<font color="red">
								<%}} %>
							<%} %>
							第<%=m %>题(<bean:write name="element" property="r5213" filter="true"/>分)&nbsp;
							<%if ("3".equals(flag.toString()) || "4".equals(flag.toString()) || "6".equals(flag.toString()) ||"7".equals(flag.toString())) { %>								
								<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
								<%if (!bo.judge(o_answer.toString(),r5209.toString())){ %>
									</font>
								<%}} %>
							<%} %>
							<bean:define name="element" property="r5205" id="r5205"/>
							<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
								<%out.println(QuestionesBo.toHtml(r5205.toString())); %>
							<%} else { 
								String tmpr5205 = r5205.toString();
								String str = "<input type='text' name='answer_o_"+r5200+"' id='answer_"+r5200+"_"+flag+"_answer' style='width:240px;' class='text'>";
								if ("3".equals(flag.toString()) || "4".equals(flag.toString()) ||"6".equals(flag.toString()) || "7".equals(flag.toString()) || "8".equals(flag.toString())) {
									o_answer = o_answer == null ? "" : o_answer;
									String an[] = o_answer.toString().split("@,@");
									
									
									for (int j = 0; j < an.length; j++) {
										tmpr5205 = tmpr5205.replaceFirst("______", "<input type='text' value='"+an[j]+"' name='answer_o_"+r5200+"' disabled='disabled' id='answer_"+r5200+"_"+flag+"_answer' style='width:240px;' class='text'>");
										
									}
									str = "<input type='text' name='answer_o_"+r5200+"' disabled='disabled' id='answer_"+r5200+"_"+flag+"_answer' style='width:240px;' class='text'>";
									tmpr5205=tmpr5205.replaceAll("______",str);		
								} else {
									if(tmpr5205!=null&&tmpr5205.indexOf("______")!=-1)
									tmpr5205=tmpr5205.replaceAll("______",str);
								}
								out.println(QuestionesBo.toHtml(tmpr5205));
							} %>						
							
							
						</td>
					</tr>
					</logic:notEqual>
			
					
					<logic:notEqual value="4" name="type_id">
					<%if("1".equals(type_id)||"2".equals(type_id)||"3".equals(type_id)){ %>
					<tr>
						<td class="RecordRow" style="padding-top: 1px;border-right:0px;border-bottom:0px;">
							<bean:define name="element" property="r5207" id="r5207"/>
							<div style="padding: 10px;padding-right:0px;width: 100%">
							<%if ("3".equals(flag.toString()) || "4".equals(flag.toString()) || "6".equals(flag.toString()) || "7".equals(flag.toString()) ) { %>
							<hrms:examquestionsoptions xml="${r5207}" name_id="answer_${r5200}" type_id="${type_id}" flag="${papersPreviewForm.flag}" disabled="true" answer="${o_answer}"></hrms:examquestionsoptions>
							</div>
								<table class="answertab">
		    					<tr>
									<td class="answertd">
								<%if (("3".equals(flag.toString()) || "4".equals(flag.toString())) && "1".equals(enableArch.toString())){ %>
								&nbsp;
								<%} else { %>	
									<B>正确答案：</B>
									<%if ("3".equals(type_id)) {%>
										<%if ("A".equalsIgnoreCase(r5208.toString())||"A".equalsIgnoreCase(r5209.toString())){ %>
											对
										<%} else if ("B".equalsIgnoreCase(r5208.toString())||"B".equalsIgnoreCase(r5209.toString())){ %>
											错
										<%} %>
										<!--此处加上判断多选题 如果是多选 则从r5208中取得答案 库中存的有点凌乱 此处暂且加上判断 显示出单选多选题的正确答案 以后再做具体整理-->
									<%}else if("2".equals(type_id) && r5208 != null && !r5208.equals("")){ //个别库多选答案存到了r5208%>
										<SCRIPT type="text/javascript"> document.write(T("<%=r5208.toString() %>"));</SCRIPT>
									<%} else {%>
										<SCRIPT type="text/javascript"> document.write(T("<%=r5209.toString() %>"));</SCRIPT>
						
									<%} 
									}%></td>
									<td class="finalscore"><%if ("7".equals(flag.toString())) { %>&nbsp;得分&nbsp;<input type="text" readonly="readonly" size="10" name="score_<%=r5200.toString() %>" class="text4" id="score_<%=r5200.toString() %>" onchange="valueChange(this,'<bean:write name="element" property="r5213" filter="true"/>')" value="<bean:write name="element" property="single_score"/>" format="#.00"/><%}else { %>&nbsp;得分&nbsp;<bean:write name="element" property="single_score"/><%} %></td>
								</tr>
								<%if((("3".equals(flag.toString())||"4".equals(flag.toString())) && !"1".equals(enableArch.toString()))||"7".equals(flag.toString())){ %>
								<logic:notEmpty name="element" property="r5210">
								<tr>
									<td class="analysetd">
                                       <B>试题分析：</B><%=QuestionesBo.toHtml(r5210.toString()) %>
                                    </td>
								</tr>
								</logic:notEmpty>
								<%} %>
								</table>
							<%}else if ("8".equals(flag.toString())){%>
								<hrms:examquestionsoptions xml="${r5207}" name_id="answer_${r5200}" type_id="${type_id}" flag="${papersPreviewForm.flag}" disabled="true" answer="${o_answer}"></hrms:examquestionsoptions>
							<%}else  { %>
							<div style="padding: 10px;padding-right:0px;width: 100%">
		    				<hrms:examquestionsoptions xml="${r5207}" name_id="answer_${r5200}" type_id="${type_id}" flag="${papersPreviewForm.flag}"></hrms:examquestionsoptions>
		    				</div>
		    				<%} %>
								

						</td>
					</tr>
					<%}else{ %>
					<tr>
						<td class="RecordRow" style="border-right:0px;border-bottom: 0px;">
		    				<textarea  rows="4" cols="12" style="width: 100%;height: 150px;<%if("8".equals(flag.toString())){ %>border:0px;<%}else{ %>border:solid ;border-width:0 0 1px 0;<%} %>padding-left: 10px;" name="answer_${r5200}"<%if ("3".equals(flag.toString()) || "4".equals(flag.toString()) || "6".equals(flag.toString()) || "7".equals(flag.toString())||"8".equals(flag.toString())) { %>  readonly="true"
		    					<%} %>id="answer_${r5200}_${papersPreviewForm.flag}_answer"><%if ("3".equals(flag.toString()) || "4".equals(flag.toString()) || "6".equals(flag.toString()) || "7".equals(flag.toString()) || "8".equals(flag.toString())) { out.println(QuestionesBo.toHtml(s_answer.toString()));} %></textarea>
		    				
		    				<%if ("3".equals(flag.toString()) || "7".equals(flag.toString())|| "4".equals(flag.toString()) || "6".equals(flag.toString())) { %>
							<%} %>
		    				<%if ("3".equals(flag.toString()) || "7".equals(flag.toString())){ %>
		    					<bean:define id="t_score" name="element" property="single_score" />
		    					<%if("7".equals(flag.toString())&&Double.parseDouble(t_score.toString())<1){t_score="";} %>
		    					<table class="answertab">
		    					<tr>
									<td class="answertd">
									<%if("3".equals(flag.toString()) && "1".equals(enableArch.toString())) { %>
										&nbsp;
									<%} else { %>
									    <B>正确答案：</B>
									    <%=QuestionesBo.toHtml(r5208.toString()) %>
									<%} %>
									</td><!-- <SCRIPT type="text/javascript"> document.write(T("//r5208.toString() "));</SCRIPT> -->
									<td class="finalscore">
									    &nbsp;得分&nbsp;
									    <input type="text" size="10" name="score_<%=r5200.toString() %>" 
									           id="score_<%=r5200.toString() %>" class="text4"
									           onchange="valueChange(this,'<bean:write name="element" property="r5213" filter="true"/>')" 
									           value="${t_score }"/>
									</td>
								</tr>
								<%if(("3".equals(flag.toString()) && !"1".equals(enableArch.toString())) ||"7".equals(flag.toString())){ %>
								<logic:notEmpty name="element" property="r5210">
								<tr>
									<td class="analysetd">
									    <B>试题分析：</B>
									    <%=QuestionesBo.toHtml(r5210.toString()) %>
									</td>
								</tr>
								</logic:notEmpty>
								<%} %>
								</table>
		    				<%} %>
		    				<%if (("4".equals(flag.toString()) && !"1".equals(enableArch.toString())) || "6".equals(flag.toString())){ %>
		    					<table class="answertab">
		    					<tr> 
									<td class="answertd">
									<%if("4".equals(flag.toString()) && "1".equals(enableArch.toString())) { %>
										&nbsp;
									<%} else { %>
									<B>正确答案：</B><SCRIPT type="text/javascript"> document.write(T("<%=r5208.toString() %>"));</SCRIPT>
									<%} %>
									</td>
									<td class="finalscore">&nbsp;得分&nbsp;<bean:write name="element" property="single_score" /></td>
								</tr>
								<%if("4".equals(flag.toString()) && !"1".equals(enableArch.toString())){ %>
								<logic:notEmpty name="element" property="r5210">
								<tr>
									<td class="analysetd">
									  <B>试题分析：</B><%=QuestionesBo.toHtml(r5210.toString()) %>
									</td>
								</tr>
								</logic:notEmpty>
								<%} %>
								</table>
		    				<%} %>
						</td>
					</tr>
					<%} %>
					
					</logic:notEqual>
					
				</hrms:paginationdb>
				</table>
			
			</td>
		</tr>
		</table>
		<table border="0" align="center">
		<tr><td height="35" id="buttonObj">
		<logic:equal value="1" name="papersPreviewForm" property="flag">
			<logic:equal value="1" name="papersPreviewForm" property="flag">
				<hrms:priv func_id="3238209">
				<input type="button" value="校验" class="mybutton" onclick="verify('${papersPreviewForm.r5300 }');"/>
			    </hrms:priv>
			</logic:equal>
			<object ID= "WebBrowser1" WIDTH= "0" HEIGHT= "0" CLASSID= "CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></object>
			<hrms:priv func_id="3238210">
			<input type="button" value='<bean:message key="button.print"/>' class="mybutton" onclick="printer('buttonObj');WebBrowser1.execwb(6,6);document.getElementById('buttonObj').style.display='';"/>
			</hrms:priv>
			<hrms:priv func_id="3238211">
			<input type="button" value='<bean:message key="button.export"/>' class="mybutton" onclick="exportWord('${papersPreviewForm.r5300}',imgurl);"/>
			</hrms:priv>
			<!-- <input type="button" value="设置" class="mybutton" onclick="WebBrowser1.ExecWB(8,1)"/> -->
			<logic:equal value="0" name="papersPreviewForm" property="returnId">
				<input type="button" value='<bean:message key="button.close"/>' class="mybutton" onclick="window.close();"/>
			</logic:equal>
			<logic:equal value="1" name="papersPreviewForm" property="returnId">
				<input type="button" value='<bean:message key="button.leave"/>' class="mybutton" onclick="returnqURL('/train/trainexam/paper/questiontype.do?b_add=link&r5300=${papersPreviewForm.r5300 }');"/>
			</logic:equal>
			<logic:equal value="3" name="papersPreviewForm" property="returnId">
			<input type="button" value='<bean:message key="button.leave"/>' class="mybutton" onclick="window.history.back();"/>
			</logic:equal>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="5">
			<input type="button" id="saveid" value="保存" class="mybutton" onclick="saveAll('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }');"/>
			<input type="button" id="submitid" value="交卷" class="mybutton" onclick="submitAnswer('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.r5000 }','1');"/>
			<logic:equal value="2" name="papersPreviewForm" property="returnId"><!-- 返回到自测考试中 -->
				<input type="button" value="返回" class="mybutton" onclick="returnSelfExam('${papersPreviewForm.r5000 }','${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }');"/>
			</logic:equal>
			<input type="hidden" name="tomodify" id="tomodify" value="/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300=${papersPreviewForm.r5300 }&exam_type=${papersPreviewForm.exam_type }&flag=3&returnId=4&r5000=${papersPreviewForm.r5000 }"/>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="3">
			<input type="button" value="保存" class="mybutton" onclick="saveScore('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }');"/>
			<logic:equal value="4" name="papersPreviewForm" property="returnId"><!-- 返回到自测考试中 -->
				<input type="button" value="返回" class="mybutton" onclick="returnToMyTest('${papersPreviewForm.r5000 }');"/>
			</logic:equal>
		</logic:equal>
		<logic:equal name="papersPreviewForm" property="flag" value="4">
			<logic:equal value="5" name="papersPreviewForm" property="returnId"><!-- 返回到自测考试中 -->
				<input type="button" value="返回" class="mybutton" onclick="returnToMyTest('${papersPreviewForm.r5000 }');"/>
			</logic:equal>
		</logic:equal>
		
		<logic:equal name="papersPreviewForm" property="flag" value="2">
			<input type="button" id="saveid" value="保存" class="mybutton" onclick="saveAll('${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }');"/>
			<input type="button" id="submitid" value="交卷" class="mybutton" onclick="submitAnswer('${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }','','0');"/>
			<!-- <logic:equal value="2" name="papersPreviewForm" property="returnId">返回到我的考试中
				<input type="button" value="返回" class="mybutton" onclick="returnSelfExam('${papersPreviewForm.r5000 }','${papersPreviewForm.flag }','${papersPreviewForm.plan_id }','${papersPreviewForm.r5300 }');"/>
			</logic:equal> -->
			<input type="hidden" name="tomodify" id="tomodify" value="/train/resource/myexam.do?b_query=link"/>
		</logic:equal>
		
		<logic:equal name="papersPreviewForm" property="flag" value="6">
			<logic:equal value="5" name="papersPreviewForm" property="returnId"><!-- 返回到自测考试中 -->
				<input type="button" value="返回" class="mybutton" onclick="returnToMyExam('/train/resource/myexam.do?b_query=link&first=no');"/>
			</logic:equal>
			<logic:equal value="0" name="papersPreviewForm" property="returnId"><!-- 返回到自测考试中 -->
				<input type="button" value="关闭" class="mybutton" onclick="javascript:window.close();"/>
			</logic:equal>
		</logic:equal>
		
		<logic:equal name="papersPreviewForm" property="flag" value="7">
			<logic:notEqual name="papersPreviewForm" property="marking" value="1">
				<logic:notEqual name="papersPreviewForm" property="marking" value="2">
			<input type="button" value="保存" class="mybutton" onclick="savePerScore('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.a0100 }','${papersPreviewForm.nbase }');"/>
			<input type="button" value="提交" class="mybutton" onclick="savePerScore2('${papersPreviewForm.flag }','${papersPreviewForm.paper_id }','${papersPreviewForm.r5300 }','${papersPreviewForm.a0100 }','${papersPreviewForm.nbase }');"/>
				</logic:notEqual>
			</logic:notEqual>
			<logic:equal value="4" name="papersPreviewForm" property="returnId"><!-- 返回到自测考试中 -->
				<input type="button" value="返回" class="mybutton" onclick="returnToMyExam('/train/trainexam/exam/student.do?b_query=return&planid=${papersPreviewForm.paper_id }');"/>
			</logic:equal>
		</logic:equal>
		
		<logic:equal name="papersPreviewForm" property="flag" value="8">
			<logic:equal value="5" name="papersPreviewForm" property="returnId">
				<logic:equal value="5" name="papersPreviewForm" property="home">
					<input type="button" value="返回" class="mybutton" onclick="window.parent.goPortal();"/>
				</logic:equal>
				<logic:notEqual value="5" name="papersPreviewForm" property="home">
				<!-- 返回到我的考试列表 -->
				<input type="button" value="返回" class="mybutton" onclick="returnToMyExam('/train/resource/myexam.do?b_query=link&first=no');"/>
				</logic:notEqual>
			</logic:equal>
		</logic:equal>
		</td></tr></table>
</html:form>
</body>
</html>
<script>
	<logic:equal value="5" name="papersPreviewForm" property="flag">
	<logic:empty name="papersPreviewForm" property="examtime">
		daojishi("spanid", 0*60, "1", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");
	</logic:empty>
	<logic:notEmpty name="papersPreviewForm" property="examtime">
		daojishi("spanid", ${papersPreviewForm.examtime}*60, "1", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.paper_id }\",\"${papersPreviewForm.r5300 }\")");
	</logic:notEmpty>
	</logic:equal>
	
	<logic:equal value="2" name="papersPreviewForm" property="flag">
	<logic:notEqual value="2" name="papersPreviewForm" property="r5415">
	<logic:notEmpty name="papersPreviewForm" property="over">
		var endt = ${papersPreviewForm.over};
		var times = 0;
		var realEndt= ${papersPreviewForm.over};
		var returnEndt=${papersPreviewForm.over};
		
		function dao() {
			endt = returnEndt;
			returnEndt = daojishi2("spanid", endt, "1", "forcSubmitPaper(\"${papersPreviewForm.flag }\",\"${papersPreviewForm.plan_id }\",\"${papersPreviewForm.r5300 }\")");
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
	     			if(xhr.readyState == 3){//状态3响应   
		      			var header = xhr.getAllResponseHeaders(); //获得所有的头信息         			  
		      			var ServerDate = new Date(xhr.getResponseHeader('Date')); //弹出时间，那么可以利用获得的时间做倒计时程序了。
		      			var endStr = document.getElementById("endtimeid").value;
		      			var twoend = endStr.split(" ");  
		      			var riqi = twoend[0].split("-");
		      			var ti = twoend[1].split(":");
		      			var endTime = new Date(riqi[0],parseInt(riqi[1],10)-1,riqi[2],ti[0],ti[1],0); 
		      			realEndt = parseInt((endTime - ServerDate)/(1000));
		      			if (returnEndt != realEndt) {
		      				//returnEndt = realEndt; //暂时先注释  有需要再放开 导致三十秒时间跳动的原因
		      			}
		      			//setTimeout("daojishi2('"+divId+"',"+only+",'"+type+"','"+actionMethod+"')", 1000);
	     			}  
 				}  
 				xhr.send(null); 
			} 
			
			if (returnEndt % 60 == 0) {
				var map = new HashMap();
				map.put("planid","${papersPreviewForm.plan_id }");
				Rpc({functionId:'2020030192',success:checkAjaxState},map);
			}
		}
		times = setInterval(dao,1000);
		
		function checkAjaxState(response) {
			var value=response.responseText;
			var map=Ext.decode(value);
			if (map.state == "06") {
				forcSubmitPaper("${papersPreviewForm.flag }","${papersPreviewForm.plan_id }","${papersPreviewForm.r5300 }");
			}
		}
	</logic:notEmpty>
	</logic:notEqual>
	</logic:equal>
	//屏蔽ctrl+c ctrl+v
	document.onkeydown=function key(){
		 if (event.ctrlKey==1){
			 e = event;
			if(document.all){
				k=e.keyCode;
			} else{
				k=e.which;
			}

//			if(k==86){
//				return false;
//			}

//			if(k==67){
//				return false;
//			}
		 }
	}

</script>