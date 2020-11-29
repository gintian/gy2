Ext.define('TemplateNavigation.TemplateChangeLog',{
	constructor:function(config){
		ChangeLogScope = this;
		var map = new HashMap();
		ChangeLogScope.module_id=config.module_id;
		/* 模块ID
		 * 1、人事异动
		 * 2、薪资管理
		 * 3、劳动合同
		 * 4、保险管理
		 * 5、出国管理
		 * 6、资格评审
		 * 7、机构管理
		 * 8、岗位管理
		 * 9、业务申请（自助）
		 * 10、考勤管理
		 * 11、职称评审
		*/	
		if(ChangeLogScope.module_id=="7"||ChangeLogScope.module_id=="8"){//如果是单位管理机构调整 或 岗位管理机构调整 
			map.put("optype","2");
		}else{
			map.put("optype","1");
		}
	    map.put("personname","");
		map.put("orgName","");
		map.put("fieldName","-1");
		map.put("tabid","-1");
		map.put("fieldsetName","-1");
		map.put("year","");//liuyz 主题名称
	    Rpc({functionId:'MB00006020',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				var templatejson=result.templatejson;
				var templateFieldjson=result.templateFieldjson;
				var templateFieldSetNameJson=result.templateFieldSetNameJson;
				ChangeLogScope.templatejson = templatejson;
				ChangeLogScope.templateFieldjson = templateFieldjson;
				ChangeLogScope.templateFieldSetNameJson = templateFieldSetNameJson;
		    	var conditions=result.tableConfig;
				var obj = Ext.decode(conditions);
				ChangeLogScope.templateObj = new BuildTableObj(obj);
	  		  	ChangeLogScope.init(ChangeLogScope.templateObj);
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	init:function(){
		var infoTypeStore = Ext.create('Ext.data.Store', {
		    fields: ['flag', 'name'],
		    data : [
		        {"flag":"1", "name":MB.CHANGELOG.personInfo},
		        {"flag":"2", "name":MB.CHANGELOG.orgInfo}
		    ]
		});

		var templateNameStore = Ext.create('Ext.data.Store', {
			id:'templateNameStore',
			fields:['name','id'],
			data:ChangeLogScope.templatejson
		});

		var templateFieldNameStore = Ext.create('Ext.data.Store', {
			id:'templateFieldNameStore',
			fields:['name','id'],
			data:ChangeLogScope.templateFieldjson
		});
		var templateFieldSetNameStore = Ext.create('Ext.data.Store', {
			id:'templateFieldSetNameStore',
			fields:['name','id'],
			data:ChangeLogScope.templateFieldSetNameJson
		});

		ChangeLogScope.infoType = Ext.create('Ext.form.ComboBox', {
		    store: infoTypeStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    labelSeparator: '',
		    displayField: 'name',
		    valueField: 'flag',
		    matchFieldWidth:false,
		    editable:false,
		    width:100,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					if(ChangeLogScope.module_id=="7"||ChangeLogScope.module_id=="8"){//如果是单位管理机构调整 或 岗位管理机构调整 
						combo.setValue(infoTypeStore.getAt(1).get('flag'));
					}else{
						combo.setValue(infoTypeStore.getAt(0).get('flag'));
					}
					ChangeLogScope.info_type = combo.getValue();
					if(ChangeLogScope.info_type=='1'){
						Ext.getCmp('template_person_name').show(); 
						Ext.getCmp('orgCodeselect').hide(); 
						ChangeLogScope.orgCodesetid="";
						ChangeLogScope.personName="";
						ChangeLogScope.fieldsetname="";
					}else{
						Ext.getCmp('template_person_name').hide(); 
						Ext.getCmp('orgCodeselect').show(); 
						ChangeLogScope.personName="";
						ChangeLogScope.orgCodesetid="";
						ChangeLogScope.fieldsetname="";
					}
	     		},
   				select:function(combo,ecords){
					ChangeLogScope.info_type = combo.getValue();
					ChangeLogScope.fieldsetnameCom.clearValue();
					ChangeLogScope.fieldnameCom.clearValue();
					ChangeLogScope.personNameText.setValue("");
					ChangeLogScope.orgCodeSelect.setValue("");
					ChangeLogScope.templateName.clearValue();
					ChangeLogScope.personName="";
					ChangeLogScope.orgCodesetid="";
					ChangeLogScope.fieldname="-1";
					ChangeLogScope.fieldsetname="-1";
					ChangeLogScope.tabid ="-1";
					if(ChangeLogScope.info_type=='1'){
						Ext.getCmp('template_person_name').show(); 
						Ext.getCmp('orgCodeselect').hide(); 
						ChangeLogScope.orgCodesetid="";
						ChangeLogScope.personName="";
					}else{
						Ext.getCmp('template_person_name').hide(); 
						Ext.getCmp('orgCodeselect').show(); 
						ChangeLogScope.personName="";
						ChangeLogScope.orgCodesetid="";
					}
					ChangeLogScope.query();
					var templateFieldSetNameStore = Ext.data.StoreManager.lookup('templateFieldSetNameStore');
					templateFieldSetNameStore.removeAll();
					templateFieldSetNameStore.add(ChangeLogScope.templateFieldSetNameJson);
					ChangeLogScope.fieldsetnameCom.setValue(templateFieldSetNameStore.getAt(0).get('id'));
					//bug 50044  切换回之后 赋值全部 因为此时 fieldnameCom 框其值为空
					ChangeLogScope.fieldnameCom.setValue(ChangeLogScope.fieldname);
					ChangeLogScope.templateName.setValue("-1");
				}
			}
		});
		//信息集名称
		ChangeLogScope.fieldsetnameCom = Ext.create('Ext.form.ComboBox', {
			store: templateFieldSetNameStore,
			queryMode: 'local',
			margin:'0 10 0 0',
			repeatTriggerClick : true,
			fieldLabel:MB.CHANGELOG.fieldSetName,//模板名称
			labelSeparator: '',
			labelAlign:'right',
			labelWidth:60,
			displayField: 'name',
			valueField: 'id',
			matchFieldWidth:false,
			editable:false,
			width:200,
			fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(templateFieldSetNameStore.getAt(0).get('id'));
					ChangeLogScope.fieldsetname = combo.getValue();
				},
				select:function(combo,ecords){
					ChangeLogScope.fieldsetname = combo.getValue();
					/**bug52385V76人事异动 变动日志选中信息集时应默认显示当前子集的变动日志，不应再去点查询*/
					ChangeLogScope.query();
				},
				change:function(obj, newValue, oldValue, eOpts){
					ChangeLogScope.fieldsetname =newValue;
					if(newValue!=oldValue){
						ChangeLogScope.fieldsetChange = true;
					}
				}
			}
		});
		//指标名称
		ChangeLogScope.fieldnameCom = Ext.create('Ext.form.ComboBox', {
		    store: templateFieldNameStore,
		     queryMode: 'local',
		    margin:'0 10 0 0',
		    repeatTriggerClick : true,
		    fieldLabel:MB.CHANGELOG.fieldName,//模板名称
		    labelSeparator: '',
		    labelAlign:'right',
		    labelWidth:60,
		    displayField: 'name',
		    valueField: 'id',
		    matchFieldWidth:false,
		    editable:false,
		    width:200,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(templateNameStore.getAt(0).get('id'));
					ChangeLogScope.fieldname = combo.getValue();
	     		},
   				select:function(combo,ecords){
					ChangeLogScope.fieldname = combo.getValue();
					/**bug53266 人事异动 变动日志中指标名称和模板名称，点击下拉框选择后，未直接查询出结果*/
					ChangeLogScope.query();
				},
				change:function(obj, newValue, oldValue, eOpts){
					ChangeLogScope.fieldname =newValue;
				}
			}
		});

		

		ChangeLogScope.orgCodeSelect = Ext.widget('codecomboxfield',{
			id:'orgCodeselect',
			margin:'0 0 0 0',
			width:150,
		 	codesetid:"@K",
		 	nmodule:"0",
		 	ctrltype:"0",
		 	codesource:"",
		 	onlySelectCodeset:false,
		 	afterCodeSelectFn:function(a,value){
		 		ChangeLogScope.orgCodesetid=value;
		 		/**bug52385V76人事异动 变动日志选中信息集时应默认显示当前子集的变动日志，不应再去点查询*/
				ChangeLogScope.query();
		 	}
		});
		
		ChangeLogScope.personNameText=Ext.create('Ext.form.field.Text',{
			id:'template_person_name',
			width:180,
			margin:'0 2 0 10',
			emptyText:MB.CHANGELOG.emptyText,
			listeners:{
				change:function( textfield , newValue , oldValue , eOpts ){
					ChangeLogScope.personName=newValue;
				} 
			}
		})
		//模板名称
		ChangeLogScope.templateName = Ext.create('Ext.form.ComboBox', {
			id:'template_db_select',
		    store: templateNameStore,
		    queryMode: 'local',
		    margin:'0 10 0 0',
		    repeatTriggerClick : true,
		    fieldLabel:MB.CHANGELOG.templateName,//模板名称
		    labelSeparator: '',
		    labelAlign:'right',
		    labelWidth:60,
		    displayField: 'name',
		    valueField: 'id',
		    matchFieldWidth:false,
		    editable:false,
		    width:250,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(templateNameStore.getAt(0).get('id'));
					ChangeLogScope.tabid = combo.getValue();
	     		},
   				select:function(combo,ecords){
					ChangeLogScope.tabid = combo.getValue();
					/**bug53266 人事异动 变动日志中指标名称和模板名称，点击下拉框选择后，未直接查询出结果*/
					ChangeLogScope.query();
				},
	     		change:function(obj, newValue, oldValue, eOpts){
	     			ChangeLogScope.tabid =newValue;
	     		}
			}
		});
		
		//查询按钮，lis add 20160607
		var queryBut = Ext.create('Ext.Button', {
		    text: common.button.query,
		    handler: function() {
		        ChangeLogScope.query();
		    }
		});
		var toolBar = Ext.getCmp("changeInfo_toolbar");
		toolBar.insert(ChangeLogScope.infoType);
		toolBar.insert(ChangeLogScope.orgCodeSelect);
		toolBar.insert(ChangeLogScope.fieldsetnameCom);
		toolBar.insert(ChangeLogScope.fieldnameCom);
		toolBar.insert(ChangeLogScope.templateName);
		
		var Mypicker =Ext.require('EHR.extWidget.field.DateExtendField', function(){		
			var date_me =	Ext.create('EHR.extWidget.field.DateExtendField',{
				id:'data_picker',
				fieldLabel:common.button.year,
				labelSeparator: '',
				checkflag:false,
				labelWidth:30,
				format:'Y',
				matchFieldWidth:false,
                padding:'5 0 0 5',
                datechecked:function(year,quarter,month){
                	ChangeLogScope.year=year;
                	/**bug52385V76人事异动 变动日志选中信息集时应默认显示当前子集的变动日志，不应再去点查询*/
					ChangeLogScope.query();
                },
                scope:ChangeLogScope,
                fieldStyle:'height:20px;',
                width : 150
 			});
 			var comPanel = Ext.widget('panel',{
 				header:false,
				border:0,
				layout:{
					type:'hbox',
					align:'middle',
					pack:'center'
				},
				items:[date_me]
			});
			toolBar.insert(date_me);
			/**53267 人事异动 变动日志中姓名快速查询框调整到最后面，与其他地方风格保持一致*/
			toolBar.insert(ChangeLogScope.personNameText);
			toolBar.insert(queryBut);
		});	

		
		var ChangeLogkPanel = Ext.create('Ext.panel.Panel', {
			id:'changeLogPanel',		
			border : false,
			height:'100%',
			autoScroll:true,
			margin:"0 0 0 2",//lis 20160513
			layout:'fit',
			items:[ChangeLogScope.templateObj.getMainPanel()]
		})
		
		var ChangeLogkWindow = Ext.create('Ext.window.Window', {
			id:'changeLogWindow',
			height:'100%',
			resizable:false,
			scrollable:true,
			width:'100%',
			renderTo : Ext.getBody(),
			layout:'fit',
			title:MB.CHANGELOG.changeLog,
			items:[ChangeLogkPanel],
			listeners:{
				close:function(){
					var display = Ext.getCmp('changeLogDisplay');//关闭前判断子集信息显示框是否消除了
					if(display){
						display.destroy();
					}
				}
			}
		});
		var vs = Ext.getBody().getViewSize();  
		ChangeLogkWindow.setSize(vs.width, vs.height);  
		ChangeLogkWindow.show();  
	},
	query:function(){
	    var map = new HashMap();
	    var text= ChangeLogScope.orgCodeSelect.getValue();
	    if(text==null||text==""||text.length==0){
	    	ChangeLogScope.orgCodesetid="";
	    }
	    map.put("optype",ChangeLogScope.info_type+"");
    	map.put("personname",ChangeLogScope.personName);
		map.put("orgName",ChangeLogScope.orgCodesetid);
		map.put("fieldsetName",ChangeLogScope.fieldsetname);
		map.put("fieldName",ChangeLogScope.fieldname);
		map.put("tabid",ChangeLogScope.tabid);
		map.put("year",ChangeLogScope.year);//liuyz 主题名称
	    Rpc({functionId:'MB00006020',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){ 
				var templatejson=result.templatejson;
				var templateFieldjson=result.templateFieldjson;
				var templateFieldSetNameJson=result.templateFieldSetNameJson;
				ChangeLogScope.templatejson = templatejson;
				ChangeLogScope.templateFieldjson = templateFieldjson;
				ChangeLogScope.templateFieldSetNameJson = templateFieldSetNameJson;
		    	ChangeLogScope.loadTable();
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	loadTable:function(){
		var store = Ext.data.StoreManager.lookup('changeInfo_dataStore');
		var templateFieldNameStore = Ext.data.StoreManager.lookup('templateFieldNameStore');
		var templateNameStore = Ext.data.StoreManager.lookup('templateNameStore');
		var pagingtool = Ext.getCmp('changeInfo_pagingtool');
		var inputitem = pagingtool.getInputItem();
		var currentPage = inputitem.getValue();
		templateFieldNameStore.removeAll();
		templateFieldNameStore.add(ChangeLogScope.templateFieldjson);
		templateNameStore.removeAll();
		templateNameStore.add(ChangeLogScope.templatejson);
		if(!ChangeLogScope.fieldsetChange){
			var filename=ChangeLogScope.fieldnameCom.getValue();//bug 50043
			ChangeLogScope.fieldnameCom.setValue(filename);
			var templateName=ChangeLogScope.templateName.getValue();//bug 50044
			ChangeLogScope.templateName.setValue(templateName);
		}else{
			ChangeLogScope.fieldnameCom.setValue("-1");
			ChangeLogScope.templateName.setValue("-1");
		}
		store.load({
			scope: this,
		    callback: function(records, operation, success) {
		    	var recordCount = store.getTotalCount();
				var pageRowCount = pagingtool.child('#inputCount').getValue();
				
		    	if(Math.ceil(recordCount/pageRowCount)<currentPage)
		    		pagingtool.moveLast();
		    	else if(currentPage==0){//currentPage为0 切换回来的场景syl
		    		pagingtool.moveFirst();
		    	}
		    	ChangeLogScope.fieldsetChange = false;
		    }
		});
	},
	optTypeRenered:function(disValue,meta,record){
		var name="";
		if(disValue==1){
			name=MB.CHANGELOG.addRecord;
		}
		if(disValue==2){
			name=MB.CHANGELOG.updateRecord;
		}
		if(disValue==3){
			name=MB.CHANGELOG.deleteRecord;
		}
		return name;
	},
	setNameRenered:function(disValue,meta,record){
		
		var name="";
		if(disValue==null||disValue.length==0){
			name=MB.CHANGELOG.cell;
		}else{
			var subcontent= record.data.sub_content;
			subcontent=getEncodeStr(subcontent);
			var imgName="row_view.png";
			name=disValue+"&nbsp;&nbsp;<a href='javascript:void(0);' onclick=ChangeLogScope.showSubset(this,'"+subcontent+"')><img src="+rootPath+"/images/new_module/"+imgName+" border=0></a>"
		}
		return name;
	},
	showSubset:function(ele,subcontent){
		subcontent=replaceAll(getDecodeStr(subcontent),"＂","\"");
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateNavigationOther': rootPath+'/module/template/templatenavigation/other'
			}
		});
		Ext.require('TemplateNavigationOther.ShowChangeLogSubInfo',function(){
			Ext.create("TemplateNavigationOther.ShowChangeLogSubInfo",{element:ele,subcontent:subcontent});
		});
	}
	
})