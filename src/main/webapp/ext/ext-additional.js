//修改ext默认图标src。防止连接www.sencha.com
Ext.BLANK_IMAGE_URL='data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';

//window控件一些公共的属性，为了统一样式
Ext.define('Ext.additional.window.Window',{
	override:'Ext.window.Window',
	constrain:true,
	border:false,
	shadow:false,
	bodyStyle:"background-color:white;",
	resizable:true
});

//解决Ext.form.field.Picker 弹出的picker内文本框不能获取焦点的问题
Ext.define("Ext.additional.form.field.Picker",{
	override:"Ext.form.field.Picker",
	mimicBlur: function(e) {
        var me = this,
            picker = me.picker;

        if (!picker || !me.owns(e.target)) {
            me.callParent(arguments);
        }
    }
});

//Ext提示弹框添加警告图标
Ext.define("Ext.additional.window.MessageBox",{
	override:"Ext.window.MessageBox",
	alert: function(cfg, msg, fn, scope) {
        if (Ext.isString(cfg)) {
            cfg = {
                title : cfg,
                msg : msg,
                icon:this.INFO,
                buttons: this.OK,
                fn: fn,
                scope : scope,
                minWidth: this.minWidth
            };
        }
        return this.show(cfg);
    }
});

// sting 获取字节长度（中文按2个长度计算）
Ext.getStringByteLength = function(value){
	var len = value.replace(/[^\x00-\xff]/gi, "--").length;
	return len;
};