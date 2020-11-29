<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hjsj.hrms.utils.PubFunc,
				 java.math.BigDecimal,
				 com.hrms.hjsj.sys.DataDictionary,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge; IE=9; IE=8; IE=7">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>


<style>
div#tbl-container {	
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
#tbl td{
    white-space: normal;
}
</style>
<hrms:themes />
<style>
.TEXT_NB {
	BACKGROUND-COLOR:transparent;
	BORDER-LEFT: medium none !important; 
	BORDER-RIGHT: medium none !important; 
	BORDER-TOP: medium none !important;
	
}
.button{
    color:#414141 !important;
}
.t_cell_locked5,.header_locked,.cell_locked5,.t_cell_locked5_b,.t_cell_locked_b{
    position: static !important;
}
<% 
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String themes = "";
	if(userView!=null)
		themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	int versionFlag = 1;
	if (userView != null)
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版
	EvaluationForm myForm=(EvaluationForm)session.getAttribute("evaluationForm");	
	String busitype = myForm.getBusitype(); // 业务分类字段 =0(绩效考核); =1(能力素质)
	String byModel = myForm.getByModel();//1:按岗位素质模型测评
	String object_type=(String)myForm.getObject_type();	//显示考核对象详情  1团队 2人员
	String hand_Score=(String)myForm.getHandScore();
	Hashtable planParamset = myForm.getPlanParamSet();
	String GrpMenu1 = (String)planParamset.get("GrpMenu1");  //排名指标1，格式：字段名;层级
	String GrpMenu2 = (String)planParamset.get("GrpMenu2");  //排名指标2，格式：字段名;层级
	String allowAdjustEvalResult = (String)planParamset.get("AllowAdjustEvalResult");
	String showEvalDirector = (String) planParamset.get("ShowEvalDirector");
	String EvalOutLimitStdScore = (String) planParamset.get("EvalOutLimitStdScore");//评分时得分不受标准分限制True, False
	String isDispAll = myForm.getIsDispAll();
	String startEditScore = myForm.getStartEditScore();
	boolean queryFlag=true;
	if(isDispAll.equals("true"))
		queryFlag=false;
	int dataSize = myForm.getSetlist().size();	
	int currentPage = myForm.getSetlistform().getPagination().getCurrent();
	int pagecount = myForm.getSetlistform().getPagination().getPageCount();
	int pages = myForm.getSetlistform().getPagination().getPages();
	int lastIndex = pagecount;//当前页的最后一条	

	if(pages>1&&currentPage<pages)
		lastIndex = pagecount;
	else if(pages>1&&currentPage==pages)		
		lastIndex=dataSize-pagecount*(currentPage-1);
	else if(pages==1)
		lastIndex = dataSize;
	String showmethod=myForm.getShowmethod();
	String showbenbu=myForm.getShowbenbu();
	String showaband=myForm.getShowaband();
	String position=myForm.getShowobjectpos();
	String name=myForm.getObject_name();
	String upa0100=myForm.getUpa0100();
	String nexta0100=myForm.getNexta0100();
	String deviationScore=myForm.getDeviationScore();
	String pointResultValue=myForm.getPointResultValue();
	String showDetails=myForm.getShowDetails();///显示对象详情
	String Plan_type=myForm.getPlan_type();//计划类型 0:不记名 1:记名 chent 20160115
	VersionControl ver = new VersionControl();  //查看得分明细受权限控制   2013.12.06 pjf
    String bosFlag = userView.getBosflag();
    String top = "";
    top += bosFlag.equalsIgnoreCase("hcm") ? 52 : 62;
%>


</style>

<script type="text/javascript">
var IVersion=getBrowseVersion();
if(IVersion==8)
{
  	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
  	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}

</script>

<!--
<link href="../../css/locked-column-new.css" rel="stylesheet" type="text/css">  
-->

<script language="JavaScript" src="evaluation.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script language="JavaScript"src="../../js/showModalDialog.js"></script> 
<script language="JavaScript"src="../../module/utils/js/template.js"></script> 

