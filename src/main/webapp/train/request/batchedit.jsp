<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getUnitIdByBusi("4"); 
	String path=request.getParameter("path");
	pageContext.setAttribute("path",path);
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/train/request/TrainData.js"></script>

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
			tablevos[i].checked=true;
      	 }
   	}
}
function clearAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
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
           width: 400px!important;
           width:440px;            
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
<table width="560px" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top: 0px;">
  <tr> 
    <td width="89%" height="380" align="center"> 
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td height="360" valign="top"> 
          <fieldset style="width:auto;height:auto;padding: 5px;">
     		 <legend><bean:message key='infor.menu.batupdate_m'/></legend>
     		 <div id="Wdiv" class="complex_border_color">
              <table width="100%" border="0" class="ListTable1">
              <tr> 
                <td width="10%" class="TableRow noleft" style="border-top: none;" align="center">&nbsp;</td>
                <td width="20%" class="TableRow" style="border-top: none;" align="center"><bean:message key='field.label'/></td>
                <td width="55%" class="TableRow" style="border-top: none;" align="center"><bean:message key='infor.menu.alert.value'/></td>
                <td width="15%" class="TableRow" style="border-top: none;" align="center"><bean:message key='label.edit'/></td>
              </tr>
              <logic:iterate id="element" name="courseTrainForm" property="datelist">
              <tr> 
                <td class="RecordRow noleft" align="center" nowrap><%=i%></td>
                <td class="RecordRow"  nowrap><bean:write name="element" property="itemdesc"/></td>
                <logic:equal name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="itemtype" value="D">
                		<td class="RecordRow"  nowrap>
                			<input class="textColorWrite" type="text" name="${element.itemid}.value"  extra="editor" onchange="timeCheck(document.getElementsByName('${element.itemid}.value')[0]);" style="width:150px;font-size:10pt;text-align:left" dropDown="dropDownDate">
                		 </td>
                		 <td class="RecordRow" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="itemtype" value="N">
                		<td class="RecordRow"  nowrap>
                			<logic:equal name="element" property="decimalwidth" value="0">
                			<input class="textColorWrite" type="text" name="${element.itemid}.value" maxlength="8" onkeypress="return checkINT(event);" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}')" style="width:150px;ime-mode:disabled">
                			</logic:equal>
                			<logic:notEqual name="element" property="decimalwidth" value="0">
                			<input class="textColorWrite" type="text" name="${element.itemid}.value" maxlength="8" onkeypress="return IsIntDigit1(event);" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}')" style="width:150px;ime-mode:disabled">
                			</logic:notEqual>
                		</td>
                		 <td class="RecordRow" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}">
                		 </td>
                	</logic:equal>
                	<logic:notEqual name="element" property="itemtype" value="D">
                		<logic:notEqual name="element" property="itemtype" value="N">
                			<td class="RecordRow"  nowrap>
                			    <logic:notEqual name="element" property="itemid" value="r3125">
                				    <input class="textColorWrite" type="text" name="${element.itemid}.value" style="width:150px;">
                				</logic:notEqual>
                			    <logic:equal value="r3125" name="element" property="itemid">
                				    <input type="hidden" name="${element.itemid}.value">
                					<input class="textColorWrite" type="text"  name="${element.itemid}.hzvalue" style="width:150px;" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);">
                					<img  src="/images/code.gif" align="absmiddle" onclick='javascript:openTrainPlanDialog();'/>&nbsp;
                			    </logic:equal>
                			</td>
                			<td class="RecordRow" align="center"  nowrap>
                		 		<input type="checkbox" name="${element.itemid}">
                		 	</td>
                		 </logic:notEqual>
                	</logic:notEqual>	
                </logic:equal>
                <logic:notEqual name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="codesetid" value="UN">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="comp" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UN');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],1);">
                			<input class="textColorRead" type="text" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(document.getElementsByName('${element.itemid}.hzvalue')[0],1);" title="点击清空当前数据" readonly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);changeshow('companyid');">
                			<img  src="/images/code.gif" align="absmiddle" onclick='openInputCodeDialogOrgInputPos("UN","${element.itemid}.hzvalue","<%=manager%>",1);'/>&nbsp;
                			<!-- openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",1,"1"); -->
                		</td>
                		<td class="RecordRow" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}" onclick="comCheckAll(document.getElementsByName('${element.itemid}')[0],'${element.itemid}');">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="UM">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="dep" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UM');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],2);">
                			<input class="textColorRead" type="text" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(document.getElementsByName('${element.itemid}.hzvalue')[0],1);" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);changeshow('depid')">
                			<logic:notEqual name="element" property="itemid" value="e0122">
                				<img  src="/images/code.gif" align="absmiddle" onclick='openInputCodeDialog("UM","${element.itemid}.hzvalue",document.getElementById("companyid").value,1);'/>&nbsp;
                			</logic:notEqual>
                			<logic:equal name="element" property="itemid" value="e0122">
                				<img  src="/images/code.gif" align="absmiddle" onclick='openInputCodeDialogOrgInputPos("UM","${element.itemid}.hzvalue",document.getElementById("companyid").value,1);'/>&nbsp;
                			</logic:equal>
                		</td>
                		<td class="RecordRow" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}" onclick="comCheckAll(document.getElementsByName('${element.itemid}')[0],'${element.itemid}');">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="@K">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="job" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('@K');orgchngeinfo(document.getElementsByName('${element.itemid}.value')[0],3);">
                			<input class="textColorRead" type="text" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(document.getElementsByName('${element.itemid}.hzvalue')[0],1);" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);">
                			<img  src="/images/code.gif" align="absmiddle" onclick='openInputCodeDialogOrgInputPos("@K","${element.itemid}.hzvalue",document.getElementById("depid").value,1);'/>&nbsp;
                		</td>
                		<td class="RecordRow" align="center"  nowrap>
                		 	<input type="checkbox" name="${element.itemid}" onclick="comCheckAll(document.getElementsByName('${element.itemid}')[0],'${element.itemid}');">
                		 </td>
                	</logic:equal>
                	<logic:notEqual name="element" property="codesetid" value="UN">
                		<logic:notEqual name="element" property="codesetid" value="UM">
                			<logic:notEqual name="element" property="codesetid" value="@K">
                				<td class="RecordRow"  nowrap>
                					<input type="hidden" name="${element.itemid}.value">
                					<input class="textColorRead" type="text"  name="${element.itemid}.hzvalue" style="width:150px;" title="点击清空当前数据" readOnly onclick="clearvalue(document.getElementsByName('${element.itemid}.hzvalue')[0],1);">
                					<img  src="/images/code.gif" align="absmiddle" onclick='javascript:openCondCodeDialog("${element.codesetid}","${element.itemid}.hzvalue");'/>&nbsp;
                				</td>
                				<td class="RecordRow" align="center"  nowrap>
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
              <div>
					<table>
						<tr>
							<td width="50%">
							&nbsp;&nbsp;培训班报名是否需要审批&nbsp;&nbsp;<select name="ctrl_apply.value" >
							<option value="0">否</option>
							<option value="1" selected="selected">是</option>
							</select>
                		 	<input type="checkbox" name="ctrl_apply">
							</td>
							<td width="50%">
							&nbsp;&nbsp;培训班报名是否满额控制&nbsp;&nbsp;<select name="ctrl_count.value" >
							<option value="0" selected="selected">否</option>
							<option value="1">是</option>
							</select>
                		 	<input type="checkbox" name="ctrl_count">
                		 		
							</td>
						</tr>
						<tr>
							<td width="50%" colspan="2">
							&nbsp;
							</td>
						</tr>
					</table>

			  </div>
            </fieldset>
           <table width="100%" border="0">
			<tr>
				<td height="40" width="100">
					<input type="radio" name="selectid" value="0" checked/>
						查询结果
				</td>
				<td width="180">
					<input type="radio" name="selectid" value="1"/>
					全部记录(权限范围下)
				</td>
			</tr>
		</table>
          </td>
        </tr>
      </table>
    </td>
    <td width="11%" valign="top"> 
      <table width="100%" cellpadding="0" cellspacing="0" border="0" style="margin-top: 15px;">
      <tr> 
          <td height="30" align="center" style="padding-bottom: 30px;">
			<input type="button" name="Submit4" value="<bean:message key='button.all.select'/>" onclick="checkAll();" Class="mybutton">
          </td>
        </tr>
        <tr> 
          <td height="30" align="center">
			<input type="button" name="Submit5" value="<bean:message key='button.all.reset'/>" onclick="clearAll();" Class="mybutton">
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
				<td align="center" style="padding-top: 5px;" colspan="2">
					<input type="button" name="Submit" value="<bean:message key='button.ok'/>" onclick="batchSaveSet('${courseTrainForm.a_code}');" Class="mybutton">
					<input type="button" name="Submit2" value="<bean:message key='button.close'/>" onclick="top.close();" Class="mybutton">
				</td>
			</tr>
