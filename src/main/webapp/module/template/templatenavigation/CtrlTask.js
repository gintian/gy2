/**
* 任务监控 页签生成js
* zhaoxg 2016-3-8
*/
Ext.define('TemplateNavigation.CtrlTask',{
   constructor:function(config){
   		if(Ext.getCmp("ctrltask1_toolbar")){//由于点击页签重新加载表格工具，而页签的removeAll（true）未能销毁之前曾经加载过的，导致对象冲突，故有此判断
    		Ext.getCmp("ctrltask1_toolbar").destroy();
    	}
    	CtrlTaskScope = this;
    	CtrlTaskScope.callBackFunc = config.callBackFunc;//回调函数（itemid，panel）  可用于该组件之渲染
    	CtrlTaskScope.itemid = config.itemid;//配合回调函数把该组件渲染到的位置
    	CtrlTaskScope.clienth = config.clienth;
    	CtrlTaskScope.module_id = config.module_id;
    	CtrlTaskScope.bs_flag = "";
    	CtrlTaskScope.query_type = "";
    	CtrlTaskScope.days = config.days;
    	CtrlTaskScope.isShowDelButton=true;
    	var map = new HashMap();
    	map.put("flag","0");//0:首次进入 1：查询进入
    	map.put("module_id",CtrlTaskScope.module_id);
		map.put("bs_flag","1");
		map.put("query_type","1");
		map.put("query_method","1");
		map.put("days",CtrlTaskScope.days);
		map.put("sp_flag","2");
	    Rpc({functionId:'MB00006004',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				var templatejson=result.templatejson;
				CtrlTaskScope.templatejson = templatejson;
		    	var conditions=result.tableConfig;
				var obj = Ext.decode(conditions);
				CtrlTaskScope.templateObj = new BuildTableObj(obj);
	  		  	CtrlTaskScope.init(CtrlTaskScope.templateObj);
	  		  	CtrlTaskScope.isShowDelButton=result.showDelButton;
	  		  	CtrlTaskScope.buttonShow(true,true,CtrlTaskScope.isShowDelButton);
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
    },
   	init:function(templateObj){
   	  	var templateNameStore = Ext.create('Ext.data.Store', {
			fields:['name','id'],
			data:CtrlTaskScope.templatejson
		});
		var templateName = Ext.create('Ext.form.ComboBox', {//模板名称所用的下拉框
		    id:'template_ct_select',
			store: templateNameStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    fieldLabel:MB.LABLE.templateName,
		    labelSeparator: '',
		    margin:'0 10 0 0',
		   // emptyText:"请选择任务类型",
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
					CtrlTaskScope.tabid = combo.getValue();
	     		},
   				select:function(combo,ecords){
					CtrlTaskScope.tabid = combo.getValue();
					CtrlTaskScope.query();
				}
			}
		});
		var radioPanel = Ext.create('Ext.panel.Panel', {
			border : false,
			layout: 'auto',
			items:[	          	
				{
        			xtype      : 'fieldcontainer',
		            defaultType: 'radiofield',
		            layout: 'hbox',
		            fieldLabel:MB.LABLE.taskType,//'任务类型'
		            labelSeparator: '',
		            margin:'0 0 0 0',
		            labelAlign:'right',
		            labelWidth:60,
	            	items: [
	            		{
		                    boxLabel  : MB.LABLE.running,//运行中
		                    name      : 'ctrlmethod',
		                    inputValue: '1',
		                    width     :  60,
		                    checked   :  true,
		                    id        : 'ctrlquery_method0',
		                    listeners:{
				            	'change':function(th,newvalue){
				   					if(newvalue){
				   						CtrlTaskScope.query_method = "1";
				   						CtrlTaskScope.query();
				   						CtrlTaskScope.buttonShow(true,true,CtrlTaskScope.isShowDelButton);
				   					}
				            	}
				            }
		                },
		                {
		                    boxLabel  : MB.LABLE.hasEnded,//终止
		                    name      : 'ctrlmethod',
		                    inputValue: '3',
		                    width     :  60,
		                    id        : 'ctrlquery_method2',
		                    listeners:{
				            	'change':function(th,newvalue){
				   					if(newvalue){
				   						CtrlTaskScope.query_method = "3";
				   						CtrlTaskScope.query();
				   						CtrlTaskScope.buttonShow(false,false,CtrlTaskScope.isShowDelButton);
				   					}
				            	}
				            }
		                },
		                {
		                    boxLabel  : MB.LABLE.finish,//'结束'
		                    name      : 'ctrlmethod',
		                    inputValue: '2',
		                    width     :  60,
		                    id        : 'ctrlquery_method1',
				            listeners:{
				            	'change':function(th,newvalue){
				   					if(newvalue){
				   						CtrlTaskScope.query_method = "2";
				   						CtrlTaskScope.query();
				   						CtrlTaskScope.buttonShow(false,false,CtrlTaskScope.isShowDelButton);
				   						if(Ext.getCmp("repreated_approval")){
				   							Ext.getCmp("repreated_approval").show();
				   						}
				   					}
				            	}
				            }
		                }
		            ]
		         }
		     ]
		})
		var selectStore = Ext.create('Ext.data.Store', {
		    fields: ['flag', 'name'],
		    data : [
		        {"flag":"1", "name":MB.LABLE.byDate},//"按日期最近"
		        {"flag":"2", "name":MB.LABLE.byTimeDomain}//按时间段
		    ]
		});
		var selectPanel = Ext.create('Ext.form.ComboBox', {
		    store: selectStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    //fieldLabel:'过滤条件',
		    margin:'0 5 0 0',
		    //emptyText:"请选择过滤方式",
		    labelAlign:'right',
		    labelWidth:60,
		    displayField: 'name',
		    valueField: 'flag',
		    matchFieldWidth:false,
		    editable:false,
		    width:100,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(selectStore.getAt(0).get('flag'));
					CtrlTaskScope.query_type = combo.getValue();
	     		},
   				select:function(combo,ecords){
					CtrlTaskScope.query_type = combo.getValue();
   					if(CtrlTaskScope.query_type=="2"){
   						Ext.getCmp('ctrldays').hide(); 
   						Ext.getCmp('ctrlfrom').show(); 
   						Ext.getCmp('ctrlto').show(); 
   					}else{
   						Ext.getCmp('ctrldays').show(); 
   						Ext.getCmp('ctrlfrom').hide(); 
   						Ext.getCmp('ctrlto').hide(); 
   					}
				}
			}
		});
		
		var _selectPanel = Ext.create('Ext.panel.Panel', {
			border : false,
			layout: 'hbox',
           	items: [
           		{
		    	xtype:"container",
		    	layout: 'hbox',
		    	id:'ctrldays',
		    	items:[
						{
							xtype:"numberfield",
						    maxValue: 9999, // 最大值
						    minValue: 0, // 最小值 
						    value:CtrlTaskScope.days,  
						    id:'ctrlday',
						    width:60,
						    fieldStyle:'height:20px;',
						    name:'days'
						},
		    	        {
		                	xtype:'label',
		                	margin:'2 0 0 5',
		                	text:'天'
		    	        }
		    	      ]
		            },{
			        xtype: 'datefield',
			        id:'ctrlfrom',
			        name: 'ctrlfrom',
			        labelAlign:'right',
			        labelWidth:20,
			        hidden:true,
			        width:130,
			        format: 'Y-m-d',
			        formatText:'',//提示信息
			        labelSeparator:'',
			        fieldStyle:'height:20px;',
			        fieldLabel: common.label.from//从
			    },{
			        xtype: 'datefield',
			        id:'ctrlto',
			        name: 'ctrlto',
			        labelAlign:'right',
			        labelWidth:20,
			        hidden:true,
			        width:130,
			        format: 'Y-m-d',
			        formatText:'',//提示信息
			        labelSeparator:'',
			        fieldStyle:'height:20px;',
			        fieldLabel: common.label.to//至
			    }
            ]
		})
		var CtrlTaskPanel = Ext.create('Ext.panel.Panel', {
			id:'ctrltaskId',		
			border : false,
			height:CtrlTaskScope.clienth,
			autoScroll:true,
			margin:"0 0 0 2",//lis 20160513
			layout:'fit',
			items:[templateObj.getMainPanel()]
		})
		
		//查询按钮，lis add 20160607
		var queryBut = Ext.create('Ext.Button', {
		    text: common.button.query,
		    handler: function() {
		        CtrlTaskScope.query();
		    }
		});
		//主题名称
		var themeName=Ext.create('Ext.form.field.Text',{
			fieldLabel:'主题名称',
			labelWidth:50,
			fieldStyle:'height:20px;',
			listeners:{
				change:function( textfield , newValue , oldValue , eOpts ){
					CtrlTaskScope.titlename=newValue;
				} 
			}
		})
		
		var toolBar = Ext.getCmp("ctrltask1_toolbar");
		toolBar.insert(radioPanel);
		toolBar.insert(selectPanel);
		toolBar.insert(_selectPanel);
		toolBar.insert(templateName);
		toolBar.insert(themeName);  //liuyz 主题名称输入框
		toolBar.insert(queryBut);
		
		if(CtrlTaskScope.callBackFunc){
            Ext.callback(eval(CtrlTaskScope.callBackFunc),null,[CtrlTaskScope.itemid,CtrlTaskPanel]);
		}
   	},
   	
   	/**
   	 * 按钮是否隐藏 lis 20160613
   	 * @param {} processEndButIsShow 是否隐藏流程终止按钮
   	 * @param {} assignButIsShow 是否隐藏重新分派按钮
   	 * @param {} delButIsShow 是否隐藏删除按钮
   	 */
   	buttonShow:function(processEndButIsShow,assignButIsShow,delButIsShow){
   		var processEndBut = Ext.getCmp('processEndId');
		var assignBut = Ext.getCmp('reAssignId');
		var delBut = Ext.getCmp('ctrlDelId');
		if(processEndBut){
			if(processEndButIsShow)
				processEndBut.show();
			else
				processEndBut.hide();
		}
		if(assignBut){
			if(assignButIsShow)
				assignBut.show();
			else
				assignBut.hide();
		}
		if(delBut){
			if(delButIsShow)
				delBut.show();
			else
				delBut.hide();
		}
		if(CtrlTaskScope.query_method!='2'){
			if(Ext.getCmp("repreated_approval")){
					Ext.getCmp("repreated_approval").hide();
				}
		}
   	},
	getSploop:function(value, metaData, Record){//审批过程
	   	var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id_e;
		return "<a href='javascript:void(0);' onclick=CtrlTaskScope.showCard('"+tabid+"','"+ins_id+"','"+task_id+"')><img src='"+rootPath+"/images/view.gif' width='16' height='16' border='0'></a>";
	},
	
 	getTopic:function(value, metaData, Record){//主题
   		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id_e;
		var name = Record.data.name;
		name = getEncodeStr(name);
		var html="<a href=\"javascript:CtrlTaskScope.showPrint('"+tabid+"','"+ins_id+"','"+task_id+"','"+name+"');\" >"+value+"<a/>";
		return html;
	},
	
	showCard:function(tabid,ins_id,task_id){
		var obj = new Object();
		 obj.tabid=tabid; 
	     obj.task_id=task_id;
	     if(CtrlTaskScope.module_id=='7')
	    	  obj.infor_type="2";
	     else	 if(CtrlTaskScope.module_id=='8')
	    	  obj.infor_type="3";
	     else
	    	  obj.infor_type="1"; 
		 obj.return_flag="4";  
		
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateViewProcessUL':rootPath+'/module/template/templatetoolbar/viewprocess'
			}
		});
	   	Ext.require('TemplateViewProcessUL.TemplateViewProcess',function(){
			Ext.create("TemplateViewProcessUL.TemplateViewProcess",obj);
		});
	},
	getBrowsePrint:function(value, metaData, Record){//浏览打印
		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id_e;
		var name = Record.data.name;
		name = getEncodeStr(name);
		return "<a href='javascript:void(0);' onclick=CtrlTaskScope.showPrint('"+tabid+"','"+ins_id+"','"+task_id+"','"+name+"')><img src='"+rootPath+"/images/new_module/row_view.png' width='16' height='16' border='0'></a>";
	},
	query:function(){
	    var map = new HashMap();
	    map.put("flag","1");//0:首次进入 1：查询进入
	    map.put("tabid",CtrlTaskScope.tabid+"");
    	map.put("module_id",CtrlTaskScope.module_id);
		map.put("bs_flag",CtrlTaskScope.bs_flag);
		map.put("query_type",CtrlTaskScope.query_type);
		map.put("query_method",CtrlTaskScope.query_method);
		map.put("titlename",CtrlTaskScope.titlename);//liuyz 主题名称
		var day = Ext.getCmp("ctrlday").getValue();
		if(day >= 0 && day <=9999)
			map.put("days",day+"");
		else{
			Ext.showAlert(MB.MSG.laterDateError);//"最近日期输入有误，请重新输入！"
			return;
		};
		
		var fromDate = Ext.getCmp("ctrlfrom").getValue();
		var toDate = Ext.getCmp("ctrlto").getValue();
		if(CtrlTaskScope.query_type == "2" && fromDate && toDate && (fromDate>toDate)){
			Ext.showAlert("开始日期不能大于结束日期！");
			return;
		};
		var from = Ext.Date.format(fromDate, "Y-m-d");
		var to = Ext.Date.format(toDate, "Y-m-d");
		map.put("start_date",from);
		map.put("end_date",to);
		map.put("sp_flag","2");
	    Rpc({functionId:'MB00006004',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){ 
				var templatejson=result.templatejson;
				CtrlTaskScope.templatejson = templatejson;
				var tempSelectStore=Ext.getCmp('template_ct_select').store; 
				if(CtrlTaskScope.tabid==-1){
					tempSelectStore.removeAll();
					tempSelectStore.add(CtrlTaskScope.templatejson); 
				}
				CtrlTaskScope.isShowDelButton=result.showDelButton;
		    	CtrlTaskScope.loadTable();
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	loadTable:function(){
		var store = Ext.data.StoreManager.lookup('ctrltask1_dataStore');
        //liuyz bug 26508 用户点击返回每次都返回第一页。
		//store.currentPage=1;
		var pagingtool = Ext.getCmp('ctrltask1_pagingtool');
		var inputitem = pagingtool.getInputItem();
		var currentPage = inputitem.getValue();
		store.load({
			scope: this,
		    callback: function(records, operation, success) {
		    	var recordCount = store.getTotalCount();
				var pageRowCount = pagingtool.child('#inputCount').getValue();
		    	if(Math.ceil(recordCount/pageRowCount)<currentPage)
		    		pagingtool.moveLast();
		    }
		});
	},
	showPrint:function(tabid,ins_id,task_id,name){ 
		CtrlTaskScope.myMask = Ext.getCmp("maskId");
      	if(!!!CtrlTaskScope.myMask){
	      	CtrlTaskScope.myMask = new Ext.LoadMask({
	      		id:"maskId",
			    target : Ext.getCmp("template")
			});
		}
		CtrlTaskScope.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="4";
		templateObj.module_id=CtrlTaskScope.module_id;
		templateObj.approve_flag="0";
		templateObj.task_id=task_id;
		templateObj.sp_flag="2";
		templateObj.ins_id=ins_id;
		templateObj.callBack_init="CtrlTaskScope.tempFunc";
		templateObj.callBack_close="CtrlTaskScope.goBack";
		var isDelete = false;
		name = getDecodeStr(name);
		if(name.indexOf(MB.MSG.bydelete)!=-1&&CtrlTaskScope.query_method == "3"){
			isDelete = true;
			templateObj.other_param="isDelete="+isDelete+"";
		}
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
   	tempFunc:function(){
		CtrlTaskScope.myMask.hide();
		Ext.getCmp("template").removeAll(false);
		Ext.getCmp("template").add(templateMain_me.mainPanel);
	},
	goBack:function(){
		Ext.getCmp("template").removeAll(true);
		Ext.getCmp("template").add(templatenavigation.tabs);
		CtrlTaskScope.loadTable();
	},
	
/*	*//**
	 * 删除 lis 20160419
	 *//*
	deleteTask:function(){
		if(!CtrlTaskScope.validate())
			return;

		//确认删除
		Ext.Msg.confirm(common.button.promptmessage,common.msg.isDelete,function(id){if(id=='yes'){
			var hashvo = new HashMap();
			CtrlTaskScope.prepareParameter(hashvo)
			hashvo.put("tablekey","ctrltask");
			//将数据传入后台，key为‘deletedata’
			Rpc({functionId:"MB00006008",scope:this,success:function(res){
				var resultObj = Ext.decode(res.responseText);
				if(resultObj.result!=undefined && !resultObj.result){
					Ext.Msg.alert(common.button.promptmessage,resultObj.hinttext?resultObj.hinttext:common.label.deleteFailed+"！");//删除失败
					return;
				}
				CtrlTaskScope.loadTable();	
			}},hashvo);
		}});
	
	},*/
	
	/**
	 * 导出excel lis 20160422
	 */
	exportExcel:function(){
		//获取选中行数据
		var selectRecord = CtrlTaskScope.templateObj.tablePanel.getSelectionModel().getSelection();
		//var isSelectAll = CtrlTaskScope.templateObj.tablePanel.getSelectionModel().doSelectAll;
		//var selectRecordCount = CtrlTaskScope.templateObj.tablePanel.getStore().getCount();
		var isExportAll = false;
		if(selectRecord.length<1){//如果没有选中数据，则全部导出，复杂只导出选中数据
			isExportAll = true;
		}

		var records = [];
		for(var i=0;i<selectRecord.length;i++){
			records.push(selectRecord[i].data);
		}
		var hashvo = new HashMap();
		hashvo.put("selectdata",records);
		//hashvo.put("doSelectAll",isSelectAll);
		hashvo.put("isExportAll",isExportAll);
		//将数据传入后台，key为‘deletedata’
		Rpc({functionId:"MB00006010",scope:this,success:function(response){
			var result = Ext.decode(response.responseText);  	
			if (result.succeed) { 
				var outName = result.fileName;	
				var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"zip");
			} else {  
				Ext.showAlert(result.message+"！");
			}
		}},hashvo);
	},
	
		
	//点击重新分派按钮  lis 20160425
	selectObject:function(objectType){
		if(!CtrlTaskScope.validate())
			return;
		if(objectType=="3"){
			var map = new HashMap();
		    map.put('codesetidstr',"UM,UN,@K");
			map.put('codesource','');
			map.put('nmodule','4');
			map.put('ctrltype','3');
			map.put('parentid','');
			map.put('searchtext',encodeURI(""));
			map.put('multiple',false);
			map.put('isencrypt',true);
			map.put('confirmtype','1');
			map.put('title','组织单元');
			map.put('callbackfunc',CtrlTaskScope.getOrgList);
			Ext.require('EHR.orgTreePicker.OrgTreePicker', function(){          
				Ext.create('EHR.orgTreePicker.OrgTreePicker',{map:map});
			},this);
		}else if(objectType=="4"){//组织机构
			var f = document.getElementById("organization");
			var p = new PersonPicker({
				multiple: false,
				isSelfUser:false,//是否选择自助用户
				isMiddle:true,//是否居中显示
				isPrivExpression:false,//是否启用人员范围（含高级条件）
				selfUserIsExceptMe:false,//修正业务用户重新分派不能分派给自己。 liuyz 31333
				callback: function (obj) {
					CtrlTaskScope.selectUser(obj);
				}
			}, f);
			p.open();
	    }else if(objectType=="1"){
	    	var f = document.getElementById("organization");
			var p = new PersonPicker({
				multiple: false,
				isSelfUser:true,//是否选择自助用户
				isMiddle:true,//是否居中显示
				isPrivExpression:false,//是否启用人员范围（含高级条件）
				callback: function (obj) {
					CtrlTaskScope.selectPeople(obj)
				}
			}, f);
			p.open();
	    }else{
	    	Ext.require('EHR.rolepicker.RolePicker', function(){          
				Ext.create('EHR.rolepicker.RolePicker',{callBackFunc:CtrlTaskScope.getRolesList,multiple:false});
			},this);
	    }
	},
	
	//选中组织机构  lis 20160425
	getOrgList:function(record){
		var actorid = "";
    	var actorname = "";
    	var codesetid = "";
    	for(var i=0;i<record.length;i++){
    		actorid = record[i].id;
    		actorname = record[i].text
    		codesetid = record[i].codesetid;
    	}

    	var hashvo = new HashMap();
		CtrlTaskScope.prepareParameter(hashvo);
		hashvo.put("actorid",actorid);
		hashvo.put("actorname",actorname);
		hashvo.put("codesetid",codesetid);
		hashvo.put("actortype","3");
		CtrlTaskScope.reAssignTask(hashvo);
	
    },
    
    //选中业务用户  lis 20160425
	selectUser:function(appealObject){
		var actorid = appealObject.id;
		var actorname = appealObject.name
		var hashvo = new HashMap();
		CtrlTaskScope.prepareParameter(hashvo);
		hashvo.put("actorid",actorid);
		hashvo.put("actorname",actorname);
		hashvo.put("actortype","4");
		
		CtrlTaskScope.reAssignTask(hashvo);
	},
	
	//选中自助人员
	selectPeople:function(appealObject){
		var actorid = appealObject.id;
		var actorname = appealObject.name
		var hashvo = new HashMap();
		CtrlTaskScope.prepareParameter(hashvo);
		hashvo.put("actorid",actorid);
		hashvo.put("actorname",actorname);
		hashvo.put("actortype","1");
		
		CtrlTaskScope.reAssignTask(hashvo);
	},
	
	//选中角色  lis 20160425
    getRolesList:function(selectRecords){
    	var hashvo = new HashMap();
		CtrlTaskScope.prepareParameter(hashvo);
		hashvo.put("actorid",selectRecords[0].role_id_e);
		hashvo.put("actorname",selectRecords[0].role_name);
		hashvo.put("actortype","2");
		CtrlTaskScope.reAssignTask(hashvo);
		//判断是否有特殊角色 暂时未做
		/*Rpc({functionId:"MB00005002",scope:this,success:function(response){
			var result = Ext.decode(response.responseText);  	
			if (result.succeed) { 
				if(result.selectSpecial){
					
				}else{
					CtrlTaskScope.reAssignTask(hashvo);
				}
			}else{  
				Ext.MessageBox.show({  
					title : common.button.promptmessage,  
					msg : result.message+"！", 
					buttons: Ext.Msg.OK,
					icon: Ext.MessageBox.INFO  
				});  
			}
		}},hashvo)*/
	
    },
    
    //重新分派  lis 20160425
    reAssignTask:function(hashvo){
    	Rpc({functionId:"MB00006011",scope:this,success:function(response){
			var result = Ext.decode(response.responseText);  	
			if (result.succeed) { 
				CtrlTaskScope.loadTable();	
			}else{  
				Ext.showAlert(result.message+"！");
			}
		}},hashvo)	
    },
    
    //验证是否选中  lis 20160425
    validate:function( ){
    	//获取选中行数据
		var selectRecord = CtrlTaskScope.templateObj.tablePanel.getSelectionModel().getSelection();
		//var isSelectAll = CtrlTaskScope.templateObj.tablePanel.getSelectionModel().doSelectAll;
		//var selectRecordCount = CtrlTaskScope.templateObj.tablePanel.getStore().getCount();
		if(selectRecord.length == 0){
			Ext.showAlert(MB.MSG.selectData);//请选择数据！
			return false;
		}else{
			return true;
		}
    } ,
    
    //预处理参数 lis 20160425
    prepareParameter:function(hashvo){
    	//获取选中行数据
		var selectRecord = CtrlTaskScope.templateObj.tablePanel.getSelectionModel().getSelection();
		//var isSelectAll = CtrlTaskScope.templateObj.tablePanel.getSelectionModel().doSelectAll;
		//var selectRecordCount = CtrlTaskScope.templateObj.tablePanel.getStore().getCount();
		if(hashvo){
			var records = [];
			for(var i=0;i<selectRecord.length;i++){
				records.push(selectRecord[i].data);
			}
			hashvo.put("selectedList",records);
			//hashvo.put("doSelectAll",isSelectAll);
		}
    },
    
    // 流程终止  lis 20160520
    processEnd:function(){
    	if(!CtrlTaskScope.validate())
			return;
    	Ext.showConfirm("您确定要终止选中任务吗？",function(id){
    		if(id=='yes'){
    			var hashvo = new HashMap();
    	    	CtrlTaskScope.prepareParameter(hashvo);
    	    	Rpc({functionId:"MB00006012",scope:this,success:function(response){
    				var result = Ext.decode(response.responseText);  	
    				if (result.succeed) { 
    					CtrlTaskScope.loadTable();	
    				}else{  
    					Ext.showAlert(result.message+"！");
    				}
    			}},hashvo)	
    		}
    	});
    },
    //显示审批人角色详细
    getRoleInfo:function(value, metaData, Record){
		var tabid = Record.data.tabid;
		var actorname = Record.data.actorname;
		var task_id = Record.data.task_id_e;
		var actor_type = Record.data.actor_type;
		if(actor_type=='2')
			return "<a href='javascript:void(0);' onclick=CtrlTaskScope.showRoleInfo(this,\""+tabid+"\",\""+task_id+"\")>"+actorname+"<a/>";
		else if(actor_type=='5'){
			return "<a href='javascript:void(0);' onclick=CtrlTaskScope.showRoleInfo(this,\""+tabid+"\",\""+task_id+"\")>"+actorname+"<a/>";
		}else
		    return actorname;
	},
	showRoleInfo:function(e,tabid,task_id){
	    Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateNavigationOther': rootPath+'/module/template/templatenavigation/other'
			}
		});
		Ext.require('TemplateNavigationOther.DisplayRoleInfo',function(){
			Ext.create("TemplateNavigationOther.DisplayRoleInfo",{element:e,tabid:tabid,task_id:task_id});
		});
	},
	showChangeInfo:function(){//显示变动日志界面
		
		Ext.require('TemplateNavigation.TemplateChangeLog',function(){
			Ext.create("TemplateNavigation.TemplateChangeLog",{module_id:CtrlTaskScope.module_id});
		});
	},
	processArchiving:function(){
		Ext.require('TemplateNavigation.TemplateProcessArchiving',function(){
			Ext.create("TemplateNavigation.TemplateProcessArchiving",{});
		});
	},
	repreated:function(){//重复报批
	 var items=CtrlTaskScope.templateObj.tablePanel.getSelectionModel().getSelection();
	 if(items.length>0){
		 Ext.showConfirm("您确认要重提结束单据的记录吗？",function(value){
				if(value=="yes"){
					 Ext.MessageBox.wait("正在执行重复提交结束单据...", "等待");
					var list=[];
					for(var i=0;i<items.length;i++){
						var map=new HashMap();
						var data=items[i].data;
						map.put("tabid",data.tabid);
						map.put("ins_id",data.ins_id);
						map.put("task_id",data.task_id_e);
						list.push(map);
					}
					var hashvo = new HashMap();
					hashvo.put("dataList",list);
					hashvo.put("flag","repreate");
	    	    	Rpc({functionId:"MB00005005",async:true,scope:this,success:function(rs){
	    	    		var result = Ext.decode(rs.responseText);
	    	    		Ext.MessageBox.close(); 	 
	    	    		if (!result.msg) { 
	    						
	    				}else{  
	    					Ext.showAlert(result.msg+"！");
	    				}
	    			}},hashvo)	
				}else{
					return;
				}
			});
	 }else{
		 Ext.showAlert("请选择需要重提结束单据的数据！");
		 return
	 }
	
	}
})