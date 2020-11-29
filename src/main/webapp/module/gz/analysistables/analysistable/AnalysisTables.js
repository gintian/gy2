Ext.define("Analysistable.AnalysisTables", {
    requires: ['EHR.extWidget.field.CodeTreeCombox'],
    imodule: 0,//0:薪资  1：保险
    constructor: function (config) {
        analysisTables_me = this;
        this.imodule = config.imodule;
        //操作人权限范围，选择所属机构用。
        this.orgId = "";
        this.getMainView();
    },
    getMainView: function () {
        Ext.create("Ext.container.Viewport", {
            id: 'mainpanel',
            layout: 'fit',
            items: [{
                xtype: 'panel',
                layout: 'fit',
                autoScroll: true,
                title: gz.label.analysisReport,
                items: [this.createGridPanel()]
            }]
        });
    },
    //初始化分析表主页面
    createGridPanel: function () {
        var me = this;
        var vo = new HashMap();
        vo.put("imodule", this.imodule);
        var gridpanel = "";
        Rpc({
            functionId: 'GZ00000714', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                analysisTables_me.orgId = resultObj.return_data.priv;
                //新增，修改，删除，复制的权限
                me.add_pow = resultObj.return_data.add_pow;
                analysisTables_me.edi_pow = resultObj.return_data.edi_pow;
                me.del_pow = resultObj.return_data.del_pow;
                me.cop_pow = resultObj.return_data.cop_pow;
                this.tableStore = Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: resultObj.return_data.table_data
                    }
                });
                Ext.util.CSS.createStyleSheet("width:0px;height:0px", "treeiconCls");
                gridpanel = Ext.create('Ext.tree.Panel', {
                    flex: 1,
                    id: 'tree_analy',
                    height: window.innerHeight - 40,
                    margin: '3 1 1 1',
                    rootVisible: false, // 指定根节点可见
                    border: true,
                    columnLines: true,
                    useArrows: true,
                    defaults: {
                        iconCls: 'treeiconCls'
                    },
                    stripeRows: true,
                    rowLines: true,
                    enableColumnMove: false,
                    sortableColumns: false,
                    store: this.tableStore,
                    columns: [
                        {
                            xtype: 'treecolumn',
                            text: AnalysisTables.tableTr_tableName,
                            dataIndex: 'tableName',
                            width: 350,
                            height: 15,
                            renderer: function (value, metaData, record) {
                                var istitle = record.get("istitle");
                                if (istitle == 1) {
                                    return value;
                                } else {
                                    var rsid_enc = record.get("rsid_enc");
                                    var rsid = record.get("rsid");
                                    var rsdtlid = record.get("rsdtlid");
                                    var nbase = record.get("nbase");
                                    var jump2set = record.get("jump2set");
                                    var salaryids = record.get("salaryids");
                                    var verifying = record.get("verifying");
                                    var tabid = record.get("tabid");
                                    var flag = record.get("flag");
                                    var report_type = record.get("report_type");
                                    return "<span style='cursor:pointer;color:#4169E1;position:relative;' onclick='openAnalysisTable(\"" + rsid + "\",\"" +
                                        rsdtlid + "\",\"" + rsid_enc + "\",\"" + getEncodeStr(value) + "\"," + me.imodule + ",\"" + jump2set + "\",\"" + nbase + "\",\"" +
                                        salaryids + "\",\"" + verifying + "\",\"" + tabid + "\",\"" + flag + "\",\"" + report_type + "\")'>" + value + "</span>";
                                }
                            }
                        },
                        {
                            text: AnalysisTables.tableTr_B0110,
                            dataIndex: 'B0110',
                            align: 'left',
                            width: 130,
                            renderer: function (value, metaData, record) {
                                if (record.data.opretion == 1) {
                                    var imodule = me.imodule;
                                    var rsid = record.data.rsid;
                                    var tabid = record.data.tabid;
                                    var B0110 = record.data.B0110 || "";
                                    var title = ""
                                    if (!Ext.isEmpty(record.data.unitdesc)) {
                                        title = "title='" + record.data.unitdesc + "'";
                                    }
                                    return "<div " + title + " id='ownunit_" + record.data.tabid + "' style='cursor:pointer;overflow: hidden;text-overflow:ellipsis;' onclick='editUnit(\"" + imodule + "\",\"" + rsid + "\",\"" + tabid + "\",\"" + B0110 + "\")' ><image style='margin:0 4px 0 10px;position:relative;top:4px;' src='/images/new_module/depart_edit.png' />" + (record.data.unitdesc || gz.label.all) + "</div>";
                                }
                                return '';
                            }
                        },
                        {
                            text: AnalysisTables.tableTr_username,
                            dataIndex: 'username',
                            align: 'left',
                            width: 120
                        },
                        {
                            text: AnalysisTables.tableTr_createtime,
                            dataIndex: 'create_time',
                            align: 'left',
                            width: 160
                        },
                        {
                            text: AnalysisTables.tableTr_opretion,
                            dataIndex: 'opretion',
                            align: 'center',
                            width: 150,
                            renderer: function (value, metaData, record) {
                                var html = "";
                                if (!record.data.report_type) {
                                    if (record.data.opretion == 0 && me.add_pow) {
                                        html = "<div id='addbtn_" + record.data.rsid + "' style='display:none;' ><image title='" + AnalysisTables.add + "' class='img_cls' src='/images/new_module/salaryReportAdd.png' onclick='createSalary(" + record.data.rsid + "," + me.imodule + ")' /></div>";
                                    } else if (record.data.opretion == 1) {
                                        html += "<div id='infobtn_" + record.data.tabid + "' style='cursor:pointer;display:none;' >";
                                        //根据权限显示
                                        if (analysisTables_me.edi_pow) {
                                            html += "<image title='" + AnalysisTables.update + "' class='img_cls' " +
                                                "src='/images/new_module/salaryReportEdit.png' style='margin-right:10px;' " +
                                                "onclick='editSalary(" + me.imodule + "," + record.data.rsid + "," + record.data.tabid + ",true,\"" + record.get("rsid_enc") + "\",\"" + record.get("rsdtlid") + "\")' />";
                                        }
                                        if (me.del_pow) {
                                            html += "<image title='" + AnalysisTables.dele + "' class='img_cls' " +
                                                "src='/images/new_module/salaryReportDel.png' style='margin-right:10px;' " +
                                                "onclick='delSalary(" + me.imodule + "," + record.data.rsid + "," + record.data.tabid + ",\"" + getEncodeStr(record.get("tableName")) + "\")' />";
                                        }
                                        if (me.cop_pow) {
                                            html += "<image title='" + AnalysisTables.copy + "'class='img_cls' src='/images/new_module/salaryReportCopy.png' " +
                                                "onclick='copySalary(" + me.imodule + "," + record.data.rsid + "," + record.data.tabid + ")' />";
                                        }
                                        html += "</div>";
                                    } else if (record.data.opretion == 3) {
                                        html += "<div id='infobtn_" + record.data.tabid + "' style='cursor:pointer;display:none;' >";
                                        if (analysisTables_me.edi_pow) {
                                            html += "<image title='" + AnalysisTables.update + "' class='img_cls' src='/images/new_module/salaryReportEdit.png' " +
                                                "onclick='editSalary(" + me.imodule + "," + record.data.rsid + "," + record.data.tabid + ",false)' />";
                                        }
                                        html += "</div>";
                                    } else if (record.data.opretion == 4) {
                                        // 对于上级的，不能修改，只有复制
                                        html += "<div id='infobtn_" + record.data.tabid + "' style='cursor:pointer;display:none;' >";
                                        if (me.cop_pow) {
                                            html += "<image title='" + AnalysisTables.copy + "'class='img_cls' src='/images/new_module/salaryReportCopy.png' " +
                                                "onclick='copySalary(" + me.imodule + "," + record.data.rsid + "," + record.data.tabid + ")' />";
                                        }
                                        html += "</div>";
                                    }
                                }
                                return html;
                            }
                        }
                    ],
                    listeners: {
                        itemmouseenter: function (me, record) {
                            if (!record.data.report_type) {
                                if (record.data.opretion == 0 && document.getElementById("addbtn_" + record.data.rsid)) {
                                    document.getElementById("addbtn_" + record.data.rsid).style.display = 'block';
                                } else if (record.data.opretion == 1 || record.data.opretion == 3) {
                                    document.getElementById("infobtn_" + record.data.tabid).style.display = 'block';
                                }
                            }
                        },
                        itemmouseleave: function (me, record) {
                            if (!record.data.report_type) {
                                if (record.data.opretion == 0 && document.getElementById("addbtn_" + record.data.rsid)) {
                                    document.getElementById("addbtn_" + record.data.rsid).style.display = 'none';
                                } else if (record.data.opretion == 1 || record.data.opretion == 3) {
                                    document.getElementById("infobtn_" + record.data.tabid).style.display = 'none';
                                }
                            }
                        }
                    }
                });
            }
        }, vo);

        return gridpanel;
    }

});

