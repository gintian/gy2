<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script language="javascript" src="/ajax/common.js"></script>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String manager = userView.getUnitIdByBusi("4");
	int i = 0;
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
%>
<style>
<!--
.div_table {
	border-width: 1px;
	BORDER-BOTTOM: #aeac9f 1pt solid;
	BORDER-LEFT: #aeac9f 1pt solid;
	BORDER-RIGHT: #aeac9f 1pt solid;
	BORDER-TOP: #aeac9f 1pt solid;
}

.tdFontcolor {
	text-decoration: none;
	Font-family: ;
	font-size: 12px;
	height: 12px;
	align: "center"
}
--> 
</style>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script language="javascript">
	   var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
	 function showDateSelectBox(srcobj)
   {
       
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        if(navigator.appName.indexOf("Microsoft")!= -1){
	    		style.posLeft=pos[0]-1;
				style.posTop=pos[1]-1+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
			}else{
				style.left=pos[0]-1;
				style.top=pos[1]-1+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
			}
          }                 

   }
 function validate()
	{
	     var tag=true;   
          <logic:iterate  id="element"    name="staticFieldForm"  property="factorlist" indexId="index"> 
            <logic:equal name="element" property="fieldtype" value="D">   
               var valueInputs=document.getElementsByName("<%="factorlist[" + index + "].value"%>");
               var dobj=valueInputs[0];
               tag= checkDate(dobj);   
	       if(tag==false)
	         {
	           dobj.focus();
	           return false;
	         }
            </logic:equal> 
          </logic:iterate>   
         return tag;
	 
	}
	function check()
	{
	   var h_obj=document.getElementById("historybox");
	   if(h_obj.checked==true)
	   {
	      document.staticFieldForm.history.value="1";      
	   }else
	   {
	     document.staticFieldForm.history.value="0";
	   }
	    var r_obj=document.getElementById("resultbox");
	   if(r_obj.checked==true)
	   {
	      document.staticFieldForm.result.value="1";      
	   }else
	   {
	     document.staticFieldForm.result.value="0";
	   }
	    var like=document.getElementById("findbox");
	   if(like.checked==true)
	   {
	      document.staticFieldForm.find.value="1";      
	   }else
	   {
	     document.staticFieldForm.find.value="0";
	   }
	}
function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
} 	
function addDict(obj,event,flag)
{ 
	var ff=document.getElementById('dict').style.display;
	if('block'==ff){
		document.getElementById('dict').style.display="none";
		return;
	}
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var aTag;
   	aTag = obj;   
   var un_vos=document.getElementsByName("userbase")[0];
   var userbases=document.getElementsByName("userbases")[0].value;
   if(!un_vos)
		return false;
   var unArrs=un_vos.options;	
   //var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		 if(un_str)
		 {
		     if(userbases.indexOf(un_str.value)!=-1){
		     	if(c%2==0)
			     	rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' checked=checked />"+un_str.text+"</td>"; 
             	else
             		rs[c]="<td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' checked=checked />"+un_str.text+"</td></tr>"; 
             }else{
             	if(c%2==0)
                 	rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' style='height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' />"+un_str.text+"</td>"; 
             	else
             		rs[c]="<td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' />"+un_str.text+"</td></tr>"; 
             }
             c++;
		 }
        
	}
	if(c%2!=0){
		rs[c]="<td id='al"+c+"'  onclick=\"\"  style='height:10px;cursor:pointer' nowrap class=tdFontcolor></td></tr>"; 
		c++;
	}
    resultuser=rs.join("");
    resultuser="<div style='border-width: 1px;BORDER-bottom: #aeac9f 1pt solid;height:80px;width:"+($('viewuserbases').offsetWidth+aTag.offsetWidth-22)+"px;overflow:auto;margin:9 9 9 9'><table width='100%' cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'>"+resultuser+"</table></div>"; 
    resultuser+="<table style='margin:9 9 9 9' width="+($('viewuserbases').offsetWidth+aTag.offsetWidth-22)+"px cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'><tr id='tv' name='tv'><td id='al"+c+"' style='width:85%;height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=allbox type=checkbox onclick='selectallcheckbox(this)' value='' />全部</td></tr>";
    resultuser+="<tr><td align='center' style='height:35px'><input onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" value='确定' type='button' class='mybutton'/>&nbsp;&nbsp;<input onclick=\"document.getElementById('dict').style.display='none'\" value='取消' type='button' class='mybutton'/></td></tr></table>";
    document.getElementById("dict").innerHTML=resultuser;
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.width=$('viewuserbases').offsetWidth+aTag.offsetWidth;
    var pos=getAbsPosition(aTag);
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=pos[0]-$('viewuserbases').offsetWidth;
    document.getElementById('dict').style.top=pos[1]+aTag.offsetHeight-1;
    if(navigator.appName.indexOf("Microsoft")!= -1){
	    var objdiv=document.getElementById("dict");
	    var w = objdiv.offsetWidth;
		var h = objdiv.offsetHeight;
		var ifrm = document.createElement('iframe');
		ifrm.src = 'javascript:false';
		ifrm.style.cssText = 'position:absolute; visibility:inherit; top:0px; left:0px; width:' + w + 'px; height:' + h + 'px; z-index:-1; filter: \'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)\'';
		objdiv.appendChild(ifrm);
	}
} 

