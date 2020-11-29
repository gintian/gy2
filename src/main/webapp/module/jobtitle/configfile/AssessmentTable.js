/**
 * 职称评审_配置_测评表配置
 * @createtime April 4, 2018 15:07:55 PM
 * @author linbz
 * 
 * */
	
Ext.define('ConfigFileURL.AssessmentTable', {
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	// 构造方法
	constructor : function(config) {
		assessmentTable_me = this;
		this.value = false==config.value?"":config.value;
		// =1展现全部树，=4展现参数配置的已勾选的模板表  （默认=1）
		this.selectType = Ext.isEmpty(config.selectType) ? "1" : config.selectType;
		// 创建或编辑会议，阶段时已选模板表
		this.selectTabids = Ext.isEmpty(config.selectTabids) ? "" : config.selectTabids;
		// 回调函数
		this.success = config.success;
		// 已选指标集合
		this.checkids = "," + (("4"==assessmentTable_me.selectType)?(this.selectTabids+","):(this.value+","));
		// 已选指标|名称集合
		this.checkidTexts = ",";
		// 保存按钮显示/隐藏
		this.saveButHiddenflag = (null==config.saveButHiddenflag)?false:config.saveButHiddenflag;
		
		this.showAssessConfigWin();
		
		//自适应窗口大小 haosl
		Ext.EventManager.onWindowResize(function(){
			var assessconfigwin =  Ext.getCmp('assessconfigwin');
			if(assessconfigwin){
				assessconfigwin.setWidth(Ext.getBody().getWidth()*0.8);
				assessconfigwin.setHeight(Ext.getBody().getHeight()*0.86);	
			}
		});
	},
	// 测评表配置
	showAssessConfigWin : function() {
		var map = new HashMap();
		map.put("type", this.selectType);
		// 传入已选模板ID,以逗号分割 P0032,P00043
		map.put("value", this.value);
		map.put("selectTabids", this.selectTabids);
		map.put("dataflag", "0");
		Rpc({functionId : 'ZC00004007',async : false,success : this.showAssessConfigWinOK,scope:this}, map);
	},
	showAssessConfigWinOK:function(form, action){
		var result = Ext.decode(form.responseText);
		// 第一个模板表的路径
		var firstSrc = result.firstSrc;
		// 第一个模板表id
		var firstTempid = result.firstTempid;
		// 原有的已选模板id|名称，
		assessmentTable_me.checkidTexts = ","+result.selectidTexts+",";
		// 左侧模板树store
		var treeStore = Ext.create('Ext.data.TreeStore', {
			proxy:{
			    	type: 'transaction',
	        		functionId:'ZC00004007',
	        		// 参数
			        extraParams:{
			        	type:this.selectType,
			        	dataflag:"1",
		        		value:this.value,
		        		selectTabids : this.selectTabids
			        },
			        // 返回值   
			        reader: {
			            type: 'json',
			            root: 'data'    	
			        }
			},
			root: {
				// 根节点的文本					
				text:'测评表分类',
				expanded: true,
				icon:"/images/add_all.gif"
			}
		});
		// 左侧模板树
		treePanel = Ext.create('Ext.tree.Panel', {
			// 不使用Vista风格的箭头代表节点的展开/折叠状态
			useArrows: false,
			width:'25%',
			margin:'0 2px 0 0',
			region:'west',
			height:'100%',
			id:'treePanel',
			// 指定该树所使用的TreeStore
			store: treeStore, 
			// 指定根节点可见
			rootVisible: true
		});
		
		// 监听1，所有业务类共有监听 监听被点击的节点信息
		treePanel.on("itemclick",function(view,record,item,index,e){
			// record.data.id 如 Z005.16|2
			var tableid = record.data.id;
			if(!Ext.isEmpty(tableid) && undefined!=tableid){
				var nodeid = tableid.split("|")[1];
				if("2" == nodeid){
					var tabid = tableid.split(".")[0];
					if(!Ext.isEmpty(tabid) && undefined!=tabid){
						// 记录选中或取消勾选
						assessmentTable_me.checkOutOff(record.data.checked, tabid, record.data.text);
						// 选中事件 渲染右侧表格内容
						assessmentTable_me.getTableContent(tabid);
					}
				}else
					Ext.getCmp('tableContentid').setHtml(zc.label.templatePrompt);
			}
		});
		
		// 监听2，监听当前点击的父节点 单选,以前选中节点未展开
		treePanel.on('itemexpand', function(obj) { 
			// 获得所有被选中的模版
			var nodes = treePanel.getChecked();
			// 若没有选中的模板则默认展开第一个
			if(obj.childNodes.length > 0) {
				if(0==nodes.length 
						&& (Ext.isEmpty(assessmentTable_me.value)
								||(("4"==assessmentTable_me.selectType) && Ext.isEmpty(assessmentTable_me.selectTabids)))){
					
					obj.firstChild.expand();
					// 并展现该表的详细信息
					var tableid = obj.firstChild.id;
					var nodeid = tableid.split("|")[1];
					if("2" == nodeid){
						var tabid = tableid.split(".")[0];
						if(!Ext.isEmpty(tabid) && undefined!=tabid)
							assessmentTable_me.getTableContent(tabid);
					}
				}else{
					var ids = firstSrc.split(",");
					Ext.each(ids, function (nodeid) {
						if(!Ext.isEmpty(nodeid)){
							if(treeStore.byIdMap[nodeid]){
								treeStore.byIdMap[nodeid].expand();
								// 展现所选的第一个模板firstTempid
								assessmentTable_me.getTableContent(firstTempid);
							}
						}
					});
				}
			}
		});
		
		// 主窗体
		Ext.create('Ext.window.Window', {
			title:zc.label.assessmentTablePn + zc.label.peizhiBtn,
			id:'assessconfigwin',
			layout: 'border',
			modal: true,
			width:Ext.getBody().getWidth()*0.8,
			height:Ext.getBody().getHeight()*0.86,
			border:false,
			padding:'2 2 2 2',
			autoScroll:false,
		    items: [
		    	treePanel
		    	,{
		    	id:'tableContentid',
		    	region:'center',
		    	xtype:'panel',
		    	height:'100%',
		    	layout:'fit',
		    	border:1,
		    	margin:'0 0 0 2',
		    	autoScroll:true,
		    	// 请选择左侧模板，不要选择模板分类。
		    	html:zc.label.templatePrompt
			    }
		    ],
		    buttonAlign:'center',
		    fbar : [{
						text : zc.label.confirm,
						hidden : assessmentTable_me.saveButHiddenflag,
						handler : function() {
							var selectids = "";
							var selectidTexts = "";
							var checkstrs = assessmentTable_me.checkids.split(",");
							Ext.each(checkstrs, function (checkstr) {
								if(!Ext.isEmpty(checkstr))
									selectids += checkstr + ",";
							});
							var texts = assessmentTable_me.checkidTexts.split(",");
							Ext.each(texts, function (text) {
								if(!Ext.isEmpty(text) && text!='undefined')
									selectidTexts += text + ",";
							});
							
							this.success(selectids, selectidTexts);
							Ext.getCmp('assessconfigwin').close();
						},
						scope : this
			},{
				text : zc.label.cancel,
				handler:function(){
					Ext.getCmp('assessconfigwin').close();
				}
			}]
		    
		}).show();
	},
	/**
	 * 选中事件 渲染右侧表格内容
	 */
	getTableContent:function(tabid){
		
		var map = new HashMap();
		map.put("type", "2");
		// 传入已选模板ID  Z005
		map.put("tabid", tabid);
		Rpc({functionId : 'ZC00004007',async : false,success : function(form){
			var result = Ext.decode(form.responseText);
			if(result.succeed){
				var tableContent = result.tableContent;
				Ext.getCmp('tableContentid').setHtml(tableContent);
			}
			else
				Ext.showAlert(result.message);
			
	      },scope:this},map);
	},
	/**
	 * 记录选中或取消的模板信息
	 */
	checkOutOff:function(checked, tabid, text){
		// 处理text[Z005]集团员工绩效考核表
		if(text.indexOf("]") > -1)
			text = text.split("]")[1];
		// 记录选中
		if(checked){
			if(assessmentTable_me.checkids.indexOf("," + tabid + ",") == -1){
				assessmentTable_me.checkids += tabid + ",";
				assessmentTable_me.checkidTexts += tabid+"|"+text + ",";
			}
		}
		// 记录非选中
		else{
			// 移除掉选中集合里数据
			if(assessmentTable_me.checkids.indexOf("," + tabid + ",") > -1){
				assessmentTable_me.checkids = (assessmentTable_me.checkids).replace("," + tabid + ",", ",");
				assessmentTable_me.checkidTexts = (assessmentTable_me.checkidTexts).replace("," + tabid+"|"+text + ",", ",");
			}
		}
	}
	
});
