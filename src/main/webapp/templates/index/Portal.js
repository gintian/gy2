/*
 * Ext JS 4.2   zhaoxg 2014-6-7
 */


Ext.define('Ext.ux.Portal', {
	extend: 'Ext.panel.Panel',
  
	xtype:'portal',
	alias: 'widget.portal', 
	layout: 'column',
    autoScroll:true,
    //overflowY:'hidden',
    cls:'x-portal',
    defaultType: 'portalcolumn',
    
    initComponent: function () { 
        var me = this;  
        this.layout = { 
            type: 'column' 
        };  
        this.callParent(); 
  
	        this.addEvents({ 
            validatedrop: true, 
            beforedragover: true, 
            dragover: true, 
            beforedrop: true, 
            drop: true 
        });  
        this.on('drop', this.doLayout, this); 
    }, 
    beforeLayout: function () {  
        var items = this.layout.getLayoutItems(), 
            len = items.length, 
            i = 0, 
            item; 
  
        for (; i < len; i++) {  
            item = items[i];  
            item.columnWidth = 1 / len;  
            item.removeCls(['x-portal-column-first', 'x-portal-column-last']);         }  
        items[0].addCls('x-portal-column-first'); 
        items[len - 1].addCls('x-portal-column-last'); 
        return this.callParent(arguments); 
    }, 

    initEvents: function () { 
		
        this.callParent();  
        this.dd = Ext.create('Ext.ux.Portal.DropZone', this, this.dropConfig); 

    },  
    beforeDestroy: function () { 
        if (this.dd) { 
            this.dd.unreg(); 
        }  
        this.callParent(); 
    } 


});



Ext.define('Ext.ux.Portal.DropZone', {
	extend : 'Ext.dd.DropTarget',

    constructor: function(portal, cfg) { 
        this.portal = portal;  
        Ext.dd.ScrollManager.register(portal.body);  
        Ext.ux.Portal.DropZone.superclass.constructor.call(this, portal.body,  cfg);  
        portal.body.ddScrollConfig = this.ddScrollConfig; 
    }, 

    ddScrollConfig: { 
        vthresh: 50, 
        hthresh: -1, 
        animate: true, 
        increment: 200 
    }, 

    createEvent: function(dd, e, data, col, c, pos) { 
       return {  
            portal: this.portal, 
            panel: data.panel, 
            columnIndex: col, 
            column: c, 
            position: pos, 
            data: data, 
            source: dd, 
            rawEvent: e,  
            status: this.dropAllowed 
        }; 
    }, 

    notifyOver: function(dd, e, data) { 
        var xy = e.getXY(), 
            portal = this.portal,  
            proxy = dd.proxy;  
        if (!this.grid) {  
            this.grid = this.getGrid(); 
        } 
  
        
 
        var cw = portal.body.dom.clientWidth; 
        if(!this.lastCW) {               
            this.lastCW = cw;  
        } else if(this.lastCW != cw) {              
            this.lastCW = cw; 
            this.grid = this.getGrid(); 
        } 
  
        var colIndex = 0, 
            colRight = 0,  
            cols = this.grid.columnX, 
            len = cols.length, 
            cmatch = false; 
  
        for(len; colIndex < len; colIndex++) {  
            colRight = cols[colIndex].x + cols[colIndex].w; 
            if(xy[0] < colRight) { 
                cmatch = true; 
                break; 
            } 
        }  
        
 
        if(!cmatch) { 
            colIndex--; 
        } 
  

 
        var overPortlet, pos = 0, 
            h = 0,  
            match = false,  
            overColumn = portal.items.getAt(colIndex), 
            portlets = overColumn.items.items,  
            overSelf = false; 
  
        len = portlets.length; 
  
        for(len; pos < len; pos++) { 
            overPortlet = portlets[pos]; 
            h = overPortlet.el.getHeight(); 
            if(h === 0) { 
                overSelf = true;  
            } else if((overPortlet.el.getY() + (h / 2)) > xy[1]) { 
                match = true; 
                break; 
            } 
        } 
  
        pos = (match && overPortlet ? pos : overColumn.items.getCount()) + (overSelf ? -1 : 0);  
        var overEvent = this.createEvent(dd, e, data, colIndex, overColumn, pos); 
  
        if(portal.fireEvent('validatedrop',  overEvent)!==  false&&  portal.fireEvent('beforedragover', overEvent) !== false) {  
   
            proxy.getProxy().setWidth('auto'); 
            if(overPortlet) {  
                dd.panelProxy.moveProxy(overPortlet.el.dom.parentNode, match  ?  overPortlet.el.dom : null); 
            } else {  
                dd.panelProxy.moveProxy(overColumn.el.dom, null); 
            } 
  
            this.lastPos = { 
                c: overColumn, 
                col: colIndex,  
                p: overSelf || (match && overPortlet) ? pos : false 
            };  
            this.scrollPos = portal.body.getScroll(); 
  
            portal.fireEvent('dragover', overEvent); 
            return overEvent.status; 
        } else{  
            return overEvent.status; 
        }  
    }, 
  
    notifyOut : function(){
        delete this.grid;
    },

 notifyDrop: function (dd, e, data) { 
	 
        delete this.grid; 
        if(!this.lastPos) { 
            return; 
        }  
        var c = this.lastPos.c, 
            col = this.lastPos.col, 
            pos = this.lastPos.p, 
            panel = dd.panel,  
            dropEvent = this.createEvent(dd, e, data, col, c, pos !== false? pos : c.items.getCount()); 

          Ext.suspendLayouts(); 
  
        if(this.portal.fireEvent('validatedrop',  dropEvent)  !==  false&&  this.portal.fireEvent('beforedrop', dropEvent) !== false) {  
              
            panel.el.dom.style.display = ''; 
  
            if(pos !== false) { 
                c.insert(pos, panel); 
            } else{  
                c.add(panel); 
            } 
			
            dd.proxy.hide();  
            this.portal.fireEvent('drop', dropEvent); 
 
            var st = this.scrollPos.top; 
            if (st){  
                var d = this.portal.body.dom; 
                setTimeout(function(){ 
                    d.scrollTop = st; 
                },10);  
            }
              
            var result = []; 
            var items = viiewportal.items.get(0).items; 
            var cols=items.getCount()+"";  
	        for (var i = 0; i < cols; i++) {   
	            var c = items.get(i);   
	            c.items.each(function(portlet) {   
	                        var o = {   
	                            id : portlet.getId(),   
	                            col : i+""   
	                        };   
	                        result.push(o); ;   
	                    });   
	        }  
	        var map = new HashMap();
            map.put("result",result);
        	map.put("cols",cols);
        	map.put("portalid",portalid);
       		Rpc({functionId:'1010010069',success:null},map);
        } 
        Ext.resumeLayouts(true); 
        delete this.lastPos; 
    }, 

    getGrid: function() {  
        var box = this.portal.body.getBox(); 
        box.columnX = [];  

        this.portal.items.each(function (c) { 
            box.columnX.push({ 
                x: c.el.getX(), 
                w: c.el.getWidth() 
            }); 
        });  
        return box; 
    }, 
	unreg: function () {  
        Ext.dd.ScrollManager.unregister(this.portal.body);  
        Ext.ux.Portal.DropZone.superclass.unreg.call(this); 
    } 
});