</table>
<script language="javascript">
	function selectUpdate(selectid){
		
		if(selectid=="0")
			viewHide('hisview');
		else
			viewToggle('hisview');
	}
	
	function checkINT(e){
	e=e?e:(window.event?window.event:null);
	return IsIntDigit(e);
	}
	
	function changeshow(id){
			document.getElementById(id).value="<%=manager%>";
	}
	
	function batchSaveSet(a_code){
		var tablevos=document.getElementsByTagName("input");
		var itemid_arr=new Array();
		var itemvalue_arr=new Array();
		var ctrlapply="",ctrlcount="";
		var j=0;
		for(var i=0;i<tablevos.length;i++){
			
			if(tablevos[i].type=="checkbox"){
		     	if(tablevos[i].checked){
		     		var itemid = tablevos[i].name;
		     		if("ctrl_apply"==itemid)
		     			ctrlapply=document.getElementsByName("ctrl_apply.value")[0].value;
		     		else if("ctrl_count"==itemid)
		     			ctrlcount=document.getElementsByName("ctrl_count.value")[0].value;
		     		else {
			     		itemid_arr[j]=itemid;
			     		itemvalue=document.getElementsByName(itemid+".value")[0].value;
			     		itemvalue_arr[j] = getEncodeStr(itemvalue);
			     		j++;
		     		}
		     	}
			}
	    }
	    
	    if(itemid_arr.length<1 && (ctrlapply==null||ctrlapply.length<1)&&(ctrlcount==null||ctrlcount.length<1)){
	    	alert("请选择将要修改的指标!");
	    	return;
	    }
	    
		var selectid;
		var radios=document.getElementsByName("selectid");
	
		for(var i=0;i<radios.length;i++){
			if(radios[i].checked){
				selectid = radios[i].value;
			}
		}
		var spflag='${courseTrainForm.spflag}';
		var timeflag='${courseTrainForm.timeflag}';
		var startime='${courseTrainForm.startime}';
		var endtime='${courseTrainForm.endtime}';
		var searchstr='${courseTrainForm.hsearchstr}';
		
		var hashvo1=new ParameterSet();
		hashvo1.setValue("selectid",selectid);	
		hashvo1.setValue("a_code",a_code);
		hashvo1.setValue("spflag",spflag);
		hashvo1.setValue("timeflag",timeflag);
		hashvo1.setValue("startime",startime);
		hashvo1.setValue("endtime",endtime);
		hashvo1.setValue("searchstr",searchstr);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showcount,functionId:'2020040026'},hashvo1);
		
		function showcount(outparamters){
				
			var count=outparamters.getValue("count");
			if(count=='0'){
				alert("没有记录需要修改。");
				return;
			}
			if(!confirm(ALERT_SELECT_ITEM+count+SELECT_ITEM_RECORDE)){
				return;
			}
			
		    var hashvo=new ParameterSet();
			hashvo.setValue("itemid_arr",itemid_arr);
			hashvo.setValue("itemvalue_arr",itemvalue_arr);	
			hashvo.setValue("a_code",a_code);
			hashvo.setValue("selectid",selectid);
			hashvo.setValue("ctrlapply",ctrlapply);
			hashvo.setValue("ctrlcount",ctrlcount);
			hashvo.setValue("spflag",spflag);
			hashvo.setValue("timeflag",timeflag);
			hashvo.setValue("startime",startime);
			hashvo.setValue("endtime",endtime);
			hashvo.setValue("searchstr",searchstr);
			var request=new Request({method:'post',asynchronous:false,onSuccess:saveflag,functionId:'2020040027'},hashvo);
		}
	}
	
	function saveflag(outparamters){
		var flag = outparamters.getValue("flag");
		if(flag=="true"){
			alert("修改成功");
			window.returnValue=flag;
			window.close();
		} else
			alert("修改失败")
	}

	function openTrainPlanDialog()
    {
        var thecodeurl="/train/traincourse/trainplan.jsp";
        var return_vo= window.showModalDialog(thecodeurl, "", 
                  "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
        if(return_vo!=null){
            var arr = return_vo.split("`");
            if(arr.length!=2)
                return false;
            var r3125 = $('r3125_value');
            var r3125_viewvalue =  $('r3125_viewvalue');
            document.getElementsByName("r3125.value")[0].value=arr[0];
            document.getElementsByName("r3125.hzvalue")[0].value=arr[1];    
        }
    }
</script>
</html:form>
