<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.businessobject.workplan.WorkPlanUtil"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%
    String curUsername="";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    if(userView != null)
    {
        curUsername=userView.getUserName();
    }
    String strHavePublicRole="false";
    
    String strHaveShowDetailPri="false";//是否有权限点击任务名称查看任务详情 chent 20160413
    if(userView.hasTheFunction("0KR010102")) {
		strHaveShowDetailPri="true"; 
	}
    String strHaveCommuctionPri="false";//是否有计划沟通权限 chent 20160413
    if(userView.hasTheFunction("0KR010103")) {
		strHaveCommuctionPri="true"; 
	}
    String strHaveSummaryPri="false";//是否有工作总结权限 chent 20160413
    if(userView.hasTheFunction("0KR010104")) {
		strHaveSummaryPri="true"; 
	}
	

	
%>

<html>
  <head>

    <title></title>    
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	
<%--	<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />--%>
	<link rel="stylesheet" type="text/css" href="/workplan/style/treegrid.css">
    <link href="/workplan/style/task.css" rel="stylesheet" />
	<link rel="stylesheet" type="text/css" href="/workplan/style/stars.css">
	<link rel="stylesheet" type="text/css" href="/workplan/style/workplan.css">
	
	<script type="text/javascript" src="/components/tableFactory/customs/ext_custom.js"></script>
    <script type="text/javascript" src="/workplan/js/global.js"></script>

    <script type="text/javascript" src="/workplan/js/util.js"></script>
    <script type="text/javascript" src="/workplan/js/biz.js"></script>
    <script type="text/javascript" src="/workplan/js/prompt.js"></script>
    <script type="text/javascript" src="/workplan/js/stars.js"></script>
	
	<script language="JavaScript" src="/js/wz_tooltip.js"></script>
	<script language="JavaScript" src="/workplan/js/workplan.js"></script> 
	<script type="text/javascript" src="/components/personPicker/PersonPicker.js"></script>
	<script type="text/javascript" src="/workplan/js/commuction.js"></script>
<%--	<script type="text/javascript" src="/components/sysExtPlugins/CodeSelectField.js"></script>--%>
	<script type="text/javascript" src="/components/extWidget/field/CodeTreeCombox.js"></script>
	<script language="JavaScript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
  </head>
  
  <script type="text/javascript">  
</script>  
<form id='planForm' action="">
<% 
	String plantype=(String)request.getParameter("type");
	String periodtype=(String)request.getParameter("periodtype");
	String periodyear=(String)request.getParameter("periodyear");
	String periodmonth=(String)request.getParameter("periodmonth");
	String periodweek=(String)request.getParameter("periodweek");
	String p0723=(String)request.getParameter("p0723");
	String objectid=(String)request.getParameter("objectid");
	String p0700=(String)request.getParameter("p0700");
	String subobjectid=(String)request.getParameter("subobjectid");
	String subpersonflag=(String)request.getParameter("subpersonflag");
	String concerned_bteam=(String)request.getParameter("concerned_bteam");
	String concerned_cur_page=(String)request.getParameter("concerned_cur_page");
	String deptleader=(String)request.getParameter("deptleader");
	String plandesign=(String)request.getParameter("plandesign");
	String superconcernedjson=(String)request.getParameter("superconcernedjson");
	String needcheck=(String)request.getParameter("needcheck");
	String bhr=(String)request.getParameter("bhr");//业务用户登录
	String task_path=(String)request.getParameter("task_path");//业务用户登录
	String returnurl=(String)request.getParameter("returnurl");//业务用户登录
	String fromflag=(String)request.getParameter("fromflag");//来源 hr email
	plantype=(plantype!=null)?plantype:"";
	periodtype=(periodtype!=null)?periodtype:"";
	periodyear=(periodyear!=null)?periodyear:"";
	periodmonth=(periodmonth!=null)?periodmonth:"";
	periodweek=(periodweek!=null)?periodweek:"";
	p0700=(p0700!=null)?p0700:"";
	p0723=(p0723!=null)?p0723:"";
	if("".equals(plantype)){
		if("1".equals(WorkPlanUtil.decryption(p0723)))
			plantype = "person";
		else
			plantype = "org";
	}
	objectid=(objectid!=null)?objectid:"";	
	concerned_bteam=(concerned_bteam!=null)?concerned_bteam:"";
	concerned_cur_page=(concerned_cur_page!=null)?concerned_cur_page:"1";
	subobjectid=(subobjectid!=null)?subobjectid:"";
	subpersonflag=(subpersonflag!=null)?subpersonflag:"";
	deptleader=(deptleader!=null)?deptleader:"";
	plandesign=(plandesign!=null)?plandesign:"1";
	superconcernedjson=(superconcernedjson!=null)?superconcernedjson:"";
	needcheck=(needcheck!=null)?needcheck:"";
	bhr=(bhr!=null)?bhr:"false";
	task_path=(task_path!=null)?task_path:"";
	returnurl=(returnurl!=null)?returnurl:"";
	fromflag=(fromflag!=null)?fromflag:"";

    //plantype="1";
