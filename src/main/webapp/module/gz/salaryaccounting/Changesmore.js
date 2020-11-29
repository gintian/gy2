/**
 * 数据比对 调用的时候先使用Ext.MessageBox.wait("正在比对，请稍候...", "等待"); 否则关闭要报错
 * lis 2016-01-11
 */
Ext.define('SalaryUL.Changesmore',{
    setid:'',//页面设置标示
    obj:'',
    constructor:function(config){
        gz_changesmore=this;
        gz_changesmore.salaryid = config.salaryid;
        gz_changesmore.imodule = config.imodule;
        gz_changesmore.appdate = config.appdate;
        gz_changesmore.count = config.count;
        gz_changesmore.type = config.type;
        gz_changesmore.addflag = config.addflag;
        gz_changesmore.minusflag = config.minusflag;
        gz_changesmore.changeflag = config.changeflag;
        gz_changesmore.returnBackFunc = config.returnBackFunc;
        gz_changesmore.changeflags = ",9";//信息变动0 新增1 or 减少2 的标记
        gz_changesmore.init();
    },
    init:function(config){
        var map = new HashMap();
        map.put("salaryid",gz_changesmore.salaryid);
        map.put("appdate",gz_changesmore.appdate);
        map.put("imodule",gz_changesmore.imodule);
        map.put("count",gz_changesmore.count);
        map.put("type",gz_changesmore.type);
        Rpc({functionId:'GZ00000071',timeout:100000,success: gz_changesmore.getTableOk},map);
    },
    // 加载页签下表单
    getTableOk:function(form, action){
        var result = Ext.decode(form.responseText);
        Ext.MessageBox.hide();//关闭上级页面 “正在比对”的遮罩
        if(result.succeed){
            if(result.msg=="ok"){
                var orgTreeTitle=result.orgTreeTitle;
                var store = Ext.create('Ext.data.Store', {
                    fields:['itemid','itemdesc',"lastdata","nowdata","peoplenum","margin"],
                    id:'mainStore',
                    data:result.data
                });
                //生成导出项目表格
                var panel = Ext.create('Ext.grid.Panel', {
                    store: store,
                    width: 390,
                    height: 410,
                    columnLines:true,
                    sortableColumns:false,
                    columns: [
                        { text: "id",hidden:true, dataIndex: 'itemid'},
                        { text: gz.label.itemdescName,menuDisabled:true, dataIndex: 'itemdesc',width: 140,renderer:gz_changesmore.renderName},//项目名称
                        { text: gz.label.nowdata,menuDisabled:true, dataIndex: 'nowdata',width: 110,align:'right'},//本期数据
                        { text: gz.label.lastdata,menuDisabled:true, dataIndex: 'lastdata',width: 110,align:'right'},//上期数据
                        { text: gz.label.margin,menuDisabled:true, dataIndex: 'margin',width: 110,align:'right'},//差异值
                        { text: gz.label.peoplenum,menuDisabled:true, dataIndex: 'peoplenum',width: 110,align:'right'}//差异人数
                    ],
                    listeners:{
                        rowdblclick:gz_changesmore.mainRowdbclick,
                        selectionchange:gz_changesmore.selectChange

                    }
                });
                gz_changesmore.titleBarText=result.titleBarText;
                Ext.define('Employee',{
                    extend:'Ext.data.Model',
                    fields:[
                        {name:'id',type:'string'},
                        {name:'text',type:'string'}
                    ]

                });

                var treeStore=Ext.create('Ext.data.TreeStore',{
                    model:Employee,
                    proxy:{
                        type: 'transaction',
                        functionId:'GZ00000073',
                        extraParams:{
                            salaryid:gz_changesmore.salaryid,
                            appdate:gz_changesmore.appdate,
                            imodule:gz_changesmore.imodule,
                            count:gz_changesmore.count,
                            type:gz_changesmore.type
                        },
                        reader: {
                            type: 'json',
                            root: 'data'
                        },
                        timeout:999999
                    }
                });

                var tree=Ext.create('Ext.tree.Panel',{
                    useArrows:false,
                    width:300,
                    height:600,
                    id:'treePanel',
                    border:false,
                    bodyBorder:false,
                    store:treeStore,
                    rootVisible:false,
                    listeners:{
                        checkchange : function(model,selected){//选择有改变时产生的事件
                            var parentmodel=model.parentNode;
                            if(!Ext.isEmpty(parentmodel)&&parentmodel.id!='root')
                                gz_changesmore.treeParChecked(parentmodel,model,selected);
                            else{
                                gz_changesmore.treeAllChecked(model,selected);//后台sql拼接父节点选中则不再判断子节点，所以全选递归无论是否执行结束无影响
                                gz_changesmore.selectOrgTree();
                            }
                        }
                    }
                });
                var viewPort=new Ext.Panel({
                    layout:'border',
                    border:false,
                    items:[
                        {
                            region:'west',
                            layout:'fit',
                            xtype:'panel',
                            title:orgTreeTitle,//gz.label.orgtable,//组织列表
                            collapsible:true,
                            bodyStyle:'border-right:none;',
                            width:210,
                            collapseMode:'header',//取消隐藏后的浮动窗口
                            id:'west',
                            items:[tree]
                        },
                        {region:'center',layout:'fit',border:false,xtype:'panel',id:'center',items:[panel]}
                    ]
                });
                var vs = Ext.getBody().getViewSize();
                var win = Ext.widget("window",{
                    title:gz.label.Changemore,  //数据比对
                    padding:'0 2 0 2',
                    margin:0,
                    tbar:{
                        id:'viewBar',
                        xtype:'toolbar',
                        height:32,
                        padding:0,
                        margin:0,
                        border:false,
                        items:[{
                            margin:'0 0 0 2',
                            padding:0,
                            height:22,
                            xtype:'button',
                            text:gz.label.outExcel,//导出excel
                            handler:gz_changesmore.exportData
                        }]
                    },
                    height:vs.height,
                    width:vs.width,
                    id:'mWindow',
                    layout:'fit',
                    modal:true,
                    bodyBorder:false,
                    border:false,
                    closeAction:'destroy',
                    resizable:false,
                    items:[viewPort]
                });
                win.show();
                var header=Ext.getCmp('west').header;
                header.setHeight(31);
                header.setStyle('background-color', 'rgb(240, 240, 240)');
                header.setStyle('padding', '0px 10px');
                header.setStyle('border-right', '0px');
                //插入右上角信息栏
                var toolbar=Ext.getCmp('viewBar');
                toolbar.add("->");
                toolbar.add({ xtype: 'label',id:'titleBarText',html:gz_changesmore.titleBarText});
            }
        }else{
            Ext.showAlert(result.message);
        }
    },
    treeParChecked:function(model,ymodel,selected){//修改父节点选中状态
        var isselect=true;
        for(var i=0;i<model.childNodes.length;i++){
            var child=model.childNodes[i];
            if(!child.raw.checked)
                isselect=false;
        }
        model.set({checked:isselect});
        var parentmodel=model.parentNode;
        if(Ext.isEmpty(parentmodel))
            gz_changesmore.selectOrgTree();
        else
            gz_changesmore.treeParChecked(parentmodel,ymodel,selected);
// 暂时取消对子节点遍历
//	        	if(selected)
//	        		gz_changesmore.treechiChecked(ymodel)
//	        	else
//	        		gz_changesmore.selectOrgTree();
//	        else
//	        	gz_changesmore.treeParChecked(parentmodel,ymodel,selected);
    },
//		treechiChecked:function(model){//修改子节点选中状态 目前没有深度遍历。  暂时取消
//        	var isselect=true;
//	        for(var i=0;i<model.childNodes.length;i++){
//	        	var child=model.childNodes[i];
//	        	child.set({checked:true});
//	        }
//	        gz_changesmore.selectOrgTree();
//		},
    //树节点全选
    treeAllChecked:function(model,selected){
        for(var i=0;i<model.childNodes.length;i++){
            var child=model.childNodes[i];
            child.set({checked:selected});
            gz_changesmore.treeAllChecked(child,selected);
        }
    },
    //更新主页面
    selectOrgTree:function(){
        gz_changesmore.selectID='';
        var records = Ext.getCmp('treePanel').getChecked();
        for(var i=0;i<records.length;i++){
            gz_changesmore.selectID+="#"+records[i].data.id;

        }

        var map = new HashMap();
        map.put("salaryid",gz_changesmore.salaryid);
        map.put("appdate",gz_changesmore.appdate);
        map.put("imodule",gz_changesmore.imodule);
        map.put("count",gz_changesmore.count);
        map.put("type",gz_changesmore.type);
        map.put("selectID",gz_changesmore.selectID);
        Rpc({functionId:'GZ00000071',timeout:100000,success:function(form, action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    var store = Ext.data.StoreManager.lookup('mainStore');
                    store.loadData(result.data);
                    Ext.getCmp('titleBarText').destroy();
                    var toolbar=Ext.getCmp('viewBar');
                    toolbar.add({ xtype: 'label',id:'titleBarText',html:result.titleBarText});
                }
            }},map);
        if(!Ext.isEmpty(gz_changesmore.haveWin)&&gz_changesmore.haveWin==true){
            gz_changesmore.openDetaileTable(gz_changesmore.fieldItem,gz_changesmore.fieldName);
        }
    },
    renderName:function(value, metaData, Record){
        var fieldItem=Record.data.itemid;
        var fieldName=Record.data.itemdesc;
        if(fieldItem=='rootSum')
            return value;
        var html = "<a href=javascript:gz_changesmore.openDetaileTable('"+fieldItem+"','"+fieldName+"');>" + value + "</a>";
        return html;
    },
    //主页面选中行变更
    selectChange:function(model,selected){
        if(model.selected.items[0]!=''&&model.selected.items[0]!=undefined){
            var fieldItem=model.selected.items[0].data.itemid;
            var fieldName=model.selected.items[0].data.itemdesc;
            gz_changesmore.fieldItem=fieldItem;
            gz_changesmore.fieldName=fieldName;
            if(fieldItem=='rootSum'||!gz_changesmore.haveWin)
                return ;
            gz_changesmore.openDetaileTable(fieldItem,fieldName);
        }
    },
    //双击行事件
    mainRowdbclick:function(tab,record){
        var fieldItem=record.data.itemid;
        var fieldName=record.data.itemdesc;
        if(fieldItem=='rootSum')
            return ;
        gz_changesmore.openDetaileTable(fieldItem,fieldName);
    },
    //打开明细表
    openDetaileTable:function(itemid,itemdesc){
        gz_changesmore.fieldItem=itemid;
        gz_changesmore.fieldName=itemdesc;
        var map = new HashMap();
        map.put("flag","0");
        map.put("salaryid",gz_changesmore.salaryid);
        map.put("type",gz_changesmore.type);
        Rpc({functionId:'GZ00000074',timeout:100000,success:gz_changesmore.getDetailTableOk},map);
    },
    //加载明细表
    getDetailTableOk:function(form, action){
        var result = Ext.decode(form.responseText);
        if(result.succeed){
            var store = Ext.create('Ext.data.Store', {
                fields:Ext.decode(result.fields),
                proxy:{
                    type: 'transaction',
                    functionId:'GZ00000074',
                    extraParams:{
                        salaryid:gz_changesmore.salaryid,
                        appdate:gz_changesmore.appdate,
                        imodule:gz_changesmore.imodule,
                        count:gz_changesmore.count,
                        type:gz_changesmore.type,
                        selectID:gz_changesmore.selectID,
                        fieldItem:gz_changesmore.fieldItem
                    },
                    reader: {
                        type: 'json',
                        totalProperty:'totalCount',
                        root: 'data'
                    }
                },
                pageSize:15,
                remoteSort:true,
                autoLoad:true
            });
            var mainPanel = Ext.create('Ext.grid.Panel', {
                store: store,
                columnLines:true,
                rowLines:true,
                layout:'fit',
                bodyBorder:false,
                columns: Ext.decode(result.column),
                bbar:{
                    xtype:'pagingtoolbar',
                    store:store,
                    displayInfo:true
                }
            });
            if(!Ext.isEmpty(Ext.getCmp('detailWin'))){
                Ext.getCmp('detailWin').destroy();
            }
            var vs = Ext.getBody().getViewSize();
            windet = Ext.widget("window",{
                title:gz.label.datatable+gz_changesmore.fieldName,  //数据明细
                height:vs.height,
                width:vs.width-530,
                x:530,
                id:'detailWin',
                layout:'fit',
                modal:false,
                bodyBorder:false,
                closeAction:'destroy',
                bodyStyle: 'background:#ffffff;',
                resizable:false,
                listeners:{
                    'beforeclose':function(){
                        gz_changesmore.haveWin = false;
                    }
                },
                tbar:{
                    xtype:'toolbar',
                    height:32,
                    padding:0,
                    margin:0,
                    border:false,
                    items:[{
                        xtype:'button',
                        text:gz.label.outExcel,//导出excel
                        handler:gz_changesmore.exportDetailData
                    }]
                },
                items:[mainPanel]
            });
            gz_changesmore.haveWin=true;
            windet.show();
        }
    },

    //主页面导出
    exportData:function(flag){
        Ext.MessageBox.wait("正在导出，请稍候...", "等待");
        var map = new HashMap();
        map.put("salaryid",gz_changesmore.salaryid);
        map.put("appdate",gz_changesmore.appdate);
        map.put("imodule",gz_changesmore.imodule);
        map.put("count",gz_changesmore.count);
        map.put("type",gz_changesmore.type);
        map.put("selectID",gz_changesmore.selectID);
        map.put("changeflags","0");
        Rpc({functionId:'GZ00000072',success:function(form,action){
                var result = Ext.decode(form.responseText);
                var fileName = getDecodeStr(result.fileName);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
                Ext.MessageBox.close();
            }},map);
    },
    //导出明细表
    exportDetailData:function(flag){
        Ext.MessageBox.wait("正在导出，请稍候...", "等待");
        var map = new HashMap();
        map.put("salaryid",gz_changesmore.salaryid);
        map.put("appdate",gz_changesmore.appdate);
        map.put("imodule",gz_changesmore.imodule);
        map.put("count",gz_changesmore.count);
        map.put("type",gz_changesmore.type);
        map.put("selectID",gz_changesmore.selectID);
        map.put("changeflags","1");//主页面导出
        map.put("fieldItem",gz_changesmore.fieldItem);
        Rpc({functionId:'GZ00000072',success:function(form,action){
                var result = Ext.decode(form.responseText);
                var fileName = getDecodeStr(result.fileName);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
                Ext.MessageBox.close();
            }},map);
    }

});

