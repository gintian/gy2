/**
 *薪资报表页面
 *zhaoxg 2016-4-13
 */
Ext.define('SalaryReport.SalaryReport', {
    constructor: function (config) {
        salaryReportScope = this;
        salaryReportScope.gz_module = config.gz_module;////薪资和保险区分标识  1：保险  否则是薪资
        salaryReportScope.model = config.model;//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
        salaryReportScope.salaryHistoryModel = config.salaryHistoryModel;//model为3（工资历史数据）时：=3未归档  =4归档
        salaryReportScope.bosdate_encrypt = config.appdate;
        salaryReportScope.salaryid_encrypt = config.salaryid;
        salaryReportScope.count_encrypt = config.count;
        salaryReportScope.viewtype = config.viewtype;
        salaryReportScope.tablesubModuleId = config.tablesubModuleId;
        if(config.model=='1'){
            parentPage=spCollectScope;
        }else if (config.model == '3') {//薪资历史数据进入
            parentPage = SalaryHistoryData;
        } else {
            parentPage = accounting;
        }

        var map = new HashMap();
        map.put("salaryid", config.salaryid);
        map.put("bosdate", config.appdate);
        map.put("count", config.count);
        map.put("gz_module", config.gz_module);
        map.put("model", config.model);
        Rpc({
            functionId: 'GZ00000501', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                    salaryReportScope.salaryid = result.salaryid;
                    salaryReportScope.bosdate = result.bosdate;
                    salaryReportScope.count = result.count;
                    salaryReportScope.a_code = result.a_code;
                    salaryReportScope.init(result);
                } else {//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                    Ext.showAlert(result.message);
                }
            }
        }, map);
    },
    init: function (result) {
        Ext.util.CSS.createStyleSheet("width:0px;height:0px", "treeiconCls");
        var addPower = result.addPower;
        var delPower = result.delPower;
        var editPower = result.editPower;
        var columns = [
            {text: 'id', hidden: true, dataIndex: 'id', hideable: false},//主键
            {
                xtype: 'treecolumn',
                text: gz.label.reportName, //报表名称
                sortable: false, width: 280, dataIndex: 'text', menuDisabled: true,
                renderer: function (value, meta, record) {
                    var data = record.data;
                    var returnValue = value;
                    var parentNode = record.parentNode.data;
                    if (data.leaf) {
                        if (data.rsid != undefined && data.rsid != '0') {
                            returnValue = "<a href='javascript:salaryReportScope.openSalaryReport(\"" + data.rsid + "\",\"" + data.id + "\",\"" + parentNode.text + "\",\"" + value + "\")'>" + value + "</a>";
                        } else if (data.rsid == '0') {
                            returnValue = salaryReportScope.getMuster(data,parentNode,value);
                        } else {
                            if (data.id == 'm4') {
                                returnValue = "<a href='javascript:salaryReportScope.openSalaryReport(\"4\",\"\",\"\",\"" + value + "\")'>" + value + "</a>";
                            }
                        }
                    }
                    return returnValue;
                }
            },
            {
                text: gz.label.commonly,// 常用
                sortable: false, width: 50, dataIndex: 'commonRepot', menuDisabled: true,
                renderer: function (value, meta, record) {
                    var returnValue = "";
                    if (undefined != value && "" != value) {
                        var data = record.data;
                        var img = "";
                        if ("0" == value) {
                            img = "/images/new_module/button_off.png";
                        } else {
                            img = "/images/new_module/button_on.png";
                        }
                        returnValue = "<img style='cursor:pointer;' id='stateBtn_" + data.rsid + "_" + data.id + "' onclick='salaryReportScope.changeState(\"" + data.rsid + "\",\"" + data.id + "\",\"" + value + "\")' src='" + img + "' width=35 height=16/>";
                    }
                    return returnValue;
                }
            },
            {
                text: gz.label.operation,//'操作'
                align: 'center', sortable: false, width: 120, menuDisabled: true,
                renderer: function (value, meta, record) {
                    var data = record.data;

                    var rsid = data.rsid == undefined ? 'p' : data.rsid;
                    if (data.id == 'm0' || rsid == '0' || data.id == 'm4') {
                        return "";
                    }
                    var hdiv = Ext.getDom("console_" + rsid + "_" + data.id);
                    var returnValue = "";
                    if (hdiv == undefined) {
                        returnValue = "<div id='console_" + rsid + "_" + data.id + "' style='height: auto;width: auto;display:none'  > ";
                        var img = "";
                        if (record.isLeaf() == true) {
                            if (rsid != 'p' && data.isShared != '1') {
                                if (editPower == '1') {
                                    //修改
                                    img = "<img style='cursor:pointer;' title='" + common.button.update + "' onclick='salaryReportScope.editReport(\"" + data.id + "\",\"edit\")' src='/images/new_module/salaryReportEdit.png' width=20 height=20/>";
                                }
                                if (delPower == '1') {
                                    if (img != "") {
                                        img += "&nbsp;&nbsp;";
                                    }
                                    //删除
                                    img += "<img style='cursor:pointer;' title='" + common.button.todelete + "' onclick='salaryReportScope.delReport(\"" + data.text + "\",\"" + rsid + "\",\"" + data.id + "\")' src='/images/new_module/salaryReportDel.png' width=20 height=20/>";
                                }
                            }

                        } else {
                            if (addPower == '1') {
                                //新增
                                img = "<img style='cursor:pointer;' title='" + common.button.insert + "' onclick='salaryReportScope.editReport(\"" + data.id.substring(1) + "\",\"add\")' src='/images/new_module/salaryReportAdd.png' width=20 height=20/>";
                            }
                        }
                        returnValue += img;
                        returnValue += "</div>";
                    } else {
                        returnValue = hdiv.parentElement.innerHTML;
                    }
                    return returnValue;
                }
            }
        ];

        var store = Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true,
                children: result.treeData
            }
            // root: {
            //     expanded: true,
            //     children: data
            // }
        });
        var mainPanel = Ext.create('Ext.tree.Panel', {
            layout: 'fit',
            id: 'reportTreePanel',
            store: store,
            rootVisible: false, // 指定根节点可见
            border: false,
            columnLines: false,
            useArrows: true,
            defaults: {
                iconCls: 'treeiconCls'
            },
            stripeRows: true,
            rowLines: true,
            enableColumnMove: false,
            sortableColumns: false,
            columns: columns,
            listeners: {
                'cellclick': function (me, td, cellIndex, record, tr, rowIndex, e, eOpts) {//点击行时 打开或者关闭树节点
                    if ((cellIndex == 1 || cellIndex == 2) && record.isLeaf() == false) {
                        if (record.isExpanded()) {
                            record.collapse();
                        } else {
                            record.expand();
                        }
                    }
                },

                afteritemexpand: function (node, index, item, eOpts) {
                    // node.data.iconCls = 'treeiconCls';
                },
                itemmouseleave: function (me, record) {
                    var data = record.data;
                    var rsid = data.rsid == undefined ? 'p' : data.rsid;
                    if (data.id != 'm0' && rsid != '0' && rsid != '4') {
                        var id = "console_" + rsid + "_" + data.id;
                        var dom = Ext.getDom(id);
                        if (dom != undefined) {
                            dom.style.display = 'none';
                        }
                    }

                },
                itemmouseenter: function (me, record) {
                    var data = record.data;
                    var rsid = data.rsid == undefined ? 'p' : data.rsid;
                    if (data.id != 'm0' && rsid != '0' && rsid != '4') {
                        var id = "console_" + rsid + "_" + data.id;
                        var dom = Ext.getDom(id);
                        if (dom != undefined) {
                            dom.style.display = 'inline';
                        }
                    }
                }
            }
        });
        //mainPanel.expandAll();
        var win = Ext.widget("window", {
            title: gz.label.reportwindow,//'报表输出'
            height: 450,
            width: 479,
            layout: 'fit',
            modal: true,
            closeAction: 'destroy',
            resizable: false,
            items: mainPanel
        });
        win.show();
    },

    /**
     * 打开薪资报表
     * @param rsid 表类号
     * @param rsdtlid 具体表号
     * @param parenttext 父节点名称
     * @param text 选中表名称
     */
    openSalaryReport: function (rsid, rsdtlid, parenttext, text) {
        var theArr = new Array(parenttext, text);
        if (rsid == 3 || rsid == 13) {
            Ext.require('SalaryReport.SalaryReportGroup', function () {
                Ext.create("SalaryReport.SalaryReportGroup", {
                    rsid: rsid,
                    rsdtlid: rsdtlid,
                    salaryid: salaryReportScope.salaryid_encrypt,
                    theArr: theArr
                });
            });
        } else {
            var obj = new Object();
            obj.rsid = rsid;
            obj.rsdtlid = rsdtlid;
            obj.salaryid = salaryReportScope.salaryid_encrypt;
            obj.gz_module = salaryReportScope.gz_module;
            obj.model = salaryReportScope.salaryHistoryModel ? salaryReportScope.salaryHistoryModel : salaryReportScope.model;
            obj.bosdate = salaryReportScope.bosdate_encrypt;
            obj.count = salaryReportScope.count_encrypt;
            obj.title = parenttext + "-->" + text;
            Ext.require('SalaryReport.OpenSalaryReport', function () {
                Ext.create("SalaryReport.OpenSalaryReport", obj);
            });
        }
    },

    getMuster:function(data,parentNode,value){
        var strfunction="";
        if(data.reporttype=='10'){
            strfunction = "<a href='javascript:salaryReportScope.showCustom(\"" + data.id + "\",\"" + parentNode.text + "\",\"" + value + "\")'>" + value + "</a>";
        }else if(data.reporttype=='0'){
            //特殊报表
            strfunction = "<a href='javascript:salaryReportScope.showSpecialreport(\"" + data.tabid + "\")'>" + value + "</a>";
        }else if(data.reporttype=='3'){
            //花名册
            strfunction = "<a href='javascript:salaryReportScope.showOpenMuster(\"" + data.tabid + "\",\"" + parentNode.text + "\",\"" + value + "\",\"" + data.nmodule + "\")'>" + value + "</a>";
        }else if(data.reporttype=='4'){
            strfunction = "<a href='javascript:salaryReportScope.showSimpleMuster(\"" + data.url + "\")'>" + value + "</a>";
        }
        return strfunction;
    },
    //打开自定义报表
    showCustom: function (uid, parenttext, text) {
        var strurl = "/gz/gz_accounting/report/open_gzbanner.do?b_report=link&" +
            "checksalary=salary&opt=int" +
            "&salaryid=" + salaryReportScope.salaryid +
            "&tabid=" + uid +
            "&a_code=" + salaryReportScope.a_code +
            "&subModuleId=" + (salaryReportScope.tablesubModuleId ? salaryReportScope.tablesubModuleId : "") +
            "&gz_module=" + salaryReportScope.gz_module + "&reset=1&" +
            "model=" + (salaryReportScope.salaryHistoryModel ? salaryReportScope.salaryHistoryModel : salaryReportScope.model) +
            "&boscount=" + salaryReportScope.count + "&bosdate=" + salaryReportScope.bosdate + "&pageRows=init";

        Ext.require('SalaryReport.CreateWindow', function () {
            Ext.create("SalaryReport.CreateWindow", {title: parenttext + "-->" + text, url: strurl});
        });
    },
    //打开花名册
    showOpenMuster: function (uid, parenttext, text,nmodule) {

        var a_inforkind='';
        if('3'==nmodule){
            a_inforkind=1;
        }else if('21'==nmodule){
            a_inforkind=2;
        }else{
            a_inforkind=3;
        }

        var thecodeurl = "/general/muster/hmuster/select_muster_name.do?b_custom=link&nFlag="
            +nmodule+"&isCloseButton=1&closeWindow=0&a_inforkind="+a_inforkind+"&result=0&isGetData=1&operateMethod=direct&costID=" + uid;

        Ext.require('SalaryReport.CreateWindow', function () {
            Ext.create("SalaryReport.CreateWindow", {title: parenttext + "-->" + text, url: thecodeurl});
        });
    },
    //打开简单名册报表
    showSimpleMuster: function (url) {
        window.open(url, "_blank", "left=0,top=0,width=" + screen.availWidth + ",height=" + screen.availHeight +
            ",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
    },
    //打开特殊报表
    showSpecialreport:function(uid)
    {
        var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+uid;
        window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
    },
    changeState: function (rsid, id, value) {


        var map = new HashMap();
        map.put("reportStyleID", rsid);
        map.put("tabid", id);
        map.put("salaryid", salaryReportScope.salaryid);
        map.put("model", salaryReportScope.model);
        if (value == "0") {
            map.put("actionType", "addCommon");
        } else {
            map.put("actionType", "delCommon");
        }
        Rpc({
            functionId: 'GZ00000506', async: false, success: function (form, action) {
                var result = Ext.decode(form.responseText);
                if (result.succeed) {

                    var treeStore = Ext.getCmp("reportTreePanel").getStore();
                    var note = treeStore.getNodeById(id);

                    //添加
                    if (value == "0") {
                        note.set("commonRepot", "1");
                        var record = {
                            commonRepot: "1",
                            id: note.data.id,
                            isShared: note.data.isShared,
                            leaf: true,
                            rsid: note.data.rsid,
                            text: note.data.text,
                            reporttype: note.data.reporttype,
                            tabid: note.data.tabid
                        }
                        parentPage.commonreportlist.push(record);
                        if (parentPage.commonreportlist.length == 1) {
                            Ext.getCmp('common_Report_button').setHidden(false);
                            Ext.getCmp('common_Report_button').setText(note.data.text);

                        } else if (parentPage.commonreportlist.length > 1) {
                            if (parentPage.commonreportlist.length == 2) {
                                Ext.getCmp('common_Report_button').setText(gz.label.commonlyReport + '<img style="" src="/ext/ext6/resources/images/button/arrow.gif">');
                            }
                            parentPage.commonreportlist.sort(function (a, b) {
                                var rsid1 = a.rsid;
                                var tabid1 = a.id;
                                var rsid2 = b.rsid;
                                var tabid2 = b.id;
                                if (rsid1 == rsid2) {
                                    return parseInt(tabid1) - parseInt(tabid2);
                                } else {
                                    if ("0" == rsid1 + "") {
                                        return 1;
                                    } else if ("0" == rsid2 + "") {
                                        return -1;
                                    } else {
                                        return parseInt(rsid1) - parseInt(rsid2);
                                    }
                                }
                            });
                        }

                    }
                    //取消
                    else {
                        note.set("commonRepot", "0");
                        var tableid = note.data.id;
                        var rsid = note.data.rsid;
                        Ext.each(parentPage.commonreportlist, function (record, index) {
                            if (record.rsid == rsid && tableid == record.id) {
                                parentPage.commonreportlist.splice(index, 1);
                                return false;
                            }
                        });

                        if (parentPage.commonreportlist.length == 1) {
                            Ext.getCmp('common_Report_button').setText(parentPage.commonreportlist[0].text);

                        } else if (parentPage.commonreportlist.length == 0) {
                            Ext.getCmp('common_Report_button').setHidden(true);
                        }
                    }
                    treeStore.commitChanges();
                } else {
                    Ext.showAlert(result.message);
                }
            }
        }, map);

    },
    //opt add新建 edit编辑
    editReport: function (tabid, opt) {
        Ext.require('SalaryReport.SalaryReportDefine', function () {
            Ext.create("SalaryReport.SalaryReportDefine", {
                salaryid: salaryReportScope.salaryid_encrypt,
                rsdtlid: tabid,
                opt: opt
            });
        });
    },
    //删除薪资报表
    delReport: function (text, rsid, rsdtlid) {
        Ext.showConfirm('<div style="width:200px">确定要删除"' + text + '"吗？</div>', function (optional) {
            if (optional == 'yes') {
                var map = new HashMap();
                map.put("rsid", rsid);
                map.put("rsdtlid", rsdtlid);
                Rpc({
                    functionId: 'GZ00000504', async: false, success: function (form, action) {
                        var result = Ext.decode(form.responseText);
                        var flag = result.succeed;
                        if (flag == true) {
                            var treeStore = Ext.getCmp("reportTreePanel").getStore();
                            var note = treeStore.getNodeById(rsdtlid);
                            var tableid = note.data.id;
                            var rsid = note.data.rsid;
                            Ext.each(parentPage.commonreportlist, function (record, index) {
                                if (record.rsid == rsid && tableid == record.id) {
                                    parentPage.commonreportlist.splice(index, 1);
                                    return false;
                                }
                            });
                            treeStore.remove(note);
                            note.remove();

                            if (parentPage.commonreportlist.length == 1) {
                                Ext.getCmp('common_Report_button').setText(parentPage.commonreportlist[0].text);

                            } else if (parentPage.commonreportlist.length == 0) {
                                Ext.getCmp('common_Report_button').setHidden(true);
                            }

                        } else {
                            Ext.showAlert(result.message);
                        }
                    }
                }, map);
            }
        }, this)
    }
})