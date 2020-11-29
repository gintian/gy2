<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//String manager=userView.getManagePrivCodeValue();  
	String manager=userView.getUnitIdByBusi("4"); 
	String path=request.getParameter("path");
	pageContext.setAttribute("path",path);
    String bosflag="";
    if(userView!=null){
     bosflag = userView.getBosflag();
    }
    String strid =request.getParameter("strid")==null? "":request.getParameter("strid");
%>
<script type="text/javascript">
<!--
	path="${path}"
//-->
</script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<%-- 引入Request 相关js  wangb  20180206 bug 34579--%>
<script language="JavaScript" src="../../inform.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<script language="JavaScript">
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
function IsIntDigit(e) {
	var key = window.event?e.keyCode:e.which;
	key=parseInt(key);
	if(((key >= 48) && (key <= 57))||key==8){
		return true;
	}else{
		return false;
	}
}
function IsIntDigit1(e) {
	var key = window.event?e.keyCode:e.which;
	key=parseInt(key);
	if(((key >= 48) && (key <= 57))||key==45||key==46){
		return true;
	}else{
		return false;
	}
}
function checkAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			if(tablevos[i].name=='history')
				continue;
			tablevos[i].checked=true;
      	 }
   	}
}
function clearAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			if(tablevos[i].name=='history')
				continue;
			tablevos[i].checked=false;
      	 }
   	}
}
function checkValue(obj,itemlength,decimalwidth)
  {
     if(decimalwidth=='')
      return true;
     if(itemlength=='')
      return true;
     var t_len=obj.value;
     if(t_len!="")
     {
        var decimalw=parseInt(decimalwidth,10);	
        var itemlen=parseInt(itemlength,10);	
        var inde=t_len.indexOf(".");
        if(inde==-1)
        {
          if(t_len.length>itemlen)
          {
            alert("整数位长度超过定义"+itemlen+",请修改！");
            obj.focus(); 
            return false;
          }
        }else
        {
           var q_srt=t_len.substring(0,inde);
           var n_srt=t_len.substring(inde+1);           
           if(q_srt.length>itemlen)
           {
             alert("整数位长度超过定义"+itemlen+",请修改！");
             obj.focus(); 
             return false;
           }else if(n_srt.length>decimalw)
           {
              alert("小数位长度超过定义"+decimalw+",请修改！");
              obj.focus(); 
              return false;
           }
        }
     }
  } 
</script>
<style type="text/css">
#Wdiv {
           border: 1px solid;
           height: 270px;        
           overflow: auto;            
           margin: 1em 0;
       }
