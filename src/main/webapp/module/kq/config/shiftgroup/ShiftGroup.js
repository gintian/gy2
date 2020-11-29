/**
 * 班组管理
 */
Ext.define('ShiftURL.ShiftGroup',{
	requires:['EHR.extWidget.field.CodeTreeCombox'],
	
	constructor:function(config) {
		shiftGroup = this;
		shiftGroup.nbases = "";
		shiftGroup.selectClassIds = new Array();
		// 固定班制 班次初始化 
		shiftGroup.selectFixedClassIdsMap = new HashMap();
		// 排班班制 班次初始化
		shiftGroup.selectCycleClassIdsMap = new HashMap();
		// 班组的排班对应数据
		shiftGroup.classidDescsMap = new HashMap();
		// 是否显示有效期内班组标识
		shiftGroup.validityflag = "1";
		// 权限 编辑 删除 班组成员维护
		shiftGroup.privs = null;
		// 班组或人员需要的其他参数信息
		shiftGroup.userInfo = null;
		// 节假日是否自动排休
		shiftGroup.rest_type = "1";
		this.init(shiftGroup.validityflag);
	},
	// 初始化函数
	init: function(validityflag) {
		var map = new HashMap();
		map.put("flag", ",init,");
		map.put("validityflag", validityflag);
	    Rpc({functionId:'KQ00021301',success:shiftGroup.loadeTable},map);
	},
	
	loadeTable: function(response){
		
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			shiftGroup.privs = map.privs;
			shiftGroup.userInfo = map.userInfo;
			
			var conditions = map.tableConfig;
			var tableConfig = Ext.decode(conditions);
			
			var tablePanel = shiftGroup.initTableConfig(tableConfig);
			if(Ext.getCmp("shiftGroup"))
				Ext.getCmp("shiftGroup").removeAll();	
			new Ext.Viewport({
				id:"shiftGroup",
				layout : "fit",
				items:[tablePanel]
			});
		} else
			Ext.showAlert(map.message);
	},
	initTableConfig : function(tableConfig){
		
		tableConfig.beforeBuildComp = function(grid){
			// 得到当前表格的列
			var cols = grid.tableConfig.columns;
			for(var i in cols){
				// 隐藏对操作列的列头功能
				if("borrowing_id" == cols[i].dataIndex){
					cols[i].menuDisabled=true;
					break;
				}
			}
		}
		shiftGroup.table = new BuildTableObj(tableConfig);
		var tablePanel = shiftGroup.table.getMainPanel();
		
		var validityChecked = 'checked="checked"';
		if(document.getElementById('vehicle')){
			if(document.getElementById('vehicle').checked)
				validityChecked = 'checked="checked"';
			else
				validityChecked = '';
		}
		var validityObj = { 
				xtype: 'tbtext', 
				id: 'tbText', 
				html: '<div style="height:25px;" >'
					+'<input type="checkbox" '+validityChecked+' style="cursor:pointer;vertical-align:middle;"'
					+'id="vehicle" name="vehicle" onclick="shiftGroup.checkedValidityGroup()"/>'
					+'<span style="line-height:25px;">'
					+'仅显示有效期内班组</span></div>'
			};
		Ext.getCmp("kqshiftgroup_01_toolbar").add('->');	
		Ext.getCmp("kqshiftgroup_01_toolbar").add(validityObj);
		
		return tablePanel;
	},
	
	checkedValidityGroup: function(){
		var validityBool = document.getElementById('vehicle').checked;
		// =0显示全部 =1显示有效期内班组
		shiftGroup.validityflag = (true==validityBool) ? "1" : "0";
		shiftGroup.init(shiftGroup.validityflag);
	},
	/**
	 * 展现班组信息窗口
	 */
	showGroupWin: function(jsonObj, isCreate){
		
		shiftGroup.class_ids_All = jsonObj.class_ids_All;
		// {1=[2`白班], 2=[], 3=[], 4=[5`晚班], 5=[], 6=[2`白班, 3`中班, 5`晚班]}
		shiftGroup.classidDescsMap = jsonObj.classid_descs;
		shiftGroup.rest_type = jsonObj.rest_type;
		var win = Ext.getCmp("addGroupWinId");
		if(win)
			win.close();
		
		var nameText = Ext.create('Ext.form.field.Text', {
			id:'nameId',
			fieldLabel: kq.label.name +'<font style="color:red;">*</font>',
			labelWidth:70,
			labelAlign:'left',
			width: 300,
			margin: '10 5 0 20',
			maxLength: 100,
			maxLengthText: kq.group.nameMaxLength,
			value: jsonObj.name,
			disabled: isCreate,
			allowBlank: false
			
		});
		// 默认显示7天
		if(Ext.isEmpty(jsonObj.shift_cycle))
			jsonObj.shift_cycle = '7';
		var shiftTypeBool = ("0"==jsonObj.shift_type) ? true : false;
		var typeRadio = Ext.create('Ext.form.Panel', {
			border: false,
			width: 550,
			margin: '10 5 0 20',
			layout: 'hbox',
            items:[{
            	xtype: 'radiogroup',
            	id: 'typeId',
            	disabled: isCreate,
            	fieldLabel : kq.group.shiftType +'<font style="color:red;">*</font>',
            	labelWidth:70,
            	labelSeparator: '',
            	labelAlign:'left',
            	//width: 260,
            	defaults: {
            		flex: 1
            	},
            	layout: 'hbox',
            	items: [{
            			boxLabel  : kq.group.fixed,
            			name      : 'shiftType',
            			inputValue: '0',
            			width: 120,
            			id        : 'fixedid',
            			checked : shiftTypeBool
            		}, {
            			width: 60,
            			boxLabel  : kq.group.cycle,
            			name      : 'shiftType',
            			inputValue: '1',
            			id        : 'cycleid',
            			checked : !shiftTypeBool
            		}],
            		listeners: {
	                    click: {
	                        element: 'el', 
	                        fn: function(){
	                        	var fixedBool = Ext.getCmp("fixedid").value;
	                        	var cycleBool = Ext.getCmp("cycleid").value;
	                        	if(fixedBool){
	                        		Ext.getCmp("calview").setHtml(shiftGroup.fixedTableHtml("1"));
	                        		Ext.getCmp("cycledayId").setHidden(true);
	                        	}else if(cycleBool){
	                        		Ext.getCmp("calview").setHtml(shiftGroup.cycleTableHtml("1", jsonObj.shift_cycle));
	                        		Ext.getCmp("cycledayId").setHidden(false);
	                        	}
	                        }
	                    }
            		}
            	},{
            		xtype: 'textfield',
            		id: 'cycledayId',
            		name: 'days',
            		regex: /^([1-9]$)|([1][0-4]$)/,
            		regexText: kq.group.checkInt,
                    fieldLabel: kq.group.cycledays,
                    labelWidth:60,
        			labelAlign:'left',
        			width: 100,
        			margin: '2 0 0 0',
        			value: jsonObj.shift_cycle,
        			hidden: shiftTypeBool,
                    allowBlank: false,
                    disabled: isCreate,
                    listeners: {
		            	change:function(field, newValue, oldValue){
		            		Ext.getCmp("calview").setHtml(shiftGroup.cycleTableHtml(shiftGroup.rest_type, newValue));
		            		if(newValue > 7)
		            			Ext.getCmp("addGroupWinId").setHeight(520);
		            	}
            		}
            	}
            ]
		});
		
		var typeHtml = Ext.create('Ext.panel.Panel', {
			border: false,
			margin: '5 5 0 40',
			width: '93%',
			id: 'calview',
			html: shiftTypeBool ? shiftGroup.fixedTableHtml(jsonObj.rest_type, isCreate) : shiftGroup.cycleTableHtml(shiftGroup.rest_type, jsonObj.shift_cycle, isCreate)
					
		});
		
		var chargePerson = Ext.create('Ext.panel.Panel', {
			height: 70,
		    width: 600,
		    margin: '5 5 0 20',
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
		    	xtype: 'label',
		        forId: 'myFieldId',
		        height: 58,
		        width: 70,//vertical-align:middle;top:50%;
		        html: '<div style="position:absolute;height:56px;margin-top:20px;text-align:left;">'+kq.group.personCharge
		        		+'<font style="color:red;">*</font></div>',//负责人
		        margin: '5 5 0 0'
		    }
		    ,{
				xtype: 'panel',
				layout: {
			        align: 'center',
			        pack: 'center',
			        type: 'vbox'
				},
				border: false,
				width: 60,
				height: 70,
				items:[{
					xtype:'image',
					id:'chargePersonId',
					disabled: isCreate,
					width: 50,
					height: 50,
					style: "border-radius:50%;cursor:pointer;",
					src: jsonObj.photourl,
					margin: '5 5 0 0',
					listeners: {
						click: {
				            element: 'el', 
				            fn: function(e){ 
				            	shiftGroup.selectPerson(this, jsonObj.nbases ,1);
			            	}
				        }
					}
				},{
					xtype:'label',
					id:'personNameId',
					margin: '0 5 0 0',
					html: jsonObj.admin_name
				}]
			}
		    ,{
				xtype:'textfield',
				id:'chargePersonNameId',
				hidden: true,
				value:jsonObj.admin_name+","+jsonObj.user_id
			}]
		});
		
