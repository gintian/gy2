<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7;">
<link href='/ext/ext6/resources/ext-theme.css' rel='stylesheet' type='text/css'><link>
<link rel="stylesheet" type="text/css" href="/workplan/style/treegrid.css">
<%--<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />--%>
<link href="/workplan/style/workplan.css" rel="stylesheet" />
<link href="/workplan/style/task.css" rel="stylesheet" />

<link rel="stylesheet" type="text/css" href="/workplan/style/stars.css">
<script type="text/javascript" src="/workplan/js/global.js"></script>
<script type="text/javascript" src="/workplan/js/util.js"></script>
<script type="text/javascript" src="/workplan/js/biz.js"></script>
<script type="text/javascript" src="/workplan/js/prompt.js"></script>
<script type="text/javascript" src="/workplan/js/commuction.js"></script>
<script type="text/javascript" src="/workplan/js/stars.js"></script>
<script type="text/javascript" src="/components/personPicker/PersonPicker.js"></script>



<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//登陆人id
	//String loaderId = userView.getDbname()+userView.getA0100();
	//js文件获取loaderId需要这样
	//out.write("<SCRIPT language="+"'"+"JavaScript"+"'"+">var loaderId="+"'"+loaderId+"'"+";</SCRIPT>");
	//out.write("<SCRIPT language="+"'"+"JavaScript"+"'"+">var nn="+"'"+nn+"'"+";</SCRIPT>");
%>
<script type="text/javascript">
var form_id="taskForm";  //form_id,用于上传沟通附件
Ext.onReady(function() {
	//禁止后退键 作用于Firefox、Opera
	document.onkeypress = forbidBackSpace;
	//禁止后退键  作用于IE、Chrome
	document.onkeydown = forbidBackSpace;
	/*####################### 判断当前任务是否存在,若不存在直接抛出异常终止程序####################### */
	var temp=basic.biz.isHaveTask();
	if(temp==false){
		return;
	}
	/*####################### 加载任务及任务相关信息 ####################### */
	basic.biz.step1(); // 1、计划信息; 2、[load=init]; 3、进度条
	basic.biz.step2(); // 1、负责人; 2、参与人; 3、关注人; 4、父任务; 5、子任务
	basic.biz.step3(); // 1、待评价的任务; 2、动态展现的字段; 3、能否删除,取消,发布
	// 任务进展
	basic.biz.getProgress();
	if (document.getElementById("param.needEvaluate").value === "true") {
		basic.biz.tmp.needEvaluate = "true";
		document.getElementById("nowEvaluate").style.display = "inline-block";
	}
	initTextarea();//textarea自适应高度
	basic.biz.adjustStyle();//解决当任务名称过长（大概超过100个汉字时），页面样式错乱问题
	//绩效界面计入任务后,隐藏不用显示的元素
	if(performanceStr == "1"){
		document.getElementById("editTask").style.display = "none";
		document.getElementById("taskDecom").style.display = "none";
		/** 绩效进入时，进度条不隐藏 chent 20150923 start */
		//document.getElementById("taskProcss").style.display = "none";
		//document.getElementById("msgContent").style.display = "none";
		//document.getElementById("gtBtn").style.display = "none";
		/** 绩效进入时，进度条不隐藏 chent 20150923 end */
	}
	// 任务改成弹出层展现，frame(iframe_task)的高度自适应内容 lium
	var iframe_task = window.parent.document.getElementById("iframe_task");
	var piframe_task = window.parent.document.getElementById("piframe_task");
	var init = true;
	setInterval(function () {
		if(iframe_task){
			// 任务详情界面与工作计划页面高度进行比对，相差的绝对值大于5，则调整页面高度 chent 20160321 start
			var iframeHeight = document.body.scrollHeight;
			var mainHeight = window.parent.document.body.scrollHeight;
			if(Math.abs(iframeHeight - mainHeight) > 5) {
				iframe_task.style.height = Math.max(iframeHeight, mainHeight) + "px";
			}
			// 任务详情界面与工作计划页面高度进行比对，相差的绝对值大于5，则调整页面高度 chent 20160321 end
		}
		else if(piframe_task){
			piframe_task.style.height = Math.max(document.body.scrollHeight, window.parent.document.body.scrollHeight) + "px";
		}
		
		// 工作计划页面高度过高导致任务详情页面展现不全时，滚动条重置到0的位置 chent 20171206 start 
		if(init){
			var scrollPos = parent.Ext.getBody().getScrollTop(); // 滚动条位置
			var lastTop = basic.util.getAbsoluteLocation(Ext.getDom('last')).top; // 页面尾部元素位置
			if(scrollPos - lastTop >= 0){// 滚动条位置超过尾部元素时页面会显示空白，滚动条重置。
				parent.window.document.documentElement.scrollTop = 0;
				init = false;
			}
		}
		// 工作计划页面高度过高导致任务详情页面展现不全时，滚动条重置到0的位置 chent 20171206 end 
	}, 500);
});

