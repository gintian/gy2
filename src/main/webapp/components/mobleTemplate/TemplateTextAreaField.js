/**
 * textareafield
 * 解决手机端拖动大文本，屏幕也跟着动的问题
 */
Ext.define('EHR.mobleTemplate.TemplateTextAreaField',{
	extend:'Ext.field.TextArea',
	xtype:'templatetextareafield',
	config:{
		readOnly:false
	},
	initialize:function(){
		var me = this;
		me.callParent();
		var textarea = me.element.dom.getElementsByTagName("textarea")[0];
		textarea.addEventListener('touchstart',function(event) {
			event.stopPropagation();
		}, false);
	},
	onFocus:function(){
		if(this.getReadOnly()){
		    return false;
		}
		this.callParent();
	},
	onBlur:function(){
		window.scrollTo(0,0);
		this.callParent();
	}
});