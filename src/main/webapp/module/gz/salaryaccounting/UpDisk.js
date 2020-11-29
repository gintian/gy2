/**
 *银行报盘
 */
Ext.define('SalaryUL.UpDisk',{
    salaryid:'',//薪资id
    gz_updisk:'',
    arr:'',//银行列表的数组
    bankTable:'',//加载的页面
    bankid:'',//银行id
    underlineId:'',//下划线所在id
    itemsNum:'',//当前第一位的items下标
    constructor:function(config){
        gz_updisk=this;
        gz_updisk.isFirst = true;
        gz_updisk.salaryid = config.salaryid;
        gz_updisk.appdate = config.appdate;
        gz_updisk.model = config.model;// model =0 薪资发放的银行报盘 =1 薪资审批的银行报盘
        gz_updisk.bankid='';
        gz_updisk.count=config.count==null?"":config.count;
        arr = new Array();
        chkid=0;//代发银行位置信息文本框的id
        //定义银行列表需要加载的css
        //Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
        gz_updisk.init('');
    },
    init:function(bank_id){
        var tool=Ext.getCmp('toolbar');//处理跨页面id重复
        if(tool)
            tool.destroy();
        gz_updisk.bankid=bank_id;
        var mainPanel=Ext.create("Ext.panel.Panel",{
            border:false,
            bodyBorder:false,
            id:'mPanel',
            layout:'fit',
            width:'100%',
            height:'100%',
            closeAction:'destroy',
            tbar:new Ext.Toolbar({
                dock:'top',
                border:false,
                height:19,
                padding:0,
                items:[{
                    id:"toolbar",
                    border:false,
                    xtype:'panel',
                    layout:'hbox',
                    width:'100%',
                    align:'top'
                }]
            }),
            defaults: {
                bodyStyle: "background-color: #FFFFFF;"
            }
        })

        var vs = Ext.getBody().getViewSize();
        var window1=Ext.widget("window",{
            title:"银行报盘",
            // title:"银行报盘<a id='schemeSetting'  href='javascript:void(0)'  onclick='gz_updisk.schemeSetting()' style='position:absolute;right:2px;top:3px'><img src='/components/tableFactory/tableGrid-theme/images/Settings.png'  title='栏目设置'/></a>",
            height:vs.height,
            id:'mainWindow',
            width:vs.width,
            layout:'fit',
            scrollable:false,
            modal:true,
            border:false,
            closeAction:'destroy',
            items: [mainPanel]
        });
        window1.show();

        var map = new HashMap();
        map.put("salaryid",gz_updisk.salaryid);
        map.put("appdate",gz_updisk.appdate);
        map.put("opt",'1');
        map.put("bankid",bank_id);
        map.put("tp",'0');
        map.put("model",gz_updisk.model);
        map.put("count",gz_updisk.count);
        Rpc({functionId:'GZ00000121',success: function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    gz_updisk.tableId=result.tableId;
                    gz_updisk.createBankLable(bank_id);
                    gz_updisk.createTableOK(result,form,action);
                }else{
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    createBankLable:function(initBank_id){
        var toolbars=Ext.getCmp('toolbar').items;
        var bankPanel=Ext.getCmp('bankdiskpanelid');
        if(bankPanel)
            bankPanel.destroy();
        bankPanel=Ext.create("Ext.panel.Panel",{
            border:false,
            id:'bankdiskpanelid',
            width:Ext.getCmp('mainWindow').getWidth()-25//左右按钮占用30像素
        });
        toolbars.add(bankPanel);

        //代发银行列表的store
        var store = Ext.create('Ext.data.Store', {
            fields:['bankid','bankname'],
            storeId:'bankstore',
            proxy:{
                type: 'transaction',
                functionId:'GZ00000121',
                extraParams:{
                    salaryid:gz_updisk.salaryid,
                    appdate:gz_updisk.appdate,
                    opt:'0',
                    tp:'1'
                },
                reader: {
                    type: 'json',
                    root: 'banklist'
                }
            },
            autoLoad: true
        });


        store.on('load',function(){
            arr = new Array();
            gz_updisk.isFirst = false;
            var htmls='';
            gz_updisk.itemsNum=0;
            //循环添加银行
            for(var i=0;i<store.getCount();i++){
                var title = store.getAt(i).get("bankname");
                var bank_id = store.getAt(i).get("bankid");
                arr[i]=bank_id+'';
                var style = 'style="margin-left:10px;margin-right:10px;"';
//                if(bank_id == bankid){//添加下划线
//                    style = "color: green;text-decoration: underline"
//                }
                if(initBank_id=='')
                    initBank_id=bank_id;//若为空，则取第一个。
                if (bank_id==initBank_id){
                    style='style="margin-left:10px;margin-right:10px;text-decoration:underline;"';
                    underlineId=bank_id;//初始化下划线位置
                }
//                var name=store.getAt(i).get("bankname").trim().length!=''?store.getAt(i).get("bankname"):'&nbsp&nbsp&nbsp';//临时处理报盘名为空无法选中
                htmls+='<a href="javascript:gz_updisk.getBankDiskList('+bank_id+');" id="'+bank_id+'" '+style+'   >'+store.getAt(i).get("bankname")+'</a> ';
            }
            bankPanel.add(
                Ext.create('Ext.form.Label',{text:'报盘方案：'})
            );
            var bankLabel = Ext.create('Ext.form.Label',{
                id: 'bankLabel',
                html:htmls
            });

            bankPanel.add(bankLabel);
            gz_updisk.isMoveButton();
        });
    },
    isMoveButton:function(){//初始化左右移动按钮
        if(Ext.getCmp('bankdiskpanelid').getHeight()>18){//若报盘列表高度超过18像素，即有多行列表的情况下 出现左右移动按钮
            var b=Ext.getCmp('buttonPanel');
            if(b)
                b.destroy();
            var buttonPanel=Ext.create("Ext.panel.Panel",{
                border:false,
                layout:'hbox',
                id:'buttonPanel',
                width:15,
                align:'begin'
            });
            var buttonLeft = Ext.create('Ext.form.Label',{
                html:'<a href="javascript:gz_updisk.moveBankpanel(-1);"><img src="/images/new_module/upDiskLeft.png"/></a>',//-1为下标左移
                width:14,
                height:15
            });
            var buttonRight = Ext.create('Ext.form.Label',{
                html:'<a href="javascript:gz_updisk.moveBankpanel(1);"><img src="/images/new_module/upDiskRight.png"/></a>',//1为下标右移
                width:14,
                height:15
            });
            Ext.getCmp('bankdiskpanelid').insert(1,buttonLeft);
            buttonPanel.add(buttonRight);

            Ext.getCmp('toolbar').insert(buttonPanel);
        }
    },
    moveBankpanel:function(type){//移动工具栏报盘名称 1向左移动（右箭头） -1向右移动（左箭头）
        var itemslength=arr.length;
        if(gz_updisk.itemsNum+type<0||gz_updisk.itemsNum+type>(itemslength-1))
            return;
        if(type<0){
            gz_updisk.itemsNum=gz_updisk.itemsNum+type;
            document.getElementById(arr[gz_updisk.itemsNum]).style.display="inline";//左移 显示id为arr中前一项的a标签
        }
        else{
            document.getElementById(arr[gz_updisk.itemsNum]).style.display="none";//右移 隐藏id为arr中当前项a标签
            gz_updisk.itemsNum+=type;
        }
    },
    createTableOK:function(result,form,action){

        var conditions=result.tableConfigUpDisk;
        var fieldsize = result.fieldsize;
        bankid=result.bankid;
        var obj = Ext.decode(conditions);
        obj.openColumnQuery = true;
        bankTable = new BuildTableObj(obj);
        var mainPanel = bankTable.getMainPanel();

        //手动加载查询控件
        var map = new HashMap();
        map.put("salaryid",gz_updisk.salaryid);
        map.put("appdate",gz_updisk.appdate);
        map.put("bankid",bankid);
        map.put("model",gz_updisk.model);
        map.put("subModuleId","salaryaccount_updisk_00000001");
        map.put("opt","1");
        map.put("tp","2");
        map.put("count",gz_updisk.count);
        var SearchBox = Ext.create("EHR.querybox.QueryBox",{
            hideQueryScheme:false,
            emptyText:result.lookStr,
            subModuleId:result.tableId,
            customParams:map,
            funcId:"GZ00000121",
            fieldsArray:result.fieldsArray,
            success:function(){gz_updisk.restore()}//重新加载数据列表
        });
        var toolBar = Ext.getCmp("salaryaccountingupdisk_toolbar");
        toolBar.add(SearchBox);


//    	var params = new Object();
//		params.salaryid=gz_updisk.salaryid;
//		params.appdate=gz_updisk.appdate;
//		params.bankid=bankid;
//		params.model=gz_updisk.model;
//		params.opt="1";
//		params.tp="2";
//		params.subModuleId="salaryaccount_updisk_00000001";
//		Ext.getCmp("salaryaccountingupdisk_querybox").setCustomParams(params);

        Ext.getCmp("mPanel").add(mainPanel);
        if(fieldsize==1&&bankid!='0'){
            Ext.showAlert("该银行模板未设置代发银行要求的数据内容");
        }
    },
    //点击银行列表时，选中银行加下划线，并在列表中展示此银行模板下的报盘数据
    getBankDiskList:function(bankid){
        if(document.getElementById(underlineId))
            document.getElementById(underlineId).style.cssText='margin-left:10px;margin-right:10px;';
        document.getElementById(bankid).style.cssText='margin-left:10px;margin-right:10px;text-decoration:underline';
        underlineId=bankid;
//        for(var i in arr){
//            Ext.getCmp(arr[i]+'').removeCls("scheme-selected-cls");
//        }
//        Ext.getCmp(id).addCls('scheme-selected-cls');
        gz_updisk.bankid=bankid;
        var map = new HashMap();
        map.put("salaryid",gz_updisk.salaryid);
        map.put("appdate",gz_updisk.appdate);
        map.put("opt",'1');
        map.put("tp",'1');
        map.put("model",gz_updisk.model);
        map.put("bankid",bankid+'');
        map.put("count",gz_updisk.count);
        var mask = new Ext.LoadMask({
            target : Ext.getCmp('mPanel')
        });
        mask.show();
        Rpc({functionId:'GZ00000121',success: function(form,action){
                var result = Ext.decode(form.responseText);

                if(result.succeed){
                    bankTable.getMainPanel().destroy();
                    gz_updisk.createTableOK(result,form,action);
                }else{
                    Ext.showAlert(result.message);
                }
                mask.hide();
            }},map);
    },
    //重新加载模板主页的方法
    restore:function(response){
        var store = Ext.data.StoreManager.lookup('salaryaccountingupdisk_dataStore');
        store.reload();
    },
    //新增模板窗口
    addBankTemplate:function(){
        bank_id='';
        type='0';
        gz_updisk.addoreditBankTemplate(type,bank_id);
    },
    //编辑模板窗口
    editBankTemplate:function(){
        bank_id=bankid;
        type='1';
        gz_updisk.addoreditBankTemplate(type,bank_id);
    },
    //新增或编辑模板窗口
    addoreditBankTemplate:function(type,bank_id){
        var store = Ext.create('Ext.data.Store', {
            fields:['itemdesc','itemid','itemtype','itemlength','format','bank_id','item_id','norder'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000122',
                extraParams:{
                    salaryid:gz_updisk.salaryid,
                    bankid:bank_id,
                    opt:'1',
                    type:type//=1编辑 =0新增
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        store.load();

        //代发银行要求的数据内容
        var panel = Ext.create('Ext.grid.Panel', {
            width:790,
            height:250,
            store:store,
            margin:'5,0,0,0',
            id:'panelId',
            selType: "checkboxmodel",
            columnLines:true,
            rowLines:true,
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            viewConfig:{
                plugins:{
                    ptype:'gridviewdragdrop',
                    dragText:common.label.DragDropData
                },
                listeners: {
                }
            },
            enableDragDrop: true,
            dropConfig: {
                appendOnly:true
            },
            columns: [{
                text: '栏目名称',
                dataIndex: 'itemdesc',
                id:'itemdesc',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                align : 'center',
                editor:{
                    xtype:'textfield',
                    allowBlank:false
                },
                flex:10
            },{
                text: '数据来源',
                dataIndex: 'itemid',
                id:'itemid',
                align : 'center',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:3
            },{
                text: '数据类型',
                dataIndex: 'itemtype',
                id:'itemtype',
                align : 'center',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:3
            },{
                text: '输出长度',
                dataIndex: 'itemlength',
                id:'itemlength',
                align : 'center',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                editor:{
                    xtype:'textfield'
                },
                flex:3
            },{
                text: '输出格式',
                dataIndex: 'format',
                id:'format',
                align : 'center',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                editor:{
                    xtype:'textfield'
                },renderer:function(value){//add by xiegh on 20171108 bug29423
                    return '<label>'+value+'</label>';
                },
                flex:6
            },{
                text: '模板id',
                dataIndex: 'bank_id',
                id:'bank_id',
                hidden:true,
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:5
            },{
                text: '顺序',
                dataIndex: 'item_id',
                id:'item_id',
                hidden:true,
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:5
            },{
                text: '顺序',
                dataIndex: 'norder',
                id:'norder',
                hidden:true,
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:5
            }],
            listeners:{
                'render':function(){
                    //获取代发银行位置等信息
                    var map = new HashMap();
                    map.put("salaryid",gz_updisk.salaryid);
                    map.put("bankid",bank_id);
                    map.put("opt",'0');
                    map.put("type",'0');
                    Rpc({functionId:'GZ00000122',success:gz_updisk.getCheckAndFormat },map);
                },
                'validateedit':function(editor,e){
                    var value=e.value;
                    if(e.field!='format'||value==''){
                        return;
                    }
                    var itemid=e.record.get('itemid');
                    var data=store.query('itemid',itemid);
                    var itemType=data.items[0].data['item_type'];
                    //数字
                    if(itemType=='N'){

                        if(!new RegExp(/^\"?[^\"]*\"?[0#,]*\.?[0#]+%?!?\"?[^\"]*\"?$/).test(value)){//验证正浮点数，末尾可加"!%"   \"?[^\"]*\"?匹配有双引号的
                            Ext.showAlert('格式错误！请使用正确格式，例如：0.00或#.##。');
                            e.cancel=true;//取消编辑
                        }
                    }
                    //日期
                    if(itemType=='D'){
                        if(!new RegExp(/^[yY.MmDd年月]*$/).test(value)){//仅可使用ymd.四个字符
                            Ext.showAlert('格式错误！请使用正确格式，例如：yyyy.mm.dd或yyyymmdd。');
                            e.cancel=true;
                        }
                    }
                    //字符
                    if(itemType=='A'){
                        if(!new RegExp(/^[@&<>]\S?$/).test(value)){//验证字符开头
                            Ext.showAlert('格式错误！请使用正确格式，字符格式仅支持@、&、<、>。');
                            e.cancel=true;
                        }
                    }

                }
            },
            tbar: [
                {
                    text:'增加',
                    listeners:{
                        'click':function(){
                            gz_updisk.getSelectItemList();
                        }
                    }
                },{
                    text:'删除',
                    listeners:{
                        'click':function(){
                            var record = Ext.getCmp('panelId').getSelectionModel().getSelection();
                            if(record==''){
                                Ext.showAlert("请选择一个输出项目!");
                                return;
                            }
                            Ext.showConfirm('是否删除所选输出项目？', function(button, text) {
                                if (button == "yes") {
                                    Ext.getCmp('panelId').getStore().remove(record);
                                }
                            },this);


                        }
                    }
                }
            ]

        });
        //代发银行位置信息combo的store
        var chkStore = Ext.create('Ext.data.Store',{
            fields:['id','desc'],
            data:[{'id':'0','desc':'无'},{'id':'1','desc':'首行'},{'id':'2','desc':'末行'}]
        });
        //银行标志位置的panel
        var chkPanel = Ext.create('Ext.panel.Panel',{
            width:790,
            height:95,
            margin:'5,0,0,0',
            id:'chkPanel',
            layout:'column',
            autoScroll:true,
            tbar: [{
                text:'增加',
                listeners:{
                    'click':function(){
                        var textfield = Ext.create('Ext.form.field.Text',{
                            width:150,
                            id:chkid+'chkid',
                            style:'margin-right:3px;margin-top:3px;margin-left:3px;'
                        });
                        chkid++;
                        Ext.getCmp('chkPanel').insert(textfield);
                    }
                }
            },{
                text:'删除',
                listeners:{
                    'click':function(){
                        if(Ext.isDefined(Ext.getCmp(chkid-1+'chkid'))){
                            Ext.getCmp(chkid-1+'chkid').destroy();
                        }
                        if(chkid==0){
                            return;
                        }
                        chkid--;
                    }
                }
            }
            ]
        });
        var win = Ext.create('Ext.Window',{
            title:type==0?'新建银行报盘':'编辑银行报盘',
            width:810,
            height:540,
            border:false,
            bodyBorder:false,
//			alwaysOnTop:true,
            resizable:false,
            modal: true,
            items:[{
                xtype:'panel',
                margin:'5,0,0,0',
                layout:'column',
                height:25,
                border:false,
                items:[{
                    xtype:'textfield',
                    fieldLabel:'银行名称',
                    labelWidth:60,
                    id:'bankname'
                },{
                    xtype:'radiogroup',
                    name:'rd',
                    id:'rd',
                    //border:false,
                    labelAlign:'left',
                    labelWidth:50,
                    labelSeparator:null,
                    style:'margin-left:20px',
                    items:[
                        { boxLabel: '私有', name: 'rd',id:'rd1', inputValue: '1',width:75,checked:true},
                        { boxLabel: '共享', name: 'rd',id:'rd2', inputValue: '0',width:75 }]
                }]
            },{xtype:'tbfill',style:'margin-top:5px;margin-bottom:5px;border-bottom:1px dashed #C5C5C5'}
                ,{
                    xtype:'label',
                    margin:'5,0,0,0',
                    text:'设置代发银行要求的数据内容'
                },panel,{xtype:'tbfill',style:'margin-top:10px;margin-bottom:5px;border-bottom:1px dashed #C5C5C5'},{
                    xtype:'panel',
                    margin:'5,0,0,0',
                    layout:'column',
                    border:false,
                    items:[{
                        xtype:'label',
                        margin:'5,0,0,0',
                        text:'设置代发银行标志位置'
                    },{
                        xtype:'combo',
                        labelAlign:'right',
                        width:90,
                        margin:'3,5,3,5',
                        store:chkStore,
                        displayField:'desc',
                        valueField:'id',
                        editable:false,
                        labelSeparator:null,
                        value:'1',
                        id:'chkCombo'
                    },{
                        margin:'4,0,0,0',
                        xtype:'label',
                        text:'输出内容'
                    }]
                },chkPanel],
            bbar:[{xtype:'tbfill'
            },{
                text:'确定',
                style:'margin-right:5px',
                listeners:{
                    'click':function(){
                        //取得代发银行需要的内容列表
                        var selectedFieldList = new Array();
                        selectedFieldList=gz_updisk.getSelect(Ext.getCmp('panelId'));
                        var bankCheck=Ext.getCmp('chkCombo').getValue();
                        var bankFormat='';
                        for(var i=0;i<chkid;i++){
                            bankFormat+=Ext.getCmp(i+'chkid').getValue()+'`';
                        }
                        bankFormat=bankFormat.substring(0,bankFormat.length-1);
                        var scope=Ext.getCmp('rd').getChecked()[0].inputValue;
                        var bankname=Ext.getCmp('bankname').getValue();
                        if(trim(bankname)==''){
                            Ext.showAlert("银行名称不能为空，请输入正确的银行名称!");
                            return;
                        }
                        if(gz_updisk.fucCheckLength(bankname)>30){
                            Ext.showAlert("名称长度过长！");
                            return;
                        }
                        if(selectedFieldList.length==0){
                            Ext.showAlert("未设置代发银行要求的数据内容!");
                            return;
                        }
                        var map = new HashMap();
                        map.put("salaryid",gz_updisk.salaryid);
                        map.put("bankid",bank_id);
                        map.put("selectedFieldList",selectedFieldList);
                        map.put("bankCheck",bankCheck);
                        map.put("bankFormat",bankFormat);
                        map.put("scope",scope);
                        map.put("bankname",getEncodeStr(bankname));
                        map.put("type",type);
                        Rpc({functionId:'GZ00000123',success:function(){
                                win.close();
                                //bankTable.getMainPanel().destroy();
                                Ext.getCmp('mainWindow').destroy();
                                if(gz_updisk.scope!=scope)//若修改 是否共享 则初始化页面，否则打开上次点击页面
                                    bank_id="";
                                gz_updisk.init(bank_id);
                            } },map);
                    }
                }
            },{
                text:'取消',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            },{xtype:'tbfill'}]
        });
        chkid=0;//初始化代发银行标识id
        win.show();
    },
    //为代发银行位置等赋值
    getCheckAndFormat:function(response){
        var value = response.responseText;
        var map = Ext.decode(value);
        var arr = map.map;
        var bankcheck = arr.bankcheck;
        var bankformat = arr.bankformat;
        var bank_name = arr.bank_name;
        var scope = arr.scope;
        gz_updisk.scope=scope;
        //给银行名称赋值
        if(bank_name!=null && bank_name.length>0){
            Ext.getCmp('bankname').setValue(bank_name);
        }
        //给scope赋值
        if(scope!=null&&scope.length>0){
            if(scope=='1'){
                Ext.getCmp('rd').items.get(0).setValue(true);
            }else{
                Ext.getCmp('rd').items.get(1).setValue(true);
            }
        }
        var array = new Array();
        if(bankformat!=null && bankformat.length>0){
            array = bankformat.split('`');
        }
        if(array.length>0){
            //将银行位置chengbox选中，并为combo赋值
            Ext.getCmp('chkCombo').setValue(bankcheck);
            chkid=0;
            //加载后台传输的银行标志位置信息
            for(var i=0;i<array.length;i++){
                chkid=i;
                var textfield = Ext.create('Ext.form.field.Text',{
                    value:array[i],
                    width:150,
                    id:chkid+'chkid',
                    style:'margin-right:3px;margin-top:3px;margin-left:3px;'
                });
                Ext.getCmp('chkPanel').insert(textfield);
            }
            chkid++;
        }
    },
    //新增代发银行要求的数据内容的窗口
    getSelectItemList:function(){
        var selectList = gz_updisk.getSelectChange(Ext.getCmp('panelId'));
        var store = Ext.create('Ext.data.Store', {
            fields:['itemdesc','itemid','itemtype','itemlength','format','isSelect'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000122',
                extraParams:{
                    salaryid:gz_updisk.salaryid,
                    bankid:bankid,
                    selectList:selectList,
                    opt:'2'
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        var sm = Ext.create('Ext.selection.CheckboxModel',{
            injectCheckbox:0,//checkbox位于哪一列，默认值为0
            mode:'multi',//multi,simple,single；默认为多选multi
            checkOnly:true,//如果值为true，则只用点击checkbox列才能选中此条记录
            allowDeselect:true,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
            enableKeyNav:false,
            stopSelection: false,
            selType : 'rowmodel'
        });  //选行模式

        var panel = Ext.create('Ext.grid.Panel', {
            width:290,
            height:330,
            store:store,
            id:'getSelectItemPanelId',
            selModel: sm,
            columnLines:true,
            rowLines:true,
            columns: [{
                text: '项目名称',
                dataIndex: 'itemdesc',
                sortable:false,
                menuDisabled:true,
                flex:5
            },{
                text:'id',
                dataIndex:'itemid',
                sortable:false,
                menuDisabled:true,
                hidden:true,
                flex:5
            },{
                text:'itemtype',
                dataIndex:'itemtype',
                sortable:false,
                menuDisabled:true,
                hidden:true,
                flex:5
            },{
                text:'itemlength',
                dataIndex:'itemlength',
                sortable:false,
                hidden:true,
                menuDisabled:true,
                flex:5
            },{
                text:'format',
                dataIndex:'format',
                sortable:false,
                menuDisabled:true,
                hidden:true,
                flex:5
            },{
                text:'是否选中',
                dataIndex:'isSelect',
                id:'isSelectId',
                sortable:false,
                menuDisabled:true,
                hidden:true,
                flex:5
            }],
            listeners:{
                'render':function(){
                    Ext.getCmp('getSelectItemPanelId').getStore().on('load',function(){
                        var count =  selectList.length;
                        if(count>0){
                            Ext.getCmp('getSelectItemPanelId').getSelectionModel().selectRange(0,count-1);
                        }
                    });
                }
            }

        });
        var win = Ext.create('Ext.Window',{
            title:'请选择输出项目',
            id:'getSelectItemId',
            width: 300,
            height:400,
            resizable: false,
//			alwaysOnTop:true,
            modal: true,
            border:false,
            items:[panel],
            bbar:[{xtype:'tbfill'},{
                text:'确定',
                style:'margin-right:5px',
                listeners:{
                    'click':function(){
                        gz_updisk.getSelectList(Ext.getCmp('getSelectItemPanelId'));
                    }
                }
            },{text:'取消',
                listeners:{
                    'click':function(){
                        Ext.getCmp('getSelectItemId').close();
                    }
                }
            },{xtype:'tbfill'}]

        });
        win.show();
    },
    getSelect:function (grid) { //获取选中grid的数据
        grid.getSelectionModel().clearSelections();
        grid.getView().refresh();
        grid.getSelectionModel().selectAll();
        var arr = new Array();
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            var selectMap = new HashMap();
            if(grid.getSelectionModel().getSelection()[i].get("itemid")!=null && grid.getSelectionModel().getSelection()[i].get("itemid").length>0){
                selectMap.put("itemdesc",grid.getSelectionModel().getSelection()[i].get("itemdesc"));
                selectMap.put("itemid",grid.getSelectionModel().getSelection()[i].get("itemid"));
                if(grid.getSelectionModel().getSelection()[i].get("itemtype")=='字符型'){
                    selectMap.put("item_type",'A');
                }
                if(grid.getSelectionModel().getSelection()[i].get("itemtype")=='数值型'){
                    selectMap.put("item_type",'N');
                }
                if(grid.getSelectionModel().getSelection()[i].get("itemtype")=='日期型'){
                    selectMap.put("item_type",'D');
                }
                selectMap.put("itemlength",grid.getSelectionModel().getSelection()[i].get("itemlength"));
                selectMap.put("format",grid.getSelectionModel().getSelection()[i].get("format"));
                selectMap.put("bank_id",grid.getSelectionModel().getSelection()[i].get("bank_id"));
                selectMap.put("item_id",grid.getSelectionModel().getSelection()[i].get("item_id"));
                selectMap.put("norder",i+1);
                arr[i]=selectMap;
            }
        }
        return arr;
    },
    getSelectChange:function (grid) { //获取选中grid的数据
        Ext.getCmp('panelId').getSelectionModel().selectAll();
        var arr = new Array();
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            if(grid.getSelectionModel().getSelection()[i].get("itemid")!=null && grid.getSelectionModel().getSelection()[i].get("itemid").length>0){
                arr[i]=grid.getSelectionModel().getSelection()[i].get("itemid");
            }
        }
        return arr;
    },
    //判断数组中是否包含字符串的方法
    indexof:function (arr,flag){
        var arrflag='';
        for(var i in arr)
        {
            if(arr[i]==flag){
                arrflag=true;
                return arrflag;
            }
        }
        return arrflag;
    },
    getSelectList:function (grid) { //获取选中grid的数据
        var array = gz_updisk.getSelectChange(Ext.getCmp('panelId'));
        var arr = new Array();
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            var itemid=grid.getSelectionModel().getSelection()[i].get("itemid");
            if(itemid!=null && itemid.length>0&&!gz_updisk.indexof(array,itemid)){
                Ext.getCmp('panelId').getStore().add({
                    itemdesc:grid.getSelectionModel().getSelection()[i].get("itemdesc"),
                    itemid:itemid,
                    itemtype:grid.getSelectionModel().getSelection()[i].get("itemtype"),
                    itemlength:grid.getSelectionModel().getSelection()[i].get("itemlength"),
                    format:grid.getSelectionModel().getSelection()[i].get("format"),
                    bank_id:'2',
                    item_id:''
                });
            }
        }
        Ext.getCmp('getSelectItemId').close();
        return arr;
    },
    //生成报盘页面
    getBankList:function(){
        if(bankid==null||bankid==''||bankid=='0'){
            Ext.showAlert('请选择报盘名称！');
            return;
        }
        var win = Ext.create('Ext.Window',{
            title:'生成报盘',
            id:'getFileTypeId',
            width: 300,
            height:300,
//			alwaysOnTop:true,
            resizable: false,
            modal: true,
            border:false,
            closeAction:'destroy',
            items:[{
                xtype:'panel',
                border:false,
                items:[{
                    xtype:'radiogroup',
                    name:'rg',
                    id:'rg',
                    fieldLabel:'',
                    border:false,
                    layout:'fit',
                    labelAlign:'left',
                    labelWidth:50,
                    labelSeparator:null,
                    items:[
                        { boxLabel: '制表符分隔的文本文件', name: 'rg',id:'rg0', inputValue: '0',style:'margin-bottom:10px'},
                        { boxLabel: '空格分隔的文本文件', name: 'rg',id:'rg1', inputValue: '1' ,style:'margin-bottom:10px'},
                        { boxLabel: '无分隔的文本文件', name: 'rg', id:'rg2',inputValue: '2',style:'margin-bottom:10px',checked:true},
                        { boxLabel: 'Excel', name: 'rg', id:'rg3',inputValue: '3',style:'margin-bottom:10px'},
                        { boxLabel: '|分隔的文本文件', name: 'rg', id:'rg4',inputValue: '4',style:'margin-bottom:10px'},
                        { boxLabel: '逗号分隔的文本文件', name: 'rg', id:'rg5',inputValue: '5'} ]
                }]
            }],
            bbar:[{xtype:'tbfill'},{
                text:'确定',
                style:'margin-right:5px',
                listeners:{
                    'click':function(){
                        var fileType = Ext.getCmp('rg').getChecked()[0].inputValue;
                        var map = new HashMap();
                        map.put("salaryid",gz_updisk.salaryid);
                        map.put("bankid",bankid);
                        map.put("fileType",fileType);
                        map.put("model",gz_updisk.model);
                        var store= Ext.StoreMgr.get('bankstore');
                        var bankname=store.getAt(store.find('bankid',bankid)).data.bankname;
                        map.put("bankName",bankname);
                        Rpc({functionId:'GZ00000124',success:function(form,action){
                                var result = Ext.decode(form.responseText);
                                if(result.succeed){
                                	var fieldName = getDecodeStr(result.fileName);
                                    window.location.target="_blank";
                                    window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
                                    win.close();
                                }else{
                                    Ext.showAlert(result.message);
                                }
                            }},map);
                    }
                }
            },{text:'取消',
                listeners:{
                    'click':function(){
                        Ext.getCmp('getFileTypeId').close();
                    }
                }
            },{xtype:'tbfill'}]

        });
        win.show();
    },
    //删除银行模板
    deleteBankTemplate:function(){
        var store= Ext.StoreMgr.get('bankstore');
        var bankname=store.getAt(store.find('bankid',bankid)).data.bankname;
        Ext.showConfirm('确认要删除"'+bankname+'"报盘方案吗？',function(btn){
            if(btn=="yes"){
                Ext.getCmp('mainWindow').destroy();
                // 确认触发，继续执行后续逻辑。
                var map = new HashMap();
                map.put("salaryid",gz_updisk.salaryid);
                map.put("bankid",bankid);
                Rpc({functionId:'GZ00000125',success:function(){gz_updisk.init('')}},map);
            }
        },this);

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
    },
    //栏目设置按钮事件
    schemeSetting:function(){
        Ext.require("EHR.tableFactory.plugins.SchemeSetting", function() {
            var window = new EHR.tableFactory.plugins.SchemeSetting({
                subModuleId : gz_updisk.tableId,
                schemeItemKey:'',
                itemKeyFunctionId:'',
                viewConfig:{
                    publicPlan:true,
                    sum:false,
                    lock:true,
                    merge:false,
                    pageSize:'20'
                },
                closeAction:function(){
//						Ext.getCmp('mPanel').destroy();
//						gz_updisk.init(gz_updisk.bankid);
                    var map = new HashMap();
                    map.put("salaryid",gz_updisk.salaryid);
                    map.put("appdate",gz_updisk.appdate);
                    map.put("opt",'1');
                    map.put("bankid",gz_updisk.bankid);
                    map.put("tp",'0');
                    map.put("model",gz_updisk.model);
                    map.put("count",gz_updisk.count);
                    Rpc({functionId:'GZ00000121',success: function(form,action){
                            var result = Ext.decode(form.responseText);
                            if(result.succeed){
                                gz_updisk.tableId=result.tableId;
                                gz_updisk.createBankLable(gz_updisk.bankid);
                                gz_updisk.createTableOK(result,form,action);
                            }else{
                                Ext.showAlert(result.message);
                            }
                        }},map);
                }
            });
        });
    }

});
