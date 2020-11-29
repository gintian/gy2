<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.general.kanban.KanBanForm" %>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/train/traincourse/trainAdd.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<link href="/css/css1_template.css" rel="stylesheet" type="text/css">
<style type="text/css">
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 60px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input {
	width: 18px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
#scroll_box {
    border: 0px solid #eee;
    height: 200px;    
    width: 100%;            
    overflow: auto;            
    margin-top:0px;
}
</style>
<script type="text/javascript">
//保存
function save(){	
	var fillable = "${kanBanForm.filltable}";
	if(fillable!=null&&fillable.length>1){
		var flag = "1";
		var arr = fillable.split("`");
		for(var i=0;i<arr.length;i++){
			var id = arr[i];
			if(id!=null&&id.length>1){
				var itemarr = id.split("::");
				if(itemarr.length>1){
					var values = "";
					if(document.getElementById(itemarr[0])!=null){
						values = document.getElementById(itemarr[0]).value;	
						if(values==null||values.length<1){
							alert(itemarr[1]+"不能为空!");
							flag = "0";
							document.getElementById(itemarr[0]).focus();
							break;
						}
					}
				}
			}
		}
		if(flag=="0")
			return false;
	}
	kanBanForm.action="/general/kanban/kanban.do?b_query=link&checkflag=${kanBanForm.checkflag}&p0500=${kanBanForm.p0500}";
	kanBanForm.submit();
}
function saveSubmit(){	
	var fillable = "${kanBanForm.filltable}";
	
	if(fillable!=null&&fillable.length>1){
		var flag = "1";
		var arr = fillable.split("`");
		for(var i=0;i<arr.length;i++){
			var id = arr[i];
			if(id!=null&&id.length>1){
				var itemarr = id.split("::");
				if(itemarr.length>1){
					var values = "";
					if(document.getElementById(itemarr[0])!=null){
						values = document.getElementById(itemarr[0]).value;	
						if(values==null||values.length<1){
							alert(itemarr[1]+"不能为空!");
							flag = "0";
							document.getElementById(itemarr[0]).focus();
							break;
						}
					}
				}
			}
		}
		if(flag=="0")
			return false;
	}
	kanBanForm.action="/general/kanban/kanban.do?b_query=link&subs=1&checkflag=${kanBanForm.checkflag}&p0500=${kanBanForm.p0500}";
	kanBanForm.submit();
}
function blackKanban(){
	kanBanForm.action="/general/kanban/kanban.do?b_query=link";
	kanBanForm.submit();
}
function selectPerson(){
	var reutrn_value=select_org_emp_dialog(1,2,0,0,0,0);
	 if(reutrn_value!=null){
	 	 var values = reutrn_value.content;
	 	 if(values!=null&&values.length>3){
	 	 	var persons = values.substring(0,3)+"::"+values.substring(3)+"::"+reutrn_value.title;
	 	 	document.getElementById("person").value=persons;
	 	 	document.getElementById("a0101_0").value=reutrn_value.title;
	 	 }
	 }
}
function selectPerson1(){
	var reutrn_value=select_org_emp_dialog(1,2,0,0,0,0);;
	 if(reutrn_value!=null){
		 var values = reutrn_value.content;
		 if(values!=null&&values.length>3){
	 		var checkperson = values.substring(0,3)+"::"+values.substring(3)+"::"+reutrn_value.title;
	 		document.getElementById("checkperson").value=checkperson;
	 		document.getElementById("a0101_1").value=reutrn_value.title;
	 	}
	 }
}
function IsInteger(str){        
    if(str.length!=0){     
        reg=/^[-+]?\d*$/;      
        if(!reg.test(str)){     
            alert("请输入正整数类型!");//请将“整数类型”要换成你要验证的那个属性名称！     
            return false;
        }     
    }
    return true;     
}
function setHiddenValue(itemid,obj,cht){
	var htvalue = document.getElementById(itemid).value;
	var htviewvalue="";
	if(htvalue!=null){
		var htArr = htvalue.split(":");
		if(htArr.length==3){
			if(cht=="h"){
				htviewvalue+=obj.value+":"+htArr[1]+":"+htArr[2];
			}else if(cht=="m"){
				htviewvalue+=htArr[0]+":"+obj.value+":"+htArr[2];
			}else if(cht=="mm"){
				htviewvalue+=htArr[0]+":"+htArr[1]+":"+obj.value;
			}
		}else{
			if(cht=="h"){
				htviewvalue+=obj.value+":00:00";
			}else if(cht=="m"){
				htviewvalue+="00:"+obj.value+":00";
			}else if(cht=="mm"){
				htviewvalue+="00:00:"+obj.value;
			}
		}
	}else{
		if(cht=="h"){
			htviewvalue+=obj.value+":00:00";
		}else if(cht=="m"){
			htviewvalue+="00:"+obj.value+":00";
		}else if(cht=="mm"){
			htviewvalue+="00:00:"+obj.value;
		}
	}
	document.getElementById(itemid).value=htviewvalue;
}
function IsDigit(obj) {
	if((event.keyCode >= 46) && (event.keyCode <= 57)){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
	}else{
		return false;
	}
}   
this.fObj = null;
var time_r=0; 
function setFocusObj(obj,time_vv) {		
	this.fObj = obj;
	time_r=time_vv;		
}
function IsInputTimeValue() {	     
	event.cancelBubble = true;
	var fObj=this.fObj;		
	if (!fObj) return;		
	var cmd = event.srcElement.innerText=="5"?true:false;
	if(fObj.value==""||fObj.value.lenght<=0)
		fObj.value="0";
	var i = parseInt(fObj.value,10);		
	var radix=parseInt(time_r,10)-1;				
	if (i==radix&&cmd) {
		i = 0;
	} else if (i==0&&!cmd) {
		i = radix;
	} else {
		cmd?i++:i--;
	}	
	if(i==0){
	  fObj.value = "00"
	}else if(i<10&&i>0){
		fObj.value="0"+i;
	}else{
		fObj.value = i;
	}			
	fObj.select();
} 
//输入整数
function isBigNumBer(obj) {
var checkOK = "-0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		alert(INPUT_NUMBER_VALUE+'!');
  		obj.value=''; 
  	    obj.focus();
  	}  	   
	if(obj.value>100){
		alert('请输入小于100的数值!');
  		obj.value=''; 
  	    obj.focus();
	}
	else if(obj.value<0){
		alert('请输入大于0的数值!');
  		obj.value=''; 
  	    obj.focus();
	}	
}
var itemid="";
var chselectps="0";
var chclear="0";
function query(itemids){
	itemid = itemids;
	 chselectps="0";
   	 var a0101=document.getElementById(itemids).value;
   	 var hashvo=new ParameterSet();	
     hashvo.setValue("a0101",a0101);			
     var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'2020050034'},hashvo); 
}
function showA0101(outparamters){
	var objlist=outparamters.getValue("objlist");
	if(objlist!=null){
		AjaxBind.bind($('a0101_box'),objlist);		
	}
}
function onSelectValue(){
	chclear="1";
}
function setSelectValue(){
	var objid,i;
	var obj=$('a0101_box');
	if(obj.options.length<1){
		document.getElementById(itemid).value="";
		Element.hide('a0101_pnl');         
		return false;
	}
	for(i=0;i<obj.options.length;i++){
          if(obj.options[i].selected)
            objid=obj.options[i].value
	}       
	if(objid){
		var arr = objid.split("::");
		if(arr.length==3){
			if(itemid=="a0101_1"){
				document.getElementById("checkperson").value=objid;
	 			document.getElementById("a0101_1").value=arr[2];
			}else{
				document.getElementById("person").value=objid;
	 			document.getElementById("a0101_0").value=arr[2];
			}
		}	   
	}else{
		document.getElementById(itemid).value="";
	}
	chselectps = "1";
    Element.hide('a0101_pnl');         
}
function checkslPerson(){
	if(chselectps=="0"){
		if(document.getElementById(itemid)!=null){
			if(itemid=="a0101_1"){
				document.getElementById("checkperson").value="";
	 			document.getElementById("a0101_1").value="";
			}else{
				document.getElementById("person").value="";
	 			document.getElementById("a0101_0").value="";
			}
		}
	} 
	if(chclear=="0"){
		Element.hide('a0101_pnl'); 
	} 
}
function clearsl(){
	if(chselectps=="0"){
		if(document.getElementById(itemid)!=null){
			if(itemid=="a0101_1"){
				document.getElementById("checkperson").value="";
	 			document.getElementById("a0101_1").value="";
			}else{
				document.getElementById("person").value="";
	 			document.getElementById("a0101_0").value="";
			}
		}
	} 
	Element.hide('a0101_pnl');  
	chclear="0";
}

