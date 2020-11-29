<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView,java.util.*,com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.performance.options.PerRelationForm"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();  
	PerRelationForm form = (PerRelationForm)request.getAttribute("perRelationForm");
	ArrayList list = form.getObjectTypes();	
%>

<style>

.textInterface 
{
	BACKGROUND-COLOR:transparent;
	font-size: 12px;
	height:22;
	border: 1pt solid;
}
</style>

<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<SCRIPT LANGUAGE=javascript>	
	function check()
	{
		
		<% int n=0;  %>
		<logic:iterate  id="element"    name="perRelationForm"  property="selectedFieldList" indexId="index"> 
			<% n++; %>
			<logic:equal name="element" property="itemtype" value="N">
					var a<%=n%>=document.getElementsByName("aa<%=n%>.value")
					if(a<%=n%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=n%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>请输入数字！");
						return;
					 }
					 }
			</logic:equal>
			
		</logic:iterate>

		var relation=new Array();
		var fielditemid=new Array();
		var operate=new Array();
		var values=new Array();
		var a=0;		
		if(document.perRelationForm.relation.length)
		{
			for(var i=0;i<document.perRelationForm.relation.length;i++)
			{
			
				var a_relation=document.perRelationForm.relation[i].value;
				var a_fielditemid=document.perRelationForm.itemid[i].value;
				var a_operate=document.perRelationForm.operate[i].value;
				var name=$("aa"+(i+1)+".value");	
						
				//if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					values[a]=name.value;
					a++;
				}
			}
		}
		else
		{
				var a_relation=document.perRelationForm.relation.value;
				var a_fielditemid=document.perRelationForm.itemid.value;
				var a_operate=document.perRelationForm.operate.value;
				var name=$("aa1.value");	
						
			//	if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					values[a]=name.value;
					a++;
				}
		
		}	
		if(relation.length==0)
		{
			alert("请选择条件！");
			return;
		}		
		
		var hashvo=new ParameterSet();
		 hashvo.setValue("relation",relation);
		 hashvo.setValue("fielditemid",fielditemid);
		 hashvo.setValue("operate",operate);
		 hashvo.setValue("values",values);

		 In_paramters='flag=1';
		 
		 var queryType = '';
		 var obj=$('like');
		 if(obj.checked==true)
		 	queryType=queryType+'like=1';
		 else
		 	queryType=queryType+'like=0';		 	 
		 
		 In_paramters=queryType;		 
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'9026007014'},hashvo);		
	}
		
	function returnInfo(outparamters)
	{
		var sql=outparamters.getValue("sql");	
		if(window.showModalDialog){
            parent.window.returnValue=sql;
			parent.window.close();
		}else {
			parent.parent.query_ok_(sql);
			var win = parent.parent.Ext.getCmp('query_win');
	   		if(win) {
	    		win.close();
	   		}
		}
		
	}
	
	//上一步
	function pre_phase()
	{
		document.perRelationForm.action="/performance/options/kh_relation/query.do?b_init=link";
		document.perRelationForm.submit();
	}
	
	
	
	
	</SCRIPT>
