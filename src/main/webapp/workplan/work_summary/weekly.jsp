<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7;">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<link href='/ext/ext6/resources/ext-theme.css' rel='stylesheet' type='text/css'><link>
<!--  <hrms:themes /> 7.0css -->
<%--<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />--%>
<link rel="stylesheet" type="text/css" href="/workplan/style/treegrid.css">
<link href="/components/personPicker/PersonPicker.css" rel="stylesheet" type="text/css"/>
<link href="/workplan/style/stars.css" rel="stylesheet" />
<link href="/workplan/style/style.css" rel="stylesheet" />

<style>
<!--
.marginLeft{margin-left: 25px;margin-bottom: 10px; font-family:"微软雅黑";  font-size:14px;}
.hide-overflow-xy{overflow-x:hidden;overflow-y:hidden;}
.textareaCSS{
	resize:none;
    width:100%;
	overflow-x:hidden;overflow-y:hidden;
	margin-left:25px;
	border-bottom: none;border-left: none;border-right: none;border-top:1px solid #d5d5d5;
	font-family:"微软雅黑"; line-height:22px; font-size:14px; letter-spacing:1px;
	}
	
.hj-wzm-gzjh-spanstyle{float:left;margin-left:10px;}<%-- 给.hj-wzm-gzjh-spanstyle加样式,解决其展示问题(附件展示wusy) --%>
.hj-wzm-five-dinwei—right{
	background:#FFF;border:1px #D5D5D5 solid;
	width:80px;
	position:relative;top:-15px;left:130px;z-index:2;
	z-index: 20;position: absolute;
	top:770px;left: 965px;
	font-family: fantasy;}
	
.addCommunicationDiv {
margin:20px 0 0 29px;width:97%;padding-bottom:20px;
}

a{
cursor: pointer;
}
#tab a:visited {
	COLOR: #838383 !important; TEXT-DECORATION: none;font-size: 12px
}
.month-width{width:30px;}

/* 团队样式 */
#more_page .hj-wzm-tdzb-three h2{color:#747474;font-family:"微软雅黑"; font-size:16px;}
#more_page .hj-wzm-tdzb-three p{margin-top:10px;height:18px; line-height:18px;}
#more_page .hj-wzm-tdzb-three p a{font-family:"微软雅黑"; font-size:14px;}
#more_page .hj-wzm-tdzb-three table{margin-top:10px;}
#more_page .hj-wzm-tdzb-three table{color:#A7A7A7;}
#more_page .hj-wzm-tdzb-three table tr{height:36px; line-height:36px;}
#more_page .hj-wzm-tdzb-three table tr .hj-wzm-tdzb-td{padding-left:10px;}
#more_page .hj-wzm-tdzb-three table tr td a img{width:24px;height:24px;}
#more_page .hj-wzm-tdzb-three table tr td a img{width:24px;height:24px;}
#more_page .hj-wzm-tdzb-three table tr td{border-bottom:1px #D5D5D5 dashed;}
-->
</style>
<%
	String iscookie = request.getParameter("iscookie");
	String planType = request.getParameter("type");//个人或部门总结
%>
<html:form action="/workplan/work_summary" styleId="upForm">
	<div class="hj-wzm-zb-all" >
    <div class="hj-wzm-table-all">   
    
     <%-- 成员列表  --%>
     <div class="hj-wzm-all-right" style="position:absolute;right:8px; display:none;" id="rightDiv">
     			<a id="hideRightDiv"  href="javascript:void(0)"  onclick="hideRightDiv()" style="position:absolute;right:5px;top:-30px">
            		<img src="/module/system/questionnaire/images/directingright.png" width="25px;"height="25px;"  title="隐藏人力地图"/>
            	</a>
            	<dl id="hjrightdl" class="hj-right-dl">
                	<dt>
                	<% if(!"org".equals(planType)){  %>
                		<a href="javascript:void(0)" onclick="showperdetail('','','','','0')"><%-- 个人总结 --%>
                	<%} else{ %>
                		<a href="javascript:void(0)" onclick="showperdetail('','','','','2')">
                				<%-- 部门总结 --%>
                		<%} %>
                			<img class="img-circle" src="${workSummaryForm.photo}" />
                		</a>
                	</dt>
                    <dd style="margin-top:15px;margin-left:2px;width: 72px" class="hj-zm-name">
<%--                    	<a title="${workSummaryForm.a0101}" href="javascript:void(0)" onclick="showperdetail('','','','','0')">--%>
<%--                    		<span id="mapa0100">${workSummaryForm.a0101}</span>--%>
<%--                    	</a>--%>

						<div class="hj-wzm-right-wgzd" style="width:100px; height:32px;border-bottom:0px #D5D5D5 solid;line-height:24px;text-align:center; margin-left:5px; ">
		                    <div style="display:block; position:absolute; z-index:9999; margin-left:3px;margin-top:-2px;">
		                	  <a href="###" id="returnId" title="切换到列表显示" style="display:none" onclick="returnBefore();" >
		                	     <img src="/workplan/image/list.png">
		                	  </a>
		                	</div> 
		                	&nbsp;&nbsp;&nbsp;&nbsp;
		                	<a id="showselectmaplist" onclick="display('selectmaplist','showselectmaplist');">我关注的</a> 
		                	<img id="maparrow" style="cursor: pointer;" onclick="display('selectmaplist','showselectmaplist');" src="/workplan/image/baijiant.jpg" />
	                	</div> 
                    </dd>
                    
                </dl>
<%--                <div class="hj-wzm-right-wgzd" >--%>
<%--                    <div style="display:block; position:absolute; z-index:9999; margin-left:3px;margin-top:-2px;">--%>
<%--                	  <a href="###" id="returnId" title="切换到列表显示" style="display:none" onclick="returnBefore();" >--%>
<%--                	     <img width="28px" height="28px" src="/workplan/image/list.png">--%>
<%--                	  </a>--%>
<%--                	</div> --%>
<%--                	<a id="showselectmaplist" onclick="display('selectmaplist','showselectmaplist');">我关注的</a> <img id="maparrow" style="cursor: pointer;" onclick="display('selectmaplist','showselectmaplist');" src="/workplan/image/baijiant.jpg" />--%>
<%--                </div>   --%>
               <%-- 地图下拉 --%>
               <div class="hj-wzm-one-dinwei dropdownlist"  id="selectmaplist" style="display: none;cursor: pointer;width:154px;">
                    <ul>
                    </ul>
                </div>
                 <div class="hj-wzm-right-xshang" align="center" id="xshangjpg" style="display: none;"><a href="javascript:mapchangenum(-1)"><img src="/workplan/image/xshang.png"/></a></div>
                <div id="backSuperDiv" align="center" style="display: none">
                <a href="###" onclick="backSuper()">返回上级 </a></div>
                <div class="hj-wzm-right-dllb" id="personmapdiv">
              
                </div>
                <div class="hj-wzm-right-xxia" align="center" id="xxiajpg"><a href="javascript:mapchangenum(1)"><img src="/workplan/image/xxia.png" /></a></div>
            </div>
            
      <div class="hj-wzm-all-left" id="leftDiv">
      	<a id="showRightDiv"  href="javascript:void(0)"  onclick="showRightDiv()" style="position:absolute;right:5px;top:5px; display:none;">
            		<img src="/module/system/questionnaire/images/directingleft.png" width="25px;" height="25px;" title="显示人力地图"/>
            </a>
         <div class="hj-wzm-one">
                <div class="hj-wzm-one-left" id='menubar'>
                    <a href="javascript:display('typelist','summarytype');" id="summarytype" style="text-align: right;width: 60px;margin-right: 30px">
                    <span id='typetitle'></span>&nbsp;<img  onclick="display('typelist','summarytype');" src="/workplan/image/jiantou.png" /></a>
                    <a href="javascript:display('yearlist','summaryyear');" id="summaryyear" style="text-align: right;width: 60px;display: none">
                    <span  id='yeartitle'>${workSummaryForm.year}</span>年&nbsp;<img onclick="display('yearlist','summaryyear');" src="/workplan/image/jiantou.png" />
                    </a>
                    <a href="javascript:display('monthlist','summarytype');" id="summarymonth" style="text-align: right;width: 80px;">
                    <span id="timetitle">${workSummaryForm.year}</span>年<span id='monthtitle'>${workSummaryForm.month}</span>月&nbsp;<img onclick="display('monthlist','summarytype');" src="/workplan/image/jiantou.png" /></a>
                    <html:hidden styleId="week" name="workSummaryForm" property="week" />
                    <html:hidden styleId="summaryTypeJson" name="workSummaryForm" property="summaryTypeJson" />
                    <html:hidden styleId="p0100" name="workSummaryForm" property="p0100" />
                    <html:hidden styleId="weeknum" name="workSummaryForm" property="weeknum" />
                    <html:hidden styleId="ishr" name="workSummaryForm" property="ishr" />
                    <html:hidden styleId="isemail" name="workSummaryForm" property="isemail" />
                  	<html:hidden styleId="type" name="workSummaryForm" property="type" />
                    <html:hidden styleId="month" name="workSummaryForm" property="month"/>
                    <html:hidden styleId="year" name="workSummaryForm" property="year"/>
                    <html:hidden styleId="cycle" name="workSummaryForm" property="cycle"/>
                    <html:hidden styleId="isLeader" name="workSummaryForm" property="isLeader"/>
                    <html:hidden styleId="p0115" name="workSummaryForm" property="p0115"/>
                  <%-- 
                     <html:hidden styleId="p011501" name="workSummaryForm" property="p011501" />
                      <input type="hidden" id="visibleId" value="4"/> 
                   --%>
                    <html:hidden styleId="p011503" name="workSummaryForm" property="p011503" />
                    <html:hidden styleId="visibleId" name="workSummaryForm" property="scope" />
                    <html:hidden styleId="score" name="workSummaryForm" property="score" />
                    <html:hidden styleId="p0113" name="workSummaryForm" property="p0113" />
                    <html:hidden styleId="e0122" name="workSummaryForm" property="e0122" />
                    <html:hidden styleId="b01ps" name="workSummaryForm" property="b01ps" />
                    <html:hidden styleId="deptdesc" name="workSummaryForm" property="deptdesc" />
                    <html:hidden styleId="can_edit" name="workSummaryForm" property="can_edit" />
                    <html:hidden styleId="isself" name="workSummaryForm" property="isself" />

                    <html:hidden styleId="yearListStr" name="workSummaryForm" property="yearListStr" />                    
                    <html:hidden styleId="a0100" name="workSummaryForm" property="a0100" />
                    <html:hidden styleId="nbase" name="workSummaryForm" property="nbase" />
                    <html:hidden styleId="user_a0100" name="workSummaryForm" property="user_a0100" />
                    <html:hidden styleId="user_nbase" name="workSummaryForm" property="user_nbase" />
                    <html:hidden styleId="nbaseA0100" name="workSummaryForm" property="nbaseA0100" />
                    <html:hidden styleId="belong_type" name="workSummaryForm" property="belong_type" />
                    <html:hidden styleId="returnurl" name="workSummaryForm" property="returnurl" />
                    <html:hidden styleId="zhouzj" name="workSummaryForm" property="zhouzj" /><!-- 周总结，是否启用周总结 chent 20161205 add -->
                    <html:hidden styleId="zhouzjpx" name="workSummaryForm" property="zhouzjpx" /><!-- 周总结，培训需求字段。空 为没有设置 chent 20161205 add -->
                    <html:hidden styleId="personCycleFunction" name="workSummaryForm" property="personCycleFunction" /><!-- 个人填报权限 haosl add -->
                    <html:hidden styleId="orgCycleFunction" name="workSummaryForm" property="orgCycleFunction" /><!-- 部门填报权限 haosl add -->

                    <input type="hidden" id="iscookie" value="<%=iscookie %>" />
                    <input type="hidden" id="haveleader" value="" />
                    <input type="hidden" id="num_map" value="1" />
                    <input type="hidden" id="pagenum" value="1" />
                   
                  
                     <html:hidden styleId="maptype" name="workSummaryForm" property="maptype" />

