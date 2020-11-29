/**
* 设置简单查询指标列表 
*  lis 
*  2015-12-16
* 
*	调用方法：		
			Ext.require('EHR.selectfield.QueryFieldSet',function(){
				Ext.create("EHR.selectfield.QueryFieldSet",{imodule:imodule});
			})
*	参数说明：
			imodule:模块号；0:薪资类别,1:人事异动,2:所得税管理结构设置
			type：同一个模块不同位置；
			dataMap:不同模块调用时传入的参数；
			saveCallbackfunc：回调函数，把条件返还给调用页面并触发一个自定义的方法，用于保存条件；
			queryCallbackfunc:回调函数，把条件返还给调用页面并触发一个自定义的方法，用于查询；
**/
Ext.define('EHR.selectfield.QueryFieldSet',{
	
/**
* 构造方法
* config 构造参数
*/
constructor:function(config){
	queryField_me = this;
	queryField_me.selectFieldConfig = config.selectFieldConfig;
	queryField_me.imodule = config.imodule;//模块号;0:薪资类别,1:人事异动,2:所得税管理结构设置9:职称
	queryField_me.type = config.type;//不同操作
	queryField_me.saveCallbackfunc = config.saveCallbackfunc;//保存回调函数
	queryField_me.queryCallbackfunc = config.queryCallbackfunc;//查询回调函数
	queryField_me.isShowResult = config.isShowResult || false;
	queryField_me.info_type = config.dataMap.info_type;
	queryField_me.nbases=config.nbases;
	var dataMap = config.dataMap;
	if(dataMap){
		/**薪资类别-薪资属性-简单条件 start */
		queryField_me.salaryid = dataMap.salaryid;//薪资类别id
		queryField_me.expr = dataMap.expr;//表达式逻辑关系，1*2+3
		/**薪资类别-薪资属性-简单条件 end */


        /** 职称评审 start **/
        queryField_me.isFilterSelectedExpert = dataMap.isFilterSelectedExpert;// 是否过滤掉已选专家
        /** 职称评审 end **/



		queryField_me.buttonText = dataMap.buttonText;
		queryField_me.isShowButton = true;
		if(!!!queryField_me.buttonText){
			queryField_me.isShowButton = false;
		}
		if(dataMap.expression){
			queryField_me.expression = decode(dataMap.expression);
		}
		queryField_me.right_fields = dataMap.right_fields;//已选指标id
		queryField_me.queryType = dataMap.queryType;//"1"是通用查询，"0"是简单条件，"2"是简单查询
		queryField_me.isShowDb = true;//是否显示人员库
		if(dataMap.info_type && (dataMap.info_type == "2" || dataMap.info_type == "3")){//人事异动模板，人员模板显示人员库，部门和岗位的不显示
			queryField_me.isShowDb = false;
		}
		// 按检索条件和人员范围 begin
		if(dataMap.filter_factor)
		{
			queryField_me.filter_factor=dataMap.filter_factor;
		}
		if(dataMap.priv)
		{
			queryField_me.priv=dataMap.priv;
		}
		// 按检索条件和人员范围 end
	}
	this.panelWidth = 535;//panel宽度
	this.panelHeight = 171;//panel高度
	this.panelHeight2 = 333;//panel高度
	 
	var map = new HashMap();
	map.put("imodule",queryField_me.imodule);
	map.put("type",queryField_me.type);
	map.put("right_fields",queryField_me.right_fields);
	map.put("expr",queryField_me.expr);
	map.put("expression",queryField_me.expression);
	map.put("queryType",queryField_me.queryType);
	map.put("nbases",queryField_me.nbases);
	
	queryField_me.createSalary();//初始化显示窗口
	Rpc({functionId:'ZJ100000125',success:function(response,action){
		var result = Ext.decode(response.responseText);
		if (result.succeed) { 
			queryField_me.dataList = result.factorlist;
			if(result.expression)
				queryField_me.expression = decode(result.expression);
			queryField_me.initPanel();//初始化数据显示面板
		    if(queryField_me.queryType == "1"){//通用查询
		    	queryField_me.initExpression();
		    	queryField_me.dbList = result.dbList;
		    	if(queryField_me.isShowDb)
		    		queryField_me.initDbList();
		    	queryField_me.initCheckBoxPanel();//初始化多选矿panel
			 }else if(queryField_me.queryType == "2"){//简单查询
			    	queryField_me.dbList = result.dbList;
			    	if(queryField_me.isShowDb)
			    		queryField_me.initDbList();
			    	queryField_me.initCheckBoxPanel();//初始化多选矿panel
			 }
		}else {  
			Ext.showAlert(result.message+"！"); 
			var queryField_win = Ext.getCmp("queryField_win");
			if(queryField_win)
				queryField_win.close();
		}
	}},map);
},

 createSalary:function()  
 {
	var height = this.panelHeight2;
	if(queryField_me.queryType == "1"){
		height = this.panelHeight;
	}else if(queryField_me.queryType == "2"){
		height = 310;
	}
	var mainPanel = Ext.widget({
		xtype:'form',
		border:false,
		id:'mainForm',
		layout:'vbox',
		items:[{
			xtype:'panel',
			width: this.panelWidth,
			height: height, 
			id:'gridPanelId'
		},{
			xtype:'container',
			id:'expressionPanelId'
		},{
			xtype:'container',
			border:false,
			id:'checkPanelId'
		}]
	});
	
	//生成弹出得window
	queryField_me.win=Ext.widget("window",{
		  title : queryField_me.queryType == "0"||queryField_me.queryType == "2"?common.button.hquery:common.button.cquery,
		  id:'queryField_win',//haosl add 2017-07-06
		  width: 550,
		  height: 420, 
		  bodyPadding:'3 2',
		  minButtonWidth:45,
		  modal:true,
		  resizable:false,
		  closeAction:'destroy',
		  layout: 'hbox',
		  items: [mainPanel],
	      bbar:[
	                 {xtype:'tbfill'},
	                 {
		          			text:common.label.preStep, //上一步
		          			handler:function(){
		          				queryField_me.preStep();
		          				queryField_me.win.close();
		          			}
		          		},
		          		{
		          			text:common.button.query, //查询
		          			hidden:queryField_me.queryType=="1"||queryField_me.queryType == "2"?false:true,
		          			handler:function(){
	                	 		queryField_me.query();
	                 		}
		          		},
		          		{
		          			text:queryField_me.buttonText, //确定
		          			hidden:!queryField_me.isShowButton?true:false,
		          			handler:function(){
		          				queryField_me.saveCond();
		          			}
		          		},
		          		{xtype:'tbfill'}
		           ]     
			}).show();
 },
 
 //初始化逻辑符下拉框
 initLogComBox:function(){
	 var comBoxStore = Ext.create('Ext.data.Store', {
		    fields: ['log', 'name'],
		    data : [
		        {"log":"*", "name":common.button.And},//且
		        {"log":"+", "name":common.button.Or}//或
		    ]
		});
 	//子集列表数据store
 	var comBox = Ext.widget('combo',{
			store:comBoxStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    editable: false,
		    forceSelection: true,
		    displayField: 'name',//显示的值
		    valueField: 'log'//隐藏的值
 	});
 	
 	return comBox;
 },
 
 //初始化操作符
 initOperComBox:function(){
	 var comBoxStore = Ext.create('Ext.data.Store', {
		    fields: ['oper', 'name'],
		    data : [
		            {"oper":"=", "name":"="},
			        {"oper":">", "name":">"},
			        {"oper":">=", "name":">="},
			        {"oper":"<", "name":"<"},
			        {"oper":"<=", "name":"<="},
			        {"oper":"<>", "name":"<>"}
		    ]
		});

 	//子集列表数据store
 	var comBox = Ext.widget('combo',{
			store:comBoxStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    editable: false,
		    forceSelection: true,
		    displayField: 'name',//显示的值
		    valueField: 'oper'//隐藏的值
 	});
 	
 	return comBox;
 },
 
 //初始化列
 initColumn:function(logComBox,operComBox){
	 	var tempColumns = new Array();//列对象
	 	
	 		var obj = new Object();
	 		obj.flex=20;
	 		obj.dataIndex='log';
	 		obj.align = 'center';
	 		if(queryField_me.queryType == "0"||queryField_me.queryType == "2"){//简单条件
	 			obj.header=common.label.logic;
	 			obj.editor = logComBox;
	 		}else{
	 			obj.header=common.label.order;//序号
	 		}
	 		obj.renderer = queryField_me.renderLog;
	 		obj.menuDisabled=true;
	 		tempColumns[0]=obj;
		
		var obj = new Object();
		obj.text=common.label.field;//查询指标
		obj.flex=50;
		obj.menuDisabled=true;
		obj.dataIndex='hz';
		tempColumns[1]=obj;
		
		var obj = new Object();
		obj.text=common.label.relation;//逻辑关系
		obj.flex=20;
		obj.menuDisabled=true;
		obj.dataIndex='oper';
		obj.align = 'center';
		obj.editor = operComBox;
		tempColumns[2]=obj;
		
		var obj = new Object();
		obj.text=common.label.value;//指标值
		obj.menuDisabled=true;
		obj.field = 'codecolumn';
		obj.dataIndex='value';
		obj.flex=50;
		obj.renderer = queryField_me.renderValue;
		tempColumns[3]=obj;
		
		return tempColumns;
 },
 
 //初始化人员库
 initDbList:function(){
 	var container = Ext.widget({
			 xtype:'container',
			 height:'100%',
			 scrollable:'y',
			 layout:'hbox',
			 border:false 
		 })
 	var containerLeft = Ext.widget({
			 xtype:'container',
			 margin:'2 0 0 0',
			 width:45,
			 layout: {
		        type: 'vbox'
		    },
			 border:false
		 })
 	var containerRight = Ext.widget({
			 xtype:'container',
			 layout:'vbox',
			 border:false
		 })
	 var label = Ext.widget({
		 	xtype: 'label',
		 	margin:'0 5 0 0',
	        text: "人员库"
		});
	containerLeft.add(label);
	 var check = null;
	 var containerCheck = null;
	 Ext.each(queryField_me.dbList,function(db,index){
			if(index%4 == 0){//每行显示4个人员库，每行生成一个容器
				containerCheck = Ext.widget({
					 xtype:'container',
					 layout:'hbox',
					 border:false
				})
				containerRight.add(containerCheck);
			}
			
			check = Ext.create('Ext.form.field.Checkbox',{
				 boxLabel  : db.dataName,
				 margin	   : '0 15 0 0',
                 name      : 'dbName',
                 checked   : index == 0?true:false,
                 inputValue: db.dataValue
			});
			containerCheck.add(check);

	 })
	 container.add([containerLeft,containerRight]);
	 Ext.getCmp('tbarId').add(container);
 },
 
 //初始化数据
 initPanel:function(){
	 var store = Ext.create('Ext.data.Store', {
		 storeId:'panelId',
		 fields:['log','fieldname','fieldtype','hz','codeid','oper','value','hzvalue','itemlen']
	 });	
	 store.load();	
	 if(queryField_me.dataList){
			Ext.each(queryField_me.dataList,function(record,index){
				var obj = new Object();
				obj.log = record.log;
				obj.fieldname = record.fieldname;
				obj.fieldtype = record.fieldtype;
				obj.hz = record.hz;
				obj.codeid = record.codeid;
				obj.oper = record.oper;
				obj.value = record.value;
				obj.hzvalue = record.hzvalue;
				obj.itemlen = record.itemlen;
				store.insert(index,[obj]);
			})
		}
	 var logComBox = queryField_me.initLogComBox();//初始化逻辑符下拉框
	 var operComBox = queryField_me.initOperComBox();//初始化操作符
	 var cloumn = queryField_me.initColumn(logComBox,operComBox);//初始化列
	 var tbar = undefined;
	 if(queryField_me.isShowDb && (queryField_me.queryType == "1"||queryField_me.queryType == "2")){
		 tbar = [{
			 xtype:'container',
			 height:50,
			 width:"100%",
			 id:'tbarId',
			 border:false
		 }];
	 }
		 
	 //面板
	 queryField_me.panel = Ext.create('Ext.grid.Panel', {
		store:store,
		width:533,
		tbar:tbar,
	 	height: queryField_me.queryType == "1"||queryField_me.queryType == "2"?this.panelHeight:this.panelHeight2,
	 	border:false,
	 	columnLines:true,
	 	rowLines:true,
         multiSelect:true,
		 sortableColumns:false,
	 	forceFit:true,
	 	plugins: [
	 	         Ext.create('Ext.grid.plugin.CellEditing', {
	 	             clicksToEdit: 1,
	 	            listeners:{
	 	 	    	'beforeedit':function(edit,e){//编辑之前触发的事件
	 					queryField_me.editorSet(e);
	 	     		},
	 	     		'edit':function(edit,e){//编辑后触发
	 					queryField_me.edit(e);
	 	     		}
	 		 		}
	 	         })
	 	     ],
		columns: cloumn,
		renderTo:Ext.getBody()
	});
	Ext.getCmp('gridPanelId').add(queryField_me.panel);
 },
 
 //初始化因式表达式
 initExpression:function(){
	 var expression = Ext.widget({
		 xtype:'panel',
		 border:false,
		 bodyBorder:false,
		 tbar:[
		       {
		    	   xtype:'label',
		    	   border:false,
		    	   text:common.label.expression//因式表达式
		       }
		 ],
		 items:[
		        {
		        	 xtype     : 'textareafield',
		        	 name	   :'expression',
		        	 height    :80,
		        	 width     :this.panelWidth,
		             grow      : true,
		             id        :'expressionId',
		             value	   :queryField_me.expression
		        }
		 ],
		 bbar:[
		       {
		    	   	text:"(",
		    	 	listeners:{"click":function(){ queryField_me.symbol('expressionId',this.text);}}
		       },
		       {
		    	   	 text:common.button.And,//"且"
		    	     listeners:{"click":function(){ queryField_me.symbol('expressionId',"*");}}
			   },
		       {
			    	 text:common.button.Not,//"非"
			    	 listeners:{"click":function(){ queryField_me.symbol('expressionId',"!");}}
			   },
		       {
			    	 text:")",
			    	 listeners:{"click":function(){ queryField_me.symbol('expressionId',this.text);}}
			   },
		       {
			    	 text:common.button.Or,//或
			    	 listeners:{"click":function(){ queryField_me.symbol('expressionId',"+");}}
			   }
		 ]
	 });
	 Ext.getCmp('expressionPanelId').add(expression);
 },
 
 //初始化多选框
 initCheckBoxPanel:function(){
	 var checksPanel = Ext.widget({
		 xtype:'container',
		 items: [
		         {
		             xtype: 'form',
		             defaultType: 'checkboxfield',
		             width: this.panelWidth,
		             border:false,
		             layout: {
		                 type: 'hbox',
		                 pack:'left'
		             },
		             items: [
		             		
		                 {
		                     boxLabel  : common.label.like,//模糊查询
		                     padding:'0 5 0 0',
		                     name      : 'like',
		                     inputValue: 'like',
		                     id        : 'likeQuery'
		                 }, {
		                     boxLabel  : common.label.search_result,//查询结果
		                     padding:'0 5 0 0',
		                     name      : 'search_result',
		                     hidden    :queryField_me.imodule=="1"||queryField_me.imodule=="9"?false:true,
		                     inputValue: 'search_result',
		                     id        : 'search_result'
		                 }, {
		                     boxLabel  : common.label.history,//历史记录查询
		                     padding:'0 5 0 0',
		                     name      : 'history',
		                     inputValue: 'history',
		                     id        : 'historyQuery'
		                 }, {
		                     boxLabel  : common.label.second,//二次查询
		                     hidden    :queryField_me.imodule=="1"||queryField_me.imodule=="9"?true:false,
		                     name      : 'second',
		                     inputValue: 'second',
		                     id        : 'secondQuery'
		                 }
		             ]
		         }
		     ]
	 });
	 
	 Ext.getCmp('checkPanelId').add(checksPanel);
 },
 
 //渲染逻辑符列
 renderLog:function(value,a,b,rowIndex){
	 if(queryField_me.queryType == "0"||queryField_me.queryType == "2"){//简单查询
		 if(rowIndex!=0){
			 if('*'==value)
				 return common.button.And;
			 else if('+'==value)
				 return common.button.Or;
			 else return "";
		 }else
		 	 return "";
	 }else if(queryField_me.queryType == "1"){//通用查询
		 return rowIndex+1;//序号从1开始
	 }
	 
 },
 
//渲染查询值
 renderValue:function(value,column,record,rowIndex,columnIndex,store){
	 if(record.get('codeid') != 0 && value.indexOf('`') != -1){//indexOf从0开始的，不能只>0
		 var arr = value.split('`');
		 return arr[1];
	 }else
		 return value;
 },
 
//逻辑符写入因式表达式
 symbol:function(id,strexpr){
 	Ext.getCmp(id).focus();
 	if(Ext.isIE10m){
 		var element = document.selection;
 		if (element!=null) {
 			var rge = element.createRange();
 			if (rge!=null)	
 			rge.text=strexpr;
 		}
 	}else{
 	     var rulearea = Ext.getCmp(id);
         var rulevalue = rulearea.getValue();
         var start = rulearea.inputEl.dom.selectionStart;
         var end = rulearea.inputEl.dom.selectionEnd;
         var oriValue = rulearea.getValue().toString();  
         rulearea.setValue(oriValue.substring(0,start) + strexpr + oriValue.substring(end)); 
     }
 },
 //编辑后，去掉修改标记
 edit:function(e){
	 if(e.field=="value" || e.field=="oper" || e.field=="log"){
		 e.record.commit();//编辑后直接提交，去掉修改标记
         var likeQuery = Ext.getCmp("likeQuery");
         if(likeQuery){
             likeQuery.focus();//选择了代码类型指标的时候，选择下拉框之后的光标问题[36514],将光标移到别的位置，这样就不会出现再次选择不能选择的问题
         }
	 }
 },
 
 //指标值编辑时根据不同数据类型加载不同编辑器
 editorSet:function(e){
	 //第一行第一列不能编辑
	 if(e.rowIdx  == 0 && e.field=="log"){
	 		e.cancel = true;//Ext6必须加这个才好使
			return false;
	 }
	 if(e.field=="value"){
		 var codeid = e.record.get('codeid');//UM
		 if(queryField_me.info_type =='2'){//单位登记表特殊处理 单位名称选择部门
			 if(e.record.get('fieldname')=='b0110'){
				 codeid='UM';
			 }
		 }
		 var fieldType = e.record.get('fieldtype');
		 var itemlen = e.record.get('itemlen');
		 
		 //销毁上次生成的对象。
		 var lastId = Ext.getCmp('codesetid');
		 if(lastId)
		 	lastId.destroy();
		 if(codeid != "0"){
		 	 var ctrltype = "3";//0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
			 var codeComBox = Ext.widget({
				 xtype : 'codecomboxfield',
				 border : false,
				 width:100,
				 id:'codesetid',
				 editable:true,//通用查询代码类允许根据代码类模糊搜索
				 nmodule:queryField_me.imodule==0?"1":queryField_me.imodule,//薪资模块走业务范围-操作单位-人员范围，getUnitIdByBusi方法1是薪资发放，但是imodule=0代表薪资，控件不方便改，所以这块强行判断下 zhaoxg add 2016-12-28
				 ctrltype:ctrltype,
				 codesetid : codeid,
				 inputable:true,
				 onlySelectCodeset:false//通用查询能查询父节点。
			 });
			 e.column.setEditor(codeComBox);
		 }else{
			 if(fieldType == "N"){//是数字类型
				 e.column.setEditor({
					 xtype:'numberfield',
					 id:'codesetid',
					 hideTrigger:true,
					 maxLength:30
				 }); 
			 }else if(fieldType == "A"){//是字符型
				 e.column.setEditor({
				 	id:'codesetid',
					 xtype:'textfield',
					 maxLength:30
				 });
			 }else if(fieldType == "D"){//是日期型
				 var format = 'Y-m-d';
		          if(itemlen==4)
		          	format = 'Y';
		          else if(itemlen==7)
		          	format = 'Y-m';
		          else if(itemlen==10)
		          	format = 'Y-m-d';
		          else if(itemlen==16)
		          	format = 'Y-m-d H:i';
		          else if(itemlen==18)
		            format = 'Y-m-d H:i:s';
				 e.column.setEditor({
				 	id:'codesetid',
					 xtype:'datetimefield',
					 format: format,
					 editable:true
				 });
			 }
		 }
	 }
 },
 
 //生成因式表达式
 genExpression:function(){
	 var _str="1";
	 if(queryField_me.queryType == "0"||queryField_me.queryType == "2"){//简单查询
		 var n=1;
		 var panelStore = Ext.data.StoreManager.lookup('panelId');
		 panelStore.each(function(record,index,count){ //遍历每一条数据
			 if(index != 0){//从第二条数据开始
				 n++;
				 _str+=record.get('log')+n; 
			 };
		 });
	 }else if(queryField_me.queryType == "1"){//通用查询
		 _str = Ext.getCmp('expressionId').getValue(); 
	 }
	 return _str;
 },
 //校验
 submitCond:function(needValidate)
 {
	var isValidate = true;
	var expression= queryField_me.genExpression(); 
	var arr = new Array();
	var panelStore = Ext.data.StoreManager.lookup('panelId');
	if(panelStore){
		panelStore.each(function(record,index,count){ //遍历每一条数据
			var oobj=new Object();
			oobj.value=record.get('value')+"";
			oobj.oper=record.get('oper');
			oobj.fieldname=record.get('fieldname');   
			oobj.log=record.get('log');
			oobj.hz=record.get('hz'); 
			oobj.codeid=record.get('codeid'); 
			oobj.fieldtype=record.get('fieldtype'); 
			arr[index]=oobj;
		});
	}
	queryField_me.dataList = arr;// lis 20160406
    var map=new HashMap();
    map.put("expression",encode(expression)); 
    map.put("type",queryField_me.type);
    map.put("arr",arr); 
    map.put("needValidate",needValidate); //是否需要校验
    Rpc({functionId:'ZJ100000123',async:false,success:function(form,action){
    	 var result = Ext.decode(form.responseText);  
    	 if(result.succeed){//校验成功
    		 if(needValidate)
    			 queryField_me.expr = result.expr;//因子表达式和查询条件用“|”分割
    		 else{
    			 queryField_me.cexpr = result.cexpr;
    			 queryField_me.condStr = result.condStr;
    		 }
    	 }else {  
			 Ext.showAlert(result.message);  
			 isValidate = false;
		 }
	}},map);
    return isValidate;
 },
 
 //上一步 不校验因式表达式
 preStep:function(){
 	if(queryField_me.submitCond(false)){
 		if(queryField_me.cexpr)
 		  queryField_me.selectFieldConfig.dataMap.cexpr = queryField_me.cexpr;
 		else
 		  queryField_me.selectFieldConfig.dataMap.cexpr = queryField_me.expression;
 		queryField_me.selectFieldConfig.dataMap.condStr = queryField_me.condStr;
 		if(queryField_me.queryType=="1")
 			queryField_me.selectFieldConfig.dataMap.expression = encode(Ext.getCmp('expressionId').getValue());
 		Ext.create("EHR.selectfield.SelectField",queryField_me.selectFieldConfig);
 	};
 },
 
 //查询
 query:function(){
	 if(queryField_me.queryCallbackfunc){
		 if(queryField_me.submitCond(true)){
			 //expression=1&dbName=Usr&dbName=Ret&dbName=Trs&dbName=Oth&like=like&history=history&second=second
			 var checkValues = Ext.getCmp('mainForm').getValues(true);//获得选中的人员库
			 if(queryField_me.isShowDb && checkValues.indexOf("dbName") < 0){//判断是否勾选人员库  lis 20160406
				 Ext.showAlert(common.msg.selectDbname); 
				 return;
			 }
			 if(queryField_me.info_type == '1' && queryField_me.isShowResult){
					Ext.require("EHR.selectfield.ShowResult",function(){
						var re = Ext.create("EHR.selectfield.ShowResult",{
							width:'600',
							height:'400',
							expr:queryField_me.expr,
							checkValues:checkValues,
							dataList:queryField_me.dataList,
							query_type:queryField_me.queryType,
                            isFilterSelectedExpert:queryField_me.isFilterSelectedExpert,
							title:'请选择',
							callback:queryField_me.queryCallbackfunc,
							imodule:queryField_me.imodule//模块号
						});
					});
			 } else {
				 queryField_me.win.close();
			 	 Ext.callback(eval(queryField_me.queryCallbackfunc),null,[queryField_me.expr,checkValues,queryField_me.dataList,queryField_me.queryType,queryField_me.priv,queryField_me.filter_factor]);
			 }
		 }
	 }
 },
 
 //保存简单条件
 saveCond:function(){
	 //因子表达式和查询条件用“|”分割
	 if(queryField_me.saveCallbackfunc){
		 if(queryField_me.submitCond(true)){
		 	 queryField_me.win.close();
			 Ext.callback(eval(queryField_me.saveCallbackfunc),null,[queryField_me.expr]);
		 }
	 }
 }
})