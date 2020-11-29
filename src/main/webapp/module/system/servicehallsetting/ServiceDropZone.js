Ext.define('ServiceHall.ServiceDropZone', {
    extend: 'Ext.dd.DropTarget',
    constructor: function(servicePanel, cfg) {
        this.portal = servicePanel;
        ServiceHall.ServiceDropZone.superclass.constructor.call(this, servicePanel.body, cfg);
    },
    notifyOver: function(dd, e, data) {
    
    	var me = this,
            xy = e.getXY(),
            portal = me.portal,
            proxy = dd.proxy,
            panelProxy = dd.panelProxy,
            x,y,width,height;

         var spacer = panelProxy.proxy;
         x = spacer.getX();
         y = spacer.getY();
         width = spacer.getWidth();
         height = spacer.getHeight();
         if(x<=xy[0] && y<=xy[1] && x+width>=xy[0] && y+height>=xy[1]){
         	return;
         }
         
         var goBack = false;
         if(y+height<xy[1] || (y<xy[1] && x+width<xy[0]))
         	goBack = true;
            
         var overPanel;
         for(var i=0;i<portal.items.items.length;i++){
         
         	var pp = portal.items.items[i];
         	var x = pp.el.getX();
         	var y = pp.el.getY();
         	var width = pp.el.getWidth();
         	var height = pp.el.getHeight();
         	
         	if(!pp.draggable)
         		continue;
         	if(x<=xy[0] && y<=xy[1] && x+width>=xy[0] && y+height>=xy[1]){
         		overPanel = pp;
         		break;
         	}
         }   
         if(overPanel){
         	if(goBack && panelProxy.proxy){
         		panelProxy.proxy.insertAfter(overPanel.el.dom);
         	}else{
        		panelProxy.moveProxy(dd.panel.el.dom.parentNode, overPanel.el.dom);
        	}
         }else{
         	var lastCmp = portal.items.items[portal.items.items.length-1];
         	panelProxy.moveProxy(dd.panel.el.dom.parentNode,lastCmp.el.dom);
         }
         this.beMoved = true;
    },

    notifyDrop: function(dd, e, data) {
    	if(!this.beMoved)
    		return false;
    	var items = dd.panel.el.dom.parentNode.childNodes;
    	var index = 0;
    	for(var i=0;i<items.length;i++){
    		if(items[i].style.display=='none')
    			index = -1;
    		if(dd.panelProxy.proxy.dom === items[i]){
    			index +=i;
    			break;
    		}
    	}
        dd.panelProxy.hide();
        dd.proxy.hide();
        if(index!=-1){
        	this.portal.insert(index,dd.panel);
        }else
        	this.portal.add(dd.panel);
        
        delete this.beMoved;
        
        this.portal.fireEvent('drop',this.portal);
        return true;
    },


    // unregister the dropzone from ScrollManager
    unreg: function() {
        Ext.dd.ScrollManager.unregister(this.portal.body);
        ServiceHall.ServiceDropZone.superclass.unreg.call(this);
        delete this.portal.afterLayout;
    }
});
