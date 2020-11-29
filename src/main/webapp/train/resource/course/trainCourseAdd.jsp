<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hjsj.hrms.actionform.train.resource.course.CourseForm,com.hjsj.hrms.valueobject.common.FieldItemView,java.util.List,com.hrms.hjsj.sys.FieldItem"%>
<script language="javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<!-- script type="text/javascript" src="/js/validate.js">
</script -->
<!--
<style>
.textbox{border:solid 1px #98C2E8;}
.textboxMul{border:solid 1px #98C2E8;}
</style>
-->
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
	function openOrgInfo(codeid,mytarget,check,flag){
	var managerstr ="";
	if(check==2){
		managerstr=document.getElementById("companyid").value;
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
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
}
function save0(oper)
{
    var filex = document.getElementById('file').value;
      if(!validateUploadFilePath(filex)){
			alert(ERROR_FILE_UPLOADFAIL);
			return;
         }
	var value0 = document.getElementById('r5003').value;	
	if(value0 == null || value0 == ""){
		alert('课程名称不允许为空!');
	}else{
	<% int m=0; %>
	<logic:iterate  id="element1"    name="courseForm"  property="itemlist" indexId="index"> 
	<%
		FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
	    boolean isFillable=abean1.isFillable();	
	%>  
		var aa<%=m%>=document.getElementsByName("itemlist[<%=m%>].value")
		if(<%=isFillable%>)
		{
			if(aa<%=m%>[0].value=="")
			{
				alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
				return;						
			}
		}
		
		 <%if("A".equals(abean1.getItemtype())  &&  "0".equals(abean1.getCodesetid()) ){ %>
			if(IsOverStrLength(aa<%=m%>[0].value,aa<%=m%>[0].maxLength)){
				alert("<bean:write  name="element1" property="itemdesc"/>只能输入"+aa<%=m%>[0].maxLength+"个字母、数字或"+aa<%=m%>[0].maxLength/2+"个汉字！");
				aa<%=m%>[0].value="";
				aa<%=m%>[0].focus();
				return;
			}	
 		<%}	%>
		<% m++; %>
	</logic:iterate>


	var r5030=document.getElementById('r5030');
	var r5031=document.getElementById('r5031');
	if (r5030!=null && r5031!=null && r5030.value!="" && r5031.value!="") {
		if (r5030.value>r5031.value) {
            alert("有效日期止不能早于有效日期起！");
            return;
		}
	}

	
	save(oper);
	}
}


function checkInput(currentid){
	if("r5026"!=currentid)
		return;
	var examBySelf=document.getElementById("r5024_viewvalue").value;
	var examCount=document.getElementById(currentid).value;

	if("否"==examBySelf){
		if(examCount!=0){
			alert("您选择了不允许自考，所以不允许填写自考次数！");
			document.getElementById(currentid).value=0;
		}
		
	}
}

function isenable(){
	if(typeof(eval("document.all.r5037_viewvalue"))!= "undefined"){
		document.getElementById("r5037_viewvalue").disabled="true";
		document.getElementById("r5037_viewvalue").className="textColorRead";
	}
	<logic:notEqual value="04" name="courseForm" property="r5022">
		if(typeof(eval("document.all.img_r5037"))!= "undefined")
			document.getElementById("img_r5037").style.display="none";
		if(typeof(eval("document.all.img_r5004"))!= "undefined")
			document.getElementById("img_r5004").style.display="none";
	</logic:notEqual>
	if(typeof(eval("document.all.r5004_viewvalue"))!= "undefined"){
	    document.getElementById("r5004_viewvalue").disabled="true";
	    document.getElementById("r5004_viewvalue").className="textColorRead";
	}
}

function getCodeItemDesc(codesetid,itemid){
	openKhTargetCardInputCode(codesetid,itemid);
	//选择是否允许自考后，再去校验
	checkInput('r5026');
}


function CheckFile(obj){

 	if(!validateUploadFilePath(obj.value)){
		alert(ERROR_FILE_UPLOADFAIL);
			return;
      }
         
 	var img  =null;
 	img = document.createElement("img");
 	var f = GetFileExt(obj.value);
 	
 	if(!/^.*\.(jpg|png)$/i.test(f))
	{
	obj.outerHTML = obj.outerHTML;  
		alert("只能上传jpg或png格式图片!")
	return
	}
	var fName = obj.value;
	var stu = fName.lastIndexOf("\\");
	fName = fName.substring(stu+1);
	if(fName.length>18){
    	fName = fName.substring(0,17);
		fName=fName+"...";
	}
	document.getElementById("filepath").value = fName;
	document.getElementById("delePath").innerHTML="";
	document.getElementById("delePath").innerHTML="<a href=\"###\"  onclick=\"dete()\">删除图片</a>";
}
 
 
 function GetFileExt(filepath) {
            if (filepath != "") {
                var pos = "." + filepath.replace(/.+\./, "");
                return pos;
            }
}
	function dete(){
		document.getElementById("filepath").value="";
	    var obj=document.getElementById("file");
	    obj.outerHTML = obj.outerHTML;
	    document.getElementById("delePath").innerHTML="";
	}
	
	
window.onload=function(){
	var path = document.getElementById("filepath").value;
    if(path.length>0){
    	var delePath = document.getElementById("delePath");
    	if(delePath != null)
    		delePath.innerHTML="<a href=\"###\"  onclick=\"dete()\">删除图片</a>";
    }
	document.getElementsByTagName("body")[0].onkeydown =function(e){            
        //获取事件对象
        var event = e?e:window.event;
		var elem = event.relatedTarget || event.srcElement || event.target ||event.currentTarget;   
        if(event.keyCode==8){//判断按键为backSpace键  
			//获取按键按下时光标做指向的element  
            var elem = event.srcElement || event.currentTarget;   
            //判断是否需要阻止按下键盘的事件默认传递  
            var name = elem.nodeName;  
            if(name!='INPUT' && name!='TEXTAREA'){  
            	return _stopIt(event);  
            }  
            var type_e = elem.type.toUpperCase();  
            if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){  
            	return _stopIt(event);
            }  
            if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)){  
            	return _stopIt(event);  
            }  
        }  
    };
};  
function _stopIt(e){  
	if(e.returnValue){  
		e.returnValue = false ;  
    }  
    if(e.preventDefault ){  
        e.preventDefault();  
    }                 
    return false;
}

