<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css"> 
.textSize {
	border: 1pt solid #A8C4EC;
	width:150;
}
.textReadSize {
	BACKGROUND-COLOR:#E5E5E5;
	border: 1pt solid #A8C4EC;
	width:150;
}
</style>
<script language="javascript">
function saveEdit(setid){
	var url =  "/general/approve/personinfo/iframapp.do?b_query=link&savEdit=save&setid=";
	url+=setid+"&check=open&keyid=${approvePersonForm.keyid}&code=${approvePersonForm.a_code}";
	url+="&chg_id=${approvePersonForm.chg_id}&fcheck=fopen&typeid=${approvePersonForm.typeid}&sequenceid=${approvePersonForm.sequenceid}";	 
	approvePersonForm.action=url;
   	approvePersonForm.submit();
}
function checkFlag(flag,setid){
	if(flag=='boflag'){
		if(confirm("确定退回?")){
			var url = "/general/approve/personinfo/iframapp.do?b_query=link&savEdit=";
			url+=flag+"&check=open&setid="+setid+"&keyid=${approvePersonForm.keyid}";
			url+="&code=${approvePersonForm.a_code}&chg_id=${approvePersonForm.chg_id}&fcheck=fopen";
			url+="&typeid=${approvePersonForm.typeid}&sequenceid=${approvePersonForm.sequenceid}";
			approvePersonForm.action= url; 
   			approvePersonForm.submit();
   		}
   	}else if(flag=='pflag'){
   		if(confirm("确定批准?")){
   			var url = "/general/approve/personinfo/iframapp.do?b_query=link&savEdit=";
   			url+=flag+"&check=open&setid="+setid+"&keyid=${approvePersonForm.keyid}";
   			url+="&code=${approvePersonForm.a_code}&chg_id=${approvePersonForm.chg_id}";
   			url+="&fcheck=fopen&typeid=${approvePersonForm.typeid}&sequenceid=${approvePersonForm.sequenceid}";
   			
			approvePersonForm.action= url; 
   			approvePersonForm.submit();
   		}
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
function checkFlagSearch(setid,viewitem,chg_id){
	var url = "/general/approve/personinfo/iframapp.do?b_query=link&savEdit=search&setid="+setid+"&check=open&keyid=${approvePersonForm.keyid}";
	url+="&code=${approvePersonForm.a_code}&chg_id=${approvePersonForm.chg_id}";
	url+="&fcheck=fopen&typeid=${approvePersonForm.typeid}&viewitem=";
	url+=viewitem+"&sequenceid=${approvePersonForm.sequenceid}";
	
	approvePersonForm.action=url;	 
   	approvePersonForm.submit();
}
function backMain(){
	document.location.href = "/general/approve/personinfo/approve.do?b_search=link&code=${approvePersonForm.a_code}&kind=${approvePersonForm.kind}";
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
		alert("请输入正确格式的日期!");
		obj.value='';
	}
}
</script>
<html:form action="/general/approve/personinfo/iframapp">
<bean:define id="chg_id" name='approvePersonForm' property='chg_id'/>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<logic:iterate id="felement" name="approvePersonForm" property="fieldlist" indexId="index">
	<bean:define id="fieldsetid" name='felement' property='fieldsetid'/>
	<bean:define id="fieldsetdesc" name="felement" property="customdesc"/>
	<logic:equal name="approvePersonForm" property="setid" value="${fieldsetid}">
	<tr>
		<td>
			<a href="/general/approve/personinfo/iframapp.do?b_query=link&code=${approvePersonForm.a_code}&chg_id=${chg_id}&check=close&fcheck=fopen" title="隐藏">
			${fieldsetdesc}<img src="/images/button_vert1.gif" border="0"></a>
		</td>
		<td>
			<logic:equal name="approvePersonForm" property="allflag" value="02">
				 <hrms:priv func_id="03086,260636">
				<a href="###" onclick='saveEdit("${fieldsetid}");' title="保存">保存<!--  <img src="/images/detail.gif" border="0">--></a>
				</hrms:priv>
				 <hrms:priv func_id="03081,260631">
				<a href="###" onclick='checkFlag("boflag","${fieldsetid}");' title="退回">退回<!--<img src="/images/goto_input.gif" border="0">--></a>				
				</hrms:priv>
				<hrms:priv func_id="03082,260632">
				<a href="###" onclick='checkFlag("pflag","${fieldsetid}");' title="批准">批准<!--<img src="/images/cc1.gif" border="0">--></a>
				</hrms:priv>
			</logic:equal>
			<logic:equal name="approvePersonForm" property="allflag" value="03">
			    <logic:equal name="approvePersonForm" property="checked_may_reject" value="true">
			      <a href="###" onclick='checkFlag("boflag","${fieldsetid}");' title="退回">退回<!-- <img src="/images/goto_input.gif" border="0">--></a>		
			    </logic:equal>
			</logic:equal>
			<html:radio name="approvePersonForm" property="viewitem" onclick="checkFlagSearch('${fieldsetid}',0,'${chg_id}');" value="0">显示变动信息</html:radio>
			<html:radio name="approvePersonForm" property="viewitem" onclick="checkFlagSearch('${fieldsetid}',1,'${chg_id}');" value="1">显示全部</html:radio>
		</td>
		<td align="right">
			<logic:equal name="approvePersonForm" property="allflag" value="01">起草</logic:equal>
			<logic:equal name="approvePersonForm" property="allflag" value="02">已报批</logic:equal>
			<logic:equal name="approvePersonForm" property="allflag" value="03">已批</logic:equal>
			<logic:equal name="approvePersonForm" property="allflag" value="07">退回</logic:equal>
			|
			<logic:equal name="approvePersonForm" property="typeid" value="update">修改</logic:equal>
			<logic:equal name="approvePersonForm" property="typeid" value="new">新增</logic:equal>
			<logic:equal name="approvePersonForm" property="typeid" value="insert">插入</logic:equal>
			<logic:equal name="approvePersonForm" property="typeid" value="delete">删除</logic:equal>
		</td>
	</tr>
	
		<tr>
			<td colspan="3" height="30">
				<logic:iterate id="keyitem" name="approvePersonForm" property="keylist" indexId="index1">
					<bean:define id="numitem" value="${index1}"/>  
					<logic:iterate id="typeitem" name="approvePersonForm" property="typelist" indexId="index2">
						<logic:equal name="numitem"  value="${index2}">
							<%int pagenum=1; %>
							<logic:iterate id="sequenceitem" name="approvePersonForm" property="sequenceList" indexId="index3">
								<logic:equal name="numitem"  value="${index3}">	
									<logic:notEqual name="approvePersonForm" property="keyid" value="${keyitem}">
										<a href="/general/approve/personinfo/iframapp.do?b_query=link&code=${approvePersonForm.a_code}&chg_id=${chg_id}&check=open&setid=${fieldsetid}&fcheck=fopen&keyid=${keyitem}&typeid=${typeitem}&sequenceid=${sequenceitem}"><%=pagenum%></a>
									</logic:notEqual>
									<logic:equal name="approvePersonForm" property="keyid" value="${keyitem}">
										<logic:equal name="approvePersonForm" property="typeid" value="${typeitem}">
											<logic:equal name="approvePersonForm" property="sequenceid" value="${sequenceitem}">
												<font color="red"><%=pagenum%></font>
											</logic:equal>
											<logic:notEqual name="approvePersonForm" property="sequenceid" value="${sequenceitem}">
												<a href="/general/approve/personinfo/iframapp.do?b_query=link&code=${approvePersonForm.a_code}&chg_id=${chg_id}&check=open&setid=${fieldsetid}&fcheck=fopen&keyid=${keyitem}&typeid=${typeitem}&sequenceid=${sequenceitem}"><%=pagenum%></a>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="approvePersonForm" property="typeid" value="${typeitem}">
											<a href="/general/approve/personinfo/iframapp.do?b_query=link&code=${approvePersonForm.a_code}&chg_id=${chg_id}&check=open&setid=${fieldsetid}&fcheck=fopen&keyid=${keyitem}&typeid=${typeitem}&sequenceid=${sequenceitem}"><%=pagenum%></a>
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
		<td colspan="3" align="right">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<tr>
					<td width="30%" class="TableRow">指标名</td>
					<td width="30%" class="TableRow">变动前</td>
					<td width="40%" class="TableRow">变动后</td>
				</tr>
				<logic:iterate id="itemelement" name="approvePersonForm" property="newFieldList" indexId="index4">
				<bean:define id="itemid" name="itemelement" property="itemid"/>
				<bean:define id="itemdesc" name="itemelement" property="itemdesc"/>
				<bean:define id="itemlength" name="itemelement" property="itemlength"/>
				<logic:notEqual name="itemelement" property="priv_status" value="0">
				<tr>
					<td class="RecordRow">${itemdesc}</td>
					<td width class="RecordRow">
						<logic:equal name="itemelement" property="itemtype" value="M">
								<html:textarea name="approvePersonForm" cols="20" rows="5" property='<%="oldFieldList["+index4+"].value"%>' readonly="true" styleClass="textSize"></html:textarea>
						</logic:equal>
						<logic:notEqual name="itemelement" property="itemtype" value="M">
								<logic:notEqual name="itemelement" property="codesetid" value="0">
									<html:text name="approvePersonForm" property='<%="oldFieldList["+index4+"].viewvalue"%>' readonly="true" styleClass="textSize"/>
								</logic:notEqual>
								<logic:equal name="itemelement" property="codesetid" value="0">
									<html:text name="approvePersonForm" property='<%="oldFieldList["+index4+"].value"%>' readonly="true" styleClass="textSize"/>
								</logic:equal>
						</logic:notEqual>
					</td>
					<td class="RecordRow">
						<logic:equal name="itemelement" property="itemtype" value="A">
							<logic:notEqual name="itemelement" property="codesetid" value="0">
								<logic:notEqual name="approvePersonForm" property="allflag" value="02">
									<html:hidden name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>'/> 
									<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].viewvalue"%>' readonly="true"  styleClass="textReadSize"/> 
								</logic:notEqual>
								<logic:equal name="approvePersonForm" property="allflag" value="02">
									<html:hidden name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>'/> 
									<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)" styleClass="textReadSize"/>
									<logic:equal name="itemelement" property="priv_status" value="2">
									<img  src="/images/code.gif" onclick='javascript:openInputCodeDialog("${itemelement.codesetid}","<%="newFieldList["+index4+"].viewvalue"%>");'/>&nbsp;
									</logic:equal>
								</logic:equal>
							</logic:notEqual>
							<logic:equal name="itemelement" property="codesetid" value="0">
							<logic:notEqual name="approvePersonForm" property="allflag" value="02">
								<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>'    styleClass="textReadSize"/>
							</logic:notEqual>
							<logic:equal name="approvePersonForm" property="allflag" value="02">
								<logic:equal name="itemelement" property="priv_status" value="2">
								<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' styleClass="textSize"/>
								</logic:equal>
								<logic:notEqual name="itemelement" property="priv_status" value="2">
								<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' styleClass="textReadSize" readonly="true"/>
								</logic:notEqual>
							</logic:equal>
						</logic:equal>
				</logic:equal>
				<logic:equal name="itemelement" property="itemtype" value="D">
					<logic:notEqual name="approvePersonForm" property="allflag" value="02">
						<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' readonly="true" styleClass="textReadSize"/>
					</logic:notEqual>
					<logic:equal name="approvePersonForm" property="allflag" value="02">
						<logic:equal name="itemelement" property="priv_status" value="2">
						<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' onblur="timeCheck(this);" ondblclick="showDateSelectBox(this);" styleClass="textSize"/>
						</logic:equal>
						<logic:notEqual name="itemelement" property="priv_status" value="2">
						<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' onblur="timeCheck(this);" ondblclick="showDateSelectBox(this);" readonly="true" styleClass="textReadSize"/>
						</logic:notEqual>
					</logic:equal>
				</logic:equal>
				<logic:equal name="itemelement" property="itemtype" value="N">
					<logic:notEqual name="approvePersonForm" property="allflag" value="02">
						<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' readonly="true" styleClass="textReadSize"/>
					</logic:notEqual>
					<logic:equal name="approvePersonForm" property="allflag" value="02">
						<logic:equal name="itemelement" property="priv_status" value="2">
						<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' onkeypress="event.returnValue=IsDigit();" styleClass="textSize"/>
						</logic:equal>
						<logic:notEqual name="itemelement" property="priv_status" value="2">
						<html:text name="approvePersonForm" property='<%="newFieldList["+index4+"].value"%>' onkeypress="event.returnValue=IsDigit();" readonly="true" styleClass="textReadSize"/>
						</logic:notEqual>
					</logic:equal>
				</logic:equal>
				<logic:equal name="itemelement" property="itemtype" value="M">
					<logic:notEqual name="approvePersonForm" property="allflag" value="02">
						<html:textarea  name="approvePersonForm" cols="20" rows="5" property='<%="newFieldList["+index4+"].value"%>' readonly="true" styleClass="textReadSize"></html:textarea>
					</logic:notEqual>
					<logic:equal name="approvePersonForm" property="allflag" value="02">
						<logic:equal name="itemelement" property="priv_status" value="2">
						<html:textarea  name="approvePersonForm" cols="20" rows="5" property='<%="newFieldList["+index4+"].value"%>' styleClass="textSize"></html:textarea>
						</logic:equal>
						<logic:notEqual name="itemelement" property="priv_status" value="2">
						<html:textarea  name="approvePersonForm" cols="20" rows="5" property='<%="newFieldList["+index4+"].value"%>' readonly="true" styleClass="textReadSize"></html:textarea>
						</logic:notEqual>
					</logic:equal>
				</logic:equal>
				</td>
			</tr>
			</logic:notEqual>
		</logic:iterate>
		</table>
		</td>
	<tr>
	</logic:equal>
	<logic:notEqual name="approvePersonForm" property="setid" value="${fieldsetid}">
	<tr height="30">
		<td>
			<a href="/general/approve/personinfo/iframapp.do?b_query=link&code=${approvePersonForm.a_code}&chg_id=${chg_id}&check=open&setid=${fieldsetid}&fcheck=fopen" title="显示">
			${fieldsetdesc}<img src="/images/button_vert2.gif" border="0"></a>
		</td>
		<td>&nbsp;</td>
		<td align="right">
			<logic:equal name="felement" property="changeflag" value="01">起草</logic:equal>
			<logic:equal name="felement" property="changeflag" value="02">已报批</logic:equal>
			<logic:equal name="felement" property="changeflag" value="03">已批</logic:equal>
			<logic:equal name="felement" property="changeflag" value="07">退回</logic:equal>
			|
			<logic:equal name="felement" property="moduleflag" value="update">修改</logic:equal>
			<logic:equal name="felement" property="moduleflag" value="new">新增</logic:equal>
			<logic:equal name="felement" property="moduleflag" value="insert">插入</logic:equal>
			<logic:equal name="felement" property="moduleflag" value="delete">删除</logic:equal>
		</td>
	</tr>
	</logic:notEqual>
	</logic:iterate>
</table>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr>
		<td align="center">&nbsp;</td>
	</tr>
	<tr>
		<td align="center"><input type="button" value="返回" onclick="backMain();" class="mybutton"></td>
	</tr>
</table>
<div id="date_panel">
	<select name="date_box" multiple="multiple" size="10"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
		<option value="1992.4.12">1992.04.12</option>	
		<option value="1992.4">1992.04</option>	
		<option value="1992">1992</option>			    
		<option value="1992-04-12">1992-04-12</option>
		<option value="1992-04">1992-04</option>			    			    		    
	</select>
</div>
</html:form>
<script language="JavaScript">
Element.hide('date_panel');
</script>