<%--                    <input type="hidden" id="a0100" value="" />--%>
<%--                    <input type="hidden" id="nbase" value=""/>
                    <input type="hidden" id="user_a0100" value="" />
                    <input type="hidden" id="user_nbase" value=""/>  --%>
                    <input type="hidden" id="e01a1" value=""/> 
                    <html:hidden styleId="weekstart" name="workSummaryForm" property="weekStart" />
                    <html:hidden styleId="weekend" name="workSummaryForm" property="weekEnd" />
                </div>
                  	 <div class="hj-wzm-one-right" id="btn_return" style="width:80px;float:right;display:none;">
                    <a href="javascript:void(0);" onclick="returnUrl()" >返回</a>
                </div>
                  	
                <div class="hj-wzm-one-right" id="weeks" style="float:right;width:320px;">
                    <a href="###" onclick="checkweek('1')" >第一周</a>
                    <a href="###" onclick="checkweek('2')" >第二周</a>
                    <a href="###" onclick="checkweek('3')" ><%--    <img src="/workplan/image/123.png" /> --%> 第三周</a>
                    <a href="###" onclick="checkweek('4')" >第四周</a>
                    <a href="###" style = "display: none" id='fiveweek' onclick="checkweek('5')">第五周</a>
                </div>
              
                <div class="hj-wzm-one-right" id="quaters" style="display:none;float:right;width:320px;">
                    <a href="###" onclick="checkweek('1')" >第一季度</a>
                    <a href="###" onclick="checkweek('2')" >第二季度</a>
                    <a href="###" onclick="checkweek('3')" >第三季度</a>
                    <a href="###" onclick="checkweek('4')" >第四季度</a>
                </div>
                <div class="hj-wzm-one-right" id="halfyears" style="display:none;float:right;width:160px;">
                   <a href="###" onclick="checkweek('1')" >上半年</a>
                   <a href="###" onclick="checkweek('2')" >下半年</a>
                </div>

                <div class="hj-wzm-clock dropdownlist"  id="monthlist" style="display: none;">
                    <ul style="text-align:center;" >
                        <span style="color:#549FE3;">
                        <a s href="javascript:yearchange(-1);display('monthlist','summarytype');"><img style="margin-bottom: 3px;" src="/workplan/image/left2.gif" /></a>
                        <span id='myeartitle'>${workSummaryForm.year}</span>年 
                        <a href="javascript:yearchange(1);display('monthlist','summarytype');"><img style="margin-bottom: 5px;" src="/workplan/image/right2.gif" /></a>
                        </span>
                    </ul>
                    <ul id="months">
                        <li ><a href="###" onclick="hidemonth(2,'1',1)" >1月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'2',2)" >2月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'3',3)" >3月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'4',4)" >4月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'5',5)" >5月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'6',6)" >6月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'7',7)" >7月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'8',8)" >8月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'9',9)" >9月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'10',10)" >10月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'11',11)" >11月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'12',12)" >12月</a></li>
                    </ul>
                </div>
                <div class="hj-wzm-one-dinwei dropdownlist"  id="typelist" style="display: none;cursor: pointer;">
                   <ul></ul>
                </div>
                <div class="hj-wzm-one-dinwei dropdownlist"  id="yearlist" style="display: none;cursor: pointer;">
                    <ul>
                    </ul>
                </div>
            
            </div>
        <div id = "person_page">
        <div class="hj-wzm-zb-two">
            <dl >
                <dt><a href="javascript:void(0);" style="cursor:default"><img id="user_photo" class="img-circle" src="${workSummaryForm.user_photo}" /></a>
                <dd >
                  <div style="height:20px;margin-top:2px;font-size:18px!important;">
                   <span  style="display:inline;font-size:18px!important;" name="showdeptdesc">${workSummaryForm.deptdesc}</span>
                   <span id='user_name' style="font-size:18px!important;" >${workSummaryForm.user_a0101}</span>
                  <span id="summarydesc" style="font-size:18px!important;"></span>
        	    	<span style="font-size:18px!important;">工作总结</span>
                  </div>
                  <div style="height:20px; margin-bottom:2px;">
                  	 <span style="color:#fff;font-size:12px;font-family:'宋体';" id="submitDate"></span> 
                  	
                  </div>
               </dd>
            </dl>
            <div class="hj-wzm-zb-four-wtj" ><a  style="cursor: default" href="###"><font id="state" style="color:#FFF;margin-left:0px;font-size:12px;"></font></a>            
            </div>
          <div style="float: right;margin-top: 28px;margin-right: 2px">
          <a href="###" id="approve"  onclick="approveSummary()" style="display:none;color:#fff;font-size:12px;font-family:'宋体';" ><img src="/workplan/image/pizhun.png"></a> 
          <a href="###" id="reject"  onclick="rejectWin()" style="display:none;color:#fff;font-size:12px;font-family:'宋体';" ><img src="/workplan/image/tuihui.png"></a> 
          <a href="###" id="showpingjia" onclick="show_tongzhi('pingjia')" style="display:none;color:#fff;font-size:12px;font-family:'宋体';" ><img src="/workplan/image/pingjia.png"></a> 
          </div>
            <span style="float: right;margin-top: 1px;" id="starshow1"></span>
        </div>
      <div class="hj-wzm-zb-three" style="display:none;">
       	<div class="hj-wzm-zb-three-top" id="tasktypes" style="display:none;">
       	  <a href="###" id="showTaskBtn" style="float:right" onclick="showtask(false);">展开</a>  
          <a href="javascript:void(0)" onclick="getMyWorkTaskList('2');showtask(true)" style="float:right">参与的任务</a>
          <a href="javascript:void(0)" onclick="getMyWorkTaskList('1');showtask(true)" style="float:right">负责的任务</a>
          <a href="javascript:void(0)" onclick="getMyWorkTaskList('0');showtask(true)" style="float:right">所有任务</a>
        </div>
        <%--style="display:none;"
          <a href="###" id="showTaskBtn" style="float:right" onclick="showtask(false);">展开</a>
          --%>
        <div id="taskdetail" style = "display: none">
	        <table width="100%" border="0" cellpadding="0" cellspacing="0" id="planTab">
	          
	        </table>
        </div>

        </div>
        
        <div class="hj-wzm-zb-fove" id="editWorkSummary" style="display:none;">
            <div class="hj-wzm-zb-fove-top">
                <p><a href="javascript:void(0)" onclick="displayPlan('thisWorkPlan');"><font style="font-size:14px; color:#529FE5;">本期工作计划 </font><img style='margin-bottom:4px;' id='thisWorkPlanImg' src="/workplan/image/jiantou_up.png" /></a></p>
                <textarea class="hj-wzm-zb-fove-gzzj" readonly="readonly" style="display:inline;background-color:#EDEDEE;" id="thisWorkPlan"></textarea>
            </div>
           <div class="hj-wzm-zb-fove-top">
            	<p><a href="javascript:void(0)" onclick="displayPlan('thisWorkSummary');"><font style="font-size:14px;color:#529FE5;">本期工作总结 </font><img style='margin-bottom:4px;' id='thisWorkSummaryImg' src="/workplan/image/jiantou_up.png" /></a></p><%-- haosl update 此处font标签没有结束标签，导致ie下一对问题产生  --%>
                <textarea class="hj-wzm-zb-fove-gzzj" id="thisWorkSummary">${workSummaryForm.thisWorkSummary}</textarea>
            </div>
            <div class="hj-wzm-zb-fove-top" style= "margin-bottom: 10px;">
            	<p><a href="javascript:void(0)" onclick="displayPlan('nextWorkSummary');"><font style="font-size:14px;color:#529FE5;">下期工作计划 </font><img style='margin-bottom:4px;' id='nextWorkSummaryImg' src="/workplan/image/jiantou_up.png" /></a></p>
                <textarea class="hj-wzm-zb-fove-gzzj" id="nextWorkSummary">${workSummaryForm.nextWorkSummary}</textarea>
            </div>
           
       
        </div>
        <div id='zhouzjdiv' style="border:0px;display: none;"></div><!-- 周总结,渲染表格区域 chent 20161205 add border:1px solid #c5c5c5;-->
        <div class="hj-wzm-zb-fove" id="notEditSummary" style="display:none">
		
		  <div class="hj-wzm-six-bottom-er" style="height:auto;">
                    <p><a style="float:left;margin-left:25px;margin-bottom:10px;" href="javascript:void(0)" onclick="displayPlan('thisPlan');"><font style="font-size:14px; color:#529FE5;">本期工作计划 </font><img style='margin-bottom:4px;' id='thisPlanImg' src="/workplan/image/jiantou_up.png" /></a></p>
                    <textarea id="thisPlan" class="textareaCSS" readonly="readonly"
                      style="width:95%;background-color:#EDEDEE;"></textarea>
                    
            </div>
            <div class="hj-wzm-six-bottom-er" style="height:auto;">
            		<p class="marginLeft"><span style='color:#529FE5;cursor:pointer;' onclick="displayPlan('thisSummary');" style='position: absolute;'>本期工作总结 <img style='margin-bottom:4px;' id='thisSummaryImg' src="/workplan/image/jiantou_up.png" /></span></p>
            		<textarea id="thisSummary" class="textareaCSS" readonly="readonly"
            		  style="width:95%;">${workSummaryForm.thisWorkSummary}</textarea>
            		
            </div>
            <div class="hj-wzm-six-bottom-er" style="height:auto;">
            		<p class="marginLeft"><span style='color:#529FE5;cursor:pointer;' onclick="displayPlan('nextPlan');" style='position: absolute;'>下期工作计划 <img style='margin-bottom:4px;' id='nextPlanImg' src="/workplan/image/jiantou_up.png" /></span></p>
            		<textarea id="nextPlan" class="textareaCSS" readonly="readonly"
            		style="width:95%;">${workSummaryForm.nextWorkSummary}</textarea>
            </div>
        </div>
                	 <%-- 选人菜单 --%>
       		 <div class="hj-wzm-five-dinwei dropdownlist" id="personlist" style="z-index: 20;position: absolute;top:760px;left: 100px;display: none">
                	<div class="hj-five-top">
                    	<input type="text"  
                    	       id="searchinput"
                    	       title="<bean:message key='workplan.summary.personlist'/>" 
                    	       class="hj-five-din-ss" style="color: #a1acb8; margin-left: 10px;" 
                    	       value="<bean:message key='workplan.summary.personlist'/>" 
                    	       onkeyup="searchperson(this)" 
                    	       onfocus="if(this.value=='<bean:message key='workplan.summary.personlist'/>'){this.value=''};"/>
                    </div>
                    <ul id="personul" style="width:320px">
               
                    </ul>
                </div>
          <div id="visibleList" class="hj-wzm-one-dinwei dropdownlist" style="display: none;z-index: 30;position: absolute;width:100px;">
                 	<ul>
                 	    <li><a href="###" style="float:none; width:60px;margin:0;padding-top:0;" onclick="changeVisible('4')" >上级可见</a></li>
                 	    <li><a href="###" style="float:none; width:60px;margin:0;padding-top:0;" onclick="changeVisible('2')" >本部门可见</a></li>
                 	    <li><a href="###" style="float:none; width:60px;margin:0;padding-top:0;" onclick="changeVisible('1')" >本单位可见</a></li>
                 	    <li><a href="###" style="float:none; width:60px;margin:0;padding-top:0;" onclick="changeVisible('3')" >完全公开</a></li>
                    </ul>
                 </div>
                
                 <div id="summarylist" class="hj-wzm-one-dinwei dropdownlist" style="display: none;z-index: 1000;position: absolute;width:160px;">
                 	<ul>
                 	    <li id="timesumplan"><a href="###"  style="float:none; width:130px;margin:0;padding-top:0;" onclick="collectWorkPlan();" >汇总期间计划</a></li>
                 	    <li id="timesummary"><a href="###" style="float:none; width:130px;margin:0;padding-top:0;" onclick="collectSummary('','person')" >汇总下属个人总结</a></li>
                 	    <li id="timesumdepar"><a href="###" style="float:none; width:130px;margin:0;padding-top:0;" onclick="collectSummary('','org')" >汇总下属部门总结</a></li>
                 	    <li id="timesum" onmouseover="display('durationlist','timesum');"><a href="###"  style="float:none; width:130px;margin:0;padding-top:0;" >按期间汇总<img style="margin-left: 2px" onmouseover="display('durationlist','timesum');" src="/workplan/image/you-jt.png" /></a></li>
                    </ul>
                 </div>
                 <div id="durationlist" class="hj-wzm-one-dinwei dropdownlist" style="display: none;z-index: 31;position: absolute;width:130px;">
                 	<ul>
                 	    <li><a href="###" style="float:none; width:100px;margin:0;padding-top:0;" onclick="collectSummary('1','time')" >汇总工作周报</a></li>
                 	    <li><a href="###" style="float:none; width:100px;margin:0;padding-top:0;" onclick="collectSummary('2','time')" >汇总工作月报</a></li>
                 	    <li><a href="###" style="float:none; width:100px;margin:0;padding-top:0;" onclick="collectSummary('3','time')" >汇总季度总结</a></li>
                    </ul>
                 </div>
         <div class="hj-wzm-five-top" id="tools" style="width:100%;padding:0;border-bottom:1px solid #d5d5d5;display: block;">
             <a href="javascript:exportExcel();" id="daochu"  style="margin-left:0" title="导出"><img src="/images/daochu.png" /></a>
       		 <a href="javascript:display('summarylist','autosummary');" id="autosummary"  title="根据工作任务快速生成工作总结"><img id="autosummarybtn" src="/workplan/image/jiahao.png" /></a><%--class="hj-wzm-five-a" --%>
             <a  id="edit_button" style="display: none" href="javascript:editSummary()"><img title="修改工作总结" src="/workplan/image/edit.png" /></a>
             <a  href="javascript:void(0)"  onclick="addFollower(this)"><img id="ait" title="添加工作总结关注人" src="/workplan/image/ait.png" /></a>
             <input id='publishbtn' type="button" class="hj-wzm-five-fabu" style="cursor: pointer;" value="发布" onclick="publishSummary()"/>
             <input id='savebutton' type="button" style="margin-right:10px;cursor: pointer;" class="hj-wzm-five-fabu" value="保存" onclick="saveSummary(true)"/>
             <a href="javascript:display('visibleList','visibleA');" id="visibleA" class="hj-wzm-five-a2"><img id="visiblePhotos" onclick="display('visibleList','visiblePhotos');" src="/workplan/image/suo.jpg" />&nbsp;<span id="visibleRange" ></span>&nbsp;<img id="visiblePhotox" onclick="display('visibleList','visiblePhotox');" src="/workplan/image/jiantou.png" /></a>
            
        </div>
             <%-- 选关注的人 --%>
        <div class="hj-wzm-five-bottom bh-space" id="photolist" style="width:100%;height:auto!important;height:100px; ">
                    
        </div>
      	<div id="focusafterclear" class="bh-clear"'></div>
        <%-- 提醒写周报 --%>
        <div class="hj-wzm-zb-fove" id="remind" style="display:none">
			<div class="hj-wzm-six-bottom-er">
            	<dl>
                	<dt></dt>
                    <dd class="hj-wzm-six-dd">总结还未提交！<br/> <a onclick="fontgrey(this);sendEmail('person','one','','');" style="font-family: '微软雅黑';font-size: 18px"> 提醒写总结</a></dd>
                </dl>
            </div>
        </div>
        <div class="hj-wzm-six" style="width:100%;margin:0 auto;margin-bottom: 15px">
            	<div class="hj-wzm-six-top" style="border-bottom:1px solid #d5d5d5;">
                	<a href="###" onclick="show_tongzhi('tongzhi')" class="hj-wzm-six-top-a" style="font-size:14px;font-family:微软雅黑;color:#549FE3;">沟&nbsp;&nbsp;通</a>
                	<a href="###" id="contents" onclick="show_tongzhi('pingjia')" style="display:none; font-size:14px;font-family:微软雅黑;color:#549FE3;">评&nbsp;&nbsp;价</a>
                </div>

                
                <div class="hj-wzm-six-bottom"  id="tongzhi">
                
                </div>
						<div class="hj-wzm-six-bottom" id="pingjia" style="display: none">
							<table style="width: 100%">
								<tr><td style="width: 50px;padding-bottom: 15px;" >
										<br><span>打分&nbsp;&nbsp;&nbsp;</span>
									</td>
									<td>
										<span id="starlist" style="display: inline"> </span>
										<span>&nbsp;<a href="javascript:resetpingyu()" id="reset_pingyu" style="font-family: '微软雅黑';">重新评价</a>
										</span><br>
									</td>
								</tr>
								<tr>
									<td style="margin-bottom: 5px" valign="top">	评语&nbsp;	</td>
									<td>
										<div>
											<textarea id="addContent" rows="" cols="" style="width: 100%; height: 120px; margin-left: 0px; word-break: break-all; overflow: auto; margin-bottom: 10px;"></textarea>
										</div>
										<div>
										    <p id="showContent" style='word-wrap:break-word;word-break:break-all;line-height:normal;'></p>
											 <%--<textarea id="showContent" readonly="readonly"	class="textareaCSS hj-wzm-six-fabu" rows="" cols="" 
												style="width: 100%; height: 120px; margin-left: 0px; word-break: break-all; overflow: auto; margin-bottom: 10px; display: none"></textarea> 
										--%></div>
										<input type="button" id="save_pingyu" class="hj-wzm-five-fabu" value="发布评价" onclick="savepingyu('addContent');" style="cursor: pointer;" />
									</td>
								</tr>
							</table>
						</div>
					</div>
        </div>
        