%>

<body Style="width:100%">
<jsp:include page="prompt_box.jsp"></jsp:include>

	<div class="hj-wzm-all">
		<input id="plantype" type="hidden" value="<%=plantype %>">
		<input id="periodtype" type="hidden" value="<%=periodtype %>">
		<input id="periodyear" type="hidden" value="<%=periodyear %>">
		<input id="periodmonth" type="hidden" value="<%=periodmonth %>">		
		<input id="periodweek" type="hidden" value="<%=periodweek %>">		
		<input id="objectid" type="hidden" value="<%=objectid %>">			
		<input id="deptleader" type="hidden" value="<%=deptleader %>">	
						
		<input id="subobjectid" type="hidden" value="<%=subobjectid %>">	
		<input id="subpersonflag" type="hidden" value="<%=subpersonflag %>">	
		<input id="superconcernedjson" type="hidden" value="<%=superconcernedjson %>">			
		<input id="concerned_bteam" type="hidden" value="<%=concerned_bteam %>">			
		<input id="concerned_cur_page" type="hidden" value="<%=concerned_cur_page %>">			
		
		<input id="p0700" type="hidden" value="<%=p0700%>">			
		<input id="p0723" type="hidden" value="<%=p0723%>">	
		<input id="ismyplan" type="hidden" value="">	
		<input id="plandesign" type="hidden" value="<%=plandesign %>">
		<input id="planscope" type="hidden" value="4">
		<input id="needcheck" type="hidden" value="<%=needcheck %>">
		<input id="bhr" type="hidden" value="<%=bhr %>">
		<input id="fromflag" type="hidden" value="<%=fromflag %>">
		<input id="task_path" type="hidden" value="<%=task_path %>">
		<input id="returnurl" type="hidden" value="<%=returnurl %>">
		<input id="curUsername" type="hidden" value="<%=curUsername %>">
    	<div class="hj-wzm-all-table">
  	            <div class="hj-wzm-all-right" id="rightDiv" style="display:none;">
                <dl class="hj-right-dl" id="concerneddivx">
                    <dt>
                    	<% if("person".equals(plantype)){ %>
                    		<a href="javascript:loadMyPlan()"><img class="img-circle" id="my_image" style="display:none;"/></a>
                    	<% }else if("org".equals(plantype)){ %>
                    	<a href="javascript:clickConcerneders('','','','org')"><img class="img-circle" id="my_image"/></a>
                    	<% } %>
                    </dt>
                     <dd>
                     
	                      <div id="concerneddiv" class="hj-wzm-right-wgzda">
	                    <div style="display:block; position:absolute; z-index:99; margin-left:2px;">
	                      <img id="teamlistimg" src="/workplan/image/list.png" 
	                            dropdownName="dropdownBox" onclick="backMyTeam()" title="切换到列表显示" style="cursor:pointer"/>
	                    </div> 
	                    &nbsp;&nbsp;&nbsp;&nbsp;
	                    
	                    <a id="concernedtitle" dropdownName="dropdownBox" onclick="dropdownAttentionMenu()" style="cursor:pointer;">我关注的</a>
	                    <img id="concernedimg" src="/workplan/image/baijiant.jpg" 
	                            dropdownName="dropdownBox" onclick="dropdownAttentionMenu()" style="cursor:pointer" />
	                	</div> 
                     
                     </dd>
                </dl>   
                
				<%--        --%>
                         
