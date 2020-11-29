/**
 * 年计划 任务跟踪
 * @author haosl
 */
Ext.define('YearPlanTraceUL.YearPlanTrace',{
    year:undefined,//选中的年份
    id:'yearPlanTrace',
    yearStore:undefined,//年度数据（2016 2015...）
    planRecord:'',//记录选中的计划
    planDataStore:undefined,
    planId:undefined,//用于代办任务定位  haosl 20170310 add
    quarter:undefined,//用于代办季度定位 haosl 20170310 add
    mapState:undefined,
    planState:{},//用于记录计划颜色
    standardFunction:{//编辑器的工具条
        toolbarGroups:[
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'insert', groups: [ 'insert' ] },
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about'] }
        ],
        removeButtons:'BidiRtl,BidiLtr,CreateDiv,Blockquote,Subscript,Superscript,RemoveFormat,Save,Source,NewPage,Cut,Copy,Paste,PasteText,PasteFromWord,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,SelectAll,Anchor,About,SpecialChar,Flash,Iframe,Language'
    },
    constructor:function(config){
    	this.planId = config.planId;
    	this.year = config.year;
    	this.quarter = config.quarter;
        wp_yearPlanTrace = this;
        wp_yearPlanTrace.init();
        wp_yearPlanTrace.createMainPanel();//创建主面板
    },
    init:function(){
        wp_yearPlanTrace.loadYearList();
        var store = wp_yearPlanTrace.yearStore;
        if(store && store.getCount()>0){
            var year = "";
            if(wp_yearPlanTrace.year)//代办链接过来的会带过来年份的值，没有的话是通过点击任务跟踪菜单过来的；haosl 20170310
            	year=wp_yearPlanTrace.year
        	else
        		year = new Date().getFullYear();
            var record = store.findRecord("value",year);
            if(!record)
                record = store.getAt(0);
            wp_yearPlanTrace.year= record.data.value;
        }
        //查询方案数据 并且为全局 变量赋值
        wp_yearPlanTrace.getPlanData();
        var planStore = wp_yearPlanTrace.planDataStore;
        if(planStore && planStore.getCount()>0){
        	if(wp_yearPlanTrace.planId){
        		var rec = planStore.findRecord("P1700",wp_yearPlanTrace.planId);
        		if(rec)
        			wp_yearPlanTrace.planRecord=rec;
        		else
        			wp_yearPlanTrace.planRecord = planStore.getAt(0);//初始化选中任务
        	}else
    			wp_yearPlanTrace.planRecord = planStore.getAt(0);//初始化选中任务
        	
        }
    },
    /** 根据年度加载计划数据 **/
    getPlanData:function(){
        var map = new HashMap();
        map.put("opt","0");
        var planStore = Ext.create("Ext.data.Store",{
            id:'planDataStore',
            fields: ['P1700','P1701','P1705','P1729','P1709','P1711','P1713','P1715','P1717','P1719','P1721','P1720','P1723','P1731','P1745','P1747']
        })
        if(wp_yearPlanTrace.year){
            map.put("year",wp_yearPlanTrace.year);
              Rpc({functionId:'WP00002005',async:false,success:function(form){
                      var result = Ext.decode(form.responseText);
                      var data = result.planList;
                      //查询各个任务的状态
                      planStore.loadData(data,false);
              }},map);
         }
      wp_yearPlanTrace.planDataStore = planStore;
    },
    //年度下拉
    loadYearList:function(){
        var map = new HashMap();
        map.put("opt","1");//加载年度
      Rpc({functionId:'WP00002005',async:false,success:function(form){
          var result = Ext.decode(form.responseText);
          if(!result.succeed)
              Ext.Msg.alert("提示信息",result.message);
          var yearList = result.yearList;
          var dataArr = new Array();
          for(var i in yearList){
              dataArr.push({year:yearList[i]+"年",value:yearList[i]});
          }
          wp_yearPlanTrace.yearStore = Ext.create("Ext.data.Store",{// 会议 数据源
               id:'yearComboStore',
               fields: ['year','value'],
               data: dataArr
           });
      }},map);
    },
    /**创建主面板*/
    createMainPanel:function(){
        var planList = wp_yearPlanTrace.createPlanList();
        //季度完成情况面板
        var achievement = wp_yearPlanTrace.createAchievement();
        //季度完成情况汇报过程面板
        var achievementProcess = wp_yearPlanTrace.createAchievementProcess();
        var taskInfo = wp_yearPlanTrace.createTaskInfo();
        //任务列表面板和任务责任人
        var panelArr = new Array();
        panelArr.push(achievement);
        panelArr.push(achievementProcess);
        panelArr.push(taskInfo);
        var rightPanel = Ext.widget('container',{
            layout:'vbox',
            flex:1,
            height:'100%',
            scrollable:true,
            border:false,
            items:panelArr
        });
        Ext.widget('viewport',{
            id:'myViewPort',
            layout:'fit',
            items:[{
                xtype:'panel',
                title:wp.yearplan.trace.title,
                border:false,
                layout: 'hbox',
                items:[planList,rightPanel]
            }],
            listeners:{ 
                'afterrender':function(){
                    //显示年计划下拉
                    wp_yearPlanTrace.displayYearlist();
                   
                    var yearCombo = Ext.getCmp('yearCombo');
                    if(yearCombo){
                        var store =wp_yearPlanTrace.yearStore;
                        //默认选中
                        if(store.getCount()>0){
                            var year = wp_yearPlanTrace.year; 
                            var record = store.findRecord("value",year);
                            if(!record)
                                record = store.getAt(0);
                            yearCombo.setSelection(record);
                            var store = Ext.getCmp("planView").getStore();
                            if(store.getCount()>0){
                                Ext.getCmp("planView").getSelectionModel().select(wp_yearPlanTrace.planRecord);
                            }
                        }
                    }
                }
            },
            renderTo:Ext.getBody()
        });
    },
    /** 任务列表和任务责任人面板*/
    createPlanList:function(){
        var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="hj_item_event {[xindex % 2 === 0 ? "hj_item_odd" : ""]}">',
                        '<div id="plan{P1700}"  style="border-left:3px solid {[this.setColor(values)]}">',
                             '<div class="year_plan_nameCls" title="{P1705}">{[this.converstr(values.P1705)]}</div>',
                         '</div>',
                    '</div>',
                '</tpl>',
                    {
                        setColor:function (values){//显示计划的状态（颜色）
                            var mapState = wp_yearPlanTrace.getCurrentRole(values.P1700);
                            wp_yearPlanTrace.setPlanColor(values,mapState);
                            return wp_yearPlanTrace.planState[values.P1700];
                        },
                        converstr:function(p1705){
                            return wp_yearPlanTrace.convertStr(p1705);
                        }
                    }
        );
        var dataview = Ext.create("Ext.view.View",{
            id:'planView',
            itemSelector : 'div .hj_item_event',
            scrollable:'y',
            tpl:tpl,
            deferEmptyText:false,
            emptyText:'<span style="font-size:14px">没有任务!</span>',//空文本时显示
            overItemCls:'overCls',
            selectedItemCls:'selectedCls',
            store:wp_yearPlanTrace.planDataStore,
            multiSelect:false,
            listeners:{
                select:wp_yearPlanTrace.fireSelect
            }
        });
        Ext.util.CSS.createStyleSheet("#planListPanel .x-panel-header{background-color:#F9F9F9;}","delImg");
        var title ='<div id = "yeardownlist" class="yeardownlist"></div> <div style="float:left;font-size:15px;margin:3px 0 0 10px;">任务列表</div>'
        return Ext.create("Ext.panel.Panel",{
            id:'planListPanel',
            layout:'fit',
            height:'100%',
            width:225,
            title:title,
            margin:'5 2 5 5',
            border:'true',
            scrollabel:false,
            items:dataview
        });
    },
    /** 季度完成情况面板 */
    createAchievement:function(){
        var tabArr  = new Array();
       var selectPlan = wp_yearPlanTrace.planRecord;
       var mapState;
       if(selectPlan){
           mapState = wp_yearPlanTrace.getCurrentRole(selectPlan.data.P1700);//mapState 当前季度总结状态和当前用户角色
           wp_yearPlanTrace.mapState = mapState;
       }
       for(var i=1;i<=4;i++){
            var state = "";
            var btnBar = wp_yearPlanTrace.getBtnBar(i,mapState);//按钮条
            var title = "第"+wp_yearPlanTrace.convertQuarter(i)+"季度完成情况<span id='achivmentTitle"+i+"'></span>";
            var panelItem = Ext.create("Ext.panel.Panel",{
                id:'achievement'+i,
                title:title,
                quarter:i,
                layout:'fit',
                margin:"5 5 5 5",
                border:false,
                fbar:btnBar,
                buttonAlign:'left',
                listeners:{
                    activate:wp_yearPlanTrace.activate
                }
            });
            tabArr.push(panelItem);
        }
        return Ext.create("Ext.tab.Panel",{
            id:'tabPanel',
            deferredRender:false,//禁止延时渲染
            margin:'5 5 0 5',
            width:'100%',
            flex:5,
            minHeight:270,
            items:tabArr
        });
    },
    /** 季度完成情况汇报过程面板 */
    createAchievementProcess:function(){
        var processPanel = Ext.create("Ext.panel.Panel",{
            id:'processPanel',
            margin:'5 5 5 5',
            border:true,
            scrollable:true,
            collapsible:false,
            collapsed:true,
            width:'100%',
            maxHeight:180,
            minHeight:180,
            layout:"vbox",
            title:'<label id="processTitle"></label><img id="processImg" style="cursor:pointer;position:relative;top:2px;margin-left:5px;" src="/images/new_module/expand.png" onclick="wp_yearPlanTrace.clickTitleIcon(\'processPanel\')"/>',
            listeners:{
                'beforeexpand' : function(o) {
                    wp_yearPlanTrace.setIcon('processImg','up');// 收起按钮
                },
                'beforecollapse' : function(o) {
                    wp_yearPlanTrace.setIcon('processImg','down');// 展开按钮
                }
            }
        });
        return processPanel;
    },
    /** 创建按钮条 **/
    getBtnBar:function(index,mapState){
        //保存
        var savebtn = Ext.widget("button",{
            id:'savebtn'+index,
            text:wp.yearplan.trace.savebtn,
            handler:function(){
              wp_yearPlanTrace.save(index);
            }
        });
        //报批
        var toApprovalbtn = Ext.widget("button",{
            id:'toApprovalbtn'+index,
            text:wp.yearplan.trace.toApprovalbtn,
            handler:function(btn){
                wp_yearPlanTrace.toApproval(btn,index);
            }
        });
        //撤回
        var revocationbtn = Ext.widget("button",{
            id:'revocationbtn'+index,
            text:wp.yearplan.trace.revocationbtn,
            handler:function(){
                wp_yearPlanTrace.revocation(index);
            }
        });
      //批准
        var approvalbtn = Ext.widget("button",{
            id:'approvalbtn'+index,
            text:wp.yearplan.trace.approvalbtn,
            handler:function(){
                wp_yearPlanTrace.approval(index);
            }
        });
      //退回
        var rejectbtn = Ext.widget("button",{
            id:'rejectbtn'+index,
            text:wp.yearplan.trace.rejectbtn,
            handler:function(){
                wp_yearPlanTrace.reject(index);
            }
        });

        //发布
        var deployelbtn = Ext.widget("button",{
            id:'deployelbtn'+index,
            text:wp.yearplan.trace.deploye,
            handler:function(btn){
                wp_yearPlanTrace.toApproval(btn,index);
            }
        });
        var btnArr = new Array();
        if(!mapState)
            return btnArr;
       var state = mapState[index];
       var hasVerifier = mapState.hasVerifier//是否有审核人
       var role = mapState.role.join(",");
        if(role.indexOf('7')>-1){//责任人
            if(!state || state=="01" || state=="07"){//没有填写或起草状态
                revocationbtn.hidden = true;
                savebtn.hidden=false;
                toApprovalbtn.hidden = false;
            }else if((hasVerifier=='0' && state=="02") || state=='08'){//待批、报审状态
                revocationbtn.hidden=false;
                savebtn.hidden=true;
                toApprovalbtn.hidden = true;
            }else{
                revocationbtn.hidden=true;
                savebtn.hidden=true;
                toApprovalbtn.hidden = true;
            }
        } else if(role.indexOf('8')>-1){//审批人 为待批状态时显示按钮
            if(state != "02"){//不是待批状态隐藏按钮
                approvalbtn.hidden = true;
                rejectbtn.hidden = true;
            }else{
                approvalbtn.hidden = false;
                rejectbtn.hidden = false; 
            }
        } else if(role.indexOf('3')>-1){//审核人
            if(state!="08" && state != "07"){
                savebtn.hidden = true;
                deployelbtn.hidden = true;
                rejectbtn.hidden = true;
            }else{
                savebtn.hidden = false;
                deployelbtn.hidden = false;
                rejectbtn.hidden = false;
            }
        }
        btnArr.push(savebtn);
        btnArr.push(toApprovalbtn);
        btnArr.push(deployelbtn);
        btnArr.push(revocationbtn);
        btnArr.push(approvalbtn);
        btnArr.push(rejectbtn);
        
        
        return btnArr;
    },
    /**
     * 显示 季度完成情况
     */
    activate:function(activeItem){
    	//创建页签下的组件
    	var i = activeItem.quarter;
    	var CKEditor = Ext.getCmp("ckeditorid"+i);
        var contenxttext = Ext.getCmp("contenxttext"+i);
        var selectPlan = wp_yearPlanTrace.planRecord;
        var htmlValue="";
        if(!wp_yearPlanTrace.mapState)
        	return;
        var role =  wp_yearPlanTrace.mapState.role.join(",");
        var state = wp_yearPlanTrace.mapState[i];
        var isSelf = wp_yearPlanTrace.mapState['isSelf_'+i];
        if(selectPlan&&i==1 && selectPlan.data.P1709)
            htmlValue = selectPlan.data.P1709;
        else if(selectPlan&&i==2 && selectPlan.data.P1711)
            htmlValue = selectPlan.data.P1711;
        else if(selectPlan&&i==3 && selectPlan.data.P1713)
            htmlValue = selectPlan.data.P1713;
        else if(selectPlan&&i==4 && selectPlan.data.P1715)
            htmlValue = selectPlan.data.P1715;
        if(!CKEditor){
        	CKEditor = Ext.create("EHR.ckEditor.CKEditor",{
	             id:'ckeditorid'+i,
	             ckEditorConfig:wp_yearPlanTrace.standardFunction,
	             style:{
	                 border:'1px solid #C5C5C5'
	             },
	             hidden:true,
	             value:htmlValue
	         });
        	activeItem.add(CKEditor);
        }
        if(!contenxttext){
        	if((role.indexOf('3')>-1 && (state=="01" || !state))
                    ||(role.indexOf('8')>-1 && (state=="01" || !state　|| state=="08")　)){
        		htmlValue = "<div style='color:#4FAAFE;font-size:16px;'>该季度总结未汇报！</font>"
            }else if((role.indexOf('3')>-1&& state=="07")
                    ||(role.indexOf('8')>-1 && state=="07")){
            	htmlValue = "<div style='color:#4FAAFE;font-size:16px;'>待汇报人修改！</font>"
            }
        	contenxttext = Ext.widget("container",{
	             id:'contenxttext'+i,              
	             style:{
	                 border:'1px solid #C5C5C5'
	             },
	             scrollable:true,
	             hidden:true,
	             html:"<div style='margin:20px 20px' id='container_html"+i+"'>"+htmlValue+"</div>",
	        }); 
        	activeItem.add(contenxttext);
        }
        if(CKEditor.editor && CKEditor.editor.document){
        	CKEditor.editor.document.getBody().focus();
        }
        if((role.indexOf('7')>-1&& (state=="01" || !state || (state=="07" && isSelf=='1')))
                ||(role.indexOf('3')>-1 && (state=="08" || (state=="07" && isSelf=='1')))){//state==undefined时代表未填写 01 =起草
        	   contenxttext.setHidden(true);
        	   CKEditor.setHidden(false);
          }else{
        	   CKEditor.setHidden(true);
       		   contenxttext.setHidden(false);
          }
        var title = activeItem.title.substr(0,8);
        var titleEl = document.querySelector("#processTitle");
        titleEl.innerHTML = title+"汇报过程";
        //显示过程信息
        wp_yearPlanTrace.displayProcessInfo(i);
    },
    /**
     * 加载下拉年份
     */
    displayYearlist:function(){
        Ext.create('Ext.form.ComboBox', {
            id:'yearCombo',
            queryMode: 'local',
            style:"width:70px",
            displayField: 'year',
            store:wp_yearPlanTrace.yearStore,
            valueField: 'value',
            editable:false,
            renderTo: 'yeardownlist',
            listeners:{
                select:function(combo,record){
                    wp_yearPlanTrace.year = record.data.value;//获得计划的时候需要
                    wp_yearPlanTrace.getPlanData();
                    var planView = Ext.getCmp("planView");
                    var store = wp_yearPlanTrace.planDataStore;
                    var rec ="";
                    planView.setStore(store);
                    if(wp_yearPlanTrace.planId){
                		rec = store.findRecord("P1700",wp_yearPlanTrace.planId);
                		if(!rec)
                			rec = store.getAt(0);//初始化选中任务
                	}else
                		rec = store.getAt(0);//初始化选中任务
                	
            		if(rec)
            			planView.getSelectionModel().select(rec);
                }
            }
        });
    },
    /** 责任人信息面板 **/
    createTaskInfo:function(){
        return Ext.create("Ext.panel.Panel",{
            id:'taskInfo',
            margin:'0 5 5 5',
            border:true,
            scrollable:true,
            collapsible:false,
            collapsed:true,
            width:'100%',
            layout:'column',
            maxHeight:75,
            minHeight:75,
            title:'任务责任信息<img id="taskInfoImg" style="cursor:pointer;position:relative;top:2px;margin-left:5px;" src="/images/new_module/expand.png" onclick="wp_yearPlanTrace.clickTitleIcon(\'taskInfo\');"/>',
            html:'<div id="displayTaskInfo" class="taskInfoCls"></div>',
            listeners:{
                'beforeexpand' : function(o) {
                    wp_yearPlanTrace.setIcon('taskInfoImg','up');// 收起按钮
                },
                'beforecollapse' : function(o) {
                    wp_yearPlanTrace.setIcon('taskInfoImg','down');// 展开按钮
                }
            }
        });
    },
    //处理任务列表选中事件
    fireSelect:function(model,record,index){
        //当前登录人的角色（责任人或审批人） 当前任务季度总结填写状态
        wp_yearPlanTrace.needQuarter(record.data.P1745,record.data.P1747);//显示需要填写的季度
        var mapState = wp_yearPlanTrace.getCurrentRole(record.data.P1700);
        
        wp_yearPlanTrace.mapState = mapState;
        wp_yearPlanTrace.planRecord = record;//记录下选中的计划id\
        //选中有代办的季度总结===============================
        var tabPanel = Ext.getCmp("tabPanel")
        if(tabPanel){
            var role =  mapState.role.join(",");
            var flag = true;
            var achievement = tabPanel.child('#achievement'+wp_yearPlanTrace.quarter);//代办进入的季度总结（需要默认定位到的）
            if(flag&&achievement && !achievement.tab.hidden){
            	tabPanel.setActiveTab(achievement);
            	flag = false;
            }
            for(var i=1;flag && i!=wp_yearPlanTrace.quarter && i<5;i++){//查询个季度的状态
                achievement = tabPanel.child('#achievement'+i);
                var state = mapState[i];
                if(!achievement.tab.hidden && role.indexOf('7')>-1){//责任人
                    if(!state || state=="01" || (state=="07" && mapState['isSelf_'+i]=="1")){//没有填写或起草状态
                        tabPanel.setActiveTab(achievement);
                        flag = false;
                        break;
                    }
                } else if(!achievement.tab.hidden && role.indexOf('8')>-1){//审批人 
                    if(state == "02"){
                        tabPanel.setActiveTab(achievement);
                        flag = false;
                        break;
                    }
                } else if(!achievement.tab.hidden && role.indexOf('3')>-1){//审核人
                    if(state=="08" || (state=="07" && mapState['isSelf_'+i]=="1")){
                        tabPanel.setActiveTab(achievement);
                        flag = false;
                        break;
                    }
                }
            }
            if(flag){
                for(var i=1;i<5;i++){//显示非隐藏的第一个页签
                   achievement = tabPanel.child('#achievement'+i);
                   if(!achievement.tab.hidden){
                       tabPanel.setActiveTab(achievement);
                       break;
                   }
                }
            }
           wp_yearPlanTrace.displayProcessInfo(achievement.quarter);
        }
        //更新季度完成情况的状态标识==============
        wp_yearPlanTrace.updateAchievementState(record.data.P1700,mapState);
     
         
       //按钮显示情况
       wp_yearPlanTrace.displayButtons(mapState);
     
     //更新责任单位面板信息
     wp_yearPlanTrace.displayTaskInfo(record);
     //显示任务完成情况 
     wp_yearPlanTrace.displayAchievementInfo(record,mapState);

    },
    /** 显示责任单位相关信息 **/
    displayTaskInfo:function(plan){
       var html="";
       html+='<span>责任单位：<font color="#85A6DB">'+(!plan.data.P1717?'无':plan.data.P1717)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       html+='<span>牵头单位：<font color="#85A6DB">'+(!plan.data.P1719?'无':plan.data.P1719)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       html+='<span>公司领导：<font color="#85A6DB">'+(!plan.data.P1721?'无':plan.data.P1721)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       html+='<span>审批人：<font color="#85A6DB">'+(!plan.data.P1720?'无':plan.data.P1720)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       html+='<span>审核人：<font color="#85A6DB">'+(!plan.data.P1723?'无':plan.data.P1723)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       html+='<span>责任人：<font color="#85A6DB">'+(!plan.data.P1731?'无':plan.data.P1731)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       var displayTaskInfo = document.querySelector("#displayTaskInfo");
       if(displayTaskInfo)
           displayTaskInfo.innerHTML = html;
    },
    /** 显示季度完成情况
     * reocrd 左侧选中的计划
     *  */
    displayAchievementInfo:function(record,mapState){
       var ckeditor;
       var role =  mapState.role.join(",");
       for(var i=1;i<5;i++){
           var state = mapState[i];
           var isSelf = mapState['isSelf_'+i];
           ckeditor = Ext.getCmp("ckeditorid"+i);
           var value = "";
           if(i==1 && record.data.P1709)
               value = record.data.P1709;
           else if(i==2 && record.data.P1711)
               value = record.data.P1711;
           else if(i==3 && record.data.P1713)
               value = record.data.P1713;
           else if(i==4 && record.data.P1715)
               value = record.data.P1715
           if(ckeditor)
               ckeditor.setValue(value);
           var contenxttext = Ext.getCmp("contenxttext"+i);
    	   var div = document.querySelector("#container_html"+i);
           if((role.indexOf('3')>-1 && (state=="01" || !state))
                   ||(role.indexOf('8')>-1 && (state=="01" || !state　|| state=="08")　)){
               value = "<div style='color:#4FAAFE;font-size:16px;'>该季度总结未汇报！</font>"
           }else if((role.indexOf('3')>-1&& state=="07")
                   ||(role.indexOf('8')>-1 && state=="07")){
               value = "<div style='color:#4FAAFE;font-size:16px;'>待汇报人修改！</font>"
           }
           if(div)
        	   div.innerHTML = value;
           if((role.indexOf('7')>-1&& (state=="01" || !state || (state=="07" && isSelf=='1')))
                 ||(role.indexOf('3')>-1 && (state=="08" || (state=="07" && isSelf=='1')))){//state==undefined时代表未填写 01 =起草
        	   if(contenxttext)
        		   contenxttext.setHidden(true);
               if(ckeditor)
            	   ckeditor.setHidden(false);
           }else{
        	   if(ckeditor)
        		   ckeditor.setHidden(true);
        	   if(contenxttext)
        		   contenxttext.setHidden(false);
           }
       }
    },
    /** 保存季度完成情况 **/
    save:function(index){
        var planId = wp_yearPlanTrace.planRecord.data.P1700;
        var map = new HashMap();
        map.put("opt","0");//保存
        map.put("planId",planId);
        var achvContent = Ext.getCmp('ckeditorid'+index).getHtml();
        var itemId = '';
        if(index==1)
            itemId = 'P1709';
        else if(index==2)
            itemId = 'P1711';
        else if(index==3)
            itemId = 'P1713';
        else
            itemId = 'P1715';
        wp_yearPlanTrace.planRecord.data[itemId] = achvContent;//更新季度总结内容
        map.put("content",achvContent);
        map.put("itemId",itemId);
        Rpc({functionId:'WP00002006',async:false,success:function(form){
            var result = Ext.decode(form.responseText);
            var msg = "";
            if(result.msg){
               msg = result.msg;
               var role = wp_yearPlanTrace.mapState.role.join(",");
               if(role.indexOf('7')>-1){
            	   wp_yearPlanTrace.mapState[index] = "01";
               }else
               	   wp_yearPlanTrace.mapState[index] = "08";
              
               wp_yearPlanTrace.refreshStatus(wp_yearPlanTrace.planRecord,wp_yearPlanTrace.mapState,"save");
               wp_yearPlanTrace.displayProcessInfo(index);
            }else
                msg = result.message;
            Ext.Msg.alert("提示信息",msg);  
        }},map);
    },
    /**
     * 根据状态代码返回任务状态
     * role 登录用户的角色
     *  state 季度总结状态
     *  hasVerifier 是否有审核人
     *  isSelf 当前办理人是否是自己（登录用户）
     * */
    getAchievementState:function(role,state,hasVerifier,isSelf){
        var value = "";
        /**
         * 01：起草；02：待批；03：已批；07：驳回；08：报审。
         **/
        if(role){
            if(role.indexOf('8')>-1){//当前任务的任务角色是审核人
                
                switch (state) {
                case '01':
                    value = "（未汇报）";
                    break;
                case '02':
                    value = "（请审批）";
                    break;
                case '03':
                    value = "（已完成）";
                    break;
                case '07':
                    value = "（已退回）";
                    break;
                case '08':
                    value = "（未汇报）";
                    break;
                default:
                    value = "（未汇报）";
                    break;
                }
            }else if(role.indexOf('7')>-1) {//责任人
                switch(state){
                case '01':
                    value = "（起草中）";
                    break;
                case '02':
                    value = "（审批中）";
                    break;
                case '03':
                    value = "（已完成）";
                    break;
                case '07':
                    if(hasVerifier=='1'){//有审核人
                        if(isSelf=='1')//当前办理人是自己
                            value = "（已退回）";
                        else
                            value='（审批中）';//当前办理人不是自己，证明退回到了审核人。
                    }else
                        value = "（已退回）"; 
                    break;
                case '08':
                    value = "（审核中）";
                    break;
                default:
                    value = "（请填写）";
                    break;
                }
            }else if(role.indexOf('3')>-1){//审核人
                switch(state){
                case '01':
                    value = "（未汇报）";
                    break;
                case '02':
                    value = "（审批中）";
                    break;
                case '03':
                    value = "（已完成）";
                    break;
                case '07':
                    value = "（已退回）";
                    break;
                case '08':
                    value = "（请审核）";
                    break;
                default:
                    value = "（未汇报）";
                    break;
                }
            }
        }
        return value;
    },
    /**
     * 更新标题和按钮的显示情况
     * */
    updateAchievementState:function(planId,mapState){
       var role =  mapState.role.join(",");
      var hasVerifier = mapState.hasVerifier;
       //更新四个季度标题
       for(var i=1;i<5;i++){
           var key = i+"";
           var state = mapState[i];
           var isSelf = mapState['isSelf_'+i];//当前办理人是否是自己
           var value  = wp_yearPlanTrace.getAchievementState(role,state,hasVerifier,isSelf);
           if(value && value.length>0){
               var titleEl = document.querySelector("#achivmentTitle"+i);  
               titleEl.innerHTML = value;
           }
       }
    },
    /** 报批 **/
    toApproval:function(btn,index){
       var planId = wp_yearPlanTrace.planRecord.data.P1700;
       var CKEditor = Ext.getCmp("ckeditorid"+index)
       if(CKEditor){
           var value = CKEditor.getHtml();
           if(!value || value.length==0){
               Ext.Msg.alert("提示信息","请先填写总结内容！");
               return;
           }
       }
        Ext.Msg.confirm("提示信息", "是否"+btn.getText()+"第"+wp_yearPlanTrace.convertQuarter(index)+"季度完成情况？", function(id){
            if(id=='yes'){
                var map = new HashMap();
                map.put("opt","2");//保存
                var achvContent = Ext.getCmp('ckeditorid'+index).getHtml();
                var itemId = '';
                if(index==1)
                    itemId = 'P1709';
                else if(index==2)
                    itemId = 'P1711';
                else if(index==3)
                    itemId = 'P1713';
                else
                    itemId = 'P1715';
                wp_yearPlanTrace.planRecord.data[itemId] = achvContent;//更新季度总结内容
                map.put("content",achvContent);
                map.put("itemId",itemId);
                map.put("planId",planId);
                map.put("quarter",index);
                Rpc({functionId:'WP00002006',async:false,success:function(form){
                    var result = Ext.decode(form.responseText);
                    var msg = "";
                    if(result.msg){
                        msg = result.msg;
                        var role = wp_yearPlanTrace.mapState.role.join(",");
                        if(role.indexOf('7')>-1&& wp_yearPlanTrace.mapState["hasVerifier"]=="1"){
                        	wp_yearPlanTrace.mapState[index] = "08";
                        }else
                        	wp_yearPlanTrace.mapState[index] = "02";
                        wp_yearPlanTrace.mapState["isSelf_"+index]="0";
                        wp_yearPlanTrace.refreshStatus(wp_yearPlanTrace.planRecord,wp_yearPlanTrace.mapState);
                        wp_yearPlanTrace.displayProcessInfo(index);
                    }else
                        msg = result.message;
                    Ext.Msg.alert("提示信息",msg);   
                }},map); 
            }
        });
    },
    setPlanColor:function(data,mapState){
        //开始时间和结束时间可以确定要填写的季度，根据要填写的季度，组装季度的状态
        var startTime=data.P1745;
        var endTime=data.P1747;
        var planId =data.P1700;
        var role =  mapState.role.join(",");
        var state = ",";
        var currentYear = new Date().getYear();//得当当前年份
        if(!startTime){
            if(!endTime){
                for(var j=1;j<=4;j++){
                    if(mapState[j]=='07' && mapState['isSelf_'+j]=="1")
                        state +="07_,";//07 和07_ 区分退回状态的总结，当前办理人是否是自己（07_代表 是 07 否）
                    else
                        state +=mapState[j]+",";
                }
            }else{
            	 endTime = endTime.replace(/\-/g,'/');
                 var endYear = new Date(endTime).getYear();//得到填写的年份
                 var endMoth = "";
                 if(endYear>currentYear)
                 	endMoth = 12;
                 else
                 	endMoth = new Date(endTime).getMonth()+1;//1-12
                var endQ =  Math.ceil(endMoth/3);
                for(var j=1;j<=endQ;j++){
                    if(mapState[j]=='07' && mapState['isSelf_'+j]=="1")
                        state +="07_,";//07 和07_ 区分退回状态的总结，当前办理人是否是自己（07_代表 是 07 否）
                    else
                        state +=mapState[j]+",";
                }
            }
        }else{
        	startTime = startTime.replace(/\-/g,'/');
            var startYear = new Date(startTime).getYear();//得到填写的年份
            var startMoth = "";
            if(startYear<currentYear)
            	startMoth = 1;
            else
            	startMoth = new Date(startTime).getMonth()+1//1-12;
            var startQ = Math.ceil(startMoth/3);
            if(!endTime){
                for(var i=startQ;i<=4;i++){
                    if(mapState[i]=='07' && mapState['isSelf_'+i]=="1")
                        state +="07_,";//07 和07_ 区分退回状态的总结，当前办理人是否是自己（07_代表 是 07 否）
                    else
                        state +=mapState[i]+",";
                 }  
            }else{
            	 endTime = endTime.replace(/\-/g,'/');
                 var endYear = new Date(endTime).getYear();//得到填写的年份
                 var endMoth = "";
                 if(endYear>currentYear)
                 	endMoth = 12;
                 else
                 	endMoth = new Date(endTime).getMonth()+1;//1-12
                var endQ =  Math.ceil(endMoth/3);
                if(endMoth<startMoth)
                    state="";
                else{
                   
                    for(var i=startQ;i<=endQ;i++){
                        if(mapState[i]=='07' && mapState['isSelf_'+i]=="1")
                            state +="07_,";//07 和07_ 区分退回状态的总结，当前办理人是否是自己（07_代表 是 07 否）
                        else
                            state +=mapState[i]+",";
                    }
                }
            }
        }
        //====季度状态颜色
        var color = "";
        if(role.indexOf('7')>-1){//责任人
            if(state.indexOf(",undefined,")!=-1
               ||state.indexOf(",07_,")!=-1
               ||state.indexOf(",01,")!=-1){
                color="#FFE56A"; //黄色 (待办)
            }else if(state.indexOf(",08,")!=-1
                    ||state.indexOf(",02,")!=-1
                    ||state.indexOf(",07,")!=-1){
                color="#589AFE";//蓝色（进行中）
            }else{
                color="#53C383";//绿色（无需关注）
            }
        }else if(role.indexOf('3')>-1){//审核人
            if(state.indexOf(",08,")!=-1
                    ||state.indexOf(",07_,")!=-1) {
                     color="#FFE56A"; //黄色 (待办) 
                 }else if(state.indexOf(",02,")!=-1){
                     color="#589AFE";//蓝色（进行中）
                 }else{
                     color="#53C383";//绿色（无需关注）
                 }
        }else if(role.indexOf('8')>-1){//审批人
            if(state.indexOf(",02,")!=-1)
                color="#FFE56A"; //黄色 (待办) 
            else
                color="#53C383";//绿色（无需关注）
        }
        wp_yearPlanTrace.planState[planId] = color;
    },
    /**
     * 刷新任务状态
     * \
     * 当opt 为save时不刷新任务完成情况。
     */
    refreshStatus:function(record,mapState,opt){
        //更新任务颜色
        wp_yearPlanTrace.setPlanColor(record.data,mapState);
        var div = Ext.getDom("plan"+record.data.P1700);
        if(div){
            div.style.borderColor=wp_yearPlanTrace.planState[record.data.P1700];
        }
        wp_yearPlanTrace.planState[record.data.P1700];
       //更新当前季度状态
        wp_yearPlanTrace.updateAchievementState(record.data.P1700,mapState);
        //显示任务完成情况 
        if(opt!="save")
        	wp_yearPlanTrace.displayAchievementInfo(record,mapState);
        
      //按钮显示情况
      wp_yearPlanTrace.displayButtons(mapState);
    },
    /**
     * 获得当前登陆人的角色信息
     * */
    getCurrentRole:function(planId){
        var map = new HashMap();
        map.put("opt","1");
        map.put("planId",planId);
        var mapState={};
        Rpc({functionId:'WP00002006',async:false,success:function(form){
           var result = Ext.decode(form.responseText);
           mapState = result.mapState;//返回角色信息和季度完成情况状态
        }},map);
        
        return mapState;
    },
    //按钮的显示和隐藏（切换任务和季度时调用）
    displayButtons:function(mapState){
      var hasVerifier = mapState.hasVerifier//是否有审核人
      for(var i = 1;i<5;i++){
          var state = mapState[i];
          var role = mapState.role.join(",");
          var isSelf = mapState["isSelf_"+i]//当前办理人是否是自己
          if(role.indexOf('7')>-1){//责任ren
              if(!state 
                  || state=="01"
                  || (state=="07" && isSelf=='1')){//没有填写或起草状态
                  Ext.getCmp("savebtn"+i).setHidden(false);
                  Ext.getCmp("toApprovalbtn"+i).setHidden(false);
                  Ext.getCmp("revocationbtn"+i).setHidden(true);  
                  Ext.getCmp("approvalbtn"+i).setHidden(true);  
                  Ext.getCmp("deployelbtn"+i).setHidden(true);
                  Ext.getCmp("rejectbtn"+i).setHidden(true);
              }else if((hasVerifier=='0' && state=="02") || state=='08'){//待批、报审状态
                  Ext.getCmp("revocationbtn"+i).setHidden(false);
                  Ext.getCmp("savebtn"+i).setHidden(true);  
                  Ext.getCmp("toApprovalbtn"+i).setHidden(true);  
                  Ext.getCmp("rejectbtn"+i).setHidden(true);
                  Ext.getCmp("deployelbtn"+i).setHidden(true);
                  Ext.getCmp("approvalbtn"+i).setHidden(true);  
              }else{
                  Ext.getCmp("savebtn"+i).setHidden(true);  
                  Ext.getCmp("toApprovalbtn"+i).setHidden(true);  
                  Ext.getCmp("revocationbtn"+i).setHidden(true);
                  Ext.getCmp("rejectbtn"+i).setHidden(true);
                  Ext.getCmp("deployelbtn"+i).setHidden(true);
                  Ext.getCmp("approvalbtn"+i).setHidden(true);  
              }
               
          }else if(role.indexOf('8')>-1){//审批人
              if(state != "02"){//不是待批状态隐藏按钮
                  Ext.getCmp("savebtn"+i).setHidden(true);  
                  Ext.getCmp("toApprovalbtn"+i).setHidden(true);  
                  Ext.getCmp("revocationbtn"+i).setHidden(true);
                  Ext.getCmp("deployelbtn"+i).setHidden(true);
                  Ext.getCmp("approvalbtn"+i).setHidden(true);  
                  Ext.getCmp("rejectbtn"+i).setHidden(true);
              }else{
                  Ext.getCmp("savebtn"+i).setHidden(true);  
                  Ext.getCmp("deployelbtn"+i).setHidden(true);
                  Ext.getCmp("rejectbtn"+i).setHidden(false);  
                  Ext.getCmp("approvalbtn"+i).setHidden(false);  
                  Ext.getCmp("toApprovalbtn"+i).setHidden(true);  
                  Ext.getCmp("revocationbtn"+i).setHidden(true);
              }
          }else if(role.indexOf('3')>-1){//审核人
              if(!(state == "07" &&　isSelf=='1') && state!="08"){
                  Ext.getCmp("savebtn"+i).setHidden(true);  
                  Ext.getCmp("deployelbtn"+i).setHidden(true);  
                  Ext.getCmp("rejectbtn"+i).setHidden(true);  
                  Ext.getCmp("approvalbtn"+i).setHidden(true);  
                  Ext.getCmp("toApprovalbtn"+i).setHidden(true);  
                  Ext.getCmp("revocationbtn"+i).setHidden(true);
              }else{
                  Ext.getCmp("savebtn"+i).setHidden(false);  
                  Ext.getCmp("deployelbtn"+i).setHidden(false);  
                  Ext.getCmp("rejectbtn"+i).setHidden(false);  
                  Ext.getCmp("approvalbtn"+i).setHidden(true);  
                  Ext.getCmp("toApprovalbtn"+i).setHidden(true);  
                  Ext.getCmp("revocationbtn"+i).setHidden(true);
              }
          }
      }
    },
    /**
     * 显示过程信息
     * quater 季度号  1,2,3,4
     *  
     */
    displayProcessInfo:function(quarter){
        var processPanel = Ext.getCmp("processPanel");
        if(processPanel)
            processPanel.removeAll();
        var map = new HashMap();
        map.put("opt","2");
        if(wp_yearPlanTrace.planRecord)
            map.put("planId",wp_yearPlanTrace.planRecord.data.P1700);
        else
            return;
        map.put("quarter",quarter);
        var labels = [];
        Rpc({functionId:'WP00002005',async:false,success:function(form){
            var result = Ext.decode(form.responseText);
            var infos = result.infos;
            for(var i in infos){
                var label = {
                        xtype:'label',
                        width:'100%',
                        text:infos[i],
                        margin:'10 0 0 20'
                       };
                labels.push(label);
            }
    }},map);
        if(labels.length>0)
            processPanel.add(labels);
    },
    /** 负责人撤回 */
    revocation:function(quarter){
       var planId = wp_yearPlanTrace.planRecord.data.P1700;
       Ext.Msg.confirm("提示信息","是否撤回第"+wp_yearPlanTrace.convertQuarter(quarter)+"季度总结？",function(flag){
           if(flag=="no")
               return;
           var map =new HashMap();
           map.put("opt","3");
           map.put("planId",planId);
           map.put("quarter",quarter);
           Rpc({functionId:'WP00002006',async:false,success:function(form){
               var result = Ext.decode(form.responseText);
               if(result.flag=="true"){
            	   wp_yearPlanTrace.mapState[quarter] = "01";
                   wp_yearPlanTrace.refreshStatus(wp_yearPlanTrace.planRecord,wp_yearPlanTrace.mapState);
                   wp_yearPlanTrace.displayProcessInfo(quarter);
               }
       }},map);
           
           
       })
    },
    /** 退回 */
    reject:function(quarter){
        var planId = wp_yearPlanTrace.planRecord.data.P1700;
        Ext.Msg.confirm("提示信息","是否退回第"+wp_yearPlanTrace.convertQuarter(quarter)+"季度总结？",function(flag){
            if(flag=="no")
                return;
            var map =new HashMap();
            Ext.widget("window",{
                id:'adviceWin',
                width: 400,
                height:200,
                layout:'fit',
                bodyPadding: 5,
                title:'审批意见',
                buttonAlign:'center',
                fbar:[{
                    text:"确定",
                    handler:function(){
                        var advice = Ext.getCmp('adviceContent').value;
                        if(!advice || advice.trim().length==0){
                            Ext.Msg.alert("提示信息","请填写审批意见！");
                            return;
                        }
                        map.put("opt","4");
                        map.put("planId",planId);
                        map.put("quarter",quarter);
                        map.put("advice",advice);
                        Rpc({functionId:'WP00002006',async:false,success:function(form){
                            var result = Ext.decode(form.responseText);
                            var msg = "";
                            if(result.msg && result.msg.length>0){
                                Ext.getCmp("adviceWin").close();
                                msg = result.msg;
                                wp_yearPlanTrace.mapState[quarter] = "07";
                                wp_yearPlanTrace.mapState["isSelf_"+quarter]="0";
                                wp_yearPlanTrace.refreshStatus(wp_yearPlanTrace.planRecord,wp_yearPlanTrace.mapState);
                                wp_yearPlanTrace.displayProcessInfo(quarter);
                            }else{
                                msg = result.message;
                            }
                        }},map);
                    }
                },{
                    text:"取消",
                    handler:function(){
                        Ext.getCmp("adviceWin").close();
                    }
                }],
                items:[{
                    id:'adviceContent',
                    emptyText:"请输入对总结内容的意见",
                    xtype:'textareafield',
                    grow: true,
                    allowBlank:false,
                    name: 'message'
                }]
            }).show();
        })
    },
    /** 批准 */
    approval:function(quarter){
        var planId = wp_yearPlanTrace.planRecord.data.P1700;
        Ext.Msg.confirm("提示信息", "是否批准第"+wp_yearPlanTrace.convertQuarter(quarter)+"季度完成情况？", function(id){
            if(id=='no')
                return;
            var map = new HashMap();
            map.put("opt","5");//保存
            map.put("planId",planId);
            map.put("quarter",quarter);
            Rpc({functionId:'WP00002006',async:false,success:function(form){
                var result = Ext.decode(form.responseText);
                var msg = "";
                if(result.msg){
                    msg = result.msg;
                    wp_yearPlanTrace.mapState[quarter] = "03";
                    wp_yearPlanTrace.refreshStatus(wp_yearPlanTrace.planRecord,wp_yearPlanTrace.mapState);
                    wp_yearPlanTrace.displayProcessInfo(quarter);
                }else
                    msg = result.message;
                Ext.Msg.alert("提示信息",msg);   
            }},map); 
        });
    },
    /** panel收缩展开 */
    clickTitleIcon : function(id) {
        var panel = Ext.getCmp(id);
        panel.toggleCollapse();
    },
    /** 设置按钮的图标 */
    setIcon : function(imgId,state) {
        var img = Ext.getDom(imgId);
        var src = '/images/new_module/expand.png';
        if (state == 'up') {
            src = '/images/new_module/collapse.png';

        }
        img.src = src;
    },
    /** 方案名字超出范围省略号显示 **/
 // 把字符串转化成后面带省略号形式
    convertStr:function(str){
        var reStr = str;
        
        var maxwidth = 54;//字母排列的话最多占的个数
        var index = 0;
        var useWidth = 0;
        for(i=0; i<str.length; i++){
             if(wp_yearPlanTrace.checknum(str.charAt(i))) {//字母或数字
                useWidth += 1;
             } else {//汉字
                useWidth += 2;//每个汉字占宽度约为字母的2倍
             }
             if(useWidth >= maxwidth && index == 0){
                index = i;
             }
        } 
        //checknum
        if(useWidth > maxwidth){
            reStr = str.substring(0, index-1);
            reStr += '...';
        }
        return reStr;
    },
    // 判断是否是字母或数字
    checknum : function(value) {
        var flg = false;
        var Regx = /^[A-Za-z0-9]*$/;
        if (Regx.test(value)) {
            flg =  true;
        }
        return flg;
    },
    //需要填写的季度总结
    needQuarter:function(startTime,endTime){
        var tabPanel = Ext.getCmp('tabPanel');
        if(tabPanel)
           wp_yearPlanTrace.showAllTab();
        var currentYear = new Date().getYear();//得当当前年份
        if(!startTime){
            if(!endTime){
                return
            }else{
                endTime = endTime.replace(/\-/g,'/');
                var endYear = new Date(endTime).getYear();//得到填写的年份
                var endMoth = "";
                if(endYear>currentYear)
                	endMoth = 12;
                else
                	endMoth = new Date(endTime).getMonth()+1;//1-12
                var endQ =  Math.ceil(endMoth/3);
                for(var j=4;j>endQ;j--){
                    tabPanel.child('#achievement'+j).tab.hide();
                 }
            }
        }else{
            startTime = startTime.replace(/\-/g,'/');
            var startYear = new Date(startTime).getYear();//得到填写的年份
            var startMoth = "";
            if(startYear<currentYear)
            	startMoth = 1;
            else
            	startMoth = new Date(startTime).getMonth()+1//1-12;
            var startQ = Math.ceil(startMoth/3);
            if(!endTime){
                for(var i=1;i<startQ;i++){
                    tabPanel.child('#achievement'+i).tab.hide();
                 }  
            }else{
                endTime = endTime.replace(/\-/g,'/');
                var endYear = new Date(endTime).getYear();//得到填写的年份
                var endMoth = "";
                if(endYear>currentYear)
                	endMoth = 12;
                else
                	endMoth = new Date(endTime).getMonth()+1;//1-12
                if(endMoth<startMoth)
                    return;
               var endQ =  Math.ceil(endMoth/3);
               for(var i=1;i<startQ;i++){
                   tabPanel.child('#achievement'+i).tab.hide();
               }
               for(var j=4;j>endQ;j--){
                   tabPanel.child('#achievement'+j).tab.hide();
                }
            }
        }
        
    },
    //显示全部tab
    showAllTab:function(){
        var tabPanel = Ext.getCmp('tabPanel');
        if(!tabPanel)
            return;
        for(var i=1;i<5;i++){
            var tab = tabPanel.child('#achievement'+i).tab;
            if(tab.hidden)
                tab.show();
        }
    },
    /*将数字1234转换成一二三四并返回*/
    convertQuarter:function(quarter){
        var quarterStr = "";
        switch (quarter) {
        case 1:
            return "一";
            break;
        case 2:
            return "二";
            break;
        case 3:
            return "三";
            break;
        case 4:
            return "四";
            break;
        default:
            return "";
            break;
        }
    }
    
})