<script language="javascript">

	var aclientHeight=document.body.clientHeight||document.documentElement.clientHeight;
    var down_percent=420/490;
    var down_height=down_percent*aclientHeight;
	if('${evaluationForm.isAlert}'=='1')
		alert(KH_EVALUATION_INFO3);
	
	var themes = "<%=themes %>";
	var bgColor="";
	var borderColors="";
	if(themes=="default"){
	    Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{color:#414141 !important;}");
	}
    bgColor="#F9F9F9";
    borderColors = "#C5C5C5";
	Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mc{border:1px solid "+borderColors+";background-color:"+bgColor+" !important;}","menu_ms_bg");
	Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-ml{display:none !important;background:none !important;}","");
	Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mr{display:none !important;background:none !important;}","");
	Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small{border-color:"+borderColors+" !important;background-color:"+bgColor+" !important;}","menu1");
	Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small{background-color:"+bgColor+" !important;}","");
	Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{padding:2px 4px !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-br{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-bl{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-tr{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-tl{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tr{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-frame-bl{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tl{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-frame-br{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tc{background-image:none !important;}","");
	Ext.util.CSS.createStyleSheet("#toolbars .x-frame-bc{background-image:none !important;}","");

    //取消锁列样式 此样式在ie8以上无法使用
    Ext.util.CSS.createStyleSheet(".t_cell_locked{left: 0 !important;top: 0 !important;position: static !important;}", "");
    Ext.util.CSS.createStyleSheet(".t_cell_locked2{left: 0 !important;top: 0 !important;position: static !important;}","");
	Ext.onReady(function(){
	    var menu1Arr = new Array();
        <logic:equal name="evaluationForm" property="computeFashion" value="1"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		        	<%	// 区分 绩效管理和能力素质 模块的功能授权
					if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
				%>
					<hrms:priv func_id="3260412">
                         menu1Arr.push(new Ext.menu.Item({
			                text: "评估表结构设置",
			                handler: function(){
			                	setTableStructure();
			                }
			            }))
					</hrms:priv>

					<hrms:priv func_id="3260414">
                        menu1Arr.push(new Ext.menu.Item({
			                text: "导出Excel",
			                icon:"/images/export.gif",
			                handler: function(){
			                	exportExcel('${evaluationForm.code}','${evaluationForm.computeFashion}','${evaluationForm.bodyid}','${evaluationForm.pointResult}');
			                }
			            }))
					</hrms:priv>

					<logic:equal name="evaluationForm" property="method" value="2"><!-- 目标卡导出excel -->
						<hrms:priv func_id="3260416">
                             menu1Arr.push(new Ext.menu.Item({
				                text: "导出打分明细",
				                icon:"/images/export.gif",
				                handler: function(){
				                	exportScoreDetails();
				                }
				            }))
				        </hrms:priv>
					</logic:equal>
					<logic:notEqual name="evaluationForm" property="planStatus" value="4">
						<hrms:priv func_id="3260408">
                            menu1Arr.push(new Ext.menu.Item({
				                text: "打印登记表",
				                icon:"/images/print.gif",
				                handler: function(){
				                	printcard();
				                }
				            }))
						</hrms:priv>
					</logic:notEqual>

					<hrms:priv func_id="3260410">
                        menu1Arr.push(new Ext.menu.Item({
			                text: "发送反馈表",
			                handler: function(){
			                	sendBackTables();
			                }
			            }))
					</hrms:priv>

				    <logic:notEqual name="evaluationForm" property="jxReportInfo" value="">
				    	<hrms:priv func_id="3260415">
                             menu1Arr.push(new Ext.menu.Item({
				                text: "批量导出绩效报告",
				                icon:"/images/export.gif",
				                handler: function(){
				                	batchExportReport();
				                }
				            }))
				   		</hrms:priv>
				    </logic:notEqual>
			    <%}else{%>
			    	<hrms:priv func_id="36030311">
                         menu1Arr.push(new Ext.menu.Item({
			                text: "评估表结构设置",
			                handler: function(){
			                	setTableStructure();
			                }
			            }))
					</hrms:priv>

					<hrms:priv func_id="36030301">
                        menu1Arr.push(new Ext.menu.Item({
			                text: "导出Excel",
			                icon:"/images/export.gif",
			                handler: function(){
			                	exportExcel('${evaluationForm.code}','${evaluationForm.computeFashion}','${evaluationForm.bodyid}','${evaluationForm.pointResult}');
			                }
			            }))
					</hrms:priv>

				    <%if(false){ //暂时隐藏%>
					    <logic:notEqual name="evaluationForm" property="planStatus" value="4">
					    	<hrms:priv func_id="36030302">
                                menu1Arr.push(new Ext.menu.Item({
					                text: "打印登记表",
					                icon:"/images/print.gif",
					                handler: function(){
					                	printcard();
					                }
					            }))
				            </hrms:priv>
					    </logic:notEqual>

					    <hrms:priv func_id="36030303">
                             menu1Arr.push(new Ext.menu.Item({
				                text: "发送反馈表",
				                handler: function(){
				                	sendBackTables();
				                }
				            }))
			            </hrms:priv>
				    <%} %>
				    <logic:notEqual name="evaluationForm" property="jxReportInfo" value="">
				    	<hrms:priv func_id="36030304">
                            menu1Arr.push(new Ext.menu.Item({
				                text: "批量导出绩效报告",
				                icon:"/images/export.gif",
				                handler: function(){
				                	batchExportReport();
				                }
				            }))
				   		</hrms:priv>
				    </logic:notEqual>
			    <%}%>

			    <logic:notEqual name="evaluationForm" property="planStatus" value="4">
			    	<%if(false){ //未开发完 暂时隐藏%>
			    		<hrms:priv func_id="3260409">
                            menu1Arr.push(new Ext.menu.Item({
				                text: "打印高级花名册",
				                icon:"/images/print.gif",
				                handler: function(){
				                	printHighroster();
				                }
				            }))
			            </hrms:priv>
			    	 <%} %>
			    </logic:notEqual>
			    <%
				if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("bjga"))
				{%>
					<hrms:priv func_id="3260413">
                        menu1Arr.push(new Ext.menu.Item({
			                text: "输出开放式意见明细表",
			                icon:"/images/print.gif",
			                handler: function(){
			                	exportOpenOpinionExcel('${evaluationForm.code}','${evaluationForm.computeFashion}','${evaluationForm.bodyid}','${evaluationForm.pointResult}');
			                }
			            }))
					</hrms:priv>
				<%} %>
			</logic:equal>

			<logic:notEqual  name="evaluationForm" property="computeFashion" value="1">
				<hrms:priv func_id="3260414">
                    menu1Arr.push(new Ext.menu.Item({
			            text: "导出Excel",
			            icon:"/images/export.gif",
			            handler: function(){
			            	exportExcel('${evaluationForm.code}','${evaluationForm.computeFashion}','${evaluationForm.bodyid}','${evaluationForm.pointResult}');
			            }
			        }))
		        </hrms:priv>
			    <logic:notEqual name="evaluationForm" property="planStatus" value="4">
			    	<logic:notEqual name="evaluationForm" property="computeFashion" value="6">
			    		<hrms:priv func_id="3260408">
                            menu1Arr.push(new Ext.menu.Item({
					            text: "打印登记表",
				                icon:"/images/print.gif",
				                handler: function(){
				                	printcard();
				                }
					        }))
				        </hrms:priv>
			    	</logic:notEqual>
			    	<%if(false){ //未开发完 暂时隐藏%>
			    		<hrms:priv func_id="3260409">
                            menu1Arr.push(new Ext.menu.Item({
				                text: "打印高级花名册",
				                icon:"/images/print.gif",
				                handler: function(){
				                	printHighroster();
				                }
				            }))
			            </hrms:priv>
			    	<%} %>
			    </logic:notEqual>
			    <hrms:priv func_id="3260410">
                     menu1Arr.push(new Ext.menu.Item({
			               text: "发送反馈表",
			               handler: function(){
			               	sendBackTables();
			               }
			           }))
				</hrms:priv>
			     <logic:notEqual name="evaluationForm" property="jxReportInfo" value="">
			     	<hrms:priv func_id="3260415">
                        menu1Arr.push(new Ext.menu.Item({
			                text: "批量导出绩效报告",
			                icon:"/images/export.gif",
			                handler: function(){
			                	batchExportReport();
			                }
			            }))
			   		</hrms:priv>
			    </logic:notEqual>
			  </logic:notEqual>
        var menu1 = new Ext.menu.Menu({
            allowOtherMenus: false,
            items:menu1Arr
        });
	    var menu2_1 = new Ext.menu.Menu({
	        allowOtherMenus: false,
	        items: [
	        	<logic:equal name="evaluationForm" property="planStatus" value="4">
		        	<logic:equal name="evaluationForm" property="busitype" value="0">
				    	<hrms:priv func_id="3260403">
					    	new Ext.menu.Item({
				                text: "录入",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	correctScore();
				                }
				            }),
					    	new Ext.menu.Item({
				                text: "批量导入",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	batchImport('${evaluationForm.code}');
				                }
				            })
			            </hrms:priv>
				    </logic:equal>
				</logic:equal>

				<logic:equal name="evaluationForm" property="planStatus" value="6">
					<logic:equal name="evaluationForm" property="busitype" value="0">
				    	<hrms:priv func_id="3260403">
					    	new Ext.menu.Item({
				                text: "录入",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	correctScore();
				                }
				            }),
					    	new Ext.menu.Item({
				                text: "批量导入",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	batchImport('${evaluationForm.code}');
				                }
				            })
			            </hrms:priv>
				    </logic:equal>
				</logic:equal>
	        ]
	    });
	    var menu2Arr = new Array();
        <logic:equal name="evaluationForm" property="computeFashion" value="1"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		        	<logic:equal name="evaluationForm" property="planStatus" value="4">
			 	    <logic:equal name="evaluationForm" property="dispUnitScore" value="1">
                        menu2Arr.push(new Ext.menu.Item({
			                text: "统一打分",
			                icon:"/images/edit.gif",
			                handler: function(){
			                	rate();
			                }
			            }))
			 	  	</logic:equal>

			 	  	<logic:equal name="evaluationForm" property="busitype" value="0">
			            <hrms:priv func_id="3260404">
                            menu2Arr.push(new Ext.menu.Item({
				                text: "引入",
				                handler: function(){
				                	importexpre('${evaluationForm.code}');
				                }
				            }))
			            </hrms:priv>
				    </logic:equal>

				    <%	// 区分 绩效管理和能力素质 模块的功能授权
						if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
					%>
						<%if(userView.hasTheFunction("3260401") && userView.hasTheFunction("3260402")) {%>
							<hrms:priv func_id="3260401">
                            menu2Arr.push(new Ext.menu.Item({
					                text: "计算",
					                icon:"/images/compute.gif",
					                handler: function(){
					                	computeValidate();
					                }
					            }))
				            </hrms:priv>
				    	<%} %>
				    <%}else{%>
				    	<%if(userView.hasTheFunction("36030305") && userView.hasTheFunction("36030306")) {%>
				    		<hrms:priv func_id="36030305">
                                menu2Arr.push(new Ext.menu.Item({
					                text: "计算",
					                icon:"/images/compute.gif",
					                handler: function(){
					                	computeValidate();
					                }
					            }))
				            </hrms:priv>
			    		<%} %>
			    	<%}%>
			   </logic:equal>

			   <logic:equal name="evaluationForm" property="planStatus" value="7">
                menu2Arr.push(new Ext.menu.Item({
		                text: "结果分析",
		                icon:"/images/icon_ypjlfx.gif",
		                handler: function(){
		                	resultAnalyse();
		                }
		            }))
                menu2Arr.push(new Ext.menu.Item({
		                text: "简报输出",
		                icon:"/images/export.gif",
		                handler: function(){
		                	briefingOut();
		                }
		            }))
			   </logic:equal>
			   <logic:equal name="evaluationForm" property="planStatus" value="6">
			      	<logic:equal name="evaluationForm" property="dispUnitScore" value="1">
                menu2Arr.push(new Ext.menu.Item({
			                text: "统一打分",
			                icon:"/images/edit.gif",
			                handler: function(){
			                	rate();
			                }
			            }))
			        </logic:equal>
				  	<logic:equal name="evaluationForm" property="busitype" value="0">
			            <hrms:priv func_id="3260404">
                        menu2Arr.push(new Ext.menu.Item({
                            text:"<bean:message key="kh.field.editscore"/>",
                            menu:menu2_1
                        }));
                        menu2Arr.push(new Ext.menu.Item({
				                text: "引入",
				                handler: function(){
				                	importexpre('${evaluationForm.code}');
				                }
				            }))
			            </hrms:priv>
				    </logic:equal>

				    <%	// 区分 绩效管理和能力素质 模块的功能授权
						if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
					%>
						<%if(userView.hasTheFunction("3260401") && userView.hasTheFunction("3260402")) {%>
							<hrms:priv func_id="3260401">
                             menu2Arr.push(new Ext.menu.Item({
					                text: "计算",
					                icon:"/images/compute.gif",
					                handler: function(){
					                	computeValidate();
					                }
					            }))
				            </hrms:priv>
					    <%} %>
					    <hrms:priv func_id="3260407">
                            menu2Arr.push(new Ext.menu.Item({
				                text: "提高等级",
				                handler: function(){
				                	adjustGrade(1);
				                }
				            }));
                            menu2Arr.push(new Ext.menu.Item({
				                text: "降低等级",
				                handler: function(){
				                	adjustGrade(2);
				                }
				            }))
			            </hrms:priv>
				    <%}else{%>
				    	<%if(userView.hasTheFunction("36030305") && userView.hasTheFunction("36030306")) {%>
				    		<hrms:priv func_id="36030305">
                            menu2Arr.push(new Ext.menu.Item({
					                text: "计算",
					                icon:"/images/compute.gif",
					                handler: function(){
					                	computeValidate();
					                }
					            }))
				            </hrms:priv>
					    <%} %>
					    <hrms:priv func_id="36030309">
                            menu2Arr.push(new Ext.menu.Item({
				                text: "提高等级",
				                handler: function(){
				                	adjustGrade(1);
				                }
				            }))
                            menu2Arr.push(new Ext.menu.Item({
				                text: "降低等级",
				                handler: function(){
				                	adjustGrade(2);
				                }
				            }))
			            </hrms:priv>
				    <%}%>

				    <%if(hand_Score.equalsIgnoreCase("0")){ %>
                        menu2Arr.push(new Ext.menu.Item({
			                text: "生成评语",
			                icon:"/images/check.gif",
			                handler: function(){
			                	generateRemark();
			                }
			            }))
				    <%}%>
                    menu2Arr.push(new Ext.menu.Item({
		                text: "结果分析",
		                icon:"/images/cards.bmp",
		                handler: function(){
		                	resultAnalyse();
		                }
		            }))
                    menu2Arr.push(new Ext.menu.Item({
		                text: "简报输出",
		                icon:"/images/export.gif",
		                handler: function(){
		                	briefingOut();
		                }
		            }))

				 	<%	// 区分 绩效管理和能力素质 模块的功能授权
						if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
					%>
						<hrms:priv func_id="3260411">
                            menu2Arr.push(new Ext.menu.Item({
				                text: "结果归档",
				                icon:"/images/deal.gif",
				                handler: function(){
				                	resultFiled();
				                }
				            }))
			            </hrms:priv>
				    <%}else{%>
				    	<hrms:priv func_id="36030310">
                            menu2Arr.push(new Ext.menu.Item({
				                text: "结果归档",
				                icon:"/images/deal.gif",
				                handler: function(){
				                	resultFiled();
				                }
				            }))
			            </hrms:priv>
				    <%}%>
				    </logic:equal>
				</logic:equal>


			    <logic:notEqual  name="evaluationForm" property="computeFashion" value="1">
				    <logic:equal name="evaluationForm" property="computeFashion" value="2">
					   <logic:equal name="evaluationForm" property="planStatus" value="6">
                            menu2Arr.push(new Ext.menu.Item({
				                text: "票数计算",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	voteCompute('${evaluationForm.code}');
				                }
				            }))
                            menu2Arr.push(new Ext.menu.Item({
				                text: "票数统计",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	voteStatis('${evaluationForm.code}');
				                }
				            }))
						</logic:equal>
				  	</logic:equal>
				  	<logic:equal name="evaluationForm" property="computeFashion" value="3">
					   <logic:equal name="evaluationForm" property="planStatus" value="6">
                        menu2Arr.push(new Ext.menu.Item({
				                text: "票数统计",
				                icon:"/images/edit.gif",
				                handler: function(){
				                	voteStatis('${evaluationForm.code}');
				                }
				            }))
						</logic:equal>
				  	</logic:equal>
			    </logic:notEqual>
        var menu2 = new Ext.menu.Menu({
            allowOtherMenus: false,
            items: menu2Arr
        });
        var menu3Arr = new Array();
	        	<%	// 区分 绩效管理和能力素质 模块的功能授权
				if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
			%>
				<hrms:priv func_id="3260406">
                    menu3Arr.push(new Ext.menu.Item({
		                text: "考核等级",
		                handler: function(){
		                	setKhDegree();
		                }
		            }))
	            </hrms:priv>
	            <hrms:priv func_id="3260402">
                menu3Arr.push( new Ext.menu.Item({
                    text: "计算规则",
                    handler: function(){
                        defineRule('${evaluationForm.planStatus}');
                    }
                }))
	            </hrms:priv>

			    <%if(userView.hasTheFunction("326040501")||userView.hasTheFunction("326040502")||userView.hasTheFunction("326040503")){ %>
			    	<hrms:priv func_id="3260405">
                    menu3Arr.push(new Ext.menu.Item({
			                text: "计算公式",
			                icon:"/images/compute.gif",
			                handler: function(){
			                	computFormula('${evaluationForm.planStatus}');
			                }
			            }))
		            </hrms:priv>
			    <%} %>

		    <%}else{%>
		    	<hrms:priv func_id="36030308">
                menu3Arr.push(new Ext.menu.Item({
		                text: "考核等级",
		                handler: function(){
		                	setKhDegree();
		                }
		            }))
	            </hrms:priv>
	            <hrms:priv func_id="36030306">
                menu3Arr.push(new Ext.menu.Item({
		                text: "计算规则",
		                handler: function(){
		                	defineRule('${evaluationForm.planStatus}');
		                }
		            }))
	            </hrms:priv>

			    <%if(userView.hasTheFunction("3603030701")||userView.hasTheFunction("3603030702")||userView.hasTheFunction("3603030703")){ %>
			    	<hrms:priv func_id="36030307">
                    menu3Arr.push(new Ext.menu.Item({
			                text: "计算公式",
			                icon:"/images/compute.gif",
			                handler: function(){
			                	computFormula('${evaluationForm.planStatus}');
			                }
			            }))
		            </hrms:priv>
			    <%} %>

		    <%}%>
        var menu3 = new Ext.menu.Menu({
            allowOtherMenus: false,
            items:menu3Arr
        });
	    var menu4_4 = new Ext.menu.Menu({
	        allowOtherMenus: false,
	        items: [
	        	<logic:notEqual name="evaluationForm" property="byModel" value="1">
			    	new Ext.menu.Item({
		                text: "分数",
		                icon:"/images/prop_ps.gif",
		                handler: function(){
		                	setPointResult(1);
		                }
		            }),
		            new Ext.menu.Item({
		                text: "平均分比值",
		                icon:"/images/prop_ps.gif",
		                handler: function(){
		                	setPointResult(2);
		                }
		            }),
		            new Ext.menu.Item({
		                text: "总分比值",
		                icon:"/images/prop_ps.gif",
		                handler: function(){
		                	setPointResult(3);
		                }
		            }),
		            new Ext.menu.Item({
		                text: "单项比值",
		                icon:"/images/prop_ps.gif",
		                handler: function(){
		                	setPointResult(4);
		                }
		            })
		    	</logic:notEqual>
	        ]
	    });
	    var menu4 = new Ext.menu.Menu({
	        allowOtherMenus: false,
	        items: [
	        	<logic:equal name="evaluationForm" property="computeFashion" value="1"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		        	new Ext.menu.Item({
		                text: "全部",
		                handler: function(){
		                	showResultData('true');
		                }
		            }),
		            new Ext.menu.Item({
		                text: "查询结果",
		                handler: function(){
		                	showResultData('false');
		                }
		            }),
		            new Ext.menu.Item({
		                text: "显示对象详情",
		                handler: function(){
		                	showDetails();
		                }
		            }),
		            new Ext.menu.Item({
		                text: "指标结果值",
		                menu: menu4_4
		            }),
		            <logic:equal  name="evaluationForm" property="object_type" value="2">
		    	        new Ext.menu.Item({
		    	            text: "简单查询",
		    	            handler: function(){
		    	            	simpleQuery();
		    	            }
		    	        }),
		            </logic:equal>
		            new Ext.menu.Item({
		                text: "手工选择",
		                icon: "/images/quick_query.gif",
		                handler: function(){
		                	handSel();
		                }
		            }),
		            <logic:notEqual  name="evaluationForm" property="planStatus" value="4">
		    	        new Ext.menu.Item({
		    	            text: "排序",
		    	            icon: "/images/sort.gif",
		    	            handler: function(){
		    	            	sort('${evaluationForm.code}','${evaluationForm.computeFashion}');
		    	            }
		    	        }),
		            </logic:notEqual>
		            new Ext.menu.Item({
		                text: "同步对象顺序",
		                icon: "/images/sort.gif",
		                handler: function(){
		                	synchronizeObjs('${evaluationForm.code}');
		                }
		            })
		        </logic:equal>

	            <logic:notEqual  name="evaluationForm" property="computeFashion" value="1">
		            <logic:notEqual name="evaluationForm" property="computeFashion" value="6">
			    	  	new Ext.menu.Item({
			                text: "全部",
			                handler: function(){
			                	showResultData('true');
			                }
			            }),
			            new Ext.menu.Item({
			                text: "查询结果",
			                handler: function(){
			                	showResultData('false');
			                }
			            }),
			            new Ext.menu.Item({
			                text: "显示对象详情",
			                handler: function(){
			                	showDetails();
			                }
			            }),
			            <logic:equal  name="evaluationForm" property="object_type" value="2">
			    	        new Ext.menu.Item({
			    	            text: "简单查询",
			    	            handler: function(){
			    	            	simpleQuery();
			    	            }
			    	        }),
			            </logic:equal>
			            new Ext.menu.Item({
			                text: "手工选择",
			                icon: "/images/quick_query.gif",
			                handler: function(){
			                	handSel();
			                }
			            }),
			            <logic:notEqual  name="evaluationForm" property="planStatus" value="4">
			    	        new Ext.menu.Item({
			    	            text: "排序",
			    	            icon: "/images/sort.gif",
			    	            handler: function(){
			    	            	sort('${evaluationForm.code}','${evaluationForm.computeFashion}');
			    	            }
			    	        }),
			    	        new Ext.menu.Item({
			    	            text: "同步对象顺序",
			    	            icon: "/images/sort.gif",
			    	            handler: function(){
			    	            	synchronizeObjs('${evaluationForm.code}');
			    	            }
			    	        })
			            </logic:notEqual>
			    	</logic:notEqual>
	    	  </logic:notEqual>
	        ]
	    });
	    var toolbar = Ext.create("Ext.Toolbar", {
	        renderTo: "toolbars",
	        width: 280,
            margin:"0 0 0 2",
            padding:0,
	        border:false,
	        items:[{
		        text: "文件",
		        menu: menu1,
                width:50
		    },
		    <logic:notEqual name="evaluationForm" property="computeFashion" value="4"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		    	<logic:notEqual name="evaluationForm" property="computeFashion" value="6">
				    {
				        text: "业务处理",
				        menu: menu2,
                        width:75
				    },
			    </logic:notEqual>
		    </logic:notEqual>
		    <logic:equal name="evaluationForm" property="computeFashion" value="1"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
			    {
			        text: "设置",
			        menu: menu3,
                    width:50
			    },
		    </logic:equal>
			<logic:notEqual name="evaluationForm" property="computeFashion" value="6">
		    {
		        text: "显示",
		        menu: menu4,
                width:50
		    }
		    </logic:notEqual>
		    ]
	    });
	});

