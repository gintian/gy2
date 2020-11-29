<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.stat.InfoSetupForm"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@page import="com.hrms.struts.taglib.CommonData"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	InfoSetupForm form = (InfoSetupForm) session
			.getAttribute("infoSetupForm");
	ArrayList tempCondList = form.getTempCondList();
	//非IE浏览器获取标识  wangb 20180126
    String count = request.getParameter("count")==null? "":request.getParameter("count");
%>

<STYLE type=text/css>
.fixedDiv5 {
	overflow: auto;
	height: expression(document . body . clientHeight-80);
	width: expression(document . body . clientWidth-44);
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
}

.div2 {
	overflow: auto;
	width: 430px;
	height: 300px;
	line-height: 15px;
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
	border-color: #C4D8EE;
}
</STYLE>
<script type="text/javascript">
<!--
	function changetarget(v){
		//非IE浏览器中按钮没有随内容改变而改变   wangb 20180206 bug 34613
		var lasttable = document.getElementById('lasttable');		
		if(v!=null&&v!=""){
			var hashvo=new ParameterSet();          
            hashvo.setValue("unit", v);
            var request=new Request({method:'post',onSuccess:inittarget,functionId:'11080204102'},hashvo);
		}else{
			<logic:iterate id="element" name="infoSetupForm" property="volist" indexId="index">
				var obj=document.getElementsByName("<%="volist[" + index + "].value"%>")[0];
				obj.options.length=0;
	    	</logic:iterate>
	    	var waitInfo=eval("wait");	
	    	waitInfo.style.display="none";
	    	/* bug 35934   页面最下面 按钮 去了position样式 不需要设置margin样式   wangb 20180326
	    	if(!getBrowseVersion()){
	    		lasttable.style.marginTop ='2px';//上边距清空 wangb 20180206 bug 34613
	    	}
	    	*/	    
		}
	}
	function inittarget(outparamters){
		var unittargetlist=outparamters.getValue("unittargetlist");
		var seansonaldatalist=outparamters.getValue("seansonaldatalist");
		AjaxBind.bind($('seasonal'),seansonaldatalist);
		<logic:iterate id="element" name="infoSetupForm" property="volist" indexId="index">
			AjaxBind.bind(document.getElementsByName("<%="volist[" + index + "].value"%>")[0],unittargetlist);
	    </logic:iterate>
	    var waitInfo=eval("wait");	
		waitInfo.style.display="block";
		/* bug 35934   页面最下面 按钮 去了position样式 不需要设置margin样式   wangb 20180326
		if(!getBrowseVersion()){
	    	//非IE浏览器中按钮没有随内容改变而改变   wangb 20180206 bug 34613
			var lasttable = document.getElementById('lasttable');	
	    	lasttable.style.marginTop =waitInfo.clientHeight; //按钮显示为最底部  wangb 20180206 bug 34613
	    }
	    */
	}
	
	var aa;
	function localvalidata(){
		var hashvo=new ParameterSet();
		var unit=$F('unit');
		//if(unit==null||unit==""){
		//	alert("请选择关联子集！");
		//	return false;
		//}
		hashvo.setValue("unit",unit);
		var seasonal=$F('seasonal');
		//if(seasonal==null||seasonal==""){
		//	alert("请选择周期！");
		//	return false;
		//}
		hashvo.setValue("seasonal",seasonal);
		var temp=new Array();
		var volist=new Array();
		<logic:iterate id="element" name="infoSetupForm" property="volist" indexId="index">
			var obj=new Object();
			obj.id='<%=request.getParameter("id")%>';
			obj.norder='<bean:write name="element" property="itemid"/>';
			var v=(document.getElementsByName("<%="volist[" + index + "].value"%>")[0]).value;
			//if(v==null||v==""){
			//	alert("[<bean:write name="element" property="itemdesc"/>]关联指标不能为空！");
			//	return false;
			//}
			//for(var i=0;i<temp.length;i++){
			//	if(v==temp[i]){
			//		alert("关联指标不能重复！");
			//		return false;
			//	}
			//}
			temp[temp.length]=v
			obj.archive_field=v;
			volist[volist.length]=obj;
	    </logic:iterate>
	    hashvo.setValue("volist",volist);
	    hashvo.setValue("id","<%=request.getParameter("id")%>");
	    <%if (form.getInforkind().equals("1")) {%>
	    var dbnames = document.getElementsByName("dbname");
	    var dbname = "";
	    for (i = 0; i < dbnames.length; i++) {
	    	var el = dbnames[i];
	    	if (el.checked==true) {
	    		dbname +=el.value+",";
	    	}
	    }
	    dbname=dbname.substr(0,dbname.length-1);
	    hashvo.setValue("dbname",dbname);
	    
	    var o = document.getElementById("condition"); 
		var intvalue=""; 
		for(i=0;i<o.length;i++){   
			intvalue+=o.options[i].value+","; 			
		} 
	    intvalue=intvalue.substr(0,intvalue.length-1);
	    //liuy 2014-12-9 5247：自助服务/统计分析，信息集设置，选择的分类统计条件很多时，提示：保存失败！不对。 start
	    if(intvalue.length>250){
	    	alert('<bean:message key="static.cross.installType.toomuch"/>');
	    	return;
	    }
	    //liuy 2014-12-9 end
	    hashvo.setValue("condition",intvalue);
	    <%}%>
	    // 赵国栋 2014-8-13 给二维统计定义条件时，不需要下列内容 and 多维统计定义条件时，也不需要   wangb 20180807 39452
	    <%if (!"2".equals(form.getType()) && !"3".equals(form.getType())) {%>
	    hashvo.setValue("auto",$F('autoid'));
	    hashvo.setValue("unit_level",document.getElementById('unit_levelid').value);
	    hashvo.setValue("dept_level",document.getElementById('dept_levelid').value);
//	    hashvo.setValue("unit_level",document.getElementsByName('unit_levelid')[0].value);
//	    hashvo.setValue("dept_level",document.getElementsByName('dept_levelid')[0].value);
	    hashvo.setValue("ctrl",document.getElementById('ctrlid').value);
	    <%}%>
	    var request=new Request({method:'post',onSuccess:exearchive,functionId:'11080204103'},hashvo);
	}
	function exearchive(outparamters){
		var msg=outparamters.getValue("msg");
		if(msg=="ok"){
			alert('<bean:message key="static.cross.success"/>');
			aa="aa";
		}else{
			alert('<bean:message key="static.cross.saveFile"/>');
		}
	}
	
	function clearupvalue(){
		var hashvo=new ParameterSet();
		hashvo.setValue("id","<%=request.getParameter("id")%>");
		var request=new Request({method:'post',onSuccess:execlear,functionId:'11080204104'},hashvo);
	}
	function execlear(outparamters){
		var msg=outparamters.getValue("msg");
		if(msg=="ok"){
			
			var type = '${infoSetupForm.type}';
			var unit=$('unit');
			unit.value="";
			unit.text="";
			
			var objs=document.getElementsByTagName("input");
	   		for(var i=0;i<objs.length;i++){
	   			var obj=objs[i];
	   			if(obj.type=="checkbox"){
	   				obj.checked=false;
	   			}
	   		}
			
			//多维统计不需要下列内容  wangb 20180807 bug 39452
			if(type!=2 && type!=3){
				var seasonal=$('seasonal');
				seasonal.options.length=0;
				seasonal.text="";
				seasonal.value="";
			
				<logic:iterate id="element" name="infoSetupForm" property="volist" indexId="index">
					var v=(document.getElementsByName("<%="volist[" + index + "].value"%>")[0]);
					v.options.length=0;
		   		</logic:iterate>
		   		
		   		document.getElementById("autoid").value="0";
		   		document.getElementById("ctrlid").value="0";
				document.getElementById("dept_levelid").value="1"
				document.getElementById("dept_levelid").disabled=true;
				document.getElementById("unit_levelid").value="1"
				document.getElementById("unit_levelid").disabled=true;
				document.getElementById("ctrlboxid").disabled=true;
			}
			<%if (form.getInforkind().equals("1")) {%>
			var condition = document.getElementById("condition");
			while(condition.options.length>0) {        
                condition.options.remove(0);        
            }  
            <%} %>      
			alert("清空成功！");
			aa="aa";
		}else{
			alert("清空失败！");
		}
	}
	function windowclose(){
		if(getBrowseVersion()){
			parent.window.returnValue=aa;
		}else{//非IE浏览器  open弹窗返回回调方法  wangb 20180127
			parent.opener.openReturn(aa,'<%=count%>');
		}
		parent.window.close();
	}
	function condset() {
		var conddiv = document.getElementById("conddiv");
		var dw=400,dh=300,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		var url = "/general/static/commonstatic/iframe_query.jsp?src=/general/static/commonstatic/statshowsetup.do?b_showdialog=link";
		if(getBrowseVersion()){
			window.showModalDialog(url,conddiv,'dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:300px;dialogWidth:400px;center:yes;help:no;resizable:no;status:no;scroll:no');
		}else{//非IE浏览器 用open弹窗
			var iTop = (window.screen.availHeight - 30 - 320) / 2;  //获得窗口的垂直位置
			var iLeft = (window.screen.availWidth - 10 - 400) / 2; //获得窗口的水平位置 
			window.open(url,'','width=400px,height=320px,resizable=no,status=no,scroll=no,left='+iLeft+',top='+iTop);
		}
		
		//window.open(url);
	}
	function initdiv()
	{
	    //校验 节点 id为wait 在非Ie浏览器页面是否显示    bug 34828  wangb 20180226
	    if(!document.getElementById('wait'))
	    	return;
	    var waitInfo=eval("wait");	
	    var obj=document.getElementsByName("unit");       	     
      	var selectvalue="";
      	if(obj)
      	{      	         
      	     obj=obj[0];
      	     if(obj)
      	     {
      	         for(var i=0,j=0;i<obj.options.length;i++)
                 {
                    if(obj.options[i].selected)
                    {
    	                selectvalue=obj.options[i].value;    
    	                break;	
                    }
                 } 
      	    }
      	 }
	     if(selectvalue!='')
	     {
	        waitInfo.style.display="block";
	     }else
	     {
	       waitInfo.style.display="none";
	     }
	}
