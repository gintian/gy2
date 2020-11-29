/**
 * 考核结果表单组件
 * haosl
 * 19/6/28
 */
Ext.define('AssessFieldSet', {
    cloudSets: [],//同步的信息集列表
    currentSetMap:new HashMap(),
    constructor: function (config) {
        var me = this;
        me.cloudSets = config.cloudSets;
        //创建自定义样式
        me.createStyleCss();
        me.mainContainer = Ext.widget("container",{layout: 'vbox',flex:1});
        me.timer;//定义全局的定时器，用于删除已选信息集
    },
    getMainContainer: function () {
        var me = this;
        me.mainContainer.add(me.createSetTitle());
        me.mainContainer.add(Ext.widget("panel", {
            id: 'fieldMappings',
            width: 670,
            margin: '0 0 0 100',
            flex:1,
            border: false,
            layout: 'vbox'
        }));
        for(var i=0;i<me.cloudSets.length;i++){
            var iset = me.cloudSets[i];
            if (iset.selected != "true") {
                continue;
            }
            me.refreshFieldMappings(iset.id,"",i ==0?false:true);
        }

        return me.mainContainer;
    },
    /**
     * 创建关联子集指标
     */
    refreshFieldMappings: function (newSetId,oldSetId,isHidden) {
        var me = this;
        var fieldMappings = Ext.getCmp("fieldMappings");
        if (!fieldMappings)
            return;
        //关联子集数据源
        var currentFormPanel = Ext.getCmp("formpanel"+newSetId);
        var oldFormPanel = Ext.getCmp("formpanel"+oldSetId);
        if (oldFormPanel){
            oldFormPanel.setVisible(false);
        }
        if (currentFormPanel) {
            currentFormPanel.setVisible(true);
            return;
        }
        var currentSet = me.getCurrentSet(newSetId);
        //创建关联子集的下拉框
        var store = Ext.create('Ext.data.Store', {
            fields: ['set_id', 'set_name'],
            data: currentSet.hr_set
        });
        var items = [];
        var hrSet = '';
        for (var i = 0; i < currentSet.hr_set.length; i++) {
            if (currentSet.hr_set[i].selected == "true") {
                hrSet = currentSet.hr_set[i];
                break;
            }
        }
        items.push(
            Ext.widget('combo', {//考核结果子集
                    fieldLabel: currentSet.name,
                    isChildSet:currentSet.isChildSet,
                    margin: '10 0 0 0',
                    style: 'text-align:left;',
                    store: store,
                    queryMode: 'local',
                    width: 255,
                    id:currentSet.id,
                    displayField: 'set_name',
                    valueField: 'set_id',
                    value: hrSet.set_id,
                    margin: '10 0 30 0',
                    editable: false,
                    listeners: {
                        change: function (combo, newValue) {
                            me.onSetChange(currentSet.id,newValue);
                        }
                    }
                }
            ));
        var coloudFields = currentSet.cloud_fields;
        for (var i = 0; i < coloudFields.length; i++) {
            var field = coloudFields[i];
            var connectField = field.connectField;

            var hritemid = '';
            var hritemdesc = '';
            if (connectField && connectField.itemid && connectField.itemid != '') {
                hritemid = connectField.itemid;
                hritemdesc = connectField.itemdesc;
            }
            items.push(Ext.widget('FieldSelectorPiker', {
                source: hrSet.set_id,
                width: 255,
                style: 'text-align:left;margin-bottom:5px;',
                itemid: field.id,
                type: field.type,
                codesetid:field.codesetid,
                required:field.required,
                hritemid: hritemid,
                value: hritemdesc,
                fieldLabel: field.name
            }));
        }
        fieldMappings.add(Ext.widget("form",{
            id:'formpanel'+newSetId,
            border:false,
            scrollable: true,
            width:'100%',
            flex:1,
            hidden:isHidden,
            items:items
        }));
    },
    removeSelected: function () {
        var oldSetId = "";
        var arr = Ext.query(".selected_cls_haosl");
        if (arr.length>0){
            var selected = arr[0];
            var cmp = Ext.getCmp(selected.id);
            oldSetId = selected.id.substring(6);
            cmp.removeCls("selected_cls_haosl");
        }
        return oldSetId;

    },
    /**
     * 自定义样式
     */
    createStyleCss: function () {
        if (!Ext.util.CSS.getRule(".btn_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".btn_cls_haosl{" +
                "font-family: 微软雅黑,宋体,tahoma,arial,verdana,sans-serif;font-weight:600;" +
                "font-size:13px;height:22px;cursor:pointer;}");
        }
        if (!Ext.util.CSS.getRule(".selected_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".selected_cls_haosl{color:#3A9CFF;border-bottom:3px solid #3A9CFF;}");
        }
        if (!Ext.util.CSS.getRule(".over_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".over_cls_haosl{color:#3A9CFF;font-weight:600;}");
        }
    },
    /**
     * 创建已选信息集Tab栏
     */
    createSetTitle: function () {
    	//xus 19/11/25 云同步到hcm指标子集选项支持横向滚动
        var container = Ext.widget("panel", {
        	margin: '10 0 0 0',
        	height:30,
        	//xus 19/12/30 【56952】v77发版：云集成，第二步指标对应，云往HCM同步指标，已选择的信息集超出当前界面显示时，右边箭头不显示
        	layout:'hbox',
        	bodyStyle: 'border-left:0;border-top:0;border-right:0;border-bottom:1px solid #c5c5c5;',
            items:[{
            	xtype: 'image',
            	id:'imgLeft',
            	margin: '8 0 0 0',
            	height:16,
            	width:16,
            	style:'float:left',
                src: '/images/new_module/upDiskLeft.png',
                listeners:{
                	afterrender : function( imgLeft, eOpts ){
                		imgLeft.el.on('click',function(){
                			if(Ext.getCmp('setListTitle').scrollable.position.x < 100){
                				Ext.getCmp('setListTitle').setScrollX( 0 );
                			}else{
                				Ext.getCmp('setListTitle').setScrollX( Ext.getCmp('setListTitle').getScrollX( ) - 100 );
                			}
                		});
                	}
                	
                }
            },{
            	id: 'setListTitle',
            	xtype:'panel',
                width: 738,
                layout: 'hbox',
                style:'float:left',
                border:0,
                scrollable: 'x',
                items: []
            },{
            	xtype: 'image',
            	id:'imgRight',
            	margin: '8 0 0 0',
            	height:16,
            	width:16,
            	style:'float:left',
                src: '/images/new_module/upDiskRight.png',
                listeners : {
                	afterrender : function( imgRight, eOpts ){
                		imgRight.el.on('click',function(){
                			Ext.getCmp('setListTitle').setScrollX( Ext.getCmp('setListTitle').getScrollX( ) + 100 );
                		});
                	}
                }
            }]
        });
        var me = this;
        var cloudSets = me.cloudSets;
        for (var i = 0; i < cloudSets.length; i++) {
            var iset = cloudSets[i];
            if (iset.selected != "true") {
                continue;
            }
            me.addSetTitle(iset, i == 0, i == 0,i>=2,"");
        }
        return container;
    },
    removeSetTitle:function(id){
        var me = this;
        var setListTitle = Ext.getCmp("setListTitle");
        var oldSetId = "";
        if (setListTitle){
            setListTitle.remove(Ext.getCmp(id),true);
            var setStore = Ext.getCmp("infosetSeletor").getStore();
            oldSetId = id.substring(9);
            var record = setStore.getById(oldSetId);
            record.set("selected","false");
            setStore.load();
        }
        var arr = Ext.query(".selected_cls_haosl");
        if (arr.length==0){
            var component = setListTitle.getComponent(0);
            if(component){
                var newSetId = component.id.substring(9);
                var titleSet = Ext.getCmp("title_"+newSetId);
                if (titleSet && !titleSet.hasCls("selected_cls_haosl")) {
                    titleSet.addCls("selected_cls_haosl");
                    me.refreshFieldMappings(newSetId,oldSetId);
                }

            }
        }
    },
    /**
     * 添加选中的同步信息集
     * @param iset
     * @param defaultSelect 是否默认需要选中
     * @param 是否允许删除
     */
    addSetTitle: function (iset, defaultSelect, isFirst,delFlag,oldSetId) {
        var me = this;
        var setListTitle = Ext.getCmp("setListTitle");
        if (setListTitle) {
            setListTitle.add({
                xtype: 'container',
                id: 'container' + iset.id,
                style: 'position:relative;',
                padding: isFirst ? '5 10 0 5' : '5 10 0 0',
                height: 28,
                type:iset.type,
                listeners: {
                    mouseover: {
                        element: 'el',
                        fn: function (e, div) {
                        	//xus 19/12/24 【56542】v77发版：云集成，指标对应，云同步到HCM指标，信息集选择超过当前可显示个数后，鼠标放置在信息集名称上，不显示删除按钮，且跳回显示位置
                        	var scrollX = Ext.getCmp('setListTitle').getScrollX( );
                            var img = Ext.getCmp(div.id + "_del");
                            //鼠标悬浮两秒后显示删除功能
                            me.timer = setTimeout(function () {
                                if (img) {
                                    img.setVisible(true);
                                    Ext.getCmp('setListTitle').setScrollX(scrollX);
                                }
                            }, 1000);
                        }
                    },
                    mouseout: {
                        element: 'el',
                        fn: function (e, div) {
                        	var scrollX = Ext.getCmp('setListTitle').getScrollX( );
                            var img = Ext.getCmp(div.id + "_del");
                            if (img) {
                                img.setVisible(false);
                                Ext.getCmp('setListTitle').setScrollX(scrollX);
                            }
                            //取消定时器
                            if (me.timer) {
                                clearTimeout(me.timer);
                            }
                        }
                    }
                },
                items: [
                    {
                        xtype: 'tbtext',
                        id: 'title_' + iset.id,
                        padding: '1 5 0 5',
                        baseCls: 'btn_cls_haosl',
                        cls: defaultSelect ? 'selected_cls_haosl' : "",
                        overCls: 'over_cls_haosl',
                        html:iset.name,
                        listeners: {
                            click: {
                                element: 'el',
                                fn: function () {
                                    var tbtext = Ext.getCmp('title_'+iset.id);
                                    if (!tbtext.hasCls("selected_cls_haosl")) {
                                       var oldSetId = me.removeSelected();
                                        tbtext.addCls("selected_cls_haosl");
                                        me.refreshFieldMappings(iset.id,oldSetId);
                                    }
                                }
                            }
                        }
                    },delFlag?{
                        xtype: 'image',//删除图标
                        src: './images/remove.png',
                        hidden: true,
                        id: 'title_' + iset.id + "_del",
                        style: 'cursor:pointer;position:absolute;right:7px;bottom:11px;',
                        title: syncSetting.delete_infoset,
                        width: 16,
                        height: 16,
                        listeners: {
                            click: {
                                element: 'el',
                                fn: function () {
                                    var titleId = "container"+iset.id;
                                    me.removeSetTitle(titleId);
                                }
                            },
                            mouseover: {
                                element: 'el',
                                fn: function (e, i) {
                                    var img = Ext.getCmp(i.id);
                                    if (img) {
                                        img.setVisible(true);
                                    }
                                }
                            },
                            mouseout: {
                                element: 'el',
                                fn: function (e, i) {
                                    var x = e.pageX;
                                    var y = e.pageY;
                                    var img = Ext.getCmp(i.id);
                                    var tbtext = "title_"+iset.id;
                                    var cXy = Ext.getCmp(tbtext).getPosition();
                                    if (img) {
                                        if (x <= cXy[0] || x >= (cXy[0] + 88) || cXy[1] >= y || y >= cXy[1] + 22) {
                                            img.setVisible(false);
                                        }
                                    }
                                }
                            }
                        }
                    }:undefined
                ]

            });
        }
        //重新生成新的指标对应关系
        me.refreshFieldMappings(iset.id,oldSetId);
    },
    /**
     * 获取指定信息集指标对应信息
     */
    getCurrentSet: function (setId) {
        var me = this;
        var appId = Ext.String.trim(Ext.getCmp('user_appId').getValue());

        var tenantId = Ext.String.trim(Ext.getCmp('user_tenantID').getValue());
        var appSecret = Ext.String.trim(Ext.getCmp('user_AppSecret').getValue());
        var vo = new HashMap();
         var currentSet = {};
         vo.put("transType", "loadCurrentSet");
         vo.put("appId", appId);
         vo.put("tenantId", tenantId);
         vo.put("appSecret", appSecret);
         vo.put("id",setId);
         Rpc({
             functionId: 'SYS00005001', async: false, success: function (res) {
                 var resultObj = Ext.decode(res.responseText).returnStr;
                 currentSet = resultObj.return_data.cloudTOhr.currentSet;

         }}, vo);
        me.currentSetMap.put(currentSet.id,currentSet);
        return currentSet;
    },
    onSetChange: function (setId,source) {
        var formpanel = Ext.getCmp("formpanel"+setId);;
        var pikers = formpanel.query('FieldSelectorPiker');
        for (var i = 0; i < pikers.length; i++) {
            //清除指标值
            pikers[i].setItemValue('');
            //改变指标source属性
            pikers[i].changeSource(source);
        }
    }
});