//薪资审批汇总主页面
Ext.define('Salarybase.salaryspcollect.SalarySpCollect',{
    constructor:function(config){
        spCollectScope = this;
        spCollectScope.salaryid = config.salaryid;
        spCollectScope.imodule = config.imodule;//薪资和保险区分标识  1：保险  否则是薪资
        spCollectScope.appdate = config.appdate;
        spCollectScope.count = config.count;
        spCollectScope.returnflag = config.returnflag;
        spCollectScope.currentPage = config.currentPage;//定位页码
        spCollectScope.clienth = document.documentElement.clientHeight;
        spCollectScope.detailsql = "";
        spCollectScope.selectID = "";//选中的行
        spCollectScope.num = 0;//选中个数
        spCollectScope.color = "";//选中的人是否可操作，为“”则不可操作 否则可驳回批准等操作
        spCollectScope.cound = "all";//发起人
        spCollectScope.date_count = "#";//业务日期和次数的加密集合 中间用#分割  #没加密
        spCollectScope.id = "";//明细表所选择的具体汇总指标值
        spCollectScope.obj="";
        spCollectScope.personcount = "";
        spCollectScope.encryptParam = config.encryptParam;
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("bosdate",spCollectScope.appdate);
        map.put("count",spCollectScope.count);
        map.put("returnflag",spCollectScope.returnflag);
        map.put("encryptParam",spCollectScope.encryptParam);
        map.put("imodule",spCollectScope.imodule);
        Rpc({functionId:'GZ00000431',success: function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    spCollectScope.records="";
                    spCollectScope.collectPoint = result.collectPoint;
                    spCollectScope.selectcollectPoint = result.selectcollectPoint;
                    spCollectScope.isSendMessage = result.isSendMessage;
                    spCollectScope.gz_module = result.gz_module;
                    spCollectScope.codeset = result.codeset;
                    spCollectScope.subNoShowUpdateFashion = result.subNoShowUpdateFashion;
                    spCollectScope._records = result.records==null?"":spCollectScope._records;
                    spCollectScope.date_count = result.date_count;
                    spCollectScope.title = result.title;
                    spCollectScope.imodule = result.imodule;
                    spCollectScope.viewtype = result.viewtype;
                    spCollectScope.salaryid=result.salaryid_encrypt; //从链接直接进入，需写回salaryid的加密值
                    spCollectScope.verify_ctrl = result.verify_ctrl;//是否审核校验  1：是
                    spCollectScope.isTotalControl = result.isTotalControl;//是否总额控制  1：是
                    spCollectScope.ctrlType = result.ctrlType;//总额校验是否强行控制   0：不强行控制
                    spCollectScope.sp_actor_str = result.sp_actor_str;//审批关系中定义的直接领导
                    spCollectScope.setId = result.setId;//栏目设置 唯一标识 subModuleId

                    spCollectScope.detailPulicPlan=result.detailPulicPlan; //明细页面栏目设置权限 1有 0无
                    spCollectScope.mainPulicPlan=result.mainPulicPlan;//汇总页面栏目设置权限 1有 0无
                    spCollectScope.showDate=result.showDate;//刚进入加载出业务日期sunjian 2017-7-1
                    spCollectScope.spOperationDateList=result.spOperationDateList;//查找出业务日期集合 sunjian 2017-7-1
                    spCollectScope.spOperationCoundList=result.spOperationCoundList;//发起人集合sunjian 2017-7-1
                    spCollectScope.collectvalue = Ext.decode(result.collectvalue);
                    spCollectScope.columns = Ext.decode(result.columns);
                    spCollectScope.buttons = Ext.decode(result.buttons);
                    spCollectScope.bosflag =result.bosflag;//版本 hcm or hl
                    spCollectScope.commonreportlist=result.commonreportlist;//薪资常用报表数据

                    spCollectScope.init();
                }else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                    Ext.showAlert(result.message,function(){
                        var imodule=0;
                        if(spCollectScope.imodule=="Z~30DuTtqmt~33kPAATTP~33HJDPAATTP")
                            imodule=1;
                        window.location.href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=1&imodule="+imodule+"&currentPage="+spCollectScope.currentPage+"";
                    },this);
                }
            }},map);
    },
    init:function(){
        var sm = Ext.create('Ext.selection.CheckboxModel',{
            injectCheckbox:0,//checkbox位于哪一列，默认值为0
            id:'cbm',
            mode:'multi',//multi,simple,single；默认为多选multi
            checkOnly:true,//如果值为true，则只用点击checkbox列才能选中此条记录
            allowDeselect:true,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
            enableKeyNav:false,
            stopSelection: false,
            bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
            selType : 'rowmodel',  //选行模式
            listeners:{
                selectionchange: function(model,selected){//选择有改变时产生的事件
                    var records=model.getSelection();
                    spCollectScope.selectID="";
                    spCollectScope.color="";
                    spCollectScope.num=0;
                    for(var i=0;i<records.length;i++){
                        if(records[i].get('id')=='sum'){
                            //spCollectScope.selectID = '#sum';
                            continue;
                        }
                        spCollectScope.selectID+="#"+records[i].get('id');
                        spCollectScope.color+=records[i].get('color');
                        spCollectScope.num++;
                    }
                }
            }});

        var dateStore = Ext.create('Ext.data.Store',
            {
                fields:['name','id'],
                data:spCollectScope.spOperationDateList
            });

        var condStore = Ext.create('Ext.data.Store',
            {
                fields:['name','id']
            });
        condStore.loadData(spCollectScope.spOperationCoundList);

        // 创建Ext.data.TreeStore
        spCollectScope.treeStore = Ext.create('Ext.data.TreeStore', {
            proxy:{
                type: 'transaction',

                functionId:'GZ00000432',
                extraParams:{
                    salaryid:spCollectScope.salaryid,
                    collectPoint:spCollectScope.collectPoint,
                    selectcollectPoint:spCollectScope.selectcollectPoint,
                    codeset:spCollectScope.codeset,
                    date_count:spCollectScope.date_count,
                    zjjt:'1',
                    record:spCollectScope._records,
                    inCount:'1'//调用该方法的次数，只要这块是第一次，其他地方都不是，为了判断第一次进来树节点的展现与否  zhaoxg add 2016-10-11

                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            // 定义该TreeStore所包含的字段
            fields: spCollectScope.collectvalue
        });
        var tree = Ext.create('Ext.tree.Panel', {
            // 不使用Vista风格的箭头代表节点的展开/折叠状态
            useArrows: true,
            id:'tree',
            store: spCollectScope.treeStore, // 指定该树所使用的TreeStore
            rootVisible: false, // 指定根节点可见
            selModel:sm,
            columnLines:true,
            stripeRows:true,
            rowLines:true,
            enableColumnMove:false,
            bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
            sortableColumns:false,
            // 指定columns选项
            columns:spCollectScope.columns
        });
        Ext.create('Ext.container.Viewport',{
//			autoScroll:false,
            style:'backgroundColor:white',
            id:'spviewport',
            layout:'fit',
            items:[{
                xtype:'panel',
                title:spCollectScope.title+"<a id='schemeSetting'  href='javascript:void(0)'  onclick='spCollectScope.schemeSetting()' style='position:absolute;right:0px;top:5px'><img src='/components/tableFactory/tableGrid-theme/images/Settings.png'  title='栏目设置'/></a>",
                border:false,
                bodyBorder:false,
                autoScroll:true,
                layout:'fit',
                tbar:spCollectScope.buttons.concat([
                    {id:'date_combobox',xtype:'combobox',fieldLabel:'业务日期',store:dateStore,displayField:'name',valueField:'id',queryMode:'local',labelAlign:'right',editable:false,value:spCollectScope.showDate,
                        listeners:{
                            select:function(combo,ecords){
                                spCollectScope.date_count = combo.value;
                                Ext.getCmp('cond_combobox').reset();
                                //condStore.getProxy().extraParams.date_count=combo.value;
                                var map = new HashMap();
                                map.put("salaryid",spCollectScope.salaryid);
                                map.put("date_count",combo.value);
                                map.put("opt","count");
                                Rpc({functionId:'GZ00000433',success:function(form,action){//因为在选择业务日期的时候要联动发起人过滤
                                        var result = Ext.decode(form.responseText);
                                        var flag=result.succeed;
                                        if(flag){
                                            condStore.loadData(result.data);
                                            if(condStore.getById(spCollectScope.cound)==undefined||condStore.getById(spCollectScope.cound)=='')
                                                spCollectScope.cound = "";
                                            else
                                                Ext.getCmp('cond_combobox').setValue(spCollectScope.cound);
                                            if(condStore.getAt(0)!=null){
                                                spCollectScope.treeStore.setProxy({
                                                    type: 'transaction',
                                                    functionId:'GZ00000432',
                                                    extraParams:{
                                                        salaryid:spCollectScope.salaryid,
                                                        date_count:combo.value,
                                                        collectPoint:spCollectScope.collectPoint,
                                                        selectcollectPoint:spCollectScope.selectcollectPoint,
                                                        codeset:spCollectScope.codeset,
                                                        cound:spCollectScope.cound,
                                                        inCount:'2'//调用该方法的次数，为了判断第一次进来树节点的展现与否  zhaoxg add 2016-10-11

                                                    },
                                                    reader: {
                                                        type: 'json',
                                                        root: 'data'
                                                    }
                                                });
                                                spCollectScope.reload();
                                            }
                                        }
                                    }},map);
                            }
                        }
                    },
                    {id:'cond_combobox',xtype:'combobox',fieldLabel:'发起人过滤',store:condStore,displayField:'name',valueField:'id',queryMode:'local',labelWidth:65,labelAlign:'right',editable:false,value:"全部",
                        listeners:{
                            select:function(combo,ecords){
                                spCollectScope.cound = combo.value;
                                dateStore.load({
                                    callback: function(record, option, succes){
                                        if(condStore.getAt(0)!=null){
                                            spCollectScope.treeStore.setProxy({
                                                type: 'transaction',

                                                functionId:'GZ00000432',
                                                extraParams:{
                                                    salaryid:spCollectScope.salaryid,
                                                    date_count:spCollectScope.date_count,
                                                    cound:combo.value,
                                                    collectPoint:spCollectScope.collectPoint,
                                                    selectcollectPoint:spCollectScope.selectcollectPoint,
                                                    codeset:spCollectScope.codeset,
                                                    inCount:'2'//调用该方法的次数，为了判断第一次进来树节点的展现与否  zhaoxg add 2016-10-11
                                                },
                                                reader: {
                                                    type: 'json',
                                                    root: 'data'
                                                }
                                            });
                                            spCollectScope.reload();
                                        }
                                    }
                                });
                            }
                        }
                    }
                ]),
                items:[tree]
            }]
        });
    },
    renderTree:function(value, metaData, Record){
        var id = Record.data.id;
        var desc = Record.data.desc==null?"":Record.data.desc;
        var img = "";
        if(Record.data.color.length>0){
            img = "<img width='13' height='13'  src='/images/new_module/"+Record.data.color+"'  align='absmiddle'/>";
        }
        return "<a title='"+desc+"' href=javascript:spCollectScope.checkSpDetail('"+getEncodeStr(trim(value)+"的数据明细")+"','"+id+"');>"+value+"&nbsp;&nbsp"+img+"</a>";
    },
    checkSpDetail:function(obj,id){
        obj=getDecodeStr(obj);
        if(spCollectScope.date_count){
            spCollectScope.title = obj+" ("+Ext.getCmp('date_combobox').getRawValue()+")";
        }else{
            spCollectScope.title = obj;
        }
        spCollectScope.obj = obj;
        spCollectScope.id = id;//点击具体节点的时候才赋值
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("appdate",spCollectScope.date_count.split("#")[0]);
        map.put("count",spCollectScope.date_count.split("#")[1]);
        map.put("imodule",spCollectScope.imodule);
        map.put("viewtype",spCollectScope.viewtype);
        map.put("collectPoint",spCollectScope.collectPoint);
        map.put("cound",spCollectScope.cound);
        map.put("id",id);
        Rpc({functionId:'GZ00000434',async:false,success:spCollectScope.getTableOK},map);
    },
    getTableOK:function(form,action){
        var result = Ext.decode(form.responseText);
        var conditions=result.tableConfig;
        spCollectScope.detailSetId=result.detailSetId;//明细页面栏目设置 唯一标识
        spCollectScope.detailsql = result.detailsql;
        spCollectScope.username=result.username;
        spCollectScope.allowEditSubdata=result.allowEditSubdata; //允许提交后更改数据
        spCollectScope.hasSubPirv=result.hasSubPirv; //是否有提交结束数据的权限
        var obj = Ext.decode(conditions);
        obj.openColumnQuery = true;
        spCollectScope.haveWin = false;
        if(!Ext.isEmpty(spCollectScope.win)){
        	spCollectScope.win.destroy();
        	if(spCollectScope.salaryObj)
        		spCollectScope.salaryObj.getMainPanel().destroy();
            spCollectScope.haveWin = true;
        }
        Ext.util.CSS.createStyleSheet(".x-ssm-extender-drag-handle{display:none}","spreadsheet_extender");//消除选框拖拽点
        Ext.util.CSS.createStyleSheet(".x-grid-cell-inner-row-numberer{    background-image: none !important;background-color:transparent !important}","spreadsheet_extender");//消除选框拖拽点


        spCollectScope.treeMenu = Ext.create('Ext.menu.Menu', {
            margin: '0 0 10 0',
            floating: true,
            id:'menuBar',
            renderTo: Ext.getBody(),
            items: [{
                id:'copyitem',
                text: "复制&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+C",
                icon:'/images/new_module/shift_copy.png',
                handler:function () {
                    spCollectScope.getCells('raw',false);
                }
            },{
                id:'cutitem',
                text: "剪切&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+X",
                icon:'/images/new_module/shift_cut.png',
                handler:function () {
                    spCollectScope.getCells('raw',true);
                }
            },{
                id:'pasteitem',
                text: "粘贴&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+V",
                icon:'/images/new_module/shift_paste.png',
                handler:function () {
                    spCollectScope.putCellData();
                }
            }]
        });
        obj.beforeBuildComp = function(grid){
            grid.storeConfig.listeners={
                'beforeload':function () {
                    if(Ext.getCmp("salaryspdetail_tablePanel")!=undefined) {
                        Ext.getCmp("salaryspdetail_tablePanel").getSelectionModel().deselectAll();
                    }
                }
            }
            // 序号列名
            grid.tableConfig.columns.unshift({xtype:'rownumberer',text:gz.label.rowNumberer,width:50,style:'padding-left:4px;'});
            grid.tableConfig.selModel = {
                type: 'spreadsheet',
                checkboxSelect:true,
                columnSelect: true,
                rowNumbererHeaderWidth:45,
                pruneRemoved:false
            };

            grid.tableConfig.plugins.push({ptype:'clipboard',
                system:'cell',
                formats: {
                    cell: {
                        get: 'getCells',
                        put: 'putCellData'
                    },
                    text:{
                        get: 'getTextData'
                    }
                },
                //复写复制的方法
                getCells:function(format,erase){
                    spCollectScope.getCells(format,erase);
                },
                //复写粘贴的方法
                putCellData: function (data, format) {
                    spCollectScope.putCellData(data, format);
                },
                getTextData: function (format, erase) {
                    return this.getCells(format, erase);
                }
            });
            grid.tableConfig.listeners= {
                //表格添加鼠标右键事件
                cellcontextmenu: function (view, td, cellIndex, record, tr, rowIndex, e) {
                    //禁用浏览器的右键相应事件
                    e.preventDefault();
                    e.stopEvent();
                    // var selModel = view.grid.getSelectionModel();
                    //右键前没有选中的话禁用右键菜单
                    // if (!selModel.getSelected()) {
                    //     accounting.disabledMenuItem('all', true);
                    // } else {
                    spCollectScope.disabledMenuItem('all', false);
                    //剪切板没有值的话，禁用粘贴菜单
                    if (!spCollectScope.cellData) {
                        spCollectScope.disabledMenuItem('pasteitem', true);
                    } else {
                        spCollectScope.disabledMenuItem('pasteitem', false);
                    }
                    // }
                    spCollectScope.treeMenu.showAt(e.getXY());
                }
            }
        };
        //取消表格控件右键代码型指标统计功能
        obj.contextAnalyse=false;
        spCollectScope.salaryObj = new BuildTableObj(obj);
        var vs = Ext.getBody().getViewSize();
        spCollectScope.win=Ext.widget("window",{
            id:'spwin',
            height:spCollectScope.clienth,
            width:vs.width-310,
            x:310,
            minButtonWidth:40,
            title:spCollectScope.title+"<a id='schemeSetting'  href='javascript:void(0)'  onclick='spCollectScope.DetailSetting()' style='position:absolute;right:2px;top:3px'><img src='/components/tableFactory/tableGrid-theme/images/Settings.png'  title='栏目设置'/></a>",
            layout:'fit',
            bodyStyle: 'background:#ffffff;',
            modal:false,
            resizable:false,
            closeAction:'destroy',
            listeners:{
                'beforeclose':function(){
                    spCollectScope.haveWin = false;
                    spCollectScope.reload();
                },
                "close":function() {
                	spCollectScope.salaryObj.getMainPanel().destroy();
                	spCollectScope.salaryObj = undefined;
                }
            },
            items: [spCollectScope.salaryObj.getMainPanel()]
        });

        if(Ext.getCmp("spcompute") != undefined)
            Ext.getCmp("spcompute").disable();
        if(Ext.getCmp("spcheck") != undefined)
            Ext.getCmp("spcheck").disable();
        if(Ext.getCmp("spappeal") != undefined)
            Ext.getCmp("spappeal").disable();
        if(Ext.getCmp("spreject") != undefined)
            Ext.getCmp("spreject").disable();
        if(Ext.getCmp("spapprove") != undefined)
            Ext.getCmp("spapprove").disable();
        if(Ext.getCmp("spconfirm") != undefined)
            Ext.getCmp("spconfirm").disable();
        spCollectScope.win.show();
        if(!spCollectScope.haveWin)
            Ext.get('spwin').slideIn('r', { duration: 1000,easing: 'easeOut' });

        /**	    var fieldsArray = result.fieldsArray;
         var lookStr = result.lookStr;
         var map = new HashMap();
         map.put("salaryid",spCollectScope.salaryid);
         map.put("appdate",spCollectScope.date_count.split("#")[0]);
         map.put("count",spCollectScope.date_count.split("#")[1]);
         map.put("imodule",spCollectScope.imodule);
         map.put("viewtype",spCollectScope.viewtype);
         spCollectScope.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			emptyText:lookStr,
			subModuleId:"salaryspdetail",
			customParams:map,
			funcId:"GZ00000434",
			fieldsArray:fieldsArray,
			success:spCollectScope.loadStore
		});
         var toolBar = Ext.getCmp("salaryspdetail_toolbar");
         toolBar.add(spCollectScope.SearchBox);*/
        var params = new Object();
        params.salaryid=spCollectScope.salaryid;
        params.appdate=spCollectScope.date_count.split("#")[0];
        params.count=spCollectScope.date_count.split("#")[1];
        params.imodule=spCollectScope.imodule;
        params.viewtype=spCollectScope.viewtype;
        params.subModuleId="salaryspdetail";
        Ext.getCmp("salaryspdetail_querybox").setCustomParams(params);
    },
    closeDetailSettingWindow:function(){
        Ext.getCmp('spwin').close();
        spCollectScope.checkSpDetail(spCollectScope.obj,spCollectScope.id);
    },
    closeDetailWindow:function(){
        if(spCollectScope.win){//关闭明细页面 zhanghua 2017-6-26
            spCollectScope.win.close();
            spCollectScope.win.destroy();
        }
    },
    
    isHaveChange:function(){//判断数据是否存在变化
    	if(!spCollectScope.salaryObj) {
    		return false;
    	}
		var store=spCollectScope.salaryObj.tablePanel.getStore();
		var list=store.getModifiedRecords();
		if(list.length>0)
			return true;
		else
			return false;

	},
	
    getCells: function (format, erase) {
        var me=this;
        var cmp = Ext.getCmp("salaryspdetail_tablePanel"),
            selModel = cmp.getSelectionModel(),
            ret = [],
            dataIndex, lastRecord, record, row;

        if(selModel!=undefined&&selModel.getSelected()!=undefined) {
            selModel.getSelected().eachCell(function (cellContext) {
                record = cellContext.record;
                if (erase) {
                    if(cellContext.column.dataIndex=='a00z1'||cellContext.column.dataIndex=='a00z0'||cellContext.column.dataIndex=='a01z0'){
                        return false;
                    }
                    if (cellContext.column.getEditor() == undefined) {
                        return false;
                    }
                    if (!spCollectScope.clickCell(record)) {
                        return false;
                    }
                }
                if (lastRecord !== record) {
                    lastRecord = record;
                    ret.push(row = {
                        model: record.self,
                        fields: []
                    });
                }
                dataIndex = cellContext.column.dataIndex;
                row.fields.push({
                    name: dataIndex,
                    value: record.data[dataIndex],
                    codesetid: cellContext.column.codesetid,
                    columnType: cellContext.column.columnType
                });

                if (erase && dataIndex) {
                    record.set(dataIndex, null);
                }
            });
            Ext.getCmp("salaryspdetail_tablePanel").plugins[0].cancelEdit();
            me.cellData = Ext.encode(ret);
            return ret;
        }else{
            return false;
        }
    },
    putCellData:function () {
        var me = this;
        if (Ext.isEmpty(me.cellData))
            return;
        var data = me.cellData;
        var values = Ext.decode(data),
            row,
            recCount = values.length,
            colCount = recCount ? values[0].fields.length : 0,
            sourceRowIdx, sourceColIdx,
            view = Ext.getCmp("salaryspdetail_tablePanel").getView(),
            maxRowIdx = view.dataSource.getCount() - 1,
            maxColIdx = view.getVisibleColumnManager().getColumns().length - 1,
            destination,
            dataIndex, destinationStartColumn,
            dataObject = {};

        var firstColIndex = 0;
        var firstRowIndex = 0;
        var endColIndex = 0;
        var endRowIndex = 0;
        var eachFlag = false;
        view.getSelectionModel().getSelected().eachCell(function(c,cIdx,rIdx){
            if (!eachFlag) {
                firstColIndex = cIdx;
                firstRowIndex = rIdx;
                eachFlag = true;
            }
            endColIndex = cIdx;
            endRowIndex = rIdx;
        });
        var colCount_ = endColIndex-firstColIndex+1;//目标区域列数
        var recCount_ = endRowIndex-firstRowIndex+1;//目标区域行数

        //如果目标区域是源的整数倍，复制单元格内容时，重复填充
        if(colCount_*recCount_>0 && recCount*colCount>0 && colCount_*recCount_%(recCount*colCount)==0){
            view.getSelectionModel().getSelected().eachCell(function(context,ci_,ri_){
                ci_ = ci_-firstColIndex;
                ri_ = ri_-firstRowIndex;

                //复制的格子
                var copyField = values[ri_%recCount].fields[ci_%colCount];
                dataIndex = context.column.dataIndex;
                //editor为空不可编辑
                var editor=context.column.getEditor();
                if (editor==undefined) {
                    return false;
                }
                if(!spCollectScope.clickCell(context.record)){
                    return false;
                }
                var codesetid = context.column.codesetid;
                var columnType = context.column.columnType;
                //同类的才可以复制
                if (copyField.columnType != columnType || copyField.columnType == "A" && copyField.codesetid != codesetid) {
                    return false;
                }

                if (dataIndex) {
                    context.record.set(dataIndex,copyField.value);
                }
            });
        }else{
            if (!destination) {
                view.getSelectionModel().getSelected().eachCell(function (c, cIdx, rIdx) {
                    destination = c;
                    return false;
                });
            }

            if (destination) {
                destination = new Ext.grid.CellContext(view).setPosition(destination.record, destination.column);
            } else {
                destination = new Ext.grid.CellContext(view).setPosition(0, 0);
            }

            destinationStartColumn = destination.colIdx;
            for (sourceRowIdx = 0; sourceRowIdx < recCount; sourceRowIdx++) {
                row = values[sourceRowIdx].fields;
                for (sourceColIdx = 0; sourceColIdx < colCount; sourceColIdx++) {
                    var copyField = row[sourceColIdx];
                    dataIndex = destination.column.dataIndex;
                    var editor=destination.column.getEditor();
                    if (editor==undefined) {
                        return false;
                    }
                    if(!spCollectScope.clickCell(destination.record)){
                        return false;
                    }
                    var codesetid = destination.column.codesetid;
                    var columnType = destination.column.columnType;

                    if (copyField.columnType != columnType || copyField.columnType == "A" && copyField.codesetid != codesetid) {
                        return false;
                    }
                    if (dataIndex) {
                        dataObject[dataIndex] = copyField.value;
                    }
                    if (destination.colIdx === maxColIdx) {
                        break;
                    }
                    destination.setColumn(destination.colIdx + 1);
                    var obj = {};
                    obj[dataIndex] = copyField.value;
                }
                destination.record.set(dataObject);
                if (destination.rowIdx === maxRowIdx) {
                    break;
                }
                destination.setPosition(destination.rowIdx + 1, destinationStartColumn);
            }
        }
        Ext.getCmp("salaryspdetail_tablePanel").plugins[0].cancelEdit();
    },
    /**
     * item =all 禁用所有按钮
     * @param item
     */
    disabledMenuItem:function (item,disabled) {
        if (item == "all") {
            Ext.getCmp("menuBar").setDisabled(disabled);
        }else{
            Ext.getCmp(item).setDisabled(disabled);
        }
    },
    //栏目设置按钮事件 明细页面栏目设置
    DetailSetting:function(){
        Ext.require("EHR.tableFactory.plugins.SchemeSetting", function() {
            var window = new EHR.tableFactory.plugins.SchemeSetting({
                subModuleId : spCollectScope.detailSetId,
                schemeItemKey:'',
                itemKeyFunctionId:'',
                viewConfig:{
                    publicPlan:spCollectScope.detailPulicPlan=='1'?true:false,
                    sum:true,
                    lock:true,
                    merge:false,
                    pageSize:'20'
                },
                closeAction:spCollectScope.closeDetailSettingWindow
            });
        });
    },
    compute:function(a){//计算
    	var hasChange = spCollectScope.isHaveChange();
    	if(hasChange) {
    		Ext.showAlert(gz.label.hasChange);
    		return;
    	}
        var date=Ext.getCmp('date_combobox').getValue();
        if(date==''||date==null)
        {
            Ext.Msg.alert('提示信息', "请选择业务日期！");
            return;
        }
        var obj = new Object();
        obj.salaryid = spCollectScope.salaryid;
        obj.appdate = spCollectScope.date_count.split("#")[0];
        obj.count = spCollectScope.date_count.split("#")[1];
        obj.imodule = spCollectScope.imodule;
        obj.viewtype = spCollectScope.viewtype;
        obj.collectPoint = spCollectScope.collectPoint;
        obj.selectID = spCollectScope.selectID.substring(1);
        if(a!=0)//a=0的时候是审批界面。不等于0是明细界面
            obj.detailsql = spCollectScope.detailsql;
        else//只有在不是明细页面的时候关闭明旭页面
            spCollectScope.closeDetailWindow();//关闭明细页面
        Ext.require('SalaryUL.Compute',function(){
            Ext.create("SalaryUL.Compute", obj);
        })
    },
    verify:function(){//审核
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("appdate",spCollectScope.date_count.split("#")[0]);
        map.put("count",spCollectScope.date_count.split("#")[1]);
        map.put("imodule",spCollectScope.imodule);
        map.put("viewtype",spCollectScope.viewtype);
        map.put("selectID",spCollectScope.selectID.substring(1));
        map.put("collectPoint",spCollectScope.collectPoint);
        map.put("cound",spCollectScope.cound);
        spCollectScope.closeDetailWindow();//关闭明细页面
        Ext.MessageBox.wait("正在审核，请稍候...", "等待");
        Rpc({functionId:'GZ00000016',success:function(form,action){
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                var flag=result.succeed;
                if(flag==true){
                    if(result.msg=='yes'){
                        Ext.showAlert('审核通过！');
                    }else if(result.fileName.length>0){
                    	var fieldName = getDecodeStr(result.fileName);
                        Ext.showAlert('审核不通过！');
                        window.location.target="_blank";
                        window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
                    }else{
                        Ext.showAlert('审核不通过！');
                    }
                }else{
                    Ext.showAlert(result.message);
                }

            }},map);
    },
    //薪资审批报批
    appeal:function(){
        var date=Ext.getCmp('date_combobox').getValue();
        if(date==''||date==null)
        {
            Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
            return;
        }
        if(spCollectScope.num==0){
            Ext.Msg.alert(common.button.promptmessage, "没有选择可报批的记录！");
            return;
        }
        if(spCollectScope.color==''){
            Ext.Msg.alert(common.button.promptmessage, "没有可操作的数据！");
            return;
        }
//		if(spCollectScope.selectID=='#sum'){
//			Ext.Msg.confirm("确认","选择合计，则报批全部",
//				function(id){
//					if(id=='no'){
//						return;
//					}
//				}
//			);
//			return;
//		}
        spCollectScope.closeDetailWindow();//关闭明细页面
        var str="";
        if(spCollectScope.isTotalControl=='1')
            str+="、总额校验";
        if(spCollectScope.verify_ctrl=='1'){
            str+="、数据审核";
        }else{//如果没设置审核那么直接去执行总额校验
            str = str.substring(1)==""?"报批":str.substring(1);
            Ext.MessageBox.wait("正在"+str+"，请稍候...", "等待");
            spCollectScope.totalControl("1");
            return;
        }
        str = str.substring(1)==""?"报批":str.substring(1);
        Ext.MessageBox.wait("正在"+str+"，请稍候...", "等待");
        spCollectScope.verify1("1");
    },
    verify1:function(opt){//审核（报批、 审批确认和批准调用） opt：1 报批 2 批准 3 审批确认
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("appdate",spCollectScope.date_count.split("#")[0]);
        map.put("count",spCollectScope.date_count.split("#")[1]);
        map.put("imodule",spCollectScope.imodule);
        map.put("viewtype",spCollectScope.viewtype);
        map.put("selectID",spCollectScope.selectID.substring(1));
        map.put("collectPoint",spCollectScope.collectPoint);
        map.put("cound",spCollectScope.cound);
        Rpc({functionId:'GZ00000016',success:function(form,action){
                var result = Ext.decode(form.responseText);
                var flag=result.succeed;
                if(flag==true){
                    if(result.msg=='yes'){
                        spCollectScope.totalControl(opt);
                    }else if(result.fileName.length>0){
                    	var fieldName = getDecodeStr(result.fileName);
                        Ext.MessageBox.close();
                        Ext.showAlert('审核不通过！');
                        window.location.target="_blank";
                        window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
                    }else{
                        Ext.MessageBox.close();
                        Ext.showAlert('审核不通过！');
                    }
                }else{
                    Ext.MessageBox.close();
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    //总额校验
    totalControl:function(opt){//总额校验（报批、审批确认和批准调用） opt：1 报批 2 批准 3 审批确认
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("viewtype",spCollectScope.viewtype);
        map.put("appdate",spCollectScope.date_count.split("#")[0]);
        map.put("count",spCollectScope.date_count.split("#")[1]);
        map.put("selectID",spCollectScope.selectID.substring(1));
        map.put("collectPoint",spCollectScope.collectPoint);
        map.put("cound",spCollectScope.cound);
        Rpc({functionId:'GZ00000018',success:function(form,action){
                var result = Ext.decode(form.responseText);
                var flag=result.succeed;
                if(flag==true){
                    if(result.info=='success'){
                        Ext.MessageBox.close();
                        if(opt=="1"){
                            spCollectScope.appeal0();//实际报批操作
                        }else if(opt=="2"){
                            spCollectScope.gzSpConfirm1();//实际批准操作
                        }else if(opt=="3"){
                            spCollectScope.submit();//实际的审批确认操作
                        }
                    }else{
                        Ext.MessageBox.close();
                        if(spCollectScope.ctrlType=="0"){//总额校验不强行控制
                            Ext.Msg.confirm(common.button.promptmessage,result.info+"&nbsp",
                                function(id){
                                    if(id=='yes'){
                                        if(opt=="1"){
                                            spCollectScope.appeal0();//实际报批操作
                                        }else if(opt=="2"){
                                            spCollectScope.gzSpConfirm1();//实际批准操作
                                        }
                                    }else{
                                        return;
                                    }
                                }
                            );
                        }else{
                            Ext.showAlert(result.info);
                        }
                    }
                }else{
                    Ext.MessageBox.close();
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    //报批选人操作
    appeal0:function(){
        if(spCollectScope.sp_actor_str.length>0)
        {
            var temps=getDecodeStr(spCollectScope.sp_actor_str).split("`");
            if(temps.length==1)
            {
                Ext.Msg.confirm(common.button.promptmessage,"确认报批吗？",
                    function(id){
                        if(id=='yes'){
                            spCollectScope.appeal1(temps[0].split("##")[3]);
                        }else{
                            return;
                        }
                    }
                );
            }
            else if(temps.length>1)
            {
                var colums = new Array();
                for(var i=0;i<temps.length;i++)
                {
                    var _temps=temps[i].split("##");
                    var obj = new Object();
                    obj.boxLabel=_temps[0]+"/"+_temps[1]+"/"+_temps[2];
                    obj.name='rb';
                    obj.inputValue=_temps[3];
                    colums[i]=obj;
                }
                var win=Ext.widget("window",{
                    title:'请选择报批人',
                    height:300,
                    width:500,
                    layout:'fit',
                    modal:true,
                    closeAction:'destroy',
                    items: [{
                        xtype:'panel',
                        border:false,
                        width:485,
                        height:220,
                        items:[{
                            xtype:'radiogroup',
                            columns:2,//两列
                            vertical:true,
                            id:'state',
                            width:485,
                            items:colums
                        }],
                        buttons:[
                            {xtype:'tbfill'},
                            {
                                text:common.button.ok,
                                handler:function(){
                                    Ext.Msg.confirm(common.button.promptmessage,"确认报批吗？",
                                        function(id){
                                            if(id=='yes'){
                                                var appealObject = Ext.getCmp('state').getValue();
                                                win.close();
                                                spCollectScope.appeal1(appealObject.rb);//单选框组的获取是根据自定义的name字段来取得哪个选中了
                                            }else{
                                                return;
                                            }
                                        }
                                    );
                                }
                            },
                            {
                                text:common.button.cancel,
                                handler:function(){
                                    win.close();
                                }
                            },
                            {xtype:'tbfill'}
                        ]
                    }]
                });
                win.show();
            }
        }else{
            var f = document.getElementById("spappeal");
            var p = new PersonPicker({
                multiple: false,
                isSelfUser:false,//是否选择自助用户
                isMiddle:true,//是否居中显示
                extend_str:"salary/"+spCollectScope.salaryid,//薪资选人控件个性化标注，用于控件中薪资权限的控制
                callback: function (c) {
                    var appealObject=c.id;
                    Ext.Msg.confirm(common.button.promptmessage,"确认报批吗？",
                        function(id){
                            if(id=='yes'){
                                spCollectScope.appeal1(appealObject);
                            }else{
                                return;
                            }
                        }
                    );
                }
            }, f);
            p.open();
        }
    },
    //报批操作
    appeal1:function(appealObject){
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("appdate",spCollectScope.date_count.split("#")[0]);
        map.put("count",spCollectScope.date_count.split("#")[1]);
        map.put("appealObject",appealObject);
        map.put("selectID",spCollectScope.selectID.substring(1));
        map.put("cound",spCollectScope.cound);
        map.put("collectPoint",spCollectScope.collectPoint);
        map.put("fromPending",spCollectScope.returnflag=='menu'?'0':'1');
        map.put("opt","appeal");
        Ext.MessageBox.wait("正在报批，请稍候...", "等待");
        Rpc({functionId:'GZ00000435',success:function(form,action){
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                var flag=result.succeed;
                if(flag){
                    //剩余可批条数
                    var listNumber=result.lastNumber;
                    //从待办进来，且剩余可批条数为0，那么跳回待办页面
                    if(listNumber==0&&spCollectScope.returnflag!='menu'){
                        spCollectScope.back();
                    }else{
                        spCollectScope.reload();
                    }
                }else{
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    //驳回
    gzSpReject:function(){
        var date=Ext.getCmp('date_combobox').getValue();
        if(date==''||date==null)
        {
            Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
            return;
        }
        if(spCollectScope.num==0){
            Ext.Msg.alert(common.button.promptmessage, "请选择需要驳回的记录！");
            return;
        }
        if(spCollectScope.color==''){
            Ext.Msg.alert(common.button.promptmessage, "没有可操作的数据！");
            return;
        }
        spCollectScope.closeDetailWindow();//关闭明细页面
        var win=Ext.widget("window",{
            title:'驳回原因',
            height:300,
            width:500,
            layout:'fit',
            modal:true,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                border:false,
                items:[{
                    border:false,
                    xtype:'textareafield',
                    id:'rejectCause',
                    width:485,
                    height:220
                }],
                buttons:[
                    {xtype:'tbfill'},
                    {
                        text:common.button.ok,
                        handler:function(){
                            if(Ext.getCmp("rejectCause").getValue().length==0){
                                Ext.showAlert("请填写驳回原因！");
                                return;
                            }

                            Ext.Msg.confirm(common.button.promptmessage,"确认驳回选择的数据吗？",
                                function(id){
                                    if(id=='yes'){
                                        var map = new HashMap();
                                        map.put("salaryid",spCollectScope.salaryid);
                                        map.put("appdate",spCollectScope.date_count.split("#")[0]);
                                        map.put("count",spCollectScope.date_count.split("#")[1]);
                                        map.put("rejectCause",Ext.getCmp("rejectCause").getValue());
                                        map.put("selectID",spCollectScope.selectID.substring(1));
                                        map.put("cound",spCollectScope.cound);
                                        map.put("collectPoint",spCollectScope.collectPoint);
                                        map.put("fromPending",spCollectScope.returnflag=='menu'?'0':'1');
                                        map.put("opt","reject");
                                        win.close();
                                        Ext.MessageBox.wait("正在驳回，请稍候...", "等待");
                                        Rpc({functionId:'GZ00000435',success:function(form,action){
                                                Ext.MessageBox.close();
                                                var result = Ext.decode(form.responseText);
                                                var flag=result.succeed;
                                                if(flag){
                                                    //剩余可批条数
                                                    var listNumber=result.lastNumber;

                                                    //从待办进来，且剩余可批条数为0，那么跳回待办页面
                                                    if(listNumber==0&&spCollectScope.returnflag!='menu'){
                                                        spCollectScope.back();
                                                    }else {
                                                        if (result.count == 0) {
                                                            var temp = Ext.getCmp('date_combobox').getStore();
                                                            for (var i = 0; i < temp.getCount(); i++) {  //getCount() 方法 获取 数据集 的长度
                                                                if (Ext.getCmp('date_combobox').getRawValue() == temp.getAt(i).get('name')) {
                                                                    temp.remove(temp.getAt(i));
                                                                }
                                                            }
                                                            Ext.getCmp('date_combobox').setValue("");
                                                            Ext.getCmp('cond_combobox').setValue("");
                                                            Ext.getCmp('cond_combobox').getStore().removeAll();
                                                        }
                                                        spCollectScope.reload();
                                                    }
                                                }else{
                                                    Ext.showAlert(result.message);
                                                }
                                            }},map);
                                    }else{
                                        return;
                                    }
                                }
                            );
                        }
                    },
                    {
                        text:common.button.cancel,
                        handler:function(){
                            win.close();
                        }
                    },
                    {xtype:'tbfill'}
                ]
            }]
        });
        win.show();
    },
    //明细驳回（个别驳回）
    reject:function(){
        var selectData = spCollectScope.salaryObj.tablePanel.getSelectionModel().getSelection(true);
        if(selectData.length==0){
            Ext.showAlert('请选择需要驳回的记录！');
            return
        }
        if(spCollectScope.isHaveChange()) {
        	Ext.showAlert(gz.label.hasChange);
            return
        }
        if(spCollectScope.isHaveChange()) {
        	Ext.showAlert(gz.label.hasChange);
            return
        }
        var isCanCtrl = false;
        var selectGzRecords = "";
        for(var j=0;j<selectData.length;j++){
            selectGzRecords += selectData[j].data.a0100_e+"/";
            selectGzRecords += selectData[j].data.nbase1_e+"/";
            selectGzRecords += selectData[j].data.a00z0+"/";
            selectGzRecords += selectData[j].data.a00z1;
            selectGzRecords += "#";
            if(selectData[j].data.sp_flag.split("`")[0]=='02'||selectData[j].data.sp_flag.split("`")[0]=='07'){
                isCanCtrl = true;
            }
        }
        if(isCanCtrl==false){
            Ext.Msg.alert(common.button.promptmessage, "请选择可以驳回的数据！");
            return;
        }
        var doSelectAll = spCollectScope.salaryObj.tablePanel.getSelectionModel().doSelectAll;

        var win=Ext.widget("window",{
            title:'驳回原因',
            height:300,
            width:500,
            layout:'fit',
            modal:true,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                border:false,
                items:[{
                    border:false,
                    xtype:'textareafield',
                    id:'rejectCause',
                    width:485,
                    height:220
                }],
                buttons:[
                    {xtype:'tbfill'},
                    {
                        text:common.button.ok,
                        handler:function(){
                            if(Ext.getCmp("rejectCause").getValue().length==0){//明细添加未填写驳回原因提示
                                Ext.showAlert("请填写驳回原因！");
                                return;
                            }

                            Ext.Msg.confirm(common.button.promptmessage,"确认驳回选择的数据吗？",
                                function(id){
                                    if(id=='yes'){
                                        var map = new HashMap();
                                        map.put("salaryid",spCollectScope.salaryid);
                                        map.put("appdate",spCollectScope.date_count.split("#")[0]);
                                        map.put("count",spCollectScope.date_count.split("#")[1]);
                                        map.put("rejectCause",Ext.getCmp("rejectCause").getValue());
                                        map.put("selectID",spCollectScope.id);
                                        map.put("selectGzRecords",selectGzRecords);
                                        map.put("doSelectAll",doSelectAll);
                                        map.put("cound",spCollectScope.cound);
                                        map.put("collectPoint",spCollectScope.collectPoint);
                                        win.close();
                                        Ext.MessageBox.wait("正在驳回，请稍候...", "等待");
                                        Rpc({functionId:'GZ00000438',success:function(form,action){
                                                Ext.MessageBox.close();
                                                var result = Ext.decode(form.responseText);
                                                var flag=result.succeed;
                                                if(flag){
                                                    spCollectScope.loadStore();
                                                }else{
                                                    Ext.showAlert(result.message);
                                                }
                                            }},map);
                                    }else{
                                        return;
                                    }
                                }
                            );
                        }
                    },
                    {
                        text:common.button.cancel,
                        handler:function(){
                            win.close();
                        }
                    },
                    {xtype:'tbfill'}
                ]
            }]
        });
        win.show();
    },
    //批准
    gzSpConfirm:function(){
        var date=Ext.getCmp('date_combobox').getValue();
        if(date==''||date==null)
        {
            Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
            return;
        }
        if(spCollectScope.num==0){
            Ext.Msg.alert(common.button.promptmessage, "请选择需要批准的记录！");
            return;
        }
        if(spCollectScope.color==''){
            Ext.Msg.alert(common.button.promptmessage, "没有可操作的数据！");
            return;
        }
        spCollectScope.closeDetailWindow();//关闭明细页面
        var str="";
        if(spCollectScope.isTotalControl=='1')
            str+="、总额校验";
        if(spCollectScope.verify_ctrl=='1'){
            str+="、数据审核";
        }else{//如果没设置审核那么直接去执行总额校验
            str = str==""?"、批准":str;
            Ext.MessageBox.wait("正在"+str.substring(1)+"，请稍候...", "等待");
            spCollectScope.totalControl("2");
            return;
        }
        str = str==""?"、批准":str;
        Ext.MessageBox.wait("正在"+str.substring(1)+"，请稍候...", "等待");
        spCollectScope.verify1("2");
    },
    //批准实际操作
    gzSpConfirm1:function(){
        spCollectScope.sendMen="";//批准后抄送人 设置了通知才生效
        spCollectScope.x='';
        spCollectScope.y='';//坐标
        spCollectScope.reportObjectId='';//通知增加的id
        spCollectScope.reportObjectName='';//通知增加的人名
        var confirmPanel = "";
        if(spCollectScope.isSendMessage=="1"){
            confirmPanel = Ext.create('Ext.panel.Panel', {
                border:false,
                items:[
                    {
                        xtype:'textareafield',
                        id:'rejectCause',
                        value:'同意，审批通过。',
                        fieldStyle:'font-size:14px;',
                        width:490,
                        height:250
                    },{
                        xtype:'panel',
                        layout: 'hbox',
                        border:false,
                        height:40,
                        style:'padding-top:3px;',
                        items:[
                            {
                                xtype: "label",
                                text: '通知人员：',
                                lableAlign: 'left',
                                style: 'padding-top:6px;'
                            },
                            {
                                xtype:'panel',id:'msg',selectSpId:'selectSpId',border:1,height : 22,width : 370,padding:'0 10 0 10',
                                front:true,
                                bodyStyle:"border-top:none;border-left:none;border-right:none;",
                                floating:false,
                                shadow:false,
                                scrollFlags:{overflowX:'',overflowY:''},
                                layout:{
                                    type:"column"
                                },
                                listeners:{
                                    render:function(){
                                        this.mon(this.getEl(),{
                                            mouseover:this.hadFocus,
                                            mouseout:this.lostFocus,
                                            scope:this
                                        });
                                    }
                                },
                                hadFocus:function(){
                                    var selectSpId=Ext.ComponentQuery.query("panel[selectSpId=selectSpId]")[0];
                                    if(selectSpId.items.length>5){
                                        spCollectScope.showSpWin(Ext.getCmp("msg").getEl().getLeft(),Ext.getCmp("msg").getEl().getTop(),selectSpId.items);
                                    }
                                },
                                lostFocus:function(){
                                }
                            },{
                                xtype:'button',
                                text:'添加',
                                style:'margin: 0 0 0 21px;',
                                handler:function(){
                                    var roi = spCollectScope.reportObjectId;
                                    var f = document.getElementById("msg");
                                    var arrayReportObjectId = roi.substring(0,roi.length-1).split(",");
                                    var p = new PersonPicker({
                                        multiple: true,
                                        isSelfUser:false,//是否选择自助用户
                                        isMiddle:true,//是否居中显示
                                        //	extend_str:"salary/"+spCollectScope.salaryid,//薪资选人控件个性化标注，用于控件中薪资权限的控制
                                        defaultSelected:arrayReportObjectId,//业务用户默认已选
                                        callback: function (c) {
                                            var value = "";
                                            var name = "";
                                            var selectSp=Ext.ComponentQuery.query("panel[selectSpId=selectSpId]")[0];
                                            selectSp.removeAll();
                                            for(var i=0;i<c.length;i++){
                                                spCollectScope.reportObjectId += c[i].id+",";
                                                spCollectScope.reportObjectName += c[i].name+",";
                                                selectSp.add(spCollectScope.createKeyItem('1',c[i].name,c[i].id,'0'));
                                            }
                                            spCollectScope.personcount=spCollectScope.reportObjectId;
                                            spCollectScope.sendMen=spCollectScope.sendMen+spCollectScope.reportObjectId;
                                        }
                                    }, f);
                                    p.open();
                                }
                            }]
                    }]
            });
        }else{
            confirmPanel = Ext.create('Ext.panel.Panel', {
                border:false,
                items:[{
                    xtype:'textareafield',
                    id:'rejectCause',
                    value:'同意，审批通过。',
                    width:485,
                    height:275
                }]
            });
        }

        var win=Ext.widget("window",{
            title:'批准意见',
            height:365,
            width:500,
            layout:'fit',
            modal:true,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                border:false,
                items:[confirmPanel],
                buttons:[
                    {xtype:'tbfill'},
                    {
                        text:common.button.ok,
                        handler:function(){
                            Ext.Msg.confirm(common.button.promptmessage,"确认批准选择的数据吗？",
                                function(id){
                                    if(id=='yes'){
                                        var map = new HashMap();
                                        map.put("salaryid",spCollectScope.salaryid);
                                        map.put("appdate",spCollectScope.date_count.split("#")[0]);
                                        map.put("count",spCollectScope.date_count.split("#")[1]);
                                        map.put("rejectCause",Ext.getCmp("rejectCause").getValue());
                                        if(Ext.getCmp("rejectCause").getValue().length==0){
                                            Ext.showAlert("请填写批准意见!");
                                            return;
                                        }
                                        map.put("selectID",spCollectScope.selectID.substring(1));
                                        map.put("cound",spCollectScope.cound);
                                        map.put("collectPoint",spCollectScope.collectPoint);
                                        map.put("sendMen",spCollectScope.sendMen);

                                        //判断是否有提交按钮 如果从待办进来且没有提交按钮且批准后剩余可批条数为0 那么跳回待办页面
                                        //如果有提交按钮，则和不从待办进入一样
                                        if(Ext.getCmp("spconfirm")==undefined){
                                            map.put("fromPending",spCollectScope.returnflag=='menu'?'0':'1');
                                        }
                                        map.put("opt","confirm");
                                        win.close();
                                        Ext.MessageBox.wait("正在批准，请稍候...", "等待");
                                        Rpc({functionId:'GZ00000435',success:function(form,action){
                                                Ext.MessageBox.close();
                                                var result = Ext.decode(form.responseText);
                                                var flag=result.succeed;
                                                if(flag){
                                                    //剩余可批条数
                                                    var listNumber=result.lastNumber;
                                                    //从待办进来，且剩余可批条数为0，那么跳回待办页面
                                                    if(listNumber==0&&spCollectScope.returnflag!='menu'){
                                                        spCollectScope.back();
                                                    }else {
                                                        Ext.getCmp('tree').selModel.deselectAll();
                                                        spCollectScope.reload();
                                                    }
                                                }else{
                                                    Ext.showAlert(result.message);
                                                }
                                            }},map);
                                    }else{
                                        return;
                                    }
                                }
                            );
                        }
                    },
                    {
                        text:common.button.cancel,
                        handler:function(){
                            win.close();
                        }
                    },
                    {xtype:'tbfill'}
                ],
                listeners:{
                    move:function(obj,x,y){
                        spCollectScope.x=x;
                        spCollectScope.y=y;
                    }
                }}]
        });
        win.show();

    },
    //审批确认
    open_submit_dialog:function(){
        var appdate=spCollectScope.date_count.split("#")[0];
        var count=spCollectScope.date_count.split("#")[1];
        if(appdate==''||count==''){
            Ext.showAlert("没有可操作的数据！");
            return;
        }
        spCollectScope.closeDetailWindow();//关闭明细页面
        if(spCollectScope.subNoShowUpdateFashion=='0')
        {
            spCollectScope.flag="sp";
            spCollectScope.appdate=spCollectScope.date_count.split("#")[0];
            spCollectScope.count=spCollectScope.date_count.split("#")[1];
            Ext.require('SalaryUL.Submit',function(){
                Ext.create("SalaryUL.Submit",spCollectScope);
            })
        }else{
            var str="";
            if(spCollectScope.isTotalControl=='1')
                str+="、总额校验";
            if(spCollectScope.verify_ctrl=='1'){
                str+="、数据审核";
            }else{//如果没设置审核那么直接去执行总额校验
                str = str.substring(1)==""?"提交":str.substring(1);
                Ext.MessageBox.wait("正在"+str+"，请稍候...", "等待");
                spCollectScope.totalControl("3");
                return;
            }
            Ext.MessageBox.wait("正在"+str.substring(1)+"，请稍候...", "等待");
            spCollectScope.verify1("3");
        }
    },
    //实际审批提交操作
    submit:function(){
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("appdate",spCollectScope.date_count.split("#")[0]);
        map.put("count",spCollectScope.date_count.split("#")[1]);
        map.put("selectID",spCollectScope.selectID.substring(1));
        map.put("cound",spCollectScope.cound);
        map.put("collectPoint",spCollectScope.collectPoint);
        map.put("subNoShowUpdateFashion",spCollectScope.subNoShowUpdateFashion);
        map.put("fromPending",spCollectScope.returnflag=='menu'?'0':'1');
        Ext.MessageBox.wait("正在提交，请稍候...", "等待");
        Rpc({functionId:'GZ00000440',timeout:1000000,success:function(form,action){
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                var flag=result.succeed;
                if(flag){
                    //剩余可批条数
                    var listNumber=result.lastNumber;
                    //从待办进来，且剩余可批条数为0，那么跳回待办页面
                    if(listNumber==0&&spCollectScope.returnflag!='menu'){
                        spCollectScope.back();
                    }else {
                        spCollectScope.reload();
                    }
                }else{
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    //个别提交
    gbsubmit:function(){
        var selectData = spCollectScope.salaryObj.tablePanel.getSelectionModel().getSelection(true);
        if(selectData.length==0){
            Ext.showAlert('请选择需要提交的记录！');
            return
        }
        var selectGzRecords = "";
        var ishave =0;
        for(var j=0;j<selectData.length;j++){
            selectGzRecords += selectData[j].data.a0100_e+"/";
            selectGzRecords += selectData[j].data.nbase1_e+"/";
            selectGzRecords += selectData[j].data.a00z0+"/";
            selectGzRecords += selectData[j].data.a00z1;
            selectGzRecords += "#";
            if(selectData[j].data.sp_flag.split("`")[0]!='06')
                ishave=1;
        }

        if(ishave==1){
            Ext.showAlert("请选择结束状态的记录！");
            return;
        }
        
        Ext.Msg.confirm(common.button.promptmessage,"是否重新提交？",function(id){
			if(id=='yes'){
				var doSelectAll = spCollectScope.salaryObj.tablePanel.getSelectionModel().doSelectAll;

		        var map = new HashMap();
		        map.put("salaryid",spCollectScope.salaryid);
		        map.put("appdate",spCollectScope.date_count.split("#")[0]);
		        map.put("count",spCollectScope.date_count.split("#")[1]);
		        map.put("selectID",spCollectScope.id);
		        map.put("selectGzRecords",selectGzRecords);
		        map.put("doSelectAll",doSelectAll);
		        map.put("cound",spCollectScope.cound);
		        map.put("collectPoint",spCollectScope.collectPoint);
		        Ext.MessageBox.wait("正在提交，请稍候...", "等待");
		        Rpc({functionId:'GZ00000439',success:function(form,action){
		                Ext.MessageBox.close();
		                var result = Ext.decode(form.responseText);
		                var flag=result.succeed;
		                if(flag){
		                    spCollectScope.loadStore();
		                }else{
		                    Ext.showAlert(result.message);
		                }
		            }},map);
			}
			else{
				return;
			}
		});
    },
    //批量引入
    batchImport:function(){
        if(spCollectScope.date_count.split("#")[0]==''){
            Ext.showAlert(gz.label.selectbosdateandcount);
            return;
        }
        spCollectScope.closeDetailWindow();//关闭明细页面
        Ext.require('SalaryUL.BatchImport',function(){
            Ext.create("SalaryUL.BatchImport",{salaryid:spCollectScope.salaryid,imodule:spCollectScope.imodule,viewtype:spCollectScope.viewtype,appdate:spCollectScope.date_count.split("#")[0],type:'0',count:spCollectScope.date_count.split("#")[1]});
        })
    },
    //银行报盘
    updisk:function(){
        if(spCollectScope.date_count.split("#")[0]==''){
            Ext.showAlert(gz.label.selectbosdateandcount);
            return;
        }
        Ext.require('SalaryUL.UpDisk',function(){
            GzGlobal = Ext.create('SalaryUL.UpDisk',{salaryid:spCollectScope.salaryid,model:'1',appdate:spCollectScope.date_count.split("#")[0],count:spCollectScope.date_count.split("#")[1]});
        })
    },

    //数据比对
    changesMore:function(){
        if(spCollectScope.date_count=='')
        {
            Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
            return;
        }
        var ym_c = spCollectScope.date_count.split("#");
        Ext.MessageBox.wait("正在比对，请稍候...", "等待");
        Ext.require('SalaryUL.Changesmore',function(){
            GzGlobal = Ext.create('SalaryUL.Changesmore',{returnBackFunc:spCollectScope.reloadStore,salaryid:spCollectScope.salaryid,imodule:"0",appdate:ym_c[0],count:ym_c[1],type:'2',addflag:'1',minusflag:'1',changeflag:'1'});
        })
    },

    //返回主页面
    reloadStore:function(salaryid){
        window.location.href="/module/gz/salaryspcollect/SalarySpCollect.html?salaryid="+spCollectScope.salaryid+"&currentPage="+spCollectScope.currentPage+"&returnflag=menu";
//		SalaryTemplateGlobal.returnSalarySp(salaryid);
    },

    //同步人员顺序
    syncgzemp:function(){
        var ym_c = spCollectScope.date_count.split("#");
        var map = new HashMap();
        map.put("salaryid",spCollectScope.salaryid);
        map.put("viewtype",spCollectScope.viewtype);
        map.put("appdate",ym_c[0]);
        Rpc({functionId:'GZ00000061',async:false,success:function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    spCollectScope.reload();
                    Ext.showAlert("同步人员顺序成功！");
                }else{
                    Ext.showAlert("同步人员顺序失败！错误信息："+result.message);
                };
            }},map);
    },
    //刷新汇总页面
    reload:function(){
        Ext.getCmp('tree').selModel.deselectAll();
        var nodes = [];
        var selNodes = Ext.getCmp('tree').getRootNode().childNodes;
        Ext.each(selNodes, function(node){
            if(node.isExpanded()){
                nodes[nodes.length]=node;
            }
        });
        spCollectScope.treeStore.removeAll();//xiegh 20170426 bug26867

        //刷新记录时重设inCount为3 防止多次刷新代办 zhanghua 2017-5-15
        var xml = spCollectScope.treeStore.getProxy().extraParams;
        xml.inCount='3';
        spCollectScope.treeStore.getProxy().extraParams = xml;
        if(Ext.getCmp("spcompute") != undefined)
            Ext.getCmp("spcompute").enable();
        if(Ext.getCmp("spcheck") != undefined)
            Ext.getCmp("spcheck").enable();
        if(Ext.getCmp("spappeal") != undefined)
            Ext.getCmp("spappeal").enable();
        if(Ext.getCmp("spreject") != undefined)
            Ext.getCmp("spreject").enable();
        if(Ext.getCmp("spapprove") != undefined)
            Ext.getCmp("spapprove").enable();
        if(Ext.getCmp("spconfirm") != undefined)
            Ext.getCmp("spconfirm").enable();
        spCollectScope.treeStore.load({
            callback: function(record, option, succes){
                Ext.each(record, function(re){
                    Ext.each(nodes, function(node){
                        if(node.data.id==re.data.id){
                            re.expand();
                        }
                    });
                });
            }
        });
    },
    //刷新明细页面
    loadStore:function(){
        var store = Ext.data.StoreManager.lookup('salaryspdetail_dataStore');
//		store.currentPage=1;
        if(store!=undefined)
            store.load();
    },
    //刷新整个页面数据
    reLoadStoreData:function () {
        spCollectScope.reload();
        spCollectScope.loadStore();

    },

    back:function(){
        if(spCollectScope.returnflag=="menu")
        {
            var imodule=0;
            if(spCollectScope.imodule=="Z~30DuTtqmt~33kPAATTP~33HJDPAATTP")
                imodule=1;
            Ext.getCmp('spviewport').destroy();
            window.location.href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=1&imodule="+imodule+"&currentPage="+spCollectScope.currentPage+"";
//			tableObj.getMainPanel().add(tableObj.bodyPanel);
//			SalaryTemplateGlobal.SearchBox.removeAllKeys();
        }else if(spCollectScope.returnflag=="portal_list"){
            window.location.href="/general/template/matterList.do?b_query=link";
        } else{
            if(spCollectScope.bosflag=="hl")
            {
                window.location.href="/templates/index/portal.do?b_query=link";
            }else if(spCollectScope.bosflag=="hcm"){
                window.location.href="/templates/index/hcm_portal.do?b_query=link";
            }
        }
    },

    //薪资审批-导出excel lis-20160106
    exportData:function(){
        var appdate = spCollectScope.date_count.split("#")[0];
        var count = spCollectScope.date_count.split("#")[1];
        if(appdate==''||count==''){
            Ext.showAlert("没有可操作的数据！");
            return;
        }
        Ext.require('SalaryUL.inout.ExportData',function(){
            Ext.create("SalaryUL.inout.ExportData",{salaryid:spCollectScope.salaryid,flag:"sp",appdate:appdate,count:count,cound:spCollectScope.cound});
        })
    },

    //下载模板数据
    downLoadTemp:function(){
        //薪资审批改为可选择指标进行下载sunjian
        var appdate = spCollectScope.date_count.split("#")[0];
        var count = spCollectScope.date_count.split("#")[1];
        if(appdate==''||count==''){
            Ext.showAlert("没有可操作的数据！");
            return;
        }
        Ext.require('SalaryUL.inout.ExportData',function(){//typeflag=2表示下载模板，1或者空为导出 sunjian2017-05-31
            Ext.create("SalaryUL.inout.ExportData",{salaryid:spCollectScope.salaryid,typeflag:"2",flag:"sp",appdate:appdate,count:count,cound:spCollectScope.cound});
        })
    },
    //导出合计
    downLoadTotal:function(){
        var appdate = spCollectScope.date_count.split("#")[0];
        var count = spCollectScope.date_count.split("#")[1];
        if(appdate==''||count==''){
            Ext.showAlert("没有可操作的数据！");
            return;
        }
        var treePanel=Ext.getCmp('tree');
        var store=Ext.getCmp('tree').getStore();
        var data=store.data;
        var list=new Array();
        Ext.Array.each(data.items,function(item, index, self){//获取页面显示数据
            list.push(item.data);

        });
        var itemids="";
        var itemText=new Array();
        Ext.Array.each(treePanel.columns,function(item, index, self){//取导出项id和名称
            itemids=itemids+"/"+item.dataIndex;
            itemText.push(item.text);
        });
        spCollectScope.exportDataTotal(itemids,itemText,list);
    },
    exportDataTotal:function(itemids,itemText,dataList){
        var appdate = spCollectScope.date_count.split("#")[0];
        var count = spCollectScope.date_count.split("#")[1];
        var map = new HashMap();
        map.put("itemids",itemids);
        map.put("itemText",itemText);
        map.put("salaryid",spCollectScope.salaryid);
        map.put("flag","sptotal");
        map.put("appdate",appdate);
        map.put("count",count);
        map.put("imodule",spCollectScope.imodule);
        map.put("cound",spCollectScope.cound);
        map.put("dataList",dataList);
        Rpc({functionId:'GZ00000042',success:function(form,action){
//				Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                	var fieldName = getDecodeStr(result.fileName);
                    window.location.target="_blank";
                    window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
                }else{
                    Ext.showAlert(result.message);
                }

            }},map);
    },
    importAsTemplate :function(){
        Ext.require('SalaryUL.ImportAsTemplate',function(){
            Ext.create('SalaryUL.ImportAsTemplate',{returnBackFunc:spCollectScope.reLoadStoreData,download:spCollectScope.downLoadTemp,see:spCollectScope.importTemplTable});
        })
    },
    //导入下载模板
    importTemplTable :function(){
        var appdate = spCollectScope.date_count.split("#")[0];
        var count = spCollectScope.date_count.split("#")[1]
        if(appdate==''||count==''){
            Ext.showAlert("没有可操作的数据！");
            return;
        }
        Ext.require('SalaryUL.inout.ImportTemplTable',function(){
            Ext.create("SalaryUL.inout.ImportTemplTable",{salaryid:spCollectScope.salaryid,flag:"sp",appdate:appdate,count:count});
        })
    },
    //栏目设置按钮事件汇总页面栏目设置
    schemeSetting:function(){
        Ext.require("EHR.tableFactory.plugins.SchemeSetting", function() {
            var window = new EHR.tableFactory.plugins.SchemeSetting({
                subModuleId : spCollectScope.setId,
                schemeItemKey:'',
                itemKeyFunctionId:'',
                viewConfig:{
                    publicPlan:spCollectScope.mainPulicPlan=='1'?true:false,
                    sum:false,
                    lock:true,
                    merge:false,

                    pageSize:'20'
                },
                closeAction:spCollectScope.closeSettingWindow
            });
        });
    },
    closeSettingWindow:function(){
        Ext.getCmp('spviewport').destroy();
        SalarySpCollect = null;
        Ext.require('Salarybase.salaryspcollect.SalarySpCollect', function(){
            SalarySpCollect = Ext.create("Salarybase.salaryspcollect.SalarySpCollect",{salaryid:spCollectScope.salaryid,appdate:spCollectScope.appdate,count:spCollectScope.count});
        });
    },
    gzReport:function(salaryid)
    {
        var a00z1=spCollectScope.date_count.split("#")[0];
        var a00z0=spCollectScope.date_count.split("#")[1];
        if(a00z1==''||a00z0=='')
        {
            Ext.showAlert(gz.label.selectbosdateandcount);
            return;
        }
        Ext.Loader.setPath('SalaryReport',"../../../../module/gz/salaryreport");
        Ext.require('SalaryReport.SalaryReport', function(){
            Ext.create("SalaryReport.SalaryReport",{salaryid:spCollectScope.salaryid,gz_module:spCollectScope.gz_module,appdate:spCollectScope.date_count.split("#")[0],count:spCollectScope.date_count.split("#")[1],model:'1'});
        });
    },
    clickCell:function(record){
        var rowdata = record.data;
        var sp_flag = rowdata.sp_flag;
        var curr_user=rowdata.curr_user;
        /*
            if(sp_flag!=null&&sp_flag.split("`")[0]!="01"&&sp_flag.split("`")[0]!="07"){
                return false;
            }
            */
        if(spCollectScope.allowEditSubdata=='1'&&(sp_flag!=null&&sp_flag.split("`")[0]=='06')&&spCollectScope.hasSubPirv=='true')//运行修改提交后的数据，且具有提交权限
        {
            return true;
        }
        else
        {
            if(curr_user.toLowerCase()==spCollectScope.username.toLowerCase()&&(sp_flag!=null&&(sp_flag.split("`")[0]=="02"||sp_flag.split("`")[0]=="07")))
            {
                return true;
            }
            else
                return false;
        }

    },
    //弹出显示所有人员的框x:panel控件的左坐标，y:控件的高度坐标
    showSpWin:function(x,y,items){
        var xx = 0;
        var yy = 0;
        //获取到panel控件的高度
        var eHeight = Ext.getCmp("msg").getEl().getHeight();
        xx = x + 5;
        yy = y + eHeight -1;
        var emailWin = Ext.getCmp('emailWin');
        if(emailWin==undefined){
            emailWin = Ext.widget('window',{
                width:355,
                id:'emailWin',
                x:xx,
                y:yy,
                header:false,
                resizable : false,
                closeAction:'hide',
                layout:{type:"column"},
                items:[],
                listeners:{
                    render:function(){
                        this.mon(Ext.getDoc(), {
                            mouseover: this.showIf,
                            mousedown: this.hiddenIf,
                            scope: this
                        });
                    }
                },
                hiddenIf: function(e) {
                    var me = this;
                    if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target)) {
                        me.hide();
                    }
                },
                showIf:function(e){

                }
            });
        }else{
            emailWin.setPagePosition(xx,yy);
        }
        emailWin.removeAll(true);
        items.each(function(r){
            emailWin.add(spCollectScope.createKeyItem(r.entertype,r.staffname, r.staffid,'1'));
        });
        emailWin.show();
    },
    //被选择的人的框
    createKeyItem:function(entertype,staffname,staffid,flag){
        var con = Ext.widget('container',{
            style:{border:"solid #c5c5c5 0px",backgroundColor:'#f8f8f8'},
            margin:2,
            width:65,
            height:16,
            value:staffname,
            entertype:entertype,
            staffname:staffname,
            staffid:staffid,
            layout:'hbox',
            items:[{xtype:'label',padding:'0 0 0 5',text:staffname,flex:10},{
                xtype:'image',src: rootPath+'/components/querybox/images/hongcha.png',hidden:true,
                width:16,height:16,margin:0,style:'cursor:pointer;',
                listeners:{
                    render:function(hh){
                        this.getEl().on('click',function(){
                            var staffid =  this.ownerCt.staffid;
                            var staffname =  this.ownerCt.staffname;
                            var entertype = this.ownerCt.entertype;
                            var emailcon = Ext.ComponentQuery.query("container[staffid="+this.staffid+"]")[0];
                            var selectEmail=Ext.ComponentQuery.query("panel[selectSpId=selectSpId]")[0];
                            var emailWin = Ext.getCmp('emailWin');
                            if(flag=='0'){
                                if(emailWin!=undefined){
                                    emailWin.remove(emailcon,true);
                                }
                            }else{
                                var a = selectEmail.query("container[staffid="+staffid+"]")[0];
                                selectEmail.remove(a,true);
                            }
                            spCollectScope.removeQueryKey(this.ownerCt);
                            spCollectScope.reportObjectId =  spCollectScope.reportObjectId.replace(entertype+':'+staffid+',',"");
                            spCollectScope.reportObjectName = spCollectScope.reportObjectName.replace(entertype+':'+staffname+',',"");
                        },this);
                    }
                }
            }],
            listeners:{
                render:function(){
                    this.mon(this.getEl(),{
                        mouseover:this.hadFocus,
                        mouseout:this.lostFocus,
                        scope:this
                    });
                },
                afterrender: function(hh) {
                    //计算长度
                    var staffnamelength = staffname.replace(/[^\x00-\xff]/g,"01").length;
                    var tip = Ext.create('Ext.tip.ToolTip', {
                        target: staffnamelength < 11?"":hh.getEl(),
                        html: staffnamelength < 11?"":staffname,
                        trackMouse: true,
                        bodyStyle:"background-color:white;border:1px solid #c5c5c5",
                        border:true
                    });
                }
            },
            hadFocus:function(){
                this.child('image').setVisible(true);
                this.setStyle({
                    border:"solid #ff8c26 0px",
                    backgroundColor:'#feefe5'
                });
            },
            lostFocus:function(){
                this.child('image').setVisible(false);
                this.setStyle({
                    border:"solid #c5c5c5 0px",
                    backgroundColor:'#f8f8f8'
                });
            }

        });
        return con;
    },
    removeQueryKey:function(keyItem){
        keyItem.destroy();
    },


    //打开常用报表下拉列表
    openCommon_reportCombo:function () {

        if(spCollectScope.commonreportlist.length==1){
            var record=spCollectScope.commonreportlist[0];
            var tabid=record.id;
            var rsid=record.rsid;
            if("0"==rsid){
                spCollectScope.doShowCustom(record,gz.label.userDefinedTable,record.text);//'用户自定义表'
            }else{
                var title = "";
                if ("12" == rsid) {
                    title = gz.label.insuranceSchedule;//"保险明细表"
                } else if ("13" == rsid) {
                    title = gz.label.insuranceSummary;//"保险汇总表"
                } else if ("1" == rsid) {
                    title =gz.label.payroll;// "工资条"
                } else if ("2" == rsid) {
                    title = gz.label.payrollSignature;//"工资发放签名表"
                } else if ("3" == rsid) {
                    title = gz.label.salarySummary;//"工资汇总表"
                }else if ("4" == rsid) {
                    title = gz.label.salaryReportAnalysis;//"人员结构分析表"
                }
                spCollectScope.openSalaryReport(rsid,tabid,title,record.text);
            }
            return;
        }


        var window = Ext.getCmp("commonReportWin");
        if (window == undefined) {
            var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                '<div  style=" white-space:nowrap;height:auto;width:auto;cursor:pointer;margin-bottom: 3px;margin-left: 3px;margin-right: 3px;margin-bottom: 5px" >' +
                ' {text} </div>',
                '</tpl>'
            );

            //方案数据store
            var schemeStore = Ext.create('Ext.data.Store', {
                storeId: 'commonReportListStore',
                fields: ['text', 'id'],
                data: spCollectScope.commonreportlist
            });
            var dataView = Ext.create('Ext.view.View', {
                itemSelector: 'div',
                scrollable: 'y',
                tpl: tpl,
                layout: 'fit',
                deferEmptyText: false,
                overItemCls: 'commonReportComboOverCls',
                border: false,
                selectedItemCls: 'commonReportComboSelectedCls',
                store: schemeStore,
                multiSelect: false,
                listeners: {
                    select: function (me, record) {
                        var data=record.data;
                        var tabid=data.id;
                        var rsid=data.rsid;
                        if("0"==rsid){
                            spCollectScope.doShowCustom(data,gz.label.userDefinedTable,data.text);//'用户自定义表'
                            window.hide();
                        }else{
                            var title="";
                            if ("12" == rsid) {
                                title = gz.label.insuranceSchedule;//"保险明细表"
                            } else if ("13" == rsid) {
                                title = gz.label.insuranceSummary;//"保险汇总表"
                            } else if ("1" == rsid) {
                                title =gz.label.payroll;// "工资条"
                            } else if ("2" == rsid) {
                                title = gz.label.payrollSignature;//"工资发放签名表"
                            } else if ("3" == rsid) {
                                title = gz.label.salarySummary;//"工资汇总表"
                            }
                            spCollectScope.openSalaryReport(rsid,tabid,title,data.text);
                            window.hide();
                        }
                        me.clearSelections();
                        dataView.refresh();
                    }
                }
            });
            var btnX=Ext.getCmp('common_Report_button').getX();
            var btnY=Ext.getCmp('common_Report_button').getY()+21;

            window = Ext.widget("window", {
                layout: 'fit',
                x: btnX,
                y: btnY,
                minWidth: 150,
                maxHeight: 400,
                scrollable: true,
                header: false,
                modal: false,
                id: 'commonReportWin',
                border: false,
                closeAction: 'destroy',
                items: [dataView],
                listeners: {
                    "render": function () {
                        document.getElementById("commonReportWin").onmouseout = function (e) {
                            if (e == undefined) {
                                e = event;
                            }
                            var s = e.toElement || e.relatedTarget;
                            if (s == undefined || !this.contains(s)) {
                                window.hide();
                            }
                        };
                        //移出常用报表按钮方法
                        document.getElementById("common_Report_button").onmouseout = function (e) {
                            if (e == undefined) {
                                e = event;
                            }
                            var left = this.getBoundingClientRect().left;
                            var top = this.getBoundingClientRect().top;

                            if (!(e.clientX > left && e.clientY + 15 > (top + this.offsetHeight))) {
                                var s = e.toElement || e.relatedTarget;
                                if (s == undefined || !this.contains(s)) {
                                    window.hide();
                                }
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
                var store=Ext.StoreMgr.get('commonReportListStore');
                store.load(spCollectScope.commonreportlist);
                window.show();
            }
        }

    },
    /**
     * 打开薪资报表
     * @param rsid 表类号
     * @param rsdtlid 具体表号
     * @param parenttext 父节点名称
     * @param text 选中表名称
     */
    openSalaryReport: function (rsid, rsdtlid, parenttext, text) {
        var obj = new Object();
        obj.rsid = rsid;
        obj.rsdtlid = rsdtlid;
        obj.salaryid = spCollectScope.salaryid;//salaryReportScope.salaryid_encrypt;
        obj.gz_module = spCollectScope.imodule;
        obj.model = '1';//salaryReportScope.model;
        obj.bosdate = spCollectScope.date_count.split("#")[0];//salaryReportScope.bosdate_encrypt;
        obj.count = spCollectScope.date_count.split("#")[1];//salaryReportScope.count_encrypt;
        obj.title = parenttext + "-->" + text;

        Ext.require('SalaryReport.OpenSalaryReport', function () {
            Ext.create("SalaryReport.OpenSalaryReport", obj);
        });
        //}
    },
    //打开自定义报表
    showCustom: function (uid, parenttext, text) {
        var strurl = "/gz/gz_accounting/report/open_gzbanner.do?b_report=link&" +
            "checksalary=salary&opt=int" +
            "&salaryid=" + spCollectScope.setId.split("_")[1] +
            "&tabid=" + uid +
            "&a_code=" +
            "&subModuleId=" +
            "&gz_module=" + spCollectScope.imodule + "&reset=1&" +
            "model=1&boscount=" + spCollectScope.date_count.split("#")[1] + "&bosdate=" + spCollectScope.date_count.split("#")[0] + "&pageRows=init";
        Ext.require('SalaryReport.CreateWindow', function () {
            Ext.create("SalaryReport.CreateWindow", {title: parenttext + "-->" + text, url: strurl});
        });
    },
    doShowCustom:function(data,parentNode,value){
        if(data.reporttype=='10'){
            spCollectScope.showCustom(data.id,parentNode,value);
        }else if(data.reporttype=='0'){
            //特殊报表
            spCollectScope.showSpecialreport(data.tabid);
        }else if(data.reporttype=='3'){
            //花名册
            spCollectScope.showOpenMuster(data.tabid,parentNode,value,data.nmodule);
        }else if(data.reporttype=='4'){
            //简单名册
            spCollectScope.showSimpleMuster(data.url);
        }
    },    //打开花名册
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
    }
})