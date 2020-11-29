Ext.define('SYSP.CodeSelectField',{
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
	setValue: function(valueObj, doSelect) {
		var value = valueObj;
		var me = this;
		
		if(valueObj &&  valueObj instanceof String && valueObj.indexOf("`")>-1){
			me.value = valueObj.split("`")[0];
			me.setRawValue(valueObj.split("`")[1]);
			return me;
		}
		
        var valueNotFoundText = me.valueNotFoundText,
        inputEl = me.inputEl,
        i, len, record,
        dataObj,
        matchedRecords = [],
        displayTplData = [],
        processedValue = [];

	    if (me.store.loading) {
	        // Called while the Store is loading. Ensure it is processed by the onLoad method.
	        me.value = value;
	        me.setHiddenValue(me.value);
	        return me;
	    }
	
	    // This method processes multi-values, so ensure value is an array.
	    value = Ext.Array.from(value);
	
	    // Loop through values, matching each from the Store, and collecting matched records
	    for (i = 0, len = value.length; i < len; i++) {
	        record = value[i];
	        if (!record || !record.isModel) {
	            record = me.findRecordByValue(record);
	        }
	        // record found, select it.
	        if (record) {
	            matchedRecords.push(record);
	            displayTplData.push(record.data);
	            processedValue.push(record.get(me.valueField));
	        }
	        // record was not found, this could happen because
	        // store is not loaded or they set a value not in the store
	        else {
	            // If we are allowing insertion of values not represented in the Store, then push the value and
	            // create a fake record data object to push as a display value for use by the displayTpl
	            if (!me.forceSelection) {
	                processedValue.push(value[i]);
	                dataObj = {};
	                dataObj[me.displayField] = value[i];
	                displayTplData.push(dataObj);
	                // TODO: Add config to create new records on selection of a value that has no match in the Store
	            }
	            // Else, if valueNotFoundText is defined, display it, otherwise display nothing for this value
	            else if (Ext.isDefined(valueNotFoundText)) {
	                displayTplData.push(valueNotFoundText);
	            }
	        }
	    }
	
	    // Set the value of this field. If we are multiselecting, then that is an array.
	    me.setHiddenValue(processedValue);
	    me.value = me.multiSelect ? processedValue : processedValue[0];
	    if (!Ext.isDefined(me.value)) {
	        me.value = null;
	    }
	    me.displayTplData = displayTplData; //store for getDisplayValue method
	    me.lastSelection = me.valueModels = matchedRecords;
	
	    if (inputEl && me.emptyText && !Ext.isEmpty(value)) {
	        inputEl.removeCls(me.emptyCls);
	    }
	
	    // Calculate raw value from the collection of Model data
	    me.setRawValue(me.getDisplayValue());
	    me.checkChange();
	
	    if (doSelect !== false) {
	        me.syncSelection();
	    }
	    me.applyEmptyText();
	
	    return me;
    },
    getValue: function() {
        return this.value+"`"+this.rawValue;
    },
    defaultRenderer: function(value){
    	if(value){
           return value.split('`')[1];
    	}
    } 
});