<%--  我的团队 /我的下属部门显示部分     --%>
        <div id="more_page" style="display:none;">
	 <div class="hj-wzm-zb-two" >
            <dl>
                <dd style="color: #747474">
                  <%-- <div style="height:20px;margin-top:2px;">
                  <span id="teamsummarydesc" style="color: #747474"></span>
        	   工作总结提交情况
                  <span id='selectdateshow' style="font-family: '微软雅黑';font-size: 18px;"></span>  
                  </div> --%>
                	<div id="myTeam">
                	 <input type="hidden" id="flag" value="true" />
                	 <input type="hidden" id="e01a1s" value="" />
	          	<%--  <h2 id="teamsummarydesc"></h2> --%>
	            <p style="height:18px;padding-top:8px;color: #555555;font-size: 14px;">
	            	<span style="color: #747474">提交情况：</span>
	            	应报：<a href="javascript:showTableByAjax('');" id="totalNum" style="">0人</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;未报：<a href="javascript:showTableByAjax('p011501');" id="p011501Num" style="">0人</a> 
	            	&nbsp;&nbsp;&nbsp;&nbsp;已报：<a href="javascript:showTableByAjax('commit');" id="p011503Num" style="">0人</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;未批：<a href="javascript:showTableByAjax('p011503');" id="notApproveNum" style="">0人</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;已批：<a href="javascript:showTableByAjax('score');" id="scoreNum" style="">0人</a>
	            	<a onclick="fontgrey(this);sendEmail('','more','','');" name="fontbluet" style="margin-left:45px;">提醒大家写总结</a></p>
            </div>
              <div id="mySub_org">
	            <%-- <h2 id="suborgsummarydesc"> </h2> --%>
	            <p style="height:18px;padding-top:8px;color: #555555;font-size: 14px;">
	            	<span style="color: #747474">提交情况：</span>
	            	应报部门数：<a href="javascript:showTableByAjax('');" style="" id="subOrgTotalNum"></a>  
	            	&nbsp;&nbsp;&nbsp;&nbsp; 未报：<a href="javascript:showTableByAjax('p011501');" style="" id="subOrgP011501"></a> 
	            	&nbsp;&nbsp;&nbsp;&nbsp; 已报：<a href="javascript:showTableByAjax('commit');" style="" id="subOrgP011503"></a> 
	            	&nbsp;&nbsp;&nbsp;&nbsp; 未批：<a href="javascript:showTableByAjax('p011503');" style="" id="subNotApproveNum"></a> 
	            	&nbsp;&nbsp;&nbsp;&nbsp; 已批：<a href="javascript:showTableByAjax('score');" style="" id="subOrgScore"></a>  
	            	<a id="fontblueo" onclick="fontgrey(this);sendEmail('','more','','');"  style="margin-left:45px; ">提醒大家写部门工作总结</a></p>
            </div>
               </dd>
            </dl>
        </div>
		  <div class="hj-wzm-tdzb-three">
        
           	<table width="100%" border="0" id="tab">

            </table>
          </div>        	
        </div>
   
    </div>
    </div>
    </div>
    <!-- 任务界面展示在弹出层 -->
    <img src="/workplan/image/left-arrow.png" id="leftArrow" />
    <div id="taskFrame" name="summaryPage"><iframe name="iframe_task" id="iframe_task" scrolling="no" frameborder="0"></iframe></div>
    <style>
   	#leftArrow {
   		position: absolute;
   		right: 50.4%;
   		top: 150px;
   		z-index: 101;
   		width: 10px;
   		height: 12px;
   		display: none;
   	}
   	#taskFrame {
   		position: absolute;
   		padding: 0px;
   		margin: 0px;
   		border: 1px solid #d5d5d5;
    		right: 0.3%;
   		width: 50%;
   		z-index: 10000;
   		display: none;
   	}
   	#taskFrame>iframe {
   		width: 100%;
   		padding: 0px;
   		margin: 0px;
   		border: 0;
   		height: 500px;
   	}
   </style>
</html:form>


<script type="text/javascript" src="/workplan/js/global.js"></script>
<script type="text/javascript" src="/workplan/js/worksummary.js"></script>
<script type="text/javascript" src="/workplan/js/commuction.js"></script>
<script type="text/javascript" src="/workplan/js/stars.js"></script>
<script type="text/javascript" src="/components/personPicker/PersonPicker.js"></script>

<script language="javascript">
var saveNeedHint = false;
weeklySummary={};//用于存储总结内容的全局变量
var form_id="upForm";  //form_id,用于上传沟通附件
// hidemonth参数 
var seldesc_a ='工作周报';
var selvalue_a =1;
var seltype_a =0;
var g_selTypea=0;
var g_selDesca ='工作周报';
var g_selValuea=1;

//加载团队/下属部门成员 
function initTeamPerson(){

	var obj ;
	
	var team = document.getElementById("more_page"); 
	var user_nbase = '';
	var user_a0100 = '';
	
	if(team.style.display == "block"){
		// var objnum = historys.pop();
		var state = Ext.getDom('type').value;
		if(historys.length >0 && state == "team"){
			// 查询团队成员
			obj = historys[historys.length-1];
			user_nbase = obj.nbase;
			user_a0100 = obj.a0100;
			initpersonmap('teammap','month','week',obj.nbase,obj.a0100,'','0');
			queryTeamPerson(user_nbase,user_a0100,'');
		}else if(historys.length >0 && state == "sub_org"){
			obj = historys[historys.length-1];
			queryTeamPerson('','',obj.e0122);
			
		}else
		{	queryTeamPerson(user_nbase,user_a0100,'');
			if(Ext.getDom('maptype').value == "teammap")
				initpersonmap('teammap','month','week','','','','0');
		}
		
	}
}

function checkweek(week)
{	
	<%--从计划进入工作总结,区间会乱,下面注掉的方法可以解决--%>
<%--	var cyc = Ext.get('cycle').getValue();--%>
<%--	if(g_selValuea != cyc){--%>
<%--		g_selValuea = cyc;--%>
<%--		if('1' == cyc){--%>
<%--			g_selDesca = "工作周报";--%>
<%--		}--%>
<%--		if('2' == cyc){--%>
<%--			g_selDesca = "工作月报";--%>
<%--		}--%>
<%--		if('3' == cyc){--%>
<%--			g_selDesca = "季度总结";--%>
<%--		}--%>
<%--		if('4' == cyc){--%>
<%--			g_selDesca = "年度总结";--%>
<%--		}--%>
<%--		if('5' == cyc){--%>
<%--			g_selDesca = "半年总结";--%>
<%--		}--%>
<%--	}--%>
	var type = Ext.getDom('type').value;
	if("person" == type || "org" ==type){
		searchSummary('','','',week);
	}
	else if("sub_org" == type || "team" ==type)
	{
	document.getElementById('week').value=week;
	initTeamPerson();
	}
}
//部门显示 
function orgSumShow()
{
	var type = Ext.getDom('type').value;
	var hashvo = new HashMap();
	hashvo.put("querytype",type);
	hashvo.put("type","orgsumshow");
	Rpc( {functionId : '9028000807',success : initsummarylist}, hashvo);
}
function initsummarylist(response)
{
	var list =Ext.query("#summarylist ul li");
	var map = Ext.JSON.decode(response.responseText);
	list[1].style.display=map.personsum;
	list[2].style.display=map.orgsum;
	
}
//汇总区间显示 
function timeSumShow()
{ 
	var cyc = Ext.get('cycle').getValue();
	if('1' ==cyc)
		Ext.get('timesum').setDisplayed(false);
	else{
		Ext.get('timesum').setDisplayed(true);
	var timelist = Ext.query('#durationlist ul li');
	for ( var i = 0; i < timelist.length; i++) {
		timelist[i].style.display="none";
		}
	if('2' == cyc)
		timelist[0].style.display="block"; // 周
	else if('3' == cyc)
		timelist[1].style.display="block"; // 月
	else if('4' == cyc)
		timelist[2].style.display="block"; // 季
	else if('5' == cyc)
		{
			timelist[1].style.display="block"; // 季
			timelist[2].style.display="block"; // 季
		}
	}
}

