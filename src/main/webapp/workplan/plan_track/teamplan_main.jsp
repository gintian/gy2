<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head> 
    <title></title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <script type="text/javascript" src="/workplan/js/global.js"></script>
    <link rel="stylesheet" type="text/css" href="/workplan/style/workplan.css">
    <script language="JavaScript" src="/js/wz_tooltip.js"></script>
    <script language="JavaScript" src="/workplan/js/workplan.js"></script> 
  </head>

<%
    String plantype = (String) request.getParameter("type");
    String periodtype = (String) request.getParameter("periodtype");
    String periodyear = (String) request.getParameter("periodyear");
    String periodmonth = (String) request.getParameter("periodmonth");
    String periodweek=(String)request.getParameter("periodweek");
    String objectid = (String) request.getParameter("objectid");
    String subobjectid = (String) request.getParameter("subobjectid");
	String subpersonflag=(String)request.getParameter("subpersonflag");
    String concerned_bteam = (String) request.getParameter("concerned_bteam");
    String concerned_cur_page = (String) request.getParameter("concerned_cur_page");

    String deptleader = (String) request.getParameter("deptleader");
    String superconcernedjson = (String) request.getParameter("superconcernedjson");

    plantype = (plantype != null) ? plantype : "";
    periodtype = (periodtype != null) ? periodtype : "";
    periodyear = (periodyear != null) ? periodyear : "";
    periodmonth = (periodmonth != null) ? periodmonth : "";
    periodweek=(periodweek!=null)?periodweek:"";
    objectid = (objectid != null) ? objectid : "";

    concerned_bteam = (concerned_bteam != null) ? concerned_bteam : "false";
    concerned_cur_page = (concerned_cur_page != null) ? concerned_cur_page : "1";
    subobjectid = (subobjectid != null) ? subobjectid : "";
    subpersonflag=(subpersonflag!=null)?subpersonflag:"";
    deptleader = (deptleader != null) ? deptleader : "";
    superconcernedjson = (superconcernedjson != null) ? superconcernedjson : "";

   // if ("person".equals(plantype)) {
     //   plantype = "2";
   // } else if ("org".equals(plantype)) {
       // plantype = "4";
   // }
%>
  
<body>
		<div class="hj-wzm-all">
			<input id="plantype" type="hidden" value="<%=plantype%>">
			<input id="periodtype" type="hidden" value="<%=periodtype%>">
			<input id="periodyear" type="hidden" value="<%=periodyear%>">
			<input id="periodmonth" type="hidden" value="<%=periodmonth%>">
			<input id="periodweek" type="hidden" value="<%=periodweek %>">   
			<input id="objectid" type="hidden" value="<%=objectid%>">

			<input id="superconcernedjson" type="hidden"
				value="<%=superconcernedjson%>">
			<input id="subobjectid" type="hidden" value="<%=subobjectid%>">
			<input id="subpersonflag" type="hidden" value="<%=subpersonflag %>">
			<input id="deptleader" type="hidden" value="<%=deptleader%>">
			<input id="concerned_bteam" type="hidden"
				value="<%=concerned_bteam%>">
			<input id="concerned_cur_page" type="hidden"
				value="<%=concerned_cur_page%>">

			<input id="ismyplan" type="hidden" value="">

			<input id="p0723" type="hidden" value="">

			<div class="hj-wzm-all-table">
				<div class="hj-wzm-all-right" id="rightDiv">
					<dl class="hj-right-dl" id="concerneddivx">
						<dt>
							<a href="javascript:void(0)"><img class="img-circle"
									id="my_image" style="display:none" onclick="loadMyPlan()" src=""/>
							</a>
						</dt>
<%--						<dd class="hj-zm-name">--%>
<%--							<span id="my_name" onclick="loadMyPlan()">小吴</span>--%>

						<dd>
                     
	                      <div id="concerneddiv" class="hj-wzm-right-wgzda">
	                    <div style="display:block; position:absolute; z-index:99; margin-left:2px;">
	                      <img id="teamlistimg" src="/workplan/image/list.png" 
	                            dropdownName="dropdownBox" onclick="backMyTeam()" title="切换到列表显示" style="cursor:pointer"/>
	                    </div> 
	                    &nbsp;&nbsp;&nbsp;&nbsp;
	                    
	                    <a id="concernedtitle" dropdownName="dropdownBox" onclick="dropdownAttentionMenu()" style="cursor:pointer; margin-left:10px;">我关注的</a>
	                    <img id="concernedimg" src="/workplan/image/baijiant.jpg" 
	                            dropdownName="dropdownBox" onclick="dropdownAttentionMenu()" style="cursor:pointer" />
	                	</div> 
                     
                     </dd>
					</dl>

