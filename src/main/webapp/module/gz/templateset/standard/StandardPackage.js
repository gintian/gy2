/**
 * 历史沿革列表界面
 * qinxx 2019-12-5
 */
Ext.define('Standard.StandardPackage',{
	extend:'Ext.panel.Panel',
	layout:'fit',
	requires:['Standard.StandardList','Standard.EditStandardPackage'],
	initComponent:function(){
		standardPackagePage = this;
		this.callParent();
		this.loadStandardPackage();
	},
	loadStandardPackage:function(){
		//点击保存有提示信息，翻页自动保存无提示信息
		standardPackagePage.showAlertFlag = true;
		var param = new HashMap();
		Rpc({
			functionId: 'GZ00001201',
			success: standardPackagePage.createPageLayout,
			scope: standardPackagePage
		}, param);


	},

	/**
	 * 创建页面布局
	 */
	createPageLayout:function(res){
		var result = Ext.decode(res.responseText);
		if (result.return_code === 'success') {
			var return_data = result.return_data;
			var tableConfig = Ext.decode(return_data.tableConfig);
			tableConfig.onChangePage = function () {
				//翻页自动保存无提示信息
				standardPackagePage.showAlertFlag = false;
				standardPackagePage.saveStandardPackage();
			};
			//当前人员权限map
			standardPackagePage.funcPrivMap = return_data.funcPrivMap;
			//tablebulid创建之前插件改为双击修改
			tableConfig.beforeBuildComp = function(grid){
				grid.tableConfig.plugins.pop();
				grid.tableConfig.plugins.push({ptype:"cellediting",clicksToEdit:2});
			}

			standardPackagePage.tableObj = new BuildTableObj(tableConfig);
			standardPackagePage.addEvent();
			standardPackagePage.add(standardPackagePage.tableObj.getMainPanel());
		} else {
			Ext.Msg.alert(gz.standard.tip, eval(result.return_msg));
		}
	},
	/**
	 * 添加监听事件
	 */
	addEvent: function () {
		standardPackagePage.tableObj.tablePanel.on('beforeedit', function (owner, row) {
			var pkg_id = row.record.data.pkg_id_e;
			//控制是否有重命名和修改所属组织的权限
			if (row.field === "name") {
				if (!standardPackagePage.funcPrivMap[pkg_id].renameFlag) {
					return false;
				}
			}else if (row.field === "b0110") {
				if (!standardPackagePage.funcPrivMap[pkg_id].editFlag) {
					return false;
				}
			}
		});
		standardPackagePage.tableObj.tablePanel.on('cellclick',function(panel,td,cellIndex,record,tr,rowIndex,e,eOpts){
			//单击第一列和复选框列时不能编辑
			if(cellIndex == 1||cellIndex == 0){
				return;
			}
			//单击其余列可编辑
			var cellediting = standardPackagePage.tableObj.tablePanel.findPlugin('cellediting');
			cellediting.startEditByPosition({
				row:rowIndex,
				column:cellIndex
			})
		})
	},

	/**
	 * 自定义渲染启用列
	 */
	renderStatusColumn:function(value, metaData, record){
		var pkg_id = record.data.pkg_id_e;
		var status = record.data.status;
		var styleHtml = "style='cursor:pointer;width:48px;height:24px;' ";
		var clickHtml = "onclick='standardPackagePage.startStandardPackage(\"" + status + "\",\"" + pkg_id + "\")'";
		if(status=='1'){
			var html = "<img src='./images/open.png' " + styleHtml + clickHtml + "/>";
	        return html;
		}else{
			var html = "<img src='./images/close.png' " + styleHtml + clickHtml + "/>";
	        return html;
		}
	},
	renderOrganizationFunc:function(value){
		if(!value){
			return '全部';
		}else{
			return value.split('`')[1];
		}
	},
	/**
	 * 自定义渲染操作列
	 */
	renderOperateColumn:function(value, metaData, record){
		var pkg_id_e = record.data.pkg_id_e;
		var name = record.data.name;
		var styleHtml = 'style="cursor:pointer;width:24px;height:24px;margin-right:10px"';
        var html = '<div><img src="./images/sdIcon.png" title='+ "'" +gz.standard.pkg.browsePackage+ "'" + styleHtml +
            'onclick="standardPackagePage.sdIconClick(\'' + pkg_id_e +'\')"/>';

		if (standardPackagePage.funcPrivMap[pkg_id_e].editFlag) {
			html = html + '<img src="./images/edit.png" title='+ "'" +gz.standard.pkg.editPackage+ "'" + styleHtml +
				'onclick="standardPackagePage.addStandardPackage(\'' + pkg_id_e + '\',\'' + name + '\')"/>';
		}

		//正在启用 且 有导入导出权限的 才显示下载按钮
		if (standardPackagePage.funcPrivMap[pkg_id_e].importOrExportFlag) {
			html = html + '<img src="./images/downLoad.png" title='+ "'" +gz.standard.pkg.downloadPackage + "'" + styleHtml +
				'onclick="standardPackagePage.exmportStandard(\'' + pkg_id_e + "," + name + '\')"/>';
		}

		if (standardPackagePage.funcPrivMap[pkg_id_e].deleteFlag) {
			html = html + '<img src="./images/delete.png" title='+ "'" +gz.standard.pkg.deletePackage+ "'" + styleHtml +
				'onclick="standardPackagePage.deleteStandardPackage(\'' + pkg_id_e + '\')"/>';
		}
		return html + '</div>';
	},

	/**
	 * 启用历史沿革
	 */
	startStandardPackage: function (status, pkg_id) {
		//先判断有没有启用历史沿革的权限
		var noEnable = false;
		//启用中的沿革id
		var oldEnablePkgId = "";
		for (var key in standardPackagePage.funcPrivMap) {
			var funcPriv = standardPackagePage.funcPrivMap[key];
			//正在启用的沿革
			if (funcPriv.status == "1") {
				oldEnablePkgId = key;
				//没有修改启用的权限
				if (!funcPriv.enableFlag) {
					noEnable = true;
					break;
				}
			}
		}
		//点击的沿革没有修改启用的权限 或者 正在启用的沿革没有修改启用的权限 则return
		noEnable = !(standardPackagePage.funcPrivMap[pkg_id].enableFlag) || noEnable;

		if (noEnable) {
			//无权启用历史沿革！
			Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.noPrivEnablePkg);
			return;
		}

		//有修改的数据未保存给出提示
		var modifiedRecords = standardPackagePage.tableObj.dataStore.getModifiedRecords();
		if (modifiedRecords.length > 0) {
			//请先保存修改的数据！
			Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.firstSaveData);
			return;
		}

		//不允许关闭当前启用的历史沿革
		if (status === '1') {
			Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.stopClosePkg);
			return;
		}

		// 是否启用该历史沿革？
		Ext.Msg.confirm(gz.standard.tip, gz.standard.pkg.enablePkgConfirm, function (res) {
			if (res === "yes") {
				var param = new HashMap();
				param.put("pkg_id", pkg_id);
				Rpc({
					functionId: 'GZ00001209', success: function (result) {
						var result = Ext.decode(result.responseText);
						if (result.return_code == 'success') {
							//启用后更改前台缓存正在启用的沿革
							standardPackagePage.funcPrivMap[oldEnablePkgId].status = "0";
							standardPackagePage.funcPrivMap[pkg_id].status = "1";
							standardPackagePage.tableObj.dataStore.reload();
						} else {
							Ext.Msg.alert(gz.standard.tip, eval(result.return_msg));
						}
					}, scope: standardPackagePage
				}, param);
			}
		});
	},

	/**
	 * 保存历史沿革
	 */
	saveStandardPackage: function () {
		var modifiedRecords = standardPackagePage.tableObj.dataStore.getModifiedRecords();
		//数据未修改，无需保存！
		if (modifiedRecords.length == 0) {
			//点击保存按钮有提示，翻页无提示
			if (standardPackagePage.showAlertFlag) {
				Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.noDataToSave);
			}
			standardPackagePage.showAlertFlag = true;
			return;
		}
		//存放需要更新的数据
		var updateInfor = [];
		for (var i = 0; i < modifiedRecords.length; i++) {
			var modifiedData = standardPackagePage.assembleData(modifiedRecords, i);
			updateInfor.push(modifiedData);
		}

		//保存所有修改的数据
		var map = new HashMap();
		map.put("updateInfor", updateInfor);
		Rpc({
			functionId: "GZ00001204", success: function (res) {
				var result = Ext.decode(res.responseText);
				if (result.return_code === 'success') {
					if (standardPackagePage.showAlertFlag) {
						//保存成功
						Ext.Msg.alert(gz.standard.tip, gz.standard.saveSuccess);
					}
					standardPackagePage.funcPrivMap = result.return_data.funcPrivMap;
					standardPackagePage.tableObj.dataStore.reload();
				} else {
					Ext.Msg.alert(gz.standard.tip, eval(result.return_msg));
				}
				standardPackagePage.showAlertFlag = true;
			}, scope: standardPackagePage
		}, map);
	},

	/**
	 * 删除历史沿革
	 */
	deleteStandardPackage:function(pkg_id){
		//是否要删除此历史沿革
		Ext.Msg.confirm(gz.standard.tip, gz.standard.pkg.deletePkgConfirm, function(opt){
			if(opt=="yes"){
				var param = new HashMap();
				param.put("pkg_id",pkg_id);
				Rpc({functionId:"GZ00001207", success: function (res) {
					var result = Ext.decode(res.responseText);
					if (result.return_code === 'success') {
						if(result.return_data.flag === "yes"){
							//没有引用标准表的历史沿革删除后刷新
							standardPackagePage.tableObj.dataStore.reload();
						} else{
							//引用标准表的历史沿革不允许删除
							Ext.Msg.alert(gz.standard.tip, gz.standard.pkg.stopDeletePkg);
						}
					} else {
						Ext.Msg.alert(gz.standard.tip, eval(result.return_msg));
					}
				}, scope: standardPackagePage},param)
			}
		});
	},

	/**
	 * 新增/修改历史沿革界面
	 */
	addStandardPackage: function (pkg_id, pkgName) {
		var title = gz.standard.pkg.addTitle;
		var init_type = "create";
		if (pkg_id) {
			title = pkgName;
			init_type = "edit";
		} else {
			for (var key in standardPackagePage.funcPrivMap) {
				var status = standardPackagePage.funcPrivMap[key].status;
				if (status == "1") {
					pkg_id = key;
					break;
				}
			}
		}
		Ext.create("Standard.EditStandardPackage", {
			privMap: standardPackagePage.funcPrivMap,
			pkg_id: pkg_id,
			title: title,
			init_type: init_type
		}).show();
	},

	exmportStandard: function (data) {  //下载
		var strs = data.split(","); //字符分割
		var pkg_id = strs[0];  //历史沿革
		var name = strs[1];//历史沿革name
		Ext.require('Standard.ExportStandard', function () {
			Ext.create("Standard.ExportStandard", {
				"pkg_id": pkg_id, "name": name
			});
		});
	},

	/**
	 * 拼装数据
	 * @param resourceData 数据源
	 * @param index 数据索引
	 * @returns {{pkg_id: *}}
	 */
	assembleData: function (resourceData, index) {
		var oneData = resourceData[index].data;
		var modifiedData = {"pkg_id": oneData.pkg_id_e};
		if (trim(oneData.name)) {
			modifiedData.name = oneData.name;
		}
		if (oneData.b0110) {
			modifiedData.b0110 = oneData.b0110;
		}
		return modifiedData;
	},

	/**
	 * 导出历史沿革
	 * @param tableInfo table信息
	 * @param selectRecord 已选择的记录
	 */
	exportButton: function (tableInfo, selectRecord) {
		if( selectRecord.length == 0){
			Ext.MessageBox.alert(gz.standard.tip,gz.standard.pkg.tipValue);
			return;
		}
		if( selectRecord.length>1){
			Ext.MessageBox.alert(gz.standard.tip,gz.standard.pkg.exportStandPackage);
			return;
		}
		var pkg_id = selectRecord[0].data.pkg_id_e;
		if (!standardPackagePage.funcPrivMap[pkg_id].importOrExportFlag) {
			//您无权导入导出当前已启用的历史沿革！
			Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.noImportOrExportPriv);
			return;
		}

		var name = selectRecord[0].data.name;
		standardPackagePage.exmportStandard(pkg_id + "," + name);
	},

	/**
	 * 导入
	 */
	importButton:function(){
		var noImport = false;
		for (var key in standardPackagePage.funcPrivMap) {
			var funcPriv = standardPackagePage.funcPrivMap[key];
			//正在启用的沿革 且 没有导入导出的权限
			if (funcPriv.status == "1" && !funcPriv.importOrExportFlag) {
				noImport = true;
				break;
			}
		}
		if(noImport) {
			//您无权导入导出当前已启用的历史沿革！
			Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.noImportOrExportPriv);
			return;
		}
		Ext.require('Standard.ImportStandard', function () {
			Ext.create("Standard.ImportStandard", {});
		});
	},

	sdIconClick:function(pkg_id_e,status){    //打开历史沿革相对应的薪资标准表列表
		standardPackagePage.removeAll(true);
		var stanardList = Ext.create('Standard.StandardList',{
			exparam:pkg_id_e,//加密后的历史沿革套序号的值
	    });
		standardPackagePage.add(stanardList);
	},

	/**
	 * 单击名字穿透
	 */
	renderNameColumn:function(value, metaData, record, rowIndex, colIndex, store, view){
		var html= '<span style="color:#1B4A98;cursor: pointer" onclick="standardPackagePage.sdIconClick(\''+record.data.pkg_id_e+'\')">' + value + '</span>';
        return html;
    },

    /**
     * 控制名称禁止输入空格
     */
	trim:function(String) {
		//\uFEFF为es5新增的空白符，\XA0是不间断空白符 &nbsp;
		return String.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,'');
	},

    /**
     * 名称编辑后校验
     */
	nameValidate:function(name){
		var errMsg = "";
        if (!trim(name)) {
        	errMsg = gz.standard.pkg.inputValidate;//"该输入项为必填项"
        }
        return errMsg ? errMsg : true;
    }

});