function maxLengthTip(obj){
	if(IsOverStrLength(obj.value,obj.maxLength)){
		alert("只能输入"+obj.maxLength+"个字母、数字或"+obj.maxLength/2+"个汉字！");
		obj.value="";
		obj.focus();
 	}
}
</script>
<%
	CourseForm form = (CourseForm) session.getAttribute("courseForm");
	int len = form.getItemlist().size();
	List itemlist = form.getItemlist();
	for (int i = 0; i < itemlist.size(); i++) {
		FieldItem field = (FieldItem) itemlist.get(i);			
	}
%>
<html:form action="/train/resource/course"  enctype="multipart/form-data" >   
	<bean:define id="cid" name="courseForm" property="id"></bean:define>
	<html:hidden name="courseForm" property="id" value="${cid}"/>
	<html:hidden name="courseForm" property="a_code1" />
	<html:hidden name="courseForm" property="a_code" styleId="acode"/>
	<table width="96%" align="center" border="0" cellpadding="0"
		cellspacing="0" style="margin-top: 8px;">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTable">
					<tr height="20">
						<td colspan="4" align="left" valigh="bottom" class="TableRow">
							&nbsp;&nbsp;培训课程记录&nbsp;
						</td>
					</tr>
					<tr class="trDeep">
						<%
							int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="courseForm" property="itemlist"
							indexId="index">
							<%
								FieldItemView abean = (FieldItemView) pageContext.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
												if (i == 2) {
							%>
							<%
								if (j % 2 == 0) {
							%>
						
					</tr>
					<tr class="trShallow">
						<%
							} else {
						%>
					</tr>
					<tr class="trDeep">
						<%
							}
												i = 0;
												j++;
											}
						%>
						<logic:notEqual name="element" property="state" value="0">
						<logic:notEqual name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow_left" nowrap>
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
								<% if(i==1){ %>
								<td align="left" class="RecordRow_right" nowrap >
								<%}else{ %>
								<td align="left" class="RecordRow_inside" nowrap >
								<%} %>
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
										 <bean:define id="num" name="element" property="itemlength"></bean:define>
											<logic:equal name="element" property="decimalwidth" value="0">
												<html:text maxlength="<%=num.toString() %>" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit2(this);"
													onblur="checkInput('${element.itemid}');isNumber(this);"
													name="courseForm" 
													styleId="${element.itemid}"
													property='<%="itemlist["
														+ index + "].value"%>' />
											</logic:equal>
											<logic:notEqual name="element" property="decimalwidth"
												value="0">
												<html:text maxlength="<%=num.toString() %>" size="30" styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit(this);"
													onblur='isNumber(this);' name="courseForm"
													styleId="${element.itemid}"
													property='<%="itemlist["
														+ index + "].value"%>' />
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
										 <bean:define id="num" name="element" property="itemlength"></bean:define>
											<html:text maxlength="<%=num.toString() %>" size="30" styleClass="textColorWrite"
												name="courseForm" styleId="${element.itemid}" 
												property='<%="itemlist[" + index
													+ "].value"%>'  onblur="maxLengthTip(this)"/>
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="itemtype" value="D">
										<logic:notEqual value="04" name="courseForm" property="r5022">
										<input type="text"
											name='<%="itemlist[" + index
												+ "].value"%>'
											maxlength="50" size="29" id="${element.itemid}"
											extra="editor" class="textColorWrite"
											style="font-size: 10pt; text-align: left;width: 200px;"
											dropDown="dropDownDate" value="${element.value}"
											onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }">
										</logic:notEqual>
										<logic:equal value="04" name="courseForm" property="r5022">
											<input type="text"
											name='<%="itemlist[" + index + "].value"%>'
											maxlength="50" size="29" id="${element.itemid}"  class="textColorWrite"
											style="font-size: 10pt; text-align: left;width: 200px;"
											value="${element.value}" readonly="readonly">
										</logic:equal>
									</logic:equal>
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
										<html:hidden name="courseForm"
											property='<%="itemlist[" + index
												+ "].value"%>'
											onchange="fieldcode2(this)" />
										<html:text maxlength="50" size="30" styleClass="textColorRead"
											name="courseForm"
											property='<%="itemlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
									</logic:equal>
									<logic:notEqual name="element" property="itemid" value="b0110">
									   <logic:notEqual name="element" property="itemid" value="r5004">
										<html:hidden name="courseForm" styleId="${element.itemid}_value"
											property='<%="itemlist[" + index + "].value"%>' />
									   </logic:notEqual>
									   <logic:equal name="element" property="itemid" value="r5004">
									   	<bean:define id="r5004" name="courseForm" property='<%="itemlist[" + index + "].value"%>' />
									   	<% String codeid = SafeCode.encode(PubFunc.encrypt(r5004.toString())); %>
											<html:hidden name="courseForm" styleId="${element.itemid}_value"
											property='<%="itemlist[" + index + "].value"%>' value="<%=codeid %>" />
									    </logic:equal>
										<html:text maxlength="50" size="30" styleClass="textColorRead" styleId="${element.itemid}_viewvalue"
											name="courseForm" property='<%="itemlist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2);" readonly="true"/>
									</logic:notEqual>
								</logic:notEqual>
								<%
									i++;
								%>
								<logic:equal name="element" property="itemid" value="r5004">
									<!-- <img src="/images/code.gif"
										onclick='javascript:openInputCodeDialogOrgInputPos1("55","<%="itemlist[" + index + "].viewvalue"%>","","1");' /> -->
									<logic:notEqual value="04" name="courseForm" property="r5022">
										<img src="/images/code.gif" id="img_${element.itemid}"
											onclick='javascript:openInputCodeDialogOrgInputPos1("55","<%="itemlist[" + index + "].viewvalue"%>","","1");'  style="vertical-align: middle;"/>
									</logic:notEqual>
								</logic:equal>
								<logic:equal name="element" property="itemid" value="r5020">
									<logic:notEqual value="04" name="courseForm" property="r5022">
										<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>","${courseForm.orgparentcode }","1");'  style="vertical-align: middle;"/>
									</logic:notEqual>
								</logic:equal>
								<logic:equal name="element" property="itemtype" value="A">
								<logic:notEqual name="element" property="codesetid" value="0">
								<logic:notEqual name="element" property="itemid" value="r5004">
								<logic:notEqual name="element" property="itemid" value="r5020">
									<logic:notEqual value="04" name="courseForm" property="r5022">
										<img src="/images/code.gif" id="img_${element.itemid}"
											onclick='javascript:getCodeItemDesc("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
									</logic:notEqual>
								</logic:notEqual>
								</logic:notEqual>
								</logic:notEqual>
								</logic:equal>
								<!-- 
								<logic:equal name="element" property="itemid" value="r5014">
									<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:equal>
								<logic:equal name="element" property="itemid" value="r5016">
									<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:equal>
								<logic:equal name="element" property="itemid" value="r5024">
									<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:equal>
								<logic:equal name="element" property="itemid" value="r5018">
									<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
												+ "].viewvalue"%>");'  style="vertical-align: middle;"/>
								</logic:equal>
								 -->
								<%
												if (isFillable1) {
											%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
							</td>
							<%
								if (index.intValue() < len - 1) {
							%>
							<logic:equal name="courseForm"
								property='<%="itemlist["
												+ Integer.toString(index
														.intValue() + 1)
												+ "].itemtype"%>'
								value="M">
								<%
									if (i < 2) {
								%>
								<td align="left" class="RecordRow_inside" nowrap ></td>
							<td align="left" class="RecordRow_right" nowrap ></td>
								<%
									i++;
																}
								%>

							</logic:equal>
							<%
								} else if (index.intValue() == len - 1) {
							%>
							<%
								if (i < 2) {
							%>
							<td align="left" class="RecordRow_inside" nowrap ></td>
							<td align="left" class="RecordRow_right" nowrap ></td>
							<%
								i++;
														}
							%>
							<%
								}
							%>
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow_left" nowrap valign="top">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow_right" nowrap colspan="3">
									<html:textarea name="courseForm"
									property='<%="itemlist[" + index
											+ "].value"%>'
									cols="90" rows="6" styleClass="textboxMul"></html:textarea>
								<%
									if (isFillable1) {
								%>
								&nbsp;
								<font color='red'>*</font>&nbsp;<%
									}
								%>
							</td>
							<%
								i = 2;
							%>
						</logic:equal>
						</logic:notEqual>
					    
						</logic:iterate>

					</tr>
					<tr class="trShallow">
						<td   align="right"  class="RecordRow_left" >
							<bean:message key='conlumn.infopick.educate.img' />
						</td> 
						<td colSpan="3" align="left" class="RecordRow_right" nowrap >
							<span  style="display:inline-block;float:left;">
								<html:text  styleClass="textColorWrite" property='filepath' styleId="filepath" value="${courseForm.imagename}" style="float:left;margin-right:10;" readonly="true"/>
									<span id="upfile" style="color:#549FE3;display:inline-block; position:relative;overflow:hidden;vertical-align: middle;margin-right:10" href="###" >
										<logic:notEqual value="04" name="courseForm" property="r5022">
										<logic:notEqual value="1" name="courseForm" property="isP">
                        					<html:button styleClass="button" property="scan" ><bean:message key='button.view' />...</html:button>
                        				</logic:notEqual>
						   	 			</logic:notEqual> 
										<html:file  property="file"  value="" styleId="file"  onchange ="CheckFile(this)"  size="20px" style="position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;"/>
									 </span>
									 	<logic:notEqual value="04" name="courseForm" property="r5022">
										<logic:notEqual value="1" name="courseForm" property="isP">
										<span id="delePath">
									 	</span>
									 	
									 	</logic:notEqual>
						   	 			</logic:notEqual> 
								</span>
							</td>
					    </tr>
				</table>
				<table width='100%' align='center' cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" style="padding-top: 5px;">
						<logic:notEqual value="04" name="courseForm" property="r5022">
							<logic:notEqual value="1" name="courseForm" property="isP">
							<logic:empty name="cid">
							<hrms:priv func_id="32306C05" module_id="">
								<input type='button' value='<bean:message key='button.save' />'
									class="mybutton" onclick="save0('saveClose');">
							</hrms:priv>
							</logic:empty>
							<logic:notEmpty name="cid">
							<hrms:priv func_id="32306C06" module_id="">
							<input type='button' value='<bean:message key='button.save' />'
									class="mybutton" onclick="save0('saveClose');">
							</hrms:priv>
							</logic:notEmpty>
							</logic:notEqual>
						</logic:notEqual>
							<input type="button" class="mybutton"
								value="<bean:message key='button.return'/>"
								onClick="returnback();">
						</td>
					</tr>
				</table>
				</html:form>
				<script type="text/javascript">
				isenable();
				<logic:equal value="04" name="courseForm" property="r5022">
				var ips=document.getElementsByTagName('input');
				for(var i=0;i<ips.length;i++){
					if(ips[i].getAttribute('type').toLowerCase()=='text')
						ips[i].readOnly=true;
				}

				var textarea=document.getElementsByTagName('textarea');
				for(var i=0;i<textarea.length;i++){
					textarea[i].readOnly=true;
				}
				</logic:equal>
				</script>
