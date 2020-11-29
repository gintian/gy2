
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.operation.OperationForm
				,java.util.ArrayList,com.hrms.hjsj.sys.VersionControl" %>
	<%
	OperationForm operationForm = (OperationForm)session.getAttribute("operationForm");
	String code_leader=operationForm.getCode_leader();
	String infor_type = operationForm.getInfor_type();
	%>
	<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
	<script type="text/javascript">
	<!--
	var sp_flag22='${operationForm.sp_flag}';
	//不需要审批
	function approvetypef()
	{
		var info=$('info');
		var info2=$('info2');
		var info5=$('info5');//驳回方式
		var infoDefFlowSelf=$('infoDefFlowSelf');//自定义审批流程
		info.style.display="none";
		info2.style.display="none";
		info5.style.display="none";
		infoDefFlowSelf.style.display="none";
	}
	//需要审批
	function approvetype()
	{
		var info=$('info');//审批模式
		info.style.display="block";
		
		var infoDefFlowSelf=$('infoDefFlowSelf');//自定义审批流程
		if(sp_flag==1)//手工模式
		{
			info2.style.display="block";
			info5.style.display="block";
			infoDefFlowSelf.style.display="block";
		}
		else//自动流转
		{
			info2.style.display="none";
			info5.style.display="block";
			infoDefFlowSelf.style.display="none";
		}
	}
	//自动流转模式
	function approvetypef2()
	{
		var info2=$('info2');
		info2.style.display="none";
		sp_flag="0";
		var infoDefFlowSelf=$('infoDefFlowSelf');//自定义审批流程
		infoDefFlowSelf.style.display="none";
	}
	//手动流转模式
	function approvetype2()
	{
		var info2=$('info2');
		info2.style.display="block";
		sp_flag="1";
		var infoDefFlowSelf=$('infoDefFlowSelf');//自定义审批流程
		infoDefFlowSelf.style.display="block";
	}
	function selecttype(){
		var selectid = document.operationForm.endusertype.value;
		if(selectid==""){
		$('enduser').value="";
		$('endusertype').value="";
		}
		if(selectid=="0"){//选择用户
		$('endusertype').value="0";
		selectobject('0');
		}
		if(selectid=="1"){//选择人员
		$('endusertype').value="1";
		selectobject('1');
		}
	}
	
		function selectobject(objecttype)
		{
		//  $('actor_type').value=objecttype;  
		  if(objecttype=="0")
		   {
		        var return_vo=select_user_dialog('1','2');
		 	if(return_vo)
		 	{
		 		$('enduser').value=return_vo.title;
		 		$('enduservalue').value=return_vo.content;
		 	}
		      
		   }else if(objecttype=="1")
		   {
		        //var return_vo=select_org_emp_dialog(1,2,1,0,0,1);   //select_org_emp_dialog(1,2,1,0);  
		        var return_vo=select_org_emp_dialog2("1","2","0","1","0","1");  
			 if(return_vo)
			 {
		 		$('enduser').value=return_vo.title;
		 		$('enduservalue').value=return_vo.content;
		 	}	
		   }
		}
		
		function approve()
		{
			var info3=$('info3');
			if(document.operationForm.email.checked||document.operationForm.sms.checked)
			{
				info3.style.display="block";
			}
			else
			{
				info3.style.display="none";
			}
		}
		
		function approveinfo4()
		{
			var info4=$('info4');
			if(document.operationForm.email_staff.checked)
			{
				info4.disabled="";
			}else
			{
				info4.disabled="true";
			}
		}
	function validate(){
			var istrue=document.getElementById("code").checked;
			var objvalue=operationForm.codeitemid_buffer.value;
			if(istrue){
				if(objvalue==''){
				  alert("请选择分组指标！");
				  return false;
				}
		
			}
  		var def_flow_self = document.getElementById("def_flow_self");
  		if(!def_flow_self.checked)
  		{
  		    def_flow_self.value="0";
  		    def_flow_self.checked=true;
  		}
  		
		if(sp_flag==sp_flag22){
			if(document.getElementById("code").checked==false&&document.getElementById("leader").checked==false){ 
				document.getElementById("code").value="-1";
		    	document.getElementById("code").checked=true;   
			}
		 	return true;
		}else{
			
			if(sp_flag22=="0"){
				if(confirm("审批模式改变，流程定义被删除！是否确定？"))
				{
					if(document.getElementById("code").checked==false&&document.getElementById("leader").checked==false){ 
						document.getElementById("code").value="-1";
				    	document.getElementById("code").checked=true;   
					} 
					 return true;
			    }
			    else
			 		 return false;
			}
			else{
				if(document.getElementById("code").checked==false&&document.getElementById("leader").checked==false){ 
					document.getElementById("code").value="-1";
			    	document.getElementById("code").checked=true;   
				} 
				 return true;
			}
		}
		 
        return false;
	}
	var codeitemid_buffers="";
	var layer_buffers="";
	function codeitem_diff_report(){
		var tabid="${operationForm.tabid}";
		var target_url="/general/operation/updateapproveway.do?b_report=link&tab_id="+tabid+"&codeitemid_buffers="+codeitemid_buffers+"&layer_buffers="+layer_buffers;
	    //var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	    var returnvo=window.showModalDialog(target_url,null,"dialogWidth:300px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no"); 
	    if(returnvo==null){
	    	return;
	    }
	    var obj = new Object();
	    obj.codeitemid_buffer=returnvo.codeitemid_buffer;
	    codeitemid_buffers=returnvo.codeitemid_buffer;
	    layer_buffers=returnvo.layer_buffer;
	    obj.layer_buffer=returnvo.layer_buffer;
	    operationForm.codeitemid_buffer.value=obj.codeitemid_buffer;
	    operationForm.layer_buffer.value=obj.layer_buffer;
	}
 
 	
 	var num1=0;
 	var num2=0;
 	var str='<input type="radio" name="code_leader" value="0"   onclick="clickRadio1(this)" id="code">按单据中数据的分组指标值不同分批上报';
	str+='<Input type="button"  id="code_diff_report" value="..."  class="mybutton"  onclick="codeitem_diff_report()" />';
	str+='<br><input type="radio" name="code_leader" value="1" onclick="clickRadio2(this)" id="leader">按单据中人员的直接领导不同分批上报';
 	
 	if("<%=code_leader%>"==0){
		 	num1="1";
	 }
	if("<%=code_leader%>"==1){
		 	num2="1";
	}
	function clickRadio1(obj)
	{
		 var isTrue=false;
		 if(obj.value=='0'&&num1>0)
		 {  
		 	document.getElementById("groupId").innerHTML=str; 
		 	isTrue=true;
		 }
		 num2=0;
		 num1=num1+1;
		 if(isTrue)
		 	num1=0;

		 if(document.getElementById("code").checked==true){
		 	Element.show("code_diff_report");
		 }else{
		 	Element.hide("code_diff_report");
		 }
		 
	}
	
	function clickRadio2(obj)
	{  
		 
		 var isTrue=false;
		 if(obj.value=='1'&&num2>0)
		 { 
		 	document.getElementById("groupId").innerHTML=str; 
		 	isTrue=true;
		 }
		 num1=0; 
		 num2=num2+1;
		 if(isTrue)
			num2=0;

		 	Element.hide("code_diff_report");

	}
	function init(){
		var code_leader="<%=code_leader%>";
		if(code_leader=="0"){
			Element.show("code_diff_report");
		}else{
			Element.hide("code_diff_report");
		}
		var obj_reject_type1=$('reject_type1');
		var obj_reject_type2=$('reject_type2');
		if((!obj_reject_type1.checked) && (!obj_reject_type2.checked))
		{
			obj_reject_type1.checked="true";
		}
	}
	function clearall(){
	
        	document.getElementById("code").checked=false;
			document.getElementById("leader").checked=false;
			Element.hide("code_diff_report");
	}
