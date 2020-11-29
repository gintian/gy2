//考勤审批主页面
Ext.define('KqDataURL.KqDataSp', {
	requires:['KqDataURL.StaffChangeContrast'],
    constructor: function (config) {
        KqDataSp = this;
        var scheme_id = "";
        var kq_year = "";
        var kq_duration = "";
        if (config.scheme_id != undefined) {
            scheme_id = config.scheme_id;
        }
        if (config.kq_year != undefined) {
            kq_year = config.kq_year;
        }
        if (config.kq_duration != undefined) {
            kq_duration = config.kq_duration;
        }

        var jsonStr = {
            type: 'main',
            query: '',
            status: '',
            scheme_id: scheme_id,
            kq_duration: kq_duration,
            kq_year: kq_year
        };
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(jsonStr));
        Rpc({functionId: 'KQ00021201', async: false, success: KqDataSp.init}, map);

    },
    //考勤年份
    kq_year: '',
    //考勤期间
    kq_duration: '',
    //考勤方案
    scheme_id: '',
    //页面数据
    org_list: null,
    //方案列表
    scheme_list: null,
    //考勤期间列表
    year_list: null,
    //状态过滤
    selectStatus: '',
    //身份 1人事处考勤员 2人事处审核人 3下级单位考勤人员 4下级单位审核员
    role: '0',
    //快速查询
    queryValue: '',
    //考勤方案是否需要报人事处审批 0不需要 1需要（需要人事处才可以对数据进行新建 审批等操作）
    secondary_admin: "",
    // 考勤各功能权限
    privs: null,
    // 计算默认不覆盖
    coverDataFlag: "1",
    // 当前用户名
    currentUser: "",
    // 填写审批意见 0：不填写（默认），1：需要填写意见
    approvalMessage:"0",
    init: function (form) {
        var result = Ext.decode(form.responseText);
        var returnStr = eval(result.returnStr);
        //没有负责方案时给出提示
        if (returnStr.return_code == "fail") {
            Ext.create('Ext.panel.Panel', {
                renderTo: Ext.getBody(),
                width: '100%',
                height: '100%',
                margin: '50 0 0 0',
                style: 'text-align:center;',
                border: false,
                html: '<div style="height:135px;"><img src="../../../images/nodata.png"/></div><div style="font-weight:bold;color:#7c7c7c;font-size:16px;font-family' + kq.fontfamily + '">' + returnStr.return_msg + '</div>'
            });
            return;
        }

        var return_data = returnStr.return_data;
        KqDataSp.kq_year = return_data.kq_year;
        KqDataSp.kq_duration = return_data.kq_duration;
        KqDataSp.scheme_id = return_data.scheme_id;
        KqDataSp.org_list = return_data.org_list;
        KqDataSp.year_list = return_data.year_list;
        KqDataSp.scheme_list = return_data.scheme_list;
        KqDataSp.role = return_data.role;
        KqDataSp.privs = return_data.privs;
        KqDataSp.currentUser = return_data.currentUser;
        // 应急中心个性化批量计算 覆盖
        KqDataSp.coverDataFlag = ("1"==KqDataSp.privs.hlwyjzx_flag) ? "0" : "1";
        //是否填写审批意见
        KqDataSp.approvalMessage=return_data.approvalMessage;
        var tpl = new Ext.XTemplate(
            '<tpl for=".">',
            '<div  style=" float:left;height: 148px;width: 240px;padding:10px 0px 10px 15px;" ' +
            'onmouseover="KqDataSp.mouseOver(\'{org_id}\')" onmouseout="KqDataSp.mouseOut(\'{org_id}\')">',//最外层div
            '<div class="" style="height: 128px;width: 200px;float: left;cursor:pointer;border-radius:5px;" onclick="KqDataSp.openKqMx(\'{org_id}\',\'{operation}\',\'{hasNextApprover}\',\'{sp_flag}\');" ' +
            'onmouseover="KqDataSp.divMouseOver(this)" onmouseout="KqDataSp.divMouseOut(this)">',//卡片范围div
            '<table height="108px" style="border:1px solid #D8D8D8;width: 200px;background-color:#FFFFFF;padding:5px;border-radius:5px;">',//卡片table
            '<tr height="30px" style="padding: 0px;">',
            '<td width="125px" valign="top"><div title="{org_name}" style="width:125px;overflow: hidden;',
            'font-size: 16px;padding-top: 2px;padding-left: 5px;text-overflow:ellipsis;white-space: nowrap;">{org_name}</div>',//单位名称
            '</td>',
            '<td width="50px" style="padding-top: 2px;padding-right: 5px;text-align:right;{[this.getStyle(values)]}" valign="top">',
            '<div style="width:50px;display:block">{sp_flag_name}</div>',//审批标识

            '</tr>',
            '<tr height="20px"><td colspan="2" style="padding-top: 2px;padding-right: 5px;text-align:right;{[this.getStyle(values)]}" width="100%">',
            '<tpl if="approveUser"><div style="display:block;font-size: 12px;">({approveUser})</div></tpl></td>',
            '</td></tr>',
            '<tr  height="58px">',
            '<td style="font-size: 15px;padding: 0px;padding-top: 2px;padding-left: 5px;" valign="bottom"><img height="16" width="16" src="../../../images/person.png">&nbsp{number}',//数量
            '</td>',
            '<td valign="bottom" align="right" style="padding-top: 2px;padding-right: 5px;">',
            '<div><tpl if="KqDataSp.approvalMessage==\'1\'&&process==\'0\'"><img width="23"style="float:left;" title="' + kq.dataAppeal.appProcessErr + '" height="20" src="../images/noApprove.png" ></tpl>',//上报流程
            '<tpl if="KqDataSp.approvalMessage==\'1\'&&process==\'1\'"><img width="23"style="float:left;" title="' + kq.dataAppeal.appProcess + '" height="20" src="../images/approve.png" onclick="KqDataSp.approvalProcessClick(event,\'{org_id}\')"></tpl>',//上报流程
            '<img width="23" title="' + kq.label.messageNotice + '" height="20" src="/images/new_module/smallemail.png" onclick="KqDataSp.openEmail(event,\'{clerk_name}\',\'{clerk_id}\')">',//姓名
            '</div></td>',
            '</tr>',
            '</table>',
            '</div>',
            '<div id="orgDiv{org_id}" style=" float: right;height: 100%;width: 20px;display:none;">',//右侧按钮div
            '<tpl if="hasNextApprover==\'0\'&&operation==\'1\'"> <img TITLE="' + kq.label.agree + '" src="/images/new_module/smallagree.png" style="cursor:pointer;margin-bottom: 3px;margin-left: 0px" onclick="KqDataSp.operKqData(\'approve\',\'{org_id}\')"/></tpl>',
            '<tpl if="hasNextApprover==\'1\'&&operation==\'1\'"><img TITLE="' + kq.label.appeal + '" src="/images/new_module/smallappeal.png" style="cursor:pointer;margin-bottom: 3px;margin-left: 0px" onclick="KqDataSp.operKqData(\'appeal\',\'{org_id}\')"/></tpl>',
            '<tpl if="operation==\'1\'"><img title="' + kq.label.back + '" src="/images/new_module/smallback.png" style="cursor:pointer;margin-bottom: 3px;margin-left: 0px" onclick="KqDataSp.openReject(\'{org_id}\')"/></tpl>',
            '<tpl if="operation==\'3\'"><img title="' + kq.label.newData + '" src="/images/new_module/smallcreate.png" style="cursor:pointer;margin-bottom: 3px;margin-left: 0px" onclick="KqDataSp.createNewKQ(\'{org_id}\')"/></tpl>',
            '</div>',
            '</div>',

            '</tpl>', {
                getStyle: function (values) {
                    var sp_flag = values.sp_flag;
                    var sp_flag_name = values.sp_flag_name;
                    var process=values.process;
                    var style = "";
                    switch (sp_flag) {
                        case '00': {
                            style = "color:#9299A9";
                        }
                            break;
                        case '01': {
                            style = "color:#9299A9";
                        }
                            break;
                        case '02': {
                            style = "color:#54A976";
                        }
                            break;
                        case '07': {
                            style = "color:#FF0000";
                        }
                            break;
                        case '03': {
                            style = "color:#F6A623";
                        }
                            break;
                        case '06': {
                            style = "color:#9299A9";
                        }
                            break;
                        case '08': {
                            style = "color:#9299A9";
                        }
                            break;

                    }
                    if (sp_flag_name == "已提交(审核)") {
                        style += ";font-size: 13px;"
                    } else {
                        style += ";font-size: 15px;"
                    }

                    return style;
                }
            }
        );

        //页面数据store
        var store = Ext.create('Ext.data.Store', {
            storeId: 'itemMainStore',
            fields: ['org_id', 'org_name', 'sp_flag', 'sp_flag_name', 'number'],
            data: KqDataSp.org_list
        });


        var dataView = Ext.create('Ext.view.View', {
            itemSelector: '.aaa',//没用到 没有还报错
            scrollable: 'y',
            tpl: tpl,
            layout: 'fit',
            style: 'background-color:#F7F7F7 ',
            deferEmptyText: false,
            border: false,
            store: store,
            multiSelect: false
        });

        //快速查询
        var SearchBox = Ext.create("EHR.querybox.QueryBox", {
            id: 'kqDataSpSearchBox',
            hideQueryScheme: true,
            emptyText: kq.label.searchFromOrgId,
            callBackScope: this,
            success: function (inputValues) {
                var values = inputValues.inputValues;
                var t = "";
                for (var i = 0; i < values.length; i++) {
                    t += getEncodeStr(values[i]) + ",";
                }
                KqDataSp.queryValue = t;
                KqDataSp.reLoadData();
            }//重新加载数据列表//重新加载数据列表
        });
        
        // 51390 初始的时候没有更改规则：只要有一个机构没创建 就显示新建按钮
        var isCanCreate = false;
        for (var i = 0; i < KqDataSp.org_list.length; i++) {
            var data = KqDataSp.org_list[i];
            //如果有未创建的机构考勤，则显示批量新建的按钮 
            if (data.sp_flag == "00") {
                isCanCreate = true;
                break;
            }
        }
        
        var schemeName = "";
        Ext.each(KqDataSp.scheme_list, function (record) {
            if (record.scheme_id == KqDataSp.scheme_id) {
                schemeName = record.name;
                KqDataSp.secondary_admin = record.secondary_admin;
            }
        });
        // 导航菜单
        var menu = Ext.create('Ext.menu.Menu', {
            items: [
//                new Ext.menu.Item({
//                    text: kq.label.compute,//"计算"
//                    handler: function () {
//                    }
//                }),
                new Ext.menu.Item({
                	id: 'submitid',
                    text: kq.label.submits,//"批量归档"
                    hidden: !("1"==KqDataSp.secondary_admin && "1"==KqDataSp.privs.submitp),
                    handler: function () {
                        KqDataSp.submitKqData();
                    }
                }),
                new Ext.menu.Item({
                    text: kq.label.submitConfig,//"归档方案配置"
                    handler: function () {
                        KqDataSp.openSumitDataScheme();
                    }
                }),
                new Ext.menu.Item({
                	id: 'exportConfigid',
                    text: kq.label.exportConfig,//"输出方案配置"
                    hidden:KqDataSp.role != 1,
                    handler: function () {
                        KqDataSp.exportExcelScheme();
                    }
                })
            ]
        });
        var hiddenFlag = KqDataSp.role == 1 && isCanCreate && KqDataSp.secondary_admin == "1" ? false : true;
        var downwardHiddenFlag = KqDataSp.role == 1 && KqDataSp.secondary_admin == "1" ? false : true;
        // 应急中心个性化下发
        if("0"==KqDataSp.privs.hlwyjzx_flag)
        	downwardHiddenFlag = true;
        var mainPanel = Ext.create('Ext.Panel', {
            layout: 'fit',
            border: false,
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'top',

                border: false,
                items: [
                    {
                        text: kq.label.navigation,//"功能导航"
                        menu: menu,
                        height: 22
                    },
                    {
                        text: kq.label.batchNewData,//"批量新建"
                        width: 80,
                        height: 22,
                        id: 'kqAddbtn',
                        hidden: hiddenFlag,
                        handler: function () {
                            KqDataSp.createNewKQ();
                        }
                    },
                    {
                        text: kq.label.compute,//"计算"
                        width: 80,
                        height: 22,
                        id: 'computesid',
                        hidden: !hiddenFlag,
                        handler: function () {
                        	KqDataSp.batchCompute();
                        }
                    },
                    {
                        text: kq.label.downward,//"下发"
                        width: 80,
                        height: 22,
                        id: 'downwardid',
                        hidden: downwardHiddenFlag,
                        handler: function () {
                        	KqDataSp.operKqDataIng("downward", "");
                        }
                    }, SearchBox, '->',
                    {
                        xtype: 'label',
                        width: 20,
                        height: 22,
                        //数据明细
                        html: '<img src="/images/new_module/listview.png" title="' + kq.label.dataDetail + '" style="cursor:pointer;" onclick="KqDataSp.openKqMx(0,0,0,undefined,1)" />'
                    }
                ]
            }],
            items: dataView
        });
        
        var numMap = KqDataSp.getStartNumMap();
        KqDataSp.kq_year = return_data.kq_year;
        KqDataSp.kq_duration = return_data.kq_duration;


        var labelHtml = kq.label.searchProject + "：";
        //全部
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"00\");' id='checkNum_a' class='checkUnderline' style='color: #EA8E3B !important'>" + kq.label.SpflagAll +
            "(<span id='checkNum10'>" + numMap.get("10") + "</span>)</a>";

        var hiddenStyle = "display:inline;";
        //【45789】 考勤方案中下级机构的状态改为全部显示
