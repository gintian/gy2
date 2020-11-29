/**
 * 薪资类别-薪资属性-计件薪资
 * lis 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.PriecerateValid',{
        constructor:function(config){
			pricecrate_me = this;
			pricecrate_me.salaryid = config.salaryid;
			var result = config.result;
			pricecrate_me.priecerateList = result.priecerateList;//引入指标表格数据
			pricecrate_me.priecerateFieldList = result.priecerateFieldList;//计件薪资指标
			pricecrate_me.salarySetFieldList = result.salarySetFieldList;//薪资项目
			
			pricecrate_me.createSalary(); 
        },
		 createSalary:function()  
		 {

        	//周期下拉框
        	var states = Ext.create('Ext.data.Store', {
        	    fields: ['value', 'sp_status'],
        	    data : [
        	        {"value":"1", "sp_status":gz.label.month},//月
        	        {"value":"2", "sp_status":gz.label.season},//季 
        	        {"value":"3", "sp_status":gz.label.halfYear},//半年
        	        {"value":"4", "sp_status":gz.label.year}//年
        	    ]
        	});
        	
        	//计件指标下拉框
        	var pricecrateStore = Ext.create('Ext.data.Store', {
        		 fields: ['priecerateField', 'dataValue']
        	});
        	
        	//薪资指标下落框
        	var salaryItemStore = Ext.create('Ext.data.Store', {
        		 fields: ['salartSetField', 'dataValue']
        	});
        	
        	Ext.each(pricecrate_me.priecerateFieldList,function(obj,index){
        		pricecrateStore.insert(index,[{priecerateField: obj.priecerateField,dataValue: obj.dataValue}]);
        	});
        	
        	Ext.each(pricecrate_me.salarySetFieldList,function(obj,index){
        		salaryItemStore.insert(index,[{salartSetField: obj.salartSetField,dataValue: obj.dataValue}]);
        	});
        	
        	var margin = "10 0 0 0";
        	var width = 110;
        	var labelWidth = 60;
        	// 周期
        	var combox = Ext.create('Ext.form.ComboBox', {
        		fieldLabel:gz.label.period,//周期
         	    store: states,
         	    editable:false,
         	    margin:'0 10 0 0',
         	    width:width,
         	    labelWidth:labelWidth+5,
         	    name:'priecerate_period',
         	    value:salaryOtherParam_me.priecerate_period,
         	    queryMode: 'local',
         	    displayField: 'sp_status',
         	    valueField: 'value',
         	    listeners:{
	        		'change':function(f,newValue){
	        			if(newValue == "1")
	        				panel.queryById('monthId').show();
	        			else
	        				panel.queryById('monthId').hide();
		        	}
        		}
        	});
        	
        	// 计件指标下拉框
        	var priecerateCombox = Ext.create('Ext.form.ComboBox', {
         	    store: pricecrateStore,
         	    editable:false,
         	    labelWidth:labelWidth,
         	    queryMode: 'local',
         	    displayField: 'priecerateField',
         	    valueField: 'dataValue'
        	});
        	
        	// 薪资指标下落框
        	var salaryItemCombox = Ext.create('Ext.form.ComboBox', {
         	    store: salaryItemStore,
         	    editable:false,
         	    labelWidth:labelWidth,
         	    queryMode: 'local',
         	    displayField: 'salartSetField',
         	    valueField: 'dataValue'
        	});
        	
        	//引入指标表格数据store
        	var store = Ext.create('Ext.data.Store', {
				fields:['priecerateField','salartSetField']
        	});
        	
        	Ext.each(pricecrate_me.priecerateList,function(obj,index){
        		store.insert(index,[{priecerateField: obj.priecerateField,salartSetField: obj.salartSetField}]);
        	});
        	
        	//引入指标表格
        	var fieldpanel = Ext.widget({
        		xtype:'grid',
        		id:'fieldpanelId',
        		store: store,
        		border:false,
        		rowLines:true,
        		columnLines:true,
        		forceFit:true,
				width: 300,
		    	height: 230,
		    	plugins: [
		    	           Ext.create('Ext.grid.plugin.CellEditing', {
		    	               clicksToEdit: 1
		    	           })
		    	       ],
		    	selModel: Ext.create("Ext.selection.CheckboxModel", {
		            mode: "multi",//multi,simple,single；默认为多选multi
		            checkOnly: false,//如果值为true，则只用点击checkbox列才能选中此条记录
		            allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
		            enableKeyNav: true
		        }),
				columns: [
					{ text: gz.label.priecerateField,menuDisabled:true, dataIndex: 'priecerateField',flex: 1,editor:priecerateCombox},//计件指标
					{ text: gz.label.salaryField,menuDisabled:true, dataIndex: 'salartSetField',flex: 1,editor:salaryItemCombox}//薪资指标
				]
        	});
        	
        	var panel = Ext.widget({
        		xtype:'panel',
        		border:false,
        		layout:'vbox',
        		items:[
        		{
        			xtype:'panel',
        			border:false,
        			layout:'hbox',
        			items:[{
						xtype:'label',
						text:gz.label.dataScope+"："//数据范围:
					},{
        				xtype:'button',
        				id:'dataCond',
        				text:'...',
        				margin:'0 0 0 10',
        				handler:function(){
							var map = new HashMap();
	    					map.put("saveText", common.button.ok);//"确定"
	    					map.put("itemsetid",'s05');
	    					Ext.Loader.setPath("DECN","../../../components/definecondition");
	    				 	Ext.require('DECN.DefineCondition',function(){
	    				 		Ext.create("DECN.DefineCondition",{dataMap:map,imodule:'3',opt:"1",conditions:salaryOtherParam_me.priecerate_expression_str,afterfunc:'pricecrate_me.getCon'});
	    				 	});
						}
        			}]
        		},{
        			xtype:'panel',
        			border:false,
        			layout:'hbox',
        			margin:margin,
        			items:[combox,
        			  {
        				xtype:'panel',
        				itemId:'monthId',
        				hidden:salaryOtherParam_me.priecerate_period=="1"?false:true,
        				layout:'hbox',
        				border:false,
        				items:[ {
            				xtype:'radio',
    						name:'month',
    						boxLabel:gz.label.natureMonth,//自然月份
    						margin:'0 5 0 0',
    						inputValue:"1",
    						width:65,
    						checked:salaryOtherParam_me.priecerate_firstday=="1"?true:false
            			},
            			{
            				xtype:'radio',
    						name:'month',
    						boxLabel:gz.label.preMonth,//上月
    						labelWidth:2,
    						inputValue:'2',
    						width:45,
    						checked:salaryOtherParam_me.priecerate_firstday!="1"?true:false
            			},{
            				xtype: 'numberfield',
        			        width: 40,
        			        name: 'priecerate_firstday',
        			        margin:'0 2 0 0',
        			        value: salaryOtherParam_me.priecerate_firstday=="1"?2:salaryOtherParam_me.priecerate_firstday,
        			        maxValue: 28,
        			        minValue: 2,
        			        listeners:{
            					'change':function(number,newValue,oldValue){
            						if(newValue>28)
            							number.setValue(28);
            						else
            							Ext.getCmp('thisMonthId').setValue(newValue-1);
            						if(newValue<2)
            							number.setValue(2);
            						else
            							Ext.getCmp('thisMonthId').setValue(newValue-1);
            					}
            				}
            			},{
            				xtype: 'numberfield',
        			        width: 78,
        			        name: 'thisMonth',
        			        id:'thisMonthId',
        			        fieldLabel: gz.label.toThisMonth,//日到本月
        			        labelSeparator:'',
        			        labelWidth:48,
        			        value: 1,
        			        hideTrigger:true,
        			        editable: false
            			}]
        			   }
        			]
        		},
		       {
					xtype:'panel',
					border:false,
					layout:'hbox',
					margin:margin,
					items:[{
						xtype:'label',
						text:gz.label.importField//'引入指标:'
					},{
						xtype:'panel',
						margin:'0 0 0 10',
						width: 300,
				    	height: 230,
				    	buttonAlign:'left',
		        		minButtonWidth:50,
    					items:fieldpanel,
    					buttons: [
    					          { text: common.button.insert,//新增
    					        	handler:function(){
    					        	  var obj = new Object();
    					        	  store.insert(store.getCount(),obj);
    					          } },
    					          { text: common.button.todelete,//删除
    					        	handler:function(){
    					        	  var sel = fieldpanel.getSelectionModel().getSelection();
    					        	  store.remove(sel);
    					          } }
    					]
					}]
		       }]
        	})
 /**       	var dbaseFieldset = Ext.widget({
				xtype:'fieldset',
				height: 325, 
				width: 390,
				title:gz.label.priecerateSalary,//计件薪资
				padding:'5 0 0 10',
				items:[{
					xtype:'form',
					id:'priFormId',
					border:false,
					items:panel
				}]
			});*/
        	
        	//生成弹出得window
			var win=Ext.widget("window",{
	   		  //title : gz.label.newItemField,
	   		  title:gz.label.priecerateSalary,//计件薪资
	   		  height: 405, 
	   		  width: 390,
	   		  minButtonWidth:45,
		      border:false,
			  modal:true,
			  closeAction:'destroy',
			  items: [{
					xtype:'form',
					id:'priFormId',
					border:false,
					items:panel
				}],
	          bbar:[
	                     {xtype:'tbfill'},
			          		{
			          			text:common.button.ok, //保存
			          			style:'margin-right:5px',
			          			handler:function(){
	                    	 		pricecrate_me.ok();
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
	   		}).show();              
		 
		 },
		 
		 //获得数据范围条件
		 getCon:function(con){
			 salaryOtherParam_me.priecerate_expression_str = con;
		 },
		 
		 //点击确定
		 ok:function(){
			 var form = Ext.getCmp('priFormId').getValues();
			 var priecerateFields = "";
			 Ext.getCmp('fieldpanelId').getStore().each(function(record,index){
				 priecerateFields += record.get('priecerateField').split(":")[0] + "=" + record.get('salartSetField').split(":")[0] +",";
			 });
			 salaryOtherParam_me.priecerateFields = priecerateFields;
			 salaryOtherParam_me.priecerate_period = form.priecerate_period;
			 if(form.month=="1")
				 salaryOtherParam_me.priecerate_firstday = "1";
			 else
				 salaryOtherParam_me.priecerate_firstday = form.priecerate_firstday;
		 }
 });