//-->
</script>
<hrms:themes />
<html:form action="/general/static/commonstatic/statshowsetup">
<table cellpadding="0" cellspacing="0" border="0"><!-- 【9007】、【9013】员工管理：常用统计，统计项太多时，选择关联子集后，浏览器IE8、IE9页面有问题。 jingq upd 2015.04.22 -->
<tr><td valign="top" style="width:expression(document.body.clientWidth);height:expression(document.body.clientHeight-80);">
	<div class="fixedDiv5" style=""><!-- position:absolute;left:5px;right:0px; add by xiegh date20180307 bug35252-->
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="" style="margin-top: 0;padding:0 5px 0 5px;">
			<thead>
				<tr>
					<td align="left" id="titlename" class="TableRow_top" nowrap colspan="2" style="padding:0 5px 0 5px;">
						<%--=com.hrms.frame.codec.SafeCode.decode(request
								.getParameter("name"))--%>
								<script language="javascript">
								    //通过获取父页面方式获取name属性值  wangb 20180804  bug 39386
									if(getBrowseVersion()){
										 document.getElementById('titlename').innerHTML=window.dialogArguments;
									}else{
									    if(window.opener)
									    	document.getElementById('titlename').innerHTML=window.opener.titlename;
									    else if(parent.window.opener)//具体统计条件进入显示title bug 39412 wangb 20180806
									        document.getElementById('titlename').innerHTML=parent.window.opener.titlename;
									}
								</script>
					</td>
				</tr>
			</thead>
			<%
				if (form.getInforkind().equals("1")) {
			%>
			<tr><td height="5px"></td></tr>
			<tr>
				<td align="right" width="100" valign="middle">
					<bean:message key="stat.info.setup.label.target.pre" />
				</td>
				<td align="left">
					<div style="position: relative;">
						<logic:iterate id="element" name="infoSetupForm" property="dbList"
							indexId="index">
							<div style="position: relative; float: left; width: 120px;">
								<%
									if (form.getDbname().indexOf(
																((CommonData) element).getDataValue()) != -1) {
								%>
								<input checked="checked" type="checkbox" name="dbname"
									value="<bean:write name="element" property="dataValue"/>"
									id="dbname<bean:write name="index"/>" style="cursor: pointer;" />
								<%
									} else {
								%>
								<input type="checkbox" name="dbname"
									value="<bean:write name="element" property="dataValue"/>"
									id="dbname<bean:write name="index"/>" style="cursor: pointer;" />
								<%
									}
								%>
								<label for="dbname<bean:write name="index"/>"
									style="cursor: pointer;">
									<bean:write name="element" property="dataName" />
								</label>
							</div>
						</logic:iterate>
						<div>
				</td>
			</tr>
			<tr><td height="5px"></td></tr>
			<tr>
				<td align="right" valign="top">
					<bean:message key="stat.info.setup.label.target.condition" />
				</td>
				<td align="left">
					<table border="0">
						<tr>
							<td valign="bottom">
								<span id="conddiv" style="margin: 0px; padding: 0px;"> 
									<select id="condition" name="condition" multiple="multiple"
										style="height: 150px; width: 200px;">
										<%
											if (tempCondList != null) {
														for (int i = 0; i < tempCondList.size(); i++) {
															RecordVo vo = (RecordVo) tempCondList.get(i);
										%>
										<option value="<%=vo.getString("id")%>"
											title="<%=vo.getString("name")%>"><%=vo.getString("name")%></option>
										<%
											}
													}
										%>
									</select>
					 			</span> 
							</td>
							<td valign="bottom" style="padding-bottom: 5px">
								<input class="mybutton" type="button" name="set" value="<bean:message key="button.orgmapset"/>" onclick="condset()">
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<%
				}
			%>
			<!--  赵国栋 2014-8-13 给二维统计定义条件时，不需要下列内容 -->
			<%
				//if (!"2".equals(form.getType())) {
				if ("1".equals(form.getType())) {//liuy 2015-3-16 8056：统计分析\常用统计，选中多维统计，点到信息集设置，设置下面的归档信息不应该显示出来
			%>
			<tr><td height="3px"></td></tr>
			<tr>
				<td align="right">
					<bean:message key="stat.info.setup.label.target.set" />&nbsp;
				</td>
				<td align="left">
					<html:select name="infoSetupForm" property="unit" style="width:200"
						onchange="changetarget(this.value);">
						<html:optionsCollection property="unitdatalist" value="dataValue"
							label="dataName" />
					</html:select>
					<bean:message key="stat.info.setup.label.target.set.not" />
				</td>
			</tr>
			<tr>
				<td align="right">
				<div style="margin-top:5px;">
					<bean:message key="stat.info.setup.label.seasonal" />&nbsp;
				</div>
				</td>
				<td align="left">
					<html:select name="infoSetupForm" property="seasonal"
						style="width:200;margin-top:5px;">
						<html:optionsCollection property="seansonaldatalist"
							value="dataValue" label="dataName" />
					</html:select>
				</td>
			</tr>
			<tr><td height="5px"></td></tr>
			<tr>
				<td align="center" colspan="2">
					<div id='wait' style="display: none;">
						<fieldset style="width: 90%;text-align:left;">
							<legend>
								<bean:message key="stat.info.setup.label.target" />
							</legend>
							<table align="center">
								<logic:iterate id="element" name="infoSetupForm"
									property="volist" indexId="index">
									<tr>
										<td align="right" nowrap>
											<bean:write name="element" property="itemdesc" />
										</td>
										<td align="left" nowrap>
											<html:select name="infoSetupForm"
												property='<%="volist[" + index + "].value"%>' style="width:150">
												<html:optionsCollection property="unittargetlist"
													value="dataValue" label="dataName" />
											</html:select>
											<bean:message key="stat.info.setup.label.target.note" />
										</td>
									</tr>
								</logic:iterate>
							</table>
						</fieldset>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<fieldset style="width: 90%;margin-bottom:1px;text-align:left;"><!--add by xiegh date20180307 bug35252  -->
						<legend align="left">
							自动归档
							<input type=hidden name=auto value="${infoSetupForm.auto }"
								id=autoid />
							<logic:equal name="infoSetupForm" property="auto" value="1">
								<input type=checkbox checked="checked"
									onclick="changeouto(this)" />
							</logic:equal>
							<logic:notEqual name="infoSetupForm" property="auto" value="1">
								<input type=checkbox onclick="changeouto(this)" />
							</logic:notEqual>
						</legend>
						<table align="left">
							<tr>
								<td align="right">
									单位归档层级
								</td>
								<td align="left">
									<logic:equal name="infoSetupForm" property="auto" value="1">
	    	&nbsp;<html:select name="infoSetupForm" property="unit_level"
											styleId="unit_levelid">
											<html:optionsCollection property="unit_levellist"
												value="dataValue" label="dataName" />
										</html:select>
									</logic:equal>
									<logic:notEqual name="infoSetupForm" property="auto" value="1">
	    	&nbsp;<html:select name="infoSetupForm" property="unit_level"
											styleId="unit_levelid" disabled="true">
											<html:optionsCollection property="unit_levellist"
												value="dataValue" label="dataName" />
										</html:select>
									</logic:notEqual>
								</td>
							</tr>
							<logic:notEmpty name="infoSetupForm" property="dept_levellist">
								<tr>
									<td align="right">
										部门归档
									</td>
									<td align="left">
										<input type=hidden name=ctrl value="${infoSetupForm.ctrl }"
											id=ctrlid />
										<logic:equal name="infoSetupForm" property="ctrl" value="1">
											<input type=checkbox checked="checked" id=ctrlboxid
												onclick="changectrl(this)" />
										</logic:equal>
										<logic:notEqual name="infoSetupForm" property="ctrl" value="1">
											<input type=checkbox id=ctrlboxid
												<logic:notEqual  name="infoSetupForm" property="auto"  value="1">disabled="disabled"</logic:notEqual>
												onclick="changectrl(this)" />
										</logic:notEqual>
										是否归档 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 部门层级
										<logic:notEqual name="infoSetupForm" property="ctrl" value="1">
											<html:select name="infoSetupForm" property="dept_level"
												styleId="dept_levelid" disabled="true">
												<html:optionsCollection property="dept_levellist"
													value="dataValue" label="dataName" />
											</html:select>
										</logic:notEqual>
										<logic:equal name="infoSetupForm" property="ctrl" value="1">
											<html:select name="infoSetupForm" property="dept_level"
												styleId="dept_levelid">
												<html:optionsCollection property="dept_levellist"
													value="dataValue" label="dataName" />
											</html:select>
										</logic:equal>
									</td>
								</tr>
							</logic:notEmpty>
						</table>
					</fieldset>
				</td>
			</tr>
			<%} %>
		</table>
	</div>
	</td>
	</tr>
	<tr height="15px">
	<td>
	<table id="lasttable" width="70%"  border="0" cellspacing="0" align="center" style = "margin-top:10px;"
		cellpadding="0">
		<tr>
			<td align="center" nowrap>
				<html:button property="b_save" styleClass="mybutton"
					onclick="return localvalidata();"><bean:message
						key='button.ok' /></html:button>
				<html:button property="b_clear" onclick="clearupvalue();"
					styleClass="mybutton"><bean:message
						key='button.clearup' /></html:button>
				<html:button property="b_close" styleClass="mybutton"
					onclick="windowclose();"><bean:message
						key='button.close' /></html:button>
			</td>
		</tr>
	</table>
	</td>
	</tr></table><!--add by xiegh date20180307 bug35252  -->
	

