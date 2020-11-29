/*
 * 所得税管理
 * salaryid 薪资类型，加密数据
 */
Ext.define('SearchTaxUL.SearchTax',{
	requires:['SYSF.FileUpLoad','Date.DateYM'],
	constructor:function(config) {
		searchtax_me = this;
		searchtax_me.taxMode="";
		searchtax_me.init(config);
		searchtax_me.config=config;
		searchtax_me.dateComBoxTime="";//页面显示日期，仅在选择时更新。
		
	},
	// 初始化函数
	init:function(config) {
		searchtax_me.salaryid = config.salaryid;
		searchtax_me.datetime = config.datetime;//实际业务日期
		if(searchtax_me.datetime!="JEPWw5tnIio@3HJD@")
			searchtax_me.dateComBoxTime=searchtax_me.datetime;
		var map = new HashMap();
		map.put("taxMode", searchtax_me.taxMode);//计税方式
		map.put("salaryid", searchtax_me.salaryid);
		map.put("datetime", searchtax_me.datetime);
		map.put("firstComing", true);
	    Rpc({functionId:'GZ00000510',async:false,success:searchtax_me.initOK},map);
	},
	initOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		searchtax_me.getItems = result.items;
		searchtax_me.reportMenu=Ext.decode(result.reportMenu);
		searchtax_me.publicPlanPriv=result.publicPlanPriv;
		searchtax_me.taxMode=result.taxMode;
        searchtax_me.isComputeDep=result.isComputeDep;
        searchtax_me.map_name=result.map_name;
        searchtax_me.count = 1;
		var obj = Ext.decode(conditions);
		searchtax_me.datafields = obj.datafields;
		obj.showPlanBox=false;
		templatelistObj = new BuildTableObj(obj);
		
		if(searchtax_me.salaryid!=null&&searchtax_me.salaryid.length>0){
			var vs = Ext.getBody().getViewSize();
	    	searchtax_me.win = Ext.create('Ext.window.Window', {
	    	    title: "所得税管理<a id='schemeSetting'  href='javascript:void(0)'  onclick='searchtax_me.schemeSetting()' style='position:absolute;right:2px;top:3px'><img src='/components/tableFactory/tableGrid-theme/images/Settings.png'  title='栏目设置'/></a>",
		        height:vs.height,  
		        width:vs.width,
		        id:'mainwin',
		        scrollable:false,
				modal:true,
				border:false,
				layout:'fit',
				closeAction:'destroy',
	    	    items: [templatelistObj.getMainPanel()]
	    	});
	    	searchtax_me.win.show();
		}
		
		// searchtax_me.dateStore = Ext.create('Ext.data.Store', {
		// 	fields:['dataName','dataValue'],
		//     data : result.datelist
		//  });
		searchtax_me.tablename = result.tablename;
		searchtax_me.datetime = result.datetime;
				
		var params = new Object();
		params.salaryid=searchtax_me.salaryid;
		params.datetime=searchtax_me.datetime;
		params.subModuleId="SearchTax";
        params.tablename=result.tablename;
        params.taxMode=searchtax_me.taxMode;
		Ext.getCmp("SearchTax_querybox").setCustomParams(params);
		
		// //报税时间下拉框对象
		// searchtax_me.dateComBox = Ext.create('Ext.form.ComboBox', {
		// 		id:'codesetid',
		// 		store:searchtax_me.dateStore,
		// 		fieldLabel:'报税时间',
		// 		labelSeparator:null,
		// 		blankText : '请选择',
		// 		labelAlign:'right',
		// 		labelWidth:55,
		// 		labelStyle:"color:#1b4a98",
		// 		editable:false,
		// 		displayField:'dataName',
		// 		valueField:'dataValue',
		// 		queryMode:'local',
		// 		width:160,
		// 		listeners: {
		// 			'select':function(combo,records,eOpts){
		// 				searchtax_me.selectDate(records.data.dataValue);
		// 			}
		// 		}
		// });
		//
		// searchtax_me.dateComBox.setValue(searchtax_me.dateStore.getAt(0).data.dataValue);
		


//		var select = Ext.widget('button',{
//			text: '输出报表',
//			maxWidth:100,
//			minWidth:37,
//			menu:{
//				items: searchtax_me.reportMenu
//			}
//		});
//		templatelistObj.toolBar.insert(5,select);		
		
//		var fill = Ext.create('Ext.panel.Panel', {
//			border:0,
//			width: 20,
//			height:10,
//		     tbar : [
//		         { xtype: 'tbfill' }
//		     ]
//		 });
//		if(searchtax_me.salaryid!=""){
//			templatelistObj.toolBar.insert(9,fill);
//		}else{
//			templatelistObj.toolBar.insert(7,fill);
//		}
		
		//设定日期初值
		if(searchtax_me.dateComBoxTime==""||searchtax_me.dateComBoxTime==undefined){
            if(searchtax_me.datetime==""||searchtax_me.datetime=="JEPWw5tnIio@3HJD@") {
                var myDate = new Date();
                searchtax_me.dateComBoxTime = myDate.getFullYear() + '.' + (parseInt(myDate.getMonth()) + 1);
            }else{
                searchtax_me.dateComBoxTime = searchtax_me.datetime;
			}
		}

		searchtax_me.date_me=Ext.create('Date.DateYM',{
			scope:searchtax_me,
			datechecked:function(year,month){
				Ext.getDom("dateymid").style.textDecoration="underline";
				searchtax_me.selectDate(year+"."+month);
			}
		});
		
		/*var list = [["全部", "0"], ["工资薪金", "1"], ["全年一次性奖金", "2"], ["企业年金", "3"], ["劳务报酬", "4"]];  
		searchtax_me.condStore = Ext.create('Ext.data.Store', {
			fields:['dataName','dataValue'],
		    data : list
		 });*/
		var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
			border:0,
			id:"toolbar",
			dock:'top',
			height:27,
			items:[{
				xtype:'label',
				text: '数据源：',
				style:'margin-right:10px'
			},{
				xtype:'label',
				html:'<a href="javascript:searchtax_me.changeFrom(\'53P4LJ3FkNrloIwicaDY8dQ\');" id="00" group="all" style="text-decoration: underline;color:green">当前个税明细表</a> ',
				style:'margin-left:10px;margin-right:10px;'
			},{
				xtype:'label',
				hidden:result.archivesPriv=='1'?false:true,
				html:'<a href="javascript:searchtax_me.changeFrom(\'rebQ8VIJ1dEh0byegywRKA\')" id="01" group="hall" style="text-decoration: none;color:#1b4a98">个税明细归档表</a> ',
				style:'margin-left:10px;margin-right:10px;'
			},'-',{xtype:'label',style:'margin-right:5px;'}
			]
		});
		
		for(var key in searchtax_me.map_name) {
			if(searchtax_me.map_name.hasOwnProperty(key)) {
				var label = Ext.widget({
					xtype:'label',
					html:'<a href="javascript:searchtax_me.selectTaxMode(\'' + key + '\')" id="taxMode' + key + '" group="hall" style="text-decoration: none;color:#1b4a98">' + searchtax_me.map_name[key] + '</a> ',
					style:'margin-left:8px;margin-right:10px;'
				});
				toolbar.add(label);
				searchtax_me.count += 1;
			}
		}
		
		var label = Ext.widget({
			xtype:'label',
			html:'<a href="javascript:searchtax_me.selectDate(\'JEPWw5tnIio@3HJD@\')" id="02" group="hall" style="text-decoration: none;color:#1b4a98">全部</a> ',
			style:'margin-left:10px;margin-right:10px;'
		});
		toolbar.add('-',{xtype:'label',style:'margin-right:5px;'},searchtax_me.date_me.picker);
		toolbar.add(label);
		templatelistObj.insertItem(toolbar,0);
