/**
 * 人员工资|保险台账
 */
Ext.define("EmployeePayMusterURL.EmployeePayMuster",{
	constructor:function(config) {
		employpaymuster_me = this;
		employpaymuster_me.callBackFunc = config.callBackFunc;
		employpaymuster_me.edit_pow = config.edit_pow;
		employpaymuster_me.init(config);
	},
	init:function(config){
		employpaymuster_me.rsid = config.rsid;
		employpaymuster_me.rsdtlid = config.rsdtlid;
		employpaymuster_me.imodule = config.imodule;//0:薪资  1：保险
		employpaymuster_me.orgcodeids = "";
		employpaymuster_me.orgcodeidsMap = new HashMap();
		employpaymuster_me.keyCode = "";
		employpaymuster_me.tableName = config.tableName;
		var map = new HashMap();
		employpaymuster_me.tYear = "";
		map.put("year",employpaymuster_me.tYear);
		map.put("rsid",employpaymuster_me.rsid);
		map.put("rsdtlid",employpaymuster_me.rsdtlid)
		map.put("transType","1");
		map.put("intoflag",'0');
		Rpc({functionId:'GZ00000706',async:false,success:employpaymuster_me.getTableOK,scope:employpaymuster_me},map);
	},
	getTableOK:function(form,action){
		var responseText = Ext.decode(form.responseText);
		var returnObj = Ext.decode(responseText.returnStr);
        var return_code = returnObj.return_code;
        var result = returnObj.return_data;
		var flag = result.flag;
		if(flag=='1'){
			var store = Ext.data.StoreManager.lookup('employeepaymuster1_dataStore'); 
			store.currentPage=1;
			store.load();
		}else{
			employpaymuster_me.fieldsMap = result.fieldsMap;
			employpaymuster_me.fieldsArray = result.fieldsArray;
			var yearselectjson = result.yearselectjson;
			var isHaveOnlyField = result.isHaveOnlyField;
			employpaymuster_me.rsid_d = result.rsid_d;
			employpaymuster_me.rsdtlid_d = result.rsdtlid_d;
			//左侧人员列表
			var personListObj = result.tableConfig;
			/*personListObj.beforeBuildComp=function(config){
				config.tableConfig.selModel=Ext.create('Ext.selection.CheckboxModel', {
	                mode : 'SINGLE'
	            })
			};*/
			personListObj.simpleModel=true;
			personListObj.showDisplayInfo = true;
			//左侧人员列表需添加默认选中第一个人
			employpaymuster_me.personListGrid = new BuildTableObj(personListObj);
			document.onkeydown=function(evt){
				var isie = (document.all) ? true : false;
				if (isie) {
					employpaymuster_me.keyCode = event.keyCode;
				}
				else {
					employpaymuster_me.keyCode = evt.which;
				}
			};
			employpaymuster_me.addGridListens(employpaymuster_me.personListGrid);
			//给左侧人员添加查询框
			var emptyText = gz.label.analysisdata.queryitem;
			if(!isHaveOnlyField){
				emptyText = gz.label.analysisdata.queryitemnoonlycode;
			}
			employpaymuster_me.subModuleId = "employeepaymuster";
			var map = new HashMap();
			map.put('fieldsMap',employpaymuster_me.fieldsMap);
			map.put('isSearch','true');
			employpaymuster_me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
	            emptyText:emptyText,
	            subModuleId:employpaymuster_me.subModuleId,
	            customParams:map,
	            funcId:"GZ00000706",
	            queryBoxWidth:240,
	            fieldsArray:employpaymuster_me.fieldsArray,
	            success:employpaymuster_me.searchEmployOK
	        });
	        
	        //var toolbar = Ext.getCmp('employeepaymuster1_toolbar');
	        //toolbar.insert(0,employpaymuster_me.SearchBox);
			
			employpaymuster_me.mainPanel = employpaymuster_me.personListGrid.getMainPanel();
			var leftpanel = Ext.create('Ext.panel.Panel',{
				region:'west',
				border:0,
				layout:'fit',
				margin:'-1 0 0 0',
				width:263,
				items:[employpaymuster_me.mainPanel]
			})
			
			//右侧数据列表  先显示空数据 ///////////////////
			var map = new HashMap();
			map.put("year",employpaymuster_me.tYear);
			map.put("objectid",'');
			map.put("rsid",employpaymuster_me.rsid);
			map.put("rsdtlid",employpaymuster_me.rsdtlid)
			map.put("transType","2");
			map.put("fielditemid",'');
			map.put("fielditemvalue",'');
			map.put("intoflag",'0');
			Rpc({functionId:'GZ00000706',async:false,success:employpaymuster_me.getTableDataOk,scope:employpaymuster_me},map);
			var toolbar_data = Ext.create('Ext.toolbar.Toolbar', {
				id:'employee_toolbar',
				height:35,
				border:0,
				items:[{
			        text: gz.label.analysisdata.navigation,
			        height:24,
			        menu:
			        {
			            items: [
			                {
			                    text: gz.label.outExcel,
			                    icon:"/images/export.gif",
			                    menu:{
			                    	items:[
			                    		{
			                    			text: gz.label.analysisdata.excelall,
			                    			handler: function () {
			                    				var store = Ext.data.StoreManager.lookup('employeepaymuster1_dataStore');
			                    				if(store.totalCount>1000){
			                    					Ext.showAlert(gz.label.analysisdata.outof1000formessage);
			                    					return;
			                    				}
			                    				var map = new HashMap();
						                    	map.put("rsid",employpaymuster_me.rsid);
						                		map.put("rsdtlid",employpaymuster_me.rsdtlid)
						                    	map.put("objectids","");
						                    	map.put("sheetnames","");
						                    	map.put("year",employpaymuster_me.tYear);
						                    	map.put("transType",'3');
						                    	map.put("intoflag",'1');
						                    	map.put("outflag",'0');
						                    	map.put("tableName",employpaymuster_me.tableName);
						                    	Ext.MessageBox.wait(gz.label.analysisdata.waitformessage, gz.label.wait);
						                    	Rpc({functionId:'GZ00000706',async:true,success:employpaymuster_me.outOk,scope:employpaymuster_me},map);
			                    			}
			                    		},{
			                    			text: gz.label.analysisdata.excelpart,
			                    			handler: function () {
						                    	//得到选择的人员
						                    	var selectRecords = employpaymuster_me.personListGrid.tablePanel.getSelectionModel().getSelection();
						                    	if(selectRecords.length<1){
						                    		Ext.showAlert(gz.label.analysisdata.selectneedperson);
						                			return;
						                    	}
						                    	var objectids = "";
						                    	var sheetnames = "";
						                    	for(var i=0;i<selectRecords.length;i++){
						            				var objectid = selectRecords[i].data.objectid_e;
						            				var a0101 = selectRecords[i].data.a0101;
						            				objectids+=objectid+',';
						            				sheetnames+=a0101+",";
						            			}
						                    	objectids=objectids.substring(0,objectids.length-1);
						                    	sheetnames=sheetnames.substring(0,sheetnames.length-1);
						                    	var map = new HashMap();
						                    	map.put("rsid",employpaymuster_me.rsid);
						                		map.put("rsdtlid",employpaymuster_me.rsdtlid)
						                    	map.put("objectids",objectids);
						                    	map.put("sheetnames",sheetnames);
						                    	//map.put("type",'1');
						                    	map.put("year",employpaymuster_me.tYear);
						                    	map.put("transType",'3');
						                    	map.put("intoflag",'1');
						                    	map.put("outflag",'1');
						                    	map.put("tableName",employpaymuster_me.tableName);
						                    	Ext.MessageBox.wait(gz.label.analysisdata.waitformessage, gz.label.wait);
						                    	Rpc({functionId:'GZ00000706',async:true,success:employpaymuster_me.outOk,scope:employpaymuster_me},map);
						                    }
			                    		}
			                    	]
			                    }
			                }/*, {
			                    text: '导出PDF',
			                    handler: function () {

			                    }
			                }*/,
			                {
			                    text: gz.label.analysisdata.pagesetting,
			                    icon: '/images/img_o.gif',
			                    handler: function () {
			                    	employpaymuster_me.showpagesetting(employpaymuster_me.rsid,employpaymuster_me.rsdtlid);
			                    }
			                }
			            ]
			        }
			    },'-',{xtype:'button',text:gz.label.analysisdata.setrange,handler:function(){employpaymuster_me.setRange()},hidden:!employpaymuster_me.edit_pow},'-',employpaymuster_me.SearchBox,'-',
			    {xtype:'button',text:'',id:'orgtreebutton',icon:'/module/gz/analysistables/images/orgtree.png',handler:function(){employpaymuster_me.getorgtree()}},
			    "->"/*,{xtype:'label',id:'label_usr',text:''}*/]
			});
			//年份选择下拉
			var selectStore = Ext.create('Ext.data.Store', {
				id:'selectStore',
				fields:['name','id'],
				data:yearselectjson
			});
			var selectPanel = Ext.create('Ext.form.ComboBox', {
				id:'selectPanel',
			    store: selectStore,
			    queryMode: 'local',
			    repeatTriggerClick : true,
			    margin:'0 5 0 0',
			    labelAlign:'right',
			    labelWidth:30,
			    displayField: 'name',
			    valueField: 'id',
			    editable:false,
			    width:110,
			    fieldStyle:'height:20px;',
				listeners:{
					afterrender:function(combo){
						var count = selectStore.getCount();
						if(count>0){
							var id = selectStore.getAt(0).get('id');
							if(id)
								combo.setValue(id);
							else
								combo.setValue(employpaymuster_me.tYear);
						}
						employpaymuster_me.tYear = combo.getValue()+"";
		     		},
	   				select:function(combo,records){
	   					employpaymuster_me.tYear = combo.getValue()+"";
	                	employpaymuster_me.switchyear();
					}
				}
			});
			toolbar_data.insert(4,selectPanel);
			//toolbar_data.insert(5,gz.label.analysisdata.particularyear);
			toolbar_data.insert(5,'-');
			///////////////////////////////////////////
			employpaymuster_me.panel = Ext.create('Ext.panel.Panel',{
	            border : 0,
	            layout:'border',  
	            items: [
	            	leftpanel,employpaymuster_me.centerpanel
	            ],
	            tbar:toolbar_data
	        });
			if(employpaymuster_me.callBackFunc){
	            Ext.callback(eval(employpaymuster_me.callBackFunc),null,[employpaymuster_me.panel]);
			}
		}
	},
	searchEmployOK:function(){
		var store = Ext.data.StoreManager.lookup('employeepaymuster1_dataStore');
		store.currentPage=1;
		store.load();
		var customMap = new HashMap();
    	customMap.put('isSearch','true');
    	employpaymuster_me.SearchBox.setCustomParams(customMap);
	},
	outOk:function(form,action){
		 Ext.MessageBox.close();
		 var responseText = Ext.decode(form.responseText);
		 var returnObj = Ext.decode(responseText.returnStr);
         var result = returnObj.return_data;
		 var filename=result.fileName; 
		 filename=getDecodeStr(filename);
		 window.location.target="_blank";
         window.location.href="/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true";
	},
	addGridListens:function(grid){
       //复选框初始选中
       grid.tablePanel.getStore().on('load',function(store,records){
           var selectModel = grid.tablePanel.getSelectionModel();
           var arrRecords=new Array();
           if(records.length > 0){
        	   arrRecords.push(records[0]);
               selectModel.select(arrRecords,false,true);
        	   employpaymuster_me.personListCurRecord = records[0];
               var object_id = records[0].get('objectid_e');
               /*var a0101 = records[0].get('a0101');
               var e0122 = records[0].get('e0122');
               if(e0122&&e0122!=''){
            	   e0122 = e0122.split("`")[1];
               }
               var uniqueid = records[0].get('uniqueid');
               uniqueid = uniqueid==''?'':"("+uniqueid+")";
               var labelvalue = e0122!=''?e0122+"："+a0101+uniqueid:a0101+uniqueid;
               Ext.getCmp("label_usr").setText(labelvalue);*/
               employpaymuster_me.switchPerson(object_id);
            }
            else{
            	employpaymuster_me.switchPerson("");
            	//Ext.getCmp("label_usr").setText("");
            }
       },employpaymuster_me);
       
       //行单击事件
       grid.tablePanel.on('itemclick',function(obj,record,item,index){
    	   employpaymuster_me.personListCurRecord=record;
	       var objectid = record.data.objectid_e;
	       /*var a0101 = record.data.a0101;
           var e0122 = record.data.e0122;
           if(e0122&&e0122!=''){
        	   e0122 = e0122.split("`")[1];
           }
           var uniqueid = record.data.uniqueid;
           uniqueid = uniqueid==''?'':"("+uniqueid+")";
           var labelvalue = e0122!=''?e0122+"："+a0101+uniqueid:a0101+uniqueid;
           Ext.getCmp("label_usr").setText(labelvalue);*/
	       employpaymuster_me.switchPerson(objectid);
       },grid.tablePanel);
       
       grid.tablePanel.on('select',function( obj, record, item, index, e, eOpts ){
    	   if(employpaymuster_me.keyCode==38||employpaymuster_me.keyCode==40){
    		   employpaymuster_me.personListCurRecord=record;
    	       var objectid = record.data.objectid_e;
    	       employpaymuster_me.switchPerson(objectid);
    	   }
       });
   },
   switchPerson:function(objectid){
	   var map = new HashMap();
		map.put("year",employpaymuster_me.tYear);
		map.put("objectid",objectid);
		map.put("rsid",employpaymuster_me.rsid);
		map.put("rsdtlid",employpaymuster_me.rsdtlid)
		map.put("transType","2");
		map.put("fielditemid",'');
		map.put("fielditemvalue",'');
		map.put("intoflag",'1');
		Rpc({functionId:'GZ00000706',async:false,success:employpaymuster_me.getTableDataOk,scope:employpaymuster_me},map);
   },
   getTableDataOk:function(form,action){
	    var responseText = Ext.decode(form.responseText);
	    var returnObj = Ext.decode(responseText.returnStr);
        var result = returnObj.return_data;
		var flag = result.flag;
		if(flag=='1'){
			var store = Ext.data.StoreManager.lookup('employeepaymusterdata1_'+employpaymuster_me.rsdtlid+'_dataStore'); 
			store.currentPage=1;
			store.load();
		}else{
			var dataListObj = result.tableConfig;	
			employpaymuster_me.dataListGrid = new BuildTableObj(dataListObj);
			employpaymuster_me.addDataListGridListens(employpaymuster_me.dataListGrid);
			employpaymuster_me.datamainPanel = employpaymuster_me.dataListGrid.getMainPanel();
			employpaymuster_me.centerpanel = Ext.create('Ext.panel.Panel',{
				region:'center',
				width:'100%',
				margin:'-2 0 0 -2',
				border:0,
				layout:'fit',
				items:[employpaymuster_me.datamainPanel]
			})
		}
	},
	addDataListGridListens:function(grid){
		grid.tablePanel.on('columnresize',function(ct, column, width, eOpts){
			var dataIndex = column.dataIndex;//修改的列codeitemid
			var map = new HashMap();
			map.put("codeitemid",dataIndex);
			map.put("submoduleid","employeepaymusterdata_"+employpaymuster_me.rsdtlid);
			map.put("width",width+"");
			map.put("isshare","0");//0 私有方案 1共有方案
			map.put("transType","6");
			Rpc({functionId:'GZ00000706',async:false,success:function(){},scope:employpaymuster_me},map);
		});
		grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
			employpaymuster_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			employpaymuster_me.saveColumnMove(grid, column);
		});
	},
	switchyear:function(){
		var map = new HashMap();
		map.put("year",employpaymuster_me.tYear);
		map.put("rsid",employpaymuster_me.rsid);
		map.put("rsdtlid",employpaymuster_me.rsdtlid)
		map.put("transType","1");
		map.put("intoflag",'1');
		Rpc({functionId:'GZ00000706',async:false,success:employpaymuster_me.getTableOK,scope:employpaymuster_me},map);
	},
	getorgtree:function(){
		var map = new HashMap();
	    map.put('codesetidstr','UN,UM');
		map.put('codesource','');
		map.put('nmodule','1');
		map.put('ctrltype','3');
		map.put('parentid','');
		map.put('searchtext',encodeURI(""));
		map.put('multiple',true);
		map.put('isencrypt',true);
		map.put('confirmtype','1');
		map.put('expandTop',true);
		map.put('limitSelectNum',6);
		map.put('callbackfunc',employpaymuster_me.getOrgList);
		//得到查询框的查询项
		var currentids = ""; 
		employpaymuster_me.SearchBox.queryKeyPanel.items.each(function(c){
        	if(employpaymuster_me.orgcodeidsMap.hasOwnProperty(c.value)){
        		var id = employpaymuster_me.orgcodeidsMap[c.value];
        		currentids+="`"+id;
        	}
        });
		employpaymuster_me.orgcodeids = currentids;
		map.put('checkedcodeids',employpaymuster_me.orgcodeids);
		Ext.require('EHR.orgTreePicker.OrgTreePicker', function(){          
			Ext.create('EHR.orgTreePicker.OrgTreePicker',{map:map});
		},this);
	},
	getOrgList:function(record){
		var staffids = "";
		var showTexts = "";
    	for(var i=0;i<record.length;i++){
    		staffids += record[i].id +"`";
    		showTexts += record[i].text +"`";
    		//记录单位部门对应的id
    		employpaymuster_me.orgcodeidsMap.put(record[i].text,record[i].id);
    	}
    	var customMap = new HashMap();
    	customMap.put('isSearch','false');
    	employpaymuster_me.SearchBox.setCustomParams(customMap);
    	//给查询控件回显
    	employpaymuster_me.SearchBox.showQueryKey(showTexts);
    	employpaymuster_me.orgcodeids = staffids;
    	var map = new HashMap();
    	map.put('subModuleId',employpaymuster_me.subModuleId);
 		map.put('type','3');
 		map.put('ids',staffids);
    	Rpc({functionId:'GZ00000706',async:false,success:employpaymuster_me.searchEmployOK,scope:employpaymuster_me},map);
	},
	getMainView:function(){
		return employpaymuster_me.panel;
	},
	/**
     * 设置取数范围
     */
    setRange:function(){
        //将页面作为窗口展现出来
        var panel = Ext.create("Analysistable.OptAnalysisTable",{
                opt:3,
                imodule:employpaymuster_me.imodule,
                rsid:employpaymuster_me.rsid_d,
                rsdtlid:employpaymuster_me.rsdtlid_d,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    employpaymuster_me.getSetRangeYear();
                }
            });
        OptAnalysisTable_me.setRange();
    },
    getSetRangeYear:function(){
    	employpaymuster_me.tYear = "";
    	var map = new HashMap();
		map.put("year",employpaymuster_me.tYear);
		map.put("rsid",employpaymuster_me.rsid);
		map.put("rsdtlid",employpaymuster_me.rsdtlid)
		map.put("transType","5");
		Rpc({functionId:'GZ00000706',async:false,success:employpaymuster_me.getTableOK_setRange,scope:employpaymuster_me},map);
    },
    getTableOK_setRange:function(form,action){
    	var responseText = Ext.decode(form.responseText);
	    var returnObj = Ext.decode(responseText.returnStr);
        var result = returnObj.return_data;
        var yearselectjson = result.yearselectjson;
        var yearStore = Ext.data.StoreManager.lookup("selectStore");
        yearStore.loadData(yearselectjson);
        var yearCombo = Ext.getCmp("selectPanel");
        var count = yearStore.getCount();
        if(count>0){
        	yearCombo.select(yearStore.data.items[0]);
        	employpaymuster_me.tYear = yearStore.data.items[0].id;
        }else{
        	yearCombo.select("");
        	employpaymuster_me.tYear = "-1";
        }
        employpaymuster_me.switchyear();
    },
    /**
     * 调用页面设置控件
     */
    showpagesetting:function(){
        var map = new HashMap();
        map.put("rsid",employpaymuster_me.rsid);
        map.put("rsdtlid",employpaymuster_me.rsdtlid);
        map.put("opt","1");
        map.put("transType","4");
        Rpc({functionId : 'GZ00000706',success: function(form){
            var result = Ext.decode(form.responseText);
            Ext.create("EHR.exportPageSet.ExportPageSet",{rsid:employpaymuster_me.rsid,rsdtlid:employpaymuster_me.rsdtlid,
            	result:result,callbackfn:'employpaymuster_me.savePageSet'});
        }}, map);
    },
    /**
     * 保存页面设置
     * @param pagesetupValue
     * @param titleValue
     * @param pageheadValue
     * @param pagetailValue
     * @param textValueValue
     * @param type
     */
    savePageSet:function(pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue,type) {
        var map = new HashMap();
        map.put("rsid",employpaymuster_me.rsid);
        map.put("rsdtlid",employpaymuster_me.rsdtlid);
        map.put("opt","2");
        map.put("transType","4");
        map.put("pagesetupValue",pagesetupValue);
        map.put("titleValue",titleValue);
        map.put("pageheadValue",pageheadValue);
        map.put("pagetailidValue",pagetailValue);
        map.put("textValueValue",textValueValue);
        Rpc({functionId : 'GZ00000706',success: function(form){
            var result = Ext.decode(form.responseText);
        }}, map);
    },
    getNameDesc:function(value, metaData, Record){
    	var a0101 = Record.data.a0101;
		var b0110 = Record.data.b0110;
		var e0122 = Record.data.e0122;
		if(e0122!=''){
			b0110 = b0110+'/'+e0122;
		}
		var photo = Record.data.photo;
		var html="<div style='width: 100%;height:50px;'>"+
		"<div style='margin-top:5px;;width:40px;height:40px;;float:left;border-radius:50%; overflow:hidden;'><img style='width:40px;height:40px;' src='"+photo+"'></img></div>"+//图片
		"<div style='width:75%;height:100%;float:right;margin-top:0px'>"+
		"<div style='width:100%;height:40%;margin-top:10px'>"+"<font style='color:#434343;;font-weight:bold;font-family:宋体;font-size:11pt;'>"+a0101+"</font></div>"+//姓名
		"<div style='width:100%;height:40%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;'><font color='#555050' >"+b0110+"</font></div>"+//单位部门
		"</div></div>";
		return html;
    },
    
	// 调整顺序
	saveColumnMove:function(grid, column) {
		var tablePanel = grid.tablePanel;
		var is_lock = column.isLocked()?'1':'0';
		var index = tablePanel.getColumnManager().getHeaderIndex(column);
		var nextcolumn = tablePanel.getColumnManager().getHeaderAtIndex(index+1);
		var nextid = "-1";
		if(nextcolumn && nextcolumn.dataIndex)
			nextid = nextcolumn.dataIndex;
		
	    var map = new HashMap();
	    map.put("submoduleid","employeepaymusterdata_"+employpaymuster_me.rsdtlid);
	    map.put("nextid", nextid);
	    map.put("transType", "7");
	    map.put("is_lock", is_lock);
	    map.put("itemid", column.dataIndex);
	    map.put("rsid", employpaymuster_me.rsid);
	    map.put("rsdtlid", employpaymuster_me.rsdtlid);
	    Rpc({
	        functionId: 'GZ00000706', async: true, success: function (res) {
	        	
	        },
	        scope: this
	    }, map);
	}
})