</script>

<body onresize="resizeWindowRefrsh()">
<html:form action="/performance/evaluation/performanceEvaluation">
	<html:hidden name="evaluationForm" property="objStr"/>
	<html:hidden name="evaluationForm" property="objStr_temp"/>
	<%-- 只存放当前统计方式对应的排序字符串 add by 刘蒙 --%>
	<input type="hidden" name="order_str" value="${evaluationForm.computeFashionSQLMap[evaluationForm.computeFashion] }">
	<html:hidden name="evaluationForm" property="code"/>
	<html:hidden name="evaluationForm" property="planid"/>
	<html:hidden name="evaluationForm" property="object_type"/>
	<html:hidden name="evaluationForm" property="busitype"/>
	<html:hidden name="evaluationForm" property="khObjWhere"/>
	<html:hidden name="evaluationForm" property="khObjWhere2"/>
	<html:hidden name="evaluationForm" property="showBackTables"/>
	<html:hidden name="evaluationForm" property="templateid"/>
	<html:hidden name="evaluationForm" property="obtype"/>
<table width='100%'>
	<tr><td>
		<div id="toolbars" class="toolbars"/>
	</td></tr>
<%-- 	<tr><td>

	<table><tr><td colspan='2' >
<logic:notEqual name="evaluationForm" property="planid" value="">

	<!-- 得分统计开始 -->
	<logic:equal name="evaluationForm" property="computeFashion" value="1">
	</logic:equal>
	<!-- 得分统计结束 -->




	<!-- 非得分统计开始 -->
	<logic:notEqual  name="evaluationForm" property="computeFashion" value="1">	<!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->

	</logic:notEqual>
	<!-- 非得分统计结束 -->
</logic:notEqual>