</html:form>
<script>
	$('unitview').value="";
	$('anyunit').value="";
	initdiv();
	function changeouto(obj){
		if(obj.checked){
			document.getElementById("autoid").value="1";
			document.getElementById("unit_levelid").disabled=false;
			//document.getElementById("dept_levelid").disabled=false;
			document.getElementById("ctrlboxid").disabled=false;
		}else{
			document.getElementById("autoid").value="0";
			document.getElementById("ctrlid").value="0";
			document.getElementById("dept_levelid").value="1";
			document.getElementById("unit_levelid").value="1";
			document.getElementById("dept_levelid").disabled=true;
			document.getElementById("unit_levelid").disabled=true;
			document.getElementById("ctrlboxid").checked=false;
			document.getElementById("ctrlboxid").disabled=true;
		}
	}
	
	function changectrl(obj){
		if(obj.checked){
			document.getElementById("ctrlid").value="1";
			document.getElementById("dept_levelid").disabled=false;
		}else{
			document.getElementById("ctrlid").value="0";
			document.getElementById("dept_levelid").value="1";
			document.getElementById("dept_levelid").disabled=true;
		}
	}
if(!getBrowseVersion() ||getBrowseVersion ==10){//兼容非IE兼容视图版本浏览器  wangb 20180127

	if(document.getElementById('wait')){//统计条件 id为wait节点 是否在页面显示   bug 34828 20180226 wangb 
		var form = document.getElementsByName('infoSetupForm')[0];
		var firsttable = form.getElementsByTagName('table')[0];
		firsttable.style.height='340px';
		var waitInfo=eval("wait");
		var fixedDiv5  = document.getElementsByClassName('fixedDiv5')[0];  //设置 table 外的div 宽度和高度    wangb  20180502 bug 36635
		fixedDiv5.style.width = '600px';
		fixedDiv5.style.height = '400px';
        document.getElementById('wait').getElementsByTagName('fieldset')[0].style.textAlign='left';
        var legend = document.getElementById('wait').getElementsByTagName('legend')[0];
        legend.setAttribute('align','left');
		/* bug 35934   页面最下面 按钮 去了position样式 不需要设置margin样式   wangb 20180326
		if(waitInfo && waitInfo.style.display == 'block'){
			//非IE浏览器中按钮没有随内容改变而改变   wangb 20180206 bug 34613
			var lasttable = document.getElementById('lasttable');	
	    	lasttable.style.marginTop =waitInfo.clientHeight; //按钮显示为最底部  wangb 20180206 bug 34613
		}
		*/
	}else{//统计项 页面 显示 修改样式   bug 34828 20180226 wangb 
		var fixedDiv5 = document.getElementsByClassName('fixedDiv5')[0];
		fixedDiv5.style.right = '5px';
		var fixedDiv5table = fixedDiv5.getElementsByTagName('table')[0];
		fixedDiv5table.style.padding = '';
		//var lasttable = document.getElementById('lasttable');
		//lasttable.style.marginTop = fixedDiv5.clientHeight+2;
	}
}else{//ie兼容模式去滚动条
	/*
    //兼容模式不支持getElementsByClassName wangbs 20190314
    var fixedDiv5 = document.getElementsByTagName('div')[3];
    //fixedDiv5.style.width = '585px';
    //fixedDiv5.style.height = '435px';
    */
}
</script>