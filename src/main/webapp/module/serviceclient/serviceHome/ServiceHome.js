Ext.define('ServiceClient.serviceHome.ServiceHome',{
	requiers:['ServiceClient.serviceHome.ServicePlatform','ServiceClient.serviceHome.FirstModifyPassword'],
	extend:'Ext.container.Container',
	layout:'border',//border布局
	loadType:undefined,//默认为undefined
	ip:undefined,
	initComponent:function(){
		this.callParent();
		this.initHeader();
		this.initServiceMainBox();
	},
	//初始化头部
	initHeader:function(){
    		if(!this.loadType){
    		    VirtualKeyboard.close();
    		}
			this.add({
				xtype:'container',
				//显示到顶部
				region:'north',
				style:'background:url(/module/serviceclient/images/header_back.png)',
				//固定高度70px
				height:100,
				layout:{
					type:'hbox',
					align:'middle'
				},
				items:[{
					xtype:'component',
					style:'margin-left:40px',
					html:'<img src="/module/serviceclient/images/index/logo.png"/>',
					width:218,height:70
					//src:'/module/serviceclient/images/index/logo.png',
				},{
					xtype:'component',
					html:'<img src="/module/serviceclient/images/index/title.png"/>',
                    width:312,height:70
                   //src:'/module/serviceclient/images/index/title.png',
				},{
					xtype:'component',flex:1
				},{
                    xtype:'container',//首页
                    id:'goHome'
                },{
                    xtype:'component',
                    style:'background:url(/module/serviceclient/images/modifyPwd.png) no-repeat;background-position-y:60%;font-size:20px;color:white;margin-right:10px;margin-left:4px;padding-left:25px;cursor:pointer',
                    html:sc.home.modifyPassWord,//修改密码
                    hidden:this.loadType=='preview'? true:false,
                    listeners:{
                        click:function(){
                            Ext.create('ServiceClient.serviceHome.FirstModifyPassword',{
                                accessType:"modifyPassword"
                            }).show();
                        },
                        element:'el',
                        scope:this
                    }
                },{
					xtype:'component',
					style:'background:url(/module/serviceclient/images/logout.png) no-repeat;background-position-y:60%;font-size:20px;color:white;margin-right:40px;padding-left:30px;cursor:pointer',
					html:this.loadType!='preview'?sc.home.exitSystem:sc.home.exitPreview,//退出系统  退出预览
					listeners:{
						click:this.loadType!='preview'?this.logout:this.logoutPreview,
						element:'el',
						scope:this
					}
				}]
			});
	},
	//初始化服务中心
	initServiceMainBox:function(){
		this.add({
			//中心区域
			xtype:'container',
			//固定id，用于后期切换界面
			id:'serviceMainBox',
			//显示到中心
			region:'center',
			//子元素布局为充满
	     	layout:'fit',
			//加载具体服务列表对象
			items:Ext.create('ServiceClient.serviceHome.ServicePlatform',{
				servicesData:this.servicesData,
				ip:this.ip,
				loadType:this.loadType
			})
		});
	},
	
	//退出登录 操作方法
	logout:function(){
		/*alert('logout!');
		window.location.href="index.jsp";*/
		Ext.Ajax.request({
			//注销地址
			url: '/servler/sys/logout',
			params:{flag:14},
			success: function() {
            //注销成功刷新界面
            window.location.reload();
            }
		});
	},
	
	logoutPreview:function(){
		window.location.href="setting.html";
	}
});