</td>
</tr>
</table>
</td>
</tr>--%>


	<tr><td  valign='top' >
	<logic:equal name="evaluationForm" property="computeFashion" value="1"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		<%	// 区分 绩效管理和能力素质 模块的功能授权
			if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
		%>
			<%if(userView.hasTheFunction("3260401") && userView.hasTheFunction("3260402")) {%>
				<button type="button" style="margin-left:2px;" extra="button" class="button" id="cl1" onclick="computeValidate()"  <logic:equal name="evaluationForm" property="planStatus"  value="7">disabled </logic:equal> allowPushDown="false" down="false"><bean:message key="infor.menu.compute"/></button>
			<%} %>
		<%}else{%>
			<%if(userView.hasTheFunction("36030305") && userView.hasTheFunction("36030306")) {%>
				<button type="button" style="margin-left:2px;" extra="button" class="button" id="cl1" onclick="computeValidate()"  <logic:equal name="evaluationForm" property="planStatus"  value="7">disabled </logic:equal> allowPushDown="false" down="false"><bean:message key="infor.menu.compute"/></button>
			<%} %>
		<%}%>
	</logic:equal>
	<%--
		<logic:notEqual  name="evaluationForm" property="computeFashion" value="1">
			 <button    extra="button"    id="cl1" onclick="computeValidate()"  disabled  allowPushDown="false" down="false"><bean:message key="infor.menu.compute"/></button>
		</logic:notEqual>
	--%>
<logic:equal name="evaluationForm" property="computeFashion" value="1">
	<logic:equal name="evaluationForm" property="planStatus" value="6">
		<logic:equal name="evaluationForm" property="isHandScore" value="1">
			<logic:equal name="evaluationForm" property="pointResult" value="1">
				<button type="button" extra="button"  class="button" id="cl6" onclick="editScore()" allowPushDown="false" down="false"><bean:message key="lable.performance.makescore"/></button>
			</logic:equal>
		</logic:equal>
	</logic:equal>
</logic:equal>

	<logic:equal name="evaluationForm" property="planStatus" value="4">
		<logic:equal name="evaluationForm" property="isHandScore" value="1">
			<logic:equal name="evaluationForm" property="pointResult" value="1">
			 <logic:notEqual name="evaluationForm" property="computeFashion" value="6">
				<button type="button" extra="button"  class="button" id="cl6" onclick="editScore()" allowPushDown="false" down="false"><bean:message key="lable.performance.makescore"/></button>
				</logic:notEqual>
			</logic:equal>
		</logic:equal>
	</logic:equal>
	<logic:equal name="evaluationForm" property="computeFashion" value="1">
		<logic:notEqual  name="evaluationForm" property="planStatus" value="4">
			<button type="button" extra="button" class="button" id="cl3" onclick="resultAnalyse()" allowPushDown="false" down="false"><bean:message key="kh.field.analyseResult"/></button>
		</logic:notEqual>
		<%if(hand_Score.equalsIgnoreCase("0")){ %>
			<button type="button" extra="button" class="button" id="cl4" onclick="showRemark(1)" allowPushDown="false" down="false"><bean:message key="hire.employActualize.personnelFilter.comment"/></button>
		<%}%>
		<logic:equal name="evaluationForm" property="busitype" value="0">
			<button type="button" extra="button" class="button" id="cl5" onclick="showRemark(2)" allowPushDown="false" down="false"><bean:message key="lable.performance.perSummary"/></button>
		</logic:equal>
	</logic:equal>
	<logic:equal name="evaluationForm" property="computeFashion" value="2">
	<logic:equal name="evaluationForm" property="planStatus" value="6">
		<button type="button" extra="button" style="margin-left:2px;" class="button" id="cl32" onclick="voteStatis('${evaluationForm.code}')" allowPushDown="false" down="false"><bean:message key="label.commend.analyse"/></button>
		<button type="button" extra="button" class="button" id="cl31" onclick="voteCompute('${evaluationForm.code}')" allowPushDown="false" down="false"><bean:message key="jx.evaluation.statCompute"/></button>
	</logic:equal>
	</logic:equal>
	<logic:equal name="evaluationForm" property="computeFashion" value="3">
		<logic:equal name="evaluationForm" property="planStatus" value="6">
			<button type="button" extra="button" style="margin-left:2px;" class="button" id="cl32" onclick="voteStatis('${evaluationForm.code}')" allowPushDown="false" down="false"><bean:message key="label.commend.analyse"/></button>
		</logic:equal>
		</logic:equal>
	<logic:equal name="evaluationForm" property="computeFashion" value="1">
			<button type="button" extra="button" class="button" id="cl6" onclick="perInterview('${evaluationForm.interViewType}')" allowPushDown="false" down="false"><bean:message key="jx.khplan.interview"/></button>
	</logic:equal>
		  <logic:notEqual name="evaluationForm" property="computeFashion" value="6">
	<logic:notEqual name="evaluationForm" property="planStatus" value="4">
		<logic:notEqual name="evaluationForm" property="planStatus" value="">
			<button type="button" extra="button" style="margin-left:2px;" class="button" id="cl2" onclick="sort('${evaluationForm.code}','${evaluationForm.computeFashion}')" allowPushDown="false" down="false"><bean:message key="label.zp_exam.sort"/></button>
		</logic:notEqual>
	</logic:notEqual>
	</logic:notEqual>
	<hrms:priv func_id="3260417">
		<logic:equal name="evaluationForm" property="planStatus" value="6"><%-- 绩效评估 状态只能是 4：执行 6：评估 7：结束 只有6的时候且feedback不是1的情况下才显示结果反馈按钮 zhaoxg add 2017-2-27  --%>
			<logic:notEqual name="evaluationForm" property="feedback" value="1">
				<button type="button" extra="button" class="button" id="jg" onclick="jgfk()" style="margin-right:3px;" allowPushDown="false" down="false">结果反馈</button>
			</logic:notEqual>
		</logic:equal>
	</hrms:priv>
	<%	// 区分 绩效管理和能力素质 模块的功能授权
		if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
	%>
		<hrms:priv func_id="3260410">
			<button type="button" id="cl_sendBackTables" extra="button" class="button" onclick="sendBackTables('${evaluationForm.showBackTables}')" allowPushDown="false" down="false"><bean:message key="jx.eval.sendBackTables"/></button>
		</hrms:priv>
		<logic:equal name="evaluationForm" property="computeFashion"   value="1">
			<button type="button" id="card" extra="button" class="button" onclick="showCard()" allowPushDown="false" down="false"><bean:message key="button.card"/></button>
			<button type="button" id="card" extra="button" class="button" onclick="totalEvaluate()" allowPushDown="false" down="false"><bean:message key="lable.statistic.wholeeven"/></button>
		</logic:equal>
	<%}else{%>
		<%if(false){ //暂时隐藏%>
		<hrms:priv func_id="36030303">
			<button type="button" id="cl_sendBackTables" extra="button" class="button" onclick="sendBackTables('${evaluationForm.showBackTables}')" allowPushDown="false" down="false"><bean:message key="jx.eval.sendBackTables"/></button>
		</hrms:priv>
		<%}%>
		<logic:equal name="evaluationForm" property="planStatus" value="6">
			<hrms:priv func_id="36030312">
				<button type="button" id="propelling" extra="button" class="button" onclick="sendLessons()" allowPushDown="false" down="false"><bean:message key="button.propelling"/></button>
			</hrms:priv>
		</logic:equal>
		<logic:equal name="evaluationForm" property="planStatus" value="7">
			<hrms:priv func_id="36030312">
				<button type="button" id="propelling" extra="button" class="button" onclick="sendLessons()" allowPushDown="false" down="false"><bean:message key="button.propelling"/></button>
			</hrms:priv>
		</logic:equal>
		<logic:equal name="evaluationForm" property="computeFashion"   value="1">
			<button type="button" id="card" extra="button" class="button" onclick="showCard()" allowPushDown="false" down="false"><bean:message key="button.card"/></button>
			<button type="button" id="card" extra="button" class="button" onclick="totalEvaluate()" allowPushDown="false" down="false"><bean:message key="lable.statistic.wholeeven"/></button>
		</logic:equal>
	<%}%>

	<button type="button" id="cl_return" extra="button" class="button" onclick="return_bt()" allowPushDown="false" down="false"><bean:message key="button.return"/></button>
	&nbsp;&nbsp;&nbsp;
	</td>
	<td>
	<table>
    <tr>
    <td>
    &nbsp;
	<logic:equal name="evaluationForm" property="isShowComputFashion" value="1">
	<% if(versionFlag==1){%>
		<logic:equal name="evaluationForm" property="busitype" value="0">
			<bean:message key="kh.field.computeFashion"/>:
			<html:select name="evaluationForm" property="computeFashion" size="1" onchange="changeFashion()">
  				 <html:optionsCollection property="computeFashionList" value="dataValue" label="dataName"/>
			</html:select>&nbsp;
		</logic:equal>
	<% } %>
	</logic:equal>

	<logic:equal name="evaluationForm" property="computeFashion"   value="3">
	&nbsp;
	<bean:message key="lable.performance.perMainBodySort"/>:&nbsp;
	<html:select name="evaluationForm" property="bodyid" size="1" onchange="changeFashion()" >
  	 <html:optionsCollection property="bodyList" value="dataValue" label="dataName"/>
	</html:select>
	</logic:equal>
	<logic:equal name="evaluationForm" property="computeFashion"   value="6">
	&nbsp;
	<bean:message key="lable.performance.perMainBodySort"/>:&nbsp;
	<html:select name="evaluationForm" property="bodyid" size="1" onchange="changeFashion()" >
  	 <html:optionsCollection property="bodyList" value="dataValue" label="dataName"/>
	</html:select>
	</logic:equal>
	</td></tr>
	</table>


	</td></tr>
	<logic:equal name="evaluationForm" property="computeFashion" value="6">
		<tr>
			<td>
			 <logic:equal  name="evaluationForm" property="object_type" value="2">
			人员：
			</logic:equal>
			<logic:notEqual  name="evaluationForm" property="object_type" value="2">
			单位名称：
			</logic:notEqual>
				<%=name %>
			</td>
			<td>
				<input type=checkbox value=1 name=showdd onclick="doforcheck(this);" <%if(showaband!=null&&showaband.trim().length()!=0&&showaband.equalsIgnoreCase("1")){ %> checked<%} %>/>显示弃票&nbsp;
				<input type=checkbox value=2 name=showdd onclick="doforcheck(this);" <%if(showbenbu!=null&&showbenbu.trim().length()!=0&&showbenbu.equalsIgnoreCase("1")){ %> checked<%} %>/>显示本部平均&nbsp;
				<input type=checkbox value=3 name=showdd onclick="doforcheck(this);" <%if(showmethod!=null&&showmethod.trim().length()!=0&&showmethod.equalsIgnoreCase("2")){ %> checked<%} %>/>横向排列&nbsp;
				<%if(upa0100!=null && upa0100.trim().length()>0) { %>
				    <a href="javascript:hre(0);" >首条&nbsp;</a>
				<%} else  { %>
				     <a href="###" disabled style="color:#414141 !important;cursor: default;">首条&nbsp;</a>
				<%} %>
				<%if(upa0100!=null && upa0100.trim().length()>0) { %>
				    <a href="javascript:hre(1);" >上一条&nbsp;</a>
				<%} else { %>
				    <a href="###" disabled style="color:#414141 !important;cursor: default;">上一条&nbsp;</a>
				<%} %>
				<%if(nexta0100!=null && nexta0100.trim().length()>0) { %>
					<%if("1".equals(nexta0100.split("`")[1])){ %>
						<a href="###" disabled style="color:#414141 !important;cursor: default;">下一条&nbsp;</a>
					<%}else{ %>
				    	<a href="javascript:hre(2);" >下一条&nbsp;</a>
				    <%} %>
				<%} else { %>
				    <a href="###" disabled style="color:#414141 !important;cursor: default;">下一条&nbsp;</a>
				<%} %>
				<%if(nexta0100!=null && nexta0100.trim().length()>0) { %>
					<%if("1".equals(nexta0100.split("`")[1])){ %>
						<a href="###" disabled style="color:#414141 !important;cursor: default;">末条&nbsp;</a>
					<%}else{ %>
				    	<a href="javascript:hre(3);" >末条&nbsp;</a>
				    <%} %>
				<%} else { %>
				    <a href="###" disabled style="color:#414141 !important;cursor: default;">末条&nbsp;</a>
				<%} %>
			</td>		
		</tr>
		<input type="hidden" name="upa0100" id="upa0100" value="${evaluationForm.upa0100}" />
		<input type="hidden" name="nexta0100" id="nexta0100" value="${evaluationForm.nexta0100}" />
		<input type="hidden" name="a0100" id="a0100" value="${evaluationForm.a0100}" />
		<input type="hidden" name="templateid" id="templateid" value="${evaluationForm.templateid}" />
		<input type="hidden" name="showmethod" id="showmethod" value="${evaluationForm.showmethod}" />
		<input type="hidden" name="showaband" id="showaband" value="${evaluationForm.showaband}" />
		<input type="hidden" name="showbenbu" id="showbenbu" value="${evaluationForm.showbenbu}" />
	</logic:equal>
