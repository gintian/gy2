/**
 * 定义数据标准js
 */
Ext.define('SetupschemeUL.DefiningDataStandards',{
	 defineButtons : undefined,
	 separationLine:undefined,//分隔线
	 selectField : undefined,//左侧数选择指标
	 sequiredField:undefined,
	 arr :undefined,//声明空数组 用于存取修改后的指标
	 photoCheck:undefined,//选择是否上传照片存到photoCheck
	 fileds:'',//选择的指标存到fileds
	 dblist:undefined,//从后台查的人员库dblist
	 protectDbname:undefined,//保护人员条件的人员库
	 protectPeople:undefined,//第一个受保护人员条件
	 protectPeopleFieldOne:undefined,//第一个受保护指标条件
	 protectPeopleFieldtwo:undefined,//第二受保护指标id以“/”分隔
	 protectPeopleFieldtwoValue:undefined,//第二受保护指标id以“/”分隔
	 peopleCheckbox:undefined,//保护人员条件复选框的值
	 peopleDbname : undefined,//保护人员数据库名称
	 fieldCheckbox:undefined,//保护指标条件复选框的值
	 fieldDbname : undefined,//保护指标数据库名称
	 dbnameRelationField : undefined,
	 dbnameRelationCodeitemid : undefined,
     constructor:function(config){//构造方法
    	definingDataStandards=this;
    	definingDataStandards.init(config);
     },
	 init:function(config){
		 definingDataStandards.defineDataSpecification();
	 },
	 /**
	  * 定义数据标准
	  */
	defineDataSpecification:function(){
		var map = new HashMap();
		//用户回写数据库中的数据
		Rpc({functionId:'SYS0000003010',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			var dbmap=result.dbmap;
			dblist=dbmap.list;
			protectDbname=result.peopleDbpre;//数据库保存的保护人员 人员库前缀
			peopleDbname=result.peopleDbname;//要显示的人员库值
			protectPeople=result.peopleCondition;//第一个受保护人员条件
			protectFieldDbname=result.fieldpre;//数据库保存的保护指标 人员库前缀
			fieldDbname=result.fieldDbname;//显示的数据库值
			protectPeopleFieldOne=result.protectPeopleFieldOne;//第一个受保护人员条件
			protectPeopleFieldtwo=result.protectPeopleFieldtwo;
			protectPeopleFieldtwoValue=result.protectPeopleFieldtwoValue;
			peopleCheckbox=result.peopleCheckbox;
			fieldCheckbox=result.fieldCheckbox;
			photoCheck=result.photoCheckbox;
			dbnameRelationField=result.personItemid;
		}},map);
		if(!Ext.getCmp('dataSceondWindow')){//如果不存在则创建
			definingDataStandards.caeatSecondWindow().show();
	    	if("true"==photoCheck){
	    		Ext.getCmp("photo").setValue(true);
	    	}
	    }else{//存在则显示
	        Ext.getCmp('dataSceondWindow').show();
	    }
	},
	//创建第一个窗口
	caeatSecondWindow:function(){
		var dataSceondWindow = undefined;
		if(!Ext.getCmp('dataSceondWindow')){
			selectField = definingDataStandards.creatSelectField();
			defineButtons = definingDataStandards.stepview(1);
			dataSceondWindow=Ext.create('Ext.window.Window', {
			    id:'dataSceondWindow',
			    title: "<div align='left' style='font-size:14px;'>"+define_data_specification+"</div>",
			    height: 510,
			    resizable : false,//禁止缩放
			    width: 700,
			    modal:true,
			    layout: 'vbox',
			    buttonAlign: 'center',
			    listeners: {
			    	beforeClose:beforeCloseSecond
			    },
			    items: [{width:700,height:40,border:true,bodyStyle:'border-width: 0 0 1px 0;border-style: solid;border-color: #c0c0c0;',items:defineButtons},
			    	selectField
			        ],
			    buttons:[{ 
				     text : next_step,
				     height: 22,
				     handler :SecondNextStep
				    }]
			});
		};
		var firstFileds=undefined;
		function SecondNextStep(){
	    	//获得上报人员照片的chachbox
			var flagb0110 = true;
			var flage0122 = true;
			var flage01a1 = true;
			var fieldName ='';
	    	photoCheck =Ext.getCmp("photo").getValue();
	    	//遍历右侧每一个
	    	fileds='';
	    	var jsonArray=[];
	    	parentNode = Ext.getCmp('rightTree').getRootNode();
	    	for(var i=0;i<parentNode.childNodes.length;i++){
			   var childrenArray = parentNode.childNodes[i].childNodes;
			   for(var j=0;j<childrenArray.length;j++){
				   fieldName=childrenArray[j].get('id').toUpperCase();
				   if('E0122' == fieldName){
				    	flage0122 = false;
				   }
				   if('B0110'== fieldName){
				    	flagb0110 = false;
				   }
				   if('E01A1'== fieldName){
				    	flage01a1 = false;
				   }
				   jsonArray.push(fieldName);
			   }
			}
	    	fileds=jsonArray.join(',');
	    	if(firstFileds!=undefined){//证明不是第一次进来
				if(firstFileds!=fileds&&Ext.getCmp('dataThirdWindow')){//证明fileds发生了变化，将第三个window销毁
					windowNumber=10;
					Ext.getCmp('dataThirdWindow').close();
					if(Ext.getCmp('dataFourthWindow')){
						Ext.getCmp('dataFourthWindow').close();
					}
				}
			}
			firstFileds=fileds;
	    	if(flagb0110||flage0122||flage01a1){
				Ext.showAlert(three_indices_to_be_selected);
			}else{
			    dataSceondWindow.hide();
			    if(!Ext.getCmp('dataThirdWindow')){//如果不存在则创建
			    	definingDataStandards.creatThiedWindow().show();
			    }else{//存在则显示
			        Ext.getCmp('dataThirdWindow').show();
			    }
			}
	    };
	     function beforeCloseSecond(){
	    	if(windowNumber==undefined){
	    		windowNumber=2;
	    	}
	    	if(Ext.getCmp('dataFiveWindow')&&windowNumber!=3&&windowNumber!=4&&windowNumber!=5&&windowNumber!=10){//如果第四个窗口存在，先关闭第四个窗口
    			Ext.getCmp('dataFiveWindow').close();
    		}
	    	if(Ext.getCmp('dataFourthWindow')&&windowNumber!=3&&windowNumber!=4&&windowNumber!=5&&windowNumber!=10){//如果第四个窗口存在，先关闭第四个窗口
    			Ext.getCmp('dataFourthWindow').close();
    		}
    		if(Ext.getCmp('dataThirdWindow')&&windowNumber!=3&&windowNumber!=4&&windowNumber!=5&&windowNumber!=10){//如果第三个窗口存在，先关闭第三个窗口
    			Ext.getCmp('dataThirdWindow').close();
    		}
	    };
		return dataSceondWindow;
	},
	//创建的第三个窗口
	creatFourthWindow:function(){
		var dataFourthWindow = undefined;
		if(!Ext.getCmp('dataFourthWindow')){
			var defineDbnameTypePanel = definingDataStandards.createDefineDbnameTypePanel();
			var defineButtons = definingDataStandards.stepview(3);
			dataFourthWindow=Ext.create('Ext.window.Window', {
			    id:'dataFourthWindow',
			    title: "<div align='left' style='font-size:14px;'>"+define_data_specification+"</div>",
			    height: 510,
			    modal:true,
			    resizable : false,//禁止缩放
			    width: 700,
			    layout: 'vbox',
			    buttonAlign: 'center',
			    listeners: {
			    	 beforeClose: function (sender, handlers) {
				    	if(windowNumber==undefined){
				    		windowNumber=4;
				    	}
				    	if(Ext.getCmp('dataFiveWindow')&&windowNumber!=2&&windowNumber!=3&&windowNumber!=10&&windowNumber!=5){
			    			Ext.getCmp('dataFiveWindow').close();
			    		}
				    	if(Ext.getCmp('dataThirdWindow')&&windowNumber!=2&&windowNumber!=3&&windowNumber!=10&&windowNumber!=5){
			    			Ext.getCmp('dataThirdWindow').close();
			    		}
			    		if(Ext.getCmp('dataSceondWindow')&&windowNumber!=2&&windowNumber!=3&&windowNumber!=10&&windowNumber!=5){
			    			Ext.getCmp('dataSceondWindow').close();
			    		}
			    	 }
			    },
			    items: [
			    	{width:700,height:40,border:true,bodyStyle:'border-width: 0 0 1px 0;border-style: solid;border-color: #c0c0c0;',items:defineButtons},
			    	defineDbnameTypePanel
			        ],
			    buttons:[{ 
					     text : the_last_step,
					     height: 22,
					     handler :function(){
					     	Ext.getCmp('dataFourthWindow').hide();
					     	Ext.getCmp('dataThirdWindow').show();
					     }
				    },{ 
					     text : next_step,
					     height: 22,
					     handler :function(){
					    	 var dbnameFieldStore = Ext.getCmp('dbnameFieldGridPanel').getStore();
					    	 dbnameRelationCodeitemid=new Array();
					    	 for(var i=0;i<dbnameFieldStore.getCount();i++){
					    		 if(dbnameFieldStore.getAt(i).get('dbnamepre')==undefined){
					    			 Ext.Msg.alert(hint_information,please_complete_all_correspondences);
								     return;
					    		 }else{
					    			 var codeitemid = dbnameFieldStore.getAt(i).get('codeitemid');
					    			 var dbnamepre = dbnameFieldStore.getAt(i).get('dbnamepre');
					    			 dbnameRelationCodeitemid.push(codeitemid+"-"+dbnamepre);
					    		 }
					    	 }
					    	 Ext.getCmp('dataFourthWindow').hide();
					    	 if(Ext.getCmp('dataFiveWindow')){
					    		 Ext.getCmp('dataFiveWindow').show();
					    	 }else{
					    		 definingDataStandards.creatFiveWindow().show();
					    		 if("true"==peopleCheckbox){
								    document.getElementsByName("peopleCheckbox")[0].checked=true;   
								}
								if("true"==fieldCheckbox){
								   document.getElementById('fieldCheckbox').checked=true;   
								}
								if("undefined"!=peopleDbname){
								   Ext.getCmp("protectDbname").setValue(peopleDbname);
								}
								if("undefined"!=protectPeople){
								   Ext.getCmp("protectPeople").setValue(protectPeople);
								}
								if("undefined"!=fieldDbname){
								   Ext.getCmp("protectDbnameField").setValue(fieldDbname);
								}
								if("undefined"!=protectPeopleFieldOne){
								   Ext.getCmp("protectPeopleFieldOne").setValue(protectPeopleFieldOne);
								}
								if("undefined"!=protectPeopleFieldtwoValue){
								   Ext.getCmp("protectPeopleFieldtwo").setValue(protectPeopleFieldtwoValue);
								}
					    	 }
					     }
				    }]
			});
		};
		return dataFourthWindow;
	},
	//设置人员库映射
	createDefineDbnameTypePanel:function(){
		var defineDbnameTypePanel =Ext.create('Ext.panel.Panel', {
			  style: 'margin-top:1px;margin-left:10px',
			  layout: 'vbox',
			  height:380,
			  border:false,
			  width:'100%',
			  items:[{
						 xtype: 'container',
						 border:false	,
						 layout: 'hbox',
						 items:[{
							   xtype: 'container',
							   border:false	,
							   id:'fieldsetcombox'
						 },{
							 html: "<div style='font-family:'宋体';font-size:20px;color:green'>"+dbname_field_tips+"</div>",  
							 style: 'margin-bottom:0px;margin-top:5px;',
							 border:false
						 }]
						  
					  },{
						  xtype: 'container',
						  border:false,
						  id:'fieldgridpanel'
					  }]
			});
		//主集中的代码型指标下拉框的store
		var comBoxStore = Ext.create('Ext.data.Store',{
			id:'comBoxStoreCode',
            fields:['fieldName','fieldValue'],
            proxy:{
		       	type: 'transaction',
			    functionId:'SYS0000003029',
	            reader: {
	                type: 'json',
	                root: 'list'
	            },
	            extraParams:{
	            	fileds:fileds
				}
            }
        });
        //主集中的代码型指标下拉框
	   	var comBox = Ext.widget('combo',{
	   		    id:'comBoxCodeitem',
				store:comBoxStore,
				width:310,
			    queryMode: 'local',
			    hidden:false,
			    repeatTriggerClick : true,
			    editable: false,
			    fieldLabel:dbname_field,
			    forceSelection: true,
			    displayField: 'fieldName',//显示的值
			    valueField: 'fieldValue',//隐藏的值
			    listeners:{
	  				select:function(combo,records){
	  					dbnameRelationField=combo.getValue();
	  					fieldChangeSelect(combo.getValue());
					}
	   		}
	   	});
	   	comBoxStore.load();
	   	//下拉框初始化显示第一个
  		comBoxStore.on('load',function(store,records,options){
  			if(dbnameRelationField!=undefined&&dbnameRelationField!==null){
  				comBox.select(dbnameRelationField);
  				fieldChangeSelect(dbnameRelationField);
  			}
		});
	   	Ext.getCmp('fieldsetcombox').add(comBox);
	    //人员库的combobox的store
		var dbnameComboxStore = Ext.create('Ext.data.Store', {
				fields : ['dbnameCombox', 'dbnamepreCombox'],
				// 数据代理服务
				proxy : {
					type : 'transaction',
					functionId : 'SYS0000003031',
					reader : {
						type : 'json',
						root : 'list'
					}
				},
				// 自动加载
				autoLoad : true
			});
		//人员库的combobox
		var dbnameCombox = new Ext.form.ComboBox({
				store : dbnameComboxStore,
				displayField : 'dbnameCombox',//显示的值
				valueField : 'dbnamepreCombox',//隐藏的值
				mode : 'local',
				editable : true,
				triggerAction : 'all',//默认显示全部
				forceSelection: true,
				typeAhead:true,//模糊匹配
				listeners : {
					select : function(editor, e, eOpts) {
						var records=dbnameFieldGridPanel.getSelectionModel().getSelection();
						var record = records[0];
						record.set("dbnamepre",e.get("dbnamepreCombox"));
					}
				}
			});
	    //grid panel store
    	var dbnameFieldStore = Ext.create('Ext.data.Store', {
             fields:['codeitemid','codeitemdesc','dbname','dbnamepre'],
             proxy:{
            	 type: 'transaction',
			     functionId:'SYS0000003030',
                 reader: {
                     type: 'json',
                     root: 'list'
                 }
             }
         });
        //指标和人员库的映射关系的panel
	    var dbnameFieldGridPanel = Ext.create('Ext.grid.Panel', {
	    	id:"dbnameFieldGridPanel",
			store:dbnameFieldStore,
			width:670,
		 	height:343,
		 	border:true,
		 	scrollable:"y",
		 	bufferedRenderer:false,
		 	multiSelect:true,
		 	forceFit:true,
		 	columnLines:true,//显示grid.Panel数据列之间的竖线
		 	hideHeaders:false,
			plugins:[  
             	Ext.create('Ext.grid.plugin.CellEditing',{  
		                     clicksToEdit:1 //设置单击单元格编辑  
		        })  
		    ],
		    viewConfig: {　　 
				markDirty: false //不显示编辑后的三角
			},
			//selType: 'checkboxmodel',//添加复选框列
			columns: [
			    { text: '代码值', dataIndex: 'codeitemid'},
			    { text: '代码名称', dataIndex: 'codeitemdesc'},
			    { text: '人员库名称', 
			      dataIndex: 'dbname',
		    	  editor : dbnameCombox,
				  renderer : function(value, cellmeta, record) {
					var index = dbnameComboxStore.find(dbnameCombox.valueField, value);
					var ehrRecord = dbnameComboxStore.getAt(index);
					var returnvalue = "";
					if (ehrRecord) {
						returnvalue = ehrRecord.get('dbnameCombox');
					}
					return returnvalue;
				 }  
			   },
			    { text: '人员库代码', dataIndex: 'dbnamepre'}
			],
			renderTo:Ext.getBody()
		});
		Ext.getCmp('fieldgridpanel').add(dbnameFieldGridPanel);
		function fieldChangeSelect(fielditem){
			 dbnameFieldStore.load({
				 params:{
					 fielditem:fielditem,
					 fileds:fileds
			 	 }
			  }); 
	    }
		return defineDbnameTypePanel;
	},
	//创建第四个窗口
	creatFiveWindow:function(){
		var dataFiveWindow = undefined;
		if(!Ext.getCmp('dataFiveWindow')){
			var defineButtons = definingDataStandards.stepview(4);
			var protectionConditions = definingDataStandards.creatProtectionConditions();
			dataFiveWindow=Ext.create('Ext.window.Window', {
			    id:'dataFiveWindow',
			    title: "<div align='left' style='font-size:14px;'>"+define_data_specification+"</div>",
			    height: 510,
			    modal:true,
			    resizable : false,//禁止缩放
			    width: 700,
			    layout: 'vbox',
			    buttonAlign: 'center',
			    listeners: {
			    	 beforeClose: function (sender, handlers) {
				    	if(windowNumber==undefined){
				    		windowNumber=5;
				    	}
				    	if(Ext.getCmp('dataFourthWindow')&&windowNumber!=2&&windowNumber!=3&&windowNumber!=4&&windowNumber!=10){
			    			Ext.getCmp('dataFourthWindow').close();
			    		}
				    	if(Ext.getCmp('dataThirdWindow')&&windowNumber!=2&&windowNumber!=3&&windowNumber!=4&&windowNumber!=10){
			    			Ext.getCmp('dataThirdWindow').close();
			    		}
			    		if(Ext.getCmp('dataSceondWindow')&&windowNumber!=2&&windowNumber!=3&&windowNumber!=4&&windowNumber!=10){
			    			Ext.getCmp('dataSceondWindow').close();
			    		}
			    	 }
			    },
			    items: [
			    	{width:700,height:40,border:true,bodyStyle:'border-width: 0 0 1px 0;border-style: solid;border-color: #c0c0c0;',items:defineButtons},
			    	protectionConditions
			        ],
			    buttons:[{ 
					     text : the_last_step,
					     height: 22,
					     handler :function(){
					     	Ext.getCmp('dataFiveWindow').hide();
					     	Ext.getCmp('dataFourthWindow').show();
					     }
				    },{ 
					     text : sure,
					     height: 22,
					     handler :submintStep
				    }]
			});
		};
		function submintStep(){
			var peopleCheckbox=document.getElementsByName("peopleCheckbox")[0].checked;
			var fieldCheckbox=document.getElementsByName("fieldCheckbox")[0].checked;
			var map = new HashMap();
			map.put("dbnameRelationCodeitemid",dbnameRelationCodeitemid.join(","));//指标和人员库的对应
			if(null==dbnameRelationField){
				map.put("dbnameRelationField","");//对应人员库的代码型指标
			}else{
				map.put("dbnameRelationField",dbnameRelationField);//对应人员库的代码型指标
			}
			map.put("arr",arr.join(",").toUpperCase());//存选中的指标
			map.put("photoCheck",photoCheck==true?"true":"false");//是否上传照片
			map.put("peopleCheckbox",peopleCheckbox==true?"true":"false");//存是否启用保护人员条件
			map.put("protectDbname",protectDbname);//保护人员条件人员库
			map.put("protectPeople",protectPeople);//第一个受保护人员条件
			map.put("fieldCheckbox",fieldCheckbox==true?"true":"false");//存是否启用保护指标条件
			map.put("protectFieldDbname",protectFieldDbname);//保护指标条件人员库
			map.put("protectPeopleFieldOne",protectPeopleFieldOne);//第二个受保护人员条件
			map.put("protectPeopleFieldtwo",protectPeopleFieldtwo);//第三个受保护人员条件
			Rpc({functionId:'SYS0000003008',success: function(res){
				if(Ext.getCmp('dataFiveWindow')){
					Ext.getCmp('dataFiveWindow').close();
				}
				Ext.getCmp('setupscheme001_tablePanel').getStore().reload();
			}},map);
	    };
		return dataFiveWindow;
	},
	//创建保护条件Panel
	creatProtectionConditions:function(){
		var requiredField=Ext.create('Ext.panel.Panel', {
			  style: 'margin-top:1px',
			  height:400,
			  width:'100%',
			  border:false,
			  layout:'vbox',
		  	  items:[{
				    xtype: 'fieldset',
					height:140,
					width:'100%',
					title:"<div><input type='checkbox' name='peopleCheckbox' id='peopleCheckbox' />"+enabling_protection_personnel_conditions+"</div>",
					style: 'margin-left:9px;margin-right:9px;',
					layout:'vbox',
					items:[{
						xtype: 'panel',
						border:false,
						width:'100%',
						height:40,
						layout:'hbox',
						items:[{
							xtype:'textfield',
							fieldLabel: protected_personnel_pool,
							name:'protectDbname',
							width:'80%',
							readOnly:true,
							id:'protectDbname'
						},{
							xtype:'button',
							margin:'0 0 10 20',
							id:'dbnameButton',
							text:'...',
							handler:function(){
								if(!Ext.getCmp('peopleDbwindow')){
									var checkboxgroup = Ext.widget({
							   			xtype     : 'checkboxgroup',
										columns   : dblist.length>4?2:1,
										id:'checkboxdbValue',
										width     : 350,
										vertical  : true
							       	});
							       	Ext.each(dblist,function(obj,index){
							    		var checkbox = Ext.widget({
							    			xtype     : 'checkbox',
											boxLabel  : obj.dbname,
											name:'dbValue',
											checked   : protectDbname!=undefined&&protectDbname.indexOf(obj.pre)>-1?true:false,
							                inputValue: obj.pre,
							                id:obj.pre
							        	});
							    		checkboxgroup.add(checkbox);
							    	})
									var peopleDbwindow=	Ext.create('Ext.window.Window', {
									 	title: please_select_a_personnel_pool,
									    height:260,
									    modal:true,
									    id:'peopleDbwindow',
									    width:350,
									    border:false,
									    scrollable:"y",
									    items:[checkboxgroup],
									    buttonAlign: 'center',
									    buttons:[{
									    	 text : sure,
										     height: 22,
										     handler :function(){
										     	//CheckboxGroup取值方法    
										     	var dbnameValueName='';
										     	protectDbname=undefined;
										     	//Ext.getCmp('protectDbname').setValue(dbnameValueName);
									            for (var i = 0; i < checkboxgroup.items.length; i++){    
									                if (checkboxgroup.items.items[i].checked){ 
									                	if(dbnameValueName!=''){
									                		dbnameValueName+=',';
									                	}
									                	if(protectDbname!=undefined){
									                		protectDbname+=',';
									                	}else{
									                		protectDbname='';
									                	}
									                    dbnameValueName+=checkboxgroup.items.items[i].boxLabel;
									                    protectDbname+=checkboxgroup.items.items[i].inputValue;   
									                }    
									            }
									            Ext.getCmp('protectDbname').setValue(dbnameValueName);
										     	peopleDbwindow.hide();
										     }
									    }]
									}).show();
								}else{
									Ext.getCmp('peopleDbwindow').show();
								}
							}
						}]
					},{
						xtype: 'panel',
						border:false,
						width:'100%',
						height:90,
						//style: 'margin-top:9px',
						layout:'hbox',
						items:[{
							xtype:'textarea',
							fieldLabel: conditions_of_protected_personnel,
							name:'protectPeople',
							readOnly:true,
							height:70,
							width:'80%',
							id:'protectPeople'
						},{
							xtype:'button',
							margin:'0 0 0 20',
							id:'peopleButton',
							text:'...',
							handler:function(){
								var dataMap = new HashMap();
								if(Ext.getCmp('protectPeople').getValue()!=undefined){
									dataMap.put("express",Ext.getCmp('protectPeople').getValue());
								}
					         	Ext.require('EHR.complexcondition.ComplexCondition',function(){
					         		Ext.create("EHR.complexcondition.ComplexCondition",{dataMap:dataMap,imodule:"3",opt:"1",title:retrieval_condition,callBackfn:saveCond});
					         	});
							}
						}]
					}]
				},{
					xtype: 'fieldset',
				    height:220,
				    width:'100%',
				    title:"<div><input type='checkbox' name='fieldCheckbox' id='fieldCheckbox' />"+enabling_protection_criteria+"</div>",
				    style: 'margin-left:9px;margin-right:9px;',
				    layout:'vbox',
				    items:[{
						xtype: 'panel',
						border:false,
						width:'100%',
						height:30,
						layout:'hbox',
						items:[{
							xtype:'textfield',
							fieldLabel: protected_personnel_pool,
							name:'protectDbnameField',
							readOnly:true,
							width:'80%',
							id:'protectDbnameField'
						},{
							xtype:'button',
							margin:'0 0 10 20',
							id:'dbnameButtonField',
							text:'...',
							handler:function(){
								if(!Ext.getCmp('peopleDbFieldwindow')){
									var checkboxgroup = Ext.widget({
							   			xtype     : 'checkboxgroup',
										columns   : dblist.length>4?2:1,
										//id:'checkboxdbValue',
										width     : 350,
										vertical  : true
							       	});
							       	Ext.each(dblist,function(obj,index){
							    		var checkbox = Ext.widget({
							    			xtype     : 'checkbox',
											boxLabel  : obj.dbname,
											name:'dbValue',
											checked   : protectFieldDbname!=undefined&&protectFieldDbname.indexOf(obj.pre)>-1?true:false,
							                inputValue: obj.pre
							                //id:obj.pre
							        	});
							    		checkboxgroup.add(checkbox);
							    	})
									var peopleDbFieldwindow=Ext.create('Ext.window.Window', {
									 	title: please_select_a_personnel_pool,
									 	modal:true,
									    height:260,
									    id:'peopleDbFieldwindow',
									    width: 350,
									    border:false,
									    scrollable:"y",
									    items:[checkboxgroup],
									    buttonAlign: 'center',
									    buttons:[{
									    	 text : sure,
										     height: 22,
										     handler :function(){
										     	//CheckboxGroup取值方法    
										     	protectFieldDbname=undefined;
										     	var dbnameValueName='';
										     	//Ext.getCmp('protectDbnameField').setValue(dbnameValueName);
									            for (var i = 0; i < checkboxgroup.items.length; i++){    
									                if (checkboxgroup.items.items[i].checked){ 
									                	if(dbnameValueName!=''){
									                		dbnameValueName+=',';
									                	}
									                	if(protectFieldDbname!=undefined){
									                		protectFieldDbname+=',';
									                	}else{
									                		protectFieldDbname='';
									                	}
									                    dbnameValueName+=checkboxgroup.items.items[i].boxLabel; 
									                    protectFieldDbname+=checkboxgroup.items.items[i].inputValue; 
									                }    
									            }
									            Ext.getCmp('protectDbnameField').setValue(dbnameValueName);
										     	peopleDbFieldwindow.hide();
										     }
									    }]
									}).show();
								}else{
									Ext.getCmp('peopleDbFieldwindow').show();
								}
							}
						}]
					},{
						xtype: 'panel',
						border:false,
						width:'100%',
						height:70,
						//style: 'margin-top:9px',
						layout:'hbox',
						items:[{
							xtype:'textarea',
							fieldLabel: conditions_of_protected_personnel,
							readOnly:true,
							name:'protectPeopleFieldOne',
							height:70,
							width:'80%',
							id:'protectPeopleFieldOne'
						},{
							xtype:'button',
							margin:'0 0 0 20',
							id:'peopleButtonFieldOne',
							text:'...',
							handler:function(){
								var map = new HashMap();
								if(protectPeopleFieldOne!=undefined){
									if("undefined"==protectPeopleFieldOne){
										protectPeopleFieldOne="";
									}
									map.put("express",protectPeopleFieldOne);
								}
					         	Ext.require('EHR.complexcondition.ComplexCondition',function(){
					         		Ext.create("EHR.complexcondition.ComplexCondition",{dataMap:map,imodule:"3",opt:"1",title:retrieval_condition,callBackfn:saveCondFieldOne});
					         	});
							}
						}]
					},{
						xtype: 'panel',
						border:false,
						width:'100%',
						height:70,
						style: 'margin-top:9px',
						layout:'hbox',
						items:[{
							xtype:'textarea',
							fieldLabel: protected_field_conditions,
							readOnly:true,
							name:'protectPeopleFieldtwo',
							height:70,
							width:'80%',
							id:'protectPeopleFieldtwo'
						},{
							xtype:'button',
							margin:'0 0 0 20',
							id:'peopleButtonFieldtwo',
							text:'...',
							handler:function(){
		                    	 var map = new HashMap();
								 map.put(1, "a,b,k");//要显示的指标集
								 //map.put(2, "a,b,k,y,v,w");//2表示要排除的指标集
								 if("undefined" != protectPeopleFieldtwo&&""!=protectPeopleFieldtwo){
								 	  var map1 = new HashMap();
								 	  map1.put("protectPeopleFieldtwo",protectPeopleFieldtwo);
									  Rpc({functionId:'SYS0000003011',success: function(form,action){
											var result = Ext.decode(form.responseText);
											Ext.require('SetupschemeUL.SelectField',function(){
												 Ext.create("SetupschemeUL.SelectField",{excludeFields:protectPeopleFieldtwo,rightDataList:result.rightDataList,comBoxDataInfoMap:map,title:add_field,saveCallbackfunc:saveFields});
											 })
									  }},map1);
								 }else{
									 Ext.require('SetupschemeUL.SelectField',function(){
										 Ext.create("SetupschemeUL.SelectField",{comBoxDataInfoMap:map,title:add_field,saveCallbackfunc:saveFields});
									 })
								 }
								 
							}
						}]
					}]
			  }]
				 
		});
		function saveCond(c_expr){
			Ext.getCmp('protectPeople').setValue(getDecodeStr(c_expr));
			protectPeople=getDecodeStr(c_expr);
		}
		function saveCondFieldOne(c_expr){
			Ext.getCmp('protectPeopleFieldOne').setValue(getDecodeStr(c_expr));
			protectPeopleFieldOne=getDecodeStr(c_expr);
		}
		function saveFields(fields){
			 var map = new HashMap();
			 map.put("fields",fields);
			 Rpc({functionId:'SYS0000003007',success: function(form,action){
					var result = Ext.decode(form.responseText);
					Ext.getCmp('protectPeopleFieldtwo').setValue(result.itemdesc);
					protectPeopleFieldtwo=result.fields;
			}},map);
		}
		return requiredField;
	},
	//创建第二个窗口
	creatThiedWindow:function(){
		var dataThirdWindow = undefined;
		if(!Ext.getCmp('dataThirdWindow')){
			var defineButtons = definingDataStandards.stepview(2);
			var sequiredField = definingDataStandards.creatRequiredField();
			dataThirdWindow=Ext.create('Ext.window.Window', {
			    id:'dataThirdWindow',
			    title: "<div align='left' style='font-size:14px;'>"+define_data_specification+"</div>",
			    height: 510,
			    resizable : false,//禁止缩放
			    width: 700,
			    modal:true,
			    layout: 'vbox',
			    buttonAlign: 'center',
			    listeners: {
			    	 beforeClose: function (sender, handlers) {
				    	if(windowNumber==undefined){
				    		windowNumber=3;
				    	}
				    	if(Ext.getCmp('dataFiveWindow')&&windowNumber!=2&&windowNumber!=4&&windowNumber!=5&&windowNumber!=10){//如果第四个窗口存在，先关闭第四个窗口
			    			Ext.getCmp('dataFiveWindow').close();
			    		}
				    	if(Ext.getCmp('dataFourthWindow')&&windowNumber!=2&&windowNumber!=4&&windowNumber!=5&&windowNumber!=10){//如果第四个窗口存在，先关闭第四个窗口
			    			Ext.getCmp('dataFourthWindow').close();
			    		}
				    	if(Ext.getCmp('dataSceondWindow')&&windowNumber!=2&&windowNumber!=4&&windowNumber!=5&&windowNumber!=10){//如果第二个窗口存在，先关闭第二个窗口
			    			Ext.getCmp('dataSceondWindow').close();
			    		}
			    	 }
			    },
			    items: [
			    	{width:700,height:40,border:true,bodyStyle:'border-width: 0 0 1px 0;border-style: solid;border-color: #c0c0c0;',items:defineButtons},
			    	sequiredField
			        ],
			    buttons:[{ 
				     text : the_last_step,
				     height: 22,
				     handler :function(){
				     	Ext.getCmp('dataThirdWindow').hide();
				     	Ext.getCmp('dataSceondWindow').show();
				     }
				    },{ 
				     text : next_step,
				     height: 22,
				     handler :ThirdNextStep
				    }]
			});
		};
		function ThirdNextStep(){
			var store = Ext.getCmp('requiredPanel').getStore();
			arr=new Array();
			for(var i=0;i<store.getCount();i++){
			    var recored = store.getAt(i);  
			    var jsonArray= new Array();
			    jsonArray.push(recored.data.fieldsetid);//子集fieldsetdesc
			    jsonArray.push(recored.data.fieldsetdesc);
			    jsonArray.push(recored.data.dataValue);//子标
			    jsonArray.push(recored.data.itemtype);//类型
			    jsonArray.push(recored.data.requiredfield);//必填字标
			    //jsonArray.push(recored.data.onlyfield);//唯一性指标
			    var onlyfieldresult = recored.data.onlyfield;
			    if(recored.data.onlyfield&&!recored.data.requiredfield){
			    	if(recored.data.fieldsetid == 'A01'&& recored.data.itemtype=='A'&& recored.data.codesetid=='0'){
			    		Ext.Msg.alert(hint_information,only_field_must_be_filled);
				    	return;
			    	}else{
			    		onlyfieldresult = false;
			    	}
			    }
			    jsonArray.push(onlyfieldresult);
			    arr.push(jsonArray.join("-"));
			} 
		    Ext.getCmp('dataThirdWindow').hide();
		    if(Ext.getCmp('dataFourthWindow')){
		    	Ext.getCmp('dataFourthWindow').show();
		    }else{
		    	definingDataStandards.creatFourthWindow().show();
		    }
	    };
		return dataThirdWindow;
	},
	//第三步创建 gridpanel
	creatRequiredField:function(){
		var checkrender = function(value,metaData,record,rowIndex,cloIndex,store){
	        var com = new Ext.grid.column.CheckColumn();
	        if(record.data.fieldsetid == 'A01'&& record.data.itemtype=='A'&& record.data.codesetid=='0'){
	            return com.renderer(value);
	        }else {
	            return '';
	        }
	    };
    	//grid panel store
    	var mainStore = Ext.create('Ext.data.Store', {
             storeId: 'leftStoreId',
             fields:['dataName','dataValue','requiredfield','onlyfield','itemtype','fieldsetdesc','fieldsetid','codesetid'],
             proxy:{
            	 type: 'transaction',
			     functionId:'SYS0000003006',
                 extraParams:{
                         fileds:fileds
                 },
                 reader: {
                     type: 'json',
                     root: 'list'
                 }
             },
             autoLoad: true
         });
          //主面板
	    var mainGrid = Ext.create('Ext.grid.Panel', {
			store:mainStore,
			width:670,
			style: 'margin-left:9px;margin-top:1px',
		 	height:375,
		 	id:'requiredPanel',
		 	border:true,
		 	scrollable:"y",
		 	bufferedRenderer:false,
		 	multiSelect:true,
		 	forceFit:true,
		 	hideHeaders:false,
		 	viewConfig: {　　 
				markDirty: false //不显示编辑后的三角
			},
		 	columnLines:true,//显示grid.Panel数据列之间的竖线
			columns: [
			    {text:subset,dataIndex:'fieldsetdesc'},
			    {text:subset,dataIndex:'fieldsetid',hidden:true},
			    { text: field,dataIndex: 'dataName',xtype : 'gridcolumn'},
			    { text: field,dataIndex: 'dataValue',hidden:true},
			    { text: field,dataIndex: 'itemtype',hidden:true},
			    { text: required_field, dataIndex: 'requiredfield',align : 'center',xtype : 'checkcolumn'},
			    { text: only_field, dataIndex: 'onlyfield',align : 'center',xtype : 'checkcolumn', renderer : checkrender,
		    	  listeners: {
					 checkchange: function (column, rowIndex, checked, eOpts ) {
			             if(checked){
			            	var gridstore =  mainGrid.getStore();
			            	var record = gridstore.getAt(rowIndex);
			            	record.set('requiredfield',true);
			             }
			         }
		    	  }
			    } 
			],
			renderTo:Ext.getBody()
		});
		return mainGrid;
	},
	   //创建 选择上报指标
    creatSelectField:function(){
    	var jsondata = undefined;
    	var selectedJsonData = undefined;
    	var map = new HashMap();
		//用户回写数据库中的数据
		Rpc({functionId:'SYS0000003034',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			jsondata = result.data;
			selectedJsonData = result.selectedJsonData;
		}},map);
    	var selectedRightRecord=undefined;
    	var selectedLeftRecord=undefined;
    	var parentNode = undefined;
    	var leftNode = undefined;
		var leftTreeStore = Ext.create('Ext.data.TreeStore',{ 
			root: {
				// 根节点的文本
				id:'root',				
				expanded: true,
				children:jsondata
			}
		});
		/** 递归遍历父节点 **/  
		var travelParentChecked = function(node, checkStatus, opts){  
		    //父节点  
		    var upNode = node.parentNode;  
		    if(upNode != null){  
		        var opts = {};  
		        opts["isPassive"] = true;  
		        //父节点当前选中状态  
		        var upChecked = upNode.data.checked;  
		        //选中状态，遍历父节点，判断有父节点下的子节点是否都全选  
		        if(checkStatus){  
		            var allChecked = true;  
		            //此时父节点不可能是选中状态  
		            //如果有一个节点未选中，可以判断，当前父节点肯定是未选中状态，所以此时不必向上遍历  
		            upNode.eachChild(function (child) {  
		                if(!child.data.checked){  
		                    allChecked = false;  
		                    return false;  
		                }  
		            });  
		            upNode.set('checked', allChecked);  
		            if(allChecked){  
		                travelParentChecked(upNode, allChecked, opts);  
		            }else{//如果后台传递数据时，选择状态正确的话，此处不需要执行  
		                //travelParentChecked(upNode, allChecked, opts);  
		            }  
		        }else{//未选中，让父节点全都 不选  
		            if(upNode.data.checked){  
		                upNode.set('checked', checkStatus);  
		                travelParentChecked(upNode, checkStatus, opts);  
		            }else{  
		                //travelParentChecked(upNode, allChecked, opts);  
		            }  
		        }  
		    }  
		};
		/** 递归遍历子节点，复选框 **/  
		var travelChildrenChecked = function(node, checkStatus, opts){  
		    var isLeaf = node.data.leaf;  
		    if(!isLeaf){ 
		    	node.eachChild(function (child) {  
                    child.set('checked', checkStatus);  
                    //travelChildrenChecked(child, checkStatus, eOpts); //递归遍历，此处只有二级不需要递归 
                }); 
		    }  
		    node.set('checked', checkStatus);  
		}; 
		//var travelChildrenChecked ;
		leftTreePanel =  new Ext.tree.TreePanel({
			title:'备选指标',
			id:'leftTree',
			store:leftTreeStore,
			renderTo: Ext.getBody(),
			rootVisible:false,
			width:310,
			height:370,
			useArrows:true,
			border: true,
			viewConfig: {　　 
				markDirty: false //不显示编辑后的三角
			},
			listeners: {
				 checkchange: function(node, checked, eOpts){
		             travelChildrenChecked(node, checked, eOpts);
		             travelParentChecked(node, checked, eOpts);
		         },
				//鼠标放置子集上显示超链接
				itemmouseenter:function(e,record){
					if(record.get('leaf')==false && record.get('text').indexOf('全部添加')=='-1'){
						record.set('text', record.get('text')+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='addAllChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>全部添加</button>");
					}
					var aBtn = document.getElementById('addAllChilNodes');
					if(aBtn!=null){
						aBtn.onclick = function(){
							addAllOrRemove(record,"addAll");
						}
					}
				},
				//鼠标移出子集上隐藏超链接
				itemmouseleave:function(e,record){
					if(record.get('leaf')==false && record.get('text').indexOf('全部添加')!='-1'){
						record.set('text', record.get('text').replace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='addAllChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>全部添加</button>",""));
					}
				},
				//双击行触发事件，实现添加指标到右侧树
				rowdblclick:function(me,record,element,rowIndex){       			
					if(record.data.leaf == true){
						addField(record,"left");
					}
				},
				
			}
		});
		var rightTreeStore = Ext.create('Ext.data.TreeStore', {
			root: {
				// 根节点的文本
				id:'root',				
				expanded: true,
				children:selectedJsonData
			}
		});	
		rightTreePanel =  new Ext.tree.TreePanel({
			id:"rightTree",
			title:selectedField,
			renderTo: Ext.getBody(),
			store:rightTreeStore,
			rootVisible:false,
			width:310,
			height:370,
			useArrows:true,
			border: true,
			viewConfig: {　　 
				markDirty: false //不显示编辑后的三角
			},
			listeners: {
				 checkchange: function(node, checked, eOpts){
		             travelChildrenChecked(node, checked, eOpts);
		             travelParentChecked(node, checked, eOpts);
		         },
				//单击行事件获取右侧选中节点
				itemclick: function (record, node) {               
					selectedRightRecord=node;
				},
				itemmouseenter:function(e,record){				
					if(record.data.leaf == false&&record.get('text').indexOf('移除')=='-1'){
						record.set('text', record.get('text')+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='removeChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:36px;height:22px;'>移除</button>");
					} 
					var aBtn = document.getElementById('removeChilNodes');
					if(aBtn!=null){
						aBtn.onclick = function(){
							addAllOrRemove(record,"remove");
						}
					}
				},
				//鼠标移出子集上隐藏超链接
				itemmouseleave:function(e,record){
					if(record.data.leaf == false&&record.get('text').indexOf('移除')!='-1'){
						record.set('text', record.get('text').replace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='removeChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:36px;height:22px;'>移除</button>",""));
					}				
				},
				//双击行触发事件，实现删除指标到左侧树
				rowdblclick:function(me,record,element,rowIndex){
					addField(record,"right");
				}
			}
		});
		rightTreePanel.expandAll();
    	var selectField=Ext.create('Ext.panel.Panel', {
		  style: 'margin-top:2px',
		  layout: 'hbox',
		  height:400,
		  border:false,
		  width:'100%',
		  items:[{
				  xtype: 'container',
				  border:false,
				  style: 'margin-left:9px',
				  layout:'vbox',
				  items:[{
						    xtype: 'container',
							border:false,
							id:'fieldPanelIdSecond' 
						},{
							xtype: 'container',
						    border:false,
						    id:'leftPanelIdSecond' 
					  },new Ext.form.Checkbox({  
			                name:'photo',  
			                id:'photo',
			                boxLabel:photo_of_reporting_personnel  
            			})]
				  }, {
					  xtype: 'container',
					  border:false,
					  id:'buttonIdSecond' 
				  },{
					  xtype: 'container',
					  border:false,
					  id:'rightPanelIdSecond'  
				  }]
		});
	    Ext.getCmp('leftPanelIdSecond').add(leftTreePanel);
    	//中间按钮
    	var butPanel = Ext.widget({
		    xtype: 'panel',
		    border:false,
		    width: 40,
		    margin:'140 0 0 7',
	    	items:[{
					xtype:'button',
          			text:'>>', //添加
          			margin:'0 0 5 0',
          			handler:function(){
          				var flag = false ;
          				rootNode = Ext.getCmp('leftTree').getRootNode();
          		    	for(var i=rootNode.childNodes.length-1;i>-1;i--){
          				   var parentNode = rootNode.childNodes[i];//.childNodes;
          				   if(parentNode.data.checked){
          					 if(!flag){
          						flag = true ;
          					 }
          					 addAllOrRemove(parentNode,"addAll");
          				   }
          				}
          		    	for(var i=rootNode.childNodes.length-1;i>-1;i--){
           				    var childNodes = rootNode.childNodes[i].childNodes;
	           				for(var j=childNodes.length-1;j>-1;j--){
	           					var node = childNodes[j];
	           					if(node.data.checked){
	           						if(!flag){
	              						flag = true ;
	              					}
	           						addField(node,"left");
	           					}
	           				}
           				}
          		    	if(!flag){
          		    		Ext.MessageBox.alert(hint_information,please_selectField);
          		    	}
					}
				},{
					xtype:'button',
          			text:'<<', //删除
          			handler:function(){
          				var flag = false ;
          				rootNode = Ext.getCmp('rightTree').getRootNode();
          		    	for(var i=rootNode.childNodes.length-1;i>-1;i--){
          				   var parentNode = rootNode.childNodes[i];//.childNodes;
          				   if(parentNode.data.checked){
          					 if(!flag){
          						flag = true ;
          					 }
          					 addAllOrRemove(parentNode,"remove");
          				   }
          				}
          		    	for(var i=rootNode.childNodes.length-1;i>-1;i--){
           				    var childNodes = rootNode.childNodes[i].childNodes;
	           				for(var j=childNodes.length-1;j>-1;j--){
	           					var node = childNodes[j];
	           					if(node.data.checked){
	           						if(!flag){
	              						flag = true ;
	              					}
	           						addField(node,"right");
	           					}
	           				}
           				}
          		    	if(!flag){
          		    		Ext.MessageBox.alert(hint_information,please_selectedField);
          		    	}
					}
				}]
	    });
	    Ext.getCmp('buttonIdSecond').add(butPanel);
		Ext.getCmp('rightPanelIdSecond').add(rightTreePanel);
	   /*
	    * 把指标左侧添加到右侧 flag=left
	    * 把指标右侧添加到左侧 flag=right
	    */
		function addField(record,flag){
			if(record.get("leaf")){
				var treePanel = Ext.getCmp('leftTree');
				//新建节点存入此信息
				var modalNode1 = Ext.create('Ext.data.NodeInterface',{});
				var modalNode2 = Ext.create('Ext.data.NodeInterface',{});
				parentNode = Ext.getCmp('rightTree').getRootNode();
				if(flag=="right"){
					parentNode = Ext.getCmp('leftTree').getRootNode();
					treePanel = Ext.getCmp('rightTree');
				}
				var scrollTop = treePanel.getView().getEl().getScrollTop();
				var modalNode1Index=undefined;
				var parentNodeFlag=false;
				var sub=undefined,newIndex=undefined;
				for(var i=parentNode.childNodes.length-1;i>=0;i--){
					if(parentNode.childNodes[i].id == record.parentNode.data.id){
						parentNodeFlag=true;
						sub = i;
						break;
					}
					if(parentNode.childNodes[i].id > record.parentNode.data.id){
						if(i<=newIndex){
							newIndex = i;
						}            					            				
					}else{
						newIndex = i + 1;
					}	
				}
				if(!parentNodeFlag){
					if(parentNode.childNodes.length==0||record.parentNode.data.id=="A01"){
						newIndex=0;
					}
					modalNode1Index=newIndex;
					newNode = parentNode.createNode(modalNode1);
					newNode.set('id',record.parentNode.data.id);
					newNode.set('text',record.parentNode.data.text);
					newNode.set('checked',false);
					parentNode.insertChild(newIndex,newNode); 
				}else{
					newNode = parentNode.childNodes[sub];
					modalNode1Index=sub;
				}
				var B0110flag = true;
				var E0122flag = true;
				if(record.parentNode.data.id=="A01"){
					for(var i=0;i<rightTreePanel.getStore().getCount();i++){
					    var recored = rightTreePanel.getStore().getAt(i); 
					    if(recored.data.id.toUpperCase()=="B0110"){
					    	B0110flag = false;
					    }
					    if( recored.data.id.toUpperCase()=="E0122"){
					    	E0122flag = false;
					    }
					} 
				}
				var childIndex=0;
				for(var j=0;j<newNode.childNodes.length;j++){
					var leftid=newNode.childNodes[j].id.toUpperCase();
					var rightid = record.data.id.toUpperCase();
					if( leftid> rightid&&leftid!="B0110"&&leftid!="E0122"&&leftid!="E01A1"
						||rightid=="B0110"||rightid=="E0122"||rightid=="E01A1"){
						if(rightid=="B0110"){
							childIndex = 0;
						}else if(rightid=="E0122"){
							if(B0110flag){
								childIndex = 1;
							}else{
								childIndex = 0;
							}
						}else if(rightid=="E01A1"){
							if(B0110flag&&E0122flag){
								childIndex = 2;
							}else if(B0110flag||E0122flag){
								childIndex = 1;
							}else {
								childIndex = 0;
							}
						}else if(j<=newIndex){
							childIndex = j;
						} 
					}else{
						childIndex = j + 1;
					}
				}
				var childNode = newNode.createNode(modalNode2);
				childNode.set('id',record.data.id);
				childNode.set('text',record.data.text);
				childNode.set('leaf',true);
				childNode.set('checked',false);
				newNode.insertChild(childIndex,childNode);
				if(flag!="remove"){
					parentNode.childNodes[modalNode1Index].expand();
				}
				if(record.parentNode.childNodes.length==1){
					record.parentNode.remove();
				}else{
					record.remove();
				}
				treePanel.getView().getEl().setScrollTop(scrollTop);
			}
		};
		//全部添加或移除
		/*
		 * flag = remove 移除
		 * flag = addALL 全部添加
		 */
		function addAllOrRemove(record,flag){
			var modalNode1 = Ext.create('Ext.data.NodeInterface',{});		            
			parentNode = Ext.getCmp('leftTree').getRootNode();
			if(flag=="addAll"){
				parentNode = Ext.getCmp('rightTree').getRootNode();
			}
			var sub=0,newIndex=0;
			var parentNodeFlag=false;
			for(var i=0;i<parentNode.childNodes.length;i++){
				if(parentNode.childNodes[i].id == record.data.id){
					parentNodeFlag=true;
					sub = i;
					break;
				}
				if(parentNode.childNodes[i].id > record.data.id){
					if(i<=newIndex){
						newIndex = i;
					}            					            				
				}else{
					newIndex = i + 1;
				}	
			}
			if(flag=="addAll"){
				if(record.get('text').indexOf('全部添加')!='-1'){
					record.set('text', record.get('text').replace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='addAllChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>全部添加</button>",""));
				}
			}else{
				if(record.get('text').indexOf('移除')!='-1'){
					record.set('text', record.get('text').replace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id='removeChilNodes' style='border:none;background-color:#FFFACD;color:blue;font-size: 12px;border-radius: 50%;width:36px;height:22px;'>移除</button>",""));
				}
			}
			if(!parentNodeFlag){
				if(parentNode.childNodes.length==0){
					newIndex=0;
				}
				newNode = parentNode.createNode(modalNode1);
				newNode.set('id',record.data.id);
				newNode.set('text',record.data.text);
				newNode.set('checked',false);
				parentNode.insertChild(newIndex,newNode);
			}else{
				newNode = parentNode.childNodes[sub];
			}
			var childIndex=0;
			for(var j=0;j<record.childNodes.length;j++){
				for(var k=0;k<newNode.childNodes.length;k++){
					var rightid=newNode.childNodes[k].id;
					if(rightid > record.childNodes[j].data.id&&rightid!="B0110"&&rightid!="E0122"&&rightid!="E01A1"
						||record.childNodes[j].data.id=="B0110"||record.childNodes[j].data.id=="E0122"||record.childNodes[j].data.id=="E01A1"){
						if(record.childNodes[j].data.id=="B0110"){
							childIndex = 0;
						}else if(record.childNodes[j].data.id=="E0122"){
							childIndex = 1;
						}else if(record.childNodes[j].data.id=="E01A1"){
							childIndex = 2;
						}else if(k<=newIndex){
							childIndex = k;
						}       
					}else{
						childIndex = k + 1;
					}			        				
				}
				var modalNode2 = Ext.create('Ext.data.NodeInterface',{});
				var childNode = newNode.createNode(modalNode2);
				childNode.set('id',record.childNodes[j].data.id);
				childNode.set('text',record.childNodes[j].data.text);
				childNode.set('leaf',true);
				childNode.set('checked',false);
				newNode.insertChild(childIndex,childNode);
			}
			if(flag=="addAll"){
				parentNode.childNodes[newIndex].expand();
			}
			record.remove();
		};
		return selectField;
	},
	stepview:function(index){
		var stepview = Ext.widget("stepview",{
			renderTo:document.body,
			currentIndex:index-1,
			freeModel:false,
			stepData:[{name:selecting_index_of_sending_sub_sets},{name:set_required_indicators},{name:setting_dbname_type},{name:setting_protection_condition}]
		});
		return stepview;
	}
 });
