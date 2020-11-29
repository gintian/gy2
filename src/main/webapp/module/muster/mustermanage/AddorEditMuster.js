Ext.define('SetupschemeUL.AddorEditMuster',{
	requires:['EHR.extWidget.proxy.TransactionProxy','EHR.extWidget.field.DateTimeField', 'EHR.extWidget.field.CodeTreeCombox'],
	musterType : '1',//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；默认为"1"。
	moduleID : '0',//模块号，=0：员工管理；=1：组织机构；参照t_hr_subsys中内容，如果t_hr_subsys没有则按顺序添加；默认为0。
	operateFlag :'0',//操作标识,=0 新增；=1 编辑
	tabid :'',
    sortFields:'',
	constructor:function(config) {
		addMuster=this;
		moduleID=config.moduleID;
		musterType=config.musterType;
		operateFlag = config.operateFlag;
		tabid = config.tabid;
		addMuster.ifclose=false;
		addMuster.parttimejobvalue=false;
		addMuster.monthChangeItemData = new Array();
		addMuster.musterName = '';//新建花名册名称
		addMuster.portionCord = '',
		addMuster.portionFrom='',
		addMuster.selDBId = 'userDb0,',
		addMuster.changeMonthByMonth = '',
		addMuster.selMoreFieldCord = '',
		addMuster.portionTo='',
		addMuster.from = '',
		addMuster.to = '',
		addMuster.unitname = undefined;//机构名称
		addMuster.selmusterType = '',
		addMuster.ordergridStore = '';
		addMuster.yearArray = new Array();//年份数据
		//addMuster.orderStore = "";//排序指标表格的数据
		//addMuster.orderGridField = new Array();//已选指标的数据
		addMuster.musterItem = "";//选择指标
		addMuster.musterItemId = new Array();//选择指标的id
		addMuster.orderItem = new Array();//备选排序指标
		addMuster.removeOrderItem = new Array();//暂时移出的排序指标
		//addMuster.selOrderItem = new Array();//已选择排序指标的数组
		addMuster.usrDBPrv = new Array;//权限人员库标识
		addMuster.musterUnit = "";//选择的所属机构
		addMuster.monthStore = "";//月份数据
		addMuster.year = 2019;//年份
		addMuster.month = 1;//月份
		addMuster.number = "";//次数
		addMuster.selFieldMoreRecord = new Array();//部分历史记录选择及条件的弹框回显
		addMuster.page =0,//页数
		addMuster.nameFlag = true;
		addMuster.addDataArea = new Array();
		addMuster.dataTerm = -1;//数据过滤条件
		addMuster.group = 1;//子集范围的checkgroup
		addMuster.treeData = "";//树的数据
		addMuster.treeStore = "";
		addMuster.oneRecordFlag = true;
		addMuster.moreRecordFlag = true;
		addMuster.otherSetName = "";
		addMuster.selectedNode = [];
		var map = new HashMap();
		map.put("operate",1)
		Rpc({functionId:'MM01030001',success:function(result){
			map = Ext.decode(result.responseText);
			for(var i=0;i<map.data.length;i++){
				var pre = map.data[i].pre;
				var dbname = map.data[i].DBName;
				addMuster.usrDBPrv.push(pre+"#!#"+dbname);
			}
			addMuster.init();
		}},map);
		var mp = new HashMap();
		mp.put("operate",2);
		mp.put("musterType",musterType);
		Rpc({functionId:'MM01030001',async:false,success:function(result){
			var result = Ext.decode(result.responseText);
			addMuster.treeData = result.data;
		}},mp);
	},
	init:function(){
		var curDate = new Date();
		var time=Ext.Date.format(curDate, 'Y-m-d'); 
		addMuster.year = time.split('-')[0];
		addMuster.month = parseInt(time.split('-')[1]);
		var monthData = new Array();
		for(var i=1;i<=12;i++){
			var month = {'month':i};
			monthData.push(month);
		}
		addMuster.monthStore = Ext.create('Ext.data.Store',{
			fields:['month']
		});
		addMuster.monthStore.loadData(monthData);
		var yearArray = new Array();
		var date=new Date;
		var curyear=date.getFullYear();
		for(var i=curyear-5;i<=curyear;i++){
			var year = {'year':i};
			yearArray.push(year);
		}

		addMuster.yearStore =  Ext.create("Ext.data.Store", {
			fields: ['year']
		});
		addMuster.yearStore.loadData(yearArray);
		addMuster.orderStore=Ext.create('Ext.data.Store', {
			fields:['name','id']
		});
		addMuster.musterItem =Ext.create('Ext.data.Store', {
			fields:['name']
		});
	
		if(operateFlag == 0){ //新增花名册
			sortFields =  '';
			//初始化参数
			addMuster.musterName = '';
			usrDB = ' ';
			addMuster.musterItemId = new Array();
			addMuster.selOrderItem = new Array();
			orderItem = new Array();
			var map = new HashMap();
			map.put("operate",2);
			map.put("musterType",musterType);
			//排序指标grid的store
			addMuster.createAddWindow();
		}else if(operateFlag == 1){	//编辑花名册
			var map = new HashMap();
			map.put('tabid',tabid);
			map.put('operate',2);//编辑
			map.put("musterType",musterType);
			Rpc({functionId:'MM01030002',success: function(form,action){
				var result = Ext.decode(form.responseText);
				var itemData = result.item;
				addMuster.musterItem.loadData(itemData);
				for(var i=0;i<itemData.length;i++){
					var showName = itemData[i].name;
					if(showName.indexOf(":")>-1){
						var treeId = showName.split(':')[0];
						addMuster.musterItemId.push(treeId);
					}
				}
				var name = "";
				var musType = "";
				var filter = "";
				var condition='';
				var range_type = "";
				var DBname = "";
				addMuster.selDBId = "";
				var from = "";
				var to = "";
				var musOrg = "";
				var musOrgName = "";
				var moreFieldCord = "";
				addMuster.parttimejobvalue = result.otherData[0].parttimejobvalue;
				var fieldItemCord = new Array();
				for(var i=0;i<result.otherData.length;i++){
					name = result.otherData[i].name;
					musType = result.otherData[i].musType;
					sortFields = result.otherData[i].SortField;
					filter = result.otherData[i].filter;
					condition = result.otherData[i].condition;
					range_type = result.otherData[i].range_type;
					DBname = result.otherData[i].DBname;
					from = result.otherData[i].from;
					to = result.otherData[i].to;
					musOrg = result.otherData[i].musOrg;
					musOrgName = result.otherData[i].musOrgName;
					fieldItemCord = result.otherData[i].moreFieldCord;
				}
				if(musOrg!=null&&musOrg!=""){
					addMuster.musterUnit = musOrg;
					addMuster.unitname = musOrgName;
				}
				for(var i = 0;i<addMuster.usrDBPrv.length;i++){
					var DBid = "userDb"+i;
					var seleDB =addMuster.usrDBPrv[i].split("#!#")[0]+",";
					if(DBname.indexOf(seleDB)>-1){
						addMuster.selDBId = addMuster.selDBId + DBid+",";
					}

				}
				addMuster.dataTerm = filter;
				if(range_type =="0"){
					addMuster.group = 1;
				}else if(range_type =="1"){
					addMuster.group = 2;
					if(condition.indexOf(",")>-1){
						addMuster.changeMonthByMonth = condition.split(",")[0];
						var time = condition.split(",")[1];
						addMuster.year = time.split("-")[0];
						if(time.split("-")[1].indexOf('0')==0){
							addMuster.month=time.split("-")[1].substr(1, 1);
						}else{
							addMuster.month=time.split("-")[1];
						}
						var conditionArray = condition.split(",")[1].split("|");
						if(conditionArray.length==3){
							addMuster.number = conditionArray[2];
						}
					}
				}else if(range_type =="2"|range_type=="3"){
					addMuster.group = 3;
					if(range_type=="3"){
						addMuster.portionCord =condition;
						addMuster.from = from;
						addMuster.to = to;
					}else{
						addMuster.portionCord = -1;
						addMuster.selMoreFieldCord = condition;
						for(var i = 0;i<fieldItemCord.length;i++){
							var itemname = fieldItemCord[i].name;
							var id = fieldItemCord[i].itemid;
							var item = {'dataName':itemname,'dataValue':id};
							addMuster.selFieldMoreRecord.push(item);
						}
						
					}

				}
				//addMuster.loadOrderData();
				//addMuster.ordergridStore.loadData(orderData);
				addMuster.selmusterType = String(musType);
				addMuster.musterName =  name;
				addMuster.createAddWindow();
			}},map);
		}
	},
	createAddWindow:function(){
		//已选指标的grid数据store
		var stepviews = Ext.widget("stepview",{
			listeners:{
				stepchange:function(stepview,step){
				}	
			},
			renderTo:document.body,
			stepData:[{name:musterAdd.oneStepDesc},{name:musterAdd.twoStepDesc},{name:musterAdd.threeStepDesc},{name:musterAdd.fourStepDesc}]
		});
		var addPanel = Ext.create('Ext.form.Panel',{
			height:330,
			width:670,
			id:'addPanel',
			border:false,
			margin:'0,0,0,0',
			layout: {
				type: 'vbox',
				align: 'center'
			},
			buttons:['->',/*{
					xtype:'button',
					text:muster_cancel,
					id:'cencelAdd',
					height:22,
					handler:function(){//取消按钮
							addWindow.close();
							addMuster.page = 0;
					}
				},*/{ xtype:'button',
					height:22,
					text:musterAdd.button.prevStep,//上一步
					id:'backAdd',
					handler:function(){
						stepviews.previousStep();
						addMuster.page -=1;
						addMuster.ttbarCreate();//重新渲染bbar
						addMuster.subground();//重新渲染panel
						addMuster.changePageBut();
					}
				},{ xtype:'button',
					height:22,
					text:musterAdd.button.nextStep,//下一步
					formBind : true,
					id:'nextAdd',
					handler:function(thisBtn){
						if(addMuster.page==0){
							addMuster.musterName = Ext.getCmp('addNameText').getValue();
							addMuster.musterUnit = Ext.getCmp('selUnit').getValue().split('`')[0];
							addMuster.unitname = Ext.getCmp('selUnit').getValue().split('`')[1];
							addMuster.musterName = addMuster.musterName.replace(/^\s*|\s*$/g,"");
							if(addMuster.selmusterType == ''|addMuster.selmusterType == null){
								Ext.Msg.alert(hint_information,musterAdd.selMusType)
								return;
							}
							if(addMuster.musterName.length > 20){
								Ext.Msg.alert(hint_information,musterAdd.musterNameMaxLength);//花名册名称请在20个字符以内
								return ;
							}
							if(addMuster.page==0&&addMuster.musterName == ''|addMuster.musterName == null|addMuster.musterName.length == 0){
								Ext.Msg.alert(hint_information,musterAdd.musNameNotNull)
								return ;
							}
							if(addMuster.musterNameCnki()){
								Ext.Msg.alert(hint_information,musterAdd.nameRepeat);
								return ;
							}
							if(addMuster.musterUnit==""|addMuster.musterUnit==null|addMuster.musterUnit==undefined){
								Ext.Msg.alert(hint_information,musterAdd.selOrg);
								return ;
							}
						}else if(addMuster.page == 1){
							if(addMuster.musterItem.getCount()==0){
								Ext.Msg.alert(hint_information,musterAdd.selInputField)
								return;
							}
						}
						stepviews.nextStep();//注意：方法执行顺序影响
						addMuster.page +=1;
						addMuster.ttbarCreate();//重新渲染bbar
						addMuster.subground();//重新渲染panel
						addMuster.changePageBut();
					}
			},{xtype:'button',
			   height:22,
			   text:musterAdd.button.addFinish,
			   formBind : true,
			   id:'finishAdd',
			   handler:function(){
					addMuster.finish();
			   }
			},{
			   xtype:'button',
			   height:22,
			   text:musterAdd.button.preview,
			   formBind : true,
			   id:'previewAdd',
			   handler:function(){
				   addMuster.musterPreview();
			   }
			},'->']
		});
		addMuster.ttbarCreate();//控制button的显示与隐藏
		addMuster.subground();//每个页面的内容都在这个里创建
		var addWindow = Ext.create('Ext.window.Window',{
			resizable:false,
			id:'addwindow',
			//resizable:false,	//变大小	
			modal: true,
			height:430,
			title:operateFlag == 0?musterAdd.title.createMuster:musterAdd.title.editeMuster,
			layout:'vbox',
			items:[{
					width:670,
					height:50,
					border:true,
					bodyStyle:'border-width: 0 0 0 0;border-style: solid;border-color: #c0c0c0;',
					items:stepviews
				},{
					border:false,
					items:addPanel,
				}]
		});
	    addWindow.on("beforeclose",addMuster.addWindowclose);
		addWindow.show();
        //拖动时不会隐藏下拉框的处理
        addWindow.dd.onBefore('mousedown',function(){
            var fieldArray = addWindow.query('pickerfield');
            Ext.each(fieldArray, function(field) {
                if(field && field.isExpanded){
                    field.collapse();
                }
            })
        });
	},
	addWindowclose:function(){
		if(addMuster.ifclose){
			return true;
		}
		var massage = "";
		if(operateFlag == 1){
			massage = musterAdd.editMsgCancel;//是否取消编辑花名册操作？
		}else if(operateFlag == 0){
			massage = musterAdd.addMsgCancel;//是否取消新建花名册操作？
		}
		Ext.Msg.confirm(hint_information,massage, function(btn) {
			if (btn == muster_yes) {
				addMuster.ifclose = true;
				Ext.getCmp('addwindow').close();
			}
		},this);
		return false;
	},
	//控制tbar显示的按钮
	ttbarCreate:function(){
		if(addMuster.page == 0){
			//Ext.getCmp('addbar').items.clear();
			//Ext.getCmp("cencelAdd").show() ;
			Ext.getCmp("nextAdd").show() ;
			Ext.getCmp("backAdd").hide() ;
			Ext.getCmp("finishAdd").hide() ;
			Ext.getCmp("previewAdd").hide() ;
		}else if(addMuster.page == 1|addMuster.page == 2){
			//Ext.getCmp("cencelAdd").show() ;
			Ext.getCmp("nextAdd").show() ;
			Ext.getCmp("backAdd").show() ;
			Ext.getCmp("finishAdd").hide() ;
			Ext.getCmp("previewAdd").hide() ;
		}else if(addMuster.page == 3){
			//Ext.getCmp("cencelAdd").show() ;
			Ext.getCmp("nextAdd").hide() ;
			Ext.getCmp("backAdd").show() ;
			Ext.getCmp("finishAdd").show() ;
			Ext.getCmp("previewAdd").show() ;
		}
	},
	//每个页面的控件都是在这个方法创建的
	subground:function(){
		if(addMuster.page==0){//如果是第一个界面  ：设置花名册分类 、名称、所属单位
			//新花名册选择花名册类型
			var selMusType = Ext.create('Ext.data.Store',{
				fields:['name','id'],
				proxy:{
					type: 'transaction',
					functionId:'MM01030001',
					extraParams:{
						operate:4,
						musterType:musterType
					},
					reader: {
						type: 'json',
						root: 'data'         	
					}
				},
				autoLoad: true
			});	 
			var addMusTypeText = Ext.create('Ext.form.field.ComboBox',{
				width:350,
				//height:25,
				autoSelect:false,
				store:selMusType,
				id:'addMusType',
				allowBlank:false,
				fieldLabel: musterAdd.musType+'&nbsp;&nbsp;',
				style:'text-align:right;',
				displayField:'name',
				valueField:'id',
				queryMode:'local',
				editable : false,
				listeners:{
					'select':function() {
						addMuster.selmusterType = this.getValue();
					}
				}
			});
			var addMusType = Ext.create('Ext.panel.Panel',{
				//width:400,
				border:false,
				margin:'0,0,20,0',
				layout:'hbox',
				items:[addMusTypeText,{html:'<font color=red>&nbsp*</font>',border:false}]
			});
			
			//新花名册的名称框
			var addNametext =  Ext.create('Ext.form.field.Text',{
				width:350,
				//height:22,
				allowBlank:false,
				value:addMuster.musterName,
				id:'addNameText',
				fieldLabel: musterAdd.musName+'&nbsp;&nbsp;',
				style:'text-align:right;'
			});
			var addName = Ext.create('Ext.panel.Panel',{
				border:false,
				//width:400,
				layout:'hbox',
				margin:'20,0,20,0',
				items:[addNametext,{html:'<font color=red>&nbsp*</font>',border:false}]
			});
			//所属单位
			var selUnitPanel = Ext.create('Ext.panel.Panel',{
				//height:25,
				layout: {
			        type:'hbox',
			        align:'center'
			    }, 
				border:false,
				items:[{
					xtype: 'label',
					margin:'10 10 0 48',
					html: musterAdd.html.belongToOrg
				},{
					xtype:'codecomboxfield',
		            codesetid:'UM',
		            allowBlank:false,
		            editable : false,
		            onlySelectCodeset:false,
		            width:245,
		            value:addMuster.unitname != undefined?addMuster.musterUnit+'`'+addMuster.unitname:"",
		            id:'selUnit',
		            ctrltype:musterType=='1'?'1':'3',//1是人员范围 3是业务范围
		            //ctrltype:'3',//1是人员范围 3是业务范围
		            nmodule:'4'
				},{
					html:'<font color=red>&nbsp*</font>',
					border:false
				}]
				
		   });
			var firstPanel = Ext.create('Ext.panel.Panel',{
				border:false,
				width:'100%',
				layout: {
					align: 'center',
					type: 'vbox'
				},
				margin:'60,0,0,0',
				items:[
					addMusType,
					addName,
					selUnitPanel
				],
			});
			if(addMuster.selmusterType != ''){
				Ext.getCmp('addMusType').select(addMuster.selmusterType);
			}
			Ext.getCmp('addPanel').removeAll();
			Ext.getCmp('addPanel').add(firstPanel);
		}else if(addMuster.page == 1){//第二个界面：选择指标的界面 
			//备选指标tree的Store
			addMuster.treeStore = Ext.create('Ext.data.TreeStore',{
				root: {
					// 根节点的文本
					id:'root',	
					expanded: true,
					children:addMuster.treeData
				}
			});
			//新花名册备选指标树结构
			var addSelFieldTree = Ext.create('Ext.panel.Panel',{
				height:300,
				layout:'vbox',
				margin:'5,0,0,0',
				html:musterAdd.html.optionField,  //备选指标
				border:false,
				scrollable:true,
				items:[{
					xtype:'textfield',
					width:260,
					height:22,
					enableKeyEvents:true,
					emptyText:musterAdd.serchFieldPoint,//默认是null   请输入子集或指标名称，回车进行查询
					listeners:{
						specialkey:function(field,e){
							if(e.keyCode==13){
								addMuster.ItemQuery(field.lastValue);
							}
						},
						scope:this
					}
				},{
					xtype:'treepanel',
					rootVisible:false,
					id:'addMusTreepanel',
					height:249,
					width:260,
					store:addMuster.treeStore,
					listeners:{
						 checkchange: function(node, checked, eOpts){
				             travelChildrenChecked(node, checked, eOpts);
							 travelParentChecked(node, checked, eOpts);
							 if(node.data.checked){
								 if(node.data.leaf){
									 addMuster.selectedNode.push(node);
								 }else{
									 var existArry = [];
									 for (var i=0;i<addMuster.selectedNode.length;i++){
										 for (var j=0;j<node.childNodes.length;j++){
											 if(addMuster.selectedNode[i].id == node.childNodes[j].id){
												 existArry.push(addMuster.selectedNode[i]);
											 }
										 }
									 }
									 for (var k=0;k<existArry.length;k++){
										 addMuster.selectedNode.pop(existArry[k]);
									 }
									 addMuster.selectedNode.push(node);
								 }
							 }else{
								 var existArry = [];
								 for (var i=0;i<addMuster.selectedNode.length;i++){
									 if(addMuster.selectedNode[i].id == node.id){
										 existArry.push(addMuster.selectedNode[i]);
									 }
								 }
								 for (var k=0;k<existArry.length;k++){
									 addMuster.selectedNode.pop(existArry[k]);
								 }
								 //addMuster.selectedNode.push(node);
							 }
						 },
						/*rowdblclick:function(){
							var sel = Ext.getCmp('addMusTreepanel').getSelectionModel().selected.items;
							if(sel.length!=0&&sel[0].data.leaf){
								addMuster.treePanelSelectAction();
							}
						}, */
						//双击行触发事件，实现指标的添加
						rowdblclick:function(me,record,element,rowIndex){
							addMuster.addField(record,true);
						},
						//鼠标放置子集上显示超链接
						itemmouseenter:function(e,record){
							if(record.get('leaf')==false && record.get('text').indexOf('全部添加')=='-1'){
								record.set('text', record.get('text')+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='addAllChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>全部添加</button>");
							}
							var aBtn = document.getElementById('addAllChilNodes');
							if(aBtn!=null){
								aBtn.onclick = function(){
									addMuster.addAll(record,true);
								}
							}
						},
						//鼠标移出子集上隐藏超链接
						itemmouseleave:function(e,record){
							if(record.get('leaf')==false && record.get('text').indexOf('全部添加')!='-1'){
								record.set('text', record.get('text').replace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='addAllChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>全部添加</button>",""));
							}
						}
					}
				}]
			});
			/** 递归遍历父节点 **/  
			var travelParentChecked = function(node, checkStatus, opts){  
			    //父节点  
			    var upNode = node.parentNode;  
			    if(upNode != null){  
			        var opts = {};  
			        opts["isPassive"] = true;  
			        //父节点当前选中状态  
			        var upChecked = upNode.data.checked;  
			        //选中状态，遍历父节点，判断有父节点下的子节点是否都全选  
			        if(checkStatus){  
			            var allChecked = true;  
			            //此时父节点不可能是选中状态  
			            //如果有一个节点未选中，可以判断，当前父节点肯定是未选中状态，所以此时不必向上遍历  
			            upNode.eachChild(function (child) {  
			                if(!child.data.checked){  
			                    allChecked = false;  
			                    return false;  
			                }  
			            });  
			            upNode.set('checked', allChecked);  
			            if(allChecked){  
			                travelParentChecked(upNode, allChecked, opts);  
			            }else{//如果后台传递数据时，选择状态正确的话，此处不需要执行  
			                //travelParentChecked(upNode, allChecked, opts);  
			            }  
			        }else{//未选中，让父节点全都 不选  
			            if(upNode.data.checked){  
			                upNode.set('checked', checkStatus);  
			                travelParentChecked(upNode, checkStatus, opts);  
			            }else{  
			                //travelParentChecked(upNode, allChecked, opts);  
			            }  
			        }  
			    }  
			};
			/** 递归遍历子节点，复选框 **/  
			var travelChildrenChecked = function(node, checkStatus, opts){
			    var isLeaf = node.data.leaf;
			    if(!isLeaf){ 
			    	node.eachChild(function (child) {  
	                    child.set('checked', checkStatus);
	                    //travelChildrenChecked(child, checkStatus, eOpts); //递归遍历，此处只有二级不需要递归
	                }); 
			    }  
			    node.set('checked', checkStatus);  
			}; 
			//添加指标按钮
			var addMusBut = Ext.create('Ext.panel.Panel',{
				height:22,
				width:32,
				id:'addMusButtonOfAdd',
				margin:'0,0,20,0',
				border:false,
				html:'<img src="/images/muster/right.png" onclick = addMuster.addFielditemOrFieldset()>'		
			});
			//删除指标按钮
			var delMusBut = Ext.create('Ext.panel.Panel',{
				height:22,
				width:32,
				margin:'20,0,0,0',
				border:false,
				id:'deleteMusButOfAdd',
				html:'<img src="/images/muster/left.png" onclick = addMuster.deleteGridPanel()>',
			});
			//添加或删除按钮的panel
			var addOrdelBut = Ext.create('Ext.panel.Panel',{
				border:false,
				height:100,
//				width:32,
				layout: {
					align: 'center',
					type: 'vbox'
				},
				style:'margin-top:115px',
				items:[addMusBut,delMusBut]
			});
			//上移按钮
			var orderUpMusBut =  Ext.create('Ext.panel.Panel',{
				height:22,
				width:32,
				border:false,
				margin:'0,0,20,0',
				id:'upMusButOfAdd',
				html:'<img src="/images/muster/up.png" onclick = addMuster.upBut()>',	
			});
			//下移按钮
			var orderDownMusBut = Ext.create('Ext.panel.Panel',{
				height:22,
				width:32,
				margin:'20,0,0,0',
				border:false,
				html:'<img src="/images/muster/down.png" onclick = addMuster.downBut()>',
				id:'downMusButOfAdd'
			});
			//上移或下移按钮的panel
			var upOrdownBut = Ext.create('Ext.panel.Panel',{
				border:false,
				height:100,
				width:60,
				layout: {
					align: 'center',
					type: 'vbox'
				},
				style:'margin-top:115px',
				items:[orderUpMusBut,orderDownMusBut]
			});
			//已选指标gridpanel
			var selFieldGrid = Ext.create('Ext.panel.Panel',{
				id:'addMusSelFieldGridOfAdd',
				border:false,
				margin:'0,10,0,10',
				width:255,
				layout: {
					align: 'left',
					type: 'vbox'
				},
				items:[{
					border:false,
					height:22,
					html:musterAdd.html.selectedMusField ,
				},{
						xtype:'grid',
						hideHeaders:true,
						store:addMuster.musterItem,
						id:'selectfieldgrid',
						border:true,
						width:255,
						height:260,
						selModel:{
							mode:'MULTI'
						},
						viewConfig: {
							plugins: {
								ptype: "gridviewdragdrop",
								dragText: musterAdd.dropSort
							}
						},
						columns:[{
							dataIndex:'name',
							width:'100%',
							renderer:function(v) {
								if(v == null)
									return;
								if(v.indexOf(":")!=-1){
									return v.split(":")[1];
								}else{
									return v;
								}
							}
						}],
						listeners:{
							'itemdblclick':function(){
								addMuster.deleteGridPanel();
							}
						}
					}]
				});
			//已选指标的panel
			var selectFieldPanel = Ext.create('Ext.panel.Panel',{
				border:false,
				height:330,
				width:670,
				style:'margin-left:5px;',
				layout: {
					align: 'left',
					type: 'hbox'
				},
				items:[
					addSelFieldTree,
					addOrdelBut,
					selFieldGrid,
					upOrdownBut
				]
			});
			Ext.getCmp('addPanel').removeAll();
			Ext.getCmp('addPanel').add(selectFieldPanel);
		}else if(addMuster.page==2){//第三个界面：设置排序指标的界面
			addMuster.changePageBut();
			var sortFieldArray = sortFields.split(",");
			var orderComboxStore = Ext.create('Ext.data.Store', {
	             fields:['id','name'],
	             data:[{'id':'0','name':'正序'},{'id':'1','name':'倒序'}]
	         });
			//排序指标的combobox
			var orderCombox = new Ext.form.ComboBox({
					store : orderComboxStore,
					displayField : 'name',//显示的值
					valueField : 'id',//隐藏的值
					mode : 'local',
					editable : true,
					triggerAction : 'all',//默认显示全部
					forceSelection: true,
					typeAhead:true,//模糊匹配
					editable : false,
					listeners : {
						select : function(editor, e, eOpts) {
							var records=fieldOrderGridPanel.getSelectionModel().getSelection();
							var record = records[0];
							record.set("order",e.get("id"));
						}
					}
				});
			var fielditemComboxStore = Ext.create('Ext.data.Store', {
	             fields:['id','name'],
	             data:addMuster.orderItem
	         });
			//排序指标的combobox
			var fielditemCombox = new Ext.form.ComboBox({
					store : fielditemComboxStore,
					displayField : 'name',//显示的值
					valueField : 'id',//隐藏的值
					mode : 'local',
					editable : true,
					triggerAction : 'all',//默认显示全部
					forceSelection: true,
					typeAhead:true,//模糊匹配
					listeners : {
						select : function(editor, e, eOpts) {
							var records=fieldOrderGridPanel.getSelectionModel().getSelection();
							var record = records[0];
							record.set("name",e.get("id"));
						},
						beforeselect  : function( combo, record, index, eOpts ) {
							var itemid = record.get("id");
							var j=0;
							for(var i=0;i<fieldOrderStore.getCount();i++){
			    				var orderFieldName = fieldOrderStore.getAt(i).get('name');
			    				if(itemid==orderFieldName){
			    					j++;
			    				}
			    		    }
							if(j>=1){
								Ext.showAlert("您已选择了该排序指标!")
								return false;
							}
						},
					}
				});
			var dataArray = new Array();//grid panel的store
			//先把数据库保存排序指标的放进去
			if(sortFieldArray.length>0){
				for(var j=0;j<sortFieldArray.length;j++){
					var sortField = sortFieldArray[j];
					for(var k=addMuster.orderItem.length-1;k>-1;k--){
						var itemid = addMuster.orderItem[k].id;
						if(sortField.indexOf(itemid)!=-1){
							var  index = sortField.length-1;
							dataArray.push({number:dataArray.length+1,name:itemid,order:sortField.charAt(index)});
							break;
						}
					}
				}
			}
			if(addMuster.orderItem.length>=5&&dataArray.length<5){
				for(var i=dataArray.length;i<5;i++){
					dataArray.push({number:i+1,name:'',order:'0'})
				}
			}else if(addMuster.orderItem.length>0&&addMuster.orderItem.length<5&&dataArray.length<addMuster.orderItem.length){
				for(var i=dataArray.length;i<addMuster.orderItem.length;i++){
					dataArray.push({number:i+1,name:'',order:'0'})
				}
			}
			var fieldOrderStore = Ext.create('Ext.data.Store', {
	             fields:['number','name','order'],
	             data:dataArray
	         });
			//指标排序的gridpanel
		    var fieldOrderGridPanel = Ext.create('Ext.grid.Panel', {
		    	id:"fieldOrderGridPanel",
		    	style:'margin-top:30px;',
				store:fieldOrderStore,
				width:600,
			 	autoHeight:true, 
			 	border:true,
			 	scrollable:"y",
			 	bufferedRenderer:false,
			 	multiSelect:true,
			 	forceFit:true,
			 	enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
			 	enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
			 	sortableColumns:false,
			 	columnLines:true,//显示grid.Panel数据列之间的竖线
			 	hideHeaders:false,
				plugins:[  
	             	Ext.create('Ext.grid.plugin.CellEditing',{  
			                     clicksToEdit:1 //设置单击单元格编辑  
			        })  
			    ],
			    viewConfig: {　　 
					markDirty: false //不显示编辑后的三角
				},
				columns: [{
					    	text: musterAdd.number,//序号
					    	dataIndex: 'number',
					    	width:30,
					    	align:'center'
				    	},{ 
				    	    text: musterAdd.header.fieldName, 
				    	    dataIndex: 'name',
				    	    editor : fielditemCombox,//指标名称
							renderer : function(value, cellmeta, record) {
								var index = fielditemComboxStore.find(fielditemCombox.valueField, value);
								var ehrRecord = fielditemComboxStore.getAt(index);
								var returnvalue = "";
								if (ehrRecord) {
									returnvalue = ehrRecord.get('name');
								}
								return returnvalue;
						   }  
				    	},{ 
				    		text: musterAdd.header.order, //排序
						    dataIndex: 'order',
						    width:50,
					    	editor : orderCombox,
							renderer : function(value, cellmeta, record) {
								var index = orderComboxStore.find(orderCombox.valueField, value);
								var ehrRecord = orderComboxStore.getAt(index);
								var returnvalue = "";
								if (ehrRecord) {
									returnvalue = ehrRecord.get('name');
								}
								return returnvalue;
						   }  
				  }],
				renderTo:Ext.getBody()
			});
			Ext.getCmp('addPanel').removeAll();
			Ext.getCmp('addPanel').add(fieldOrderGridPanel);
		}else if(addMuster.page==3){//第四个界面：设置自动取数范围的界面
			//保存第三个页面的数据
			 var fieldOrderStore = Ext.getCmp('fieldOrderGridPanel').getStore();
			 sortFields = '';
	    	 for(var i=0;i<fieldOrderStore.getCount();i++){
	    		 var orderField = fieldOrderStore.getAt(i).get('name');
	    		 var order = fieldOrderStore.getAt(i).get('order');
	    		 if(orderField!=undefined&&orderField!=""){
	    			 sortFields=sortFields+orderField+order+","
	    		 }
	    	 }
			//人员库panel
			var selUsrDB = Ext.create('Ext.Container',{
				width:395,
				height:'100%',
				id:"addUsrDBCheckPanelOfAdd",
				border:false,
				style:'margin-left:10px;overflow:auto;',
				layout: 'column'
			});
			if(addMuster.usrDBPrv.length>1){
				for(var i = 0;i<addMuster.usrDBPrv.length;i++){
					var userDBPre="";
					var userDBName="";
					if(addMuster.usrDBPrv[i].indexOf("#!#")>-1){
						userDBName = addMuster.usrDBPrv[i].split("#!#")[1];
						userDBPre = addMuster.usrDBPrv[i].split("#!#")[0];
					}
					var userDBId= "userDb"+i;
					var userDB = Ext.create('Ext.form.field.Checkbox',{
						boxLabel : userDBName,
						id:userDBId,
						height: 22,
						margin: '0 10 0 0',
						columnWidth: 0.33,
						listeners:{
							'change':function(combo,record) {
								var id = this.getId();
								if(this.getValue()){
									if(addMuster.selDBId.indexOf(this.getId())==-1){
										addMuster.selDBId = addMuster.selDBId + this.getId()+","; 
									}
								}else{
									var delDbid = "";
									if(addMuster.selDBId.indexOf(",")>-1){
										var selId = addMuster.selDBId.split(",");
										for(var i=0;i<selId.length;i++){
											var checkId = selId[i];
											if(id != checkId){
												delDbid = delDbid+checkId+",";
											}
										}
									}
									addMuster.selDBId =delDbid;
								}
							}
						}
					});
					selUsrDB.add(userDB);
				}
			}
			var userDBtext =Ext.create('Ext.Component',{
				width:96,
				style:'margin-left:16px;',
				html:musterAdd.html.selUsrDB//选择人员库
			});
			var UserDBPanel = Ext.create('Ext.Container',{
				width:520,
				style:'margin-top:10px;',
				id:'addUserDBPanelOfAdd',
				layout:'hbox',
				items:[
					userDBtext,
					selUsrDB
				]
			});
			//过滤条件store
			var DataFilterStore = Ext.create('Ext.data.Store',{
				fields:['name','id'],
				proxy:{
					type: 'transaction',
					functionId:'MM01030001',
					extraParams:{
						operate:3
					},
					reader: {
						type: 'json',
						root: 'data'         	
					}
				},
				autoLoad: true
			});	
			//数据过滤条件
			var DataFilterPanel = Ext.create('Ext.panel.Panel',{
				width:486,
				//height:30,
				id:'addDataFielterPanelOfAdd',
				border:false,
				style:'margin-top:12px;',
				layout:'hbox',
				items:[{
					border:false,
					//height:25,
					html:musterAdd.html.DataFilteringCon,//数据过滤条件
				},{
					xtype:'combo',
					id:'addMusDataFilter',
					style:'margin-left:33px',
					width:200,
					//height:25,
					editable : false,
					store:DataFilterStore, 
					displayField:'name',
					valueField:'id',
					value:addMuster.dataTerm,
					listeners:{
						'select':function(combo,record) {
							addMuster.dataTerm = this.getValue();	
						}
					}
				}]
			});
			//显示兼职人员
			var parttimejobBox = Ext.create('Ext.panel.Panel',{
				width:486,
				border:false,
				style:'margin-top:10px;',
				layout:'hbox',
				items:[{
					xtype:'checkboxfield',
					style:'margin-left:106px',
					width:200,
					name:'parttimejob',  
	                id:'parttimejob',
	                boxLabel:"显示兼职人员" ,
	                checked: addMuster.parttimejobvalue
				}]
			});
			//部分历史记录store
			var moredataAreaStore = Ext.create('Ext.data.Store',{
				fields:['name','id']
			});
			//部分历史记录
			var moredataAreaPanel =  Ext.create('Ext.panel.Panel',{
				width:140,
				height:30,
				border:false,
				id:'addmoredataAreaPanel',
				items:[{
					xtype:'combo',
					id:'moreFieldAreaSel',
					store:moredataAreaStore,
					width:140,
					queryMode:'local',
					editable : false,
					displayField:'dataName',
					valueField:'dataValue',
					listeners:{
						'select':function(){
							addMuster.portionCord = this.getValue() ;
							var id = this.getValue();
							addMuster.selMoreDataCord(id);
							addMuster.from = '';
							addMuster.to = '';
							if(id==-1){
								addMuster.selDataAreaWin();
							}
						}}
				}]
			});
			//从
			var fromText = Ext.create('Ext.form.field.Text',{
				width:95,
				id:"addMusFromText",
				listeners:{
					'change':function(){
						addMuster.from = this.getValue();
					}}
			});
			//到
			var toText = Ext.create('Ext.form.field.Text',{
				width:95,
				id:"addMusToText",
				listeners:{
					'change':function(){
						addMuster.to = this.getValue();
					}}
			});
			var selectAreaPanelCord = Ext.create('Ext.panel.Panel',{
				height:22,
				width:386,
				border:false,
				id:'addMusFromToPanelCord',
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items:[{
					xtype: 'label',
					html: musterAdd.from
				},{
					border:false,
					//margin:'0 0 0 10',
					codesetid: '0',
					xtype:'codecomboxfield',
					editable : false,
					width:95,
					id:"addMusFromCord",
					listeners:{
						'select':function(){
							addMuster.from = this.getValue();
						}}
				},{
					xtype: 'label',
					html: musterAdd.to
				},{
					border:false,
					codesetid: '0',
					xtype:'codecomboxfield',
					editable : false,
					width:95,
					id:"addMusToCord",
					listeners:{
						'select':function(){
							addMuster.to = this.getValue();
						}}
				}]
			});
			var selectAreaPanelText = Ext.create('Ext.panel.Panel',{
				width:386,
				border:false,
				height:25,
				id:'addMusFromToPanelText',
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items:[{
					xtype: 'label',
					html: musterAdd.from
				},{
					border:false,
					items:fromText,
				},{
					xtype: 'label',
					html: musterAdd.to
				},{
					border:false,
					items:toText,
				}]
			});
			var selectAreaPanelDate = Ext.create('Ext.panel.Panel',{
				height:22,
				width:386,
				border:false,
				id:'addMusFromToPanelDate',
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items:[{
					xtype: 'label',
					html: musterAdd.from
				},{
					border:false,
					xtype:'datetimefield',
					width:95,
					id:"addMusFromDate",
					format: 'Y-m-d', 
					editable : false, 
					//maxValue: new Date(),
					listeners:{
						'select':function(){
							addMuster.from = this.getValue();
						}}
				},{
					xtype: 'label',
					html: musterAdd.to
				},{
					border:false,
					xtype:'datetimefield',
					width:95,
					id:"addMusToDate",
					format: 'Y-m-d',
					editable : false, 
					//maxValue: new Date(),
					listeners:{
						'select':function(){
							addMuster.to = this.getValue();
						}}
				}]
			});
			//部分历史记录的panel
			var ParHistorRecordsPanel = Ext.create('Ext.panel.Panel',{
				width:200,
				height:22,
				id:'ParHistorRecordsPanel',
				layout: {
					type: 'hbox',
					align: 'center'
				},
				items:[{
					xtype:'combo',
					width:80,
					queryMode:'local',
					editable : false
				}]
			});
			//年月变化标识的panel
			var dataFilterPanel = Ext.create('Ext.panel.Panel',{
				width:200,
				//height:22,
				id:'oneMonthChangePanel',
				border:false,
				layout: {
					type: 'hbox',
				},
				items:[{xtype:'combo',
					width:60,
					store:addMuster.yearStore,
					displayField:'year',
					valueField:'year', 
					queryMode:'local',
					editable : false,
					value:addMuster.year,
					listeners:{'select':function(combo,record) {
						addMuster.year = this.getValue();	
					}}
				},{ 
					xtype: 'label',
					html: musterAdd.year
				},{ xtype:'combo',
					width:50,
					store:addMuster.monthStore,
					displayField:'month',
					valueField:'month', 
					queryMode:'local',
					editable : false,
					value:addMuster.month,
					listeners:{'select':function(combo,record) {
						addMuster.month = this.getValue();	
					}}
				},{ xtype: 'label',
					html: musterAdd.month
				},{
	                border: false,
	                html:muster_di//第
	            }, {
	                xtype:'numberfield',
	                hideTrigger: true,
	                keyNavEnabled: true,
	                mouseWheelEnabled: true,
	            	id:'numberofsubset',
	            	value:addMuster.number,
	            	width:30
	            }, {
	                border: false,
	                html:muster_ci//
	            }]
			});
			//子集范围
			var groupPanel = Ext.create('Ext.panel.Panel',{
				width:486,
				height:140,
				border:false,
				style:'margin-top:5px;',
				layout: {
			        type:'vbox',
			        align:'left'
			    }, 
				items:[{
						xtype:'radio',
						fieldLabel: musterAdd.html.gatherArea,//子集范围
			            boxLabel: musterAdd.currentRecord,//'当前记录',
			            name: 'size',
			            inputValue: '1',
			            id: 'nomalData',
			            listeners: {
		                    change: function () {
		                    	if(this.getValue()){
		                    		addMuster.group =1;
		                    	}
		                    }
			            }
				},{
				 	xtype: 'radio',
	                fieldLabel: '    ',
	                boxLabel: musterAdd.oneHistoricalRecord,//'某月历史记录(仅支持主集和一个年月变化子集)',
	                name: 'size',
	                inputValue: '2',
	                id: 'oneHistoryRecord',
	                disabled:true,
	                listeners: {
	                    change: function () {
	                    	if(this.getValue()){
	                    		Ext.getCmp('oneFieldRecordArea').show();
	                    		addMuster.group =2;
	                    	}else{
	                    		Ext.getCmp('oneFieldRecordArea').hide();
	                    	}
	                    }
	                }
				},{		
					xtype:'panel',
					width:210,
					height:30,
					id:'oneFieldRecordArea',
					hidden:true,
					border:false,
					style:'margin-left:110px;',
					items:[{
						items:dataFilterPanel,
						border:false
						}]
				},{
				 	xtype: 'radio',
	                fieldLabel: '    ',
	                boxLabel: musterAdd.partialHistoricalRecords,//'部分历史记录(仅支持主集和一个子集)',
	                name: 'size',
	                inputValue: '3',
	                id: 'selectHistoryRecord',
	                disabled:true,
	                listeners: {
	                	change: function () {
	                		if(this.getValue()){
	                			Ext.getCmp('manyFieldRecordArea').show();
	                			addMuster.selMoreDataCord(addMuster.portionCord);
	                			if(addMuster.portionCord!=-1){
									if(addMuster.portionCord.indexOf('.'>-1)){
										var itemType = addMuster.portionCord.split('.')[2];		
										if(itemType == 'N'){
											Ext.getCmp('addMusFromText').setValue(addMuster.from);
											Ext.getCmp('addMusToText').setValue(addMuster.to);
										}else if(itemType == 'A'){
											Ext.getCmp('addMusFromCord').setValue(addMuster.from);
											Ext.getCmp('addMusToCord').setValue(addMuster.to);
										}else if(itemType == 'D'){
											Ext.getCmp('addMusFromDate').setValue(addMuster.from);
											Ext.getCmp('addMusToDate').setValue(addMuster.to);
										}
									}
	                			}
								addMuster.group =3;
	                		}else{
	                			Ext.getCmp('manyFieldRecordArea').hide();
	                		}
	                	}
	                }
				},{
						xtype:'panel',
						width:370,
						height:50,
						id:'manyFieldRecordArea',
						hidden:true,
						border:false,
						style:'margin-left:110px;',
						layout:'hbox',
						items:[{
									border:false,
									items:moredataAreaPanel
								},{
									border:false,
									items:selectAreaPanelDate
								},{
									border:false,
									items:selectAreaPanelCord
								},{
									border:false,
									items:selectAreaPanelText
								}
							]
						}
					]
			});
			if(addMuster.page == 3&&addMuster.oneRecordFlag&&Ext.isIE){
				Ext.getCmp('oneHistoryRecord').style ="color:#999999";
			}else if(addMuster.page == 3&&Ext.isIE){
				Ext.getCmp('oneHistoryRecord').style ="";
			}
			if(addMuster.page == 3&&addMuster.moreRecordFlag&&Ext.isIE){
				Ext.getCmp('selectHistoryRecord').style ="color:#999999";
			}else if(addMuster.page == 3&&Ext.isIE){
				Ext.getCmp('selectHistoryRecord').style ="";
			}
			if(addMuster.usrDBPrv.length<4){
				UserDBPanel.height = 30;
			}else if(addMuster.usrDBPrv.length<7&&addMuster.usrDBPrv.length>=4){
				UserDBPanel.height = 50;
			}else if(addMuster.usrDBPrv.length<10&&addMuster.usrDBPrv.length>=7){
				UserDBPanel.height = 70;
			}else if(addMuster.usrDBPrv.length>=10&&addMuster.usrDBPrv.length<13){
				UserDBPanel.height = 90;
			}else if(addMuster.usrDBPrv.length>=13){
				UserDBPanel.height = 90;
				UserDBPanel.setAutoScroll(true);
				//selUsrDB.height = addMuster.usrDBPrv.length/3*25;
			}
			var dataPanel = Ext.create('Ext.panel.Panel',{
				border:false,
				width:'100%',
				layout: {
					align: 'center',
					type: 'vbox'
				},
				//margin:'60,0,0,0'
			});
			if(musterType==1){
				if(addMuster.usrDBPrv.length>1){
					dataPanel.add(UserDBPanel);
				}
				dataPanel.add(DataFilterPanel);
				dataPanel.add(parttimejobBox);
				dataPanel.add(groupPanel);
			}else{
				groupPanel.style = "margin-top:80px;";
				dataPanel.add(groupPanel);
			}
			Ext.getCmp('addPanel').removeAll();
			Ext.getCmp('addPanel').add(dataPanel);
		}
	},
	//上/下一步按钮方法（数据处理）
	changePageBut:function(){
		if(addMuster.page == 0){
			addMuster.musterName = Ext.getCmp('addNameText').getValue();
			if(!addMuster.musterName){
				Ext.showAlert(musterAdd.musNameNotNull);
				return ;
			}
			addMuster.musterUnit = Ext.getCmp('selUnit').getValue().split('`')[0];
			addMuster.unitname = Ext.getCmp('selUnit').getValue().split('`')[1];
		}else if(addMuster.page == 1){
			addMuster.initTreePanel();
		}else if(addMuster.page == 2){//第三个页面设置排序指标
			addMuster.loadOrderData();
			if (Ext.isIE){
				var name = "";
				var id = "";
				var num = 0;
				var itemsetid = "";
				var fieldsetNum=0;//除了基本信息集之外的子集
				var selSecField = "";
				var fieldSetid = "";
				var array = new Array();
				for (var i = 0; i < addMuster.musterItem.getCount(); i++) {
					var record = addMuster.musterItem.getAt(i);
					var show = record.get('name');
					var id = show.split(":")[0];
					var itemType = id.split('.')[2];
					var changFlag = false;
					if(show.indexOf(":")>-1){
						name =  show.split(":")[1];
						id = show.split(":")[0];
						if(id.indexOf('.')>-1){
							var changeflag = id.split('.')[3];
							fieldSetid = id.split('.')[0];
							if(fieldSetid == 'A01'|fieldSetid == 'K01'|fieldSetid == 'H01'|fieldSetid == 'B01'){
								dataFieldFlag = true;
							}
							if(fieldSetid!=itemsetid){
							if(changeflag == 1|changeflag == 2){
								if(itemsetid!=id.split('.')[0]){
									num+=1;
									itemsetid = id.split('.')[0];
								}
							}
							}
							if(id.split('.')[0]!='A01'&&id.split('.')[0]!='B01'&&id.split('.')[0]!='K01'&&id.split('.')[0]!='H01'){
								if(selSecField!=id.split('.')[0]){
									fieldsetNum +=1;
									selSecField = id.split('.')[0];
								}
								if(itemType!="M"){
									changFlag = true;
									array.push(id);
								}
							}
	
						}
					}
				}
				if(num == 1&&dataFieldFlag&&fieldsetNum == 1){
					addMuster.oneRecordFlag = false;
				}else{
					addMuster.oneRecordFlag = true;
				}
				if(fieldsetNum == 1&&dataFieldFlag){
					if(musterType!=4){
						addMuster.moreRecordFlag = false;
					}else{
						if(array.lenth==0){
							addMuster.moreRecordFlag = true;
						}
					}
				}else{
					addMuster.moreRecordFlag = true;
				}
		  }
		}else if(addMuster.page==3){
			//处理第四个页面的数据
			var dataFieldFlag = false;
			if(addMuster.selDBId.indexOf(",")>-1&&addMuster.usrDBPrv.length>1){
				var selId = addMuster.selDBId.split(",");
				for(var i=0;i<selId.length-1;i++){
					var checkId = selId[i];
					if(checkId!=null&&checkId!=""&&checkId.length>0){
						Ext.getCmp(checkId).setValue(true);
					}
				}
			}
			
			addMuster.addDataArea = new Array();
			addMuster.monthChangeItemData = new Array();
			var name = "";
			var id = "";
			var num = 0;
			var itemsetid = "";
			var fieldsetNum=0;//除了基本信息集之外的子集
			var selSecField = "";
			var fieldSetid = "";
			var itemType = "";
			var dataFieldFlag = false;
			for (var i = 0; i < addMuster.musterItem.getCount(); i++) {
				var record = addMuster.musterItem.getAt(i);
				var show = record.get('name');
				if(show.indexOf(":")>-1){
					name =  show.split(":")[1];
					id = show.split(":")[0];
					if(id.indexOf('.')>-1){
						itemType = id.split('.')[2];
						var changeflag = id.split('.')[3];
						fieldSetid = id.split('.')[0];
						var item = {'dataName':name,'dataValue':id.split('.')[1]};
						var  comboValue = id.split('.')[0]+'.'+id.split('.')[1]+'.'+id.split('.')[4]+'.'+id.split('.')[2];
						var comboItem = {'dataName':name,'dataValue':comboValue};
						//if(itemType!="M"){
						if(id.split('.')[0].indexOf('01')==-1){//排除主集的指标
							addMuster.monthChangeItemData.push(item);
						}
						//}
						if(fieldSetid == 'A01'|fieldSetid == 'K01'|fieldSetid == 'H01'|fieldSetid == 'B01'){
							dataFieldFlag = true;
						}
						if(fieldSetid!=itemsetid){
							if(changeflag == 1|changeflag == 2){
								//if(id.split('.')[1].indexOf("Z0")!=-1){
									if(itemsetid!=id.split('.')[0]){
										num+=1;
										itemsetid = id.split('.')[0];
										addMuster.changeMonthByMonth = itemsetid;
									}
								//}
							}
						}
						if(id.split('.')[0]!='A01'&&id.split('.')[0]!='B01'&&id.split('.')[0]!='K01'&&id.split('.')[0]!='H01'){
							//if(itemType!="M"){
								addMuster.addDataArea.push(comboItem);
							//}
							if(selSecField!=id.split('.')[0]){
								fieldsetNum +=1;
								selSecField = id.split('.')[0];
								var map = new HashMap();
								map.put("operate",6);
								map.put("FieldSet",selSecField);
								Rpc({functionId:'MM01030001',async:false,success:function(result){
									map = Ext.decode(result.responseText);
									addMuster.otherSetName = map.data;
								}},map);
							}
						}
					}
				}
			}
			if(num == 1&&dataFieldFlag&&fieldsetNum == 1){
				Ext.getCmp('oneHistoryRecord').enable();
				addMuster.oneRecordFlag = false;
			}else{
				if(addMuster.group==2){
					addMuster.group = 1;
				}
			}
			if(fieldsetNum == 1&&dataFieldFlag){
				var otherItem = {'dataName':addMuster.otherSetName+musterAdd.term,'dataValue':-1};
				var map = new HashMap();
				map.put("operate",8);
				map.put("fielditemid",addMuster.addDataArea[0].dataValue);
				Rpc({functionId:'MM01030001',async:false,success:function(result){
					addMuster.addDataArea= Ext.decode(result.responseText).areaData;
					addMuster.monthChangeItemData = Ext.decode(result.responseText).itemData;
				}},map);
				if(musterType!=4){
					addMuster.addDataArea.push(otherItem);
				}
				if(addMuster.addDataArea.length>0){
					Ext.getCmp('selectHistoryRecord').enable();
					addMuster.moreRecordFlag = false;
				}else{
					if(addMuster.group==3){
						addMuster.group = 1;
					}
				}
				Ext.getCmp('moreFieldAreaSel').getStore().loadData(addMuster.addDataArea);
			}else{
				if(addMuster.group==3){
					addMuster.group = 1;
				}
			}
			if(addMuster.group==1){
				Ext.getCmp('nomalData').setValue(true);
				Ext.getCmp('oneHistoryRecord').setValue(false);
				Ext.getCmp('selectHistoryRecord').setValue(false);
			}else if(addMuster.group==2){
				Ext.getCmp('nomalData').setValue(false);
				Ext.getCmp('oneHistoryRecord').setValue(true);
				Ext.getCmp('selectHistoryRecord').setValue(false);
			}else if(addMuster.group==3){
				Ext.getCmp('nomalData').setValue(false);
				Ext.getCmp('oneHistoryRecord').setValue(false);
				Ext.getCmp('selectHistoryRecord').setValue(true);
				Ext.getCmp('moreFieldAreaSel').setValue(addMuster.portionCord);
				addMuster.selMoreDataCord(addMuster.portionCord);
				if(addMuster.portionCord!=-1&&addMuster.portionCord.indexOf('.')>-1){
					var itemType = addMuster.portionCord.split('.')[3];	
					if(itemType == 'N'){
						Ext.getCmp('addMusFromText').setValue(addMuster.from);
						Ext.getCmp('addMusToText').setValue(addMuster.to);
					}else if(itemType == 'A'){
						var cordType = addMuster.portionCord.split('.')[2];
						if(cordType!=0){
						Ext.getCmp('addMusFromCord').setValue(addMuster.from);
						Ext.getCmp('addMusToCord').setValue(addMuster.to);
						}else{
						Ext.getCmp('addMusFromText').setValue(addMuster.from);
						Ext.getCmp('addMusToText').setValue(addMuster.to);
						}
					}else if(itemType == 'D'){
						Ext.getCmp('addMusFromDate').setValue(addMuster.from);
						Ext.getCmp('addMusToDate').setValue(addMuster.to);
					}
				}
			}

		}
	},
	//搜索框方法
	ItemQuery:function(input){
		var newData  = undefined;
		var mp = new HashMap();
		mp.put("operate",2);
		mp.put("musterType",musterType);
		Rpc({functionId:'MM01030001',async:false,success:function(result){
			mp = Ext.decode(result.responseText);
			newData = mp.data;
		}},mp);
		
		for(var i = 0;i<newData.length;i++){
				var children = newData[i].children;
				if(children!=""&&children!=undefined){
					for(var j =children.length-1;j>-1;j--){
							var itemid = children[j].id;
							for(var k = 0 ;k<addMuster.musterItem.getCount();k++){
								var showName = addMuster.musterItem.getAt(k).get('name');
								if(showName.indexOf(":")>-1){
									var selid = showName.split(":")[0];
									var regString = /[a-z]+/;
									if(regString.test(selid)){
										selid = selid.toUpperCase();
									}
									if(itemid == selid){
										children.splice(j,1);
									}
								}
							}
						}
					}
				}
		var inputText = input.replace(/\s*/g,"");//input.value.replace(/\s*/g,"");
		addMuster.serchTreeData(newData,inputText);
	},
	//遍历
	serchTreeData:function(newData,inputText){
		if(""!=inputText){
			for(var i = 0;i<newData.length;i++){
				if(newData[i].text.indexOf(inputText)==-1){
					var children = newData[i].children;
					if(children!=""&&children!=undefined){
						for(var j =children.length-1;j>-1;j--){
							if(children[j].text.indexOf(inputText)==-1){
								children.splice(j,1);
							}else{
								var itemid = children[j].id;
								for(var k = 0 ;k<addMuster.musterItem.getCount();k++){
									var showName = addMuster.musterItem.getAt(k).get('name');
									if(showName.indexOf(":")>-1){
										var selid = showName.split(":")[0];
										var regString = /[a-z]+/;
										if(regString.test(selid)){
											selid = selid.toUpperCase();
										}
										if(itemid == selid){
											children.splice(j,1);
										}
									}
								}
							}
						}
					}
				}
			}
			for(var i = newData.length-1;i>-1;i--){
				var children = newData[i].children;
				if(children==undefined){
					newData.splice(i,1);
				}
				if(children!=undefined&&children.length==0){
					newData.splice(i,1);
				}
			}
		}
		var treeStore = Ext.create('Ext.data.TreeStore', {
			root: {
				// 根节点的文本
				id:'root',				
				//expanded: true,
				children:newData
			}
		});
		Ext.getCmp('addMusTreepanel').setStore(treeStore);
	},

  
	//备选指标的添加操作
	treePanelSelectAction:function(){
		var scrollTop = Ext.getCmp('addMusTreepanel').getView().getEl().getScrollTop();
		var sel = Ext.getCmp('addMusTreepanel').getSelectionModel().selected.items;
		if(sel.length==0){
			Ext.Msg.alert(hint_information,musterAdd.pleaseSelField);
		}else if(!sel[0].data.leaf){
			Ext.Msg.alert(hint_information,musterAdd.pleaseSelField);
		} else{
			var treeId = "";
			var treeName="";
			var treeflag="";
			var parentId = "";
			var flag = "";
			for(var i=0;i<sel.length;i++){
				treeId = sel[i].data.id;
				treeName = sel[i].data.text;
				treeflag = sel[i].data.leaf;
				parentId = treeId.split('.')[0];
			}
			if(treeflag){
				var scrollTop = Ext.getCmp('addMusTreepanel').getView().getEl().getScrollTop();
				var showName = treeId+":"+treeName+":";
				var selectField = {'name':showName};
				Ext.getCmp("selectfieldgrid").getStore().add(selectField);
				addMuster.musterItemId.push(treeId);
				var children = Ext.getCmp("addMusTreepanel").getStore().getNodeById(treeId);
				var parentNode = Ext.getCmp("addMusTreepanel").getStore().getNodeById(parentId);
				parentNode.removeChild(children);
				addMuster.musterItem = Ext.getCmp("selectfieldgrid").getStore();
				Ext.getCmp('addMusTreepanel').getView().getEl().setScrollTop(scrollTop);
				var parentNode = Ext.getCmp("addMusTreepanel").getStore().getNodeById(parentId);
				if(!parentNode.hasChildNodes()){
					Ext.getCmp('addMusTreepanel').getRootNode().removeChild(parentNode);
				}
				Ext.getCmp('addMusTreepanel').getView().getEl().setScrollTop(scrollTop);
			}
		}
	},
	//已选指标的删除操作
	deleteGridPanel:function(){
		var scrollTop = Ext.getCmp('addMusTreepanel').getView().getEl().getScrollTop();
		var sel = Ext.getCmp('selectfieldgrid').getSelectionModel().selected.items;
		if(sel==null|sel==""){
			Ext.Msg.alert(hint_information,musterAdd.pleaseSelCancleField);
		}else{
			var gridId = "";
			var gridName="";
			var showName = "";
			var parentId = "";
			while(sel.length > 0){
				showName = sel[sel.length-1].data.name;
				gridId=showName.split(':')[0];
				gridName = showName.split(':')[1];
				parentId = gridId.split('.')[0];
				Ext.getCmp("selectfieldgrid").getStore().remove(sel[sel.length-1]);
				addMuster.musterItem = Ext.getCmp("selectfieldgrid").getStore();
				var parentNode = Ext.getCmp("addMusTreepanel").getStore().getNodeById(parentId);
				var store = Ext.getCmp("addMusTreepanel").getStore();
				var parentName = "";
				if(parentNode == null|parentNode==undefined){
					var num = 0;
					var num1 = -1;
					for(var i = 0;i<addMuster.treeData.length;i++){
						var flag =true;
						for(var j=0;j<store.getCount();j++){
							if(store.getAt(j).id == addMuster.treeData[i].id){
								flag = true;
								break;
							}else{
								flag = false;
							}
						}
						if(!flag){
							num1+=1;
						}
						if(parentId == addMuster.treeData[i].id){
							parentName = addMuster.treeData[i].text;
							var root = Ext.getCmp('addMusTreepanel').getRootNode();
							var modalNode = Ext.create('Ext.data.NodeInterface',{});
							newParentNode = root.createNode(modalNode);
							newParentNode.set('id',parentId)
							newParentNode.set('text',parentName);
							newParentNode.set('leaf',false);
							newParentNode.set('checked',false);
							var store = Ext.getCmp("addMusTreepanel").getStore();
							root.insertChild(i-num1,newParentNode);
							break;
						}
					}
					parentNode = Ext.getCmp("addMusTreepanel").getStore().getNodeById(parentId);
				}
				var portionCordId = "";
				if(gridId.indexOf('.')>-1){
					var property = gridId.split('.');
					portionCordId = property[0]+'.'+property[1]+'.'+property[4]+'.'+property[2];
				}
				if(portionCordId == addMuster.portionCord){
					addMuster.portionCord = "";
					addMuster.from = "";
					addMuster.to = "";
				}
				var modalNode = Ext.create('Ext.data.NodeInterface',{});
				newNode = parentNode.createNode(modalNode);
				newNode.set('id',gridId);
				newNode.set('text',gridName);
				newNode.set('leaf',true);
				newNode.set('checked',false);
				var num = 0;
				for(var i = 0 ;i<addMuster.treeData.length;i++){
					if(addMuster.treeData[i].id == parentId){
						for(var j = 0;j<addMuster.treeData[i].children.length;j++){

							for(var k = 0;k<addMuster.musterItem.getCount();k++){
								var itemdesc = addMuster.musterItem.getAt(k).get("name");
								var itemid = "";
								if(itemdesc.indexOf(":")>-1){
									itemid = itemdesc.split(":")[0];
									if(itemid == addMuster.treeData[i].children[j].id){
										num+=1;
									}
								}
							}

							if(addMuster.treeData[i].children[j].id==gridId) {
								if(j==0){
									parentNode.insertChild(j,newNode);
									break;
								}else{
									parentNode.insertChild(j-num,newNode);
									break;
								}
							}
						}
						break;
					}
				}
				var fieldSetId = gridId.split(".")[0]+"."+gridId.split(".")[1];
				var itemId = gridId.split(".")[1];
				var selItem = new Array();
				for(var i=0;i<addMuster.selFieldMoreRecord.length;i++){
					var id = addMuster.selFieldMoreRecord[i].dataValue;
					if(id != itemId){
						selItem.push(addMuster.selFieldMoreRecord[i]);
					}
				}
				addMuster.selFieldMoreRecord = selItem;
			}
		}
	},
	//加载排序指标的方法(过滤备注型指标)
	loadOrderData:function(){
		addMuster.orderItem = new Array();
		var id = "";
		var itemType = "";
		var name = "";
		var Setitemid = "";
		for (var i = 0; i < addMuster.musterItem.getCount(); i++) {
			var record = addMuster.musterItem.getAt(i);
			var show = record.get('name');
			if(show.indexOf(":")>-1){
				id = show.split(":")[0];
				if(id.indexOf(".")>-1){
					Setitemid = id.split(".")[0]+"."+id.split(".")[1];
				}
			}
			if(id.indexOf('.')>-1){
				itemType = id.split('.')[2];
			}
			if(itemType != "M"){
				var name = show.split(":")[1];
				var item = {'id':Setitemid,'name':name};
				addMuster.orderItem.push(item);
			}
		}
		//var item = {'id':-1,'name':musterAdd.none};//无
		//addMuster.orderItem.push(item);
		//addMuster.orderStore.loadData(addMuster.orderItem);
	},
	//下移按钮功能
	downBut:function(){
		var grid = Ext.getCmp("selectfieldgrid");
		var record = grid.getSelectionModel().getSelected().items;
		if(record==null|record==""){
			Ext.showAlert(export_NoFieldMsg);
		}else{
			var store = grid.getStore();
			record = record[0].data.name;
			var index = 0;
			for(var i=0;i<store.getCount();i++){
				if(record==store.getAt(i).get('name')){
					index = i;
					break;
				}
			}
			var item = {'name':record};
			if (index<store.getCount()-1) {
				store.removeAt(index);
				store.insert(index + 1, item);
				grid.getView().refresh(); // refesh the row number
				grid.getSelectionModel().selectRange(index+1,index+1);
			}
		}
	},
	//上移按钮功能
	upBut:function(){
		var grid = Ext.getCmp("selectfieldgrid");
		var record = grid.getSelectionModel().getSelected().items;
		if(record==null|record==""){
			Ext.showAlert(export_NoFieldMsg);
		}else{
			var store = grid.getStore();
			record = record[0].data.name;
			var index = 0;
			for(var i=0;i<store.getCount();i++){
				if(record==store.getAt(i).get('name')){
					index = i;
					break;
				}
			}
			var item = {'name':record};
			if (index > 0) {
				store.removeAt(index);
				store.insert(index - 1, item);
				grid.getView().refresh(); // refesh the row number
				grid.getSelectionModel().selectRange(index-1,index-1);
			}
		}
	},
	//完成按钮
	finish:function(){
			Ext.getCmp('finishAdd').disable();
			if(addMuster.group == 3&&addMuster.portionCord ==""|addMuster.portionCord==null){
				Ext.Msg.alert(hint_information,musterAdd.pleaseSelParHisField);
				Ext.getCmp('finishAdd').enable();
			}else if(addMuster.portionCord!=-1&&addMuster.group == 3&&addMuster.from == ""|addMuster.from==null|addMuster.to==""|addMuster.to==null){
				Ext.Msg.alert(hint_information,musterAdd.pleaseSelParHisArea);
				Ext.getCmp('finishAdd').enable();
			}else if(addMuster.group == 3&&addMuster.portionCord==-1&&addMuster.selMoreFieldCord==""|addMuster.selMoreFieldCord==null){
				Ext.Msg.alert(hint_information,musterAdd.pleaseSelParHisArea);
				Ext.getCmp('finishAdd').enable();
			}else{
				var seleDB = "";//人员库
				var musterItem = "";//花名册指标
				if(addMuster.usrDBPrv.length==1){
					seleDB =addMuster.usrDBPrv[0].split("#!#")[0]+",";
				}else{
					for(var i = 0;i<addMuster.usrDBPrv.length;i++){
						var DBid = "userDb"+i;
						if(Ext.getCmp(DBid).getValue()){
							seleDB =seleDB+addMuster.usrDBPrv[i].split("#!#")[0]+",";
						}
					}
				}
				if(musterType!=1){
					seleDB = String(-1);
				}
				if(seleDB.length==0|seleDB==""){
					Ext.Msg.alert(hint_information,musterAdd.pleaseSelUseDB);	
				}else{
					var addMusName = addMuster.musterName;//花名册名称
					for(var i = 0;i<addMuster.musterItem.getCount();i++){
						var showName = addMuster.musterItem.getAt(i).get('name');
						var showId = "";
						var fieldName = "";
						if(showName.indexOf(":")>-1){
							showId = showName.split(":")[0];
							fieldName = showName.split(":")[1];

						}
						if(showId.indexOf(".")>-1){
							musterItem = musterItem + showId.split(".")[0]+"."+showId.split(".")[1]+":"+fieldName+":"+ showId.split(".")[2]+"#!#";
						}
					}
					var rangeType ="";
					if(addMuster.group==1){
						rangeType = 0;
					}else if(addMuster.group==2){
						rangeType = 1;
					}else if(addMuster.group==3){
						if(addMuster.portionCord == -1){
							rangeType = 2;
						}else{
							rangeType = 3;
						}
					}
					var fieldId =  "";
					if(addMuster.portionCord!=-1){
						if(addMuster.portionCord.indexOf('.')>-1){
							fieldId= addMuster.portionCord.split('.')[0]+'.'+addMuster.portionCord.split('.')[1];
						}
					}
					var numberCondition = Ext.getCmp('numberofsubset').getValue();
					var year =  parseInt(addMuster.year);
					var month = parseInt(addMuster.month);
					var filter=addMuster.dataTerm;
					var condition = addMuster.selMoreFieldCord;
					var type = addMuster.selmusterType;
					var areaBegin = addMuster.from;
					var areaEnd = addMuster.to;
					var map = new HashMap();
					if(operateFlag == 0){
						map.put("flag","add");
					}else if(operateFlag == 1){
						map.put("flag","updata");
					}
					map.put('operate',1);//保存
					map.put("orderItem",sortFields);
					map.put("musterItem",musterItem);
					map.put("usrDB",seleDB);
					map.put("musterName",addMusName);
					map.put("rangetype",rangeType);
					map.put("filter",String(filter));
					map.put("condition",condition);
					map.put("year",year);
					map.put("month",month);
					if(numberCondition){
						map.put('numberCondition',numberCondition);
					}
					map.put("parttimejobvalue",Ext.getCmp("parttimejob").getValue());
					map.put("tabid",tabid);
					map.put("musterUnit",addMuster.musterUnit);
					map.put("musterType",String(type));
					map.put("modileMusterType",musterType);
					map.put("idflag",fieldId);
					map.put("form",areaBegin);
					map.put("to",areaEnd);
					map.put("fieldByMonth",addMuster.changeMonthByMonth);
					Rpc({functionId:'MM01030002',success:function(success){
						if(success){
							if(Ext.getCmp("addwindow")){
								Ext.getCmp("addwindow").un('beforeclose',addMuster.addWindowclose);
								Ext.getCmp("addwindow").close();
							}
							musterManage.loadStore();
						}else{
							Ext.getCmp('finishAdd').enable();
						}
					}},map);
				}
			}
	},
	//选择数据范围
	selDataAreaWin:function(){
		var map = new HashMap();
		map.put("info_type", "");
		map.put('buttonText', common.button.ok);
		var rightDataList = new Array();
		Ext.require('EHR.selectfield.SelectField', function () {
			var selectField = Ext.create("EHR.selectfield.SelectField", {
				imodule: "1",
				type: '1',
				flag:'1',
				leftDataList: addMuster.monthChangeItemData,
				rightDataList:addMuster.selFieldMoreRecord,
				title: dataRangeRec.text.title,//"选择指标",
				saveCallbackfunc:addMuster.saveData,
				dataMap: map
			});
			Ext.override(selectField,{
                 //删除右侧已选指标
				nextStep:function(){
					  var right_fields = new Array();
					   //var right_field_objects = new Array();
					  var rightStore = Ext.data.StoreManager.lookup('rightStoreId');
					  rightStore.each(function(item,index,count){ //遍历每一条数据
						  right_fields.push(item.get('dataValue'));
						   //right_field_objects.push(item.data);
					  });
					  if(right_fields.length == 0){
					   	return;
					  }
					   //selectField_me.selectFieldConfig.rightDataList = right_field_objects;
					   
					  var map = new HashMap();
					  map.put("info_type", selectField_me.selectFieldConfig.dataMap.info_type);
					  map.put("expr", addMuster.selMoreFieldCord);
						//map.put("expression", selectField_me.expression);//liuyz bug26539 解决重新选择指标后因子表达式未实现更新
					  map.put("right_fields",right_fields);
					  map.put("buttonText",selectField_me.buttonText);
					  if(selectField_me.queryType)
						  map.put("queryType",selectField_me.queryType);
					  else
						  map.put("queryType","0");
					  map.put("priv",selectField_me.priv);
					  map.put("filter_factor",selectField_me.filter_factor);
					  map.put("isFilterSelectedExpert",selectField_me.isFilterSelectedExpert );
						//配置参数
					  var configObj = new Object();
					  configObj.selectFieldConfig = selectField_me.selectFieldConfig;
					  configObj.imodule = selectField_me.imodule;
					  configObj.type = selectField_me.type;
					  configObj.dataMap = map;
					  configObj.saveCallbackfunc = selectField_me.saveCallbackfunc;
					  configObj.queryCallbackfunc = selectField_me.queryCallbackfunc;
					  configObj.isShowResult = selectField_me.isShowResult;
					  Ext.require('EHR.selectfield.QueryFieldSet', function(){
						  var scopePanel = Ext.create("EHR.selectfield.QueryFieldSet",configObj);
						  selectField_me.win.close();
					  });
					}
               	});
               //selectField.show();
		});
	},
//保存及其他部分历史记录数据
	saveData:function(data){
		addMuster.selMoreFieldCord = data;
		addMuster.selFieldMoreRecord = new Array();
		var map = new HashMap();
		map.put("operate",7);
		map.put("data",data);
		map.put("musterType",musterType);
		Rpc({functionId:'MM01030001',success:function(result){
			map = Ext.decode(result.responseText);
			for(var i=0;i<map.data.length;i++){
				var name = map.data[i].name;
				var id = map.data[i].itemid;
				var cord = map.data[i].cord;
				var item = {'dataName':name,'dataValue':id};
				addMuster.selFieldMoreRecord.push(item);
			}
		}},map);
	},
	selMoreDataCord:function(id){
		var itemType = "";
		if(id!=-1){
			if(id.indexOf(".")>-1){
				itemType = id.split(".")[3];
			}
			if(itemType == 'N'){
				Ext.getCmp('addMusFromToPanelText').show();
				Ext.getCmp('addMusFromToPanelDate').hide();
				Ext.getCmp('addMusFromToPanelCord').hide();
				Ext.getCmp('addMusFromText').regex = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
				Ext.getCmp('addMusToText').regex = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
				Ext.getCmp('addMusFromText').regexText = musterAdd.onlyInputNum;
				Ext.getCmp('addMusToText').regexText = musterAdd.onlyInputNum;
				Ext.getCmp('addMusFromText').setValue("");
				Ext.getCmp('addMusToText').setValue("");
			}else if(itemType == 'A'||itemType == 'M'){
				var cordSetId="";
				Ext.getCmp('addMusFromToPanelText').hide();
				Ext.getCmp('addMusFromToPanelDate').hide();
				Ext.getCmp('addMusFromToPanelCord').show();
				Ext.getCmp('addMusFromCord').setValue("");
				Ext.getCmp('addMusToCord').setValue("");
				if(id.indexOf(".")>-1){
					cordSetId = id.split(".")[2];
					if(cordSetId!=0){
					Ext.getCmp('addMusFromCord').codesetid = cordSetId;
					Ext.getCmp('addMusToCord').codesetid = cordSetId;
					var treeStore0=Ext.getCmp('addMusFromCord').treeStore;
					var treeStore1=Ext.getCmp('addMusToCord').treeStore;
					treeStore0.on('beforeload',function(){
						Ext.apply(treeStore0.proxy.extraParams,{codesetid:cordSetId})
					});
					treeStore1.on('beforeload',function(){
						Ext.apply(treeStore1.proxy.extraParams,{codesetid:cordSetId})
					});
					treeStore0.load();
					treeStore1.load();
				}else{
					Ext.getCmp('addMusFromToPanelText').show();
					Ext.getCmp('addMusFromToPanelDate').hide();
					Ext.getCmp('addMusFromToPanelCord').hide();
					Ext.getCmp('addMusFromText').setValue("");
					Ext.getCmp('addMusToText').setValue("");
				}
				}
			}else if(itemType == 'D'){
				Ext.getCmp('addMusFromToPanelText').hide();
				Ext.getCmp('addMusFromToPanelDate').show();
				Ext.getCmp('addMusFromToPanelCord').hide();
				Ext.getCmp('addMusFromDate').setValue("");
				Ext.getCmp('addMusToDate').setValue("");
			}
		}else{
			Ext.getCmp('addMusFromToPanelText').hide();
			Ext.getCmp('addMusFromToPanelDate').hide();
			Ext.getCmp('addMusFromToPanelCord').hide();
			Ext.getCmp('moreFieldAreaSel').setValue(addMuster.otherSetName+musterAdd.term);
		}
	},
	//预览
	musterPreview:function(){
			if(addMuster.group == 3&&addMuster.portionCord ==""|addMuster.portionCord==null){
				Ext.Msg.alert(hint_information,musterAdd.pleaseSelParHisField);
			}else if(addMuster.portionCord!=-1&&addMuster.group == 3&&addMuster.from == ""|addMuster.from==null|addMuster.to==""|addMuster.to==null){
				Ext.Msg.alert(hint_information,musterAdd.pleaseSelParHisArea);
			}else if(addMuster.group == 3&&addMuster.portionCord==-1&&addMuster.selMoreFieldCord==""|addMuster.selMoreFieldCord==null){
				Ext.Msg.alert(hint_information,musterAdd.pleaseSelParHisArea);
			}else{
				var seleDB = "";
				if(addMuster.usrDBPrv.length==1){
					seleDB =addMuster.usrDBPrv[0].split("#!#")[0]+",";
				}else{
					for(var i = 0;i<addMuster.usrDBPrv.length;i++){
						var DBid = "userDb"+i;
						if(Ext.getCmp(DBid).getValue()){
							if(addMuster.usrDBPrv[i].indexOf("#!#")>-1){
								seleDB =seleDB+addMuster.usrDBPrv[i].split("#!#")[0]+",";
							}
						}
					}
				}
				if(musterType!=1){
					seleDB = String(-1);
				}
				if(seleDB.length==0|seleDB==""){
					Ext.Msg.alert(hint_information,musterAdd.pleaseSelUseDB);	
				}else{
					var year = addMuster.year 
					var month = addMuster.month
					var areaBegin = addMuster.from;
					if(areaBegin.indexOf("`")>-1){
						areaBegin = areaBegin.split("`")[0];
					}
					var areaEnd = addMuster.to;
					if(areaEnd.indexOf("`")>-1){
						areaEnd = areaEnd.split("`")[0];
					}
					var rangeType ="";
					var condition = "";
					if(addMuster.group==1){
						rangeType = String(0);
						condition = "";
					}else if(addMuster.group==2){
						rangeType = String(1);
						var selMonth = String(month);
						var nextYear = year;
						var nextMonth = String(month);
						if(month<12){
							nextMonth = parseInt(month)+parseInt(1);
							if(String(nextMonth).length ==1){
								nextMonth = "0"+nextMonth;
							}
						}else if(month==12){
							nextMonth = "01";
							nextYear = parseInt(year)+parseInt(1);
						}
						if(selMonth.length==1){
							selMonth = "0"+selMonth;
						}
						condition = addMuster.changeMonthByMonth+","+year +"-"+selMonth+"-"+"01"+"|"+nextYear+"-"+nextMonth+"-"+"01";
						var numberCondition = Ext.getCmp('numberofsubset').getValue();
						if(numberCondition){
							condition+="|"+numberCondition;
						}
					}else if(addMuster.group==3){
						if(addMuster.portionCord == -1){
							rangeType = String(2);
							condition = addMuster.selMoreFieldCord;
						}else{
							rangeType = String(3);
							var fieldId = "";
							if(addMuster.portionCord.indexOf(".")>-1){
								fieldId = addMuster.portionCord.split(".")[0]+"."+addMuster.portionCord.split(".")[1];
							}
							condition = fieldId+","+areaBegin+"|"+areaEnd;
						}
					}
					var filter=String(addMuster.dataTerm);
					if(filter == -1){
						filter = "";
					}
					var showId = "";
					var fieldSetId = "";
					var fieldItemId = "";
					for(var i = 0;i<addMuster.musterItem.getCount();i++){
						var showName = addMuster.musterItem.getAt(i).get('name');
						if(showName.indexOf(":")>-1){
							showId = showName.split(":")[0];
						}
						if(showId.indexOf(".")>-1){
							if(fieldSetId.indexOf(showId.split(".")[0])==-1){
								fieldSetId = fieldSetId +showId.split(".")[0]+",";
							}
							fieldItemId = fieldItemId + showId.split(".")[0]+"."+showId.split(".")[1]+",";
						}
					}

					var map = new HashMap();
					map.put("flag","preview");//预览标识
					map.put("musterType",musterType);
					map.put("moduleID",moduleID);//模块号
					map.put("sortField",sortFields);
					if(seleDB!=-1){
						map.put("nbases",seleDB);						
					}else{
						map.put("nbases","");
					}
					map.put("range_type",rangeType);
					map.put("filterid",filter);
					map.put("parttimejobvalue",Ext.getCmp("parttimejob").getValue());
					map.put("condition",condition);
					map.put("fieldsetString",fieldSetId);
					map.put("fielditemString",fieldItemId);
					Rpc({functionId:'MM01020001',success: function(form,action){
						var result = Ext.decode(form.responseText);
						if(result.succeed){
							addMuster.creatPreviewWindow(result,form,action,fieldSetId).show();
						}else{
							Ext.showAlert(result.message);
						}
					}},map);
				}
			}

	},
	creatPreviewWindow:function(result,form,action,fieldSetId){
		var fields = result.fields;
		var datasql = result.sql;
		var ordersql = result.ordersql;
		var columns = result.columns;
		var fieldStore = Ext.create('Ext.data.Store', {
			fields:fields,
			pageSize :20,
			proxy:{
				type: 'transaction',
				functionId:'MM01020005',
				extraParams:{
					datasql:datasql,
					fields:fields.join(","),
					sortField:sortFields,
					fieldsetString:fieldSetId,
					musterType:musterType
				},	
				reader: {
					type: 'json',
					root: 'list',
					totalProperty: 'total'
				}
			},
			autoLoad:true
		});

		var fieldGridPanel = Ext.create('Ext.grid.Panel', {
			store:fieldStore,
			height: document.body.clientHeight-40,
			width:document.body.clientWidth-10,
			bodyStyle:'width:100%',  
			autoWidth:true ,
			enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
			enableColumnHide:false,//隐藏列选择
			sortableColumns:false,//隐藏列排序
			sortable: false,
			border:true,
			cellTip : true,
			enableColumnMove:false,
			enableColumnResize:false,
			viewConfig:{forceFit: false,width:824,autoScroll:true},
			columnLines:true,//显示grid.Panel数据列之间的竖线
			columns: columns,
			bbar : new Ext.PagingToolbar({
				store : fieldStore,
				pageSize :20,
				displayInfo : true,
				displayMsg : display_bottom_information,
				emptyMsg : no_record
			}),
			listeners:{		
				render : function(panel){							
					Ext.create('Ext.tip.ToolTip', {
					    target: panel.body,
					    delegate:"td > div.x-grid-cell-inner",
					    shadow:false,
					    //id:musterClass.subModuleId+'_celltip',
					    trackMouse: true,
					    maxWidth:800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
					    renderTo: Ext.getBody(),
					    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					    listeners: {
					        beforeshow: function updateTipBody(tip) {
					        	    var div = tip.triggerElement;//.childNodes[0];
					        	    if (Ext.isEmpty(div))
					        	    	return false;
						        	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight-4){
						        		//div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
						        		tip.update("<div style='white-space:nowrap;overflow:hidden;'>"+div.innerHTML+"</div>");
						        	}else
						        		return false;
					        }
					    }
		    		});
					/*鼠标滚动翻页功能 start*/
		    		var view = panel.getView();
		    		var lockview;
		    		if(view.normalView){
		    			lockview = view.lockedView;
		    			view = view.normalView;
		    			
		    		}
		    		
		    		var viewevent = {
		    			mousewheel:{
		    				element:'el',
			    			fn:function(eo){
			    				
			    				var deltaY = eo.browserEvent.deltaY;
			    				if(!deltaY){
			    					deltaY = eo.browserEvent.wheelDelta;
			    					deltaY = -deltaY;
			    				}
			    				
			    				//表格控件引用ext4在ie下，eo.delegatedTarget不存在导致报错，故此处做下处理。 add hej 20180227
			    				var target = eo.delegatedTarget?eo.delegatedTarget:eo.browserEvent.srcElement;
			    				var scrollTop = target.scrollTop;
			    				if(deltaY<0 && scrollTop==0 && this.store.currentPage>1){
                               this.store.previousPage();
                               //this.mousePrePage=true;
                               return;
                            }
                            
                            
                            var clientHeight = target.clientHeight;
                            var scrollHeight = target.scrollHeight;
                            var pageCount = Math.ceil(this.store.totalCount/this.store.pageSize);
                            if(deltaY>0 && scrollTop+clientHeight>=scrollHeight && this.store.currentPage<pageCount){
                                this.store.nextPage();
                            }
			    			},
			    			scope:view
		    			},
		    			wheel:{
		    				element:'el',
			    			fn:function(eo){
			    				var deltaY = eo.browserEvent.deltaY;
			    				if(!deltaY){
			    					deltaY = eo.browserEvent.wheelDelta;
			    					deltaY = -deltaY;
			    				}
			    				
			    				
			    				var scrollTop = eo.delegatedTarget.scrollTop;
			    				if(deltaY<0 && scrollTop==0 && this.store.currentPage>1){
                               this.store.previousPage();
                               this.mousePrePage=true;
                               return;
                            }
                            
                            
                            var clientHeight = eo.delegatedTarget.clientHeight;
                            var scrollHeight = eo.delegatedTarget.scrollHeight;
                            var pageCount = Math.ceil(this.store.totalCount/this.store.pageSize);
                            if(deltaY>0 && scrollTop+clientHeight>=scrollHeight && this.store.currentPage<pageCount){
                                this.store.nextPage();
                            }
			    			},
			    			scope:view
		    			}
		    		};
		    			
		    		view.on(viewevent);
		    		if(lockview)
		    			lockview.on(viewevent);
		    		
		    		/*鼠标滚动翻页功能 end*/  
				}
			},
			renderTo:Ext.getBody()
		});
		var previewWindow	=Ext.create('Ext.window.Window', {
			id:'dataFirstWindow',
			title: addMuster.musterName,
			height: document.body.clientHeight,
			width:document.body.clientWidth,
			resizable : false,//禁止缩放
			modal:true,
			layout: 'vbox',
			buttonAlign: 'center',
			items: [fieldGridPanel],
		});
		return previewWindow;
	},
	//初始化树的数据
	initTreePanel:function(){ 
		for(var i = 0 ;i<addMuster.musterItem.getCount();i++){
			var showName = addMuster.musterItem.getAt(i).get('name');
			if(showName.indexOf(":")>-1){
				var id = showName.split(":")[0];
				if(id.indexOf(".")>-1){
					var setId = id.split(".")[0];
					var regString = /[a-z]+/;
					if(regString.test(id)){
						id = id.toUpperCase();
					}
					var children = Ext.getCmp("addMusTreepanel").getStore().getNodeById(id); 
					var parentsNode = addMuster.treeStore.getNodeById(setId);
					if(parentsNode!=null){
						parentsNode.removeChild(children);
						if(!parentsNode.hasChildNodes()){
							Ext.getCmp('addMusTreepanel').getRootNode().removeChild(parentsNode);
						}
					}
				}
			}
		}
	},
	musterNameCnki:function(){
		var map = new HashMap();
		map.put("musterType",addMuster.selmusterType);
		map.put("musterName",addMuster.musterName);
		map.put("operate",5);
		map.put("tabid",tabid);
		Rpc({functionId:'MM01030001',async:false,success:function(form,action){
			map = Ext.decode(form.responseText);
			for(var i=0;i<map.data.length;i++){
				addMuster.nameFlag = map.data[i];
			}
		}},map)
		return addMuster.nameFlag;
	},
	//添加的功能按钮
	addFielditemOrFieldset:function(){
			var scrollTop = Ext.getCmp('addMusTreepanel').getView().getEl().getScrollTop();
			var flag = false ;
			var removeFieldset = new Array();
			var rootNode = Ext.getCmp('addMusTreepanel').getRootNode();
			for (var k=0;k<addMuster.selectedNode.length;k++){
				if (addMuster.selectedNode[k].data.leaf) {
					addMuster.addField(addMuster.selectedNode[k],false);
				}else{
					for (var m=0;m<addMuster.selectedNode[k].childNodes.length;m++){
						addMuster.addField(addMuster.selectedNode[k].childNodes[m],false);
					}
				}
			}
			for(var i=0;i<rootNode.childNodes.length;i++){
				var childNodes = rootNode.childNodes[i].childNodes;
				var removeFielditem = new Array();
				for(var j=0;j<childNodes.length;j++){
					var node = childNodes[j];
					if(node.data.checked){
						if(!flag){
							flag = true ;
						}
						//addMuster.addField(node,false);
						removeFielditem.push(j);
					}
				}
				for(var j=removeFielditem.length-1;j>-1;j--){
					childNodes[removeFielditem[j]].remove();
				}
			}
	    	for(var i=0;i<rootNode.childNodes.length;i++){
			   var parentNode = rootNode.childNodes[i];
			   if(parentNode.data.checked){
				 if(!flag){
					flag = true ;
				 }
				 //addMuster.addAll(parentNode,false);
				 removeFieldset.push(i);
			   }
			}
	    	for(var i=removeFieldset.length-1;i>-1;i--){
	    		rootNode.childNodes[removeFieldset[i]].remove();
	    	}

	    	Ext.getCmp('addMusTreepanel').getView().getEl().setScrollTop(scrollTop);
	    	if(!flag){
	    		Ext.showAlert(musterAdd.pleaseSelField);
	    	}
	    	addMuster.selectedNode = [];
	},
	//将单个指标从左侧机构树添加到右侧
	addField:function(record,flag){
		if(record.get("leaf")){
			var treeId = record.data.id;
			var treeName= record.data.text;
			var showName = treeId+":"+treeName;
			var selectField = {'name':showName};
			Ext.getCmp("selectfieldgrid").getStore().add(selectField);
			addMuster.musterItemId.push(treeId);
			addMuster.musterItem = Ext.getCmp("selectfieldgrid").getStore();
			if(flag){
				if(record.parentNode.childNodes.length==1){
					record.parentNode.remove();
				}else{
					record.remove();
				}
			}
		}
	},
	//子集的全部添加
	addAll:function(record,flag){
		for(var j=0;j<record.childNodes.length;j++){
			addMuster.addField(record.childNodes[j],false)
		}
		if(flag){
			record.remove();
		}
	}
});