</table>
<!-- 【753】票数占比反馈表中，所有主体（分类统计）下，指标解释显示太窄了，建议加宽些  jingq 2015.01.19 -->
<table cellpadding="0" cellspacing="0" border="0" width="100%"><tr><td width="100%">
<!-- 开始显示数据 -->
<script language='javascript' >
    var theHeight = document.documentElement.clientHeight||document.body.clientHeight;
			document.write("<div id=\"tbl-container\" style='position:absolute;width:99%;overflow:auto;height:"+(theHeight-125)+"px;' >");
</script>	
 
	${evaluationForm.evaluationTableHtml}
		<%
			int i = 0;
			LazyDynaBean abean=null;
			String object_id="";
			ArrayList mainbodySetList = null;
			String a0101="";
			ArrayList perGradeTemplateList = null;
		%>
	<hrms:extenditerate id="element" name="evaluationForm" property="setlistform.list" indexes="indexes" pagination="setlistform.pagination" pageCount="20" scope="session">
		<%
			 	int _index=((Integer)pageContext.getAttribute("indexes")).intValue();
				String className="trShallow";
				String className2="t_cell_locked common_border_color";
				String className3="t_cell_locked5 common_border_color";
				String color="#F3F5FC";
				if(i%2==1)
				{
					 className="trDeep";
					 className2="t_cell_locked common_border_color";
					 className3="t_cell_locked5 common_border_color";
					 color="#E4F2FC";
				}	i++;
				if(lastIndex==i) {
					className2 = "t_cell_locked_b common_border_color";
					className3="t_cell_locked5_b common_border_color";
				}
		%>		
		<logic:equal name="evaluationForm" property="computeFashion"  value="1"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		<%
			abean=(LazyDynaBean)pageContext.getAttribute("element");
 	   		object_id=(String)abean.get("object_id");	
		%>
	        <tr class="<%= className%>" onClick="javascript:tr_onclick_self(this,'<%=color %>')" id='<bean:write name='element' property='object_id' filter='true' />' id_s='<%=object_id%>' >
				<td id='a' class='<%= className2%>' align='center' nowrap width='50' style="background:none !important;"><%=(_index+1)%></td>
				<!-- 显示对象详情 -->
				<%if("true".equals(showDetails)){
				%>
					<%if("2".equals(object_type)) {%>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />' style='background:none !important;'>&nbsp;<bean:write name='element' property='b0110' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />' style='background:none !important;'>&nbsp;<bean:write name='element' property='e0122' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />' style='background:none !important;'>&nbsp;<bean:write name='element' property='e01a1' filter='true' /></td>
					<%} %>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />' style='background:none !important;'>&nbsp;<bean:write name='element' property='body_id' filter='true' /></td>
				<%	
				}
				%>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />' style='background:none !important;'>&nbsp;<bean:write name='element' property='a0101' filter='true' /></td>
				<%
		     	    //查看得分明细功能     郭峰修改
		     	    a0101=(String)abean.get("a0101");	     		
					String planid=(String)abean.get("planid");
		     		ArrayList a_pointList = new ArrayList();
					if(!byModel.equals("1")){//按岗位素质模型测评不需要显示指标的分数   郭峰
						a_pointList = (ArrayList)abean.get("a_pointList");
					}
		     		String handScore = (String)abean.get("handScore");
		     		HashMap handScorePointMap = (HashMap)abean.get("handScorePointMap");	
		     		ArrayList planlist = (ArrayList)abean.get("planlist");
		     		HashMap planObjectScoreMap = (HashMap)abean.get("planObjectScoreMap");	
		     		String gradedisp = (String)abean.get("gradedisp");
		     		ArrayList gradeTempList = (ArrayList)abean.get("gradeTempList");
		     		String isCorrectScoreObj = (String)abean.get("isCorrectScoreObj");
		     		ArrayList subsetfilds = (ArrayList)abean.get("subsetfilds");
		     		String wholeEvalMode = (String)abean.get("wholeEvalMode");
		     		
		     		for (int n = 0; n < subsetfilds.size(); n++)
					{
						String field = (String)subsetfilds.get(n);
						FieldItem fieldItem = DataDictionary.getFieldItem(field);
						String fieldType = fieldItem.getItemtype();
						String _value = (String)abean.get(field);
		     	%>
		     		<td id='a' style='border-top-width:0px;' class='RecordRow'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		     		  		<%if(fieldType.equalsIgnoreCase("M")){///如果是备注型数据，则显示放大镜
		     		  		%>
		     		  			<img src='/images/view.gif' BORDER='0' style='cursor:hand;' onclick='QueryRemarkField("<%=a0101 %>","<%=object_id %>","<%=field %>")'>
		     		  		<%
		     		  		}
		     		  		else
		     		  		{
		     		  		%>
		     		  		<%=_value %>
		     		  		<%} %>
		     		</td>	
		     	<%
		     		}
		     		for (int j = 0; j < a_pointList.size(); j++)
					{
			   			 String[] temp = (String[]) a_pointList.get(j);	
		     	%>
					<td id='a' class='RecordRow' style="border-left:none;border-top:none;" align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
				<%
					    LazyDynaBean aabean=(LazyDynaBean)handScorePointMap.get(temp[0]);
					    String theValue = (String)abean.get(temp[0]);	
						if(handScore.equals("1"))//在考核实施里面计划以[启动(录入结果)]方式启动
			 	  		{			 	  				
							String theName = planid+"/"+object_id+"/"+temp[0]+"/"+(String)aabean.get("score");	
							theValue = (String)abean.get(theName);
							if(startEditScore.equals("1")){			
				%>
				            <input type='text' value='<%=theValue %>' onblur='autoValue(this,"<%=EvalOutLimitStdScore %>")'  name='<%=theName %>'  class='inputtext TEXT_NB' size='5'  />
				            <%}else{ %>
				           	 <%=theValue %>
				           <%} %> 
				<% 
						} else//在考核实施里面计划以[启动(打分)]方式启动
						{
						    if(handScorePointMap.get(temp[0])!=null)//如果为打分指标的话还是在考核评估里面以录入方式打分
			    			{	
			    				String pointPriv = (String)abean.get(planid+"/"+object_id+"/"+temp[0]+"/pointPriv");	
								String theName = planid+"/"+object_id+"/"+temp[0]+"/"+(String)aabean.get("score");	
																		
								theValue = (String)abean.get(theName);	
								//定量统一打分的指标 还要判断对考核对象是否有权限 在如下判断中加入	&& pointPriv.equals("1") 即可
								if(startEditScore.equals("1") && pointPriv.equals("1")){																	    		
				%>
					   <input type='text' value='<%=theValue %>' onblur='autoValue(this,"<%=EvalOutLimitStdScore %>")'  name='<%=theName %>'  class='inputtext TEXT_NB' size='5'  />
					           <%}else{ %>
					         	 <%=theValue %>
					      			<%} %> 	 
				<% 
							}else{
				%>	
						<%=theValue %>
			 					<%}}%>	
			 		 </td>		
			 		<%}
					for(int m=planlist.size()-1;m>=0;m--)
					{
						LazyDynaBean _abean=(LazyDynaBean)planlist.get(m);
						String id=(String)_abean.get("id");											
						String Menus=(String)_abean.get("Menus");
						if(Menus!=null&&Menus.trim().length()>0)
						{
							String[] temps=Menus.split(",");
							for(int b=0;b<temps.length;b++)
							{
								String temp=temps[b].trim();
								if(temp.length()==0)
									continue;
								String theValue = "";
								if(temp.equalsIgnoreCase("score"))						
									theValue =(String) abean.get("G_"+id);							
								else if(temp.equalsIgnoreCase("Grade"))						
									theValue =(String) abean.get("G_"+id+"_Grade");							
								else if(temp.equalsIgnoreCase("Avg"))
									theValue =(String) abean.get("G_"+id+"_Avg");						
								else if(temp.equalsIgnoreCase("Max"))
									theValue =(String) abean.get("G_"+id+"_Max");	
								else if(temp.equalsIgnoreCase("Min"))
									theValue =(String) abean.get("G_"+id+"_Min");
								else if(temp.equalsIgnoreCase("XiShu"))
									theValue = (String) abean.get("G_"+id+"_XiShu");
								else if(temp.equalsIgnoreCase("Order"))
									theValue =(String) abean.get("G_"+id+"_Order");
								else if(temp.equalsIgnoreCase("UMOrd"))
									theValue =(String) abean.get("G_"+id+"_UMOrd");
								else if(temp.equalsIgnoreCase("Mark"))
									theValue =(String) abean.get("G_"+id+"_Mark");
								else if(temp.indexOf("Body")!=-1)
								{
									String bodyid=temp.replaceAll("Body","");
									theValue =(String) abean.get("G_"+id+"_B_"+(bodyid.equals("-1")?"X1":bodyid));						
								}
								else if(temp.indexOf("Item")!=-1)
								{
									String itemid=temp.replaceAll("Item","");
									theValue =(String) abean.get("G_"+id+"_Item"+itemid);	
								}
								else if(!temp.equalsIgnoreCase("UMNum")&&!temp.equalsIgnoreCase("GrpNum")) 
									theValue =(String) abean.get("G_"+id+"_"+temp);	
							%>	
							<% if(!temp.equalsIgnoreCase("UMNum")&&!temp.equalsIgnoreCase("GrpNum")){ %>
								<td style='border-top-width:0px;' id='a' class='RecordRow'  align='center' width='<bean:write name='element' property='columnWidth' filter='true' />' nowrap >
									<%=theValue %>
			 		   			 </td>
			 		   			 <% }%>
							  <%							
							}
						}else	{	
									%>		
								<td id='a' class='RecordRow'  align='center' width='<bean:write name='element' property='columnWidth' filter='true' />' nowrap >
									<%=(String) abean.get("G_"+id) %>	
			 		    		</td>
									  <%}
						String HZMenus=(String)_abean.get("HZMenus");
						String temp2 ="";
						if(HZMenus!=null&&HZMenus.trim().length()>0)
						{
							String[] temps=HZMenus.split(",");
							for(int b=0;b<temps.length;b++)
							{    
								String temp=temps[b].trim();
								String temphz=temps[b].trim();
								
								if(temp.indexOf(":")!=-1){
						        temp = temp.substring(0,temp.indexOf(":"));	
						        temp2 = "_Z"+temphz.substring(temphz.indexOf(":")+1);
						        }
								if(temp.length()==0)
									continue;
								String theValueHZ = "";
								if(temp.equalsIgnoreCase("score"))						
									theValueHZ =(String) abean.get("G_"+id+temp2);							
								else if(temp.equalsIgnoreCase("Grade"))						
									theValueHZ =(String) abean.get("G_"+id+"_Grade"+temp2);							
								else if(temp.equalsIgnoreCase("Avg"))
									theValueHZ =(String) abean.get("G_"+id+"_Avg"+temp2);						
								else if(temp.equalsIgnoreCase("Max"))
									theValueHZ =(String) abean.get("G_"+id+"_Max"+temp2);	
								else if(temp.equalsIgnoreCase("Min"))
									theValueHZ =(String) abean.get("G_"+id+"_Min"+temp2);
								else if(temp.equalsIgnoreCase("XiShu"))
									theValueHZ = (String) abean.get("G_"+id+"_XiShu"+temp2);
								else if(temp.equalsIgnoreCase("Order"))
									theValueHZ =(String) abean.get("G_"+id+"_Order"+temp2);
								else if(temp.equalsIgnoreCase("UMOrd"))
									theValueHZ =(String) abean.get("G_"+id+"_UMOrd"+temp2);
								else if(temp.equalsIgnoreCase("Mark"))
									theValueHZ =(String) abean.get("G_"+id+"_Mark"+temp2);
								else if(temp.indexOf("Body")!=-1)
								{
									String bodyid=temp.replaceAll("Body","");
									theValueHZ =(String) abean.get("G_"+id+"_B_"+(bodyid.equals("-1")?"X1":bodyid)+temp2);						
								}
								else if(temp.indexOf("Item")!=-1)
								{
									String itemid=temp.replaceAll("Item","");
									theValueHZ =(String) abean.get("G_"+id+"_Item"+itemid+temp2);	
								}
								else  
									theValueHZ =(String) abean.get("G_"+id+"_"+temp+temp2);
							%>	
								<td id='a' class='RecordRow'   align='center' width='<bean:write name='element' property='columnWidth' filter='true' />' nowrap >
									<%=theValueHZ %>
			 		   			 </td>
							  <%							
							}
						}	  
									  
						     								
					} 		
			 	 %>
			 	    <td id='a'  name="original_score"  style="border-top:none;border-left:none;" class='RecordRow' align='center' nowrap width='70'>
			 	    	<bean:write name='element' property='original_score' filter='true' />
			 	    </td>	
			 	    <td id='a'  name='score' class='RecordRow' style="border-top:none;" <%if(isCorrectScoreObj.equals("1")) { %>style="BACKGROUND-COLOR: #98FB98;" <%} %> align='center' nowrap width='70'>
			 	    	<%if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){ //绩效 %>
			 	    		<%if(ver.searchFunctionId("3260416") && versionFlag==1 ) {%> 
			 	    		<logic:notEqual value="1" name="evaluationForm" property="handScore">
			 	    		<%if("1".equals(Plan_type)) {%> 
				 	    		<hrms:priv func_id="3260416">
				 	    			<a href="javascript:scoreDetail();">
				 	    		</hrms:priv>
				 	    	<%} %>
			 	    		</logic:notEqual>
			 	    		<bean:write name='element' property='score' filter='true' />
			 	    		<logic:notEqual value="1" name="evaluationForm" property="handScore">
				 	    		<hrms:priv func_id="3260416">
				 	    			</a>
				 	    		</hrms:priv>
				 	    	</logic:notEqual>
			 	    		<%} else {%>
			 	    		<bean:write name='element' property='score' filter='true' />
			 	    		<%} %>
			 	    	<%}else{//能力素质 %>
			 	    		<bean:write name='element' property='score' filter='true' />
			 	    	<%} %>
			 	    </td>
			 	    <% if("1".equals(deviationScore)){%><!-- 纠偏总分  pointResultValue指标结果值 1:分数 2:平均分比值 3:总分比值 4:单项比值  &&"1".equals(pointResultValue)-->
			 	    <td id='a'  name="deviation_score" style="border-top:none;" class='RecordRow' align='center' nowrap width='70'><bean:write name='element' property='reviseScore' filter='true' /></td><!-- 纠偏总分 -->
			 	    <%} %>
			 	    <logic:equal name="evaluationForm" property="busitype" value="1">
			 	    	<td id='a' name="postRuleScore" style="border-top:none;" class='RecordRow' align='center' nowrap width='100'>
				 	    	<bean:write name='element' property='postRuleScore' filter='true' />
				 	    </td>
			 	    	<td id='a' name="mateSurmise" style="border-top:none;" class='RecordRow' align='center' nowrap width='70'>
				 	    	<bean:write name='element' property='mateSurmise' filter='true' />
				 	    </td>			 	    	
			 	    </logic:equal>	
			 	    
			 	    <%		
			 	    	if((GrpMenu1==null && GrpMenu1.trim().length()<=0) && (GrpMenu2==null && GrpMenu2.trim().length()<=0))
			 	    	{
					%>
				 	    <td id='a' name='exs_umPaiMing' style="border-top:none;" class='RecordRow' align='center' nowrap width='70' >
	 						<bean:write name='element' property='umPaiMing' filter='true' />
	 					</td>
 					<%	}else
 						{
 							String[] str1=GrpMenu1.split(";");
							String[] str2=GrpMenu2.split(";");
							if((str1[0]!=null && str1[0].trim().length()>0) || (str2[0]!=null && str2[0].trim().length()>0))
							{}
							else
							{								
 					%>
 						<td id='a' name='exs_umPaiMing' style="border-top:none;" class='RecordRow' align='center' nowrap width='70' >
	 						<bean:write name='element' property='umPaiMing' filter='true' />
	 					</td>
 					<%}} %>
			 	    <td id='a' name='exs_grpavg' style="border-top:none" class='RecordRow' align='center' nowrap width='70'>
			 	     	<bean:write name='element' property='exs_grpavg' filter='true' />
			 	    </td>
			 	    <td id='a' name='exs_grpmax' style="border-top:none;" class='RecordRow' align='center' nowrap width='70'>
			 	    	<bean:write name='element' property='exS_GrpMax' filter='true' />
			 	    </td>
 					<td id='a' name='exs_grpmin' style="border-top:none;" class='RecordRow' align='center' nowrap width='70'>
 						<bean:write name='element' property='exS_GrpMin' filter='true' />
 					</td>					
			 	    <td id='a' name='exs_paiming'style="border-top:none;"  class='RecordRow' align='center' nowrap width='70' >
 						<bean:write name='element' property='paiming' filter='true' />
 					</td>
 					<logic:equal name="evaluationForm" property="busitype" value="0">						
				 	    <td id='a' name='exx_object' style="border-top:none;" class='RecordRow' align='center' nowrap width='70'>
				 	    	<bean:write name='element' property='exx_object' filter='true' />
				 	    </td>
			 	    </logic:equal>
			 	    <td id='a' name='desc'style="border-top:none;" class='RecordRow' align='left' nowrap width='70'>
			 	    	&nbsp;<bean:write name='element' property='desc' filter='true' />
			 	    </td>
			 	    			 	    
			 	    <logic:equal name="evaluationForm" property="busitype" value="0">			 	    			 	    
				 	    <td id='a' name='addScore' style="border-top:none;" class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
				 	    	<bean:write name='element' property='addScore' filter='true' />
				 	    </td>
				 	    <td id='a' name='minusScore' style="border-top:none;" class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
				 	    	<bean:write name='element' property='minusScore' filter='true' />
				 	    </td>
			 	    </logic:equal>			 	    
			 	    			 	    			 	    
			 	    <td id='a' name='evalremark' style="border-top:none;border-left:none;" class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 	     	 <img src='/images/view.gif' BORDER='0' style='cursor:hand;' onclick='QueryRemark("<%=a0101 %>","<%=object_id %>")'>
			 	    </td> 
			 	    
			 	    <%
			 	    	if(showEvalDirector.equalsIgnoreCase("true") && (!object_type.equalsIgnoreCase("2"))){
			 	    %>
			 	    <td id='a' name='director' class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'><!-- 【6740】绩效管理：绩效评估，负责人列下的线是蓝色的，和绿色皮肤不符合  jingq add 2015.01.20 -->
			 	     	  <input type='text' id='director_val' value='<bean:write name='element' property='director' filter='true' />' onblur='autoSave(this,"<%=object_id %>")'   class='TEXT_NB common_border_color' size='5'  />
			 	    </td> 
			 	    
			 	    <%}
			 	    	if(allowAdjustEvalResult.equalsIgnoreCase("true")){
			 	    %>
			 	      <td id='a' name='score_adjust' class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 	     		 <bean:write name='element' property='score_adjust' filter='true' />
			 	   	  </td>
			 	    <%}
			 	    	if(gradedisp.equalsIgnoreCase("true"))
			 	    	{
			 	    		if("0".equals(wholeEvalMode)){
				 	    		for (int j = 0; j < gradeTempList.size(); j++)
				 		  	   {
									LazyDynaBean a_bean = (LazyDynaBean) gradeTempList.get(j);
									String id = (String) a_bean.get("id");
									String value = abean.get("V_" + id) != null ? (String) abean.get("V_" + id) : "";
				 	     	%>
				 	     			<td id='a'  class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
				 	          		 <%= value%>
				 	       		    </td>
				 	    	 <%}%>
				 	    	  <td id='a'  class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 	    			<bean:write name='element' property='V_SUM' filter='true' />
			 	    		</td>	
			 	    		
			 	    		<% } else if("1".equals(wholeEvalMode)){
			 	    	  %>
			 	    	   <td id='a'  class='RecordRow' align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 	    			<bean:write name='element' property='wholeEvalScore' filter='true' />
			 	    		</td>
			 	    	  <%}} %>	
			 	   		<td id='a' name='evalremark' style="border-top:none;border-left:none;" class='RecordRow' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 	     	 	<bean:write name='element' property='confirmFlag' filter='true' />
			 	    	</td> 
			</tr>
		</logic:equal>
		
		
		<logic:equal name="evaluationForm" property="computeFashion"  value="2"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
	  		<tr class="<%= className%>" onClick="javascript:tr_onclick_self(this,'<%=color %>')">
				<td id='a' class='<%= className2%>'  align='center' nowrap width='50'><%=(_index+1)%></td>
								<!-- 显示对象详情 -->
				<%if("true".equals(showDetails)){
				%>
					<%if("2".equals(object_type)) {%>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='b0110' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='e0122' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='e01a1' filter='true' /></td>
					<%} %>
					<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='body_id' filter='true' /></td>
				<%	
				}
				%>
				<td id='a' class='<%= className2%>'  align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='a0101' filter='true' /></td>
				<%
		     	    abean=(LazyDynaBean)pageContext.getAttribute("element");
		     	    mainbodySetList = (ArrayList)abean.get("mainbodySetList");
		     	    perGradeTemplateList = (ArrayList)abean.get("perGradeTemplateList");
		     		 for (int j = 0; j < mainbodySetList.size(); j++)
		  		     {
						LazyDynaBean bodyBean = (LazyDynaBean) mainbodySetList.get(j);
						String body_id = (String) bodyBean.get("body_id");
						if(body_id.equals("-1"))
							body_id="X1";
						for (int e = 0; e < perGradeTemplateList.size(); e++)
						{
			   				 LazyDynaBean tempBean = (LazyDynaBean) perGradeTemplateList.get(e);
			   				 String grade_template_id = (String) tempBean.get("grade_template_id");
			   				 String theVal =  (String) abean.get("B_B" + body_id + "_G" + grade_template_id);
		     	%>
		     	<td  class='RecordRow' align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		     		<%= theVal%>
		     	</td>
		     		<% }
		     				 String bodyVal =  (String) abean.get("V_" + body_id);
		     		%>
		     	<td  class='RecordRow' align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		     		<%= bodyVal%>
		     	</td>
		     	 <% }
		     	 	for (int e = 0; e < perGradeTemplateList.size(); e++)
		   			{
		   				LazyDynaBean tempBean = (LazyDynaBean) perGradeTemplateList.get(e);
						String grade_template_id = (String) tempBean.get("grade_template_id");
						String theVal =  (String) abean.get("S_" + grade_template_id);
		     	 %>    				
		     			<td  class='RecordRow' align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		     				<%= theVal%>
		     			</td>
		     	  <%}%>	     	  
		     	  <td  class='RecordRow' align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		     		<bean:write name='element' property='VoteNum' filter='true' />
		     	  </td>	
		  	</tr>	  
		</logic:equal>
		
		
		<logic:equal name="evaluationForm" property="computeFashion"  value="3"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
		 	<!-- 【758】指标票数统计中，指标标度和参与情况界面显示风格建议保持一致，不然界面显示太过凌乱  jingq add 2015.01.20 -->
		 	<style>
		 		.cell_locked5{
		 			font-weight:bold;
		 		}
		 		.RecordRow{
		 			border-left:none;
		 		}
		 	</style>
		 	<tr class="<%= className%>" onClick="javascript:tr_onclick_self(this,'<%=color %>')">
				<td id='a' class='<%= className3%>'  align='center' nowrap width='50'><%=(_index+1)%></td>
												<!-- 显示对象详情 -->
				<%if("true".equals(showDetails)){
				%>
					<%if("2".equals(object_type)) {%>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='b0110' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='e0122' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='e01a1' filter='true' /></td>
					<%} %>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='body_id' filter='true' /></td>
				<%	
				}
				%>
				<td id='a' class='<%= className3%>'  align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='a0101' filter='true' /></td>
		<%
		     abean=(LazyDynaBean)pageContext.getAttribute("element");
		     object_id= (String) abean.get("object_id");	
		     String votecount = (String) abean.get("votecount");
		     String mainbody = (String) abean.get("mainbody"); 
		     if("0".equals(mainbody)){%>
		     	<td id='a' class='<%= className3%>'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;</td>
		     <%} else { %>
		     	<td id='a' class='<%= className3%>'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='mainbody' filter='true' /></td>
		     <%} %>
		     <% if("0".equals(votecount)){%>
		    	 <td id='a' class='<%= className3%>'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;</td>
				<td id='a' class='<%= className3%>'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;</td>
		     <%} else{%>
		     <td id='a' class='<%= className3%>'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='votecount' filter='true' /></td>
			 <td id='a' class='<%= className3%>'  align='center' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='voterate' filter='true' />%</td>
		    <% }
		     ArrayList perPointList = (ArrayList)abean.get("perPointList");
		     perGradeTemplateList = (ArrayList)abean.get("perGradeTemplateList");
		     HashMap resultMap = (HashMap)abean.get("resultMap");
		     LazyDynaBean a_bean = (LazyDynaBean) resultMap.get(PubFunc.decrypt(object_id));
		     HashMap templatePoint = (HashMap)abean.get("templatePoint");
		     String proAppraise=myForm.getProAppraise();//判断是否定义了“描述性评议项”
		     boolean flag=false;//zhaoxg add
		     if("true".equals(proAppraise)){
		     	flag=true;
		     }
		     for (int j = 0; j < perPointList.size(); j++)
		  	 {
					String[] temp = (String[]) perPointList.get(j);	
					ArrayList gradeList=(ArrayList) templatePoint.get(temp[0].toLowerCase());
						for (int e = 0; gradeList !=null && e < gradeList.size(); e++)
						{
				   			LazyDynaBean tempBean = (LazyDynaBean) gradeList.get(e);
				   			if (a_bean != null)
							{	
								String col = "P_C_"+temp[0]+"_G_G"+(String) tempBean.get("gradecode");
								String theVal = (String)abean.get(col);						
			 		%>
			 				<td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 					<%= theVal%>
			 				</td>	
			 			  <%}else{%>
			 			   <td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 			   		&nbsp;
			 			   </td>	
			 				  <%}	
			 				  
			 				 if (a_bean != null&&flag)
							{	
								String col = "P_C_"+temp[0]+"_G_G"+(String) tempBean.get("gradecode");
								String theVal = (String)abean.get(col);		
								String _col = "V_C_"+temp[0];
					  			String _theVal = (String)abean.get(_col);	

					  			BigDecimal a = new BigDecimal(theVal==null||theVal.equals("")?"0":theVal);
       							BigDecimal b = new BigDecimal(_theVal==null||_theVal.equals("")?"0":_theVal);
					  			String num="";
					  			if(a.compareTo(BigDecimal.ZERO)  == 0 || b.compareTo(BigDecimal.ZERO) == 0){
					  				num="";
					  			}else{
					  				
					  				num=a.divide(b,4,BigDecimal.ROUND_HALF_UP).toString();
					  				num = Double.parseDouble(num)*100+"%";
					  			}		
			 		%>
			 				<td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 					<%=num %>
			 				</td>	
			 			  <%}else if(flag){%>
			 			   <td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
			 			   		&nbsp;
			 			   </td>	
			 				  <%}	
			 			  }
					if (a_bean != null)
					  {	  
					  	 String col = "V_C_"+temp[0];
					  	 String theVal = (String)abean.get(col);		
		 			  %>
		 				<td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 					<%= theVal%>
		 				</td>	
		 			  <%}else{%>
		 			   <td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 			   		&nbsp;
		 			   </td>	
		 				  <%}		 	  
		 	  		}%>
		 	  		<%
		 	  		String gradedisp = (String)abean.get("gradedisp");
		     		ArrayList gradeTempList = (ArrayList)abean.get("gradeTempList");
		     		String wholeEvalMode = (String)abean.get("wholeEvalMode");
		     		String uselessvote = (String)abean.get("uselessvote");
		     		String usefullvote = (String)abean.get("usefullvote");
		     				 	BigDecimal aa = new BigDecimal(uselessvote==null||uselessvote.equals("")?"0":uselessvote);
		     				 	String cc=(Double.parseDouble(uselessvote==null||uselessvote.equals("")?"0":uselessvote)+Double.parseDouble(usefullvote==null||usefullvote.equals("")?"0":usefullvote))+"";
       							BigDecimal bb = new BigDecimal(cc==null||cc.equals("")?"0":cc);
					  			String _num="";
					  			if(aa.compareTo(BigDecimal.ZERO)  == 0 || bb.compareTo(BigDecimal.ZERO) == 0){
					  				_num="";
					  			}else{
					  				
					  				_num=aa.divide(bb,4,BigDecimal.ROUND_HALF_UP).toString();
					  				_num = Double.parseDouble(_num)*100+"%";
					  			}	
		     		if(gradedisp.equalsIgnoreCase("true"))
		 	    	{
		 	    		for (int j = 0; j < gradeTempList.size(); j++)
		 		  	   {
		 	    			LazyDynaBean a_gradebean = (LazyDynaBean) gradeTempList.get(j);
		 	    			String id = (String) a_gradebean.get("id");
		 	    			String value = abean.get("V_" + id) != null ? (String) abean.get("V_" + id) : "";
		 	    				BigDecimal a = new BigDecimal(value==null||value.equals("")?"0":value);
       							BigDecimal b = new BigDecimal(usefullvote==null||usefullvote.equals("")?"0":usefullvote);
					  			String num="";
					  			if(a.compareTo(BigDecimal.ZERO)  == 0 || b.compareTo(BigDecimal.ZERO) == 0){
					  				num="";
					  			}else{
					  				
					  				num=a.divide(b,4,BigDecimal.ROUND_HALF_UP).toString();
					  				num = Double.parseDouble(num)*100+"%";
					  			}	
		 	  		%>
		 	  		<td  class='RecordRow'  align='center'   nowrap width='60'>
		 			   		<%=value %>
		 			 </td>
		 			 <%if(flag){ %>
		 			<td  class='RecordRow'  align='center'   nowrap width='60'>
		 			   		<%=num %>
		 			 </td>
		 			 <%}}%>
		 			 <td  class='RecordRow'  align='center'   nowrap width='60'>
		 			   		<%=uselessvote %>
		 			 </td>
		 			 <%if(flag){ %>
		 			 <td  class='RecordRow'  align='center'   nowrap width='60'>
		 			   		<%=_num %>
		 			 </td>
		 			 <%}%>
		 			 <td  class='RecordRow'  align='center'   nowrap width='60'>
		 			   		<%=usefullvote %>
		 			 </td>
		 	  		<% } %>
			</tr> 		  
		</logic:equal>
		
		
		<logic:equal name="evaluationForm" property="computeFashion"  value="4"><!--1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计  -->
			<tr class="<%= className%>" onClick="javascript:tr_onclick_self(this,'<%=color %>')" id='<bean:write name='element' property='object_id' filter='true' />' id_s=''>
				<td id='a' class='<%= className2%>'  align='center' nowrap width='50'><%=(_index+1)%></td>
												<!-- 显示对象详情 -->
				<%if("true".equals(showDetails)){
				%>
					<%if("2".equals(object_type)) {%>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='b0110' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='e0122' filter='true' /></td>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='e01a1' filter='true' /></td>
					<%} %>
				<td id='a' class='<%= className2%>' align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='body_id' filter='true' /></td>
				<%	
				}
				%>
				<td id='a' class='<%= className2%>'  align='left' nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>&nbsp;<bean:write name='element' property='a0101' filter='true' /></td>
				<%
		     	    abean=(LazyDynaBean)pageContext.getAttribute("element");
		     	    object_id=(String)abean.get("object_id");	
		     	    a0101=(String)abean.get("a0101");	     		
		     	    mainbodySetList = (ArrayList)abean.get("mainbodySetList");		     		
		     		ArrayList items = (ArrayList)abean.get("items");
		     	    for (int j = 0; j < mainbodySetList.size(); j++)
				    {
				    	LazyDynaBean tempBean = (LazyDynaBean) mainbodySetList.get(j);
						String body_id = (String)tempBean.get("body_id");
						if(body_id.equals("-1"))
							body_id="X1";
						String pcount1 = (String)abean.get("B"+body_id+"_PCount");
						String vcount1 = (String)abean.get("B"+body_id+"_VCount");
		     	%>
		     	 <td  class='RecordRow' align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 					<%= pcount1%>
				 </td>	
				 <td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 					<%= vcount1%>
				 </td>
		 			  <%
		 			  for (Iterator t = items.iterator(); t.hasNext();)
					  {
					    String[] temp = (String[]) t.next();
					    if (temp[1] == null)
					    {
							String theVal = (String)abean.get("B"+body_id+"_I"+temp[0]);		 			  		
		 			  %>
		 		 <td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 					<%= theVal%>
				 </td>
				 	  <%}
		 			 }
		 		   }%>
		 		<td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 				<bean:write name='element' property='original_score' filter='true' />
				</td>
		 	    <td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 				<bean:write name='element' property='score' filter='true' />
				</td>				 			  
		 		<td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 				<bean:write name='element' property='resultdesc' filter='true' />
				</td>		  
		 		<td  class='RecordRow'  align='center'   nowrap width='<bean:write name='element' property='columnWidth' filter='true' />'>
		 				<bean:write name='element' property='exX_object' filter='true' />
				</td>	
			</tr>  
		</logic:equal>
		
	</hrms:extenditerate>