<%--                <div id="concerneddiv" class="hj-wzm-right-wgzd">--%>
<%--                    <div style="display:block; position:absolute; z-index:99; margin-left:3px;margin-top:-2px;">--%>
<%--                      <img id="teamlistimg" src="/workplan/image/list.png" --%>
<%--                            dropdownName="dropdownBox" onclick="backMyTeam()" title="切换到列表显示" style="cursor:pointer"/>--%>
<%--                    </div> --%>
<%--                    --%>
<%--                    --%>
<%--                    <a id="concernedtitle" dropdownName="dropdownBox" onclick="dropdownAttentionMenu()" style="cursor:pointer">我关注的</a>--%>
<%--                    <img id="concernedimg" src="/workplan/image/baijiant.jpg" --%>
<%--                            dropdownName="dropdownBox" onclick="dropdownAttentionMenu()" style="cursor:pointer" />--%>
<%--                </div>--%>
                <div id="xshangjpg" class="hj-wzm-right-xshang" align="center" style="margin:0 auto; display:none;"><a href="javascript:upConcerneders()" ><img src="/workplan/image/xshang.jpg"/></a></div>
                <div id="backSuperDiv" align="center">
                <a href="javascript:backSuper()">返回上级 </a></div>
                <div id="concernedersdiv" class="hj-wzm-right-dllb">
                                    
                </div>
                <div class="hj-wzm-right-xxia" align="center" id="xxiajpg"><a href="javascript:downConcerneders()"><img src="/workplan/image/xxia.jpg" /></a></div>
              	<a id="hideRightDiv"  href="javascript:void(0)"  onclick="hideRightDiv()" style="position:absolute;right:10px;top:7px">
<%--            		<img src="/workplan/image/right_arrows.png"  title="隐藏人力地图"/>--%>
            		<img src="/module/system/questionnaire/images/directingright.png" width="25px;"height="25px;"  title="隐藏人力地图"/>
            	</a>
              
            </div>
        <div class="hj-wzm-all-left" id="leftDiv">
        	<a id="showRightDiv"  href="javascript:void(0)"  onclick="showRightDiv()" style="position:absolute;right:10px;top:7px; display:none;">
<%--            		<img src="/workplan/image/left_arrows1.png"  title="显示人力地图"/>--%>
            		<img src="/module/system/questionnaire/images/directingleft.png" width="25px;"height="25px;"  title="显示人力地图"/>
            </a>
        	<div class="hj-wzm-one" id ="one_div" >
            	<div id ="plantype_div" class="hj-wzm-one-left" style="width:260">
                    <a id="periodtypename" dropdownName="dropdownBox"
                         onclick="dropdownPeriodType()" style="width:80px;"> <img  dropdownName="dropdownBox" src="/workplan/image/jiantou.png"/></a>
                    <a id="periodname" dropdownName="dropdownBox"
                         onclick="dropdownPeriodYear()" style="width:100px;"> <img  src="/workplan/image/jiantou.png"/></a>
            
                </div>
                 <div class="hj-wzm-one-right-return" id="btn_return" style="display:none;">
                    <a href="###" onclick="returnLast()" >返回</a>
                </div>
