/**
 * liuy add 2015/12/09
 * 评审会议
 */
Ext.define("ReviewMeetingURL.ReviewMeeting",{
	tableObj:undefined,
	columns:'',
	store:'',
	orgid:'',
	constructor:function(config) {
		meeting_me = this;
		this.init();
	},
	//初始加载页面
	init:function(url) {
	    w0323 = [{'dataValue':'2','dataName':'否'}, {'dataValue':'1','dataName':'是'}];
	    Rpc({functionId:'ZC00002301',async:false,success:this.getTableOK,scope:this},new HashMap());
	},
	//加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		obj.openColumnQuery = true;//haosl 2017-07-31 方案查询可以查询自定义指标
		var tableObj = new BuildTableObj(obj);
		meeting_me.tableObj = tableObj;
		
		var searchSchemePanel = this.searchSchemeView();
		this.tableObj.insertItem(searchSchemePanel, 0);// 插入方案查询
		
		meeting_me.columns=meeting_me.tableObj.tablePanel.columns;
		meeting_me.commiteeInfo = result.commiteeInfo;
		//默认显示全部，为“全部”链接添加选中样式
		var all = Ext.getCmp("all");
		all.addCls('scheme-selected-cls');
	},
	//添加评审会议
	addMeeting:function(){
        var storeid="meeting_dataStore";
		meeting_me.store=Ext.data.StoreManager.lookup(storeid);
		var columns=Ext.getCmp("meeting_tablePanel").columns;
		var record =meeting_me.getNewRecord(columns);	
	    meeting_me.store.insert(0,/*meeting_me.store.getCount(),*/record);	
	    meeting_me.sortstore(meeting_me.store); 
	},
	//返回新增的记录 空记录
    getNewRecord:function(columns){
		var strRecord ="I9999:'-1'";
		for(var i=0;i<columns.length;i++)
		{	
			var column=columns[i].dataIndex;
			var strFieldValue=column+":"+"'"+""+"'";
			strRecord=strRecord+","+strFieldValue;
		}
		strRecord+=",w0321_h:''"
		strRecord="{"+strRecord+"}";
		var record= Ext.decode(strRecord);
		return record;
	},
	//对store中的数据进行index赋值
    sortstore:function(store)
	{
		for(var i=0;i<store.getCount();i++){
	       var rec = store.getAt(i);
	       rec.data.index = i;
	    }
	},
	//权限验证可以编辑的列
	checkCell:function(record){
		var w0321 = record.data.w0321;
		if(w0321.indexOf('06')!=-1)//执行中状态的评审会议不能编辑
			return false;
		if(w0321.indexOf('05')!=-1)//结束状态的评审会议不能编辑
			return false;
		var b0110 = record.data.b0110;
		if(meeting_me.orgid==''){//登录人职称管理权限
			return true;
		}else{
			var orgarr = meeting_me.orgid.split(',');
			for(var i=0;i<orgarr.length;i++){
				var org = orgarr[i];
				if(org==b0110||b0110.indexOf(org)==0)
					return true;
				else{
					for(var k=org.length-2;k>0;k--){
	         			org = org.substring(0,k);
	         			if(org==b0110)
	         				return false;
	         			k--;
         			}
				}
			}
		}
	},
	//撤销起草状态评审会议
	deleteMeeting:function(){
		var tablePanel=meeting_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.showAlert("请选择要撤销的会议！");
			return;
		}
		var list  =[];
		for(var i=0;i<records.length;i++){
			var w0321 = records[i].data.w0321_h;
			//执行中、结束、暂停状态的评审会议不能删除
			if(w0321.indexOf('06')!=-1||w0321.indexOf('05')!=-1||w0321.indexOf('09')!=-1){
				Ext.showAlert("只能撤销起草状态的评审会议！");
				return;
			}
			var w0301 = records[i].data.w0301_e;
			if(w0301)
				list.push(w0301);
		}
		Ext.Msg.confirm('提示信息',"确定要撤销选中会议吗？",function(btn){
			if(btn == 'yes'){
				var storeid="meeting_dataStore";
			    var store=Ext.data.StoreManager.lookup(storeid);
				if(list.length<0){ 
					store.remove(records);
					meeting_me.sortstore(store);
					return;
				}
				var vo = new HashMap();
			    vo.put("idlist", Ext.encode(list));
				Rpc({functionId:'ZC00002302',async:false,success:function(res){
					var resultObj = Ext.decode(res.responseText);
					store.remove(records);
					meeting_me.sortstore(store);
				}},vo);	
			}
		});	
		
	},

	/**
	 * 保存专家
	 * callBack:保存回调，用于打开选择参会人数页面
	 */
	saveMeeting:function(isflag,callBack){
		var tablePanel = meeting_me.tableObj.tablePanel;
		var storeid="meeting_dataStore";
		var hashvo = new HashMap();
	    var store=Ext.data.StoreManager.lookup(storeid);
		var updateList = store.getUpdatedRecords();//修改过的数据
		var addList = store.getNewRecords();
		if(updateList.length==0 && addList.length == 0){
			if(typeof callBack == "function")
			   callBack();//保存操作完成后打开参会界面
    	   return;
		}
    	var addrecord = [];
		if(addList.length>0){
			for(var i=0;i<addList.length;i++){
				var record = addList[i].data;
				addrecord.push(record);
			}
		}
	    var updaterecord = [];
    	if(updateList.length>0){
    		for(var i=0;i<updateList.length;i++){
    			var record = updateList[i].data;
    			var w0325 = record.w0325;
    			if(w0325)//未授权同行阶段时，无需保存同行是否启用  haosl 2018-2-1
    				record.w0325 = w0325.split("`").length==2?w0325.split("`")[0]:w0325;
				updaterecord.push(record);
			}
    	}
    	hashvo.put("addrecord",addrecord);
        hashvo.put("updaterecord",updaterecord);
        Rpc({functionId:'ZC00002303',sync:false,scope:this,success:function(res){
        	if(typeof callBack == "function")
        		callBack();//保存操作完成后打开参会界面
        	if(!isflag){//是否需要提示信息和刷新页面
				var resultObj = Ext.decode(res.responseText);
				if(resultObj.result!=undefined && !resultObj.result){
					Ext.showAlert("保存失败！");
					return;
				}
				Ext.showAlert("保存成功！");
			}
			var store = Ext.data.StoreManager.lookup('meeting_dataStore');
			store.load();
		}},hashvo);
	},
	//启动评审会议
	startTheMeeting:function(){
		var tablePanel=meeting_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.showAlert("请选择一个评审会议！");
			return;
		}
		var list  =[];
		for(var i=0;i<records.length;i++){
			var w0301 = records[i].data.w0301_e;
			if(w0301)
				list.push(w0301);
		}
		if(list.length==0){
			Ext.showAlert("请先保存评审会议！");
			return;
		}
		
		Ext.Msg.confirm('提示信息',"确定要启动选中会议吗？",function(btn){
			if(btn == 'yes'){
				var vo = new HashMap();
			    vo.put("idlist", Ext.encode(list));
			    vo.put("type", "1");
				Rpc({functionId:'ZC00002305',async:false,success:function(res){
					var resultObj = Ext.decode(res.responseText);
					 var msg = resultObj.msg;
					 if(resultObj.flag=='1'){
					 	Ext.showAlert(msg+"&nbsp;&nbsp;&nbsp;&nbsp;");
					    return;
					 }else if(resultObj.flag=='0') {
						var storeid="meeting_dataStore";
			        	var store=Ext.data.StoreManager.lookup(storeid);
			        	store.load();
						if(msg != '')
							Ext.showAlert(""+msg+"");
						meeting_me.sortstore(store);
					 }
				}},vo);
			}
		});
	},
	//暂停评审会议
	pauseMeeting:function(){
		var tablePanel=meeting_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.showAlert("请选择一个评审会议！");
			return;
		}
		var list  =[];
		for(var i=0;i<records.length;i++){
			var w0301 = records[i].data.w0301_e;
			if(w0301)
				list.push(w0301);
		}
		if(list.length==0){
			Ext.showAlert("请先保存评审会议！");
			return;
		}
		Ext.Msg.confirm('提示信息',"确定要暂停选中会议吗？",function(btn){
			if(btn == 'yes'){
				var vo = new HashMap();
			    vo.put("idlist", Ext.encode(list));
			    vo.put("type", "2");
				Rpc({functionId:'ZC00002305',async:false,success:function(res){
					 var resultObj = Ext.decode(res.responseText);
					 var msg = resultObj.msg;
					 if(resultObj.flag=='1'){//
					 	Ext.showAlert(""+msg+"");
					    return;
					 }else if(resultObj.flag=='0') {
						var storeid="meeting_dataStore";
			        	var store=Ext.data.StoreManager.lookup(storeid);
			        	store.load();
						if(msg != '')
							Ext.showAlert(""+msg+"");
						meeting_me.sortstore(store);
					 }
				}},vo);
			}
		});
	},
	//选择参会人员
	openChoosePage:function(value,meta,record){
		if(value=='')
			value='0';
		var columnName = meta.column.dataIndex;
 		var committee_id = "";
 		var typeCommittee = "";
 		if(columnName == 'w0323'){
 			committee_id = record.get("sub_committee_id");
 			typeCommittee = 4;
 		}else if(columnName == 'w0315'){
 			committee_id = record.get("committee_id");
 			typeCommittee = 1;
 		}
		var html="<a style=\"cursor:pointer;\" href='javascript:meeting_me.openPage(\""+record.get("w0301_e")+"\",\""+committee_id+"\",\""+record.get("w0321_h")+"\","+typeCommittee+");'>"+value+"<a/>";
		return html;
	},
	//打开参会人员页面
	openPage:function(w0301,committee_id,w0321,typeCommittee){
		if(w0301=='undefined'){
			return;
		}
		
		//通过给saveMeeting传入回调，打开选人窗口，避免选人窗口加载不完刚刚自动关联的人  haosl 20170621
		meeting_me.saveMeeting(true,function(){
			if(Ext.isEmpty(committee_id) || committee_id=="null"){//判断是否选择了聘委会
				Ext.showAlert("请先选择一个"+zc.menu.committeeshowtext+"！");
				return;
			}
			Ext.require("ReviewMeetingURL.ChoosePerson",function(){
				var re = Ext.create("ReviewMeetingURL.ChoosePerson",{
					width:'800',
					height:'500',
					title:'执行评委',
					w0301:w0301,
					w0321:w0321,
					typeCommittee:typeCommittee,
					committee_id:committee_id
				});
			});
		});
//		var storeid="meeting_dataStore";
//	   	var store = Ext.data.StoreManager.lookup(storeid);
//		store.on('load', function(){
			
//		}, this, {single:true});

		
//		var storeid="meeting_dataStore";
//       	var store=Ext.data.StoreManager.lookup(storeid);
//		store.reload();
//		meeting_me.sortstore(store);
	},
	//关联学科组
	openSubjects:function(value,meta,record){
		
		var w0301 = record.get("w0301_e");
		var w0321_h = record.get("w0321_h");

		var html = '';
		if(Ext.isEmpty(w0301)){// 新增的会议w0301为空无法分配评审成员，故不显示<a>链接
			html = "<span>评审成员<span/>";;
		} else {
			html = "<a style=\"cursor:pointer;\" href='javascript:meeting_me.openSubjectsPage(\""+w0301+"\",\""+w0321_h+"\");'>"+"评审成员"+"<a/>";
		}
		return html;
	},
	//打开评审成员页面
	openSubjectsPage:function(w0301,w0321){
		var readonly = '1'
		if(w0321.indexOf('01')!=-1||w0321.indexOf('09')!=-1)//暂停或起草状态的评审会议才能维护学科组成员
			readonly = '0';
		Ext.require('JobtitleSubjects.SubjectsForMeeting', function(){
			RevewFileGlobal = Ext.create("JobtitleSubjects.SubjectsForMeeting", {w0301:w0301,readonly:readonly});
		});
	},
	//【发送通知】按钮
	sendMessage:function(){
		var tablePanel=meeting_me.tableObj.tablePanel;
		var records = tablePanel.getSelectionModel().getSelection();
		if(records.length < 1){
			Ext.showAlert("选择要发送通知的评审会议！");
			return;
		}
		if(records.length > 1){
			Ext.showAlert("只能选择一个评审会议！");
			return;
		}
		Ext.require('SendMessageURL.SendMessage', function(){
			RevewFileGlobal = Ext.create("SendMessageURL.SendMessage", {});
		});
	},
	/**
	 * 解决权限范围外的评委会显示为代码号的问题
	 */
	committeeRenderer:function(value, metaData, record, rowIndex){
		return meeting_me.commiteeInfo[value];
	},
	// 查询方案区域
	searchSchemeView:function(meetingList){
		
		this.schemeStateArray = ['0', '0'];
		
		if(!Ext.util.CSS.getRule('.scheme-selected-cls a')){
			Ext.util.CSS.createStyleSheet(".scheme-selected-cls a{text-decoration:underline !important;}","underline");
		}
		
		var panel = Ext.widget('panel',{ 
			id:'reviewfile_schemePanel',
			padding:'5 0 5 0',
			layout: {
		        type: 'hbox'
		    },
			defaults: {
		        margin:'0 0 0 10'
		    },
			border:false,
			items:[{
					xtype : 'label',
					margin:'0 0 0 3',
		            text: zc.label.searchScheme+'：'
	            },{
	            	xtype : 'label',
	            	id:'all',
	                html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('all');>"+zc.label.all+"</a>"//全部
	            },{
	            	xtype : 'label',
	            	style:'color:#C5C5C5;',
	            	text:"|"
	            },{
	            	xtype : 'label',
	            	id:'nowyear',
	                html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('nowyear');>本年度</a>"//本年度
	            },{
	            	xtype : 'label',
	            	id:'preyear',
	                html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('preyear');>上年度</a>"//上年度
	            },{
	            	xtype : 'label',
	            	style:'color:#C5C5C5;',
	            	text:"|"
	            },{
		        	xtype : 'label',
		        	id:'init',
		            html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('init');>起草</a>"//起草
		        },{
		        	xtype : 'label',
		        	id:'in',
		            html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('in');>执行中</a>"//执行中
		        },{
		        	xtype : 'label',
		        	id:'finish',
		            html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('finish');>结束</a>"//结束
		        },{
		        	xtype : 'label',
		        	id:'stop',
		            html:"<a href=javascript:ReviewMeetingGlobal.searchScheme('stop');>暂停</a>"//暂停
		        }]
		});
		
		return panel
	},
	// 查询方案
	searchScheme:function(schemeId){
		var searchBox = Ext.getCmp("meeting_querybox");
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

		if(meeting_me.schemeStateArray != undefined && meeting_me.schemeStateArray.indexOf(schemeId) > -1){//查询方案中已经有了，不进行
			return ;
		}
	    
		// 移除当前组选中样式
		var group1 = ['nowyear','preyear'];//时间组
		var group2 = ['init','in','stop','finish'];//会议状态组
		var removeArr = new Array();
		if(schemeId == 'all'){//全部，复位所有
			removeArr = meeting_me.schemeStateArray;
			meeting_me.schemeStateArray = ['0', '0'];
			
		} else if(group1.indexOf(schemeId) > -1){//时间组
			removeArr.push(meeting_me.schemeStateArray[0]);
			removeArr.push('all');
			meeting_me.schemeStateArray[0] = schemeId;
			
		} else if(group2.indexOf(schemeId) > -1){//会议状态组
			removeArr.push(meeting_me.schemeStateArray[1]);
			removeArr.push('all');
			meeting_me.schemeStateArray[1] = schemeId;
			
		}
		for(var p in removeArr){
			var tmp = Ext.getCmp(removeArr[p]);
			if(tmp){
				tmp.removeCls('scheme-selected-cls');
			}
		}
		// 添加选中样式
		var selected = Ext.getCmp(schemeId);
		if(selected){
			selected.addCls('scheme-selected-cls');
		}
		
	    var map = new HashMap();
		map.put("type", "3");
		map.put("subModuleId", "zc_reviewmeeting_00001");
		map.put("schemeTypeArray", meeting_me.schemeStateArray);
	    Rpc({functionId:'ZC00002301',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);	
			var errorcode = result.errorcode;
			if(errorcode == 0){
			    var store = Ext.data.StoreManager.lookup('meeting_dataStore');
			    store.load();
			}
			
	    }},map);
	    // 快速查询恢复
	    if(searchBox){
	    	searchBox.removeAllKeys();
	    }
	},
	/**
	 * 同行专家评议列
	 *  haosl
	 */
	outsideAndSubjecetsRenderer:function(value,metaData,record,rowIndex,colIndex,store){
		var id = rowIndex;
		var w0321 = record.data.w0321_h;
		var html='';
		var checked ='';
		var disabled = '';
		if(w0321.indexOf('05')!=-1 || w0321.indexOf('06')!=-1){
			disabled = 'disabled';
		}
		var temp = value.split("`").length==2?value.split("`")[0]:value;
		if(temp==1){
			checked='checked=checked';//选中
		}
		html = "<div style='width:100%;'><input "+disabled+" onclick='meeting_me.setW0325Col("+rowIndex+")' id="
				+id+" type='checkbox' "+checked+"/><label style='margin-left:3px;margin-top:3px;' for='"
				+id+"'>启用</label>";
		return html;
	},
	// 同行专家启用
	setW0325Col:function(index){
		var storeid="meeting_dataStore";
	    var store=Ext.data.StoreManager.lookup(storeid);
		var record = store.getAt(index);
		var checked = document.getElementById(index).checked;
		if(checked)
			record.set("w0325",'1');	//选中为1
		else
			record.set("w0325",'2');//没有选中为2
	},
	// 起草以外的会议渲染链接，可直接跳转至上会材料页面
	toReviewfilePage :function(value,metaData,record,rowIndex,colIndex,store){
		if(Ext.isEmpty(value)){//新增会议时value值是空，不需要额外渲染。
			return ;
		}
		var w0301 = record.data.w0301_e;
		var w0321 = record.data.w0321_h;//起草：01 进行中：05 暂停：09 结束：06
		
		if(Ext.isEmpty(w0301)){//新增会议时w0301值是空，不需要额外渲染。
			return value;
		}
		
		var w0321Str = '';
		if(w0321 == '01'){//起草状态的会议，不需要增加链接。
			return value;
		}else if(w0321 == '05'){//起草以外的会议。增加链接，可以直接跳转至上会材料页面。
			w0321Str = 'in';
		}else if(w0321 == '09'){
			w0321Str = 'stop';
		}else if(w0321 == '06'){
			w0321Str = 'finish';
		}
		var url = '/module/jobtitle/reviewfile/ReviewFile.html?id='+w0301+'&st='+w0321Str;
		return "<a href='"+url+"'>"+value+"</a>";
	}
});