function hidemonth(seltype, seldesc, selvalue){
	document.getElementById('week').value=""; //选月时 ，周重新赋值 
	g_selDesca =seldesc;
	g_selValuea =selvalue;
	g_selTypea =seltype;
	var targetId = '';
	var type = Ext.getDom('type').value;
	/*var p0115 = Ext.getDom('p0115').value;
    var state = Ext.getDom('state');
	*/
	if(seltype==0){
		  var months = Ext.query("#months li",false);
		  if(selvalue==1){//选中工作周报时显示全部月份haosl
		      for(var j=0;j<months.length;j++){
	             months[j].setDisplayed(true);
		      }
		  }
		  else if(selvalue==2){//选中月度总结时重新根据参数配置显示月份 haosl
		      renderFillPeriod();
		  }
	}
	if("person" == type || "org" ==type)
	{
		if (0 == seltype) { //周报类型
			if(Ext.getDom('isself').value=="me")
		    {
			    //存cookie需要设置有效期 
				 var Days = 30; 
				 var exp = new Date(); 
				 exp.setTime(exp.getTime() + Days*24*60*60*1000*6); 
		    	document.cookie=Ext.get('user_a0100').getValue()+Ext.get('user_nbase').getValue()+"worksummary="+escape("cycle:"+(g_selValuea ==''?Ext.get('cycle').getValue():g_selValuea+"&title:"+g_selDesca)) + ";expires=" + exp.toGMTString();
		    }
		    //  alert(document.cookie);
			searchSummary(''+selvalue,'','','');
		}
		else if (1 == seltype) { //年
			searchSummary('',''+selvalue,'','');
		}
		else if (2 == seltype) {//月
			searchSummary('',Ext.getDom('myeartitle').innerHTML,''+selvalue,'');
		}
		else return;
	}
	else if("sub_org" == type || "team" ==type)
	{
		if (0 == seltype) { //周报类型
			targetId = "type";

			Ext.getDom("cycle").value = selvalue;
			
			//除周报外，其它都不需要显示月份选择
		    Ext.get("summarymonth").setDisplayed(1 == selvalue || 2 == selvalue);
		    Ext.get("summaryyear").setDisplayed(1 != selvalue && 2 != selvalue);
		    Ext.get('weeks').setDisplayed(1==selvalue);
		    Ext.get('quaters').setDisplayed(3==selvalue);
		    Ext.get('halfyears').setDisplayed(5==selvalue);
		    //Ext.get('months').setDisplayed(2==selvalue);
		}
		else if (1 == seltype) { //年
			targetId = "year";
			Ext.getDom("year").value = selvalue;
			Ext.get('timetitle').setHtml(seldesc);
			Ext.get('myeartitle').setHtml(seldesc);
			
		}
		else if (2 == seltype) {//月
			targetId = "month";
			Ext.getDom("month").value = selvalue;
			Ext.get("timetitle").setHtml(Ext.getDom('myeartitle').innerHTML);
			Ext.get("myeartitle").setHtml(Ext.getDom('myeartitle').innerHTML);
			Ext.get("yeartitle").setHtml(Ext.getDom('myeartitle').innerHTML);
			//列表视图下,增加周总结在月份变化时周数的变化  wusy
			searchSummary('',Ext.getDom('myeartitle').innerHTML,''+selvalue,'');
		}
		else return;
		Ext.get(targetId+'list').setDisplayed(false);
		Ext.get(targetId+'title').setHtml(seldesc);

		initTeamPerson();
	}
}

function searchSummary(cycle,year,month,week) {
	var map = new HashMap();
	var p0100 = document.getElementById("p0100").value;
    map.put("type",Ext.getDom('type').value);
    map.put("maptype",Ext.getDom('maptype').value);
    map.put("belong_type",Ext.getDom('belong_type').value);
    map.put("cycle", cycle ==''?Ext.get('cycle').getValue():cycle);
    map.put("year", year ==''? Ext.get('year').getValue():year);
    map.put("month", month== ''?Ext.get('month').getValue():month);
    map.put("week", week==''?Ext.get('week').getValue():week);
    map.put("p0100", p0100);
    map.put("ishr", Ext.get('ishr').getValue());
	// 查询团队成员
	var obj;
	if(historys.length >0){
	 obj = historys[historys.length-1];
    map.put("hisnbase",obj.nbase);
    map.put("hisa0100",obj.a0100);
    map.put("hise01a1",obj.e01a1);
    map.put("hisflag",obj.flag);
	}   map.put("nbase",Ext.get('user_nbase').getValue());
    	map.put("a0100",Ext.get('user_a0100').getValue());
    map.put("a0101",Ext.getDom('user_name').innerHTML);
            
    //如果是部门 总结  
    if(Ext.getDom('belong_type').value == "2"){
        map.put("e0122",Ext.getDom('e0122').value);
        map.put("haveleader",Ext.getDom('haveleader').value);
    } 
    
    Rpc({functionId:'9028000809',success:queryOK},map); 
}
/**
 * 根据区间类型动态显示填报周期
 */
function renderFillPeriod(){
   var jsonValue = Ext.getDom("summaryTypeJson").value;
   if(!jsonValue ||jsonValue.length==0)
       return;
   var summaryTypeJson = Ext.decode(jsonValue);
   
   var months = Ext.query("#months li",false);
   var halfyears = Ext.query("#halfyears a",false);
   var quaters = Ext.query("#quaters a",false);
  
   for(var i in summaryTypeJson){
	   var s3 = summaryTypeJson[i].s3//月总结 配置
	   var s2 = summaryTypeJson[i].s2//季度总结 配置
	   var s1 = summaryTypeJson[i].s1//半年总结 配置
	   //月报
	   if(s3 && s3.cycle){
	      var cycle = ","+s3.cycle+",";
	      for(var j=0;j<months.length;j++){
              if(cycle.indexOf(","+(j+1)+",")<0)
                 months[j].setDisplayed(false);
          }
	   }
	   //季度总结
	   if(s2 && s2.cycle){
           var cycle = ","+s2.cycle+",";
           for(var j=0;j<quaters.length;j++){
              if(cycle.indexOf(","+(j+1)+",")<0)
                  quaters[j].setDisplayed(false);
           }
       }
	   //半年总结
	   if(s1 && s1.cycle){
	      var cycle = ","+s1.cycle+",";
	      for(var j=0;j<halfyears.length;j++){
	          if(cycle.indexOf(","+(j+1)+",")<0)
	              halfyears[j].setDisplayed(false);
	      }
	   }
   }
}
/**
 * 显示查看的人的已启用的总结类型列表
 */
function loadSummaryTypeList(summaryTypeJson){
	if(!summaryTypeJson || summaryTypeJson.length==0)
	    return;
    var summaryTypeArr = Ext.decode(summaryTypeJson);
    var typeList = Ext.getDom("typelist");
    var ul = typeList.getElementsByTagName('ul')[0];
    var li = '';
    for(var i in summaryTypeArr){
        var item = summaryTypeArr[i];
        if(item.s4)
            li+='<li ><a href="###" onclick="hidemonth(0,\'工作周报\',1)" >工作周报</a></li>';
        else if(item.s3)
         li+= '<li ><a href="###" onclick="hidemonth(0,\'工作月报\',2)" >工作月报</a></li>';
        else if(item.s2)    
            li+= '<li ><a href="###" onclick="hidemonth(0,\'季度总结\',3)" >季度总结</a></li>';
        else if(item.s1)    
            li+= '<li ><a href="###" onclick="hidemonth(0,\'半年总结\',5)" >半年总结</a></li>';
        else if(item.s0)    
            li+= '<li ><a href="###" onclick="hidemonth(0,\'年度总结\',4)" >年度总结</a></li>';
   }
    if(li.length>0)
        ul.innerHTML = li;
}

function queryOK(response){
	var value=response.responseText;
	var map=Ext.JSON.decode(value);
	if(map.iscanread == "no")
	{
		Ext.Msg.alert("提示信息","你没有权限查看当前期间的总结！");
		g_selTypea =seltype_a;
		g_selDesca=seldesc_a;
		g_selValuea=selvalue_a;
		return;
	}

	seltype_a = g_selTypea;
    selvalue_a = map.cycle;
    if('1' == selvalue_a){
        seldesc_a = "工作周报";
    }
    else if('2' == selvalue_a){
        seldesc_a = "工作月报";
    }
    else if('3' == selvalue_a){
        seldesc_a = "季度总结";
    }
    else if('4' == selvalue_a){
        seldesc_a = "年度总结";
    }
    else if('5' == selvalue_a){
        seldesc_a = "半年总结";
    }	
		    
	//选择日期   区间变化 
		var targetId;
		if (0 == seltype_a) { //周报类型
			targetId = "type";
			Ext.getDom("cycle").value = selvalue_a;
			timeSumShow();
			//除周报外，其它都不需要显示月份选择
		    Ext.get("summarymonth").setDisplayed(1 == selvalue_a || 2 == selvalue_a);
		    Ext.get("summaryyear").setDisplayed(1 != selvalue_a && 2 != selvalue_a);
		    Ext.get('weeks').setDisplayed(1==selvalue_a);
		    Ext.get('quaters').setDisplayed(3==selvalue_a);
		    Ext.get('halfyears').setDisplayed(5==selvalue_a);
		   // Ext.get('months').setDisplayed(2==selvalue_a);
		    Ext.get("yeartitle").setHtml(map.year);
		}
        else if (1 == seltype_a) { //年
            targetId = "year";
            Ext.getDom("year").value = selvalue_a;
            seldesc_a = g_selDesca;
        }
        else if (2 == seltype_a) {//月
            targetId = "month";
            Ext.getDom("month").value = selvalue_a;
            seldesc_a = g_selDesca;
            
        }
		
		Ext.get(targetId+'list').setDisplayed(false);
		Ext.get(targetId+'title').setHtml(seldesc_a);
		Ext.get("timetitle").setHtml(map.year);
		Ext.get("myeartitle").setHtml(map.year);
	
	Ext.getDom('p0100').value=map.p0100;
	Ext.getDom('weeknum').value=map.weeknum;
	// 已经提交的 显示提交时间 
	if("01" == map.p0115 || "" == map.p0115 )
		Ext.getDom('submitDate').innerHTML="";
	else
		Ext.getDom('submitDate').innerHTML="提交时间："+map.p0114;
	Ext.getDom('p0115').value=map.p0115;
	//Ext.getDom('p011501').value=map.p011501;
	Ext.getDom('p011503').value=map.p011503;
	Ext.getDom('visibleId').value=map.scope;
	Ext.getDom('score').value=map.score;
	Ext.getDom('p0113').value=map.p0113;
	Ext.getDom('belong_type').value=map.belong_type;
	Ext.getDom('type').value=map.type;
	Ext.getDom('week').value=map.week;
	Ext.getDom('cycle').value=map.cycle;
	Ext.getDom('year').value=map.year;
	Ext.getDom('month').value=map.month;
	
	Ext.get("monthtitle").setHtml(map.month);//haosl 20160113
	
	Ext.getDom('isLeader').value=map.isLeader;
	Ext.getDom('thisSummary').value=map.thisWorkSummary;
	Ext.getDom('nextPlan').value=map.nextWorkSummary;
	//haosl add 20170509 存储之前的总结的内容，用于总结填错时的清空操作 end 
	weeklySummary.thisWorkSummary = map.thisWorkSummary;
	weeklySummary.nextWorkSummary = map.nextWorkSummary;
	//haosl add 20170509 存储之前的总结的内容，用于总结填错时的清空操作 end 
	Ext.getDom('thisWorkSummary').value = map.thisWorkSummary;
	Ext.getDom('nextWorkSummary').value = map.nextWorkSummary;
	Ext.getDom('weekstart').value = map.weekstart;
	Ext.getDom('weekend').value = map.weekend;
	Ext.getDom('e0122').value = map.e0122;
	Ext.getDom('b01ps').value = map.b01ps;
	Ext.getDom('nbaseA0100').value = map.nbaseA0100;
	if("sub_org"!= Ext.get('type').getValue())
	Ext.query('[name=showdeptdesc]')[0].innerHTML = map.deptdesc;

	Ext.getDom('yearListStr').value = map.yearList;
	//增加参数show_task
	document.getElementById("tasktypes").style.display = map.show_task=="true"?"block":"none"; 
	//显示本期工作计划
	document.getElementById('thisWorkPlan').value = map.thisWorkPlan;
	document.getElementById('thisPlan').value = map.thisWorkPlan;
	
    //自适应高度
    adapt.adaptTextareaHeight();
    
	//通过人力地图查看周报 时存a0100
	Ext.getDom('haveleader').value = map.haveleader;
	Ext.getDom('user_a0100').value = map.a0100;
	Ext.getDom('user_name').innerHTML = map.a0101;
	Ext.getDom('user_nbase').value = map.nbase;
	Ext.getDom('user_photo').src = map.photo;
	Ext.getDom('can_edit').value = map.can_edit;
	Ext.getDom('isself').value = map.isself;
	var ishr = Ext.getDom('ishr').value;
	if("true"!=ishr){
		if( "person" == Ext.getDom('type').value){
			//状态区分，显示div，月，周 ,nbase,a0100
			if("personmap" == Ext.getDom('maptype').value)
		    initpersonmap("personmap",'month','week','','','','0');
			else if("teammap" == Ext.getDom('maptype').value){
				if(historys.length >0 ){
					// 查询团队成员
					var obj = historys[historys.length-1];
					user_nbase = obj.nbase;
					user_a0100 = obj.a0100;
				initpersonmap("teammap",'month','week',obj.nbase,obj.a0100,obj.e01a1,'0',obj.flag);
				}else
				initpersonmap("teammap",'month','week','','','','0');
			}
		}
	}
	
	
	init();
	adapt.adaptTextareaHeight();
}

