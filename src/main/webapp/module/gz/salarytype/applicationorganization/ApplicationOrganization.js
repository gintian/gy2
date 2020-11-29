/**
 * 薪资应用机构-弹出框
 * sunjian 2018-8-10
 */

Ext.define('SalaryTypeUL.applicationorganization.ApplicationOrganization', {
    constructor: function (config) {
        salaryapporg_me = this;
        salaryapporg_me.salaryid = config.salaryid;
        salaryapporg_me.flag = config.flag;//0:薪资类别进来，1：薪资发放进入
        if (salaryapporg_me.flag == '1' && Ext.isEmpty(config.a00z2)) {
            Ext.showAlert(gz.label.createSalary);
            return;
        }
        salaryapporg_me.a00z2 = config.a00z2;//发放日期
        salaryapporg_me.a00z3 = config.a00z3;//发放次数
        salaryapporg_me.imodule = config.imodule;//薪资和保险区分标识  1：保险  否则是薪资
        salaryapporg_me.selectAll = true;
        salaryapporg_me.getStore();
    },
    getStore: function () {
        //获得薪资项目数据store
        salaryapporg_me.salaryItemStore = Ext.create('Ext.data.Store', {
            fields: ['enable', 'org_id', 'org_name', 'isAppli', 'username', 'fullname'],
            proxy: {
                type: 'transaction',
                functionId: 'GZ00000238',
                extraParams: {
                    salaryid: salaryapporg_me.salaryid,
                    a00z2: salaryapporg_me.a00z2,
                    type: salaryapporg_me.flag//0:薪资类别进来，1：薪资发放进入
                },
                reader: {
                    type: 'json',
                    root: 'appOrgDataList'
                }
            },
            listeners: {
                load: function (store, records, successful, operation) {
                    var responseText = Ext.util.JSON.decode(operation._response.responseText);
                    salaryapporg_me.start_date = responseText.start_date;
                    salaryapporg_me.end_date = responseText.end_date;
                    salaryapporg_me.isShare = responseText.isShare;
                    salaryapporg_me.isSelect = responseText.isSelect;
                    salaryapporg_me.orgid = responseText.orgid;
                    salaryapporg_me.handImportScope = responseText.handImportScope;
                    salaryapporg_me.configSelect = null;//panel选中的值记录
                    salaryapporg_me.checkedIndex = 0;
                    salaryapporg_me.createAppOrg();
                }
            },
            autoLoad: true
        });
    },

    createAppOrg: function () {
        var dateStore = Ext.create('Ext.data.Store', {
            fields: ['dataName', 'dataValue'],
            data: [
                {dataName: "1", dataValue: "1"},
                {dataName: "2", dataValue: "2"},
                {dataName: "3", dataValue: "3"},
                {dataName: "4", dataValue: "4"},
                {dataName: "5", dataValue: "5"},
                {dataName: "6", dataValue: "6"},
                {dataName: "7", dataValue: "7"},
                {dataName: "8", dataValue: "8"},
                {dataName: "9", dataValue: "9"},
                {dataName: "10", dataValue: "10"},
                {dataName: "11", dataValue: "11"},
                {dataName: "12", dataValue: "12"},
                {dataName: "13", dataValue: "13"},
                {dataName: "14", dataValue: "14"},
                {dataName: "15", dataValue: "15"},
                {dataName: "16", dataValue: "16"},
                {dataName: "17", dataValue: "17"},
                {dataName: "18", dataValue: "18"},
                {dataName: "19", dataValue: "19"},
                {dataName: "20", dataValue: "20"},
                {dataName: "21", dataValue: "21"},
                {dataName: "22", dataValue: "22"},
                {dataName: "23", dataValue: "23"},
                {dataName: "24", dataValue: "24"},
                {dataName: "25", dataValue: "25"},
                {dataName: "26", dataValue: "26"},
                {dataName: "27", dataValue: "27"},
                {dataName: "28", dataValue: "28"},
                {dataName: "29", dataValue: "29"},
                {dataName: "30", dataValue: "30"},
                {dataName: "31", dataValue: "31"}
            ]
        });

        //生成薪资项目grid
        salaryapporg_me.gridpanel = Ext.create('Ext.grid.Panel', {
            store: salaryapporg_me.salaryItemStore,
            width: 590,
            height: 345,
            multiSelect: true,
            columnLines: true,
            rowLines: true,
            border: false,
            bufferedRenderer: false,//一起把数据去拿过来，不用假分页模式
            tbar: [{
                xtype: 'toolbar',
                border: false,
                height: 34,
                hidden: salaryapporg_me.flag == 1 ? true : false,//薪资发放界面隐藏
                items: [{
                    xtype: 'combobox',
                    id: 'start_date',
                    width: 132,
                    labelAlign: 'left',
                    labelWidth: 72,
                    fieldLabel: gz.label.start,//填报日期：从
                    store: dateStore,
                    height:20,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'dataValue',
                    valueField: 'dataName',
                    value: salaryapporg_me.start_date
                }, {
                    xtype: 'combobox',
                    id: 'end_date',
                    width: 72,
                    labelAlign: 'left',
                    labelWidth: 12,
                    height:20,
                    fieldLabel: gz.label.to,//至
                    store: dateStore,
                    editable: false,
                    style: 'padding-left:2px;',
                    queryMode: 'local',
                    displayField: 'dataValue',
                    valueField: 'dataName',
                    value: salaryapporg_me.end_date
                }]
            }],
            columns: [
                {

                    text: salaryapporg_me.flag == 1 ?'<input name="selectall" type=checkbox id="selall" onclick="salaryapporg_me.selectALL(this.checked);" />':gz.label.use,
                    width: 45,
                    menuDisabled: true,
                    sortable: false,
                    align: 'center',
                    dataIndex: 'enable',
                    renderer: function (value, data, record, rowIndex) {
                    	//如果全选了，全选按钮得勾上
                    	var check = '';
                    	if(salaryapporg_me.flag==1 && salaryapporg_me.isSelect == true) {
                    		check = 'checked';
                    		Ext.getDom("selall").checked = true;
                    	}else if(salaryapporg_me.flag==0){
                    		check = value == '1' ? 'checked' : '';
                    	}
                        salaryapporg_me.checkedIndex++;
                        return '<input type=checkbox name="itemid" value='+record.data.enable+' id="use_salary_'+salaryapporg_me.checkedIndex+'" '+check+' onclick="salaryapporg_me.selectSingle();" />';
                    },
                    listeners: {
                        'click': function (el, domValue, rowIndex) {
                            var enableChecked = domValue.childNodes[0].childNodes[0].checked;
                            var checkFlag = "0";//薪资发放进来的保存为
                            if (enableChecked) {
                                checkFlag = "1";
                            }
                            //如果是薪资发放，并且不是第一次进来的这样的时候永远不取消下发状态
                            salaryapporg_me.salaryItemStore.data.items[rowIndex].data.enable = checkFlag;
                        }
                    }
                },
                {
                    text: gz.label.appOrganization.substring(0, 4),//应用机构
                    dataIndex: 'org_name',
                    flex: 2,
                    menuDisabled: true,
                    sortable: false
                },
                {
                    text: gz.label.isapplic,//是否下发
                    dataIndex: 'isAppli',
                    flex: 1,
                    hidden: salaryapporg_me.flag == 1 ? false : true,//仅薪资发放显示
                    menuDisabled: true,
                    sortable: false
                },
                {
                    text: gz.label.informant.substring(0, 3), //填报人
                    flex: 1,
                    menuDisabled: true,
                    sortable: false,
                    renderer: function (value, data, record, rowIndex) {
                        return '<div unselectable="on" title="' + gz.label.loginID + record.data.username + '" style="text-align:left;">' + record.data.fullname + '</div>';
                    },
                    listeners: {
                        'click': function (el, domValue, rowIndex) {
                            if (salaryapporg_me.flag == 0) {//只有薪资类别进来的才能点击修改填报人
                            	salaryapporg_me.picker = new PersonPicker({
                                    multiple: false,//因为只能选择一个人，不需要多选框
                                    isSelfUser: salaryapporg_me.isShare,//是否选择自助用户
                                    selfUserIsExceptMe: false,
                                    isMiddle: true,//是否居中显示
                                    isPrivExpression:salaryapporg_me.handImportScope=='1'?true:false,
                                    orgid: salaryapporg_me.orgid,
                                    //multipleAndSingle: true,
                                    callback: function (c) {
                                    	var beanOlds = salaryapporg_me.salaryItemStore.data.items[rowIndex].data;
                                    	var beanOld = new Array();
                                    	beanOld.push(beanOlds.a0100);
                                    	beanOld.push(beanOlds.username);
                                    	beanOld.push(beanOlds.org_id);
                                        var cc = c;
                                        domValue.innerHTML = '<div unselectable="on" class="x-grid-cell-inner " title="' + gz.label.loginID + cc.userName + '" style="text-align:left;">' + cc.name + '</div>';
                                        salaryapporg_me.salaryItemStore.data.items[rowIndex].data.a0100 = cc.id;
                                        salaryapporg_me.salaryItemStore.data.items[rowIndex].data.fullname = cc.name;
                                        salaryapporg_me.salaryItemStore.data.items[rowIndex].data.username = cc.userName;
                                        var beanNew = new Array();
                                        beanNew.push(cc.id);
                                        beanNew.push(cc.userName);
                                        beanNew.push(salaryapporg_me.salaryItemStore.data.items[rowIndex].data.org_id);
                                        var map = new HashMap();
                                    	map.put("beanOld",beanOld);
                                    	map.put("beanNew",beanNew);
                                    	map.put("salaryid",salaryapporg_me.salaryid);
                                    	map.put("imodule",salaryapporg_me.imodule);
                                    	map.put("type","3");
                                    	Rpc({
                                            functionId: 'GZ00000239', async: false, success: function (response, action) {
                                            	salaryapporg_me.picker.close();
                                            }
                                        }, map);
                                    }
                                }, el);
                            	salaryapporg_me.picker.open();
                            }
                        }
                    }
                },
                {
                    text: gz.label.start.substring(0, 4),//填报日期
                    flex: 1.5,
                    menuDisabled: true,
                    sortable: false,
                    hidden: salaryapporg_me.flag == 1 ? false : true,//仅薪资发放显示
                    renderer: function (value, data, record, rowIndex) {
                        if (record.data.start_date) {
                            salaryapporg_me.insertOrUpdate = "1";
                            return '<div style="text-align:center;">从&nbsp;&nbsp;<input type=text maxlength="2" onkeyup="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',0)" onafterpaste="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',0)" ' +
                                'style="text-align:center;width:20px;background-color: transparent;border-bottom:1px solid #818181;border-top:none;border-left:none;border-right:none;" value="' + record.data.start_date + '"/> ' +
                                '&nbsp;&nbsp;至&nbsp;&nbsp;<input type=text maxlength="2" onkeyup="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',1)" onafterpaste="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',1)" ' +
                                'style="text-align:center;width:20px;background-color: transparent;border-bottom:1px solid #818181;border-top:none;border-left:none;border-right:none;" value="' + record.data.end_date + '"/></div>';
                        } else {
                            salaryapporg_me.insertOrUpdate = "0";//是0插入还是1更新
                            salaryapporg_me.salaryItemStore.data.items[rowIndex].data.start_date = salaryapporg_me.start_date;
                            salaryapporg_me.salaryItemStore.data.items[rowIndex].data.end_date = salaryapporg_me.end_date;
                            return '<div style="text-align:center;">从&nbsp;&nbsp;<input type=text maxlength="2" onkeyup="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',0)" onafterpaste="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',0)" ' +
                                'style="text-align:center;width:20px;background-color: transparent;border-bottom:1px solid #818181;border-top:none;border-left:none;border-right:none;" value="' + salaryapporg_me.start_date + '"/> ' +
                                '&nbsp;&nbsp;至&nbsp;&nbsp;<input type=text maxlength="2" onkeyup="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',1)" onafterpaste="this.value = salaryapporg_me.replaceDigit(this.value,' + rowIndex + ',1)" ' +
                                'style="text-align:center;width:20px;background-color: transparent;border-bottom:1px solid #818181;border-top:none;border-left:none;border-right:none;" value="' + salaryapporg_me.end_date + '"/></div>';
                        }
                    }
                }
            ],
            listeners: {
                'select': function (rowmode, record, rowIndex) {//选中时显示当前的记录
                    salaryapporg_me.configSelect = record;
                }
            },
            renderTo: Ext.getBody()
        });


        //生成弹出得window
        var win = Ext.widget("window", {
            title: gz.label.appOrganization.substring(0, 4),
            width: 601,
            id: 'appOrganizationWinId',
            height: 420,
            minButtonWidth: 45,
            resizable: false,
            border: true,
            modal: true,
            closeAction: 'destroy',
            items: [
                salaryapporg_me.gridpanel
            ], buttons: [
                {xtype: 'tbfill'},
                {
                    hidden: salaryapporg_me.flag == 1 ? true : false,//薪资发放界面显示
                    text: gz.label.addOrganization,//新增机构
                    handler: function (el) {
                        //获取新增的行，这里每次只能增加一个，保存了之后再增加就没问题，不保存一直出现问题
                        var picker = new PersonPicker({
                            multiple: true,
                            text: "添加",
                            addunit: true, //是否可以添加单位
                            adddepartment: true, //是否可以添加单位
                            orgid: salarytype_me.orgid, // 组织机构，不传代表全部
                            addpost: false,
                            isPrivExpression: false,
                            //defaultSelected:workunits,
                            callback: function (c) {
                                for (var i = 0; i < c.length; i++) {
                                    var cc = c[i];
                                    var id = cc.id;//a0100
                                    id = cc.rawType + id;
                                    var value = cc.name;//名称
                                    var addList = salaryapporg_me.salaryItemStore.getNewRecords();
                                    var count = salaryapporg_me.salaryItemStore.count();
                                    salaryapporg_me.salaryItemStore.insert(count, {
                                        enable: '1',
                                        org_id: id,
                                        org_name: value,
                                        username: '',
                                        fullname: '',
                                        updateFlag: '0'
                                    });
                                }
                            }
                        }, el);
                        picker.open();

                    }
                },
                {
                    hidden: salaryapporg_me.flag == 1 ? true : false,//薪资发放界面显示
                    text: common.button.todelete,//删除
                    handler: function (e, c) {
                        if (salaryapporg_me.configSelect == null) {
                            Ext.showAlert(gz.label.deleteOrg);
                        } else {
                            //是否删除应用机构是
                            Ext.showConfirm(common.label.isDeleteSelected + gz.label.appOrganization + salaryapporg_me.configSelect.data.org_name +
                                "&nbsp;&nbsp;" + gz.label.informant + salaryapporg_me.configSelect.data.fullname +
                                "&nbsp;&nbsp;" + gz.label.record + "？     ",
                                function (v) {
                                    if (v == 'yes') {
                                    	//删除的时候
                                    	/*var map = new HashMap();
                                    	map.put("org_id",salaryapporg_me.configSelect.data.org_id);
                                    	map.put("username",salaryapporg_me.configSelect.data.username);
                                    	map.put("salaryid",salaryapporg_me.salaryid);
                                    	map.put("imodule",salaryapporg_me.imodule);
                                    	map.put("type","2");
                                    	Rpc({
                                            functionId: 'GZ00000239', async: false, success: function (response, action) {
                                                
                                            }
                                        }, map);*/
                                    	salaryapporg_me.configSelect.data.org_id;
                                        salaryapporg_me.salaryItemStore.remove(salaryapporg_me.configSelect);
                                        salaryapporg_me.configSelect = null;//清空上次删除的信息
                                    } else {
                                        return;
                                    }
                                }
                            )
                        }
                    }
                },
                {
                    text: common.button.ok,//确定
                    handler: function (e, c) {
                        salaryapporg_me.saveData();
                    }

                },
                {
                    text: common.button.cancel,//取消//关闭
                    handler: function () {
                        win.close();
                    }
                }, {xtype: 'tbfill'}
            ],
            listeners:{
       			'beforeclose':function(){
       				var win = Ext.getCmp('person_picker_single_view');
					if(win) {//关闭窗口之前，先判断是否有选人控件，有则关闭
						win.close();
					}
       			}
       		}
        });
        win.show();
    },

    //填报日期验证flag:0,开始时间1：结束时间
    replaceDigit: function (value, rowIndex, flag) {
        value = value.replace(/[^\d]/g, '');
        if (flag == "0")
            salaryapporg_me.salaryItemStore.data.items[rowIndex].data.start_date = value;
        else
            salaryapporg_me.salaryItemStore.data.items[rowIndex].data.end_date = value;
        salaryapporg_me.salaryItemStore.data.items[rowIndex].data.needUpdate = "1";
        return value;
    },

    saveData: function () {
        
        var items = salaryapporg_me.salaryItemStore.data.items;
        var writeInformant = true;
        if (salaryapporg_me.flag == "1") {
        	var checkboxs = Ext.query("*[name=itemid]");
	        Ext.each(checkboxs,function(checkbox,index){
	        	if(checkbox.checked == true) {
	        		if(items[index].data.fullname != '') {
	        			items[index].data.enable = "1";
	        		}else {//如果只维护了应用机构，未维护填报人，不能下发
	        			items[index].data.enable = "0";
	        			writeInformant = false;
	        		}
	        	}
	        });
        }
        
        if(salaryapporg_me.flag == "1" && !writeInformant) {
        	//如果是薪资发放页面进来的，并且有应用机构没有填报人的，提示一下未填写申报人的机构不会下发，确认下发吗？
	        Ext.showConfirm(gz.label.canDistribute, function (value) {
	        	if (value != "yes")
	                return;
	        	salaryapporg_me.returnSave();
	        	
	        });
        }else {
        	salaryapporg_me.returnSave();
        }
        
        
    },
    
  //全选
    selectALL:function(checked){
        var checkboxs = Ext.query("*[name=itemid]");
        Ext.each(checkboxs,function(checkbox,index){
            checkbox.checked=checked;
        })
    },
    //单选
    selectSingle:function() {
    	var check=true;
    	var checkboxs = Ext.query("*[name=itemid]");
    	Ext.each(checkboxs,function(checkbox,index){
    		if(!checkbox.checked)
    			check=false;
        })
        var selall= document.getElementById("selall");
    	selall.checked=check;
    },
    
    returnSave:function() {
    	var list = new Array();
    	var items = salaryapporg_me.salaryItemStore.data.items;
        for (var i = 0; i < items.length; i++) {
            //薪资发放如果是插入则全部添加，如果是更新则值添加修改的记录
            list.push(items[i].data);
        }
        var map = new HashMap();
        map.put("data", list);
        map.put("salaryid", salaryapporg_me.salaryid);
        if (salaryapporg_me.flag == "0") {
            var start_value = Ext.getCmp("start_date").getValue() == null ? '' : Ext.getCmp("start_date").getValue();
            var end_date = Ext.getCmp("end_date").getValue() == null ? '' : Ext.getCmp("end_date").getValue();
            if (start_value == '' || end_date == '') {
                Ext.showAlert(gz.label.saveDate);
                return;
            }
            map.put("start_date", start_value);
            map.put("end_date", end_date);
        }
        map.put("type", salaryapporg_me.flag);//0:薪资类别进来，1：薪资发放进入
        map.put("imodule", salaryapporg_me.imodule);
        map.put("A00Z2", salaryapporg_me.a00z2);
        map.put("A00Z3", salaryapporg_me.a00z3);
        map.put("insertOrUpdateFlag", salaryapporg_me.insertOrUpdate);
        Rpc({
            functionId: 'GZ00000239', async: false, success: function (response, action) {
                Ext.getCmp("appOrganizationWinId").close();
                if (salaryapporg_me.flag == "1") {
                    Ext.showAlert(gz.label.distributeSuccess);
                }
            }
        }, map);
    }
})