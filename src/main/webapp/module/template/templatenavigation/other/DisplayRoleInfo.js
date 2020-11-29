/**
*审批人角色信息显示  hej 2016-5-20
*
**/
Ext.define('TemplateNavigationOther.DisplayRoleInfo',{
	top:true,
	left:true,
	constructor:function(config){
		RoleInfo_me = this;
    	RoleInfo_me.tabid = config.tabid;
    	RoleInfo_me.task_id = config.task_id;
    	RoleInfo_me.element = config.element;
    	RoleInfo_me.flag = config.flag;
    	RoleInfo_me.ins_id = config.ins_id;
    	RoleInfo_me.init();
	},
	init:function(){
		var map = new HashMap();
		map.put("tabid",RoleInfo_me.tabid+'');
		map.put("taskid",RoleInfo_me.task_id+'');
		map.put("flag",RoleInfo_me.flag);
		map.put("ins_id",RoleInfo_me.ins_id);
		var left =RoleInfo_me.getElementLeft(RoleInfo_me.element)+26-400-26;
    	var top =RoleInfo_me.getElementTop(RoleInfo_me.element)+RoleInfo_me.element.offsetHeight-50;
    	var display = Ext.getCmp('display');
    	if(display){
    		display.destroy();
    	}
		var win = Ext.widget('container',{
				id:'display',
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
		var myMask = new Ext.LoadMask({
			id:'myGridMask',
  	 		msg: '正在加载数据,请稍候...',
   			target : win,
   			//style:'background-color:#ffffff',
   			autoShow:true
		});
		myMask.show();
		Rpc({functionId:'MB00006013',success:function(form,action){
			var resultObj = Ext.decode(form.responseText);
			var approvePeople = resultObj.approvePeople;
			var approveContent = resultObj.approveContent;
			var html = '';
			var apPaneltop = Ext.widget('panel',{
				border:0,
				bodyStyle: 'background-color:#F5F5F5',
   		    	height:32,layout:{type:'vbox',align:'left'},
   		    	items:[{xtype:'label',margin:'5 0 0 5',text:'审批人',width:45,style:'font-size:13px;font-weight:bold'}]
			});
			var apPanel = Ext.widget('container',{
				margin:'5 0 0 15',
				height:100,
				autoScroll:true,
				html:getDecodeStr(approvePeople)
			})
			var acPanelbottom = Ext.widget('panel',{
				border:0,
				bodyStyle: 'background-color:#F5F5F5',
   		    	height:32,layout:{type:'vbox',align:'left'},
   		    	items:[{xtype:'label',margin:'5 0 0 5',text:'审批意见',width:57,style:'font-size:13px;font-weight:bold'}]
			});
			var acPanel = Ext.widget('container',{
				margin:'5 0 0 15',
				height:100,
				autoScroll:true,
				html:getDecodeStr(approveContent)
			})

			if(RoleInfo_me.left==true){
				win.add([{xtype:'panel',width:383,items:[apPaneltop,apPanel,acPanelbottom,acPanel]},
					{xtype:'image',margin:RoleInfo_me.top==true?'35 0 0 -1':'215 0 0 -1',src:'/images/new_module/rightrole.png'}])
			}/*else{
				win.add([{xtype:'image',margin:RoleInfo_me.top==true?'35 0 0 2':'215 0 0 2',src:'/images/new_module/leftrole.png',style:'z-index:100;'},
				{xtype:'panel',width:383,items:[apPaneltop,apPanel,acPanelbottom,acPanel]}])
			}*/
			
			Ext.getCmp("myGridMask").destroy();
	    },scope:this},map);
	},
	getElementLeft:function(element){
		var width = document.body.clientWidth;
		var actualLeft= element.getBoundingClientRect().left;
		if (document.compatMode == "BackCompat"){
　　　　　　  var elementScrollLeft=document.body.scrollLeft;
	　　 } else {
	　　　　 var elementScrollLeft=document.documentElement.scrollLeft; 
	　　 }
	    actualLeft = actualLeft+elementScrollLeft;
	    /*if(actualLeft<400){
	    	RoleInfo_me.left=false;
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
		if(actualTop+300>height){
			RoleInfo_me.top=false;
			actualTop = actualTop-180;
		}
	    return actualTop;
	}
})