<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css"> 
.textSize {
	border: 1pt solid #A8C4EC;
	width:230;
}
.textReadSize {
	BACKGROUND-COLOR:#E5E5E5;
	border: 1pt solid #A8C4EC;
	width:230;
}
</style>
<script language="javascript">
function saveEdit(setid,changeflag){
	var urlstr = "/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=save&setid=";
		urlstr+=setid+"&sflag=opens&keyid=${selfInfoForm.keyid}&sp_flag=";
		urlstr+=changeflag+"&typeid=${selfInfoForm.typeid}&chg_id=${selfInfoForm.chg_id}&sequenceid=${selfInfoForm.sequenceid}";
	selfInfoForm.action=urlstr;	 
   	selfInfoForm.submit();
}
function appEdit(setid,changeflag){
	if(confirm(APP_DATA_NOT_UPDATE+"?")){
		var urlstr = "/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=app&setid=";
			urlstr+=setid+"&sflag=opens&keyid=${selfInfoForm.keyid}&sp_flag=";
			urlstr+=changeflag+"&typeid=${selfInfoForm.typeid}&chg_id=${selfInfoForm.chg_id}&sequenceid=${selfInfoForm.sequenceid}";
		selfInfoForm.action=urlstr; 
   		selfInfoForm.submit();
   	}
}
function delEdit(setid,changeflag){
	if(confirm(DEL_INFO)){
		var urlstr = "/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=del&setid=";
			urlstr+=setid+"&sflag=opens&keyid=${selfInfoForm.keyid}&sp_flag=";
			urlstr+=changeflag+"&typeid=${selfInfoForm.typeid}&chg_id=${selfInfoForm.chg_id}&sequenceid=${selfInfoForm.sequenceid}";
		selfInfoForm.action=urlstr; 
   		selfInfoForm.submit();
   	}
}
var date_desc;
function showDateSelectBox(srcobj){
	date_desc=srcobj;
	Element.show('date_panel');   
	var pos=getAbsPosition(date_desc);
	with($('date_panel')){
		style.position="absolute";
		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
	}                 
}
function setSelectValue(){
	if(date_desc){
		date_desc.value=$F('date_box');
       	Element.hide('date_panel'); 
	}
}
function checkFlag(setid,viewitem){
	var url = "/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=search&setid=";
	url+=setid+"&chg_id=${selfInfoForm.chg_id}&sflag=opens&keyid=${selfInfoForm.keyid}";
	url+="&typeid=${selfInfoForm.typeid}&sequenceid=${selfInfoForm.sequenceid}&viewitem="+viewitem;
	selfInfoForm.action=url;	 
   	selfInfoForm.submit();
}
function backMain(){
	document.location.href = "/selfservice/selfinfo/inforchange.do?b_query=link";
}
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function checkTime(times){
	if(times.length==10||times.length==9){
 		var result=times.match(/^(\d{1,4})(.|\/)(\d{1,2})\2(\d{1,2})$/);
 		if(result==null) {
 			return false;
 		}
 		var d= new Date(result[1], result[3]-1, result[4]);
 		return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
 	}else if(times.length==7||times.length==6){
 		var result=times.match(/^(\d{1,4})(.|\/)(\d{1,2})$/);
 		if(result==null) {
 			return false;
 		}
 		var d= new Date(result[1], result[3]-1,'01');
 		return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
 	}else if(times.length==4){
 		if(times>2100||times<1800){
 			return false;
 		}else{
 			return true;
 		}	
 	}else if(times.length==0){
 		return true;
 	}else{
 		return false;
 	}
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		alert(INPUT_FORMAT_DATE+"!");
		obj.value='';
	}
}
</script>
<hrms:themes />
<% int i = 0; %>
<html:form action="/selfservice/selfinfo/iframapp">
	<bean:define id="chg_id" name='selfInfoForm' property='chg_id' />
	<%
	int m=0;%>
	<table width="80%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<logic:iterate id="element" name="selfInfoForm" property="fieldlist"
			indexId="index">
			<bean:define id="fieldsetid" name='element' property='fieldsetid' />
			<bean:define id="fieldsetdesc" name="element" property="customdesc" />
			<%
			String fieldsetId = PubFunc.encrypt(fieldsetid.toString());
			%>
			<logic:equal name="selfInfoForm" property="setid"
				value="<%=fieldsetId %>">
				<tr>
					<td valign="top">
						<table width="100%" border="0" cellspacing="0" align="center"
							cellpadding="0">
							<tr>
								<td>
									<a
										href="/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=search&chg_id=${chg_id}&sflag=closes"
										title="<bean:message key='lable.channel.hide'/>">
										${fieldsetdesc} <img src="/images/button_vert1.gif" border="0">
									</a>
								</td>
								<td align="center" valign="bottom">
									<logic:equal name="selfInfoForm" property="sp_flag" value="01">
										<hrms:priv func_id="0103010601">        
										<a href="###"
											onclick='saveEdit("<%=fieldsetId %>","${selfInfoForm.sp_flag}")'
											title="<bean:message key='button.save'/>"><bean:message key="button.save"/>
										</a>
										</hrms:priv>
										<hrms:priv func_id="0103010602">     
										<a href="###"
											onclick='appEdit("<%=fieldsetId %>","${selfInfoForm.sp_flag}")'
											title="<bean:message key='info.appleal.state1'/>"><bean:message key="info.appleal.state1"/>
										</a>
										</hrms:priv>
										<hrms:priv func_id="0103010603">     
										<a href="###"
											onclick='delEdit("<%=fieldsetId %>","${selfInfoForm.sp_flag}")'
											title="<bean:message key='kq.shift.cycle.del'/>"><bean:message key="kq.shift.cycle.del"/>
										</a>
										</hrms:priv>
									</logic:equal>
									<logic:equal name="selfInfoForm" property="sp_flag" value="07">
										<hrms:priv func_id="0103010601">      
										<a href="###"
											onclick='saveEdit("<%=fieldsetId %>","${selfInfoForm.sp_flag}")'
											title="<bean:message key='button.save'/>"><bean:message key="button.save"/>
										</a>
										</hrms:priv>
										<hrms:priv func_id="0103010602">      
										<a href="###"
											onclick='appEdit("<%=fieldsetId %>","${selfInfoForm.sp_flag}")'
											title="<bean:message key='label.hiremanage.status2'/>"><bean:message key="label.hiremanage.status2"/>
										</a>
										</hrms:priv>
										<hrms:priv func_id="0103010603">      
										<a href="###"
											onclick='delEdit("<%=fieldsetId %>","${selfInfoForm.sp_flag}")'
											title="<bean:message key='button.delete'/>"><bean:message key="button.delete"/>
										</a>
										</hrms:priv>
									</logic:equal>
									<input type="radio" id="viewitem-0"
										onclick='checkFlag("<%=fieldsetId %>","0");' value="0"/>
										<bean:message key='workdiary.message.view.change.infor' />
									<input type="radio" id="viewitem-1"
										onclick='checkFlag("<%=fieldsetId %>","1");' value="1"/>
										<bean:message key='workdiary.message.view.all.infor' />
								</td>
								<td align="right">
									<logic:equal name="selfInfoForm" property="sp_flag" value="01">
										<bean:message key='hire.jp.pos.draftout' />
									</logic:equal>
									<logic:equal name="selfInfoForm" property="sp_flag" value="02">
										<bean:message key='workdiary.message.apped' />
									</logic:equal>
									<logic:equal name="selfInfoForm" property="sp_flag" value="03">
										<bean:message key='label.hiremanage.status3' />
									</logic:equal>
									<logic:equal name="selfInfoForm" property="sp_flag" value="07">
										<bean:message key='button.rejeect2' />
									</logic:equal>
									|
									<logic:equal name="selfInfoForm" property="typeid"
										value="update">
										<bean:message key='label.edit' />
									</logic:equal>
									<logic:equal name="selfInfoForm" property="typeid" value="new">
										<bean:message key='button.new.add' />
									</logic:equal>
									<logic:equal name="selfInfoForm" property="typeid"
										value="insert">
										<bean:message key='button.new.insert' />
									</logic:equal>
									<logic:equal name="selfInfoForm" property="typeid"
										value="delete">
										<bean:message key='button.setfield.delfield' />
									</logic:equal>
								</td>
							</tr>

							<tr>
								<td colspan="3">
									<logic:iterate id="keyitem" name="selfInfoForm"
										property="keylist" indexId="index1">
										<bean:define id="numitem" value="${index1}" />
										<logic:iterate id="typeitem" name="selfInfoForm"
											property="typelist" indexId="index2">
											<logic:equal name="numitem" value="${index2}">
											<%int pagenum=1; %>
												<logic:iterate id="sequenceitem" name="selfInfoForm"
													property="sequenceList" indexId="index3">
													<logic:equal name="numitem" value="${index3}">
														<logic:notEqual name="selfInfoForm" property="keyid"
															value="${keyitem}">
															<a
																href="/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=search&chg_id=${chg_id}&setid=<%=fieldsetId %>&sflag=opens&keyid=<%=keyitem%>&typeid=${typeitem}&sequenceid=<%=sequenceitem%>"><%=pagenum%>
															</a>
														</logic:notEqual>
														<logic:equal name="selfInfoForm" property="keyid"
															value="${keyitem}">
															<logic:equal name="selfInfoForm" property="typeid"
																value="${typeitem}">
																<logic:equal name="selfInfoForm" property="sequenceid"
																	value="${sequenceitem}">
																	<font color="red"><%=pagenum%>
																	</font>
																</logic:equal>
																<logic:notEqual name="selfInfoForm"
																	property="sequenceid" value="${sequenceitem}">
																	<a
																		href="/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=search&setid=<%=fieldsetId %>&chg_id=${chg_id}&sflag=opens&keyid=<%=keyitem%>&typeid=<%=typeitem%>&sequenceid=<%=sequenceitem%>"><%=pagenum%>
																	</a>
																</logic:notEqual>
															</logic:equal>
															<logic:notEqual name="selfInfoForm" property="typeid"
																value="${typeitem}">
																<a
																	href="/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=search&setid=<%=fieldsetId %>&chg_id=${chg_id}&sflag=opens&keyid=<%=keyitem%>&typeid=<%=typeitem%>&sequenceid=<%=sequenceitem%>"><%=pagenum%>
																</a>
															</logic:notEqual>
														</logic:equal>
													</logic:equal>
													<%pagenum++; %>
												</logic:iterate>
											</logic:equal>
										</logic:iterate>
									</logic:iterate>
								</td>
							</tr>

							<tr>
								<td colspan="3">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="100%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable">
							<tr>
								<td width="20%" class="TableRow">
									<bean:message key='workdiary.message.item.name' />
								</td>
								<td width="35%" class="TableRow">
									<bean:message key='workdiary.message.change.former' />
								</td>
								<td width="45%" class="TableRow">
									<bean:message key='workdiary.message.change.after' />
								</td>
							</tr>
							<logic:iterate id="itemelement" name="selfInfoForm"
								property="newFieldList" indexId="index4">
								<bean:define id="itemid" name="itemelement" property="itemid" />
								<bean:define id="itemdesc" name="itemelement"
									property="itemdesc" />
								<bean:define id="itemlength" name="itemelement"
									property="itemlength" />
								<logic:notEqual name="itemelement" property="priv_status" value="0">
								 <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
									<td class="RecordRow">
										${itemdesc}
									</td>
									<td width class="RecordRow"><!-- 【6100】我的变动信息明细，显示全部后，线条的颜色跟主题的颜色不一致  jingq add 2014.12.18 -->
										<logic:equal name="itemelement" property="itemtype" value="M">
											<html:textarea name="selfInfoForm" cols="30" rows="6"
												property='<%="oldFieldList["+index4+"].value"%>'
												readonly="true" styleClass="textSize complex_border_color"></html:textarea>
										</logic:equal>
										<logic:notEqual name="itemelement" property="itemtype"
											value="M">
											<logic:notEqual name="itemelement" property="codesetid"
												value="0">
												<html:text name="selfInfoForm"
													property='<%="oldFieldList["+index4+"].viewvalue"%>' readonly="true" styleClass="textSize complex_border_color" maxlength="${itemelement.itemlength}"/>
											</logic:notEqual>
											<logic:equal name="itemelement" property="codesetid"
												value="0">
												<html:text name="selfInfoForm"
													property='<%="oldFieldList["+index4+"].value"%>'
													readonly="true" styleClass="textSize complex_border_color" />
											</logic:equal>
										</logic:notEqual>
									</td>
									<td class="RecordRow">
										<logic:equal name="itemelement" property="itemtype" value="A">
											<logic:notEqual name="itemelement" property="codesetid"
												value="0">
												<logic:notEqual name="selfInfoForm" property="sp_flag"
													value="01">
													<logic:notEqual name="selfInfoForm" property="sp_flag"
														value="07">
														<html:hidden name="selfInfoForm"
															property='<%="newFieldList["+index4+"].value"%>' />
														<html:text name="selfInfoForm"
															property='<%="newFieldList["+index4+"].viewvalue"%>'
															readonly="true" styleClass="textReadSize complex_border_color" maxlength='${itemelement.itemlength}'/>
													</logic:notEqual>
												</logic:notEqual>
												<logic:equal name="selfInfoForm" property="sp_flag"
													value="01">
													<html:hidden name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>' />
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].viewvalue"%>'
														styleClass="textSize complex_border_color" onchange="fieldcode(this,2)" readonly="true"/>
													<logic:equal name="itemelement" property="priv_status" value="2">
													<img src="/images/code.gif"
														onclick='javascript:openInputCodeDialog("${itemelement.codesetid}","<%="newFieldList["+index4+"].viewvalue"%>");' />&nbsp;
													</logic:equal>
												</logic:equal>
												<logic:equal name="selfInfoForm" property="sp_flag"
													value="07">
													<html:hidden name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>' />
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].viewvalue"%>'
														styleClass="textSize complex_border_color" onchange="fieldcode(this,2)" readonly="true"/>
													<logic:equal name="itemelement" property="priv_status" value="2">
													<img src="/images/code.gif"
														onclick='javascript:openInputCodeDialog("${itemelement.codesetid}","<%="newFieldList["+index4+"].viewvalue"%>");' />&nbsp;
													</logic:equal>
												</logic:equal>
											</logic:notEqual>
											<logic:equal name="itemelement" property="codesetid"
												value="0">
												<logic:notEqual name="selfInfoForm" property="sp_flag"
													value="01">
													<logic:notEqual name="selfInfoForm" property="sp_flag"
														value="07">
														<html:text name="selfInfoForm"
															property='<%="newFieldList["+index4+"].value"%>'
															readonly="true" styleClass="textReadSize complex_border_color" maxlength="${itemelement.itemlength}"/>
													</logic:notEqual>
												</logic:notEqual>
												<logic:equal name="selfInfoForm" property="sp_flag"
													value="01">
													<logic:equal name="itemelement" property="priv_status" value="2">
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>'
														styleClass="textSize complex_border_color" maxlength="${itemelement.itemlength}"/>
													</logic:equal>
													<logic:notEqual name="itemelement" property="priv_status" value="2">
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>'
														styleClass="textReadSize complex_border_color" readonly="true" maxlength="${itemelement.itemlength}"/>
													</logic:notEqual>
												</logic:equal>
												<logic:equal name="selfInfoForm" property="sp_flag"
													value="07">
													<logic:equal name="itemelement" property="priv_status" value="2">
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>'
														styleClass="textSize complex_border_color" maxlength="${itemelement.itemlength}"/>
													 </logic:equal>
													 <logic:notEqual name="itemelement" property="priv_status" value="2">
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>'
														styleClass="textReadSize complex_border_color" readonly="true" maxlength="${itemelement.itemlength}"/>
													 </logic:notEqual>
												</logic:equal>
											</logic:equal>
										</logic:equal>
										<logic:equal name="itemelement" property="itemtype" value="D">
											<logic:notEqual name="selfInfoForm" property="sp_flag"
												value="01">
												<logic:notEqual name="selfInfoForm" property="sp_flag"
													value="07">
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>'
														readonly="true" styleClass="textReadSize complex_border_color"/>
												</logic:notEqual>
											</logic:notEqual>
											<logic:equal name="selfInfoForm" property="sp_flag"
												value="01">
												<logic:equal name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onblur="timeCheck(this);"
													ondblclick="showDateSelectBox(this);" styleClass="textSize complex_border_color"/>
												</logic:equal>
												<logic:notEqual name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onblur="timeCheck(this);"
													ondblclick="showDateSelectBox(this);" styleClass="textReadSize complex_border_color" readonly="true"/>
												</logic:notEqual>
											</logic:equal>
											<logic:equal name="selfInfoForm" property="sp_flag"
												value="07">
												<logic:equal name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onblur="timeCheck(this);"
													ondblclick="showDateSelectBox(this);" styleClass="textSize complex_border_color"/>
												</logic:equal>
												<logic:notEqual name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onblur="timeCheck(this);"
													ondblclick="showDateSelectBox(this);" styleClass="textReadSize complex_border_color" readonly="true"/>
												</logic:notEqual>
											</logic:equal>
										</logic:equal>
										<logic:equal name="itemelement" property="itemtype" value="N">
											<logic:notEqual name="selfInfoForm" property="sp_flag"
												value="01">
												<logic:notEqual name="selfInfoForm" property="sp_flag"
													value="07">
													<html:text name="selfInfoForm"
														property='<%="newFieldList["+index4+"].value"%>'
														readonly="true" styleClass="textReadSize complex_border_color" maxlength='${itemelement.itemlength}'/>
												</logic:notEqual>
											</logic:notEqual>
											<logic:equal name="selfInfoForm" property="sp_flag"
												value="01">
												<logic:equal name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onkeypress="event.returnValue=IsDigit();"
													styleClass="textSize complex_border_color" maxlength='${itemelement.itemlength}'/>
												</logic:equal>
												<logic:notEqual name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onkeypress="event.returnValue=IsDigit();"
													styleClass="textReadSize complex_border_color" readonly="true" maxlength='${itemelement.itemlength}'/>
												</logic:notEqual>
											</logic:equal>
											<logic:equal name="selfInfoForm" property="sp_flag"
												value="07">
												<logic:equal name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onkeypress="event.returnValue=IsDigit();"
													styleClass="textSize complex_border_color" maxlength='<%="newFieldList["+index4+"].itemlength"%>'/>
												</logic:equal>
												<logic:notEqual name="itemelement" property="priv_status" value="2">
												<html:text name="selfInfoForm"
													property='<%="newFieldList["+index4+"].value"%>'
													onkeypress="event.returnValue=IsDigit();"
													styleClass="textReadSize complex_border_color" readonly="true" maxlength='${itemelement.itemlength}'/>
												</logic:notEqual>
											</logic:equal>
										</logic:equal>
										<logic:equal name="itemelement" property="itemtype" value="M">
											<logic:notEqual name="selfInfoForm" property="sp_flag"
												value="01">
												<logic:notEqual name="selfInfoForm" property="sp_flag"
													value="07">
													<html:textarea name="selfInfoForm" cols="30" rows="6"
														property='<%="newFieldList["+index4+"].value"%>'
														readonly="true" styleClass="textReadSize complex_border_color"></html:textarea>
												</logic:notEqual>
											</logic:notEqual>
											<logic:equal name="selfInfoForm" property="sp_flag"
												value="01">
												<logic:equal name="itemelement" property="priv_status" value="2">
												<html:textarea name="selfInfoForm" cols="30" rows="6"
													property='<%="newFieldList["+index4+"].value"%>'
													styleClass="textSize complex_border_color"></html:textarea>
												</logic:equal>
												<logic:notEqual name="itemelement" property="priv_status" value="2">
												<html:textarea name="selfInfoForm" cols="30" rows="6"
													property='<%="newFieldList["+index4+"].value"%>'
													styleClass="textReadSize complex_border_color" readonly="true"></html:textarea>
												</logic:notEqual>
											</logic:equal>
											<logic:equal name="selfInfoForm" property="sp_flag"
												value="07">
												<logic:equal name="itemelement" property="priv_status" value="2">
												<html:textarea name="selfInfoForm" cols="30" rows="6"
													property='<%="newFieldList["+index4+"].value"%>'
													styleClass="textSize complex_border_color"></html:textarea>
												</logic:equal>
												<logic:notEqual name="itemelement" property="priv_status" value="2">
												<html:textarea name="selfInfoForm" cols="30" rows="6"
													property='<%="newFieldList["+index4+"].value"%>'
													styleClass="textReadSize complex_border_color" readonly="true"></html:textarea>
												</logic:notEqual>
											</logic:equal>
										</logic:equal>
									</td>
								</tr>
								</logic:notEqual>
							</logic:iterate>
						</table>
					</td>
				</tr>
				<logic:notEmpty name="selfInfoForm" property="multimediaInfoList">
				<tr>
					<td>
						<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
						<tr>
							<td class="TableRow" colspan="2" align="left" valign="middle" style="border-top: none;">附件 </td>
						</tr>
						<logic:iterate id="media" name="selfInfoForm" property="multimediaInfoList" indexId="index2">
	                         <bean:define id="mediatype" name="media" property="fileType"/>
	                         <bean:define id="filePath" name="media" property="filePath"/>
	                         <bean:define id="filename" name="media" property="fileName"/>
	                         <logic:equal value="new"  name="mediatype">
	                           <tr>
	                              <td class="recordRow" style="border-top: none;" nowrap width=30>新增</td>
	                              <td class="recordRow" style="border-top: none;border-left: none;" nowrap>
	                              	<a href="/servlet/DisplayOleContent?filePath=<%= PubFunc.encrypt(filePath+"/"+filename)%>" target="_blank">
	                              		<bean:write name="media" property="topic"/>
	                               	</a>
	                              </td>
	                           </tr>
	                         </logic:equal>
	                         <logic:equal value="delete" name="mediatype">
	                            <tr>
	                               <td class="recordRow" style="border-top: none;" nowrap width=30>删除</td>
	                               <td class="recordRow" style="border-top: none;border-left: none;" nowrap>
	                               		<bean:write name="media" property="topic"/>
	                               </td>
	                            </tr>
	                         </logic:equal>
	                    </logic:iterate>
						</table>
					</td>
				</tr>
				</logic:notEmpty>
			</logic:equal>
			<logic:notEqual name="selfInfoForm" property="setid"
				value="<%=fieldsetId %>">
				 <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
					<td>
						<table width="100%" border="0" cellspacing="0" align="center"
							cellpadding="0">
							<tr>
								<td>
									<a
										href="/selfservice/selfinfo/iframapp.do?b_query=link&savEdit=search&setid=<%=fieldsetId %>&sflag=opens&chg_id=${chg_id}"
										title="显示"> ${fieldsetdesc} <img
											src="/images/button_vert2.gif" border="0">
									</a>
								</td>
								<td align="right">
									<logic:equal name="element" property="changeflag" value="01">
										<bean:message key='hire.jp.pos.draftout' />
									</logic:equal>
									<logic:equal name="element" property="changeflag" value="02">
										<bean:message key='workdiary.message.apped' />
									</logic:equal>
									<logic:equal name="element" property="changeflag" value="03">
										<bean:message key='label.hiremanage.status3' />
									</logic:equal>
									<logic:equal name="element" property="changeflag" value="07">
										<bean:message key='button.rejeect2' />
									</logic:equal>
									|
									<logic:equal name="element" property="moduleflag"
										value="update">
										<bean:message key='label.edit' />
									</logic:equal>
									<logic:equal name="element" property="moduleflag" value="new">
										<bean:message key='button.insert' />
									</logic:equal>
									<logic:equal name="element" property="moduleflag"
										value="insert">
										<bean:message key='button.new.insert' />
									</logic:equal>
									<logic:equal name="element" property="moduleflag"
										value="delete">
										<bean:message key='button.setfield.delfield' />
									</logic:equal>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</logic:notEqual>
			<%m++;%>
		</logic:iterate>
	</table>
	<%if(m<1){%>
	<script language="JavaScript">
backMain();
</script>
	<%}%>
	<table width="80%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<tr>
			<td align="center">
				&nbsp;
			</td>
		</tr>
		<tr>
			<td align="center">
				<input type="button" value="<bean:message key='button.leave'/>"
					onclick="backMain();" class="mybutton">
			</td>
		</tr>
	</table>
	<div id="date_panel">
		<select name="date_box" multiple="multiple" size="10"
			style="width:120" onchange="setSelectValue();"
			onclick="setSelectValue();">
			<option value="1992.04.12">
				1992.04.12
			</option>
			<option value="1992.04">
				1992.04
			</option>
			<option value="1992">
				1992
			</option>
			<option value="1992-04-12">
				1992-04-12
			</option>
			<option value="1992-04">
				1992-04
			</option>
		</select>
	</div>
</html:form>
<script language="JavaScript">
Element.hide('date_panel');
document.getElementById("viewitem-${selfInfoForm.viewitem}").checked='checked';
</script>
