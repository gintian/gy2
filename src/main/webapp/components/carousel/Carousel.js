Ext.define("EHR.carousel.Carousel",{
	xtype:'carousel',
	extend:'Ext.panel.Panel',
	iconBase:'/components/carousel/images/',
	scrollable:{
		y:false,
		x:false
    	},
    	activeItemIndex:0,
    	bodyStyle:'border:none;',
	initComponent:function(){
		this.resizable = false;
		this.title = false;
		
		var nextIcon = this.iconBase+"nextPage.png";
		if(this.items.length<2)
			nextIcon = this.iconBase+"nextPage-disable.png";
		
		this.dockedItems=[{
			xtype:'component',width:40,dock:'left',
			html:"<div func='pre' style='height:100%;width:100%;background:url("+this.iconBase+"prevPage-disable.png) center center no-repeat;'></div>"
			
		},{
			xtype:'component',width:40,dock:'right',
			html:"<div func='nxt' style='height:100%;width:100%;background:url("+nextIcon+") center center no-repeat;'></div>"
			
		}];
		
		this.layout={
			type:'hbox',
			align:'stretch'
		};
		for(var i=0;i<this.items.length;i++){
			this.items[i].width='100%';
		}
		this.callParent();
		
		this.on({
			click:this.changeView,
			element:'el',
			delegate:'div[func]',
			scope:this
		});
	},
	changeView:function(evt,ele){
		var func = ele.getAttribute("func");
		
		if(func=='pre')
			this.pre();
		else
			this.next();
	},
	pre:function(){
		if(this.activeItemIndex==0)
			return;
		
		if(this.activeItemIndex==this.items.getCount()-1)
			this.el.query('div[func=nxt]')[0].style.backgroundImage = "url("+this.iconBase+"nextPage.png)";
	
		this.activeItemIndex--;
		
		var firstChild = this.items.getAt(0);
		var currentChild = this.items.getAt(this.activeItemIndex);
		var po = currentChild.el.getOffsetsTo(firstChild.el.dom);
		this.getScrollable().scrollTo(po[0],0,{duration:500});
		
		if(this.activeItemIndex==0)
			this.el.query('div[func=pre]')[0].style.backgroundImage = "url("+this.iconBase+"prevPage-disable.png)";
	},
	next:function(){
		if(this.activeItemIndex==this.items.getCount()-1)
			return;
		
		if(this.activeItemIndex==0)
			this.el.query('div[func=pre]')[0].style.backgroundImage = "url("+this.iconBase+"prevPage.png)";
			
	
		this.activeItemIndex++;
		
		var firstChild = this.items.getAt(0);
		var currentChild = this.items.getAt(this.activeItemIndex);
		var po = currentChild.el.getOffsetsTo(firstChild.el.dom);
		this.getScrollable().scrollTo(po[0],0,{duration:500});
		
		if(this.activeItemIndex==this.items.getCount()-1){
			this.el.query('div[func=nxt]')[0].style.backgroundImage = "url("+this.iconBase+"nextPage-disable.png)";
		}
	}

});