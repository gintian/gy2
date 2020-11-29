<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.general.muster.hmuster.HmusterForm"%>

<script language="javascript" src="/ajax/constant.js"></script>
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
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
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
<%
	//liuy 2015-3-2 7258：组织机构-岗位管理-基准岗位-高级花名册-取数中设置排序指标不起作用
	HmusterForm hmusterForm=(HmusterForm)session.getAttribute("hmusterForm");
	String modelFlag = hmusterForm.getModelFlag();
	modelFlag = modelFlag==null?"":modelFlag;
	//liuy 2015-3-2 end
	int i = 0;
%>

<script language="javascript">
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 


/*显示*/
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}else if(document.getElementsByName(targetId)){
		target = document.getElementsByName(targetId)[0];
		target.style.display = "block";
	}
}
/*隐藏*/
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}else if(document.getElementsByName(targetId)){
		target = document.getElementsByName(targetId)[0];
		target.style.display = "none";
	}
}
function show()
{
	var bb=eval("b");
	bb.style.display="block";
	hides("dataareaview");
}

function closes()
{
	var bb=eval("b");
	bb.style.display="none"; 
	toggles("dataareaview");
}

function setGroupPoint(flag)
{
	
	var aa=eval("a"+flag);
	var bb=eval("layerview"+flag);
	var g2=eval("group2");
	var isGroupPoint = document.getElementsByName("isGroupPoint"+flag);
	if(isGroupPoint[0].checked) {	
	       if(flag=='')
	       {
	          g2.style.display="block";
	          var obj=document.getElementsByName("isGroupPoint2")[0];
	          if(obj.checked)
	          {
	             var aa2=eval("a2");
              //var bb2=eval("layerview2");
                 aa2.style.display="block";
              //bb2.style.display="block";
                 setLayerDef("2"); 
              }
	       }		
	 		aa.style.display="block"; 	
	 		setLayerDef(flag); 		
	 }else{	
	    if(flag=='')
	       {
	          var aa2=eval("a2");
              var bb2=eval("layerview2");
              aa2.style.display="none";
              bb2.style.display="none";
              document.getElementsByName("isGroupPoint2")[0].checked=false; 
              g2.style.display="none";
	       }	
	 		aa.style.display="none"; 
	 		bb.style.display="none"; 
	 }
}
function setLayerDef(flag){
	var ispointgroup = getCheckboxValue("isGroupPoint"+flag);
	var groupPoint = document.getElementsByName("groupPoint"+flag)[0].value;
	if(ispointgroup!='0'){
		if(groupPoint=='b0110'||groupPoint=='B0110'
			||groupPoint=='e0122'||groupPoint=='E0122'){
			toggles("layerview"+flag);
		}
	}
}
function goback(){
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?br_return2=return2";
	hmusterForm.submit();

}
function checkPrint(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	var tabname = tablevos[i].name;
	    	if(tabname=='printzero'){
	    		if(tablevos[i].checked==true){
	    			document.getElementsByName("zeroPrint")[0].value="1";
	    		}else{
	    			document.getElementsByName("zeroPrint")[0].value="0";
	    		}
	    	}else if(tabname=='printrow'){
	    		if(tablevos[i].checked==true){
	    			document.getElementsByName("emptyRow")[0].value="1";
	    		}else{
	    			document.getElementsByName("emptyRow")[0].value="0";
	    		}
	    	}else if(tabname=='gridprint'){
	    		if(tablevos[i].checked==true){
	    			document.getElementsByName("printGrid")[0].value="0";
	    		}else{
	    			document.getElementsByName("printGrid")[0].value="1";
	    		}
	    	}else if(tabname=='isGroupPoint'){
	    		if(tablevos[i].checked==true){
	    			document.getElementsByName("isGroupPoint")[0].value="1";
	    		}else{
	    			document.getElementsByName("isGroupPoint")[0].value="0";
	    		}
	    	}
		}
    }
    
}

