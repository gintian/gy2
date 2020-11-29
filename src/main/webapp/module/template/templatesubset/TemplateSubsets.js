/**
 * 人事异动-人事异动列表-子集编辑 弹出页面
 */
Ext.define('TemplateSubsetUL.TemplateSubsets',{
	extend:'Ext.window.Window',
	requires:["SYSF.FileUpLoad"],
	substore:'',   //表格数据源  
	subcolumns:'', //表格的列
	subtableObj:'',
	templPropety:'', 
	subDatajson:'', 
	saveflag:'true',
	subDatajsonStr:'',//优化记录回传的xml数据
	closeFlag:'',//子集数据是否变化，变化先保存再关闭，否则直接关闭。
	isNeedSubsetNo:false,//是否显示子集序号
	isShowWinCellChange:false,//子集弹出框时 判断单元格是否做修改
    constructor:function(config){
        templateSubset = this;
		var templateSubset_me = this;
		//liuyz bug25354 
		templateSubset_me.onlyHistoryReord=true;//只有历史不可编辑记录
		templateSubset_me.templPropety=config.map;
		templateSubset_me.uniqueId = this.templPropety.uniqueId;//只有卡片有此参数
		templateSubset_me.view_type = this.templPropety.view_type;//区分来自卡片 还是列表
		if(templateSubset_me.templPropety.isShowWin)
			templateSubset_me.isShowWin = this.templPropety.isShowWin;
		else
			templateSubset_me.isShowWin = "1";//“0”不显示，“1”显示
		//生成唯一id，将组件注册到 ext组件管理对象中，方便后面调用
		this.templateSubset_id = Ext.id(this,"ext-templatesubset");
		Ext.ComponentManager.register(this);
		templateSubset_me.init(config.map); 
    },
    init:function(config){	
    	var templateSubset_me = this;
    	var map = new HashMap();
    	map.put("ins_id",templateSubset_me.templPropety.ins_id);	
		map.put("tabid",templateSubset_me.templPropety.tabid);		
		map.put("a0100",templateSubset_me.templPropety.a0100);		
		map.put("objectid",templateSubset_me.templPropety.objectid);		
		map.put("basepre",templateSubset_me.templPropety.basepre);
		map.put("table_name",templateSubset_me.templPropety.table_name);		
		map.put("columnName",templateSubset_me.templPropety.columnName);
		map.put("data_xml",templateSubset_me.templPropety.data_xml);
		map.put("approveflag",templateSubset_me.templPropety.approveflag);
		map.put("nodePriv",templateSubset_me.templPropety.nodePriv);
		map.put("rwPriv",templateSubset_me.templPropety.rwPriv);
		map.put("chgState",templateSubset_me.templPropety.chgState);//变化前后
		map.put("allNum",templateTool_me.getTotalCount());
		map.put("task_id",templateMain_me.templPropety.task_id);	
		map.put("view_type",templateSubset_me.templPropety.view_type);	
		map.put("type","1");
		map.put("isAutoLog",templateMain_me.templPropety.isAutoLog);
		var functionid = "MB00003003";
		var isarchive = getTemplPropetyOthParam("isarchive");
		if(isarchive=='0'){
			functionid = "MB00008008";
			var record_id = getTemplPropetyOthParam("record_id");
			var archive_year = getTemplPropetyOthParam("archive_year");
			var archive_id = getTemplPropetyOthParam("archive_id");
			map.put("record_id",record_id);
			map.put("archive_year",archive_year);
			map.put("archive_id",archive_id);
		}
	    Rpc({functionId:functionid,async:false,success:templateSubset_me.getSubsetXml,scope:templateSubset_me},map);	
	},
	/**显示插入子集内容*/
	getSubsetXml:function(form,action)
	{
		var templateSubset_me = this;
		var tabid=templateSubset_me.templPropety.tabid;
		var table_name=templateSubset_me.templPropety.table_name;
		var a0100=templateSubset_me.templPropety.a0100;
		var basepre=templateSubset_me.templPropety.basepre;
		var form = Ext.decode(form.responseText);
		if(!form.succeed){
			var message = form.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
			}else
				Ext.showAlert(result.message);
		}else{
			templateSubset_me.isNeedSubsetNo=form.isNeedSubsetNo;
			templateSubset_me.allow_del_his = form.allow_del_his;
			var Xml_param=form.Xml_param;     //Xml_param存储的是子集的表结构，在 template_set 表中 
			templateSubset_me.record_key_id_pre=form.record_key_id_pre;//子集唯一值前缀
			this.subDatajson=Ext.decode(form.subDatajson); //subDatajson存储的是子集的数据，在 用户名template_tabid 表中
			this.subDatajsonStr=form.subDatajson; //subDatajson存储的是子集的数据，在 用户名template_tabid 表中
		    var field_name=templateSubset_me.templPropety.columnName;//子集名称。格式为t_a19_1 
		    var subFields=new Array();   //子集指标列参数
			templateSubset_me.parserSubXml(subFields,Xml_param);  //解析表结构的xml，把字段的一些属性存储到subFields中
			var rootNode = templateSubset_me.subDatajson;
			templateSubset_me.remarks=getDecodeStr(form.remarks); 
			templateSubset_me.chgInfoList=form.chgInfoList;//变动的子集记录
			if(rootNode)
			{
				templateSubset_me.SubSetView(rootNode,subFields,field_name,tabid,table_name,a0100,basepre);
				templateSubset_me.showViewByExt();
				
			}
		}
	},
	// 创建子集视图，rootNode为records记录数据，subFields为子集表结构，field_name为子集表名
	SubSetView:function(rootNode,subFields,field_name,tabid,table_name,a0100,basepre){
		var templateSubset_me = this;
		templateSubset_me.table_name=table_name;
		templateSubset_me.tabid=tabid;
		templateSubset_me._field_name=field_name;  //子集表名,如 t_a19_2
		templateSubset_me.a0100=a0100;       //人员编号
		templateSubset_me.basepre=basepre;   //人员库
		templateSubset_me._row=0;
		templateSubset_me._recNodes='';
        if(rootNode.records!=null){
			var recNodes = rootNode.records; 
			templateSubset_me._row=recNodes.length;//记录条数
			templateSubset_me._recNodes=recNodes;//各个record记录	
        }
		//数据里面的指标 可能与目前子集设置的指标个数不同 展现数据以此为准 代码型数据 todo wangrd
		var reordFields=rootNode.columns;
		
		templateSubset_me._rwPriv=templateSubset_me.templPropety.rwPriv; //子集的读写权限 =1为读权限，=2为写权限
		templateSubset_me._chgState=templateSubset_me.templPropety.chgState; //子集变化前后 =1为变化前，=2为变化后
		templateSubset_me._field_list=new Array();
		var fieldsPriv="";   //子集指标权限的集合，以','连接
		var fieldsWidth="";  //子集指标宽度的集合，以','连接
		var fieldsTitle="";  //子集指标标题的集合，以','连接
		var fields="";       //子集指标编码的集合，以','连接
		for (var i=0;i<subFields.length;i++){
			var subField =subFields[i];
			var fldname =subField.fldName;
			var fldTitle =subField.fldTitle;
			var newFldObj=new Object();
			newFldObj.fldName=fldname;
			newFldObj.fldType=subField.fldType;
			newFldObj.codesetId=subField.codeSetId;
			newFldObj.flddesc=fldTitle;
			newFldObj.fldPriv=subField.rwPriv;
			newFldObj.fldLength=subField.fldLength;
			newFldObj.fldDecLength=subField.fldDecLength;
			newFldObj.fldWidth=subField.fldWidth;
			newFldObj.defaultValue=subField.defaultValue;
			newFldObj.align=subField.align;
			newFldObj.format=subField.format;
			newFldObj.need=subField.need;//是否必填
			newFldObj.pre=subField.pre;//前缀
			newFldObj.imppeople=subField.imppeople;//是否启用选人组件
			newFldObj.his_readonly=subField.his_readonly;//列是否历史记录只读
			newFldObj.fatherRelationField=subField.fatherRelationField;//级联的父节点
			newFldObj.childRelationField=subField.childRelationField;//级联的孩子节点
			newFldObj.record_key_id=subField.record_key_id;//子集唯一值
			if(subField.fldType=="M")
				newFldObj.fldInputType=subField.fldInputType;//文本编辑方式
			if (fields!="")
				fields=fields+","+fldname;
			else 
				fields=fldname;
			templateSubset_me._field_list[i]=newFldObj;
		}
		//templateSubset_me._recordColumn=reordFields.split("`");  //recordFields为数据中存储的column
		var fieldarr=fields.split(",");             //fields为模板中存储的column
		templateSubset_me._col=fieldarr.length;//列数
		templateSubset_me._column=fieldarr;//指标列的字符串数组
	    templateSubset_me._removedRecords=[];//已经删除的数据 wangrd 2015-04-08
	    for(var i=0;i<templateSubset_me._col;i++)
	    {
	    	/*
			var indexname="_"+templateSubset_me._column[i].toUpperCase();
			if(!(typeof(g_fm[indexname])=="undefined"||g_fm[indexname]==null))
				  this._field_list[i]=g_fm[indexname];
			else
				  this._field_list[i]=null; 
			*/	  
		}   
	},
	/*
	 * 使用ext显示表格
	 */
	showViewByExt:function() {
		var templateSubset_me = this;
		var bCanEdit=false; //表格可编辑
		var bCanSelect=false;//显示选择框
		if(templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2') {//变化后的可编辑的
			bCanSelect=true;
		}		
		if(templateSubset_me._chgState=="2") {//非变化前子集即可编辑 无子集权限的不走此方法，由指标权限控制单元格是否可编辑
			bCanEdit=true;
		}
		
		//表格列头
		var tableColumns=templateSubset_me.getSubSetTableColumns();
		//表格内容
		var storeData=templateSubset_me.getSubSetTableData();
		//表格数据列
		var dataFields=templateSubset_me.getSubSetFields();	
		//工具栏
		var toolBar=templateSubset_me.getSubSetToolBars();
		/*var toolbar1=Ext.getCmp(templateSubset_me.uniqueId+"_"+templateSubset_me.isShowWin + "_toolbar");
		if (toolbar1){
			toolbar1.setHeight(48);
		}*/
		var pre = templateSubset_me.uniqueId+"_"+templateSubset_me.isShowWin;
		//创建一个新的表格，在弹出窗口中显示 liuzy 20151021
		var configs={
		        prefix:pre,
		    	pagesize:1000,
		    	tdMaxHeight:-1,//iE8 多点几个人报错 bug9001
		    	editable:bCanEdit,
		    	lockable:true,
		    	selectable:bCanSelect,
		    	//customtools:templateSubset_me.isShowWin=="1"?toolBar:undefined,
		    	datafields:dataFields,
		    	beforeBuildComp:function(config){ 
			    	   var tablename = templateSubset_me._field_name;
					   config.tableConfig.viewConfig={
					   		templateSubset_id:templateSubset_me.templateSubset_id,
					   		tablename:tablename,
                            tabid:templateSubset_me.tabid,
                            getRowClass:function(record,rowIndex,rowParams,store){
                            	/*if(record.get("hisEdit")=='0'&&templateSubset_me._chgState=="2"){//改变行颜色
                            		//return 'x-grid-row-selected-gray';
                            	}else
                    				return 'x-grid-row-selected-white';*/
                            }
                       };
					   config.tableConfig.viewConfig.plugins={
							ptype: 'gridviewdragdrop',
							dragText: common.label.DragDropData
				        };
				        config.tableConfig.viewConfig.listeners={
				        	drop: templateSubset_me.dropOK
					    };
					    config.tableConfig.selModel={
							selType:'checkboxmodel',
							renderer:function(value,metaData,record){//渲染每行是否显示多选框
									var hisEdit = record.data.hisEdit;
									var canEdit = record.data.canEdit;
									if((hisEdit=="0"&&templateSubset_me.allow_del_his=="0")||templateSubset_me._rwPriv=="1"){//只读或者有读权限的
										metaData.tdAttr = 'bgcolor=#F3F3F3';
										return "";
									}else{
										if(canEdit=="true")
										{
										    //liuyz bug25354
											/*if(templateSubset_me.onlyHistoryReord)
											{
												templateSubset_me.onlyHistoryReord=false;
												if(Ext.getCmp("deleteButton"+templateSubset_me.id))
													Ext.getCmp("deleteButton"+templateSubset_me.id).setDisabled(templateSubset_me.onlyHistoryReord);
											}*/
											metaData.tdAttr = 'bgcolor=#FFFFFF';
											return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="button" tabIndex="0">&#160;</div>';
										}
										else{
											metaData.tdAttr = 'bgcolor=#F3F3F3';
										    return "";
										}
									}
								}
						};
					    config.tableConfig.enableColumnMove = false;//禁止列拖拽
				},
		    	storedata:storeData,
		    	forceFit:true,    //定义forceFit为自适应宽度，填充整个页面
		    	toolPosition:"bottom",
		    	tablecolumns:tableColumns
		};
		
		var tablegrid=new BuildTableObj(configs);
		templateSubset_me.subtableObj=tablegrid;
		var tableComp = tablegrid.getMainPanel();
		//添加事件
		if(templateSubset_me._chgState=='2'){
			tablegrid.tablePanel.findPlugin('cellediting').on("edit",function(edit,e){
				templateSubset_me.isEditChange=true;
				var column = e.column;
				var record=e.record;
				var index = templateSubset_me._column.indexOf(column.dataIndex);	
				var fmobj=templateSubset_me._field_list[index];
				if(fmobj.fldType=='A'&&(fmobj.codesetId==''||fmobj.codesetId=='0')){//bug 44387 用户输入`号与分割子集数据标识冲突转换为全角
					record.set(column.dataIndex,e.value.replace("`","｀"));
					e.value=e.value.replace("`","｀");
				}
				
				if(e.value!=e.originalValue)
				{
					record.set("isHaveChange","true");//标记客户编辑了此条子集记录
					//liuyz 卡片状态才判断是否更改提示保存成功
					if(templateSubset_me.view_type=='card')
					{
						if(!templateCard_me.isHaveChange)
				    	{
				    		templateCard_me.isHaveChange=true;
				    	}	
					}
		    	}	
				if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
					templateSubset_me.isShowWinCellChange=true;
				}
				if(fmobj.codesetId!=''&&fmobj.codesetId!='0'&&e.value!=e.originalValue){
					var childRelationFieldStr=fmobj.childRelationField;
					if(childRelationFieldStr!=undefined&&childRelationFieldStr.length>0){
						var childRelationFieldList=childRelationFieldStr.split(",");
						for(var index=0;index<childRelationFieldList.length;index++){
							templateSubset_me.clearValue(childRelationFieldList[index],record,tablegrid.tablePanel);
						}
					}
					var map = new HashMap();
					map.put('codesetid',fmobj.codesetId);
					map.put('itemid',e.value.substring(0,e.value.indexOf('`')));
					map.put('searchlevel',"");
          
					Rpc({functionId:'MB00004004',async:false,success:function(form){
						var result = Ext.decode(form.responseText);
						var returnlist = result.returnlist;
						var isHasFather=true;
						var fatherRelationFieldStr=fmobj.fatherRelationField;
						Ext.each(returnlist,function(e){
							if(e.codesetid!=undefined&&e.codesetid!=''&&e.codesetid!='0'){
								if(fatherRelationFieldStr!=undefined&&fatherRelationFieldStr.length>0&&isHasFather){
									var columns= tablegrid.tablePanel.query('gridcolumn[dataIndex='+fatherRelationFieldStr+']');
									if(columns.length>0){
										column=columns[0];
									}else{
										column=null;
										isHasFather=false;
									}
								}
								if(column){
									var index = templateSubset_me._column.indexOf(column.dataIndex);	
									var fmobj=templateSubset_me._field_list[index];
									if(fmobj){
										if(fmobj.codesetId.toUpperCase()=='UN'&&e.codesetid=='UN'||fmobj.codesetId.toUpperCase()=='UM'&&e.codesetid=='UM'||fmobj.codesetId.toUpperCase()=='UM'&&e.codesetid=='UN'||fmobj.codesetId.toUpperCase()=='@K'&&e.codesetid=='@K'||(fmobj.codesetId!=''&&fmobj.codesetId!=undefined&&fmobj.codesetId.toUpperCase()!='UM'&&fmobj.codesetId.toUpperCase()!='UN'&&fmobj.codesetId.toUpperCase()!='@K')){
											record.set(fatherRelationFieldStr,e.codeitemid+'`'+e.codeitemdesc);
											fatherRelationFieldStr=fmobj.fatherRelationField;
										}
									}
								}
							}
						});
					},scope:this},map);
				}
					e.record.commit();
		   		if(templateSubset_me.isShowWin == "0")//不是弹出窗口
		   			templateSubset_me.dataChange();
			});
			tablegrid.tablePanel.on('beforeedit', function(editor, context, eOpts) {
				var record = context.record;
				var field = context.field;
				var hisEdit = record.data.hisEdit;
				var canEdit = record.data.canEdit;
				templateSubset_me.addindexof();
				var index = templateSubset_me._column.indexOf(field);	
				var fmobj=templateSubset_me._field_list[index];
				if(hisEdit=="0"){//只读
			    	if(fmobj){
			    		var his_readonly = fmobj.his_readonly;
			    		if(his_readonly=="false"){
			    			if((fmobj.fldType=='A'||fmobj.fldType=='M')&&(fmobj.codesetId =='' || fmobj.codesetId == '0')&&fmobj.imppeople=="true"){
								if(templateMain_me.templPropety.isValidOnlyname==undefined||templateMain_me.templPropety.isValidOnlyname=='false'){
									Ext.showAlert("请设置并且启用唯一性指标！");
									return false;
								}
								var defaultSelectedPerson=new Array();
								if(record.data[field]!=undefined&&record.data[field]!=''){
									var hashvo = new HashMap();
									   hashvo.put("ids",record.data[field]);
									   hashvo.put("tabid",templateMain_me.templPropety.tab_id);
									Rpc( {functionId : 'MB00002030',async:false,success:function(form,action){//
										   var result = Ext.decode(form.responseText);	
										   if(!result.resultValue.succeed){
											   Ext.showAlert(result.resultValue.Msg);
											   return false;
										   }else{
											   defaultSelectedPerson=result.resultValue.value;
										   }
										}}, hashvo);
								}
								var temIsPrivExpression="";
								var isPrivExpression=false;
								var filter_factor="";
								var orgId="";
								var f = document.getElementById("getHandTemp");
								var p = new PersonPicker({
									addunit:false, //是否可以添加单位
									adddepartment:false, //是否可以添加部门
									multiple: true,//为true可以多选
									orgid:orgId,
									isPrivExpression:isPrivExpression,//是否启用人员范围（含高级条件）
									extend_str:"template/"+templateMain_me.templPropety.tab_id,
									validateSsLOGIN:false,//是否启用认证库
									selectByNbase:true,//是否按不同人员库显示
									deprecate :  '',//不显示的人员
									defaultSelected:defaultSelectedPerson,
									text: "确定",
									callback: function (c) {
										var staffids = "";
										var errerMsg="";
										for (var i = 0; i < c.length; i++) {
											if(c[i].onlyName==undefined||c[i].onlyName==''){
												if(errerMsg.length>0){
													errerMsg+="、";
												}
												errerMsg+=c[i].name;
											}else{
												staffids += c[i].name + ":"+c[i].onlyName+"、";
											}
											
										}
										record.data[field]=staffids.substring(0,staffids.length-1);
										templateSubset_me.isEditChange=true;
										if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
											templateSubset_me.isShowWinCellChange=true;
										}
										record.commit();
										if(templateSubset_me.isShowWin == "0")//不是弹出窗口
											templateSubset_me.dataChange();
										if(errerMsg.length>0){
											Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
										}
									}
								}, f);
								p.open();
								return false;
							}else if(fmobj.fldType=='A'&&fmobj.codesetId !='' && fmobj.codesetId != '0'){
								var column = context.column;
								if(fmobj.fatherRelationField!=undefined&&fmobj.fatherRelationField!=''){
									var columns = tablegrid.tablePanel.query('gridcolumn[dataIndex='+fmobj.fatherRelationField+']');
									if(columns.length>0){
										var value = record.get(fmobj.fatherRelationField);
										if(value!=''&&value!='`'&&value!=undefined){
											column.getEditor().parentid=value.split('`')[0];
										}
									}
								}
							}else if(fmobj.fldType=="A" || fmobj.fldType=="M"){//文本型校验，汉字算2个长度 lis 20160909
								var column = context.column;
								var fldLength = parseInt(fmobj.fldLength);
								if(fldLength!=0&&fldLength!=10){
									column.getEditor().validator = function (val) {
				   						var me = this;
				   						var maxLength = fldLength;
				   						if(fmobj.fldType=="M"){
				   							val = me.value;
				   						}
				   						var length = 0;
				   						if(val.length>0){
										    var a = val.split(""); 
										    for (var i=0;i<a.length;i++){ 
										        if (a[i].charCodeAt(0)<299) {
										        	length++; 
										        } else { 
										        	length+=2; 
										        } 
										    }//计算长度，汉字算2个长度
				   						}
								        errMsg = "该输入项的最大长度是 " + maxLength + " 个字符";
								        return (length <= maxLength) ? true : errMsg;
									}
								}
				    		}else{
								return true;
							}
			    		}
			    		else
			    			return false;
			    	}else
			    		return false;
				}else{
					if(canEdit=="true"){
						if(fmobj){
							if((fmobj.fldType=='A'||fmobj.fldType=='M')&&(fmobj.codesetId =='' || fmobj.codesetId == '0')&&fmobj.imppeople=="true"){
								if(templateMain_me.templPropety.isValidOnlyname==undefined||templateMain_me.templPropety.isValidOnlyname=='false'){
									Ext.showAlert("请设置并且启用唯一性指标！")
									return false;
								}
								var defaultSelectedPerson=new Array();
								if(record.data[field]!=undefined&&record.data[field]!=''){
									var hashvo = new HashMap();
									   hashvo.put("ids",record.data[field]);
									   hashvo.put("tabid",templateMain_me.templPropety.tab_id);
									Rpc( {functionId : 'MB00002030',async:false,success:function(form,action){//
										   var result = Ext.decode(form.responseText);	
										   if(!result.resultValue.succeed){
											   Ext.showAlert(result.resultValue.Msg);
											   return false;
										   }else{
											   defaultSelectedPerson=result.resultValue.value;
										   }
										}}, hashvo);
								}
								var temIsPrivExpression="";
								var isPrivExpression=false;
								var filter_factor="";
								var orgId="";
								/*if(templateMain_me.templPropety.orgId)//bug 43518 启用选人组件不应控制范围
									orgId=templateMain_me.templPropety.orgId;
								else
									orgId="";
								if(templateMain_me.templPropety.filter_by_factor==1)
								{
									temIsPrivExpression=templateMain_me.templPropety.isPrivExpression;
									filter_factor=templateMain_me.templPropety.filter_factor;
								}
								if(temIsPrivExpression!=null&&typeof(temIsPrivExpression)!='underfined'&&!temIsPrivExpression)////是否启用人员范围
								{
									isPrivExpression=temIsPrivExpression;
									orgId='';
								}*/
								var f = document.getElementById("getHandTemp");
								var p = new PersonPicker({
									addunit:false, //是否可以添加单位
									adddepartment:false, //是否可以添加部门
									multiple: true,//为true可以多选
									orgid:orgId,
									isPrivExpression:isPrivExpression,//是否启用人员范围（含高级条件）
									extend_str:"template/"+templateMain_me.templPropety.tab_id,
									validateSsLOGIN:false,//是否启用认证库
									selectByNbase:true,//是否按不同人员库显示
									deprecate :  '',//不显示的人员
									defaultSelected:defaultSelectedPerson,
									text: "确定",
									callback: function (c) {
										var staffids = "";
										var errerMsg="";
										for (var i = 0; i < c.length; i++) {
											if(c[i].onlyName==undefined||c[i].onlyName==''){
												if(errerMsg.length>0){
													errerMsg+="、";
												}
												errerMsg+=c[i].name;
											}else{
												staffids += c[i].name + ":"+c[i].onlyName+"、";
											}
											
										}
										record.data[field]=staffids.substring(0,staffids.length-1);
										templateSubset_me.isEditChange=true;
										if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
											templateSubset_me.isShowWinCellChange=true;
										}
										record.commit();
										if(templateSubset_me.isShowWin == "0")//不是弹出窗口
											templateSubset_me.dataChange();
										if(errerMsg.length>0){
											Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
										}
									}
								}, f);
								p.open();
								return false;
							}else if(fmobj.fldType=='A'&&fmobj.codesetId !='' && fmobj.codesetId != '0'){
								var column = context.column;
								if(fmobj.fatherRelationField!=undefined&&fmobj.fatherRelationField!=''){
									var columns = tablegrid.tablePanel.query('gridcolumn[dataIndex='+fmobj.fatherRelationField+']');
									if(columns.length>0){
										var value = record.get(fmobj.fatherRelationField);
										if(value!=''&&value!='`'&&value!=undefined){
											column.getEditor().parentid=value.split('`')[0];
										}
									}
								}
							}else if(fmobj.fldType=="A" || fmobj.fldType=="M"){//文本型校验，汉字算2个长度 lis 20160909
								var column = context.column;
								var fldLength = parseInt(fmobj.fldLength);
								if(fldLength!=0&&fldLength!=10){
									column.getEditor().validator = function (val) {
				   						var me = this;
				   						var maxLength = fldLength;
				   						if(fmobj.fldType=="M"){
				   							val = me.value;
				   						}
				   						var length = 0;
				   						if(val.length>0){
										    var a = val.split(""); 
										    for (var i=0;i<a.length;i++){ 
										        if (a[i].charCodeAt(0)<299) {
										        	length++; 
										        } else { 
										        	length+=2; 
										        } 
										    }//计算长度，汉字算2个长度
				   						}
								        errMsg = "该输入项的最大长度是 " + maxLength + " 个字符";
								        return (length <= maxLength) ? true : errMsg;
									}
								}
							}
							else{
								return true;
							}
						}else{
							return true;
						}
					}
					else
					    return false;
				}
			});
		}
		
		if(templateSubset_me.isShowWin == "1"){
		    var toolbar = Ext.widget('toolbar',{
		    	dock: 'top',
		    	items:toolBar
		    });
			var win1=Ext.widget("window",{
				id:templateSubset_me.uniqueId + 'subwin',    //使用id属性唯一标示一个组件，但往往会引起错误，尽量少用
				title:this.templPropety.title,  
				height:document.documentElement.clientHeight*3/4>550?550:document.documentElement.clientHeight*3/4,
				width:document.documentElement.clientWidth*2/3>950?950:document.documentElement.clientWidth*2/3,
				layout:'fit',             //当窗口只有一个元素是，使用fit布局将整个窗口填充满
				modal:true,               //遮罩效果
				maximizable:true,            
				closeAction:'destroy',    //当窗口关闭时，hide为隐藏窗口，destroy为销毁窗口  
				dockedItems: toolbar,
				bodyBorder:false,
				items: [tableComp],
				listeners:{
					//liuyz 关闭前如果还需要保存，就不让窗口关闭，调用保存方法，保存后会调用关闭。否则就关闭窗口。
					beforeclose:function(){
						//if(templateSubset_me.view_type == "list"){
					    //	templateSubset_me.saveflag="false";
					    //}
					    templateSubset_me.subtableObj.tablePanel.getView().focusRow(0);//liuyz防止用户编辑了子集但是没有让编辑框失去焦点，数据不能保存，导致子集数据丢失。
						if(templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2'&&templateSubset_me.saveflag=="true")
						{
						    templateSubset_me.closeFlag=true;
						    templateSubset_me.save_Subset_Record('1',false);
							return false;
						}
						else
						{
							if(templateSubset_me._chgState=='1'&&templateSubset_me.saveflag=="true"){
								templateSubset_me.closeFlag=true;
							    templateSubset_me.save_Subset_Record('1',false);
								return false;	
							}else
								return true;
						}
					},
					close:function(){
						templateSubset_me.isShowWin = "0";
						var display = Ext.getCmp('display');
						if(display){
							display.destroy();
						}
					}
				}
			});
		    win1.show(); 
		}else{
			//放置表格的panel
			var p = Ext.getCmp(templateSubset_me.uniqueId + "subset");
			if(p){
				//偶尔出现p的destroy方法丢失,暂时原因未知
				try{
					p.destroy();
				}catch(e){
				}
			}
			var subpanel = Ext.widget('panel', {
			    id:templateSubset_me.uniqueId + 'subset',
				buttonAlign : 'center',
				autoScroll:true,
				width:'100%',
				height:'100%',
				dockedItems: toolBar,
			    //bodyPadding: 1,
			    border:0,
			    bodyBorder:false,
			//    forceFit:true,
			    layout:'fit',
			    listeners:{
					afterrender:function(panel,e){
							if(toolBar){
								var toolbar = undefined;
								if(templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2')//变化后的可编辑的
									toolbar = panel.getComponent('toobarId_2');
								else
									toolbar = panel.getComponent('toobarId_1');
								panel.getEl().on("mouseenter",function(){//鼠标进入工具栏时显示
									var height_un = parseInt(document.getElementById(templateSubset_me.uniqueId).style.height+0);
									if(height_un>120){
										toolbar.show();
									}
								});
								/*panel.getEl().on("mouseleave",function(){//鼠标离开工具栏时隐藏
									toolbar.hide();
								})*/
							}
					},
					dblclick: {
            			element: 'body', //bind to the underlying body property on the panel
			            fn: function(){ 
			           		 //放大
			            	templateSubset_me.enlarge();
			            }
			        }
				},
			    items:[tableComp]
			});
			
			//添加提示信息
			tableComp.on("render",function(view){  
               Ext.create('Ext.tip.ToolTip',{  
                    target:tableComp.getEl(),  
                   //trackMouse:true,  
					bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					html: MB.MSG.dbClickShowSub//'请双击查看编辑子集'
                });  
            });  
			tablegrid.tablePanel.on('rowclick',function(view,record,el,index){
				var grid= tablegrid.tablePanel;
				var lockedGrid = null;
				var lockedView = null;
				var normalView = grid.getView();
				var lockedRowNode = null;
				var normalRowNode = null;
				if(grid.enableLocking){//如果当前grid是锁列的 
					lockedGrid = grid.lockedGrid;//锁列所在的grid
					lockedView =  lockedGrid.getView();
					templateSubset_me.selecRowCss(lockedView,index,grid.getStore().getCount());
				}
				templateSubset_me.selecRowCss(normalView,index,grid.getStore().getCount());
			},templateSubset_me);
			subpanel.render(templateSubset_me.templPropety.uniqueId);
		}
		
		//数据变化触发事件
		this.store =tablegrid.tablePanel.getStore();
		/*gridStore.on("datachanged","subSetDataChanged",this,{});
		gridStore.on("update","subSetUpdate",this,{});	*/
	}, 
	selecRowCss:function(view,index,storeCount){
		//循环清除其他选中样式
		for(var i=0;i<storeCount;i++){
			var node = view.getNode(i);
			if(node!=null){
				var rowCss = node.rows[0].className;
				if(rowCss.indexOf('x-grid-row-selected-itemclick') >= 0){
					//是选中行，则清除选中样式
					view.removeRowCls(i,'x-grid-row-selected-itemclick');
				}
			}
		};
		//去除掉鼠标进入和本身的样式
		view.removeRowCls(index,'x-grid-row-selected-mouseenter');
		//添加选中样式
		view.addRowCls(index,'x-grid-row-selected-itemclick');
	},
	/*
	 * 获取表格显示列
	 */
	getSubSetTableColumns:function() {

		var templateSubset_me = this;
		var tableColumns=[];
		//如果显示子集序号，增加序号列在第一列。
		if(templateSubset_me.isNeedSubsetNo){
			var columnObj={
	   				xtype:"rownumberer",
	   				text:"序号",
	   	   			align:"center",
	   	   			width:50,
	   	   		    dataIndex:-1,
		   	   		menuDisabled:true,
		   	   		sortable:false,//bug22075
			   	   	renderer:function(value,metadata,record,rowIndex){   
			   	       return  rowIndex+1;
			   	   }
	   		};
			tableColumns.push(columnObj);
		}
		for(var i=0;i<templateSubset_me._col;i++)
	    {	
	    	var fmobj=templateSubset_me._field_list[i];
	    	if(fmobj){
		   		var currentPriv=fmobj.fldPriv;
		   		var currentWidth=fmobj.fldWidth;
		   		var his_readonly = fmobj.his_readonly;
		   		currentWidth=parseInt(currentWidth);
		   		var currentTitle="";
		   		if (templateSubset_me._field_list.length==templateSubset_me._col){
		   			if(fmobj.need == "true")//如果是必填则显示红星 lis 20160809
		   		  		currentTitle=fmobj.flddesc+'<font face="仿宋" style="margin:0 0 0 2" color="red">*</font>';
		   		  	else
		   		  		currentTitle=fmobj.flddesc;
		   		}
		   		if(currentPriv=="0"){// 指标无权限 不显示
		   			continue;
		   		}
		   		var strformat="";
		   		var strType="";
		   		var strEditType="";
		   		var strCodeSetid ="";
		   		var strAlign="left";
		   		var maxlength=100;
		   		var ctrltype='0';
		   		if (fmobj){
		   			var align = fmobj.align;
	   				if(align == 2)
	   					strAlign="right";
					else if(align == 0)
	   					strAlign="left";
					else
	   					strAlign="center";
		   			if(fmobj.fldType=="D")
		   			{
		   				//strType="datecolumn";  
		   				strType="gridcolumn"; 
		   				strEditType="datetimefield";
		   				strformat="Y.m.d";
		   				//maxlength=fmobj.fldLength;
		   			}
		   			else if(fmobj.fldType=="N"){
		   				strType="numbercolumn";
		   				strEditType="numberfield"
		   				maxlength=fmobj.fldLength+fmobj.fldDecLength+1;
		   			}
		   			else if(fmobj.fldType=="M"){
		   				strType="bigtextcolumn";
		   				strEditType="bigtextfield";   				
		   				maxlength=1000000;
		   				if (fmobj.fldLength!=0 && fmobj.fldLength!=10 ){//设置备注长度大于50才控制。
		   					//maxlength=fmobj.fldLength;
		   				}
		   			}
		   			else {
		   				strType="";// "textfield"
		   				if (fmobj.codesetId!="0"){
		   					strType="codecolumn";
		   					strCodeSetid=fmobj.codesetId;
		   					strEditType="codecomboxfield";// 树 列表方式
		   					maxlength=100;
		   				}
		   				else {
		   					maxlength=fmobj.fldLength;
		   				}
		   			}
		   		};
		   		
		   		var columnObj={
		   				xtype:strType,
		   				text:currentTitle,
		   	   			width:currentWidth,
		   	   			dataIndex:templateSubset_me._column[i],
		   	   			editablevalidfunc:null,
		   	   			format:strformat,   		
		   	   			align:strAlign,
			   	   		menuDisabled:true,
			   	   		sortable:false//bug22075
		   		};
		   		if(fmobj.fldType=="D"){//日期显示格式与后台模板设置一致 lis 20160718
		   			columnObj.renderer = function(value, metaData, record, rowIndex, colIndex, store){
		   				var fmobj=templateSubset_me._field_list[colIndex];
				    	if(fmobj){
				    		var his_readonly = fmobj.his_readonly;
				    		if(fmobj.his_readonly=='false'||record.data.I9999==-1)
			   					metaData.tdAttr = 'bgcolor=#FFFFFF';
				    		else
				    			metaData.tdAttr = 'bgcolor=#F3F3F3';
				    		if(templateMain_me.templPropety.isAutoLog){
								if(templateSubset_me.chgInfoList&&templateSubset_me.chgInfoList.length>0){
									var record_key_id=record.get("record_key_id");
									var keyvalue=record_key_id+":"+fmobj.fldName+";";
									if(templateSubset_me.chgInfoList.indexOf(keyvalue)>-1)
										metaData.style += ' color:'+templateMain_me.templPropety.autoLogColor+';';
								}
							}
				    	}
		   				if(value){
		   					var headerCt = this.getHeaderContainer();
	        				var column = headerCt.getHeaderAtIndex(colIndex);
		   					var index = column.dataIndex+"_D";
		   					var format = record.get(index);
		   					value = value.replace(/\./g,'-');
							var date= parseTemplateDate(value);
							value = templateSubset_me.formatSubSetDate(date,format);
		   				}
		   				return value;
		   			}
		   		}else if(fmobj.fldType=="N"){//对数值型小数点位数设置 lis 20160728
		   			var decimalWidth = ""
		   			if(fmobj.fldDecLength){
		   				for(var j = 0; j < fmobj.fldDecLength; j++){
		   					decimalWidth += "0";
		   				}
		   			}
		   			var format = "0";
		   			if(decimalWidth != "")
		   				format += "." + decimalWidth
		   			columnObj.format = format;
		   			columnObj.renderer=function(value, metaData, record,rowIndex,colIndex,store,tableView){
		   				var fmobj=templateSubset_me._field_list[colIndex];
				    	if(fmobj){
				    		var his_readonly = fmobj.his_readonly;
				    		if(fmobj.his_readonly=='false'||record.data.I9999==-1)
			   					metaData.tdAttr = 'bgcolor=#FFFFFF';
				    		else
				    			metaData.tdAttr = 'bgcolor=#F3F3F3';
				    		if(templateMain_me.templPropety.isAutoLog){
								if(templateSubset_me.chgInfoList&&templateSubset_me.chgInfoList.length>0){
									var record_key_id=record.get("record_key_id");
									var keyvalue=record_key_id+":"+fmobj.fldName+";";
									if(templateSubset_me.chgInfoList.indexOf(keyvalue)>-1)
										metaData.style += ' color:'+templateMain_me.templPropety.autoLogColor+';';
								}
							}
				    	}
				    	if(value!=''){
				    		value = value.toString();
				    		var decimalWidth = fmobj.format<fmobj.fldDecLength?fmobj.format:fmobj.fldDecLength;
					    	var pos_decimal = value.indexOf('.');
					    	if (pos_decimal < 0) {
					    		pos_decimal = value.length;
					    		if(decimalWidth>0){
					    			value += '.';
					    		}
					    	}
					    	if(decimalWidth>0){
						    	while (value.length <= pos_decimal + parseInt(decimalWidth)) {
						    		value += '0';
						    	}
					    	}
				    	}
		   				return value;
		   			}
		   		}
		   		else if(fmobj.fldName=='attach')//附件
		   		{
		   			
		   			Ext.each(Ext.ComponentQuery.query('container[id^=_panelWindowId]'),function(e){
	        	    	e.destroy();
	        	    });
		   			columnObj.align = 'center';
		   		    columnObj.renderer=function(value, metaData, record,rowIndex,colIndex,store,tableView){
		   		    	var fmobj=templateSubset_me._field_list[colIndex];
				    	if(fmobj){
				    		var his_readonly = fmobj.his_readonly;
				    		if(fmobj.his_readonly=='false'||record.data.I9999==-1)
			   					metaData.tdAttr = 'bgcolor=#FFFFFF';
				    		else
				    			metaData.tdAttr = 'bgcolor=#F3F3F3';
				    	}

		   				var index=metaData.recordIndex;
		   				var hisEdit = record.data.hisEdit;
		   				var id = templateSubset_me.templateSubset_id+rowIndex+colIndex;
		   				var html ="";
		   				//28452 linbz 增加校验hisEdit不等于0，即行记录为可编辑状态才显示上传附件按钮
		   				if ((templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2') && (hisEdit!='0'||his_readonly=="false")){
			   				html +='<a  title="上传附件" style="margin-right:5px;" href="javascript:Ext.getCmp(\''+templateSubset_me.templateSubset_id+'\').showfiles('+index+',\''+his_readonly+'\');"><img src="'+rootPath+'/images/new_module/upload.png" border=0></a>';
		   				}
		   				var imgPath = "/images/new_module/grayfile.png";
		   				if(value)
		   					imgPath = "/images/file.png";
		   				html +='<a title="查看附件" id="'+id+'" href="javascript:Ext.getCmp(\''+templateSubset_me.templateSubset_id+'\').showUploadFiles(\''+id+'\','+index+');"><img src="'+rootPath+imgPath+'" border=0></a>';
		   				return html;
		   			 };
		   		}else{
		   			if (templateSubset_me._chgState=='2'){//变化后的才添加此renderer
			   			columnObj.renderer=function(value, metaData, record,rowIndex,colIndex,store,tableView){	
					    	var fmobj=templateSubset_me._field_list[colIndex];
					    	if(fmobj){
					    		var his_readonly = fmobj.his_readonly;
					    		if(fmobj.his_readonly=='false'||record.data.I9999==-1)
				   					metaData.tdAttr = 'bgcolor=#FFFFFF';
					    		else
					    			metaData.tdAttr = 'bgcolor=#F3F3F3';
					    		if(templateMain_me.templPropety.isAutoLog){
									if(templateSubset_me.chgInfoList&&templateSubset_me.chgInfoList.length>0){
										var record_key_id=record.get("record_key_id");
										var keyvalue=record_key_id+":"+fmobj.fldName+";";
										if(templateSubset_me.chgInfoList.indexOf(keyvalue)>-1)
											metaData.style += ' color:'+templateMain_me.templPropety.autoLogColor+';';
									}
								}
					    	}
					    	if(fmobj.fldType=="A"&&(fmobj.codesetId!="0"&&fmobj.codesetId!=""&&fmobj.codesetId!=null)){
					    		var valuearr = value.split("`");
					    		if(valuearr.length>=2)
					    			value = valuearr[1];
					    		else
					    			value='';
					    	}
			   				return value;
			   			}
		   			}
		   		}
		   		if (templateSubset_me._chgState=='2'&&currentPriv=="2"){ 
		   			var editorObj=null;
		   			if(fmobj.fldType=="N")
		   			{ 
		   				editorObj={
			   					xtype:strEditType,
			   					maxLength:maxlength,
			   					codesetid:strCodeSetid,
			   					allowBlank:true,
			   					allowDecimals:(fmobj.format<fmobj.fldDecLength?fmobj.format:fmobj.fldDecLength)>0,
			   					decimalPrecision:fmobj.format<fmobj.fldDecLength?fmobj.format:fmobj.fldDecLength,//如果子集设置的位数少于指标的位数，以子集设置为准。如果子集设置位数大于指标位数，以指标位数为准。
			   					maxValue:null,
			   					format:strformat,
			   					validator:null
			   			};
		   			}
		   			else
		   			{ 
			   			 editorObj={
			   					xtype:strEditType,
			   					maxLength:maxlength,
			   					codesetid:strCodeSetid,
			   					allowBlank:true,
			   					maxValue:null,
			   					format:strformat,
			   					validator:null
			   			 };
		   			}
		   			if(fmobj.fldType=="A" || fmobj.fldType=="M"){//文本型校验，汉字算2个长度 lis 20160909
						if(fmobj.codesetId=="UN"||fmobj.codesetId=="UM"||fmobj.codesetId=="@K"){
							 editorObj.ctrltype=ctrltype;
						}
						if(fmobj.fldType=="A"&&fmobj.codesetId!="@K"){
							editorObj.onlySelectCodeset=false;
						}
		   			}
		   			if(fmobj.fldName!='attach'){
		   			    columnObj.editor=editorObj;
		   			}
		   		}
		   		tableColumns.push(columnObj);
	    	}
	    }
		return tableColumns;
	},
	/*
	 * 获取表格显示data
	 */
	getSubSetTableData:function() {
		var templateSubset_me = this;
		var storeData=[];
	    for (var i=0; i<templateSubset_me._recNodes.length; i++)
		{
			var record={};
	    	var recNode = templateSubset_me._recNodes[i];
			var keyid = recNode.I9999;		
			var state = recNode.state;
			var edit = recNode.edit;
			var timestamp = recNode.timestamp;
			var record_key_id  = recNode.record_key_id;
			var isHaveChange  = recNode.isHaveChange;
			if (state==null) state="";		
			
			record.I9999=keyid;
			record.delState=state;
			record.hisEdit = edit;
			record.timestamp = timestamp;
			record.record_key_id = record_key_id;
			record.isHaveChange = isHaveChange;
			var value = recNode.contentValue;
			var valuearr=value.split("`");
			
			if (templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2'/*&&edit!='0'*/){
				record.canEdit="true";
			}
			else {
				record.canEdit="false";
			}
		
			for(var j=0;j<templateSubset_me._column.length;j++)
			{
				record[templateSubset_me._column[j]]="";
			}
			for(var j=0;j<valuearr.length;j++)
			{
				var tmp=valuearr[j];  
				var fmobj=templateSubset_me._field_list[j];
				if (fmobj){   
				  	if(fmobj.codesetId!="0"&&tmp.length>0){
				  		/*var m=tmp.indexOf('||');
				  		valuearr[j]=tmp.substring(m+2);
				  		tmp=tmp.substring(0,m);*/
				  		var val=tmp.split('||');
				  		valuearr[j]=val[1];
				  		tmp=val[0];
				  		tmp=valuearr[j]+"`"+tmp;
				  	}
				  	if(fmobj.fldType == "D"){//日期格式显示与模板中子集设置一致 lis 20160715
						record[templateSubset_me._column[j]]=tmp;
						record[templateSubset_me._column[j]+"_D"]=fmobj.format;
					}else
						record[templateSubset_me._column[j]]=tmp;
					}
					
			}
			if (record.delState=="D"){
				var isIn=false;
				for(var m=0;m<templateSubset_me._removedRecords.length;m++){
					var record_ = templateSubset_me._removedRecords[m];	
					var i9999 = record_["I9999"];
					if(record.I9999!=-1&&record.I9999==i9999)
					{
						isIn=true;
						break;
					}
				}
				if(!isIn)
					templateSubset_me._removedRecords.push(record);
			}else
				storeData.push(record);
		}
		return storeData;
	},
	/*
	 * 获取表格数据列
	 */
	getSubSetFields:function() {
		var templateSubset_me = this;
		var strfields ="'I9999','delState','canEdit','hisEdit'";
		for(var i=0;i<templateSubset_me._col;i++){	
			var fmobj=templateSubset_me._field_list[i];
			if(fmobj){
				if(fmobj.fldType == "D"){
					strfields=strfields+","+"'"+fmobj.fldName+"_D"+"'";
				}
				strfields=strfields+","+"'"+fmobj.fldName+"'";
			}
	    }
		//strfields=strfields.substr(1);
		strfields="["+strfields+"]";
		var dataFields=Ext.decode(strfields);
		return dataFields;
	},
	
	/*
	 * 获取表格工具栏
	 */
	getSubSetToolBars:function() {
		var templateSubset_me = this;
		var toolBarItems = null;
		var handimport = false;
		var task_id = '';
		var module_id = '';
		var infor_type ='';
		var maintoorbar = templateMain_me.tableConfig.customtools;
		if(templateSubset_me.view_type=='list'){
			task_id = templateList_me.templPropety.task_id;
			module_id = templateList_me.templPropety.module_id;
			infor_type = templateList_me.templPropety.infor_type;
		}else{
			task_id = templateCard_me.templPropety.task_id;
			module_id = templateCard_me.templPropety.module_id;
			infor_type = templateCard_me.templPropety.infor_type;
		}
		if(maintoorbar.length>0){
			Ext.each(maintoorbar,function(e){
				if((infor_type=='1'&&e.text=='选人')||(infor_type!='1'&&e.text=='选择')){
					handimport = true;
				}
			});
		}
		if (templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2'){//变化后且可编辑
			toolBarItems=[
					{xtype:"button",text:common.button.newadd,
						listeners: {
				            click: {
				                element: 'el',
				                fn: function(){ templateSubset_me.add_Subset_Record(); }
				            }
						}//新增
					},
					{xtype:"button",text:common.button.newinsert,
						listeners: {
				            click: {
				                element: 'el',
				                fn: function(){ templateSubset_me.ins_Subset_Record(); }
				            }
						}//插入
					},
					{xtype:"button",text:common.button.todelete,
					 //liuyz bug25354
					 id:"deleteButton"+templateSubset_me.id,
					 //disabled:templateSubset_me.onlyHistoryReord&&templateSubset_me.getSubSetTableData().length>0,
						listeners: {
				            click: {
				                element: 'el',
				                fn: function(){ /*if(!templateSubset_me.onlyHistoryReord)*/{templateSubset_me.del_Subset_Record();} }
				            }
						}//删除
					},{xtype:"button",text:common.button.toimport,
						listeners: {
				            click: {
				                element: 'el',
				                fn: function(){
				                	templateSubset_me.excelImport();
				                }
				            }
						}},//导入
					{xtype:"button",text:common.button.toTop,
						id:"toTopButton"+templateSubset_me.id,
						listeners: {
							click: {
								element: 'el',
								fn: function(){ 
									templateSubset_me.moveToTop_Subset_Record();
								}
							}
						}//置顶
					}
			];
			if(templateSubset_me.isShowWin == "1"){//显示弹出窗口
				var saveText = common.button.save//"保存";
				var closeText = common.button.close;//关闭
				toolBarItems.push({xtype:"button",text:saveText,
					listeners: {
			            click: {
			                element: 'el',
			                fn: function(){ 
			                	templateSubset_me.closeFlag=false;
			                	templateSubset_me.save_Subset_Record('0',true); 
			                }
			            }
					}//保存
				});
				//28937刷新
				templateSubset_me.createRefreshButton(module_id,handimport,toolBarItems);
				
				toolBarItems.push({xtype:"button",text:closeText,
					listeners: {
			            click: {
			                element: 'el',
			                fn: function(){ 
			                	if(templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2'&&templateSubset_me.saveflag=="true")
								{
								    templateSubset_me.closeFlag=true;
								    templateSubset_me.save_Subset_Record('1',false);
								    if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
										templateSubset_me.isShowWinCellChange=true;
									}
								}else{
									templateSubset_me.close_Subset_Record();
								}
//			                	templateSubset_me.close_Subset_Record();
			                	}
			            }
					}//关闭
				});
				toolBarItems.push({ xtype: 'tbfill' });
				if(templateSubset_me.remarks!=null&&templateSubset_me.remarks.length>0){
					var remarksButton=templateSubset_me.createRemarskButton(templateSubset_me.remarks);
					toolBarItems.push(remarksButton);
				}
			}else{
				toolBarItems.push({ xtype: 'tbfill' });
				if(templateSubset_me.remarks!=null&&templateSubset_me.remarks.length>0){
					var remarksButton=templateSubset_me.createRemarskButton(templateSubset_me.remarks);
					toolBarItems.push(remarksButton);
				}
				templateSubset_me.createRefreshImg(module_id,handimport,toolBarItems);
				var enlargeImg = Ext.create('Ext.Img', {
					    src: '/images/new_module/enlarge.png',
					    style:'cursor:pointer',
					    margin:'0 5 0 0',
					    listeners: {
					        click: {
					            element: 'el',
					            fn: function(){
									//点击放大按钮时调用回调函数
					            	templateSubset_me.enlarge();
								}
					        }
					    }
					});
				toolBarItems.push(enlargeImg);
			}
			if(templateSubset_me.isShowWin == "0"){//不是弹出窗口
				var toolBar = Ext.widget({
					xtype: 'toolbar',
					dock: 'top',
					hidden:true,
					itemId: 'toobarId_2',
					items:toolBarItems
				});
				toolBarItems = toolBar;
			}
		}else{
			toolBarItems=[];
			if(templateSubset_me.isShowWin == "1"){//显示弹出窗口
				templateSubset_me.createRefreshButton(module_id,handimport,toolBarItems);
				if(templateSubset_me.remarks!=null&&templateSubset_me.remarks.length>0){
					toolBarItems.push({ xtype: 'tbfill' });
					var remarksButton=templateSubset_me.createRemarskButton(templateSubset_me.remarks);
					toolBarItems.push(remarksButton);
				}
			}else{
				toolBarItems.push({ xtype: 'tbfill' });
				var isCreate = templateSubset_me.createRefreshImg(module_id,handimport,toolBarItems);
				if(!isCreate){
					toolBarItems=[];
					toolBarItems.push({ xtype: 'tbfill' });
				}
				var isHaveRemark = false;
				if(templateSubset_me.remarks!=null&&templateSubset_me.remarks.length>0){
					var remarksButton=templateSubset_me.createRemarskButton(templateSubset_me.remarks);
					toolBarItems.push(remarksButton);
					isHaveRemark = true;
				}
				if(!isCreate&&!isHaveRemark)
					toolBarItems=[];
				else{
					var enlargeImg = Ext.create('Ext.Img', {
					    src: '/images/new_module/enlarge.png',
					    style:'cursor:pointer',
					    margin:'0 5 0 0',
					    listeners: {
					        click: {
					            element: 'el',
					            fn: function(){
									//点击放大按钮时调用回调函数
					            	templateSubset_me.enlarge();
								}
					        }
					    }
					});
					toolBarItems.push(enlargeImg);
				}
			}
			if(templateSubset_me.isShowWin == "0"){//不是弹出窗口
				if(toolBarItems.length==0)
					toolBarItems = undefined;
				else{
					var toolBar = Ext.widget({
						xtype: 'toolbar',
						dock: 'top',
						hidden:true,
						itemId: 'toobarId_1',
						items:toolBarItems
					})
					toolBarItems = toolBar;
				}
			}
		}
		return toolBarItems;
	},
	//创建刷新图片
	createRefreshImg:function(module_id,handimport,toolBarItems){
		var templateSubset_me = this;
		var isCreate = false;
		var refreshImg = undefined;
		var confirmMsg = "";
		if (templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2')
			confirmMsg = "此操作是同步信息库的数据到当前子集，其中修改的以及新增的子集记录不会被覆盖，确认刷新吗？";
		else
			confirmMsg = "你希望把档案库的数据同步至当前业务表单吗？";
		refreshImg = Ext.create('Ext.Img', {
			title:common.button.refresh,
		    src: '/images/new_module/refresh.png',
		    style:'cursor:pointer',
		    margin:'0 5 0 0',
		    listeners: {
		        click: {
		            element: 'el',
		            fn: function(){
		            	Ext.showConfirm(confirmMsg, function(btn) {
		                if (btn == 'yes') {
		                   templateSubset_me.refresh();
		                } else {
		                   return;
		                }
	                  }, this);
					}
		        }
		    }
		});
		if(module_id=='9'){//自助
			if(templateMain_me.templPropety.approve_flag=='1'&&templateMain_me.templPropety.tasktype==true){
				toolBarItems.push(refreshImg);
				isCreate = true;
			}
		}else{//业务
			if(templateMain_me.templPropety.approve_flag=='1'&&(handimport||templateMain_me.templPropety.task_id!='0')&&templateMain_me.templPropety.tasktype==true){
				toolBarItems.push(refreshImg);
				isCreate = true;
			}
		}
		return isCreate;
	},
	createRemarskButton:function(remarks){
		templateSubset_me = this;
		var remarksButton ={
			xtype:"label",
			html:"<label style='font:12px/13px 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;color:#416da3;'>填表说明</label>",
			margin:'0 5 0 0',
			listeners: {
				click: {
					element: 'el',
					fn: function(e,m,o){
						isChangeTop=true;
						var width = document.body.clientWidth;
						var actualLeft= m.getBoundingClientRect().left;
						 var elementScrollLeft=0;
						if (document.compatMode == "BackCompat"){
								elementScrollLeft=document.body.scrollLeft;
							} else {
								elementScrollLeft=document.documentElement.scrollLeft; 
							}
						if(templateSubset_me.isShowWin == "0")
							actualLeft = actualLeft+elementScrollLeft-440;
						if(templateSubset_me.isShowWin == "1")
							actualLeft = actualLeft+elementScrollLeft-460;
						var height = document.body.clientHeight;
						var actualTop =m.getBoundingClientRect().top;
						var elementScrollTop=0;
						if (document.compatMode == "BackCompat"){
								elementScrollTop=document.body.scrollTop;
						  }else {
							  	elementScrollTop=document.documentElement.scrollTop; 
						  }
						actualTop = actualTop+elementScrollTop+5;
						actualTop=actualTop+m.offsetHeight;
						var win = Ext.widget('panel',{
							id:'display',
							x:actualLeft,
							y:actualTop,
							layout:'fit',
							border:1,
							floating : true,
							height:220,
							width:500,
							//bodyStyle:'border-color:#416da3;',
							shadow : true,
							closeAction : "destroy",
							listeners:{
								render:function(){
									this.mon(Ext.getDoc(), {
										mousewheel: this.hiddenIf,
										mousedown: this.hiddenIf,
										scope: this
									});
								}
							},
							hiddenIf: function(e) {
								var me = this;
									if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target)) {
										me.destroy();
									}
							}
						})
						var apPanel = Ext.widget('container',{
							margin:'5 15 0 15',
							height:190,
							autoScroll:true,
							html:"<span style='font-size:1.2em;'>"+replaceAll(replaceAll(replaceAll(getDecodeStr(remarks),"\r\n","<br />"),"\r","<br />"),"\n","<br />")+"</span>"
						})
						win.add([{xtype:'panel',width:500,items:[apPanel]}]);
						win.show();
					}
				}
			}
		};
		return remarksButton;
	},
	//创建刷新按钮
	createRefreshButton:function(module_id,handimport,toolBarItems){
		var templateSubset_me = this;
		var confirmMsg = "";
		if (templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2')
			confirmMsg = "此操作是同步信息库的数据到当前子集，其中修改的以及新增的子集记录不会被覆盖，确认刷新吗？";
		else
			confirmMsg = "你希望把档案库的数据同步至当前业务表单吗？";
		if(module_id=='9'){//自助
			if(templateMain_me.templPropety.approve_flag=='1'&&templateMain_me.templPropety.tasktype==true){
				toolBarItems.push({xtype:"button",text:common.button.refresh,
					listeners: {
		            click: {
		                element: 'el',
		                fn: function(){ 
		                	Ext.showConfirm(confirmMsg, function(btn) {
				                if (btn == 'yes') {
				                   templateSubset_me.refresh();
				                } else {
				                   return;
				                }
			                  }, this);
		                }
		            }
					}//刷新
				});
			}
		}else{//业务
			if(templateMain_me.templPropety.approve_flag=='1'&&(handimport||templateMain_me.templPropety.task_id!='0')&&templateMain_me.templPropety.tasktype==true){
				toolBarItems.push({xtype:"button",text:common.button.refresh,
					listeners: {
		            click: {
		                element: 'el',
		                fn: function(){ 
							Ext.showConfirm(confirmMsg, function(btn) {
				                if (btn == 'yes') {
				                   templateSubset_me.refresh();
				                } else {
				                   return;
				                }
			                  }, this);
		                }
		            }
					}//刷新
				});
			}
		}
	},
	//拖拽
	dropOK:function(node,data,overModel,dropPosition,eOpts ) {
		var templateSubset_id= this.templateSubset_id;
		var templateSubset_me = Ext.getCmp(templateSubset_id);
		if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
			templateSubset_me.isShowWinCellChange=true;
		}
		//列表下、变化前子集自动保存
		//if(templateSubset_me.view_type=='list'){
			//if(templateSubset_me._rwPriv=='1'){
		        templateSubset_me.sortstore(templateSubset_me.store);
				templateSubset_me.closeFlag=false;
				templateSubset_me.dataChange();	 
				//templateSubset_me.save_Subset_Record('2');
			//}
		/*}
		else {
			//保存数据
			templateSubset_me.sortstore(templateSubset_me.store);
			templateSubset_me.dataChange();	 
		}*/
	},
	enlarge:function(){
		var templateSubset_me = this;
		//点击放大按钮时调用回调函数
		if(templateSubset_me.templPropety.enlargeFunc)
		{ 
			var uniqueId = templateSubset_me.uniqueId;
			var columnId = templateSubset_me.templPropety.columnName;
			var tabid = templateSubset_me.templPropety.tabid;
			Ext.callback(eval(templateSubset_me.templPropety.enlargeFunc),null,[uniqueId,columnId,tabid]);
		}
	},
	/*
	*刷新子集列表
	*/
	refresh:function(){
		var templateSubset_me = this;
	    templateSubset_me.save_Subset_Record("2",false);
		var a0100 = '';
		var basepre = '';
		var infor_type = '';
		var map = new HashMap();
		if(templateSubset_me.view_type=='card'){
			infor_type = templateCard_me.templPropety.infor_type;
			var record  = templateCard_me.getValueItem(templateSubset_me.templPropety.uniqueId);
			var newRecord={};
			if(record){
				/*var recordData="";
				for (var i=0;i<record.recordSet.fields.length;i++){
					var valueItem =record.recordSet.fields[i];
					if (!valueItem.modified && valueItem.uniqueId==""){
					    newRecord[valueItem.fldName]=valueItem.keyValue;
					}
				}*/
				var objectid = templateCard_me.personListCurRecord.data.objectid_e;
				a0100 = objectid;
				/*if(infor_type=='1'){//人员
					a0100 = objectid.split('`')[1];//newRecord.a0100;
					basepre = objectid.split('`')[0];//newRecord.basepre;
				}else if(infor_type=='2'){//单位
					a0100 = newRecord.b0110;
				}else if(infor_type=='3'){//岗位
					a0100 = newRecord.e01a1;
				}*/
			}
		}else{
			infor_type = templateList_me.templPropety.infor_type;
		    a0100 = templateSubset_me.templPropety.objectid;
		}
    	map.put("tab_id",templateSubset_me.templPropety.tabid);
    	map.put("view_type",templateSubset_me.view_type);
		map.put("id",a0100);
		map.put("basepre",basepre);
		map.put('columnname',templateSubset_me.templPropety.columnName);
		map.put("infor_type",infor_type);
		map.put("module_id",templateMain_me.templPropety.module_id);
		map.put("task_id",templateMain_me.templPropety.task_id);
		map.put("isSysData",false);
	    Rpc({functionId:'MB00004005',async:true,success:templateSubset_me.refreshSuccess,scope:templateSubset_me},map);
	},
	refreshSuccess:function(form,action){
		var templateSubset_me = this;
		var leave=[];
		var i9999arr= [];
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
        	Ext.showAlert(result.message);
         	return;
        }else{
			var subsetlist = result.subsetlist;
			templateSubset_me.store.removeAll();
			var removedStores=templateSubset_me.store.removed;
			for(var m=0;m<removedStores.length;m++){
				var record = removedStores[m].data;
				var i9999 = record.I9999;
				if(i9999!=-1){
					record.refState="S";
				}
			}
			templateSubset_me.store.add(subsetlist);
			templateSubset_me.dataChange();
			templateSubset_me.saveflag='true';
			templateSubset_me.isShowWinCellChange=true;//标记修改了子集内容。
		}
	},
	/*
	 * 新增子集记录
	 */
	add_Subset_Record:function()
	{
		var templateSubset_me = this;
		var index = templateSubset_me.store.getCount();
		var record =templateSubset_me.get_Subset_NewRecord();	
		templateSubset_me.store.insert(index,record);
		//param.subSetView.combineSubSetXml(store);
		
		templateSubset_me.sortstore(templateSubset_me.store);
		templateSubset_me.dataChange();
		/*var model = templateSubset_me.subtableObj.tablePanel.getSelectionModel();
		var newid = record.id;
		var selected = [];
		templateSubset_me.store.each(function(r){
		    if(newid==r.get('id'))
		   		selected.push(r);
		});
		model.select(selected,true);*/
		//暂时只定位到新增的行  不选中
		templateSubset_me.subtableObj.tablePanel.getView().focusRow(index);
		if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
			templateSubset_me.isShowWinCellChange=true;
		}
	},
	
	/*
	 * 插入子集记录
	 */
	ins_Subset_Record:function()
	{
		var templateSubset_me = this;
		//获取当前选中第一条记录，目前点击行即选中
		var tablePanel=templateSubset_me.subtableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		var index=0;
		if(records.length>0){
			var record=records[0];
			index=templateSubset_me.store.indexOf(record);
		}
		
		var record =templateSubset_me.get_Subset_NewRecord();	
		templateSubset_me.store.insert(index,record);
		templateSubset_me.sortstore(templateSubset_me.store);
		templateSubset_me.dataChange();
		templateSubset_me.subtableObj.tablePanel.getView().focusRow(index);//暂时只定位到新增的行  不选中
		if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
			templateSubset_me.isShowWinCellChange=true;
		}
	},
	
	/*
	 * 删除子集记录
	 */
	del_Subset_Record:function()
	{
		var templateSubset_me = this;
		var tablePanel=templateSubset_me.subtableObj.tablePanel;
		var records=[];
		var records_=tablePanel.getSelectionModel().getSelection();
		for(var i=0; i<records_.length; i++){
			var record = records_[i].data;
			var canEdit = record.canEdit;
			var hisEdit = record.hisEdit;
			if((hisEdit=="0"&&templateSubset_me.allow_del_his=="0")||templateSubset_me._rwPriv=="1"){
				continue;
			}else{
				if(canEdit=="true"){
					records.push(records_[i]);
				}
				else{
					continue;
				}
			}
		}
		if(records.length<1){
			Ext.showAlert(common.msg.selectData);
			return;
		}
		Ext.showConfirm(common.msg.isDelete, function(optional){
    		if(optional=='yes'){
    			templateSubset_me.store.remove(records);
    			templateSubset_me.sortstore(templateSubset_me.store);
    			var removedStores=templateSubset_me.store.removed;
	    		for(var k=0; k<records.length; k++){
					var record = records[k].data;
					var I9999 = record.I9999;
					if(I9999!=-1){
						var isIn=false;
						for(var m=0;m<templateSubset_me._removedRecords.length;m++){
							var record_ = templateSubset_me._removedRecords[m];	
							var i9999_ = record_["I9999"];
							if(I9999!=-1&&I9999==i9999_)
							{
								isIn=true;
								break;
							}
						}
						if(!isIn){//子集删除如果在记录在勾选的列表，修改标志。
							record.delState="D";
							record.refState="";
							templateSubset_me._removedRecords.push(record);
						}
						if(record.refState!="S"){
							record.refState="";
							//break;
						}
						
					}else{
						record.delState="D";
					}
				}
    			templateSubset_me.dataChange();
    			if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
    				templateSubset_me.isShowWinCellChange=true;
    			}
    		}
		})
	},
	//置顶
	moveToTop_Subset_Record:function(){
		var templateSubset_me = this;
		var tablePanel=templateSubset_me.subtableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		var storeData=templateSubset_me.getSubSetTableData();
		if(records.length<1){
			Ext.showAlert(common.msg.selectData);
			return;
		}
		records=this.sortOrderByRecords(records,templateSubset_me.store)
		for(var i=0;i<records.length;i++){
			templateSubset_me.store.remove(records[i]);
			templateSubset_me.store.insert(i,records[i]);
		}
		templateSubset_me.sortstore(templateSubset_me.store);
		templateSubset_me.dataChange();
        //templateSubset_me.save_Subset_Record("0");		
		if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
			templateSubset_me.isShowWinCellChange=true;
		}
		
	},
	//重新排序
	sortOrderByRecords:function(records,store)
	{
		for(var num=0;num<records.length-1;num++)
		{
			for(var numY=0;numY<records.length-1-num;numY++)
			{
				var firstIndex=store.findBy(function(record){return record==records[numY];});
				var secIndex=store.findBy(function(record){return record==records[numY+1];});
				if(firstIndex>secIndex)
				{
					var recordTemp=records[numY+1];
					records[numY+1]=records[numY];
					records[numY]=recordTemp;
				}
			}
		}
		return records;
	},
	/*
	 *  对store中的数据进行index赋值 
	 */
	sortstore:function(store)
	{
		var templateSubset_me = this;
		for(var i=0;i<store.getCount();i++){
	       var rec = store.getAt(i);
	       var attach = rec.get('attach');
	       rec.set('index',i);
	    }
	},
	
	/*
	 * 关闭子集显示 
	 */
	close_Subset_Record:function()
	{
		var templateSubset_me = this;
		var subwinId=templateSubset_me.uniqueId + "subwin";  
		var subwin=Ext.getCmp(subwinId);
		subwin.close(); 
	},
	
	//数据改变时回调函数 lis 20160506
	dataChange:function()
	{ 
		var templateSubset_me = this;
		var xmldata=templateSubset_me.populateSubSetXml();
		if(templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2')
		{
			//liuyz 卡片状态才判断是否更改提示保存成功
			if(templateSubset_me.view_type=='card')
			{
				if(!templateCard_me.isHaveChange&&!templateSubset_me.isEditChange)
		    	{
		    		templateCard_me.isHaveChange=true;
		    	}
			}
		}
		//显示弹出窗口时调用回调函数
		if(templateSubset_me.templPropety.dataChangeFunc)
		{ 
			Ext.callback(eval(templateSubset_me.templPropety.dataChangeFunc),null,[templateSubset_me.templPropety.uniqueId,xmldata]);
		}
		
		
	},
	
	//保存按钮
	 save_Subset_Record:function(flag,asyncflag){//bug47446新增参数用于确定保存操作是同步还是异步。
		 if(asyncflag==null||asyncflag==undefined||typeof(asyncflag)=='undefined'){
			 asyncflag=true;
		 }
		var templateSubset_me = this;
	    var map = new HashMap();
	    if(templateSubset_me.isShowWin != "0"){//弹出窗口 内容无修改时直接关闭不执行保存操作
	    	if(!templateSubset_me.isShowWinCellChange){
	    		templateSubset_me.saveflag = '';
				templateSubset_me.saveflag='false';
				if(templateSubset_me.closeFlag)
				{
					templateSubset_me.close_Subset_Record(); //暂时去掉关闭窗口
				}
		    	return;
	    	}else{
	    		templateSubset_me.isShowWinCellChange=false;
	    		//超出100条数据添加等待框
	    		if(templateSubset_me.store.data.length>100){
	    			templateSubset_me.loadTask('save',templateSubset_me.uniqueId + "subwin");
	    		}
	    	}
	    }
	    if(flag==0)
	    {
	    	templateSubset_me.saveflag='true';
	    }
	    else if(flag==1&&templateSubset_me.isShowWin == "1")
	    {
	    	templateSubset_me.saveflag='unKnow';
	    }
	    else //防止列表保存出现死循环。
	    {
	    	templateSubset_me.saveflag='false';
	    }
	    var realtask_id_e="0";
		if(templateSubset_me.view_type=='list'){
			task_id = templateList_me.templPropety.task_id;
			realtask_id_e=templateSubset_me.templPropety.task_id;
		}else{
			task_id = templateCard_me.templPropety.task_id;
			realtask_id_e=templateCard_me.cur_task_id;
		}
	    var xmldata=templateSubset_me.populateSubSetXml();
	    map.put('tabid',templateSubset_me.tabid);
	    map.put('a0100',templateSubset_me.a0100);
	    map.put('objectid',templateSubset_me.view_type=='card'?templateCard_me.personListCurRecord.data.objectid_e:templateSubset_me.templPropety.objectid);
	    map.put('basepre',templateSubset_me.basepre);
	    map.put("table_name",templateSubset_me.table_name);   //表名
	    map.put('columnName',templateSubset_me._field_name);
	    map.put('ins_id',templateSubset_me.templPropety.ins_id);
	    map.put('task_id',task_id);
	    map.put('realtask_id_e',realtask_id_e);
	    map.put('uniqueId',templateSubset_me.templPropety.uniqueId);
	    map.put('approveflag',templateSubset_me.templPropety.approveflag);
	    map.put('xmldata',xmldata);
	    map.put('viewtype',templateSubset_me.view_type);
	    map.put("allNum",templateTool_me.getTotalCount());
		map.put("task_id",templateMain_me.templPropety.task_id);
	    Rpc({functionId:'MB00003007',async:asyncflag,success:templateSubset_me.save_ok,scope:templateSubset_me},map);
	 },
	
	 save_ok:function(form,action){ 
	    var templateSubset_me = this;
	    if(templateSubset_me.isShowWin != "0"){//保存关闭等待框
	    	templateSubset_me.loadTask('destroy');
	    }
		var form = Ext.decode(form.responseText);  
		if(form.succeed)
		{
		    //liuyz 子集内容修改了才提示保存成功
			if(templateSubset_me.saveflag=='true'){
				 Ext.showAlert('保存成功！',function(){
					 templateSubset_me.saveData_commonFun(form);
				 });
			}else if(templateSubset_me.saveflag=='unKnow'&&templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2'&&!(templateSubset_me.subDatajsonStr==getDecodeStr(form.subData)))
			{
				Ext.showAlert('保存成功！',function(){
					templateSubset_me.saveData_commonFun(form);
                });
		    }
		    else
		    {
		    	templateSubset_me.saveData_commonFun(form);
		    }
		}
		else
		{
			var message = form.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
			}else{
				if(templateSubset_me.saveflag=='true')
				{
					Ext.showAlert(form.message);
				} 
			}
		}
	},
	saveData_commonFun:function(form){
		var templateSubset_me = this;
		templateSubset_me.chgInfoList=form.chgInfoList; 
        if(templateSubset_me.isShowWin == "1" ){
			    templateSubset_me.saveflag = '';
				templateSubset_me.saveflag='false';
				if(templateSubset_me.closeFlag)
				{
					templateSubset_me.close_Subset_Record(); //暂时去掉关闭窗口
				}
		  }
		//显示弹出窗口时调用回调函数
		if(templateSubset_me.templPropety.saveFunc)
		{ 
			this.subDatajson=Ext.decode(getDecodeStr(form.subData));  
			Ext.callback(eval(templateSubset_me.templPropety.saveFunc),null,[templateSubset_me.templPropety.uniqueId,form.subData,form.xmldata]);
			if(Ext.getCmp(templateSubset_me.uniqueId + "_0_tablePanel").getStore().data.items.length>0){
				var subwin_ = Ext.getCmp(templateSubset_me.uniqueId + "subwin");
				if(subwin_){
					var records = []; 
					Ext.getCmp(templateSubset_me.uniqueId + "_0_tablePanel").getStore().each(function(r){ records.push(r.copy());}); 
					Ext.getCmp(templateSubset_me.uniqueId + "_1_tablePanel").getStore().removeAll();
					var removedStores=Ext.getCmp(templateSubset_me.uniqueId + "_1_tablePanel").getStore().removed;
					for(var m=0;m<removedStores.length;m++){
						var record = removedStores[m].data;
						var i9999 = record.I9999;
						if(i9999!=-1&&record.delState!='D'){
							record.refState="S";
						}
					}
					Ext.getCmp(templateSubset_me.uniqueId + "_1_tablePanel").getStore().add(records);
				}
			}
		}
  
	},
		
	/**
	 * 显示上传的附件 lis
	 */
	showUploadFiles:function(id,index){
		var templateSubset_me = this;
		var dataList = [];
		 var pre = templateSubset_me.uniqueId+"_"+templateSubset_me.isShowWin;
		var storeId=pre+"_dataStore";    //"t_a19_2_dataStore"
        var substore = Ext.data.StoreManager.lookup(storeId);  //利用Ext.data.StoreManager的lookup()方法可以根据storeId得到对应的store
        var record = substore.getAt(index);
        var value=record.data.attach;
        var filelist=new Array();
        if(!!value && value!=''){
             var lists=value.split(',');
             if(lists.length > 1){
	             for(n = 0; n < lists.length; n++ )   
	             { 
	               var listn=lists[n].split('|');
	               var localName = listn[2];
	               var src = "";
	               var ext = localName.substring(localName.lastIndexOf(".")).toLowerCase();
	               var map = new HashMap();
	               					//fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx",
		
	               if(ext == ".txt")
	               	 src = "/images/txt.png";
	               else if(ext == ".doc" || ext == ".docx"){
	               	 src = "/images/word.png";
	               }
	               else if(ext == ".xls" || ext == ".xlsx"){
	               	 src = "/images/excell.png";
	               }
	               else if(ext == ".ppt" || ext == ".pptx"){
	               	 src = "/images/ppt.png";
	               }
	               else if(ext == ".pdf"){
	               	 src = "/images/PDF.png";
	               }
	               else if(ext == ".zip" || ext == ".rar")
	               	 src = "/images/zip.png";
	               else if(ext == ".jpg" || ext == ".jpeg" || ext == ".png" || ext == ".bmp"){
	                 src = "/images/img.png";
	               }
	       
	               map.put('src',rootPath + src);
	               map.put('Fn',"templateSubset.downFile");
	               map.put('params',[listn[1],ext,listn[2],listn[0]]);
	               map.put('desc',listn[2]);
	               filelist.push(map);
	            }
		      Ext.require('EHR.photoViewer.PhotoViewer',function(){
					 templateSubset_me.photoView = new EHR.photoViewer.PhotoViewer({
						dataList:filelist,
						connEle:id,
						config:{
							ImgNum:4,
							listeners:"closeImg",
							floatImgWidth:40,//展示图片宽
		    				floatImgHeight:40//展示图片高
		 				 }
					})
				})
             }else{
             	var listn=lists[0].split('|');
                var filename = listn[0];
                var localname = listn[2];
                var path = listn[1];
                var src = "";
                var ext = localname.substring(localname.lastIndexOf("."));
                templateSubset_me.downFile(path,ext,localname,filename);
             }
       }
	},
		
	/**
	 * 下载文件 lis
	 */
	downFile:function(path,ext,srcfilename,filename){
		var templateSubset_me = this;
		var map = new HashMap();
    	map.put("path",path);	
    	map.put("srcfilename",encode(srcfilename));	
    	map.put("filename",filename);
    	map.put("ext",ext);	
    	map.put("type","0");	
    	map.put("task_id",templateMain_me.templPropety.task_id);
    	if(Ext.isIE)
    		map.put("isIE","true");	
    	else
    		map.put("isIE","false");	
	    Rpc({functionId:'MB00003003',async:false,success:function(form){
	    	var result = Ext.decode(form.responseText);
	    	if(result.succeed){
	    		var filePath = result.path;
	    		var displayfilename = result.displayfilename;;
	    		if(ext == ".jpg" || ext == ".jpeg" || ext == ".png" || ext == ".bmp"){
	            		var obj = new Object();
						obj.filePath = filePath;
						//obj.imgWidth = result.imgWidth;
						//obj.imgHeight = result.imgHeight;
						obj.srcfilename = displayfilename;
						Ext.require('EHR.imgshow.ImgShow',function(){
							Ext.create("EHR.imgshow.ImgShow",obj);
						})
            	}else if(ext == ".pdf"){
	            	if (filePath=="" ) return;
	                var url = "/servlet/vfsservlet?fileid="+filePath;
        			var win=open(url,"pdf");
            	}/* 调用vfs下载控件暂时去除ie浏览器预览文件
            	else if(Ext.isIE && (ext == ".xlsx" || ext == ".xls" || ext == ".doc" || ext == ".docx" || ext == ".dot" || ext == ".ppt" || ext == ".pptx")){
            		if (filePath=="" ) return;
	                var win=open("/system/options/customreport/displayFile.jsp?filename="+$URL.encode(displayfilename)+"&filepath="+$URL.encode(filePath) ,"");
            	}*/else{
	            	if (filePath=="" ) return;
	            	var url = "/servlet/vfsservlet?fileid="+filePath;
        			var win=open(url,"");
            	}
	    	}else{
	    		Ext.showAlert(result.message);
	    	}
	    },scope:templateSubset_me},map);
	},
	 /*
	 * 显示文件附件 liuzy 20151019
	 */
	 showfiles:function(index,his_readonly){ 
		   var templateSubset_me = this;
		   var rootDir = templateSubset_me.templPropety.rootDir;   //是否设置了文件根目录
		   /*if(rootDir == ""){
			    Ext.showAlert(MB.MSG.subAttachmentNoDir);
		   		return ;
		   }*/
		   
		   var mediasortList = undefined;
		   var map = new HashMap();
		   map.put("tabid",templateSubset_me.templPropety.tabid);
		   map.put("task_id",templateMain_me.templPropety.task_id);
		   map.put("type","2");
		   
		   //vfs重构 兼容旧子集附件保存为路径时 filename 处理为 path+filename---start
		   var pre = templateSubset_me.uniqueId+"_"+templateSubset_me.isShowWin;
	 	   var storeId=pre+"_dataStore";    //"t_a19_2_dataStore"
           var substore = Ext.data.StoreManager.lookup(storeId);  //利用Ext.data.StoreManager的lookup()方法可以根据storeId得到对应的store
	       var record = substore.getAt(index);
	       var value=record.data.attach;
	       map.put("value",value);
	       //----end
		   
		   Rpc({functionId:'MB00003003',async:false,success:function(form){
		   		var result = Ext.decode(form.responseText);
		   		mediasortList = result.mediasortList;//多媒体文件类型
		   		value=result.value;
		   },scope:templateSubset_me},map);	
		   if(mediasortList.length==0){
		   		Ext.showAlert("没有设置多媒体文件类型权限，不能进行上传操作！");
		   		return;
		   }
		   var tabid = templateSubset_me.templPropety.tabid;
		  
	       var edit = 0;
	       if((templateSubset_me._rwPriv=="2"&&templateSubset_me._chgState=='2')&&(record.data.hisEdit!='0'||his_readonly=="false")){//有写权限
	       		 if (record.data.canEdit=="true"){
      				edit = 1;
      	   		 }
	       }
	       var filelist=new Array();
	       var ids = "";
	       //var rootPath="/multimedia/template/template_"+tabid+"/";
	       if(typeof(value)!='undefined' && value!=''){
	       		var lists=value.split(',');
	            for(n=0; n<lists.length; n++ )   
	            {  
	               var listn=lists[n].split('|');
	               var map = new HashMap();
	               map.put('filename',listn[1]);
	               if(listn[1]==''){
	            	   map.put('path',listn[0]);
	               }else{
	            	   map.put('path',listn[1]);
	               }
	               map.put('localname',listn[2]);
	               map.put('size',listn[3]);
	               map.put('id',listn[4]);
	               map.put("fileid",listn[1])
	               ids = ids + "," + listn[4];
	               map.put('successed',true);
	               var filetype = listn[6];
		           if(filetype!=undefined){
	               	   filetype = filetype.substring(5,filetype.length);
	               }
	               map.put('fileType',filetype);
	               filelist.push(map);
	            } 
	         /*var i= value.indexOf(',');
	         if(i!=-1)
	         {
	                      
	         }else{
	               var listn=value.split('|');
	               var map = new HashMap();
	               map.put('filename',listn[0]);
	               map.put('path',listn[1]);
	               map.put('localname',listn[2]);
	               map.put('size',listn[3]);
	               map.put('id',listn[4]);
	               ids = ids + "," + listn[4];
	               map.put('successed',true);
	               filelist.push(map);
	         }*/
	       }
	       
	       if(!!!templateSubset_me.templPropety.multimedia_maxsize)
	       			templateSubset_me.templPropety.multimedia_maxsize = '20M';
	        Ext.create("SYSF.FileUpLoad",{
					renderTo:Ext.getBody(),
					emptyText:"请输入文件路径或选择文件",
					upLoadType:2,
					realDelete:false,
					fileNameMaxLength:180,
					//uploadUrl:rootPath,
					isDownload:true,
					//savePath:rootPath,
					fileList:filelist,
					isShowOrEdit:edit,
					fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.pptx;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.pdf",
					buttonText:'上传',
					fileTypeMapList:mediasortList,
					isTempFile:true,//上传文件为临时文件 后台保存再转正
					VfsFiletype:VfsFiletypeEnum.multimedia,
					VfsModules:VfsModulesEnum.RS,
					VfsCategory:VfsCategoryEnum.other,
					CategoryGuidKey:'',
					success:function(list){
					    if(list.length!=0){
							var valuestr='';
						    for(var m=0;m<list.length;m++){
						   		var text = '';
								//var id = list[m].id;              //文件唯一标识 (取消文件唯一标识)     
								var localname=list[m].localname;  //原始文件名 
								localname=replaceAll(localname,",","，");// 由于","是字符串拼接符,如果文件名中包含",",需要将其转成全角"，"
								/*
								 if(ids.indexOf(id) < 0)
									localname = localname.substring(0,localname.lastIndexOf("."));//去掉后缀
								*/	
								var size = list[m].size.replace("\r","").replace("\n","").replace("\r\n","");       //文件大小
								var path ="";          //文件上传路径
								if(list[m].path){
									path=list[m].path;
								}
								var filename = ""  //编码后文件名
							    if(list[m].fileid){
							    	filename="";
							    	path = list[m].fileid;
							    }else{
							    	filename=list[m].filename
							    }
								var successed=list[m].successed;  //是否成功标识
								var filetype = list[m].fileType;  //文件类型
								text=" "+'|'+list[m].fileid+'|'+localname+'|'+size+'|'+" "+'|'+m+'|type:'+filetype ;
		                        valuestr+=text+',';
							}
							valuestr=valuestr.substring(0,valuestr.length-1);
						    record.set('attach',valuestr);
					    }else{
					        record.set('attach','');
					    }
					    record.commit();
			        	templateSubset_me.dataChange();
			        	templateSubset_me.isShowWinCellChange=true;//标记修改了子集内容。
					},
					//回调方法，失败
		 			error:function(){
		  				Ext.showAlert("文件上传失败 ！");
		 			},
					//fileSizeLimit:templateSubset_me.templPropety.multimedia_maxsize, 取消最大文件大小校验 上传文件servlet会校验文件大小
					isDelete:true
			 }); 		     
	 },
	 /*
	 * 返回新增的子集记录 空记录
	 */
	get_Subset_NewRecord:function()
	{
		var templateSubset_me = this;
		var strRecord ={};
		strRecord["I9999"]='-1';
		strRecord["canEdit"]='true';
		var timestamp=new Date().getTime();
		strRecord["timestamp"]=timestamp+'';
		strRecord["record_key_id"]=templateSubset_me.record_key_id_pre+timestamp+(Math.round(Math.random()*100)*Math.round(Math.random()*100));
		for(var i=0;i<templateSubset_me._col;i++)
		{	
			var obj = templateSubset_me._field_list[i];
			var defValue = obj.defaultValue;
			strRecord[templateSubset_me._column[i]]=defValue;
			var fldPriv = obj.fldPriv;
			//修正子集插入不能编辑
			/*if(fldPriv=='2'&&templateSubset_me._chgState=='2')
				strRecord["canEdit"]='true';
			else
				strRecord["canEdit"]='false';*/
			if(obj.fldType == "D"){
				strRecord[templateSubset_me._column[i]+"_D"]=obj.format;
			}
		}
		return strRecord;
	},
	 /*
	 * 组装子集某条记录xml storeFlag:store格式的数据。
	 */
	getSubSetRecordXml:function(record,storeFlag,subcolumns,_field_list){
		var recordXml="";
		var i9999="";
		var delState="";
		var edit = "";
		var timestamp = "";
		var record_key_id="";
		var isHaveChange="";
		if(storeFlag){
			i9999=record.get("I9999");
			delState=record.get("delState");
			if(i9999!='-1')
				edit = record.get("hisEdit");
			if(i9999=='-1')
				timestamp = record.get("timestamp");
			record_key_id=record.get("record_key_id");
			isHaveChange= record.get("isHaveChange");
		}
		else {
			i9999=record["I9999"];
			delState=record["delState"];
			if(i9999!='-1')
				edit = record["hisEdit"];
			if(i9999=='-1')
				timestamp = record["timestamp"];
			record_key_id=record["record_key_id"];
			isHaveChange= record["isHaveChange"];
		}
	   
	    recordXml = "<record I9999=\""+i9999+"\"  ";
	    recordXml+="  record_key_id=\""+record_key_id+"\"  ";
		if(delState=='D')//已删除的
			recordXml +="state=\"D\"";
		else
			recordXml +="state=\"\"";
		if(edit!="")
			recordXml +=" edit=\""+edit+"\"  ";
		if(timestamp!=undefined&&timestamp!=''&&timestamp!=null)
			recordXml +=" timestamp=\""+timestamp+"\"  ";
		if(isHaveChange!=undefined&&isHaveChange!=''&&isHaveChange!=null)
			recordXml +=" isHaveChange=\""+isHaveChange+"\"  ";
		recordXml +=" >";	
		var values="";
		var isCdata = false;
		for(var i=0;i<subcolumns.length;i++){
			var fmobj=_field_list[i];
			if(fmobj.fldType=="M"&&fmobj.fldInputType=='1'&&!isCdata){
				isCdata = true;
				break;
			}
		}
		for(var j=0;j<subcolumns.length;j++)
	    {	
			if(j==0&&isCdata)
				recordXml +=" <![CDATA[";
			var columnFld=subcolumns[j];
			var value="";
			if(storeFlag)
				value=record.get(columnFld);
			else 
				value=record[columnFld];
			value = Ext.isDate(value)? Ext.Date.dateFormat(value, "Y-m-d") : value;
			if (value==null) value="";
			if (value==undefined||(value+"").replace(/(^\s*)|(\s*$)/g, "")=="undefined`") value="";//"undefined`前后可能有空格，需要将空格处理"
	   		var fmobj=_field_list[j];
	   		if (fmobj){
	   			if(fmobj.fldType=="A")
	   			{
	   				if (fmobj.codesetId!="0"){
	   					var valuearr=value.split("`");
	   					if (valuearr.length>1){
	   						value=valuearr[0];
	   					}
	   				}
	   			}
	   			else if(fmobj.fldType=="D"&&value!=null&&value!='')  
				{
				   // var _date=new Date(value.replace(/-/g, "/"));
					/*if (isNaN(_date))
					{
						value="";
					}*/
				}  
	   			else if(fmobj.fldType=="M")  
				{
				    
				}  
	   		}
	   		if (value==""&&(value+"").length==0){//bug32919 空串和0在js中相等，所以0会被清空
	   			value=" ";
	   		}
			value=replaceAll(value+"","~","～");
	   		values=values+"`"+value;
		}
		values=values.substr(1,values.length-1);
		if(!isCdata){
			values=replaceAll(values,"<","〈");
			values=replaceAll(values,">","〉");
			values=replaceAll(values,"＜","〈");
			values=replaceAll(values,"＞","〉");
		}
		recordXml += values;
		if(isCdata)
			recordXml +="]]>";
		recordXml += "</record>";	
		return recordXml;
	},
	
	/*
	 * 组装子集xml
	 */
	populateSubSetXml:function()
	{
		var templateSubset_me = this;
		var xml = "";
		xml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		xml += "<records columns=\"" +templateSubset_me._column.join("`")+ "\">";
		var content="";
		var i9999arr = [];
		//组装当前显示的记录
		//liuyz bug25354
		templateSubset_me.onlyHistoryReord=true;
		for(var i=0;i<templateSubset_me.store.getCount();i++)	{
		    var record = templateSubset_me.store.getAt(i);
		    //liuyz bug25354
		    var hisEdit = record.data.hisEdit;
			var canEdit = record.data.canEdit;
			/*if(templateSubset_me.onlyHistoryReord)
			{
				if(hisEdit!="0")
					if(canEdit=="true")
					{
						templateSubset_me.onlyHistoryReord=false;
					}
			}*/
		    var i9999=record.get("I9999");
		    i9999arr.push(i9999);
		    var recordXml=templateSubset_me.getSubSetRecordXml(record,true,templateSubset_me._column,templateSubset_me._field_list);	
		    content+=recordXml;
		}
		//liuyz bug25354
		/*if(Ext.getCmp("deleteButton"+templateSubset_me.id)&&templateSubset_me.store.getCount()>0)
			Ext.getCmp("deleteButton"+templateSubset_me.id).setDisabled(templateSubset_me.onlyHistoryReord);
		if(Ext.getCmp("deleteButton"+templateSubset_me.id)&&templateSubset_me.store.getCount()==0)
			Ext.getCmp("deleteButton"+templateSubset_me.id).setDisabled(false);*/
		//组装刚删除i9999不等于-1的记录
		var removedStores=templateSubset_me.store.getRemovedRecords();
		for(var i=0;i<removedStores.length;i++)	{
			var record = removedStores[i];
		    var i9999=record.get("I9999");
			if (i9999!="-1"){//删除库中已存在的记录
				if(record.get("refState")=='S'){
				}else{
					i9999arr.push(i9999);
					record.set("delState","D");
					var recordXml=templateSubset_me.getSubSetRecordXml(record,true,templateSubset_me.subcolumns,templateSubset_me._field_list);	
					content+=recordXml;
				}
			}
		}
		//组装之前删除的记录
		for(var i=0;i<templateSubset_me._removedRecords.length;i++)	{
			var record = templateSubset_me._removedRecords[i];	
			var i9999 = record["I9999"];
			if(i9999arr.length>0){
				templateSubset_me.addindexof();
				if(i9999arr.indexOf(i9999)==-1){
					var recordXml=templateSubset_me.getSubSetRecordXml(record,false,templateSubset_me.subcolumns,templateSubset_me._field_list);	
					content+=recordXml;
				}
			}else{
				var recordXml=templateSubset_me.getSubSetRecordXml(record,false,templateSubset_me.subcolumns,templateSubset_me._field_list);	
				content+=recordXml;
			}
		}

		xml = xml+content+"</records>";
		return	xml;
	},
	
	/**解析子集指标属性 xmlcontent为子集的表结构 */
	parserSubXml:function(subFields,Xml_param){
		var xmlrec=getDecodeStr(Xml_param);
		xmlrec=replaceAll(xmlrec,"＜","<");
		xmlrec=replaceAll(xmlrec,"＞",">");
		xmlrec=replaceAll(xmlrec,"＇","'");
		xmlrec=replaceAll(xmlrec,"＂",'"');
		xmlrec=replaceAll(xmlrec,"&","");
		var XMLDoc = loadXMLString(xmlrec);  //位于外部 JavaScript 中，用于加载 XML 字符串
		XMLDoc.async=false;
		var rootNode = XMLDoc.documentElement;//fields那层节点 
		try
		{
			if(rootNode)
			{
				var recNodes = xmlDoc.getElementsByTagName("field"); 
				for(var i=0;i<recNodes.length;i++){
					var node = recNodes[i];						
					var field={};
					field.fldName=node.getAttribute("fldName");
					field.fldTitle=node.getAttribute("fldTitle");
					field.fldWidth=node.getAttribute("fldWidth");
					field.format=node.getAttribute("format");//指标显示格式(日期型) 小数位（数值型）
					field.codeSetId=node.getAttribute("codeSetId");//是否是代码
					field.fldLength=node.getAttribute("fldLength");//长度
					field.fldDecLength=node.getAttribute("fldDecLength");//小数位
					if(node.getAttribute("fldType")=="N")
					{
						var r = /^\+?[1-9][0-9]*$/;//正整数 
		   				var decimalLength=field.fldDecLength;
		   				if(!r.test(field.fldDecLength))
		   					decimalLength=0; 
		   				if(r.test(node.getAttribute("format"))&&node.getAttribute("format")<decimalLength)
		   					decimalLength=node.getAttribute("format");
						field.fldDecLength=decimalLength; //node.getAttribute("format");//小数位
						//field.fldDecLength=node.getAttribute("format");//小数位
					}
					field.align=node.getAttribute("align");//水平对齐
					field.valign=node.getAttribute("valign");//垂直对齐
					field.need=node.getAttribute("need");//必填
					field.defaultValue=node.getAttribute("defaultValue");//默认值
					field.pre=node.getAttribute("pre");//前缀		
					field.fldType=node.getAttribute("fldType");//类型
					field.rwPriv=node.getAttribute("rwPriv");//权限
					if(node.getAttribute("fldType")=="M")
						field.fldInputType=node.getAttribute("fldInputType");//文本编辑方式（大文本才有）
					field.his_readonly=node.getAttribute("his_readonly");//列是否历史记录只读
					field.imppeople=node.getAttribute("imppeople");//启用选人组件
					field.childRelationField=node.getAttribute("childRelationField");//联动父级指标
					field.fatherRelationField=node.getAttribute("fatherRelationField");//联动孩子指标
					subFields[i]= field;
				}
			}
		}
		catch(e)
		{
			Ext.showAlert(e.message);
		}
	},
	
	/**
 * 格式化日期格式
 * date:日期类型
 * format 日期格式 人事异动专用格式
 */
formatSubSetDate: function(date, format) {
    format = format + "";
    var yy = date.getFullYear() + "";//获得完整年份，1988  
    var mm = date.getMonth() + 1;
    var dd = date.getDate();
   // var dd = date.getDay();//星期
    var hh = date.getHours();
    var mi = date.getMinutes();
    var ss = date.getSeconds();
    var strResult="";
   /* if (yy<100){//1900到2000年
        yy=1900+parseInt(yy);
    }*/
    var disformat= parseInt(format);
    switch (disformat) {
        case 0: // 1991.12.3  注意：因为这种格式是默认格式 都是按1991.12.03显示。
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
        case 1: // 91.12.3
            strResult=yy.substring(2);
            strResult=strResult+"."+getTwoDigitNumber(mm)+"."+dd;
            break;
        case 2:// 1991.2
            strResult=yy+"."+mm;
            break;
        case 3:// 1992.02
            strResult= yy+"."+getTwoDigitNumber(mm);
            break;
        case 4:// 92.2
            strResult=yy.substring(2);
            strResult=strResult+"."+mm;
            break;
        case 5:// 98.02
            strResult=yy.substring(2);
            strResult=strResult+"."+getTwoDigitNumber(mm);
            break;
        case 6:// 一九九一年一月二日
            strResult= exchangYearToCn(yy)+"年"+exchangMonthToCn(mm)+"月"+exchangDayToCn(dd)+"日";
            break;
        case 7:// 一九九一年一月
            strResult= exchangYearToCn(yy)+"年"+exchangMonthToCn(mm)+"月";
            break;
        case 8:// 1991年1月2日
            strResult= yy+"年"+mm+"月"+dd+"日";
            break;
        case 9:// 1991年1月
            strResult= yy+"年"+mm+"月";
            break;
        case 10:// 91年1月2日
            strResult=yy.substring(2);
            strResult=strResult+"年"+mm+"月"+dd+"日";
            break;
        case 11:// 91年1月
            strResult=yy.substring(2);
            strResult=strResult+"年"+mm+"月";
            break;
        case 12:// 年龄
        	var date=new Date();
        	var year=date.getFullYear();
        	var month=date.getMonth()+1;
        	var day=date.getDate();
        	var result=year-parseInt(yy,10);
        	if(month<parseInt(mm,10))
        	{
        		result=result-1>0?result-1:0;
        	}
        	else
        	{
        		if(month==parseInt(mm,10))
        		{
        			if(day<parseInt(dd,10))
        			{
        				result=result-1>0?result-1:0;
        			}
        		}
        	}
        	strResult=result;
            break;
        case 13:// 1991（年）
            strResult=yy+"";
            break;
        case 14:// 1 （月）
            strResult=mm+"";
            break;
        case 15:// 23 （日）
            strResult=dd+"";
            break;
        case 16:// 1999年02月
            strResult= yy+"年"+getTwoDigitNumber(mm)+"月";
            break;
        case 17:// 1999年02月03日
            strResult= yy+"年"+getTwoDigitNumber(mm)+"月"+getTwoDigitNumber(dd)+"日";
            break;
        case 18:// 1992.02.01
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
        default:
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
    }
  return strResult;
},
addindexof:function(){
		if (!Array.prototype.indexOf)
			{
			  Array.prototype.indexOf = function(elt /*, from*/)
			  {
			    var len = this.length >>> 0;
			    var from = Number(arguments[1]) || 0;
			    from = (from < 0)
			         ? Math.ceil(from)
			         : Math.floor(from);
			    if (from < 0)
			      from += len;
			    for (; from < len; from++)
			    {
			      if (from in this &&
			          this[from] === elt)
			        return from;
			    }
			    return -1;
			  };
			}
	},
excelImport:function(){//子集导入功能
		var templateSubset_me = this;
		var store=templateSubset_me.store;
		var dbper=templateSubset_me.basepre;//人员库
		var fieldSet=templateSubset_me._field_name.split('_')[1];//子集
		
		var colums=templateSubset_me._field_list;//列名
		var panel=Ext.create('Ext.window.Window',{
	        title:common.button.toimport,  
	        height:200,  
	        width:300,
	        layout:'fit',
	        id:'winImportTemplate',
			modal:true,
			resizable:false,
			closeAction:'destroy',
	        items: [{
	        		xtype:'panel',
	        		border:false,
	        		layout:'absolute',	
	       		items: [{
	       			x:60,
	       			y:30,
				        xtype: 'label',
				        forId: 'myFieldId',
				        text: '1、'+common.button.downLoadTitle
	              },
	              {
	                x:200,
	       			y:30,
				        xtype: 'button',
				        id:'download',
				        text: common.button.downLoad,
				        handler:function(){
				        	templateSubset_me.outExcel(colums,fieldSet);
				        }
				    },
	              {
	                  x:60,
	       			y:80,
				        xtype: 'label',
				        forId: 'myFieldId',
				        text: '2、'+common.label.selectImportFile
	              },
	              {
	                  x:200,
	       			  y:80,
				      xtype: 'button',
				      id:'see',
				      text:common.button.view,
				      height:22,
				      listeners:{
						afterrender : function(btn){
								Ext.widget("fileupload",{
				  	   					upLoadType:3,
				  	   					width:32,
				  	   					realDelete:true,
				  	   					style:'position:relative;top:-22px',
				  	   					buttonText:'',
				  	   					fileExt:"*.xls",//添加对上传文件类型控制
				  	   					renderTo:this.id,
				  	   					error:function(){
							   				Ext.showAlert(common.msg.uploadFailed+"！");
						   				},
				  	   					success:function(list){
				  	   					templateSubset_me.inputExcel(list[0].fileid,colums,fieldSet);
						   				},
				  	   					callBackScope:'',
				  	   					//savePath:''，
				  	   					uploadUrl:"/case/",
					  	   				isTempFile:true,
										VfsFiletype:VfsFiletypeEnum.doc,
										VfsModules:VfsModulesEnum.RS,
										VfsCategory:VfsCategoryEnum.other,
										CategoryGuidKey:''
				  	   				});
							}
						
				      }
				    },{
				       x:60,
				       y:120,
				       xtype: 'label',
				       html:'注意：此功能仅支持追加记录!'
				    }
				    ]
	        }]
	  }).show();
	},
	outExcel:function(column,fieldSet){//导出模板
		var map=new HashMap();
		map.put("fieldSet",fieldSet);
		map.put("column",column);
		map.put("flag","1");
		Ext.MessageBox.wait(common.msg.exporting, common.msg.wait);
		Rpc({functionId:'MB00004007',success:function(res){
			var res=Ext.decode(res.responseText);
			if(res.errorMsg){
				Ext.showAlert(res.errorMsg);
			}else{
				Ext.MessageBox.close(); 
				Ext.getCmp('winImportTemplate').close();
				 open("/servlet/vfsservlet?fromjavafolder=true&fileid="+res.file);	
			}
				
		},scope:this},map);
	},
	inputExcel:function(fullPath,column,fieldSet){//数据导入
		var templateSubset_me = this;
		var task_id;
		var realtask_id_e="0";
		if(templateSubset_me.view_type=='list'){
			task_id = templateList_me.templPropety.task_id;
			realtask_id_e=templateSubset_me.templPropety.task_id;
		}else{
			task_id = templateCard_me.templPropety.task_id;
			realtask_id_e=templateCard_me.cur_task_id;
		}
		var map=new HashMap();
		map.put("table_name",templateSubset_me.table_name);
		map.put("tabid",templateSubset_me.tabid);
		if(templateSubset_me.view_type=='list'){
			map.put("basepre",templateSubset_me.templPropety.objectid);
		}else{
			map.put("basepre",templateSubset_me.basepre);
		}
		map.put("_field_name",templateSubset_me._field_name);
		map.put("uniqueId",templateSubset_me.uniqueId);
		map.put("path",fullPath);
		map.put("fieldSet",fieldSet);
		map.put("column",column);
		map.put("ins_id",templateSubset_me.templPropety.ins_id);
		map.put("task_id",task_id);
		map.put("realtask_id_e",realtask_id_e);
		map.put("flag","2");
		 map.put('viewtype',templateSubset_me.view_type);
		var xmldata=templateSubset_me.populateSubSetXml();
		map.put("xmldata",xmldata);
		Ext.MessageBox.wait(common.msg.importing, common.msg.wait);
		Rpc({functionId:'MB00004007',success:function(form,action){
			Ext.MessageBox.close(); 
			Ext.getCmp('winImportTemplate').close();
			var form = Ext.decode(form.responseText);
			if(form.errorMsg){
				Ext.showAlert(form.errorMsg);
			}else if(form.succeedFlag){
				templateSubset_me.store.add(form.recoredlist);
				templateSubset_me.dataChange();
				if(templateSubset_me.isShowWin != "0"){//弹出框内容修改 单元格修改标记置为true
					templateSubset_me.isShowWinCellChange=true;
				}
				Ext.showAlert(form.Msg);
			}
		},scope:templateSubset_me},map);
	},
	loadTask:function(type,renderToId){//等待提示框，保存 删除使用
		var templateSubset_me=this;
		var text=type=='save'?MB.MSG.savetemplateSubset:MB.MSG.delTemplateSubset
		if(type!='destroy'){
			new Ext.LoadMask({
				id:templateSubset_me.uniqueId+"myMask",
	        	msg    : text,
	        	style:'background-color: rgba(204, 204, 204, 0.01);',//遮罩层设为透明
	        	target : Ext.getCmp(renderToId)
				}).show();
		}else{//去除等待框
			var mytask=Ext.getCmp(templateSubset_me.uniqueId+"myMask");
			if(mytask){
				mytask.destroy();
			}
		}
		
	},clearValue : function(id,record,grid){
		templateSubset_me=this;
		record.set(id,'');
		var columns = grid.query('gridcolumn[dataIndex='+id+']');
		if(columns.length>0){
			var column=columns[0];
			column.getEditor().parentid='';
			var index = templateSubset_me._column.indexOf(column.dataIndex);	
			var fmobj=templateSubset_me._field_list[index];
			if(fmobj){
				var childRelationFieldStr=fmobj.childRelationField;
				if(childRelationFieldStr!=undefined&&childRelationFieldStr.length>0){
					var childRelationFieldList=childRelationFieldStr.split(",");
					for(var index=0;index<childRelationFieldList.length;index++){
						templateList_me.clearValue(childRelationFieldList[index],record,grid);
					}
				}
			}
		}
	}
});