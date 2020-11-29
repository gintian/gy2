Ext.define('TemplateViewProcessUL.TemplateViewProcess',{
	constructor:function(config){
		thisVeiwPro=this;
		thisVeiwPro.tabid=config.tabid; 
		thisVeiwPro.task_id=config.task_id;
		thisVeiwPro.infor_type=config.infor_type;
		thisVeiwPro.return_flag=config.return_flag;
		thisVeiwPro.isDelete = config.isDelete;
		var allNum=config.allNum;
		thisVeiwPro._name="姓名";
		if(thisVeiwPro.infor_type=='2'||thisVeiwPro.infor_type=='3')
			thisVeiwPro._name="名称";
		var map = new HashMap();
		map.put('tabid',thisVeiwPro.tabid);
		map.put('return_flag',thisVeiwPro.return_flag);
		map.put('task_id',thisVeiwPro.task_id);
		map.put('infor_type',thisVeiwPro.infor_type);
		map.put('isDelete',thisVeiwPro.isDelete+"");
		if(allNum){
			map.put('allNum',allNum);
		}
		Rpc({functionId:'MB00005008',async:false,success:thisVeiwPro.init},map);//审批环节查询
		//thisVeiwPro.init();
	},
	init:function(form,action){
		var result=Ext.decode(form.responseText);
		if(!result.succeed){
			var message = result.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
			}
		}
		thisVeiwPro.tableName=result.tableName;
		thisVeiwPro.names = '';
		var namearr = result.a0101s.split(',');
		if(namearr.length>5){//如果多于5个，则最多显示5个
			for(var i=0;i<5;i++){
				thisVeiwPro.names+=namearr[i]+',';
			}
			thisVeiwPro.names = thisVeiwPro.names.substring(0,thisVeiwPro.names.length-1);
		}else{
			thisVeiwPro.names = result.a0101s;
		}
		thisVeiwPro.spData=result.spData;
		//审批环节store
		thisVeiwPro.store=Ext.create('Ext.data.Store',{
			fields:['bs_flag','name','nodename','time','desc'],
			data:thisVeiwPro.spData
		});
		//审批模版信息
		thisVeiwPro.form=new Ext.form.Panel({
			height:40,
			x:0,
			y:0,
	        frame:false,//是否填充画面
	        bodyStyle:'background:#FFFFFF;border-left-style:none;border-top-style:none;border-right-style:none;',//白色
	        border:true,
	        layout:'form',
	        items:[
	  /*          {	
	            	fieldLabel:'模板',
	            	value:thisVeiwPro.tableName,
	            	xtype:'displayfield'
	            	
	            },*/
	            {	
	            	fieldLabel:thisVeiwPro._name,
	            	value:thisVeiwPro.names,
	            	xtype:'displayfield'
	            	//labelAlign:'right'
	            	
	            }]
		});
		//审批环节信息
		thisVeiwPro.panel=new Ext.grid.GridPanel({
			width:590,
		    height:387,
	        bodyStyle:'background:#FFFFFF;',//白色
			border:false,
	        stripeRows:true,//隔行换色
	        forceFit:true,//让每列自动填满表格，可以根据columns中设置的width按比例分配
    		store:thisVeiwPro.store,
    		enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
    		enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
    		enableColumnMove:false,//是否允许拖放列，默认为true
    		enableColumnResize:false,//是否允许改变列宽，默认为true
    		columnLines:true,//是否显示列分割线，默认为false
    		columns:[
    				{	header:'报送类型',
         		        dataIndex:'bs_flag',
         		        width:"6%",
    		        	sortable:false
    		         },
    		         {  
    		         	header:'姓名',
         		        dataIndex:'name',
         		        width:"9%",
    		        	sortable:false
    		         },
    		         {	header:'审批节点',
         		        dataIndex:'nodename',
         		        width:"10%",
    		        	sortable:false
    		         },
    		         {	header:'审批时间',
         		        dataIndex:'time',
         		        width:"12%",
    		        	sortable:false
    		         },
    		         {	header:'审批意见',
         		        dataIndex:'desc',
    		        	sortable:false,
    		        	listeners:{
    		        		click:function(a,b,c,d,e,g){//审批意见点击弹出框 查看全部审批意见
    		        			if(g&&g.data&&g.data.desc){
    		        				var strc=g.data.desc;
    		        				var reg = new RegExp( '<p>' , "g" );
    		        				strc=strc.replace(reg,"<br>");
    		        				Ext.create('Ext.window.Window',{
    		        					title:'审批意见',
    		        					resizable:false,//是否允许改变窗口大小
    		        					width:400,
    		        					height:300,
    		        					html:"<div style='width:390px;height:100%;overflow-y:auto;'>"+strc+"</div>",
    		        					modal:true//模态窗口,窗口遮住的页面不可编辑
    		        				}).show()
    		        			}
    		        		}
    		        	
    		        	}
    		         }],
    		         listeners:{
    		         		render:function(panel){
					    		Ext.create('Ext.tip.ToolTip', {
								    target: panel.getView().id,
								    delegate:"td > div.x-grid-cell-inner",
								    shadow:false,
								    trackMouse: true,
								    renderTo: Ext.getBody(),
								    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
								    listeners: {
								        beforeshow: function updateTipBody(tip) {
								        	    var div = tip.triggerElement;//.childNodes[0];
								        	    if (Ext.isEmpty(div))
								        	    	return false;
									        	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight){
									        		//tip.update("<div style='WORD-BREAK:normal;'>"+div.innerHTML+"</div>");
									        		tip.update("<div style='WORD-BREAK:normal;'>点击查看详细信息</div>");
									        	}else
									        		return false;
								        }
								    }
					    		});  
					    	},
		    				scope:thisVeiwPro
    		         }
		});
		thisVeiwPro.mainPanel=new Ext.Panel({
		    border:true,
		    layout:{xtype:'hbox'},
		    items:[thisVeiwPro.form,thisVeiwPro.panel]
		});
		//创建一个窗口
		   	thisVeiwPro.win=Ext.widget("window",{
		   		  title:thisVeiwPro.tableName,
		          width:600,
		          height:500,
		          resizable:false,//是否允许改变窗口大小
				  modal:true,//模态窗口,窗口遮住的页面不可编辑
				  closable:true,//是否显示关闭按钮
				  closeAction:'destroy',//控制按钮是销毁（destroy）还是隐藏（hide）
				  border: false,
				  plain:true,//true则主体背景透明，false则主体有小差别的背景色，默认为false
		          items: [{
		         		xtype:'panel',
		         		border:false,
						items:[thisVeiwPro.mainPanel],
						buttonAlign:'center',
						buttons:[
					          		{
					          			text:'导出Excel',
					          			handler:function(){
					          				var map=new HashMap();			 
											map.put('tabid',thisVeiwPro.tabid);
											map.put('return_flag',thisVeiwPro.return_flag);
											map.put('task_id',thisVeiwPro.task_id);
											map.put('infor_type',thisVeiwPro.infor_type); 
											Rpc({functionId:'MB00005009',async:false,success:thisVeiwPro.outExcel},map);
				          					thisVeiwPro.win.close();
				          				} 
					          		},          		
					          		{
					          			text:'关闭',
					          			handler:function(){
					          				thisVeiwPro.win.close();
					          			}
					          		}
					           ]
		          }]
		    }); 
		    thisVeiwPro.win.show();
	},
	//导出Execl
	outExcel:function(form,action){
		var result = Ext.decode(form.responseText);
		var fileName=result.excelfile;
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName;
	}
	
});