</style>
<hrms:themes />
<html:form action="/general/inform/emp/batch/alertmoreind">
<%int i=1;%>
<input type="hidden" name="companyid" id="companyid" value="<%=manager%>">
<input type="hidden" name="depid" id="depid" value="<%=manager%>">
<input type="hidden" name="jobid" id="jobid" value="<%=manager%>">
<%if("hcm".equals(bosflag)){ %>
<table width="100%" border="0" align="center" >
<%}else{ %>
<table width="100%" border="0" align="center" style="margin-top: 10px">
<%} %>
  <tr> 
    <td height="310" align="center"> 
          <fieldset style="width:auto;height:auto;padding-left:5px;padding-right:5px;">
     		 <legend><bean:message key='infor.menu.batupdate_m'/></legend>
     		 <logic:equal value="1" name="indBatchHandForm" property="infor">
     		 <table  width="100%" border="0" >
     		 	<tr>
					<td align="left" nowrap="nowrap"><bean:message key="workbench.info.batchinout.lebal"/>
						<hrms:optioncollection name="indBatchHandForm" property="fieldSetDataList" collection="list" />
						<html:select name="indBatchHandForm" property="setname" indexed="setname" styleId="setname" onchange="changeInfor();">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
					</td>
			 	</tr>
     		 </table>
     		 </logic:equal>
     		 <div id="Wdiv" class="fixedtab"  style="border: 1pt solid;border-color:#C4D8EE; border-bottom: none; margin-top: 0px; margin-bottom: 0px;">
              <table width="100%" border="0" cellspacing='0' cellpadding='0'>
              <tr class="fixedHeaderTr" style="border-collapse: collapse;"> 
                <td width="10%" class="TableRow fixedHeaderTr" align="center" style="border-left:none;border-top:none;border-right:none;border-bottom: 1pt solid;border-bottom-color:#C4D8EE;">&nbsp;</td>
                <td width="20%" class="TableRow fixedHeaderTr" style="border-left:none;border-right:none;border-top:none;border-bottom: 1pt solid;border-bottom-color:#C4D8EE;" align="center"><bean:message key='field.label'/></td>
                <td width="55%" class="TableRow fixedHeaderTr" style="border-left:none;border-right:none;border-top:none;border-bottom: 1pt solid;border-bottom-color:#C4D8EE;"  align="center"><bean:message key='infor.menu.alert.value'/></td>
                <td width="15%" class="TableRow fixedHeaderTr" style="border-left:none;border-right: none;border-top:none;border-bottom: 1pt solid;border-bottom-color:#C4D8EE;"  align="center"><bean:message key='label.edit'/></td>
              </tr>
              <logic:iterate id="element" name="indBatchHandForm" property="fieldlist">
              <tr> 
                <td class="RecordRow" align="center" style="border-left:none;border-top:none;" nowrap><%=i%></td>
                <td class="RecordRow" title="<bean:write name="element" property="itemdesc"/>" style="border-left:none;border-top:none;" nowrap>
                	<div STYLE="width: 100px; overflow: hidden; text-overflow: ellipsis; white-space:nowrap;">
                		<bean:write name="element" property="itemdesc"/>
                	</div>
                </td>
                <logic:equal name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="itemtype" value="D">
                		<td class="RecordRow" align="left" style="border-left:none;border-top:none;"  nowrap>
                			<input type="text" class="textColorWrite" name="${element.itemid}.value"  extra="editor" onblur="timeCheck(document.getElementsByName('${element.itemid}.value')[0]);" style="width:200px;font-size:10pt;text-align:left" dropDown="dropDownDate"  itemlength="${element.itemlength }" dataType="simpledate">
                		 </td>
                		 <td class="RecordRow" align="center" style="border-left:none;border-top:none;border-right: none;" nowrap>
                		 	<input type="checkbox" name="${element.itemid}">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="itemtype" value="N">
                		<td class="RecordRow" align="left" style="border-left:none;border-top:none;" nowrap>
                			<logic:equal name="element" property="decimalwidth" value="0">
                			<input type="text" class="textColorWrite" name="${element.itemid}.value" maxlength="8" onkeypress="return checkINT(event);" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}')" style="width:200px;ime-mode:disabled">
                			</logic:equal>
                			<logic:notEqual name="element" property="decimalwidth" value="0">
                			<input type="text" class="textColorWrite" name="${element.itemid}.value" maxlength="8" onkeypress="return IsIntDigit1(event);" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}')" style="width:200px;ime-mode:disabled">
                			</logic:notEqual>
                		</td>
                		 <td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="itemtype" value="M">
                	       <td class="RecordRow" align="left" style="border-left:none;border-top:none;padding:2px 5px;" nowrap>
                                <textarea rows="5" cols="300" class="textColorWrite" name="${element.itemid}.value" style="width:200px;height: 60px;"></textarea>
                            </td>
                            <td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                                <input type="checkbox" name="${element.itemid}">
                            </td>
                	</logic:equal>
                	<logic:notEqual name="element" property="itemtype" value="D">
                		<logic:notEqual name="element" property="itemtype" value="N">
                		  <logic:notEqual name="element" property="itemtype" value="M">
                			<td class="RecordRow" align="left" style="border-left:none;border-top:none;"  nowrap>
                				<input type="text" class="textColorWrite" name="${element.itemid}.value" style="width:200px;">
                			</td>
                			<td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                		 		<input type="checkbox" name="${element.itemid}">
                		 	</td>
                    		 </logic:notEqual>
                		 </logic:notEqual>
                	</logic:notEqual>	
                </logic:equal>
                <logic:notEqual name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="codesetid" value="UN">
                		<td class="RecordRow" align="left" style="border-left:none;border-top:none;" nowrap>
                			<logic:equal value="b0110" name="element" property="itemid">
                				<input type="hidden" id="comp" value="${element.itemid}">
                			</logic:equal>
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UN');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],1);">
                			<input type="text" class="textColorWrite" name="${element.itemid}.hzvalue" style="width:200px;" onchange="fieldcode(document.getElementsByName('${element.itemid}.hzvalue')[0],1);" title="点击清空当前数据" readonly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);changeshow('companyid', '${element.itemid}');">
                			<logic:notEqual name="element" property="itemid" value="b0110">
	                        	<img src="/images/code.gif" align="absmiddle"  onlySelectCodeset="true" plugin="codeselector" codesetid="UN" inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
                        	</logic:notEqual>
                			<logic:equal name="element" property="itemid" value="b0110">
                        		<img src="/images/code.gif" align="absmiddle"  onlySelectCodeset="true" plugin="codeselector" codesetid="UN" nmodule='4' ctrltype='3' afterfunc="changeLowerLevel('${element.itemid}.value','deptId');changepos('UN');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],1);" inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
                        	</logic:equal>
                		</td>
                		<td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}" onclick="comCheckAll(document.getElementsByName('${element.itemid}')[0],'${element.itemid}');">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="UM">
                		<td class="RecordRow" align="left" style="border-left:none;border-top:none;" nowrap>
                			<logic:equal value="e0122" name="element" property="itemid">
                				<input type="hidden" id="dep" value="${element.itemid}">
                			</logic:equal>
                			
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UM');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],2);">
                			<input type="text" class="textColorWrite" name="${element.itemid}.hzvalue" style="width:200px;" onchange="fieldcode(document.getElementsByName('${element.itemid}.hzvalue')[0],1);" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);changeshow('depid','${element.itemid}')">
                			<logic:notEqual name="element" property="itemid" value="e0122">
                        		<img src="/images/code.gif" align="absmiddle"  onlySelectCodeset="false" plugin="codeselector" codesetid="UM" inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
                			</logic:notEqual>
                			<logic:equal name="element" property="itemid" value="e0122">
                        	   <img src="/images/code.gif" id="deptId" align="absmiddle"  onlySelectCodeset="true" afterfunc="changeLowerLevel('${element.itemid}.value','jobId');changepos('UM');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],2);" plugin="codeselector" codesetid="UM" nmodule='4' ctrltype='3' inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
                			</logic:equal>
                		</td>
                		<td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}" onclick="comCheckAll(document.getElementsByName('${element.itemid}')[0],'${element.itemid}');">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="@K">
                		<td class="RecordRow" align="left" style="border-left:none;border-top:none;" nowrap>
                			<logic:equal value="e01a1" name="element" property="itemid">
                				<input type="hidden" id="job" value="${element.itemid}">
                			</logic:equal>
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('@K');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],3);">
                			<input type="text" class="textColorWrite" name="${element.itemid}.hzvalue" style="width:200px;" onchange="fieldcode(document.getElementsByName('${element.itemid}.hzvalue')[0],1);" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);">
                        	<logic:notEqual name="element" property="itemid" value="e01a1">
                        		<img src="/images/code.gif" align="absmiddle"  onlySelectCodeset="true" afterfunc="changepos('@K');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],3);" plugin="codeselector" codesetid="@K" nmodule='4' ctrltype='3' inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
                        	</logic:notEqual>
                        	<logic:equal name="element" property="itemid" value="e01a1">
                        		<img src="/images/code.gif" align="absmiddle" id="jobId" onlySelectCodeset="true" afterfunc="changepos('@K');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],3);" plugin="codeselector" codesetid="@K" nmodule='4' ctrltype='3' inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
 							</logic:equal>                       	
                		</td>
                		<td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}" onclick="comCheckAll(document.getElementsByName('${element.itemid}')[0],'${element.itemid}');">
                		 </td>
                	</logic:equal>
                	<logic:notEqual name="element" property="codesetid" value="UN">
                		<logic:notEqual name="element" property="codesetid" value="UM">
                			<logic:notEqual name="element" property="codesetid" value="@K">
                				<td class="RecordRow" align="left" style="border-left:none;border-top:none;" nowrap>
                					<input type="hidden" name="${element.itemid}.value">
                					<input type="text" class="textColorWrite"  name="${element.itemid}.hzvalue" style="width:200px;" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);">
                        			<img src="/images/code.gif" align="absmiddle"  onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='${element.itemid}.hzvalue'  valuename="${element.itemid}.value"/>&nbsp;
                				</td>
                				<td class="RecordRow" style="border-left:none;border-top:none;border-right: none;" align="center"  nowrap>
                		 			<input type="checkbox" name="${element.itemid}">
                		 		</td>
                			</logic:notEqual>
                		</logic:notEqual>
                	</logic:notEqual>
                </logic:notEqual>
              </tr>
              <%i++;%>
              </logic:iterate>
              </table>
              </div>
            </fieldset>
           <table width="100%" border="0">
			<tr>
				<td width="80">
					<input type="radio" name="selectid" value="0" onclick="selectUpdate(document.getElementsByName('selectid')[0].value)" checked/><!-- selectUpdate('0'); -->
					<logic:equal value="1" name="indBatchHandForm" property="infor">
						查询结果 
					</logic:equal>
					<logic:notEqual value="1" name="indBatchHandForm" property="infor">
						所选记录
					</logic:notEqual>
				</td>
				<!--   //20141029  dengcan 人员信息批量修改增加对所选记录的更新操作   -->
				<logic:equal value="1" name="indBatchHandForm" property="infor"> 
					<td width="80">
						<input type="radio" name="selectid" value="2" onclick="selectUpdate(document.getElementsByName('selectid')[0].value)" />
							所选记录
					</td>		
				</logic:equal>	 
				<td width="160">
					<input type="radio" name="selectid" value="1" onclick="selectUpdate(document.getElementsByName('selectid')[0].value)"/> <!-- selectUpdate('1'); -->
					全部记录(权限范围下)
				</td>
				<td>
					<logic:equal value="1" name="indBatchHandForm" property="infor">
					<logic:equal name="indBatchHandForm" property="history" value="1">
					<input type="checkbox" name="history" value="1" />
					<bean:message key='org.autostatic.mainp.update.history' />
					</logic:equal>
					</logic:equal>
					<logic:notEqual value="1" name="indBatchHandForm" property="infor">
						<span id="hisview" style="display:none">
							<logic:equal name="indBatchHandForm" property="history" value="1">
							<input type="checkbox" name="history" value="1" />
							<bean:message key='org.autostatic.mainp.update.history' />
							</logic:equal>
						</span>
					</logic:notEqual>
					
				</td>
			</tr>
		</table>
    </td>
    <td valign="top" align="left">
    	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
    		<tr><td>
				<input style="margin-top: 9px" type="button" name="Submit4" value="<bean:message key='button.all.select'/>" onclick="checkAll();" class="mybutton">
    		</td></tr>
    		<tr><td height="18px">
    		</td></tr>
    		<tr><td>
				<input style="margin-top: 10px" type="button" name="Submit5" value="<bean:message key='button.all.reset'/>" onclick="clearAll();" class="mybutton">
    		</td></tr>
    	</table>
    </td>
  </tr>
