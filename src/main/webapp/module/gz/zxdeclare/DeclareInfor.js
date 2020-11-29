Ext.define('Declare.DeclareInfor', {
    extend: 'Ext.Panel',
    xtype: 'declareinfor',
    border: 0,
    flex: 1,
    layout: 'vbox',
    scrollable: 'y',
    constructor: function (config) {
        this.callParent();
        this.loadData(config);
    },
    loadData: function (config) {
        var vo = new HashMap();
        vo.put('type', 'search');
        vo.put('id', config.id);
        Rpc({functionId: 'GZ00000703', success: this.rendData, scope: this}, vo);
    },
    rendData: function (response) {
        var me = this;
        var respData = Ext.decode(response.responseText);
        me.returnData = respData.returnStr.return_data;
        // console.log(me.returnData);
        var declare_type = me.returnData.declare_type;
        //me.setTitle(me.tranlateZxdeclareType(declare_type));
        me.personInfo();
        me.declareInfo();
        me.descriptionInfo();
        if (declare_type == '01') {
            me.declareSubInfo(declare_type, '', gz.label.zxdeclare.childInfoMsg);
        } else if (declare_type == '06') {
            me.declareSubInfo(declare_type, 'support', gz.label.zxdeclare.supportedManInfoMsg);
            me.declareSubInfo(declare_type, 'comonsupport', gz.label.zxdeclare.comonsupportManInfoMsg);
        } else if (declare_type == '02') {
            me.declareSubInfo(declare_type, me.returnData.cuntin_edu_type, me.returnData.cuntin_edu_type == '01' ? gz.label.zxdeclare.continuingEdu : gz.label.zxdeclare.CareerEdu);
        } else if (declare_type == '03') {
            me.declareSubInfo(declare_type, '', gz.label.zxDeclareTypeHouseRent);
        } else if (declare_type == '04') {
            me.declareSubInfo(declare_type, '', gz.label.zxDeclareTypeInterestExpense);
        }
        me.attachInfo();
        if (me.returnData.approve_state == '02')
            me.desclareBtns();
    },
    /**个人信息**/
    personInfo: function () {
        var me = this;
        var person = Ext.create('Ext.panel.Panel', {
            width: '100%',
            border: 0,
            items: [{
                xtype: 'component',
                border: 0,
                width: '100%',
                html: '<div style="margin:0px 10px;padding:4px 0px;font-weight:blod;border-bottom:1px solid #c5c5c5;">' + gz.label.zxdeclare.selfInfo + '</div>'//个人信息
            }, {
                xtype: 'component',
                border: 0,
                width: '100%',
                margin: '10 0 10 0',
                html: '<div style="margin:0px 10px;padding:4px 0px 4px 24px;">' + me.returnData.name + '</div>'
            }]
        });
        this.add(person);
    },
    /**专项申报**/
    declareInfo: function () {
        var me = this;
        var infor = Ext.create('Ext.panel.Panel', {
            width: '100%',
            border: 0,
            items: [{
                xtype: 'component',
                border: 0,
                width: '100%',
                html: '<div style="margin:0px 10px;padding:4px 0px;font-weight:blod;border-bottom:1px solid #c5c5c5;">' + gz.label.zxdeclare.declareInfo + '</div>'//专项信息
            }]
        });

        var declare_type = me.returnData.declare_type;
        var declare = Ext.create('Ext.panel.Panel', {
            border: 0,
            width: '100%'
        });
        var deduct_type = me.returnData.deduct_type == '01' ? gz.label.zxdeclare.deductTypeMonth : gz.label.zxdeclare.deductTypeYear;//'按月抵扣':'按年抵扣';
        var deduct_money = me.returnData.deduct_money;
        var start_date = me.returnData.start_date;
        var end_date = me.returnData.end_date;

        if (declare_type == '01') {//子女教育
            var html = '<div style="margin-left:34px;margin-bottom:10px;">';
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductType + '</span><span style="margin-left:100px;">' + deduct_type + '</span></div>';//抵扣方式
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductionStandard + '</span><span style="margin-left:100px;">' + gz.label.zxdeclare.zxDeclareTypeChildEduDeductionStandard + '</span></div>';//抵扣标准
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.declareDate + '</span><span style="margin-left:100px;">' + me.returnData.create_date + '</span><div>';//申报日期
            html += "</div>";
            declare.setHtml(html);
        } else if (declare_type == '02') {//继续教育
            var post_type_desc;
            if (me.returnData.post_type == '01') {
                post_type_desc = gz.label.zxdeclare.skilledPersonProfessionalQualification;
            } else if (me.returnData.post_type == '02') {
                post_type_desc = gz.label.zxdeclare.professionalQualifications;
            }
            var deductionStandard =  me.returnData.cuntin_edu_type == '01' ?gz.label.zxdeclare.continuingeduDeductionStandard:gz.label.zxdeclare.careereduDeductionStandard;
            var html = '<div style="margin-left:34px;margin-bottom:10px;">';
            var declare_type_desc = me.returnData.cuntin_edu_type == '01' ? gz.label.zxdeclare.continuingEdu : gz.label.zxdeclare.CareerEdu;//'学历（学位）继续教育':'职业资格继续教育';
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.eduType + '</span><span style="margin-left:100px;">' + declare_type_desc + '</span></div>';//教育方式
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductType + '</span><span style="margin-left:100px;">' + deduct_type + '</span></div>';//抵扣方式
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductionStandard +'</span><span style="margin-left:100px;">' + deductionStandard+ '</span></div>';//抵扣标准
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductMoney + '</span><span style="margin-left:100px;">' + deduct_money + gz.label.zxdeclare.moneydesc + '</span></div>';//抵扣金额
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.declareDate + '</span><span style="margin-left:100px;">' + me.returnData.create_date + '</span><div>';//申报日期
            /*  if (me.returnData.cuntin_edu_type == '01') {
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.currentContinuingEducationStartDate + '</span><span style="margin-left:100px;">' + start_date + '</span><div>';//当前继续教育起始时间
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.currentContinuingEducationEndDate + '</span><span style="margin-left:100px;">' + end_date + '</span><div>';//结束日期
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.zeduLevel + '</span><span style="margin-left:100px;">' + edu_level + '</span><div>';//教育阶段
              } else {
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.currentContinuingEducationType + '</span><span style="margin-left:76px;">' + post_type_desc + '</span><div>';//继续教育类型
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.dateOfIssue + '</span><span style="margin-left:52px;">' + start_date + '</span><div>';//发证批准日期
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.postCertificateName + '</span><span style="margin-left:100px;">' + post_certificate_name + '</span><div>';//证书名称
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.postCertificateNumber + '</span><span style="margin-left:100px;">' + post_certificate_number + '</span><div>';//证书编号
                  html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.postCertificateOrg + '</span><span style="margin-left:100px;">' + post_certificate_org + '</span><div>';//发证机关
              }*/
            html += "</div>";
            declare.setHtml(html);
        } else if (declare_type == '03') {//住房租金
            // var rent_house_type_desc;
            // if (me.returnData.rent_house_type == '01') {
            //     rent_house_type_desc = gz.label.zxdeclare.personal;
            // } else if (me.returnData.rent_house_type == '02') {
            //     rent_house_type_desc = gz.label.zxdeclare.organization;
            // }
            var html = '<div style="margin-left:34px;margin-bottom:10px;">';
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductMoney + '</span><span style="margin-left:100px;">' + deduct_money + gz.label.zxdeclare.moneydesc + '</span></div>';//抵扣金额
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductType + '</span><span style="margin-left:100px;">' + deduct_type + '</span></div>';//抵扣方式
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductionStandard + '</span><span style="margin-left:100px;">' + deduct_money+gz.label.zxdeclare.moneydesc+'/'+gz.label.month+'</span></div>';//抵扣标准
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.declareDate + '</span><span style="margin-left:100px;">' + me.returnData.create_date + '</span><div>';//申报日期
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseProvince + '</span><span style="margin-left:277px;">' + me.returnData.rent_house_province_desc + '</span></div>';//主要工作省份
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseCity + '</span><span style="margin-left:277px;">' + me.returnData.rent_house_city_desc + '</span></div>';//主要工作城市
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseType + '</span><span style="margin-left:325px;">' + rent_house_type_desc + '</span></div>';//类型
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseName + '</span><span style="margin-left:218px;">' + me.returnData.rent_house_name + '</span></div>';//出租方姓名
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseIdType + '</span><span style="margin-left:265px;">' + me.returnData.rent_house_id_type_desc + '</span></div>';//出租方证件类型
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseIdNumber + '</span><span style="margin-left:155px;">' + me.returnData.rent_house_id_number + '</span></div>';//身份证件号码
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseAddress + '</span><span style="margin-left:276px;">' + me.returnData.rent_house_address + '</span></div>';//住房坐落地址
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentHouseNo + '</span><span style="margin-left:251px;">' + me.returnData.rent_house_no + '</span></div>';//住房租赁合同编号
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentStartDate + '</span><span style="margin-left:299px;">' + start_date + '</span><div>';//租赁期起
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.rentEndDate + '</span><span style="margin-left:299px;">' + end_date + '</span><div>';//租赁期止
            html += "</div>";
            declare.setHtml(html);
        } else if (declare_type == '04') {//房贷利息
            var loan_self_flag_desc;
            var house_type_desc;
            var loan_flat_desc;
            var loan_type_desc;
            if (me.returnData.loan_self_flag == '1') {
                loan_self_flag_desc = gz.label.zxdeclare.yes;
            } else if (me.returnData.loan_self_flag == '2') {
                loan_self_flag_desc = gz.label.zxdeclare.no;
            } else {
                loan_self_flag_desc = "";
            }
            if (me.returnData.house_type == '01') {
                house_type_desc = gz.label.zxdeclare.houseOwnershipCertificate;
            } else if (me.returnData.house_type == '02') {
                house_type_desc = gz.label.zxdeclare.immovableTitleCertificate;
            } else if (me.returnData.house_type == '03') {
                house_type_desc = gz.label.zxdeclare.houseSaleContract;
            } else if (me.returnData.house_type == '04') {
                house_type_desc = gz.label.zxdeclare.presaleContract;
            } else {
                house_type_desc = "";
            }
            if (me.returnData.loan_flat == '1') {
                loan_flat_desc = gz.label.zxdeclare.yes;
            } else if (me.returnData.loan_flat == '2') {
                loan_flat_desc = gz.label.zxdeclare.no;
            } else {
                loan_flat_desc = "";
            }
            // if (me.returnData.loan_type == '01') {
            //     loan_type_desc = gz.label.zxdeclare.providentFundLoan;
            // } else if (me.returnData.loan_type == '02') {
            //     loan_type_desc = gz.label.zxdeclare.commercialLoans;
            // }
            var html = '<div style="margin-left:34px;margin-bottom:10px;">';
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductMoney + '</span><span style="margin-left:300px;">' + deduct_money + gz.label.zxdeclare.moneydesc + '</span></div>';//抵扣金额
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductionStandard + '</span><span style="margin-left:300px;">' + gz.label.zxdeclare.interestExpenseDeductionStandard+ '</span></div>';//抵扣标准
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.houseAddress + '</span><span style="margin-left:276px;">' + me.returnData.house_address + '</span></div>';//房屋坐落地址
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanSelfFlag + '</span><span style="margin-left:265px;">' + loan_self_flag_desc + '</span></div>';//本人是否借款人
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.houseType + '</span><span style="margin-left:277px;">' + house_type_desc + '</span></div>';//房屋证书类型
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.houseNumber + '</span><span style="margin-left:277px;">' + me.returnData.house_number + '</span></div>';//房屋证书号码
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanFlat + '</span><span style="margin-left:120px;">' + loan_flat_desc + '</span></div>';//是否婚前各自首套贷款且婚后分别扣除50%
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.declareDate + '</span><span style="margin-left:300px;">' + me.returnData.create_date + '</span><div>';//申报日期
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanType + '</span><span style="margin-left:300px;">' + loan_type_desc + '</span></div>';//贷款类型
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanBank + '</span><span style="margin-left:299px;">' + me.returnData.loan_bank + '</span></div>';//贷款银行
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanContractNo + '</span><span style="margin-left:274px;">' + me.returnData.loan_contract_no + '</span></div>';//贷款合同编号
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanStartDate + '</span><span style="margin-left:274px;">' + start_date + '</span><div>';//首次还款日期
            // html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.loanAllotedTime + '</span><span style="margin-left:298px;">' + me.returnData.loan_alloted_time + '</span><div>';//贷款期限
            html += "</div>";
            declare.setHtml(html);
        } else if (declare_type == '05') {//大病医疗
            var html = '<div style="margin-left:34px;margin-bottom:10px;">';
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductMoney + '</span><span style="margin-left:100px;">' + deduct_money + gz.label.zxdeclare.moneydesc + '</span></div>';//抵扣金额
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.createData + '</span><span style="margin-left:100px;">' + start_date + '</span><div>';//起始日期
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.endDate + '</span><span style="margin-left:100px;">' + end_date + '</span><div>';//结束日期
            html += "</div>"
            declare.setHtml(html);
        } else if (declare_type == '06') {//赡养老人
            var apportion_type_desc;
            if (me.returnData.apportion_type == '01') {
                apportion_type_desc = gz.label.zxdeclare.averageShareOfDependents;//赡养人平均分摊
            } else if (me.returnData.apportion_type == '02') {
                apportion_type_desc = gz.label.zxdeclare.dependentAgreement;//赡养人约定分摊
            } else if (me.returnData.apportion_type == '03') {
                apportion_type_desc = gz.label.zxdeclare.assignedByTheDependent;//被赡养人指定分摊
            } else {
                apportion_type_desc = "";
            }
            var html = '<div style="margin-left:34px;margin-bottom:10px;">';
            var isSelfChild = me.returnData.apportion_type ? gz.label.zxdeclare.no : gz.label.zxdeclare.yes;//'是':'否';
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.isSelfChild + '</span><span style="margin-left:135px;">' + isSelfChild + '</span></div>';//非独生子女按约定分摊
            if (me.returnData.child_apportion > 1 && me.returnData.apportion_type == '01') {
                html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.childApportionNum + '</span><span style="margin-left:137px;">' + me.returnData.child_apportion + '</span></div>';//分摊兄弟姐妹数
            }
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductType + '</span><span style="margin-left:172px;">' + deduct_type + '</span></div>';//抵扣方式
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductionStandard + '</span><span style="margin-left:171px;">' + gz.label.zxdeclare.supportelderlyDeductionStandard+ '</span></div>';//抵扣标准
            if (apportion_type_desc) {
                html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.apportionType + '</span><span style="margin-left:172px;">' + apportion_type_desc + '</span></div>';//分摊方式
            }
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.deductMoney + '</span><span style="margin-left:170px;">' + deduct_money + gz.label.zxdeclare.moneydesc + '</span></div>';//抵扣金额
            html += '<div style="margin-top:10px"><span>' + gz.label.zxdeclare.declareDate + '</span><span style="margin-left:170px;">' + me.returnData.create_date + '</span><div>';//申报日期
            html += "</div>"
            declare.setHtml(html);
        }
        infor.add(declare);
        this.add(infor);
    },
    /**备注信息**/
    descriptionInfo: function () {
        var me = this;
        var descInfo = me.returnData.description ? me.returnData.description : gz.label.zxdeclare.zanwu;//'暂无';
        descInfo = descInfo.replace(/\n/g, '<br/>');
        descInfo = descInfo.replace(/\s/g, '&nbsp');
        var desc = Ext.create('Ext.panel.Panel', {
            width: '100%',
            border: 0,
            items: [{
                xtype: 'component',
                border: 0,
                width: '100%',
                html: '<div style="margin:0px 10px;padding:4px 0px;font-weight:blod;border-bottom:1px solid #c5c5c5;">' + gz.label.zxdeclare.descriptionInfo + '</div>'//备注信息
            }, {
                xtype: 'component',
                border: 0,
                width: '100%',
                margin: '10 0 10 0',
                html: '<div style="margin:0px 10px;padding:4px 0px 4px 24px;">' + descInfo + '</div>'
            }]
        });
        this.add(desc);
    },
    /**创建gridpanel**/
    declareSubInfo: function (declare_type, type, title) {
        var me = this;
        var columns = me.getCoulumns(declare_type, type);
        var grid = Ext.create('Ext.grid.Panel', {
            width: '100%',
            height: 200,
            enableLocking: true,
            style: 'margin-left:34px;margin-right:34px;',
            columns: columns,
            columnLines: true,
            store: me.getGridTableStore(type),
            listeners: {
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
                                var div = tip.triggerElement;//.childNodes[0];
                                if (Ext.isEmpty(div))
                                    return false;
                                if (div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight - 4) {
                                    //div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
                                    tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + div.innerHTML + "</div>");
                                } else
                                    return false;
                            }
                        }
                    });
                }
            }
        });
        var subInfo = Ext.create('Ext.panel.Panel', {
            width: '100%',
            layout: 'vbox',
            border: 0,
            items: [{
                xtype: 'component',
                border: 0,
                width: '100%',
                html: '<div style="margin:0px 10px;padding:4px 0px;font-weight:blod;border-bottom:1px solid #c5c5c5;">' + title + '</div>'
            }, {
                xtype: 'component',
                border: 0,
                width: '100%',
                height: 10
            }, grid, {
                xtype: 'component',
                border: 0,
                width: '100%',
                height: 10
            }]
        });
        me.add(subInfo);

    },
    getGridTableStore: function (type) {//主要是用来区分是创建共同赡养人还是被赡养人store
        var me = this;
        var fields;
        var data;
        var declare_type = me.returnData.declare_type;
        data = me.returnData.sub_items;
        if (declare_type == '01') {
            fields = ['member_name', 'id_type_desc', 'id_number', 'birthday', 'nationality_desc', 'edu_level', 'start_date', 'end_date', 'edu_stop_date', 'edu_nationality_desc', 'edu_institution', 'deduct_proportion', 'deduct_money'];
        } else if (declare_type == '06') {
            fields = ['member_name', 'id_type_desc', 'id_number', 'birthday', 'nationality_desc', 'relation'];
            if (type == 'support') {//被赡养人
                data = me.returnData.sub_old_items;
            } else if (type == 'comonsupport') {//共同赡养人
                data = me.returnData.sub_child_items
            }

        } else if (declare_type == '02') {
            fields = ['start_date', 'end_date', 'jx_edu_level', 'post_type', 'post_certificate_name', 'post_certificate_number', 'post_certificate_org'];
        } else if (declare_type == '03') {
            fields = ['rent_house_province_desc', 'rent_house_city_desc', 'rent_house_type', 'rent_house_name', 'rent_house_id_type_desc', 'rent_house_id_number', 'rent_house_address', 'rent_house_no', 'start_date', 'end_date'];
        } else if (declare_type == '04') {
            fields = ['loan_type', 'loan_bank', 'loan_contract_no', 'loan_alloted_time'];
        }
        var store = Ext.create('Ext.data.Store', {
            fields: fields,
            data: data
        });
        return store;
    },
    /**附件信息**/
    attachInfo: function () {
        var me = this;
        var fileInfo = Ext.create('Ext.panel.Panel', {
            width: '100%',
            border: 0,
            layout: 'vbox',
            items: [{
                xtype: 'component',
                border: 0,
                width: '100%',
                html: '<div style="margin:0px 10px;padding:4px 0px;font-weight:blod;border-bottom:1px solid #c5c5c5;">' + gz.label.zxdeclare.fileInfo + '</div>'//附件
            }]
        });
        var attach_path = me.returnData.attach_path;
        if (!attach_path || attach_path.length == 0) {
            fileInfo.add({
                xtype: 'component',
                border: 0,
                width: '100%',
                margin: '10 0 10 0',
                html: '<div style="margin:0px 10px;padding:4px 0px 4px 24px;">' + gz.label.zxdeclare.zanwu + '</div>'//暂无
            });
            this.add(fileInfo);
            return;
        }

        var file = Ext.create('Ext.panel.Panel', {
            xtype: 'panel',
            layout: 'hbox',
            width: '100%',
            border: 0,
            margin: '0 34'
        });


        for (var i = 0; i < attach_path.length; i++) {
            var attach = attach_path[i];
            var imgPanel = Ext.create('Ext.panel.Panel', {
                xtype: 'panel',
                border: 0,
                layout: 'vbox',
                margin: '0 10 0 0',
                items: [{
                    xtype: 'image',
                    width: '100px',
                    height: '150px',
                    src: '/servlet/DisplayOleContent?openflag=true&&filePath=' + attach.file_path
                }, {
                    xtype: 'component',
                    height: 100,
                    width: 100,
                    style: 'white-space:normal;word-break:break-all;word-wrap:break-word',
                    html: '<a target="_blank" href="/servlet/DisplayOleContent?filePath=' + attach.file_path + '">' + attach.file_name + '</a>',
                    margin: '0 0 0 10'
                }]
            });
            file.add(imgPanel);
        }
        fileInfo.add(file);

        this.add(fileInfo);
    },
    /**审批中显示按钮**/
    desclareBtns: function () {
        var me = this;
        var btn = Ext.create('Ext.panel.Panel', {
            xtype: 'panel',
            layout: 'hbox',
            width: '100%',
            style: 'margin-top:10px;',
            border: 0,
            items: [{
                xtype: 'component',
                flex: 1,
                border: 0
            }, {
                xtype: 'button',
                width: 80,
                height: 24,
                html: gz.label.zxdeclare.agreeBtn,//'同意'
                handler: function () {
                    // 提示信息  确定要同意选中的数据？
                    Ext.Msg.confirm(gz.msg.zxDeclareTitle, gz.label.zxdeclare.agreeOperateMsg, function (res) {//"确定同意该申报信息？"
                        if (res == "yes") {
                            var map = new HashMap();
                            map.put("operateType", "approve");
                            map.put("ids", '' + me.returnData.id);
                            Rpc({functionId: 'GZ00000701', success: me.returnResult, scope: me}, map);
                        }
                    });
                }
            }, {
                xtype: 'component',
                width: 10,
                border: 0
            }, {
                xtype: 'button',
                width: 80,
                height: 24,
                html: gz.label.zxdeclare.returnBtn,//'退回'
                handler: function () {
                    // 提示信息  确定要同意选中的数据？
                    Ext.Msg.confirm(gz.msg.zxDeclareTitle, gz.label.zxdeclare.returnOperateMsg, function (res) {//"确定退回该申报信息？"
                        if (res == "yes") {
                            var approveWin = Ext.create('Ext.window.Window', {
                                width: 400,
                                height: 250,
                                resizable: false,
                                layout: {
                                    type: 'vbox',
                                    align: 'middle',
                                    pack: 'center'
                                },
                                title: gz.label.zxdeclare.approveTextTitile,
                                items: [
                                    {
                                        xtype: 'textareafield',
                                        itemId: 'approveAreafield',
                                        width: 390,
                                        height: 180

                                    },
                                    {
                                        xtype: 'container',
                                        layout: {
                                            type: 'hbox',
                                            align: 'cener',
                                            pack: 'middle'
                                        },
                                        items: [
                                            {
                                                xtype: 'button',
                                                heiht: 50,
                                                width: 50,
                                                html: gz.label.zxdeclare.returnBtn,
                                                margin: '0 10 0 0',
                                                handler: function () {
                                                    var approve_desc = approveWin.query('#approveAreafield')[0].getValue();
                                                    var map = new HashMap();
                                                    map.put("operateType", "reject");
                                                    map.put("ids", '' + me.returnData.id);
                                                    map.put("approveDesc", approve_desc);
                                                    approveWin.close();
                                                    Rpc({
                                                        functionId: 'GZ00000701',
                                                        success: me.returnResult,
                                                        scope: me
                                                    }, map);
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                heiht: 50,
                                                width: 50,
                                                margin: '0 0 0 10',
                                                html: gz.label.cancel,
                                                handler: function () {
                                                    approveWin.close();
                                                }
                                            }
                                        ]


                                    }
                                ]
                            }).show();
                        }
                    });
                }
            }, {
                xtype: 'component',
                flex: 1,
                border: 0
            }]
        });

        me.add(btn);
    },
    /**执行同意和退回操作,刷新主页面数据**/
    returnResult: function () {
        //刷新数据成功
        var store = Ext.data.StoreManager.lookup('declareListTable_dataStore');
        store.reload();
        Ext.getCmp('declareInfoWindow').close();
    },
    cityZxdeclareDesc: function (value) {
        var text;
        if (value = '01') {
            text = gz.label.zxdeclare.cityType.firstTypeCity;//直辖市、省会城市、计划单列市
        } else if (value = '02') {
            text = gz.label.zxdeclare.cityType.secondTypeCity;//市辖区户籍人口超过100万的
        } else if (value = '03') {
            text = gz.label.zxdeclare.cityType.threeTypeCity;//市辖区户籍人口小于100万的
        }
        return text;
    },
    getCoulumns: function (declare_type, type) {
        var columns;
        var hidden;
        if (declare_type == '01') {
            columns = [
                {
                    header: gz.label.zxdeclare.memberName, dataIndex: 'member_name', flex: 6, align: 'left'//'子女姓名'
                }, {
                    header: gz.label.zxdeclare.idType, dataIndex: 'id_type_desc', flex: 6, align: 'left'//'身份证件类型'
                }, {
                    header: gz.label.zxdeclare.idNumber, dataIndex: 'id_number', flex: 12, align: 'right'//'身份证件号码'
                }, {
                    header: gz.label.zxdeclare.birthday, dataIndex: 'birthday', flex: 8, align: 'right'//'出生日期'
                }, {
                    header: gz.label.zxdeclare.nationality, dataIndex: 'nationality_desc', flex: 6, align: 'left'//'国籍（地区）'
                }, {
                    header: gz.label.zxdeclare.eduLevel, dataIndex: 'edu_level', flex: 8, align: 'left',//'当前受教育阶段'
                    renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                        if (value == '01') {
                            value = gz.label.zxdeclare.preschoolEducation;
                        } else if (value == '02') {
                            value = gz.label.zxdeclare.compulsoryEducation;
                        } else if (value == '03') {
                            value = gz.label.zxdeclare.highSchoolEducation;
                        } else if (value == '04') {
                            value = gz.label.zxdeclare.higherEducation;
                        }
                        return value;
                    }
                }, {
                    header: gz.label.zxdeclare.eduStartDate,
                    dataIndex: 'start_date',
                    flex: 12,
                    align: 'right'//'当前受教育起始时间'
                }, {
                    header: gz.label.zxdeclare.eduEndtDate,
                    dataIndex: 'end_date',
                    flex: 12,
                    align: 'right'//'当前受教育结束时间'
                }, {
                    header: gz.label.zxdeclare.eduStopDate,
                    dataIndex: 'edu_stop_date',
                    flex: 10,
                    align: 'right'//'教育终止时间'
                }, {
                    header: gz.label.zxdeclare.eduNationality,
                    dataIndex: 'edu_nationality_desc',
                    flex: 10,
                    align: 'left'//'当前就读国家(地区)'
                }, {
                    header: gz.label.zxdeclare.eduInstitution,
                    dataIndex: 'edu_institution',
                    flex: 6,
                    align: 'left'//'当前就读学校'
                }, {
                    header: gz.label.zxdeclare.deductProportion,
                    dataIndex: 'deduct_proportion',
                    flex: 6,
                    align: 'right'//'本人扣除比例'
                }/*, {
                    header: gz.label.zxdeclare.deductMoney,
                    dataIndex: 'deduct_money',
                    flex: 6,
                    align: 'right',//'抵扣金额'
                    renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                        return value + gz.label.zxdeclare.moneydesc;
                    }
                }*/]
        } else if (declare_type == '06') {
            if (type == 'support') {
                hidden = false;
            } else if (type == 'comonsupport') {
                hidden = true;
            }
            columns = [
                {
                    header: gz.label.zxdeclare.name, dataIndex: 'member_name', flex: 1, align: 'left',//'姓名'
                }, {
                    header: gz.label.zxdeclare.idType, dataIndex: 'id_type_desc', flex: 1, align: 'left'//'身份证件类型'
                }, {
                    header: gz.label.zxdeclare.idNumber, dataIndex: 'id_number', flex: 1, align: 'right'//'身份证件号码'
                }, {
                    header: gz.label.zxdeclare.nationality, dataIndex: 'nationality_desc', flex: 1, align: 'left'//'国籍（地区）'
                }, {
                    header: gz.label.zxdeclare.relation, dataIndex: 'relation', flex: 1, align: 'left', hidden: hidden//'关系'
                }, {
                    header: gz.label.zxdeclare.birthday, dataIndex: 'birthday', flex: 1, align: 'right', hidden: hidden//'出生日期'
                }]
        } else if (declare_type == '02') {
            if (type == '01') {
                columns = [{
                    header: gz.label.zxdeclare.currentContinuingEducationStartDate,
                    dataIndex: 'start_date',
                    flex: 1,
                    align: 'right'//'当前继续教育起始时间'
                }, {
                    header: gz.label.zxdeclare.currentContinuingEducationEndDate,
                    dataIndex: 'end_date',
                    flex: 1,
                    align: 'right'//'（预计）当前继续教育结束时间'
                }, {
                    header: gz.label.zxdeclare.zeduLevel, dataIndex: 'jx_edu_level', flex: 1, align: 'left',//'教育阶段'
                    renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                        if (value == '01') {
                            value = gz.label.zxdeclare.specialist;
                        } else if (value == '02') {
                            value = gz.label.zxdeclare.bachelor;
                        } else if (value == '03') {
                            value = gz.label.zxdeclare.masterStudent;
                        } else if (value == '04') {
                            value = gz.label.zxdeclare.doctoralStudent;
                        } else if (value == '05') {
                            value = gz.label.zxdeclare.other;
                        }
                        return value;
                    }
                }]
            } else if (type == '02') {
                columns = [
                    {
                        header: gz.label.zxdeclare.currentContinuingEducationType,
                        dataIndex: 'post_type',
                        flex: 1,
                        align: 'left', //'继续教育类型'
                        renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                            if (value == '01') {
                                value = gz.label.zxdeclare.skilledPersonProfessionalQualification;
                            } else if (value == '02') {
                                value = gz.label.zxdeclare.professionalQualifications;
                            }
                            return value;
                        }
                    }, {
                        header: gz.label.zxdeclare.dateOfIssue,
                        dataIndex: 'start_date',
                        flex: 1,
                        align: 'right' //'发证（批准）日期'
                    }, {
                        header: gz.label.zxdeclare.postCertificateName,
                        dataIndex: 'post_certificate_name',
                        flex: 1,
                        align: 'left' //'证书名称'
                    }, {
                        header: gz.label.zxdeclare.postCertificateNumber,
                        dataIndex: 'post_certificate_number',
                        flex: 1,
                        align: 'right' //'证书编号'
                    }, {
                        header: gz.label.zxdeclare.postCertificateOrg,
                        dataIndex: 'post_certificate_org',
                        flex: 1,
                        align: 'left' //'发证机关'
                    }]
            }
        } else if (declare_type == '03') {//住房租金
            columns = [
                {
                    header: gz.label.zxdeclare.rentHouseProvince,//主要工作省份
                    dataIndex: 'rent_house_province_desc',
                    flex: 1,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.rentHouseCity,//主要工作城市
                    dataIndex: 'rent_house_city_desc',
                    flex: 2,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.rentHouseType,//类型
                    dataIndex: 'rent_house_type',
                    flex: 1,
                    align: 'left',
                    renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                        if (value == '01') {
                            value = gz.label.zxdeclare.personal;
                        } else if (value == '02') {
                            value = gz.label.zxdeclare.organization;
                        }
                        return value;
                    }
                }, {
                    header: gz.label.zxdeclare.rentHouseName,//出租方姓名
                    dataIndex: 'rent_house_name',
                    flex: 1.5,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.rentHouseIdType,//出租方证件类型
                    dataIndex: 'rent_house_id_type_desc',
                    flex: 1.5,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.rentHouseIdNumber,//身份证件号码
                    dataIndex: 'rent_house_id_number',
                    flex: 2,
                    align: 'right'
                }, {
                    header: gz.label.zxdeclare.rentHouseAddress,//住房坐落地址
                    dataIndex: 'rent_house_address',
                    flex: 2,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.rentHouseNo,//住房租赁合同编号
                    dataIndex: 'rent_house_no',
                    flex: 2,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.rentStartDate,//租赁期起
                    dataIndex: 'start_date',
                    flex: 1,
                    align: 'right'
                }, {
                    header: gz.label.zxdeclare.rentEndDate,//租赁期止
                    dataIndex: 'end_date',
                    flex: 1,
                    align: 'right'
                }]
        } else if (declare_type == '04') {//房贷利息
            columns = [
                {
                    header: gz.label.zxdeclare.loanType,//贷款类型
                    dataIndex: 'loan_type',
                    flex: 1,
                    align: 'left',
                    renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                        if (value == '01') {
                            value = gz.label.zxdeclare.providentFundLoan;
                        } else if (value == '02') {
                            value = gz.label.zxdeclare.commercialLoans;
                        }
                        return value;
                    }
                }, {
                    header: gz.label.zxdeclare.loanBank,//贷款银行
                    dataIndex: 'loan_bank',
                    flex: 1,
                    align: 'left'
                }, {
                    header: gz.label.zxdeclare.loanContractNo,//贷款合同编号
                    dataIndex: 'loan_contract_no',
                    flex: 1,
                    align: 'right'
                }, {
                    header: gz.label.zxdeclare.loanStartDate,//首次还款日期
                    dataIndex: 'start_date',
                    flex: 1,
                    align: 'right'
                }, {
                    header: gz.label.zxdeclare.loanAllotedTime,//贷款期限
                    dataIndex: 'loan_alloted_time',
                    flex: 1,
                    align: 'right'
                }]
        }
        return columns;
    }
});