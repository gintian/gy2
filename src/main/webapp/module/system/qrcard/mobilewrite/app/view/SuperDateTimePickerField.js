Ext.define('QRCard.view.SuperDateTimePickerField',{
	extend:'Ext.field.Text',
	alternateClassName:'superdatetimepickerfield',
	xtype:'superdatetimepickerfield',
	requires:['QRCard.view.SuperDateTimePicker','Ext.DateExtras'],
	config:{
		ui:'select',
 
		picker:true,
         
		clearIcon:false,
		
		destroyPickerOnHide:false,
		
		dateFormat:'Y.m.d H:i',
		
		component:{
			useMask:true 
		}
	},
	
	initialize:function(){
		var me = this,
			component = me.getComponent();
		
		me.callParent();
		
		component.on({
			scope:me,
			masktap:'onMaskTap'
		});
		
		component.doMaskTap = Ext.emptyFn;
		
		if(Ext.browser.is.AndroidStock2){
			component.input.dom.disabled = true;
		}
	},
	
	//syncEmptyCls: Ext.emptyFn,
	/*
	applyValue:function(value){
		if(Ext.isEmpty(value))
			return null;
		if(Ext.isObject(value)){
			//console.log('value is Object');
			return new Date(value.year, value.month - 1, value.day, value.hour, value.minute);
		}
		
		if(!Ext.isDate(value)){
			value = value.replace(/-/g,"/");
			value = value.replace(/\./g,"/");
			//console.log(value.length);
			if(value.length==4){
				value +="/01/01";
			}else if(value.length==6||value.length==7){
				value +="/01";
			}
			//console.log(value);
			var date = new Date(value);
//			console.log(date);
			return date;
		}
		//console.log(value);
		return value;
	},
	
	updateValue:function(newValue, oldValue){
		var me = this,
			picker = me._picker;
		
		if(picker && picker.isPicker){
			picker.setValue(newValue);
		}
		if(newValue !== null){
			me.getComponent().setValue(Ext.Date.format(newValue, me.getDateFormat() || Ext.util.Format.defaultDateFormat));
		} else {
			me.getComponent().setValue('');
		}
		
		if(newValue !== oldValue){
			me.fireEvent('change', me, newValue, oldValue);
		}
	},
	*/
    updateDateFormat:function(newDateFormat, oldDateFormat){
    	var value = this.getValue();
    	if (newDateFormat != oldDateFormat && Ext.isDate(value)) {
    		this.getComponent().setValue(Ext.Date.format(value, newDateFormat || Ext.util.Format.defaultDateFormat));
    	}
    },
    
	getValue: function () {
		/*if (this._picker && this._picker instanceof SelfServiceApp.view.SuperDateTimePicker) {
			//console.log(this._picker.getValue());
			return this._picker.getValue();
		}
		//return this._value;
		var value= this._value;
		return (Ext.isDate(value)) ? Ext.Date.format(value,this.getDateFormat() || Ext.util.Format.defaultDateFormat) : value;*/
		return this.getComponent().getValue();
	},
	
	getFormattedValue:function(format){
		var value = this.getValue();
		//console.log(value);
		return (Ext.isDate(value)) ? Ext.Date.format(value, this.getDateFormat() || Ext.util.Format.defaultDateFormat) : value;
	},
	
	applyPicker:function(picker, pickerInstance){
		if(pickerInstance && pickerInstance.isPicker){
			picker = pickerInstance.setConfig(picker);
		}
		return picker;
    },
    
	getPicker:function(){
		var picker = this._picker,
			value = this.getComponent().getValue();
		//console.log(value);
		if(picker && !picker.isPicker){
			picker = Ext.factory({dateFormat:this.getDateFormat()}, QRCard.view.SuperDateTimePicker);
		}
		if(value){
			picker.setValue(value);
		}
		picker.on({
			scope:this,
			change:'onPickerChange',
			hide:'onPickerHide'
		});
		this._picker = picker;
		return picker;
	},
	
	onMaskTap:function(){
		if(this.getDisabled()){
			return false;
		}
		this.onFocus();
		return false;
	},
	onPickerChange:function(picker, value){
		if(value.indexOf('null')!=-1)
			value = null;
		//this.getComponent().setValue(value);
		this.setValue(value);
	},
	onPickerHide:function(){
		var me = this,
			picker = me.getPicker();
		if(me.getDestroyPickerOnHide() && picker){
			picker.destroy();
			me._picker = me.getInitialConfig().picker || true;
		}
	},
	reset:function(){
		this.setValue(this.originalValue);
	},
	onFocus:function(e){
		var component = this.getComponent();
		this.fireEvent('focus', this, e);
		if(Ext.os.is.Android4){
			component.input.dom.focus();
		}
		component.input.dom.blur();
		if(this.getReadOnly()){
			return false;
		}
		this.isFocused = true;
		this.getPicker().show();
	},
	destroy:function(){
		var picker = this._picker;
		
		if(picker && picker.isPicker){
			picker.destroy();
		}
		this.callParent(arguments);
	}
}, function(){
	this.override({
		getValue:function(format){
			if(format){
				Ext.Logger.deprecate("format argument of the getValue method is deprecated, please use getFormattedValue instead", this);
				return this.getFormattedValue(format);
			}
			return this.callOverridden();
		}
	});
	Ext.deprecateMethod(this, 'getDatePicker', 'getPicker');
});