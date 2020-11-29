Ext.define('mysalary.MySalarySheet', {
    extend: 'Ext.window.Window',
    height:this.height,
    width:400,
    x:1400,
    resizable:false,
    titleAlign:'center',
    layout: 'vbox',
    initComponent: function () {
        mySalarySheet = this;
        this.callParent();
        this.initData();
    },
    initData: function () {
        var year = mySalarySheet.year;
        var month = mySalarySheet.month;
        if (month.length>2){
            month = month.substring(0, 2);
        } else {
            month = month.substring(0, 1);
            month = "0" + month;
        }
        var schemeName = mySalarySheet.schemeName;
        var schemeId = mySalarySheet.schemeId;
        var map = new HashMap();
        map.put("type", "month");
        map.put("id", schemeId);
        map.put("startDate", year + '-' + month);
        map.put("endDate", year + '-' + month);
        Rpc({
            functionId: 'GZ00000808',
            async: false,
            success: function (response) {
                var response = Ext.decode(response.responseText);
                if (response.returnStr.return_code === 'success') {
                    var colors = ['#338DC9', '#EE7541', '#2BD62B', '#DBDC26', '#8FbC8B', '#D2B48C', '#DC648A', '#21B2AA', '#B0C4DE', '#DDA0DD', '#9C9AFF', '#9C3164', '#FFB248', '#1fcf03', '#005eaa', '#339ca8', '#d9b014', '#32a487', '#333333', '#FFB6C1', '#FF69B4', '#D8BFD8', '#DDA0DD', '#FF00FF'];
                    var salaryData = response.returnStr.return_data;
                    var salaryMainData = [];
                    var showMapFlag = false;
                    // PC端可勾选显示图例与否，取色需进行算法处理
                    var tempCount = 0;
                    for (var index = 0; index < salaryData.items.length; index++) {
                        var oneData = salaryData.items[index];
                        var itemInfo = {};
                        itemInfo.name = oneData.name;
                        itemInfo.value = oneData.value;
                        // 通过total控制合计值显示不显示
                        if (oneData.total == '0'){
                            itemInfo.value = '';
                        }
                        itemInfo.total = oneData.total;
                        itemInfo.zeroFlag = true;
                        if (oneData.chart === '0') {
                            tempCount++;
                            itemInfo.color = '';
                        } else {
                            var colorIndex = (index - tempCount) % colors.length;
                            itemInfo.color = colors[colorIndex];
                            showMapFlag = true;
                        }
                        itemInfo.fieldList = oneData.fieldList;
                        salaryMainData.push(itemInfo);
                    }
                    if(salaryData.payable||salaryData.payable==0){
                        salaryMainData.push({ name: gz.label.payable, value: Number(salaryData.payable).toFixed(2) ,color:'',zeroFlag:false});
                    }
                    if(salaryData.taxable||salaryData.taxable==0){
                        salaryMainData.push({ name: gz.label.taxableAmount, value: Number(salaryData.taxable).toFixed(2),color:'',zeroFlag:false });
                    }
                    if(salaryData.incometax||salaryData.incometax==0){
                        salaryMainData.push({ name: gz.label.personalIncomeTax, value: Number(salaryData.incometax).toFixed(2),color:'',zeroFlag:false});
                    }
                    if(salaryData.realpay||salaryData.realpay==0){
                        salaryMainData.push({ name: gz.label.realWage, value: Number(salaryData.realpay).toFixed(2),color:'',zeroFlag:false });
                    }
                    if (salaryData&&showMapFlag) {
                        // 将数字保留俩位小数
                        for (var i = 0;i<salaryData.items.length;i++) {
                            var item = salaryData.items[i];
                            if(item.total != '0'){
                                item.value = Number(item.value).toFixed(2);
                            }
                            for (var j = 0; j < item.fieldList.length; j++) {
                                var field = item.fieldList[j];
                                // 判断指标的类型
                                if (field.itemtype == 'N'){
                                    salaryData.items[i].fieldList[j].value = Number(field.value).toFixed(2);
                                }
                                salaryData.items[i].fieldList[j].value = field.value;
                            }
                        }
                        mySalarySheet.showMap(salaryData.items);
                    }
                    if (salaryMainData) {
                        // 将数字保留俩位小数
                        for (var i = 0;i<salaryMainData.length;i++) {
                            var item = salaryMainData[i];
                            if(item.total != '0'){
                                item.value = Number(item.value).toFixed(2);
                            }
                            if(item.fieldList) {
                                for (var j = 0; j < item.fieldList.length; j++) {
                                    var field = item.fieldList[j];
                                    // 判断指标的类型
                                    if (field.itemtype == 'N'){
                                        salaryMainData[i].fieldList[j].value = Number(field.value).toFixed(2);
                                    }
                                    salaryMainData[i].fieldList[j].value = field.value;
                                }
                            }
                        };
                        mySalarySheet.createSalaryPanel(salaryMainData);
                    }
                }
            }
        }, map);
    },
    showMap:function(mapData) {
        var me = this;
        var seriesData = [];
        for (var i = 0; i < mapData.length; i++) {
            if (!(mapData[i].value == 0 && mySalarySheet.zeroItemCtrl == 1)){
                var itemName = mapData[i].name;
                var itemValue = mapData[i].value;
                var showChart = mapData[i].chart;
                if (showChart === '0') {
                    continue;
                }
                var itemInfo = {};
                itemInfo.name = itemName;
                itemInfo.value = itemValue;
                seriesData.push(itemInfo);
            }
        }
        // 指定图表的配置项和数据
        var option = {
            tooltip: {
                trigger: 'item',
                confine: true, // 是否将 tooltip 框限制在图表的区域内
                formatter: '{b}: {c} ({d}%)'
            },
            color: ['#338DC9', '#EE7541', '#2BD62B', '#DBDC26', '#8FbC8B', '#D2B48C', '#DC648A', '#21B2AA', '#B0C4DE', '#DDA0DD', '#9C9AFF', '#9C3164', '#FFB248', '#1fcf03', '#005eaa', '#339ca8', '#d9b014', '#32a487', '#333333', '#FFB6C1', '#FF69B4', '#D8BFD8', '#DDA0DD', '#FF00FF'],
            series: [
                {
                    type: 'pie',
                    radius: ['50%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        normal:{
                            show: false,
                            position: 'center',
                            //formatter: mySalarySheet.formatterLableLine
                        }
                    },
                    emphasis: {
                        label: {
                            show: false,
                            fontSize: '20',
                            fontWeight: 'bold'
                        }
                    },
                    labelLine: {
                        normal:{
                            show: false,
                            length:4
                        }
                    },
                    data: seriesData
                }
            ]
        };
        var showMapContainer =  Ext.create('Ext.container.Container',{
            height: 250,
            width:'100%',
            items: [{
                border:false,
                html:'<div id="chartPanel" style="width:400px;height:250px;border: 0;"></div>'
            }],
            listeners : {
                "afterrender" :function ( t, eOpts ) {
                    var showMapEcharts = echarts.init(Ext.getDom("chartPanel"));
                    // 使用刚指定的配置项和数据显示图表。
                    showMapEcharts.setOption(option);
                }
            }
        });
        mySalarySheet.add(showMapContainer);
    },
    /*formatterLableLine:function(e){
        var newStr=" ";
        var start,end;
        var name_len=e.name.length;    　　　　　　　　　　　　   //每个内容名称的长度
        var max_name=7;    　　　　　　　　　　　　　　　　　　//每行最多显示的字数
        var new_row = Math.ceil(name_len / max_name); 　　　　// 最多能显示几行，向上取整比如2.1就是3行
        if(name_len>max_name){ 　　　　　　　　　　　　　　  //如果长度大于每行最多显示的字数
            for(var i=0;i<new_row;i++){ 　　　　　　　　　　　   //循环次数就是行数
                var old='';    　　　　　　　　　　　　　　　　    //每次截取的字符
                start=i*max_name;    　　　　　　　　　　     //截取的起点
                end=start+max_name;    　　　　　　　　　  //截取的终点
                if(i==new_row-1){    　　　　　　　　　　　　   //最后一行就不换行了
                    old=e.name.substring(start);
                }else{
                    old=e.name.substring(start,end)+"\n";
                }
                newStr+=old; //拼接字符串
            }
        }else{                                          //如果小于每行最多显示的字数就返回原来的字符串
            newStr=e.name;
        }
        return newStr;
    },*/
    createSalaryPanel:function(salaryMainData) {
        var height = mySalarySheet.height-50;
        if (mySalarySheet.items.length>0){
            height = height-250;
        }
        var salaryMainPanel = Ext.create('Ext.panel.Panel', {
            width: '100%',
            maxHeight: height,
            border: false,
            overflow: 'auto',
            layout: 'vbox',
            autoScroll: true,
            bodyStyle:'padding:0;',
        });
        for (var i = 0; i < salaryMainData.length; i++) {
            if (!(salaryMainData[i].value == 0 && mySalarySheet.zeroItemCtrl == 1&&salaryMainData[i].zeroFlag)){
                var salaryPanel = "salaryPanel" + i;
                var background = salaryMainData[i].color?'background:'+salaryMainData[i].color:'';
                salaryPanel = Ext.create('Ext.panel.Panel', {
                    width: '100%',
                    border: salaryMainData[i].name==gz.label.payable?true:false,
                    bodyStyle:'border-width: 1px 0 0 0;border-style: solid;padding-top:5px;',
                    margin: '5,0,0,0',
                    id: salaryPanel,
                    items: [{
                        xtype: 'panel',
                        layout: 'hbox',
                        border: false,
                        items: [{
                            xtype: 'component',
                            height:'12px',
                            width:'20px',
                            margin:'2px 10px 0 0',
                            style: background,
                        }, {
                            xtype: 'component',
                            flex: 3,
                            html:salaryMainData[i].name
                        }, {
                            xtype: 'component',
                            flex: 1,
                            style:'text-align:right;padding-right:15px',
                            html: salaryMainData[i].fieldList&&salaryMainData[i].fieldList.length>1 ?salaryMainData[i].value:salaryMainData[i].fieldList[0].value,
                        }, {
                            xtype: 'image',
                            src: salaryMainData[i].fieldList&&salaryMainData[i].fieldList.length>1 ? '/workplan/image/jiantou.png' : '',
                            id: "salaryImg"+i,
                            margin:'5px 0 0 0',
                            width: 7,
                            height: 6,
                            listeners: {
                                element: 'el',
                                click: function (t,td) {
                                    var index = td.id.substring(9);
                                    mySalarySheet.createSalaryFired(index,salaryMainData);
                                }
                            }
                        }],
                    }],
                    listeners: {
                        element: 'el',
                        click: function (t,td) {
                            var salaryName = td.innerText;
                            for (var j = 0; j < salaryMainData.length; j++){
                                if (salaryMainData[j].name==salaryName&&salaryMainData[j].fieldList.length>1){
                                    mySalarySheet.createSalaryFired(j,salaryMainData);
                                    return
                                }
                            }
                        }
                    }
                });
                salaryMainPanel.add(salaryPanel);
            }
        }
        mySalarySheet.add(salaryMainPanel);
    },
    createSalaryFired:function(index,salaryMainData){
        var fieldList = salaryMainData[index].fieldList;
        var salaryFiredListPanel = Ext.getCmp('salaryFiredListPanel'+index);
        if (salaryFiredListPanel){
            Ext.getCmp('salaryPanel'+index).remove(salaryFiredListPanel);
        } else {
            var firedPanel = mySalarySheet.createSalaryFiredPanel(fieldList,index);
            Ext.getCmp('salaryPanel'+index).add(firedPanel);
        }
    },
    createSalaryFiredPanel:function(firedList,index){
        var salaryFiredListPanel =  Ext.create('Ext.panel.Panel', {
            border: false,
            id: 'salaryFiredListPanel'+index,
            layout:'vbox',
            bodyStyle:'padding:0;',
        })
        for (var i = 0 ; i<firedList.length ; i++){
            if (!(firedList[i].value == 0 && mySalarySheet.zeroItemCtrl == 1)) {
                var firedListPanel = "firedListPanel" + i;
                firedListPanel = Ext.create('Ext.panel.Panel', {
                    width: '100%',
                    border: false,
                    margin: '5,0,0,0',
                    bodyStyle:'padding-top:10px;',
                    height: '80px',
                    layout: 'hbox',
                    items: [{
                        xtype: 'component',
                        flex: 0.7,
                    },{
                        xtype: 'component',
                        flex: 4,
                        html: firedList[i].name,
                    }, {
                        xtype: 'component',
                        flex: 1.2,
                        html: firedList[i].value,
                        style:'text-align:right;padding-right:17px',
                    }]
                });
                salaryFiredListPanel.add(firedListPanel);
            }
        }
        return salaryFiredListPanel;
    }
});