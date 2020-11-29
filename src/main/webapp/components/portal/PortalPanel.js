Ext.define('EHR.portal.PortalPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.portalpanel',

    requires: [
        'Ext.layout.container.Column',
        'EHR.portal.plugin.PortalDropZone'
    ],
    autoScroll: true,
    manageHeight: false,
    initComponent : function() {
        var me = this;

        this.layout = {
            type : 'column'
        };
        this.callParent();

    },

    beforeLayout: function() {
    		return this.callParent(arguments);
    },

    initEvents : function(){
        this.callParent();
        this.dd = Ext.create('EHR.portal.plugin.PortalDropZone', this, this.dropConfig);
    },

    beforeDestroy : function() {
        if (this.dd) {
            this.dd.unreg();
        }
        this.callParent();
    }
});
