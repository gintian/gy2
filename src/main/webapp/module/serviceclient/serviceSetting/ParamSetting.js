Ext.define('ServiceClient.serviceSetting.ParamSetting', {
	extend:'Ext.window.Window',
	title:sc.setting.paramsetting,//参数设置
	width:500,
	height:400,
	modal:true,
	id:'ParamSetting',
	settingMessage:'',//用于接收获取的信息
	resizable : false,//禁止缩放
	draggable : false,//禁止拖动
	initComponent : function() {
		var me = this;
		me.callParent();
		me.searchMsg();//获取配置信息
		me.init();//主函数入口
	},
	//获取配置信息
	searchMsg : function(){
		var me = this;
		var saveType = 'searchParamSetting';//用于判断的标志位
		var vo = new HashMap();
		vo.put("saveType",saveType);
		Rpc({
			functionId : 'SC000000002',
			async : false,
			success : function(form, action) {	
				me.settingMessage = Ext.decode(form.responseText).settingMsg;
				me.displayFielditem = me.settingMessage.displayFielditem;
			},
			scope : this
		}, vo);
	},
	//传输信息
	sendMsg : function(){
		var me = this;
		var itemId = Ext.getCmp("icCardField").getValue();
		if(!itemId){
			itemId = "";
		}
		var saveType = 'saveParamSetting';
		var  needPwdInput = me.query('#needPassword')[0].getValue();
		if(needPwdInput==true){
			needPwdInput="1";
		}else if(needPwdInput==false){
			needPwdInput="0";
		}
		var vo = new HashMap();
		vo.put("saveType",saveType);
		vo.put("itemId",itemId);
		vo.put("needPwdInput",needPwdInput);
		Rpc({
					functionId : 'SC000000002',
					async : false,
					success : function(form, action) {	
						var result = Ext.decode(form.responseText).result;
						if(result){
							Ext.MessageBox.alert(sc.setting.tip,sc.setting.settingsuccess);//配置成功
							me.close();
						}else{
							Ext.MessageBox.alert(sc.setting.tip,sc.setting.settingfailure);//配置失败
						}
					},
					scope : me
				}, vo);
	},
	//入口
	init:function(){
		var me = this;
		var storeData = [];//数据源的数据
		var fieldLength = me.displayFielditem.length;//指标数量
		for(var i=0;i<fieldLength;i++){
		    var eachFieldData = [];//每一个指标数据
		    var itemId = me.displayFielditem[i].itemid;
		    var itemDesc = me.displayFielditem[i].itemdesc;
		    eachFieldData.push(itemId);
		    eachFieldData.push(itemDesc);
		    storeData.push(eachFieldData);
		}
		var store = new Ext.data.ArrayStore({
            fields:['itemId','itemDesc'],
            data:storeData
		});
		var showPanel = Ext.create("Ext.panel.Panel", {
			width : '100%',// 宽
			height :350,// 高
			border : false,
			itemId:'showPanel',
			items:[{
				boxLabel:sc.setting.boxtip,//读卡登录时，需要输入密码
				margin:'10 0 0 30',
				xtype:'checkbox',
				itemId:'needPassword',
				value :me.settingMessage.password_cardValue
			},{
                xtype:'combo',
                id:'icCardField',
                style:'margin-left:-22px;margin-top:15px',
                fieldLabel: sc.home.IcCardField,//工卡指标
                store:store,
                valueField:'itemId',
                displayField:'itemDesc',
                labelAlign:'right',
                emptyText:sc.setting.pleaseSelect,//请选择
                width:215,
                editable:false,
                listeners:{
                    afterRender:function(combo){
                        combo.setValue(me.settingMessage.fieldItemId);
                    }
                }
			}],
			buttonAlign:'center',
			buttons: [{
					text:sc.setting.ok,
					handler :function(){
						me.sendMsg();
					}
					},{
					text:sc.setting.cancel,
					handler : function() {
						this.up('window').close();
						}
					}]	
			});
		me.add(showPanel);
	}
})