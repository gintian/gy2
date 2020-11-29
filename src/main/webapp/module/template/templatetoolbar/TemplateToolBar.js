/**
 * 声明本界面需要调用的类
 * */
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'TemplateBatchUL': rootPath+'/module/template/templatetoolbar/batch',
		'TemplateImportmenUL': rootPath+'/module/template/templatetoolbar/importmen',
		'TemplateSetupUL': rootPath+'/module/template/templatetoolbar/setup',
		'TemplateApplyUL': rootPath+'/module/template/templatetoolbar/apply',
		'TemplateUpDownUL':rootPath+'/module/template/templatetoolbar/updown',
		'TemplateViewProcessUL':rootPath+'/module/template/templatetoolbar/viewprocess',
		'EHR.defineformula':rootPath+'/components/defineformula',
		'JobtitleUL': rootPath+'/module/template/templatetoolbar/jobtitle',
		'EHR.orgTreePicker':rootPath+'/components/orgTreePicker',
		'ExpressOpinionsUL': rootPath+'/module/template/templatetoolbar/expressopinion',
		'SelfDefineFlowUL': rootPath+'/module/template/templatetoolbar/selfdefineflow',
		'EHR.extWidget.proxy': rootPath+'/components/extWidget/proxy/TransactionProxy',
		'FingerprintInformInputUL': rootPath + '/module/template/templatenavigation/personInfoInput'
	}
});
/**
 * 模板工具栏按钮类 TemplateToolBar.js 定义按钮调用的方法
 * */
