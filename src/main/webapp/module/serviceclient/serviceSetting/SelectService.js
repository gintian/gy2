Ext.define('ServiceClient.serviceSetting.SelectService', {
	extend:'Ext.panel.Panel',
	id:'serviceSelect',
	width:'',
	panelWidth:'',
	long:'',
	serviceList:'',
	clientId:[],
	dateType:'',
	floating:true,
	style:'background-color:white',
	scrollable:'x',//水平滚动条
	border : false,//无边框
	buttonAlign:'center',
	buttons: [{
				text: sc.setting.ok,//确定
				handler: function(btn) {
					var array = [];
					var vo = new HashMap();
					var ownerCt = Ext.getCmp('controlanalyse');
					for(var k=0;k<serviceList.length;k++){
						if(Ext.getCmp('service_'+k).config.flag == 1){
							array.push(Ext.getCmp('service_'+k).config.serviceid);
						}
					}
					vo.put("clientId",clientId+'');
					if(array.length == serviceList.length){
						vo.put("selectedServiceId",'-1');
					}else{
						vo.put("selectedServiceId",array+'');
					}
					vo.put("dateType",dateType);
					if(array.length == serviceList.length){
						ownerCt.services = '-1';
					}else{
						ownerCt.services = array+'';
					}
					ownerCt.analyseKeyData = vo;
					ownerCt.reloadAnalyse();
					btn.up('panel').close();
				}
			},{
				text: sc.setting.cancel,//取消
				handler: function(btn) {
					btn.up('panel').close();
				}
			}],
	constructor:function(config){
		dateType = config.dateType;
		clientId = config.clientId;
		serviceList = config.serviceList;
		if(serviceList.length%4 != 0){
			this.long = (parseInt(serviceList.length/4)+1);
		}else{
			this.long = serviceList.length/4;
		}
		if(this.long>4){
			this.setWidth(802);
		}
		panelWidth = this.long*200;
		this.callParent();
		this.init();
	},
	init:function(){
		var form = this.getMainPanel();
		this.add(form);
	},
	getMainPanel:function(){
		var ownerCt = Ext.getCmp('controlanalyse');
		var tempArray = [];
		var mainPanel = Ext.create('Ext.form.Panel',{
										width:panelWidth,
										border:false,
										layout:'hbox',
										items:[serPanel],
									});
		for(var i=0;i<this.long;i++){
			var serPanel = Ext.create('Ext.form.Panel',{
				layout:'vbox',
				width:200,
				margin:'0 0 20 0',
				border:false,
			});
			var length = 4*(i+1);
			if(length>serviceList.length){
				length = serviceList.length;
			}
			for(var j=(i*4);j<length;j++){
				var iconSrc = sc.setting.iconurl + serviceList[j].icon;
				var servicePanel = Ext.create('Ext.form.Panel',{layout:'hbox',
					id:'service_'+j,
					flag:1,//选中
					border:false,
					serviceid:serviceList[j].serviceid,
					items:[{
    						xtype:'container',
    						items:[{
                                xtype:'image',
                                id:'service_'+i+'_'+j,
                                width:145,
                                height:75,
                                margin:'10 0 0 10',
                                src:iconSrc
    						},{
    							xtype:'panel',
                                border:false,
                                html:"<div style='word-break:break-all;color:white;cursor:pointer;font-size:13px;width:80px;height:37px;'>"+serviceList[j].name+"</div>",
                                bodyStyle:'background:transparent;',
                                style:'position:absolute;top:30px;left:70px;'
    						}]
						},{
						xtype:'image',
						id:'true_'+i+'_'+j,
						width:32,
						height:32,
						margin:'30 0 0 10',
						src:'/module/serviceclient/images/true.png'
					}],
					listeners:{
						render:function(){
							 if(ownerCt.services != "-1"){
								tempArray =  ownerCt.services.split(",");
								 if(!Ext.Array.contains(tempArray,this.serviceid)){
									 this.query('image')[1].hide();
									 this.config.flag=0;
								 }
							 }
					 },
					 click: {
				            element: 'el',
				            fn: function(){
				            	var panel = Ext.getCmp(this.id);
				            	if(panel.config.flag==1){
				            		panel.config.flag=0;
				            		var image = panel.query('image')[1];
				            		image.hide();
				            	}else{
				            		panel.config.flag=1;
				            		var image = panel.query('image')[1];
				            		image.show();
				            	}
				            }
				        }
					}});
				serPanel.add(servicePanel);
			}	
			mainPanel.add(serPanel);
		}
		return mainPanel;
	}
});
