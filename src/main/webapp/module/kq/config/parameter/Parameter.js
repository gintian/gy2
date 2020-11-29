/**
 * 考勤管理参数
 */
Ext.define('KqParameterURL.Parameter',{
	
    constructor:function(config){
    	kqParameter = this;
    	kqParameter.kqParameMap = null;
    	kqParameter.listFieldSet = null;
    	// 请假子集信息
    	kqParameter.leaveListDate = null;
    	kqParameter.leaveListStr = null;
    	kqParameter.leaveListCode = null;
    	// 公出子集信息
    	kqParameter.officeleaveListDate = null;
    	kqParameter.officeleaveListStr = null;
    	kqParameter.officeleaveListCode = null;
    	// 加班子集信息
    	kqParameter.overtimeListDate = null;
    	kqParameter.overtimeListStr = null;
    	kqParameter.overtimeListCode = null;
    	// 描述信息宽度
    	kqParameter.descWidth = 150;
    	this.init();
    },
  
    init: function(){
    	var json = {};
		json.type = "init";
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021501',success:kqParameter.loadeOK},map);
    },
    
    loadeOK: function(response){
    	var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var jsonObj = map.returnStr;
			if(jsonObj.return_code == "fail"){
				Ext.showAlert(jsonObj.return_msg);
				return;
			}
			var jsonData = jsonObj.return_data;
			// 赋值全部参数
			kqParameter.kqParameMap = jsonData.kqParameMap;
			// 赋值人员子集列表
			kqParameter.listFieldSet = jsonData.listFieldSet;
			// 请假子集信息
	    	kqParameter.leaveListDate = jsonData.leaveListDate;
	    	kqParameter.leaveListStr = jsonData.leaveListStr;
	    	kqParameter.leaveListCode = jsonData.leaveListCode;
	    	// 公出子集信息
	    	kqParameter.officeleaveListDate = jsonData.officeleaveListDate;
	    	kqParameter.officeleaveListStr = jsonData.officeleaveListStr;
	    	kqParameter.officeleaveListCode = jsonData.officeleaveListCode;
	    	// 加班子集信息
	    	kqParameter.overtimeListDate = jsonData.overtimeListDate;
	    	kqParameter.overtimeListStr = jsonData.overtimeListStr;
	    	kqParameter.overtimeListCode = jsonData.overtimeListCode;
			// 处理人员库
			var myCheckboxItems = [];
			var nbaseCheck = "";
		    if(jsonData.kqParameMap && jsonData.kqParameMap.nbase)
		    	nbaseCheck = jsonData.kqParameMap.nbase;
		    var nbaseList = jsonData.nbase_all;
	        for (var i = 0; i < nbaseList.length; i++) {
	            var nbaseMap = nbaseList[i];
	            for (var key in nbaseMap) {
	                var boxLabel = key;
	                var name = nbaseMap[key];
	                if ((","+nbaseCheck+",").indexOf(','+name+",") != -1){
	                    myCheckboxItems.push({
	                        boxLabel: boxLabel,
	                        name: name,
	                        checked: true
	                    });
	                } else {
	                    myCheckboxItems.push({
	                        boxLabel: boxLabel,
	                        name: name,
	                        checked: false
	                    });
	                }
	            }
	    	}
	        myCheckboxGroup = new Ext.form.CheckboxGroup({
		        xtype : 'checkboxgroup',
		        id: 'checkboxgroup',
		        margin:'10 0 20 50',
		        width: 720, 
		        columns : 6,
		        items : myCheckboxItems
		    });
	        // 处理工号
	        var gnoValue = jsonData.kqParameMap.g_no;
	        var listA01CodeStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:jsonData.listA01
	        });
	        // 处理考勤部门
	        var kqdeptValue = jsonData.kqParameMap.kq_dept;
	        var listA01UMCodeStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:jsonData.listA01UM
	        });
	        // 考勤变动子集信息
	        var listFieldSetCodeStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:jsonData.listFieldSet
	        });
	        var listChangeUMCodeStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:jsonData.listChangeUM
	        });
	        var listChangeDateCodeStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:jsonData.listChangeDate
	        });
	        // 处理时间型指标store
	        var listDateFieldStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:jsonData.listDateField
	        });
	        // 申请信息页签tabpanel
	        var applyTabpanel = kqParameter.getApplyTabpanel();
	        
	        var formPanel = Ext.create('Ext.panel.Panel', {
	        	title:'<div style="float:left;">'+kq.param.info+'</div>'
	        		+'<div style="float:right;padding-right:10px;font-size:16px;">'
	        		+'<a href="javascript:void(0);" onclick="kqParameter.saveKqParam();" >'+kq.label.save+'</a></div>',
	        	region: 'center',
	        	autoScroll: true,
	        	bodyStyle:'overflow-x:hidden;',
        		layout: {
        			type: 'vbox'
        		},
        		border: 0,
        		items: [{
        			xtype:'panel',
        			width:'100%',
        			margin:'20 0 0 0',
        			border:0,
        			layout:{
        				type:'vbox'
        			},
        			items:[{
        				xtype:'component',
        				html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        					+kq.param.nbases+'</div>'
        			}
        			,myCheckboxGroup
        			]
        		},{// 考勤指标
        			xtype:'component',
        			html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        				+kq.param.fields+'</div>',
        		},{// 卡号
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;"><span style="color:red;">*</span>卡号</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'cardnoCode',
        				name: 'cardnoCode',
        				store: listA01CodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.card_no,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.gnoDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 工号
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;"><span style="color:red;">*</span>'+kq.param.gno+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'gnoCode',
        				name: 'gnoCode',
        				store: listA01CodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: gnoValue,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.gnoDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 考勤部门
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.kqdept+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'deptCode',
        				name: 'deptCode',
        				store: listA01UMCodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: kqdeptValue,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.kqdeptDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		}
        		,{// 考勤开始时间
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.kqStartDate+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'startDateCode',
        				name: 'startDateCode',
        				store: listDateFieldStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.kq_start_date,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.kqFieldSetDateItems+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 考勤结束时间
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.kqEndDate+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'endDateCode',
        				name: 'endDateCode',
        				store: listDateFieldStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.kq_end_date,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.kqFieldSetDateItems+'</div>',
				        margin: '0 0 0 5'
        			}]
        		}
        		,{// 变动轮岗信息
        			xtype:'component',
        			margin:'10 0 0 0',
        			html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        				+kq.param.kqChangeField
        				+'</div>'
        		},{// 变动子集
        			xtype:'panel',
        			width:'100%',
        			margin:'20 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.changeFieldSet+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'changeFieldSetid',
        				name: 'changeFieldSetid',
        				store: listFieldSetCodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.setid,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.changeFieldSetDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 变动部门
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.changeUM+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'changeUMCode',
        				name: 'changeUMCode',
        				store: listChangeUMCodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.dept_field,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.changeUMDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 变动开始日期
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.changeStartDate+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'changeStartDateCode',
        				name: 'changeStartDateCode',
        				store: listChangeDateCodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.start_field,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.changeDateDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 变动结束日期
        			xtype:'panel',
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[{
        				xtype:'component',
        				width:kqParameter.descWidth,
        				html:'<div style="float:right;padding-right:10px;">'+kq.param.changeEndDate+'</div>'
        			},{
        				xtype: 'combobox',
        				width: 300,
        				height: 24,
        				id: 'changeEndDateCode',
        				name: 'changeEndDateCode',
        				store: listChangeDateCodeStore,
        				queryMode: 'local', 
        				displayField: 'dataName',
        				valueField: 'dataValue',
        				value: jsonData.kqParameMap.end_field,
        				emptyText: kq.param.choose,
        				editable: false
        			},{
        				xtype: 'label',
				        html: '<div style="float:right;padding-left:10px;">'+kq.param.changeDateDesc+'</div>',
				        margin: '0 0 0 5'
        			}]
        		},{// 申请信息
        			xtype:'component',
        			margin:'10 0 0 0',
        			html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        				+"申请信息"
        				+'</div>'
        		},{
        			xtype:'panel',
        			width:700,
        			height:250,
        			margin:'10 0 10 0',
        			border:0,
        			layout:{
        				type:'hbox',
        				align:'middle'
        			},
        			items:[
        				applyTabpanel
        			]
        		},{// 数据上报
        			xtype:'component',
        			html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        				+'数据上报'+'</div>',
        		},{// 允许修改统计项、计算项数据
        			xtype:'panel',
        			width:'100%',
        			//margin:'10 0 10 0',
        			border:0,
        			items:[{
        				xtype : 'checkboxgroup',
        		        margin:'10 0 0 50',
        		        items : [{
        		        	id: 'enableModifyid',
        		        	boxLabel: kq.param.updateEnableModify,
	                        name: 'enableModify',
	                        checked: ('1'==jsonData.kqParameMap.report_daily_apply.enable_modify)
        		        }]
        			}]
        		},{//填写审批意见 0：不填写（默认），1：需要填写意见
        			xtype:'panel',
        			width:'100%',
        			//margin:'10 0 10 0',
        			border:0,
        			items:[{
        				xtype : 'checkboxgroup',
        		        margin:'10 0 10 50',
        		        items : [{
        		        	id: 'approvalMessageid',
        		        	boxLabel: kq.param.approvalMessage,
	                        name: 'approvalMessage',
	                        checked: ('1'==jsonData.kqParameMap.report_daily_apply.approval_message)
        		        }]
        			}]
        		}],
        		renderTo: Ext.getBody()
	        });
	        
	        new Ext.Viewport({
	        	layout: 'border',
	        	items: [formPanel]
	        });
	        // 变动子集选择
	        Ext.getCmp("changeFieldSetid").on('change',kqParameter.changeKqFieldSet);
        	// 请假子集选择
        	Ext.getCmp("leave_setid").on('change',kqParameter.changeKqApplySet);
        	// 公出子集选择
        	Ext.getCmp("officeleave_setid").on('change',kqParameter.changeKqApplySet);
        	// 加班子集选择
        	Ext.getCmp("overtime_setid").on('change',kqParameter.changeKqApplySet);
	        
		}else {
			Ext.showAlert(map.message);
		}
	},
	/**
	 * 选择申请子集后渲染方法
	 */
	changeKqApplySet:function(t, newValue, oldValue){
		var id = t.id;
		var flag = "";
		// 请假leave  公出officeleave 加班overtime
		if("leave_setid" == id){
			flag = "leave";
		}else if("officeleave_setid" == id){
			flag = "officeleave";
		}else if("overtime_setid" == id){
			flag = "overtime";
		}else
			return;
		
		var setidValue = Ext.getCmp(id).getValue();
		if(setidValue == null){
			newValue = "";
		}
		var json = {};
		json.type = "kqApplySetid";
		json.setid = newValue;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021501',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				var jsonObj = result.returnStr;
				if(jsonObj.return_code == "fail"){
					Ext.showAlert(jsonObj.return_msg);
					return;
				}else{
					var jsonData = jsonObj.return_data;
					// 代码型下拉数据
			        var listCodeStore = Ext.create('Ext.data.Store',{
			        	fields:['dataValue','dataName'],
			        	data:jsonData.listCode
			        });
			        var type =Ext.getCmp(flag+'_type'); 
			        type.setStore(listCodeStore);
			        type.setValue('');
			        
					// 日期型下拉数据
			        var listDateStore = Ext.create('Ext.data.Store',{
			        	fields:['dataValue','dataName'],
			        	data:jsonData.listDate
			        });
			        var startDate =Ext.getCmp(flag+'_start'); 
			        startDate.setStore(listDateStore);
			        startDate.setValue('');
			        var endDate =Ext.getCmp(flag+'_end'); 
			        endDate.setStore(listDateStore);
			        endDate.setValue('');
					
			        // 字符型下拉数据
			        var listStrStore = Ext.create('Ext.data.Store',{
			        	fields:['dataValue','dataName'],
			        	data:jsonData.listStr
			        });
			        var reason =Ext.getCmp(flag+'_reason'); 
			        reason.setStore(listStrStore);
			        reason.setValue('');
				}
			}else 
				Ext.showAlert(result.message);
			
	    }},map);
	},
	/**
	 * 选择人员变动子集后渲染方法
	 */
	changeKqFieldSet:function(t, newValue, oldValue){
		var changeFieldSetidValue = Ext.getCmp("changeFieldSetid").getValue();
		if(changeFieldSetidValue == null){
			newValue = "";
		}
		var json = {};
		json.type = "kqChangeSetid";
		json.setid = newValue;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021501',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				var jsonObj = result.returnStr;
				if(jsonObj.return_code == "fail"){
					Ext.showAlert(jsonObj.return_msg);
					return;
				}else{
					var jsonData = jsonObj.return_data;
					var listChangeUMCodeStore = Ext.create('Ext.data.Store',{
			        	fields:['dataValue','dataName'],
			        	data:jsonData.listChangeUM
			        });
					var changeUMCode =Ext.getCmp("changeUMCode"); 
					changeUMCode.setStore(listChangeUMCodeStore);
					changeUMCode.setValue('');
				    
			        var listChangeDateCodeStore = Ext.create('Ext.data.Store',{
			        	fields:['dataValue','dataName'],
			        	data:jsonData.listChangeDate
			        });
			        var changeStartDateCode =Ext.getCmp("changeStartDateCode"); 
			        changeStartDateCode.setStore(listChangeDateCodeStore);
			        changeStartDateCode.setValue('');
				    var changeEndDateCode =Ext.getCmp("changeEndDateCode"); 
				    changeEndDateCode.setStore(listChangeDateCodeStore);
				    changeEndDateCode.setValue('');
				    
//					Ext.showAlert(kq.label.saveSuccess);
				}
			}else 
				Ext.showAlert(result.message);
			
	    }},map);
	},
	/**
	 * 保存参数
	 */
	saveKqParam:function(){
		var cardnoCodeValue = Ext.util.Format.trim(Ext.getCmp("cardnoCode").getValue());
		if(Ext.isEmpty(cardnoCodeValue)){
			Ext.showAlert(kq.param.cardNoEmpty);
			return;
		}
		var gnoCodeValue = Ext.util.Format.trim(Ext.getCmp("gnoCode").getValue());
		if(Ext.isEmpty(gnoCodeValue)){
			Ext.showAlert(kq.param.gnoNotEmpty);
			return;
		}
		var checkboxgroup = Ext.getCmp('checkboxgroup').getChecked();
	    var nbaseCheck = "";
	    Ext.Array.each(checkboxgroup, function(item){
	    	nbaseCheck += item.name + ",";
	    });
	    if(!Ext.isEmpty(nbaseCheck))
	    	nbaseCheck = nbaseCheck.substring(0, nbaseCheck.length-1);
	    // 考勤部门指标
	    var deptCodeValue = Ext.util.Format.trim(Ext.getCmp("deptCode").getValue());
	    
	    // 考勤变动部门指标
	    var changeFieldSetidValue = Ext.util.Format.trim(Ext.getCmp("changeFieldSetid").getValue());
	    var changeUMCodeValue = "";
    	var changeStartDateCodeValue = "";
    	var changeEndDateCodeValue = "";
    	// 若变动子集不为空则其他三项为必填项
	    if(!Ext.isEmpty(changeFieldSetidValue)){
	    	changeUMCodeValue = Ext.util.Format.trim(Ext.getCmp("changeUMCode").getValue());
	    	if(Ext.isEmpty(changeUMCodeValue)){
	    		Ext.showAlert(kq.param.changeUMNotEmpty);
				return;
	    	}
	    	changeStartDateCodeValue = Ext.util.Format.trim(Ext.getCmp("changeStartDateCode").getValue());
	    	if(Ext.isEmpty(changeStartDateCodeValue)){
	    		Ext.showAlert(kq.param.changeStartDateNotEmpty);
				return;
	    	}
	    	changeEndDateCodeValue = Ext.util.Format.trim(Ext.getCmp("changeEndDateCode").getValue());
	    	if(Ext.isEmpty(changeEndDateCodeValue)){
	    		Ext.showAlert(kq.param.changeEndDateNotEmpty);
				return;
	    	}
	    }
	    var startDateCodeValue = Ext.util.Format.trim(Ext.getCmp("startDateCode").getValue());
	    var endDateCodeValue = Ext.util.Format.trim(Ext.getCmp("endDateCode").getValue());
    	// 请假leave  公出officeleave 加班overtime
	    var leaveJson = kqParameter.getOneApplyJson("leave");
    	if(false == leaveJson){
    		Ext.showAlert(kq.msg.noLeaveSet);
    		return;
    	}
    	var officeleaveJson = kqParameter.getOneApplyJson("officeleave");
    	if(false == officeleaveJson){
    		Ext.showAlert(kq.msg.noOfficeleaveSet);
    		return;
    	}
    	var overtimeJson = kqParameter.getOneApplyJson("overtime");
    	if(false == overtimeJson){
    		Ext.showAlert(kq.msg.noOvertimeSet);
    		return;
    	}
    	
    	var enableModifyVlaue = Ext.getCmp("enableModifyid").checked ? "1" : "0";
    	var approvalMessageVlaue = Ext.getCmp("approvalMessageid").checked ? "1" : "0";
    	var reportDailyApplyJson = {'enable_modify':enableModifyVlaue,'approval_message':approvalMessageVlaue};
	    
	    var json = {};
		json.type = "save";
		json.nbase = nbaseCheck;
		json.g_no = gnoCodeValue;
		json.kq_dept = deptCodeValue;
		json.setid = changeFieldSetidValue;
		json.dept_field = changeUMCodeValue;
		json.start_field = changeStartDateCodeValue;
		json.end_field = changeEndDateCodeValue;
		json.kq_start_date = startDateCodeValue;
		json.kq_end_date = endDateCodeValue;
		json.card_no = cardnoCodeValue;
		// 申请子集信息
		json.leave_subset = leaveJson;
		json.officeleave_subset = officeleaveJson;
		json.overtime_subset = overtimeJson;
		
		json.report_daily_apply = reportDailyApplyJson;
		
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021501',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				var jsonObj = result.returnStr;
				if(jsonObj.return_code == "fail"){
					Ext.showAlert(jsonObj.return_msg);
					return;
				}else
					Ext.showAlert(kq.label.saveSuccess);
			}else 
				Ext.showAlert(result.message);
			
	    }},map);
	},
	/**
	 * 获取申请信息applyTabpanel
	 */
	getApplyTabpanel:function(){
		// 申请信息页签tabpanel
        var applyTabpanel = Ext.create('Ext.tab.Panel', {
			border:true,
			width:'100%',
			height:245,
			activeTab:0,
			margin:'8 20 0 20',
			id:'applyTabpanelid',
	   	    items: [{
	   	            title: "请假申请",
	   	            id:'leaveid',
	   	            height:245,
	   	    		border:false,
	   	    		items: kqParameter.getOneApplyTabpanel("leave"),
	   	            listeners: { 
	   	    			activate:function (declarer) {
	   	    			} 
	   	    		}
	   	        },{
	   	            title: "公出申请",
	   	            id:'officeleaveid',
	   	            height:245,
	   	    		border:false,
	   	    		items: kqParameter.getOneApplyTabpanel("officeleave"),
	   	            listeners: { 
	   	    			activate:function (declarer) {
	   	    			} 
	   	    		}
	   	        },{
	   	            title: "加班申请",
	   	            id:'overtimeid',
	   	            height:245,
	   	    		border:false,
	   	    		items: kqParameter.getOneApplyTabpanel("overtime"),
	   	            listeners: { 
	   	    			activate:function (declarer) {
	   	    			} 
	   	    		}
	   	        }
	   	    ],
	   	    listeners:{
	   	    	afterrender:function(tabpanel){
	   	    		//semdMessage_me.addTabs(tabpanel);
	   	    	}
	   	    }
	   	});
        
        return applyTabpanel;
	},
	/**
	 * 获取一个页签对象
	 * flag 请假leave  公出officeleave 加班overtime
	 */
	getOneApplyTabpanel:function(flag){
		
		var flagInfo = "";
		var subsetMap = new HashMap();
		var listDate = null;
    	var listStr = null;
    	var listCode = null;
		if("leave" == flag){
			flagInfo = "请假";
			subsetMap = kqParameter.kqParameMap.leave_subset;
			// 请假子集信息
			listDate = kqParameter.leaveListDate;
			listStr = kqParameter.leaveListStr;
			listCode = kqParameter.leaveListCode;
	    	
		}else if("officeleave" == flag){
			flagInfo = "公出";
			subsetMap = kqParameter.kqParameMap.officeleave_subset;
			// 公出子集信息
	    	listDate = kqParameter.officeleaveListDate;
			listStr = kqParameter.officeleaveListStr;
			listCode = kqParameter.officeleaveListCode;
	    	
		}else if("overtime" == flag){
			flagInfo = "加班";
			subsetMap = kqParameter.kqParameMap.overtime_subset;
			// 加班子集信息
	    	listDate = kqParameter.overtimeListDate;
			listStr = kqParameter.overtimeListStr;
			listCode = kqParameter.overtimeListCode;
		}else
			return ;
		
		// 子集
		var setidValue = subsetMap.setid;
		// 申请类型
		var typeValue = subsetMap.type;
		// 开始时间
		var startValue = subsetMap.start;
		// 结束时间
		var endValue = subsetMap.end;
		// 事由
		var reasonValue = subsetMap.reason;
		
		// 子集信息
        var listFieldSetStore = Ext.create('Ext.data.Store',{
        	fields:['dataValue','dataName'],
        	data:kqParameter.listFieldSet
        });
        // 代码型下拉数据
        var listCodeStore = Ext.create('Ext.data.Store',{
        	fields:['dataValue','dataName'],
        	data:listCode
        });
        // 字符型下拉数据
        var listStrStore = Ext.create('Ext.data.Store',{
        	fields:['dataValue','dataName'],
        	data:listStr
        });
        // 日期型下拉数据
        var listDateStore = Ext.create('Ext.data.Store',{
        	fields:['dataValue','dataName'],
        	data:listDate
        });
        
        var oneApplyTab = [{// 子集
	        			xtype:'panel',
	        			width:'100%',
	        			margin:'20 0 10 0',
	        			border:0,
	        			layout:{
	        				type:'hbox',
	        				align:'middle'
	        			},
	        			items:[{
	        				xtype:'component',
	        				width:kqParameter.descWidth,
	        				html:'<div style="float:right;padding-right:10px;">'+flagInfo+'子集'+'</div>'
	        			},{
	        				xtype: 'combobox',
	        				width: 300,
	        				height: 24,
	        				id: flag+'_setid',
	        				name: flag+'_setid',
	        				store: listFieldSetStore,
	        				queryMode: 'local', 
	        				displayField: 'dataName',
	        				valueField: 'dataValue',
	        				value: setidValue,
	        				emptyText: kq.param.choose,
	        				editable: false
	        			},{
	        				xtype: 'label',
					        html: '<div style="float:right;padding-left:10px;">'+kq.param.changeFieldSetDesc+'</div>',
					        margin: '0 0 0 5'
	        			}]
	        		},{// 类型
	        			xtype:'panel',
	        			width:'100%',
	        			margin:'10 0 10 0',
	        			border:0,
	        			layout:{
	        				type:'hbox',
	        				align:'middle'
	        			},
	        			items:[{
	        				xtype:'component',
	        				width:kqParameter.descWidth,
	        				html:'<div style="float:right;padding-right:10px;">'+flagInfo+'类型'+'</div>'
	        			},{
	        				xtype: 'combobox',
	        				width: 300,
	        				height: 24,
	        				id: flag+'_type',
	        				name: flag+'_type',
	        				store: listCodeStore,
	        				queryMode: 'local', 
	        				displayField: 'dataName',
	        				valueField: 'dataValue',
	        				value: typeValue,
	        				emptyText: kq.param.choose,
	        			}]
	        		},{// 开始时间
	        			xtype:'panel',
	        			width:'100%',
	        			margin:'10 0 10 0',
	        			border:0,
	        			layout:{
	        				type:'hbox',
	        				align:'middle'
	        			},
	        			items:[{
	        				xtype:'component',
	        				width:kqParameter.descWidth,
	        				html:'<div style="float:right;padding-right:10px;">'+flagInfo+'开始时间'+'</div>'
	        			},{
	        				xtype: 'combobox',
	        				width: 300,
	        				height: 24,
	        				id: flag+'_start',
	        				name: flag+'_start',
	        				store: listDateStore,
	        				queryMode: 'local', 
	        				displayField: 'dataName',
	        				valueField: 'dataValue',
	        				value: startValue,
	        				emptyText: kq.param.choose,
	        			}]
	        		},{// 结束时间
	        			xtype:'panel',
	        			width:'100%',
	        			margin:'10 0 10 0',
	        			border:0,
	        			layout:{
	        				type:'hbox',
	        				align:'middle'
	        			},
	        			items:[{
	        				xtype:'component',
	        				width:kqParameter.descWidth,
	        				html:'<div style="float:right;padding-right:10px;">'+flagInfo+'结束时间'+'</div>'
	        			},{
	        				xtype: 'combobox',
	        				width: 300,
	        				height: 24,
	        				id: flag+'_end',
	        				name: flag+'_end',
	        				store: listDateStore,
	        				queryMode: 'local', 
	        				displayField: 'dataName',
	        				valueField: 'dataValue',
	        				value: endValue,
	        				emptyText: kq.param.choose,
	        			}]
	        		},{// 事由
	        			xtype:'panel',
	        			width:'100%',
	        			margin:'10 0 10 0',
	        			border:0,
	        			layout:{
	        				type:'hbox',
	        				align:'middle'
	        			},
	        			items:[{
	        				xtype:'component',
	        				width:kqParameter.descWidth,
	        				html:'<div style="float:right;padding-right:10px;">'+flagInfo+'事由'+'</div>'
	        			},{
	        				xtype: 'combobox',
	        				width: 300,
	        				height: 24,
	        				id: flag+'_reason',
	        				name: flag+'_reason',
	        				store: listStrStore,
	        				queryMode: 'local', 
	        				displayField: 'dataName',
	        				valueField: 'dataValue',
	        				value: reasonValue,
	        				emptyText: kq.param.choose,
	        			}]
	        		}];
        
        return oneApplyTab;
	},
	/**
	 * 获取要保存的 申请数据json对象
	 */
	getOneApplyJson:function(flag){
		var json = {};
		json.setid = Ext.util.Format.trim(Ext.getCmp(flag+'_setid').getValue());
		json.type = Ext.util.Format.trim(Ext.getCmp(flag+'_type').getValue());
		json.start = Ext.util.Format.trim(Ext.getCmp(flag+'_start').getValue());
		json.end = Ext.util.Format.trim(Ext.getCmp(flag+'_end').getValue());
		json.reason = Ext.util.Format.trim(Ext.getCmp(flag+'_reason').getValue());
		// 如果选择子集 必须选其他指标
		if(!Ext.isEmpty(json.setid)){
			if(Ext.isEmpty(json.type) || Ext.isEmpty(json.start) 
					|| Ext.isEmpty(json.end) || Ext.isEmpty(json.reason)){
				
				return false;
			}
		}
		return json;
	}
	
});
