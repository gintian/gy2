Ext.Date.monthNames = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'];
Ext.MessageBox.OKCANCEL= [{text:'取消'},{text:'确定'}];
Ext.MessageBox.OK.text = '确定';
Ext.MessageBox.YES.text = '是';
Ext.MessageBox.NO.text = '否';
Ext.MessageBox.CANCEL.text = '取消';
Ext.Loader.setConfig({
	scriptCharset:'UTF-8'
});
Ext.application({
	name:'Questionnaire',
	appFloder:'/module/system/questionnaire/mobile',
	controllers:['Main'],
	views:['Main','CodeSelectField','SuperDateTimePickerField','MobileLogin'],
	launch:function(){
		mainview = Ext.create('Questionnaire.view.Main');
		Ext.Viewport.add(mainview);
	}
});
