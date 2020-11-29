Ext.define('TaxTable.TaxTableDetail', {
    extend: 'Ext.panel.Panel',
    xtype: 'taxtabledetail',
    layout: 'fit',
    bodyStyle:'z-index:2;',
    title:'',
    border: false,
    taxid:"",//加密的税率表编号
    description:"",//税率表名称
    taxModeCode:"",//税率表计税方式
    k_base:"",//税率表基数
    initComponent: function () {
        taxTableDetail = this;
        this.callParent();
        taxTableDetail.comboxData = [];
        //加载表格及数据
        this.loadData();
    },
    /*加载表格数据*/
    loadData: function () {
        if(this.k_base == "" || this.k_base == null){
            this.k_base = 0;
        }
        var vo = new HashMap();
        vo.put("postNum",this.taxid);
        Rpc({functionId: 'GZ00001008',async:false,success:taxTableDetail.getTableOK, scope: this}, vo);
    },
    getTableOK:function(form,action){
        var result = Ext.decode(form.responseText);
        var return_cod = result.return_code;
        if(return_cod == "fail"){
            Ext.Msg.alert(gz.taxTableDetail.msg.hint,result.return_msg);
            return;
        }
        var return_data = result.return_data;
        var obj = Ext.decode(return_data.gridConfig);
        //加载表格之前禁用menu和排序
        obj.beforeBuildComp = function (grid) {
            var columns = grid.tableConfig.columns;
            for (var i in columns) {
                columns[i].menuDisabled = true;//禁用下拉按钮
                columns[i].sortable = false;//禁止排序
                columns[i].draggable=false;//禁止拖拽
            }
            grid.tableConfig.dockedItems = [];
            grid.tableConfig.bodyStyle += 'border-color:#ffffff;';
        };
        taxTableDetail.sortFlag = false;
        taxTableDetail.tableObj = new BuildTableObj(obj);
        taxTableDetail.setDeatailPanelTitle(taxTableDetail.taxid);//设置明细面板标题：新建或修改
        taxTableDetail.tableObj.insertItem(taxTableDetail.createHeadPanel());//表格上面部分
        taxTableDetail.add(taxTableDetail.tableObj.getMainPanel());//加载表格
        taxTableDetail.gridPanel = taxTableDetail.tableObj.tablePanel;
        taxTableDetail.gridStore = taxTableDetail.gridPanel.getStore();
        taxTableDetail.gridStore.on('load',function(t){
            taxTableDetail.tableObj.insertItem(taxTableDetail.createData());//新建时插入七条数据
            t.add({
                series:'end',
                ynse_down: 'end',
                handle:'end',
                isImg:true
            });
            taxTableDetail.tableObj.dataStore.getModifiedRecords();
            taxTableDetail.tableObj.dataStore.commitChanges();//提交更改的数据

        });
        taxTableDetail.addEvent();
    },
    //上限新增图标按钮
    addImgFunc:function(value, metaData, record, rowIndex, colIndex, store, view){
        var addFlag = record.data.isImg;
        if(addFlag){
            if(record.data.series == "end"){
                var gridPanel = taxTableDetail.gridPanel;
                view.grid.addListener(
                    'cellclick',function (table,td,cellIndex,record) {
                        var fileName;
                        var record = record.get("series");
                        if(record == "end"){
                            taxTableDetail.gridPanel.getSelectionModel().clearSelections();
                            taxTableDetail.gridPanel.getView().refresh();
                            return false;
                        }
                    }
                )
            }
            return "<a href=javascript:taxTableDetail.addFunc()><img style='width: 20px;height: 20px' src='/module/gz/mysalary/images/org_add.png'></a>";
        }
        return value;
    },

    //新增
    addFunc:function(store){
        var count = taxTableDetail.tableObj.tablePanel.store.data.length-1;
        var map = new HashMap();
        map.put("taxid",this.taxid);
        map.put("taxitem","");
        map.put("series",count+1);
        if(count-1 >= 0){
            var record = taxTableDetail.tableObj.tablePanel.getStore().getData().getAt(count-1);
            map.put("ynse_down",record.get('ynse_up'));
        }else{
            map.put("ynse_down",0);
        }
        map.put("ynse_up",0);
        map.put("sl",0.00);
        map.put("sskcs",0);
        map.put("flag",'0');
        map.put("kc_base",0);
        map.put("description","");
        taxTableDetail.tableObj.tablePanel.store.insert(count,map);
        var sel = taxTableDetail.tableObj.tablePanel.getSelectionModel();
        sel.select(count,true);
    },
    //保存的方法
    saveFunc:function (store) {
        TaxTable_me.gridPanel.getStore();
        var map = new HashMap();
        var taxList = new Array();
        var taxDetail = new Array();
        var isExist = false;
        var taxMap = {};
        var taxDetailMap;
        var gridStore = taxTableDetail.gridStore;
        var taxName = Ext.getCmp("name").getValue();
        var k_base = Ext.getCmp("cardinal").getValue();
        var taxNameOld = Ext.getCmp("name").originalValue;
        var k_baseOld = Ext.getCmp("cardinal").originalValue;
        var taxWay = Ext.getCmp("taxWay").getValue();
        var taxWayOld = Ext.getCmp("taxWay").originalValue;
        var modifiedRecords = gridStore.getModifiedRecords();
        var taxTableItems = TaxTable_me.gridPanel.getStore().getData().items;
        //判断名称是否为空
        if(taxName.replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' ).length == 0){
            Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,gz.taxTableHomePage.msg.stringIsNull);
            return;
        }
        //判断名称不可输入空格&nbsp，null,NULl等等特殊字符
        var strTestWSave = /&nbsp|null|[<>?"{}.\[\]]/ig;
        if(strTestWSave.test(taxName)){
            Ext.Msg.alert(gz.taxTableDetail.msg.hint,gz.taxTableHomePage.msg.stringIllegal);
            return;
        }
        //判断名称长度
        if(Ext.getStringByteLength(taxName)>50){
            Ext.Msg.alert(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.nameLength);
            return;
        }
        //判断基数是否为空
        if(k_base.replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' ).length == 0){
            Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.cardinalNone);
            return;
        }
        //判断基数是否为纯数字
        // var reg = /^(([^0][0-9]+|0)\r.([0-9]{1,2})$)|^([^0][0-9]+|0)([0-9]{1,2})$/;
        var reg = /^[0-9]+(.[0-9]{1,2})?$/;
        if(!reg.test(k_base)){
            Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.k_baseIsNumber);
            return;
        }
        //级数不能小于0
        if(k_base<0){
            Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,gz.taxTableHomePage.msg.numLessThanZero)
            return;
        }
        //判断税率表名称是否重复
        Ext.each(taxTableItems, function (record) {
            var taxid = record.get("taxid");
            var description = record.get("description");
            if(Ext.util.Format.trim(description)  == Ext.util.Format.trim(taxName) && taxTableDetail.taxid != taxid){
                isExist = true;
            }
        });
        if(isExist){
            var message = gz.taxTableHomePage.msg.importTypeTitle+gz.taxTableDetail.msg.leftBrackets+taxName;
            message= message+gz.taxTableDetail.msg.rightBrackets+gz.taxTableDetail.msg.taxExist;
            Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,message);
            return;
        }
        //没有是需要保存的数据
        if (( modifiedRecords.length == 0) && taxName == taxNameOld && k_base == k_baseOld && taxWay == taxWayOld) {
            Ext.Msg.alert(gz.taxTableDetail.msg.hint, gz.taxTableDetail.msg.saveNone);
            return;
        }
        //taxTableDetail.sortDetail();
        //验证数据是否正确
        for (var i = 0; i < gridStore.data.length; i++) {
            if(gridStore.data.items[i].data.ynse_down == "end"){
                continue;
            }else{
                var count = gridStore.data.length - 1;
                var ynse_up = gridStore.data.items[i].data.ynse_up;
                var ynse_down = gridStore.data.items[i+1].data.ynse_down;
                if(gridStore.data.items[i].data.ynse_up <= gridStore.data.items[i].data.ynse_down){
                    var message = gz.taxTableDetail.msg.zore+(i+1)+gz.taxTableDetail.msg.row+gz.taxTableDetail.msg.upperLimit;
                    message = message+gridStore.data.items[i].data.ynse_up+gz.taxTableDetail.msg.overstepLowerLimit;
                    message = message+gridStore.data.items[i].data.ynse_down+gz.taxTableDetail.msg.rightBrackets;
                    taxTableDetail.tableObj.tablePanel.getSelectionModel().select(gridStore.data.items[i]);
                    Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,message);
                    return;
                }
                if(ynse_up != ynse_down && i < count-1){
                    var message = gz.taxTableDetail.msg.zore+(i+1)+gz.taxTableDetail.msg.row+gz.taxTableDetail.msg.upperLimit;
                    message = message+gridStore.data.items[i].data.ynse_up+gz.taxTableDetail.msg.andCondition+(i+2)+gz.taxTableDetail.msg.rowLowerLimit;
                    message = message+gridStore.data.items[i+1].data.ynse_down+gz.taxTableDetail.msg.consistent;
                    taxTableDetail.tableObj.tablePanel.getSelectionModel().select(gridStore.data.items[i]);
                    Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,message);
                    return;
                }
            }
        }
        //待保存的数据
        var items = modifiedRecords;
        for (var i = 0; i < items.length; i++) {
            for (var j = 0; j < items.length - i -1; j++) {
                if (items[j].data.series > items[j + 1].data.series) {
                    var temp = items[j];
                    items[j] = items[j + 1];
                    items[j + 1] = temp;
                }
            }
        }
        for (var i = 0; i < items.length; i++) {
            if(items[i].data.ynse_down == "end"){
                continue;
            }else{
                taxDetailMap = {};
                taxDetailMap['taxid'] = items[i].data.taxid;
                taxDetailMap['taxitem'] = items[i].data.taxitem+'';
                taxDetailMap['ynse_down'] = items[i].data.ynse_down+'';
                taxDetailMap['ynse_up'] = items[i].data.ynse_up+'';
                taxDetailMap['sl'] = items[i].data.sl+'';
                taxDetailMap['flag'] = items[i].data.flag;
                taxDetailMap['sskcs'] = items[i].data.sskcs+'';
                taxDetailMap['kc_base'] = items[i].data.kc_base+'';
                taxDetailMap['description'] = items[i].data.description;
                taxDetail.push(taxDetailMap);
            }
        }
        //计税方式由代码转为名称
        var taxWay = Ext.getCmp('taxWay').getValue();
        for (var i = 0; i < comboxData.length; i++) {
            if(taxWay == comboxData[i].codeitemdesc){
                taxWay = comboxData[i].codeitemid;
                break;
            }
        }
        var saveAll = false;
        var ids = [];
        if(taxTableDetail.sortFlag && taxTableDetail.dataLength > 0){
            //taxDetail = taxTableDetail.gridStore.data.items;
            taxDetail.length = 0;;
            var detailItems = taxTableDetail.gridStore.data.items;
            for (var i=0;i<detailItems.length-1;i++){
                taxDetailMap = {};
                taxDetailMap['taxid'] = detailItems[i].data.taxid;
                taxDetailMap['taxitem'] = '';
                taxDetailMap['ynse_down'] = detailItems[i].data.ynse_down+'';
                taxDetailMap['ynse_up'] = detailItems[i].data.ynse_up+'';
                taxDetailMap['sl'] = detailItems[i].data.sl+'';
                taxDetailMap['flag'] = detailItems[i].data.flag;
                taxDetailMap['sskcs'] = detailItems[i].data.sskcs+'';
                taxDetailMap['kc_base'] = detailItems[i].data.kc_base+'';
                taxDetailMap['description'] = detailItems[i].data.description;
                taxDetail.push(taxDetailMap);
                if(detailItems[i].data.taxitem == ''){
                    continue;
                }
                ids.push(detailItems[i].data.taxid + '`' + detailItems[i].data.taxitem);
            }
            saveAll = true;
        }
        taxMap['taxid'] = this.taxid;
        taxMap['description'] = Ext.getCmp('name').getValue();
        taxMap['taxModeCode'] = taxWay;
        taxMap['k_base'] = Ext.getCmp('cardinal').getValue();
        taxList.push(taxMap);
        map.put("taxid",this.taxid);
        map.put("taxData", taxList);
        map.put("taxDetail", taxDetail);
        map.put("saveAll", saveAll);
        map.put("ids", ids);
        Rpc({functionId: 'GZ00001006', success: taxTableDetail.saveOK, scope: this}, map);
    },
    saveOK:function (result) {
        var result = Ext.decode(result.responseText);
        var return_cord = result.return_code;
        var msg = result.return_msg;
        this.taxid = result.taxid;
        if(return_cord == "success"){
            Ext.getCmp("name").originalValue = Ext.getCmp("name").getValue();
            Ext.getCmp("cardinal").originalValue = Ext.getCmp("cardinal").getValue();
            Ext.getCmp("taxWay").originalValue = Ext.getCmp("taxWay").getValue();
            Ext.Msg.alert(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.saveOK);
            //taxTableDetail.tableObj.dataStore.commitChanges();
            taxTableDetail.reloadData();
            taxTableDetail.sortFlag = false;
        }else{
            Ext.Msg.alert(gz.taxTableDetail.msg.hint,eval(msg));
        }
    },
    //删除
    deleteFunc:function (value) {
        var html = '';
        if(value != "end"){
            html = "<a href=javascript:taxTableDetail.deletes()><span>"+gz.label.del+"</span></a>";
        }
        return html;
    },
    //删除的方法
    deletes:function(){
        var rows = taxTableDetail.tableObj.tablePanel.getSelectionModel().getSelection();
        if(rows[0].data.taxitem == ""){
            var selectedItems = taxTableDetail.gridPanel.getSelectionModel().selected.items;
            taxTableDetail.gridStore.remove(selectedItems);
            /*重新排序级数*/
            var series = rows[0].data.series;
            var gridStore = taxTableDetail.gridStore;
            for (var i = series; i < gridStore.data.length; i++) {
                var ynse_downRecord = taxTableDetail.tableObj.tablePanel.getStore().getData().getAt(i-1);
                ynse_downRecord.data['series'] = i;
                taxTableDetail.gridPanel.getView().refresh();
            }
        }else{
            var gridStore = taxTableDetail.gridStore.data.items;
            var count = gridStore.length-2;
            var	store = taxTableDetail.gridPanel.getSelectionModel().selected.items;
            if(store[0].data.ynse_down != gridStore[count].data.ynse_down){
                Ext.MessageBox.alert(gz.taxTableDetail.msg.hint,store[0].data.sl+gz.taxTableDetail.msg.notDelete);
                return;
            }
            var taxid = store[0].data.taxid;
            var taxitem = store[0].data.taxitem;
            var vo = new HashMap();
            vo.put("taxid",taxid);
            vo.put("taxitem",taxitem);
            Ext.MessageBox.confirm(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.isDelete,function (btn) {
                if(btn == "yes"){
                    Rpc({functionId:'GZ00001007',success:taxTableDetail.deleteOK,scope:this},vo);
                }else{
                    return;
                }
            })
        }

    },
    deleteOK:function(result){
        var result = Ext.decode(result.responseText);
        var return_code = result.return_code;
        var return_msg = result.return_msg;
        var selectedItems = taxTableDetail.gridPanel.getSelectionModel().selected.items;
        if(return_code == "success"){
            var rows = taxTableDetail.tableObj.tablePanel.getSelectionModel().getSelection();
            // Ext.Msg.alert(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.deleteSuccess);
            taxTableDetail.gridStore.remove(selectedItems);
            /*重新排序级数*/
            var series = rows[0].data.series;
            var gridStore = taxTableDetail.gridStore;
            for (var i = series; i < gridStore.data.length; i++) {
                var ynse_downRecord = taxTableDetail.tableObj.tablePanel.getStore().getData().getAt(i-1);
                ynse_downRecord.set('series',i);
            }
            //taxTableDetail.tableObj.dataStore.commitChanges();
	     taxTableDetail.reloadData();
        }else{
            Ext.Msg.alert(gz.taxTableDetail.msg.hint,return_msg);
        }

    },
    //返回
    returnFunc:function(){
        var modifiedRecords = taxTableDetail.gridStore.getModifiedRecords();
        var taxName = Ext.getCmp("name").getValue();
        var taxNameOld = Ext.getCmp("name").originalValue;
        var k_base = Ext.getCmp("cardinal").getValue();
        var k_baseOld = Ext.getCmp("cardinal").originalValue;
        var taxWay = Ext.getCmp("taxWay").getValue();
        var taxWayOld = Ext.getCmp("taxWay").originalValue;
        if(modifiedRecords.length == 0 && taxName == taxNameOld && k_base == k_baseOld && taxWay == taxWayOld){
            window.location.href = "TaxTableSet.html";
        }else{
            Ext.MessageBox.confirm(gz.taxTableDetail.msg.hint,gz.taxTableDetail.msg.dataChange,function (btn) {
                if(btn == "yes"){
                    window.location.href = "TaxTableSet.html";
                }else{
                    return;
                }
            })
        }
    },
    //操作列不允许编辑
    edit:function(){
        return false;
    },
    //头部分
    createHeadPanel:function () {
        var me = this;
        var vo = new HashMap();
        vo.put('operateType','getTaxMode');
        Rpc({functionId:'GZ00001008',success:function (form) {
                    var result = Ext.decode(form.responseText);
                    comboxData = result.taxModeData;
                },scope:this,async:false},vo);
        var taxWay = Ext.create('Ext.data.Store',{
            fields:['codeitemid','codeitemdesc'],
            data:comboxData
        });
        var count = comboxData.length - 1;
        if(this.taxModeCode == ""){
            //取计税方式最后一个
            this.taxModeCode = comboxData[count].codeitemdesc;
        }else{
            for (var i = 0; i <= count; i++) {
                if(this.taxModeCode == comboxData[i].codeitemid){
                    this.taxModeCode =  comboxData[i].codeitemdesc;
                    break;
                }
            }
        }
        me.head = new Ext.FormPanel({
            xtype:"form",
            id:"formPanel",
            layout:"vbox",
            height:55,
            border:false,
            items:[{
                layout:"hbox",
                border:false,
                items:[{
                    id:'name',
                    itemId:'taxName',
                    xtype:'textfield',
                    fieldLabel:gz.taxTableDetail.msg.name,
                    value:this.description,
                    forceMaxLength:true,
                    validator:function(val){
                        if(Ext.getStringByteLength(val)>50)
                            return "该输入项的最大长度是50个字符！";
                        else
                            return true;
                    },
                    labelWidth:40,
                    height:23,
                    width:463,
                    labelAlign:'right',
                    allowBlank:false,
                    blankText:gz.taxTableDetail.msg.nameNone
                },{
                    xtype: 'component',
                    html: '<font color="red">*</font>',
                    margin: '3 0 0 3'
                }]
            },{
                layout:"hbox",
                margin:'5 0 0 0',
                border:false,
                items:[{
                    id:'cardinal',
                    xtype:'textfield',
                    fieldLabel:gz.taxTableDetail.msg.cardinal,
                    value:Ext.util.Format.number(this.k_base,'0.00'),
                    labelWidth:40,
                    height:23,
                    width:200,
                    labelAlign:'right',
                    allowBlank:false,
                    blankText:gz.taxTableDetail.msg.cardinalNone,
                    listeners:{
                        blur:function( t, e, eOpts ){
                            Ext.getCmp('cardinal').setValue(Ext.util.Format.number(t.value,'0.00'));
                        }
                    }
                },{
                    xtype: 'component',
                    html: '<font color="red">*</font>',
                    margin: '3 0 0 3'
                },{
                    id:"taxWay",
                    xtype:"combo",
                    value:this.taxModeCode,
                    store:taxWay,
                    editable:false,
                    height:23,
                    labelAlign:'right',
                    fieldLabel:gz.taxTableDetail.msg.taxWay,
                    displayField: 'codeitemdesc',
                    matchFieldWidth:true,
                    listeners:{
                        select : function (combo, record, eOpts) {
                            var kc_baseColumns = taxTableDetail.tableObj.tablePanel.getColumnManager().getHeaderByDataIndex('kc_base');
                            if(record.get('codeitemdesc') == gz.taxTableDetail.msg.remuneration){
                                kc_baseColumns.setHidden(false);
                            }else {
                                kc_baseColumns.setHidden(true);
                            }
                        }
                    }
                },{
                    xtype: 'component',
                    html: '<font color="red">*</font>',
                    margin: '3 0 0 3'
                }]
            }],
        });
        var toolBar = Ext.getCmp("taxTableDetail_toolbar");
        toolBar.add(me.head);
    },
    addEvent:function () {
        var cursor = this.taxid;//taxid 为空时名称插入光标
        var ynse_downColumns = taxTableDetail.tableObj.tablePanel.getColumnManager().getHeaderByDataIndex('ynse_down');
        if(ynse_downColumns){
            var ynse_downIndex = ynse_downColumns.getIndex();
        }
        taxTableDetail.tableObj.tablePanel.on('beforeedit',function (editor, context, eOpts) {
            var rowIndex = context.rowIdx;
            var length = taxTableDetail.tableObj.tablePanel.getStore().getData().items.length;
            var columnid = context.field;
            var series = context.record.get('series');
            if(columnid == 'series' || series == 'end' || rowIndex == length-1){
                return false;
            }
        });
        /*上下限联动*/
        taxTableDetail.tableObj.tablePanel.on('edit',function (editor, context, eOpts) {
            var dataIndex = context.column.dataIndex;
            var record = context.record;
            var rowIdx = context.rowIdx;
            if(dataIndex == 'ynse_up'){
                var ynse_up = record.get('ynse_up');
                var index = taxTableDetail.tableObj.dataStore.getData().items.length - 2;
                if(rowIdx+1<=index){
                    var ynse_downRecord = taxTableDetail.tableObj.tablePanel.getStore().getData().getAt(rowIdx+1);
                    ynse_downRecord.set('ynse_down',ynse_up);
                    var cellediting = TaxTable_me.gridPanel.findPlugin('cellediting');
                    cellediting.startEditByPosition({
                        row:rowIdx+1,
                        column:ynse_downIndex
                    });
                }
            }
        });
        taxTableDetail.tableObj.tablePanel.on('render', function (t) {
            var kc_baseColumns = taxTableDetail.tableObj.tablePanel.getColumnManager().getHeaderByDataIndex('kc_base');
            var taxMode = Ext.getCmp('taxWay').getDisplayValue();
            if(taxMode == gz.taxTableDetail.msg.remuneration){
                kc_baseColumns.setHidden(false);
            }else {
                kc_baseColumns.setHidden(true);
            }
            var flagColumn = t.getColumnManager().getHeaderByDataIndex("flag");
            if (flagColumn) {
                var eidtior = {
                    xtype: "combo",
                    store:{
                        fields:['flag','flagName'],
                        data:[{
                            'flag':'0',
                            'flagName':gz.taxTableDetail.msg.ynse_up
                        },{
                            'flag':'1',
                            'flagName':gz.taxTableDetail.msg.ynse_down
                        }]
                    },
                    displayField: 'flagName',
                    valueField: 'flag',
                    editable:false
                };
                flagColumn.setEditor(eidtior);
            }
            if(cursor == "" || cursor == null){
                taxTableDetail.query('#taxName')[0].focus();
            }
        });
        taxTableDetail.tableObj.dataStore.on('load',function (s,records) {
            taxTableDetail.dataLength = records.length;
        });
    },
    //封闭标志转换
    flagRenderFun:function (value) {
        if(value == '0'){
            value = gz.taxTableDetail.msg.ynse_up;
        }else if(value == '1'){
            value = gz.taxTableDetail.msg.ynse_down;
        }else{
            value = '';
        }
        return value;
    },
    //数据校验
    dataValidFunc:function (value) {
        if(Ext.getStringByteLength(value) > 11){
            return gz.taxTableDetail.msg.maxNumber;
        }
        if(value == "" || value == null){
            return gz.taxTableDetail.msg.sskcsNone;
        }
        return true;
    },

    //税率数据验证
    dataSlValidFunc:function (value) {
        if(value >= 1){
            return gz.taxTableDetail.msg.sl;
        }
        if(value == "" || value == null){
            return;
        }
        return true;
    },

    //设置明细面板标题：新建或修改
    setDeatailPanelTitle:function (taxid) {
        var html = '<div style="float:left">';
        if(taxid){
            html += gz.taxTableDetail.updateTitle;
        }else{
            html += gz.taxTableDetail.addTitle;
        }
        html += '</div><div style="float:right;padding-right:10px;margin-right: 10px;font-weight:normal"><a href="javascript:void(0);" onclick="taxTableDetail.saveFunc();"><strong>';
        html += gz.taxTableDetail.msg.save;//保存
        html += '</strong></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="taxTableDetail.returnFunc();"><strong>';
        html += gz.taxTableDetail.msg.back;//返回
        html += '</strong></a>&nbsp;&nbsp;</div>';
        taxTableDetail.setTitle(html);
    },
    //增加七条伪数据
    createData:function(){
        if(this.taxid == ""){
            var vo = null;
            for (var i = 1; i <= 7; i++) {
                vo = new HashMap();
                vo.put("taxid",this.taxid);
                vo.put("taxitem","");
                vo.put("series",i);
                vo.put("ynse_down",0);
                vo.put("ynse_up",0);
                vo.put("sl",0.00);
                vo.put("sskcs",0);
                vo.put("flag",'0');
                vo.put("kc_base",0);
                vo.put("description","");
                taxTableDetail.tableObj.tablePanel.store.insert(i,vo);
                var sel = taxTableDetail.tableObj.tablePanel.getSelectionModel();
                sel.select(i,true);
            }
        }
    },
    sortDetail:function(){
        var items = taxTableDetail.tableObj.dataStore.getData().items;
        for (var i = 0; i < items.length-1; i++) {
            for (var j = 0; j < items.length - i -2; j++) {
                if (items[j].data.ynse_down > items[j + 1].data.ynse_down) {
                    taxTableDetail.sortFlag = true;
                    var temp = items[j];
                    items[j] = items[j + 1];
                    items[j + 1] = temp;
                }
            }
        }
        taxTableDetail.tableObj.dataStore.setData(items);
    },
    reloadData:function () {
        var map = new HashMap();
        map.put('operateType','reloadData');
        map.put('taxid',taxTableDetail.taxid);
        Rpc({functionId: 'GZ00001008', success: function(form){
                var result = Ext.decode(form.responseText);
                var return_cod = result.return_code;
                if(return_cod == "fail"){
                    Ext.Msg.alert(gz.taxTableDetail.msg.hint,result.return_msg);
                    return;
                }
                taxTableDetail.tableObj.reloadStore();
            }, scope: this}, map);
    }
})
