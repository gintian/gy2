/**
 * 考勤期间
 * 
 * @date 2018.10.12
 * 
 * @author haosl
 */
Ext.define('ConfigPeriodURL.Period',{
	constructor:function(){
		config_period_me = this;
		this.getListView();
	},
	/**
	 * 考勤期间列表
	 */
	getListView:function(){
		var map = new HashMap();
		var json = {"type":"pclist","kq_year":""};
		map.put("jsonStr", Ext.encode(json));
		Rpc({functionId:'KQ00020301',async:false,success:function(form){
			var res = Ext.decode(form.responseText);	
			var obj = Ext.decode(res.tableConfig);
			var yearList = res.yearList;
			obj.beforeBuildComp = function(grid){
				
				//得到当前表格的列
				var cols = grid.tableConfig.columns;
				for(var i in cols){
					cols[i].menuDisabled=true;
				}
				var yearStore = Ext.create("Ext.data.Store",{
					id:'yearList',
					field:["name","value"],
					data:yearList,
				});
				var curYear = new Date().getFullYear()+"";
				var combox = {};
				combox.id="kqYearCombo";
				combox.width=80;
				combox.height=20;
				combox.style="margin-bottom:5px;";
				combox.value=curYear;
				combox.xtype="combo";
				combox.store=yearStore;
				combox.displayField = "name";
				combox.valueField = "value";
				combox.listeners={
						select:function(combox,record){
							var map = new HashMap();
							var json = {"type":"pclist","kq_year":record.get("value")};
							map.put("jsonStr", Ext.encode(json));
							Rpc({functionId:'KQ00020301',async:false,success:function(form){
								var store = Ext.data.StoreManager.lookup("period_dataStore");
								store.load();
							}},map);
						}
				}
				grid.toolConfig.items[3]=combox;
			}
			this.tableObj  = new BuildTableObj(obj);
			var pagingtool = Ext.getCmp("period_pagingtool");
			if(pagingtool){
				pagingtool.setHidden(true);
			}
		},scope:this},map);
	},
	/**
	 * 新建考勤期间页面
	 */
	addPeriod:function(model){
		var kq_year = Ext.getCmp("kqYearCombo").getValue();
		if(model=="1" || model=="2"){
			if(model=="1"){
				//同上一年度
				var priv_kq_year = (parseInt(kq_year)-1)+"";
				if(!this.checkHasPrivPreiod(priv_kq_year)){
					Ext.showAlert(kq.period.error.nopreriod);
					return;
				}
			}
			var json = {"type":"create","model":model,"kq_year":kq_year};
			var map = new HashMap();
			map.put("jsonStr",Ext.encode(json));
			Rpc({functionId:'KQ00020301',async:false,success:function(form){
				var response = Ext.decode(form.responseText);
				var jsonObj = Ext.decode(response.returnStr);
				if(jsonObj.return_code=="success"){
					var store = Ext.data.StoreManager.lookup("period_dataStore");
					store.load();
				}else{
					Ext.showAlert(jsonObj.return_msg);
					return;
				}
			},scope:this},map);
		}else if(model=="3"){
			//指定期间
			this.addPeriod3View(kq_year);
		}
	},
	addPeriod3View:function(kq_year){
		Ext.create("Ext.window.Window",{
			id:'createPeriodWin',
			title:kq.period.create+kq.period.name,
			width:350,
			modal:true,
			height:210,
			layout:"vbox",
			bbar: [
				   '->',
				  { xtype: 'button', text: kq.button.ok,width:75,height:22,handler:function(){
					   var form = Ext.getCmp("dateform").getForm();
					   if(!form.isValid()){
						  return;
					   }
					   var json = form.getValues();
					   json.type="create";
					   json.model="3"
					   json.privios_month = !json.privios_month?"2":json.privios_month
					   var map = new HashMap(); 
					   map.put("jsonStr", Ext.encode(json));
					   Rpc({functionId:'KQ00020301',async:false,success:function(form){
							var response = Ext.decode(form.responseText);
							var jsonObj = Ext.decode(response.returnStr);
							if(jsonObj.return_code=="success"){
								Ext.getCmp("createPeriodWin").close();
								var store = Ext.data.StoreManager.lookup("period_dataStore");
								store.load();
							}else{
								Ext.showAlert(jsonObj.return_msg);
								return;
							}
						},scope:this},map);
					  
				  } },
				  { xtype: 'button', text: kq.button.cancle,width:75,height:22,handler:function(){
					 Ext.getCmp("createPeriodWin").destroy();
				  }},
				  '->'
				],
			items:[
				{
					xtype:'form',
					id:'dateform',
					border:false,
					items:[{
						xtype:'hiddenfield',
						name:'kq_year',
						value:kq_year
					},{
						xtype:'numberfield',
						margin:'20 0 0 60',
						fieldLabel:kq.period.startmonth,
						name:'start_month',
						minValue:1,
						maxValue:12,
						allowBlank:false,
						width:180,
						labelAlign:'right'
					},{
						xtype:'numberfield',
						margin:'10 0 0 60',
						fieldLabel:kq.period.startday,
						allowBlank:false,
						name:'start_day',
						minValue:1,
						maxValue:31,
						width:180,
						labelAlign:'right'
					},{
						xtype:'checkbox',
						width:180,
						name:'privios_month',
						margin:'10 0 0 88',
						boxLabel :kq.period.fromprivmonth,
						boxLabelAlign:'after',
						inputValue:'1'
					}]
				}
			]
		}).show();
	},
	/**
	 * 校验是否有上年度考勤期间
	 */
	checkHasPrivPreiod:function(kq_year){
		var flag = false;
		var json = {"type":"checkHasPeriod","kq_year":kq_year};
		var map = new HashMap(); 
		map.put("jsonStr", Ext.encode(json));
		Rpc({functionId:'KQ00020301',async:false,success:function(form){
			var response = Ext.decode(form.responseText);
			var jsonObj = Ext.decode(response.returnStr);
			if(jsonObj.return_code=="fail"){
				Ext.showAlert(jsonObj.return_msg);
				return;
			}
			flag = jsonObj.flag;
		},scope:this},map);
		return flag;
	},
	/**
	 * 删除期间
	 */
	deletePeriod:function(){
		var selectData = config_period_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selectData.length == 0){
			Ext.showAlert(kq.period.confirm.selectdelrows);
			return ;
		}
		Ext.showConfirm(kq.period.confirm.askfordel,function(flag){
			if(flag!="yes")
				return;
			var map = new HashMap();
			var kq_year;
			var kq_durations = [];
			for(var i=0;i<selectData.length;i++){
				var record = selectData[i];
				if(i==0){
					kq_year = record.get("kq_year");
				}	
				var kq_duration = record.get("kq_duration");
				kq_durations.push(kq_duration);
			}
			var json = {"type":"delete","kq_year":kq_year,"kq_durations":kq_durations};
			map.put("jsonStr", Ext.encode(json));
			Rpc({functionId:'KQ00020301',async:false,success:function(form){
				var response = Ext.decode(form.responseText);
				var jsonObj = Ext.decode(response.returnStr);
				if(jsonObj.return_code=="success"){
					var store = Ext.data.StoreManager.lookup("period_dataStore");
					store.load();
				}else{
					Ext.showAlert(jsonObj.return_msg);
					return;
				}
			},scope:this},map);
		});
	}
});