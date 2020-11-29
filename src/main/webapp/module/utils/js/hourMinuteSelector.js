/**
 * 时分下拉框
 */
Ext.define("ZP.GENERATETIME",{
	
	extend:'Ext.panel.Panel',
	me:'',
	//下拉框宽度
	width:50,
	//返回panel宽度
	panelWidth:400,
	hourId:'bottles',
	minuteId:'bottles1',
	//1  全部       2  只需要时       3  只需要分钟
	flag:1,
	//小时下拉框样式
	hourStyle:'',
	minuteStyle:'',
	panelStyle:'',
	//返回panel
	panel:'',
	
	constructor:function(config){
		this.callParent(arguments);
		me = this;
		me.width = Ext.isEmpty(config.width) ? me.width : config.width;
		me.hourId = Ext.isEmpty(config.hourId) ? me.hourId : config.hourId;
		me.minuteId = Ext.isEmpty(config.minuteId) ? me.minuteId : config.minuteId;
		me.flag = Ext.isEmpty(config.flag) ? me.flag : config.flag;
		me.hourStyle = Ext.isEmpty(config.hourStyle) ? me.hourStyle : config.hourStyle;
		me.minuteStyle = Ext.isEmpty(config.minuteStyle) ? me.minuteStyle : config.minuteStyle;
		me.panelWidth = Ext.isEmpty(config.panelWidth) ? me.panelWidth : config.panelWidth;
		me.panelStyle = Ext.isEmpty(config.panelStyle) ? me.panelStyle : config.panelStyle;
		
		me.init();
	},
	init:function(){
		var map = new HashMap();
		Rpc({functionId:'ZP0000002560',async:false,success:me.createHourMinu},map);
	},
	//时分（下拉框）
	createHourMinu:function(response){
		var result = Ext.decode(response.responseText);
		var times = Ext.decode(result.times);
		var items;
		
		if(me.flag == 1)
			items = [me.generateSelector(times.hour,me.hourId,me.hourStyle,"hour"),
						{html:'：',border:false},
						me.generateSelector(times.minute,me.minuteId,me.minuteStyle,"minute")];
		else if(me.flag == 2)
			items = [me.generateSelector(times.hour,me.hourId,me.hourStyle,"hour")];
		else if(me.flag == 3)
			items = [me.generateSelector(times.minute,me.minuteId,me.minuteStyle,"minute")]
			         
		
		me.panel =  Ext.create('Ext.panel.Panel',{
			width:me.panelWidth,
			border:false,
			layout:'table',
			style:me.panelStyle,
			items:items
		});
	},
	//时分下拉框           jsonStr 需要展示的数据        id   下拉框id     style   下拉框样式        hOrM  标志是时还是分（用来赋值默认值）
	generateSelector:function(jsonStr,id,style,hOrM){
		var store = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			data:jsonStr
		});
		return Ext.create('Ext.form.ComboBox', {
			fieldLabel:'',
	  		width:me.width,
	  		id:id,
	  		fieldStyle:'padding-top:2px',
	  		style:style,
	  		allowBlank:false,
	  		blankText : '时间不能为空',
	  	    store: store,
	  	    autoSelect:true,
	  	    queryMode: 'local',
	  	   	displayField: 'dataName',
	  	    valueField: 'dataValue',
	  	    labelPad:0,
	  	    labelAlign:'right',
	  	    listeners:{
		   		blur:{
					fn:function(combox){
						var res = store.find('dataValue',combox.getValue());
						if(res==-1){//无效输入值
							combox.setValue("");
						}
					}
				},
				//收窄后有阴影，暂时不用
//				expand:{
//					fn:function(field){
//						Ext.getDom(field.getPicker().getId()).style.width = me.width+"px";
//						Ext.getCmp(field.getPicker().getId()).shadow = false;
//					}
//				},
				beforerender:{
					fn:function(box){
						if(hOrM == "hour")
							box.setValue("09");
						else
							box.setValue("00");
					}
				}
	  		}
	  	});
	},
	getPanel:function(){
		return me.panel;
	},
	hide:function(){
		Ext.getCmp(me.hourId).setFieldStyle("border:0px;text-align:center;padding-top:5px");
		Ext.getCmp(me.hourId).readOnly = true;
		Ext.getCmp(me.minuteId).setFieldStyle("border:0px;text-align:center;padding-top:5px");
		Ext.getCmp(me.minuteId).readOnly = true;
	}
});