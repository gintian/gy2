/**
 * 薪资发放--变动比对
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 *
 * */
Ext.define('SalaryUL.ComparisonWithFile',{
    change_me:'',
    salaryid:'',
    imodule:'',
    dbname:'',
    mainPanel:'',
    pathform:'',
    currentPage:'',
    tableObjArray:new Array(),
    constructor:function(config) {
        change_me = this;
        salaryid = config.salaryid;// 薪资类别编号
        imodule = config.imodule;// 薪资/保险区分 0:薪资  1:保险
        dbname = config.dbname;//dbname转化usr=>在职人员库
        change_me.createPanel(config);
        pathform=config.pathform;//标示来源页面，0来自类别列表,1来自人员列表
        currentPage=config.currentPage;
    },
    // 加载标签页
    createPanel:function(config) {
        var tablePanel = change_me.createMainPanel();// 加载主页面
        change_me.isExistAdd = config.isExistAdd;
        change_me.isExistDel = config.isExistDel;
        change_me.isExistInfo = config.isExistInfo;
        var isNeedOpen = true;//展开第一个(fix：现在只要有就全部展现 sunjian)
        if(config.isExistAdd === "1"){// 新增人员
            tablePanel.add(change_me.createAddPanel(isNeedOpen));
            tablePanel.add({hidden:true});//设置一个隐藏items,使Panel内各表单互不影响
            //isNeedOpen = false;
        }
        if(config.isExistDel === "1"){// 减少人员
            tablePanel.add(change_me.createReducePanel(isNeedOpen));
            tablePanel.add({hidden:true});
            //isNeedOpen = false;
        }
        if(config.isExistInfo === "1"){// 信息变动人员
            tablePanel.add(change_me.createInfoPanel(isNeedOpen));
            tablePanel.add({hidden:true});
            //isNeedOpen = false;
        }
        if(config.isExistStop === "1"){// 停发人员
            tablePanel.add(change_me.createStopPanel(isNeedOpen));
            tablePanel.add({hidden:true});
            //isNeedOpen = false;
        }
        change_me.createSelfCss();
//		console.log(Ext.getBody().getWidth());
//		var tittleX = Ext.fly("addPanel_header_hd").getX();//标题X
//		var tittleY = Ext.fly("addPanel_header_hd").getY();//标题Y
//		Ext.fly("addPanel_header_hd").setLocation(tittleX+25,tittleY);
//		var imgX = Ext.fly(Ext.select(".x-tool").elements[0].id).getX();//标题X
//		var imgY = Ext.fly(Ext.select(".x-tool").elements[0].id).getY();//标题Y
//		Ext.fly(Ext.select(".x-tool").elements[0].id).setLocation(tittleX,imgY);

    },
    // 获取主页面
    createMainPanel:function(){
        var tablePanel = Ext.widget('panel',{
            style:'background-color:#ffffff;',
            autoScroll:true,// 滚动条
            layout:{
                type:'accordion',// 手风琴
                collapseFirst:false,
                //hideCollapseTool:true,//隐藏收缩按钮
                titleCollapse:false,//允许通过点击标题栏的任意位置来展开/收缩
                multi:true,
                animate: true
            },
            bodyPadding: 20,
            border:false,
            items:[]
        });
        mainPanel = Ext.widget('panel',{
            title:gz.label.changecompare,
            //autoScroll:true,// 滚动条
            layout:'fit',
            items:[tablePanel],
            border:false,
            bbar : [{xtype:'tbfill'},{
                xtype : 'button',
                text : common.button.ok,
                style:'margin-right:5px',
                width:75,
                handler : function() {change_me.enter();}
            }, {
                xtype : 'button',
                text : common.button.toreturn,
                style:'margin-right:5px',
                width:75,
                handler : function() {change_me.returnBack();}
            }, {
                xtype : 'button',
                text : common.button.close,
                style:'margin-right:5px',
                width:75,
                hidden:true,
                handler : function() {change_me.enter();}
            },{xtype:'tbfill'}]

        });
        Ext.create('Ext.container.Viewport', {
            autoScroll:false,
            style:'background-color:#ffffff;',
            layout:'fit',
            items:[mainPanel]
        });

        return tablePanel;
    },
    // 获取新增人员表单
    createAddPanel:function(isNeedOpen){
        var btn = change_me.createToolBtn('print', "addExpt", "add");
        var addPanel = Ext.widget('panel', {
            title:gz.label.addperson,
            style:'background-color:#ffffff;',
            border:true,
            id:'addPanel',
            tools:[btn],
            layout:'fit',
            minHeight: Ext.getBody().getViewSize().height/1.5,
            collapsed:!isNeedOpen//折叠
        });

        change_me.createTable(addPanel);
        return addPanel;
    },
    // 获取减少新增人员表单
    createReducePanel:function(isNeedOpen){
        var btn = change_me.createToolBtn('print', "reduceExpt", "reduce");
        var reducePanel = Ext.widget('panel', {
            title:gz.label.reduceperson,
            style:'background-color:#ffffff;',
            id:'reducePanel',
            tools:[btn],
            margin:change_me.isExistAdd == 1?'30 0 0 0':'0 0 0 0',//只有减少人员的样式问题
            layout:'fit',
            minHeight: Ext.getBody().getViewSize().height/1.5,
            collapsed:!isNeedOpen
        });
        change_me.createTable(reducePanel);
        return reducePanel;
    },
    // 获取信息变动人员表单
    createInfoPanel:function(isNeedOpen){
        var btn = change_me.createToolBtn('print', "infoExpt", "info");
        var infoPanel = Ext.widget('panel', {
            title:gz.label.changeinfoperson+"<span style='color:#9299a9;font-size: 10px'>&nbsp;&nbsp;(“现”指人员信息集数据)</span>",
            style:'background-color:#ffffff;',
            id:'infoPanel',
            tools:[btn],
            margin:change_me.isExistAdd == 1 || change_me.isExistDel == 1?'30 0 0 0':'0 0 0 0',//只有变动信息的样式问题
            layout:'fit',
            minHeight: Ext.getBody().getViewSize().height/1.5,
            collapsed:!isNeedOpen
        });

        //infoPanel.addTool('<div></div>')
        change_me.createTable(infoPanel);
        return infoPanel;
    },
    // 获取停发人员表单
    createStopPanel:function(isNeedOpen){
        var btn = change_me.createToolBtn('print', "stopExpt", "stop");
        var stopPanel = Ext.widget('panel', {
            title:gz.label.stopperson,
            style:'background-color:#ffffff;',
            id:'stopPanel',
            margin:change_me.isExistAdd == 1 || change_me.isExistDel == 1 || change_me.isExistInfo == 1?'30 0 0 0':'0 0 0 0',
            tools:[btn],
            layout:'fit',
            minHeight: Ext.getBody().getViewSize().height/1.5,
            collapsed:!isNeedOpen
        });

        change_me.createTable(stopPanel);
        return stopPanel;
    },
    // 获取页签下表单
    createTable:function(p){
        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("url",url);
        map.put("flag","1");
        if(p.id === "addPanel"){// 新增
            Rpc({functionId:'GZ00000204',async:false,success:change_me.getAddTableOk,scope:this,panel:p},map);
        }
        if(p.id === "reducePanel"){// 减少
            Rpc({functionId:'GZ00000205',async:false,success:change_me.getDelTableOk,scope:this,panel:p},map);
        }
        if(p.id === "infoPanel"){// 信息变动
            Rpc({functionId:'GZ00000206',async:false,success:change_me.getChangeTableOk,scope:this,panel:p},map);
        }
        if(p.id === "stopPanel"){// 停发
            Rpc({functionId:'GZ00000207',async:false,success:change_me.getStopTableOk,scope:this,panel:p},map);
        }
    },
    // 加载新增页签下表单
    getAddTableOk:function(form, action){
        var result = Ext.decode(form.responseText);
        change_me.addStore = Ext.create('Ext.data.Store', {
            fields:Ext.decode(result.fields),
            proxy:{
                type: 'transaction',
                functionId:'GZ00000204',
                extraParams:{
                    salaryid:salaryid,
                    flag:"2"
                },
                reader: {
                    type: 'json',
                    totalProperty:'totalCount',
                    root: 'data'
                }
            },
            pageSize:10,
            remoteSort:true,
            autoLoad:true
        });
        var tableComp = Ext.create('Ext.grid.Panel', {
            store: change_me.addStore,
            width: 390,
            height: 425,
            columnLines:true,
            bufferedRenderer:false,
            rowLines:true,
//			sortableColumns:false,
            columns: Ext.decode(result.column),
            bbar:{
                id:'add',
                xtype:'pagingtoolbar',
                store:change_me.addStore,
                displayInfo:true
            }
        });
        action.panel.add(tableComp);
    },
    // 加载减少页签下表单
    getDelTableOk:function(form, action){
        var result = Ext.decode(form.responseText);
        change_me.delStore = Ext.create('Ext.data.Store', {
            fields:Ext.decode(result.fields),
            proxy:{
                type: 'transaction',
                functionId:'GZ00000205',
                extraParams:{
                    salaryid:salaryid,
                    flag:"2"
                },
                reader: {
                    type: 'json',
                    totalProperty:'totalCount',
                    root: 'data'
                }
            },
            pageSize:10,
            remoteSort:true,
            autoLoad: true
        });
        var tableComp = Ext.create('Ext.grid.Panel', {
            store: change_me.delStore,
            width: 390,
            height: 425,
            columnLines:true,
            bufferedRenderer:false,
            rowLines:true,
            columns: Ext.decode(result.column),
            bbar:{
                id:'del',
                xtype:'pagingtoolbar',
                store:change_me.delStore,
                displayInfo:true
            }
        });
        action.panel.add(tableComp);
    },
    // 加载信息变动页签下表单
    getChangeTableOk:function(form, action){
        var result = Ext.decode(form.responseText);
        change_me.tipItem = result.tipItem;
        change_me.changeStore = Ext.create('Ext.data.Store', {
            fields:Ext.decode(result.fields),
            proxy:{
                type: 'transaction',
                functionId:'GZ00000206',
                extraParams:{
                    salaryid:salaryid,
                    flag:"2"
                },
                reader: {
                    type: 'json',
                    totalProperty:'totalCount',
                    root: 'data'
                }
            },
            pageSize:10,
            remoteSort:true,
            autoLoad: true
        });
        var tableComp = Ext.create('Ext.grid.Panel', {
            store: change_me.changeStore,
            width: 390,
            height: 425,
            bufferedRenderer:false,
            columnLines:true,
            rowLines:true,
//			sortableColumns:false,
            columns: Ext.decode(result.column),
            bbar:{
                id:'change',
                xtype:'pagingtoolbar',
                store:change_me.changeStore,
                displayInfo:true
            }
        });
        action.panel.add(tableComp);
    },
    // 加载停发页签下表单
    getStopTableOk:function(form, action){
        var result = Ext.decode(form.responseText);
        change_me.stopStore = Ext.create('Ext.data.Store', {
            fields:Ext.decode(result.fields),
            proxy:{
                type: 'transaction',

                functionId:'GZ00000207',
                extraParams:{
                    salaryid:salaryid,
                    flag:"2"
                },
                reader: {
                    type: 'json',
                    totalProperty:'totalCount',
                    root: 'data'
                }
            },
            pageSize:10,
            remoteSort:true,
            autoLoad: true
        });
        var tableComp = Ext.create('Ext.grid.Panel', {
            store: change_me.stopStore,
            width: 390,
            height: 425,
            columnLines:true,
            rowLines:true,
            bufferedRenderer:false,
            columns: Ext.decode(result.column),
            bbar:{
                id:'stop',
                xtype:'pagingtoolbar',
                store:change_me.stopStore,
                displayInfo:true
            }
        });
        action.panel.add(tableComp);
    },
    // 人员库的转换usr=>在职人员库
    dbname:function(value, metaData, Record){
        var html = "";
        value = value.toLowerCase();
        for(var p in dbname){
            if(value == p.toLowerCase()){
                html = dbname[p];
            }
        }

        return html;
    },
    // 现数据渲染
    nowInfo:function(value, metaData, Record){
        var colunmIdx = metaData.column.dataIndex;
        var obj = Record.data;
        for(var p in obj){
            if(p == colunmIdx){
                var base = obj[p];
            }else if(p == (colunmIdx.substring(0,colunmIdx.length-1))){
                var now = obj[p];
            }
        }
        if(base != now){
            metaData.tdAttr='style="background-color:#FFF8D2;width:120px;"';
        }
        if(value!=null&&value.length>0){
            if(value.indexOf("`")!=-1){
                value = value.split("`")[1];
            }
        }
        return value;
    },
    // 原数据渲染
    baseInfo:function(value, metaData, Record){
        var colunmIdx = metaData.column.dataIndex;
        var obj = Record.data;
        for(var p in obj){
            if(p == colunmIdx){
                var now = obj[p];
            }else if(p == (colunmIdx+"1")){
                var base = obj[p];
            }
        }
        if(base != now){
            metaData.tdAttr='style="background-color:#FFF8D2;width:120px;"';
        }
        if(value!=null&&value.length>0){
            if(value.indexOf("`")!=-1){
                value = value.split("`")[1];
            }
        }
        return value;
    },
    updateState:function(id,value,tabletype){
        if(id=="selall"){
            if(tabletype=="0"){
                tabletype = "add";
            }else if(tabletype=="1"){
                tabletype = "reduce";
            }else if(tabletype=="2"){
                tabletype = "info";
            }else if(tabletype=="3"){
                tabletype = "stop";
            }
            var map = new HashMap();
            map.put("isUpdateAll", "1");//是否需要把全部数据更新成state
            map.put("state", value?"1":"0");
            map.put("tabletype", tabletype);
            Rpc({functionId:'GZ00000208',async:false,timeout:10000000,success:function(form,action){
                    if(tabletype=="add"){
                        change_me.addStore.load();
                    }else if(tabletype=="reduce"){
                        change_me.delStore.load();
                    }else if(tabletype=="info"){
                        change_me.changeStore.load();
                    }else if(tabletype=="stop"){
                        change_me.stopStore.load();
                    }

                }},map);
        }else{
            var map = new HashMap();
            map.put("dbname", id.substring(0,3));
            map.put("a0100", id.substring(3));
            map.put("tabletype", tabletype);
            map.put("state", value?"1":"0");
            Rpc({functionId:'GZ00000208',async:false,scope:this,success:function(form,action){
                    var result = Ext.decode(form.responseText);
                    var flag=result.succeed;
                    //xiegh 20170413 24026 options:复选框对象数组(除了全选框) selall：全选框对象
                    var options = Ext.query('input[class = options]',true,document);
                    var l= options.length;
                    var selall = document.getElementById('selall');
                    var b=0;//全选状态=0：全选；=1：取消全选
                    if(flag){
                        for(var i=l;i--;){
                            if(!options[i].checked)
                                b=1;
                        }
                        if(b==1)
                            selall.checked = false;
                        else
                            selall.checked = true;
                    }
                }},map);
        }
    },
    // 确定
    enter:function(){
        /** 确定 */
        var map = new HashMap();
        map.put("salaryid", salaryid);
        //如果信息变动有人员选中，提示 信息变动中所选人员的 '{0}' 列将被人员信息集数据覆盖，是否继续？（如不想覆盖，可取消勾选信息变动中人员）";
        var info = Ext.query('input[name=info]');
        var flag = false;
        for(var i = 0; i < info.length; i++) {
        	if(info[i].checked) {
        		flag = true;
        	}
        }
        if(Ext.getCmp("infoPanel") && flag) {
        	Ext.Msg.confirm(common.button.promptmessage,gz.label.compareTip.replace("{0}",change_me.tipItem),
        		function(id){
					if(id=='yes'){
						Rpc({functionId:'GZ00000210',async:false,success:change_me.enterOk,scope:this},map);
					}
        		}
        	);
        }else {
        	Rpc({functionId:'GZ00000210',async:false,success:change_me.enterOk,scope:this},map);
        }
        
    },
    // 确定执行成功
    enterOk:function(form,action){
        var result = Ext.decode(form.responseText);
        var flag=result.succeed;
        if(flag==true){
            Ext.Msg.alert(common.button.promptmessage, gz.msg.dosucceed+"！",function(btn){
                if(pathform)
                    accounting.backToAccount();
                else{
                    var modu = '0';
                    var type = '0';
                    if('Z~30DuTtqmt~33kPAATTP~33HJDPAATTP'==imodule){
                        modu = '1';
                    }
                    window.location.href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype="+type+"&imodule="+modu+"&currentPage="+currentPage+"";
                }
            });
        }else{
            Ext.showAlert(result.message);
        }
    },
    // 返回
    returnBack:function(){
        mainPanel.close();
        change_me.removeSelfCss();
        if(pathform){
            accounting.backToAccount();
        }else{
            var modu = '0';
            var type = '0';
            if('Z~30DuTtqmt~33kPAATTP~33HJDPAATTP'==imodule){
                modu = '1';
            }
            window.location.href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype="+type+"&imodule="+modu+"&currentPage="+currentPage+"";
        }
    },
    // 导出
    expportData:function(tableType){
        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("tableType",tableType);
        Rpc({functionId:'GZ00000209',success:function(form,action){
                var result = Ext.decode(form.responseText);
                var fieldName = getDecodeStr(result.fileName);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
            }},map);
    },
    // 复写样式，不影响总体Css
    createSelfCss:function(){
        Ext.util.CSS.createStyleSheet("div.x-accordion-item .x-accordion-hd{background: #ffffff !important;border-width: 1px !important;border-color: #C5C5C5 !important;}","whiteLine");//消除列表上端横线
        Ext.util.CSS.createStyleSheet("div.x-accordion-item .x-grid-header-ct{border-width: 1px !!!important;border-color: #C5C5C5;!important}","tableLine");//表头上加细实线  zhaoxg add 2016-5-4
//		Ext.util.CSS.createStyleSheet(".x-accordion-item .x-accordion-hd-last-collapsed{border-bottom:1px solid;border-bottom-color: #c5c5c5}","solid1");//列表分隔线设定为实线
//		Ext.util.CSS.createStyleSheet(".x-panel-header-default{font-size: 11px;border: 1px solid #c5c5c5}","solid2");//列表下边线设定为实线
//		Ext.util.CSS.createStyleSheet(".x-accordion-hd .x-tool-img{background-color: #ffffff}","sf1");//手风琴图片
//		Ext.util.CSS.createStyleSheet("div.x-accordion-item .x-grid-header-ct{border-width: 0 0 0px !important}","solid3");
//		Ext.util.CSS.createStyleSheet("div.x-toolbar-default{border-left-color:#c5c5c5;border-right-color:#c5c5c5;border-bottom-color:#ffffff;border-width: 1px;background-image: none;background-color: white;}","solid3");
//		Ext.util.CSS.createStyleSheet("div.x-accordion-item .x-accordion-hd-last-collapsed{border-bottom-color: #c5c5c5}","solid3");
        //Ext.util.CSS.createStyleSheet("div.x-accordion-item .x-accordion-hd{background: #d9e7f8;border-top-color: #ffffff;padding: 4px 5px 5px 5px}","solid3");
//		var tittleX = Ext.fly("addPanel_header_hd").getX();//标题X
//		var tittleY = Ext.fly("addPanel_header_hd").getY();//标题Y
//		Ext.util.CSS.createStyleSheet(".x-tool-img{position:absolute;right:"+tittleX+"px;overflow: hidden;width: 15px;height: 15px;background-image: url(/../../components/tableFactory/tableGrid-theme/images/tools/tool-sprites.gif);margin: 0}","sf2");//手风琴图片
        //Ext.util.CSS.createStyleSheet(".x-accordion-item .x-accordion-hd{background: #d9e7f8;border-top:1px dashed #C5C5C5;padding: 4px 5px 5px 5px}","dashed1");
    },
    // 移除复写的样式，不影响总体Css
    removeSelfCss:function(){
        Ext.util.CSS.removeStyleSheet("whiteLine");
        Ext.util.CSS.removeStyleSheet("solid1");
        Ext.util.CSS.removeStyleSheet("solid2");
        //Ext.util.CSS.removeStyleSheet("dashed1");
    },
    // 创建导出按钮（小图标）
    createToolBtn:function(type, id, fnVlue){
        var btn = Ext.widget({
            xtype: 'tool',
            type:type,
            margin:'0 8 0 0',
            // text:'导出',
            id:id,
            listeners: {
            	el:{
            		// 没找到tool中属性有提示的，间接处理下
	            	mouseover: function () {
	            		Ext.getDom(id).title = gz.label.zxdeclare.exportText;
	            	}
            	}
            },
            handler : function() {change_me.expportData(fnVlue);}
        });
        return btn;
    }
});