</table>
		<%-- tbody第一行最后一个单元格与表头最后一个单元格边框重合 lium --%>
		<style type="text/css">
			#tbl-container tbody td {border-top-width: 0px;}
		</style>
		
		<script language='javascript' >
			document.write("</div>");
			document.write("</td></tr></table>");
			<logic:notEqual name="evaluationForm" property="computeFashion"  value="6">
				var topHeight = document.documentElement.clientHeight || document.body.clientHeight;
				document.write("<div id='pagebar' style='font-size:12px;border:1px solid #C4D8EE;position:absolute;top:"+(topHeight-<%=top %>)+"px;width:99%'  >");
			</logic:notEqual>
		</script>

		<logic:notEqual name="evaluationForm" property="computeFashion"  value="6">
		<table width="100%" align="center" style="position: relative">
			<tr>
				<td valign="bottom" align="left" class="tdFontcolor">
					第
					<bean:write name="evaluationForm"
						property="setlistform.pagination.current" filter="true" />
					页 共
					<bean:write name="evaluationForm"
						property="setlistform.pagination.count" filter="true" />
					条 共
					<bean:write name="evaluationForm"
						property="setlistform.pagination.pages" filter="true" />
					页
				</td>
				<td align="right" nowrap class="tdFontcolor">
					<p align="right">
						<hrms:paginationlink name="evaluationForm"
							property="setlistform.pagination" nameId="setlistform"
							propertyId="roleListProperty">
						</hrms:paginationlink>
				</td>
			</tr>
		</table>
		</logic:notEqual>
     </div>
