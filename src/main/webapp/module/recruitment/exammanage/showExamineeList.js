/**
 * 考生管理
 * @author zx
 * 
 * */
Ext.define('ExamineeTemplateUL.showExamineeList',{
	examinee_me:'',
	tableObj:'',
	searchIds:'',//选中的查询方案id
	a0100s:'',//选中记录的人员编号
	nbases:'',//选中记录的人员库
	z0301s:'',//选中记录的申请职位id
	z6301s:'',//选中记录的准考证号
	a0101s:'',//选中记录的名字
	examJson:'',//考试科目
	temFlag:false,//判断打印准考证时是否是最后一人
	fileNames:'',//打印准考证所有文件名
	cardid:'',//准考证模板
	isNumValid:true,//批量修改数值型的需要验证
	constructor:function(config) {
		examinee_me = this;
		a0100s = "";
		a0101s = "";
		nbases = "";
		z0301s = "";
		z6301s = "";
		searchIds = "";
		examJson = "";
		temFlag = false;
		fileNames = "";
		cardid = "";
		this.init();
	},
	// 初始化函数
	init:function() {
		var score;
		var map = new HashMap();
		map.put("flag","2");
		map.put("searchStr", "00");
	    Rpc({functionId:'ZP0000002551',async:false,success:examinee_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		if(!conditions){
			Ext.showAlert(result.message);
			return;
		}
		var defaultQuery=result.defaultQuery;
		var optionalQuery=result.optionalQuery;
		var hasTheFunction=result.hasTheFunction;
		//招聘批次json
		var batchJson = result.batchList;
		//所有启用考试科目
		if(result.examJson)
			examJson = Ext.decode(result.examJson);
		var batchId = result.batchId;//针对栏目设置后重新刷新页面导致所选批次不在问题
		
		var obj = Ext.decode(conditions);
		examinee_me.tableObj = new BuildTableObj(obj);
		examinee_me.createSearchPanel();
		//生成招聘批次下拉框
		examinee_me.generateCombox(Ext.decode(batchJson),"batchComb");
		//当点击栏目设置保存后，页面加载的时候将批次置为保存前的
		if(!Ext.isEmpty(batchId))
			Ext.getCmp("batch_ids").setValue(batchId);
		//初始化上传文件组件(加判断是防止未授权导入成绩按钮时页面报错)
		if(!Ext.isEmpty(Ext.get("importScoreExcel")))
			examinee_me.createUploadFile();
	 Ext.require("EHR.commonQuery.CommonQuery",function(){
        var commonQuery = Ext.create("EHR.commonQuery.CommonQuery",{
             subModuleId:'zp_exam_assignList',
             ctrltype:'3',
             nmodule:'7',
             defaultQueryFields:defaultQuery,
              optionalQueryFields:optionalQuery,
              doQuery:function(items){
              	var map = new HashMap();
              	map.put("items", items);
              	map.put("type", "3");
              	map.put("subModuleId", "zp_exam_assignList");
              	Rpc({
              		functionId : 'ZP0000002556',
              		success :examinee_me.loadTable
              	}, map);
              },
              scope:examinee_me,
              fieldPubSetable:hasTheFunction
         });
         
         examinee_me.tableObj.insertItem(commonQuery,0);
        });
	},
	
	cnameForPay:function(value, metaData, Record){
		var salaryid = Record.data.salaryid_safe;
		var appdate = Record.data.appdate_safe;
		var count = Record.data.count_safe;
		var imodule = Record.data.imodule_safe;
		var viewtype = Record.data.viewtype_safe;
		var html = "<a href=javascript:examinee_me.openSalaryPayPage('"+salaryid+"','"+appdate+"','"+count+"','"+imodule+"','"+viewtype+"');>" + value + "</a>"; 
		return html;
	},
	// 查询方案
	createSearchPanel:function(){

		var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
			border:0,
			id:"toolbar",
			dock:'top',
			padding:2,
			items:[{
					xtype:'label',
					text: '查询方案：',
			     	style:'margin-right:10px'
				},{
					xtype:'label',
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'00\')" id="00" group="all" style="text-decoration: underline;color:green">全部</a> ',
					style:'margin-left:10px;margin-right:10px;'
				},'-',{
					xtype:'label',
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'01\')" id="01" group="hall" style="text-decoration: none;color:#1b4a98">考场未安排</a> ',
					style:'margin-left:10px;margin-right:10px;'
				},{
					xtype:'label',
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'02\')" id="02" group="hall" style="text-decoration: none;color:#1b4a98">已安排</a> ',
					style:'margin-left:10px;margin-right:10px;'
				},'-',{
					xtype:'label',
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'03\')" id="03" group="examNo" style="text-decoration: none;color:#1b4a98">准考证未生成</a> ',
					style:'margin-left:10px;margin-right:10px;'
				},{
					xtype:'label',
					hidden:true,
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'04\')" id="04" group="examNo" style="text-decoration: none;color:#1b4a98">准考证未打印</a> ',
					style:'margin-left:10px;margin-right:10px;'
				},'-',{
					xtype:'label',
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'05\')" id="05" group="score" style="text-decoration: none;color:#1b4a98">成绩未录入</a> ',
					style:'margin-left:10px;margin-right:10px;'
				},{
					xtype:'label',
					html:'<a href="javascript:examinee_me.conditionSearchList(\'2\',\'06\')" id="06" group="score" style="text-decoration: none;color:#1b4a98">成绩已录入</a> ',
					style:'margin-left:10px;margin-right:10px;'
				}]
		});
		examinee_me.tableObj.insertItem(toolbar,0);
	},
	//查询    2代表从查询方案进来，3代表从招聘批次
	conditionSearchList:function(flag,searchStr,batchId){
		var res = searchStr;
		if(flag == 2){
			//全部
			if(searchStr == "00"){
				for(var i = 1;i<=6;i++){
					Ext.getDom("0"+i).style.color = "#1b4a98";
					Ext.getDom("0"+i).style.textDecoration="none";
				}
				if(searchIds.indexOf("00") != -1){
					Ext.getDom("00").style.color = "#1b4a98";
					Ext.getDom("00").style.textDecoration="none";
				}else{
					Ext.getDom("00").style.color = "green";
					Ext.getDom("00").style.textDecoration="underline";
					searchIds = "00,"; 
				}
			}else{
				//点击其他查询时去掉全部
				if(searchIds.indexOf("00,") != -1)
					searchIds = searchIds.replace("00,","");
				Ext.getDom("00").style.color = "#1b4a98";
				Ext.getDom("00").style.textDecoration="none";
				
				var obj = Ext.getDom(searchStr);
				if(searchIds.indexOf(obj.id) < 0){
					searchIds += obj.id+",";
					obj.style.color = "green";
					obj.style.textDecoration="underline";
				}else{
					// 撤选当前项
					searchIds = searchIds.replace(searchStr+",","");
					obj.style.color = "#1b4a98";
					obj.style.textDecoration="none";
					// 全部撤选后，再次选中“全部”
					if(searchIds=='') {
						//再次点击其他查询时 为全部增加下划线，颜色调成绿色，searchIds = "00,"
						Ext.getDom("00").style.color = "green";
						Ext.getDom("00").style.textDecoration="underline";
						searchIds = "00,"; 
					}
				}
				
				//如果当前节点同组的其他元素处于选中，则去掉
				var tem = Ext.query("[group="+obj.getAttribute("group")+"]");
				Ext.each(tem,function(htm){
					if( htm.id != searchStr){
						if(searchIds.indexOf(htm.id) != -1){
							htm.style.color = "#1b4a98";
							htm.style.textDecoration="none";
							
							searchIds = searchIds.replace(htm.id+",","");
						}
					}
				});
			}
			res = searchIds.substring(0, searchIds.length-1);
		}
		
		var map = new HashMap();
		map.put("flag",flag);
		map.put("searchStr", res);
		map.put("subModuleId",examinee_me.tableObj.subModuleId)
		map.put("path", "notMenu");
		
		batchId = Ext.isEmpty(batchId) ? Ext.getCmp("batch_ids").getValue() : batchId;
		map.put("batchId", batchId);
	    Rpc({functionId:'ZP0000002551',async:false,success:examinee_me.loadTable},map);
	},
	//获取选中记录的参数（a0100,nbase,z0301）
	getSelectedParams:function(){
		var selectRecords = Ext.getCmp("zp_exam_assign_tablePanel").getSelectionModel().getSelection();
		if(selectRecords.length<=0){
			Ext.showAlert("请选择需要操作的记录");
			return false;
		}
		a0100s = "";
		nbases = "";
		z0301s = "";
		z6301s = "";
		a0101s = "";
		Ext.each(selectRecords,function(rec,index){
			if(index == selectRecords.length-1){
				a0100s += rec.data.a0100_e; 
				nbases += rec.data.nbase_e;
				z0301s += rec.data.z0301_e;
				z6301s += Ext.isEmpty(rec.data.z6301) ? "未生成" : rec.data.z6301;
				a0101s += rec.data.a0101;
			}else{
				a0100s += rec.data.a0100_e+",";
				nbases += rec.data.nbase_e+",";
				z0301s += rec.data.z0301_e+",";
				z6301s += Ext.isEmpty(rec.data.z6301) ? "未生成," : rec.data.z6301 +",";
				a0101s += rec.data.a0101 + ",";
			}
		});
		return true;
	},
	//删除考生
	deleteExaminees:function(){
		//处理参数
		var tem = examinee_me.getSelectedParams();
		if(!tem)
			return;
		
		Ext.Msg.confirm("提示信息","确定要删除考生吗？",function(res){
				if(res=="yes"){
					var map = new HashMap();
					map.put("a0100s",a0100s);
					map.put("nbases", nbases);
					map.put("z0301s", z0301s);
					Rpc({functionId:'ZP0000002552',success:examinee_me.loadTable},map);
				}
			});
	},
	//生成下拉框
	generateCombox:function(jsonStr,id){
		var store = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			data:jsonStr
		});
		var box = Ext.create('Ext.form.ComboBox', {
	  		width:150,
	  		id:'batch_ids',
	  		blankText : '--请选择--',
	  	    store: store,
	  	    autoSelect:true,
	  	   // overflowY:'scroll',
	  	    queryMode: 'local',
	  	   	displayField: 'dataName',
	  	    valueField: 'dataValue',
	  	    labelPad:0,
	  	    labelAlign:'right',
	  	    renderTo: Ext.getBody(),
	  	    listeners:{
		   		blur:{
					fn:function(combox){
						var res = store.find("dataValue",combox.getValue());
						if(res==-1){//无效输入值
							combox.setValue("");
						}
					}
				}
	  		}
	  	});
		if(store.getTotalCount() > 1)
			box.setValue(store.getAt(1).data.dataValue);
		else
			box.setValue(store.getAt(0).data.dataValue);
			
		//在此处添加监听事件而不再创建box的时候添加是为了防止初始化时候触发而访问两次后台
		box.on({
			select:{
				fn:function(combo,records){
					examinee_me.conditionSearchList('3',records.data.dataValue);
				}
			}
		});
//		var bar = examinee_me.tableObj.getTitleBar();
//		bar.setWidth(310);
//		bar.add(box);
		examinee_me.tableObj.toolBar.insert(examinee_me.tableObj.toolBar.items.length-1,box);
	},
	//指定批次下是否存在准考证号
	isExitExamNo:function(){
		var batch_id = Ext.getCmp("batch_ids").getValue();
		if(Ext.isEmpty(batch_id) || "all"==batch_id){
			Ext.showAlert("请选择招聘批次!");
			return;
		}
		var map = new HashMap();
		map.put("batch_id",batch_id);
	    Rpc({functionId:'ZP0000002553',async:false,success:examinee_me.checkSuccess},map);
	},
	checkSuccess:function(form){
		var result = Ext.decode(form.responseText);
		var batch_id = result.batch_id;
		//标志是否存在已生成的准考证
		var isExit = result.isExit;
		if(isExit){
			Ext.Msg.confirm("提示信息","系统将自动为所有考生重新分配准考证,是否继续？",function(res){
				if(res=="yes"){
					Ext.MessageBox.wait("", "正在生成准考证号");	
					var map = new HashMap();
					map.put("batch_id",batch_id);
				    Rpc({functionId:'ZP0000002554',async:true,success:function(){
				    	Ext.MessageBox.close();
				    	var store = Ext.data.StoreManager.lookup('zp_exam_assign_dataStore');
						store.reload();
				    }},map);
				}
			});
		}else{
			Ext.MessageBox.wait("", "正在生成准考证号");	
			var map = new HashMap();
			map.put("batch_id",batch_id);
		    Rpc({functionId:'ZP0000002554',async:true,success:function(){
		    	Ext.MessageBox.close();
		    	var store = Ext.data.StoreManager.lookup('zp_exam_assign_dataStore');
				store.reload();
		    }},map);
		}
	},
	//清除考场分派记录
	clearExamHallRecord:function(){
		var selectRecords = Ext.getCmp("zp_exam_assign_tablePanel").getSelectionModel().getSelection();
		var count = 0;
		var a0101s = "";
		var alertMsg = "";
		
		Ext.each(selectRecords,function(record,index){
			if(Ext.isEmpty(record.data.hall_id)){
				if(a0101s.split(",").length <5)
					a0101s += record.data.a0101+",";
				count += 1;
			}
		});
		if(count == 0)
			alertMsg = "确定要清除考场分派记录吗？";
		else if(count > 0)
			alertMsg = a0101s.substring(0,a0101s.length-1)+"等"+count+"人未分派考场,<br/>确定清除其他考生的考场分派记录吗？";
			
		//处理参数
		var tem = examinee_me.getSelectedParams();
		if(!tem)
			return;
		Ext.Msg.confirm("提示信息",alertMsg,function(res){
				if(res=="yes"){
					var map = new HashMap();
					map.put("a0100s",a0100s);
					map.put("nbases", nbases);
					map.put("z0301s", z0301s);
					Rpc({functionId:'ZP0000002555',async:false,success:examinee_me.loadTable},map);
				}
			});
	},
	//打印准考证
	printExamNoCards:function(){
		//处理参数
		var tem = examinee_me.getSelectedParams();
		if(!tem)
			return;
		
		var map = new HashMap();
		map.put("a0100s",a0100s);
		map.put("nbases", nbases);
		map.put("a0101s", a0101s);
		
		map.put("z6301s", z6301s);
		map.put("userpriv","zpselfinfo");
		map.put("fieldpurv","1");
		map.put("istype","1");        
		map.put("queryType","1");
		map.put("infokind","1");
		var In_paramters="exce=PDF";
		Rpc({functionId:'ZP0000002559',async:false,parameters:In_paramters,success:examinee_me.showZip},map);
	},
	//下载准考证生成的压缩包
	showZip:function(response){
		var result=Ext.decode(response.responseText);
		if(result.message != "未设置准考证模板!"){
			var url = result.url;
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"zip");	
		}else
			Ext.showAlert(result.message);
	},
	//时分秒(暂时先不用这种方式)
	createHourMinu:function(){
		return Ext.create('Ext.panel.Panel',{
			width:400,
			border:false,
			layout:'table',
			style:'margin-top:20px',
			items:[{
				xtype:'label',
				style:'margin-left:32px;',
				html:'考试时间'
			},{
				xtype:'numberfield',
				anchor: '100%',
				allowBlank:false,//不允许为空
				style:'margin-left:32px;',
		        id: 'bottles',
		        inputId:'tes',
		        hideTrigger:true,
		        width:40,
		        value: 9,
		        maxValue: 23,
		        minValue: 0,
		        listeners:{
					blur:{
						fn:function(num){
							if(parseInt(num.value) < 10)
								document.getElementById("tes").value = "0"+num.value;
						}
					},
					change:{
						fn:function(num,newValue,oldValue){
							if(parseInt(newValue) < 10)
								document.getElementById("tes").value = "0"+num.value;
						}
					}
				}
			},{html:'：',border:false},{
				xtype:'numberfield',
				allowBlank:false,//不允许为空
				anchor: '100%',
		        id: 'bottles1',
		        width:40,
		        value: 0,
		        maxValue: 60,
		        minValue: 0
			},{
				xtype:'numberfield',
				allowBlank:false,//不允许为空
				anchor: '100%',
		        id: 'bottles2',
		        width:60,
		        style:'margin-left:5px;',
		        fieldLabel:'至',
		        labelSeparator:null,
		        labelWidth:15,
		        value: 11,
		        maxValue: 24,
		        minValue: 0
			},{html:'：',border:false},{
				xtype:'numberfield',
				allowBlank:false,//不允许为空
				anchor: '100%',
		        id: 'bottles3',
		        width:40,
		        value: 30,
		        maxValue: 60,
		        minValue: 0
			}]
		});
	},
	//时分秒下拉框           jsonStr 需要展示的数据        id   下拉框id     style   下拉框样式
	generateSelector:function(jsonStr,id,style){
		var store = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			data:jsonStr
		});
		return Ext.create('Ext.form.ComboBox', {
			fieldLabel:'',
	  		width:42,
	  		id:id,
	  		fieldStyle:'text-align:center',
	  		style:style,
	  		allowBlank:false,
	  		blankText : '时间不能为空',
	  	    store: store,
	  	    autoSelect:true,
	  	    queryMode: 'local',
	  	   	displayField: 'dataName',
	  	    valueField: 'dataValue',
	  	    labelPad:0,
	  	    labelAlign:'right',
	  	    listeners:{
		   		blur:{
					fn:function(combox){
						var res = store.find('dataValue',combox.getValue());
						if(res==-1){//无效输入值
							combox.setValue("");
						}
					}
				},
				beforeshow:{
					fn:function(combox){
						combox.getPicker().setWidth(42);
					}
				}
	  		}
	  	});
	},
	//时分秒（下拉框）
	createHourMinu1:function(){
		var panels1 = Ext.create("ZP.GENERATETIME", {
	    	   width:42,
	    	   panelWidth:132,
	    	   hourId:'bottles',
	    	   minuteId:'bottles1',
	    	   hourStyle:'margin-left:38px;',
	    	   minuteStyle:'',
	    });
		var panel1 = panels1.getPanel();
		var panels2 = Ext.create("ZP.GENERATETIME", {
			width:42,
			panelWidth:128,
			hourId:'bottles2',
			flag:1,
			minuteId:'bottles3',
			hourStyle:'margin-left:11px;',
			minuteStyle:'',
		});
		var panel2 = panels2.getPanel();
		
		return Ext.create('Ext.panel.Panel',{
			width:400,
			border:false,
			layout:'table',
			id:'hourRange',
			style:'margin-top:20px',
			hidden:true,
			items:[
			       {
					xtype:'label',
					style:'margin-left:32px;display:inline',
					html:'考试时间'
			       },
					panel1,
					{html:'至:',style:'margin-left:8px',border:false},
					panel2
				]
		});
	},
	//批量修改
	updateExamTime:function(){
		var batch_id = Ext.getCmp("batch_ids").getValue();
		if(Ext.isEmpty(batch_id) || "all"==batch_id){
			Ext.showAlert("请选择招聘批次!");
			return;
		}
		
		var codeStore = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			proxy:{
				type:'transaction',
				functionId:'ZP0000002565',
		        reader: {
		            type: 'json',
		            root: 'data'         	
		        }
			},
			autoLoad:false
		});
		
		var store = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			data:examJson
		});
		var box = Ext.create('Ext.form.ComboBox', {
			fieldLabel:'项目',
			allowBlank:false,
	  		width:310,
	  		id:'examSub',
	  		blankText : '请选择项目',
	  	    store: store,
	  	    autoSelect:true,
	  	    queryMode: 'local',
	  	   	displayField: 'dataName',
	  	    valueField: 'dataValue',
	  	    labelWidth:80,
	  	    labelAlign:'left',
	  	    style:'margin-left:32px;margin-top:20px;',
	  	    labelSeparator:null,
	  	    listeners:{
		   		blur:{
					fn:function(combox){
						var res = store.find("dataValue",combox.getValue());
						if(res==-1){//无效输入值
							combox.setValue("");
						}
					}
				},
				'select':function(){
 	   				var codeItemId= this.getValue();
	    	   		codeStore.load({
						params:{
							codeItemId:codeItemId
						},
						callback: function(record, option, succes){
							if(record.length>1){//有代码值的
								Ext.getCmp('batchUp').setHeight(240);
								examinee_me.isNumValid=true;//每次换其他类型的时候需要去掉上一次的验证，否则验证一直存在
								Ext.getCmp('examSubjectTime1').hide();
								Ext.getCmp('hourRange').hide();
								Ext.getCmp('updateTextId').show();
								Ext.getCmp('codeItemId').show(); 
								Ext.getCmp('codeItemId').setValue('');
								Ext.getCmp('updateTextId').setValue('');//将原来可能数值型的值清空
								Ext.getCmp('updateTextId').setReadOnly(true);//只能通过选则代码写入
							}else if(record.length=1){
								examinee_me.isNumValid=true;
								if(record[0].data.dataValue=='N') {//数值
									Ext.getCmp('batchUp').setHeight(200);
									Ext.getCmp('examSubjectTime1').hide();
									Ext.getCmp('hourRange').hide();
									Ext.getCmp('updateTextId').show();
									Ext.getCmp('updateTextId').setValue('');
									Ext.getCmp('codeItemId').setValue('');
									Ext.getCmp('codeItemId').hide(); 
									Ext.getCmp('updateTextId').setReadOnly(false);
								}else if(record[0].data.dataValue=='D') {//时间
									Ext.getCmp('batchUp').setHeight(240);
									Ext.getCmp('examSubjectTime1').show();
									Ext.getCmp('hourRange').show();
									Ext.getCmp('updateTextId').hide();
									Ext.getCmp('codeItemId').hide();
									Ext.getCmp('codeItemId').setValue('');
									Ext.getCmp('updateTextId').setValue('');//将原来可能数值型的值清空
								}else {
									Ext.getCmp('batchUp').setHeight(160);
									Ext.getCmp('examSubjectTime1').hide();
									Ext.getCmp('hourRange').hide();
									Ext.getCmp('updateTextId').hide();
									Ext.getCmp('codeItemId').hide();
									Ext.getCmp('codeItemId').setValue('');
									Ext.getCmp('updateTextId').setValue('');//将原来可能数值型的值清空
								}
							}
						}
					});
 	   			}
	  		}
	  	});
		var panels = examinee_me.createHourMinu1();
		
		var win = Ext.create('Ext.window.Window',{
			title:'批量修改',
			width:400,
			height:200,
			modal:true,
			id:'batchUp',
			autoScroll:false,
			autoShow:true,
			buttons:[{text:'确定',handler:function(){
				//校验
				var tableCount = 0;//要修改的数量
				var selectRecords = Ext.getCmp("zp_exam_assign_tablePanel").getSelectionModel().getSelection();
				if(selectRecords.length<=0)
					tableCount = examinee_me.tableObj.dataStore.totalCount;
				else
					tableCount = selectRecords.length;
				if(tableCount == 0) {
					Ext.showAlert("当前没有可以修改的数据！");
					return;
				}
				if(!examinee_me.isNumValid) {
					Ext.showAlert("请输入正确的数字型格式！");
					return;
				}
				var name = Ext.getCmp("examSub").rawValue;
				if(name=="" || name==null) {
					Ext.showAlert("请选择要替换的项目！");
					return;
				}
				
				Ext.Msg.confirm(common.button.promptmessage,"确认要对选择的" + tableCount + "个人的 “" + name + "”进行修改？",function(btn){ 
					if(btn=="yes"){
						var subId = Ext.getCmp("examSub").getValue();
						var regResult = Ext.getCmp("examSub").validate() && Ext.getCmp("examSubjectTime1").validate() 
						&& Ext.getCmp("bottles").validate() && Ext.getCmp("bottles1").validate()
						&& Ext.getCmp("bottles2").validate() && Ext.getCmp("bottles3").validate();
						if(!regResult){
							Ext.Msg.alert(PROMPT_INFORMATION,ENTER_DATE_FORMAT_ERROR);
							return;
						}
						
						var bottles = examinee_me.dealTimeValue("bottles");
						var bottles1 = examinee_me.dealTimeValue("bottles1");
						var bottles2 = examinee_me.dealTimeValue("bottles2");
						var bottles3 = examinee_me.dealTimeValue("bottles3");
						//js中parseInt(str,radix)    第二个参数是进制参数，如果没有提供，则按照str的格式进行判断，当str是0x开头时，将转成16进制，当以0开头时，转成8进制
						if(parseInt(bottles2,10)<parseInt(bottles,10) || (parseInt(bottles2,10) == parseInt(bottles,10) && parseInt(bottles3,10)<parseInt(bottles1,10))){
							Ext.showAlert("开始时间不能大于结束时间");
							return false;
						}
						
						var selectRecords = Ext.getCmp("zp_exam_assign_tablePanel").getSelectionModel().getSelection();
						var map = new HashMap();
						if(selectRecords.length > 0) {
							var tem = examinee_me.getSelectedParams();
							map.put("a0100s",a0100s);
							map.put("nbases",nbases);
							map.put("z0301s",z0301s);
						}
						//考试时间 = 考试日期+考试时间
						var examTime = Ext.get(Ext.getCmp("examSubjectTime1").getInputId()).getValue() + "  "+bottles+":"+bottles1+"--"+bottles2+":"+bottles3;
						var updateTextId = Ext.getCmp("updateTextId").getValue();//替换成
						var codeItemId = Ext.getCmp("codeItemId").getValue();//代码项
						if(Ext.isEmpty(subId))
							return;
						map.put("batch_id",batch_id);
						map.put("subId",subId);
						map.put("examTime",examTime);
						map.put("updateTextId",updateTextId);
						map.put("codeItemId",codeItemId + "");
					    Rpc({functionId:'ZP0000002558',async:false,success:examinee_me.loadTable},map);
					    win.close();
					}
				})
			}}],
			buttonAlign:'center',
			items:[box,{
				xtype:'datefield',
				anchor: '100%',
				allowBlank:false,
				style:'margin-left:32px;margin-top:20px;',
				fieldLabel:'考试日期',
				width:310,
				format:'Y-m-d',
				labelSeparator:null,
				id:'examSubjectTime1',
				hidden:true,
				labelWidth:80,
				value: new Date()
			},{
				xtype:'textfield',
	        	fieldLabel:'替换成',
	        	id:'updateTextId',
	        	style:'margin-left:32px;margin-top:20px;',
	            labelAlign:'left',
	            labelSeparator:null,
	            labelWidth:80,
	            readOnlyCls:'background-color: #ffffff',
	            width:310,
	            enableKeyEvents:true,//是否监听键盘事件
	            regex:/^(-?\d+)(\.?\d+)?$/,
	            listeners : {
	                keyup : function(textField, e){//判断是否为数字，只有在数值型的时候监听，代码类型的设置readonly
	                	examinee_me.isNumValid = Ext.getCmp("updateTextId").validate();//验证是否为数值型
	                	if(examinee_me.isNumValid){
                            textValue =  Ext.getCmp('updateTextId').getValue();
                            textLength = textValue.length;
                            if(textLength>10){
                                textValue = textValue.substring(0,10)
                            }
                            Ext.getCmp('updateTextId').setValue(textValue);
                        }
	                }
	            }
			},{
				xtype:'combo',
	        	fieldLabel:'代码项',
	        	store:codeStore,
	        	id:'codeItemId',
	        	name:'codeItemName',
	        	displayField:'dataName', 
 	    	    valueField:'dataValue',
	        	labelSeparator:null,
	        	editable:false,
	            hidden:true,
	            queryMode:'local',
	        	style:'margin-left:32px;margin-top:20px;',
	            labelAlign:'left',
	            labelWidth:80,
	            width:310,
	            listeners:{
 	    	    	'select':function(){
	 	    	   		var text  =  this.getValue();
	 	    	   		Ext.getCmp('updateTextId').setValue(text);
 	    	    	}
 	    	   	}
			},panels]
		}).show();
	},
	//处理时分秒
	dealTimeValue:function(id){
		var bottles = Ext.getCmp(id).getValue();
		return bottles;
	},
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('zp_exam_assign_dataStore');
		store.reload();
	},
	//导出成绩
	achieveMent:function(){
		var batch_id = Ext.getCmp("batch_ids").getValue();
		var map = new HashMap();
		if(batch_id!=null){
		map.put("batch_id",batch_id);
		}
	    Rpc({functionId:'ZP0000002538',async:false,success:examinee_me.showExcel},map);
	},
	showExcel:function(outparamters){
	var outparameters = Ext.decode(outparamters.responseText);
	var url=outparameters.fileName;
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
   },
   //下载模板
  uploadExcel:function (){
	  var batch_id = Ext.getCmp("batch_ids").getValue();
	  var map = new HashMap();
	  if(batch_id!=null){
	      map.put("batch_id",batch_id);
	  }
	  map.put("model", "true");
	  Rpc({functionId:'ZP0000002538',async:false,success:examinee_me.showExcel},map);
   },
   //导入成绩Excel
   createUploadFile:function(){
	   Ext.create("SYSF.FileUpLoad",{
			renderTo:"importScoreExcel",
			upLoadType:3,
			buttonText:'',
			fileSizeLimit:'20MB',
			fileExt:"*.xlsx;*.xls;",
			width:65,
			height:20,
			isTempFile:true,
            VfsModules:VfsModulesEnum.ZP,
            VfsFiletype:VfsFiletypeEnum.other,
            VfsCategory:VfsCategoryEnum.other,
			success:examinee_me.uploadSuccess
		});
	   Ext.getDom("importScoreExcel").childNodes[1].style.marginTop = "-20px";
   },
   //上传完成后执行Excel导入
   uploadSuccess:function(list){
	   var upfile = Ext.getCmp("imputeId");
	   if(upfile)
		   upfile.close();
	   
	   Ext.MessageBox.wait("", "正在导入数据");	
	   score = setInterval(function () {
		   var amap = new HashMap();
		   amap.put("zpflag", "examScore");
 		   Rpc( {
 			  functionId : 'ZP0000002540',
 			  success : examinee_me.judSuccess
 		   }, amap);
	   }, 5000);
	   
	   var map = new HashMap();
	   map.put("batchId",Ext.getCmp("batch_ids").getValue());
	   Ext.each(list,function(obj){
			var fileId = obj.fileid;
			map.put("flag","1");
			map.put("fileId",fileId);
			Rpc({
				functionId : 'ZP0000002539',
				async:true,
				success : function (a){}
		    }, map);
	
		});
   },
   judSuccess:function(res){
	   var outparameters = Ext.decode(res.responseText);
		var jsonData=outparameters.error_message;
		if(!jsonData){
			return;
		}
		
		clearInterval(score);
		var message = Ext.decode(jsonData);
		var batchId = outparameters.batchId;
		Ext.MessageBox.close();
		if(message.length==3){
			var num = message[0];
			var noInfos = message[1];
			var list = message[2];
			var message = "成功导入"+num+"条记录<br><br>";
			var noInfo = "";
			var mesInfo = "";
			var a=0;
			var b=0;
			for(var c in noInfos){
				a++;
				if(a<=5){
					noInfo=noInfo+"第"+c+"行,"+noInfos[c]+"<br>";
				}
			}
			if(a!=0){noInfo = "有"+a+"条记录没有准考证号导入失败<br>"+noInfo;}
			if(a>5){noInfo=noInfo+"……<br>"}
			for(var c in list){
				b++;
				if(b<=10){
					mesInfo=mesInfo+"第"+c+"行,"+list[c]+"<br>";
				}
			}
			if(b!=0){mesInfo = "有"+b+"条记录准考证号查不到导入失败<br>"+mesInfo;}
			if(b>5){mesInfo=mesInfo+"……<br>"}
			Ext.showAlert(message+noInfo+"<br>"+mesInfo);
		}else{
			Ext.showAlert(message);
		}
		Ext.getCmp("batch_ids").setValue(batchId);
		examinee_me.conditionSearchList('2',"00",batchId);
		
		 var amap = new HashMap();
		 amap.put("zpflag", "examScore");
		 amap.put("deleteInfor", "1");
		 Rpc( {
			 functionId : 'ZP0000002540',
			 success : examinee_me.judSuccess
		 }, amap);
   },
   //渲染方法
   dealShowResult:function(value,metaData,record){
	   if(Ext.isEmpty(value))
		   return "-";
	   if(value.indexOf("`") != -1)
		   return value.split("`")[1];
	   else
		   return value;
   },
   importData:function(){
		var win = Ext.create('Ext.window.Window',{
			title:'导入成绩',
	 		id:'imputeId',
	 		width:300,
	 		height:180,  
	        resizable: false,  
	        modal: true,
	        border:false,
	       	bodyStyle: 'background:#ffffff;',
	       	layout: {
	            type: 'vbox',
//	            align: 'left'
	            padding:'0 0 0 50'
	        },
	       	items:[{
	       		xtype:'container',
	       		margin: '30 0 0 0',
	       		layout:'hbox',
	       		items:[{
		            xtype:"label",
		            margin: '0 30 0 0',
		            width:120,
		            text:"1、下载模板文件"
		        },{
		            xtype:"button",
		            text:"下载",
		            handler:examinee_me.uploadExcel
		        }]},{
	       			xtype:'container',
	       			margin: '30 0 0 0',
		       		layout:'hbox',
		       		items:[{
			            xtype:"label",
			            width:120,
			            margin: '0 30 0 0',
			            html:"2、请选择导入文件"
		        },{
		            xtype:"button",
		            width:40,
		   	   		height:22,
		            text:"浏览"
		        }]},
		        {
		   	   		xtype:'box',
		   	   		border:false,
		   	   		width:40,
		   	   		height:22,
		  	   		margin: '-22 0 0 150',
		  	   		style:{
		  	   			background:'',
		  	   			borderColor:'#c5c5c5',
		  	   			borderStyle:'dashed'
		  	   		},
		  	   		listeners:{
		  	   			render:function(){
		  	   				var uploadBox = this;
		  	   				Ext.widget("fileupload",{
		  	   					upLoadType:3,
		  	   					height:22,width:40,
		  	   					buttonText:'',
		  	   					fileExt:"*.xls;*.xlsx",//添加对上传文件类型控制
		  	   					fileSizeLimit:'20MB',
		  	   					renderTo:uploadBox.id,
		  	   					callBackScope:'',
			  	   				isTempFile:true,
				  	              VfsModules:VfsModulesEnum.ZP,
				  	              VfsFiletype:VfsFiletypeEnum.other,
				  	              VfsCategory:VfsCategoryEnum.other,
				  	              success:examinee_me.uploadSuccess
		  	   				});
		  	   			}
		  	   		}
		        }]
		});
		win.show();
	
   
   }
});