<%--                <div id="div_plandesign" class="hj-wzm-one-right">--%>
<%--                	<a id="a_plandesign" class="hj-wzm-or-a" onclick="planDesign()" >计划制订</a>--%>
<%--                    <a id="a_plantrace" onclick="planTrace()">计划跟踪</a>--%>
<%--                </div>--%>
                 
                <div class="hj-wzm-one-right2" id="div_halfyears" style="display:none;position:absolute;width:120px;right:350px;">
                    <a id='halfyear1' href="###" id="a_halfyears" onclick="selectPeriodWeek('1')" >上半年</a>
                    <a id='halfyear2' href="###" id="a_halfyears" class="hj-wzm-or-a" onclick="selectPeriodWeek('2')" >下半年</a>
                </div>
                
                <div class="hj-wzm-one-right2" id="div_quaters" style="display:none;position:absolute;width:240px;right:350px;">
                    <a id='quarter1' href="###" onclick="selectPeriodWeek('1')" >第一季度</a>
                    <a id='quarter2' href="###" onclick="selectPeriodWeek('2')" >第二季度</a>
                    <a id='quarter3' href="###" onclick="selectPeriodWeek('3')" >第三季度</a>
                    <a id='quarter4' href="###" onclick="selectPeriodWeek('4')" >第四季度</a>
                </div>
              <div class="hj-wzm-one-right2" id="div_weeks" style="display:none;position:absolute;width:300px;right:350px;">
                    <a href="###" onclick="selectPeriodWeek('1')" >第一周</a>
                    <a href="###" onclick="selectPeriodWeek('2')" >第二周</a>
                    <a href="###" onclick="selectPeriodWeek('3')" >第三周</a>
                    <a href="###" onclick="selectPeriodWeek('4')" >第四周</a>
                    <a href="###" style = "display: none" id='fiveweek' onclick="selectPeriodWeek('5')">第五周</a>
              </div>
            
             <div class="hj-wzm-clock dropdownlist"  id="monthlist" style="display: none;">
                    <ul style="text-align:center">
                        <span style="color:#549FE3;">
                        <a   dropdownName="monthbox" href="javascript:yearchange(-1);"><img dropdownName="monthbox" src="/workplan/image/left2.gif" /></a>
                        <span id='myeartitle'></span>年   <a  dropdownName="monthbox" href="javascript:yearchange(1);"><img dropdownName="monthbox" src="/workplan/image/right2.gif" /></a>
                        </span>
                    </ul>
                    <ul id="months">
                        <li id='li1'><a href="###" onclick="selectPeriodMonth(1)" >&nbsp;1月</a></li>
                        <li id='li2'><a href="###" onclick="selectPeriodMonth(2)" >&nbsp;2月</a></li>
                        <li id='li3'><a href="###" onclick="selectPeriodMonth(3)" >&nbsp;3月</a></li>
                        <li id='li4'><a href="###" onclick="selectPeriodMonth(4)" >&nbsp;4月</a></li>
                        <li id='li5'><a href="###" onclick="selectPeriodMonth(5)" >&nbsp;5月</a></li>
                        <li id='li6'><a href="###" onclick="selectPeriodMonth(6)" >&nbsp;6月</a></li>
                        <li id='li7'><a href="###" onclick="selectPeriodMonth(7)" >&nbsp;7月</a></li>
                        <li id='li8'><a href="###" onclick="selectPeriodMonth(8)" >&nbsp;8月</a></li>
                        <li id='li9'><a href="###" onclick="selectPeriodMonth(9)" >&nbsp;9月</a></li>
                        <li id='li10'><a href="###" onclick="selectPeriodMonth(10)" >10月</a></li>
                        <li id='li11'><a href="###" onclick="selectPeriodMonth(11)" >11月</a></li>
                        <li id='li12'><a href="###" onclick="selectPeriodMonth(12)" >12月</a></li>
                    </ul>
                </div>
            </div>
            <div id="dropdownBox" tabindex="-1" class="hj-wzm-one-dinwei" style="display:none;" onblur="hideDropdownBox()">
               <ul>
                   <li><a ></a></li>
                   <li><a ></a></li>           
               </ul>
           </div>
            <div class="hj-wzm-two">
            	<a ><img class="img-circle" id="plan_owner_image" style="display:none"/></a>
                <p><a id="plantitle" style="font-size: 20px!important"></a></p>
                <div id="div_planstatus" class="hj-wzm-two-dinwei"><a id="planstatus">已批准</a></div>
                <div id="planreject" style="display:none" class="hj-wzm-two-pizhun"><a onclick="rejectPlan()"><img src="/workplan/image/tuihui.png" /></a></div>                
                <div id="planapprove" style="display:none" class="hj-wzm-two-pizhun"><a onclick="approvePlan()"><img src="/workplan/image/pizhun.png" /></a></div>
                 <div id="btnApprove" style="display:none" class="hj-wzm-two-pizhun">   <a   onclick="transitBatch('approve')"><img src="/workplan/image/pizhun.png" /></a>
                                </div>
            </div>
      
            <div id="taskgrid" oldW="0">
	       
            </div>
            
            <div id="editask" class="hj-wzm-four">         

            	<table width="100%" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td width="48%" height="30" >
	                    <input id="task_name" type="text" value="创建任务" class="hj-wzm-four-rwmc"
	                     onFocus="taskFocus(this)" onBlur="taskBlur(this)" />
                     </td>
                     <!--  
                    <td width="30%" height="30" >
	                    <input id="task_desc" type="text" value="任务描述" class="hj-wzm-four-rwms"
	                      onFocus="taskFocus(this)" onBlur="taskBlur(this)" />
                    </td>
                    -->
                    <td width="15%" height="30" >
