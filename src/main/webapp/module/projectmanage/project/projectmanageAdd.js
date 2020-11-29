/**
 * 员工项目工添加 ly 2015-12-03
 * 
 */
Ext.define('ProjectManageTemplateUL.projectmanageAdd',{
	constructor : function(config) {
		ManProjectHours_me = this;
		ManProjectHours_me.title = '';
		ManProjectHours_me.ids = '';
		ManProjectHours_me.addposA01 = new Array();
		ManProjectHours_me.reposA01 = ''; // 项目负责人
		ManProjectHours_me.reposA01List = new Array(); // 项目负责人
		ManProjectHours_me.indexAd = 0;
		ManProjectHours_me.width = '';
		ManProjectHours_me.nameList = '';
		ManProjectHours_me.hideStrs = '';
		ManProjectHours_me.showBar = '';
		ManProjectHours_me.type = config.type;
		ManProjectHours_me.projectId = '';
		ManProjectHours_me.modifyPermissions = '';
		ManProjectHours_me.beginTime = '';
		ManProjectHours_me.endTime = '';
		if("edit"==ManProjectHours_me.type||"landMarkEdit"==ManProjectHours_me.type||"landMarkAdd"==ManProjectHours_me.type){
			ManProjectHours_me.projectId = config.projectId+'';
			if("landMarkEdit"==ManProjectHours_me.type){
				ManProjectHours_me.landMarkId = config.landMarkId;
			}
		}
		ManProjectHours_me.readOnlyStrs ='';
		ManProjectHours_me.formiterms =new Array();
		ManProjectHours_me.init();
	},
	// 初始化函数
	init : function() {
		Ext.util.CSS.removeStyleSheet('treegridImg');
		Ext.util.CSS.removeStyleSheet('gridCell');
		var map = new HashMap();
		ManProjectHours_me.width = document.body.clientWidth * 0.96;
		
		if ("edit"==ManProjectHours_me.type||"landMarkEdit"==ManProjectHours_me.type||"landMarkAdd"==ManProjectHours_me.type) {
			map.put("projectId", ManProjectHours_me.projectId);
			if("landMarkEdit"==ManProjectHours_me.type){
				map.put("landMarkId", ManProjectHours_me.landMarkId);
			}
		};
		map.put("type", ManProjectHours_me.type);
		Rpc({
			functionId : 'PM00000100',
			success : ManProjectHours_me.getTableOK
		}, map);
	},
	// 加载表单
	getTableOK : function(form, action) {
		var result = Ext.decode(form.responseText);
		conditions = result.listValues;
		ManProjectHours_me.modifyPermissions = result.modifyPermissions;
		if("edit"==ManProjectHours_me.type){
			projectList = result.projectList;
			manList = result.manList;
		}
		
		if("landMarkEdit" == ManProjectHours_me.type){
			projectList = result.landMarkList;
			ManProjectHours_me.beginDate = result.beginDate;
			ManProjectHours_me.endDate = result.endDate;
		}
		
		if("landMarkAdd"== ManProjectHours_me.type){
			ManProjectHours_me.beginDate = result.beginDate;
			ManProjectHours_me.endDate = result.endDate;
		}
		
		var showMembers = "";
		ManProjectHours_me.formiterms = conditions;
		
		if ("add" == ManProjectHours_me.type) {
			organizationsName = result.organizationsName;
			// 隐藏
			ManProjectHours_me.hideStrs = ',p1101,p1113,p1115,p1117,';
			ManProjectHours_me.title = '新增项目';
		}else if("edit" == ManProjectHours_me.type){
			// 隐藏
			ManProjectHours_me.hideStrs = ',p1101,';
			//只读
			ManProjectHours_me.readOnlyStrs = ',p1113,p1115,p1117,';
			ManProjectHours_me.title = '修改项目';
		}else if("landMarkAdd" == ManProjectHours_me.type){
			//隐藏
			ManProjectHours_me.hideStrs = ',p1101,P1201,p1213,p1215,p1217,';
			ManProjectHours_me.title = '新增里程碑';
		}else if("landMarkEdit" == ManProjectHours_me.type){
			// 隐藏
			ManProjectHours_me.hideStrs = ',p1101,P1201,';
			//只读
			ManProjectHours_me.readOnlyStrs = ',p1213,p1215,p1217,';
			ManProjectHours_me.title = '修改里程碑';
		}
		ManProjectHours_me.width = document.body.clientWidth;
		var saveButton = '';
		
		if('YES'== ManProjectHours_me.modifyPermissions){
			saveButton= '<a id="buttonSave"  href="javascript:ManProjectHours_me.save();" >'
			+ '保存'
			+ '</a>'; 
		}
		
		var win = new Ext.window.Window(
				{
					title : '<div style="float:left">'
							+ ManProjectHours_me.title
							+ '</div>'
							+ '<div id="titilPanel" style="font-weight:normal;float:right;padding-right:70px">'
							+ saveButton
							+ '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="ManProjectHours_me.cancel()" >取消</a>'
							+ '</div>',
					renderTo : Ext.getBody(),
					maximized : true,
					border : false,
					id : 'formId',
					closable : false,
					autoScroll : true,
					layout : {
						type : "vbox",
						align : "left"
					},
					defaults : {
						border : false,
						padding:'10 0 0 50'
					}
				}).show();
		
		if(!win)
			win = Ext.getCmp("formId");
		
		for ( var i = 0; i < conditions.length; i++) {
			ManProjectHours_me.nameList = ManProjectHours_me.nameList+ conditions[i].itemid + '`';
			if (("A" == conditions[i].columnType || "a" == conditions[i].columnType)&& '0' == conditions[i].codesetId) {
				if ("p1121" == conditions[i].itemid) {
					var p1121 = Ext.create('Ext.panel.Panel',{
						border : false,
						items : [ {
							xtype : 'codecomboxfield',
							fieldLabel : "<span Style='padding-right:10px;'> </span>" + conditions[i].itemdesc,
							id : 'p1121',
							labelSeparator : null,
							codesetid : "UM",
							onlySelectCodeset:false,
							width : 405,
							//editable:false,
							nmodule:'4',
							ctrltype:'3',
							fieldStyle : 'NO'== ManProjectHours_me.modifyPermissions ? 'background-color:rgb(238, 238, 238)': '',
							readOnly : 'NO'== ManProjectHours_me.modifyPermissions ? true: false
						}]
					})
					win.insert(p1121);
				} else {
					var textfield = Ext.create('Ext.form.TextField',{
						fieldLabel : "<span Style='color:red;padding-right:5px;'>*</span>"+ conditions[i].itemdesc,
						border : false,
						id : conditions[i].itemid,
						labelSeparator : null,
						width : 405,
						itemLength:conditions[i].itemLength,
						fieldStyle : 'NO'== ManProjectHours_me.modifyPermissions ?'background-color:rgb(238, 238, 238)': '',
					    readOnly : 'NO'== ManProjectHours_me.modifyPermissions ? true: false,
						validator:function(value){
							if(value.replace(/[\u4E00-\u9FA5]/g,'aa').length>this.itemLength){
								return conditions[i].itemdesc + "超出可以输入字符长度，长度为"+this.itemLength/2+"汉字或"+this.itemLength+"字母或数字！";
							}
							return true;
						},
						enableKeyEvents:true,
						listeners:{
							"change": function (obj, newvalue) {
								if("p1103" == obj.getId() || "p1203" == obj.getId()){
									newvalue=trimStr(newvalue);
									obj.setValue(newvalue.replace(/\s/g,""));
								}
							}
						}
					})
					win.insert(textfield);
				}
			}
			
			if ("D" == conditions[i].columnType|| "d" == conditions[i].columnType) {var panel_i = Ext.create('Ext.panel.Panel',{
					border : false,
					items : [ {
						xtype : 'datetimefield',
						fieldLabel : "<span Style='color:red;padding-right:5px;'>*</span>"+ conditions[i].itemdesc,
						id : conditions[i].itemid,
						labelSeparator : null,
						format : 'Y-m-d',
						width : 300,
						allowBlank:false,
						fieldStyle : 'NO'== ManProjectHours_me.modifyPermissions ? 'background-color:rgb(238, 238, 238)': '',
						readOnly : 'NO'== ManProjectHours_me.modifyPermissions ? true: false,
						validator: function (value) {
							if("p1107"==this.id){
								var p1109 = Ext.getCmp("p1109").getValue();
								if(p1109!=''&&p1109<value){
									return "开始日期不能晚于结束日期！"
								}
							}
							if("p1109"==this.id){
								var p1107 = Ext.getCmp("p1107").getValue();
								if(p1107!=''&&p1107>value){
									return "开始日期不能晚于结束日期！"
								}
							}
							if("p1207"==this.id){
								var p1209 = Ext.getCmp("p1209").getValue();
								if(Ext.isEmpty(value))
									return;
								var startDate = Ext.Date.parse(value,"Y-m-d");
					 			var endDate =  Ext.Date.parse(p1209,"Y-m-d");
					 			var projectStartDate = Ext.Date.parse(ManProjectHours_me.beginDate,"Y.m.d");
					 			var projectEndDate = Ext.Date.parse(ManProjectHours_me.endDate,"Y.m.d");
					 			if(startDate < projectStartDate){
									return "里程碑起始日期不能早于项目开始日期 ！";
								}
								
								if(startDate > projectEndDate){
									return "里程碑起始日期不能晚于项目结束日期 ！";
								}
								
								if((!Ext.isEmpty(p1209))&&(startDate > endDate)){
									return "里程碑起始日期不能晚于结束日期！";
								}
								
							}
							if("p1209"==this.id){
								var p1207 = Ext.getCmp("p1207").getValue();
								if(Ext.isEmpty(value))
									return;
								var startDate = Ext.Date.parse(p1207,"Y-m-d");
					 			var endDate =  Ext.Date.parse(value,"Y-m-d");
					 			var projectStartDate = Ext.Date.parse(ManProjectHours_me.beginDate,"Y.m.d");
					 			var projectEndDate = Ext.Date.parse(ManProjectHours_me.endDate,"Y.m.d");
					 			if(endDate < projectStartDate){
									return "里程碑结束日期不能早于项目开始日期！";
								}
								
								if(endDate > projectEndDate){
									return "里程碑结束日期不能晚于项目结束日期！";
								}
								
								if((!Ext.isEmpty(p1207))&&(startDate > endDate)){
									return "里程碑开始日期不能晚于结束日期！";
								}
							}
							return true;
						}
					}]
				})
				win.insert(panel_i);
			}
			
			if ("N" == conditions[i].columnType|| "n" == conditions[i].columnType) {
				var readable = false;
				var hide = false;
				var editable = true;
				var fieldStyle = false;
				if(ManProjectHours_me.hideStrs.indexOf(conditions[i].itemid) > 0) {
					hide = true;
				}
				
				if(ManProjectHours_me.readOnlyStrs.indexOf(conditions[i].itemid) > 0){
					readable = true;
					editable = false;
					fieldStyle = true;
				}
				
				var itemLength = conditions[i].itemLength;
				var width = conditions[i].width;
				var intpart = itemLength - width;
				intpart=Math.pow(10,intpart)-1;
				var floatpart = 1-Math.pow(0.1,width);
				var numberfield = Ext.create('Ext.form.field.Number',{
					anchor : '100%',
					id : conditions[i].itemid,
					labelSeparator : null,
					fieldLabel : "<span Style='padding-right:10px;'> </span>" + conditions[i].itemdesc,
					decimalPrecision : conditions[i].width,
					//maxValue :intpart+floatpart,
					hideTrigger : true,
					keyNavEnabled : false,
					mouseWheelEnabled : false,
					hidden : hide,
					editable:editable,
					fieldStyle:fieldStyle||('NO'== ManProjectHours_me.modifyPermissions) ? 'background-color:rgb(238, 238, 238)': '',
					readOnly : readable||('NO'== ManProjectHours_me.modifyPermissions)? true: false,
					width : 405,
					validator: function (value) {
						if("p1111"==this.id){
							if(value>intpart+floatpart){
								Ext.getCmp("p1111").setValue(intpart+floatpart);
							}
							if(value<0){
								return "预计工时（天）不能为负数";
							}
						}
						return true;
					}
				})
				win.insert(numberfield);
			}
			
			if ("M" == conditions[i].columnType|| "m" == conditions[i].columnType) {
				var textArea = Ext.create('Ext.form.field.TextArea',
					{
						id : conditions[i].itemid,
						labelSeparator : null,
						grow : true,
						fieldLabel : "<span Style='padding-right:10px;'> </span>" + conditions[i].itemdesc,
						width : ManProjectHours_me.width * 0.5,
						fieldStyle : 'NO'== ManProjectHours_me.modifyPermissions ? 'background-color:rgb(238, 238, 238)': '',
						readOnly : 'NO'== ManProjectHours_me.modifyPermissions ? true: false
					})
				win.insert(textArea);
			}
			
			if (("A" == conditions[i].columnType || "a" == conditions[i].columnType)&& '0' != conditions[i].codesetId) {
				var point = "<span Style='padding-right:10px;'> </span>";
				if ("p1119" == conditions[i].itemid)
					point = "<span Style='color:red;padding-right:5px;'>*</span>";
				var panel = Ext.create('Ext.panel.Panel', {
					border : false,
					items : [ {
						xtype : 'codecomboxfield',
						fieldLabel : point + conditions[i].itemdesc,
						id : conditions[i].itemid,
						labelSeparator : null,
						codesetid : conditions[i].codesetId,
						nmodule:'4',
						ctrltype:'3',
						width : 405,
						allowBlank: "p1119" != conditions[i].itemid,
						fieldStyle : 'NO'== ManProjectHours_me.modifyPermissions ? 'background-color:rgb(238, 238, 238)': '',
						readOnly : 'NO'== ManProjectHours_me.modifyPermissions ? true: false
					} ]
				})
				win.insert(panel);
			}
		}
		
		if("add" == ManProjectHours_me.type||"edit"==ManProjectHours_me.type){
			var selMngLinkName = "选择负责人";
			var addMembers = "添加项目成员"
			if('NO'== ManProjectHours_me.modifyPermissions){
				selMngLinkName = "";
				addMembers = "";
			}
			var html =  '<div class="hj-nmd-dl" id = "showPerson">'
			+ '<dl>'
			+ '<dt id="responsTitle"><a href="javascript:void(0)" style="cursor: default;">'
			+ '<img id="responsPosiPic" /></a></dt>'
			+ '<dd id ="responsPosiName"></dd>'
			+ '</dl></div>'
			+ '<input type="hidden" id="responsPosiId" />';
			var panel = Ext.create('Ext.panel.Panel', {
				width : ManProjectHours_me.width * 0.8,
				border : false,
				items:[{
					xtype:'panel',
					layout:"table",
					border:false,
					items:[{
						xtype:'label',
						height:'100%',
						html:'<div style="margin-left:0px"><span Style="color:red;padding-right:5px;text-align:center;">*</span>项目负责人</div>'
					},{
						xtype:label,
						width: '40px',
						border:false
						
					},{
						xtype:'panel',
						id:'addManager',
						border:false,
						html:html
					},{
						xtype:'label',
						html:'<a href="javascript:void(0)" id="selMngLinkNameId" style="line-height:50px" onclick="ManProjectHours_me.pickPerson(this,1)">' + selMngLinkName + '</a>'
					}]
				},{
					xtype:'panel',
					layout:'table',
					border:false,
					items:[{
						xtype:'label',
						html:'<div style="margin-left:9px;margin-right:5px;">项目成员</div>',
						
					},{
						xtype:label,
						width: '45px',
						border:false
						
					},{
						xtype:'panel',
						maxWidth: 470,
						layout:'column',
						border:false,
						id:'addPerson'
					},{
						xtype:'label',
						html:'<a id="addA1" href="javascript:void(0)" style=" line-height:50px;white-space: nowrap;" onclick="ManProjectHours_me.addPerson(this)">'+addMembers+'</a>'
					}]
					
				}]
				
			});
			win.insert(panel);
		}
		win.show();
		
		if (ManProjectHours_me.type == 'add') {
			Ext.getCmp("addManager").hide();
			Ext.getCmp("p1121").setValue(organizationsName);
			manList = result.manList;
			if(manList.a0100 != null && manList.a0100 != '' && manList.a0100.length>0){
				var elem1 = Ext.getDom("responsPosiName");
				var elem2 = Ext.getDom("responsPosiPic");
				var elem3 = Ext.getDom("responsPosiId");
				var elem4 = Ext.getDom("responsTitle");
				elem2.src = manList.imageUrl;
				elem3.value = manList.a0100;
				var name = manList.a0101;
				if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
					name = ManProjectHours_me.cut_str(name,3);
				}
				elem1.innerHTML = name;
				elem4.title = name;
				ManProjectHours_me.reposA01 = manList.a0100;
				ManProjectHours_me.reposA01List.push(manList);
				Ext.getDom("selMngLinkNameId").innerHTML="转给他人负责";
				Ext.getCmp("addManager").show();
			}
		
		}else if(ManProjectHours_me.type == 'edit') {
			Ext.getCmp("addManager").hide();
			for ( var i = 0; i < manList.length; i++) {
				var elem1 = Ext.getDom("addTd1");
				var elem2 = Ext.getDom("addA1");
				if (manList[i].p1311 == '01') {
					var elem1 = Ext.getDom("responsPosiName");
					var elem2 = Ext.getDom("responsPosiPic");
					var elem3 = Ext.getDom("responsPosiId");
					var elem4 = Ext.getDom("responsTitle");
					elem2.src = manList[i].imageUrl;
					elem3.value = manList[i].a0100;
					var name = manList[i].a0101;
					if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
						name = ManProjectHours_me.cut_str(name,3);
					}
					elem1.innerHTML = name;
					elem4.title = name;
					ManProjectHours_me.reposA01 = manList[i].a0100;
					ManProjectHours_me.reposA01List.push(manList[i]);
					Ext.getDom("selMngLinkNameId").innerHTML="转给他人负责";
					Ext.getCmp("addManager").show();
					continue;
				}
				ManProjectHours_me.addposA01.push(manList[i]);
				ManProjectHours_me.indexAd++;
			
				var divid = "divs"+ ManProjectHours_me.indexAd;
				var name = manList[i].a0101;
				if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
					name = ManProjectHours_me.cut_str(name,3);
				}
				var ele3 = '<div style="padding-right:10px;padding-top:10px;"><dl onmouseover="ManProjectHours_me.toRemove(\''
					+ divid
					+ '\')" onmouseleave="ManProjectHours_me.toChan(\''
					+ divid
					+ '\')"><dt title="'
					+ name
					+ '"><img src="'
					+ manList[i].imageUrl
					+ '" /><img id="'
					+ divid
					+ '" class="deletePic" onclick="ManProjectHours_me.toDelet(\''+manList[i].a0100+'\',this)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'
					+ name
					+ '</dd></div>';
			
				var label = Ext.create('Ext.form.Label',{
						html:ele3,
						id:manList[i].a0100
					});
				Ext.getCmp('addPerson').insert(label);
			};
		var beforEditList = ManProjectHours_me.nameList.substring(0, ManProjectHours_me.nameList.lastIndexOf('`'));
		beforEditList = beforEditList.split('`');
		for ( var i = 0; i < beforEditList.length; i++) {
			Ext.getCmp(beforEditList[i]).setValue(projectList[beforEditList[i]]);
			}
		}else if(ManProjectHours_me.type == 'landMarkEdit'){
			var beforEditList = ManProjectHours_me.nameList.substring(0, ManProjectHours_me.nameList.lastIndexOf('`'));
			beforEditList = beforEditList.split('`');
			for ( var i = 0; i < beforEditList.length; i++) {
				Ext.getCmp(beforEditList[i]).setValue(projectList[beforEditList[i]]);
			}
		};
	},
	pickPerson : function(btn, type) {
		var beforeManagerId = new Array();
		beforeManagerId.push(ManProjectHours_me.reposA01);
		var picker = new PersonPicker({
			multiple : false,
			deprecate : beforeManagerId,
			isPrivExpression: false,
			callback : function(c) {
				var arr = ManProjectHours_me.getArray();
				Ext.getDom("selMngLinkNameId").innerHTML="转给他人负责";
				if('edit' == ManProjectHours_me.type){
					var map = new HashMap();
					map.put("projectId", ManProjectHours_me.projectId);
					map.put("beforeManId", ManProjectHours_me.reposA01);
					var memberToManager = '';
					var afterManMap = new HashMap();
					ManProjectHours_me.reposA01 = c.id;
					if (ManProjectHours_me.jugeArray(arr,c.id) < 0) {
						afterManMap.put("p1305", c.dept);
						afterManMap.put("p1303", c.unit);
						afterManMap.put("a0101", c.name);
						afterManMap.put("a0100", c.id);
						afterManMap.put("nbase", 'usr');
						afterManMap.put("p1307", c.c0104);
						afterManMap.put("p1309", c.email);
						afterManMap.put("p1311", "01");
						afterManMap.put("imageUrl",c.photo);
						map.put("afterManMap", afterManMap);
						var endDate = Ext.getCmp("p1109").getRawValue();
						map.put("endDate", endDate);
						memberToManager = '0';
						map.put("afterManMap", afterManMap);
					}else{
						memberToManager = '1';
						afterManMap.put("id", c.id);
						map.put("afterManMap", afterManMap);
						var endDate = Ext.getCmp("p1109").getRawValue();
						map.put("endDate", endDate);
					}
					map.put("memberToManager", memberToManager);
					map.put("type", "editProjectManager");
					Rpc({functionId : 'PM00000099', async : false,success : function(form,action) {
						var result = Ext.decode(form.responseText);
						if(result.tip=='1'){
							if(memberToManager == '1'){
								Ext.getCmp("addPerson").remove(Ext.getCmp(c.id));
								for ( var g = 0; g < ManProjectHours_me.addposA01.length; g++) {
									var map = new HashMap();
									map = ManProjectHours_me.addposA01[g];
									if (map.a0100 == c.id) {
										ManProjectHours_me.addposA01.removeAt(g);
									}
								}
							}
							Ext.getDom("showPerson").style.display = 'block';
							var elem1 = Ext.getDom("responsPosiName");
							var elem2 = Ext.getDom("responsPosiPic");
							var elem3 = Ext.getDom("responsPosiId");
							var elem4 = Ext.getDom("responsTitle");
							var name = c.name;
							if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
								name = ManProjectHours_me.cut_str(name,3);
							}
							elem1.innerHTML = name;
							elem2.src = c.photo;
							elem3.value = c.id;
							elem4.title = name;
							ManProjectHours_me.reposA01 = c.id;
							var maplist = new HashMap();
							maplist.put("p1305", c.dept);
							maplist.put("p1303", c.unit);
							maplist.put("a0101", c.name);
							maplist.put("a0100", c.id);
							maplist.put("nbase", 'usr');
							maplist.put("p1307", c.c0104);
							maplist.put("p1309", c.email);
							maplist.put("p1311", "01");
							maplist.put("imageUrl",c.photo);
							
							if(result.result == "1"){
								ManProjectHours_me.indexAd++;
								var note = "1";
								var arrList = ManProjectHours_me.getArray();
								for ( var int = 0; int < arrList.length; int++) {
									if (ManProjectHours_me.reposA01List[0].a0100 == arrList[int]) {
										return;
									}
								}
								var divid = "divs"+ ManProjectHours_me.indexAd;
								var name = ManProjectHours_me.reposA01List[0].a0101;
								if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
									name = ManProjectHours_me.cut_str(name,3);
								}
								
								var ele3 = '<div style="padding-right:10px;padding-top:10px;">'
									     +   '<dl onmouseover="ManProjectHours_me.toRemove(\'' + divid + '\')"'
									     +      ' onmouseleave="ManProjectHours_me.toChan(\'' + divid + '\')">'
									     +     '<dt title="'+ManProjectHours_me.reposA01List[0].a0101+ '"/>'
									     +     '<img src="'+ManProjectHours_me.reposA01List[0].imageUrl+'" />'
									     +     '<img id="' + divid + '" class="deletePic img-middle" onclick="ManProjectHours_me.toDelet(\''+ ManProjectHours_me.reposA01List[0].a0100 +'\',this)" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img>'
									     +     '<dd>' + name + '</dd>'
									     +   '</dl>' 
										 + '</div>';
								var label = Ext.create('Ext.form.Label',{
									html:ele3,
									id:ManProjectHours_me.reposA01List[0].a0100
								});
								
								Ext.getCmp('addPerson').insert(label);
								if (ManProjectHours_me.jugeArray(arr, c.id) < 0) {
									var map = new HashMap();
									map.put("p1305", ManProjectHours_me.reposA01List[0].p1305);
									map.put("p1303", ManProjectHours_me.reposA01List[0].p1303);
									map.put("a0101", ManProjectHours_me.reposA01List[0].a0101);
									map.put("a0100", ManProjectHours_me.reposA01List[0].a0100);
									map.put("p1307", ManProjectHours_me.reposA01List[0].p1307);
									map.put("p1309", ManProjectHours_me.reposA01List[0].p1309);
									map.put("p1311", "02");
									map.put("imageUrl",ManProjectHours_me.reposA01List[0].imageUrl);
									ManProjectHours_me.addposA01.push(map);
									}
								}
								
								ManProjectHours_me.reposA01List.removeAt(0);
								ManProjectHours_me.reposA01List.push(maplist);
								Ext.getCmp("addManager").show();
							}
						}
			    	}, map);
			}else{
					Ext.getDom("showPerson").style.display = 'block';
					var elem1 = Ext.getDom("responsPosiName");
					var elem2 = Ext.getDom("responsPosiPic");
					var elem3 = Ext.getDom("responsPosiId");
					var elem4 = Ext.getDom("responsTitle");
					var name = c.name;
					if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
						name = ManProjectHours_me.cut_str(name,3);
					}
					elem1.innerHTML = name;
					elem2.src = c.photo;
					elem3.value = c.id;
					elem4.title = name;
					ManProjectHours_me.reposA01 = c.id;
					var map = new HashMap();
					map.put("p1305", c.dept);
					map.put("p1303", c.unit);
					map.put("a0101", c.name);
					map.put("a0100", c.id);
					map.put("p1307", c.c0104);
					map.put("p1309", c.email);
					map.put("imageUrl",c.photo);
					map.put("p1311", "01");
					ManProjectHours_me.reposA01List.removeAt(0);
					ManProjectHours_me.reposA01List.push(map);
					Ext.getCmp("addManager").show();
					if (ManProjectHours_me.jugeArray(arr,c.id) > 0) {
						Ext.getCmp("addPerson").remove(Ext.getCmp(c.id));
						for ( var g = 0; g < ManProjectHours_me.addposA01.length; g++) {
							var map = new HashMap();
							map = ManProjectHours_me.addposA01[g];
							if (map.a0100 == c.id) {
								ManProjectHours_me.addposA01.removeAt(g);
							}
						}
					}
				} 
			}
		}, btn);
		picker.open();
	},
	addPerson : function(btn) {
		var arr = ManProjectHours_me.getArray();
		var picker = new PersonPicker({
			multiple : true,
			deprecate : arr,
			isPrivExpression: false,
			callback : function(cm) {
				if("edit"==ManProjectHours_me.type){
					var map = new HashMap();
					map.put("type", "addMembers");
					var beginDate = Ext.getCmp("p1107").getRawValue();
					var endDate = Ext.getCmp("p1109").getRawValue();
					map.put("beginDate", beginDate);
					map.put("endDate", endDate);
					var menData = new Array();
					for ( var i = 0; i < cm.length; i++) {
						var note = "1";
						var c = cm[i];
						for ( var int = 0; int < arr.length; int++) {
							if (c.id == arr[int]) {
								note = "0";
								break;
							}
						}
						if(note =="0")
							continue;
						var manMap = new HashMap();
						manMap.put("p1305", c.dept);
						manMap.put("p1303", c.unit);
						manMap.put("a0101", c.name);
						manMap.put("a0100", c.id);
						manMap.put("p1307", c.c0104);
						manMap.put("p1309", c.email);
						manMap.put("p1311", "02");
						manMap.put("imageUrl",c.photo);
						menData.push(manMap);
					}
					map.put("menData", menData);
					map.put("projectId",ManProjectHours_me.projectId );
					Rpc({
						functionId : 'PM00000099',
						async : false,
						success : function(form, action) {
							for ( var int2 = 0; int2 < cm.length; int2++) {
								var c = cm[int2];
								ManProjectHours_me.indexAd++;
								var note = "1";
								for ( var int = 0; int < arr.length; int++) {
									if (c.id == arr[int]) {
										note = "0";
										break;
									}
								}
								if(note =="0")
									continue;
								var divid = "divs"+ ManProjectHours_me.indexAd;
								var name = c.name;
								if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
									name = ManProjectHours_me.cut_str(name,3);
								}
								var ele3 = '<div style="padding-right:10px;padding-top:10px;">'
									     +   '<dl onmouseover="ManProjectHours_me.toRemove(\'' + divid + '\')"'
									     +      ' onmouseleave="ManProjectHours_me.toChan(\'' + divid + '\')">'
									     +     '<dt title="' + name + '"/>'
									     +     '<img src="'	+ c.photo + '" />'
									     +     '<img id="' + divid + '" class="deletePic img-middle" onclick="ManProjectHours_me.toDelet(\''+ c.id+'\',this)" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img>'
									     +     '<dd>' + name + '</dd>'
									     +   '</dl>' 
										 + '</div>';
								var label = Ext.create('Ext.form.Label',{
									html:ele3,
									id:c.id
								});
								Ext.getCmp('addPerson').insert(label);
								if (ManProjectHours_me.jugeArray(arr, c.id) < 0) {
									var map = new HashMap();
									map.put("p1305", c.dept);
									map.put("p1303", c.unit);
									map.put("a0101", c.name);
									map.put("a0100", c.id);
									map.put("p1307", c.c0104);
									map.put("p1309", c.email);
									map.put("p1311", "02");
									map.put("imageUrl",c.photo);
									ManProjectHours_me.addposA01.push(map);
								}
							}
						}
					}, map);
				}else{
					for ( var int2 = 0; int2 < cm.length; int2++) {
						var c = cm[int2];
						ManProjectHours_me.indexAd++;
						var note = "1";
						for ( var int = 0; int < arr.length; int++) {
							if (c.id == arr[int]) {
								note = "0";
								break;
							}
						}
						if(note =="0")
							continue;
						var divid = "divs"+ ManProjectHours_me.indexAd;
						var name = c.name;
						if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
							name = ManProjectHours_me.cut_str(name,3);
						}
						var ele3 = '<div style="padding-right:10px;padding-top:10px;">'
						     +   '<dl onmouseover="ManProjectHours_me.toRemove(\'' + divid + '\')"'
						     +      ' onmouseleave="ManProjectHours_me.toChan(\'' + divid + '\')">'
						     +     '<dt title="' + name + '"/>'
						     +     '<img src="'	+ c.photo + '" />'
						     +     '<img id="' + divid + '" class="deletePic img-middle" onclick="ManProjectHours_me.toDelet(\''+ c.id+'\',this)" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img>'
						     +     '<dd>' + name + '</dd>'
						     +   '</dl>' 
							 + '</div>';
						var label = Ext.create('Ext.form.Label',{
							html:ele3,
							id:c.id
					});
					Ext.getCmp('addPerson').insert(label);
					if (ManProjectHours_me.jugeArray(arr, c.id) < 0) {
						var map = new HashMap();
						map.put("p1305", c.dept);
						map.put("p1303", c.unit);
						map.put("a0101", c.name);
						map.put("a0100", c.id);
						map.put("p1307", c.c0104);
						map.put("p1309", c.email);
						map.put("p1311", "02");
						map.put("imageUrl",c.photo);
						ManProjectHours_me.addposA01.push(map);
						}
					}
				}
			}
		}, btn);
		picker.open();
	},
	
	toRemove : function(par) {
		var a = Ext.getDom(par);
		if("YES"==ManProjectHours_me.modifyPermissions)
		a.style.display = "";
	},
	
	jugeArray : function(arr, param) {
		var temp = -1;
		for ( var i = 0; i < arr.length; i++) {
			if (arr[i] == param) {
				temp = i;
				break;
			}
		}
		return temp;
	},
	
	getArray : function() {
		var arr = new Array();
		if (ManProjectHours_me.reposA01.length > 0)
			arr.push(ManProjectHours_me.reposA01);
		if (ManProjectHours_me.addposA01.length > 0) {
			for ( var int = 0; int < ManProjectHours_me.addposA01.length; int++) {
				var map = new HashMap();
				map = ManProjectHours_me.addposA01[int];
				arr.push(map.a0100);
			}
		}
		return arr;
	},
	toChan : function(par) {
		var a = Ext.getDom(par);
		a.style.display = "none";
	},
	
	toDelet : function(elem,id) {
		if("edit"==ManProjectHours_me.type){
			var map = new HashMap();
			map.put("type", "beforeDeleMember");
			map.put("manId", elem);
			map.put("projectId",ManProjectHours_me.projectId );
			Rpc({
				functionId : 'PM00000099',
				async : false,
				success : function(form, action) {
					var result = Ext.decode(form.responseText);
					var tip = "";
					var map = new HashMap();
					map.put("type", "deleMember");
					map.put("manId", elem);
					map.put("projectId",ManProjectHours_me.projectId );
					Rpc({functionId : 'PM00000099', async : false,
						success : function(form,action) {
							var result = Ext.decode(form.responseText);
							if(result.result!='1'){
								Ext.getCmp("addPerson").remove(Ext.getCmp(elem));
								for ( var g = 0; g < ManProjectHours_me.addposA01.length; g++) {
									var map = new HashMap();
									map = ManProjectHours_me.addposA01[g];
									if (map.a0100 == elem) {
										ManProjectHours_me.addposA01.removeAt(g);
									}
								}
							}
							if(result.tip!='1'){
								Ext.Msg.show({
								     title:'提示信息',
								     msg: '删除失败！',
								     buttons: Ext.Msg.OK,
								     icon: Ext.Msg.WARNING
								});
								return;
							}
						}
			    	}, map);
				}
			}, map);
		}else{
			Ext.getCmp("addPerson").remove(Ext.getCmp(elem));
			for ( var g = 0; g < ManProjectHours_me.addposA01.length; g++) {
				var map = new HashMap();
				map = ManProjectHours_me.addposA01[g];
				if (map.a0100 == elem) {
					ManProjectHours_me.addposA01.removeAt(g);
				}
			}
		}
	},
	
	save : function() {
		if("add" == ManProjectHours_me.type||"edit"==ManProjectHours_me.type){
			if(""==Ext.getCmp("p1103").getValue()||null==Ext.getCmp("p1103").getValue()){
				Ext.Msg.alert('提示信息', "项目名称为必填项，请填写后保存！");
				return;
			}
			if(""==Ext.getCmp("p1107").getRawValue()||null==Ext.getCmp("p1107").getRawValue()){
				Ext.Msg.alert('提示信息', "开始日期为必填项，请填写后保存！");
				return;
			}
			
			if(""==Ext.getCmp("p1109").getRawValue()||null==Ext.getCmp("p1109").getRawValue()){
				Ext.Msg.alert('提示信息', "结束日期为必填项，请填写后保存！");
				return;
			}
			
			var p1119cmp = Ext.getCmp("p1119");
			if(p1119cmp && (Ext.isEmpty(p1119cmp.getValue()) || "undefined`"== p1119cmp.getValue())){
				Ext.Msg.alert('提示信息', "项目阶段为必填项，请填写后保存！", function(id) {
					p1119cmp.focus();
				});
				return;
			}
		}else{
			
			if(""==Ext.getCmp("p1203").getValue()||null==Ext.getCmp("p1203").getValue()){
				Ext.Msg.alert('提示信息', "里程碑名称为必填项，请填写后保存！");
				return;
			}
			if(""==Ext.getCmp("p1207").getRawValue()||null==Ext.getCmp("p1207").getRawValue()){
				Ext.Msg.alert('提示信息', "开始日期为必填项，请填写后保存！");
				return;
			}
			if(""==Ext.getCmp("p1209").getRawValue()||null==Ext.getCmp("p1209").getRawValue()){
				Ext.Msg.alert('提示信息', "结束日期为必填项，请填写后保存！");
				return;
			}
		}
		
		if(ManProjectHours_me.reposA01List.length==0&&("add" == ManProjectHours_me.type||"edit"==ManProjectHours_me.type)){
			Ext.Msg.alert('提示信息', "项目负责人为必选项，请选择后保存！");
			return;
		}
		var mapAllList = new HashMap();
		var mapList = new HashMap();
		
		var componIds = ManProjectHours_me.nameList.substring(0, ManProjectHours_me.nameList.lastIndexOf('`'));
		componIds = componIds.split('`');
		for ( var i = 0; i < componIds.length; i++) {
			for(var j = 0;j<ManProjectHours_me.formiterms.length;j++){
				if(ManProjectHours_me.formiterms[j].itemid == componIds[i] ){
					if("A"==ManProjectHours_me.formiterms[j].columnType||"a"==ManProjectHours_me.formiterms[j].columnType){
						var value =	Ext.getCmp(componIds[i]).getValue();
						if("p1119"==componIds[i]){
							var str = value.split("`");
							value = str[0];
						}
						var itemLength = ManProjectHours_me.formiterms[j].itemLength;
						var itemdesc = ManProjectHours_me.formiterms[j].itemdesc;
						var codesetId = ManProjectHours_me.formiterms[j].codesetId;
						if("p1121"!=componIds[i] && codesetId == 0 && value.replace(/[\u4E00-\u9FA5]/g,'aa').length>itemLength){
							Ext.Msg.alert('提示信息', itemdesc+"超出可以输入字符长度，长度为"+itemLength/2+"汉字或"+itemLength+"字母或数字！");
							return;
						}
					}
					if("N"==ManProjectHours_me.formiterms[j].columnType||"n"==ManProjectHours_me.formiterms[j].columnType){
						var value =	Ext.getCmp(componIds[i]).value;
						var width = ManProjectHours_me.formiterms[j].width;
						var itemLength = ManProjectHours_me.formiterms[j].itemLength-width;
						var intpart = Math.pow(10,itemLength)-1;
						var floatpart = 1-Math.pow(0.1,width);
						if(isNaN(value)){
							Ext.Msg.alert('提示信息', "请输入数字！");
							return;
						}
							if(value>(intpart+floatpart)){
							Ext.Msg.alert('提示信息', ManProjectHours_me.formiterms[j].itemdesc+"最大只能输入"+(intpart+floatpart)+"！");
							return;
						}
					}
					if("D"==ManProjectHours_me.formiterms[j].columnType||"d"==ManProjectHours_me.formiterms[j].columnType){
						var regResult =  Ext.getCmp(componIds[i]).validate();
						if(!regResult){
							Ext.Msg.alert('提示信息', ManProjectHours_me.formiterms[j].itemdesc+"输入错误，请重新填写！");
							return;
						}
					}
				}
			}
			var value = '';
			if (ManProjectHours_me.hideStrs.indexOf(conditions[i].itemid) > 0) {
				continue;
			}
			value = Ext.getCmp(componIds[i]).getValue();
			if("p1119"==componIds[i]){
				var str = value.split("`");
				value = str[0];
			}
			if("p1121"==componIds[i]){
				var str = value.split("`");
				value = str[0];
				if(str[0].indexOf("UN")>-1||str[0].indexOf("UM")>-1){
					value = str[0].substring(2);
				}
			}
			mapList.put(componIds[i], value);
		}
		mapAllList.put("type",ManProjectHours_me.type);
		if("edit"==ManProjectHours_me.type||"landMarkEdit"==ManProjectHours_me.type||"landMarkAdd"==ManProjectHours_me.type){
			mapAllList.put("projectId",ManProjectHours_me.projectId);
			if("landMarkEdit"==ManProjectHours_me.type){
				mapAllList.put("landMarkId",ManProjectHours_me.landMarkId);
			}
		}
		
		mapAllList.put("mapList", mapList);
		if("add" == ManProjectHours_me.type||"edit"==ManProjectHours_me.type){
			mapAllList.put("memebersId",ManProjectHours_me.addposA01);
			mapAllList.put("managerId",ManProjectHours_me.reposA01List);
		}
		
		Rpc({
			functionId : 'PM00000099',
			async : false,
			success : function(form, action) {
				var store = Ext.getCmp("projectmanage_0001_dataStore");
				
				Ext.util.CSS.createStyleSheet(".x-tree-icon{width: 0px;height: 0px;}","treegridImg"); 
				Ext.getCmp("formId").close();
				var result = Ext.decode(form.responseText);
				var tip = result.tip;
				projectManage.newData = result.newData;
				Ext.callback(projectManage.updateStore,tip,[tip,ManProjectHours_me.type]);
				if ("1"!= tip) {
					Ext.Msg.alert('提示信息',"保存失败 ！");
				}
			}
		}, mapAllList);
	},

	cut_str:function (str, len){
        var char_length = 0;
        for (var i = 0; i < str.length; i++){
            var son_str = str.charAt(i);
            encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
            if (char_length >= len){
                var sub_len = char_length == len ? i+1 : i;
                return str.substr(0, sub_len);
                break;
            }
        }
    },

	cancel:function(){
		Ext.util.CSS.createStyleSheet(".x-tree-icon{width: 0px;height: 0px;}","treegridImg"); 
		Ext.getCmp("formId").close();
	}
});
