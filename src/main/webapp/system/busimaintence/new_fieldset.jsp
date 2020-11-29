<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script type="text/javascript" language="javascript">
<!--
function save()
{
	if(!testId()){
		return false;
	}
    var hashVo=new ParameterSet();
    var fieldsetid=busiMaintenceForm.setid.value;
    var setdesc=busiMaintenceForm.setdesc.value;
    var nid=busiMaintenceForm.mid.value;
    if(trim(fieldsetid).length==0){
    	alert(KJG_YWZD_INFO14);
    	return;
    }
    if(trim(setdesc).length==0)
    {
      alert(KJG_YWZD_INFO15);
      return;
    }
    hashVo.setValue("fieldsetid",fieldsetid);
    hashVo.setValue("setdesc",getEncodeStr(setdesc));
    var In_parameters="opt=1";
    var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:save_ok,functionId:'1010060021'},hashVo);			
}
function save_ok(outparameters)
{
  var msg = outparameters.getValue("msg");
  if(msg=="yes")
  {
    alert(KJG_YWZD_INFO16);
    return;
   }
//    var fieldsetid=busiMaintenceForm.setid.value;
//    var setdesc=busiMaintenceForm.setdesc.value;
//    var mid=busiMaintenceForm.mid.value;
//    var changeflag="";
//    var da=document.getElementsByName("changeflag");
//    for(var i=0;i<da.length;i++)
//    {
//       if(da[i].checked==true)
//       {
//           changeflag=da[i].value;
//       }
//    }
//    var obj = new Object();
//    obj.fieldsetid=fieldsetid;
//    obj.setdesc = setdesc;
//    obj.mid=mid;
//    obj.changeflag=changeflag
//    alert(changeflag);
//    obj.type="0";
//    returnValue=obj;
//    window.close();
   busiMaintenceForm.action="/system/busimaintence/new_fieldset.do?b_save=save";
   busiMaintenceForm.submit();
}
//function ret()
//{
//    var obj = new Object();
//    obj.type="1";
//    returnValue=obj;
//    window.close();
//}
//-->
//限制代号输入
function checkNuNS(obj){
	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
function isNums(i_value){
	var fy = i_value.substring(0,1);
	rs=new RegExp("[^A-Za-z]");
	var e;
	if(e!=fy.match(rs)){
		alert(KJG_YWZD_INFO8);
		return false;	
	}
    re=new RegExp("[^A-Za-z0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}

//业务字典，薪资分析，根据userType限制新建指标集的指标集代号  jingq add 2015.01.26
function testId(){
	var dev_flag = document.getElementById("userType").value;
	var indexcode = document.getElementsByName("setid")[0].value;
	var str = indexcode.substring(indexcode.length-2,indexcode.length-1);
	var reg = /^[a-zA-Z]+$/;
	if(dev_flag==null||dev_flag=="0"||dev_flag==""||dev_flag==undefined){
		if(!reg.test(str)||str.toUpperCase()=="X"||str.toUpperCase()=="Y"||str.toUpperCase()=="Z"){
			alert("指标集代号倒数第2位必须是字母且不能为X、Y、Z。");
			return false;
		}
	} else if(dev_flag=="1"){
		if(reg.test(str)){
			alert("指标集代号倒数第2位必须为数字。");
			return false;
		}
	}
	return true;
}
//【7099】业务字典和指标体系，创建的指标字母改为大写。 jingq add 2015.02.02
function checknode(){
	var item = document.getElementById("itemid");
	var itemid = item.value;
	var reg = /^[a-zA-Z0-9_]+$/;
	var code = "";
	var index = "";
	if(itemid.length>0){
		for(var i=0;i<itemid.length;i++){
			index = itemid.substring(i,i+1);
			if(reg.test(index)){
				code += index;
			}
		}
		item.value = trim(code).toUpperCase();
	}
}
</script>

<html:form action="/system/busimaintence/new_fieldset">
	<br>
	<html:hidden name="busiMaintenceForm" property="userType" styleId="userType"/>
	<table width="400" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				<bean:message key='kjg.title.xjzbj' />
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td>-->
			<td align=center class="TableRow">
				<bean:message key='kjg.title.xjzbj' />
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
				<table border="0" cellpmoding="0" cellspacing="2"
					class="DetailTable" cellpadding="0">
					<tr class="list3">
						<td align="right" nowrap>
							<bean:message key='kjg.title.sumk' />
							:
						</td>
						<td align="left" nowrap><!-- 【7078】业务字典中定位薪资分析，然后点击新建按钮，框线颜色不对 jingq add 2015.01.28 -->
							<html:text disabled="true" name="busiMaintenceForm"
								property="mname" styleClass="text4"/>
							<html:hidden name="busiMaintenceForm" property="mid" />
						</td>
					</tr>
					<tr class="list3">
						<td align="right" nowrap>
							<bean:message key='kjg.title.zbjdh' />
							:
						</td>
						<td align="left" nowrap>
							<html:text styleId="itemid" name="busiMaintenceForm" styleClass="text4"
								property="setid" maxlength="3" onkeyup="checknode();"></html:text>
						</td>
					</tr>
					<tr class="list3">
						<td align="right" nowrap>
							<bean:message key='kjg.title.zjname' />
							:
						</td>
						<td align="left" nowrap>
							<html:text name="busiMaintenceForm" property="setdesc" styleClass="text4"></html:text>
						</td>
					</tr>
					<tr class="list3">
						<td align="center" colspan='2' nowrap>
						<logic:equal name="busiMaintenceForm" property="mid" value="35">
							<html:radio name="busiMaintenceForm" property="changeflag" value="0" disabled="true"><bean:message key='kjg.title.ybzj' /></html:radio>
							<html:radio name="busiMaintenceForm" property="changeflag" value="1" disabled="fals"><bean:message key='kjg.title.aybh' /></html:radio>
							<html:radio name="busiMaintenceForm" property="changeflag" value="2" disabled="fals"><bean:message key='kjg.title.anbh' /></html:radio>
						</logic:equal>
						<logic:notEqual name="busiMaintenceForm" property="mid" value="35">
							<html:radio name="busiMaintenceForm" property="changeflag" value="0" disabled="fals"><bean:message key='kjg.title.ybzj' /></html:radio>
							<html:radio name="busiMaintenceForm" property="changeflag" value="1" disabled="fals"><bean:message key='kjg.title.aybh' /></html:radio>
							<html:radio name="busiMaintenceForm" property="changeflag" value="2" disabled="fals"><bean:message key='kjg.title.anbh' /></html:radio>
						</logic:notEqual>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="400" align="center">
		<tr>
			<td align="center" style="height:35px;">
				<input type="button" name="sa"
					value="<bean:message key="button.ok" />" class="mybutton"
					onclick="save();" />
				<input type="button" name="rt"
					value="<bean:message key="button.return"/>" class="mybutton"
					onclick="history.back();" />
			</td>
		</tr>
	</table>
</html:form>
