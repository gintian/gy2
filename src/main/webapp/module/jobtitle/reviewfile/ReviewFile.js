/**
 * 资格评审_职称评审_上会材料
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ReviewFileURL.ReviewFile',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	reviewFile_viewtype:'list',//当前是卡片模式card，还是列表模式list
	searchBox:'',//快速查询控件对象
	schemeIdArray:['in','stop','finish','all','w0555_1','w0555_2','w0555_3',"w0555_4"],//方案查询id集
	schemeStateArray:['in','0','0'],
	tabName:'',
	tableObj:'',
	seletedW0301:'',//上会材料页面默认选中的w0301
	returnButton:false,
	constructor:function(config) {
		reviewfile_me = this;
		var w0301= config.w0301;
//		this.isStop=false;//会议是否结束
//		this.isPause = false //会议是否暂停
		this.unitIds = "";
		var schemestate= config.schemestate;
		if(!Ext.isEmpty(w0301) && !Ext.isEmpty(schemestate)){
			this.schemeStateArray[0] = schemestate;
			this.defaultW0301 = w0301
			this.returnButton = true;
		}
		this.init();
	},
	//创建主页面
	createMainPanel:function(w0301){
		return Ext.widget("viewport",{
			layout:'fit',
			items:[{
				xtype:'panel',
				title:zc.reviewfile.pagetitle,
				layout:'border',
				tools:[{id:'ReviewFile_schemeSetting',xtype:'toolbar',border:false}],
				border:false,
				items:[this.searchSchemeView(),{
					id:'tableMain',
					xtype:'container',
					region:'center',
					layout:'fit',
					items:[]
				}],
				listeners:{
					render:function(){
						Ext.getCmp("tableMain").add(reviewfile_me.getTableConfig(w0301));
					}
				}
			}],
			renderTo:Ext.getBody()
		})
	},
	// 初始化数据和主页面  by  haosl update 2018-6-62
	init:function() {
		var data = this.getMeetingData();
		var store = Ext.create("Ext.data.Store",{
				id:'objectiveStore',
				fields:['w0301', 'w0303'],
				data:data
		})
		var w0301 = "";
		if(store.getCount()>0){
			w0301 = this.defaultW0301 || store.getAt(0).get("w0301").split("_")[1];
			this.seletedW0301 = w0301;
		}
		this.createMainPanel(w0301);
	},
	//获得表格组件 by  haosl update 2018-6-62
	getTableConfig:function(w0301){
		var mainPanel = null;
		//如果没有会议，则增加提示图标，提示没有会议数据   haosl 2018-9-10
		if(Ext.isEmpty(w0301)){
			mainPanel = Ext.create("Ext.container.Container",{
				width:'100%',
				height:'100%',
				style:'text-align:center',
				items:[
					{
						xtype:'image',
						margin:'20 0 0 0',
						src:'/module/jobtitle/images/reviewmeeting/nomeeting.png'
					}
				]
			})
			return mainPanel;
		}
		var map = new HashMap();
		map.put("returnButton", this.returnButton);
		map.put("schemeType", this.schemeStateArray);
		map.put("w0301", w0301);
	    Rpc({functionId:'ZC00003001',async:false,success:function(form){
	    	var result = Ext.decode(form.responseText);
			var jsonData = result.tableConfig;
	    	group_id = result.group_id;// 专业(学科)组
			qnPlan = result.qnPlan;//问卷调查计划
			reviewsteplist = result.reviewsteplist;//评审环节
			w0573 = [{'dataValue':'1','dataName':'材料审核阶段'}, {'dataValue':'2','dataName':'投票阶段'}];//评审状态
			var obj = Ext.decode(jsonData);
			obj.openColumnQuery = true;//haosl 2017-07-31 方案查询可以查询自定义指标
			obj.columnNowrap = true;//表头不换行
			
			//单独处理三层表头  haosl 2018年8月2日 start
		var columns = obj.tablecolumns;
		var reg = /^c_\w+_[1-4]$/i;
		for(var i in columns){
			var childColumns = columns[i].childColumns;
			if(Ext.isEmpty(columns[i].columnId)){
				if(childColumns && childColumns.length==2){
					var cid = childColumns[0].columnId;
					if(!Ext.isEmpty(cid) && reg.test(cid)){
						var index = cid.substring(cid.length-1);
						var columhb = {};
						if("1"==index){
							columhb.columnId = "committee_hb";
							columhb.columnRealDesc=zc.reviewfile.step1showtext;
						}else if("2"==index){
							columhb.columnId = "subject_hb";
							columhb.columnRealDesc=zc.reviewfile.step2showtext;
						}else if("3"==index){
							columhb.columnId = "checkproficient_hb";
							columhb.columnRealDesc=zc.reviewfile.step3showtext;
						}else if("4"==index){
							columhb.columnId = "college_hb";
							columhb.columnRealDesc=zc.reviewfile.step4showtext;
						}
						columhb.columnType="A";
						columhb.columnWidth=100;
						columhb.columnDesc=columhb.columnRealDesc;
						columhb.hintText=columhb.columnRealDesc;
						columhb.editableValidFunc='false';
						columhb.locked=false;
						columhb.rendererFunc="";
						columhb.queryable=false;
						columhb.childColumns=[columns[i]];
						columns[i]=columhb;
					}
				}
			}
		}
		//单独处理三层表头  haosl 2018年8月2日 end
			
			this.tableObj = new BuildTableObj(obj);
			this.tableObj.setSchemeViewConfig({//配置栏目设置参数
	                            publicPlan:true,
	                            sum:false,
	                            lock:true,
	                            merge:false,
	                            pageSize:'20'
	                        });
			// 公示、投票环节显示申报材料表单上传的word模板内容
			this.support_word = result.support_word;
			mainPanel = this.tableObj.getMainPanel();
	    },scope:this},map);
	    return mainPanel;
	},
	// 【评审材料】渲染
	w0535:function(value, metaData, Record){
		var val = Record.data.w0535;
		var w0536 = Record.data.w0536;
		var html = "";
		if(!Ext.isEmpty(val) || !Ext.isEmpty(w0536)){
			if(reviewfile_me.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
				html = "<div style='text-align:left;'>"
				html += "<a href=javascript:JobTitleReviewFile.checkfile('" + val + "','" + Record.data.nbasea0100_e + "','');>职称申报材料</a><br>";
				html +="<a href=javascript:JobTitleReviewFile.w0536Show('" + w0536 + "');>申报材料一览表</a>";
				html += "</div>"
			} else {
				html = "<a href=javascript:JobTitleReviewFile.checkfile('" + val + "','" + Record.data.nbasea0100_e + "','"+w0536+"');><img src='/images/new_module/salaryitem.gif' border=0></a>";
			}
		}
		
		return html;
	},
	
	// 【评审材料word模板】渲染
	w0536:function(value, metaData, Record){
		var w0536 = Record.data.w0536;
		var html = "";
		if(reviewfile_me.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
			html = "<div style='text-align:left;'>"
			html +="<a href=javascript:JobTitleReviewFile.w0536Show('" + w0536 + "');>申报材料一览表</a>";
			html += "</div>"
		}
		
		return html;
	},
	// 【送审材料】渲染
	w0537:function(value, metaData, Record){
		var html = "";
		var val = Record.data.w0537
		if(val !="" && val != null){
			html = "<a href=javascript:JobTitleReviewFile.checkfile('" + val + "','" + Record.data.nbasea0100_e + "');><img src='/images/new_module/salaryitem.gif' border=0></a>";
		}
		
		return html;
	},
	// 【鉴定专家】渲染
	checkProficient:function(value, metaData, Record){
		var meetingId = Record.data.w0301_safe_e;//会议id加密
		var w0501_safe = Record.data.w0501_safe_e;//申报人主键序号加密
		var w0501 = Record.data.w0501;//申报人主键序号加密
		var subObject = w0501+"_3";//被调研对象唯一标志
		var planId = Record.data.w0541;//专家鉴定问卷计划号
		var w0523 = value.split("/")[1];
		if(w0523.length == 1){
			w0523 = " " + w0523;
		}
			
		var w0525 = Record.data.w0525;
		
		var evaluatedSum = value.split("/")[0];//已评人数
		if(evaluatedSum.toString().length == 1){
			evaluatedSum = evaluatedSum+" ";
		}
		var text = Number(evaluatedSum)+"/"+w0523;//"已评审外部鉴定专家人数/外部鉴定专家人数"
		
		var html = "";
		if(Number(evaluatedSum) === 0||planId == 0 ||Ext.isEmpty(w0525) ||w0525.substring(0, 1)=='1'){
			html = text;
		}else{
			html ="<a title='问卷结果分析' href=javascript:JobTitleReviewFile.analysisPlanData('" + planId + "','" + subObject + "');>"+text+"</a>";//问卷结果分析
		}
		var w0321_flag = reviewfile_me.checkCell(Record);
		if(!w0321_flag){
			html += "<a title='结束的数据不可账号分配'>&nbsp;&nbsp;&nbsp;&nbsp;<img src='/images/new_module/addperson.png' border=0></a>";
		}else{
			if(!Ext.isEmpty(w0525) && w0525.substring(0, 1) == '0'){//不是导入数据
				html += "<a title='账号分配' href=javascript:JobTitleReviewFile.checkProficientClick('" + meetingId + "','" + w0501_safe + "');>&nbsp;&nbsp;&nbsp;&nbsp;<img src='/images/new_module/addperson.png' border=0></a>";
			} else {
				html += "<a title='导入的数据不可账号分配'>&nbsp;&nbsp;&nbsp;&nbsp;<img src='/images/new_module/addperson.png' border=0></a>";
			}
		}
		return html;
	},
	// 鉴定专家点击事件
	checkProficientClick:function(w0301, w0501, type){
		var obj = new Object();
		obj.w0301 = w0301;
		obj.w0501 = w0501;
		Ext.require('OutProficientURL.OutProficient', function(){
			RevewFileGlobal = Ext.create("OutProficientURL.OutProficient", obj);
		});
	},
	// 【状态】渲染
	status:function(value, metaData, Record, d, e){
		//#17CE67绿、#FF0000红
		var color = '';
		if(value == '02`未通过'){
			color = '#FF0000';
		}else if(value == '01`通过'){
			color = '#17CE67';
		}
		value = value.substring(3);
		return html = "<label style='color:" + color + "'>" + value + "</label>"; 
		//return "121212";
	},
	// 导出评审或投票账号密码
	expKey_old:function(usetype){
		// 等待进度条
		Ext.MessageBox.wait(zc.label.exping+","+zc.label.wait, zc.label.waiting);	

		var idlist = new Array();
		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		for(var i=0; i<selectData.length; i++){
			var map = new HashMap();
			map.put("meetingid", selectData[i].data.w0301_safe_e);//会议id加密
			map.put("userid", selectData[i].data.w0501_safe_e);//申报人主键序号加密
			idlist.push(map);
		}

	
		var map = new HashMap();
		if(selectData.length>0)//不选择和选中两种情况
		  map.put("isSelectAll","0");
		else
		  map.put("isSelectAll","1");
		map.put("idlist",idlist);
		map.put("usetype",usetype);
	
		Rpc({functionId:'ZC00003008',success:function(form,action){
			Ext.MessageBox.close();	
			var result = Ext.decode(form.responseText);	
			if(result.msg==null||result.msg==""){
			if(result.succeed){
				window.location.target="_blank";
				window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+result.fileName;
				}else{
					Ext.MessageBox.show({  
						title : zc.label.remind,  
						msg : result.message, 
						icon: Ext.MessageBox.INFO  
					})
				}
	     	}else{
	     	     Ext.showAlert(result.msg);
	     	}
		 }},map);
	},
	expKey:function(usetype){
		var meetingCombo = Ext.getCmp("meetingCombo");//当前选中会议
		var record = meetingCombo.findRecordByValue(meetingCombo.getValue());
		
		var win = Ext.create('Ext.window.Window',{
	  		title:'选择阶段',
	  		closeToolText : '',
	 		id:'checklist',
	 		//minWidth: 360,  
	        height: 150,  
	        resizable: false,  
	        modal: true,
	        border:false,
	       	buttonAlign:'center',
	       	bodyStyle: 'background:#ffffff;',
	       	items:[{
	            xtype: 'fieldcontainer',
	            defaultType: 'radio',
	            layout:'table',
	            id:'radiofield',
	            padding:'0 10 0 10',
	            items: [
	           		{
	                    boxLabel  : zc.reviewfile.step4showtext,//显示二级单位评议阶段
	                    name      : 'topping',
	                    inputValue: '4',
	                    id        : 'radio4',
	                    padding   : '20 10 0 5',
	                    hidden    : !record.data.enableSteps.step4_me
	                },{
	                    boxLabel  : zc.reviewfile.step3showtext,//显示同行专家阶段
	                    name      : 'topping',
	                    inputValue: '3',
	                    id        : 'radio3',
	                    padding   : '20 10 0 5',
	                    hidden    : usetype=='1'?true:!record.data.enableSteps.step3_me
	                }, {
	                    boxLabel  : zc.reviewfile.step2showtext,//显示学科组阶段
	                    name      : 'topping',
	                    inputValue: '2',
	                    id        : 'radio2',
	                    padding   : '20 10 0 5',
	                    hidden    : !record.data.enableSteps.step2_me
	                },{
	                    boxLabel  : zc.reviewfile.step1showtext,//显示评委会阶段
	                    name      : 'topping',
	                    inputValue: '1',
	                    id        : 'radio1',
	                    padding   : '20 10 0 5',
	                    hidden    : !record.data.enableSteps.step1_me
	                }
	            ]
	        }],
	    	bbar:[{xtype:'tbfill'},{
	        	text:'确定',
	        	margin:'0 10 0 0',
	        	listeners:{
	        		'click':function(){
		        		var radio1=Ext.getCmp('radio1').getValue();
		        		var radio2=Ext.getCmp('radio2').getValue();
		        		var radio3=Ext.getCmp('radio3').getValue();
		        		var radio4=Ext.getCmp('radio4').getValue();
		        		if(!(radio1||radio2||radio3||radio4)){
		        			Ext.showAlert("请"+win.getTitle()+"！");
		        			return;
		        		}
		        		
		        		var type = '';
		        		if(radio1){
		        			type = '1';
		        		}else if(radio2){
		        			type = '2';
		        		}else if(radio3){
		        			type = '3';
		        		}else if(radio4){
		        			type = '4';
		        		}
		        		
		        		
		        		var map = new HashMap();
						map.put("w0301",record.data.w0301);
						map.put("type",type);
						map.put("usetype",usetype);
					
						Rpc({functionId:'ZC00003008',success:function(form,action){
							var result = Ext.decode(form.responseText);	
							if(result.msg==null||result.msg==""){
							if(result.succeed){
								var checklist = Ext.getCmp('checklist');
								if(checklist){
									checklist.close();
								}
								window.location.target="_blank";
								window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+result.fileName;
								}else{
									Ext.MessageBox.show({  
										title : zc.label.remind,  
										msg : result.message, 
										icon: Ext.MessageBox.INFO  
									})
								}
					     	}else{
					     	     Ext.showAlert(result.msg);
					     	}
						 }},map);
	        		}
	        	}
	        },{
	        	text:zc.label.cancel,
	        	listeners:{
	        		'click':function(){
	        			win.close();
	        		}
	        	}
	        },{xtype:'tbfill'}]
	       
		});
		win.show();
	},
	// 生成同行专家账号
	randomCreateKey:function(){
		
		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selectData.length == 0){
			Ext.showAlert("没有选中数据！");
			return ;
		}

		var dataList = new Array();
		for(var i=0; i<selectData.length; i++){
			var map = new HashMap();
			map.put("meetingid", selectData[i].data.w0301_safe_e);//会议id加密
			map.put("userid", selectData[i].data.w0501_safe_e);//申报人主键序号加密
			dataList.push(map);
		}
		
		var obj = new Object();
		obj.dataList = dataList;
		obj.isSelectAll = "0";
		Ext.require('GenerateAcPwURL.GenerateAcPw', function(){
			RevewFileGlobal = Ext.create("GenerateAcPwURL.GenerateAcPw", obj);
		});
		
		       	  		
	},
	/**
	 * 生成审核或者投票账号密码
	 * method:  =examine审查操作； =vote 启动评审操作 haosl
	 * type =1 评委会阶段 =2 学科组阶段  =3 同行专家阶段 =4 二级单位阶段
	 * typeName 阶段名称
	 */
	createExamineVoteKey:function(method,type){
		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selectData.length == 0){
			Ext.showAlert("没有选中数据！");
			return ;
		}
		var typeName = "";
		if(type==1)
			typeName=zc.reviewfile.step1showtext;
		if(type==2)
			typeName=zc.reviewfile.step2showtext;
		if(type==4)
			typeName=zc.reviewfile.step4showtext;
		Ext.Msg.confirm('提示信息',"是否生成"+typeName+(method=="vote"?"投票":"审核")+"账号？",function(btn){
			if(btn == 'yes'){
				
				if(type!=3)
					Ext.MessageBox.wait("正在生成账号密码...", "等待");
				
				var dataList = new Array();
				for(var i=0; i<selectData.length; i++){
					var map = new HashMap();
					map.put("meetingid", selectData[i].data.w0301_safe_e);//会议id加密
					map.put("userid", selectData[i].data.w0501_safe_e);//申报人主键序号加密
					dataList.push(map);
				}
				
				if(type==3){//同行生成账号界面
					var obj = new Object();
					obj.dataList = dataList;
					obj.isSelectAll = "0";
					Ext.require('GenerateAcPwURL.GenerateAcPw', function(){
						RevewFileGlobal = Ext.create("GenerateAcPwURL.GenerateAcPw", obj);
					});
				}else{
					var map = new HashMap();
					map.put("idList",dataList);
					map.put("w0301", selectData[0].data.w0301_safe_e);
					map.put("method",method);
					map.put("type",type);
					Rpc({functionId:'ZC00005008',async:true,success:function(form){
						Ext.MessageBox.close();
						var data = Ext.decode(form.responseText);
						if(data.msg){
							Ext.showAlert(data.msg);
						}else{
							if(data.flag=="1"){
								reviewfile_me.loadTable();	//同步专家人数 haosl 20160903
								Ext.showAlert("帐号密码生成成功！");
							}else{
								Ext.showAlert(data.message);
							}
						}
						
						
					}},map);
				}
			}
		});
		
	},
	// 材料审查&启动评审
	examineAndStart:function(method){
		
		var method = method;//method =examine审查操作； =vote 启动评审操作 haosl
		var dataList = new Array();
		// 同行专家阶段投票时又要直接启动，需要选择记录 chent 20180130 delete
		// if(method == 'examine'){//启动投票阶段时，不需要校验是否选中数据。chent
			var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
			if(method == 'examine'){
				if(selectData.length == 0){
					Ext.showAlert("没有选中数据！");
					return ;
				}
			}
			var w0555=selectData[0].data.w0555;
			// 整理选中数据
			for(var i=0;i<selectData.length;i++){
				var map = new HashMap();
				map.put("meetingid", selectData[i].data.w0301_safe_e);//会议id加密
				map.put("userid", selectData[i].data.w0501_safe_e);//申报人主键序号加密
				dataList.push(map);
			}
		// }
		var meetingCombo = Ext.getCmp("meetingCombo");//当前选中会议
		var record = meetingCombo.findRecordByValue(meetingCombo.getValue());
		
		var obj = new Object();
		obj.dataList = dataList;
		obj.isHiddenRadio1 = !record.data.enableSteps.step1_me;//评委会(不包含同年度已结束会议)
		obj.isHiddenRadio2 = !record.data.enableSteps.step2_me;//专业学科组(不包含同年度已结束会议)
		obj.isHiddenRadio3 = !record.data.enableSteps.step3_me;//外部专家(不包含同年度已结束会议)
		obj.isHiddenRadio4 = !record.data.enableSteps.step4_me;//二级单位评议组(不包含同年度已结束会议)
		obj.method = method;	//操作类型 审查材料||启动评审
		//obj.w0555 = w0555;//用于启动评审时默认勾选评审环节
		Ext.require('StartReviewURL.StartReview', function(){
			RevewFileGlobal = Ext.create("StartReviewURL.StartReview", obj);
		});
	},
	//结果归档
	resultsArchiving:function(){
		Ext.showConfirm('归档后数据不能修改,是否继续？',function(btn){
		 	if(btn == 'yes'){
				var map = new HashMap();
				var meetingCombo = Ext.getCmp("meetingCombo");
				var record = meetingCombo.findRecordByValue(meetingCombo.getValue());//获取选中归档会议
				map.put("w0301",JobTitleReviewFile.schemeStateArray[2]);//会议编号
				map.put("type","0");//判断评审流程走完了吗
				Rpc({functionId:'ZC00003017',async:false,success:function(res){
					 var resultObj = Ext.decode(res.responseText);
					 var type = resultObj.type;
					 var msg = resultObj.msg;
					 if(type == '1'){
					 	Ext.Msg.confirm('提示信息',msg,function(btn){
							if(btn == 'yes'){
								var vo = new HashMap();
							    vo.put("w0301",JobTitleReviewFile.schemeStateArray[2]);//会议编号
								vo.put("type","1");//直接归档
								Rpc({functionId:'ZC00003017',async:false,success:function(resp){
									var resultObj = Ext.decode(resp.responseText);
									var msg = resultObj.msg;
									Ext.showAlert(msg);
									var store = Ext.data.StoreManager.lookup('reviewFile_dataStore');
									store.load();
								}},vo);	
							}
					 	});
					 }else{
					 	Ext.showAlert(msg);
					 	var store = Ext.data.StoreManager.lookup('reviewFile_dataStore');
						store.load();
					 }
				}},map);
		 		
		 	}
		 });
	},
	//导出外审材料
	materialWindow:function(){
		//ZC00005011
		var selection=JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		var personStr="";
		Ext.MessageBox.wait("", "正在导出，请稍后...");
		if(selection.length>0){//selection[i].data.w0535;ins_id
			var list=[];
			for(var i=0;i<selection.length;i++){
				var w0535=selection[i].data.w0535;
					w0535=w0535.substring(w0535.indexOf("tabid=")+6);
				var tabid=w0535.substring(0,w0535.indexOf("&"));
					w0535=w0535.substring(w0535.indexOf("ins_id"))
				var ins_id=w0535.substring(w0535.indexOf("ins_id=")+7,w0535.indexOf("&"));
					w0535=w0535.substring(w0535.indexOf("taskid"));
				var task_id=w0535.substring(w0535.indexOf("taskid=")+7,w0535.indexOf("&"));
				 
				if(ins_id==''||tabid==''||task_id==''){
					Ext.Msg.alert( "提示信息","评审材料不能为空！");
					return ;
				}  
				 var map=new HashMap();
				 map.put("a0100",selection[i].data.w0505);
				 map.put("ins_id",ins_id);
				 map.put("taskid",task_id);
				 map.put("tabid",tabid);
				 list.push(map);
			}
			var map=new HashMap();
			map.put("data",list);
			Rpc({functionId:'ZC00005011',success:function(res){
				Ext.MessageBox.close();
				res=Ext.decode(res.responseText);
				if(res.flag){
					var url = "/servlet/vfsservlet?fromjavafolder=true&fileid="+res.filename;
						var win=open(url,"zip");
				}else{
					Ext.showAlert(res.emsg);
				}
			}},map);
		}else{
			Ext.MessageBox.close();
			Ext.Msg.alert( "提示信息","未选择人员！");
			return;
		}
	},
	drop:function(node,data,model,dropPosition,dropHandlers){//拖拽排序
		var gridPanel=Ext.getCmp('otherFileID');
		var store=gridPanel.store;
        if(data.records.length>1){
          Ext.Msg.alert("提示信息","禁止多行拖动！")
          gridPanel.getStore().reload();
        }
        
        for (var i = 0; i < store.totalCount; i++) {
			store.getAt(i).set("index",i+1);
			store.commitChanges();
		}
        //gridPanel.getStore().load();
	},
	// 会议名称
	meetingNameClick:function(){
		alert("会议名称");
	},
	// 外部鉴定专家 赞成人数占比
	expertagree:function(value, metaData, Record){
		var html = '';
		/*var agreeSum = Record.data.w0531;//赞成人数
		var againstSum = Record.data.w0527;//反对人数
		var giveupSum = Record.data.w0529;//弃权人数
		var sum = Record.data.w0523;//总人数
		if(sum == 0 ){
			html = "0%";
		}else{
			html = parseInt((agreeSum/sum*100))+"%";
		}*/
		html = value;
		var w0525 = Record.data.w0525;
		if(!Ext.isEmpty(w0525) && w0525.substring(0, 1) == '1'){
			html += '&nbsp;<img title="导入数据" src="/images/download.png" style="width:12px;height:12px;" border=0>';
		}
		
		return html;
	},
	// 专业（学科）组 总人数
	w0521:function (value, metaData, Record){
		var html = '';
		var w0501 = Record.data.w0501;//申报人主键序号
		var subObject = w0501+"_2";//被调研对象唯一标志
		var planId = Record.data.w0539;//内部评审问卷计划号
		var w0525 = Record.data.w0525;//导入标志
		var sum = value.split("/")[1];//总人数
		var evaluatedSum = value.split("/")[0];//已评人数
		
		if(evaluatedSum.toString().length == 1){
			evaluatedSum = evaluatedSum+" ";
		}
		if(sum.toString().length == 1){
			sum = " "+sum;
		}
		
		var text = Number(evaluatedSum)+"/"+sum;
		if(Number(evaluatedSum) === 0||planId == 0  ||Ext.isEmpty(w0525) ||w0525.substring(1, 2)=='1'){
			html = text;
		}else{
			html ="<a title='问卷结果分析' href=javascript:reviewfile_me.analysisPlanData('" + planId + "','" + subObject + "');>"+text+"</a>";//问卷结果分析
		}
		
		return html;
		
	},
	//学院聘任组已评数/未评数
	W0571:function (value, metaData, Record){
		var html = '';
		var w0501 = Record.data.w0501;//申报人主键序号
		var w0525  = Record.data.w0525;//导入标志
		var subObject = w0501+"_4";//被调研对象唯一标志
		var planId = Record.data.w0539;//内部评审问卷计划号
		
		var sum = value.split("/")[1];//总人数
		var evaluatedSum = value.split("/")[0];//已评人数
		
		if(evaluatedSum.toString().length == 1){
			evaluatedSum = evaluatedSum+" ";
		}
		if(sum.toString().length == 1){
			sum = " "+sum;
		}
		
		var text = Number(evaluatedSum)+"/"+sum;
		if(Number(evaluatedSum) === 0||planId == 0 ||Ext.isEmpty(w0525) ||w0525.substring(3, 4)=='1'){
			html = text;
		}else{
			html ="<a title='问卷结果分析' href=javascript:reviewfile_me.analysisPlanData('" + planId + "','" + subObject + "');>"+text+"</a>";//问卷结果分析
		}
		
		return html;
		
	},
	// 专业（学科）组 赞成人数占比
	subjectsagree:function(value, metaData, Record){
		if(value == '%'){
			return '';
		}
		
		var html = '';
		/*var agreeSum = Record.data.w0547;//赞成人数
		var againstSum = Record.data.w0543;//反对人数
		var giveupSum = Record.data.w0545;//弃权人数
		var sum = Record.data.w0521;//总人数
		if(sum == 0){
			html = "0%";
		}else{
			html = parseInt((agreeSum/sum*100))+"%";
		}*/
		html = value;
		var w0525 = Record.data.w0525;
		if(!Ext.isEmpty(w0525) && w0525.substring(1, 2) == '1'){
			html += '&nbsp;<img title="导入数据" src="/images/download.png" style="width:12px;height:12px;" border=0>';
		}
		
		return html;
	},
	// 评委会 总人数
	w0517:function(value, metaData, Record){
		var html = '';
		var w0501 = Record.data.w0501;//申报人主键序号
		var subObject = w0501+"_1";//被调研对象唯一标志
		var planId = Record.data.w0539;//内部评审问卷计划号
		var w0525  = Record.data.w0525;//导入标志
		var sum = value.split("/")[1];//总人数
		var evaluatedSum = value.split("/")[0];//已评人数
		if(evaluatedSum.toString().length == 1){
			evaluatedSum = evaluatedSum+" ";
		}
		if(sum.toString().length == 1){
			sum = " "+sum;
		}
		
		
		if(evaluatedSum.toString().length == 1){
			evaluatedSum = evaluatedSum+" ";
		}
		if(sum.toString().length == 1){
			sum = " "+sum;
		}
		var text = Number(evaluatedSum)+"/"+sum;
		if(Number(evaluatedSum) === 0||planId == 0 ||Ext.isEmpty(w0525) ||w0525.substring(2, 3)=='1'){
			html = text;
		}else{
			html ="<a title='问卷结果分析' href=javascript:reviewfile_me.analysisPlanData('" + planId + "','" + subObject + "');>"+text+"</a>";//问卷结果分析
		}
		
		return html;
	},
	// 评委会 赞成人数占比
	committeeagree:function(value, metaData, Record){
		var html = '';
		/*var agreeSum = Record.data.w0553;//赞成人数
		var againstSum = Record.data.w0549;//反对人数
		var giveupSum = Record.data.w0551;//弃权人数
		var sum = Record.data.w0517;//总人数
		if(sum == 0){
			html = "0%";
		}else{
			html = parseInt((agreeSum/sum*100))+"%";
		}*/
		html = value;
		var w0525 = Record.data.w0525;
		if(!Ext.isEmpty(w0525) && w0525.substring(2, 3) == '1'){
			html += '&nbsp;<img title="导入数据" src="/images/download.png" style="width:12px;height:12px;" border=0>';
		}
		
		return html;
	},
	//学院聘任组 赞成人数占比
	collegeagree:function(value, metaData, Record){
		var html = '';
		/*var agreeSum = Record.data.w0567;//赞成人数
		var againstSum = Record.data.w0563;//反对人数
		var giveupSum = Record.data.w0565;//弃权人数
		var sum = Record.data.w0571;//总人数
		if(sum == 0){
			html = "0%";
		}else{
			html = parseInt((agreeSum/sum*100))+"%";
		}*/
		html = value;
		var w0525 = Record.data.w0525;
		if(!Ext.isEmpty(w0525) && w0525.substring(3, 4) == '1'){
			html += '&nbsp;<img title="导入数据" src="/images/download.png" style="width:12px;height:12px;" border=0>';
		}
		
		return html;
	},
	// 查询方案
	searchScheme:function(schemeId){
		//默认选中会议下拉框 的第一条记录
		var searchBox = Ext.getCmp('reviewFile_querybox');
		// 结束时
		/*if(this.reviewFile_viewtype == "list"){//列表视图时才可以操作按钮
			var btn = Ext.getCmp('reviewfile_createkey');//【生成鉴定专家账号】按钮
			if(schemeId == "finish"){
				//不可用状态
				btn.setDisabled(true);
			}else{
				//可用状态
				btn.setDisabled(false);
			}
		}*///按钮已改成菜单，去掉此操作
		//以下是处理IE中indexOf不兼容的定义方法
		if(!Array.indexOf){
		    Array.prototype.indexOf = function(obj){
		        for(var i=0; i<this.length; i++){
		            if(this[i]==obj){
		                return i;
		            }
		        }
		        return -1;
		    }
		}
		if(this.schemeStateArray.indexOf(schemeId) > -1){//查询方案中已经有了，不进行
			return ;
		}
		
		// 移除当前组选中样式
		var arr = ['in','stop','finish','all','w0555_1','w0555_2','w0555_3'];
		var group1 = ['all','in','stop','finish'];//会议状态组
		var group2 = ['all','w0555_1','w0555_2','w0555_3','w0555_4'];//环节组
		
		var arr = new Array();
		if(schemeId == 'all'){//全部，复位所有
			arr = this.schemeIdArray;
			this.schemeStateArray = ['0', '0', '0'];
			
		} else if(group1.indexOf(schemeId) > -1){//会议状态组
			arr = group1;
			this.schemeStateArray[0] = schemeId;
			this.schemeStateArray[2] = '0';
			
		} else if(group2.indexOf(schemeId) > -1){//环节组
			arr = group2;
			this.schemeStateArray[1] = schemeId;
			
		} else {
			this.schemeStateArray[2] = schemeId;
		}
		
		for(var p in arr){
			var tmp = Ext.getCmp(arr[p]);
			if(tmp){
				tmp.removeCls('scheme-selected-cls');
			}
		}
		
		// 添加选中样式
		var selected = Ext.getCmp(schemeId);
		if(selected){
			selected.addCls('scheme-selected-cls');
		}
		
		// 更新查询控件的检索条件
		var map = new HashMap();
		map.put('schemeType', this.schemeStateArray);
		map.put('subModuleId', 'reviewFile');
		map.put('type', '3');
		if(searchBox){
			searchBox.customParams = map;
		}
		// 查询方案  
	    Rpc({functionId:'ZC00003001', async:false, success:this.loadTable,scope:this,schemeId:schemeId}, map);
	},
	// 重新加载数据列表
	loadTable:function(form, action){
		var group1 = ['all','in','stop','finish'];//会议状态组
		//方案查询--会议 重新获取
		if((typeof (action) != 'undefined') && (typeof (action.schemeId) != 'undefined') && group1.indexOf(action.schemeId) > -1){//会议状态组
			var combo =Ext.getCmp("meetingCombo");
			var data = this.getMeetingData();
			var store = Ext.data.StoreManager.lookup('objectiveStore');
			store.loadData(data,false);
			if(store.getCount()>0){
				var record = store.getAt(0);
				combo.select(record);
				combo.fireEvent("select", combo, record);
			}else{
				combo.setValue(null);
				var tableMain = Ext.getCmp("tableMain");
	         	if(tableMain){
	         		tableMain.removeAll(true);
	         	}
                tableMain.add(this.getTableConfig(''));
			}
		}else{
			//切换会议 刷新store
			var store = Ext.data.StoreManager.lookup('reviewFile_dataStore');
			store.load();
		}
	},
	// 查询方案区域
	searchSchemeView:function(){
		var me = this;
		var panel = Ext.widget('panel',{ 
			id:'reviewfile_schemePanel',
			region:'north',
			padding:'8 0 0 0',
			layout: {
		        type: 'hbox'
		    },
			border:false,
			defaults: {
		        margin:'3 0 0 0'
		    },
			items:[{
					xtype : 'label',
					margin:'3 0 0 3',
		            text: zc.label.searchScheme+'：'
	            },{
	            	xtype : 'label',
	            	id:'all',
	                html:"<a href=javascript:reviewfile_me.searchScheme('all');>"+zc.label.all+"</a>"//全部
	            },{
	            	xtype : 'label',
	            	html:'<span style="margin-right:10px"></span>'
	            },{
	            	xtype : 'label',
	            	style:'color:#C5C5C5;',
	            	text:"|"
	            },{
	            	xtype : 'label',
	            	html:'<span style="margin-right:10px"></span>'
	            },{
	            	xtype : 'label',
	            	id:'in',
	                html:"<a href=javascript:reviewfile_me.searchScheme('in');>"+zc.label.running+"</a>"//进行中
	            },{
	            	xtype : 'label',
	            	html:'<span style="margin-right:10px"></span>'
	            },{
	            	xtype : 'label',
	            	id:'stop',
	                html:"<a href=javascript:reviewfile_me.searchScheme('stop');>"+zc.label.stop+"</a>"//暂停
	            },{
	            	xtype : 'label',
	            	html:'<span style="margin-right:10px"></span>'
	            },{
	            	xtype : 'label',
	            	id:'finish',
	                html:"<a href=javascript:reviewfile_me.searchScheme('finish');>"+zc.label.finish+"</a>"//结束
	            },{
	            	xtype : 'label',
	            	html:'<span style="margin-right:10px"></span>'
	            },{
	            	xtype : 'label',
	            	style:'color:#C5C5C5;',
	            	text:"|"
	            },{
	            	xtype : 'label',
	            	html:'<span style="margin-right:10px"></span>'
	            }],
	         listeners:{
	         	render:function(){
	         		this.firstLoadClick();// 初次选中查询方案【进行中】
					this.createCardListButton();// 渲染卡片/列表切换按钮
	         	},
	         	scope:this
	         }
		});
		
		// 会议名称
		if(!Ext.util.CSS.getRule('.noBorder div')){
        	Ext.util.CSS.createStyleSheet(".noBorder div{border-color:#ffffff;}","card_css");
		}
        if(!Ext.util.CSS.getRule('.noBorder .x-form-text-default')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-default{color:#1B4A98;}","card_css");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-default')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}","card_css");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-over')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}","12313");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-focus')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-focus{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}","12313");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-text-wrap-default')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-wrap-default{border-color:#ffffff;}","card_css");
        }
		if(!Ext.util.CSS.getRule('.noBorder .x-form-text-wrap-focus')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-wrap-focus{border-color:#ffffff;}","card_css");
		}
		if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus{background-position:0px 0px;}","card_css");
		}
		panel.add({
		    	xtype:'combo',
		    	id:'meetingCombo',
		    	queryMode: 'local',  // 解决下拉框首次不能加载store的问题 haosl 20160817
		    	store:Ext.data.StoreManager.lookup("objectiveStore"),
		        valueField: 'w0301',
		        displayField: 'w0303',
		    	fieldLabel: '会议名称',
		        labelAlign:'left',
		        labelWidth:55,
		        width:230,
		        labelStyle:'margin:3 0 0 0',
		        forceSelection :true,
		        matchFieldWidth:false,
		        listConfig:{
		        	minWidth:180
		        },
		        editable:false,
		        emptyText:'—— 请选择会议名称  ——',
		        margin:0,
		        cls:'noBorder',
		        listeners:{
			        scope: me,
			        select: function(combo, data){
			         	if(typeof(data) == 'undefined'){
			         		return ;
			         	}
			         	var w0301 = data.data.w0301;//_mainPanel
			         	var tableMain = Ext.getCmp("tableMain");
			         	if(tableMain){
			         		tableMain.removeAll(true);
			         	}
			         	if(!Ext.isEmpty(w0301)){
			         		w0301 = w0301.split("_")[1];
			         		
			         		//存储当前选择的w0301 
			         		this.seletedW0301 = w0301;
			         	}
			         	//栏目设置渲染位置：将之前的栏目设置按钮移除
			         	var elem = document.getElementById("ReviewFile_schemeSetting-targetEl");
			         	elem.innerHTML = ""; 
			         	tableMain.add(this.getTableConfig(w0301));
			         	
			         },
			         change :function(combo, newValue){//获取当前会议下的投票集合，只取一次 chent 20170502
			         	var w0301_e = newValue;
			         	if(!Ext.isEmpty(w0301_e)){
			         		w0301_e = w0301_e.substring(6)
			         	}
			         	this.approvalPersonSet = this.getPersonSet(w0301_e);
			         },
			         afterrender:function(combo){
						var comStore = Ext.data.StoreManager.lookup("objectiveStore");
						if(comStore.getCount()>0){
							var index = 0;//会议初始选中第一条
							if(!Ext.isEmpty(this.defaultW0301)){//有默认会议时，要定位到默认会议
								comStore.each(function(record, _index){
									
									var w0301 = record.data.w0301;
									if(("w0301_"+this.defaultW0301) == w0301){
										index = _index;
									}
								}, this);
							}
							var record = comStore.getAt(index);
							combo.select(record);
						}
			         },
			         scope:this
		        }
		});
		/*// 评审环节4个阶段
		panel.add({
	            	xtype : 'label',
	            	margin:"3 0 0 10",
	            	style:'color:#C5C5C5;',
	            	text:"|"
	            },{
	            	xtype : 'label',
	            	id:'w0555_4',
	            	margin:"3 0 0 10",
	                html:"<a href=javascript:reviewfile_me.searchScheme('w0555_4');>"+zc.reviewfile.step4showtext+"</a>"//第1阶段
	            },{
	            	xtype : 'label',
	            	id:'w0555_3',
	            	margin:"3 0 0 10",
	                html:"<a href=javascript:reviewfile_me.searchScheme('w0555_3');>"+zc.reviewfile.step3showtext+"</a>"//第2阶段
	            },{
	            	xtype : 'label',
	            	id:'w0555_2',
	            	margin:"3 0 0 10",
	                html:"<a href=javascript:reviewfile_me.searchScheme('w0555_2');>"+zc.reviewfile.step2showtext+"</a>"//第3阶段
	            },{
	            	xtype : 'label',
	            	id:'w0555_1',
	            	margin:"3 0 0 10",
	                html:"<a href=javascript:reviewfile_me.searchScheme('w0555_1');>"+zc.reviewfile.step1showtext+"</a>"//第4阶段
	            });*/
		return panel
	},
	// 卡片/列表切换
	createCardListButton:function(){
		
		var cardButton = Ext.create('Ext.Button', {
			icon: '/images/new_module/cardview.png',
			tooltipType:'title',
			tooltip:zc.msg.cardview,
		    allowDepress: true,     //是否允许按钮被按下的状态
		    //enableToggle: true,   //是否允许按钮在弹起和按下两种状态中切换
		    scale: 'small',
		    scope:this,
		    handler:reviewfile_me.selectCardButton
		});
		var listButton = Ext.create('Ext.Button', {
			icon: '/images/new_module/listview.png',
			tooltipType:'title',
			tooltip:zc.msg.listview,
		    allowDepress: true,     //是否允许按钮被按下的状态
		    //enableToggle: true,   //是否允许按钮在弹起和按下两种状态中切换
		    scale: 'small',
		   	// disabled:true,
		    scope:this,
		    handler:reviewfile_me.selectListButton
		});
		
		
	/*	var toolBar = Ext.getCmp("reviewFile_toolbar");
		toolBar.add("->");
		toolBar.add(cardButton);
		toolBar.add(listButton);*/
	},
	// 切换成卡片视图
	selectCardButton:function(form){
		if(this.reviewFile_viewtype == 'card'){//已经是当前状态下，不继续进行
			return ;
		}
		this.reviewFile_viewtype = 'card';//卡片视图
		//卡片/列表视图按钮可用状态
		//this.cardButton.setDisabled(true);
		//this.listButton.setDisabled(false);
		
		//设置功能按钮的是否可用
		JobTitleReviewFile.setBtnDisable(true);
		
		//如果有锁列，隐藏锁列表与正常表
		if(JobTitleReviewFile.tableObj.tablePanel.lockedGrid){
			JobTitleReviewFile.tableObj.tablePanel.lockedGrid.hide();
			JobTitleReviewFile.tableObj.tablePanel.normalGrid.hide();
		}else{//没有锁列直接隐藏view，还得隐藏表头
			JobTitleReviewFile.tableObj.tablePanel.view.hide();
			JobTitleReviewFile.tableObj.tablePanel.query("headercontainer")[0].hide();
		}
	  
		//如果存在 卡片 view ,直接显示
		//if(tableObj.tablePanel.cardView){
		//	tableObj.tablePanel.cardView.show();
		//}else{//不存在，创建卡片view
			var cardView = JobTitleReviewFile.cardViewList();
			JobTitleReviewFile.tableObj.tablePanel.cardView = cardView;//卡片 view 保存在grid中，方便以后使用
			JobTitleReviewFile.tableObj.tablePanel.add(cardView);//显示卡片 模式
		//}
	}, 
	// 切换成列表视图
	selectListButton:function(form){
		
		if(this.reviewFile_viewtype == 'list'){//已经是当前状态下，不继续进行
			return ;
		}
		this.reviewFile_viewtype = 'list';//列表视图
		//卡片/列表视图按钮可用状态
		//this.listButton.setDisabled(true);
		//this.cardButton.setDisabled(false);
		
		//设置功能按钮是否可用
	//	JobTitleReviewFile.setBtnDisable(reviewfile_me.isStop);
		//隐藏卡片模式
		JobTitleReviewFile.tableObj.tablePanel.cardView.destroy();
		
		//如果有锁列表格，显示锁列和正常表格
		if(JobTitleReviewFile.tableObj.tablePanel.lockedGrid){
			JobTitleReviewFile.tableObj.tablePanel.lockedGrid.show();
			JobTitleReviewFile.tableObj.tablePanel.normalGrid.show();
		}else{//没有锁列表格，直接显示view ，并且显示 表头
			JobTitleReviewFile.tableObj.tablePanel.query("headercontainer")[0].show();
			JobTitleReviewFile.tableObj.tablePanel.view.show();
		}
	},
	// 卡片视图
	cardViewList:function(){
		JobTitleReviewFile.createSelfCss();//加载名片样式
		
		// 获取“现聘职称”“申报职称”的显示文字
		var w0513itemtext = '';
		var w0515itemtext = '';
		var columns = JobTitleReviewFile.tableObj.tablePanel.columns;
		for(var i in columns){
			if(columns[i].dataIndex == "w0513"){
				w0513itemtext = columns[i].text+"：";
			}
			if(columns[i].dataIndex == "w0515"){
				w0515itemtext = columns[i].text+"：";
			}
		}
		
		// 卡片模板
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
            '<div class="hj-wzm-top-yi">',
            	'<div class="hj-wzm-top-yi-left"></div>',
                '<div class="hj-wzm-top-yi-center">',
                	'<ul>',
                    	'<li style="margin:0 0 5px 0;"><label style="font-weight:bold;font-size:large;">{w0511}</label></li>',
                        '<li style="width:140px;white-space:nowrap; overflow:hidden;text-overflow:ellipsis;">'+w0513itemtext+'<label style="font-weight:bold;">{[this.getW0513OrW0515(values.w0513)]}</label></li>',
                        '<li style="width:140px;white-space:nowrap; overflow:hidden;text-overflow:ellipsis;">'+w0515itemtext+'<label style="font-weight:bold;">{[this.getW0513OrW0515(values.w0515)]}</label></li>',
		                        '<tpl if="this.notBlank(w0535)">',
									"<li>"+zc.label.applyfile+"：",
										"<a href=javascript:reviewfile_me.checkfile('{w0535}','{nbasea0100_e}','{w0536}');>评审材料</a>",
									"</li>",
								'</tpl>',
								
								'<tpl if="this.isBlank(w0535) && this.notBlank(w0537)">',
									"<li>"+zc.label.applyfile+"：",
										"<a href=javascript:reviewfile_me.checkfile('{w0537}','{nbasea0100_e}');>"+zc.label.proficientcheckfile+"</a>",
									"</li>",
								'</tpl>',
									
								'<tpl if="this.notBlank(w0535) && this.notBlank(w0537)">',	
			                        '<li><span style="margin-right:60px"></span>',
			                        	"<a href=javascript:reviewfile_me.checkfile('{w0537}','{nbasea0100_e}');>"+zc.label.proficientcheckfile+"</a>",
			                        "</li>",
			                    '</tpl>',
			                    
			                    '<tpl if="this.isBlank(w0535) && this.isBlank(w0537)">',
			                        "<li>"+zc.label.applyfile+"：</li>",
			                     '</tpl>',
						//'<li>状<span style="margin-right:24px"></span>态：{[values.w0533.split("`")[1]]}',
                        '</li>',
                    '</ul>',
                    '<img src="/servlet/DisplayOleContent?nbase={w0503_safe_e}&a0100={w0505_safe_e}&quality=~39R~32N~33wgsvgg~40~33HJD~40&caseNullImg=/images/photo.jpg"/>',
                '</div>',
                '<div class="hj-wzm-top-yi-right"></div>',
            '</div>',
            '</tpl>',
             {
		        // XTemplate 配置：
		        disableFormats: true,
		        getW0513OrW0515:function(value){
		        	if(Ext.isEmpty(value)){
		        		return '';
		        	}
		        	return value.split("`")[1];
		        },
		        // 成员函数:
		        isBlank: function(val){
					var flag = false;
		        	if(val == "" || val == null || val == undefined)
						flag = true;
					return flag;
		        },
		        notBlank: function(val){
					var flag = false;
		        	if(val !== null && val !== '' && val !== undefined)
						flag = true;
					return flag;
		        }
		    }
		);
		
		// 关联列表中的数据源
		var store = JobTitleReviewFile.tableObj.tablePanel.store;
		
		var dataview =Ext.create('Ext.view.View', {
            itemId : 'nav',
            //width : 1400,
            margin : '8 2 3 0',
            itemSelector : 'div.dataana-nav',
            cls:'clearfix',//清除内部浮动，否则div无法被撑开
            id : 'dataana',
            trackOver : true,//overItemCls前提
            //overItemCls : 'nav-hover',//鼠标悬停样式
            autoScroll : true,
            emptyText : '没有记录',//空文本时显示
            tpl : tpl,
            store : store
        });
		return dataview;
	},
	// 复写样式，不影响总体Css
	createSelfCss:function(){
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi{position:relative;width:260px;float:left;border:1px solid #F4F4F4;margin:0px 5px 5px 5px;}","card_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-left')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-left{width:26px;float:left;}","card_left_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-center')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center{position:relative;height:145px;float:left;}","card_center_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-center ul')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center ul{position:absolute;float:left;margin-top:15px;margin-left:103px;}","card_center_ul_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-center ul li')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center ul li{line-height:23px;color:#787878;list-style-type:none;white-space:nowrap; overflow:hidden;}","card_center_li_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-center ul li a')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center ul li a{color:#22549b;text-decoration:underline;}","card_center_a_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-center img')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center img{border:1px #BDBDBD solid;width:80px;height:110px;position:absolute;top:15px;left:15px;}","card_center_img_css");
		}
		if(!Ext.util.CSS.getRule('.hj-wzm-top-yi-right')){
			Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-right{width:32px;float:right;}","card_right_css");
		}
		//Ext.util.CSS.createStyleSheet("body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,table,tr,td,img,div,dl,dt,dd,span{margin:0;padding:0; border:none;}","card_css");
		
	},
	// 方案查询中，初次加载选中【进行中】
	firstLoadClick:function(){
		if(!Ext.util.CSS.getRule('.scheme-selected-cls a')){
			Ext.util.CSS.createStyleSheet(".scheme-selected-cls a{text-decoration:underline;}","underline");
		}
		var selected = Ext.getCmp(this.schemeStateArray[0]);
		selected.addCls('scheme-selected-cls');
		//this.searchScheme(this.schemeStateArray[0]);
		
	},
	// 设置功能按钮的可用状态为state
	setBtnDisable:function(state){
		var arr = ['reviewfile_outputData','reviewfile_importData','reviewfile_save','reviewfile_exp','reviewfile_revoke','reviewfile_createkey','reviewfile_start','reviewfile_archiving','navbar','startVote','reviewfile_syncVotes'];
		for(var p in arr){
			var btn = Ext.getCmp(arr[p]);
			if(btn){
				btn.setDisabled(state);
			}
		}
	},
	getQnId:function(planId){
		var qnId = "";
		
		var map = new HashMap();
		map.put("type", "1");
		map.put("planId", planId);
	    Rpc({functionId:'ZC00003010',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);	
			qnId = result.qnId;
	    }},map);
	    
	    return qnId;
	},
	// 评审材料、送审材料
	checkfile:function(path, nbasea0100, w0536){
		if(this.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
			this.w0536Show(w0536);
		} else {
			/** 解析path中的参数 */
			var tabid = "";
			var taskid = "";
			var taskid_validate = "";
			var index = path.indexOf("?");
			var paramStr =  path;
			if(index > -1){
				paramStr = path.substring(index+1);
			}
			var paramArray = new Array();
			paramArray = paramStr.split('&');
			for(var i=0; i<paramArray.length; i++){
				var param = paramArray[i];
				var key = param.split('=')[0];
				if(key == 'tabid'){
					tabid = param.split('=')[1];
				} else if(key == 'taskid'){
					taskid = param.split('=')[1];
				} else if(key == 'taskid_validate'){
					taskid_validate = param.split('=')[1];
				}
			}
			if(Ext.isEmpty(taskid_validate)){// 获取taskid的校验code
				var map = new HashMap();
				map.put("type", '2');
				map.put("taskid", taskid);
				Rpc({functionId:'ZC00003022',async:false,success:function(res){
					var result = Ext.decode(res.responseText);
					taskid_validate = result.taskid_validate;
				
				}},map);
			}
			// 配置参数 
			var obj={};
			obj.module_id="11";////调用模块标记：职称模块
			obj.return_flag="14";//返回模块标记：不需要返回关闭按钮
			obj.tab_id=tabid;//模板号
			obj.task_id=taskid;//任务号 除0以外需加密
			obj.approve_flag="0";//不启用审批
			obj.view_type="card";//卡片模式
			obj.card_view_type="1";//卡片模式下不要显示左边导航树
			obj.other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100+"`taskid_validate="+taskid_validate;;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
			obj.callBack_init="JobTitleReviewFile.showView";
			//obj.callBack_close="JobTitleReviewFile.closeView";
			
			//获取业务模板名称
			var map = new HashMap();
			map.put("tabId", tabid);
		    Rpc({functionId:'ZC00003018',async:false,success:function(){
		    	var result = Ext.decode(arguments[0].responseText);
		    	this.tabName = result.tabName;
				// 调用人事异动模板 
				createTemplateForm(obj);
		    },scope:this},map);
		}
	},
	// 申报材料展示，word模板
	w0536Show:function(w0536){
		var servletpath = '/servlet/DisplayOleContent?filePath='+w0536+'&bencrypt=true'+'&openflag=true';
			
		var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
		var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
		Ext.create('Ext.window.Window',{
	  		title:'申报材料',
	       	layout:'fit',
	        modal: true,
	        resizable: false,  
	        border:false,
	  		closeToolText : '',
	       	items:[{
	            xtype: 'panel',
	            border:false,
	           	html:'<iframe src="'+servletpath+'" width="'+(width-10)+'" height="'+(height-40)+'"></iframe>'
	        }]
		}).show();
	},
	// 绑定学科组数据源
	createGroup:function(form){
		var flag = reviewfile_me.checkCell(form);
		if(!flag){
			return ;
		}
		// 数据源
       var store = Ext.create('Ext.data.Store', {
			fields: ['myId','displayText'],
			proxy:{
				type: 'transaction',
		        functionId:'ZC00003014',
				extraParams:{
					w0301:form.data.w0301_safe_e
				},
				 reader: {
					  type: 'json',
					  root: 'subjectgroup'         	
				}
			},
			autoLoad: true
		});
       var a =  Ext.create('Ext.form.ComboBox', {
		    	store:store,
		        forceSelection :true,
		        valueField: 'myId',
		        displayField: 'displayText',
		        //editable:false,
		        allowBlank: false
		});
		
		var i = 0;
		var columns = JobTitleReviewFile.tableObj.tablePanel.columns;
		for(var p in columns){
			if(columns[p].dataIndex == "group_id"){
				i = p;
			}
		}
		JobTitleReviewFile.tableObj.tablePanel.columns[i].setEditor(a);
	},
	//评审环节
	w0555:function(value){
		reValue = value;
		if(value == 1){
			reValue = zc.reviewfile.step1showtext;//第1阶段同行 
			
		}else if(value == 2){
			reValue = zc.reviewfile.step2showtext;//第2阶段学科组
			
		}else if(value == 3){
			reValue = zc.reviewfile.step3showtext;//第3阶段评委会
		}
		else if(value == 4){
			reValue = zc.reviewfile.step4showtext;//第4阶段聘任组
		}else {
			reValue = "";
		}
		
		return reValue;
	},
	// 问卷分析
	analysisPlanData:function(planId, subObject){
		
		var qnId = reviewfile_me.getQnId(planId);//获取问卷号
		
		Ext.require('QuestionnaireTemplate.QuestionnaireBuilder', function(){
			var AnalysisBuilder = Ext.create("QuestionnaireTemplate.QuestionnaireBuilder",{
				title:'结果分析',
				planId:planId,//计划号
				qnId:qnId,//问卷号
				subObject:subObject, //被调查对象
				hideNavigation:true,//是否显示导航
				flex:1,
				backButtonFn:JobTitleReviewFile.questionnaireTemplateWinClose,
				border:false
			});
			var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
			var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
			Ext.create('Ext.window.Window', {
			    title: '结果分析',
			    id:'questionnaireTemplateWin',
			    padding:0,
			    margin:0,
				modal: true,
				width:width,
				height:height,//窗口的高(不含菜单)，150：菜单高度,
				border:false,
				header:false,
				layout:{
					type:'vbox',
					align:'stretch'
				},
			    items: [AnalysisBuilder]
			}).show();
		});
	},
	// 关闭问卷分析
	questionnaireTemplateWinClose:function(){
		var questionnaireTemplateWin = Ext.getCmp('questionnaireTemplateWin');
		if(questionnaireTemplateWin){
			questionnaireTemplateWin.close();
		}
	},
	// 显示人事异动模板
	showView:function(){
//		var mainPanel = Ext.getCmp('reviewFile_mainPanel');
//		mainPanel.removeAll().add(templateMain_me.mainPanel);
        
        var container = Ext.create('Ext.container.Container', {
        	region: 'center',
		    layout: 'fit',
		    border: false,
		    items: [templateMain_me.mainPanel]
		});
		var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
		var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
		var reviewfile_showfile_win = Ext.create('Ext.window.Window', {
			title:JobTitleReviewFile.tabName,
			id:'reviewfile_showfile_win',
			layout: 'border',
			modal: true,
			width:width,
			height:height,
			border:false,
			autoScroll:false,
		    items: [container]
		}).show();
		if(reviewfile_showfile_win){
			window.onresize=function(){
				var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
				var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
				reviewfile_showfile_win.setWidth(width);
				reviewfile_showfile_win.setHeight(height);
			}
		}
	},
	// 撤销 
	deletePerson:function() {
		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selectData.length == 0){
			Ext.showAlert("没有选中数据！");
			return ;
		}
		Ext.Msg.confirm(common.button.promptmessage, "确定要撤销选中记录吗？", function(id){
			if(id == 'yes') {
				var meetingCombo = Ext.getCmp('meetingCombo')
				var meetingComboRecord = meetingCombo.findRecordByDisplay(meetingCombo.getRawValue());
				// 选中数据重组
				var idlist = new Array();
				for(var i=0; i<selectData.length; i++){
					var map = new HashMap();
					map.put("w0301", selectData[i].data.w0301_safe_e);//会议编号selectData[i].data.w0301_safe_e
					map.put("w0501", selectData[i].data.w0501_safe_e);//申报人编号
					idlist.push(map);
				}
				var map = new HashMap();
				map.put("idlist",idlist);
			    Rpc({functionId:'ZC00003007', async:false, success:function(form){
			    	// 更新会议检索下拉框数据
			    	var result = Ext.decode(form.responseText);
					if(result.flag=="1")
						reviewfile_me.loadTable();
					else if(result.flag=="0")
						Ext.showAlert("申报人已开始投票，不允许撤销！");
					else
						Ext.showAlert(result.message);
			    },scope:this}, map);
			}
		});
	},
	importData:function(){
		var meettingId = Ext.getCmp("meetingCombo").getValue();
			if(meettingId == null){
			Ext.showAlert("请在上方选择一个会议再进行导入！");
			return;
		}
		var win = Ext.create('Ext.window.Window',{
			closeToolText : '',
			title:'导入',
	 		id:'imputeId',
	 		width:300,
	 		height:180,  
	        resizable: false,  
	        modal: true,
	        border:false,
	       	bodyStyle: 'background:#ffffff;',
	       	layout: {
	            type: 'vbox',
//	            align: 'left'
	            padding:'0 0 0 50'
	        },
	       	items:[{
	       		xtype:'container',
	       		margin: '30 0 0 0',
	       		layout:'hbox',
	       		items:[{
		            xtype:"label",
		            margin: '0 30 0 0',
		            width:120,
		            text:"1、下载模板文件"
		        },{
		            xtype:"button",
		            text:"下载",
		            handler:function(){
				        	var idlist = new Array();
				    		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
				    		for(var i=0; i<selectData.length; i++){
				    			var map = new HashMap();
				    			map.put("meetingid", selectData[i].data.w0301_safe_e);//会议id加密
				    			map.put("userid", selectData[i].data.w0501_safe_e);//申报人主键序号加密
				    			idlist.push(map);
				    		}
				    		if(selectData.length==0){//如果没有选中的，则默认全部选中的效果
				    			var store = JobTitleReviewFile.tableObj.tablePanel.getStore();
				    			if(store.getCount()==0){
				    				Ext.showAlert("当前搜索条件下没有模版数据，请重新查询！");
				    				return;
				    			}
				    			for(var i = 0;i<store.getCount();i++){
				    				var map = new HashMap();
					    			map.put("meetingid", store.getAt(i).data.w0301_safe_e);//会议id加密
					    			map.put("userid", store.getAt(i).data.w0501_safe_e);//申报人主键序号加密
					    			idlist.push(map);
				    			}
				    		}
				    		var meettingName = Ext.getCmp("meetingCombo").getRawValue();
				    		var map = new HashMap();
				    		map.put("meettingName",meettingName);
				    		map.put("isSelectAll","0");
				    		map.put("idlist",idlist);
				    		Rpc({functionId:'ZC00003019',success:function(form,action){
				    			var result = Ext.decode(form.responseText);	
				    			if(result.succeed){
				    				window.location.target="_blank";
				    				window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+result.fileName;
				    				}else{
				    					Ext.MessageBox.show({
				    						title : zc.label.remind,  
				    						msg : result.message, 
				    						icon: Ext.MessageBox.INFO  
				    					})
				    				}
				    		 }},map);
		        		}
		        }]},{
	       			xtype:'container',
	       			margin: '30 0 0 0',
		       		layout:'hbox',
		       		items:[{
			            xtype:"label",
			            width:120,
			            margin: '0 30 0 0',
			            html:"2、请选择导入文件"
		        },{
		            xtype:"button",
		            width:40,
		   	   		height:22,
		            text:"浏览"
		        }]},
		        {
		   	   		xtype:'box',
		   	   		border:false,
		   	   		width:40,
		   	   		height:22,
		  	   		margin: '-22 0 0 150',
		  	   		style:{
		  	   			background:'',
		  	   			borderColor:'#c5c5c5',
		  	   			borderStyle:'dashed'
		  	   		},
		  	   		listeners:{
		  	   			render:function(){
		  	   				var uploadBox = this;
		  	   				Ext.widget("fileupload",{
		  	   					upLoadType:3,
		  	   					height:22,width:40,
		  	   					buttonText:'',
		  	   					fileExt:"*.xls;*.xlsx",//添加对上传文件类型控制
		  	   					renderTo:uploadBox.id,
		  	   					success:function(list){
		  	   							var obj = list[0];
					   					var map1 = new HashMap();
					   					var meettingCombo = Ext.getCmp("meetingCombo");
					   					var meetingComboRecord = meettingCombo.findRecordByDisplay(meettingCombo.getRawValue());
					   					var w0301 = meetingComboRecord.data.w0301;
					   					map1.put("filename",obj.filename);
					   					map1.put("path",obj.path);
					   					map1.put("w0301", w0301);
					   					Rpc({functionId:'ZC00003020',success:function(data){
					   						var result = Ext.decode(data.responseText);
					   						var msglist = result.importMsg;
					   						var msgg = "";
					   						if(msglist != ""){
					   							for(var i=0;i<msglist.length;i++){
			 	           							var msg = msglist[i];
			 	           							msgg += msg+"<br>";
			 	           						}
					   							msgg = msgg;
						 	           			Ext.showAlert(""+msgg+"", function(){
						 	           				win.close();
						 	           				reviewfile_me.loadTable();
						 	           				
						 	           			});
						 	           		}
					   					},scope:this},map1);	
					   				},
		  	   					callBackScope:'',
		  	   					savePath:''
		  	   				});
		  	   			}
		  	   		}
		        }]
		});
		win.show();
	},
	//判断单元格是否可以编辑（归档的会议不允许编辑）
	checkCell:function(record){
		var w0321 = record.data.w0321;
		if(w0321==06){
			return false;
		}else{
			return true;
		}
		
	},
	// 单元格中显示文本+title(投票信息)
	getApprovalPersonSetHtml:function(value, columnId, record){
		var html = value;
		
		var type = 0;//1：评委会 2：学科组 3：外部专家 4：学院任聘组
		if("w0553" == columnId || "w0549" == columnId || "w0551" == columnId){
			type = 1;
		} else if("w0547" == columnId || "w0543" == columnId || "w0545" == columnId){
			type = 2;
		} else if("w0531" == columnId || "w0527" == columnId || "w0529" == columnId){
			type = 3;
		} else if("w0567" == columnId || "w0563" == columnId || "w0565" == columnId){
			type = 4;
		}
		
		var expert_state = '';//1：赞成 2：反对 3：弃权
		if('w0553' == columnId || 'w0547' == columnId || 'w0531' == columnId || 'w0567' == columnId){
			expert_state = '1';
		} else if('w0549' == columnId || 'w0543' == columnId || 'w0527' == columnId || 'w0563' == columnId){
			expert_state = '2';
		} else if('w0551' == columnId || 'w0545' == columnId || 'w0529' == columnId || 'w0565' == columnId){
			expert_state = '3';
		}
		
		if(!Ext.isEmpty(value) && value >= 0){// 有投票且不是导入数据时，显示投票结果。
			var w0525 = record.data.w0525;
			
			if(!Ext.isEmpty(w0525) &&  
				((type == 1 && w0525.substring(2, 3) == '0') 
				|| (type == 2 && w0525.substring(1, 2) == '0') 
				|| (type == 3 && w0525.substring(0, 1) == '0') 
				|| (type == 4 && w0525.substring(3, 4) == '0'))){//不是导入数据
					
				var w0501 = record.data.w0501_safe_e;
				var key = w0501+"_"+type+"_"+expert_state;
				var title = JobTitleReviewFile.approvalPersonSet[key];
				if(Ext.isEmpty(title)){
					title = '';
				}
				html = "<span title='"+title+"'>"+value+"</span>";
			}
		}
	
		return html;
	},
	//外部专家，赞成
	w0531:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0531', Record);
	},
	//外部专家，反对
	w0527:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0527', Record);
	},
	//外部专家，弃权
	w0529:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0529', Record);
	},
	//专业学科组，赞成
	w0547:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0547', Record);
	},
	//专业学科组，反对
	w0543:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0543', Record);
	},
	//专业学科组，弃权
	w0545:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0545', Record);
	},
	//评委会，赞成
	w0553:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0553', Record);
	},
	//评委会，反对
	w0549:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0549', Record);
	},
	//评委会，弃权
	w0551:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0551', Record);
	},
	//学院任聘组，赞成
	w0567:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0567', Record);
	},
	//学院任聘组，反对
	w0563:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0563', Record);
	},
	//学院任聘组，弃权
	w0565:function(value, metaData, Record){
		return reviewfile_me.getApprovalPersonSetHtml(value, 'w0565', Record);
	},
	//获取赞成、反对、弃权人员集
	getPersonSet:function(w0301_e){
		var approvalPersonSet = "";
		
		if(Ext.isEmpty(w0301_e)){
			return approvalPersonSet;
		}
		var map = new HashMap();
		map.put("type", "2");//获取人员集
		map.put("w0301", w0301_e);
	    Rpc({functionId:'ZC00003010',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);	
			approvalPersonSet = result.approvalPersonSet;
	    },scope:this},map);
	    
	    return approvalPersonSet;
	},
	// 公告维护--选择列表指标 chent 2017-03-02 add 
	notice:function() {
		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selectData.length == 0){
			Ext.showAlert("没有选中数据！");
			return ;
		}
		//默认选中：部门、单位名称、姓名、现聘职务、申报职务
		var defaultSelectedItems = ',w0509,w0507,w0511,w0513,w0515,';
		//排除指标：评审环节、评审状态、评审材料、送审材料、二级单位评议组、已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、
		//同行专家、基本达到人数、未达到人数、已达到人数、赞成人数占比、状态、学科组已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、参会人数、
		//已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、评价表、同行专家评价表
		var exceptItems = ',w0555,w0573,w0535_,w0537_,committeename,w0571,w0567,w0563,w0565,collegeagree,w0569' +
				',checkproficient,w0527,w0529,w0531,w0533,proficientagree,group_id,w0521,w0523,w0547,w0543,w0545,subjectsagree' +
				',w0557,w0519,w0517,w0553,w0549,w0551,committeeagree,w0559,w0539,w0541,';

		var map = new HashMap();
		map.put("type","2");//2代表查询
		//公示材料右侧列表排序
		Rpc({
			functionId : 'ZC00003021',
			success : function(res){
				var resultObj = Ext.decode(res.responseText);
				var config = resultObj.msg;
				var items = new Array();
				var columns = JobTitleReviewFile.tableObj.tablePanel.columns;

				// 是否配置过指标顺序，如果为空则为没有保存过。走默认情况
				if(Ext.isEmpty(config)) {
					
					var maps = new HashMap();
					for(var i in columns){
						if(columns[i].hidden == true){
							continue ;
						}
						var dataIndex = columns[i].dataIndex;
						if(exceptItems.indexOf(','+dataIndex+',') > -1){// 排除指标
							continue ;
						}
						var obj = new Object();
						obj.selected = '0';
						obj.dataValue = dataIndex;
						obj.dataName = columns[i].text;
						if(defaultSelectedItems.indexOf(dataIndex) > -1){//默认选中
							obj.selected = 1;
						}
						if(Ext.isEmpty(obj.dataName)){//如果指标被隐藏或者已不存在，则会dataname会为空，则不显示出来。
							continue ;
						}
						items.push(obj);
					}
				}else {
					
					var maps = new HashMap();
					for(var i in columns){
						if(columns[i].hidden == true){
							continue ;
						}
						var dataIndex = columns[i].dataIndex;
						if(config.indexOf(dataIndex) > -1){//默认选中加入map中，以便排序
							maps.put(dataIndex,columns[i].text);
						}
					}
					//将constant中正确的排序加入items中
					var msgArray = config.split(",");
					for(var i = 0; i < msgArray.length; i++) {
						
						var obj = new Object();
						obj.dataValue = msgArray[i];
						obj.dataName = maps.get(msgArray[i]);
						if(Ext.isEmpty(obj.dataName)){//如果指标被隐藏或者已不存在，则会dataname会为空，则不显示出来。
							continue ;
						}
						obj.selected = '1';
						items.push(obj);
					}
					for(var i in columns){
						var dataIndex = columns[i].dataIndex;
						if(exceptItems.indexOf(','+dataIndex+',') > -1
								||config.indexOf(dataIndex) > -1){// 排除指标
							continue ;
						}
						var obj = new Object();
						obj.selected = '0';
						obj.dataValue = dataIndex;
						obj.dataName = columns[i].text;
						
						if(Ext.isEmpty(obj.dataName)){//如果指标被隐藏或者已不存在，则会dataname会为空，则不显示出来。
							continue ;
						}
						items.push(obj);
					}
				}
				// 公告维护--选择列表指标
				Ext.require('NoticePath.SelectNoticeField', function() {
					Ext.create("NoticePath.SelectNoticeField", {
						title : '选择职称评审列表指标',
						items : items,
						callBackFunc : 'JobTitleReviewFile.selectNoticeFieldCallBackFunc'
					});
				});
			},scope:this
		}, map);
	},
	// 公告维护
	selectNoticeFieldCallBackFunc:function(selectedItems){
		//将集合取出dataValue
		var list = "";
		for(var i = 0; i < selectedItems.length; i++) {
			if(i == 0)
				list = selectedItems[i].get("dataValue");
			else
				list += "," + selectedItems[i].get("dataValue");
		}
		
		var map = new HashMap();
		map.put("list",list);
		map.put("type","1");//1代表新增修改
		// 保存/更新公示材料指标顺序，顺便取出默认通知对象。通知对象为业务范围对应机构。
		var unitIdByBusiList = new Array();// 通知对象集合
		Rpc({functionId:'ZC00003021',async:false,success:function(res){
			var result = Ext.decode(res.responseText);
			unitIdByBusiList = result.unitIdByBusiList;
			reviewfile_me.unitIds = result.unitIds;
			
		}},map);
		
		var selectData = JobTitleReviewFile.tableObj.tablePanel.getSelectionModel().getSelection();
		var contentHtml = JobTitleReviewFile.getContentHtml(selectData, selectedItems);
		// 默认通知对象
		var default_notice_object = new Array();
		var len = unitIdByBusiList.length;
		if(len > 0){
			for(var i=0; i<len; i++){
				var obj = unitIdByBusiList[i];
				default_notice_object.push(obj);
			}
		}
		
		// 公告维护
		Ext.require('NoticePath.Notice',function(){
			Ext.create('NoticePath.Notice', {
				title : '职称公告维护',
				notice_name : '关于' + Ext.getCmp('meetingCombo').getRawValue() + '的通知',
				notice_content : contentHtml,
				notice_time : '5',
				notice_seq : '1',
				notice_object : '',
				isApproved : true,
				default_notice_object:default_notice_object,
				unitIds:reviewfile_me.unitIds
			});
		});
		
	},
	// 公告内容html
	getContentHtml:function(selectData, selectedItems){
		Ext.util.CSS.createStyleSheet(".noBorder div{border-color:#ffffff;}","card_css");
		/** 职称评审人员信息列表表格html */ 
		//border:none 覆盖掉ckeditor 的自带样式
		var widthNum = 650/(selectedItems.length+2);//加2，其中1个是序号列；1个是详情列。
		var tdStyle = "border:none;text-align:center; height:42px; line-height:42px;border-bottom:1px solid #e5e6e8;width:"+widthNum+"px;max-width:"+widthNum+"px;"
		var thStyle = "border:none;background:#f5f5f5;height:42px; line-height:42px;font-family:'微软雅黑'; font-size:14px;color:#666;width:"+widthNum+"px;max-width:"+widthNum+"px;";
		
		var tableHtml = "";
		tableHtml+='<div class="hj-wzm-table">';
		tableHtml+='<table style="border:1px solid #e5e6e8;border-bottom:none;" width="100%" border="0" cellpadding="0" cellspacing="0">';
		
		// 表格列头
		tableHtml+='<tr>';
		tableHtml+='<th style="'+thStyle+'"; font-size:14px;color:#666;" scope="col">序号</th>';
		for(var j=0; j<selectedItems.length; j++){
			var text = selectedItems[j].get('dataName');
			tableHtml+='<th style="'+thStyle+'" scope="col">'+text+'</th>';
		}
		tableHtml+='<th style="'+thStyle+'" scope="col">详情</th>';
		tableHtml+='</tr>';
		
		// 表格数据
		for(var i=0; i<selectData.length; i++){
			
			tableHtml+='<tr>';
			tableHtml+='<td style="'+tdStyle+'">'+(i+1)+'</td>';
			var data = selectData[i].data;
			for(var w=0; w<selectedItems.length; w++){
				var itemid = selectedItems[w].get('dataValue');
				var text = data[itemid];
				if(typeof text == 'string' && text.indexOf('`') > -1){
					text = text.split('`')[1];
				}
				tableHtml+='<td style="'+tdStyle+'">'+text+'</td>';
				
			}
			var nbasea0100 = data.nbasea0100_e;
			var nbasea0100_1 = data.nbasea0100_1_e;
			var w0535 = data.w0535;
			var w0536 = data.w0536;
			
			var isWord = false;
			if(this.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
				isWord = true;
			}
			
			var path = '';
			if(isWord){
				path = encodeURIComponent(w0536);
			} else {
				path = encodeURIComponent(w0535);
			}
			
			//var url = this.getTemplateFileUrl(path, nbasea0100, nbasea0100_1);
//			tableHtml+="<td style='text-align:center;'><a onclick=window.open('"+url+"');><img src='/images/new_module/icon1.png' /></a></td>";//评审材料
			tableHtml+="<td style='"+tdStyle+"'>";//评审材料
			if(!Ext.isEmpty(w0535)){// 链接为空时，不加详情图标
				//haosl update 20170420 为链接添加timestamp当前时间的时间戳，区别相同链接
				var ahref = "/module/jobtitle/reviewfile/ViewTemplate.html?path="+path+"&user="+nbasea0100+"&timestamp="+new Date().getTime();
				if(isWord){
					ahref += "&isword=true";
				}
				tableHtml+="<a href='"+ahref+"'><img src='/images/new_module/icon1.png' /></a>";
			}
			tableHtml+="</td>";
			
			tableHtml+='</tr>';
		}
		tableHtml += '</table>';
		tableHtml += '</div >';
		
		/** 公告内容=文字+表格 */
		var d = new Date();
		var contentHtml = '';
		contentHtml+=
			'<div >' +
				'<p>' +
					'全体：<br />' +
					'关于' + Ext.getCmp("meetingCombo").getRawValue() + '的通知，详细如下：<br />' +
				'</p>' +tableHtml+
				'<p style="text-align:right;">' +
					//'<span">' +//此处加span标签 会引起插入分割线时，ie报错  haosl   20170401   delete
						/*'以上，如有异议请及时提示<br />' +
						'单位名称&nbsp;&nbsp;&nbsp;&nbsp;<br />' +*/
						d.getFullYear()+'年'+(d.getMonth()+1)+'月'+d.getDate()+'日' +
					//'</span>' +
				'</p>' +
			'</div>';
		return contentHtml;
	},
	// 获取【详情】列下载pdf链接
	getTemplateFileUrl:function(path, nbasea0100, nbasea0100_1){
		/** 解析path中的参数 */
		var tabid = "";
		var taskid = "";
		var taskid_validate = "";
		var index = path.indexOf("?");
		var paramStr =  path;
		if(index > -1){
			paramStr = path.substring(index+1);
		}
		var paramArray = new Array();
		paramArray = paramStr.split('&');
		for(var i=0; i<paramArray.length; i++){
			var param = paramArray[i];
			var key = param.split('=')[0];
			if(key == 'tabid'){
				tabid = param.split('=')[1];
			} else if(key == 'taskid'){
				taskid = param.split('=')[1];
			} else if(key == 'taskid_validate'){
				taskid_validate = param.split('=')[1];
			}
		}
		if(Ext.isEmpty(taskid_validate)){// 获取taskid的校验code
			var map = new HashMap();
			map.put("type", '2');
			map.put("taskid", taskid);
			Rpc({functionId:'ZC00003022',async:false,success:function(res){
				var result = Ext.decode(res.responseText);
				taskid_validate = result.taskid_validate;
			
			}},map);
		}
		// 配置参数 
		var obj={};
		obj.module_id="11";////调用模块标记：职称模块
		obj.return_flag="14";//返回模块标记：不需要返回关闭按钮
		obj.tab_id=tabid;//模板号
		obj.task_id=taskid;//任务号 除0以外需加密
		obj.approve_flag="0";//不启用审批
		obj.view_type="card";//卡片模式
		obj.card_view_type="1";//卡片模式下不要显示左边导航树
		obj.other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100+"`taskid_validate="+taskid_validate;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
		obj.callBack_init="JobTitleReviewFile.showView";
		
		var url = "";
		var map = new HashMap();
	    this.initPublicParam(map,obj);
		map.put("infor_type", '1');
		map.put("flag", '1');
		map.put("object_id", nbasea0100_1);
		map.put("cur_task_id", taskid);
		Rpc({
			functionId : 'MB000020014',
			async : false,
			success : function(form) {
				var result = Ext.decode(form.responseText);
				if (result.succeed) {
					var judgeisllexpr = result.judgeisllexpr;
					if (judgeisllexpr != null && judgeisllexpr != "1")
						alert(judgeisllexpr);
					else {
						var filename = result.filename;
						url = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + filename;
					}
				} else {
					alert(result.message);
				}
			}
		}, map);   
		
		return url;
	},
	// 初始化模板参数
	initPublicParam:function(map,templPropety) {
	    map.put("sys_type",templPropety.sys_type);
	    map.put("module_id",templPropety.module_id);
	    map.put("return_flag",templPropety.return_flag);
	    map.put("approve_flag",templPropety.approve_flag);    
	    map.put("tab_id",templPropety.tab_id);     
	    map.put("task_id",templPropety.task_id);
	    map.put("view_type",templPropety.view_type);
	    map.put("infor_type",templPropety.infor_type);
	    map.put("other_param",templPropety.other_param);
	},
	// 不支持审核时，隐藏【审核账号】【材料审核】按钮；否则显示。
	/*showHideMenuItems : function(enableSteps){
		//结束会议的归档按钮 haosl 20170613
		if(reviewfile_me.reviewFile_viewtype=='card')
			reviewfile_me.setBtnDisable(true);
		else{
			//结束时，按钮不可用
			reviewfile_me.setBtnDisable(reviewfile_me.isStop)
			if(reviewfile_me.isStop)
				return;
			//暂停状态下同步票数不可用 haosl
			if(reviewfile_me.isPause){
				var syncVotesBtn = Ext.getCmp("reviewfile_syncVotes");
				if(syncVotesBtn)
					syncVotesBtn.setDisabled(true);
			}else{
				var syncVotesBtn = Ext.getCmp("reviewfile_syncVotes");
				if(syncVotesBtn)
					syncVotesBtn.setDisabled(false);
			}
			//【审核账号】
			var key1 = Ext.getCmp('key1');
			if(key1){
				key1.hidden = !reviewfile_me.support_checking;
			}
			
			//审核阶段下如果只有同行阶段 则隐藏 材料审核按钮
			var step1 = enableSteps.step1_me;
			var step2 = enableSteps.step2_me;
			var step4 = enableSteps.step4_me;
			//【材料审核】
			var examine = Ext.getCmp('examine');
			if(examine){
					examine.setHidden(!(reviewfile_me.support_checking && (step1 || step2 || step4)));
			}
		}
	},
	showHideColumns : function(enableSteps){
		
		var step1 = enableSteps.step1;
		var step2 = enableSteps.step2;
		var step3 = enableSteps.step3;
		var step4 = enableSteps.step4;
		
		//会议本身启用的阶段
		var step1_me = enableSteps.step1_me;
		var step2_me = enableSteps.step2_me;
		var step3_me = enableSteps.step3_me;
		var step4_me = enableSteps.step4_me;

		//haosl delete 2017-07-07 没有阶段权限就不显示阶段，如果return掉生成账号密码按钮的权限则不受控制了
//		if(!step1 && !step2 && !step3 && !step4){
//			return ;
//		}
//		
		//需要隐藏的列
		var str1 = ",w0517,w0519,w0549,w0551,w0553,w0559,committeeagree,";//聘委会
		var str2 = ",w0521,w0543,w0545,w0547,group_id,w0557,subjectsagree,";//学科组
		var str3 = ",w0527,w0529,w0531,w0533,w0523,checkproficient,proficientagree,w0541,";//同行
		var str4 = ",w0563,w0565,w0567,w0569,committeename,w0571,collegeagree,";	//学院聘任组指标
			
		var columns = reviewfile_me.tableObj.tablePanel.columns;
		for(var index in columns){
			var dataIndex = columns[index].dataIndex;
			if(columns.hasOwnProperty(index)){
				
				if(str1.indexOf(dataIndex) > -1 ){
					columns[index].hidden = !step1;
					
				}else if(str2.indexOf(dataIndex) > -1){
					columns[index].hidden = !step2;
					
				}else if(str3.indexOf(dataIndex) > -1){
					columns[index].hidden = !step3;
					
				}else if(str4.indexOf(dataIndex) > -1){
					columns[index].hidden = !step4;
				}
				
				if("w0539" == dataIndex){//内部评审问卷计划号
					columns[index].hidden = (!step1 && !step2 && !step4);
				}
			}
		}
		//阶段显示隐藏 start
		if(step1){// 评委会
			if(Ext.getCmp("w0555_1"))
				Ext.getCmp("w0555_1").show();
		}else {
			if(Ext.getCmp("w0555_1"))
				Ext.getCmp("w0555_1").hide();
		}
		if(step2){// 学科组
			if(Ext.getCmp("w0555_2"))
				Ext.getCmp("w0555_2").show();
		}else {
			if(Ext.getCmp("w0555_2"))
				Ext.getCmp("w0555_2").hide();
		}
		
		if(step3){// 同行专家
			if(Ext.getCmp("w0555_3"))
				Ext.getCmp("w0555_3").show();
		}else {
			if(Ext.getCmp("w0555_3"))
				Ext.getCmp("w0555_3").hide();
		}
		
		if(step4){// 二级单位
			if(Ext.getCmp("w0555_4"))
				Ext.getCmp("w0555_4").show();
		}else {
			if(Ext.getCmp("w0555_4"))
				Ext.getCmp("w0555_4").hide();
		}
		//阶段显示隐藏  end
		if(step1_me){
			if(Ext.getCmp("create11"))
				Ext.getCmp("create11").show();
			if(Ext.getCmp("create21"))
				Ext.getCmp("create21").show();
		}else{
			if(Ext.getCmp("create11"))
				Ext.getCmp("create11").hide();
			if(Ext.getCmp("create21"))
				Ext.getCmp("create21").hide();
		}
		
		if(step2_me){
			if(Ext.getCmp("create12"))
				Ext.getCmp("create12").show();
			if(Ext.getCmp("create22"))
				Ext.getCmp("create22").show();
		}else{
			if(Ext.getCmp("create12"))
				Ext.getCmp("create12").hide();
			if(Ext.getCmp("create22"))
				Ext.getCmp("create22").hide();
		}
		if(step3_me){
			if(Ext.getCmp("create23"))
				Ext.getCmp("create23").show();
		}else{
			if(Ext.getCmp("create23"))
				Ext.getCmp("create23").hide();
		}
		if(step4_me){
			if(Ext.getCmp("create14"))
				Ext.getCmp("create14").show();
			if(Ext.getCmp("create24"))
				Ext.getCmp("create24").show();
		}else{
			if(Ext.getCmp("create14"))
				Ext.getCmp("create14").hide();
			if(Ext.getCmp("create24"))
				Ext.getCmp("create24").hide();
		}
	},*/
	// 获取会议数据源
	getMeetingData:function(){
		var map = new HashMap();
		map.put("type",'1');
		map.put("schemeType",this.schemeStateArray);
		var data = null;
		Rpc({functionId:'ZC00003022',async:false,scope:this,success:function(res){
			var response = Ext.decode(res.responseText);
			data = response.meetingdata;
		}},map);
		return data;
	},
	// 返回按钮
	returnPage: function(){
		window.history.go(-1);
	},
	/**
	 * 保存
	 */
	saveData : function(){
		var tablePanel =reviewfile_me.tableObj.tablePanel;
		var storeid="reviewFile_dataStore";
		var map = new HashMap();
	    var store=Ext.data.StoreManager.lookup(storeid);
		var updateList = store.getUpdatedRecords();//修改过的数据
	    var updaterecord = [];
    	if(updateList.length>0){
    		for(var i=0;i<updateList.length;i++){
    			var record = updateList[i].data;
				updaterecord.push(record);
			}
    	}
        map.put("savedata",updaterecord);
        Rpc({functionId:'ZC00003012',async:false,scope:this,success:function(res){
			var data = Ext.decode(res.responseText);
			if(!Ext.isEmpty(data.msg)){
				Ext.showAlert(data.msg);
				var store = Ext.data.StoreManager.lookup(storeid);
				store.load();
			}else{
				Ext.showAlert(data.message);
			}
		}},map);
	},
	/**
	 * 同步票数
	 * haosl
	 * 2017-07-18
	 */
    syncVotes:function(){
       var meettingCombo = Ext.getCmp('meetingCombo')
       var w0301 = "";
       if(meettingCombo.getValue()){
      	  var meetingComboRecord = meettingCombo.findRecordByDisplay(meettingCombo.getRawValue());
      	  w0301 = meetingComboRecord.data.w0301.split("_")[1]; //w0301_xxxxxx
       }
  	   if(Ext.isEmpty(w0301))
  		   return;
	   var map = new HashMap();
	   map.put("w0301",w0301);
	   Rpc({functionId:'ZC00005010',async:false,scope:this,success:function(res){
		   var data = Ext.decode(res.responseText);
			if(!data.succeed){
				Ext.showAlert(data.message);
			}else{
				reviewfile_me.approvalPersonSet = reviewfile_me.getPersonSet(w0301);
				reviewfile_me.loadTable();
			}
			}},map); 
   },
   /**
   *保存栏目设置回调
   */
   schemeSetting_callBack:function(){
   			var url = '/module/jobtitle/reviewfile/ReviewFile.html?id='+reviewfile_me.seletedW0301+'&st='+reviewfile_me.schemeStateArray[0];
   			window.location.href=url;
   }
   
});
