/**
 * 薪资类别-薪资属性-设置比对指标
 * lis 2015-12-12
 */
Ext.define('SalaryTypeUL.salaryproperty.SetChangeSp',{
        constructor:function(config){
			setChangeSp_me = this;
			setChangeSp_me.salaryid = config.salaryid;
			setChangeSp_me.gz_module = config.gz_module;//模块号
			setChangeSp_me.callBackfn = config.callBackfn;//回调函数
			var result = config.result;
			setChangeSp_me.leftlist = result.leftlist;
			setChangeSp_me.rightlist = result.rightlist;
			setChangeSp_me.addleftlist = result.addleftlist;
			setChangeSp_me.addrightlist = result.addrightlist;
			setChangeSp_me.delleftlist = result.delleftlist;
			setChangeSp_me.delrightlist = result.delrightlist;
			
			setChangeSp_me.rightValue=config.rightValue;
			setChangeSp_me.addrightValue=config.addrightValue;
			setChangeSp_me.delrightValue=config.delrightValue;
			
			setChangeSp_me.createSalary();      
        },
		 createSalary:function()  
		 {
        	//变动信息左侧数据store
        	var leftStore = Ext.create('Ext.data.Store', {
							fields:['dataName','dataValue']
			});	
        	
        	//变动信息右侧数据store
        	var rightStore = Ext.create('Ext.data.Store', {
							fields:['dataName','dataValue']
			});	
        	
        	//新增人员信息左侧数据store
        	var addLeftStore = Ext.create('Ext.data.Store', {
							fields:['dataName','dataValue']
			});	
        	
        	//新增人员信息右侧数据store
        	var addRrightStore = Ext.create('Ext.data.Store', {
							fields:['dataName','dataValue']
			});	
        	
        	//减少人员信息左侧数据store
        	var delLeftStore = Ext.create('Ext.data.Store', {
							fields:['dataName','dataValue']
			});	
        	
        	//减少人员信息右侧数据store
        	var delRightStore = Ext.create('Ext.data.Store', {
							fields:['dataName','dataValue']
			});	
			
			
        	//将上次操作在页面中暂存尚未写入数据库的数据同步至当前列表。
			var valuelist;//已选指标
			var isHave=false;//删除标识
			var indexNum=new Array();//已选指标上次已删除的列表。
			
			//信息变动修改同步
    		valuelist=setChangeSp_me.rightValue.split(",");//获取已选指标
    		//将存在于rightValue中的项加入右侧列表中
    		Ext.each(valuelist,function(values,indexs){
	        	Ext.each(setChangeSp_me.leftlist,function(obj,index){
	        		if(obj!=null&&obj.dataValue==values){
	        			Ext.Array.remove(setChangeSp_me.leftlist,obj);//从左侧移除已选
	        			Ext.Array.include(setChangeSp_me.rightlist,obj);//加入右侧
	        			
	        		}
	        	});
    		});
    		//排除右侧列表中没有存在于rightValue中的项
        	Ext.each(setChangeSp_me.rightlist,function(obj,index){
        		isHave=false;
        		Ext.each(valuelist,function(values,indexs){
	        		if(obj!=null&&obj.dataValue==values)
	        			isHave=true;
        		});
        		if(!isHave){//若存在没有存在于rightValue中的项
        			indexNum.push(obj);
	        		Ext.Array.include(setChangeSp_me.leftlist,obj);//加入左侧
        		}
        	});
        	Ext.each(indexNum,function(obj,index){
        		Ext.Array.remove(setChangeSp_me.rightlist,obj);//从右侧移除已选
        	});
        	
        	//新增人员修改同步
        	indexNum = new Array()
        	//if(setChangeSp_me.addrightValue){
    		valuelist=setChangeSp_me.addrightValue.split(",");
    		Ext.each(valuelist,function(values,indexs){
	        	Ext.each(setChangeSp_me.addleftlist,function(obj,index){
	        		if(obj!=null&&obj.dataValue==values){
	        			Ext.Array.remove(setChangeSp_me.addleftlist,obj);
	        			Ext.Array.include(setChangeSp_me.addrightlist,obj);
	        		}
	        	});  	
    		});
        	Ext.each(setChangeSp_me.addrightlist,function(obj,index){
        		isHave=false;
        		Ext.each(valuelist,function(values,indexs){
	        		if(obj!=null&&obj.dataValue==values)
	        			isHave=true;
        		});
        		if(!isHave){
        			indexNum.push(obj);
	        		Ext.Array.include(setChangeSp_me.addleftlist,obj);//加入左侧
        		}
        	});
        	Ext.each(indexNum,function(obj,index){
        		Ext.Array.remove(setChangeSp_me.addrightlist,obj);//从右侧移除已选
        	});
        	
        	//减少人员修改同步
    		indexNum = new Array()
    		valuelist=setChangeSp_me.delrightValue.split(",");
    		Ext.each(valuelist,function(values,indexs){
	        	Ext.each(setChangeSp_me.delleftlist,function(obj,index){
	        		if(obj!=null&&obj.dataValue==values){
	        			Ext.Array.remove(setChangeSp_me.delleftlist,obj);
	        			Ext.Array.include(setChangeSp_me.delrightlist,obj);
	        		}
	        	});
    		});
        	Ext.each(setChangeSp_me.delrightlist,function(obj,index){
        		isHave=false;
        		Ext.each(valuelist,function(values,indexs){
	        		if(obj!=null&&obj.dataValue==values)
	        			isHave=true;
        		});
        		if(!isHave){
        			indexNum.push(obj);
	        		Ext.Array.include(setChangeSp_me.delleftlist,obj);//加入左侧
        		}
        	});
        	Ext.each(indexNum,function(obj,index){
        		Ext.Array.remove(setChangeSp_me.delrightlist,obj);//从右侧移除已选
        	});
			
			
        	Ext.each(setChangeSp_me.leftlist,function(obj,index){
        		leftStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(setChangeSp_me.rightlist,function(obj,index){
        		rightStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(setChangeSp_me.addleftlist,function(obj,index){
        		addLeftStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(setChangeSp_me.addrightlist,function(obj,index){
        		addRrightStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(setChangeSp_me.delleftlist,function(obj,index){
        		delLeftStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(setChangeSp_me.delrightlist,function(obj,index){
        		delRightStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	var panelWidth = 242;
        	var panelHeight = 290;
			//生成信息变动左侧表格
			var leftPanel = Ext.create('Ext.grid.Panel', {
				store:leftStore,
				width: panelWidth,
		    	height: panelHeight,
		    //	border:false,
		    	multiSelect:true,
		    	forceFit:true,
		    	hideHeaders:true,
		    	tbar:[{
		    		xtype:'label',
		    		text:common.label.alternativeField//备选指标
		    	}],
				columns: [
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
	        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
							setChangeSp_me.addMode(leftPanel,rightPanel);
	            		}
	            }
			});
			
			//存放目标数据的grid
			var rightPanel = Ext.create('Ext.grid.Panel', {
				store:rightStore,
				width: panelWidth,
		    	height: panelHeight,
		    //	border:false,
		    	multiSelect:true,
		    	forceFit:true,
		    	hideHeaders:true,
		    	tbar:[{
		    		xtype:'label',
		    		text:common.label.hasSelectField//已选指标
		    	}],
				columns: [
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
					{ text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
						setChangeSp_me.delMode(leftPanel,rightPanel);
            		}
            }
			});
			
			var butPanel = Ext.widget({
			    xtype: 'panel',
			    border:false,
			    width: 50,
			    height: panelHeight, 
		    	layout: {
			    	type: 'vbox',
	    	        align: 'center',
	    	        pack :'center'
		        },
		        defaults:{  
	   	             margin:'5,0,0,0'  
	   	        },
		    	items:[
					{
						xtype:'button',
	          			text:common.button.addfield, //添加
	          			handler:function(){
							setChangeSp_me.addMode(leftPanel,rightPanel);
						}
					},
					{
						xtype:'button',
	          			text:common.button.todelete, //删除
	          			handler:function(){
							setChangeSp_me.delMode(leftPanel,rightPanel);
						}
					}
		          	]
		    });
			
			//生成信息变动左侧表格
			var addLeftPanel = Ext.create('Ext.grid.Panel', {
				store:addLeftStore,
				width: panelWidth,
		    	height: panelHeight,
		    	border:true,
		    	multiSelect:true,
		    	forceFit:true,
		    	hideHeaders:true,
		    	tbar:[{
		    		xtype:'label',
		    		text:common.label.alternativeField//备选指标
		    	}],
				columns: [
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
	        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
							setChangeSp_me.addMode(addLeftPanel,addRightPanel);
	            		}
	            }
			});
			
			//存放目标数据的grid
			var addRightPanel = Ext.create('Ext.grid.Panel', {
				store:addRrightStore,
				width: panelWidth,
		    	height: panelHeight,
		    	border:true,
		    	multiSelect:true,
		    	forceFit:true,
		    	hideHeaders:true,
		    	tbar:[{
		    		xtype:'label',
		    		text:common.label.hasSelectField//已选指标
		    	}],
				columns: [
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
					{ text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
						setChangeSp_me.delMode(addLeftPanel,addRightPanel);
            		}
            }
			});
			
			var addButPanel = Ext.widget({
			    xtype: 'panel',
			    border:false,
			    width: 50,
			    height: panelHeight, 
		    	layout: {
			    	type: 'vbox',
	    	        align: 'center',
	    	        pack :'center'
		        },
		        defaults:{  
	   	             margin:'5,0,0,0'  
	   	        },
		    	items:[
					{
						xtype:'button',
	          			text:common.button.addfield, //添加
	          			handler:function(){
							setChangeSp_me.addMode(addLeftPanel,addRightPanel);
						}
					},
					{
						xtype:'button',
	          			text:common.button.todelete, //删除
	          			handler:function(){
							setChangeSp_me.delMode(addLeftPanel,addRightPanel);
						}
					}
		          	]
		    });
			
			//生成信息变动左侧表格
			var delLeftPanel = Ext.create('Ext.grid.Panel', {
				store:delLeftStore,
				width: panelWidth,
		    	height: panelHeight,
		    	border:true,
		    	multiSelect:true,
		    	forceFit:true,
		    	hideHeaders:true,
		    	tbar:[{
		    		xtype:'label',
		    		text:common.label.alternativeField//备选指标
		    	}],
				columns: [
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
	        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
							setChangeSp_me.addMode(delLeftPanel,delRightPanel);
	            		}
	            }
			});
			
			//存放目标数据的grid
			var delRightPanel = Ext.create('Ext.grid.Panel', {
				store:delRightStore,
				width: panelWidth,
		    	height: panelHeight,
		    	border:true,
		    	multiSelect:true,
		    	forceFit:true,
		    	hideHeaders:true,
		    	tbar:[{
		    		xtype:'label',
		    		text:common.label.hasSelectField//已选指标
		    	}],
				columns: [
				    { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
					{ text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
						setChangeSp_me.delMode(delLeftPanel,delRightPanel);
            		}
            }
			});
			
			var delButPanel = Ext.widget({
			    xtype: 'panel',
			    border:false,
			    width: 50,
			    height: panelHeight, 
		    	layout: {
			    	type: 'vbox',
	    	        align: 'center',
	    	        pack :'center'
		        },
		        defaults:{  
	   	             margin:'5,0,0,0'  
	   	        },
		    	items:[
					{
						xtype:'button',
	          			text:common.button.addfield, //添加
	          			handler:function(){
							setChangeSp_me.addMode(delLeftPanel,delRightPanel);
						}
					},
					{
						xtype:'button',
	          			text:common.button.todelete, //删除
	          			handler:function(){
							setChangeSp_me.delMode(delLeftPanel,delRightPanel);
						}
					}
		          	]
		    });
			 var css_template_tab="#changeTab-body {border-width: 0px 1px 1px 1px;}";
			Ext.util.CSS.createStyleSheet(css_template_tab,"tab_css");
			//比对指标
			var tabs = Ext.create('Ext.tab.Panel', {
        	    activeTab: 0,
        	    plain: false,
        	    height: 327,
        	    id:'changeTab',
        	    bodyPadding:'3 2',
        	    items: [
        	        {
        	            title: '信息变动',
        	            layout: 'hbox',
        				items:[leftPanel,butPanel,rightPanel]
        	        },
        	        {
        	        	title: '新增人员',
        	        	layout: 'hbox',
         				items:[addLeftPanel,addButPanel,addRightPanel]
        	        },
        	        {
        	        	title: '减少人员',
        	        	layout: 'hbox',
         				items:[delLeftPanel,delButPanel,delRightPanel]
        	        }
        	    ],
        	    renderTo : Ext.getBody()
        	});
			
	 		//生成弹出得window
			var win=Ext.widget("window",{
	   		  title : gz.button.setComparisonField,//设置比对指标
	   		  width: 550,
	   		  height: 400, 
	   		  resizable: false,
	   		  minButtonWidth:45,
		      border:false,
			  modal:true,
//			  alwaysOnTop:true,
			  closeAction:'destroy',
			  items: [tabs],
	          buttons:[
	                     {xtype:'tbfill'},
		          		 {
		          			text:common.button.ok, //确定
		          			handler:function(){
                    	 		setChangeSp_me.saveItems(rightPanel,addRightPanel,delRightPanel);
                    	 		win.close();
                     		}
		          		 },
		          		 {
		          			text:common.button.cancel, //取消
		          			handler:function(){
		          				win.close();
		          			}
		          		 },{xtype:'tbfill'}
			          ]     
	   		});
	   		win.show();
		 },
        
		 //将左侧勾选的子集指标添加到右侧panel
		 addMode:function(leftPanel,rightPanel){
			 var records = leftPanel.getSelectionModel().getSelection();
			 if(records.length == 0){
				 Ext.showAlert(gz.msg.selectAddObj);
				 return;
			 };
			 Ext.Array.each(records, function(record, index, countriesItSelf) {
				 var modeValue = record.get('dataValue');
				 //生成要插入的model对象
				 var aimMode = {
						 dataName:record.get('dataName'), 
						 dataValue:record.get('dataValue')
				 };
				 var rowlength = rightPanel.getStore().data.length;
				 var isAdd = true;
				 rightPanel.getStore().each(function(item,index,count){ //确认是否已存在
					 if(item.get('dataValue').indexOf(modeValue)!="-1"){
						 isAdd = false;
						 return;
					 }
				 })
				 if(isAdd){
					 leftPanel.getStore().remove(record);
					 rightPanel.getStore().insert(rowlength, aimMode);//将选中对象数据插入到指定位置
				 }
			 });
		 },
		 
		 //保存右侧已经选好的薪资项目
		 saveItems:function(rightPanel,addRightPanel,delRightPanel){
			 var rightvalue = "";
			 var addrightvalue = "";
			 var delrightvalue = "";
			 rightPanel.getStore().each(function(item,index,count){ //遍历每一条数据
				 if(rightvalue=="")
					 rightvalue = item.get('dataValue');
				 else
					 rightvalue = rightvalue + "," + item.get('dataValue');
			 });
			 
			 addRightPanel.getStore().each(function(item,index,count){ //遍历每一条数据
				 if(addrightvalue=="")
					 addrightvalue = item.get('dataValue');
				 else
					 addrightvalue = addrightvalue + "," + item.get('dataValue');
			 });
			 
			 delRightPanel.getStore().each(function(item,index,count){ //遍历每一条数据
				 if(delrightvalue=="")
					 delrightvalue = item.get('dataValue');
				 else
					 delrightvalue = delrightvalue + "," + item.get('dataValue');
			 });
			 
			 if(setChangeSp_me.callBackfn)
					Ext.callback(eval(setChangeSp_me.callBackfn),null,[rightvalue,addrightvalue,delrightvalue]);
		 },
		 
		//删除右侧已选指标
		 delMode:function(leftPanel,rightPanel){
			 var records = rightPanel.getSelectionModel().getSelection();
				if(records.length == 0){
					Ext.showAlert(gz.msg.selectDelObj);
					return;
				}
				Ext.Array.each(records, function(record) {
					rightPanel.getStore().remove(record);
					leftPanel.getStore().insert(leftPanel.getStore().getCount(), record);//将选中对象数据插入到指定位置
    			}); 
		 }
 })