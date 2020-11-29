/*
数据表显示组件，默认为全屏显示
组件调用参数说明：
1、title：表格title
2、subModuleId: 表id，必须唯一，用于保存栏目设置和查询方案
3、setname:数据来源，表名或试图
4、privtype:	权限类型。manage代表管理范围；unit代表操作单位；如果走业务范围，直接传业务模块id。如果为空，则代表不控制权限
5、infokind:数据类型，A代表人员，B代表单位，K代表岗位。
			A：表中必须存在nbase、A0100、B0110、E0122、E01A1 指标，用于权限控制
			B：表中必须存在B0110字段，用于权限控制
			K：表中必须存在E01A1字段，用于权限控制
			权限校验时，会根据此参数确定以哪些指标进行权限控制
6、privfield:指定以哪个指标用于权限控制，此参数优先级大于infokind参数
7、nbase:人员库
			all：所有人员库，不走个人人员库权限
			self：走当前登录用户人员库权限
			Usr,Oth：指定人员库，多个用逗号隔开。加载时将过滤掉没有权限的人员库
			不配置此属性时默认为self
8、queryfield:默认查询指标，多个用逗号隔开，例如：a0101,a0104,a0107
9、beforeBuildComp: 创建表格之前的回调方法
10、handlerScope: 回调方法作用域
*/
Ext.define("EHR.dataview.DataView",{
	requires:['EHR.tableFactory.TableBuilder','EHR.commonQuery.CommonQuery'],
	//构建方法
	constructor:function(config){
		
		if(!config.reportid){
			if(!config.subModuleId){
				alert("no [subModuleId] attribute,please check your config.");
				return;
			}
			if(!config.setname){
				alert("no [setname] attribute,please check your config.");
				return;
			}
		}
		this.beforeBuildComp = config.beforeBuildComp;
		this.handlerScope = config.handlerScope;
		delete config.beforeBuildCmp;
		delete config.handlerScope;
	
		var map = new HashMap();
		config.privtype = config.privtype||'';
		config.infokind = config.infokind||'';
		config.privfield = config.privfield||'';
		Ext.apply(map,config);
		Rpc({functionId:'ZJ100000181',success:this.createTable,scope:this},map);
		
		window.DataViewComp_self = this;
	},
	//创建表格
	createTable:function(response){
		var param = Ext.decode(response.responseText);
		if(param.errorMsg){
			Ext.showAlert(param.errorMsg);
			return;
		}
		this.subModuleId = param.subModuleId;
		this.columnLinkMap = param.columnLinkMap;
		this.codeFieldMap = param.codeFieldMap;
		this.templateList = param.templateList;
		this.infokind = param.infokind;
		var tableConfig = param.tableConfig;
		tableConfig = Ext.decode(tableConfig);
		tableConfig.beforeBuildComp = this.beforeBuildComp;
		tableConfig.handlerScope = this.handlerScope;
		this.tableBuilder = Ext.create("EHR.tableFactory.TableBuilder",tableConfig);
		this.createCommonQuery(this.tableBuilder,param.storageQueryField,param.defaultQueryField);
		if(this.templateList.length>0)
			this.createTemplateMenu(this.tableBuilder,this.templateList);
	},
	
	createCommonQuery:function(tableCmp,storageField,defaultField){
		var defaultQueryField;
		if(defaultField){
			defaultQueryField = defaultField;
		}else if(storageField.length>5){
			defaultQueryField = storageField.slice(0,5);
		}else{
			defaultQueryField = 	storageField;
		}
		
		var queryConfig = {
			xtype:'commonquery',
			padding:'0 0 4 0',
			subModuleId:this.subModuleId,
			defaultQueryFields:defaultQueryField,
			optionalQueryFields:storageField,
			doQuery:this.executeQuery,
			scope:this
		};
		tableCmp.insertItem(queryConfig,0);
	},
	executeQuery:function(items){
		var me = this;
		var vo = new HashMap();
		vo.put("subModuleId",me.subModuleId);
		vo.put("items",items);
		Rpc({functionId:'ZJ100000182',success:function(){
			me.tableBuilder.reloadStore();
		}},vo);
	},
	columnLinkRender:function(value,metadata,record){
	
		if(!metadata)
			return value;
	
		var linkurl = DataViewComp_self.columnLinkMap[metadata.column.dataIndex];
		
		while(true){
			var start = linkurl.indexOf("${");
			if(start == -1)
				break;
			var end = linkurl.substr(start+2).indexOf("}");
			if(end == -1)
				break;
			var field = linkurl.substr(start+2,end);
			var param;
			if(field=='a0100')
				param = record.get(field+"_e");
			else{
				param = record.get(field.toLowerCase());
				param = param && param.length>0 && DataViewComp_self.codeFieldMap[field.toLowerCase()]?param.split("`")[0]:param;
			}
			
			linkurl = linkurl.replace("${"+field+"}",param);
		}
		if(metadata.column.xtype=='codecolumn')
			value = value.split("`")[1];
		if(value)
			return "<a href='"+linkurl+"' target='_blank'>"+value+"</a>";
		else
			return '';
	},
	createTemplateMenu:function (builder,templates) {
		var me = this;
		builder.toolBar.add({
			xtype:'button',
			text:'业务办理',
			menu:{
				defaults:{
					handler:this.openTemplate,
					scope:me
				},
				items: templates
			}
		})
	},
	openTemplate:function (item) {
		var tabId = item.tempid;
		var tabName = item.text;
		var value = new HashMap();
		value.put("tabid",tabId);
		var records = this.tableBuilder.tablePanel.getSelectionModel().getSelection();

		var type = undefined;
		var moduleId =1;
		if(this.infokind == 'A'){
			type = '1';
			moduleId =1;
		}else if(this.infokind == 'B'){
			type = '2';
			moduleId =7;
		}else if(this.infokind == 'K'){
			type = '3';
			moduleId =8;
		}
		if(records.length>0){
			var newRecords = [];
			for(var i=0;i<records.length;i++){
				var data = Ext.clone(records[i].data);
				if(this.infokind=='A'){
					data.basepre = data.nbase.split('`')[0];
					data.a0100 = data.a0100_e;
				}
				for(var key in data){
					var fieldValue = data[key];
					if(typeof(fieldValue)=='string' && fieldValue.split('`').length==2){
						data[key] = fieldValue.split('`')[0];
					}
				}
				newRecords.push(data);
			}
			value.put('savedata',newRecords);

			value.put('type',type);
		}else{
			this.openTemplateWindow(tabId,tabName,moduleId);
			return;
		}
		Rpc({functionId:'MB00007010',success:function(resp){
			var param = Ext.decode(resp.responseText);
			if(!param.flag){
				Ext.showAlert(param.errMsg);
				return;
			}
			this.openTemplateWindow(tabId,tabName,moduleId)
		},scope:this},value);
	},
	openTemplateWindow:function(tabId,tabName,moduleId){
		Ext.widget('window',{
			width:'100%',
			height:'100%',
			maximized:true,
            title:tabName,
			html:"<iframe width='100%' height='100%' frameborder='0' src='/module/template/templatemain/templatemain.html?b_query=link&ins_id=0&tab_id="+tabId+"&return_flag=14&approve_flag=1&module_id="+moduleId+"&etoken=11&other_param=visible_title=0'></iframe>"
		}).show();
		/*
		window.open(
			"/module/template/templatemain/templatemain.html?b_query=link&ins_id=0&tab_id="+tabId+"&return_flag=14&approve_flag=1&module_id="+moduleId+"&view_type=card",
			"_blank",
			"left=0,top=0,width="+window.clientWidth+",height="+window.clientHeight+",scrollbars=yes,toolbar=no,menubar=no,location=yes,resizable=yes,status=no"
		);

		 */
	}

});