</table>
<div style="padding-top:5px;" align="center">
	<input type="button" name="Submit" value="<bean:message key='button.ok'/>" onclick='saveSet("${indBatchHandForm.setname}","${indBatchHandForm.a_code}","${indBatchHandForm.viewsearch}","${indBatchHandForm.dbname}","${indBatchHandForm.infor}","${indBatchHandForm.history}","${indBatchHandForm.inforflag}");' Class="mybutton">
	<input type="button" name="Submit2" value="<bean:message key='button.close'/>" onclick="openClose();" Class="mybutton">
</div>
<html:hidden name="indBatchHandForm" property="count" styleId="count"/>
<html:hidden name="indBatchHandForm" property="countall" styleId="countall"/>
<html:hidden name="indBatchHandForm" property="strid" styleId="strid"/> 
<html:hidden name="indBatchHandForm" property="secount" styleId="secount"/>
<script language="javascript">
var info;
if(getBrowseVersion()){
	info=dialogArguments;
}else{//非IE浏览器获取数据 wangb 20180126
	info=new Array('<%=strid%>');
}
//浏览器兼容性  关闭弹窗方法  wangb 20180127
function openClose(){//关闭窗口方法 
	if(getBrowseVersion())
		top.close();
	else
		parent.parent.winClose();
}

