Ext.define('ServiceHall.Setting',{
	extend:'Ext.container.Container',
	requires:['EHR.portal.PortalPanel','ServiceHall.LabelEditor','ServiceHall.EditService','ServiceHall.ServicePanel'],
	layout:{type:'vbox',align:'stretch'},
	padding:'20 0 0 20',
	priv:undefined,
	//初始化组件
	initComponent:function(){
		//初始化框架参数
		this.initFrameConfig();
		this.callParent();
		this.groupBox = this.query('#groupBox')[0];
		this.serviceMap = {};
		this.loadData();
	},
	/*setStyle:function(){
		Ext.getCmp('serSetting_header').setStyle({
			borderStyle:'hidden hidden hidden solid'
		});
	},*/
	//初始化框架参数
	initFrameConfig:function(){
		var me = this;
		//创建分类按钮和分类容器
		me.items = [{
			//新建分类功能
			xtype:'container',layout:'hbox',
			items:[
			{xtype:'textfield',itemId:'groupNameField',emptyText:shs.entergroupname,inputWrapCls:'groupNameInput',height:30,width:400},
			{xtype:'component',width:10},
			{
				xtype:'component',html:'<input type="button" class="groupAddButton" value='+shs.creategroup+'>',
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
			
			
			for(var i=0;i<serviceData.length;i++){
				//创建分类面板
				var group = me.createGroupCmp(serviceData[i]);
				me.groupBox.add(group);
			}
			//me.setStyle();
		};
	
		var vo = new HashMap();
		vo.put("transType",'queryData');
		Rpc({functionId:'SYS00003001',success:callbackFn,scope:this},vo);
	},
	
	createGroupCmp:function(groupConfig){
		var me = this;
		var groupBoxId = Ext.id(undefined,"groupBox_");

		var groupPanel = Ext.widget('panel',{
			xtype:'panel',
			border:false,
			padding:'10 0 0 0',
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
					src:rootPath+'/workplan/image/jiantou.png',
					style:'cursor:pointer',
					listeners:{
						click:{
							element:'el',
							fn:function(evt,img){
								var serviceBox = Ext.getCmp(groupBoxId);
								if(serviceBox.isVisible()){
									serviceBox.setVisible(false);
									img.src=rootPath+"/workplan/image/jiantou_up.png";
								}else{
									serviceBox.setVisible(true);
									img.src=rootPath+"/workplan/image/jiantou.png";
								}
							},
							scope:me
						}
					}
				},{
					itemId:'groupName',xtype:'labeleditor',groupBoxId:groupBoxId,
					oldText:groupConfig.groupname,
					text:groupConfig.groupname,
					style:'color:rgb(13,141,252);font-size:20px;',
					height:30,
					deletable:true,
					listeners:{
						//监听分类名称，如果点击删除按钮，则关闭 分类 panel
						remove:me.removeGroup,
						completeedit:me.editGroupName,
						scope:me
					}
				}]
			},{
				//此容器为显示 服务 的容器
				xtype:'servicepanel',
				id:groupBoxId,
				border:false,
				ddGroup:groupBoxId,
				listeners:{
					drop:me.sortService
				}
			}]
		});
		var groupBox = Ext.getCmp(groupBoxId);
		if(groupConfig.services){
			for(var i=0;i<groupConfig.services.length;i++){
				var serviceObj = groupConfig.services[i];
				serviceObj.groupBoxId = groupBoxId;
				var serviceCmp = me.createServiceCmp(serviceObj);
				groupBox.add(serviceCmp);
				
				if(serviceObj.type!=3)
					me.serviceMap[serviceObj.type+"|"+serviceObj.tabid] = true;
			}
		}
		
		//创建 添加服务 按钮
		groupBox.add({
			xtype:'image',
			style:'float:left;cursor:pointer',
			width:48,
			height:48,
			src:rootPath+'/images/new_module/nocycleadd.png',
			margin:14,
			listeners:{
				click:{
					element:'el',
					fn:function(){
						this.showServiceWindow({groupBoxId:groupBoxId});
					},
					scope:me
				}
			}
		});
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
		var groupname = field.getValue();
		field.setValue("");
		groupname = Ext.util.Format.trim(groupname);
		if(!groupname || groupname.length<1 || groupname==""){
			Ext.showAlert(shs.entertypename);//为空
			return;
		}
		var varlength = me.getByteLen(groupname);//前50字符长度
		groupname = groupname.substring(0,varlength);
		var labels = me.groupBox.query('labeleditor');
		for(var i=0;i<labels.length;i++){
			if(groupname == labels[i].text){
				Ext.showAlert(shs.groupalreadyexist);//已存在
				return;
			}
		}
		var groupCmp = me.createGroupCmp({groupname:groupname,services:[]});
		me.groupBox.add(groupCmp);
	},
	//删除分类保存
	removeGroup:function(label){
		var groupBox = Ext.getCmp(label.groupBoxId);
		/*保存数据*/
		var vo = new HashMap();
		vo.put("transType","delGroup");
		vo.put("saveData",{groupname:label.text});
	
		Ext.Msg.confirm(shs.remind,shs.deletegroup,function(opt){
	   		if(opt=='yes'){
	   			Rpc({functionId:'SYS00003001',success:function(res){
	   				var param = Ext.decode(res.responseText);
					if(!param.result){
						Ext.showAlert(shs.savefailed);
						return;
					}
					label.up("panel").close();
	   			}},vo);
   				
	   		}else{
	   		 	return;
	   		}
		 });

	},
	//编辑分类保存
	editGroupName:function(label){
		/*保存数据*/
		var vo = new HashMap();
		vo.put("transType","editGroup");
		var saveData = {oldgroupname:label.oldText,newgroupname:label.text}
		vo.put("saveData",saveData);
		Rpc({functionId:'SYS00003001',success:function(res){
			var param = Ext.decode(res.responseText);
			if(!param.result){
				Ext.showAlert(shs.savefailed);
				return;
			}
			label.oldText = label.text;
		}},vo);
	},
	//分类排序保存
	groupSort:function(param){
		var portal = param.portal;
		var saveData = [];
		var i=0;
		portal.child('#groupBox').items.each(function(cmp){
			saveData.push({groupname:cmp.queryById('groupName').text,grouporder:i});
			i++;
		});
		var vo = new HashMap();
		vo.put("transType","sortGroup");
		vo.put("saveData",saveData);
		Rpc({functionId:'SYS00003001',success:function(res){
			var param = Ext.decode(res.responseText);
			if(!param.result){
				Ext.showAlert(shs.savefailed);
			}
		}},vo);
	},
	//新增服务
	/**
	 * 参数：
	 * serviceObj:服务对象
	 * serviceCmpId:编辑的组件id（如果新增服务，无此参数）
	 * 创建或修改服务window，收集完数据后，调用saveService方法保存服务数据并获取后端回传的serviceId参数，
	 * 
	 */
	showServiceWindow:function(serviceObj,serviceCmpId){
		var me = this;
	   	Ext.create("ServiceHall.EditService",{
		   serviceData:serviceObj,
		   serviceCmpId:serviceCmpId,
		   serviceMap:me.serviceMap,
		   title:serviceCmpId===undefined?shs.createservice:shs.editservice,
		   listeners:{
		   	   editcomplete:me.saveService,
		   	   scope:me
		   }
	   	});
	},
	//创建服务显示对象
	/**
	 * 参数格式：
	 * {
		//服务id
		serviceId:1,
		//所在分组组件id
		groupBoxId:'xxxx',
		//服务名称
		name:’收入证明’, 
		//1:业务表单；2：登记表；3：自定义链接
		type:’1’,
		tabid:'12',业务表单&登记表id
		//服务图标
		icon:’xxx.png’, 
		//其他服务地址
		linkurl:’/xxx/xxx/xxxx’
		}
	 */
	createServiceCmp:function(serviceConfig){
		var name = serviceConfig.tabname;
		name = name.replace(/(\n)/g, "");    
        name = name.replace(/(\t)/g, "");    
        name = name.replace(/(\r)/g, "");    
        name = name.replace(/<\/?[^>]*>/g, "");    
        name = name.replace(/\s*/g, "");
		name = name.replace("<", "");
		name = name.replace(">", "");
        serviceConfig.tabname = name;
		var me = this;
	    var iconPath = rootPath+"/components/homewidget/images/serviceicon/"+serviceConfig.icon;
	    var cmpId = Ext.id(undefined,"service_");
		var serviceCmp = Ext.widget('panel',{
			width:155,
			height:85,
			id:cmpId,
			serviceid:serviceConfig.serviceid,
			serviceObj:serviceConfig,
			border:false,
			draggable:{
				moveOnDrag: false,
				ddGroup:serviceConfig.groupBoxId,
				//此参数定义 ext组件选择器，指定可以触发拖动的对象
				dragTargetSelector:'div[box]'
			},
			style:'float:left;position:relative;',
			html:'<div box="1" style="margin:5px;padding:19px 0 0 10px;width:145;height:100%;background:#f6f6f6;">'+
				'<img  width="36" height="36" type="tabicon" style="float:left;" src="'+iconPath+'">'+
				'<div type="tabname" style="float:left;margin-left:10px;font-size:14px;line-height:17px;overflow:hidden;width:80px;height:36px;">'+serviceConfig.tabname+'</div>'+
				'</div>'+
				'<img func="del"  self="'+cmpId+'" src="'+rootPath+'/components/homewidget/images/del.png" style="display:none;position:absolute;cursor:pointer;top:0px;left:140px;"/>'+
				'<img func="edit" self="'+cmpId+'" src="'+rootPath+'/components/homewidget/images/edit.png" style="display:none;position:absolute;cursor:pointer;top:61px;left:126px;"/>',
				
			listeners:{
				click:{
					element:'el',
					delegate:'img[func]',
					scope:me,
					fn:me.handlerFunc //me.deleteService
				},
				mouseover:{
					element:'body',
					fn:function(ev){
						var body = Ext.get(ev.delegatedTarget);
						var funcImg = body.query('img[func]',true);
						funcImg[0].style.display='block';
						funcImg[1].style.display='block';
					}
				},mouseout:{
					element:'body',
					fn:function(ev){
						var body = Ext.get(ev.delegatedTarget);
						var funcImg = body.query('img[func]',true);
						funcImg[0].style.display='none';
						funcImg[1].style.display='none';
					}
				}
			}
		});
		return serviceCmp;
	},
	
	//服务保存
	/**
	 * 调用交易类（SC000000002）保存服务
	 * 参数：
	 * serviceConfig格式:
	 * {
			groupBoxId:所属分组panel的id
			name:名称
			icon:图标名称
			type:类型：1=业务模板；2=登记表；3：自定义url地址
			template:模板号&登记表号&url地址
		}
		
	 *	返回值：serviceId
	 */
	saveService:function(serviceConfig,serviceCmpId){
		var me = this;
		//如果有serviceid，是修改操作
		if(serviceConfig.serviceid){
			var map = new HashMap();
			map.put("transType","updateService");
			map.put("saveData",serviceConfig);
			//【61473】服务大厅，添加，选择业务模板后保存，提示保存失败
			//判断名称所占字节是否超过50（数据库中定义的字段长度是50）
			if(serviceConfig.tabname.replace(/[^\u0000-\u00ff]/g,"aa").length > 50){
				Ext.showAlert(shs.lengthfailed);
				return;
			}
			Rpc({functionId:'SYS00003001',success:function(res){
				var serviceCmp = Ext.getCmp(serviceCmpId);
				var namediv = serviceCmp.el.query('div[type]')[0];
				namediv.innerHTML=serviceConfig.tabname;
				
				var iconimg = serviceCmp.el.query('img[type]')[0];
				iconimg.src=rootPath+"/components/homewidget/images/serviceicon/"+serviceConfig.icon;
				
				if(serviceConfig.type!=3)
					me.serviceMap[serviceConfig.type+"|"+serviceConfig.tabid] = true;
			}},map);
			
			return;
		}
		
		//新增服务操作
		var sc = serviceConfig;
		var groupcmp = Ext.getCmp(serviceConfig.groupBoxId);
		var groupPortal = groupcmp.ownerCt;
		
		serviceConfig.groupname = groupPortal.queryById('groupName').text;
		serviceConfig.grouporder = groupPortal.ownerCt.items.indexOf(groupPortal);
		serviceConfig.taborder = groupcmp.items.getCount()-1;
		if(serviceConfig.type==3){
			serviceConfig.tabid = -1;
		}else{
			serviceConfig.tabid = parseInt(serviceConfig.tabid);
		}
		
		var map = new HashMap();
		map.put("transType","addService");
		map.put("saveData",serviceConfig);
		//【61473】服务大厅，添加，选择业务模板后保存，提示保存失败
		//判断名称所占字节是否超过50（数据库中定义的字段长度是50）
		if(serviceConfig.tabname.replace(/[^\u0000-\u00ff]/g,"aa").length > 50){
			Ext.showAlert(shs.lengthfailed);
			return;
		}
		Rpc({functionId:'SYS00003001',success:function(res){
			var param = Ext.decode(res.responseText);
			if(!param.result){
				Ext.showAlert(shs.savefailed);
				return;
			}
			serviceConfig.serviceid = param.serviceid;
			var cmp = me.createServiceCmp(serviceConfig);
			groupcmp.insert(groupcmp.items.length-1,cmp);
			
			me.serviceMap[serviceConfig.type+"|"+serviceConfig.tabid] = true;
		}},map);
	},
	
	handlerFunc:function(ev,ele){
		
		var func = ele.getAttribute("func");
		var serviceCmpId = ele.getAttribute("self");
		var serviceCmp = Ext.getCmp(serviceCmpId);
		var serviceObj = serviceCmp.serviceObj;
		
		if(func=='del'){
			this.deleteService(serviceCmpId,serviceObj)
		}else{
			this.showServiceWindow(serviceObj,serviceCmpId);
		}
		
	},
	
	//删除服务保存
	/**
	 * 参数：
	 * serviceId：删除的服务id
	 */
	deleteService:function(serviceCmpId,serviceObj){
		var me =this;
		var doDelete = function(){
			var groupBoxId = serviceObj.groupBoxId;
			var serviceCmp = Ext.getCmp(serviceCmpId);
			
			var saveData = {serviceid:serviceCmp.serviceid};
			var vo = new HashMap();
			vo.put("transType","delService");
			vo.put("saveData",saveData);
			Rpc({functionId:'SYS00003001',success:function(res){
				var param = Ext.decode(res.responseText);
				if(!param.result){
					Ext.showAlert(shs.savefailed);
					return;
				}
				delete me.serviceMap[serviceObj.type+"|"+serviceObj.tabid];
				Ext.getCmp(groupBoxId).remove(serviceCmp,true);
			}},vo);
		};
		Ext.Msg.confirm(shs.remind,shs.deleteservice,function(opt){
	   		if(opt=='yes'){
   				doDelete();
	   		}else{
	   		 	return;
	   		}
		 });
	},
	sortService:function(groupbox){
		var items = groupbox.items.items;
		var saveData = [];
		for(var i=0;i<items.length-1;i++){
			saveData.push({serviceid:items[i].serviceid,taborder:i});
		}
		var vo = new HashMap();
		vo.put("transType","sortService");
		vo.put("saveData",saveData);
		Rpc({functionId:'SYS00003001',success:function(res){
			var param = Ext.decode(res.responseText);
			if(!param.result){
				Ext.showAlert(shs.savefailed);
			}
		}},vo);
	}
});