</script>

<form id='taskForm' action="">

	<input type="hidden" id="param.concerned_bteam" value="${param["concerned_bteam"] }" />
	<input type="hidden" id="param.othertask" value="${param["othertask"] }" />
	<input type="hidden" id="param.recordId" value="${param["recordId"] }" />
	<input type="hidden" id="param.superiorEvaluation" value="${param["superiorEvaluation"] }" />
	<input type="hidden" id="param.p0700" value="${param["p0700"] }" />
	<input type="hidden" id="param.myP0700" value="${param["myP0700"] }" />
	<input type="hidden" id="param.p0800" value="${param["p0800"] }" />
	<input type="hidden" id="param.objectid" value="${param["objectid"] }" />
	<input type="hidden" id="param.p0723" value="${param["p0723"] }" />
	<input type="hidden" id="param.needEvaluate" value="${param["needEvaluate"] }" />
	<input type="hidden" id="param.returnurl" value="${param["returnurl"] }" />
	<input type="hidden" id="param.taskreturnurl" value="${param["taskreturnurl"] }" />
	<input type="hidden" id="param.fromflag" value="${param["fromflag"] }" />
	<input type="hidden" id="param.personCF" value="${param["personCF"] }" />
	<input type="hidden" id="param.isCanFill" value="${param["isCanFill"] }" />
	<input type="hidden" id="param.period_type" value="${param["period_type"] }" />
	<input type="hidden" id="p0837s" />
	<input type="hidden" id="p0841s" />
	<jsp:include page="prompt_box.jsp"></jsp:include>
	<%
	    String p0700 = request.getParameter("p0700");
	    String p0723 = request.getParameter("p0723");
	    String objectid = request.getParameter("objectid");
	    String fromflag=request.getParameter("fromflag")!=null?request.getParameter("fromflag"):"";
	    String taskreturn_url="";
	    int length=0;
	    String performance = request.getParameter("performance");
	    performance = performance==null?"":performance;
	    out.write("<SCRIPT language="+"'"+"JavaScript"+"'"+">var performanceStr="+"'"+performance+"'"+";</SCRIPT>");
		String returnurl=request.getParameter("returnurl");
		returnurl = returnurl == null ? "" : returnurl;
		String taskreturnurl= request.getParameter("taskreturnurl");
		taskreturnurl = taskreturnurl == null ? "" : taskreturnurl;
		if(!"".equals(taskreturnurl)){
			String[] temp=taskreturnurl.split(",");
		    length=temp.length;
		    if(length>1){//把第一位之后的p0800再放回到url的taskreturnurl中
			   for(int i=0;i<temp.length-1;i++){
				 taskreturn_url=taskreturn_url+","+temp[i];
			   }
			   taskreturnurl="/workplan/plan_task.do?br_task=link&p0700="+p0700+"&p0800="+temp[temp.length-1]+"&objectid="+objectid+"&p0723="+p0723+"&fromflag="+fromflag+"&taskreturnurl="+taskreturn_url;
			}
		 }
		 String planUrl="";
		 if (returnurl!=null || returnurl.length()>0){
			planUrl = com.hrms.frame.codec.SafeCode.decode(returnurl);
		 } 
		String taskplanUrl =taskreturnurl+"&returnurl="+returnurl;
	%>
	<%-- 任务报批，批准按钮 --%>
	<div class="obviousBtn">
		<a id="transit" href="javascript:basic.biz.transit()"><img id="transit_img" /></a>
		
		<a id="nowEvaluate" href='javascript:document.getElementById("taskEvaluation").click()'>
			<img src="/workplan/image/pingjia.png" />
		</a>
	</div>
	
	<div class="hj-rwbj-wzm-all" style="margin-top:30px;">
		<div class="hj-wzm-rwbj-table">
			<%-- ########################## 计划描述 ########################## --%>
			<div class="hj-zm-rwbj-one" style="height:50px;padding:0;width:100%;position:relative">
				<dl>
					<dt><a href="javascript:void(0);"><img id="planPhoto" class="img-circle" style="width:50px;height:50px;" /></a></dt>
					<div style="float:left;width:70%;">
						<dd style="font-size:21px;width:90%;font-family:微软雅黑;">
							<div id="p0801" load="init"  style="float:left;max-width:480px;font-size:21px!important;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;"></div>
							<span id="taskChangedDesc" style="line-height:31px;color:#FFF;font-size:11px;"></span>	
						</dd>
						<dd style="margin-top:0px;width:70%;font-size:12px;"><font id="planDesc"></font></dd>
					</div>
					<%-- 上级给我评价的分值 --%>
					<div id="evaluationScore" style="position:absolute;top:0px;right:0px;margin:3px 3px 0px 0px;width: 100px;height:17px;"></div>
				</dl>
				<div class="hj-zm-rwbj-one-a" style="top:28px;">
					<style type="text/css">.hj-zm-rwbj-one-a font {display:inline-block;}</style>
					<div style="color:#FFF;margin-left:18px;font-size:11px;">
						<font load="init" id="p0835" edit="none"></font><font id="percentSign" style="display:none;">%</font>
						<font load="init" id="p0809" edit="none" style=""></font>
					</div>
				</div>
			</div>

			<%-- ########################## 任务按钮 ########################## --%>
			<div>
				<div class="hj-zm-rwbj-two-top" id="taskBtns">
