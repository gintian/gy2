/**
*审批人角色信息?允?  hej 2016-5-20
*
**/
Ext.define('TemplateNavigationOther.ShowChangeLogSubInfo',{
	top:true,
	left:true,
	constructor:function(config){
		ChangeLogSubInfo_me = this;
    	ChangeLogSubInfo_me.element = config.element;
    	ChangeLogSubInfo_me.subConetnt = config.subcontent;
    	ChangeLogSubInfo_me.init();
	},
	init:function(){
		var left =ChangeLogSubInfo_me.getElementLeft(ChangeLogSubInfo_me.element);
    	var top =ChangeLogSubInfo_me.getElementTop(ChangeLogSubInfo_me.element)+ChangeLogSubInfo_me.element.offsetHeight-50;
    	var display = Ext.getCmp('changeLogDisplay');
    	if(display){
    		display.destroy();
    	}
		var win = Ext.widget('container',{
				id:'changeLogDisplay',
				x:left,
				y:top,
				layout:'hbox',
				border:0,
				floating : true,
				height:300,
				width:400,
				bodyStyle:'opacity:0;filter:alpha(opacity=0)',
				shadow : false,
				closeAction : "destroy",
				//autoScroll : true,
				listeners:{
					render:function(){
						this.mon(Ext.getDoc(), {
			                mousewheel: this.hiddenIf,
			                mousedown: this.hiddenIf,
			                scope: this
			            });
					}
                },
                
				hiddenIf: function(e) {
		        	var me = this;
		        		if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target)) {
		        			me.destroy();
		       			}
		    	}
            }).show();
            var list=null;
            if(ChangeLogSubInfo_me.subConetnt.length>0)
            	list=JSON.parse(ChangeLogSubInfo_me.subConetnt);
            var infohtml="<table border='0'  cellspacing='0' style='font-size:12px;' cellpadding='0' width='100%' >";
            var line=0;
            infohtml+="<tr>";
            for(var p in list){
                line++;
                if(line!=0&&line%2==0){
                    infohtml+="</tr><tr>";
                }
                infohtml+="<td style='width:50%'>"+p+":"+list[p]+"</td>";
            }
            infohtml+="</tr>";
            infohtml+="</table>";
            var apPanel = Ext.widget('container',{
				margin:'5 0 0 15',
				height:100,
				autoScroll:true,
				html:infohtml
			})
			if(ChangeLogSubInfo_me.left==true){
				win.add([{xtype:'image',margin:ChangeLogSubInfo_me.top==true?'35 0 0 -1':'215 0 0 -1',src:'/images/new_module/leftrole.png'}
,{xtype:'panel',width:383,items:[apPanel]}])
            }
	},
	getElementLeft:function(element){
		var width = document.body.clientWidth;
		var actualLeft= element.getBoundingClientRect().right;
		var elementScrollLeft=0;
		if (document.compatMode == "BackCompat"){
			elementScrollLeft=document.body.scrollLeft;
		} else {
	　　　　        elementScrollLeft=document.documentElement.scrollLeft; 
	　　   }
	    actualLeft = actualLeft+elementScrollLeft;
	    /*if(actualLeft<400){
	    	ChangeLogSubInfo_me.left=false;
	    	actualLeft = actualLeft+490;
	    }*/
	    return actualLeft;
	},
	getElementTop:function(element){
		var height = document.body.clientHeight;
		var actualTop =element.getBoundingClientRect().top;
		if (document.compatMode == "BackCompat"){
　　　　　　  var elementScrollTop=document.body.scrollTop;
　　　　  } else {
　　　　　　  var elementScrollTop=document.documentElement.scrollTop; 
　　　　  }
		actualTop = actualTop+elementScrollTop;
		/*if(actualTop+300>height){
			ChangeLogSubInfo_me.top=false;
			actualTop = actualTop-180;
		}*/
	    return actualTop;
	}
})