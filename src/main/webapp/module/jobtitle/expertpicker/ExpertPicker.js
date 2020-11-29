/**
 * 资格评审_专家选择控件
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ExpertPicker.ExpertPicker',{
	width:'',
	height:420,
	callback:Ext.emptyFn,
	sql:'',
	orderBy:'',
	searchItems:'',
	searchText:'',
    supportPersonPicker:false,//是否支持手工引入
    supportImportExpertsFilter:false,//是否支持条件引人
	title:'请选择',
	extpert_picker_tableObj:{},
	constructor:function(config) {
		var me = this;
		me.width = config.width;
		me.height = config.height;
		me.sql = config.sql;
		me.orderBy = config.orderBy;
		me.searchItems = config.searchItems;
		me.searchText = config.searchText;
		me.title = config.title;
		me.callback = config.callback;
		me.supportPersonPicker=config.supportPersonPicker;
		me.supportImportExpertsFilter=config.supportImportExpertsFilter;
		me.init();
	},
	// 初始化函数
	init:function(url) {
		var me = this;
		//加载自定义类
		Ext.Loader.loadScript({url:'/components/tableFactory/tableFactory.js'});
        Ext.Loader.loadScript({url:rootPath+'/components/personPicker/PersonPicker.js'});
        Ext.util.CSS.swapStyleSheet("one", rootPath+"/components/personPicker/PersonPicker.css");
		var map = new HashMap();
		map.put("sql", me.sql);
		map.put("orderBy", me.orderBy);
		map.put("searchText", me.searchText);
	    Rpc({functionId:'ZC00002206',async:false,success:me.getTableOK,scope:this},map);
	},
	// 加载表单
	getTableOK:function(form, action){
		var me = this;
		
		var result = Ext.decode(form.responseText);
		var jsonData = result.tableConfig;
		var obj = Ext.decode(jsonData);
		var tableObj = new BuildTableObj(obj);
		me.extpert_picker_tableObj = tableObj;
		var tableComp = tableObj.getMainPanel();
		
		var pageheight = window.parent.window.document.getElementById('center_iframe').offsetHeight;
		var pagewidth = window.parent.window.document.getElementById('center_iframe').offsetWidth;
		Ext.create('Ext.window.Window', {
			id : 'picker_expert_window',
			modal:true,
		    title: me.title,
			modal: true,
			layout:'fit',
			width:pagewidth*0.9,
			height:pageheight*0.9,//窗口的高(不含菜单)，150：菜单高度,
			border:false,
			items:[tableComp],
            closeAction: 'destroy',
			buttonAlign:'center',
			//alwaysOnTop:true,
			buttons : [{
				xtype : 'button',
				text : '手工引入',
				hidden:!me.supportPersonPicker,
				margin:'20 10 0 0',
				handler : function() {me.importExperts(result.orgid);}
			}, {
				xtype : 'button',
				text : '条件引入',
                hidden:!me.supportImportExpertsFilter,
				margin:'20 10 0 0',
				handler : function() {me.importExpertsFilter();}
			},{
				xtype : 'button',
				text : common.button.ok,
				margin:'20 10 0 0',
				handler : function() {me.enter();}
			},{
				xtype : 'button',
				text : "取消",
				margin:'20 0 0 0',
				handler : function() {me.closePicker();}
			}]
		}).show();
	},
	//确定
	enter:function(){
		var selectedList = new Array();//人员信息集

		var selectData = this.extpert_picker_tableObj.tablePanel.getSelectionModel().getSelection();//获取数据
		for(var p in selectData){
			if(selectData.hasOwnProperty(p)){
				var w0101 = selectData[p].data.w0101_e;
				selectedList.push(w0101);
			}
		}
		/** 获取的是选择的数据 */
		if(selectedList.length == 0){//如果没选，不允许【确定】
			return ;
		}
		this.callback(selectedList);
		this.closePicker();
	},
	// 关闭
	closePicker:function(){
		Ext.getCmp('picker_expert_window').close();
	},
    // 条件引入
    importExpertsFilter:function(){
		var me =this;
        var map2 = new HashMap();
        map2.put(1, "a,b,k");//要显示的子集
        var map = new HashMap();
        map.put('salaryid', '');
        map.put('condStr', '');//复杂条件，简单条件表达式
        map.put('cexpr', '');//简单公式时：1*2
        map.put('path', "2306514");
        map.put('priv', "0");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
        map.put('info_type', "1");//=1人员,=2单位,=3职位
        map.put("isFilterSelectedExpert","0");
        Ext.require('EHR.selectfield.SelectField',function(){
            Ext.create("EHR.selectfield.SelectField", {
                imodule : "9",
                type : "1",
                queryType : "1",
                dataMap : map,
                comBoxDataInfoMap : map2,
                rightDataList : '',
                title : "选择指标",
                flag:'1',//允许选择相同的字段  haosl 2017-07-19
                isShowResult : true,
                queryCallbackfunc :function(c){
                    var staffids = "";
                    for (var i = 0; i < c.length; i++) {
                        staffids += c[i] + "'";
                    }
                    var hashvo = new HashMap();
                    hashvo.put("ids",staffids);
                    Rpc({
                        functionId : 'ZC00002002', success : function(form, action) {
                            var result = Ext.decode(form.responseText);
                            var flag = result.succeed;

                            if(flag) {
                                var selectedList = me.getSelectedList(result.selectedIdList);
                                if (selectedList.length == 0) {//如果没选，不允许【确定】
                                    return;
                                }
                                if(result.msg!=undefined&&result.msg!='')
                                    Ext.showAlert(result.msg);
                                me.callback(selectedList);
                                Ext.getCmp('picker_expert_window').close();
                            }else{
                                Ext.showAlert(result.message);
                            }
                            // var storeid="experts_dataStore";
                            // var store = Ext.data.StoreManager.lookup(storeid);
                            // store.load();
                        }
                    }, hashvo);
                }
            });
        });
    },
	/**
     * 引入专家 手工选人
     */
    importExperts:function(orgid){
    	var me=this;
        if(orgid=='UN`'){
            orgid = '';
        }
		//此处要展示全部人员，不排除已选

        //获取需要排除的人员
        // var map = new HashMap();
        // map.put("subModuleId", 'zc_reviewmeeting_experts_00001');
        // map.put("type", '3');
        // Rpc({functionId:'ZC00002008', async:false, success:function(form){
        //     var result = Ext.decode(form.responseText);
        //     var experts = result.experts;

            var picker = new PersonPicker({
                multiple: true,
                orgid: orgid,
               // deprecate : experts,
                isPrivExpression:false,
                text: "选择人员",
                titleText:"请选择",
                callback: function (c) {
                    var staffids = "";
                    for (var i = 0; i < c.length; i++) {
                        staffids += c[i].id + "'";
                    }
                    var hashvo = new HashMap();
                    hashvo.put("ids",staffids);
                    Rpc({
                        functionId : 'ZC00002002', success : function(form, action) {
                            var result = Ext.decode(form.responseText);
                            var flag = result.succeed;
                            if(flag){

                            	var selectedList=me.getSelectedList(result.selectedIdList);
                                if(selectedList.length == 0){//如果没选，不允许【确定】
                                    return ;
                                }
                                if(result.msg!=undefined&&result.msg!='')
                                    Ext.showAlert(result.msg);
                                me.callback(selectedList);
                                Ext.getCmp('picker_expert_window').close();
							}else{
                                Ext.showAlert(result.message);
                            }
                            // var store = Ext.data.StoreManager.lookup('experts_dataStore');
                            // store.load();
                        }
                    }, hashvo);
                }});
            picker.open();

        // }, scope:this},map);

    },
	getSelectedList:function (prolist) {
    	var me=this;
        var selectedList = new Array();//人员信息集

        var selectData = this.extpert_picker_tableObj.tablePanel.getSelectionModel().getSelection();//获取数据
        for(var p in selectData){
            if(selectData.hasOwnProperty(p)){
                var w0101 = selectData[p].data.w0101_e;
                selectedList.push(w0101);
            }
        }
        for(var str in prolist){
            selectedList.push(prolist[str]);
		}
		return selectedList;

    }
});