/**
 * 薪资发放-导入数据-对比页面
 */
Ext.define('SearchTaxUL.SetTaxRelation',{
    constructor:function(config){
        import_me = this;
        import_me.viewport = '';
        import_me.salaryid = config.salaryid;
        import_me.fileid = config.fileid;
        import_me.datetime=config.datetime;
        var map = new HashMap();
        map.put("salaryid",import_me.salaryid);
        map.put("fileid",config.fileid);
        map.put("flag","excelheads");
        Rpc({functionId:'GZ00000514',async:false,success:function(form,action){
                var result = Ext.decode(form.responseText);
                var flag=result.succeed;
                if(flag==true){
                    import_me.dataFiledList=result.dataFiledList;
                    import_me.aimDataList=result.aimDataList;//目标指标
                    if(import_me.dataFiledList.length==0){
                        Ext.showAlert("文件格式不正确，第一行为导入的文件列名，不能为空！");
                        import_me.goback();
                        //return;
                    }else{
                        import_me.createSalary(config);
                    }
                }else{
                    Ext.showAlert(result.message,function(){
                    	import_me.goback();        			
                    },this);
                    
                }
            }},map);
    },
    createSalary:function(config)
    {
        //导入excel数据model
        Ext.define('Original', {
            extend: 'Ext.data.Model',
            fields:['itemid','itemdesc','itemid1','itemid2']
        });

        //导入excel数据store
        import_me.originalDataStore = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc','itemid1','itemid2'],
            data:import_me.dataFiledList
        });
        
        var aimDataStore = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc'],
            data:import_me.aimDataList
        });
        /*//薪资项目可更新数据store
        var aimDataStore = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000514',
                extraParams:{
                    salaryid:import_me.salaryid
                },
                reader: {
                    type: 'json',
                    root: 'aimDataList'
                }
            },
            autoLoad: true
        });*/
        //可更新下拉框
        import_me.aimCom = Ext.widget('combo',{
            store:aimDataStore,
            width: 250,
            queryMode: 'local',
            repeatTriggerClick : true,
            editable: false,
            forceSelection: true,
            displayField: 'itemdesc',//显示的值
            valueField: 'itemid',//隐藏的值
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
            fields:['itemid','itemdesc'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000514',
                extraParams:{
                	fileid:config.fileid,
                    salaryid:import_me.salaryid,
                    flag:"relation"
                },
                reader: {
                    type: 'json',
                    root: 'relationList'
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
            displayField: 'itemdesc',//显示的值
            valueField: 'itemid',//隐藏的值
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

        //按钮
        var tools = [{xtype:'button',text:gz.button.importData,//导入数据
            handler:function(){
                import_me.importData();
            }
        },{xtype:'button',text:common.button.toreturn,//返回
            handler:function(){
                import_me.goback();
            }
        }];
        //显示上传excel数据的panel
        import_me.panel = Ext.widget("gridpanel",{
            title: gz.label.zxdeclare.declareRelationshipTitle,//设置对应关系
            store: import_me.originalDataStore,
            id:'originalId',
            columnLines:true,
            rowLines:true,
            width: 830,
            height:500,
            tbar:tools,
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
            columns: [
                {text: 'itemid',menuDisabled:true, dataIndex: 'itemid', hidden: true},
                {text: gz.label.sourceData ,menuDisabled:true,dataIndex: 'itemdesc', flex: 1},
                {text: gz.label.selectTargetData,itemId:'itemid1',menuDisabled:true,dataIndex: 'itemid1',flex: 1,editor:import_me.aimCom,
                    renderer:function(v,record,obj){
                        var item = '';
                        if(import_me.aimCom.findRecordByValue(v)){
                            item = import_me.aimCom.findRecordByValue(v).data.itemdesc;
                        }
                        //第一次找不到数据的情况，直接对应的显示名称和excel名称相同的目标指标
                        if(v == "" && !item) {
                        	var items_ = aimDataStore.data.items;
                        	for(var i = 1; i < items_.length; i++) {
                        		var itemdesc = items_[i].data.itemdesc;
                        		if(itemdesc.split(":").length>0) {
                        			itemdesc = Ext.String.trim(itemdesc.split(":")[1].split("(")[0]);
                        		}
                        		if(itemdesc == Ext.String.trim(obj.data.itemdesc)) {
                        			//赋值
                        			obj.data.itemid1 = Ext.String.trim(items_[i].data.itemdesc.split(":")[0]);
                        			return items_[i].data.itemdesc;
                        		}
                        	}
                        }
                        
                        if(v == 'blank')
                            return "";
                        
                        return item;
                    }
                },
                {text: gz.label.selectRelationData,itemId:'itemid2',menuDisabled:true,dataIndex: 'itemid2',flex: 1,editor:import_me.aimComA,
                    renderer:function(v,record){
                        var itemA = '';
                        if(import_me.aimComA.findRecordByValue(v)){
                            itemA = import_me.aimComA.findRecordByValue(v).data.itemdesc
                        }
                        if(v == 'blank')
                            return "";
                        return itemA;
                    }
                }
            ],
            renderTo: Ext.getBody()
        });

        import_me.viewport = Ext.create('Ext.container.Viewport',{
            autoScroll:false,

            style:'backgroundColor:white',
            layout:'fit',
            items:[{
                xtype:'panel',
                width: 950,
                height:500,
                title: gz.label.selectRelationIndex,
                border:true,
                layout: {
                    type: 'vbox',
                    align: 'center',
                    pack :'center'
                },
                items:[import_me.panel]
            }]
        })
    },
    //返回所得税管理页面
    goback:function(){
        if(import_me.viewport!='')
            import_me.viewport.destroy();
        if(import_me.salaryid!='')
            Ext.require('SearchTaxUL.SearchTax', function(){
                Ext.create("SearchTaxUL.SearchTax",{salaryid:import_me.salaryid,datetime:import_me.datetime});
            });
        else
            window.location.href="/module/gz/tax/SearchTax.html?datetime="+import_me.datetime;
    },
    //确定导入数据
    importData:function(){
        var flag1 = true;
        var flag2 = true;
        var flag3 = true;	//判断报税时间
        var flag4 = true;	//判断人员库
        var flag5 = true;	//判断单位
        var flag6 = true;	//判断部门
        var relationItem = new Array();//关联数据数组
        var oppositeItem = new Array();//更新数据数组
        var i = 0;
        var j = 0;
        import_me.panel.getStore().each(function(record,index,count){ //遍历每一条数据
            var itemid = record.get('itemid');
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
            if(itemid1 != "" && itemid1 != null) {
	            if("DECLARE_TAX"==itemid1.toUpperCase() || "报税时间"==itemid){
	                flag3 = false;
	            }
	            if("NBASE"==itemid1.toUpperCase() || ("人员库"==itemid || "人员库标识"==itemid)){
	                flag4 = false;
	            }
	            if("A0101"==itemid1.toUpperCase() || ("单位名称"==itemid||"单位"==itemid)){
	                flag5 = false;
	            }
	            if("E0122"==itemid1.toUpperCase() || "部门"==itemid){
	                flag6 = false;
	            }
            }
        });
        if(flag1){
            import_me.validateItem(0);//更新指标
            return;
        }
        if(flag2){
            import_me.validateItem(1);//关联指标
            return;
        }
        if(flag3||flag4||flag5||flag6){
            Ext.showAlert("人员库，单位，部门，报税时间不能为空！");//请指定更新指标
            return;
        }
        Ext.Msg.confirm(common.button.promptmessage, common.msg.affirmImport, function(button, text) {
            if (button == "yes") {
                Ext.MessageBox.wait(common.msg.importing+"...", common.msg.wait);
                var map = new HashMap();
                map.put("relationItem",relationItem);
                map.put("oppositeItem",oppositeItem);
                map.put("salaryid",import_me.salaryid);
                map.put("fileid",import_me.fileid);
                Rpc({functionId:'GZ00000514',success:function(response,action){
                        var success = Ext.decode(response.responseText).succeed;
                        var rowNums = Ext.decode(response.responseText).rowNums;
                        Ext.MessageBox.close();
                        if (success) {
                            import_me.goback();
                            Ext.showAlert(common.msg.successImport+rowNums+common.msg.dataNums+"！");
                        } else {
                            Ext.showAlert(Ext.decode(response.responseText).message);
                        }
                    }},map);
            }
        })
    },
    //验证指标不能为空，1是关联指标，0是更新指标
    validateItem:function(type)
    {
        if(type == 0){
            Ext.showAlert("请指定更新指标！");
            return false;
        }

        if(type == 1){
            Ext.showAlert("请指定关联指标！");
            return false;
        }
    }
});