/**
 * 打开薪资分析表
 */
function openAnalysisTable(rsid, rsdtlid, rsid_enc, tableName, imodule, jump2set, nbase, salaryids, verifying, tabid, flag, report_type) {
    var me = this;
    var mainView;
    var title = "";
    tableName = getDecodeStr(tableName);
    //除了人员工资汇总表和按部门各月工资构成分析表，其余表都判断需要先设置人员库，薪资类别，统计指标
    if (rsid != '8' && rsid != "17" && rsid != '9' && jump2set == "true") {
        if (analysisTables_me.edi_pow)
            editSalary(imodule, rsid, tabid, true, rsid_enc, rsdtlid);
        else
            Ext.showAlert(AnalysisTables.linkSet);
    } else if (rsid == '12') {
        if (report_type && report_type != "undefined") {
            eval(flag + "('" + tabid + "','" + tableName + "')");
        } else {
            showMusterOpen(tabid, tableName, nbase, salaryids, verifying);
        }
    } else {
        if (rsid == '8') {
            //工资
            title = gz.label.analysisdata.summarytable.replace("{0}", gz.label.salary2);
        } else {
            //保险
            title = gz.label.analysisdata.summarytable.replace("{0}", gz.label.insurance);
        }
        //open 人员工资|保险汇总表
        if (rsid == '8' || rsid == "17") {
            var table = Ext.create("AnalysisdataURL.EmployeePaySummaryTable", {
                rsid: rsid_enc,
                rsdtlid: rsdtlid,
                imodule: imodule,
                edit_pow: analysisTables_me.edi_pow,
                tableName: title
            });
            title += " --> " + tableName;
            mainView = table.getMainView();
        }//open 人员工资|保险项目统计表
        else if (rsid == '7' || rsid == "16") {
            if (rsid == '7') {
                //工资
                title = OptAnalysisTable.user_salary_project_summary_table.replace("{0}", gz.label.salary2);
            } else {
                //保险
                title = OptAnalysisTable.user_salary_project_summary_table.replace("{0}", gz.label.insurance);
            }
            var table = Ext.create("AnalysisdataURL.EmployeePayStatMuster", {
                rsid: rsid_enc,
                rsdtlid: rsdtlid,
                imodule: imodule,
                edit_pow: analysisTables_me.edi_pow,
                tableName: title + "_" + tableName
            });
            title += " --> " + tableName;
            mainView = table.getMainView();
        } else if (rsid == '5' || rsid == "14") {
            if (rsid == '5') {
                //工资
                title = OptAnalysisTable.user_salary_count.replace("{0}", gz.label.salary2);
            } else {
                //保险
                title = OptAnalysisTable.user_salary_count.replace("{0}", gz.label.insurance);
            }
            Ext.require('EmployeePayMusterURL.EmployeePayMuster', function () {
                var table = Ext.create("EmployeePayMusterURL.EmployeePayMuster", {
                    rsid: rsid_enc,
                    rsdtlid: rsdtlid,
                    edit_pow: analysisTables_me.edi_pow,
                    imodule: imodule,
                    tableName: title + "_" + tableName
                });

                title += " --> " + tableName;
                mainView = table.getMainView();

            });
        } else if (rsid == '6' || rsid == "15") {
            if (rsid == '6') {
                //工资
                title = OptAnalysisTable.salary_project_summary_count.replace("{0}", gz.label.salary2);
            } else {
                //保险
                title = OptAnalysisTable.salary_project_summary_count.replace("{0}", gz.label.insurance);
            }
            Ext.require('ItemGroupMusterURL.ItemGroupMuster', function () {
                var table = Ext.create("ItemGroupMusterURL.ItemGroupMuster", {
                    rsid: rsid_enc,
                    rsdtlid: rsdtlid,
                    edit_pow: analysisTables_me.edi_pow,
                    imodule: imodule,
                    tableName: title + "_" + tableName
                });
                title += " --> " + tableName;
                mainView = table.getMainView();
            });
        } else if (rsid == '9') {
            title = gz.label.analysisdata.orderDepartment;
            var table = Ext.create("AnalysisdataURL.GzStructureTable", {
                rsid: rsid_enc,
                rsdtlid: rsdtlid,
                edit_pow: analysisTables_me.edi_pow,
                imodule: imodule,
                tableName: title
            });
            title += " --> " + tableName;
            mainView = table.getMainView();
        } else if (rsid == '10') {//工资总额构成分析表
            title = OptAnalysisTable.salary_total_contant_table;
            var table = Ext.create("AnalysisdataURL.GzAmountStructureTable", {
                rsid: rsid_enc,
                rsdtlid: rsdtlid,
                edit_pow: analysisTables_me.edi_pow,
                imodule: imodule,
                tableName: title + "_" + tableName
            });
            title += " --> " + tableName;
            mainView = table.getMainView();
        } else if (rsid == '11') {//单位部门工资项目统计表
            title = OptAnalysisTable.unit_salary_project_summary;
            var table = Ext.create("AnalysisdataURL.GzItemSummaryTable", {
                rsid: rsid_enc,
                rsdtlid: rsdtlid,
                edit_pow: analysisTables_me.edi_pow,
                imodule: imodule,
                tableName: title + "_" + tableName
            });
            title += " --> " + tableName;
            mainView = table.getMainView();
        }
        if (!mainView) {
            return;
        }
        Ext.create("Ext.window.Window", {
            maximized: true,
            id: 'analysis_detail',
            layout: 'fit',
            title: title,
            autoScroll: true,
            border: false,
            resizable: false,
            items: [mainView]
        }).show();
    }
}