function showSelectBox(srcobj){
   Element.show('a0101_pnl');   
   var pos=getAbsPosition(srcobj);
   with($('a0101_pnl')){
	   	style.position="absolute";
   		style.posLeft=pos[0]-1;
 		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
    }                 
} 

</script>
<%
	KanBanForm form = (KanBanForm)session.getAttribute("kanBanForm");
	int len = form.getFieldlist().size();  
 %>
<html:form action="/general/kanban/kanban">
<table width="94%" align="center" border="0" cellpadding="0" cellspacing="0">
<%int j=0; %>
	<tr>
		<td valign="top">
			<table width="100%" border="0" cellpadding="0" cellspacing="0"　align="center" class="ListTable">
				<tr height="20">
					<td colspan="2" align="left" class="TableRow">${kanBanForm.kbtitle}</td>
				</tr>
				<logic:equal name="kanBanForm" property="checkflag" value="add">
				<tr height="20" class="trDeep">
					<td align="right" class="RecordRow" >发单人</td>
					<td align="left" class="RecordRow">&nbsp;${kanBanForm.billperson}</td>
				</tr>
				</logic:equal>
				<logic:equal name="kanBanForm" property="checkflag" value="update">
				<tr height="20" class="trDeep">
					<td align="right" class="RecordRow" >发单人</td>
					<td align="left" class="RecordRow">&nbsp;${kanBanForm.billperson}</td>
				</tr>
				</logic:equal>
				<logic:notEqual name="kanBanForm" property="checkflag" value="add">
				<logic:notEqual name="kanBanForm" property="checkflag" value="update">
				<tr height="20" class="trDeep">
					<td align="right" class="RecordRow" >接单人</td>
					<td align="left" class="RecordRow">&nbsp;${kanBanForm.billperson}</td>
				</tr>
				</logic:notEqual>
				</logic:notEqual>
				<logic:iterate  id="element" name="kanBanForm" property="fieldlist" indexId="index">
				<logic:notEqual name="element" property="itemid" value="p0500">
				<logic:notEqual name="element" property="itemid" value="a0100">
				<logic:notEqual name="element" property="itemid" value="nbase">
				<%if(j%2 == 0){%>
				<tr class="trShallow">
				<%}else{%>
				<tr class="trDeep">
				<%}j++;%>
				<logic:notEqual name="element" property="itemtype" value="M">
					<td align="right" class="RecordRow" nowrap>
						<logic:equal name="element" property="priv_status" value="1">
							<logic:equal name="element" property="fillable" value="true">
							<font color='red'>*</font>
							</logic:equal>
						</logic:equal>
						<bean:write name="element" property="itemdesc" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap>
						<logic:equal name="element" property="codesetid" value="0">
						<logic:notEqual name="element" property="itemtype" value="D">
						<logic:notEqual name="element" property="itemtype" value="N">	
							<logic:equal name="element" property="itemid" value="a0101_0">	
							<logic:equal name="element" property="priv_status" value="1">																	
							<html:text maxlength="30" size="20"
								name="kanBanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' onblur="checkslPerson();" onkeyup="query('a0101_0');showSelectBox(this);"/> 																																
							<img id='imga0101_0' src="/images/code.gif" onclick='selectPerson()' align="absmiddle"/>&nbsp;
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">	
							&nbsp;${element.value}														
							</logic:notEqual>
							</logic:equal>
							<logic:equal name="element" property="itemid" value="a0101_1">	
							<logic:equal name="element" property="priv_status" value="1">															
							<html:text  size="20" 
								name="kanBanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' onblur="checkslPerson();" onkeyup="query('a0101_1');showSelectBox(this);"/> 																																
							<img id='imga0101_1' src="/images/code.gif" onclick='selectPerson1()' align="absmiddle"/>&nbsp;
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">	
							&nbsp;${element.value}																
							</logic:notEqual>
							</logic:equal>
							<logic:notEqual name="element" property="itemid" value="a0101_0">
							<logic:notEqual name="element" property="itemid" value="a0101_1">	
							<logic:equal name="element" property="priv_status" value="1">																					
							<html:text maxlength="${element.itemlength}" size="20" styleClass="textbox" 
								name="kanBanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' /> 																																
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">	
							&nbsp;${element.value}																
							</logic:notEqual>
							</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>																									
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="N">
						<logic:equal name="element" property="itemid" value="p0507">
						<logic:equal name="element" property="priv_status" value="1">				
							<html:text maxlength="3" size="20" styleClass="textbox" onkeypress="event.returnValue=IsDigit2(this);" onblur='isBigNumBer(this);'
								name="kanBanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' />%
						</logic:equal>
						<logic:notEqual name="element" property="priv_status" value="1">	
							<logic:notEqual name="element" property="value" value="0">
							<logic:notEqual name="element" property="value" value="">
    						<img width="${element.value}px" height="10" src='/images/board_bottom_1.gif' />${element.value}%
    						</logic:notEqual>
    						</logic:notEqual>
    						<logic:equal name="element" property="value" value="0">
    						&nbsp;
    						</logic:equal>
    						<logic:equal name="element" property="value" value="">
    						&nbsp;
    						</logic:equal>
    					</logic:notEqual>
						</logic:equal>
						<logic:notEqual name="element" property="itemid" value="p0507">
						<logic:equal name="element" property="decimalwidth" value="0">
							<logic:equal name="element" property="priv_status" value="1">	
							<html:text maxlength="10" size="20" styleClass="textbox" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'
								name="kanBanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' />
							</logic:equal>	
							<logic:notEqual name="element" property="priv_status" value="1">
							&nbsp;${element.value}				
							</logic:notEqual>	  						
						</logic:equal>												
						<logic:notEqual name="element" property="decimalwidth" value="0">
							<logic:equal name="element" property="priv_status" value="1">	
							<html:text maxlength="20" size="20" styleClass="textbox" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);'
								name="kanBanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' /> 								
							</logic:equal>	
							<logic:notEqual name="element" property="priv_status" value="1">
							&nbsp;${element.value}				
							</logic:notEqual>	
						</logic:notEqual>
						</logic:notEqual>
						</logic:equal>	
						<logic:equal name="element" property="itemtype" value="D">
							<logic:equal name="element" property="itemid" value="p0501">
							<logic:equal name="element" property="priv_status" value="1">	
							<bean:define id="nvalue" name='element' property='viewvalue'/>
							<table border="0" cellspacing="0" align="left" cellpadding="0">
							<tr><td>
							<html:hidden name="kanBanForm" property='<%="fieldlist[" + index + "].viewvalue"%>' styleId="${element.itemid}_viewvalue"/>  
							<input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="23"  id="${element.itemid}" extra="editor"  styleClass="textbox"
								dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">																			
							</td>
							<td>&nbsp;时分秒：</td>
							<td width="65">
							<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
		             		<tr>
		               		<td width="40" nowrap style="background-color:#FFFFFF";> 
		                	<div class="m_frameborder">
		                 	<input type="text" class="m_input" maxlength="2" name="${element.itemid}_time_h" id="start_h2" value="<%=nvalue.toString().substring(0,2)%>" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);" onblur="setHiddenValue('${element.itemid}_viewvalue',this,'h');"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="${element.itemid}_time_m" id="start_m2" value="<%=nvalue.toString().substring(3,5)%>" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);" onblur="setHiddenValue('${element.itemid}_viewvalue',this,'m');"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="${element.itemid}_time_mm" id="start_mm2" value="<%=nvalue.toString().substring(6,8)%>" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);" onblur="setHiddenValue('${element.itemid}_viewvalue',this,'mm');">
		               		</div>
		             		</td>
		             		<td>
		             		<table border="0" cellspacing="2" cellpadding="0">
		                	<tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		                	<tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		             		</table>
		            		</td>
		            		</tr>
		       				</table> 
	       					</td>
							</tr>
							</table>
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">
							&nbsp;${element.value}&nbsp;${element.viewvalue}				
							</logic:notEqual>
							</logic:equal>
							<logic:equal name="element" property="itemid" value="p0502">
							<logic:equal name="element" property="priv_status" value="1">	
							<bean:define id="nvalue" name='element' property='viewvalue'/>
							<table border="0" cellspacing="0" align="left" cellpadding="0">
							<tr><td>	
							<html:hidden name="kanBanForm" property='<%="fieldlist[" + index + "].viewvalue"%>' styleId="${element.itemid}_viewvalue"/>  
							<input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="23"  id="${element.itemid}" extra="editor"  styleClass="textbox"
								dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">																			
							</td>
							<td>&nbsp;时分秒：</td>
							<td width="65">
							<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
		             		<tr>
		               		<td width="40" nowrap style="background-color:#FFFFFF";> 
		                	<div class="m_frameborder">
		                 	<input type="text" class="m_input" maxlength="2" name="${element.itemid}_time_h" id="start_h2" value="<%=nvalue.toString().substring(0,2)%>" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);" onblur="setHiddenValue('${element.itemid}_viewvalue',this,'h');"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="${element.itemid}_time_m" id="start_m2" value="<%=nvalue.toString().substring(3,5)%>" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);" onblur="setHiddenValue('${element.itemid}_viewvalue',this,'m');"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="${element.itemid}_time_mm" id="start_mm2" value="<%=nvalue.toString().substring(6,8)%>" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);" onblur="setHiddenValue('${element.itemid}_viewvalue',this,'mm');">
		               		</div>
		             		</td>
		             		<td>
		             		<table border="0" cellspacing="2" cellpadding="0">
		                	<tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		                	<tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		             		</table>
		            		</td>
		            		</tr>
		       				</table> 
	       					</td>
							</tr>
							</table>
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">
							&nbsp;${element.value}&nbsp;${element.viewvalue}			
							</logic:notEqual>
							</logic:equal>
							<logic:notEqual name="element" property="itemid" value="p0501">
							<logic:notEqual name="element" property="itemid" value="p0502">
							<logic:equal name="element" property="priv_status" value="1">
							<input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="23"  id="${element.itemid}" extra="editor"  styleClass="textbox"
								dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">																			
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">
							&nbsp;${element.value}				
							</logic:notEqual>
							</logic:notEqual>
							</logic:notEqual>
						</logic:equal>	
						</logic:equal>									
						<logic:notEqual name="element" property="codesetid" value="0">		
							<logic:equal name="element" property="priv_status" value="1">							
							<html:hidden name="kanBanForm" property='<%="fieldlist[" + index + "].value"%>' styleId="${element.itemid}_value"/>  
							<html:text maxlength="${element.itemlength}" size="20" styleClass="textbox" 
								name="kanBanForm" property='<%="fieldlist[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
								styleId="${element.itemid}" />
			 				<img id='img${element.itemid}' src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>");' align="absmiddle"/>&nbsp;
							</logic:equal>
							<logic:notEqual name="element" property="priv_status" value="1">
							&nbsp;${element.viewvalue}				
							</logic:notEqual>
						</logic:notEqual>									
					</td>
				</logic:notEqual>
				<logic:equal name="element" property="itemtype" value="M">
					<logic:notEqual name="element" property="itemid" value="p0509">
					<td align="right" class="RecordRow" nowrap valign="center" >
						<bean:write name="element" property="itemdesc" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap>
						<logic:equal name="element" property="priv_status" value="1">
						<html:textarea name="kanBanForm" styleId="${element.itemid}"
							property='<%="fieldlist[" + index + "].value"%>'
							cols="64" rows="4" styleClass="textboxMul"></html:textarea>
						</logic:equal>
						<logic:notEqual name="element" property="priv_status" value="1">
							${element.value}	
						</logic:notEqual>
					</td>
					</logic:notEqual>
					<logic:equal name="element" property="itemid" value="p0509">
					<td align="right" class="RecordRow" nowrap  valign="center" >
						<bean:write name="element" property="itemdesc" filter="true" />
					</td>
					<td class="RecordRow">
						<div id="scroll_box">${element.value}</div>
					</td>
					<logic:equal name="element" property="priv_status" value="1">
					</tr>
					<%if(j%2 == 0){%>
						<tr class="trShallow">
					<%}else{%>
						<tr class="trDeep">
					<%}j++;%>
					<td align="right" class="RecordRow" nowrap  valign="top" >
						发言
					</td>
					<td align="left" class="RecordRow" nowrap>
						<html:textarea name="kanBanForm" styleId="${element.itemid}"
							property='<%="fieldlist[" + index + "].value"%>'
							cols="64" rows="4" value="" styleClass="textboxMul"></html:textarea>
					</td>
					</logic:equal>
					</logic:equal>
				</logic:equal>
				</tr>
				</logic:notEqual>
				</logic:notEqual>
				</logic:notEqual>
			</logic:iterate>			
	</table>
	<table width='100%' align='center'>
		<tr>
			<td align='center' style="height:35px;">
				<logic:equal name="kanBanForm" property="checkflag" value="fill">
				<input type='button' value='保存' class="mybutton" onclick="save();">&nbsp;&nbsp;
				<input type='button' value='提交' class="mybutton" onclick="saveSubmit();">&nbsp;&nbsp;
				<input type="button" class="mybutton" value="返回" onClick="blackKanban();"> 
				</logic:equal>
				<logic:notEqual name="kanBanForm" property="checkflag" value="fill">
				<input type='button' value='确定' class="mybutton" onclick="save();">&nbsp;&nbsp;
				<input type="button" class="mybutton" value="返回" onClick="blackKanban();">   
				</logic:notEqual>								
			</td>
		</tr>
	</table>
	</td></tr>
</table>
<div id="a0101_pnl" style="border-style:nono;display:none">
  	<select name="a0101_box" multiple="multiple" size="10" class="dropdown_frame" onblur="clearsl();" style="width:200px" onMouseDown="onSelectValue();" ondblclick='setSelectValue();'>    
    </select>
  </div> 
<html:hidden name="kanBanForm" property="checkperson"/>
<html:hidden name="kanBanForm" property="person"/>
</html:form>