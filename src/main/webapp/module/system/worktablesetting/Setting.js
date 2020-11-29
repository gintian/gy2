Ext.define('worktableURL.Setting',{
	requires:['EHR.homewidget.WorkTable'],
	constructor:function(){
		this.init();
	},
	init:function(){
		var setting_me=this;
		this.selectedData=[];
		//加载数据
		this.loadData();
		//下拉选组件store
		this.selectStore= Ext.create('Ext.data.Store', {
			id:'selectstore',
			fields: ['role_id','role_name'],
//			data:setting_me.data
		});
		//初始化选中的数据
		this.initSelectedData();
		//加载下拉选的store
		this.reloadSelectStore();
		//下拉选组件
		var selectComp={
			 xtype: 'combobox',
			 id:'rolecombobox',
			 margin:'20 0 5 20',
	         queryMode: 'local',
	         editable:false,
	         displayField: 'role_name',
	         valueField: 'role_id',
	         store:this.selectStore
		};
		//查询按钮
		var addBtn={
			xtype:'button',
			margin:'20 0 5 20',
			width:60,
			text: '添加',
			listeners:{
				click :function( me, e, eOpts ){
					var role_id=Ext.getCmp('rolecombobox').value;
					if(!role_id){
						Ext.MessageBox.alert("添加失败","请选择角色！");
						return;
					}
					setting_me.addSingleRegion(Ext.getCmp('rolecombobox').selection.data);
					setting_me.selectedData.push(role_id);
					setting_me.reloadSelectStore();
					Ext.getCmp('rolecombobox').setValue('');
				}
			}
		};
		
		//下拉选及添加按钮页面
		var selectAddPanel={
			xtype:'panel',
			height:60,
			border:0,
			layout:'hbox',
			items:[selectComp,addBtn]
		};
		
		//角色的方案配置面板主页面
		var menuCardListsPanel={
			xtype:'panel',
			id:'menuCardListsPanel',
			height:document.documentElement.clientHeight-60,
			width:document.documentElement.clientWidth,
			border:0,
//			autoScroll:true,
			bodyStyle:'overflow-y:auto;overflow-x:hidden',
//			layout:'vbox',
		};
		//主页面
		var mainPanel={
			xtype:'panel',
			id:'worktablemainpanel',
			height:document.documentElement.clientHeight,
			width:document.documentElement.clientWidth,
			border:0,
//			autoScroll:true,
			layout:'vbox',
			items:[selectAddPanel,menuCardListsPanel]
		};
		//渲染主页面
		Ext.create('Ext.container.Viewport', {
			style:'backgroundColor:white',
			layout:'fit',
			items:[mainPanel]
	    });
		this.initMenuPanel();
		//改变窗体大小时 组件大小也跟着改变
		Ext.on('resize', function (width, height)
          {
			var mainpanel=Ext.getCmp('worktablemainpanel');
			var menuCardListsPanel=Ext.getCmp('menuCardListsPanel');
          	if(mainpanel){
                mainpanel.setHeight(height);
                mainpanel.setWidth(width);
                menuCardListsPanel.setHeight(height-60);
                menuCardListsPanel.setWidth(width);
          	}
       });
	},
	//加载数据
	loadData:function(){
		var setting_me=this;
		Rpc({
			functionId : 'SYS00000001',
			async : false,
			success:function(res){
				var respon = Ext.decode(res.responseText);
				setting_me.data=respon.data;
				flag= respon.succeed;
			}
		}, new HashMap());
	},
	//设置选中的数据
	initSelectedData:function(){
		for(var i=0;i<this.data.length;i++){
			if(this.data[i].menuinfo[0]&&this.data[i].menuinfo!='[]')
				this.selectedData.push(this.data[i].role_id);
		}
	},
	//重新加载下拉选的store
	reloadSelectStore:function(){
		var data=[];
		for(var i=0;i<this.data.length;i++){
			if(!this.judgeIfInArray(this.data[i].role_id,this.selectedData))
				data.push(this.data[i]);
		}
		this.selectStore.removeAll(false);
		this.selectStore.loadData( data ) ;
	},
	//初始化菜单模板
	initMenuPanel:function(){
		for(var i=0;i<this.data.length;i++){
			if(this.judgeIfInArray(this.data[i].role_id,this.selectedData)){
				this.addSingleRegion(this.data[i]);
			}
		}
	},
	//加载单个角色区域
	addSingleRegion:function(Obj){
		var oneMenuPanel={
				xtype:'panel',
				border:0,
				layout:'vbox',
				items:[{
					xtype:'component',
					margin:'20 0 0 15',
					style:'color:#0092D2;font-size:22px;',
					html:Obj.role_name
				},{
					xtype:'worktable',
					title:false,
					collapsible:false,
					border:false,
					settingModel:true,
//					width:document.documentElement.clientWidth,
					width:'100%',
					objectid:Obj.role_id,
					objecttype:'role',
					menuinfo:Obj.menuinfo
				}]
				
		};
		Ext.getCmp('menuCardListsPanel').add(oneMenuPanel);
	},
	//判断数组中是否存在此字符
	judgeIfInArray:function(str,array){
		for(var i=0;i<array.length;i++){
			if(array[i]==str){
				return true;
			}
		}
		return false;
	}
})
