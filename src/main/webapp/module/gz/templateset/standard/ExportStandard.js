Ext.define('Standard.ExportStandard', {
	extend : 'Ext.window.Window',
	id : "exportStandard",
	layout : 'fit',
	initComponent : function() {
		this.callParent();
		exportStandard = this;
		exportStandard.pkg_id;
		exportStandard.name;
		this.loaDate();
	},
	
	loaDate : function() {
		var map = new HashMap();
		map.put("pkg_id",exportStandard.pkg_id);//历史沿革id
		Rpc({
			functionId : 'GZ00001205',
			success : function(result) {
				result = Ext.decode(result.responseText);
				if(result.returnStr.return_code=="success"){
					exportStandard.loadTable(result.returnStr.return_data.ref_standList);
				}else{
					Ext.MessageBox.alert(gz.standard.tip, eval(result.returnStr.return_msg));
				}
			},
			scope : this
		}, map);
	},

	loadTable : function(ref_standList) {
		// 加载table表中数据
		exportStandard.store = Ext.create('Ext.data.Store', {
			fields : [ 'id', 'name' ],
			data:ref_standList
		});
		exportStandard.ttables = Ext.create('Ext.grid.Panel', {
			id : 'ttables',
			border : true,
			columnLines : true,
			rowLines : true,
			store : exportStandard.store,
			renderTo : Ext.getBody(),
			mode : "multi",
			selModel : {// 模型实例或配置对象，或选择模型类的别名字符串
				selType : 'checkboxmodel',
				mode : "multi", 
				enableKeyNav : true,
			},
			bufferedRenderer : false,// 一起把数据去拿过来，不用假分页模式
			columns : [ {
				text : gz.standard.pkg.exportFirstColumName,
				dataIndex : 'id',
				hideable : true,
				menuDisabled : true,
				sortable:false,
				align: 'right',
				flex : 1,
			}, {
				text : gz.standard.pkg.exportSecondeColumName,
				dataIndex : 'name',
				hideable : true,
				menuDisabled : true,
				sortable:false,
				flex : 3,
				renderer:function(value,metdata){
//					var headerCt = this.getHeaderContainer();
//					headerCt.addCls("columnStyle");
					metdata.tdStyle = 'border-right:0px !important';//右边框设置为0
					return value;
				}
			}

			]
		});
		
		var win = Ext.create('Ext.window.Window', {
			title : exportStandard.name,
			layout : 'fit',
			width : 400,
			height : 500,
			id : 'window',
			modal : true,
			border : false,
			constrainHeader : true,
			items : [ exportStandard.ttables ],
			buttonAlign : 'center',
			buttons : [
					{
						text : gz.standard.pkg.exportFirstWindowButton,
						handler : function() {
							var a=exportStandard.store;
							var stand_ids = []; // 
							var rows = exportStandard.ttables.getSelectionModel()
									.getSelection();
							if (rows.length == 0) {
								Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.tipValue);
								return;
							}
							for (var i = 0; i < rows.length; i++) {
								if (rows[i].get('id') == "") {
									continue;
								} else {
									stand_ids.push(rows[i].get('id'));
								}
							}
							var map = new HashMap();
							map.put("stand_ids", stand_ids.toString());//选中的要导出的标准表id
							map.put("pkg_id", exportStandard.pkg_id);//历史沿革id
							map.put("outfilename", exportStandard.name);
							Rpc({
								functionId : 'GZ00001206',
								async : false,
								success : function(res) {
									var result = Ext.decode(res.responseText);
									if(result.returnStr.return_code=="success"){
										var fileName = result.returnStr.return_data.fileName;
										window.open("/servlet/vfsservlet?fileid=" + fileName + "&fromjavafolder=true");
									}else{
										Ext.MessageBox.alert(gz.standard.tip, eval(result.returnStr.return_msg));
									}
								}
							}, map);
							win.destroy();
						}
					},

					{
						text : gz.standard.pkg.exportSecondWindowButton,
						handler : function() {
							win.destroy();
						}
					},
			],
			renderTo : Ext.getBody()
		});
		win.show();

	},
})