<base id="mybase" target="_self">
<html:form action="/performance/options/kh_relation/query">
	<br>
	<br>
	<fieldset align="center" style="width:90%;">
		<legend>
			<bean:message key="button.h.query" />
		</legend>
		<table border="0" cellspacing="0" width="100%" align="center"
			cellpadding="0">
			<tr>
				<td>
					<table border="0" cellspacing="0" width="100%" class="ListTable"
						cellpadding="2" align="center">
						<tr>
							<td colspan="4">
								<br>
								<table border="0" cellspacing="0" width="97%" class="ListTable1"
									cellpadding="2" align="center">
									<tr>
										<td width="16%" align="center" nowrap class="TableRow">
											<bean:message key="label.query.logic" />
										</td>
										<td width="29%" align="center" nowrap class="TableRow">
											<bean:message key="label.query.field" />
										</td>
										<td width="13%" align="center" nowrap class="TableRow">
											<bean:message key="label.query.relation" />
										</td>
										<td width="42%" align="center" nowrap class="TableRow">
											<bean:message key="label.query.value" />
										</td>
									</tr>
									<%
									int i = 0;
									%>
									<logic:iterate id="element" name="perRelationForm"
										property="selectedFieldList">

										<tr>
											<td align="center" class="RecordRow" nowrap>
												<%
														if (i++ == 0)
														{
												%>
												<input type='hidden' name='relation' value='*' />
												<%
														} else
														{
												%>
												<select name="relation" size="1">
													<option value="*" selected="selected">
														<bean:message key="kq.wizard.even" />
													</option>
													<option value="+">
														<bean:message key="kq.wizard.and" />
													</option>
												</select>
												<%
												}
												%>
												&nbsp;
											</td>
											<td align="center" class="RecordRow" nowrap>
												<input type='hidden' name='itemid'
													value='<bean:write name="element" property="itemid" />§§<bean:write name="element" property="itemtype" />§§<bean:write name="element" property="itemsetid" />§§<bean:write name="element" property="table_name" />' />
												<bean:write name="element" property="itemdesc" />
											</td>
											<td align="center" class="RecordRow" nowrap>
												<select name="operate" size="1" style="width:100%">
													<option value="=" selected="selected">
														=
													</option>
													<option value="&gt;">
														&gt;
													</option>
													<option value="&gt;=">
														&gt;=
													</option>
													<option value="&lt;">
														&lt;
													</option>
													<option value="&lt;=">
														&lt;=
													</option>
													<option value="&lt;&gt;">
														&lt;&gt;
													</option>
												</select>
											</td>

											<td align="left" class="RecordRow" nowrap>
												<!--日期型 -->
												<logic:equal name="element" property="itemtype" value="D">
													<input type='text' name="aa<%=i%>.value" size="24" class="textInterface tabsetstyle"
														maxlength="10" onfocus='inittime(false);setday(this);'
														readOnly />
												</logic:equal>
												<!--字符型 -->
												<logic:equal name="element" property="itemtype" value="A">

													<logic:notEqual name="element" property="itemsetid"  value="0">
														<input type='hidden' name="aa<%=i%>.value" />
														<input type="text" name="aa<%=i%>.hzvalue" size="24"
															value="" readOnly class="textInterface tabsetstyle">
														<logic:notEqual name="element" property="itemsetid" value="UN">
															<logic:notEqual name="element" property="itemsetid" value="UM">
																<logic:notEqual name="element" property="itemsetid" value="@K">
																	<img src="/images/code.gif"	onclick='openCondCodeDialog("<bean:write name="element" property="itemsetid" />","aa<%=i%>.hzvalue");' />
																</logic:notEqual>
															</logic:notEqual>	
														</logic:notEqual>	
														<logic:equal name="element" property="itemsetid" value="UN">
															<img src="/images/code.gif" onclick="openInputOrgCodeDialogOrg('UN','aa<%=i%>.hzvalue','<%=manager%>',1);" />
														</logic:equal>
														<logic:equal name="element" property="itemsetid" value="UM">
															<img src="/images/code.gif" onclick="openInputOrgCodeDialogOrg('UM','aa<%=i%>.hzvalue','<%=manager%>',1);" />
														</logic:equal>
														<logic:equal name="element" property="itemsetid" value="@K">
															<img src="/images/code.gif" onclick="openInputOrgCodeDialogOrg('@K','aa<%=i%>.hzvalue','<%=manager%>',1);" />
														</logic:equal>
													</logic:notEqual>
													
													<logic:equal name="element" property="itemsetid" value="0">														
														<input type='text' name="aa<%=i%>.value" size="24" class="textInterface tabsetstyle" />														
													</logic:equal>
												
												</logic:equal>
												<!--数据值-->
												<logic:equal name="element" property="itemtype" value="N">
												
													<logic:equal name="element" property="itemid" value="obj_body_id">
														<select name="aa<%=i%>.value" size="1" style="width:153px">
															<% 
																for(int j=0;j<list.size();j++)
																{
																	CommonData data = (CommonData)list.get(j);
															%>
			  	 													<option value="<%=data.getDataValue() %>"><%=data.getDataName() %></option>
			  	 											<%
			  	 												}
			  	 											%>
														</select>	
													</logic:equal>
													<logic:notEqual name="element" property="itemid" value="obj_body_id">
														<input type='text' name="aa<%=i%>.value" size="24" class="textInterface tabsetstyle"/>
													</logic:notEqual>																								
													
												</logic:equal>
											</td>
										</tr>
									</logic:iterate>
									<tr>
										<td align="center" nowrap class="RecordRow" colspan="4">
											<input type="checkbox" name="like">
											&nbsp;
											<bean:message key="label.query.like" />							
										</td>
									</tr>
								</table>
							</td>
						</tr>
						
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding:10px;">
					<input type="button" value="<bean:message key="button.query.pre"/>"
						class="mybutton" onclick='pre_phase()' />
					<input type="reset" value="<bean:message key="button.clear"/>"
						class="mybutton">
					<input type="button" name="b_update"
						value="<bean:message key="button.ok"/>" onclick='check()'
						class="mybutton">
				</td>
			</tr>
		</table>
	</fieldset>
</html:form>
