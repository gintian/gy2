/**
 * 职称评审_配置_公示材料配置
 * @createtime August 24, 2017 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ConfigFileURL.NoticeConfig', {
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	configMap:new Ext.util.HashMap(),//配置信息
	key : '',//6：材料审查模板 5:论文送审
	constructor : function(config) {// 构造方法
		this.defaultConfigMap = this.getDefaultConfigArray();
		this.key = config.key;
		this.showNoticeConfigWin();
		this.support_subcomittee = false;//是否启用了二级单位 2017-07-13
	},
	// 公示材料配置
	showNoticeConfigWin : function() {
		/*window.onresize = function(){
			var noticeconfigwin = Ext.getCmp("noticeconfigwin");
			if(!!noticeconfigwin){
				noticeconfigwin.setWidth(document.documentElement.clientWidth);
				noticeconfigwin.setHeight(document.documentElement.clientHeight);
			}
				
		}*///这样写在ie下出现各种样式问题，用ext自己的maximized：true
		var map = new HashMap();
		map.put("type", '1');// 1：获取模板信息
		map.put("key", this.key);// 6：材料审查模板 5:论文送审
		Rpc({functionId : 'ZC00004005',async : false,success : this.showNoticeConfigWinOK,scope:this}, map);
	},
	showNoticeConfigWinOK:function(form, action){
		var result = Ext.decode(form.responseText);
		var dataList = result.data;
		//为二级单位赋值  haosl 2017-07-13
		this.support_subcomittee = result.support_subcomittee;
		// 去除掉tabpanel的蓝色外边框
		var css_template_tab="#tabpanel_tabpanel-body {border-width: 0px 1px 1px 1px;}";
		if(!!!Ext.util.CSS.getRule(css_template_tab))
			Ext.util.CSS.createStyleSheet(css_template_tab,"tab_css");
		
		// 获取tab页
		var tabpanal = Ext.widget('tabpanel', {
			id:'tabpanel',
			minTabWidth:50,
			removePanelHeader:true,
			minTabWidth:50,
			animScroll:true,	
//			frame : true,//填充画面
		    height:600,
//			plain:true,//选项卡不显示背景
			layout:'fit',
			bodyStyle: 'padding:0px;TOP:0px;margin:0px 0px 2px;'
		});
		
		for(var i=0; i<dataList.length; i++){
			var data = dataList[i];
			
			var templateId = data.templateId;
			var templateName = data.templateName;
		
			tabpanal.add(this.getTemplatePageGrid(templateName, templateId));
			
	        if(i == 0){
	        	tabpanal.setActiveTab(templateId);
	        }
		}
		// 主窗体
		Ext.create('Ext.window.Window', {
			title:'材料配置',
			id:'noticeconfigwin',
			layout: 'fit',
			modal: true,
			width:document.documentElement.clientWidth,
			height:document.documentElement.clientHeight,
			border:false,
			autoScroll:false,
			maximized:true,//自动适应
			resizable :false,
		    items: [tabpanal],
		    buttonAlign:'center',
		    buttons : [{
						text : '确定',
						handler : function() {
							var map = new HashMap();
							var configArr = new Array();
							this.configMap.each(function(key, value){
								var config = new HashMap();
								config.put('key', key);
								config.put('value', value);
							    configArr.push(config);
							});
							map.put('type', '4');// 保存
							map.put('configArr', configArr);//配置信息
							Rpc({functionId:'ZC00004005',async:false,success:function(form,action){
								var msg = Ext.decode(form.responseText).msg;
								if(!Ext.isEmpty(msg)){
									Ext.showAlert(msg, function(){ 
							           Ext.getCmp('noticeconfigwin').close();
							        }, this); 
								}
							},scope:this},map);
							
						},
						scope : this
					}, {
						text : '取消',
						handler : function() {
							Ext.getCmp('noticeconfigwin').close();
						},
						scope : this
					}]
		}).show();
	},
	getTemplatePageGrid : function(templateName, templateId) {
		
		var columns = this.getColumns();
		
		return Ext.create('Ext.grid.Panel', {
			id:templateId,
			title : templateName,
			store : this.getTemplatePageData(templateId),
			columns : columns
		});
	},
	// 获取列,材料审查时显示【是否公示】【评委会】【学科组】【二级单位】、论文送审时显示【同行专家】
	getColumns : function() {
		var columns = new Array();
		columns.push({
				header : '序号',
				dataIndex : 'seq',
				menuDisabled : true,
				sortable : false,
				align:'center',
				flex : 1
			});
		columns.push({
				header : '页签名',
				dataIndex : 'title',
				menuDisabled : true,
				sortable : false,
				align:'center',
				flex : 2
			});
		
		
		for(var i=0; i<=4; i++){
			if(this.key == '6' && i == '3'/* || (!this.support_subcomittee && i=='4')*/){//材料审查时不要“同行专家”。//原先需要启用二级单位所以限制了现在不需要暂时给注销了
				continue ;
			} else if(this.key == '5' && (i == '0' || i == '1' || i == '2' || i == '4')){//论文送审时不要“是否公示”“评委会”“学科组”“二级单位”。
				continue ;
			}
			
			var header = '';
			if(i == '0'){
				header = '是否公示';
			} else if(i == '1'){
				header = '评委会';
			} else if(i == '2'){
				header = '专业（学科组）';
			} else if(i == '3'){
				header = '同行专家';
			} else if(i == '4'){
				header = '二级单位评议组';
			}
			
			columns.push({
				dataIndex:'notice_'+i,
				header : header,
				menuDisabled : true,
				sortable : false,
				align:'center',
				flex : 2,
				renderer:function(value, metaData, record, rowIndex){
					var html='';
					
					var tabid = record.data.tabid;
					var pageid = record.data.pageid;
					
					var key = tabid + "_" + pageid + "_" + value;
					
					this.configMap.add(key, this.defaultConfigMap[key]?this.defaultConfigMap[key]:'true');
					
					var id = 'chk_' + key;
					var checked ='';
					if(this.configMap.get(key) == 'true'){
						checked='checked=checked';//选中
					}
					html = "<div style='width:100%;text-algn:center'>" +
								"<input type='checkbox' onclick='jobtitleNoticeConfigGloble.setIsnotice(this)' id=" +id+" "+checked+">"+
							"</div>";
							
					return html;
				},
				scope:this
			});
		}
		
		
		return columns;
	},
	// 获取页签数据
	getTemplatePageData : function(tabid) {

		return Ext.create('Ext.data.Store', {
			fields : [ 'seq', 'pageid', 'title', 'notice_0', 'notice_1', 'notice_2', 'notice_3', 'notice_4'],
			proxy : {
				type: 'transaction',
				functionId : 'ZC00004005',
				extraParams : {
					type : '2',
					tabid : tabid
				},
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true
		});
	},
	setIsnotice: function(component){
		var arr = component.id.split('_');
		var tabid = arr[1];
		var pageid = arr[2];
		var type = arr[3];
		
		var state = component.checked;
		
		var key = tabid + "_" + pageid + "_" + type;
		this.configMap.replace(key, state);
	},
	// 获取配置参数
	getDefaultConfigArray:function(){
		
		var configMap = new HashMap();

		var map = new HashMap();
		map.put("type", "3");// 获取配置信息
		Rpc({functionId:'ZC00004005',async:false,success:function(form,action){
			configMap = Ext.decode(form.responseText).configmap;
		}},map);
		return configMap;
	}
});