/**
 * 提示写工作总结
 *thisWorkSummary	本周工作总结
 *nextWorkSummary	下周工作计划 
 */
function tishiWiriteSummary(thisWorkSummary,nextWorkSummary){
	if(trim(thisWorkSummary) == "" && trim(nextWorkSummary) == ""){
		Ext.Msg.alert("提示信息","请填写本期工作总结和下期工作计划！");
		return true;
	} else
		return false;
}

// 保存 
function saveSummary(needHint){

	//needHint 用来区别是否是自动保存，自动保存不校验啦 haosl 2018年7月14日
	if(needHint){
		if(!validatePreNow())
			return;
	}

	if(!privCheck("true")){
	    return;
    }
	var thisWorkSummary = Ext.getDom('thisWorkSummary').value;
	var nextWorkSummary = Ext.getDom('nextWorkSummary').value;
	
	if (Ext.isEmpty(needHint)){
		needHint = false;
	}else {
		// 提示 
		if(Ext.getDom('zhouzj').value!='true'&& weeklySummary.thisWorkSummary=="" && weeklySummary.nextWorkSummary=="" && tishiWiriteSummary(thisWorkSummary,nextWorkSummary) )// 周总结，启用周总结时，不校验原大文本 chent 20161205 add
		{
			if(publishbtn){
				publishbtn.removeAttribute('disabled');
			}
			return;
		}
	}
	saveNeedHint = needHint;

	var p0100 = document.getElementById("p0100").value;
	var week = document.getElementById("week").value;
	 
	var e0122 = Ext.getDom('e0122');
	var b01ps = Ext.getDom('b01ps');
    var visibleId = document.getElementById("visibleId").value;
    var map = new HashMap();
	map.put("opt","add");
	map.put("cycle", Ext.get('cycle').getValue());
	map.put("year", Ext.get('year').getValue());
	map.put("month", Ext.get('month').getValue());
	map.put("week", Ext.get('week').getValue());
//	map.put("type",Ext.getDom('type').value);
	map.put("belong_type",Ext.getDom('belong_type').value);
	map.put("p0100",p0100);
	map.put("thisWorkSummary",getEncodeStr(thisWorkSummary));
	map.put("nextWorkSummary",getEncodeStr(nextWorkSummary));
	map.put("week",week);
	map.put("e0122",e0122==null?'':e0122.value);
	map.put("b01ps",b01ps==null?'':b01ps.value);
	map.put("scope",visibleId);
    Rpc({functionId:'9028000802',success:saveOK},map); 
}
function saveOK(response){// 目前有两个地方在用这个方法 
	var value=response.responseText;
	var map=Ext.JSON.decode(value);
	Ext.getDom('p0100').value=map.p0100;
	Ext.get('ait').show();

	initMessageContentlist('3',map.p0100);
	if (saveNeedHint) {
		if(typeof okr_weeklysummary != 'undefined'){//周总结-保存培训需求 chent 20161220
			okr_weeklysummary.updateContentValue();
		}
		Ext.Msg.alert("提示信息","保存成功！");
	}
	saveNeedHint = false;
	//haosl add 20170509
	weeklySummary.thisWorkSummary = map.thisWorkSummary;
	weeklySummary.nextWorkSummary = map.nextWorkSummary;
}
/**
 * 校验填报期间
 */
function validatePreNow(){
	
	var jsonValue = Ext.getDom("summaryTypeJson").value;
	if(!jsonValue ||jsonValue.length==0)
	    return true;
	var flag = false;
	var summaryTypeJson = Ext.decode(jsonValue);
	var key = "";
    switch(Ext.get('cycle').getValue()){
    	case "1"://周
    		key="s4";
    		break;
    	case "2"://月
    		key="s3";
    		break;
    	case "3"://季度
    		key="s2";
    		break;
    	case "4"://年
    		key="s0";
    		break;
    	case "5"://半年
    		key="s1";
    		break;
    	
    }
	var validPre = "";
	var validNow = "";
	for(var p in summaryTypeJson){
		var obj = summaryTypeJson[p];
		if(obj[key]){
			validPre = obj[key].pre;
			validNow = obj[key].now;
			break;
		}
	}
	var map = new HashMap();
	map.put("cycle",Ext.get('cycle').getValue());
	map.put("year",Ext.get('year').getValue());
	map.put("month",Ext.get('month').getValue());
	map.put("week",Ext.get('week').getValue());
	map.put("validPre",validPre);
	map.put("validNow",validNow);
	map.put("opt","validatePreNow");
	Rpc({functionId:'9028000802',async:false,success:function(form){
		 var result = Ext.decode(form.responseText);
		 flag = result.fillSummary;
		 if(!flag){
		 	Ext.Msg.alert('提示信息', '总结不在填报期限内,不能填报！');
		 }
	}},map);
	return flag;
}
//发布 
function publishSummary(){
	
	if(!validatePreNow())
		return;
    if(!privCheck("true")){
        return;
    }
	 var p0100 = document.getElementById("p0100").value;
	 var thisWorkSummary = Ext.getDom('thisWorkSummary').value;
	 var nextWorkSummary = Ext.getDom('nextWorkSummary').value;
	// 提示
		if(Ext.getDom('zhouzj').value!='true' && tishiWiriteSummary(thisWorkSummary,nextWorkSummary))//周总结，启用周总结时，不校验原大文本 chent 20161205 add
		{
			return;
		}
    //启用周总结，发布个人周总结时，需要判断是否填写了本期工作总结和下期工作计划
    if(Ext.getDom('zhouzj').value=='true'
        && Ext.getDom('cycle').value=="1"
        && Ext.getDom('type').value=="person"){
        var store1 = Ext.getCmp("grid1").getStore();
        var store3 = Ext.getCmp("grid3").getStore();
        if(store1.getCount()==0 && store3.getCount()==0){
            Ext.showAlert("请填写本期工作总结和下期工作计划！");
            return true;
        }
    }
    var publishbtn = document.getElementById("publishbtn");
    if(publishbtn){
        publishbtn.setAttribute('disabled','disabled');
    }
	 var e0122 = Ext.getDom('e0122');
	 var b01ps = Ext.getDom('b01ps');
	 var isLeader = Ext.getDom('isLeader').value;
	 var visibleId = document.getElementById("visibleId").value;
	 
	 var hashvo = new HashMap();
	 hashvo.put("cycle", Ext.get('cycle').getValue());
	 hashvo.put("year", Ext.get('year').getValue());
	 hashvo.put("month", Ext.get('month').getValue());
	 hashvo.put("week", Ext.get('week').getValue());
	 hashvo.put("belong_type",Ext.getDom('belong_type').value);
	// hashvo.put("type",Ext.getDom('type').value);
     hashvo.put("p0100",p0100);
     hashvo.put("thisWorkSummary",getEncodeStr(thisWorkSummary));
     hashvo.put("nextWorkSummary",getEncodeStr(nextWorkSummary));
     hashvo.put("e0122",e0122==null?'':e0122.value);
     hashvo.put("b01ps",b01ps==null?'':b01ps.value);
     hashvo.put("scope",visibleId);
     if(p0100 == ""){
	   	var week = document.getElementById("week").value;
	   	hashvo.put("opt","add");
        hashvo.put("week",week);
    }else{
   	 	hashvo.put("opt","publish");
   	}
		if(isLeader =='true')
			hashvo.put("saveState","03");//是领导时直接批准
		else
			hashvo.put("saveState","02");	

    Rpc({functionId:'9028000802',success:publishOK},hashvo); 
}
function publishOK(response){
	var publishbtn = document.getElementById("publishbtn");
	if(publishbtn){
		publishbtn.removeAttribute('disabled');
	}
	var value=response.responseText;
	var map=Ext.JSON.decode(value);

	var pResult = map.pulishResult;
	Ext.getDom('p0100').value=map.p0100;
	if(pResult == null)
		return;
	
	result(response);
	 var isLeader = Ext.getDom('isLeader').value;
	document.getElementById("submitDate").innerHTML = "提交时间："+pResult;
	if(isLeader =='true')
	document.getElementById("state").innerHTML='已批准';  
	else
	document.getElementById("state").innerHTML='已提交'; 	

	var cycle = Ext.getDom('cycle').value;
	var type = Ext.getDom('type').value;
	var zhouzj = Ext.getDom('zhouzj').value;
	var openzhouzj = false;
	if(cycle == "1" && 'person' == type && zhouzj === 'true'){//1、周总结  2、个人总结 3、启用周总结
		openzhouzj = true;
	}
	if(!openzhouzj){//周总结-不是表格时才显示文本 chent 20161220
		document.getElementById("autosummary").style.display="none"; 
		document.getElementById("edit_button").style.display="block"; 
	}
	 Ext.query("#tools input")[0].style.display="none";
	 Ext.query("#tools input")[1].style.display="none";
	 
	 if(!openzhouzj){//周总结-不是表格时才显示文本 chent 20161220
		document.getElementById("editWorkSummary").style.display="none";
		document.getElementById("notEditSummary").style.display="block";
	 }
	//Ext.get(Ext.query('.hj-wzm-six-top span')[1]).show();
	if(isLeader =='true')
		Ext.getDom('p0115').value ='03';
	else
	Ext.getDom('p0115').value ='02';
	//initTextarea();

    adapt.adaptTextareaHeight();
	//改提交状态szk
	if(isLeader =='false'){//大boss发布工作总结,不需要发邮件
		sendEmail('person','one','publish','');//发布 发送提醒 
	}
	if(typeof okr_weeklysummary != 'undefined'){//周总结-保存培训需求 chent 20161220
		okr_weeklysummary.updateContentValue();
		
		okr_weeklysummary.p0115 = Ext.getDom('p0115').value;
		okr_weeklysummary.reload();
	}
}

function result(response){
	var value=response.responseText;
	var map=Ext.JSON.decode(value);
	
	var thisSummary = map.thisSummary;
	var nextPlan = map.nextPlan;
	
	document.getElementById("thisSummary").value = thisSummary;
	document.getElementById("nextPlan").value = nextPlan;
}
//退回
function rejectWin(){
	var p0100 = document.getElementById("p0100").value;
    if (p0100 != '') {
	    Ext.create("Ext.window.Window",{
		    title : "退回工作总结",
		    id:'rejectWin',
	   		height: 200,
	   	    width: 300,
	   	    layout:'fit',
	   	    padding:'10 10 5 10',
		   	items: {
		         xtype: 'textarea',
		         id:'rejectMsg',
		         border: true,
		         margin:'0 0 5 0',
		         emptyText:'请填写退回原因'
		    },
		    buttonAlign:'center',
		    fbar: [
		    	  { type: 'button', text: '退回',handler:function(){
		    		  var msg = Ext.getCmp("rejectMsg").getValue();
		    		  var hashvo = new HashMap();
	                  hashvo.put("opt","reject");
	                  hashvo.put("p0100", p0100);
	                  hashvo.put("rejectValue",getEncodeStr(msg));
	                  Rpc({functionId : '9028000802',success : rejectOK}, hashvo);
		    	  } },
		    	  { type: 'button', text: '取消',handler:function(){
		    		  Ext.getCmp("rejectWin").close();
		    	  } }
		    	]
	   	}).show();
    }
}

