<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.hjsj.sys.VersionControl"%>
	<%! 
		boolean bdisplayMultimedia=false;
    %>
<%
	String infor=(String)session.getAttribute("infor");
	if(request.getParameter("infor")!=null)
	{			
		infor = request.getParameter("infor");
	}
	session.setAttribute("infor", infor);
	VersionControl vc = new VersionControl();
	if(vc.searchFunctionId("03040110")){ 
	    bdisplayMultimedia=true;
		session.setAttribute("result", "true");
	}else{
		session.setAttribute("result", "false");
	}
%>
<hrms:themes></hrms:themes>
<script language="javascript">
//指标体系，根据dev_flag限制新建指标集的指标集代号  jingq add 2015.01.26
function testId(){
	var dev_flag = document.getElementById("dev_flag").value;
	var indexcode = document.getElementsByName("code")[0].value;
	var str = indexcode.substring(indexcode.length-2,indexcode.length-1);
	
	if(dev_flag==null||dev_flag=="0"||dev_flag==""||dev_flag==undefined){
		var reg = /^.[a-wA-W][a-zA-z]$/; // guodd 用户模式下 必须全部为字母，并且第二位不能为X、Y、Z
		if(!reg.test(indexcode)){//||str.toUpperCase()=="X"||str.toUpperCase()=="Y"||str.toUpperCase()=="Z"
			alert("指标集代号必须全部为字母且倒数第2位不能为X、Y、Z。");
			return false;
		}
	} else if(dev_flag=="1"){
		var reg = /^.[0-9][0-9A-Z]$/;
		var reg2 = /^.[A-W][0-9]$/;
		if(!(reg.test(indexcode) || reg2.test(indexcode))){
			alert("指标集代号规则错误,后两位应为[0-9]+[0-9或A-Z]或[A-W]+[0-9]。");
			return false;
		}
	}
	return true;
}

	function save(infor,result,btn){
		if(!testId()){
			btn.disabled="";
			return false;
		}
        var code = "";
        var name = "";
        var settype = "";
        var multimedia_file_flag = "";
        if (!getBrowseVersion() || getBrowseVersion() == 10) {//谷歌ie11
            code = document.getElementsByName('code')[0].value;
            name = document.getElementsByName('name')[0].value;
            settype = document.getElementsByName('settype')[0].value;
            multimedia_file_flag = document.getElementsByName('multimedia_file_flag')[0];
        } else {
            code = document.getElementById("code").value;
            name = document.getElementById("name").value;
            settype = document.getElementById("settype").value;
            multimedia_file_flag = document.getElementById("multimedia_file_flag");
        }
		// var code = document.getElementById("code").value;
		// var name = document.getElementById("name").value;
		// var settype = document.getElementById("settype").value;
		if(infor=="A"&&result=="true"){
			// if(document.getElementById("multimedia_file_flag").checked){
			// 	document.getElementById("multimedia_file_flag").value=1;
			// }else{
			// 	document.getElementById("multimedia_file_flag").value=0;
			// }
            if(multimedia_file_flag.checked){
                multimedia_file_flag.value=1;
            }else{
                multimedia_file_flag.value=0;
            }
		}
		var cod = code.charAt(0);
		if(code==null||code==""){
			alert(KJG_ZBTX_INF27);
			btn.disabled="";
			return;
		}
			if(settype=="A"&&cod!="A"&&cod!="a"){
				alert(KJG_ZBTX_INF28);
				btn.disabled="";
				return;
			}
			if(settype=="B"&&cod!="B"&&cod!="b"){
				alert(KJG_ZBTX_INF29);
				btn.disabled="";
				return;
			}
			if(settype=="K"&&cod!="K"&&cod!="k"){
				alert(KJG_ZBTX_INF30);
				btn.disabled="";
				return;
			}
			if(settype=="H"&&cod!="H"&&cod!="h"){
				alert("职务子集代号必须以H开始!");
				btn.disabled="";
				return;
			}
		if(settype=="W"&&cod!="W"&&cod!="w"){
				alert("工会组织子集代号必须以W开始!");
				btn.disabled="";
				return;
			}
			if(settype=="V"&&cod!="V"&&cod!="v"){
				alert("团组织子集代号必须以V开始!");
				btn.disabled="";
				return;
			}
			if(settype=="Y"&&cod!="Y"&&cod!="y"){
				alert("党组织子集代号必须以Y开始!");
				btn.disabled="";
				return;
			}
		if(code=="A00"||code=="a00"||code=="B00"||code=="b00"||code=="K00"||code=="k00"||code=="Y00"||code=="y00"||code=="V00"||code=="v00"||code=="H00"||code=="h00"||code=="W00"||code=="w00")
		{
			alert(code+"为系统指标集代号，不允许使用，请输入新的指标集代号！");
			btn.disabled="";
			return;
		}
		// if(document.getElementById("code").value.length!=3){
		// 	alert(KJG_ZBTX_INF31);
		// 	btn.disabled="";
		// 	return;
		// }
        if(code.length!=3){
            alert(KJG_ZBTX_INF31);
            btn.disabled="";
            return;
        }
		if(name==null||name==""){
			alert(KJG_ZBTX_INF32);
			btn.disabled="";
			return;
		}
		if (doValidate(name)) {
		 var hashvo=new ParameterSet();
	     hashvo.setValue("code",code);
	     hashvo.setValue("name",name);
	     var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'1020010107'},hashvo);
     	}else{
     		btn.disabled="";
     	}	
	}
	
	/**
 *过滤特殊字符，特殊字符不允许在子集名称中出现。
 *
 */
