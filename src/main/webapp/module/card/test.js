
Ext.define("SearchCardsURL.test",{
	requires:['Card.SearchCards'],
	extend:'Ext.window.Window',
	width:1200,
	height:600,
	initComponent:function(){
		this.callParent();
		this.createWindow();
	},createWindow:function(){
		var cardFormProty={
			    'fieldpurv':'0',//指标权限是否按管理范围 0 否 1 是
			    'a0100':'',
			    'bizDate':'',//业务日期
			    'tabid':'',
			    'inforkind':'1',//模块类型
			    'plan_id':'',//绩效所需参数
			    'temp_id':'',//绩效所需参数
			    'Callbackfunc':'test_me.ss',
			    'isFitFlag':false
			 };
		Ext.apply(cardGlobalBeanDefault,cardFormProty);
		var panel=Ext.create("Card.SearchCards",{cardFormProty:cardGlobalBeanDefault});
		this.add(panel);
	},ss:function(){
		alert(22);
	}
});