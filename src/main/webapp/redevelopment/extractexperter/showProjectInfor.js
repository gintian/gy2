/**
 * 项目信息管理主界面调用js
 * @author        chenxg
 */
Ext.define('ShowProjectTemplateUL.showProjectInfor',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	projectInfor:'',
	tableObj:'',
	//选中记录的编号
	projectIds:'',
	constructor:function(config) {
		projectInfor = this;
		projectIds = "";
		experts = "";
		saveinfo=new Array();
		nbases = "";
		z0301s = "";
		searchIds = "";
		examJson = "";
		this.init();
		projectInfor.createSearchPanel();
	},
	// 初始化函数
	init:function() {
		var map = new HashMap();
	    Rpc({functionId:'RE100000001',async:false,success:projectInfor.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		//console.log(conditions);
		var obj = Ext.decode(conditions);
		projectInfor.tableObj = new BuildTableObj(obj);
		// Ext.getCmp('re_project_tablePanel').addListener("celldblclick",projectInfor.onCellClick);
		 Ext.getCmp('re_project_tablePanel').addListener("celldblclick",projectInfor.onCellClick);
	},
	// 初始化函数
	/*init:function() {
		var map = new HashMap();
		projectInfor.tableObj.getMainPanel.on({
		    cellClick: this.onCellClick,
		    scope: this // Important. Ensure "this" is correct during handler execution
		});
	    Rpc({functionId:'RE100000001',async:false,success:projectInfor.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var message = Ext.decode(form.responseText);
		var column = message.column;
		alert(column);
		var data= message.data;
		projectInfor.projstore = Ext.create('Ext.data.Store', {
            
			fields:[{name:'n0301'},{name:'n0302'},{name:'n0303'},{name:'n0304'},{name:'n0305'},{name:'n0306'},{name:'n0307'},
		            {name:'n0308'},{name:'n0309'},{name:'n0310'},{name:'n0311'},{name:'n0312'},{name:'n0313'},{name:'n0314'}
		            ,{name:'n0315'},{name:'n0316'},{name:'n0317'},{name:'n0318'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000001',
			        extraParams:{
			        	'req':'getstore'
			        },
			        reader: {
			            type: 'json',
			            root: 'data'         	
			        }
			},
			autoLoad: true  
		});
		var panel = Ext.create('Ext.grid.Panel', {
		    title: 'Simpsons',
		    store: Ext.data.StoreManager.lookup(projectInfor.projstore),
		    columns:[{"dataIndex":"n0301","header":"项目序号"},{"dataIndex":"n0302","header":"采购项目名称"},{"dataIndex":"n0303","header":"采购项目内容"},{"dataIndex":"n0304","header":"需求部门"},{"dataIndex":"n0305","header":"主管部门"},{"dataIndex":"n0306","header":"评标时间"},{"dataIndex":"n0307","header":"评标地点"},{"dataIndex":"n0308","header":"抽取人"},{"dataIndex":"n0309","header":"抽取时间"},{"dataIndex":"n0310","header":"监督人"},{"dataIndex":"n0311","header":"招标结果"},{"dataIndex":"n0312","header":"招标金额"},{"dataIndex":"n0313","header":"正选人数"},{"dataIndex":"n0314","header":"备选人数"},{"dataIndex":"n0315","header":"所属科别"},{"dataIndex":"n0316","header":"专业1"},{"dataIndex":"n0317","header":"专业2"},{"dataIndex":"n0318","header":"招标时间"},{"dataIndex":"n0319","header":"参与专家"}],
		    width:500,
		    height:600,
		    renderTo: Ext.getBody()
		});
		projectInfor.tableObj=panel;
	   // Ext.getCmp('re_project_tablePanel').addListener("celldblclick",projectInfor.onCellClick);
		projectInfor.tableObj.getMainPanel.on({
		    cellClick: this.onCellClick,
		    scope: this // Important. Ensure "this" is correct during handler execution
		});
	},*/
	//获取选中记录的编号
	getSelectedParams:function(){
		var selectRecords = Ext.getCmp("re_project_tablePanel").getSelectionModel().getSelection();
		if(selectRecords.length<=0){
			Ext.Msg.alert("提示信息","请选择需要操作的记录");
			return false;
		}
		projectIds = "";
		Ext.each(selectRecords,function(rec,index){
			var obj=new Object();
			if(index == selectRecords.length-1){
				projectIds += rec.data.n0301;
				experts += (rec.data.n0311+rec.data.n0312);
			}else{
				projectIds += rec.data.n0301+",";
				experts += (rec.data.n0311+rec.data.n0312+",");
			}
			obj.proj_id=rec.data.n0301;
			obj.result=rec.data.n0311;
			obj.rmb=rec.data.n0312;
			//alert(obj.rmb);
			saveinfo.push(obj);
		});
		return true;
	},
	//删除项目信息
	deleteProject:function(){
		//处理参数
		var tem = projectInfor.getSelectedParams();
		if(!tem)
			return;
/*        if(this.experts!=""){
        Ext.Msg.alert('提示','当前项目已经录入招标结果！');
        experts='';
		return;}*/
		Ext.Msg.confirm("提示信息","确定要项目信息吗？",function(res){
				if(res=="yes"){
					var map = new HashMap();
					map.put("projectIds",projectIds);
					Rpc({functionId:'RE100000003',success:function(form,action){
						var result = Ext.decode(form.responseText);
						var message = result.messages;
						if(message!=null && message!=''){
							Ext.Msg.alert('提示',message);	
						}
						var store = Ext.data.StoreManager.lookup('re_project_dataStore');
    	    			store.reload();
					}},map);
				}
			});
	},
	// 查询控件
	createSearchPanel:function(){
	//	Ext.Msg.alert('eq','eqeq');
		var me = this;
		var map = new HashMap();
		me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			width:400,
			hideQueryScheme:true,
			emptyText:'请输入项目名称、需求部门、主管部门、评标时间和地点、专家姓名...',
			subModuleId:"re_project_00001",
			customParams:map,
			funcId:"RE100000002",
			success:projectInfor.loadTable
		});
		Ext.getCmp('re_project_toolbar').add(me.SearchBox);
	},
	//刷新表格，重新加载数据列表
	loadTable:function(result){
		var messages=result.messages;
		if(messages!=null && messages!=''){
		Ext.Msg.alert('提示',messages);
		}
		var store = Ext.data.StoreManager.lookup('re_project_dataStore');
		store.reload();
	},
	//创建项目
	addProject:function(){
		var deptstore = Ext.create('Ext.data.Store', {
			fields:[{name:'deptdesc'},{name:'deptcode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"dept"
			        },
			        reader: {
			            type: 'json',
			            root: 'deptstr'         	
			        }
			},
			autoLoad: true
		});
		catagoryStore=Ext.create('Ext.data.Store',{
			fields:[{name:'catagorydesc'},{name:'catagorycode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"catagory"
			        },
			        reader: {
			            type: 'json',
			            root: 'categorystr'         	
			        }
			},
			autoLoad: true
			
		});
		nameStore=Ext.create('Ext.data.Store',{
			fields:[{name:'namedesc'},{name:'namecode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"name"
			        },
			        reader: {
			            type: 'json',
			            root: 'namestr'         	
			        }
			},
			autoLoad: true
			
		});
		 specialStore=Ext.create('Ext.data.Store',{
			fields:[{name:'specialdesc'},{name:'specialcode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			       		"req":"special"
			        },
			        reader: {
			            type: 'json',
			            root: 'specialstr'         	
			        }
			},
			autoLoad: true
			
		});
		 Ext.define("charge", {
			    extend: "Ext.data.Model",
			    fields: [{name:'yesno'}]
			});
		 
		 var chargeStore=Ext.create('Ext.data.Store',{
			 model:'charge',
			 data:[{yesno:'是'},{yesno:'否'},{yesno:''}]
		 });
		//符合条件的专家（显示）
		  projectInfor.simpsonsStore1=Ext.create('Ext.data.Store', {
			  //  storeId:'projectInfor.simpsonsStore',
			 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
				proxy:{
				    	type: 'transaction',
				        functionId:'RE100000004',
				        extraParams:{
				        	req:'selexpert'
				        },
				        reader: {
				            type: 'json',
				            root: 'expert'         	
				        }
				},
				autoLoad: true
			});
		  //符合条件的专家（调用计算）
		  projectInfor.simpsonsStore=Ext.create('Ext.data.Store', {
			  //  storeId:'projectInfor.simpsonsStore',
			 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
				proxy:{
				    	type: 'transaction',
				        functionId:'RE100000004',
				        extraParams:{
				        	req:'selexpert'
				        },
				        reader: {
				            type: 'json',
				            root: 'expert'         	
				        }
				},
				autoLoad: true
			});
		  //第一次抽选的专家
		  projectInfor.isselexpert=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
				 	req:'isselexpert'
				 },
				 reader:{
					 type:'json',
					 root:'isselexpert'
				 }
			 }
		 });
		//第二次抽选的专家
		  projectInfor.isselexpert2=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
					 req:'isselexpert'
				 },
				 reader:{
					 type:'json',
					 root:'isselexpert'
				 }
			 }
		 });
		 //临时的抽选中的专家
		  projectInfor.isselexpert3=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
					 req:'isselexpert'
				 },
				 reader:{
					 type:'json',
					 root:'isselexpert'
				 }
			 }
		 });
		  //指定的专家
		  projectInfor.issedexpert=Ext.create('Ext.data.Store',{
				 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
				 proxy:{
					 type:'transaction',
					 functionId:'RE100000004',
					 extraParams:{
						 req:'isselexpert'
					 },
					 reader:{
						 type:'json',
						 root:'isselexpert'
					 }
				 }
			 });
            /**
			  * 第一次抽选的专家面板
			  * 
			  */
			 var gridpanel1=Ext.create('Ext.grid.Panel', {
				    store: Ext.data.StoreManager.lookup(projectInfor.isselexpert),
				    border:true,
				    columns: [
	                     { header: '',  dataIndex: 'remark',width:40 },
				         { header: '工号',  dataIndex: 'a0100',width:80 },
		            	    	        { header: '姓名', dataIndex: 'a0101',width:80 },
		            	    	        { header: '科别', dataIndex: 'category',width:80 },
		            	    	        { header: '科室',  dataIndex: 'dept',width:90 },
		            	    	        { header: '电话', dataIndex: 'tel',width:100 },
		            	    	        { header: '座机/小灵通',  dataIndex: 'phone',width:100 },
		            	    	        { header: '专业', dataIndex: 'special' ,width:100},
		            	    	        { header:'是否参加',dataIndex:'accept',width:76, editor:new Ext.form.ComboBox({
		            	    	        	store:new Ext.data.SimpleStore({
		            	    	        		fields:['key','value'],
		            	    	        		data:[['是','是'],['否','否'],['','']]
		            	    	        	}),
		            	    	        	valueField:'key',
		            	    	        	displayField:'value',	
		            	    	        	 editable: false,forceSelection: true,mode:'local',width:50, listWidth:50,  
		            	    	             blankText:'选择', emptyText:'选择' ,triggerAction: 'all'  
		            	    	        })}
				    ],
				    plugins: [
				              Ext.create('Ext.grid.plugin.CellEditing', {
				                  clicksToEdit: 1
				              })
				          ],
				    width:765,
				    height:334,
				    columnLines:true,
				    renderTo: Ext.getBody()
				    //autoScroll: false
				});
			 /**
			  * 第二次抽选的专家的面板
			  */
			 var gridpanel2=Ext.create('Ext.grid.Panel', {
				    store: Ext.data.StoreManager.lookup(projectInfor.isselexpert2),
				    border:true,
				    columns: [
	                     { header: '',  dataIndex: 'remark',width:41 },
				         { header: '工号',  dataIndex: 'a0100',width:80 },
		            	    	        { header: '姓名', dataIndex: 'a0101',width:80 },
		            	    	        { header: '科别', dataIndex: 'category',width:80 },
		            	    	        { header: '科室',  dataIndex: 'dept',width:90 },
		            	    	        { header: '电话', dataIndex: 'tel',width:100 },
		            	    	        { header: '座机/小灵通',  dataIndex: 'phone',width:100 },
		            	    	        { header: '专业', dataIndex: 'special' ,width:100},
		            	    	        { header:'是否参加',dataIndex:'accept',width:76, editor:new Ext.form.ComboBox({
		            	    	        	store:new Ext.data.SimpleStore({
		            	    	        		fields:['key','value'],
		            	    	        		data:[['是','是'],['否','否']]
		            	    	        	}),
		            	    	        	valueField:'key',
		            	    	        	displayField:'value',	
		            	    	        	 editable: false,forceSelection: true,mode:'local',width:50, listWidth:50,  
		            	    	             blankText:'选择', emptyText:'选择' ,triggerAction: 'all'  
		            	    	        })}
				    ],
				    plugins: [
				              Ext.create('Ext.grid.plugin.CellEditing', {
				                  clicksToEdit: 1
				              })
				          ],
				    width:766,
				    height:334,
				    columnLines:true,
				    renderTo: Ext.getBody()
				    //scroll:true
				});
		
		 projectInfor.personPanel = Ext.widget('panel',{
			 id:'personPanel',
				layout:{
					type:'hbox'
				},
				bodyStyle:'padding-top:5px',
				width:200,
				height:60,
				border:false,
				items:[]
		 });

			 projectInfor.personList=new Array(); 
		 var projform=Ext.create('Ext.form.Panel',{
			 //bodyPadding:5,
			 width:790,
			 url:'',
			 autoScroll:true,
			 formBind: true,
			 bodyPadding: 0,
			 defaults:{
				allowBlank:false,
				blankText:'dada',
				bodyPadding: 0,
				bodyStyle: 'padding:0px'
			 },
			 layout:'anchor',
			 border:false,
			 items:[{               // Results grid specified as a config object with an xtype of 'grid'
	    	        title:'招标项目信息',
	    	        xtype:'fieldset',
	    	       // bodyPadding: 5,
	    	        collapsible:true,
	    	        layout:'form',
	    	        checkboxName:'description',
	    	        //defaultType:'textfield',
	    	        fieldDefaults:{
	    	        	 labelSeparator :'',
	    	        	 	labelWidth:60,
	    	        	 Width:100
	    	        	// allowBlank:false
	    	        },
	    	        items: [{
	    	        	bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.6,
	 	        		// layout:'form',
	 	        		border:false,
	 	        		//items:[{
	 	        		//	allowBlank:false,	
	    	            xtype : "textfield", 
	    	            id:'proj_name',
	    	            fieldLabel:'项目名称'
	    	          //  blankText:'dada'
	 	        		//}]
	 	        	}]
	    	        }, {
	    	        	bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.6,
	 	        		// layout:'form',
	 	        		//border:false,
	 	        		//items:[{
	    	        //	id:'proj_content',
	    	            fieldLabel: '项目内容', 
	    	            xtype:'textarea',
	    	            id:'proj_content',
	    	            height:50
	 	        		//}]
	 	        	    },{ columnWidth:0.38,
	 	        	    	//bodyStyle : "padding-top: 0px; padding-left:10px;",
	 	 	        	    layout:'vbox',
	 	 	        		border:false,
	 	 	        		//items:[{
			 	        	//	 layout:'form',
			 	        	//	border:false,
			 	        		items:[{
			 	        		    xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	            width:300,
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'need_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;需求部门',
			 	    	           // value:'dada',
			 	    	            listeners:{
			 	    	            	/*'change':function(){
			 	    	            		deptstore.load({params:{selitem:this.getValue()}}); 
			 	    	            	}*/
			 	    	            }
			 	        		},{
			 	        			
			 	        			xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	           width:300,
			 	    	           bodyStyle:'padding-left:5px',
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'manager_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;主管部门',
			 	        			
			 	        		}]
	 	    	         
	 	 	        		/*},{	
			 	        		 layout:'form',
			 	        		border:false,
			 	        		items:[{
			 	        		   xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	           bodyStyle:'padding-left:5px',
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'manager_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;主管部门',
			 	    	           // value:'dada',
			 	    	            listeners:{
			 	    	            	/*'change':function(){
			 	    	            		deptstore.load({params:{selitem:this.getValue()}}); 
			 	    	            	}*/
			 	    	          //  }
			 	        		//}]*/
	 	    	         
	 	 	        		//}]
	 	        	    }]
	    	        },{
	    	        	layout:'column',
	    	        	//bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	border:false,
	 	        	    items:[{
		 	        		 columnWidth:0.3,
		 	        		// layout:'form',
		 	        		//border:false,
		 	        		//items:[{
		 	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	 	 	    	            fieldLabel: '抽取人',
	 	 	    	            triggerAction:'all',
	 	    	                store:nameStore,
	 	    	                displayField:'namedesc',
	 	    	                valueField:'namecode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
		 	        		//	xtype:'textfield',
		 	        			//fieldLabel:'抽取人',
		 	        			id:'sup_person'
		 	        		//}]
		 	        	},{
		 	        		columnWidth:0.3,
		 	        	//	layout:'form',
		 	        	//	border:false,
		 	        		//items:[{
		 	        			xtype:'combo',
  	 	 	    	            listConfig:{
  	 	 	    	            	emptyText:'未找到匹配值',
  	 	 	    	            	maxHeight:120
  	 	 	    	            },
  	 	 	    	        
 	 	    	            fieldLabel: '&nbsp;&nbsp;&nbsp;&nbsp;监督人',
 	 	    	            triggerAction:'all',
 	    	                store:nameStore,
 	    	                displayField:'namedesc',
 	    	                valueField:'namecode',
 	    	                queryMode:'local',
 	    	                //forceSelection:true,
 	    	                typeAhead:true,
		 	        			id:'see_person'
		 	        	//	}]
		 	        	},{
		 	        		columnWidth:0.38,
		 	        		// layout:'form',
		 	        	//	border:false,
		 	        		//items:[{
		    	        	 xtype : "textfield", 
		    	            id:'comment_addr',
		    	            fieldLabel:'&nbsp;&nbsp;评标地点'
		 	        	//	}]
		 	        	    }]
	    	        },    /* {
	    	        	layout:'column',
	    	        	border:false,
	 	        	items:[{
	 	        		 columnWidth:0.3,
	 	        		 layout:'form',
	 	        		border:false,
	 	        		items:[{
	 	        			xtype:'textfield',
	 	        			fieldLabel:'抽取人',
	 	        			id:'sup_person'
	 	        		}]
	 	        	},{
	 	        		columnWidth:0.3,
	 	        		layout:'form',
	 	        		border:false,
	 	        		items:[{
	 	        			xtype:'textfield',
	 	        			fieldLabel:'&nbsp;&nbsp;&nbsp;&nbsp;监督人',
	 	        			id:'see_person'
	 	        		}]
	 	        	},{
		        	    	
		        	    	columnWidth:0.4,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	    layout:'form',
		 	        		border:false,
		 	        		items:[{
		    	        	id:'sup_time',
		    	        	format:'Y年m月d日',
		    	        	xtype:'datefield',
		    	            fieldLabel: '&nbsp;&nbsp;抽取时间'
		    	          //  value:new Date()
		    	           
		        	    }]
	 	     
	     	    }]
	 	        },*/
	    	        {
	 	        	layout:'column',
		        	border:false,
		        	    items:[{
		        	    	
		        	    	columnWidth:0.3,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	 //   layout:'form',
		 	        	//	border:false,
		 	        	//	items:[{
		    	        	id:'sup_time',
		    	        	format:'Y年m月d日',
		    	        	xtype:'datefield',
		    	            fieldLabel: '抽取时间',
		    	            value:new Date()
		    	           
		        	  //  }]
	 	     
	     	    },{
		        	    	columnWidth:0.3,
		        	    	//bodyStyle : "padding-top: 25px; padding-left:0px;",
		 	        	 //   layout:'form',
		 	        		//border:false,
		 	        		//items:[{
		    	        	id:'invite_time',
		    	        	xtype:'datefield',
		    	        	format:'Y年m月d日',
		    	            fieldLabel: '&nbsp;&nbsp;招标时间'
		    	            //value:new Date()
		 	        	//	}]	
		        	    	
		        	    },{
		        	    	
		        	    	columnWidth:0.38,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	//    layout:'form',
		 	        	//	border:false,
		 	        	//	items:[{
		    	        	name:'time',
		    	        	xtype:'datefield',
		    	        	format:'Y年m月d日',
		    	        	id:'comment_time',
		    	            fieldLabel: '&nbsp;&nbsp;评标时间'
		    	           
		        	   // }]
	 	     
	     	    }]
	    	        }
	    	        ],// A dummy empty data store
	    	        flex: 2                                       // Use 1/3 of Container's height (hint to Box layout)
	    	    }, {                    // Details Panel specified as a config object (no xtype defaults to 'panel').
	    	        title: '专家抽取条件',
	    	        xtype:'fieldset',
	    	        bodyPadding: 5,
	    	        collapsible:true,
	    	        checkboxName:'description',
	    	        layout:'form',
	    	        fieldDefaults:{
	    	        	labelSeparator :'',
	    	        	labelWidth:80,
	    	        	Width:100
	    	        	// allowBlank:false
	    	        },
	    	        items: [{
	    	        	//allowBlank:true,
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.2,
	 	        		// layout:'form',
	 	        	//	border:false,
	 	        		//items:[{
	    	            xtype : 'spinnerfield',
	    	            id:"right",
	    	            name:'off_num',
	    	            fieldLabel:'1、正选人数',
	    	            value:5,
	    	            onSpinUp:function(){
	    	            	var rightCmp=Ext.getCmp('right');
	    	            	//if(rightCmp.getValue)
	    	            	rightCmp.setValue(Number(rightCmp.getValue())+1);
	    	            },
	    	            onSpinDown:function(){
	    	            	var rightCmp=Ext.getCmp('right');
	    	            	if(rightCmp.value<1){
	    	            		return;
	    	            	}
	    	            	rightCmp.setValue(Number(rightCmp.getValue())-1);
	    	            }
	 	        	//	}]
	 	        	},{
	 	        		 columnWidth:0.26,
	 	        	//	 layout:'form',
	 	        	//	border:false,
	 	        	//	items:[{
	    	            xtype : "spinnerfield", 
	    	            labelWidth:120,
	    	            id:'spa',
	    	            value:5,  //初始化字段
	    	            name:'spa_num',
	    	            fieldLabel:'人，&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备选人数',
	    	            onSpinUp:function(){
	    	            	var spaCmp=Ext.getCmp('spa');  //通过组件ID获取组件对象
	    	            	//增加默认值
	    	            	spaCmp.setValue(Number(spaCmp.getValue())+1);
	    	            },
	 	        		onSpinDown:function(){
	 	        			var  spaCmp=Ext.getCmp('spa');
	 	        			if(spaCmp.value<1){
	    	            		return;
	    	            	}
	 	        			spaCmp.setValue(Number(spaCmp.getValue())-1);
	 	        		}
	 	        	//	}]
	 	        	},{
	 	        		columnWidth:0.1,
	 	        		//bodyStyle : "padding-top: 55px; padding-left:10px;",
	 	        		//bodyStyle:'padding-top:10px',
	 	        		//layout:'form',
	 	        	//	border:false,
	 	        	//	items:[{
	 	        			xtype:'label',
	 	        			text:'人'
	 	        	//	}]
	 	        	}]
	    	        },{
	    	        	layout:'column',
	    	        	border:false,
	    	        	fieldDefault:{
	    	        		//labelWidth:80,
	    	        	   // allowBlank:false
	    	        	},
	    	        	items:[{
	    	        		columnWidth:.3,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        	//	items:[{
	    	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        id:'category',
	  	 	 	    	        name:'catagory',
	 	 	    	            fieldLabel: '2、所属科别',
	 	 	    	            triggerAction:'all',
	 	    	                store:catagoryStore,
	 	    	                displayField:'catagorydesc',
	 	    	                valueField:'catagorycode',
	 	    	                queryMode:'local',
	 	    	               // forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                listeners:{
	 	    	                	'change':function(){
	 	    	                	/*	var rt=parseInt(Ext.getCmp("right").value);
	 	    	                		var spa=parseInt(Ext.getCmp("spa").value);
	 	    	                		var sum=rt+spa;*/
	 	    	                		var special1=Ext.getCmp("special1").value;
	 	    	                		var special2=Ext.getCmp("special2").value;
	 	    	                		var category=this.getValue();
	 	    	                		   // alert(category);
	 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			//projectInfor.isselexpert.load();
	 	    	                			//projectInfor.isselexpert2.load();
	 	    	                			
	 	    	                	}
	 	    	                }
	    	        			
	    	        		//}]
	    	        	},{
	    	        		columnWidth:.22,
	    	        		//layout:'form',
	    	        		bodyStyle:'padding-left:13',
	    	        	//	border:false,
	    	        	//	items:[{
	    	        			 xtype:'combo',
	    	        			 labelWidth:40,
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        name:'need_dpt',
	 	 	    	            fieldLabel: '专业',
	 	 	    	            triggerAction:'all',
	 	    	                store:specialStore,
	 	    	                displayField:'specialdesc',
	 	    	                valueField:'specialcode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                //value:'dada',
	 	    	                id:'special1',
	 	    	               listeners:{
	 	    	            	  'change':function(){
	 	    	                		//var rt=parseInt(Ext.getCmp("right").value);
	 	    	                		//var spa=parseInt(Ext.getCmp("spa").value);
	 	    	                	//	var sum=rt+spa;
	 	    	                		var category=Ext.getCmp("category").value;
	 	    	                		var special2=Ext.getCmp("special2").value;
	 	    	                		var special1=this.getValue();
	 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                		//	projectInfor.isselexpert.load();
	 	    	                		//	projectInfor.isselexpert2.load();
	 	    	            	  }
	 	    	             }
	    	        	//	}]
	    	        	},{
	    	        		columnWidth:.05,
	    	        		//layout:'form',
	    	        		bodyStyle:'padding-left:13;padding-top:5',
	    	        		border:false,
	    	        		//items:[{
	    	        			//labelWidth:40,
	    	        			xtype:'label',
	    	        			text:'或'
	    	        		//}]
	    	        	},{
	    	        		columnWidth:.14,
	    	        	//	layout:'form',
	    	        		bodyStyle:'padding-left:0',
	    	        	//	border:false,
	    	        		//items:[{
	    	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        name:'need_dpt',
	  	 	 	    	        id:'special2',
	 	 	    	            fieldLabel: '',
	 	 	    	            triggerAction:'all',
	 	    	                store:specialStore,
	 	    	                displayField:'specialdesc',
	 	    	                valueField:'specialcode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                //value:'dada'
	 	    	               listeners:{
		 	    	            	  'change':function(){
		 	    	                		var category=Ext.getCmp("category").value;
		 	    	                		var special1=Ext.getCmp("special1").value;
		 	    	                		var special2=this.getValue();
		 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
		 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
		 	    	                		//	projectInfor.isselexpert.load();
		 	    	                		//	projectInfor.isselexpert2.load();
		 	    	            	  }
		 	    	             }
	    	        		//}]
	    	        	}]
	    	        },{
	    	        	layout:'column',
	    	        	border:false,
	    	        	items:[{
	    	        		columnWidth:.35,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        		bodyStyle:'padding-top:10px',
	    	        	//	items:[{
	    	        			
	    	        			labelWidth:140,
	    	        			xtype:'label',
	    	        			name:'zhiding',
	    	        			text:'3、本次指定以下专家'
	    	        	//	}]
	    	        	},projectInfor.personPanel,{
	    	        		columnWidth:.2,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        		bodyStyle : "padding-left:10px;padding-top:10px",
	    	        	//	items:[{
	    	        			  xtype : "label", 
		    	    	           // id:'proj_name',
		    	    	            //text:'添加专家',
	    	        			  
		    	    	          html:"<a href=javascript:projectInfor.addexpertPanel()>添加专家</a>"
	    	        	//	}]
	    	        	},{

	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		  //  items:[{
	    	    			xtype: 'button',
	    	                text : '抽选专家',
	    	                handler:function(){
	    	                	//projectInfor.simpsonsStore.reload();
	    	                	 projectInfor.simpsonsStore.reload();
		    	                 var selected=projectInfor.issedexpert.getCount();
	    	                	 projectInfor.simpsonsStore1.reload();
	    	                	// alert(selected);
	    	                	var right=parseInt(Ext.getCmp('right').value);
	    	                	var selected=projectInfor.issedexpert.getCount();
	    	                	if(selected>right){
	    	                		Ext.Msg.alert('提示','指定专家人数不能多于正选人数！');
	    	                		return;
	    	                	}
	    	                	while(projectInfor.isselexpert.getCount()>selected){
	    	                		//alert('建国门'+selected);
	    	                    	projectInfor.isselexpert.removeAt(selected);
	    	                    }
	    	                	var total=projectInfor.simpsonsStore.getCount();
	    	                	//alert(selected+' '+total)
	    	                	if(selected>0){
	    	                		right=right-projectInfor.issedexpert.getCount();
	    	                		/*projectInfor.issedexpert.each(function(rec){
	    	                			//alert(rec.data.a0100);	
	    	                			projectInfor.isselexpert.add(rec);
	    	                		}) */
	    	                		for(var i=0;i<projectInfor.simpsonsStore.getCount();i++){
	    	                			for(var j=0;j<projectInfor.issedexpert.getCount();j++){
	    	                				if(projectInfor.simpsonsStore.getAt(i).get('id')==projectInfor.issedexpert.getAt(j).get('id')){
	    	                					projectInfor.simpsonsStore.removeAt(i);
	    	                					//continue;
	    	                					//alert(1);
	    	                				}//
	    	                			}
	    	                		}
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                			for(var j=0;j<projectInfor.issedexpert.getCount();j++){
	    	                				if(projectInfor.simpsonsStore1.getAt(i).get('id')==projectInfor.issedexpert.getAt(j).get('id')){
	    	                					projectInfor.simpsonsStore1.removeAt(i);
	    	                					//projectInfor.isselexpert.add
	    	                					//continue;
	    	                					//alert(1);
	    	                				}//
	    	                			}
	    	                		}
	    	                	};
	    	                    while(projectInfor.isselexpert.getCount()>selected){
	    	                    	//alert(1);
	    	                    	projectInfor.isselexpert.removeAt(selected-1);
	    	                    }
	    	                	var spa=parseInt(Ext.getCmp('spa').value);
	    	                	var count=right+spa;  //所要抽选的总人数
	    	                	var length=projectInfor.simpsonsStore.getCount();
	    	                	//alert(count+"?"+length)
	    	                	if(count>length){
	    	                		Ext.MessageBox.alert('提示','没有足够可供抽选的专家');
	    	                	}else{
	    	                		Ext.getCmp('selzone1').show();
	    	                		projectInfor.isselexpert2.load();
	    	                		for(var i=0;i<count;i++){
	    	                		var j=parseInt(Math.random()*projectInfor.simpsonsStore.getCount());
	    	                		if(i<right){
	    	                		//	alert(projectInfor.simpsonsStore.getAt(j).get('a0100'));
                                        projectInfor.isselexpert.add({remark:'正选',a0100:projectInfor.simpsonsStore.getAt(j).get('a0100'),a0101:projectInfor.simpsonsStore.getAt(j).get('a0101'),
	    	                			dept:projectInfor.simpsonsStore.getAt(j).get('dept'),category:projectInfor.simpsonsStore.getAt(j).get('category'),special:projectInfor.simpsonsStore.getAt(j).get('special'),
	    	                			id:projectInfor.simpsonsStore.getAt(j).get('id'),tel:projectInfor.simpsonsStore.getAt(j).get('tel'),flag:projectInfor.simpsonsStore.getAt(j).get('flag'),phone:projectInfor.simpsonsStore.getAt(j).get('phone'),accept:''})
	    	                			projectInfor.simpsonsStore.removeAt(j);
                                        projectInfor.simpsonsStore1.removeAt(j);
	    	                		}else{	
	    	                		projectInfor.isselexpert.add({remark:'备选',a0100:projectInfor.simpsonsStore.getAt(j).get('a0100'),a0101:projectInfor.simpsonsStore.getAt(j).get('a0101'),
		    	                		dept:projectInfor.simpsonsStore.getAt(j).get('dept'),category:projectInfor.simpsonsStore.getAt(j).get('category'),special:projectInfor.simpsonsStore.getAt(j).get('special'),
		    	                		id:projectInfor.simpsonsStore.getAt(j).get('id'),tel:projectInfor.simpsonsStore.getAt(j).get('tel'),flag:projectInfor.simpsonsStore.getAt(j).get('flag'),phone:projectInfor.simpsonsStore.getAt(j).get('phone'),accept:''})
		    	                		projectInfor.simpsonsStore.removeAt(j);
	    	                		    projectInfor.simpsonsStore1.removeAt(j);
	    	                		}
	    	                		};
	    	                		
	    	                	}
	    	                }
	    	    		//}]
	    	    		
	    	        	},{


	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		//    items:[{
	    	    			xtype: 'button',
	    	                text : '二次抽选',
	    	                handler:function(){
	    	                projectInfor.simpsonsStore1.reload();
							projectInfor.isselexpert3.removeAll();
							var zhflag =  '0';
							if(projectInfor.isselexpert2.getCount()>0){
                            projectInfor.isselexpert2.each(function(recd){
                                 zhflag = '0';
								if(recd.data.accept == '是'){
								projectInfor.isselexpert.each(function(recs){
									if(recd.data.id == recs.data.id){
									zhflag = '1';
									}
								});
								if(zhflag == '0'){
								  projectInfor.isselexpert3.add(recd);
								}
									}
								
							});
							}
	    	                projectInfor.isselexpert2.removeAll();
	    	                var right=parseInt(Ext.getCmp('right').value);
	    	                var spa=parseInt(Ext.getCmp('spa').value);
	    	                var total=right+spa;
	    	                var length=projectInfor.isselexpert.getCount();
	    	                if(total==length){
	    	                var i=0;
	    	               for(var index=0;index<projectInfor.isselexpert.getCount();index++){
	    	                	if(projectInfor.isselexpert.getAt(index).get('accept')=='是'){
	    	                	i++;
	    	                	if(i<=right){
	    	                	projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.isselexpert.getAt(index).get('a0100'),a0101:projectInfor.isselexpert.getAt(index).get('a0101'),
    	                			dept:projectInfor.isselexpert.getAt(index).get('dept'),category:projectInfor.isselexpert.getAt(index).get('category'),special:projectInfor.isselexpert.getAt(index).get('special'),
    	                			id:projectInfor.isselexpert.getAt(index).get('id'),tel:projectInfor.isselexpert.getAt(index).get('tel'),flag:projectInfor.isselexpert.getAt(index).get('flag'),phone:projectInfor.isselexpert.getAt(index).get('phone'),
    	                			accept:'是'});
	    	                	}else{
	    	                		projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.isselexpert.getAt(index).get('a0100'),a0101:projectInfor.isselexpert.getAt(index).get('a0101'),
	    	                			dept:projectInfor.isselexpert.getAt(index).get('dept'),category:projectInfor.isselexpert.getAt(index).get('category'),special:projectInfor.simpsonsStore.getAt(index).get('special'),
	    	                			id:projectInfor.isselexpert.getAt(index).get('id'),tel:projectInfor.isselexpert.getAt(index).get('tel'),flag:projectInfor.isselexpert.getAt(index).get('flag'),phone:projectInfor.isselexpert.getAt(index).get('phone'),
	    	                			accept:'是'});	
	    	                	}
	    	                	}
	    	                }
							 if(projectInfor.isselexpert3.getCount()>0){
								 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
								 i++;
							 if(i<=right){
	    	                	projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.isselexpert3.getAt(index).get('a0100'),a0101:projectInfor.isselexpert3.getAt(index).get('a0101'),
    	                			dept:projectInfor.isselexpert3.getAt(index).get('dept'),category:projectInfor.isselexpert3.getAt(index).get('category'),special:projectInfor.isselexpert3.getAt(index).get('special'),
    	                			id:projectInfor.isselexpert3.getAt(index).get('id'),tel:projectInfor.isselexpert3.getAt(index).get('tel'),flag:projectInfor.isselexpert3.getAt(index).get('flag'),phone:projectInfor.isselexpert3.getAt(index).get('phone'),
    	                			accept:'是'});
	    	                	}else{
	    	                		projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.isselexpert3.getAt(index).get('a0100'),a0101:projectInfor.isselexpert3.getAt(index).get('a0101'),
	    	                			dept:projectInfor.isselexpert3.getAt(index).get('dept'),category:projectInfor.isselexpert3.getAt(index).get('category'),special:projectInfor.simpsonsStore.getAt(index).get('special'),
	    	                			id:projectInfor.isselexpert3.getAt(index).get('id'),tel:projectInfor.isselexpert3.getAt(index).get('tel'),flag:projectInfor.isselexpert3.getAt(index).get('flag'),phone:projectInfor.isselexpert3.getAt(index).get('phone'),
	    	                			accept:'是'});	
	    	                	}
								 }
							 }
	    	                 if((2*total-i)>(projectInfor.simpsonsStore.getCount())){
	    	                	 Ext.Msg.alert('提示','没有足够可供二次抽选的专家');
	    	                	 projectInfor.isselexpert2.removeAll();
	    	                	 return; 
	    	                 }else{
	    	                	 Ext.getCmp('selzone2').show();
	    	                 };
	    	                 total=total-projectInfor.isselexpert2.getCount();
	    	                 if(total>spa){
	    	                	 right=total-spa;
	    	                	 for(var index=0;index<projectInfor.isselexpert.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
								 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert3.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
	    	                	 for(var i=0;i<right;i++){
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                	 for(var i=0;i<spa;i++){		
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                 }else{
	    	                	 for(var index=0;index<projectInfor.isselexpert.getCount();index++){
 		    	                	
		    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
		    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert.getAt(index).get('a0100')){
		    	                		    	projectInfor.simpsonsStore1.removeAt(i);
		    	                		    	break;
		    	                		    	 }
		    	                		  }
		    	                	 }
									for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert3.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
	    	                	 for(var i=0;i<total;i++){
	    	                		// alert(projectInfor.simpsonsStore1.getCount());
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                 }
	    	                }
	    	                else{
	    	                	Ext.Msg.alert('提示','还没有抽取专家');
	    	                }
	    	    		}
	    	        	//}]
	    	    		},{

	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		  //  items:[{
	    	    			xtype: 'button',
	    	                text : '撤选',
	    	                handler:function(){
	    	                    projectInfor.simpsonsStore.reload();
	    	                    var selected=projectInfor.issedexpert.getCount();
	    	                	while(projectInfor.isselexpert.getCount()>selected){
		    	                    	projectInfor.isselexpert.removeAt(selected);
		    	                    }
	    	                    projectInfor.isselexpert2.load();
		    	                 //   var total=
		    	                	/*for(var i=0;i<projectInfor.simpsonsStore.getCount();i++){
		    	                		for(var j=0;j<projectInfor.issedexpert.getCount();j++){
		    	                			if(projectInfor.simpsonsStore.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
		    	                				//projectInfor.simpsonsStore.add(projectInfor.simpsonsStore1.getAt(i));
		    	                				alert('OK');
		    	                			}
		    	                		}
		    	                	}*/
	    	                }
	    	    		//}]
	    	    		
	    	    		}
	    	        	]
	    	        }], // An array of form fields
	    	        flex: 2             // Use 2/3 of Container's height (hint to Box layout)
	    	    },{
	    	    	title:'抽选的专家',
	    	    	id:'selzone1',
	    	    	xtype:'fieldset',
	    	    //	border:true,
	    	    	bodyPadding:2,
	    	    	hidden:true,
	    	    	collapsible:true,
		    	    layout:'form',
		    	    checkboxName:'description',
	    	    	//layout:'form',
	    	    	items:[{
	    	    		border:false,
	    	    	//	layout:'column',
	    	    	//	items:[{
	    	    			
		    	    		border:false,
		    	    	//	columnWidth:1,
		        	   // 	xtype:'fieldset',
		        	    	//title: '第一次抽选的专家',
		        	    	items:[gridpanel1]
		    	    //	}]
	    	    	}]
	    	    },{
	    	    	title:'第二次抽选的专家',
	    	    	id:'selzone2',
	    	    	xtype:'fieldset',
	    	    	border:true,
	    	    	bodyPadding:2,
	    	    	hidden:true,
	    	    	collapsible:true,
		    	    //layout:'form',
		    	    checkboxName:'description',
	    	    	layout:'form',
	    	    	items:[{
	    	    		border:false,
	    	    	//	layout:'column',
	    	    		//items:[{
		    	    		
		    	    		border:false,
		    	    	//	columnWidth:1,
		        	    	//xtype:'fieldset',
		        	    	//title: '第一次抽选的专家',
		        	    	items:[gridpanel2]
		    	    	}]
	    	    //	}]
	    	    }],
	    	    buttonAlign : "center",
	    	    buttons:[{
	    	    	
	    	    	text:'清空',
	    	    	handler:function(){
	    	    		projectInfor.personPanel.removeAll();
	    	    		//Ext.removeNode(document.getElementById('addexpertwid'));
	    	    		projectInfor.simpsonsStore1.removeAll();
	    	    		projectInfor.isselexpert.load();
	    	    		projectInfor.isselexpert2.load();
	    	    		projectInfor.issedexpert.load();
	    	    		this.up(projform).getForm().reset();
	    	    	}
	    	    },{
	    	    	text:'提交',
	    	    	formBind:true,
	    	    	disable:true,
	    	    	handler:function(){
	    	    		var map = new HashMap();
	    	    		map.put('opt', 'insertInfo');
	    	    	    var proj_name=Ext.getCmp('proj_name').value;
	    	    	   // alert(proj_name);
	    	    	    if(proj_name==""){
	    	    	    	Ext.MessageBox.alert('提示','项目名称不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       if(proj_name.length>50){
	    	    	       Ext.MessageBox.alert('提示','项目名称过长');
	    	    	       return;
	    	    	       }
	    	    	       map.put('proj_name', proj_name);
	    	    	    }
	    	    	    var proj_content=Ext.getCmp('proj_content').value;
	    	    	  //  alert(proj_content);
	    	    	    if(proj_content==""){
	    	    	    	Ext.MessageBox.alert('提示','项目内容不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	    	if(proj_content.length>200){
	    	    	    		Ext.MessageBox.alert('提示','项目内容过长');
	    	    	    	}
	    	    	       map.put('proj_content', proj_content);
	    	    	    }
	    	    	    var need_dept=Ext.getCmp('need_dept').value;
	    	    	    if(need_dept==null){
	    	    	    	Ext.MessageBox.alert('提示','需求部门不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('need_dept', need_dept);
	    	    	    }
	    	    	    var manager_dept=Ext.getCmp('manager_dept').value;
	    	    	    if(manager_dept==null){
	    	    	    	Ext.MessageBox.alert('提示','主管部门不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('manager_dept', manager_dept);
	    	    	    }
	    	    	    var comment_addr=Ext.getCmp('comment_addr').value;
	    	    	    if(comment_addr==null || comment_addr==''){
	    	    	    	Ext.MessageBox.alert('提示','评标地点不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('comment_addr', comment_addr);
	    	    	    }
	    	    	    var comment_time=Ext.getCmp('comment_time').value;
	    	    	    if(comment_time==null){
	    	    	    	Ext.MessageBox.alert('提示','评标时间不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('comment_time', comment_time);
	    	    	    }
						var sup_personid = Ext.getCmp('sup_person').value;
	    	    	    var sup_person=Ext.getCmp('sup_person').getRawValue();
						//alert("2332"+sup_person);
	    	    	    if(sup_person==null || sup_person==''){
	    	    	    	Ext.MessageBox.alert('提示','抽取人不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('sup_person', sup_person);
						   map.put('sup_personid', sup_personid);
	    	    	    }
	    	    	    var see_person=Ext.getCmp('see_person').getRawValue();
						var see_personid=Ext.getCmp('see_person').value;
	    	    	    if(see_person==null || see_person==''){
	    	    	    	Ext.MessageBox.alert('提示','监督人不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('see_person', see_person);
						   map.put('see_personid', see_personid);
	    	    	    }
	    	    	    var sup_time=Ext.getCmp('sup_time').value;
	    	    	    if(sup_time==null){
	    	    	    	Ext.MessageBox.alert('提示','抽取时间不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('sup_time', sup_time);
	    	    	    }
	    	    	    var invite_time=Ext.getCmp('invite_time').value;
	    	    	    if(invite_time==null){
	    	    	    	Ext.MessageBox.alert('提示','招标时间不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('invite_time', invite_time);
	    	    	    }
	    	    	    var right=Ext.getCmp('right').value;
	    	    	    if(right!=null && right!=''){
	    	    	    	map.put('right',right);
	    	    	    }
	    	    	    var spa=Ext.getCmp('spa').value;
	    	    	    if(spa!=null && spa!=''){
	    	    	    	map.put('spa',spa);
	    	    	    }
	    	    	    var category=Ext.getCmp('category').value;
	    	    	    if(category!=null){
	    	    	    	map.put('category',category);
	    	    	    }
	    	    	    var special1=Ext.getCmp('special1').value;
	    	    	    if(special1!=null){
	    	    	    	map.put('special1',special1);
	    	    	    }
	    	    	    var special2=Ext.getCmp('special2').value;
	    	    	    if(special2!=null){
	    	    	    	map.put('special2',special2);
	    	    	    }
	    	    	   if(projectInfor.isselexpert.getCount()>0){
	    	    	     var selected=new Array();
	    	    	     projectInfor.isselexpert.each(function(record){
	    	    	    	 alert(record.data.accept);
	    	    	    	 selected.push(record.data);
	    	    	    	 
	    	    	     });
	    	    	     
	    	    	    // alert(selected);
	    	    	     if(projectInfor.isselexpert.getCount()!=(parseInt(right)+(parseInt(spa)))){
	    	    	    	 Ext.Msg.alert('提示','所抽选专家人数与设定人数不符');
	    	    	    	 return;
	    	    	     }
	    	    	     map.put('selected1', selected)
	    	    	    }
	    	    	   if(projectInfor.isselexpert2.getCount()>0){
	    	    		   var selected=new Array();
		    	    	     projectInfor.isselexpert2.each(function(record){
		    	    	    	 selected.push(record.data)
		    	    	     });
		    	    	     if(projectInfor.isselexpert2.getCount()!=(parseInt(right)+parseInt(spa))){
		    	    	    	 Ext.Msg.alert('提示','所抽选专家人数与设定人数不符');
		    	    	    	 return;
		    	    	     }
		    	    	     map.put('selected2', selected) 
	    	    	   }
	    	    	   // alert(0);
	    	    	    Rpc({functionId:'RE100000005',success:function(form,action){
	    	    	    	Ext.getCmp('addprojectwid').destroy();
	    	    	    	//Ext.removeNode(document.getElementById('addprojectwid'));
	    	    	    	var store = Ext.data.StoreManager.lookup('re_project_dataStore');
	    	    			store.load();
		    	    	}},map);
	    	    	}
	    	    },{
	    	    	text:'关闭',
	    	    	handler:function(){
	    	    		//alert(1);
	    	    		Ext.getCmp('addprojectwid').destroy();
	    	    	//	parentNode.removeChild(document.getElementById('addprojectwid'));
	    	    	}
	    	    }]
		 });
		 Ext.create('Ext.window.Window',{
	    	    title: '创建招标项目',
	    	    id:'addprojectwid',
	    	   // closable: false,
	    	    layout:'fit',
	    	    closable:false,
	    	    width: 840,
	    	    height: 600,
	    	    renderTo: Ext.getBody(),
	    	    items: [projform],
	    	  //  modal:true,
	    	    autoScroll:true,
                    maximizable:true,
	    	    closeAction:'close'
	       }).show();
	},
	showProjectInfo:function(form,action){
	      
	},
	//保存项目
	saveProject:function(){  
		var tem = projectInfor.getSelectedParams();
		if(!tem)
			return;
		//alert(saveinfo[0].rmb);
		var map=new HashMap();
		map.put('opt', 'save');
		map.put('saveinfo', saveinfo);
		saveinfo=[];
		Rpc({functionId:'RE100000005',success:projectInfor.loadTable},map);
		experts='';
		Ext.Msg.alert('提示','保存成功！');
	},
	//专家抽选
	selectedExperts:function(){
		var tem = projectInfor.getSelectedParams();
		if(!tem)
			return;
		var str=projectIds.split(',');
		if(str.length>1){
			Ext.Msg.alert('提示','不能对多个项目抽取专家！');
			return;
		}
		 var map=new HashMap();
		 map.put('opt', 'chargesel');
	     map.put('projectIds', projectIds);
		// Rpc({functionId:'RE100000001',async:false,success:projectInfor.getTableOK},map);
		 Rpc({functionId:'RE100000005',async:false,success:function(form,action){
			 var result = Ext.decode(form.responseText);
			 var info=result.data;
			 experts=info;
		 }},map);
        if(experts!=''){
			 Ext.Msg.alert('提示','当前项目已经录入招标结果！');
			 experts="";
			 return;
		}
		     var map=new HashMap();
		     map.put('opt', 'selexpert');
		     map.put('projectIds', projectIds);
			// Rpc({functionId:'RE100000001',async:false,success:projectInfor.getTableOK},map);
			 Rpc({functionId:'RE100000005',async:false,success:projectInfor.selexpertPage},map);
	},
	//抽取专家界面
	selexpertPage:function(form,action){
	//	alert(1);
		var result = Ext.decode(form.responseText);
		var info=result.data;
		var deptstore = Ext.create('Ext.data.Store', {
			fields:[{name:'deptdesc'},{name:'deptcode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"dept"
			        },
			        reader: {
			            type: 'json',
			            root: 'deptstr'         	
			        }
			},
			autoLoad: true
		});
		var catagoryStore=Ext.create('Ext.data.Store',{
			fields:[{name:'catagorydesc'},{name:'catagorycode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"catagory"
			        },
			        reader: {
			            type: 'json',
			            root: 'categorystr'         	
			        }
			},
			autoLoad: true
			
		});
		var nameStore=Ext.create('Ext.data.Store',{
			fields:[{name:'namedesc'},{name:'namecode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"name"
			        },
			        reader: {
			            type: 'json',
			            root: 'namestr'         	
			        }
			},
			autoLoad: true
			
		});
		var specialStore=Ext.create('Ext.data.Store',{
			fields:[{name:'specialdesc'},{name:'specialcode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"special"
			        },
			        reader: {
			            type: 'json',
			            root: 'specialstr'         	
			        }
			},
			autoLoad: true
			
		});
		//符合条件的专家（显示）
		  projectInfor.simpsonsStore1=Ext.create('Ext.data.Store', {
			  //  storeId:'projectInfor.simpsonsStore',
			 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
				proxy:{
				    	type: 'transaction',
				        functionId:'RE100000004',
				        extraParams:{
				        	req:'selexpert',
				        	opt:'select',
				        	projectId:info.proj_id
				        },
				        reader: {
				            type: 'json',
				            root: 'expert'         	
				        }
				},
				autoLoad: true
			});
		  //符合条件的专家（调用计算）
		  projectInfor.simpsonsStore=Ext.create('Ext.data.Store', {
			  //  storeId:'projectInfor.simpsonsStore',
			 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
				proxy:{
				    	type: 'transaction',
				        functionId:'RE100000004',
				        extraParams:{
				        	req:'selexpert',
				        	opt:'select',
				        	projectId:info.proj_id
				        },
				        reader: {
				            type: 'json',
				            root: 'expert'         	
				        }
				},
				autoLoad: true
			});
		  
		  //第一次抽选的专家
		  projectInfor.isselexpert=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
					 req:'isselexpert',
					 opt:'sel1',
				 	 projectId:info.proj_id
				 },
				 reader:{
					 type:'json',
					 root:'selexpert'
				 }
			 }
		 });
		//第二次抽选的专家
		  projectInfor.isselexpert2=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
					 req:'isselexpert',
					 opt:'sel2',
					 projectId:info.proj_id
				 },
				 reader:{
					 type:'json',
					 root:'selexpert'
				 }
			 }
		 });
		  //指定的专家
		  projectInfor.issedexpert=Ext.create('Ext.data.Store',{
				 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
				 proxy:{
					 type:'transaction',
					 functionId:'RE100000004',
					 extraParams:{
						 req:'issedexpert',
						 opt:'sed',
						 projectId:info.proj_id
					 },
					 reader:{
						 type:'json',
						 root:'sedexpert'
					 }
				 },
				 idProperty : "id"
			 });
		  var gridpanel=Ext.create('Ext.grid.Panel', {
			    store: Ext.data.StoreManager.lookup(projectInfor.simpsonsStore1),
			    border:true,
			    columns: [
			         { header: '工号',  dataIndex: 'a0100',width:70 },
	            	    	        { header: '姓名', dataIndex: 'a0101',width:50 },
	            	    	        { header: '科别', dataIndex: 'category',width:60 },
	            	    	        { header: '科室',  dataIndex: 'dept',width:60 },
	            	    	        { header: '专业', dataIndex: 'special' ,width:60,renderer: function (v, m, r) {
	            	    	            m.attr = 'style="white-space:normal;word-wrap:break-word;word-break:break-all;"';
	            	    	            return v;
	            	    	        }}
			    ],
			    width:302,
			    height:260,
			    columnLines:true,
			    renderTo: Ext.getBody(),
			    scroll:true
			});
		  
		  /**
			  * 第一次抽选的专家面板
			  * 
			  */
			 var gridpanel1=Ext.create('Ext.grid.Panel', {
				    store: Ext.data.StoreManager.lookup(projectInfor.isselexpert),
				    border:true,
				    columns: [
	                     { header: '',  dataIndex: 'remark',width:40 },
				         { header: '工号',  dataIndex: 'a0100',width:80 },
		            	    	        { header: '姓名', dataIndex: 'a0101',width:80 },
		            	    	        { header: '科别', dataIndex: 'category',width:80 },
		            	    	        { header: '科室',  dataIndex: 'dept',width:90 },
		            	    	        { header: '电话', dataIndex: 'tel',width:100 },
		            	    	        { header: '座机/小灵通',  dataIndex: 'phone',width:100 },
		            	    	        { header: '专业', dataIndex: 'special' ,width:100},
		            	    	        { header:'是否参加',dataIndex:'accept',width:76, editor:new Ext.form.ComboBox({
		            	    	        	store:new Ext.data.SimpleStore({
		            	    	        		fields:['key','value'],
		            	    	        		data:[['是','是'],['否','否']]
		            	    	        	}),
		            	    	        	valueField:'key',
		            	    	        	displayField:'value',	
		            	    	        	 editable: false,forceSelection: true,mode:'local',width:50, listWidth:50,  
		            	    	             blankText:'选择', emptyText:'选择' ,triggerAction: 'all'  
		            	    	        })
	                 }
				    ],
				    plugins: [
				              Ext.create('Ext.grid.plugin.CellEditing', {
				                  clicksToEdit: 1
				              })
				          ],
				    width:766,
				    height:334,
				    columnLines:true,
				    renderTo: Ext.getBody(),
				    scroll:true
				});
			 /**
			  * 第二次抽选的专家的面板
			  */
			 var gridpanel2=Ext.create('Ext.grid.Panel', {
				    store: Ext.data.StoreManager.lookup(projectInfor.isselexpert2),
				    border:true,
				    columns: [
	                  { header: '',  dataIndex: 'remark',width:40 },
				         { header: '工号',  dataIndex: 'a0100',width:80 },
		            	    	        { header: '姓名', dataIndex: 'a0101',width:80 },
		            	    	        { header: '科别', dataIndex: 'category',width:80 },
		            	    	        { header: '科室',  dataIndex: 'dept',width:90 },
		            	    	        { header: '电话', dataIndex: 'tel',width:100 },
		            	    	        { header: '座机/小灵通',  dataIndex: 'phone',width:100 },
		            	    	        { header: '专业', dataIndex: 'special' ,width:100},
		            	    	        { header:'是否参加',dataIndex:'accept',width:76,editor:new Ext.form.ComboBox({
		            	    	        	store:new Ext.data.SimpleStore({
		            	    	        		fields:['key','value'],
		            	    	        		data:[['是','是'],['否','否']]
		            	    	        	}),
		            	    	        	valueField:'key',
		            	    	        	displayField:'value',	
		            	    	        	 editable: false,forceSelection: true,mode:'local',width:50, listWidth:50,  
		            	    	             blankText:'选择', emptyText:'选择' ,triggerAction: 'all'  
		            	    	        })}
				    ],
				    plugins: [
				              Ext.create('Ext.grid.plugin.CellEditing', {
				                  clicksToEdit: 1
				              })
				          ],
				    width:766,
				    height:334,
				    columnLines:true,
				    renderTo: Ext.getBody(),
				    scroll:true
				});
		 projectInfor.personPanel = Ext.widget('panel',{
			 id:'personPanel',
				layout:{
					type:'hbox'
				},
				bodyStyle:'padding-top:5px',
				width:200,
				height:60,
				margin: 0,
				border:false,
				items:[]
		 });
		 for(var i=0;i<info.list.length;i++){
			  var split=info.list[i].split(",");
			 var imgUsr = 'usr'; //人员库前缀
				var imgA0100 = split[0];   //人员编号
				var imgQuality = 'l';  // 照片质量 l 低分辨看  h 高分辨率 
				var personImg = Ext.create('Ext.Img', {
					id:'pho_'+imgA0100,
				    src: "/servlet/DisplayOleContent?nbase="+imgUsr+"&a0100="+imgA0100+"&sanyuan=1&quality="+imgQuality,
				    style:'border-radius: 50%;',
				    width:25,
				    height:25,
				    padding:0,
				    margin:'0 15 0 0',
				    listeners: {
				        mouseover: {
				            element: 'el', 
				            fn: function(a, o){ projectInfor.showHideDelImg(o.id, "1"); }
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ projectInfor.showHideDelImg(o.id,"0"); }
				        }
					}
				});
				var delImg = Ext.create('Ext.Img', {
					src: "/workplan/image/remove.png",
					id:'del_'+imgA0100,
					style:'cursor:pointer;',
					width:15,
					height:15,
					hidden:true,
					cls:'delImg',
					listeners: {
				        click: {
				            element: 'el', 
				            fn: function(a, o, b, c){ projectInfor.deletePerson(o.id); }
				        }, mouseover: {
				            element: 'el', 
				            fn: function(a, o){ projectInfor.showHideDelImg(o.id, "1"); }
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ projectInfor.showHideDelImg(o.id,"0"); }
				        }
					}
				});
				var person = Ext.widget('panel',{
					id:'per_'+imgA0100,
					layout:{
						type:'vbox',
						align:'center'
					},
					margin: 0,
					border:false,
					items:[delImg,personImg,{xtype: 'label',text: split[1],margin:0}]
				});
				
				projectInfor.personPanel.add(person);
		 };
		 projectInfor.simpsonsStore.reload();
		 projectInfor.simpsonsStore1.reload();
		 projectInfor.issedexpert.reload();
		 projectInfor.isselexpert.reload();
		 projectInfor.isselexpert2.reload();
