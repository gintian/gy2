Ext.define('ResumeLog.showResumeLog',{
	config:{
		positionid:undefined,//职位id
		a0100:undefined,
		nbase:undefined
	},
	constructor:function(config){
		resumeLog = this;
		resumeLog.config = config;
		this.init();
	},
	init:function(){
		var map = new HashMap();
		map.put("positionid", resumeLog.config.positionid);
		map.put("a0100", resumeLog.config.a0100);
		map.put("nbase", resumeLog.config.nbase);
	    Rpc({functionId:'ZP0000002380',async:false,success:resumeLog.generatePage},map);
	},
	//生成页面
	generatePage:function(response){
		var result = Ext.decode(response.responseText);
		var json = Ext.decode(result.jsonStr);
		
		if(Ext.isEmpty(json[0]))
			return;
		var items = new Array();
		Ext.each(json,function(obj,index){
			items[index] = resumeLog.createEachPanel(obj);
		});
		if(items.length == 0)
			return;
		
		Ext.create('Ext.container.Viewport',{
			padding:"0 5 0 5",
			layout:'fit',
			id:"resumePanel_viewport",
			items:{
				xtype:'panel',
				id:'resumePanel_viewport_panel',
				border:false,
				layout:'vbox',
				scrollable:true,
				items:items
			},
			autoDestroy:true,
			listeners:{
				//处理多层代码筛选后自适应页面的问题
				resize:{
					fn:function(port,width,height,oldWidth,oldHeight){
						
					}
				}
			}
	    });
	},
	//创建每个记录panel
	createEachPanel:function(obj){
		return Ext.widget('panel',{
			border:false,
			//style:'margin-top:10px;',
			width:'90%',
			height:100,
			html:obj.html,
			items:[
			       {
			    	   xtype:'textfield',
			    	   name:'logid',
			    	   value:obj.id,
			    	   hidden:true,
			    	   fieldLabel:null
			    	}
			       ]
		});
	}
});