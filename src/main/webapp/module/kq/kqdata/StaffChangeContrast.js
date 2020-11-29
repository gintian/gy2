/**
 * 考勤人员数据对比
 */
Ext.define("KqDataURL.StaffChangeContrast",{
    requires:['EHR.extWidget.proxy.TransactionProxy'],
    scheme_id:"",//考勤方案ID（加密） 必须
    kq_duration:"",//考勤期间 必须
    kq_year:"",//考勤年份 必须
    orgId:"",//机构id（加密）  不必须
    inflag:"",//进入方式
    constructor:function (config)
    {
        var me = this;
        Ext.apply(this,config);
        //唯一标识名称
        me.onlyFieldName = "";
        me.pageSize = 20;
        me.count1 = 0;//新增人员数
        me.count2 = 0;//减少人员数
        me.showNbase = true;//默认显示人员库标识
        var showflag = me.init();
        if(showflag){
        	me.createStyleCss();
        	me.createMainView();
        }
    },
    /**
     * 自定义样式
     */
    createStyleCss:function(){
        if (!Ext.util.CSS.getRule(".add_btn_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".add_btn_cls_haosl{" +
                "font-family: 微软雅黑,宋体,tahoma,arial,verdana,sans-serif;font-weight:bold;" +
                "font-size:13px;height:35px;padding:10px 5px 0 5px;cursor:pointer;top:10px;left:5px !important;}");
        }
        if(!Ext.util.CSS.getRule(".selected_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".selected_cls_haosl{color:#3A9CFF;font-weight:bold;border-bottom:3px solid #3A9CFF;}");
        }
        if(!Ext.util.CSS.getRule(".over_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".over_cls_haosl{color:#3A9CFF;font-weight:bold;}");
        }
        if(!Ext.util.CSS.getRule(".lose_btn_cls_haosl")) {
            Ext.util.CSS.createStyleSheet(".lose_btn_cls_haosl{" +
                "font-family: 微软雅黑,宋体,tahoma,arial,verdana,sans-serif;font-weight:bold;" +
                "font-size:13px;height:35px;padding:10px 5px 0 5px;cursor:pointer;top:10px;left:110px !important;}");
        }
    },
    /*创建主页面*/
    createMainView:function ()
    {
        var me = this;
        var bool = (me.count1==0 && me.count2>0) ? true : false;
        Ext.create("Ext.window.Window",{
            id : 'staffWin',
            width :800,
            height :500,
            resizable:false,
            titleAlign:'right',
            layout:'border',
            listeners:{
            	beforeclose:function(){
            		Ext.showConfirm(kq.datamx.msg.staffchange,function(flag){
                        if (flag =="yes"){
                        	Ext.getCmp("staffWin").destroy();
                        }
                      
                    });
            		return false;
                 	}
                 
            },
            fbar:[
                '->',
                {
                	//确定按钮增加背景色，吸引操作人员注意
                	xtype:'tbtext',
                	html:'<button style="background:#3A9CFF;color:white;width:75px;height:100%;top:1px;padding: 2px 2px 2px 2px;border-width: 1px;border-color: #c5c5c5;border-style: solid;cursor:pointer ">'+kq.button.ok+'</button>',
                	listeners:{
                		click:{
                			element:'el',
                			fn:function(){
                				 var addBtn = Ext.getCmp("addBtn");
                                 //点确定时判断执行的是什么操作
                                 if (addBtn.hasCls("selected_cls_haosl")){
                                     me.changeStaffs("add");
                                 }
                                 else{
                                     me.changeStaffs("del");
                                 }
                			}
                		}
                	}/*
                    text:kq.button.ok,	
                    handler:function ()
                    {
                        var addBtn = Ext.getCmp("addBtn");
                        //点确定时判断执行的是什么操作
                        if (addBtn.hasCls("selected_cls_haosl")){
                            me.changeStaffs("add");
                        }
                        else{
                            me.changeStaffs("del");
                        }
                    }
                    */
                },{
                    text:kq.button.cancle,
                    handler:function ()
                    {
                        Ext.getCmp("staffWin").close();
                    }
                },'->'
            ],
            tools:[
                {
                    xtype:'tbtext',
                    id:'addBtn',
                    baseCls:'add_btn_cls_haosl',
                    cls:(bool ? '' : 'selected_cls_haosl'),
                    overCls:'over_cls_haosl',
                    html:kq.datamx.label.newstaff.replace("{0}",me.count1),
                    listeners:{
                        click:{
                            element:'el',
                            fn:function(){
                                var addBtn = Ext.getCmp("addBtn");
                                var loseBtn = Ext.getCmp("loseBtn");
                                if (addBtn.hasCls("selected_cls_haosl")) {
                                    return;
                                }
                                loseBtn.removeCls("selected_cls_haosl");
                                addBtn.addCls("selected_cls_haosl");
                                me.loadStore("add");
                            }
                        }
                    }
                },
                {
                    xtype:'tbtext',
                    id:'loseBtn',
                    baseCls:'lose_btn_cls_haosl',
                    cls:(bool ? 'selected_cls_haosl' : ''),
                    overCls:'over_cls_haosl',
                    margin:'0 0 0 10',
                    html:kq.datamx.label.losedstaff.replace("{0}",me.count2),
                    listeners:{
	                    click:{
	                        element:'el',
	                        fn:function(){
	                            var addBtn = Ext.getCmp("addBtn");
	                            var loseBtn = Ext.getCmp("loseBtn");
	                            if (loseBtn.hasCls("selected_cls_haosl")) {
	                                return;
	                            }
	                            addBtn.removeCls("selected_cls_haosl");
	                            loseBtn.addCls("selected_cls_haosl");
	                            me.loadStore("del");
	                        }
	                    }
                    }
                }
            ],
            modal:true,
            items:[me.createDataGrid(),
                {
                    //分页工具
                    xtype: 'pagingtoolbar',
                    id:'pagingtoolbarId',
                    width:'100%',
                    store:"staff_store",
                    style:'border-width:0 1px 1px 1px;',
                    region:'south',
                    displayInfo: true
                }
            ]
        }).show();
    },
    /**
     * 创建表格
     */
    createDataGrid:function()
    {
      var me = this;
      return Ext.create("Ext.grid.Panel",
            {
                id:'staffsGrid',
                region:'center',
                emptyText:'暂无人员数据！',
                selModel:"checkboxmodel",
                viewConfig:{
                    deferEmptyText:true
                },
                store:"staff_store",
                scrollable:true,
                columnLines:true,
                columns:[
                    {
                        text:kq.datamx.label.dbname,
                        dataIndex:'dbname',
                        hidden:!me.showNbase,
                        width:120
                    },{
                        text:kq.datamx.label.b0110,
                        dataIndex:'b0110',
                        width:130
                    },{
                        text:kq.datamx.label.e0122,
                        dataIndex:'e0122',
                        width:130
                    },
                    {
                        text:kq.datamx.label.e01a1,
                        dataIndex:'e01a1',
                        width:130
                    },{
                        text:kq.datamx.label.pname,
                        dataIndex:'a0101',
                        width:100
                    },{
                        text:me.onlyFieldName,
                        hidden:Ext.isEmpty(me.onlyFieldName),
                        dataIndex:'only_field',
                        width:130
                    }
                ]
            }
        );
    },
    /**
     * 初始化
     */
    init:function ()
    {
        var me = this;
        var json = {};
        json.type = "init";
        json.scheme_id = me.scheme_id;
        json.kq_duration = me.kq_duration;
        json.kq_year = me.kq_year;
        json.orgId = me.orgId;
        var map = new HashMap();
        var showflag = true;
        map.put("jsonStr",Ext.encode(json));
        Rpc({functionId:'KQ00021205',async:false,success:function(form){
            var res = Ext.decode(form.responseText);
            var data = Ext.decode(res.returnStr);
            var returnData  = data.return_data;
            if(data.return_code=="fail"){
                Ext.showAlert(data.return_msg);
                return;
            }
            me.onlyFieldName = returnData.onlyFieldName;
            me.count1 = returnData.totalCount1;
            me.count2 = returnData.totalCount2;
            // 若没有人员变动则不需要弹出增减窗口
            if(0==me.count1 && 0==me.count2){
            	showflag = false;
            	// 点击功能按钮才给出提示信息
            	if("0" != me.inflag){
            		Ext.showAlert(kq.dataAppeal.noEmpChangeInfo);
            	}
            	return showflag;
            }
            me.showNbase = returnData.showNbase;
        },scope:this},map);
        
        if(showflag)
        	me.getStaffsStoreSync(me.count1==0 && me.count2>0);
        
        return showflag;
    },
    /**
     * flag add 新增 del 减少
     * @param flag
     */
    changeStaffs:function (flag) {
       var me = this;
       var selected = Ext.getCmp("staffsGrid").getSelectionModel().getSelection();
       if (selected.length==0) {
           Ext.showAlert(kq.datamx.msg.noselectedstaffs);
           return;
       }
       var msg = flag == "add"?kq.datamx.label.newstaff:kq.datamx.label.losedstaff;
       msg = msg.replace(" {0} 人","");
       Ext.showConfirm(kq.datamx.label.kqDataSp.confirmmsg.replace("{0}",msg),function(t){
           if (t != "yes") {
               return;
           }
           var json = {};
           json.type = "changeStaffs";
           json.scheme_id = me.scheme_id;
           json.kq_duration = me.kq_duration;
           json.kq_year = me.kq_year;
           var guidkeys = [];
           for (var i in selected) {
               var record = selected[i];
               guidkeys.push(record.get("guidkey"));
           }
           json.guidkeys = guidkeys;
           json.opration = flag;
           json.orgId = me.orgId;
           var map = new HashMap();
           map.put("jsonStr", Ext.encode(json));
           Rpc({
               functionId: 'KQ00021205', async: false, success: function (form) {
                   var res = Ext.decode(form.responseText);
                   var data = Ext.decode(res.returnStr);
                   if(data.return_code=="fail"){
                       Ext.showAlert(data.return_msg);
                       return;
                   }
                   else{
                       Ext.getCmp("staffWin").destroy();
                       //刷新日明细界面
                       var returnData  = data.return_data;
                       kqDataMx_me.changePerData = returnData.changePerData ||{};
                       // 如果是新增并有计算权限则再次计算一遍
                       if("add"==flag && "1"==kqDataMx_me.privs.computep){
                    	   kqDataMx_me.coverDataFlag=1;
                    	   kqDataMx_me.spOptDetail("compute_only");
                       }else{
                    	   kqDataMx_me.loadSpData();
                       }
                   }
               }
               , scope: this
           }, map);
       });
    },
    getStaffsStoreSync:function (flag) {
        var me = this;
        var json = {};
        json.type = "getStaffs";
        json.scheme_id = me.scheme_id;
        json.kq_duration = me.kq_duration;
        json.kq_year = me.kq_year;
        json.orgId = me.orgId;
        // 46675 如果没有新增人员有减少人员就默认加载减少人员  有新增人员数据则不变
        json.operation = flag ? "del" : "add";
        Ext.create("Ext.data.Store",{
            id:'staff_store',
            fields:["nbase","b0110","e0122","e01a1","a0101","only_field"],
            proxy:{
                type: 'transaction',
                functionId : 'KQ00021205',
                extraParams:{
                    jsonStr:Ext.encode(json)
                },
                reader: {
                    type : 'json',
                    transform:function(data){
                        var returnData =  Ext.decode(data.returnStr);
                        return returnData.return_data;
                    },
                    totalProperty:'totalCount',
                    rootProperty :'staffs'
                }
            },
            //点击页面列排序功能
           /* remoteSort:true,*/
            pageSize:me.pageSize,
            autoLoad:true,
            listeners:{
                load : function(store, records, successful, operation, eOpts ){
                    if (store.getCount()){
                       var staffsGrid = Ext.getCmp("staffsGrid");
                       if (staffsGrid){
                           staffsGrid.getSelectionModel().selectAll();
                       }
                    }
                }
            }

        })
    },
    loadStore:function (operation) {
        var me = this;
        var json = {};
        json.type = "getStaffs";
        json.scheme_id = me.scheme_id;
        json.kq_duration = me.kq_duration;
        json.kq_year = me.kq_year;
        json.orgId = me.orgId;
        json.operation = operation;

        var store = Ext.data.StoreManager.lookup("staff_store");
        var extraParams = {
            jsonStr: Ext.encode(json)
        };
        Ext.apply(store.proxy.extraParams, extraParams);
        store.load();
    }
});