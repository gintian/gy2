Ext.define("Talent.TalentHallParameter", {
    extend: 'Ext.panel.Panel',
    requires: ['EHR.extWidget.proxy.TransactionProxy', 'EHR.templateSelector.TemplateSelector', 'EHR.fielditemselector.FieldItemSelector'],// 加载js必要的父类
    title: tm.config.name,
    xtype: 'talent',
    border: false,
    //bodyBorder: false,
    bodyStyle :'z-index:2;',
    hidden:true,
    scrollable:document.documentElement.clientWidth<800 ? true:'y',
    buttonAlign:'center',
    tabName: '',
    tabId: '',
    id:"TalentHallParameter",
    configParam: {
        competition: {
            templates: {
                releasePost_template: '',
                apply_template: '',
                hire_template: ''
            },
            rnames: {
                applyResume_rname: '',
                postDetail_ranme: ''
            },
            postFields: [],
            maxCompetitionPost: '',
            quickApprove:false,
            openInterview:true,
            templatesRelation: {
                releasePost_template: {},
                apply_template: {},
                hire_template:{}
            }
        },
        talentHall: {
            templates: {
                apply_template: '',
                cancel_template: ''
            },
            rnames: {
                talent_rname: ''
            },
            resumePostTypeField: [],
            resumeSelfIntroduction: []
        }
    },
    layout: {
        type: 'vbox',
        align: 'center'
    },
    listeners:{
        'resize':function () {
            if(document.documentElement.clientWidth<800){
                if(Ext.getCmp("TalentHallParameter")){
                    Ext.getCmp("TalentHallParameter").setScrollable(true);
                }
            }else{
                if(Ext.getCmp("TalentHallParameter")){
                    Ext.getCmp("TalentHallParameter").setScrollable('y');
                }
            }
        }
    },
    initComponent: function () {
        me = this;
        this.callParent();
        Ext.QuickTips.init();
        this.initConfigPage();
        //请求数据，填充界面数据
        this.initData();

    },
    /*buttons:[
        {
            text:tm.config.msg.save,
            // margin:'0 0 150 0',
            style:'margin-bottom:0.2%',
            handler:function(){
                me.save();
            }
        }
    ],*/
    save:function(){
        var maxJobs = me.query("#maxCompetitionPost")[0].value;
        if(maxJobs < 1 || maxJobs == null){
            Ext.Msg.alert(tm.tip,tm.config.maxJobsMinAlert);
            return;
        }
        if(maxJobs > 100){
            Ext.Msg.alert(tm.tip,tm.config.maxJobsMaxAlert);
            return;
        }
        me.configParam.competition.maxCompetitionPost = maxJobs;
        //me.configParam.competition.maxCompetitionPost = me.query("#maxCompetitionPost")[0].value;
        var param = new HashMap();
        param.put("jsonStr", me.configParam);
        param.put("type", "save");// save/search
        Rpc({
            functionId: 'TM000000006', success: function (result) {// TM000000301
                var retparam = Ext.decode(result.responseText);// decode是必要的
                var msg = retparam.returnStr.return_msg;
                if (msg) {
                    Ext.Msg.alert(tm.tip, tm.config.msg.saveSuccess);
                } else {
                    Ext.Msg.alert(tm.tip, tm.config.msg.saveFailure);
                }
            }, scope: this
        }, param);
    },
    /**
     * 初始化加载数据
     * @Author xuchangshun
     * @Date 2019/8/19 15:38
     */
    initData: function () {
        var param = new HashMap();
        param.put("type", "search");
        Rpc({
            functionId: 'TM000000006', success: function (result) {// TM000000301
                // 获取返回值
                var retparam = Ext.decode(result.responseText);
                var jsonStr = retparam.returnStr;
                var data = jsonStr.return_data;
                if (Ext.JSON.encode(data) != "{}") {
                    //已经有数据存储了,解析并加载到前台界面,从上至下依次解析赋值
                    /**快速审批**/
                    var quickApproveData = data.competition.quickApprove;
                    var openInerviewData = data.competition.openInterview;
                    /*if(quickApproveData){
                        me.query("#quickApprovalLabelComponent")[0].setHtml('<img id="quickApproveImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/turnOn.png" onclick="javascript:void(0);me.changeApproveStatus();"/>');
                        me.query("#quickApprovalConfigPanel")[0].setHidden(false);
                        me.query("#releasePostPanel")[0].setHidden(true);
                        me.configParam.competition.templates.quickApprove = quickApproveData;
                    }else{
                        me.query("#quickApprovalLabelComponent")[0].setHtml('<img id="quickApproveImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/shutDown.png" onclick="javascript:void(0);me.changeApproveStatus();"/>');
                        me.query("#quickApprovalConfigPanel")[0].setHidden(false);
                        me.query("#releasePostPanel")[0].setHidden(false);
                        me.configParam.competition.templates.quickApprove = false;
                    }*/
                    if(quickApproveData){
                        me.query("#quickApproveContainer")[0].setHtml('<img id="quickApproveImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/turnOn.png" onclick="javascript:void(0);me.changeApproveStatus(\'quickApproveImg\');"/>');
                        //me.query("#quickApprovalConfigPanel")[0].setHidden(false);
                        me.query("#releasePostPanel")[0].setHidden(true);
                        me.configParam.competition.quickApprove = quickApproveData;
                    }else{
                        me.query("#quickApproveContainer")[0].setHtml('<img id="quickApproveImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/shutDown.png" onclick="javascript:void(0);me.changeApproveStatus(\'quickApproveImg\');"/>');
                        //me.query("#quickApprovalConfigPanel")[0].setHidden(false);
                        me.query("#releasePostPanel")[0].setHidden(false);
                        me.configParam.competition.quickApprove = false;
                    }
                    /**是否打开面试过程**/
                    if(!openInerviewData){
                        if(quickApproveData == undefined){
                            me.query("#openInterviewContainer")[0].setHtml('<img id="openInterviewImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/turnOn.png" onclick="javascript:void(0);me.changeApproveStatus(\'openInterviewImg\');"/>');
                            me.configParam.competition.openInterview = true;
                        }else{
                            me.query("#openInterviewContainer")[0].setHtml('<img id="openInterviewImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/shutDown.png" onclick="javascript:void(0);me.changeApproveStatus(\'openInterviewImg\');"/>');
                            me.configParam.competition.openInterview = openInerviewData;
                        }
                    }else{
                        me.query("#openInterviewContainer")[0].setHtml('<img id="openInterviewImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/turnOn.png" onclick="javascript:void(0);me.changeApproveStatus(\'openInterviewImg\');"/>');
                        me.configParam.competition.openInterview = openInerviewData;
                    }

                    /**岗位发布模板**/
                    var releasePostData = data.competition.templates.releasePost_template;
                    if (releasePostData.name) {
                        var releasePostName = releasePostData.name;
                        if (data.competition.templatesRelation) {
                            var releasePost_template = data.competition.templatesRelation.releasePost_template;
                            if(!releasePost_template.interviewArrangement){//面试官是后增加的指标对应，因此需要特殊处理下
                                releasePost_template.interviewArrangement = '';//默认赋值空，防止不加载保存不进去
                            }
                            me.configParam.competition.templatesRelation.releasePost_template = releasePost_template;
                        }
                        me.configParam.competition.templates.releasePost_template = releasePostData.tabId;
                        me.temp_releasePost_template = releasePostData.tabId;
                        me.query("#releasePostLabelComponent")[0].setHtml('<span style="color:#666666;" width="100%" align="left">' + releasePostName + '</span>');
                        if (releasePostName) {
                            me.query("#releasePostConfigPanel")[0].setHidden(false);
                        }
                    }
                    //竞聘报名模版
                    var applyTemplateData = data.competition.templates.apply_template;
                    if (applyTemplateData.name) {
                        var applyTemplateName = applyTemplateData.name;
                        if (data.competition.templatesRelation) {
                            me.configParam.competition.templatesRelation.apply_template = data.competition.templatesRelation.apply_template;
                        }
                        me.configParam.competition.templates.apply_template = applyTemplateData.tabId;
                        me.temp_apply_template = applyTemplateData.tabId;
                        me.query("#signUpLabelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + applyTemplateName + '</span>');
                        if (applyTemplateName) {
                            me.query("#signUpConfigPanel")[0].setHidden(false);
                        }
                    }
                    //录用审批模版
                    var hireTemplateData = data.competition.templates.hire_template;
                    if (hireTemplateData.name) {
                        var hireTemplateName = hireTemplateData.name;
                        if (data.competition.templatesRelation) {
                            me.configParam.competition.templatesRelation.hire_template = data.competition.templatesRelation.hire_template;
                        }
                        me.configParam.competition.templates.hire_template = hireTemplateData.tabId;
                        me.temp_hire_template = hireTemplateData.tabId;
                        //'<span style="color:#666666;" width="80%" align="left">'  + '</span>'
                        me.query("#employApprovalLabelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + hireTemplateName + '</span>');
                        if (hireTemplateName) {
                            me.query("#employApprovalConfigPanel")[0].setHidden(false);
                        }
                    }
                    //应聘简历登记表
                    var applyResumeData = data.competition.rnames.applyResume_rname;
                    if (applyResumeData.name) {
                        var applyResumeTemplateName = applyResumeData.name;
                        me.configParam.competition.rnames.applyResume_rname = applyResumeData.tabId;
                        me.query("#applyResumeComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + applyResumeTemplateName + '</span>');
                    }
                    //竞聘岗位详情等级表
                    var postDetailData=data.competition.rnames.postDetail_ranme;
                    if(postDetailData.name){
                        var postDetailName=postDetailData.name;
                        me.configParam.competition.rnames.postDetail_ranme=postDetailData.tabId;
                        me.query("#postDetailComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + postDetailName + '</span>');
                    }
                    //应聘规则
                    var maxjobs = data.competition.maxCompetitionPost;
                    me.configParam.competition.maxCompetitionPost = maxjobs;
                    me.query("#maxCompetitionPost")[0].setValue(maxjobs);
                    // 各指标项数据集
                    var postFieldsData = data.competition.postFields;
                    if (postFieldsData) {
                        var str = "";
                        for (var i = 0; i < postFieldsData.length; i++) {
                            str += postFieldsData[i].itemdesc + "、";
                            var item = postFieldsData[i].itemid;
                            me.configParam.competition.postFields.push(item);
                        }
                        me.query("#postFieldsComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + str.slice(0, str.length - 1) + '</span>');
                    }
                    //人才展厅相关界面<二期相关功能本次暂不开放>
                    var resumeSelfIntroductionData = data.talentHall.resumeSelfIntroduction;// 个人简介
                    var resumePostTypeFieldData = data.talentHall.resumePostTypeField;// 岗位类别
                    //申请模板
                    var talentHallApplyData=data.talentHall.templates.apply_template;
                    if(talentHallApplyData.name){
                        var talentHallApplyName=talentHallApplyData.name;
                        me.configParam.talentHall.templates.apply_template=talentHallApplyData.tabId;
                        me.query("#talentHallApplyComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + talentHallApplyName + '</span>');

                    }
                    //撤销模板
                    var talentHallCancelData=data.talentHall.templates.cancel_template;
                    if(talentHallCancelData.name){
                        var talentHallCancelName=talentHallCancelData.name;
                        me.configParam.talentHall.templates.cancel_template=talentHallCancelData.tabId;
                        me.query("#talentHallCancelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + talentHallCancelName + '</span>');
                    }
                    //人才登记表
                    var talentRnameData=data.talentHall.rnames.talent_rname;
                    if(talentRnameData.name){
                        var talentRnameName=talentRnameData.name;
                        me.configParam.talentHall.rnames.talent_rname=talentRnameData.tabId;
                        me.query("#talentRnameComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + talentRnameName + '</span>');
                    }
                    //个人简历展示项
                    if(resumeSelfIntroductionData && resumeSelfIntroductionData.length>0){
                        var resumeFields="";
                        resumeFields+=resumeSelfIntroductionData[0].itemdesc;
                        var resumeItem=resumeSelfIntroductionData[0].itemid;
                        me.configParam.talentHall.resumeSelfIntroduction.push(resumeItem);
                        me.query("#myResumeFieldsComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + resumeFields + '</span>');
                    }
                    //岗位类别展示项
                    if(resumePostTypeFieldData && resumePostTypeFieldData.length>0){
                        var resumePostTypeFields="";
                        resumePostTypeFields+=resumePostTypeFieldData[0].itemdesc;
                        var resumePostItem=resumePostTypeFieldData[0].itemid;
                        me.configParam.talentHall.resumePostTypeField.push(resumePostItem);
                        me.query("#resumePostTypeFieldComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + resumePostTypeFields + '</span>');
                    }
                }
                if(Ext.getCmp("TalentHallParameter")){
                    Ext.getCmp("TalentHallParameter").show();
                }
            }, scope: this
        }, param);
        me.checkConfigurable();
    },
    /**
     * 初始化配置页面
     * @Author xuchangshun
     * @Date 2019/8/16 11:03
     */
    initConfigPage: function () {
        //1:先创建业务模版panel
        me.createTemplatePanel();
        //2：创建登记表
        me.createRegistrationPanel();
        //3:创建应聘规则panel
        me.createApplyRulePanel();
        //4:创建竞聘岗位详情panel
        me.createPostItemPanel();
        //5:创建人才展厅界面
        me.createTalentHallPanel();
    },
    /**
     * 创建业务模板面板
     * @Author xuchangshun
     * @Date 2019/8/16 15:25
     */
    createTemplatePanel: function () {
        var templatePanel = Ext.create("Ext.Panel", {
            itemId: 'templatePanel',
            /*margin: '0 0 0 50',*/
            width: '90%',
            minWidth:800,
            border: false,
            bodyBorder: false,
            layout: {//采用纵向布局
                type: 'vbox'
            }/*,
            title: tm.config.title.template*/
        });
        //业务模板标题
        var templateTitle=new Ext.Panel({
            id:'templateTitlePn',
            border:false,
            width: '84%',
            html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="20%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+tm.config.title.template+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'
        });
        //快速审批按钮
        //var quickApprovalPanel = me.creatTemplatePanel("quickApproval");
        //岗位发布模板
        var releasePostPanel = me.creatTemplatePanel("releasePost");
        //竞聘报名模板
        var competitionSignUp = me.creatTemplatePanel("signUp");
        var employApproval = me.creatTemplatePanel("employApproval");
        templatePanel.add(templateTitle);
        //templatePanel.add(quickApprovalPanel);
        templatePanel.add(releasePostPanel);
        templatePanel.add(competitionSignUp);
        templatePanel.add(employApproval);
        me.add(templatePanel);
    },
    /**
     * 创建岗位发布模板配置面板
     * @Author xuchangshun
     * @Date 2019/8/16 15:30
     */
    creatTemplatePanel: function (templateItemid) {
        var descHtml;//固定的描述html
        //var isQuickPanel = false;//是否为快速审批面板标志
        var templatePanelId = templateItemid + 'Panel';
        var componentLabelItemId = templateItemid + 'LabelComponent';//模板名称组件itemid
        var templateConfigItemid = templateItemid + 'ConfigPanel';//指标对应面板itemid
        var clickparam;//点击指标对应时传递的参数
        if (templateItemid === 'releasePost') {
            descHtml = tm.config.releasePost_template;
            clickparam = 'post'
        } else if (templateItemid === 'signUp') {
            descHtml = tm.config.apply_template;
            clickparam = 'signUp'
        } else if (templateItemid === 'employApproval') {
            descHtml = tm.config.hire_template;
            clickparam = 'employApproval'
        }/*else if (templateItemid === 'quickApproval') {
            isQuickPanel = true;
            descHtml = tm.config.quickApproval;
            clickparam = 'quickApproval'
        }*/
        //岗位发布模版配置
        var releasePostPanel = Ext.create('Ext.Panel', {
            itemId:templatePanelId,
            margin: '10 0 0 50',
            width: '100%',
            hidden: me.configParam.competition.quickApprove ? true : false,
            border: false,
            bodyBorder: false,
            layout: {
                type: 'hbox'
            }
        });
        //岗位发布的label
        var releasePostLabelPanel = Ext.create('Ext.Component', {
            width: '20%',
            /*margin:isQuickPanel ? '14 0 0 0' : '0 0 0 0',*/
            style: 'text-align:right',
            html: '<span>' + descHtml + '</span>'
        });

        //选择后模板展示和指标对应模块
        var releasePostTemplatePanel = Ext.create('Ext.Panel', {
            style: 'text-align:left;',
            border: 0,
            //width: '300px',
            itemId: templateConfigItemid,
            hidden: true,
            hideMode: 'visibility',
            layout: {
                type: 'hbox'
            },
            items: [
                {//用于填充与左侧的距离
                    xtype: 'component',
                    width: 20
                },/*displayContainer*/
                {
                    xtype:'container',
                    layout:'vbox',
                    items:[
                        {//用于展现选中的业务模版组件
                            xtype: 'component',
                            itemId: componentLabelItemId,
                            style:'white-space:nowrap;',
                            html: ''
                        },
                        {//指标对应组件
                            xtype: 'component',
                            margin:'5 0 0 0',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.itemCorresponding(\'' + clickparam + '\')">' + tm.config.indicatorCorrespondence + '</span>'
                        }
                    ]
                }/*,
                {//用于填充指标对应与业务模版组件之间的距离
                    xtype: 'component',
                    width: 10
                }*/

            ]
        });
        //用于配置前填充界面
        var perFillComponent = Ext.create('Ext.Component', {
            flex: 1
        });
        //配置
        var postTemplateSelect = Ext.create('Ext.Component', {
            width: '20%',
            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.templateSelect(\'' + clickparam + '\')">' + tm.config.setting + '</span>'//配置
        });

        releasePostPanel.add(releasePostLabelPanel);
        releasePostPanel.add(releasePostTemplatePanel);
        releasePostPanel.add(perFillComponent);
        releasePostPanel.add(postTemplateSelect);
        /*if(!isQuickPanel){
            releasePostPanel.add(postTemplateSelect);
        }*/
        return releasePostPanel;
    },
    /**
     * 创建登记表面板
     * @Author xuchangshun
     * @Date 2019/8/16 16:02
     */
    createRegistrationPanel: function () {
        var registrationPanel = Ext.create("Ext.Panel", {
            itemId: 'registrationPanel',
            /*margin: '0 0 0 50',*/
            width: '90%',
            minWidth:800,
            border: false,
            bodyBorder: false,
            layout: {//采用纵向布局
                type: 'vbox'
            },
            //title: tm.config.title.rName,
            items: [
                {
                    id:'registrationTitlePn',
                    border:false,
                    width: '84%',
                    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="20%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+tm.config.title.rName+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'
                },
                {
                    xtype: 'panel',
                    itemId: 'applyResumePanel',//应聘简历登记表
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.applyResume_rname + '</span>'//应聘简历登记表
                        },
                        {
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            //width: '300px',
                            itemId: 'applyResumeComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.templateSelect(\'applyResume\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    itemId: 'postDetailRnamePanel',//竞聘岗位详情登记表
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.postDetail_ranme + '</span>'//竞聘岗位详情登记表
                        },
                        {
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            //width: '300px',
                            itemId: 'postDetailComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.templateSelect(\'postDetail\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                }
            ]
        });
        me.add(registrationPanel);
    },
    /**
     * 创建应聘规则面板
     * @Author xuchangshun
     * @Date 2019/8/16 16:22
     */
    createApplyRulePanel: function () {
        var quickApproveContainer = Ext.create('Ext.container.Container',{
            layout:'vbox',
            margin:'0 0 0 15',
            items:[
                {//用于展现选中的业务模版组件
                    xtype: 'component',
                    itemId: 'quickApproveContainer',
                    style:'white-space:nowrap;',
                    html: '<img id="quickApproveImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/shutDown.png" onclick="javascript:void(0);me.changeApproveStatus(\'quickApproveImg\');"/>'
                }
            ]
        });
        var openInterviewContainer = Ext.create('Ext.container.Container',{
            layout:'vbox',
            margin:'0 0 0 15',
            items:[
                {//用于展现选中的业务模版组件
                    xtype: 'component',
                    itemId: 'openInterviewContainer',
                    style:'white-space:nowrap;',
                    html: '<img id="openInterviewImg" style="cursor:pointer;width: 45px;height: 45px;" src="../images/turnOn.png" onclick="javascript:void(0);me.changeApproveStatus(\'openInterviewImg\');"/>'
                }
            ]
        });
        var applyRulePanel = Ext.create("Ext.Panel", {
            itemId: 'applyRulePanel',
            /*margin: '0 0 0 50',*/
            width: '90%',
            minWidth:800,
            border: false,
            bodyBorder: false,
            //title: tm.config.title.competionRule,
            items: [
                {
                    id:'applyRuleTitlePn',
                    border:false,
                    width: '84%',
                    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="20%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+tm.config.title.competionRule+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'
                },
                {
                    xtype: 'panel',
                    margin: '10 0 0 50',
                    width: '100%',
                    height:25,
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox',
                        align: 'center'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.maxCompetitionPost + '</span>'//应聘简历登记表
                        },
                        {
                            xtype: 'numberfield',
                            itemId: 'maxCompetitionPost',
                            margin: '0 0 0 20',
                            allowDecimals:false,
                            value: 1,
                            minValue: 1,
                            maxValue:100
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox',
                        align: 'center'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right;white-space:nowrap;',
                            html: '<span>' + tm.config.quickApproval + '</span>'//应聘简历登记表
                        },
                        quickApproveContainer
                    ]
                },
                {
                    xtype: 'panel',
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox',
                        align: 'center'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.openInterview + '</span>'//应聘简历登记表
                        },
                        openInterviewContainer
                    ]
                }
            ]
        });
        me.add(applyRulePanel);
    },
    /**
     * 创建岗位详情指标界面panel
     * @Author xuchangshun
     * @Date 2019/8/16 16:38
     */
    createPostItemPanel: function () {
        var postItemPanel = Ext.create("Ext.Panel", {
            id: 'postItemPanel',
            /*margin: '0 0 0 50',*/
            width: '90%',
            minWidth:800,
            border: false,
            bodyBorder: false,
            layout: {//采用纵向布局
                type: 'vbox'
            },
            //title: tm.config.title.competionDetil,
            items: [
                {
                    id:'postItemTitlePn',
                    border:false,
                    width: '84%',
                    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="20%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+tm.config.title.competionDetil+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'
                },
                {
                    xtype: 'panel',
                    itemId: 'postFieldsPanel',//竞聘岗位详情指标项
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.postFields + '</span>'//竞聘岗位详情展示项
                        },
                        {//用于填充与左侧的距离
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left;',
                            width: '55%',
                            itemId: 'postFieldsComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.chooseFieldItemJpgw(\'Z81\',\'multi\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                }
            ]
        });
        me.add(postItemPanel);
    },
    /**
     * 配置模板界面
     * @Author xuchangshun
     * @param type 要配置的模板|登记表类型
     * @Date 2019/8/19 13:47
     */
    templateSelect: function (type) {
        var dataType;//是选择业务模版还是选择登记表
        var stat = '';//控制是选择单位模版还是岗位模版
        var filterStatic = "";//如果stat为10、11那么此字段可以为空,即不进行模版过滤，如果此stat不是10、11的话，此字段应当为'10`11'
        var addgswinTitle = "";
        if (type == 'post') {//岗位发布模板
            dataType = 1;
            stat = '11';//11 只能选择岗位模版
            addgswinTitle = tm.config.chooseModel;
            if(!me.postConfigurable){//当前模板在流程中使用不能配置
                Ext.Msg.alert(tm.tip,tm.config.notConfigurable);
                return;
            }
        } else if (type == 'signUp') {//竞聘报名模板，选择人员模板
            dataType = 1;
            addgswinTitle = tm.config.chooseModel;
            if(!me.applyConfigurable){//当前模板在流程中使用不能配置
                Ext.Msg.alert(tm.tip,tm.config.notConfigurable);
                return;
            }
        } else if (type == 'employApproval') {//拟录用审批，选择人员模版
            dataType = 1;
            addgswinTitle = tm.config.chooseModel;
            if(!me.hireConfigurable){//当前模板在流程中使用不能配置
                Ext.Msg.alert(tm.tip,tm.config.notConfigurable);
                return;
            }
        } else if (type == 'applyResume') {//应聘简历登记表，选择人员登记表
            dataType = 2;
            addgswinTitle = tm.config.chooseForm;
            stat = 'A';//人员登记表传A
        } else if (type == 'postDetail') {
            dataType = 2;
            addgswinTitle = tm.config.chooseForm;
            stat = 'K';//岗位登记表传K
        }else if(type =='talentHallApply'){//人才展厅个人申请模版
            dataType = 1;
            addgswinTitle = tm.config.chooseModel;
        }else if(type =='talentHallCancel'){
            dataType = 1;
            addgswinTitle = tm.config.chooseModel;
        }else if(type == 'talentRname'){//人才展厅简历登记表，选择登记表
            dataType = 2;
            stat = 'A';//人员登记表传A
            addgswinTitle = tm.config.chooseForm;
        }
        if (stat != "10" && stat != "11") {
            filterStatic = "10`11";
        }
        var addgswin = Ext.create("Ext.window.Window", {
            id: 'modelWin',
            title: addgswinTitle,
            height: 400,
            width: 300,
            layout: 'fit',
            modal: true,
            items: {
                xtype: 'templateselector',
                scrollable:'y',
                dataType: dataType,// 1 业务模板 2登记表
                Static: stat,// 控制状态 10 单位模板 ; 11 岗位模板; Static
                filterStatic: filterStatic,// 过滤控制状态 中间`字符间隔 新增参数   人员'10`11'
                rnameFlag: stat,//A,K
                childTemplateType:'10',
                listeners: {
                    itemclick: function (tree, node) {
                        if(node.data.leaf){
                            me.tabName = node.data.text;
                            me.tabId = node.data.id;
                        }
                        me.node = node;
                    }
                }
            },
            buttonAlign: "center",
            buttons: [
                {
                    text: tm.config.msg.confirm, handler: function () {
                        if (type == "post") {//岗位发布
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.competition.templates.releasePost_template = me.tabId;
                                    me.query("#releasePostLabelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName + '</span>');
                                    me.node.data.leaf = false;
                                    if (me.configParam.competition.templatesRelation) {//指标对应关系清空
                                        me.configParam.competition.templatesRelation.releasePost_template = {};
                                    }
                                    me.query("#releasePostConfigPanel")[0].setHidden(false);//指标对应显示出来
                                }
                            }
                        } else if (type == "signUp") {//竞聘报名
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.competition.templates.apply_template = me.tabId;
                                    me.query("#signUpLabelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName + '</span>');
                                    me.node.data.leaf = false;
                                    if (me.configParam.competition.templatesRelation) {//指标对应关系清空
                                        me.configParam.competition.templatesRelation.apply_template = {};
                                    }
                                    me.query("#signUpConfigPanel")[0].setHidden(false);
                                }
                            }
                        } else if (type == "employApproval") {//录用审批
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.competition.templates.hire_template = me.tabId;
                                    me.query("#employApprovalLabelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName + '</span>');
                                    me.node.data.leaf = false;
                                    if (me.configParam.competition.templatesRelation) {//指标对应关系
                                        me.configParam.competition.templatesRelation.hire_template = {};
                                    }
                                    me.query("#employApprovalConfigPanel")[0].setHidden(false);//
                                }
                            }
                        } else if (type == "applyResume") {
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.competition.rnames.applyResume_rname = me.tabId;
                                    me.query("#applyResumeComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName + '</span>');
                                    me.node.data.leaf = false;
                                }
                            }

                        } else if (type == "postDetail") {//
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.competition.rnames.postDetail_ranme = me.tabId;
                                    me.query("#postDetailComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName  + '</span>');
                                    me.node.data.leaf = false;
                                }
                            }

                        } else if (type == "talentHallApply") {//人才市场的个人简历模版
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.talentHall.templates.apply_template = me.tabId;
                                    me.query("#talentHallApplyComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName  + '</span>');
                                    me.node.data.leaf = false;
                                }
                            }
                        } else if (type == "talentHallCancel") {//人才市场取消模版
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.talentHall.templates.cancel_template = me.tabId;
                                    me.query("#talentHallCancelComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName  + '</span>');
                                    me.node.data.leaf = false;
                                }
                            }
                        } else if (type == "talentRname") {
                            if(me.node){
                                if(me.node.data.leaf){
                                    me.configParam.talentHall.rnames.talent_rname = me.tabId;
                                    me.query("#talentRnameComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + me.tabName   + '</span>');
                                    me.node.data.leaf = false;
                                }
                            }
                        }
                        Ext.getCmp("modelWin").close();
                    }
                },
                {
                    text: tm.config.msg.cancel, handler: function () {
                        Ext.getCmp("modelWin").close();
                    }
                }
            ]
        }).show();
    },
    /**
     * 指标对应界面展现
     * @Author xuchangshun
     * @param type 要配置指标对应模板的类型
     * @Date 2019/8/19 10:41
     */
    itemCorresponding: function (type) {
        var gridRowIndex = {};
        var tabid = '';
        if (type === 'post') {//岗位发布
            tabid = me.configParam.competition.templates.releasePost_template;
            if(!me.postConfigurable){//当前模板处于流程中，不允许指标对应
                Ext.Msg.alert(tm.tip,tm.config.notIndicatorCorr);
                return;
            }
        } else if (type === 'signUp') {//竞聘报名
            tabid = me.configParam.competition.templates.apply_template;
            if(!me.applyConfigurable){//当前模板处于流程中，不允许指标对应
                Ext.Msg.alert(tm.tip,tm.config.notIndicatorCorr);
                return;
            }
        } else if (type === 'employApproval') {//拟录用审批
            tabid = me.configParam.competition.templates.hire_template;
            if(!me.hireConfigurable){//当前模板处于流程中，不允许指标对应
                Ext.Msg.alert(tm.tip,tm.config.notIndicatorCorr);
                return;
            }
        }
        var gwfbTabid = me.configParam.competition.templates.releasePost_template;
        var jpbmTabid = me.configParam.competition.templates.apply_template;
        var lyspTabid = me.configParam.competition.templates.hire_template;
        var indicatorCorrWin = Ext.create("Ext.window.Window", {//指标对应弹窗
            id: 'indicatorCorrWin',
            title: tm.config.indicatorCorrespondence,
            width: 600,
            autoHeight: true,
            modal: true,
            layout: 'form',
            resizable: false
        });
        var templateItems = [];//用于存储指标模板combobox数据

        var destStore = Ext.create("Ext.data.Store", {//combobox数据域
            fields: ['DestItemId', 'fieldHz'],
            data: templateItems
        });
        //岗位发布数据
        var gwfbData = [
            /**
             {
                'serialNumber': '1',
                'sourceItemId': 'e0122',
                'sourceItemDesc': tm.config.e0122,
                'DestItemId': ''
            },
             {
                'serialNumber': '2',
                'sourceItemId': 'e01a1',
                'sourceItemDesc': tm.config.e01a1,
                'DestItemId': ''
            },**/
            {
                'sourceItemId': 'z8105',
                'sourceItemDesc': tm.config.z8105,
                'DestItemId': '',
                'DestItemIdDesc':''
            },
            {
                'sourceItemId': 'z8107',
                'sourceItemDesc': tm.config.z8107,
                'DestItemId': '',
                'DestItemIdDesc':''
            },
            /**
             {
                'serialNumber': '5',
                'sourceItemId': 'z8109',
                'sourceItemDesc': tm.config.z8109,
                'DestItemId': ''
            },**/
            {
                'sourceItemId': 'z8101',
                'sourceItemDesc': tm.config.z8101,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'interviewArrangement',
                'sourceItemDesc': tm.config.interviewArrangement,
                'DestItemId': '',
                'DestItemIdDesc':''
            }];
        //竞聘报名数据
        var jpbmData = [{
            'sourceItemId': 'z8101',
            'sourceItemDesc': tm.config.competitivePostNumber,
            'DestItemId': '',
            'DestItemIdDesc':''
        },
            {
                'sourceItemId': 'z8301',
                'sourceItemDesc': tm.config.personUniqueIdentifier,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'b0110',
                'sourceItemDesc': tm.config.competitionUnit,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'e0122',
                'sourceItemDesc': tm.config.competitionPosition,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'e01a1',
                'sourceItemDesc': tm.config.competitivePost,
                'DestItemId': '',
                'DestItemIdDesc':''
            }];
        //录用审批数据
        var lyspData = [{
            'sourceItemId': 'z8101',
            'sourceItemDesc': tm.config.competitivePostNumber,
            'DestItemId': '',
            'DestItemIdDesc':''
        },
            {
                'sourceItemId': 'z8301',
                'sourceItemDesc': tm.config.personUniqueIdentifier,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'b0110',
                'sourceItemDesc': tm.config.competitionUnit,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'e0122',
                'sourceItemDesc': tm.config.competitionPosition,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'e01a1',
                'sourceItemDesc': tm.config.competitivePost,
                'DestItemId': '',
                'DestItemIdDesc':''
            },{
                'sourceItemId': 'interviewScore',
                'sourceItemDesc': tm.config.interviewScore,
                'DestItemId': '',
                'DestItemIdDesc':''
            }];

        //对应指标数据回显
        me.dataEcho(tabid, gwfbData, jpbmData, lyspData, gwfbTabid, jpbmTabid, lyspTabid);

        var indicatorCorrStore = Ext.create("Ext.data.Store", {
            storeId: 'indicatorCorrStore',
            fields: ['serialNumber', 'sourceItemId', 'sourceItemDesc', 'DestItemId'],
            data: []
        });

        var indicatorCorrGrid = {};
        if (type === "post") {
            indicatorCorrGrid = Ext.create("Ext.grid.Panel", {
                id: 'indicatorCorrGrid',
                store: indicatorCorrStore,
                columnLines: true,
                rowLines: true,
                enableColumnResize:false,
                sortableColumns:false,
                style: 'margin:10 0 0 0;',
                plugins: [Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1})],
                columns: [
                    {text: tm.config.internalCompetitivePosition, dataIndex: 'sourceItemDesc', flex: 1,menuDisabled:true,renderer: function(value, metadata, record, rowIndex, columnIndex, store) {
                            var text = '';
                            switch (rowIndex) {
                                case 0:
                                    text = tm.config.numericalType;
                                    break;
                                case 1:
                                    text = tm.config.numericalType;
                                    break;
                                case 2:
                                    text = tm.config.characterTypeOne;
                                    break;
                                default:
                                    text = tm.config.optionalRefillType;
                            }
                            return '<span data-qtip="'+text +'">'+value+'</span>';
                        }},
                    {
                        text: tm.config.postCompetitiveTemplate,
                        dataIndex: 'DestItemId',
                        flex: 1,
                        menuDisabled:true,
                        xtype: 'widgetcolumn',
                        widget: {
                            xtype: 'combobox',
                            store: destStore,
                            width: 250,
                            editable: false,
                            emptyText:'请选择...',
                            displayField: 'fieldHz',
                            valueField: 'DestItemId',
                            listeners: {
                                'select': function (combo, record, eOpts) {
                                    //indicatorCorrStore.getAt(gridRowIndex).set('DestItemId', record.data.DestItemId);
                                    combo.getWidgetRecord().set(combo.dataIndex,record.data.DestItemId);
                                },
                                'render':function (t) {
                                    return t.setValue(t.getWidgetRecord().get("DestItemIdDesc"));
                                }
                            }
                        }
                    }],
                listeners: {
                    'cellclick': function (t, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                        gridRowIndex = rowIndex;
                        var vo = new HashMap();
                        if (rowIndex == 0 || rowIndex == 1 ) {
                            vo.put("fieldType", "N");
                        } else if(rowIndex == 2){
                            vo.put("fieldType", "A");
                            vo.put("changeType","afterChange");
                        }else{
                            vo.put("fieldType", "M");
                            vo.put("changeType","afterChange");
                        }
                        vo.put("tabid", tabid);
                        vo.put("type", "indicatorCorr");
                        Rpc({
                            functionId: 'TM000000006', success: function (result) {
                                var resData = Ext.decode(result.responseText);
                                var map = new HashMap();
                                map.put("fieldHz", "请选择...");
                                map.put("DestItemId", "");
                                templateItems.push(map);
                                for(var i=0;i<resData.templateItems.length;i++){
                                    templateItems.push(resData.templateItems[i]);
                                }
                                //templateItems = resData.templateItems;
                            }, scope: this, async: false
                        }, vo);
                        destStore.setData(templateItems);
                        destStore.filterBy(function (recordData,index) {
                            var destItemId = [];
                            var indicatorItems = Ext.getCmp('indicatorCorrGrid').getStore().getData().items;

                            for (var i=0;i<indicatorItems.length;i++){
                                destItemId.push(indicatorItems[i].data.DestItemId);
                            }
                            if(recordData.get('DestItemId') == ''){
                                return true;
                            }else {
                                return Ext.Array.indexOf(destItemId,recordData.get('DestItemId')) == -1;
                            }
                        });
                        templateItems = [];
                    }
                }
            });
            indicatorCorrGrid.getStore().setData(gwfbData);
        } else if (type === "signUp") {
            var columnText = tm.config.competitionRegistrationTemplate;
            indicatorCorrGrid = me.getIndicatorCorrGrid(indicatorCorrStore, destStore, templateItems, gridRowIndex, tabid, columnText);
            indicatorCorrGrid.getStore().setData(jpbmData);
        } else if (type === "employApproval") {
            var columnText = tm.config.employmentApprovalTemplate;
            indicatorCorrGrid = me.getIndicatorCorrGrid(indicatorCorrStore, destStore, templateItems, gridRowIndex, tabid, columnText);
            indicatorCorrGrid.getStore().setData(lyspData);
        }
        var tools = Ext.create("Ext.toolbar.Toolbar", {
            border: false,
            layout: {
                type: 'hbox',
                pack: 'center'
            },
            style: 'margin:20 0 0 0;',
            items: [{
                xtype: 'button',
                text: tm.config.msg.confirm,
                handler: function () {
                    me.confirmCorr(type);
                }
            }, {
                xtype: 'button',
                text: tm.config.msg.cancel,
                style: 'margin-left:20px;',
                handler: function () {
                    indicatorCorrWin.close();
                }
            }]
        });
        indicatorCorrWin.add(indicatorCorrGrid);
        indicatorCorrWin.add(tools);
        indicatorCorrWin.show();
    },
    /**
     * 指标对应回显
     * @Author xuchangshun
     * @param tabid
     * @param gwfbData
     * @param jpbmData
     * @param lyspData
     * @param gwfbTabid
     * @param jpbmTabid
     * @param lyspTabid
     * @Date 2019/8/19 11:38
     */
    dataEcho: function (tabid, gwfbData, jpbmData, lyspData, gwfbTabid, jpbmTabid, lyspTabid) {//指标对应数据回显
        var templateItems = [];
        var vo = new HashMap();
        vo.put("fieldType", "A-N-M");
        vo.put("tabid", tabid);
        vo.put("type", "indicatorCorr");
        Rpc({
            functionId: 'TM000000006', success: function (result) {
                var resData = Ext.decode(result.responseText);
                templateItems = resData.templateItems;
            }, scope: this, async: false
        }, vo);
        if ((me.configParam.competition.templatesRelation && me.temp_releasePost_template == gwfbTabid && tabid==gwfbTabid) || me.fillInAll) {//回显岗位发布已选指标
            for (var i = 0; i < templateItems.length; i++) {
                /**if (me.configParam.competition.templatesRelation.releasePost_template.e0122 == templateItems[i].DestItemId) {
                    gwfbData[0].DestItemId = templateItems[i].fieldHz;
                }
                 if (me.configParam.competition.templatesRelation.releasePost_template.e01a1 == templateItems[i].DestItemId) {
                    gwfbData[1].DestItemId = templateItems[i].fieldHz;
                }**/
                if (me.configParam.competition.templatesRelation.releasePost_template.z8105 == templateItems[i].DestItemId) {
                    gwfbData[0].DestItemId = templateItems[i].DestItemId;
                    gwfbData[0].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.releasePost_template.z8107 == templateItems[i].DestItemId) {
                    gwfbData[1].DestItemId = templateItems[i].DestItemId;
                    gwfbData[1].DestItemIdDesc = templateItems[i].fieldHz;
                }
                /**
                 if (me.configParam.competition.templatesRelation.releasePost_template.z8109 == templateItems[i].DestItemId) {
                    gwfbData[4].DestItemId = templateItems[i].fieldHz;
                }**/
                if (me.configParam.competition.templatesRelation.releasePost_template.z8101 == templateItems[i].DestItemId) {
                    gwfbData[2].DestItemId = templateItems[i].DestItemId;
                    gwfbData[2].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.releasePost_template.interviewArrangement == templateItems[i].DestItemId) {
                    gwfbData[3].DestItemId = templateItems[i].DestItemId;
                    gwfbData[3].DestItemIdDesc = templateItems[i].fieldHz;
                }
            }
        }
        //回显竞聘报名已选择指标
        if ((me.configParam.competition.templatesRelation && me.temp_apply_template == jpbmTabid && tabid==jpbmTabid) || me.fillInAll) {
            for (var i = 0; i < templateItems.length; i++) {
                if (me.configParam.competition.templatesRelation.apply_template.z8101 == templateItems[i].DestItemId) {
                    jpbmData[0].DestItemId = templateItems[i].DestItemId;
                    jpbmData[0].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.apply_template.z8301 == templateItems[i].DestItemId) {
                    jpbmData[1].DestItemId = templateItems[i].DestItemId;
                    jpbmData[1].DestItemIdDesc = templateItems[i].fieldHz;
                }

                if (me.configParam.competition.templatesRelation.apply_template.b0110 == templateItems[i].DestItemId) {
                    jpbmData[2].DestItemId = templateItems[i].DestItemId;
                    jpbmData[2].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.apply_template.e0122 == templateItems[i].DestItemId) {
                    jpbmData[3].DestItemId = templateItems[i].DestItemId;
                    jpbmData[3].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.apply_template.e01a1 == templateItems[i].DestItemId) {
                    jpbmData[4].DestItemId = templateItems[i].DestItemId;
                    jpbmData[4].DestItemIdDesc = templateItems[i].fieldHz;
                }
            }
        }
        //回显录用审批已选择指标
        if ((me.configParam.competition.templatesRelation && me.temp_hire_template == lyspTabid && tabid==lyspTabid ) || me.fillInAll) {
            for (var i = 0; i < templateItems.length; i++) {
                if (me.configParam.competition.templatesRelation.hire_template.z8101 == templateItems[i].DestItemId) {
                    lyspData[0].DestItemId = templateItems[i].DestItemId;
                    lyspData[0].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.hire_template.z8301 == templateItems[i].DestItemId) {
                    lyspData[1].DestItemId = templateItems[i].DestItemId;
                    lyspData[1].DestItemIdDesc = templateItems[i].fieldHz;
                }

                if (me.configParam.competition.templatesRelation.hire_template.b0110 == templateItems[i].DestItemId) {
                    lyspData[2].DestItemId = templateItems[i].DestItemId;
                    lyspData[2].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.hire_template.e0122 == templateItems[i].DestItemId) {
                    lyspData[3].DestItemId = templateItems[i].DestItemId;
                    lyspData[3].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.hire_template.e01a1 == templateItems[i].DestItemId) {
                    lyspData[4].DestItemId = templateItems[i].DestItemId;
                    lyspData[4].DestItemIdDesc = templateItems[i].fieldHz;
                }
                if (me.configParam.competition.templatesRelation.hire_template.interviewScore == templateItems[i].DestItemId) {
                    lyspData[5].DestItemId = templateItems[i].DestItemId;
                    lyspData[5].DestItemIdDesc = templateItems[i].fieldHz;
                }
            }
        }
    },
    /**
     * 创建指标对应gridPanel
     * @Author xuchangshun
     * @param indicatorCorrStore
     * @param destStore
     * @param templateItems
     * @param gridRowIndex
     * @param tabid
     * @return
     * @throws
     * @Date 2019/8/19 11:16
     */
    getIndicatorCorrGrid: function (indicatorCorrStore, destStore, templateItems, gridRowIndex, tabid, columnText) {
        var indicatorCorrGrid = Ext.create("Ext.grid.Panel", {
            id: 'indicatorCorrGrid',
            store: indicatorCorrStore,
            columnLines: true,
            rowLines: true,
            enableColumnResize:false,
            sortableColumns:false,
            style: 'margin:10 0 0 0;',
            plugins: [Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1})],
            columns: [
                {text: tm.config.applicantTable, dataIndex: 'sourceItemDesc', flex: 1,menuDisabled:true,renderer: function(value, metadata, record, rowIndex, columnIndex, store) {
                        var text = '';
                        switch (rowIndex) {
                            case 0:
                                text = tm.config.characterTypeOne;
                                break;
                            case 1:
                                text = tm.config.characterTypeTwo;
                                break;
                            case 5:
                                text = tm.config.optionalNumericalType;
                                break;
                            default:
                                text = tm.config.optionalCodeType ;
                        }
                        return '<span data-qtip="'+text +'">'+value+'</span>';
                    }},
                {
                    text: columnText,
                    dataIndex: 'DestItemId',
                    flex: 1,
                    menuDisabled:true,
                    xtype: 'widgetcolumn',
                    widget: {
                        xtype: 'combobox',
                        store: destStore,
                        width: 250,
                        editable: false,
                        emptyText:'请选择...',
                        displayField: 'fieldHz',
                        valueField: 'DestItemId',
                        listeners: {
                            'select': function (combo, record, eOpts) {
                                // indicatorCorrStore.getAt(gridRowIndex).set('DestItemId', record.data.DestItemId);
                                combo.getWidgetRecord().set(combo.dataIndex,record.data.DestItemId);
                            },
                            'render':function (t) {
                                return t.setValue(t.getWidgetRecord().get("DestItemIdDesc"));
                            }
                        }
                    }

                }],
            listeners: {
                'cellclick': function (t, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                    gridRowIndex = rowIndex;
                    var vo = new HashMap();
                    vo.put("fieldType", "A");
                    vo.put("tabid", tabid);
                    vo.put("type", "indicatorCorr");
                    vo.put("changeType",'afterChange');
                    if(rowIndex == 1){
                        vo.put("lengthLimit",'lengthLimit');
                    }else if(rowIndex == 2){
                        //vo.put("changeType",'0');
                        vo.put("searchType","un");
                    }else if(rowIndex == 3){
                        vo.put("searchType","um");
                    }else if(rowIndex == 4){
                        vo.put("searchType","@k");
                    }else if(rowIndex == 5){
                        vo.put("fieldType","N");
                        vo.put("changeType",'');
                    }
                    var p = '';
                    Rpc({
                        functionId: 'TM000000006', success: function (result) {
                            var resData = Ext.decode(result.responseText);
                            var map = new HashMap();
                            map.put("fieldHz", "请选择...");
                            map.put("DestItemId", "");
                            map.put("DestItemIdDesc", "");
                            templateItems.push(map);
                            for(var i=0;i<resData.templateItems.length;i++){
                                templateItems.push(resData.templateItems[i]);
                            }
                            //templateItems = resData.templateItems;
                        }, scope: this, async: false
                    }, vo);

                    destStore.setData(templateItems);
                    destStore.filterBy(function (recordData,index) {
                        var destItemId = [];
                        var indicatorItems = Ext.getCmp('indicatorCorrGrid').getStore().getData().items;
                        for (var i=0;i<indicatorItems.length;i++){
                            destItemId.push(indicatorItems[i].data.DestItemId);
                        }
                        if(recordData.get('DestItemId') == ''){
                            return true;
                        }else {
                            return Ext.Array.indexOf(destItemId,recordData.get('DestItemId')) == -1;
                        }
                    });
                    templateItems = [];
                }
            }
        });
        return indicatorCorrGrid;
    },
    /**
     * 指标对应确定按钮事件
     * @Author xuchangshun
     * @param type
     * @Date 2019/8/19 11:45
     */
    confirmCorr: function (type) {
        var templatesRelation = me.configParam.competition.templatesRelation;//暂存对应信息
        var storeData = Ext.getCmp('indicatorCorrGrid').getStore().getData();
        for (var i = 0; i < storeData.items.length; i++) {
            if(type == 'post'){
                if ((!storeData.items[0].data.DestItemId) || (!storeData.items[1].data.DestItemId) || (!storeData.items[2].data.DestItemId)) {//未全部对应指标提示
                    me.configParam.competition.templatesRelation = templatesRelation;
                    Ext.showAlert(tm.config.indicatorCorrAlert);
                    me.fillInAll = false;
                    return;
                } else {
                    me.fillInAll = true;
                }
            }else{
                if ((!storeData.items[0].data.DestItemId) || (!storeData.items[1].data.DestItemId)) {//未全部对应指标提示
                    me.configParam.competition.templatesRelation = templatesRelation;
                    Ext.showAlert(tm.config.indicatorCorrAlert);
                    me.fillInAll = false;
                    return;
                } else {
                    me.fillInAll = true;
                }
            }

            if (type == 'post') {//岗位发布指标对应，修改json
                if (escape(storeData.items[i].data.DestItemId).indexOf("%u")<0) {//排除中文
                    me.configParam.competition.templatesRelation.releasePost_template[storeData.items[i].data.sourceItemId] = storeData.items[i].data.DestItemId;
                }
            } else if (type == 'signUp') {//竟品报名修改json
                if (escape(storeData.items[i].data.DestItemId).indexOf("%u")<0) {
                    me.configParam.competition.templatesRelation.apply_template[storeData.items[i].data.sourceItemId] = storeData.items[i].data.DestItemId;
                }
            } else if (type == 'employApproval') {//录用审批修改json
                if (escape(storeData.items[i].data.DestItemId).indexOf("%u")<0) {
                    me.configParam.competition.templatesRelation.hire_template[storeData.items[i].data.sourceItemId] = storeData.items[i].data.DestItemId;
                }
            }
        }
        if (Ext.getCmp("indicatorCorrWin")) {
            Ext.getCmp("indicatorCorrWin").close();
        }
    },
    /**
     * 选择展现指标
     * @Author xuchangshun
     * @param type 要选择指标的类型 包括[竞聘岗位详情展示项、人才展厅个人简介展示项、人才展厅岗位类别展示项]
     * @param boxOrRadio 代表是checkbox还是checkradio即多选还是单选
     * @Date 2019/8/19 16:34
     */
    chooseFieldItemJpgw:function(type,boxOrRadio) {
        var me = this;
        // 指标数据
        var data = [];
        var jpgwStore = Ext.create("Ext.data.Store", {//combobox数据域
            fields: ['itemid', 'itemdesc'],
            data: data
        });
        var param = new HashMap();
        param.put("type",type);
        Rpc({
            functionId: 'TM000000007', success: function (res) {
                var resData = Ext.decode(res.responseText);
                data = resData.data;
                jpgwStore.setData(data);
            }, scope: this, async: false
        }, param);
        // 渲染拖拽功能
        var viewConfig = {
            plugins:{
                ptype:'gridviewdragdrop',
                dragText:tm.config.msg.dragAndDrop// 拖放数据
            },
            listeners: {
                beforedrop:me.adjustsec
            }
        };
        if(type!="Z81"){
            viewConfig={};
        }
        //创建panel
        var fieldItemGrid=Ext.create('Ext.grid.Panel',{
            id:'fieldItemPanel',
            height: 360,
            width: 240,
            scrollable:'y',
            renderTo: Ext.getBody(),
            store: jpgwStore,
            viewConfig:viewConfig,
            enableColumnResize:false,
            sortableColumns:false,
            selModel:{
                selType:'checkboxmodel',
                mode: boxOrRadio,// multi,simple,single；默认为多选multi
                checkOnly: false,// 如果值为true，则只用点击checkbox列才能选中此条记录
                enableKeyNav: true,// 开启/关闭在网格内的键盘导航。
                allowDeselect: true,//是否允许取消选择
            },
            columns: [
                { text: '指标id', dataIndex: 'itemid',width:20,flex:1,hidden:true,menuDisabled:true},
                { text: '项目名称', dataIndex: 'itemdesc',flex:1,menuDisabled:true}
            ],
            buttonAlign:"center",
            buttons:[{
                text:tm.config.msg.confirm,handler:function(){
                    if(type=="Z81"){
                        me.configParam.competition.postFields=[];
                    }else if(type=="A01"){
                        me.configParam.talentHall.resumeSelfIntroduction=[];
                    }else if(type=="K01"){
                        me.configParam.talentHall.resumePostTypeField=[];
                    }
                    var str="";
                    var selected=Ext.getCmp('fieldItemPanel').getSelectionModel().selected.items;
                    for(var i=0;i<selected.length;i++){
                        var data=selected[i].data;
                        str+=data.itemdesc+"、";
                        var item=data.itemid;
                        if(type=="Z81"){
                            me.configParam.competition.postFields.push(item);
                        }else if(type=="A01"){
                            me.configParam.talentHall.resumeSelfIntroduction.push(item);
                        }else if(type=="K01"){
                            me.configParam.talentHall.resumePostTypeField.push(item);
                        }
                    }
                    str=str.slice(0,-1);
                    if(type=="Z81"){
                        me.query("#postFieldsComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + str + '</span>');
                    }else if(type=="A01"){
                        me.query("#myResumeFieldsComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + str + '</span>');
                    }else if(type=="K01"){
                        me.query("#resumePostTypeFieldComponent")[0].setHtml('<span style="color:#666666;" width="80%" align="left">' + str + '</span>');
                    }
                    win.close();
                }},{
                text:tm.config.msg.cancel,handler:function(){win.close();}
            }]
        });
        var windowTitle = '';
        var itemids = [];
        if(type == "Z81"){
            windowTitle = tm.config.selectPostFields;
            itemids = me.configParam.competition.postFields
        }else if(type == "A01"){
            windowTitle = tm.config.selectResumeSelfIntroduction;
            itemids = me.configParam.talentHall.resumeSelfIntroduction
        }else{
            windowTitle = tm.config.selectResumePostTypeField;
            itemids = me.configParam.talentHall.resumePostTypeField
        }
        //回显已勾选的配置
        var grid=Ext.getCmp("fieldItemPanel");
        var model=grid.getSelectionModel();
        me.model = model;
        var itemData=grid.getStore().data.items;
        for(var i=0;i<itemids.length;i++){
            var itemid=itemids[i];
            for(var j=0;j<itemData.length;j++){
                if(itemData[j].data.itemid==itemid){
                    model.select(j,true);
                    break;
                }
            }
        }
        var win = Ext.widget('window',{
            title:windowTitle,
            height:400,
            width:300,
            layout:'fit',
            modal : true,
            items:fieldItemGrid
        }).show();
    },
    /**
     * 拖拽排序功能
     * @Author xuchangshun
     * @param node
     * @param data
     * @param overModel
     * @param dropPosition
     * @param dropHandlers
     * @Date 2019/8/19 16:47
     */
    adjustsec:function(node, data, overModel, dropPosition, dropHandlers){
        var oriItemArr = [];
        for(var i=0;i<data.records.length;i++){
            oriItemArr.push(data.records[i].get("itemid"));
        }
        //var ori_itemid=data.records[0].get("itemid");
        var to_itemid=overModel.get('itemid');
        var param = new HashMap();
        //param.put("ori_itemid",ori_itemid);
        param.put("oriItemArr",oriItemArr);
        param.put("to_itemid",to_itemid);
        param.put("dropPosition",dropPosition);
        Rpc({functionId:'TM000000008',success:function(){
                Ext.Msg.alert(tm.tip,tm.config.msg.saveSuccess);
            },scope:this},param);
    },
    /**
     * 创建人才展厅配置panel界面
     * @Author xuchangshun
     * @Date 2019/8/19 17:02
     */
    createTalentHallPanel:function () {
        var talentHallPanel = Ext.create("Ext.Panel", {
            margin: '0 0 70 0',
            width: '90%',
            minWidth:800,
            border: false,
            bodyBorder: false,
            layout: {//采用纵向布局
                type: 'vbox'
            },
            //title: tm.config.title.talentHall,
            items:[
                {
                    id:'talentHallTitlePn',
                    border:false,
                    width: '84%',
                    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="20%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+tm.config.title.talentHall+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'
                },
                {
                    xtype: 'panel',
                    itemId: 'talentHallApply_template',//个人简历申请模版
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.tapply_template + '</span>'//申请模板
                        },
                        {
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            width: '300px',
                            itemId: 'talentHallApplyComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.templateSelect(\'talentHallApply\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    itemId: 'talentHallCancel_template',//个人简历撤销模版
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.cancel_template + '</span>'//撤销模板
                        },
                        {
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            width: '300px',
                            itemId: 'talentHallCancelComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.templateSelect(\'talentHallCancel\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    itemId: 'myResumeFieldsPanel',//个人简历展示项
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.resumeSelfIntroduction + '</span>'//个人简历展示项
                        },
                        {//用于填充与左侧的距离
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            width: '300px',
                            itemId: 'myResumeFieldsComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.chooseFieldItemJpgw(\'A01\',\'single\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    itemId: 'resumePostTypeFieldPanel',//岗位类别展示项
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.resumePostTypeField + '</span>'//岗位类别展示项
                        },
                        {//用于填充与左侧的距离
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            width: '300px',
                            itemId: 'resumePostTypeFieldComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.chooseFieldItemJpgw(\'K01\',\'single\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    itemId: 'talentRname',//简历登记表
                    margin: '10 0 0 50',
                    width: '100%',
                    border: false,
                    bodyBorder: false,
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: '20%',
                            style: 'text-align:right',
                            html: '<span>' + tm.config.talent_rname + '</span>'//简历登记表
                        },
                        {
                            xtype: 'component',
                            width: 20
                        },
                        {
                            xtype: 'component',
                            style: 'text-align:left',
                            width: '300px',
                            itemId: 'talentRnameComponent',
                            html: ''
                        },
                        {xtype: 'component', flex: 1},
                        {
                            xtype: 'component',
                            width: '20%',
                            html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);me.templateSelect(\'talentRname\')">' + tm.config.setting + '</span>'//配置
                        }
                    ]
                }
            ]
        });
        me.add(talentHallPanel);
    },
    checkConfigurable:function () {
        var vo = new HashMap();
        vo.put("type","checkConfigurable");
        /*vo.put("postTabid",me.temp_releasePost_template);
        vo.put("applyTabid",me.temp_apply_template);
        vo.put("hireTabid",me.temp_hire_template);*/
        Rpc({functionId: 'TM000000006', success: function (res) {
                var resultData = Ext.decode(res.responseText);
                var checkData = resultData.checkData;
                me.postConfigurable = checkData.postConfigurable;
                me.applyConfigurable = checkData.applyConfigurable;
                me.hireConfigurable = checkData.hireConfigurable;
            }, scope: this,async:false}, vo);
    },
    changeApproveStatus:function (imgtype) {
        var img = document.getElementById(imgtype);
        if(imgtype == 'quickApproveImg'){
            if(!me.postConfigurable && !me.configParam.competition.quickApprove){
                Ext.Msg.alert(tm.tip,tm.config.openQuickApprove);
                return;
            }else if(!me.postConfigurable && me.configParam.competition.quickApprove){
                Ext.Msg.alert(tm.tip,tm.config.closeQuickApprove);
                return;
            }
            if(!me.configParam.competition.quickApprove){
                img.src = "../images/turnOn.png";
                me.configParam.competition.quickApprove = true;
                me.query("#releasePostPanel")[0].setHidden(true);
            }else{
                img.src = "../images/shutDown.png";
                me.configParam.competition.quickApprove = false;
                me.query("#releasePostPanel")[0].setHidden(false);
            }
        }else{
            if(me.configParam.competition.openInterview){
                img.src = "../images/shutDown.png";
                me.configParam.competition.openInterview = false;
            }else{
                img.src = "../images/turnOn.png";
                me.configParam.competition.openInterview = true;
            }
        }

       // me.query("#quickApprovalLabelComponent")[0].setHtml('<img id="quickApproveImg" style="width: 45px;height: 45px;" src="../images/turnOn.png" onclick="javascript:void(0);me.changeApproveStatus();"/>');
    }
});

