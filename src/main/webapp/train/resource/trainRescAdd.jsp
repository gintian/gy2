<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.train.resource.TrainResourceForm,com.hjsj.hrms.valueobject.common.FieldItemView"%>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
<script language="javascript" src="/train/resource/trainResc.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<%
String viewunit="1";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null)
{
    if(userView.getStatus()==4||userView.isSuper_admin())
      viewunit="0";
}
/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
if(userView.getStatus()==0&&!userView.isSuper_admin()){
  String codeall = userView.getUnit_id();
  if(codeall==null||codeall.length()<3)
    viewunit="0";
}
%>

<script language="javascript">
	function fieldcode2(sourceobj)
	{	
	  var targetobj,target_name,hidden_name,hiddenobj;
   	  target_name=sourceobj.name;    
      hidden_name=target_name.replace(".viewvalue",".value");       	
      var hiddenInputs=document.getElementsByName(hidden_name);
      if(hiddenInputs!=null)    
    	hiddenobj=hiddenInputs[0];
     hiddenobj.value=sourceobj.value;	
	}
	
	function rt(){
		document.forms[0].action="/train/resource/trainroom/selftrainroom.do?b_query=return&type=self";
		document.forms[0].submit();
	}
	
	function openOrgInfo(codeid,mytarget,check,flag)
	{
		var managerstr ="";
		if(check==2)
		{
			managerstr=trainResourceForm.orgparentcode.value;
		}
		else if(check==3)
		{
			managerstr=document.getElementById("depid").value;
	    }

	    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
	    if(mytarget==null)
	      return;
	    var oldInputs=document.getElementsByName(mytarget);
	    oldobj=oldInputs[0];
	    target_name=oldobj.name;
	    hidden_name=target_name.replace(".viewvalue",".value"); 
	    hidden_name=hidden_name.replace(".hzvalue",".value");
	       
	    var hiddenInputs=document.getElementsByName(hidden_name);
	      
	    if(hiddenInputs!=null)
	    {
	    	hiddenobj=hiddenInputs[0];
	    	codevalue=managerstr;
	    }
	    
	    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
	    thecodeurl="/system/untrain.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
	    var popwin= window.showModalDialog(thecodeurl, theArr, 
	        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}
	
	function save0(oper)
	{
		var b0110 = $F('b0110');
		if(b0110=='')
		{
			alert(SEL_UN);
			return;
		}
		<% int m=0; %>
		<logic:iterate  id="element1"    name="trainResourceForm"  property="fields" indexId="index"> 
		<%
			FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
		    boolean isFillable=abean1.isFillable();	
		%>		
			var aa<%=m%>=document.getElementsByName("fields[<%=m%>].value");
			if(<%=isFillable%>)
			{
				if(aa<%=m%>[0].value=="")
				{
					alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
					return;						
				}
				<logic:equal name="element1" property="itemtype" value="N">
				checkNumItem("${element1.itemid}",${element1.itemlength},${element1.decimalwidth});
				</logic:equal>
			}
			
			<logic:equal name="element1" property="itemtype" value="M">
			if(<%=isFillable%> && !aa<%=m%>[0].value){
				alert('<bean:write name="element1" property="itemdesc"/>不能为空！');
				return;
			} else {
				if(IsOverStrLength(aa<%=m%>[0].value,2000)){
					alert('<bean:write name="element1" property="itemdesc"/>'
    						+TRAIN_ROOM_MORE_LENGTH1+2000+TRAIN_ROOM_MORE_LENGTH2+1000+TRAIN_ROOM_MORE_LENGTH3);
					return;
				}
			}
			
			</logic:equal>	
			<% m++; %>
		</logic:iterate>
		save(oper);
		
	}
	
	//组织机构树如果显示人员，则先显示人员库
	function select_org_emp_dialog22(flag,selecttype,dbtype,priv,isfilter,loadtype)
	{
	   if(priv!=0)
	      priv=1;
	      
		var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+"`nmodule=6`viewunit=<%=viewunit %>"+
		           "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);  
		 
		var return_vo= window.showModalDialog(iframe_url,1, 
		   "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	   return return_vo;
	}
	
	function get_hand_query()
	 {
	    var return_vo;
	    return_vo = select_org_emp_dialog22("1","2","0","1","1","0"); //select_org_emp_dialog4("1","1","0","1","0","1","","","${trainExamStudentForm.examDBPres}",""); 
	    
	      
	     if(return_vo)
	     {
	     	var hashvo=new ParameterSet();  
	        var sid=return_vo.content;
	        
	        var nbase = sid.substr(0,3);
	        var a0100 = sid.substr(3,8);
	        var title = return_vo.title;
	        
	        var obj = document.getElementById('r0402');
	        obj.value = title;
	        
	        obj = document.getElementById('nbase');

	        if(null!=obj)
	          obj.value = nbase;

	        obj = document.getElementById('a0100');
	        if(null!=obj)
	          obj.value = a0100;        
	        
  			hashvo.setValue("nbase",nbase);//指标关联
  			hashvo.setValue("a0100",a0100);
   			var request=new Request({method:'post',onSuccess:saveOk,functionId:'2020030100'},hashvo);
	    }
	 }  
	 
	function saveOk(outparamters){
	    var isCheck = outparamters.getValue("isCheck");
		if(isCheck != null && isCheck != ""){           
		    alert(isCheck);
		    document.getElementById('r0402').value = "";
		    document.getElementById('nbase').value = "";
		    document.getElementById('a0100').value = "";
		    return;
		}
		 
	 	var dest = outparamters.getValue("dest");
	 	var values = outparamters.getValue("values");
	 	if(dest != null){	 		
		 	var dests = dest.split(",");
		 	var value = values.split(",");	 		
		 
		 	var v;
		 	var code;
		 	for(var i = 0 ; i < value.length-1 ; i ++){
		 		v = value[i];
			 	if(v != null && v != "" && v != "null"){
			 		code = v.split("|");
					var desid = dests[i].split(":");
					if(desid.length !=2 || desid[1]=="0")
						continue;
					
			 		if (code.length==0 || code[0]==null || code[0]=="")
			 			continue;
			 		
			 		if(code.length > 1){
			 			document.getElementById(desid[0]).value = code[1];
			 			document.getElementById("h_"+desid[0]).value = code[0];
			 		}else if(code.length == 1){
			  			document.getElementById(desid[0]).value = code[0];	 			
			 		}
			 	}
		 	}
		 }
	 }
	 
	 function selTeacherEnabled()
	 {
	 	var r0412 = document.getElementById('r0412').value;
	 	if ((null!=r0412)&&("01"!=r0412.substring(0, 2))){
	 		$("img_r0412").style.display="none";
	 		document.getElementById('nbase').value = "";
	 		document.getElementById('a0100').value = "";
	 		document.getElementById('r0402').setAttribute("readOnly",false);
		} else{
	 		$("img_r0412").style.display="inline";
	 		document.getElementById('r0402').value="";
	 		document.getElementById('nbase').value = "";
	 		document.getElementById('a0100').value = "";
	 		document.getElementById('r0402').setAttribute("readOnly",true);
	 	}
	 }
	 
	 function chackLength(obj,name){
			if(IsOverStrLength(obj.value,2000)){
				alert(name+TRAIN_ROOM_MORE_LENGTH1+2000+TRAIN_ROOM_MORE_LENGTH2+1000+TRAIN_ROOM_MORE_LENGTH3);
			}
		}
</script>
<%
	TrainResourceForm form = (TrainResourceForm) session.getAttribute("trainResourceForm");
	int len = form.getFields().size();
	String teachertype = (String)form.getTeachertype();
	if (null == teachertype)
		teachertype="02";
%>
<html:form action="/train/resource/trainRescAdd">
	<input type="hidden" id="type" value="${param.type}">
	<html:hidden name="trainResourceForm" property="orgparentcode" />
	<html:hidden name="trainResourceForm" property="primaryField"	styleId="priFld" />
	<html:hidden name="trainResourceForm" property="nbase"	styleId="nbase" />
	<html:hidden name="trainResourceForm" property="a0100"	styleId="a0100" />
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0" align="center" >
					<tr height="20">
						<td colspan="4" align="left" valigh="bottom" class="TableRow">
							&nbsp;&nbsp;
							<bean:write name="trainResourceForm" property="recName" />
							&nbsp;
						</td>
					</tr>
					<tr class="trDeep">
						<%
						int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="trainResourceForm" property="fields" indexId="index">
						<%
						    FieldItemView abean = (FieldItemView) pageContext.getAttribute("element");
						    boolean isFillable1 = abean.isFillable();
						    if (i == 2)
						    {
							    if (j % 2 == 0)
							    {
						%>
						
					</tr>
					<tr class="trShallow">
						<%
							} else
							{
						%>
					</tr>
					<tr class="trDeep">
						<%
							}
							i = 0;
							j++;
								    }
						%>
						<logic:notEqual name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow_left" style="border-top: none;" nowrap>
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<% if(i==1){ %>
								<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >
								<%}else{ %>
								<td align="left" class="RecordRow_inside" style="border-top: none;" nowrap >
								<%} %>
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										
										<logic:equal name="element" property="itemtype" value="N">
											
											<logic:equal name="element" property="decimalwidth" value="0">
												<html:text maxlength="4" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit2(this);"
													onblur="checkNumItem('${element.itemid}',${element.itemlength},${element.decimalwidth});" name="trainResourceForm"
													styleId="${element.itemid}" 
													property='<%="fields[" + index + "].value"%>' />
											</logic:equal>
											
											<logic:notEqual name="element" property="decimalwidth"
												value="0">
												<html:text maxlength="13" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit1(this);"
													onblur="checkNumItem('${element.itemid}',${element.itemlength},${element.decimalwidth});" name="trainResourceForm"
													styleId="${element.itemid}"
													property='<%="fields[" + index + "].value"%>' />
											</logic:notEqual>
											
					                      <% if (isFillable1) { %> 
					                      &nbsp;<font color='red'>*</font>&nbsp;
					                      <% } %>
										</logic:equal>
										
										<logic:notEqual name="element" property="itemtype" value="N">				  
												<bean:define id="fid" value="${element.itemid}"></bean:define>
												<%if("r0106".equalsIgnoreCase(fid)||"r0410".equalsIgnoreCase(fid)||"r1009".equalsIgnoreCase(fid)){ %>
												<html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite"
													name="trainResourceForm" styleId="${element.itemid}"
													property='<%="fields[" + index + "].value"%>' />
												<%}else if("r0402".equalsIgnoreCase(fid)) { %>
												  <html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite"
                                                      name="trainResourceForm" styleId="${element.itemid}"
                                                      property='<%="fields[" + index + "].value"%>' />
                                                      <img id="img_r0412" src="/images/code.gif" 
                                                          style="vertical-align: middle;
                                                          <% if (!teachertype.startsWith("01")){ %>
                                                          	display:none;
                                                          <% } %>
                                                          "
                                                          onclick='javascript:get_hand_query();'/>
												<%}else{ %>
												<html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite"
													name="trainResourceForm" styleId="${element.itemid}"
													property='<%="fields[" + index + "].value"%>' />
												<% } %>
						                      <% if (isFillable1) { %> 
						                      &nbsp;<font color='red'>*</font>&nbsp;
						                      <% } %>
						                  </logic:notEqual>
										
									</logic:notEqual>
									
									<logic:equal name="element" property="itemtype" value="D">
										<input type="text" 
											name='<%="fields[" + index + "].value"%>'
											maxlength="50" id="${element.itemid}"
											extra="editor" class="editor"
											style="font-size: 10pt; text-align: left; width: 200px;"
											dropDown="dropDownDate" value="${element.value}"
											onchange=" if(!checkdate(this,'${element.itemdesc}','${element.itemlength}')) {this.focus(); this.value=''; }">
					                      <% if (isFillable1) { %> 
					                      &nbsp;<font color='red'>*</font>&nbsp;
					                      <% } %>
									</logic:equal>								
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
									 	<html:hidden name="trainResourceForm" 	property='<%="fields[" + index + "].value"%>' onchange="fieldcode2(this)" />  
											<html:text maxlength="50" size="30" styleClass="textColorWrite" styleId="b0110" 
													name="trainResourceForm" property='<%="fields[" + index + "].viewvalue"%>' 
													onchange="fieldcode(this,2)" readonly="true"/>	
									  	<img src="/images/code.gif" style="vertical-align: middle;"
										       onclick='javascript:openOrgInfo("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>",2,"1");'/>
					                      <% if (isFillable1) { %> 
					                      &nbsp;<font color='red'>*</font>&nbsp;
					                      <% } %>
					                      	 
									</logic:equal>
									
									<logic:equal name="element" property="itemid" value="r0708">
										<html:hidden name="trainResourceForm"	property='<%="fields[" + index + "].value"%>' />
										<html:text maxlength="50" size="30" styleClass="textColorWrite"
											name="trainResourceForm"
											property='<%="fields[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true"/>
										<img src="/images/code.gif"
											onclick='javascript:openKhTargetCardInputCode("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>");'  style="vertical-align: middle;"/>
							              <% if (isFillable1)  { %> 
							              &nbsp;<font color='red'>*</font>&nbsp;
							              <% } %>
									</logic:equal>
									
									
									<logic:equal name="element" property="itemid" value="r0700">
										<html:hidden name="trainResourceForm"	property='<%="fields[" + index + "].value"%>' />
										<html:text maxlength="50" size="30" styleClass="textColorWrite"
											name="trainResourceForm"
											property='<%="fields[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
										<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialogOrgInputPos2("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>");'  style="vertical-align: middle;"/>
							              <% if (isFillable1)  { %> 
					                     &nbsp;<font color='red'>*</font>&nbsp;
					                     <% } %>
									</logic:equal>
									
									<logic:equal name="element" property="itemid" value="r0412">
										<html:hidden styleId="r0412" name="trainResourceForm"	property='<%="fields[" + index + "].value"%>' 
											onchange="javascript:selTeacherEnabled();"/>
										<html:text maxlength="50" size="30" styleClass="textColorWrite"
											name="trainResourceForm" styleId="${element.itemid}"
											property='<%="fields[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true"/>
										<img src="/images/code.gif"
											onclick='javascript:openKhTargetCardInputCode("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>");'  style="vertical-align: middle;"/>
							              <% if (isFillable1)  { %> 
							              &nbsp;<font color='red'>*</font>&nbsp;
							              <% } %>
									</logic:equal>
									
									<logic:notEqual name="element" property="itemid" value="r0412">
										<logic:notEqual name="element" property="itemid" value="r0700">
										  <logic:notEqual name="element" property="itemid" value="r0708">
												<logic:notEqual name="element" property="itemid" value="b0110">
												  <html:hidden name="trainResourceForm" property='<%="fields[" + index + "].value"%>' styleId="h_${element.itemid}"/>  
												  <html:text maxlength="50" size="30" styleClass="textColorWrite" readonly="true"  
														name="trainResourceForm" styleId="${element.itemid}" property='<%="fields[" + index + "].viewvalue"%>' 
														onchange="fieldcode(this,2)"/>
				                                     <img src="/images/code.gif" style="vertical-align: middle;"
				                                        onclick='javascript:openKhTargetCardInputCode("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>");'"/>
						 							    <% if (isFillable1) { %> 
								                      	&nbsp;<font color='red'>*</font>&nbsp;
								                		<% } %>
				 							  </logic:notEqual>
										  </logic:notEqual>	
										</logic:notEqual>	
									</logic:notEqual>
								</logic:notEqual>
							  <% i++; %>
							</td>
							<%
						    if (index.intValue() < len - 1)
						    {
							%>
							<logic:equal name="trainResourceForm"
								property='<%="fields[" + Integer.toString(index.intValue() + 1) + "].itemtype"%>'
								value="M">
								<%
									if (i < 2)
									{
								%>
								<td align="left" class="RecordRow_inside" style="border-top: none; border-left: 1px solid #c4d8ee;" nowrap >&nbsp;</td>
								<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >&nbsp;</td>
								<%
									i++;
									}
								%>

							</logic:equal>
							<%
						    } else if (index.intValue() == len - 1)
						    {
							%>
							<%
								if (i < 2)
								{
							%>
							<td align="left" class="RecordRow_inside" style="border-top: none;border-left: 1px solid #c4d8ee;" nowrap >&nbsp;</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >&nbsp;</td>
							<%
								i++;
								}
							%>
							<%
							}
							%>
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow_left" style="border-top: none;" nowrap valign="top">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap colspan="3">
								<html:textarea name="trainResourceForm" onchange="chackLength(this,'${element.itemdesc}');"
									property='<%="fields[" + index + "].value"%>' styleId="${element.itemid}"
									cols="90" rows="6" styleClass="textboxMul">
								</html:textarea>
								<%
										    if (isFillable1)
										    {
								%>
								&nbsp;
								<font color='red'>*</font>&nbsp;
								<%
								}
								%>
							</td>
							<%
							i = 2;
							%>
						</logic:equal>
						</logic:iterate>
					</tr>
				</table>

				<table width='100%' align='center' cellpadding="0" cellspacing="0">
					<tr>
						<td align='left' style="padding-top: 5px;">
						  <logic:notEqual name="trainResourceForm" property="dispSaveContinue" value="true">
							  <logic:equal name="trainResourceForm" property="type" value="1">
				        <hrms:priv func_id="3230102" module_id="">  
                  <input type='button' value='<bean:message key='button.save' />'
                    class="mybutton" onclick="save0('saveClose');">
				        </hrms:priv>				        
				       </logic:equal>
				       <logic:equal name="trainResourceForm" property="type" value="2">
				        <hrms:priv func_id="3230202" module_id="">  
                  <input type='button' value='<bean:message key='button.save' />'
                    class="mybutton" onclick="save0('saveClose');">
				        </hrms:priv>				        
				       </logic:equal>
				       <logic:equal name="trainResourceForm" property="type" value="3">
				       <logic:equal name="trainResourceForm" property="aa" value="2">
				        <hrms:priv func_id="3230302" module_id="">  
                  <input type='button' value='<bean:message key='button.save' />'
                    class="mybutton" onclick="save0('saveClose');">
				        </hrms:priv>	
				        </logic:equal>			        
				       </logic:equal>
				       <logic:equal name="trainResourceForm" property="type" value="4">
				        <hrms:priv func_id="3230402" module_id="">  
                  <input type='button' value='<bean:message key='button.save' />'
                    class="mybutton" onclick="save0('saveClose');">
				        </hrms:priv>
				       </logic:equal>
				       <logic:equal name="trainResourceForm" property="type" value="5">
				        <hrms:priv func_id="3230506" module_id="">  
				          <input type='button' value='<bean:message key='button.save' />'
                    class="mybutton" onclick="save0('saveClose');">
				        </hrms:priv>
				       </logic:equal>
              </logic:notEqual>

              <logic:equal name="trainResourceForm" property="dispSaveContinue" value="true">
								<input type='button' value='<bean:message key='button.save' />'
									class="mybutton" onclick="save0('saveClose');">
									<input type="button"
										value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>"
										onclick="save0('saveContinue');" Class="mybutton">
							</logic:equal>
							<logic:notEqual name="trainResourceForm" property="aa" value="3">
							<input type="button" class="mybutton"
								value="<bean:message key='button.return'/>"
								onClick="freshMain('${trainResourceForm.a_code}');">
							</logic:notEqual>
							<logic:equal name="trainResourceForm" property="aa" value="3">
							<input type="button" class="mybutton"
								value="<bean:message key='button.return'/>"
								onClick="rt();">
							</logic:equal>
						</td>
					</tr>
				</table>
		</td>
	</table>
</html:form>
<!-- 
<script>
	var priFld = $F('priFld');
	var obj = $(priFld);
	obj.readOnly="true";
	obj.className="textColorRead";
	obj.style.width = "200px";
</script> -->