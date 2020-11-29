 //加载自定义类
//Ext.Loader.loadScript({url:rootPath+'/module/template/templatecard/FieldSet.js'});
//Ext.Loader.loadScript({url:rootPath+'/module/template/templatecard/CardView.js'});
//Ext.Loader.loadScript({url:'/js/wz_tooltip.js'});
Ext.Loader.loadScript({url:rootPath+'/components/codeSelector/codeSelector.js'});
Ext.Loader.loadScript({url:rootPath+'/components/dateTimeSelector/dateTimeSelector.js'});
Ext.Loader.loadScript({url:rootPath+'/general/sys/hjaxmanage.js'}); 
/*
Ext.Loader.loadScript({ url: script1path,scope:this,onLoad:function(){  
    Ext.Loader.loadScript({ url:script2path,scope:this});  
}});
*/
var ctrl = null;
var websock = true;
var b;
var ArmHandle;	//加密锁句柄
var FileID,ReadLength,FileOffset,Handle,Index,DongleInfoNum,Offset;  
/**
 * 模板卡片类 TemplateCard.js
 * */
Ext.define('TemplateCardUL.TemplateCard',{
	templPropety:'',//模板的所有属性 模板号，实例号等
	mainPanel:'',//主界面
	personListGrid:'', //左侧人员列表表格
	tabList:'',//页签对象列表
	curTab:'',//当前操作的页签 默认是第一个
	object_id:"",//当前显示人员编号 单位、岗位
	cur_task_id:"0",//当前显示人员的taskid
	cur_ins_id:"0",//当前显示人员的insid
	prefix:"templcard",//模板卡片前缀
	changedA0101:"",//需要更改的人员姓名
	personListCurRecord:"",//左侧人员列表当前定位的record
	activeElement:"",//当前操作的模板元素
	fieldSet:"",
	recordSet:"",
    signXml:"",//签章用xml
    documentrecordID:"",//记录签章对应的唯一id（金格科技）
    signobjarr:[],//BJCA签章用
    signatureMap:new HashMap(),//金格签章判断一个人是否重复签章用
    callBackFunc:null,//回调函数
    isHaveChange:false,//是否需要弹出保存成功对话框
    opinion_field:"",//liuyz bug31563
    operationType:'',
    Signature:null,//金格html5签章对象
    documentid:"",
    signatureInit:false,
    signature_fldid:new HashMap(),
	constructor:function(config) {	
		templateCard_me=this;
		templateCard_me.group_arr = null;
		if (config.callBackFunc){
          templateCard_me.callBackFunc= config.callBackFunc;
        }
		this.templPropety=config.templPropety;
        this.cur_task_id= config.templPropety.task_id;
		this.init(config);
		//初始化签章锁
		if(templateCard_me.signature_usb)
			initSignObject();
	},
	   /**
     * 请求后台,加载页面
     * */
	init:function(config) {
		var map = new HashMap();
        initPublicParam(map,this.templPropety);
        var objectid="";
        if (config.cur_object_id!=""){
            objectid =config.cur_object_id;
        }
        else if(typeof(templateList_me) != "undefined"&&templateList_me.current_object_id&&trim(templateList_me.current_object_id).length>0) //默认显示列表界面选中的对象
        {
            objectid =templateList_me.current_object_id;
        }
        map.put("cur_object_id",objectid);var isarchive = getTemplPropetyOthParam("isarchive");
	    var functionid = "MB00004001";
	    if(isarchive=='0'){
	    	functionid = "MB00008003";
	    }   
	    Rpc({functionId:functionid,async:false,success:this.initOK,scope:this},map);
	},
	
	/**
     * 请求后台成功后加载页面
     * */
	initOK:function(form,action){
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
			Ext.showAlert(result.message);
			this.mainPanel = Ext.create('Ext.panel.Panel', {
                id:this.prefix+"_mainCard",
                border : 0,
                layout:"border",  
                items: []           
            });
			return;
		}else{
			var pageList =result.pageList;
			if (pageList.length<1){
				Ext.showAlert("您无权查看和使用模板!请检查设置 ①此页是否显示 ②此页是否适用手机 ③子集&指标权限!",function(){templateTool_me.returnBack(true);}); //bug 44120 
				this.mainPanel = Ext.create('Ext.panel.Panel', {
	                id:this.prefix+"_mainCard",
	                border : 0,
	                layout:"border",  
	                items: []           
	            });
				
				return;
			}
			else {
				this.initTemplatePage(result);
			}
		}
	},
	/**
	 * 快速查询回调函数
	 * @param {} config
	 */
	loadStore:function(config){
        var condsql=config.condsql;
        if(Ext.isEmpty(condsql) || 'undefined' == condsql){
     	      condsql="";
        }
        //转码
        condsql = getEncodeStr(condsql);
        //other_param参数扩展方法
        setTemplPropetyOthParam('search_sql', condsql);
        var map = new HashMap();
        initPublicParam(map,templateCard_me.templPropety);
        var store = Ext.data.StoreManager.lookup('templatecard_dataStore');
        Rpc({functionId:'MB00004001',async:false,success:function(){
        	store.loadPage(1);//bug 39992 快速搜索后没有跳转到第一页，不显示人员
            //29332 linbz 查询完后定位到第一个人，先置空该参数
            templateCard_me.object_id="";
        },scope:this},map);
    },
    
	/**
     * 加载页面
     * */
	initTemplatePage:function(result){
		//签章厂家标识
		this.signatureType = Ext.decode(result.signatureType);
		this.mServerUrl = getDecodeStr(result.mServerUrl);
		this.imgUrlList = getDecodeStr(result.imgUrlList);
		templateCard_me.fieldsArray=result.fieldsArray;
        templateCard_me.fieldsMap=result.fieldsMap;
        templateMain_me.deprecate=result.deprecate;
        templateCard_me.subModuleId=result.subModuleId;
		templateCard_me.nodePriv = result.nodePriv;//是否走节点权限控制
		templateCard_me.opinion_field=result.opinion_field;//liuyz bug31563
		templateCard_me.hidePersonGrid=result.hidePersonGrid;//农大 一个人不显示右侧人员列表
		templateCard_me.nodeId=result.node_id;//记录当前节点ID
		templateCard_me.currentUser = result.currentUser;//当前登陆用户
		templateCard_me.signature_usb = result.signature_usb;
		templateCard_me.operationType=result.operationType;
		templateCard_me.serverUrlForHtml5 = result.serverUrlForHtml5;
		templateCard_me.keysn = result.keysn;
		templateCard_me.currentUsername = result.currentUsername;
		//左侧人员列表
		var personListObj = Ext.decode(result.tableConfig);	
		//配置多选框
		personListObj.beforeBuildComp=function(config){
			config.tableConfig.scrollable='y',//只显示垂直滚动条
			config.tableConfig.selModel={
					selType:'checkboxmodel',
					lockableScope:'both',
					checkOnly: true,     //只能通过checkbox选择
					enableAllSelect:true};
			config.tableConfig.viewConfig.getRowClass=function(record,rowIndex,rowParams,store){//改变行颜色
				if(rowIndex%2==0)
					return 'x-grid-row-selected-white';
				else
					return 'x-grid-row-selected-grey';
    			};
			};
			personListObj.simpleModel=true;//简单翻页 lis 20160722
			this.personListGrid = new BuildTableObj(personListObj);
			var columns = this.personListGrid.tablePanel.columns;
			columns[0].menuDisabled = true;
			this.addGridListens(this.personListGrid);
			//添加左侧人员栏切页自动保存
			var pagingtool = Ext.getCmp('templatecard_pagingtool');
			pagingtool.on('beforechange',function(a, pageData, eOpts){
				templateTool_me.save('true','false');
			},templateCard_me);
		//}
		
		if (result.objectId!=null){
			this.object_id =result.objectId;
		}
		
		//页签对象
		var pageList =result.pageList;
		this.tabList = new Array();
		for (var i=0;i<pageList.length;i++){
			var obj =pageList[i];
			var tabObj = new Object();
			tabObj.id ="tab_"+obj.pageId;
			tabObj.title =obj.title;
			tabObj.html="<div id='tabdiv_"+tabObj.id+"'></div>"	;
			tabObj.autoScroll=true;
			tabObj.isLoadPageHtml=false;//是否已加载当前页html，每张模板页只加载一次
			tabObj.isLoadData=false;//是否已加载数据标识，默认未加载
			this.tabList.push(tabObj);
			if (i==0) this.curTab = tabObj;
		}
		//加载所有单元格指标fieldset
		var fieldList = result.fieldList;
		//展现html及加载指标模型 每个页签只加载一次。
		var idlist = new Array();
		if (fieldList!=null){
			initFieldSet(fieldList);
		}
		
		//渲染卡片页面
		this.initForm();
		//enter转tab
		document.onkeydown=function(evt){
			var isie = (document.all) ? true : false;
			var key;
			var srcobj;
			if (isie) {
				key = event.keyCode;
				srcobj=event.srcElement;
			}
			else {
				key = evt.which;
				srcobj=evt.target;
			}
			var srcobjid=srcobj.id;
			if(key==13) {
				if(srcobj.type!='textarea' && srcobj.type!='button' && srcobj.type!='submit' &&srcobj.type!='reset' && srcobj.type!='' && srcobj.id.indexOf('fld')!=-1)
				{
					if(typeof(codeSelector)!="undefined"&&typeof(codeSelector.selector)=="undefined")
					{
						var fn=function(){
							getNextElement(srcobjid);
							var treepanelitem = Ext.ComponentQuery.query('treepanel');
							if(treepanelitem.length>0){
								Ext.each(treepanelitem,function(e){
									if(e.style.indexOf('z-index:100000000')!=-1)
									{
										e.close();
									}
								});
							}
						};
						//按下enter键后200毫秒触发跳到下一个方法。否则下一个元素是下拉框会触发下拉框的keyup事件。导致错误。加延时防止冲突。
						setTimeout(fn,200);
				 	}
				 	 //屏蔽enter键，否则有图像时会定位到图片（原因未知）
				 	return false;
				}
				
			}else if(key==8){//屏蔽backspace键
				var focusedElementtype = document.activeElement.type;
				if(focusedElementtype=='text'||focusedElementtype=='textarea'||focusedElementtype=='password'){
					if(document.activeElement.readOnly==true)
						return false;
					else
						return true;
				}else{
					return false;
				}
			}else if(key==9){
			//liuyz 使用tab健切换到下一个单元格时，上一个单元格的代码应不显示
				var treepanelitem = Ext.ComponentQuery.query('treepanel');
				if(treepanelitem.length>0){
					Ext.each(treepanelitem,function(e){
						if(e.style.indexOf('z-index:100000000')!=-1)
						{
							e.close();
						}
					});
				}
			   var treepanelitem = Ext.ComponentQuery.query('datetimepicker');
			   if(treepanelitem){
					if(isie)
					{
						//手动触发mousedown事件使时间弹出框消失
						document.getElementById(srcobjid).fireEvent('onmousedown');
					}
					else
					{
						//手动触发mousedown事件使时间弹出框消失
						var fireOnThis = document.getElementById(srcobjid);
		                var evObj = document.createEvent('MouseEvents');
		                evObj.initEvent('mousedown', true, false);
		                fireOnThis.dispatchEvent(evObj);
					}
			   }
			}
		}
	},
	/**
	 * 渲染页面布局，左侧人员列表 页签 卡片区域
	 * */
	initForm:function(config){
		//创建左侧人员列表面板
        var pnWest=null;
        var leftPanelWidth = 186;
        if(templateCard_me.operationType == 8|| templateCard_me.operationType == 9){
        	leftPanelWidth = 226;
        }
       // if (this.templPropety.card_view_type=="0"){
            pnWest=Ext.create('Ext.panel.Panel',{ 
                id:this.prefix+"_pnWest",   
                border : 0,
                width:leftPanelWidth, 
                height:'auto',  
                layout: 'fit',
                split:false,//是否可拖动
                items:[this.personListGrid.getMainPanel()],
                region:'west',
                hidden:templateCard_me.hidePersonGrid&&templateCard_me.templPropety.task_id!="0"//一个人不显示右侧人员列表
            }); 
       // }
	
        //删掉此处，别的模块调用模板的时候报错,改为在bodyStyle中加入样式  bug 31254 2017-09-04
        /*var css_template_tab="#"+this.prefix+"_tabpanel-body {border-width: 0px 1px 1px 1px;}";
 		if(!!!Ext.util.CSS.getRule(css_template_tab))//如果css_template_tab不存在,
 			Ext.util.CSS.createStyleSheet(css_template_tab,"tab_css");*/
            if(this.tabList.length>7){
          	   //Ext.util.CSS.createStyleSheet(".x-tab-default-left {border-radius: 0px 0px 0px 0px;margin-top:-1px !important;padding: 3px 9px 3px 9px;border-width: 1px 0 1px 1px ;border-style: solid;background-image: none;background-color: #f9f9f9;}");   
          	   Ext.util.CSS.createStyleSheet(".x-tab-bar-default-left .x-box-scroller-top{background-image: url(../../../ext/ext6/resources/images/tab-bar/default-scroll-top-left-w.gif);}"); //左侧栏按钮样式修改
          	   Ext.util.CSS.createStyleSheet(".x-tab-bar-default-left .x-box-scroller-bottom{background-image: url(../../../ext/ext6/resources/images/tab-bar/default-scroll-bottom-left-w.gif);}");
             }
		//创建右侧页签对象
		var tabPanal = Ext.widget('tabpanel', {
			id:this.prefix+"_tabpanel",
			scope:this,
			removePanelHeader:true,
			minTabWidth:86,//liuyz 优化增加只读和编辑图标 如果修改此值需要一同修改TemplateCardTrans.java中134、138行min-width值设置，否则页签会出现分离现象  27514 火狐一部分被盖住 有可能是添加图片导致 增加16像素 changxy 
			animScroll:true,	
		    height:600,
		    activeTab : 0, // 默认激活第1个tab页
			style: {
	            width: '10%',
	            top:'0'
	        },
	        tabBar:this.tabList.length>7?{
	        	defaults:{
	        		width:125,
	        		height:40
	        	}
	        	}:{
	        	height: 25,
	        	defaults:{
	        		height:23
	        	}	
	        },
	        bodyStyle: this.tabList.length>7?'padding:1px;TOP:1px;margin:0px 0px 2px;border-width: 1px 0px 1px 0px;':'padding:0px;TOP:0px;margin:0px 0px 2px;border-width: 0px 1px 1px 1px;',
			plain:true,
			items:	this.tabList,    
			listeners:{ 
                tabchange:function(tabPanel, newCard, oldCard){ 
               		var fn=function(){
                		templateTool_me.save('unKnow','false');//切换页签之前先保存
                		var tabObj= templateCard_me.getTabObj(newCard.id);					
						templateCard_me.curTab= tabObj;
						if (tabObj.isLoadData){//已加载过，不需要重复加载
							return;
						}
						templateCard_me.loadPageData();
                	};
                	setTimeout(fn,200);//增加延迟，否则大文本blur事件没有执行完，不能改变是否更改状态，不提示保存成功。	
                },
                afterrender: function() {  
                /*
                	if (templateCard_me.tabList.length>0){
                	    templateCard_me.curTab=templateCard_me.tabList[0];
            		}
            		*/
            		if (templateCard_me.personListGrid){
                       if (templateCard_me.personListGrid.tablePanel.getStore().getCount()>0){
                           templateCard_me.personListCurRecord = templateCard_me.personListGrid.tablePanel.getStore().getAt(0);
                       }
                    }
                }  
            }
		});
		tabPanal.setActiveTab(0);
		if(this.tabList.length>7){//
			tabPanal.setTabPosition('left');//设置页签显示位置
			tabPanal.setTabRotation(0);
		}
		
        //创建右侧卡片面板
        var pnCenter=new Ext.Panel({  
            id:this.prefix+"_pnCenter",
            border : 0,
            region:'center',
            bodyPadding: '0 0 0 5',
            layout: 'fit',
            items:[tabPanal]
        });     
        //创建整个卡片面板
        if (this.templPropety.card_view_type=="0"){
            this.mainPanel = Ext.create('Ext.panel.Panel', {
                id:this.prefix+"_mainCard",
                border : 0,
                layout:"border",  
                items: [
                        pnWest,
                        pnCenter
                ]           
            });
        }
        else {
            this.mainPanel = Ext.create('Ext.panel.Panel', {
                id:this.prefix+"_mainCard",
                border : 0,
                layout:"border",  
                items: [                        
                        pnCenter
                ]           
            }); 
        }
        
        //默认选中第一行
         new Ext.util.DelayedTask(function(){
    		templateMain_me.selecRowChangeCss(templateCard_me.personListGrid.tablePanel,0);
		}).delay(300);
		
        //加载完后回调方法
	    if(templateCard_me.callBackFunc){
            Ext.callback(eval(templateCard_me.callBackFunc),null,[]);
        }	
		
	//	this.mainPanel.render('main_bottom');
	//	var bottom_1 =Ext.getCmp("main_bottom");
	//	bottom_1.add(this.mainPanel);
	},
	
	   /**
     * 左侧人员列表全选、选中、撤选、行单击事件
     * */
    addGridListens:function(grid){
		 //点击列头事件
		 grid.tablePanel.on('headerclick',function(ct, column, e, t, eOpts){
	        	//第0列
     		    var propety =  this.templPropety;
	        	if(column.getIndex() == 0){
	        		var task = new Ext.util.DelayedTask(function(){
	       			    var sel = grid.tablePanel.getSelectionModel();
	        			var records = [];
	        			grid.tablePanel.getStore().getData().each(function(record,index){
	        				records.push(record.data);
	        			})
	        		    var submitflag = "0";
	        		    if(sel.getCount() == records.length)
	        		    	submitflag = "1";
	        		    var map = new HashMap();
	        		    //选中
	        		    initPublicParam(map,propety);
	        		    //28838
	        		    map.put("doSelectAll","0");	        		    
	        		    map.put("submitflag",submitflag);
//	        		    map.put("selectRecords",records);
	        		    Rpc({functionId:'MB00003005',async:false,success:function(){}},map);
	        		});
	        		task.delay(1);
	        	}
	        },templateCard_me);
		 
		//复选框选中状态   
        grid.tablePanel.on('cellclick',function( view, td, cellIndex, record, tr, rowIndex, e, eOpts ){
            //第0列
            if(cellIndex==0){
                var objectid=record.data.objectid_e;
                //var insid=record.data.ins_id;
                var realtask_id=record.data.realtask_id_e;  
                if(!!!realtask_id)
                	realtask_id = "0";
                var isSelect = grid.tablePanel.getSelectionModel().isSelected(record);
                var submitflag = "0";
                if(isSelect){
                    submitflag = "1";
                }
                var map = new HashMap();
                initPublicParam(map,this.templPropety);
                //28838
                map.put("doSelectAll","1");    
                map.put("objectid",objectid);
                map.put("task_id",realtask_id);
                //map.put("ins_id",insid);
                map.put("submitflag", submitflag);
                Rpc({functionId:'MB00003005',async:false,success:function(){}},map);
            }
        },templateCard_me);
        //复选框取消选中状态
        grid.tablePanel.on('deselect',function(Datathis,record, index, eOpts){
/*        		var objectid=record.data.objectid;
        		var insid=record.data.ins_id;
        		var realtask_id=record.data.realtask_id_e;  
        		var map = new HashMap();
        		map.put("doSelectAll",isSelectAll);
        		map.put("infor_type",templateCard_me.templPropety.infor_type);
        		map.put("objectid",objectid);
        		map.put("tabid",templateCard_me.templPropety.tab_id);
        		map.put("task_id",realtask_id);
        		map.put("ins_id",insid);
        		map.put("submitflag","0");
        		Rpc({functionId:'MB00003005',async:false,success:function(){}},map);*/
        });
        //复选框初始选中
        grid.tablePanel.getStore().on('load',function(store,records){
            var selectModel = grid.tablePanel.getSelectionModel();
            var arrRecords=new Array();
            //28710 当表单内没有选人时，加载空的表单，如果有则不再重复加载
            var bool = false;
            if (templateCard_me.object_id!=""){
            	bool = true;//bug 43979 撤销人员后再点撤销无效，ckeditor报错。
            }else {
	            if(records.length > 0){
	            	templateCard_me.personListCurRecord = records[0];
	                templateCard_me.object_id = records[0].get('objectid_e');
	                templateCard_me.cur_task_id = records[0].get('realtask_id_e');
	                if (templateCard_me.cur_task_id==""){
	                   templateCard_me.cur_task_id="0";
	                }
	                //29332 linbz 查询完后定位到第一个人
	                templateCard_me.cur_ins_id = records[0].get('ins_id');
	                if (templateCard_me.cur_ins_id == ""||templateCard_me.cur_ins_id==undefined){
	                	templateCard_me.cur_ins_id = "0";
                    }
                    //28710 表单有人的情况
                    bool = true;
	                templateCard_me.switchPerson(templateCard_me.object_id, templateCard_me.cur_ins_id,templateCard_me.cur_task_id);
	                //end
	            }
	            //当查不到记录时，需要将cur_task_id置为0，否则可能cur_task_id中存在多个task_id，导致后台报错。
	            else
	            {
	            	templateCard_me.cur_task_id="0";
	            	templateCard_me.cur_ins_id = "0";
	            }
            }
            //28710 当表单内没有选人时，加载空的表单
            if(!bool){
                //30230查询后没有符合条件的人员时清空当前数据集recordSet
            	templateCard_me.recordSet = "";
                templateCard_me.loadPageData();
                //如果没有数据 将原来附件指标部分数据清空
                for(var i=0;i<templateCard_me.recordSet.fieldSet.fields.length;i++){
            		var field = templateCard_me.recordSet.fieldSet.fields[i];
            		var flag = field.flag;
            		var uniqueId = field.uniqueId;
            		if(flag=='F'){//证明有附件指标
            			if(Ext.getCmp("attachmentgrid_"+uniqueId))
            				Ext.getCmp("attachmentgrid_"+uniqueId).destroy();
            		}
            	}
            }
            
            //设置选中状态。
            for(var i=0;i<records.length;i++){
               if(records[i].get("submitflag2")=='1'){
                  arrRecords.push(records[i]);
               }
            }
            selectModel.select(arrRecords,false,true);
            //暂时解决垂直滚动条问题（复选框无法选择）
            var height = Ext.getCmp("templmain_body_panel").getHeight();
			var count = templateCard_me.personListGrid.tablePanel.getStore().getCount();
			if((count+2)*30>height){
				templateCard_me.isChangeWidth=true;
				Ext.getCmp("templcard_pnWest").setWidth(203);
			}
			//29235 linbz 增加选人控件不显示的人员参数
			if(templateCard_me.templPropety.infor_type=='1'){
				var map = new HashMap();
				setTemplPropetyOthParam('deprecate_flag', "1");
	            initPublicParam(map,templateCard_me.templPropety);
	            Rpc({functionId:'MB00004001',async:false,success:function(form){
	                var result = Ext.decode(form.responseText);
	            	templateMain_me.deprecate=result.deprecate;
	            	setTemplPropetyOthParam('deprecate_flag', ""); 
	            },scope:this},map);
			}
        },templateCard_me);
        
        //行单击事件
        grid.tablePanel.on('itemclick',function(obj,record,item,index){
        	templateTool_me.save('true','false');//情况之前要保存当前信息 lis 20160714
        	////更新右侧模板数据，先执行select事件，后执行itemclick事件
        	templateCard_me.personListCurRecord=record;
        	var objectid = record.data.objectid_e;
        	var taskid = record.data.realtask_id_e;
        	var insid = record.data.ins_id;
        	templateCard_me.switchPerson(objectid,insid,taskid);
        },grid.tablePanel);
       
        //行颜色改变事件
        templateMain_me.rowCssChangeEvent(grid.tablePanel,templateCard_me,true);
    },
	
	// 【人员列表列】渲染
	renderPersonColumn:function(value, metaData, Record){
		var objectid = Record.data.objectid_e;
		var taskid = Record.data.realtask_id_e;
		var insid = Record.data.ins_id;
		var to_id = Record.get('to_id');
		if(value==null)
			value = ""; 
		if(to_id!=null&&Record.data.objectid_noencrypt!=null&&to_id==Record.data.objectid_noencrypt&&value.length>0)
			value=value+" (目标机构)";
		var html = "<a href=javascript:void(0)>" + value + "</a>"; 
		return html;
	},
	 
	// 【组号】渲染
	renderGroupColumn:function(value, metaData, record, rowIndex, colIndex, store, view){
		var _to_id="";
		var group_no=0;
		if(rowIndex == 0){
			templateCard_me.group_arr = new HashMap();
			store.each(function(record,index){
				var key_value = record.data.objectid_e;
				var to_id = record.get('to_id');
                if(to_id == "" || to_id==null)
                { 
                	templateCard_me.group_arr.put(key_value,"");
                }else{
	                if(_to_id != to_id)
	                {
	                	group_no++;
	                	_to_id = to_id;
	                }
	            	templateCard_me.group_arr.put(key_value,group_no);
                }
                
			})
		}
		return templateCard_me.group_arr.get(record.data.objectid_e);
	},
	
	  /**
     * 点击人员列表，切换人员
     * */
	switchPerson:function(objectid,insid,taskid){
		//清除上一个人的数据
		this.clearRecordSet();
		this.object_id =objectid;
		
		if (!!!taskid || taskid==""){
		  taskid="0";
		}
		if (!!!insid || insid==""){
			insid="0";
		}
		templateCard_me.cur_task_id =taskid;
		templateCard_me.cur_ins_id = insid;
		if(this.tabList.length>7){//放置左侧时，修改第一个页签样式
			var style=Ext.query("a[class$=x-tab-default-left]",true,templateCard_me.mainPanel.el.dom)[0].style;
			if(style.cssText.indexOf("margin-top")==-1&&templateCard_me.getCurrPageId()==0){
				style.cssText=style.cssText+";margin-top:1px !important";
			}
		}
			
		for (var i=0;i<this.tabList.length;i++){
			var tabObj =this.tabList[i];
			//全部页签置为未加载数据 
			tabObj.isLoadData=false;	
			//重新加载当前显示的页签
			if (this.curTab.id ==tabObj.id ){
				templateCard_me.loadPageData();
			}
		}
	},
	/**
	 *刷新人员 
	 *record:需要刷新的人记录
	 * */
	 refreshPerson: function(record){
	    if (record){
	        var objectid = record.data.objectid_e;
	        var taskid = record.data.realtask_id_e;
	        var insid = record.data.ins_id;
	        templateCard_me.switchPerson(objectid,insid,taskid);
	    }
	    else {
	       templateCard_me.switchPerson("","0","0");
	    }
	},
	
    /**
     *刷新当前人员  
     * */
     refreshCurrPerson: function(){
        templateCard_me.refreshPerson(templateCard_me.personListCurRecord);
    },
	/**
	 * 当前类主面板，供其他类调用
	 * */
	getMainPanel:function(){
		return this.mainPanel;
	},
	/**
	 * 加载页签的html
	 * */
	loadPageData:function(){
        var pageId= templateCard_me.getCurrPageId();
		var map = new HashMap();
        initPublicParam(map,this.templPropety);
		map.put("page_id",pageId+"");		
		map.put("object_id",this.object_id);
		var needLoadPageHtml="true";
		if (this.curTab.isLoadPageHtml){
			needLoadPageHtml="false";
		}
		//是否需要加载数据
		var needLoadFieldValue="false";
		if (templateCard_me.recordSet==""){
			needLoadFieldValue="true";
		}
		map.put("needLoadPageHtml",needLoadPageHtml);
		map.put("needLoadFieldValue",needLoadFieldValue);
		map.put("cur_task_id",templateCard_me.cur_task_id);
		map.put("firstPageNo",templateMain_me.templPropety.firstPageNo+'')
		var isarchive = getTemplPropetyOthParam("isarchive");
	    var functionid = "MB00004002";
	    if(isarchive=='0'){
	    	functionid = "MB00008004";
	    }
		Rpc({functionId:functionid,async:false,success:function(form){
			var result = Ext.decode(form.responseText);
			var pageId=templateCard_me.getCurrPageId();
			var htmlView = result.htmlView;
			//var tabObj= templateCard_me.getTabObj(templateCard_me.curTab.id);					
			//展现html 每个页签只加载一次。
			if (!this.curTab.isLoadPageHtml){
				var div =Ext.getDom('tabdiv_'+this.curTab.id);
				if (div !=null){
					div.innerHTML=result.htmlview;
					this.curTab.isLoadPageHtml=true;
					setAllElementStyle(templateCard_me.getCurFieldSet(),pageId);
				}
				
				//绑定代码选择控件
				var idlist = new Array();
				var fieldSet = this.getCurFieldSet().fields;
				for (var i=0;i<fieldSet.length;i++){
			        var field=fieldSet[i]; 
			        if (pageId!=field.pageId) 
			          continue;
			        if(field.fldType=="M"&&field.inputType==1){}
		        	else{
		        		if (field.rwPriv!="2"){//无权限 不能查看
				             continue;                   
				        }
				        if(field.chgState=='1'){
			        		 continue;             
				        }
		        	}
		        	
			        var uniqueId=field.uniqueId;
			        var element =Ext.getDom(uniqueId);//根据唯一值取得页面对应元素
			        if (element !=null){
						if(field.codeSetId!='0'&&field.codeSetId!=''){
							idlist.push(uniqueId);
						}
			        }
			        if(field.fldType=="M"&&field.inputType==1&&!field.imppeople)
			    	{
			    			var config = {};
							config.toolbar = [
									['Source','-','Undo','Redo'],
									['Find','Replace','-','SelectAll','RemoveFormat'],
									['Bold','Italic','Underline'],
									'/',
									['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
									['SpecialChar','Subscript','Superscript'],
									['Style','FontFormat','FontName','FontSize'],
									['TextColor','BGColor','-','Image'],
									['lineheight']];
							var editor = CKEDITOR.replace(field.uniqueId);
			    			CKFinder.setupCKEditor(editor, "/ckfinder/");
							if(field.chgState=='1'){
								editor.config.readOnly=true;
							}
							var width=document.getElementById(field.uniqueId).offsetWidth;
							var titleLength=width/120;
							if(titleLength>=4)
							{
								titleLength=4;
							}
							editor.config.toolbar=config.toolbar;
							editor.config.height=document.getElementById(field.uniqueId).offsetHeight-Math.ceil(4/titleLength)*2*34;
							editor.config.resize_maxHeight=document.getElementById(field.uniqueId).offsetHeight-Math.ceil(4/titleLength)*2*34;
							editor.config.allowedContent=true; 
							editor.config.width='100%';
							editor.config.enterMode = CKEDITOR.ENTER_BR; //可选：CKEDITOR.ENTER_P或CKEDITOR.ENTER_DIV
							editor.config.resize_enabled = false;
							editor.config.toolbarLocation = 'top';
							editor.config.baseFloatZIndex = 10000;
							editor.config.removePlugins = 'elementspath';
			    			setCkeditorListens(editor);
                    }
				}
			    if(idlist.length>0){
			     	setEleConnect(idlist);
			    }
			}
			
			//填充当前模板页数据
			var fieldValueList = result.fieldValueList;
			if (fieldValueList!=null){
				initRecordSet(fieldValueList);
			}
		
			initElementValue(templateCard_me.getCurRecordSet(),pageId);	
			if(templateCard_me.templPropety.fillInfo=="1"&&this.curTab.isLoadData==false){
				var root = templateCard_me.mainPanel.el.dom;
				var a0101_2 = Ext.query("input[field='a0101_2']",true,root)[0];
				if(a0101_2){
					if(a0101_2.value.indexOf('临时人员_')!=-1)
						a0101_2.value="";
				}
			}
			this.curTab.isLoadData=true;
			var count = this.personListGrid.tablePanel.store.getCount();
			if(count==0){
				if(this.tabList.length>7){//放置左侧时，修改第一个页签样式
					var style=Ext.query("a[class$=x-tab-default-left]",true,templateCard_me.mainPanel.el.dom)[0].style;
					if(style.cssText.indexOf("margin-top")==-1&&templateCard_me.getCurrPageId()==0){
						style.cssText=style.cssText+";margin-top:1px !important";
					}
				}
			}
		},scope:this},map);
	},
	/**
	 * 获取保存record记录
	 * */
	getSaveRecord:function(){
	    templateCard_me.changedA0101="";	
		var records = [];
		var record=this.getCurRecordSet();	
		var modified = false;
		if(record){
			var recordData="";
			var newRecord={};
			for (var i=0;i<record.getFieldCount();i++){
				var valueItem =record.fields[i];
				if (valueItem.fldName=='a0101_2' || valueItem.fldName=='codeitemdesc_2'){
					var fieldsetItem = templateCard_me.getFieldItem(valueItem.uniqueId);
					if(fieldsetItem.ismobile!='1')
						templateCard_me.changedA0101=valueItem.keyValue;
				}
				if (valueItem.modified || valueItem.uniqueId==""||(templateCard_me.signXml!=''&&valueItem.fldName=='signature')){//做过修改的及系统指标才传到后台 20160628启用 
				    if(templateCard_me.signXml!=''&&valueItem.fldName=='signature'){
				    	if(templateCard_me.signatureType==0&&Ext.isIE){//金格科技
				    		updateJgkjDocumentid();
				    	}else if(templateCard_me.signatureType==2){
				    		updateDocumentid();
				    	}
				    	newRecord[valueItem.fldName]=templateCard_me.signXml;
				    	if(!modified){
					    	modified = true;
					    }
				    }else{
				    	newRecord[valueItem.fldName]=valueItem.keyValue;
				    	if(valueItem.modified&&!modified){
					    	modified = true;
					    }
				    }
				}
				//newRecord[valueItem.fldName]=valueItem.keyValue;
			}
			newRecord["realtask_id_e"]=templateCard_me.cur_task_id;
			records.push(newRecord);
		}
		if(!modified){
			records = [];
		}
		return records;
	},
	
	   /**
     * 如姓名发生变化，更新左侧列表的姓名
     * value:用于实时更新，但需考虑未保存情况，暂不这样做
     * */
    changeA0101:function(value){   
	     if (!templateCard_me.personListCurRecord){
	       templateCard_me.personListCurRecord=templateCard_me.personListGrid.tablePanel.getStore().getAt(0);
	     }  
        if (templateCard_me.personListCurRecord){
            if (templateCard_me.changedA0101!=""){
                var columnName="a0101_2";
                if (templateCard_me.templPropety.infor_type!="1"){
                    columnName="codeitemdesc_2";
                }
                if(value=="1"&&templateCard_me.templPropety.infor_type=="1")
                {
                	columnName="a0101_1";
                }
                if(value=="1"&&templateCard_me.templPropety.infor_type!="1")
                {
                	columnName="codeitemdesc_1";
                }
	           // templateCard_me.personListCurRecord.set(columnName,value);
	            templateCard_me.personListCurRecord.set(columnName,templateCard_me.changedA0101);
	            templateCard_me.personListCurRecord.commit();
	            var store = Ext.data.StoreManager.lookup('templatecard_dataStore');
	            var rowIndex = 0;
	            store.each(function(Record,index){
				    		if(Record.get('objectid_e') == templateCard_me.personListCurRecord.get('objectid_e')){
				    			rowIndex = index;
				    			return;
				    		}
	    		})
	    		templateMain_me.selecRowChangeCss(templateCard_me.personListGrid.tablePanel,rowIndex);//第一行颜色为选中
            }
        }
    },

    /**
     * 取得页签对象
     * */
    getTabObj:function(tabId){
        var tabObj =null;
        for (var i=0;i<this.tabList.length;i++){
            tabObj =this.tabList[i];            
            if (tabId ==tabObj.id ){
                break;
            }
        }
        return tabObj;
    },
        /**
     * 取得当前页签号pageid
     * */
    getCurrPageId:function(){
    	if(templateCard_me.curTab){
	        var tabId =templateCard_me.curTab.id;
	        var i= tabId.indexOf("_");
	        return tabId.substr(i+1,10);
    	}else
    		return  0;//对页签没有权限
    },

	/**
	 *获取当前指标集fieldSet
	 * */
	getCurFieldSet:function(){	
		return templateCard_me.fieldSet;
	},
	/**
	 *获取当前数据集recordSet
	 * */
	getCurRecordSet:function(){
		return templateCard_me.recordSet;
	},
	 /**
     *获取当前操作元素的指标属性
     * */
    getCurFieldItem:function(){
        var unique_id =templateCard_me.activeElement.getAttribute("id");
        return templateCard_me.getFieldItem(unique_id);
    },
	/**
     *获取当前操作元素的value属性
     * */
    getCurValueItem:function(){
        var unique_id =templateCard_me.activeElement.getAttribute("id");
        return templateCard_me.getValueItem(unique_id);
    },
    /**
     *获取当前指标集fieldSet某个字段的属性
     * */
	getFieldItem:function(unique_id){
	    var fieldItem= templateCard_me.fieldSet.getField(unique_id)
        return fieldItem;
    },
    /**
     *获取当前指标集recordSet某个元素的value属性
     * */
	getValueItem:function(unique_id){
	    var valueItem= templateCard_me.recordSet.getField(unique_id)
        return valueItem;
    },
	/**
	 *清空当前的recordSet
	 * */
	clearRecordSet:function(){
		templateCard_me.recordSet="";
		//清除签章
		if(this.signatureType==1&&Ext.isIE&&templateCard_me.signXml!=''){
			var root = templateCard_me.mainPanel.el.dom;
			var object = Ext.query("object",true,root);
			Ext.each(object,function(e){
				e.visible = false;
			});
		}else if(this.signatureType==0&&Ext.isIE&&templateCard_me.signXml!=''){
			var flag = ShowJgkjSignature('0');//隐藏签章
			if(flag =='0')
				DeleteJgkjSignature();
		}else if(this.signatureType==2&&templateCard_me.signXml!=''){
			HideSignature();//隐藏签章
		}else if(this.signatureType==3){
			HideSignatureHtml5();//隐藏签章
		}
		templateCard_me.signXml="";
		templateCard_me.documentrecordID = "";
		templateCard_me.signobjarr = [];
		templateCard_me.signatureMap = new HashMap();
		templateCard_me.showSignatureId = "";
		templateCard_me.userSignatureList = "";
		templateCard_me.notShowSignatureIDs = "";
		templateCard_me.readOnlySignatureIDs = "";
		templateCard_me.signature_fldid = new HashMap();
	},
//自下向上级联
	downToUpOthers:function(codeset_id,id,text,fatherUniqueid,uniqueid){
		var seleElement=document.getElementById(uniqueid);
		seleElement.value = text;
		var valueItem = templateCard_me.getValueItem(uniqueid);
		valueItem.setValue(id);
		if(fatherUniqueid!=""&&fatherUniqueid!=undefined){
			var fatherElement=document.getElementById(fatherUniqueid);
			var field=templateCard_me.getFieldItem(fatherUniqueid);
			if(field&&field.chgState=='2'){
			var map = new HashMap();
			map.put('codesetid',codeset_id);
			map.put('itemid',id);
			map.put('searchlevel','');
			Rpc({functionId:'MB00004004',async:false,success:function(form){
				var result = Ext.decode(form.responseText);
				var returnlist = result.returnlist;
				var isHaveFather=true;
				Ext.each(returnlist,function(e){
					var field=templateCard_me.getFieldItem(fatherUniqueid);
					if(field.codeSetId.toUpperCase()=='UN'&&e.codesetid=='UN'||field.codeSetId.toUpperCase()=='UM'&&e.codesetid=='UM'||field.codeSetId.toUpperCase()=='UM'&&e.codesetid=='UN'||field.codeSetId.toUpperCase()=='@K'&&e.codesetid=='@K'||((field.codeSetId!=''&&field.codeSetId!=undefined)&&field.codeSetId.toUpperCase()!='UN'&&field.codeSetId.toUpperCase()!='UM'&&field.codeSetId.toUpperCase()!='@K')){
						if(fatherElement)
							fatherElement.value = e.codeitemdesc;
						if(field&&field.chgState=='2'&&isHaveFather){
							var valueItem = templateCard_me.getValueItem(fatherUniqueid);
							valueItem.setValue(e.codeitemid);
							if(field.fatherRelationField!=""&&field.fatherRelationField!=undefined){
								fatherUniqueid=field.fatherRelationField;
								fatherElement=document.getElementById(fatherUniqueid);
							}else{
								fatherElement=null;
								isHaveFather=false;
							}
						}
					}
				});
			},scope:this},map);
			}
		}
	},
	/**
	 *单位部门岗位由下向上级联
	 **/
	downToUp:function(codeset_id,id,text){
		var root = templateCard_me.mainPanel.el.dom;
		var searchlevel = '',UNinputname = '',Kinputname = '',
		UMinputname = '',UNvalueName = '',KvalueName = '',
		UMvalueName = '',UNhidden,UMhidden,Khidden;
		var UN = Ext.query("input[field='b0110_2']",true,root);
		var UM = Ext.query("input[field='e0122_2']",true,root);
		var K = Ext.query("input[field='e01a1_2']",true,root);
		if(codeset_id=='@K'){
			if(UN.length>0){
				Ext.each(UN,function(e){
					var ids = e.id;
					document.getElementById(ids).setAttribute('selectcode','1');
				});
				searchlevel+='UN,';
			}
			if(UM.length>0){
				searchlevel+='UM,';
			}
			if(K.length>0){
				Ext.each(K,function(e){
					var valueItem = templateCard_me.getValueItem(e.id);
					valueItem.setValue(id);
					e.value=text;
					Kinputname = e.name;
					KvalueName = Kinputname.substring(0,Kinputname.length-4)+"value";
					Khidden = Ext.query("input[name='"+KvalueName+"']",true,root);
					Khidden[0].value = id;
				});
			}
		}
		else if(codeset_id=='UM'){
			if(UM.length>0){
				Ext.each(UM,function(e){
					var valueItem = templateCard_me.getValueItem(e.id);
					valueItem.setValue(id);
					e.value=text;
					UMinputname = e.name;
					UMvalueName = UMinputname.substring(0,UMinputname.length-4)+"value";
					UMhidden = Ext.query("input[name='"+UMvalueName+"']",true,root);
					UMhidden[0].value = id;
				});
			}
			if(UN.length>0){
				Ext.each(UN,function(e){
					var ids = e.id;
					document.getElementById(ids).setAttribute('selectcode','1');
				});
				searchlevel+='UN';			
			}
			if(K.length>0){
				if(K[0].value!=''&&K[0].value!='`'&&K[0].value!=undefined){
					Ext.each(K,function(e){
						var valueItem = templateCard_me.getValueItem(e.id);
						valueItem.setValue('');
						e.value='';
					});
				}
			}
		}else if(codeset_id=='UN'){
			if(UN.length>0){
				Ext.each(UN,function(e){
					//document.getElementById(ids).setAttribute('selectcode','1');
					var valueItem = templateCard_me.getValueItem(e.id);
					valueItem.setValue(id);
					e.value=text;
					UNinputname = e.name;
					UNvalueName = UNinputname.substring(0,UNinputname.length-4)+"value";
					UNhidden = Ext.query("input[name='"+UNvalueName+"']",true,root);
					UNhidden[0].value = id;
				});
			}
			if(UM.length>0){
				if(UM[0].value!=''&&UM[0].value!='`'&&UM[0].value!=undefined){
					Ext.each(UM,function(e){
						var valueItem = templateCard_me.getValueItem(e.id);
						valueItem.setValue('');
						e.value='';
					});
				}
			}
			if(K.length>0){
				if(K[0].value!=''&&K[0].value!='`'&&K[0].value!=undefined){
					Ext.each(K,function(e){
						var valueItem = templateCard_me.getValueItem(e.id);
						valueItem.setValue('');
						e.value='';
					});
				}
			}
		}
		if(searchlevel!=''){
			var map = new HashMap();
			map.put('codesetid',codeset_id);
			map.put('itemid',id);
			map.put('searchlevel',searchlevel);
			Rpc({functionId:'MB00004004',async:false,success:function(form){
				var result = Ext.decode(form.responseText);
				var returnlist = result.returnlist;
				Ext.each(returnlist,function(e){
					if(e.codesetid=='UM'){
						//if(UM[0].value==''){
							for(var i=0;i<UM.length;i++){
								var layerdesc = e.layerdesc;
								if(layerdesc!="")
									UM[i].value = layerdesc;
								else
									UM[i].value = e.codeitemdesc;
								UMinputname = UM[i].name;
								UMvalueName = UMinputname.substring(0,UMinputname.length-4)+"value";
								UMhidden = Ext.query("input[name='"+UMvalueName+"']",true,root);
								UMhidden[0].value = e.codeitemid;
								var valueItem = templateCard_me.getValueItem(UM[i].id);
								valueItem.setValue(e.codeitemid);
							}
						//}
					}
					if(e.codesetid=='UN'){
						//if(UN[0].value==''){
							for(var i=0;i<UN.length;i++){
								UN[i].value = e.codeitemdesc;
								UNinputname = UN[i].name;
								UNvalueName = UNinputname.substring(0,UNinputname.length-4)+"value";
								UNhidden = Ext.query("input[name='"+UNvalueName+"']",true,root);
								UNhidden[0].value = e.codeitemid;
								var valueItem = templateCard_me.getValueItem(UN[i].id);
								valueItem.setValue(e.codeitemid);
							}
						//}
					}
				});
			},scope:this},map);
		}
	}
});

