/**
 * 创建方案window
 * wangbs 2019年5月27日
 */
Ext.define('GenaratedataURL.MatchScheme', {
    requires: ["SYSF.FileUpLoad"],
    extend: 'Ext.window.Window',
    layout: 'fit',
    width: 850,
    height: 505,
    modal: true,//遮罩
    resizable: false,//禁止拉伸
    closable: true,//允许关闭按钮
    title: dr.gd.matchscheme,//创建方案
    constructor: function () {
        this.callParent();
        MatchScheme = this;
        this.init();
    },
    init: function () {
        var me = this;
        //默认选单个机构
        me.selectedOrgKind = "1";
        me.nullItem = [dr.gd.pleaseselect, dr.gd.pleaseselect];//请选择...=空项
        var mainPanel = this.getMainPanel();
        me.add(mainPanel);
        me.echoFirstData();
    },
    /**
     * 回显第一页数据
     */
    echoFirstData: function () {
        var me = this;
        me.firstEchoData = me.searchFirstData();//多页面公用信息放到类变量中
        //填充fieldset
        var count = 0;
        for (var item in me.firstEchoData) {
            count++;
        }
        if (count > 0) {
            me.fillBasicInfo();
            //对应本地机构combo赋值
            var locOrgCode = me.firstEchoData.locorgcode;
            if (locOrgCode) {
                me.bs_asyn_param_c.locorgcode = locOrgCode;
                me.selectedOrgKind = locOrgCode.split("`")[0];
                //回显radiogroup的值
                me.query("radiogroup")[0].setValue({orgKind:me.selectedOrgKind});

                var orgCode = locOrgCode.split("`")[1];
                me.selectedOrg = orgCode.split(",")[0];
                var locOrgDesc = me.firstEchoData.locorgdesc;
                var targetCombo = {};
                if (me.selectedOrgKind == '1') {
                    targetCombo = me.query("#single_selectOrg")[0];
                }else{
                    targetCombo = me.query("#multi_selectOrg")[0];
                }
                targetCombo.setValue(orgCode + "`" + locOrgDesc);
            }
        }
    },
    /**
     * 创建主体panel
     * @returns {Ext.Panel}
     */
    getMainPanel: function () {
        var me = this;
        me.borderFlag = true;//有gridpanel时控制外部边框是否显示 防止gridpanel边框和容器边框重合变丑
        me.bs_asyn_param_c = {};//存入常量表中constant='BS_ASYN_PARAM_C'的数据

        var mainPanel = Ext.create("Ext.Panel", {
            itemId: "mainPanel",
            border: false
        });

        var stepviewPanel = Ext.create("Ext.Panel", {
            border: false,
            itemId: 'steppanel',
            style: 'border-bottom:solid 1px #c5c5c5',
            items: [me.createStepview(0)]//初始化在第一步
        });

        //第一步的panel
        var firstPanel = me.createFirstPanel();

        mainPanel.add(stepviewPanel);
        mainPanel.add(firstPanel);
        return mainPanel;
    },
    /**
     * 创建stepview组件
     * @param index 当前在第几步
     * @returns {*}
     */
    createStepview: function (index) {
        var stepview = Ext.widget("stepview", {
            currentIndex: index,
            freeModel: false,
            stepData: [{name: dr.gd.importscheme}, {name: dr.gd.reportset}, {name: dr.gd.matchesfield}
                , {name: dr.gd.matchescode}, {name: dr.gd.reportnbase}, {name: dr.gd.checkconditions}]
        });
        return stepview;
    },
    /**
     * 查询第一步数据回显
     * @returns {string}
     */
    searchFirstData: function () {
        var schemeInfo = "";
        var map = new HashMap();
        map.put("operaType", "searchFirstData");
        Rpc({
            functionId: 'SYS0000003102', async: false, success: function (res) {
                var result = Ext.decode(res.responseText);
                schemeInfo = result.schemeInfo;
                MatchScheme.saveFilePath = result.filePath;
                Ext.getCmp("savePath").setValue(result.filePath);
            }
        }, map);
        return schemeInfo;
    },
    /**
     * 创建第一步对应的panel
     * @returns {Ext.Panel}
     */
    createFirstPanel: function () {
        var me = this;
        var fileUpload = me.createFileUpload();//上传附件组件
        var orgRadioPanel = me.createOrgRadioPanel();//选择单个机构或者多个机构
        var singleOrgCombobox = me.createOrgCombobox("single");//机构单选对应combobox
        var multiOrgCombobox = me.createOrgCombobox("multi");//机构多选对应combobox
        var firstPanel = me.createCommonPanel(1, "", "firstPanel", "secondPanel");

        //文件存储路径
        firstPanel.add({
            xtype: 'textfield',
            id: 'savePath',
            margin: '10 0 0 163',
            width: 395,
            height: 22,
            fieldLabel: saveurl,
            labelAlign: 'right',
            labelWidth: 90,
            allowBlank: false,
            listeners:{
                blur: function () {
                    this.setValue(Ext.String.trim(this.getValue()));
                    var savePath = this.getValue();
                    if (savePath && MatchScheme.saveFilePath != savePath) {
                        var map = new HashMap();
                        map.put("operaType", "saveFilePath");
                        map.put("savePath", savePath);
                        Rpc({functionId: 'SYS0000003102', success: function (res) {
                            var resultData = Ext.decode(res.responseText);
                            if (resultData.saveFilePathMsg) {
                                MatchScheme.saveFilePath = "checkPathExistError";
                                Ext.Msg.alert(setmatch_tips, eval(resultData.saveFilePathMsg));
                            }else{
                                MatchScheme.saveFilePath = savePath;
                            }
                        }}, map);
                    }
                }
            }
        });
        firstPanel.add({
            xtype: 'panel',
            border: false,
            margin: '5 0 5 0',
            layout: 'hbox',
            items: [{
                xtype: 'component',//上传接收方案
                margin: '10 5 0 205',
                html: dr.gd.receivescheme
            }, fileUpload]
        });
        firstPanel.add({
            xtype: 'fieldset',//方案基本信息
            height: 180,
            width: 300,
            title: dr.gd.schemebasicinfo,
            style: 'margin-left:258px;'
        });
        firstPanel.add(orgRadioPanel);
        firstPanel.add(singleOrgCombobox);
        firstPanel.add(multiOrgCombobox);
        return firstPanel;
    },
    /**
     * 创建上传文件FileUpLoad组件
     * @returns {SYSF.FileUpLoad}
     */
    createFileUpload: function () {
        var me = this;
        me.dataRendering = false;//渲染页面数据中
        var fileUpload = Ext.create("SYSF.FileUpLoad", {//创建上传附件组件
            upLoadType: 1,//单文件上传
            fileExt: "*.zip",
            readInputWidth: 300,
            buttonText: dr.gd.fileselect,//选择
            isTempFile: true,
            VfsFiletype:VfsFiletypeEnum.doc,
            VfsModules:VfsModulesEnum.TB,
            VfsCategory: VfsCategoryEnum.other,
            CategoryGuidKey: '',
            success: function (list) {
                if (list.length != 0) {
                    me.dataRendering = true;
                    var map = new HashMap();
                    map.put("operaType", "receiveScheme");
                    map.put("file", list[0]);
                    Rpc({
                        functionId: 'SYS0000003102', success: function (res) {
                            var returnData = Ext.decode(res.responseText);
                            var filelist = returnData.filelist;
                            var return_code = filelist[0];
                            if (return_code.indexOf("success") > -1) {
                                me.fileMap = filelist[1];
                                me.fillBasicInfo();
                                var filePath = return_code.split("`")[1];
                                me.saveSchemeInfo(filePath);
                                me.dataRendering = false;
                            } else {
                                me.dataRendering = false;
                                if (return_code == "fileerror") {
                                    Ext.Msg.alert(dr.gd.tip, dr.gd.uploadcorrectfile);//请上传符合规范的文件！
                                } else if (return_code == "ioerror") {
                                    Ext.Msg.alert(dr.gd.tip, dr.gd.readfileerror);//读取文件出错！
                                } else if (return_code == "pathBlank") {
                                    Ext.Msg.alert(dr.gd.tip, dr.gd.pathBlank);//请先填写文件存储路径！
                                }
                            }
                        }
                    }, map);
                }
            },
            //回调方法，失败
            error: function () {
                Ext.Msg.alert(dr.gd.tip, dr.gd.uploaderrormsg);// 文件上传失败！
            }
        });
        return fileUpload;
    },
    /**
     * 保存方案信息
     */
    saveSchemeInfo: function (filePath) {
        var me = this;
        //上报方式
        var import_type = me.bs_asyn_param_c.import_type;
        if (import_type == '0') {//手工上报
            delete me.bs_asyn_param_c.middb;
            delete me.bs_asyn_param_c.ftp;
            delete me.bs_asyn_param_c.wsdl;
        }else if (import_type == '1') {//中间库
            delete me.bs_asyn_param_c.ftp;
            delete me.bs_asyn_param_c.wsdl;
        }else if (import_type == '2') {//FTP
            delete me.bs_asyn_param_c.middb;
            delete me.bs_asyn_param_c.wsdl;
        }else if (import_type == '3') {//Webservice
            delete me.bs_asyn_param_c.middb;
            delete me.bs_asyn_param_c.ftp;
        }

        var map = new HashMap();
        map.put("operaType", "saveSchemeInfo");
        map.put("schemeInfo", me.bs_asyn_param_c);
        map.put("filePath", filePath);
        Rpc({functionId: 'SYS0000003102', success: function (res) {
        }}, map);
    },
    /**
     * 创建选择单个or多个机构
     * @returns {Ext.Panel}
     */
    createOrgRadioPanel: function () {
        var me = this;
        var orgCheckPanel =Ext.create("Ext.Panel", {
            border: false,
            layout: 'hbox',
            items:[{
                xtype: 'component',
                margin: '2 5 0 181',
                html: dr.gd.locationorg// 本地对应机构
            }, {
                xtype: 'radiogroup',//单选组件
                items: [{
                    boxLabel: dr.gd.singleorg,
                    name: 'orgKind',
                    inputValue: '1',
                    checked: me.selectedOrgKind == "1" ? true : false
                }, {
                    boxLabel: dr.gd.multiorg,
                    name: 'orgKind',
                    width: 100,
                    margin: '0 0 0 10',
                    inputValue: '2',
                    checked: me.selectedOrgKind == "2" ? true : false
                }],
                listeners: {
                    change: function (owner, newValue) {
                        var selectOrgValue = "";
                        me.selectedOrgKind = newValue.orgKind;
                        if (me.selectedOrgKind == "1") {//单个机构
                            me.query("#single_selectOrg")[0].show();
                            me.query("#multi_selectOrg")[0].hide();
                            selectOrgValue = me.query("#single_selectOrg")[0].getValue();
                        } else {//多个机构
                            me.query("#single_selectOrg")[0].hide();
                            me.query("#multi_selectOrg")[0].show();
                            selectOrgValue = me.query("#multi_selectOrg")[0].getValue();
                        }
                        if (selectOrgValue) {
                            me.bs_asyn_param_c.locorgcode = me.selectedOrgKind + "`" + selectOrgValue.split("`")[0];
                            me.saveSchemeInfo();
                        }
                    }
                }
            }]
        });
        return orgCheckPanel;
    },
    /**
     * 创建本地对应机构Combobox
     * @returns {*}
     */
    createOrgCombobox: function (orgKind) {
        var me = this;
        var codecomboxfield = {
            xtype: 'codecomboxfield',
            width: 300,
            itemId: orgKind + '_selectOrg',
            margin: '5 0 0 258',
            codesetid: 'UN',
            nmodule: "4",//组织机构
            ctrltype: "3",
            hidden: orgKind == "single" ? false : true
        };
        if (orgKind == "single") {
            codecomboxfield.afterCodeSelectFn = function (a, value) {
                me.selectedOrg = value;
                me.bs_asyn_param_c.locorgcode = "1`" + value;
                me.saveSchemeInfo();
            }
        }else{
            codecomboxfield.multiple = true;
            codecomboxfield.listeners = {
                multiplefinish: function (values) {
                    var checkFlag = me.checkSelectOrgRules(values);
                    if (checkFlag) {//校验通过
                        var locorgcode = "";
                        for (var i = 0; i < values.length; i++) {
                            var orgId = values[i].id;
                            if (i == 0) {
                                me.selectedOrg = orgId;
                            }
                            locorgcode = locorgcode + "," + orgId;
                        }
                        if (locorgcode) {
                            locorgcode = locorgcode.substring(1);
                            me.bs_asyn_param_c.locorgcode = "2`" + locorgcode;
                            me.saveSchemeInfo();
                        }
                    }else{
                        //请选择同一单位下的同级机构
                        Ext.Msg.alert(dr.gd.tip, dr.gd.sameunsameorg);
                    }
                }
            };
        }
        return codecomboxfield;
    },
    /**
     * 校验选择的单位是否符合规则
     * @param values 选择的数据
     */
    checkSelectOrgRules: function (values) {
        var me = this;
        var targetCombo = me.query("#multi_selectOrg")[0];
        if (values.length > 1) {
            var parentId = "";
            for (var i = 0; i < values.length; i++) {
                if (!parentId) {
                    parentId = values[i].parentId;
                }else{
                    if (parentId != values[i].parentId) {
                        targetCombo.setValue("");
                        return false;
                    }
                }
            }
        }
        return true;
    },
    /**
     * 填充第一步骤的基本信息fieldset
     */
    fillBasicInfo: function () {
        var me = this;
        if (me.fileMap) {//me.fileMap存在说明是导入方案
            for (var key in me.fileMap) {
                if (key == "menus" || key == "codeitems") {
                    continue;
                }
                var basicInfo = Ext.decode(me.fileMap[key]);
                me.addBasicInfoComponent(basicInfo);
            }
            var menusInfo = Ext.decode(me.fileMap.menus);
            me.bs_asyn_param_c.report_photo = menusInfo.photo;
        } else {//回显
            me.addBasicInfoComponent(me.firstEchoData, true);
        }
    },
    /**
     * 填充基本信息component
     * @param basicInfo
     * @param echoFlag 是否是回显
     */
    addBasicInfoComponent: function (basicInfo, echoFlag) {
        if (!basicInfo) {//无信息return
            return;
        }
        var me = this;
        var firstPanel = me.query("#firstPanel")[0];
        var basicInfoPanel = firstPanel.query("fieldset")[0];
        //重置数据
        basicInfoPanel.removeAll(true, true);

        me.bs_asyn_param_c.pkgtime = basicInfo.pkgtime;
        me.bs_asyn_param_c.pkgtype = basicInfo.pkgtype;
        me.bs_asyn_param_c.import_type = basicInfo.import_type;

        if(basicInfo.middb) {
            me.bs_asyn_param_c.middb = basicInfo.middb;
        }
        if(basicInfo.ftp) {
            me.bs_asyn_param_c.ftp = basicInfo.ftp;
        }
        if(basicInfo.wsdl) {
            me.bs_asyn_param_c.wsdl = basicInfo.wsdl;
        }

        if (echoFlag) {//回显与选择导入方案的数据格式不一致，需处理
            if (basicInfo.superorg) {
                var schemeUnitCode = basicInfo.superorg.unitcode;
                var schemeUnitName = basicInfo.superorg.unitname;
            }
            me.bs_asyn_param_c.superorg = basicInfo.superorg;
            me.bs_asyn_param_c.report_photo = basicInfo.report_photo;
        } else {
            var schemeUnitCode = basicInfo.unitcode;
            var schemeUnitName = basicInfo.unitname;
            var superorg = {};
            superorg.unitcode = schemeUnitCode;
            superorg.unitname = schemeUnitName;
            superorg.unitguid = basicInfo.unitguid;
            me.bs_asyn_param_c.superorg = superorg;
        }

        if (me.bs_asyn_param_c.superorg) {
            me.addComponent(basicInfoPanel, dr.gd.unitcode, schemeUnitCode);
            me.addComponent(basicInfoPanel, dr.gd.unitname, schemeUnitName);
            me.addComponent(basicInfoPanel, dr.gd.pkgtime, basicInfo.pkgtime);
            var pkgtype = dr.gd.fulldose;//默认全量
            if (basicInfo.pkgtype == "2") {
                pkgtype = dr.gd.increment;
            }
            me.addComponent(basicInfoPanel, dr.gd.pkgtype, pkgtype);

            var importType = dr.gd.manual;//默认手工上报
            if (basicInfo.import_type == "1") {
                importType = dr.gd.database;
            } else if (basicInfo.import_type == "2") {
                importType = dr.gd.ftp;
            } else if (basicInfo.import_type == "3") {
                importType = dr.gd.webservice;
            }
            me.addComponent(basicInfoPanel, dr.gd.importtype, importType);
        }
    },
    /**
     * 添加方案基本信息
     * @param basicInfoPanel
     * @param label 字段
     * @param labelValue 字段值
     */
    addComponent: function (basicInfoPanel, label, labelValue) {
        var panel = Ext.create("Ext.Panel", {
            border: false,
            layout: 'hbox',
            margin: '5 0 0 -5',
            items: [{
                xtype: 'panel',
                width: 90,
                border: false,
                style: 'text-align:right',
                items:[{
                    xtype: 'component',
                    html: label
                }]
            },{
                xtype: 'component',
                html: labelValue
            }]
        });
        basicInfoPanel.add(panel);
    },
    /**
     * 获取gridpanel中的所有记录
     * @param itemId bodyPanel的itemId
     * @returns {*}
     */
    getGridAllStore: function (itemId) {
        var me = this;
        var currentBodyPanel = me.query("#" + itemId)[0];
        var currentGrid = currentBodyPanel.query("gridpanel")[0];
        var currentGridStore = currentGrid.getStore();
        return currentGridStore;
    },
    /**
     * 自动对应子集
     * @param fieldSetList 所有信息集
     */
    autoMatchSet: function (fieldSetList) {
        var me = this;
        var currentGridStore = me.getGridAllStore("secondPanel");
        var currentGridData = currentGridStore.data.items;

        for (var i = 0; i < currentGridData.length; i++) {
            var currentStore = currentGridData[i];
            var oneData = currentStore.data;
            var setId = oneData.parentSetId;//本条记录的上级setid
            var setName = oneData.parentSetName;//本条记录的上级setname

            for (var j = 0; j < fieldSetList.length; j++) {
                var localSetInfo = fieldSetList[j][0];//所有信息中的一条数据 如 A01:基本信息
                var setInfoArr = localSetInfo.split(":");
                var setInfoId = setInfoArr[0];
                var setInfoName = setInfoArr[1];

                //主集A01匹配 || 子集名称匹配
                if (setId == "A01" && setInfoId == setId || setId != "A01" && setInfoName == setName) {
                    oneData.matchSet = localSetInfo;
                    break;
                }else {//自动对应全部重新对应，清掉原先的对应数据
                    if (oneData.matchSet) {
                        oneData.matchSet = "";
                    }
                }
            }
        }
        var matchSetArr = [];
        for (var i = 0; i < currentGridData.length; i++) {
            matchSetArr.push(currentGridData[i].data);
        }
        me.saveMatchSet(matchSetArr);
        //刷新数据
        var mainPanel = me.query("#mainPanel")[0];
        mainPanel.query("#secondPanel")[0].destroy();
        mainPanel.add(me.createSecondPanel());
    },
    /**
     * 保存信息集对应关系
     * @param matchSetArr
     */
    saveMatchSet: function (matchSetArr) {
        var me = this;
        var tempMatchSetArr = [];
        for (var i = 0; i < matchSetArr.length; i++) {
            var tempInfo = {};
            var oneData = matchSetArr[i];
            for (var key in oneData) {
                if (key == "filterIcon") {
                    continue;
                }
                tempInfo[key] = oneData[key];
            }
            tempMatchSetArr.push(tempInfo);
        }
        var map = new HashMap();
        map.put("operaType", "saveMatchSet");
        map.put("matchSetArr", tempMatchSetArr);
        Rpc({functionId: 'SYS0000003102', async: false, success: function () {
            if(me.myMask) {
                me.myMask.destroy();
            }
            clearTimeout(me.setTimeOut);
        }}, map);
    },
    /**
     * 创建第二步对应的panel
     * @returns {Ext.Panel}
     */
    createSecondPanel: function () {
        var me = this;
        var returnMap = me.searchSecondData();
        var setMatchList = returnMap.setMatchList;//上下级信息集的对应关系
        me.menusJsonStr = returnMap.menusInfo;
        me.menusContent = Ext.decode(returnMap.menusInfo);//页面共用的menus.json内容

        for (var i = 0; i < setMatchList.length; i++) {//当新方案比旧方案多时，更新新方案中的每个信息集的matchset
            var setId = setMatchList[i].set_id;
            for (var j = 0; j < me.menusContent.set_list.length; j++) {
                var menusSetId = me.menusContent.set_list[j].set_id;
                if (menusSetId == setId) {
                    me.menusContent.set_list[j].matchSet = setMatchList[i].matchSet;
                    me.menusContent.set_list[j].filterIcon = setMatchList[i].filterIcon;
                    break;
                }
            }
        }

        me.borderFlag = false;
        var fieldSetList = returnMap.fieldSetList;//信息集的所有指标
        fieldSetList.splice(0, 0, me.nullItem);

        var secondPanel = me.createCommonPanel(2, "firstPanel", "secondPanel", "thirdPanel");
        var tbar = [{
            xtype: 'button',
            text: dr.gd.automatch,//自动对应
            handler: function () {
                me.myMask = me.createMask(dr.gd.saveingcodematch, gridPanel);// = "正在保存数据,请不要进行其他操作！"
                me.setTimeOut = setTimeout(function () {
                    me.autoMatchSet(fieldSetList);
                }, 100);
            }, scope: me
        }];
        var gridStore = new Ext.data.ArrayStore({
            fields: ["parentSetId", "parentSetName", "matchSet", "filterIcon"]
        });
        var matchSetStore = new Ext.data.ArrayStore({
            fields: ['id', 'displayText'],
            data: fieldSetList
        });
        var matchSetCombo = Ext.widget("combobox", {
            store: matchSetStore,
            valueField: 'id',
            displayField: 'displayText',
            editable: false,
            listeners: {
                change: function (owner, newValue) {
                    var currentGrid = secondPanel.query("gridpanel")[0];
                    var recordData = me.getSelectedData(currentGrid);
                    var selectedSetId = recordData.parentSetId;//上级setid

                    if (newValue == dr.gd.pleaseselect) {//请选择
                        owner.setValue("");
                        return;
                    }
                    //删除因更改匹配信息集造成的指标、代码对应表中的脏数据
                    var map = new HashMap();
                    map.put("operaType", "deleteSetAssociatedData");
                    map.put("set1", selectedSetId);
                    Rpc({functionId: 'SYS0000003102', success: function (res) {
                    }}, map);

                    if (selectedSetId == "A01") {//主集必须对应主集
                        if (newValue && newValue.indexOf(selectedSetId) == -1) {
                            Ext.Msg.alert(dr.gd.tip, dr.gd.mainmatchmain);//主集必须对应主集
                            owner.setValue("");
                            return;
                        }
                    }

                    //数据全部保存 防止中途退出后再进来数据紊乱
                    var matchSetArr = [];
                    var currentGridStore = me.getGridAllStore("secondPanel");
                    var currentGridData = currentGridStore.data.items;
                    for (var i = 0; i < currentGridData.length; i++) {
                        var oneRecord = currentGridData[i].data;
                        var parentSetId = oneRecord.parentSetId;

                        //store中的数据此时还没有赋新值 所以需要手动修改
                        if (selectedSetId == parentSetId) {
                            oneRecord.matchSet = newValue ? newValue : "";
                        }
                        matchSetArr.push(oneRecord);
                    }
                    me.myMask = me.createMask(dr.gd.saveingcodematch, gridPanel);// = "正在保存数据,请不要进行其他操作！"
                    me.setTimeOut = setTimeout(function () {
                        me.saveMatchSet(matchSetArr);
                    }, 100);
                }
            }
        });
        var columns = [me.createOneColumn(dr.gd.parentsetid, "parentSetId", 1),//上级子集代码
            me.createOneColumn(dr.gd.parentsetname, "parentSetName", 1.5),//上级子集名称
            me.createOneColumn(dr.gd.matchset, "matchSet", 2, "", matchSetCombo),//对应子集
            me.createOneColumn(dr.gd.filteconditions, "filterIcon", 1)//过滤记录条件
        ];
        var bbar = [{
            xtype: 'checkbox',
            margin: '0 0 0 5',
            readOnly: true,//不让用户修改
            boxLabel: dr.gd.reportphoto,//上报人员照片
            checked: me.menusContent.photo == "1" ? true : false
        }];

        var gridPanel = me.createCommonGrid(tbar, gridStore, columns, bbar);
        //填充数据
        var gridPanelStore = gridPanel.getStore();
        var setList = me.menusContent.set_list;
        for (var i = 0; i < setList.length; i++) {
            var oneSet = setList[i];
            var iconHtml = '<div style="text-align: center">' +
                '<image src="/images/new_module/no_filter.png" ' +
                'onclick="MatchScheme.filteConditionWindow()"/></div>';

            if (oneSet.filterIcon == '1') {//本地子集有过滤条件
                iconHtml = iconHtml.replace("no_filter", "filter");
            }
            gridPanelStore.add({
                parentSetId: oneSet.set_id,
                parentSetName: oneSet.set_name,
                matchSet: oneSet.matchSet ? oneSet.matchSet : "",
                filterIcon: iconHtml
            });
        }

        secondPanel.add(gridPanel);
        return secondPanel;
    },
    /**
     * 过滤条件window
     */
    filteConditionWindow: function () {
        var me = this;
        setTimeout(function () {
            var secondPanel = me.query("#secondPanel")[0];
            var ownerGrid = secondPanel.query("gridpanel")[0];
            var ownerGridStore = ownerGrid.getStore();
            var rowData = ownerGridStore.getData().items[me.filterIndex].data;
            var parentSetId = rowData.parentSetId;//上级子集编码
            var parentSetName = rowData.parentSetName;//上级子集描述

            var matchSet = rowData.matchSet;//匹配的下级子集
            if (!matchSet) {
                Ext.Msg.alert(dr.gd.tip, dr.gd.matchlocalset);//请先对应本地子集！
                return;
            }
            var matchSetArr = matchSet.split(":");
            var fieldSetId = matchSetArr[0];

            var map = new HashMap();
            map.put("type", "open");
            map.put("setid", parentSetId);
            map.put("unitcodeid", me.selectedOrg);
            var currentexpress = "";//已选中的子集的过滤条件
            Rpc({
                functionId: 'SYS0000003022', success: function (res) {
                    var result = Ext.decode(res.responseText);
                    if (result.succeed) {
                        currentexpress = getDecodeStr(result.express);

                        var dataMap = new HashMap();
                        dataMap.put("express", currentexpress);//默认公式
                        dataMap.put("initflag", '1');//只显示指标下拉框
                        Ext.require('EHR.complexcondition.ComplexCondition', function () {
                            var formulaWin = Ext.create("EHR.complexcondition.ComplexCondition", {
                                dataMap: dataMap,
                                imodule: "3",
                                opt: "1",
                                title: dr.gd.setfiltecondition,//设置过滤记录条件
                                callBackfn: function (c_expr) {
                                    var vo = new HashMap();
                                    vo.put("type", "save");
                                    vo.put("c_expr", c_expr);
                                    vo.put("setid", parentSetId);
                                    vo.put("desc", parentSetName);
                                    vo.put("unitcodeid", me.selectedOrg);
                                    Rpc({
                                        functionId: 'SYS0000003022', success: function (res) {
                                            var result = Ext.decode(res.responseText);
                                            if (!result.succeed) {
                                                Ext.Msg.alert(dr.gd.tip, setmatch_save_fail);// 保存失败
                                            }else {
                                                me.changeFilterIcon(c_expr);
                                            }
                                        }
                                    }, vo);
                                }
                            });
                            var fieldSetCombo = Ext.getCmp("fieldItem_id");//过滤条件window中的指标选择下拉框
                            var fieldSetComboStore = fieldSetCombo.getStore();
                            fieldSetComboStore.load({
                                params: {
                                    value: fieldSetId,
                                    imodule: "3",
                                    opt: "1",
                                    flag: '1'
                                },
                                callback: function (record, option, succes) {
                                    fieldSetCombo.setValue("");
                                    Ext.getCmp('codeItem_id').hide();
                                }
                            });
                        });
                    } else {
                        Ext.Msg.alert(dr.gd.tip, dr.gd.calladminister);// "窗口打开失败，请联系管理员！";
                    }
                }
            }, map);
        }, 5);
    },
    /**
     * 变更过滤条件图标区分已设置和未设置
     * @param c_expr
     */
    changeFilterIcon: function (c_expr) {
        var me = this;
        var gridStore = me.getGridAllStore("secondPanel");
        var selectStore =gridStore.getData().items[me.filterIndex];
        var selectData = selectStore.data;
        if (!Ext.String.trim(c_expr)) {
            selectData.filterIcon = '<div style="text-align: center">' +
                '<image src="/images/new_module/no_filter.png" ' +
                'onclick="MatchScheme.filteConditionWindow()"/></div>';
        }else {
            selectData.filterIcon = selectData.filterIcon.replace("no_filter", "filter");
        }
        selectStore.commit();
    },
    /**
     * 获取信息集对应关系
     * @returns {Array}
     */
    getMatchSetList: function () {
        var setMatchList = [];
        var map = new HashMap();
        map.put("operaType", "getSetMatchList");
        Rpc({
            functionId: 'SYS0000003102', async: false, success: function (res) {
                returnData = Ext.decode(res.responseText);
                setMatchList = returnData.setMatchList;
            }
        }, map);
        return setMatchList;
    },
    /**
     * 防止切换子集后数据丢失
     * @param returnMap
     */
    updateFieldMatch: function (returnMap) {
        var me = this;
        var bodyPanel = me.query("#thirdPanel")[0];
        var thirdSelectSetComboValue = bodyPanel.query("#thirdSelectSetCombo")[0].getValue();
        var parentValue = thirdSelectSetComboValue.split("--")[0];
        var parentSetId = parentValue.split(":")[0];
        var resultMap = returnMap[parentSetId];
        var fieldMatchMap = resultMap.fieldMatchMap;//上下级指标对应关系
        var gridAllData = me.getGridAllStore("thirdPanel").data.items;

        for (var i = 0; i < gridAllData.length; i++) {//更新returnMap中的指标对应数据
            var oneRecordData = gridAllData[i].data;
            var newValue = oneRecordData.localItem;
            var field1 = oneRecordData.parentItemId;
            fieldMatchMap[field1] = newValue;
        }
    },
    /**
     * 指标未全部对应的信息集
     * @param returnMap {A01:{},A04:{}}
     * @param selectSetStoreData  上下级信息集匹配关系
     */
    getNotMatchFieldSet: function (returnMap, selectSetStoreData) {
        var me = this;
        me.notMatchFieldSet = {};
        var setList = me.menusContent.set_list;
        var psnStatus = me.menusContent.psn_status;//校验人员状态指标
        var verifyFieldArr = me.getVerifyFieldArr();

        var mustMatchField = {};//指标是否必填对应
        for (var set1 in returnMap) {
            var oneSetInfoMap = returnMap[set1];
            var oneSetFieldMatchMap = oneSetInfoMap.fieldMatchMap;//该信息集指标对应关系

            var fieldItemLength = 0;//记录该信息集上级传过来多少指标
            for (var i = 0; i < setList.length; i++) {
                var oneSetInfo = setList[i];
                var set_id = oneSetInfo.set_id;
                if (set_id == set1) {
                    var fieldItemList = oneSetInfo.fielditem_list;
                    fieldItemLength = fieldItemList.length;

                    for (var j = 0; j < fieldItemLength; j++) {
                        var oneFieldObj = fieldItemList[j];
                        var itemId = oneFieldObj.itemid;
                        var mustFlag = oneFieldObj.mustbe;
                        var fieldName1 = oneFieldObj.itemdesc;
                        mustMatchField[itemId] = mustFlag;
                        //E0122、E01A1、B0110、人员状态指标、必填指标记录到me.specialFieldmatchMap
                        if (itemId == "B0110" || itemId == "E0122" || itemId == "E01A1" || mustFlag == "TRUE" || itemId == psnStatus) {
                            me.specialFieldmatchMap[itemId] = fieldName1 + "`false";
                        }
                    }
                    break;
                }
            }
            var matchCount = 0;//记录有多少指标进行了匹配
            for (var field1 in oneSetFieldMatchMap) {//oneSetFieldMatchMap是空时
                if (oneSetFieldMatchMap[field1]) {
                    //E0122、E01A1、B0110、人员状态指标、必填指标记录到me.specialFieldmatchMap
                    if (field1 == "B0110" || field1 == "E0122" || field1 == "E01A1" || mustMatchField[field1] == "TRUE" || field1 == psnStatus) {
                        me.specialFieldmatchMap[field1] = me.specialFieldmatchMap[field1].replace("false", "true");
                    }
                    for (var i = 0; i < verifyFieldArr.length; i++) {
                        var oneVerifyField = verifyFieldArr[i];
                        if (oneVerifyField == field1) {
                            me.verifyFieldmatchMap[field1] = oneSetFieldMatchMap[field1].split(":")[0];
                            break;
                        }
                    }
                    matchCount++;
                }
            }
            if (matchCount != fieldItemLength) {//对应条数与上级传过来的不一致,则写到me.notMatchFieldSet
                me.writeNotMatchFieldSet(selectSetStoreData, set1);
            }
        }
    },
    /**
     * 向me.notMatchFieldSet里写未完成匹配的信息集
     * @param selectSetStoreData
     * @param set1
     */
    writeNotMatchFieldSet: function (selectSetStoreData, set1) {
        var me = this;
        for (var i = 0; i < selectSetStoreData.length; i++) {
            var oneUpAndDownSetMatch = selectSetStoreData[i][0];
            var comboSet1 = oneUpAndDownSetMatch.split("--")[0].split(":")[0];
            var comboSetName1 = oneUpAndDownSetMatch.split("--")[0].split(":")[1];
            if (comboSet1 == set1) {
                me.notMatchFieldSet[set1] = comboSetName1;
                break;
            }
        }
    },
    /**
     * 校验下级指标长度是否超过上级指标
     * @param returnMap
     * @param rowData
     * @param newValue
     */
    checkItemLength: function (returnMap, rowData, newValue) {
        var me = this;
        var lengthError = false;//默认不超长
        var selectedField2 = newValue.split(":")[0];
        var codeSet = rowData.codeSet;//代码型指标不校验长度
        if(codeSet) {
            return lengthError;
        }

        var parentFieldLength = rowData.parentFieldLength;
        //获取上级信息集编号
        var thirdSelectSetComboValue = me.query("#thirdSelectSetCombo")[0].getValue();
        var parentSetInfo = thirdSelectSetComboValue.split("--")[0];//A01:基本信息（上级）
        var set1 = parentSetInfo.split(":")[0];//上级信息集编码
        var oneSetReturnMap = returnMap[set1];
        var fieldTypeMap = oneSetReturnMap.fieldTypeMap;
        var fieldLengthMap = oneSetReturnMap.fieldLengthMap;
        for (var field2 in fieldTypeMap) {
            if (selectedField2 == field2) {
                var itemType = fieldTypeMap[field2];
                var itemLength = fieldLengthMap[field2];
                var fieldLength = itemLength.split("`")[0];
                if (parseInt(parentFieldLength)< parseInt(fieldLength)) {
                    lengthError = true;
                    break;
                }
                if (itemType == "N") {//数值型的整数位数和小数位数都要校验
                    var decimalWidth = itemLength.split("`")[1];
                    var parentDecimalWidth = rowData.parentDecimalWidth;
                    if (parseInt(parentDecimalWidth) < parseInt(decimalWidth)) {
                        lengthError = true;
                    }
                }
                break;
            }
        }
        return lengthError;
    },
    /**
     * 自动对应指标时校验下级指标类型是否与上级指标一致
     * @param returnMap
     * @param parentSetInfo 上级信息集
     * @param fieldStr 下级指标信息
     * @param tempData 当前数据
     */
    checkItemType: function (returnMap, parentSetInfo, fieldStr, tempData) {
        var typeError = false;//默认类型一致
        var set1 = parentSetInfo.split(":")[0];
        var field2 = fieldStr.split(":")[0];
        var oneSetMap = returnMap[set1];
        var fieldTypeMap = oneSetMap.fieldTypeMap;
        var fieldCodeSetIdMap = oneSetMap.fieldCodeSetIdMap;
        var codeSet = tempData.codeSet;//上级代码类
        var parentItemType = tempData.parentItemType;//上级指标类型

        var fieldType2 = fieldTypeMap[field2];
        var fieldCodeSet2 = fieldCodeSetIdMap[field2];

        if (fieldType2 != parentItemType) {
            return true;
        }
        if (fieldType2 == "A" && "A" == parentItemType) {
            if (fieldCodeSet2 && !codeSet || !fieldCodeSet2 && codeSet) {
                return true;
            }
        }
        return typeError;
    },
    /**
     * 创建第三步对应的panel
     * @returns {Ext.Panel}
     */
    createThirdPanel: function () {
        var me = this;
        me.verifyFieldmatchMap = {};//记录校验规则中的指标对应关系 用于第六步
        me.specialFieldmatchMap = {};//E0122、E01A1、B0110、校验条件指标、人员状态指标、必填指标是否完成匹配
        var setMatchList = me.getMatchSetList();

        var thirdPanel = me.createCommonPanel(3, "secondPanel", "thirdPanel", "forthPanel");

        var selectSetStoreData = [];//选择信息集数据
        var setMatchObj = {};//上级与下级信息集编码对应关系

        for (var i = 0; i < setMatchList.length; i++) {
            var oneSet = setMatchList[i];
            var matchedSet = oneSet.matchSet;
            if (matchedSet) {
                var setArr = [];//一条数据
                setArr.push(oneSet.set_id + ":" + oneSet.set_name + "--" + matchedSet);
                setArr.push(oneSet.set_id + ":" + oneSet.set_name + "--" + matchedSet);
                selectSetStoreData.push(setArr);
                setMatchObj[oneSet.set_id] = oneSet.matchSet;
            }
        }

        var returnMap = me.searchThirdData(setMatchObj);
        //指标未全部对应的信息集
        me.getNotMatchFieldSet(returnMap, selectSetStoreData);

        var matchItemCombo = Ext.widget("combobox", {//指标匹配combo
            valueField: 'id',
            displayField: 'displayText',
            editable: false,
            listeners: {
                change: function (combo, newValue) {
                    var lengthError = false;
                    var rowRecord = gridPanel.getSelectionModel().getSelection()[0];
                    var rowData = rowRecord.data;//选中的数据

                    if (newValue && newValue != dr.gd.pleaseselect) {
                        lengthError = me.checkItemLength(returnMap, rowData, newValue);
                        if (lengthError) {
                            Ext.Msg.alert(dr.gd.tip, dr.gd.itemlengtherror,function(){
                                combo.setValue("");
                            });// "下级指标长度不能大于上级指标！";
                            return;
                        }
                    }
                    if (newValue == dr.gd.pleaseselect) {
                        combo.setValue("");
                        return;
                    }

                    var field1List = [];
                    field1List.push(rowData.parentItemId);
                    //删除因更改匹配指标造成的代码对应表中的脏数据
                    var map = new HashMap();
                    map.put("operaType", "deleteFieldAssociatedData");
                    map.put("field1List", field1List);
                    Rpc({functionId: 'SYS0000003102', success: function (res) {
                    }}, map);

                    rowData.localItem = newValue ? newValue : "";
                    rowRecord.commit();//更新数据

                    me.myMask = me.createMask(dr.gd.saveingcodematch, gridPanel);//"正在保存数据,请不要进行其他操作！
                    // 延时解决数据太多下拉框不回收的现象
                    me.setTimeOut = setTimeout(function () {
                        me.matchFieldFunc(setMatchObj, false,returnMap);
                        window.focus();
                    }, 100);
                },
                focus: function (unknow, targetInfo) {
                    if (targetInfo.relatedTarget.cellIndex != 5) {//该事件的弊端，点击其他地方也触发，过滤一下
                        return;
                    }
                    //获取正在操作的行数据
                    var rowData = me.getSelectedData(gridPanel);
                    var newStoreData = [];
                    var newCodeStoreData = [];
                    for (var i = 0; i < me.fieldList.length; i++) {
                        var fieldIdName = me.fieldList[i][0];//下级某信息集的某个指标  如：A0101:人员编号
                        var fieldId = fieldIdName.split(":")[0];//该指标编码
                        var fieldType = me.fieldTypeMap[fieldId];//该指标类型

                        if (fieldType == rowData.parentItemType) {//下级指标类型与上级指标类型相同放入下拉框数据
                            var fieldCodeSetId = me.fieldCodeSetIdMap[fieldId];//该指标的codesetid
                            if (fieldType == "A") {//字符型 代码型特殊处理
                                var parentCodeSetId = rowData.codeSet;
                                if (parentCodeSetId == "") {
                                    parentCodeSetId = "0";
                                }
                                if (fieldCodeSetId == "") {
                                    fieldCodeSetId = "0";
                                }

                                if (parentCodeSetId != "0" && fieldCodeSetId != "0") {
                                    newCodeStoreData.push(me.fieldList[i]);
                                }
                                if (parentCodeSetId == "0" && fieldCodeSetId == "0") {
                                    newStoreData.push(me.fieldList[i]);
                                }
                            } else {
                                newStoreData.push(me.fieldList[i]);
                            }
                        }
                    }

                    var finallyStoreData = newStoreData.length > 0 ? newStoreData : newCodeStoreData;
                    finallyStoreData.splice(0, 0, me.nullItem);

                    var newMatchItemStore = new Ext.data.ArrayStore({
                        fields: ['id', 'displayText'],
                        data: finallyStoreData
                    });
                    matchItemCombo.setStore(newMatchItemStore);
                    matchItemCombo.expand();
                }
            }
        });

        var selectSetStore = new Ext.data.ArrayStore({
            fields: ['selectSetId', 'selectSetValue'],
            data: selectSetStoreData
        });
        var tbar = [{
            xtype: 'combobox',
            itemId: "thirdSelectSetCombo",
            width: 300,
            emptyText: dr.gd.selectset,//请选择信息集
            store: selectSetStore,
            valueField: 'selectSetId',
            displayField: 'selectSetValue',
            editable: false,
            listeners: {
                render: function (combo) {
                    combo.setValue(selectSetStoreData[0][0]);//默认选择第一个子集
                },
                change: function (combo, newValue) {
                    me.myMask = me.createMask(dr.gd.loading, gridPanel);//"正在加载,请稍候...s
                    var gridPanelStore = gridPanel.getStore();
                    gridPanelStore.removeAll();

                    //取当前选择信息集的所有数据
                    var parentNewValue = newValue.split("--")[0];
                    var parentSetId = parentNewValue.split(":")[0];
                    var resultMap = returnMap[parentSetId];

                    me.fieldList = resultMap.fieldList;//选择指标combo数据源
                    me.fieldTypeMap = resultMap.fieldTypeMap;//指标与类型对应关系
                    me.fieldCodeSetIdMap = resultMap.fieldCodeSetIdMap;//指标与其代码类对应关系
                    me.fieldMatchMap = resultMap.fieldMatchMap;//上下级指标对应关系
                    var matchItemStore = new Ext.data.ArrayStore({
                        fields: ['id', 'displayText'],
                        data: me.fieldList
                    });
                    matchItemCombo.setStore(matchItemStore);

                    // 延时解决数据太多下拉框不回收的现象
                    me.setTimeOut = setTimeout(function () {
                        // 填充第三页面数据
                        me.refreshThirdGridStore(parentSetId);
                        if(me.myMask) {
                            me.myMask.destroy();
                        }
                        clearTimeout(me.setTimeOut);
                    }, 100);
                }
            }
        }, {
            xtype: 'button',
            text: dr.gd.automatch,//自动对应
            handler: function () {
                me.myMask = me.createMask(dr.gd.saveingcodematch, gridPanel);//"正在保存数据,请不要进行其他操作！
                // 延时解决数据太多下拉框不回收的现象
                me.setTimeOut = setTimeout(function () {
                    me.matchFieldFunc(setMatchObj, true,returnMap);
                }, 100);
            }, scope: me
        }];

        var gridStore = new Ext.data.ArrayStore({
            fields: ["parentItemId", "parentItemName", "parentItemType", "parentFieldLength","parentDecimalWidth", "codeSet", "mustItem", "onlyItem", "localItem"]
        });

        var columns = [me.createOneColumn(dr.gd.parentitemid, "parentItemId", 2),//上级指标代码
            me.createOneColumn(dr.gd.parentitemname, "parentItemName", 2),//上级指标名称
            me.createOneColumn("", "parentItemType", 0, "", "", true),//上级指标类型
            me.createOneColumn("", "parentFieldLength", 0, "", "", true),//上级指标长度
            me.createOneColumn("", "parentDecimalWidth", 0, "", "", true),//数值型指标小数位数
            me.createOneColumn(dr.gd.codeset, "codeSet", 1),//代码类
            {
                xtype: 'checkcolumn',
                text: dr.gd.mustitem,
                menuDisabled: true,
                dataIndex: "mustItem",
                sortable: false,
                flex: 1,
                listeners: {
                    checkchange: function (checkbox, rowIndex) {//该列不能改变
                        me.resetValueChange(gridPanel, checkbox, rowIndex);
                    }
                }
            }, {
                xtype: 'checkcolumn',
                text: dr.gd.onlyitem,
                menuDisabled: true,
                dataIndex: "onlyItem",
                sortable: false,
                flex: 1,
                listeners: {
                    checkchange: function (checkbox, rowIndex) {//该列不能改变
                        me.resetValueChange(gridPanel, checkbox, rowIndex);
                    }
                }
            },
            me.createOneColumn(dr.gd.localitem, "localItem", 2, "", matchItemCombo)//唯一性指标
        ];
        var gridPanel = me.createCommonGrid(tbar, gridStore, columns);
        thirdPanel.add(gridPanel);
        return thirdPanel;
    },
    /**
     * checkcolumn拒绝修改数据
     * @param gridPanel
     * @param checkbox
     * @param rowIndex
     */
    resetValueChange: function (gridPanel, checkbox, rowIndex) {
        var currentGridStore = gridPanel.getStore();
        var tempRecord = currentGridStore.data.items[rowIndex];
        tempRecord.reject();//拒绝修改
    },
    /**
     * 获取所有校验指标拼成的字符串
     */
    getVerifyFieldArr: function () {
        var me = this;
        var verifyFieldArr = [];
        var verifyFieldStr = "";
        var verifyList = me.menusContent.verify_list;
        if (verifyList) {
            for (var i = 0; i < verifyList.length; i++) {
                var oneVerifyObj = verifyList[i];
                var vfMenus = oneVerifyObj.vfmenus.toUpperCase();
                verifyFieldStr = verifyFieldStr + vfMenus + "`";
            }
            if (verifyFieldStr) {
                verifyFieldStr = verifyFieldStr.substring(0, verifyFieldStr.length - 1);
                verifyFieldArr = verifyFieldStr.split("`");
                for (var i = 0; i < verifyFieldArr.length; i++) {
                    if(me.verifyFieldmatchMap){//初始化校验指标对应
                        if(!me.verifyFieldmatchMap[verifyFieldArr[i]]){
                            me.verifyFieldmatchMap[verifyFieldArr[i]] = "";
                        }
                    }
                }
            }
        }
        return verifyFieldArr;
    },
    /**
     * 指标对应
     * @param setMatchObj 上下级信息集对应关系
     * @param autoFlag 自动对应标识
     */
    matchFieldFunc: function (setMatchObj, autoFlag, returnMap) {
        var me = this;
        var psnStatus = me.menusContent.psn_status;//校验人员状态指标
        var verifyFieldArr = me.getVerifyFieldArr();
        var thirdSelectSetComboValue = me.query("#thirdSelectSetCombo")[0].getValue();
        var parentSetInfo = thirdSelectSetComboValue.split("--")[0];//A01:基本信息（上级）
        var currentGridStore = me.getGridAllStore("thirdPanel");
        var currentGridData = currentGridStore.data.items;

        if (autoFlag) {//自动对应
            for (var i = 0; i < currentGridData.length; i++) {
                var tempRecord = currentGridData[i];
                var tempData = tempRecord.data;
                var parentItemName = tempData.parentItemName;//上级指标名称
                for (var j = 0; j < me.fieldList.length; j++) {
                    var fieldStr = me.fieldList[j][0];//如 "A0101:人员编号"
                    var fieldName = fieldStr.split(":")[1];//下级指标名称
                    if (fieldName == parentItemName) {//相同时
                        var lengthError = me.checkItemLength(returnMap, tempData, fieldStr);
                        var typeError = me.checkItemType(returnMap, parentSetInfo, fieldStr, tempData);
                        if (!lengthError && !typeError) {
                            tempData.localItem = fieldStr;
                            break;
                        } else {
                            var tempLocalItem = tempData.localItem;
                            if (tempLocalItem) {
                                tempData.localItem = "";
                            }
                        }
                    } else {//自动对应全部重新对应，清掉原先的对应数据
                        var tempLocalItem = tempData.localItem;
                        if (tempLocalItem) {
                            tempData.localItem = "";
                        }
                    }
                }
            }
        }
        me.updateFieldMatch(returnMap);

        var matchFieldArr = [];//所有指标对应关系
        var haveFieldNotMatch = false;//默认全部对应完
        for (var i = 0; i < currentGridData.length; i++) {
            var oneFieldObj = currentGridData[i].data;
            var field1 = oneFieldObj.parentItemId;//上级指标编号
            var oneFieldLocalItem = oneFieldObj.localItem;
            var fieldName1 = oneFieldObj.parentItemName;
            var mustFlag = oneFieldObj.mustItem;

            //特殊指标或者关联特殊代码类的 对应关系记录一下用于校验
            if (field1 == "B0110" || field1 == "E0122" || field1 == "E01A1" || mustFlag == "1" || field1 == psnStatus) {
                if (oneFieldLocalItem) {
                    me.specialFieldmatchMap[field1] = fieldName1 + "`true";
                } else {
                    me.specialFieldmatchMap[field1] = fieldName1 + "`false";
                }
            }
            var localItemId = "";
            if (oneFieldLocalItem) {
                localItemId = oneFieldLocalItem.split(":")[0];
                for (var j = 0; j < verifyFieldArr.length; j++) {
                    var oneVerifyField = verifyFieldArr[j];
                    if (oneVerifyField == field1) {
                        me.verifyFieldmatchMap[field1] = localItemId;
                        break;
                    }
                }
            } else {
                haveFieldNotMatch = true;
            }

            oneFieldObj.set1 = parentSetInfo.split(":")[0];
            oneFieldObj.setname1 = parentSetInfo.split(":")[1];
            oneFieldObj.set2 = setMatchObj[oneFieldObj.set1].split(":")[0];
            oneFieldObj.setname2 = setMatchObj[oneFieldObj.set1].split(":")[1];
            oneFieldObj.fieldtype2 = me.fieldTypeMap[localItemId] ? me.fieldTypeMap[localItemId] : "";
            oneFieldObj.codeset2 = me.fieldCodeSetIdMap[localItemId] ? me.fieldCodeSetIdMap[localItemId] : "";
            matchFieldArr.push(oneFieldObj);
        }
        if (haveFieldNotMatch) {//该信息集有指标未对应
            me.notMatchFieldSet[oneFieldObj.set1] = oneFieldObj.setname1;
        } else {//全部对应完毕后，删除未对应记录
            if (me.notMatchFieldSet[oneFieldObj.set1]) {
                delete me.notMatchFieldSet[oneFieldObj.set1];
            }
        }

        if (autoFlag) {
            me.autoFieldAfterRefreshThirdGridStore(matchFieldArr);
        }
        me.saveFieldMatch(matchFieldArr, parentSetInfo.split(":")[0]);
    },
    /**
     * 自动对应指标之后刷新gridpanel的数据(取代逐条record.commit()的形式，效率低)
     * @param matchFieldArr
     */
    autoFieldAfterRefreshThirdGridStore: function (matchFieldArr) {
        var me = this;
        var gridPanel = me.query("#thirdPanel")[0].query("gridpanel")[0];
        var gridStore = gridPanel.getStore();
        gridStore.removeAll();

        for (var i = 0; i < matchFieldArr.length; i++) {
            var oneFieldMatch = matchFieldArr[i];
            gridStore.add({
                parentItemId: oneFieldMatch.parentItemId,
                parentItemName: oneFieldMatch.parentItemName,
                parentItemType: oneFieldMatch.parentItemType,
                parentFieldLength: oneFieldMatch.parentFieldLength,
                parentDecimalWidth: oneFieldMatch.parentDecimalWidth,
                codeSet: oneFieldMatch.codeSet,
                mustItem: oneFieldMatch.mustItem,
                onlyItem: oneFieldMatch.onlyItem,
                localItem: oneFieldMatch.localItem
            });
        }
    },
    /**
     * 指标对应关系
     * @param matchFieldArr
     * @param set1
     */
    saveFieldMatch: function (matchFieldArr, set1) {
        var me = this;
        var map = new HashMap();
        map.put("operaType", "saveFieldMatch");
        map.put("matchFieldArr", matchFieldArr);
        map.put("set1", set1);
        Rpc({functionId: 'SYS0000003102', success: function (res) {
            if(me.myMask) {
                me.myMask.destroy();
            }
            clearTimeout(me.setTimeOut);
        }}, map);
    },
    /**
     * 初始化或刷新thirdpanel数据
     * @param parentSetId 需显示的信息集编码
     */
    refreshThirdGridStore: function (parentSetId) {
        var me = this;
        var setList = me.menusContent.set_list;
        //填充grid数据
        var currentGridStore = me.getGridAllStore("thirdPanel");
        currentGridStore.removeAll();
        for (var i = 0; i < setList.length; i++) {
            var oneSetObj = setList[i];
            var oneSetId = oneSetObj.set_id;
            if (oneSetId == parentSetId) {
                var fieldItemList = oneSetObj.fielditem_list;//信息集中所有的指标
                for (var j = 0; j < fieldItemList.length; j++) {
                    var oneField = fieldItemList[j];
                    var fieldItemId = oneField.itemid;
                    var localItem = "";//对应本地哪个指标
                    for (var key in me.fieldMatchMap) {
                        if (fieldItemId == key) {
                            localItem = me.fieldMatchMap[key];
                        }
                    }
                    currentGridStore.add({
                        parentItemId: fieldItemId,
                        parentItemName: oneField.itemdesc,
                        parentItemType: oneField.itemtype,
                        parentFieldLength: oneField.itemlength,
                        parentDecimalWidth: oneField.decimalwidth,
                        codeSet: oneField.codesetid == "0" ? "" : oneField.codesetid,
                        mustItem: oneField.mustbe == "TRUE" ? 1 : 0,
                        onlyItem: oneField.uniqueflag == "TRUE" ? 1 : 0,
                        localItem: localItem
                    });
                }
            }
        }
    },
    /**
     * 查询第四步数据
     */
    searchForthData: function () {
        var me = this;
        var returnMap = {};
        var map = new HashMap();
        map.put("operaType", "searchForthData");
        Rpc({functionId: 'SYS0000003102', async: false, success: function (res) {
            var returnData = Ext.decode(res.responseText);
            returnMap = returnData.returnMap;
        }}, map);
        return returnMap;
    },
    /**
     * 记录哪些指标的代码项未完成匹配
     * @param codeFieldInfoMap
     */
    getNotMatchCodeObj: function (codeFieldInfoMap) {
        var me = this;
        me.notMatchCodeObj = {};//记录哪些指标的代码项未完成匹配
        var codeMatchList = codeFieldInfoMap.codeMatchList;//trandb_code表中的代码项对应关系
        //本地代码型指标与代码项的对应 [{codeName: "男", codeItem: "1"}, {codeName: "女", codeItem: "2"}]
        var fieldMatchCodeMap = codeFieldInfoMap.fieldMatchCodeMap;

        var codeFieldMatchList = codeFieldInfoMap.codeFieldMatchList;//[["A0107:性别--A0107:性别", "A0107:性别--A0107:性别"],...]
        var fieldMatchCodeSetIdMap = codeFieldInfoMap.fieldMatchCodeSetIdMap;//{A0107: "AX:AX",A0405: "AM:AM"}
        for (var field1 in fieldMatchCodeSetIdMap) {//field1 上级指标
            var matchValue = fieldMatchCodeSetIdMap[field1];//field1:A0107  matchValue:"AX:AX"
            var matchCodeSet2 = matchValue.split(":")[1];//拿到下级指标对应的代码类  AX

            var codeSetItemLength = 0;//记录当前本地代码类有多少代码项
            for (var i = 0; i < codeFieldMatchList.length; i++) {
                var oneFieldMatch = codeFieldMatchList[i][0];//拿到一条上下级指标的对应关系  "A0107:性别--A0107:性别"
                var fieldInfo1 = oneFieldMatch.split("--")[0];//上级的指标编码和指标名称拼接 A0107:性别"
                var matchField1= fieldInfo1.split(":")[0];//上级的指标编码 A0107
                if (matchField1 == field1) {
                    var fieldInfo2 = oneFieldMatch.split("--")[1];//下级的指标编码和指标名称拼接 A0107:性别"
                    var fieldItem2 = fieldInfo2.split(":")[0];//下级的指标编码 A0107
                    codeSetItemLength = fieldMatchCodeMap[fieldItem2].length;
                    break;
                }
            }
            var codeMatchCount = 0;//记录匹配的几条
            for (var i = 0; i < codeMatchList.length; i++) {
                var oneCodeMatch = codeMatchList[i];
                var codeSet2 = oneCodeMatch.codeSet2;//AX
                var fieldItem1 = oneCodeMatch.fieldItem1;//上级指标
                if (matchCodeSet2 == codeSet2 && field1 == fieldItem1) {
                    var code1 = oneCodeMatch.code1;
                    if (code1) {
                        codeMatchCount++;
                    }
                }
            }
            if (codeMatchCount != codeSetItemLength) {//如果匹配的条数不一致
                me.writeNotMatchCodeObj(codeFieldMatchList, field1);
            }
        }
    },

    /**
     * 把未完成匹配的代码类指标记录一下
     * @param codeFieldMatchList
     * @param field1
     */
    writeNotMatchCodeObj: function (codeFieldMatchList, field1) {
        var me = this;
        for (var j = 0; j < codeFieldMatchList.length; j++) {
            var oneFieldMatch = codeFieldMatchList[j][0];//拿到一条上下级指标的对应关系  "A0107:性别--A0107:性别"
            var fieldInfo1 = oneFieldMatch.split("--")[0];//上级的指标编码和指标名称拼接 A0107:性别"
            var fieldItem1 = fieldInfo1.split(":")[0];//上级的指标编码 A0107

            if (fieldItem1 == field1) {
                var fieldName1 = fieldInfo1.split(":")[1];//上级的指标名称 性别
                me.notMatchCodeObj[field1] = fieldName1;
                break;
            }
        }
    },
    /**
     * 创建遮罩
     * @param msg
     * @param target
     * @returns {*|void}
     */
    createMask: function (msg,target) {
        var myMask = new Ext.LoadMask({
            msg: msg,
            target: target
        }).show();
        return myMask;
    },
    /**
     * 创建第四步对应的panel
     * @returns {Ext.Panel}
     */
    createForthPanel: function () {
        var me = this;
        var returnMap = me.searchForthData();
        var codeFieldMatchList = returnMap.codeFieldInfoMap.codeFieldMatchList;//代码类指标
        me.codeSetList = Ext.decode(returnMap.codeItemInfo).codeset_list;//所有上级代码类

        var codeFieldInfoMap = returnMap.codeFieldInfoMap;
        me.getNotMatchCodeObj(codeFieldInfoMap);

        var forthPanel = me.createCommonPanel(4, "thirdPanel", "forthPanel", "fifthPanel");

        var store = new Ext.data.ArrayStore({
            fields: ['selectcodeid', 'selectcodedValue'],
            data: codeFieldMatchList
        });
        var tbar = [{
            xtype: 'combobox',
            itemId: "forthSelectCodeItemCombo",
            width: 400,
            editable: false,
            emptyText: dr.gd.selectcodeitem,//请选择代码类指标
            store: store,
            valueField: 'selectcodeid',
            displayField: 'selectcodedValue',
            editable: false,
            listeners: {
                render: function (combo) {
                    if (codeFieldMatchList.length > 0) {
                        combo.setValue(codeFieldMatchList[0][0]);//默认选择第一个子集
                    }
                },
                change: function (combo, newValue) {
                    me.myMask = me.createMask(dr.gd.loading, gridPanel);//"正在加载,请稍候...
                    var gridPanelStore = gridPanel.getStore();
                    gridPanelStore.removeAll();

                    // 延时解决数据太多下拉框不回收的现象
                    me.setTimeOut = setTimeout(function () {
                        // 填充第四页面数据
                        me.codeSet1 = me.refreshForthTableData(returnMap, gridPanel, newValue);
                        // 修改上级代码名称下拉框数据
                        me.modifyCodeNameStore(codeNameCombo);
                        if(me.myMask) {
                            me.myMask.destroy();
                        }
                        clearTimeout(me.setTimeOut);
                    }, 100);
                }
            }
        }, {
            xtype: 'button',
            text: dr.gd.automatch,//自动对应
            handler: function () {
                me.myMask = me.createMask(dr.gd.saveingcodematch, gridPanel);//正在保存数据,请不要进行其他操作...
                me.setTimeOut = setTimeout(function () {
                    me.codeItemMatchFunc(true, returnMap);
                }, 100);
            }, scope: me
        }];

        var gridStore = new Ext.data.ArrayStore({
            fields: ["parentCodeItem", "parentCodeName", "codeItem", "codeName"]
        });

        var codeNameCombo = Ext.widget("combobox", {
            valueField: 'id',
            displayField: 'displayText',
            editable: false,
            listeners: {
                change: function (combo, newValue) {
                    if (newValue == dr.gd.pleaseselect) {
                        combo.setValue("");//再次触发change事件
                        return;
                    }
                    me.myMask = me.createMask(dr.gd.saveingcodematch, gridPanel);//正在保存数据,请不要进行其他操作...

                    var rowRecord = gridPanel.getSelectionModel().getSelection()[0];
                    var rowData = rowRecord.data;

                    rowData.parentCodeItem = newValue ? newValue.split("`")[1] : "";
                    rowData.parentCodeName = newValue ? newValue.split("`")[0] : "";
                    rowRecord.commit();//更新数据

                    var comboValue = me.query("#forthSelectCodeItemCombo")[0].getValue();
                    var fieldItem1 = comboValue.split("--")[0].split(":")[0];//上级某代码型指标编码
                    var fieldName1 = comboValue.split("--")[0].split(":")[1];//上级某代码型指标名称

                    var codeItemMatchArr = [];
                    var codeItemMatchObj = {};
                    codeItemMatchObj.code1 = rowData.parentCodeItem;
                    codeItemMatchObj.code2 = rowData.codeItem;
                    codeItemMatchObj.codeName1 = rowData.parentCodeName;
                    codeItemMatchObj.codeName2 = rowData.codeName;
                    codeItemMatchObj.codeSet1 = rowData.upAndDownCodeSetId.split(":")[0];
                    codeItemMatchObj.codeSet2 = rowData.upAndDownCodeSetId.split(":")[1];
                    codeItemMatchObj.fieldItem1 = fieldItem1;
                    codeItemMatchArr.push(codeItemMatchObj);

                    var forthGridStore = gridPanel.getStore();
                    me.setTimeOut = setTimeout(function () {
                        //更新前台缓存数据
                        me.updateCodeMatch(returnMap);

                        //检测该代码型指标是否全部对应
                        var haveCodeNotMatch = false;//默认全部对应
                        var forthGridData = forthGridStore.data.items;
                        for (var i = 0; i < forthGridData.length; i++) {
                            var oneRecord = forthGridData[i];
                            var oneData = oneRecord.data;
                            if (!oneData.parentCodeItem) {
                                haveCodeNotMatch = true;
                            }
                        }
                        if (haveCodeNotMatch) {
                            me.notMatchCodeObj[fieldItem1] = fieldName1;
                        } else {
                            if (me.notMatchCodeObj[fieldItem1]) {
                                delete me.notMatchCodeObj[fieldItem1];
                            }
                        }

                        //单条数据实时入库
                        me.saveCodeItemMatchFunc(codeItemMatchArr, fieldItem1);
                    }, 100);
                }
            }
        });

        var columns = [me.createOneColumn(dr.gd.parentcodeitem, "parentCodeItem", 2),//上级代码项
            me.createOneColumn(dr.gd.parentcodename, "parentCodeName", 2, "", codeNameCombo),//上级代码名称
            me.createOneColumn(dr.gd.codeitem, "codeItem", 2),//代码项
            me.createOneColumn(dr.gd.codename, "codeName", 2),//代码名称
            me.createOneColumn("", "upAndDownCodeSetId", 0, "", "", true)//上下级代码类
        ];
        var gridPanel = me.createCommonGrid(tbar, gridStore, columns);

        forthPanel.add(gridPanel);
        return forthPanel;
    },
    /**
     * 对应代码并保存入库
     * @param autoFlag 自动对应标识
     * @param returnMap
     */
    codeItemMatchFunc: function (autoFlag, returnMap) {
        var me = this;
        var codeItemMatchArr = [];//所有代码对应关系
        var comboValue = me.query("#forthSelectCodeItemCombo")[0].getValue();
        var fieldItem1 = comboValue.split("--")[0].split(":")[0];//上级某代码型指标编码
        var fieldName1 = comboValue.split("--")[0].split(":")[1];//上级某代码型指标名称

        var forthGridStore = me.getGridAllStore("forthPanel");
        var forthGridData = forthGridStore.data.items;
        var haveCodeNotMatch = false;//默认全匹配完成

        for (var i = 0; i < forthGridData.length; i++) {//循环gridpanel中的所有数据拼装
            var codeItemMatchObj = {};
            var oneRecord = forthGridData[i];
            var oneData = oneRecord.data;
            var upAndDownCodeSetId = oneData.upAndDownCodeSetId;

            codeItemMatchObj.fieldItem1 = fieldItem1;
            codeItemMatchObj.codeSet1 = upAndDownCodeSetId.split(":")[0];
            codeItemMatchObj.codeSet2 = upAndDownCodeSetId.split(":")[1];
            codeItemMatchObj.code2 = oneData.codeItem;
            codeItemMatchObj.codeName2 = oneData.codeName;
            if (autoFlag) {//自动匹配数据
                for (var j = 0; j < me.codeSetList.length; j++) {
                    var oneCodeSet = me.codeSetList[j];
                    var codeSetId = oneCodeSet.codeset_id;
                    if (codeSetId == me.codeSet1) {
                        var itmeList = oneCodeSet.item_list;

                        //自动对应全部重新对应，清掉原先的对应数据
                        oneData.parentCodeItem = "";
                        oneData.parentCodeName = "";
                        for (var k = 0; k < itmeList.length; k++) {
                            var oneItem = itmeList[k];
                            var itemId1 = oneItem.itemid;//父级代码项id
                            var itemDesc1 = oneItem.itemdesc;//父级代码项描述
                            if (itemId1 == oneData.codeItem) {//优先按itemId对应
                                oneData.parentCodeItem = itemId1;
                                oneData.parentCodeName = itemDesc1;
                                break;
                            } else if (itemDesc1 == oneData.codeName) {//按名称对应
                                oneData.parentCodeItem = itemId1;
                                oneData.parentCodeName = itemDesc1;
                            }
                        }
                        break;
                    }
                }
            }
            if (!oneData.parentCodeItem) {
                haveCodeNotMatch = true;
            }
            codeItemMatchObj.code1 = oneData.parentCodeItem ? oneData.parentCodeItem : "";
            codeItemMatchObj.codeName1 = oneData.parentCodeName ? oneData.parentCodeName : "";
            codeItemMatchArr.push(codeItemMatchObj);
        }

        if (haveCodeNotMatch) {
            me.notMatchCodeObj[fieldItem1] = fieldName1;
        } else {
            if (me.notMatchCodeObj[fieldItem1]) {
                delete me.notMatchCodeObj[fieldItem1];
            }
        }
        //防止切换代码型指标后页面数据丢失
        me.updateCodeMatch(returnMap);
        //替代逐条record.commit()效率更高
        me.autoAfterRefreshForthGridStore(codeItemMatchArr);
        me.saveCodeItemMatchFunc(codeItemMatchArr, fieldItem1);
    },
    /**
     * 自动对应后刷新数据
     * @param codeItemMatchArr
     */
    autoAfterRefreshForthGridStore:function(codeItemMatchArr) {
        var me = this;
        var gridPanel = me.query("#forthPanel")[0].query("gridpanel")[0];
        var gridStore = gridPanel.getStore();
        gridStore.removeAll();

        for (var i = 0; i < codeItemMatchArr.length; i++) {
            var oneCodeMatch = codeItemMatchArr[i];
            gridStore.add({
                parentCodeItem: oneCodeMatch.code1,
                parentCodeName: oneCodeMatch.codeName1,
                codeItem: oneCodeMatch.code2,
                codeName: oneCodeMatch.codeName2,
                upAndDownCodeSetId: oneCodeMatch.codeSet1 + ":" + oneCodeMatch.codeSet2//AX:AX
            });
        }
    },

    /**
     * 保存代码型指标的代码项对应关系
     * @param codeItemMatchArr
     */
    saveCodeItemMatchFunc: function (codeItemMatchArr, fieldItem1) {
        var me = this;
        var map = new HashMap();
        map.put("operaType", "saveCodeItemMatch");
        map.put("fieldItem1", fieldItem1);
        map.put("codeItemMatchArr", codeItemMatchArr);
        Rpc({functionId: 'SYS0000003102', success: function (res) {
            clearTimeout(me.setTimeOut);
            if(me.myMask) {
                me.myMask.destroy();
            }
        }}, map);
    },
    /**
     * 获取操作行数据
     * @param gridPanel
     * @returns {*}
     */
    getSelectedData: function (gridPanel) {
        return gridPanel.getSelectionModel().getSelection()[0].data;
    },
    /**
     * 修改上级代码名称下拉框数据
     * @param codeNameCombo
     * @param codeSetList
     */
    modifyCodeNameStore: function (codeNameCombo) {
        var me = this;
        var codeNameData = [];//下拉框数据源

        for (var i = 0; i < me.codeSetList.length; i++) {
            var oneCodeSet = me.codeSetList[i];
            var codeSetId = oneCodeSet.codeset_id;
            if (codeSetId == me.codeSet1) {//选择匹配的代码类提取数据
                var oneCodeSetItemList = oneCodeSet.item_list;
                for (var j = 0; j < oneCodeSetItemList.length; j++) {
                    var codeItem = oneCodeSetItemList[j];
                    var itemDesc = codeItem.itemdesc;
                    var itemId = codeItem.itemid;
                    var oneCodeNameData = [];//下拉框数据源子元素

                    oneCodeNameData.push(itemDesc + "`" + itemId);
                    oneCodeNameData.push(itemDesc);
                    codeNameData.push(oneCodeNameData);
                }
                break;
            }
        }
        codeNameData.splice(0, 0, me.nullItem);
        var codeNameStore = new Ext.data.ArrayStore({
            fields: ['id', 'displayText'],
            data: codeNameData
        });
        codeNameCombo.setStore(codeNameStore);
    },
    /**
     * 防止切换代码型指标后页面数据丢失
     * @param returnMap
     */
    updateCodeMatch: function (returnMap) {
        var me = this;
        var bodyPanel = me.query("#forthPanel")[0];
        var forthSelectCodeItemComboValue = bodyPanel.query("#forthSelectCodeItemCombo")[0].getValue();
        var value1 = forthSelectCodeItemComboValue.split("--")[0];//取上级指标
        var fieldItem1 = value1.split(":")[0];//上级指标编码
        var gridAllData = me.getGridAllStore("forthPanel").data.items;

        var codeFieldInfoMap = returnMap.codeFieldInfoMap;
        var codeMatchList = codeFieldInfoMap.codeMatchList;

        for (var i = 0; i < gridAllData.length; i++) {//更新returnMap中的指标对应数据
            var oneRecordData = gridAllData[i].data;
            var code1 = oneRecordData.parentCodeItem;
            var codeName1 = oneRecordData.parentCodeName;

            var code2 = oneRecordData.codeItem;
            var codeSet2 = oneRecordData.upAndDownCodeSetId.split(":")[1];

            var haveCodeMatch = false;//默认无该代码项的对应
            for (var j = 0; j < codeMatchList.length; j++) {
                var oneCodeMatch = codeMatchList[j];
                var dbFieldItem1 = oneCodeMatch.fieldItem1;
                var dbCode2 = oneCodeMatch.code2;
                var dbCodeSet2 = oneCodeMatch.codeSet2;
                if (dbFieldItem1 == fieldItem1 && code2 == dbCode2 && codeSet2 == dbCodeSet2) {
                    haveCodeMatch = true;
                    oneCodeMatch.code1 = code1;
                    oneCodeMatch.codeName1 = codeName1;
                    break;
                }
            }
            if (!haveCodeMatch) {
                var oneCodeMatchObj = {};
                oneCodeMatchObj.code1 = code1;
                oneCodeMatchObj.code2 = code2;
                oneCodeMatchObj.codeName1 = codeName1;
                oneCodeMatchObj.codeSet2 = codeSet2;
                oneCodeMatchObj.fieldItem1 = fieldItem1;
                codeMatchList.push(oneCodeMatchObj);
            }
        }
    },

    /**
     * 填充第四页面数据
     * @param returnMap
     * @param gridPanel
     * @param newValue
     * @returns {*}
     */
    refreshForthTableData: function (returnMap, gridPanel, newValue) {
        var configInputTableStore = gridPanel.getStore();

        var fieldInfo1 = newValue.split("--")[0];//上级指标
        var fieldCode1 = fieldInfo1.split(":")[0];//上级指标编码
        var fieldInfo2 = newValue.split("--")[1];//下级指标
        var fieldCode2 = fieldInfo2.split(":")[0];//下级指标编码

        var codeFieldInfoMap = returnMap.codeFieldInfoMap;
        var fieldMatchCodeMap = codeFieldInfoMap.fieldMatchCodeMap;
        var codeMatchList = codeFieldInfoMap.codeMatchList;
        var fieldMatchCodeSetIdMap = codeFieldInfoMap.fieldMatchCodeSetIdMap;
        var codeItemList = fieldMatchCodeMap[fieldCode2];

        var upAndDownCodeSetId = fieldMatchCodeSetIdMap[fieldCode1];
        var downCodeSet = upAndDownCodeSetId.split(":")[1];

        for (var i = 0; i < codeItemList.length; i++) {
            var oneCodeItem = codeItemList[i];
            var codeItem = oneCodeItem.codeItem;
            var codeName = oneCodeItem.codeName;
            var parentCodeItem = "";
            var parentCodeName = "";
            for (var j = 0; j < codeMatchList.length; j++) {
                var oneCodeMatch = codeMatchList[j];
                var code2 = oneCodeMatch.code2;
                var codeSet2 = oneCodeMatch.codeSet2;
                var fieldItem1 = oneCodeMatch.fieldItem1;
                if (codeSet2 == downCodeSet && code2 == codeItem && fieldItem1 == fieldCode1) {
                    parentCodeItem = oneCodeMatch.code1;
                    parentCodeName = oneCodeMatch.codeName1;
                    break;
                }
            }
            configInputTableStore.add({
                parentCodeItem: parentCodeItem,
                parentCodeName: parentCodeName,
                codeItem: codeItem,
                codeName: codeName,
                upAndDownCodeSetId: upAndDownCodeSetId//AX:AX
            });
        }
        return upAndDownCodeSetId.split(":")[0];//返回上级代码项
    },
    searchFifthData: function () {
        var returnMap = {};
        var map = new HashMap();
        map.put("operaType", "searchFifthData");
        Rpc({functionId: 'SYS0000003102', async: false, success: function (res) {
            var returnData = Ext.decode(res.responseText);
            returnMap = returnData.returnMap;
        }}, map);
        return returnMap;
    },
    /**
     * 创建第五步对应的panel
     * @returns {Ext.Panel}
     */
    createFifthPanel: function () {
        var me = this;
        var returnMap = me.searchFifthData();
        var nbase = returnMap.nbase;//选择过的人员库
        var dbList = returnMap.dbList;//本地所有人员库

        var fifthPanel = me.createCommonPanel(5, "forthPanel", "fifthPanel", "sixthPanel");

        fifthPanel.add({
            xtype: 'fieldset',//选择人员库
            height: 383,
            scrollable: true,
            title: dr.gd.selectnbase
        });
        var fieldset = fifthPanel.query("fieldset")[0];
        for (var i = 0; i < dbList.length; i++) {
            var dbPre = dbList[i][0];
            var checkflag = false;
            if (nbase && nbase.indexOf(dbPre) > -1) {
                checkflag = true;
            }
            fieldset.add({
                xtype: 'checkbox',
                itemId: 'nabase_' + i,
                margin: '10 0 0 5',
                width: 135,
                style: 'float:left',
                dbPre: dbPre,
                boxLabel: dbList[i][1],
                checked: checkflag
            });
        }
        return fifthPanel;
    },
    /**
     * 创建第六步对应的panel
     * @returns {Ext.Panel}
     */
    createSixthPanel: function () {
        var me = this;
        me.handleSixData();
        var sixthPanel = me.createCommonPanel(6, "fifthPanel", "sixthPanel", "");

        var tbar = [];
        var gridStore = new Ext.data.ArrayStore({
            fields: ["ruleName", "checkItem", "matchItem", "checkConditions", "forcestate", "valid"]
        });

        var columns = [
            me.createOneColumn(dr.gd.rulename, "ruleName", 2),//规则名称
            me.createOneColumn(dr.gd.checkitem, "checkItem", 2),//校验指标
            me.createOneColumn(dr.gd.matchitem, "matchItem", 2),//对应指标
            me.createOneColumn(dr.gd.checkconditions1, "checkConditions", 1.5),//校验条件
            {
                xtype: 'checkcolumn',
                text: dr.gd.must,//强制
                menuDisabled: true,
                dataIndex: "forcestate",
                sortable: false,
                flex: 1,
                listeners: {
                    checkchange: function (checkbox, rowIndex) {//该列不能改变
                        me.resetValueChange(gridPanel, checkbox, rowIndex);
                    }
                }
            },{
                xtype: 'checkcolumn',
                text: dr.gd.useable,//启用
                menuDisabled: true,
                dataIndex: "valid",
                sortable: false,
                flex: 1,
                listeners: {
                    checkchange: function (checkbox, rowIndex) {//该列不能改变
                        me.resetValueChange(gridPanel, checkbox, rowIndex);
                    }
                }
            }
        ];
        var gridPanel = me.createCommonGrid(tbar, gridStore, columns);
        var gridStore = gridPanel.getStore();
        var verifyList = me.menusContent.verify_list;
        if(verifyList) {
            for (var i = 0; i < verifyList.length; i++) {
                var oneVerifyObj = verifyList[i];
                var vfMenus = oneVerifyObj.vfmenus.toUpperCase();
                var vfmenusArr = vfMenus.split("`");
                var oneVerifyStr = "";
                for (var j = 0; j < vfmenusArr.length; j++) {
                    oneVerifyStr = oneVerifyStr + me.verifyFieldmatchMap[vfmenusArr[j]]+"`";
                }
                oneVerifyStr = oneVerifyStr.substring(0, oneVerifyStr.length - 1);
                var tempVerifyStr = oneVerifyStr.replace(/`/g,"");
                gridStore.add({
                    ruleName: oneVerifyObj.vfname,
                    checkItem: vfMenus,
                    matchItem: tempVerifyStr ? oneVerifyStr : "",
                    checkConditions: oneVerifyObj.vfcond,
                    forcestate: oneVerifyObj.vfforcestate == "1" ? 1 : 0,
                    valid: oneVerifyObj.vfvalid == "1" ? 1 : 0
                });
            }
        }
        sixthPanel.add(gridPanel);
        return sixthPanel;
    },
    /**
     * 处理校验条件
     */
    handleSixData: function () {
        var me = this;
        var map = new HashMap();
        var verifyList = me.menusContent.verify_list;
        map.put("operaType", "handleSixthData");
        map.put("menusInfo", me.menusJsonStr);
        map.put("verifyList", verifyList);
        Rpc({functionId:'SYS0000003102',success:function(res){
        }},map);
    },
    /**
     * 创建公共panel
     * @param nextStepIndex 点击下一步后stepview定位
     * @param upItemId 上一步骤panel的itemId
     * @param itemId 当前panel的itemId
     * @param nextItemId 下一步骤panel的itemId
     * @returns {Ext.Panel}
     */
    createCommonPanel: function (nextStepIndex, upItemId, itemId, nextItemId) {
        var me = this;
        var buttonsArr = [];
        if (itemId.indexOf("first") > -1) {//第一步有取消按钮
            buttonsArr.push({
                text: dr.gd.cancel,//取消
                height: 22,
                handler: function () {
                    me.close();
                }
            });
        }
        if (itemId.indexOf("first") == -1) {//不是第一步都有上一步按钮
            buttonsArr.push({
                text: dr.gd.upstep,//上一步
                height: 22,
                handler: function () {
                    commonPanel.hide();//隐藏当前panel
                    var upPanel = me.query("#" + upItemId)[0];//上一步骤panel
                    upPanel.show();
                    me.changeStepIndex(nextStepIndex - 2);
                }
            });
        }
        if (itemId.indexOf("sixth") == -1) {//不是第六步都有下一步按钮
            buttonsArr.push({
                text: dr.gd.nextstep,//下一步
                height: 22,
                handler: function () {
                    var dataErrorMsg = me.checkDataRule(itemId);
                    if (dataErrorMsg.indexOf("alert") > -1) {
                        Ext.Msg.alert(dr.gd.tip, dataErrorMsg.split("`")[1]);
                    } else if (dataErrorMsg.indexOf("confirm") > -1) {
                        Ext.Msg.confirm(dr.gd.tip, dataErrorMsg.split("`")[1], function (btn) {
                            if (btn == "yes") {
                                me.nextStepHandle(nextStepIndex, commonPanel, itemId, nextItemId);
                            }
                        });
                    }
                    if (!dataErrorMsg) {
                        me.nextStepHandle(nextStepIndex, commonPanel, itemId, nextItemId);
                    }
                }
            });
        }
        if (itemId.indexOf("sixth") > -1) {//第六步有完成按钮
            buttonsArr.push({
                text: dr.gd.finish,//完成
                height: 22,
                handler: function () {
                    me.close();
                    Ext.Msg.alert(dr.gd.tip, dr.gd.schemesavesuccess);//"方案保存成功！"
                }
            });
        }

        var commonPanel = Ext.create("Ext.Panel", {
            itemId: itemId,
            border: me.borderFlag,
            margin: '8 5 0 5',
            height: 413,
            buttonAlign: 'center',
            buttons: buttonsArr
        });
        return commonPanel;
    },
    /**
     * 下一步点击事件
     * @param nextStepIndex
     * @param commonPanel
     * @param itemId
     * @param nextItemId
     */
    nextStepHandle: function (nextStepIndex, commonPanel, itemId, nextItemId) {
        var me = this;
        /**改变stepview定位*/
        me.changeStepIndex(nextStepIndex);

        commonPanel.hide();//隐藏当前panel
        var mainPanel = me.query("#mainPanel")[0];
        var nextPanel = me.query("#" + nextItemId)[0];

        if (itemId.indexOf("first") > -1) {
            nextPanel = me.createSecondPanel();
        } else if (itemId.indexOf("second") > -1) {
            nextPanel = me.createThirdPanel();
        } else if (itemId.indexOf("third") > -1) {
            nextPanel = me.createForthPanel();
        } else if (itemId.indexOf("forth") > -1) {
            nextPanel = me.createFifthPanel();
        } else if (itemId.indexOf("fifth") > -1) {
            nextPanel = me.createSixthPanel();
        }
        mainPanel.add(nextPanel);
    },
    /**
     * 校验数据是否正确
     * @param itemId 当前所在页面
     * @returns {string}
     */
    checkDataRule: function (itemId) {
        var me = this;
        var dataErrorMsg = "";
        if (itemId.indexOf("first") > -1) {
            if (MatchScheme.saveFilePath === 'checkPathExistError') {
                dataErrorMsg = "alert`" + checkPathExistError;//存储路径配置错误，请重新配置！
            }
            if (me.dataRendering) {//渲染基本信息中
                dataErrorMsg = "alert`" + dr.gd.datarendering;//数据读取中，请稍后
            }
            var selectOrgValue = "";
            if (me.selectedOrgKind == "1") {//单个机构
                selectOrgValue = me.query("#single_selectOrg")[0].getValue();
            }else {
                selectOrgValue = me.query("#multi_selectOrg")[0].getValue();
            }
            var savePathComValue = Ext.String.trim(Ext.getCmp("savePath").getValue());
            //未导入方案 || 未选择对应机构 || 没有输入文件存储路径
            if (!me.bs_asyn_param_c.pkgtype || !selectOrgValue || !savePathComValue) {
                dataErrorMsg = "alert`" + dr.gd.mustfinishcurrentpage;//"请先完成本页内容！"
            }
        } else if (itemId.indexOf("second") > -1) {
            var currentGridStore = me.getGridAllStore("secondPanel");
            var currentGridData = currentGridStore.data.items;
            for (var i = 0; i < currentGridData.length; i++) {
                var oneData = currentGridData[i].data;
                var matchSet = oneData.matchSet;//本条记录的上级setid
                if (oneData.parentSetId == "A01" && !matchSet) {
                    dataErrorMsg = "alert`" + dr.gd.mainmatchmain;// 主集必须对应主集！
                    break;
                }
                if (!matchSet) {
                    dataErrorMsg = "confirm`" + dr.gd.finishsetmatch;//您有信息集未对应，确定进入下一步吗？
                    break;
                }
            }
        } else if (itemId.indexOf("third") > -1) {
            var specialFieldStr = "";
            for (var key in me.specialFieldmatchMap) {//UN UM @K指标必须对应
                var specialFieldValueFlag = me.specialFieldmatchMap[key].split("`")[1];

                if (specialFieldValueFlag == "false") {
                    specialFieldStr = specialFieldStr + me.specialFieldmatchMap[key].split("`")[0] + "，";
                }
            }
            if(specialFieldStr) {
                specialFieldStr = specialFieldStr.substring(0, specialFieldStr.length - 1);
                var tempStr = dr.gd.mustmatchspecialfield;
                tempStr = tempStr.replace("{0}", specialFieldStr);
                dataErrorMsg = "alert`" + tempStr;//{0}指标必须对应！
                return dataErrorMsg;
            }

            var errorSet = "";
            for (var key in me.notMatchFieldSet) {
                errorSet = errorSet + me.notMatchFieldSet[key] + ",";
            }
            if (errorSet) {
                errorSet = errorSet.substring(0, errorSet.length - 1);
                var tempStr = dr.gd.havefieldnotmatch;//{0}还有未对应的指标，确定进入下一步吗？
                tempStr = tempStr.replace("{0}", errorSet);
                dataErrorMsg = "confirm`" + tempStr;
            }
        } else if (itemId.indexOf("forth") > -1) {
            var errorCode = "";
            for (var key in me.notMatchCodeObj) {
                errorCode = errorCode + me.notMatchCodeObj[key] + ",";
            }
            if (errorCode) {
                errorCode = errorCode.substring(0, errorCode.length - 1);
                var tempStr = dr.gd.havecodenotmatch;//{0}还有未对应的代码项，确定进入下一步吗？
                tempStr = tempStr.replace("{0}", errorCode);
                dataErrorMsg = "confirm`" + tempStr;
            }
        } else if (itemId.indexOf("fifth") > -1) {
            var fifthPanel = me.query("#fifthPanel")[0];
            var fieldset = fifthPanel.query("fieldset")[0];
            var checkboxArr = fieldset.query("checkbox");
            me.bs_asyn_param_c.nbase = "";
            for (var i = 0; i < checkboxArr.length; i++) {
                var checked = checkboxArr[i].checked;
                var dbPre = checkboxArr[i].config.dbPre;
                if(checked){
                    me.bs_asyn_param_c.nbase = me.bs_asyn_param_c.nbase + dbPre + ",";
                }
            }
            if(me.bs_asyn_param_c.nbase){
                me.bs_asyn_param_c.nbase = me.bs_asyn_param_c.nbase.substring(0, me.bs_asyn_param_c.nbase.length - 1);
                me.saveNbaseMatch();
            }else{
                dataErrorMsg = "alert`" + dr.gd.mustnbase;//至少选择一个人员库！
            }
        }
        return dataErrorMsg;
    },

    /**
     * 保存上报人员库
     */
    saveNbaseMatch: function () {
        var me = this;
        var map = new HashMap();
        map.put("operaType", "saveNbaseMatch");
        map.put('nbase', me.bs_asyn_param_c.nbase);
        Rpc({functionId: 'SYS0000003102', success: function (res) {
        }}, map);
    },
    /**
     * 改变step定位在哪一步骤
     * @param nextStepIndex
     */
    changeStepIndex: function (nextStepIndex) {
        var me = this;
        var stepPanel = me.query("#steppanel")[0];
        stepPanel.removeAll(true, true);
        var stepview = me.createStepview(nextStepIndex);
        stepPanel.add(stepview);
    },
    /**
     * 创建共用的grid模板
     * @param tbar 顶部组件
     * @param gridStore 数据模型
     * @param columns 列信息
     * @param bbar 底部组件
     * @returns {*}
     */
    createCommonGrid: function (tbar, gridStore, columns, bbar) {
        var me = this;
        var commonGridPanel = Ext.widget("gridpanel", {
            scrollable: true,
            height: 383,
            flex: 1,
            stripeRows: true,//表格是否隔行换色
            columnLines: true,
            enableColumnResize: false,//禁止改变列宽
            enableColumnMove: false,//禁止拖放列
            tbar: tbar.length > 0 ? tbar : false,
            bbar: bbar ? bbar : false,
            store: gridStore,
            columns: columns,
            plugins: {
                ptype: 'cellediting',
                clicksToEdit: 1//单击编辑
            },
            listeners:{
                render: function (panel) {
                    Ext.create('Ext.tip.ToolTip', {
                        target: panel.body,
                        delegate: "td > div.x-grid-cell-inner",
                        shadow: false,
                        trackMouse: true,
                        maxWidth: 800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
                        renderTo: Ext.getBody(),
                        bodyStyle: "background-color:white;border:1px solid #c5c5c5;",
                        listeners: {
                            beforeshow: function updateTipBody(tip) {
                                var div = tip.triggerElement;
                                if (Ext.isEmpty(div)) {
                                    return false;
                                }
                                if (div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight - 4) {
                                    tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + div.innerHTML + "</div>");
                                } else {
                                    return false;
                                }
                            }
                        }
                    });
                },
                rowclick:function (owner, record, element, rowIndex) {
                    me.filterIndex = rowIndex;
                }
            }
        });
        //对于编辑后的单元格，会在左上角出现一个红色的标识，说明该数据是编辑过的，要想去掉这个红色箭头，需要调用record的commit()方法。
        commonGridPanel.on('edit', function (editor, e) {
            e.record.commit();
        });
        return commonGridPanel;
    },
    /**
     * 创建gridpanel的列信息
     * @param text 列头
     * @param dataIndex 列标识
     * @param flex 宽度占比
     * @param xtype 列类型
     * @param editor 列编辑器
     * @param hidden 是否显示该列
     */
    createOneColumn: function (text, dataIndex, flex, xtype, editor, hidden) {
        var columnObj = {};
        columnObj.text = text;
        columnObj.sortable = false;
        columnObj.menuDisabled = true;
        columnObj.dataIndex = dataIndex;
        columnObj.flex = flex;
        if (xtype) {
            columnObj.xtype = xtype;
        }
        if (editor) {
            columnObj.editor = editor;
        }
        if (hidden) {
            columnObj.hidden = hidden;
        }
        return columnObj;
    },
    /**
     * 第二步所需数据
     * @returns {Array}
     */
    searchSecondData: function () {
        var returnMap = [];
        var map = new HashMap();
        map.put("operaType", "searchSecondData");
        Rpc({
            functionId: 'SYS0000003102', async: false, success: function (res) {
                var returnData = Ext.decode(res.responseText);
                returnMap = returnData.returnMap;
            }
        }, map);
        return returnMap;
    },
    /**
     * 第三步所需数据
     */
    searchThirdData: function (setMatchObj) {
        var me = this;
        var returnMap = {};
        var map = new HashMap();
        map.put("operaType", "searchThirdData");
        map.put("menusInfo", me.menusJsonStr);
        map.put("setMatchObj", setMatchObj);
        Rpc({functionId: 'SYS0000003102', async: false, success: function (res) {
            returnData = Ext.decode(res.responseText);
            returnMap = returnData.returnMap;
        }}, map);
        return returnMap;
    }
});
