Ext.define('KqSchemeURL.SchemeDetails',{
	requires:['EHR.extWidget.field.CodeTreeCombox'],
	constructor:function(config){
		SchemeDetails = this;
		SchemeDetails.scheme_id = config.scheme_id;
		//是否可以修改，不可修改的时候置灰
		//SchemeDetails.canEdit = true;//config.canEdit;//true:false
		//SchemeDetails.nonIEDisabledStyle = SchemeDetails.canEdit?"":"opacity:0.3;";
		//SchemeDetails.ieDisabledStyle = SchemeDetails.canEdit?"":"FILTER: progid:DXImageTransform.Microsoft.Alpha(Opacity=30);";
		SchemeDetails.init();
    },
    
    init: function() {
    	var json = {};
		json.type = "get_info";
		json.id = SchemeDetails.scheme_id;
		var map = new HashMap();
		map.put("jsonStr",JSON.stringify(json));
		Rpc({functionId:'KQ00020201',success:function(response,action){
			var result = Ext.decode(response.responseText);
			if (result.succeed) { 
				var data = result.returnStr.return_data;
				SchemeDetails.name = data.name;//名称
				SchemeDetails.clerk_fullname = data.clerk_fullname;
				SchemeDetails.clerk_username = data.clerk_username;
				SchemeDetails.clerk_imgPath = data.clerk_imgPath;
				if(data.clerk_fullname && data.clerk_username)
					SchemeDetails.clerk = data.clerk_fullname + " (" + data.clerk_username + ")";//考勤人
				SchemeDetails.reviewer_id = data.reviewer_id;//审核人ID
				SchemeDetails.old_reviewer_id = data.reviewer_id;//审核人ID
				SchemeDetails.reviewer_fullname = data.reviewer_fullname;//审核人全称
				SchemeDetails.reviewer_imgPath = data.reviewer_imgPath;//审核人图片src
				SchemeDetails.b0110 = data.b0110;//所属机构
				SchemeDetails.remark = data.remark;//备注
				SchemeDetails.org_scope = data.org_scope;//应用范围
				SchemeDetails.org_scope_fullname = data.org_scope_fullname;//应用范围
				SchemeDetails.org_scope_enc = data.org_scope_enc;//应用范围加密
				SchemeDetails.cond = data.cond;//条件项
				SchemeDetails.item_ids = !Ext.isEmpty(data.item_ids)?","+data.item_ids:data.item_ids;//考勤项目ID选中ID
				SchemeDetails.item_ids_All = data.item_ids_All;//考勤项目ID
				SchemeDetails.item_id_list = data.item_id_list;//考勤项目ID顺序
				SchemeDetails.class_ids = !Ext.isEmpty(data.class_ids)?","+data.class_ids:data.class_ids;//班次ID选中ID
				SchemeDetails.class_ids_All = data.class_ids_All;//班次ID
				SchemeDetails.class_id_list = data.class_id_list;//考勤班次ID顺序
				SchemeDetails.is_validate = data.is_validate;//是否生效
				SchemeDetails.map_oldOrg = data.map_oldOrg;//所有的已经存在的数据机构
				//选择应用范围，需要根据人员的权限进行显示，选人控件无法知道考勤的业务范围，这里先把值传过去
				SchemeDetails.org_unit = data.org_unit;
				SchemeDetails.dbname = data.dbname;//人员库
				SchemeDetails.filling_agencys = data.filling_agencys;//数据上报机构
				SchemeDetails.filling_agencys_select_org = data.filling_agencys_select_org;//数据上报已选的
                //显示日明细数据 默认值为1（显示） ；0||null 不显示
                SchemeDetails.day_detail_enabled = data.day_detail_enabled;
				SchemeDetails.item_ids_array = new Array();
				if(SchemeDetails.item_ids != '') {
					var items = SchemeDetails.item_ids.split(",");
					for(var i = 0; i < items.length; i++) {
						if(items[i] != "") 
							SchemeDetails.item_ids_array.push(items[i]);
					}
				}
				SchemeDetails.class_ids_array = new Array();
				if(SchemeDetails.class_ids != '') {
					var classs = SchemeDetails.class_ids.split(",");
					for(var i = 0; i < classs.length; i++) {
						if(classs[i] != "") 
							SchemeDetails.class_ids_array.push(classs[i]);
					}
				}
				
				//员工确认考勤结果 1:生效 默认勾选
				SchemeDetails.confirm_flag = data.confirm_flag;
				//员工确认考勤结果 1:生效 默认勾选
				SchemeDetails.secondary_admin = data.secondary_admin;
				
				SchemeDetails.createFirstPanel();//第一步
				SchemeDetails.createSecondPanel();//第二步
				SchemeDetails.createThirdPanel();//第三步
				SchemeDetails.createFourthPanel();//第四步
				SchemeDetails.createWindow();
			} else {  
				Ext.showAlert(result.message+"！");
			}
		}},map);
    },
    //第一步
    createFirstPanel:function() {
		//名称
		var name = Ext.widget('textfield',{
			id: 'name',
			fieldLabel: kq.label.name + "<span style='color:red;padding-left:5px;'>*</span>",
			labelAlign: 'left',
			margin: '30 0 0 40',
			width: 460,
			name: 'name',
			labelWidth: 50,
			labelAlign: 'right',
			maxLength: 80,
			value: SchemeDetails.name
		});
		
		//start--考勤员和审核人
		var clerkName = Ext.widget('label',{
			html: kq.scheme.workName + "<font color='red'> * </font>",
			maxWidth:50,
			height:18,
			style: "white-space: nowrap;float: left;margin:16px 0 0 0;"
		});
		//考勤员图片
		var clerkImg = Ext.create('Ext.Img',{
		    src: SchemeDetails.clerk_imgPath,
		    style: 'border-radius: 50%;cursor:pointer;',
		    width: 50,
		    height: 50,
		    id: 'workId',
		    margin: '0 0 0 10',
			listeners:{
		        //监听click事件
		        el:{
		            click: function(){ SchemeDetails.changework(false); }//imgClick方法写在了controller中，
		        }
		    }
		});
		var clerkImgPanel =  Ext.widget('panel',{
			width: 130,
			height: 120,
			border: false,
			layout: 'vbox',
			items: [clerkImg,{
				xtype: 'label',
				id: 'clerkLabel',
				height: 40,
				border: false,
				width: 130,
				margin: '0 0 0 9',
				html: SchemeDetails.clerk
			}]
		});
		
		//审核人
		var reviewerName = Ext.widget('label',{
			text: kq.scheme.checkName,
			maxWidth:50,
			height:18,
			style:'white-space: nowrap;float: left;margin:16px 0 0 180px;'
		});
		var review_path = SchemeDetails.reviewer_imgPath?SchemeDetails.reviewer_imgPath:"/servlet/DisplayOleContent?filePath=/images/photo.jpg&bencrypt=false&caseNullImg=/images/photo.jpg";
		var reviewerImg = Ext.widget('panel',{
			name: 'reviewer',
			border: false,
			width: 95,
			html: "<div onmouseover='SchemeDetails.showReview(\"review_id_delete\",\"block\",\"\")' onmouseout='SchemeDetails.showReview(\"review_id_delete\",\"none\",\"\")' ><img id='reviewerId' title='" + SchemeDetails.reviewer_fullname + "' " +
					" style='margin-left:10px;border-radius: 50%;cursor:pointer;' src='" + review_path + "' onclick='SchemeDetails.changework(true)' width=50 height=50>" +
							"<img id='review_id_delete' onclick='SchemeDetails.deleteReview()' title='" + kq.label.del + "' width=20 height=20 src='/workplan/image/remove.png' style='cursor:pointer;position:absolute;top:-2px;left:44px;display:none;'></div>"
		});
		var reviewImgPanel =  Ext.widget('panel',{
			width: 130,
			height: 120,
			border: false,
			layout: 'vbox',
			items: [reviewerImg,{
				xtype: 'label',
				id: 'reviewLabel',
				height: 40,
				border: false,
				width: 70,
				style: 'text-align:center;',
				html: SchemeDetails.reviewer_fullname
			}]
		});
		var workPanel = Ext.widget('panel',{
			margin: '20 0 0 40',
			width: 460,
			border: false,
			height: 80,
			layout:{
				type:'hbox'
			},
			items: [clerkName,clerkImgPanel,reviewerName,reviewImgPanel]
		});
		//end--考勤员和审核人
		
		//员工确认考勤数据
		var confirmResult = Ext.create('Ext.container.Container', {
		    layout: {
		        type: 'hbox'
		    },
		    margin: '0 0 0 40',
		    border: false,
		    items: [{
		        xtype: 'label',
		        html: kq.scheme.confirmResult
		    },{
		    	xtype: 'checkbox',
				boxLabel: kq.shifts.on,
				id: 'confirm_flag',
				margin: '4 0 0 10',
				checked: SchemeDetails.confirm_flag == 1?true:false
		    }]
		});
		
		//备注
		var remark = Ext.widget('textarea',{
			fieldLabel: kq.scheme.remark,
			labelAlign: 'right',
			name: 'remark',
			margin: '15 0 0 40',
			width: 460,
			height: 80,
			labelWidth: 50,
			value: SchemeDetails.remark
		});
		
		SchemeDetails.panel0 = Ext.widget('panel',{
			border: false,
			id: 'panel_0',
			style: 'border-bottom:1px solid #c5c5c5;',
			items: [name,workPanel,confirmResult
				,{// 所属机构
					xtype: 'panel',
				    layout: {
				        type: 'hbox'
				    },
				    margin: '15 0 0 40',
				    border: false,
				    id: 'org_select',
				    items: [{
				        xtype: 'label',
				        html: kq.scheme.organization
				    },{
		        		xtype:'codecomboxfield',
		        		id: 'b0110_combox',
		        		width: 404,
		    			height:22,
			        	margin:"0 0 0 8",
			        	onlySelectCodeset:false,
			        	codesetid:"UM",
			        	ctrltype:'3',
			        	nmodule:"11",
						listeners : {
							afterrender: function(){
								this.setValue(SchemeDetails.b0110,true);
							},
							select:function(a,b){
			                    SchemeDetails.b0110 = b.get('id') + "`" + b.get('text');
			                }
						}
					}]
				}
				,remark]
		});
	},
	
	//第二步
	createSecondPanel: function() {
		var checkboxgroup = Ext.widget({
   			xtype     : 'checkboxgroup',
			columns   : 4,
			columnWidth: 90,
			id:'checkboxdbValue',
			width     : 450,
			margin: '0 0 0 8'
       	});
		
		//循环展示人员库
		Ext.each(SchemeDetails.dbname,function(obj,index){
    		var checkbox = Ext.widget({
    			xtype: 'checkbox',
				boxLabel: obj.name,
				name: 'cbase',
				id: obj.id + '_',
				checked: obj.checked == "true"?true:false,
                inputValue: obj.id
        	});
    		checkboxgroup.add(checkbox);
    	});
    	
    	var heights = 65;
    	if(SchemeDetails.dbname.length > 12) {
			heights = heights + (Math.floor(SchemeDetails.dbname.length/4)-2)*20;
		}
		//人员库的panel
		var personPanel = Ext.widget('panel',{
	    	border:false,
	    	height: heights,
	    	margin: '30 0 0 40',
	    	layout:'hbox',
	    	items:[{
	    		xtype: 'label',
	    		html: kq.scheme.personDatabase + "<font style='color:red;'>*</font>",
				maxWidth:44,
				margin: '3 0 0 10'
	    	},checkboxgroup]
    	});
		
		//应用范围
		var rangePanel = Ext.widget('panel',{
			border:false,
			layout:'vbox',
			items:[{
				xtype:'panel',
				border:false,
				layout:'hbox',
				items:[{
					xtype: 'textarea',
					fieldLabel: kq.scheme.range + "<font style='color:red;'>*</font>",
					labelAlign: 'left',
					margin: '15 0 2 40',
					id: 'org_scope_id',
					editable: false,
					width: 440,
					height: 80,
					labelWidth: 60,
					value: SchemeDetails.org_scope_fullname
				},{
					xtype: 'button',
					text: kq.scheme.choose,
					margin:'16 0 0 10',
					height:'20',
					listeners: {
						'click': function(el){
							var pickers = new PersonPicker({
	                            multiple: true,
	                            text: kq.scheme.addOrg,
	                            addunit: true, //是否可以添加单位
	                            adddepartment: true, //是否可以添加单位
	                            orgid: SchemeDetails.org_unit, // 组织机构，不传代表全部
	                            isPrivExpression:false,
	                            defaultSelected:SchemeDetails.org_scope_enc,
	                            addpost: false,
	                            isPrivExpression: false,
	                            callback: function (c) {
	                            	var org_ids = [];
	                            	var org_id = [];
	                            	var org_name = [];
	                            	//数据上报中的机构id集合
	                            	Ext.each(SchemeDetails.filling_agencys,function(filling_agency,index){
	                            		org_ids.push(filling_agency.org_id);
	                                });
	                            	
	                                for (var i = 0; i < c.length; i++) {
	                                    var cc = c[i];
	                                    org_id.push(cc.rawType + ',' + cc.id);
	                                    org_name.push(cc.name);
	                                }
	                                var json = {};
	                        		json.type = "sort_range";
	                        		json.org_id = org_id;
	                        		json.org_ids = org_ids;//数据上报中的
	                        		json.org_name = org_name;
	                                var map = new HashMap();
	                        		map.put("jsonStr",JSON.stringify(json));
	                        		//由于需要有父节点时，其子节点不用保存
	                        		Rpc({functionId:'KQ00020201',success:function(response,action){
	                        			var result = Ext.decode(response.responseText);
	                        			if(result.succeed == true) {
	                        				SchemeDetails.needDeleteIndex = result.returnStr.return_data.needDeleteIndex;
	                        				if(SchemeDetails.needDeleteIndex.length != 0) {
	                        					Ext.showConfirm(kq.scheme.confirmRange,function (v) {
	                        						if(v == "yes") {
	                        							Ext.getCmp("org_scope_id").setValue(result.returnStr.return_data.org_name);
	                        							SchemeDetails.org_scope = result.returnStr.return_data.org_id;
	                        							SchemeDetails.org_scope_enc = result.returnStr.return_data.org_scope_enc;
	                        							var k = 0;
	                        							//删除SchemeDetails.filling_agencys和store中的对应的数据
	                        							for(var i = 0; i < SchemeDetails.needDeleteIndex.length; i++) {
	                        								var rowIndex = SchemeDetails.needDeleteIndex[i];
		                        							SchemeDetails.filling_agencys.splice(rowIndex - k,1);
		                        							if(SchemeDetails.store) {
		                        								SchemeDetails.store.remove(SchemeDetails.store.data.items[rowIndex - k]);
		                        							}
		                        							k++;
	                        							}
	                        							//如果删除完了，这时候新增一个默认空白行
	                        							if(SchemeDetails.filling_agencys.length == 0) {
	                        			                    SchemeDetails.insertData(0, "-1", "");
	                        			                    SchemeDetails.filling_agencys.splice(0,1);
	                        							}
	                        							if(SchemeDetails.store) {
	                        								//现在完全根据行号塞值，取值，所以一旦删除了，就直接重新给值
	                        								SchemeDetails.resetSort();
	                        							}
	                        							SchemeDetails.store.commitChanges();
	                        						}
	                        					});
	                        				}else {
	                        					Ext.getCmp("org_scope_id").setValue(result.returnStr.return_data.org_name);
                    							SchemeDetails.org_scope = result.returnStr.return_data.org_id;
                    							SchemeDetails.org_scope_enc = result.returnStr.return_data.org_scope_enc;
	                        				}
	                        			}
	                        		}},map);
	                            }
	                        }, el);
	                        pickers.open();
						}
					}
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'hbox',
				margin: '5 0 0 105',
				items: [{
					xtype: 'checkbox',
					boxLabel: kq.scheme.limitCondition,
					id: 'cond_check',
					checked: SchemeDetails.cond==''?false:true,
					inputValue: '1',
					listeners:{
						'change':function(field,newValue,oldValue){
							if(newValue){
								rangePanel.queryById('condition').show();
							}else{
								rangePanel.queryById('condition').hide();
							}
						}
					}
				},{
					xtype: 'button',
					id: 'condition',
					height: 25,
					width: 25,
					margin: '0 0 0 10',
					text: '...',
					hidden: SchemeDetails.cond==''?true:false,
					handler:function(){//弹出复杂条件
			         	Ext.require('EHR.complexcondition.ComplexCondition',function(){
			         		Ext.create("EHR.complexcondition.ComplexCondition",{
			         			imodule:"11",
			         			opt:"0",
			         			formula: getDecodeStr(SchemeDetails.cond),
			         			title:kq.scheme.limitCondition,
			         			callBackfn:"SchemeDetails.saveComplexCond"});
			         	});
					}
				}]
			}]
		});
		
		SchemeDetails.panel1 = Ext.widget('panel',{
			border: false,
			id: 'panel_1',
			style: 'border-bottom:1px solid #c5c5c5;',
			items: [personPanel,rangePanel]
		});
	},
	
	//第三步
	createThirdPanel: function(){
		
		var kqItem = Ext.widget('panel',{
			border: false,
			title: kq.scheme.selectKqItem,
			layout: 'absolute',
			autoScroll:true,
			width: 570,
			id: 'kqItem',
	        height: 200
		});
		
		var kq_item_sort = Ext.widget('button',{
			text: kq.scheme.sort,
			margin: '13 0 0 508',
			height:'20',
			style: 'position:absolute;z-index:9;',
			handler: function() {
				SchemeDetails.sortItem("kqItem");
			}
		});
		
    	var x = 0;//x坐标
		var y = 2;//y坐标
		var addItemPanel = Ext.widget({
            xtype: 'panel',
            height: 40,
            width: 60,
            border: false,
            x: x,
            y: y+10,
            html: "<img style='cursor:pointer;' id='addItem' src='/images/new_module/nocycleadd.png' onclick='SchemeDetails.showAllItem(\"kqItem\")'>"
		});
		kqItem.add(addItemPanel);
		
		var kqClassCheckBox = Ext.widget({
			xtype: 'checkbox',
			id: 'classCheck',
			height: 10,
			boxLabel: kq.scheme.showDayDetail + "<span style='color:red;'>" + kq.scheme.justChooseCount + "</span>",
			checked: (SchemeDetails.day_detail_enabled==1 || Ext.isEmpty(SchemeDetails.scheme_id))?true:false,
			style:'z-index:0;padding-bottom: 7px;',
			listeners: {
				'change': function(this_panel, checked) {
					Ext.getCmp("kqClass").setHidden(!checked);
					if(checked) {
						SchemeDetails.day_detail_enabled = 1;
					}else {
						SchemeDetails.day_detail_enabled = 0;
					}
				}
			}
		});
		
		var kqClass = Ext.widget('panel',{
			border: false,
			layout: 'absolute',
			id: 'kqClass',
			autoScroll:true,
			height: 110,
			hidden: (SchemeDetails.day_detail_enabled==1 || Ext.isEmpty(SchemeDetails.scheme_id))?false:true,
			width: 570,
			style: 'top:-9px;z-index:9;border-top:1px solid #c5c5c5;'
		});
		
		var kq_class_sort = Ext.widget('button',{
			text: kq.scheme.sort,
			//margin: '13 0 0 502',
			height:'20',
			style: 'position:absolute;z-index:10;margin-left:508px;',
			handler: function() {
				SchemeDetails.sortItem("kqClass");
			}
		});
		
		SchemeDetails.panel2 = Ext.widget('panel',{
			padding: '0 30 0 25',
			border: false,
			style: 'border-bottom:1px solid #c5c5c5;',
			items: [kq_item_sort,kqItem,kq_class_sort,kqClassCheckBox,kqClass]
		});
		
		SchemeDetails.mapItemDelete = new HashMap();
		SchemeDetails.selectMapItem = new HashMap();
		SchemeDetails.mapClassDelete = new HashMap();
		SchemeDetails.selectMapClass = new HashMap();
		//SchemeDetails.mapItemDelete 没有选中的
		//SchemeDetails.selectMapItem 选中的
		for(var key in SchemeDetails.item_ids_All) {
			if(SchemeDetails.in_array(key, SchemeDetails.item_ids_array) != -1) {
				SchemeDetails.selectMapItem.put(key, SchemeDetails.item_ids_All[key]);
			}else {
				SchemeDetails.mapItemDelete.put(key, SchemeDetails.item_ids_All[key]);
			}
		}
    	SchemeDetails.getShow("kqItem");
    	
		for(var key in SchemeDetails.class_ids_All) {
			if(SchemeDetails.in_array(key, SchemeDetails.class_ids_array) != -1) {
				SchemeDetails.selectMapClass.put(key, SchemeDetails.class_ids_All[key]);
			}else {
				SchemeDetails.mapClassDelete.put(key, SchemeDetails.class_ids_All[key]);
			}
		}
    	SchemeDetails.getShow("kqClass");
	},
	
	//第四步
	createFourthPanel: function(){
		
		if(!SchemeDetails.filling_agencys) {
			SchemeDetails.filling_agencys = new Array();
		}
		SchemeDetails.store = new Ext.data.Store({
			storeId: 'simpsonsStore',
		    fields:[ 'org_name', 'clerk_fullname', 'reviewer_fullname'],
		    data: SchemeDetails.filling_agencys
		});
		
		// 渲染拖拽功能
		var viewConfig = {
			plugins:{
				ptype:'gridviewdragdrop',
				dragText:common.label.DragDropData//拖放数据
			},
			listeners: {
                drop:SchemeDetails.dragColumn
            }
		};
		
		var gridPanel = Ext.create('Ext.grid.Panel', {
            columnLines: true,
            rowLines: true,
            store: SchemeDetails.store,
            border: false,
            id: 'kqAppeal',
            viewConfig:viewConfig,
            style: 'border-top:1px solid #c5c5c5;',
            height: 286,
            columns: [{
            	text: kq.scheme.orgName,//机构名称
                dataIndex: 'org_name',
                width: 280,
                menuDisabled: true,
                sortable: false,
                renderer: function (value, data, record, rowIndex, e, f) {
            		var nameMap = SchemeDetails.getSubStrLength(value,35);
            		var name = nameMap.get("str");
            		var title = '';
            		if(nameMap.get("len")) {
            			title = value;
            		}
                	return "<table onmouseover='SchemeDetails.filling_agencyOpea(\"" + record.data.org_id + "\",\"block\")' onmouseout='SchemeDetails.filling_agencyOpea(\"" + record.data.org_id + "\",\"none\")' " +
                			"style='height:40px;width:270px;'><tr><td title='" + title +"'>" + name + 
                			"</td><td id='operate_" + record.data.org_id + "' style='display:none;padding-top: 10px;'><span name='items_org'>" + SchemeDetails.items_org(rowIndex) +"</span></td></tr></table>";
                }
            },{
            	text: kq.scheme.workName,//考勤员
                dataIndex: 'clerk_fullname',
                flex: 1.5,
                menuDisabled: true,
                sortable: false,
                renderer: function (value, data, record, rowIndex) {
                	if(record.data.org_id == "-1") {
                		return "";
                	}else {
                		var nameAll = record.data.clerk_username==''?"":record.data.clerk_username+"(" + record.data.clerk_fullname +")";
                		var nameMap = SchemeDetails.getSubStrLength(nameAll,24);
                		var name = nameMap.get("str");
                		var title = '';
                		if(nameMap.get("len")) {
                			title = nameAll;
                		}
	                    return "<span name='items_clerk'>" + SchemeDetails.items_clerk(rowIndex, record.data.clerk_imgPath, name, title) + "</span>";
                	}
                }
            },{
            	text: kq.scheme.checkName,//审核人
                dataIndex: 'reviewer_fullname',
                flex: 1,
                menuDisabled: true,
                sortable: false,
                renderer: function (value, data, record, rowIndex) {
                	if(record.data.org_id == "-1") {
                		return "";
                	}else {
                		var reviewid = record.data.reviewer_id;
                		var nameAll = record.data.reviewer_fullname;
                		var nameMap = SchemeDetails.getSubStrLength(nameAll,12);
                		var name = nameMap.get("str");
                		var title = '';
                		if(nameMap.get("len")) {
                			title = nameAll;
                		}
                		var imgPath = record.data.reviewer_imgPath?record.data.reviewer_imgPath:"/images/photo.jpg";
	                    return "<span name='items_reviewer' style='text-align:center;'>" + SchemeDetails.items_reviewer(rowIndex, imgPath, name, title) + "</span>";
                	}
                }
            }]
		});
		
		SchemeDetails.panel3 = Ext.widget('panel',{
			margin: '20 0 0 0',
			border: false,
			items: [gridPanel,{
				xtype: 'checkbox',
				boxLabel: kq.scheme.secondaryAdmin,
				width: 601,
				id: 'secondary_admin',
				height: 28,
				margin: '2 0 0 0',
				checked: SchemeDetails.secondary_admin == 1?true:false,
				style: 'border-bottom:1px solid #c5c5c5;padding-left:5px;'
			}]
		});
	},
	
	createWindow: function(){
		
		var form = Ext.widget({
			xtype:'form',
			border:false,
			layout: 'card',
			id: 'createKqOrg',
			width: 601,
            height: 366,
			items:[],
			minButtonWidth:50,
			buttonAlign: 'bottom',
			buttons:[
            	{xtype:'tbfill'},
         		{
         			text:kq.scheme.upStep,//上一步
         			id: 'previousStep',
         			hidden: true,
         			height: 22,
         			handler:function(){
         				SchemeDetails.stepview.previousStep();
         			}
         		},
         		{
         			text:kq.scheme.nextStep,//下一步
         			id: 'nextStep',
         			height: 22,
         			handler:function(){
         				if(SchemeDetails.currPage == 1) {
         					//名称必填
         					var nameValue = Ext.util.Format.trim(Ext.getCmp("name").getValue());
         					if(Ext.isEmpty(nameValue)) {
         						Ext.showAlert(kq.scheme.writeName);//请填写名称
         						return;
         					}
         					//考勤员必填
         					if(!SchemeDetails.clerk_username) {
         						Ext.showAlert(kq.scheme.chooseClerk);//请选择考勤员
         						return;
         					}
         				}else if(SchemeDetails.currPage == 2){
         					var items = Ext.getCmp("checkboxdbValue").items.items;
         					var flag = true;
         					for(var i = 0; i < items.length; i++) {
         						if(items[i].checked) {
         							flag = false;
         							break;
         						}
         					}
         					if(flag) {
         						Ext.showAlert(kq.scheme.chooseCbase);//请选择人员库
         						return;
         					}
         					//应用范围必填
         					if(!SchemeDetails.org_scope) {
         						Ext.showAlert(kq.scheme.chooseOrgScope);//请选择应用范围
         						return;
         					}
         				}
         				SchemeDetails.stepview.nextStep();
         			}
         		},
         		{
         			text: common.button.ok,//确定/关闭
         			id: 'enterOk',
         			height: 22,
         			hidden: SchemeDetails.scheme_id?false:true,
                    handler: function (e, c) {
                		var forms = this.up('form').getForm().getValues();
               	 		SchemeDetails.saveData(forms);
                    }
         		},
         		{xtype:'tbfill'}
         	]
		});
		form.setActiveItem(SchemeDetails.panel0);
		SchemeDetails.currPage = 1;
		//第一步，第二步，头部流程控件
		SchemeDetails.stepview = Ext.widget("stepview",{
			listeners:{
				stepchange:function(stepview,step){
					SchemeDetails.changeStep(stepview.currentIndex);
				}	
			},
			height: 45,
			renderTo:Ext.getBody(),
			freeModel:SchemeDetails.scheme_id?true:false,
			stepData:[{name:kq.scheme.info},{name:kq.scheme.range},{name:kq.scheme.select},{name:kq.scheme.orgAppeal}]
		});
		
		var win = Ext.widget("window", {
            title: SchemeDetails.scheme_id ? kq.scheme.editKqOrg : kq.scheme.createKqOrg,
            id: 'win',
            resizable: false,
            border: false,
            modal: true,
            height: 451,
            closeAction: 'destroy',
            padding: '5px 0 0 0;',
            items: [{
            	xtype: 'panel',
            	width: 580,
            	border: false,
            	margin: '0 0 0 20px',
            	items: [SchemeDetails.stepview]
            },form],
            listeners:{
                'beforeclose':function(){
                	if(Ext.getCmp("person_picker_single_view"))//如果选人框还在，在关闭的时候关掉
                		Ext.getCmp("person_picker_single_view").close();
                	if(Ext.getCmp("person_picker_multiple_view"))
                		Ext.getCmp("person_picker_multiple_view").close();
                }
            }
            
		});
		win.show();
		
	},
	//上一步，下一步切换
	changeStep: function(index){
		
		if(index == 0) {
			SchemeDetails.currPage = 1;
			if(!SchemeDetails.scheme_id && !Ext.getCmp("enterOk").isHidden())
				Ext.getCmp("enterOk").setHidden(true);
			
			Ext.getCmp("previousStep").setHidden(true);
			Ext.getCmp("nextStep").setHidden(false);
			Ext.getCmp("createKqOrg").setActiveItem(SchemeDetails.panel0);
		}
		if(index == 1) {
			SchemeDetails.currPage = 2;
			if(!SchemeDetails.scheme_id && !Ext.getCmp("enterOk").isHidden())
				Ext.getCmp("enterOk").setHidden(true);
			Ext.getCmp("previousStep").setHidden(false);
			Ext.getCmp("nextStep").setHidden(false);
			Ext.getCmp("createKqOrg").setActiveItem(SchemeDetails.panel1);
		}
		if(index == 2) {
			SchemeDetails.currPage = 3;
			if(!SchemeDetails.scheme_id && !Ext.getCmp("enterOk").isHidden())
				Ext.getCmp("enterOk").setHidden(true);
			Ext.getCmp("previousStep").setHidden(false);
			Ext.getCmp("nextStep").setHidden(false);
			Ext.getCmp("createKqOrg").setActiveItem(SchemeDetails.panel2);
		}
		if(index == 3) {
			SchemeDetails.currPage = 4;
			if(!SchemeDetails.scheme_id && Ext.getCmp("enterOk").isHidden())
				Ext.getCmp("enterOk").setHidden(false);
			Ext.getCmp("nextStep").setHidden(true);
			Ext.getCmp("previousStep").setHidden(false);
			Ext.getCmp("createKqOrg").setActiveItem(SchemeDetails.panel3);
		}
	},
	
	
	//第一步false:选择考勤员和true:审核人
	changework: function(flag){
		var picker = new PersonPicker({
            multiple: false,//因为只能选择一个人，不需要多选框
            isSelfUser: flag,//是否选择自助用户
            selfUserIsExceptMe: false,
            isMiddle: true,//是否居中显示
            orgid: SchemeDetails.org_unit, // 组织机构，所选上报数据机构必须为第二步应用范围选择的机构范围内。
            isPrivExpression: false,
            callback: function (c) {
            	if(flag) {
            		Ext.getCmp("reviewLabel").setHtml(c.name);
            		Ext.getDom("reviewerId").src = c.photo;
            		Ext.getDom("reviewerId").title = c.name;
            		SchemeDetails.reviewer_id = c.id;
            		SchemeDetails.reviewer_fullname = c.name;
            	}else {
            		var json = {};
            		json.type = "getImg";
            		json.busiName = c.userName;
            		var map = new HashMap();
            		map.put("jsonStr",JSON.stringify(json));
            		Rpc({functionId:'KQ00020201',success:function(response,action){
            			var result = Ext.decode(response.responseText);
            			if (result.succeed) { 
            				var imgPath = result.returnStr.return_data;
            				if(imgPath) {
            					Ext.getCmp("workId").setSrc(imgPath);
            				}
            				Ext.getCmp("clerkLabel").setHtml(c.userName + "(" + c.name + ")");
            				SchemeDetails.clerk_username = c.userName;
            				SchemeDetails.clerk_fullname = c.name;
            			} else {  
            				Ext.showAlert(result.message+"！");
            			}
            		}},map);
            	}
            }
		});
		picker.open();
	},
	
	//1:item项目 2:class班次
	showAllItem: function(type) {
		var list = new Array();
		var list_sort = new Array();
		var map = new HashMap();
		//将删除的放在以及集合里面，添加的放在一个集合里面，这样就是全部的数据了，添加和删除就两个结合进行操作，保证数据正确
		if(type == "kqItem") {
			if(!SchemeDetails.mapItemDelete) {
				SchemeDetails.mapItemDelete = SchemeDetails.item_ids_All;
			}
			map = SchemeDetails.mapItemDelete;
			list_sort = SchemeDetails.item_id_list;
		}else {
			if(!SchemeDetails.mapClassDelete) {
				SchemeDetails.mapClassDelete = SchemeDetails.class_ids_All;
			}
			map = SchemeDetails.mapClassDelete;
			list_sort = SchemeDetails.class_id_list;
		}
		//按照后台传过来的顺序进行排序
		for(var i = 0; i < list_sort.length; i++) {
			var sort = map[list_sort[i]];
			if(sort) {
				var mapJust = new HashMap();
				mapJust.put("itemid",list_sort[i]);
				var itemdesc = "";
				if(type == "kqClass") {
					itemdesc = sort.split(",")[0];
				}else {
					itemdesc = sort;
				}
				mapJust.put("itemdesc_value",itemdesc);
				mapJust.put("itemdesc",sort);
				list.push(mapJust);
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
            width: Ext.isIE?280:283,
            height: 325,
            forceFit:true,
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
                enableKeyNav: true,
                checkOnly: true
            }),
            style: Ext.isIE?'':'padding-left:5px;',
            columns: [
                { text: kq.label.name,border:false,menuDisabled:true, dataIndex: 'itemdesc_value',flex:0.9}
            ]
        });
        
		var winItem = Ext.widget("window", {
            title: type == 'kqItem'?kq.scheme.selectKqItem:kq.scheme.selectKqClass,
            id: 'winItem',
            resizable: false,
            border: false,
            modal: true,
            width: 290,
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
                        Ext.Array.each(sel,function(record,index){
                        	if(type == "kqItem") {
                            	if(!SchemeDetails.selectMapItem)
                            		SchemeDetails.selectMapItem = new HashMap();
                            	delete SchemeDetails.mapItemDelete[record.data.itemid];
                            	SchemeDetails.selectMapItem.put(record.data.itemid, record.data.itemdesc);
                            	SchemeDetails.item_ids_array.push(record.data.itemid);
                    		}else {
                    			if(!SchemeDetails.selectMapClass)
                    				SchemeDetails.selectMapClass = new HashMap();
                    			delete SchemeDetails.mapClassDelete[record.data.itemid];
                            	SchemeDetails.selectMapClass.put(record.data.itemid, record.data.itemdesc);
                            	SchemeDetails.class_ids_array.push(record.data.itemid);
                    		}
                        });
                        SchemeDetails.getShow(type);
                        winItem.close();
                    }
                },
                {
                    text:common.button.cancel,//取消
                    handler:function(){
                    	winItem.close();
                    }
                },{xtype:'tbfill'}
            ]
            
		});
		winItem.show();
		
	},
	
	//1:item项目 2:class班次
	getShow: function(type) {
		var x = 0;//x坐标
		var y;//y坐标
		var map = new HashMap();//需要循环的选择的map
		var mapMove = new HashMap();//移动需要循环的map
		var id_type = "";//每个panel表格的id
		var id_add = "";//添加的id
		var count = 0;//数量
		var idsArray = "";
		if(type == "kqItem") {
			 y = 2;
			 map = SchemeDetails.selectMapItem;
			 id_type = "kqItem";
			 id_add = "addItem";
			 idsArray = SchemeDetails.item_ids_array;
		}else {
			 y = 0;
			 map = SchemeDetails.selectMapClass;
			 id_type = "kqClass";
			 id_add = "addClass";
			 idsArray = SchemeDetails.class_ids_array;
		}
		//先销毁所有的panel，通过组成的map重新生成界面，只要map中顺序都对所有的都对了
		Ext.getCmp(id_type).removeAll();
		SchemeDetails.classTipY = 8;
		for(var k = 0; k < idsArray.length; k++) {
			var lastValue = "";
			var tipPanel = "";
			var detail = "";
			key = idsArray[k];
			if(Ext.isEmpty(key) || !map[key]) {
				continue;
			}
			if(type == "kqClass") {
				var value = map[key].split(",");
				var left = 47;
				if(Ext.isGecko || Ext.isIE) {
					left = 44;
				}
				for(var i = 1; i < value.length; i++) {
					if(i != 1) {
						detail += "<br><span style='padding-left:" + left + "px;'>";
					}
					detail += value[i] + "</span>";
				}
				lastValue = value[0];
				
				if(detail != '') {
					var margin = Ext.isIE?'0 0 0 0':'-3px 0 0 -6px';
					var y1 = y + SchemeDetails.classTipY;
					//var x1 = x + 61;
					var x1 = ((x > 390)?(x-121):(x + 61));
					tipPanel = Ext.widget({
		                xtype: 'panel',
		                height: 50,
		                width: 120,
		                frame: Ext.isIE?false:true,
		                x: x1,
		                y: y1,
		                hidden: true,
		                style: 'background-color: #fff8d2;z-index:99;',
		    			id: 'kqClassTip_' + key,
		                margin: '10 0 0 0',
		                html: "<div style='background:#fff8d2;width:124px;height:50px;margin:" + margin + 
		                	";cursor:pointer;-webkit-transform: scale(0.9);font-size: 11px;' ><span style='width:120px;height:50px;'>" + 
		                	kq.scheme.timeRange + detail + "</span></div>"
		            }); 
				}
			}else {
				lastValue = map[key];
			}
			var panel_on = Ext.widget({
                xtype: 'panel',
                height: 38,
                width: 60,
                frame: Ext.isIE?false:true,
                style: 'background: #fff !important;',
                html: "<div style='background:#fff !important;width:60px;height:30px;display: table-cell;vertical-align: middle;" + 
                		"text-align: center;' ><span style='width:50px;height:30px;font-size: 11px;-webkit-transform: scale(0.9);" +
                		"line-height: 1.2;'>" + lastValue + "</span></div>"
            });
			
			var panel = Ext.widget({
                xtype: 'panel',
                height: 40,
                width: 70,
                border: false,
                x: x,
                y: y,
                id: id_type + "_" + key,
                //拖动参数，必须
    			/*draggable:{
    				moveOnDrag: false,
    				onDrag:function(e){
    					SchemeDetails.move(e.xy[0], e.xy[1], this.id, id_type);
    				}
    			},*///去掉拖拽，现在页面有排序按钮
                margin: '10 0 0 0',
                items: [panel_on],
                html: "<div style='height:40px;width:70px;position: absolute;top: -9px;' " +
                		"onmouseover='SchemeDetails.showDelIcon(\"" + key + "\",\"block\",\"" + id_type + "\")' " +
                		"onmouseout='SchemeDetails.showDelIcon(\"" + key + "\",\"none\",\"" + id_type + "\")'>" +
                		"<img onclick='SchemeDetails.delItem(\"" + key + "\",\"" + map[key] + "\",\"" + id_type + "\")' " +
                		"id='imgdel" + id_type + "_" + key + "' src='/workplan/image/remove.png' " +
                		"style='display:none;width:15px;height:15px;cursor:pointer;position:absolute;top:8px;left:52px;' /></div>"
            });
			
			mapMove.put(id_type + "_" + key, count + "," + x + "," + y);
			Ext.getCmp(id_type).add(panel);
			if(type == "kqClass" && detail != '') {
				Ext.getCmp(id_type).add(tipPanel);
			}
			
			count++;
			if(count%7 != 0) {
				x = x + 80;//横坐标
			}else {//换行了
				x = 0;
				y += 45;
			}
		}
		var addItemPanel = Ext.widget({
            xtype: 'panel',
            height: 40,
            width: 60,
            border: false,
            x: x,
            y: y+8,
            html: "<img style='cursor:pointer;' id='" + id_add + "' src='/images/new_module/nocycleadd.png' onclick='SchemeDetails.showAllItem(\"" + id_type + "\")'>"
		});
		
		Ext.getCmp(id_type).add(addItemPanel);
		
		if(type == "kqItem") {
			SchemeDetails.selectMapItem = map;
			SchemeDetails.countItem = count;
			SchemeDetails.mapItem = mapMove;
		}else {
			SchemeDetails.selectMapClass = map;
			SchemeDetails.countClass = count;
			SchemeDetails.mapClass = mapMove;
		}
	},
	
	//type:class:项目，item:班次
	showDelIcon: function(key, flag, type) {
		Ext.getDom("imgdel" + type + "_" + key).style.display = flag;
		if(type == "kqClass" && Ext.getCmp("kqClassTip_" + key)) {
			if(flag == 'none')
				Ext.getCmp("kqClassTip_" + key).setHidden(true);
			else {
				Ext.getCmp("kqClassTip_" + key).setHidden(false);
			}
		}
	},
	//type:class:项目，item:班次
	delItem: function(key, value, type) {
		
		if(type == "kqItem") {
			if(SchemeDetails.getMapLength(SchemeDetails.mapItemDelete) == 0) {
				SchemeDetails.mapItemDelete = new HashMap();
			}
			//将删除的放在以及集合里面，添加的放在一个集合里面，这样就是全部的数据了，添加和删除就两个结合进行操作，保证数据正确
			SchemeDetails.mapItemDelete.put(key, value);
			delete SchemeDetails.selectMapItem[key];
			var index = SchemeDetails.in_array(key, SchemeDetails.item_ids_array);
			if (index > -1) {
				SchemeDetails.item_ids_array.splice(index, 1);
			}
		}else {
			//将删除的放在以及集合里面，添加的放在一个集合里面，这样就是全部的数据了，添加和删除就两个结合进行操作，保证数据正确
			if(SchemeDetails.getMapLength(SchemeDetails.mapClassDelete) == 0) {
				SchemeDetails.mapClassDelete = new HashMap();
			}
			SchemeDetails.mapClassDelete.put(key, value);
			delete SchemeDetails.selectMapClass[key];
			var index = SchemeDetails.in_array(key, SchemeDetails.class_ids_array);
			if (index > -1) {
				SchemeDetails.class_ids_array.splice(index, 1);
			}
		}
		Ext.getCmp(type + "_" + key).destroy();
		SchemeDetails.getShow(type);
	},
	
	//考勤项目排序功能type:kqItem:考勤项目type:kqClass：班次
	sortItem: function(type) {
		var list = new Array();
		var map = new HashMap();
		var idsArray = "";
		if(type == "kqItem") {
			 map = SchemeDetails.selectMapItem;
			 idsArray = SchemeDetails.item_ids_array;
		}else {
			 map = SchemeDetails.selectMapClass;
			 idsArray = SchemeDetails.class_ids_array;
		}
		//按照后台传过来的顺序进行排序
		for(var i = 0; i < idsArray.length; i++) {
			var mapJust = new HashMap();
			mapJust.put("itemid",idsArray[i]);
			mapJust.put("itemdesc",type == "kqItem"?map[idsArray[i]]:map[idsArray[i]].split(",")[0]);
			list.push(mapJust);
		}
		
		var store = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc'],
            data: list,
            autoLoad: true
        });
        //生成表格
        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: Ext.isIE?280:283,
            height: 325,
            viewConfig : {
    			plugins:{
    				ptype:'gridviewdragdrop',
    				dragText:common.label.DragDropData//拖放数据
    			}
    		},
    		style: Ext.isIE?'':'padding-left:5px;',
            columns: [
                { text: kq.label.name,border:false, sort:false, menuDisabled:true, dataIndex: 'itemdesc',flex:0.9}
            ]
        });
        
		var winItem = Ext.widget("window", {
            title: kq.scheme.doSort,
            id: 'win_Item',
            resizable: false,
            border: false,
            modal: true,
            width: 290,
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
                    	idsArray = new Array();
    					var items = store.data.items;
    					for(var i = 0; i < items.length; i++) {
    						if(items[i] != "") 
    							idsArray.push(items[i].data.itemid);
    					}
    					if(type == "kqItem") {
    						 SchemeDetails.item_ids_array = idsArray;
    					}else {
    						 SchemeDetails.class_ids_array = idsArray;
    					}
    					SchemeDetails.getShow(type);
                        winItem.close();
                    }
                },
                {
                    text:common.button.cancel,//取消
                    handler:function(){
                    	winItem.close();
                    }
                },{xtype:'tbfill'}
            ]
            
		});
		winItem.show();
		
	},
	
	//显示审核员的删除按钮
	showReview: function(id,flag, reviewId) {
		if(id == "review_id_delete" && SchemeDetails.reviewer_id != "") {
			Ext.getDom(id).style.display = flag;
		}else if(id != "review_id_delete"){
			if(Ext.getDom(reviewId).innerText != '' && flag == 'block')
				Ext.getDom(id).style.display = flag;
			else if(flag == 'none')
				Ext.getDom(id).style.display = flag;
		}
	},
	
	// 删除方案审核人
	deleteReview: function(){
		if(Ext.isEmpty(SchemeDetails.scheme_id) || Ext.isEmpty(SchemeDetails.old_reviewer_id)){
			// 新建的不需要校验
			SchemeDetails.deleteReviewDone();
		}else{
			SchemeDetails.checkdeleteReview("2", SchemeDetails.reviewer_id, "");
		}
	},
	// 校验后  删除方案审核人操作
	deleteReviewDone: function(){
		Ext.getDom("reviewerId").src = "/images/photo.jpg";
		Ext.getDom("reviewerId").title = "";
		SchemeDetails.reviewer_id = "";
		SchemeDetails.reviewer_fullname = "";
		Ext.getDom("review_id_delete").style.display = "none";
		Ext.getCmp("reviewLabel").setHtml("");
	},
	
	//第二步保存限制条件
	saveComplexCond: function(c_expr,heapFlag,initflag,fieldid) {
		SchemeDetails.cond = c_expr;
	},
	
	//第四步，删除审核人
	delReview: function(imgId, delId, nameId, rowIndex) {
		var orgid = SchemeDetails.filling_agencys[rowIndex].org_id;
		var oldOrg = null;
		var bool = true;
		// 如果没有scheme_id 或 旧的机构信息map_oldOrg为空 说明是新建的 不需要校验可以直接删除
		if(Ext.isEmpty(SchemeDetails.scheme_id) || !SchemeDetails.map_oldOrg) {
			bool = false;
		}else{
			oldOrg = SchemeDetails.map_oldOrg[orgid];
			// 如果获取对应的机构信息为空 或者 机构为空 或者 审核人为空 不需要校验可以直接删除
			if(null==oldOrg || Ext.isEmpty(oldOrg.org_id_detail) || Ext.isEmpty(oldOrg.review_name_old)) {
				bool = false;	
			}
		}
		
		if(bool) {
			SchemeDetails.checkdeleteReview("4", oldOrg.review_name_old, oldOrg.org_id_detail, imgId, delId, nameId, rowIndex);
		}else{
			SchemeDetails.delReviewDone(imgId, delId, nameId, rowIndex);
		}
		
	},
	// 校验后 第四步，删除机构审核人操作
	delReviewDone: function(imgId, delId, nameId, rowIndex) {
		Ext.getDom(imgId).src = "/images/photo.jpg";
		Ext.getDom(imgId).title = "";
		Ext.getDom(nameId).innerText = "";
		Ext.getDom(delId).style.display = "none";
		SchemeDetails.filling_agencys[rowIndex].reviewer_fullname = "";
		SchemeDetails.filling_agencys[rowIndex].reviewer_id = "";
	},
	/**
	 * 校验审核人是否存在待办
	 * flag 		=2方案审核人；=4机构审核人
	 * reviewer_id 	审核人id（加密）
	 * org_id_e		上报机构（加密）
	 */
	checkdeleteReview: function(flag, reviewer_id, org_id_e, imgId, delId, nameId, rowIndex){
		// 55945 先校验是否存在待办 否则不允许删除
		Ext.showConfirm((kq.scheme.delReviewConfirm), function (value) {
			if('yes' == value) {
				var json = {};
				json.type = "checkReviewPerson";
				json.scheme_id = SchemeDetails.scheme_id;
				json.old_name = reviewer_id;
				json.org_id = org_id_e;
				var map = new HashMap();
				map.put("jsonStr", JSON.stringify(json));
				Rpc({functionId:'KQ00020201',async:false,success:function(form,action){
					var result = Ext.decode(form.responseText);
					if("success" == result.returnStr.return_code) {
						if("2" == flag){
							SchemeDetails.deleteReviewDone();
						}else if("4" == flag){
							SchemeDetails.delReviewDone(imgId, delId, nameId, rowIndex);
						}
					}else{
						if("-1" == result.returnStr.return_msg){
							Ext.showAlert(kq.scheme.delReviewMsg);
							return;
						}
					}
				}},map);
			}
		});
	},
	//第四步选择考勤员flag:false:修改考勤员，true：修改审核人
	changeClerk: function(id,flag,rowIndex, review_id) {
		var picker = new PersonPicker({
            multiple: false,//因为只能选择一个人，不需要多选框
            isSelfUser: flag,//是否选择自助用户
            selfUserIsExceptMe: false,
            orgid: SchemeDetails.org_unit, // 组织机构，所选上报数据机构必须为第二步应用范围选择的机构范围内。
            isMiddle: true,//是否居中显示
            isPrivExpression: false,
            callback: function (c) {
            	if(flag) {
            		Ext.getDom(id).src = c.photo;
            		var nameMap = SchemeDetails.getSubStrLength(c.name,12);
            		Ext.getDom(review_id).innerText = nameMap.get("str");
            		if(nameMap.get("len")) {
            			Ext.getDom(review_id).title = c.name;
            		}
            		//记录下新的名称，进行删除对应的代办和流程操作
            		if(SchemeDetails.scheme_id) {
	            		var org_id = SchemeDetails.filling_agencys[rowIndex].org_id;
	            		var map_ = new HashMap();
	            		if(SchemeDetails.map_oldOrg[org_id]) {
	            			var personArray = SchemeDetails.map_oldOrg[org_id];
	            			for(var name in personArray){
								if(personArray.hasOwnProperty(name)){
									map_.put(name, personArray[name]);
								}
	            			}
	            		}
	            		map_.put("review_name_new", c.id);
	            		SchemeDetails.map_oldOrg[org_id] = map_;
            		}
            		SchemeDetails.filling_agencys[rowIndex].reviewer_fullname = c.name;
            		SchemeDetails.filling_agencys[rowIndex].reviewer_id = c.id;
            		SchemeDetails.store.data.items[rowIndex].data.reviewer_imgPath = c.photo;
            		SchemeDetails.store.data.items[rowIndex].data.reviewer_id = c.id;
            		SchemeDetails.store.data.items[rowIndex].data.reviewer_fullname = c.name;
            		SchemeDetails.store.commitChanges();
            		
            	}else {
            		var json = {};
            		json.type = "getImg";
            		json.busiName = c.userName;
            		var map = new HashMap();
            		map.put("jsonStr",JSON.stringify(json));
            		Rpc({functionId:'KQ00020201',success:function(response,action){
            			var result = Ext.decode(response.responseText);
            			if (result.succeed) { 
            				var imgPath = result.returnStr.return_data;
            				if(imgPath) {
            					Ext.getDom("img_clerk_" + rowIndex).src = imgPath;
            				}
            				var nameMap = SchemeDetails.getSubStrLength(c.userName + "(" + c.name + ")",24);
                    		Ext.getDom(id).innerText = nameMap.get("str");
                    		if(nameMap.get("len")) {
                    			Ext.getDom(id).title = c.userName + "(" + c.name + ")";
                    		}
                    		if(SchemeDetails.scheme_id) {
	                    		var org_id = SchemeDetails.filling_agencys[rowIndex].org_id;
	                    		var map_ = new HashMap();
	                    		if(SchemeDetails.map_oldOrg[org_id]) {
	                    			var personArray = SchemeDetails.map_oldOrg[org_id];
	                    			for(var name in personArray){
	        							if(personArray.hasOwnProperty(name)){
	        								map_.put(name, personArray[name]);
	        							}
	                    			}
	                    		}
	                    		map_.put("clerk_name_new", c.userName);
	                    		SchemeDetails.map_oldOrg[org_id] = map_;
                    		}
                    		
                    		SchemeDetails.filling_agencys[rowIndex].clerk_fullname = c.name
                    		SchemeDetails.filling_agencys[rowIndex].clerk_username = c.userName;
                    		SchemeDetails.store.data.items[rowIndex].data.clerk_imgPath = imgPath?imgPath:"/images/photo.jpg";
                    		SchemeDetails.store.data.items[rowIndex].data.clerk_username = c.userName;
                    		SchemeDetails.store.data.items[rowIndex].data.clerk_fullname = c.name;
                    		SchemeDetails.store.commitChanges();
            			} else {  
            				Ext.showAlert(result.message+"！");
            			}
            		}},map);
            		
            	}
            }
		});
		picker.open();
	},
	
	//记下单个字符串的长度，和字符串变成双字节的长度，这样可以有效的截取中英文都存在的情况下长度截取问题
    getSubStrLength:function(str,length) {
    	var map = new HashMap();
    	var singleString = 0;//记下循环的字符串长度
		var doubleString = 0;//双字节，如果长度超出24的，截取单个字节的singleString长度
		for (var i = 0; i < str.length; i++) {//遍历字符串
		      if(/[^\u0000-\u00ff]/g.test(str[i])) {
		    	  doubleString = doubleString + 2;
		      }else {
		    	  doubleString++;
		      }
		      if(doubleString <= length)
		    	  singleString++;
		      else {
		    	  str = str.substr(0,singleString) + "...";
		    	  map.put("len", "1");
		    	  break;
		      }
	    }
		map.put("str", str);
		return map;
    },
	
	//保存
	saveData: function(form) {
		//名称必填
		if(Ext.getCmp("name").value == "") {
			Ext.showAlert(kq.scheme.writeName);//请填写名称
			return;
		}
		//考勤员必填
		if(!SchemeDetails.clerk_username) {
			Ext.showAlert(kq.scheme.chooseClerk);//请选择考勤员
			return;
		}
		var items = Ext.getCmp("checkboxdbValue").items.items;
		var flag = true;
		var cbase = "";
		for(var i = 0; i < items.length; i++) {
			if(items[i].checked) {
				cbase = cbase + "," + items[i].inputValue;
				flag = false;
			}
		}
		if(flag) {
			Ext.showAlert(kq.scheme.chooseCbase);//请选择人员库
			return;
		}
		form.cbase = cbase.substring(1);//人员库
		//应用范围必填
		if(!SchemeDetails.org_scope) {
			Ext.showAlert(kq.scheme.chooseOrgScope);//请选择应用范围
			return;
		}
		
		var flag = false;
		Ext.each(SchemeDetails.filling_agencys,function(filling_agency,index){
    		if(filling_agency.clerk_username == "") {
    			flag = true;
    			return false;
    		}
        });
		
		if(flag || SchemeDetails.filling_agencys.length == 0) {
			Ext.showAlert(kq.scheme.pleaseInsertClerk);//请选择数据上报中的考勤员
			return;
		}
		//考勤人
		form.clerk_username = SchemeDetails.clerk_username;
		form.clerk_fullname = SchemeDetails.clerk_fullname;
		//审核人
		form.reviewer_id = SchemeDetails.reviewer_id;
		form.reviewer_fullname = SchemeDetails.reviewer_fullname;
		//员工确认考勤结果
		form.confirm_flag = Ext.getCmp("confirm_flag").value?1:0;
		
		//月考勤结果提交上级机构审批--第四步
		form.secondary_admin = Ext.getCmp("secondary_admin").value?1:0;
		
		//所属机构
		if(Ext.getCmp("b0110_combox").value == "") {
			form.b0110 = "";
		}else {
			form.b0110 = SchemeDetails.b0110 == '`'?"":SchemeDetails.b0110;
		}
		//应用范围
		form.org_scope = SchemeDetails.org_scope;
		
		//限制条件
		if(Ext.getCmp("cond_check").value) {
			form.cond = SchemeDetails.cond;
		}else {
			form.cond = "";
		}
		
		//考勤项目
		if(SchemeDetails.item_ids_array) {
			var value = "";
			for(var i = 0; i < SchemeDetails.item_ids_array.length; i++) {
				value += "," + SchemeDetails.item_ids_array[i];
			}
			form.item_ids = value.substring(1);
		}
		//日明细
		if(SchemeDetails.class_ids_array) {
			var valueClass = "";
			for(var i = 0; i < SchemeDetails.class_ids_array.length; i++) {
				valueClass += "," + SchemeDetails.class_ids_array[i];
			}
			form.class_ids = valueClass.substring(1);
		}
		//是否选择日明细
		form.dayDetailEnabled = SchemeDetails.day_detail_enabled;
		//数据上报机构
		form.filling_agencys = SchemeDetails.filling_agencys;

		if(SchemeDetails.scheme_id)
			form.scheme_id = SchemeDetails.scheme_id;
		
		//记录下所有的旧名和旧的上报机构，这样修改的时候修改对应的名称和上报机构的待办
		form.map_oldOrg = SchemeDetails.map_oldOrg;
		form.old_name = SchemeDetails.name;
		var json = {};
		json.type = "save";
		json.info = form;
		var map = new HashMap();
		map.put("jsonStr",JSON.stringify(json));
		Rpc({functionId:'KQ00020201',success:function(response,action){
			var result = Ext.decode(response.responseText);
			if (result.succeed) { 
				Ext.getCmp("win").close();
				if(SchemeDetails.scheme_id) {//不是新增的
					SchemeList.changePage();
				}else {//是新增的
					SchemeList.totalCount += 1;
					SchemeList.currentPage = 1;
					SchemeList.changePage();
				}
			} else {  
				Ext.showAlert(result.message+"！");
			}
		}},map);
	},
	
	addOrg: function(rowIndex) {
		if(!SchemeDetails.org_scope) {
			Ext.showAlert(kq.scheme.chooseRange);
			return;
		}
		var el = document.getElementById("kqAppeal");
		var picker = new PersonPicker({
            multiple: true,
            text: common.button.addfield,
            addunit: true, //是否可以添加单位
            adddepartment: true, //是否可以添加单位
            orgid: SchemeDetails.org_scope, // 组织机构，所选上报数据机构必须为第二步应用范围选择的机构范围内。
            addpost: false,
            isPrivExpression: false,
            callback: function (c) {
            	if(c.length > 0 && SchemeDetails.store.data.items.length == 1 && SchemeDetails.store.data.items[0].data.org_id=='-1') {
            		SchemeDetails.store.removeAll();
            		SchemeDetails.filling_agencys.splice(0,1);
            	}
                for (var i = 0; i < c.length; i++) {
                    var cc = c[i];
                    var id = cc.id;//a0100
                    id = cc.rawType + id;
                    var flags = false;
                    for(var j = 0; j < SchemeDetails.filling_agencys.length; j++) {
            			if(SchemeDetails.filling_agencys[j].org_id == id) {
            				flags = true;
            				break;
            			}
            		}
                    if(flags == true) {
                    	continue;
                    }
                    var value = cc.name;//名称
                    rowIndex++;
                    //给store中添加，同时给SchemeDetails.filling_agencys添加数据
                    SchemeDetails.insertData(rowIndex, id, value);
                    
    				//现在完全根据行号塞值，取值，所以一旦删除了，就直接重新给值
                    SchemeDetails.resetSort();
                }
            }
        }, el);
        picker.open();
	},
	//删除数据上报机构
	delOrg: function(rowIndex) {
        //确定删除这{0}条数据吗？
		Ext.showConfirm(kq.scheme.confirmdelete,function (v) {
			if(v == "yes") {
				var flag = 0;//因为循环删除的时候第一个删除了，对应的index也变了，所以重新找index
				
				//给store中添加，同时给SchemeDetails.filling_agencys添加数据
				SchemeDetails.store.remove(SchemeDetails.store.data.items[rowIndex]);
				SchemeDetails.filling_agencys.splice(rowIndex,1);
				
				if(SchemeDetails.filling_agencys == 0) {
                    SchemeDetails.insertData(0, "-1", "");
                    SchemeDetails.filling_agencys.splice(0,1);
				}
				//现在完全根据行号塞值，取值，所以一旦删除了，就直接重新给值
				SchemeDetails.resetSort();
			}
		});
	},
	
	resetSort: function() {
		var items_orgs = Ext.query("*[name=items_org]");
		Ext.each(items_orgs,function(items_org,index){
			items_org.innerHTML = SchemeDetails.items_org(index);
		});
		//现在完全根据行号塞值，取值，所以一旦删除了，就直接重新给值
		var items_clerks = Ext.query("*[name=items_clerk]");
    	Ext.each(items_clerks,function(items_clerk,index){
    		items_clerk.innerHTML = SchemeDetails.items_clerk(index, items_clerk.childNodes[0].src, items_clerk.childNodes[1].innerText, items_clerk.childNodes[1].title);
        });
        
        var items_reviewers = Ext.query("*[name=items_reviewer]");
    	Ext.each(items_reviewers,function(items_reviewer,index){
    		items_reviewer.innerHTML = SchemeDetails.items_reviewer(index, items_reviewer.childNodes[0].src, items_reviewer.childNodes[2].innerText, items_reviewer.childNodes[2].title);
        });
	},
	
	//这里采用的是通过行号进行增删改，插入，对于SchemeDetails.filling_agency操作也方便，能保证正确的顺序，但是代码就麻烦，每次插入，删除，排序完了，需要重新对行号排序
	items_org: function(index) {
		return "<img src='/images/new_module/org_del.png' " +"onclick='SchemeDetails.delOrg(\"" + index + "\")' title='" + kq.label.del + "' style='height: 16px;width: 16px;float:right;cursor:pointer;'>" + 
		"<img src='/images/new_module/org_add.png' onclick='SchemeDetails.addOrg(\"" + index + "\")' title='" + kq.label.insert + "' " +
		"style='height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;'>";
	},
	
	items_clerk: function(index, imgPath, name, title) {
		return "<img width=30 height=30 id='img_clerk_" + index + "' style='border-radius: 50%;cursor:pointer;margin:5px;position:relative;' " +
		"onclick='SchemeDetails.changeClerk(\"clerk_" + index + "\",false,\"" + index + "\",\"\")' src='" + imgPath + "' />" +
		"<span style='position: absolute;float: left;margin: 15px 0 0 2px;'><span " + (title==""?"":"title='" + title + "'") +  
		" id='clerk_" + index + "'>" + name + "</span></span>";
	},
	
	items_reviewer: function(index, imgPath, name, title) {
		return "<img width=30 height=30 style='cursor:pointer;border-radius: 50%;margin:5px' " +
				"onclick='SchemeDetails.changeClerk(\"reviewer_" + index + "\",true,\"" + index + "\",\"review_" + index + "\")' id='reviewer_" + index + "'" + "src='" + imgPath + "' " +
				" onmouseover='SchemeDetails.showReview(\"del_review_" + index + "\",\"block\",\"review_" + index + "\")' onmouseout='SchemeDetails.showReview(\"del_review_" + index + "\",\"none\",\"review_" + index + "\")'/>" +
				"<img onmouseover='SchemeDetails.showReview(\"del_review_" + index + "\",\"block\",\"review_" + index + "\")' onmouseout='SchemeDetails.showReview(\"del_review_" + index + "\",\"none\",\"review_" + index + "\")' " +
					"id='del_review_" + index + "' onclick='SchemeDetails.delReview(\"reviewer_" + index + "\",\"del_review_" + index + "\",\"review_" + index + "\",\"" + index + "\")' title='" + kq.label.del + "' " +
					" src='/workplan/image/remove.png' style='cursor:pointer;position:absolute;top:0px;left:32px;width:15px;height:15px;display:none;' />" + 
					"<span style='position: absolute;float: left;margin: 15px 0 0 2px;'><span id='review_" + index + "' " + (title==""?"":"title='" + title + "'") +">" + name + "</span></span>";
	},
	
	insertData: function(rowIndex, org_id, org_name) {
		if(SchemeDetails.store) {
			SchemeDetails.store.insert(rowIndex, {
				org_id:org_id,
	        	org_name:org_name, 
	        	clerk_username:'', 
	        	clerk_fullname:'', 
	        	clerk_imgPath:"/images/photo.jpg",
	        	reviewer_id:'', 
	        	reviewer_fullname:'',
	        	reviewer_imgPath:'/images/photo.jpg'
	        });
		}
		SchemeDetails.filling_agency = new HashMap();
        SchemeDetails.filling_agency.put("org_id", org_id);
        SchemeDetails.filling_agency.put("org_name", org_name);
        SchemeDetails.filling_agency.put("clerk_username", "");
        SchemeDetails.filling_agency.put("clerk_fullname", "");
        SchemeDetails.filling_agency.put("clerk_imgPath", "/images/photo.jpg");
        SchemeDetails.filling_agency.put("reviewer_id", "");
        SchemeDetails.filling_agency.put("reviewer_fullname", "");
        SchemeDetails.filling_agency.put("reviewer_imgPath", "/images/photo.jpg");
        SchemeDetails.filling_agencys.splice(rowIndex, 0, SchemeDetails.filling_agency);
	},
	
	//移动班次和考勤项目的代码
	move: function(pageX, pageY, id, flag) {
		var initX = 0;
		var initY = 0;
		var count = 0;
		var yDistance = 0;
		var map = new HashMap();
		var sortArr = new Array();
		var kqInitX = "";//是否有初始值
		var kqInitY = "";
		if(flag == "kqItem") {
			
			kqInitX = SchemeDetails.kqItem_initX;
			kqInitY = SchemeDetails.kqItem_initY;
			yDistance = 2;
			count = SchemeDetails.countItem;//总数量，为了在超出了总数量之后，坐标赋值的正确
			map = SchemeDetails.mapItem;//值未count + "," + x + "," + y（当前位置，x轴，y轴坐标）
			sortArr = SchemeDetails.item_ids_array;//排序
		}else {
			kqInitX = SchemeDetails.kqClass_initX;
			kqInitY = SchemeDetails.kqClass_initY;
			yDistance = 0;
			count = SchemeDetails.countClass;//总数量，为了在超出了总数量之后，坐标赋值的正确
			map = SchemeDetails.mapClass;//值未count + "," + x + "," + y（当前位置，x轴，y轴坐标）
			sortArr = SchemeDetails.class_ids_array;//排序
		}
		
		//获取最初始的位置，如果是第一个取第二个的位置，因为getPosition当前的永远是0,0，如果不是第一个取第一个的位置
		if(!kqInitX) {
			var flags = false;
			var needCal = "";
			var len = 0;
			for(var key in map) {
				if(map.hasOwnProperty(key)) {
					if(id == key && map[key].split(",")[0] == 0) {
						flags = true;
					}else if(map[key].split(",")[0] == 0){
						needCal = key;
						len = 0;
					}
					if(flags && map[key].split(",")[0] == 1) {
						needCal = key;
						len = 70;
					}
				}
			}
			
			if(flag == "kqItem") {
				SchemeDetails.kqItem_initX = Ext.getCmp(needCal).getPosition()[0] - len;
				SchemeDetails.kqItem_initY = Ext.getCmp(needCal).getPosition()[1];
			}else {
				SchemeDetails.kqClass_initX = Ext.getCmp(needCal).getPosition()[0] - len;
				SchemeDetails.kqClass_initY = Ext.getCmp(needCal).getPosition()[1];
			}
			initX = Ext.getCmp(needCal).getPosition()[0] - len;
			initY = Ext.getCmp(needCal).getPosition()[1];
		}else {
			initX = kqInitX;
			initY = kqInitY;
		}
		//找到对应的移动到的位置移动到X轴的位置
		var moveId = Math.floor((pageX - Number(initX))/80);
		if(moveId < 0) {
			moveId = 0;
		}
		if(moveId > 6) {//每行7个
			moveId = 6;
		}
		//算y轴移动的位置
		var moveIdY = pageY - Number(initY);
		if(moveIdY < 0) 
			moveIdY = 0;
		var lastNum = 0;
		var moveY = Math.floor(moveIdY/55);//移动了几行Y轴的位置
		//算出最后的位置
		lastNum = (moveId + 7*moveY) >= count?(count-1):(moveId + 7*moveY);
		var yHeight = 43;//y轴的高度，找到正确的y轴坐标
		var classTipY = SchemeDetails.classTipY;
		var currX = Number(map[id].split(",")[1]);//当前位置的坐标X
		var currY = Number(map[id].split(",")[2]);//当前位置的坐标Y
		var currNum = Number(map[id].split(",")[0]);//当前位置
		var mapCurrent = new HashMap();
		for(var key in map){
			if(!map.hasOwnProperty(key))
				continue;
			
			var ids = key.split("_");
			var id = ids[ids.length-1];
			var num = Number(map[key].split(",")[0]);
			var x = Number(map[key].split(",")[1]);
			var y = Number(map[key].split(",")[2]);
			if(lastNum > currNum) {//下移
				if(num <= lastNum && num > currNum) {
					if(num%7 == 0) {//往上移动一行
						mapCurrent.put(key, SchemeDetails.movePosition(flag, key, num-1, 6*80, Math.floor((num-1)/7)*yHeight + yDistance, classTipY, id));
					}else {
						mapCurrent.put(key, SchemeDetails.movePosition(flag, key, num-1, x-80, (Math.floor((num-1)/7)*yHeight) + yDistance, classTipY, id));
					}
				}else if(num == currNum){
					mapCurrent.put(key, SchemeDetails.movePosition(flag, key, lastNum, (lastNum>=7?(lastNum%7):lastNum)*80, (Math.floor(lastNum/7)*yHeight) + yDistance, classTipY, id));
					//重新给与顺序
					var num = sortArr[currNum];
					sortArr.splice(currNum, 1, sortArr[lastNum]);//交换顺序
					sortArr.splice(lastNum, 1, num);
				}else {
					mapCurrent.put(key, num + "," + x + "," + y);
				}
			}else {
				if(num >= lastNum && num < currNum) {
					if(num%7 == 6) {//往上移动一行
						mapCurrent.put(key, SchemeDetails.movePosition(flag, key, num+1, 0, (Math.floor((num+1)/7)*yHeight) + yDistance, classTipY, id));
					}else {
						mapCurrent.put(key, SchemeDetails.movePosition(flag, key, num+1, x+80, (Math.floor((num+1)/7)*yHeight) + yDistance, classTipY, id));
					}
				}else if(num == currNum){
					mapCurrent.put(key, SchemeDetails.movePosition(flag, key, lastNum, (lastNum>=7?(lastNum%7):lastNum)*80, (Math.floor(lastNum/7)*yHeight) + yDistance, classTipY, id));
					//重新给与顺序
					var num = sortArr[currNum];
					sortArr.splice(currNum, 1);//交换顺序
					sortArr.splice(lastNum, 0, num);
				}else {
					mapCurrent.put(key, num + "," + x + "," + y);
				}
			}
		}
		if(flag == "kqItem") {
			SchemeDetails.mapItem = mapCurrent;
			SchemeDetails.item_ids_array = sortArr;
		}else {
			SchemeDetails.mapClass = mapCurrent;
			SchemeDetails.class_ids_array = sortArr;
		}
	},
	
	movePosition: function(flag, key, num, x, y, classTipY, id) {
		var current = num + "," + x + "," + y;
		Ext.getCmp(key).setPosition(x, y, true);
		if(flag == "kqClass" && Ext.getCmp("kqClassTip_" + id)) {
			var tipY = (Math.floor(num/7)*52) == 0?classTipY:  -(Math.floor(num/7)*52);
			Ext.getCmp("kqClassTip_" + id).setPosition(x - ((x == 480)?60:-61), y + tipY,true);
		}
		return current;
	},
	
	//拖拽数据上报机构
	dragColumn: function(node,data,model,dropPosition,dropHandlers,d) {
		SchemeDetails.filling_agencys = new Array();
		SchemeDetails.store.commitChanges();
		var storeData = SchemeDetails.store.getData();
		//从store取值，是最正确的，否则会出现拖拽线位置和data的位置不一样
		for(var i = 0; i < storeData.length; i++) {
			SchemeDetails.filling_agencys.push(SchemeDetails.store.data.items[i].data);
		}
		SchemeDetails.resetSort();
	},
	
	//第四步数据上报机构触发显示添加删除按钮
	filling_agencyOpea: function(id, state) {
		Ext.getDom("operate_" + id).style.display = state;
	},
	//找到数组的是否存在某个元素
	in_array: function (element, arr) {
		for (var i = 0; i < arr.length; i++) {
			if (arr[i] == element) {
				return i;
			}
		}
		return -1;
	},
	getMapLength: function(map) {
		var count = 0;
		for (var key in map) {
			if (map.hasOwnProperty(key)) {
				count++;
			}
		}
		return count;
	}
})