//-->
</script>
<%
UserView userView = (UserView) pageContext.getSession()
			.getAttribute(WebConstant.userView);
		int versionFlag = 1;
		//zxj 20160613 人事异动不再区分标准版专业版
//if (userView != null)
	//	versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版	
 %>
<html:form action="/general/operation/updateapproveway">
	<br>
	<br>
	<table width="700px"  border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">	
				<bean:message key="t_template.update"/>
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="350"></td -->
			<td align="left"  class="TableRow">&nbsp;<bean:message key="t_template.update"/>&nbsp;</td> <!-- 修改审批模式 -->
		</tr>
		<tr>
			<td  class="framestyle3">
				<html:hidden name="operationForm" property="usertype"/>
				<html:hidden name="operationForm" property="enduservalue"/>
				<table border="0" cellpmoding="0" valign="top" cellspacing="2" class="DetailTable" cellpadding="0" width="90%" align="center">
					<tr class="list3">
						<td align="left" nowrap>
							<bean:message key="operation.process"/>:	<!-- 业务流程名称 -->					
							<logic:equal value="1" name="operationForm" property="usertype">
								<html:text name="operationForm" property="t_wf_defineVo.string(name)" maxlength="30" size="30" styleClass="text"  disabled="true"></html:text>
							</logic:equal>
							<logic:equal value="0" name="operationForm" property="usertype">
								<html:text name="operationForm" property="template_tableVo.string(name)" maxlength="30" size="30" styleClass="text"  disabled="true"></html:text>
							</logic:equal>
							</td>
					</tr>
				</table>
						<!-- 最外层第一个fieldset标签   审批方式-->
						<fieldset align="center"   style="width:90%;">
						 	<legend>审批方式</legend>
						 <table >
							<tr class="list3">
								<td align="left"   nowrap>
									<html:radio  name="operationForm" property="bsp_flag" value="0" onclick="approvetypef();"><bean:message key="t_template.approve.no"/></html:radio><!-- 不需要审批 -->
									<html:radio  name="operationForm" property="bsp_flag" value="1" onclick="approvetype();"><bean:message key="t_template.approve.ok"/></html:radio><!-- 需要审批 -->
								</td>
							</tr>
							<tr>
								<td >
								<!-- 审批方式里面第一个fieldset标签   审批模式-->
									<div id="info" style="display=none"><!-- 审批模式 -->
										<fieldset align="center" style="width:550px;">
									 		<legend><bean:message key="t_template.approve.mode"/></legend>
											<table>
												<tr class="list3">
													<td align="left"   nowrap>
														<logic:equal value="1" name="operationForm" property="usertype">
															<html:radio styleId="sp_flag0" name="operationForm" property="t_wf_defineVo.string(sp_flag)" value="0" onclick="approvetypef2();">自动流转模式</html:radio>
															<html:radio styleId="sp_flag1" name="operationForm" property="t_wf_defineVo.string(sp_flag)" value="1" onclick="approvetype2();">手工指派模式</html:radio>
														</logic:equal>
														<logic:equal value="0" name="operationForm" property="usertype">
															<html:radio name="operationForm" property="template_tableVo.string(sp_flag)" value="0" disabled="" onclick="approvetypef2();">自动流转模式</html:radio>
															<html:radio name="operationForm" property="template_tableVo.string(sp_flag)" value="1" disabled="" onclick="approvetype2();">手工指派模式</html:radio>
														</logic:equal>
													 </td>
												</tr>
												<tr>
													<td>
														<div id="info2" style="display:none">
															<table>
																<tr class="list3">
																	<td align="left"   nowrap>
																		业务办理人
																	 	<html:select name="operationForm" property="endusertype" size="1" onchange="selecttype()" >
																			<html:optionsCollection property="enduserList" value="dataValue" label="dataName"/>
																	  	</html:select>
											            	    		<html:text name="operationForm" property="enduser"  readonly="true" styleClass="text" />
																	</td>
																</tr>
															</table>
															<br/>
														<div>
													</td>
											 	</tr>
												<%
													if ( versionFlag == 1) {
												%>
												<tr class="list3">
													<td align="left"   nowrap>审批关系：
														<html:select name="operationForm" property="relation_id" size="1"  >
															<html:optionsCollection property="relationList" value="dataValue" label="dataName"/>
														</html:select>
													</td>
												</tr>
										 		<%
												  }
												%>
												
												<tr>
													<td>
														<div id="info5" style="display=none">
															<table>
																<tr class="list3">
																	<td align="left"   nowrap>
																		驳回方式：
																	 	<html:radio styleId="reject_type1" name="operationForm" property="reject_type" value="1">逐级驳回</html:radio>
																		<html:radio styleId="reject_type2" name="operationForm" property="reject_type" value="2">驳回到发起人</html:radio>
																	</td>
																</tr>
																<tr class="list3">
				                                                    <td align="left"   nowrap>
				                                                    <logic:equal value="1" name="operationForm" property="no_sp_yj">
                                                                         <input type="checkbox" name="no_sp_yj"  checked="checked" /><bean:message key="t_template.approve.no_sp_yj"/>
                                                                     </logic:equal>
                                                                     <logic:notEqual value="1" name="operationForm" property="no_sp_yj">
                                                                         <input type="checkbox" name="no_sp_yj" /><bean:message key="t_template.approve.no_sp_yj"/>
                                                                     </logic:notEqual>
                                                                     
				                                                    </td>
                                                                </tr>
                                                        
															</table>
														<div>
													</td>
											 	</tr>
												<tr>
													<td>
														<div id="infoDefFlowSelf" style="display=none">
															<table>
																<tr class="list3">
																	<td align="left"   nowrap>
															         	<html:checkbox property="def_flow_self" name="operationForm" 
													                		    value="1" >
													                		    <bean:message key="t_template.approve.selfdefflow"/>
													                	</html:checkbox>	
																	</td>
																</tr>
															</table>
														<div>
													</td>
											 	</tr>
											</table>
										</fieldset>
									</div> <!-- 审批模式 结束 -->
								</td>
							</tr>
							<%if(infor_type.equals("1")){ %>
		
							<tr>
								<td>
								<!-- 审批方式里面第二个fieldset标签   拆单模式-->
									<div id="infod" style="display=;margin-top:4px;">
										<fieldset align="center" style="width:550px;">
						 					<legend>拆单模式</legend>
											<table width="90%" height="100%">
												<tr>
													<td id='groupId' >
														<html:radio name="operationForm" property="code_leader" styleId="code" value="0" disabled="" onclick="clickRadio1(this)"/>按单据中数据的分组指标值不同分批上报
													    	<Input type='button'id="code_diff_report" value='...'  class="mybutton" onclick='codeitem_diff_report()' />
														<br>
														<html:radio name="operationForm" property="code_leader" value="1" disabled="" styleId="leader" onclick="clickRadio2(this)"/>按单据中人员的直接领导不同分批上报
														
													</td>
												</tr>	
											</table>
										</fieldset>	
									</div>	<!-- 拆单模式  结束 -->
								</td>
							<td><input type="button" class="mybutton" value="清空" onclick="clearall(); ">	</td>
							</tr>
							<%} %>
						</table>
					</fieldset> <!-- 整个审批方式 结束 -->
					<!-- 通知 -->
					<div style="margin-top:8px;margin-bottom:5px;">	
					<fieldset align="center" style="width:90%;">
						<legend><bean:message key="t_template.approve.tongzhi"/></legend>
							<table width="90%" height="100%">
								<tr>
									<td >
										<table width="100%" height="100%">
											<tr>
												<td align="left" valign="top" nowrap>
													<logic:equal value="0" name="operationForm" property="email">
														&nbsp;<input type="checkbox" name="email"  checked="checked" onclick="approve();" />
													</logic:equal>
													<logic:notEqual value="0" name="operationForm" property="email">
														&nbsp;<input type="checkbox" name="email" onclick="approve();"/>
													</logic:notEqual>	<bean:message key="t_template.approve.mode.email"/>
													<logic:equal value="0" name="operationForm" property="sms">
														<input type="checkbox" name="sms" checked="checked" onclick="approve();"/>
													</logic:equal>
								
													<logic:notEqual value="0" name="operationForm" property="sms">
														<input type="checkbox" name="sms" onclick="approve();"/>
													</logic:notEqual>
													<bean:message key="t_template.approve.mode.sms"/>
												</td>
											</tr>
											
											<tr>
												<td >
													
													<div id="info3" style="display=none">
														<table width="90%">
														  <tr class="list4">
                                                                <td align="left" nowrap>
                                                                     <logic:equal value="true" name="operationForm" property="notice_initiator">
                                                                         <input type="checkbox" name="notice_initiator"  checked="checked" />
                                                                     </logic:equal>
                                                                     <logic:notEqual value="true" name="operationForm" property="notice_initiator">
                                                                         <input type="checkbox" name="notice_initiator" />
                                                                     </logic:notEqual><bean:message key="t_template.approve.emailtobeginer"/>
                                                               </td>
                                                               <td align="left" nowrap>
                                                                     <html:select name="operationForm" property="template_initiator" size="1"  >
                                                                        <html:optionsCollection property="template_spList" value="dataValue" label="dataName"/>
                                                                     </html:select>
                                                               </td>
                                                            </tr>
															<tr class="list4">
															<!-- 
																<td align="left"   nowrap>
																	审批通知模板:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														    -->
														       <td align="left" nowrap>
														         &nbsp;审批通知模板:
														       </td>
														       <td align="left" nowrap>
																   <html:select name="operationForm" property="template_sp" size="1"  >
																	  <html:optionsCollection property="template_spList" value="dataValue" label="dataName"/>
																   </html:select>
																</td>
															</tr>
															<tr class="list4">
																<td align="left"   nowrap>
																	&nbsp;抄送通知模板:<!--  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-->
															    </td>
															    <td align="left" nowrap>
																 	<html:select name="operationForm" property="template_bos" size="1"  >
																		<html:optionsCollection property="template_spList" value="dataValue" label="dataName"/>
																 	</html:select>
																</td>
															</tr>
															
															<logic:notEqual value="0" name="operationForm" property="operationtype">
																<logic:notEqual value="5" name="operationForm" property="operationtype">
																	<tr class="list4">
																		<td align="left"  nowrap>
																			<logic:equal value="0" name="operationForm" property="email_staff">
																				<input type="checkbox" name="email_staff"  checked="checked" onclick="approveinfo4();" />
																			</logic:equal>
																			<logic:notEqual value="0" name="operationForm" property="email_staff">
																				<input type="checkbox" name="email_staff" onclick="approveinfo4();"/>
																			</logic:notEqual>抄送通知到本人，模板:
														               </td>
														               <td align="left"  nowrap>
																			<span id="info4" >
																				 <html:select name="operationForm" property="template_staff" size="1"  >
																					<html:optionsCollection property="template_spList" value="dataValue" label="dataName"/>
																				  </html:select>
																			</span>
																		</td>
																	</tr>
																</logic:notEqual>
															</logic:notEqual>
														</table>
														<br/>
													<div>
												</td>
											</tr>
										</table>
										<br/>
									</td>
								</tr>
							</table>
						</fieldset>
						</div><!-- 通知 结束 -->
						<div style="margin-top:8px;margin-bottom:5px;width:90%;height:75px;">	
							<table width="90%" align="center">
								<tr><td>导出文件格式（WORD | PDF）</td></tr>
								<tr nowrap>
									<td>
										<html:radio  name="operationForm" property="out_type" value="1" >分页导出</html:radio>
										<html:radio  name="operationForm" property="out_type" value="2" >不分页连续导出</html:radio>
									</td>
							</table>
						</div>
					</td>
				</tr>
				<bean:define id="bsp_flagvalue" name="operationForm" property="bsp_flag"/>
				<bean:define id="sp_flagvalue" name="operationForm" property="sp_flag"/>
				<bean:define id="sp_bos_flagvalue" name="operationForm" property="sp_bos_flag"/>
				<bean:define id="email_bos_flagvalue" name="operationForm" property="email_staff"/>

			</table><!-- 整个table结束。下面是按钮 -->
	
		<table width="400" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:5px;">
			<tr class="list3">
				<td align="center" >
					<input type="hidden" name="tabid" value=${operationForm.tabid} >
					<html:hidden name="operationForm" property="codeitemid_buffer" />
					<html:hidden name="operationForm" property="layer_buffer"/>
					<html:hidden name="operationForm" property="code_leader" />
					<hrms:submit styleClass="mybutton" property="b_update" onclick="return validate();" ><bean:message key="button.save"/>
						</hrms:submit>
						<hrms:submit styleClass="mybutton" property="br_return">
							<bean:message key="button.return" />
						</hrms:submit>
				</td>
			</tr>
		</table>

