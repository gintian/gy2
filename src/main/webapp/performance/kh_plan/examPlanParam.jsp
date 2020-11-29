<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient" %>
<script language="javascript" src="/js/common.js"></script>
<script language="javascript" src="/performance/kh_plan/examPlanParam.js"></script>
<script LANGUAGE=javascript src="/js/function.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<STYLE type=text/css>


.div2
{
 	width: 478px;
 	line-height:15px; 
 	border-width:1px; 
 	border-style: groove;
 	border-width :thin ;
 
 	border: inset 1px #C4D8EE;
 	BORDER-BOTTOM: #C4D8EE 1pt solid; 
 	BORDER-LEFT: #C4D8EE 1pt solid; 
 	BORDER-RIGHT: #C4D8EE 1pt solid; 
 	BORDER-TOP: #C4D8EE 1pt solid; 
}
</STYLE>

<script>
	<%
		String str= request.getParameter("method");
  		EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
  		boolean methodFlag = false;
    	if(lockclient!=null)
   		 {
       		 if(lockclient.isHaveBM(29)){
            	methodFlag=true;
        	 }
    	}
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		int versionFlag = 1;
		if (userView != null)
			versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版		

		if(request.getParameter("b_saveparam")!=null){
	%>
		window.returnValue = {
			byModel : '${examPlanForm.byModel}',
			// 用于将最终的必填指标填充到计划明细页 modify by 刘蒙
			requiredFieldStr : "${examPlanForm.requiredFieldStr }"
		};	
		closewindow();
	<%} %>
	var planId = '${param.plan_id}';
	var theMethod = '${param.method}';// 1 360考核  2目标考核
	var theStatus='${param.status}';
	var theGatherType='${param.gather_type}'; //数据采集类型 0 网上 1机读 2 网上+机读
	var theObjectType='${param.object_type}';
	var theTemplateId='${param.templateId}';
	var busitype='${examPlanForm.busitype}';
	var sameAllScoreValue='${examPlanForm.sameAllScoreNumLess}';
	var setid='${examPlanForm.setid}';
	function gradeChange(obj){
	  if(obj.checked)
	     showElement('asag');
	  else
	     hideElement('asag');
	}

/**author:zangxj *day:2014-06-07 *打开绩效模板文件控制窗口   */
function  _showAffixWindows()
{			
	var target_url='/selfservice/performance/selfGrade.do?b_query3=link`plan_id='+planId;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);


    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "newwindow",
            "dialogWidth:640px; dialogHeight:190px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var config = {
            width:640,
            height:190,
            type:'2',
            id:'newwindow'
        };
        modalDialog.showModalDialogs(iframe_url,'newwindow',config);
    }
//			window.open ('/selfservice/performance/selfGrade.do?b_query3=link&plan_id='+planId,'newwindow','height=190,width=550,top=400,left=500,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=yes')
}
    function closewindow()
    {
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.open("about:blank","_top").close();
        }
    }
	function IsDigit(obj) 
	{
		return ((event.keyCode >= 46) && (event.keyCode <= 57)); 

	}

