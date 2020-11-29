Ext.override(Ext.Component,{
    render:function(){
        if(HJSJ_EXT_CLASS_OVERRIDE_CONFIG[Ext.HJSJ_MODULE_ID] && HJSJ_EXT_CLASS_OVERRIDE_CONFIG[Ext.HJSJ_MODULE_ID][this.$className]){
            var afterRender = HJSJ_EXT_CLASS_OVERRIDE_CONFIG[Ext.HJSJ_MODULE_ID][this.$className].afterRender;
            Ext.callback(afterRender,this);
        }
        this.callParent(arguments);
    }
});
Ext.apply(Ext,{
	define : function (className, data, createdFn) {
		if(HJSJ_EXT_CLASS_OVERRIDE_CONFIG[Ext.HJSJ_MODULE_ID] && HJSJ_EXT_CLASS_OVERRIDE_CONFIG[Ext.HJSJ_MODULE_ID][className]){
			  var overrideConfig = HJSJ_EXT_CLASS_OVERRIDE_CONFIG[Ext.HJSJ_MODULE_ID][className].override;
			  Ext.apply(data,overrideConfig);
		  }
		
		var Manager = Ext.ClassManager;
	    Ext.classSystemMonitor && Ext.classSystemMonitor(className, 'ClassManager#define', arguments);
	    
	    if (data.override) {
	        Manager.classState[className] = 20;
	        return Manager.createOverride.apply(Manager, arguments);
	    }

	    Manager.classState[className] = 10;
	    return Manager.create.apply(Manager, arguments);
	}
});

//Ext.define= ;