<%--					<div id="concerneddiv" class="hj-wzm-right-wgzd">--%>
<%--				    <div style="display:block; position:absolute; z-index:9999; margin-left:3px;margin-top:-2px;">--%>
<%--                     &nbsp;--%>
<%--                    </div> --%>
<%--						<a id="concernedtitle" dropdownName="dropdownBox"--%>
<%--							onclick="dropdownAttentionMenu()" style="cursor: pointer">团队成员</a>--%>
<%--						<img src="/workplan/image/baijiant.jpg" dropdownName="dropdownBox"--%>
<%--							onclick="dropdownAttentionMenu()" style="cursor: pointer" />--%>
<%--					</div>--%>
					<div id="xshangjpg" class="hj-wzm-right-xshang" align="center" style="display:none;">
						<a href="javascript:void(0)"><img
								src="/workplan/image/xshang.jpg" onclick="upConcerneders()" />
						</a>
					</div>
					<div id="backSuperDiv" align="center">
						<a href="javascript:void(0)" onclick="backSuper()">返回上级 </a>
					</div>

					<div id="concernedersdiv" class="hj-wzm-right-dllb">

					</div>
					<div class="hj-wzm-right-xxia" align="center" id="xxiajpg">
						<a href="javascript:void(0)"><img
								src="/workplan/image/xxia.jpg" onclick="downConcerneders()" />
						</a>
					</div>
				</div>
				<div class="hj-wzm-all-left">
					<div class="hj-wzm-one">
						<div id="plantype_div" class="hj-wzm-one-left" style="width: 260">
							 <a id="periodtypename" dropdownName="dropdownBox"
			                         onclick="dropdownPeriodType()" style="width:80px;"><img  dropdownName="dropdownBox" src="/workplan/image/jiantou.png"/></a>
			                    <a id="periodname" dropdownName="dropdownBox"
			                         onclick="dropdownPeriodYear()" style="width:100px;"> <img   src="/workplan/image/jiantou.png"/></a>
						</div>
				
					
		                <div id="div_plandesign" class="hj-wzm-one-right" style="display:none">
		                    <a id="a_plandesign" class="hj-wzm-or-a" onclick="planDesign()">计划制订</a>
		                    <a id="a_plantrace" onclick="planTrace()">计划跟踪</a>
		                </div>
		                
		                <div class="hj-wzm-one-right2" id="div_halfyears" style="display:none;float:right;width:120px;">
		                    <a href="###" id="a_halfyears" onclick="selectPeriodWeek('1')" >上半年</a>
		                    <a href="###" id="a_halfyears" class="hj-wzm-or-a" onclick="selectPeriodWeek('2')" >下半年</a>
		                </div>
		                
		                <div class="hj-wzm-one-right2" id="div_quaters" style="display:none;float:right;width:240px;">
		                    <a href="###" onclick="selectPeriodWeek('1')" >第一季度</a>
		                    <a href="###" onclick="selectPeriodWeek('2')" >第二季度</a>
		                    <a href="###" onclick="selectPeriodWeek('3')" >第三季度</a>
		                    <a href="###" onclick="selectPeriodWeek('4')" >第四季度</a>
		                </div>
		                <div class="hj-wzm-one-right2" id="div_weeks" style="display:none;float:right;width:240px;">
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
		                        <span id='myeartitle'>2014</span>年  
		                         <a  dropdownName="monthbox" href="javascript:yearchange(1);">
		                         <img dropdownName="monthbox" src="/workplan/image/right2.gif" /></a>
		                        </span>
		                    </ul>
		                    <ul id="months">
		                        <li ><a href="###" onclick="selectPeriodMonth(1)" >&nbsp;1月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(2)" >&nbsp;2月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(3)" >&nbsp;3月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(4)" >&nbsp;4月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(5)" >&nbsp;5月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(6)" >&nbsp;6月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(7)" >&nbsp;7月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(8)" >&nbsp;8月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(9)" >&nbsp;9月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(10)" >10月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(11)" >11月</a></li>
		                        <li ><a href="###" onclick="selectPeriodMonth(12)" >12月</a></li>
		                    </ul>
		                </div>
                </div>
	            <div id="dropdownBox" style='display:none;' tabindex="-1" class="hj-wzm-one-dinwei" onblur="hideDropdownBox()">
	           </div>				
					<div class="hj-wzm-zb-two">

						<div class="hj-wzm-zb-two-right">
							<div id="plantitle" class="hj-wzm-tdjh">
								
							</div>
						</div>
					</div>
					
					<div id="teaminfodiv" class="hj-wzm-tdzb-three">
					</div>
				</div>


			</div>
		</div>
	</body>


 <script type="text/javascript">
    Ext.onReady(function() {
        Ext.get(document).on("click", function(e) {
            e = e || window.event;
            var target = e.target || e.srcElement;
    
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
    });
    wpm.planType = "<%=plantype%>";//（查看计划类型）定义为全局变量，workplan.js用  haosl 20161128
</script> 
  <script language="javascript">  
	initform("teamplan");
	// document.getElementById("test").focus();
	 document.body.focus();//解决IE11报getAttr找不到的问题
   </script>
</html>
