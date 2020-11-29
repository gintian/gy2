/**
 * 薪资类别
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('SalaryTypeUL.SalaryType',{
	constructor:function(config) {
		salarytype_me = this;
		salarytype_me.tableObj="";
		salarytype_me.cookie="";
		salarytype_me.url=config.url;
		salarytype_me.cookie = Ext.create('Ext.state.CookieProvider', {
		    path: "/module/gz/",//作用路径
		    expires: new Date(new Date().getTime()+(1000*60*60*24*30)) //30天
		});
		salarytype_me.init(salarytype_me.url);
	},
	// 初始化函数 currentPage:加载页码
	init:function(url) {
		var map = new HashMap();
		map.put("url",url);
		map.put("CurrentPage","1");
	    Rpc({functionId:'GZ00000202',async:false,success:salarytype_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		if(salarytype_me.tableObj)
			salarytype_me.tableObj.getMainPanel().destroy();
	
		var result = Ext.decode(form.responseText);
		salarytype_me.orgid = result.orgid;
		salarytype_me.commissionFlag = result.commissionFlag;//xiegh 20170412 add提成标识
		salarytype_me.cnameLength = result.cnameLength;
		salarytype_me.imodule = result.imodule;// 0:薪资  1:保险
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		//配置上下移动
		if(result.movePriv=='1'){
			obj.beforeBuildComp=function(config){
				config.tableConfig.viewConfig.plugins={
					ptype: 'gridviewdragdrop',
					dragText: common.msg.moveToDesignated
		        };
		        config.tableConfig.viewConfig.listeners={
		        		drop: salarytype_me.drop
			    };
			    config.tableConfig.sortableColumns=false;
			};
		}
		salarytype_me.tableObj = new BuildTableObj(obj);
		salarytype_me.createSearchPanel();
		var tablePanel = salarytype_me.tableObj.tablePanel;
		
		//得到单元格编辑器，添加编辑事件
		if(result.reNamePriv=='1'){
			var cellClick = tablePanel.findPlugin("cellediting");
			cellClick.on("edit", salarytype_me.reName, salarytype_me);
		}
	},
	// 【属性】按钮渲染
	property:function(value, metaData, record){
		var collectPoint = "";
		var isCurr_user = "";
		var salaryid = record.data.salaryid_safe;
		var html = '<a href="####" onclick="salarytype_me.openProperty(\'';
		html = html + salaryid;
		html = html + '\',\''+record.data.cname+'\')"><img src="/images/new_module/property.gif" border=0></a>';	 
		if(record.data.ishave=="1"){
			html = '<a href="####" onclick="salarytype_me.showIshaveUnits()"><img src="/images/new_module/property.gif" border=0></a>'; 
		}
		return html;
	},
	// 【临时变量】按钮渲染
	tmpVar:function(value, metaData, record){
		var collectPoint = "";
		var isCurr_user = "";
		var salaryid = record.data.salaryid_safe;
		var html = '<a href="####" onclick="salarytype_me.viewTempVar(\'';
		html = html + salaryid;
		html = html + '\')"><img src="/images/new_module/tmpvar.gif" border=0></a>';	
		if(record.data.ishave=="1"){
			html = '<a href="####" onclick="salarytype_me.showIshaveUnits()"><img src="/images/new_module/tmpvar.gif" border=0></a>'; 
		} 
		return html;
	},
	// 【计算公式】按钮渲染
	countFormula:function(value, metaData, record){
		var collectPoint = "";
		var isCurr_user = "";
		var salaryid = record.data.salaryid_safe;
		var html = '<a href="####" onclick="salarytype_me.defineFormula(\''+salaryid+'\')"><img src="/images/new_module/formula.gif" border=0></a>'; 
		if(record.data.ishave=="1"){
			html = '<a href="####" onclick="salarytype_me.showIshaveUnits()"><img src="/images/new_module/formula.gif" border=0></a>'; 
		}
		return html;
	},

	// 【审核公式】按钮渲染
	approvalFormula:function(value, metaData, record){
		var collectPoint = "";
		var isCurr_user = "";
		var salaryid = record.data.salaryid_safe;
		var html = '<a href="####" onclick="salarytype_me.defineSpFormula(\''+salaryid+'\')"><img src="/images/new_module/formula2.gif" border=0></a>'; 
		if(record.data.ishave=="1"){
			html = '<a href="####" onclick="salarytype_me.showIshaveUnits()"><img src="/images/new_module/formula2.gif" border=0></a>'; 
		}
		return html;
	},

	// 【薪资项目】按钮渲染
	salaryProject:function(value, metaData, record){
		var collectPoint = "";
		var isCurr_user = "";
		var salaryid = record.data.salaryid_safe;
		var html = '<a href="####" onclick="salarytype_me.salaryItem(\'';
		html = html + salaryid;
		html = html + '\',\''+record.data.cname+'\')"><img src="/images/new_module/salaryitem.gif" border=0></a>';
		if(record.data.ishave=="1"){
			html = '<a href="####" onclick="salarytype_me.showIshaveUnits()"><img src="/images/new_module/salaryitem.gif" border=0></a>'; 
		} 
		return html;
	},
	
	// 【应用机构】按钮渲染
	salaryAppOrganization:function(value, metaData, record){
		var collectPoint = "";
		var isCurr_user = "";
		var salaryid = record.data.salaryid_safe;
		var html = '<a href="####" onclick="salarytype_me.openSalaryAppOrganization(\'';
		html = html + salaryid;
		html = html + '\',\''+record.data.cname+'\')"><img src="/images/new_module/salaryorg.png" border=0></a>';
		return html;
	},
	// 查询控件
	createSearchPanel:function(){
		var map = new HashMap();
		map.put("url",url);
		salarytype_me.searchBox = Ext.create("EHR.querybox.QueryBox",{
			hideQueryScheme:true,
			emptyText:gz.msg.searchmsg,
			subModuleId:"gz_salaryType_00000001",
			customParams:map,
			funcId:"GZ00000202",
			success:salarytype_me.loadTable//重新加载数据列表
		});
		var toolBar = Ext.getCmp("salaryType_toolbar");
		toolBar.add(salarytype_me.searchBox);
	},
	
	//重新加载数据列表
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('salaryType_dataStore');
		store.currentPage=1;
		store.load();
	},
	
	//应用机构弹出框
	openSalaryAppOrganization:function(salaryid,cname){
		Ext.require('SalaryTypeUL.applicationorganization.ApplicationOrganization', function(){
			Ext.create("SalaryTypeUL.applicationorganization.ApplicationOrganization",
					{salaryid:salaryid,flag:"0",imodule:salarytype_me.imodule,orgid:salarytype_me.orgid});
		});
	},
	
	//薪资项目弹出框     lis   2015-10-17
	salaryItem:function(salaryid,cname){
		Ext.require('SalaryTypeUL.salaryitem.SalaryItem', function(){
			Ext.create("SalaryTypeUL.salaryitem.SalaryItem",{salaryid:salaryid,cname:cname});
		});
	},
	
	//临时变量弹出框     lis   2015-10-17
	viewTempVar:function(salaryid){
		Ext.require('EHR.defineformula.DefineTempVar',function(){
			Ext.create("EHR.defineformula.DefineTempVar",{module:'1',id:salaryid,type:'1'});
		})
	},
	
	//定义计算公式    lis   2015-11-12
	defineFormula:function(salaryid){
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module:'1',id:salaryid});
		})
	},
	
	//定义审批公式    lis   2015-11-12
	defineSpFormula:function(salaryid){
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module:'1',id:salaryid,formulaType:'2'});
		})
	},
	
	//新增薪资类别
	addSalaryType:function(){
		var panel = Ext.create('Ext.panel.Panel', {
		    bodyPadding: 5,
		    border:false,
		    minButtonWidth:60,
		    layout: 'anchor',
		    defaults: {
		        anchor: '100%'
		    },
		    items: [{
		    	xtype:'textfield',
		        fieldLabel: '',
		        id:'textid',
		        itemId:'name_id',
		        allowBlank: false,
                validator: function (value) {
		    		if(salarytype_me.getStrLength(value)>30){
		    			return '名称长度过长！';
					}else
						return true;
                }
		    }],
		    //保存 按钮.
		    bbar: [{xtype:'tbfill'},{
		    	xtype:'button',
		        text: common.button.ok,
		        style:'margin-right:5px',
		        handler: function() {
				if(!Ext.getCmp("textid").isValid())
                  return;
				var value = panel.getComponent("name_id").getValue();
				var result = value.replace(/(^\s+)|(\s+$)/g,"");
				result = result.replace(/\s/g,"");
				if(result.length==0){
			    	Ext.showAlert("内容不能为空且不可为空格！");
					return;
				}
	    		var map = new HashMap();
	    		map.put("name",getEncodeStr(value));
	    		map.put("gz_module",salarytype_me.imodule);
	    		map.put("type","1");
	    		map.put("salaryid","-1");
	    		map.put("isAdd","1");
	    		Rpc({functionId:'GZ00000218',success:function(response,action){
	    			var result = Ext.decode(response.responseText);  	
	    			if (result.succeed) {
	    				if(result.msg != '0'){
	    					Ext.showAlert(result.msg);
	    				}else{
	    					Rpc({functionId:'GZ00000215',success:function(response,action){
				    			var result = Ext.decode(response.responseText);  	
				    			if (result.succeed) { 
				    				win.close();
			    					salarytype_me.reLoad(1);
				    			} else {  
				    				Ext.showAlert(result.message);
				    			}
				    		}},map);
	    				}
	    			} else {  
	    				Ext.showAlert(result.message);
	    			}
	    		}},map);
		        }
		    },{
		    	xtype:'button',
		        text: common.button.cancel,
		        handler: function() {
		            win.close();
		        }
		    },{xtype:'tbfill'}],
		    renderTo: Ext.getBody()
		});
		
		var win = Ext.create('Ext.window.Window', {
		    title: gz.label.inputSalaryName,//"请输入薪资类别名称"
		    height: 110,
		    width: 400,
		    resizable:false,
		    layout: 'fit',
		    modal:true,
		    items: [panel]
		}).show();
		Ext.getCmp('textid').focus(false, 100);
	},
	
	//重命名
	reName:function(editor, e, eOpts){
		var record  = e.record;
		var originalValue = e.originalValue;
		if(trim(e.value)==''){
			Ext.showAlert("类别名称不能为空且不可为空格！");
			salarytype_me.reLoad(0);
			return;
		}
		
		var map = new HashMap();
		map.put("name",getEncodeStr(e.value));
		map.put("gz_module",salarytype_me.imodule);
		map.put("type","0");
		map.put("oldname",getEncodeStr(originalValue));
		map.put("salaryid",record.get('salaryid_safe'));
		Rpc({functionId:'GZ00000218',success:function(response,action){
			var result = Ext.decode(response.responseText);  	
			if (result.succeed) { 
				if(result.msg != '0'){//有重复名称
					Ext.showAlert(result.msg);
				}else{//校验成功
					Rpc({functionId:'GZ00000215',success:function(response,action){
						var result = Ext.decode(response.responseText);  	
						if (result.succeed) { 
							record.commit();
						} else {  
							Ext.showAlert(common.msg.saveFailed+"！");
						}
					}},map);
				}
			} else {  
				Ext.showAlert(common.msg.saveFailed+"！");
			}
		}},map);
	},
	
	//重新加载数据
	reLoad:function(amount){//判断是否应该翻页，删除时amount传负数，新增为正数，刷新为0
		var store= salarytype_me.tableObj.tablePanel.getStore();
		var maxlength=store.pageSize;//最大行数
		var currentPage=store.currentPage;//当前页码 amount为0时页码不变
		if(amount>0)//新增
			currentPage=Math.ceil((store.totalCount+amount)/maxlength);//取最大页数 不能整除，页数加一
		else if(amount<0)//删除
			if(store.data.length+amount<=0)
				currentPage--;//若当前页不再有数据，页数减一
		salarytype_me.searchBox.removeAllKeys();
		salarytype_me.init(salarytype_me.url,currentPage);
	},
	//删除
	deleteSalaryType:function(){
		var sel = salarytype_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(sel.length==0)
  		{
			Ext.showAlert(gz.msg.selectDeleteSalary);//"请选择要删除的薪资类别！"
  		    return;
  		}
		Ext.MessageBox.confirm(
				common.button.promptmessage,
				gz.msg.sureDelete,//您真的希望删除选中的薪资类别
				function(but){
					if(but == 'yes'){
						Ext.MessageBox.confirm(
								common.button.promptmessage,
								gz.msg.sureDeleteAgain,//删除类别将删除该类别的历史数据，再次确认是否删除
								function(but){
									if(but == 'yes'){
										var recordIds = [];
										Ext.Array.each(sel,function(record,index){
											recordIds.push(record.get("salaryid_safe"))
										})
										
										var map = new HashMap();
										map.put("selectedList",recordIds);
										Rpc({functionId:'GZ00000216',success:function(response,action){
											var result = Ext.decode(response.responseText);  
											if (result.succeed) { 
												salarytype_me.reLoad(-recordIds.length);
											} else {  
												Ext.showAlert(result.message);
											}
										}},map);
									}
								},
								salarytype_me
						);
					}
				},
				salarytype_me
		);
	},
	
	//上下移动
	drop:function(node,data,model,dropPosition,dropHandlers){
		var panel = salarytype_me.tableObj.tablePanel;
		if(data.records.length>1){
			   /*Ext.MessageBox.show({  
	  				title : common.button.promptmessage,  
	  				msg : gz.msg.forbidMultiMove, 
	  				icon: Ext.MessageBox.INFO  
	  			});*/
			   Ext.showAlert(gz.msg.forbidMultiMove);
			   panel.getStore().load();
		   }else{
			   var ori_id=data.records[0].get("salaryid_safe");
			   var ori_seq = data.records[0].get('seq');
			   var to_id=model.get('salaryid_safe');
			   var to_seq = model.get('seq');
			   var map = new HashMap();
			   map.put("ori_id",ori_id);
			   map.put("ori_seq",ori_seq);
			   map.put("to_id",to_id);
			   map.put("to_seq",to_seq);
			   map.put("dropPosition",dropPosition);
			   map.put("oper","move");
			   Rpc({functionId:'GZ00000217',success:function(response,action){
				   var result = Ext.decode(response.responseText);  			    
				   if (!result.succeed) {
					   panel.getStore().load();
					   /*Ext.MessageBox.show({  
						   title : common.button.promptmessage,  
						   msg : gz.msg.moveFalse, //移动失败
						   buttons: Ext.Msg.OK,
						   icon: Ext.MessageBox.INFO  
					   }); */
					   Ext.showAlert(gz.msg.moveFalse);
				   }
				   else{
				   		var data=result.data;//移动成功，更新界面seq
				   		var store= panel.getStore();
				  		for(var i=0;i<data.length;i++){
				  			store.findRecord('salaryid',data[i].salaryid).set('seq',data[i].seq);
				  		}
				   }
			   }},map);
		 }
	},
	
	//薪资类别另存为
	saveAs:function()
	{
		var sel = salarytype_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(sel.length==0)
  		{
			Ext.showAlert(gz.msg.selectSaveAsSalaryType);//请选择需要另存的薪资类别！
  		    return;
  		}else if(sel.length>1){
  			Ext.showAlert(gz.msg.saveAsOnlyOne);//'每次只能另存一个薪资类别！'
  		    return;
  		}
	
		var record = sel[0];
		var name = record.get("cname")+"-副本";
		var oldname = name;
		var length = salarytype_me.cnameLength;
		var panel = Ext.create('Ext.panel.Panel', {
		    bodyPadding: 25,
		    border:false,
		    minButtonWidth:50,
		    width: 350,
		    // 表单域 Fields 将被竖直排列, 占满整个宽度
		    layout: 'anchor',
		    defaults: {
		        anchor: '100%'
		    },
		    items: [{
		    	xtype:'textfield',
		        fieldLabel: '',
		        itemId:'cname_id',
		        value:name,
		        allowBlank: false
		    }],

		    //保存 按钮.
		    bbar: [{xtype:'tbfill'},{
		    	xtype:'button',
		        text: common.button.ok,
		        style:'margin-right:5px',
		        handler: function() {
		        	var newname = panel.getComponent("cname_id").getValue();
		        	newname=trim(newname);
		        	if(newname==''){
	  		    		Ext.showAlert("类别名称不能为空！");
	       				return;
		        	}
		        	var realLength = 0;
		        	var charCode = -1;
		        	for (var i=0;i<newname.length;i++){
			        	charCode = newname.charCodeAt(i);
			        	if(charCode>=0&&charCode<=128)
			        	  realLength+=1;
			        	else
			        	  realLength+=2;
		        	}
        			if(realLength>salarytype_me.cnameLength){
						Ext.showAlert("类别名称长度最长为"+salarytype_me.cnameLength/2+"个汉字或"+salarytype_me.cnameLength+"个字符！");
						return;
					}
	            	var map = new HashMap();
	        		map.put("name",getEncodeStr(newname));
	        		map.put("gz_module",salarytype_me.imodule);
	        		map.put("type","0");
	        		map.put("oldname",oldname);
	        		map.put("salaryid","-1");
	        		Rpc({functionId:'GZ00000218',success:function(response,action){
	        			var result = Ext.decode(response.responseText);  
	        			if (result.succeed) {
	        				if(result.msg != '0'){//有重复名称
	        					Ext.showAlert(result.msg);
	        				}else{//校验成功
	        					map.put("salaryid",record.get('salaryid_safe'));
	        					Rpc({functionId:'GZ00000219',success:function(response,action){
	    		        			var result = Ext.decode(response.responseText);  	
	    		        			if (result.succeed) { 
	    		        				win.close();
	    		        				salarytype_me.reLoad(1);
	    		        			} else {  
	    		        				Ext.showAlert(result.message+"！");
	    		        			}
	    		        		}},map);
	        				}
	        			} else {  
	        				Ext.showAlert(result.message+"！");
	        			}
	        		}},map);
		        }
		    },{
		    	xtype:'button',
		        text: common.button.cancel,
		        handler: function() {
		            win.close();
		        }
		    },{xtype:'tbfill'}],
		    renderTo: Ext.getBody()
		});
		
		var win = Ext.create('Ext.window.Window', {
		    title: gz.label.inputSaveAsName,//请输入另存的薪资类别名称
		    height: 150,
		    resizable:false,
		    width: 400,
		    layout: 'fit',
		    modal:true,
		    items: [panel]
		}).show();
	},
	
	//导出薪资类别
	exportZip:function(){
		var sel = salarytype_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(sel.length==0)
  		{
			Ext.showAlert("请选择要导出的类别名称！");//"请选择要删除的薪资类别！"
  		    return;
  		}
		var ids = '';
		Ext.Array.each(sel,function(record){
			ids = ids +"," + record.get('salaryid_safe');
		})
		var map = new HashMap();
		map.put("salaryids",ids);
		Rpc({functionId:'GZ00000220',success:function(response,action){
			var result = Ext.decode(response.responseText);  	
			if (result.succeed) { 
				var outName = result.outName;	
				outName = getDecodeStr(outName);
				window.location.target="_blank";
				window.location.href = "/servlet/vfsservlet?fileid=" + outName +"&fromjavafolder=true";
			} else {  
				Ext.showAlert(result.message+"！");
			}
		}},map);
	},
	
	//导入薪资类别
	importZip:function()
	{
		Ext.require('SalaryTypeUL.ImportSalaryType', function(){
			Ext.create("SalaryTypeUL.ImportSalaryType",{imodule:salarytype_me.imodule});
		});
	},
	
	//币种维护
	moneyMaintenance:function(){
		Ext.require('SalaryTypeUL.moneystyle.InitMoney', function(){
			Ext.create("SalaryTypeUL.moneystyle.InitMoney");
		});
	},
	
	//历史数据初始化
	historyDataInit:function(){
		var sel = salarytype_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(sel.length==0)
  		{
  			Ext.showAlert(gz.msg.selectInitSalary);//请选择需要初始化的薪资类别！
  		    return;
  		}
		
		var ids = '';
		Ext.Array.each(sel,function(record){
			ids = ids +"," + record.get('salaryid_safe');
		})
		
		var hboxPanel = Ext.widget({
			xtype:'panel',
			border:false,
			hidden:true,
			layout:'hbox',
			items:[{
		    	xtype:'panel',
		    	border:false,
		    	items:[{
				  xtype:'datetimefield',
				  itemId:'startDate',
				  editable:false,
				  fieldLabel: common.label.from,
				  labelWidth:20,
				  width:203,
				  format:'Y-m-d',
				  labelSeparator :'',//去掉后面的冒号
				  margin:'1 0 0 10'
				}]
		    },{
		    	xtype:'panel',
		    	border:false,
		    	items:[{
					  xtype:'datetimefield',
					  itemId:'endDate',
					  editable:false,
					  fieldLabel: common.label.to,
					  labelWidth:20,
					  width:203,
					  format:'Y-m-d',
					  labelSeparator :'',
					  margin:'1 0 0 10'
				}]
		    }],
		    listeners: {
		        click: {
		            element: 'el', //bind to the underlying el property on the panel
		            fn: function(){ 
						panel.queryById('date').setValue(true);
					}
		        }
		    }
		});
		var panel = Ext.create('Ext.form.Panel', {
		    bodyPadding: 5,
		    width: 450,
		    height:130,
		    border:false,
		    layout: 'vbox',
		    items: [{
		    	xtype:'radio',
		    	boxLabel:gz.label.all,//全部
		    	checked:true,
		        name: 'all',
		        id:'all',
		        listeners:{
			    	'change': function(radio){
			            if(radio.getValue()){
			            	var startDate = hboxPanel.queryById('startDate');
			            	var endDate = hboxPanel.queryById('endDate');
			            	startDate.setValue("");
			            	endDate.setValue("");
			        
			            }
			         hboxPanel.show();
			    	}
		    	}
		    },{
		    	xtype:'radio',
		    	boxLabel:gz.label.timeScope,//时间范围
		        name: 'all',
		        id:'date',
		        listeners:{
			        'change': function(radio){
			        	 hboxPanel.hide();
			        }
		        }
		    },hboxPanel],
		    minButtonWidth:50,
		    bbar: [ {xtype:'tbfill'},{
		    	xtype: 'button',
		        text: common.button.ok,
		        formBind: true, //only enabled once the form is valid
		        disabled: true,
		        style:'margin-right:5px',
		        handler: function() {
		            var form = this.up('form').getForm();
		            if (form.isValid()) {
		            	
		            	Ext.MessageBox.wait(gz.msg.historyDataInit, common.msg.wait);
		            	var map = new HashMap();
		            	map.put("selectedList",ids)
		            	if(panel.queryById('all').getValue()){
		            		map.put("initType","1")
		            	}
		            	if(panel.queryById('date').getValue()){
		            		map.put("initType","2");
		            		var startValue=hboxPanel.queryById('startDate').getValue();
		            		if(startValue==null)
		            			startValue="";
		            		var endValue=hboxPanel.queryById('endDate').getValue();
		            		if(endValue==null)
		            			endValue="";
		            		if(startValue==''&&endValue=='')
		            		{
		            			Ext.MessageBox.close();	
		            			Ext.showAlert("请选择起始日期！");
		            			return;
		            		}
		            		map.put("startDate",startValue);
		            		map.put("endDate",endValue);
		            	}
		            	Rpc({functionId:'GZ00000230',success:function(response,action){
		        			var result = Ext.decode(response.responseText);
		        			Ext.MessageBox.close();	
		        			if (result.succeed) { 
		        				Ext.showAlert(gz.msg.historyDataInitOk);//历史数据初始化成功
		        			} else {  
		        				Ext.showAlert(result.message+"！");
		        			}
		        		}},map);
		            	win.close();
		            }
		        }
		    },{
		    	xtype: 'button',
		        text: common.button.cancel,
		        handler: function() {
		    		win.close();
		        }
		    },{xtype:'tbfill'}],
		    renderTo: Ext.getBody()
		});
		
		var win = Ext.create('Ext.window.Window', {
		    title: "历史数据初始化",
		    resizable:false,
		    border:false,
			modal:true,
		    items: panel
		});
		win.show();
	},
	
	//结构同步
	structSynchro:function(){
		var sel = salarytype_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(sel.length==0)
  		{
			Ext.showAlert(gz.msg.selectSyncSalary);//请选择需要同步的薪资类别！
  		    return;
  		}
		Ext.MessageBox.wait(gz.msg.inSync, common.button.promptmessage) 
		var ids = '';
		Ext.Array.each(sel,function(record){
			ids = ids +"," + record.get('salaryid_safe');
		})
		var map = new HashMap();
		map.put("salaryids",ids);
		Rpc({functionId:'GZ00000229',timeout:1000000,success:function(response,action){
			var result = Ext.decode(response.responseText);
			Ext.MessageBox.close();	
			if (result.succeed) { 
				var errorMessage = result.errorMessage;
				var msg = errorMessage == ""?gz.msg.synchronousCompletion+"！":gz.msg.synchronousCompletion+";  "+errorMessage;
				Ext.showAlert(msg);//同步完成
			} else {  
				Ext.showAlert(result.message+"！");
			}
		}},map);
	},
	
	//弹出薪资属性设置页面
	openProperty:function(salaryid,cname){
		Ext.require('SalaryTypeUL.salaryproperty.SalaryProperty', function(){
			Ext.create("SalaryTypeUL.salaryproperty.SalaryProperty",{salaryid:salaryid,cname:cname,imodule:salarytype_me.imodule,commissionFlag:salarytype_me.commissionFlag});//xiegh 20170412 add 薪资提成权限标识
		});
	},
	//所属单位渲染 zhaoxg add 2016-12-13
	subordinateUnits:function(value, metaData, record){
		var salaryid = record.data.salaryid_safe;
        var html = '&nbsp&nbsp<a href="####" onclick="salarytype_me.openSelectPerson(this,\''+salaryid+'\',\'' + record.data.workunits + '\')"><img src="/images/new_module/depart_edit.png" border=0 align="absmiddle">'+record.data.subordinateunits+'</a>';
        if(record.data.ishave=="1"){
			html = '&nbsp&nbsp<a href="####" onclick="salarytype_me.showIshaveUnits()"><img src="/images/new_module/depart_edit.png" border=0>'+record.data.subordinateunits+'</a>';	
		}
		return html;
	},
	openSelectPerson:function(obj,salaryid,workunits){
		workunits=[workunits];
		var picker = new PersonPicker({
			multiple: true,
			text: "添加",
			orgid: salarytype_me.orgid, // 组织机构，不传代表全部
			addunit:true, //是否可以添加单位
			adddepartment:true, //是否可以添加单位
			addpost:false,
			isPrivExpression:false,
			multipleAndSingle:true,
			defaultSelected:workunits,
			callback: function (c) {
				var id = "";
				var value = "";
				for (var i = 0; i < c.length; i++) {
					var cc= c[i];
					id = cc.id;
					value = cc.name;	
				}
				var map = new HashMap();
	    		map.put("workunits",id);
	    		map.put("salaryid",salaryid);
                map.put("subordinateunits",value);
	    		Rpc({functionId:'GZ00000213',success:function(response,action){
	    			var result = Ext.decode(response.responseText);  	
	    			if (result.succeed) {
	    				if(value.length>0){
	    					obj.setAttribute('onclick','salarytype_me.openSelectPerson(this,\''+salaryid+'\',\'' + result.workunits + '\')');
	    					obj.innerHTML='<img src="/images/new_module/depart_edit.png" border=0  align="absmiddle">'+value;
	    				}else{
	    					obj.setAttribute('onclick','salarytype_me.openSelectPerson(this,\''+salaryid+'\',\'' + result.workunits + '\')');
	    					obj.innerHTML='<img src="/images/new_module/depart_edit.png" border=0  align="absmiddle">全部';
	    				}
	    			} else {
	    				Ext.showAlert(result.message);
	    			}
	    		}},map);
			}
		}, obj);
		picker.open();
	},
	showIshaveUnits:function(){
		Ext.showAlert("您没有操作此功能的操作单位！");
	},
    getStrLength:function(str){
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    }
});