function rejectOK(response){
	Ext.getCmp("rejectWin").close();
	var value=response.responseText;
    var map=Ext.JSON.decode(value);
    var arry = new Array();
    arry[0] = map.name;
    arry[1] = map.rejectMsg;
    arry[2] = map.dateValue;
    arry[3] = "";
    arry[4] = "";
    arry[5] = map.photoUrl; 
    arry[6] = "3";
    arry[7] = "0";
    //添加沟通信息
    addDiv(arry, true);
    
	document.getElementById("state").innerHTML='已退回';
    document.getElementById("approve").style.display="none";
    document.getElementById("reject").style.display="none";
    document.getElementById("showpingjia").style.display="none";
    //将页面存总结状态置成03(已批准),否则点重新评价会导致批准按钮,已提交状态的出现wusy
    Ext.getDom('p0115').value = "07";
    Ext.get('ait').show();
    sendEmail('person','one','reject','');//发送提醒 
    //init();
    //adapt.adaptTextareaHeight();

}
//批准 
function approveSummary(){
	var p0100 = document.getElementById("p0100").value;
	var hashvo = new HashMap();
    hashvo.put("opt","approve");
	hashvo.put("belong_type",Ext.getDom('belong_type').value);
	//hashvo.put("type",Ext.getDom('type').value);
    hashvo.put("p0100",p0100);

    Rpc({functionId:'9028000802',success:approveOK},hashvo);
}
function approveOK(response){
	document.getElementById("state").innerHTML='已批准';
	document.getElementById("approve").style.display="none";
	document.getElementById("reject").style.display="none";
	//将页面存总结状态置成03(已批准),否则点重新评价会导致批准按钮,已提交状态的出现wusy
	Ext.getDom('p0115').value = "03";
	Ext.get('ait').show();
	sendEmail('person','one','approve','');//发送提醒 

}
// 编辑 
function editSummary(){
	var p0100 = document.getElementById("p0100").value;
	var visibleId = document.getElementById("visibleId").value;
	document.getElementById("submitDate").innerHTML = "";
	var hashvo = new HashMap();
    hashvo.put("opt","edit");
	hashvo.put("belong_type",Ext.getDom('belong_type').value);
	//hashvo.put("type",Ext.getDom('type').value);
    hashvo.put("p0100",p0100);

    hashvo.put("scope",visibleId);
    Rpc({functionId:'9028000802',success:editOK},hashvo);
}

function editOK(response){
	document.getElementById("state").innerHTML='未提交';
	Ext.query("#tools input")[0].style.display="block";
	Ext.query("#tools input")[1].style.display="block";
	var cyclenow = Ext.getDom("cycle").value;
	if(!(Ext.getDom('zhouzj').value=='true' && cyclenow=="1")){//启用周总结时，不显示文本 ;并且只有是周总结类型时不显示
			
	    document.getElementById("editWorkSummary").style.display="block";
		document.getElementById("autosummary").style.display="block"; 
		document.getElementById("edit_button").style.display="none"; 
		document.getElementById("notEditSummary").style.display="none";
		 document.getElementById("starshow1").style.display="none";
	 	 Ext.query('.hj-wzm-zb-four-wtj')[0].style.display="block";
		Ext.get(Ext.query('.hj-wzm-six-top a')[1]).hide();
		show_tongzhi('tongzhi');
		Ext.get('ait').show();
		Ext.getDom('p0115').value = '01';
		adapt.adaptTextareaHeight();
	}
	//改提交状态szk
	//var yi1=Ext.getDom('yi');
	//var yi2=Ext.query('.a1');
	//var wei1=Ext.getDom('wei');
	//var weeknum=Ext.getDom('weeknum');
	//var wei2=Ext.query('.a2');
	
	//yi1.innerHTML=yi1.innerHTML-1;
	//yi2[0].innerHTML=yi1.innerHTML;
	//wei1.innerHTML=weeknum.value-yi1.innerHTML;
	//wei2[0].innerHTML=wei1.innerHTML;
}

//切换可见范围 
function changeVisible(visibleId){
	Ext.getDom("visibleId").value=visibleId;

	var visibleName = "上级可见";
	if ('1' == visibleId)
		visibleName = "本单位可见";
	else if ('2' == visibleId)
		visibleName = "本部门可见";
	else if ('3' == visibleId)
		visibleName = "完全公开"; 
		
	Ext.get("visibleRange").setHtml(visibleName);
	var p0115 = Ext.getDom('p0115').value;
	if(p0115 == '02' || p0115 =='03')
	saveScope(visibleId);
}
//保存范围 
function saveScope(visibleId)
{
	 var hashvo = new HashMap();
	 hashvo.put("opt","savescope");
	 hashvo.put("p0100",Ext.getDom('p0100').value);
	 hashvo.put("scope",visibleId);
	 Rpc({functionId:'9028000802'},hashvo);
}

//重新评价 
function resetpingyu()
{
	 var hashvo = new HashMap();
    hashvo.put("opt","savepingyu");
    hashvo.put("p0100",Ext.getDom('p0100').value);
    hashvo.put("p0113","");
    hashvo.put("score","-1");
    Ext.getDom('score').value="-1";
    Ext.getDom('p0113').value="";
    Ext.getDom('addContent').value="";
    Ext.getDom('showContent').innerHTML="";
    Rpc({functionId:'9028000802',success:resetpingyuOk},hashvo);
}
function resetpingyuOk(response){
	//alert(Ext.getDom('p0115').value);
	if(Ext.getDom('p0115').value == "02"){
		document.getElementById("state").innerHTML='已提交';
		document.getElementById("approve").style.display="inline";
		document.getElementById("reject").style.display="inline";
	}else if(Ext.getDom('p0115').value == "03"){
		document.getElementById("state").innerHTML='已批准';
	}
	var addContent = document.getElementById("addContent");
	document.getElementById("showpingjia").style.display="inline";
    document.getElementById("starshow1").style.display="none";
    addContent.style.display="block";
	addContent.focus();
	document.getElementById("showContent").style.display="none";
	Ext.query('.hj-wzm-zb-four-wtj')[0].style.display="block";
	document.getElementById("reset_pingyu").style.display="none";
	document.getElementById("save_pingyu").style.display="block";
	initstar('starlist');
 	initstar('starshow1');
 
}

//保存评语 和 分数 
function savepingyu(id)
{
	var content = Ext.getDom(id).value;
	 var hashvo = new HashMap();
	 if(_score.value < 0 )
	 {
		 if(trim(content)==""){
			 Ext.Msg.alert({title:"提示信息",msg:'您确定打0分吗？</br>请先填写评语之后再发布。',icon:Ext.MessageBox.QUESTION, buttons:Ext.MessageBox.YES} );
		return;
		}
		 else
			 _score.value = 0; 
	}
    hashvo.put("opt","savepingyu");
    hashvo.put("p0100",Ext.getDom('p0100').value);
    hashvo.put("p0113",getEncodeStr(content));
    hashvo.put("score",_score.value);
    Ext.getDom('p0113').value=Ext.getDom(id).value;
    Rpc({functionId:'9028000802',success:pingyuOK},hashvo);
}
function pingyuOK(response){
	//调用处理登录超时的方法
	basic.global.logonOut();
	var value=response.responseText;
	var map=Ext.JSON.decode(value);
	Ext.getDom('score').value = map.score;
	Ext.getDom('showContent').innerHTML=map.showtext;
	Ext.getDom('p0115').value = "03";
	//显示审批  haosl 2017-11-28
 	Ext.getDom('state').innerHTML='已批准';
 	Ext.query('.hj-wzm-zb-four-wtj')[0].style.display="block";
	document.getElementById("showpingjia").style.display="none";
	document.getElementById("approve").style.display="none";
	document.getElementById("reject").style.display="none";
    document.getElementById("starshow1").style.display="block";
	document.getElementById("addContent").style.display="none";
	document.getElementById("addContent").value="";
	//document.getElementById("addContent").innerHTML = "";
	document.getElementById("showContent").style.display="block";
	
	document.getElementById("reset_pingyu").style.display="inline";
	document.getElementById("save_pingyu").style.display="none";
	
	initstar('starlist');
 	initstar('starshow1');
	sendEmail('person','one','contents','');//发送评价提醒 
}

function tranNumberToChinese(aNum) {
	var numChn = ["一","二","三","四","五","六","七","八","九","十"];
	return numChn[aNum - 1];
}

function showSummaryDesc() {
	var cycle = Ext.get('cycle').getValue();
    var year = Ext.get('year').getValue();
    var month = Ext.get('month').getValue();
    var week = Ext.get('week').getValue();
    var weekstart = Ext.get('weekstart').getValue();
    var weekend = Ext.get('weekend').getValue();

    var cyclename = "";
    var desc = "";
    var summaryDetailDesc = year + "年";

    if (1 == cycle) {
        cyclename = "周";
        desc = month + "月周报"; 
        if (!Ext.isEmpty(weekstart)) {
	        var adate = dt = Ext.Date.parse(weekstart, "Y-m-d");
	        var startMonth = adate.getMonth()+1;
	        var startDay = adate.getDate();
	        
	        adate = Ext.Date.parse(weekend, "Y-m-d");;
	        var endMonth = adate.getMonth()+1;
	        var endDay = adate.getDate();
	        
	        summaryDetailDesc = summaryDetailDesc + month + "月第" + tranNumberToChinese(week) + "周" 
	                          + "(" + startMonth + "." + startDay + "~"+ endMonth + "." + endDay +")";
        }
    } else {
        desc = year + "年";
        if (2 == cycle) {
        	cyclename = "月";
            desc = desc + "月";
            summaryDetailDesc = summaryDetailDesc + month + "月";
        }
        else if (3 == cycle) {
        	cyclename = "季";
            desc = desc + "季";
            summaryDetailDesc = summaryDetailDesc + "第" + tranNumberToChinese(week) + "季度";
        }
        else if (4 == cycle) {
        	cyclename = "年";
            desc = desc + "年";
        }
        else if (5 == cycle) {
        	cyclename = "半年";
            desc = desc + "半年";
            if (1 == week)
                summaryDetailDesc = summaryDetailDesc + "上半年";
            else 
                summaryDetailDesc = summaryDetailDesc + "下半年";
        }

        desc = desc + "报";
    }

    Ext.getDom('summarydesc').innerHTML = summaryDetailDesc;
    //Ext.getDom('teamsummarydesc').innerHTML = summaryDetailDesc;
}

function initYears(){

	if (!Ext.isEmpty(Ext.getDom("yearlist").innerHTML))
	    Ext.getDom("yearlist").innerHTML = "";

	var ul = document.createElement("ul"); 
	var yearStr = Ext.get('yearListStr').getValue();
	yearStr = yearStr.substr(1,yearStr.length-2);
	
	var yearList = yearStr.split(",");

	for(var i = 0 ;i < yearList.length; i++){
		var item = yearList[i];
		ul.innerHTML += "<li><a href='###' onclick=\"hidemonth(1,'"+item+"',"+item+")\">"+item+"年</a></li>";
	}
	Ext.getDom("yearlist").appendChild(ul);
}

