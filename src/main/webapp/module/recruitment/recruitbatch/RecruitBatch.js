
Ext.define('RecruitbatchUL.RecruitBatch',{
	recruit_batch:'',
	cookie:'',
	tableObj:'',
	constructor:function(config) {
		labelflag = 'label1';//查询方案选中值，用于导出excel
		recruit_batch=this;
		flagPriv=3;
        recruit_batch.init();
        list=new Array();
        lengthList=new Array();
        person=new Array(); 
        recruit_batch.bususer=[]; 
	},
	init:function(){
		 var map = new HashMap();
		 map.put('z0129','00');
		 Rpc({functionId:'ZP0000002531',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					recruit_batch.createTableOK(result,form,action);
				}else{
					Ext.showAlert(result.message);
				}
			}},map);
	},
	createTableOK:function(result,form,action){
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		tableObj = new BuildTableObj(obj);
		var map = new HashMap();
		 var value = '00';
		 if(recruit_batch.labelflag=='label1'){
		 	value='00';
		 }
		 if(recruit_batch.labelflag=='label2'){
		 	value='01';
		 }
		 if(recruit_batch.labelflag=='label3'){
		 	value='04';
		 }
		 if(recruit_batch.labelflag=='label4'){
		 	value='06';
		 }
		 map.put('z0129',getEncodeStr(value));
		 map.put('type','1');
		 var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
			border:0,
			id:"toolbar",
			dock:'top',
			items:[{
					xtype:'label',
					text: '查询方案：',
					style:'margin-left:5px',
				},{
					xtype:'label',
					html:'<a href="javascript:recruit_batch.searchStatus(label1.id)">全部</a> ',
					id:'label1',
					style:'margin-left:5px;margin-right:10px;',
					cls:'scheme-selected-cls'
				},'-',{
					xtype:'label',
					id:'label2',
					html:'<a href="javascript:recruit_batch.searchStatus(label2.id)">起草</a> ',
					style:'margin-left:5px;margin-right:5px;'
				},{
					xtype:'label',
					id:'label3',
					html:'<a href="javascript:recruit_batch.searchStatus(label3.id)">已发布</a> ',
					style:'margin-left:5px;margin-right:5px;'
				},{
					xtype:'label',
					id:'label4',
					html:'<a href="javascript:recruit_batch.searchStatus(label4.id)">结束</a> ',
					style:'margin-left:5px;margin-right:5px;'
				}]
		});
		tableObj.insertItem(toolbar,0);
	},
	//查询方案
	searchStatus:function(id){
		for(var i=1;i<=4;i++){
			Ext.getCmp('label'+i).removeCls('scheme-selected-cls');
		}
		Ext.getCmp(id).addCls('scheme-selected-cls');
		recruit_batch.labelflag = id;
		 var map = new HashMap();
		 var value = '00';
		 if(id=='label1'){
		 	value='00';
		 }
		 if(id=='label2'){
		 	value='01';
		 }
		 if(id=='label3'){
		 	value='04';
		 }
		 if(id=='label4'){
		 	value='06';
		 }
		 map.put('z0129',getEncodeStr(value));
		 Rpc({functionId:'ZP0000002531',success: function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
	},
	//创建招聘批次
	addRecruitBatch:function(){
		 var map = new HashMap();
		 map.put('type','1');
		 Rpc({functionId:'ZP0000002532',success: recruit_batch.addRecruitBatchOK},map);
	},
	//双击修改招聘批次
	modifyRecruitBatch:function(){
		var grid = Ext.getCmp('recruitbatch001_tablePanel').getSelectionModel().getSelection();
		var id=grid[0].get("z0101");
		var flag=grid[0].get("flag");
	    var map = new HashMap();
	    map.put('type','2');
	    map.put('id',id);
	    Rpc({functionId:'ZP0000002532',success: recruit_batch.addRecruitBatchOK},map);
	},
	//点击批次名称进入修改页面
	toModifyRecruitBatch:function(value,metaData,Record){
		var id = Record.data.z0101;
		var flag = Record.data.flag;
		return "<a onclick='recruit_batch.toModifyRecruitBatchOK("+id+","+flag+");' href='javascript:void(0);' >"+value+"</a>";
	},
	//点击批次名称进入修改页面
	toModifyRecruitBatchOK:function(id,flag){
		flagPriv=flag;
		var map = new HashMap();
	    map.put('type','2');
	    map.put('id',id+'');
		Rpc({functionId:'ZP0000002532',success: recruit_batch.addRecruitBatchOK},map);
	},
	//创建或修改招聘批次页面
	addRecruitBatchOK:function(response){
		var value = response.responseText;
		var map = Ext.decode(value);
		var id = map.id;
		var type = map.type;
		recruit_batch.fields = map.fields;
		recruit_batch.list = map.list;
		
		if(type=='2')
			recruit_batch.lengthList = recruit_batch.list[recruit_batch.list.length-1];
		else
			recruit_batch.lengthList = recruit_batch.list;
		
		//招聘时间范围时间控件格式
		var RecruitmentStart = recruit_batch.getFormat(recruit_batch.lengthList[0]);
		var RecruitmentEnd = recruit_batch.getFormat(recruit_batch.lengthList[1]);
		//报名时间范围时间控件格式
		if(Ext.isEmpty(recruit_batch.lengthList[2]) && Ext.isEmpty(recruit_batch.lengthList[3])){
			var applyStart = recruit_batch.getFormat(recruit_batch.lengthList[0]);
			var applyEnd = recruit_batch.getFormat(recruit_batch.lengthList[1]);
		}else{
			var applyStart = recruit_batch.getFormat(recruit_batch.lengthList[2]);
			var applyEnd = recruit_batch.getFormat(recruit_batch.lengthList[3]);
		}
		//打印准考证时间控件格式
		var printTicketStart = recruit_batch.getFormat(recruit_batch.lengthList[4]);
		var printTicketEnd = recruit_batch.getFormat(recruit_batch.lengthList[5]);
		//查看成绩时间控件格式
		var ViewResultStart = recruit_batch.getFormat(recruit_batch.lengthList[6]);
		var ViewResultEnd = recruit_batch.getFormat(recruit_batch.lengthList[7]);
		var priv=true;
		if(type=='2'){
			priv = map.priv;
		}
		Ext.util.CSS.createStyleSheet(".x-form-field-my{border:0;}","underline");
		Ext.util.CSS.createStyleSheet(".x-form-field{border:1px;}","underline");
		 //招聘渠道的store，需要从后台查询。
		 var channelStore = Ext.create('Ext.data.Store',{
			 	fields:['itemid','itemdesc'],
				proxy:{
					type:'transaction',
					functionId:'ZP0000002533',
					extraParams:{
						type:'1'
					},
					reader: {
						 type: 'json',
						root: 'data'         	
					 }
				},
				autoLoad: true
		});
		channelStore.load();
		//招聘流程的store，需要从后台查询。
		 var flowStore = Ext.create('Ext.data.Store',{
			 	fields:['itemid','itemdesc'],
				proxy:{
					type:'transaction',
					functionId:'ZP0000002533',
					extraParams:{
						type:'2'
					},
					reader: {
						 type: 'json',
						 root: 'data'         	
					 }
				},
				autoLoad: true
		});
		flowStore.load();
		//tableObj.getMainPanel().removeAll(true);
		var width1 = document.body.clientWidth;
		var widht11=width1*0.1;
		var width = 1300;
		if(width1<=700){
			widht11=0;
			width=1000;
		}
		var list = new Array(["b"]);
		var list1 = new Array(["a"]);
		var title = "创建招聘批次";
		if(type=='2'){
			title='修改招聘批次';
		}else{
			flagPriv=3;
		}
		var flag = '保存';
		if(type=='2'){
			flag = '编辑';
		}
		if(flagPriv==1||!priv){
			flag='';
		}
		var changed = false;
		var win = Ext.create('Ext.Window',{
			id:'addWin',
			plain: true,
			header: false,
		    border: false,
		    closable: false,
		    draggable: false,
			frame:false,
			resizable :false,
			maximized:true,
			closable:false,
			autoScroll:true,
			items:[{
                xtype:'panel',
				title:'<div style="float:left">'
								+ title
							+ '</div>'
							+'<div id="titilPanel" style="font-weight:normal;float:right;padding-right:70px">'
							+ '<a id="buttonSave" href="javascript:recruit_batch.saveRecruitBatch();" >'
							+ flag 
							+ '</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="recruit_batch.cancel()" >取消</a>'
							+'</div>',
			//renderTo:Ext.getBody(),
			maximized:true,
			closable:false,
			autoScroll:true,
			border: false,
			items:[{
			  xtype:'panel',
			  layout: "anchor", 
			  style:'margin-left:'+widht11+'px', 
			  border:false,
			  region: 'center',
  			  width:width1*0.8,
			  items:[{
				xtype:'panel',
				id:'addPanel',
				border:false,
				items:[{
					xtype:'panel',
					border:false,
					html:'<div style="background:#f0f0f0;color:#333;width: 100%;height:36px;line-height:36px;font-weight:bolder;font-size:12px; margin-left: 1%;margin-top:20px">&nbsp;&nbsp;&nbsp;招聘批次</div>'
				},{
					xtype:'textfield',
					fieldLabel:'批次编号',
					labelSeparator:null,
					style:'margin-left:32px;margin-top:20px;',
					id:'batchCodeId',
					labelWidth:116,
					inputWrapCls: 'x-form-field-my inwidth',
					readOnly:true,
					width:width*0.3,
					beforeLabelTextTpl:"<font color='red'> * </font>",
					listeners : {
 						'render': function(obj) {
　　							this.inputEl.addListener('focus', function(event, htmlElement, options) {
								htmlElement.blur();
　　							});
							Ext.getDoc().on("contextmenu", function(e){
								e.stopEvent();
　　							});
						}
					}
				},{
					xtype:'textfield',
					fieldLabel:'批次名称',
					allowBlank:false,
					style:'margin-left:30px;margin-top:20px;',
					id:'batchNameId',
					maxLength:50,
	    	        maxLengthText:'批次名称不能超过25个汉字或者50个字母数字！',
					labelWidth:118,
					labelSeparator:null,
					width:width*0.3,
					beforeLabelTextTpl:"<font color='red'> * </font>",
					inputWrapCls: 'x-form-field-my inwidth',
					readOnly:type==2?true:false,
					listeners:{
						'focus':function(obj){
							if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
								obj.blur();
							}
						},
						'render':function(){
							if(type=='1'){
								this.setFieldStyle("border:1px solid #B5B8C8");
							}
						}
						
					}
				},{
				xtype:'panel',
				border:false,
				layout:'table',
				style:'margin-left:30px;margin-top:20px;',
				items:[{
						xtype:'panel',
						style:'width:123px',
						border:false,
						html:'<font color="red"> * </font>归属单位'
					},{
						xtype:'codecomboxfield',
						border:false,
						id:'b0110Id',
						codesetid:"UN",
						onlySelectCodeset:false,
						ctrltype:"3",
						nmodule:"7",
						inputWrapCls: 'x-form-field-my codewidth',
						readOnly:type==2?true:false,
						width:width*0.3-122,
						listeners:{
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							}
						}
					}]
				},{
					xtype:'combo',
					fieldLabel:'&nbsp;&nbsp;招聘渠道',
					labelAlign:'left',
					labelWidth:118,
					style:'margin-left:30px;margin-top:20px;',
					id:'z0151Id',
					labelSeparator:null,
					width:width*0.3+1,
					editable:false,
					store:channelStore,
					displayField:'itemdesc', 
					inputWrapCls:'x-form-field-my codewidth',
					readOnly:type==2?true:false,
					valueField:'itemid',
					listeners:{
						'focus':function(obj){
							if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
								obj.blur();
							}
						},
						'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
						}
					}
				},{
					xtype:'combo',
					fieldLabel:'&nbsp;&nbsp;招聘流程',
					id:'z0153Id',
					labelAlign:'left',
					labelWidth:116,
					labelSeparator:null,
					style:'margin-left:32px;margin-top:20px;',
					store:flowStore,
					editable:false,
					displayField:'itemdesc', 
					valueField:'itemid',
					inputWrapCls: type==2?'x-form-field-my codewidth':'codewidth',
					readOnly:type==2?true:false,
					width:width*0.3-1,
					autoHeight:true,
					listeners:{
						'focus':function(obj){
							if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
								obj.blur();
							}
						},
						'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
						}
					}
				},{
					xtype:'textfield',
					id:'typeid',
					hidden:true
				}]
			},{
				xtype:'panel',
				height:250,
				border:false,
				items:[{
					xtype:'panel',
					border:false,
					html:'<div style="background:#f0f0f0;color:#333;width: 100%;height:36px;line-height:36px;font-weight:bolder;font-size:12px; margin-left: 1%;margin-top:20px">&nbsp;&nbsp;&nbsp;批次发布设置</div>'
				},{
					xtype:'panel',
					border:false,
					layout:'table',
					style:'margin-left:32px;margin-top:20px;',
					items:[{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	     	 
						labelSeparator:null,
						fieldLabel: '&nbsp;&nbsp;招聘时间范围', 
						//width:300,
						id:'date1',
						labelWidth:116,
						selectOnFocus:true, 
						format: RecruitmentStart,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						listeners:{
							'change':function(field,value,obj){
								var str = Ext.getCmp('date1').getRawValue();
								if(str ==""){
									Ext.getCmp('date3').setRawValue("");
									return;
								}
								str = str.replace(/-/g, '/');
								var date = new Date(str);
								var date3str = Ext.Date.format(date,applyStart);
								if(date3str.indexOf("NaN") == -1)
									Ext.getCmp('date3').setRawValue(date3str);
								
								changed = true;
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date1').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									if(Ext.getCmp('date1').getRawValue()>Ext.getCmp('date2').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date2').getRawValue())
											&&!Ext.isEmpty(Ext.getCmp('date1').getRawValue())){
										Ext.Msg.alert('提示信息',"招聘起始时间不能大于招聘截止时间");
										this.setValue('');
									}
									changed = false;
								}
  							},
  							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							}
						}
					},{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	
						style:'margin-left:10px',     	 
						fieldLabel: '至', 
						labelWidth:15,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						//width:197,
						id:'date2',
						labelSeparator:null,
						format: RecruitmentEnd,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
								var str = Ext.getCmp('date2').getRawValue();
								if(str ==""){
									Ext.getCmp('date4').setRawValue("");
									return;
								}
								str = str.replace(/-/g, '/');
								var date = new Date(str);
								var date4str = Ext.Date.format(date,applyEnd);
								if(date4str.indexOf("NaN") == -1)
									Ext.getCmp('date4').setRawValue(date4str);
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date2').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									var date2Value = Ext.getCmp('date2').getRawValue();
									if(date2Value.length ==10)
										date2Value =  date2Value + " 23:59:59"
									
									if(date2Value<Ext.getCmp('date1').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date1').getRawValue())
											&&!Ext.isEmpty(Ext.getCmp('date2').getRawValue())){
										Ext.Msg.alert('提示信息',"招聘截止时间不能小于招聘起始时间");
										this.setValue('');
										Ext.getCmp('date4').setRawValue('');
									}
									changed = false;
								}
  							}
						}
					}]
					
				},{
					xtype:'panel',
					border:false,
					layout:'table',
					style:'margin-left:32px;margin-top:20px;',
					items:[{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	     	 
						labelSeparator:null,
						fieldLabel: '&nbsp;&nbsp;报名时间范围', 
						labelWidth:116,
						id:'date3',
						format: applyStart,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date3').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									if(Ext.getCmp('date3').getRawValue()>Ext.getCmp('date4').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date4').getRawValue())){
										Ext.Msg.alert('提示信息',"报名起始时间不能大于报名截止时间");
										this.setValue('');
									}
									changed = false;
								}
  							}
						}
					},{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	
						style:'margin-left:10px',     	 
						id:'date4',
						fieldLabel: '至', 
						labelWidth:15,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						labelSeparator:null,
						format : applyEnd,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date4').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									var date4Value = Ext.getCmp('date4').getRawValue();
									if(date4Value.length ==10)
										date4Value =  date4Value + " 23:59:59"
									
									if(date4Value<Ext.getCmp('date3').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date3').getRawValue())){
										Ext.Msg.alert('提示信息',"报名截止时间不能小于报名起始时间");
										this.setValue('');
									}
									changed = false;
								}
  							}
						}
					}]
				},{
					xtype:'panel',
					border:false,
					layout:'table',
					style:'margin-left:32px;margin-top:20px;',
					items:[{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						id:'date5',
						labelSeparator:null,
						name: 'info.createTimestamp',	     	 
						fieldLabel: '&nbsp;&nbsp;打印准考证时间', 
						labelWidth:116,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						format: printTicketStart,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date5').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									if(Ext.getCmp('date5').getRawValue()>Ext.getCmp('date6').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date6').getRawValue())){
										Ext.Msg.alert('提示信息',"打印准考证起始时间不能大于打印准考证截止时间");
										this.setValue('');
									}
									changed = false;
								}
  							}
						}
					},{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	
						style:'margin-left:10px',     	 
						fieldLabel: '至', 
						id:'date6',
						labelWidth:15,
						labelSeparator:null,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						format: printTicketEnd,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date6').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									if(Ext.getCmp('date5').getRawValue()>Ext.getCmp('date6').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date6').getRawValue())){
										Ext.Msg.alert('提示信息',"打印准考证起始时间不能大于打印准考证截止时间");
										this.setValue('');
									}
									changed = false;
								}
  							}
						}
					}]
				},{
					xtype:'panel',
					border:false,
					layout:'table',
					style:'margin-left:32px;margin-top:20px;',
					items:[{
						xtype : 'datetimefield',
						id:'date7',
						labelSeparator:null,
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	     	 
						fieldLabel: '&nbsp;&nbsp;查看成绩时间', 
						labelWidth:116,
						format: ViewResultStart,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date7').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									if(Ext.getCmp('date7').getRawValue()>Ext.getCmp('date8').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date8').getRawValue())){
										Ext.Msg.alert('提示信息',"查看成绩起始时间不能大于查看成绩截止时间");
										this.setValue('');
									}
									changed = false;
								}
  							}
						}
					},{
						xtype : 'datetimefield',
						labelStyle: 'text-align:left;',
						name: 'info.createTimestamp',	
						style:'margin-left:10px',     	 
						fieldLabel: '至', 
						labelWidth:15,
						id:'date8',
						labelSeparator:null,
						format: ViewResultEnd,
						fieldStyle:'text-align:center',
						inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
						readOnly:type==2?true:false,
						listeners:{
							'change':function(field,value,obj){
								changed = true;
							},
							'focus':function(obj){
								if(type=='2'&&document.getElementById('buttonSave').innerHTML=='编辑'){
									obj.blur();
								}
							},
							'render':function(){
								if(type=='1'){
									this.setFieldStyle("border:1px solid #B5B8C8");
								}
							},
							'blur':function(obj){
								if(Ext.isEmpty(trim(Ext.getCmp('date8').getRawValue()))){
									this.setValue('');
									this.validate();
								}
								if(changed){
									if(Ext.getCmp('date7').getRawValue()>Ext.getCmp('date8').getRawValue()&&!Ext.isEmpty(Ext.getCmp('date8').getRawValue())){
										Ext.Msg.alert('提示信息',"查看成绩起始时间不能大于查看成绩截止时间");
										this.setValue('');
									}
									changed = false;
								}
  							}
						}
					}]
				}]
				
				}]
			}],
			listeners:{
				'afterrender':function(){
					Ext.getCmp('batchCodeId').setValue(id);
					Ext.getCmp('batchCodeId').setReadOnly(true);
					Ext.getCmp('typeid').setValue(type);
					//新增时，归属单位默认显示第一个 
					var status = true;
					if(type=='1'){
					    Ext.getCmp('batchCodeId').hide();
						Ext.getCmp('b0110Id').getPicker().getStore().on('load',function(){
							if(Ext.getCmp('b0110Id').getPicker().getStore().getRootNode().firstChild){
								var node = Ext.getCmp('b0110Id').getPicker().getStore().getRootNode().firstChild.data;
								var value=node.id+"`"+node.text;
								if(status){
									Ext.getCmp('b0110Id').setValue(value);
								}
								status=false;
							}
							
						});
					}
					
					if(type=='2'){
						recruit_batch.list = map.list;
						Ext.getCmp('batchNameId').setValue(recruit_batch.list[0]);
						//归属单位
					    Ext.getCmp('b0110Id').setValue(recruit_batch.list[1]);
						//招聘渠道
						Ext.getCmp('z0151Id').setValue(recruit_batch.list[2]);
						//招聘流程
					    Ext.getCmp('z0153Id').setValue(recruit_batch.list[3]);
						//招聘时间范围
						Ext.getCmp('date1').setRawValue(recruit_batch.list[4]);
						Ext.getCmp('date2').setRawValue(recruit_batch.list[5]);
						//报名时间范围
						if(Ext.isEmpty(recruit_batch.list[6]) && Ext.isEmpty(recruit_batch.list[7])){
							Ext.getCmp('date3').setRawValue(recruit_batch.list[4]);
							Ext.getCmp('date4').setRawValue(recruit_batch.list[5]);
						}else{
							Ext.getCmp('date3').setRawValue(recruit_batch.list[6]);
							Ext.getCmp('date4').setRawValue(recruit_batch.list[7]);
						}
					
						
						//打印准考证时间
						Ext.getCmp('date5').setRawValue(recruit_batch.list[8]);
						Ext.getCmp('date6').setRawValue(recruit_batch.list[9]);
						//查看成绩时间
						Ext.getCmp('date7').setRawValue(recruit_batch.list[10]);
						Ext.getCmp('date8').setRawValue(recruit_batch.list[11]);
						var fieldsValue = recruit_batch.list[recruit_batch.list.length-2];
						Ext.each(recruit_batch.fields,function(obj,index) {
						    var itemtype = obj.itemtype;
						    var itemdesc = obj.itemdesc;
						    var itemid = obj.itemid;
						    var item = fieldsValue[index];
						    if("D" == itemtype)
						    	Ext.getCmp(itemid+"Id").setRawValue(eval("item."+itemid));
						    else
						    	Ext.getCmp(itemid+"Id").setValue(eval("item."+itemid));
						});
					}
				}
			}
			  }]
		});
		var addPanel = Ext.getCmp('addPanel');
		Ext.each(recruit_batch.fields,function(obj) {
		    var itemtype = obj.itemtype;
		    var itemdesc = obj.itemdesc;
		    if("D"==itemtype){
			    addPanel.add({
					xtype : 'datetimefield',
					labelStyle: 'text-align:left;',
					name: 'info.createTimestamp',	     	 
					labelSeparator:null,
					fieldLabel: '&nbsp;&nbsp;'+itemdesc, 
					id:obj.itemid+"Id",
					labelWidth:116,
					selectOnFocus:true, 
					format: 'Y-m-d H:i',
					inputWrapCls: type==2?'x-form-field-my timewidth':'timewidth',
					readOnly:type==2?true:false,
					style:'margin-left:32px;margin-top:20px;',
					listeners:{
						'render':function(){
							if(type=='1'){
								this.setFieldStyle("border:1px solid #B5B8C8");
							}
						}
					}
				});
		    }else if("A"==itemtype&&"0"!=obj.codesetid){
		    	addPanel.add({xtype:'panel',
						border:false,
						layout:'table',
						style:'margin-left:30px;margin-top:20px;',
						items:[{
							xtype:'panel',
							style:'width:123px',
							border:false,
							html:'&nbsp;&nbsp;'+itemdesc
						},{
							xtype:'codecomboxfield',
							border:false,
							id:obj.itemid+"Id",
							codesetid:obj.codesetid,
							ctrltype:"3",
							nmodule:"7",
							inputWrapCls: 'x-form-field-my codewidth',
							readOnly:type==2?true:false,
							width:width*0.3-122,
							listeners:{
								'render':function(){
									if(type=='1'){
										this.setFieldStyle("border:1px solid #B5B8C8");
									}
								}
							}
				    	}]
					})
		    }
		    else{
		    	addPanel.add({
					xtype:'textfield',
					fieldLabel:'&nbsp;&nbsp;'+itemdesc,
					style:'margin-left:32px;margin-top:20px;',
					id:obj.itemid+"Id",
					labelWidth:116,
					inputWrapCls: 'x-form-field-my inwidth',
					readOnly:type==2?true:false,
					width:width*0.3,
					listeners : {
						'render':function(){
							if(type=='1'){
								this.setFieldStyle("border:1px solid #B5B8C8");
							}
						}
					}
				});
		    }
		});
		 win.show();
		
	},
	//取消
	cancel:function(){
		Ext.getCmp('addWin').close();
		//recruit_batch.init();
		Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();
	},
	//保存招聘批次
	saveRecruitBatch:function(){
		if(document.getElementById('buttonSave').innerHTML=='编辑'){
			document.getElementById('buttonSave').innerHTML='保存';
			Ext.getCmp('batchNameId').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('b0110Id').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('z0151Id').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('z0153Id').setFieldStyle("border:1px solid #B5B8C8;height:23px");
			Ext.getCmp('z0153Id').setHeight(23);
			Ext.getCmp('date1').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date2').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date3').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date4').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date5').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date6').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date7').setFieldStyle("border:1px solid #B5B8C8");
			Ext.getCmp('date8').setFieldStyle("border:1px solid #B5B8C8");
			Ext.each(recruit_batch.fields,function(obj,index) {
				Ext.getCmp(obj.itemid+"Id").setFieldStyle("border:1px solid #B5B8C8");
				Ext.getCmp(obj.itemid+"Id").setReadOnly(false);
				if(recruit_batch.fields.length==index+1){
					Ext.getCmp(obj.itemid+"Id").setFieldStyle("height:23px");
					Ext.getCmp(obj.itemid+"Id").setHeight(23);
				}
			});
			Ext.getCmp('batchNameId').setReadOnly(false);
			Ext.getCmp('b0110Id').setReadOnly(false);
			Ext.getCmp('z0151Id').setReadOnly(false);
			Ext.getCmp('z0153Id').setReadOnly(false);
			Ext.getCmp('date1').setReadOnly(false);
			Ext.getCmp('date2').setReadOnly(false);
			Ext.getCmp('date3').setReadOnly(false);
			Ext.getCmp('date4').setReadOnly(false);
			Ext.getCmp('date5').setReadOnly(false);
			Ext.getCmp('date6').setReadOnly(false);
			Ext.getCmp('date7').setReadOnly(false);
			Ext.getCmp('date8').setReadOnly(false);
		}else{
			//批次编号
			var z0101 = Ext.getCmp('batchCodeId').getValue();
			//批次名称
			var z0103 = Ext.getCmp('batchNameId').getValue();
			//归属单位
			var z0105 = Ext.getCmp('b0110Id').getValue();
			//招聘渠道
			var z0151 = Ext.getCmp('z0151Id').getValue();
			//招聘流程
			var z0153 = Ext.getCmp('z0153Id').getValue();
			//招聘时间范围
			var z0107 = Ext.getCmp('date1').getRawValue();
			var z0109 = Ext.getCmp('date2').getRawValue();
			//报名时间范围
			var z0155 = Ext.getCmp('date3').getRawValue();
			var z0157 = Ext.getCmp('date4').getRawValue();
			//打印准考证时间
			var z0159 = Ext.getCmp('date5').getRawValue();
			var z0161 = Ext.getCmp('date6').getRawValue();
			//查看成绩时间
			var z0163 = Ext.getCmp('date7').getRawValue();
			var z0165 = Ext.getCmp('date8').getRawValue();
			var type = Ext.getCmp('typeid').getValue();
			
			if(!z0103 || trim(z0103).length<=0 ){
				Ext.Msg.alert('提示信息','批次名称不能为空');
				return;
			}
			
			if(z0103.replace(/[\u4E00-\u9FA5]/g,'aa').length>50){
				Ext.showAlert(BATCHNAME_MORE_LONG);
				return;
	        }
			
			if(z0105=='`'||!z0105){
				Ext.Msg.alert('提示信息','归属单位不能为空');
				return;
			}
			var regResult = Ext.getCmp("date1").validate() && Ext.getCmp("date2").validate() 
						&& Ext.getCmp("date3").validate() && Ext.getCmp("date4").validate()
						&& Ext.getCmp("date5").validate() && Ext.getCmp("date6").validate()
						&& Ext.getCmp("date7").validate() && Ext.getCmp("date8").validate();
			if(!regResult){
				Ext.Msg.alert(PROMPT_INFORMATION,ENTER_DATE_FORMAT_ERROR);
				return;
			}
				
			var arr = new Array();
			arr[0]=z0103==null?'':z0103;
			arr[1]=z0105;
			arr[2]=z0151;
			arr[3]=z0153;
			//arr[4]=recruit_batch.dateFormat(z0107);
			arr[4]=z0107;
			arr[5]=z0109;
			arr[6]=z0155;
			arr[7]=z0157;
			arr[8]=z0159;
			arr[9]=z0161;
			arr[10]=z0163;
			arr[11]=z0165;
			arr[12]=z0101;
			var mapValue = new HashMap();
			Ext.each(recruit_batch.fields,function(obj) {
				var value = "";
				if("D"==obj.itemtype)
					value = Ext.getCmp(obj.itemid+"Id").getRawValue();
				else
					value = Ext.getCmp(obj.itemid+"Id").getValue();
				
				mapValue.put(obj.itemid, value);
			});
			 var map = new HashMap();
			 map.put('arr',arr);
			 map.put('type',type);
			 map.put("fields",recruit_batch.fields);
			 map.put("fieldsValue",mapValue);
			 Rpc({functionId:'ZP0000002534',async:false,success:recruit_batch.cancel},map);
		}
		
	},
	//格式化日期
	dateFormat:function(value){ 
	    if(null != value){ 
	        return Ext.Date.format(new Date(Date.parse(value.replace(/-/g,"/"))),'Y-m-d H:m:s'); 
	    }else{ 
	        return null; 
		}
	},
	//获取选中grid的列
	 getSelect:function (grid, col) { 
		var arr=new Array();
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
			      arr[i]=grid.getSelectionModel().getSelection()[i].get(col);
			 }
		}
		return arr;
	},
		//获取选中grid的列,用于删除权限控制
	 getSelectByPrivDelete:function (grid, col) { 
		var arr=new Array();
		var array = new Array();
		var j = 0;
		var z = 0;
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
			     if(grid.getSelectionModel().getSelection()[i].get('flag')==2){
				     arr[z]=grid.getSelectionModel().getSelection()[i].get(col);
				     z++;
			     }else{
			     	array[j]=grid.getSelectionModel().getSelection()[i].get('z0103');
			     	j++;
			     }
			     var count = array.length;
			     if(arr.length>0){
			     	if(count>=2){
				     Ext.Msg.confirm('提示信息','您没有 '+array[0]+','+array[1]+' 等'+count+'个招聘批次的操作权限，是否继续删除其余招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								Rpc({functionId:'ZP0000002535',async:false,success:recruit_batch.deleteRecruitBatchOK},map);
						 	} 
						});
				     }else if(count>0){
					     Ext.Msg.confirm('提示信息','您没有 '+array+' 招聘批次的操作权限,是否继续删除其余招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								Rpc({functionId:'ZP0000002535',async:false,success:recruit_batch.deleteRecruitBatchOK},map);
						 	} 
						});
				     }else{
					     Ext.Msg.confirm('提示信息','确认删除招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								Rpc({functionId:'ZP0000002535',async:false,success:recruit_batch.deleteRecruitBatchOK},map);
						 	} 
						});
				     }
			     }else{
			     	if(count>=2){
				     	Ext.Msg.alert('提示信息','您没有 '+array[0]+','+array[1]+' <br/>等'+count+'个招聘批次的操作权限');
				     }else if(count>0){
					     Ext.Msg.alert('提示信息','您没有 '+array+' 招聘批次的操作权限');
				     }
			     }
			     
			     
			 }
		}
		return arr;
	},
		//获取选中grid的列，用于发布权限控制
	 getSelectByPrivPublish:function (grid, col) { 
		var arr=new Array();
		var array = new Array();
		var j = 0;
		var z = 0;
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
			     if(grid.getSelectionModel().getSelection()[i].get('flag')==2){
				     arr[z]=grid.getSelectionModel().getSelection()[i].get(col);
				     z++;
			     }else{
			     	array[j]=grid.getSelectionModel().getSelection()[i].get('z0103');
			     	j++;
			     }
			     var count = array.length;
			     if(arr.length>0){
				     if(count>=2){
					     Ext.Msg.confirm('提示信息','您没有 '+array[0]+','+array[1]+' 等'+count+'个招聘批次的操作权限，是否继续发布其余招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								map.put("type","1");
								Rpc({functionId:'ZP0000002536',async:false,success:function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
						 	} 
						});
				     }else if(count>0){
					     Ext.Msg.confirm('提示信息','您没有 '+array+' 招聘批次的操作权限,是否继续发布其余招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								map.put("type","1");
								Rpc({functionId:'ZP0000002536',async:false,success:function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
						 	} 
						});
				     }else{
					     Ext.Msg.confirm('提示信息','确认发布招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								map.put("type","1");
								Rpc({functionId:'ZP0000002536',async:false,success:function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
						 	} 
						});
				     }
				  }else{
			     	if(count>=2){
				     	Ext.Msg.alert('提示信息','您没有 '+array[0]+','+array[1]+' 等'+count+'个招聘批次的操作权限');
				     }else if(count>0){
					     Ext.Msg.alert('提示信息','您没有 '+array+' 招聘批次的操作权限');
				     }
			     }
			 }
		}
		return arr;
	},
		//获取选中grid的列，用于结束权限控制
	 getSelectByPrivClose:function (grid, col) { 
		var arr=new Array();
		var array = new Array();
		var j = 0;
		var z = 0;
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
			     if(grid.getSelectionModel().getSelection()[i].get('flag')==2){
				     arr[z]=grid.getSelectionModel().getSelection()[i].get(col);
				     z++;
			     }else{
			     	array[j]=grid.getSelectionModel().getSelection()[i].get('z0103');
			     	j++;
			     }
			     var count = array.length;
			     if(arr.length>0){
				     if(count>=2){
					     Ext.Msg.confirm('提示信息','您没有 '+array[0]+','+array[1]+' 等'+count+'个招聘批次的操作权限，是否继续结束其余招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								map.put("type","2");
								Rpc({functionId:'ZP0000002536',async:false,success:function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
						 	} 
						});
				     }else if(count>0){
					     Ext.Msg.confirm('提示信息','您没有 '+array+' 招聘批次的操作权限,是否继续结束其余招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								map.put("type","2");
								Rpc({functionId:'ZP0000002536',async:false,success:function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
						 	} 
						});
				     }else{
					     Ext.Msg.confirm('提示信息','确认结束招聘批次？',function(btn){ 
							if(btn=="yes"){ 
								// 确认触发，继续执行后续逻辑。 
								//selectid选中的记录
								var map = new HashMap();
								map.put("selectid",arr);
								map.put("type","2");
								Rpc({functionId:'ZP0000002536',async:false,success:function(form,action){Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();}},map);
						 	} 
						});
				     }
				   }else{
			     	if(count>=2){
				     	Ext.Msg.alert('提示信息','您没有 '+array[0]+','+array[1]+' 等'+count+'个招聘批次的操作权限');
				     }else if(count>0){
					     Ext.Msg.alert('提示信息','您没有 '+array+' 招聘批次的操作权限');
				     }
			     }
			 }
		}
		return arr;
	},
	//删除招聘批次
	deleteRecruitBatch:function(){
		var grid = Ext.getCmp('recruitbatch001_tablePanel');
		if(grid.getSelectionModel().getSelection().length<=0){
			Ext.showAlert("请选择批次！");
			return;
		}
		
		for ( var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			var record = grid.getSelectionModel().getSelection()[i];
			var z0129 = record.data.z0129;
			if(z0129=="04" || z0129=="03" || z0129=="02"){
				Ext.showAlert(CANNOT_DELETE_BATCH);
				return;
			}
		}
		
		recruit_batch.getSelectByPrivDelete(grid,'z0101');
	},
	
	deleteRecruitBatchOK:function(response){
		var value = response.responseText;
		var map = Ext.decode(value);
		var list = map.list;
		var flag = "";
		Ext.getCmp('recruitbatch001_tablePanel').getStore().reload();
		if(list[list.length-1]=='2'){
			for(var i = 0;i<list.length-1;i++){
				flag=flag+list[i]+"],[";
			}
			flag=flag.substring(0,flag.length-2);
			Ext.showAlert("招聘批次: ["+flag+" 已绑定职位或考场，不能删除");
		}
	},
	//发布招聘批次
	publishRecruitBatch:function(){
		var grid = Ext.getCmp('recruitbatch001_tablePanel');
		if(grid.getSelectionModel().getSelection().length<=0){
			Ext.showAlert("请选择批次！");
			return;
		}
		recruit_batch.getSelectByPrivPublish(grid,'z0101');
		
	},
	//结束招聘批次
	closeRecruitBatch:function(){
		var grid = Ext.getCmp('recruitbatch001_tablePanel');
		if(grid.getSelectionModel().getSelection().length<=0){
			Ext.showAlert("请选择批次！");
			return;
		}
		recruit_batch.getSelectByPrivClose(grid,'z0101');
	},
	toGetDataValue:function(){
		var dataList = [{dataValue:'01',dataName:'起草'},{dataValue:'04',dataName:'已发布'},{dataValue:'06',dataName:'结束'}];
		return dataList;
	},
	
	//获取时间控件的日期格式
	getFormat:function(itemLength){
		if(itemLength ==4){
			dateFormat ="Y";
		}else if(itemLength ==7){
			dateFormat ="Y-m";
		}else if(itemLength ==10){
			dateFormat ="Y-m-d";
		}else if(itemLength ==16){
			dateFormat ="Y-m-d H:i";
		}else if(itemLength >=18){
			dateFormat ="Y-m-d H:i:s";
		}
		return dateFormat;
	},
	
	//获取发送通知内容
	sendNotice:function(){
		var selectstore = Ext.getCmp("recruitbatch001_tablePanel").getSelectionModel().getSelection();
		if(selectstore.length<=0){
			Ext.showAlert("请选择批次！");
			return;
		}
		
		if(selectstore.length>1){
			Ext.showAlert(CHOOSE_A_BATCH_SEND_NOTIFICATION);
			return;
		}
		
		recruit_batch.z0101s = new Array();
		var flag = false;
		Ext.each(selectstore,function(record,index){
			if(0==index)
				recruit_batch.title = record.data.z0103;
			if(record.data.z0129!='04'){
				flag = true;
				return;
			}
			recruit_batch.z0101s.push(record.data.z0101);
		});
		if(flag){
			Ext.showAlert("仅已发布批次可以发送通知！");
			return;
		}
		var map = new HashMap();
    	map.put("functionType","search");
    	map.put("batchIds",recruit_batch.z0101s);
		Rpc({functionId:'ZP0000002549',async:false,success:recruit_batch.showNoticeWindow},map);
	},
	showNoticeWindow:function(param){
		var result = Ext.decode(param.responseText);
		var content = result.content;
		var data = Ext.decode(result.templateList); 
		var selfuser = result.selfuser;
		var bususer = result.bususer; 
		if(data==0){
			Ext.showAlert(SET_Batch_RESUME_TEMPLATE);
			return;
		}
		var combo = recruit_batch.createComboBox(result);
		var noticeWindow = Ext.create('Ext.window.Window',{
			title: recruit_batch.title,
			height: 420,
			id:'noticeWindowId',
			width: 600,
			padding:'0 10 0 10',
			items : [{
				xtype:'container',
				layout: {
				    type: 'hbox',
				    align:'middle'
				},
				items:[combo,
					{
					xtype:'checkbox',
					id:'sendEmailId',
					padding:'0 0 0 10px',
					width:60,
					boxLabel:'邮件'
					}]
			},{
		        xtype:'fieldset',
		        columnWidth: 0.5,
		        title: '<span style="color:#000000;">通知内容</span>',
		        defaultType: 'textfield',
		        height:210,
		        margin:0,
		        scrollable:true,
		        defaults: {anchor: '100%'},
		        layout: 'anchor',
		        html:'<div id="noticeContentId" style="overflow:auto;" >'+content+'</div>'
		    },{xtype:'panel',
		    	width:'100%',
		    	border:false,
		    	height:100,
		    	padding:'0 10 0 10',
		    	items:[{ 
		    	    title: '通知对象',
		    	    border:false,
		    	    height:30,
		    	    fullscreen: true,
		    	    tools: [{
		    	    	xtype: 'label',
		    	    	margin:'0 6 0 0',
		    	    	html:'添加：'
		    	    },{
		    	    	xtype: 'label',
		    	    	margin:'0 6 0 0',
		    	        html: '<a>自助用户</a>',
		    	        listeners: {
		    	            click: {
		    	                element: 'el',
		    	                fn:function(){recruit_batch.openPicker(true)}
		    	            }
		    	        }
		    	    },{
		    	    	xtype: 'label',
		    	        html: '<a>业务用户</a>',
		    	        listeners: {
		    	            click: {
		    	                element: 'el',
		    	                fn:function(){recruit_batch.openPicker(false)} 
		    	            }
		    	        }
		    	    }]
		    	}],
		    	html:'<div id="personArea" class="hj-zm-xq-two" style="overflow-y:auto;width:558px;height:70px" ></div>',
	    	}],
		    buttonAlign : 'center',
		    listeners:{
		    	'close':function(){
		    		person = new Array(); 
		    		recruit_batch.bususer=[];
		    	},
		    	'afterrender':function(){
    				recruit_batch.insertObj(selfuser,true);
    				recruit_batch.insertObj(bususer,false); 
		    	}
		    },
	        buttons : [{
				text : "确定",
				id : "sendEmail",
				handler : recruit_batch.enter
			}, {
				text : "关闭",
				handler : function(){
					noticeWindow.close();
				}
			}]
		}).show();
	},
	//切换邮件模板
    createComboBox:function(result){
    	var data = Ext.decode(result.templateList);
		var store = new Ext.data.ArrayStore( {
			fields : ['value', 'name'],
			data:data
		});
		var combo = Ext.create('Ext.form.ComboBox', {
			id : 'combo',
			injectCheckbox : 1,
			autoSelect : true,
			editable : false,
			margin : '10 0 10 0',
			store : store,
			fieldLabel : "通知模板",
			labelSeparator : "",
			labelWidth : 55,
			anchor : "80%",
			valueField : 'value',
			displayField : 'name',// store字段中你要显示的字段，多字段必选参数，默认当mode为remote时displayField为undefine，当
			// select列表时displayField为”text”
			mode : 'local',// 因为data已经取数据到本地了，所以’local’,默认为”remote”，枚举完
			emptyText : '请选择一个模板',
			applyTo : 'combo',
			autoloader : true,
			listeners : {
				'afterRender':function(){
					if(data.length > 0)
						   value = data[0][0];
					this.setValue(value);
				},
				'select' : function(combo, record, index) {
					var map = new HashMap();
			    	map.put("functionType","search");
			    	map.put("templateId", combo.value);
					map.put("batchIds",recruit_batch.z0101s);
			    	Rpc({functionId : 'ZP0000002549',success:function(param){
			    		var result = Ext.decode(param.responseText);
			    		var content = result.content;
			    		Ext.getDom("noticeContentId").innerHTML = content;
			    	}}, map);
				}
			}
		});
		return combo;
    },
    enter:function(){ 
    	if(person.length==0&&recruit_batch.bususer.length==0){
    		Ext.showAlert("请选择通知对象！"); 
    		return;
    	}
    	var map = new HashMap();
    	map.put("batchIds",recruit_batch.z0101s);
    	map.put("templateId", combo.value);
    	map.put("content",Ext.getDom("noticeContentId").innerHTML);
    	map.put("functionType","send");
    	map.put("sendEmail",Ext.getCmp('sendEmailId').checked);
    	map.put("persons",person); 
    	map.put("bususer",recruit_batch.bususer); 
    	Rpc({functionId:'ZP0000002549',async:false,success:function(form,action){
    		var result = Ext.decode(form.responseText);
    		Ext.getCmp('noticeWindowId').close();
    		if(result.succeed){
    			Ext.showAlert(NOTIFICATION_SENT_SUCCESSFULLY); 
			}
    		
    	}},map);
    },
    
	getDateStr: function(date, format){
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        var hour = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
		var minute = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
		var second = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
		var newTime = year + "-" + month + "-" + day + "  " + hour + ":" + minute + ":" + second;
		if(format =="Y"){
			newTime = year;
		}else if(format =="Y-m"){
			newTime = year + "-" + month;
		}else if(format =="Y-m-d"){
			newTime = year + "-" + month + "-" + day; 
		}else if(format =="Y-m-d H:i"){
			newTime = year + "-" + month + "-" + day + "  " + hour + ":" + minute;
		}
	        return newTime
	},
 
	openPicker:function(isSelfUser){
		var deprecate = isSelfUser?person:recruit_batch.bususer;
    	var picker = new PersonPicker({
    		multiple: true,
    		isPrivExpression:false,//是否启用人员范围（含高级条件）
    		validateSsLOGIN:true,
    		isSelfUser:isSelfUser,
    		deprecate: deprecate,
    		callback:function(c){
				recruit_batch.insertObj(c,isSelfUser);
    		}
    	}, this);
    	picker.open();
	},
	insertObj:function(c,isSelfUser){//新增接收人
		for(var i=0;i<c.length;i++){
			var el = c[i];
			if(!isSelfUser)
				recruit_batch.bususer.push(el.id);
			else
				person.push(el.id);
			var elem = Ext.getDom("personArea");
			var obj = document.createElement("div");
			obj.className="hj-nmd-dl";
			obj.onmouseover=function(){this.getElementsByTagName('img')[0].style.visibility=''}
			obj.onmouseleave=function(){this.getElementsByTagName('img')[0].style.visibility='hidden'}
			var html='<img class="deletePic" id="'+el.id+'" onclick="recruit_batch.deleteObj(this)" style="width: 14px; height: 14px;visibility:hidden" src="/workplan/image/remove.png" />';
			obj.innerHTML=html+'<dl><dt title="'+el.name+'"><img class="img-circle" src="'+el.photo+'" /></dt><dd class="text-ellipsis">'+el.name+'</dd></dl>';
			elem.appendChild(obj);
		}
	},
	deleteObj:function(el){//删除接收人
		Ext.Array.remove(person,el.id);
		Ext.Array.remove(recruit_batch.bususer,el.id); 
		var obj = el.parentNode;
		obj.parentNode.removeChild(obj);
	}
    
});
