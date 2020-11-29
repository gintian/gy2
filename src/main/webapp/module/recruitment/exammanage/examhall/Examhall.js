/**
 * 考试管理数据增删改
 */
Ext.define('ExamHallUL.Examhall',{
	examHallSalaryObj : '',
	examhall : '',
	addBatchId: '',
	iscontinuehidden: 'false',
	// 修改时的主键ID
	id : '',
	// 添加-1 修改-2
	type : '',
	constructor : function(config) {
		type = config.type;// 添加或修改
		examHallSalaryObj = config.tableObj;
		addBatchId = config.addBatchId;
		addBatchId ='all'==addBatchId?'':addBatchId;
		if (type == '2') {
			beforeditId = config.id;// 修改的主键id
		}
		examhall = this;
		examhall.init();
	},
	init : function() {
		Ext.Loader.setConfig({
			enabled : true,
			paths : {
				'SYSP' : '/components/sysExtPlugins'
			}
		});
		fObjz = null;
		var time_r=0; 
		// 为归属单位 ，考试时间 加载html代码
		var map = new HashMap();
		map.put('type', type);
	
		if (type == '1')
			map.put("type", "query");
		if (type == '2') {
			map.put('id',beforeditId);
			map.put("type", "beforedit");
		}
		
		Rpc({
			functionId : 'ZP0000002504',
			success : examhall.createTableOK
			}, map);
	},
	createTableOK:function(form){
		var flag = "保存";
		var a = "";
		var b = "";
		if (type == '2') {
			topTitle = '编辑考场信息';
			flag = "编辑";
			var mapExamRight = new HashMap();
			mapExamRight.put("id",id);
			mapExamRight.put("type", "examEditRight")
			mapExamRight.put("isEditOrDele", "edit");
			var test = false;
			Rpc({
				functionId : 'ZP0000002504',
				async : false,
				success : function(form, action) {
					var result = Ext.decode(form.responseText);
					if (result.examEditRight == '0') {
						test = true;
					}
				}
			}, mapExamRight);
			
			if (test)
				flag = "";
		}
		// 设置显示宽度
		var width = document.body.clientWidth * 0.96;
		// 获取所属班次的下拉值
		var affiliationStore = Ext.create('Ext.data.Store', {
			id : 'simpsonsStore',
			fields : [ "id", "des" ],
			proxy : {
				type:'transaction',
			    functionId:'ZP0000002504',
			    extraParams:{
			    	type : 'affiliation'
				},
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true
		});
		var topTitle = "创建考场";
		var result = Ext.decode(form.responseText);
		var dataList = result.dataList;
		var datemap = new HashMap();
		var panels1 = Ext.create("ZP.GENERATETIME", {
		    	   width:42,
		    	   panelWidth:132,
		    	   hourId:'bottles',
		    	   minuteId:'bottles1',
		    	   hourStyle:'margin-left:32px;',
		    	   minuteStyle:''
		    });
			var panel1 = panels1.getPanel();
			var panels2 = Ext.create("ZP.GENERATETIME", {
				width:42,
				panelWidth:128,
				hourId:'bottles2',
				flag:1,
				minuteId:'bottles3',
				//hourStyle:'margin-left:5px;',
				minuteStyle:''
			});
			var panel2 = panels2.getPanel();
			
			var panels3 = Ext.create("ZP.GENERATETIME", {
		    	   width:42,
		    	   panelWidth:128,
		    	   hourId:'bottles4',
		    	   minuteId:'bottles5',
		    	   hourStyle:'margin-left:32px;',
		    	   minuteStyle:''
		    });
			var panel3 = panels3.getPanel();
			panels3.hide();
			var panels4 = Ext.create("ZP.GENERATETIME", {
				width:42,
				panelWidth:128,
				hourId:'bottles6',
				flag:1,
				minuteId:'bottles7',
				//hourStyle:'margin-left:5px;',
				minuteStyle:''
			});
			var panel4 = panels4.getPanel();
			panels4.hide();
			var str = "";
			if(examhall.iscontinuehidden=="true")
		    {
		    	str ='<a id="iscontinue" href="javascript:void(0);" style="display:none;"><input style="margin-top:-3px;" id="iscontinuech" checked="checked" type="checkbox"/>继续创建新考场</font></a>';
		    }else{
		    	str ='<a id="iscontinue" href="javascript:void(0);" style="display:none;"><input style="margin-top:-3px;" id="iscontinuech" type="checkbox"/>继续创建新考场</font></a>';
		    }
		var regexp = new RegExp("^[^@#￥……&*]{1,}$");
		var d = new Ext.window.Window(
				{
					title:'<div style="float:left">'
						+ topTitle
						+ '</div>'
						+'<div id="titilPanel" style="font-weight:normal;float:right;padding-right:70px">'
						+ '<a id="buttonSave" href="javascript:examhall.saveExamHall(1);" >'
						+ flag
						+ '</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="examhall.cancel()" >取消&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>'
						+ str
						+'</div>',
					renderTo : Ext.getBody(),
					maximized:true,
					border : false,
					id : 'formId',
					height: 450,
					closable:false,
					autoScroll:true,
					layout : {
						type : "vbox",
						align : "center"
					},
					defaults : {
						border : false
					},
					items : [ {
						type : "vbox",
						align : "left",
						height: 455,
						defaults : {
							width : width * 0.8,
							border : false,
							style : 'margin-top:20px;margin-left:-25px',
							labelPad : 45
						},
						items : [
								{
									xtype : "textfield",
									id : "id",
									hidden : true,
									style : 'margin-top:20px;margin-left:0px'
								},
								{
									// id
									xtype : 'panel',
									border : false,
									id : 'secondTitle',
									style : 'margin-top:10px'
								},
								{
									// 招聘批次
									xtype : "combo",
									id : "affiliation",
									fieldLabel : "<span Style='color:red;'>*</span>招聘批次",
									store : affiliationStore,
									displayField : "des",
									valueField : "id",
									labelAlign : 'right',
									editable : false,
									labelPad : 45,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									labelSeparator : "",
									minWidth : 445,
									maxWidth : 445,
									fieldStyle : type == 2 ? 'border:0': '',
									readOnly : type == 2 ? true: false,
									listeners : {
										'focus' : function(obj) {
											if (type == '2'&&document.getElementById('buttonSave').innerHTML=='编辑') {
												obj.blur();
											}
										}
									}
								},
								{
									// 归属单位
									xtype : 'panel',
									border : false,
									layout : 'table',
									minWidth : 450,
									maxWidth : 450,
									items : [
											{
												xtype : 'tbfill',
												width : 47
											},
											{
												xtype : 'panel',
												style : 'width:57px',
												border : false,
												html : '<font color="red">*</font>所属单位'
											},
											{
												xtype : 'tbfill',
												width : 42
											},
											{
												xtype : 'codecomboxfield',
												border : false,
												id : 'b0110Id',
												codesetid : "UN",
												ctrltype : "3",
												nmodule : "7",
												inputWrapCls: type==2?'x-form-field-my':'x-form-field',
												fieldStyle : type == 2 ? 'border:0': '',
												readOnly : type == 2 ? true: false,
												width : 300
											} ]
								},
								{
									// 考场号
									xtype : "textfield",
									id : "examNumber",
									name : "examNumber",
									labelAlign : 'right',
									labelSeparator : "",
									labelPad : 45,
									fieldLabel : "<span Style='color:red;'>*</span>考场号",
									minWidth : 445,
									maxWidth : 445,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									fieldStyle : type == 2 ? 'border:0': '',
									readOnly : type == 2 ? true: false,
									regex : /^[^@#￥……&*]{1,}$/,
									regexText : '只能填写50位以内的数字或字母&nbsp;&nbsp;&nbsp;',
									listeners : {
										'focus' : function(obj) {
											if (type == '2'&&document.getElementById('buttonSave').innerHTML=='编辑') {
												obj.blur();
											}
										}
									}
								},
								{
									// 考场名称
									xtype : "textfield",
									id : "examName",
									name : "examNameShow",
									labelSeparator : "",
									labelAlign : 'right',
									fieldLabel : "<span Style='color:red;'>*</span>考场名称",
									minWidth : 445,
									maxWidth : 445,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									fieldStyle : type == 2 ? 'border:0': '',
									readOnly : type == 2 ? true: false,
									regex:/^((?!(@|#|￥|……|&|\*)).)*$/,
									regexText:"考场名称不要包含@#￥……&*",
									listeners : {
										'change':function(obj){
											if(obj.value.replace(/[\u4E00-\u9FA5]/g,'aa').length>50){
												Ext.getCmp("examName").setValue(a);
												Ext.showAlert("超出可以输入字符长度，长度为25汉字或50字母或数字！");
											}else{
												a = obj.value;
											}
										},
										'focus' : function(obj) {
											if ((type == '2'&&document.getElementById('buttonSave').innerHTML=='编辑')) {
												obj.blur();
											}
										}
									}
								},
								{
									// 考场地点
									xtype : "textfield",
									name : "situs",
									id : "situs",
									labelAlign : 'right',
									labelSeparator : "",
									fieldLabel : "地点",
									minWidth : 445,
									maxWidth : 445,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									fieldStyle : type == 2 ? 'border:0': '',
									readOnly : type == 2 ? true: false,
									listeners : {
										'change':function(obj){
											if(obj.value.replace(/[\u4E00-\u9FA5]/g,'aa').length>250){
												Ext.getCmp("situs").setValue(b);
												Ext.showAlert("超出可以输入字符长度，长度为125汉字或250字母或数字！");
											}else{
												b = obj.value;
											}
										},
										'focus' : function(obj) {
											if (type == '2'&&document.getElementById('buttonSave').innerHTML=='编辑') {
												obj.blur();
											}
										}
									}
								},
								{
									// 考场日期
									xtype : 'datefield',
									fieldLabel : '考试日期',
									name : 'examDate',
									id : 'examDate',
									labelAlign : 'right',
									labelSeparator : "",
									format : 'Y-m-d',
									minWidth : 290,
									maxWidth : 290,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									fieldStyle : type == 2 ? 'border:0': '',
									readOnly : type == 2 ? true: false,
									listeners : {
										'focus' : function(obj) {
											if (type == '2'&&document.getElementById('buttonSave').innerHTML=='编辑') {
												obj.blur();
											}
										}
									}
								},
								{
									// 考场时间考场信息浏览状态下的时间
									layout : 'hbox',
									id : 'timeedit',
									border : false,
									minWidth : 600,
									maxWidth : 600,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									layoutConfig : {
										align : 'middle',
										pack : 'start'
									},
									items : [{
										xtype : 'tbfill',
										maxWidth : 53
									},{
										border:false,
										html  : '考试时间'
									},{
										xtype : 'tbfill',
										maxWidth : 12
									},panel3, {
										xtype : 'tbfill',
										maxWidth : 10
									}, {
										border:false,
										style:'margin-top:5px',
										html : '至'
									}, {
										xtype : 'tbfill',
										maxWidth : 10
									},panel4 ]
								},
								{
									// 非考场时间考场信息浏览状态下的时间
									layout : 'hbox',
									id : 'timeadd',
									border : false,
									minWidth : 600,
									maxWidth : 600,
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									layoutConfig : {
										align : 'middle',
										pack : 'start'
									},
									items : [ {
										xtype : 'tbfill',
										maxWidth : 53
									},{
										border:false,
										html  : '考试时间'
									},{
										xtype : 'tbfill',
										maxWidth : 12
									},panel1, {
										xtype : 'tbfill',
										maxWidth : 10
									}, {
										border:false,
										style:'margin-top:5px',
										html : '至'
									}, {
										xtype : 'tbfill',
										maxWidth : 10
									},panel2 ]
								},
								{
									// 座位数
									xtype : "textfield",
									name : "sitNumber",
									id : "sitNumber",
									labelSeparator : "",
									labelAlign : 'right',
									minWidth : 207,
									maxWidth : 207,
									fieldLabel : "<span Style='color:red;'>*</span>座位数",
									regex : /^[1-9]\d{0,3}$/,
									regexText : '只能录入大于零且是4位以内的数字',
									inputWrapCls: type==2?'x-form-field-my':'x-form-field',
									fieldStyle : type == 2 ? 'border:0': '',
									readOnly : type == 2 ? true: false,
									listeners : {
										'focus' : function(obj) {
											if (type == '2'&&document.getElementById('buttonSave').innerHTML=='编辑') {
												obj.blur();
											}
										}
									}
								} ],
								listeners:{
									afterrender:function(){
										//新增时，归属单位默认显示第一个 sunming add 2015-12-23
										var status = true;
										if(type=='1'){
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
										
										Ext.getCmp("id").setValue(dataList[0]);
										var title = '';
										if (dataList[0] != '') {
											Ext.getCmp("affiliation").setValue(dataList[3]);
											Ext.getCmp("timeadd").hide();
											title = '编辑';
											a = dataList[2];
											b = dataList[4];
										} else {
											Ext.getCmp("affiliation").setValue(addBatchId);
											Ext.getCmp("timeedit").hide();
											title = '考场';
										}
										Ext.getCmp("examName").setValue(dataList[2]);
										Ext.getCmp("examNumber").setValue(dataList[1]);
										Ext.getCmp("situs").setValue(dataList[4]);
										Ext.getCmp("b0110Id").setValue(dataList[12] + "`"+ dataList[7]);
										var trainingStartTime = dataList[6];
										if(dataList[6]!="")
											trainingStartTime = new Date(Date.parse(dataList[6], 'y-m-d'));
										Ext.getCmp("examDate").setValue(trainingStartTime);
										if (dataList[6] != null&& dataList[6].length >= 10)
											Ext.getCmp("examDate").setValue(dataList[6].substring(0, 10));
										var secondTitle = '<div style="background:#f0f0f0;color:#333;width: 100%;height:36px;line-height:36px;font-weight:bolder;font-size:12px; margin-left: 1%;margin-top:20px">&nbsp;&nbsp;&nbsp;'
												+ '考场信息</div>';
										// 修改时，给时间标签HTML
										if (dataList[0] != '') {
											Ext.getCmp('bottles').setValue(dataList[8]);
											Ext.getCmp('bottles1').setValue(dataList[9]);
											Ext.getCmp('bottles2').setValue(dataList[10]);
											Ext.getCmp('bottles3').setValue(dataList[11]);
											Ext.getCmp('bottles4').setValue(dataList[8]);
											Ext.getCmp('bottles5').setValue(dataList[9]);
											Ext.getCmp('bottles6').setValue(dataList[10]);
											Ext.getCmp('bottles7').setValue(dataList[11]);
											// 添加时，给时间标签HTML
										} else {
											Ext.getCmp('bottles').setValue(dataList[8]);
											Ext.getCmp('bottles1').setValue(dataList[9]);
											Ext.getCmp('bottles2').setValue(dataList[10]);
											Ext.getCmp('bottles3').setValue(dataList[11]);
										}
										// 绑定页面修改信息表头HTML
										Ext.getCmp('secondTitle').update(
												secondTitle);
										Ext.getCmp('sitNumber').setValue(
												dataList[5]);
															}
								}
								
					} ]
				}).show();
		
		
		if (type == '1') {
			Ext.getCmp('affiliation').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('b0110Id').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('examNumber').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('examName').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('situs').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('examDate').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('sitNumber').setFieldStyle("border:1px solid #d0d0d0");
		}
		
		if(Ext.get("buttonSave").getHtml() == "保存"){
			Ext.get("iscontinue").show();
		}
	},
	setFocusObj : function(obj, time_vv) {
		fObjz = obj;
		time_r = time_vv;
	},
	IsInputTimeValue : function() {
		event.cancelBubble = true;
		var fObj = fObjz;
		if (!fObj)
			return;
		var cmd = event.srcElement.innerText == "5" ? true: false;
		if (fObj.value == "" || fObj.value.lenght <= 0)
			fObj.value = "0";
		var i = parseInt(fObj.value, 10);
		var radix = parseInt(time_r, 10) - 1;
		if (i == radix && cmd) {
			i = 0;
		} else if (i == 0 && !cmd) {
			i = radix;
		} else {
			cmd ? i++ : i--;
		}
		if (i == 0) {
			fObj.value = "00"
		} else if (i < 10 && i > 0) {
			fObj.value = "0" + i;
		} else {
			fObj.value = i;
		}
		fObj.select();
	},
	IsDigit : function() {
		return ((event.keyCode >= 48) && (event.keyCode <= 57));
	},
	saveExamHall : function(flag) {
		if (document.getElementById('buttonSave').innerHTML == '编辑') {
			document.getElementById('buttonSave').innerHTML='保存';
			Ext.getCmp('b0110Id').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('examNumber').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('examName').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('situs').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('examDate').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('sitNumber').setFieldStyle("border:1px solid #d0d0d0");
			Ext.getCmp('b0110Id').setReadOnly(false);
			Ext.getCmp('examNumber').setReadOnly(false);
			Ext.getCmp('examName').setReadOnly(false);
			Ext.getCmp('situs').setReadOnly(false);
			Ext.getCmp('examDate').setReadOnly(false);
			Ext.getCmp('sitNumber').setReadOnly(false);
			Ext.getCmp('timeedit').hide();
			Ext.getCmp('timeadd').show();

		} else {
			if (Ext.getCmp("bottles").getValue() > 23) {
				Ext.getCmp('bottles').setValue("00");
				Ext.showAlert("起始时间格式不正确");
				return;
			}
			if (Ext.getCmp("bottles2").getValue() > 23) {
				Ext.getCmp('bottles2').setValue("23");
				Ext.showAlert("结束时间格式不正确");
				return;
			}
			if (Ext.getCmp("bottles1").getValue() > 59) {
				Ext.getCmp('bottles1').setValue("00");
				Ext.showAlert("起始时间格式不正确");
				return;
			}
			if (Ext.getCmp("bottles3").getValue() > 59) {
				Ext.getCmp('bottles3').setValue("59");
				Ext.showAlert("结束时间格式不正确");
				return;
			}
			if (parseInt(Ext.getCmp("bottles").getValue(), 10) > parseInt(Ext.getCmp("bottles2").getValue(), 10)) {
				Ext.showAlert(EXAM_STARDATE_NOTBEFORE_ENDDATE);
				return;
			} else if (parseInt(Ext.getCmp("bottles").getValue(), 10) == parseInt(Ext.getCmp("bottles2").getValue(), 10)) {
				if (parseInt(Ext.getCmp("bottles1").getValue(), 10) > parseInt(parseInt(Ext.getCmp("bottles3").getValue(), 10))){
					Ext.showAlert(EXAM_STARDATE_NOTBEFORE_ENDDATE);
					return;
				}
			}
			var id = Ext.getCmp('id').getValue();
			var affiliation = Ext.getCmp('affiliation').getValue();
			var examNumber = Ext.getCmp('examNumber').getValue();
			var examName = Ext.getCmp('examName').getValue();
			var situs = Ext.getCmp('situs').getValue();
			var examDate = Ext.getCmp('examDate').getValue();
			var b0110Id = Ext.getCmp('b0110Id').getValue();
			var sitNumber = Ext.getCmp('sitNumber').getValue();
			var startTime = Ext.getCmp("bottles").getValue()+ ":"+ Ext.getCmp("bottles1").getValue();
			var endTime = 	Ext.getCmp("bottles2").getValue()+ ":"+ Ext.getCmp("bottles3").getValue();
			//时间验证  sunm add 2016-1-12
			var regResult = Ext.getCmp("bottles").validate() &&Ext.getCmp("bottles1").validate() 
							&&Ext.getCmp("bottles2").validate() && Ext.getCmp("bottles3").validate()
							&&Ext.getCmp('examDate').validate();
			if(!regResult)
				return;
			if (affiliation == "" || affiliation == null) {
				Ext.showAlert("招聘批次请选择");
				return;
			}
			if (b0110Id == "" || b0110Id == null||b0110Id == "`") {
				Ext.showAlert("所属单位为必填项，请录入");
				return;
			}
			if (examNumber == "" || examNumber == null) {
				Ext.showAlert("考场号为必填项，请录入");
				return;
			}
			var ze = /^[A-Za-z0-9]{1,50}$/;
			if(!ze.test(examNumber)){
				Ext.showAlert("考场号录入错误，只能录入50位以内的数字或英文");
				return;
			}
			if (examName == "" || examName == null|| examName.replace(/\s+/g,"")=="") {
				if(examName != "")
					Ext.showAlert("考场名称不能为空格，请重新录入");
				else
					Ext.showAlert("考场名称为必填项，请录入");
				return;
			}
			var regexp = new RegExp("[@#￥……&*]");
			if(regexp.test(examName)){
				Ext.showAlert("考场名称不要包含@#￥……&*");
				return;
			}
			if (sitNumber == "" || sitNumber == null) {
				Ext.showAlert("座位数为必填项，请录入");
				return;
			}
			var zeNum = /^[1-9]\d{0,3}$/;
			if(!zeNum.test(sitNumber)){
				Ext.showAlert("座位数录入不正确,只能录入大于零且是4位以内的数字！");
				return;
			}
			var mapexamId = new HashMap();
			mapexamId.put("affiliation", affiliation);
			mapexamId.put("examNumber", examNumber);
			mapexamId.put("sitNumber", sitNumber);
			mapexamId.put("type", "examId");
			if (id != null && id!='') {
				mapexamId.put("id", id);
			}
			var test = false;
			Rpc({
				functionId : 'ZP0000002504',
				async : false,
				success : function(form, action) {
					var result = Ext.decode(form.responseText);
					if (result.isExis == '1') {
						Ext.Msg.show({
							title: '提示信息',
							msg: "此招聘批次中存在此考场号,保存失败！",
							buttons: Ext.Msg.OK, 
							icon: Ext.Msg.INFO  
						});
						test = true;
						return;
					}
					if (result.ple == '1') {
						test = true;
						Ext.showConfirm("修改后座位数将少于本考场已分配的考生人数！ 确定要修改吗？",function(btn){
							if(btn == 'yes'){
								examhall.successfun(affiliation,examNumber,examName,situs,examDate,sitNumber,startTime,endTime,b0110Id,id);
							}
					});
				}
			  }
			}, mapexamId);
			
			if(!test){
				examhall.successfun(affiliation,examNumber,examName,situs,examDate,sitNumber,startTime,endTime,b0110Id,id);
			}
		}
	},
	successfun:function(affiliation,examNumber,examName,situs,examDate,sitNumber,startTime,endTime,b0110Id,id) {
			var map = new HashMap();
			map.put("affiliation", affiliation);
			map.put("examNumber", examNumber);
			map.put("examName", examName);
			map.put("situs", situs);
			examDate=examDate==null?"":examDate;
			map.put("examDate", examDate);
			map.put("sitNumber", sitNumber);
			map.put("startTime", startTime);
			map.put("endTime", endTime);
			map.put("b0110", b0110Id);
			if (id != null && id.length > 0) {
				map.put("type", "edit");
				map.put("id", id);
			} else {
				map.put("type", "add");
			}
			Rpc({
				functionId : 'ZP0000002504',
				async : false,
				success : function(form, action) {
					var result = Ext.decode(form.responseText);
					if (result.tip == '0') {
						Ext.showAlert("保存失败");
						return;
					} else {
						if(Ext.getDom("iscontinuech").checked==false){
							examhall_me.loadStore();
							Ext.getCmp('formId').close();
						}else{
							examhall.iscontinuehidden = "true";
							examhall.init();
							Ext.getCmp('formId').close();
						}
						
					}
				}
			}, map);
	},
	cancel : function() {
		Ext.getCmp('formId').close();
		examhall_me.loadStore();
	}
});