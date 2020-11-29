//人事异动查看申报信息与待办事项
/*Ext.Loader.loadScript({url:'../../module/utils/js/template.js'});
Ext.Loader.loadScript({url:'../../components/tableFactory/tableFactory.js'});*/
Ext.define("EHR.homewidget.TemplateTable",{
	extend:'Ext.panel.Panel',
	xtype:'templateTable',
	bodyPadding:'0 10 10 0',
	margin:'10 10 0 10',
	collapsible:true,
	res:undefined,
	titleCollapse:true,
	hideCollapseTool:true,
	animCollapse: true,
	minHeight:200,
	style:'background:white',
	bodyStyle:"border-top:0 !important",
	title:'<div style="table-layout:fixed;;width:100%;height:100%;border-bottom:1px solid #dedede"><table cellspacing="0" cellpadding="0"  align="center"  style="width:100%;height:100%">'+
				'<tr height="30px">'+
				  '<td width="5"></td>'+
				  '<td width="100" id="my_task" onclick="template_table.change(this,\'my_declare\')" style="">'+
				  '<table cellspacing="0" cellpadding="0" style="height:30px;width:100px">'+
					  '<tr>'+
					    '<td style="max-width:20px"><div style="float:left;width:16px;height:16px;margin:1px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/icon_business.png) no-repeat  -16px -96px;"></div></td>'+
					    '<td style="max-width:60px" id="my_task_index"><div style="float:left"><span style="font-size:14px">待办任务</span></div></td>'+
					    '<td style="width:20px"><div id="total_num_id" style="float:left;width:20px;margin-top:-4px;height:20px;background:url(/images/new_module/disagree_unchecked.png);text-align:center;line-height:20px;color:white;"><span id="total_num" style="font-size:14px"></span></div></td>'+
					  '</tr>'+
				  '</table>'+
				  '</td>'+
				  '<td width="2%"></td>'+
				  '<td width="85" id="my_declare" onclick="template_table.change(this,\'my_task\')" style="">'+
					  '<table cellspacing="0" cellpadding="0" style="width:100%;height:30px">'+
					  	'<tr>'+
					  		'<td style="max-width:20px" >'+
					  			'<div style="float:left;width:16px;height:16px;margin:1px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/icon_business.png) no-repeat 0px -96px;"></div>'+
					  		'</td>'+
					  		'<td id="my_declare_index">'+
					  			'<span style="font-size:14px">我的申请</span>'+
					  		'</td>'+
					  	'</tr>'+
					  '</table>'+
				 '</td>'+
				  '<td>'+
				  '<td width="78%" style="";></td>'+
				  '<td width="4%" id="more_Img" onclick="" style=""><img id="more_Img_ID" src="/images/hcm/themes/default/icon/icon55.png"></img></td>'+
				'</tr></table></div>',
	constructor:function(){
		this.callParent();
		template_table=this;
	},
	listeners:{
		beforecollapse:function(p, direction, animate, eOpts){
			var index_X=0;
			(document.onclick=function getIndex(ev){
				var oEvent=ev||event;
				index_X=oEvent.clientX
			})();
			var left_index=Ext.get("my_declare").getX()+Ext.get("my_declare").getWidth();
			var right_index=Ext.get("more_Img").getX();
			if(left_index<index_X&&index_X<right_index)
				return true;
			else
				return false;
		},
		afterrender:function(panel,opt){
			var declare=document.getElementById('my_declare');
			var task=document.getElementById('my_task');
			var map=new HashMap();
			
			Rpc({functionId:"ZJ100001000",success:function(res){
				this.res=res;
				res=Ext.decode(res.responseText);
				if(res.total&&parseInt(res.total)>0){
					document.getElementById("total_num").innerText=res.total;
					document.getElementById("total_num").onclick=function(){
						window.location.href="/general/template/matterList.do?b_query=link";
					}
				}else{
					Ext.get("total_num_id").setStyle({
						display:"none"
					});
				}
				//有待办 先显示待办 没有待办显示申报  服务大厅进入返回时 显示业务申请 
				if(window.top.myTask==='false'||!res.total||res.total==""){//待办为空时 显示申报界面
					this.change(declare,'my_task');
				}else{
					this.change(task,'my_declare');
				}
			},scope:this},map);
			
		}
	},
	change:function(obj,id){
		var me=this;
		if(id=='my_task'){
			Ext.get(id).setStyle({
				minWidth:"100px",
				borderBottom:0,
				cursor:"pointer"
				});
			Ext.get('my_task_index').setStyle({
				borderBottom:0
			})
		}else{
			Ext.get(id).setStyle({
				minWidth:"85px",
				borderBottom:0,
				cursor:"pointer"
				});
			Ext.get('my_declare_index').setStyle({
				borderBottom:0,
			});
		}
		if(obj.id=="my_task"){
			Ext.get(obj.id).setStyle({
				minWidth:"100px",
				cursor:"pointer"
			});
			Ext.get('my_task_index').setStyle({
				borderBottom:"1px solid #00aaee"
			})
		}else{
			Ext.get(obj.id).setStyle({
				minWidth:"85px",
				borderBottom:0,
				cursor:"pointer"
			});
			Ext.get('my_declare_index').setStyle({
				borderBottom:"1px solid #00AAEE",
			});
		}
		
		
		this.removeAll();
		if(obj.id==="my_declare"){//加载查看申报信息
			this.createdeclare(this.res);
		}else if(obj.id==="my_task"){//加载查看待办事项
			window.top.myTask="";
			this.renderTask(this.res);
		}
	},
	renderTask:function(res){
		res=Ext.decode(res.responseText);
		if(res.total>5){
			document.getElementById("more_Img").onclick=function(){
				window.location.href="/general/template/matterList.do?b_query=link";
			};
			Ext.get("more_Img").setStyle({cursor:"pointer"});
			Ext.get("more_Img_ID").setStyle({display:"block"});
		}else{
			document.getElementById("more_Img").onclick=function(){
			};
			Ext.get("more_Img").setStyle({});
			Ext.get("more_Img_ID").setStyle({display:"none"});
		}
		if(res.total&&parseInt(res.total)>0){
			document.getElementById("total_num").innerText=res.total;
		}
		var html='<table width="99%" border="0" style="font-family:微软雅黑;" cellspacing="0" align="center" cellpadding="0">'+
				'<tr height="0px"><td>'+
				'<table width="99%" height="90%">'+
				'<tr>'+
				'<td valign="top">'+
				'<table width="99%" border="0" cellspacing="0" align="center" cellpadding="1">'+
				(res.my_task_html?res.my_task_html:"")+'</table></td></tr></table></td></tr></table>';
		//window.top.myTask="task";
		this.containerPanel(html);
	},
	createdeclare:function(res){//创建查看我的申报信息内容
		res=Ext.decode(res.responseText);
		var data=res.data_list;
		template_table.data=data;
		//window.top.myTask="";
		if(res.data_list_total>4){
			document.getElementById("more_Img").onclick=function(){
				template_table.searchDeclare('','1');
			};
			Ext.get("more_Img").setStyle({cursor:"pointer"});
			Ext.get("more_Img_ID").setStyle({display:"block"});
		}else{
			document.getElementById("more_Img").onclick=function(){
			};
			Ext.get("more_Img").setStyle({});
			Ext.get("more_Img_ID").setStyle({display:"none"});
		}
		var rows="";
		if(data){
			for(var i=0;i<data.length;i++){
				if(i>=4){
					continue;
				}
				var type=data[i].finished.split("`")[1];//this.getActorType(data[i].finished);
				var tabid=data[i].tabid;
				var ins_id=data[i].ins_id;
				var task_id=data[i].task_id_e;
				var recallflag=data[i].recallflag;
				var actor_type=data[i].actortype;
				var actorname="";
					actorname=data[i].actorname;
				var encrypt=data[i].encrypt;
				var index=i;
				rows+="<tr height='32px'><td valign='middle' style='font-size:14px'><a class=\"tt\" href=\"javascript:template_table.searchDeclare('"+encrypt+"')\">"+data[i].name+"</a></td>"+
						"<td valign='middle' style='font-size:14px'>"+data[i].ins_start_date+"</td>"+
						"<td valign='middle' style='font-size:14px'>"+actorname+"</td>"+
						"<td valign='middle' style='font-size:14px'>"+type+((data[i].finished.split("`")[0]=="3"||data[i].finished.split("`")[0]=="2")&&recallflag=="1"?"<img src='/images/new_module/recall.png' onclick='template_table.recallTask("+index+")'  width='16' height='16' style='vertical-align:bottom;cursor:pointer;' title='撤回单据' border='0'>":"")+"</td></tr>";
			}
			var html="<table style='width:75%;height:100%;font-family:微软雅黑;margin-left:10px'><tr><td valign='top'><table width='99%' border='0' cellspacing='0' align='center' cellpadding='1'><tr height='32px' ><td valign='top'  style='width:25%;color:#474646;font-size:14px'>主题</td><td valign='top' style='width:15%;color:#474646;font-size:14px'>申请时间</td><td valign='top' style='width:10%;color:#474646;font-size:14px'>当前审批人</td><td valign='top' style='width:10%;color:#474646;font-size:14px'>状态</td></tr>"+rows+"</table></td></tr></table>";
			if(!data||data.length==0)//申报信息为空时 默认不显示表头
				html="";
			this.containerPanel(html);
		}
	},
	containerPanel:function(html){
		var panel=Ext.create('Ext.container.Container',{
			width:'100%',
			height:'100%',
			layout:'fit',
			html:html
		})
		this.add(panel);
	},
	showRoleInfo:function(e,tabid,task_id){
	    Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateNavigationOther': '../../module/template/templatenavigation/other'
			}
		});
		Ext.require('TemplateNavigationOther.DisplayRoleInfo',function(){
			Ext.create("TemplateNavigationOther.DisplayRoleInfo",{element:e,tabid:tabid,task_id:task_id});
		});
	},
	searchDeclare:function(encrpy,flag){//查看主题
		var htmlTem ="";
		if(flag=="1"){//查看申报信息
			var status=Ext.decode(this.res.responseText).status; 
			if(status==4){
				htmlTem='<iframe style="margin-top:-1px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/components/homewidget/TemplateHistoryData.html?b_query=link&tab_id=&module_id=9&fromflag=deskTop&other_param=visible_title=1&view_type=1&query_type=0';
			}else{
				htmlTem='<iframe style="margin-top:-1px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/components/homewidget/TemplateHistoryData.html?b_query=link&tab_id=&module_id=1&fromflag=deskTop&other_param=visible_title=1&view_type=1&query_type=0';
			}
		}else{
			htmlTem='<iframe style="margin-top:-1px;margin-left:-1px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/module/template/templatemain/templatemain.html?b_query=link&encryptParam='+encrpy;
		}
		var newWindow = Ext.create("Ext.container.Container", {
			plain : true,
			border : false,
			frame : false,
			resizable : false,
			modal : true,
			floating : true,
			layout : 'fit',
			draggable : false,
			style : 'background:white;z-index:9999',
			height : '100%',
			width : '100%',
			id : 'serviceHallWin',
			renderTo : Ext.getBody(),
			scrollable : false,
			html : htmlTem + '" />'
		});
	},
	recallTask:function(index){//撤回单据操作
	
		var data=template_table.data;
		if(data&&data.length>0){
			var records=[];
			records.push(data[index]);
			var myMsg = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
				title: '提示信息',
				icon: Ext.Msg.QUESTION,
				msg:Ext.isIE? "<div style='margin:0px 0px 14px 0px;'>确定执行撤回操作吗?</div>":"确定执行撤回操作吗?",
	            buttons: Ext.Msg.YESNO,
	            buttonText: {
	    						yes: '确认',
	    						no:'取消'
				},
				callback: function(optional){
					var map =new HashMap();
					if(optional=='yes'){
						map.put("recallList",records);
						map.put("module_id","9");
						map.put("ischeck","1");
					    Rpc({functionId:'MB00006018',async:false,success:function(form,action){
					    	var result = Ext.decode(form.responseText);
					    	var flag=result.succeed;
							if(flag==true){
								var notRecallName = result.notRecallName;
								if(notRecallName!=undefined&&notRecallName.length>0){
									Ext.showAlert("《"+notRecallName+"》表单已被查看或者已被处理,不可撤回!",function(){
										template_table.refreshPanel();
									});
									return;
								}
								var recallname = result.recallname;
								if(recallname!=undefined&&recallname.length>0){
									Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
										title: '提示信息',
										icon: Ext.Msg.QUESTION,
										msg:"《"+recallname+"》表单中存在未提交的数据,撤回将被覆盖,确定撤回吗\?",
							            buttons: Ext.Msg.YESNO,
							            buttonText: {
							    						yes: '确认',
							    						no:'取消'
										},
										callback: function(optional){
											if(optional=='yes'){
								    			var map = new HashMap();
								    			map.put("recallList",records);
								    			map.put("module_id","9");
								    			map.put("ischeck","0");
								    			Rpc({functionId:'MB00006018',async:false,success:function(form,action){
								    				var result = Ext.decode(form.responseText);
								    				if(result.succeed){
								    					template_table.refreshPanel();
								    				}else{
								    					Ext.showAlert(result.message);
														template_table.refreshPanel();
								    				}
								    			}},map);
								    		}else{
								    			return;
								    		}
										},
							            scope : this,
									    closeAction: 'destroy'
									 });
									
								}else{
									template_table.refreshPanel();
								}
					  		}else{
								Ext.showAlert(result.message);
								template_table.refreshPanel();
							}
					    }},map);
					}else
						return;
				},
	            scope : this,
			    closeAction: 'destroy'
		    });
		}
	},
	refreshPanel:function(){//刷新操作
		window.location.href="/templates/index/hcm_portal.do?b_query=link";
	}
	
});