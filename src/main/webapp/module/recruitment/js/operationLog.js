Ext.define("OperationLogUL.operationLog",{
    extend:'Ext.window.Window',
    xtype:'operationLog',
    me:'',
    config:{
    	a0100s:undefined,
    	status:undefined,
    	link_id:undefined,
    	position_id:undefined,
    	function_str:undefined,
    	now_linkId:undefined,
    	fn:undefined,
    	msgType:undefined
    },
    initComponent:function(){
    	me = this;
    	this.init();
    },
    init:function(){
    	var title ='提示信息';
    	if(me.msgType && me.msgType=="1")
    		title ='推荐意见';
    	
    	var win = Ext.widget('window',{
    		title: title,
    		width:300,
    		modal:true,
    		items:{
    			xtype:'textarea',
    			id:'operationID',
    			width:'100%',
    			height:200,
    			rows:3,
    			emptyText:'请填写意见'
    		},
    		buttonAlign:'center',
    		buttons: [
    		          {
    		        	  text: '确定',
    		        	  handler:function(){
    		        		  var map = new HashMap();
    		        		  map.put("a0100", me.a0100s);
    		        		  map.put("node_id", me.status);
    		        		  map.put("link_id", me.link_id);
    		        		  map.put("z0301", me.position_id);
    		        		  map.put("description", Ext.getCmp('operationID').getValue());
    		        		  map.put("function_str", me.function_str);
    		        		  map.put("now_linkId", me.now_linkId);
    		        		  Rpc({asynchronous:true,functionId : 'ZP0000002004',success:function(){
    		        			  win.close();
    		        			  Ext.callback(me.fn,this,['ok']);
    		        		  }},map);
    		        	  }
    		          },
    		          {
    		        	  text: '取消',
			        	  handler:function(){
			        		  win.close();
			        	  }
    		          }
    		        ]
    	}).show();
    }
});