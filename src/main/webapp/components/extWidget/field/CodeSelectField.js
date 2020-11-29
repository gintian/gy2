Ext.define('EHR.extWidget.field.CodeSelectField',{
	extend:'Ext.form.field.ComboBox',
	xtype:'codeselectfield',
	editable: false,
	onTriggerClick: function() {
        var me = this;
        if (!me.readOnly && !me.disabled) {
            if (me.isExpanded) {
                me.collapse();
            } else {
            	me.expand();
            }
            me.inputEl.focus();
        }
    },
	setValue: function(value) {
		var me = this;

		if(value &&  typeof(value)=="string" && value.indexOf("`")>-1){
			me.value = value.split("`")[0];
			me.setRawValue(value.split("`")[1]);
			value = me.value;
		}

        // Value needs matching and record(s) need selecting.
        if (value != null) {
            return me.doSetValue(value);
        }
        // Clearing is a special, simpler case.
        else {
            me.suspendEvent('select');
            me.valueCollection.beginUpdate();
            me.pickerSelectionModel.deselectAll();
            me.valueCollection.endUpdate();
            me.lastSelectedRecords = null;
            me.resumeEvent('select');
        }
        
    },
    getValue: function() {
    	if(!this.value)
    		return "";
 
        return this.value+"`"+this.rawValue;
    },
    defaultRenderer: function(value){
    	if(value){
           return value.split('`')[1];
    	}
    } 
});