//        if (KqDataSp.secondary_admin == '0') {
//            hiddenStyle="display:none;"
//        }
        //未提交
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"01\");' id='checkNum_a01' class='checkNoUnderline' style='"+hiddenStyle+"' >" + kq.label.SpflagNoAppeal +
            "(<span id='checkNum01'>" + numMap.get("01") + "</span>)</a>";
        //已提交
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"02\");' id='checkNum_a02' class='checkNoUnderline'  style='"+hiddenStyle+"'>" + kq.label.SpflagAppealed +
            "(<span id='checkNum02'>" + numMap.get("02") + "</span>)</a>";
        
        //退回
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"07\");' id='checkNum_a07' class='checkNoUnderline'  style='"+hiddenStyle+"'>" + kq.label.SpflagBack +
            "(<span id='checkNum07'>" + numMap.get("07") + "</span>)</a>";

        //已批准
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"03\");' id='checkNum_a03' class='checkNoUnderline'>" + kq.label.SpflagApprove +
            "(<span id='checkNum03'>" + numMap.get("03") + "</span>)</a>";
        //已归档
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"06\");' id='checkNum_a06' class='checkNoUnderline' >" + kq.label.SpflagSubmit +
            "(<span id='checkNum06'>" + numMap.get("06") + "</span>)</a>";
        // 我的待办
        labelHtml += " | <a href='javascript:KqDataSp.doCheck(\"current\");' id='checkNum_acurrent' class='checkNoUnderline' >" + kq.dataAppeal.upcoming +
            "(<span id='checkNumcurrent'>" + numMap.get("current") + "</span>)</a>";
        //已办
        labelHtml += " <a href='javascript:KqDataSp.doCheck(\"done\");' id='checkNum_adone' class='checkNoUnderline' style='"+hiddenStyle+"'>" + kq.label.done +
            "(<span id='checkNumdone'>" + numMap.get("done") + "</span>)</a>";
        
        var label = Ext.create('Ext.form.Label', {
            id: 'schemeLabel',
            html: labelHtml
        });


        //方案下拉列表 若仅有一个方案 无需显示下拉列表
        var schemeCompHtml = "";
        if (KqDataSp.scheme_list.length > 1) {
            schemeCompHtml = "<a href='javascript:KqDataSp.openSchemeComp();' id=\"kq_schemeid_a\" style='margin-left: 3px'>"
                + schemeName + "&nbsp;&nbsp;&nbsp;</a>" +
                "<img src='/workplan/image/jiantou.png' style='cursor:pointer;' onclick='KqDataSp.openSchemeComp();' /> ";
        } else {
            schemeCompHtml = "<div id=\"kq_schemeid_a\" style='margin-left: 3px;color: #1B4A98'>"
                + schemeName + "&nbsp;&nbsp;&nbsp;</div>";
        }


        var panel = Ext.create('Ext.Panel', {
            layout: 'fit',
            border: false,
            bodyBorder: false,
            header: {
                xtype: 'header',
                height: 30,
                itemPosition: 0,
                style: 'background-color: #F4F4F4;',
                layout: 'hbox',
                items: [
                    {
                        xtype: "panel",
                        width: 100,
                        height: 30,
                        border: false,
                        bodyStyle: 'background-color: #F4F4F4;',
                        html: "<a href='javascript:KqDataSp.openMonthComp();' id=\"kq_year_a\" style='margin-left: 3px'>"
                        + KqDataSp.kq_year + kq.dataAppeal.year + KqDataSp.kq_duration + kq.dataAppeal.month + "&nbsp;&nbsp;&nbsp;</a>" +
                        "<img src='/workplan/image/jiantou.png' style='cursor:pointer' onclick='KqDataSp.openMonthComp();' /> "
                    },
                    {
                        xtype: "panel",
                        width: 400,
                        height: 30,
                        border: false,
                        bodyStyle: 'background-color: #F4F4F4;',
                        html: schemeCompHtml
                    }
                ]

            },
            items: [{
                xtype: 'panel',
                layout: 'fit',
                border: false,
                bodyBorder: false,
                items: mainPanel,
                dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'top',
                    border: false,
                    items: [label]
                }]
            }]
        });

        Ext.create('Ext.container.Viewport', {
            style: 'backgroundColor:white',
            layout: 'fit',
            items: panel
        });
    },
    //重新加载页面数据 reLoadTool true:刷新状态过滤栏
    reLoadData: function (reLoadTool) {
        var jsonStr = {
            type: 'main',
            status: KqDataSp.selectStatus,
            scheme_id: KqDataSp.scheme_id,
            kq_duration: KqDataSp.kq_duration,
            kq_year: KqDataSp.kq_year,
            query: KqDataSp.queryValue
        };
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(jsonStr));
        Rpc({
            functionId: 'KQ00021201', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);

                var return_data = returnStr.return_data;
                KqDataSp.kq_year = return_data.kq_year;
                KqDataSp.kq_duration = return_data.kq_duration;
                KqDataSp.scheme_id = return_data.scheme_id;
                KqDataSp.org_list = return_data.org_list;
                KqDataSp.year_list = return_data.year_list;
                KqDataSp.scheme_list = return_data.scheme_list;
                KqDataSp.role = return_data.role;
                var isCanCreate = false;
                for (var i = 0; i < KqDataSp.org_list.length; i++) {
                    var data = KqDataSp.org_list[i];
                    //如果有未创建的机构考勤，则显示批量新建的按钮 haosl
                    if (data.sp_flag == "00") {
                        isCanCreate = true;
                        break;
                    }
                }
                var hiddenFlag = KqDataSp.role == 1 && isCanCreate && KqDataSp.secondary_admin == "1" ? false : true;
                Ext.getCmp('kqAddbtn').setHidden(hiddenFlag);
                // 计算 
                Ext.getCmp("computesid").setHidden(!hiddenFlag);
                // 下发按钮
                var downwardHiddenFlag = KqDataSp.role == 1 && KqDataSp.secondary_admin == "1" ? false : true;
                if("0" == KqDataSp.privs.hlwyjzx_flag){
                	Ext.getCmp("downwardid").setHidden(true);
                }else{
                	Ext.getCmp("downwardid").setHidden(downwardHiddenFlag);
                }
                // 输出方案
                Ext.getCmp("exportConfigid").setHidden(KqDataSp.role!=1);
                
                var itemMainStore = Ext.StoreMgr.get('itemMainStore');
                itemMainStore.loadData(KqDataSp.org_list);

                if (Ext.getCmp("kqMonthComp") != undefined) {
                    Ext.getCmp("kqMonthComp").setTotalData(KqDataSp.year_list);
                    Ext.getCmp("kqMonthComp").reloadComp();
                    Ext.getCmp("kqMonthComp");
                }

                if (true == reLoadTool) {
                    var numMap = KqDataSp.getStartNumMap();
                    Ext.getDom("checkNum10").innerHTML = numMap.get("10");
                    if (KqDataSp.secondary_admin == '1') {
                        // 需要上报则显示归档按钮 并且校验功能授权
                        if("1"==KqDataSp.privs.submitp)
                        	Ext.getCmp("submitid").setHidden(false);
                    }else{
                        // 不需要上报则不显示归档按钮
                        Ext.getCmp("submitid").setHidden(true);
                    }
                    
                    Ext.getDom("checkNum01").innerHTML = numMap.get("01");
                    Ext.getDom("checkNum02").innerHTML = numMap.get("02");
                    Ext.getDom("checkNumdone").innerHTML = numMap.get("done");
                    if (KqDataSp.role != '2') {
                    	Ext.getDom("checkNum07").innerHTML = numMap.get("07");
                    }
                    Ext.getDom("checkNum03").innerHTML = numMap.get("03");
                    Ext.getDom("checkNum06").innerHTML = numMap.get("06");
                    Ext.getDom("checkNumcurrent").innerHTML = numMap.get("current");
                    
                    // 57087 操作之后任然要看到查询框条件范围内的机构
//                  KqDataSp.queryValue = ""
//                  Ext.getCmp("kqDataSpSearchBox").removeAllKeys();
                    if(!Ext.isEmpty(KqDataSp.queryValue)){
                    	KqDataSp.reLoadData();
                    }
                }
            }
        }, map);
    },

    //切换状态过滤栏颜色
    doCheck: function (status) {
        var aid = "checkNum_a" + KqDataSp.selectStatus;
        Ext.getDom(aid).style.cssText = "";
        var bool = (status == '00');
        if (bool) {
            KqDataSp.selectStatus = '';
        } else {

            KqDataSp.selectStatus = status;
        }
        aid = "checkNum_a" + KqDataSp.selectStatus;
        Ext.getDom(aid).style.cssText = "color: #EA8E3B !important";
        // 只有点击全部时更新状态栏
        KqDataSp.reLoadData(bool);

    },
    //新建
    createNewKQ: function (org_id) {
        if(KqDataSp.org_list.length==0){
            Ext.showAlert(kq.msg.haveNotAddOrg);
            return;
        }
        if (!org_id) {
            org_id = "";
            for (var i = 0; i < KqDataSp.org_list.length; i++) {
                var data = KqDataSp.org_list[i];
                if (data.sp_flag == "00") {
                    org_id += data.org_id+",";
                }
            }
            if (!Ext.isEmpty(org_id)){
                org_id = org_id.substring(0,org_id.length-1);
            }else{
                Ext.showAlert(kq.msg.haveNotAddOrg);
                return;
            }
        }
        var jsonStr = {
            type: 'create',
            scheme_id: KqDataSp.scheme_id,
            kq_duration: KqDataSp.kq_duration,
            kq_year: KqDataSp.kq_year,
            org_id: org_id
        };
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(jsonStr));
        Rpc({
            functionId: 'KQ00021201', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);

                var return_code = returnStr.return_code;
                if (return_code == 'success') {
                	// 如果有计算权限则  新建完直接计算一遍
                	if("1" == KqDataSp.privs.computep){
                		KqDataSp.operKqData("compute", org_id);
                		Ext.getCmp("computesid").setHidden(false);
                		Ext.getCmp("downwardid").setHidden(false);
                	}else{
	                    Ext.showAlert(kq.msg.addSuccess);
	                    KqDataSp.reLoadData();
                	}
                }
            }
        }, map);
    },
    //*************显示隐藏报批驳回按钮方法*****************
    mouseOver: function (index) {
        Ext.getDom("orgDiv" + index).style.display = "inline";
    },
    mouseOut: function (index) {
        Ext.getDom("orgDiv" + index).style.display = "none";
    },
    //*************显示隐藏报批驳回按钮方法结束*****************

    //数据操作
    //action: reject驳回  approve批准 appeal报批 compute计算
    operKqData: function (action, org_id, user_id, role_id) {
    	
    	var optStr = "";
        switch (action){
            case 'appeal':
            	optStr = kq.datamx.label.kqDataSp.appeal;
                approvalMessage=kq.datamx.msg.verifyMessage;
                sp_flag = "02";
                break;
//            case 'reject':
//                optStr = kq.datamx.label.kqDataSp.reject;
//                break;
            case 'approve':
                optStr = kq.datamx.label.kqDataSp.approve;
                approvalMessage=kq.datamx.msg.agree;
                sp_flag = "03";
                break;
            case 'submit':
                optStr = kq.label.submit;
                break;
//            case 'compute':
//                optStr = kq.datamx.label.kqDataSp.compute;
//                break;
            default:
                break;
        };
        if(Ext.isEmpty(optStr)){
        	KqDataSp.operKqDataIng(action, org_id, user_id, role_id);
        }else{
        	var msg = kq.datamx.label.kqDataSp.confirmmsg.replace("{0}",optStr);
        	if (KqDataSp.approvalMessage == "1") {
	        	if (action=="appeal"||action=="approve") {
	        		Ext.create("Ext.window.Window",{
	                    height: 300,
	                    width: 450,
	                	title:"填写意见",
	                    layout: 'vbox',
	                    id: 'msgWin',
	                    align: 'stretch',
	                    scrollable: false,
	                    modal: true,
	                    border: false,
	                    closeAction: 'destroy',
	                    defaults: {
	                        width: 440,
	                        border: false
	                    },
	                    items: [{
	                        xtype: 'panel',
	                        height: 230,
	                        border: false,
	                        layout: 'fit',
	                        items: {
	                            xtype: 'textareafield',
	                            id: 'msgText',
	                            value:approvalMessage
	                        }
	                    }],
	                    bbar: [ 
	                        '->',
	                        { 
	                        	xtype: 'button',
	                        	style:'margin-right:5px;',
	                        	text: kq.button.ok,
	                        	width:75,
	                        	height:25,
	                        	handler:function(){
	                        	 var map = new HashMap();
		            		     var json = {};
		            		     json.type="fillProcess";
		            		     json.org_id=org_id;
		            		     json.scheme_id = KqDataSp.scheme_id;
		            		     json.kq_duration = KqDataSp.kq_duration;
		            		     json.kq_year = KqDataSp.kq_year;
		            		     json.sp_message =Ext.getCmp("msgText").value;
		            		     json.sp_flag=sp_flag;
		            		     map.put("jsonStr", Ext.encode(json));
		            		        Rpc({
		            		            functionId: 'KQ00021201', async: true, success: function (form) {
	            		            	 var result = Ext.decode(form.responseText);
	            		                 var returnStr = eval(result.returnStr);
	            		                 var return_code = returnStr.return_code;
	            		                 if (return_code == 'success') {
	            		                	 var msgWin = Ext.getCmp("msgWin");
		         	                            if(msgWin){
		         	                            	msgWin.destroy();
		         	                            }
	            		                	 KqDataSp.operKqDataIng(action, org_id, user_id, role_id); 
	            		                 }
		            		            }
		            		        },map);
	                        } },
	                        { xtype: 'button', text: kq.button.cancle,width:75,height:25,handler:function(){
	                            var msgWin = Ext.getCmp("msgWin");
	                            if(msgWin){
	                            	msgWin.destroy();
	                            }
	                        }},
	                        '->'
	                    ]
	                }).show();
				}else{
		        	Ext.showConfirm(msg,function(flag){
		        		if(flag=="yes"){
		        			KqDataSp.operKqDataIng(action, org_id, user_id, role_id);
		        		}
		        	});
				}
        	}else{
	        	Ext.showConfirm(msg,function(flag){
	        		if(flag=="yes"){
	        			KqDataSp.operKqDataIng(action, org_id, user_id, role_id);
	        		}
	        	});
			}
        }
    },
    
    //action: reject驳回  approve批准 appeal报批 compute计算
    operKqDataIng: function (action, org_id, user_id, role_id) {
    	
		Ext.MessageBox.wait(kq.label.wait, kq.label.waiting);
		if (user_id == undefined) {
			user_id = "";
		}
		if (role_id == undefined) {
			role_id = "";
		}
		var jsonStr = {
				type: action, scheme_id: KqDataSp.scheme_id, viewtype: '1',
				kq_duration: KqDataSp.kq_duration, kq_year: KqDataSp.kq_year, org_id: org_id,
				user_id: user_id, role_id: role_id
		};
		// 计算 默认不覆盖
		var computeflag = ("compute"==action);
		if(computeflag){
			jsonStr.coverDataFlag = KqDataSp.coverDataFlag;
		}
		// 审批页面的计算目前只支持批量计算  传org_id
		if(computeflag && Ext.isEmpty(org_id)){
			org_id = "";
			Ext.each(KqDataSp.org_list, function (record) {
                org_id += record.org_id + ",";
            });
			jsonStr.org_id = org_id;
		}
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(jsonStr));
		Rpc({
			functionId: 'KQ00021201', async: true, success: function (form) {
				var result = Ext.decode(form.responseText);
				var returnStr = eval(result.returnStr);
				var return_code = returnStr.return_code;
				Ext.MessageBox.close();
				
				if (return_code == 'success') {
					// 新建后计算需要更新状态栏
					KqDataSp.reLoadData(true);
				} else {
					Ext.showAlert(returnStr.return_msg);
				}
			}
		}, map);
    },

    /**
     * 打开驳回窗口
     * @param org_id
     */
    openRejectWindow: function (action, org_id, userid, role_id) {
	        		Ext.create("Ext.window.Window",{
	                    height: 300,
	                    width: 450,
	                	title:"填写意见",
	                    layout: 'vbox',
	                    id: 'msgWin',
	                    align: 'stretch',
	                    scrollable: false,
	                    modal: true,
	                    border: false,
	                    closeAction: 'destroy',
	                    defaults: {
	                        width: 440,
	                        border: false
	                    },
	                    items: [{
	                        xtype: 'panel',
	                        height: 230,
	                        border: false,
	                        layout: 'fit',
	                        items: {
	                            xtype: 'textareafield',
	                            id: 'msgText',
	                            value:kq.datamx.msg.disagree
	                        }
	                    }],
	                    bbar: [ 
	                        '->',
	                        { 
	                        	xtype: 'button',
	                        	style:'margin-right:5px;',
	                        	text: kq.button.ok,
	                        	width:75,
	                        	height:25,
	                        	handler:function(){
	                        	 var map = new HashMap();
		            		     var json = {};
		            		     json.type="fillProcess";
		            		     json.org_id=org_id;
		            		     json.scheme_id = KqDataSp.scheme_id;
		            		     json.kq_duration = KqDataSp.kq_duration;
		            		     json.kq_year = KqDataSp.kq_year;
		            		     json.sp_message =Ext.getCmp("msgText").value;
		            		     json.sp_flag="07";
		            		     map.put("jsonStr", Ext.encode(json));
		            		        Rpc({
		            		            functionId: 'KQ00021201', async: true, success: function (form) {
		            		            	 var result = Ext.decode(form.responseText);
		            		                 var returnStr = eval(result.returnStr);
		            		                 var return_code = returnStr.return_code;
		            		                 if (return_code == 'success') {
		            		                	 var msgWin = Ext.getCmp("msgWin");
		         	                            if(msgWin){
		         	                            	msgWin.destroy();
		         	                            }
		         	                           KqDataSp.operKqDataIng('reject', org_id, userid, role_id); 
		            		                 }
		            		            }
		            		        },map);
	                        } },
	                        { xtype: 'button', text: kq.button.cancle,width:75,height:25,handler:function(){
	                            var msgWin = Ext.getCmp("msgWin");
	                            if(msgWin){
	                            	msgWin.destroy();
	                            }
	                        }},
	                        '->'
	                    ]
	                }).show();

    },
  //显示驳回
  openReject: function (org_id) {
        Ext.require('KqDataURL.KqDataRejectWindow', function () {
            Ext.create("KqDataURL.KqDataRejectWindow", {
                scheme_id: KqDataSp.scheme_id,
                org_id: org_id,
                viewType: "1",
                rejectFunction: eval(function (org_id, userid, role_id) {
                	if (KqDataSp.approvalMessage == "1") {
                		KqDataSp.openRejectWindow('reject', org_id, userid, role_id);
					}else{
						KqDataSp.operKqData('reject', org_id, userid, role_id);
					}
                })
            });
        });

    },
    //显示月份过滤面板
    openMonthComp: function () {

        var window = Ext.getCmp("dateWin");
        if (window == undefined) {
            var date = new Date;

            var p = Ext.create("EHR.attendanceMonth.AttendanceMonthComp", {
                totalData: KqDataSp.year_list,
                currentYear: KqDataSp.kq_year,
                currentMonth: Number(KqDataSp.kq_duration),
                border: false,
                id: 'kqMonthComp',
                onMonthSelected: function (value) {
                    KqDataSp.kq_year = value.year;
                    KqDataSp.kq_duration = value.kq_duration;
                    Ext.getDom('kq_year_a').innerHTML = KqDataSp.kq_year + kq.dataAppeal.year + value.monthOrder + kq.dataAppeal.month + "&nbsp;&nbsp;";
                    window.hide();
                    // 切换月份时默认显示全部页签
                    KqDataSp.doCheck("00");
                }

            });

            window = Ext.widget("window", {
                height: 175,
                width: 270,
                layout: 'fit',
                x: 0,
                y: 30,
                scrollable: false,
                header: false,
                modal: false,
                id: 'dateWin',
                border: false,
                closeAction: 'destroy',
                items: [p],
                listeners: {
                    "render": function () {
                        document.getElementById("dateWin").onmouseout = function (e) {
                            if (e == undefined) {
                                e = event;
                            }
                            var s = e.toElement || e.relatedTarget;
                            if (s == undefined || !this.contains(s)) {
                                window.hide();
                            }
                        };
                    }
                }

            });
            window.show();
        } else {
            if (window.hidden == false) {
                window.hide();
            } else {
                Ext.getCmp('kqMonthComp').setCurrentYear(KqDataSp.kq_year);
                Ext.getCmp('kqMonthComp').reloadComp();
                window.show();
            }
        }

    },
    //显示方案过滤面板
    openSchemeComp: function () {

        var window = Ext.getCmp("schemeCompWin");
        if (window == undefined) {
            var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                '<div  style="white-space:nowrap;height:auto;width:auto;cursor:pointer;margin-bottom: 3px;margin-left: 3px;margin-right: 3px;margin-bottom: 5px" >' +
                ' {name} </div>',
                '</tpl>'
            );
            //方案数据store
            var schemeStore = Ext.create('Ext.data.Store', {
                storeId: 'schemeListStore',
                fields: ['name', 'scheme_id'],
                data: KqDataSp.scheme_list
            });
            var dataView = Ext.create('Ext.view.View', {
                itemSelector: 'div',
                scrollable: 'y',
                tpl: tpl,
                layout: 'fit',
                deferEmptyText: false,
                overItemCls: 'spOverCls',
                border: false,
                selectedItemCls: 'spSelectedCls',
                store: schemeStore,
                multiSelect: false,
                listeners: {
                    select: function (t, record) {
                        KqDataSp.scheme_id = record.data.scheme_id;
                        KqDataSp.secondary_admin = record.data.secondary_admin;
                        Ext.getDom('kq_schemeid_a').innerHTML = record.data.name + "&nbsp;&nbsp;&nbsp;";
                        window.hide();
                        // 切换方案时默认显示全部页签
                        KqDataSp.doCheck("00");
                    }
                }
            });
            window = Ext.widget("window", {
                layout: 'fit',
                x: 105,
                y: 30,
                minWidth: 80,
                maxHeight:400,
                scrollable: true,
                header: false,
                modal: false,
                id: 'schemeCompWin',
                border: false,
                closeAction: 'destroy',
                items: [dataView],
                listeners: {
                    "render": function () {
                        document.getElementById("schemeCompWin").onmouseout = function (e) {
                            if (e == undefined) {
                                e = event;
                            }
                            var s = e.toElement || e.relatedTarget;
                            if (s == undefined || !this.contains(s)) {
                                window.hide();
                            }
                        };
                    }
                }

            });
            window.show();
        } else {
            if (window.hidden == false) {
                window.hide();
            } else {
                window.show();
            }
        }

    },
    //卡片移入添加阴影
    divMouseOver: function (e) {
        e.className = "kqShadow";
    },
    //卡片移出取消阴影
    divMouseOut: function (e) {
        var eve = arguments.callee.caller.arguments[0] || window.event
        var s = eve.toElement || eve.relatedTarget;

        if (s == undefined || !e.contains(s)) {
            e.className = "";
        }
    },
    //打开email窗口
    openEmail: function (event, clerk_name, clerk_id) {

        if (Ext.isIE) {
            event.cancelBubble = true
        } else {
            event.stopPropagation();
        }

        var jsonStr = {
            type: "msgConfig", scheme_id: KqDataSp.scheme_id, username: clerk_id,
            kq_duration: KqDataSp.kq_duration, kq_year: KqDataSp.kq_year
        };
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(jsonStr));
        Rpc({
            functionId: 'KQ00021201', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;
                var return_data = returnStr.return_data;
                var msgConfig = return_data.msgconfig;

                if (msgConfig.a0101 != '') {
                    clerk_name = msgConfig.a0101;
                }
                if (return_code == 'success') {
                    KqDataSp.showMsgWin(msgConfig, clerk_name, clerk_id);
                } else {
                    Ext.showAlert(returnStr.return_msg);
                }

            }
        }, map);

    },

    //打开发送邮件窗口
    showMsgWin: function (msgConfig, clerk_name, clerk_id) {

        if (msgConfig.email == '0' && msgConfig.phone == '0' && msgConfig.wechat == '0' && msgConfig.dingtalk == '0') {
            //"考勤员'"+clerk_name+"'未配置通讯参数！"
            Ext.showAlert(kq.msg.noMessageConfig.replace("{0}", clerk_name));
            return;
        }
        var window = Ext.widget("window", {
            height: 400,
            width: 600,
            title: kq.label.messageNotice,//'消息通知'
            layout: 'vbox',
            id: 'msgWin',
            align: 'stretch',
            scrollable: false,
            modal: true,
            border: false,
            closeAction: 'destroy',
            defaults: {
                width: 590,
            },
            items: [{
                xtype: 'panel',
                flex: 1,
                border: false,
                html: kq.label.receiver + '：' + clerk_name//接收人
            }, {
                xtype: 'panel',
                height: 300,
                border: false,
                layout: 'fit',
                items: {
                    xtype: 'textarea',
                    id: 'msgText'
                }
            }, {
                xtype: 'panel',
                border: false,
                layout: 'hbox',
                flex: 1,
                items: [
                    {
                        xtype: 'panel',
                        border: false,
                        layout: 'hbox',
                        width: 200,
                        defaults: {
                            width: 50,
                            labelWidth: 30
                        },
                        items: [
                            {
                                xtype: 'checkboxfield',
                                boxLabel: kq.label.email,//'邮件'
                                id: 'emailCheck',
                                hidden: msgConfig.email == '0' ? true : false,
                                checked: msgConfig.email == '0' ? false : true
                            },
                            {
                                xtype: 'checkboxfield',
                                boxLabel: kq.label.SMS,// '短信'
                                hidden: msgConfig.phone == '0' ? true : false,
                                id: 'phoneCheck'
                            },
                            {
                                xtype: 'checkboxfield',
                                boxLabel: kq.label.weChat,//'微信'
                                hidden: msgConfig.wechat == '0' ? true : false,
                                id: 'weChatCheck'
                            },
                            {
                                xtype: 'checkboxfield',
                                boxLabel: kq.label.dingTalk,//'钉钉'
                                hidden: msgConfig.dingtalk == '0' ? true : false,
                                id: 'dingTalkCheck'
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: common.button.ok,//'确定'
                        width: 60,
                        height: 23,
                        margin: '4 5 0 30',
                        pack: 'center',
                        handler: function () {
                            if (Ext.getCmp("emailCheck").checked == true || Ext.getCmp("phoneCheck").checked == true ||
                                Ext.getCmp("weChatCheck").checked == true || Ext.getCmp("dingTalkCheck").checked == true) {

                                KqDataSp.sendMsg(clerk_id);
                            } else {
                                Ext.showAlert(kq.msg.selectSendType);//请选择消息类型！
                            }
                        }
                    }, {
                        xtype: 'button',
                        text: common.button.cancel,//'取消'
                        width: 60,
                        height: 23,
                        margin: '4 0 0 0',
                        pack: 'center',
                        handler: function () {
                            window.close();
                        }
                    }
                ]
            }],
            listeners: {
                afterrender: function () {
                    if (Ext.isIE) {
                        if (msgConfig.email == '0') {
                            KqDataSp.setDisabledStyle('emailCheck');
                        }
                        if (msgConfig.phone == '0') {
                            KqDataSp.setDisabledStyle('phoneCheck');
                        }
                        if (msgConfig.dingtalk == '0') {
                            KqDataSp.setDisabledStyle('dingTalkCheck');
                        }
                        if (msgConfig.wechat == '0') {
                            KqDataSp.setDisabledStyle('weChatCheck');
                        }
                    }
                }
            }

        });
        window.show();
    },
    //设置单元按钮半透明
    setDisabledStyle: function (id) {
        Ext.getDom(id + '-displayEl').setAttribute('style', 'filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
        Ext.getDom(id + '-boxLabelEl').setAttribute('style', 'filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
    },
    //发送邮件
    sendMsg: function (clerk_id) {

        var text = Ext.getCmp("msgText").getValue();
        if(text==undefined||trim(text)==""){
            Ext.showAlert(kq.msg.pleaseFillContext);//"请填写发送内容！"
            return;
        }

        var jsonStr = {
            type: 'sendmsg',
            scheme_id: KqDataSp.scheme_id,
            kq_duration: KqDataSp.kq_duration,
            kq_year: KqDataSp.kq_year,
            clerk_id: clerk_id,
            sendemail: Ext.getCmp("emailCheck").checked == true ? "1" : "0",
            sendphone: Ext.getCmp("phoneCheck").checked == true ? "1" : "0",
            sendweichat: Ext.getCmp("weChatCheck").checked == true ? "1" : "0",
            senddingtalk: Ext.getCmp("dingTalkCheck").checked == true ? "1" : "0",
            msgvalue: text
        };
        Ext.MessageBox.wait(kq.msg.sendingPleaseWait, common.button.promptmessage);

        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(jsonStr));
        Rpc({
            functionId: 'KQ00021201', async: true, success: function (form) {
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;
                if (return_code == 'success') {
                    Ext.showAlert(kq.msg.sendSuccess);//'发送成功！'
                    Ext.getCmp("msgWin").close();
                } else {
                    Ext.showAlert(returnStr.return_msg);
                }
            }
        }, map);

    },
    //打开归档方案配置
    openSumitDataScheme: function () {
        var map = new HashMap();
        map.put('type', 'initItemMapping');
        Rpc({
            functionId: 'KQ00021203', async: false, success: function (resp) {
                var result = Ext.decode(resp.responseText);

                var sourceFieldData = result.return_data.field_item_list;
                var mapping_list = result.return_data.mapping_list;
                var SubSetData = result.return_data.set_list;
                var fieldsetid = (result.return_data.fieldsetid == 'null' || !result.return_data.fieldsetid) ? '' : result.return_data.fieldsetid;
                var showSubSet = {};
                if (fieldsetid) {
                    for (var i = 0; i < SubSetData.length; i++) {
                        if (SubSetData[i].fieldsetid == fieldsetid) {
                            showSubSet.fieldsetid = fieldsetid;
                            showSubSet.fieldsetdesc = SubSetData[i].fieldsetdesc;
                        }
                    }
                }
                if (!SubSetData)
                    SubSetData = [];
                if (!sourceFieldData)
                    sourceFieldData = [];
                if (!mapping_list)
                    mapping_list = [];
                var data = JSON.stringify(sourceFieldData);
                data = data.replace(/item_id/g, 'itemid');
                data = data.replace(/item_name/g, 'itemdesc');
                data = data.replace(/item_type/g, 'itemtype');
                sourceFieldData = JSON.parse(data);
                for (var i = 0; i < mapping_list.length; i++) {
                    var to_item_id = mapping_list[i].to_item_id;
                    var item_name = mapping_list[i].item_name;
                    var item_id = mapping_list[i].item_id;
                    for (var j = 0; j < sourceFieldData.length; j++) {
                        if (item_id == sourceFieldData[j].itemid) {
                            sourceFieldData[j].valueitemid = to_item_id;
                            sourceFieldData[j].valuedesc = item_name;
                            break;
                        }
                    }
                }

                Ext.require('Ext.scheme.ArchiveSchemeConfig', function () {
                    Ext.create('Ext.scheme.ArchiveSchemeConfig', {
                        sourceFieldData: sourceFieldData,
                        SubSetData: SubSetData,
                        showSubSet: showSubSet,
                        // 54240 设置归档失效的指标提示信息
                        messages: result.return_data.messages,
                        filterItem:[showSubSet.fieldsetid+"z0",showSubSet.fieldsetid+"z1"],
                        callbackFn: KqDataSp.returnArichveOK
                    });
                })

            }
        }, map);
    },

    //打开数据明细
    openKqMx: function (org_id, operation, hasNextApprover, sp_flag,type) {

        if (type != undefined && type == 1) {
            org_id = "";
            operation = "0";
            Ext.each(KqDataSp.org_list, function (record) {
                org_id += record.org_id + ",";
                if (record.sp_flag == '02') {
                    operation = "2";
                }
            });
            hasNextApprover = "0";
        }

        var map = new HashMap();
        map.put("kqYear", KqDataSp.kq_year);
        map.put("kqDuration", KqDataSp.kq_duration);
        map.put("schemeId", KqDataSp.scheme_id);
        map.put("orgId", org_id);
        map.put("viewType", "1");
        map.put("operation", operation);
        map.put("showMx", "true");
        map.put("hasNextApprover", hasNextApprover);
        map.put("optRole", KqDataSp.role);
        map.put("sp_flag", sp_flag);
        map.put("callBackFunc", eval(function () {
        	if("00" == KqDataSp.selectStatus){
        		KqDataSp.reLoadData(true);
        	}else{
        		// 54701 由于返回事件 刷新时只考虑到当前条件 暂处理为 先刷新全部 再重新定位到当前所选类别
        		var selectStatusValue = KqDataSp.selectStatus;
        		KqDataSp.selectStatus = "00";
        		KqDataSp.reLoadData(true);
        		KqDataSp.selectStatus = selectStatusValue;
        		KqDataSp.reLoadData(false);
        		
        	}
        }));
        Ext.require('KqDataURL.KqDataMx', function () {
            Ext.create("KqDataURL.KqDataMx", map);
        });
    },
    /**保存考勤归档方案配置 */
    returnArichveOK: function (sourceFieldData, SubSetData, showSubSet) {
        var fieldsetid = showSubSet.fieldsetid;
        var mapping_list = new Array();
        for (var i = 0; i < sourceFieldData.length; i++) {
            var obj = {};
            obj.item_id = (!sourceFieldData[i].valueitemid || sourceFieldData[i].valueitemid == '　') ? '' : sourceFieldData[i].valueitemid;
            obj.to_item_id = sourceFieldData[i].itemid;
            mapping_list.push(obj);
        }
        var map = new HashMap();
        map.put('type', 'saveItemMapping');
        map.put('fieldsetid', fieldsetid);
        map.put('mapping_list', mapping_list);
        Rpc({
            functionId: 'KQ00021203', async: false, success: function (resp) {
                var result = Ext.decode(resp.responseText);
                if (result.return_code = 'success')
                    Ext.Msg.alert(kq.arichve.tip, kq.arichve.saveSuccess);
                else
                    Ext.Msg.alert(kq.arichve.tip, result.return_msg);
            }
        }, map);
    },
    //归档方法
    submitKqData: function () {
        var isHaveSubmitData = false;
        Ext.each(KqDataSp.org_list, function (record) {
            if (record.operation == '2') {
                isHaveSubmitData = true;
            }
        });
        if (!isHaveSubmitData) {
            Ext.showAlert(kq.msg.noSumbitData);//"没有可归档数据！"
            return;
        }
        var msg = kq.datamx.label.kqDataSp.confirmmsg.replace("{0}",kq.datamx.label.kqDataSp.submit);
    	Ext.showConfirm(msg,function(flag){
            if (flag =="yes"){
		        var jsonStr = {
		            type: "submit", scheme_id: KqDataSp.scheme_id, viewtype: '1',
		            kq_duration: KqDataSp.kq_duration, kq_year: KqDataSp.kq_year, org_id: ""
		        };
		        var map = new HashMap();
		        map.put("jsonStr", JSON.stringify(jsonStr));
		        Rpc({
		            functionId: 'KQ00021201', async: false, success: function (form) {
		                var result = Ext.decode(form.responseText);
		                var returnStr = eval(result.returnStr);
		                var return_code = returnStr.return_code;
		                if (return_code == 'success') {
		                    Ext.showAlert(kq.msg.sumbitSuccess);//"归档成功！"
		                    KqDataSp.reLoadData();
		                } else {
		                    Ext.showAlert(returnStr.return_msg);
		                }
		            }
		        }, map);
            }
            return;
        });
    },
    /**
     * 输出方案配置页面
     * haosl
     * */
    exportExcelScheme:function(){
        Ext.create("KqDataURL.KqExportSetting",{
            scheme_id:KqDataSp.scheme_id
        });
    },
    /**
     * 覆盖手工修改的数据 复选框单击触发事件
     * @param checkbox
     */
    checkboxOnclick :function (checkbox) {
        var me = this;
        if (checkbox.checked)
            me.coverDataFlag=0;
        else
            me.coverDataFlag=1;
    },
    /**
     * 批量计算
     */
    batchCompute:function(){
    	var checkedStr = ("0"==KqDataSp.coverDataFlag) ? "checked" : "";
    	var temp = "<div id='coverDataDiv'>" +
    			"<input onclick='KqDataSp.checkboxOnclick(this)' style='cursor: pointer;' type='checkbox' id='coverData' "+checkedStr+" />" +
    			"<label for='coverData' style='cursor: pointer;position:relative;bottom:2px;'>"+kq.datamx.label.kqDataSp.hasComputeData+"</label>" +
    					"</div>";
    	var msg = kq.datamx.label.kqDataSp.confirmmsg.replace("{0}", kq.datamx.label.kqDataSp.compute);
    	
    	Ext.showConfirm(temp+msg,function(flag){
        	var coverDataDiv = Ext.getDom("coverDataDiv");
           	if(coverDataDiv && coverDataDiv.parentNode){
           		coverDataDiv.parentNode.removeChild(coverDataDiv);
           	}
            if(flag=="yes"){
            	KqDataSp.operKqDataIng("compute");
            }else
            	return;
        });
    },
    /**
     * 获取各个状态的个数
     */
    getStartNumMap:function(){
    	var numMap = new HashMap();
        numMap.put("00", 0);
        numMap.put("01", 0);
        numMap.put("02", 0);
        // 当前用户已办
        numMap.put("done", 0);
        numMap.put("03", 0);
        numMap.put("06", 0);
        numMap.put("07", 0);
        numMap.put("10", 0);
        // 当前用户待办
        numMap.put("current", 0);
        Ext.each(KqDataSp.org_list, function (record) {
            var num = numMap.get(record.sp_flag);
            num += 1;
            numMap.put(record.sp_flag, num);
            num = numMap.get("10");
            num += 1;
            numMap.put("10", num);
            var sp_flagValue = record.sp_flag;
            // 待办
            if((KqDataSp.currentUser==record.approveUser && '02'==sp_flagValue)){
            	num = numMap.get("current");
            	num += 1;
                numMap.put("current", num);
            }
            // 处理已办
            if(KqDataSp.currentUser != record.approveUser) {
            	var bool = false;
            	// 考勤员
            	if(1 == KqDataSp.role){
            		// 需要上报 任是 02状态
            		if('1'==KqDataSp.secondary_admin && '02'==sp_flagValue){
            			bool = true;
            		}// 不需要上报 03
            		else if('0'==KqDataSp.secondary_admin && '03'==sp_flagValue){
            			bool = true;
            		}
            	}// 审核人
            	else if(2 == KqDataSp.role){
            		// 需要上报 03
            		if('1'==KqDataSp.secondary_admin && '03'==sp_flagValue){
            			bool = true;
            		}
            	}
            	if(bool){
            		num = numMap.get("done");
            		num += 1;
            		numMap.put("done", num);
            	}
            }
        });
        return numMap;
    },
    approvalProcessClick: function (event,org_id) {//上报过程点击事件
   	 if (Ext.isIE) {
            event.cancelBubble = true
        } else {
            event.stopPropagation();
        }
  	 	var map = new HashMap();
	     var json = {};
	     json.type="getProcess";
	     json.org_id=org_id;
	     json.scheme_id =KqDataSp.scheme_id;
	     json.kq_duration = KqDataSp.kq_duration;
	     json.kq_year = KqDataSp.kq_year;
	    map.put("jsonStr", Ext.encode(json));
    var value = [];
    Rpc({
        functionId: 'KQ00021201', success: function (res) {
        	var result = Ext.decode(res.responseText);
            var returnStr = eval(result.returnStr);
            var return_code = returnStr.return_code;
            if (return_code == "success") {
                value = returnStr.return_data.list;
            } else {
                Ext.Msg.alert(kq.dataAppeal.tip,kq.dataAppeal.appProcessErr);
                return;
            }
        }, scope: this, async: false
    }, map);
   //展示审批信息容器
   var fieldCmp = Ext.create("EHR.processViewer.ProcessViewer", {
   	processData:value,
   });
   //审批过程弹窗
   var approvalProcessWin = Ext.create("Ext.window.Window", {
       title: kq.dataAppeal.competitivePosition,
       height: 520,
       //autoHeight:true,
       width: 460,
       modal: true,
       resizable: false,
       items: [fieldCmp],
   });
   approvalProcessWin.show();
}
});