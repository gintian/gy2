Ext.define("ServiceClient.serviceHome.ViewService",{
	extend:'Ext.container.Container',
	serviceName:undefined,
	serviceUrl:undefined,
	initComponent:function(){
		this.callParent();
		this.createViewBox();
	},
	
	createViewBox:function(){
		me = this;
		var title =  Ext.create('Ext.panel.Panel',{
			xtype:'container',
			//显示到顶部
			region:'north',
			style:'background:url(/module/serviceclient/images/header_back.png)',
			//固定高度70px
			height:40,
			layout:{
				type:'hbox',
				align:'middle'
			},
			items:[{
				xtype:'panel',
				border:false,
				style:'margin-left:40px',
				html:sc.setting.servicename+':'+me.serviceName//sc.setting.servicename 服务名称
			},{
				xtype:'component',flex:1
			},{
				xtype:'component',
				style:'margin-right:40px;cursor:pointer',
				html:'<font size="2" color="red">'+sc.home.home+'</font>',
				listeners:{
					click:this.backHome,
					element:'el',
					scope:me
				}
			}]
			
		});
		var otherUrl = Ext.create('Ext.panel.Panel',{
			items:[{
				xtype:'panel',
				flex:1,
				border:0,
				style:'background:white',
				html:'<iframe width="100%" height="850px" scrolling="auto" src="'+me.serviceUrl+'"></iframe>'
			}]
			})
		var box =  Ext.create('Ext.panel.Panel',{
			region:'center',
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[title,otherUrl]
			
		});
		me.add(box);
	},
	/**返回首页按钮事件*/
	backHome:function(){
		//清除定时数据
        ServiceClientSecurity.resetTime();
		//通过id获取<中心区域>容器
		var serviceMainBox = Ext.getCmp('serviceMainBox');
		//移除并销毁当前对象
		serviceMainBox.removeAll(true);
		var servicePlatform = Ext.getCmp('servicePlatformCmp');
		//将服务面板显示到 容器中
		serviceMainBox.add(servicePlatform);
	}
});