Ext.define('TemplateToolBarUL.TemplateToolBar',{
	menuPanel:'',
	prefix:"template",
	templPropety:'',//模板的所有属性 模板号，实例号等 参考html中的templateGlobalBean
	bodyPanel:'',
	tm_prefix:'', //TemplateMain.js中定义的前缀
	batchtempl:'',//备份templPropety
	tab_id:'',
	constructor:function(config) {
		templateTool_me = this;
		this.templPropety=config.templPropety;
		this.bodyPanel=config.bodyPanel;
		this.tm_prefix=config.tm_prefix;
		this.tab_id=config.templPropety.tab_id;
		//this.init(config.config1);
	},
	init:function(config) {
		//生成功能按钮

	},

	/**
	 * 当前类主面板，供其他类调用 暂未用
	 * */
	getMainPanel:function(){
		return this.menuPanel;
	},
	/**
	 * 列表、卡片切换
	 * @return Panel
	 */
	changeView:function(){
		var cardButton=Ext.getDom('cardButton-btnInnerEl');
		var line = templateMain_me.mainPanel.queryById('lineId');//工具栏和模板之间的分割线  lis 20160407
		if (this.templPropety.view_type=="card"){
			this.templPropety.view_type="list";
			Ext.require('TemplateListUL.TemplateList', function(){
				TemplateList = Ext.create("TemplateListUL.TemplateList", {templPropety:this.templPropety});
				var listPanel=TemplateList.getMainPanel();
				this.bodyPanel.removeAll();
				this.bodyPanel.add(listPanel);
				//linbz 26870 打印需要的div
				this.bodyPanel.add({id:'printPreviewdiv',border:0});

				/**lis 20160407 start*/
				line.hide();
				templateMain_me.bodyContainer.setStyle({padding:'0px'});
				/**lis 20160407 end*/

				templateTool_me.updateViewState("list");


			},this);
			templateMain_me.comSet.show();
			cardButton.innerHTML=MB.RESOURCE.BUTTON_CARD;	//按钮显示卡片
		}
		else {
			this.templPropety.view_type="card";
			Ext.require('TemplateCardUL.TemplateCard', function(){
				Ext.create("TemplateCardUL.TemplateCard", {templPropety:this.templPropety});
				var cardPanel=templateCard_me.getMainPanel();
				this.bodyPanel.removeAll();
				this.bodyPanel.add(cardPanel);
				this.bodyPanel.add({id:'printPreviewdiv',border:0});

				/**lis 20160407 start*/
				line.show();
				templateMain_me.bodyContainer.setStyle({padding:'5px 0px 0px 0px'});
				/**lis 20160407 end*/
				templateTool_me.updateViewState("card");
			},this);


			//需重新生成快速选人框 解决位置不对问题
			/*
           templateMain_me.removeInsertPanel();
           templateMain_me.createInsertPanel();
           */

			templateMain_me.comSet.hide();
			cardButton.innerHTML=MB.RESOURCE.BUTTON_LIST;//按钮显示列表
		}
	},
	/**
	 * 卡片、列表 互相切换后更改界面控件显示及状态等
	 * view_type :切换后的状态
	 */
	updateViewState:function(view_type){
		var midText = "人员";
		if(this.templPropety.infor_type=='2')
			midText = "机构";
		if(this.templPropety.infor_type=='3')
			midText = "岗位";
		if (view_type=="list"){
			//生成查询框
			templateMain_me.createSearchBox();
			if (Ext.getCmp('curOutPdf'))
				Ext.getCmp('curOutPdf').hide();
			if (Ext.getCmp('curOutword'))
				Ext.getCmp('curOutword').hide();
			Ext.each(Ext.ComponentQuery.query("*[text^='当前"+midText+"生成PDF']"),function(child){
				child.hide();
			});
			if (templateMain_me.templPropety.task_id.indexOf(",")>0){//批量审批隐藏审批过程
				var viewProcessButton=Ext.getCmp('viewProcessButton');
				if (viewProcessButton)
					viewProcessButton.hide();
			}
			//是否隐藏导航按钮
			templateMain_me.showOrHideNavigation(Ext.getCmp('navigationId'));
		}
		else {
			// linbz  卡片增加查询框
			templateMain_me.removeSearchBox();
			if(templateCard_me==null||!(templateCard_me!=null&&templateCard_me.hidePersonGrid&&templateCard_me.templPropety.task_id!="0")){
				templateMain_me.createSearchBox();
			}
			//liuyz 2016-12-28 显示打印当前页pdf菜单
			if(Ext.get("outPdfMenu"))
				Ext.get("outPdfMenu").hide();
			if(Ext.getCmp('singleListMenu'))
				Ext.getCmp('singleListMenu').show();
			if (Ext.getCmp('curOutPdf'))
				Ext.getCmp('curOutPdf').show();
			if (Ext.getCmp('curOutword'))
				Ext.getCmp('curOutword').show();
			Ext.each(Ext.ComponentQuery.query("*[text^='当前"+midText+"生成PDF']"),function(child){
				child.show();
			});
			//删除查询框
//                templateMain_me.removeSearchBox();
			if (templateMain_me.templPropety.task_id.indexOf(",")>0){//批量审批
				var viewProcessButton=Ext.getCmp('viewProcessButton');
				if (viewProcessButton)
					viewProcessButton.show();
			}
			//是否隐藏导航按钮
			templateMain_me.showOrHideNavigation(Ext.getCmp('navigationId'));
		}

	},

	/**
	 * 新增
	 */
	add_newobj:function(){
		//linbz 27820 新增之前先行保存
		templateTool_me.save('true','true');
		var me = this;
		var map = new HashMap();
		map.put('tabid',this.tab_id);
		map.put('view_type',this.templPropety.view_type);
		Rpc({functionId:'MB00003002',async:false,success:function(form,action){
				if(this.templPropety.view_type=="list"){
					var result = Ext.decode(form.responseText);
					if (result.record!=null){
						var strRecord = getDecodeStr(result.record);
						var record= Ext.decode(strRecord);
						var grid = templateList_me.templateListGrid.tablePanel;
						var store = grid.getStore();
						var index = store.getCount();
						store.insert(index,record);
						templateMain_me.selecRowChangeCss(grid,index);
						templateTool_me.save('true','true');//列表新增后自动保存刷新出默认值
					}
				}else{
					var result = Ext.decode(form.responseText);
					if (result.record!=null){
						var strRecord = getDecodeStr(result.record);
						var record= Ext.decode(strRecord);
						var grid = templateCard_me.personListGrid.tablePanel;
						var store = grid.getStore();
						var index = store.getCount();
						store.insert(index,record);
						templateCard_me.personListCurRecord = store.getAt(index);
						templateMain_me.selecRowChangeCss(grid,index);
						templateCard_me.refreshCurrPerson();//刷新当前人员
						if(record.submitflag2 == "1")//是调入模板时是选中 lis 20160809
							grid.getSelectionModel().select(index,true,true);
						//暂时解决垂直滚动条问题（复选框无法选择）
						//57981 V77包：人事异动：业务处理，已经在上方显示总条数，应下方去掉“共”字，见附件。
			            var height = Ext.getCmp("templmain_body_panel").getHeight();
						var count = templateCard_me.personListGrid.tablePanel.getStore().getCount();
						if((count+2)*30>height){
							templateCard_me.isChangeWidth=true;
							Ext.getCmp("templcard_pnWest").setWidth(203);
						}
						//获取当前名字 syl 卡片模式左侧人员列表 显示 共几条 实时刷新
						var columns = templateCard_me.personListGrid.tablePanel.getColumnManager().getColumns();
						for(var i=0;i<columns.length;i++){
							var col = columns[i];
							var desc=col.text;
							if(desc.indexOf("&nbsp;共")!=-1){
								var index=desc.indexOf("&nbsp;共");
								var num=desc.substr(index+7,desc.length-15-index);
								col.setText(desc.substr(0,index)+"&nbsp;共"+(parseInt(num)+1)+"条</font>");
							}
						}
					}
				}
			},scope:this},map);
	},
	/**
	 * 撤销
	 */
	delete_obj:function(){
		var me = this;
		var tablePanel=null;
		if(this.templPropety.view_type=="list"){
			//获取选中行数据
			tablePanel=templateList_me.templateListGrid.tablePanel;
		}else{
			tablePanel=templateCard_me.personListGrid.tablePanel;
		}
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.showAlert("请选择要撤销的数据！");
			return;
		}
		var map = new HashMap();
		var dataStr = '';
		var containMsg='0';  //是否包含来自通知单的记录

		if(this.templPropety.view_type=="list"){
			for(var i=0;i<selectRecord.length;i++){
				//var basepre = selectRecord[i].data.basepre;
				//var a0100=selectRecord[i].data.a0100;
				var objectid = selectRecord[i].data.objectid_e;
				var state=selectRecord[i].data.state;
				if(state=='1')
					containMsg='1';
				var realtask_id=selectRecord[i].data.realtask_id_e;
				if(realtask_id == null || realtask_id == "")
					realtask_id = "0";
				//dataStr+=basepre+'`'+a0100+"`"+realtask_id+',';
				//dataStr+=objectid+"`"+realtask_id+',';
				dataStr+=realtask_id+',';
			}
		}
		else {
			for(var i=0;i<selectRecord.length;i++){
				var objectid = selectRecord[i].data.objectid_e;
				var realtask_id=selectRecord[i].data.realtask_id_e;
				if(!!!realtask_id)
					realtask_id = "0";
				var state=selectRecord[i].data.state;
				if(state=='1')
					containMsg='1';
				//dataStr+=objectid+"`"+realtask_id+',';
				dataStr+=realtask_id+',';
			}
		}
		dataStr=dataStr.substring(0,dataStr.length-1);

		map.put('dataStr',dataStr);
		map.put('tabid',this.tab_id);
		//	map.put('ins_id',this.templPropety.ins_id);
		map.put('task_id',this.templPropety.task_id);
		//	map.put('sp_batch',this.templPropety.sp_batch);
		map.put('infor_type',this.templPropety.infor_type);
//  	map.put('selected','1');
		map.put('allNum',tablePanel.store.totalCount+"");
		map.put("isDelMsg",'0');
		var task_id=this.templPropety.task_id;
		Ext.showConfirm("您确认要撤销选中的记录吗？",function(value){
				if(value=="yes"){

					if(containMsg=='1')
					{
						Ext.showConfirm("通知单中的记录也一并被撤销吗？",function(value){
								if(value=="yes"){
									map.put("isDelMsg",'1');
								}
								else
									map.put("isDelMsg",'0');

								Rpc({functionId:'MB00003004',async:false,success:function(form,action){
										var result = Ext.decode(form.responseText);
										if(!result.succeed){
											var message = result.message;
											if(message&&message.indexOf("拆分审批")!=-1){
												templateTool_me.checkSpllit(message);
											}
										}else{
											if(templateTool_me.templPropety.view_type=="list"){
												templateTool_me.listRemove(tablePanel,task_id);
											}else {
												templateTool_me.cardRefresh(tablePanel,"true",task_id);
											}
										}
									},scope:this},map);
							}
						);

					}
					else
					{
						Rpc({functionId:'MB00003004',async:false,success:function(form,action){
								var result = Ext.decode(form.responseText);
								var checkSpllitInfo = result.checkSpllitInfo;
								if(!result.succeed){
									var message = result.message;
									if(message&&message.indexOf("拆分审批")!=-1){
										templateTool_me.checkSpllit(message);
									}
								}else{
									if(templateTool_me.templPropety.view_type=="list"){
										templateTool_me.listRemove(tablePanel,task_id);
									}else {
										templateTool_me.cardRefresh(tablePanel,"true",task_id);
									}
								}
							},scope:this},map);
					}
				}else{
					return;
				}

			}
		);

	},
	checkSpllit:function(checkSpllitInfo){
		Ext.showAlert(checkSpllitInfo, function() {
			if(templateTool_me.templPropety.return_flag=='13'&&templateTool_me.templPropety.task_id!='0'){
				window.parent.close();
			}else{
				if (templateTool_me.templPropety.return_flag=="11"){//主页待办
					if (templateTool_me.templPropety.bos_flag=="hcm"){
						location.href=rootPath+"/templates/index/hcm_portal.do?b_query=link";
					}
					else {
						location.href=rootPath+"/templates/index/portal.do?b_query=link";
					}
				}else if (templateTool_me.templPropety.return_flag=="12"){//主页待办更多列表
					location.href=rootPath+"/general/template/matterList.do?b_query=link";
				}else if(templateTool_me.templPropety.return_flag=="1"||templateTool_me.templPropety.return_flag=="2"
					||templateTool_me.templPropety.return_flag=="3"||templateTool_me.templPropety.return_flag=="4"){
					location.href=rootPath+"/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id="+templateTool_me.templPropety.module_id;
				}
			}
			return;
		});
	},
	/**
	 * 卡片删除人 lis
	 * @param {} tablePanel 表格
	 * @param {} isShowFirst 是否显示第一行数据
	 */
	cardRefresh:function(tablePanel,showFirst,task_id){
		var store = Ext.data.StoreManager.lookup('templatecard_dataStore');
		//store.remove(selectRecord);
		store.load({//lis edit 20160517
			callback: function(records, operation, success) {
				var rowIndex = 0;
				//获取当前名字 syl 卡片模式左侧人员列表 显示 共几条 实时刷新
				var columns = templateCard_me.personListGrid.tablePanel.getColumnManager().getColumns();
				for(var i=0;i<columns.length;i++){
					var col = columns[i];
					var desc=col.text;
					if(desc.indexOf("&nbsp;共")!=-1){
						var index=desc.indexOf("&nbsp;共");
						col.setText(desc.substr(0,index)+"&nbsp;共"+store.totalCount+"条</font>");
					}
				}
				if(store.getCount() > 0){
					if(showFirst == "true"){
						templateCard_me.personListCurRecord = store.getAt(rowIndex);
					}else{
						store.each(function(Record,index){
							if(Record.get('objectid_e') == templateCard_me.personListCurRecord.get('objectid_e')){
								rowIndex = index;
								return;
							}
						});
					}
					templateMain_me.selecRowChangeCss(tablePanel,rowIndex);//第一行颜色为选中
					templateCard_me.refreshCurrPerson();//刷新右边模板
					if(templateCard_me!=null&&store.getCount()==1&&templateCard_me.templPropety.task_id!="0"){
						templateMain_me.removeSearchBox();
						Ext.getCmp("cardButton").hide();
					}
				}
				else {
					templateCard_me.personListCurRecord = null;
					templateCard_me.refreshPerson(null);
					if(task_id!=null&&task_id!=0)
					{
						templateTool_me.returnBack(true);//如果撤销后没有人员了，返回之前页面
					}
				}
			}
		});
	},

	//列表删除人,lis
	listRemove:function(tablePanel,task_id){
		var store = Ext.data.StoreManager.lookup('templatelist_dataStore');
		//store.remove(selectRecord);
		store.load({//lis edit 20160517
			callback: function(records, operation, success) {
				if(store.getCount() > 0){
					templateMain_me.selecRowChangeCss(tablePanel,0);//第一行颜色为选中
				}else{
					if(task_id!=null&&task_id!=0)
					{
						templateTool_me.returnBack(true);//如果撤销后没有人员了，返回之前页面
					}
				}
			}
		});
		//tablePanel.getSelectionModel().clearSelections();
	},

	/**
	 * 保存
	 * noHint:true 不提示信息，false提示信息，unKnow 根据数据是否修改决定是否提示信息
	 */
	save:function(noHint,isCompute,isSaveButton){
		setTimeout(function(){ },300);//增加延时解决部分时候指标修改了但是保存不上。
		var bReturn=false;//返回值
		var store=null;
		var records = [];
		var pageid="";
		if(getBrowseVersion() && !isCompatibleIE()&&!templateMain_me.canToSave)
			return;
		if(this.templPropety.view_type=="list"){
			store = Ext.data.StoreManager.lookup('templatelist_dataStore');
			var addOrUpdateList = store.getModifiedRecords(); //获取修改的数据。注意：新增的数据也在这里面
			if(addOrUpdateList.length<1){
				/*if(isSaveButton=='0'){
					Ext.showAlert("保存成功！");
					return true;
				}else
					return true;*/
			}

			for(var i=0;i<addOrUpdateList.length;i++){
				var record = addOrUpdateList[i].data;
				records.push(record);
			}
		}
		else {
			records= templateCard_me.getSaveRecord();
			//更新卡片左侧列表的姓名
			templateCard_me.changeA0101();
			if(records.length<1){
				/*if(isSaveButton=='0'){
                    Ext.showAlert("保存成功！");
                    return true;
                }else
                    return true;*/
			}
			pageid=templateCard_me.getCurrPageId();
		}
		var hashvo = new HashMap();
		hashvo.put("savedata",records);
		hashvo.put("noHint",noHint);
		hashvo.put("isCompute",isCompute);
		initPublicParam(hashvo,this.templPropety);
		//将数据传入后台，key为‘savedata’
		if(noHint=='false'){
			hashvo.put("allNum",templateTool_me.getTotalCount());
		}
		var async = false;
		if(this.templPropety.autoCompute&&noHint=='false'){//目前只在点击保存按钮时提示
			Ext.MessageBox.wait("正在执行自动计算操作，请稍候...", "等待");
			async = true;
		}
		Rpc({functionId:'MB00003006',async:async,success:function(res){
				if(this.templPropety.autoCompute&&noHint=='false')
					Ext.MessageBox.close();
				var result = Ext.decode(res.responseText);
				if (result.succeed) {
					var updateSize=result.updateSize;
					if(noHint=="unKnow")
					{
						//liuyz 修正保存，点击保存后，切页不再提示保存成功
						if(templateTool_me.templPropety.view_type=='card'){
							if(templateCard_me.isHaveChange)
								noHint="false";
							else
								noHint="true";
						}
					}
					if(this.templPropety.view_type=="list"){
						var store = Ext.data.StoreManager.lookup('templatelist_dataStore');
						store.reload();
					} else {
						//保存子集后返回子集信息
						var subDataList = result.subDataList;
						//重新给子集赋值
						Ext.each(subDataList,function(bean,index){
							var uniqueId = bean.uniqueId;
							var recordSet=templateCard_me.getCurRecordSet();
							var fieldSet=recordSet.fieldSet;
							for (var i=0;i<fieldSet.getFieldCount();i++){
								var field=fieldSet.fields[i];
								if(field.uniqueId==uniqueId)
								{
									var disValue = bean.disValue;
									templateCard_me.recordSet.getField(uniqueId).keyValue=bean.keyValue;
									templateCard_me.recordSet.getField(uniqueId).disValue=disValue;
									var element =Ext.getDom(uniqueId);//根据唯一值取得页面对应元素
									if (element !=null){
										var valueItem =recordSet.getField(uniqueId);
										var disValueDec=getDecodeStr(disValue);
										var recordRoot=Ext.decode(disValueDec);
										var tabid = templateMain_me.templPropety.tab_id;
										showSubView(uniqueId,valueItem.fldName,tabid,element,recordRoot);
									}
									break;
								}
							}
						});
						//liuyz 修正保存，点击保存后，切页不再提示保存成功
						if(templateTool_me.templPropety.view_type=='card'){
							templateCard_me.isHaveChange=false;
						}
					}
					if(noHint!="true"){
						Ext.showAlert("保存成功！");
						if (result.autoCompute=="true"||templateMain_me.templPropety.isAutoLog){//自动计算后或者设置了记录变动日志的，需刷新页面
							templateTool_me.refreshCurrent();//刷新当前，不考虑左侧人员列表，姓名参与计算无意义
						}
					}
					bReturn=true;
				} else {
					if(noHint=='false'){
						var message = result.message;
						if(message&&message.indexOf("拆分审批")!=-1){
							templateTool_me.checkSpllit(message);
						}else
							Ext.showAlert(result.message);
					}else
						Ext.showAlert(result.message);
				}
			},scope:this},hashvo);
		//liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
		templateTool_me.enabledButton("template_rejectButton");
		templateTool_me.enabledButton("template_applyButton");
		templateTool_me.enabledButton("template_submitButton");
		return bReturn;
	},
	/**
	 * 刷新  此方法废弃
	 * needLocate 卡片下，定位当前人员 默认定位 传入"false"不定位
	 */
	/*
   refresh:function(needLocate,showFirst){
       var tablePanel=null;
       if (this.templPropety.view_type=="list"){
           Ext.require('TemplateListUL.TemplateList', function(){
               TemplateList = Ext.create("TemplateListUL.TemplateList", {templPropety:this.templPropety});
               var listPanel=TemplateList.getMainPanel();
               this.bodyPanel.removeAll();
               this.bodyPanel.add(listPanel);

           },this);
       }
       else {
           if (needLocate!=null && needLocate=="false"){//整个页面刷新后定位
               tablePanel=templateCard_me.personListGrid.tablePanel;
               templateTool_me.cardRefresh(tablePanel,showFirst);
           }
           else {
               templateCard_me.refreshCurrPerson();//不刷新页面，只是定位
           }
       }
   },
   */
	/**
	 * 刷新当前人：用于计算、保存、批量修改单个多个指标后 ，只刷新右侧卡片界面。
	 */
	refreshCurrent:function(){
		var tablePanel=null;
		if (this.templPropety.view_type=="list"){
			templateTool_me.reLoadForm();
		}
		else {
			templateCard_me.refreshCurrPerson();
		}
	},

	/**
	 * 刷新
	 * 左侧人员列表刷新，刷新后定位到当前人员，
	 * 如果当前人员已删除，定位到第一个(如部分报批)。
	 * showFirst：true 刷新后定位到第一个人
	 *
	 */
	refreshAll:function(showFirst){
		var tablePanel=null;
		if (this.templPropety.view_type=="list"){
			templateTool_me.reLoadForm();
		}
		else {
			tablePanel=templateCard_me.personListGrid.tablePanel;
			if (showFirst!="true"){
				showFirst="false";
			}
			templateTool_me.cardRefresh(tablePanel,showFirst,null);
		}
	},

	/**
	 * 重新加载当前界面
	 */
	reLoadForm:function(){
		if (this.templPropety.view_type=="list"){
			Ext.require('TemplateListUL.TemplateList', function(){
				TemplateList = Ext.create("TemplateListUL.TemplateList", {templPropety:this.templPropety});
				var listPanel=TemplateList.getMainPanel();
				this.bodyPanel.removeAll();
				this.bodyPanel.add(listPanel);

			},this);
		}
		else {
			Ext.require('TemplateCardUL.TemplateCard', function(){
				Ext.create("TemplateCardUL.TemplateCard", {templPropety:templateTool_me.templPropety,cur_object_id:''});
				var cardPanel=templateCard_me.getMainPanel();
				if(templateCard_me!=null&&templateCard_me.hidePersonGrid&&templateCard_me.templPropety.task_id!="0"){
					templateMain_me.removeSearchBox();
					Ext.getCmp("cardButton").hide();
				}
				this.bodyPanel.removeAll();
				this.bodyPanel.add(cardPanel);
			},this);
		}
	},
	/**
	 * 批量计算
	 */
	batchCalc:function(){
		setTimeout(
			function(){
				if (templateTool_me.save('true','false')) {
					var obj = new Object();
					obj.templPropety=templateTool_me.templPropety;
					obj.allNum = templateTool_me.getTotalCount();
					Ext.require('TemplateBatchUL.TemplateBatchCalc',function(){
						Ext.create("TemplateBatchUL.TemplateBatchCalc",obj);
					},this);
				}
			},300);

	},

	/*
     * 上会 职称评审会议
     * */
	subMeeting:function(){
		if (templateTool_me.save('true',"true")) {
			var map = new HashMap();
			initPublicParam(map,this.templPropety);
			//校验是否选中记录
			Rpc({functionId:'MB00002017',async:false,success:this.subMeetingOK},map);
		}
	},

	/**
	 * 上会 职称评审会议
	 */
	subMeetingOK:function(form,action){
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
			Ext.showAlert(result.message);
			return;
		}
		var info=result.info;
		if (info!=""){//有校验提示信息
			Ext.showAlert(info);
			return;
		}
		else {
			Ext.require('JobtitleUL.SubMeeting',function(){
				Ext.create("JobtitleUL.SubMeeting",{templPropety:templateTool_me.templPropety}
				);
			});
		}
	},

	/**
	 * 返回
	 *bAssign :是否是审批后自动返回  true：审批后自动返回或关闭， false:手动点击返回或关闭按钮
	 * @return Panel
	 */
	returnBack:function(bAssign){
		if (bAssign==undefined){//默认不传则为true。
			bAssign=true;
		}
		if(!bAssign)//bug32590 liuyz  用户手工点击返回或关闭自动保存数据
			templateTool_me.save('true','false');
		if(templateMain_me.callBack_close){
			clearInterval(window.lockedtimer);
			Ext.callback(eval(templateMain_me.callBack_close),null,[]);
		}
		else {
			if (templateTool_me.templPropety.return_flag=="11"){//主页待办
				if (templateTool_me.templPropety.bos_flag=="hcm"){
					if(parent.Ext&&parent.Ext.getCmp('serviceHallWin')){
						Ext.destroy(parent.Ext.getCmp('serviceHallWin'));
					}else{
						location.href=rootPath+"/templates/index/hcm_portal.do?b_query=link";
					}
				}
				else {
					location.href=rootPath+"/templates/index/portal.do?b_query=link";
				}
			}else if (templateTool_me.templPropety.return_flag=="12"){//主页待办更多列表
				location.href=rootPath+"/general/template/matterList.do?b_query=link";
			}else if (templateTool_me.templPropety.return_flag=="13"){//关闭
				if (bAssign){
					Ext.showAlert(MB.MSG.assign_sucess);
				}
				if(Ext.isIE){
					window.opener = null;
					window.open('', '_self', '');
					window.close();
				}else{
					//非ie浏览器无法应用关闭浏览器方法。暂时跳转空白页面。
					window.location.href = 'about:blank';
				}
			}else if (templateTool_me.templPropety.return_flag.indexOf("7-")==0){ //预警列表 xx:预警id
				location.href=rootPath+"/system/warn/result_manager.do?b_query=link&warn_wid="+templateTool_me.templPropety.return_flag.substring(2);
			}else if(templateTool_me.templPropety.return_flag.indexOf("8-")==0){
				location.href=rootPath+"/dtgh/party/person/searchbusinesslist.do?b_search=link&tabIndex=0&param="+templateTool_me.templPropety.return_flag.substring(2);
			}else if(templateTool_me.templPropety.return_flag.indexOf("-r")!=0){//证明是撤回的单据的返回
				location.href=rootPath+"/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id="+templateTool_me.templPropety.module_id;
			}
		}
	},
	/**
	 * 批量修改多个指标
	 */
	batchUpdateFields:function(){
		var obj = new Object();
		obj.batchtempl=this.templPropety;
		obj.allNum=templateTool_me.getTotalCount();
		Ext.require('TemplateBatchUL.UpdateMultiFieldItem',function(){
			Ext.create("TemplateBatchUL.UpdateMultiFieldItem",obj);
		});
	},
	/**
	 * 批量修改单个指标
	 */
	singleUpdateFields:function(){
		var obj = new Object();
		obj.batchtempl=this.templPropety;
		obj.allNum=templateTool_me.getTotalCount();
		Ext.require('TemplateBatchUL.UpdateSingleFieldItem',function(){
			Ext.create("TemplateBatchUL.UpdateSingleFieldItem",obj);
		});
	},
	/**
	 * 生成序号
	 */
	filloutSequence:function(){
		var tablePanel=null;
		if(this.templPropety.view_type=="list"){
			//获取选中行数据
			tablePanel=templateList_me.templateListGrid.tablePanel;
		}else{
			tablePanel=templateCard_me.personListGrid.tablePanel;
		}
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.showAlert("请选择生成序号的数据！");
			return;
		}


		var map = new HashMap();
		map.put("tab_id",this.tab_id);
		map.put("task_id",this.templPropety.task_id);
		map.put("infor_type",this.templPropety.infor_type);
		map.put("module_id",this.templPropety.module_id);
		map.put("allNum",templateTool_me.getTotalCount());
		Rpc({functionId:'MB00002027',async:false,success:function(form,action){
				var result = Ext.decode(form.responseText);
				if(!result.succeed){
					var message = result.message;
					if(message&&message.indexOf("拆分审批")!=-1){
						templateTool_me.checkSpllit(message);
					}
				}else
					templateTool_me.refreshCurrent();//刷新
			}},map) ;
	},


	/**
	 * 手工选择
	 */
	getHandQuery:function(){
		templateTool_me.save('true','true');
		var obj=new Object();
		obj.tabid=this.tab_id;
		obj.view_type=this.templPropety.view_type;
		obj.infor_type=this.templPropety.infor_type;
		obj.nbases=this.templPropety.nbases;//进入时人员库设置
		if(this.templPropety.orgId)
			obj.orgId=this.templPropety.orgId;
		else
			obj.orgId="";

		//29235 linbz 增加选人控件不显示的人员参数
		obj.deprecate = templateMain_me.deprecate;
		// 按检索条件和人员范围 begin
		if(this.templPropety.filter_by_factor==1)
		{
			obj.isPrivExpression=this.templPropety.isPrivExpression;
			obj.filter_factor=this.templPropety.filter_factor;
			if(this.templPropety.infor_type!=1){
				obj.sqlwhere_factor=this.templPropety.sqlwhere_factor;
			}
		}
		if(this.templPropety.nbases==-1){//bug 43517 提示没有权限不显示选人控件。
			Ext.showAlert("您没有模板设置的进入时人员库权限。");
		}else{
			Ext.require('TemplateImportmenUL.HandImportMen',function(){
				Ext.create("TemplateImportmenUL.HandImportMen",obj);
			});
		}
	},
	/**
	 * 简单查询
	 */
	simpleQuery:function(){
		var map2 = new HashMap();
		map2.put(1, "a,b,k");//要显示的子集
		var map = new HashMap();
		map.put('salaryid', '');
		map.put('condStr', '');//复杂条件，简单条件表达式
		map.put('cexpr', '');//简单公式时：1*2
		map.put('path', "2306514");
		map.put('priv', "1");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
		Ext.require('EHR.selectfield.SelectField',function(){
			Ext.create("EHR.selectfield.SelectField",{imodule:"1",type:"1",queryType:"2",dataMap:map,comBoxDataInfoMap:map2,rightDataList:'',title:"选择指标",queryCallbackfunc:"templateTool_me.callBack"});
		});
	},
	/**
	 * 通用查询
	 */
	generalQuery:function(){
		templateTool_me.save('true','true');
		var map2 = new HashMap();
		map2.put(1, "a,b,k");//要显示的子集
		var map = new HashMap();
		map.put('salaryid', '');
		map.put('condStr', '');//复杂条件，简单条件表达式
		map.put('cexpr', '');//简单公式时：1*2
		map.put('path', "2306514");
		map.put('nbases',this.templPropety.nbases); //进入时人员库设置
		// 按检索条件和人员范围
		if(this.templPropety.filter_by_factor==1)
		{
			map.put('priv', this.templPropety.isPrivExpression?"1":"0");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
			map.put('filter_factor', this.templPropety.filter_factor);
		}
		map.put('info_type', this.templPropety.infor_type);
		if(this.templPropety.nbases==-1){
			Ext.showAlert("您没有模板设置的进入时人员库权限。");
		}else{
			Ext.require('EHR.selectfield.SelectField',function(){
				Ext.create("EHR.selectfield.SelectField",{imodule:"1",type:"1",queryType:"1",flag:'1',dataMap:map,comBoxDataInfoMap:map2,rightDataList:'',title:"选择指标",queryCallbackfunc:"templateTool_me.callBack"});
			});
		}
	},
	// 按检索条件和人员范围
	callBack:function(expr,checkValues,dataList,query_type,priv,filter_factor){
		var map = new HashMap();
		map.put('expr',expr);
		map.put('checkValues',checkValues);
		map.put('type',templateTool_me.templPropety.infor_type);//=1人员,=2单位,=3职位
		map.put('dataList',dataList);
		map.put('tabid',templateTool_me.tab_id);
		map.put('query_type',query_type);
		map.put('filter_factor',filter_factor);
		map.put('chpriv',priv);
		//syl通用查询 用来区分是人事异动表单 的，当且仅当该字段为1 时才走人事异动权限： 操作范围-》人员范围（因业务范围涉及模块，选人控件暂不支持业务范围控制）
    	//其他默认  57977 V77包：薪资变动模板，手工选人按照操作单位->管理范围控制权限，但是通用查询却按照管理范围控制了，此处需要统一
		map.put('moudle_id','1');
		Ext.MessageBox.wait("正在执行引入操作，请稍候...", "等待");
		Rpc({functionId:'MB000020013',async:true,success:function(form,action){
				Ext.MessageBox.close();
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					var flag = result.flag;
					if(flag){
						templateTool_me.refreshAll("false");
					}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
						Ext.showAlert("无此人或者没有权限！");
					}
				}else{
					Ext.showAlert(result.message);
				}
			}},map);
	},
	/**
	 * 设置业务日期
	 */
	setAppDate:function(picker,date){
		var fm = Ext.Date.format(date, "Y.m.d"); //对时间格式化
		Ext.showConfirm("您确定要设置业务日期为："+fm+"\?", function(optional){
			if(optional=='yes'){
				var map = new HashMap();
				map.put("appdate",fm);
				Rpc({functionId:'MB00002006',async:false,success:function(form,action){
						var result = Ext.decode(form.responseText);
						if(!result.succeed){
							Ext.showAlert("设置失败！");
						}
					}},map);
			}
		});
	},
	/**
	 * 定义计算公式
	 */
	setFormula:function(){
		var obj = new Object();
		obj.module="3";
		obj.tableid=templateTool_me.tab_id;
		obj.infor_type = this.templPropety.infor_type;
		Ext.require('TemplateSetupUL.TemplateSetupFormula',function(){
			Ext.create("TemplateSetupUL.TemplateSetupFormula",obj);
		});
	},
	/**
	 * 报批
	 */
	apply:function(){
		var obj = new Object();
		obj.templPropety=this.templPropety;
		obj.type='1';
		Ext.require('TemplateApplyUL.TemplatePrepare',function(){
			Ext.create("TemplateApplyUL.TemplatePrepare",obj);
		});
	},
	/**
	 * 审批，自动按钮标题为报批或提交    手动：审批
	 *flag :1 报批 ; 2 驳回 ;  3 ：批准 ; 4:手动审批(报批、驳回、批准在弹出框中显示，根据点击的按钮更改flag标记)
	 */
	assign:function(flag){
		var obj = new Object();
		obj.templPropety=templateTool_me.templPropety;
		obj.type=flag+"";
		obj.allNum = templateTool_me.getTotalCount();
		Ext.require('TemplateApplyUL.TemplatePrepare',function(){
			Ext.create("TemplateApplyUL.TemplatePrepare",obj);
		});
	},
	/**
	 * 不走审批，直接提交
	 */
	submit:function(){
		var obj = new Object();
		obj.templPropety=templateTool_me.templPropety;
		obj.type="0";
		Ext.require('TemplateApplyUL.TemplatePrepare',function(){
			Ext.create("TemplateApplyUL.TemplatePrepare",obj);
		});
	},
	/**
	 * 指纹、人脸录入
	 */
	fingerprintinfo:function(){
		var me = this;
		var tablePanel=null;
		if(this.templPropety.view_type=="list"){
			//获取选中行数据
			tablePanel=templateList_me.templateListGrid.tablePanel;
		}else{
			tablePanel=templateCard_me.personListGrid.tablePanel;
		}
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.showAlert("请选择要录入人员！");
			return;
		}
		if(selectRecord.length>1){
			Ext.showAlert("只能勾选一个人进行指纹、人脸录入操作！");
			return;
		}
		templateTool_me.save('true','true');
		Ext.create("FingerprintInformInputUL.FingerPrintInformInput",{
			tab_id:templateTool_me.templPropety.tab_id
		});

	},
	/**
	 *审批过程
	 */
	openShowyj:function(){
		var obj = new Object();
		obj.tabid=this.tab_id;
		obj.task_id=this.templPropety.task_id;
		if (obj.task_id.indexOf(",")>0){//批量审批
			if(this.templPropety.view_type=="card"){
				obj.task_id=templateCard_me.cur_task_id;
			}
		}
		obj.infor_type=this.templPropety.infor_type;
		obj.return_flag=this.templPropety.return_flag;
		obj.allNum=templateTool_me.getTotalCount();
		Ext.require('TemplateViewProcessUL.TemplateViewProcess',function(){
			Ext.create("TemplateViewProcessUL.TemplateViewProcess",obj);
		});

	},
	/**
	 * 审核公式
	 */
	checkFormula:function(){
		var obj = new Object();
		obj.module="3";
		obj.id=this.tab_id;
		obj.formulaType='2';//1是计算公式， 2是校验公式
		obj.infor_type = this.templPropety.infor_type;
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",obj);
		});
	},
	/**
	 * 临时变量
	 */
	setTempVar:function(){
		var obj = new Object();
		obj.module='1';
		obj.id=this.tab_id;
		obj.nflag='0';
		obj.type='3';
		obj.infor_type = this.templPropety.infor_type;
		Ext.require('EHR.defineformula.DefineTempVar',function(){
			Ext.create("EHR.defineformula.DefineTempVar",obj);
		});
	},
	/**
	 * 下载模版
	 */
	downLoadTempData:function(){
		var outName='';//下载文件
		var flag = '0';//下载程序是否执行完成
		var succeed = false;//下载程序是否执行成功
		//zhangh 2019-11-20 在参数中增加office版本
		var officeType='';
		var radio1 =  Ext.getCmp('radio1');
		//zhangh 2019-12-18,当初为了区分office版本增加了单选按钮，但是其他地方也有用到这个方法，兼容处理下
		if(radio1!=undefined){
			if(radio1.getValue()){
				officeType = radio1.inputValue;
			}
			var radio2 =  Ext.getCmp('radio2');
			if(radio2.getValue()){
				officeType = radio2.inputValue;
			}
		}
		//if (this.templPropety.view_type=="list"){
		var map = new HashMap();
		map.put("tabid",this.tab_id);
		map.put("ins_id",this.templPropety.ins_id);
		map.put("task_id",this.templPropety.task_id);
		map.put("infor_type",this.templPropety.infor_type);
		map.put("view_type",this.templPropety.view_type);
		map.put("allNum",templateTool_me.getTotalCount());
		//zhangh 2019-11-20 在参数中增加office版本
		map.put("officeType",officeType);
		Rpc({functionId:'MB00002008',timeout:10000000,async:true,success:function(form,action){
				var result = Ext.decode(form.responseText);
				flag = '1';//1表示下载程序执行完成
				succeed=result.succeed;
				if(succeed){
					outName=result.outName;
				}else{
					var message = result.message;
					if(message&&message.indexOf("拆分审批")!=-1){
						templateTool_me.checkSpllit(message);
					}
				}

			}},map);
		/* var msgBox = Ext.MessageBox.show({
             title:common.button.promptmessage,
             msg:'动态更新进度条和信息文字',
             modal:true,
             width:300,
             progress:true
         })*/
		var msgBox = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
			title: common.button.promptmessage,
			msg:'动态更新进度条和信息文字',
			modal:true,
			width:300,
			progress:true
		});
		var progressText='';//进度条信息
		var task = {
			run:function(){
				//进度条信息
				progressText = '下载中...';
				//更新信息提示对话框
				msgBox.updateProgress('',progressText,'当前时间：'+Ext.util.Format.date(new Date(),'Y-m-d g:i:s A'));
				//下载文件成功，关闭更新信息提示对话框
				if(flag=='1'){
					Ext.TaskManager.stop(task);
					msgBox.hide();
					if(succeed){
						window.location.target="_blank";
						var userAgent = navigator.userAgent;
						window.location.href ="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
					}else{
						Ext.showAlert("导出失败！错误信息："+result.message);
					}
				}
			},
			interval:1000//时间间隔
		};
		Ext.TaskManager.start(task);
		//}
	},
	/**
	 * 上传模版
	 */
	upLoadTempData:function(){
		//if (this.templPropety.view_type=="list"){
		var obj = new Object();
		obj.id=this.tab_id;
		obj.ins_id=this.templPropety.ins_id;
		obj.task_id=this.templPropety.task_id;
		obj.infor_type=this.templPropety.infor_type;
		obj.view_type=this.templPropety.view_type;
		obj.allNum=templateTool_me.getTotalCount();
		Ext.require('TemplateUpDownUL.TemplateUpLoadData',function(){
			Ext.create("TemplateUpDownUL.TemplateUpLoadData",obj);
		});
		//}
	},
	/**
	 * 报备-发表意见
	 */
	pubOpinion:function(){
		var obj = new Object();
		obj.task_id=this.templPropety.task_id;
		obj.tab_id=this.tab_id;
		obj.approve_flag=this.templPropety.approve_flag;
		Ext.require('ExpressOpinionsUL.ExpressOpinions',function(){
			Ext.create("ExpressOpinionsUL.ExpressOpinions",obj);
		});
	},
	/**
	 *导入数据与下载模板合并
	 **/
	downTempData: function () {
		var win = Ext.create('Ext.window.Window', {
			title: '导入数据',
			id: 'importDataId',
			width: 400,
			height: (this.templPropety.downmap.bDownload && this.templPropety.downmap.bUploadload) ? 260 : 150,
			resizable: false,
			modal: true,
			border: false,
			bodyStyle: 'background:#ffffff;',
			layout: {
				type: 'vbox',
				padding: '0 0 0 50'
			},
			items: [
				//zhangh 2019-11-20 增加office版本的选项
				{
					xtype: 'container',
					//fieldLabel :'office版本',
					defaultType: 'radiofield',
					labelAlign:'right',
					labelWidth:60,
					//style:'left:18px'
					margin: '10 0 0 10',
					layout: 'hbox',
					width: 350,
					height: 25,
					items: [
						{
							boxLabel  : 'office2007以上版本',
							checked : true,
							name      : 'a',
							inputValue: '1',
							id        : 'radio1'

						},
						{
							boxLabel  : 'office2007及以下版本',
							name      : 'a',
							inputValue: '2',
							id        : 'radio2',
							margin: '0 0 0 20',
						}
					]
				},
				this.templPropety.downmap.bDownload == true ? {
					xtype: 'container', margin: '25 0 0 12', layout: 'hbox',
					items: [{
						xtype: "label",
						margin: '2 30 0 0',
						width: 120,
						text: (this.templPropety.downmap.bDownload && this.templPropety.downmap.bUploadload) ? "1、下载模板文件" : "下载模板文件"
					},
						{
							xtype: "button", text: "下载",margin: '0 0 0 50', handler: function () {
								templateTool_me.downLoadTempData();
							}
						}]
				} : {html: ''}, this.templPropety.downmap.bUploadload == true ? {
					xtype: 'container', margin: '25 0 0 12', layout: 'hbox',
					items: [{
						xtype: "label",
						width: 120,
						margin: '2 30 0 0',
						html: (this.templPropety.downmap.bDownload && this.templPropety.downmap.bUploadload) ? "2、请选择导入文件" : "请选择导入文件"
					}, {
						xtype: "button", width: 40, height: 22, text: "浏览",margin: '0 0 0 50', listeners: {
							afterrender: function (btn) {
								Ext.widget("fileupload", {
									upLoadType: 3,
									height: 22, width: 40,
									style: 'position:relative;top:-22px',
									buttonText: '',
									fileExt: "*.xls;*.xlsx",//添加对上传文件类型控制
									renderTo: this.id,
									error: function () {
										Ext.showAlert(common.msg.uploadFailed + "！");
									},
									success: function (list) {
										//win.destroy();
										templateTool_me.downSuccess(list);
									},
									callBackScope: '',
									//savePath:''，
									uploadUrl: "/case/",
									isTempFile:true,
									VfsFiletype:VfsFiletypeEnum.doc,
									VfsModules:VfsModulesEnum.RS,
									VfsCategory:VfsCategoryEnum.other,
									CategoryGuidKey:''
								});
							}
						}
					}]
				} : {html: ''}, this.templPropety.infor_type == '1' ? {
					xtype: "label",
					width: 260,
					margin: '20 10 0 12',
					html: '注意：模板含唯一指标才可下载子集数据,代码项超过1000,&ensp;下载模板不显示'
				} : {html: ''}]
		});
		win.show();
	},/**
     *syl 职称评审Excel模板下载与上传
     **/
    downExcelTemp:function(){
    	var map = new HashMap();
		map.put("tabid",this.tab_id);
		map.put("ins_id",this.templPropety.ins_id);
		map.put("task_id",this.templPropety.task_id);
		map.put("infor_type",this.templPropety.infor_type);
		map.put("view_type",this.templPropety.view_type);
		map.put("allNum",templateTool_me.getTotalCount());
		//zhangh 2019-11-20 在参数中增加office版本
		map.put("downtype","2");
		//校验有没有用户名模板
		var userflag=false;
		var succeed='';
		Rpc({functionId:'MB00008010',timeout:10000000,async:true,success:function(form,action){
				var result = Ext.decode(form.responseText);
				succeed=result.succeed;
				if(succeed){
					if(result.errorinfo){
						userflag=true;
					}
				}else{
					userflag=true;
				}
				var win = Ext.create('Ext.window.Window',{
					title:'导入HTML表单',
					id:'importExcelId',
					width:400,
					height:200,
					resizable: false,  
					modal: true,
					border:false,
					bodyStyle: 'background:#ffffff;',
					layout: {
						type: 'vbox',
						padding:'0 0 0 50'
					},
					items:[{xtype:'container',margin: '25 0 0 0',layout:'hbox',
						items:[{xtype:"label",margin: '2 30 0 0',width:120,text:"1、下载HTML表单"},
						       {xtype:"button",text:"下载模板",menu:{items:[
						                                                {text:'标准模板',handler:function(){templateTool_me.downLoadExcelTemp("1");},id:'standExcel'},
						                                                {text:'用户模板',disabled:userflag,handler:function(){templateTool_me.downLoadExcelTemp("2");},id:'userExcel'}]}}
						]},{xtype:'container',margin: '25 0 0 0',layout:'hbox',
							items:[{xtype:"label",width:120,margin: '2 30 0 0',html:"2、请选择导入文件"
							},{
								xtype:"button",height:22,html:"&nbsp;&nbsp;&nbsp;浏览&nbsp;&nbsp;&nbsp;&nbsp;",listeners:{
									afterrender : function(btn){
										Ext.widget("fileupload",{
											upLoadType:3,
											height:22,width:40,
											style:'position:relative;top:-20px',
											buttonText:'',
											fileExt:"*.xls;*.xlsx",//添加对上传文件类型控制
											renderTo:this.id,
											error:function(){
												Ext.showAlert(common.msg.uploadFailed+"！");
											},
											success:function(list){
												templateTool_me.downExcelSuccess(list);
											},
											callBackScope:'',
											uploadUrl:"/case/",
											isTempFile:true,
											VfsFiletype:VfsFiletypeEnum.doc,
											VfsModules:VfsModulesEnum.RS,
											VfsCategory:VfsCategoryEnum.other,
											CategoryGuidKey:''
										});
									}
								}}]},{xtype:"label",width:220,margin: '20 10 0 -10',html:''}]
				});
				win.show();
		}},map);
    },/**
	 * syl下载HTML模版
	 */
	downLoadExcelTemp:function(downtype){
		var fieldid='';//下载文件filedid
		var flag = '0';//下载程序是否执行完成
		var errorinfo='';
		var succeed = false;//下载程序是否执行成功
		var map = new HashMap();
		map.put("tabid",this.tab_id);
		map.put("ins_id",this.templPropety.ins_id);
		map.put("task_id",this.templPropety.task_id);
		map.put("infor_type",this.templPropety.infor_type);
		map.put("view_type",this.templPropety.view_type);
		map.put("allNum",templateTool_me.getTotalCount());
		//zhangh 2019-11-20 在参数中增加office版本
		map.put("downtype",downtype);
		Rpc({functionId:'MB00008010',timeout:10000000,async:true,success:function(form,action){
				var result = Ext.decode(form.responseText);
				flag = '1';//1表示下载程序执行完成
				succeed=result.succeed;
				if(succeed){
					fieldid=result.fieldid;
					errorinfo=result.errorinfo;
				}else{
					var message = result.message;
				}

			}},map);
		var msgBox = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
			title: common.button.promptmessage,
			msg:'动态更新进度条和信息文字',
			modal:true,
			width:300,
			progress:true
		});
		var progressText='';//进度条信息
		var task = {
			run:function(){
				//进度条信息
				progressText = '下载中...';
				//更新信息提示对话框
				msgBox.updateProgress('',progressText,'当前时间：'+Ext.util.Format.date(new Date(),'Y-m-d g:i:s A'));
				//下载文件成功，关闭更新信息提示对话框
				if(flag=='1'){
					Ext.TaskManager.stop(task);
					msgBox.hide();
					if(errorinfo){
						Ext.showAlert(errorinfo);
					}else if(succeed){
						window.location.target="_blank";
						var userAgent = navigator.userAgent;
						if(userAgent.indexOf("Edge")>-1)//Edge浏览器需要走UTF-8
							{
							if(downtype=="2"){
								var win=open("/servlet/vfsservlet?fileid="+fieldid+"&fromjavafolder=true","excel");
							}else{
								var win=open("/servlet/vfsservlet?fileid="+fieldid+"&fromjavafolder=true","excel");
							}
							}
						else{
							if(downtype=="2"){
								var win=open("/servlet/vfsservlet?fileid="+fieldid+"&fromjavafolder=true","excel");
							}else{
								var win=open("/servlet/vfsservlet?fileid="+fieldid+"&fromjavafolder=true","excel");
							}
						}
					}else{
						Ext.showAlert("导出失败！错误信息："+result.message);
					}
				}
			},
			interval:1000//时间间隔
		};
		Ext.TaskManager.start(task);
	},/**
     *导入成功
     **/
    downExcelSuccess:function(list){
  		var success = false;//是否上传成功
		var errorFileName = "";//导入数据失败提示excel
		var onlyname = "";//唯一标识
		var flag = '0';//上传程序是否执行完成
		var message = '';//报错提示信息
		var fileid = list[0].fileid;
		var filename = list[0].filename;
		var map = new HashMap();
   		map.put("tabid",this.tab_id);
   		map.put("fileid",fileid);
		map.put("filename",filename);
   		map.put("ins_id",this.templPropety.ins_id);
   		map.put("task_id",this.templPropety.task_id);
   		map.put("infor_type",this.templPropety.infor_type);
   		map.put("moudle_id",this.templPropety.module_id);
   		Rpc({functionId:'MB00008009',async:true,success:function(response,action){
   			var result = Ext.decode(response.responseText);
   			flag = '1';
   			success = result.succeed;
   			message = result.message;
   			if(success){
	   			errorFileName = result.errorFileName;//导入数据失败提示excel
   			}
   		}},map);
   		
		var msgBox = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
			title: common.button.promptmessage,
			modal:true,
			width:300,
			progress:true
	    });

		var progressText='正在导入Excel模板文件,请稍候...';//进度条信息
		var win = Ext.getCmp('importExcelId');
		var task = {
			run:function(){
				//进度条信息
				//更新信息提示对话框
				msgBox.updateProgress('',progressText,'');
				//完成上传文件，关闭更新信息提示对话框
				if(flag =='1'){
					Ext.TaskManager.stop(task);
					msgBox.hide();
    				if(success){
    					if(errorFileName){
    						var msg="导入失败，错误新如下："+errorFileName;
    						Ext.showAlert(msg);
    					}
    					else
    					{
    						var msg="导入Excel模板文件成功";
    						Ext.showAlert(msg);
    					}
    					win.destroy();
	    				templateTool_me.refreshAll("true");//刷新列表
	    			}else{
	    				Ext.showAlert(message);  
	    			}
				}
			},
			interval:1000//时间间隔
		};
		Ext.TaskManager.start(task);
    },
	/**
	 *导入成功
	 **/
	downSuccess:function(list){
		var success = false;//是否上传成功
		var errorFileName = "";//导入数据失败提示excel
		var onlyname = "";//唯一标识
		var flag = '0';//上传程序是否执行完成
		var message = '';//报错提示信息
		var fileid = list[0].fileid;
		var filename = list[0].filename;
		var successNum=0;
		var importCount=0;
		var map = new HashMap();
		map.put("tabid",this.tab_id);
		map.put("fileid",fileid);
		map.put("filename",filename);
		map.put("ins_id",this.templPropety.ins_id);
		map.put("task_id",this.templPropety.task_id);
		map.put("infor_type",this.templPropety.infor_type);
		Rpc({functionId:'MB00002009',async:true,success:function(response,action){
				var result = Ext.decode(response.responseText);
				flag = '1';
				success = result.succeed;
				message = result.message;
				if(success){
					errorFileName = result.errorFileName;//导入数据失败提示excel
					successNum=result.successNum;
					importCount=result.importCount;
					onlyname =result.onlyname;
				}
			}},map);

		/*   		var msgBox = Ext.MessageBox.show({
                    title:'提示',
                    //msg:'动态更新进度条和信息文字',
                    modal:true,
                    width:300,
                    progress:true
                })*/
		var msgBox = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
			title: common.button.promptmessage,
			modal:true,
			width:300,
			progress:true
		});

		var progressText='正在导入数据,请稍候...';//进度条信息
		var win = Ext.getCmp('importDataId');
		var task = {
			run:function(){
				//进度条信息
				//更新信息提示对话框
				msgBox.updateProgress('',progressText,'');
				//完成上传文件，关闭更新信息提示对话框
				if(flag =='1'){
					Ext.TaskManager.stop(task);
					msgBox.hide();
					if(success){
						if(errorFileName){
							open("/servlet/vfsservlet?fromjavafolder=true&fileid="+errorFileName);//下载错误提示信息excel lis 20160805
						}
						else
						{
							var msg="成功";
							//55169  V771封版：office2007单位部门岗位级联测试：人事异动新增类型的模板导入数据时提示信息不对，现有程序提示“成功修改*条数据”
							//syl57831  v771封版：组织机构/机构调整，导入数据，提示信息，当没有要修改的数据时，修改0条数据就不要显示了。
							if(importCount>0){
								msg+="追加"+importCount+"条数据";
								if(successNum>0){
									msg+=",";
								}else{
									msg+="。";
								}
							}
							if(successNum>0){
								msg+="修改"+successNum+"条数据。";
							}
							if(msg.length==2){
								msg+="导入"+successNum+"条数据。";
							}
							Ext.showAlert(msg);
						}
						win.destroy();
						templateTool_me.refreshAll("true");//刷新列表
					}else{
						Ext.showAlert(message);
					}
				}
			},
			interval:1000//时间间隔
		};
		Ext.TaskManager.start(task);
	},

	/**
	 * 单位合并
	 */
	combine:function(){
		var table_name = this.templPropety.table_name;
		var infor_type = this.templPropety.infor_type;
		var tab_id = this.tab_id;
		var map = new HashMap();
		map.put("tabid",this.tab_id);
		map.put("table_name",table_name);
		map.put("infor_type",this.templPropety.infor_type);
		Rpc({functionId:'MB00002020',async:true,success:function(response,action){
				var result = Ext.decode(response.responseText);
				var success = result.succeed;
				if(result.succeed){
					var msg = result.msg;
					var operationType = result.operationType;
					if(msg=="equals"){
						if(operationType=="8"){
							Ext.showAlert(MB.MSG.selectCombineData);
						}else if(operationType=="9"){
							Ext.showAlert(MB.MSG.selectCombineData2);
						}
					}else if(msg=="ok"){
						var maxstartdate=result.maxstartdate;//开始日期
						var obj = new Object();
						obj.operationType = operationType;
						obj.maxstartdate = maxstartdate;
						obj.table_name = table_name;
						obj.infor_type = infor_type;
						obj.tab_id = tab_id;
						if(operationType=="8"){
							Ext.require('TemplateToolBarUL.org.OrgCombine',function(){
								Ext.create("TemplateToolBarUL.org.OrgCombine",obj);
							});
						}else if(operationType=="9"){
							Ext.require('TemplateToolBarUL.org.OrgCombine',function(){
								Ext.create("TemplateToolBarUL.org.OrgCombine",obj);
							});
						}
					}
					else if(msg=="date"){
						Ext.showAlert("你选择了有效日期为当日机构,不允许此操作！");
					}
					else{
						if(msg.length>5){
							Ext.showAlert(replaceAll(replaceAll(replaceAll(msg,"\\r\\n","<br />"),"\\n","<br />"),"\\r","<br />"));
						}else
							Ext.showAlert("检查能否此操作时失败，不允许此操作！");
					}
				}
			}},map);
	},
	/**
	 *自助配置链接无返回提交后刷新页面
	 */
	refreshApply:function(){
		/*var parent = templateMain_me.mainPanel.ownerCt;
        parent.removeAll(true);
        var templateBean = new Object();
        templateBean.sys_type="1";
        templateBean.tab_id=this.templPropety.tab_id;
        templateBean.return_flag="14";
        templateBean.module_id="9";
        templateBean.approve_flag="1";
        templateBean.task_id="0";
        templateBean.card_view_type="1";
        templateBean.view_type="card";
        Ext.require('TemplateMainUL.TemplateMain', function(){
             Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateBean});
           //parent.add(templateMain_me.mainPanel);
        });*/
		location.href=rootPath+"/module/template/templatemain/templatemain.html?b_query=link&tab_id="+this.templPropety.tab_id+"&view_type=card" +
			"&task_id=0&return_flag=14&module_id=9&approve_flag=1&card_view_type=1&approve_flag=1";
	},
	/**
	 *刷新页面
	 **/
	refreshData:function(){
		templateTool_me.save('true','false');
		var map = new HashMap();
		map.put("tab_id",this.tab_id);
		map.put("isSysData",true);
		map.put("ins_id",this.templPropety.ins_id);
		map.put("task_id",this.templPropety.task_id);
		map.put("infor_type",this.templPropety.infor_type);
		map.put("view_type",this.templPropety.view_type);
		map.put("module_id",this.templPropety.module_id);
		map.put("allNum",templateTool_me.getTotalCount());
		Ext.MessageBox.wait("正在刷新,请稍候...", "等待");
		Rpc({functionId:'MB00004005',async:true,success:templateTool_me.refreshDataSuccess},map);
	},
	refreshDataSuccess:function(form,action){
		setTimeout(function(){Ext.MessageBox.close();},1000);
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
			var message = result.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
			}
		}else{
			if(templateTool_me.templPropety.view_type=='card'){
				templateCard_me.refreshCurrPerson();
				var fieldSet = templateCard_me.getCurFieldSet().fields;
				for (var i=0;i<fieldSet.length;i++){
					var field=fieldSet[i];
					if(field.fldName == "A0101" || field.fldName == "codeitemdesc"){//实时更新姓名
						if(!!templateCard_me){
							var valueItem = templateCard_me.getValueItem(field.uniqueId);
							templateCard_me.changedA0101 = valueItem.keyValue;
							templateCard_me.changeA0101("1");
						}
					}
				}
			}else{
				templateTool_me.refreshAll();
			}
		}
	},
	/**
	 * 自定义审批流程
	 */
	showDefFlowSelf:function(){
		var obj=new Object();
		obj.tabid=this.tab_id;
		if(this.templPropety.orgId)
			obj.orgId=this.templPropety.orgId;
		else
			obj.orgId="";
		Ext.onReady(function(){
			Ext.require('SelfDefineFlowUL.SelfDefineFlow', function(){
				DefineFlowSelfGlobal = Ext.create("SelfDefineFlowUL.SelfDefineFlow", obj);
			});
		});
	},
	//liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
	/*按钮置灰，用户不可点击*/
	disabledButton:function(id){
		var button=Ext.getCmp(id);
		if(button){
			button.disable();
		}
	},
	/*按钮恢复，用户可点击*/
	enabledButton:function(id){
		var button=Ext.getCmp(id);
		if(button){
			button.enable();
		}
	},
	//撤回
	recallTask:function(){
		var map = new HashMap();
		var records = [];
		var record = {};
		record["task_id_e"]=templateTool_me.templPropety.task_id;
		record["ins_id"]=templateTool_me.templPropety.ins_id;
		record["tabid"]=templateTool_me.templPropety.tab_id;
		records.push(record);
		map.put("recallList",records);
		map.put("module_id",templateTool_me.templPropety.module_id);
		map.put("ischeck","1");
		Ext.showConfirm("确定执行撤回操作吗?",function(optional){
			if(optional=='yes'){
				Rpc({functionId:'MB00006018',async:false,success:function(form,action){
						var result = Ext.decode(form.responseText);
						var flag=result.succeed;
						if(flag==true){
							var notRecallName = result.notRecallName;
							if(notRecallName!=undefined&&notRecallName.length>0){
								Ext.showAlert("《"+notRecallName+"》表单已被查看或者已被处理,不可撤回!",function(){
									//MyApplyScope.loadTable();
								});
								return;
							}
							var recallname = result.recallname;
							if(recallname!=undefined&&recallname.length>0){
								Ext.showConfirm("《"+recallname+"》表单中存在未提交的数据,撤回将被覆盖,确定撤回吗\?", function(optional){
									if(optional=='yes'){
										var map = new HashMap();
										map.put("recallList",records);
										map.put("module_id",templateTool_me.templPropety.module_id);
										map.put("ischeck","0");
										Rpc({functionId:'MB00006018',async:false,success:function(form,action){
												var result = Ext.decode(form.responseText);
												if(result.succeed){
													templateTool_me.refeshTable();
												}else{
													Ext.showAlert(result.message);
												}
											}},map);
									}else{
										return;
									}
								});
							}else{
								templateTool_me.refeshTable();
							}
						}else{
							Ext.showAlert(result.message);
						}
					}},map);
			}else
				return;
		});
	},
	refeshTable:function(){
		/*var parent = templateMain_me.mainPanel.ownerCt;
    	parent.removeAll(true);
    	var templateBean = new Object();
		templateBean.sys_type="1";
		templateBean.tab_id=this.templPropety.tab_id;
		templateBean.return_flag=this.templPropety.return_flag;
		templateBean.module_id=this.templPropety.module_id;
		templateBean.approve_flag="1";
		templateBean.task_id="0";
        templateBean.card_view_type=this.templPropety.card_view_type;
	    templateBean.view_type=this.templPropety.view_type;
	    templateBean.callBack_close=this.templPropety.callBack_close;
		Ext.require('TemplateMainUL.TemplateMain', function(){
			 Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateBean});
			 parent.add(templateMain_me.mainPanel);
		});*/
		location.href=rootPath+"/module/template/templatemain/templatemain.html?b_query=link&tab_id="+this.templPropety.tab_id+"&view_type="+
			this.templPropety.view_type+"&ins_id=0&return_flag="+this.templPropety.return_flag+"-r&module_id="+this.templPropety.module_id+"&approve_flag=1&card_view_type="+
			this.templPropety.card_view_type;
	},
	getTotalCount:function(){
		if(this.templPropety.view_type=="list"){
			//获取选中行数据
			tablePanel=templateList_me.templateListGrid.tablePanel;
		}else{
			tablePanel=templateCard_me.personListGrid.tablePanel;
		}
		var totalCount = tablePanel.store.totalCount;
		return totalCount+"";
	}
});

