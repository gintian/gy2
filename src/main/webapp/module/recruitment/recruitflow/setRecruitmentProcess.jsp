<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hjsj.hrms.module.recruitment.recruitflow.actionform.RecruitflowForm"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>招聘环节</title>
		<link href="../../module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
		<script language="JavaScript" src="../../components/tableFactory/tableFactory.js"></script>
		<script language="JavaScript" src="../../js/function.js"></script>
		<script type="text/javascript" src="../../module/recruitment/recruitflow/flowLinks.js"></script>
		<script language="JavaScript" src="../../components/personPicker/PersonPicker.js"></script>
		<script language="JavaScript" src="../../module/recruitment/recruitment_resource_zh_CN.js"></script>
	</head>
	<%RecruitflowForm recruitflowForm=(RecruitflowForm)session.getAttribute("recruitflowForm"); 
	String isParent = recruitflowForm.getIsParent();
		String flag = StringUtils.isEmpty(recruitflowForm.getFlag())?"2":recruitflowForm.getFlag();
		String tabWid1 = "";
		String tabWid2 = "";
		if("1".equals(flag)){
			tabWid1="98%";
			tabWid2="99%";
		}else{
			tabWid2="98%";
			tabWid1="99%";
		}
	%>
	<body>
		<div id="panel"></div>
		<div id="lcbase_div" class="hj-wzm-hj-all" style='width:70%;margin-left:auto;margin-right:auto;'>
			<div class="hj-zm-hj-one" style="margin-top: 15px;">
				<h2>
					招聘环节
				</h2>
				<table width="100%" border="0" cellpadding="0" cellspacing="0"
					style="margin: 0">
					<tr>
						<td width="90%" style="padding-top: 5px;padding-left: 10px;">
							<table border="0" width="100%" cellpadding="0" cellspacing="0"
								style="margin-top: 0">
								<!-- <tr>
									<td width="6%" valign="middle" align="right" nowrap="nowrap" class="hj-zm-cj-one">
										<font style='white-space:nowrap;'>&nbsp;&nbsp;&nbsp;</font>流程序号：
									</td>
									<td  align="left" class="hj-zm-cj-one">
										${recruitflowForm.linkid }
									</td>
								</tr> -->
								<tr>
									<td width="6%" valign="middle" align="right" nowrap="nowrap" class="hj-zm-cj-one">
										<font style='white-space:nowrap;'>&nbsp;&nbsp;&nbsp;</font>系统名称：
									</td>
									<td align="left" class="hj-zm-cj-one">
											${recruitflowForm.sysName }
									</td>
								</tr>
								<tr>
									<td width="6%" valign="middle" align="right" nowrap="nowrap" class="hj-zm-cj-one"> 
										<font color="red" style='white-space:nowrap;'>*&nbsp;</font>显示名称：
									</td>
									<td style="line-height: 20px;" class="hj-zm-cj-one">
										<div id="name" style="float:left">
											${recruitflowForm.custom_name}
										</div>
										<div id="newdiv" style="height:22px;width:300px;float:left;"></div>
									</td>
								</tr>
							</table>
						</td>
						<%if(!Boolean.valueOf(isParent)){ %>
							<td  nowrap="nowrap" valign="top" align="center" width="10%">
								<p id="edit"  style="margin-top:10px">
									<a href="javascript:void(0);" onclick="edit('1');">编辑</a>
								</p>
								<p id="save" style="display: none;margin-top:10px" onclick="edit('2');">
									<a href="javascript:void(0);">保存</a>
								</p>
							</td>
						<%} %>
					</tr>
				</table>
				
			</div>
			<div class="hj-zm-hj-two" style="margin-top: 15px;">
				<h2  style="margin-top: 10px;" id="imgDiv">
					<img id="imgky" valign="middle" 	src="/module/recruitment/image/jianhao.png" onclick="dispaly('ky')" />
					招聘环节-状态设置
				</h2>
				<div id="zp_hj_ky" style="width: 100%;">
					<table width="<%=tabWid1 %>" cellpadding="0" align="center" cellspacing="0"
						style="margin-top: 10px;margin-bottom:20px" id="kytable">
						<tr>
							<td width="93%" style="border: none; vertical-align: top;">
								<div id="zphj" style="width:100%;height:300px;padding-left:10px"></div>
							</td>
							<%if(!Boolean.valueOf(isParent)){ %>
								<td align="center" valign="top"  nowrap="nowrap" width="7%">
									<p style="display:none"><a href="javascript:void(0)" onclick="Global.updateStatus()" id="szbc">保存</a>&nbsp;</p>
								</td>
							<% }%>
						</tr>
					</table>
				</div>
				<div class="bh-clear"></div>
				<div class="hj-zm-hj-two" style="margin-top: 15px;">
					<h2  style="margin-top: 10px">
						<img id="imgcz" valign="middle"	src="/module/recruitment/image/jianhao.png" onclick="dispaly('cz')" />
						招聘环节-操作设置
					</h2>
					<div id="zp_hj_cz" style="width: 100%">
						<table width="<%=tabWid2 %>" cellpadding="0" align="center" cellspacing="0"
							style="margin-top: 10px;;margin-bottom:20px" id="cztable">
							<tr>
								<td width="94%" style="border: none; vertical-align: top;">
									<div id="funcs" style="width:100%;height:300px;padding-left:10px"></div>
								</td>
								<%if(!Boolean.valueOf(isParent)){ %>
									<td align="center" valign="top"  nowrap="nowrap">
									<%--<p><a href="javascript:void(0)" onclick="Global.updateFuc('${recruitflowForm.linkid}')" id="czbc" style="display:none">保存</a></p>
										--%><p><a href="javascript:void(0)" onclick="Global.addFunc()" id="czadd" >新增</a></p>
										<p><a href="javascript:void(0)" onclick="Global.delFunc('${recruitflowForm.linkid }')" >删除</a></p>
									</td>
								<%} %>
							</tr>
						</table>
					</div>
				</div>
				<div class="hj-zm-hj-two hj-zm-cj-two" style="margin-top: 15px;">
					<h2  style="margin-top: 10px">
						<img id="imgczr" valign="middle"	src="/module/recruitment/image/jianhao.png" onclick="dispaly('czr')" />
						招聘环节-操作人
					</h2>
					<div id="zp_hj_czr" style="width: 100%">
						<table width="100%" border="1" cellpadding="0" cellspacing="0">
							<tr>
								<td style="width: 25px;height:50px;"></td>
								<td class="verticall">本环节只允许&nbsp;&nbsp;</td>
								<td colspan="2">
								<input type="checkbox"  style="padding-top: 0px;" id="zp1" onclick="Global.checkfun(this,'${recruitflowForm.linkid}')"/>招聘负责人
								<input type="checkbox"  style="padding-top: 0px;" id="zp2" onclick="Global.checkfun(this,'${recruitflowForm.linkid}')" />招聘成员
								<input type="checkbox"  style="padding-top: 0px;" id="zp3" onclick="Global.checkfun(this,'${recruitflowForm.linkid}')" />部门负责人
								&nbsp;&nbsp;&nbsp;&nbsp;进行业务操作
								</td>
							</tr>
							<tr>
								<td style="width: 25px;height:30px"></td>
								<td colspan="3" class="verticall" style="vertical-align: top">允许以下角色、岗位或具体人员操作本环节：</td>
								
							</tr>
                    	</table>
						<table >
							<tr>
								<td style="width: 25px;"></td>
								<td class="verticall" width="50px">角色&nbsp;&nbsp;</td>
								<td id="addTd1" align="left" >
		                        <a id="addA1" href="javascript:void(0)" style="float:left;line-height:50px;white-space: nowrap;" onclick="Global.addPerson(this,'${recruitflowForm.linkid }')">添加角色</a>
		                        </td>
							</tr>
						</table>
						<table>
							<tr>
								<td style="width: 25px;"></td>
								<td class="verticall" width="50px">岗位&nbsp;&nbsp;</td>
								<td id="addTd2" align="left">
		                        <a id="addA2" href="javascript:void(0)" style="float:left;line-height:50px;white-space: nowrap;" onclick="Global.addPerson(this,'${recruitflowForm.linkid }')">添加岗位</a>
		                        </td>
							</tr>
						</table>
						<table>
							<tr>
								<td style="width: 25px;"></td>
								<td class="verticall" >人员&nbsp;&nbsp;</td>
								<td id="addTd3" align="left" >
								<a id="addA3" href="javascript:void(0)" style="float:left;line-height:50px;white-space: nowrap;" onclick="Global.addPerson(this,'${recruitflowForm.linkid }')">添加人员</a>
								</td>
								<td align="left"></td>
							</tr>
						</table>
					</div>
				</div>
			</div>
			</div>
	</body>
	<script type="text/javascript">
		Ext.onReady(function(){

	     	var Panel = new Ext.Panel({      
	     		xtype:'panel',
				id:'view_panel',
				title:'<div style="width:100px;display:inline;float:left">招聘环节设置  </div><div style="float: right;margin-right: 5%;white-space:nowrap;display:inline"><a href="/recruitment/recruitmentProcess/searchProcess.do?b_query=link">返回</a></div>',
   			  	html:"<div id='topPanel' ></div>",
   			  	region:'center',
   			  	border:false
			});

	     	new Ext.Viewport({
	        	layout:'border',
	        	title:'招聘环节设置',
	            padding:"0 5 0 5",
	            renderTo: Ext.get('panel'),
	         	style:'backgroundColor:white',
	            items:[Panel]
	             
	         });
	         
	     	document.getElementById('topPanel').appendChild(document.getElementById('lcbase_div'));
	     	document.getElementById('lcbase_div').style.display="block";

	     	//if(navigator.userAgent.indexOf("MSIE")==-1){//非ie浏览器
	     		changeWsize();
			 //}
	     	var view_panel = Ext.getCmp('view_panel');
		    view_panel.setAutoScroll(true);
		    var winHeight =parent.document.body.clientHeight;
		    view_panel.setHeight(winHeight);
		    generateFlowName();
		    showCzr('${recruitflowForm.linkid}');
	    });
		/*
	    	给gridPanel绑定edit事件，实现失去焦点后保存更改的record
	    */
	    function afterEdit(){
    		Ext.getCmp("rzRegister_tablePanel").on('edit',function(editor,e){
    			Global.updateStatus();
    		});
    	}
	    function afterEdit2(){
    		Ext.getCmp("rzRegister2_tablePanel").on('edit',function(editor,e){
    			Global.updateFuc('${recruitflowForm.linkid}');
    		});
    	}
		//生成流程名称输入框
	    function generateFlowName(){
	    	Ext.create('Ext.form.Panel', {
	    	    width: 305,
	    	    height:30,
	    	    renderTo: 'newdiv',
	    	    border:false,
	    	    //style:{borderRightWidth:"1px"},
	    	    items: [{
	    	        xtype: 'textfield',
	    	        name: 'name',
	    	        id:'newname',
	    	        border:'1 1 1 1',
	    	        width:300,
	    	        maxLength:25,
	    	        maxLengthText:'流程名称不能超过25个字符长度',
	    	        height:22,
	    	        hidden:true,
	    	        value:'${ recruitflowForm.custom_name}',
	    	        allowBlank: false  // 表单项非空
	    	    }]
	    	});
		}
	    function validLen(obj){
	    	if(Ext.String.trim(obj.value)==""){
				Ext.Msg.alert("提示信息","请输入流程环节显示名称");
				Ext.get(obj).focus();
				return false;
			}
			if(IsOverStrLength(obj.value,100)){
				Ext.Msg.alert("提示信息","流程环节名称长度不能大于100");
				Ext.get(obj).focus();
				return false;
			}
			return true;
		}
		function edit(flag){
			if("1" == flag){
				document.getElementById('name').style.display="none";
				Ext.getCmp('newname').show();
				document.getElementById('edit').style.display="none";
				document.getElementById('save').style.display="block";
			} else{
				if(!Ext.getCmp('newname').validate()){//流程名称验证不通过
					Ext.getCmp('newname').focus();
					return; 
				}
				
				if(trim(Ext.getCmp('newname').getValue()).length<=0){
					Ext.showAlert(LINK_NAME_NOT_STANDARD);
					Ext.getCmp('newname').focus();
					return;
				} 
				
				var value = Ext.getCmp('newname').getValue();
				
				var hashvo=new ParameterSet();
			    hashvo.setValue("custom_name",value);
			    hashvo.setValue("linkid","${recruitflowForm.linkid }");
			    var request=new Request({method:'post',asynchronous:true,onSuccess:saveSuccess,functionId:'ZP0000002322'},hashvo);
			}
		}
		function saveSuccess(param){
			var value = param.getValue("value");
			document.getElementById('name').style.display="block";
			document.getElementById('name').innerHTML=value;
			Ext.getCmp('newname').hide();
			document.getElementById('edit').style.display="block";
			document.getElementById('save').style.display="none";
		}
		function dispaly(id){
			var div = document.getElementById('zp_hj_'+id);
			if("none" == div.style.display){
				if(id=="ky"&&Ext.getDom("zphj").innerHTML==""){
					Global.searchHj('${recruitflowForm.node_id }','${recruitflowForm.linkid }','${recruitflowForm.isParent }');
				}
				if(id=="cz"&&Ext.getDom("funcs").innerHTML==""){
					Global.searchFunc('${recruitflowForm.linkid }','${recruitflowForm.isParent }');
				}
				div.style.display="block";
			} else {
				div.style.display="none";
			}
			var img = document.getElementById('img'+id);
			if(img.src.indexOf("jianhao.png")!=-1)
		        img.src = img.src.replace("jianhao.png","jiahao.png");
		    else if(img.src.indexOf("jiahao.png")!=-1)
		        img.src = img.src.replace("jiahao.png","jianhao.png");
		}
		//点击是否启用
		function change(obj,pre){
			var tableid ="";
			if(pre==1){
				tableid = "rzRegister_dataStore";
			}else{
				tableid = "rzRegister2_dataStore";
			}
			var valid = obj.parentNode.innerHTML;
			var id = obj.nextSibling.value;
			var tem = valid.lastIndexOf("<");
			var numb = obj.id.indexOf("f");
			if(obj.checked){
				obj.value=1;
				tem = "<input type=\"checkbox\" onclick=\"change(this,"+pre+")\" id=\""+obj.id+"\" value=\"1\" checked=\"checked\"/>"+valid.substring(tem);
			}else{
				obj.value=0;
				var falg = "";
				if(pre==1){
					var map = new HashMap();
					map.put("uncheckedid", obj.nextSibling.value);
					Rpc({functionId : 'ZP0000002325',async:false,
						success :function(mes){
							var value = mes.responseText;
							var map = Ext.decode(value);
							falg = map.falg;
						}
					}, map);
					if(falg){
						Ext.getDom(obj.id).checked='checked';
						Ext.showAlert("当前状态下存在应聘人员，不允许关闭！");
						return;
					}
				}
				tem = "<input type=\"checkbox\" onclick=\"change(this,"+pre+")\" id=\""+obj.id+"\" value=\"0\"/>"+valid.substring(tem);
			}
			Ext.data.StoreManager.lookup(tableid).each(function(record,index){
				if(id!=""&&record.get("valid").indexOf(id)!=-1&&record.get("valid").indexOf(obj.id)!=-1){//将obj对应的record修改
					record.set("valid",tem);
					if(pre==1)
						Global.updateStatus();
					else
						Global.updateFuc('${recruitflowForm.linkid}');
				}
				if(id==""&&parseInt(obj.id.substring(0,numb))==index+1){//针对新增的记录
					record.set("valid",tem);
					Global.updateFuc('${recruitflowForm.linkid}');
				}
			});
		}
		//点击是否允许在线修改简历
		function change1(obj,pre){
			var tableid ="";
			if(pre==1){
				tableid = "rzRegister_dataStore";
			}
			var resume_modify = obj.parentNode.innerHTML;
			var id = obj.nextSibling.value;
			var tem = resume_modify.lastIndexOf("<");
			var numb = obj.id.indexOf("f");
			if(obj.checked){
				obj.value=1;
				tem = "<input type=\"checkbox\" onclick=\"change1(this,"+pre+")\" id=\""+obj.id+"\" value=\"1\" checked=\"checked\"/>"+resume_modify.substring(tem);
			}else{
				obj.value=0;
				tem = "<input type=\"checkbox\" onclick=\"change1(this,"+pre+")\" id=\""+obj.id+"\" value=\"0\"/>"+resume_modify.substring(tem);
			}
			Ext.data.StoreManager.lookup(tableid).each(function(record,index){
				if(id!=""&&record.get("resume_modify").indexOf(id)!=-1&&record.get("resume_modify").indexOf(obj.id)!=-1){//将obj对应的record修改
					record.set("resume_modify",tem);
					if(pre==1)
						Global.updateStatus();
					
				}
				if(id==""&&parseInt(obj.id.substring(0,numb))==index+1){//针对新增的记录
					record.set("resume_modify",tem);
					Global.updateFuc('${recruitflowForm.linkid}');
				}
			});
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
			//var flag = '${recruitflowForm.flag }';
			if(Ext.getDom("funcs").innerHTML!=""){
				Ext.getDom("funcs").innerHTML="";
			}
			if(Ext.getDom("zphj").innerHTML!=""){
				Ext.getDom("zphj").innerHTML="";
			}
			/*
			*针对chrome、firefox不会自适应大小(不识别百分比宽度)的问题处理
			*/
			var clientWid = document.body.clientWidth;
			var screenHeight =  window.screen.height;
	     	var screenWidth = window.screen.width;
			if(screenHeight * screenWidth <= 1280*768)
				Ext.get("zphj").setWidth(clientWid*0.98*0.93*0.75);
			else
				Ext.get("zphj").setWidth(clientWid*0.98*0.93*0.7);
			
			if(screenHeight * screenWidth <= 1280*768)
				Ext.get("funcs").setWidth(clientWid*0.98*0.93*0.75);
			else
				Ext.get("funcs").setWidth(clientWid*0.98*0.93*0.7);
			
			var nodeid = '${recruitflowForm.node_id }';
			if(nodeid!="09")//非体检环节
		    	Global.searchHj(nodeid,'${recruitflowForm.linkid }','${recruitflowForm.isParent }');
		    Global.searchFunc('${recruitflowForm.linkid }','${recruitflowForm.isParent }');
		    if(nodeid=="09"){//不存在操作状态   体检环节
			    Ext.get("zphj").setHeight("0px");
			    Ext.get("zp_hj_ky").setHeight("0px");
			    Ext.get("zp_hj_ky").hide();
			    Ext.get("imgDiv").hide();
			}
		    	/**document.getElementById('zp_hj_ky').style.display="none";
		    	var img =document.getElementById('imgky');
	    		img.src = img.src.replace("jianhao.png","jiahao.png");*/
			//} 
		}
		window.onresize = function(){
			/* if(resizeTimer)
				clearTimeout(resizeTimer);//取消setTimeout设置的间隔时间
			resizeTimer = setTimeout("changeWsize()",300);//设置执行的间隔时间 */
		}
		
</script>
</html>