<!-- 					<a href="javascript:basic.biz.releaseTask()" style="display:none;" id="releaseTask">发布任务</a> -->
					<span style="color:#999; float: left; margin-left:15px;">
							任务于
							<font id="create_time" load="init"></font>
							由
							<font id="create_fullname" load="init" style="color:#555;"></font>
							创建
						</span>
					<a href="javascript:basic.biz.edit()" id="editTask" style="display:none;">编辑任务</a>
					<a href="javascript:basic.biz.revert()" id="endEdit" style="display:none;">取消编辑</a>
					<a href="javascript:basic.biz.save()" id="saveTask" style="display:none;">保存</a> 
					<a href="javascript:basic.biz.cancelOrDelete('cancel')" style="display:none;" id="cancelTask">取消任务</a>
					<a href="javascript:basic.biz.cancelOrDelete('delete')" style="display:none;" id="delTask">删除任务</a>
					<a href="javascript:basic.biz.cancelOrDelete('delDeptTask')" style="display:none;"  id="delDeptTask">移出部门计划</a>
					<%
						if (!"".equals(taskplanUrl)&&length>1) {
					%>
					   <a href=<%=taskplanUrl %>  id="goBack" style="display:none" visible="true">返回&nbsp;&nbsp;&nbsp;</a>
					<%
					    }else if(planUrl==""){
					%> 
					  
					<%
					   }else{
					%>
					  <a href=<%=planUrl %>  id="goBack" style="display:none" visible="true">返回</a>
					<%
					}
					%>
				   </div>
			</div>

			<%-- ########################## 任务明细 ########################## --%>
			<div class="hj-zm-rwbj-three" style="float:left;">
				<table width="100%">
					<col width="60">
					<col>
					<%--					任务描述--%>
					<tr>
						<%--<td class="labelForTextarea"  id="taskdesc"></td>--%>
						<td colspan='2'><font load="init" id="p0803" type="textarea" style="margin-left:-60px;"></font><input id="p0803s" type="hidden"></td>
					</tr>
