/**
 * 薪资发放-导入数据-对比页面
 */
Ext.define('SalaryUL.inout.SetImpRelation',{
    constructor:function(config){
        import_me = this;
        import_me.viewport = '';
        import_me.salaryid = config.salaryid;
        import_me.appdate=config.appdate;
        import_me.count=config.count;
        import_me.viewtype=config.viewtype;
        import_me.imodule=config.imodule;
        import_me.onlynamedesc=config.onlynamedesc;
        import_me.fileid=config.fileid;
        import_me.createSalary(config);
    },


    createSalary:function(config)
    {
        var onlyStore = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc','itemid1','itemid2']
        });

        //导入excel数据model
        Ext.define('Original', {
            extend: 'Ext.data.Model',
            fields:['itemid','itemdesc','itemid1','itemid2']
        });

        //导入excel数据store
        var originalDataStore = Ext.create('Ext.data.Store', {
            model: 'Original',
            proxy:{
                type: 'transaction',
                functionId:'GZ00000043',
                extraParams:{
                    salaryid:import_me.salaryid,
                    fileid:import_me.fileid
                },
                reader: {
                    type: 'json',
                    root: 'originalDataList'
                }
            },
            autoLoad: true,

            listeners: {
                load: function(e,records){
                    //当上传excel格式错误时导致list没有内容，提示错误
                    if(originalDataStore.count() == 0)
                        Ext.showAlert(gz.msg.importFailure);
                    else if(import_me.window1.hidden == true)
                        import_me.window1.show();
                    //依据此数据构建唯一性指标下拉列表。
                    var p = new Ext.data.Record({ itemid: "",itemdesc:'（空）',itemid1:'',itemid2:'' });
                    var onlyNameIndex=-1;
                    var onlyName="";
                    var appprocessName=-1;
                    onlyStore.add(p);
                    Ext.Array.each(records,function(record,index,count){
                        if(record.data.itemid=='onlyName'){//获取唯一性指标项
                            onlyNameIndex=index;
                            onlyName=record.data.itemdesc;
                        }
                        if(record.data.itemid == '审批意见') {
                            appprocessName=index;
                        }
                        onlyStore.add(record);
                    });
                    if(onlyNameIndex!=-1)//将唯一性指标项移除出页面列表store以免被显示出来。
                        originalDataStore.removeAt(onlyNameIndex);
                    if(appprocessName!=-1)
                        originalDataStore.removeAt(appprocessName);
//	   		        	if(import_me.onlynamedesc!="")
//	   		        		onlycbx.setValue(onlyName);//初始化值
                }}

        });

        //薪资项目可更新数据store
        var aimDataStore = Ext.create('Ext.data.Store', {
            fields:['itemid1','itemdesc1'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000043',
                extraParams:{
                    salaryid:import_me.salaryid,
                    flag:"1"
                },
                reader: {
                    type: 'json',
                    root: 'aimDataList'
                }
            },
            autoLoad: true
        });

        //可更新下拉框
        import_me.aimCom = Ext.widget('combo',{
            store:aimDataStore,
            width: 250,
            queryMode: 'local',
            repeatTriggerClick : true,
            editable: false,
            forceSelection: true,
            displayField: 'itemdesc1',//显示的值
            valueField: 'itemid1',//隐藏的值
            listeners: {
                select: {
                    fn: function(combox){
                        if(combox.getValue() == 'blank'){
                            combox.reset();
                            combox.setRawValue("");
                        }
                    }
                }
            }
        })

        //薪资项目关联数据store
        var aimDataStoreA = Ext.create('Ext.data.Store', {
            fields:['itemid2','itemdesc2'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000043',
                extraParams:{
                    salaryid:import_me.salaryid,
                    flag:"2"
                },
                reader: {
                    type: 'json',
                    root: 'aimDataListA'
                }
            },
            autoLoad: true
        });

        //关联数据下拉框
        import_me.aimComA = Ext.widget('combobox',{
            store:aimDataStoreA,
            width: 250,
            queryMode: 'local',
            repeatTriggerClick : true,
            editable: false,
            forceSelection: true,
            displayField: 'itemdesc2',//显示的值
            valueField: 'itemid2',//隐藏的值
            listeners: {
                select: {
                    fn: function(combox){
                        if(combox.getValue() == 'blank'){
                            combox.reset();
                            combox.setRawValue("");
                        }
                    }
                }
            }

        });

        //数据显示按钮
        var menu = Ext.create('Ext.menu.Menu', {
            width: 100,
            margin: '0 0 10 0',
            items: [{
                text: gz.button.repeatData,//重复数据
                listeners: {
                    click: {
                        fn: function(){import_me.showRepeatData(); }//显示excel中的重复数据
                    }
                }
            },{
                text: gz.button.noRelationData,//无对应数据
                listeners: {
                    click: {
                        fn: function(){ import_me.showNoRelationData(); }//显示excel中有的但是数据库里没有
                    }
                }
            }]
        });



        //按钮
        var tools = [{xtype:'button',text:gz.button.savaRelation,
            handler:function(){
                import_me.saveRelation()//保存对应方案
            }
        },{xtype:'button',text:gz.button.selectRelation,//选择方案
            handler:function(){
                Ext.require('SalaryUL.inout.ReadRelation',function(){
                    Ext.create("SalaryUL.inout.ReadRelation",{salaryid:import_me.salaryid});//显示对应方案
                })
            }
        },{xtype:'button',text:gz.button.importData,//导入数据
            handler:function(){
                import_me.importData();
            }
        },{xtype:'button',text:gz.button.viewData,//查看数据
            menu:menu
        }
            //,'->',onlycbx
        ];




        //显示上传excel数据的panel
        import_me.panel = Ext.widget("gridpanel",{
            store: originalDataStore,
            id:'originalId',
            columnLines:true,
            rowLines:true,
            width: 830,
            height:500,

            dockedItems: [{
                xtype: 'toolbar',
                dock: 'top',
                border:false,
                padding:'1 0 5 2',
                id:'panelbar',
                items: tools
            }],
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    pluginId:'celledit',
                    clicksToEdit: 1,
                    listeners:{
                        'edit':function(editor, e, eOpts ){
                            e.record.commit();
                        }
                    }
                })
            ],
            renderTo: Ext.getBody(),

            columns: [
                {text: 'itemid',menuDisabled:true, dataIndex: 'itemid', hidden: true},
                {text:gz.label.sourceData  ,menuDisabled:true,dataIndex: 'itemdesc', flex: 1},//gz.label.sourceData
                {text: gz.label.selectTargetData,itemId:'itemid1',menuDisabled:true,dataIndex: 'itemid1',flex: 1,editor:import_me.aimCom,//gz.label.selectTargetData
                    renderer:function(v,record){
                        var item = '';
                        if(import_me.aimCom.findRecordByValue(v)){
                            item = import_me.aimCom.findRecordByValue(v).data.itemdesc1
                        }
                        if(v == 'blank')
                            return "";
                        return item;
                    }
                },
                {text:gz.label.selectRelationData ,itemId:'itemid2',menuDisabled:true,dataIndex: 'itemid2',flex: 1,editor:import_me.aimComA,//gz.label.selectRelationData
                    renderer:function(v,record){
                        var itemA = '';
                        if(import_me.aimComA.findRecordByValue(v)){
                            itemA = import_me.aimComA.findRecordByValue(v).data.itemdesc2
                        }
                        if(v == 'blank')
                            return "";
                        return itemA;
                    }
                }
            ]
        });
        var css_title="#minWin_header {border-width: 0px 0px 1px 0px}";
        Ext.util.CSS.createStyleSheet(css_title,"title_css");

        var vs = Ext.getBody().getViewSize();
        import_me.window1=Ext.widget("window",{
            title: gz.label.selectImportMode,
            height:vs.height,
            width:vs.width,
            id:'minWin',
            layout:'fit',
            scrollable:false,
            modal:true,
            border:false,
            bodyBorder:false,
            style:'border-width:0px',
            bodystyle:'border-width:0px',
            closeAction:'destroy',
            items: [import_me.panel]
        });

    },

    //保存对应方案
    saveRelation:function(){
        var i = 0;
        var recordDatas = new Array();
        import_me.panel.getStore().each(function(record,index,count){ //遍历每一条数据
            var itemid1 = record.get('itemid1');
            var itemid2 = record.get('itemid2');
            if((itemid1 != null && itemid1 != '' && itemid1!='blank') || (itemid2 != null && itemid2 != '' && itemid2!='blank')){
                recordDatas[i] = record.data;
                i++;
            }
        });

        var onlyName='';
// 		if(import_me.onlynamedesc!=''){
// 			onlyName=Ext.getCmp('onlycbx').getValue()
//	 		if(onlyName==null)
//	 			onlyName='';
// 		}


        if(i == 0){
            Ext.showAlert(gz.msg.selectDataIn); //请选择需要更新或关联的数据项目
        }else{
            var panel = Ext.create('Ext.panel.Panel', {
                bodyPadding: 5,
                border:false,
                minButtonWidth:50,
                width: 380,
                height:100,
                // 表单域 Fields 将被竖直排列, 占满整个宽度
                layout: 'vbox',
                items:[{
                    xtype: 'fieldcontainer',
                    defaultType: 'radiofield',
                    layout: 'hbox',
                    align:'center',
                    items: [{
                        xtype:'textfield',
                        fieldLabel: '',
                        itemId:'cname_id',
                        id:'nameid',
                        value:gz.label.newRelation,
                        width: 380,
                        allowBlank: false
                    }]
                }],

                //保存 按钮.
                buttons: [{xtype:'tbfill'},{
                    text: common.button.ok,
                    handler: function() {
                        var schemeName=Ext.getCmp('nameid').value;
                        if(trim(schemeName)==''){
                            Ext.showAlert(gz.msg.nameRelationNotNull);
                            return;
                        }
                        if(import_me.fucCheckLength(schemeName)>30){
                            Ext.showAlert(gz.msg.NameTooLong);
                            return;
                        }
                        var map = new HashMap();
                        map.put("schemeName",schemeName);
                        map.put("recordDatas",recordDatas);
                        map.put("oper","save");
                        map.put("salaryid",import_me.salaryid);
                        map.put("onlyName",onlyName);
                        Rpc({functionId:'GZ00000047',async:false,success:function(response,action){
                                var result = Ext.decode(response.responseText);
                                var success = result.succeed;
                                if (success) {
                                    if(result.savemsg!=null&&result.savemsg.length>0){
                                        Ext.showAlert(result.savemsg);
                                        return;
                                    }else{
                                        Ext.showAlert(common.msg.saveSucess+"！");
                                    }
                                } else {
                                    Ext.showAlert( common.msg.saveFailed+"！");
                                }
                                win.close();
                            }},map);
                    }
                },{
                    text: common.button.cancel,
                    handler: function() {
                        win.close();
                    }
                },{xtype:'tbfill'}]
            });

            var win = Ext.create('Ext.window.Window', {
                title: gz.label.nameRelation,//请输入方案名称
                height: 110,
                resizable:false,
//			    alwaysOnTop:true,
                width: 400,
                layout: 'fit',
                items: [panel]
            }).show();
        }
    },

    //读取方案后显示
    readRelation:function(id){
        import_me.panel.getStore().load({
            id:id
        });
    },

    //确定导入数据
    importData:function(){
        var flag1 = true;
        var flag2 = true;
        var relationItem = new Array();//关联数据数组
        var oppositeItem = new Array();//更行数据数组
        var i = 0;
        var j = 0;
        var onlyName='';
// 		if(import_me.onlynamedesc!=''){
// 			onlyName=Ext.getCmp('onlycbx').getValue()
//	 		if(onlyName==null)
//	 			onlyName='';
// 		}
        import_me.panel.getStore().each(function(record,index,count){ //遍历每一条数据
            var itemid1 = record.get('itemid1');
            var itemid2 = record.get('itemid2');
            if(itemid1 != null && itemid1 != '' && itemid1!='blank'){
                oppositeItem[i]=record.data;
                i++;
                flag1 = false;
            }
            if(itemid2 != null && itemid2 != '' && itemid2!='blank'){
                relationItem[j]=record.data;
                j++;
                flag2 = false;
            }
        });

        if(flag1&&onlyName==''){
            import_me.validateItem(0);//更新指标
            return;
        }
        if(flag2){
            import_me.validateItem(1);//关联指标
            return;
        }
        import_me.relationItem=relationItem;
        import_me.oppositeItem=oppositeItem;


        Ext.showConfirm( common.msg.affirmImport, function(button, text) {
            if (button == "yes") {
                Ext.MessageBox.wait(common.msg.importing+"...", common.msg.wait);
                var map = new HashMap();
                map.put("relationItem",relationItem);
                map.put("oppositeItem",oppositeItem);
                map.put("appdate",import_me.appdate);
                map.put("salaryid",import_me.salaryid);
                map.put("fileid",import_me.fileid);
                map.put("onlyname",onlyName);
                map.put("inputType","1");
                Rpc({functionId:'GZ00000048',success:function(response,action){
                        var result = Ext.decode(response.responseText);
                        var success = result.succeed;

                        if (success) {
                            Ext.MessageBox.close();
                            import_me.showInputMsg(result);
                        }else{
                            Ext.MessageBox.close();
                            Ext.showAlert( Ext.decode(response.responseText).message);
                        }
                    }},map);
            }
        })
    },

    showInputMsg:function(result){
        var panel = Ext.create('Ext.panel.Panel', {
            bodyPadding: 5,
            border:false,
            minButtonWidth:50,
            width: 360,
            height:200,
            // 表单域 Fields 将被竖直排列, 占满整个宽度
            layout: 'vbox',
            items:[{
                xtype: "label",
                height:20,
                width:200,
                text:"检验结果如下："
            },
                {
                    xtype: "label",
                    margin:'0 0 0 100',
                    width:200,
                    height:20,
                    text:"可更新数据"+result.updateNum+"条。"
                },
                {
                    xtype: "label",
                    margin:'0 0 0 100',
                    width:200,
                    height:20,
                    text:"可新增数据"+result.insertNum+"条。"
                },

                {
                    xtype: "label",
                    margin:'0 0 0 100',
                    width:200,
                    height:20,
                    text:"无效数据"+result.errNum+"条。"
                }
//				    ,
//			    	{
//				    	xtype: "label",
//				    	margin:'0 0 0 100',
//				    	text:"存在无法对应数据"+result.unNum+"条。"
//					}
            ],

            //保存 按钮.
            buttons: [{xtype:'tbfill'},{
                text: '导出详情',
                handler: function() {
                    var map = new HashMap();
                    map.put("tempTableList",result.tempTableList);
                    map.put("salaryid",import_me.salaryid);
                    map.put("tempTableName",result.tempTableName);
                    Rpc({functionId:'GZ00000050',async:false,success:function(response,action){
                            var result = Ext.decode(response.responseText);
                            var success = result.succeed;
                            if (success) {
                            	var fileName = getDecodeStr(result.fileName);
                                window.location.target="_blank";
                                window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
                            } else {

                            }
                        }},map);
                }
            },{
                text: '继续导入',
                handler: function() {
                    Ext.MessageBox.wait(common.msg.importing+"...", common.msg.wait);

                    var onlyName='';
//		     		if(import_me.onlynamedesc!=''){
//		     			onlyName=Ext.getCmp('onlycbx').getValue()
//		    	 		if(onlyName==null)
//		    	 			onlyName='';
//		     		}
                    var map = new HashMap();
                    map.put("relationItem",import_me.relationItem);
                    map.put("oppositeItem",import_me.oppositeItem);
                    map.put("appdate",import_me.appdate);
                    map.put("count",import_me.count);
                    map.put("salaryid",import_me.salaryid);
                    map.put("fileid",import_me.fileid);
                    map.put("tempTableName",result.tempTableName);
                    map.put("onlyname",onlyName);
                    map.put("inputType","2");
                    Rpc({functionId:'GZ00000048',success:function(response,action){
                            var result = Ext.decode(response.responseText);
                            var success = result.succeed;
                            if (success) {
                                Ext.MessageBox.close();
                                Ext.showAlert(result.msg,function(){
                                    Ext.getCmp('winInput').close();
                                    import_me.window1.close();
                                    accounting.backToAccount();
                                })
                            }else{
                                Ext.MessageBox.close();
                                Ext.showAlert( Ext.decode(response.responseText).message);
                            }
                        }},map);
                }
            },{
                text: common.button.cancel,
                handler: function() {
                    win.close();
                }
            },{xtype:'tbfill'}]
        });

        var win = Ext.create('Ext.window.Window', {
            title: '导入数据信息',
            height: 180,
            resizable:false,
            modal:true,
            id:'winInput',
//		    alwaysOnTop:true,
            width: 380,
            layout: 'fit',
            items: [panel]
        }).show();
    },

    //显示未对应数据，即excel有但数据库中没有
    showNoRelationData:function(){
        //if(import_me.validateItem(originalGrid,aimGrid,null,relationGrid))
        var i = 0;
        var arrayObj = new Array();//创建一个数组
        var fields = new Array();//表格表头数组
        import_me.panel.getStore().each(function(item,index,count){ //遍历每一条数据
            fields[index] = item;
            if(item.get('itemid2') != null && item.get('itemid2') != ""  && item.get('itemid2') != 'blank'){
                arrayObj[i]=item.data;
                i++;
            }
        });

        if(i == 0){
            import_me.validateItem(1);
            return;
        }

        var map = new HashMap();
        Ext.require('SalaryUL.inout.ShowNoRelationData',function(){
            Ext.create("SalaryUL.inout.ShowNoRelationData",{fileid:import_me.fileid,salaryid:import_me.salaryid,fields:fields,relationItem:arrayObj});
        })
    },
    //显示excel中重复的数据
    showRepeatData:function(){
        var arrayObj = new Array();//创建一个数组
        var i = 0;
        import_me.panel.getStore().each(function(item,index,count){ //遍历每一条数据
            if(item.get('itemid2') != null && item.get('itemid2') != ""  && item.get('itemid2') != 'blank'){
                arrayObj[i]=item.data;
                i++;
            }
        });

        if(i == 0){
            import_me.validateItem(1);
            return;
        }

        Ext.require('SalaryUL.inout.ShowReapeatData',function(){
            Ext.create("SalaryUL.inout.ShowReapeatData",{fileid:import_me.fileid,salaryid:import_me.salaryid,relationItem:arrayObj});
        })
    },

    //验证指标不能为空，1是关联指标，0是更新指标
    validateItem:function(type)
    {
        if(type == 0){
            Ext.showAlert( gz.msg.selectDataRelationItem); //请指定更新指标
            return false;
        }

        if(type == 1){
            Ext.showAlert(gz.msg.specifyRelationItem); //请指定关联指标
            return false;
        }
    },
    fucCheckLength:	function (strTemp){
        var i,sum;
        sum=0;
        for(i=0;i<strTemp.length;i++){
            if ((strTemp.charCodeAt(i)>=0) && (strTemp.charCodeAt(i)<=255)){
                sum=sum+1;
            }else{
                sum=sum+2;
            }
        }
        return sum;
    }
});