/**
 * 考场设置
 * lis 2015-11-02
 * 
 * */
Ext.define('ExamHallUL.ShowExamHall',{
	constructor:function(config) {
		examhall_me = this;
		examhall_me.batchId = 'all';//批次id
		examhall_me.b0110="";
		examhall_me.sqlCondition;//快速查询框输入值
		examhall_me.init();
	},
	// 初始化函数
	init:function(a) {
		
		var map = new HashMap();
		map.put("batchId",examhall_me.batchId);
		map.put("isInit","1");//第一次进入
	    Rpc({functionId:'ZP0000002501',async:false,success:examhall_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions = result.tableConfig;
		examhall_me.batchId = result.batchId;
		if(conditions){
			examhall_me.obj = Ext.decode(conditions);
			examhall_me.obj.showPlanBox=false;
			examhall_me.tableObj = new BuildTableObj(examhall_me.obj);
			examhall_me.tablePanel = examhall_me.tableObj.tablePanel;
		}else{
			Ext.showAlert(NO_JOBMODULE_PURVIEW);
			return;
		}
		
		//招聘批次类数据store
		var batchStore = Ext.create('Ext.data.Store',
				{
					fields:['dataName','dataValue'],
					proxy:{
					        type:'transaction',
						    functionId:'ZP0000002501',
						    extraParams:{
						    	flag:'1'
							},
					        reader: {
					            type: 'json',
					            root: 'allBatch'         	
					        }
					}
				});
		
	  //招聘批次下拉框对象
		var batchComBox = Ext.create('Ext.form.ComboBox', {
				store:batchStore,
				id:'codesetid',
				renderTo: Ext.getBody(),
//				fieldLabel:'招聘批次',
				//emptyText:'qing',
				blankText : '请选择',
				labelAlign:'right',
				editable:false,
				displayField:'dataName',
				valueField:'dataValue',
				queryMode:'local',
				width:200,
				//height:22,
				listConfig: {//此属性是用来在下拉框选项增加title（即鼠标移上去后的提示）
				    itemTpl:'<tpl for="."><div class="x-combo-list-item" title="{dataName}" ext:qtitle="dataName" ext:qtip="{dataName} : {dataValue} tip" >{dataName}</div></tpl>'
			    },
				listeners: {
					'select':function(combo,records,eOpts){
						examhall_me.selectBatch(records.data.dataValue);
					}
				}
				
		});
		
		//批次下拉框数据加载
		batchStore.load(function(records){
			batchComBox.select(examhall_me.batchId);
		});
		
		/*var bar = examhall_me.tableObj.getTitleBar();
		bar.setWidth(310);
		bar.add(batchComBox);*/
		examhall_me.tableObj.toolBar.insert(examhall_me.tableObj.toolBar.items.length-1,batchComBox);
		
		//复杂查询
		var map = new HashMap();
		map.put("batchId",examhall_me.batchId);
//		examhall_me.searchBox = Ext.create("EHR.querybox.QueryBox",{
//			renderTo:"fastsearch",
//			//width:350,
//			hideQueryScheme:true,
//			emptyText:'请输入考场号、考场名称、招聘批次或地址',
//			subModuleId:"zp_exam_hall_id_001",
//			customParams:map,
//			fieldsArray:[],
//			funcId:"ZP0000002501",
//			success:examhall_me.loadTable//重新加载数据列表
//		});
	},
	
	//批次下拉框查询结果
	loadStore:function(){
		var store = Ext.data.StoreManager.lookup('zp_exam_hall_dataStore');
		//store.currentPage=1;
		store.reload();
	},
	
	//查询结果
	loadTable:function(result){
		examhall_me.sqlCondition = result.sqlCondition;
		examhall_me.loadStore();
	},
	
	//考场名称
	hallNameRenderer:function(value,c,record){
		var html = "<a href=javascript:examhall_me.openEditPage('"+record.data.idx+"');>" + value + "</a>"; 
		return html;
	},
	
	//考生人数
	peopleNumRenderer:function(value,c,record){
		var hallId = record.data.idx;
		var hallName = record.data.hall_name;
		var batchName = record.data.batch_name;
		var html = '<a href="javascript:void(0)" onclick="examhall_me.toExamineeNameList(\''+hallId+'\',\''+hallName+'\',\''+batchName+'\');">' + value + '</a>'; 
		return html;
	},
	//进入考生名单页面
	toExamineeNameList:function(hallId,hallName,batchName){
		examhall_me.tableObj.getMainPanel().removeAll(false);
		    
		Ext.require('ExamHallUL.ExamineeNameList', function(){
			ExamineeNameListlGlobal=Ext.create("ExamHallUL.ExamineeNameList",{'hall_id':hallId,"hall_name":hallName,"batch_name":batchName});
		});
	},
	//招聘批次下拉
	selectBatch:function(batchId){
		var map = new HashMap();
		map.put("batchId",batchId);
		map.put("sqlCondition",examhall_me.sqlCondition);
	    Rpc({functionId:'ZP0000002501',async:false,success:function(){
	    	examhall_me.loadStore();
	    }},map);
	    
		examhall_me.batchId = batchId;
		
		//为查询控件改变参数batchid
		var map = new HashMap();
		map.put("batchId",examhall_me.batchId);
		var querybox = Ext.getCmp(examhall_me.tableObj.prefix+"_querybox");
		querybox.setCustomParams(map);
	},
	
	//导出考场设置
	exportData:function(){
	/**
		if(examhall_me.tablePanel.getStore().getCount() == 0){
			Ext.MessageBox.show({  
				 title : common.button.promptmessage,  
				 msg : "没有数据导出！",
				 buttons: Ext.Msg.OK,
				 icon: Ext.MessageBox.INFO  
			 });
			return;
		}
		var map = new HashMap();
		map.put("type", "excel"); 
	    Rpc({functionId:'ZP0000002502',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);	
			if(result.succeed){
				window.location.target="_blank";
				window.location.href = "/servlet/DisplayOleContent?filename="+result.fileName;
			}else{
				Ext.MessageBox.show({  
					title : common.button.promptmessage,  
					msg : result.message, 
					 buttons: Ext.Msg.OK,
					icon: Ext.MessageBox.INFO  
				})
			}
		    	
	 }},map);
	 **/
	},
	
	//添加方法
	insert:function(){
		var obj = new Object();
		obj.tableObj = examhall_me.tableObj;
		obj.type ='1';
		obj.addBatchId = examhall_me.batchId;
		Ext.require('ExamHallUL.Examhall',function(){
			Ext.create("ExamHallUL.Examhall",obj);
		})
	},
	//修改方法
	openEditPage:function(id){
		var obj = new Object();
		obj.tableObj = examhall_me.tableObj;
		obj.type ='2';
		obj.id =id;
		Ext.require('ExamHallUL.Examhall', function(){
			Ext.create("ExamHallUL.Examhall", obj);
		});
	},
	//考场分派
	assignExamHall:function(){
		
		 var batchId=Ext.getCmp('codesetid').getValue();
		 var grid=Ext.getCmp('zp_exam_hall_tablePanel');
		 var batchids = examhall_me.getSelect(grid,'batch_id');
		 var batchName=examhall_me.getSelect(grid,'batch_name');
		 if(grid.getSelectionModel().getSelection().length<=0){
			 Ext.Msg.alert('提示信息','请选择考场！');
			 return;
		 }
		 for(var i=0;i<batchids.length-1;i++){
			 if(batchids[i]!=batchids[i+1]){
				 Ext.Msg.alert('提示信息','请选择同一招聘批次下的考场，执行考场分派操作！');
				 return;
			 }
		 }
		 if(batchId=='all'){
			 batchId=batchids[0];
		 }
		 var map = new HashMap();
		 map.put("1","1");
		 map.put("batchId",batchId);
		 Rpc({functionId:'ZP0000002545',success: function(response){
				var value = response.responseText;
				var map = Ext.decode(value);
				examhall_me.b0110 = map.b0110;
		}},map);
	
		//var batchName = Ext.getCmp('codesetid').getDisplayValue();
		var width = 1300;
		var width1 = document.body.clientWidth;
		var widht11=width1*0.1;
		var width = 1300;
		if(width1<=700){
			widht11=0;
			width=1000;
		}
		Ext.util.CSS.createStyleSheet(".x-form-field-my{border:0;}","underline");
		var win = new Ext.create('Ext.Window',{
			title:"<div style='display:inline;float:left'>考场分派("+batchName[0]+")</div><div id='titilPanel' style='float:right;margin-right:5%;white-space:nowrap;display:inline'></div>",
			maximized:true,
			id:'addWin',
			closable:false,
			autoScroll:true,
			items:[{
			  xtype:'panel',
			  style:'margin-left:'+widht11+'px', 
			  border:false,
			  region: 'center',
  			 width:width1*0.8,
  			 items:[{
				xtype:'panel',
				id:'addPanel',
				border:false,
				items:[{
					xtype:'panel',
					border:false,
					html:'<div style="background:#f0f0f0;color:#333;width: 100%;height:36px;line-height:36px;font-weight:bolder;font-size:12px; margin-left: 1%;margin-top:20px">&nbsp;&nbsp;&nbsp;考场</div>'
				},{
					xtype:'panel',
					border:false,
					style:'margin-left:30px;margin-top:15px',
					layout:'table',
					items:[{
						xtype:'panel',
						border:false,
						width:'150px',
						style:'margin-top:15px;',
						items:[{
							xtype:'label',
							html:'考场（未分派/总座位数）'
						}]
					},{
						xtype:'panel',
						style:'margin-left:15px;',
						border:false,
						//height:50,
						maxWidth:width*0.5,
						id:'panel1'
					},{
						xtype:'panel',
						border:false,
						width:100,
						style:'margin-top:15px;',
						html:"<a href='javascript:void(0)' onclick='examhall_me.toAddExamHall("+batchId+")'>添加考场</a>"
					},{
						xtype:'panel',
						style:'margin-left:15px;',
						border:false,
						height:50,
						items:[{
							//考场id、
							xtype:'textfield',
							id:'textfield1',
							hidden:true
						},{
							//未分派桌数
							xtype:'textfield',
							id:'textfield11',
							hidden:true
						},{
							xtype:'textfield',
							hidden:true,
							id:'textfield111'
						}]
					}]
				},{
					xtype:'panel',
					border:false,
					style:'margin-left:30px;margin-top:20px',
					layout:'table',
					items:[{
						xtype:'label',
						html:'考场统计（未分派座位数）:'
					},{
						xtype:'panel',
						style:'margin-left:20px;',
						border:false,
						id:'panel2',
						width:100
					}]
				}]
			},{
				xtype:'panel',
				style:'margin-top:15px',
				id:'addPanel2',
				border:false,
				items:[{
					xtype:'panel',
					border:false,
					html:'<div style="background:#f0f0f0;color:#333;width: 100%;height:36px;line-height:36px;font-weight:bolder;font-size:12px; margin-left: 1%;margin-top:20px">&nbsp;&nbsp;&nbsp;考生安排</div>'
				},{
						xtype:'panel',
						border:false,
						layout:'table',
						style:'margin-left:30px;margin-top:20px;',
						items:[{
								xtype:'panel',
								style:'margin-top:10px;width:100px',
								border:false,
								items:[{
									xtype:'label',
									html:'需求单位'
								}]
							},{
								xtype:'panel',
								border:false,
								id:'panel3',
								maxWidth:width*0.5
							},{
								xtype:'panel',
								border:false,
								width:100,
								style:'margin-top:10px;',
								html:"<a href='javascript:void(0)' onclick='examhall_me.toSelectItem(this)'>添加需求单位</a>"
							},{
								xtype:'panel',
								style:'margin-left:20px;',
								border:false,
								items:[{
									xtype:'textfield',
									id:'textfield3',
									hidden:true,
									listeners:{
										'change':function(){
											 var map = new HashMap();
											 var z0321=Ext.getCmp('textfield3').getValue();
											 var z0357=Ext.getCmp('textfield4').getValue();
											 var subject=Ext.getCmp('textfield5').getValue();
											 map.put('z0321',z0321);
											 map.put('z0357',z0357);
											 map.put('subject',subject);
											 map.put('batchId',batchId);
											 Rpc({functionId:'ZP0000002543',success: examhall_me.toAutoComputeExaminee},map);
											
										}
									}
								}]
						}]
				},{
					xtype:'panel',
					border:false,
					layout:'table',
					style:'margin-left:30px;margin-top:15px',
					items:[{
						xtype:'panel',
						style:'width:100px;margin-top:10px;',
						border:false,
						items:[{
							xtype:'label',
							html:'职位类别'
						}]
					},{
						xtype:'panel',
						html:'',
						border:false,
						id:'panel4',
						maxWidth:width*0.5
					},{
						xtype:'panel',
						border:false,
						id:'panel44',
						style:'margin-top:10px;width:100px',
						//html:'<div><input type="hidden" name="aa_value"/><input type="text" name="aa_view" style="width:200px;margin-top:-18px;height:22px"/><img id="deep1" multiple="true" afterfunc="examhall_me.toGetPanel4Value" src="/module/recruitment/image/xiala2.png" plugin="deepcodeselector" codesetid="H1" style="margin-left:-1px;" inputname="aa_view"/></div>'
						html:'<input type="hidden" name="aa_value"/><input type="text" name="aa_view" style="border:0px;width:0px;margin-top:-18px;height:22px"/>'+
						'<a id="deep1" src="/module/recruitment/image/xiala2.png" plugin="deepcodeselector" multiple="true" afterfunc="examhall_me.toGetPanel4Value" '+
						'codesetid="H1" style="margin-left:-1px" inputname="aa_view" href="javascript:void(0)">请选择职位类别</a>'
						
					},{
						xtype:'panel',
						style:'margin-left:20px;',
						border:false,
						height:50,
						items:[{
							xtype:'textfield',
							id:'textfield4',
							hidden:true,
							listeners:{
								'change':function(){
									var map = new HashMap();
									var z0321=Ext.getCmp('textfield3').getValue();
									var z0357=Ext.getCmp('textfield4').getValue();
									var subject=Ext.getCmp('textfield5').getValue();
									map.put('z0321',z0321);
									map.put('z0357',z0357);
									map.put('subject',subject);
									map.put('batchId',batchId);
									Rpc({functionId:'ZP0000002543',success: examhall_me.toAutoComputeExaminee},map);										
								}
							}
						}]
					}]
				},{
					xtype:'panel',
					border:false,
					layout:'table',
					style:'margin-left:30px;margin-top:10px',
					items:[{
						xtype:'panel',
						style:'width:100px;margin-top:10px',
						border:false,
						items:[{
							xtype:'label',
							html:'考试科目'
						}]
					},{
						xtype:'panel',
						html:'',
						border:false,
						maxWidth:width*0.5,
						id:'panel5'
					},{
						xtype:'panel',
						border:false,
						id:'panel55',
						style:'width:100px;margin-top:10px',
						//html:'<div><input type="hidden" name="aa_value"/><input type="text" name="aa_view" style="margin-top:-18px;height:22px"/><img id="deep2" src="/module/recruitment/image/xiala2.png" plugin="deepcodeselector" codesetid="H1" style="margin-left:-1px" inputname="aa_view"/></div>'
						html:'<input type="hidden" name="bb_value"/><input type="text" name="bb_view" style="border:0px;width:0px;margin-top:-18px;height:22px"/>'+
						'<a id="deep2" src="/module/recruitment/image/xiala2.png" plugin="deepcodeselector" multiple="true" afterfunc="examhall_me.toGetPanel5Value" '
						+'codesetid="79" style="margin-left:-1px" inputname="bb_view" href="javascript:void(0)">请选择考试科目</a>'
						
					},{
						xtype:'panel',
						style:'margin-left:20px;',
						border:false,
						height:50,
						items:[{
							xtype:'textfield',
							id:'textfield5',
							hidden:true,
							listeners:{
								'change':function(){
									var map = new HashMap();
									var z0321=Ext.getCmp('textfield3').getValue();
									var z0357=Ext.getCmp('textfield4').getValue();
									var subject=Ext.getCmp('textfield5').getValue();
									map.put('z0321',z0321);
									map.put('z0357',z0357);
									map.put('subject',subject);
									map.put('batchId',batchId);
									Rpc({functionId:'ZP0000002543',success: examhall_me.toAutoComputeExaminee},map);
											
								}
							}
						}]
					}]
				},{
					xtype:'panel',
					border:false,
					style:'margin-left:30px;margin-top:30px',
					layout:'table',
					items:[{
						xtype:'label',
						html:'考生统计（未分派人数）:'
					},{
						xtype:'panel',
						style:'margin-left:20px;',
						border:false,
						id:'panel_examinee_sum',
						items:[{xtype:'panel',id:'panel6',width:100,border:false},{
							xtype:'textfield',
							id:'textfield6',
							hidden:true
						}]
					}]
				}]
			},{//增加座位分配规则---zhiyh20200409
				xtype:'panel',
				style:'margin-top:10px',
				id:'addPanel3',
				border:false,
				items:[{
					xtype:'panel',
					border:false,
					html:'<div style="background:#f0f0f0;color:#333;width: 100%;height:36px;line-height:36px;font-weight:bolder;font-size:12px; margin-left: 1%;margin-top:20px">&nbsp;&nbsp;&nbsp;座位分配规则</div>'
				},{
					xtype:'panel',
					border:false,
					style:'margin-left:30px;margin-top:10px',
					layout:'vbox',
					items:[{
						xtype:"radio",
						style:'margin-top:10px',
	                    name: 'distributionrule',
	                    inputValue: '0',
	                    width:width1*0.8,
	                    boxLabel: '顺序分配 (按招聘部门顺序为考生安排座位)',
	                    checked: true
	                }, {
	                	xtype:"radio",
	                    name: 'distributionrule',
	                    style:'margin-top:10px',
	                    id:'distrubuteRule',
	                    width:width1*0.8,
	                    inputValue: '1',
	                    boxLabel: '随机分配 (打乱顺序随机为考生安排座位)'
	                }]
				}]
			 }]
			}],
			listeners:{
				'render':function(){
					var idList = new Array();
					idList[0]='deep1';
					idList[1]='deep2';
					setDeepEleConnect(idList);
				},
				'afterrender':function(){
					var records = examhall_me.getSelectRecord(Ext.getCmp('zp_exam_hall_tablePanel'),'hall_name');
					var surplus_nums = examhall_me.getSelect(Ext.getCmp('zp_exam_hall_tablePanel'),'surplus_num');
					var hallIds = examhall_me.getSelect(Ext.getCmp('zp_exam_hall_tablePanel'),'idx');
					var surplusNum = 0;
					var topPx = 0;
					var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
					if(userAgent.indexOf("Firefox") > -1) //判断是否Firefox浏览器
						 topPx=8;
					for(var i=0;i<records.length;i++){
						surplusNum=surplus_nums[i]+surplusNum;
						var id = 'label'+hallIds[i];
						var idz = 'label'+id;
						var idzz = id+"idx";
						var idd = id+"d";
						var html ="<div style='display:inline-block;'>";
						html += "<dl style='float: left;padding-top:5px;padding-right:15px;text-align:center;margin-top:13px;'>";
						html += '<dt onmouseover="examhall_me.onMouseover(this)" onmouseleave="examhall_me.onMouseleave(this)" id=\''+id+'\' >'+
								'<a href="javaScript:void(0)" style="color:black" id=\''+idd+'\'>' +records[i]+'</a>'+
								'<img style="display:none;width: 15px; height: 15px;float:left;top: '+topPx+'px;" class="deletePic" title="点击删除考场安排" '+
								'onclick="examhall_me.removeHall(this,\''+id+'\',\''+surplus_nums[i]+'\')" src="/workplan/image/remove.png" >' +
								'</dt>';
						html += '<dt style="display:none;" id='+idz+'> '+hallIds[i]+'</dt>';
						html += "</dl>";
						html += "</div>";
						var label = Ext.create('Ext.form.Label',{
							 style:'margin-top:-10px;margin-right:10px;',
							 html:html,
							 id:idzz
						});
						Ext.getCmp('panel1').insert(label);
						Ext.getCmp('textfield1').setValue(Ext.getCmp('textfield1').getValue()+",'"+hallIds[i]+"'");
						Ext.getCmp('textfield111').setValue(Ext.getCmp('textfield111').getValue()+","+hallIds[i]+"`"+trim(records[i]));
					}
					var label2 = Ext.create('Ext.form.Label',{
						style:'margin-right:10px',
					    html:surplusNum+"人",
					    id:'label2'
					});
					Ext.getCmp('textfield11').setValue(surplusNum);
					Ext.getCmp('panel2').insert(label2);
				}
			}
		});
		win.show();
		 var html1 = '<a id="buttonSave" href="javascript:void(0);" onclick="examhall_me.submit(\''+batchId+'\')">提交</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="examhall_me.returnb()" >取消</a>';
    	 document.getElementById('titilPanel').innerHTML=html1;
	},
	//考生统计赋值
	toAutoComputeExaminee:function(response){
		var value = response.responseText;
		var map = Ext.decode(value);
		var count = map.count;
		Ext.getCmp('panel6').update(count+"人");
		Ext.getCmp('textfield6').setValue(count);
	},
			//获取选中grid的列
	 getSelectRecord:function (grid, col) { 
		var arr=new Array();
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
			     //seat_num surplus_num people_num
			      arr[i]=grid.getSelectionModel().getSelection()[i].get(col)+"("+grid.getSelectionModel().getSelection()[i].get('surplus_num')+"/"
			      +grid.getSelectionModel().getSelection()[i].get('seat_num')+")";
			 }
		}
		return arr;
	},
		//获取选中grid的列
	 getSelect:function (grid, col) { 
		var arr=new Array();
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null ){
			     //seat_num surplus_num people_num
			      arr[i]=grid.getSelectionModel().getSelection()[i].get(col);
			 }
		}
		return arr;
	},

	//鼠标移出图片时隐藏删除图标
   	onMouseleave:function(obj){
		if (!Ext.isEmpty(obj.childNodes[1]))
   		    obj.childNodes[1].style.display="none";
 	},
	//鼠标移入图片时显示删除图标
	onMouseover:function(obj){
 		if (!Ext.isEmpty(obj.childNodes[1])){
 			obj.childNodes[1].style.display="";
 			var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
 		    if(userAgent.indexOf("Firefox") > -1) //判断是否Firefox浏览器
 		    	obj.childNodes[1].className="newdeletePic";
 		}
	},
	//删除考场
	removeHall:function(obj,id,surplus_num){
		Ext.getCmp('panel1').setWidth("");
		var value=Ext.getCmp('textfield1').getValue();
		var arr = value.split(",");
		if(arr.length<=2){
			Ext.Msg.alert('提示信息','请至少保留一个考场');
			return;
		}
		Ext.getCmp('label2').update(Ext.getCmp('textfield11').getValue()-surplus_num+"人");
		Ext.getCmp('textfield11').setValue(Ext.getCmp('textfield11').getValue()-surplus_num);
		var str = document.getElementById('label'+id).innerHTML+'';
		var desc = document.getElementById(id+'d').innerHTML+'';
		var value = Ext.getCmp('textfield1').getValue()+'';
		str = trim(str);
		desc = trim(desc);
		value=value.replace(",'"+str+"'",'');
		Ext.getCmp('textfield1').setValue(value);
		value = Ext.getCmp('textfield111').getValue()+'';
		value=value.replace(","+str+"`"+desc,'');
		Ext.getCmp('textfield111').setValue(value);
 		Ext.getCmp(id+'idx').destroy();
 		Ext.getCmp('panel1').setWidth(Ext.getCmp('panel1').getWidth()+1);
	},
	//删除需求单位、职位类别、考试科目
	removeUnit:function(obj,id,textfieldid){
		if(textfieldid =="textfield3")
			Ext.getCmp('panel3').setWidth("");
		else if(textfieldid =="textfield4")
			Ext.getCmp('panel4').setWidth("");
		else if(textfieldid =="textfield5")
			Ext.getCmp('panel5').setWidth("");
		var str = document.getElementById('label'+id).innerHTML+'';
		var value=Ext.getCmp(textfieldid).getValue()+'';
		str = trim(str.substring(6,str.length));
		if(textfieldid=='textfield3'){
			value=value.replace(","+str,'');
		}else{
			value=value.replace(",'"+str+"'",'');
		}
		
		Ext.getCmp(textfieldid).setValue(value);
		Ext.getCmp("id"+id).destroy();
		if(textfieldid =="textfield3")
			Ext.getCmp('panel3').setWidth(Ext.getCmp('panel3').getWidth()+1);
		else if(textfieldid =="textfield4")
			Ext.getCmp('panel4').setWidth(Ext.getCmp('panel4').getWidth()+1);
		else if(textfieldid =="textfield5")
			Ext.getCmp('panel5').setWidth(Ext.getCmp('panel5').getWidth()+1);
	
	},
	//职位类别的回调函数
	toGetPanel4Value:function(array){
		var flag = '';
		Ext.getCmp('panel4').setWidth("")
		var value = Ext.getCmp('textfield4').getValue();
		for(var i=0;i<array.length;i++){
			var id="panel4"+array[i].value;
			var idz='label'+id;
			var textfield4 = 'textfield4';
			var html ="<div style='display:inline-block;'>";
					html += "<dl style='float: left;padding-top:10px;padding-right:15px;text-align:center;color:black;'>";
					html += '<dt onmouseover="examhall_me.onMouseover(this)" onmouseleave="examhall_me.onMouseleave(this)" id=\''+id+'\' >' +array[i].text+
								'<img style="display:none;width: 15px; height: 15px;float:left;" class="deletePic" onclick="examhall_me.removeUnit(this,\''+id+'\',\''+textfield4+'\')" src="/workplan/image/remove.png" >' +
								'</dt>';
					html += '<dt style="display:none" id='+idz+'>'+id+'</dt>';
					html += "</dl>";
					html += "</div>";
			if(!Ext.isDefined(Ext.getCmp('id'+id))){
				var label = Ext.create('Ext.form.Label',{
					style:'margin-right:10px;',
					html:html,
					id:'id'+id
				});
				Ext.getCmp('panel4').insert(label);
				value=value+",'"+array[i].value+"'";
			}else{
				flag=flag+array[i].text+",";
			}
		}
		Ext.getCmp('panel4').setWidth(Ext.getCmp('panel4').getWidth()+1);
		Ext.getCmp('textfield4').setValue(value);
		//if(!Ext.isEmpty(flag)){
		//	Ext.Msg.alert('提示信息',flag.substring(0,flag.length-1)+' 已添加，请勿重复操作');
		//}
	},
	//考试科目的回调函数
	toGetPanel5Value:function(array){
		var flag = '';
		Ext.getCmp('panel5').setWidth("")
		var value = Ext.getCmp('textfield5').getValue();
		for(var i=0;i<array.length;i++){
			var id="panel5"+array[i].value;
			var idz='label'+id;
			var textfield5 = 'textfield5';
			var html ="<div style='display:inline-block;'>";
					html += "<dl style='float: left;padding-top:10px;padding-right:15px;text-align:center;;color:black;'>";
					html += '<dt onmouseover="examhall_me.onMouseover(this)" onmouseleave="examhall_me.onMouseleave(this)" id=\''+id+'\' >' +array[i].text+
								'<img style="display:none;width: 15px; height: 15px;float:left;" class="deletePic" onclick="examhall_me.removeUnit(this,\''+id+'\',\''+textfield5+'\')" src="/workplan/image/remove.png" >' +
								'</dt>';
					html += '<dt style="display:none" id='+idz+'>'+id+'</dt>';
					html += "</dl>";
					html += "</div>";
			if(!Ext.isDefined(Ext.getCmp('id'+id))){
				var label = Ext.create('Ext.form.Label',{
					style:'margin-right:10px',
					html:html,
					id:'id'+id
				});
				Ext.getCmp('panel5').insert(label);
				value=value+",'"+array[i].value+"'";
			}else{
				flag=flag+array[i].text+",";
			}
		}
		Ext.getCmp('panel5').setWidth(Ext.getCmp('panel5').getWidth()+1);
		Ext.getCmp('textfield5').setValue(value);
		//if(!Ext.isEmpty(flag)){
			//Ext.Msg.alert('提示信息',flag.substring(0,flag.length-1)+' 已添加，请勿重复操作');
		//}
	},
	//归属单位赋值
	toSelectItem:function(btn){
		var flag = '';
		var picker = new PersonPicker({
					multiple: true,
					//deprecate:'',//需排除人员
					text: "添加",
					nbases: 'oth', // 人员库范围字符串，空为默认全部。如：Usr,Ret
					orgid: examhall_me.b0110, // 组织机构，不传代表全部
					recruitmentSpecial:true,
					//extend_str: "select a0100 from ${nbase}A01 where A0000 = 20", 
					addunit:true, //是否可以添加单位
				//	adddepartment:true, //是否可以添加部门
					callback: function (c) {
						Ext.getCmp('panel3').setWidth("")
						var value=Ext.getCmp('textfield3').getValue();
						for (var i = 0; i < c.length; i++) {
							var staffs= c[i];
							var id = "panel3"+staffs.id;
							var idz = 'label'+id;
							var textfield3 = 'textfield3';
							var html ="<div style='display:inline-block;'>";
							html += "<dl style='float: left;padding-top:10px;padding-right:15px;text-align:center;;color:black;'>";
							html += '<dt onmouseover="examhall_me.onMouseover(this)" onmouseleave="examhall_me.onMouseleave(this)" id=\''+id+'\' >' +staffs.name+
										'<img style="display:none;width: 15px; height: 15px;float:left;" class="deletePic" onclick="examhall_me.removeUnit(this,\''+id+'\',\''+textfield3+'\')" src="/workplan/image/remove.png" >' +
										'</dt>';
							html += '<dt style="display:none" id='+idz+'>'+id+'</dt>';
							html += "</dl>";
							html += "</div>";
							if(!Ext.isDefined(Ext.getCmp('id'+id))){
								var label = Ext.create('Ext.form.Label',{
									style:'margin-right:10px',
									html:html,
									id:'id'+id
								});
								Ext.getCmp('panel3').insert(label);
								value=value+","+staffs.id;
							}else{
								flag=flag+staffs.name+",";
							}
						}
						Ext.getCmp('panel3').setWidth(Ext.getCmp('panel3').getWidth()+1);
						Ext.getCmp('textfield3').setValue(value);
					}
				}, btn);
				picker.open();
				//if(!Ext.isEmpty(flag)){
					//Ext.Msg.alert('提示信息',flag.substring(0,flag.length-1)+' 已添加，请勿重复操作');
				//}
	
	},
	//考生分派提交
	submit:function(batchId){
		var value = Ext.getCmp('textfield6').getValue();
		var value1 = Ext.getCmp('textfield11').getValue();
		if(value==0){
			Ext.Msg.alert('提示信息','没有满足的考生，请重新设置条件');
			return;
		}
		if(parseInt(value)>parseInt(value1)){
			Ext.Msg.confirm("提示信息","当前考场剩余座位数不足，是否需要添加新考场？",function(btn){ 
				if(btn=="yes"){ 
					examhall_me.toAddExamHall(batchId);
					return;
			 	}else{
			 		examhall_me.submitOK(batchId);
			 	} 
			});
		}else{
			examhall_me.submitOK(batchId);
		}
	
	},
	submitOK:function(batchId){
		Ext.MessageBox.wait("", "正在分派考场");
		var map = new HashMap();
		var hallIds = Ext.getCmp('textfield1').getValue();
		var z0321s=Ext.getCmp('textfield3').getValue();
		var z0357s=Ext.getCmp('textfield4').getValue();
		var subjects=Ext.getCmp('textfield5').getValue();
		//增加座位分配规则---zhiyh20200409
		var distrubuteRule = "0";
		var distrubuteRuleVlaue = Ext.getCmp('distrubuteRule').checked;
		if(distrubuteRuleVlaue){
			distrubuteRule="1";
		}
		map.put('distrubuteRule',distrubuteRule);
		map.put('hallIds',hallIds);
		map.put('z0321s',z0321s);
		map.put('z0357s',z0357s);
		map.put('subjects',subjects);
		map.put('batchId',batchId);
		Rpc({functionId:'ZP0000002544',success: function(){
			Ext.MessageBox.close();
			Ext.getCmp('addWin').close();
			Ext.getCmp('zp_exam_hall_tablePanel').getStore().reload();
		}},map);
	},
	//返回
	returnb:function(){
		Ext.getCmp('addWin').destroy();
	},
	//添加考场
	toAddExamHall:function(batchId){
	 	var map = new HashMap();
		var items=Ext.getCmp('textfield111').getValue();
		map.put('batch_id',batchId);
		Ext.require('ExamHallUL.AddExamHall', function(){
			Ext.create("ExamHallUL.AddExamHall",{data:map,functionId:'ZP0000002546',desc:'考场',items:items,allowDel:false,afterfunc:'examhall_me.addExamHall'});
		});
	},
	//添加考场获取数据
	addExamHall:function(array){
	 	var arr = getDecodeStr(array).split(",");
	 	var batchId=Ext.getCmp('codesetid').getValue();
	 	var grid=Ext.getCmp('zp_exam_hall_tablePanel');
	 	var batchids = examhall_me.getSelect(grid,'batch_id');
	 	if(batchId=='all'){
			 batchId=batchids[0];
		 }
	 	var map = new HashMap();
		map.put('batchId',batchId);
		map.put('array',arr);
		Rpc({functionId:'ZP0000002547',success:examhall_me.addExamHallOK},map);
	},
	//添加考场获取数据并加载页面
	addExamHallOK:function(response){
		var value = response.responseText;
		Ext.getCmp('panel1').setWidth("")
		var map = Ext.decode(value);
		var list = map.list;
		//考场id
		var hallIds = list[0];
		//考场名称
		var records = list[1];
		//剩余数量数组
		var surplus_nums = list[2];
		//座位数
		var seatNums = list[3];
		//剩余数量
		var surplusNum = list[4];
		for(var i=0;i<records.length;i++){
			var id = 'label'+hallIds[i];
			var idz = 'label'+id;
			var idzz = id+"idx";
			var idd = id+"d";
			var topPx = 0;
			var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
			if(userAgent.indexOf("Firefox") > -1) //判断是否Firefox浏览器
				 topPx=8;
			var flag = records[i]+"("+surplus_nums[i]+"/"+seatNums[i]+")";
			var html ="<div style='display:inline-block;'>";
			html += "<dl style='float: left;padding-top:5px;padding-right:15px;text-align:center;margin-top:13px;color:black;'>";
			html += '<dt onmouseover="examhall_me.onMouseover(this)" onmouseleave="examhall_me.onMouseleave(this)" id=\''+id+'\' ><a href="javaScript:void(0)" style="color:black;" id=\''+idd+'\'>'+flag+'</a>'+
					'<img style="display:none;width: 15px; height: 15px;float:left;top: '+topPx+'px;" class="deletePic" title="点击删除考场安排" onclick="examhall_me.removeHall(this,\''+id+'\',\''+surplus_nums[i]+'\')" src="/workplan/image/remove.png" >' +
					'</dt>';
			html += '<dt style="display:none;" id='+idz+'> '+hallIds[i]+'</dt>';
			html += "</dl>";
			html += "</div>";
			//防止重复添加
			if(Ext.isDefined(Ext.getCmp(idzz))){
				continue;
			}
			var label = Ext.create('Ext.form.Label',{
				style:'margin-top:-10px;margin-right:10px;',
				html:html,
				id:idzz
			});
			Ext.getCmp('panel1').insert(label);
			Ext.getCmp('textfield1').setValue(Ext.getCmp('textfield1').getValue()+",'"+hallIds[i]+"'");
			flag = trim(flag);
			Ext.getCmp('textfield111').setValue(Ext.getCmp('textfield111').getValue()+","+hallIds[i]+"`"+flag);
		}
		Ext.getCmp('panel1').setWidth(Ext.getCmp('panel1').getWidth()+1);
		
		Ext.getCmp('textfield11').setValue(surplusNum+parseInt(Ext.getCmp('textfield11').getValue()));
		Ext.getCmp('label2').update(Ext.getCmp('textfield11').getValue()+"人");
	},
	//删除按钮
	dele:function(){
    	var obj = new Object();
		var tablePanel=Ext.getCmp('zp_exam_hall_tablePanel');
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.Msg.alert('提示信息',"请选择删除数据！");
			return;
		}else{
			var map = new HashMap();
			var dataStr = '';
			for(var i=0;i<selectRecord.length;i++){
				var id = selectRecord[i].data.idx;
				dataStr+= id+',';
			}
			dataStr=dataStr.substring(0,dataStr.length-1);
			
			var mapExamRight = new HashMap();
			mapExamRight.put("id",dataStr);
			mapExamRight.put("type","examEditRight" )
			mapExamRight.put("isEditOrDele","dele" )
			var test =false;
			//var tips = "删除考场，";
			var tips = "";
			Rpc({
				functionId : 'ZP0000002504',
				async : false,
				success : function(form,action) {
					var result = Ext.decode(form.responseText);
					
					if("1"==result.isExistStudent){
						//tips =tips+"将同时删除该考场的考生分派记录，";
						tips =tips+"删除考场操作将同时删除该考场的考生分派记录，";
					}
					//tips =tips+"确定删除 ？";
					tips =tips+"确认要删除考场吗？";
					if(result.examEditRight=='0'){
						tips = 	"您没有"+result.tipNames+"考场<br>的操作权限，";
						if("1" == result.isExistStudent){
//							tips = tips+"删除考场将同时删除该考场的考生分派记录，";
							tips = tips+"删除考场操作将同时删除该考场的考生分派记录，";
						}	
						tips = tips+"是否继续删除其余考场？"
						mapExamRight.put("id",result.canBeDeleIds);
					}
					if(result.examEditRight=='2'){
						test = true;
						Ext.Msg.alert('提示信息',"已选考场无权限删除 ！");
						return;
					}
				}}, mapExamRight);
			if(test)
				return;
			Ext.Msg.confirm("提示信息", tips, function(button, text) {  
	            if(button == "yes") { 
			Rpc({
				functionId : 'ZP0000002503',
				async : false,
				success : function(form,action) {
					var result = Ext.decode(form.responseText);
					if(result.deleTip=='0'){
						Ext.Msg.alert('提示信息',"删除不成功 ！");
						return;
					}else{
						examhall_me.loadStore();
						}
					}}, mapExamRight);
				}
			})
		}
	}
	
});
