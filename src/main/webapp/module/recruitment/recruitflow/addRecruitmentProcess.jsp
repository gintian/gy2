<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hjsj.hrms.module.recruitment.recruitflow.actionform.RecruitflowForm"%>
		<script language="JavaScript" src="/components/codeSelector/codeSelector.js"></script>
		<script language="JavaScript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
		<script language="JavaScript" src="/js/function.js"></script>
		<script language='JavaScript' src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
		<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
<%RecruitflowForm recruitflowForm = (RecruitflowForm)session.getAttribute("recruitflowForm"); %>
	<body>
		<div id="panel" style="width:100%"></div>
		<div id="lcbase_div" class="hj-wzm-xq-all" style="display: none;width:100%">
			<div class="hj-cj-all">
				<div class="hj-zm-lc-one">
					<h2>
						流程基本信息
					</h2>
					<div class="bh-space"></div>
						<table width="98%" border="0" cellpadding="0" style="margin-left: 1%;" cellspacing="0">
							<%--<tr>
								<td width="60"  nowrap="nowrap" class="hj-zm-cj-one">
									<font style='white-space:nowrap;'>&nbsp;&nbsp;&nbsp;</font>流程编号：
								</td>
								<td>
									${recruitflowForm.xjflowid}
									<input type="hidden" id="node_id" id="node_id" value=""/>
									<input type="hidden" id="custom_name" id="custom_name" value=""/>
								</td>
							</tr>--%>
							<tr>
								<td nowrap="nowrap" class="hj-zm-cj-one">
									<font color="red" style='white-space:nowrap;'>*&nbsp;</font>流程名称：
								</td>
								<td id="newdiv"></td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="hj-zm-cj-one"><font color="red" style='white-space:nowrap;'>*&nbsp;</font>所属机构：</td>
								<td>
									<input type="hidden" name="codeinput_value" id="ssjg"/><input type="text" name="codeinput_view" style="height:22px;width:300px" />
									<img style="margin-left:-19px;" src="/module/recruitment/image/xiala2.png"  class="img-middle" plugin="codeselector" codesetid="UN" ctrltype='3' nmodule='7' inputname="codeinput_view"/>
								</td>
							</tr>
							<tr>
								<td width="6%" nowrap="nowrap" class="hj-zm-cj-one">
									<font style='white-space:nowrap;'>&nbsp;&nbsp;&nbsp;</font>流程说明：
								</td>
								<td width="94%" style="padding-top: 5px;">
									<textarea id="description" style="overflow:hidden;font-style: normal;font-size:12px;font-family: 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;width:80%;height:150px" class='hj-zm-cj-gzzz'></textarea>
								</td>
							</tr>
						</table>

						<div class="bh-space"></div>
						<h2>
							招聘流程设置
							<span><a href="#"></a>
							</span>
						</h2>

						<table cellpadding="0" cellspacing="0" border="0" style="margin-left: 1%;" width="98%">
							<tr>
								<td width="48%">
									<div class="hj-wzm-xq-tianjia-left" style="width: 99%;padding-top:10px">
										<h3>
											系统内置招聘环节
										</h3>
										<div class="hj-xq-left-bottom"
											style="height: 260px; overflow: auto;">
											<table width="100%" border="0" cellpadding="0"
												cellspacing="0">
												<tr onmousemove="onmouse(1,1);" onmouseout="onmouse(1,0);">
													<td width="17%"  nowrap="nowrap">
														人力筛选
													</td>
													<td width="58%" class="hj-zm-cj-kcj"  nowrap="nowrap">
														（可创建多个人力筛选环节）
													</td>
													<td width="25%"  nowrap="nowrap">
														<a id="1" href="javascript:addZphj('1');" style="display: none;">添加</a>
													</td>
												</tr>
												<tr onmousemove="onmouse(2,1);" onmouseout="onmouse(2,0);">
													<td  nowrap="nowrap">
														部门筛选
													</td>
													<td class="hj-zm-cj-kcj"  nowrap="nowrap">
														（可创建多个部门筛选环节）
													</td>
													<td  nowrap="nowrap">
														<a id="2" href="javascript:addZphj('2');" style="display: none;">添加</a>
													</td>
												</tr>
												<tr onmousemove="onmouse(3,1);" onmouseout="onmouse(3,0);">
													<td  nowrap="nowrap">
														笔试
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="3" href="javascript:addZphj('3');" style="display: none;">添加</a>
													</td>
												</tr>
												<%--
												 <tr onmousemove="onmouse(4,1);" onmouseout="onmouse(4,0);">
													<td  nowrap="nowrap">
														人才测评
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="4" href="javascript:addZphj('4');" style="display: none;">添加</a>
													</td>
												</tr>
												--%><tr onmousemove="onmouse(5,1);" onmouseout="onmouse(5,0);">
													<td  nowrap="nowrap">
														面试
													</td>
													<td class="hj-zm-cj-kcj"  nowrap="nowrap">
														（可创建多个面试环节）
													</td>
													<td  nowrap="nowrap">
														<a id="5" href="javascript:addZphj('5');" style="display: none;">添加</a>
													</td>
												</tr>
												 <tr onmousemove="onmouse(6,1);" onmouseout="onmouse(6,0);">
													<td  nowrap="nowrap">
														背景调查
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="6" href="javascript:addZphj('6');" style="display: none;">添加</a>
													</td>
												</tr> 
												<tr onmousemove="onmouse(7,1);" onmouseout="onmouse(7,0);">
													<td  nowrap="nowrap">
														录用审批
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="7" href="javascript:addZphj('7');" style="display: none;">添加</a>
													</td>
												</tr>
												<tr onmousemove="onmouse(8,1);" onmouseout="onmouse(8,0);">
													<td  nowrap="nowrap">
														Offer
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="8" href="javascript:addZphj('8');" style="display: none;">添加</a>
													</td>
												</tr>
												<%--<tr onmousemove="onmouse(9,1);" onmouseout="onmouse(9,0);">
													<td  nowrap="nowrap">
														体检
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="9" href="javascript:addZphj('9');" style="display: none;">添加</a>
													</td>
												</tr>
												--%><tr onmousemove="onmouse(10,1);" onmouseout="onmouse(10,0);">
													<td  nowrap="nowrap">
														入职
													</td>
													<td class="hj-zm-cj-kcj">
														&nbsp;
													</td>
													<td  nowrap="nowrap">
														<a id="10" href="javascript:addZphj('10');" style="display: none;">添加</a>
													</td>
												</tr>
											</table>

										</div>
									</div>
								</td>
								<td width="4%" align="center"></td>
								<td width="48%">
									<div class="hj-wzm-xq-tianjia-right" style="width: 99%;">
										<h3>
											已选招聘环节
										</h3>
										<div class="hj-xq-right-bottom"
											style="height: 260px; overflow: auto;">
											<table width="100%" border="0" cellpadding="0"
												cellspacing="0" id="hjs">
											</table>
										</div>
									</div>
								</td>
							</tr>
						</table>
						<div class="bh-clear"></div>
					</div>

				</div>
			</div>
	</body>
	<script type="text/javascript">
	var msg = true;
		Ext.onReady(function(){
			Ext.QuickTips.init();
			Ext.form.Field.prototype.msgTarget = 'side';
	     	var Panel = new Ext.Panel({      
	     		xtype:'panel',
				id:'view_panel',
				title:'创建流程<div style="float: right;margin-right: 5%;" id="bcId" align="center"><a href="javascript:void(0);" style="margin-right: 2%" onclick="bc();" id="savelink">保存</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="/recruitment/recruitmentProcess/searchProcess.do?b_query=link&flowid="'+'"">返回</a></div>',
   			  	html:"<div id='topPanel'></div>",
   			  	region:'center',
   			  	border:false
			});

	     	new Ext.Viewport({
	        	layout:'border',
	        	title:'创建流程',
	            padding:"0 5 0 5",
	            renderTo: Ext.get('panel'),
	         	style:'backgroundColor:white',
	            items:[Panel]
	             
	         });
	         
	     	document.getElementById('topPanel').appendChild(document.getElementById('lcbase_div'));
	     	document.getElementById('lcbase_div').style.display="block";
	     	
	     	var view_panel = Ext.getCmp('view_panel');
		    view_panel.setAutoScroll(true);
		    var winHeight =parent.document.body.clientHeight;
		    view_panel.setHeight(winHeight);
		    //autoArea();
		    generateFlowName();
	    });
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
	    	        name: 'flowName',
	    	        id:'flowName',
	    	        border:'1 1 1 1',
	    	        width:300,
	    	        maxLength:25,
	    	        maxLengthText:'流程名称不能超过25个字符长度',
	    	        regex:new RegExp("^[^\'\"<>]*$"),
	    	        regexText:'不允许输入<>\'\"',
	    	        height:22,
	    	        hidden:false,
	    	        allowBlank: false , // 表单项非空
	    	        listeners:{
						blur:{
							fn:function(cop){
								var flowname = Ext.String.trim(cop.getValue());
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
				Ext.Msg.alert("提示信息",msg);
				Ext.getCmp("flowName").setValue("");
				Ext.getCmp("flowName").focus();
			}
		}
		var x;
		function onmouseR(aid1,aid2,flag){
			if(!msg)
				return;
			onmouse(aid1,flag);
			onmouse(aid2,flag);
			if(Ext.get(aid2)!=null)
				x = Ext.get(aid2).getX();
		}	
		function onmouse(id,flag){
			if(!Ext.isEmpty(Ext.getDom(""+id+""))){
				if(Ext.String.trim(Ext.getDom(""+id+"").innerHTML)=="添加"){
					var xtmc = Ext.String.trim(Ext.get(""+id+"").parent().parent().first().getHtml());
					if(xtmc!="人力筛选"&&xtmc!="部门筛选"&&xtmc!="面试"){
						var rId = "r"+id;
						if(!Ext.isEmpty(Ext.getDom(""+rId+""))){//判断已选流程环节中是否已有该环节
							Ext.get(""+id+"").hide();
							return false;
						}
					}
				}
				if("1"==flag)
					Ext.get(""+id+"").show();
				else 
					Ext.get(""+id+"").hide();
			}
		}	
		var oldname;
		function rename(id){
			if(!msg)
				return;
			document.getElementById(id).nextSibling.style.display="none";		
			document.getElementById(id).nextSibling.nextSibling.style.display="none";			
			msg = false;
			var value = document.getElementById(id).innerHTML;
			oldname=value;
			var input = "<input type='text' id='text_"+id+"' onkeypress=\"EnterPress(event,this,'"+id+"')\"  onkeydown=\"EnterPress('',this,'"+id+"')\" style='' value='"+value+"'/> (按回车键保存)";
			document.getElementById(id).innerHTML=input;
			document.getElementById('text_'+id).focus();
		}
		//校验用户自定义环节名称
		function valid(obj,id){
			var res = true;
			if(Ext.String.trim(obj.value)==""){
				Ext.Msg.alert("提示信息","流程环节名称不能为空！");
				res= false;
			}
			if(IsOverStrLength(obj.value,50)){
				Ext.Msg.alert("提示信息","流程环节名称长度不能大于25！");
				res= false;
			}
			obj.value=oldname;
			document.getElementById(id).innerHTML=oldname;
			document.getElementById(id).nextSibling.style.display="";		
			document.getElementById(id).nextSibling.nextSibling.style.display="";
			msg = true;
			return res;
		}
		function EnterPress(e,obj,id){
			if(e==null || e=='')
				e = window.event; 
			if(e.keyCode == 13){ 
				var value = Ext.String.trim(obj.value);
				if(valid(obj,id)){
					Ext.get(id).setHtml(value);
					document.getElementById(id).nextSibling.childNodes[0].style.display="none";		
					document.getElementById(id).nextSibling.nextSibling.childNodes[0].style.display="none";
					Ext.getDom(id).nextSibling.removeAttribute("style");
					Ext.getDom(id).nextSibling.nextSibling.removeAttribute("style");
					msg = true;
				}
			}
		}
		var creament=0;
		//添加流程环节
		function addZphj(id){
			var nodeids="";
			var hjs = Ext.getDom("hjs").childNodes;
			var jls = hjs.length;
			Ext.Array.each(hjs,function(item){
				if(item!=""&&item.firstChild!=null&&item.firstChild.innerHTML!=""){//判断是否已删除(前两个条件排除符合W3C的浏览器取出的空文本节点)
					if(Ext.Array.indexOf(hjs,item,0)<jls-1){//Ext.Array.indexOf(hjs,item,0)获取当前元素在数组中的索引
						nodeids+=Ext.String.trim(item.firstChild.lastChild.lastChild.value)+",";
					}else{
						nodeids+=Ext.String.trim(item.firstChild.lastChild.lastChild.value);
					}
				}
			});
			var hjmc = Ext.String.trim(Ext.get(id).parent().parent().first().getHtml());
			var hjid = "";
			if(id<10)
				hjid = Ext.String.trim("0"+id);
			else
				hjid = Ext.String.trim(id)
			var r1 = "r_1_"+id;
			var r2 = "r_2_"+id;
			var r3 = "r"+id;
			//多次添加同一环节
			if(nodeids.indexOf(hjid)!=-1){
				r1=r1+"_"+creament;
				r2=r2+"_"+creament;
				r3=r3+"_"+creament;
				creament = creament+1;
			}
			var nodeIdVal = id < 10 ? "0"+id : id;
			Ext.get("hjs").createChild('<tr onmousemove="onmouseR(\''+r1+'\',\''+r2+'\','+'1);" onmouseout="onmouseR(\''+r1+'\',\''+r2+'\','+'0);"><td height=10 id="'+r3+'" nowrap="nowrap" width="33%">'+hjmc+
					'</td><td height=10 nowrap="nowrap"><a id="'+r1+'" style="display: none;" href="###" onclick="rename(\''+r3+'\')">重命名</a>'+
					'</td><td height=10 nowrap="nowrap"><a id="'+r2+'" style="display: none;" href="###" onclick="del(\''+r2+'\')">删除</a><input type="hidden" value="'+nodeIdVal+'"></td></tr>');
			if(hjmc!="人力筛选"&&hjmc!="部门筛选"&&hjmc!="面试"){//主要针对火狐下添加完成后不能立即隐藏添加
				Ext.get(""+id+"").hide();
			}
		}
		//删除
		function del(id){
			try {
			  Ext.DomHelper.overwrite(Ext.getDom(id).parentNode.parentNode,"");
			} catch(e) {
				//ie报错，原因不明，但不影响功能，无奈之下，屏蔽掉异常信息
			}
		}
		//校验长度
		function validLen(id,text,length){
			var val = Ext.getDom(id).value;
			if(Ext.String.trim(val)==""){
				Ext.Msg.alert("提示信息","请输入"+text);
				Ext.get(id).focus();
				return false;
			}
			if(IsOverStrLength(val,length)){
				Ext.Msg.alert("提示信息",text+"长度不能大于"+length);
				Ext.get(id).focus();
				return false;
			}
			return true;
		}
		//保存新建流程
		function bc(){
			if(!Ext.getCmp('flowName').validate()){//流程名称验证不通过
				if(trim(Ext.getCmp('flowName').getValue()).length<=0)
					Ext.showAlert(PROCESS_NAME_NOT_STANDARD);
			
				Ext.getCmp('flowName').focus();
				return;
			}
			
			if(trim(Ext.getCmp('flowName').getValue()).length<=0){
				Ext.showAlert(PROCESS_NAME_NOT_STANDARD);
				Ext.getCmp('flowName').focus();
				return;
			}
			
			if(Ext.String.trim(Ext.get("ssjg").getValue())==""){
				Ext.showAlert("请选择所属机构！");
				Ext.get("ssjg").focus();
				return false;
			}
			//解决点击重命名后直接保存导致报错问题
			var alreadySelcs = Ext.query("#hjs td input");
			Ext.each(alreadySelcs,function(tem){
				if(!Ext.isEmpty(tem.id))
					Ext.get(tem.id.split("text_")[1]).setHtml(tem.value);
			});
			
			Ext.getDom("savelink").disabled="disabled";
			var nodeids="";
			var nodenames="";
			var hjs = Ext.getDom("hjs").childNodes;
			var jls = hjs.length;
			Ext.Array.each(hjs,function(item){
				if(item!=null&&item.firstChild!=null&&item.firstChild.innerHTML!=""){//判断是否已删除
					if(Ext.Array.indexOf(hjs,item,0)<jls-1){//Ext.Array.indexOf(hjs,item,0)获取当前元素在数组中的索引
						nodenames+=Ext.String.trim(item.firstChild.firstChild.innerHTML)+",";
						nodeids+=Ext.String.trim(item.firstChild.lastChild.lastChild.value)+",";
					}else{
						nodenames+=Ext.String.trim(item.firstChild.firstChild.innerHTML);
						nodeids+=Ext.String.trim(item.firstChild.lastChild.lastChild.value);
					}
				}
			});
			var message = "";
			var sx = nodeids.indexOf("01")==-1&&nodeids.indexOf("02")==-1;
			var rz = nodeids.indexOf("10")!=-1;
			if(sx&&rz){//已选招聘环节中没有简历筛选环节
				nodenames="人力筛选,"+nodenames;
				nodeids = "01,"+nodeids;
				message="招聘流程缺少简历筛选环节，</br>系统将自动添加到招聘流程中！";
			}else if(!sx&&!rz){
				nodenames=nodenames+",入职";
				nodeids = nodeids+",10";
				message="招聘流程缺少入职环节，</br>系统将自动添加到招聘流程中！";
			}else if(sx&&!rz){
				nodenames="人力筛选,"+nodenames+",入职";
				nodeids = "01,"+nodeids+",10";
				message="招聘流程缺少简历筛选和入职环节，</br>系统将自动添加到招聘流程中！";
			}
			var res ="";
			if(message!=""){
				res=message;
			}
			if(res!=""){
				Ext.Msg.confirm("提示信息",res,function(res){
					if(res=="yes"){
						commonSaves(nodeids,nodenames);
					}else{
						Ext.getDom("savelink").removeAttribute("disabled");
						return;
					}
				});
			}else{
				commonSaves(nodeids,nodenames);
			}
		}
		var num=0;
		function commonSaves(nodeids,nodenames){
			if(num!=0){
				return;
			}
			num+=1;
			var map = new HashMap();
		    map.put("description",Ext.getDom("description").value);
		    map.put("flowid","${recruitflowForm.xjflowid }");
		    map.put("flowName",Ext.getCmp("flowName").getValue());
		    map.put("node_id",nodeids);
		    map.put("codeinput_value",Ext.getDom("ssjg").value);
		    map.put("custom_name",nodenames);
		    Rpc( {
	    		functionId : 'ZP0000002314',
	    		success : saveSuccess
	    	}, map);
		}
		function saveSuccess(response){
			var value = response.responseText;
			var map = Ext.decode(value);
			var msg = map.message;
			if(msg=="操作执行成功!"){
				//Ext.Msg.alert("提示信息","保存成功",function(){
					location.href="/recruitment/recruitmentProcess/searchProcess.do?b_query=link&flowid=${recruitflowForm.xjflowid}";
				//});
			}else{
				Ext.Msg.alert("提示信息","保存失败！");
			}
		}

		//屏蔽backspace
		window.onload=function(){
			Ext.getDoc().on('keydown',function(e){  
			    if(e.getKey() == 8 && e.getTarget().type =='text' && !e.getTarget().readOnly){  
			           
			    }else if(e.getKey() == 8 && e.getTarget().type =='textarea' && !e.getTarget().readOnly){   
			       
			    }else if(e.getKey() == 8){  
			        e.preventDefault();  
			    }  
			});
			
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

            var _area = "description";
            
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
	    	 var areas = document.getElementsByTagName("textarea");
	    	 var _area = document.getElementById("description");
	    	 for (var i = 0; i < areas.length; i++) {
	             if ("oninput" in areas[i]) { // W3C标准浏览器
	                 areas[i].oninput = adapt.bind(adapt.adaptTextareaHeight, null, areas[i]);
	             } else { // IE
	                 function adapt4IE(t) {
	                     t.style.height = (t.scrollHeight > adapt.minHeight ? t.scrollHeight : adapt.minHeight) + "px";
	                 }
	                 areas[i].onpropertychange = adapt.bind(adapt4IE, null, areas[i]);
	                 areas[i].onkeyup = adapt.bind(adapt4IE, null, areas[i]);
	             }
	         }
	    	    
	    	    adapt.adaptTextareaHeight();
	    	    Ext.widget('viewport',{
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
	    	    view_panel.setHeight(winHeight);
		 }
</script>