function check(){	
	var checkflag = "${hmusterForm.checkflag}";
	var hashvo=new ParameterSet();
	hashvo.setValue("tabid","${hmusterForm.tabID}");
	var column = getRadioValue("column");
	hashvo.setValue("column",column);
	if(column!='0'){
		var pix = document.hmusterForm.pix.value;
		hashvo.setValue("pix",pix);
		var columnLine = getCheckboxValue("columnLine");
		hashvo.setValue("columnLine",columnLine);
	}else{
		var dataarea = getRadioValue("dataarea");
		hashvo.setValue("dataarea",dataarea);
	}
	var ispointgroup = getCheckboxValue("isGroupPoint");
	hashvo.setValue("pointgroup",ispointgroup);
	var groupPoint="";
	if(ispointgroup!='0'){
		groupPoint = document.getElementsByName("groupPoint")[0].value;
		hashvo.setValue("groupPoint",groupPoint);
		var multigroups = getCheckboxValue("multigroups");
		hashvo.setValue("multigroups",multigroups);
		if(groupPoint=='b0110'||groupPoint=='B0110'
			||groupPoint=='e0122'||groupPoint=='E0122'){
			var layerid = document.getElementsByName("layeridValue")[0].value;
			if(layerid.indexOf(",")!=-1){
				var layerArr = layerid.split(",");
				hashvo.setValue("groupOrgCodeSet",layerArr[1]);
				hashvo.setValue("layerid",layerArr[0]);
			}else{
				hashvo.setValue("groupOrgCodeSet","");
				hashvo.setValue("layerid",layerid);
			}
		}
	}
	var ispointgroup2 = getCheckboxValue("isGroupPoint2");
	hashvo.setValue("pointgroup2",ispointgroup2);
	var groupPoint2="";
	if(ispointgroup2!='0'){
		groupPoint2 = document.getElementsByName("groupPoint2")[0].value;
		hashvo.setValue("groupPoint2",groupPoint2);
		if(groupPoint2=='b0110'||groupPoint2=='B0110'||groupPoint2=='e0122'||groupPoint2=='E0122'){
			var layerid2 = document.getElementsByName("layerid2Value")[0].value;
			if(layerid2.indexOf(",")!=-1){
				var layerArr2 = layerid2.split(",");
				hashvo.setValue("groupOrgCodeSet2",layerArr2[1]);
				hashvo.setValue("layerid2",layerArr2[0]);
			}else{
				hashvo.setValue("groupOrgCodeSet2","");
				hashvo.setValue("layerid2",layerid2);
			}
		}
	}
	var showPartJob="False";
	var spj=document.getElementsByName("showPartJob")[0];
	if(spj&&spj.checked)
	{
	    showPartJob=spj.value;
	}
	hashvo.setValue("showPartJob",showPartJob);
	//if(groupPoint!=''&&groupPoint2!=''&&groupPoint.toUpperCase()==groupPoint2.toUpperCase())
	 //{
	   // alert("分组指标一和分组指标二不能选择同一个指标！");
	   // return;
	// }
	var sortcheck = getCheckboxValue("sortcheck");
	if(sortcheck=="0"){
		document.getElementsByName("sortitem")[0].value="";
		hashvo.setValue("sortitem","");
	}else{
		var sortitem = document.getElementsByName("sortitem")[0].value
		hashvo.setValue("sortitem",sortitem);
	}
	
	var printrow = getCheckboxValue("printrow");
	hashvo.setValue("emptyRow",printrow);
	
	var a;
	checkPrint();
	for(var i=0;i<document.hmusterForm.isAutoCount.length;i++)
	{
		if(document.hmusterForm.isAutoCount[i].checked)
		{
			a=document.hmusterForm.isAutoCount[i].value;
		}
	}
    if(document.hmusterForm.pix.value!=""&&document.hmusterForm.pix.value!=" ")
	{	
		if(!checkIsNum(document.hmusterForm.pix.value)&&document.hmusterForm.pix.value!=0)
		{	
			alert("栏间距需大于等于0整数");		
			return;
		}
	}
	
	if(!document.hmusterForm.columnLine.checked)
	{
		document.hmusterForm.columnLine.value="0";
		document.hmusterForm.columnLine.checked=true;
	}
	
	var request=new Request({asynchronous:false,functionId:'0550000012'},hashvo);
	if(a==1)
	{
		if(document.hmusterForm.pageRows.value=="")
		{
			alert(ENTER_DESIGNATED_NUMBER_ROWS);
			return;
		}
		
		var flag=checkIsIntNum(hmusterForm.pageRows.value);//changxy  20160829 提示改为请输入正整数
		if(flag)//checkNUM1(hmusterForm.pageRows)	
		{
			//if(checkflag=='1'){
			//	window.open("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&operateMethod=next&checkflag=1");
			//	window.close();
		//	}else{
				document.hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&isGetData=0&operateMethod=next&tabID=${hmusterForm.tabID}";
				document.hmusterForm.submit();
				document.getElementsByName("br_return")[0].disabled=true;
	            document.getElementsByName("b_next2")[0].disabled=true;
	            jinduo();
		//	}
		}
		else
		{
 		     alert("指定行数为正整数!") //changxy
			return;
		}	
		
	}
	else
	{
		//if(checkflag=='1'){
		//	window.open("/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&operateMethod=next&checkflag=1");
		//	window.close();
		//}else{
			document.hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&isGetData=0&operateMethod=next&tabID=${hmusterForm.tabID}";
			document.hmusterForm.submit();
			document.getElementsByName("br_return")[0].disabled=true;
	        document.getElementsByName("b_next2")[0].disabled=true;
	        jinduo();
		//}
	}
	
	
}
function getRadioValue(names){
	var obj = document.getElementsByName(names);
	var values = "";
	for(var i=0;i<obj.length;i++){
		if(obj[i].checked)
			values=obj[i].value;
	}
	return values;
}
function getCheckboxValue(names){
	var obj = document.getElementsByName(names)[0];
	var values = "0";
	if(obj.checked){
		values="1"
	}
	return values;
}
var dt=(window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
var dl=(window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
function setSort(){
	var sortitem = document.getElementsByName("sortitem")[0].value;
	var infor_Flag = "${hmusterForm.modelFlag}";
	if(infor_Flag=="1")
		infor_Flag="r1";
	else if(infor_Flag=="2")
		infor_Flag="z3";
	else if(infor_Flag=="3")
		infor_Flag="z3";
	
	var thecodeurl ="/gz/sort/sorting.do?b_query=link&flag="+infor_Flag+"&sortitem="+$URL.encode(sortitem);
	if(infor_Flag=="5")
		thecodeurl+="&relatTableid=${hmusterForm.relatTableid}";
	if(infor_Flag=='81')
	    thecodeurl+="&tid=${hmusterForm.tabID}";
	//19/3/15 xus ie 非兼容模式 选择排序指标 值不回填bug
    var config = {id:'returnSort_showModalDialogs',width:570,height:360,type:'0'};
	modalDialog.showModalDialogs(thecodeurl,'',config,returnSort);
	/*
	var return_vo;    
	    if(isIE){
	    	return_vo= window.showModalDialog(thecodeurl, "", 
          		"dialogWidth:540px; dialogHeight:325px;resizable:no;center:yes;scroll:yes;status:no;");  //showdialogWidth 谷歌不兼容
	    }else{
	    	window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=540px,height=325px');
	    }
	    
   if(return_vo!=null){
   		return_vo=return_vo!='not'?return_vo:"";
    	document.getElementById("sortitem").value = return_vo;
    	setSortStr(return_vo);
    }
   */
}

function returnSort(return_vo){
	if(return_vo!=null){
   		return_vo=return_vo!='not'?return_vo:"";
    	document.getElementsByName("sortitem")[0].value = return_vo;
    	setSortStr(return_vo);
    }
}

function setSortStr(sortitem){
	var arr = sortitem.split("`");
	var itemstr = "";
	if(arr.length>0){
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split(":");
			if(arr_item.length==3){
				itemstr +=arr_item[1];
				if(arr_item[2]=="0"){
					itemstr +="(降序),";
				}else{
					itemstr +="(升序),";
				}
			}
		}
	}
	document.getElementById("sortitemstr").value=itemstr;
	document.getElementById("sortitemstr").title=itemstr;
}
function setSortView(obj){
	var aa=eval("sortview");
	if(obj.checked) {			
	 	aa.style.display="block"; 	 		
	 }else{	
	 	aa.style.display="none"; 
	 }  
}
function setLayer(obj,flag){
	if(obj.value=='B0110'||obj.value=='b0110'||obj.value=='e0122'||obj.value=='E0122'){
		var aa=eval("layerview"+flag);		
	 	aa.style.display="block";
	 	var hashvo=new ParameterSet();
	 	hashvo.setValue("modelFlag","${hmusterForm.modelFlag}");
		hashvo.setValue("itemid",obj.value); 	
		hashvo.setValue("flag",flag);
		var request=new Request({asynchronous:false,onSuccess:showLayerList,functionId:'0550000014'},hashvo); 		
	}else{
		var aa=eval("layerview"+flag);		
	 	aa.style.display="none"; 	 		
	}
}
function showLayerList(outparamters){
	var layerlist=outparamters.getValue("layerlist");
	var flag=outparamters.getValue("flag");
	if(layerlist.length>0&&flag=='')
		AjaxBind.bind(hmusterForm.layeridValue,layerlist);
	if(layerlist.length>0&&flag=='2')
		AjaxBind.bind(hmusterForm.layerid2Value,layerlist);
}
function jinduo(){
	var x=document.body.clientWidth/2-300;
    var y=document.body.clientHeight/2-125;
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
</script>
<hrms:themes />
<style>
.selectmusterformaTable {
	margin-top: 6px;
}
</style>
<html:form action="/general/muster/hmuster/select_muster_name">
	<div id="wait"
		style='position: absolute; top: 285; left: 120; display: none; width: 500px; heigth: 250px'>
		<table border="1" width="50%" cellspacing="0" cellpadding="4"
			class="table_style" height="100" align="center">
			<tr>
				<td class="td_style" height=24>
					<bean:message key="hmuster.label.wait" />
				</td>
			</tr>
			<tr>
				<td style="font-size: 12px; line-height: 200%" align=center>
					<marquee class="marquee_style" direction="right" width="400"
						scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		
	</div>
	<table width="700px" border="0" cellpadding="0" cellspacing="0"
		align="center" class="selectmusterformaTable">
		<tr height="20">
			<td align='left' class='TableRow_lrt'>
				<bean:message key="hmuster.label.report_format" />
			</td>
		</tr>
		<tr>
			<td class="framestyle">
				<table width="100%" border="0" cellpmoding="0" cellspacing="0"
					class="DetailTable" cellpadding="0">
					<tr>
						<td width="50%" style="padding:5px,0px,0px,0px;margin:0px,0px,0px,0px;">
							<fieldset align="center" style="width: 97%;">
								<legend>
									<bean:message key="hmuster.label.page_rows" />
								</legend>
								<table width="100%" border="0" cellpmoding="0" cellspacing="0"
									class="DetailTable" cellpadding="0" style="padding:5px,0px,0px,0px;margin:0px,0px,0px,0px;">
									<tr>
										<td width="20%" height="30" align="right">
											<html:radio name="hmusterForm" property="isAutoCount"
												value="0" onclick="showPages('0')" />
										</td>
										<td>
											<bean:message key="hmuster.label.auto_count" />
										</td>
									</tr>
									<tr>
										<td width="20%" height="30" align="right">
											<html:radio name="hmusterForm" property="isAutoCount"
												value="1" onclick="showPages('1')" />
										</td>
										<td nowrap>
											<table width="100%">
												<tr>
													<td width="25%">
														<bean:message key="hmuster.label.user_define" />
													</td>
													<td>
														<html:text name="hmusterForm" property="pageRows"  size="5"
															maxlength="4" styleClass="text" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
						<td></td>
						<td width="45%" rowspan="2" valign="top" style="padding:5px,0px,0px,0px;margin:0px ,0px,0px,0px;">
							<fieldset align="center" style="width:90%;"><!-- 分栏控制 线宽度 changxy 20160826-->
								<legend>
									<bean:message key="hmuster.label.sort_cortrol" />
								</legend>
								<table width="100%" border="0" cellpadding="0" cellspacing="0"
									class="DetailTable" cellpadding="0">
									<tr>
										<td width="20%" height="30" align="right">
											<html:radio name="hmusterForm" property="column" value="0"
												onclick="closes()" />

										</td>
										<td>
											<bean:message key="hmuster.label.no_sort" />
										</td>
									</tr>
									<tr>
										<td width="20%" height="30" align="right">
											<html:radio name="hmusterForm" property="column" value="1"
												onclick="show()" />

										</td>
										<td>
											<bean:message key="hmuster.label.landscape_sort" />
										</td>
									</tr>
									<tr>
										<td width="20%" height="30" align="right">
											<html:radio name="hmusterForm" property="column" value="2"
												onclick="show()" />
										</td>
										<td>
											<bean:message key="hmuster.label.portrait_sort" />
										</td>
									</tr>
									<tr>
										<td width="100%" height="30" colspan="2">
											<div id="b" style="display: none;">
												<bean:message key="hmuster.label.column_space" />
												<html:text name="hmusterForm" property="pix" size="5"
													styleClass="text" />
												<bean:message key="hmuster.label.pix" />
												&nbsp;&nbsp;
												<html:checkbox name="hmusterForm" property="columnLine"
													value="1" />
												<bean:message key="hmuster.label.compart" />
											</div>
										</td>
									</tr>
									<tr>
										<td width="100%" height="30" colspan="2">
											<div id="dataareaview" style="display: none;">
												<html:radio name="hmusterForm" property="dataarea" value="0" />
												单行数据区
												<html:radio name="hmusterForm" property="dataarea" value="1" />
												多行数据区
											</div>
										</td>
									</tr>
									<tr>
										<td width="100%" height="10" colspan="2">
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td style="padding-top:5px;">
							<fieldset align="center" style="width: 97%;">
								<legend>
									<bean:message key='reportcheck.title_set' />
								</legend>

								<table width="100%" border="0" cellpmoding="0" cellspacing="0"
									class="DetailTable" cellpadding="0">
									<tr>
										<td width="20%" height="25" align="right">
											<input type="checkbox" name="printzero">
											<html:hidden name="hmusterForm" property="zeroPrint" />
										</td>
										<td>
											<bean:message key="hmuster.label.zero_print" />
										</td>
									</tr>
									<tr>
										<td width="20%" height="25" align="right">
											<logic:equal name="hmusterForm" property="emptyRow" value="1">
												<input type="checkbox" name="printrow" checked>
											</logic:equal>
											<logic:notEqual name="hmusterForm" property="emptyRow"
												value="1">
												<input type="checkbox" name="printrow">
											</logic:notEqual>
											<html:hidden name="hmusterForm" property="emptyRow" />
										</td>
										<td>
											<bean:message key="hmuster.label.empty_print" />
										</td>
									</tr>
									<tr>
										<td width="20%" height="25" align="right">
											<input type="checkbox" name="gridprint">
											<html:hidden name="hmusterForm" property="printGrid" />
										</td>
										<td>
											<bean:message key='inform.muster.not.print.secant' />
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
				</table>
				<hr style="height:1px;border-top:1px" class="complex_border_color">
				<table width="100%" border="0" cellspacing="0" class="DetailTable"
					cellpadding="0">
					<tr>
						<td width="20%" height="40">
							<html:checkbox name="hmusterForm" property="isGroupPoint"
								value="1" onclick="setGroupPoint('');" />
							<bean:message key="hmuster.label.group_point" />
							一
						</td>
						<td width="50%" align="left">
							<span id="a" style="display: none;"> <hrms:optioncollection
									name="hmusterForm" property="groupPointList" collection="list" />
								<html:select name="hmusterForm" property="groupPoint"
									onchange="setLayer(this,'');" style="width:100">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>&nbsp; <html:checkbox name="hmusterForm" property="multigroups"
									value="1" />
								<bean:message key='inform.muster.packet.not.page' /> </span>
						</td>
						<td align="left">
							<span id="layerview" style="display: none;">层级 <html:select
									name="hmusterForm" property="layeridValue" style="width:70">
									<html:optionsCollection property="layerlist" value="dataValue"
										label="dataName" />
								</html:select> </span>
						</td>
					</tr>
					<tr>
						<td width="20%" height="40">
							<span id="group2" style="display: none;"> <html:checkbox
									name="hmusterForm" property="isGroupPoint2" value="1"
									onclick="setGroupPoint('2');" />&nbsp; <bean:message
									key="hmuster.label.group_point" />二 </span>
						</td>
						<td width="50%" align="left">
							<span id="a2" style="display: none;"> <hrms:optioncollection
									name="hmusterForm" property="groupPointList" collection="list" />
								<html:select name="hmusterForm" property="groupPoint2"
									onchange="setLayer(this,'2');" style="width:100">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>&nbsp; </span>
						</td>
						<td align="left">
							<span id="layerview2" style="display: none;">层级 <html:select
									name="hmusterForm" property="layerid2Value" style="width:70">
									<html:optionsCollection property="layerlist2" value="dataValue"
										label="dataName" />
								</html:select> </span>
						</td>
					</tr>
				</table>
				<logic:equal name="hmusterForm" property="historyRecord" value="1">
					<div style="display: none">
				</logic:equal>
				<%
					String isdisplay = "none;";
					if(modelFlag.equals("3")||modelFlag.equals("6")||modelFlag.equals("8")||modelFlag.equals("11")||modelFlag.equals("14")||modelFlag.equals("21")||modelFlag.equals("41")){
						isdisplay = "block";
					}
				%>
					<hr style="height:1px;border-top:1px" class="complex_border_color" style="display: <%=isdisplay %>">
					<logic:equal name="hmusterForm" property="sortitem" value="">
						<table width="100%" border="0" cellspacing="0" class="DetailTable"
							cellpadding="0" style="display: <%=isdisplay %>">
							<tr>
								<td width="20%" height="40"><input type="checkbox" name="sortcheck" value="1"
										onclick="setSortView(this);">排序指标
								</td>
								<td id="sortview" style="display: none;margin-top: 10px;" >
									<input type="text" name="sortitemstr" id="sortitemstr" class="text">
									<html:hidden name="hmusterForm" property="sortitem" />
									<input type="button" class="mybutton" value="选择排序指标"
										onclick="setSort();">
								</td>
							</tr>
						</table>
					</logic:equal>
					<logic:notEqual name="hmusterForm" property="sortitem" value="">
						<table width="100%" border="0" cellspacing="0" class="DetailTable"
							cellpadding="0">
							<tr>
								<td width="20%" height="40">
									<input type="checkbox" name="sortcheck" value="1"
										onclick="setSortView(this);" checked>
									排序指标
								</td>
								<td id="sortview" style="margin-top: 10px;">
									<input type="text" name="sortitemstr" id="sortitemstr" class="text">
									<html:hidden name="hmusterForm" property="sortitem" />
									<input type="button" class="mybutton" value="选择排序指标"
										onclick="setSort();">
								</td>
							</tr>
						</table>
					</logic:notEqual>
				<logic:equal name="hmusterForm" property="historyRecord" value="1">
					</div>
				</logic:equal>
				<logic:equal value="3" name="hmusterForm" property="modelFlag">
					<hr style="height:1px;border-top:1px" class="complex_border_color">
					<table width="100%" border="0" cellspacing="0" class="DetailTable"
						cellpadding="0">
						<tr>
							<td height="40">
								<html:checkbox property="showPartJob" value="True"
									name="hmusterForm">显示兼职人员</html:checkbox>
							</td>
						</tr>
					</table>
				</logic:equal>
			</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr class="list3">
			<td align="left" colspan="4">
				<html:button styleClass="mybutton" property="br_return"
					onclick="goback()">
					<bean:message key="button.query.pre" />
				</html:button>
				<html:button styleClass="mybutton" property="b_next2"
					onclick="check()">
					<bean:message key="button.query.next" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<logic:notEqual name="hmusterForm" property="column" value="0">
	<script language="javascript">
show();
</script>
</logic:notEqual>
<logic:equal name="hmusterForm" property="column" value="0">
	<script language="javascript">
toggles("dataareaview");
</script>
</logic:equal>
<logic:equal name="hmusterForm" property="isGroupPoint" value="1">
	<script language="javascript">
setGroupPoint("");
</script>
</logic:equal>
<script language="javascript">
var isAutoCount="${hmusterForm.isAutoCount}";
function showPages(objvalue){
	if(objvalue=='1'){
		toggles("pageRows");
	}else{
		hides("pageRows");
	}
}

showPages(isAutoCount);
setSortStr("${hmusterForm.sortitem}");
</script>