<logic:equal value="1" name="indBatchHandForm" property="infor">
if(info.length>0&&info[0].length>0)
{ 
	document.getElementsByName("strid")[0].value=info[0];
	document.getElementsByName("secount")[0].value=info[0].split("`").length;
}


</logic:equal>

function selectUpdate(selectid){
	<logic:notEqual value="1" name="indBatchHandForm" property="infor">
	if(selectid=="0")
		viewHide('hisview');
	else
		viewToggle('hisview');
	</logic:notEqual>
}

function changeInfor(){
	var setname=document.getElementById("setname").value;
	thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link&setname="+setname+"&a_code=${indBatchHandForm.a_code }&dbname=${indBatchHandForm.dbname }&viewsearch=${indBatchHandForm.viewsearch }&infor=${indBatchHandForm.infor}&strid=${indBatchHandForm.strid}&path=${path}";
    window.open(thecodeurl,"_self",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=520,height=500');
}

function checkINT(e){
e=e?e:(window.event?window.event:null);
return IsIntDigit(e);
}

function changeshow(id, itemid){
	document.getElementById(id).value="<%=manager%>";
	if("b0110" == itemid) {
		document.getElementById("deptId").setAttribute("parentid", "");
		document.getElementById("jobId").setAttribute("parentid", "");
		var deptId = document.getElementsByName("e0122.value")[0];
		var deptDesc = document.getElementsByName("e0122.hzvalue")[0];
		if(deptId) {
			deptId.value = "";
			deptDesc.value = "";
		}
		
		var jobId = document.getElementsByName("e01a1.value")[0];
		var jobDesc = document.getElementsByName("e01a1.hzvalue")[0];
		if(jobId) {
			jobId.value = "";
			jobDesc.value = "";
		}
	} else if("e0122" == itemid) {
		document.getElementById("jobId").setAttribute("parentid", "");
		var jobId = document.getElementsByName("e01a1.value")[0];
		var jobDesc = document.getElementsByName("e01a1.hzvalue")[0];
		if(jobId) {
			jobId.value = "";
			jobDesc.value = "";
		}
	}
}

function changeLowerLevel(curentId,childId){
	if(document.getElementById(childId))
		document.getElementById(childId).setAttribute("parentid",document.getElementsByName(curentId)[0].value);
}
</script>
</html:form>