</html:form>
	<script type="text/javascript">
	init();
	<!--
		var bsp_flag=${bsp_flagvalue};//=0:不需要审批  =1:需要审批
		var info=$('info');//审批模式
		var sp_flag=${sp_flagvalue};//=1:手工指派 =0：自动流转
		var info2=$('info2');//业务办理人
		var info5=$('info5');//驳回方式
		if(bsp_flag==1)
		{
			var infoDefFlowSelf=$('infoDefFlowSelf');//自定义审批流程			
			info.style.display="block";
			if(sp_flag==1)//手工指派
			{
				infoDefFlowSelf.style.display="block"	
				info2.style.display="block";
				info5.style.display="block";//隐藏驳回方式
			}
			else//自动流转
			{
				infoDefFlowSelf.style.display="none"	
				info2.style.display="none";//隐藏业务办理人
				info5.style.display="block";//显示驳回方式
			}
		}
		else
		{
			info.style.display="none";//审批模式和业务办理人都隐藏
			info2.style.display="none";
		}
		var sp_bos_flag=${sp_bos_flagvalue};
		var info3=$('info3');//点击"通知"里的"手机短信"或"电子邮件"复选框出现的内容
		if(sp_bos_flag==1)
		{
			info3.style.display="block";
		}
		else
		{
			info3.style.display="none";
		}
		var info4=$('info4');//是否勾选了"抄送通知到本人，模板"
		var email_staff=${email_bos_flagvalue};
		
		if(email_staff+""=="0")
		{
			info4.disabled="";
		}
		else
		{
			info4.disabled="true";
		}
	//-->
	</script>
