Ext.define('ServiceHall.ServicePanel', {
		requires:['ServiceHall.ServiceDropZone'],
	    extend: 'Ext.panel.Panel',
	    alias: 'widget.servicepanel',
	    requiers:['ServiceHall.ServiceDropZone'],
	    autoScroll: true,
	    manageHeight: false,
	    beforeLayout: function() {
	    	return this.callParent(arguments);
	    },

	    initEvents : function(){
	        this.callParent();
	        var dropConfig = {ddGroup:this.ddGroup};
	        this.dd = Ext.create('ServiceHall.ServiceDropZone', this, dropConfig);
	    },

	    beforeDestroy : function() {
	        if (this.dd) {
	            this.dd.unreg();
	        }
	        this.callParent();
	    }
	});