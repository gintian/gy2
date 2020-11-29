//薪资发放主页面
Ext.define('SalaryUL.SalaryAccounting',{
    constructor:function(config){
    	accounting = this;
    	salaryid = config.salaryid;//薪资类别号
    	appdate = config.appdate;//业务日期
    	count = config.count;//次数
    	imodule = config.imodule;//薪资和保险区分标识  1：保险  否则是薪资
    	viewtype = config.viewtype;// 页面区分 0:薪资发放  1:审批  2:上报
    	accounting.returnflag = config.returnflag;
    	accounting.currentPage = config.currentPage;
    	accounting.encryptParam = config.encryptParam;
        Ext.util.CSS.createStyleSheet(".x-ssm-extender-drag-handle{display:none}","spreadsheet_extender");//消除选框拖拽点
        Ext.util.CSS.createStyleSheet(".x-grid-cell-inner-row-numberer{    background-image: none !important;background-color:transparent !important}","spreadsheet_extender");//消除选框拖拽点
    	this.init();
    },
   	init:function(){
	 	var map = new HashMap();
		map.put("salaryid",salaryid);
		map.put("appdate",appdate);
		map.put("count",count);
		map.put("imodule",imodule);
		map.put("viewtype",viewtype);
		map.put("returnflag",accounting.returnflag);
		map.put("encryptParam",accounting.encryptParam);
	    Rpc({functionId:'GZ00000001',async:false,success:accounting.getTableOK},map);
	},
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var flag=result.succeed;
		if(flag==true){
			salaryid=result.salaryid_encrypt; //从链接直接进入，需写回salaryid的加密值
			imodule = result.gz_module_encrypt;//薪资和保险区分标识  1：保险  否则是薪资
	    	viewtype = result.viewtype_encrypt;// 页面区分 0:薪资发放  1:审批  2:上报
	    	appdate = result.appdate;//业务日期
    		count = result.count;//次数

			accounting.isRedo=result.isRedo;//是否薪资重发数据
			accounting.verify_ctrl = result.verify_ctrl;//是否审核校验  1：是
	    	accounting.isTotalControl = result.isTotalControl;//是否总额控制  1：是
	    	accounting.sp_actor_str = result.sp_actor_str;//审批关系中定义的直接领导
	    	accounting.isNotSpFlag2Records = result.isNotSpFlag2Records;//薪资发放临时表中是否还含有没报审的记录 0:没有 1：有
	    	accounting.subNoShowUpdateFashion = result.subNoShowUpdateFashion;//提交的时候是否显示提交方式窗口 0：显示  否则不显示
	    	accounting.tar = result.tar;//hl:70前页面  hcm：70及以后页面
	    	accounting.viewtype=viewtype;// 页面区分 0:薪资发放  1:审批  2:上报
	    	accounting.appdate = appdate;//业务日期
	    	accounting.count = count;//次数
	    	accounting.returnflag = result.returnflag;
	    	accounting.imodule = imodule;//薪资和保险区分标识  1：保险  否则是薪资
	    	accounting.datetime = result.datetime;//个人所得税里面用到
            accounting.unitcodes=result.unitcodes;//业务范围，给选人控件
	    	accounting.cbase=result.cbase;//适用人员库
	    	accounting.handImportScope=result.handImportScope;//手工引入是否走人员范围加高级 0不走 1走
	    	accounting.allowEditSubdata=result.allowEditSubdata;//提交是否可以修改数据 '1':可以修改 '0':不可修改或需要审批
	    	accounting.ishave=result.ishave;//是否满足所属单位控制  0：满足 1：不满足
	    	accounting.onlyNameDesc=result.onlyNameDesc;
	    	accounting.tablesubModuleId=result.tablesubModuleId;
	    	accounting.sharedAdministratorFlag=result.sharedAdministratorFlag;//是否是共享管理员1：是;;0：不是
	    	accounting.bedit=result.bedit;//是否有提交按钮
	    	accounting.commonreportlist=result.commonreportlist;//薪资常用报表数据
			var conditions=result.tableConfig;
			var obj = Ext.decode(conditions);

            accounting.treeMenu = Ext.create('Ext.menu.Menu', {
                margin: '0 0 10 0',
                floating: true,
                id:'menuBar',
                renderTo: Ext.getBody(),
                items: [{
                    id:'copyitem',
                    text: "复制&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+C",
                    icon:'/images/new_module/shift_copy.png',
                    handler:function () {
                        accounting.getCells('raw',false);
                    }
                },{
                    id:'cutitem',
                    text: "剪切&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+X",
                    icon:'/images/new_module/shift_cut.png',
                    handler:function () {
                        accounting.getCells('raw',true);
                    }
                },{
                    id:'pasteitem',
                    text: "粘贴&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+V",
                    icon:'/images/new_module/shift_paste.png',
                    handler:function () {
                        accounting.putCellData();
                    }
                }]
            });
            // Ext.require('SalaryUL.TableCopyPlugins',function() {
            //     Ext.create("SalaryUL.TableCopyPlugins", {tableConfig: obj, tableId: result.tablesubModuleId});
            // });

            obj.beforeBuildComp = function(grid){
                grid.storeConfig.listeners={
                	'beforeload':function () {
                		if(Ext.getCmp("salaryaccounting_tablePanel")!=undefined) {
                            Ext.getCmp("salaryaccounting_tablePanel").getSelectionModel().deselectAll();
                        }
                    }
				};

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
                        accounting.getCells(format,erase);
                    },
                    //复写粘贴的方法
                    putCellData: function (data, format) {
                        accounting.putCellData(data, format);
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
                            accounting.disabledMenuItem('all', false);
                            //剪切板没有值的话，禁用粘贴菜单
                            if (!accounting.cellData) {
                                accounting.disabledMenuItem('pasteitem', true);
                            } else {
                                accounting.disabledMenuItem('pasteitem', false);
                            }
                        // }
                        accounting.treeMenu.showAt(e.getXY());
                    }
                }
            };

			obj.openColumnQuery = true;
			//取消表格控件右键代码型指标统计功能
            obj.contextAnalyse=false;
			var sp_flagname=String(obj.customtools[obj.customtools.length-1]);
			//判断当前薪资账套数据是否已提交
			if(sp_flagname.indexOf('结束')>-1) {
				accounting.isSubed=true;
			}else{
                accounting.isSubed = false;
            }
			if(document.getElementById('salaryaccountingsave')){
				obj.onChangePage=function(){document.getElementById('salaryaccountingsave').click();};
			}
			salaryObj = new BuildTableObj(obj);
			//单列统计 执行之前调用的方法，返回过滤后的列
	//		salaryObj.beforeColumnAnalyse(accounting.beforeColumnAnalyse);

			var params = new Object();
			params.salaryid=salaryid;
			params.appdate=appdate;
			params.count=count;
			params.imodule=imodule;
			params.viewtype=viewtype;
			params.subModuleId="salaryAccounting";
			Ext.getCmp("salaryaccounting_querybox").setCustomParams(params);
			if(appdate=="~32iIeo~37kAcbUPAATTP~33HJDPAATTP"){//如果业务日期没有的空帐套，那么把加密串改成空字符串，以免报错  zhaoxg add 2016-11-1
				appdate="";
			}
		}else{
			Ext.showAlert(result.message,function(){
				var modu = '0';
				var type = '0';
				if('Z~30DuTtqmt~33kPAATTP~33HJDPAATTP'==imodule){
					modu = '1';
				}
				if('Z~30DuTtqmt~33kPAATTP~33HJDPAATTP'==viewtype){
					type = '1';
				}else if('yMs~36zFRuw~36IPAATTP~33HJDPAATTP'==viewtype){
					type = '2';
				}
				if(!!accounting.currentPage)//如果是配的链接这块是没有页码值的，也没有返回的界面，这种情况只是弹窗个提醒，确定后空白页就行了 zhaoxg add 2016-10-19
					window.location.href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype="+type+"&imodule="+modu+"&currentPage="+accounting.currentPage+"";
			},this);
		}
	},

    getCells: function (format, erase) {
		var me=this;
        var cmp = Ext.getCmp("salaryaccounting_tablePanel"),
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
                    if (!accounting.clickCell(record)) {
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
            me.cellData = Ext.encode(ret);
            Ext.getCmp("salaryaccounting_tablePanel").plugins[0].cancelEdit();
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
            view = Ext.getCmp("salaryaccounting_tablePanel").getView(),
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
                if(!accounting.clickCell(context.record)){
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
                    if(!accounting.clickCell(destination.record)){
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
        Ext.getCmp("salaryaccounting_tablePanel").plugins[0].cancelEdit();
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
    deleteData: function () {//删除薪资数据
        if (salaryObj.tablePanel.getSelection().length == 0) {
            Ext.showAlert(gz.msg.selectDelRecard);
            return;
        }

        Ext.showConfirm(gz.msg.sureDelRecard, function (value) {
            if (value != "yes")
                return;

            var delArray = new Array();
            var i = 0;
            Ext.each(salaryObj.tablePanel.getSelection(), function (recard) {
                delArray[i] = recard.data;
                i++;
            });
            var map = new HashMap();
            map.put("salaryid", salaryid);
            map.put("imodule", imodule);
            map.put("appdate", appdate);
            map.put("count", count);
            map.put("deletedata", delArray);
            Rpc({
                functionId: 'GZ00000026', success: function (data) {
                    var result = Ext.decode(data.responseText);
                    if (result.succeed == true) {

                        accounting.loadStore();
                        if (result.isEnd == true) {//如果状态有修改，则将右上角状态修改为最新状态
                            document.getElementById("sp_flagname").innerHTML = gz.label.end
                        }
                    } else {
                        Ext.showAlert(result.message);
                    }
                }
            }, map);
        });
    },

	//单列统计 执行之前调用的方法，返回过滤后的列
	beforeColumnAnalyse:function(columns){
		var columnsArr = new Array();
		Ext.Array.each(columns,function(column,index){
			var dataIndex = column.dataIndex.toLowerCase();
			if(dataIndex != 'a0000'.toLowerCase() && dataIndex != 'a00z3'.toLowerCase() && dataIndex != 'a00z1'.toLowerCase()){
				columnsArr.push(column);
			}
		});
		return columnsArr;
	},

	newSalaryTable:function(){//新建工资表
		Ext.require('SalaryUL.CreateAccount',function(){
			Ext.create("SalaryUL.CreateAccount",{salaryid:salaryid});
		})
	},
	defineFormula:function(){//定义计算公式
		if(accounting.ishave=="1"){
			accounting.showIshaveUnits();
			return;
		}
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module:'1',id:salaryid});
		})
	},

	//定义审核公式
	defineSpFormula:function(){
		if(accounting.ishave=="1"){
			accounting.showIshaveUnits();
			return;
		}
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module:'1',id:salaryid,formulaType:'2'});
		})
	},

	compute:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.Compute',function(){
			Ext.create("SalaryUL.Compute",{salaryid:salaryid,appdate:appdate,count:count,imodule:imodule,viewtype:viewtype});
		})
	},
	// 变动比对:检查是否存在变动比对表，没有则显示提示。
	checkChangeCompare_account:function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.MessageBox.wait("正在比对，请稍候...", "等待");
		var map = new HashMap();
		map.put("salaryid",salaryid);
		map.put("imodule",imodule);
		map.put("appdate",appdate);
		map.put("count",count);
	    Rpc({functionId:'GZ00000203',success:function(data) {
	    	var checkResult = Ext.decode(data.responseText); // 查询结果
	    	if(!checkResult.succeed){
				Ext.showAlert(checkResult.message);
			}else{
		    	if (checkResult.isExistAdd === "0"
		    		&& checkResult.isExistDel === "0"
		    		&& checkResult.isExistInfo === "0"
		    		&& checkResult.isExistStop === "0") {
		    		var salaryRs = checkResult.salaryid;
		    		var cnameRs = checkResult.cname;
		    		Ext.Msg.alert(common.button.promptmessage, salaryRs+"."+cnameRs+gz.msg.nothaveperson);
		    		return;
				}
		    	accounting.openChangeComparePage_account(salaryid, imodule, checkResult);
	    	}
	    },scope:this},map);
	},
	// 变动比对:打开变动比对页面
	openChangeComparePage_account:function(salaryid, imodule, checkResult){
		var obj = new Object();
		obj.salaryid = salaryid;
		obj.imodule = imodule;
		obj.isExistAdd = checkResult.isExistAdd;//新增  1:有0:无
		obj.isExistDel = checkResult.isExistDel;//减少  1:有0:无
		obj.isExistInfo = checkResult.isExistInfo;//信息变动  1:有0:无
		obj.isExistStop = checkResult.isExistStop;//停发  1:有0:无
		obj.dbname = checkResult.dbname;//dbname转化usr=>在职人员库
		obj.pathform=1;//标示来源页面，1来自人员列表

		salaryObj.getMainPanel().removeAll(false);
		Ext.require('SalaryUL.ComparisonWithFile', function(){
			ChangeCompareGlobal = Ext.create("SalaryUL.ComparisonWithFile", obj);
		});
		Ext.MessageBox.close();
	},
	//手工引入
	handImportMen:function(){
		if(appdate=='')
        {
            Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
            return;
        }
		Ext.require('SalaryUL.importmen.HandImportMen',function(){
			Ext.create("SalaryUL.importmen.HandImportMen",{salaryid:salaryid,cbase:accounting.cbase,Scope:accounting.handImportScope,orgid:accounting.unitcodes,appdate:accounting.appdate});
		})
	},

	loadStore:function(){
        Ext.getCmp("salaryaccounting_tablePanel").getStore().load();
	},
	reloadStore:function(){
		window.location.href="/module/gz/salaryaccounting/SalaryAccounting.html?salaryid="+salaryid+"&appdate="+accounting.appdate+"&count="+accounting.count+"&imodule="+accounting.imodule+"&viewtype="+accounting.viewtype+"&currentPage="+accounting.currentPage+"&returnflag="+accounting.returnflag+"";
//		SalaryTemplateGlobal.returnSalaryAccounting();
	},
    //判断是否可编辑
	clickCell:function(record){
		var rowdata = record.data;
		var sp_flag = rowdata.sp_flag==undefined?undefined:rowdata.sp_flag.split("`")[0];
		var sp_flag2 = rowdata.sp_flag2==undefined?undefined:rowdata.sp_flag2.split("`")[0];
		var flag=sp_flag==undefined?sp_flag2:sp_flag;
		if(sp_flag2!=undefined){//如果是共享账套
            //非共享管理员 仅可修改起草和驳回数据
            if(accounting.sharedAdministratorFlag == "0") {
                if(sp_flag2!="01"&&sp_flag2!="07") {
                    return false;
                }
            }else {//是共享管理员
				if(sp_flag!=undefined){//走审批 可修改起草 驳回数据
					if(sp_flag=='01'||sp_flag=='07'){
						return true;
					}
				}else{//不走审批
                    if(flag!= "06"){//不是结束状态数据都可以修改
                        return true;
                    }
				}
			}
		}
		//判断结束状态数据是否可修改
        //allowEditSubdata允许提交后更改数据 "1"可以修改 '0':不可修改或需要审批
		if(accounting.allowEditSubdata == "1"&&flag=="06"){//已结束且可以修改
			return true;
		}
		//无特殊设置 仅可修改起草和驳回状态数据
        if(flag!="01"&&flag!="07") {
            return false;
        }
        return true;
	},
	returnBack:function(){
		if(accounting.returnflag=="menu"){
			var modu = '0';
			var type = '0';
			//【60387】VFS+UTF-8：保险管理/保险核算，点类别进入后，就点返回，结果返回到了薪资管理/薪资发放界面了，不对
			//PubFunc加密算法换了，加密串需要跟着变化
			if('gVsvXtmedSTicnZa~35NmIqiag'==accounting.imodule){
				modu = '1';
			}
			if('gVsvXtmedSTicnZa~35NmIqiag'==accounting.viewtype){
				type = '1';
			}else if('E~38HsCQY~33F~37QJlRaTstWW~36g'==accounting.viewtype){
				type = '2';
			}
			window.location.href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype="+type+"&imodule="+modu+"&currentPage="+accounting.currentPage+"";
		}else if(accounting.returnflag=="portal_list"){
            window.location.href="/general/template/matterList.do?b_query=link";
        }else{
			if(accounting.tar=="hl")
		    {
		   		window.location.href="/templates/index/portal.do?b_query=link";
		    }else if(accounting.tar=="hcm"){
		   		window.location.href="/templates/index/hcm_portal.do?b_query=link";
		    }
		}
	},
	backToAccount:function(){
		window.location.href="/module/gz/salaryaccounting/SalaryAccounting.html?salaryid="+salaryid+"&appdate="+appdate+"&count="+count+"&imodule="+imodule+"&viewtype="+viewtype+"&currentPage="+accounting.currentPage+"&returnflag="+accounting.returnflag+"";
	},
	//发送通知入口
	sendMsg:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.SendMsg',function(){
			Ext.create('SalaryUL.SendMsg',{salaryid:salaryid,appdate:appdate,count:count});
		})
	},
	//薪资发放-导出excel
	exportData:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.inout.ExportData',function(){
			Ext.create("SalaryUL.inout.ExportData",{salaryid:salaryid,appdate:appdate,imodule:accounting.imodule});
		})
	},
	//批量修改
	batchUpdate:function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.BatchUpdate',function(){
			Ext.create("SalaryUL.BatchUpdate",{salaryid:salaryid,imodule:imodule});
		})
	},
	//批量引入
	batchImport:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.BatchImport',function(){
			Ext.create("SalaryUL.BatchImport",{salaryid:salaryid,imodule:imodule,appdate:appdate,type:'0',count:count,viewtype:viewtype});
		})
	},
	//重新导入
	reimport:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.BatchImport',function(){
			Ext.create("SalaryUL.BatchImport",{salaryid:salaryid,imodule:imodule,appdate:appdate,type:'1',count:count,viewtype:viewtype});
		})
	},
	//银行报盘
	updisk:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.UpDisk',function(){
			Ext.create('SalaryUL.UpDisk',{salaryid:salaryid,model:"0",appdate:appdate});
		})
	},
	//数据比对
	changesmore:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.MessageBox.wait("正在比对，请稍候...", "等待");
		Ext.require('SalaryUL.Changesmore',function(){
			Ext.create('SalaryUL.Changesmore',{returnBackFunc:accounting.reloadStore,salaryid:salaryid,imodule:imodule,appdate:appdate,type:'1',addflag:'1',minusflag:'1',changeflag:'1',count:count});
		})
	},
	//上传表格
	importTable:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		var query = Ext.getCmp('salaryaccounting_querybox');
		query.removeAllKeys();
		Ext.require('SalaryUL.inout.ImportTable',function(){
			Ext.create("SalaryUL.inout.ImportTable",{salaryid:salaryid,appdate:appdate,count:count,imodule:imodule,viewtype:viewtype,onlynamedesc:accounting.onlyNameDesc});
		})
	},
	//按模板
	importAsTemplate:function(){
		Ext.require('SalaryUL.ImportAsTemplate', function(){
			Ext.create("SalaryUL.ImportAsTemplate",{returnBackFunc:accounting.loadStore,download:'GzGlobal.downLoadTemp',see:'GzGlobal.importTemplTable'});
		});
	},
    //审核
    // type: 1报批 2提交  其他:点击审核按钮
    verify: function (type) {
        if (appdate == '') {
            Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
            return;
        }
        if (accounting.isHaveChange()) {
            Ext.showAlert("数据有变动，请进行保存！");
            return;
        }
        var map = new HashMap();
        map.put("salaryid", salaryid);
        map.put("appdate", appdate);
        map.put("count", count);
        map.put("imodule", imodule);
        map.put("viewtype", viewtype);
        if (accounting.isTotalControl == '1' && (type=='1'||type=='2'))
            Ext.MessageBox.wait("正在审核、总额校验，请稍候...", "等待");
        else
            Ext.MessageBox.wait("正在审核，请稍候...", "等待");
        Rpc({
            functionId: 'GZ00000016', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                if (result.succeed == true) {
                    if (result.msg == 'yes') {
                        if (type=='1'||type=='2') {
                            accounting.salaryControl(type);//审核完毕 进行总额检验
                        } else {
                            Ext.MessageBox.close();
                            Ext.showAlert('审核通过！');
                        }
                    } else if (result.fileName.length > 0) {
                        Ext.MessageBox.close();
                        Ext.showAlert("审核不通过！");
                        var fileName = getDecodeStr(result.fileName);
                        window.location.target = "_blank";
                        window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
                    } else {
                        Ext.MessageBox.close();
                        Ext.showAlert("审核不通过！");
                    }
                } else {
                    Ext.MessageBox.close();
                    Ext.showAlert(result.message);
                }

            }
        }, map);
    },

	//设置业务日期
	setAppDate:function(pick,date){
		var fm = Ext.Date.format(date, "Y.m.d") //对时间格式化
		var msg="您确定要设置业务日期为："+fm+"?";
		Ext.MessageBox.confirm('信息提示',msg, function(optional){
    		if(optional=='yes'){
    			var map = new HashMap();
    			map.put("appdate",fm);
    			Rpc({functionId:'GZ00000150',async:false,success:function(form,action){
    				var result = Ext.decode(form.responseText);
    				if(!result.succeed){
    					Ext.showAlert("设置失败！错误信息："+result.message);
    				}
    			}},map)
    		}
    	} )
	},

	//重置业务日期
	reSetGzDate:function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.ReSetGzDate',function(){
			Ext.create("SalaryUL.ReSetGzDate",{salaryid:salaryid});
		})
	},

	//下载模板数据
	downLoadTemp:function(){
		if(appdate=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, "请选择业务日期！");
	        return;
	    }
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.inout.ExportData',function(){//typeflag=2表示下载模板，1或者空为导出 sunjian2017-05-31
			Ext.create("SalaryUL.inout.ExportData",{salaryid:salaryid,typeflag:"2",appdate:appdate,imodule:accounting.imodule});
		})
	},

	//导入下载模板
	importTemplTable :function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.require('SalaryUL.inout.ImportTemplTable',function(){
			Ext.create("SalaryUL.inout.ImportTemplTable",{salaryid:salaryid,appdate:appdate,count:count});
		})
	},

	//薪资重发
	reDoGz :function(){
		if(accounting.isRedo=='1')
		{
			Ext.showAlert("数据不允许重复重发!");
			return;
		}
		Ext.require('SalaryUL.ReDoGz',function(){
			Ext.create("SalaryUL.ReDoGz",{salaryid:salaryid});
		})
	},
	//同步人员顺序
	syncgzemp:function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		var map = new HashMap();
		map.put("salaryid",salaryid);
		map.put("viewtype",viewtype);
		Rpc({functionId:'GZ00000061',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			if(result.succeed){
//				SalaryTemplateGlobal.returnSalaryAccounting();
				Ext.showAlert("同步人员顺序成功！");
                accounting.loadStore();
			}else{
				Ext.showAlert("同步人员顺序失败！错误信息："+result.message);
			};
		}},map);
	},

	//引入计件薪资
	importPiece:function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.MessageBox.confirm('信息提示','您确定要引入计件薪资吗？', function(optional){
    		if(optional=='yes'){
    			var map = new HashMap();
    			map.put("salaryid",salaryid);
    			map.put("appdate",appdate);
    			Rpc({functionId:'GZ00000062',async:false,success:function(form,action){
    				var result = Ext.decode(form.responseText);
    				if(result.succeed){
    					accounting.loadStore();
//    					SalaryTemplateGlobal.returnSalaryAccounting();
    				}else{
    					Ext.showAlert("引入计件薪资失败！错误信息："+result.message);
    				}
    			}},map)
    		}
    	} )
	},
	//报审
	gzAproval:function(){
		if(accounting.isHaveChange()){
			Ext.showAlert("数据有变动，请进行保存！");
			return;
		};
		Ext.Msg.confirm(common.button.promptmessage,"确认报审全部数据吗？",
			function(id){
				if(id=='yes'){
					if(accounting.verify_ctrl == '1') {//进行审核公式控制,
						var map = new HashMap();
						map.put("salaryid",salaryid);
						map.put("appdate",appdate);
						map.put("count",count);
						map.put("imodule",imodule);
						map.put("viewtype",viewtype);
						Ext.MessageBox.wait("正在审核，请稍候...", "等待");
					    Rpc({functionId:'GZ00000016',success:function(form,action){
					    	Ext.MessageBox.close();
					    	var result = Ext.decode(form.responseText);
					    	var flag=result.succeed;
							if(flag==true){
								if(result.msg=='yes'){
									accounting.gzAprovalOper();
						    	}else if(result.fileName.length>0){
                                    Ext.showAlert("审核不通过！");
                                    var fileName = getDecodeStr(result.fileName);
                                    window.location.target = "_blank";
                                    window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
						    	}else{
						    		Ext.showAlert("审核不通过！");
						    	}
							}else{
								Ext.showAlert(result.message);
							}

					    }},map);
					}else {
						accounting.gzAprovalOper();
					}
				}else{
					return;
				}
			}
		);
	},
	gzAprovalOper:function() {
		var map = new HashMap();
		map.put("salaryid",salaryid);
		map.put("appdate", appdate);
		map.put("count", count);
        map.put("imodule", imodule);
		map.put("opt","2");//1:驳回 2：报审
		Ext.MessageBox.wait("正在报审，请稍候...", "等待");
	    Rpc({functionId:'GZ00000017',success:function(form,action){
	    	Ext.MessageBox.close();
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag){
				accounting.loadStore();
			}else{
				Ext.showAlert(result.message);
			}

	    }},map);
	},
    //驳回
    gzReject: function () {
        if (accounting.isHaveChange()) {
            Ext.showAlert("数据有变动，请进行保存！");
            return;
        }
        var selectData = salaryObj.tablePanel.getSelectionModel().getSelection(true);
        var selectGzRecords = "";
        var isHaveData=false;
        for (var j = 0; j < selectData.length; j++) {
            selectGzRecords += selectData[j].data.a0100_e + "/";
            selectGzRecords += selectData[j].data.nbase1_e + "/";
            selectGzRecords += selectData[j].data.a00z0 + "/";
            selectGzRecords += selectData[j].data.a00z1;
            selectGzRecords += "#";
            if(selectData[j].data.sp_flag2=='02')
                isHaveData=true;
        }
        if(!isHaveData&&selectData.length!=0){
            Ext.showAlert(gz.msg.selectAppealData);//"请选择已报批的数据！"
            return;
		}
		var msg=selectData.length==0?gz.msg.isRejectAllData:gz.msg.isRejectSelectData;//确认驳回全部数据吗 确认驳回选择的数据吗
        Ext.Msg.confirm(common.button.promptmessage, msg,
            function (c) {
                if (c == 'yes') {
                    var win = Ext.widget("window", {
                        title: '驳回原因',
                        height: 300,
                        width: 500,
                        layout: 'fit',
                        modal: true,
                        closeAction: 'destroy',
                        items: [{
                            xtype: 'panel',
                            border: false,
                            items: [{
                                border: false,
                                xtype: 'textareafield',
                                id: 'rejectCause',
                                width: 485,
                                height: 220
                            }],
                            buttons: [
                                {xtype: 'tbfill'},
                                {
                                    text: common.button.ok,
                                    handler: function () {
                                        var map = new HashMap();
                                        map.put("salaryid", salaryid);
                                        map.put("opt", "1");//1:驳回 2：报审
                                        map.put("selectGzRecords", selectGzRecords);
                                        map.put("rejectCause", Ext.getCmp("rejectCause").getValue());
                                        win.close();
                                        Ext.MessageBox.wait("正在驳回，请稍候...", "等待");
                                        Rpc({functionId: 'GZ00000017', success: function (form, action) {
                                                Ext.MessageBox.close();
                                                var result = Ext.decode(form.responseText);
                                                var flag = result.succeed;
                                                if (flag) {
                                                    accounting.loadStore();
                                                } else {
                                                    Ext.showAlert(result.message);
                                                }
                                            }
                                        }, map);
                                    }
                                },
                                {
                                    text: common.button.cancel,
                                    handler: function () {
                                        win.close();
                                    }
                                },
                                {xtype: 'tbfill'}
                            ]
                        }]
                    });
                    win.show();
                }
            });
    },
    //薪资发放报批
    appeal: function () {
        if (accounting.isHaveChange()) {
            Ext.showAlert("数据有变动，请进行保存！");
            return;
        }
        //是否有未报审记录
        if (accounting.isNotSpFlag2Records == '1') {
            Ext.Msg.confirm(common.button.promptmessage, "有未报审的记录,是否继续报批？",
                function (id) {
                    if (id == 'yes') {
                        //verify_ctrl==1 审核公式控制
                        if (accounting.verify_ctrl == '1') {
                            accounting.verify("1");
                        } else {//如果没设置审核那么直接去执行总额校验
                            if (accounting.isTotalControl == '1')
                                Ext.MessageBox.wait("正在总额校验,请稍候...", "等待");
                            else
                                Ext.MessageBox.wait("正在报批,请稍候...", "等待");
                            accounting.salaryControl("1");//由于总额检验交易类中判断是否有可报批数据 所以必走
                        }
                    }
                });
        } else {
            //verify_ctrl==1 审核公式控制
            if (accounting.verify_ctrl == '1') {
                accounting.verify("1");
            } else {//如果没设置审核那么直接去执行总额校验
                if (accounting.isTotalControl == '1')
                    Ext.MessageBox.wait("正在总额校验,请稍候...", "等待");
                else
                    Ext.MessageBox.wait("正在报批,请稍候...", "等待");
                accounting.salaryControl("1");//由于总额检验交易类中判断是否有可报批数据 所以必走
            }
        }
    },
    //报批和提交必走的数据验证方法 包括总额校验、可操作数据条数验证 type: 1:报批 2：提交
    salaryControl: function (type) {
        var map = new HashMap();
        map.put("salaryid", salaryid);
        map.put("viewtype", viewtype);
        map.put("appdate", appdate);
		map.put("count", count);
        map.put("type", type);//1:报批 2：提交
        Rpc({
            functionId: 'GZ00000018', success: function (form, action) {
                Ext.MessageBox.close();
                var name = type == "1" ? gz.label.appeal : gz.label.submit;
                var result = Ext.decode(form.responseText);
                if (result.succeed == true) {
                    var dataCount = result.dataCount;
                    if (dataCount == 0) {
                        Ext.showAlert(gz.msg.notExistsData.replace("{0}", name));
                    } else {
                        if (result.info == 'success') {//通过总额控制
                        	Ext.showConfirm(gz.msg.canUseData.replace("{0}", name).replace("{1}", dataCount),
                                function (s) {
                                    if (s == "yes") {
                                        if (type == "1") {//报批
                                            accounting.appealSelectObjective();
                                        } else if (type == "2") {//提交
                                            accounting.doSubmit();//提交数据
                                        }
                                    }
                                }
                            );
                        } else {//未通过
                            if (result.ctrlType == "0") {//总额校验不强行控制
                            	Ext.showConfirm(result.info + "&nbsp",
                                    function (id) {
                                        if (id == 'yes') {
                                            Ext.Msg.confirm(common.button.promptmessage, gz.msg.canUseData.replace("{0}", name).replace("{1}", dataCount),
                                                function (s) {
                                                    if (s == "yes") {
                                                        if (type == "1") {//报批
                                                            accounting.appealSelectObjective();
                                                        } else if (type == "2") {//提交
                                                            accounting.doSubmit();//提交数据
                                                        }
                                                    }
                                                }
                                            );
                                        }
                                    }
                                );
                            } else {
                                Ext.showAlert(result.info);
                            }
                        }


                    }
                } else {
                    Ext.showAlert(result.message);
                }
            }
        }, map);
    },
	//报批选人操作
    appealSelectObjective:function(){
        //定义了直接领导
		if(accounting.sp_actor_str.length>0)
		{
			var temps=getDecodeStr(accounting.sp_actor_str).split("`");
			if(temps.length==1)
			{
				Ext.Msg.confirm(common.button.promptmessage,"确认报批吗？",
					function(id){
						if(id=='yes'){
							accounting.doAppeal(temps[0].split("##")[3]);
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
					var textName="";
					//obj.boxLabel=_temps[0]+"/"+_temps[1]+"/"+_temps[2];
					obj.boxLabel=_temps[0]+"/"+_temps[1];//有全称显示全称 没有显示用户名。全称为空时 全称=用户名
					obj.name='rb';
					obj.inputValue=_temps[3];
					obj.style='padding-top:9px;';
					colums[i]=obj;
				}

				if(Ext.util.CSS.getRule(".x-form-cb-default"))
			    	  Ext.util.CSS.updateRule(".x-form-cb-default","margin-top","14px");

                var win=Ext.widget("window",{
                    title:'请选择报批人',
                    height:380,
                    width:295,
                    layout:'fit',
                    modal:true,
                    closeAction:'destroy',
                    items: [{
                        xtype:'radiogroup',
                        columns:1,//一列
                        vertical:true,
                        autoScroll:true,
                        id:'state',
                        width:255,
                        height:1000,
                        items:colums
                    }],
                    buttons:[
                        {xtype:'tbfill'},
                        {
                            text:common.button.ok,
                            handler:function(){
                                if(Ext.isDefined(Ext.getCmp('state').getValue().rb)) {
                                    Ext.Msg.confirm(common.button.promptmessage,"确认报批吗？",
                                        function(id){
                                            if(id=='yes'){
                                                var appealObject = Ext.getCmp('state').getValue();
                                                win.close();
                                                accounting.doAppeal(appealObject.rb);//单选框组的获取是根据自定义的name字段来取得哪个选中了
                                            }else{
                                                return;
                                            }
                                        }
                                    );
                                }else {
                                    Ext.showAlert("请选择报批人！");
                                }
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
                });
			    win.show();
			}
		}else{
			var f = document.getElementById("gzappeal");
			var p = new PersonPicker({
				multiple: false,
				isSelfUser:false,//是否选择自助用户
				selfUserIsExceptMe:false,
				isMiddle:true,//是否居中显示
				extend_str:"salary/"+salaryid,//薪资选人控件个性化标注，用于控件中薪资权限的控制
				callback: function (c) {
					var appealObject=c.id;
					Ext.Msg.confirm(common.button.promptmessage,"确认报批吗？",
						function(id){
							if(id=='yes'){
								accounting.doAppeal(appealObject);
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
    //实际报批操作 报批的最后一步 报批按钮事件为 appeal
    doAppeal: function (appealObject) {
        var map = new HashMap();
        map.put("salaryid", salaryid);
        map.put("appdate", appdate);
        map.put("count", count);
        map.put("appealObject", appealObject);
        map.put("fromPending",accounting.returnflag=='menu'?'0':'1');
        Ext.MessageBox.wait("正在报批，请稍候...", "等待");
        Rpc({
            functionId: 'GZ00000019', success: function (form, action) {
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                var flag = result.succeed;
                if(flag){
                    //剩余可批条数
                    var listNumber=result.lastNumber;
                    //从待办进来，且剩余可批条数为0，那么跳回待办页面
                    if(listNumber==0&&accounting.returnflag!='menu'){
                        accounting.returnBack();
                    }else {
                        //accounting.reloadStore();
                        //改为不再刷新整个页面 zhanghua 2018-06-21
                        accounting.loadStore();
                        document.getElementById("sp_flagname").innerHTML = accounting.isRedo == "1" ? gz.label.execute + "&nbsp" + gz.label.again : gz.label.execute;
                    }
                } else {
                    Ext.showAlert(result.message);
                }
            }
        }, map);
    },
    //提交操作
    submit: function () {
        if (accounting.isHaveChange()) {
            Ext.showAlert("数据有变动，请进行保存！");
            return;
        }
        if (accounting.isSubed)//数据已提交
        {
            Ext.Msg.confirm(common.button.promptmessage, "数据已提交，是否重新提交？", function (id) {
                if (id == 'yes') {
                    accounting.submitVerification();
                }
            });
        }
        else
            accounting.submitVerification();
    },
    //提交验证
    submitVerification: function () {
        if (accounting.isNotSpFlag2Records == '1') {
            Ext.Msg.confirm(common.button.promptmessage, "有未报审的记录,是否进行提交？",
                function (id) {
                    if (id == 'yes') {
                        //subNoShowUpdateFashion ==0 显示数据提交方式
                        if (accounting.subNoShowUpdateFashion == '0') {
                            accounting.flag = "ff";
                            accounting.salaryid = salaryid;
                            Ext.require('SalaryUL.Submit', function () {
                                Ext.create("SalaryUL.Submit", accounting);
                            })
                        } else {
                            if (accounting.verify_ctrl == '1') {
                                //审核公式控制
                                accounting.verify("2");
                            } else {//如果没设置审核那么直接去执行总额校验
                                if (accounting.isTotalControl == '1')
                                    Ext.MessageBox.wait("正在总额校验，请稍候...", "等待");
                                else
                                    Ext.MessageBox.wait("正在提交，请稍候...", "等待");
                                accounting.salaryControl("2");
                            }
                        }
                    }
                }
            );
        } else {
            if (accounting.subNoShowUpdateFashion == '0') {
                accounting.flag = "ff";
                accounting.salaryid = salaryid;
                Ext.require('SalaryUL.Submit', function () {
                    Ext.create("SalaryUL.Submit", accounting);
                })
            } else {
				if (accounting.verify_ctrl == '1') {
					//审核公式控制
					accounting.verify("2");
				} else {//如果没设置审核那么直接去执行总额校验
					if (accounting.isTotalControl == '1')
						Ext.MessageBox.wait("正在总额校验，请稍候...", "等待");
					else
						Ext.MessageBox.wait("正在提交，请稍候...", "等待");
					accounting.salaryControl("2");
				}
			}
        }
    },
	//实际提交操作。
	doSubmit:function(){
		var map = new HashMap();
		map.put("salaryid",salaryid);
		map.put("appdate",appdate);
		map.put("count",count);
		map.put("subNoShowUpdateFashion",accounting.subNoShowUpdateFashion);
		Ext.MessageBox.wait("正在提交，请稍候...", "等待");
	    Rpc({functionId:'GZ00000022',success:function(form,action){
	    	Ext.MessageBox.close();
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag){
				//提交改为不再刷新整个页面 zhanghua 2018-06-21
				//重发的还是需要整个刷新，以添加重置业务日期等按钮
                if(accounting.isRedo=='1') {
                    accounting.reloadStore();
                }else{
                    accounting.loadStore();
                    var sp_flag=result.sp_flag;
                    if(sp_flag=="06"){
                        accounting.isSubed=true;
                        //如果状态有修改，则将右上角状态修改为最新状态
                        document.getElementById("sp_flagname").innerHTML = gz.label.end
                    }
				}

			}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},

	//薪资报表
	gzReport:function()
	{
	    if(appdate==''||count=='')
	    {
	    	Ext.Msg.alert(common.button.promptmessage, gz.msg.selectbosdateand);//请选择业务日期
	        return;
	    }
        Ext.require('SalaryReport.SalaryReport', function(){
			Ext.create("SalaryReport.SalaryReport",{salaryid:salaryid,gz_module:accounting.imodule,appdate:appdate,count:count,model:'0',tablesubModuleId:accounting.tablesubModuleId,viewtype:viewtype});
		});
	},
	//所得税管理
	searchTax:function(){
  		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'SearchTaxUL': '../../gz/tax',
				'SYSF':'../../../components/fileupload',
				'EHR': '../../../components',
				'Date':'../../../module/gz/tax'
			}
		});
		Ext.onReady(function(){
			Ext.require('SearchTaxUL.SearchTax', function(){
				SalaryTypeGlobal = Ext.create("SearchTaxUL.SearchTax",{salaryid:salaryid,datetime:accounting.datetime});
			});
		});
	},
	showIshaveUnits:function(){
		Ext.showAlert("您没有修改这个薪资类别的权限！");
	},
	isHaveChange:function(){//判断数据是否存在变化
		var store=salaryObj.tablePanel.getStore();
		var list=store.getModifiedRecords();
		var buttonSave=Ext.getCmp('salaryaccountingsave');
		if(list.length>0)
			if(buttonSave)
				return true;
		else
			return false;

	},
	distribute:function() {
		Ext.require('SalaryTypeUL.applicationorganization.ApplicationOrganization', function(){
			Ext.create("SalaryTypeUL.applicationorganization.ApplicationOrganization",{salaryid:salaryid,flag:"1",imodule:imodule,a00z2:appdate,a00z3:count});
		});
	},
	//打开常用报表下拉列表
    openCommon_reportCombo:function () {

	    if(accounting.commonreportlist.length==1){
            var record=accounting.commonreportlist[0];
            var tabid=record.id;
            var rsid=record.rsid;
            if("0"==rsid){
                accounting.doShowCustom(record,gz.label.userDefinedTable,record.text);//'用户自定义表'
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
                } else if ("4" == rsid) {
                    title = gz.label.salaryReportAnalysis;//"人员结构分析表"
                }
                accounting.openSalaryReport(rsid,tabid,title,record.text);
            }
            return;
        }


        var window = Ext.getCmp("commonReportWin");
        if (window == undefined) {
            var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                '<div  style="white-space:nowrap;  height: auto;width:auto;cursor:pointer;margin-bottom: 3px;margin-left: 3px;margin-right: 3px;margin-bottom: 5px" >' +
                ' {text} </div>',
                '</tpl>'
            );

            //方案数据store
            var schemeStore = Ext.create('Ext.data.Store', {
                storeId: 'commonReportListStore',
                fields: ['text', 'id'],
                data: accounting.commonreportlist
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
                            accounting.doShowCustom(data,gz.label.userDefinedTable,data.text);//'用户自定义表'
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
                    		accounting.openSalaryReport(rsid,tabid,title,data.text);
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
                store.load(accounting.commonreportlist);
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
            obj.salaryid = salaryid;
            obj.gz_module = imodule;
            obj.model = '0';
            obj.bosdate = appdate;
            obj.count = count;
            obj.title = parenttext + "-->" + text;

        Ext.require('SalaryReport.OpenSalaryReport', function () {
                Ext.create("SalaryReport.OpenSalaryReport", obj);
            });
        //}
    },
    doShowCustom:function(data,parentNode,value){
        if(data.reporttype=='10'){
            accounting.showCustom(data.id,parentNode,value);
        }else if(data.reporttype=='0'){
            //特殊报表
            accounting.showSpecialreport(data.tabid);
        }else if(data.reporttype=='3'){
            //花名册
            accounting.showOpenMuster(data.tabid,parentNode,value,data.nmodule);
        }else if(data.reporttype=='4'){
            //简单名册
            accounting.showSimpleMuster(data.url);
        }
    },
    //打开自定义报表
    showCustom: function (uid, parenttext, text) {
        var strurl = "/gz/gz_accounting/report/open_gzbanner.do?b_report=link&" +
            "checksalary=salary&opt=int" +
            "&salaryid=" + accounting.tablesubModuleId .split("_")[1] +
            "&tabid=" + uid +
            "&a_code=" +
            "&subModuleId=" + accounting.tablesubModuleId +
            "&gz_module=" + imodule + "&reset=1&" +
            "model=0&boscount=" + count + "&bosdate=" + appdate + "&pageRows=init";

        Ext.require('SalaryReport.CreateWindow', function () {
            Ext.create("SalaryReport.CreateWindow", {title: parenttext + "-->" + text, url: strurl});
        });
    },
    //打开花名册
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
});