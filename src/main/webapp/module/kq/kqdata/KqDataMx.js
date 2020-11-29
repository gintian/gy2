/**
 * 考清数据明细
 *
 * 注：此页面因为涉及到批量的显示和隐藏明细列，单纯的使用Ext的列隐藏功能，页面展示很慢。
 * 为了使效果能展示的更加快速，
 * 在使用表格控件的时候会创建两个表格，一个有日明细的表格和一个没有日明细的
 * 在显示|隐藏的时候，切换这两个表格就行了以Mx 和NoneMx 作为区分。
 *
 * @date 2018.10.31
 *
 * @author haosl
 */
Ext.define('KqDataURL.KqDataMx',{
    requires:['Ext.grid.selection.SpreadsheetModel',
        'Ext.grid.plugin.Clipboard',
        'KqDataURL.StaffChangeContrast','EHR.signatureFile.SignatureFile'
        ],
    kqYear:null,//年度
    kqDuration:null,//期间
    schemeId:null,//考勤方案id 加密
    orgId:null,//多个orgid不可编辑，单个orgId可以编辑  加密后多个时逗号分隔
    viewType:'',//0上报页面，1 审批页面
    operation:"1",//0 不可操作 //1 可以报批 //2 可以提交（归档到子集) //3 可新建
    hasNextApprover:'',//0=没有下级审批人，1=有下级审批人（上报页面无需传这个参数）
    optRole:'',//登录人的角色 1=人事处考勤员 2=人事处审核人 3=下级机构考勤员 4=下级机构审核人
    callBackFunc:undefined,
    fromflag:'page',//pt= 待办任务  ；page = 来自页面  ，目前只有代办发送在用，无需关注
    sp_flag:'', //审批状态 00：未创建 01：未提交 02：已提交 03：批准 06：归档 07:退回
    privs:null,// 功能授权
    hlwyjzx_flag:false,// 应急中心个性化标识
    currentUser:'',// 机构审核人 加密（nbase+a0100）
    otherParam:null,// 其他所需参数
    times:0,//点击次数
    approval_message:"0",//填写审批意见 0：不填写（默认），1：需要填写意见
    constructor:function(config){
        kqDataMx_me = this;
        this.itemCount=4;//日明细单元格控制最多可选4个班次或项目
        this.showMx="true";//是否显示日明细
        kqDataMx_me.classAndItemMap=[];
        this.selecedOrderdItem = [];
        this.replaceConfirmUsers = [];// 代确认 用户guidkey
        this.columnTtype = new HashMap();
        //变动岗人员信息
        this.changePerData = {};
        //考勤上报日明细内容显示 0 符号+名称；1仅符号；2 仅名称
        this.mxDetailType = this.getCookie("kqdatamx_detail_type");
        this.init(config);
        //创建页面
        this.getListView();
        // 校验
        this.isCheckData();
    },
    /**
     * 初始化参数
     */
    init:function(config){
        this.kqYear = config.kqYear;
        this.mainPanel = null;
        this.kqDuration = config.kqDuration;

        if(!Ext.isEmpty(this.kqDuration) && this.kqDuration.length==1){
            this.kqDuration = "0"+this.kqDuration;
        }
        this.sp_flag = config.sp_flag;
        this.kqStviewTypeatus = config.viewType;
        this.schemeId = config.schemeId;
        this.orgId = config.orgId||"";
        this.viewType = config.viewType || this.viewType;
        this.operation=config.operation||this.operation,//0 不可操作 //1 可以报批 //2 可以提交（归档到子集）
        this.hasNextApprover=config.hasNextApprover|| this.hasNextApprove,//0=没有下级审批人，1=有下级审批人
        this.optRole = config.optRole || this.optRole;//登录人的角色 1=人事处考勤员 2=人事处审核人 3=下级机构考勤员 4=下级机构审核人
        this.canEdit = (this.operation=="1" && this.orgId.split(",").length==1) || (this.operation=='3')//是否可以编辑表格
        this.idSuff = undefined;
        //this.lineHeight = this.itemCount*20+"px";
        this.callBackFunc = config.callBackFunc;
        this.fromflag = config.fromflag || this.fromflag;  //通过什么方式进入该页面
        //计算时是否覆盖手工修改的数据 =0 覆盖手工修改的数据 =1 不覆盖
        this.coverDataFlag = 1;
        //填写审批意见 0：不填写（默认），1：需要填写意见
        this.approval_message = 0;
        this.timerTask = null;
        this.cellData = null;
        this.createStyleCss();
        this.treeMenu = Ext.create('Ext.menu.Menu', {
            margin: '0 0 10 0',
            floating: true,
            id:'menuBar',
            renderTo: Ext.getBody(),
            items: [{
                id:'copyitem',
                text: kq.datamx.msg.copy+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+C",
                icon:'../../../../module/kq/images/shift_copy.png',
                handler:function () {
                    kqDataMx_me.getCellData('raw',false);
                }
            },{
                id:'cutitem',
                text: kq.datamx.msg.cut+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+X",
                icon:'../../../../module/kq/images/shift_cut.png',
                handler:function () {
                    kqDataMx_me.getCellData('raw',true);
                }
            },{
                id:'pasteitem',
                text: kq.datamx.msg.paste+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+V",
                icon:'../../../../module/kq/images/shift_paste.png',
                handler:function () {
                    kqDataMx_me.putCellData();
                }
            },{
                id:'deleteitem',
                text: kq.datamx.msg.deleteFlag+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Delete",
                icon:'../../../../module/kq/images/shift_delete.png',
                handler:function () {
                    kqDataMx_me.deleteCellData();
                }
            }]
        });

    /** 归到 pclist KQ00021204
        //同步日考勤打卡数据表结构(kq_day_detail),同时加载”是否需要员工确认"的参数 haosl add 2019.01.02
        var map = new HashMap();
        var json = {};
        json.type="initTables";
        json.scheme_id = this.schemeId;
        map.put("jsonStr", Ext.encode(json));
        Rpc({functionId:'KQ00021205',async:false,success:function(res){
            var result = Ext.decode(res.responseText);
            var data = Ext.decode(result.returnStr);
            if(data.return_code=="success"){
                //confirmFlag =1 支持员工确认 否则不支持
                kqDataMx_me.confirmFlag = data.return_data.confirmFlag;

            }else{
                Ext.showAlert(data.return_msg);
                return;
            }
        }},map);
        //启用员工确认功能
        if (this.confirmFlag == 1){
            //未创建 或 未提交并且未确认的，所有人可改；发起人登录时并且为退回或者未提交时可修改
            this.canEdit =  (this.optRole==3 && this.operation=="1")||this.operation=='3';
        }else{
            //不启用员工确认时，当前流程节点到登陆人的时候可改，否则不允许修改
            this.canEdit = (this.operation=="1" && this.orgId.split(",").length==1) || (this.operation=='3')//是否可以编辑表格
         }
     **/
    },
    /**
     * 创建样式
     */
    createStyleCss:function(){
        if(!Ext.util.CSS.getRule('.selectedCls')) {
            Ext.util.CSS.createStyleSheet(".selectedCls{cursor:pointer;border:1px solid #FF9292;position:relative;height:35px;width:65px;}");
            Ext.util.CSS.createStyleSheet(".selectedCls span:last-child{width:0;height:0;display:inline-block;"
                +"position:absolute;bottom:0;right:0;border-bottom:15px solid #ff6c6c;border-left:15px solid transparent;}");
            Ext.util.CSS.createStyleSheet(".selectedCls div:first-child{overflow:hidden;white-space:nowrap;"
                +"text-overflow:ellipsis;text-align:center;width:60px;height:35px;line-height:35px;}");
        }
        if(!Ext.util.CSS.getRule('.unselectedCls')) {
            Ext.util.CSS.createStyleSheet(".unselectedCls{cursor:pointer;border:1px solid #c5c5c5;position:relative;height:35px;width:65px;}");
            Ext.util.CSS.createStyleSheet(".unselectedCls span:last-child{display:hidden}");
            Ext.util.CSS.createStyleSheet(".unselectedCls div:first-child{width:60px;height:35px;"
                +"white-space:nowrap;overflow:hidden;text-overflow:ellipsis;text-align:center;line-height:35px;}");
        }
        if(!Ext.util.CSS.getRule('.listitemcls')) {
            Ext.util.CSS.createStyleSheet(".listitemcls{white-space:nowrap;overflow:hidden;text-overflow:ellipsis;"
                +"margin-bottom:2px;position:relative;width:100%;display:block;height:18px;text-align:left;}");
        }
        //复写选中列样式
        if(Ext.util.CSS.getRule('.x-grid-row .x-grid-cell-selected')){
            Ext.util.CSS.updateRule(".x-grid-row .x-grid-cell-selected","background-color","#ffefbb");
        }
        //行号背景样式
        Ext.util.CSS.createStyleSheet(".x-grid-cell-inner-row-numberer{background-image:none !important;background-color:transparent !important}","rowNumberCls");
        Ext.util.CSS.createStyleSheet(".x-column-header{cursor:default !important;}");
        Ext.util.CSS.createStyleSheet(".x-ssm-extender-drag-handle{height:8px !important;width:8px !important;background-color:#edaf00 !important;}","rowNumberCls");
        //创建序号样式
        if (!Ext.util.CSS.getRule('.item_xuhao')){
            Ext.util.CSS.createStyleSheet(".item_xuhao{width: 16px;height:16px;" +
                "background-image: url(/module/kq/images/sort_yuan.png);" +
                "position: absolute;top: -6px;left: 55px;color: white;" +
                "line-height: 16px;text-align:center;display:none;}");
        }
        Ext.util.CSS.createStyleSheet(".x-ssm-extender-mask{border:1px dashed #edaf00 !important;}",'x-ssm-extender-mask');
        if (!Ext.util.CSS.getRule('.no-eidt-gray')){
            Ext.util.CSS.createStyleSheet(".no-eidt-gray{background-color:#e2e2e2;}","no-eidt-gray");
        }
        
        if(Ext.util.CSS.getRule('.x-column-header-align-right .x-column-header-text')){
            Ext.util.CSS.updateRule(".x-column-header-align-right .x-column-header-text","margin-right","0px");
        }
    },
    /**
     * 考清数据列表
     */
    getListView:function(idSuffs){
        var me = this;
        var map = new HashMap();
        var json = {};
        json.type="pclist";
        json.kq_year=me.kqYear;
        json.kq_duration=me.kqDuration;
        json.status=me.kqStatus;
        json.scheme_id=me.schemeId;
        json.org_id=me.orgId;
        json.viewtype=me.viewType;
        json.canEdit = me.canEdit;
        map.put("jsonStr", Ext.encode(json));
        Rpc({functionId:'KQ00021204',async:false,success:function(form){
            var res = Ext.decode(form.responseText);
            // 50415 如果有报错信息 直接返回
            if(!res.succeed){
            	Ext.showAlert(res.message);
                return;
            }
            //confirmFlag =1 支持员工确认 否则不支持
            me.confirmFlag = res.confirmFlag;
            // 45783 为防止更改方案中是否上报参数  则 重新校验  是否有下级审批人
            me.hasNextApprover = res.hasNextApprover;
            //启用员工确认功能
            if (this.confirmFlag == 1){
                //未创建 或 未提交并且未确认的，所有人可改；发起人登录时并且为退回或者未提交时可修改
                this.canEdit =  (this.optRole==3 && this.operation=="1")||this.operation=='3';
            }else{
                //不启用员工确认时，当前流程节点到登陆人的时候可改，否则不允许修改
                this.canEdit = (this.operation=="1" && this.orgId.split(",").length==1) || (this.operation=='3')//是否可以编辑表格
            }
            me.otherParam = res.otherParam;
            /**
             * 应急中心个性化
             * 暂不考虑员工是否确认参数  直接覆盖掉
             */
            me.hlwyjzx_flag = ("1" == res.hlwyjzx_flag);
            // 应急中心个性化  操作人是方案考勤员 该单据既不是已批准的也不是归档的
            if(me.hlwyjzx_flag)
            	this.canEdit = me.hlwyjzx_flag && (this.optRole=='1') && !("03"==this.sp_flag || "06"==this.sp_flag)
            					&& ("2" != this.operation) && "2"!=me.otherParam.curr_user && !("02"==this.sp_flag && "2"==me.otherParam.curr_user);
            // 机构审核人加密（nbase+a0100）
            me.currentUser = res.currentUser;
            
            me.showMx = res.showMx;
            //初始时赋值表格submoduleId 的后缀
            if(!me.idSuff)
            	me.idSuff = me.showMx=="true"?"Mx":"NoneMx";
            
            me.schemeName = res.schemeName;
            me.changePerData = res.changePerData||{};
            me.privs = res.privs;
            //存放栏目设置
            var schemeBoxMx = Ext.widget('toolbar',{
                id:'kqdatamx_schemeSettingMx',
                hidden:(me.optRole!="1" || me.idSuff=="NoneMx"),//非人事处考勤员不显示栏目设置
                border:false
            });
            //存放栏目设置
            var schemeBoxNoneMx = Ext.widget('toolbar',{
                id:'kqdatamx_schemeSettingNoneMx',
                hidden:(me.optRole!="1" || me.idSuff=="Mx"),//非人事处考勤员不显示栏目设置
                border:false
            });

            var obj1 = undefined;
            if(res.tableConfigMx){
                obj1 = Ext.decode(res.tableConfigMx);
                obj1.openColumnQuery = true;//方案查询可以查询自定义指标
                obj1.beforeBuildComp = function(grid){
                    grid.tableConfig.enableColumnMove=false;
                    var columns = grid.tableConfig.columns;
                    for(var i in columns){
                        var c = columns[i];
                        c.hideable=false;
                        //得到列的类型
                        if(c.columns && c.columns.length>0){
                            for(var j in c.columns){
                                var cc = c.columns[j];
                                if (cc && cc.dataIndex && cc.columnType){
                                    me.columnTtype.put(cc.dataIndex,cc.columnType);
                                }
                            }
                        }else{
                            if (c && c.dataIndex && c.columnType) {
                                me.columnTtype.put(c.dataIndex, c.columnType);
                            }
                        }
                        //将周末与其他列区分
                        if(c.columns && c.columns.length==1 && c.columns[0] && me.isRiMxColumn(c.columns[0].dataIndex)){
                            //禁用列菜单
                            c.columns[0].menuDisabled = true;
                            if(c.columns[0].text==kq.datamx.label.zliu || c.columns[0].text == kq.datamx.label.zri){
                                c.style={background:'#FFFFFF',color:'#2dc02d',fontWeight:'bold'}
                                c.columns[0].style={background:'#FFFFFF',color:'#2dc02d',fontWeight:'bold'}
                            }
                        }
                    }
                    kqDataMx_me.overwirteGridConfig(grid);
                };
            }
            var obj2 = Ext.decode(res.tableConfigNoneMx);
            obj2.openColumnQuery = true;//方案查询可以查询自定义指标
            obj2.beforeBuildComp = function(grid){
                //复写表格默认行高的样式,因为创建了两个表格，只在最后一个创建的表格钱覆盖样式即可
            	// 58450 更改样式最高 高度
                Ext.util.CSS.createStyleSheet(
                    "#kqdatamxMx_tablePanel .x-grid-cell-inner,#kqdatamxNoneMx_tablePanel .x-grid-cell-inner{max-height:100px;"//+kqDataMx_me.lineHeight
                    +" !important;padding:1px 3px;","kqmxGridLineHeight");
                //对表格控件做个性化处理
                kqDataMx_me.overwirteGridConfig(grid);
            };
            me.classAndItemMap = res.classAndItems;
            me.allClassAndItemMap=res.allClassAndItems;
            var tableObj1 = undefined;
            var tableObj2 = undefined;
            if(me.showMx=="true"){
                tableObj1 = new BuildTableObj(obj1);
                tableObj1.setSchemeViewConfig({//配置栏目设置参数
                    sum:false,
                    autoSavePublic:true
                });
                this.mainPanel1 = tableObj1.mainPanel;
            }
            tableObj2 = new BuildTableObj(obj2);
            tableObj2.setSchemeViewConfig({//配置栏目设置参数
                sum:false,
                autoSavePublic:true
            });
            
            this.mainPanel2 =tableObj2.mainPanel;
            if(this.mainPanel1 && idSuffs !="NoneMx"){
                this.mainPanel1.setVisible(true);
                this.mainPanel2.setVisible(false);
            }else{
                this.mainPanel2.setVisible(true);
                if(this.mainPanel1){
                    this.mainPanel1.setVisible(false);
                }
            }
            this.tableObj1 = tableObj1;
            this.tableObj2 = tableObj2;
            var win = this.getMainWindow(schemeBoxMx,schemeBoxNoneMx);
            win.show();
        },scope:me},map);
    },
    /**
     * 创建主窗口
     */
    getMainWindow:function(schemeBoxMx,schemeBoxNoneMx){
        var me = this;
        var dataMxWin = Ext.create("Ext.window.Window",{
            maximized:true,
            id:'dataMxWin',
            layout:'fit',
            title:this.kqYear+kq.dataAppeal.year+this.kqDuration+kq.dataAppeal.month+"（"+this.schemeName+"）"+kq.datamx.label.table,
            autoScroll:true,
            tools:[schemeBoxMx,schemeBoxNoneMx],
            border:false,
            closable:false,
            resizable :false,
            items:me.showMx=="false"?[this.mainPanel2]:[this.mainPanel1,this.mainPanel2],
            listeners:{
            	beforeDestroy:function(){
            		// 关闭窗口之前 将考勤员置为代确认的员工 中已有确认待办的置为已办
            		if(kqDataMx_me.replaceConfirmUsers.length > 0){
            			kqDataMx_me.doReplaceConfirm();
            		}
            	},
                destroy:function(){
                    var mxDetailCombox = Ext.getCmp("mxDetailCombox");
                    if (mxDetailCombox){
                        Ext.getCmp("mxDetailCombox").destroy();
                    }
                },
                afterrender:function(){
                    //对按钮的处理操作
                    me.displayButtons("Mx");
                    me.displayButtons("NoneMx");
                    //自动保存事件
                    if(me.showMx=="true"){
                        var storeMx = Ext.data.StoreManager.lookup("kqdatamxMx_dataStore");
                        storeMx.on("update",function(store, record, operation, modifiedFieldNames, details){
                            if(operation!="edit")
                                return;
                            var obj = {};
                            var isSave = false;
                            var guidkey = record.get("guidkey");
                            var str = me.changePerData[guidkey]?me.changePerData[guidkey].join(","):"";
                            if (str.length>0){
                                str = ","+str+",";
                            }
                            // 原有数据
                            var modifiedArray = record.modified;
                            
                            for(var i in modifiedFieldNames){
                                var field = modifiedFieldNames[i];
                                // 原值
                                var modifiedValue = eval("modifiedArray."+field);
                                var oldEnableModifysValue = "";
                                // 校验是否存在不可编辑考勤项目
                                if(!Ext.isEmpty(kqDataMx_me.otherParam.enableModifys) && !Ext.isEmpty(modifiedValue)
                                		&& (","+kqDataMx_me.otherParam.enableModifys+",").indexOf(","+modifiedValue) != -1){
                                	var vals = modifiedValue.split(',');
                                    for(var i=0;i<vals.length;i++){
                                    	var val = vals[i];
                                    	if((","+kqDataMx_me.otherParam.enableModifys+",").indexOf(","+val+",") != -1 ){
                                    		oldEnableModifysValue += "," + val;
                                    	}
                                    }
                                }
                                // 修改后的值
                                var value = record.get(field);
                                var bool = true;
                                if(!Ext.isEmpty(oldEnableModifysValue)){
                                	var vals = oldEnableModifysValue.split(',');
                                	for(var i=0;i<vals.length;i++){
                                    	var val = vals[i];
	                                	if((","+value+",").indexOf(","+val+",") == -1){
	                                		bool = false;
	                                		break;
	                                	}
                                	}
                                }
                                // 58646 下拉复制时 如果覆盖不可编辑属性时直接跳出
                                if(!bool){
                                	continue;
                                }
                                
                                if(field == "confirm_"){
                                	var confirmVal = record.modified.confirm_;
                                	// 50085 修改的个数 如果是待确认 要覆盖 已确认直接返回，如果是修改完其他班次或单元格 需将已确认 置为 未确认
                                	var len = Ext.Object.getSize(record.modified);
                                	if(",0,2,".indexOf(","+confirmVal+",")==-1 && 1==len){
                                		continue;
                                	}
                                }
                                if((field=="confirm_" && ",0,2,".indexOf(","+value+",")==-1)
                                    ||field=="b0110"
                                    ||field=="e0122"
                                    ||field=="e01a1"
                                    ||field=="a0101"
                                    ||field=="only_field"
                                    //日明细列需要控制读写权限
                                    ||(me.isRiMxColumn(field) && str.length>0 && str.indexOf(","+field+",")==-1)){
                                    continue;
                                }
                                //已确认的不允许修改
                                if(field!="confirm_"){
                                    //显示日明细并且支持员工确认的时候，退回状态下，修改非确认列的内容时，需要自动将确认状态改为 “未确认”
                                    if (me.confirmFlag){
                                        //改为未确认
                                        if (record.get("confirm_")=="1" || record.get("confirm_")=="2"){
                                            record.set("confirm_","0");
                                        }
                                    }
                                }else{
                                    field = "confirm";
                                }
                                obj[field] = Ext.isEmpty(value)?"":value;
                                isSave = true;
                            }
                            if(isSave){
                                me.savekqDataMx(obj,record);
                            }else{
                                record.reject(false);
                            }
                        });
                    }
                    var storeNoneMx = Ext.data.StoreManager.lookup("kqdatamxNoneMx_dataStore");
                    storeNoneMx.on("update",function(store, record, operation, modifiedFieldNames, details){
                        if(operation!="edit")
                            return;
                        var obj = {};
                        var isSave = false;
                        for(var i in modifiedFieldNames){
                            var field = modifiedFieldNames[i];
                            var value = record.get(field);
                            if((field=="confirm_" && value=="1")
                                ||field=="b0110"
                                ||field=="e0122"
                                ||field=="e01a1"
                                ||field=="a0101"
                                ||(me.otherParam.confirmField.indexOf(field) != -1)){
                                continue;
                            }
                            if (field =="confirm_"){
                                field = "confirm";
                            }else{
                            	// 46598 不显示日明细时 修改非确认列的内容时，需要自动将确认状态改为 “未确认”
                                if (me.confirmFlag){
                                    //改为未确认
                                    if (record.get("confirm_")=="1" || record.get("confirm_")=="2"){
                                        record.set("confirm_","0");
                                    }
                                }
                            }
                            obj[field] = Ext.isEmpty(value)?"":value;
                            isSave = true;
                        }
                        if(isSave){
                            me.savekqDataMx(obj,record);
                        }else{
                            record.reject(false);
                        }
                    });
                    var mxDetailDiv = Ext.getDom("mxDetailDiv");
                    if (mxDetailDiv){
                        var states = Ext.create('Ext.data.Store', {
                            fields: ['type', 'name'],
                            data : [
                                {"type":"0", "name":kq.datamx.label.mxshowtype0},
                                {"type":"1", "name":kq.datamx.label.mxshowtype1},
                                {"type":"2", "name":kq.datamx.label.mxshowtype2}
                            ]
                        });
                        Ext.create("Ext.form.ComboBox",{
                            id:'mxDetailCombox',
                            store:states,
                            queryMode: 'local',
                            width:85,
                            displayField: 'name',
                            valueField: 'type',
                            editable:false,
                            renderTo:'mxDetailDiv',
                            listeners:{
                                afterrender:function (combo) {
                                    var selected = null;
                                    var store = combo.getStore();
                                    if (me.mxDetailType != null){
                                        selected = store.findRecord("type",me.mxDetailType)
                                    }else{
                                        selected = store.getAt(0);
                                    }
                                    if (store){
                                        combo.select(selected);
                                        combo.fireEvent("select", combo, selected);
                                    }
                                },
                                select:function (combo,records) {
                                    me.delCookie("kqdatamx_detail_type");
                                    // 存cookie需要设置有效期
                                    var Days = 30;
                                    var exp = new Date();
                                    var mxDetail = combo.getValue();
                                    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000 * 6);
                                    document.cookie = "kqdatamx_detail_type=" + mxDetail
                                        + ";expires=" + exp.toGMTString();
                                    me.mxDetailType = mxDetail;
                                    me.loadSpData();
                                }
                            }
                        });
                    }
                }
            }
        });
        return dataMxWin;
    },
    /**
     * 表格控件配置修改
     */
    overwirteGridConfig:function(grid){
        //修改表格的选中模式
        var me = this;
        grid.tableConfig.columns.unshift({xtype:'rownumberer',text:kq.label.rowNumberer,width:40});
        if(me.canEdit) {
            grid.tableConfig.plugins[0].clicksToEdit = 2;
            grid.tableConfig.selModel = {
                type: 'spreadsheet',
                pruneRemoved: false,
                columnSelect: true,
                allowDeselect: true,
                rowNumbererHeaderWidth:40,
                checkboxSelect:(me.optRole == '2' || me.optRole == '4') ? false : true,
                checkboxColumnIndex:'first'
            };
        }else{
            grid.tableConfig.disableSelection = true;
        }
        grid.tableConfig.viewConfig.columnLines=true;
        //grid.tableConfig.viewConfig.trackOver=false;
        grid.tableConfig.plugins[0].listeners={
            //校验是否能编辑
            beforeedit:me.checkCell
        };
        //只读时不增加下面的事件
        if(!me.canEdit)
            return;
        grid.tableConfig.listeners={
            afterrender:function(gridpanel){
                gridpanel.view.lockedView.ownerCt.el.dom.style.zIndex=100;
                document.onkeydown=function(e){
                    e = window.event?window.event:e;
                    var keyNum=window.event ? e.keyCode :e.which;
                    if (keyNum == Ext.event.Event.DELETE){
                        me.deleteCellData();
                    }
                }
            },
            //解决点击锁定列区域后无法启用右侧的列编辑插件 （属于Ext的bug,曲线救国吧）
            beforecellclick:function(view, td, cellIndex, record){
            	// 58348 复选框勾选失败 完善校验
                var columnid = view.grid.columnManager.columns[cellIndex].dataIndex;
                var guidkey = record.get("guidkey");
                var str = me.changePerData[guidkey]?me.changePerData[guidkey].join(","):"";
                if (str.length>0){
                    str = ","+str+",";
                }
                if(columnid =="b0110"
                    ||columnid =="e0122"
                    ||columnid =="e01a1"
                    ||columnid =="a0101"
                    ||(me.otherParam.confirmField.indexOf(columnid) != -1 && !Ext.isEmpty(columnid))
                    ||columnid =="only_field"
                    //日明细列需要控制读写权限
                    ||(me.isRiMxColumn(columnid) && str.length>0 && str.indexOf(","+columnid+",")==-1)){
                    //让表格重新获得下焦点 兼容IE和非IE
                    view.grid.el.dom.focus();
                    view.ownerGrid.view.normalView.el.dom.focus();
                    return false;
                }
            },
            selectionchange : function(grid,selection){
                var selModel = grid.getSelectionModel();
                if (!selection.selectedRecords) {
                    selModel.getSelected().eachCell(function (cellContext, cindex, rindex) {
                        var column = cellContext.column;
                        var dataIndex = column.dataIndex;
                        var store = grid.getStore();
                        var record = store.getAt(rindex);
                        var guidkey = record.get("guidkey");
                        var str = me.changePerData[guidkey] ? me.changePerData[guidkey].join(",") : "";
                        if (str.length > 0) {
                            str = "," + str + ",";
                        }
                        if (!me.canEdit
                            || dataIndex == "b0110"
                            || dataIndex == "e0122"
                            || dataIndex == "e01a1"
                            || dataIndex == "a0101"
                            || dataIndex == "only_field"
                            || (me.otherParam.confirmField.indexOf(dataIndex) != -1)
                            //日明细列需要做读写权限控制（变动部门）
                            || (me.isRiMxColumn(dataIndex) && str.length > 0 && str.indexOf("," + dataIndex + ",") == -1)) {
                            selModel.deselectAll(false);
                            return false;
                        }
                    });
                }
            },
            //表格添加鼠标右键事件
            cellcontextmenu : function(view,td,cellIndex,record ,tr,rowIndex,e){
                //禁用浏览器的右键相应事件
                e.preventDefault();
                e.stopEvent();
                if (!me.canEdit) {
                    return false;
                }
                var selModel = view.grid.getSelectionModel();
                //右键前没有选中的话禁用右键菜单
                if (!selModel.getSelected()){
                    me.disabledMenuItem('all',true);
                }else{
                    me.disabledMenuItem('all',false);
                    //剪切板没有值的话，禁用粘贴菜单
                    if (!me.cellData){
                        me.disabledMenuItem('pasteitem',true);
                    }else{
                        me.disabledMenuItem('pasteitem',false);
                    }
                }
                me.treeMenu.showAt(e.getXY());
            },
            beforecellcontextmenu:function(view,td,cellIndex,record){
                var dataIndex = view.grid.columnManager.columns[cellIndex].dataIndex;
                var guidkey = record.get("guidkey");
                var str = me.changePerData[guidkey]?me.changePerData[guidkey].join(","):"";
                if (str.length>0){
                    str = ","+str+",";
                }
                if(!me.canEdit
                    ||dataIndex =="b0110"
                    ||dataIndex =="e0122"
                    ||dataIndex =="e01a1"
                    ||dataIndex =="a0101"
                    ||dataIndex =="only_field"
                    ||(me.otherParam.confirmField.indexOf(dataIndex) != -1)
                    //日明细需要做读写权限控制（变动部门）
                    ||(me.isRiMxColumn(dataIndex) && str.length>0 && str.indexOf(","+dataIndex+",")==-1)
                ){
                    return false;
                }
                return true;
            },
            celldblclick:function(view,td,cellIndex,record ,tr,rowIndex,e){
                var columnid = view.grid.columnManager.columns[cellIndex].dataIndex;
                var guidkey = record.get("guidkey");
                var str = me.changePerData[guidkey]?me.changePerData[guidkey].join(","):"";
                if (str.length>0){
                    str = ","+str+",";
                }
                if(me.isRiMxColumn(columnid) && me.canEdit){
                    if(me.canEdit){
                        if (str.length==0 || (str.length>0 && str.indexOf(","+columnid+",")>-1)){
                            var column = view.grid.columnManager.columns[cellIndex];
                            //右键时选中列，打开选择班次|项目的窗口
                            me.openUpdateRiMxView(column.dataIndex,record);
                        }
                    }
                }
            },
            itemmouseenter:function (x,y,z,rowIndex) {
                if (me.canEdit && kqDataMx_me.confirmFlag==1){
                    kqDataMx_me.showOrHideConfirmIcon(rowIndex,true);
                }
            },
            itemmouseleave:function (x,y,z,rowIndex) {
                if (me.canEdit && kqDataMx_me.confirmFlag==1) {
                    kqDataMx_me.showOrHideConfirmIcon(rowIndex, false);
                }
            }
        }
        grid.tableConfig.plugins.push({ptype:'clipboard',
            system:'raw',
            formats: {
                raw: {
                    get: 'getCellData',
                    put: 'putCellData'
                }
            },
            //复写复制的方法
            getCellData:function(format,erase){
                me.getCellData(format,erase);
            },
            //复写粘贴的方法
            putCellData: function () {
                me.putCellData();
            }
        });
        grid.tableConfig.plugins.push('selectionreplicator');
    },
    /**
     *显示|隐藏 日明细
     * @returns
     */
    showMxClickFn:function(value){
        if(value=="0"){
            this.mainPanel1.setVisible(false);
            this.mainPanel2.setVisible(true);
            this.idSuff = "NoneMx";
        }else{
            this.mainPanel1.setVisible(true);
            this.mainPanel2.setVisible(false);
            this.idSuff = "Mx";
        }
        var schemeMx = Ext.getCmp("kqdatamx_schemeSettingMx");
        var schemeNone = Ext.getCmp("kqdatamx_schemeSettingNoneMx");
        if(schemeMx)
            schemeMx.setVisible(this.optRole=="1" && this.idSuff=="Mx");
        if(schemeNone)
            schemeNone.setVisible(this.optRole=="1" && this.idSuff=="NoneMx");
        this.loadSpData();
    },
    /**
     * 日明细单元格渲染
     */
    rMxDataRender:function(value,md,record,rowIdx,colIdx,store,view){
        var html = "";
        //设置单元格背景
        var columnId = view.grid.columns[colIdx].dataIndex;
        var guidkey = record.get("guidkey");
        var map = kqDataMx_me.changePerData;
        // 得到可以编辑日期列；map[guidkey]不存在map中则都可以编辑，若guidkey存在map中并且为空则表示都不可以编辑
        var q35Str = map[guidkey]?map[guidkey].join(","):"";
        if (q35Str.length>0 || map[guidkey]){
            q35Str = ","+q35Str+",";
            if(q35Str.indexOf(","+columnId+",")==-1){
                md.tdCls = "no-eidt-gray";
            }
        }
        if(Ext.isEmpty(value)){
            html = "<div style='width:100%;height:100%;";
            if(kqDataMx_me.canEdit){
                if (q35Str.length==0 || (q35Str.indexOf(","+columnId+",")>-1)) {
                    html += "cursor:pointer;' title='" + kq.datamx.label.rightkeyb + kq.datamx.label.windowtitle + "'/>"
                }else{
                    html+="'/>";
                }
            }else{
                html+="'/>";
            }
            return html;

        }
        var map = kqDataMx_me.allClassAndItemMap;
        html="<div style='width:100%;height:100%;line-height:normal !important;";
        var mouseEvent = "";
        if(kqDataMx_me.canEdit){
            if (q35Str.length==0 || (q35Str.indexOf(","+columnId+",")>-1)) {
                html+="cursor:pointer;' title='"+kq.datamx.label.rightkeyb+kq.datamx.label.windowtitle+"'>"
                mouseEvent = "onmouseover='kqDataMx_me.itemMouseOver(this,true)' onmouseout='kqDataMx_me.itemMouseOut(this,true)'";
            }else{
                html+="'>";
            }
        }else{
            html+="'>";
        }
        var vals = value.split(',');
        var guidkey = record.get("guidkey");
        for(var i=0;i<vals.length;i++){
            var isEnd = i==vals.length-1?true:false;
            for(var j in map){
                var id = map[j].id;
                if(id == vals[i]){
                    if(id.substring(0,1)=="C"){
                        var color =map[j].color;
                        //班次颜色未定义或者为白色时,字体默认显示黑色
                        if(color=="#FFFFFF" || Ext.isEmpty(color)){
                            color = "#000000";
                        }
                        var symbol = map[j].symbol;
                        var abbreviation = map[j].abbreviation;
                        var name = map[j].name;
                        html+="<span id='"+guidkey+"_"+rowIdx+"_"+columnId+"_"+id+"' class='listitemcls' " + mouseEvent + " style='text-align:left;font-size:12px;color:"+color+";"+(isEnd?"":"border-bottom:1px solid #ededed")+"'>"
                        //仅符号 没有符号时的显示优先级： 简称>名称
                        if (kqDataMx_me.mxDetailType=="1") {
                            if (!Ext.isEmpty(symbol)) {
                                html+="<span style='font-size:12px;'>"+symbol+"</span>";
                            } else {
                                html+=Ext.isEmpty(abbreviation) ? name : abbreviation;
                            }
                            //仅名称 显示优先级： 简称>名称
                        }else if (kqDataMx_me.mxDetailType=="2"){
                            html += Ext.isEmpty(abbreviation) ? name : abbreviation;
                        }else{
                            ////符号+名称 显示优先级： 简称>名称
                            if (!Ext.isEmpty(symbol)) {
                                html += "<span style='font-size:12px;'>"+symbol+"</span>&nbsp;"+(Ext.isEmpty(abbreviation) ? name : abbreviation);
                            } else {
                                html += Ext.isEmpty(abbreviation) ? name : abbreviation;
                            }
                        }

                    }else if(id.substring(0,1)=="I"){
                        var symbol = map[j].item_symbol;
                        var name = map[j].item_name;
                        var color = map[j].item_color;
                        //班次颜色未定义或者为白色时,字体默认显示黑色
                        if(color=="#FFFFFF" || Ext.isEmpty(color)){
                            color = "#000000";
                        }
                        html+="<span id='"+guidkey+"_"+rowIdx+"_"+columnId+"_"+id+"' class='listitemcls' " + mouseEvent + " style='text-align:left;color:"+color+";"+(isEnd?"":"border-bottom:1px solid #ededed")+"'>";
                        //仅符号 没有符号时显示名称
                        if (kqDataMx_me.mxDetailType=="1") {
                            if (!Ext.isEmpty(symbol)) {
                                html+="<span style='font-size:12px;'>"+symbol+"</span>";
                            } else {
                                html+=name;
                            }
                            //仅名称
                        }else if (kqDataMx_me.mxDetailType=="2"){
                            html += name;
                        }else{
                            ////符号+名称 显示优先级： 简称>名称
                            if (!Ext.isEmpty(symbol)) {
                                html += "<span style='font-size:12px;'>"+symbol+"</span>&nbsp;"+name;
                            } else {
                                html+=name;
                            }
                        }
                    }

                    if(kqDataMx_me.canEdit){
                        if (q35Str.length==0 || (q35Str.indexOf(","+columnId+",")>-1)) {
                        	// 增加参数控制 是否统计项功能 如果是统计项那么不允许编辑
                        	if((kqDataMx_me.otherParam.enableModifys + ",").indexOf(","+id+",") == -1){
                        		html += "<img onmouseover='kqDataMx_me.itemMouseOver(this,false)' onmouseout='kqDataMx_me.itemMouseOut(this,false)' onclick='kqDataMx_me.delItem(this,\"" + value + "\")' " +
                        		"id='imgdel_" + guidkey + "_" + rowIdx + "_" + columnId + "_" + id + "' src='/workplan/image/remove.png' " +
                        		"style='display:none;width:15px;height:15px;cursor:pointer;position:absolute;top:0px;right:0px;'/>";
                        	}
                        }
                    }
                    html+="</span>";
                }
            }
        }
        html+="</div>";
        return html;

    },
    /**
     * 打开修改日明细班次|项目的 窗口
     */
    openUpdateRiMxView:function(columnid,record){
        var me = this;
        //清空容器
        me.selecedOrderdItem = [];
        var map = me.classAndItemMap;
        var q35Val = record.get(columnid);
        if (q35Val && q35Val.length>0){
            var selectArr = q35Val.split(",");
            for(var i in selectArr){
                var id = selectArr[i];
                //判断之前选择过的班次或项目，是否还在方案中，否则不显示。
                for (var m in map){
                    if (map[m].id==id){
                        me.selecedOrderdItem.push(id);
                        break;
                    }
                }
            }
        }
        q35Val = ","+q35Val+",";
        if(map.length== 0){
            Ext.showAlert(kq.datamx.msg.noclassoritem);
            return;
        }
        var c_comps = [];
        var i_comps = [];
        
        for(var m in map){
            var clsName='unselectedCls';
            var id = map[m].id;
            if(!!q35Val && q35Val.indexOf(","+id+",")>-1){
                clsName='selectedCls';
            }
            if(id.substring(0,1) == "C"){
                var cname = Ext.isEmpty(map[m].abbreviation)?map[m].name:map[m].abbreviation;
                c_comps.push({
                    xtype:'container',
                    height: 35,
                    width: 65,
                    border:true,
                    html:'<div class="'+clsName+'" id="'+id+'" title="'+cname
                    +'" onclick="kqDataMx_me.changeSelected(this)"><div>'+cname+'</div><span id="'+id+'_xuhao" class="item_xuhao"></span><span></span></div>'
                });
            }else{
            	// 增加参数控制 是否统计项功能 如果是统计项那么不允许编辑
            	var onclickStr = ' onclick="kqDataMx_me.changeSelected(this)"';
            	if((kqDataMx_me.otherParam.enableModifys + ",").indexOf(","+id+",") > -1){
            		onclickStr = 'style="background-color:#E5E5E5;"';
            	}
                i_comps.push({
                    xtype:'container',
                    height: 35,
                    width: 65,
                    border:true,
                    html:'<div class="'+clsName+'" id="'+id+'" title="'+map[m].item_symbol+"&nbsp;&nbsp;"+map[m].item_name
                    +'" '+onclickStr+'><div>'+map[m].item_symbol+"&nbsp;&nbsp;"+map[m].item_name
                    +'</div><span id="'+id+'_xuhao" class="item_xuhao"></span><span></span></div>'
                });
            }
        }
        Ext.create("Ext.window.Window",{
            id:'changItemWin',
            title:kq.datamx.label.windowtitle,//+"<span style='color:red;'>（"+kq.datamx.msg.maxItems.replace("{0}",me.itemCount)+"）</span>",
            tools: [{
                id:'helpInfo',
                type: 'help',
                listeners:{
                    afterrender:function () {
                        Ext.create('Ext.tip.ToolTip', {
                            target: 'helpInfo',
                            html: "<div style='white-space:nowrap;overflow:hidden;'>"+kq.datamx.msg.maxItems.replace("{0}",me.itemCount)+"</div>",
                            bodyStyle:"background-color:white;border:1px solid #c5c5c5;"
                        });
                    }
                },
                scope: this
            }],
            width:490,
            resizable : false,
            height:350,
            overflowY:'auto',
            modal:true,
            listeners:{
                show:function () {
                    var el = Ext.getDom("helpInfo");
                    var el2 = Ext.getDom("helpInfo-toolEl");
                    if (el) {
                    	//根据字体大小设置问号的位置
                    	var element = document.getElementById('changItemWin_header-title');
                    	var size=kqDataMx_me.getStyle(element,'font-size');
                        Ext.getDom("helpInfo").style.left = size.slice(0,-2)*9.5+"px";
                    }
                    if(el2){
                        Ext.getDom("helpInfo-toolEl").style.backgroundColor = "transparent";
                    }
                    me.refreshItemsOrder();
                }
            },
            items:[
                {
                    //班次
                    xtype:'container',
                    layout:'column',
                    margin:'10 0 15 12',
                    items:c_comps,
                    defaults:{
                        margin:'0 13 10 0',
                        layout:{
                            align:'center',
                            pack:'center',
                            columnWidth:65
                        }
                    }
                },
                {
                    //项目
                    xtype:'container',
                    layout:'column',
                    items:i_comps,
                    margin:'0 0 0 12',
                    defaults:{
                        margin:'0 13 10 0',
                        layout:{
                            align:'center',
                            pack:'center',
                            columnWidth:65
                        }
                    }
                }
            ],
            bbar: [ 
                '->',
                { xtype: 'button',style:'margin-right:5px;', text: kq.button.ok,width:75,height:25,handler:function(){
                    var str = "";
                    var selectedItems = me.selecedOrderdItem;
                    if(selectedItems && selectedItems.length>me.itemCount){
                        Ext.showAlert(kq.datamx.msg.maxItems.replace("{0}",me.itemCount));
                        return;
                    }
                    for(var index in selectedItems){
                        var itemid = selectedItems[index];
                        str+=itemid+",";
                    }
                    str = str.length>0?str.substring(0,str.length-1):"";
                    if(record.get(columnid)!=str){
                        record.set(columnid,str);
                    }
                    var changItemWin = Ext.getCmp("changItemWin");
                    if(changItemWin){
                        changItemWin.destroy();
                    }
                } },
                { xtype: 'button', text: kq.button.cancle,width:75,height:25,handler:function(){
                    var changItemWin = Ext.getCmp("changItemWin");
                    if(changItemWin){
                        changItemWin.destroy();
                    }
                }},
                '->'
            ]
        }).show();
    },
    /**
     * 获取字体大小
     */
    getStyle: function (el,styleProp){
	   	 var camelize = function(str){
	   		 return str.replace("/\-()\w)/g",function(str,letter){
	   			 return letter.toUpperCase}); 
	   	}; 
	   	 if(el.currentStyle){
	   		 return el.currentStyle [camelize(styleProp)];
	   	} else if(document.defaultView&& document.defaultView.getComputedStyle){
	   		return document.defaultView.getComputedStyle(el,null).getPropertyValue(styleProp);
	   	}else{
	   		return el.style[camelize(styleProp)]; 
	   	}
   	},
    /**
     * 显示异常出勤
     */
    getKqExcept: function(value,metaData,Record){
    	var guidkey = Record.get("guidkey");
    	var exceptFlag=false;
    	var exceptDisplay= kqDataMx_me.otherParam.exceptDisplay;
    	var startDate = kqDataMx_me.otherParam.kq_start;
    	var endDate = kqDataMx_me.otherParam.kq_end;
    	var sumsVal=kqDataMx_me.otherParam.sumsVal;
    	if (exceptDisplay) {
    		var sumsValList=sumsVal.split(",");
    		for(i=0;i<sumsValList.length-1;i++){
    			var c= Record.get(sumsValList[i]);
    			if(c){
    				if(c>0){
    					exceptFlag=true;
    				}
    			}
    		}
		}
    	if (exceptFlag) {
    		return "<a onclick='kqDataMx_me.toGetKqExcept(\""+guidkey+"\",\""+value+"\",\""+startDate+"\",\""+endDate+"\");' href='javascript:void(0);' >"+value+" </a>";
		}else{
			return value;
		}
    },
    isMoreZero: function (obj){
    	if(typeof obj == "undefined" || obj == null || obj == ""){
            return true;
        }else{
            return false;
        }
    },
    toGetKqExcept: function(guidkey,name,startDate,endDate){
    	var map = new HashMap();
    	map.put("guidkey",guidkey);
    	map.put("name",name);
    	map.put('startDate', startDate);
		map.put('endDate', endDate);
    	Ext.require('KqDataURL.KqExceptInfo', function() {
        	Ext.create("KqDataURL.KqExceptInfo", map);
        });
    },
    /**
     * 显示班次或项目的序号
     */
    refreshItemsOrder:function(){
       var selectedArr = Ext.query('span[id$="_xuhao"]');
        for (var i in selectedArr){
            var temp = selectedArr[i];
            if (temp){
                temp.style.display='none';
            }
        }
        var index = 1;//序号
        for (var i=0;i<this.selecedOrderdItem.length;i++){
            var id = this.selecedOrderdItem[i];
            var p = Ext.getDom(id+"_xuhao");
            //为班次或项目添加序号
            if (p){
                p.style.display='inline-block';
                p.innerText = index;
                index++;
            }
        }
    },

    /**
     * 选中样式修改
     */
    changeSelected:function(div){
        var me = this;
        var clazz = div.className;
        var id = div.id;
        var flag = "select";
        if(clazz=="selectedCls"){
            div.className="unselectedCls";
            flag = "deselect";
            for (var i=0;i<me.selecedOrderdItem.length;i++){
                var itemid = me.selecedOrderdItem[i];
                if (id == itemid){
                    me.selecedOrderdItem.splice(i, 1);
                    break;
                }
            }
        }else{
            var selectedItems = Ext.query(".selectedCls");
            if(selectedItems && selectedItems.length>=me.itemCount){
                Ext.showAlert(kq.datamx.msg.maxItems.replace("{0}",me.itemCount));
                return;
            }
            div.className="selectedCls";
            me.selecedOrderdItem.push(id);
        }
        me.refreshItemsOrder();

    },
    /**
     * 保存考勤数据明细 自动保存(异步保存 增加页面流畅性)
     *
     * @param updateArr 修改的字段的数组，每个record 中修改的字段为一个对象 key--value
     * @param guidkey  人员唯一编号
     * @param isLoadStore 是否需要刷新store
     */
    savekqDataMx:function(paramValue,record){
    	//处理待办start
    	var guidkey = record.data.guidkey;
        // 当要改为代确认时  需要校验用户待办 如果有置为已办
        if("2" == paramValue.confirm){
        	// 不包含增加
    		if(!Ext.Array.contains(kqDataMx_me.replaceConfirmUsers, guidkey))
    			kqDataMx_me.replaceConfirmUsers.push(guidkey);
        }else if("0" == paramValue.confirm){
        	// 包含了移除
    		if(Ext.Array.contains(kqDataMx_me.replaceConfirmUsers, guidkey)){
    			var index = Ext.Array.indexOf(kqDataMx_me.replaceConfirmUsers, guidkey, 0);
    			if(index != -1)
    				Ext.Array.remove(kqDataMx_me.replaceConfirmUsers, guidkey);
    		}
        }
        // 实时更新确认待办状态
		if(kqDataMx_me.replaceConfirmUsers.length > 0){
			kqDataMx_me.doReplaceConfirm();
		}
		//处理待办end
        var map = new HashMap();
        var jsonstr = {};
        jsonstr.type="save";
        jsonstr.scheme_id=this.schemeId;
        jsonstr.kq_duration=this.kqDuration;
        jsonstr.kq_year=this.kqYear;
        jsonstr.orgId=this.orgId;
        jsonstr.guidkey=record.get("guidkey");
        jsonstr.paramValue=paramValue;
        // 55748 保存时增加校验不可编辑的考勤项目
        jsonstr.enableModifys=kqDataMx_me.otherParam.enableModifys;
        map.put("jsonStr",Ext.encode(jsonstr));
        Rpc({functionId:'KQ00021205',async:true,success:function(form){
            var res = Ext.decode(form.responseText);
            var data = Ext.decode(res.returnStr);
            if(data.return_code=="fail"){
                Ext.showAlert(data.return_msg);
                record.reject(false);
                return;
            }
            record.commit();
            //强制表格更新布局,
            var grid;
            if(this.idSuff=="Mx"){
                grid  = this.tableObj1.tablePanel;
            }else if (this.idSuff=="NoneMx"){
                grid  = this.tableObj2.tablePanel;
            }
            if(grid){
                grid.syncRowHeights();
                var selModel = grid.getSelectionModel();
                var selected = selModel.getSelected();
                if (selected && selected.startCell && selected.endCell){
                    var startRI = selected.startCell.rowIdx;
                    var startCI = selected.startCell.colIdx;
                    var endRI = selected.endCell.rowIdx;
                    var endCI = selected.endCell.colIdx;
                    selModel.selectCells([startCI,startRI],[endCI,endRI],false);
                }
            }
      },scope:this},map);
    },
    /**
     * 刷新页面数据
     */
    loadSpData:function(){
    	// 刷新页面之前取消选中区域
        var grid = Ext.getCmp("kqdatamx"+this.idSuff+"_tablePanel");
        // Ext的bug,需要在load前取消选中状态，否则会报莫名其妙的错
        grid.getSelectionModel().deselectAll();
        var store = Ext.data.StoreManager.lookup("kqdatamx"+this.idSuff+"_dataStore");
        if(store)
            store.load();
    },
    /**
     * 根据不同情况显示不同按钮
     * 	operation:"0",//0 不可操作 //1 可以报批 //2 可以提交（归档到子集) //3 可以新建
     showMx:'true',//是否显示日明细
     hasNextApprover:'1',//0=没有下级审批人，1=有下级审批人（上报页面无需传这个参数）
     optRole:'0',//登录人的角色 1=人事处考勤员 2=人事处审核人 3=下级机构考勤员 4=下级机构审核人
     */
    displayButtons:function(idSuff){
        if(this.operation=="0"){
            this.showOrHideBtn("createBtn"+idSuff,false);
            this.showOrHideBtn("staffChangeBtn"+idSuff,false);
            this.showOrHideBtn("calulateBtn"+idSuff,false);
            this.showOrHideBtn("publishBtn"+idSuff,false);
            this.showOrHideBtn("deleteBtn"+idSuff,false);
            this.showOrHideBtn("appealBtn"+idSuff,false);
            this.showOrHideBtn("agreeBtn"+idSuff,false);
            this.showOrHideBtn("rejectBtn"+idSuff,false);
            this.showOrHideBtn("submitBtn"+idSuff,false);
        }else if(this.operation=="1"){
            this.showOrHideBtn("createBtn"+idSuff,false);
            this.showOrHideBtn("calulateBtn"+idSuff,true);
            this.showOrHideBtn("submitBtn"+idSuff,false);
            if(this.hasNextApprover=="0"){//来自考勤审批有可能有同意按钮
                //如果是下级单位考勤员，并且没有下级审批人了，那么可以直接提交 zhanghua 2019-01-04
                if(this.optRole=='3'){
                	// 51227 同意按钮放开
                    this.showOrHideBtn("agreeBtn"+idSuff,true);
                    this.showOrHideBtn("appealBtn"+idSuff,false);
                    if("06" == kqDataMx_me.sp_flag){
                    	this.showOrHideBtn("submitBtn"+idSuff,true);
                    }else{
                    	this.showOrHideBtn("submitBtn"+idSuff,false);
                    }
                }else{
                    this.showOrHideBtn("agreeBtn"+idSuff,true);
                    this.showOrHideBtn("appealBtn"+idSuff,false);
                }
            }else{
                this.showOrHideBtn("agreeBtn"+idSuff,false);
                this.showOrHideBtn("appealBtn"+idSuff,true);
                this.showOrHideBtn("submitBtn"+idSuff,false);
            }
            this.showOrHideBtn("rejectBtn"+idSuff,this.optRole!="3");//下级机构考勤员没有退回按钮
            this.showOrHideBtn("publishBtn"+idSuff,this.optRole=="3");//下级考勤员有发布按钮

            //人员增减：数据比对功能仅在考勤数据为“未提交”、“退回”状态下可用,同时只能是下级机构考勤员可用
            if (this.optRole=="3"){
                this.showOrHideBtn("staffChangeBtn"+idSuff,true);
                this.showOrHideBtn("deleteBtn"+idSuff,true);//下级考勤员有删除按钮
            }else{
                this.showOrHideBtn("staffChangeBtn"+idSuff,false);
                this.showOrHideBtn("deleteBtn"+idSuff,false);//下级考勤员有删除按钮
            }
        }else if(this.operation=="2"){
            this.showOrHideBtn("staffChangeBtn"+idSuff,false);
            this.showOrHideBtn("createBtn"+idSuff,false);
            this.showOrHideBtn("calulateBtn"+idSuff,false);
            this.showOrHideBtn("publishBtn"+idSuff,false);
            this.showOrHideBtn("deleteBtn"+idSuff,false);
            this.showOrHideBtn("appealBtn"+idSuff,false);
            this.showOrHideBtn("agreeBtn"+idSuff,false);
            this.showOrHideBtn("submitBtn"+idSuff,true);
            this.showOrHideBtn("rejectBtn"+idSuff,false);
        }else if(this.operation=="3"){
            this.showOrHideBtn("createBtn"+idSuff,true);
            this.showOrHideBtn("calulateBtn"+idSuff,false);
            this.showOrHideBtn("publishBtn"+idSuff,false);
            this.showOrHideBtn("deleteBtn"+idSuff,false);
            this.showOrHideBtn("appealBtn"+idSuff,false);
            this.showOrHideBtn("agreeBtn"+idSuff,false);
            this.showOrHideBtn("submitBtn"+idSuff,false);
            this.showOrHideBtn("rejectBtn"+idSuff,false);
            this.showOrHideBtn("staffChangeBtn"+idSuff,false);
        }
        // 重置按钮 控制
        if("03"==kqDataMx_me.sp_flag || "06"==kqDataMx_me.sp_flag){
        	this.showOrHideBtn("resetBtn"+idSuff,true);	
        }else{
        	this.showOrHideBtn("resetBtn"+idSuff,false);
        	// 应急中心个性化 应该是在没有重置按钮的情况下 个性化显示
        	if(this.hlwyjzx_flag){// && this.operation != "0"
        		// 只有人事处考勤员可新建 计算
        		if('1' != this.optRole){
        			this.showOrHideBtn("createBtn"+idSuff,false);
        			this.showOrHideBtn("calulateBtn"+idSuff,false);
        			this.showOrHideBtn("staffChangeBtn"+idSuff,false);
        			this.showOrHideBtn("deleteBtn"+idSuff,false);
        		}else{
        			if(this.canEdit){
        				this.showOrHideBtn("staffChangeBtn"+idSuff,true);
        				this.showOrHideBtn("calulateBtn"+idSuff,true);
        				this.showOrHideBtn("deleteBtn"+idSuff,true);
        			}
        			// 重置 或退回到不是人事处的环节  不出现审批操作等按钮  
        			if("07"==kqDataMx_me.sp_flag && "1"!=this.otherParam.curr_user){
        				this.showOrHideBtn("appealBtn"+idSuff,false);
            			this.showOrHideBtn("rejectBtn"+idSuff,false);
            			this.showOrHideBtn("agreeBtn"+idSuff,false);
            			this.showOrHideBtn("submitBtn"+idSuff,false);
        			}
        		}
        	}
        }
        //导入月汇总按钮控制
        if("01"==kqDataMx_me.sp_flag || "07"==kqDataMx_me.sp_flag){
        	this.showOrHideBtn("importcollect"+idSuff,true);
        }else{
        	this.showOrHideBtn("importcollect"+idSuff,false);
        }
    },
    /**
     * 隐藏显示不同按钮
     * @param btnId 按钮id
     * @param flag true=显示  false=隐藏
     *
     */
    showOrHideBtn:function(btnId,flag){
        var btn = Ext.getCmp(btnId);
        if(btn)
            btn.setVisible(flag);
    },
    /**
     * 传入要修改的变量和值
     */
    setParmValue:function(name,value){
        this[name]=value;
        //重新计算页面是否可以编辑
        if(name=="operation"){
            //启用员工确认功能
            if (this.confirmFlag == 1){
                //未创建 或 未提交并且未确认的，所有人可改；发起人登录时并且为退回或者未提交时可修改
                this.canEdit = (this.optRole==3 && this.operation=="1")||this.operation=='3';
            }else{
                //不启用员工确认时，当前流程节点到登陆人的时候可改，否则不允许修改
                this.canEdit = (this.operation=="1" && this.orgId.split(",").length==1) || (this.operation=='3')//是否可以编辑表格
            }
        }
    },
    /**
     * 校验单元格是否可编辑
     */
    checkCell:function(editor,e){
    	// 应急中心机构考勤员可以修改的指标 个性化
    	if(kqDataMx_me.hlwyjzx_flag && "3"==kqDataMx_me.optRole 
    			&& (","+kqDataMx_me.otherParam.memoFields+",").indexOf(","+e.field+",") != -1
    			&& "3"==kqDataMx_me.otherParam.curr_user)
            return true;
        else if(kqDataMx_me.canEdit)
        	return true;
        return false;
    },
    closeMainPanel:function(){
        //移除复写的表格样式，避免对其他页面产生影响
        Ext.util.CSS.removeStyleSheet("kqmxGridLineHeight");
        Ext.util.CSS.removeStyleSheet("rowNumberCls");
        Ext.util.CSS.removeStyleSheet("x-ssm-extender-mask");
        var dataMxWin = Ext.getCmp("dataMxWin");
        if(dataMxWin){
            dataMxWin.destroy();
            if(kqDataMx_me.callBackFunc)
                kqDataMx_me.callBackFunc();
            //返回主页面
            if(kqDataMx_me.fromflag=="pt"){
                window.location.target='il_body';
                window.location.href = "/templates/index/hcm_portal.do?b_query=link";
            }
        }
    },
    /**
     * 考勤上报发布  release
     */
    kqDataPublish:function(){
        var me = this;
       /* if(!me.canEdit){
            Ext.showAlert(kq.datamx.msg.notallowOpt);
            return;
        }*/

        if(me.noExistConfirm()!="false"){
            Ext.showAlert(kq.datamx.msg.confirmedData);
            return;
        }
        Ext.showConfirm(kq.datamx.label.kqDataSp.publish,function(flag){
            if("yes" == flag){
            	Ext.MessageBox.wait(kq.label.wait, kq.label.waiting);
            	var map = new HashMap();
            	var json = {};
            	json.type="release";
            	json.kq_year = me.kqYear;
            	json.kq_duration = me.kqDuration;
            	json.scheme_id = me.schemeId;
            	json.org_id=me.orgId;
            	map.put("jsonStr", Ext.encode(json));
            	Rpc({
            		functionId: 'KQ00021101', async: true, success: function (form) {
            			Ext.MessageBox.close();
            			var result = Ext.decode(form.responseText);
            			if(result.succeed){
            				var return_code = result.returnStr.return_code;
            				if (return_code == 'success') {
            					me.loadSpData();
            					Ext.showAlert(kq.datamx.label.kqDataSp.success);
            				}else{
            					Ext.showAlert(result.returnStr.return_msg);
            				}
            			}
            		}
            	}, map);
            }
        });
    },
    /**
     * 考勤数据审批或计算
     * opt =appeal(上报) =reject(退回) =approve(同意)
     *     =submit（提交）=compute(计算)
     */
    kqDataSp:function(opt){

        //重置
        var me = this;
       /* if(!me.canEdit&&(opt!='submit'&&me.operation!='2')){
            Ext.showAlert(kq.datamx.msg.notallowOpt);
            return;
        }*/
        // 除计算不需要校验是否确认 其他操作都需校验是否确认 && 角色是机构考勤员
        if("compute" != opt && "3"==me.optRole){
        	//启用员工确认后校验是否确认//校验是否已确认或代确认
            if(me.confirmFlag && me.noExistConfirm()=="false"){
                Ext.showAlert(kq.datamx.msg.haveNoConfirmData.replace("{0}",kq.datamx.label.kqDataSp.operation));
                return;
            }
        }
        var optStr = "";
        var approvalMessage="";
        var sp_flag="";
        switch (opt){
            case 'appeal':
                optStr = kq.datamx.label.kqDataSp.appeal;
                approvalMessage=kq.datamx.msg.verifyMessage;
                sp_flag = "02";
                break;
            case 'reject':
                optStr = kq.datamx.label.kqDataSp.reject;
                approvalMessage=kq.datamx.msg.disagree;
                sp_flag = "07";
                break;
            case 'approve':
                optStr = kq.datamx.label.kqDataSp.approve;
                approvalMessage=kq.datamx.msg.agree;
                sp_flag = "03";
                break;
            case 'submit':
                optStr = kq.label.submit;
                break;
            case 'compute':
                optStr = kq.datamx.label.kqDataSp.compute;
                break;
            default:
                break;
        };
        var msg = kq.datamx.label.kqDataSp.confirmmsg.replace("{0}",optStr);
        //计算时要判断是否已有数据
        if(opt=="compute"){
	         // 如果不可编辑则 计算不允许覆盖
	         if (this.canEdit){// && me.beforeCompute()如果维护过考勤数据则出现 "是否覆盖数据的复选框"
	             me.coverDataFlag=1;
	             var temp = "<div id='coverDataDiv'><input onclick='kqDataMx_me.checkboxOnclick(this)' style='cursor: pointer;' type='checkbox' id='coverData'/><label for='coverData' style='cursor: pointer;position:relative;bottom:2px;'>"+kq.datamx.label.kqDataSp.hasComputeData+"</label></div>";
	             msg = temp+msg;
	         }
	         /*else{
	             me.coverDataFlag=0;
	         }*/
	         //未设置班次时长
	         if(!me.valideClass())
	        	 return;
	         Ext.showConfirm(msg,function(flag){
	        	 var coverDataDiv = Ext.getDom("coverDataDiv");
	        	 if(coverDataDiv && coverDataDiv.parentNode){
	        		 coverDataDiv.parentNode.removeChild(coverDataDiv);
	        	 }
	             if(flag=="yes"){
	            	 me.spOptDetail(opt);
	             }
	         });
	         return;
        }
        // 50105 现优化为任何操作之前 只要可编辑 就都先自动计算一遍 // 52967 退回不自动计算
        if("compute" != opt && this.canEdit && "reject" != opt){
        	// 如果有计算权限则 报批等操作之前先计算一遍 （不覆盖）
            if("1" == me.privs.computep){
            	me.coverDataFlag = 1;
            	me.spOptDetail("compute_only");
            }
        }
        if (opt == "reject" && me.viewType == "1"){
            Ext.create("KqDataURL.KqDataRejectWindow",{
                scheme_id:me.schemeId,
                org_id:me.orgId,
                viewType:"1",
                rejectFunction:me.kqDataRejectCallBack
            });
            return;
        }
        // 填写意见校验  归档不需要
        if (me.otherParam.approvalMessage == "1"&& "submit"!=opt) {
        	me.fillmsgWin(opt,approvalMessage,sp_flag);
        }else{
        	Ext.showConfirm(msg,function(flag){
        		var coverDataDiv = Ext.getDom("coverDataDiv");
             	if(coverDataDiv && coverDataDiv.parentNode){
             		coverDataDiv.parentNode.removeChild(coverDataDiv);
             	}
             	// 45070 只有点击是才接着走计算
                if(flag=="yes"){
                	me.signatureFileFunc(opt);
              }else
              	return;
            });
        }
    },
    /**
     * 驳回操作
     */
    kqDataRejectCallBack:function(org_id,userid,role_id){
    	var me = kqDataMx_me;
    	if (me.otherParam.approvalMessage == "1") {
    		me.fillmsgWin("reject",kq.datamx.msg.disagree,"07",org_id,userid,role_id)
		}else{
			Ext.showConfirm(msg,function(flag){
				var coverDataDiv = Ext.getDom("coverDataDiv");
             	if(coverDataDiv && coverDataDiv.parentNode){
             		coverDataDiv.parentNode.removeChild(coverDataDiv);
             	}
             	// 45070 只有点击是才接着走计算
             	if(flag=="yes"){
             		kqDataMx_me.spOptDetail("reject",org_id,userid,role_id);
             	}else
             		return;
			});
		}
    },
    spOptDetail:function (opt,org_id,userid,role_id, photo_info) {
        var me = this;
        Ext.MessageBox.wait(kq.label.wait, kq.label.waiting);
        var map = new HashMap();
        var json = {};
        json.type=opt;
        //审批页面进去是，退回时可选则退回的节点
        if (!Ext.isEmpty(org_id)
            && !Ext.isEmpty(userid)
            && !Ext.isEmpty(role_id)){
            json.org_id = org_id;
            json.user_id = userid;
            json.role_id = role_id;
        }else{
            json.org_id=me.orgId;
        }
        // compute_only在新建或报批时计算时的标识
        if("compute"==opt || "compute_only"==opt){
        	json.type="compute";
            json.coverDataFlag = me.coverDataFlag;
        }
        json.scheme_id = me.schemeId;
        json.kq_duration = me.kqDuration;
        json.kq_year = me.kqYear;
        json.viewtype = me.viewType;
        // 增加照片信息
        json.photo_info = photo_info;
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'KQ00021201', async: true, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;
                Ext.MessageBox.close();
                
                if (return_code == 'success') {
                    //改变页面参数,刷新页面按钮状态
                    if(opt=="approve"){
                        me.setParmValue("sp_flag", "03");
                        me.setParmValue("operation","2");
                    }else{
                        if (opt == "appeal"){
                            me.setParmValue("sp_flag", "02");
                        }
                        else if (opt == "reject"){
                            me.setParmValue("sp_flag", "07");
                        }else if (opt == "submit"){
                            me.setParmValue("sp_flag", "06");
                        }

                        if(opt!="compute" && opt!="compute_only") {
                            me.setParmValue("operation", "0");
                        }
                    }
                    // 点击重置按钮时 并且是发起人时  需直接刷新可操作按钮显示状态，并且重建页面//增加应急中心个性化标识
                    if (opt == "reject" && ("3"==me.optRole || ("1"==me.optRole && me.hlwyjzx_flag))){
                    	me.setParmValue("operation", "1");
	                    kqDataMx_me.closeMainPanel();
	                    kqDataMx_me.createStyleCss();
	                    kqDataMx_me.getListView(kqDataMx_me.idSuff);
	                }else{
	                	//重置按钮状态
	                	me.displayButtons("Mx");
	                	me.displayButtons("NoneMx");
	                	// 应急中心个性化处理备注指标是否可编辑权限
	                	if(kqDataMx_me.hlwyjzx_flag && "3"==kqDataMx_me.optRole && "02"==kqDataMx_me.sp_flag
	                			&& "3"==kqDataMx_me.otherParam.curr_user){
	                		kqDataMx_me.otherParam.curr_user = "4";
	                	}
	                	me.loadSpData();
	                	// 在新建或报批时计算的时候 不需要弹出提示信息
	                	if(opt!="compute_only") 
	                		Ext.showAlert(kq.datamx.label.kqDataSp.success);
	                }
                }else{
                    Ext.showAlert(returnStr.return_msg);
                }
            }
        }, map);
    },
    /**
     * 校验是否存在未确认的考勤数据
     *
     * return  "false"=存在未确认的考勤数据
     */
    noExistConfirm:function(){
        var flag = "false";
        var me = this;
        var json = {};
        json.type="checkCanAppeal";
        json.org_id=me.orgId;
        json.scheme_id = me.schemeId;
        json.kq_duration = me.kqDuration;
        json.kq_year = me.kqYear;
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'KQ00021201', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;
                if(return_code == 'success') {
                    flag = returnStr.return_data.canAppeal;
                }
            }
        },map)
        return flag;
    },
    /**
     * 判断是否是日明细列
     */
    isRiMxColumn:function(columnid){
        var flag = false;

        if(!Ext.isEmpty(columnid) && columnid.length>3 && columnid.substring(0,3)=='q35'){
            var c = columnid.substring(3);
            if(!isNaN(c) && parseInt(c,10)>0 && parseInt(c,10)<=31){
                flag = true;
            }
        }
        return flag;
    },
    /**
     * 计算前判断是否已经维护过日明细数据或者统计数据
     */
    beforeCompute:function(){
        var me = this;
        var flag = false;
        var map = new HashMap();
        var json = {};
        json.type="beforeCompute";
        json.org_id=me.orgId;
        json.scheme_id = me.schemeId;
        json.kq_duration = me.kqDuration;
        json.kq_year = me.kqYear;
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'KQ00021201', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;
                if (return_code == 'success') {
                    flag = returnStr.return_data.flag;
                }
            }
        }, map);
        return flag;
    },
    /**
     * 计算时校验应出勤是否设置单位（item_unit） 如果单位是时|分，校验班次是否设置了时长
     */
    valideClass:function(){
        var flag = true;
        var me = this;
        var map = new HashMap();
        var json = {};
        json.type='checkCanCompute';
        json.scheme_id = me.schemeId;
        map.put("jsonStr",Ext.encode(json));
        Rpc({functionId:'KQ00021201',async:false,success:function(res){
            var result = Ext.decode(res.responseText);
            var returnStr = eval(result.returnStr);
            var return_code = returnStr.return_code;
            if (return_code == 'fail') {
                flag = false;
                Ext.showAlert(returnStr.return_msg);
            }
        }},map);
        return flag;
    },
    /**
     * 导出Excel
     */
    exportExcel : function(type,photo){
        var me = this;
        var map = new HashMap();
        var json = {};
        json.type=(Ext.isEmpty(type)) ? 'exportExcel' : type;
        json.org_id=me.orgId;
        json.scheme_id = me.schemeId;
        json.kq_duration = me.kqDuration;
        json.kq_year = me.kqYear;
        json.showMx =me.idSuff=="Mx"?"true":"false";
        json.mxDetailType = me.mxDetailType;
        map.put("jsonStr",Ext.encode(json));
        Ext.MessageBox.wait(kq.label.exporting, kq.label.waiting);
        Rpc({functionId:'KQ00021202',async:true,success:function(res){
            var result = Ext.decode(res.responseText);
            var data = Ext.decode(result.returnStr);
            if(data.return_code=="success"){
                var filename = data.return_data.filename;
                filename = decode(filename);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true";
                Ext.MessageBox.close();
            }else{
                Ext.MessageBox.close();
                Ext.showAlert(data.return_msg);
            }
        },scope:this},map);
    	
    },
    /**
     * 保存栏目设置回调
     */
    schemeSetting_callBack:function(){
        var dataMxWin = Ext.getCmp("dataMxWin");
        if(dataMxWin){
            dataMxWin.destroy();
        }
        //为了满足特殊的情况的要求，需要将保存的栏目设置的subModuleId 改一下
        kqDataMx_me.changeSchemeId();
    },
    /**
     * 为了满足特殊的情况的要求，需要将保存的栏目设置的subModuleId 改一下
     * @returns
     */
    changeSchemeId : function(){
        var map = new HashMap();
        var json = {};
        json.type = "changeSchemeId";
        json.scheme_id = this.schemeId;
        json.subModuleIdPiff = "kqdata_"+this.idSuff+"_";
        map.put("jsonStr", Ext.encode(json));
        Rpc({functionId:'KQ00021204',async:false,success:function(form){
            kqDataMx_me.getListView(kqDataMx_me.idSuff);
        }},map);
    },
    /**
     * 班次或考勤项目鼠标悬浮事件
     */
    itemMouseOver:function(itemSpan,delay){
        if(delay){
            kqDataMx_me.timerTask=setTimeout(function(){
                var img = Ext.getDom("imgdel_"+itemSpan.id);
                if(img){
                    img.style.display = "inline";
                }
            },600);
        }else{
            itemSpan.style.display = "inline";
        }
    },
    /**
     * 班次或考勤项目鼠标移出事件
     * @returns
     */
    itemMouseOut:function(itemSpan,delay){
        if(delay){
            var img = Ext.getDom("imgdel_"+itemSpan.id);
            if(img){
                img.style.display = "none";
            }
            if(kqDataMx_me.timerTask){
                clearTimeout(kqDataMx_me.timerTask);
            }
        }else{
            itemSpan.style.display = "none";
        }


    },
    /**
     * 删除单个班次和项目
     */
    delItem : function(img,value){
        var delVal = img.id.split("_")[4];
        var grid  = this.tableObj1.tablePanel;
        var rowIdx = img.id.split("_")[2];
        var record = grid.getStore().getAt(rowIdx);
        if(!Ext.isEmpty(value) && !Ext.isEmpty(delVal)){
            var vals = value.split(",");
            for(var i=0;i<vals.length;i++){
                if(vals[i]==delVal){
                    var columnid = img.id.split("_")[3];
                    vals.splice(i,1);
                    var newVal =vals.join(",");
                    record.set(columnid,newVal);
                    img.style.display = "none";
                    break;
                }
            }
        }

    },
    getCellData:function (format,erase) {
        var me = this;
        var cmp = Ext.getCmp("kqdatamx"+me.idSuff+"_tablePanel"),
            selModel = cmp.getSelectionModel(),
            ret = [],
            isRaw = format === 'raw',
            isText = format === 'text',
            viewNode,
            cell, data, dataIndex, lastRecord, column, record, row, view;
        var checkbool = true;
        var eraseList = new Array();
        selModel.getSelected().eachCell(function (cellContext) {
            column = cellContext.column,
            view = cellContext.column.getView();
            record = cellContext.record;

            dataIndex = column.dataIndex;
            if (column.ignoreExport ||
                dataIndex =="b0110"
                ||dataIndex =="e0122"
                ||dataIndex =="e01a1"
                ||dataIndex =="a0101"
                ||(me.otherParam.confirmField.indexOf(dataIndex) != -1)
                ||dataIndex =="only_field") {
                return;
            }

            if (lastRecord !== record) {
                lastRecord = record;
                ret.push(row = []);
            }

            if (isRaw) {
                data = record.data[dataIndex];
            } else {
                viewNode = view.all.item(cellContext.rowIdx);
                if (!viewNode) {
                    viewNode = Ext.fly(view.createRowElement(record, cellContext.rowIdx));
                }
                cell = viewNode.down(column.getCellInnerSelector());
                data = cell.dom.innerHTML;
                if (isText) {
                    data = Ext.util.Format.stripTags(data);
                }
            }

            row.push(data);
            if (erase && dataIndex) {
            	// 55748 剪切校验不可编辑的考勤项目
            	 var bool=true;
                 if ("Mx"==me.idSuff) {
                 	var data = record.data[dataIndex];
                 	var bool = kqDataMx_me.checkEnableModifys(data);
                 }
            	if(bool){
            		//record.set(dataIndex, "");
            		eraseList.push(dataIndex);
            	}else{
                	checkbool = false;
            	}
            }
        });
        if (erase) {
        	// 55748 剪切操作 - 如果校验通过 则需要重新剪切
        	if(checkbool){
        		selModel.getSelected().eachCell(function (cellContext) {
        			column = cellContext.column,
        			record = cellContext.record;
        			dataIndex = column.dataIndex;
        			if(eraseList.toString().indexOf(dataIndex) != -1){
        				record.set(dataIndex, "");
        			}
        		});
        	}else{
        		Ext.showAlert(kq.datamx.msg.checkEnableModifys);
        		return;
        	}
        }
        me.cellData = Ext.encode(ret);
        return  me.cellData;
    },
    putCellData:function () {
        var me = this;
        if (Ext.isEmpty(me.cellData))
            return;
        var data = me.cellData;
        var values = Ext.decode(data),
            row,
            recCount = values.length,
            colCount = recCount ? values[0].length : 0,
            sourceRowIdx, sourceColIdx,
            view = Ext.getCmp("kqdatamx"+me.idSuff+"_tablePanel").getView(),
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
        //【56019】进行复制时先判断复制的数据或者粘帖区域的数据是否包含不可修改的考勤项目如果包含则不允许粘帖数据
        var bool=true;
        view.getSelectionModel().getSelected().eachCell(function(context,ci_,ri_){
            ci_ = ci_-firstColIndex;
            ri_ = ri_-firstRowIndex;
            var copyVal = values[ri_%recCount][ci_%colCount];
            bool = kqDataMx_me.checkEnableModifys(copyVal);
        	if(!bool){
        		return false;
        	}
            
            dataIndex = context.column.dataIndex;
            if (dataIndex) {
            	bool = kqDataMx_me.checkEnableModifys(context.record.get(dataIndex));
            	if(!bool){
            		return false;
            	}
            }
        });
	     
       if(!bool) {
          	Ext.showAlert(kq.datamx.msg.checkEnableModifys);
          	return false;
       }
      //【56019】修改结束
        //如果目标区域是源的整数倍，复制单元格内容时，重复填充
        if(colCount_*recCount_>0 && recCount*colCount>0 && colCount_*recCount_%(recCount*colCount)==0){
            view.getSelectionModel().getSelected().eachCell(function(context,ci_,ri_){
                ci_ = ci_-firstColIndex;
                ri_ = ri_-firstRowIndex;
                var copyVal = values[ri_%recCount][ci_%colCount];
                dataIndex = context.column.dataIndex;
                var guidkey = context.record.get("guidkey");
                var str = me.changePerData[guidkey]?me.changePerData[guidkey].join(","):"";
                if (str.length>0){
                    str = ","+str+",";
                }
                if (dataIndex =="b0110"
                    ||dataIndex =="e0122"
                    ||dataIndex =="e01a1"
                    ||dataIndex =="a0101"
                    ||(me.otherParam.confirmField.indexOf(dataIndex) != -1)
                    ||dataIndex =="only_field"
                    //日明细列需要做读写权限控制（变动部门）
                    ||(me.isRiMxColumn(dataIndex) && str.length>0 && str.indexOf(","+dataIndex+",")==-1)){
                    return false;
                }
                var colType = me.columnTtype.get(dataIndex);
                if(colType=="N"){
                    if(isNaN(copyVal)){
                        return;
                    }
                }else if(colType=="D"){
                    if(!isNaN(copyVal)|| isNaN(Date.parse(copyVal))){
                        return;
                    }
                }
                if(me.isRiMxColumn(dataIndex)){
                    //简单判断日明细列的值的格式
                    if(copyVal && copyVal.length>0
                        && copyVal.substring(0,1)!="C"
                        && copyVal.substring(0,1)!="I"){
                        return;
                    }
                }
                if (dataIndex) {
                    context.record.set(dataIndex,copyVal);
                }
            });
            return;
        }
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
        var guidkey = destination.record.get("guidkey");
        var str2 = me.changePerData[guidkey]?me.changePerData[guidkey].join(","):"";
        if (str2.length>0){
            str2 = ","+str2+",";
        }
        for (sourceRowIdx = 0; sourceRowIdx < recCount; sourceRowIdx++) {
            row = values[sourceRowIdx];
            for (sourceColIdx = 0; sourceColIdx < colCount; sourceColIdx++) {

                dataIndex = destination.column.dataIndex;
                if (dataIndex =="b0110"
                    ||dataIndex =="e0122"
                    ||dataIndex =="e01a1"
                    ||dataIndex =="a0101"
                    ||dataIndex =="only_field"
                    ||(me.otherParam.confirmField.indexOf(dataIndex) != -1)
                    //日明细列需要做读写权限控制（变动部门）
                    ||(me.isRiMxColumn(dataIndex) && str2.length>0 && str2.indexOf(","+dataIndex+",")==-1)){
                    return;
                }

                var colType = me.columnTtype.get(dataIndex);
                if(colType=="N"){
                    if(isNaN(row[sourceColIdx])){
                        continue;
                    }
                }else if(colType=="D"){
                    if(!isNaN(row[sourceColIdx])|| isNaN(Date.parse(row[sourceColIdx]))){
                        continue;
                    }
                }
                if(me.isRiMxColumn(dataIndex)){
                    //简单判断日明细列的值的格式
                    if(row[sourceColIdx] && row[sourceColIdx].length>0
                        && row[sourceColIdx].substring(0,1)!="C"
                        && row[sourceColIdx].substring(0,1)!="I"){
                        continue;
                    }
                }
                if (dataIndex) {
                    dataObject[dataIndex] = row[sourceColIdx];
                }
                if (destination.colIdx === maxColIdx) {
                    break;
                }
                destination.setColumn(destination.colIdx + 1);
                var obj = {};
                obj[dataIndex] = row[sourceColIdx];
            }
            destination.record.set(dataObject);
            if (destination.rowIdx === maxRowIdx) {
                break;
            }
            destination.setPosition(destination.rowIdx + 1, destinationStartColumn);
        }
    },
    deleteCellData : function () {
        var me = this;
        var grid = Ext.getCmp("kqdatamx"+me.idSuff+"_tablePanel");
        if(!grid)
            return;
        var flag = false;
        var checkbool = true;
        var checkList = new Array();
        grid.getSelectionModel().getSelected().eachCell(function (cellContext) {
            var column = cellContext.column;
            var record = cellContext.record;
            var dataIndex = column.dataIndex;
            if (column.ignoreExport ||
                dataIndex =="b0110"
                ||dataIndex =="e0122"
                ||dataIndex =="e01a1"
                ||dataIndex =="a0101"
                ||(me.otherParam.confirmField.indexOf(dataIndex) != -1)
                ||dataIndex =="only_field") {
                return;
            }
            // 55748 删除校验不可编辑的考勤项目
            var bool=true;
            if ("Mx"==me.idSuff) {
            	var data = record.data[dataIndex];
            	var bool = kqDataMx_me.checkEnableModifys(data);
            }
        	if(bool){
        		flag = true;
        		//record.set(dataIndex, "");
        		checkList.push(dataIndex);
        	}else{
        		checkbool = false;
        	}
        });
        
        if (flag && checkbool){
        	// 55748 校验都通过后 再统一删除
        	grid.getSelectionModel().getSelected().eachCell(function (cellContext) {
                var column = cellContext.column;
                var record = cellContext.record;
                var dataIndex = column.dataIndex;
                if(checkList.toString().indexOf(dataIndex) != -1){
    				record.set(dataIndex, "");
    			}
        	});
            grid.getSelectionModel().deselectAll();
        }else{
        	Ext.showAlert(kq.datamx.msg.checkEnableModifys);
        	return;
        }
    },
    /**
     *  确认列渲染
     */
    confirmRender:function (value,x,y,rowIndex) {
        var str = "<span onclick='kqDataMx_me.updateConfrim({1},"+rowIndex+")' id='edit_"+rowIndex+"_opt' " +
            "title='{0}' style='font-weight:bold;color:{3};display:none;width:16px;cursor: pointer;'>{2}？</span>" +
            "<span id='edit_"+rowIndex+"'>{4}</span>";
        if(value=="0") {
            str =str.replace("{0}",kq.datamx.label.confirmopt+kq.datamx.label.confirm2).replace("{1}",2)
                .replace("{2}",kq.datamx.label.confirm2).replace("{3}","green")
                .replace("{4}",kq.datamx.label.confirm0);
        }else if(value=='1')
        {
            str = kq.datamx.label.confirm1;
        }else if(value=='2')
        {
            str =str.replace("{0}",kq.datamx.label.confirmopt+kq.datamx.label.confirm0).replace("{1}",0)
                .replace("{2}",kq.datamx.label.confirm0).replace("{3}","red")
                .replace("{4}",kq.datamx.label.confirm2);
        }else{
            str =str.replace("{0}",kq.datamx.label.confirmopt+kq.datamx.label.confirm2).replace("{1}",2)
                .replace("{2}",kq.datamx.label.confirm2).replace("{3}","green")
                .replace("{4}","&nbsp;&nbsp;");
        }
        return str;
    },
    /**
     * 显示或隐藏确认列
     * @param id
     * @param show
     */
    showOrHideConfirmIcon:function(rIndex,show){
        var id1 = "edit_"+rIndex+"_opt";
        var id2 = "edit_"+rIndex;
        var img1 = Ext.getDom(id1);
        var img2 = Ext.getDom(id2);
        if (img1) {
            img1.style.display = show ? 'inline' : 'none';
        }
        if (img2) {
            img2.style.display = !show ? 'inline' : 'none';
        }
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
    updateConfrim:function(state,rowIndex){
        var store = Ext.data.StoreManager.lookup("kqdatamx"+this.idSuff+"_dataStore");
        var record = store.getAt(rowIndex);
        if (record){
            record.set("confirm_",state);
        }
    },
    createNewdata:function () {
        var map = new HashMap();
        var json = {};
        json.type="create";
        json.org_id=kqDataMx_me.orgId;
        json.scheme_id = kqDataMx_me.schemeId;
        json.kq_duration = kqDataMx_me.kqDuration;
        json.kq_year = kqDataMx_me.kqYear;
        json.viewtype = kqDataMx_me.viewType;

        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'KQ00021201', async: true, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;

                if (return_code == 'success') {
                    //改变页面参数,刷新页面按钮状态
                    if (kqDataMx_me.optRole=="3") {
                        kqDataMx_me.setParmValue("operation", "1");
                    }else{
                        kqDataMx_me.setParmValue("operation", "0");
                    }
                    // 更改状态未提交
                    kqDataMx_me.setParmValue("sp_flag", "01");
                    // 如果有计算权限则新建完直接计算一遍
                    if("1" == kqDataMx_me.privs.computep){
                    	kqDataMx_me.coverDataFlag=1;
                    	kqDataMx_me.spOptDetail("compute_only");
                    }else{
                    	// 重置按钮状态    计算方法已包含该方法
                    	kqDataMx_me.displayButtons("Mx");
                    	kqDataMx_me.displayButtons("NoneMx");
                    	kqDataMx_me.loadSpData();
                    }
                }else{
                    Ext.showAlert(returnStr.return_msg);
                }
            }
        }, map);


    },
    /**
     * 人员增减
     * inflag 进入方式 =0进入上报页面自动校验；=其他 功能按钮
     */
    staffChange : function (inflag)
    {
        //是否要穿组织机构id
        var flag = kqDataMx_me.orgId.split(",").length==1?true:false;
        Ext.create('KqDataURL.StaffChangeContrast',{
            scheme_id: kqDataMx_me.schemeId,//考勤方案ID（加密） 必须
            kq_duration:kqDataMx_me.kqDuration,//考勤期间 必须
            kq_year: kqDataMx_me.kqYear,//考勤年份 必须
            orgId:flag?kqDataMx_me.orgId:"",//机构id（加密）  不必须
            inflag: inflag//进入方式
        });
    },
    /**
     * 获取cookie
     *
     * @param {}
     *            name
     * @return {}
     */
    getCookie:function (name)
    {
        var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

        if(arr=document.cookie.match(reg))

            return unescape(arr[2]);
        else
            return null;
    },
    /**
     * 删除cookie
     * @param {}
     * name
     */
    delCookie:function (name)
    {
        var cval=getCookie(name);
        if(cval!=null){
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            document.cookie= name + "="+cval+";expires="+exp.toGMTString();
        }
    },
    /**
     * 覆盖手工修改的数据 复选框单击触发事件
     * @param checkbox
     */
    checkboxOnclick :function (checkbox) {
        var me = this;
        if (checkbox.checked)
            me.coverDataFlag=0;
        else
            me.coverDataFlag=1;
    },
    /**
     * 删除人员
     */
    deletePerson : function () {
        var grid;
        if(kqDataMx_me.idSuff=="Mx"){
            grid  = kqDataMx_me.tableObj1.tablePanel;
        }else if (kqDataMx_me.idSuff=="NoneMx"){
            grid  = kqDataMx_me.tableObj2.tablePanel;
        }
        var selected = grid.getSelectionModel().getSelection();
        if(selected.length==0){
            Ext.showAlert(kq.datamx.msg.noselectedstaffs);
            return;
        }
        Ext.showConfirm(kq.datamx.msg.delconfirm,function(flag){
            if (flag =="yes"){
                var json = {};
                json.type = "deletePerons";
                json.scheme_id = kqDataMx_me.schemeId
                json.kq_duration = kqDataMx_me.kqDuration
                json.kq_year = kqDataMx_me.kqYear;
                var guidkeys = [];
                for (var i in selected) {
                    var record = selected[i];
                    guidkeys.push(record.get("guidkey"));
                }
                json.guidkeys = guidkeys;
                json.orgId = kqDataMx_me.orgId;
                var map = new HashMap();
                map.put("jsonStr", Ext.encode(json));
                Rpc({
                    functionId: 'KQ00021205', async: false, success: function (form) {
                        var res = Ext.decode(form.responseText);
                        var data = Ext.decode(res.returnStr);
                        if(data.return_code=="success"){
                            Ext.showAlert(kq.datamx.msg.delsuccess);
                            kqDataMx_me.loadSpData();
                        }else{
                            Ext.showAlert(data.return_msg);
                            return;
                        }
                    }
                    , scope: this
                }, map);
            }
            return;
        });
    },
    /**
     * 目前只有上报页面增加校验
     * 1、校验是否创建，没有则给出提示是否自动创建
     * 2、如果是已创建的，则自动校验是否有人员增减
     */
    isCheckData:function(){
    	if("0" != kqDataMx_me.viewType)
    		return;
    	// 如果已创建则直接校验人员增减是否有变动 并且 只有未提交状态与退回状态校验人员增减
    	if("3" != kqDataMx_me.operation && ("01" == kqDataMx_me.sp_flag || "07" == kqDataMx_me.sp_flag)){
    		kqDataMx_me.staffChange("0");
    		return;
    	}// 如果未创建则校验是否创建,只支持上报页面
    	else if(3 == kqDataMx_me.optRole && !("3" != kqDataMx_me.operation)){
    		var info = kqDataMx_me.kqYear+kq.dataAppeal.year+kqDataMx_me.kqDuration+kq.dataAppeal.month+kq.dataAppeal.isNewData;
    		Ext.showConfirm(info,function(flag){
    			if (flag =="yes"){
    				kqDataMx_me.createNewdata();
    			}
    			return;
    		});
    	}
    },
    /**
     * 重置 已批准已归档状态
     */
    kqDataReset:function(){
    	var msg = kq.datamx.label.kqDataSp.confirmmsg.replace("{0}",kq.datamx.label.kqDataSp.reset);
    	Ext.showConfirm(msg,function(flag){
            if (flag =="yes"){
		    	var jsonStr = {
		            type: 'rejectpersonnel',
		            resetFlag: '0',
		            scheme_id: kqDataMx_me.schemeId,
		            org_id: kqDataMx_me.orgId,
		            viewType: kqDataMx_me.viewType
		        };
		        var map = new HashMap();
		        map.put("jsonStr", JSON.stringify(jsonStr));
		        Rpc({
		            functionId: 'KQ00021201', async: false, success: function (form) {
		            	var result = Ext.decode(form.responseText);
		                var return_data=result.returnStr.return_data;
		                // 重置直接退回到 下级机构考勤员
		                Ext.each(return_data.datalist, function (data) {
		                    if (data.role_id == '3') {
		                    	kqDataMx_me.kqDataRejectCallBack(kqDataMx_me.orgId, data.userid, "3");
		                    }
		                });
		                return;
		            }, scope: this
		        }, map);
            }
            return;
        });
    },
    /**
     * 代确认的员工待办取消  置为已办
     */
    doReplaceConfirm:function(){
    	var map = new HashMap();
        var json = {};
        json.type="confirm";
        json.role_id = kqDataMx_me.optRole;
        json.org_id=kqDataMx_me.orgId;
        json.scheme_id = kqDataMx_me.schemeId;
        json.kq_duration = kqDataMx_me.kqDuration;
        json.kq_year = kqDataMx_me.kqYear;
        
        json.guidkeys = kqDataMx_me.replaceConfirmUsers;
        map.put("jsonStr", Ext.encode(json));
        Rpc({functionId: 'KQ00021201', async: true, success: function (form) {
                var result = Ext.decode(form.responseText);
                kqDataMx_me.replaceConfirmUsers = [];
            }
        }, map);
    },
    /**
     * 校验是否存在不可编辑的考勤项目 55748
     */
    checkEnableModifys: function(data){
    	var vals = data.split(",");
    	var bool = true;
    	Ext.each(vals, function (val) {
            if ((kqDataMx_me.otherParam.enableModifys + ",").indexOf(","+val+",") != -1) {
            	bool = false;
            	return bool;
            }
        });
    	return bool;
    },
    /**
     * 下载模板、导入数据
     */
    importExcel: function () {
		var me = this;
		var importWin = Ext.getCmp('importWinid');
	    if(importWin)
	    	importWin.close();
		
	    importWin = Ext.create('Ext.window.Window', {
			id: 'importWinid',
		    title: kq.card.importTitle,
		    height: 180,
		    width: 320,
		    modal:true,
		    layout: {
		        align: 'middle',
		        pack: 'center',
		        type: 'vbox'
			},
		    items: [{
			    layout: 'column',
			    border: false,
			    margin: '-20 0 0 0',
			    width: 240,	
			    items: [{
			        columnWidth: 0.5,
			        border: false,		        		            
			        html:kq.card.downloadTemplate,	     
			    },{
			        columnWidth: 0.5,
			        border: false,
			        items: [{
			            xtype: 'button',
			            text: kq.label.down,	            		            
			            handler: function (){
			            	me.exportTemplate();
			            }
			        }]
			    }]
		    },{
			    layout: 'column',
			    border: false,
			    margin: '30 0 0 0',
			    width: 240,	
			    items: [{
			        columnWidth: 0.5,
			        border: false,		        		            
			        html: kq.card.selectFile,		        
			    },{
			        columnWidth: 0.5,
			        border: false,
			        items: {
			            xtype: 'button',
			            id:'importCardData',
			            text: kq.card.browse,		            		            
			            listeners:{
			            	afterrender: me.selectFile
			    		}
			        }
			    }]
		    }]
		});
		
	    importWin.show();
	},
	
	exportTemplate: function () {
		 
		var me = this;
		Ext.MessageBox.wait(kq.label.downloading, kq.label.waiting);
		var map = new HashMap();
	    var json = {};
	    json.type="collect";
	    json.org_id=me.orgId;
	    json.scheme_id = me.schemeId;
	    json.kq_duration = me.kqDuration;
	    json.kq_year = me.kqYear;
	    json.showMx ="false";
	    json.flag = "down";
	    map.put("jsonStr",Ext.encode(json));
		Rpc({functionId:'KQ00021208',async:true,success:function(res){
            var result = Ext.decode(res.responseText);
            var data = Ext.decode(result.returnStr);
            if(data.return_code=="success"){
            	Ext.MessageBox.close();
                var filename = data.return_data.filename;
                filename = decode(filename);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true";
            }else{
                Ext.MessageBox.close();
                Ext.showAlert(data.return_msg);
            }
        },scope:this},map);
	},
	
	selectFile: function(){
		Ext.require('SYSF.FileUpLoad', function(){
			var uploadObj = Ext.create("SYSF.FileUpLoad",{
				isTempFile:true,
				VfsModules:VfsModulesEnum.KQ,
				VfsFiletype:VfsFiletypeEnum.doc,
				VfsCategory:VfsCategoryEnum.other,
				CategoryGuidKey:'',
				upLoadType:3,
				fileSizeLimit:'500MB',
				fileExt:"*.xls;*.xlsx;",
				buttonText:'',
				renderTo:"importCardData",
				success:kqDataMx_me.importData,
				isDelete:true,
				width:32,
				height:20
			});
			Ext.getDom("importCardData").childNodes[1].style.marginTop = "-20px";
		});
	},
	importData: function (list) {
		if(list.length < 0)
			return;
		
		var obj = list[0];
		if(obj){
			Ext.MessageBox.wait("", kq.card.importMsg);	
			var map = new HashMap();
		    var json = {};
		    json.type="collect";
		    json.org_id=kqDataMx_me.orgId;
		    json.scheme_id = kqDataMx_me.schemeId;
		    json.kq_duration = kqDataMx_me.kqDuration;
		    json.kq_year = kqDataMx_me.kqYear;
		    json.showMx ="false";
		    json.fileid =obj.fileid;
		    json.flag = "import";
		    map.put("jsonStr",Ext.encode(json));
			Rpc({functionId : 'KQ00021208',
				success : function (response){
					 var result = Ext.decode(response.responseText);
			         var data = Ext.decode(result.returnStr);
					Ext.MessageBox.close();
					if(data.return_code=="success"){
						var msg = data.return_data.list;
						if(msg) {
							var gridStore = Ext.create('Ext.data.Store', {
								fields:['id','message'],
								data: msg,
								autoLoad: true
							});
							
							var grid = Ext.create('Ext.grid.Panel', {
								store: gridStore,
								columns: [
									{ text: kq.label.rowNumberer, dataIndex: 'id', height:30,width:'10%' },
									{ text: kq.card.tipMsg, dataIndex: 'message', height:30,width:'89%' }
								],
								border:1,
								height: 320,
								stripeRows:true,
							    columnLines:true,
								width: "100%",
								listeners : {  
						    		render : function(gridPanel){
								    	Ext.create('Ext.tip.ToolTip', {
								    		target: gridPanel.id,
										    delegate:"td",
										    trackMouse: true,
										    renderTo: document.body,
										    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
								    	    listeners: {
											    beforeshow: function updateTipBody(tip) {
										            var div = tip.triggerElement.childNodes[0];
										            var title = "";
										            if (Ext.isEmpty(div))
										            	return false;
										        	    
											       	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight){
											       		var havea = div.getElementsByTagName("a");
											            if(havea != null && havea.length > 0){
											            	title = havea[0].innerHTML;
											            } else 
											            	title = div.innerHTML;
											       		
											       		title = trimStr(title);
											       		if(Ext.isEmpty(title))
											       			return false;
											       		
											       		tip.update("<div style='WORD-BREAK:break-all;'>"+title+"</div>");
											       	}else
											       		return false;
										        }
										    }
								    	});
								    	
					            	}
					        	} 
							});
							
							var win = Ext.create('Ext.window.Window', {
								title: kq.card.importTitle,
								height: 400,
								width: 500,
								modal:true,
								items: [grid],
								buttonAlign: 'center',
								buttons: [{
									text: kq.button.close,
									handler:function(){
										win.close();
									}
								}]
							});
							
							win.show();
						} else {
							Ext.showAlert("导入成功！");
							kqDataMx_me.loadSpData();
						}
					}else{
						Ext.showAlert(data.return_msg);
					} 
				}
			}, map);
		}
		
		var importWin = Ext.getCmp('importWinid');
	    if(importWin)
	    	importWin.close();
	},
	/**
	 * 签章图片
	 */
	signatureFileFunc:function(opt){
    	var me = kqDataMx_me;
     	// 机构审核人报批 或同意时  增加该审核人签章图片
     	if(("appeal"==opt || "approve"==opt) && "4"==me.optRole){
     		// 导出明细或汇总需要增加签章
     		var sig=Ext.widget('signatureFile',{
     			// 返回第几张图片
     			isGetMarkID:true,
     			currentUser: me.currentUser,
     			onsuccess: function(argument){
     				// 签章图片标识MarkID   签章记录标识signatureID  用户名username
     				me.spOptDetail(opt, "", "", "", argument[0]);
     			},
     			onerror: function(){
     				me.spOptDetail(opt);
     			}
     		});
     	}else{
     		me.spOptDetail(opt);
     	}
    },
    //填写意见弹窗
    fillmsgWin: function(opt,approvalMessage,sp_flag,org_id,userid,role_id){
    	var me = kqDataMx_me;
    	Ext.create("Ext.window.Window",{
            height: 300,
            width: 450,
        	title:"填写意见",
            layout: 'vbox',
            id: 'msgWin',
            align: 'stretch',
            scrollable: false,
            modal: true,
            border: false,
            closeAction: 'destroy',
            defaults: {
                width: 440,
                border: false
            },
            items: [{
                xtype: 'panel',
                height: 230,
                border: false,
                layout: 'fit',
                items: {
                    xtype: 'textareafield',
                    id: 'msgText',
                    value:approvalMessage
                }
            }],
            bbar: [ 
                '->',
                { 
                	xtype: 'button',
                	style:'margin-right:5px;',
                	text: kq.button.ok,
                	width:75,
                	height:25,
                	handler:function(){
                	if (me.times==1) {
						return;
					}
                	me.times=1;
                	 var map = new HashMap();
        		     var json = {};
        		     json.type="fillProcess";
        		     json.org_id=me.orgId;
        		     json.scheme_id = me.schemeId;
        		     json.kq_duration = me.kqDuration;
        		     json.kq_year = me.kqYear;
        		     json.sp_message =Ext.getCmp("msgText").value;
        		     json.sp_flag=sp_flag;
        		     map.put("jsonStr", Ext.encode(json));
        		        Rpc({
        		            functionId: 'KQ00021201', async: true, success: function (form) {
        		            	 var result = Ext.decode(form.responseText);
        		                 var returnStr = eval(result.returnStr);
        		                 var return_code = returnStr.return_code;
        		                 if (return_code == 'success') {
        		                	 if ("reject"==opt) {
        		                		 kqDataMx_me.spOptDetail("reject",org_id,userid,role_id);
									}else{
										var coverDataDiv = Ext.getDom("coverDataDiv");
										if(coverDataDiv && coverDataDiv.parentNode){
											coverDataDiv.parentNode.removeChild(coverDataDiv);
										}
										me.signatureFileFunc(opt);
									}
        		                 }
        		                var msgWin = Ext.getCmp("msgWin");
 	                            if(msgWin){
 	                            	msgWin.destroy();
 	                            }
        		            }
        		        },map);
                } },
                { xtype: 'button', text: kq.button.cancle,width:75,height:25,handler:function(){
                    var msgWin = Ext.getCmp("msgWin");
                    if(msgWin){
                    	msgWin.destroy();
                    }
                }},
                '->'
            ]
        }).show();
    }
});