/*	     
		 }*/
		// projectInfor.issedexpert.reload();
		// alert(projectInfor.issedexpert.getCount());
		 deptstore.reload();
		// alert(deptstore.getCount());
		 catagoryStore.reload();
		 specialStore.reload(); 
		 nameStore.reload();
		 var projform=Ext.create('Ext.form.Panel',{
			 bodyPadding:5,
			 width:790,
			 url:'',
			 autoScroll:true,
			 formBind: true,
			 layout:'anchor',
			 border:false,
			 items:[{               // Results grid specified as a config object with an xtype of 'grid'
	    	        title:'招标项目信息',
	    	        xtype:'fieldset',
	    	       // bodyPadding: 5,
	    	        collapsible:true,
	    	        layout:'form',
	    	        checkboxName:'description',
	    	        //defaultType:'textfield',
	    	        fieldDefaults:{
	    	        	 labelSeparator :'',
	    	        	 	labelWidth:60,
	    	        	 Width:100
	    	        	// allowBlank:false
	    	        },
	    	        items: [{
	    	        	bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.6,
	 	        		// layout:'form',
	 	        		border:false,
	 	        		//items:[{
	 	        		//	allowBlank:false,	
	    	            xtype : "textfield", 
	    	            id:'proj_name',
	    	            fieldLabel:'项目名称',
	    	            	value:info.proj_name
	    	          //  blankText:'dada'
	 	        		//}]
	 	        	}]
	    	        }, {
	    	        	bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.6,
	 	        		// layout:'form',
	 	        		//border:false,
	 	        		//items:[{
	    	        //	id:'proj_content',
	    	            fieldLabel: '项目内容', 
	    	            xtype:'textarea',
	    	            id:'proj_content',
	    	            value:info.proj_content,
	    	            height:50
	 	        		//}]
	 	        	    },{ columnWidth:0.38,
	 	        	    	//bodyStyle : "padding-top: 0px; padding-left:10px;",
	 	 	        	    layout:'vbox',
	 	 	        		border:false,
	 	 	        		//items:[{
			 	        	//	 layout:'form',
			 	        	//	border:false,
			 	        		items:[{
			 	        		    xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	            width:300,
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'need_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;需求部门',
			 	    	           // value:'dada',
			 	    	          value:info.need_dept,
			 	    	            listeners:{
			 	    	            	/*'change':function(){
			 	    	            		deptstore.load({params:{selitem:this.getValue()}}); 
			 	    	            	}*/
			 	    	            }
			 	        		},{
			 	        			
			 	        			xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	           width:300,
			 	    	           bodyStyle:'padding-left:5px',
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'manager_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;主管部门',
			 	    	          value:info.manager_dept
			 	        			
			 	        		}]
	 	    	         
	 	 	        		/*},{	
			 	        		 layout:'form',
			 	        		border:false,
			 	        		items:[{
			 	        		   xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	           bodyStyle:'padding-left:5px',
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'manager_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;主管部门',
			 	    	           // value:'dada',
			 	    	            listeners:{
			 	    	            	/*'change':function(){
			 	    	            		deptstore.load({params:{selitem:this.getValue()}}); 
			 	    	            	}*/
			 	    	          //  }
			 	        		//}]*/
	 	    	         
	 	 	        		//}]
	 	        	    }]
	    	        },{
	    	        	layout:'column',
	    	        	//bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	border:false,
	 	        	    items:[{
		 	        		 columnWidth:0.3,
		 	        		// layout:'form',
		 	        		//border:false,
		 	        		//items:[{
		 	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	 	 	    	            fieldLabel: '抽取人',
	 	 	    	            triggerAction:'all',
	 	    	                store:nameStore,
	 	    	                displayField:'namedesc',
	 	    	                valueField:'namecode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	               value:info.sup_person,
		 	        		//	xtype:'textfield',
		 	        			//fieldLabel:'抽取人',
		 	        			id:'sup_person'
		 	        		//}]
		 	        	},{
		 	        		columnWidth:0.3,
		 	        	//	layout:'form',
		 	        	//	border:false,
		 	        		//items:[{
		 	        			xtype:'combo',
  	 	 	    	            listConfig:{
  	 	 	    	            	emptyText:'未找到匹配值',
  	 	 	    	            	maxHeight:120
  	 	 	    	            },
  	 	 	    	        
 	 	    	            fieldLabel: '&nbsp;&nbsp;&nbsp;&nbsp;监督人',
 	 	    	            triggerAction:'all',
 	    	                store:nameStore,
 	    	                displayField:'namedesc',
 	    	                valueField:'namecode',
 	    	                queryMode:'local',
 	    	                //forceSelection:true,
 	    	                typeAhead:true,
		 	        			id:'see_person',
		 	        			value:info.see_person
		 	        	//	}]
		 	        	},{
		 	        		columnWidth:0.38,
		 	        		// layout:'form',
		 	        	//	border:false,
		 	        		//items:[{
		    	        	 xtype : "textfield", 
		    	            id:'comment_addr',
		    	            fieldLabel:'&nbsp;&nbsp;评标地点',
		    	            value:info.comment_addr
		 	        	//	}]
		 	        	    }]
	    	        },    /* {
	    	        	layout:'column',
	    	        	border:false,
	 	        	items:[{
	 	        		 columnWidth:0.3,
	 	        		 layout:'form',
	 	        		border:false,
	 	        		items:[{
	 	        			xtype:'textfield',
	 	        			fieldLabel:'抽取人',
	 	        			id:'sup_person'
	 	        		}]
	 	        	},{
	 	        		columnWidth:0.3,
	 	        		layout:'form',
	 	        		border:false,
	 	        		items:[{
	 	        			xtype:'textfield',
	 	        			fieldLabel:'&nbsp;&nbsp;&nbsp;&nbsp;监督人',
	 	        			id:'see_person'
	 	        		}]
	 	        	},{
		        	    	
		        	    	columnWidth:0.4,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	    layout:'form',
		 	        		border:false,
		 	        		items:[{
		    	        	id:'sup_time',
		    	        	format:'Y年m月d日',
		    	        	xtype:'datefield',
		    	            fieldLabel: '&nbsp;&nbsp;抽取时间'
		    	          //  value:new Date()
		    	           
		        	    }]
	 	     
	     	    }]
	 	        },*/
	    	        {
	 	        	layout:'column',
		        	border:false,
		        	    items:[{
		        	    	
		        	    	columnWidth:0.3,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	 //   layout:'form',
		 	        	//	border:false,
		 	        	//	items:[{
		    	        	id:'sup_time',
		    	        	format:'Y年m月d日',
		    	        	xtype:'datefield',
		    	            fieldLabel: '抽取时间',
		    	            value:new Date()
		    	           
		        	  //  }]
	 	     
	     	    },{
		        	    	columnWidth:0.3,
		        	    	//bodyStyle : "padding-top: 25px; padding-left:0px;",
		 	        	 //   layout:'form',
		 	        		//border:false,
		 	        		//items:[{
		    	        	id:'invite_time',
		    	        	xtype:'datefield',
		    	        	format:'Y年m月d日',
		    	            fieldLabel: '&nbsp;&nbsp;招标时间',
		    	            value:info.invite_time
		    	            //value:new Date()
		 	        	//	}]	
		        	    	
		        	    },{
		        	    	
		        	    	columnWidth:0.38,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	//    layout:'form',
		 	        	//	border:false,
		 	        	//	items:[{
		    	        	name:'time',
		    	        	xtype:'datefield',
		    	        	format:'Y年m月d日',
		    	        	id:'comment_time',
		    	            fieldLabel: '&nbsp;&nbsp;评标时间',
			    	            value:info.comment_time
		        	   // }]
	 	     
	     	    }]
	    	        }
	    	        ],// A dummy empty data store
	    	        flex: 2                                       // Use 1/3 of Container's height (hint to Box layout)
	    	    }, {                    // Details Panel specified as a config object (no xtype defaults to 'panel').
	    	        title: '专家抽取条件',
	    	        xtype:'fieldset',
	    	        bodyPadding: 5,
	    	        collapsible:true,
	    	        checkboxName:'description',
	    	        layout:'form',
	    	        fieldDefaults:{
	    	        	labelSeparator :'',
	    	        	labelWidth:80,
	    	        	Width:100
	    	        	// allowBlank:false
	    	        },
	    	        items: [{
	    	        	//allowBlank:true,
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.2,
	 	        		// layout:'form',
	 	        	//	border:false,
	 	        		//items:[{
	    	            xtype : 'spinnerfield',
	    	            id:"right",
	    	            name:'off_num',
	    	            fieldLabel:'1、正选人数',
	    	            value:info.right,
	    	            onSpinUp:function(){
	    	            	var rightCmp=Ext.getCmp('right');
	    	            	//if(rightCmp.getValue)
	    	            	rightCmp.setValue(Number(rightCmp.getValue())+1);
	    	            },
	    	            onSpinDown:function(){
	    	            	var rightCmp=Ext.getCmp('right');
	    	            	if(rightCmp.value<1){
	    	            		return;
	    	            	}
	    	            	rightCmp.setValue(Number(rightCmp.getValue())-1);
	    	            }
	 	        	//	}]
	 	        	},{
	 	        		 columnWidth:0.26,
	 	        	//	 layout:'form',
	 	        	//	border:false,
	 	        	//	items:[{
	    	            xtype : "spinnerfield", 
	    	            labelWidth:120,
	    	            id:'spa',
	    	            value:info.spa, //初始化字段
	    	            name:'spa_num',
	    	            fieldLabel:'人，&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备选人数',
	    	            onSpinUp:function(){
	    	            	var spaCmp=Ext.getCmp('spa');  //通过组件ID获取组件对象
	    	            	//增加默认值
	    	            	spaCmp.setValue(Number(spaCmp.getValue())+1);
	    	            },
	 	        		onSpinDown:function(){
	 	        			var  spaCmp=Ext.getCmp('spa');
	 	        			if(spaCmp.value<1){
	    	            		return;
	    	            	}
	 	        			spaCmp.setValue(Number(spaCmp.getValue())-1);
	 	        		}
	 	        	//	}]
	 	        	},{
	 	        		columnWidth:0.05,
	 	        		//bodyStyle : "padding-top: 55px; padding-left:10px;",
	 	        		bodyStyle:'padding-top:4px',
	 	        		//layout:'form',
	 	        	//	border:false,
	 	        	//	items:[{
	 	        			xtype:'label',
	 	        			text:'人'
	 	        	//	}]
	 	        	}]
	    	        },{
	    	        	layout:'column',
	    	        	border:false,
	    	        	fieldDefault:{
	    	        		//labelWidth:80,
	    	        	   // allowBlank:false
	    	        	},
	    	        	items:[{
	    	        		columnWidth:.3,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        	//	items:[{
	    	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        id:'category',
	  	 	 	    	        name:'catagory',
	 	 	    	            fieldLabel: '2、所属科别',
	 	 	    	            triggerAction:'all',
	 	    	                store:catagoryStore,
	 	    	                displayField:'catagorydesc',
	 	    	                valueField:'catagorycode',
	 	    	                queryMode:'local',
	 	    	               // forceSelection:true,
	 	    	                typeAhead:true,
	 	    	               value:info.category,
	 	    	                listeners:{
	 	    	                	'change':function(){
	 	    	                	/*	var rt=parseInt(Ext.getCmp("right").value);
	 	    	                		var spa=parseInt(Ext.getCmp("spa").value);
	 	    	                		var sum=rt+spa;*/
	 	    	                		var special1=Ext.getCmp("special1").value;
	 	    	                		var special2=Ext.getCmp("special2").value;
	 	    	                		var category=this.getValue();
	 	    	                		   // alert(category);
	 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			//projectInfor.isselexpert.load();
	 	    	                			//projectInfor.isselexpert2.load();
	 	    	                			
	 	    	                	}
	 	    	                }
	    	        			
	    	        		//}]
	    	        	},{
	    	        		columnWidth:.22,
	    	        		//layout:'form',
	    	        		bodyStyle:'padding-left:13',
	    	        	//	border:false,
	    	        	//	items:[{
	    	        			 xtype:'combo',
	    	        			 labelWidth:40,
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        name:'need_dpt',
	 	 	    	            fieldLabel: '专业',
	 	 	    	            triggerAction:'all',
	 	    	                store:specialStore,
	 	    	                displayField:'specialdesc',
	 	    	                valueField:'specialcode',
	 	    	                queryMode:'local',
	 	    	               value:info.special1,
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                //value:'dada',
	 	    	                id:'special1',
	 	    	               listeners:{
	 	    	            	  'change':function(){
	 	    	                		//var rt=parseInt(Ext.getCmp("right").value);
	 	    	                		//var spa=parseInt(Ext.getCmp("spa").value);
	 	    	                	//	var sum=rt+spa;
	 	    	                		var category=Ext.getCmp("category").value;
	 	    	                		var special2=Ext.getCmp("special2").value;
	 	    	                		var special1=this.getValue();
	 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                		//	projectInfor.isselexpert.load();
	 	    	                		//	projectInfor.isselexpert2.load();
	 	    	            	  }
	 	    	             }
	    	        	//	}]
	    	        	},{
	    	        		columnWidth:.05,
	    	        		//layout:'form',
	    	        		bodyStyle:'padding-left:13;padding-top:5',
	    	        		border:false,
	    	        		//items:[{
	    	        			//labelWidth:40,
	    	        			xtype:'label',
	    	        			text:'或'
	    	        		//}]
	    	        	},{
	    	        		columnWidth:.14,
	    	        	//	layout:'form',
	    	        		bodyStyle:'padding-left:0',
	    	        	//	border:false,
	    	        		//items:[{
	    	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        name:'need_dpt',
	  	 	 	    	        id:'special2',
	 	 	    	            fieldLabel: '',
	 	 	    	            triggerAction:'all',
	 	    	                store:specialStore,
	 	    	                displayField:'specialdesc',
	 	    	                valueField:'specialcode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                //value:'dada'
	 	    	               value:info.special2,
	 	    	               listeners:{
		 	    	            	  'change':function(){
		 	    	                		var category=Ext.getCmp("category").value;
		 	    	                		var special1=Ext.getCmp("special1").value;
		 	    	                		var special2=this.getValue();
		 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
		 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
		 	    	                		//	projectInfor.isselexpert.load();
		 	    	                		//	projectInfor.isselexpert2.load();
		 	    	            	  }
		 	    	             }
	    	        		//}]
	    	        	}]
	    	        },{
	    	        	layout:'column',
	    	        	border:false,
	    	        	fieldDefaults:{
	    	        		//labelWidth:140,
	    	        		//Width:120,
	    	        		//allowBlank:false
	    	        	},	
	    	        	items:[{
	    	        		columnWidth:.35,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        		bodyStyle:'padding-top:10px',
	    	        		//items:[{
	    	        			//labelWidth:140,
	    	        			xtype:'label',
	    	        			name:'zhiding',
	    	        			text:'3、本次指定以下专家'
	    	        	//	}]
	    	        	},projectInfor.personPanel,{
	    	        		columnWidth:.2,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        		bodyStyle : "padding-left:10px;padding-top:10px",
	    	        	//	items:[{
	    	        			  xtype : "label", 
		    	    	           // id:'proj_name',
		    	    	            //text:'添加专家',
	    	        			  
		    	    	          html:"<a href=javascript:projectInfor.addexpertPanel()>添加专家</a>"
	    	        	//	}]
	    	        	},{

	    	    		//	border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		  //  items:[{
	    	    			xtype: 'button',
	    	                text : '抽选专家',
	    	                handler:function(){
	    	                	//projectInfor.simpsonsStore.reload();
	    	                	 projectInfor.simpsonsStore.reload();
		    	                 //   var selected=projectInfor.issedexpert.getCount();
	    	                	 projectInfor.simpsonsStore1.reload();
	    	                	var right=parseInt(Ext.getCmp('right').value);
	    	                	var selected=parseInt(projectInfor.issedexpert.getCount());
	    	                	//alert(selected+" 65 "+right);
	    	                	//alert(right);
	    	                	if(selected>right){
	    	                		Ext.Msg.alert('提示','指定专家人数不能多于正选人数！');
	    	                		return;
	    	                	}
	    	                	while(projectInfor.isselexpert.getCount()>selected){
	    	                    	projectInfor.isselexpert.removeAt(selected);
	    	                    }
	    	                	var total=projectInfor.simpsonsStore.getCount();
	    	                	//alert(selected+' '+total)
	    	                	if(selected>0){
	    	                		right=right-projectInfor.issedexpert.getCount();
	    	                		/*projectInfor.issedexpert.each(function(rec){
	    	                			//alert(rec.data.a0100);	
	    	                			projectInfor.isselexpert.add(rec);
	    	                		}) */
	    	                		for(var i=0;i<projectInfor.simpsonsStore.getCount();i++){
	    	                			for(var j=0;j<projectInfor.issedexpert.getCount();j++){
	    	                				if(projectInfor.simpsonsStore.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
	    	                					projectInfor.simpsonsStore.removeAt(i);
	    	                					//continue;
	    	                					//alert(1);
	    	                				}//
	    	                			}
	    	                		}
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                			for(var j=0;j<projectInfor.issedexpert.getCount();j++){
	    	                				if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
	    	                					projectInfor.simpsonsStore1.removeAt(i);
	    	                					//continue;
	    	                					//alert(1);
	    	                				}//
	    	                			}
	    	                		}
	    	                	};
	    	                    while(projectInfor.isselexpert.getCount()>selected){
	    	                    	//alert(1);
	    	                    	projectInfor.isselexpert.removeAt(selected-1);
	    	                    }
	    	                	var spa=parseInt(Ext.getCmp('spa').value);
	    	                	var count=right+spa;  //所要抽选的总人数
	    	                	var length=projectInfor.simpsonsStore.getCount();
	    	                	//alert(count+"?"+length)
	    	                	if(count>length){
	    	                		Ext.MessageBox.alert('提示','抽选专家人数不能超过符合条件人数！');
	    	                	}else{
	    	                		Ext.getCmp('selzone1').show();
	    	                		projectInfor.isselexpert2.load();
	    	                		for(var i=0;i<count;i++){
	    	                		var j=parseInt(Math.random()*projectInfor.simpsonsStore.getCount());
	    	                		if(i<right){
	    	                		//	alert(projectInfor.simpsonsStore.getAt(j).get('a0100'));
                                        projectInfor.isselexpert.add({remark:'正选',a0100:projectInfor.simpsonsStore.getAt(j).get('a0100'),a0101:projectInfor.simpsonsStore.getAt(j).get('a0101'),
	    	                			dept:projectInfor.simpsonsStore.getAt(j).get('dept'),category:projectInfor.simpsonsStore.getAt(j).get('category'),special:projectInfor.simpsonsStore.getAt(j).get('special'),
	    	                			id:projectInfor.simpsonsStore.getAt(j).get('id'),tel:projectInfor.simpsonsStore.getAt(j).get('tel'),flag:projectInfor.simpsonsStore.getAt(j).get('flag'),phone:projectInfor.simpsonsStore.getAt(j).get('phone'),accept:''})
	    	                			projectInfor.simpsonsStore.removeAt(j);
                                        projectInfor.simpsonsStore1.removeAt(j);
	    	                		}else{	
	    	                		projectInfor.isselexpert.add({remark:'备选',a0100:projectInfor.simpsonsStore.getAt(j).get('a0100'),a0101:projectInfor.simpsonsStore.getAt(j).get('a0101'),
		    	                		dept:projectInfor.simpsonsStore.getAt(j).get('dept'),category:projectInfor.simpsonsStore.getAt(j).get('category'),special:projectInfor.simpsonsStore.getAt(j).get('special'),
		    	                		id:projectInfor.simpsonsStore.getAt(j).get('id'),tel:projectInfor.simpsonsStore.getAt(j).get('tel'),flag:projectInfor.simpsonsStore.getAt(j).get('flag'),phone:projectInfor.simpsonsStore.getAt(j).get('phone'),accept:''})
		    	                		projectInfor.simpsonsStore.removeAt(j);
	    	                		    projectInfor.simpsonsStore1.removeAt(j);
	    	                		}
	    	                		};
	    	                		
	    	                	}
	    	                }
	    	    	//	}]
	    	    		
	    	        	},{


	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		   // items:[{
	    	    			xtype: 'button',
	    	                text : '二次抽选',
	    	                handler:function(){
	    	                projectInfor.simpsonsStore1.reload();
                            
							projectInfor.isselexpert3.removeAll();
							var zhflag =  '0';
							if(projectInfor.isselexpert2.getCount()>0){
                            projectInfor.isselexpert2.each(function(recd){
                                 zhflag = '0';
								if(recd.data.accept == '是'){
								projectInfor.isselexpert.each(function(recs){
									if(recd.data.id == recs.data.id){
									zhflag = '1';
									}
								});
								if(zhflag == '0'){
								  projectInfor.isselexpert3.add(recd);
								}
									}
								
							});
							}
	    	                projectInfor.isselexpert2.removeAll();
	    	                var right=parseInt(Ext.getCmp('right').value);
	    	                var spa=parseInt(Ext.getCmp('spa').value);
	    	                var total=right+spa;
	    	                var length=projectInfor.isselexpert.getCount();
	    	                if(total==length){
	    	                var i=0;
	    	               for(var index=0;index<projectInfor.isselexpert.getCount();index++){
	    	                	if(projectInfor.isselexpert.getAt(index).get('accept')=='是'){
	    	                	i++;
	    	                	if(i<=right){
	    	                	projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.isselexpert.getAt(index).get('a0100'),a0101:projectInfor.isselexpert.getAt(index).get('a0101'),
    	                			dept:projectInfor.isselexpert.getAt(index).get('dept'),category:projectInfor.isselexpert.getAt(index).get('category'),special:projectInfor.isselexpert.getAt(index).get('special'),
    	                			id:projectInfor.isselexpert.getAt(index).get('id'),tel:projectInfor.isselexpert.getAt(index).get('tel'),flag:projectInfor.isselexpert.getAt(index).get('flag'),phone:projectInfor.isselexpert.getAt(index).get('phone'),
    	                			accept:'是'});
	    	                	}else{
	    	                		projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.isselexpert.getAt(index).get('a0100'),a0101:projectInfor.isselexpert.getAt(index).get('a0101'),
	    	                			dept:projectInfor.isselexpert.getAt(index).get('dept'),category:projectInfor.isselexpert.getAt(index).get('category'),special:projectInfor.simpsonsStore.getAt(index).get('special'),
	    	                			id:projectInfor.isselexpert.getAt(index).get('id'),tel:projectInfor.isselexpert.getAt(index).get('tel'),flag:projectInfor.isselexpert.getAt(index).get('flag'),phone:projectInfor.isselexpert.getAt(index).get('phone'),
	    	                			accept:'是'});	
	    	                	}
	    	                	}
	    	                }
                             if(projectInfor.isselexpert3.getCount()>0){
								 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
								 i++;
							 if(i<=right){
	    	                	projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.isselexpert3.getAt(index).get('a0100'),a0101:projectInfor.isselexpert3.getAt(index).get('a0101'),
    	                			dept:projectInfor.isselexpert3.getAt(index).get('dept'),category:projectInfor.isselexpert3.getAt(index).get('category'),special:projectInfor.isselexpert3.getAt(index).get('special'),
    	                			id:projectInfor.isselexpert3.getAt(index).get('id'),tel:projectInfor.isselexpert3.getAt(index).get('tel'),flag:projectInfor.isselexpert3.getAt(index).get('flag'),phone:projectInfor.isselexpert3.getAt(index).get('phone'),
    	                			accept:'是'});
	    	                	}else{
	    	                		projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.isselexpert3.getAt(index).get('a0100'),a0101:projectInfor.isselexpert3.getAt(index).get('a0101'),
	    	                			dept:projectInfor.isselexpert3.getAt(index).get('dept'),category:projectInfor.isselexpert3.getAt(index).get('category'),special:projectInfor.simpsonsStore.getAt(index).get('special'),
	    	                			id:projectInfor.isselexpert3.getAt(index).get('id'),tel:projectInfor.isselexpert3.getAt(index).get('tel'),flag:projectInfor.isselexpert3.getAt(index).get('flag'),phone:projectInfor.isselexpert3.getAt(index).get('phone'),
	    	                			accept:'是'});	
	    	                	}
								 }
							 }

	    	                 if((2*total-i)>(projectInfor.simpsonsStore.getCount())){
	    	                	 Ext.Msg.alert('提示','没有足够可供二次抽选的专家');
	    	                	 projectInfor.isselexpert2.removeAll();
	    	                	 return; 
	    	                 }else{
	    	                	 Ext.getCmp('selzone2').show();
	    	                 };
	    	                 total=total-projectInfor.isselexpert2.getCount();
	    	                 if(total>spa){
	    	                	 right=total-spa;
	    	                	// alert(right);
	    	                	 for(var index=0;index<projectInfor.isselexpert.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
								 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert3.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
	    	                	 for(var i=0;i<right;i++){
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                	 for(var i=0;i<spa;i++){		
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                	 //alert(projectInfor.simpsonsStore1.getCount());
	    	                 }else{
	    	                	 for(var index=0;index<projectInfor.isselexpert.getCount();index++){
 		    	                	
		    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
		    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert.getAt(index).get('a0100')){
		    	                		    	projectInfor.simpsonsStore1.removeAt(i);
		    	                		    	break;
		    	                		    	 }
		    	                		  }
		    	                	 }
									 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert3.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
	    	                	 for(var i=0;i<total;i++){
	    	                		 //alert(projectInfor.simpsonsStore1.getCount());
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                 }
	    	                }
	    	                else{
	    	                	Ext.Msg.alert('提示','还没有抽取专家');
	    	                }
	    	    		}
	    	        	//}]
	    	    		},{

	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		  //  items:[{
	    	    			xtype: 'button',
	    	                text : '撤选',
	    	                handler:function(){
	    	                    projectInfor.simpsonsStore.reload();
	    	                    var selected=projectInfor.issedexpert.getCount();
	    	                	while(projectInfor.isselexpert.getCount()>selected){
		    	                    	projectInfor.isselexpert.removeAt(selected);
		    	                    }
	    	                	projectInfor.isselexpert2.load();
		    	                 //   var total=
		    	                	/*for(var i=0;i<projectInfor.simpsonsStore.getCount();i++){
		    	                		for(var j=0;j<projectInfor.issedexpert.getCount();j++){
		    	                			if(projectInfor.simpsonsStore.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
		    	                				//projectInfor.simpsonsStore.add(projectInfor.simpsonsStore1.getAt(i));
		    	                				alert('OK');
		    	                			}
		    	                		}
		    	                	}*/
	    	                }
	    	    		//}]
	    	    		
	    	    		}
	    	        	]
	    	        }], // An array of form fields
	    	        flex: 2             // Use 2/3 of Container's height (hint to Box layout)
	    	    },{
	    	    	title:'抽选的专家',
	    	    	id:'selzone1',
	    	    	xtype:'fieldset',
	    	    	border:true,
	    	    	
	    	    	hidden:true,
	    	    	collapsible:true,
		    	    //layout:'form',
		    	    checkboxName:'description',
	    	    	layout:'form',
	    	    	items:[{
	    	    		border:false,
	    	    		layout:'column',
	    	    		//items:[{
		    	    		
		    	    		border:false,
		    	    		columnWidth:1,
		        	    //	xtype:'fieldset',
		        	    	//title: '第一次抽选的专家',
		        	    	items:[gridpanel1]
		    	    	//}]
	    	    	}]
	    	    },{

	    	    	title:'二次抽选的专家',
	    	    	id:'selzone2',
	    	    	xtype:'fieldset',
	    	    	border:true,
	    	    	hidden:true,
	    	    	collapsible:true,
		    	    //layout:'form',
		    	    checkboxName:'description',
	    	    	layout:'form',
	    	    	items:[{
	    	    		border:false,
	    	    		layout:'column',
	    	    		//items:[{
		    	    		
		    	    		border:false,
		    	    		columnWidth:1,
		        	    //	xtype:'fieldset',
		        	    	//title: '二次抽取的专家',
		        	    	items:[gridpanel2]
		    	    //	}]
	    	    	}]
	    	    
	    	    }],
	    	    buttonAlign : "center",
	    	    buttons:[{
	    	    	text:'保存',
	    	    	formBind:true,
	    	    	disable:true,
	    	    	handler:function(){
	    	    		var map = new HashMap();
	    	    		map.put('projectId',info.proj_id)
	    	    		map.put('opt', 'updproject');
	    	    	    var proj_name=Ext.getCmp('proj_name').value;
	    	    	   // alert(proj_name);
	    	    	    if(proj_name==""){
	    	    	    	Ext.MessageBox.alert('提示','项目名称不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	    	if(proj_name.length>50){
	 	    	    	       Ext.MessageBox.alert('提示','项目名称过长');
	 	    	    	       return;}
	    	    	       map.put('proj_name', proj_name);
	    	    	    }
	    	    	    var proj_content=Ext.getCmp('proj_content').value;
	    	    	  //  alert(proj_content);
	    	    	    if(proj_content==""){
	    	    	    	Ext.MessageBox.alert('提示','项目内容不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	    	if(proj_content.length>200){
	    	    	    		Ext.MessageBox.alert('提示','项目内容过长');
	    	    	    	}
	    	    	       map.put('proj_content', proj_content);
	    	    	    }
	    	    	    var need_dept=Ext.getCmp('need_dept').value;
	    	    	    if(need_dept==null){
	    	    	    	Ext.MessageBox.alert('提示','需求部门不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('need_dept', need_dept);
	    	    	    }
	    	    	    var manager_dept=Ext.getCmp('manager_dept').value;
	    	    	    if(manager_dept==null){
	    	    	    	Ext.MessageBox.alert('提示','主管部门不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('manager_dept', manager_dept);
	    	    	    }
	    	    	    var comment_addr=Ext.getCmp('comment_addr').value;
	    	    	    if(comment_addr==null || comment_addr==''){
	    	    	    	Ext.MessageBox.alert('提示','评标地点不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('comment_addr', comment_addr);
	    	    	    }
	    	    	    var comment_time=Ext.getCmp('comment_time').value;
	    	    	    if(comment_time==null){
	    	    	    	Ext.MessageBox.alert('提示','评标时间不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('comment_time', comment_time);
	    	    	    }
	    	    	    var sup_person=Ext.getCmp('sup_person').value;
	    	    	    if(sup_person==null || sup_person==''){
	    	    	    	Ext.MessageBox.alert('提示','抽取人不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('sup_person', sup_person);
	    	    	    }
	    	    	    var see_person=Ext.getCmp('see_person').value;
	    	    	    if(see_person==null || see_person==''){
	    	    	    	Ext.MessageBox.alert('提示','监督人不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('see_person', see_person);
	    	    	    }
	    	    	    var sup_time=Ext.getCmp('sup_time').value;
	    	    	    if(sup_time==null){
	    	    	    	Ext.MessageBox.alert('提示','抽取时间不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('sup_time', sup_time);
	    	    	    }
	    	    	    var invite_time=Ext.getCmp('invite_time').value;
	    	    	    if(invite_time==null){
	    	    	    	Ext.MessageBox.alert('提示','招标时间不能为空');
	    	    	    	return;
	    	    	    }else{
	    	    	       map.put('invite_time', invite_time);
	    	    	    }
	    	    	    var right=Ext.getCmp('right').value;
	    	    	    if(right!=null && right!=''){
	    	    	    	map.put('right',right);
	    	    	    }else{
	    	    	    	right=0;
	    	    	    }
	    	    	    var spa=Ext.getCmp('spa').value;
	    	    	    if(spa!=null && spa!=''){
	    	    	    	map.put('spa',spa);
	    	    	    }else{	    	    	    	
	    	    	    	spa=0;	    	    	    	
	    	    	    }
	    	    	    var category=Ext.getCmp('category').value;
	    	    	    if(category!=null){
	    	    	    	map.put('category',category);
	    	    	    }
	    	    	    var special1=Ext.getCmp('special1').value;
	    	    	    if(special1!=null){
	    	    	    	map.put('special1',special1);
	    	    	    }
	    	    	    var special2=Ext.getCmp('special2').value;
	    	    	    if(special2!=null){
	    	    	    	map.put('special2',special2);
	    	    	    }
	    	    	    
		    	    	   if(projectInfor.isselexpert.getCount()>0){
			    	    	     var selected=new Array();
			    	    	     projectInfor.isselexpert.each(function(record){
			    	    	    	 selected.push(record.data)
			    	    	     });
			    	    	     if(projectInfor.isselexpert.getCount()!=(parseInt(right)+parseInt(spa))){
			    	    	    	 Ext.Msg.alert('提示','所抽选专家人数与设定人数不符');
			    	    	    	 return;
			    	    	     }
			    	    	     map.put('selected1', selected)
			    	    	    }
			    	    	   if(projectInfor.isselexpert2.getCount()>0){
			    	    		   var selected=new Array();
				    	    	     projectInfor.isselexpert2.each(function(record){
				    	    	    	 selected.push(record.data)
				    	    	     });
				    	    	     if(projectInfor.isselexpert2.getCount()!=(parseInt(right)+parseInt(spa))){
				    	    	    	 Ext.Msg.alert('提示','所抽选专家人数与设定人数不符');
				    	    	    	 return;
				    	    	     }
				    	    	     map.put('selected2', selected) 
			    	    	   }
	    	    	   // alert(0);
	    	    	    Rpc({functionId:'RE100000005',success:function(form,action){
	    	    	    	Ext.getCmp('addprojectwid').destroy();
	    	    	    //	Ext.removeNode(document.getElementById('addprojectwid'));
	    	    	    	var store = Ext.data.StoreManager.lookup('re_project_dataStore');
	    	    			store.reload();
		    	    	}},map);
	    	    	}
	    	    },{
	    	    	text:'关闭',
	    	    	handler:function(){
	    	    		Ext.getCmp('addprojectwid').destroy();
	    	    	//	Ext.removeNode(document.getElementById('addprojectwid'));
	    	    	}
	    	    }]
		 });
		 if(info.times!=null){
			     if(info.times=='2'){
				 Ext.getCmp('selzone1').show();
				 Ext.getCmp('selzone2').show();
			     }else{
			     Ext.getCmp('selzone1').show();	 
			     }
				/* alert(info.times);
				 Ext.getCmp('selzone1').show();*/
			 
		 }
		 Ext.create('Ext.window.Window',{
	    	    title: '抽取专家',
	    	    id:'addprojectwid',
	    	   // closable: false,
	    	    closable:false,
	    	    layout:'fit',
	    	    width: 840,
	    	    height: 600,
	    	    renderTo: Ext.getBody(),
	    	    items: [projform],
	    	    modal :true,
                    maximizable:true,
	    	    autoScroll:true,
	    	    closeAction:'close'
	       }).show();
	},
//  展示项目详细信息
	onCellClick:function(tab,el,cellindex,rec,tr,rowindex,event,opt){
		 if(cellindex==1){
			 var map=new HashMap();
			 var ss=rec.data.n0301+'';
		     map.put('opt', 'selexpert');
		     map.put('projectIds', ss);
		    // alert(rec.data.n0301);
			// Rpc({functionId:'RE100000001',async:false,success:projectInfor.getTableOK},map);
			 Rpc({functionId:'RE100000005',async:false,success:projectInfor.checkInfor},map); 
		 }
		
	},
	checkInfor:function(form,action){
	//	alert(1);
		var result = Ext.decode(form.responseText);
		var info=result.data;
		var deptstore = Ext.create('Ext.data.Store', {
			fields:[{name:'deptdesc'},{name:'deptcode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"dept"
			        },
			        reader: {
			            type: 'json',
			            root: 'deptstr'         	
			        }
			},
			autoLoad: true
		});
		var catagoryStore=Ext.create('Ext.data.Store',{
			fields:[{name:'catagorydesc'},{name:'catagorycode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"catagory"
			        },
			        reader: {
			            type: 'json',
			            root: 'categorystr'         	
			        }
			},
			autoLoad: true
			
		});
		var nameStore=Ext.create('Ext.data.Store',{
			fields:[{name:'namedesc'},{name:'namecode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"name"
			        },
			        reader: {
			            type: 'json',
			            root: 'namestr'         	
			        }
			},
			autoLoad: true
			
		});
		var specialStore=Ext.create('Ext.data.Store',{
			fields:[{name:'specialdesc'},{name:'specialcode'}],
			proxy:{
			    	type: 'transaction',
			        functionId:'RE100000004',
			        extraParams:{
			        	"req":"special"
			        },
			        reader: {
			            type: 'json',
			            root: 'specialstr'         	
			        }
			},
			autoLoad: true
			
		});
		//符合条件的专家（显示）
		  projectInfor.simpsonsStore1=Ext.create('Ext.data.Store', {
			  //  storeId:'projectInfor.simpsonsStore',
			 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
				proxy:{
				    	type: 'transaction',
				        functionId:'RE100000004',
				        extraParams:{
				        	req:'selexpert',
				        	opt:'select',
				        	projectId:info.proj_id
				        },
				        reader: {
				            type: 'json',
				            root: 'expert'         	
				        }
				},
				autoLoad: true
			});
		  //符合条件的专家（调用计算）
		  projectInfor.simpsonsStore=Ext.create('Ext.data.Store', {
			  //  storeId:'projectInfor.simpsonsStore',
			 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
				proxy:{
				    	type: 'transaction',
				        functionId:'RE100000004',
				        extraParams:{
				        	req:'selexpert',
				        	opt:'select',
				        	projectId:info.proj_id
				        },
				        reader: {
				            type: 'json',
				            root: 'expert'         	
				        }
				},
				autoLoad: true
			});
		  
		  //第一次抽选的专家
		  projectInfor.isselexpert=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
					 req:'isselexpert',
					 opt:'sel1',
					 projectId:info.proj_id
				 },
				 reader:{
					 type:'json',
					 root:'selexpert'
				 }
			 }
		 });
		//第二次抽选的专家
		  projectInfor.isselexpert2=Ext.create('Ext.data.Store',{
			 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
			 proxy:{
				 type:'transaction',
				 functionId:'RE100000004',
				 extraParams:{
					 req:'isselexpert',
					 opt:'sel2',
					 projectId:info.proj_id
				 },
				 reader:{
					 type:'json',
					 root:'selexpert'
				 }
			 }
		 });
		  //指定的专家
		  projectInfor.issedexpert=Ext.create('Ext.data.Store',{
				 fields:['remark','a0100','a0101','category','dept','special','id','tel','flag','phone','accept'],
				 proxy:{
					 type:'transaction',
					 functionId:'RE100000004',
					 extraParams:{
						 req:'issedexpert',
						 opt:'sed',
						 projectId:info.proj_id
					 },
					 reader:{
						 type:'json',
						 root:'sedexpert'
					 }
				 },
				 idProperty : "id"
			 });
		  var gridpanel=Ext.create('Ext.grid.Panel', {
			    store: Ext.data.StoreManager.lookup(projectInfor.simpsonsStore1),
			    border:true,
			    columns: [
			         { header: '工号',  dataIndex: 'a0100',width:70 },
	            	    	        { header: '姓名', dataIndex: 'a0101',width:50 },
	            	    	        { header: '科别', dataIndex: 'category',width:60 },
	            	    	        { header: '科室',  dataIndex: 'dept',width:60 },
	            	    	        { header: '专业', dataIndex: 'special' ,width:60,renderer: function (v, m, r) {
	            	    	            m.attr = 'style="white-space:normal;word-wrap:break-word;word-break:break-all;"';
	            	    	            return v;
	            	    	        }}
			    ],
			    width:302,
			    height:260,
			    columnLines:true,
			    renderTo: Ext.getBody(),
			    scroll:true
			});
		  
		  /**
			  * 第一次抽选的专家面板
			  * 
			  */
			 var gridpanel1=Ext.create('Ext.grid.Panel', {
				    store: Ext.data.StoreManager.lookup(projectInfor.isselexpert),
				    border:true,
				    columns: [
	                     { header: '',  dataIndex: 'remark',width:40 },
				         { header: '工号',  dataIndex: 'a0100',width:80 },
		            	    	        { header: '姓名', dataIndex: 'a0101',width:80 },
		            	    	        { header: '科别', dataIndex: 'category',width:80 },
		            	    	        { header: '科室',  dataIndex: 'dept',width:90 },
		            	    	        { header: '电话', dataIndex: 'tel',width:100 },
		            	    	        { header: '座机/小灵通',  dataIndex: 'phone',width:100 },
		            	    	        { header: '专业', dataIndex: 'special' ,width:100},
		            	    	        { header:'是否参加',dataIndex:'accept',width:76
	                 }
				    ],
				    plugins: [
				              Ext.create('Ext.grid.plugin.CellEditing', {
				                  clicksToEdit: 1
				              })
				          ],
				    width:766,
				    height:334,
				    columnLines:true,
				    renderTo: Ext.getBody(),
				    scroll:true
				});
			 /**
			  * 第二次抽选的专家的面板
			  */
			 var gridpanel2=Ext.create('Ext.grid.Panel', {
				    store: Ext.data.StoreManager.lookup(projectInfor.isselexpert2),
				    border:true,
				    columns: [
	                  { header: '',  dataIndex: 'remark',width:40 },
				         { header: '工号',  dataIndex: 'a0100',width:80 },
		            	    	        { header: '姓名', dataIndex: 'a0101',width:80 },
		            	    	        { header: '科别', dataIndex: 'category',width:80 },
		            	    	        { header: '科室',  dataIndex: 'dept',width:90 },
		            	    	        { header: '电话', dataIndex: 'tel',width:100 },
		            	    	        { header: '座机/小灵通',  dataIndex: 'phone',width:100 },
		            	    	        { header: '专业', dataIndex: 'special' ,width:100},
		            	    	        { header:'是否参加',dataIndex:'accept',width:76}
				    ],
				    plugins: [
				              Ext.create('Ext.grid.plugin.CellEditing', {
				                  clicksToEdit: 1
				              })
				          ],
				    width:766,
				    height:334,
				    columnLines:true,
				    renderTo: Ext.getBody(),
				    scroll:true
				});
		 projectInfor.personPanel = Ext.widget('panel',{
			 id:'personPanel',
				layout:{
					type:'hbox'
				},
				bodyStyle:'padding-top:5px',
				width:200,
				height:60,
				margin: 0,
				border:false,
				items:[]
		 });
		 for(var i=0;i<info.list.length;i++){
			  var split=info.list[i].split(",");
			 var imgUsr = 'usr'; //人员库前缀
				var imgA0100 = split[0];   //人员编号
				var imgQuality = 'l';  // 照片质量 l 低分辨看  h 高分辨率 
				var personImg = Ext.create('Ext.Img', {
					id:'pho_'+imgA0100,
				    src: "/servlet/DisplayOleContent?nbase="+imgUsr+"&a0100="+imgA0100+"&sanyuan=1&quality="+imgQuality,
				    style:'border-radius: 50%;',
				    width:25,
				    height:25,
				    padding:0,
				    margin:'0 15 0 0',
				    listeners: {
				        mouseover: {
				            element: 'el', 
				            fn: function(a, o){ projectInfor.showHideDelImg(o.id, "1"); }
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ projectInfor.showHideDelImg(o.id,"0"); }
				        }
					}
				});
/*				var delImg = Ext.create('Ext.Img', {
					src: "/workplan/image/remove.png",
					id:'del_'+imgA0100,
					style:'cursor:pointer;',
					width:15,
					height:15,
					hidden:true,
					cls:'delImg',
					listeners: {
				        click: {
				            element: 'el', 
				            fn: function(a, o, b, c){ projectInfor.deletePerson(o.id); }
				        }, mouseover: {
				            element: 'el', 
				            fn: function(a, o){ projectInfor.showHideDelImg(o.id, "1"); }
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ projectInfor.showHideDelImg(o.id,"0"); }
				        }
					}
				});*/
				var person = Ext.widget('panel',{
					id:'per_'+imgA0100,
					layout:{
						type:'vbox',
						align:'center'
					},
					margin: 0,
					border:false,
					items:[personImg,{xtype: 'label',text: split[1],margin:0}]
				});
				
				projectInfor.personPanel.add(person);
		 };
		 projectInfor.simpsonsStore.reload();
		 projectInfor.simpsonsStore1.reload();
		 projectInfor.issedexpert.reload();
		 projectInfor.isselexpert.reload();
		 projectInfor.isselexpert2.reload();
