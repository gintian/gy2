<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   String strHaveCreatePlanPri_p="false"; //是否有制定(人员)工作计划权限 dengc 
   String strHaveCreatePlanPri_u="false"; //是否有制定（部门）工作计划权限 dengc
    if(userView.hasTheFunction("0KR02020102")) {
		strHaveCreatePlanPri_p="true"; 
    }
     if(userView.hasTheFunction("0KR02010201")) {
		strHaveCreatePlanPri_u="true"; 
   }

%>
<html>
  <head> 
    <title>
    </title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<%--<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />
    --%><link rel="stylesheet" type="text/css" href="/workplan/style/treegrid.css">
    <link rel="stylesheet" type="text/css" href="/workplan/style/workplan.css">
    <script type="text/javascript" src="/workplan/js/global.js"></script>
    <script language="JavaScript" src="/js/wz_tooltip.js"></script>
    <script language="JavaScript" src="/workplan/js/workplan.js"></script> 
    <script language="JavaScript" src="/workplan/js/workplan_hr.js"></script> 
    <script language="JavaScript" src="../../module/utils/js/template.js"></script>
  </head>

<%
    String plantype = (String) request.getParameter("type");
    String periodtype = (String) request.getParameter("periodtype");
    String periodyear = (String) request.getParameter("periodyear");
    String periodmonth = (String) request.getParameter("periodmonth");
    String periodweek = (String) request.getParameter("periodweek");
    String viewtype = (String) request.getParameter("viewtype");
    String submittype = (String) request.getParameter("submittype");
    String curpage = (String) request.getParameter("curpage");
    String pagesize = (String) request.getParameter("pagesize");
    String querytype = (String) request.getParameter("querytype");
    String querytext = (String) request.getParameter("querytext");

    plantype = (plantype != null) ? plantype : "";
    periodtype = (periodtype != null) ? periodtype : "";
    periodyear = (periodyear != null) ? periodyear : "";
    periodmonth = (periodmonth != null) ? periodmonth : "";
    periodweek = (periodweek != null) ? periodweek : "";
    viewtype = (viewtype != null) ? viewtype : "1";
    submittype = (submittype != null) ? submittype : "";
    curpage = (curpage != null) ? curpage : "1";
    pagesize = (pagesize != null) ? pagesize : "10";
    querytype = (querytype != null) ? querytype : "";
    querytext = (querytext != null) ? querytext : "";

    if ("org".equals(plantype)||"2".equals(plantype)) {
        plantype = "2";
    } else {
        plantype = "1";
    }
