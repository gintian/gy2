/**
 * 新建年度计划页面布局
 * 
 */
Ext.define('YearPlan.CreateYearPlan', {
    funcpriv : undefined,
    depredutyUion : [], // 记录责任单位已添加的单位 //'dutyUion',//责任单位
                        // 'leadUion',//牵头单位'leader',//公司领导
    depreleadUion : [],// 记录牵头单位已添加的单位
    depreleader : [],// 记录已添加的公司领导
    depreAppor : [],// 记录审批人
    deperResponse : [],// 责任人
    deperAuditor : [],// 审核人
    year : '',// 年份
    items : undefined,
    editflag : false,
    typeflag : '',// 进入当前页面类型
    orgid : '',
    p1700 : '',// 年计划id
    columns:undefined,
    constructor : function (config) {
        CreatePlan = this;
        Ext.apply(this, config);
        CreatePlan.items = this.createFillItem();
        this.createMainPanel();
    },
    createMainPanel : function () {
        var me = this;
        var title;
        if (me.typeflag == 'edit') {
            title = '编辑任务';
        } else if (me.typeflag == 'taskAssign') {
            title = '任务指派';
        } else {
            title = '新建任务';
        }
        me.panel = Ext.widget('panel', {
            id:'myPanel',
            title : title,
            layout : {
                type : 'vbox',
                align : 'center'
            },
            width : '90%',
            height : '90%',
            scrollable : "y",
            style : 'background-color:#0000FF',
            tools : [ (!me.editflag) ? {
                xtype : 'label',
                html : '<a href=javascript:CreatePlan.saveYearPlan("1");>保存</a>',
                margin : '0 5 0 5'/* ,flex:4 */
            } : {
                xtype : 'label',
                html : ''
            }, (!me.editflag && me.typeflag != 'taskAssign') ? {
                xtype : 'label',
                html : '<a href=javascript:CreatePlan.saveYearPlan("4");>保存并发布</a>',
                margin : '0 5 0 5'/* ,flex:4 */
            } : {
                xtype : 'label',
                html : ''
            }, {
                xtype : 'label',
                html : '<a href=javascript:CreatePlan.backYearPlan();>返回</a>',
                margin : '0 5 0 5'/* ,flex:4 */
            } ],
            items : [ {
                xtype : 'form',
                width : 600,
                id : 'formV',
                minWidth : 450,
                border : false,
                style : 'margin-Top:25px',
                layout : {
                    type : 'vbox',
                    align : 'left'
                },
                items : me.items
            } ]
        });
        Ext.widget("viewport", {
            layout : 'fit',
            id : "mainPanel",
            items : [ me.panel ]
        });
        Ext.on('resize', function (width, height) {
            var mainPanel = Ext.getCmp('mainPanel');
            mainPanel.setWidth(width);
            mainPanel.setHeight(height);
        });

        if (me.typeflag == 'edit' || me.typeflag == 'taskAssign') {
            var maps = new HashMap();
            maps.put("operaflag", "edit");
            maps.put("planid", me.p1700);
            Rpc({
                functionId : 'WP00002001',
                async : false,
                success : function (res) {
                    var res = Ext.decode(res.responseText);
                    var data = res.list[0];// 交易类查询出项目任务等内容
                    for (var i = 0; i < me.columns.length; i++) {
                        var fitem = me.columns[i];
                        var el = Ext.getCmp(fitem.itemid);
                        if(!el)//组件不存在
                            continue;
                        //回显代码型指标 haosl 20170206
                        if(fitem.itemtype == "A" && fitem.codesetid != '0'){
                            el.treeStore.on('load',function(){
                                el.setValue(data[fitem.itemid]);
                                var record = el.treeStore.findRecord("id",data[fitem.itemid]+"");
                                if(record)
                                    el.setRawValue(record.data.text);
                            },this,{single:true});
                            el.treeStore.load();
                        }else{
                           el.setValue(data[fitem.itemid]);
                        }
                    }
                    if (res.list[1]) {
                        me.orgid = res.list[1].orgid;//
                        var typelist = res.list[1].typelist;
                        for (var i = 0; i < typelist.length; i++) {
                            var dutyUion = [];// 责任单位数组集合
                            var leadUion = [];// 牵头单位
                            var approver;// 审批人
                            var leader;// 公司领导
                            var approver;// 审批人
                            var obj = {
                                id : '',
                                name : '',
                                rawType : ''
                            }
                            obj.id = typelist[i].typeid;
                            obj.name = typelist[i].codedesc;
                            obj.rawType = typelist[i].codename;
                            var personobj = {
                                id : '',
                                name : '',
                                type : '',
                                photo : ''
                            };
                            // personobj.id=typelist[i].typeid;
                            personobj.type = 'persion';

                            if (typelist[i].type == '0') {// 责任单位
                                dutyUion.push(obj);
                                me.getOrgList(dutyUion, "dutyUion", true);
                            }
                            if (typelist[i].type == '1') {// 牵头单位
                                leadUion.push(obj);
                                me.getOrgList(leadUion, 'leadUion', true);
                            }
                            if (typelist[i].type == '8') {// 审批人
                                personobj.name = typelist[i].name;
                                personobj.id = typelist[i].ids;
                                personobj.photo = "/servlet/DisplayOleContent?nbase=" + typelist[i].nbase + "&a0100=" + typelist[i].id + "&quality=1&caseNullImg=/images/photo.jpg&imageResize=55`55"
                                me.getOrgList(personobj, "approver", false);// 渲染审批人头像
                            }
                            if (typelist[i].type == '2') {// 公司领导
                                personobj.name = typelist[i].name;
                                personobj.id = typelist[i].ids;
                                personobj.photo = "/servlet/DisplayOleContent?nbase=" + typelist[i].nbase + "&a0100=" + typelist[i].id + "&quality=1&caseNullImg=/images/photo.jpg&imageResize=55`55"
                                me.getOrgList(personobj, "leader", false);// 渲染公司领导头像
                            }

                            if (typelist[i].type == '3') {// 审核人
                                personobj.name = typelist[i].name;
                                personobj.id = typelist[i].ids;
                                personobj.photo = "/servlet/DisplayOleContent?nbase=" + typelist[i].nbase + "&a0100=" + typelist[i].id + "&quality=1&caseNullImg=/images/photo.jpg&imageResize=55`55"
                                me.getOrgList(personobj, "auditor", false);// 渲染头像
                            }
                            if (typelist[i].type == '7') {// 责任人
                                var responObj = res.list[2];
                                var personobj = {
                                    id : '',
                                    name : '',
                                    type : '',
                                    photo : '',
                                    dept : '',// 责任室/作业区
                                    post : '',// 责任岗位
                                    e0122 : '',// 部门
                                    e01a1 : ''
                                }; // 责任人需要带入 责任岗位 责任组 责任室
                                personobj.dept = data.p1725;
                                personobj.post = data.p1729;
                                personobj.e0122 = responObj.obj4;
                                personobj.e01a1 = responObj.obj6;
                                personobj.name = typelist[i].name;
                                personobj.id = typelist[i].ids;
                                personobj.photo = "/servlet/DisplayOleContent?nbase=" + typelist[i].nbase + "&a0100=" + typelist[i].id + "&quality=1&caseNullImg=/images/photo.jpg&imageResize=55`55"
                                me.getOrgList(personobj, "responsible", false);// 渲染头像

                            }
                        }

                    }

                },
                scope : me
            }, maps)

        }
        if (me.typeflag == 'edit' || me.typeflag == '') {// 新建与编辑 选择单位
                                                            // 牵头单位关联操作人权限
            var maps = new HashMap();
            maps.put("operaflag", "getorgid");
            Rpc({
                functionId : 'WP00002001',
                async : false,
                success : function (res) {
                    var params = Ext.decode(res.responseText);
                    me.recommendOrgid = params.recommendOrgid;
                },
                scope : me
            }, maps);
        }

    },
    selectPerson : function (id) {
        var me = this;
        var deprecate;
        var flag = false;// 新建时 审批人标记
        // 区分不同选择人使用控件记录选人数组
        if (id == 'approver') {// 审批人
            var map = new HashMap();
            map.put('operaflag', 'decrpt');// 审批人 是牵头单位下的人员
            map.put('orglist', me.depreleadUion);
            if (me.depreleadUion.length > 0)
                Rpc({
                    functionId : 'WP00002001',
                    async : false,
                    success : function (res) {
                        var res = Ext.decode(res.responseText);
                        me.orgid = res.decrylist;
                        flag = true;
                    },
                    scope : me
                }, map);
            else {
                Ext.Msg.alert('提示信息', "牵头单位不能为空！");
                return;
            }

            deprecate = me.depreAppor;
        } else if (id == 'leader')// 公司领导
            deprecate = me.depreleader;
        else if (id == 'responsible')// 责任人
            deprecate = me.deperResponse;
        else if (id == 'auditor')// 审核人
            deprecate = me.deperAuditor;// 审核人

        // 审批人、审核人、责任人不允许为同一人
        if (id == 'approver' || id == 'auditor' || id == 'responsible') {
            deprecate = [];
            for (var i = 0; i < me.depreAppor.length; i++) {
                if (me.depreAppor[i] != null && me.depreAppor[i] != '') {
                    deprecate.push(me.depreAppor[i])
                }
            }
            for (var i = 0; i < me.deperAuditor.length; i++) {
                if (me.deperAuditor[i] != null && me.deperAuditor != '') {
                    deprecate.push(me.deperAuditor[i]);
                }
            }
            for (var i = 0; i < me.deperResponse.length; i++) {
                if (me.deperResponse[i] != null && me.deperResponse[i] != '') {
                    deprecate.push(me.deperResponse[i]);
                }
            }
        }

        var picker = new PersonPicker({
            multiple : false,
            deprecate : deprecate,
            orgid : ((me.typeflag == 'taskAssign' || flag) && (id == 'responsible' || id == 'auditor' || id == 'approver')) ? me.orgid : '',// 推荐显示人员
                                                                                                                                            // 任务指派
                                                                                                                                            // 责任人必须是牵头单位下的人
            titleText : "选择人员",
            isMiddle : true,
            isPrivExpression : false,// 不启用高级权限
            callback : function (c) {
                //兼容ie下选完人和单位后滚动条回到顶部的问题（渲染完头像panel的内容溢出，导致高度变化）。
                var d = Ext.getCmp('myPanel').body.dom;
                var offsite = d.scrollTop;//得到滚动天的位置
                me.getOrgList(c, id, false);
                d.scrollTop = offsite;
            }
        }, this);
        picker.open();

    },
    selectOrg : function (id) {// 选择组织机构
        var me = this;
        var picker = new PersonPicker({
            addunit : true,
            adddepartment : true,
            orgid : '',
            deprecate : id == 'dutyUion' ? me.depredutyUion : me.depreleadUion,
            addpost : false,// 不选择岗位
            multiple : true,
            text : '添加单位',
            orgid : id == 'leadUion' ? me.recommendOrgid : '',
            isPrivExpression : false,// 不启用高级权限
            callback : function (c) {
              //兼容ie下选完人和单位后滚动条回到顶部的问题（渲染完头像panel的内容溢出，导致高度变化）。
                var d = Ext.getCmp('myPanel').body.dom;
                var offsite = d.scrollTop;//得到滚动天的位置
                me.getOrgList(c, id, true);
                d.scrollTop = offsite;
            }
        });
        picker.open();

    },
    getOrgList : function (c, ids, flag) {
        var me = this;
        if (!Ext.util.CSS.getRule('.delImg')) {
            if (ids == "leader" || ids == "approver" || ids == "auditor" || ids == "responsible") {
                Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:50px !important; top:0px !important;}", "delImg");
            } else {
                Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:40px !important; top:0px !important;}", "delImg");
            }
        }
        var containers = Ext.getCmp(ids + 'container');
        if (!containers)
            containers = Ext.widget('container', {
                id : ids + 'container',
                layout : {
                    type : 'hbox',
                    align : 'center'
                }
            });
        ;
        if (ids == 'leader')// 公司领导
        {
            me.leadercontainers = Ext.getCmp(ids + 'container');
            if (!me.leadercontainers)
                me.leadercontainers = containers;
        } else if (ids == 'leadUion')// 牵头单位
        {
            me.leUioncontainers = Ext.getCmp(ids + 'container');
            if (!me.leUioncontainers)
                me.leUioncontainers = containers;
        } else if (ids == 'dutyUion') {// 责任单位
            me.duUioncontainers = Ext.getCmp(ids + 'container');
            if (!me.duUioncontainers)
                me.duUioncontainers = containers;
        } else if (ids == 'approver') {// 审批人
            me.approcontainers = Ext.getCmp(ids + 'container');
            if (!me.approcontainers)
                me.approcontainers = containers;
        } else if (ids == 'auditor') {// 审核人
            me.auditorcontainer = Ext.getCmp(ids + 'container');
            if (!me.auditorcontainer)
                me.auditorcontainer = containers;
        } else if (ids == 'responsible') {
            me.responscontainer = Ext.getCmp(ids + 'container');
            if (!me.responscontainer)
                me.responscontainer = containers;
        }

        if (flag) {// 选择单位
            for (var i = 0; i < c.length; i++) {
                var id = c[i].id;// 选中单位部门id
                var desc = c[i].name;// 选中单位部门的名称
                var type = c[i].rawType;// um/un

                if (ids == 'leadUion')
                    me.depreleadUion.push(id);
                if (ids == 'dutyUion')
                    me.depredutyUion.push(id);

                var delImg = Ext.create('Ext.Img', {
                    src : "/workplan/image/remove.png",
                    id : 'dele_' + ids + '_' + type + '_' + id,
                    style : 'cursor:pointer;',
                    width : 15,
                    height : 15,
                    hidden : true,
                    cls : 'delImg',
                    listeners : {
                        click : {
                            element : 'el',
                            fn : function (a, o, b, c) {
                                me.openDeletePersonMsg(o.id, ids);
                            }
                        },
                        mouseover : {
                            element : 'el',
                            fn : function (a, o) {
                                me.showHideDelImg(o.id, "1");
                            }
                        },
                        mouseout : {
                            element : 'el',
                            fn : function (a, o) {
                                me.showHideDelImg(o.id, "0");
                            }
                        }
                    }
                });

                var Image = Ext.widget('image', {
                    id : 'phot_' + ids + '_' + type + '_' + id,
                    title : desc,
                    width : 20,
                    height : 20,
                    src : type == 'UM' ? "/components/personPicker/image/dept.png" : "/components/personPicker/image/unit.png"

                });
                var childContainer = Ext.widget('container', {
                    id : 'cote_' + ids + '_' + type + '_' + id,
                    height : 36,
                    width : 72,
                    style : 'margin-left:5px',
                    border : false,
                    layout : {
                        type : 'vbox',
                        align : 'center'
                    },
                    items : [ Image, {
                        xtype : 'label',
                        width : 72,
                        style : 'text-align:center;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;',
                        html : desc + '&nbsp;'
                    }, delImg ],
                    listeners : {
                        mouseout : {
                            element : 'el',
                            fn : function (a, o) {
                                if (o.id.substring(0, 4) == 'phot') {
                                    me.showHideDelImg(o.id, "0");

                                }
                            }
                        },
                        mouseover : {
                            element : 'el',
                            fn : function (a, o) {
                                if (o.id.substring(0, 4) == 'phot') {
                                    me.showHideDelImg(o.id, "1");

                                }
                            }

                        }
                    }
                });
                if (ids == 'leadUion')// 牵头单位
                {
                    me.leUioncontainers.add(childContainer);
                } else if (ids == 'dutyUion') {// 责任单位
                    me.duUioncontainers.add(childContainer);

                }
                // containers.add(childContainer);
            }
        } else {// 选择人员
            var id = c.id;// 选中单位部门id
            var desc = !c.name||c.name=='null'?'':c.name;// 选中单位部门的名称
            var type = c.type;// person
            var filePath = c.photo;
            if (ids == 'leader')
                me.depreleader.push(id);
            else if (ids == 'approver')
                me.depreAppor.push(id);
            else if (ids == 'auditor')
                me.deperAuditor.push(id);
            else if (ids == 'responsible') {
                me.deperResponse.push(id);
                Ext.getCmp('OperatingArea').items.items[1].setHtml(c.dept);// 责任室/作业区
                Ext.getCmp('responsepost').items.items[1].setHtml(c.post);// 责任岗位

                var map = new HashMap();
                map.put('operaflag', 'responsible');// 选择责任人操作
                map.put('orgid', c.e0122);// 部门
                /*
                 * map.put('b0110',c.b0110); map.put('e01a1',c.e01a1);
                 * map.put('e0122',c.e0122);
                 */
                map.put('leadUion', me.depreleadUion);// 牵头单位
                Rpc({
                    functionId : 'WP00002001',
                    async : false,
                    success : function (res) {
                        var res = Ext.decode(res.responseText);
                        Ext.getCmp('responsegroup').items.items[1].setHtml(res.orgIdAndName.split(',')[1]);
                        me.response = {
                            e01a1 : c.e01a1,// 责任岗位
                            e0122 : c.e0122,// 责任室
                            b0110 : res.orgIdAndName.split(',')[0]
                        };// 责任组id
                    },
                    scope : me
                }, map);

                // 设置责任人后责任处OperatingArea 责任岗位内容
            }

            var delImg = Ext.create('Ext.Img', {
                src : "/workplan/image/remove.png",
                id : 'dele_' + ids + '_' + type + '_' + id,
                style : 'cursor:pointer;',
                width : 15,
                height : 15,
                hidden : true,
                cls : 'delImg',
                listeners : {
                    click : {
                        element : 'el',
                        fn : function (a, o, b, c) {
                            me.openDeletePersonMsg(o.id, ids);
                        }
                    },
                    mouseover : {
                        element : 'el',
                        fn : function (a, o) {
                            me.showHideDelImg(o.id, "1");
                        }
                    },
                    mouseout : {
                        element : 'el',
                        fn : function (a, o) {
                            me.showHideDelImg(o.id, "0");
                        }
                    }
                }
            });

            var Image = Ext.widget('image', {
                id : 'phot_' + ids + '_' + type + '_' + id,
                width : 50,
                title : desc,
                height : 50,
                style : 'border-radius: 50%;',// 人员显示圆形
                src : filePath
            // "/servlet/DisplayOleContent?filePath="+filePath+"&bencrypt=false&caseNullImg=/images/photo.jpg&imageResize=55`55"
            });

            var childContainer = Ext.widget('container', {
                id : 'cote_' + ids + '_' + type + '_' + id,
                height : 70,
                width : 72,
                style : 'margin-left:5px',
                border : false,
                layout : {
                    type : 'vbox',
                    align : 'center'
                },
                items : [ Image, {
                    xtype : 'label',
                    html : desc + '&nbsp;'
                }, delImg ],
                listeners : {
                    mouseout : {
                        element : 'el',
                        fn : function (a, o) {
                            if (o.id.substring(0, 4) == 'phot') {
                                me.showHideDelImg(o.id, "0");

                            }
                        }
                    },
                    mouseover : {
                        element : 'el',
                        fn : function (a, o) {
                            if (o.id.substring(0, 4) == 'phot') {
                                me.showHideDelImg(o.id, "1");

                            }
                        }

                    }
                }
            });
            if (ids == 'leader')// 公司领导
            {
                me.leadercontainers.add(childContainer);
                Ext.getCmp('leaderImg').hide();
            } else if (ids == 'approver') {// 审批人
                me.approcontainers.add(childContainer);
                Ext.getCmp('approverImg').hide();
            } else if (ids == 'auditor') {// 审核人
                me.auditorcontainer.add(childContainer);
                Ext.getCmp('auditorImg').hide();
            } else if (ids == 'responsible') {// 责任人
                me.responscontainer.add(childContainer);
                Ext.getCmp('responsibleImg').hide();
            }
            // containers.add(childContainer);
        }

        if (ids == 'leader')// 公司领导
        {
            Ext.getCmp(ids).insert(1, me.leadercontainers);
        } else if (ids == 'approver') {// 审批人
            Ext.getCmp(ids).insert(1, me.approcontainers);
        } else if (ids == 'leadUion')// 牵头单位
        {
            Ext.getCmp(ids).insert(1, me.leUioncontainers);
        } else if (ids == 'dutyUion') {// 责任单位
            Ext.getCmp(ids).insert(1, me.duUioncontainers);
        } else if (ids == 'auditor') {// 审核人
            Ext.getCmp(ids).insert(1, me.auditorcontainer);
        } else if (ids == 'responsible') {// 责任人
            Ext.getCmp(ids).insert(1, me.responscontainer);
        }

    },
    showHideDelImg : function (id, flag) {
        var me = this;
        id = id.substring(4);
        var delImg = Ext.getCmp('dele' + id);
        if (flag == '1') {
            if ((me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag))
                    && (id.split('_')[1] == 'leader' || id.split('_')[1] == 'approver' || id.split('_')[1] == 'dutyUion' || id.split('_')[1] == 'leadUion')) {

            } else {
                delImg.show();
            }
        } else {
            delImg.hide();
        }
    },
    openDeletePersonMsg : function (id, ids) {//
        var me = this;
        var args = id.split('_');
        id = id.substring(4);
        var childcontainer = Ext.getCmp('cote' + id);
        // var parechild=Ext.getCmp('parecontainer');
        if (ids == 'leader')// 公司领导
        {
            for (var i = 0; i < me.depreleader.length; i++) {
                if (me.depreleader[i] == args[3])
                    me.depreleader[i] = '';// 数组元素边空 ，使用delete 数组对应下标存undefined
                                            // 使用控件报错
            }
            Ext.getCmp('leaderImg').show();
        } else if (ids == 'approver') {
            for (var i = 0; i < me.depreAppor.length; i++) {
                if (me.depreAppor[i] == args[3])
                    me.depreAppor[i] = '';
            }
            Ext.getCmp('approverImg').show();
        } else if (ids == 'leadUion')// 牵头单位
        {
            for (var i = 0; i < me.depreleadUion.length; i++) {
                if (me.depreleadUion[i] == args[3])
                    me.depreleadUion[i] = '';
                // me.depreleadUion[i]='';//数组元素边空 ，使用delete 数组对应下标存undefined
                // 使用控件报错

            }
        } else if (ids == 'dutyUion') {// 责任单位
            for (var i = 0; i < me.depredutyUion.length; i++) {
                if (me.depredutyUion[i] == args[3])
                    me.depredutyUion[i] = '';// 数组元素边空 ，使用delete
                                                // 数组对应下标存undefined 使用控件报错
            }
        } else if (ids == 'auditor') {// 审核人
            for (var i = 0; i < me.deperAuditor.length; i++) {
                if (me.deperAuditor[i] == args[3])
                    me.deperAuditor[i] = '';
            }
            Ext.getCmp('auditorImg').show();
        } else if (ids == 'responsible') {
            for (var i = 0; i < me.deperResponse.length; i++) {
                if (me.deperResponse[i] == args[3])
                    me.deperResponse[i] = '';
            }
            Ext.getCmp('OperatingArea').items.items[1].setHtml('');// 责任室/作业区
            Ext.getCmp('responsepost').items.items[1].setHtml('');// 责任岗位
            Ext.getCmp('responsegroup').items.items[1].setHtml('');// 责任室
            me.response = {};
            Ext.getCmp('responsibleImg').show();
        }

        var containers = Ext.getCmp(ids + 'container');
        containers.remove(childcontainer, true);
    },
    saveYearPlan : function (type) {
        var me = this;
        var map = new HashMap();
        var values = Ext.getCmp("formV").getValues();
        if (me.typeflag != 'taskAssign') {// 保存任务
            if (me.detilText('leadUioncontainer').length <= 0) {
                Ext.Msg.alert("提示信息", "牵头单位不能为空！");
                return;
            }
            if (me.detilperson('approver').length == 0) {
                Ext.Msg.alert("提示信息", "审批人不能为空！");
                return;
            }
            for (var i = 0; i < me.columns.length; i++) {
                var fitem = me.columns[i];
                if ((fitem.useflag == "1" && fitem.visible && fitem.fillable) || fitem.itemid == "p1703" || fitem.itemid == "p1705" || fitem.itemid == "p1707") {
                    var e = Ext.getCmp(fitem.itemid);
                    if (e && (!e.value) || e.value == "") {
                        Ext.Msg.alert("提示信息", e.fieldLabel.replace("*", "") + " 不能为空！");
                        return;
                    }
                }
                if (fitem.useflag == "1" && fitem.visible && fitem.itemtype == "A" && fitem.codesetid != '0') {
                    var e = Ext.getCmp(fitem.itemid);
                    if (e)
                        values[fitem.itemid] = e.getValue();
                }
            }
            // 处理选人和选择单位
            values.p1717 = me.detilText('dutyUioncontainer');// 责任单位dutyUion
            values.p1719 = me.detilText('leadUioncontainer');// 牵头单位leadUion
            values.p1721 = me.detilperson('leader');// 公司领导
            values.p1720 = me.detilperson('approver');// 审批人
            values.p1743 = type;
            values.p1701 = me.year;
            values.dutyUion = me.depredutyUion;// 责任单位
            values.leadUion = me.depreleadUion;// 牵头单位
            values.leader = me.depreleader;// 公司领导
            values.approver = me.depreAppor;// 审批人
        } else {// 任务指派
            map.put("typeflag", "taskAssign");
            if (me.detilText('responsiblecontainer').length <= 0) {
                Ext.Msg.alert("提示信息", "责任人不能为空！");
                return;
            }
            // 处理选人和选择单位
            values.p1731 = me.detilperson('responsible');// 责任人
            values.p1729 = Ext.getCmp('responsepost').items.items[1].html ? Ext.getCmp('responsepost').items.items[1].html : '';// 责任岗位
            values.p1727 = Ext.getCmp('responsegroup').items.items[1].html ? Ext.getCmp('responsegroup').items.items[1].html : '';// 责任组
            values.p1725 = Ext.getCmp('OperatingArea').items.items[1].html ? Ext.getCmp('OperatingArea').items.items[1].html : '';// 责任处
            values.p1723 = me.detilperson('auditor');// 审核人
            values.deperResponse = me.deperResponse;// 责任人
            values.deperAuditor = me.deperAuditor;// 审核人

            var mapid = new HashMap();
            if (me.response) {// 处理选择责任人为空时的处理
                mapid.put("responsepostId", me.response.e01a1 ? me.response.e01a1 : '');
                mapid.put("operatingAreaId", me.response.e0122 ? me.response.e0122 : '');
                mapid.put("responsegroupId", me.response.b0110 ? me.response.b0110 : '');
                values.responseobj = mapid;// 责任岗位/处/组 对应id
            }
        }
        map.put("bean", values)
        Rpc({
            functionId : 'WP00002002',
            async : false,
            success : function (res) {
                var res = Ext.decode(res.responseText);
                if (res.flag && me.typeflag != 'taskAssign') {
                    if (me.typeflag == 'edit') {
                        Ext.Msg.alert('提示信息', "任务修改成功!");
                        me.backYearPlan();
                    } else {
                        Ext.showConfirm("信息添加成功！</br>是否继续新建任务?", function (btn) {
                            if (btn == 'yes') {// 继续创建新任务//清除相关内容
                                Ext.getCmp('p1703').setValue('');
                                // 光标默认定位到项目
                                Ext.getCmp('p1703').focus();
                                if (Ext.isIE) {// 兼容ie
                                    var tempRange = Ext.getDom("p1703-inputEl").createTextRange();// 创建文本区
                                    tempRange.moveStart('character', 0);// 设置移动起点
                                    tempRange.collapse(true);
                                    tempRange.select();
                                }
                                Ext.getCmp('p1705').setValue('');
                                Ext.getCmp('p1707').setValue('');
                            } else {// 返回
                                me.backYearPlan();
                            }
                        });
                    }
                    return;
                }
                if (res.flag && me.typeflag == 'taskAssign') {
                    Ext.Msg.alert("提示信息", "任务指派成功！");
                    me.backYearPlan();
                }
                if (res.flag && me.typeflag != 'taskAssign') {
                    Ext.Msg.alert('提示信息', "任务添加成功!");
                } else if (!res.flag) {
                    if (me.typeflag == 'edit') {
                        Ext.Msg.alert("提示信息", "任务修改失败！");
                    }
                    if (me.typeflag == "taskAssign") {
                        Ext.Msg.alert("提示信息", "任务指派失败！");
                    } else {
                        Ext.Msg.alert('提示信息', "信息添加失败!");
                    }
                }
            },
            scope : me
        }, map);
    },
    detilDate : function (value) {
        if (value != null) {
            var date = new Date(value);
            return Ext.Date.format(date, 'Y-m-d');
        } else
            return '';
    },
    detilText : function (containerid) {// 处理责任单位和牵头单位显示名称
        if (!Ext.getCmp(containerid))
            return '';
        var conContext = Ext.getCmp(containerid).items.items;
        var text = '';
        for (var i = 0; i < conContext.length; i++) {
            if (i < conContext.length - 1)
                text += conContext[i].items.items[1].config.html.split('&nbsp;')[0] + ',';
            else
                text += conContext[i].items.items[1].config.html.split('&nbsp;')[0];
        }
        return text;
    },
    detilperson : function (containerid) {// 处理人员，显示名称
        if (!Ext.getCmp(containerid + 'container'))
            return '';
        var text = "";
        if (Ext.getCmp(containerid + 'container').items.items.length > 0)
            text = Ext.getCmp(containerid + 'container').items.items[0].items.items[1].config.html.split('&nbsp;')[0];
        return text;
    },
    backYearPlan : function () {// 返回计划制定界面
        var me = this;
        me.remove();
        var bodyPanel = YearplanGlobal.tableObj.bodyPanel;
        YearplanGlobal.tableObj.getMainPanel().add(bodyPanel);
        YearplanGlobal.tableObj.tablePanel.getStore().load();
    },
    remove : function () {
        Ext.getCmp('mainPanel').destroy();
    },
    createFillItem : function () {// 创建需要填写的项
        var me = this;
        var items = [];
        var temp = []; //因责任组需要放到布局的最下方，直接从业务字典中得到的指标无法控制顺序，所以创建临时数组解决
        items.push({
            xtype : 'textfield',
            name : 'p1700',
            id : 'p1700',
            fieldLabel : '<font color="red">*</font> 计划编号',
            labelAlign : 'left',
            labelWidth : 100,
            hidden : true,
            width : 400
        });
        var map = new HashMap();
        map.put("typeflag", "getfielditems");
        Rpc({
            functionId : 'WP00002002',
            async : false,
            success : function (response) {
                var columns = Ext.decode(response.responseText).fielditems;
                me.columns = columns;
                for (var i = 0; i < columns.length; i++) {
                    var fitem = columns[i];
                    if (fitem.itemid == "p1703") {
                        items.push({
                            xtype : 'textfield',
                            name : 'p1703',
                            id : 'p1703',
                            fieldLabel : '<font color="red">*</font> 项目',
                            labelAlign : 'left',
                            labelWidth : 100,
                            width : 400,
                            readOnly : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                            fieldStyle : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? 'background-color:#EEEEEE' : '',
                            emptyText : '请输入项目名称',
                            listeners : {
                                'focus' : function () {// 光标获得焦点
                                    if (Ext.isIE) {
                                        var tempRange = Ext.getDom("p1703-inputEl").createTextRange();// 创建文本区
                                        tempRange.moveStart('character', 0);// 设置移动起点
                                        tempRange.collapse(true);
                                        tempRange.select();
                                    }
                                }
                            }
                        });
                    } else if (fitem.itemid == "p1705") {
                        items.push({
                            xtype : 'textfield',
                            name : 'p1705',
                            id : 'p1705',
                            margin : '10 0 10 0 ',
                            fieldLabel : '<font color="red">*</font> 任务',
                            labelAlign : 'left',
                            labelWidth : 100,
                            width : 400,
                            enforceMaxLength : true,
                            readOnly : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                            fieldStyle : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? 'background-color:#EEEEEE' : '',
                            maxLength : 200,
                            emptyText : '请输入任务名称',
                            listeners : {
                                'focus' : function () {// 光标获得焦点
                                    if (Ext.isIE) {
                                        var tempRange = Ext.getDom("p1705-inputEl").createTextRange();// 创建文本区
                                        tempRange.moveStart('character', 0);// 设置移动起点
                                        tempRange.collapse(true);
                                        tempRange.select();
                                    }
                                }
                            }
                        });
                    } else if (fitem.itemid == "p1707") {
                        items.push({
                            xtype : 'textareafield',
                            grow : true,
                            name : 'p1707',
                            id : 'p1707',
                            sytle : 'margin-Top:20px',
                            fieldLabel : '<font color="red">*</font>  工作内容',
                            readOnly : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                            fieldStyle : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? 'background-color:#EEEEEE' : '',
                            labelAlign : 'left',
                            labelWidth : 100,
                            width : 500,
                            enforceMaxLength : true,
                            maxLength : 250,
                            emptyText : '请输入工作内容'
                        });

                    } else if (fitem.itemid == "p1745" && fitem.useflag == "1" && fitem.visible) {
                        items.push({
                            xtype : 'datefield',
                            name : 'p1745',
                            id : 'p1745',
                            margin : '10 0 10 0',
                            fieldLabel : '&nbsp;&nbsp;开始日期',
                            labelAlign : 'left',
                            labelWidth : 100,
                            format : 'Y-m-d',
                            readOnly : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                            fieldStyle : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? 'background-color:#EEEEEE' : '',
                            width : 300,
                            allowBlank : true,
                            listeners : {
                                select : function (datefield, date) {
                                    var endDate = Ext.getCmp('p1747').value;
                                    var selectDate = date.getTime();
                                    if (endDate && selectDate > endDate.getTime()) {
                                        Ext.Msg.alert('提示信息', '开始日期不能大于截止日期');
                                        Ext.getCmp('p1745').setValue('');
                                    }
                                }
                            }
                        });
                    } else if (fitem.itemid == "p1747" && fitem.useflag == "1" && fitem.visible) {
                        items.push({
                            xtype : 'datefield',
                            name : 'p1747',
                            id : 'p1747',
                            margin : '10 0 10 0',
                            format : "Y-m-d",
                            fieldLabel : '&nbsp;&nbsp;截止日期',
                            readOnly : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                            fieldStyle : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? 'background-color:#EEEEEE' : '',
                            labelAlign : 'left',
                            labelWidth : 100,
                            width : 300,
                            allowBlank : true,
                            listeners : {
                                select : function (datefield, date) {
                                    var startTime = Ext.getCmp('p1745').value;
                                    var selectTime = date.getTime();// 选择日期
                                    if (startTime && startTime.getTime() > selectTime) {// 开始日期大于结束日期
                                        Ext.Msg.alert('提示信息', '截止日期不能小于开始日期');
                                        Ext.getCmp('p1747').setValue('');
                                    }
                                }
                            }
                        });
                    } else if (fitem.itemid == "p1717" && fitem.useflag == "1" && fitem.visible) {// 责任单位
                        items.push({
                            xtype : 'container',
                            width : '100%',
                            margin : '20 0 10 0',
                            id : 'dutyUion',// 责任单位
                            layout : {
                                type : 'hbox',
                                align : 'center'
                            },
                            items : [ {
                                xtype : 'label',
                                html : '&nbsp;&nbsp;责任单位',
                                width : 100
                            }, {
                                xtype : 'container',
                                width : '100%',
                                style : 'margin-left:0px',
                                items : [ {
                                    xtype : 'image',
                                    src : "/workplan/image/jiahao.png",
                                    style : 'cursor:pointer;margin-left:5px;margin-top:2px;',
                                    hidden : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                                    listeners : {
                                        click : {
                                            element : 'el',
                                            fn : function () {
                                                me.selectOrg('dutyUion');
                                            }
                                        }
                                    }
                                } ]
                            }

                            ]

                        });
                    } else if (fitem.itemid == "p1719") {// 牵头单位
                        items.push({
                            xtype : 'container',
                            width : '100%',
                            margin : '10 0 10 0',
                            id : 'leadUion',// 牵头单位
                            layout : {
                                type : 'hbox',
                                align : 'center'
                            },
                            items : [ {
                                xtype : 'label',
                                html : '<font color="red">*</font>&nbsp;牵头单位',
                                width : 100
                            }, {
                                xtype : 'container',
                                width : '100%',
                                style : 'margin-left:0px',
                                items : [ {
                                    xtype : 'image',
                                    style : 'cursor:pointer;margin-left:5px;margin-top:2px;',
                                    src : "/workplan/image/jiahao.png",
                                    hidden : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                                    listeners : {
                                        click : {
                                            element : 'el',
                                            fn : function () {
                                                me.selectOrg('leadUion');
                                            }
                                        }
                                    }
                                } ]
                            }

                            ]

                        });
                    } else if (fitem.itemid == "p1721" && fitem.useflag == "1" && fitem.visible) {// 公司领导
                        items.push({
                            xtype : 'container',
                            width : '100%',
                            margin : '10 0 10 0',
                            id : 'leader',// 公司领导
                            layout : {
                                type : 'hbox',
                                align : 'center'
                            },
                            items : [ {
                                xtype : 'label',
                                html : '&nbsp;&nbsp;公司领导',
                                width : 100
                            }, {
                                xtype : 'container',
                                width : '100%',
                                style : 'margin-left:0px',
                                items : [ {
                                    xtype : 'image',
                                    id : 'leaderImg',
                                    style : 'cursor:pointer;margin-left:5px;margin-top:2px;',
                                    src : "/workplan/image/jiahao.png",
                                    hidden : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                                    listeners : {
                                        click : {
                                            element : 'el',
                                            fn : function () {
                                                me.selectPerson('leader');
                                            }
                                        }
                                    }
                                } ]
                            }

                            ]

                        });
                    } else if (fitem.itemid == "p1720") {// 审批人
                        items.push({
                            xtype : 'container',
                            width : '100%',
                            margin : '10 0 10 0',
                            id : 'approver',// 审批人
                            layout : {
                                type : 'hbox',
                                align : 'center'
                            },
                            items : [ {
                                xtype : 'label',
                                html : '<font color="red">*</font>&nbsp;审批人',
                                width : 100
                            }, {
                                xtype : 'container',
                                width : '100%',
                                style : 'margin-left:0px',
                                items : [ {
                                    xtype : 'image',
                                    id : 'approverImg',
                                    style : 'cursor:pointer;margin-left:5px;margin-top:2px;',
                                    src : "/workplan/image/jiahao.png",
                                    hidden : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                                    listeners : {
                                        click : {
                                            element : 'el',
                                            fn : function () {
                                                me.selectPerson('approver');
                                            }
                                        }
                                    }
                                } ]
                            } ]
                        });
                    } else if (fitem.itemid == "p1723" && fitem.useflag == "1" && fitem.visible) {// 审核人
                        items.push({
                            xtype : 'container',
                            width : '100%',
                            margin : '10 0 10 0',
                            hidden : me.typeflag == 'taskAssign' ? false : true,
                            id : 'auditor',// 审核人
                            layout : {
                                type : 'hbox',
                                align : 'center'
                            },
                            items : [ {
                                xtype : 'label',
                                html : '&nbsp;&nbsp;审核人',
                                width : 100
                            }, {
                                xtype : 'container',
                                style : 'margin-left:0px',
                                width : '100%',
                                items : [ {
                                    xtype : 'image',
                                    id : 'auditorImg',
                                    style : 'cursor:pointer;margin-left:5px;margin-top:2px;',
                                    src : "/workplan/image/jiahao.png",

                                    listeners : {
                                        click : {
                                            element : 'el',
                                            fn : function () {
                                                me.selectPerson('auditor');
                                            }
                                        }
                                    }
                                } ]
                            } ]
                        });
                    } else if (fitem.itemid == "p1731") {// 责任人
                        items.push({
                            xtype : 'container',
                            width : '100%',
                            margin : '10 0 10 0',
                            hidden : me.typeflag == 'taskAssign' ? false : true,
                            id : 'responsible',// 责任人
                            layout : {
                                type : 'hbox',
                                align : 'center'
                            },
                            items : [ {
                                xtype : 'label',
                                html : '<font color="red">*</font>&nbsp;责任人',
                                width : 100
                            }, {
                                xtype : 'container',
                                width : '100%',
                                style : 'margin-left:0px',
                                items : [ {
                                    xtype : 'image',
                                    id : 'responsibleImg',
                                    style : 'cursor:pointer;margin-left:5px;margin-top:2px;',
                                    src : "/workplan/image/jiahao.png",

                                    listeners : {
                                        click : {
                                            element : 'el',
                                            fn : function () {
                                                me.selectPerson('responsible');
                                            }
                                        }
                                    }
                                } ]
                            } ]
                        });
                    } else if (fitem.itemid == "p1729" && fitem.useflag == "1" && fitem.visible) {// 责任岗位
                        temp.push("p1729");
                        continue;
                    } else if (fitem.itemid == "p1725" && fitem.useflag == "1" && fitem.visible) {
                        temp.push("p1725");
                        continue;
                    } else if (fitem.itemid == "p1727" && fitem.useflag == "1" && fitem.visible) {// 责任组
                        temp.push("p1727");
                        continue;
                    } else {
                        if ((fitem.useflag == "1" && !fitem.visible) || fitem.itemid == "p1709" || fitem.itemid == "p1711" || fitem.itemid == "p1713" || fitem.itemid == "p1715"
                                || fitem.itemid == "p1743"// 任务状态
                                || fitem.itemid == "p1701"// 年度
                        )// 排除四个季度和隐藏的字段
                            continue;
                        else {
                            var obj = {
                                xtype : "textfield",
                                name : fitem.itemid,
                                id : fitem.itemid,
                                margin : '10 0 10 0',
                                fieldLabel : '&nbsp;&nbsp;' + fitem.itemdesc,
                                readOnly : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? true : false,
                                fieldStyle : (me.typeflag == 'taskAssign' || (me.typeflag == 'edit' && me.editflag)) ? 'background-color:#EEEEEE' : '',
                                labelAlign : 'left',
                                labelWidth : 100,
                                width : 300,
                                allowBlank : true
                            };
                            if (fitem.fillable) {
                                obj.fieldLabel = '<font color="red">*</font> ' + fitem.itemdesc;
                            }
                            if (fitem.itemtype == "D") {
                                obj.xtype = 'datefield';
                                obj.format = "Y-m-d";

                            } else if (fitem.itemtype == "M") {
                                obj.xtype = 'textareafield';
                                obj.grow = true;
                                obj.sytle = 'margin-Top:20px';
                                obj.width = 500;
                                obj.maxLength = 250;
                                obj.enforceMaxLength = true;
                            } else if (fitem.itemtype == "A" && fitem.codesetid != '0') {
                                // 设置选择代码编辑器
                                Ext.require([ 'EHR.extWidget.field.CodeTreeCombox' ]);
                                obj.border = false;
                                obj.xtype = 'codecomboxfield';
                                obj.codesetid = fitem.codesetid;
                            } else if (fitem.itemtype == "N") {
                                obj.validator = function (val) {
                                    var reg = /^\+?[1-9][0-9]*$/;
                                    var errMsg = "请输入正整数";
                                    return (reg.test(val) || val == "") ? true : errMsg;
                                }
                            }
                            items.push(obj);
                        }
                    }
                }
            }
        }, map);
        for(var i=0;i<temp.length;i++){
            if(temp[i]=="p1729"){
                items.push({
                    xtype : 'container',
                    width : '100%',
                    margin : '10 0 10 0',
                    hidden : me.typeflag == 'taskAssign' ? false : true,
                    id : 'responsepost',
                    layout : {
                        type : 'hbox',
                        align : 'center'
                    },
                    items : [ {
                        xtype : 'label',
                        html : '&nbsp;&nbsp;责任岗位',
                        width : 100
                    }, {
                        xtype : 'container',
                        style : 'margin-left:5px',
                        width : '100%'
                    } ]
                });
            }else if(temp[i]=="p1725"){
                items.push({
                    xtype : 'container',
                    width : '100%',
                    margin : '10 0 10 0',
                    hidden : me.typeflag == 'taskAssign' ? false : true,
                    id : 'OperatingArea',// 责任处或作业区
                    layout : {
                        type : 'hbox',
                        align : 'center'
                    },
                    items : [ {
                        xtype : 'label',
                        html : '&nbsp;&nbsp;责任室/作业区',
                        width : 100
                    }, {
                        xtype : 'container',
                        style : 'margin-left:5px',
                        width : '100%'
                    } ]
                });
            }else if(temp[i]=="p1727"){
                items.push({
                    xtype : 'container',
                    width : '100%',
                    margin : '10 0 10 0',
                    hidden : me.typeflag == 'taskAssign' ? false : true,
                    id : 'responsegroup',
                    layout : {
                        type : 'hbox',
                        align : 'center'
                    },
                    items : [ {
                        xtype : 'label',
                        html : '&nbsp;&nbsp;责任组',
                        width : 100
                    }, {
                        xtype : 'container',
                        style : 'margin-left:5px',
                        width : '100%'
                    } ]
                });
            }
        }
        return items;
    }
})