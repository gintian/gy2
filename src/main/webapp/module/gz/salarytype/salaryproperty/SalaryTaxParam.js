/**
 * 薪资类别-薪资属性-计税参数
 * lis 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.SalaryTaxParam',{
        constructor:function(config){
			salaryTaxParam_me = this;
			salaryTaxParam_me.salaryid = config.salaryid;
			var result = config.result;
			salaryTaxParam_me.calculateTaxTime = result.calculateTaxTime;//计税时间指标
			salaryTaxParam_me.calculateTaxTimeList = result.calculateTaxTimeList;
			salaryTaxParam_me.appealTaxTime = result.appealTaxTime;//报税时间指标
			salaryTaxParam_me.sendSalaryItem = result.sendSalaryItem;//发薪标识指标
			salaryTaxParam_me.sendSalaryItemList = result.sendSalaryItemList;
			salaryTaxParam_me.taxType = result.taxType;//计税方式指标
			salaryTaxParam_me.taxTypeList = result.taxTypeList;
			salaryTaxParam_me.ratepayingDecalre = result.ratepayingDecalre;//纳税项目指标
			salaryTaxParam_me.ratepayingDeclareList = result.ratepayingDeclareList;
			salaryTaxParam_me.islsDept = result.islsDept;
			salaryTaxParam_me.lsDept = result.lsDept; //归属部门
			salaryTaxParam_me.lsDeptList = result.lsDeptList;
			salaryTaxParam_me.taxUnit = result.taxUnit;//计税单位指标
			salaryTaxParam_me.taxUnitList = result.taxUnitList;
			salaryTaxParam_me.percent = result.percent;//是否残疾人的百分比
			
			salaryTaxParam_me.hiredate =result.hiredate
			salaryTaxParam_me.hiredateList =result.hiredateList
			salaryTaxParam_me.disability =result.disability
			salaryTaxParam_me.sf_List =result.sf_List
			
			salaryTaxParam_me.createSalary(); 
        },
		 createSalary:function()  
		 {
        	//计税时间指标\报税时间指标
        	var states1 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//发薪标识指标
        	var states2 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//计税方式指标
        	var states3 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//纳税项目指标
        	var states4 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//归属部门
        	var states5 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//计税单位指标
        	var states6 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	//入职时间指标
        	var states7 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	//是否残疾人指标
        	var states8 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	Ext.each(salaryTaxParam_me.calculateTaxTimeList,function(obj,index){
        		states1.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.sendSalaryItemList,function(obj,index){
        		states2.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.taxTypeList,function(obj,index){
        		states3.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.ratepayingDeclareList,function(obj,index){
        		states4.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.lsDeptList,function(obj,index){
        		states5.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.taxUnitList,function(obj,index){
        		states6.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.hiredateList,function(obj,index){
        		states7.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salaryTaxParam_me.sf_List,function(obj,index){
        		states8.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	var width = 300;
        	// 计税时间指标下拉框
        	var combox1 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.calculateTaxTime,//计税时间指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states1,
         	    editable:false,
         	    width:width,
         	    name:'calculateTaxTime',
         	    value:salaryTaxParam_me.calculateTaxTime,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 报税时间指标下拉框
        	var combox2 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.appealTaxTime,//报税时间指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states1,
         	    editable:false,
         	    width:width,
         	    name:'appealTaxTime',
         	    value:salaryTaxParam_me.appealTaxTime,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 发薪标示指标下拉框
        	var combox3 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.sendSalaryItem,//发薪标示指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states2,
         	    editable:false,
         	    width:width,
         	    name:'sendSalaryItem',
         	    value:salaryTaxParam_me.sendSalaryItem,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 计税方式指标下拉框
        	var combox4 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.taxType,//计税方式指标
        		labelSeparator :'',//去掉后面的冒号
        		editable:false,
         	    store: states3,
         	    width:width,
         	    name:'taxType',
         	    value:salaryTaxParam_me.taxType,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 纳税项目指标下拉框
        	var combox5 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.ratepayingDecalre,//纳税项目指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states4,
         	    editable:false,
         	    width:width,
         	    name:'ratepayingDecalre',
         	    value:salaryTaxParam_me.ratepayingDecalre,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 归属单位指标下拉框(单位+部门)
        	var combox6 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.lsDept,//归属单位指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states5,
         	    editable:false,
         	    width:width,
         	    name:'lsDept',
         	    value:salaryTaxParam_me.lsDept,
         	    hidden:salaryTaxParam_me.islsDept==1?false:true,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 归属部门指标下拉框
        	var combox7 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.taxUnit,//归属部门指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states6,
         	    editable:false,
         	    width:width,
         	    name:'taxUnit',
         	    value:salaryTaxParam_me.taxUnit,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	}); 
        	
        	// 入职时间指标下拉框
        	var combox8 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.hiredate,//入职时间指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states7,
         	    editable:false,
         	    width:width,
         	    name:'hiredate',
         	    value:salaryTaxParam_me.hiredate,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	// 是否残疾指标下拉框
        	var combox9 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.disability,//是否残疾指标
        		labelSeparator :'',//去掉后面的冒号
         	    store: states8,
         	    editable:false,
         	    width:width,
         	    name:'disability',
         	    value:salaryTaxParam_me.disability,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue',
         	    listeners:{
         	    	select:function(combo,records){
         	    		if(combo.value.length > 0) {
         	    			Ext.getCmp("percent").setHidden(false);
         	    			Ext.getCmp("textPercent").setHidden(false);
         	    		}else {
         	    			Ext.getCmp("percent").setHidden(true);
         	    			Ext.getCmp("textPercent").setHidden(true);
         	    		}
          			}
         	    }
        	}); 
        	
        	var panel9 = Ext.widget('panel',{
    			height: 35,
    			border: false,
    			layout: 'hbox',
    			items: [combox9,{
    				xtype: 'numberfield',
    				name: 'percent',
    				id: 'percent',
    				hidden: salaryTaxParam_me.disability.length > 0?false:true,
                    maxValue: 100,
                    minValue: 1,
                    width: 140,
                    margin: '0 0 0 10',
                    value: salaryTaxParam_me.percent,
                    fieldLabel: gz.label.disabilityPercent,//减征比例
                    labelAlign: 'left',
                    labelWidth: 88,
                    allowDecimals: false,//限制只能整数
                    mouseWheelEnabled: true,//鼠标滚动的效果
                    labelSeparator: null,
                    step: 1,
                    listeners:{
                    	 change: function (me, newValue, oldValue, eOpts) {
                             if(newValue<1||newValue>100) 
                             {
                            	 Ext.MessageBox.alert("提示","征收比例范围:1-100");
                            	 Ext.getCmp("percent").setValue("");
                             }
                         }
             	    }
    				
    			},{
                    xtype: 'label',
                    id: 'textPercent',
                    hidden: salaryTaxParam_me.disability.length > 0?false:true,
                    text:'%',
                    style:'margin-left:6px;font-size: 15px;'
                }]
        	});
    		//计税参数panel
        	var grid = Ext.widget({
        		xtype:'panel',
		    	border:false,
		    	padding:Ext.isIE?'3 0 0 10':'5 0 0 10',
		    	layout: {
		            type: 'vbox'
		        },
		    	items:[combox1,combox2,combox3,combox4,combox5,combox6,combox7,combox8,panel9]
        	});
        	
        	var scopePanel = Ext.widget({
        		xtype:'panel',
        		border:false,
        		bbar:[{
        				xtype:'panel',
        				border:false,
        				layout:'vbox',
        				items:[{ xtype: 'label', text: gz.label.explain, hidden: Ext.isIE?true:false },//说明
        				       //1.计税方式，请选择工资类别中关联代码类46的指标
        				       { xtype: 'label', text: gz.msg.explain1, style: salaryTaxParam_me.islsDept==1?"":"padding-top:5px;"},
        				       //2.发薪标识，请选择工资类别中关联代码类42的指标
        				       { xtype: 'label', text: gz.msg.explain2, style: salaryTaxParam_me.islsDept==1?"":"padding-top:5px;" },
        				       //3.纳税项目说明，在所得税管理中使用，请选择工资类别中的字符型指标
        				       { xtype: 'label', text: gz.msg.explain3, style: salaryTaxParam_me.islsDept==1?"":"padding-top:5px;" },
        				       //4.归属单位，在所得税管理中使用，请选择工资类别中关联代码UM、UN的指标
					           { xtype: 'label', text: gz.msg.explain4, hidden:salaryTaxParam_me.islsDept==1?false:true },
        				       //5.计税单位，在合并计税时使用，请选择工资类别中关联代码类UN的指标
        				       { xtype: 'label', text: (salaryTaxParam_me.islsDept==1?"5.":"4.") + gz.msg.explain5, style: salaryTaxParam_me.islsDept==1?"":"padding-top:5px;" },
        				       //6.入职时间，日期型的薪资项目，按入职时间当月重新累计算税
        				       { xtype: 'label', text: (salaryTaxParam_me.islsDept==1?"6.":"5.") + gz.msg.explain6, style: salaryTaxParam_me.islsDept==1?"":"padding-top:5px;" },
        				       //7.是否残疾，关联代码类45的薪资项目，残疾人生效时间，在system中指定参数：disability_date=指标代号
        				       { xtype: 'label', html: (salaryTaxParam_me.islsDept==1?"7.":"6.") + gz.msg.explain7, style: salaryTaxParam_me.islsDept==1?"":"padding-top:5px;" },
					           ]
        			}
        		],
        		items:[grid]
        	});
        	//将当前panel渲染到tab页
        	salaryProperty_me.tabs.child('#maxId').add(scopePanel);
		}
 });