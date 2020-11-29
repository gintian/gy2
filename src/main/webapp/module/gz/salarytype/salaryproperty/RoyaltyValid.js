/**
 * 薪资类别-薪资属性-提成薪资
 * lis 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.RoyaltyValid',{
        constructor:function(config){
			royaltyValid_me = this;
			
			royaltyValid_me.salaryid = config.salaryid;
			var result = config.result;
			
			royaltyValid_me.setList = result.setList;//提成数据子集 
			royaltyValid_me.dateList = salaryOtherParam_me.dateList;//日期数据列表
			royaltyValid_me.fieldList = salaryOtherParam_me.fieldList;//审批关系
			royaltyValid_me.royalty_setid = salaryOtherParam_me.royalty_setid;//提成数据子集 id
			if(royaltyValid_me.royalty_setid==null||royaltyValid_me.royalty_setid=='')
				royaltyValid_me.royalty_setid='blank';
			royaltyValid_me.royalty_date = salaryOtherParam_me.royalty_date;//计划日期指标
			royaltyValid_me.royalty_period = salaryOtherParam_me.royalty_period;//周期
			royaltyValid_me.royalty_relation_fields = salaryOtherParam_me.royalty_relation_fields;//关联指标
			
			royaltyValid_me.createSalary(); 
			royaltyValid_me.initHideOrShow();
        },
		 createSalary:function()  
		 {
        	//提成数据子集
        	var states1 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	//计划日期指标
        	var states2 = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	//周期下拉框
        	var states3 = Ext.create('Ext.data.Store', {
        		fields: ['dataName', 'dataValue'],
        	    data : [
        	        {"dataValue":"1", "dataName":gz.label.month},//月
        	        {"dataValue":"2", "dataName":gz.label.season},//季 
        	        {"dataValue":"3", "dataName":gz.label.halfYear},//半年
        	        {"dataValue":"4", "dataName":gz.label.year}//年
        	    ]
        	});
        	
        	Ext.each(royaltyValid_me.setList,function(obj,index){
        		states1.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(royaltyValid_me.dateList,function(obj,index){
        		states2.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        	});
        	
        	var margin = "10 0 0 0";
        	var width = 300;
        	var labelWidth = 80;
        	// 提成数据子集
        	var combox1 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.rotaltyDataSet,//提成数据子集
         	    store: states1,
         	    editable:false,
         	    width:width,
         	    labelAlign:'right',
         	    id:'cbx_setid',
         	    labelWidth:labelWidth,
         	    name:'royalty_setid',
         	    value:royaltyValid_me.royalty_setid,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue',
         	    listeners:{
	        		'change':function(checkbox,newValue){
	        			royaltyValid_me.relationField(newValue,fieldpanel,combox2,combox3,'')
		        	}
        		}
        	});

        	
        	// 计划日期指标
        	var combox2 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.planDateField,//'计划日期指标'
         	    store: states2,
         	    editable:false,
         	    width:width,
         	    labelAlign:'right',
         	    labelWidth:labelWidth,
         	    margin:margin,
         	    name:'royalty_date',
         	    value:royaltyValid_me.royalty_date,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	// 周期
        	var combox3 = Ext.create('Ext.form.ComboBox', {
        		fieldLabel: gz.label.period,//周期
         	    store: states3,
         	    editable:false,
         	    width:width,
         	    labelAlign:'right',
         	    labelWidth:labelWidth,
         	    margin:margin,
         	    name:'royalty_period',
         	    value:royaltyValid_me.royalty_period,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	//关联指标
        	var fieldpanel = Ext.widget({
        		xtype:'panel',
        		id:'fieldPanelId',
				width: 212,
				height: 190, 
				border:false,
				defaultType: 'checkbox',
				padding:'0 0 0 10',
				autoScroll:true,
        		layout:'vbox'
        	});
        	
        	var royalty_relation_fields = "," + royaltyValid_me.royalty_relation_fields + ",";
        	Ext.each(royaltyValid_me.fieldList,function(obj,index){
        		var dataValue = "," + obj.dataValue + ",";
        		checkbox = Ext.widget({
        			xtype:'checkbox',
					boxLabel  : obj.dataName,
					name:'royalty_relation_fields',
					checked      : royalty_relation_fields.indexOf(dataValue)<0?false:true,
                    inputValue: obj.dataValue,
                    id:obj.dataValue
            	});
        		fieldpanel.add(checkbox);
        	});
        	if(royaltyValid_me.royalty_setid!='blank'){
        		royaltyValid_me.relationField(royaltyValid_me.royalty_setid,fieldpanel,combox2,combox3,royaltyValid_me.royalty_date)
        	}
        	
        	var panel = Ext.widget({
        		xtype:'panel',
        		border:false,
        		layout:'vbox',
        		items:[{
        			xtype:'panel',
        			border:false,
        			layout:'hbox',
        			items:[combox1,{
        				xtype:'button',
        				id:'dataCond',
        				text:gz.label.dataScope,//数据范围
        				margin:'0 0 0 10',
        				handler:function(){
        					var setid=Ext.getCmp('cbx_setid').value;
	          				if(setid==null||setid==''||setid=='blank'){
          						 Ext.showAlert(gz.msg.selectRotaltyDataSet);
								 return;
	          				}
        					var map = new HashMap();
        					map.put("itemsetid", combox1.getValue());
        					map.put("saveText", common.button.ok);//确定
        					Ext.Loader.setPath("DECN","../../../components/definecondition");
        				 	Ext.require('DECN.DefineCondition',function(){
        				 		//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
        				 		Ext.create("DECN.DefineCondition",{dataMap:map,imodule:'3',opt:"1",primarykey:royaltyValid_me.salaryid,conditions:salaryOtherParam_me.strExpression,afterfunc:'royaltyValid_me.getCon'});
        				 	});
        				}
        			}]
        		},combox2,combox3,
        		       {
        					xtype:'panel',
        					border:false,
        					layout:'hbox',
        					margin:margin,
        					items:[{
        						xtype:'label',
        						margin: '0 0 0 30',
        						text:gz.label.relationField//关联指标:
        					},{
        						xtype:'panel',
        						padding:'0 0 0 5',
            					items:fieldpanel
        					}]
        		       }]
        	})
 /**       	var dbaseFieldset = Ext.widget({
				xtype:'fieldset',
				height: 325, 
				width: 390,
				title:gz.label.royaltySalary,//提成薪资
				padding:'5 0 0 10',
				items:[{
					xtype:'form',
					id:'royFormId',
					border:false,
					items:panel
				}]
			});*/
        	
        	//生成弹出得window
			var win=Ext.widget("window",{
	   		  //title : gz.label.newItemField,
	   		  title:gz.label.royaltySalary,//提成薪资
	   		  height: 405, 
	   		  width: 390,
	   		  minButtonWidth:45,
	   		  id:'validMainWin',
//	   		  alwaysOnTop:true,
	   		  resizable:false,
		      border:false,
			  modal:true,
			  closeAction:'destroy',
			  items: [{
					xtype:'form',
					id:'royFormId',
					border:false,
					items:panel
				}],
	          bbar:[
	                     {xtype:'tbfill'},
			          		{
			          			text:common.button.ok, //保存
			          			style:'margin-right:5px',
			          			handler:function(){
			          				var setid=Ext.getCmp('cbx_setid').value;
			          				if(setid==null||setid==''||setid=='blank'){
											 Ext.showAlert(gz.msg.selectRotaltyDataSet);//请选择提成数据子集
											 return;
			          				}
	                    	 		royaltyValid_me.ok();
	                    	 		//win.close();
	                     	}
									
			          		},
			          		{
			          			text:common.button.cancel, //取消
			          			handler:function(){
			          				win.close();
			          			}
			          		},{xtype:'tbfill'}
			           ]     
	   		}).show();              
		 },
		 
		 initHideOrShow:function(){
			 var condButton = Ext.getCmp('dataCond');
			 if(royaltyValid_me.royalty_setid)
					condButton.show();
				else  condButton.hide();
		 },
		 //提成数据子集下拉框值变动时改变关联指标和日期指标
		 relationField:function(royalty_setid,fieldpanel,combox2,combox3,value){
			 combox2.setValue("");
			 combox3.setValue("");
			 fieldpanel.removeAll(true);
			 
			 if(royalty_setid != "blank"){
				 var map = new HashMap();
				 map.put("royalty_setid",royalty_setid)
				 map.put("salaryid",royaltyValid_me.salaryid);
				 Rpc({functionId:'GZ00000233',success:function(response,action){
					 var result = Ext.decode(response.responseText);
					 if (result.succeed) { 
						 var hideFlag = result.hideFlag;
						 var condButton = Ext.getCmp('dataCond');
						 if(hideFlag == "1")
							 condButton.hide();
						 else  condButton.show();
						 //计划日期指标
						 combox2.getStore().removeAll();
						 royaltyValid_me.dateList = result.dateList;
						 Ext.each(result.dateList,function(obj,index){
							 combox2.getStore().insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
						 });
						 //重置下拉框
						 combox2.reset();
						 
						 if(value!=""){
							 combox2.select(value);
						 }else if(result.dateList[0])
							 combox2.select(result.dateList[0].dataValue);
						 else
							 combox2.setValue("");
						 	
						 if(royaltyValid_me.royalty_period)
						 	combox3.setValue(royaltyValid_me.royalty_period);
						 else
						 	combox3.setValue(1);
						 //关联指标
						 royaltyValid_me.fieldList = result.fieldList;
						 
						 var royalty_relation_fields = "," + royaltyValid_me.royalty_relation_fields + ",";
				        	Ext.each(royaltyValid_me.fieldList,function(obj,index){
				        		var dataValue = "," + obj.dataValue + ",";
				        		checkbox = Ext.widget({
				        			xtype:'checkbox',
									boxLabel  : obj.dataName,
									name:'royalty_relation_fields',
									checked      : royalty_relation_fields.indexOf(dataValue)<0?false:true,
				                    inputValue: obj.dataValue,
				                    id:obj.dataValue
				            	});
				        		fieldpanel.add(checkbox);
				        	});  
						 
					 } else {  
						 Ext.showAlert(result.message+"！");
					 }
				 }},map);
			 }
		 },
		 
		 //得到数据范围公式
		 getCon:function(conditions){
			 salaryOtherParam_me.strExpression = conditions;
		 },
		 
		//点击确定
		 ok:function(){
			 var form = Ext.getCmp('royFormId').getValues();
			 salaryOtherParam_me.royalty_setid = form.royalty_setid;
			 salaryOtherParam_me.royalty_date = form.royalty_date;
			 salaryOtherParam_me.royalty_period = form.royalty_period;
			 var fieldCheck = Ext.getCmp('fieldPanelId').items;
             var fields = "";
             for(var i = 0; i < fieldCheck.length; i++){
	             if(fieldCheck.get(i).checked){
	            	 fields += ',' + fieldCheck.get(i).getSubmitValue();
	              }
             }
             if(fields != "")
            	 fields = fields.substring(1);
            else{
            	Ext.showAlert(gz.msg.selectRelationField);
            	return;
            }
             
             salaryOtherParam_me.royalty_relation_fields = fields;
             Ext.getCmp('validMainWin').close();
		 }
 });