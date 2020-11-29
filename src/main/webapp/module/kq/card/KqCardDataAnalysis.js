Ext.define('KqCardDataURL.KqCardDataAnalysis',{
	constructor:function(config) {
		KqCardDataAnalysis = this;
		KqCardDataAnalysis.prefix="KqCardDataAnalysis_01";
		KqCardDataAnalysis.interval;
		this.init();
	},
	
	init:function(){
		var map = new HashMap();
	    Rpc({functionId:'KQ00021603',success:KqCardDataAnalysis.showTable},map);
	},
	//加载表格
	showTable: function (response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var conditions=map.tableConfig;
			var tableConfig = Ext.decode(conditions);
			if(!Ext.util.Cookies.get(tableConfig.cookiePre+"_" + KqCardDataAnalysis.prefix + "_filter"))
				Ext.util.Cookies.set(tableConfig.cookiePre+"_" + KqCardDataAnalysis.prefix + "_filter","e0122");
			
			KqCardDataAnalysis.table = new BuildTableObj(tableConfig);
			var tablePanel = KqCardDataAnalysis.table.getMainPanel();
			
			var win = Ext.getCmp("KqCardDataAnalysis");
		    if(win)
		    	win.close();
		    
		    win = new Ext.window.Window({
		    	title : kq.card.analysis,
		    	renderTo : Ext.getBody(),
				maximized : true,
				border : false,
				id : 'KqCardDataAnalysis',
				closable : false,
				autoScroll : true,
				items:[tablePanel],
				layout : 'fit',
				border : false
			});
		    
		    win.show();
			
			KqCardDataAnalysis.createSearchPanel(Ext.decode(map.fieldArray));
		} else {
			Ext.showAlert(map.message);
		}
			
	},
	// 查询控件
	createSearchPanel: function(fieldsArray){
		var me = this;
		var map = new HashMap();
		
		me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			id:'queryFieldBox',
			hideQueryScheme:false,
			emptyText:kq.card.searchEmptyText,
			subModuleId:KqCardDataAnalysis.prefix,
			customParams:map,
			funcId:"KQ00021604",
			fieldsArray:fieldsArray,
			success:function (){Ext.getCmp(KqCardDataAnalysis.prefix + '_tablePanel').getStore().reload({page:1});}
		});
		
		Ext.getCmp(KqCardDataAnalysis.prefix + '_toolbar').add(me.SearchBox);
	},
	//处理考勤状态的文字颜色
	showStatus: function(value,c,record) {
		if(Ext.isEmpty(value)){
			return "";
		}
		
		var html = "";
		var values = value.split(",");
		for(var i = 0; i < values.length; i++){
			if(html && html.length > 0){
				html += ",";
			}
			
			if(kq.card.leave == values[i] || kq.card.publicRelease == values[i]){
				html += "<font color='green'>" + values[i] + "</font>";
			} else if(kq.card.late == values[i] || kq.card.leaveEarly == values[i] 
				|| kq.card.absent == values[i]){
				html += "<font color='red'>" + values[i] + "</font>";
			} else{
				html += values[i];
			}
		}
		
		return html;
	},
	//切换日期查询数据
	searchCardData:function (){
		var sDate = Ext.getCmp("sDate").value;
		var eDate = Ext.getCmp("eDate").value;
		if(sDate.getTime() > eDate.getTime()) {
			Ext.showAlert(kq.card.errorTimemsg);
			return;
		}
		
		eDate = Ext.Date.format(eDate,"Y.m.d");
		sDate = Ext.Date.format(sDate,"Y.m.d");
		var param = {
				sDate: sDate,
				eDate: eDate
		};
		var map = new HashMap();
		map.put("type", "searchAction");
		map.put("param", Ext.encode(param));
	    Rpc({functionId:'KQ00021603',success:KqCardDataAnalysis.reloadTable},map);
	},
	//重新加载表格
	reloadTable: function(response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			if("true" == map.errorMsg) 
				Ext.getCmp(KqCardDataAnalysis.prefix + '_tablePanel').getStore().reload();
			else
				Ext.showAlert(map.errorMsg);
		} else {
			Ext.showAlert(map.message);
		}
	},
	//导出excel
	exportCardData: function () {
		var selectRecord = Ext.getCmp(KqCardDataAnalysis.prefix + '_tablePanel').getView().getSelectionModel().getSelection();
		var cardDatas = new Array();
		for(var i = 0; i < selectRecord.length; i++){
			var data = selectRecord[i].data;
			var cardData = {
					guidkey : data.guidkey_e,
					kq_date : data.kq_date
			};
			
			cardDatas.push(cardData);
		}
		Ext.MessageBox.wait("", kq.card.exportMsg);	
		var map = new HashMap();
		map.put("type", "exportAction");
		map.put("param", Ext.encode(cardDatas));
		Rpc({functionId:'KQ00021603',success:KqCardDataAnalysis.exportSucc},map);
	},
	//导出excel
	exportSucc: function (response){
		Ext.MessageBox.close();
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+map.fileName+"&fromjavafolder=true";
		} else {
			Ext.showAlert(map.message);
		}
	},
	// 数据分析
	dataAnalys: function () {
		var win = Ext.getCmp("dataAnalysWin");
		if(win)
			win.close();
		
		win = Ext.create('Ext.window.Window', {
			title: kq.card.dataCount,
			id:"dataAnalysWin",
			height: 200,
			width: 300,
			modal:true,
			items: [{
				xtype:'datetimefield',
				id:'startDate',
				format:'Y.m.d',
				height:23,
				labelWidth: 80,
				fieldLabel:kq.card.sDate,
				labelAlign:'right'
				
			},{
				xtype:'datetimefield',
				id:'endDate',
				format:'Y.m.d',
				height:23,
				labelWidth: 80,
				fieldLabel:kq.card.eDate,
				labelAlign:'right'
			}],
			layout: {
		        align: 'middle',
		        pack: 'center',
		        type: 'vbox'
			},
			buttonAlign: 'center',
			buttons: [{
				text: kq.button.ok,
				handler:KqCardDataAnalysis.dataAnalysAction
			},{
				text: kq.button.close,
				handler:function(){
					win.close();
				}
			}]
		});
		
		win.show();
	},
	//数据分析操作
	dataAnalysAction:function(){
		var sDate = Ext.getCmp("startDate").value;
		var eDate = Ext.getCmp("endDate").value;
		if(Ext.isEmpty(sDate)){
			Ext.showAlert(kq.card.sDateEmpty);
			return;
		}
		
		if(Ext.isEmpty(eDate)) {
			Ext.showAlert(kq.card.eDateEmpty);
			return;
		}
		
		if(sDate.getTime() > eDate.getTime()) {
			Ext.showAlert(kq.card.errorTimemsg);
			return;
		}
		
		sDate = Ext.Date.format(sDate,"Y.m.d");
		eDate = Ext.Date.format(eDate,"Y.m.d");
		var param = {
				sDate: sDate,
				eDate: eDate
		};
		Ext.MessageBox.wait("", kq.card.countMsg);	
		var map = new HashMap();
		map.put("type", "dataAnalysAction");
		map.put("param", Ext.encode(param));
		KqCardDataAnalysis.interval = setInterval(function () {
			var amap = new HashMap();
			amap.put('flag','1');
			Rpc( {
				functionId : 'KQ00021606',
				success : KqCardDataAnalysis.showAnalysInfor
			}, amap);
		}, 10000);
	    Rpc({functionId:'KQ00021605',success: function(response) {
	    	
	    }},map);
	},
	//关闭数据分析页面
	closeWin: function(){
		var win = Ext.getCmp("KqCardDataAnalysis");
	    if(win)
	    	win.close();
	},
	//关闭数据统计操作的窗口
	showAnalysInfor: function (param){
    	var value = param.responseText;
		var map	 = Ext.decode(value);
		if("true" != map.finish){
			return;
		} else {
			Ext.MessageBox.close();
			if(map.succeed){
				if('true' == map.errorMsg) {
					KqCardDataAnalysis.reloadTable(param);
					var win = Ext.getCmp("dataAnalysWin");
					if(win)
						win.close();
				} else {
					Ext.showAlert(map.errorMsg);
				}
			} else {
				Ext.showAlert(map.message);
			}
			
			clearInterval(KqCardDataAnalysis.interval);
		}
	}
});