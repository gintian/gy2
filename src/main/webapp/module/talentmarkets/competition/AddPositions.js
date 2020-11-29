Ext.define('Talentmarkets.competition.AddPositions', {
        extend: 'Ext.panel.Panel',
        layout: 'vbox',
        bodyPadding: '0 0 0 5',
        requires: ["EHR.extWidget.field.DateTimeField", "EHR.extWidget.field.CodeTreeCombox", "EHR.extWidget.field.BigTextField"],
        title: tm.contendPos.addPositions,
        bodyStyle: 'z-index:2',
        deprecateArr: [],//已选择的面试官
        interviewPersonArr: [],//面试官信息
        competitiveScopeArr: [],//竞聘范围数据
        defaultFieldMap: {},
        extraFieldList: [],
        buttonAlign: 'center',
        scrollable: 'y',
        initComponent: function () {
            AddPositions = this;
            this.callParent();
            this.init();
        },
        buttons: [
            {
                xtype: 'button',
                text: tm.contendPos.saveAndContinue,
                margin: '0 15 2 0',
                handler: function () {
                    if (AddPositions.checkValues()) {
                        AddPositions.saveValues();
                        AddPositions.refreshBasicFromValues();
                    }
                }
            },
            {
                xtype: 'button',
                text: tm.save,
                margin: '0 15 2 0',
                handler: function () {
                    if (AddPositions.checkValues()) {
                        AddPositions.saveValues();
                        competitionJobs.remove(AddPositions);
                        AddPositions.destroy();
                        Ext.getCmp("competitionJobsTable_mainPanel").show();
                    }
                }
            },
            {
                xtype: 'button',
                text: tm.config.msg.cancel,
                margin: '0 15 2 0',
                handler: function () {
                    competitionJobs.remove(AddPositions);
                    AddPositions.destroy();
                    Ext.getCmp("competitionJobsTable_mainPanel").show();
                }
            }
        ],
        init: function () {
            var vo = new HashMap();
            vo.put("operateType", "createInitData");
            Rpc({
                functionId: 'TM000000002', success: function (res) {
                    var resData = Ext.decode(res.responseText);
                    var return_code = resData.return_code;
                    if (return_code == 'success') {
                        AddPositions.defaultFieldMap = resData.return_data.fieldMap.defaultFieldMap;
                        AddPositions.extraFieldList = resData.return_data.fieldMap.extraFieldList;
                        AddPositions.orgIdStr = resData.return_data.orgIdStr;
                        AddPositions.openInterview = resData.return_data.openInterview;
                        AddPositions.initLayout();
                    } else if (return_code == 'getCreatePostFieldListError') {
                        Ext.Msg.alert(tm.tip, tm.contendPos.getCreatePostFieldListError);
                    }
                }, scope: this, async: false
            }, vo);
        },
        initLayout: function () {
            this.addTools();
            //创建基本信息
            this.createBasicInformationContainer();
            if (AddPositions.openInterview) {
                //创建面试官
                this.createUpPanel();
            }
            //创建竞聘范围
            this.createCompetitiveScopeContainer();
        },
        /**
         * 检查必填项
         */
        checkValues: function () {
            var basicInformationForm = AddPositions.query("#basicInformationForm")[0];
            var basicInformationFormValues = AddPositions.getBasicInformationFormValues();
            if (!basicInformationFormValues.e01a1) {
                Ext.Msg.alert(tm.tip, tm.contendPos.peleaseSelectE01a1);
                return false;
            }
            if (!basicInformationFormValues.z8105) {
                Ext.Msg.alert(tm.tip, tm.contendPos.peleaseInputZ8105);
                return false;
            }
            if (!basicInformationFormValues.z8107) {
                Ext.Msg.alert(tm.tip, tm.contendPos.peleaseInputZ8107);
                return false;
            }
            if (!basicInformationFormValues.z8111) {
                Ext.Msg.alert(tm.tip, tm.contendPos.peleaseInputZ8111);
                return false;
            }
            if (!basicInformationFormValues.z8113) {
                Ext.Msg.alert(tm.tip, tm.contendPos.peleaseInputZ8113);
                return false;
            }
            if (Ext.Date.parse(basicInformationFormValues.z8113, "Y-m-d H:i") < new Date()) {
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.notAllowEndDatelowNowDate);
                return false;
            }
            if(Ext.Date.parse(basicInformationFormValues.z8111,"Y-m-d H:i")>Ext.Date.parse(basicInformationFormValues.z8113,"Y-m-d H:i")){
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.notLegitimateDate);
                return false;
            }
            if(!basicInformationForm.isValid()){
                return false;
            }
            return true;
        },
        /**
         * 创建基本信息container
         */
        createBasicInformationContainer: function () {
            var basicInformationContainer = Ext.create('Ext.container.Container', {
                    width: '100%',
                    margin: '10 55 0 55',
                    items: [
                        AddPositions.createTitlePanel(tm.contendPos.basicInformation, "basicInformation"),
                        {
                            xtype: 'form',
                            itemId: 'basicInformationForm',
                            border: false,
                            width: '100%',
                            flex:1,
                            items: AddPositions.createDefaultInformationItems()
                        }

                    ]
                }
            );
            AddPositions.add(basicInformationContainer);
            if (AddPositions.extraFieldList.length > 0) {
                AddPositions.createBasicInformationExtraItems();
            }
        },
        createDefaultInformationItems: function () {
            var itemsList = [];
            var e01a1Field = AddPositions.createBasicInformationItem(AddPositions.defaultFieldMap.e01a1);
            e01a1Field.allowBlank = false;
            // e01a1Field.on('select', function (t, record) {
            //     var vo = new HashMap();
            //     vo.put("operateType", "checkPostStatus");
            //     vo.put("e01a1", record.get("id"));
            //     Rpc({
            //         functionId: 'TM000000002', success: function (res) {
            //             var resData = Ext.decode(res.responseText);
            //             var return_code = resData.return_code;
            //             if (return_code == 'success') {
            //                 debugger;
            //                 if(resData.return_data.isExits){
            //
            //                 }
            //             } else if (return_code == 'checkStatusSqlFail') {
            //                 Ext.Msg.alert(tm.tip, tm.config.msg.saveFailure);
            //             }
            //         }, scope: this, async: false
            //     }, vo);
            // });

            e01a1Field.treeStore.on('load', function (t) {
                //store用异步获取数据时  不能用filterby进行过滤  用下面的方式可以进行过滤
                var vo = new HashMap();
                var e01a1Arr = [];
                vo.put("operateType", "getIngE01a1");
                Rpc({
                    functionId: 'TM000000002', success: function (res) {
                        var resData = Ext.decode(res.responseText);
                        var return_code = resData.return_code;
                        if (return_code == 'success') {
                            e01a1Arr = resData.return_data.e01a1List;
                        } else if (return_code == 'getIngE01a1Error') {
                            Ext.Msg.alert(tm.tip, tm.contendPos.getIngE01a1Error);
                        }
                    }, scope: this, async: false
                }, vo);
                var filters = e01a1Field.treeStore.getFilters();

                function checkE01a1(item) {
                    return Ext.Array.indexOf(e01a1Arr, item.data.id) == -1;
                }

                filters.add(checkE01a1);
            });
            e01a1Field.treeStore.on('nodebeforeappend',function (t,node,e) {
                //非岗位节点  不显示复选框
                if(node.data.codesetid!='@K' && node.data.checked == false || node.data.checked == true){
                    node.data.checked = null;
                }
                return true;
            });
            //解决 codetreecombox  验证不及时问题
            e01a1Field.on('blur',function () {
                //当验证通过时 手动去除提示信息
                if(e01a1Field.isValid()){
                    e01a1Field.clearInvalid();
                }
            });

            var z8105Field = AddPositions.createBasicInformationItem(AddPositions.defaultFieldMap.z8105);
            z8105Field.allowBlank = false;
            z8105Field.setMinValue(0);
            var z8107Field = AddPositions.createBasicInformationItem(AddPositions.defaultFieldMap.z8107);
            z8107Field.allowBlank = false;
            z8107Field.setMinValue(0);
            AddPositions.defaultFieldMap.z8111.itemlength= 16;
            var z8111Field = AddPositions.createBasicInformationItem(AddPositions.defaultFieldMap.z8111);
            z8111Field.allowBlank = false;
            AddPositions.defaultFieldMap.z8113.itemlength= 16;
            var z8113Field = AddPositions.createBasicInformationItem(AddPositions.defaultFieldMap.z8113);
            z8113Field.allowBlank = false;
            itemsList.push(AddPositions.createBasicInformationConatainer(AddPositions.defaultFieldMap.e01a1.itemdesc, e01a1Field, true));
            itemsList.push(AddPositions.createBasicInformationConatainer(AddPositions.defaultFieldMap.z8105.itemdesc, z8105Field, true));
            itemsList.push(AddPositions.createBasicInformationConatainer(AddPositions.defaultFieldMap.z8107.itemdesc, z8107Field, true));
            itemsList.push(AddPositions.createBasicInformationConatainer(AddPositions.defaultFieldMap.z8111.itemdesc, z8111Field, true));
            itemsList.push(AddPositions.createBasicInformationConatainer(AddPositions.defaultFieldMap.z8113.itemdesc, z8113Field, true));
            return itemsList;
        },
        createBasicInformationExtraItems: function () {
            var basicInformationForm = AddPositions.query("#basicInformationForm")[0];
            for (var i = 0; i < AddPositions.extraFieldList.length; i++) {
                var item = AddPositions.createBasicInformationItem(AddPositions.extraFieldList[i]);
                basicInformationForm.add(AddPositions.createBasicInformationConatainer(AddPositions.extraFieldList[i].itemdesc, item, false));
            }
        },
        /**
         * 保存数据
         */
        saveValues: function () {
            var basicInformationFormValues = AddPositions.getBasicInformationFormValues();
            var vo = new HashMap();
            vo.put("operateType", "saveCreatePostData");
            vo.put("basicInformationValues", basicInformationFormValues);
            vo.put("competitiveScopeData", AddPositions.competitiveScopeArr);
            vo.put("interviewerData", AddPositions.interviewPersonArr);
            Rpc({
                functionId: 'TM000000002', success: function (res) {
                    var resData = Ext.decode(res.responseText);
                    var return_code = resData.return_code;
                    if (return_code == 'success') {
                        Ext.Msg.alert(tm.tip, tm.config.msg.saveSuccess,function () {
                            competitionJobs.tableObj.dataStore.reload();
                        });
                    } else if (return_code == 'saveCreatePostDataError') {
                        Ext.Msg.alert(tm.tip, tm.config.msg.saveFailure);
                    }
                }, scope: this, async: false
            }, vo);
        },
        getBasicInformationFormValues: function () {
            var basicInformationForm = AddPositions.query("#basicInformationForm")[0];
            return basicInformationForm.getValues();
        },
        /**
         * 重置formpanel value数据
         */
        refreshBasicFromValues: function () {
            //清空基本信息value
            var basicInformationForm = AddPositions.query("#basicInformationForm")[0];
            basicInformationForm.reset();
            //清空面试官数据
            var addPersonIcon = Ext.create("Ext.Img", {
                height: 48,
                width: 48,
                itemId: 'addPersonIcon',
                style: 'float:left;cursor:pointer',
                src: '../images/addPerson.png',
                listeners: {
                    element: 'el',
                    click: function () {
                        if (AddPositions.orgIdStr == "no") {
                            Ext.Msg.alert(tm.tip, tm.contendPsn.noPsnSelect);//'您权限范围内没有可选择的人员！';
                        } else {
                            AddPositions.createPersonPicker();
                        }
                    }
                }
            });
            var upContentPanel = AddPositions.query("#upContentPanel")[0];
            upContentPanel.removeAll(true);
            upContentPanel.add(addPersonIcon);
            var addcompetitiveScopeImg = Ext.create('Ext.Img', {
                    height: 48,
                    width: 48,
                    itemId: 'addCompetitiveScopeIcon',
                    style: 'float:left;cursor:pointer',
                    src: '../images/addPerson.png',
                    listeners: {
                        element: 'el',
                        click: function () {
                            var map = new HashMap();
                            map.put('codesetidstr', "UN,UM");
                            map.put('codesource', '');
                            map.put('nmodule', '4');
                            map.put('ctrltype', '3');
                            map.put('parentid', '');
                            map.put('searchtext', encodeURI(""));
                            map.put('multiple', true);
                            map.put('isencrypt', true);
                            map.put('confirmtype', '1');
                            map.put('callbackfunc', AddPositions.getOrgList);
                            Ext.require('EHR.orgTreePicker.OrgTreePicker', function () {
                                Ext.create('EHR.orgTreePicker.OrgTreePicker', {map: map});
                            }, AddPositions);
                        }
                    }
                }
            );
            AddPositions.interviewPersonArr = [];
            var competitiveScopeContainer = AddPositions.query('#competitiveScopeContainer')[0];
            competitiveScopeContainer.removeAll(true);
            competitiveScopeContainer.add(addcompetitiveScopeImg);
            AddPositions.competitiveScopeArr = [];
        },
        createBasicInformationConatainer: function (desc, item, requried) {
            var itemsContainer = Ext.create('Ext.container.Container', {
                margin: '15 0 5 0',
                layout: {
                    type: 'hbox',
                    align: 'center',
                },
                items: [
                    {
                        xtype: 'component',
                        html: '<span style="float: right">' + desc + '</span>',
                        margin: '0 25 0 0',
                        flex: 1
                    },
                    {
                        xtype: 'container',
                        flex: 4,
                        layout: {
                            type: 'hbox',
                            align: 'center',
                        },
                        items: [
                            item,
                            {
                                xtype: 'component',
                                margin: '14 0 0 3',
                                hidden: !requried,
                                html: '<font color="red"> * </font>'
                            }
                        ]
                    }

                ]
            });
            return itemsContainer;
        },
        createBasicInformationItem: function (field) {
            var width = 300;
            var height = 22;
            var items = Ext.create('Ext.form.field.Text', {
                name: field.itemid,
                width: width,
                height:height,
                selectOnFocus: true,
                focusable: true
            });
            if (field.itemtype == 'A' && field.codesetid != '0') {//代码型
                items = Ext.create('EHR.extWidget.field.CodeTreeCombox', {
                    codesetid: field.codesetid,
                    nmodule: "4",
                    ctrltype: "3",
                    name: field.itemid,
                    width: width,
                    height:height,
                    multiple: field.itemid == 'e01a1' ? true : false,
                    onFieldMutation: function(e) {//禁掉模糊查询 在ie下会导致一些问题，未找到解决办法 。
                        // When using propertychange, we want to skip out on various values, since they won't cause
                        // the underlying value to change.
                        var me = this,
                            task = me.checkChangeTask;

                        if (!me.readOnly && !(e.type === 'propertychange' && me.ignoreChangeRe.test(e.browserEvent.propertyName))) {
                            if (!task) {
                                me.checkChangeTask = task = new Ext.util.DelayedTask(me.doCheckChangeTask, me);
                            }
                            if (!me.bindNotifyListener) {
                                // We continually create/destroy the listener as needed (see doCheckChangeTask) because we're listening
                                // to a global event, so we don't want the event to be triggered unless absolutely necessary. In this case,
                                // we only need to fix the value when we have a pending change to check.
                                me.bindNotifyListener = Ext.on('beforebindnotify', me.onBeforeNotify, me, {destroyable: true});
                            }
                            task.delay(me.checkChangeBuffer);
                        }
                    }
                    // editable:false //不支持编辑 去除模糊查询
                });
            } else if (field.itemtype == 'N') {
                var formatpattern = "00000000000000000000000000000";
                var format = formatpattern.substr(0, field.itemlength);
                if (field.decimalwidth > 0) {
                    format += "." + formatpattern.substr(0, field.decimalwidth);
                }
                var limit = format.replace(/0/g, 9);
                items = Ext.create('Ext.form.field.Number', {
                    name: field.itemid,
                    maxValue: limit,
                    minValue: -limit,
                    decimalPrecision: field.decimalwidth,
                    hideTrigger: true,//隐藏上下箭头
                    width: width,
                    height:height,
                    mouseWheelEnabled:false
                });
            } else if (field.itemtype == 'D') {
                var format = 'Y-m-d';
                if (field.itemlength == 4) {
                    format = 'Y';
                } else if (field.itemlength == 7) {
                    format = 'Y-m';
                } else if (field.itemlength == 10) {
                    format = 'Y-m-d';
                } else if (field.itemlength == 16) {
                    format = 'Y-m-d H:i';
                } else if (field.itemlength == 18) {
                    format = 'Y-m-d H:i:s';
                }
                items = Ext.create('EHR.extWidget.field.DateTimeField', {
                    name: field.itemid,
                    format: format,
                    width: width,
                    height:height
                });
            } else if (field.itemtype == 'M') {
                items = Ext.create('Ext.form.field.TextArea', {
                    name: field.itemid,
                    width: width,
                    height:60
                });
            }
            return items;
        },
        /**
         * 创建竞聘范围容器
         */
        createCompetitiveScopeContainer: function () {
            var competitiveScopeContainer = Ext.create('Ext.container.Container', {
                    width: '100%',
                    margin: '10 55 0 55',
                    items: [
                        AddPositions.createTitlePanel(tm.contendPos.competitiveScope, "competitiveScope"),
                        {
                            xtype: 'container',
                            itemId: 'competitiveScopeContainer',
                            margin: '5 0 0 0',
                            items: [{
                                xtype: 'image',
                                height: 48,
                                width: 48,
                                itemId: 'addCompetitiveScopeIcon',
                                style: 'float:left;cursor:pointer',
                                src: '../images/addPerson.png',
                                listeners: {
                                    element: 'el',
                                    click: function () {
                                        var map = new HashMap();
                                        map.put('codesetidstr', "UN,UM");
                                        map.put('codesource', '');
                                        map.put('nmodule', '4');
                                        map.put('ctrltype', '3');
                                        map.put('parentid', '');
                                        map.put('searchtext', encodeURI(""));
                                        map.put('multiple', true);
                                        map.put('isencrypt', true);
                                        map.put('confirmtype', '1');
                                        map.put('callbackfunc', AddPositions.getOrgList);
                                        Ext.require('EHR.orgTreePicker.OrgTreePicker', function () {
                                            Ext.create('EHR.orgTreePicker.OrgTreePicker', {map: map});
                                        }, AddPositions);
                                    }
                                }
                            }]
                        }
                    ]
                }
            );
            AddPositions.add(competitiveScopeContainer);
        },
        createCompetitiveScopeItem: function (orgId, orgDesc) {
            var competitiveScopeContainer = AddPositions.query("#competitiveScopeContainer")[0];
            var item = Ext.create('Ext.container.Container', {
                style: 'float:left',
                width:60,
                items: [
                    {
                        xtype: 'image',
                        width: 50,
                        height: 50,
                        src: '../images/jigou.png',
                        id: orgId,
                        listeners: {
                            render: function () {
                                Ext.create("Ext.tip.ToolTip", {
                                    target: orgId,
                                    minWidth: 100,
                                    trackMouse: true,//跟随鼠标移动
                                    html: orgDesc
                                });
                            }
                        }

                    }, {
                        xtype: 'component',
                        maxWidth: 54,
                        style: 'overflow:hidden;text-overflow:ellipsis;white-space:nowrap;',
                        html: '<span style="font-size: 12px">' + orgDesc + '</span>'
                    }, {
                        xtype: 'image',
                        itemId: 'competitiveScopeDelIcon',
                        orgId: orgId,
                        src: '/components/homewidget/images/del.png',
                        style: 'visibility:hidden;position:absolute;cursor:pointer;margin:-74px 0 0 41px',
                        listeners: {
                            element: 'el',
                            click: function () {
                                var delIcon = competitiveScopeContainer.query("#competitiveScopeDelIcon")[0];
                                competitiveScopeContainer.remove(item);
                                Ext.Array.remove(AddPositions.competitiveScopeArr, delIcon.orgId);
                            }
                        }
                    }
                ],
                listeners: {
                    element: 'el',
                    mouseover: function () {
                        var delIcon = item.query("#competitiveScopeDelIcon")[0];
                        delIcon.setStyle({
                            visibility: 'visible'
                        });
                    },
                    mouseout: function () {
                        var delIcon = item.query("#competitiveScopeDelIcon")[0];
                        delIcon.setStyle({
                            visibility: 'hidden'
                        });
                    }
                }
            });

            return item;
        },
        getOrgList: function (data) {
            var competitiveScopeContainer = AddPositions.query("#competitiveScopeContainer")[0];
            for (var i = 0; i < data.length; i++) {
                if (Ext.Array.indexOf(AddPositions.competitiveScopeArr, data[i].id) == -1) {
                    var item = AddPositions.createCompetitiveScopeItem(data[i].id, data[i].text);
                    competitiveScopeContainer.insert(competitiveScopeContainer.items.length - 1, item);
                    AddPositions.competitiveScopeArr.push(data[i].id);
                }
            }
        },
        /**
         * 创建存放面试官的顶部panel
         * @param interviewers 面试官信息
         */
        createUpPanel: function (interviewers) {
            var me = this;
            var upPanel = Ext.create("Ext.panel.Panel", {
                margin: '10 55 0 55',
                width: '100%',
                border: false,
                items: [me.createTitlePanel(tm.contendPsn.interviewer, "upContentPanel"),//面试官
                    {
                        xtype: 'panel',
                        border: false,
                        itemId: 'upContentPanel'
                    }
                ]
            });
            me.add(upPanel);
            var upContentPanel = upPanel.query("#upContentPanel")[0];
            //添加面试官按钮
            var addPersonIcon = Ext.create("Ext.Img", {
                height: 48,
                width: 48,
                itemId: 'addPersonIcon',
                style: 'float:left;cursor:pointer',
                src: '../images/addPerson.png',
                listeners: {
                    element: 'el',
                    click: function () {
                        if (me.orgIdStr == "no") {
                            Ext.Msg.alert(tm.tip, tm.contendPsn.noPsnSelect);//'您权限范围内没有可选择的人员！';
                        } else {
                            me.createPersonPicker();
                        }
                    }
                }
            });
            upContentPanel.add(addPersonIcon);
        },
        /**
         * 创建遮罩
         * @param msg
         * @param target
         * @returns {*|void}
         */
        createMask: function (msg, target) {
            var myMask = new Ext.LoadMask({
                msg: msg,
                target: target
            }).show();
            return myMask;
        },
        /**
         * 创建选人控件
         */
        createPersonPicker: function () {
            var me = this;
            var upContentPanel = me.query("#upContentPanel")[0];
            var picker = new PersonPicker({
                multiple: true,
                orgid: me.orgIdStr,//组织机构业务范围
                isPrivExpression: false,//不控制人员范围
                deprecate: AddPositions.deprecateArr,
                callback: function (people) {
                    if (people.length > 0) {
                        //创建遮罩  '数据加载中，请稍候...';
                        me.customMyMask = me.createMask(tm.contendPos.loadDataWaiting, competitionJobs);
                        setTimeout(function () {
                            for (var i = 0; i < people.length; i++) {
                                var person = people[i];
                                var nbaseA0100 = person.id;
                                var guidkey = person.guidkey;
                                var name = person.name;//姓名
                                var photoPath = person.photo;//照片路径
                                var email = person.email ? person.email : "";//邮箱
                                var phoneNumber = person.c0104 ? person.c0104 : "";//电话

                                var suffix = 0;//id后缀
                                if (upContentPanel.items.length > 1) {//存在面试官，取到最后一个面试官的组件id后缀+1
                                    var lastPsnId = upContentPanel.items.items[upContentPanel.items.length - 2].id;
                                    suffix = parseInt(lastPsnId.split("_")[1]) + 1;
                                }
                                me.deprecateArr.push(nbaseA0100);//已选择的不再显示
                                if (Ext.Array.indexOf(me.interviewPersonArr, guidkey) == -1) {
                                    me.interviewPersonArr.push(guidkey);
                                }
                                var interviewerPanel = me.getInterviewerPanel(suffix, photoPath, name, nbaseA0100, guidkey);
                                var insertIndex = upContentPanel.items.length - 1;
                                upContentPanel.insert(insertIndex, interviewerPanel);

                                me.createToolTip(suffix, phoneNumber, email);
                            }
                            if (me.customMyMask) {
                                me.customMyMask.destroy();
                            }
                            me.query("#addPersonIcon")[0].setMargin("15 0 0 0");
                        }, 1);
                    }
                }
            }, Ext.getBody());
            picker.open();
        },
        /**
         * 获得一个面试官panel
         * @param suffix id后缀
         * @param interviewerPhotoPath 头像路径
         * @param interviewerName 面试官姓名
         * @param nbaseA0100
         * @param guidkey
         */
        getInterviewerPanel: function (suffix, interviewerPhotoPath, interviewerName, nbaseA0100, guidkey) {
            var me = this;
            var upContentPanel = me.query("#upContentPanel")[0];
            var interviewerPanel = Ext.create("Ext.Panel", {
                id: 'psn_' + suffix,//tooltip的目标
                border: false,
                style: 'float:left',
                width: 60,
                items: [{
                    xtype: 'image',
                    width: 48,
                    height: 48,
                    style:'border-radius:50%',
                    margin: '8 0 0 0',
                    src: interviewerPhotoPath
                }, {
                    xtype: 'panel',
                    width: 48,
                    border: false,
                    layout: {
                        type: 'vbox',
                        align: 'center'
                    },
                    items: [{
                        xtype: 'component',
                        maxWidth: 54,
                        style: 'overflow:hidden;text-overflow:ellipsis;white-space:nowrap;',
                        html: '<span style="font-size: 14px">' + interviewerName + '</span>'
                    }]
                }, {
                    xtype: 'image',
                    itemId: 'delIcon',
                    psnFlag: nbaseA0100,//删除面试官时用
                    psnGuidFlag: guidkey,//删除面试官时用
                    src: '/components/homewidget/images/del.png',
                    style: 'visibility:hidden;position:absolute;cursor:pointer;margin:-72px 0 0 33px',
                    listeners: {
                        element: 'el',
                        click: function () {
                            var delIcon = interviewerPanel.query("#delIcon")[0];
                            upContentPanel.remove(interviewerPanel);
                            Ext.Array.remove(me.deprecateArr, delIcon.psnFlag);
                            Ext.Array.remove(me.interviewPersonArr, delIcon.psnGuidFlag);
                            if (me.deprecateArr.length == 0) {
                                me.query("#addPersonIcon")[0].setMargin("0 0 0 0");
                            }

                        }
                    }
                }],
                listeners: {
                    element: 'el',
                    mouseover: function () {
                        var delIcon = interviewerPanel.query("#delIcon")[0];
                        delIcon.setStyle({
                            visibility: 'visible'
                        });
                    },
                    mouseout: function () {
                        var delIcon = interviewerPanel.query("#delIcon")[0];
                        delIcon.setStyle({
                            visibility: 'hidden'
                        });
                    }
                }
            });
            return interviewerPanel;
        },
        /**
         * 添加工具栏按钮
         */
        addTools: function () {
            var me = this;
            me.tools = [{
                type: 'close',
                handler: function () {
                    competitionJobs.remove(me);
                    me.destroy();
                    Ext.getCmp("competitionJobsTable_mainPanel").show();
                    competitionJobs.tableObj.dataStore.reload();
                }
            }];
        },
        /**
         * 创建titlepanel
         * @param title 标题
         * @param position 哪一块panel
         * @returns {Ext.Panel}
         */
        createTitlePanel: function (title, type) {
            var me = this;
            var titlePanel = Ext.create("Ext.Panel", {
                height: 35,
                border: false,
                width: '100%',
                layout: {
                    type: 'hbox',
                    align: 'center'
                },
                bodyStyle: 'backgroundColor:#F0F0F0',
                items: [{
                    xtype: 'image',
                    itemId: 'downIcon',
                    height: 16,
                    width: 16,
                    margin:'0 0 0 5',
                    style: 'margin-right:10px;cursor:pointer',
                    src: '../images/shrink.png',
                    listeners: {
                        element: 'el',
                        click: function () {
                            var upContentPanel = me.query("#upContentPanel")[0];
                            var basicInformationForm = me.query("#basicInformationForm")[0];
                            var competitiveScopeContainer = me.query('#competitiveScopeContainer')[0];
                            titlePanel.query("#downIcon")[0].hide();
                            titlePanel.query("#upIcon")[0].show();
                            if (type == "upContentPanel") {//面试官容器
                                upContentPanel.hide();
                            } else if (type == 'basicInformation') {//基本信息容器
                                basicInformationForm.hide();
                            } else if (type == 'competitiveScope') {
                                competitiveScopeContainer.hide();
                            }


                        }
                    }
                }, {
                    xtype: 'image',
                    itemId: 'upIcon',
                    hidden: true,
                    height: 16,
                    width: 16,
                    margin:'0 0 0 5',
                    style: 'margin-right:10px;cursor:pointer',
                    src: '../images/expand.png',
                    listeners: {
                        element: 'el',
                        click: function () {
                            var upContentPanel = me.query("#upContentPanel")[0];
                            var basicInformationForm = me.query("#basicInformationForm")[0];
                            var competitiveScopeContainer = me.query('#competitiveScopeContainer')[0];
                            titlePanel.query("#downIcon")[0].show();
                            titlePanel.query("#upIcon")[0].hide();
                            if (type == "upContentPanel") {
                                upContentPanel.show();
                            } else if (type == 'basicInformation') {//基本信息容器
                                basicInformationForm.show();
                            } else if (type == 'competitiveScope') {
                                competitiveScopeContainer.show();
                            }
                        }
                    }

                }, {
                    xtype: 'component',
                    margin: '0 0 0 5',
                    html: '<span style="font-size: 14px">' + title + '</span>'
                }]
            });
            return titlePanel;
        },
        /**
         * 创建tooltip组件
         * @param suffix target后缀
         * @param interviewerPhone 电话
         * @param interviewerEmail 邮箱
         */
        createToolTip: function (suffix, interviewerPhone, interviewerEmail) {
            Ext.create("Ext.tip.ToolTip", {
                target: 'psn_' + suffix,
                minWidth: 200,
                trackMouse: true,//跟随鼠标移动
                html: tm.contendPsn.phone + interviewerPhone + '<br>' + tm.contendPsn.email + interviewerEmail
            });
        }
    }
);