%>

	<body>
		<div class="hj-wzm-all">
			<input id="plantype" type="hidden" value="<%=plantype%>">
			<input id="periodtype" type="hidden" value="<%=periodtype%>">
			<input id="periodyear" type="hidden" value="<%=periodyear%>">
			<input id="periodmonth" type="hidden" value="<%=periodmonth%>">
			<input id="periodweek" type="hidden" value="<%=periodweek%>">
			<input id="viewtype" type="hidden" value="<%=viewtype%>">
			<input id="submittype" type="hidden" value="<%=submittype%>">
			<input id="curpage" type="hidden" value="<%=curpage%>">
			<input id="pagesize" type="hidden" value="<%=pagesize%>">
			<input id="querytype" type="hidden" value="<%=querytype%>">
			<input id="querytext" type="hidden" value="<%=querytext%>">

			<div class="hj-wzm-hr-table">
				<div class="hj-wzm-hr-left">
					<div class="hj-hr-one">
						<div id="plantype_div" class="hj-hr-one-left" style="width: 260">
							<a id="periodtypename" dropdownName="dropdownBox"style="width: 80px;"
								onclick="dropdownPeriodType('1')"><img
									dropdownName="dropdownBox" src="/workplan/image/jiantou.png" />
							</a>
							<a id="periodname" dropdownName="dropdownBox"
								onclick="dropdownPeriodYear('1')" style="width: 100px;">
								<img  src="/workplan/image/jiantou.png" /> </a>
						</div>


						<div id="div_plandesign" class="hj-wzm-xxk">
							<div class="hj-wzm-shur">
								<input type="text" id="quickQueryText" class="hj-zm-xxk-style"
									value="" onkeydown="quickQueryTextOnEnter()"
									onFocus="quickQueryTextFocus(this)"
									onBlur="quickQueryTextBlur(this)" />
						<%if("2".equals(plantype)){%>
								<a href=javascript:void(0); style="float: right; margin-top: 4px;"> <img
										id="down_img" style="display: none" src="image/down.png"
										onclick="displayCommonQuery()" /> <img id="up_img"
										src="image/up.png" onclick="hideCommonQuery()" /> </a>
						<%} %>
							</div>
							<div class="hj-wzm-xxk-a">
								<a href=javascript:void(0);><img src="image/chaxun.png"
										onclick="quickQuery()" />
								</a>
							</div>
						</div>

						<div class="hj-hr-one-right2" id="div_halfyears"
							style="display: none; float: right; width: 120px;">
							<a id="halfyear1" href="###" onclick="selectPeriodWeek('1')">上半年</a>
							<a id="halfyear2" href="###" class="hj-wzm-or-a" onclick="selectPeriodWeek('2')">下半年</a>
						</div>

						<div class="hj-hr-one-right2" id="div_quaters"
							style="display: none; float: right; width: 240px;">
							<a id='quarter1' href="###" onclick="selectPeriodWeek('1')">第一季度</a>
							<a id='quarter2' href="###" onclick="selectPeriodWeek('2')">第二季度</a>
							<a id='quarter3' href="###" onclick="selectPeriodWeek('3')">第三季度</a>
							<a id='quarter4' href="###" onclick="selectPeriodWeek('4')">第四季度</a>
						</div>
						<div class="hj-hr-one-right2" id="div_weeks"
							style="display: none; float: right; width: 240px;">
							<a href="###" onclick="selectPeriodWeek('1')">第一周</a>
							<a href="###" onclick="selectPeriodWeek('2')">第二周</a>
							<a href="###" onclick="selectPeriodWeek('3')">第三周</a>
							<a href="###" onclick="selectPeriodWeek('4')">第四周</a>
							<a href="###" style="display: none" id='fiveweek'
								onclick="selectPeriodWeek('5')">第五周</a>
						</div>
						<div class="hj-wzm-clock" id="monthlist"
							style="display: none;">
							<ul style="text-align: center">
								<span style="color: #549FE3;"> <a dropdownName="monthbox"
									href="javascript:yearchange(-1);"><img
											dropdownName="monthbox" src="/workplan/image/left2.gif" /> </a>
									<span id='myeartitle'>2014</span>年 <a dropdownName="monthbox"
									href="javascript:yearchange(1);"><img
											dropdownName="monthbox" src="/workplan/image/right2.gif" />
								</a> </span>
							</ul>
							<ul id="months">
								<li id='li1' ><a href="###" onclick="selectPeriodMonth(1)">&nbsp;1月</a></li>
								<li id='li2'><a href="###" onclick="selectPeriodMonth(2)">&nbsp;2月</a></li>
								<li id='li3'><a href="###" onclick="selectPeriodMonth(3)">&nbsp;3月</a></li>
								<li id='li4'><a href="###" onclick="selectPeriodMonth(4)">&nbsp;4月</a></li>
								<li id='li5'><a href="###" onclick="selectPeriodMonth(5)">&nbsp;5月</a></li>
								<li id='li6'><a href="###" onclick="selectPeriodMonth(6)">&nbsp;6月</a></li>
								<li id='li7'><a href="###" onclick="selectPeriodMonth(7)">&nbsp;7月</a></li>
								<li id='li8'><a href="###" onclick="selectPeriodMonth(8)">&nbsp;8月</a></li>
								<li id='li9'><a href="###" onclick="selectPeriodMonth(9)">&nbsp;9月</a></li>
								<li id='li10'><a href="###" onclick="selectPeriodMonth(10)">10月</a></li>
								<li id='li11'><a href="###" onclick="selectPeriodMonth(11)">11月</a></li>
								<li id='li12'><a href="###" onclick="selectPeriodMonth(12)">12月</a></li>
							</ul>
						</div>
					</div>
					<div id="dropdownBox" tabindex="-1" class="hj-wzm-one-dinwei"
						style="display:none;" onblur="hideDropdownBox()">
						<ul>
							<li>
								<a>2014年4月</a>
							</li>
							<li>
								<a>2014年5月</a>
							</li>
						</ul>
					</div>
					<div id="dividerLineDiv" style="border-bottom: 1px #D5D5D5 solid;"></div>
					<div class="bh-clear"></div>
					<%if("2".equals(plantype)){%>
					<div id="commonQueryDiv" class="hj-wzm-cxgn" style="display:none">
						<div class="hj-zm-cxgn" id="query_dept_div">
							<table width="100%" border="0">
								<tr>
									<input type="hidden" id="query_dept.value"
										name="query_dept.value" value="" class="text">

									<td width="100" align="right">
										部门
									</td>
									<td width="200" align="left">
										<input type="text" class="hj-zm-bumen hj-zm-bumen-line" readonly="readonly"
											name="query_dept.viewvalue" onchange="fieldcode(this,2);" />
										<img src="/images/code.gif"
											onclick="openInputCodeDialogOrgInputPosForBatchUpdate('UM','query_dept.viewvalue','UN`',1,2);"
											align="absmiddle" />
									</td>
								</tr>
							</table>
						</div>

						<div class="hj-zm-cxgn" id="query_a0101_div">
							<table width="100%" border="0">
								<tr>
									<td width="100" align="right">
										姓名
									</td>
									<td width="200" align="left">
										<input name="a0101" type="text" class="hj-zm-bumen hj-zm-bumen-line" />
									</td>
								</tr>
							</table>
						</div>

						<div class="hj-zm-cxgn" id="query_pinyin_div">
							<table width="100%" border="0">
								<tr>
									<td width="100" align="right">
										拼音简称
									</td>
									<td width="200" align="left">
										<input name="pinyin" type="text" class="hj-zm-bumen hj-zm-bumen-line" />
									</td>
								</tr>
							</table>
						</div>

						<div class="hj-zm-cxgn" id="query_status_div">
							<table width="100%" border="0">
								<tr>
									<td width="100" align="right">
										计划状态
									</td>
									<td width="200" align="left">
										<!-- <select name="plan_status" size="1" class="hj-zm-bumen">
											<option value="">
												所有
											</option>
											<option value="0">
												未提交
											</option>
											<option value="1">
												已提交
											</option>
											<option value="2">
												已批准
											</option>
										</select>   -->
										<input type="hidden" name="plan_status" id="plan_status" value="" class="hj-zm-bumen"/>
										<input type="text" id="plantype1" class="hj-zm-bumen-line" onclick="display('statuslist','plantype1')" style="padding-left: 20px;width: 180px;height:18px;margin-left:10px;" readonly="readonly" value="全部计划"/>
										<img id="imga" src="/workplan/image/jiantou.png" onclick="display('statuslist','plantype1')" style="cursor: pointer;"/>
									</td>
								</tr>
							</table>
						</div>


						<div class="hj-zm-cxgn">
							<table width="100%" border="0">
								<tr>
									<td width="258" align="right">
										<input type="button" value="查询" class="hj-zm-cx"
											onclick="commonQuery()" style="" />
										<input type="button" value="重置" class="hj-zm-cx"
											onclick="resetQuery()" style="" />
									</td>
									<td></td>
								</tr>
							</table>
						</div>
						<div class="bh-clear"></div>
						 <div id="statuslist" class="hj-wzm-one-dinwei dropdownlist" style="display: none;z-index: 30;position: absolute;width:190px;">
                 	<ul>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('','全部计划')" >全部计划</a></li>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('2','已批准')" >已批准</a></li>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('1','已提交')" >已提交</a></li>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('0','未提交')" >未提交</a></li>
                    </ul>
                 </div>
					</div>
					<%}else{ %>
					<div id="commonQueryDivId" ></div>
					<%}%>
					<div id="teaminfotitle" class="hj-hr-tdzb-teaminfotitle">
						<h2>
							团队5月计划提交情况
						</h2>
						<p>
							总人数：3人 已提交：2人
						</p>
					</div>
					<div id="teaminfobtn" class=".hj-hr-teaminfobtn">
						<div class="hj-hr-teaminfobtn-left" id="remind">
							<a style='color:#1B4A98 ;' onclick="Ext.Msg.confirm('提示信息','确定提醒大家写计划吗？',function(btn){if(btn == 'yes'){fontgrey(this);remindHrTeam('','0');}},this);">提醒大家写计划</a>
							<a style='color:#1B4A98 ;' onclick="Ext.Msg.confirm('提示信息','确定提醒大家批准计划吗？',function(btn){if(btn == 'yes'){fontgrey(this);remindHrTeam('','1');}},this);">提醒批准计划</a>
							<a style='color:#1B4A98 ;' onclick="relatePlanAll()">关联到考核计划</a>
							<a style='color:#1B4A98 ;' onclick="batchUpdateTargetCard()">更新到目标卡</a>
						</div>
						<div id="view" class="hj-hr-teaminfobtn-right">
							<a id="a_planView" class="hj-wzm-or-a" onclick="displayView('1')">显示计划</a>
							<a id="a_taskView" onclick="displayView('2')">显示任务</a>
						</div>

					</div>
					<div id="plan_grid" class="hj-hr-three">

					</div>
					<div id="taskgrid" class="hj-hr-four">

					</div>
					<div class="hj-wzm-gzzj-fanye">
						<div class="hj-wzm-gzzj-fanye-left">
							<table border="0"  cellpadding="10">
								<tr style="align:center">
									<td  nowrap>
										第<span id='span_curpage'>1</span>页
									</td>		
									<td width="10" nowrap>
                                      &nbsp;
                                    </td>							
									<td  nowrap>
										共<span id='span_sumcount'>1</span>条
									</td>
									<td width="10" nowrap>
                                      &nbsp;
                                    </td>
									<td  nowrap>
										共<span id='span_sumpage'>1</span>页
									</td>
									
								</tr>
							</table>
						</div>
						<div class="hj-wzm-gzzj-fanye-right">
							<table width="240" border="0" align="right">
								<tr>
									<td width="24" name="pageup" >首页</td>
                                    <td width="24" name="pageup" >上页</td>
                                    <td width="24" name="pagedown"><a  href="javascript:changepagenum(1)">下页</a></td>
                                    <td width="24" name="pagedown"><a  href="javascript:changepagenum(2)">末页</a></td>
									<td width="62">
										第<input id="input_curpage" type="text"
											maxLength="8" class="hj-wzm-gzzj-table" />页
									</td>
									<td width="36" style="text-align:right;">
										<input type="button" value="GO" class="hj-wzm-gzzj-go"
											 onclick="goPage()" />
									</td>
								</tr>
							</table>
						</div>
					</div>
					
				</div>
			</div>
		</div>
	</body>
	<!-- 任务界面展示在弹出层 -->
    <img src="/workplan/image/left-arrow.png" id="leftArrow" />
    <div id="taskFrame"><iframe name="iframe_task" id="iframe_task" scrolling="no" frameborder="0"></iframe></div>
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
 
	<script language="javascript"> 
	  var strHaveCreatePlanPri_p=<%=strHaveCreatePlanPri_p %>;
  	  var strHaveCreatePlanPri_u=<%=strHaveCreatePlanPri_u %>;
  Ext.Loader.setConfig({
    enabled: true
    });
  Ext.Loader.setPath('Ext.ux', '../ux');
    
    Ext.require([
        'Ext.data.*',
        'Ext.grid.*',
        'Ext.tree.*',
        'Ext.tip.*',
        'Ext.ux.CheckColumn'
    ]);
    
    Ext.onReady(function() {
        Ext.tip.QuickTipManager.init();
        init();
     
    });
    window.onresize=function(){  
    return;
      if (g_tree!=null){
          wpm.grid_width=getTreeGridWidth(false);
          g_tree.setWidth(wpm.grid_width);  
      }        
    }

    Ext.onReady(function() {
        Ext.get(document).on("click", function(e) {
            e = e || window.event;
            // 显示计划状态 
            var target = e.target || e.srcElement;
            var parentNode = target.parentNode||"";
	        var parentNodeClassName = parentNode.className||"";
            // 任务界面之外的区域响应点击事件时关闭
	     	if (parentNodeClassName.indexOf("x-tree-node-text") < 0){
	       		if(document.getElementById("taskFrame").style.display == "block"){
	        		document.getElementById("taskFrame").style.display = "none";
	        		document.getElementById("leftArrow").style.display = "none";
	        		//location.reload(true);
	        		//displayView('2');
	       		}
	        }
    	    if (target.getAttribute("id") == "plantype1")
    	        return false;
    	    if (target.getAttribute("id") == "imga")
    	        return false;
    	    var dropDownList = Ext.query("*[class$=dropdownlist]");
    	    for (i=0;i<dropDownList.length;i++){
    	        var aDropDown = Ext.get(dropDownList[i]);
    	        
    	        if (target != dropDownList[i])
    	            aDropDown.setDisplayed(false);
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
        if("1"==<%=plantype%>){
        	
      	//公共查询控件
      	Ext.onReady(function(){
      		if(wpm.defaultQueryFields=="")
      			return;
	        Ext.require("EHR.commonQuery.CommonQuery",function(){
	            var commonQuery = Ext.create("EHR.commonQuery.CommonQuery",{
					id:"commonQueryId",	            	
	                subModuleId:'workplan_hr_SubModuleId',
	                renderTo:'commonQueryDivId',
	                width:Ext.get('commonQueryDivId').dom.scrollWidth,
	                fieldPubSetable:wpm.saveQuery,
	                defaultQueryFields:wpm.defaultQueryFields,
	                optionalQueryFields:'A', 
	                ctrltype:'3',
	                nmodule:'5',//OKR 走绩效模板号
	                doQuery:function(items){
	                	 var map = new HashMap();
	                  	map.put("items", items);
	                  	map.put("planType", wpm.plan_type);
	                  	map.put("viewType", wpm.view_type);
	                  	map.put("periodType", wpm.period_type);
	                  	map.put("periodYear", wpm.period_year);
	                  	map.put("periodMonth", wpm.period_month);
	                  	map.put("periodWeek", wpm.period_week);
	                  	map.put("subModuleId", "workplan_hr_SubModuleId");
	                  	Rpc( {
	                  		functionId : '9028000704',
	                  		success :function(outparamters){
	                  			var result = Ext.decode(outparamters.responseText);
	                  			var viewType = result.viewType;
	                  			if(viewType=="1"){
		                  			desplayPlanDetailNext(result.info, true);
	                  			}else{
	                  				displayTaskListNext(result.info);
	                  			}
	                  		},
	                  	}, map);
	                  }             
	            
	            });
	        });
	        
	    });
      }

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
            
             
        },200);  
	initHRform();
    });
   
	if(wpm.view_type=="2"){//默认显示任务视图时，表格错位的问题	
	   setTimeout('getDetailList()', 300) ;
	}
	 document.body.focus();
  </script>
 
</html>
