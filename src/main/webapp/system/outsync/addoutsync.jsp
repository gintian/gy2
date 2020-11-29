<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
<!--
	var sub_flag = false;
	function validateToSysId(){
		var regex_id = /^[a-zA-Z]+[a-zA-Z0-9_]*/;
		if($F("vo.string(sys_id)") != "" && regex_id.test($F("vo.string(sys_id)"))){
			var vo=new ParameterSet();
			vo.setValue("sys_id",$F("vo.string(sys_id)"));	
			var request = new Request({method:'post',onSuccess:reMsg,functionId:'1010040010'},vo);
		}
	}
	
	function reMsg(outparamters){
		var flag = outparamters.getValue("flag");
		if(flag==0){
			sub_flag = false;
			$("msg").innerText="已存在！";
		}else if(flag==2){
			sub_flag = false;
			$("msg").innerText="系统代码为内部代码，不可用！";	
		}else if(flag==1){
			sub_flag = true;
			$("msg").innerText="可以使用";
		}
	}
	
	function clearFont(){
		document.getElementById("msg").innerText="";
	}
	
	function validate(){
		var regex_url = /^http\:\/\/\S*\?wsdl$/;
		var regex_id = /^[a-zA-Z]+[a-zA-Z0-9_]*/;
		if($F("vo.string(sys_id)") == ""){
        	alert("外部系统代号不能为空！");
        	return false;
        }else if(!regex_id.test($F("vo.string(sys_id)"))){
        	alert("外部系统代号的书写格式不正确，\n应该以字母开头且内部不能有不规范字符");
        	return false;
        }else if(!sub_flag){
			alert("外部系统代码已经存在，无法保存！");
			return false;
		}else if($F("vo.string(sys_name)") == ""){
        	alert("外部系统名称不能为空！");
        	return false;
        }
        if($F("vo.string(send)") == "1"){
	        if($F("vo.string(url)") == ""){
	        	alert("Webservice地址不能为空");
	        	return false;
	        }else if(!regex_url.test($F("vo.string(url)"))){
	        	alert("Webservice地址的书写格式不正确！\n http://(填写的内容)?wsdl");
	        	return false;
	        }/*else if($F("vo.string(sync_method)") == ""){
				alert("方法名称不能为空");
	        	return false;
	        }else if($F("vo.string(sync_method)") != "sendSyncMsg"){
	        	alert("方法名称不正确!必须是'sendSyncMsg'")
	        	return false;
	        }*/
        }
        var control = "";
        if(document.getElementById("control_A").checked)
        	control = control + "A,"; 
       	if(document.getElementById("control_B").checked)
       		control = control + "B,"; 
       	if(document.getElementById("control_K").checked)
       		control = control + "K,"; 
        document.getElementById("vo.string(control)").value = control;
        return true;
	}
	
	function rebreak(){
		outsyncFrom.action = "/system/outsync/outsynclist.do?b_query=link";
		outsyncFrom.submit();
	}
	
	function isShow(){
		var flag = "none";
		if($F("vo.string(send)")!=0)
			flag = "block";
		for(var i=0; i<9;i++){
			if(getBrowseVersion() && getBrowseVersion() != 10)
				document.getElementById("isshow"+i).style.display = flag;
		}
	}
	function isDis(flg,id){
		document.getElementById(id).disabled = !flg;
		if(flg)
			document.getElementById(id).style.background='';
		else
			document.getElementById(id).style.background='url()';
	}
	
	function filterange(type){
		var other_param = document.getElementById("other_param").value;
		var thecodeurl="/system/outsync/filterange.do?b_query=link&type="+type+"&other_param="+$F("vo.string(other_param)");
		var dw=700,dh=380,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    	/*
    	var returnv = window.showModalDialog(thecodeurl, "", "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    	if(returnv)
    		document.getElementById("other_param").value = returnv;
    	*/
    	//改用ext 弹窗显示  wangb 20190320
    	var win = Ext.create('Ext.window.Window',{
			id:'filterange',
			title:'过滤范围',
			width:dw+60,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.returnv)
    					document.getElementById("other_param").value = this.returnv;
				}
			}
		 }); 
	}