<%--					<tr>--%>
<%--						<td id="ptCaption" style="color:#999;"></td>--%>
<%--						<td><a id="ptName" style="width:400px;display:block;text-overflow:clip;overflow:hidden;white-space:nowrap;"></a></td>--%>
<%--					</tr>--%>
<%--					<tr>--%>
<%--						<td colspan="2" style="color:#999;">--%>
<%--							任务于--%>
<%--							<font id="create_time" load="init"></font>--%>
<%--							由--%>
<%--							<font id="create_fullname" load="init" style="color:#555;"></font>--%>
<%--							创建--%>
<%--						</td>--%>
<%--					</tr>--%>
					<tr style="height:66px;">
						<td>负责人</td>
						<td id="director" class="staff">
							<div id="director-display" class="staffDisplay"></div>
							<div style="float:left;height:66px;">
								<a href="javascript:void(0)" id="director-addBtn" onclick="basic.biz.pickStaff(this)">
									转给他人负责
								</a>
							</div>
						</td>
					</tr>
					<tr style="height:66px;">
						<td>任务成员</td>
						<td id="member" class="staff" style="white-space:normal">
							<div id="member-display" class="staffDisplay"></div>
							<div style="float:left;height:66px;">
								<a href="javascript:void(0)" id="member-addBtn" onclick="basic.biz.pickStaff(this)">
									添加任务成员
								</a>
							</div>
						</td>
					</tr>
					<tr>
						<td>时间安排</td>
						<td>
							<span>
								<font load="init" id="p0813" type="date"></font>	
							</span>
								<img src="/workplan/image/workplantime.bmp" width="15px" height="15px" id="editStartTime" style="display:none" plugin="datetimeselector"  inputname="开始时间" format="Y.m.d"/>
							<font id="char_zhi"></font>
							<span>
								<font load="init" id="p0815" type="date"></font>
							</span>
								<img src="/workplan/image/workplantime.bmp" width="15px" height="15px" id="editEndTime" style="display:none" plugin="datetimeselector"  inputname="结束时间" format="Y.m.d"/>
						</td>
					</tr>
					<tr>
						<td id="tasktype"></td>
						<td><span load="init" id="p0823"></span></td>
					</tr>
<%--					<tr>--%>
<%--						<td class="labelForTextarea"  id="taskdesc"></td>--%>
<%--						<td><font load="init" id="p0803" type="textarea"></font></td>--%>
<%--					</tr>--%>
					<tr>
						<td colspan="2">
							<input type="checkbox" id="toEvaluate" onclick="basic.biz.toggleEvaluate(this)" />待评价的任务
						</td>
					</tr>
					<tr id="rankContainer">
						<td colspan="2">
							<div style="border-bottom:1px solid #D5D5D5;height:1px;"></div>
							<table class="rankContainer-table" border="0" cellpadding="0" cellspacing="0">
								<col width="20">
								<col>
								<tr>
									<td align="right"  id="taskrank"></td><td><font id="rank"></font>&nbsp;%</td>
								</tr>
								<tr>
									<td align="right" class="labelForTextarea"  id="taskEvaluateStandard"></td>
									<td><font load="init" id="p0841"></font></td>
								</tr>
							</table>
						</td>
					</tr>
					<tbody id="dynamicFieldRegion"></tbody>
				</table>
			</div>
			
			<%-- ########################## 子任务 ########################## --%>
			<div class="hj-zm-rwbj-four" id="taskDecom" style="border-top:1px solid #D5D5D5;"><!-- overflow:hidden; ie下会引起位置偏移，导致边线看不见 -->
					<h4 style="height:36 px;line-height:36px;font-size:16px;font-family:微软雅黑;padding:10px 0 0 10px;float:left;">任务分解</h4>
	<%--				父任务--%>
				<div style="float:right; padding:20px 0 0 10px;">
					<span id="ptCaption" style="color:#999;"></span>
					<a id="ptName" style="width:400px;text-overflow:clip;overflow:hidden;white-space:nowrap; "></a>
				</div>	
				<div style="clear:both;"></div>
				<table width="100%" border="0" cellpadding="0" cellspacing="0" id="subtask" style="text-align:left;border-collapse:collapse;">
					<col width="50%">
					<col width="14%">
					<col width="36%">
				</table>
				
				<div class="hj-zm-four-tianjia"><!-- overflow:hidden; ie下会引起位置偏移，导致边线看不见 -->
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr height="30">
							<td width="40%" style="border-left:0px #C5C5C5 dashed;">
								<input type="text" id="add-subtask-name" placeHolder="添加子任务" class="add-sub-task1" style="width:90%" maxlength="250"
									desc="子任务名称" validator="notnull" tip="任务名称不能为空" />
							</td>
							<td width="16%" style="border-left:1px #C5C5C5 dashed;">
								<div prompt="prompt-box" id="subtask-director" override="replace" edit="always" class="prompt-box-edit-always"
										content="content-add-director" store="store-add-director" placeHolder="placeholder-add-director"
										onclick="basic.biz.pickSubTaskCyr(this)">
									
									<input type="hidden" desc="子任务负责人" id="store-add-director" />
									<div id="content-add-director">
										<div id="ext-gen1031" class="director" onmouseleave="basic.util.hideDelSpan(this)" onmouseover="basic.util.showDelSpan(this)">