//		if(searchtax_me.datetime!=undefined&&searchtax_me.datetime!=""&&searchtax_me.datetime!="all"){
//			searchtax_me.datetime=searchtax_me.datetime.replace(/-/g,".");
//			var datearr = searchtax_me.datetime.split(".");
//			Ext.get("timetitle").setHtml(datearr[0]);
//			Ext.get("monthtitle").setHtml(datearr[1]);
//		}else 
		if(searchtax_me.dateComBoxTime!=undefined&&searchtax_me.dateComBoxTime!=""){
			searchtax_me.dateComBoxTime=searchtax_me.dateComBoxTime.replace(/-/g,".");
			var datearr = searchtax_me.dateComBoxTime.split(".");
			Ext.get("timetitle").setHtml(datearr[0]);
			Ext.get("monthtitle").setHtml(datearr[1]);
		}
		if(searchtax_me.datetime!=undefined&&searchtax_me.datetime!="JEPWw5tnIio@3HJD@")
			Ext.getDom("dateymid").style.textDecoration="underline";
		else
			Ext.getDom("02").style.textDecoration="underline";
	},
	selectTaxMode:function(taxMode){
		for(var i = 1; i < searchtax_me.count; i++) {
			if(i == taxMode) {
				if(Ext.getDom("taxMode" + i).style.textDecoration=="none") {
					Ext.getDom("taxMode" + i).style.textDecoration="underline";
					searchtax_me.taxMode = taxMode;
				}else {
					Ext.getDom("taxMode" + i).style.textDecoration="none";
					searchtax_me.taxMode = "0";
				}
			}else {
				Ext.getDom("taxMode" + i).style.textDecoration="none";
			}
		}
        var params = new Object();
        params.salaryid=searchtax_me.salaryid;
        params.datetime=searchtax_me.datetime;
        params.tablename=searchtax_me.tablename;
        params.subModuleId="SearchTax";
        params.taxMode=searchtax_me.taxMode;
        Ext.getCmp("SearchTax_querybox").setCustomParams(params);

		var map = new HashMap();
		map.put("taxMode", searchtax_me.taxMode);//计税方式
		map.put("datetime",searchtax_me.datetime);
		map.put("tablename", searchtax_me.tablename);
		map.put("salaryid", searchtax_me.salaryid);
	    Rpc({functionId:'GZ00000510',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	searchtax_me.tablename=result.tablename;
	    	Ext.data.StoreManager.lookup('SearchTax_dataStore').loadPage(1);
	    }},map);
	},
	//报税时间下拉
	selectDate:function(datetimen){
		//输出报表菜单下面的按钮数组  xiegh 20170508 bug25135
		var a = templatelistObj.toolBar.items.items[0].menu.items.items;
		if(datetimen=="JEPWw5tnIio@3HJD@"){
			Ext.getDom("02").style.textDecoration="underline";
			Ext.getDom("dateymid").style.textDecoration="none";
			for(var p in a){
				if(a[p].text=='导出申报汇总表')
					a[p].setHidden(false);
				else
					a[p].setHidden(true);
			}
		}else{
			Ext.getDom("02").style.textDecoration="none";
			searchtax_me.dateComBoxTime=datetimen;//取得显示数字。
				for ( var p in a) {
					a[p].setHidden(false);
				}
		}
		searchtax_me.datetime = datetimen;
		var params = new Object();
		params.salaryid=searchtax_me.salaryid;
		params.datetime=searchtax_me.datetime;
		params.subModuleId="SearchTax";
		Ext.getCmp("SearchTax_querybox").setCustomParams(params);
		
		var map = new HashMap();
		map.put("taxMode", searchtax_me.taxMode);//计税方式
		map.put("datetime",datetimen);
		map.put("tablename", searchtax_me.tablename);
		map.put("salaryid", searchtax_me.salaryid);
	    Rpc({functionId:'GZ00000510',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	searchtax_me.tablename=result.tablename;
	    	Ext.data.StoreManager.lookup('SearchTax_dataStore').loadPage(1);
	    }},map);
	},
	//选择数据来源
	changeFrom:function(table){
		if(table=="@2HJF@ojg0eJKpR3wKIWZ55zD7g@3HJD@@3HJD@"){
			Ext.getDom('00').style.textDecoration="none";
			Ext.getDom('01').style.textDecoration="underline";
		}else{
			Ext.getDom('00').style.textDecoration="underline";
			Ext.getDom('01').style.textDecoration="none";
		}
        var params = new Object();
        params.salaryid=searchtax_me.salaryid;
        params.datetime=searchtax_me.datetime;
        params.tablename=table;
        params.subModuleId="SearchTax";
        params.taxMode=searchtax_me.taxMode;
        Ext.getCmp("SearchTax_querybox").setCustomParams(params);
		var map = new HashMap();
		map.put("taxMode", searchtax_me.taxMode);//计税方式
		map.put("datetime",searchtax_me.datetime);
		map.put("tablename", table);
		map.put("salaryid", searchtax_me.salaryid);
		searchtax_me.tablename = table;
		Rpc({functionId:'GZ00000510',async:false,success:function(form,action){
			var value = form.responseText;
			var map	 = Ext.decode(value);
			//searchtax_me.dateStore.setData(map.datelist);
			//searchtax_me.dateComBox.setValue(searchtax_me.datetime);
			Ext.data.StoreManager.lookup('SearchTax_dataStore').loadPage(1);
		}},map);
	},
	//删除数据
	deleteData:function(){
		deletedProjectIds='';
		deleteMilestoneIds='';
		var tablePanel=Ext.getCmp('SearchTax_tablePanel');
		var selectRecord =tablePanel.getView().getSelectionModel().getSelection();
		tax_ids = ""
		var num = "";
		Ext.each(selectRecord,function(rec,index){
			tax_ids+=rec.data.tax_max_id_e+",";
			num++;
		});
		if(num<=0){
			Ext.Msg.alert("提示信息","请选择需要操作的记录！");
			return false;
		}
		Ext.Msg.confirm("提示信息", "您确定要删除选中的"+num+"条记录吗？", function(button, text){  
			if(button != "yes"){
				return;
			}
			var map = new HashMap();
			map.put("tax_ids", tax_ids);
			map.put("tablename", searchtax_me.tablename);
			Rpc({functionId:'GZ00000511',async:false,success:function(form,action){
				Ext.data.StoreManager.lookup('SearchTax_dataStore').reload();}},map);
		});
	},
	//导出申报明细表，合并导出，不合并导出
	exportDetail:function(button){
		var map = new HashMap();
		map.put("tablename", searchtax_me.tablename);
		map.put("exportDetail", "true");
		map.put("exportType_detail", button);
		map.put("datetime",searchtax_me.datetime);
		map.put("salaryid",searchtax_me.salaryid);
		map.put("taxMode",searchtax_me.taxMode);
//		Ext.Msg.confirm("提示信息", "是否按“人员，计税时间，计税方式”合并输出", function(button, text){  
		if(button == "yes"){
			map.put("exporttype", "1");
		}
		Rpc({functionId:'GZ00000511',async:false,success:function(outparam){
			var result = Ext.decode(outparam.responseText);
			if (result.succeed == true) {
				var filename = Ext.decode(outparam.responseText).filename;
				filename = getDecodeStr(filename);
				window.location.target="_blank";
				window.location.href = "/servlet/vfsservlet?fileid="+ filename +"&fromjavafolder=true";
			}else {
				Ext.showAlert(result.message);
			}
		}},map);
//		});
	},
	//导出申报汇总表
	exportCount:function(){
		var map = new HashMap();
		map.put("tablename", searchtax_me.tablename);
		map.put("exportCount", "true");
		map.put("datetime",searchtax_me.datetime);
		map.put("salaryid",searchtax_me.salaryid);
		map.put("taxMode",searchtax_me.taxMode);
		Rpc({functionId:'GZ00000511',async:false,success:function(outparam){
			var filename = Ext.decode(outparam.responseText).filename;
			filename = getDecodeStr(filename);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+ filename +"&fromjavafolder=true"; 
		}},map);
	},
	//设置指标后刷新页面
	refreshPage:function(strData){
		var mainwin=Ext.getCmp('mainwin');
		if(mainwin)
            mainwin.close();
        else
			templatelistObj.getMainPanel().destroy();
		var map = new HashMap();
		map.put("strData", strData.salarySetIDs);
        map.put("isComputeDep", strData.isComputeDep);
		Rpc({functionId:'GZ00000513',async:false,success:function(){
				var map = new HashMap();
				map.put("salaryid", searchtax_me.salaryid);
				map.put("datetime", searchtax_me.datetime);
				searchtax_me.init(map);
			}
		},map);
	},
	//上传文件
	uploadFile:function(){
		var uploadObj =  Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请输入文件路径或选择文件",
			upLoadType:1,
			fileSizeLimit:'20MB',
			fileExt:"*.xlsx;*.xls;",
			isTempFile:true,
			VfsModules:VfsModulesEnum.GZ,
			VfsFiletype:VfsFiletypeEnum.other,
			VfsCategory:VfsCategoryEnum.other,
			CategoryGuidKey:'',
			success:function(list){
				if(list.length!=0){
					var map = new HashMap();
					map.put("fileid",list[0].fileid);
					map.put("flag","1");
					map.put("salaryid",searchtax_me.salaryid)
					map.put("datetime",searchtax_me.datetime);
					win.close();
					var mainwin=Ext.getCmp('mainwin');
					if(mainwin)
						mainwin.close();
					else
						templatelistObj.getMainPanel().destroy();
					Ext.require('SearchTaxUL.SetTaxRelation',function(){
                		Ext.create("SearchTaxUL.SetTaxRelation",map);
                	})
				}
			}
		});
		var win=Ext.widget("window",{
  			title: '文件上传',
            modal:true,
            border:false,
            width:380,
  			height: 120,
            items:[{
               xtype: 'panel',
               border:false,
        	   layout:{  
	             	type:'vbox',  
	             	padding:'15 0 0 35', //上，左，下，右 
	             	pack:'center',  
	              	align:'middle'  
	            },
               items:[uploadObj]
           }]
	    }); 
	  	win.show();
	  },
	  //保存修改
	  saveEdit:function(){
			var store = Ext.getCmp("SearchTax_tablePanel").getStore();
			var updateList = store.getModifiedRecords();
			if(updateList.length==0)
				return;
			var datafields = searchtax_me.datafields;
			var map = new HashMap();
			var dataList = new Array();
			for(var i = 0;i<updateList.length;i++){
				var record = updateList[i];
				var dataMap = new HashMap();
				for(var m = 0;m<datafields.length;m++) {
					var fieldId = datafields[m];
					if(fieldId!=null)
						dataMap.put(fieldId,eval("record.data." + fieldId));
				}
				dataList.push(dataMap);
			}
			map.put("salaryid",searchtax_me.salaryid);
			map.put("tablename", searchtax_me.tablename);
			map.put("dateList", dataList);
			map.put("datafields", datafields);
			Rpc({functionId:'GZ00000515',async:false,success:function(form,action){
				var result = Ext.decode(form.responseText);
				var flag = result.flag;
				if("success"==flag){
					  store.load();
			  }else{
					  alert("修改失败");
			  }
			}},map);
	  },
	  changeTable:function() {
		 var map = new HashMap();
		 map.put(1, "");
		 var dataMap= new HashMap();
		 dataMap.put('salaryid',searchtax_me.salaryid);
		 dataMap.put("isComputeDep",searchtax_me.isComputeDep);
		 Ext.require('EHR.selectfield.SelectField',function(){
			 Ext.create("EHR.selectfield.SelectField",{imodule:'2',type:'0',comBoxDataInfoMap:map,title:'个税明细表结构设置',saveCallbackfunc:searchtax_me.refreshPage,rightDataList:searchtax_me.getItems,dataMap:dataMap});
		 })
	 },
	  //高级花名册
	  printInform:function(){
		var url="/general/muster/hmuster/searchHroster.do?b_search=link";
		url+="`nFlag=15`sortid=11";
		url+="`salarydate="+searchtax_me.datetime;
		url+="`fromTable="+searchtax_me.tablename;
		url+="`salaryid="+searchtax_me.salaryid+"`closeWindow=6";
	   	var framesurl = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	   		window.open(framesurl);
	},
	//栏目设置按钮事件
	schemeSetting:function(){
		Ext.require("EHR.tableFactory.plugins.SchemeSetting", function() {
			var window = new EHR.tableFactory.plugins.SchemeSetting({
					subModuleId : "searchtax_id001",
					schemeItemKey:'',
					itemKeyFunctionId:'',
					viewConfig:{
						publicPlan:searchtax_me.publicPlanPriv=='1'?true:false,
						sum:true,
						lock:true,
						merge:true,
				        pageSize:'20'
				    },
					closeAction:function () {
                        var mainwin=Ext.getCmp('mainwin');
                        if(mainwin)
                            mainwin.close();
                        else
                            templatelistObj.getMainPanel().destroy();
                        searchtax_me.init(searchtax_me.config);
                    }
				});
		});
	}
});