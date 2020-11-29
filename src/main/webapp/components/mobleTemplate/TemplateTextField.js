/**
 * textfield
 * 解决ios手机出键盘后，触屏定位不准问题
 */
Ext.define('EHR.mobleTemplate.TemplateTextField',{
	extend:'Ext.field.Text',
	xtype:'templatetextfield',
	config:{
		readOnly:false
	},
	initialize:function(){
		var me = this;
		me.callParent();
	},
    onBlur: function(e) {
        window.scrollTo(0, 0);
        this.callParent();
    },
});