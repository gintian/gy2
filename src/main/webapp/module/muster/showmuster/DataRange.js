Ext.define('SetupschemeUL.DataRange', {
	//showMuster:'',
	showMusterSql:'',
	orderBySql:'',
	moduleID : '0',//模块号，=0：员工管理；=1：组织机构；参照t_hr_subsys中内容，如果t_hr_subsys没有则按顺序添加；默认为0。
    musterType : '1',//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；默认为“1”。
    tabid :'',
    oldcondition:'',
    oldrange_type:'',
    requires: ['EHR.extWidget.field.DateTimeField', 'EHR.extWidget.field.CodeTreeCombox'],
    constructor:function(config){
    	DataRange = this;
    	moduleID=config.moduleID;
    	tabid =config.tabid;//需要后台解密
    	DataRange.musterType=config.musterType;
        DataRange.callbackfn=config.callbackfn;
        DataRange.init();
        DataRange.parttimejobvalue=false;
        DataRange.codesetId = "";
        DataRange.personnelCheckBoxCount = 0;
    },
    init: function () {
        DataRange.initShowDataRange();
        //部分历史多指标结果
        DataRange.multipleIndicatorsConditionResult='';
        //过滤条件结果
        DataRange.filterConditionId='';
        //range_type
        DataRange.rangeType = undefined;
        //某条历史记录返回接口时的按月变化子集id
       // DataRange.changeByMYSetId = '';
        //某条历史记录结果
        DataRange.singleIndicatorsConditionResult = '';
    },
    //展示数据范围
    showDataRange: function (radioEnableMessage,echoConfig) {
    	DataRange.rangeType = echoConfig.range_type;
    	DataRange.parttimejobvalue = echoConfig.parttimejobvalue;
    	 //数据范围页面上方复选框区域
        var nbasesLabel=Ext.create('Ext.panel.Panel', {
        	html:dataRangeRec.label.personnelcheck,
            border: false
        });
        var nbasesCheckbox= Ext.create('Ext.panel.Panel', {
        	width: 380,
        	id:'nbasesCheckbox',
        	style: 'margin-left:45px',
            border: false,
            items: [{
                xtype: 'fieldcontainer',
                labelSeparator: '',
                defaultType: 'checkboxfield',
                id: 'fieldContainerCheckBoxPanel',
                items: []
            }],
            listeners: {
                beforerender: function () {
                    var map = new HashMap();
                    map.put('tabId', tabid);
                    map.put('opt','currentUserOwnPersonnel');
                    //向后台发送送请求获取当前花名册下包含的指标集有哪些
                    var nbases=echoConfig.nbase;
                    Rpc({
                        functionId: 'MM01021001',
                        success: function (res) {
                            var data = Ext.decode(res.responseText).data;
                            var dataSize = Ext.decode(res.responseText).dataSize;
                            if (dataSize == 1) {
                                Ext.getCmp('checkBoxPanel').setHidden(true);
                            } else if(musterType=='1'){ 
                            	var hadNbase=false;
                            	if(dataSize==0){
                        			Ext.getCmp('dataRangeWinPanel').setHeight(180);
                        		}else if(dataSize>3&&dataSize<=6){
                        			Ext.getCmp('checkBoxPanel').setHeight(70);
                        			Ext.getCmp('dataRangeWinPanel').setHeight(290);
                        		}else if(dataSize>6&&dataSize<=9){
                        			Ext.getCmp('checkBoxPanel').setHeight(90);
                        			Ext.getCmp('dataRangeWinPanel').setHeight(310);
                        		}else if(dataSize>9){
                        			Ext.getCmp('checkBoxPanel').setAutoScroll(true);
                        			Ext.getCmp('checkBoxPanel').setHeight(120);
                        			Ext.getCmp('dataRangeWinPanel').setHeight(330);
                        		}
                            	for(var i=0;i<dataSize;i++){
                            		for (var v in data[i]) {
                            			//当前人员库是否被选择
                            			var checked=false;
                            			if(nbases!=null && nbases!='undefined'){
                            				for(var j=0;j<nbases.length;j++){
                            					if(v==nbases[j]){
                            						checked=true;
                            						hadNbase=true;
                            					}
                            				}
                            			}
                            			
                                        Ext.getCmp('fieldContainerCheckBoxPanel').add({
                                        	id:"checkbox"+i,
                                            boxLabel: data[i][v],
                                            width:'33%',
                                            style: (i+1)%3==0?'':'float:left',//三个一行
                                            name: v,
                                            checked:!checked?false:true,
                                            inputValue: '3',
                                            id: 'checkbox' + DataRange.personnelCheckBoxCount++
                                        })
                                    }
                            	}
                            	if(!hadNbase){//说明没有选择人员库，默认选择在职人员库
                                	Ext.getCmp('checkbox0').setValue(true);
                                }
                            	
                            }else{
                            	Ext.getCmp('dataRangeWinPanel').setHeight(150);
                            }
                        }
                    }, map);

                }
            }

        });
       
        
        var checkBoxPanel =Ext.create('Ext.panel.Panel', {
        	id:'checkBoxPanel',
        	width: 490,
        	style: 'margin-top:10px',
        	layout:'hbox',
            border: false,
            items:[nbasesLabel,nbasesCheckbox]
        });
        //数据范围上方数据过滤条件下拉框数据源
        var dataFilterStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name', 'lexpr', 'factor'],
            pageSize: 10,
            proxy: {
                type: 'transaction',
                functionId: 'MM01021001',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                extraParams: {
                    opt: 'filter'
                }
            },
            autoLoad: true,
            listeners:{
                load : function( _this, records, successful, eOpts ) {
            		var selectedId=echoConfig.filter;
            		var displayName="无";
                	if(selectedId!=null && selectedId!="undefined"){
	                    var range = dataFilterStore.getRange();  
	                    if(range != null && range.length>0){  
	                        //var displayName = value;  
	                        for(var i=0;i<range.length;i++){  
	                            if(selectedId == range[i].data.id){ 
	                            	displayName = range[i].data.name;
	                            	Ext.getCmp("dataFilterCombobox").setValue(displayName);
	                                DataRange.filterConditionId = selectedId;
	                                break;
	                            }  
	                        }  
	                    } 
                	}
                }
            }
        });
        //数据范围上方数据过滤条件下拉框
        var dataFilterCombobox = Ext.create('Ext.form.ComboBox', {
            id: 'dataFilterCombobox',
            fieldLabel:dataRangeRec.label.dataFilter,//'数据过滤条件:',
            store: dataFilterStore,
            emptyText: '无',
            queryMode: 'local',
            displayField: 'name',
            valueField: 'abbr',
            border: false,
            listeners: {
                beforerender: function () {
                    Ext.getCmp('dataFilterCombobox').setWidth(350);
                    var selectedId=echoConfig.filter;
                    Ext.getCmp("dataFilterCombobox").select(selectedId);
                },
                select:function(combo,records,eOpts){
                    DataRange.filterConditionId = records.data.id;
                }

            }
        });
        // 数据范围页面上方数据过滤条件区域
        var dataFilterPanel = Ext.create('Ext.panel.Panel', {
            width: 490,
            //hight:25,
            id:'dataFilterPanel',
            style: 'margin-top:5px',
            items: [dataFilterCombobox],
            border: false
        });
        //显示兼职人员
		var parttimejobBox = Ext.create('Ext.panel.Panel',{
			width:490,
			hidden:musterType==1?false:true,
			border:false,
			style:'margin-top:5px;',
			layout:'hbox',
			items:[{
				xtype:'checkboxfield',
				style:'margin-left:105px',
				width:200,
				name:'parttimejob',  
                id:'parttimejob',
                boxLabel:"显示兼职人员",
                checked:DataRange.parttimejobvalue
			}]
		});
        //年份数据store
        var yearComboboxData = new Array();
        var date=new Date;
		var curyear=date.getFullYear();
		for(var i=curyear-5;i<=curyear;i++){
			var yearObj = {'abbr':i,"name": i};
			yearComboboxData.push(yearObj);
		}
		//选中某月历史记录显示的年下拉框数据源
        var yearComboboxStore = Ext.create('Ext.data.Store', {
            fields: ['abbr', 'name'],
            data: yearComboboxData
        });
        //选中某月历史记录显示的年下拉框
        var yearCombobox = Ext.create('Ext.form.ComboBox', {
            id: 'yearCombobox',
            store: yearComboboxStore,
            queryMode: 'local',
            editable:false,
            displayField: 'name',
            valueField: 'abbr',
            border: false,
            listeners: {
                beforerender: function () {
                    var date = new Date;
                    var year = date.getFullYear();
                    Ext.getCmp('yearCombobox').setWidth(55);
                    Ext.getCmp('yearCombobox').setValue(year);
                }
            }
        });
        //月份数据store
        var monthComboboxData = new Array();
		for(var i=1;i<=12;i++){
			var monthObj = {'abbr':i,"name": i};
			monthComboboxData.push(monthObj);
		}
		//选中某月历史记录显示的月下拉框数据源
        var monthComboboxStore = Ext.create('Ext.data.Store', {
            fields: ['abbr', 'name'],
            data:monthComboboxData
        });
        //选中某月历史记录显示的月下拉框
        var monthCombobox = Ext.create('Ext.form.ComboBox', {
            id: 'monthCombobox',
            store: monthComboboxStore,
            queryMode: 'local',
            editable:false,
            displayField: 'name',
            valueField: 'abbr',
            border: false,
            listeners: {
                beforerender: function () {
                    var date = new Date;
                    var month = date.getMonth() + 1;
                    Ext.getCmp('monthCombobox').setWidth(50);
                    Ext.getCmp('monthCombobox').setValue(month);
                }
            }
        });
        //选中某月历史记录显示的页面
        var historyRecordPanelOne = Ext.create('Ext.panel.Panel', {
            id: 'historyRecordPanelOne',
            //height: 25,
            layout: 'hbox',
            border: false,
            hidden: true,
            style: 'margin-left:120px',
            items: [{
                border: false,
                items: yearCombobox
            }, {
                border: false,
                html: dataRangeRec.html.year//'年'
            }, {
                border: false,
                items: monthCombobox
            }, {
                border: false,
                html: dataRangeRec.html.month//'月'
            }, {
                border: false,
                html:muster_di//第
            }, {
                xtype:'numberfield',
            	id:'numberofsubset',
            	hideTrigger: true,
                keyNavEnabled: true,
                mouseWheelEnabled: true,
            	width:30
            }, {
                border: false,
                html:muster_ci//
            }]
        });
        
        //选中部分历史记录显示的下拉框数据源
        var historyRecordComboboxStore = Ext.create('Ext.data.Store', {
            fields: ['fieldName', 'colHz','itemType','codesetId'],
            proxy: {
                type: 'transaction',
                functionId: 'MM01021001',
                extraParams: {
                        tabId: tabid,
                        opt:'partHistoryIndicator',
                        musterType:musterType
                },
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            },
            autoLoad: true,
            listeners:{
                load : function( _this, records, successful, eOpts ) {
	                if(echoConfig.range_type=='3'){
	                	var condition=echoConfig.condition.split(',');
	                	var selectedId=condition[0];
	            		//Ext.getCmp("historyRecordCombobox").setValue(condition[4]);
	                	Ext.getCmp("historyRecordCombobox").select(selectedId);
	                	if(condition[2]=='A' && condition[3]!='0'){//代码型
	                		var code0=condition[1].split("|")[0];
	                		var code1=condition[1].split("|")[1];
	                		
	                        DataRange.codesetId=condition[3];
	                        Ext.getCmp('code0').codesetid = DataRange.codesetId;
	                        Ext.getCmp('code1').codesetid = DataRange.codesetId;
	                        var treeStore0=Ext.getCmp('code0').treeStore;
	                        var treeStore1=Ext.getCmp('code1').treeStore;
	                        treeStore0.on('beforeload',function(){
	                        	Ext.apply(treeStore0.proxy.extraParams,{codesetid:DataRange.codesetId})
	                        });
	                        treeStore1.on('beforeload',function(){
	                        	Ext.apply(treeStore1.proxy.extraParams,{codesetid:DataRange.codesetId})
	                        });
	                        treeStore0.load();
	                        treeStore1.load();
	                        Ext.getCmp('datePanel').setHidden(true);
	                        Ext.getCmp('codePanel').setHidden(false);
	                        Ext.getCmp('textPanel').setHidden(true);
	                        
	                     	Ext.getCmp('code0').setValue(condition[5]);
		                    Ext.getCmp('code1').setValue(condition[6]);
	                       
	                        
	                	}else if(condition[2]=="D"){//日期型
	                		var date0=condition[1].split("|")[0];
	                		var date1=condition[1].split("|")[1];
	                		Ext.getCmp('datePanel').setHidden(false);
	                        Ext.getCmp('codePanel').setHidden(true);
	                        Ext.getCmp('textPanel').setHidden(true);
	                		Ext.getCmp('date0').setValue(date0);
	                		Ext.getCmp('date1').setValue(date1);
	                	}else if(condition[2]=="N"){//数值型
	                		var num0=condition[1].split("|")[0];
	                		var num1=condition[1].split("|")[1];
	                		Ext.getCmp('datePanel').setHidden(true);
	                        Ext.getCmp('codePanel').setHidden(true);
	                        Ext.getCmp('textPanel').setHidden(false);
	                		Ext.getCmp('num0').setValue(num0);
	                		Ext.getCmp('num1').setValue(num1);
	                		Ext.getCmp('num0').regex = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
	        				Ext.getCmp('num1').regex = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
	        				Ext.getCmp('num0').regexText = musterAdd.onlyInputNum;
	        				Ext.getCmp('num1').regexText = musterAdd.onlyInputNum;
	                	}else{//字符型
	                		var num0=condition[1].split("|")[0];
	                		var num1=condition[1].split("|")[1];
	                		Ext.getCmp('datePanel').setHidden(true);
	                        Ext.getCmp('codePanel').setHidden(true);
	                        Ext.getCmp('textPanel').setHidden(false);
	                		Ext.getCmp('num0').setValue(num0);
	                		Ext.getCmp('num1').setValue(num1);
	                	}
	                	
	            	}else if(echoConfig.range_type=='2'){
	            		var range = historyRecordComboboxStore.getRange();  
	            		var displayName = range[range.length-1].data.colHz;
                		Ext.getCmp("historyRecordCombobox").setValue(displayName);
	            	}
                }
            }
        });
        //选中部分历史记录显示的下拉框
        var historyRecordCombobox = Ext.create('Ext.form.ComboBox', {
            id: 'historyRecordCombobox',
            store: historyRecordComboboxStore,
            queryMode: 'local',
            width:140,
            style: 'margin-left:120px',
            editable:false,
            displayField: 'colHz',
            valueField: 'fieldName',
            border: false,
            listeners: {
                select: function () {
                    var comboboxLen = Ext.getCmp('historyRecordCombobox').getStore().data.length;
                    var selectionIndex = Ext.getCmp('historyRecordCombobox').getSelection().data.id.split("-")[1];
                    var selectionItemType = Ext.getCmp('historyRecordCombobox').getSelection().data.itemType;
                    var selectionCodesetId = Ext.getCmp('historyRecordCombobox').getSelection().data.codesetId;
                    if (selectionItemType == 'A' && selectionCodesetId != '0') {
                    	Ext.getCmp('code0').setValue("");
                        Ext.getCmp('code1').setValue("");
                        DataRange.codesetId=selectionCodesetId;
                        Ext.getCmp('code0').codesetid = DataRange.codesetId;
                        Ext.getCmp('code1').codesetid = DataRange.codesetId;
                        var treeStore0=Ext.getCmp('code0').treeStore;
                        var treeStore1=Ext.getCmp('code1').treeStore;
                        treeStore0.on('beforeload',function(){
                        	Ext.apply(treeStore0.proxy.extraParams,{codesetid:DataRange.codesetId})
                        });
                        treeStore1.on('beforeload',function(){
                        	Ext.apply(treeStore1.proxy.extraParams,{codesetid:DataRange.codesetId})
                        });
                        treeStore0.load();
                        treeStore1.load();
                        Ext.getCmp('datePanel').setHidden(true);
                        Ext.getCmp('codePanel').setHidden(false);
                        Ext.getCmp('textPanel').setHidden(true);
                    } else if (selectionItemType == 'D') {
                    	Ext.getCmp('date0').setValue("");
                        Ext.getCmp('date1').setValue("");
                        
                        Ext.getCmp('datePanel').setHidden(false);
                        Ext.getCmp('codePanel').setHidden(true);
                        Ext.getCmp('textPanel').setHidden(true);
                    } else if(selectionItemType=='A'||selectionItemType=='M'){
                       	Ext.getCmp('num0').setValue("");
                        Ext.getCmp('num1').setValue("");
                        Ext.getCmp('datePanel').setHidden(true);
                        Ext.getCmp('codePanel').setHidden(true);
                        Ext.getCmp('textPanel').setHidden(false);
                    }else if(selectionItemType=='N'){
                       	Ext.getCmp('num0').setValue("");
                        Ext.getCmp('num1').setValue("");
                        Ext.getCmp('num0').regex = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
        				Ext.getCmp('num1').regex = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
        				Ext.getCmp('num0').regexText = musterAdd.onlyInputNum;
        				Ext.getCmp('num1').regexText = musterAdd.onlyInputNum;
                        Ext.getCmp('datePanel').setHidden(true);
                        Ext.getCmp('codePanel').setHidden(true);
                        Ext.getCmp('textPanel').setHidden(false);
                    }else{
                    	Ext.getCmp('num0').setValue("");
                        Ext.getCmp('num1').setValue("");
                    	Ext.getCmp('datePanel').setHidden(true);
                        Ext.getCmp('codePanel').setHidden(true);
                        Ext.getCmp('textPanel').setHidden(true);
                    }
                    if (selectionIndex == comboboxLen && musterType!='4') {
                    	oldcondition ='';
                        var map = new HashMap();
                        map.put("tabId", tabid);
                        map.put('opt','setConditionIndicator');
                        Rpc({
                            functionId: 'MM01021001',
                            success: function (res) {
                                var setConditionIndicatorData = JSON.parse(res.responseText).setConditionIndicatorData;
                                var rightDataList = echoConfig.MultipleConditions.rightDataList;
                                var map2 = new HashMap();
                                //map2.put('opt','setConditionIndicator');
                                map2.put("info_type", "");
                                map2.put('buttonText', common.button.ok);
                                Ext.require('EHR.selectfield.SelectField', function () {
                                    var selectField=Ext.create("EHR.selectfield.SelectField", {
                                        imodule: '0',
                                        type: '1',
                                        flag:'1',
                                        leftDataList: setConditionIndicatorData,
                                        rightDataList:rightDataList,
                                        title: dataRangeRec.text.title,//"选择指标",
                                        saveCallbackfunc: DataRange.getMultipleIndicatorsCondition,
                                        dataMap: map2
                                    });
                                    
                                   //重写SelectField方法
                                    Ext.override(selectField,{
                                    	//下一步
	                               		 nextStep:function(){
	                               		 	   selectField_me.rightDataList='';
	                               			   var right_fields = new Array();
	                               			   //var right_field_objects = new Array();
	                               			   var rightStore = Ext.data.StoreManager.lookup('rightStoreId');
	                               			   rightStore.each(function(item,index,count){ //遍历每一条数据
	                               				   right_fields.push(item.get('dataValue'));
	                               				   //right_field_objects.push(item.data);
	                               			   });
	                               			   if(right_fields.length == 0){
	                               			   		return;
	                               			   }
	                               			   
	                               				var map = new HashMap();
	                               				map.put("info_type", selectField_me.selectFieldConfig.dataMap.info_type);
	                               				map.put("expr", echoConfig.MultipleConditions.expr);
	                               				map.put("right_fields",right_fields);
	                               				map.put("buttonText",selectField_me.buttonText);
	                               				if(selectField_me.queryType)
	                               					map.put("queryType",selectField_me.queryType);
	                               				else
	                               					map.put("queryType","0");
	                               				map.put("priv",selectField_me.priv);
	                               				map.put("filter_factor",selectField_me.filter_factor);
	                               				map.put("isFilterSelectedExpert",selectField_me.isFilterSelectedExpert );
	                               				//配置参数
	                               				var configObj = new Object();
	                               				configObj.selectFieldConfig = selectField_me.selectFieldConfig;
	                               				configObj.imodule = selectField_me.imodule;
	                               				configObj.type = selectField_me.type;
	                               				configObj.dataMap = map;
	                               				configObj.saveCallbackfunc = selectField_me.saveCallbackfunc;
	                               				configObj.queryCallbackfunc = selectField_me.queryCallbackfunc;
	                               				configObj.isShowResult = selectField_me.isShowResult;
	                               	         	Ext.require('EHR.selectfield.QueryFieldSet', function(){
	                               	         		var scopePanel = Ext.create("EHR.selectfield.QueryFieldSet",configObj);
	                               	         		selectField_me.win.close();
	                               	     		});
	                               		 }
                                    });
                                });
                                var range = historyRecordComboboxStore.getRange();  
        	            		var displayName = range[range.length-1].data.colHz;
                        		Ext.getCmp("historyRecordCombobox").setValue(displayName);
                            }
                        }, map);
                    }
                }
            }
        });
        //当部分历史记录选中后显示的下拉框选择的指标类型为日期型时显示
        var datePanel = Ext.create('Ext.panel.Panel', {
            id: 'datePanel',
            layout: 'hbox',
            width: 220,
            height:22,
            border: false,
            items: [{
                width: 15,
                html: dataRangeRec.html.from,//'从',
                border: false
            }, {
                xtype: 'datetimefield',
                id: 'date0',
                width: 95,
                editable : false, 
                name: 'w0309',
                format: 'Y-m-d',
                height:22
            }, {
                width: 15,
                html: dataRangeRec.html.to,//'到',
                border: false
            }, {
                xtype: 'datetimefield',
                width: 95,
                id: 'date1',
                height:20,
                editable : false, 
                name: 'w03091',
                format: 'Y-m-d'
                //maxValue: new Date(),
            }]
        });
        //当部分历史记录选中后显示的下拉框选择的指标类型为代码型时显示
        var codePanel = Ext.create('Ext.panel.Panel', {
            id: 'codePanel',
            layout: 'hbox',
            width: 220,
            border: false,
            //height:22,
            hidden: true,
            items: [{
                width: 15,
                html: dataRangeRec.html.from,//'从',
                border: false
            }, {
                xtype: 'codecomboxfield',
                editable : false, 
                id: 'code0',
                width: 95,
                codesetid: 'YC',
                border: false
            }, {
                width: 15,
                html: dataRangeRec.html.to,//'到',
                border: false
            }, {
                xtype: 'codecomboxfield',
                codesetid: 'YC',//DataRange.codesetId,//'YC',
                editable : false, 
                id: 'code1',
                width: 95
            }]
        });
        
        //当部分历史记录选中后显示的下拉框选择的指标类型为文本时显示
        var textPanel = Ext.create('Ext.panel.Panel', {
            id: 'textPanel',
            layout: 'hbox',
            width: 220,
            border: false,
            hidden: true,
            items: [{
                width: 15,
                html: dataRangeRec.html.from,//'从',
                border: false
            }, {
                xtype: 'textfield',
                id: 'num0',
                width: 95,
                border: false
            }, {
                width: 15,
                html: dataRangeRec.html.to,//'到',
                border: false
            }, {
                xtype: 'textfield',
                id: 'num1',
                width: 95
            }]
        });
        //选中部分历史记录显示的页面
        var historyRecordPanelPart = Ext.create('Ext.panel.Panel', {
            id: 'historyRecordPanelPart',
            height: 25,
            layout: 'hbox',
            border: false,
            hidden: true,
            items: [{
                    border: false,
                    items: historyRecordCombobox
                }, {
                    border: false,
                    items: [textPanel, codePanel, datePanel]
                }
            ]
        })
        //数据范围页面上方子集范围区域
        var subsetScope = Ext.create('Ext.panel.Panel', {
        	style: 'margin-top:5px',
        	id:'subsetScope',
            width: 490,
            layout:'vbox',
            border: false,
            items: [{
                xtype: 'radio',
                fieldLabel: dataRangeRec.label.subsetscope,//'子集范围',
                boxLabel: dataRangeRec.label.currentrecord,//'当前记录',
                width: 490,
                name: 'size',
                inputValue: 'm',
                id: 'radio1',
                checked:true
            }, {
                xtype: 'radio',
                fieldLabel: '    ',
                width: 490,
                boxLabel: dataRangeRec.label.oneHistoryRecord,//'某月历史记录(仅支持主集和一个年月变化子集)',
                name: 'size',
                inputValue: 'l',
                id: 'radio2',
                listeners: {
                    change: function () {
                        if (Ext.getCmp('radio2').getValue()) {
                            Ext.getCmp('historyRecordPanelOne').show();
                        } else {
                            Ext.getCmp('historyRecordPanelOne').hide();
                        }
                    }
                }
            }, historyRecordPanelOne, {
                xtype: 'radio',
                width: 490,
                fieldLabel: '    ',
                boxLabel: dataRangeRec.label.someHistoryRecord,//'部分历史记录(仅支持主集和一个子集)',
                name: 'size',
                inputValue: 'xl',
                id: 'radio3',
                listeners: {
                    change: function () {
                        if (Ext.getCmp('radio3').getValue()) {
                            Ext.getCmp('historyRecordPanelPart').show();
                        } else {
                            Ext.getCmp('historyRecordPanelPart').hide();
                        }

                    }
                }
            }, historyRecordPanelPart],
            listeners: {
                beforerender: function () {
                    DataRange.setRadioState(radioEnableMessage);
                }
            }

        });
        //数据范围弹出窗口中居中的Panel
        var dataRangeWinPanel = Ext.create('Ext.form.Panel', {
        	id:'dataRangeWinPanel',
		    height : 250,
            border: false,
            layout : {
            	align:'center',
                type :'vbox'
            },
            items: [checkBoxPanel, dataFilterPanel,parttimejobBox,subsetScope],
            buttons:['->',{
            	height:22,
            	formBind : true,
				text : page_setup.button.save,//确定	
				handler : function() {
					var ifCallBack=true;
					//人员库范围数据
                    var resultPersonner = DataRange.getPersonnelCheckBoxData(DataRange.personnelCheckBoxCount);
                    if((resultPersonner==''||resultPersonner==null) && musterType=='1'){
                    	var nbases=echoConfig.nbase;
                    	if(nbases.length==1){
                    		resultPersonner = nbases[0];
                    	}else{
                    		ifCallBack=false;
                        	Ext.Msg.alert(dataRangeRec.text.promptInformation,dataRangeRec.text.checkOnePersonner);//提示请至少选择一个人员库！
                    	}
                    }
                    //过滤条件
                    var resultFilterConditionId =  DataRange.filterConditionId;
                    if(Ext.getCmp('radio1').getValue()){
                       DataRange.rangeType = '0';//当前记录
                    }else if(Ext.getCmp('radio2').getValue()){
                       DataRange.rangeType = '1';//年月变化子集筛选条件
                       DataRange.singleIndicatorsConditionResult=DataRange.getChangeByMYSetId();  
                       
                       var setName=DataRange.singleIndicatorsConditionResult.split(',')[0];
                       var year_month=DataRange.singleIndicatorsConditionResult.split(',')[1];
                       var condition = year_month.split('-');
                       var year=condition[0];
                       var month=condition[1];
                       if(month=='12'){
                    	   DataRange.singleIndicatorsConditionResult=setName+','+year+'-'+month+'-01|'+(parseInt(year)+1)+'-01-01';
                       }else if(month=='11'||month=='10'){
                    	  DataRange.singleIndicatorsConditionResult=setName+','+year+'-'+month+'-01|'+year+'-'+(parseInt(month)+1)+'-01';
                       }else if(month=='9'){
                    	  DataRange.singleIndicatorsConditionResult=setName+','+year+'-0'+month+'-01|'+year+'-'+(parseInt(month)+1)+'-01';
                       }else{
                    	  DataRange.singleIndicatorsConditionResult=setName+','+year+'-0'+month+'-01|'+year+'-0'+(parseInt(month)+1)+'-01';
                       }
                       if(condition.length==3){
                    	   DataRange.singleIndicatorsConditionResult+='|'+condition[2];
                       }
                    }else{
                       DataRange.rangeType = '3';//部分历史记录默认单个指标条件 
                       var resultMultipleIndicatorsCondition = DataRange.multipleIndicatorsConditionResult;
                       if(resultMultipleIndicatorsCondition==''){
                    	   resultMultipleIndicatorsCondition=oldcondition;
                       }
                       var data;
                       if(Ext.getCmp('historyRecordCombobox').getSelection()!='undefined' && Ext.getCmp('historyRecordCombobox').getSelection()!=null){
                       	  	data = Ext.getCmp('historyRecordCombobox').getSelection().data;
                       	  	var selectionItemType=data.itemType;
	                       	var colHz=data.colHz;
							if (selectionItemType == 'A' && data.codesetId != '0') {//代码型
								var code0=Ext.getCmp('code0').getValue().replace(/[^0-9]/ig,"");
								var code1=Ext.getCmp('code1').getValue().replace(/[^0-9]/ig,"");
								if(code0=='' || code1==''){
									ifCallBack=false;
								}
								DataRange.multipleIndicatorsConditionResult=data.fieldName+","+code0+"|"+code1;
							} else if (selectionItemType == 'D') {//日期型
								var date0 = Ext.getCmp('date0').getValue();
								var date1=Ext.getCmp('date1').getValue();
								if(date0=='' || date1==''){
									ifCallBack=false;
								}
								DataRange.multipleIndicatorsConditionResult=data.fieldName+","+date0+"|"+date1;
							} else if(selectionItemType == 'N'|| (selectionItemType=='A' && data.codesetId == '0')){//数值或者文本型
								var num0=Ext.getCmp('num0').getValue();
								var num1=Ext.getCmp('num1').getValue();
								if(num0=='' || num1==''){
									ifCallBack=false;
								}
								DataRange.multipleIndicatorsConditionResult=data.fieldName+","+num0+"|"+num1;
							}else if(Ext.getCmp('historyRecordCombobox').getSelection()!='undefined' && Ext.getCmp('historyRecordCombobox').getSelection()!=null){
								if(resultMultipleIndicatorsCondition==''){
									 Ext.Msg.alert(dataRangeRec.text.promptInformation,dataRangeRec.text.dataNotNull);//提示部分历史记录不能为空！
									 return false;
								}
								DataRange.rangeType = '2';//子集的多个指标条件
								if(DataRange.multipleIndicatorsConditionResult==''){
									if(echoConfig.encryptCondition!=null || echoConfig.encryptCondition!=''){
										DataRange.multipleIndicatorsConditionResult=echoConfig.encryptCondition;
									}else{
										ifCallBack=false;
									}
								}
							}else{//什么都没设置默认走数据的
								DataRange.rangeType = oldrange_type
								DataRange.multipleIndicatorsConditionResult = oldcondition;
							}
                       }else{
                           ifCallBack=false;
                    	   // DataRange.rangeType = oldrange_type
						   // DataRange.multipleIndicatorsConditionResult = oldcondition;
                       }
                       if(!ifCallBack){//dataRangeRec.text.dRNotNull
                    	   Ext.Msg.alert(dataRangeRec.text.promptInformation,dataRangeRec.text.dataNotNull);//提示部分历史记录不能为空！
                    	   return false;
                       }
                    }  
					if(ifCallBack){
						if(musterType!='1'){
							resultPersonner='';
							DataRange.filterConditionId='';
						}
						var map = new HashMap();
						map.put("flag","dataRang");//数据范围标识
						map.put("tabid",tabid);
						map.put("parttimejobvalue",Ext.getCmp("parttimejob").getValue());//是否兼职
						map.put("musterType",musterType);
						map.put("moduleID",moduleID);//模块号
						if(resultPersonner!=null ){
							map.put("nbases",resultPersonner);
						}
					    
						if(DataRange.rangeType!=null && DataRange.rangeType!=''){
							map.put("range_type",DataRange.rangeType);
						}
						
						if(DataRange.filterConditionId !=null && DataRange.filterConditionId!='0'){
							map.put("filterid",DataRange.filterConditionId);
						}
						if(DataRange.rangeType=='1'){
							map.put("condition",DataRange.singleIndicatorsConditionResult);
						}else if(DataRange.rangeType=='2'||DataRange.rangeType=='3'){
							map.put("condition",DataRange.multipleIndicatorsConditionResult);
						}
						if(DataRange.callbackfn) {
							Ext.callback(eval(DataRange.callbackfn),null,[map]);//回调
						}
						
						Ext.getCmp('dataRangeWin').close();
					}
					
				}
			},{
				text : page_setup.button.close,//取消
				height:22,
				handler : function() {
					Ext.getCmp('dataRangeWin').close();
				}
			},'->'],
			listeners: {
				beforerender: function () {
					if(musterType!='1'){//非人员花名册
			        	 Ext.getCmp('checkBoxPanel').setHidden(true);
			        	 Ext.getCmp('dataFilterPanel').setHidden(true);
			        	 Ext.getCmp('dataRangeWinPanel').hight=210;
			        }
				}
			}
        })
        //点击数据范围弹出的窗口
        var dataRangeWin = Ext.create('Ext.Window', {
        	id:'dataRangeWin',
        	width : 670,
            modal : true,
            autoShow: true,
            title: dataRangeRec.text.dataRange,//'数据范围',
            layout : {
            	align:'center',
                type :'vbox'
            },
            items: [dataRangeWinPanel]
        })
        if(echoConfig.range_type=='1'){
    		var date=echoConfig.condition.split(",")[1].split("|")[0].split("-");
    		var year=date[0];
    		var month=date[1];
    		Ext.getCmp("yearCombobox").setValue(year);
    		Ext.getCmp("monthCombobox").setValue(month);
    		if(echoConfig.condition.split(",")[1].split("|").length==3){
    			var number = echoConfig.condition.split(",")[1].split("|")[2];
    			Ext.getCmp("numberofsubset").setValue(number);
    		}
    	}
        
        var range_type=echoConfig.range_type;
        if(range_type=="0"){
        	Ext.getCmp('radio2').setValue(false);
            Ext.getCmp('radio3').setValue(false);
    		Ext.getCmp('radio1').setValue(true);
        }else if(range_type=="1"){
    		Ext.getCmp('radio1').setValue(false);
            Ext.getCmp('radio3').setValue(false);
            Ext.getCmp('radio2').setValue(true);
    	}else if(range_type=="2"){
    		Ext.getCmp('radio1').setValue(false);
            Ext.getCmp('radio2').setValue(false);
            Ext.getCmp('radio3').setValue(true);
            Ext.getCmp('datePanel').setHidden(true);
            Ext.getCmp('codePanel').setHidden(true);
            Ext.getCmp('textPanel').setHidden(true);
    	}else if(range_type=="3"){
    		Ext.getCmp('radio1').setValue(false);
            Ext.getCmp('radio2').setValue(false);
            Ext.getCmp('radio3').setValue(true);
    	}
    },
    /*
     * 根据花名册下的指标集分布情况设置单选框状态
     */
    setRadioState: function (radioEnableMessage) {
    		if (radioEnableMessage.indexOf("radio2disable") != -1) {
                Ext.getCmp('radio2').setDisabled(true);
                 if (Ext.isIE){
                 	Ext.getCmp('radio2').style="color:#999999";
                 }
                
            }
            if (radioEnableMessage.indexOf("radio3disable") != -1) {
                Ext.getCmp('radio3').setDisabled(true);
                if (Ext.isIE){
                	Ext.getCmp('radio3').style="color:#999999";
                }
            }
    },
    /*
     * 设置数据范围指标条件窗口
     */
    initShowDataRange: function () {
    	
    	var echoMap=new HashMap();
        echoMap.put('opt','echoData');
        echoMap.put('tabid',tabid);
        echoMap.put('musterType',DataRange.musterType);
        Rpc({
            functionId: 'MM01021001',
            async:false,
            success: function (res) {
                var result = Ext.decode(res.responseText).echoData;
                var radioEnableMessage = Ext.decode(res.responseText).radioEnableMessage;
                var echoConfig=new HashMap();
                oldcondition = result.oldcondition;
                oldrange_type = result.range_type;
                var MultipleConditions ={};
                if(result.MultipleConditions){
                    MultipleConditions = result.MultipleConditions;
                }else{
                    MultipleConditions.rightDataList = [];
                    MultipleConditions.expr ="";
                }
                echoConfig.put("nbase",result.nbases);
                echoConfig.put("range_type",result.range_type);
                echoConfig.put("filter",result.filter);
                echoConfig.put("condition",result.condition);
                echoConfig.put("encryptCondition",result.encryptCondition);
                echoConfig.put("MultipleConditions",MultipleConditions);
                echoConfig.put("parttimejobvalue",result.parttimejobvalue);
                DataRange.showDataRange(radioEnableMessage,echoConfig);
            }
        }, echoMap);
    },
    /*
     * 点击确定时整理人员库部分数据
     */
    getPersonnelCheckBoxData: function (personnelCheckBoxCount) {
        var personnelCheckBoxData = "";
        var firstFlag = 0;
        for (var i = 0; i < personnelCheckBoxCount; i++) {
            var checkBoxValue = Ext.getCmp("checkbox" + i).getValue();
            var checkBoxName = Ext.getCmp("checkbox" + i).getName();
            if (firstFlag == 0 && checkBoxValue == true) {
                personnelCheckBoxData += checkBoxName;
                firstFlag = 1;
            } else if (checkBoxValue == true) {
                personnelCheckBoxData = personnelCheckBoxData + "," + checkBoxName;
            }

        }
        return personnelCheckBoxData;
    },
    /*
     * 通过回调函数来整理子集的多个指标条件
     */
    getMultipleIndicatorsCondition: function (multipleIndicatorsCondition) {
    	DataRange.multipleIndicatorsConditionResult = multipleIndicatorsCondition;
    },
    /*
     * 获取按月变化子集id
     */
    getChangeByMYSetId:function(){
    	//var DataRange = this;
    	var map = new HashMap();
    	map.put('opt','changeByMYsetName');
    	map.put('tabId',tabid);
        Rpc({
            functionId: 'MM01021001',
            success: function (res) {
                var changeByMYSetId = Ext.decode(res.responseText).changeByMYsetId;
                var yearCondition = Ext.getCmp('yearCombobox').getValue();
                var monthCondition = Ext.getCmp('monthCombobox').getValue();
                var numberCondition = Ext.getCmp('numberofsubset').getValue();
                DataRange.singleIndicatorsConditionResult =changeByMYSetId+","+yearCondition+"-"+monthCondition; 
                if(numberCondition){
                	DataRange.singleIndicatorsConditionResult+="-"+numberCondition;
                }
            },async:false
        }, map);
        return DataRange.singleIndicatorsConditionResult;
    }

})