<!--                        <input type="text" value="参与人" class="hj-wzm-four-cyr" onFocus="taskFocus(this)" onBlur="taskBlur(this)"  />-->
                        <div prompt="prompt-box" id="add-member" override="append" edit="always" class="hj-wzm-four-cyr prompt-box-edit-always1"
                                 content="content-add-member" store="store-add-member" 
                                 placeHolder="placeholder-add-member"
                                 onclick="basic.biz.pickCyr(this)">
                                    
                            <input type="hidden" id="store-add-member" />
                            <div id="content-add-member" style="hj-wzm-four-cyr">负责人
                            </div>
                        </div>
                    
                    </td>
                    <td width="10%" height="30" >
	                    <input id="task_rank" type="text" value="权重(0-100)" class="hj-wzm-four-qc" 
	                     onFocus="taskFocus(this)" maxlength="8" onBlur="taskBlur(this)" />
                     </td>
                    <td width="8%" height="30" >
                        <input type="text" name="startTime" id="task_startdate"  class="hj-wzm-four-ksrq" value="开始日期"/>
                     </td>
                     <td width="3%" height="30" align="center" style="border-right:1px #D5D5D5 dashed;">
                        <img src="/workplan/image/workplantime.bmp" width="15px"  height="15px" plugin="datetimeselector"   inputname="startTime" format="Y.m.d"/>
                     </td>
                    <td width="8%" height="30" >
                        <input type="text" name="endTime" id="task_enddate"  class="hj-wzm-four-jsrq" value="结束日期"/>
                    </td>
                    <td width="3%" height="30" align="center"  style="border-right:1px #D5D5D5 dashed;">
	             		<img src="/workplan/image/workplantime.bmp"  width="15px"  height="15px"  plugin="datetimeselector"   inputname="endTime" format="Y.m.d"/>
                    </td>                    
                    <td width="100%"><input type="button" value="+" style='margin-right:-1px;' class="hj-wzm-four-jh" onclick="addTask()"/></td>
                  </tr>
                </table>

            </div>
                    
            
            <div class="hj-wzm-five">
            	<div id="task-toolbar" class="hj-wzm-five-top">
            	    <a id="a_daochu" href="javascript:void(0)" onclick="selectAddMenu('exportTask')" title="导出" ><img src="/images/daochu.png" /></a>
                    <a id="a_addMenu" dropdownName="dropdownBox" href="javascript:void(0)" onclick="dropdownAddMenuList()" ><img dropdownName="dropdownBox" src="/workplan/image/jiahao.png" /></a>
                    <a id="a_deltask" href="javascript:void(0)" onclick="delTask()" ><img src="/workplan/image/chahao.png" /></a>
                    <a id="a_addfollower"  href="javascript:void(0)"  onclick="basic.biz.pickFollower(this)">
                      <img id="addFollower" prompt="prompt-box" src="/workplan/image/ait.png"  title="添加计划关注人"/>
                    </a>
                     <% if (userView.hasTheFunction("0KR010101")) {
                  	   		if(userView.hasTheFunction("0KR01010101")) {
                  				strHavePublicRole="true";  
            	   			}%>
		                <a id="schemeSetting"  href="javascript:void(0)"  onclick="schemeSetting(['',<%=strHavePublicRole %>,''])">
		            		<img src="/workplan/image/settings.png"  title="栏目设置"/>
		            	</a>
					<%}%>
       				<a id="showSubtask" href="javascript:void(0)" onclick="showOrHideSubTask(true)" title="显示下属任务" ><img src="/images/new_module/subtask.png" /></a>
       				<a id="hideSubTask" href="javascript:void(0)" onclick="showOrHideSubTask(false)" title="隐藏下属任务" style="display:none"><img src="/images/new_module/subtask1.png" /></a>
                    <input id="btnPublish" type="button" class="hj-wzm-five-fabu" value="发布" onclick="publishPlan()"/>
                    <input id="btnRepublish" type="button" class="hj-wzm-five-fabu" style="display:none;width:70px;"
                    		value="重新发布" onclick="transitBatch('publish')" />

                    
                    <a id="planscopename" dropdownName="dropdownBox"  class="hj-wzm-five-a2" onclick="dropdownPlanScope()" style="cursor:pointer"><img  src="/workplan/image/suo.jpg" />上级可见 <img src="/workplan/image/jiantou.png" /></a>
                </div>
            </div>   
          
             <div class="hj-wzm-five" style="overflow:hidden;">    
                
                <div class="hj-wzm-five-bottom bh-space"  id="follower" style="width:100%;height:auto!important;height:100px;" >
                	<p id="lbl_follower">关注我计划的：</p>
                     <div id="followerdiv">                         
                     </div>
                 </div>
         
            </div>
            
            <div class="clear"></div> 
            <div class="hj-wzm-six">
            	<div class="hj-wzm-six-top" style="border-bottom:1px #D5D5D5 solid;">
            	<% if(strHaveCommuctionPri == "true"){ //计划沟通权限%>
                	<a href="javascript:void(0)" onclick="refreshMessageContentlist(this)" class="hj-wzm-six-top-a">计划沟通&nbsp;</a>
               	<% } %>
                	<%-- cycle 报告周期：1=周报, 2=月报 --%>
                <% if(strHaveSummaryPri == "true"){ //工作总结权限%>
                    <a href="javascript:basic.biz.selectReportCycle()" id="workReports" dropdownName="dropdownBox">
                    	<font dropdownName="dropdownBox" id="reportCycleDesc">工作总结</font>
                    	<img dropdownName="dropdownBox" src="/workplan/image/jiantou.png" />
                    	<input id="score" type="hidden" />
                    </a>
                 <% } %>
                    <a href="javascript:void(0)" style="display:none;">操作历史&nbsp;</a>
                </div>
                <div id="tongzhi"></div>
            </div>
            <div id ="fileHint" class="hj-zm-six-duihua-dinwei">
            	
            </div>
            </div>
        </div>
    </div>
    
	<!-- 任务界面展示在弹出层 lium -->
    <img src="/workplan/image/left-arrow.png" id="leftArrow" />
    <div id="taskFrame" name="planPage" style='min-width:450px;'><iframe name="iframe_task" id="iframe_task" scrolling="no" frameborder="0"></iframe></div>
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
    		z-index: 10001;
    		display: none;
    	}
    	#taskFrame>iframe {
    		width: 100%;
    		padding: 0px;
    		margin: 0px;
    		border: 0;
    		height: 800px;
    	}
    </style>