editUnit = function (imodule, rsid, rsdtlid, B0110) {
    var me = this;
    new PersonPicker({
        multiple: true,
        text: "添加",
        orgid: analysisTables_me.orgId,//权限机构
        addunit: true, //是否可以添加单位
        adddepartment: true, //是否可以添加单位
        addpost: false,
        isPrivExpression: false,
        multipleAndSingle: true,
        defaultSelected: [B0110],
        callback: function (c) {
            var B0110 = "";
            var rawType = "";
            if (c.length > 0) {
                var unionName = c[c.length - 1].name;
                if (!Ext.isEmpty(unionName)) {
                    document.getElementById('ownunit_' + rsdtlid).title = unionName;
                }
                document.getElementById('ownunit_' + rsdtlid).innerHTML = "<image style='margin:0 4px 0 10px;position:relative;top:4px;' src='/images/new_module/depart_edit.png' />" + c[c.length - 1].name;
                B0110 = c[c.length - 1].id;
                rawType = c[c.length - 1].rawType;
            } else {
                //如果什么都不选择，则显示全部，和薪资类别所属单位规则一致
                document.getElementById('ownunit_' + rsdtlid).innerHTML = "<image style='margin:0 4px 0 10px;position:relative;top:4px;' src='/images/new_module/depart_edit.png' />" + gz.label.all;
            }
            var map = new HashMap();
            map.put('transType', '4');
            var param = new HashMap();
            param.imodule = imodule;
            param.rsid = rsid;
            param.rsdtlid = rsdtlid;
            param.B0110 = B0110;
            param.rawType = rawType;
            map.put('param', param);
            Rpc({
                functionId: 'GZ00000715',
                async: false,
                success: function (res) {
                    var respon = Ext.decode(res.responseText);
                }
            }, map);
        }
    }, id).open();
};
//新增
createSalary = function (rsid) {
    Ext.getCmp('mainpanel').removeAll();
    var panel = Ext.create("Analysistable.OptAnalysisTable",
        {
            rsid: rsid,
            opt: 1,
            afterSaveOpen: true,
            imodule: imodule
        }).getMainPanel();
    Ext.getCmp('mainpanel').add(panel);
};
/*
修改
rsid_enc 加密后的rsid
 */