function init() {
	// 加载年份 
	initYears();
	var cycle = Ext.get('cycle').getValue();
	var year = Ext.get('year').getValue();
	var month = Ext.get('month').getValue();
	var week = Ext.get('week').getValue();
	var type = Ext.getDom('type').value;
	var belong_type = Ext.getDom('belong_type').value;
	var can_edit = Ext.getDom('can_edit').value;
	var isself = Ext.getDom('isself').value;   //是自己为me
	//周报状态
 	var score = Ext.getDom('score').value;
 	var p0115 = Ext.getDom('p0115').value;
 	var state = Ext.getDom('state');
 	var maptype = Ext.getDom('maptype').value;
 	var isLeader = Ext.getDom('isLeader').value;
 	
 	var zhouzj = Ext.getDom('zhouzj').value;//是否启用周总结
	var zhouzjpx = Ext.getDom('zhouzjpx').value;//周总结-培训
 	var openzhouzj  = false;
	if(cycle == "1" && 'person' == type && zhouzj === 'true'){//1、周总结  2、个人总结 3、启用周总结
		openzhouzj = true;
	}
 
	//如果是部门 ，初始化部门列表 
	if(belong_type == "2" || type == "org"){
	//	initorglist();
		Ext.query('[name=showdeptdesc]')[0].style.display = "inline";
	} else {
	    Ext.query('[name=showdeptdesc]')[0].style.display = "none";
	}
	//显示工具条 
	if("other" ==isself){
        Ext.getDom('tools').style.display = "none";
        }
    else{
        //启用周总结时不显示汇总等周总结
        if(!openzhouzj){
            orgSumShow();
        } 
        //部门或个人汇总 
        Ext.getDom('tools').style.display = "block";
        }
    
	setSelWeekStyle(cycle,0);

	showSummaryDesc();

 	//pingjia text
 	Ext.getDom('addContent').value=Ext.getDom('p0113').value;
	Ext.getDom('showContent').innerHTML=Ext.getDom('p0113').value;
 	document.getElementById("starshow1").style.display="none";
 	Ext.query('.hj-wzm-zb-four-wtj')[0].style.display="block";
 	document.getElementById("addContent").style.display="block";
    document.getElementById("showContent").style.display="none";
	document.getElementById("reset_pingyu").style.display="none";
	document.getElementById("save_pingyu").style.display="block";
	
	document.getElementById("remind").style.display="none";
	
 	if(score >= 0 && isLeader =='false'){
		document.getElementById("starshow1").style.display="block";
		document.getElementById("addContent").style.display="none";
		document.getElementById("showContent").style.display="block";
		Ext.query('.hj-wzm-zb-four-wtj')[0].style.display="none";
		document.getElementById("reset_pingyu").style.display="inline";
		document.getElementById("save_pingyu").style.display="none";
 	}else if(p0115 == "02")
 	{
 		state.innerHTML = "已提交";
 	}
 	else if(p0115 == "03"){
 		state.innerHTML = "已批准";
 	}
 	else if(p0115 == "07"){
        state.innerHTML = "已退回";
    }
 	else {
 		state.innerHTML = "未提交";
 	}
 	initstar('starlist');
 	initstar('starshow1');
	//地图进来，自己进来 的各种情况 显示
	Ext.get("focusafterclear").addCls("bh-clear");
	document.getElementById("remind").style.display="none";
	document.getElementById("showpingjia").style.display="none";
	document.getElementById("approve").style.display="none";
	document.getElementById("reject").style.display="none";
	var attentiontitle = "<p>关注"+Ext.getDom('user_name').innerHTML+"总结的：</p>"; 
 	if(Ext.getDom('p0115').value == "03" || Ext.getDom('p0115').value == "02"){
 		
 		Ext.getDom('tools').style.display = "block";
	    document.getElementById("editWorkSummary").style.display="none";
	    document.getElementById("notEditSummary").style.display="block";
	    Ext.query("#tools input")[0].style.display="none";
	    Ext.query("#tools input")[1].style.display="none";
	    Ext.getDom('photolist').innerHTML = "";
	    //if("personorgmap" == maptype || "orgmap" == maptype)
	    if(belong_type == "2" || type == "org")
	    {attentiontitle="<p>关注"+Ext.query('[name=showdeptdesc]')[0].innerHTML+"总结的：</p>"; }
	    if( ("other" == isself||"directsuper" == isself) && "personmap" == maptype){
		    Ext.get('tools').setDisplayed(false);
		    }
	    //本人和不是直接上下级关系 被评价过了（分>0）  报批批准评价按钮看不到  
	    if( ("me" == isself  || ("other" == isself)) && score >= 0){
        	document.getElementById("edit_button").style.display="none"; 
        	document.getElementById("autosummary").style.display="none"; 
        	document.getElementById("reset_pingyu").style.display="none"; 
        	if( isLeader =='false')
        	 Ext.get(Ext.query('.hj-wzm-six-top a')[1]).show();
       	 //是本人和非直接上下级关系进来但未被评价
	    }else if(("me" == isself && score < 0) || ("other" == isself  && score < 0)){
	    	 document.getElementById("edit_button").style.display=can_edit; 
	    	 Ext.get('autosummary').setDisplayed(false);
        	 Ext.get(Ext.query('.hj-wzm-six-top a')[1]).hide();
        	
		}
		//直接上下级关系
	    else if( "directsuper" == isself ){
			   if(score < 0)
				    {
					    	document.getElementById("showpingjia").style.display="inline";
					    	if(Ext.getDom('p0115').value == "02"){
					    		document.getElementById("approve").style.display="inline";
					    		document.getElementById("reject").style.display="inline";
					    	}
					}
			   document.getElementById("edit_button").style.display=can_edit;   //是否能修改 
		    	Ext.get('autosummary').setDisplayed(false);
		    	Ext.get(Ext.query('.hj-wzm-six-top a')[1]).show();
	   }
		Ext.fly('photolist').insertHtml('afterBegin', attentiontitle); 
		 //加载关注人列表
		initphotolist();
	} else {
		if("me" == isself ){
			Ext.query("#tools input")[0].style.display="block";
			Ext.query("#tools input")[1].style.display="block";
		    document.getElementById("autosummary").style.display="block";
			document.getElementById("edit_button").style.display="none"; 
			Ext.getDom('photolist').innerHTML = "";
			if(belong_type == "2" || type == "org")
		    {attentiontitle="<p>关注"+Ext.query('[name=showdeptdesc]')[0].innerHTML+"总结的：</p>"; }
			Ext.fly('photolist').insertHtml('afterBegin', attentiontitle); 
			 //加载关注人列表
			initphotolist();
	        document.getElementById("editWorkSummary").style.display="block";
		}else if("directsuper" == isself
				||"other" == isself)//haosl	update 20170322 hr进入总结页面，不显示提醒写总结的框。
		{
			document.getElementById("remind").style.display="block";
			Ext.query('.hj-wzm-six-dd a')[0].style.color = "";
			document.getElementById("editWorkSummary").style.display="none";
			Ext.getDom('photolist').innerHTML = "";
			Ext.get("focusafterclear").removeCls("bh-clear");
			Ext.get('tools').setDisplayed(false);
		}
		document.getElementById("notEditSummary").style.display="none";
		Ext.get(Ext.query('.hj-wzm-six-top a')[1]).hide();
	}
	if( maptype == "teammap" ){
		document.getElementById("returnId").style.display="inline"; 
	}

	//周总结填写方式为表格时自动生成p0100  haosl update （大文本填写模式不需要自动生成p0100）
	if("me" == isself && openzhouzj){
	    if (Ext.isEmpty(Ext.get('p0100').getValue())) {
	    	saveSummary();  
	    }
	}
 	
 	// 加载可见范围 
 	var indexVisible = Ext.getDom('visibleId').value;
 	changeVisible(indexVisible);
 	//默认显示 通知 
 	show_tongzhi('tongzhi');
 	// 加载沟通 信息 
    var p0100 = Ext.get("p0100").getValue();
	initMessageContentlist('3',p0100);
	
	//initTextarea();

 	// 加载显示，我的相关计划 
 	if(document.getElementById("tasktypes").style.display != "none") {
 		  getMyWorkTaskList('0');
 		  adapt.adaptTextareaHeight();
 	}

 	// 周总结 chent 20161205 add start
	if(openzhouzj){//周总结
		Ext.getDom('zhouzjdiv').style.display = "block";
		
		// 老工作总结的div：hj-wzm-zb-three任务 hj-wzm-zb-fove-top总结
		// 周总结的div：zhouzj
		var domArray = Ext.query("*[class=hj-wzm-zb-three]");
		for(var i=0; i<domArray.length; i++){
			var dom = domArray[i];
			dom.style.display = "none";
		}
		domArray = Ext.query("*[class=hj-wzm-zb-fove]");
		for(var i=0; i<domArray.length; i++){
			var dom = domArray[i];
			dom.style.display = "none";
		}
		
        Ext.getDom('autosummary').style.display = 'none';
        
		Ext.getDom('edit_button').style.display = 'none';
		var user_nbase = Ext.get('user_nbase').getValue();
		var user_a0100 = Ext.get('user_a0100').getValue();
		var nbase = Ext.get('nbase').getValue();
		var a0100 = Ext.get('a0100').getValue();
		if(Ext.isEmpty(p0100) && "me"==isself){// 没有总结、则创建一个空总结
			p0100 = createP01(cycle, year, month, week, user_nbase, user_a0100 );
			Ext.getDom("p0100").value = p0100;//haosl 20170323 add 生成p0100后需要给隐藏表单设值
		}
		var zhouzjpx = Ext.getDom('zhouzjpx').value;
		
		var obj = {p0100:p0100,cycle:cycle,year:year,month:month,week:week,isself:isself,p0115:p0115,zhouzjpx:zhouzjpx,nbase:user_nbase,a0100:user_a0100}
		if(typeof okr_weeklysummary != 'undefined'){//加载过
			okr_weeklysummary.reload(obj);
		} else {
			Ext.Loader.setConfig({
				enabled: true,
				paths: {
					'OKR': '/workplan/js'
				}
			});
			Ext.require('OKR.WeeklySummary', function(){
				okr_weeklysummary = Ext.create("OKR.WeeklySummary", obj);
			});
		}
		
	} else {
	   var taskflag = document.getElementById("tasktypes").style.display; 
	   if(taskflag != "none"){
			var domArray = Ext.query("*[class=hj-wzm-zb-three]");
			for(var i=0; i<domArray.length; i++){
				var dom = domArray[i];
				dom.style.display = "block";
			}
		}
		//29548 已提交或批准状态加号不显示
		if(Ext.getDom('p0115').value == "03" || Ext.getDom('p0115').value == "02"){
			  Ext.getDom('autosummary').style.display = 'none';
		}else{
			Ext.getDom('autosummary').style.display = 'block';
		}
		if(p0115 == "03" ||p0115 =="02"){//没有填写过总结的话，编辑按钮不需要显示
			 if( ("me" == isself  || ("other" == isself)) && score >= 0){
				 Ext.getDom('edit_button').style.display="none";
		       	 //是本人和非直接上下级关系进来但未被评价
		     }else if(("me" == isself && score < 0) || ("other" == isself  && score < 0)){
		    	Ext.getDom('edit_button').style.display=can_edit;
			 }else if( "directsuper" == isself ){//直接上下级关系
				Ext.getDom('edit_button').style.display=can_edit;
			 }
		}else{
			Ext.getDom('edit_button').style.display="none"; 
		}
		Ext.getDom('zhouzjdiv').style.display = "none";
		
	}
	// 周总结 chent 20161205 add end
}
function createP01(cycle, year, month, week, nbase, a0100){
	
	var p0100 = '';
	var e0122 = Ext.getDom('e0122');
	var b01ps = Ext.getDom('b01ps');
    var visibleId = document.getElementById("visibleId").value;
    var map = new HashMap();
	map.put("opt","add");
	map.put("cycle", cycle);
	map.put("year", year);
	map.put("month", month);
	map.put("week", week);
	map.put("belong_type", "0");
	map.put("p0100","");
	map.put("thisWorkSummary"," ");
	map.put("nextWorkSummary"," ");
	map.put("e0122",e0122==null?'':e0122.value);
	map.put("b01ps",b01ps==null?'':b01ps.value);
	map.put("scope",visibleId);
	map.put("nbase",nbase);
	map.put("a0100",a0100);
	
	Rpc({functionId:'9028000802', async:false, success:function(response){
		var value = response.responseText;
    	var map = Ext.JSON.decode(value);
    	p0100 = map.p0100;
    	
    }, scope:this},map);
	return p0100;
}
//保存 
function autoSaveSummary(){

	var p0100 = Ext.get("p0100").getValue();
	var week = document.getElementById("week").value;
	var thisWorkSummary = "";
	var nextWorkSummary = "";
	var e0122 = Ext.getDom('e0122');
	var b01ps = Ext.getDom('b01ps');
    var visibleId = document.getElementById("visibleId").value;
	// 被 查看人员  
    var user_nbase = Ext.get('user_nbase').getValue();
	var user_a0100 = Ext.get('user_a0100').getValue();
     
    var map = new HashMap();
	map.put("opt","add");
	map.put("cycle", Ext.get('cycle').getValue());
	map.put("year", Ext.get('year').getValue());
	map.put("month", Ext.get('month').getValue());
	map.put("week", Ext.get('week').getValue());
	map.put("belong_type",Ext.getDom('belong_type').value);
	map.put("p0100",p0100);
	map.put("thisWorkSummary",getEncodeStr(thisWorkSummary));
	map.put("nextWorkSummary",nextWorkSummary);
	map.put("week",week);
	map.put("e0122",e0122==null?'':e0122.value);
	map.put("b01ps",b01ps==null?'':b01ps.value);
	map.put("scope",visibleId);
	map.put("nbase",user_nbase);
	map.put("a0100",user_a0100);
	
    Rpc({functionId:'9028000802',success:saveOK},map); 
}

//显示通知
function show_tongzhi(flag){
	if("tongzhi" == flag){
		Ext.query('.hj-wzm-six-top a')[1].className ='';
		Ext.query('.hj-wzm-six-top a')[0].className ='hj-wzm-six-top-a';
	 document.getElementById("pingjia").style.display="none";
     document.getElementById("tongzhi").style.display="block";
	}
	else{
		
		Ext.query('.hj-wzm-six-top a')[0].className ='';
		Ext.query('.hj-wzm-six-top a')[1].className ='hj-wzm-six-top-a';
		document.getElementById("pingjia").style.display="inline";
	    document.getElementById("tongzhi").style.display="none";
	    document.getElementById("contents").style.visibility="visible";
	    document.getElementById("contents").focus();//定位到"评价"按钮上
	    window.scrollTo(0,document.body.scrollHeight);//控制滚动条到底部
	}
}
function showtask(val) {
	var taskdetail = Ext.get("taskdetail");
	var show = val;
	if(!val)
		show=!taskdetail.isVisible(false);
	taskdetail.setDisplayed(show);

	var showTaskBtn = Ext.get("showTaskBtn");
	if (taskdetail.isVisible(false))
		showTaskBtn.setHtml("收起");
	else
		showTaskBtn.setHtml("展开");
}

/*----textarea自适应高度----------------------------*/
var observe;
if (window.attachEvent) {
    observe = function (element, event, handler) {
        element.attachEvent('on'+event, handler);
    };
}
else {
    observe = function (element, event, handler) {
        element.addEventListener(event, handler, false);
    };
}
function resize(text) {
	var parentElement = text.parentNode;
	if(120 < text.scrollTop + text.scrollHeight){
    text.style.height = text.scrollTop + text.scrollHeight+'px';
	parentElement.style.height = 40+text.scrollTop + text.scrollHeight+'px';
	//text.style.height = 'auto';
	}
	else{
		parentElement.style.height = "155px";
		text.style.height="120px";
	}
}