function selectallcheckbox(o){
	var backdatebox=document.getElementsByName("backdatebox");
	for(var i=0;i<backdatebox.length;i++){
		var obj=backdatebox[i];
		obj.checked=o.checked;
	}
}
function selectcheckbox(){
	var backdatebox=document.getElementsByName("backdatebox");
	var userbases=document.getElementsByName("userbases")[0];
	userbases.value="";
	var veiwuserbases=document.getElementsByName("viewuserbases")[0];
	veiwuserbases.value="";
	for(var i=0;i<backdatebox.length;i++){
		var obj=backdatebox[i];
		if(obj.checked){
			var tmp=obj.value.split("`");
			var viewuserbasesv=tmp[1];
			var userbasesv=tmp[0];
			if(userbases.value.length>0){
				userbases.value=userbases.value+"`"+userbasesv;
				veiwuserbases.value=veiwuserbases.value+";"+viewuserbasesv;
			}else{
				userbases.value=userbasesv;
				veiwuserbases.value=viewuserbasesv;
			}
		}
	}
}
function back(){
	staticFieldForm.action="/general/static/simple_static.do?b_back=link";
	staticFieldForm.submit();
}

function querynext(){
<logic:equal name="staticFieldForm" property="flist" value="1"> 
	if($F('userbases').length==0){
		alert('人员库不能为空');
		return false
	}else 
		return true;
</logic:equal>
<logic:notEqual name="staticFieldForm" property="flist" value="1"> 
	return true;
</logic:notEqual>
}
</script>
<hrms:themes />
<%
	if ("hl".equalsIgnoreCase(bosflag)) {
%>
<style>
.ListTable {
	margin-top: 6px;
}
</style>
<%
	}else if("hcm".equalsIgnoreCase(bosflag)){
%>
<style>
.ListTable {
	margin-top: 6px;
}
</style>
<%} %>
<html:form action="/general/static/simple_static"
	onsubmit="return check()">
	<html:hidden property="infor_Flag" />
	<table width="700px" align="center" border="0" cellpadding="0"
		cellspacing="0" style="margin-top: 6px">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0"
					align="center" class="ListTableF">
					<tr height="20">
						<td align="left" class="TableRow_left"><bean:message key="static.stat" /></td>
					</tr>
					<tr>
						<td>
							<table border="0" cellspacing="0" width="100%"
								cellpadding="2" align="center">
								<logic:equal name="staticFieldForm" property="flist" value="1">
									<tr style="display: none">
										<td align="right" nowrap class="tdFontcolor"><bean:message key="static.stor" /></td>
										<td align="left" nowrap class="tdFontcolor">
											<html:select name="staticFieldForm" property="userbase"
												size="1">
												<html:optionsCollection property="alist" value="dataValue"
													label="dataName" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td align="right" width="10%" height="30" nowrap></td>
										<td align="left" nowrap><bean:message key="menu.base" />
											<input name=viewuserbases style="width: 250px; height: 20px;"
												value='${staticFieldForm.viewuserbases }'
												readonly="readonly" class="inputtext"><img id="imgid"
												style="cursor: pointer; vertical-align: bottom;"
												src="/images/select.jpg"
												onmouseover="this.src='/images/selected.jpg'"
												onmouseout="this.src='/images/select.jpg'"
												onclick="addDict(this,event,'hidcategories');">
											<input name=userbases type="hidden"
												value='${staticFieldForm.userbases }' />
										</td>
									</tr>
								</logic:equal>
								<logic:notEqual name="staticFieldForm" property="flist"
									value="1">
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
								</logic:notEqual>
								<tr>
									<td colspan="2">
										<table border="0" cellspacing="0" width="80%"
											class="ListTable1" cellpadding="0" align="center">
											<tr>
												<td align="center" nowrap class="TableRow">
													<bean:message key="static.target" />
												</td>
												<td align="center" nowrap class="TableRow">
													<bean:message key="static.relation" />
												</td>
												<td align="center" nowrap class="TableRow_left">
													<bean:message key="static.title" />
												</td>
											</tr>
											<logic:iterate id="element" name="staticFieldForm"
												property="factorlist" indexId="index">
												<tr>
													<td align="center" class="RecordRow" nowrap>
														<bean:write name="element" property="hz" />
														&nbsp;
													</td>
													<td align="center" class="RecordRow" nowrap>
														<hrms:optioncollection name="staticFieldForm"
															property="operlist" collection="list" />
														<html:select name="staticFieldForm"
															property='<%="factorlist[" + index
												+ "].oper"%>' size="1">
															<html:options collection="list" property="dataValue"
																labelProperty="dataName" />
														</html:select>
													</td>
													<!--日期型 -->
													<logic:equal name="element" property="fieldtype" value="D">
														<td align="left" class="RecordRow_left" nowrap>
															<html:text name="staticFieldForm"
																property='<%="factorlist[" + index
										+ "].value"%>' size="30"
																maxlength="10" styleClass="text4"
																ondblclick="showDateSelectBox(this);" />
														</td>
													</logic:equal>
													<!--备注型 -->
													<logic:equal name="element" property="fieldtype" value="M">
														<td align="left" class="RecordRow_left" nowrap>
															<html:text name="staticFieldForm"
																property='<%="factorlist[" + index
										+ "].value"%>' size="30"
																maxlength='<%="factorlist[" + index
										+ "].itemlen"%>'
																styleClass="text4" />
														</td>
													</logic:equal>
													<!--字符型 -->
													<logic:equal name="element" property="fieldtype" value="A">
														<td align="left" class="RecordRow_left" nowrap>
															<logic:notEqual name="element" property="codeid"
																value="0">
																<html:hidden name="staticFieldForm"
																	property='<%="factorlist[" + index
											+ "].value"%>'
																	styleClass="text4" />
																<html:text name="staticFieldForm"
																	property='<%="factorlist[" + index
											+ "].hzvalue"%>'
																	size="30" maxlength="50" styleClass="text4"
																	onchange="fieldcode(this,1)" />
																<logic:equal name="element" property="fieldname"
																	value="b0110">
																	<img src="/images/code.gif"
																		onclick='openInputCodeDialogOrgInputPos("UN","<%="factorlist[" + index
												+ "].hzvalue"%>","<%=manager%>",1);' align="middle" />
																</logic:equal>
																<logic:notEqual name="element" property="fieldname"
																	value="b0110">
																	<logic:equal name="element" property="fieldname"
																		value="e0122">
																		<img src="/images/code.gif"
																			onclick='openInputCodeDialogOrgInputPos("UM","<%="factorlist["
													+ index + "].hzvalue"%>","<%=manager%>",1);' align="middle" />
																	</logic:equal>
																	<logic:equal name="element" property="fieldname"
																		value="e01a1">
																		<img src="/images/code.gif"
																			onclick='openInputCodeDialogOrgInputPos("@K","<%="factorlist["
													+ index + "].hzvalue"%>","<%=manager%>",1);' align="middle"/>
																	</logic:equal>
																	<logic:notEqual name="element" property="fieldname"
																		value="e0122">
																		<logic:notEqual name="element" property="fieldname"
																			value="e01a1">
																			<img src="/images/code.gif"
																				onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["
														+ index + "].hzvalue"%>");' align="middle"/>
																		</logic:notEqual>
																	</logic:notEqual>
																</logic:notEqual>
																<!-- <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist[" + index
											+ "].hzvalue"%>");'/> -->
															</logic:notEqual>
															<logic:equal name="element" property="codeid" value="0">
																<html:text name="staticFieldForm"
																	property='<%="factorlist[" + index
											+ "].value"%>' size="30"
																	maxlength="${element.itemlen}" styleClass="text4" />
															</logic:equal>
														</td>
													</logic:equal>
													<!--数据值-->
													<logic:equal name="element" property="fieldtype" value="N">
														<td align="left" class="RecordRow_left" nowrap>
															<html:text name="staticFieldForm"
																property='<%="factorlist[" + index
										+ "].value"%>' size="30"
																maxlength="${element.itemlen}" styleClass="text4" />
														</td>
													</logic:equal>
												</tr>
												<%
													++i;
												%>
											</logic:iterate>
											<tr>
												<td align="center" nowrap class="RecordRow_left" colspan="4">
													<logic:equal name="userView" property="status" value="0">
													<logic:equal value="1" name="staticFieldForm" property="find">
														<input type="checkbox" checked="checked" id="findbox">
													</logic:equal>
													<logic:notEqual value="1" name="staticFieldForm" property="find">
														<input type="checkbox" id="findbox">
													</logic:notEqual>
													<input type="hidden" name="find" id="find" value="<bean:write name="staticFieldForm" property="find"/>"/>
													
														&nbsp;<bean:message key="static.like" />&nbsp;&nbsp;&nbsp;&nbsp;
                </logic:equal>
                
                									<logic:equal value="1" name="staticFieldForm" property="history">
														<input type="checkbox" checked="checked" id="historybox">
													</logic:equal>
													<logic:notEqual value="1" name="staticFieldForm" property="history">
														<input type="checkbox" id="historybox">
													</logic:notEqual>
													<input type="hidden" name="history" id="history" value="<bean:write name="staticFieldForm" property="history"/>"/>
													&nbsp;
													<bean:message key="static.history" />
													
													<logic:equal value="1" name="staticFieldForm" property="result">
														<input type="checkbox" checked="checked" id="resultbox">
													</logic:equal>
													<logic:notEqual value="1" name="staticFieldForm" property="result">
														<input type="checkbox" id="resultbox">
													</logic:notEqual>
													<input type="hidden" name="result" id="result" value="<bean:write name="staticFieldForm" property="result"/>"/>
													&nbsp;
													<bean:message key="hmuster.label.search_result" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="3" colspan="2"></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="700px" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top: 5px">
		<tr class="list3">
			<td colspan="2" align="center">
			    <hrms:priv func_id="2602302">
					<hrms:submit styleClass="mybutton" property="b_add">
						<bean:message key="button.save" />
					</hrms:submit>
				</hrms:priv>
				<html:button styleClass="mybutton" property="b_back"
					onclick="return back();">
					<bean:message key="static.back" />
				</html:button>
				<hrms:submit styleClass="mybutton" property="b_next"
					onclick="return querynext();">
					<bean:message key="static.next" />
				</hrms:submit>
				<html:reset styleClass="mybutton"  onclick="stat_reset();">
					<bean:message key="button.clear" />
				</html:reset>
			</td>
		</tr>
	</table>

	<div id="date_panel">
		<select name="date_box" multiple="multiple" size="10"
			style="width: 200" onchange="setSelectValue();"
			onclick="setSelectValue();">
			<option value="$YRS[10]">
				年限
			</option>
			<option value="当年">
				当年
			</option>
			<option value="当月">
				当月
			</option>
			<option value="当天">
				当天
			</option>
			<option value="今天">
				今天
			</option>
			<option value="截止日期">
				截止日期
			</option>
			<option value="1992.4.12">
				1992.4.12
			</option>
			<option value="1992.4">
				1992.4
			</option>
			<option value="1992">
				1992
			</option>
			<option value="????.??.12">
				????.??.12
			</option>
			<option value="????.4.12">
				????.4.12
			</option>
			<option value="????.4">
				????.4
			</option>
		</select>
	</div>
</html:form>
<script language="javascript">
//重置操作，没有对人员库操作
function stat_reset(){
	setTimeout(function(){
		var viewuserbases =document.getElementsByName('viewuserbases')[0];
		viewuserbases.value='${staticFieldForm.init_viewuserbases}';
		var userbases =document.getElementsByName('userbases')[0];
		userbases.value='${staticFieldForm.init_userbases}';
	},100);
}

   Element.hide('date_panel');
   <logic:equal name="staticFieldForm" property="flist" value="1"> 
   if($('userbase').options.length<2){
   		$('imgid').style.display='none';
   }
   </logic:equal>
	if(!getBrowseVersion() || getBrowseVersion() ==10){//非ie兼容模式下  解决简单统计没有右边框的问题 wangz 2019-03-04
	    var ListTable1 = document.getElementsByClassName('ListTable1')[0];
	    var tableRowleft = ListTable1.getElementsByClassName("TableRow_left")[0];
        tableRowleft.style.borderRight = "#C4D8EE 1pt solid";
        var recordRow_left = document.getElementsByClassName("RecordRow_left");
		for(i = 0;i<recordRow_left.length;i++){
		    recordRow_left[i].style.borderRight = "#C4D8EE 1pt solid";
		}

		var RecordRowTable = document.getElementsByClassName('RecordRow')[0];
        RecordRowTable.removeAttribute('border-collapse');
        RecordRowTable.removeAttribute('border-right');
        var secondTrTd = RecordRowTable.getElementsByTagName('tbody')[0].getElementsByTagName('tr')[0].getElementsByTagName('td')[2];
        secondTrTd.style.borderRightStyle='';
	}
</script>
<div id="dict" class='div_table'
	style="display: none; z-index: +999; position: absolute; height: 170px; overflow: hidden; background-color: #FFF"></div>