editSalary = function (imodule, rsid, rsdtlid, afterSaveOpen, rsid_enc, rsdtlid_enc) {
    Ext.getCmp('mainpanel').removeAll();
    var panel = Ext.create("Analysistable.OptAnalysisTable",
        {
            rsid: rsid,
            opt: 2,
            rsdtlid: rsdtlid,
            imodule: imodule,
            afterSaveOpen: afterSaveOpen,
            rsid_enc: rsid_enc || "",
            rsdtlid_enc: rsdtlid_enc || ''
        }).getMainPanel();
    Ext.getCmp('mainpanel').add(panel);
};
//复制
copySalary = function (imodule, rsid, rsdtlid) {
    Ext.getCmp('mainpanel').removeAll();
    var panel = Ext.create("Analysistable.OptAnalysisTable",
        {
            rsid: rsid,
            opt: 4,
            rsdtlid: rsdtlid,
            imodule: imodule
        }).getMainPanel();
    Ext.getCmp('mainpanel').add(panel);
};
//删除
delSalary = function (imodule, rsid, tabid, tableName) {
    tableName = getDecodeStr(tableName);
    var msg = AnalysisTables.isdelete_selected_data.replace("{0}", "【" + tableName + "】");
    Ext.showConfirm(msg, function (flag) {
        if (flag == "yes") {
            var map = new HashMap();
            map.put('transType', '3');
            var param = new HashMap();
            param.imodule = imodule;
            param.rsid = rsid;
            param.rsdtlid = tabid;
            map.put('param', param);
            Rpc({
                functionId: 'GZ00000715',
                async: false,
                success: function (res) {
                    var respon = Ext.decode(res.responseText);
                    var flag = false;
                    if (respon.return_code == "success") {
                        var node = Ext.getCmp("tree_analy").getRootNode();
                        for (var i in node.childNodes) {
                            var cnode = node.childNodes[i];
                            var nodes = cnode.childNodes;
                            for (var j = 0; j < nodes.length; j++) {
                                //不刷新页面删除
                                if (nodes[j].data.rsid == rsid && nodes[j].data.tabid == tabid) {
                                    nodes[j].remove();
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                break;
                            }
                        }
                    } else {
                        Ext.showAlert(AnalysisTables.dele + OptAnalysisTable.fail);
                    }
                }
            }, map);
        } else {
            return;
        }
    });
};

showMusterOpen = function (uid, text, pre, salaryid, verifying) {
    /*if(isOutofPriv()){
        return;
    }*/
    var archive = "1";//=1分析历史数据
    //分析审批数据1：勾选
    if (verifying == '1') {
        archive = "3";
    }
    var iframe_url = "/gz/gz_analyse/gz_setinfor.do?b_query=link`archive=" + archive + "`titlename=" + text;
    iframe_url += "`tabid=" + uid + "`gz_module=" + this.imodule + "`dbname=" + pre + "`category=" + salaryid;
    var url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(iframe_url);
    ;
    var config = {
        width: 450,
        height: 330,
        type: '3',
        title: text,
        id: 'showMusterOpen_win'
    }
    modalDialog.showModalDialogs(url, "showMusterOpen_win", config, "");

};

//打开人员花名册
showOpenMusterOne = function (id, name) {
    var tabid = id;
    var theArr = new Array("root", name);
    var thecodeurl = "/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=3`a_inforkind=1`result=0`isGetData=1`operateMethod=direct`costID=" + tabid;
    var iframe_url = "/gz/gz_analyse/gz_analyse_iframe.jsp?src=" + $URL.encode(thecodeurl);
    openWin(theArr, "showOpenMusterOne_win", iframe_url);

};

//打开机构花名册
showOpenMusterTwo = function (id, name) {
    var tabid = id;
    var theArr = new Array("root", name);
    var thecodeurl = "/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=21`a_inforkind=2`result=0`isGetData=1`operateMethod=direct`costID=" + tabid;
    var iframe_url = "/gz/gz_analyse/gz_analyse_iframe.jsp?src=" + $URL.encode(thecodeurl);
    openWin(theArr, "showOpenMusterTwo_win", iframe_url);
};

//打开职位花名册
showOpenMusterThree = function (id, name) {
    var tabid = id;
    var theArr = new Array("root", name);
    var thecodeurl = "/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=41`a_inforkind=3`result=0`isGetData=1`operateMethod=direct`costID=" + tabid;
    var iframe_url = "/gz/gz_analyse/gz_analyse_iframe.jsp?src=" + $URL.encode(thecodeurl);
    openWin(theArr, "showOpenMusterThree_win", iframe_url);
};

openWin = function (theArr, id, iframe_url) {
    var width = window.screen.width;
    var height = window.screen.height;
    var config = {
        width: width,
        height: height,
        type: '2',
        id: id,
        dialogArguments: theArr
    }
    if (!window.showModalDialog)
        window.dialogArguments = theArr;
    modalDialog.showModalDialogs(iframe_url, id, config, "");
};

//打开自定义报表
showCustom = function (id, name) {
    var url = "/system/options/customreport/displaycustomreportservlet?ispriv=1&id=" + id;
    window.open(url, "_blank", "left=0,top=0,width=" + screen.availWidth + ",height=" + screen.availHeight + ",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
};

showXLSCustom = function (id, name) {
    if(!isIEBrowser()){
        Ext.showAlert("请使用IE浏览器！");
        return;
    }

    var url = "/system/options/customreport/displaycustomreportservlet?ispriv=1&id=" + id;
    window.open(url, "_blank", "left=0,top=0,width=" + screen.availWidth + ",height=" + screen.availHeight + ",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
};

//打开简单名册报表
showSimpleMuster = function (id, name) {
    window.open(id, "_blank", "left=0,top=0,width=" + screen.availWidth + ",height=" + screen.availHeight + ",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
};

isIEBrowser= function(){
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isOpera = userAgent.indexOf("Opera") > -1;
    if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
        return true;
    }
    //判断是否IE浏览器
    else{
        return false;
    }
}