//返回hr
function returnUrl()
{
	var url = Ext.getDom('returnurl').value;
	location.href =url;
}
function initTextarea() {
    var thisSummaryText = document.getElementById('thisSummary');
    var nextPlanText = document.getElementById('nextPlan');
    var thisWorkSummaryText = document.getElementById('thisWorkSummary');
    var nextWorkSummaryText = document.getElementById('nextWorkSummary');
}
//导出
function exportExcel(){
	var p0100 = Ext.get("p0100").getValue();
    var user_nbase = Ext.get('user_nbase').getValue();
    var user_a0100 = Ext.get('user_a0100').getValue();
    var zhouzj = Ext.getDom('zhouzj').value;//是否启用周总结
    var zhouzjpx = Ext.getDom('zhouzjpx').value;//周总结-培训
    var thisWorkSummaryText = document.getElementById('thisWorkSummary').value;
    var nextWorkSummaryText = document.getElementById('nextWorkSummary').value;
    var thisWorkPlan = document.getElementById('thisWorkPlan').value;

    var summarydesc = Ext.getDom('summarydesc').innerHTML;
    var userName = Ext.getDom('user_name').innerHTML
    var cyclenow = Ext.getDom("cycle").value;
    
	var hashvo = new HashMap();
    hashvo.put("p0100",p0100);
    hashvo.put("cyclenow", cyclenow); // 1周报、2月报、3季报、5半年报、4年报
    hashvo.put("nbase",user_nbase);
    hashvo.put("a0100",user_a0100);
    hashvo.put("userName",userName);
    hashvo.put("summarydesc",summarydesc);
    hashvo.put("zhouzj",zhouzj);
    hashvo.put("zhouzjpx",zhouzjpx);
    hashvo.put("thisWorkPlan",thisWorkPlan);
    hashvo.put("thisWorkSummary",thisWorkSummaryText);
    hashvo.put("nextWorkSummary",nextWorkSummaryText);
    //hashvo.put("type","orgsumshow");async:false,scope:this
	Rpc({functionId:'WP20000005', success:function(response){
		var result = Ext.JSON.decode(response.responseText);
		if(result.succeed){
            //zhangh 2020-3-5 下载改为使用VFS
            var outName=result.fileName
            outName = decode(outName);
            var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
            }
		else{
		    Ext.showAlert(result.message);
        }
		}},hashvo);
	//alert("导出");
	var sewy='';

}
/*----textarea自适应高度----------------------------*/

Ext.onReady(function (){
	
	var ishr = Ext.getDom('ishr').value;
    //初始化总结区间根据参数设置显示     根据参数设置显示
    var summaryTypeJson = Ext.getDom('summaryTypeJson').value;
 	loadSummaryTypeList(summaryTypeJson);
    //初始化总结范围（月份 周 和半年），根据参数设置显示
    renderFillPeriod();
    setPriv();
    //通过人力地图进来时不显示人力地图，与计划监控保持一致
    if(ishr!="true"){
	    initpersonmap(Ext.getDom('maptype').value,'month','week','','','','1','',true);//初始化 人力地图下拉 
	 	initselectmaplist();  //下拉框显示与隐藏
    }
    Ext.get(document).on("click", function(e) {
    	e = e || window.event;
        var target = e.target || e.srcElement;
        var parentNode = target.parentNode||"";
	    var parentNodeClassName = parentNode.className||"";
        // 任务界面之外的区域响应点击事件时关闭
     	if (parentNodeClassName.indexOf("hj-wzm-tdzb-a") < 0){
       		if(document.getElementById("taskFrame").style.display == "block"){
        		document.getElementById("taskFrame").style.display = "none";
        		document.getElementById("leftArrow").style.display = "none";
        		//location.reload(true);
        		getMyWorkTaskList(task_type);
        		showtask(true);
       		}
        }
        if (target.getAttribute("id") == "searchinput")
        	return false;
        if (target.getAttribute("id") == "ait")
        	return false;
        if (target.getAttribute("id") == "showselectmaplist") //地图下拉
        	return false;
        if (target.getAttribute("id") == "maparrow") // 箭 头
        	return false;
        if (target.getAttribute("id") == "summaryyear") // 选择日期 年
        	return false;
        if (target.getAttribute("id") == "summarymonth") // 选择日期 月
        	return false;
        if (target.getAttribute("id") == "yeartitle") // 选择日期 年
        	return false;
        if (target.getAttribute("id") == "typetitle") // 选择日期
        	return false;
       	if (target.getAttribute("id") == "summarytype") // 选择日期
            return false;
        if (target.getAttribute("id") == "timetitle") // 选择日期
        	return false;
        if (target.getAttribute("id") == "monthtitle") // 选择日期
        	return false;
        if (target.getAttribute("id") == "monthlist") // 选择日期
        	return false;
        if (target.getAttribute("id") == "visibleRange") // 选择可见权限
            return false;
        if (target.getAttribute("id") == "visibleList") // 选择可见权限
            return false;
        if (target.getAttribute("id") == "visibleA") // 选择可见权限
            return false;
        if (target.getAttribute("id") == "summarylist") // 汇总计划总结
            return false;
        if (target.getAttribute("id") == "autosummary") // 汇总计划总结
            return false;
        if(target.getAttribute("id")=="autosummarybtn")//汇总计划总结
            return false;

        var dropDownList = Ext.query("*[class$=dropdownlist]");
        for (i=0;i<dropDownList.length;i++){
            var aDropDown = Ext.get(dropDownList[i]);

            if (target != dropDownList[i])
                aDropDown.setDisplayed(false);
        }
    });
    
	//  取cookie中存的类型 
	if( Ext.getDom('iscookie').value == "true" )
	{
		//获取cookie字符串 
		var strCookie=document.cookie; 
		//将多cookie切割为多个名/值对 
		var arrCookie=strCookie.split("; "); 
		var valuecookie=""; 
		//遍历cookie数组，处理每个cookie对 
		for(var i=0;i<arrCookie.length;i++){ 
		var arr=arrCookie[i].split("="); 
			//找到名称为valuecookie的cookie，并返回它的值 
			if((Ext.get('user_a0100').getValue()+Ext.get('user_nbase').getValue()+"worksummary")==arr[0]){ 
				valuecookie=unescape(arr[1]); //解密 
				break; 
			} 
		} 
		if(valuecookie != "")
		{
			var value2 = valuecookie.split("&");
			if(value2[1].split(":")[1]!="" && value2[0].split(":")[1]!=""){
				hidemonth('0',value2[1].split(":")[1],value2[0].split(":")[1]);
  				return;
  			}
  		}
	}
	//linbz 优化
	//init();
	searchSummary('','','','');
	timeSumShow();
	if("" == Ext.getDom('p0115').value || "01" == Ext.getDom('p0115').value)
		Ext.getDom('submitDate').innerHTML="";
	else
		Ext.getDom('submitDate').innerHTML="提交时间： ${workSummaryForm.p0114}";
	
	var selvalue_b = Ext.getDom("cycle").value ;
	var isemail = Ext.getDom('isemail').value;
	//除周报外，其它都不需要显示月份选择
    Ext.get("summarymonth").setDisplayed(1 == selvalue_b || 2 == selvalue_b);
    Ext.get("summaryyear").setDisplayed(1 != selvalue_b && 2 != selvalue_b);

	if("true" == ishr ||"true" == isemail)
	{
		var thisSummaryText = document.getElementById('thisSummary');
	    var nextPlanText = document.getElementById('nextPlan');
	    var thisWorkSummaryText = document.getElementById('thisWorkSummary');
	    var nextWorkSummaryText = document.getElementById('nextWorkSummary');
	    //resize(thisSummaryText);
	    //resize(nextPlanText);
	    //resize(thisWorkSummaryText);
	    //resize(nextWorkSummaryText);
		if("true" == ishr)
		document.getElementById("btn_return").style.display="inline";
		
		document.getElementById("menubar").style.display="none";	
		Ext.get('weeks').setDisplayed(false);
	    Ext.get('quaters').setDisplayed(false);
	    Ext.get('halfyears').setDisplayed(false);
	    Ext.query('.hj-wzm-all-right')[0].style.display="none";
	    Ext.query('.hj-wzm-all-left')[0].style.marginRight="0px";
	}else
	{
	    Ext.get('weeks').setDisplayed(1==selvalue_b);
	    Ext.get('quaters').setDisplayed(3==selvalue_b);
	    Ext.get('halfyears').setDisplayed(5==selvalue_b);
	    //Ext.get('months').setDisplayed(2==selvalue_b);
		Ext.get('typelist').setDisplayed(false);
		Ext.get('typetitle').setHtml("${workSummaryForm.typetitle}");
		
	}
    
});
/**
 * 隐藏显示本期工作计划
 */
function displayPlan(id){
	var idplan = document.getElementById(id);
	if(idplan){
	   //更换箭头图标 haosl 2018-2-7
	   var img = document.getElementById(id+"Img");
	   var styledis = document.getElementById(id).style.display;
	   if(styledis == "none"){
		   document.getElementById(id).style.display="inline";
		   if(img)
	       	  img.src='/workplan/image/jiantou_up.png';
	   }else{
		   document.getElementById(id).style.display="none";
		   if(img)
		   	  img.src='/workplan/image/jiantou.png';
	   }
	   adapt.adaptTextareaHeight(idplan);
	}
}

// 设置填报范围变量 chent 20180329
function setPriv(){
    // 个人计划填报范围整理
    weeklySummary.cycleFunction_person = ",";
    var person_cycle_function = Ext.getDom("personCycleFunction").value;
    var org_cycle_function = Ext.getDom("orgCycleFunction").value;
    if(!Ext.isEmpty(person_cycle_function)){
        person_cycle_function = Ext.decode(person_cycle_function);
    }
    if(!Ext.isEmpty(org_cycle_function)){
        org_cycle_function = Ext.decode(org_cycle_function);
    }
    for(var p in person_cycle_function){
        var obj = person_cycle_function[p];
        if(obj['s0'] != undefined ){// 年
            weeklySummary.cycleFunction_person += "4,";
        }else if(obj['s1'] != undefined ){// 半年
            weeklySummary.cycleFunction_person += "5,";
        }else if(obj['s2'] != undefined ){// 季度
            weeklySummary.cycleFunction_person += "3,";
        }else if(obj['s3'] != undefined ){// 月
            weeklySummary.cycleFunction_person += "2,";
        }else if(obj['s4'] != undefined ){// 周
            weeklySummary.cycleFunction_person += "1,";
        }else{
            continue;
        }
    }

    // 部门计划填报范围整理
    weeklySummary.cycleFunction_org = ",";
    for(var p in org_cycle_function){
        var obj = org_cycle_function[p];
        if(obj['s0'] != undefined ){// 年
            weeklySummary.cycleFunction_org += "4,";
        }else if(obj['s1'] != undefined ){// 半年
            weeklySummary.cycleFunction_org += "5,";
        }else if(obj['s2'] != undefined ){// 季度
            weeklySummary.cycleFunction_org += "3,";
        }else if(obj['s3'] != undefined ){// 月
            weeklySummary.cycleFunction_org += "2,";
        }else if(obj['s4'] != undefined ){// 周
            weeklySummary.cycleFunction_org += "1,";
        }else{
            continue;
        }
    }
}
/**
 * @param {} isShowALert
 * @return {Boolean}
 */
function privCheck(isShowALert){
    var cycleFunction;
    var type = Ext.getDom("type").value;
    var str = "";
    if(type=="person") {
        cycleFunction = weeklySummary.cycleFunction_person;
        str +="个人";
    }
    else if(type=="org") {
        cycleFunction = weeklySummary.cycleFunction_org;
        str +="部门";
    }
    var cyclenow = Ext.getDom("cycle").value;
    //cycleFunction得做非空判断。 haosl 2019-5-20
    if(cycleFunction && cycleFunction.indexOf(','+cyclenow+",") == -1){

        if (cyclenow == '4') {// 年
            str += "年度总结";
        } else if (cyclenow == '5') {// 半年
            str += "半年总结";
        } else if (cyclenow == '3') {// 季度
            str += "季度总结";
        } else if (cyclenow == '2') {// 月
            str += "工作月报";
        } else if (cyclenow == '1') {// 周
            str += "工作周报";
        }
        if(isShowALert!="false")
            Ext.Msg.alert('提示信息', '您没有填写'+str+'的权限！');
        return false;
    }
    return true;
}

</script>
<style type="text/css">
textarea {overflow:hidden;height:120px;}
</style>