function doValidate(value) { 
	var vkeyWords="%$\#@\!~^&*()+\"'`";  
	   if(value==null || value=="") 
	   { 
	    alert("指标集名称不能为空！"); 
	    return false; 
	   } 
	   var flag = true;
	   for(var i=0;i<value.length;i++){
		   var s = value.charAt(i);
		   if(vkeyWords.indexOf(s)!=-1) 
		   { 
			   flag = false;
		       break;
		   }
	   }
	    if(!flag)
	    	alert("指标集名称不允许包含特殊字符!"); 
	   return flag; 
  } 
	
function check_ok(outparameters){
 var msg = outparameters.getValue("msg");
  if(msg=="1")
   {
       sav();
   }
   else
   {
     alert(msg);
     document.getElementById('saveBtn').disabled="";
     return;
   }
}
function sav(){
	dbinitForm.action="/system/dbinit/fieldsetlist.do?b_addsave=save";
	dbinitForm.submit();
	
}
function reback(){
	dbinitForm.action="/system/dbinit/fieldsetlist.do?b_query=bank";
	dbinitForm.submit();
}
function reflesh(){
   		document.dbinitForm.action="/system/dbinit/fieldsetlist.do?b_query=link";
	    document.dbinitForm.submit();
}
function IsDigit(){
	
	if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode> 90 && event.keyCode < 97) || (event.keyCode> 122 && event.keyCode < 127)) {
		return false;
	//【7099】业务字典和指标体系，创建的指标字母改为大写。 jingq add 2015.01.28
	} else if(event.keyCode>=97&&event.keyCode<=122){
		return event.keyCode = (event.keyCode-32);
	}
}
function checkNuNS(obj){
 	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
function isNums(i_value){
    re=new RegExp("[^A-Za-z0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}

function estop(e)
{
	//alert(e);
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	if(navigator.appName.indexOf("Microsoft")!= -1)
		return e.keyCode!=34&&e.keyCode!=39;
	else
		return e.which!=34&&e.which!=39;
}
	//过滤输入的指标名称，将长度大于50字节的部分截取    jingq   add   2014.5.7
	function CutStr(){
     	var str = document.getElementById("sname").value;
        var curStr = "";
        for(var i = 0;i<str.length;i++){
        	curStr += str.charAt(i);
            if(GetStrLength(curStr)>50){
            	document.getElementById("sname").value = str.substring(0,i);
                return;
            }
        }
     }
     //求输入的字符串长度
	function GetStrLength(str){
		var slength = 0;
	    for(var i = 0;i<str.length;i++){
	    	if(str.charCodeAt(i) >255){
	        	slength += 2;
	        } else {
	        	slength += 1;
	        }
	    }
	    return slength;
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

<html:form action="/system/dbinit/fieldsetlist">
	<html:hidden name="dbinitForm" property="dev_flag" styleId="dev_flag"/>
	<table width="400" border="0" cellpadding="0" cellspacing="0"
		align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td> -->
			
			<td align="left" colspan="4" class="TableRow">
				<bean:message key="kjg.title.xjzbj"/>
			</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle3">
				<table border="0" cellpmoding="0" cellspacing="5"
					class="DetailTable" cellpadding="0">
					<tr>
						<td align="right" nowrap valign="middle">
							<bean:message key="kjg.title.zbjlx"/>
						</td>
						<td align="left" nowrap valign="middle">
							<hrms:optioncollection name="dbinitForm" property="subsetList"
								collection="list" />
							<html:select name="dbinitForm" property="settype" size="1" style="width:200px;">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
							<bean:message key="kjg.title.zbjdh"/>
						</td>
						<td align="left" nowrap valign="middle">
						<html:text styleId="itemid" property="code" name="dbinitForm" maxlength="3" onkeyup="checknode();" styleClass="text4" style="width:200px;"/>
							
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
							<bean:message key="kjg.title.zbjmc"/>
						</td>
						<td align="left" nowrap valign="middle">
							<input type="text" style="width:200px;" id="sname" name="name" onkeypress="return estop(event);" value="" onkeyup="CutStr();" class="text4"> <!-- 限制指标集名称长度，防止名称过长无法存入数据库  jingq add 2014.5.6 -->
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
							 <bean:message key="kjg.title.type"/>
						</td>
						<td colspan="4" align="left" nowrap valign="middle">
								
								<html:radio name="dbinitForm" property="qobj" value="0">
									<bean:message key="kjg.title.ybzj"/>
								</html:radio>
								<html:radio name="dbinitForm" property="qobj" value="1">
									<bean:message key="kjg.title.aybh"/>
								</html:radio>
								<html:radio name="dbinitForm" property="qobj" value="2">
									<bean:message key="kjg.title.anbh"/>
								</html:radio>
							
						</td>
					</tr>
					<%	
					if(bdisplayMultimedia){ 
					%>
					<%
					if(infor.equals("A")){
					%>
					<tr>
						<td align="right" nowrap valign="middle">
						</td>
						<td align="left" nowrap valign="middle">
							<input type="checkbox" name="multimedia_file_flag" value="0">
								<bean:message key="kjg.title.support.multimedia"/>&nbsp;
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
					<%}%>
					<%} %>
					<!-- 添加指标集解释 输入项（setexplain） guodd 2018-04-24 -->
					<tr>
		  				<td align="right" nowrap valign="top">
							 <bean:message key="kjg.title.zbjexplain"/>
						</td>
						<td align="left" nowrap valign="middle">
							 <textarea name="setexplain" style="width:300px;height:100px"></textarea>
						</td>
		  			</tr>
				</table>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr class="list3">
						<td align="center" colspan="4" height="35px;">
						<input id="saveBtn" type="button" class="mybutton" value="<bean:message key="button.save" />" onClick='this.disabled="disabled";save("${sessionScope.infor}","${sessionScope.result}",this);' />
						<html:reset styleClass="mybutton" property="reset">
								<bean:message key="button.clear" />
							</html:reset>
							<input type="button" name="br_approve"
								value='<bean:message key="button.return"/>' class="mybutton"
								onclick="reback();">

						</td>
					</tr>
	</table>
</html:form>