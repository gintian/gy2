Ext.define('ServiceClient.serviceSetting.ServiceSetting',{
	extend:'Ext.panel.Panel',
	id:'serSetting',
	requires:['ServiceClient.serviceSetting.plugin.LabelEditor','EHR.portal.PortalPanel','ServiceClient.serviceSetting.EditService','ServiceClient.serviceSetting.ServiceAnalyse','ServiceClient.serviceSetting.ParamSetting'],
	title:sc.setting.funcname,
	layout:{type:'vbox',align:'stretch'},
	bodyPadding:'20 0 0 20',
	priv:undefined,
	//初始化组件
	initComponent:function(){
		//初始化框架参数
		this.initFrameConfig();
		this.callParent();
		this.loadData();
	},
	setStyle:function(){
		Ext.getCmp('serSetting_header').setStyle({
			borderStyle:'hidden hidden hidden solid'
		});
	},
	//初始化框架参数
	initFrameConfig:function(){
		var me = this;
		//参数设置、创建预览和监控按钮
		me.tools=[{
			xtype:'image',src:'/module/serviceclient/images/setting.png',height:22,width:22,
			style:'cursor:pointer',
			id:'m3',
			listeners:{
				click:function(){Ext.create('ServiceClient.serviceSetting.ParamSetting').show()},
				element:'el',
				mouseover:function(){
	        		Ext.QuickTips.init(); 
	                 Ext.QuickTips.register({ 
	                 target : 'm3', 
	                 text : sc.setting.parameterSettings//参数设置
	                }) 
	        	},
				scope:me
			}
		},{
			xtype:'image',src:'/module/serviceclient/images/preview.png',height:22,width:22,
			style:'cursor:pointer;margin-left:10px',
			id:'m1',
			listeners:{
				click:me.doPreview,
				element:'el',
				mouseover:function(){
	        		Ext.QuickTips.init(); 
	                 Ext.QuickTips.register({ 
	                 target : 'm1', 
	                 text : sc.setting.interfaceBrowsing//界面浏览
	                }) 
	        	},
				scope:me
			}
		},{
			xtype:'image',src:'/module/serviceclient/images/analyse.png',height:22,width:22,
			style:'cursor:pointer;margin-left:10px',
			id:'m2',
			listeners:{
				click:me.doAnalyse,
				element:'el',
				mouseover:function(){
	        		Ext.QuickTips.init(); 
	                 Ext.QuickTips.register({ 
	                 target : 'm2',
	                 text : sc.setting.controlanalyse //监控分析
	                }) 
	        	},
				scope:me
			}
		}];
		
		//创建分类按钮和分类容器
		me.items = [{
			//新建分类功能
			xtype:'container',layout:'hbox',
			items:[
			{xtype:'textfield',itemId:'groupNameField',emptyText:sc.setting.enterkindname,inputWrapCls:'groupNameInput',height:30,width:400},
			{xtype:'component',width:10},
			//{xtype:'button',text:sc.setting.creategroup,handler:me.createNewGroup,scope:me},
			{
				xtype:'component',html:'<input type="button" class="groupAddButton" value='+sc.setting.creategroup+'>',
				itemId:'addGroupButton',
				listeners:{
					click:me.createNewGroup,
					element:'el',
					scope:me
				}
			},
			{xtype:'component',width:10}
			]
		},{
			//设置面板，使用portalpanel组件实现拖动排序
			xtype:'portalpanel',
			flex:1,
			border:false,
			items:[{
				//portalpanel使用layout对象为column，需定义列容器
				xtype:'container',
				columnWidth:1,
				itemId:'groupBox'
			}],
			listeners:{
				//监听portalpanel的drop时间，当拖动调整顺序后会触发，此方法用于保存新的分类显示顺序
				drop:me.groupSort,
				scope:me
			}
		}]
	
	},
	//请求数据
	loadData:function(){
	
		var callbackFn = function(response){
			var me =this;
			me.groupBox = me.query('#groupBox')[0];
			var result = Ext.decode(response.responseText);
			var serviceData = result.serviceData;
			me.priv = result.priv;//权限信息
			me.query('#groupNameField')[0].setVisible( me.priv.isAddGroup?true:false);
			me.query('#addGroupButton')[0].setVisible( me.priv.isAddGroup?true:false);
			
			for(var i=0;i<serviceData.length;i++){
				//创建分类面板
				var group = me.createGroupCmp(serviceData[i]);
				me.groupBox.add(group);
			}
			me.setStyle();
		};
	
		var vo = new HashMap();
		vo.put("transType",'setting');
		Rpc({functionId:'SC000000001',transType:'setting',success:callbackFn,scope:this},vo);
	},
	
	createGroupCmp:function(groupConfig){
		var me = this;
		var groupBoxId = Ext.id(undefined,"serviceBox_");

		var groupPanel = Ext.widget('panel',{
			xtype:'panel',
			border:false,
			padding:'10 0 0 0',
			groupId:groupConfig.groupId,
			minHeight:35,
			//拖动参数，必须
			draggable:{
				moveOnDrag: false,
				//此参数定义 ext组件选择器，指定可以触发拖动的对象
				dragTargetSelector:'#groupName'
			},
			items:[{
				xtype:'container',layout:'hbox',
				style:'border-bottom:1px solid #c5c5c5',
				items:[{
					//展开合并按钮
					xtype:'image',
					margin:'10 10 0 0',
					height:6,
					width:7,
					src:'/workplan/image/jiantou.png',
					style:'cursor:pointer',
					listeners:{
						click:{
							element:'el',
							fn:function(evt,img){
								var serviceBox = Ext.getCmp(groupBoxId);
								if(serviceBox.isVisible()){
									serviceBox.setVisible(false);
									img.src="/workplan/image/jiantou_up.png";
								}else{
									serviceBox.setVisible(true);
									img.src="/workplan/image/jiantou.png";
								}
							},
							scope:me
						}
					}
				},{
					itemId:'groupName',xtype:'labeleditor',groupBoxId:groupBoxId,groupId:groupConfig.groupId,text:groupConfig.name,style:'color:rgb(13,141,252);font-size:20px;',height:30,
					deletable:me.priv.isDeleteGroup?true:false,readOnly:me.priv.isEditGroup?false:true,
					listeners:{
						//监听分类名称，如果点击删除按钮，则关闭 分类 panel
						remove:me.removeGroup,
						completeedit:me.editGroupName,
						scope:me
					}
				}]
			},{
				//此容器为显示 服务 的容器
				xtype:'container',
				id:groupBoxId,

			}]
		});
		var groupBox = Ext.getCmp(groupBoxId);
		if(groupConfig.services){
		for(var i=0;i<groupConfig.services.length;i++){
			var serviceCmp = me.createServiceCmp(groupConfig.services[i]);
			groupBox.add(serviceCmp);
		}
	}
		
		//创建 添加服务 按钮
		if(me.priv.isAddService){//新增服务权限
		groupBox.add({
			xtype:'image',
			style:'float:left;cursor:pointer',
			width:48,
			height:48,
			src:'/images/new_module/nocycleadd.png',
			margin:25,
			listeners:{
				click:{
					element:'el',
					fn:function(){
						this.showServiceWindow(groupBoxId,groupConfig.groupId);
					},
					scope:me
				}
			}
		})}else{
			groupBox.add({
				xtype:'component',
				style:'float:left;cursor:pointer',
				width:48,
				height:48,
				margin:25,
			})
		}
		
		
		return groupPanel;
	},
	//获取字符串长度（汉字算两个字符，字母数字算一个）
	getByteLen:function (val) {
      var charnum = 0;//字符数
      var varlength = 0;//前50字符长度
      for (var i = 0; i < val.length; i++) {
        var a = val.charAt(i);
        if (a.match(/[^\x00-\xff]/ig) != null) {
          charnum = charnum+2;
          varlength = varlength+1;
        }
        else {
          charnum =charnum+1;
          varlength = varlength+1;
        }
        if(charnum==50){
            return varlength;
        }else if(charnum>50){
            return varlength-1;
        }
      }
    },
	
	//创建新分类
	createNewGroup:function(){
		var me = this;
		var field = me.query("#groupNameField")[0];
		var groupName = field.getValue();
		field.setValue("");
		groupName = Ext.util.Format.trim(groupName);
		if(!groupName || groupName.length<1 || groupName==""){
			Ext.showAlert(sc.setting.entertypename);//为空
			return;
		}
		var varlength = me.getByteLen(groupName);//前50字符长度
		groupName = groupName.substring(0,varlength);
		var labels = me.groupBox.query('labeleditor');
		for(var i=0;i<labels.length;i++){
			if(groupName == labels[i].text){
				Ext.showAlert(sc.setting.alreadyexist);//已存在
				return;
			}
		}
		//后台校验
		var vo = new HashMap();
		vo.put("saveType","checkName");
		vo.put("groupName",groupName);
		Rpc({functionId:'SC000000002',async:false,success:function(res){
			flag = Ext.decode(res.responseText).flag;
		}},vo);
		if(flag){		
			Ext.Msg.alert(sc.home.tip,sc.setting.alreadyexist);//已存在
			return;
		}
		var groupId = this.addGroup(groupName);
		var groupCmp = me.createGroupCmp({groupId:groupId,name:groupName,services:[]});
		me.groupBox.add(groupCmp);
	},
	//新建分类保存
	addGroup:function(groupName){
		var groupId = -1;
		var vo = new HashMap();
		vo.put("saveType","addGroup");
		vo.put("groupName",groupName);
		Rpc({functionId:'SC000000002',async:false,success:function(res){
			groupId = Ext.decode(res.responseText).groupId;
		}},vo);
		return groupId;
	},
	//删除分类保存
	removeGroup:function(label){
		var groupBox = Ext.getCmp(label.groupBoxId);
		/*保存数据*/
		var vo = new HashMap();
		vo.put("saveType","deleteGroup");
		vo.put("groupId",label.groupId);
	
		if(groupBox.items.items.length-1>0){
			Ext.Msg.alert(sc.setting.remind,sc.setting.nodelete);
		}else{
			   Ext.Msg.confirm(sc.setting.remind,sc.setting.groupdeletebefore + label.text + sc.setting.groupdeleteafter,function(opt){
			   		if(opt=='yes'){
			   			Rpc({functionId:'SC000000002',success:this.saveResult},vo);
		   				label.up("panel").close();
			   		}else{
			   		 return;
			   		}
			   });
		}

	},
	//编辑分类保存
	editGroupName:function(label){
		/*保存数据*/
		var vo = new HashMap();
		vo.put("saveType","updateGroup");
		vo.put("groupId",label.groupId);
		vo.put("groupName",label.text);
		Rpc({functionId:'SC000000002',success:this.saveResult},vo);
	},
	//分类排序保存
	groupSort:function(param){
		var portal = param.portal;
		var groupOrder = [];
		var i=1;
		portal.child('#groupBox').items.each(function(cmp){
			groupOrder.push({groupId:cmp.groupId,norder:i});
			i++;
		});
		var vo = new HashMap();
		vo.put("saveType","sortGroup");
		vo.put("groupOrder",groupOrder);
		Rpc({functionId:'SC000000002',success:this.saveResult},vo);
	},
	//新增服务
	/**
	 * 参数：
	 * groupBoxId:所在分组的容器id，将新创建的服务添加到此容器中
	 * groupId:所在分组的id，用于保存服务数据使用
	 * 创建新建服务window，收集完数据后，调用addService方法保存服务数据并获取后端回传的serviceId参数，
	 * 调用createServiceCmp方法创建服务对象，并添加groupBoxId对应容器中
	 */
	showServiceWindow:function(groupBoxId,groupId){
		
		   Ext.create("ServiceClient.serviceSetting.EditService",{
		               groupId:groupId,
					   handler:this.addService,
					   ownerGroupBoxId:groupBoxId,
					   selectType:1,//代表新建服务
					   scope:this
		   });
	},
	//创建服务显示对象
	/**
	 * 参数格式：
	 * {
		//服务id
		serviceId:1,
		//服务名称
		name:’收入证明’, 
		//1:打印服务；2：其他服务
		type:’1’,
		//服务图标
		icon:’xxx.png’, 
		//其他服务地址
		url:’/xxx/xxx/xxxx’,
		}
	 */
	createServiceCmp:function(serviceConfig){
		var me = this;
		var nameUnion = 'name_'+serviceConfig.serviceId;
		var iconSrc = sc.setting.iconurl + serviceConfig.icon;
		var serviceHtml = '<table id="'+serviceConfig.serviceId+'" cmptype="service" serviceid="'+serviceConfig.serviceId+'" border=0 width=175 height=90 cellpadding=0 cellspacing=0 style="cursor:pointer;margin:10px 0px 0px 10px;">'+
		  '<tr>'+
		  '<td height=90 width=175>'+
	      	'<img src='+iconSrc+' style="width:100%;height:100%;">'+
	      '</td>'
		  		+
		      '<td height=75 style="vertical-align:top">'+
		      	'<img functype="del" src="/components/homewidget/images/del.png" style="display:none;position:absolute;top:3px;left:175px;width:18px;height:18px;float:right;">'+
		      '</td>'+
		  '</tr>'+
		'</table>';
		var serviceCmp = Ext.widget('container',{
			scope:me,
			width:175,
			height:110,
			style:'float:left',
			margin:'0 0 10 10',
			itemId:"serviceContainer"+serviceConfig.serviceId,
			items:[{
				xtype:'container',
				width:70,
				height:75,
				itemId:'serviceHtml'+serviceConfig.serviceId,
				html:serviceHtml,
				style:'float:left'
			},{
				xtype:'panel',
				width:95,
				height:44,
				itemId:'nameHtml'+serviceConfig.serviceId,
				border:false,
				html:"<div id = "+nameUnion+" style='word-break:break-all;color:white;font-size:16px;'>"+serviceConfig.name+"</div>",
				bodyStyle:'background:transparent;',
				style:'position:absolute;right:-4px;top:34px;cursor:pointer;',
			}],
			listeners:{
	        	element:'el',
	        	click:function(evt){
	        		var table = document.getElementById(serviceConfig.serviceId);
		        	if(evt.target.getAttribute('funcType')!='del'&&me.priv.isEditService){//编辑服务权限
						var map = new HashMap();
						map.put("saveType", "checkTabExist");
						map.put("templateType", serviceConfig.templateType);//1：业务模板  2：登记表
						map.put("templateId", serviceConfig.templateId);//登记表或业务模板id
						Rpc({functionId: 'SC000000002', success: function (res) {
							var resultData = Ext.decode(res.responseText);
							if (resultData.tabExistFlag) {
								Ext.create("ServiceClient.serviceSetting.EditService",{
									handler:me.editService,
									serviceCmp:serviceCmp,
									serviceId:serviceConfig.serviceId+'',
									selectType:2,//代表编辑服务
									templateType:serviceConfig.templateType+"",//1：业务模板  2：登记表
									scope:me
								});
							}else{
								var tempName = sc.setting.businessform;// '业务表单';
								if (serviceConfig.templateType==="2") {
									tempName = sc.setting.registryform;//'登记表';
								}
								Ext.Msg.alert(sc.home.tip, sc.setting.tabNotExist.replace("{0}", tempName));//已存在
							}
						}, scope: this}, map);
		        	}
	        	  	if(evt.target.getAttribute('funcType')=='del'){
			            Ext.Msg.show({
		                    title:"<div style='margin-left:5px'>"+sc.setting.promptmessage+"</div>",
		                    msg: sc.setting.servicedelete,//确定删除所选服务吗？
		                    buttons: Ext.Msg.YESNO,
		                    buttonText: {
		    						yes: sc.setting.ok,
		    						no: sc.setting.cancel
								},
		                    fn: function(btn){
		                       if(btn!='yes'){
		                    	   return;
		                       }
		                       me.deleteService(table.getAttribute("serviceid"),serviceCmp);        
		                    },
		                    icon: Ext.MessageBox.QUESTION
		                });
			            if(!Ext.isIE){
			            	var messageboxObj = document.getElementById("messagebox-1001").style="height: 128px; right: auto; left: 501px; top: 211px; width: 250px; z-index: 19001;";
				            var ext_gen = document.getElementsByClassName("x-css-shadow")[0];
				            if(ext_gen)
				            	ext_gen.style="display: none;box-shadow:none;";
			            }
		            	return;
	        	  	}
	        	  	if(table.getAttribute("cmptype")=='service'){
	        	     	return;
	        	  	}
	        	},
	        	mouseover:function(field){
	        		var table = document.getElementById(serviceConfig.serviceId);
	        		if(me.priv.isDeleteService){//删除服务权限
	        			table.getElementsByTagName('img')[1].style.display="block";
	        		}
	        		var div = Ext.getDom(nameUnion);
	        		Ext.QuickTips.init(); 
	                Ext.QuickTips.register({ 
	                	target : this.id, 
	                	text : div.innerHTML
	                }) 
	        	},
	        	mouseout:function(){
	        		var table = document.getElementById(serviceConfig.serviceId);
	        		table.getElementsByTagName('img')[1].style.display="none";
	        	
	        	}
			}
		});
		return serviceCmp;
	},
	
	//新建服务保存
	/**
	 * 调用交易类（SC000000002）保存服务
	 * 参数：
	 * serviceConfig格式:
	 * {
		//所属服务分类id
		groupId:1,
		//服务名称
		name:’收入证明’, 
		//1:打印服务；2：其他服务
		type:’1’,
		//服务图标
		icon:’/xxx/xxx/xxx.png’, 
		//打印服务模板id
		templateId:’12’,
		//模板类型 1:业务模板 2:登记表
		templateType:’1’,
		//有效期，单位月（按30天算）
		effectiveDate:2,
		//其他服务地址
		url:’/xxx/xxx/xxxx’,
		//免费打印份数
		freePrintCount:5,
		//打印一份的价格
		printPrice:0.5,
		description:’服务详细说明....’ 
		}
		
	 *	返回值：serviceId
	 */
	addService:function(serviceConfig,groupBoxId){
		var me = this;
		var map = new HashMap();
		map.put("saveType","addService");
		map.put("serviceConfig",serviceConfig);
		Rpc({functionId:'SC000000002',success:function(res){
			result = Ext.decode(res.responseText);
			serviceConfig.serviceId = result.serviceId;
			var cmp = me.createServiceCmp(serviceConfig);
			var groupcmp = Ext.getCmp(groupBoxId);
			groupcmp.insert(groupcmp.items.length-1,cmp);
		}},map);
	},
	//删除服务保存
	/**
	 * 参数：
	 * serviceId：删除的服务id
	 */
	deleteService:function(serviceId,serviceCmp){
		var me = this;
		var vo = new HashMap();
		vo.put("saveType","deleteService");
		vo.put("serviceId",serviceId);
		Rpc({functionId:'SC000000002',success:function(){
			var groupBox = serviceCmp.ownerCt;
			groupBox.remove(serviceCmp);
		}},vo);
	},
	
	//保存结果回调函数
	saveResult:function(){
			
	},
	//预览
	doPreview:function(){
		this.destroy();
		window.location.href="doPreview.html"

	},
	//分析监控
	doAnalyse:function(){
		this.destroy();
		window.location.href = "ServiceAnalyse.html";
	   

	},
	editService:function(editData,serviceCmp){
		var me = this;
		var map = new HashMap();
		map.put("saveType","editService");
		map.put("editData",editData);
		Rpc({functionId:'SC000000002',success:function(res){
			me.refreshServiceData(editData,serviceCmp);
		},scope:me},map);
	},
	refreshServiceData:function(editData,serviceCmp){
		var itemId = serviceCmp.itemId;
		var htmlItemId = "serviceHtml"+itemId.substring(16);
		var nameItemId = 'nameHtml'+itemId.substring(16);
		var iconImageSrc = sc.setting.iconurl + editData.icon;
		var styleItem = Ext.ComponentQuery.query('#'+htmlItemId)[0];
		var nameItem = Ext.ComponentQuery.query('#'+nameItemId)[0];
		var nameUnion = 'name_'+editData.serviceId;
		var serviceHtml = '<table id="'+editData.serviceId+'" cmptype="service" serviceid="'+editData.serviceId+'" border=0 width=175 height=90 cellpadding=0 cellspacing=0 style="cursor:pointer;margin:10px 0px 0px 10px;">'+
			'<tr>'+
				'<td height=90 width=175>'+
					'<img src='+iconImageSrc+' style="width:100%;height:100%;">'+
				'</td>'+
				'<td height=75 style="vertical-align:top">'+
					'<img functype="del" src="/components/homewidget/images/del.png" style="display:none;position:absolute;top:3px;left:175px;width:18px;height:18px;float:right;">'+
				'</td>'+
			'</tr>'+
		'</table>';
		var nameHtml = "<div id="+nameUnion+" style='word-break:break-all;color:white;font-size:16px;'>"+editData.name+"</div>"
		styleItem.setHtml(serviceHtml);
		nameItem.setHtml(nameHtml);
	}
});