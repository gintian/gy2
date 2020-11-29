<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	     <meta http-equiv="X-UA-Compatible" content="IE=7;IE=8;IE=9;">
		<title>流程可用</title>
		<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
		<script language="JavaScript" src="/js/function.js"></script>
		<script language="JavaScript" src="/components/tableFactory/tableFactory.js"></script>
		<script type="text/javascript" src="/module/recruitment/recruitflow/recruitmentProcess.js"></script>
		<script language="JavaScript" src="/components/codeSelector/codeSelector.js"></script>
		<script language="JavaScript" src="../../module/recruitment/recruitment_resource_zh_CN.js"></script>
		<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
		<style>
			
			.hj-xq-right-bottom{ width:100%; border:0px #cccccc solid;min-height:256px;}
			.hj-xq-right-bottom table tr{ line-height:30px;padding:10px;} 
			.hj-xq-right-bottom table .hj-zm-cj-kcj{color:#A3A3A3;}
			.hj-xq-right-bottom table tr td{padding-left:25px;padding-top:2px;}
			.hj-xq-right-bottom label:hover{ background:#D9ECF2;}
			
			.hj-wzm-one-dinwei{width:94px;background:#FFF;position:absolute;top:80px;left:168px;z-index:999;padding:3px 0;
			display: none;word-break: break-all;word-wrap: break-word; border-style:solid; border-width:1px; border-color:#D5D5D5;}
			.hj-wzm-one-dinwei ul li{line-height:28px; text-align:center;}
			.hj-wzm-one-dinwei ul li{display:block;color:#9A9A9A;}
			.hj-wzm-one-dinwei ul li:hover{color:#549FE3;;background:#EEEEEE;}
			.hj-wzm-one-dinwei ul li a{display:block;text-align:left;padding-left: 20px;}
			
		</style>
	</head>
	<%try{ %>
	<body>
		<div id="panel" style="overflow: auto; height: 100%"></div>
		<div id="lcbase_div" style="display: none; height:100%;">
			<div id="plantype_div" class="hj-wzm-one-left"
				style="width: 90%; height: 30px; padding-left: 5px; padding-top: 10px; margin-left: 5%;">
				<a id="periodtypename" dropdownName="dropdownBox"
					onclick="dropdownPeriodType()" onblur="" style="width: 80px;" href="javascript:void(0)">
					<bean:write name="recruitflowForm" property="flowName" filter="true"/> <img dropdownName="dropdownBox" class="img-middle"
						src="/module/recruitment/image/jiantou.png" style="margin-left:5px"/> </a>&nbsp;&nbsp;<span id="ky"></span>&nbsp;&nbsp;
				<a href="/recruitment/recruitmentProcess/addFlow.do?b_query=link">创建新流程</a>&nbsp;&nbsp; 
				
				<logic:equal name="recruitflowForm" property="flowBean.isParent" value="false">
				<logic:notEqual name="recruitflowForm" property="flowBean.flow_id" value="0000000001">
					<a href="javascript:delFlow();">删除流程</a>&nbsp;&nbsp;
				</logic:notEqual>
					<a href="javascript:stopOrStartFlow();" id="ty"></a>
				</logic:equal>
				<div id="dropdownBox" tabindex="-1" class="hj-wzm-one-dinwei" 
					onblur="hideDropdownBox()">
						${recruitflowForm.flowHtml }
				</div>
			</div>
			<div class="hj-zm-hj-one" style="width: 90%; margin-left: 5%;" id="tables">
				<h2>
					流程基本信息
				</h2>
				<table width="100%" border="0" cellpadding="0" cellspacing="0"
					style="margin: 0">
					<tr>
						<td width="90%" style="padding-top: 5px;padding-left: 10px;">
							<table border="0" width="100%" cellpadding="0" cellspacing="0"
								style="margin-top: 0">
                                <tr>
									<td width="6%" valign="middle" align="right" nowrap="nowrap" >
										<font color="red" style='white-space:nowrap;'>*&nbsp;</font>流程名称：
									</td>
									<td width="68%" align="left" >
										<div id="name" style="float:left">
											<bean:write name="recruitflowForm" property="flowBean.name" filter="true"/>
										</div>
		<!-- <input type="text" id="newname" style="display: none;height:22px;width:300px;" class="inputtext" value='<bean:write name="recruitflowForm" property="flowBean.name" filter="true"/>' maxlength="100" onblur="validLen(this)"/> -->
										<div id="newdiv" style="height:22px;width:300px;float:left;border: 0 0 0 0;"></div>
									</td>
								</tr>
							<tr>
								<td width="6%" valign="middle" align="right" nowrap="nowrap" class="hj-zm-cj-one">
											<font color="red" style='white-space:nowrap;'>*&nbsp;</font>所属机构：
								</td>
								<td width="68%" align="left" class="hj-zm-cj-one">
									<div id="codename" style="float:left;margin-top:1px;">
										<bean:write name="recruitflowForm" property="codeitemdesc" filter="true"/>
									</div>
									<div id="newcodename" style="display: none;float:left;"><input type="hidden" name="codeinput_value" id="ssjg" value="${recruitflowForm.b0110 }"/>
									<input type="text" name="codeinput_view" value="${recruitflowForm.codeitemdesc }" style="height:22px;width:300px"  id='nameInput'/>
									<img style="margin-left:-19px;"  class="img-middle" src="/module/recruitment/image/xiala2.png" plugin="codeselector" codesetid="UN" ctrltype='3'  nmodule='7' inputname="codeinput_view"/></div>
								</td>
							</tr>
								<tr>
									<td width="6%" valign="top" align="right" nowrap="nowrap" class="hj-zm-cj-one" style="line-height:normal;"> 
										<font style='white-space:nowrap;'>&nbsp;&nbsp;&nbsp;</font>流程说明：
									</td>
									<td width="94%" style="line-height: 20px;" class="hj-zm-cj-one">
										<div id="conter" style="padding:0;width:80%;word-break: break-all;word-wrap: break-word;">
										${ recruitflowForm.description}
											</div>
										<textarea id="jobduty" cols="50" rows="8"  class='hj-zm-cj-gzzz'
											style="margin-top:8px;width: 80%; display: none;overflow:hidden;font-style: normal;font-size:12px;font-family: 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;"><bean:write name="recruitflowForm" property="flowBean.description" filter="true"/></textarea>
									</td>
								</tr>
							</table>
						</td>
						<input type="hidden" id="isParent" value="<bean:write name="recruitflowForm" property="flowBean.isParent" filter="true"/>"/>
						<logic:equal name="recruitflowForm" property="flowBean.isParent" value="false">
							<td valign="top" align="center" style="padding-top:5px;padding-right: 5px;">
								<p id="edit">
									<a href="javascript:void(0);" onclick="edit('1');">编辑</a>
								</p>
								<p id="save" style="display: none;" >
									<a href="javascript:void(0);" onclick="edit('2');">保存</a>&nbsp;&nbsp;
									<a href="javascript:void(0);" onclick="edit('3');">取消</a>
								</p>
							</td>
						</logic:equal>
					</tr>
				</table>

			</div>

			<div class="hj-zm-hj-two" style="width: 90%; margin-left: 5%;">
				<h2>
					招聘流程设置
				</h2>
			</div>
			<div style="margin-left:6%;margin-top:10px;">
				<input type="checkbox" ${recruitflowForm.skipflag=='1'?'checked':'' } onclick="saveSkip(this)"/>&nbsp;招聘环节必须顺序进行
			</div>
			<!-- <div class="hj-zm-hj-two-left" style="width: 90%; height:100%; margin-left: 5%; overflow-x:hidden;"> -->
				<table width="90%" height="300px" border="0" cellpadding="0" cellspacing="0" style="margin-left:6%;margin-top:10px;">
					<tr>
						<td width="90%" style="border: none; vertical-align: top;">
							<div id="zphj" style="width:100%;height:350px;"></div>
						</td>
						<td width="10%" style="border: none; vertical-align: top;"  align="center" nowrap="nowrap" > 
							<logic:equal name="recruitflowForm" property="flowBean.isParent" value="false">
								<div style="margin-bottom: 1px"><a href='javascript:insertLinks();'>插入</a></div>
								<br />
								<div style="margin-bottom: 1px"><a href='javascript:delLink();'>删除</a></div>
								<br />
							</logic:equal>
								<a href='javascript:flowLinkSet(2)'>设置</a>
						</td>
					</tr>
				</table>
			<!-- </div>-->
		</div>
		<script type="text/javascript">
		var msg = true;
		function dropdownPeriodType(){
			var box=Ext.getDom("dropdownBox");
			box.style.display ="block";
			var parentobj=Ext.getDom("plantype_div"); 
			var left = getElementLeft(parentobj)-3;
		       var top =25;
		       top=top+"px";
		       left=left+"px";
		        box.style.top=top;
		        box.style.left=left;
		        box.style.width="250px";
		}
		
		function hideDropdownBox() {	
			var box=Ext.getDom("dropdownBox");
			box.style.display ="none";
		}
		
		Ext.onReady(function(){
	     	var Panel = new Ext.Panel({      
	     		xtype:'panel',
				id:'view_panel',
				title:'招聘流程',
   			  	html:"<div id='topPanel' style='height:100%'></div>",
   			  	region:'center',
   			  	border:false
			});

	     	new Ext.Viewport({
	        	layout:'border',
	        	id:'viewPor1',
	        	title:'招聘流程',
	            padding:"0 5 0 5",
	            renderTo: Ext.get('panel'),
	         	style:'backgroundColor:white',
	            items:[Panel]
	             
	         });

			
	        
	     	Ext.getDom('topPanel').appendChild(Ext.getDom('lcbase_div'));
	     	Ext.getDom('lcbase_div').style.display="block";
	     	
	     	var view_panel = Ext.getCmp('view_panel');
		    view_panel.setAutoScroll(true);
		    var winHeight =parent.document.body.clientHeight;
		    view_panel.setHeight(winHeight);

		    if(navigator.userAgent.indexOf("MSIE")==-1){//非ie浏览器
		    	//Global.searchHj('${recruitflowForm.flowid }');
		    	//Ext.getDom("upMove").innerHTML="上&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;移";
		    	//Ext.getDom("downMove").innerHTML="下&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;移";
			 }
		    var valid = ${recruitflowForm.valid};
		    if(valid=="0" && !Ext.isEmpty(Ext.getDom("ky")) && !Ext.isEmpty(Ext.getDom("ty"))){
		    	Ext.getDom("ky").innerHTML = "流程停用";
				Ext.getDom("ty").innerHTML = "启用流程";
			}
		    if(valid=="1" && !Ext.isEmpty(Ext.getDom("ky")) && !Ext.isEmpty(Ext.getDom("ty"))){
		    	Ext.getDom("ky").innerHTML = "流程可用";
				Ext.getDom("ty").innerHTML = "停用流程";
			}
			//给document添加点击事件，当鼠标点的位置不处于流程名称时，将流程名称的下拉框隐藏
			Ext.get(document).on("click",function(e){
				e = e || window.event;
				var target = e.target || e.srcElement;
				bodyClick(target);	
			});
			autoArea();
			generateFlowName();
			Global.searchHj('${recruitflowForm.flowid }');
	    });
	    //生成流程名称输入框
	    function generateFlowName(){
	    	Ext.create('Ext.form.Panel', {
	    	    width: 300,
	    	    height:30,
	    	    renderTo: 'newdiv',
	    	    border:false,
	    	    //style:{borderRightWidth:"1px"},
	    	    items: [{
	    	        xtype: 'textfield',
	    	        name: 'name',
	    	        id:'newname',
	    	        border:false,
	    	        width:295,
	    	        maxLength:25,
	    	        maxLengthText:'流程名称不能超过25个字符长度',
	    	        regex:new RegExp("^[^\'\"<>]*$"),
	    	        regexText:'不允许输入<>\'\"',
	    	        height:22,
	    	        hidden:true,
	    	        value:'${ recruitflowForm.flowName}',
	    	        allowBlank: false,  // 表单项非空
	    	        listeners:{
						blur:{
							fn:function(cop){
								var flowname = Ext.String.trim(cop.getValue());
								if(flowname=='${recruitflowForm.flowName}')//名称未改变
									return false;
								var map = new HashMap();
								map.put("name",flowname);
								Rpc( {
						    		functionId : 'ZP0000002361',
						    		success : judSuccess
							    }, map);
						}}
	    	        }
	    	    }]
	    	});
		}
	    function judSuccess(response){
			var result = Ext.decode(response.responseText);
			var msg = result.msg;
			if(!Ext.isEmpty(msg)){
				Ext.showAlert(msg);
				Ext.getCmp("newname").setValue("");
				Ext.getCmp("newname").focus();
			}
		}
	    function bodyClick(target){
	    	var bvisible_box=false; 
	        if (target.getAttribute("dropdownName") != null) {
	            var name = target.getAttribute("dropdownName");
	            if (name=="dropdownBox"){
	                bvisible_box=true;
	            }   
	        }
	        if (target.id=="periodtypename"){
	           bvisible_box=true;
	        }
	        if (!bvisible_box){
	            var box=Ext.getDom("dropdownBox");
	            if (box.style.display=="block"){        
	             box.style.display ="none";
	            }       
	        }
		}
		function getElementLeft(element){
			var actualLeft = element.offsetLeft;
			var current = element.offsetParent;
			while (current !== null){
				actualLeft += current.offsetLeft;
				current = current.offsetParent;
			}
			return actualLeft;
		}
		var globalNewUnit="";
		function edit(flag){
			if("1" == flag){
				Ext.getDom('name').style.display="none";
				Ext.getCmp('newname').show();
				var isUsed = '${recruitflowForm.msg }';
				if(Ext.isEmpty(isUsed)&&Ext.getDom("ssjg").value!='UN`'){//当前流程没有招聘过程数据并且不是系统内置流程
					Ext.get('newcodename').show();
					Ext.getDom('codename').style.display="none";
				}
				Ext.getDom('conter').style.display="none";
				Ext.getDom('jobduty').style.display="block";
				Ext.getDom('edit').style.display="none";
				Ext.getDom('save').style.display="block";
			} else if("3"==flag){
				Ext.getDom('name').style.display="block";
				if(Ext.getDom('name').innerText)
					Ext.getCmp('newname').setValue(Ext.getDom('name').innerText);
				
				Ext.getCmp('newname').hide();
				Ext.get('newcodename').hide();
				Ext.get('codename').show();
				Ext.getDom('nameInput').value='${recruitflowForm.codeitemdesc }';
				Ext.getDom('ssjg').value='${recruitflowForm.b0110}';
				Ext.getDom('conter').style.display="block";
				Ext.getDom('jobduty').style.display="none";
				Ext.getDom('edit').style.display="block";
				Ext.getDom('save').style.display="none";
			}else{
				if(!Ext.getCmp('newname').validate()){//流程名称验证不通过
					Ext.getCmp('newname').focus();
					return;
 
				}
				
				if(trim(Ext.getCmp('newname').getValue()).length<=0){
					Ext.showAlert(PROCESS_NAME_NOT_STANDARD);
					Ext.getCmp('newname').focus();
					return; 
				} 
				
				var b0110 = Ext.getDom('ssjg').value;
				if(Ext.isEmpty(b0110)){
					Ext.showAlert(ORGANIZATION_CANNOT_EMPTY);
					Ext.get('nameInput').focus();
					return;
				}
				globalNewUnit = Ext.getDom('nameInput').value;
				var value = Ext.getDom('jobduty').value;
				var newname = Ext.getCmp("newname").getValue();

				var map = new HashMap();
		    	map.put("description", value);
		    	map.put("newname", newname);
		    	map.put("b0110", b0110);
		    	map.put("flowid", "${recruitflowForm.flowid }");
		    	Rpc( {
		    		functionId : 'ZP0000002311',
		    		success : saveSuccess
		    	}, map);
			}
		}
		
		function saveSuccess(response){
			var param = Ext.decode(response.responseText);
			var value = param.description;
			var newname = Ext.getCmp("newname").getValue();
			Ext.getDom('conter').style.display="block";
			Ext.getDom('conter').innerHTML=value;
			Ext.getDom('jobduty').style.display="none";
			
			Ext.getDom('name').innerHTML=newname;
			Ext.getDom('name').style.display="block";
			Ext.getCmp('newname').hide();
			Ext.getDom('edit').style.display="block";
			Ext.getDom('save').style.display="none";
			
			Ext.get('codename').setHtml(globalNewUnit);
			
			Ext.get('newcodename').hide();
			Ext.get('codename').show();

			var tem = Ext.getDom("periodtypename").innerHTML;
			var lef = tem.indexOf("<img")==-1?tem.substring(tem.indexOf("<IMG")):tem.substring(tem.indexOf("<img"));
			Ext.getDom("periodtypename").innerHTML = newname+lef;
			Ext.getDom(param.flowid).innerHTML = "&nbsp;"+newname+"&nbsp;";
		}

		function searchFlow(flowid){
			window.location.href="/recruitment/recruitmentProcess/searchProcess.do?b_query=link&flowid="+flowid;
		}
		//停用或启用流程
		function stopOrStartFlow(){
			var flag;
			var judge = Ext.getDom("ty").innerHTML;
			if("停用流程"==judge){
				flag = "停用";
			}
			if("启用流程"==judge){
				flag = "启用";
			}
			Ext.showConfirm("确定要"+flag+"当前的招聘流程吗？",function(y){
				if("yes"==y){
					var hashvo = new ParameterSet();
					hashvo.setValue("flowid","${recruitflowForm.flowid}");
					var request = new Request({method:"post",asynchronous:true,onSuccess:stopSuccess,functionId:"ZP0000002328"},hashvo);
				}
			},Global);
		}
		function stopSuccess(param){
			var flag;
			var judge = Ext.getDom("ty").innerHTML;
			if("停用流程"==judge){
				flag = 0;
			}
			if("启用流程"==judge){
				flag = 1;
			}
			//var isUsed = param.getValue("msg");
			//if(!Ext.isEmpty(isUsed)&&flag==0){//停用流程时进行校验，有招聘过程数据不能停用
			//	Ext.Msg.alert("提示信息","该流程已经有招聘数据，不能停用!");
			//	return;	
			//}
			var hashvo = new ParameterSet();
			hashvo.setValue("flag",flag);
			hashvo.setValue("flowid","${recruitflowForm.flowid}");
			var request = new Request({method:"post",asynchronous:true,onSuccess:updateSuccess,functionId:"ZP0000002315"},hashvo);
		}
		function updateSuccess(param){
			if(param.getValue("flag")=="1"){
				Ext.getDom("ky").innerHTML = "流程可用";
				Ext.getDom("ty").innerHTML = "停用流程";
			}
			if(param.getValue("flag")=="0"){
				Ext.getDom("ky").innerHTML = "流程停用";
				Ext.getDom("ty").innerHTML = "启用流程";
			}
		}
		
		//删除流程
		function delFlow(){
			var hashvo = new ParameterSet();
			hashvo.setValue("flowid","${recruitflowForm.flowid}");
			var request = new Request({method:"post",asynchronous:true,onSuccess:cxUsedSuccess,functionId:"ZP0000002328"},hashvo);
		}
		
		function cxUsedSuccess(outparam){
			var message = outparam.getValue("msg");
			if(message!=null&&message!="undefined"&&message!=""){
				Ext.showAlert(message);
				return false;
			}else{
				var temHtml = Ext.get("nameli").getHtml();
				if(Ext.isEmpty(temHtml)){//全部删除流程后禁制点击删除
					return;
				}
				Ext.Msg.confirm("提示信息","此流程的环节信息及各个环节的可用状态和操作</br>都将被删除,确认删除此流程吗?",function(res){
					if(res=="yes"){
						var hashvo = new ParameterSet();
						hashvo.setValue("flowid","${recruitflowForm.flowid}");
						var request = new Request({method:"post",asynchronous:true,onSuccess:delSuccess,functionId:"ZP0000002316"},hashvo);
					}
				});
			}
		}
		function delSuccess(param){
			Ext.Msg.show({
				title:'提示信息',
				modal:true,
				msg:'删除成功！',
				icon:Ext.Msg.INFO,
				buttons:Ext.Msg.OK,
				width:300,
				fn:function(){
					location.href="/recruitment/recruitmentProcess/searchProcess.do?b_query=link&flowid=''";
				}
			});
			
		}
		function saveGridData(editor,e) {
			saveNodes(e.record);
		}
		/*
		给gridPanel绑定edit事件，实现失去焦点后保存更改的record
		*/
		function afterEdit(){
			var gridEditor = Ext.getCmp("rzRegister_tablePanel");
			if (gridEditor.hasListener('edit')) {
				gridEditor.un('edit', saveGridData);
            }

            gridEditor.on('edit', saveGridData);
		}
		function pressKey(field,e){
			if(e==null || e=='')
				e = window.event;
			if(e.keyCode == 13)
				afterEdit();
		}
		//保存流程环节
		function saveNodes(updateR){
			var ids = new Array();
			var seqs = new Array();
			var custom_names = new Array();
			var remarks = new Array();
			var org_flags = new Array();
			var valids = new Array();
			/*var updateR = Ext.data.StoreManager.lookup("rzRegister_dataStore").getUpdatedRecords();//获取table的Store对象中被更改但还未通过代理同步的数据（Model对象）
			var store = Ext.data.StoreManager.lookup("rzRegister_dataStore");
			var updateR = store.getRange(0,store.getCount());//获取所有的记录*/
			Ext.each(updateR,function(record,index){//record对应每条变更的记录
				record.commit();
				seqs[index]=record.get("seq");
				custom_names[index]=record.get("custom_name");
				remarks[index]=record.get("remark");
				org_flags[index]=Ext.getDom("flag"+record.get("seq")).value;
				valids[index]=Ext.getDom(record.get("seq")).value;
				ids[index]=Ext.getDom(""+(record.get("seq"))+"").nextSibling.value;
			});
			
			var map = new HashMap();
			map.put("flowid","${recruitflowForm.flowid}");
			map.put("ids",ids);
			map.put("seqs",seqs);
			map.put("custom_names",custom_names);
			map.put("org_flags",org_flags);
			map.put("remarks",remarks);
			map.put("valids",valids);
	    	Rpc( {
	    		functionId : 'ZP0000002317',
	    		success : upSuccess
	    	}, map);
	    	
		}
		function upSuccess(response){
			var result =Ext.decode(response.responseText);
			var msg = result.message;
			if(msg=="操作执行成功!"){
			    //zxj refresh会导致从一个编辑框调到另一个编辑框发生死循环
				//Ext.getCmp("rzRegister_tablePanel").view.refresh();//刷新table
			}else{
				Ext.showAlert("保存失败",function(){
					//Ext.getCmp("rzRegister_tablePanel").view.refresh();//刷新table
				});
			}
		}
		//点击是否启用
		function change(obj){
			var valid = obj.parentNode.innerHTML;
			var id = obj.nextSibling.value;
			var tem = valid.lastIndexOf("<");
			if(obj.checked){
				obj.value=1;
				tem = "<input type=\"checkbox\" onclick=\"change(this)\" id=\""+obj.id+"\" value=\"1\" checked=\"checked\"/>"+valid.substring(tem);
			}else{
				obj.value=0;
				tem = "<input type=\"checkbox\" onclick=\"change(this)\" id=\""+obj.id+"\" value=\"0\"/>"+valid.substring(tem);
			}
			Ext.data.StoreManager.lookup("rzRegister_dataStore").each(function(record){
				if(record.get("valid").indexOf(id)!=-1){
					record.set("valid",tem);
					saveNodes(record);
				}
			});
		}
		//点击是否启用用人单位处理环节
		function changeOrg_flag(obj){
			var valid = obj.parentNode.innerHTML;
			var id = obj.nextSibling.value;
			var tem = valid.lastIndexOf("<");
			if(obj.checked){
				obj.value=1;
				tem = "<input type=\"checkbox\" onclick=\"changeOrg_flag(this)\" id=\""+obj.id+"\" value=\"1\" checked=\"checked\"/>"+valid.substring(tem);
			}else{
				obj.value=0;
				tem = "<input type=\"checkbox\" onclick=\"changeOrg_flag(this)\" id=\""+obj.id+"\" value=\"0\"/>"+valid.substring(tem);
			}
			Ext.data.StoreManager.lookup("rzRegister_dataStore").each(function(record){
				if(record.get("org_flag").indexOf(id)!=-1){
					record.set("org_flag",tem);
					saveNodes(record);
				}
			});
		}
		//删除流程环节
		function delLink(){
			var selectR = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();//获取选中的记录
			if(selectR.length==0){
				Ext.showAlert("请选择需要删除的流程环节!");
				return;
			}
			var delLinks = "";
			Ext.each(selectR,function(rec,index){
				delLinks += Ext.getDom(rec.get("seq")).nextSibling.value+",";
			});
			var hashvo = new ParameterSet();
			hashvo.setValue("delLinks",delLinks.substring(0,delLinks.length-1));
			var request = new Request({method:"post",asynchronous:true,onSuccess:judgeSuccess,functionId:"ZP0000002328"},hashvo);
		}
		var globalTemp;
		function judgeSuccess(outparam){
			var message = outparam.getValue("msg");
			if(message!=null&&message!="undefined"&&message!=""){
				Ext.showAlert(message);
				return false;
			}else{
				var selectR = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();//获取选中的记录
				globalTemp=selectR;
				Ext.Msg.confirm("提示信息","确认要删除选中的招聘环节吗?",function(rs){
					if(rs=="yes"){
						var total="";
						Ext.data.StoreManager.lookup("rzRegister_dataStore").each(function(record){
							total=total+record.get("sysName")+",";
						});
						var ids = new Array();
						var seqs = new Array();
						var msg="";
						var custom_names = new Array();
						var leave="";
						Ext.each(selectR,function(record,index){
							var tem = record.get("sysName")+",";
							if(total.indexOf(tem)!=-1){
								if(Ext.isEmpty(leave))
									leave=total.replace(tem,"");
								else
									leave = leave.replace(tem,"");
							}
							if((leave.indexOf("部门筛选")==-1&&leave.indexOf("人力筛选")==-1)||leave.indexOf("入职")==-1){//剩余环节中不包括部门筛选、人力筛选或入职
								msg="第"+record.get("seq")+"行\""+record.get("custom_name")+"\"招聘环节不允许删除";
								return false;
							}
							ids[index]=Ext.getDom(""+(record.get("seq"))+"").nextSibling.value;
							custom_names[index]=record.get("custom_name");
							seqs[index]=record.get("seq");
						});
						if(msg==""){
							var map = new HashMap();
							map.put("flowid","${recruitflowForm.flowid}");
							map.put("ids",ids);
							map.put("seqs",seqs);
							map.put("custom_names",custom_names);
					    	Rpc( {
					    		functionId : 'ZP0000002318',
					    		success : delLinkSuccess
					    	}, map);
							/*var hashvo = new ParameterSet();
							hashvo.setValue("flowid","${recruitflowForm.flowid}");
							hashvo.setValue("ids",ids);
							hashvo.setValue("seqs",seqs);
							hashvo.setValue("custom_names",custom_names);
							var request = new Request({method:"post",asynchronous:true,onSuccess:delLinkSuccess,functionId:"ZP0000002318"},hashvo);*/
						}else{
							Ext.Msg.show({
								title: '提示信息',
								msg: msg,
								buttons: Ext.Msg.OK, //确认按钮
								icon: Ext.Msg.INFO  // 感叹号标识
							});
						}
					}
				});
			}
		}
		function delLinkSuccess(outparams){
			var outparam = Ext.decode(outparams.responseText);
			var message = outparam.msg;
			var records = outparam.records;
			if(!Ext.isEmpty(message)){
				Ext.showAlert(message);
				return false;
			}else{
				Ext.showAlert("删除成功！",function(){
					Global.opSuccess("del",records);
				});
			}
		}
		//获取选择记录的最小序号
		function getSeq(){
			var seq=0;
			var selectR = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
			Ext.each(selectR,function(record,index){
				var se = record.get("seq");
				if(index==0){
					seq=se;
				}else if(se<seq){
					seq=se;
				}
			});
			return seq;
		}
		//插入流程环节
		function insertLinks(){
			var seq=getSeq(); 
			if(seq==0){
				Ext.showAlert("请选择一条需要插入环节的记录"); 
				return;
			}
			var panel1 = Ext.widget("panel",{
				//title:'系统内置招聘环节',
				header:{xtype:'header',style:{borderRightWidth:"1px"},title:'系统内置招聘环节'},
				flex:5,
				id:'panel1',
				cls:'hj-xq-right-bottom',
				//layout:'anchor',//表单中fields被竖直排列，
				style:{borderRight:"solid 1px #cccccc",borderBottom:"solid 1px #cccccc"},
				border:false,
				layout:{
        			type:'table',
        			columns:1,
					tableAttrs:{
						style:{
							width:'100%',
							padding:5
						}
					}
            	},
				defaultType:'label',
				items:[
						{id:'s1',html:'<div style="float:left">人力筛选</div><div style="float:left">（可创建多个人力筛选环节）</div><input id="hj0" value="01" type="hidden"/><div style="float:right;"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s11\')" id="s11">添加</a></div>'},//01
						{id:'s2',html:'<div style="float:left">部门筛选</div><div style="float:left">（可创建多个部门筛选环节）</div><input id="hj1" value="02" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s21\')" id="s21">添加</a></div>'},//02
						{id:'s3',html:'<div style="float:left">笔试</div><div style="float:left">&nbsp;</div><input id="hj2" value="03" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s31\')" id="s31">添加</a></div>'},//03
						{id:'s4',hidden:true,html:'<div style="float:left">人才测评</div><div style="float:left">&nbsp;</div><input id="hj3" value="04" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s41\')" id="s41">添加</a></div>'},//04
						{id:'s5',html:'<div style="float:left">面试</div><div style="float:left">（可创建多个面试环节）</div><input id="hj4" value="05" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s51\')" id="s51">添加</a></div>'},//05
						{id:'s6',html:'<div style="float:left">背景调查</div><div style="float:left">&nbsp;</div><input id="hj5" value="06" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s61\')" id="s61">添加</a></div>'},//06
						{id:'s7',html:'<div style="float:left">录用审批</div><div style="float:left">&nbsp;</div><input id="hj6" value="07" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s71\')" id="s71">添加</a></div>'},//07
						{id:'s8',html:'<div style="float:left">Offer</div><div style="float:left">&nbsp;</div><input id="hj7" value="08" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s81\')" id="s81">添加</a></div>'},//08
						{id:'s9',hidden:true,html:'<div style="float:left">体检</div><div style="float:left">&nbsp;</div><input id="hj8" value="09" type="hidden"/><div style="float:right"><a style="display: none;margin-right:25px" href="javascript:addZphj(\'s91\')" id="s91">添加</a></div>'}//09
						//{id:'s9',html:'<div style="float:left">入职</div><div style="float:left">&nbsp;</div><div style="float:right"><a style="display: none;" href="javascript:addZphj(\'s91\')" id="s91">添加</a></div>'}
					  ]
				
			});

			var panels = Ext.widget("panel",{
				id:'panel2',
				header:{xtype:'header',style:{borderBottomWidth:"1px"},title:'已选招聘环节'},
				//title:'已选招聘环节',
				flex:5,
				cls:'hj-xq-right-bottom',
				autoScroll: true,
				style:{borderBottom:"solid 1px #cccccc"},
				border:false,
                layout:{
                			type:'table',
                			columns:1,
							tableAttrs:{
								style:{
									width:'100%',
									padding:5
								}
							}
                    	},
				 defaultType:'label'
			});
			var win = Ext.create('Ext.window.Window',{
				id:'insertWindow',
				title:'插入流程环节',
				height:430,
				autoScroll:true,
				width:650,
				buttonAlign:'center',
				modal:true,//掩饰窗口背后的一切
				layout:{type:'hbox',align:'stretch'},
				items:[panel1,panels],
				buttons:[
							{id:'sure',text:'确定',handler:function(){
								insertSave(seq);
								win.close();
							}}
						],
				listeners:{
					show:{
						fn:function(){
							var exits = "";
							var pre="";
							var nex="";
							var store = Ext.data.StoreManager.lookup("rzRegister_dataStore");
							store.each(function(record){
								var sysname=record.get("nodeid");
								exits=exits+sysname+",";
								if(record.get("seq")==seq-1)//获取选中第一条记录的前一条系统名称
									pre=sysname;
								
								if(record.get("seq")==seq){
									if(parseInt(record.get("nodeid"))<10)
										nex = parseInt(record.get("nodeid").charAt(1));
									else
										nex = parseInt(record.get("nodeid"));
								}
							});
							var preid = 0;
							panel1.items.each(function(item,index){
								if(Ext.isEmpty(item.getInsertPosition(item.getId())))
									return;
								var tem = Ext.getDom("hj"+index).value;
								if(tem==pre){
									preid=parseInt(item.getId().charAt(1));
								}
								var nexid=parseInt(item.getId().charAt(1));
								//可以选多个的环节和没选的环节都出现
								if((tem!="01"&&tem!="02"&&tem!="05"&&exits.indexOf(tem+",")!=-1)||tem=="10")
									//移除的时候需要将item对应的tr移除，避免环节的位置显示有错位
									item.getEl().parent().parent().destroy();
//								if(preid==0||(nexid>nex&&nexid!=2)){//只能添加选中记录的上条记录和下条记录中间的流程环节
//									if(Ext.isEmpty(Ext.getDom(item.getId())))//已删除的直接继续
//									 	return;
//									if(seq==1||nex==3){
//										if(tem!="01"&&tem!="02")
//											item.getEl().parent().parent().destroy();
//									}else
//										item.getEl().parent().parent().destroy();
//								}
							});
							
						}
					}
				}
			}).show();
			//给panel每个item绑定事件
			var a = Ext.getCmp("panel1").items.items;
			Ext.each(a,function(item,index,aa){
				var id1 = "s"+(index+1);
				var id2 = "s"+(index+1)+"1";
				//排除掉已经删除的环节
				if(Ext.isEmpty(Ext.get(id1)) || Ext.isEmpty(Ext.get(id2)))
					return;
				
				if(Ext.getDom(id1).lastChild.firstChild!=null&&Ext.getDom(id1).lastChild.firstChild.innerText!=""){
					Ext.get(id1).parent().on("mouseover",function(){
						onmouse(id2,"1");
					});
					Ext.get(id1).parent().on("mouseout",function(){
						onmouse(id2,"0");
					});
				}
			});
			
		}
		function insertSave(seq){
			var nodeids="";
			var nodenames="";
			var hjs = Ext.getCmp("panel2").items.items;
			var chooseLen = hjs.length;
			if(chooseLen==0||Ext.isEmpty(Ext.getDom(hjs[chooseLen-1].id).innerHTML)){//未选择环节或已选环节或删除
				return false;
			}
			Ext.each(hjs,function(item,index){
				var doms =Ext.getDom(item.id);
				if(!Ext.isEmpty(doms.innerHTML)){//去除删除的环节
					if(index==0){
						nodeids+=doms.lastChild.lastChild.value;
						nodenames+=doms.firstChild.innerHTML;
					}else{
						nodeids+=","+doms.lastChild.lastChild.value;
						nodenames+=","+doms.firstChild.innerHTML;
					}
				}
			});
			if(nodeids.indexOf(",")==0){
				nodeids = nodeids.substring(1);
				nodenames = nodenames.substring(1);
			}
			Ext.Msg.confirm("提示信息","确认插入这些环节吗?",function(res){
				if(res=="yes"){
					var map = new HashMap();
					map.put("flowid","${recruitflowForm.flowid}");
					map.put("node_id",nodeids);
					map.put("seq",seq);
					map.put("custom_name",nodenames);
			    	Rpc( {
			    		functionId : 'ZP0000002319',
			    		success : crSuccess
			    	}, map);
				}else{
					return;
				}
			});
		}
		function crSuccess(response){
			var outparam = Ext.decode(response.responseText);
			var msg = outparam.msg;
			var seq = outparam.seq;
			var records = outparam.records;
			if(msg=="success"){
				//Ext.Msg.alert("提示信息","保存成功",function(){
					Global.opSuccess("insert",records,msg);
				//});
			}
			if(msg=="failure"){
				Ext.showAlert("保存失败");
			}
		}
		//鼠标移进移出
		function onmouse(id,flag){
			if(id.indexOf("r")==-1){
				var xtmc =  Ext.String.trim(Ext.get(""+id+"").parent().parent().first().getHtml());
				if(xtmc!="人力筛选"&&xtmc!="部门筛选"&&xtmc!="面试"){
					var rId = "r"+id;
					if(!Ext.isEmpty(Ext.getDom(""+rId+""))){//判断已选流程环节中是否已有该环节
						Ext.getDom(""+id+"").style.display="none";
						return false;
					}
				}
			}
			if("1"==flag){
				if(!Ext.isEmpty(Ext.getDom(id))){
					Ext.getDom(id).style.display="block";
				}
			}
			else {
				if(!Ext.isEmpty(Ext.getDom(id))){
					Ext.getDom(id).style.display="none";
				}
			}
		}
		//校验流程名称
		function validLen(obj){
	    	if(Ext.String.trim(obj.value)==""){
				Ext.showAlert("流程名称不能为空");
				Ext.get(obj).focus();
				return false;
			}
			if(IsOverStrLength(obj.value,100)){
				Ext.showAlert("流程名称长度不能大于100");
				Ext.get(obj).focus();
			}
		}
		//校验用户自定义环节名称
		function valid(obj,id){
			var res = true;
			if(Ext.String.trim(obj.value)==""){
				Ext.showAlert("流程环节名称不能为空");
				res= false;
			} 
			if(IsOverStrLength(obj.value,50)){
				Ext.showAlert("流程环节名称长度不能大于25"); 
				res= false;
			}
			obj.value=oldname;
			Ext.getDom(id).innerHTML=oldname;
			Ext.getDom(id).nextSibling.style.display="block";		
			Ext.getDom(id).nextSibling.nextSibling.style.display="block";
			msg = true;
			return res;
		}
		var oldname;
		//重命名
		function rename(id){
			if(!msg)
				return;
			Ext.getCmp("sure").setDisabled(true);
			msg = false;
			Ext.getDom(id).nextSibling.style.display="none";		
			Ext.getDom(id).nextSibling.nextSibling.style.display="none";		
			var value = Ext.getDom(id).innerHTML;
			oldname=value;
			var input = "<input type='text' id='text_"+id+"' onkeypress=\"EnterPress(event,this,'"+id+"')\" onkeydown=\"EnterPress('',this,'"+id+"')\" style='' value='"+value+"'/> (按回车键保存)";
			Ext.getDom(id).innerHTML=input;
			Ext.getDom('text_'+id).focus();
		}
		function EnterPress(e,obj,id){
			if(e==null || e=='')
				e = window.event; 
			if(e.keyCode == 13){ 
				var value = obj.value;
				if(valid(obj,id)){
					Ext.getDom(id).innerHTML=value;
					Ext.getDom(id).nextSibling.style.display="block";		
					Ext.getDom(id).nextSibling.nextSibling.style.display="block";
					msg = true;
				}
				Ext.getCmp("sure").setDisabled(false);
			}
		}
		var creament=0;
		var totalIds="";
		//添加流程环节
		function addZphj(id){
			/*
				对于人力筛选、部门筛选、入职添加多次进行处理
			*/
			var sysId = "0"+id.charAt(1);
			
			var hjmc = Ext.String.trim(Ext.getDom(id).parentNode.parentNode.firstChild.innerHTML);
			var r1 = "r_1_"+id;
			var r2 = "r_2_"+id;
			var r3 = "r"+id;
			//多次添加同一环节
			if(totalIds.indexOf(sysId)!=-1){
				var mc = Ext.String.trim(hjmc);
				if(mc=="人力筛选" || mc=="部门筛选" || mc=="面试"){
					r1=r1+creament;
					r2=r2+creament;
					r3=r3+creament;
					creament = creament+1;
				}
			}
			totalIds+=sysId+",";//
			var panel2 = Ext.getCmp('panel2');
			panel2.add({html:'<div style="float:left;clear:both;width:33%" id="'+r3+'">'+hjmc+'</div>'+
				'<div style="float:right;width:33%"><a style="display: none;" href="javascript:del(\''+r2+'\',\''+sysId+'\')" id="'+r2+'">&nbsp;&nbsp;删除&nbsp;&nbsp;</a></div>'+
				'<div style="float:right;width:33%"><a style="display: none;" href="javascript:rename(\''+r3+'\')" id="'+r1+'">&nbsp;&nbsp;重命名&nbsp;&nbsp;</a><input type="hidden" value="'+sysId+'"/></div>'});
			Ext.get(r3).parent().parent().on("mouseover",function(){
				onmouseR(r1,r2,1);
			});
			Ext.get(r3).parent().parent().on("mouseout",function(){
				onmouseR(r1,r2,0);
			});
			var mc = Ext.String.trim(hjmc);
			if(mc!="人力筛选"&&mc!="部门筛选"&&mc!="面试"){
				Ext.getDom(id).style.display="none";
			}
		}
		//删除
		function del(id,sysId){
			totalIds=totalIds.replace(sysId,"");
			Ext.DomHelper.overwrite(Ext.getDom(id).parentNode.parentNode,"");
		}
		function onmouseR(aid1,aid2,flag){
			if(!msg)
				return;
			onmouse(aid1,flag);
			onmouse(aid2,flag);
		}

		//招聘环节设置
		function flowLinkSet(flag){
			var selectR = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
			if(selectR.length==0){
				Ext.showAlert("请选择需要设置的流程环节");
				return;
			}
			if(selectR.length>1){
				Ext.showAlert("只能选择一个流程环节进行设置");
				return;
			}
			var linkid = Ext.getDom(selectR[0].get("seq")).nextSibling.value;
			var sysName =selectR[0].get("sysName");
			var isParent = Ext.getDom("isParent").value;
			location.href="/recruitment/recruitmentProcess/searchLink.do?b_query=link&linkid="+linkid+"&sysName="+$URL.encode(sysName)+"&flag="+flag+"&isParent="+isParent;
		}

		//必须含有body元素才可屏蔽backspace
		window.onload=function(){
			document.getElementsByTagName("body")[0].onkeydown =function(e){            
		        //获取事件对象
		        var event = e?e:window.event;
				var elem = event.relatedTarget || event.srcElement || event.target ||event.currentTarget;   
		        if(event.keyCode==8){//判断按键为backSpace键  
					//获取按键按下时光标做指向的element  
		            var elem = event.srcElement || event.target; 
		            //判断是否需要阻止按下键盘的事件默认传递 
		            var name = elem.nodeName;  
		            if(name!='INPUT' && name!='TEXTAREA'){
		            	return _stopIt(event);  
		            }  
		            var type_e = elem.type.toUpperCase();  
		            if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){  
		            	return _stopIt(event);
		            }  
		            if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)){  
		            	return _stopIt(event);  
		            }  
		        }  
		    };
		}; 
		function _stopIt(e){  
			if(e.returnValue){  
				e.returnValue = false ;  
		    }  
		    if(e.preventDefault ){  
		        e.preventDefault();  
		    }                 
		    return false;
		}
		//改变窗口大小时触发
		var  resizeTimer = null;
		function changeWsize(){
			 if(document.readyState=="complete"){
				if(Ext.getDom("zphj").innerHTML!=""){
					Ext.getDom("zphj").innerHTML="";
					//Global.reloadData('${recruitflowForm.records }');
				}
				/*
				*针对chrome、firefox不会自适应大小(不识别百分比宽度)的问题处理
				*/
				if(!Ext.isIE){
					var clientWid = document.body.clientWidth;
					Ext.get("zphj").setWidth(clientWid*0.9*0.9);
				}
				
				Global.searchHj('${recruitflowForm.flowid }');
			} 
		}
		window.onresize = function(){
 			//if(resizeTimer)
			//	clearTimeout(resizeTimer);//取消setTimeout设置的间隔时间
			//resizeTimer = setTimeout("changeWsize()",300);//设置执行的间隔时间 
		}
		 /** 文本域高度自适应 */
	     var adapt = adapt || {
         minHeight: 150,
         adaptTextareaHeight: function(t) { // 文本域高度自适应
             var areas = [];
             
             if (t) { // 指定对某一个文本域自适应
                 areas[0] = t;
             } else { // 对所有的文本域自适应
                 areas = document.getElementsByTagName("textarea");
             }

             var _area = "jobduty";
             
             for (var i = 0; i < areas.length; i++) {
                 // 不对总结之外的文本域做修改
                 if (_area.indexOf(areas[i].id) < 0 || !areas[i].id) {continue;}
                 
                 var btw = adapt.style(areas[i]).borderTopWidth;
                 var bbw = adapt.style(areas[i]).borderBottomWidth;
                 
                 var iBtw = parseInt(btw.substring(0, btw.length - 2)) || 0;
                 var iBbw = parseInt(bbw.substring(0, bbw.length - 2)) || 0;
                 
                 areas[i].style.height = adapt.minHeight + "px";
                 
                 var adaptHeight = areas[i].scrollHeight + iBtw + iBbw;
                 adaptHeight = adaptHeight < adapt.minHeight ? adapt.minHeight : adaptHeight;
                 
                 areas[i].style.height = adaptHeight + "px";
             }
         },
         style: function(elmt) { // 获取元素计算后的样式
             if (elmt.currentStyle) {
                 return elmt.currentStyle;
             } else {
                 return window.getComputedStyle(elmt);
             }
         },
         bind: function(fn, thisObj) { // 创建闭包环境,用于参数传递
             if (!fn || typeof fn !== "function") {return null;}
             
             var args = [];
             if (arguments[2]) {
                 for (var i = 2; i < arguments.length; i++) {
                     args[args.length] = arguments[i];
                 }
             }
             
             return (function() {
                 fn.apply(thisObj, args);
             });
         }
         
     };

	     /** 给所有的文本域添加事件，让其能够根据内容自适应 */
	     function autoArea(){
	    	  var areas = Ext.getDom("jobduty");
	    	    if ("oninput" in areas) { // W3C标准浏览器
	    	    	areas.oninput = adapt.bind(adapt.adaptTextareaHeight, null, areas);
	    	    } else { // IE
	    	        function adapt4IE(t) {
	    	            t.style.height = (t.scrollHeight > adapt.minHeight ? t.scrollHeight : adapt.minHeight) + "px";
	    	        }
	    	        areas.onpropertychange = adapt.bind(adapt4IE, null, areas);
	    	        areas.onkeyup = adapt.bind(adapt4IE, null, areas);
	    	    }

	    	    adapt.adaptTextareaHeight();
	    	    /* Ext.widget('viewport',{
	    	        layout:'border',
	    	        padding:"0 5 0 5",
	    	        style:'backgroundColor:white',
	    	        items:[{
	    	                  xtype:'panel',
	    	                  id:'view_panel1',
	    	                  title:"<div id='headPanel1'></div>",
	    	                  html:"<div id='topPanel1'></div>",
	    	                  region:'north',border:false
	    	                }]
	    	    });
	    	    var view_panel = Ext.getCmp('view_panel1');
	    	    view_panel.setAutoScroll(true);
	    	    var winHeight =parent.document.body.clientHeight;
	    	    view_panel.setHeight(winHeight); */
		 }
	     function saveSkip(obj){
    		var map = new HashMap();
    		map.put("skipflag", '1');
	    	map.put("flowid", "${recruitflowForm.flowid }");
			if(!obj.checked)
				map.put("skipflag", '0');
			Rpc( {
				functionId : 'ZP0000002311'
			}, map);
	    	 
	     }
		</script>
	</body>
</html>
<%}catch(Exception e){e.printStackTrace();}%>