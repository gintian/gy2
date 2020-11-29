/**
 * 新增历史沿革窗口
 * qinxx 2019-12-10
 */
Ext.define('Standard.EditStandardPackage', {
	extend: 'Ext.window.Window',
	requires: ['EHR.extWidget.field.DateTimeField', 'EHR.extWidget.field.CodeTreeCombox'],
	layout: 'fit',
	width: 440,
	height: 500,
	modal: true,
	resizable: false,
	//config接收页面传递的属性会提供相应的get、set方法
	config: {
		privMap: '',
		pkg_id: '',
		init_type: ''
	},
	initComponent: function () {
		editStandardPck = this;
		this.callParent();
		//原来引用中的标准表id数组
		editStandardPck.oldEnableIdArr = [];
		//新引用的标准表id数组
		editStandardPck.newEnableId = [];
		//需要关闭引用的标准表id数组
		editStandardPck.needCloseId = [];

		this.loadData();
	},
	loadData: function () {
		var map = new HashMap();
		map.put("pkg_id", editStandardPck.getPkg_id());
		map.put("init_type", editStandardPck.getInit_type());
		Rpc({
			functionId: 'GZ00001202', success: function (res) {
				var result = Ext.decode(res.responseText);
				if (result.return_code == 'success') {
					var return_data = result.return_data;
					var standList = return_data.standList;
					var values = return_data.pkgMap;
					this.createAddPanel(standList, values);
				} else {
					Ext.Msg.alert(gz.standard.tip, eval(result.return_msg));
				}
			}, scope: this
		}, map);
	},
	/**
	 * 新增历史沿革panel
	 */
	createAddPanel: function (standList, values) {
		var pkg_id = editStandardPck.getPkg_id();
		var init_type = editStandardPck.getInit_type();

		//是否有关闭启用中历史沿革的权限
		var oldEnableFlag = true;
		//是否有启用正在编辑的历史沿革的权限
		var currentEnableFlag = true;
		//重命名所修改历史沿革的权限
		var renameFlag = true;

		//当前人员权限map
		var privMap = editStandardPck.getPrivMap();

		//之前选用的标准表id数组
		for (var i = 0; i < standList.length; i++) {
			var data = standList[i];
			var standId = data[0];
			var status = data[2];
			if (status == "1") {
				editStandardPck.oldEnableIdArr.push(standId);
			}
		}
		//拿到当前启用中的沿革权限map
		for (var key in privMap) {
			var status = privMap[key].status;
			if (status == "1") {
				//正在启用的历史沿革权限
				oldEnableFlag = privMap[key].enableFlag;
			}
		}

		//创建时的历史沿革启用权限		
		if (init_type == "create") {
			//即将启用的历史沿革的权限
			currentEnableFlag = privMap["common"].enableFlag;
		} else {
			//修改时的历史沿革启用权限
			currentEnableFlag = privMap[pkg_id].enableFlag;
			renameFlag = privMap[pkg_id].renameFlag;
		}

		var mainPanel = Ext.create("Ext.panel.Panel", {
			border: false,
			defaults: {
				margin: '10 0 0 18',
				width: 400
			},
			layout: {
				type: 'vbox',
			},
			buttonAlign: 'center',
			buttons: [{
				text: gz.standard.confirm,//确定
				handler: editStandardPck.saveStandData
			}, {
				text: gz.standard.cancel,//取消
				handler: function () {
					editStandardPck.close();
				}
			}]
		});

		//历史沿革启用日期
		var dateField = Ext.create("EHR.extWidget.field.DateTimeField", {
			itemId: 'standStartDate',
			format: "Y-m-d",
			width: 200,
			height: 22,
			allowBlank: false,
			editable: false,
			value: Ext.util.Format.date(Ext.Date.add(new Date()), 'Y-m-d')
		});
		//启用复选框
		var disabled = !(currentEnableFlag && oldEnableFlag) || values.status == "1";
		var startCheckBox = Ext.create("Ext.form.field.Checkbox", {
			itemId: 'status',
			disabled: disabled,
			boxLabel: gz.standard.pkg.start,
			style: 'margin-left:30px;',
			value: values.status == "1" ? true : false
		});

		//日期组件和复选框panel
		var dataPanel = Ext.create("Ext.panel.Panel", {
			layout: 'hbox',
			border: false,
			height: 25,
		});
		dataPanel.add(dateField);
		dataPanel.add(startCheckBox);
		
		//历史沿革名称输入框,且为必填项
		var nameContainer = Ext.create("Ext.container.Container",{
			layout:'hbox',
			height:22,
			items:[{
				xtype:'textfield',
				itemId: 'standName',
				height: 22,
				width: 390,
				emptyText: gz.standard.pkg.inputName,
				allowBlank: false,
				maxLength: 30,
				value: '',
				disabled: !renameFlag
			},{
				xtype: "displayfield",
				value:'<font color=red>*</font>',
				style:'margin-left:5px'
			}]
		})
		
		//历史沿革所属组织,且为必填项
		var b0110Container = Ext.create("Ext.container.Container",{
			layout:'hbox',
			height:22,
			items:[{
				xtype:'codecomboxfield',
				itemId: 'b0110',
				height: 22,
				width: 390,
				emptyText: gz.standard.pkg.selectOrganization,
				allowBlank: false,
				codesetid: 'UM',
				//是否只能选择根节点
				onlySelectCodeset: false,
				//权限控制方式 0：不控制 1：人员范围 2：操作单位 3：业务范围
				ctrltype: '3',
				//业务模块号
				nmodule: '1',
				value: values.b0110
			},{
				xtype: "displayfield",
				value:'<font color=red>*</font>',
				style:'margin-left:5px'
			}]
		})
		
		//文本：引用标准表
		var standardText = Ext.create("Ext.Component", {
			html: gz.standard.pkg.importStandard,
		});

		//标准表store
		var standardListStore = Ext.create("Ext.data.Store", {
			fields: ["standardId", "name", "status"],
			data: standList
		});
		//引用标准表列表
		var standardList = Ext.create("Ext.grid.Panel", {
			itemId: 'standardGrid',
			height: 290,
			width: 390,
			store: standardListStore,
			//禁止上下文菜单隐藏列
			enableColumnHide:false,
			//禁用列排序
			sortableColumns: false,
			//禁止列拉伸
			enableColumnResize: false,
			//网格线
			columnLines: true,
			columns: [
				{
					text: gz.standard.pkg.exportFirstColumName,
					dataIndex: 'standardId',
					renderer:function(value){
						return '<div align="right">'+value+'</div>';
					},
					flex:0.5
				}, {
					text: gz.standard.pkg.standardName,
					dataIndex: 'name',
					flex:3
				}, {
					text: gz.standard.pkg.selectUsed,
					dataIndex: 'status',
					flex:1,
					style: "border-right:0px",
					renderer: function (value, metaData, record) {
						//除去标题行，右边框设为0px						
                        metaData.tdStyle = 'border-right:0px !important';
						var standardId = record.data.standardId;
						
						var styleImg = "style='cursor:pointer;width:48px;height:24px;margin-left:16px;' ";
						var onclick = " onclick=editStandardPck.changeStandStatus(\"" + value + "\",\"" + standardId + "\") ";
						var openImg = "<img src='./images/open.png' " + styleImg + onclick + " />";
						var closeImg = "<img src='./images/close.png' " + styleImg + onclick + " />";
						return value == '1' ? openImg : closeImg;
					}
				}
			]
		});

		mainPanel.add(nameContainer);
		mainPanel.add(dataPanel);
		mainPanel.add(b0110Container);
		mainPanel.add(standardText);
		mainPanel.add(standardList);
		editStandardPck.add(mainPanel);
		editStandardPck.query('#standName')[0].focus();
		
		//修改时回显数据
		if (init_type == "edit") {
			editStandardPck.query('#standName')[0].setValue(values.name);
			editStandardPck.query('#standStartDate')[0].setValue(values.start_date);
		}
	},

	/**
	 * 历史沿革选用标准表
	 */
	changeStandStatus: function (value, standardId) {
		var store = editStandardPck.query("#standardGrid")[0].getStore();
		var records = store.data.items;
		//更改数据状态
		for (var i = 0; i < records.length; i++) {
			var record = records[i];
			var standId = record.data.standardId;
			if (standId == standardId) {
				if (value == '1') {
					//选用变为未选用
					record.data.status = "0";
				} else {
					//未选用变为选用
					record.data.status = '1';
				}
				record.commit();
				break;
			}
		}

		//默认关闭引用时需要移除needCloseId中的id
		var removeEnableFlag = true;
		//默认开启引用时需要将id添加到newEnableId数组中
		var needEnableFlag = true;
		
		for (var j = 0; j < editStandardPck.oldEnableIdArr.length; j++) {
			var oldEnableId = editStandardPck.oldEnableIdArr[j];
			if (standardId == oldEnableId) {
				if (value == '1') {
					//选用变为未选用
					editStandardPck.needCloseId.push(standardId);
					removeEnableFlag = false;
					break;
				} else {
					//未选用变为选用
					editStandardPck.needCloseId.remove(standardId);
					needEnableFlag = false;
					break;
				}

			}
		}
		if (value == '1' && removeEnableFlag) {
			editStandardPck.newEnableId.remove(standardId);
		} else if (value == '0' && needEnableFlag) {
			editStandardPck.newEnableId.push(standardId);
		}
	},

	//新建修改保存数据
	saveStandData: function () {
		//存放所选用所有标准表、新选用标准表、新关闭标准表id
		var ref_standIds = [];
		//存放所选用所有标准表
		var standardIdArr = [];

		var name = editStandardPck.query("#standName")[0].getValue();
		var start_date = editStandardPck.query("#standStartDate")[0].getValue();
		var status = editStandardPck.query("#status")[0].getValue();
		var b0110 = editStandardPck.query("#b0110")[0].getValue();
		var items = editStandardPck.query("#standardGrid")[0].getStore().data.items;
		if (!trim(name)) {
			Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.nameIsNull);
			return
		}
		if (!b0110) {
			Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.b0110IsNull);
			return
		}
		
		//获取引用标准表的id
		for (var i = 0; i < items.length; i++) {
			var selected = items[i].data.status;
			var standId = items[i].data.standardId;
			if (selected == "1") {
				standardIdArr.push(standId);
			}
		}

		//新增历史沿革id为空，修改则为当前id
		var pkg_id = '';
		if (editStandardPck.init_type == "edit") {
			pkg_id = editStandardPck.pkg_id
		}

		//是否启用历史沿革boolen转化为字符串
		if (status) {
			status = '1';
		} else {
			status = '0';
		}

		ref_standIds.push(standardIdArr);
		ref_standIds.push(editStandardPck.newEnableId);
		ref_standIds.push(editStandardPck.needCloseId);

		var pkg_infor = new HashMap();
		pkg_infor.put("pkg_id", pkg_id);
		pkg_infor.put("name", name);
		pkg_infor.put("start_date", start_date);
		pkg_infor.put("status", status);
		pkg_infor.put("owner_org", b0110);
		pkg_infor.put("ref_standIds", ref_standIds);
		Rpc({
			functionId: 'GZ00001203', success: function (res) {
				var result = Ext.decode(res.responseText);
				var return_data = result.return_data;
				if (result.return_code == 'success') {
					editStandardPck.close();
					standardPackagePage.tableObj.dataStore.reload();
					standardPackagePage.funcPrivMap = return_data.funcPrivMap;
				} else {
					Ext.Msg.alert(gz.standard.tip, eval(result.return_msg));
				}
			}, scope: this
		}, pkg_infor)
	},
	
});