//		var nowDate = Ext.Date.format(new Date(), 'Y-m-d');
		// 起始日期
		var beginDate = Ext.create('Ext.form.field.Date', {
			id:'beginDateId',
			fieldLabel: kq.group.startdate+'<font style="color:red;">*</font>',//'起始日期'
			labelAlign:'left',
			labelWidth:70,
			width: 300,
			value: jsonObj.start_date,
			format:'Y-m-d',
			margin: '5 5 0 20',
			disabled: isCreate,
			allowBlank:false
		});
		// 终止日期
		var endDate = Ext.create('Ext.form.field.Date', {
			id:'endDateId',
			fieldLabel: kq.group.enddate,//'终止日期'
			labelAlign:'left',
			labelWidth:70,
			width: 300,
			value: jsonObj.end_date,
			format:'Y-m-d',
			margin: '5 5 0 20',
			disabled: isCreate,
			allowBlank:false
		});
		var title = kq.group.newlyBuild;//新建
		if(!Ext.isEmpty(jsonObj.group_id))
			title = kq.group.edit;//"编辑"
		win = Ext.create('Ext.window.Window', {
			id: 'addGroupWinId',
		    title: title + kq.group.info,
		    height: 500,
		    width: 720,
		    modal: true,
		    resizable: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'vbox'
			},
			items:[
				nameText
				,typeRadio
				,typeHtml
				,chargePerson
				,{
            		xtype:'codecomboxfield',
		        	width:300,
		        	labelWidth:70,
		        	margin:"5 5 0 20",
		        	onlySelectCodeset:false,
		        	codesetid:"UM",
		        	ctrltype:'3',
		        	nmodule:"11",
		        	disabled: isCreate,
		        	fieldLabel: kq.scheme.organization +'<font style="color:red;">*</font>',
					listeners : {
						afterrender: function(){
							this.setValue(jsonObj.org_id, true);
						},
						select:function(a,b){
							jsonObj.org_id = b.get('id') + "`" + b.get('text');
		                }
					}
				}
				,beginDate
				,endDate
				],
			buttonAlign: 'center',
			buttons: [{
		    	text: kq.button.ok,//确定
		    	hidden: isCreate,
		    	handler:function(){
		    		jsonObj.type = "save";
		    		jsonObj.shift_data = "";
		    		// 固定班制
		    		var fixedBool = Ext.getCmp("fixedid").value;
		    		jsonObj.shift_type = "0";
		    		// 节假日是否排休
		    		var holidayFlag = document.getElementById("holidayId").checked;
		    		jsonObj.rest_type = "1";
		    		if(holidayFlag == false)
		    			jsonObj.rest_type = "0";
		    		
		    		shiftGroup.rest_type = jsonObj.rest_type;
		    		if(fixedBool){
		    			// 固定班制数据 周一到周日
		    			var map = shiftGroup.selectFixedClassIdsMap;
		    			jsonObj.shift_data = shiftGroup.ifnull(map["monid"])+";"+shiftGroup.ifnull(map["tueid"])+";"+shiftGroup.ifnull(map["wedid"])
		    							+";"+shiftGroup.ifnull(map["thuid"])+";"+shiftGroup.ifnull(map["friid"])+";"+shiftGroup.ifnull(map["satid"])
		    							+";"+shiftGroup.ifnull(map["sunid"]);
		    		}
		    		// 排班制
                	var cycleBool = Ext.getCmp("cycleid").value;
                	if(cycleBool){
                		jsonObj.shift_type = "1";
                		var cycleValue = Ext.getCmp("cycledayId").getValue();
                		if(cycleValue < 1 || cycleValue > 14){
    		    			Ext.showAlert(kq.group.cycledaysInfo);//"每周期天数应在1-14天内！"
    						return;
    		    		}
                		jsonObj.shift_cycle = cycleValue;
                		var map = shiftGroup.selectCycleClassIdsMap;
                		for(i=1;i<(Number(jsonObj.shift_cycle)+1);i++){
                			jsonObj.shift_data += shiftGroup.ifnull(map["cycleid"+i]) + ";"
                		}
                		if(!Ext.isEmpty(jsonObj.shift_data))
                			jsonObj.shift_data = jsonObj.shift_data.substring(0, jsonObj.shift_data.length-1);
		    		}
                	
                	jsonObj.name = Ext.getCmp("nameId").getValue();
                	if(Ext.isEmpty(jsonObj.name)){
		    			Ext.showAlert(kq.group.nameIsNotNull);//"班组名称不能为空！"
						return;
		    		}
                	if(jsonObj.name.length > 100){
		    			Ext.showAlert(kq.group.nameMaxLength);//"班组名称长度不能超过200个英文字符或100个汉字！"
						return;
		    		}
//                	admin_id  admin_name
                	var nameid = Ext.getCmp("chargePersonNameId").getValue();
                	jsonObj.admin_name = nameid.split(",")[0];
                	jsonObj.user_id = nameid.split(",")[1];
                	if(Ext.isEmpty(jsonObj.admin_name) || Ext.isEmpty(jsonObj.user_id)){
                		Ext.showAlert(kq.group.adminIsNotNull);//"负责人不能为空！"
						return;
                	}
                	// 53337 兼容老的程序班组防止有单独 ` 
                	if(Ext.isEmpty(jsonObj.org_id) || "`"==jsonObj.org_id){
		    			Ext.showAlert(kq.group.orgIsNotNull);// "所属机构不能为空！"
						return;
		    		}
                	
		    		var beginDate = Ext.getCmp('beginDateId').getValue();
		    		if(Ext.isEmpty(beginDate)){
		    			Ext.showAlert(kq.group.startIsNotNull);// "起始日期不能为空！"
						return;
		    		} else {
						jsonObj.start_date = Ext.Date.format(beginDate,'Y-m-d');
						if(Ext.isEmpty(jsonObj.start_date)) {
							//起始日期格式错误
							Ext.showAlert(kq.group.startdate + kq.group.formatError);
							return;
						}
					}

		    		var endDate = Ext.getCmp('endDateId').getValue();
		    		// 若不设置终止日期  则默认为最大
		    		if(Ext.isEmpty(endDate)){
		    			endDate = "9999-12-31";
						jsonObj.end_date = endDate;
		    		}else if(endDate <= beginDate){
		    			// 终止日期早于或等于起始日期，请重新设置！
		    			Ext.showAlert(kq.group.dateCheckInfo);
						return;
		    		} else {
						jsonObj.end_date = Ext.Date.format(endDate,'Y-m-d');
						if(Ext.isEmpty(jsonObj.end_date)) {
							//终止日期格式错误
							Ext.showAlert(kq.group.enddate + kq.group.formatError);
							return;
						}
					}

					var map = new HashMap();
					map.put("jsonStr", JSON.stringify(jsonObj));
					Rpc({functionId:'KQ00021302',success:function(form, action){
				    	var result = Ext.decode(form.responseText);
						if(result.succeed == true) {
							var jsonObj = Ext.decode(result.returnStr);
							if(jsonObj.return_code == "success"){
								
								Ext.showAlert(kq.label.saveSuccess, function(){
									Ext.getCmp('addGroupWinId').close();
								}, this);//"保存成功！"
								shiftGroup.init(shiftGroup.validityflag);
							}else{
								Ext.showAlert(jsonObj.return_msg);
								return;
							}
						}else {
							Ext.showAlert(result.message);
							return;
						}
					}},map);
		    	}
			},{
		    	text: kq.button.cancle,//'取消',
		    	handler:function(){
		    		win.close();
			    }
			}],
            listeners: {
				beforeclose:function(){
					// 监听选人控件窗口，如果未关闭则先关闭该窗口
					var win = Ext.getCmp('person_picker_single_view');
					if(win)
						win.close();
				}
	        }
		});
		
		win.show();
	},
	/**
	 * 处理空对象返回''
	 */
	ifnull:function(str){
		return Ext.isEmpty(str) ? "" : str ;
	},
	/**
	 * 排班班制
	 */
	cycleTableHtml:function(rest_type, cycledays, isCreate){
		if(Ext.isEmpty(cycledays))
			return "";
		
		var checkedbox = "0" == rest_type ? "" : "checked='checked' ";
		var func = "";
		var checkDisabled = " disabled=true";
		if(!isCreate){
			func = "onclick='shiftGroup.showAllClass(this.id)'";
			checkDisabled = "";			
		}
		
		var days = Number(cycledays);
		var theRows = parseInt(days / 7);
		var mod = days % 7;
        if (mod > 0) 
            theRows = theRows + 1;
        
		var endtr = "</tr>";
		var num = 0;
		var oneContent = "";
		var twoContent = "";
		
		for (var i = 0; i < theRows; i++) {
			var onetr = "<tr  height='30px' align='center' >";
			var oneshifttr = "<tr height='60px' align='center'>";
			var onetd = "";	
			var oneshifttd = "";	
            for (var j = 0; j < 7; j++) {
            	var day = i*7 + j+1;
            	if(day > days)
            		break;
            	if(day%7 == 0 || day == days){
            		onetd += "<td class='TableRow_top' width='84px'>第"+ day +"天</td>";
                	oneshifttd += "<td width='84px' id='cycleid"+day+"' style='cursor:pointer;' "+func+">" 
			                	+ shiftGroup.setClassIdObj(day, "cycleid"+day)
			                	+"</td>";
            	}else{
            		onetd += "<td class='TableRow_rb' width='85px'>第"+ day +"天</td>";
            		oneshifttd += "<td class='TableRow_right' width='85px' id='cycleid"+day+"' "+func+">"
			            		+ shiftGroup.setClassIdObj(day, "cycleid"+day)
			            		+ "</td>";
            	}
            }
            onetr += onetd + endtr;
            oneshifttr += oneshifttd + endtr;
            if(i == 0)
            	oneContent += onetr + oneshifttr;
            if(i == 1)
            	twoContent += onetr + oneshifttr;
		}
		
		var html = "<div style='' width='100%' >"
			+ "<table class='TableRow1'  border='1' cellspacing='0' cellpadding='0' style='font-size:14px;'>"	
			+ oneContent
			+ "</table>";
		
		if(!Ext.isEmpty(twoContent)){
			html += "<table class='TableRow2'  border='0' cellspacing='0' cellpadding='0' style='font-size:14px;' >"	
				+ twoContent
				+ "</table>";
		}
		
		html += "<div style='margin-top:5px;' >"; 
		html += "<input type='checkbox'style='vertical-align:middle;' onclick='shiftGroup.changeRestType();' ";
		html += "id='holidayId' name='ty' value='1' "+ checkedbox + checkDisabled +" />";
		html += "<font style='vertical-align:middle;'> "+ kq.group.holidayShift +"</font>";
		html += "</div>";
		html += "</div>";
		
		return html;
	},
	/**
	 * 固定班制
	 */
	fixedTableHtml:function(restType, isCreate){
		
		var checkedbox = "0"==restType ? "" : "checked='checked' ";
		var func = "";
		var checkDisabled = " disabled=true";
		if(!isCreate){
			func = "onclick='shiftGroup.showAllClass(this.id)'";
			checkDisabled = "";			
		}
		var html = "<div style='' >"
			+"<table class='TableRow1' width='100%' border='1' cellspacing='0' cellpadding='0' style='font-size:14px;' >"//color:#979797;
			+"<tr  height='30px' align='center' >"
			+"<td class='TableRow_rb'>周一</td><td class='TableRow_rb'>周二</td><td class='TableRow_rb'>周三</td>" +
					"<td class='TableRow_rb'>周四</td><td class='TableRow_rb'>周五</td><td class='TableRow_rb'>周六</td><td class='TableRow_top'>周日</td>"
			+"</tr>"
			+"<tr height='60px' align='center'>"+
				"<td class='TableRow_right fixedTable_td' id='monid' "+func+">"+shiftGroup.setClassIdObj(1, 'monid')+"</td>" +
				"<td class='TableRow_right fixedTable_td' id='tueid' "+func+">"+shiftGroup.setClassIdObj(2, 'tueid')+"</td>" +
				"<td class='TableRow_right fixedTable_td' id='wedid' "+func+">"+shiftGroup.setClassIdObj(3, 'wedid')+"</td>" +
				"<td class='TableRow_right fixedTable_td' id='thuid' "+func+">"+shiftGroup.setClassIdObj(4, 'thuid')+"</td>" +
				"<td class='TableRow_right fixedTable_td' id='friid' "+func+">"+shiftGroup.setClassIdObj(5, 'friid')+"</td>" +
				"<td class='TableRow_right fixedTable_td' id='satid' "+func+">"+shiftGroup.setClassIdObj(6, 'satid')+"</td>" +
				"<td class='fixedTable_td' id='sunid' style='cursor:pointer;' "+func+">"
				+shiftGroup.setClassIdObj(7, 'sunid')+"</td>"
			+"</tr>"
			+"</table>"
			+"<div style='margin-top:5px;' >" 
				+"<input type='checkbox'style='vertical-align:middle;' onclick='shiftGroup.changeRestType();' id='holidayId' name='ty' value='1' "+ checkedbox + checkDisabled +" />"
				+"<font style='vertical-align:middle;'> "+ kq.group.holidayShift +"</font>"
			+"</div>"
			+"</div>";
		
		return html;
	},
	
	changeRestType: function () {
		var holidayFlag = document.getElementById("holidayId").checked;
		if(holidayFlag)
			shiftGroup.rest_type = "1";
		else
			shiftGroup.rest_type = "0";
	},
	/**
	 * 为班次单元格赋值
	 */
	setClassIdObj:function(indexNum, setid){
		// {1=[2`白班], 2=[], 3=[], 4=[5`晚班], 5=[], 6=[2`白班, 3`中班, 5`晚班]}
		var html = "";
        var classids = "";
        var sel = shiftGroup.classidDescsMap[indexNum];
        if(!Ext.isEmpty(sel)){
        	html = "<table border='0' cellspacing='0' cellpadding='0' style='font-size:12px;' >";
        	Ext.Array.each(sel,function(record,index){
        		html += "<tr><td>"
        			+ record.split("`")[1]//record.data.itemdesc 
        			+"</td></tr>";
        		classids += record.split("`")[0] + ","
        	});
        	html += "</table>";
        }
        // 拼单元格所选班次
//        document.getElementById(setid).innerHTML = html;
//        班次保存格式7天的班次数据以分号隔开，每天的班次以逗号隔开1,2,3;1;2,3;..
        if(!Ext.isEmpty(classids))
        	classids = classids.substring(0, classids.length-1);
        // 排班制 id包含cycleid
        if(setid.indexOf('cycleid') != -1)
        	shiftGroup.selectCycleClassIdsMap.put(setid, classids);
        else
        	shiftGroup.selectFixedClassIdsMap.put(setid, classids);
        
        return html;
	},
	/**
	 * 选人组件
	 */
	selectPerson:function (object, nbases, type) {
		var picker = new PersonPicker({
			isZoom : true,
			multiple : false,
			deprecate : [],
			nbases : nbases,
			selectByNbase : (nbases.split(',').length > 2),// 如果只有一个权限库不显示库节点
			orgid : shiftGroup.userInfo.orgid,
			isPrivExpression : false,
			callback : function(person) {
				// 显示人员头像
				Ext.getCmp("chargePersonId").setSrc(person.photo);
				Ext.getCmp("personNameId").setHtml(person.name);
				// 返回人员姓名与人员id
				Ext.getCmp("chargePersonNameId").setValue(person.name+","+person.id);
			}
		}, object);
		
		picker.open();
	},
	/**
	 * 渲染操作列
	 */
	groupHandleFunc:function(value, metaData, Record){
		// 登录用户是否为创建人
		var isCreate = !(Record.data.create_user==shiftGroup.userInfo.user_name);
		// 登录用户是否为负责人
		var isAdmin = (Record.data.admin_id_e==shiftGroup.userInfo.user_guidkey);
		var groupId = Record.data.group_id_e;
		
		var func = "<table width='90%' border='0' cellspacing='0' align='left' cellpadding='0' style='margin:0 10px 0 10px'>" +
					"<tr align='left' >" ;
		// 编辑权限
		if("1" == shiftGroup.privs.editorpriv){// && !isCreate
			var editStr = "<a href=javascript:void(0); onclick=shiftGroup.showShiftGroup('"+groupId+"');>"+ kq.group.edit +"</a>";//,"+isCreate+"
			func += "<td width='30%'>"+ editStr +"</td>";
		}
		// 删除权限
		if("1" == shiftGroup.privs.delpriv){// && !isCreate
			delStr = "<a href=javascript:void(0); onclick=shiftGroup.delShiftGroup('"+groupId+"');>"+ kq.label.del +"</a>";
			func += "<td width='30%'>"+ delStr +"</td>";
		}
		func += "<td>";
		if(isAdmin){
			// 排班类型
			if("1" == Record.data.shift_type){
				// 排班
				if("1" == shiftGroup.privs.shiftpriv){
					var renderPerNumid = Record.data.group_id_e +"_numid";					
					func += "<a href=javascript:void(0); onclick=shiftGroup.paiban('"+groupId+"','"+Record.data.org_id+"','"+renderPerNumid+"');>"+ kq.label.shift +"</a>";
				}
			}else{
				// 班组选人
				if("1" == shiftGroup.privs.changepriv)
					func += "<a href=javascript:void(0); onclick=shiftGroup.getShiftEmpTableConfig('"+groupId+"','"+Record.data.org_id+"');>"
							+ kq.shift.person +"</a>";
			}
		}
		func += "</td>" +
				"</tr></table>";
		return func;
	},
	/**
	 * 班组名称渲染函数
	 */
	groupNameFunc:function(value, metaData, Record){
		var func = value;
		// 创建人控制
//		var isCreate = !(Record.data.create_user==shiftGroup.userInfo.user_name);
		// 默认只读
		var isCreate = true;
		// 校验编辑的功能权限
		if("1" == shiftGroup.privs.editorpriv)
			isCreate = false;
		func = "<a href=javascript:void(0); onclick=shiftGroup.showShiftGroup('"+Record.data.group_id_e+"',"+isCreate+");>"
				+ value +"</a>";
		return func;
	},
	/**
	 * 新建/编辑班组
	 */
	showShiftGroup:function(groupId, isCreate){
		var json = {};
		json.type = "detail";
		json.group_id = groupId;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021302',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				var jsonObj = Ext.decode(result.returnStr);
				if(jsonObj.return_code == "fail"){
					Ext.showAlert(jsonObj.return_msg);
					return;
				}
				// 弹出班组详细信息 窗口
				shiftGroup.showGroupWin(jsonObj, isCreate);
			}else {
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	/**
	 * 删除班组
	 */
	delShiftGroup:function(groupId){
		
		Ext.showConfirm(kq.group.checkDelInfo, function(btn) {
			if (btn == 'yes') {
				
				var json = {};
				json.type = "delete";
				json.group_id = groupId;
				var map = new HashMap();
				map.put("jsonStr", JSON.stringify(json));
				Rpc({functionId:'KQ00021302',success:function(form, action){
					var result = Ext.decode(form.responseText);
					if(result.succeed == true) {
						Ext.showAlert(kq.label.deleteSuccess);
						shiftGroup.init(shiftGroup.validityflag);
					}else {
						Ext.showAlert(kq.label.deleteFail);
					}
				}},map);
			}
		}, this);
	    
	},
	
	/**
	 * 展示班次窗口
	 */
	showAllClass: function(setid) {
		
		var list = new Array();
		var mapList = new Array();
		mapList = shiftGroup.class_ids_All;
		for (var i = 0; i < mapList.length; i++) {
			var map= mapList[i];
			for(var key in map) {
				if(map.hasOwnProperty(key)) {
					var mapJust = new HashMap();
					mapJust.put("itemid",key);
					var itemdesc = "";
					itemdesc = map[key].split(",")[0];
					mapJust.put("itemdesc_value",itemdesc);
					mapJust.put("itemdesc",map[key]);
					list.push(mapJust);
				}
			}
		}
		
		var store = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc_value','itemdesc'],
            data: list,
            autoLoad: true
        });
        //生成表格
        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: 245,
            height: 325,
            style: 'margin-left:5px;',
            forceFit:true,
            selModel: Ext.create("Ext.selection.CheckboxModel", {
            	// 50405 只允许通过复选框选中
            	checkOnly:true
            }),
            columns: [{
            	text: kq.label.name,
            	border:false,
            	menuDisabled:true, 
            	dataIndex: 'itemdesc_value',
            	width: 260}
            ],
            listeners : {
				afterrender: function(){
					var arrRecords = new Array();
					var selectids = "";
					if(setid.indexOf('cycleid') != -1)
						selectids = shiftGroup.selectCycleClassIdsMap[setid];
		            else
		            	selectids = shiftGroup.selectFixedClassIdsMap[setid];
					if(!Ext.isEmpty(selectids)){
						var selectModel = panel.getSelectionModel();
						panel.store.data.each(function(record,index){
							var itemid = record.data.itemid;
							if((","+selectids+",").indexOf(','+itemid+",") != -1)
								arrRecords.push(record); 
						});
						selectModel.select(arrRecords,false,true);
					}
				}
			}
        });
        
		var classWin = Ext.widget("window", {
            title: kq.scheme.select,//'选择班次',
            id: 'classWinid',
            resizable: false,
            border: false,
            modal: true,
            width: 260,
            height: 400,
            closeAction: 'destroy',
            padding: '5 0 0 0',
            items: [panel],
            minButtonWidth:50,
            buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.ok,//确定
                    handler:function(){
                        var sel = panel.selModel.selected.items;
                        if(sel.length > 3){
                        	Ext.showAlert(kq.group.limitShiftsNum);//"每天最多允许设置3个班次！"
        					return;
                        }
                        var html = "<table border='0' cellspacing='0' cellpadding='0' style='font-size:12px;' >";
                        var classids = "";
                        Ext.Array.each(sel,function(record,index){
                        	
                        	//record.data{itemid: "5", itemdesc_value: "晚班", itemdesc: "晚班", id: "extModel135-5"}
                        	html += "<tr><td>"
                        			+ record.data.itemdesc 
                        			+"</tr></td>";
                        	classids += record.data.itemid + ","
                        });
                        html += "</table>";
                        // 拼单元格所选班次
                        document.getElementById(setid).innerHTML = html;
//                        班次保存格式7天的班次数据以分号隔开，每天的班次以逗号隔开1,2,3;1;2,3;..
                        if(!Ext.isEmpty(classids))
                        	classids = classids.substring(0, classids.length-1);
                        // 排班制 id包含cycleid
                        if(setid.indexOf('cycleid') != -1)
                        	shiftGroup.selectCycleClassIdsMap.put(setid, classids);
                        else
                        	shiftGroup.selectFixedClassIdsMap.put(setid, classids);
                        classWin.close();
                    }
                },
                {
                    text:common.button.cancel,//取消
                    handler:function(){
                    	classWin.close();
                    }
                },{xtype:'tbfill'}
            ]
            
		});
		classWin.show();
	},
	
	paiban: function(groupId, orgId, renderPerNumid){
		orgId = encodeURIComponent(orgId);
		var win = new Ext.Window({
			id:"shiftWin",
			html: '<iframe style="border:none;" src="/module/kq/config/shiftgroup/Shift.html'
				+'?groupId=' + groupId+'&orgId='+orgId + '&renderPerNumid='+renderPerNumid +'" frameborder="0" width="100%" height="99%" scrolling="no">'
				+'</iframe>',
			layout:'fit',
			renderTo : Ext.getBody(),
			header:false,
			maximized : true,
			border : false,
			closable : false,
			autoScroll : true
		});
		
		win.show();
	},
	
	getShiftEmpTableConfig:function(groupId, orgId){
		var map = new HashMap();
    	map.put("groupId", groupId);
    	map.put("orgId", orgId);
    	map.put("operation", "groupPerson");
    	
        Ext.require('ShiftURL.Shift', function() {
        	Ext.create("ShiftURL.Shift", map);
        });
	},
	/**
	 * 渲染人数列 排班后返回需定位到当前页 故重新渲染人数
	 * linbz
	 */
	renderPerNum:function(value, metaData, Record){
		value = Ext.isEmpty(value) ? 0 : value;
		return "<div id='"+Record.data.group_id_e+"_numid' >"+ value +"</div>";
	}
});