</body>
</form>
<script language='javascript'>
  var form_id="planForm";  //form_id,用于上传沟通附件
  Ext.Loader.setConfig({
    enabled: true,
    paths:{
    	'Ext.ux':'../ux',
    	'SYSP':'/components/sysExtPlugins',
    	'EHR.extWidget.field':'/components/extWidget/field'
      }
    });

  //Ext.Loader.setPath('Ext.ux', '../ux');
 // Ext.Loader.setPath('SYSP', '/components/sysExtPlugins');
    
    Ext.require([
        'Ext.data.*',
        'Ext.grid.*',
        'Ext.tree.*',
        'Ext.tip.*',
        'Ext.ux.CheckColumn',
        'EHR.extWidget.field.DateTimeField',
        'EHR.extWidget.field.BigTextField'
    ]);
    
    Ext.onReady(function() {
        Ext.tip.QuickTipManager.init();
        init();
    });
    window.onresize=function(){
    return;  
	     if (g_tree!=null){
	          var grid_width=getTreeGridWidth(false);
	          if (grid_width>0 && wpm.grid_width!=grid_width){
	           // getPlanTaskList();
		      //  wpm.grid_width=grid_width;
		       // g_tree.setWidth(wpm.grid_width);  
	          }
	      }        
    }
    


	Ext.onReady(function() {
		// 复写日期控件提示信息 start
		Ext.define("Ext.locale.zh_CN.picker.Date", {
		    override: "Ext.picker.Date",
		    nextText: '下个月 ',
		    prevText: '上个月',
		    monthYearText: '选择一个月',
		    todayTip: "{0}",
		});
		// 复写日期控件提示信息 end
		var fromflag = document.getElementById("fromflag").value;
        if("hr" == fromflag){
			document.getElementById("div_quaters").style.right = 100;
			document.getElementById("div_weeks").style.right = 100;
			document.getElementById("div_halfyears").style.right = 100;
        }
	    Ext.get(document).on("click", function(e) {
	        e = e || window.event;
	        var target = e.target || e.srcElement;
	        var parentNode = target.parentNode||"";
	        var parentNodeClassName = parentNode.className||"";
	        //var prePreNode = preNode.parentNode||"";
	        //var prePreNodeClassName = prePreNode.className||"";
	        // 展示任务的弹出层关闭的时机: 计划界面任务列表被点击的任务及对应的任务界面之外的区域响应点击事件时关闭 lium
        	//if (preNodeClassName.indexOf("x-tree-node-text") < 0
        	//		&& prePreNodeClassName.indexOf("x-tree-node-text") < 0){
        	//alert(parentNodeClassName);
        	if (parentNodeClassName.indexOf("x-tree-node-text")<0){
            	//点击任务详情页面外面,要关闭任务界面,但任务处于编辑状态,需要保存再关闭
        		if(document.getElementById("taskFrame").style.display == "block"){
            		var childFrame = document.getElementById("iframe_task").contentWindow;
					var saveTaskEle = childFrame.document.getElementById("saveTask");
            		if(saveTaskEle){
						if(saveTaskEle.style.display == "inline"){
							var ele;
							var taskEditFlag = "noReturn";
							childFrame.basic.biz.save(ele, taskEditFlag, true);
						} else{//点击工作计划页面任意位置会任务详情页面，如果是编辑状态，则先保存，再回调关闭，ie9下会死掉 chent 20160324
                    		closeTaskEdit();
                   		}
                    }
	        		//保存时就会执行isNeedToRefresh()方法,所以此时不用再次执行
	        		//isNeedToRefresh();
        		}

				//点击searchEval窗口之外,窗口关闭
        		var win = Ext.getCmp('displayScoreWin');
        		if(target.id && win){//如果鼠标点击在窗口中,返回点击的对象.即,该方法能确定点击范围在窗口内
        			var winEle = win.getChildByElement(target.id);
                }
        	 	if(win && !winEle){
        	 		win.close();
        	 	}
        		
        	}
	
	        var bvisible_box=false; 
	        var bvisible_monthbox=false; 
	        if (target.getAttribute("dropdownName") != null) {
	            var name = target.getAttribute("dropdownName");
	            if (name=="dropdownBox"){
	                bvisible_box=true;
	            }   
	            else if (name=="monthbox"){
	                bvisible_monthbox=true;
	            }   
	            else if (name=="both"){
	                bvisible_monthbox=true;
	                bvisible_box=true;
	            }   
	        }
	        if (target.id=="periodtypename"){
	           bvisible_box=true;
	        }
	        else if (target.id=="periodname"){
	           bvisible_monthbox=true;
	        }
	        
	        if (!bvisible_box){
	            var box=document.getElementById("dropdownBox");
	            if (box.style.display=="block"){        
	             box.style.display ="none";
	            }       
	        }
	        
	        if (!bvisible_monthbox){
	            var box=document.getElementById("monthlist");
	            if (box.style.display=="block"){        
	             box.style.display ="none";
	            }        
	        }
	    });
       window.setInterval(function(){//为了彻底解决兼容模式下标题栏会出现无的情况，加定时器
            var tag=document.getElementById("taskgrid");
            var oldW,newW;
            if (tag==null) return;
            if(tag.id=="taskgrid"){
                oldW=tag.getAttribute("oldW");                
                newW=parseInt(tag.offsetWidth);                 
                if(oldW!=newW){                   
                    try{
                          if (g_tree!=null){
			                  var grid_width=newW;
			                  if (grid_width>0 && wpm.grid_width!=grid_width){
			                     wpm.grid_width=grid_width;
			                     g_tree.setWidth(wpm.grid_width);  
			                  }
                         }  
                    }catch(e){}               
                }
                 
                tag.setAttribute("oldW",newW);
            }
            
             
        },50);
    initform("selfplan");
	//-----------------------------------------------
	
    //-------------------------------------------
      //任务负责人中默认本人
    var contentObj=document.getElementById("content-add-member");
	if(!defaultName){
        var item={id:'',name:"<%=userView.getUserFullName() %>"};
	}else{
		var item={id:'',name:defaultName};
	}
    var strhtml= getDisplayParticipantItem(item);
    contentObj.innerHTML=strhtml;
	
	});
	//点击工作计划页面任意位置会任务详情页面，如果是编辑状态，则先保存，再回调关闭，ie9下会死掉 chent 20160324
	function closeTaskEdit(){
		document.getElementById("taskFrame").style.display = "none";
		document.getElementById("leftArrow").style.display = "none";
	}
    //document.getElementById("test").focus();
    document.body.focus();//解决IE11报getAttr找不到的问题
   
  var strHaveShowDetailPri = "<%=strHaveShowDetailPri %>";//是否有权限点击任务名称查看任务详情 chent 20160413
  var strHaveCommuctionPri = "<%=strHaveCommuctionPri %>";//是否有计划沟通权限 chent 20160413
  var strHaveSummaryPri = "<%=strHaveSummaryPri %>";//是否有工作总结权限 chent 20160413
  wpm.planType = "<%=plantype%>";//（查看计划类型）定义为全局变量，workplan.js用  haosl 20161128
</script> 
<script type="text/javascript" src="/components/dateTimeSelector/dateTimeSelector.js"></script>
</html>


