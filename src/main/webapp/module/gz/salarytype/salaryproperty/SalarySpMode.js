/**
 * 薪资类别-薪资属性-审批方式
 * lis 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.SalarySpMode',{
        constructor:function(config){
			salarySpMode_me = this;
			salarySpMode_me.salaryid = config.salaryid;
			var result = config.result;
			salarySpMode_me.gz_module = result.gz_module;
			
			salarySpMode_me.rightvalue = result.rightvalue;
			salarySpMode_me.addrightvalue = result.addrightvalue;
			salarySpMode_me.delrightvalue = result.delrightvalue;
			
			salarySpMode_me.flow_ctrl = result.flow_ctrl;//是否需要审批
			salarySpMode_me.reject_mode = result.reject_mode;//驳回方式  1:逐级驳回  2：驳回到发起人
			salarySpMode_me.collectPoint = result.collectPoint;//汇总指标
			salarySpMode_me.collectList = result.collectList;//汇总指标列表
			salarySpMode_me.sp_relation_id = result.sp_relation_id;//审批关系
			salarySpMode_me.spRelationList = result.spRelationList;//审批关系列表
			salarySpMode_me.sp_default_filter_id = result.sp_default_filter_id;//默认审批项目
			salarySpMode_me.spDefaultFilterList = result.spDefaultFilterList;//默认审批项目列表
			salarySpMode_me.orgid = result.orgid;//归属单位指标
			salarySpMode_me.orgList = result.orgList;//归属单位列表
			salarySpMode_me.deptid = result.deptid;//归属部门指标
			salarySpMode_me.deptList = result.deptList;//归属部门列表
			salarySpMode_me.smsNotice = result.smsNotice;//短信通知
			salarySpMode_me.mailNotice = result.mailNotice;//邮件通知
			salarySpMode_me.mailTemplateId = result.mailTemplateId;//模板id
			salarySpMode_me.mailTemplateList = result.mailTemplateList;//模板列表
			
			salarySpMode_me.createSalary(result); 
        },
		 createSalary:function(result)  
		 {
        	//审批关系
        	var states1 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//默认审批项目
        	var states2 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//归属单位
        	var states3 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	//归属部门
        	var states4 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	//模板列表
        	var states5 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	Ext.each(salarySpMode_me.spRelationList,function(obj,index){
        		states1.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salarySpMode_me.spDefaultFilterList,function(obj,index){
        		states2.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salarySpMode_me.orgList,function(obj,index){
        		states3.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salarySpMode_me.deptList,function(obj,index){
        		states4.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	Ext.each(salarySpMode_me.mailTemplateList,function(obj,index){
        		if(obj.dataValue!='createnew')//bug:18792 新版暂时去除新增模板功能。 2016.6.3
        		states5.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	})
        	
        	var width = 250;
        	var laelwidth = 75;
        	// 审批关系下拉框
        	var combox1 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.auditRelation,//审批关系
        		labelAlign:'right',
        		labelSeparator :'',
        		editable:false,
        		labelWidth:laelwidth,
         	    store: states1,
         	    width:width,
         	    name:'sp_relation_id',
         	    value:salarySpMode_me.sp_relation_id,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 默认审批项目下拉框
        	var combox2 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.defaultAuditRelation,//默认审批项目
        		labelSeparator :'',
        		labelAlign:'right',
        		editable:false,
        		labelWidth:laelwidth,
         	    store: states2,
         	    width:width,
         	    name:'sp_default_filter_id',
         	    value:salarySpMode_me.sp_default_filter_id,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 归属单位下拉框
        	var combox3 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.unitField,//归属单位指标
        		labelAlign:'right',
        		labelSeparator :'',
        		editable:false,
        		labelWidth:laelwidth,
         	    store: states3,
         	    width:width,
         	    padding:'5 0 5 0',
         	    name:'orgid',
         	    value:salarySpMode_me.orgid,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 归属部门下拉框
        	var combox4 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.departmentField,//归属部门指标
        		labelAlign:'right',
        		labelSeparator :'',
        		editable:false,
        		labelWidth:laelwidth,
         	    store: states4,
         	    width:width,
         	    name:'deptid',
         	    value:salarySpMode_me.deptid,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 通知模板下拉框
        	var combox5 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.noticeTemplate,//通知模板
        		labelSeparator :'',
        		labelAlign:'right',
        		editable:false,
        		labelWidth:laelwidth,
         	    store: states5,
         	    width:width,
         	    emptyText:"请选择...",
         	    name:'mailTemplateId',
         	    value:salarySpMode_me.mailTemplateId,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	//汇总指标
        	var states6 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
//        	if(salarySpMode_me.collectPoint){
        		Ext.each(salarySpMode_me.collectList,function(obj,index){
        			states6.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        		})
//        	}
        	
        	// 汇总指标下拉框
        	var combox6 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.collectField,//汇总指标
        		labelAlign:'right',
        		labelSeparator :'',
        		editable:false,
        		labelWidth:laelwidth,
         	    store: states6,
         	    width:width,
         	    hidden:salarySpMode_me.collectPoint==null?true:false,
         	    name:'collectPoint',
         	    value:salarySpMode_me.collectPoint,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	var fieldsetWidth = 470;
        	//比对指标
        	var bdFieldset = Ext.widget({
				xtype:'fieldset',
				width:fieldsetWidth,
				height: 65,  
				title:gz.label.comparisonField,//比对指标
				padding:'11 0 0 10',
				items:[{
					xtype:'button',
					text:gz.button.setComparisonField,//设置比对指标
					handler:function(){
						Ext.require('SalaryTypeUL.salaryproperty.SetChangeSp', function(){
			        		var changeSpField = Ext.create("SalaryTypeUL.salaryproperty.SetChangeSp",{result:result,salaryid:salaryProperty_me.salaryid,gz_module:salarySpMode_me.gz_module,callBackfn:salarySpMode_me.setChangeField,
			        						rightValue:salarySpMode_me.rightvalue,addrightValue:salarySpMode_me.addrightvalue,delrightValue:salarySpMode_me.delrightvalue});
			    		});
					}
				}]
			});
        	
        	//需要审批时显示的内容
        	var spPanel = Ext.widget({
        		xtype:'panel',
        		id:'spPanelId',
        		padding:'5 0 5 0',
        		hidden:salarySpMode_me.flow_ctrl==0?true:false,
        		border:false,
        		layout:'vbox',
        		items:[{
		            xtype      : 'fieldcontainer',
		            fieldLabel : gz.label.rejectMode,//驳回方式
		            labelSeparator :'',
		            labelAlign:'right',
		            labelSeparator :'',
	        		labelWidth:76,
		            defaultType: 'radiofield',
		            defaults: {
		                flex: 1
		            },
		            layout: 'hbox',
		            items: [
		                {
		                    boxLabel  : gz.label.layerReject,//逐级驳回
		                    name      : 'reject_mode',
		                    inputValue: '1',
		                    width:80,
							checked:salarySpMode_me.reject_mode==1?true:false,
		                    id        : 'radio1'
		                }, {
		                    boxLabel  : gz.label.rejectToOriginator,//驳回到发起人
		                    name      : 'reject_mode',
		                    inputValue: '2',
		                    width:90,
							checked:salarySpMode_me.reject_mode==2?true:false,
		                    id        : 'radio2'
		                }
		            ]
		        },combox6,combox1]
        	});
        	
        	//审批方式
        	var spFieldset = Ext.widget({
				xtype:'fieldset',
				width:fieldsetWidth,
				height: salarySpMode_me.flow_ctrl==0?136:230,  
				title:gz.label.SpMode,//审批方式
				padding:'5 0 0 10',
				layout:'vbox',
				items:[{
					xtype:'radio',
					name:'flow_ctrl',
					boxLabel:gz.label.noNeedAudit,//不需要审批
					inputValue:'0',
					width:80,
					checked:salarySpMode_me.flow_ctrl==0?true:false,
					listeners:{
						'change':function(radio,newValue,oldValue){
							if(newValue){
								spFieldset.queryById('spPanelId').hide();
								Ext.getCmp('noticeId').hide();
								spFieldset.setHeight(136);
							}else{
								spFieldset.queryById('spPanelId').show();
								Ext.getCmp('noticeId').show();
								spFieldset.setHeight(230);
							}
						}
				    }
				},{
					xtype:'panel',
					layout:'hbox',
					border:false,
					items:[{
						xtype:'radio',
						name:'flow_ctrl',
						boxLabel:gz.label.neddAudit,//需要审批
						inputValue:'1',
						width:80,
						checked:salarySpMode_me.flow_ctrl==1?true:false
					}]
				},spPanel,{
					xtype:'panel',
					layout:'hbox',
					width:'100%',
					border:false,
					items:[combox3,{
						xtype:'label',
						padding:'8 0 0 5',
						text:gz.msg.noB0110//(除B0110外关联UN的指标)
					}]
				},{
					xtype:'panel',
					layout:'hbox',
					width:'100%',
					border:false,
					items:[combox4,{
						xtype:'label',
						padding:'4 0 0 5',
						text:gz.msg.noE0122//(除E0122外关联UM的指标)
					}]
				}]
			});
        	
        	//通知方式
        	var noticeFieldset = Ext.widget({
				xtype:'fieldset',
				id:'noticeId',
				hidden:salarySpMode_me.flow_ctrl==0?true:false,
				width:fieldsetWidth,
				height: 60,  
				title:gz.label.noticeMode,//通知方式
				padding:'8 0 0 10',
				layout:'hbox',
				items:[{
					xtype     : 'checkbox',
					boxLabel  : gz.label.smsNotice,//短信通知
					name:'smsNotice',
					checked      : salarySpMode_me.smsNotice==1?true:false,
                    inputValue: '1',
                    id:'smsNotice'
				},{
					xtype     : 'checkbox',
					boxLabel  : gz.label.mailNotice,//邮件通知
					name:'mailNotice',
					padding:'0 0 0 10',
					checked      : salarySpMode_me.mailNotice==1?true:false,
                    inputValue: '1',
                    id:'mailNotice'
				},combox5]
			});
        	
    		//审批方式panel
        	var scopePanel = Ext.widget({
        		xtype:'panel',
		    	border:false,
		    	width:500,
		    	padding:'5 0 0 10',
		    	layout: {
		            type: 'vbox'
		        },
		    	items:[bdFieldset,spFieldset,noticeFieldset]
        	});
        	
        	//将当前panel渲染到tab页
        	salaryProperty_me.tabs.child('#spId').add(scopePanel);
		},
		
		//设置比对指标
		setChangeField:function(rightvalue,addrightvalue,delrightvalue){
			salarySpMode_me.rightvalue = rightvalue;
			salarySpMode_me.addrightvalue = addrightvalue;
			salarySpMode_me.delrightvalue = delrightvalue;
		}
 });