function renderSubSet(uniqueId,columnId,title,tabid){
	showSubSet(uniqueId,columnId,tabid);
};

/**
 *按模板页Id获取当前数据结构dataset的Id 
 * */
function getFieldSetIdByPageId(curPageId){
	var dataSetid= replaceAll(curPageId,"tab_","dataset_");
	return dataSetid;
};
/**
 *按模板页Id获取当前数据集record的Id 
 * */
function getRecordSetIdByPageId(curPageId){
	var recordid= replaceAll(curPageId,"tab_","dataset_");
	return recordid;
};
/**
 *enter键查找下一个元素
 **/
function getNextElement(field) {
	//liuyz 北理工优化利用enter键盘切换单元格时能否把变化前的单元格自动过滤掉，如果过滤不掉切换就没意义了
   var uniqueId=field
    var changePageList=templateCard_me.getCurFieldSet().fields;
    var curpage=templateCard_me.getCurrPageId();
    if(changePageList!=null)
    {
    	var i=0;
    	//查找当前选中的单元格
		for(;i<changePageList.length;i++)
		{
			if(uniqueId==changePageList[i].uniqueId)
			{
			   document.getElementById(changePageList[i].uniqueId).blur();
			   var treepanelitem = Ext.ComponentQuery.query('datetimepicker');
			   if(treepanelitem){
			   		var isie = (document.all) ? true : false;
					if(isie)
					{
						//手动触发mousedown事件使时间弹出框消失
						document.getElementById(changePageList[i].uniqueId).fireEvent('onmousedown');
					}
					else
					{
						//手动触发mousedown事件使时间弹出框消失
						var fireOnThis = document.getElementById(changePageList[i].uniqueId);
		                var evObj = document.createEvent('MouseEvents');
		                evObj.initEvent('mousedown', true, false);
		                fireOnThis.dispatchEvent(evObj);
					}
			   }
			   break;
			}
		}
		var j=0;
		//从当前开始向下查找，如果找到最后一个就从第一个元素开始查找，但只循环一次，防止出现死循环。
		for(;j<changePageList.length;j++)
		{	
			if(i>=changePageList.length-1)
			{
				//27606  循环到最后直接退出
				break;
			}
			else
			{
				i++;
			}
			if(changePageList[i].uniqueId.indexOf("_"+curpage+"_")>=0&&changePageList[i].rwPriv=="2"&&(changePageList[i].chgState=="2"||changePageList[i].chgState=="0")&&(changePageList[i].fldType=="A"&&changePageList[i].fldLength<255||changePageList[i].fldType=="D"||changePageList[i].fldType=="N"))
			{

				document.getElementById(changePageList[i].uniqueId).focus();
				var fn=function(){
					//手动触发click事件使时间弹出框弹出 
					document.getElementById(changePageList[i].uniqueId).click()
				};
				//按下focus200毫秒触click方法，否则可能引起冲突。
				setTimeout(fn,200);
			    break;
			}
		}
	}
};
/**
*初始化锁对象
*/
function initSignObject(){
	var SignatureControl=document.getElementById('ctrl');
	if(Ext.isIE){
		if(SignatureControl){
			SignatureControl.parentNode.removeChild(SignatureControl);
			createSignObject();
		}else{
			createSignObject();
		}
	}else
		createSignObject();
}
/**
*创建锁对象
*/
function createSignObject(){
	if(Ext.isIE){
		var signObjdiv = document.createElement("div");
		var innerHtml = '<OBJECT id="ctrl" classid="clsid:33020048-3E6B-40BE-A1D4-35577F57BF14" VIEWASTEXT width="0" height="0"></OBJECT>'; 
		signObjdiv.innerHTML = innerHtml;
		document.body.appendChild(signObjdiv);
	}
	b = new Base64();
	try{
		ctrl = new AtlCtrlForRockeyArm("{33020048-3E6B-40BE-A1D4-35577F57BF14}"); 
	}catch (e){
		ctrl = null;
		websock = false;
	}
}

