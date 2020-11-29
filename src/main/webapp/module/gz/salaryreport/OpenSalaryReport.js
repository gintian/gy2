//薪资报表组件  zhaoxg 2017-1-4
Ext.define('SalaryReport.OpenSalaryReport',{
    //构造
    constructor:function(config){
        openSalaryReportScope = this;
        openSalaryReportScope.gz_module = config.gz_module;////薪资和保险区分标识  1：保险  否则是薪资
        openSalaryReportScope.model = config.model;//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
        openSalaryReportScope.rsid = config.rsid;//表类号
        openSalaryReportScope.rsdtlid = config.rsdtlid;//具体表号
        openSalaryReportScope.salaryid = config.salaryid;//薪资类别
        openSalaryReportScope.bosdate = config.bosdate;//业务如期
        openSalaryReportScope.count = config.count;//发放次数
        openSalaryReportScope.title = config.title;//表名（弹窗标题）
        openSalaryReportScope.groupvalues = getEncodeStr(config.groupvalues);//分组值
        openSalaryReportScope.originalGroupValues=config.groupvalues;
        openSalaryReportScope.groupField="";//分组项

        Ext.MessageBox.wait("正在打开，请稍候...", "等待");

        var map = new HashMap();
        map.put("salaryid",openSalaryReportScope.salaryid);
        map.put("bosdate",openSalaryReportScope.bosdate);
        map.put("count",openSalaryReportScope.count);
        map.put("gz_module",openSalaryReportScope.gz_module);
        map.put("rsid",openSalaryReportScope.rsid);
        map.put("rsdtlid",openSalaryReportScope.rsdtlid);
        map.put("model",openSalaryReportScope.model);
        map.put("groupvalues",openSalaryReportScope.groupvalues);
        Rpc({functionId:'GZ00000507',success: function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    if(openSalaryReportScope.rsid=="4"){
                        openSalaryReportScope.initRsid4(result);
                    }else{
                        openSalaryReportScope.groupField=result.groupField;

                        openSalaryReportScope.init(result);

                    }
                }else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                	Ext.MessageBox.close();//关闭遮罩
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    init:function(result){
        openSalaryReportScope.datalist=result.datalist;
        var conditions=result.tableConfig;
        var obj = Ext.decode(conditions);
        var vs = Ext.getBody().getViewSize();
        var win = Ext.widget("window",{
            title:openSalaryReportScope.title,
            height:vs.height,
            width:vs.width,
            tools:[{id:'salaryreport_schemeSetting',xtype:'toolbar',border:false}],
            layout:'fit',
            modal:true,
            id:'wd',
            closeAction:'destroy',
            resizable:false
        });
        win.show();
        var tableObj = new BuildTableObj(obj);
        tableObj.setSchemeViewConfig({//配置栏目设置参数
                            publicPlan:true,
                            sum:false,
                            lock:true,
                            merge:false,
                            pageSize:'20'
                        });
        openSalaryReportScope.mainTableObj=tableObj;
        if(!(openSalaryReportScope.rsid=="3"||openSalaryReportScope.rsid=="13")&&!(openSalaryReportScope.rsid=="2"||openSalaryReportScope.rsid=="12")){
            var params = new Object();
            params.salaryid=openSalaryReportScope.salaryid;
            params.bosdate=openSalaryReportScope.bosdate;
            params.count=openSalaryReportScope.count;
            params.gz_module=openSalaryReportScope.gz_module;
            params.rsid=openSalaryReportScope.rsid;
            params.rsdtlid=openSalaryReportScope.rsdtlid;
            params.model=openSalaryReportScope.model;
            params.groupvalues=openSalaryReportScope.groupvalues;
            Ext.getCmp("salaryreport_querybox").setCustomParams(params);//初始化查询组件参数
        }
        openSalaryReportScope.subModuleId=result.subModuleId;
        Ext.MessageBox.close();
        win.add(tableObj.getMainPanel());
    },

    //栏目设置按钮事件
    // schemeSetting:function(){
    //     Ext.require("EHR.tableFactory.plugins.SchemeSetting", function() {
    //         var window = new EHR.tableFactory.plugins.SchemeSetting({
    //             subModuleId : openSalaryReportScope.subModuleId,
    //             schemeItemKey:'',
    //             itemKeyFunctionId:'',
    //             viewConfig:{
    //                 publicPlan:true,
    //                 sum:false,
    //                 lock:true,
    //                 merge:false,
    //
    //                 pageSize:'20'
    //             },
    //             closeAction:openSalaryReportScope.closeSettingWindow
    //         });
    //     });
    // },

    closeSettingWindow:function(){
        Ext.getCmp('wd').destroy();


        var obj = new Object();
        obj.rsid = openSalaryReportScope.rsid;
        obj.rsdtlid = openSalaryReportScope.rsdtlid;
        obj.salaryid = openSalaryReportScope.salaryid;
        obj.gz_module = openSalaryReportScope.gz_module;
        obj.model = openSalaryReportScope.model;
        obj.bosdate = openSalaryReportScope.bosdate;
        obj.count = openSalaryReportScope.count;
        obj.title =openSalaryReportScope.title;
        obj.groupvalues=openSalaryReportScope.originalGroupValues;
        openSalaryReportScope = null;
        Ext.require('SalaryReport.OpenSalaryReport', function(){
            Ext.create("SalaryReport.OpenSalaryReport",obj);
        });

    },







    //人员工资结构分析表 页面展现
    initRsid4:function(result){
        openSalaryReportScope.datalist=result.datalist;
        var conditions=result.tableConfig;
        var obj = Ext.decode(conditions);
        openSalaryReportScope.tableObj = new BuildTableObj(obj);
        openSalaryReportScope.addLisentoStore(openSalaryReportScope.tableObj.tablePanel.getStore());
        openSalaryReportScope.graphDataList=result.graphDataList;
        var baseStore = Ext.create('Ext.data.Store', {
            fields: ['value', 'name'],
            data : result.basedata
        });
        var base = Ext.create('Ext.form.ComboBox', {//统计项目
            store: baseStore,
            fieldLabel:'按',
            labelWidth:27,
            margin :'0 0 0 40',
            labelAlign:'left',
            editable:false,
            listConfig : {
                maxHeight : 300,
            },
            queryMode: 'local',
            repeatTriggerClick : true,
            displayField: 'name',
            valueField: 'value',
            listeners:{
                afterrender:function(combo){
                    if(itemStore.getAt(0)){
                        combo.setValue(baseStore.getAt(0).get('name'));
                        openSalaryReportScope.baseid=baseStore.getAt(0).get('value');
                    }
                },
                select:function(combo,ecords){
                    openSalaryReportScope.baseid = combo.getValue();
                    openSalaryReportScope.reloadPanel();
                    openSalaryReportScope.bar.axes[1]._title.setText(combo.getDisplayValue());
                }
            }
        });
        var itemStore = Ext.create('Ext.data.Store', {
            fields: ['value', 'name'],
            data : result.itemdata
        });
        var item = Ext.create('Ext.form.ComboBox', {//统计指标
            store: itemStore,
            fieldLabel:'对',
            id:'tjComboBox',
            labelAlign:'left',
            labelWidth:27,
            listConfig : {
                maxHeight : 300,
            },
            margin :'0 0 0 20',
            editable:false,
            queryMode: 'local',
            repeatTriggerClick : true,
            displayField: 'name',
            valueField: 'value',
            listeners:{
                afterrender:function(combo){
                    if(itemStore.getAt(0)){
                        combo.setValue(itemStore.getAt(0).get('name'));
                        openSalaryReportScope.itemid=itemStore.getAt(0).get('value');
                    }
                },
                select:function(combo,ecords){

                    openSalaryReportScope.itemid = combo.getValue();
                    openSalaryReportScope.reloadPanel();
                }
            }
        });
        var userAgent=navigator.userAgent;
        var vs = Ext.getBody().getViewSize();
        // 用ehchars实现
        var jsonArr = new Array();
		var bodyWidth = Ext.getBody().getWidth()*0.5;
		var pieWidth=bodyWidth>300?bodyWidth:300;
		var pieHeight=(vs.height-70)/2;
		var panel = Ext.create("Ext.container.Container",{
				id:'pie_panel',
				width:pieWidth,
				height:'100%',
				border: false,
			 	html:'<div id="echartsPie" style="width:'+pieWidth+'px;height:'+pieHeight+'px"></div>',
			 	listeners:{
			 		render:function(){
			 			var data = [];
						openSalaryReportScope.echartsPie = echarts.init(document.getElementById('echartsPie'));
						var option = {
							// 饼图提示
						    tooltip : {
						        trigger: 'item',
						        formatter: function(params){
						        	var str = params.name;
						        	return str;
						        }
						    },
						    // 右侧栏目
						    legend: {
						        orient: 'vertical',
						        right: 10,
						        type: 'scroll',
						        data:data
						    },
						    calculable : true,
						    series : [{
						    	//饼图
					            type:'pie',
					            radius : '80%',//饼图的半径大小
					            hoverOffset:8,
					            center: ['35%', '50%'],//饼图的位置
					            data:jsonArr,
					            label:{
					            	 normal: {
					                    show: false
					                }
					            },
					            labelLine: {
					                normal: {
					                    show: false
					                }
					            }
					        }]
						}; 
						openSalaryReportScope.echartsPie.setOption(option);
			 		}
			 	}
			});	


        var colors = ['#FBBC29','#CE2E4E','#7E0062','#158B90','#57880E'];
        openSalaryReportScope.bar = Ext.create({
            xtype: 'cartesian',
            width:'100%',
            height:(vs.height-60)/2,
            // border:false,
            bodyStyle:'border-top-width: 0px;border-left-width: 0px;',
            store: {
                fields: ['name', 'value'],
                data: []
            },
            axes: [{
                type: 'numeric',
                position: 'left',//左侧纵坐标
                title: {
                    text: '平均值',
                    fontSize: 15
                },
                fields: 'value'
            }, {
                type: 'category',
                position: 'bottom',//底部横坐标
                label: { rotate: { degrees: 60} },
                title: {

                    text: '统计项目',
                    fontSize: 15
                },
                fields: 'name'
            }],
            series: {
                type: 'bar',//柱状图
                xField: 'name',//x轴对应内容
                yField: 'value',//y轴对应内容

                renderer:function(sprite, storeItem, barAttr, i, store){
                    barAttr.fill = colors[i%5];
                    return barAttr;
                },
                tooltip: {
                    trackMouse: true,
                    height: 40,
                    renderer: function (storeItem, item) {//渲染鼠标悬停框

                        var text=item.get('name').replace('\n','') + ': ' + item.get('value') ;
                        var length=openSalaryReportScope.getStrLength(text);
                        this._tooltip.setWidth(length*6.5);
                        this._tooltip.setHtml(text);
                    }
                }
            }
        });



        var bodyPanel = Ext.create('Ext.panel.Panel', {
            width:'100%',
            height:'100%',
            layout: 'border',
            border:false,
            bodyBorder:false,
            bodyStyle: 'background:#ffffff;',
            tbar:[
				{
                    xtype:'button',
                    icon:"/images/img_o.gif",text:"页面设置",
                    width:80,
                    handler:function(){
                        openSalaryReportScope.pageSet();
                    }
                }
                ,{
                    xtype:'button',
                    icon:"/images/portExcel.png",text:"输出",
                    width:60,
                    handler:function(){
                        openSalaryReportScope.ExportExcel();
                    }
                }
                ,base,item,{xtype: 'tbtext',margin :'0 0 0 20',text:'进行分析'},'->',
                { xtype:'numberfield',
                    fieldLabel: '显示条数',
                    labelAlign:'right',
                    width:140,
                    minValue:1,
                    maxValue:100,
                    id:'showNum',
                    labelWidth:50,
                    value:10,
                    allowBlank: false,
                    listeners:{
                        change:function(){
                            openSalaryReportScope.initPolarStore();
                        }}
                }],
            items:[
                { region: "west",xtype:'panel',id:'westPanel',border:false,layout:'fit', width:'50%',items:openSalaryReportScope.tableObj.getMainPanel()},
                { region: "center",xtype:'panel',id:'centerPanel', width:'50%',border:false,
                    items:[
                        { region: "north",xtype:'panel',border:true,height:'50%',items:panel},
                        { region: "center",xtype:'panel',border:false,height:'50%',items:openSalaryReportScope.bar}
                    ]
                }
            ]
        });
        var win = Ext.widget("window",{
            title:openSalaryReportScope.title,
            height:vs.height,
            width:vs.width,
            layout:'fit',
            modal:true,
            closeAction:'destroy',
            resizable:false,
            items: [bodyPanel]
        });
        Ext.MessageBox.close();//关闭遮罩
        win.show();
        openSalaryReportScope.bar.axes[1]._title.setText(base.getDisplayValue());

    },
    //人员工资结构分析表 重新加载表格部分
    reloadPanel:function(baseid,itemid){
        var map = new HashMap();
        map.put("salaryid",openSalaryReportScope.salaryid);
        map.put("bosdate",openSalaryReportScope.bosdate);
        map.put("count",openSalaryReportScope.count);
        map.put("gz_module",openSalaryReportScope.gz_module);
        map.put("rsid",openSalaryReportScope.rsid);
        map.put("rsdtlid",openSalaryReportScope.rsdtlid);
        map.put("model",openSalaryReportScope.model);
        map.put("baseid",openSalaryReportScope.baseid);
        map.put("itemid",openSalaryReportScope.itemid);
        Rpc({functionId:'GZ00000507',success: function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    var conditions=result.tableConfig;
                    openSalaryReportScope.datalist=result.datalist;
                    var obj = Ext.decode(conditions);
                    var centerPanel = Ext.getCmp('westPanel');
                    centerPanel.removeAll(false);
                    if(openSalaryReportScope.tableObj){
                        openSalaryReportScope.tableObj.getMainPanel().destroy();
                    }
                    openSalaryReportScope.tableObj = new BuildTableObj(obj);
                    openSalaryReportScope.graphDataList=result.graphDataList;
                    openSalaryReportScope.addLisentoStore(openSalaryReportScope.tableObj.tablePanel.getStore());


                    centerPanel.add(openSalaryReportScope.tableObj.getMainPanel());
                }else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                    Ext.showAlert(result.message);
                }
            }},map);
    },

    //刷新图标数据
    initPolarStore:function(){
        var pieStore=new Array();
        var barStore=new Array();
        var shownum=Ext.getCmp('showNum').value;
        var sum=0;
        var i=0;
        var j=0;
        var showRowNum=new Array();

        //构建饼图数据源
        Ext.Array.each(openSalaryReportScope.graphDataList,function(item, index, self){
            if(index<shownum){
                var pie = new Object();
                var t=item.baseid.split('=');
                if(t[0]=="")
                    t[0]="空";
                pie.name = t[0]+"("+item.percentnum+"%)";
                pie.value = item.percentnum;
                pie.baseid=t[1];
                pie.text=t[0];

                pieStore[pieStore.length]=pie;
            }else{
                sum+=parseFloat(item.percentnum);
            }
        });
        //构建线图数据源 循环总数据 取饼图所使用的行加入柱状图中  使2个图显示行一致
        Ext.Array.each(openSalaryReportScope.datalist,function(item, index, self){
            if(index!=openSalaryReportScope.datalist.length-1){
                var baseid = item.baseid;
                Ext.Array.each(pieStore,function(gitem, gindex, e){
                    if(gitem.baseid==baseid){
                        var bar = new Object();
                        // 如果name是重复的，导致echart不识别，比如部门名称相同
                        var text=(gindex+1)+"."+gitem.text;
                        bar.name = text.replace(/(.{8})/g,'$1\n');
                        bar.value = item.average;
                        barStore[gindex]=bar;
                        showRowNum[i]=[j];
                        i++;

                    }
                });
                j++;
            }
        });
        if(sum!=0){
            var p = new Object();
            p.name = "其他("+sum.toFixed(2)+")";
            p.value = sum.toFixed(2);
            p.text="其他";
            p.baseid="other";
            pieStore[pieStore.length]=p;
            openSalaryReportScope.other=sum.toFixed(2);
        }else
            openSalaryReportScope.other='0';


        openSalaryReportScope.showRowNum=showRowNum;
        openSalaryReportScope.bar.getStore().removeAll(true);
        openSalaryReportScope.bar.getStore().add(barStore);
//        openSalaryReportScope.pie.getStore().removeAll(true);
//        openSalaryReportScope.pie.getStore().add(pieStore);
        var data = [];
        var jsonArr = new Array();
        var colors = ['#FBBC29','#CE2E4E','#7E0062','#158B90','#57880E'];
		for(var i = 0; i < pieStore.length; i++) {
			var name = (i+1)+"."+pieStore[i].name
			data.push(name)
			var json = {};
			// 如果name是重复的，导致echart不识别，比如部门名称相同
			json.name = name;
			json.value =  pieStore[i].value;
			json.itemStyle = {};
			json.itemStyle.normal={};
			json.itemStyle.normal.color = colors[i % 5];
			jsonArr.push(json);
		}	
        openSalaryReportScope.echartsPie.setOption({        //加载数据图表
        	legend: {
                data: data
            },
            series: [{
                data: jsonArr
            }]
        });
    },

    //人员工资结构分析表 store监听事件
    addLisentoStore:function(store){
        store.on("load",function(s){

            openSalaryReportScope.initPolarStore();

            var listdata=s.data.items[s.data.items.length-1];


        })
    },
  //为了让汇总表汇总行变色 数字渲染方法
    numRenderTo:function(value, metaData, Record){

        var obj = Record.data;
        if(Record.data.iscollect=="1"){
            var style='style=background-color:#e4f3ff;width:';
            style+=metaData.column.width+'px';
            metaData.tdAttr = style;
        }else if(Record.data.iscollect=="2"){
            var style='style=background-color:#94b6e6;width:';
            style+=metaData.column.width+'px';
            metaData.tdAttr = style;
        }
        //格式化
        if(typeof value == "number"||value=="0"){
            value=parseFloat(value).toFixed(2);
            value=openSalaryReportScope.toThousands(value);
        }
        return value;
    },
    //为了让汇总表汇总行变色
    renderTo:function(value, metaData, Record){
        var obj = Record.data;
        if(Record.data.iscollect=="1"){
            var style='style=background-color:#e4f3ff;width:';
            style+=metaData.column.width+'px';
            metaData.tdAttr = style;
        }else if(Record.data.iscollect=="2"){
            var style='style=background-color:#94b6e6;width:';
            style+=metaData.column.width+'px';
            metaData.tdAttr = style;
        }
        return value;
    },
    //结构分析表 统计项目列渲染
    renderBaseid:function(value, metaData, Record){
        //最后一页 最后一行写为总计
        var re=Record.store.data.items[Record.store.data.length-1];
        var page=Math.ceil(Record.store.totalCount/Record.store.pageSize);
        if(page==Record.store.currentPage&&re.id==Record.data.record_internalId){
            return "总计";
        }
        else{
            if(value=="")
                return "空";
            else{
                var v=value.split('`');
                if(v.length>1)
                    return v[1];
                else
                    return value;
            }
        }

    },
    
    pageSet:function(){
    	var map = new HashMap();
		map.put("rsid", openSalaryReportScope.rsid);
		map.put("rsdtlid", openSalaryReportScope.rsdtlid);		
		map.put("opt",'3');	//opt等于"3"时为页面打开时数据的初始化
		map.put("salaryid",openSalaryReportScope.salaryid);
		if(openSalaryReportScope.rsid == '4') {
			map.put("isExcel",'0');
		}
		//页面数据填充
		Rpc({functionId : 'GZ00000704',success: function(form,action){
			var result = Ext.decode(form.responseText);
			Ext.require('EHR.exportPageSet.ExportPageSet',function(){//这块的函数向导不需要卡薪资类别，取权限内的全部子集
				Ext.create("EHR.exportPageSet.ExportPageSet",{rsid:openSalaryReportScope.rsid,rsdtlid:openSalaryReportScope.rsdtlid,result:result,callbackfn:'openSalaryReportScope.savePageSet'});
			})
		}}, map);
    },
	//保存页面设置
    savePageSet:function(pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue,type) {
    	var map = new HashMap();
    	map.put("rsid", openSalaryReportScope.rsid);
		map.put("rsdtlid", openSalaryReportScope.rsdtlid);	
		map.put("salaryid",openSalaryReportScope.salaryid);
		
		map.put("pagesetupValue", pagesetupValue);
		map.put("titleValue", titleValue);//标题
		map.put("pageheadValue", pageheadValue);//页头
		map.put("pagetailidValue", pagetailValue);//页尾
		map.put("textValueValue", textValueValue);//正文
		if(type == "0")
			map.put("opt",'2');//保存
		else
			map.put("opt",'1');//初始化
    	Rpc({functionId : 'GZ00000704',success: function(form,action){
    		var result = Ext.decode(form.responseText);
    		var xmltype = result.xmltype;
    		if(type == "0") {
	    		if(xmltype != 'ok') {
	    			Ext.showAlert("保存失败");
	    		}
    		}else {
    			if(xmltype != 'ok') {
	    			Ext.showAlert("初始化失败");
	    		}
    		}
    	}}, map);
    },
    
	//导出excel
	ExportExcel:function(){
		Ext.MessageBox.wait("正在导出，请稍候...", "等待");
		//页面数据
		var tableobj;

		if(openSalaryReportScope.rsid=='4'||openSalaryReportScope.rsid=='14'){
			tableobj=openSalaryReportScope.tableObj.tablePanel;
		}else{
			tableobj=openSalaryReportScope.mainTableObj.tablePanel;
		}
		var colList=new Array();
		var i=0;
		Ext.Array.each(tableobj.columns,function(item,index,self){//页面列顺序
			if(item.hidden!=true) {
                colList[i] = item.dataIndex;
                i++;
            }
		});
		var map = new HashMap();
		map.put("tableTitle",openSalaryReportScope.title);//文件名
		map.put("groupField",openSalaryReportScope.groupField);//分组项
		map.put("UserFlag",openSalaryReportScope.userflag);//发起人，审批界面
		map.put("salaryid",openSalaryReportScope.salaryid);
		map.put("bosdate",openSalaryReportScope.bosdate);
		map.put("count",openSalaryReportScope.count);
		map.put("gz_module",openSalaryReportScope.gz_module);
		map.put("rsid",openSalaryReportScope.rsid);
		map.put("rsdtlid",openSalaryReportScope.rsdtlid);
		map.put("model",openSalaryReportScope.model);
		map.put("colList",colList);//页面列
		if(openSalaryReportScope.ids!=null&&openSalaryReportScope.ids!="")
			map.put("groupvalues",openSalaryReportScope.ids);
		else
			map.put("groupvalues",openSalaryReportScope.groupvalues);
		
		if(openSalaryReportScope.rsid=='4'||openSalaryReportScope.rsid=='14'){
			map.put("baseid",openSalaryReportScope.baseid);
			map.put("other",openSalaryReportScope.other);//其他值
			map.put("itemid",openSalaryReportScope.itemid);
			map.put("showRowNum",openSalaryReportScope.showRowNum);//页面显示列下标
		}
	    Rpc({functionId:'GZ00000508',success:function(form,action){
	    	Ext.MessageBox.close();
				var result = Ext.decode(form.responseText);	
				if(result.succeed){
					var fileName = getDecodeStr(result.fileName);
					window.location.target="_blank";
					window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
				}else{
					Ext.showAlert(result.message);
				}
			    	
		 }},map);
		
		
	},
	
	//导出pdf
	showPdfWin:function(){
		Ext.MessageBox.wait("正在导出，请稍候...", "等待");
		//页面数据
		var tableobj=openSalaryReportScope.mainTableObj.tablePanel;
		var colList=new Array();
		var i=0;
		Ext.Array.each(tableobj.columns,function(item,index,self){//页面列顺序
			if(item.hidden!=true) {
                colList[i] = item.dataIndex;
                i++;
            }
		});
		var map = new HashMap();
		map.put("tableTitle",openSalaryReportScope.title);//文件名
		map.put("groupField",openSalaryReportScope.groupField);//分组项
		map.put("UserFlag",openSalaryReportScope.userflag);//发起人，审批界面
		map.put("salaryid",openSalaryReportScope.salaryid);
		map.put("bosdate",openSalaryReportScope.bosdate);
		map.put("count",openSalaryReportScope.count);
		map.put("gz_module",openSalaryReportScope.gz_module);
		map.put("rsid",openSalaryReportScope.rsid);
		map.put("rsdtlid",openSalaryReportScope.rsdtlid);
		map.put("model",openSalaryReportScope.model);
		map.put("colList",colList);//页面列
		if(openSalaryReportScope.ids!=null&&openSalaryReportScope.ids!="")
			map.put("groupvalues",openSalaryReportScope.ids);
		else
			map.put("groupvalues",openSalaryReportScope.groupvalues);
		
	    Rpc({functionId:'GZ00000509',success:function(form,action){
			var result = Ext.decode(form.responseText);	
			Ext.MessageBox.close();
			if(result.succeed){
				var fileName = getDecodeStr(result.fileName);
				window.location.target="_blank";
				window.location.href = "/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
			}else{
				Ext.showAlert(result.message);
			}
		    	
	 }},map);

	},

    //设置范围
    defineGroup:function(){
        openSalaryReportScope.tempIds = openSalaryReportScope.ids;
        var treeStore = Ext.create('Ext.data.TreeStore', {
        	proxy:{
        		type: 'transaction',
                functionId:'GZ00000503',
                extraParams:{
                    salaryid:openSalaryReportScope.salaryid,
                    rsdtlid:openSalaryReportScope.rsdtlid,
                    selectedIds:openSalaryReportScope.ids
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
			},
			root: {
				// 根节点的文本
				id:'root',
				text:'分组范围',
				expanded: true,
				icon:'/images/add_all.gif'
			}
        });
        var tree = Ext.create('Ext.tree.Panel', {
            // 不使用Vista风格的箭头代表节点的展开/折叠状态
            useArrows: false,
            id:'definegroup',
            height:300,
            width:430,
            store: treeStore, // 指定该树所使用的TreeStore
            rootVisible: true, // 指定根节点可见
            listeners:{
                'itemclick':function(view,record,item,index){
                    var records = view.getChecked();
                    openSalaryReportScope.tempIds = "";
                    // openSalaryReportScope.ids = "";
                    Ext.Array.each(records, function(rec){
                        openSalaryReportScope.tempIds +=rec.data.id+",";

                    });
                }
            }
        });
        var buttons = Ext.create('Ext.panel.Panel',{
            layout:'column',
            border:false,
            columnWidth:1,
            width:50,
            items:[{
                xtype:'button',
                columnWidth:1,
                text:'确定',
                style:'margin-top:20px',
                listeners:{
                    'click':function(){
                        openSalaryReportScope.ids=openSalaryReportScope.tempIds;
                        var map = new HashMap();
                        map.put("salaryid",openSalaryReportScope.salaryid);
                        map.put("bosdate",openSalaryReportScope.bosdate);
                        map.put("count",openSalaryReportScope.count);
                        map.put("gz_module",openSalaryReportScope.gz_module);
                        map.put("rsid",openSalaryReportScope.rsid);
                        map.put("rsdtlid",openSalaryReportScope.rsdtlid);
                        map.put("model",openSalaryReportScope.model);
                        map.put("groupvalues",openSalaryReportScope.ids);
                        Rpc({functionId:'GZ00000507',success: function(form,action){
                                var result = Ext.decode(form.responseText);
                                if(result.succeed){
                                    openSalaryReportScope.mainTableObj.dataStore.load();//刷新前台store
                                    win.close();
                                }else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                                    Ext.showAlert(result.message);
                                }
                            }},map);
                    }
                }
            },{
                xtype:'button',
                columnWidth:1,
                style:'margin-top:20px',
                text:'取消',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            }]
        });
        var win = Ext.widget("window",{
            title:'请选择第一分组项范围...',
            height:350,
            width:500,
            layout:'fit',
            modal:true,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                layout:'border',
                bodyStyle: 'background:#ffffff;',
                border:false,
                items:[
                    { region: "center",border:false,items:tree},
                    { region: "east",border:false,items:buttons}
                ]
            }]
        });
        win.show();
    },
    toThousands:function (num) {//千分位方法
        var num = (num || 0).toString(), result = '';
        var decimal=num.substring(num.indexOf("."),num.length);
        num=num.substring(0,num.indexOf("."));
        while (num.length > 3) {
            result = ',' + num.slice(-3) + result;
            num = num.slice(0, num.length - 3);
        }
        if (num) { result = num + result; }

        return result+decimal;
    },
    getStrLength:function(str){
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    }
})