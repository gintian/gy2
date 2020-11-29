/**
 * hej add 2015/12/12
 * 盒式报表添加修改
 */
Ext.define("BoxReportURL.AddBoxReport", {
	extend:"Ext.panel.Panel",
	tablepanel:undefined,
	viewport:undefined,
	tablestore:undefined,
	flag:0,//新增与编辑标识
	editcasseid:'',
	savecasseid:'',
	status:2,//新增还未保存，初始状态
	/**
	 * 初始化组件
	 */
	initComponent:function() {
		this.callParent();
		this.createMainPanel();
	},
	/**
	 * 开始创建
	 */
	createMainPanel:function() {
		var me = this;
		if(me.flag==0){//新增
			//设置布局方式
			me.setLayout(Ext.create("Ext.layout.container.HBox",{align:'middle',pack:'center'}));
			me.setAutoScroll(true);
			me.setTitle("新增盒式报表");
			var toolbar = me.createToolbal();
			me.addDocked(toolbar);
			me.add(me.createPanel());
		}
		if(me.flag==1){//修改
			//设置布局方式
			me.setLayout(Ext.create("Ext.layout.container.HBox",{align:'middle',pack:'center'}));
			me.setAutoScroll(true);
			me.setTitle("编辑盒式报表");
			var toolbar = me.createToolbal();
			me.addDocked(toolbar);
			me.add(me.createPanel());
			var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];
			
			var vo = new HashMap();
			vo.put('cassetteid',me.editcasseid);
			vo.put('flag','1');
			Rpc({functionId:'ZJ100000115',success:function(res){
				var resultObj = Ext.decode(res.responseText);
				var editmap = resultObj.editmap;
				var cassette_name = editmap.cassette_name;
				Ext.getCmp('cassettename').setValue(cassette_name);
				var percentage = editmap.percentage;
				if(percentage=='1'){
					Ext.getCmp('percentage').setValue(true);
				}
				//数据源赋值
				var data_source = editmap.data_source;
				Ext.getCmp('datasource').setValue(data_source);
				//横坐标描述赋值
				var lateral_desc = editmap.lateral_desc;
				Ext.getCmp('lateral_desc').setValue(lateral_desc);
				//纵坐标描述赋值
				var longitudinal_desc = editmap.longitudinal_desc;
				Ext.getCmp('longitudinal_desc').setValue(longitudinal_desc);
				
				var staff_view_url = editmap.staff_view_url;
				Ext.getCmp('staff_view_url').setValue(staff_view_url);
				
				var staff_listview_url = editmap.staff_listview_url;
				Ext.getCmp('staff_listview_url').setValue(staff_listview_url);
				
				var lateral_index = Ext.ComponentQuery.query("combo[flag='lateral_index']")[0];//横坐标
				var longitudinal_index = Ext.ComponentQuery.query("combo[flag='longitudinal_index']")[0];//纵坐标
				var time_dimension = Ext.ComponentQuery.query("combo[flag='time_dimension']")[0];
				var codeindex = editmap.indexlist;
				var codecomitems = [];
				var datecomitems = [];
				if(codeindex!=""){
					for(var i=0;i<codeindex.length;i++){
					var codemap = codeindex[i];
					if(codemap.itemtype=='A'){
						var datas= {"abbr":codemap.itemid, "nameab":codemap.itemdesc ,"number":codemap.number};
						codecomitems.push(datas);
					}
					if(codemap.itemtype=='D'){
						var datas= {"abbr":codemap.itemid, "nameab":codemap.itemdesc };
						datecomitems.push(datas);
					}
				}
				var codestore = Ext.create('Ext.data.Store', {fields: ['abbr', 'nameab'],data :codecomitems});
				var datestore = Ext.create('Ext.data.Store', {fields: ['abbr', 'nameab'],data :datecomitems});
				lateral_index.setStore(codestore);
//				lateral_index.bindStore(codestore,true);
				longitudinal_index.setStore(codestore);
//				longitudinal_index.bindStore(codestore,true);
				time_dimension.setStore(datestore);
//				time_dimension.bindStore(datestore,true);
				}
				//横向指标赋值
				var lateral = editmap.lateral_index;
				var latitemid = lateral.itemid;
				if(lateral.number==0){
					lateral_index.setValue('');
				}else{
					lateral_index.setValue(latitemid);
				}
				Ext.getCmp('lateral_desc').maxLength=lateral.number*12;
				//纵向指标赋值
				var longitudinal = editmap.longitudinal_index;
				var longitemid = longitudinal.itemid;
				if(longitudinal.number==0){
					longitudinal_index.setValue('');
				}else{
					longitudinal_index.setValue(longitemid);
				}
				Ext.getCmp('longitudinal_desc').maxLength=longitudinal.number*7;
				//时间维度指标赋值
				var dimension = editmap.time_dimension;
				var isempty = me.isEmptyObject(dimension);
				if(isempty==false){
					var dimeitemid = dimension.itemid;
					var dimeitemlength = dimension.itemlength;
					time_dimension.setValue(dimeitemid);
					if(dimeitemlength=='4'){//年
		    		}
		    		else if(dimeitemlength=='7'){//年月
		    			Ext.getCmp('month').setVisible(true);
		    		}
		    		else{
		    			Ext.getCmp('season').setVisible(true);
		    			Ext.getCmp('month').setVisible(true);
		    		}
					Ext.getCmp('analysis_interval').setVisible(true);
				}
				
				//分析区间赋值
				var analysis_interval = editmap.analysis_interval;
				var analysisarry = analysis_interval.split(',');
				for(var i=0;i<analysisarry.length;i++){
					var value = analysisarry[i];
					if(value==1){//年
						Ext.getCmp('year').setValue(true);
					}
					if(value==2){//季
						Ext.getCmp('season').setValue(true);
					}
					if(value==3){//月
						Ext.getCmp('month').setValue(true);
					}
				}
				//人员范围赋值
				var pgstore = panelgrid.getStore();
				var personnel_range = editmap.personnel_range;
				var pcomitems = [];
				for(var j=0;j<personnel_range.length;j++){
					var pcodemap = personnel_range[j];
					var pid = pcodemap.id;
					var pname = pcodemap.name;
				    var pdatas= {"id":pid, "name":pname};
					pcomitems.push(pdatas);
				}
				var pstore = Ext.create('Ext.data.Store', {fields: ['id', 'name'],data :pcomitems});
				var pitems = pstore.data.items;
				for(var k=0;k<pitems.length;k++){
					var precord = pitems[k];
					pgstore.insert(k,precord);
				}
			},scope:this},vo);
			
		}
	},
	/**
	 * 创建上部按钮
	 * 
	 * @return {}
	 */
	createToolbal:function() {
		var me = this;
		var toolbar = Ext.widget('toolbar', {
					dock:'top',
					items:[{
								xtype:'button',
								text:'保存',
								handler:function() {
									me.saveBoxReport(me.flag,'S');
								}
							}, {
								xtype:'button',
								text:'运行',
								handler:function() {
									var flag = true;
									if(me.status==2){
										flag = me.saveBoxReport(me.flag,'Y');
									}
									if(flag==false){
									}else{
										if(me.flag==0){
											me.runBoxReport(me.savecasseid,'Y');
										}
										if(me.flag==1){
											me.saveBoxReport(me.flag,'Y');
											me.runBoxReport(me.editcasseid,'Y');
										}
									}
								}
							}, {
								xtype:'button',
								text:'返回',
								handler:function() {
									var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];
									panelgrid.store.removeAll();
									me.viewport.removeAll(true);
									me.viewport.add(me.tablepanel);
									me.tablestore.reload();
								}
							}]
				});
		return toolbar;
	},
	/**
	 * 创建主体部分
	 */
	createPanel:function() {
		var me = this;
		var mainPanel = {xtype:'container',items:[{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">报表名称</div>',padding:'0 10 0 12',width:90
						}, {xtype:'textfield',width:400,height:22,padding:'0 5 0 24',id:'cassettename'
						}, {xtype:'label',text:'*',style:'color:red;'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">数据来源</div>',padding:'0 10 0 12',width:90
						}, {xtype:'textfield',width:400,height:22,padding:'0 5 0 24',id:'datasource',listeners:{
												render: function(c) {  
													var tip = Ext.create('Ext.tip.ToolTip', {
    														target: c.getId(),
    														shadow:false,
    														bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
    														html: '提示人员视图必须包含人员编号(A0100)、人员排序号(A0000)、人员姓名(A0101)、单位名称(B0110)、部门(E0122)、岗位名称(E01A1)几个字段!'
														});  
        										},blur:function(nf, newv, oldv) {
												me.setStore(nf.getValue());
												            }
												        }
						}, {xtype:'label',html:'<font style="color:red;">*</font>'+'(人员视图)'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">横向指标</div>',padding:'0 10 0 12',width:90
						}, {xtype:'combo',queryMode: 'local',store:'',width:400,editable:false,height:22,
							padding:'0 5 0 24',emptyText:'请选择',flag:'lateral_index', multiSelect:false,
							displayField: 'nameab',valueField: 'abbr',listeners:{
								'select':function(combo,record,index){
									var hi = this;
									var hvalue = Ext.ComponentQuery.query("combo[flag='longitudinal_index']")[0].getValue();
									var lateral_desc = Ext.getCmp("lateral_desc");
									if(hvalue==record.data.abbr){
										Ext.Msg.alert('提示','纵向指标已经选择此指标！');
										hi.setValue('');
										return;
									}else{
										lateral_desc.maxLength = record.data.number*12;
									}
								}
							}
						}, {xtype:'label',text:'*',style:'color:red;'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">横坐标描述</div>',padding:'0 10 0 0',width:90
						}, {xtype:'textfield',width:400,padding:'0 5 0 24',id:'lateral_desc',height:22,
							listeners:{'change':function(obj, e){
								var value = obj.getValue();
									if(value.indexOf(" ")!=-1){
										value = value.replace(" ","");
										obj.setValue(value);
									}
							}}
						}, {xtype:'label',text:'*',style:'color:red;'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">纵向指标</div>',padding:'0 10 0 12',width:90
						}, {xtype:'combo',queryMode: 'local',store:'',width:400,editable:false,multiSelect:false,
							padding:'0 5 0 24',emptyText:'请选择',flag:'longitudinal_index', height:22,
							displayField: 'nameab',valueField: 'abbr',listeners:{
								'select':function(combo,record,index){
									var hi = this;
									var hvalue = Ext.ComponentQuery.query("combo[flag='lateral_index']")[0].getValue();
									var longitudinal_desc = Ext.getCmp("longitudinal_desc");
									if(hvalue==record.data.abbr){
										Ext.Msg.alert('提示','横向指标已经选择此指标！');
										hi.setValue('');
										return;
									}else{
										longitudinal_desc.maxLength = record.data.number*7;
									}
								}
							}
						}, {xtype:'label',text:'*',style:'color:red;'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">纵坐标描述</div>',padding:'0 10 0 0',width:90
						}, {xtype:'textfield',width:400,padding:'0 5 0 24',id:'longitudinal_desc',height:22,
							listeners:{'change':function(obj, e){
									var value = obj.getValue();
										if(value.indexOf(" ")){
											value = value.replace(" ","");
											obj.setValue(value);
										}
								}}
						}, {xtype:'label',text:'*',style:'color:red;'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'时间维度指标',padding:'0 0 0 0',width:100
						}, {xtype:'combo',queryMode: 'local',store:'',width:400,editable:false,
							padding:'0 5 0 14',emptyText:'请选择',flag:'time_dimension', itemlength:'',multiSelect:false,height:22,
							displayField: 'nameab',valueField: 'abbr',
							listeners:{'select':function(combo,record,index){
						    	var hi=this;
						    	if(hi.getValue()!='请选择'||hi.getValue()!=''){
						    		if(this.itemlength=='4'){//年
						    		}
						    		else if(this.itemlength=='7'){//年月
						    			Ext.getCmp('month').setVisible(true);
						    		}
						    		else{
						    			Ext.getCmp('season').setVisible(true);
						    			Ext.getCmp('month').setVisible(true);
						    		}
						    		Ext.getCmp('analysis_interval').setVisible(true);
						    	}
						    	else{
						    		Ext.getCmp('analysis_interval').setVisible(false);
						    	}
						    }}
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				hidden:true,
				id:'analysis_interval',
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">分析区间</div>',padding:'0 10 0 10',width:90
						}, {xtype:'container',layout:'hbox',padding:'0 5 0 24',items:[
							{xtype:'checkbox',padding:'0 20 0 0',boxLabel:'年',id:'year',index: '1',checked:true,hidden:false},  
								{xtype:'checkbox',padding:'0 20 0 0',boxLabel:'季',id:'season',index: '2',hidden:true}, 
								{xtype:'checkbox',padding:'0 20 0 0',boxLabel:'月',id:'month',index: '3',hidden:true}
								]}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',html:'<div style="text-indent:1em">人员范围</div>',padding:'0 10 0 11',width:90
						}, {xtype:'container',
							layout:'hbox',
							padding:'0 5 0 24',
							items:[{xtype:'gridpanel',
									flag:'panelgrid',height: 200,width:400,margin:'-30 0 0 0',
									viewConfig:{  
										forceFit :true 
										}, 
									columns:[{dataIndex: 'id',resizable:false,menuDisabled:true,sortable:false,hidden:true},
											{dataIndex: 'name',flex:1,resizable:false,menuDisabled:true,sortable:false}],
									selModel: {selType: 'checkboxmodel',mode: "SIMPLE"},
									},
									{xtype:'container',padding:'0 0 0 10',layout:'vbox',
									items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/add.png',
									title:'添加人员范围条件',height:25,width:25,margin:'0 0 20 0',style:{cursor:'pointer'},
									listeners:{
										render:function() {
											this.getEl().on('click', function() {
												me.showpersonWin();
											})
											}}},{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/cancel.png',
											title:'删除人员范围条件',height:25,width:25,style:{cursor:'pointer'},
											listeners:{
												render:function() {
													this.getEl().on('click', function() {
														me.cancelGridpanel();
													})
													}}}]}]}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:1,
				items:[{xtype:'label',text:'个人信息URL',padding:'0 10 0 0',width:90
						}, {xtype:'textfield',width:400,height:22,padding:'0 5 0 24',id:'staff_view_url',value:'/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&inforkind=1&tabid=1&multi_cards=-1&userpriv=nopriv&flag=nopriv'
						}]
			},{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				hidden:true,
				items:[{xtype:'label',text:'人员列表URL',padding:'0 10 0 0',width:90
						}, {xtype:'textfield',width:400,padding:'0 5 0 24',id:'staff_listview_url',height:22
						}]
			},
			{
				xtype:'container',
				padding:'10 0 10 0',
				layout:'hbox',
				width:600,
				border:0,
				items:[{xtype:'label',text:'',padding:'0 10 0 0',width:90
						}, {xtype:'checkbox',padding:'0 20 0 24',boxLabel:'单元格显示占比',id:'percentage'} ]
			}
		]};
		return mainPanel;
	},
	/**
	 * 给下拉赋值
	 * @param {} data
	 */
	setStore:function(data){
		if(data==undefined||data==''){
            return;
        }
		var me = this;
		var lateral_index = Ext.ComponentQuery.query("combo[flag='lateral_index']")[0];//横坐标
		var longitudinal_index = Ext.ComponentQuery.query("combo[flag='longitudinal_index']")[0];//纵坐标
		var time_dimension = Ext.ComponentQuery.query("combo[flag='time_dimension']")[0];
		var analysis_interval = Ext.getCmp('analysis_interval');
		var vo = new HashMap();
		vo.put('datasource',data);
		vo.put('flag','0');
		Rpc({functionId:'ZJ100000112',success:function(res){
			var resultObj = Ext.decode(res.responseText);
			var codeindex = resultObj.codeindex;
			var isempty = me.isEmptyObject(codeindex);
			if(isempty==false){
				var codecomitems = [];
				var datecomitems = [];
				if(codeindex!=""){
					for(var i=0;i<codeindex.length;i++){
					var codemap = codeindex[i];
					if(codemap.itemtype=='A'){
						var datas= {"abbr":codemap.itemid, "nameab":codemap.itemdesc ,"number":codemap.number};
						codecomitems.push(datas);
					}
					if(codemap.itemtype=='D'){
						var itemlength = codemap.itemlength;
						time_dimension.itemlength = itemlength;
						var datas= {"abbr":codemap.itemid, "nameab":codemap.itemdesc};
						datecomitems.push(datas);
					}
				}
				var codestore = Ext.create('Ext.data.Store', {fields: ['abbr', 'nameab'],data :codecomitems});
				var datestore = Ext.create('Ext.data.Store', {fields: ['abbr', 'nameab'],data :datecomitems});
				
				lateral_index.setStore(codestore);
				lateral_index.bindStore(codestore,true);
				longitudinal_index.setStore(codestore);
				longitudinal_index.bindStore(codestore,true);
				time_dimension.setStore(datestore);
				time_dimension.bindStore(datestore,true);
			}
			}
			else{
				Ext.Msg.alert('提示信息','该视图不存在或无效！');
				lateral_index.setValue('');
				longitudinal_index.setValue('');
				time_dimension.setValue('');
				analysis_interval.setVisible(false);
				Ext.getCmp("datasource").setValue("");
				var store = Ext.create('Ext.data.Store', {fields: ['abbr', 'nameab'],data :[{"abbr":'', "nameab":''}]});
				lateral_index.setStore(store);
				lateral_index.bindStore(store,true);
				longitudinal_index.setStore(store);
				longitudinal_index.bindStore(store,true);
				time_dimension.setStore(store);
				time_dimension.bindStore(store,true);
			}
		},scope:this},vo);
	},
	/**
	 *人员范围选择窗体
	 */
	showpersonWin:function(){
		var me = this;
		var vo = new HashMap();
		me.addindexof();
		vo.put('flag','1');
		Rpc({functionId:'ZJ100000112',success:function(res){
			var resultObj = Ext.decode(res.responseText);
			var condition = resultObj.condition;
			var comitems = [];
			if(condition!=""){
				for(var i=0;i<condition.length;i++){
				var codemap = condition[i];
			    var datas= {"id":codemap.id, "name":codemap.name};
				comitems.push(datas);
			}
			}
			var store = Ext.create('Ext.data.Store', {fields: ['id', 'name'],data :comitems});
			var wingrid = Ext.widget('gridpanel',{
				xtype:'gridpanel',
				flag:'wingrid',store:store,forceFit:true,
				columns: [
					{dataIndex: 'id',resizable:false,menuDisabled:true,sortable:false,hidden:true},
					{dataIndex: 'name',flex:1,resizable:false,menuDisabled:true,sortable:false}],
					height: 280,width: 450,margin:'20 20 0 20',
					selModel: {selType: 'checkboxmodel',mode: "SIMPLE"}}
			);
			
			var personalWin = Ext.widget('window',{
				id:'personalWin',
				title:'常用统计条件',
				resizable:false,
				modal:true,
				width:500,
				height:400,
				items:[wingrid],
				buttonAlign:'center',
				buttons: [{text:'确定',margin:'0 10 10 0',handler:function() {
					var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];
					var wingridpanel = Ext.ComponentQuery.query("gridpanel[flag='wingrid']")[0];
					var records=wingridpanel.getSelectionModel().getSelection();
					var comitems = [];
					if(records.length>=0){
						panelgrid.store.removeAll();
						for(var i=0;i<records.length;i++){
							var record = records[i];
							panelgrid.store.insert(i,record);
						}
					}
					Ext.getCmp('personalWin').close();
								}},{text:'取消',margin:'0 10 10 0',handler:function() {
									Ext.getCmp('personalWin').close();
								}}]
		}).show();
		wingrid.query("headercontainer")[0].hide();
		
		var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];
		var wingridpanel = Ext.ComponentQuery.query("gridpanel[flag='wingrid']")[0];
		var ids = '';
		panelgrid.getStore().each(function(r){
		   ids+=r.get('id')+',';
		});
		var selected = [];
		wingridpanel.getStore().each(function(r){
		   if(ids.indexOf(r.get('id')+",")>-1)
		   selected.push(r);
		      
		});
		var model = wingridpanel.getSelectionModel();
		model.select(selected);
		
		},scope:this},vo);
	},
	/**
	 * 保存盒式报表配置
	 * @param {} flag
	 * @param {} cassid
	 */
	saveBoxReport:function(flag,btnflag){
		var me= this;
		var cassid='';
		if(flag==0){
			cassid = me.savecasseid;
		}
		if(flag==1){
			cassid = me.editcasseid;
		}
		if(btnflag=='Y'){
			status = '1';
		}
		if(btnflag=='S'){
			status = '0';
		}
		var cassettename = Ext.getCmp('cassettename').getValue();
		var datasource = Ext.getCmp('datasource').getValue();
		var lateral_index = Ext.ComponentQuery.query("combo[flag='lateral_index']")[0].getValue();
		if(lateral_index==null||lateral_index=='请选择'){
			lateral_index='';
		}
		var lateral_desc = Ext.getCmp('lateral_desc').getValue();
		var longitudinal_index = Ext.ComponentQuery.query("combo[flag='longitudinal_index']")[0].getValue();
		if(longitudinal_index==null||longitudinal_index=='请选择'){
			longitudinal_index='';
		}
		var longitudinal_desc = Ext.getCmp('longitudinal_desc').getValue();
		var time_dimension = Ext.ComponentQuery.query("combo[flag='time_dimension']")[0].getValue();
		if(time_dimension==null||time_dimension=='请选择'){
			time_dimension='';
		}
		var analysis_interval = '';
		if(time_dimension!=''&&time_dimension!=null){
			var year = Ext.getCmp('year').getValue();
			if(year==true){
				analysis_interval+=Ext.getCmp('year').index+',';
			}
			var season = Ext.getCmp('season').getValue();
			if(season==true){
				analysis_interval+=Ext.getCmp('season').index+',';
			}
			var month = Ext.getCmp('month').getValue();
			if(month==true){
				analysis_interval+=Ext.getCmp('month').index+',';
			}
			if(analysis_interval!=''){
				analysis_interval = analysis_interval.substring(0,analysis_interval.length-1);
			}
		}
		me.analysis_interval = analysis_interval;
		var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];//表格对象
		var personnel_range = "";
		var records=panelgrid.store.data.items;
		for(var i=0;i<records.length;i++){
					var record = records[i];
					personnel_range+=record.data.id+',';
		}
		if(personnel_range!=''){
			personnel_range = personnel_range.substring(0,personnel_range.length-1);
		}
		var percentagecheck = "0";
		var percentage = Ext.getCmp('percentage').getValue();
		if(percentage==true){
			percentagecheck = "1";
		}
		var staff_view_url = Ext.getCmp('staff_view_url').getValue();
		var staff_listview_url = Ext.getCmp('staff_listview_url').getValue();
		if(cassettename==''||datasource==''||lateral_index==''||lateral_desc==''||longitudinal_index==''||longitudinal_desc==''){
			Ext.Msg.alert('提示信息','您有必填项没有填写！');
			return false;
			return;
		}
		var vo = new HashMap();
		vo.put('flag',flag);
		vo.put('cassid',cassid);
		vo.put('status',status);
		vo.put('cassettename',cassettename);
		vo.put('datasource',datasource);
		vo.put('lateral_index',lateral_index);
		vo.put('lateral_desc',lateral_desc);
		vo.put('longitudinal_index',longitudinal_index);
		vo.put('longitudinal_desc',longitudinal_desc);
		vo.put('time_dimension',time_dimension);
		vo.put('analysis_interval',analysis_interval);
		vo.put('personnel_range',personnel_range);
		vo.put('percentage',percentagecheck);
		vo.put('staff_view_url',staff_view_url);
		vo.put('staff_listview_url',staff_listview_url);
		Rpc({functionId:'ZJ100000113',async:false,success:function(res){
			var resultObj = Ext.decode(res.responseText);
			if(resultObj.result!=undefined && !resultObj.result){
			Ext.Msg.alert('提示信息',"保存失败！");
			return;
			}
			if(flag==0){
				me.savecasseid = resultObj.cassette_id;				
				me.status = resultObj.status;
			}
			if(btnflag=='S'){
				me.savecasseid = resultObj.cassette_id;				
				me.status = resultObj.status;
				Ext.Msg.alert('提示信息',"保存成功！");
			}
			},scope:this},vo);
	},
	/**
	 * 去除选择的人员范围条件
	 */
	cancelGridpanel:function(){
		var me = this;
		var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];
		var records=panelgrid.getSelectionModel().getSelection();
	    if(records.length<=0){
	    	//弹框提醒
	    	Ext.Msg.alert('提示信息','请选择要删除的人员范围条件！');
	    }else{
	    	var store = panelgrid.store;
	    	store.remove(records);
	    }
	},
	/**
	 * 运行盒式报表
	 */
	runBoxReport:function(casseid,btnflag){
		var me = this;
		var panelgrid = Ext.ComponentQuery.query("gridpanel[flag='panelgrid']")[0];
		//panelgrid.store.removeAll();
		var configPanel = Ext.require('BoxReportURL.SodukuItem', function(){
		var configPanel = Ext.create("BoxReportURL.SodukuItem",{casseid:casseid+'',enterflag:'working',analysis_interval:me.analysis_interval,
		viewport:me.viewport,
		tablepanel:me.tablepanel,
		tablestore:me.tablestore});
		me.viewport.removeAll(true);
		me.viewport.add(configPanel);
	 });
	},
	//解决ie数组indexof报错问题
	addindexof:function(){
		if (!Array.prototype.indexOf)
			{
			  Array.prototype.indexOf = function(elt /*, from*/)
			  {
			    var len = this.length >>> 0;
			    var from = Number(arguments[1]) || 0;
			    from = (from < 0)
			         ? Math.ceil(from)
			         : Math.floor(from);
			    if (from < 0)
			      from += len;
			    for (; from < len; from++)
			    {
			      if (from in this &&
			          this[from] === elt)
			        return from;
			    }
			    return -1;
			  };
			}
	},
	isEmptyObject: function(obj) { 
		
		for ( var name in obj ) { 
		return false; 
		} 
		return true; 
	} 
});