/**
 * 工作日历
 */
Ext.define('CalendarURL.Calendar',{
	
    constructor:function(config){
    	calendar = this;
    	//公休日
    	calendar.week = null;
    	//节假日
    	calendar.holiday = null;
    	//公休日倒休
    	calendar.turn_rest = null;
    	calendar.holidayList=new Array();
    	calendar.turn_restList=new Array();
    	calendar.weekString="";
    	calendar.kq_year="";
    	calendar.year=null;
    	calendar.descWidth = 150;
    	calendar.fileObj=null;
    	calendar.weekMap = {"1":"星期一","2":"星期二","3":"星期三","4":"星期四","5":"星期五","6":"星期六","7":"星期日"};
    	this.getYear();
    	this.init();
    },
  
    init: function(){
    	var json = {};
		json.kq_year = calendar.kq_year+""; 
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ000205401',success:calendar.loadeOK},map);
    },
    initCofig: function(){
    	var json = {};
		json.kq_year = calendar.kq_year+""; 
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ000205401',success:calendar.loadeData},map);
    },
    loadeData: function(response){
    	var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var jsonObj = Ext.decode(map.returnStr);
			if(jsonObj.return_code == "fail"){
				Ext.showAlert(jsonObj.return_msg);
				return;
			}
			var jsonData = jsonObj.return_data;
			//公休日
	    	calendar.week =  jsonData.week;
	    	//节假日
	    	calendar.holiday =  jsonData.holiday;
	    	//公休日倒休
	    	calendar.turn_rest =  jsonData.turn_rest;
	    	Ext.getDom("kqYear").innerHTML=calendar.kq_year;
	    	var holidayGird=Ext.getCmp("HolidayGird");
			var holidayStore=Ext.create('Ext.data.Store', {
			       fields: ['feast_id','feast_name','sdate'],
				   data: calendar.holiday,
			});
			holidayGird.setStore(holidayStore);
			var turnGird=Ext.getCmp("turnGird");
			var turnStore=Ext.create('Ext.data.Store', {
			       fields: ['feast_id','feast_name','sdate'],
				   data: calendar.turn_rest,
			});
			turnGird.setStore(turnStore);
		}
    },
    loadeOK: function(response){
    	var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var jsonObj = Ext.decode(map.returnStr);
			if(jsonObj.return_code == "fail"){
				Ext.showAlert(jsonObj.return_msg);
				return;
			}
			var jsonData = jsonObj.return_data;
			//公休日
	    	calendar.week =  jsonData.week;
	    	//节假日
	    	calendar.holiday =  jsonData.holiday;
	    	//公休日倒休
	    	calendar.turn_rest =  jsonData.turn_rest;
	    	var myCheckboxItems=[];
	        for (var key in calendar.weekMap) {
	                var name = calendar.weekMap[key];
	                var boxLabel = name;
	                if ((","+calendar.week+",").indexOf(","+key+",") != -1){
	                    myCheckboxItems.push({
	                        boxLabel: boxLabel,
	                        name: name,
	                        checked: true
	                    });
	                } else {
	                    myCheckboxItems.push({
	                        boxLabel: boxLabel,
	                        name: name,
	                        checked: false
	                    });
	                }
	    	}
	        myCheckboxGroup = new Ext.form.CheckboxGroup({
		        xtype : 'checkboxgroup',
		        id: 'checkboxgroup',
		        margin:'10 0 20 45',
		        width: 600, 
		        columns : 9,
		        items : myCheckboxItems,
		        listeners: {
		        	change : function (obj, ischecked) {
		        		var week="";
		        		 for (var checkName in ischecked) {
		        			 for (var key2 in calendar.weekMap) {
				 	                var name = calendar.weekMap[key2];
				 	                	if (name==checkName) {
				 	                		week +=key2+",";
										}
				 	                }
		        		 }
		        		 calendar.weekString =week;
                    }
		        }
		    });
	        var HoildayStore=calendar.getHoildayData();
	        var TurnDataStore=calendar.getTurnData();
	        // 58951 目前切换规则  向前到2000年隐藏 向前切换  向后切换到当前年后两年时 隐藏 向后切换
	        var titleStr = '<div style="float:left;font-size:20px;color:#5190ce;padding:2px;font-weight:normal;">'
        	if(calendar.kq_year > 2000){
        		titleStr = titleStr	+ '<a id="downYear" style="font-size:20px;color:#5190ce;" href="javascript:void(0);" onclick="calendar.checkDownYear();">&nbsp;<&nbsp;</a>';
        	}
            titleStr = titleStr	+ '<span id="kqYear">'+calendar.kq_year+'</span>'+'  年';
            if(calendar.kq_year < (calendar.year+2)){
            	titleStr = titleStr	+ '<a id="upYear" style="font-size:20px;color:#5190ce;" href="javascript:void(0);" onclick="calendar.checkUpYear();">&nbsp;>&nbsp;</a>';
            }
        	titleStr = titleStr	+'</div>'
            		+'<div style="float:right;padding-right:10px;font-size:20px;padding:2px;">'
            		+'<a href="javascript:void(0);" onclick="calendar.save();" >'+kq.label.save+'</a>&nbsp;&nbsp;<a href="javascript:void(0);" onclick="calendar.importExcel();" >'+kq.calendar.importData+'</a></div>'; 
	        var formPanel = Ext.create('Ext.panel.Panel', {
	        	title: titleStr,
	        	region: 'center',
	        	autoScroll: true,
	        	bodyStyle:'overflow-x:hidden;',
        		layout: {
        			type: 'vbox'
        		},
        		border: 0,
        		items: [{
        			xtype:'panel',
        			width:'100%',
        			margin:'20 0 0 0',
        			border:0,
        			layout:{
        				type:'vbox'
        			},
        			items:[{
        				xtype:'component',
        				html:'<div style="font-size:14;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        					+kq.calendar.week+'</div>'
        			}
        			,myCheckboxGroup
        			]
        		},{// 节假日
        			xtype:'component',
        			html:'<div style="font-size:14;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        				+kq.calendar.holiday+'</div>',
        		},{
        			xtype:'panel',
        			id:"holiday",
        			width:'100%',
        			margin:'10 0 10 0',
        			border:0,
        			items:[
        				HoildayStore
        			]
        		}/*,{// 节假日添加
        			xtype:'component',
        			id:"add1",
        			width:30,
        			height:30,
        			margin:'0 0 0 55',
        			html:"<img style='cursor:pointer; width:30px' src='/images/new_module/add.png'  onMouseOver=\"this.src='/module/system/personalsoduku/images/add.png'\"  onMouseOut=\"this.src='/images/new_module/add.png'\" onclick=\"calendar.onAddHolidayRow()\" >", 
        		}*/
        		,{// 公休日倒休
        			xtype:'component',
        			margin:'20 0 0 0',
        			html:'<div style="font-size:14;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">'
        				+kq.calendar.turn_rest
        				+'</div>'
        		},{
        			xtype:'panel',
        			id:"turnRest",
        			width:'100%',
        			margin:'20 0 10 0',
        			border:0,
        			items:[
        				TurnDataStore
        			]
        		}/*,{// 公休日倒休添加
        			xtype:'component',
        			id:"add2",
        			width:30,
        			height:30,
        			margin:'0 0 50 55',
        			html:"<img style='cursor:pointer; width:30px;' src='/images/new_module/add.png' onMouseOver=\"this.src='/module/system/personalsoduku/images/add.png'\"  onMouseOut=\"this.src='/images/new_module/add.png'\" onclick=\"calendar.onAddTurnRow()\" >", 
        			
        		}*/],
        		renderTo: Ext.getBody()
	        });
	        
	        new Ext.Viewport({
	        	id:'viewportid',
	        	layout: 'border',
	        	items: [formPanel]
	        });
	        
		}else {
			Ext.showAlert(map.message);
		}
	},
	//获取节假日信息
	getHoildayData: function(){
		var data= calendar.holiday;
		var gridStore = Ext.create('Ext.data.Store', {
			       fields: ['feast_id','feast_name','sdate'],
				   data: calendar.holiday,
			});
		var gird=Ext.create('Ext.grid.Panel', {
			id:'HolidayGird',
		    store: gridStore,
		    stripeRows:true,
		    columnLines:true,
		    enableColumnResize:false,
		    columns: [{ 
                xtype: 'rownumberer',
                text: kq.calendar.number,
                align: 'center',
                width: 60,
                renderer:function(value, metaData, record, rowIndex, colIndex, store, view){
                    if(rowIndex==store.data.length-1){
                        view.grid.addListener(
                            'cellclick',function (table,td,cellIndex,record) {
                                var record = record.get("feast_id");
                                if(record == "-"){
                                    return false;
                                }
                            }
                        )
                        return "<a href=javascript:calendar.onAddHolidayRow()><img style='width: 20px;height: 20px' src='/module/gz/mysalary/images/org_add.png'></a>";
                    }
                    return rowIndex+1;
                }
             },
            {
                 text: kq.calendar.holidayName,
                 dataIndex: 'feast_name',
                 width: 130,
                 sortable: false,
                 menuDisabled: true,
                 editor: {
                	 allowBlank:true  
                 }
			},{
	            text: kq.calendar.holidayDate,
	            dataIndex: 'sdate',
	            width: 420,
	            sortable: false,
	        	 menuDisabled: true,
	        	 editor: {
	        		 allowBlank:true  
	                }
	    },{
            text: kq.calendar.edit,
            width: 120,
            value:kq.label.del,
            sortable: false,
        	 menuDisabled: true,
        	 align: 'center',
        	 renderer: function (value, meta, record) {
        		 if (record.data.feast_id != '-') {
        			 var html = '<a href="####" onclick="calendar.deleteHoilday()">删除</a>';
        			 return html;
				}
             }
	    
    }],
		    width: 732,	
		    margin:'5 0 0 50',
		    plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1,
                    listeners: {
                        'edit': function (editor, context, eOpts) {
                        	var feast_name=context.record.data.feast_name;
                        	var sdate=context.record.data.sdate;
                        	var id=context.record.data.feast_id;
                        	if (Ext.isEmpty(context.value)) {
								return;
							}
                        	if (context.field=='feast_name') {
                        		feast_name= context.value;
							}else{
								if(calendar.valideFeastDate(sdate)){
									sdate= context.value;
								}else{
									context.record.data.sdate=context.originalValue;
									context.record.commit();
									return;
								}
							}
                            if(context.originalValue!=context.value) {
                            	//节假日名称和日期都填上才保存
                        		if (!Ext.isEmpty(sdate)&&!Ext.isEmpty(feast_name)) {
									//调用后台
                            		var json = {};
                            		json.id =id;
                            		json.name = feast_name;
                            		json.kq_year = calendar.kq_year+"";
                            		json.dates = sdate;
                            		var map = new HashMap();
                            		map.put("holiday", JSON.stringify(json));
                            	    Rpc({functionId:'KQ000205403',success:function(form){
                            	    	 var result = Ext.decode(form.responseText);
                                         var returnStr = Ext.decode(result.returnStr);
                                         var return_code = returnStr.return_code;
                                         var return_msg = returnStr.return_msg;
                                         if (return_code=="success") {
                                        	 var map =new HashMap();
                                        	 if (id=="") {
                                        		 map.put("id","");
                                        		 map.put("name",feast_name);
                                        		 map.put("dates",sdate);
                                        		 map.put("state","1");
                                        		 calendar.holidayList.push(map);
											}else{
												var holiday;
												for(var i=0;i<calendar.holiday.length;i++){
													holiday = calendar.holiday[i];
	                                        		 if (id==holiday.feast_id) {
	                                        			var isHave=false;
	                 		                   			for (var j = 0; j < calendar.holidayList.length; j++) {
	                 		                   				map=calendar.holidayList[j];
	                 										if (id==map.get("id")) {
	                 											map.put("name",feast_name);
	                 											map.put("dates",sdate);
	                 											map.put("state","2");
	                 											isHave=true;
	                 										}
	                 									}
	                 		                   			 if (!isHave) {
	                 		                   				 map.put("id",id);
		                                            		 map.put("name",feast_name);
		                                            		 map.put("dates",sdate);
		                                            		 map.put("state","2");
		                                            		 calendar.holidayList.push(map);
	                 									}
	                                        			 
													}
	                                        	 }	
											}
                                        	 
										}else{
											if (return_msg=="1") {
												Ext.showAlert(kq.calendar.error.holidayIsHave);
											}else if(return_msg=="3"){
												Ext.showAlert(kq.calendar.error.holidaySame);
											}
											context.record.data.sdate=context.originalValue;
										}
                                         context.record.commit();
                            	    }},map);
								}
                            }else{
                            	return;
                            }
                        }
                    }
                })
            ],
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
					        	    
						       	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < (div.scrollHeight-1)){
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
		return gird;
	},
	//获取公休日倒休
	getTurnData: function(){
		var data= calendar.turn_rest;
		var gridStore = Ext.create('Ext.data.Store', {
			       fields: ['turn_id','week_date','turn_date'],
				   data: calendar.turn_rest,
			});
		var gird=Ext.create('Ext.grid.Panel', {
			id:'turnGird',
		    store: gridStore,
		    stripeRows:true,
		    columnLines:true,
		    columns: [{ 
                xtype: 'rownumberer',
                text: kq.calendar.number,
                align: 'center',
                width: 60,
                renderer:function(value, metaData, record, rowIndex, colIndex, store, view){
                    if(rowIndex==store.data.length-1){
                        view.grid.addListener(
                            'cellclick',function (table,td,cellIndex,record) {
                                var record = record.get("turn_id");
                                if(record == "-"){
                                    return false;
                                }
                            }
                        )
                        return "<a href=javascript:calendar.onAddTurnRow()><img style='width: 20px;height: 20px' src='/module/gz/mysalary/images/org_add.png'></a>";
                    }
                    return rowIndex+1;
                }
             },
            {
                 text:kq.calendar.weekDate,
                 dataIndex: 'week_date',
                 width: 180,
                 sortable: false,
                 align: 'center',
                 menuDisabled: true,
                 editor: {
                	  xtype: 'datefield',
                	 format:'Y-m-d',
                	 allowBlank:false,
                 }
			},{
	            text: kq.calendar.turnDate,
	            dataIndex: 'turn_date',
	            width: 220,
	            sortable: false,
	            align: 'center',
	        	 menuDisabled: true,
	        	 editor: {
	        		 xtype:'datefield',
	        		 allowBlank:false,
	        		 format:'Y-m-d',
	                }
	    },{
            text: kq.calendar.edit,
            width: 120,
            sortable: false,
            align: 'center',
        	 menuDisabled: true,
        	 renderer: function (value, meta, record) {
        		 if (record.data.turn_id != '-') {
        			 var html = '<a href="####" onclick="calendar.deleteTurnDate()">删除</a>';
        			 return html;
				}
             },
    }],
		    width: 582,	
		    margin:'5 0 0 50',
		    plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1,
                    listeners: {
                        'edit': function (editor, context, eOpts) {
                            var recordData = context.record.data;
                            var value= Ext.util.Format.date(context.value,'Y-m-d');
                            var originalValue = Ext.util.Format.date(context.originalValue,'Y-m-d');
                            var week_date=context.record.data.week_date;
                            var id=context.record.data.turn_id;
                        	var turn_date=context.record.data.turn_date;
                        	if (Ext.isEmpty(context.value)) {
								Ext.showAlert("不能为空");
								return;
							}
                        	if (context.field=='week_date') {
                        		week_date= value;
							}else{
								turn_date=value;
							}
                            if(originalValue!=value) {
                            	if (!Ext.isEmpty(week_date)&&!Ext.isEmpty(turn_date)) {
									//调用后台
                            		var json = {};
                            		json.id =id;
                            		json.week_date = week_date;
                            		json.turn_date = turn_date;
                            		var map = new HashMap();
                            		map.put("turnDate", JSON.stringify(json));
                            	    Rpc({functionId:'KQ000205405',success:function(form){
                            	    	 var result = Ext.decode(form.responseText);
                                         var returnStr = Ext.decode(result.returnStr);
                                         var return_code = returnStr.return_code;
                                         var return_msg = returnStr.return_msg;
                                         if (return_code=="success") {
                                        	 var map =new HashMap();
											 var turn_rest;
                            				 for (var j = 0; j < calendar.turn_restList.length; j++) {
                            					map=calendar.turn_restList[j];
                            					if(id!=map.get("id")&&turn_date==map.get("turn_date")&&"3"!=map.get("state")&&"0"!=map.get("state")){
                            						Ext.showAlert(kq.calendar.error.four);
                            						context.record.data["turn_date"]= "";
                            						context.record.commit();
                            						return;
                            					}
                            					if(id!=map.get("id")&&week_date==map.get("week_date")&&"3"!=map.get("state")&&"0"!=map.get("state")){
                            						Ext.showAlert(kq.calendar.error.three);
                            						context.record.data["week_date"]= "";
                            						context.record.commit();
                            						return;
                            					}
                            					
                            				}
                            				 if (id=="") {
                            					 var newMap =new HashMap();
                            					 newMap.put("id","add");
                            					 newMap.put("week_date",week_date);
                            					 newMap.put("turn_date",turn_date);
                            					 newMap.put("state","1");
                                        		 calendar.turn_restList.push(newMap);
                            				}else{
                            					var newMap =new HashMap();
                            					newMap.put("id",id);
                            					newMap.put("turn_date",turn_date);
                            					newMap.put("week_date",week_date);
                            					newMap.put("state","2");
                            					calendar.turn_restList.push(newMap);
                            					
                            				}
										}else{
											if (return_msg=="2") {
												Ext.showAlert(kq.calendar.error.two);
												context.record.data["week_date"]= "";
											}else if (return_msg=="3") {//公休日已经设置过倒休
												
												Ext.showAlert(kq.calendar.error.three);
												context.record.data["week_date"]= "";
											}else if (return_msg=="4") {//倒休日已经设置过倒休
												
												Ext.showAlert(kq.calendar.error.four);
												context.record.data["turn_date"]= "";
											}else if (return_msg=="5") {
												Ext.showAlert(kq.calendar.error.five);
												context.record.data["turn_date"]= "";
											}
											 context.record.commit();
										}
                            	    }},map);
								}
                            	
                            	context.record.data[context.field]= value;
                            }else{
                            	 context.record.data[context.field]= originalValue;
                            }
                            context.record.commit();
                        }
                    }
                })
            ] 
		});
		return gird;
	},
	// 节假日添加行
	onAddHolidayRow:function() {
		var count = Ext.getCmp("HolidayGird").store.data.length-1;
        var map = new HashMap();
        map.put("feast_id",0);
        map.put("feast_name","");
        map.put("sdate","");
        Ext.getCmp("HolidayGird").store.insert(count,map);
        var sel = Ext.getCmp("HolidayGird").getSelectionModel();
        sel.select(count,true);
	  },
	//公休日倒休 添加行
	onAddTurnRow:function() {
		var count = Ext.getCmp("turnGird").store.data.length-1;
        var map = new HashMap();
        map.put("turn_id",0);
        map.put("week_date","");
        map.put("turn_date","");
        Ext.getCmp("turnGird").store.insert(count,map);
        var sel = Ext.getCmp("turnGird").getSelectionModel();
        sel.select(count,true);
	  },
	//新增
    addFunc:function(store){
        var count = Ext.getCmp("turnGird").store.data.length-1;
        var map = new HashMap();
        map.put("turn_id",0);
        map.put("week_date","");
        map.put("turn_date","");
        Ext.getCmp("turnGird").store.insert(count,map);
        var sel = Ext.getCmp("turnGird").getSelectionModel();
        sel.select(count,true);
    },
   // 删除行
	  onDelRow:function () {
              Ext.Msg.confirm('系统提示','确定要删除？',function(btn){  
                  if(btn=='yes'){  
                      var sm = grid.getSelectionModel();  
                      var record = sm.getSelection()[0];  
                      store.remove(record);  
                  }  
              });  
   },
   //删除节假日
   deleteHoilday:function(){
	   Ext.Msg.confirm(kq.dataAppeal.tip,kq.calendar.askfordel,function(btn){  
           if(btn=='yes'){  
          	 //调用后台
        	   var grid=Ext.getCmp("HolidayGird");
          	   var sm = grid.getSelectionModel();  
               var record = sm.getSelection()[0];  
               var id=record.data.feast_id;
               var feast_name=record.data.feast_name;
           		var sdate=record.data.sdate;
               if (Ext.isEmpty(id)) {
            	   var isHave=false;
          			for (var j = 0; j < calendar.holidayList.length; j++) {
          				map=calendar.holidayList[j];
						if (feast_name==map.get("name")) {
							map.put("state","3");
							isHave=true;
						}
					}
            	   grid.store.remove(record);  
            	   grid.setStore(grid.store);
				}else{
					var holiday;
					for(var i=0;i<calendar.holiday.length;i++){
						holiday = calendar.holiday[i];
						if (id==holiday.feast_id) {
							var isHave=false;
                   			for (var j = 0; j < calendar.holidayList.length; j++) {
                   				var map=calendar.holidayList[j];
								if (id==map.get("id")) {
									map.put("state","3");
									isHave=true;
								}
							}
                   			 if (!isHave) {
                   				var map =new HashMap();
                   				map.put("id",id);
                   				map.put("name",holiday.feast_name);
                   				map.put("dates",holiday.sdate);
                   				map.put("state","3");
                   				calendar.holidayList.push(map);
							}
						}
					}	
	               	grid.store.remove(record);  
	            	grid.setStore(grid.store);
					/*var json = {};
					json.id =id;
					var map = new HashMap();
					map.put("holiday", JSON.stringify(json));
					Rpc({functionId:'KQ000205404',success:function(form){
						var result = Ext.decode(form.responseText);
						var returnStr = Ext.decode(result.returnStr);
						var return_code = returnStr.return_code;
						var return_msg = returnStr.return_msg;
						if (return_code=="success") {
							Ext.showAlert(kq.label.deleteSuccess);
							calendar.initCofig();
						}else{
							Ext.showAlert(kq.label.deleteFail);
						}
					}},map);*/
				}
              
           }  
       }); 
   },
   //删除公休日
   deleteTurnDate:function(){
	   Ext.Msg.confirm(kq.dataAppeal.tip,kq.calendar.askfordel,function(btn){  
           if(btn=='yes'){ 
        	   //调用后台
        	    var grid=Ext.getCmp("turnGird");
          	 	var sm =grid.getSelectionModel();  
                var record = sm.getSelection()[0];  
                var id=record.data.turn_id;
                var week_date=record.data.week_date;
           		var turn_date=record.data.turn_date;
                var isHave=false;
                if (id=="add") {
            	   for (var j = 0; j < calendar.turn_restList.length; j++) {
          				map=calendar.turn_restList[j];
						if (turn_date==map.get("turn_date")) {
							map.put("state","0");
							isHave=true;
						}
					}
              	 	grid.store.remove(record); 
              	 	grid.setStore(grid.store);
				}else{
					var map =new HashMap();
					var turn_rest;
					for(var i=0;i<calendar.turn_rest.length;i++){
						turn_rest = calendar.turn_rest[i];
               		 if (id==turn_rest.turn_id) {
                   			for (var j = 0; j < calendar.turn_restList.length; j++) {
                   				map=calendar.turn_restList[j];
								if (id==map.get("id")) {
									map.put("state","3");
									isHave=true;
								}
							}
                   			 if (!isHave) {
                   				 var newMap =new HashMap();
                   				 newMap.put("id",id);
                   				 newMap.put("turn_date",turn_rest.turn_date);
                   				 newMap.put("week_date",turn_rest.week_date);
                   				 newMap.put("state","3");
                           		 calendar.turn_restList.push(newMap);
							}
               			 
						}
                      }	
					grid.store.remove(record);  
	            	grid.setStore(grid.store);
				}
              
           }  
       });  
   },
	// 更改公休日
    updataWeekFunc:function(input, item_id){
    	var value = '0';
    	if(input.checked)
			value = '1';
    	
 	},
 	//校验节假日日期
   valideFeastDate: function(dates){
	
	   	var tag=true;   
	    var m=0;
	    var gg=0;
	    var tem,tes,www;
    
	    tem=dates.split(",");
	    if(tem[(tem.length-1)]==""||tem[(tem.length-1)]==null)
	    {
	      gg=tem.length-1;
	    }else{
	       gg=tem.length;
	    }
	    for(m=0;m<gg;m++)
	    {
	         www=tem[m].replace(".","-");
	    	    tes=www.replace(".","-");
	    	    var ver=Array();
	    	   
	    	  if(tem[m].length==3||tem[m].length==5||tem[m].length==4)
	    	  {
	    	  
	    	      ver=tes.split("-");
	    	    if(ver.length==2)
	    	    {
	    	        if(tem[m].length==3)
	    	       {
	    	           tag= calendar.checkDat("1999-0"+ver[0]+"-0"+ver[1]);
	    	           if(tag==false)
	                {
	                   return false;
	                }
	             }
	            if(tem[m].length==4&&ver[0].length==1)
	    	      {
	    	           tag= calendar.checkDat("1999-0"+ver[0]+"-"+ver[1]);
	    	          if(tag==false)
	               {
	                   return false;
	               }
	            }
	            if(tem[m].length==4&&ver[1].length==1)
	    	      {
	    	          tag= calendar.checkDat("1999-"+ver[0]+"-0"+ver[1]);
	    	          if(tag==false)
	               {
	                    return false;
	               }
	            }
	            if(tem[m].length==5)
	    	      {
	    	          tag= calendar.checkDat("1999-"+ver[0]+"-"+ver[1]);
	    	          if(tag==false)
	                {
	                    return false;
	                }
	            }
	          }else{
	        	  Ext.showAlert(kq.calendar.error.labor);
	    	       return false;
	          }
	    	 }
	    	 if(tem[m].length==8||tem[m].length==10||tem[m].length==9)
	    	 {
	    	      ver=tes.split("-");
	    	       
	    	     if(ver.length==3)
	    	     {
	    	        if(tem[m].length==8)
	    	       {
	    	           tag= calendar.checkDat(ver[0]+"-0"+ver[1]+"-0"+ver[2]);
	    	           if(tag==false)
	                {
	                  return false;
	                }
	             }
	             if(tem[m].length==9&&ver[1].length==1)
	    	       {
	    	           tag= calendar.checkDat(ver[0]+"-0"+ver[1]+"-"+ver[2]);
	    	           if(tag==false)
	                {
	                   return false;
	                 }
	             }
	             if(tem[m].length==9&&ver[2].length==1)
	    	       {
	    	           tag= calendar.checkDat(ver[0]+"-"+ver[1]+"-0"+ver[2]);
	    	           if(tag==false)
	                {
	                   return false;
	                }
	             }
	             if(tem[m].length==10)
	    	       {
	    	           tag= calendar.checkDat(ver[0]+"-"+ver[1]+"-"+ver[2]);
	    	           if(tag==false)
	                {
	                    return false;
	                }
	             }
	            }else{
	            
	            	Ext.showAlert(kq.calendar.error.labor);
	    	       return false;
	            }
	            
	    	 }
	    	  
	    	  if(tem[m].length!=8&&tem[m].length!=10&&tem[m].length!=9&&tem[m].length!=3&&tem[m].length!=5&&tem[m].length!=4&&tem[m].length!=0)
	    	  {
	    		  Ext.showAlert(kq.calendar.error.labor);
	    	    return false;
	    	  }
	    	  return true;
        
	   }
	},
	checkDownYear: function(){
		if (calendar.weekString.length>0||calendar.holidayList.length>0||calendar.turn_restList.length>0) {
			 Ext.Msg.confirm(kq.dataAppeal.tip,kq.calendar.askforSave,function(btn){  
		           if(btn=='yes'){
		        	   calendar.save();
		           }else{
		        	   calendar.holidayList=new Array();
		        	   calendar.turn_restList=new Array();
		        	   calendar.weekString="";
		           }
			 });
		}
		var kq_year=calendar.kq_year-1;
		if (kq_year<2000) {
			Ext.showAlert(kq.calendar.error.yearTooLow);
			return;
		}else{
			Ext.getDom("upYear").innerHTML="&nbsp;>&nbsp;";
		}
		calendar.kq_year=kq_year;
		calendar.initCofig();
	},
	checkUpYear: function(){
		if (calendar.weekString.length>0||calendar.holidayList.length>0||calendar.turn_restList.length>0) {
			 Ext.Msg.confirm(kq.dataAppeal.tip,kq.calendar.askforSave,function(btn){  
		           if(btn=='yes'){
		        	   calendar.save();
		           }else{
		        	   calendar.holidayList=new Array();
		        	   calendar.turn_restList=new Array();
		        	   calendar.weekString="";
		           }
			 });
		}
		var kq_year=calendar.kq_year+1;
		var year = calendar.year;
		if (kq_year>(year+1)) {
			Ext.getDom("upYear").innerHTML="";
		}
		calendar.kq_year=kq_year;
		calendar.initCofig();
		},
	getYear:function(){
		var nowDate=new Date()
		calendar.year=nowDate.getFullYear();
		calendar.kq_year=calendar.year
	},
	checkDat:function (str){
	    var ret=false;
	      var mm="";
	      var dd="";
	      var tem="";
	      var cc=0;
      var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/; 
      var r = str.match(reg); 
      if(r==null)
      {
    	  Ext.showAlert(kq.calendar.error.labor);
         return false; 
      }
       var d=new Date(r[1], r[3]-1,r[4]);
          dd=""+d.getDate();
          cc=d.getMonth()+1;
          mm=""+cc;
      if(mm.length==1&&dd.length==2)
      {
        tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+d.getDate();
      }
       if(dd.length==1&&mm.length==2)
      {
        tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+("0"+d.getDate());
      }
      if(dd.length==1&&mm.length==1)
      {
        tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+("0"+d.getDate());
      }
      if(dd.length==2&&mm.length==2)
      {
       tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+d.getDate();
      }
      // alert(tem+str);
       if(tem==str)
       {
           ret=true;
       }else{
    	   Ext.showAlert(kq.calendar.error.labor);
          return false;
       }
     return ret;
   } ,
 //下载模板、导入数据
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
		        align: 'center',
		        pack: 'center',
		        type: 'vbox'
			},
		    items: [{
		    	align: 'center',
			    border: false,
			    layout: 'column',
			    margin: '0 0 0 0',
			    width: 240,	
			    items: [{
			        columnWidth: 0.5,
			        border: false,
			        id:'importTxt',
			        html: kq.calendar.selectTxt,		        
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
		    }],
			 bbar: [ 
	                '->',
	                { xtype: 'button',style:'margin-right:5px;', text: kq.button.ok,width:75,height:25,handler:function(){
	                	var fileObj=calendar.fileObj;
	                	if (fileObj!=null) {
	                		Ext.MessageBox.wait("", kq.card.importMsg);	
	                		var map = new HashMap();
		        		    var json = {};
		        		    json.fileid =fileObj.fileid;
		        		    map.put("jsonStr",Ext.encode(json));
		        			Rpc({functionId : 'KQ000205407',
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
		        							Ext.getCmp('viewportid').destroy();
		        							var importWinid = Ext.getCmp("importWinid");
		        		                    if(importWinid){
		        		                    	importWinid.destroy();
		        		                    }
		        							calendar.init();
		        						}
		        					} 
		        				}
		        			}, map);
						}else{
							Ext.showAlert(kq.calendar.selectTxt);
						}
	        			
	                } },
	                { xtype: 'button', text: kq.button.cancle,width:75,height:25,handler:function(){
	                    var importWinid = Ext.getCmp("importWinid");
	                    if(importWinid){
	                    	importWinid.destroy();
	                    }
	                }},
	                '->'
	            ]
		});
		
	    importWin.show();
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
				fileExt:"*.txt;",
				buttonText:'',
				renderTo:"importCardData",
				success:calendar.importData,
				isDelete:true,
				width:32,
				height:20
			});
			Ext.getDom("importCardData").childNodes[1].style.marginTop = "-20px";
			calendar.fileObj=null;
		});
	},
	save:function(){
		if (calendar.weekString.length>0||calendar.holidayList.length>0||calendar.turn_restList.length>0) {
			//访问后台
			var json={}
			var map = new HashMap();
			json.week= calendar.weekString;
			json.holiday=calendar.holidayList;
			json.turn_rest=calendar.turn_restList;
			map.put("data", JSON.stringify(json));
			Rpc({functionId:'KQ000205402',success:function(form){
				var result = Ext.decode(form.responseText);
				var returnStr = Ext.decode(result.returnStr);
				var return_code = returnStr.return_code;
				var return_msg = returnStr.return_msg;
				if (return_code=="success") {
					Ext.showAlert(kq.label.saveSuccess);
					calendar.holidayList=new Array();
			    	calendar.turn_restList=new Array();
			    	calendar.weekString="";
					calendar.initCofig();
				}else{
					Ext.showAlert(kq.label.saveFail);
				}
			}},map);
		}else{
			Ext.showAlert(kq.label.saveSuccess);
		}
	},
	importData: function (list) {
		if(list.length < 0)
			return;
		var obj = list[0];
		if(obj){
			calendar.fileObj=obj;
			Ext.getDom("importTxt").innerHTML=obj.localname;
			
		}
	},
	modifyTurnDateList: function(id,turn_date,week_date){
		var isHave=false;
		for (var j = 0; j < calendar.turn_restList.length; j++) {
			map=calendar.turn_restList[j];
			if(id!=map.get("id")&&turn_date==map.get("turn_date")&&"3"!=map.get("state")){
				Ext.showAlert(kq.calendar.error.four);
				isHave=true;
			}
			if(id!=map.get("id")&&week_date==map.get("week_date")&&"3"!=map.get("state")){
				Ext.showAlert(kq.calendar.error.three);
				isHave=true;
			}
			
		}
		 if (!isHave) {
			 map.put("id",id);
			 map.put("turn_date",turn_date);
			 map.put("week_date",week_date);
			 map.put("state","2");
			 calendar.turn_restList.push(map);
		}
    			 
	}
});