/*	     
		 }*/
		// projectInfor.issedexpert.reload();
		// alert(projectInfor.issedexpert.getCount());
		 deptstore.reload();
		// alert(deptstore.getCount());
		 catagoryStore.reload();
		 specialStore.reload(); 
		 nameStore.reload();
		 var projform=Ext.create('Ext.form.Panel',{
			 bodyPadding:5,
			 width:790,
			 url:'',
			 autoScroll:true,
			 formBind: true,
			 layout:'anchor',
			 border:false,
			 items:[{               // Results grid specified as a config object with an xtype of 'grid'
	    	        title:'招标项目信息',
	    	        xtype:'fieldset',
	    	       // bodyPadding: 5,
	    	        collapsible:true,
	    	        layout:'form',
	    	        checkboxName:'description',
	    	        //defaultType:'textfield',
	    	        fieldDefaults:{
	    	        	 labelSeparator :'',
	    	        	 	labelWidth:60,
	    	        	 Width:100,
	    	        	 readOnly:true
	    	        	// allowBlank:false
	    	        },
	    	        items: [{
	    	        	bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.6,
	 	        		// layout:'form',
	 	        		border:false,
	 	        		//items:[{
	 	        		//	allowBlank:false,	
	    	            xtype : "textfield", 
	    	            id:'proj_name',
	    	            fieldLabel:'项目名称',
	    	            	value:info.proj_name
	    	          //  blankText:'dada'
	 	        		//}]
	 	        	}]
	    	        }, {
	    	        	bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.6,
	 	        		// layout:'form',
	 	        		//border:false,
	 	        		//items:[{
	    	        //	id:'proj_content',
	    	            fieldLabel: '项目内容', 
	    	            xtype:'textarea',
	    	            id:'proj_content',
	    	            value:info.proj_content,
	    	            height:50
	 	        		//}]
	 	        	    },{ columnWidth:0.38,
	 	        	    	//bodyStyle : "padding-top: 0px; padding-left:10px;",
	 	 	        	    layout:'vbox',
	 	 	        		border:false,
	 	 	        		//items:[{
			 	        	//	 layout:'form',
			 	        	//	border:false,
			 	        		items:[{
			 	        		    xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	            width:300,
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'need_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;需求部门',
			 	    	           // value:'dada',
			 	    	          value:info.need_dept,
			 	    	            listeners:{
			 	    	            	/*'change':function(){
			 	    	            		deptstore.load({params:{selitem:this.getValue()}}); 
			 	    	            	}*/
			 	    	            }
			 	        		},{
			 	        			
			 	        			xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	           width:300,
			 	    	           bodyStyle:'padding-left:5px',
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'manager_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;主管部门',
			 	    	          value:info.manager_dept
			 	        			
			 	        		}]
	 	    	         
	 	 	        		/*},{	
			 	        		 layout:'form',
			 	        		border:false,
			 	        		items:[{
			 	        		   xtype:'combo',
			 	    	            listConfig:{
			 	    	            	emptyText:'未找到匹配值',
			 	    	            	maxHeight:120
			 	    	            },
			 	    	           bodyStyle:'padding-left:5px',
			 	    	            triggerAction:'all',
			 	    	            store:deptstore,
			 	    	            displayField:'deptdesc',
			 	    	            valueField:'deptcode',
			 	    	            queryMode:'local',
			 	    	            //forceSelection:true,
			 	    	            typeAhead:true,
			 	    	            id:'manager_dept',
			 	    	           fieldLabel: '&nbsp;&nbsp;主管部门',
			 	    	           // value:'dada',
			 	    	            listeners:{
			 	    	            	/*'change':function(){
			 	    	            		deptstore.load({params:{selitem:this.getValue()}}); 
			 	    	            	}*/
			 	    	          //  }
			 	        		//}]*/
	 	    	         
	 	 	        		//}]
	 	        	    }]
	    	        },{
	    	        	layout:'column',
	    	        	//bodyStyle : "padding-top: -5px; padding-left:0px;",
	    	        	border:false,
	 	        	    items:[{
		 	        		 columnWidth:0.3,
		 	        		// layout:'form',
		 	        		//border:false,
		 	        		//items:[{
		 	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	 	 	    	            fieldLabel: '抽取人',
	 	 	    	            triggerAction:'all',
	 	    	                store:nameStore,
	 	    	                displayField:'namedesc',
	 	    	                valueField:'namecode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	               value:info.sup_person,
		 	        		//	xtype:'textfield',
		 	        			//fieldLabel:'抽取人',
		 	        			id:'sup_person'
		 	        		//}]
		 	        	},{
		 	        		columnWidth:0.3,
		 	        	//	layout:'form',
		 	        	//	border:false,
		 	        		//items:[{
		 	        			xtype:'combo',
  	 	 	    	            listConfig:{
  	 	 	    	            	emptyText:'未找到匹配值',
  	 	 	    	            	maxHeight:120
  	 	 	    	            },
  	 	 	    	        
 	 	    	            fieldLabel: '&nbsp;&nbsp;&nbsp;&nbsp;监督人',
 	 	    	            triggerAction:'all',
 	    	                store:nameStore,
 	    	                displayField:'namedesc',
 	    	                valueField:'namecode',
 	    	                queryMode:'local',
 	    	                //forceSelection:true,
 	    	                typeAhead:true,
		 	        			id:'see_person',
		 	        			value:info.see_person
		 	        	//	}]
		 	        	},{
		 	        		columnWidth:0.38,
		 	        		// layout:'form',
		 	        	//	border:false,
		 	        		//items:[{
		    	        	 xtype : "textfield", 
		    	            id:'comment_addr',
		    	            fieldLabel:'&nbsp;&nbsp;评标地点',
		    	            value:info.comment_addr
		 	        	//	}]
		 	        	    }]
	    	        },    /* {
	    	        	layout:'column',
	    	        	border:false,
	 	        	items:[{
	 	        		 columnWidth:0.3,
	 	        		 layout:'form',
	 	        		border:false,
	 	        		items:[{
	 	        			xtype:'textfield',
	 	        			fieldLabel:'抽取人',
	 	        			id:'sup_person'
	 	        		}]
	 	        	},{
	 	        		columnWidth:0.3,
	 	        		layout:'form',
	 	        		border:false,
	 	        		items:[{
	 	        			xtype:'textfield',
	 	        			fieldLabel:'&nbsp;&nbsp;&nbsp;&nbsp;监督人',
	 	        			id:'see_person'
	 	        		}]
	 	        	},{
		        	    	
		        	    	columnWidth:0.4,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	    layout:'form',
		 	        		border:false,
		 	        		items:[{
		    	        	id:'sup_time',
		    	        	format:'Y年m月d日',
		    	        	xtype:'datefield',
		    	            fieldLabel: '&nbsp;&nbsp;抽取时间'
		    	          //  value:new Date()
		    	           
		        	    }]
	 	     
	     	    }]
	 	        },*/
	    	        {
	 	        	layout:'column',
		        	border:false,
		        	    items:[{
		        	    	
		        	    	columnWidth:0.3,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	 //   layout:'form',
		 	        	//	border:false,
		 	        	//	items:[{
		    	        	id:'sup_time',
		    	        	format:'Y年m月d日',
		    	        	xtype:'datefield',
		    	            fieldLabel: '抽取时间',
		    	            value:new Date()
		    	           
		        	  //  }]
	 	     
	     	    },{
		        	    	columnWidth:0.3,
		        	    	//bodyStyle : "padding-top: 25px; padding-left:0px;",
		 	        	 //   layout:'form',
		 	        		//border:false,
		 	        		//items:[{
		    	        	id:'invite_time',
		    	        	xtype:'datefield',
		    	        	format:'Y年m月d日',
		    	            fieldLabel: '&nbsp;&nbsp;招标时间',
		    	            value:info.invite_time
		    	            //value:new Date()
		 	        	//	}]	
		        	    	
		        	    },{
		        	    	
		        	    	columnWidth:0.38,
		        	    	//bodyStyle : "padding-top: 55px; padding-left:10px;",
		 	        	//    layout:'form',
		 	        	//	border:false,
		 	        	//	items:[{
		    	        	name:'time',
		    	        	xtype:'datefield',
		    	        	format:'Y年m月d日',
		    	        	id:'comment_time',
		    	            fieldLabel: '&nbsp;&nbsp;评标时间',
			    	            value:info.comment_time
		        	   // }]
	 	     
	     	    }]
	    	        }
	    	        ],// A dummy empty data store
	    	        flex: 2                                       // Use 1/3 of Container's height (hint to Box layout)
	    	    }, {                    // Details Panel specified as a config object (no xtype defaults to 'panel').
	    	        title: '专家抽取条件',
	    	        xtype:'fieldset',
	    	        bodyPadding: 5,
	    	        collapsible:true,
	    	        checkboxName:'description',
	    	        layout:'form',
	    	        fieldDefaults:{
	    	        	labelSeparator :'',
	    	        	labelWidth:80,
	    	        	Width:100,
	    	        	readOnly:true
	    	        	// allowBlank:false
	    	        },
	    	        items: [{
	    	        	//allowBlank:true,
	    	        	layout:'column',
	    	        	border:false,
	 	        	    items:[{
	 	        		 columnWidth:0.2,
	 	        		// layout:'form',
	 	        	//	border:false,
	 	        		//items:[{
	    	            xtype : 'spinnerfield',
	    	            id:"right",
	    	            name:'off_num',
	    	            fieldLabel:'1、正选人数',
	    	            value:info.right,
	    	            onSpinUp:function(){
	    	            	var rightCmp=Ext.getCmp('right');
	    	            	//if(rightCmp.getValue)
	    	            	rightCmp.setValue(Number(rightCmp.getValue())+1);
	    	            },
	    	            onSpinDown:function(){
	    	            	var rightCmp=Ext.getCmp('right');
	    	            	if(rightCmp.value<1){
	    	            		return;
	    	            	}
	    	            	rightCmp.setValue(Number(rightCmp.getValue())-1);
	    	            }
	 	        	//	}]
	 	        	},{
	 	        		 columnWidth:0.26,
	 	        	//	 layout:'form',
	 	        	//	border:false,
	 	        	//	items:[{
	    	            xtype : "spinnerfield", 
	    	            labelWidth:120,
	    	            id:'spa',
	    	            value:info.spa, //初始化字段
	    	            name:'spa_num',
	    	            fieldLabel:'人，&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备选人数',
	    	            onSpinUp:function(){
	    	            	var spaCmp=Ext.getCmp('spa');  //通过组件ID获取组件对象
	    	            	//增加默认值
	    	            	spaCmp.setValue(Number(spaCmp.getValue())+1);
	    	            },
	 	        		onSpinDown:function(){
	 	        			var  spaCmp=Ext.getCmp('spa');
	 	        			if(spaCmp.value<1){
	    	            		return;
	    	            	}
	 	        			spaCmp.setValue(Number(spaCmp.getValue())-1);
	 	        		}
	 	        	//	}]
	 	        	},{
	 	        		columnWidth:0.05,
	 	        		//bodyStyle : "padding-top: 55px; padding-left:10px;",
	 	        		bodyStyle:'padding-top:4px',
	 	        		//layout:'form',
	 	        	//	border:false,
	 	        	//	items:[{
	 	        			xtype:'label',
	 	        			text:'人'
	 	        	//	}]
	 	        	}]
	    	        },{
	    	        	layout:'column',
	    	        	border:false,
	    	        	fieldDefault:{
	    	        		//labelWidth:80,
	    	        	   // allowBlank:false
	    	        	},
	    	        	items:[{
	    	        		columnWidth:.3,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        	//	items:[{
	    	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        id:'category',
	  	 	 	    	        name:'catagory',
	 	 	    	            fieldLabel: '2、所属科别',
	 	 	    	            triggerAction:'all',
	 	    	                store:catagoryStore,
	 	    	                displayField:'catagorydesc',
	 	    	                valueField:'catagorycode',
	 	    	                queryMode:'local',
	 	    	               // forceSelection:true,
	 	    	                typeAhead:true,
	 	    	               value:info.category,
	 	    	                listeners:{
	 	    	                	'change':function(){
	 	    	                	/*	var rt=parseInt(Ext.getCmp("right").value);
	 	    	                		var spa=parseInt(Ext.getCmp("spa").value);
	 	    	                		var sum=rt+spa;*/
	 	    	                		var special1=Ext.getCmp("special1").value;
	 	    	                		var special2=Ext.getCmp("special2").value;
	 	    	                		var category=this.getValue();
	 	    	                		   // alert(category);
	 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			//projectInfor.isselexpert.load();
	 	    	                			//projectInfor.isselexpert2.load();
	 	    	                			
	 	    	                	}
	 	    	                }
	    	        			
	    	        		//}]
	    	        	},{
	    	        		columnWidth:.22,
	    	        		//layout:'form',
	    	        		bodyStyle:'padding-left:13',
	    	        	//	border:false,
	    	        	//	items:[{
	    	        			 xtype:'combo',
	    	        			 labelWidth:40,
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        name:'need_dpt',
	 	 	    	            fieldLabel: '专业',
	 	 	    	            triggerAction:'all',
	 	    	                store:specialStore,
	 	    	                displayField:'specialdesc',
	 	    	                valueField:'specialcode',
	 	    	                queryMode:'local',
	 	    	               value:info.special1,
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                //value:'dada',
	 	    	                id:'special1',
	 	    	               listeners:{
	 	    	            	  'change':function(){
	 	    	                		//var rt=parseInt(Ext.getCmp("right").value);
	 	    	                		//var spa=parseInt(Ext.getCmp("spa").value);
	 	    	                	//	var sum=rt+spa;
	 	    	                		var category=Ext.getCmp("category").value;
	 	    	                		var special2=Ext.getCmp("special2").value;
	 	    	                		var special1=this.getValue();
	 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
	 	    	                		//	projectInfor.isselexpert.load();
	 	    	                		//	projectInfor.isselexpert2.load();
	 	    	            	  }
	 	    	             }
	    	        	//	}]
	    	        	},{
	    	        		columnWidth:.05,
	    	        		//layout:'form',
	    	        		bodyStyle:'padding-left:13;padding-top:5',
	    	        		border:false,
	    	        		//items:[{
	    	        			//labelWidth:40,
	    	        			xtype:'label',
	    	        			text:'或'
	    	        		//}]
	    	        	},{
	    	        		columnWidth:.14,
	    	        	//	layout:'form',
	    	        		bodyStyle:'padding-left:0',
	    	        	//	border:false,
	    	        		//items:[{
	    	        			 xtype:'combo',
	  	 	 	    	            listConfig:{
	  	 	 	    	            	emptyText:'未找到匹配值',
	  	 	 	    	            	maxHeight:120
	  	 	 	    	            },
	  	 	 	    	        name:'need_dpt',
	  	 	 	    	        id:'special2',
	 	 	    	            fieldLabel: '',
	 	 	    	            triggerAction:'all',
	 	    	                store:specialStore,
	 	    	                displayField:'specialdesc',
	 	    	                valueField:'specialcode',
	 	    	                queryMode:'local',
	 	    	                //forceSelection:true,
	 	    	                typeAhead:true,
	 	    	                //value:'dada'
	 	    	               value:info.special2,
	 	    	               listeners:{
		 	    	            	  'change':function(){
		 	    	                		var category=Ext.getCmp("category").value;
		 	    	                		var special1=Ext.getCmp("special1").value;
		 	    	                		var special2=this.getValue();
		 	    	                			projectInfor.simpsonsStore.reload({params:{category:category,special1:special1,special2:special2}});
		 	    	                			projectInfor.simpsonsStore1.reload({params:{category:category,special1:special1,special2:special2}});
		 	    	                		//	projectInfor.isselexpert.load();
		 	    	                		//	projectInfor.isselexpert2.load();
		 	    	            	  }
		 	    	             }
	    	        		//}]
	    	        	}]
	    	        },{
	    	        	layout:'column',
	    	        	border:false,
	    	        	fieldDefaults:{
	    	        		//labelWidth:140,
	    	        		//Width:120,
	    	        		//allowBlank:false
	    	        	},	
	    	        	items:[{
	    	        		columnWidth:.35,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        		bodyStyle:'padding-top:10px',
	    	        		//items:[{
	    	        			//labelWidth:140,
	    	        			xtype:'label',
	    	        			name:'zhiding',
	    	        			text:'3、本次指定以下专家'
	    	        	//	}]
	    	        	},projectInfor.personPanel,{
	    	        		columnWidth:.2,
	    	        	//	layout:'form',
	    	        	//	border:false,
	    	        		bodyStyle : "padding-left:10px;padding-top:10px",
	    	        	//	items:[{
	    	        			  xtype : "label", 
		    	    	           // id:'proj_name',
		    	    	            //text:'添加专家',
	    	        			  
		    	    	        //  html:"<a href=javascript:projectInfor.addexpertPanel()>添加专家</a>"
	    	        	//	}]
	    	        	},{

	    	    		//	border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		  //  items:[{
	    	    			xtype: 'button',
	    	                text : '抽选专家',
	    	                handler:function(){
	    	                	//projectInfor.simpsonsStore.reload();
	    	                	 projectInfor.simpsonsStore.reload();
		    	                 //   var selected=projectInfor.issedexpert.getCount();
	    	                	 projectInfor.simpsonsStore1.reload();
	    	                	var right=parseInt(Ext.getCmp('right').value);
	    	                	var selected=parseInt(projectInfor.issedexpert.getCount());
	    	                	//alert(selected+" 65 "+right);
	    	                	//alert(right);
	    	                	if(selected>right){
	    	                		Ext.Msg.alert('提示','指定专家人数不能多于正选人数！');
	    	                		return;
	    	                	}
	    	                	while(projectInfor.isselexpert.getCount()>selected){
	    	                    	projectInfor.isselexpert.removeAt(selected);
	    	                    }
	    	                	var total=projectInfor.simpsonsStore.getCount();
	    	                	//alert(selected+' '+total)
	    	                	if(selected>0){
	    	                		right=right-projectInfor.issedexpert.getCount();
	    	                		/*projectInfor.issedexpert.each(function(rec){
	    	                			//alert(rec.data.a0100);	
	    	                			projectInfor.isselexpert.add(rec);
	    	                		}) */
	    	                		for(var i=0;i<projectInfor.simpsonsStore.getCount();i++){
	    	                			for(var j=0;j<projectInfor.issedexpert.getCount();j++){
	    	                				if(projectInfor.simpsonsStore.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
	    	                					projectInfor.simpsonsStore.removeAt(i);
	    	                					//continue;
	    	                					//alert(1);
	    	                				}//
	    	                			}
	    	                		}
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                			for(var j=0;j<projectInfor.issedexpert.getCount();j++){
	    	                				if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
	    	                					projectInfor.simpsonsStore1.removeAt(i);
	    	                					//continue;
	    	                					//alert(1);
	    	                				}//
	    	                			}
	    	                		}
	    	                	};
	    	                    while(projectInfor.isselexpert.getCount()>selected){
	    	                    	//alert(1);
	    	                    	projectInfor.isselexpert.removeAt(selected-1);
	    	                    }
	    	                	var spa=parseInt(Ext.getCmp('spa').value);
	    	                	var count=right+spa;  //所要抽选的总人数
	    	                	var length=projectInfor.simpsonsStore.getCount();
	    	                	//alert(count+"?"+length)
	    	                	if(count>length){
	    	                		Ext.MessageBox.alert('提示','抽选专家人数不能超过符合条件人数！');
	    	                	}else{
	    	                		Ext.getCmp('selzone1').show();
	    	                		projectInfor.isselexpert2.load();
	    	                		for(var i=0;i<count;i++){
	    	                		var j=parseInt(Math.random()*projectInfor.simpsonsStore.getCount());
	    	                		if(i<right){
	    	                		//	alert(projectInfor.simpsonsStore.getAt(j).get('a0100'));
                                        projectInfor.isselexpert.add({remark:'正选',a0100:projectInfor.simpsonsStore.getAt(j).get('a0100'),a0101:projectInfor.simpsonsStore.getAt(j).get('a0101'),
	    	                			dept:projectInfor.simpsonsStore.getAt(j).get('dept'),category:projectInfor.simpsonsStore.getAt(j).get('category'),special:projectInfor.simpsonsStore.getAt(j).get('special'),
	    	                			id:projectInfor.simpsonsStore.getAt(j).get('id'),tel:projectInfor.simpsonsStore.getAt(j).get('tel'),flag:projectInfor.simpsonsStore.getAt(j).get('flag'),phone:projectInfor.simpsonsStore.getAt(j).get('phone'),accept:''})
	    	                			projectInfor.simpsonsStore.removeAt(j);
                                        projectInfor.simpsonsStore1.removeAt(j);
	    	                		}else{	
	    	                		projectInfor.isselexpert.add({remark:'备选',a0100:projectInfor.simpsonsStore.getAt(j).get('a0100'),a0101:projectInfor.simpsonsStore.getAt(j).get('a0101'),
		    	                		dept:projectInfor.simpsonsStore.getAt(j).get('dept'),category:projectInfor.simpsonsStore.getAt(j).get('category'),special:projectInfor.simpsonsStore.getAt(j).get('special'),
		    	                		id:projectInfor.simpsonsStore.getAt(j).get('id'),tel:projectInfor.simpsonsStore.getAt(j).get('tel'),flag:projectInfor.simpsonsStore.getAt(j).get('flag'),phone:projectInfor.simpsonsStore.getAt(j).get('phone'),accept:''})
		    	                		projectInfor.simpsonsStore.removeAt(j);
	    	                		    projectInfor.simpsonsStore1.removeAt(j);
	    	                		}
	    	                		};
	    	                		
	    	                	}
	    	                }
	    	    	//	}]
	    	    		
	    	        	},{


	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		   // items:[{
	    	    			xtype: 'button',
	    	                text : '二次抽选',
	    	                handler:function(){
	    	                projectInfor.simpsonsStore1.reload();
                            
							projectInfor.isselexpert3.removeAll();
							var zhflag =  '0';
							if(projectInfor.isselexpert2.getCount()>0){
                            projectInfor.isselexpert2.each(function(recd){
                                 zhflag = '0';
								if(recd.data.accept == '是'){
								projectInfor.isselexpert.each(function(recs){
									if(recd.data.id == recs.data.id){
									zhflag = '1';
									}
								});
								if(zhflag == '0'){
								  projectInfor.isselexpert3.add(recd);
								}
									}
								
							});
							}
	    	                projectInfor.isselexpert2.removeAll();
	    	                var right=parseInt(Ext.getCmp('right').value);
	    	                var spa=parseInt(Ext.getCmp('spa').value);
	    	                var total=right+spa;
	    	                var length=projectInfor.isselexpert.getCount();
	    	                if(total==length){
	    	                var i=0;
	    	               for(var index=0;index<projectInfor.isselexpert.getCount();index++){
	    	                	if(projectInfor.isselexpert.getAt(index).get('accept')=='是'){
	    	                	i++;
	    	                	if(i<=right){
	    	                	projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.isselexpert.getAt(index).get('a0100'),a0101:projectInfor.isselexpert.getAt(index).get('a0101'),
    	                			dept:projectInfor.isselexpert.getAt(index).get('dept'),category:projectInfor.isselexpert.getAt(index).get('category'),special:projectInfor.isselexpert.getAt(index).get('special'),
    	                			id:projectInfor.isselexpert.getAt(index).get('id'),tel:projectInfor.isselexpert.getAt(index).get('tel'),flag:projectInfor.isselexpert.getAt(index).get('flag'),phone:projectInfor.isselexpert.getAt(index).get('phone'),
    	                			accept:'是'});
	    	                	}else{
	    	                		projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.isselexpert.getAt(index).get('a0100'),a0101:projectInfor.isselexpert.getAt(index).get('a0101'),
	    	                			dept:projectInfor.isselexpert.getAt(index).get('dept'),category:projectInfor.isselexpert.getAt(index).get('category'),special:projectInfor.simpsonsStore.getAt(index).get('special'),
	    	                			id:projectInfor.isselexpert.getAt(index).get('id'),tel:projectInfor.isselexpert.getAt(index).get('tel'),flag:projectInfor.isselexpert.getAt(index).get('flag'),phone:projectInfor.isselexpert.getAt(index).get('phone'),
	    	                			accept:'是'});	
	    	                	}
	    	                	}
	    	                }
                             if(projectInfor.isselexpert3.getCount()>0){
								 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
								 i++;
							 if(i<=right){
	    	                	projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.isselexpert3.getAt(index).get('a0100'),a0101:projectInfor.isselexpert3.getAt(index).get('a0101'),
    	                			dept:projectInfor.isselexpert3.getAt(index).get('dept'),category:projectInfor.isselexpert3.getAt(index).get('category'),special:projectInfor.isselexpert3.getAt(index).get('special'),
    	                			id:projectInfor.isselexpert3.getAt(index).get('id'),tel:projectInfor.isselexpert3.getAt(index).get('tel'),flag:projectInfor.isselexpert3.getAt(index).get('flag'),phone:projectInfor.isselexpert3.getAt(index).get('phone'),
    	                			accept:'是'});
	    	                	}else{
	    	                		projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.isselexpert3.getAt(index).get('a0100'),a0101:projectInfor.isselexpert3.getAt(index).get('a0101'),
	    	                			dept:projectInfor.isselexpert3.getAt(index).get('dept'),category:projectInfor.isselexpert3.getAt(index).get('category'),special:projectInfor.simpsonsStore.getAt(index).get('special'),
	    	                			id:projectInfor.isselexpert3.getAt(index).get('id'),tel:projectInfor.isselexpert3.getAt(index).get('tel'),flag:projectInfor.isselexpert3.getAt(index).get('flag'),phone:projectInfor.isselexpert3.getAt(index).get('phone'),
	    	                			accept:'是'});	
	    	                	}
								 }
							 }

	    	                 if((2*total-i)>(projectInfor.simpsonsStore.getCount())){
	    	                	 Ext.Msg.alert('提示','没有足够可供二次抽选的专家');
	    	                	 projectInfor.isselexpert2.removeAll();
	    	                	 return; 
	    	                 }else{
	    	                	 Ext.getCmp('selzone2').show();
	    	                 };
	    	                 total=total-projectInfor.isselexpert2.getCount();
	    	                 if(total>spa){
	    	                	 right=total-spa;
	    	                	// alert(right);
	    	                	 for(var index=0;index<projectInfor.isselexpert.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
								 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert3.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
	    	                	 for(var i=0;i<right;i++){
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'正选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                	 for(var i=0;i<spa;i++){		
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                	 //alert(projectInfor.simpsonsStore1.getCount());
	    	                 }else{
	    	                	 for(var index=0;index<projectInfor.isselexpert.getCount();index++){
 		    	                	
		    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
		    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert.getAt(index).get('a0100')){
		    	                		    	projectInfor.simpsonsStore1.removeAt(i);
		    	                		    	break;
		    	                		    	 }
		    	                		  }
		    	                	 }
									 for(var index=0;index<projectInfor.isselexpert3.getCount();index++){
	    	                		    	                	
	    	                		for(var i=0;i<projectInfor.simpsonsStore1.getCount();i++){
	    	                		  if(projectInfor.simpsonsStore1.getAt(i).get('a0100')==projectInfor.isselexpert3.getAt(index).get('a0100')){
	    	                		    	projectInfor.simpsonsStore1.removeAt(i);
	    	                		    	break;
	    	                		    	 }
	    	                		  }
	    	                	 }
	    	                	 for(var i=0;i<total;i++){
	    	                		 //alert(projectInfor.simpsonsStore1.getCount());
	    	                		 var index=parseInt(Math.random()*projectInfor.simpsonsStore1.getCount());
	    	                		 projectInfor.isselexpert2.add({remark:'备选',a0100:projectInfor.simpsonsStore1.getAt(index).get('a0100'),a0101:projectInfor.simpsonsStore1.getAt(index).get('a0101'),
	     	                			dept:projectInfor.simpsonsStore1.getAt(index).get('dept'),category:projectInfor.simpsonsStore1.getAt(index).get('category'),special:projectInfor.simpsonsStore1.getAt(index).get('special'),
	     	                			id:projectInfor.simpsonsStore1.getAt(index).get('id'),tel:projectInfor.simpsonsStore1.getAt(index).get('tel'),flag:projectInfor.simpsonsStore1.getAt(index).get('flag'),phone:projectInfor.simpsonsStore1.getAt(index).get('phone'),
	     	                			accept:''});
	    	                		 projectInfor.simpsonsStore1.removeAt(index);
	    	                		 
	    	                	 }
	    	                 }
	    	                }
	    	                else{
	    	                	Ext.Msg.alert('提示','还没有抽取专家');
	    	                }
	    	    		}
	    	        	//}]
	    	    		},{

	    	    			border:false,
	    	    			bodyStyle : "padding-left:10px;padding-top:10px",
	    	    			labelAlign:'middle',
	    	    		  //  items:[{
	    	    			xtype: 'button',
	    	                text : '撤选',
	    	                handler:function(){
	    	                    projectInfor.simpsonsStore.reload();
	    	                    var selected=projectInfor.issedexpert.getCount();
	    	                	while(projectInfor.isselexpert.getCount()>selected){
		    	                    	projectInfor.isselexpert.removeAt(selected);
		    	                    }
	    	                	projectInfor.isselexpert2.load();
		    	                 //   var total=
		    	                	/*for(var i=0;i<projectInfor.simpsonsStore.getCount();i++){
		    	                		for(var j=0;j<projectInfor.issedexpert.getCount();j++){
		    	                			if(projectInfor.simpsonsStore.getAt(i).get('a0100')==projectInfor.issedexpert.getAt(j).get('a0100')){
		    	                				//projectInfor.simpsonsStore.add(projectInfor.simpsonsStore1.getAt(i));
		    	                				alert('OK');
		    	                			}
		    	                		}
		    	                	}*/
	    	                }
	    	    		//}]
	    	    		
	    	    		}
	    	        	]
	    	        }], // An array of form fields
	    	        flex: 2             // Use 2/3 of Container's height (hint to Box layout)
	    	    },{
	    	    	title:'抽选的专家',
	    	    	id:'selzone1',
	    	    	xtype:'fieldset',
	    	    	border:true,
	    	    	
	    	    	hidden:true,
	    	    	collapsible:true,
		    	    //layout:'form',
		    	    checkboxName:'description',
	    	    	layout:'form',
	    	    	items:[{
	    	    		border:false,
	    	    		layout:'column',
	    	    		//items:[{
		    	    		
		    	    		border:false,
		    	    		columnWidth:1,
		        	    //	xtype:'fieldset',
		        	    	//title: '第一次抽选的专家',
		        	    	items:[gridpanel1]
		    	    	//}]
	    	    	}]
	    	    },{

	    	    	title:'二次抽选的专家',
	    	    	id:'selzone2',
	    	    	xtype:'fieldset',
	    	    	border:true,
	    	    	hidden:true,
	    	    	collapsible:true,
		    	    //layout:'form',
		    	    checkboxName:'description',
	    	    	layout:'form',
	    	    	items:[{
	    	    		border:false,
	    	    		layout:'column',
	    	    		//items:[{
		    	    		
		    	    		border:false,
		    	    		columnWidth:1,
		        	    //	xtype:'fieldset',
		        	    	//title: '二次抽取的专家',
		        	    	items:[gridpanel2]
		    	    //	}]
	    	    	}]
	    	    
	    	    }],
	    	    buttonAlign : "center",
	    	    buttons:[{
	    	    	text:'关闭',
	    	    	handler:function(){
	    	    		Ext.getCmp('addprojectwid').destroy();
	    	    		//Ext.removeNode(document.getElementById('addprojectwid'));
	    	    	}
	    	    }]
		 });
		 if(info.times!=null){
			     if(info.times=='2'){
				 Ext.getCmp('selzone1').show();
				 Ext.getCmp('selzone2').show();
			     }else{
			     Ext.getCmp('selzone1').show();	 
			     }
				/* alert(info.times);
				 Ext.getCmp('selzone1').show();*/
			 
		 }
		 Ext.create('Ext.window.Window',{
	    	    title: '项目详细信息',
	    	    id:'addprojectwid',
	    	   // closable: false,
	    	    closable:false,
	    	    layout:'fit',
	    	    width: 840,
	    	    height: 600,
	    	    renderTo: Ext.getBody(),
	    	    items: [projform],
	    	    modal :true,
                    maximizable:true,
	    	    autoScroll:true,
	    	    closeAction:'close'
	       }).show();
	},
	//短信提醒
	sendMessage:function(){
		alert(1);
	},
	//添加指定专家
	addexpertPanel:function(){  
			if(true){
				//alert(num);
				var issedStore=Ext.create('Ext.data.Store', {
					  //  storeId:'projectInfor.simpsonsStore',
					 fields:['a0100','a0101','category','dept','special','id','tel','flag','phone'],
						proxy:{
							type: 'transaction',
					       	functionId:'RE100000004',
					        extraParams:{
					        	"req":"all"
					        },
					        reader: {
					            type: 'json',
					            root: 'expert'         	
					        }
						},
						autoLoad: true
					});
				var rightPanel = Ext.create('Ext.grid.Panel',{
					//title:'符合条件的专家',
					 store:Ext.data.StoreManager.lookup(issedStore),
					 id:'addexpert',
					 border:true,
					 columns:[
						 {header:'工号',dataIndex:'a0100',width:80},
						 {header:'姓名',dataIndex:'a0101',width:80},
						 {header:'科别',dataIndex:'category',width:80},
						 {header:'科室',dataIndex:'dept',width:100},
						 {header:'专业',dataIndex:'special',width:100}
					 ],
					 columnLines:true,
					 height:210,
					 width:483,
					 scroll:true,
					 // selModel:Ext.create('Ext.selection.CheckboxModel',{mode:"SIMPLE"}),
					 selModel:Ext.create('Ext.selection.CheckboxModel',{mode:"SIMPLE"})  
				 });
				
				var rightForm = Ext.create('Ext.form.Panel',{
					title:'',
					border:true,
					//bodyPadding:5,
	    	       
					/*tbar: [
					       { xtype: 'panel', height:26,width:200,html: '<div id="fastsearch2"></div>' }
					     ],*/
					//height:320,
					items:[{
						xtype:'textfield',
						//width:200,
						emptyText : "请输入姓名查询...",
						id:'searchzone1',
						listeners:{
					       click:{
					    	   element:'el',
					    	   fn:function(){
					    		  Ext.getCmp('searchzone1').setValue("");
					    	   }
					       },
					       specialkey:function(field,e){  
					              if (e.getKey()==13){  
					            	  var value=Ext.getCmp('searchzone1').value;
					            	 // alert(Ext.encode(value));
					            	  issedStore.load({params:{'value':encode(value)}});
					           }  
					       }
						}
					},rightPanel],
					buttonAlign:'center',
					buttons:[{text:'确定',
						   // padding:'padding-top:50px',
						    handler:function(){
						var records=rightPanel.getSelectionModel().getSelection();
					// alert(records.getAt[0].get('a0100'));
					 if(records.length>0){
						 //遍历所选取的节点  rec结果集单个对象
						 if(projectInfor.issedexpert!=null && projectInfor.isselexpert!=null){
						//	 alert(1);
						 //projectInfor.issedexpert.removeAll();
						 projectInfor.isselexpert.removeAll();
						 }
						 Ext.getCmp('addexpertwid').hide();
						// projectInfor.personPanel.removeAll();
						 Ext.each(records,function(rec,index){
							// alert(rec.data.a0100);
						//	 projectInfor.personList.push(rec.data.a0100);
						//	 alert(records.index);
							 var flag=true;
							 if(projectInfor.issedexpert.getCount()>0){
								 projectInfor.issedexpert.each(function(record){
									 if(record.data.id==rec.data.id){
										 flag=false;
									 }
								 }) 
							 }
							 if(flag){
							 projectInfor.issedexpert.add({remark:'正选',a0100:rec.data.a0100,a0101:rec.data.a0101,
	 	                			dept:rec.data.dept,category:rec.data.category,special:rec.data.special,id:rec.data.id,tel:rec.data.tel,flag:1,phone:rec.data.phone,accept:''})
							// projectInfor.simpsonsStore.remove(records);
							   			 var imgUsr = 'usr'; //人员库前缀
				var imgA0100 = rec.data.id;   //人员编号
				var imgQuality = 'l';  // 照片质量 l 低分辨看  h 高分辨率 
				//	alert(imgA0100);
				var personImg = Ext.create('Ext.Img', {
					id:'pho_'+imgA0100,
				    src: "/servlet/DisplayOleContent?nbase="+imgUsr+"&a0100="+imgA0100+"&sanyuan=1&quality="+imgQuality,
				    style:'border-radius: 50%;',
				    width:25,
				    height:25,
				    padding:0,
				    margin:'0 15 0 0',
				    listeners: {
				        mouseover: {
				            element: 'el', 
				            fn: function(a, o){ projectInfor.showHideDelImg(o.id, "1"); }
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ projectInfor.showHideDelImg(o.id,"0"); }
				        }
					}
				});
				var delImg = Ext.create('Ext.Img', {
					src: "/workplan/image/remove.png",
					id:'del_'+imgA0100,
					style:'cursor:pointer;',
					width:15,
					height:15,
					hidden:true,
					cls:'delImg',
					listeners: {
				        click: {
				            element: 'el', 
				            fn: function(a, o, b, c){ projectInfor.deletePerson(o.id); }
				        }, mouseover: {
				            element: 'el', 
				            fn: function(a, o){ projectInfor.showHideDelImg(o.id, "1"); }
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ projectInfor.showHideDelImg(o.id,"0"); }
				        }
					}
				});
				var person = Ext.widget('panel',{
					id:'per_'+imgA0100,
					layout:{
						type:'vbox',
						align:'center'
					},
					margin: 0,
					border:false,
					items:[delImg,personImg,{xtype: 'label',text: rec.data.a0101,margin:0}]
				});
								projectInfor.personPanel.add(person);
							 }
						 });
						  projectInfor.issedexpert.each(function(rec){
							  projectInfor.isselexpert.add(rec);
						  })
						  Ext.getCmp('addexpertwid').destroy();
						// alert(records.getAt(1).get('a0100'))
						 /*for(var i=0;i<records.length;i++){
							var obj=records.getAt(i).get('a0100');
							 alert(obj);
							  projectInfor.personList.push(obj);
						 }
						 alert(1);*/
					 }else{
						 Ext.Msg.alert('提示','没有指定专家');
					 }
					 
					}},{text:'取消',handler:function(){
						 Ext.getCmp('addexpertwid').destroy();
						// Ext.removeNode(document.getElementById('addexpertwid'));
					}}]
				});
				
				 Ext.create('Ext.window.Window',{
					 title:'添加指定专家',
					 id:'addexpertwid',
			    	    width: 492,
			    	    height: 315,
			    	    renderTo: Ext.getBody(),
			    	    modal :true,
			    	    //layout:'form',
			    	    items: [rightForm]
			       }).show();
			}else{
				Ext.Msg.alert('提示','没有符合条件的专家');
			}
		},
	
deletePerson:function(phoId){
	   // alert(phoId);
		var delId = phoId.substring(4);
		var perPanel=Ext.getCmp('per_'+delId);
		projectInfor.personPanel.remove(perPanel);
		if(projectInfor.isselexpert.getCount()>0){
		projectInfor.isselexpert.each(function(record){
			if(record.data.id==delId){
				projectInfor.isselexpert.remove(record);
				projectInfor.issedexpert.remove(record);
				projectInfor.simpsonsStore.add(record);
			};
			
		})
		}
	},
showHideDelImg:function(phoId, state){
		//alert(1);
		var delId = phoId.substring(4);
		var delImg = Ext.getCmp("del_"+delId);
		if(state == "1"){
			delImg.show();
		}else{
			delImg.hide();
		}
	}
});