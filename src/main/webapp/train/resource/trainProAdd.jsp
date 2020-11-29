<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.train.resource.TrainProjectForm" %>
<script language="javascript" src="/train/resource/trainResc.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="javascript">
	function fieldcode2(sourceobj)
	{
	
	　var　targetobj,target_name,hidden_name,hiddenobj;
   	  target_name=sourceobj.name;    
      hidden_name=target_name.replace(".viewvalue",".value");       	
      var hiddenInputs=document.getElementsByName(hidden_name);
      if(hiddenInputs!=null)    
    	hiddenobj=hiddenInputs[0];
     hiddenobj.value=sourceobj.value;	
	}
	function openOrgInfo(codeid,mytarget,check,flag){
	var managerstr ="";
	if(check==2){
			managerstr=trainProjectForm.orgparentcode.value;
	}else if(check==3){
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
	//检查必填项
	function save(oper,type){
		<logic:iterate  id="element" name="trainProjectForm" property="fields" indexId="index">
			<logic:equal name="element" property="state" value="1">
    			<%FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element");
        			boolean isFillable=abean1.isFillable();%>   
    			var itemid='<bean:write name="element" property="itemid"/>';
    			var itemvalue="";
    			<logic:equal name="element" property="codesetid" value="0">
        			itemvalue = document.getElementById(itemid).value;
    			</logic:equal>
    			<logic:notEqual name="element" property="codesetid" value="0">
        			itemvalue = document.getElementById(itemid+"_value").value;
    			</logic:notEqual>
    			<logic:notEqual name="element" property="itemid" value="b0110">
	    			<logic:notEqual name="element" property="itemtype" value="M">
	    				if(<%=isFillable%> && !itemvalue){
	        				alert('<bean:write name="element" property="itemdesc"/>不能为空！');
	        				return;
	    				}
    				</logic:notEqual>
    				<logic:equal name="element" property="itemtype" value="M">
	    				if(<%=isFillable%> && !itemvalue){
	        				alert('<bean:write name="element" property="itemdesc"/>不能为空！');
	        				return;
	    				} else {
	    					if(IsOverStrLength(itemvalue,2000)){
	    						alert('<bean:write name="element" property="itemdesc"/>'
	    	    						+TRAIN_ROOM_MORE_LENGTH1+2000+TRAIN_ROOM_MORE_LENGTH2+1000+TRAIN_ROOM_MORE_LENGTH3);
	    						return;
	    					}
		    			}
	    				
					</logic:equal>
    			</logic:notEqual>
    			
    			<logic:equal name="element" property="itemid" value="b0110">
    			//单位不能为空
    				if(!itemvalue){
    					alert('<bean:write name="element" property="itemdesc"/>不能为空！');
    					return;
					}
				</logic:equal>
		</logic:equal>
    </logic:iterate>
    save2(oper,type);
	}

	function chackLength(obj,name){
		if(IsOverStrLength(obj.value,2000)){
			alert(name+TRAIN_ROOM_MORE_LENGTH1+2000+TRAIN_ROOM_MORE_LENGTH2+1000+TRAIN_ROOM_MORE_LENGTH3);
		}
	}
</script>
<%
	TrainProjectForm form = (TrainProjectForm)session.getAttribute("trainProjectForm");
	int len = form.getFields().size();  
	String privalue = request.getParameter("priFldValue");
 %>
<hrms:themes></hrms:themes>
<html:form action="/train/resource/trainProAdd">
	<input type="hidden" id="priFldValue" value="<%=privalue%>">
	<input type="hidden" id="code" value="${param.code}">
	<input type="hidden" value="${trainProjectForm.r1301}" id="r3101value"/>  
	<!--<input type="hidden" id="priFldValue" value=""> -->
	<html:hidden name="trainProjectForm" property="orgparentcode" />
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center">
					<tr height="20">
						<td colspan="4" align="left" class="TableRow">	
							&nbsp;&nbsp;<bean:message key='train.type.title'/>
						</td>
					</tr>
					<tr class="trDeep">
					<%int i=0,j=0; %>
						<logic:iterate  id="element" name="trainProjectForm" property="fields" indexId="index">
						<%
						FieldItemView abean = (FieldItemView) pageContext.getAttribute("element");
                        boolean isFillable1 = abean.isFillable();
						if(i==2){ %>
							<%if(j%2 == 0){%>
						</tr><tr class="trShallow">
						<%}else{%>
						</tr><tr class="trDeep">
						<%}i=0;j++;} %>
							<logic:notEqual name="element" property="state" value="0">
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
										<logic:notEqual name="element" property="itemtype" value="N">	
										<logic:notEqual name="element" property="itemtype" value="D">							
											<html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite"
												name="trainProjectForm" styleId="${element.itemid}" property='<%="fields[" + index + "].value"%>' /> 								
											<logic:equal name="element" property="itemid" value="r1302">
											<font color='red'>*</font>&nbsp;
											</logic:equal>
										</logic:notEqual>	
										</logic:notEqual>	
											<logic:equal name="element" property="itemtype" value="N">	
												<logic:equal name="element" property="decimalwidth" value="0">
														<input type="text" name='<%="fields[" + index + "].value"%>' maxlength="${element.itemlength}" size="29"  id="${element.itemid}"  class="textColorWrite"  style="font-size:10pt;text-align:left;width: 222px;"
															onblur='isNumber(this);' onkeypress="event.returnValue=IsDigit1(this);" value="${element.value}">	
												</logic:equal>
												<logic:notEqual name="element" property="decimalwidth" value="0">
													<input type="text" name='<%="fields[" + index + "].value"%>' maxlength="${element.itemlength}" size="29"  id="${element.itemid}"  class="textColorWrite"  style="font-size:10pt;text-align:left;width: 222px;"
															onblur='isNumber(this);' onkeypress="event.returnValue=IsDigit1(this);" value="${element.value}">	
												</logic:notEqual>																		
										   </logic:equal>
											<logic:equal name="element" property="itemtype" value="D">	
												<input type="text" name='<%="fields[" + index + "].value"%>' maxlength="${element.itemlength}" size="29"  id="${element.itemid}" extra="editor"  class="textColorWrite"  style="font-size:10pt;text-align:left;width: 222px;"
														dropDown="dropDownDate" onchange="if(!checkdate(this,'${element.itemdesc}','${element.itemlength}')) {this.focus(); this.value=''; }" value="${element.value}">																		
										   </logic:equal>	
									</logic:equal>		
									
									<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
										 	<html:hidden name="trainProjectForm" 	property='<%="fields[" + index + "].value"%>' onchange="fieldcode2(this)" styleId="${element.itemid}_value"/>  
											<html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite" readonly="true"  styleId="b0110" 
													name="trainProjectForm" property='<%="fields[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													   />	
										<img src="/images/code.gif" align=absmiddle
											onclick='javascript:openOrgInfo("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>",2,"1");'/>
					                      <font color='red'>*</font>&nbsp;
									</logic:equal>
										 							  
										 <logic:notEqual name="element" property="itemid" value="b0110">
																			 
												<html:hidden name="trainProjectForm" property='<%="fields[" + index + "].value"%>' styleId="${element.itemid}_value"/>  
												<html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite" 
													name="trainProjectForm" property='<%="fields[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													    styleId="${element.itemid}" /> 	
												<logic:notEqual name="element" property="itemid" value="r1308">
			 										<img src="/images/code.gif" align=absmiddle onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fields[" + index + "].viewvalue"%>");' />&nbsp;
			 								</logic:notEqual>
			 							</logic:notEqual>
									</logic:notEqual>									
											<%i++; 
											if (isFillable1) {
	                                            %> 
	                                            <logic:notEqual name="element" property="itemid" value="b0110">
	                                            <font color='red'>*</font>
	                                            </logic:notEqual>
	                                         <%} %>
											
								</td>
								<%if(index<len-1) {	%>
								<logic:equal name="trainProjectForm" property='<%="fields[" + (index+1) + "].itemtype"%>' value="M">
									<%if(i<2){ %>
									<td align="left" class="RecordRow_inside" style="border-top: none;" nowrap >&nbsp;</td>
									<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >&nbsp;</td>
									<%i++; }%>
									
								</logic:equal>
								<%} else if(index==len-1){%>
									<%if(i<2){ %>
									<td align="left" class="RecordRow_inside" nowrap >&nbsp;</td>
									<td align="left" class="RecordRow_right" nowrap >&nbsp;</td>
									<%i++; }%>		
								<%} %>
							</logic:notEqual>
							<logic:equal name="element" property="itemtype" value="M">
								<td align="right" class="RecordRow_left" style="border-top: none;" nowrap  valign="top" >
									<bean:write name="element" property="itemdesc" filter="true" />
								</td>
								<td align="left" class="RecordRow_right" style="border-top: none;" nowrap  colspan="3">
									<html:textarea name="trainProjectForm" onchange="chackLength(this,'${element.itemdesc}');"
										property='<%="fields[" + index + "].value"%>'
										cols="90" rows="6" styleClass="textboxMul" styleId="${element.itemid}"></html:textarea>
										<%if(isFillable1){%>
										<logic:notEqual name="element" property="itemid" value="r1302">
                            			<logic:notEqual name="element" property="itemid" value="b0110">
                            				<font color='red'>*</font>
                            			</logic:notEqual>	
                            			</logic:notEqual>
                            	<%}%>
								</td>
								<%i=2; %>
							</logic:equal>
							</logic:notEqual>
							<logic:equal name="element" property="state" value="0">
							     <input type="hidden" id="${element.itemid}" value="${element.value}" />							
                            </logic:equal>
						</logic:iterate>
						</tr>				
		</table>

				<table width='100%' align='center' cellpadding="0" cellspacing="0">
					<tr>
						<td align="center" style="padding-top: 10px;" style="border-top: none;">
              <logic:equal name="trainProjectForm" property="dispSaveContinue" value="true">
                <input type='button' value='<bean:message key='button.save' />' class="mybutton" onclick="save('saveClose',${trainProjectForm.dispSaveContinue });">
              </logic:equal>						
						  <logic:notEqual name="trainProjectForm" property="dispSaveContinue" value="true">
                <hrms:priv func_id="3230002" module_id="">						
							    <input type='button' value='<bean:message key='button.save' />'	class="mybutton" onclick="save('saveClose',${trainProjectForm.dispSaveContinue });">
							  </hrms:priv>
							</logic:notEqual>
							<logic:equal name="trainProjectForm" property="dispSaveContinue" value="true">
							  <input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="save('saveContinue',${trainProjectForm.dispSaveContinue });" Class="mybutton">
							</logic:equal>
							<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onClick="freshMain2();">  							
						</td>
					</tr>
				</table>
</html:form>
<script>
	var obj = $('r1301');
	if(obj) {
		var priFldValue=$('priFldValue');
		obj.readOnly="true";
		obj.className="textColorRead";
		priFldValue.value=obj.value;
	}
	
	obj = $('r1308');
	if(obj) {
		obj.className="textColorRead";
		obj.readOnly="true";
		
	}
</script>