<!-- <input type='hidden' name='plan_scope'  value="${evaluationForm.plan_scope}" /> -->

<html:hidden name="evaluationForm" property="isDispAll"/>
<html:hidden name="evaluationForm" property="pointResult"/>
<html:hidden name="evaluationForm" property="showDetails"/>
<div id='wait' style='position:absolute;top:160;position:relative;z-index:50;display:none'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr><!-- 【6297】绩效管理:绩效评估中，文件/发送反馈表的时候缓冲框中简的线和系统主题不统一    jingq upd 2015.01.05-->
             <td id="wait_desc" class="td_tyle complex_border_color" height=24>正在发送考核反馈表，请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center class="complex_border_color">
               <marquee class="marquee_style" direction="right" width="260" scrollamount="5" scrolldelay="10" >
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
<!--  
	<logic:equal name="evaluationForm" property="computeFashion"   value="1">
		<div id='menu_' onblur='hiddenElement()' style="BACKGROUND-COLOR: #F7FAFF;border:1px groove black;width:120;height:80 " >
			<table>
				<tr><td onclick="scoreDetail();"  style="cursor:hand;width:120;" onMouseOver="style.background='#0000EE';style.color='white';" onMouseOut="style.background='#F7FAFF';style.color='black';"><bean:message key="jx.object.scoreDetail"/></td></tr>
			</table>
		</div>
	</logic:equal>
-->
</html:form>
<script>
	var tableVote = document.getElementById("table-vote");
	if(tableVote){
		var theContainer = document.getElementById("tbl-container");
		if(theContainer.clientWidth>tableVote.width)
			tableVote.style.width=(theContainer.clientWidth-1)+"px";
	}
    function resizeWindowRefrsh(){
        var theHeight=document.documentElement.clientHeight||document.body.clientHeight;
        document.getElementById("tbl-container").style.height=(theHeight-125)+'px';
        document.getElementById("pagebar").style.top=(theHeight-<%=top %>)+"px";
    }
	///closeMenu();右键弹出菜单，则必须要该行代码
	if( document.getElementById('tbl')){
		var table = document.getElementById('tbl');
		if(table.rows.length>1)
		{
			for(var i=1;i<table.rows.length;i++)
			{	
				if(table.rows[i].cells.length>0 && ltrim(rtrim(table.rows[i].cells[0].innerText))=='1') 
				{
					//table.rows[i].fireEvent("onClick");
					table.rows[i].onclick();
					break;
				}
			}		
		}
	}
</script>

</body>
</html>