<div id="director-name" class="director-name" style=""></div>
<div class="director-del" onclick="basic.util.delSelectedItem(this)" itemid="" style="top: 782px; left: 281px; display: none;">×</div>
</div>
									</div>
								</div>
							</td>
							<td width="17%" style="border-left:1px #C5C5C5 dashed;">
								<input type="text"  id="add-subtask-p0813" name="startTimea" placeHolder="开始时间" value="开始时间"  class="add-sub-task2"/>
							</td>
							<td width="3%" align="center" style="border-right:1px #C5C5C5 dashed;">
								<img src="/workplan/image/workplantime.bmp" width="15px" height="15px" plugin="datetimeselector"  inputname="startTimea" format="Y.m.d"/>
							</td>
							<td width="17%" >
								<input type="text" id="add-subtask-p0815" name="endTime" placeHolder="结束时间" value="结束时间" class="add-sub-task3"/>
							</td>
							<td width="3%" align="center" style="border-right:1px #C5C5C5 dashed;">
								<img src="/workplan/image/workplantime.bmp" width="15px" height="15px" plugin="datetimeselector"  inputname="endTime" format="Y.m.d"/>
							</td>
							<td width="4%" style="border-left:1px #C5C5C5 dashed;">
								<input type="button" value="+" class="hj-zm-rwbj-four-jh" id="subTaskBtn" onclick="basic.biz.addSubTask()" />
							</td>
						</tr>
					</table>
					<script type="text/javascript">initDocument();</script>
				</div>
			</div>
			
			<%-- ########################## 任务关注人 ########################## --%>
			<div id="follower" class="staff" style="overflow:hidden;">
				<label style="float:left;display:inline-block;">关注任务的</label>
				<span style="margin:0 0px 0px 5px;float:left;">
					<a href="javascript:void(0)" onclick="basic.biz.pickStaff(this)" id="follower-addBtn" style="margin-top:0px;" >
						<img src="/workplan/image/ait.jpg" title="添加任务关注人员" />
					</a>
				</span>
				<div id="follower-display" class="staffDisplay" style="margin-left:20px;"></div>
			</div>

			<%-- ########################## 标签 ########################## --%>
			<div id="questLog" style="position:relative;">
				<div class="bh-clear"></div>
				<div style="margin-bottom:10px;">
					<div class="hj-wzm-six-top" style="width:100%;">
						<a id="taskProgress" class="hj-wzm-six-top-a" href="javascript:basic.biz.getProgress()" show="true">任务进展</a>
						<a id="taskEvaluation" href="javascript:void(0)" onclick="basic.biz.getEvaluation()" show="false">任务评价</a>
						<a id="operationlog"  href="javascript:void(0)" onclick="basic.biz.operationLog()"  show="false">操作日志</a>
					</div>
					<div class="hj-wzm-five-jdt" id="processBarContainer" >
						<div class="hj-zm-five-jdt-left" id="taskProcss" style="margin-left:-20px; margin-top:28px;">
							<jsp:include page="draggable_progress_bar.jsp"></jsp:include>
						</div>
					</div>
				</div>
				<div class="bh-clear"></div>
				<div id="tongzhi" style="margin-bottom:50px;width:100%;margin-left:-10px;position:relative;" ></div>
				<input id="score" type="hidden" /><%-- 任务评价时打分用 --%>
				<div id="last"></div><%-- 页面结尾元素 --%>
			</div>
		</div>
	</div>
</form>
<%-- ########################## 计算字符串在页面中占据的宽度 ########################## --%>
<div id="strWidth" style="position:absolute;visibility:hidden;padding:0px;margin:0px;"></div>
<script type="text/javascript">
<%--performancess = <%=performance%>;--%>
//performancess.now = function(){};
</script>
<script type="text/javascript" src="/components/dateTimeSelector/dateTimeSelector.js"></script>