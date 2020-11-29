/**
 * 生成上报数据
 * wangbs 2019年5月24日
 */
Ext.define('GenaratedataURL.GenerateData', {
	requires: ["GenaratedataURL.MatchScheme","GenaratedataURL.ReportDataWin"],
	constructor: function (config) {
		GenerateReportData = this;
		GenerateReportData.init();
	},
	/**
	 * 页面初始化数据
	 */
	init: function () {
		var map = new HashMap();
		map.put("operaType","search");
		map.put("searchPlan","all");
		Rpc({functionId: 'SYS0000003101', success: function (res) {
			var result = Ext.decode(res.responseText);
			if (result.return_code=="success") {
				GenerateReportData.createTableOK(result);
			} else {
				Ext.MessageBox.alert(dr.gd.tip, dr.gd.selectdataerrormsg);// 数据查询失败，请联系管理员！
			}
		}}, map);
	},
	/**
	 * 创建BuildTableObj
	 * @param result
	 */
	createTableOK: function (result) {
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
		var conditions = result.tableConfig;
		var obj = Ext.decode(conditions);
		var tableObj = new BuildTableObj(obj);

		var toolbar  = Ext.create('Ext.toolbar.Toolbar',{//查询方案toolbar
			border:0,
			dock:'top',
			items: [{
				xtype: 'label',
				text: dr.gd.selectPlan,//查询方案
				style: 'margin-left:5px'
			}
			,GenerateReportData.createLabel("all",dr.gd.all), '-'
			,GenerateReportData.createLabel("today",dr.gd.today)
			,GenerateReportData.createLabel("week",dr.gd.sevendays)
			,GenerateReportData.createLabel("month",dr.gd.thirtydays)]
		});
		tableObj.insertItem(toolbar,0);
	},
	/**
	 * 查询方案后面的label
	 * @param labelFlag 哪一个查询方案
	 * @param labelDesc 方案描述
	 * @returns {Ext.Component}
	 */
	createLabel:function(labelFlag,labelDesc){
		var label = Ext.create("Ext.Component", {
			html: labelDesc,
			style: 'cursor:pointer;color:#1B4A98;margin-left:5px;margin-right:5px;',
			listeners:{
				element: "el",
				click:function(){
					var map = new HashMap();
					map.put("operaType","search");
					map.put("searchPlan",labelFlag);
					Rpc({functionId: 'SYS0000003101', success: function (res) {
						var result = Ext.decode(res.responseText);
						if (result.return_code=="success") {
							Ext.getCmp(GenerateReportData.labelId).removeCls('scheme-selected-cls');
							label.addCls('scheme-selected-cls');
							GenerateReportData.labelId = label.id;
							GenerateReportData.labelFlag = labelFlag;
							//刷新数据
							Ext.getCmp('generatedata_tablePanel').getStore().reload();
						} else {
							Ext.MessageBox.alert(dr.gd.tip, dr.gd.selectdataerrormsg);// 数据查询失败，请联系管理员！
						}
					}},map);
				}
			}
		});
		if(labelFlag=="all"){//初始化样式
			label.addCls('scheme-selected-cls');
			GenerateReportData.labelId = label.id;//用于移除样式
			GenerateReportData.labelFlag = labelFlag;//用于删除日志时，知道传哪个方案
		}
		return label;
	},
	/**
	 * 渲染操作列
	 * @param value
	 * @param metaData
	 * @param record
	 */
	renderOperaCol: function (value, metaData, record) {
		var sendtype = record.data.sendtype;
        var id = record.data.recordid;//zyh 校验结果用来导出excel信息

		var html = "";//默认都不显示
		if (value == "1" && sendtype != dr.gd.database) {// 1 && 不为中间库方式 显示数据包，不显示校验结果
			html += "<a href=javascript:GenerateReportData.dataPackageClick('" + id + "');>" + dr.gd.datapackage + "</a>";
		} else if (value == "2") {//显示校验结果，不显示数据包
			html = "<a href=javascript:GenerateReportData.checkResultClick('" + id + "');>" + dr.gd.checkresult + "</a>";
		} else if (value == "3") {//都显示
			html = "<a href=javascript:GenerateReportData.checkResultClick('" + id + "');>" + dr.gd.checkresult + "</a>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
			if (sendtype != dr.gd.database) {//不为中间库方式 显示数据包
				html += "<a href=javascript:GenerateReportData.dataPackageClick('" + id + "');>" + dr.gd.datapackage + "</a>";
			}
		}
		return html;
	},
	/**
	 * 删除日志
	 */
	deleteLog: function () {
		var grid = Ext.getCmp('generatedata_tablePanel');
		var delArr = grid.getSelectionModel().getSelection();
		if (delArr.length == 0) {
			Ext.MessageBox.alert(dr.gd.tip, dr.gd.selectlog);//提示信息  请选择日志！
			return;
		}
		var delIdList = [];//删除日志的id数组
		for (var i = 0; i < delArr.length; i++) {
			delIdList.push(delArr[i].data.recordid);
		}

		Ext.Msg.confirm(dr.gd.tip,dr.gd.confirmdellog,function(btn){//确认删除日志？
			if (btn == "yes") {
				var map = new HashMap();
				map.put("operaType","delete");
				map.put("delIdList",delIdList);
				Rpc({functionId: 'SYS0000003101', success: function (res) {
					var result = Ext.decode(res.responseText);
					var return_code = result.return_code;
					if (return_code == "success") {
						var map = new HashMap();
						map.put("operaType","search");
						map.put("searchPlan",GenerateReportData.labelFlag);
						Rpc({functionId: 'SYS0000003101', success: function (res) {
							var returnData = Ext.decode(res.responseText);
							if (returnData.return_code=="success") {
								Ext.getCmp('generatedata_tablePanel').getStore().reload();
							} else {
								Ext.MessageBox.alert(dr.gd.tip, dr.gd.refreshselectdataerrormsg);// 数据查询失败，请联系管理员！
							}
						}},map);
					}else{
						Ext.Msg.alert(dr.gd.tip,dr.gd.dellogerrormsg);
					}
				}},map);
			}
		});
	},
	/**
	 * 创建方案
	 */
	createPlan: function () {
		Ext.create("GenaratedataURL.MatchScheme", {}).show();
	},
	/**
	 * 上报数据
	 */
	reportData: function () {
		Ext.create("GenaratedataURL.ReportDataWin", {}).show();
	},
	/**
	 * 下载校验结果
	 */
	checkResultClick: function (id) {
		Ext.MessageBox.wait("", in_the_process_of_exporting);
		var map = new HashMap();
	    map.put('id',id+'');
	    map.put('type','checkResult');
	    Rpc({functionId:'SYS0000003014',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					if(result.flag){
						Ext.MessageBox.close();	
						var fieldName = result.fileName;
						window.location.href = "/servlet/vfsservlet?fileid=" + fieldName + "&fromjavafolder=true";
					}else{
						Ext.MessageBox.close();	
						Ext.Msg.alert(hint_information,no_erroneous_log_records);
					}
				}else{
					Ext.MessageBox.close();	
					Ext.Msg.alert(hint_information,result.message);
				}
		}},map);
	},
	/**
	 * 下载数据包
	 * @param dataPackagePath 包路径
	 */
	dataPackageClick: function (id) {
		Ext.MessageBox.wait("", in_the_process_of_exporting);
		var map = new HashMap();
	    map.put('id',id+'');
	    map.put('operaType','package');
	    Rpc({functionId:'SYS0000003102',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					Ext.MessageBox.close();	
					var fieldName = result.fileName;
					if(fieldName){
						window.location.href = "/servlet/vfsservlet?fileid=" + fieldName + "&fromjavafolder=true";
					}else{
						Ext.showAlert("数据包不存在!");
					}
				}else{
					Ext.MessageBox.close();	
					Ext.Msg.alert(hint_information,result.message);
				}
		}},map);
	}
});
