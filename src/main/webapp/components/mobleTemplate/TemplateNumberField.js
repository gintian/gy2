/**
 * numberfield
 * 解决ios手机出键盘后，触屏定位不准问题
 */
Ext.define('EHR.mobleTemplate.TemplateNumberField',{
	extend:'Ext.field.Number',
	xtype:'templatenumberfield',
	config:{
		readOnly:false
	},
	initialize:function(){
		var me = this;
		me.callParent();
	},
	onBlur:function(){
		window.scrollTo(0, 0);
		this.callParent();
	}
});