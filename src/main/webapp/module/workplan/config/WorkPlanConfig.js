/**
 * workplan 参数设置页面
 */
 Ext.define('WorkPlanConfigUL.WorkPlanConfig',{
	 fillpersonStore:undefined,
	 isOpenCooperationTaskVersion:true,//是否在version.xml中启用“我的协作任务”功能
	 myStyle:"",
 	 requires:[
		"WorkPlanConfigUL.BaseParam",
	   	"WorkPlanConfigUL.PeriodRange",
	   	"WorkPlanConfigUL.BatchSetting"
     ],
	 constructor:function(){
		workPlanConfig = this;
		workPlanConfig.init();
		workPlanConfig.createBaseMain();//创建主页面
 	},
 	writeStyle:function(themes){
 		workPlanConfig.headerColor='';
	  	    var menuColor='';
	  	    if(themes=='gray'){//灰色
				workPlanConfig.headerColor = '#BDBDBD';
				menuColor = '#F3F3F3'
	  		}else if(themes =='green'){//绿色
	  			workPlanConfig.headerColor = '#338f5e';
				menuColor = '#E9FFFF';
			}else if(themes =='red'){//红色
	  			workPlanConfig.headerColor = '#9A0000';
				menuColor = '#FFF5F5';
			}else if(themes =='lightGreen'){//亮绿
	  			workPlanConfig.headerColor = '#38A86D';
				menuColor = '#F1FFF8';
			}else if(themes =='cyanineBlue'){//淡蓝
	  			workPlanConfig.headerColor = '#5191D1';
				menuColor = '#CBE3FD';
			}else if(themes =='lightBlue'){//亮蓝
	  			workPlanConfig.headerColor = '#22B1FF';
				menuColor = '#F5F5F5';
			}else{
				themes = 'default';
				workPlanConfig.headerColor = '#2a68bb';
				menuColor = '#D6EBFF';
			}

		Ext.util.CSS.createStyleSheet("#menuTree .no-icon {display:none;}","delImg");
		Ext.util.CSS.createStyleSheet("#menuTree .x-panel-header{background-color:"+workPlanConfig.headerColor+";}","delImg");
		Ext.util.CSS.createStyleSheet("#menuTree .x-grid-item-selected{background-color:"+menuColor+";background: url('/module/workplan/images/"+themes+"/leftbar_hover_bg.png') no-repeat scroll right center"+menuColor+";}","delImg");
		Ext.util.CSS.createStyleSheet("#menuTree .x-grid-item-over{background-color:"+menuColor+";background: url('/module/workplan/images/"+themes+"/leftbar_hover_bg.png') no-repeat scroll right center "+menuColor+";}","delImg");
		Ext.util.CSS.createStyleSheet("#menuTree .x-grid-cell-inner-treecolumn {float: right;padding-right: 18px;}","delImg");
	},
 	init:function(){//设置主题配色
	   var map = new HashMap();
	   map.put('opt','theme'); //=update 保存更新 =select查询数据 =theme 查询主题配色
       Rpc({functionId:'WP20000001',async:false,success:function(response){
    	   var data = Ext.util.JSON.decode(response.responseText);
    	   workPlanConfig.writeStyle(data.themes);
    	   
    	   workPlanConfig.isOpenCooperationTaskVersion = data.isOpenCooperationTaskVersion;
       }},map);
 	},
 	//创建主页面
 	createBaseMain:function(){
 		var treePanel = workPlanConfig.createMenu();
 		var rightPanel = Ext.widget('panel',{
			id:'rightPanel',
			border:false,
			layout:'fit',
			region:'center'
		 });
 		Ext.create('Ext.container.Viewport',{
 			id:'container',
 			layout:'border',
 			items:[treePanel,rightPanel],
 		    renderTo:Ext.getBody()
 		})
 	},
 	//创建填报期间范围页面
 	createPeriodrange:function(){
 		return Ext.create("WorkPlanConfigUL.PeriodRange",{});
 	},
 	//创建填报人员页面
 	careateFillperson:function(){
 		var mainpanel = '';
 		
		var map = new HashMap();
		map.put('opt','queryPerson');
	    Rpc({functionId:'WP20000003', async:false, success:function(){
	        isOpen = [{'dataValue':'0','dataName':''}, {'dataValue':'1','dataName':'√'}];//评审状态
	    	var result = Ext.decode(arguments[0].responseText);
			var jsonData = result.tableConfig;
			var obj = Ext.decode(jsonData);
			var tablePanel = new BuildTableObj(obj);
			workPlanConfig.fillpersonStore = tablePanel.tablePanel.getStore();
		 	mainpanel = tablePanel.getMainPanel();	
	    }, scope:this},map);
	    
	    return mainpanel;
 	},
 	//创建其他参数页面
 	createParamCfg:function(){
 		return Ext.create("WorkPlanConfigUL.BaseParam",{});
 	},
 	//创建菜单列
 	createMenu:function(){
 		var store = Ext.create("Ext.data.TreeStore",{
 			rootVisible: false,
 			root:{
 			  expanded: false,//是否展开菜单
 			  children:[
 			    {
 			    	id:'periodrange',//填报期间范围
 			   		iconCls:'no-icon',
 			   		leaf: true,
 			     	text:'<span style="cursor:pointer;">'+wp.param.periodrange+'</span>'	
 			    },{
 			    	id:'fillperson',//填报人员
 			   		iconCls:'no-icon',
 			   		leaf: true,
 			     	text:'<span style="cursor:pointer;">'+wp.param.fillperson+'</span>'	
 			    },{
 			    	id:'baseParam',//其他参数
 			    	iconCls:'no-icon',
 			    	leaf: true,
 				    text:'<span style="cursor:pointer;">'+wp.param.otherconf+'</span>'
 			    }
 			 ]
 		    }
 		});
 		var treePanel = Ext.create("Ext.tree.Panel",{
 			id:'menuTree',
 			rootVisible:false,
 			store:store,
 			region:'west',
 			width:130,
 			title:'<label style="font-size:14px;font-weight:normal;color: #FFFFFF">参数设置</label>',
 			lines:false,
 			bodyStyle:"border-width:0 1px 0 0",
 			listeners: {
 				select:workPlanConfig.itemClick,
 	 		    afterrender:function(){
 	 		        var record = this.getStore().getAt(0);
 	 		        this.getSelectionModel().select(record)
 	 		    }
 			}
 		});
 		
 		return treePanel;
 	},
 	//响应菜单点击事件
 	itemClick:function(index, record){
 		var rightPanel = Ext.getCmp('rightPanel');
 		rightPanel.removeAll();	//清空右侧页面布局
 		if(record.data.id =='baseParam')
 			rightPanel.add(workPlanConfig.createParamCfg());//其他参数页面
 		else if(record.data.id=='periodrange')
 			rightPanel.add(workPlanConfig.createPeriodrange());//填报期间范围页面
 		else if(record.data.id=='fillperson')
 			rightPanel.add(workPlanConfig.careateFillperson());//填报人员页面
 	},
 	// 批量设置窗口显示
 	batchSetting:function(){
 		Ext.create("WorkPlanConfigUL.BatchSetting",{}).show();
 	},
 	
 	//获取人员库
 	getDbaseFieldset:function(){
 	
 		var checkboxgroup = Ext.widget({
                xtype     : 'checkboxgroup',
                columns   : workPlanConfig.dbList.length>4?2:1,
                id        :'checkboxdbValue',
                width     : 400,
                vertical  : true
            });
            
            //人员库的panel
            var grid = Ext.widget({
                xtype:'panel',
                border:false,
                height: 100,
                scrollable:true,
                items:[checkboxgroup]
            });
            Ext.each(workPlanConfig.dbList,function(obj,index){
                var checkbox = Ext.widget({
                    xtype     : 'checkbox',
                    boxLabel  : obj.dbname,
                    name      : 'dbValue',
                    checked   : obj.isSelected==1?true:false,
                    inputValue: obj.pre,
                    id        : obj.pre
                });
                checkboxgroup.add(checkbox);
            });
            
            //人员库
            var dbaseFieldset = Ext.widget({
                xtype:'fieldset',
                height: 140,
                width  : 480,
                title:'人员库',
                margin:'14 0 0 10',
                items:grid
            });
            
 	      return dbaseFieldset;
 	},
 	//获取人员范围
 	getScopeFieldset:function(){
 	
 		var scopeFieldset = Ext.widget({
                xtype:'fieldset',
                height: 170,  
                width  : 480,
                title:'人员范围',
                margin:'10 0 0 10',
                items:[{
                        xtype:'panel',
                        border:false,
                        itemId:'conditionId',
                        margin :'10 0 0 10',
                        layout:'hbox',
                        items:[{
                        	xtype:'panel',
                            border:false,
                            items:[{
                                xtype:'textarea',
                                id:'contenId',
                                disabled:true,
                                height:130,
    //                            scrollable : 'y',
                                width  : 380,
                                style:{
                                    filter: '',opacity:'1.0'
                                },
                                value:workPlanConfig.empScopeValue
                             }]
                        },{
                            xtype:'panel',
                            border:false,
                            margin :'0 0 0 16',
                            layout:'vbox',
                            items:[{
                                xtype:'button',
                                id:'complexButton',
                                text:'定义',
                                handler:function(){
                                	//弹出简单条件
                                	 var map2 = new HashMap();
                                     map2.put(1, "a,b,k");//要显示的子集
                                     var map = new HashMap();
                                     map.put('salaryid', '');
                                     map.put('condStr', workPlanConfig.condStr);//复杂条件，简单条件表达式
                                     map.put('cexpr', workPlanConfig.cexpr);//简单公式时：1*2
                                     map.put('path', "2306514");
                                     map.put('priv', "1");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
                                     map.put('buttonText', common.button.ok);
                                     Ext.require('EHR.selectfield.SelectField',function(){
                                         Ext.create("EHR.selectfield.SelectField",{imodule:"",type:"1",flag:'1',dataMap:map,comBoxDataInfoMap:map2,rightDataList:'',title:"选择指标",saveCallbackfunc:"workPlanConfig.saveCond"});
                                     });
                                }
                            },{
                                xtype:'button',
                                margin:'10 0 0 0',
                                text:'清空',
                                handler:function(){
                                    var map = new HashMap();
                                    map.put('opt','deleteEmp'); //=deleteEmp 查询人员库和人员条件数据linbz
                                    Rpc({functionId:'WP20000001',async:false,success:function(response){
                                    	workPlanConfig.condStr = ""; 
                                        workPlanConfig.cexpr = "";
                                        workPlanConfig.empScopeStr = "";
                                        workPlanConfig.empScopeValue = "";
                                        Ext.getCmp('contenId').setValue("");
                                    }},map);
                                }
                            }]              
                    }]
                }]
            });
            
            return scopeFieldset;
 	},
 	//人员范围设置 窗口
 	empScopeOK:function(){
 	  
 		var win = Ext.widget("window",{
            id:'empScopeId',
            title:'人员范围设置',
            width: 520,
            height: 420,
            border:0,
            modal:true,
            resizable:false,
            closeAction:'destroy',
            items: [workPlanConfig.getDbaseFieldset(),workPlanConfig.getScopeFieldset()
                    ,{
                        xtype:'panel',
                        border:false,
                        height: 46,
                        layout: {
                            type: 'hbox',
                            align: 'middle',
                            pack: 'center'
                        },
                        items:[{
                            xtype:'button',
                            text:'确定',
                            handler:function(){
                                var dbValues = "";
                                var isHavePersonScope=false;
                                Ext.each(Ext.getCmp('checkboxdbValue').items.items,function(obj,index){
                                    if(obj.checked){
                                        isHavePersonScope=true;
                                        dbValues += ','+obj.inputValue;
                                    }
                                });
                                if(!isHavePersonScope){
                                    Ext.MessageBox.alert(common.button.promptmessage,'请选择人员库！');
                                    return;
                                }
                                var contens = workPlanConfig.empScopeStr;
                                var map = new HashMap();
                                map.put('opt','saveNabseEmp'); //=saveNabseEmp 保存人员库人员条件
                                map.put('nbases',dbValues);
                                map.put('emp_scope',getEncodeStr(contens));
                                Rpc({functionId:'WP20000001',async:false,success:function(){
                                    win.close();
                                    
                                    var rightPanel = Ext.getCmp('rightPanel');
                                    rightPanel.removeAll(); //清空右侧页面布局
                                    rightPanel.add(workPlanConfig.careateFillperson());//填报人员页面
                                    },
                                    failure:function(){
                                       Ext.Msg.show({
                                           title:'出错了！！',
                                           msg:'保存失败！ 请与管理员联系...',
                                           buttons: Ext.Msg.OK,
                                           icon:Ext.Msg.ERROR
                                       });
                                    }
                                 },map);
                            }
                        },{
                            xtype:'button',
                            margin:'0 0 0 14',
                            text:'取消',
                            handler:function(){
                                win.close();
                            }
                        }]  
                    }
            ]
        });
        win.show();
 	},
 	//人员范围设置按钮
 	empScope:function(){
 		workPlanConfig.dbList = null;
        workPlanConfig.condStr = ""; 
        workPlanConfig.cexpr = "";
        workPlanConfig.empScopeStr = "";
        workPlanConfig.empScopeValue = "";
 		var map = new HashMap();
        map.put('opt','selectNabseEmp'); //=selectNabseEmp 查询人员库和人员条件数据linbz
        Rpc({functionId:'WP20000001',async:false,success:function(response){
           var result = Ext.util.JSON.decode(response.responseText);
           workPlanConfig.dbList = result.dbList;
           workPlanConfig.empScopeValue = result.empScopeValue;
           var strValue = workPlanConfig.getReplaceStr(getDecodeStr(result.emp_scope));
           workPlanConfig.empScopeStr = strValue;
           workPlanConfig.condStr = strValue.split("|")[1];
           workPlanConfig.cexpr = strValue.split("|")[0];
           workPlanConfig.empScopeOK();
        }},map);
 	},
 	//保存公式
    saveCond:function(c_expr){
        c_expr = decode(c_expr);
        workPlanConfig.condStr = c_expr.split("|")[1];
        workPlanConfig.cexpr = c_expr.split("|")[0];
        workPlanConfig.empScopeStr = c_expr;
        if(!Ext.isEmpty(c_expr)){
            var map = new HashMap();
            map.put('opt','selectEmpScope'); //=selectEmpScope 查询人员条件数据解析公式linbz
            map.put('emp_scope',c_expr);
            Rpc({functionId:'WP20000001',async:false,success:function(response){
               var result = Ext.util.JSON.decode(response.responseText);
               workPlanConfig.empScopeValue = result.empScopeValue;
               Ext.getCmp('contenId').setValue(workPlanConfig.empScopeValue);
            }},map);
        }
    },
    
 	//
 	rendererColumn:function(value, metaData, Record){
 		var itemid = metaData.column.dataIndex;
		var guidkey = Record.data.guidkey;
 		var checked = '';
 		if(value == '1'){
 			checked = 'checked=checked';
 		}
        var index = workPlanConfig.fillpersonStore.indexOf(Record);
 		return '<div style="text-align:center;"><input name="'+itemid+'" style="cursor:pointer;" type="checkbox" '+checked+' onclick=javascript:workPlanConfig.updataPersonFunc(this,"'+guidkey+'","'+index+'"); /></div>';
 	},//更新人员设置
 	batchsettingRenderColumn:function(value, metaData, record){
 		var itemid = record.data.id;
 		return '<div style="text-align:center;"><input class="batchSettingCls" name="'+itemid+'" style="cursor:pointer;" type="checkbox"/></div>';
 	},
 	updataPersonFunc:function(input,guidkey,index){
 	    var map = new HashMap();
 		if(guidkey && input){//单个人设置
 			map.put("guidkey",guidkey);
 			map.put("itemid",input.name);
 			var value = '0';
 			if(input.checked)
 				value = '1';
 			map.put("value",value);
 			map.put("opt","saveOne");

            var record = workPlanConfig.fillpersonStore.getAt(index);
            record.set(input.name,value);
            record.dirty = false;
            record.commit();
            //保存
            workPlanConfig.rpcforSave(map,input,guidkey);
 		}else{//批量设置
 		   Ext.showConfirm("批量设置会重新设置所有人员的权限，是否继续？",function(flag){
 		       if(flag == "yes"){
                   new Ext.LoadMask({
                       id:'loadMarsk',
                       msg:'正在保存...',
                       alwaysOnTop:true,
                       target:Ext.getCmp("batchSettingWin")
                   }).show();

                   map.put("opt","batchSave");
                   var inputs = Ext.query("input[class=batchSettingCls]");
                   var hm = new HashMap();
                   Ext.Array.each(inputs,function(checkbox){
                       if(checkbox.checked){
                           hm.put(checkbox.name,"1");
                       }else{
                           hm.put(checkbox.name,"0");
                       }
                   });
                   map.put("configMap",hm);
                   workPlanConfig.rpcforSave(map,input,guidkey);
               }
           })
 		}
 	},
     rpcforSave:function(map,input,guidkey){
         Rpc({functionId:'WP20000003',async:true,success:function(form,action){
                 var loadMarsk = Ext.getCmp("loadMarsk");
                 if(loadMarsk)
                     loadMarsk.destroy();
                 var data = Ext.decode(form.responseText);
                 var msg = data.msg;
                 if(guidkey && input)
                     return;//单个设置没有提示信息
                 if(!msg || Ext.isEmpty(msg)){
                     msg = data.message;
                 }
                 Ext.Msg.alert("提示信息",msg,function(){
                     Ext.getCmp('batchSettingWin').close();
                 });
                 workPlanConfig.fillpersonStore.reload();
             }},map);
     },
 	/** 栏目设置保存后回调函数 */
 	schemeSaveCallback:function(){
 	   var rightPanel = Ext.getCmp('rightPanel');
       rightPanel.removeAll(); //清空右侧页面布局
       var map = new HashMap();
       map.put('opt','queryPerson');
       Rpc({functionId:'WP20000003', async:false, success:function(){
           //用于导出excel时 启用现实√ 不启用则不显示
           isOpen = [{'dataValue':'0','dataName':''}, {'dataValue':'1','dataName':'√'}];
           var result = Ext.decode(arguments[0].responseText);
           var jsonData = result.tableConfig;
           var obj = Ext.decode(jsonData);
           var tablePanel = new BuildTableObj(obj);
           workPlanConfig.fillpersonStore = tablePanel.tablePanel.getStore();
           rightPanel.add(tablePanel.getMainPanel());  
       }, scope:this},map);
 	},
 	//替换字符
 	getReplaceStr:function(content){
 	
 		content=replaceAll(content,"＜","<");
        content=replaceAll(content,"＞",">");
        content=replaceAll(content,"＇","'");
        content=replaceAll(content,"＂",'"');
        content=replaceAll(content,"；",";");
        
        return content;
 	}
 	
 })