//检验数字类型
	function checkValue(obj)
	{
	  	if(obj.value.length>0)
	  	{
	  		if(!checkIsNum2(obj.value))
	  		{
	  			alert('请输入数值！');
	  			obj.value='';
	  			obj.focus();
	  		}
	  	} 
	}
	

	function changeBlind()
	{
		var eva=document.getElementsByName("eva");
		var obj = document.getElementsByName("obj");
		document.getElementById("blind_").style.display="none";
		if(busitype!=null && busitype=="0")
			document.getElementById("blind_").style.display="none";
		if(eva)
		{
			for(var i=0;i<eva.length;i++)
			{
			   if(eva[i].checked && eva[i].value==7)
			   {
			      document.getElementById("blind_").style.display="";
			      break;
			   }
			}
		}
		if(obj&&busitype!=null && busitype=="0")
		{
			for(var i=0;i<obj.length;i++)
			{
			   if(obj[i].checked && obj[i].value==7)
			   {
			      document.getElementById("blind_").style.display="";
			      break;
			   }
			}
		}
		
	}
	
	function showbutton(theObj){
		if(theObj.checked==true){
			showElement("dutyRuleidSpan");
		}
		else{
			hideElement('dutyRuleidSpan');
		}
	}
	function showDutyRule(){
		var express;
		if(document.getElementById("dutyRule")){
			express = document.getElementById("dutyRule").value;
		}
		
		var setdesc='${examPlanForm.setdesc}';
		
		   var arguments=new Array(express);     
		   var strurl="/general/query/common/select_query_fields.do?b_init=link`type=1`setdesc="+setdesc+"`setid="+setid+"`show_dbpre=Usr`query_type=1`queryflag=1";
		   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		   var dw=700,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;



        if(window.showModalDialog){
            var strExpression = window.showModalDialog(iframe_url,arguments,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=610px;dialogHeight=460px;resizable=no;scroll:no;status=no;");
            express = strExpression;
            showDutyRule_window_ok(strExpression,express);
        }else{

            var win = Ext.create('Ext.window.Window',{
                id:'simple_query',
                title:'选择角色',
                width:610,
                height:460,
                arguments:arguments,
                resizable:'no',
                modal:true,
                autoScoll:false,
                autoShow:true,
                autoDestroy:true,
                html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
                renderTo:Ext.getBody(),
                listeners:{
                    'close':function(){
                        if(this.strExpression){
                            express = this.strExpression;
                        }
                        showDutyRule_window_ok(this.strExpression,express);
                    }
                }
            });
        }


	}
	
	function showDutyRule_window_ok(strExpression,express) {
        if(strExpression)
        {
            document.getElementById("dutyRule").value=strExpression;
        }
        if(!express){
            document.getElementById("dutyRuleid").checked = false;
            hideElement('dutyRuleidSpan');
        }
    }
</script>

<html:form action="/performance/kh_plan/kh_params">
	<%-- 必填评分说明的时候需要知道计划的类型和模板类型 add by 刘蒙 --%>
	<input type="hidden" id="planMethod" value="${param["method"] }" />
	<input type="hidden" id="planTpl" value="${param["templateId"] }" />
	
	<html:hidden name="examPlanForm" property="busitype" styleId="busitype" />
	<html:hidden name="examPlanForm" property="fineMax" styleId="fineMax" />
	<html:hidden name="examPlanForm" property="badlyMax" styleId="badlyMax" />
	<html:hidden name="examPlanForm" property="perSetShowMode" styleId="perSetShowMode" />
	<html:hidden name="examPlanForm" property="isBrowse" styleId="isBrowse" />
	<html:hidden name="examPlanForm" property="paramOper" styleId="paramOper" />
	<html:hidden name="examPlanForm" property="bodyTypeIds" styleId="bodyTypeIds" />
	<html:hidden name="examPlanForm" property="sameScoreNumLessValue" styleId="sameScoreNumLessValue" />
	<html:hidden name="examPlanForm" property="totalAppFormula" styleId="totalAppFormula" />
	<html:hidden name="examPlanForm" property="evaluate_str"  styleId="evaluate_str"/>
	<html:hidden name="examPlanForm" property="dutyRule"  styleId="dutyRule"/>
	<table width="620px" height="630px" border="0" cellspacing="0" style="margin-top:-5px;"
		cellpadding="5" align="center">
		<tr><!-- 【6462】绩效管理：点击计划中的参数，查看考核主体类别，弹出框出现双滚动条   jingq upd 2015.01.05 -->
			<td width="640px" valign="top" align="center">
				<hrms:tabset name="kh_param" width="580" height="620" type="false">
				
				
					<hrms:tab name="param1" label="考核主体类别" visible="true">
					<br>
						<table width="475px" border="0" cellspacing="0" align="center"
								cellpadding="0" id="bodysFromCards">								
								<tr>
									<td align="center"  nowrap width="12%" style="border-top:0px;">
										<html:checkbox styleId="bodysFromCard"
												onclick="setBodysFromCard()" name="examPlanForm"
												property="bodysFromCard" value="1" />																						
										</td>
										<td align="left"  nowrap style="border-top:0px;">
											<bean:message key='jx.khplan.bodysFromCard'/>
									</td>
								</tr>								
						</table>
						<div class="div2 common_border_color">
							<table width="100%" border="0"  align="center"
								cellpadding="0" class="ListTable" id="select">
								<%
								if (request.getParameter("method").equals("2")){
								 %>
								 	<thead>
      							 	 <tr>
      							 	 <td align="center" class="TableRow_right common_background_color common_border_color" nowrap style="border-top:0px;">&nbsp;</td>
        							 <td align="center" class="TableRow" nowrap style="border-top:0px;">主体类别</td>
        							 <td align="center" class="TableRow" nowrap colspan="2" style="border-top:0px;">参与评分过程</td>
        							 <td align="center" class="TableRow_left common_background_color common_border_color" nowrap style="border-top:0px;">评分顺序</td>
								 	</tr>
								 	</thead>
								 <% } %>
								
								
								<logic:iterate id="element" name="examPlanForm"
									property="mainbodytypeList">
									<%
									if (request.getParameter("method").equals("2"))
									{
								 	%>
								 	<tr>
								 		<td align="center" class="RecordRow_right common_border_color" nowrap width="12%">
											<input name="bodyId" type="checkbox"
												id="<bean:write name="element" property="name" filter="true" />"  onclick='selectBody(this)'
												value="<bean:write name="element" property="body_id" filter="true" />"
												<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
											<input type="hidden" name="level" value='<bean:write name="element" property="level" filter="true" />'>
										</td>
										<td align="left" class="RecordRow" nowrap>
											<bean:write name="element" property="name" filter="true" />
										</td>
										<td align="center" class="RecordRow" nowrap>
											<input name="isGrade"  id='grade_<bean:write name="element" property="body_id" filter="true" />'  onclick='setGradeValue(this)' type="checkbox" value="0"
												<logic:equal name="element" property="isgrade" value="0">checked</logic:equal> 
												<logic:equal name="element" property="selected" value="0">disabled</logic:equal>
											/>										 
										</td>
										
										<%-- 打分确认标识：0或空→打分（默认）、1→确认 by 刘蒙 --%>
										<td align="center" class="RecordRow" nowrap style="text-align:left;">
											<input type="hidden" name="opt_${element.map.body_id }"
												value='<bean:write name="element" property="opt_${element.map.body_id }" filter="true" />' />

											<%
												LazyDynaBean bean = (LazyDynaBean) pageContext.getAttribute("element");
												boolean isDisabled = false;
												if (bean.get("isgrade").equals("1") || bean.get("selected").equals("0")) {
													isDisabled = true;
												}
											%>
											<div style="border-bottom:1pt solid #C4D8EE;" class="common_border_color">
												<html:radio name="element" property="opt_${element.map.body_id }"
													value="0" disabled="<%=isDisabled %>"
													onclick="document.getElementsByName('opt_${element.map.body_id }')[0].value=this.value;" /> 打分
											</div>
											<div>
												<html:radio name="element" property="opt_${element.map.body_id }"
													value="1" disabled="<%=isDisabled %>"
													onclick="document.getElementsByName('opt_${element.map.body_id }')[0].value=this.value;" /> 确认
											</div>
										</td>
										
										<td align="center" class="RecordRow_left common_border_color" nowrap>
											<input name="gradeseq" type="text" id='seq_<bean:write name="element" property="body_id" filter="true" />'   
												   size=5 value="<bean:write name="element" property="grade_seq" filter="true"   />"   
												   <logic:equal name="element" property="selected"
													value="0">disabled</logic:equal>
												   onchange="valiSeqData(this)" />
											 
										</td>
								 	</tr>
								 	<% }else{ %>
									<tr>
										<td align="center" class="RecordRow_right" nowrap width="12%" style="border-top:0px;">
											<input name="bodyId" type="checkbox"
												id="<bean:write name="element" property="name" filter="true" />"
												value="<bean:write name="element" property="body_id" filter="true" />"
												<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
											<input type="hidden" name="level" value='<bean:write name="element" property="level" filter="true" />'>
										</td>
										<td align="left" class="RecordRow_left" nowrap style="border-top:0px;">
											<bean:write name="element" property="name" filter="true" />
										</td>
									</tr>
									<% } %>
								</logic:iterate>
							</table>
						</div>
						
						<hrms:priv func_id="3260601">
							<table width="100%">
								<tr>
									<td  align="center">
										<input type="button" id="bodyDefine"
											value="<bean:message key='jx.khplan.khmainbody_type_define'/>"
											onclick="bodyTypeDef();" Class="mybutton">
									</td>
								</tr>
							</table>
						</hrms:priv>
					</hrms:tab>
					
					
					<%
						if (request.getParameter("method").equals("1"))
						{
					%>
					<hrms:tab name="param2" label="打分控制" visible="true">
						<table width="90%" border="0" cellspacing="1" cellpadding="2"
							align="center">
							<tr>
								<td colspan="2" height="5">

								</td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title1" />
										</legend>
										<table width="100%" border="0" align="center">
											<tr>
												<td>
													<html:radio styleId="dataGatherMode" name="examPlanForm" property="dataGatherMode"
														value="1"
														onclick="hideElement('datepnl');hideElement('datepn3');showElement('datepn2');checkEval();setResult();setAllowResult();setHide();showORhide();" />
													<bean:message key='jx.khplan.param1.bd' />
												</td>
												<td>
													<table id="datepn2">
														<tr>
															<td>
																<bean:message key='jx.khplan.param1.degreeShowType' /> 
															</td>
															<td>
																<html:radio name="examPlanForm" property="degreeShowType" value="1" />
																	<bean:message key='jx.khplan.param1.degreeShowType1' /> <html:radio
																		name="examPlanForm" property="degreeShowType" value="2" />
																		
																	<logic:notEqual name="examPlanForm" property="busitype" value="1">
																		<bean:message key='jx.khplan.param1.degreeShowType2' /> 
																	</logic:notEqual>																	
																	<logic:equal name="examPlanForm" property="busitype" value="1">	
																		<bean:message key="jx.khplan.param1.evaluateShowType2" />
																	</logic:equal>																	
															</td>
														</tr>
														<tr>
															<td>																	
															</td>
															<td>
																<html:radio name="examPlanForm" property="degreeShowType" value="3"/>
																<logic:notEqual name="examPlanForm" property="busitype" value="1">
																	<bean:message key='jx.khplan.param1.degreeShowType3' />  
																</logic:notEqual>																	
																<logic:equal name="examPlanForm" property="busitype" value="1">	
																	<bean:message key='jx.khplan.param1.evaluateShowType3' /> 
																</logic:equal>
																
															</td>
														</tr>															
													</table>																				
												</td>
											</tr>
											<tr>
												<td>
													<html:radio name="examPlanForm" property="dataGatherMode"
														value="2"
														onclick="showElement('datepnl');hideElement('datepn3');hideElement('datepn2');checkEval();setResult();setHide();showORhide();" />
													<bean:message key='jx.khplan.param1.mix' />
												</td>
												<td>
													&nbsp;
													<span id="datepnl"> <bean:message
															key='jx.khplan.param1.fzzbdgz' /> <html:radio
															name="examPlanForm" property="scaleToDegreeRule"
															value="1" /> <bean:message key='jx.khplan.param1.high' />
														<html:radio name="examPlanForm"
															property="scaleToDegreeRule" value="2" /> <bean:message
															key='jx.khplan.param1.low' /> </span>
												</td>
											</tr>
											
											<logic:equal name="examPlanForm" property="busitype" value="0">
											<tr>
												<td>
													<html:radio styleId="dataGatherModes" name="examPlanForm" property="dataGatherMode" value="4"
														onclick="hideElement('datepnl');showElement('datepn3');hideElement('datepn2');checkEval();setResult();setHide();showORhide();" />
														<bean:message key='plan.param.dataGatherMode3' />
												</td>
												<td>											
													<span id="datepn3"> 
														<html:radio name="examPlanForm" property="addSubtractType" value="1" /> 
															<bean:message key='plan.param.addSubtractType1' />
														<html:radio name="examPlanForm" property="addSubtractType" value="2" /> 
															<bean:message key='plan.param.addSubtractType2' /> 
														<html:radio name="examPlanForm" property="addSubtractType" value="3" /> 
															<bean:message key='plan.param.addSubtractType3' />
													</span>
												</td>
											</tr>
											</logic:equal>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td>
									<fieldset style="width:100%;padding:0 0 0 4px;" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title2" />
											<html:checkbox styleId="fineRestrict" name="examPlanForm"
												property="fineRestrict" value="1"
												onclick="selectRestrict('high');" />

										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="70%">
													<input type="radio" name="nohigh" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title21' />
												</td>
												<td width="30%">
													<input type="text" id="bili1" size="2" disabled
														onblur="checkValue(this.id)">
													%
												</td>
											</tr>
											<tr>
												<td>
													<input type="radio" name="nohigh" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title22' />
												</td>
												<td>
													<input type="text" id="value1" size="2" disabled
														onblur="checkValue(this.id)">
												</td>
											</tr>
											<tr>
												<td>
													<input type="radio" name="nohigh" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title23' />
													:
												</td>
												<td>
													<Input type="button" value="..." id="button1"
														class="mybutton"
														onclick='partRescrit("${param.templateId}","fine");'
														disabled />
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
								<td>
									<fieldset style="width:100%;padding:0 0 0 4px;margin-left:16px" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title3" />
											<html:checkbox styleId="badlyRestrict" name="examPlanForm"
												property="badlyRestrict" value="1"
												onclick="selectRestrict('low');" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="70%">
													<input type="radio" name="nolow" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title21' />
												</td>
												<td width="30%">
													<input type="text" id="bili2" size="2" disabled
														onblur="checkValue(this.id)">
													%
												</td>
											</tr>
											<tr>
												<td>
													<input type="radio" name="nolow" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title22' />
												</td>
												<td>
													<input type="text" id="value2" size="2" disabled
														onblur="checkValue(this.id)">
												</td>
											</tr>
											<tr>
												<td>
													<input type="radio" name="nolow" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title23' />
													:
												</td>
												<td>
													<Input type="button" value="..." id="button2"
														class="mybutton"
														onclick='partRescrit("${param.templateId}","badly")'
														disabled />
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title4" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="30%">
													<html:radio name="examPlanForm"
														property="sameResultsOption" value="1" onclick="radioSelect2()"/>
													<bean:message key='jx.khplan.param1.title41' />
												</td>
												<td width="30%">
													<html:radio name="examPlanForm"
														property="sameResultsOption" value="2" onclick="radioSelect2()"/>
													<bean:message key='jx.khplan.param1.title42' />
												</td>
												<td width="40%">
													<html:radio name="examPlanForm"
														property="sameResultsOption" value="3" onclick="radioSelect2();if(this.checked) {sameResultDefine();}"/>
														&nbsp;<bean:message key="kq.duration.define"/>
													<input type='button' class="mybutton" property="b_cancel"
														onclick='sameResultDefine();' id="sameResultDef_bt" 
														value='...' />
													<html:hidden name="examPlanForm" property="noCanSaveDegrees" styleId="noCanSaveDegrees"/>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr id="totalScoreObjNumid">
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.totalScoreObjNum" />																							
											<html:checkbox styleId="sameAllScoreNumLess" name="examPlanForm"
												property="sameAllScoreNumLess" value="1"
												onclick="selectScoreNum_Less();" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="18%">
													<input type="radio" name="noScoreObj" disabled="disabled"
														onclick="radioSelectScoreNum()">
													<bean:message key='jx.khplan.param1.totalScoreObjNum1' />
												</td>
												<td>
													<input type="text" id="biliObj" value="100" size="4" disabled
														onblur="checkValue(this.id)">
													%
												</td>											
												<td width="18%">
													<input type="radio" name="noScoreObj" disabled="disabled"
														onclick="radioSelectScoreNum()">
													<bean:message key='jx.khplan.param1.totalScoreObjNum2' />
												</td>
												<td>
													<input type="text" id="valueObj" value="2" size="4" disabled
														onblur="checkValue(this.id)">
												</td>
											</tr>											
										</table>
									</fieldset>
								</td>
							</tr>

							<tr id='gather_type_1'>
								<td colspan="2">
									<fieldset style="width:100%" name="blankScoreOptionset">
										<legend>
											<bean:message key="jx.khplan.param1.title5" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="25%">
													<html:radio name="examPlanForm" property="blankScoreOption"
														value="0" onclick="setJw(this)" />
													<bean:message key='jx.khplan.param1.title51' />
												</td>
												<td width="23%">
													<html:radio name="examPlanForm" property="blankScoreOption"
														value="1" onclick="setJw(this)" />
													<bean:message key='jx.khplan.param1.title52' />
												</td>
												<td width="52%">
													<html:radio name="examPlanForm" property="blankScoreOption"
														value="2" onclick="setJw(this)" />
													<bean:message key='jx.khplan.param1.jw' />
													<html:select name="examPlanForm"
														property="blankScoreUseDegree" size="1" style="width:100px"
														styleId="blankScoreUseDegree">
														<html:optionsCollection property="grade_template"
															value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title13" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td id="muleval" width="25%">
													<html:radio name="examPlanForm" property="mailTogoLink"
														value="1" onclick="setJw(this)" />
													<bean:message key="performance.param.muleval" />
												</td>
												<td width="23%">
													<html:radio name="examPlanForm" property="mailTogoLink" styleId="singleRadio"
														value="2" onclick="setJw(this)" />
													<bean:message key="performance.param.singleleval" />
												</td>
												<td width="52%">
													<html:radio name="examPlanForm" property="mailTogoLink" styleId="nullRadio"
														value="3" onclick="setJw(this)" />
													<bean:message key="null.label" />
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>		
							<logic:equal name="examPlanForm" property="busitype" value="0">
							<tr id="pgxx">
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="performance.param.evaloption" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">	
											<tr id="keyEventCanNewPoint">
												<td>										
													<html:checkbox styleId="keyEventEnabled" name="examPlanForm"
														property="keyEventEnabled" value="1" />
													<bean:message key="jx.khplan.param2.title30" />													
												</td>
											</tr>																					
											<tr id="allowAdjustEvalResults">
												<td>												
												<% if(versionFlag==1){%>
													<html:checkbox styleId="allowAdjustEvalResult" name="examPlanForm"
														property="allowAdjustEvalResult" onclick='setAllowResult();checkEval();' value="1" />
													<bean:message key="jx.khplan.param2.allowresult" />
													&nbsp;&nbsp;&nbsp;&nbsp;													
													<bean:message key="jx.khplan.param2.evalrange" />:													
													<html:radio styleId="adjustEvalRange" name="examPlanForm" property="adjustEvalRange"
														value="0" onclick="setJw(this);checkEval();" />
													<bean:message key='menu.field' />												
													<html:radio styleId="adjustEvalRange1" name="examPlanForm" property="adjustEvalRange"
														value="1" onclick="setJw(this);checkEval();" />
													<bean:message key='label.zp_exam.sum_score' />
												<% }%>														
												</td>
											</tr>
											<tr>
												<td>
													<table id="adjustEvals">
														<tr>
															<td>
																&nbsp;&nbsp;&nbsp;&nbsp;
																<bean:message key="jx.khplan.param2.adjustType" />:													
																<html:radio styleId="adjustEvalDegreeType" name="examPlanForm" property="adjustEvalDegreeType"
																	value="0" onclick="setJw(this);checkType();" />
																<bean:message key='jx.khplan.param2.adjustType1' />												
																<html:radio styleId="adjustEvalDegreeType1" name="examPlanForm" property="adjustEvalDegreeType"
																	value="1" onclick="setJw(this);checkType();" />
																<bean:message key='jx.khplan.param2.adjustType2' />
															</td>
															<td>
																&nbsp;&nbsp;
																<bean:message key="jx.khplan.param2.adjustType3" />:	
																<input type="text" id="adjustEvalDegreeNum" name="adjustEvalDegreeNum"
															 onkeypress="event.returnValue=IsDigit(this);" value="<bean:write  name="examPlanForm" property="adjustEvalDegreeNum"/>" size='3' />
															</td>
															<td>
																<table border="0" cellspacing="2" cellpadding="0" id="adjustEvalDegreeNums">
																	<tr><td><button id="m_up" type="button" class="m_arrow" onclick="mincreasep('adjustEvalDegreeNum')">5</button></td></tr>
																	<tr><td><button id="m_down" type="button" class="m_arrow" onclick="msubtractp('adjustEvalDegreeNum')">6</button></td></tr>
																</table>
															</td>
															<td>	
																<input onkeyup="if(isNaN(value))execCommand('undo')" onafterpaste="if(isNaN(value))execCommand('undo')"
															 	type="text" id="adjustEvalGradeStep" name="adjustEvalGradeStep" value="<bean:write  name="examPlanForm" property="adjustEvalGradeStep"/>" size='3' />
															</td>
														</tr>
													</table>																									
												</td>
											</tr>
											<tr id="calcMenScoreRefDepts">
												<td>
												<% if(versionFlag==1){%>
													<html:checkbox styleId="calcMenScoreRefDept" name="examPlanForm"
														property="calcMenScoreRefDept" value="1" />
													<bean:message key="jx.khplan.param2.calcMenScoreRefDept" />													
													&nbsp;	
													<input type="button" value="部门模板..." id="b_menRefDeptTmpl" class="mybutton" onclick='showMenRefDept("${param.plan_id}","${param.status}","${param.templateId}");' />											
												<% }%>	
												</td>																																					
											</tr>
											<tr>	
												<td nowrap id="scoreFromGrpOrders" >															
													<span id="scoreFromItems" nowrap>
													<% if(versionFlag==1){%>
														<html:checkbox styleId="scoreFromItem" name="examPlanForm"
															property="scoreFromItem" value="1" />
															<bean:message key="jx.khplan.param2.scoreFromItem" />
															&nbsp;
													<% }%>
													</span>
													<span id="showGrpOrders" nowrap>
													<% if(versionFlag==1){%>
														<html:checkbox styleId="showGrpOrder" name="examPlanForm"
															property="showGrpOrder" value="1" />
														<bean:message key="jx.khplan.param2.title12" />
													<% }%>	
													</span>
												</td>																									
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							</logic:equal>
							
							<logic:equal name="examPlanForm" property="busitype" value="0">
							<%if(request.getParameter("gather_type")!=null && request.getParameter("gather_type").equals("1")) {%>
							<tr id="scoreWays">
								<td colspan="2" style="height:35px">
									<bean:message key='jx.khplan.param1.title6' />
									&nbsp;									
									<html:select name="examPlanForm" property="scoreWay" size="1"
										styleId="scoreWay">								
											<html:option value="0">
												<bean:message key='jx.khplan.param1.title61' />
											</html:option>							
									</html:select>	
									&nbsp;							
									<bean:message key='jx.khplan.param1.title60' />
								</td>
							</tr>
							<%}else{ %>
							<tr>
								<td colspan="2" style="height:35px">
									<bean:message key='jx.khplan.param1.title6' />	
									&nbsp;								
									<html:select name="examPlanForm" property="scoreWay" size="1"
										styleId="scoreWay">								
											<html:option value="0">
												<bean:message key='jx.khplan.param1.title61' />
											</html:option>							
										<html:option value="1">
											<bean:message key='jx.khplan.param1.title62' />
										</html:option>
									</html:select>
									&nbsp;
									<bean:message key='jx.khplan.param1.title60' />
								</td>
							</tr>
							<%} %>
							</logic:equal>
							
						</table>
					</hrms:tab>
					
					
					<%if(request.getParameter("gather_type")!=null && !request.getParameter("gather_type").equals("1")) { %>
					<hrms:tab name="param3" label="自助参数" visible="true">
						<table width="90%" border="0" cellspacing="0" cellpadding="1"
							align="center" valign="center" class=menu_table>
							<tr>
								<td height="5">

								</td>
							</tr>
							<tr>
								<td>
									<fieldset name="filterset">
										<legend>
											<bean:message key="performance.param.singleselfeval" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr >
												<td nowrap>
													<html:checkbox styleId="isShowSubmittedScores"
														name="examPlanForm" property="isShowSubmittedScores"
														value="1" />
													<bean:message key="jx.khplan.param2.title9" /><!-- 显示提交后的分数 -->
												</td>
												<td  nowrap> 
													<html:checkbox styleId="showDeductionCause"
														name="examPlanForm" property="showDeductionCause" onclick='setMustFillCause();' 
														value="1" />
													<bean:message key="jx.khplan.param2.showDeductionCause" /><!-- 启用评分说明	 -->
													<span id="ScoreIntroductuios">									
														<html:checkbox styleId="mustFillCause"
															name="examPlanForm" property="mustFillCause" onclick='setMustWriteButton();'
															value="1" />
														<bean:message key="jx.parameter.mustFillCause" /><!--必填  -->
													<span>	
														<Input type="button" id="mustWriteButton" value="..."
															   class="mybutton" onclick="mustWriteScore();" />
													<html:hidden name="examPlanForm" property="upIsValid" styleId="upIsValid"/>
													<html:hidden name="examPlanForm" property="downIsValid" styleId="downIsValid"/>
													<html:hidden name="examPlanForm" property="upDegreeId" styleId="upDegreeId"/>
													<html:hidden name="examPlanForm" property="downDegreeId" styleId="downDegreeId"/>
													<html:hidden name="examPlanForm" property="excludeDegree" styleId="excludeDegree"/>
													<html:hidden name="examPlanForm" property="requiredFieldStr" styleId="requiredFieldStr"/>
												</td>
											</tr>	
											
											<logic:equal name="examPlanForm" property="busitype" value="0">	
											<% if(versionFlag==1){%>
												<tr>
													<td>
														<html:checkbox styleId="showIndicatorContent"
															name="examPlanForm" property="showIndicatorContent"
															value="1" />
														<bean:message key="jx.param.showIndicatorContent" />
													</td>
													<td>
														<html:checkbox styleId="showIndicatorRole"
															name="examPlanForm" property="showIndicatorRole" value="1" />
														<bean:message key="jx.param.showIndicatorRole" />
													</td>
												</tr>
											<% } %>	
											</logic:equal>
											
											<tr>
												<td>
													<html:checkbox styleId="showIndicatorDegree"
														name="examPlanForm" property="showIndicatorDegree"
														value="1" />													
													<logic:notEqual name="examPlanForm" property="busitype" value="1">
														<bean:message key="jx.param.showIndicatorDegree" />
													</logic:notEqual>																	
													<logic:equal name="examPlanForm" property="busitype" value="1">	
														<bean:message key="jx.param.showComputcyModelFieldDegree" />
													</logic:equal>
												</td>
												<td>
													<html:checkbox styleId="selfEvalNotScore"
														name="examPlanForm" property="selfEvalNotScore" value="1" />
													<bean:message key="jx.khplan.param2.title33" />
												</td>
											</tr>
											<tr>
												<td>
													<html:checkbox styleId="allowSeeLowerGrade" name="examPlanForm"
															property="allowSeeLowerGrade" value="1" />
													<bean:message key="jx.khplan.param.AllowSeeLowerGrade" />
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td>
									<fieldset name="filterset">
										<legend>
											<span id="bymodel3"><bean:message key="performance.param.singlemuleval" /></span><span id="bymodel4"><bean:message key="performance.param.singleleval" /></span>
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td>
													<html:checkbox styleId="mitiScoreMergeSelfEval"
														name="examPlanForm" property="mitiScoreMergeSelfEval"
														value="1" />
													<bean:message key="jx.khplan.param2.title31" /><!-- 显示自我评价 -->
												</td>
												
												<td>
												</td>
											</tr>	
											<tr>
												<td colspan="2">										
													&nbsp;<html:select name="examPlanForm"
															property="selfScoreInDirectLeader" size="1"
															styleId="selfScoreInDirectLeader">
														<html:option value="0">
															<bean:message key='jx.khplan.param2.title23' />
														</html:option>
														<html:option value="1">
															<bean:message key='jx.khplan.param2.title24' />
														</html:option>
														<html:option value="2">
															<bean:message key='jx.khplan.param2.title25' />
														</html:option>
														<html:option value="3">
															<bean:message key='jx.khplan.param2.title26' />
														</html:option>
													</html:select>
													<bean:message key="jx.khplan.param2.title22" />
												</td>
											</tr>											
											<tr id="pointEvalTypes">
											<% if(versionFlag==1){%>
												<td colspan="2" style="height:30px"> 
													<logic:equal name="examPlanForm" property="busitype" value="0">
													&nbsp;<bean:message key="jx.khplan.param3.pointEvalType" /><!-- 打分时指标标度显示方式 -->
													</logic:equal>
													<logic:equal name="examPlanForm" property="busitype" value="1">
													&nbsp;<bean:message key="jx.khplan.param3.pointEvalTypenl" />
													</logic:equal>
													<html:select name="examPlanForm" property="pointEvalType" size="1" onchange="showRadioSpan()">
														<html:option value="0">
															<bean:message key="jx.khplan.param3.pointEvalType0" />
														</html:option>
														<html:option value="1">
															<bean:message key="jx.khplan.param3.pointEvalType1" />
														</html:option>
													</html:select>
													<span id="radiospan" >
													 &nbsp;排列方式
													 <html:select name="examPlanForm" property="radioDirection" size="1">
														<html:option value="0">
															纵向
														</html:option>
														<html:option value="1">
															横向
														</html:option>
													</html:select>
													</span>
												</td>
											<% } %>	
											</tr>
										</table>										
									</fieldset>
								</td>
							</tr>
							<tr>
								<td id="mulevalFieldset">
									<fieldset name="filterset">
										<legend>
											<bean:message key="performance.param.muleval" />
										</legend>
										<table border="0">
											<tr>
												<td>
													<input type="checkbox" id="num" onClick="selNum();" />
													<bean:message key="jx.khplan.param2.title11" /><!-- 每页人数 -->
												</td>
												<td align="left">
													<table border="0" cellspacing="0" align="center"
														valign="bottom" cellpadding="0">
														<tr>
															<td>
																<div class="m_frameborder">
																	<html:text name="examPlanForm" styleClass="m_input"
																		property="scoreNumPerPage" size="2"
																		onkeypress="event.returnValue=IsDigit2(this);"
																		disabled="true" onblur="testNum(this)" />
																</div>
															</td>
															<td>																
																<table border="0" cellspacing="2" cellpadding="0">
															       <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="mincrease('scoreNumPerPage',100);">5</button></td></tr>
															       <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="msubtract('scoreNumPerPage',1);">6</button></td></tr>
															    </table>							
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<% if(versionFlag==1){%>
												<logic:equal name="examPlanForm" property="busitype" value="0">	
												<td colspan="2">
												<html:checkbox styleId="mutiScoreGradeCtl"
														onclick="selMult(this)" name="examPlanForm"
														property="mutiScoreGradeCtl" value="1" />
													<bean:message key="jx.khplan.param2.title27" /><!-- 强制分布百分比基数 -->
													<html:select name="examPlanForm" property="checkGradeRange" style="width:120px"
														size="1" styleId="checkGradeRange">
														<html:option value="0">
															<bean:message key='jx.khplan.param2.title28' />
														</html:option>
														<html:option value="1">
															<bean:message key='jx.khplan.param2.title29' />
														</html:option>
													</html:select>
													<html:hidden name="examPlanForm" property="mainbodybodyid" styleId="mainbodybodyid"/>
													<html:hidden name="examPlanForm" property="allmainbodybody" styleId="allmainbodybody"/>
													<span id="MainbodyGradeCtlSpan">
													<input type="button" value="..."
														id="MainbodyGradeCtl" class="mybutton" onclick="mainbodyGradeCtl();" />
													</span>
												</td>
												</logic:equal>
												<% }%>
												
											</tr>
											
											<tr>
												<td colspan="2">
													<html:checkbox styleId="canSaveAllObjsScoreSame"
														 name="examPlanForm"
														property="canSaveAllObjsScoreSame" value="1" />
													<bean:message key="performance.plan.param.CanSaveAllObjsScoreSame" />					
													
												</td>
											</tr>
											<tr><!-- 等级不同分数相同不能提交 -->
												<td >
													<html:checkbox styleId="gradeSameNotSubmit"
														 name="examPlanForm"
														property="gradeSameNotSubmit" value="1" />
													<bean:message key="performance.plan.param.gradeSameNotSubmit" />					
												</td>
												<td>
													<html:checkbox styleId="showSumRow"
														 name="examPlanForm"
														property="showSumRow" value="1" />
													<bean:message key="performance.param.ShowSumRow" />					
													
												</td>
                                                <td>
                                             <span id="mutiScoreOnePageOnePoint" >  
                                                    <html:checkbox styleId="mutiScoreOnePageOnePoint"
                                                        name="examPlanForm" property="mutiScoreOnePageOnePoint"
                                                        value="1" />
                                                       <bean:message key="jx.param.danTiDaFen" /><!-- 单题打分 -->
                                            
                                            </span>
                                           </td>
											</tr>
											<tr>  <!-- 将总分和排名放到一起 -->
												<td>
													<html:checkbox styleId="autoCalcTotalScoreAndOrder"
														name="examPlanForm" property="autoCalcTotalScoreAndOrder"
														value="1" />
													<bean:message key="jx.khplan.param2.title13" /><!-- 自动计算总分，排名 -->
												</td>
												<td>
                                                    <html:checkbox styleId="isShowOrder" name="examPlanForm"
                                                        property="isShowOrder" value="1" />
                                                    <bean:message key="jx.khplan.param2.isShowOrder" /><!-- 显示排名 -->
                                                </td>
											</tr>

										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td>
									<fieldset name="filterset">
										<legend>
											<span id="bymodel1"><bean:message key="performance.param.selfsinglemuleval" /></span><span id="bymodel2"><bean:message key="performance.param.selfsingle" /></span>
										</legend>
										<table width="100%" border="0">
											<tr>
												<td colspan="3">
													<html:checkbox styleId="showIndicatorDesc" onclick="showTargetDesc(this)"
														name="examPlanForm" property="showIndicatorDesc" value="1" />
														
													<logic:equal name="examPlanForm" property="busitype" value="0">		
														<bean:message key="jx.khplan.param2.title1" /><!-- 显示考核指标说明 -->
													</logic:equal>
													<logic:equal name="examPlanForm" property="busitype" value="1">	
														<bean:message key="jx.khplan.param2.computcyModel" /><!-- 显示素质指标说明 -->
													</logic:equal>
													
													&nbsp;&nbsp;&nbsp;&nbsp;
													<Input type="button" value="调入..." id="diaoru"
														class="mybutton" onclick="inputFile();" />
													&nbsp;&nbsp;
													<span id='browse'> <Input type="button"
															value="浏览..." id="b_isBrowse" class="mybutton"
															onclick="outputFile();" /> </span>
													
													<logic:equal name="examPlanForm" property="busitype" value="0">		
														<html:checkbox styleId="scoreShowRelatePlan" name="examPlanForm"
															property="scoreShowRelatePlan" value="1" />
														<bean:message key="jx.param.scoreShowRelatePlan" />
													</logic:equal>
												</td>
											</tr>
											<tr>
												<td colspan="3">
													<% if(versionFlag==1){%>
													<logic:equal name="examPlanForm" property="busitype" value="0">
														<html:checkbox styleId="showOneMark" name="examPlanForm"
															property="showOneMark" value="1" />
														<bean:message key="jx.khplan.param2.title2" />
														&nbsp;&nbsp;
													</logic:equal>
													<% }%>
													<html:checkbox styleId="evalOutLimitStdScore" name="examPlanForm"
														property="evalOutLimitStdScore" value="1" />
													<bean:message key="jx.param.evalOutLimitStdScore" />
												</td>
											</tr>											
											<tr>																								
												<logic:equal name="examPlanForm" property="busitype" value="0">
													<td>
														<html:checkbox styleId="noteIdioGoal"
															onclick="setTargetCard(this)" name="examPlanForm"
															property="noteIdioGoal" value="1" />
														<bean:message key="jx.khplan.param2.title32" /><!-- 显示绩效目标 -->
													</td>													
												</logic:equal>	
												<td>
													<html:checkbox styleId="isShowSubmittedPlan"
														name="examPlanForm" property="isShowSubmittedPlan"
														value="1" />
													<bean:message key="jx.khplan.param2.title5" /><!-- 显示提交后计划 -->
												</td>	
												<logic:equal name="examPlanForm" property="busitype" value="0">
												<td>
													<html:checkbox styleId="showHistoryScore"
														name="examPlanForm" property="showHistoryScore"
														value="1" />
													<bean:message key="jx.khplan.param2.historyScore" /><!-- 显示历次得分表 -->
												</td>
												</logic:equal>										
											</tr>
											<logic:equal name="examPlanForm" property="busitype" value="0">
											<tr>
												<td id="show_target"colspan="3">
													<fieldset style="width:90%" name="filterset">
														<legend>
															<bean:message key="jx.khplan.param2.relatingTargetCard" />
														</legend>
														<table width="100%" border="0" align="center" id="relatingTargetCard">
															<tr>
																<td>
																	<html:radio name="examPlanForm" property="relatingTargetCard" value="1" onclick="hideYP();"/>
																	<bean:message key='jx.khplan.param1.noguanlian' />
																</td>																
															</tr>															
															<tr>
																<td>
																	<html:radio name="examPlanForm" property="relatingTargetCard" value="2" styleId="viewtarget" onclick="showYP();"/>
																	<bean:message key='jx.khplan.param1.selectObjTarget' />
																	<span id="ypShow">
																		&nbsp;&nbsp;<html:checkbox property="showYPTargetCard" value="1" />只显示已自评的目标卡
																	</span>
																</td>																
															</tr>															
															<tr>
																<td>
																	<html:radio name="examPlanForm" property="relatingTargetCard" value="3" onclick="hideYP();"/>
																	<bean:message key='jx.khplan.param1.selectselfObjTargetScore' />
																</td>																
															</tr>
														</table>
													</fieldset>
												</td>
											</tr>
											</logic:equal>					
											<tr>
												<td nowrap>
													<html:checkbox styleId="isEntireysub" name="examPlanForm"
														property="isEntireysub" value="1" />
													<bean:message key="performance.param.musteval" /><!-- 提交时必须评价 -->
												</td>
												<td colspan="2" nowrap>
													<html:checkbox styleId="showNoMarking" name="examPlanForm"
														property="showNoMarking" value="1" />
													<bean:message key="jx.khplan.param2.title6" /><!-- 显示不打分原因 -->
												</td>
											</tr>																						
											<tr>  <!-- 将总分和排名放到一起 -->
												<td nowrap>
													<html:checkbox styleId="showTotalScoreSort"
														name="examPlanForm" property="showTotalScoreSort" onclick="importFormula();" value="1" />
													<bean:message key="jx.khplan.param2.title4" /><!-- 显示总分 -->
                                                </td>
                                                <td colspan="2">
                                                	<span id="showImportFormula">
	                                                    <html:checkbox styleId="batchScoreImportFormula" name="examPlanForm"
	                                                        property="batchScoreImportFormula" value="1" />
	                                                    <bean:message key="jx.khplan.param2.batchScoreImportFormula" /><!-- 多人评分是否引入总分计算公式 -->
                                                    </span>
                                                </td>
											</tr>
											<%--<logic:equal name="examPlanForm" property="busitype" value="0">	--%>
											<%--<tr>												--%>
												<%--<td colspan="3">--%>
												<%--<% if(versionFlag==1){%>--%>
													<%--<html:checkbox styleId="showEmployeeRecord" name="examPlanForm"--%>
														<%--property="showEmployeeRecord" value="1" onclick='selectEmpRecordType();' />--%>
													<%--<bean:message key="jx.khplan.param2.showEmployeeRecord" />--%>
													<%--<span id="RecordIntroductuios">									--%>
														<%--<html:checkbox styleId="ShowDay" name="examPlanForm" property="showDay" value="1" />--%>
														<%--<bean:message key="jx.parameter.showDayRecord" /><!--日报  -->--%>
														<%--<html:checkbox styleId="ShowWeek" name="examPlanForm" property="showWeek" value="2" />--%>
														<%--<bean:message key="jx.parameter.showWeekRecord" /><!--周报  -->--%>
														<%--<html:checkbox styleId="ShowMonth" name="examPlanForm" property="showMonth" value="3" />--%>
														<%--<bean:message key="jx.parameter.showMonthRecord" /><!--月报  -->--%>
													<%--<span>--%>
												<%--<% } %>--%>
												<%--</td>--%>
											<%--</tr>--%>
											<%--</logic:equal>--%>
											<logic:equal name="examPlanForm" property="busitype" value="0">
												<tr >
													<td>
														<html:checkbox styleId="idioSummary" name="examPlanForm"
															onclick="setPerforReport(this)" property="idioSummary" value="1" />
														<bean:message key="jx.khplan.param2.title3" />	<!-- 360显示绩效报告 -->		
														<!-- 绩效模板文件上传按钮 author:zangxj  day:2014-06-07 -->											
														<Input type="button" value="..." id="affixUploadFile" class="mybutton" onclick="_showAffixWindows();" />
													</td>
													<td colspan="3" nowrap >
														<table>
															<tr>
															    <td colspan="" id="UploadFile.show" nowrap>
																	<html:checkbox styleId="allowUploadFile" name="examPlanForm"
																		property="allowUploadFile" value="1" />
																		支持附件上传	
																</td>
																<td colspan="" id="jx.show" nowrap>
																	<html:checkbox styleId="scoreBySumup" name="examPlanForm"
																		property="scoreBySumup" value="1" />
																	<bean:message key="jx.khplan.param2.title8" />
				
																</td>
															</tr>
														</table>
													</td>

										
												</tr>
												
												<tr>
													<td colspan="3">
													
														<html:checkbox styleId="showBasicInfo" onclick="messageButton()"
															name="examPlanForm" property="showBasicInfo" value="1" />
														
														<bean:message key="jx.khplan.param3.personMessage" />
														
														<span id="messagetwo">
														<Input type="button" value="..."
																id="message_button" class="mybutton" onclick="message();" />
														</span>
														<html:hidden name="examPlanForm" property="basicInfoItem" styleId="basicInfoItem"/>
														<html:hidden name="examPlanForm" property="lockMGradeColumn" styleId="lockMGradeColumn"/>
														<!--  360计划 不需要 ‘评分时不受考核机构限制’参数 zzk 2014/2/7 
															<html:checkbox styleId="evalOutLimitScoreOrg" name="examPlanForm"
	                                                        property="evalOutLimitScoreOrg" value="1" />
	                                                    <bean:message key="jx.param.evalOutLimitOrganization" />
                                                     -->
													 </td>
											    </tr>
											
												<tr>
													<td colspan="3">
													<% if(versionFlag==1){%>
														<html:checkbox styleId="performanceDate" name="examPlanForm" onclick="showDetail();"
															property="performanceDate" value="1" />
														<%-- 	
														<input type="checkbox" id="jixiao" onclick="showDetail()">
														--%>
														<bean:message key="jx.khplan.param2.title14" /><!-- 显示绩效数据 -->
														<span id="jxsub"> &nbsp;&nbsp;<html:select
																name="examPlanForm" property="perSet" size="1"
																style="width:150px">
																<html:optionsCollection property="itemlist"
																	value="dataValue" label="dataName" />
															</html:select> </span>
													<% } %>
													</td>
												</tr>
												<tr id="jxdata">
													<td>
														&nbsp;<fieldset style="width:100%;padding:0 0 0 4px;" name="dispset">
															<legend>
																<bean:message key="jx.khplan.param2.title15" />
															</legend>
															<table width="100%" border="0" cellpmoding="0"
																cellspacing="0" cellpadding="0" align="center">
																<tr>
																	<td>
																		<input type="checkbox" id="detailItem">
																		<bean:message key='jx.khplan.param2.title16' />
																	</td>
																</tr>
																<tr>
																	<td>
																		<input type="checkbox" id="sumItem">
																		<bean:message key='jx.khplan.param2.title17' />
																	</td>
																</tr>
															</table>
														</fieldset>
													</td>
													<td colspan="2" style="padding-left:10px">
														&nbsp;
														<fieldset style="width:100%;padding:0 0 0 4px;maring-left:16px" name="filterset">
															<legend>
																<bean:message key="jx.khplan.param2.title18" />
															</legend>
															<table width="100%" border="0" cellpmoding="0"
																cellspacing="0" cellpadding="0" align="center">
																<tr>
																	<td>
																		&nbsp;<html:select name="examPlanForm"
																			property="perSetStatMode" size="1"
																			styleId="perSetStatMode" onchange="dispTime();">
																			<html:option value="0">
																				&nbsp;  
																			</html:option>
																			<html:option value="1">
																				<bean:message key='datestyle.year' />
																			</html:option>
																			<html:option value="2">
																				<bean:message key='datestyle.month' />
																			</html:option>
																			<html:option value="3">
																				<bean:message key='kq.wizard.quarter' />
																			</html:option>
																			<html:option value="4">
																				<bean:message key='jx.khplan.halfyear' />
																			</html:option>
																			<html:option value="9">
																				<bean:message key='jx.khplan.timeduan' />
																			</html:option>
																		</html:select>
																	</td>
																	<td>
																		<html:checkbox styleId="statCustomMode"
																			name="examPlanForm" property="statCustomMode"
																			value="1" />
																		<bean:message key='jx.khplan.param2.title19' />
																	</td>
																</tr>
																<tr>
																	<td colspan="2">
																		<span id="timeduan">
																			<bean:message key="label.from" />
																			<input type="text" name="statStartDate" id="editor1"
																				style="width:85px;font-size:9pt;"
																				extra="editor" dropDown="dropDownDate">
																			<bean:message key="label.to" />
																			<input type="text" name="statEndDate" id="editor2"
																				style="width:85px;font-size:9pt;"
																				extra="editor" dropDown="dropDownDate">
																		</span>
																		<script type="text/javascript">
																			<%-- 调整日期选择框高度 modify by 刘蒙 --%>
																				var editObj1 = document.getElementById("editor1");
																				var editObj2 = document.getElementById("editor2");
																				if(editObj1.attachEvent){
																					editObj1.attachEvent("onclick", function () {
	                                                                                    document.getElementById("calendar").style.top = "379px";
	                                                                                });
																					editObj2.attachEvent("onclick", function () {
	                                                                                    document.getElementById("calendar").style.top = "379px";
	                                                                                });
																				}else{
	                                                                            	editObj1.addEventListener("click", function () {
	                                                                                    document.getElementById("calendar").style.top = "379px";
	                                                                                });
	                                                                            	editObj2.addEventListener("click", function () {
	                                                                                    document.getElementById("calendar").style.top = "379px";
	                                                                                });
																				}
																		</script>
																		<html:hidden name="examPlanForm"
																			property="statStartDate" styleId="statStartDateSt" />
																		<html:hidden name="examPlanForm" property="statEndDate"
																			styleId="statEndDateEn" />
																	</td>
																</tr>
															</table>
														</fieldset>
													</td>
												</tr>
											</logic:equal>
																																	
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</hrms:tab>
					
					
					<%
						}
						} else if (request.getParameter("method").equals("2"))
						{
					%>
					<hrms:tab name="param6" label="打分控制" visible="true">
						<table width="90%" border="0" cellspacing="0" cellpadding="1"
							align="center" valign="center" class=menu_table>	
							<tr>
								<td colspan="2" height="5">

								</td>
							</tr>						
							<tr>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title1" />
										</legend>
										<table width="100%" border="0" align="center">
											<tr>
												<td>
													<html:radio styleId="dataGatherMode" name="examPlanForm" property="dataGatherMode"
														value="1"
														onclick="hideElement('datepn3');showElement('datepn2');checkEval();setResult();setAllowResult();setHide();" />
													<bean:message key='jx.khplan.param1.bd' />
												</td>
												<td>
													<table id="datepn2">
														<tr>
															<td>
																<bean:message key='jx.khplan.param1.degreeShowType' /> 
															</td>
															<td>
																<html:radio name="examPlanForm" property="degreeShowType" value="1" />
																	<bean:message key='jx.khplan.param1.degreeShowType1' />																
															</td>
														</tr>
														<tr>
															<td>																	
															</td>
															<td>
																<html:radio name="examPlanForm" property="degreeShowType" value="3"/>
																<logic:notEqual name="examPlanForm" property="busitype" value="1">
																	<bean:message key='jx.khplan.param1.degreeShowType3' />  
																</logic:notEqual>																	
																<logic:equal name="examPlanForm" property="busitype" value="1">	
																	<bean:message key='jx.khplan.param1.evaluateShowType3' /> 
																</logic:equal>
															</td>
														</tr>															
													</table>																				
												</td>
											</tr>
											<tr>
												<td>
													<html:radio name="examPlanForm" property="dataGatherMode" value="2"
														onclick="hideElement('datepn3');hideElement('datepn2');checkEval();setResult();setHide();" />
													<bean:message key='jx.khplan.param1.mix' />
												</td>
												<td>

												</td>
											</tr>
											<tr>
												<td>
													<html:radio styleId="dataGatherModes" name="examPlanForm" property="dataGatherMode" value="4"
														onclick="showElement('datepn3');hideElement('datepn2');checkEval();setResult();setHide();" />
														<bean:message key='plan.param.dataGatherMode3' />
												</td>
												<td>											
													<span id="datepn3"> 
														<html:radio name="examPlanForm" property="addSubtractType" value="1" /> 
															<bean:message key='plan.param.addSubtractType1' />
														<html:radio name="examPlanForm" property="addSubtractType" value="2" /> 
															<bean:message key='plan.param.addSubtractType2' /> 
														<html:radio name="examPlanForm" property="addSubtractType" value="3" /> 
															<bean:message key='plan.param.addSubtractType3' />
													</span>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td>
									<fieldset style="width:100%;padding:0 0 0 4px;" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.highnum2" />
											<html:checkbox styleId="fineRestrict" name="examPlanForm"
												property="fineRestrict" value="1"
												onclick="selectRestrict('high');" />

										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="70%">
													<input type="radio" name="nohigh" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title21' />
												</td>
												<td width="30%">
													<input type="text" id="bili1" size="2" disabled
														onblur="checkValue(this.id)">
													%
												</td>
											</tr>
											<tr>
												<td>
													<input type="radio" name="nohigh" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title22' />
												</td>
												<td>
													<input type="text" id="value1" size="2" disabled
														onblur="checkValue(this.id)">
												</td>
											</tr>											
										</table>
									</fieldset>
								</td>
								<td>
									<fieldset style="width:100%;padding:0 0 0 4px;margin-left:16px" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.highnum3" />
											<html:checkbox styleId="badlyRestrict" name="examPlanForm"
												property="badlyRestrict" value="1"
												onclick="selectRestrict('low');" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="70%">
													<input type="radio" name="nolow" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title21' />
												</td>
												<td width="30%">
													<input type="text" id="bili2" size="2" disabled
														onblur="checkValue(this.id)">
													%
												</td>
											</tr>
											<tr>
												<td>
													<input type="radio" name="nolow" disabled="disabled"
														onclick="radioSelect()">
													<bean:message key='jx.khplan.param1.title22' />
												</td>
												<td>
													<input type="text" id="value2" size="2" disabled
														onblur="checkValue(this.id)">
												</td>
											</tr>											
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title4" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="30%">
													<html:radio name="examPlanForm"
														property="sameResultsOption" value="1" onclick="radioSelect2()"/>
													<bean:message key='jx.khplan.param1.title41' />
												</td>
												<td width="30%">
													<html:radio name="examPlanForm"
														property="sameResultsOption" value="2" onclick="radioSelect2()"/>
													<bean:message key='jx.khplan.param1.title42' />
												</td>
												<td width="40%">
													<html:radio name="examPlanForm"
														property="sameResultsOption" value="3" onclick="radioSelect2();if(this.checked) {sameResultDefine();}"/>
														&nbsp;<bean:message key="kq.duration.define"/>
													<input type='button' class="mybutton" property="b_cancel"
														onclick='sameResultDefine();' id="sameResultDef_bt" 
														value='...' />
													<html:hidden name="examPlanForm" property="noCanSaveDegrees" styleId="noCanSaveDegrees"/>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>																												
							<tr id="totalScoreObjNumid">
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.totalScoreObjNum" />																							
											<html:checkbox styleId="sameAllScoreNumLess" name="examPlanForm"
												property="sameAllScoreNumLess" value="1"
												onclick="selectScoreNum_Less();" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="18%">
													<input type="radio" name="noScoreObj" disabled="disabled"
														onclick="radioSelectScoreNum()">
													<bean:message key='jx.khplan.param1.totalScoreObjNum1' />
												</td>
												<td>
													<input type="text" id="biliObj" value="100" size="4" disabled
														onblur="checkValue(this.id)">
													%
												</td>											
												<td width="18%">
													<input type="radio" name="noScoreObj" disabled="disabled"
														onclick="radioSelectScoreNum()">
													<bean:message key='jx.khplan.param1.totalScoreObjNum2' />
												</td>
												<td>
													<input type="text" id="valueObj" value="2" size="4" disabled
														onblur="checkValue(this.id)">
												</td>
											</tr>											
										</table>
									</fieldset>
								</td>
							</tr>
							<tr id='gather_type_1'>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title5" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="25%">
													<html:radio name="examPlanForm" property="blankScoreOption"
														value="0" onclick="setJw(this)" />
													<bean:message key='jx.khplan.param1.title51' />
												</td>
												<td width="23%">
													<html:radio name="examPlanForm" property="blankScoreOption"
														value="1" onclick="setJw(this)" />
													<bean:message key='jx.khplan.param1.title52' />
												</td>
												<td width="52%">
													<html:radio name="examPlanForm" property="blankScoreOption"
														value="2" onclick="setJw(this)" />
													<bean:message key='jx.khplan.param1.jw' />
													<html:select name="examPlanForm"
														property="blankScoreUseDegree" size="1" style="width:100px"
														styleId="blankScoreUseDegree">
														<html:optionsCollection property="grade_template"
															value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="jx.khplan.param1.title13" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td width="25%">
													<html:radio name="examPlanForm" property="mailTogoLink"
														value="1" onclick="setJw(this)" />
													<bean:message key="performance.param.objectgrade" />
												</td>
<!-- 												<td width="23%"> 目标考核无单人 lium -->
<%-- 													<html:radio name="examPlanForm" property="mailTogoLink" --%>
<%-- 														value="2" onclick="setJw(this)" /> --%>
<%-- 													<bean:message key="performance.param.singleleval" /> --%>
<!-- 												</td> -->
												<td width="52%" colspan="2">
													<html:radio name="examPlanForm" property="mailTogoLink"
														value="3" onclick="setJw(this)" />
													<bean:message key="null.label" />
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>							
							<tr id="pgxx">
								<td colspan="2">
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="performance.param.evaloption" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr id="keyEventCanNewPoint">
												<td>										
													<html:checkbox styleId="keyEventEnabled" name="examPlanForm"
														property="keyEventEnabled" value="1" />
													<bean:message key="jx.khplan.param2.title30" />
													&nbsp;&nbsp;&nbsp;&nbsp;
													<html:checkbox styleId="evalCanNewPoint" name="examPlanForm"
														property="evalCanNewPoint" value="1" />
													<bean:message key="plan.param.evalCanNewPoint" />
												</td>
											</tr>
											<tr id="allowAdjustEvalResults">
												<td>
												<% if(versionFlag==1){%>
													<html:checkbox styleId="allowAdjustEvalResult" name="examPlanForm"
														property="allowAdjustEvalResult" onclick='setAllowResult();checkEval();' value="1" />
													<bean:message key="jx.khplan.param2.allowresult" />
													&nbsp;&nbsp;&nbsp;&nbsp;													
													<bean:message key="jx.khplan.param2.evalrange" />:													
													<html:radio styleId="adjustEvalRange" name="examPlanForm" property="adjustEvalRange"
														value="0" onclick="setJw(this);checkEval();" />
													<bean:message key='menu.field' />												
													<html:radio styleId="adjustEvalRange1" name="examPlanForm" property="adjustEvalRange"
														value="1" onclick="setJw(this);checkEval();" />
													<bean:message key='label.zp_exam.sum_score' />
												<% }%>	
												</td>
											</tr>
											<tr>
												<td>
													<table id="adjustEvals">
														<tr>
															<td>
																&nbsp;&nbsp;&nbsp;&nbsp;
																<bean:message key="jx.khplan.param2.adjustType" />:													
																<html:radio styleId="adjustEvalDegreeType" name="examPlanForm" property="adjustEvalDegreeType"
																	value="0" onclick="setJw(this);checkType();" />
																<bean:message key='jx.khplan.param2.adjustType1' />												
																<html:radio styleId="adjustEvalDegreeType1" name="examPlanForm" property="adjustEvalDegreeType"
																	value="1" onclick="setJw(this);checkType();" />
																<bean:message key='jx.khplan.param2.adjustType2' />
															</td>
															<td>
																&nbsp;&nbsp;
																<bean:message key="jx.khplan.param2.adjustType3" />:	
																<input type="text" id="adjustEvalDegreeNum" name="adjustEvalDegreeNum"
															 onkeypress="event.returnValue=IsDigit(this);" value="<bean:write  name="examPlanForm" property="adjustEvalDegreeNum"/>" size='3' />
															</td>
															<td>
																<table border="0" cellspacing="2" cellpadding="0" id="adjustEvalDegreeNums">
																	<tr><td><button id="m_up" type="button" class="m_arrow" onclick="mincreasep('adjustEvalDegreeNum')">4</button></td></tr>
																	<tr><td><button id="m_down" type="button" class="m_arrow" onclick="msubtractp('adjustEvalDegreeNum')">4</button></td></tr>
																</table>
															</td>
															<td>	
																<input onkeyup="if(isNaN(value))execCommand('undo')" onafterpaste="if(isNaN(value))execCommand('undo')"
															 	type="text" id="adjustEvalGradeStep" name="adjustEvalGradeStep" value="<bean:write  name="examPlanForm" property="adjustEvalGradeStep"/>" size='3' />
															</td>
														</tr>
													</table>																									
												</td>
											</tr>
											<tr id="calcMenScoreRefDepts">
												<td>
												<% if(versionFlag==1){%>
													<html:checkbox styleId="calcMenScoreRefDept" name="examPlanForm"
														property="calcMenScoreRefDept" value="1" />
													<bean:message key="jx.khplan.param2.calcMenScoreRefDept" />
													&nbsp;	
													<input type="button" value="部门模板..." id="b_menRefDeptTmpl" class="mybutton" onclick='showMenRefDept("${param.plan_id}","${param.status}","${param.templateId}");' />												
												<% }%>	
												</td>
											</tr>
											<tr>
												<td nowrap id="scoreFromGrpOrders" >															
													<span id="scoreFromItems" nowrap>
													<% if(versionFlag==1){%>
														<html:checkbox styleId="scoreFromItem" name="examPlanForm"
															property="scoreFromItem" value="1" />
															<bean:message key="jx.khplan.param2.scoreFromItem" />
															&nbsp;
													<% }%>	
													</span>
													<span id="showGrpOrders" nowrap>
													<% if(versionFlag==1){%>
														<html:checkbox styleId="showGrpOrder" name="examPlanForm"
															property="showGrpOrder" value="1" />
														<bean:message key="jx.khplan.param2.title12" />
													<% }%>	
													</span>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr id='gather_type_2'>
							<%-- 
								<td colspan="2" style="height:35px">
									<bean:message key='jx.khplan.param1.title6' />
									&nbsp;
									<html:select name="examPlanForm" property="scoreWay"
										size="1" styleId="scoreWay">
										<html:option value="1">
											<bean:message key='jx.khplan.param1.title62' />
										</html:option>
									</html:select>
									&nbsp;
									<bean:message key='jx.khplan.param1.title60' />
								</td>
							--%>	
							</tr>
						</table>
					</hrms:tab>
					
					
					<hrms:tab name="param5" label="目标管理" visible="true">
						<table width="90%" border="0" cellspacing="0" cellpadding="1"
							align="center" valign="center" class=menu_table>
							<tr>
								<td height="5">

								</td>
							</tr>
							<tr>
								<td>
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="performance.param.flowcontrol" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<tr>
												<td>
													<html:checkbox styleId="taskAdjustNeedNew"
														name="examPlanForm" property="taskAdjustNeedNew" value="1" />
													<bean:message key="jx.param.new_task" />
												</td>
												<td>
													<html:checkbox styleId="isEntireysub" name="examPlanForm"
														property="isEntireysub" value="1" />
													<bean:message key="performance.param.musteval" />
												</td>
											</tr>
											<tr>
												<td>
													<html:checkbox styleId="taskCanSign" name="examPlanForm"
														property="taskCanSign" value="1" />
													<bean:message key="jx.param.obj_sign" />
												</td>
												<td>
													<html:checkbox styleId="taskNeedReview" name="examPlanForm"
														property="taskNeedReview" value="1" />
													<bean:message key="jx.param.review_task" />
												</td>
											</tr>
											<tr>
												<td>
													<html:checkbox styleId="publicPointCannotEdit"
														name="examPlanForm" property="publicPointCannotEdit"
														value="1" />
													<bean:message key="jx.khplan.param2.title34" />
												</td>
												<td>
													<html:checkbox styleId="targetAllowAdjustAfterApprove"
														name="examPlanForm" property="targetAllowAdjustAfterApprove"
														value="1" />
													<bean:message key="khplan.parameter.TargetAllowAdjustAfterApprove" />
												</td>
											</tr>
											<tr>
												<td>
													<html:checkbox styleId="allowLeadAdjustCard"
														name="examPlanForm" property="allowLeadAdjustCard"
														value="1" />
													<bean:message key="jx.khplan.param.AllowLeadAdjustCard" />
												</td>
												<td>
													<html:checkbox styleId="showDeductionCause"
														name="examPlanForm" property="showDeductionCause" onclick='setMustFillCause();'
														value="1" />
													<bean:message key="jx.khplan.param2.showDeductionCause" />
													<span id="ScoreIntroductuios">												
														<html:checkbox styleId="mustFillCause"
															name="examPlanForm" property="mustFillCause" onclick='setMustWriteButton();'
															value="1" />
														<bean:message key="jx.parameter.mustFillCause" />
													</span>
													<Input type="button" id="mustWriteButton" value="..."
														   class="mybutton" onclick="mustWriteScore();" />
														   
													<html:hidden name="examPlanForm" property="upIsValid" styleId="upIsValid"/>
													<html:hidden name="examPlanForm" property="downIsValid" styleId="downIsValid"/>
													<html:hidden name="examPlanForm" property="upDegreeId" styleId="upDegreeId"/>
													<html:hidden name="examPlanForm" property="downDegreeId" styleId="downDegreeId"/>
												</td>
											</tr>	
											<tr>
												<td>
													<html:checkbox styleId="targetCompleteThenGoOn" name="examPlanForm"
														property="targetCompleteThenGoOn" value="1" />
													目标卡填写完整才允许提交
												</td>
												<td>
													<html:checkbox styleId="evalOutLimitStdScore"
														name="examPlanForm" property="evalOutLimitStdScore"
														value="1" />
													<bean:message key="jx.param.evalOutLimitStdScore" />
												</td>
											</tr>
											<tr>

												<td><!-- 评分时不受考核机构限制 -->
                                                    <html:checkbox styleId="evalOutLimitScoreOrg" name="examPlanForm"
                                                        property="evalOutLimitScoreOrg" value="1" />
                                                    <bean:message key="jx.param.evalOutLimitOrganization" />
                                                </td>
                                                <td>
                                                	<span id="duty">
	                                                	<html:checkbox styleId="dutyRuleid" name="examPlanForm" property="dutyRuleid" value="1" onclick="showbutton(this);"/>
														按条件引入岗位职责指标
														<span id="dutyRuleidSpan">
														<Input type="button" value="..."
															id="dutyRuleidButton" class="mybutton" onclick="showDutyRule();" />
														</span>
													</span>
                                                </td>
											</tr>
											<tr>												
												<td colspan="2">
													<html:checkbox styleId="processNoVerifyAllScore"
														name="examPlanForm" property="processNoVerifyAllScore"
														value="1" />
													<bean:message key="jx.param.processNoVerifyAllScore" />
													&nbsp;
													<bean:message key="jx.param.processCheckRule" /><!-- 校验规则: -->
													<html:select name="examPlanForm" property="verifyRule" size="1" styleId="verifyRule">
														<html:option value="=">
															<bean:message key="jx.param.processEqualTo" /><!-- 等于 -->
														</html:option>
														<html:option value="<=">
															<bean:message key="jx.param.processNotMoreThan" /><!-- 不大于 -->
														</html:option>
													</html:select>
													<bean:message key="label.kh.template.topscore" />
												</td>
											</tr>	
											
											<tr>
												<td colspan="2">
												<html:checkbox styleId="mutiScoreGradeCtl"
														onclick="selMult(this)" name="examPlanForm"
														property="mutiScoreGradeCtl" value="1" />
													<bean:message key="jx.khplan.param2.title27" /><!-- 强制分布百分比基数 -->
													<html:select name="examPlanForm" property="checkGradeRange" style="width:120px"
														size="1" styleId="checkGradeRange">
														<html:option value="0">
															<bean:message key='jx.khplan.param2.title28' />
														</html:option>
														<html:option value="1">
															<bean:message key='jx.khplan.param2.title29' />
														</html:option>
													</html:select>
													<html:hidden name="examPlanForm" property="mainbodybodyid" styleId="mainbodybodyid"/>
													<html:hidden name="examPlanForm" property="allmainbodybody" styleId="allmainbodybody"/>
													<span id="MainbodyGradeCtlSpan">
													<Input type="button" value="..."
														id="MainbodyGradeCtl" class="mybutton" onclick="mainbodyGradeCtl();" />
													</span>
												</td>
											</tr>
																					
											<tr>
												<td colspan="2">
													<html:checkbox styleId="taskSupportAttach" name="examPlanForm"
														property="taskSupportAttach" value="1" />
													<bean:message key="jx.param.taskSupportAttach" />
												</td>
											</tr>																						
												
											<tr>
												<td>
													<html:checkbox styleId="isLimitPointValue" name="examPlanForm"
														property="isLimitPointValue" value="1" />
														<logic:equal name="examPlanForm" property="templateType" value="0">
															<bean:message key="jx.param.IsLimitPointValue2" />
														</logic:equal>
														<logic:equal name="examPlanForm" property="templateType" value="1">
															<bean:message key="jx.param.IsLimitPointValue1" />
														</logic:equal>
													&nbsp;&nbsp;
												</td>
												<td id="verifySameScores">
													<html:checkbox styleId="verifySameScore" 
														name="examPlanForm" property="verifySameScore" value="1" />													
													<bean:message key="jx.param.verifySameScore" />																																						
												</td>												
											</tr>	
											<tr>
												<td>
													<html:checkbox styleId="showLeaderEval" 
														name="examPlanForm" property="showLeaderEval" value="1" />													
													<bean:message key="jx.param.IsBossValue" />																																						
												</td>												
											</tr>								
											<tr>
												<td colspan='2'>
													&nbsp;<bean:message key="jx.param.objectdegree1" />
													<html:select name="examPlanForm"
														property="targetMakeSeries" size="1"
														styleId="targetMakeSeries">
														<html:option value="1">
															<bean:message key="jx.param.upperpost" />
														</html:option>
														<html:option value="2">
															<bean:message key="jx.param.upperupperpost" />
														</html:option>
														<html:option value="3">
															<bean:message key="jx.param.degree7" />
														</html:option>
														<html:option value="4">
															<bean:message key="jx.param.degree8" />
														</html:option>
													</html:select>
													<bean:message key="jx.param.objectdegree2" />
													&nbsp;&nbsp;
													<bean:message key="jx.param.spmode" />
													<html:select name="examPlanForm" property="targetAppMode" onchange="changeAppMode()"
														size="1" styleId="targetAppMode">
														<html:option value="0">
															<bean:message key="performance.relation" />
														</html:option>
														<html:option value="1">
															<bean:message key="pos.posparameter.ps_reportrelation" />
														</html:option>
													</html:select>													
												</td>
												<td style="height:35px">  
													&nbsp;
												</td>
											</tr>
											<tr>
												<td id="mainbodySpByBodySeq">
													<html:checkbox styleId="spByBodySeq" onclick='selectSpByBodySeq()'
														name="examPlanForm" property="spByBodySeq" value="1" />													
													<bean:message key="jx.param.byMainbodySpByBodySeq" />																																						
												</td>
												<td>
													<html:checkbox styleId="gradeByBodySeq" 
														name="examPlanForm" property="gradeByBodySeq" value="1" onclick="gradeChange(this)"/>													
													<bean:message key="jx.param.byMainbodySeqGrade" />																																						
												</td>												
											</tr>	
											<tr>
												<td>
												&nbsp;
												</td>												
											</tr>	
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td>
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="performance.param.dispoption" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">											
											<tr>
												<td colspan="2" nowrap>
													<html:checkbox styleId="idioSummary" name="examPlanForm"
														property="idioSummary" value="1" onclick="setPerforReport1()"/>
													<bean:message key="jx.khplan.param2.title3" /><!-- 显示绩效报告  目标考核 -->
													<Input type="button" value="..." id="affixUploadFile" class="mybutton" onclick="_showAffixWindows();" />
													<span id="UploadFile.show">
														<html:checkbox styleId="scoreBySumup" name="examPlanForm"
															property="scoreBySumup" value="1" />
														<bean:message key="jx.khplan.param2.title8" />
														<html:checkbox styleId="allowUploadFile" name="examPlanForm"
															property="allowUploadFile" value="1" />
															支持附件上传	
													</span>
												</td>
								
											</tr>
											<tr>
												<td colspan="2" nowrap>
													<html:checkbox styleId="isShowSubmittedScores"
														name="examPlanForm" property="isShowSubmittedScores"
														value="1" />
													<bean:message key="jx.khplan.param2.title9" />
												</td>
											</tr>
											<tr>
												<td nowrap>
													<html:checkbox styleId="allowSeeLowerGrade" name="examPlanForm"
															property="allowSeeLowerGrade" value="1" />
													<bean:message key="jx.khplan.param.AllowSeeLowerGrade" />
												</td>
												<td nowrap>
													<html:checkbox styleId="noShowTargetAdjustHistory" name="examPlanForm"
															property="noShowTargetAdjustHistory" value="1" />
													<bean:message key="plan.param.noShowTargetAdjustHistory" />
												
												</td>
											</tr>
											
											<tr>
												<td nowrap colspan='3' id='asag'>
													<html:checkbox  styleId="allowSeeAllGrade" name="examPlanForm"
															property="allowSeeAllGrade" value="1" />
													<bean:message key="jx.param.allowSeeOtherScore" />
													
													
												</td>
												 
											</tr>
											
											
											<tr  height=24>
												<td colspan='2'>
													&nbsp;<html:select name="examPlanForm"
														property="selfScoreInDirectLeader" size="1"
														styleId="selfScoreInDirectLeader">
														<html:option value="0">
															<bean:message key='jx.khplan.param2.title23' />
														</html:option>
														<html:option value="1">
															<bean:message key='jx.khplan.param2.title24' />
														</html:option>
														<html:option value="2">
															<bean:message key='jx.khplan.param2.title25' />
														</html:option>
														<html:option value="3">
															<bean:message key='jx.khplan.param2.title26' />
														</html:option>
													</html:select>
													<bean:message key="jx.khplan.param2.title22" />
												</td>
											</tr>
											<% if(methodFlag){%>
											<tr height=24>
												<td>
                                                    <html:checkbox styleId="targetTraceEnabled" name="examPlanForm"
                                                        property="targetTraceEnabled" value="1" onclick="setTargetTraceEnabled(this)"/>
                                                    <bean:message key="jx.configparam.targetitems" />&nbsp;&nbsp;&nbsp;
													<span id="defineIndex_span">
													   <input type='button' class="mybutton" onclick='defineIndex();' id="defineIndex_bt" 
                                                        value='<bean:message key="plan.param.defineIndex"/>'/>
	                                                    <html:hidden name="examPlanForm" property="targetCalcItem" styleId="targetCalcItem"/>   
	                                                    <html:hidden name="examPlanForm" property="targetTraceItem" styleId="targetTraceItem"/> 
	                                                    <html:hidden name="examPlanForm" property="targetCollectItem" styleId="targetCollectItem"/>
	                                                    <html:hidden name="examPlanForm" property="targetDefineItem" styleId="targetDefineItem"/>
	                                                    <script type="text/javascript">
															// 标准分值不选中时，参数"评分得分不受标准分限制"必须为选中状态，且不可操作
															// 同时processNoVerifyAllScore取消选中,不可操作 add by 刘蒙
															var limitScore = document.getElementById("evalOutLimitStdScore");
															var targetItem = document.getElementById("targetDefineItem");
															var noVerify = document.getElementById("processNoVerifyAllScore");
															if (targetItem.value) {
																if (targetItem.value.indexOf("P0413") < 0) {
																	limitScore.checked = true;
																	limitScore.onclick = function() {
																		alert("目标卡指标中没有选择[标准分值]，不可取消。");
																		return false;
																	};
																	noVerify.checked = false;
																	noVerify.onclick = function() {
																		alert("目标卡指标中没有选择[标准分值]，不可选择。");
																		return false;
																	};
																} else {
																	limitScore.onclick = null;
																	noVerify.onclick = null;
																}
															}
														</script>
	                                                    <html:hidden name="examPlanForm" property="targetMustFillItem"  styleId="targetMustFillItem"/>
	                                                    <html:hidden name="examPlanForm" property="targetUsePrevious"  styleId="targetUsePrevious"/>        
	                                                    <html:hidden name="examPlanForm" property="taskNameDesc" styleId="taskNameDesc"/>   
	                                                    <html:checkbox styleId="allowLeaderTrace" name="examPlanForm"
                                                            property="allowLeaderTrace" value="1" style="display:none"/>
													</span>
												</td>
												<td>
														
												</td>
											</tr>	
											<% } %>	
											<%--<tr>--%>
											     <%--<td nowrap>--%>
                                                <%--<% if(versionFlag==1){%>--%>
                                                    <%--<html:checkbox styleId="showEmployeeRecord" name="examPlanForm"--%>
                                                            <%--property="showEmployeeRecord" value="1" onclick='selectEmpRecordType();' />--%>
                                                    <%--<bean:message key="jx.khplan.param2.showEmployeeRecord" />--%>
                                                    <%--<span id="RecordIntroductuios">                                    --%>
                                                            <%--<html:checkbox styleId="ShowDay" name="examPlanForm" property="showDay" value="1" />--%>
                                                            <%--<bean:message key="jx.parameter.showDayRecord" /><!--日报  -->--%>
                                                            <%--<html:checkbox styleId="ShowWeek" name="examPlanForm" property="showWeek" value="2" />--%>
                                                            <%--<bean:message key="jx.parameter.showWeekRecord" /><!--周报  -->--%>
                                                            <%--<html:checkbox styleId="ShowMonth" name="examPlanForm" property="showMonth" value="3" />--%>
                                                            <%--<bean:message key="jx.parameter.showMonthRecord" /><!--月报  -->--%>
                                                    <%--<span>--%>
                                                <%--<% } %>--%>
                                                <%--</td>--%>
											<%--</tr>							--%>
										</table>
									</fieldset>
								</td>
							</tr>							
							
						</table>
					</hrms:tab>
					
					
					<%
					}
					%>
					<hrms:tab name="param4" label="其它参数" visible="true">						
						<table width="90%" cellspacing="0" border="0" cellpadding="2"
							align="center" class=menu_table>
							<tr>
								<td height="5">

								</td>
							</tr>
							<tr>
								<td>
									<fieldset style="width:100%" name="filterset">
										<legend>
											<bean:message key="performance.param.evaloption" />
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<logic:equal name="examPlanForm" property="busitype" value="1">
											<tr>
												<td>
												<%if("1".equals(request.getParameter("gather_type"))){ %>
												   <html:checkbox styleId="byModel" name="examPlanForm" disabled="true"
														property="byModel" value="1" onclick="changeModel();_changeModel();"/>
													<bean:message key="jx.khplan.param3.title0" />
												<%}else{ %>
												   <html:checkbox styleId="byModel" name="examPlanForm" 
														property="byModel" value="1" onclick="changeModel();_changeModel();"/>
													<bean:message key="jx.khplan.param3.title0" />
												<%} %>
													</td>
											</tr>
											</logic:equal>
											<tr>
												<td>
												  
													<html:checkbox styleId="wholeEval" name="examPlanForm"
														property="wholeEval" value="1" onclick="showEvalClass()"/>
													<bean:message key="jx.khplan.param3.title1" /><!-- 显示总体评价 -->
													<span id="eval"> 
													&nbsp;&nbsp;<html:select name="examPlanForm" property="wholeEvalMode" onchange="changewholeEvalMode()"
															size="1">
															<html:option value="0">
																<bean:message key="jx.khplan.param3.grade" />
															</html:option>
															<html:option value="1">
																<bean:message key="jx.khplan.param3.score" />
															</html:option>
														</html:select>
														<span id="wholeEvalFormula">
										            &nbsp;&nbsp;<html:select
																name="examPlanForm" property="evalClass" size="1"
																style="width:150px">
																<html:optionsCollection property="perGradeSetList"
																	value="dataValue" label="dataName" />
															</html:select>														
															&nbsp;&nbsp;
															<input type="button" id="calbutton" class="mybutton" value="计算公式" onclick="calformula('${examPlanForm.templateId}','${param.status}');">															
														</span>
													</span>
												</td>
											</tr>
											<tr>
												<td>
												 
													<html:checkbox styleId="descriptiveWholeEval"
														name="examPlanForm" property="descriptiveWholeEval" onclick="showEvalClass2()" value="1" />
													<bean:message key="jx.khplan.param3.title8" /><!-- 显示描述性总体评价 -->
														&nbsp;&nbsp;&nbsp;&nbsp;
													<span id="showEval">
														<html:checkbox styleId="mustFillWholeEval"
															name="examPlanForm" property="mustFillWholeEval" value="1" />
														<bean:message key="jx.param.mustFillWholeEval" />
													</span>	
												</td>
											</tr>
											<%
												    if (request.getParameter("method").equals("2"))
												    {
											%>
											<tr>
												<td>
													<span id="showEvalDirectors">
														<html:checkbox styleId="showEvalDirector"
															name="examPlanForm" property="showEvalDirector" value="1" />
														<bean:message key="jx.khplan.param3.showEvalDirector" />
													</span>
												</td>
											</tr>
											<%
											}
											%>
											<%
												 if (!request.getParameter("method").equals("2"))
												 {
											%>
											<tr>
												<td>
													<html:checkbox styleId="nodeKnowDegree" name="examPlanForm"
														property="nodeKnowDegree" value="1" onclick="dispButton();" />
													<bean:message key="jx.khplan.param3.title2" />
													&nbsp;
													<span id="know_span">
														<hrms:priv func_id="3260603">
													 <Input type="button" value="..."
															id="know_button" class="mybutton" onclick="knowDegree();" />
														</hrms:priv>
													</span>
													&nbsp;&nbsp;
													<span id="showEvalDirectors">
														<html:checkbox styleId="showEvalDirector"
															name="examPlanForm" property="showEvalDirector" value="1" />
														<bean:message key="jx.khplan.param3.showEvalDirector" />
													</span>
												</td>																
											</tr>
											
											<tr>
												<td>
													<html:checkbox styleId="showAppraiseExplain"
														name="examPlanForm" property="showAppraiseExplain" value="1" />
													<logic:equal name="examPlanForm" property="busitype" value="0">
														<bean:message key="jx.khplan.param3.title3" />
													</logic:equal>
													<logic:equal name="examPlanForm" property="busitype" value="1">
														<bean:message key="jx.khplan.param3.titleScore" />
													</logic:equal>
												</td>
											</tr>
											<tr>
												<td>
				
													<html:checkbox styleId="gatiShowDegree" name="examPlanForm"
														property="gatiShowDegree" value="1" />
													<bean:message key="jx.khplan.param3.title4" />
												</td>
											</tr>
											<%}else{%>
											<tr>
												<td>
													<html:checkbox styleId="showAppraiseExplain"
														name="examPlanForm" property="showAppraiseExplain" value="1" />
													<logic:equal name="examPlanForm" property="busitype" value="0">
														<bean:message key="jx.khplan.param3.title3Eval" />
													</logic:equal>
													<logic:equal name="examPlanForm" property="busitype" value="1">
														<bean:message key="jx.khplan.param3.titleScore" />
													</logic:equal>
												</td>
											</tr>
											<%}%>
										</table>
									</fieldset>
								</td>
							</tr>
							<logic:equal name="examPlanForm" property="busitype" value="1">
									<%
									if ("1".equals(request.getParameter("gather_type")))
									{%><!-- 能力素质的机读才出现 -->
										<tr id="jiduParams" >
										<td>
											<fieldset style="width:100%" name="filterset">
												<legend>
													其它设置
												</legend>
												<table width="100%" border="0" cellpmoding="0" cellspacing="0"
													cellpadding="0" align="center">
													
													<tr id="readerTypes">
														<td>
															&nbsp;<bean:message key="jx.khplan.param3.readerType" />
															<html:select name="examPlanForm" property="readerType" onchange="changeParams()"
																size="1">
																<html:option value="0">
																	<bean:message key="jx.khplan.param3.readerType0" /><!-- 光标阅读机 -->
																</html:option>
																<html:option value="1">
																	<bean:message key="jx.khplan.param3.readerType1" /><!-- 扫描仪 -->
																</html:option>
															</html:select>
														</td>
													</tr>
													<tr id="objsFromCards">
														<td>
															<html:checkbox styleId="objsFromCard" name="examPlanForm"
																property="objsFromCard" onclick="showObjsFromCard();" value="1" />
															<bean:message key="jx.khplan.param3.bodysFromCard" /><!-- 考核对象从机读卡读取(考核实施中不需要选择考核对象) -->
														</td>
													</tr>							
												</table>
											</fieldset>
										</td>
									</tr>
									
								<%} %>
							</logic:equal>
							
							<logic:equal name="examPlanForm" property="busitype" value="0">
							<tr>
								<td>
									<fieldset style="width:100%" name="filterset">
										<legend>
											其它设置
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											
											<logic:equal name="examPlanForm" property="busitype" value="0">
												<tr>
												<%
												    if (!request.getParameter("method").equals("2"))
												    {
												%>
													<td>
														&nbsp;<bean:message key="jx.khplan.param3.title5" />
														<html:select name="examPlanForm" property="performanceType" onchange="changePerformanceType()"
															size="1">
															<html:option value="0">
																<bean:message key="jx.khplan.param3.title6" />
															</html:option>
															<html:option value="1">
																<bean:message key="jx.khplan.param3.title7" />
															</html:option>
														</html:select>
														&nbsp;
														<input type="button" id="probutton" class="mybutton" value="描述性评议项" onclick="proAppraise();">
													</td>
												<%}%>
												<%-- 
													<td>
														&nbsp;<bean:message key="jx.selfScore.e0122Level" />
														<html:select name="examPlanForm" property="departmentLevel" size="1" style="width:50px">
															<html:optionsCollection property="departmentLeveList"
																	value="dataValue" label="dataName" />
														</html:select>
													</td>
												--%>
												</tr>
											</logic:equal>
											
											<tr id="readerTypes">
												<td>
													&nbsp;<bean:message key="jx.khplan.param3.readerType" />
													<html:select name="examPlanForm" property="readerType" onchange="changeParams()"
														size="1">
														<html:option value="0">
															<bean:message key="jx.khplan.param3.readerType0" /><!-- 光标阅读机 -->
														</html:option>
														<html:option value="1">
															<bean:message key="jx.khplan.param3.readerType1" /><!-- 扫描仪 -->
														</html:option>
													</html:select>
												</td>
											</tr>
											<tr id="objsFromCards">
												<td>
													<html:checkbox styleId="objsFromCard" name="examPlanForm"
														property="objsFromCard" onclick="showObjsFromCard();" value="1" />
													<bean:message key="jx.khplan.param3.bodysFromCard" />
												</td>
											</tr>
											
											<tr>
												<td>
													&nbsp;<bean:message key="jx.parameter.showBackTables" />&nbsp;&nbsp;
													<Input type="button" value="..." id="b_showBack" class="mybutton" onclick="showBackTableSet();" />
													<html:hidden name="examPlanForm" property="showBackTables" styleId = 'showBackTables' />
												</td>
											</tr>									
										</table>
									</fieldset>
								</td>
							</tr>
														
							<tr id="warnperson">
								<td>
									<fieldset style="width:100%" name="filterset">
										<legend>
											预警提醒
										</legend>
										<table width="100%" border="0" cellpmoding="0" cellspacing="0"
											cellpadding="0" align="center">
											<%
												if (request.getParameter("method").equals("2"))
												{
											%>											
												<tr>
													<td width="40%">
														<html:checkbox styleId="warnOpt1" name="examPlanForm"
															property="warnOpt1" onclick="showWarnOpt('warnOpt1');" value="1" />
														<bean:message key="jx.param.showCardZhiDingShenPi" />
													</td>
													<td align="left" width="60%">
														<table border="0" cellspacing="0" align="left" cellpadding="0">
															<tr>
																<td>
																	<div class="m_frameborder">
																		延期
																		<html:text name="examPlanForm" styleClass="m_input"
																			property="delayTime1" size="2"
																			onkeypress="event.returnValue=IsDigit2(this);"
																			disabled="true" onblur="testNum(this)" />																		
																	</div>
																</td>
																<td>																
																	<table border="0" cellspacing="2" cellpadding="0">
																       <tr><td><button type="button" id="delayTime1_up" class="m_arrow" onmouseup="mincrease('delayTime1',100);">5</button></td></tr>
																       <tr><td><button type="button" id="delayTime1_down" class="m_arrow" onmouseup="msubtract('delayTime1',1);">6</button></td></tr>
																    </table>
																</td>
																<td>
																	天预警							
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td width="40%">
														&nbsp;&nbsp;&nbsp;			
													</td>
													<td width="60%" style="position:relative;">
														预警对象
														<html:text maxlength="30" size="20" name="examPlanForm" styleId="roleScope1Desc"
															property="roleScope1Desc" readonly="true" styleClass="textColorRead"/>
															<span  style="position:absolute;top:2px;">
															<a id="roleScope1image" onclick="selectRoles('roleScope1');">
																<img src="/images/code.gif" border=0>																																											
															</a>
															</span>	
															<html:hidden name="examPlanForm" styleId="roleScope1" property="roleScope1"/>
													</td>
												</tr>											
											<%
												}
											%>
											
												<tr>
													<td width="40%">
														<html:checkbox styleId="warnOpt2" name="examPlanForm"
															property="warnOpt2" onclick="showWarnOpt('warnOpt2');" value="1" />
														<bean:message key="jx.param.showKaoHePingfen" />
													</td>
													<td align="left" width="60%">
														<table border="0" cellspacing="0" align="left" cellpadding="0">
															<tr>
																<td>
																	<div class="m_frameborder">
																		延期
																		<html:text name="examPlanForm" styleClass="m_input"
																			property="delayTime2" size="2"
																			onkeypress="event.returnValue=IsDigit2(this);"
																			disabled="true" onblur="testNum(this)" />																		
																	</div>
																</td>
																<td>																
																	<table border="0" cellspacing="2" cellpadding="0">
																       <tr><td><button type="button" id="delayTime2_up" class="m_arrow" onClick="return false;" onmouseup="mincrease('delayTime2',100);">5</button></td></tr>
																       <tr><td><button type="button" id="delayTime2_down" class="m_arrow" onClick="return false;"  onmouseup="msubtract('delayTime2',1);">6</button></td></tr>
																    </table>							
																</td>
																<td>
																	天预警							
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td width="40%">
														&nbsp;&nbsp;&nbsp;			
													</td>
													<td width="60%"  style="position:relative;">
														预警对象
														<html:text maxlength="30" size="20" name="examPlanForm" styleId="roleScope2Desc"
															property="roleScope2Desc" readonly="true" styleClass="textColorRead"/>
															<span  style="position:absolute;top:2px;">
															<a id="roleScope2image" onclick="selectRoles('roleScope2');">							
																<img src="/images/code.gif" border=0>																					
															</a>
															</span>
															<html:hidden name="examPlanForm" styleId="roleScope2" property="roleScope2"/>
													</td>
												</tr>
																				
										</table>
									</fieldset>
								</td>
							</tr>															
							</logic:equal>
							
							

							<tr>
						       <td align="center">
						       <fieldset style="width:100%">
						       	<%if("2".equals(str)){ %>
						          <legend>目标考评</legend>		
								<%}else{ %>
						          <legend>360度考评</legend>
						        <%} %>
						          <table  border="0" align="center" width="100%">
						             <logic:iterate id="evaluate" name="examPlanForm" property="evaluateList" offset="0" indexId="index">
							                <tr><td width="10%" align="center">
					                             <logic:equal value="1" name="evaluate" property="select">
					                               	<input type="checkbox" name="eva" value="<bean:write name="evaluate" property="id"/>" onclick="changeBlind();" checked/>
					                             </logic:equal>
					                             <logic:equal value="0" name="evaluate" property="select">
					                              	<input type="checkbox" name="eva" value="<bean:write name="evaluate" property="id"/>" onclick="changeBlind();"/>
					                             </logic:equal>
				                             </td>
				                             <td>
				                                <logic:notEqual value="7" name="evaluate" property="id">
				                                	<bean:write name="evaluate" property="name"/>
				                                </logic:notEqual>
					                            <logic:equal value="7" name="evaluate" property="id">
					                              	<bean:write name="evaluate" property="name"/>&nbsp;&nbsp;<span id="blind_"><input type="text" name="blind_point" id="blind" value="${examPlanForm.blind_point}" size="3" onkeypress="event.returnValue=IsDigit(this);" onblur="checkValue(this)"/>%</span>
					                            </logic:equal>
				                             </td>
				                                </tr>
						             </logic:iterate>
						          </table>
						       </fieldset>
						     </tr>
		     				
						</table>
					</hrms:tab>
				</hrms:tabset>
				
			</td>
		
			<td width="60px" valign="top" align="left" style="padding-left:5px;padding-top:30px;">
				<input type='button' class="smallbutton" property="b_ok"
					onclick='saveParam();' name="ok"
					value='&nbsp;<bean:message key="button.ok"/>&nbsp;' id="ok" />
				<input type='button' class="smallbutton" property="b_cancel"
					onclick='closewindow();' id="cancel" name='cancel'
					value='&nbsp;<bean:message key="button.cancel"/>&nbsp;' style="margin-top:30px;"/>
			</td>
		
		</tr>
		
	</table>
	<script language="JavaScript">


	<%
	if (request.getParameter("method").equals("2"))
	{
    %>
		if(document.getElementById("dutyRuleid").checked==true) {
            //document.getElementById("dutyRuleidSpan").style.display=""
            showElement('dutyRuleidSpan');
        }else{
            //document.getElementById("dutyRuleidSpan").style.display="none"
            hideElement('dutyRuleidSpan');
		}
	<%
	}
    %>
    if(setid.length==0){
        if(document.getElementById("duty")) {
            document.getElementById("duty").style.display = "none"
        }
    }

    //alert("${examPlanForm.pointEvalType}");
	<%
	if (request.getParameter("method").equals("1"))
	{
    %>
    changeBlind();
	 var pointEvalTypeNode = eval("examPlanForm.pointEvalType");
	 var pointEvalTypevalue="0";
	 if(pointEvalTypeNode){
	    pointEvalTypevalue = pointEvalTypeNode.value;
	 }
	if(pointEvalTypevalue=="1"){
        if(document.getElementById("radiospan")){
            document.getElementById("radiospan").style.display="";
        }
        if(document.getElementById("mutiScoreOnePageOnePoint")){
            document.getElementById("mutiScoreOnePageOnePoint").style.display="";
        }
//		showElement('radiospan');
//		showElement('mutiScoreOnePageOnePoint');
		}
    else{
	    if( document.getElementById("radiospan")) {
            document.getElementById("radiospan").style.display = "none";
        }
        if( document.getElementById("mutiScoreOnePageOnePoint")) {
            document.getElementById("mutiScoreOnePageOnePoint").style.display = "none";
        }
//    	hideElement('radiospan');
//    	hideElement('mutiScoreOnePageOnePoint');
    }	
	<%
	}
    %>

		if(theMethod=="1")
		{
			dafen_onLoad();
			if(theGatherType != '1')
			{
				bs_onLoad("${examPlanForm.perSet}");	
			}
			otherParam_onLoad();		
		}else if(theMethod=="2")
		{
			mbgl_onload();	
			setTheHihtestValue();		
		}
		readOnlySet();	

		var khbodySel='';
		var khbodyCn='';
		var templateType = "${examPlanForm.templateType}";
		if(theStatus=='5' || theStatus=='0')
		{			
			for (var i=0;i<examPlanForm.bodyId.length;i++) 
			{
				if (examPlanForm.bodyId[i].checked)
				{
					khbodySel=khbodySel+examPlanForm.bodyId[i].value+','; 
					khbodyCn=khbodyCn+examPlanForm.bodyId[i].id+','; 
				}
			}
			
			if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
			{
				if(document.getElementById("allowAdjustEvalResult")!=null && document.getElementById("allowAdjustEvalResult").checked==true)
				{
					if(document.getElementById("adjustEvalRange")!=null)
						document.getElementById("adjustEvalRange").disabled=false;	
					if(document.getElementById("adjustEvalRange1")!=null)		
						document.getElementById("adjustEvalRange1").disabled=false;	
					document.getElementById("adjustEvalDegreeType").disabled=false;
					document.getElementById("adjustEvalDegreeType1").disabled=false;
					document.getElementById("adjustEvalDegreeNum").disabled=false;
					document.getElementById("adjustEvalDegreeNums").disabled=false;
				}else{
					if(document.getElementById("adjustEvalRange")!=null)
						document.getElementById("adjustEvalRange").disabled=true;
					if(document.getElementById("adjustEvalRange1")!=null)
						document.getElementById("adjustEvalRange1").disabled=true;
					document.getElementById("adjustEvalDegreeType").disabled=true;
					document.getElementById("adjustEvalDegreeType1").disabled=true;
					document.getElementById("adjustEvalDegreeNum").disabled=true;
					document.getElementById("adjustEvalDegreeNums").disabled=true;
				}
				if(document.getElementById("dataGatherModes").checked==false)
				{		
					document.getElementById("allowAdjustEvalResults").disabled=false;
					document.getElementById("adjustEvals").disabled=false;
					document.getElementById("calcMenScoreRefDepts").disabled=false;
					if(theMethod=="2")				
						document.getElementById("showGrpOrders").disabled=false;
				}
				else
				{
					document.getElementById("allowAdjustEvalResults").disabled=true;			
					document.getElementById("adjustEvals").disabled=true;	
					document.getElementById("calcMenScoreRefDepts").disabled=true;	
					if(theMethod=="2")		
						document.getElementById("showGrpOrders").disabled=true;		
				}
			}
			
			
			if(theGatherType == '1')
			{
				if(document.getElementById("bodysFromCard").checked==false)
				{		
					document.getElementById("select").disabled=false;					
				}
				else
				{
					document.getElementById("select").disabled=true;
					document.getElementById("objsFromCard").checked=true;									
				}	
			}	
		}
		if(document.getElementById("dataGatherMode").checked==true)
		{
			showElement('pointEvalTypes');
		}
		else{
			hideElement('pointEvalTypes');				
		}		
		if(theObjectType != '2')
		{
			showElement('showEvalDirectors');
		}else{
			hideElement('showEvalDirectors');		
		}
		if(theMethod=="2")
		{
			if(templateType == '1')
			{
				showElement('verifySameScores');
			}else{
				hideElement('verifySameScores');		
			}
			if(theGatherType == '1')
			{
				hideElement('gather_type_2');
			}else{
				showElement('gather_type_2');		
			}
		}		
		if(theGatherType != '1')
		{
			if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
			{
				showElement('pgxx');
				showElement('warnperson');
			}
			hideElement('bodysFromCards');
		}else{
			if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
			{
				hideElement('pgxx');
				hideElement('warnperson');
			}
			showElement('bodysFromCards');		
		}		
		if(theMethod!="2")
		{
			if(theGatherType == '1')
			{
				hideElement('scoreWays');
			}else{
				showElement('scoreWays');		
			}
		}
		if(theGatherType == '1')
		{
			showElement('readerTypes');
			if(document.examPlanForm.readerType.value==1)
			{
				hideElement('bodysFromCards');
				hideElement('objsFromCards');
			}else{
				showElement('bodysFromCards');
				showElement('objsFromCards');
			}			
		}else if(theGatherType == '2')
		{
			hideElement('objsFromCards');
		}else
		{
			hideElement('readerTypes');	
			hideElement('objsFromCards');
		}
		
		
		if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
		{
			if(document.getElementById("dataGatherMode").checked==true && (document.getElementById("adjustEvalRange")!=null && document.getElementById("adjustEvalRange").checked==true))
			{
				showElement('adjustEvals');
			}
			else{
				hideElement('adjustEvals');				
			}		
			if(theObjectType == '2')
			{
				showElement('calcMenScoreRefDepts');
			}else{
				hideElement('calcMenScoreRefDepts');		
			}
			if(document.getElementById("adjustEvalDegreeType1").checked==true)
			{
				showElement('adjustEvalGradeStep');
			}
			else
			{
				hideElement('adjustEvalGradeStep');				
			}
			if(templateType == '1')
			{
				showElement('scoreFromItems');
			}else{
				hideElement('scoreFromItems');		
			}
			
			if(theGatherType != '1')
			{
				if(document.getElementById("dataGatherModes").checked==false)
				{			
					//showElement('pgxx');	
					showElement('keyEventCanNewPoint');
					showElement('allowAdjustEvalResults');
					if(theObjectType == '2')
					{
						showElement('calcMenScoreRefDepts');
					}else{
						hideElement('calcMenScoreRefDepts');		
					}			
					showElement('scoreFromGrpOrders');
				}				
				else
				{
					//hideElement('pgxx');
					hideElement('allowAdjustEvalResults');
					hideElement('adjustEvals');
					hideElement('calcMenScoreRefDepts');
					hideElement('scoreFromGrpOrders');
				}
				showElement('totalScoreObjNumid');		
			}else
			{
				hideElement('pgxx');
				hideElement('totalScoreObjNumid');
			}
		}
		if(document.getElementById("gradeByBodySeq")!=null && document.getElementById("gradeByBodySeq").checked)
	     showElement('asag');
	    else
	     hideElement('asag');
	    if(document.getElementById("idioSummary")!=null &&document.getElementById("idioSummary").checked) {
            showElement('UploadFile.show');
            showElement('jx.show');
        }
	    else {
            hideElement('UploadFile.show');
            hideElement('jx.show');
        }

    var scoreBySumup = "${examPlanForm.scoreBySumup}";

    if("True"==scoreBySumup){
        document.getElementById("scoreBySumup").checked=true;
	}
	     
		selMult();
		showTargetDesc();
		showEvalClass();
		///让“显示已自评目标卡” 动态出现或隐藏
		var viewtarget=document.getElementById("viewtarget");
		if(viewtarget==null)
			hideYP();
		else
		{
			if(viewtarget.checked)
				showYP();
			else
				hideYP();
		}
		///“按岗位素质模型测评” 这个复选框的处理
		var byModelObj=document.getElementById("byModel");
		if(byModelObj!=null)//能力素质
		{
			if(byModelObj.checked)
			{
				///如果勾选“按岗位素质模型测评”，那么就不显示多人考评四个字
				var bymodel1=document.getElementById("bymodel1");
				bymodel1.style.display="none";
				var bymodel3=document.getElementById("bymodel3");
				bymodel3.style.display="none";
				
				var muleval=document.getElementById("muleval");
				if(muleval!=null)
				{
					muleval.style.display="none";
				}
				var mulevalFieldsetObj=document.getElementById("mulevalFieldset");
				if(mulevalFieldsetObj!=null)
				{
					mulevalFieldsetObj.style.display="none";
				}
			}
			else
			{
				var bymodel2=document.getElementById("bymodel2");
				if(bymodel2!=null){
					bymodel2.style.display="none";
				}
				var bymodel4=document.getElementById("bymodel4");
				if(bymodel4!=null){
					bymodel4.style.display="none";
				}
			}
		}
		else//绩效
		{
				var bymodel2=document.getElementById("bymodel2");
				if(bymodel2!=null)
	            	bymodel2.style.display="none";
	            var bymodel4=document.getElementById("bymodel4");
	            if(bymodel4!=null)
	            	bymodel4.style.display="none";
		}
		
		var showdayweekmonth = '${examPlanForm.showDayWeekMonth}';
		selectEmpRecord(showdayweekmonth);
		
		changeModel();//所有的能力素质计划都应该没有多人考评相关设置   2013.11.26 pjf
		if("1"!=busitype)
		setPerforReport1();
		changewholeEvalMode();
		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
	</script>
</html:form>
<form name='downLoadForm'>
	<input type='hidden' name='plan_id' value="${param.plan_id}">
	<input type='hidden' name='userId' value="<%=userView.getUserId() %>">
</form>