//-->
</script>
<html:form action="/system/outsync/addoutsync.do">
	<html:hidden name="outsyncFrom" property="vo.string(control)" />
	<html:hidden name="outsyncFrom" property="vo.string(other_param)" styleId="other_param"/>
	<table width="650" border="0" cellpadding="0" cellspacing="0"
		align="center" style="border-collapse: collapse;">
		<tr>
			<td colspan="4" align="left" class="TableRow">
				<bean:message key="label.new.set"/>
			</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle9" align="center">
				<table border="0" cellpadding="0" cellspacing="5" width="500"
					class="DetailTable" cellpadding="0" style="padding-left: 5px;padding-right: 5px;">
					<tr>
						<td height="10" colspan="2"></td>
					</tr>
					<tr>
						<td align="right" width="120">
							<bean:message key="label.external.sys.id"/>
						</td>
						<td>
							<html:text name="outsyncFrom" property="vo.string(sys_id)"
								maxlength="25" size="40" styleClass="textColorWrite" 
								onblur="validateToSysId()" onfocus="clearFont();"/>
							<font id="msg" color="red"></font>
						</td>
					</tr>
					<tr>
						<td align="right">
							<bean:message key="label.external.sys.name"/>
						</td>
						<td>
							<html:text name="outsyncFrom" property="vo.string(sys_name)" maxlength="100"
								size="40" styleClass="textColorWrite" />
						</td>
					</tr>
					<tr>
						<td align="right">
							<!--<bean:message key="label.synchronous.notice"/>-->
							<bean:message key="label.synchronous.modle"/>
						</td>
						<td>
							<html:radio name="outsyncFrom" property="vo.string(send)" value="1" onclick="isShow()"><!--<bean:message key="label.synchronous.send"/>--><bean:message key="label.synchronous.sendnotice"/></html:radio>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<html:radio name="outsyncFrom" property="vo.string(send)" value="2" onclick="isShow()"><bean:message key="label.synchronous.sendcontent"/></html:radio><br/>
							<html:radio name="outsyncFrom" property="vo.string(send)" value="3" onclick="isShow()"><bean:message key="label.synchronous.sendfieldcontent"/></html:radio>
							<html:radio name="outsyncFrom" property="vo.string(send)" value="0" onclick="isShow()"><bean:message key="label.synchronous.unsend"/></html:radio>
						</td>
					</tr>
					<tr id="isshow0">
						<td align="right">
							<bean:message key="label.Webservice.address"/>
						</td>
						<td>
							<html:text name="outsyncFrom" property="vo.string(url)" maxlength="100" size="40"
								styleClass="textColorWrite" />
						</td>
					</tr>
					<tr id="isshow1">
						<td align="right">
							<bean:message key="label.external.function.name"/>
						</td>
						<td>
							<html:text name="outsyncFrom" property="vo.string(sync_method)" maxlength="100"
								size="40" styleClass="textColorWrite" readonly="true"/>
						</td>
					</tr>
					<tr id="isshow2">
						<td align="right">
							targetNamespace
						</td>
						<td>
							<html:text name="outsyncFrom" property="vo.string(targetnamespace)" maxlength="200"
								size="40" styleClass="textColorWrite" />
						</td>
					</tr>
					<tr id="isshow8">
						<td align="right">
							<bean:message key="label.synchronous.jobid"/>
						</td>
						<td>
							<html:text name="outsyncFrom" property="jobId" maxlength="200"
								size="40" styleClass="textColorWrite" />
						</td>
						
					</tr>
					<tr id="isshow3">
						<td height="10" colspan="2"></td>
					</tr>
					<tr id="isshow4">
						<td align="right">
							<bean:message key="label.synchronous.type"/>
						</td>
						<td>
							　　　<bean:message key="label.synchronous.range"/>
						</td>
					</tr>
					<tr id="isshow5">
						<td align="right">
							<input type="checkbox" id="control_A" value="A" onclick="isDis(this.checked,'Other_param_A');"/>
							<bean:message key="label.query.employ"/>&nbsp;&nbsp;
						</td>
						<td>
							　　　<input type="button" value='<bean:message key="label.filter.range"/>' id="Other_param_A" class="mybutton" onclick="filterange(1);" disabled="disabled"/>
						</td>
					</tr>
					<tr id="isshow6">
						<td align="right">
							<input type="checkbox" id="control_B" value="B" onclick="isDis(this.checked,'Other_param_B');"/>
							<bean:message key="label.institutions"/>&nbsp;&nbsp;
						</td>
						<td>
							　　　<input type="button" value='<bean:message key="label.filter.range"/>' id="Other_param_B" class="mybutton" onclick="filterange(2);" disabled="disabled"/>
						</td>
					</tr>
					<tr id="isshow7">
						<td align="right">
							<input type="checkbox" id="control_K" value="K" onclick="isDis(this.checked,'Other_param_K');"/>
							<bean:message key="tree.kkroot.gwdesc"/>&nbsp;&nbsp;
						</td>
						<td>
							　　　<input type="button" value='<bean:message key="label.filter.range"/>' class="mybutton" id="Other_param_K" onclick="filterange(3);" disabled="disabled"/>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td>
							<html:checkbox property="vo.string(state)"
								value="1"></html:checkbox>
							<bean:message key="column.sys.valid" />
						</td>
					</tr>
					<tr>
						<td height="10" colspan="2"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="4" class="RecordRow" style="height: 35px;" nowrap="nowrap">
				<html:submit styleClass="mybutton" property="b_add" onclick="return validate();">
					<bean:message key="button.save" />
				</html:submit>
				<html:button styleClass="mybutton" property="b_return"
					onclick="rebreak();">
					<bean:message key="button.return" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script>
	if(getBrowseVersion() && getBrowseVersion !=10){
		var trs = document.getElementsByName('outsyncFrom')[0].getElementsByTagName('table')[1].getElementsByTagName('tr');
		for(var i = 0 ; i < trs.length ; i++){
			trs[i].setAttribute('align','left');
			if(i > 7)
				continue;
			var td = trs[i].getElementsByTagName('td')[0];
			td.style.paddingRight = '6px';
		}
	}
	if(!getBrowseVersion() || getBrowseVersion()==10){//非ie浏览器样式修改  wangb 20190320
		var Other_param_A = document.getElementById('Other_param_A');
		Other_param_A.style.background = 'url()';
		var Other_param_B = document.getElementById('Other_param_B');
		Other_param_B.style.background = 'url()';
		var Other_param_K = document.getElementById('Other_param_K');
		Other_param_K.style.background = 'url()';
		if(getBrowseVersion()){
			var table = document.getElementsByClassName('framestyle9')[0].getElementsByTagName('table')[0];
			var trs = table.getElementsByTagName('tr');
			for(var i=1 ; i <= 7 ; i++){
				var td = trs[i].getElementsByTagName('td')[0];
				td.style.paddingRight = '6px';
			}
		}
	}
</script>