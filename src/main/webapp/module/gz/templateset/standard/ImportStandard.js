Ext.define('Standard.ImportStandard', {
    extend: 'Ext.window.Window',
    id: "importStandard",
    layout: 'fit',
    requires: ['SYSF.FileUpLoad'],
    initComponent: function () {
        this.callParent();
        importStandard = this;
        importStandard.importFunc();
    },

    importFunc: function () {
        var fileUpLoad = Ext.create("SYSF.FileUpLoad", {
            // margin: '7 0 0 10',
            upLoadType: 1,//指定单文件上传
            emptyText: gz.standard.pkg.importfileTypesDesc,
            readInputWidth: 302,
            fileExt: "*.zip",
            buttonText: gz.standard.pkg.importbuttonText,
            isTempFile: true,
            VfsModules: VfsModulesEnum.GZ,
            VfsFiletype: VfsFiletypeEnum.other,
            VfsCategory: VfsCategoryEnum.other,
            CategoryGuidKey: '',
            success: function (files) {
                importStandard.importFile = files[0];
                var param = new HashMap();
                param.put('file', importStandard.importFile);
				param.put("flag", "0");
                Ext.Msg.wait(gz.standard.tip, gz.standard.pkg.importTipValue);//正在导入数据
                Rpc({
                    functionId: 'GZ00001208', success: function (resp) {
                        var result = Ext.decode(resp.responseText);
                        if (result.returnStr.return_code == "success") {
                            Ext.Msg.hide();
                            win.close();
                            importStandard.loadTable(result.returnStr.return_data.gzStandardPackageInfo);
                        } else {
                            Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.errorImpotInsert);
                            win.close();
							window.open("/servlet/vfsservlet?fileid=" + result.returnStr.return_msg + "&fromjavafolder=true");
                        }
                    }
                }, param);
            },
        });
        var win = Ext.create('Ext.window.Window', {
            title: gz.standard.pkg.importWindowText,
            layout: 'vbox',
            width: 400,
            height: 120,
            id: 'importDataWindow',
            modal: true,
            items: [{
                xtype: 'tbtext',
                padding: '10 0 0 14', //上，左，下，右
                text: gz.standard.importTip
            }, {
                xtype: 'panel',
                border: false,
                style: 'padding: 0 0 5px 13px;',
                items: [fileUpLoad]
            }]
        });
        win.show();

    },

    loadTable: function (list) {
        // 加载table表中数据
        importStandard.store = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data: list
        });
        importStandard.ttables = Ext.create('Ext.grid.Panel', {
            id: 'ttables',
//	width : 245,
//	height : 405,
            border: 1,
            margin: ' 0 0 0 0',
            columnLines: true,
            rowLines: true,
            store: importStandard.store,
            scroll: true,
            renderTo: Ext.getBody(),
            mode: "multi",
            selModel: {// 模型实例或配置对象，或选择模型类的别名字符串
                selType: 'checkboxmodel',
                mode: "multi",
                enableKeyNav: true
            },
            bufferedRenderer: false,// 一起把数据去拿过来，不用假分页模式
            columns: [{
                text: gz.standard.pkg.exportFirstColumName,
                dataIndex: 'id',
                hideable: true,
                menuDisabled: true,
                align: 'right',
                flex: 1,
            }, {
                text: gz.standard.pkg.exportSecondeColumName,
                dataIndex: 'name',
                hideable: true,
                menuDisabled: true,
                sortable: false,
                flex: 3,
                renderer: function (value, metdata) {
//			var headerCt = this.getHeaderContainer();
//			headerCt.addCls("columnStyle");
                    metdata.tdStyle = 'border-right:0px !important';
                    return value;
                }
            }

            ]
        });

        var winStandard = Ext.create('Ext.window.Window', {
            title: gz.standard.pkg.importWindowText,
            layout: 'fit',
            width: 400,
            height: 500,
            id: 'window',
            modal: true,
            border: false,
            constrainHeader: true,
            items: [importStandard.ttables],
            buttonAlign: 'center',
            buttons: [{
                text: gz.standard.pkg.importSecWindowSecButtonText,
                handler: function () {
                    var stand_ids = [];
                    var rows = importStandard.ttables.getSelectionModel()
                        .getSelection();
                    if (rows.length == 0) {
                        Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.importSecWindowText);
                        return;
                    }
                    for (var i = 0; i < rows.length; i++) {
                        if (rows[i].get('id') == "") {
                            continue;
                        } else {
                            stand_ids.push(rows[i].get('id'));
                        }
                    }

                    Ext.MessageBox.confirm(gz.standard.tip, gz.standard.pkg.isImpotInsert, coverStandard);

                    function coverStandard(btn) {
                        if (btn == "yes") {
                            var map = new HashMap();
                            map.put('file', importStandard.importFile);
                            map.put("stand_ids", stand_ids.toString());//选中的要导出的标准表id
                            map.put("pkg_id", importStandard.pkg_id);//历史沿革id
                            map.put("flag", "1");  //覆盖
                            Rpc({
                                functionId: 'GZ00001208',
                                async: false,
                                success: function (res) {
                                    var result = Ext.decode(res.responseText);
                                    var return_code = result.returnStr.return_code;
                                    if (return_code != "success") {  //导入失败
                                        Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.errorImpotInsert);
                                        window.open("/servlet/vfsservlet?fileid=" + result.returnStr.return_msg + "&fromjavafolder=true");
                                    }
                                }
                            }, map);
                            winStandard.destroy();
                        }
                    }
                }
            },{
                text: gz.standard.pkg.importSecWindowFirButtonText, //追加
                handler: function () {
                    var stand_ids = [];
                    var rows = importStandard.ttables.getSelectionModel().getSelection();
                    if (rows.length == 0) {
                        Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.importSecWindowText);
                        return;
                    }
                    for (var i = 0; i < rows.length; i++) {
                        if (rows[i].get('id') == "") {
                            continue;
                        } else {
                            stand_ids.push(rows[i].get('id'));
                        }
                    }
                    Ext.MessageBox.confirm(gz.standard.tip, gz.standard.pkg.isImport, addStandard);

                    function addStandard(btn) {
                        if (btn == "yes") {
                            var map = new HashMap();
                            map.put('file', importStandard.importFile);
                            map.put("stand_ids", stand_ids.toString());//选中的要导出的标准表id
                            map.put("pkg_id", importStandard.pkg_id);//历史沿革id
                            map.put("flag", "2");  //追加
                            Rpc({
                                functionId: 'GZ00001208',
                                async: false,
                                success: function (res) {
                                    var result = Ext.decode(res.responseText);
                                    var return_code = result.returnStr.return_code;
                                    if (return_code != "success") {
                                        Ext.MessageBox.alert(gz.standard.tip, gz.standard.pkg.errorImpotInsert);
                                        window.open("/servlet/vfsservlet?fileid=" + result.returnStr.return_msg + "&fromjavafolder=true");
                                    }
                                }
                            }, map);
                            winStandard.destroy();
                        }
                    }
                }
            }],
        });
        winStandard.show();
    }
});