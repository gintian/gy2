/**
 * hej add 2015/11/18
 * 专家库
 */
Ext.define("ExpertDatabaseURL.ExpertDatabase",{
	requires:["SYSF.FileUpLoad","SYSP.ImageEditingWin"],
	tableObj:undefined,
	columns:'',
	store:'',
	idlist:'',
	fileRootPath:'',
	orgid:'',
	func:'',
	constructor:function(config) {
		experts_me = this;
		experts_me.init();
	},
	/**
	 * 初始加载页面
	 */
	init:function() {
	    Rpc({functionId:'ZC00002001',async:false,success:experts_me.getTableOK},new HashMap());
	},
	/**
	 * 加载表单
	 */
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		experts_me.idlist = result.idlist;
		experts_me.fileRootPath= result.fileRootPath;
		experts_me.orgid = result.orgid;
		experts_me.func = result.func;
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		obj.openColumnQuery = true;//haosl 20161014方案查询可以查询自定义指标
		experts_me.tableObj = new BuildTableObj(obj);
		experts_me.columns=experts_me.tableObj.tablePanel.columns; 
		
		
		//var map = new HashMap();
//		map.put("url",url);
//		experts_me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
////			renderTo : "fastsearch",
//			emptyText:"请输入单位名称、部门、姓名...",
////			hideQueryScheme:true,
//			subModuleId:"zc_reviewmeeting_experts_00001",
//			customParams:map,
//			funcId:"ZC00002008",
//			fieldsArray:result.fieldsArray,
//			success:experts_me.loadStore
//		});
//		var toolbar = Ext.getCmp('experts_toolbar');
//		toolbar.add(experts_me.SearchBox);
		
		// 在照片窗口显示时，点击了按钮或者查询组件，则自动关闭照片
		if(Ext.get('experts_toolbar')!=null){
			Ext.get('experts_toolbar').on('click', function(e){
				var imgWin = Ext.getCmp('imagewin');
				if(imgWin){
					imgWin.close();
				}
			});
		}
		
	},
	loadStore:function(){
		var store = Ext.data.StoreManager.lookup('experts_dataStore');
		store.currentPage=1;
		store.load();
	},
	/**
	 * 引入专家
	 */
	importExperts:function(){
		var f = document.getElementById("importExpert");
		var orgid = '';
		if(experts_me.orgid=='UN`'){
			orgid = '';
		}else{
			orgid = experts_me.orgid;
		}
		
		//获取需要排除的人员
		var map = new HashMap();
		map.put("subModuleId", 'zc_reviewmeeting_experts_00001');
		map.put("type", '3');
	    Rpc({functionId:'ZC00002008', async:false, success:function(form){
	    	var result = Ext.decode(form.responseText);
			var experts = result.experts;
	    	
			var picker = new PersonPicker({
				multiple: true,
				orgid: orgid, 
				deprecate : experts,
				isPrivExpression:false,
				text: "选择人员",
				titleText:"请选择",
				callback: function (c) {
						var staffids = "";
						for (var i = 0; i < c.length; i++) {
							staffids += c[i].id + "'";
						}
						var hashvo = new HashMap();
						hashvo.put("ids",staffids);
						Rpc({
							functionId : 'ZC00002002', success : function(form, action) {
								var result = Ext.decode(form.responseText);
								var flag = result.succeed;
								if (result.msg != '') {
									Ext.showAlert(result.msg);
								}
								var store = Ext.data.StoreManager.lookup('experts_dataStore');
								var store = Ext.data.StoreManager.lookup('experts_dataStore');
								store.load();
							}
						}, hashvo);
				}}, f);
			picker.open();
	    	
	    }, scope:this},map);
		
	},
	// 条件引入
	importExpertsFilter:function(){
		var map2 = new HashMap();
		 map2.put(1, "a,b,k");//要显示的子集
		 var map = new HashMap();
		 map.put('salaryid', '');
		 map.put('condStr', '');//复杂条件，简单条件表达式
		 map.put('cexpr', '');//简单公式时：1*2
		 map.put('path', "2306514");
		 map.put('priv', "0");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
		 map.put('info_type', "1");//=1人员,=2单位,=3职位
		 Ext.require('EHR.selectfield.SelectField',function(){
			 Ext.create("EHR.selectfield.SelectField", {
								imodule : "9",
								type : "1",
								queryType : "1",
								dataMap : map,
								comBoxDataInfoMap : map2,
								rightDataList : '',
								title : "选择指标",
								flag:'1',//允许选择相同的字段  haosl 2017-07-19
								isShowResult : true,
								queryCallbackfunc :function(c){
									var staffids = "";
									for (var i = 0; i < c.length; i++) {
										staffids += c[i] + "'";
									}
									var hashvo = new HashMap();
									hashvo.put("ids",staffids);
									Rpc({
										functionId : 'ZC00002002', success : function(form, action) {
											var result = Ext.decode(form.responseText);
											var flag = result.succeed;
											if (result.msg != '') {
												Ext.showAlert(result.msg);
											}
											var storeid="experts_dataStore";
											var store = Ext.data.StoreManager.lookup(storeid);
											store.load();
										}
									}, hashvo);
								} 
			});
		 });
	},
	//添加专家
	addExpert:function(){
        var storeid="experts_dataStore";
		experts_me.store=Ext.data.StoreManager.lookup(storeid);
		var tablePanel = Ext.getCmp("experts_tablePanel");
		var columns=tablePanel.columns;
		var record =experts_me.getNewRecord(columns);	
		record.w0109='1`是';//添加外部专家，默认设置为可聘任  haosl add 2017-8-28
		record.w0111='1`是';
		record.b0110 = experts_me.func;
		record.changestate='add';
	    experts_me.store.insert(experts_me.store.getCount(),record);
	    var cellediting = tablePanel.findPlugin('cellediting');//获得编辑器组件
	    var selectRecords = experts_me.store.getNewRecords();//获得新增记录的数组
	    var lastRecord = selectRecords[selectRecords.length-1];//最后新增的记录
	    tablePanel.getSelectionModel().select([lastRecord]);	//选中最后新增的记录
	    cellediting.startEdit(lastRecord,1);					//新增记录获得编辑组件获得光标
	    experts_me.sortstore(experts_me.store); 
	},
	personInfoSyn:function(){  //changxy 人员信息同步  20160721
		Rpc({functionId:'ZC00005004',async:false,success:function(form,action){
		         var result = Ext.decode(form.responseText);
		         var flag=result.succeed;
                            if(result.status=="1"){
                                Ext.showAlert("信息同步成功！");
                            }else{
                              Ext.showAlert(result.message);
                            }
                    		var store = Ext.data.StoreManager.lookup('experts_dataStore');
                            store.reload();
		}},new HashMap());
	},
	// 撤销
	cancelExpert:function(){
		var tablePanel=experts_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.showAlert("请选择撤销数据！");
			return;
		}
		var list  =[];
		for(var i=0;i<records.length;i++){
			var record =  records[i];
			var data = record.data;
			var w0101 = data.w0101;
			if(w0101==undefined){
				var storeid="experts_dataStore";
	        	var store=Ext.data.StoreManager.lookup(storeid);
				store.remove(record);
				//Ext.Msg.alert('提示信息',"未保存，不能撤销！");
				return;
			}
			if(!experts_me.checkCell(record)){//撤销时，判断专家是否有操作权限
				Ext.showAlert("您没有删除该专家的权限！");
				return;
			}
			list.push(w0101);
		}
		 var vo = new HashMap();
	    	     vo.put("idlist", Ext.encode(list));
		Rpc({functionId:'ZC00002005',async:false,success:function(res){
			 var resultObj = Ext.decode(res.responseText);
			 if(resultObj.flag=='1'){//不可删除
			 	var nodelStr = resultObj.nodelStr;
			 	Ext.showAlert("您选择的"+nodelStr+"专家参与过职称评审工作，不能撤销！");
			    return;
			 }
			 else if(resultObj.flag=='0') {
			 	Ext.Msg.confirm('提示信息',"确定要撤销吗？",function(btn){
			 		if(btn == 'yes'){
			 			var storeid="experts_dataStore";
			        	var store=Ext.data.StoreManager.lookup(storeid);
						store.remove(records);
						experts_me.sortstore(store);
			 		}else{
			 			return;	
			 		}
			 	})
			 }
		}},vo);

	},
	/**
	 * 保存专家
	 */
	saveExpert:function(){
		var tablePanel = experts_me.tableObj.tablePanel;
		var storeid="experts_dataStore";
		var hashvo = new HashMap();
	    var store=Ext.data.StoreManager.lookup(storeid);
		var addOrUpdateList = store.getModifiedRecords();//修改过的数据
		if(addOrUpdateList.length==0)
    	   return;
    	var addrecord = [];
		var addList = store.getNewRecords();
		if(addList.length>0){
				for(var i=0;i<addList.length;i++){
				var record = addList[i].data;
				//新增的只能是外部专家
				var w0111 = record.w0111;
				var w0107 = record.w0107;
				if(Ext.isEmpty(w0107)){
					Ext.showAlert("专家姓名不能为空！");
					return;
				}
				var index = w0111.indexOf('`');
		        w0111 = w0111.substring(0,index);
		        if(w0111=='2'){
		        	Ext.showAlert("手动添加的专家只能是外部专家！");
		        	return;
		        }
				if(!record.changestate)//添加新增标识，说明是新增操作。
					record.changestate='add';
				addrecord.push(record);
			}
		}
	    var updaterecord = [];
    	var updateList = Ext.Array.difference(addOrUpdateList,addList);
    	if(updateList.length>0){
    		for(var i=0;i<updateList.length;i++){
				var record = updateList[i].data;
				var w0111 = record.w0111;
				var w0107 = record.w0107;
				if(Ext.isEmpty(w0107)){
					Ext.showAlert("专家姓名不能为空！");
					return;
				}
				var index = w0111.indexOf('`');
		        w0111 = w0111.substring(0,index);
				if(!record.changestate)//添加修改标识，说明是修改操作。
					record.changestate='update';
				updaterecord.push(record);
			}
    	}
    	var length = experts_me.idlist.length;
    	var idlist = experts_me.idlist.substring(1,length-1);
    	var idarr = idlist.split(',');
    	hashvo.put("addrecord",addrecord);
        hashvo.put("updaterecord",updaterecord);
        hashvo.put("idarr",idarr);
        Rpc({functionId:'ZC00002007',scope:this,success:function(res){
				var resultObj = Ext.decode(res.responseText);
				if(resultObj.result!=undefined && !resultObj.result){
					Ext.showAlert("保存失败！");
					return;
				}
				Ext.showAlert("保存成功！");
			    var store = Ext.data.StoreManager.lookup('experts_dataStore');
			    store.reload();
			}},hashvo);

	},
	/**
	 * 照片
	 */
	imageExpert:function(param){
		var tablePanel=experts_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(param.enterflag=='1'){
			if(records.length<1){
				Ext.showAlert("请选择一个专家！");
				return;
			}else if(records.length>1){
				Ext.showAlert("一次只能选择一个专家查看照片！");
				return;
			}
			var w0101 = records[0].data.w0101;
			if(w0101==undefined){
				Ext.showAlert("请先保存当前专家信息！");
				return;
			}
		}
		var a0100 = records[0].data.a0100;//内部专家编号
		var nbase = records[0].data.nbase;//专家库前缀
		var w0111 = records[0].data.w0111;//是否外部专家
		var index = w0111.indexOf('`');
		w0111 = w0111.substring(0,index);
		var w0101 = records[0].data.w0101;
		var vo = new HashMap();
		vo.put("w0101",w0101);
		vo.put("a0100",a0100);
		vo.put("nbase",nbase);
		vo.put("quality","h");
		vo.put("flag","1");
		Rpc({functionId:'ZC00002003',scope:this,success:function(res){
				var resultObj = Ext.decode(res.responseText);
				var fileid = resultObj.fileid;
				var flag = resultObj.flag;
//				var filePath = resultObj.filePath;
//				var pubRealPath = resultObj.pubRealPath;
//				var checkflag = resultObj.checkflag;
//				if(checkflag==false){
//					Ext.Msg.alert('提示信息',"您设置的文件存放目录不存在！");
//					return;
//				}
				var a0100 = resultObj.a0100;//内部专家编号
				var nbase = resultObj.nbase;//专家库前缀
				var quality = resultObj.quality;//
				var fileSizeLimit = resultObj.fileSizeLimit;
				if(w0111=='1'){//外部专家
					var imageButton = Ext.getCmp("imageButton");
					experts_me.imagewin = Ext.create('SYSP.ImageEditingWin',{ 
						renderTo:Ext.getBody(),
						flag:'1',
						modal:false,
						isupload:'1',
						fileid:fileid,
						w0101:w0101,
//						imageheight:400,
//						imagewidth:400,
//						filePath:filePath,
//						pubRealPath:pubRealPath,
//						fileSizeLimit:fileSizeLimit,
						bencrypt:'true',
						imagename:w0101,
						callback:experts_me.beforeClose
					}).show();
					//原先进入的时候不会显示，因为控件没有接收这些参数，这里直接调用一下changeImage，不修改控件让显示
					var vo = new HashMap();
//					vo.put("filePath",filePath);
					vo.put("imagename",w0101);
					vo.put("mobile","");
					vo.put("filename","");
					vo.put("perguid","");
//					vo.put("pubRealPath",pubRealPath);
//					vo.put("bencrypt",'true');
					vo.put("isupload","1");
					vo.put("fileid",fileid);
					experts_me.imagewin.changeImage('1',vo);
					//imageButton.setDisabled(true);
				}
				else if(w0111=='2'){//内部专家
					var imageButton = Ext.getCmp("imageButton");
					experts_me.imagewin = Ext.create('SYSP.ImageEditingWin',{ 
						renderTo:Ext.getBody(),
						flag:flag,//1为通过路径查找图片，2通过人员库前缀和人员编号
						modal:false,
						isupload:'2',
						fileid:fileid,
						w0101:w0101,
//						filePath:filePath,
//						pubRealPath:pubRealPath,
						bencrypt:'true',
//						imageheight:400,
//						imagewidth:400,
						nbase:nbase,
						a0100:a0100,
						quality:quality,
						callback:experts_me.beforeClose
					}).show();
					//原先进入的时候不会显示，因为控件没有接收这些参数，这里直接调用一下changeImage，不修改控件让显示
					var vo = new HashMap();
					vo.put("a0100",a0100);
					vo.put("nbase",nbase);
					vo.put("quality",quality);
					vo.put("isupload","2");
					vo.put("fileid",fileid);
					experts_me.imagewin.changeImage('2',vo);
					imageButton.setDisabled(true);
			}
		}},vo);
	},
	/**
	 * 窗口关闭之前执行方法
	 */
	beforeClose:function(){
		var imageButton = Ext.getCmp("imageButton");
		imageButton.setDisabled(false);
	},
	/**
	 * 返回新增的记录 空记录
	 */
    getNewRecord:function(columns)
	{
		var strRecord ="I9999:'-1'";
		for(var i=0;i<columns.length;i++)
		{	
			var column=columns[i].dataIndex;
			var strFieldValue=column+":"+"''";
			strRecord=strRecord+","+strFieldValue;
		}
		strRecord="{"+strRecord+"}";
		var record= Ext.decode(strRecord);
		return record;
	}, 
	/*
	 *  对store中的数据进行index赋值 
	 */
    sortstore:function(store)
	{
		for(var i=0;i<store.getCount();i++){
	       var rec = store.getAt(i);
	       rec.set('index',i);
	    }
	},
	/**
	 * 通过支撑管理权限验证可以编辑的列
	 * @param {} record
	 * @return {Boolean}
	 */
	checkCell:function(record){
		var rowdata = record.data;
		if(experts_me.imagewin==undefined||experts_me.imagewin.imageopenflag==false){
			if(rowdata.changestate=='add'){
			}else{
				var b0110 = rowdata.b0110;//每一个专家的所属单位
				if(experts_me.orgid==''){//登录人职称管理权限
					return false;
				}else if(experts_me.orgid=='UN`'){
					return true;
				}else{
					var orgarr = experts_me.orgid.split(',');
					for(var i=0;i<orgarr.length;i++){
						var org = orgarr[i];
						if(b0110){
							var index = b0110.indexOf('`');
							b0110 = b0110.substring(0,index);
							if(org.length>b0110.length){
								return false;
							}else{
								return true;
							}
						}
						else{
							return false;
						}
					}
				}
			}
		}else{//图片窗口打开
			var w0111 = rowdata.w0111;
			var a0100 = rowdata.a0100;//内部专家编号
			var nbase = rowdata.nbase;//专家库前缀
			var index = w0111.indexOf('`');
			w0111 = w0111.substring(0,index);
			var w0101 = rowdata.w0101;
			var vo = new HashMap();
			vo.put("w0101",w0101);
			vo.put("a0100",a0100);
			vo.put("nbase",nbase);
			vo.put("quality","h");
			vo.put("flag","1");
			Rpc({functionId:'ZC00002003',scope:this,success:function(res){
					var resultObj = Ext.decode(res.responseText);
					var filePath = resultObj.filePath;
				    var pubRealPath = resultObj.pubRealPath;
					var a0100 = resultObj.a0100;//内部专家编号
					var nbase = resultObj.nbase;//专家库前缀
					var quality = resultObj.quality;//
					if(w0111==2){//内部
						var vo = new HashMap();
						vo.put("a0100",a0100);
						vo.put("nbase",nbase);
						vo.put("quality","h");
						vo.put("isupload","2");
						experts_me.imagewin.changeImage('2',vo);
					}
					else if(w0111==1){//外部
						var vo = new HashMap();
						vo.put("filePath",filePath);
						vo.put("imagename",w0101);
						vo.put("mobile","");
						vo.put("filename","");
						vo.put("perguid","");
						vo.put("pubRealPath",pubRealPath);
						vo.put("bencrypt",'true');
						vo.put("isupload","1");
						experts_me.imagewin.changeImage('1',vo);
					}
			}},vo);
			return false;//看照片时，不可编辑 chent
		}
	}
});