Ext.define('EHR.mobleTemplate.Template', {
	extend: 'Ext.Container',
	xtype: 'wTemplate',
	requires: [	'EHR.mobleTemplate.CodeSelectField','EHR.photoselector.PhotoSelector','EHR.mobleTemplate.TemplateNumberField','EHR.mobleTemplate.TemplateTextField','EHR.mobleTemplate.TemplateTextAreaField'],
	param: '{"tabid":"1","isEdit":"1","taskid":"20","ins_id":"102","fromMessage":"1","object_id":"Usr00000001"}',
	selectIndex: undefined,
	config:{
		width:'100%',
		height:'100%',
		id:'template',
		items:[{
			id:'toolbarid',
			title:'<b>请假申请<b>',
			xtype:'toolbar',
			docked:'top',
			height:'45px',
			cls:'template_toolbar',
			layout:{
				type:'hbox',
				align:'middle'
			}
			},{
				style:{
					//'border-bottom':'1px solid #DBDBE0'
				},
				id:'templateform',
				xtype:'templateForm',
				height:'100%',
				width:'100%'
			}]
	},
	initialize:function(){
		this.callParent();
//		this.setFormItem(this.config.qrData);
	},

	setParam: function(param) {
		this.param = param;
	},
	getParam: function() {
		return this.param;
	},
	setSelectIndex: function(selectIndex) {
		this.selectIndex = selectIndex;
	},
	getSelectIndex: function() {
		return this.selectIndex;
	},
	setFormItem: function(data) {
		var me = this;
		var fieldList = data.fieldList; //指标集合		
		var fieldCmpArray = [];
		var lineStyle = 'font-size:16px;border-bottom:1px solid #ccc;line-height:24px;';
		me.codes = data.codes;
		//创建其余普通项
		for (var i = 0; i < fieldList.length; i++) {
			var fieldCmp = undefined;
			var notEditable = true;
			var field = fieldList[i];
			var priv = field.priv; //// 0：无读写权限 1：读权限  2：写权限
			var chgstate = field.chgstate;//// 1：变化前指标  2：变化后指标
/*			if (priv == 0){ 临时人员没有权限 但是要显示模版数据
				continue;
			}*/
			if(field.chgstate == 2){//是变化后指标
					notEditable = false; //编辑
			}
			var itemtype = field.item_type; //指标类型
			if(itemtype=='P'||itemtype =='F'){
				notEditable = false; //编辑
			}
			if(field.opinion_field&&field.opinion_field=='true'){//审批意见指标不支持编辑
				notEditable = true;
			}
			var code_id = field.code_id; ////指标关联代码
			var isSingleLevel = true;
			for(var s= 0;s<me.codes.length;s++){
				var codeitem = me.codes[s];
				if(codeitem.codesetid == code_id){
					isSingleLevel = codeitem.isSingleLevel;//代码指标  是否是多层级  多层级true 单层级 false 默认多层级
				}
			}
			if(code_id=="UN"||code_id=='UM'||code_id=='@K'){//单位部门岗位 为多层级
				isSingleLevel = false;
			}
			var isRequired = '';
			var isrequired = false;
			if ("true" == field.fillable) {
				isRequired = '（必填）';
				isrequired = true;
			}
			var value = field.value; //值
			if (value == undefined) {
				value = '';
			}
			var subflag  = field.subflag;//1 是子集 0不是子集
			var mustfillrecord = field.mustfillrecord;//子集记录必填标志位
			var text = field.hz;
			var placeHolder = ""; //输入框为空时显示的文字
			if(!notEditable){
				if((code_id!=0||"D"==itemtype)&&"M"!=itemtype){
					placeHolder = "请选择"+isRequired;
				}else{
					placeHolder = "请输入"+isRequired;
				}
			}
			var commonName;
			if(itemtype){
				//单元格id  单元格类型   label值     为空时显示值    是否只读
				commonName = field.gridno+"`"+itemtype+"`"+text+"`"+placeHolder+"`"+notEditable+"`"+isrequired;
			}else{//子集 ` 单元格id ` 子集编号 ` 名称label值 ` 是否编辑
				commonName = field.gridno+"`"+'sub'+'`'+field.set_id+"`"+text+"`"+notEditable;
			}
			//isSingleLevel
			if ("A" == itemtype) {//字符型 和 代码型
				fieldCmp = me.setZiFuFieldItem(code_id,notEditable,commonName,text,value,placeHolder,field.value_view,lineStyle,field.item_length,isSingleLevel);
			}else if("P" == itemtype){//照片
				fieldCmp = me.setPhotoFieldItem(notEditable,commonName,text,value,field.item_id,field.gridno,undefined,isrequired);
			}else if("F" == itemtype){//附件
				fieldCmp = me.setFileFieldItem(notEditable,commonName,text,value,field.item_id,field.gridno,undefined,isrequired,field.file_type);
			}else if("N"==itemtype){//数值
				fieldCmp = me.setNumFieldItem(notEditable,commonName,text,value,placeHolder,lineStyle);
			}else if("M"==itemtype){//大文本
				fieldCmp = me.setMaxTextFieldItem(notEditable,commonName,text,value,placeHolder);
			}else if("D"==itemtype){//日期型
				fieldCmp = me.setDateFieldItem(notEditable,commonName,text,value,placeHolder,lineStyle,field.format);
			}else if(subflag == 1){//子集
				fieldCmp = me.setFieldSetItem(notEditable,commonName,text,field,field.sub_domain,field.value,field.gridno,undefined,mustfillrecord);
			}
			if (fieldCmp) {
				if(itemtype=="A"||itemtype=="D"||itemtype=="M"||itemtype=="N"){
					if(isrequired&&!notEditable){
						fieldCmp.required = true;//如果是必须填 则不允许为空
					}else{
						fieldCmp.required = false;
					}
				}
				fieldCmpArray.push(fieldCmp);
			}
		}
		me.choseButton(fieldCmpArray,data)
	},
	choseButton:function(fieldCmpArray,data){
		var toolbar = this.getComponent('toolbarid');
		toolbar.setTitle('<b>'+data.tabname+'</b>');
		var form = this.getComponent('templateform');
		form.getComponent('fieldsetid').setItems(fieldCmpArray);
	
		var deal_flag = data.deal_flag;
		var pagenum = data.page_num;//总页数
		var pageno = data.page_no;//当前页
		var pageIndexList = data.pageIndexList;
		if(pageIndexList.length == 1){
			form.query('#preButton')[0].setHidden(true);
			form.query('#nextButton')[0].setHidden(true);
			if(deal_flag==2||deal_flag==3){
				form.query('#submitButton')[0].setHidden(false);//保存
				form.query('#submitButton')[0].setWidth('80%');
//				form.getComponent('baopiButton').setHidden(false);//报批、提交
//				Ext.getCmp('showInfoBtn').setHidden(true);//显示详情
//				if(deal_flag==3){
//					form.getComponent('baopiButton').setText("<font color=\"#fff\">提交</font>");
//				}
				//toolbar.getComponent('transactionbutton').setHidden(true);//办理 驳回批准
			}
			/*if(deal_flag==0){
				toolbar.getComponent('transactionbutton').setHidden(true);//办理 驳回批准
			}*/
//			if(deal_flag!=0){
//				form.getComponent('submitButton').setHidden(false);//保存
//			}
//			if(deal_flag == 1){
//				Ext.getCmp('showInfoBtn').setHidden(false);//显示详情
//				form.getComponent('pizhunButton').setHidden(false);//批准
//				form.getComponent('bohuiButton').setHidden(false);//驳回
//			}
//			if(deal_flag == 0){
//				Ext.getCmp('showInfoBtn').setHidden(false);//显示详情
//			}
		}
		if(pageIndexList.length >1){
			if(pageIndexList[0] == Ext.getCmp('qrcardinfo').pageid){
				form.query('#preButton')[0].setHidden(true);
				form.query('#nextButton')[0].setHidden(false);
				form.query('#nextButton')[0].setWidth('80%');
				form.query('#submitButton')[0].setHidden(true);//保存
			}else if(pageIndexList[pageIndexList.length-1] == Ext.getCmp('qrcardinfo').pageid){
				form.query('#preButton')[0].setHidden(false);
				form.query('#nextButton')[0].setHidden(true);
				form.query('#nextButton')[0].setWidth('40%');
				if(deal_flag==2||deal_flag==3){
					form.query('#submitButton')[0].setHidden(false);//保存
//					form.getComponent('baopiButton').setHidden(false);//报批、提交
//					Ext.getCmp('showInfoBtn').setHidden(true);//显示详情
//					if(deal_flag==3){
//						form.getComponent('baopiButton').setText("<font color=\"#fff\">提交</font>");
//					}
					//toolbar.getComponent('transactionbutton').setHidden(true);//办理 驳回批准
				}
				/*if(deal_flag==0){
					toolbar.getComponent('transactionbutton').setHidden(true);//办理 驳回批准
				}*/
//				if(deal_flag!=0){
//					form.getComponent('submitButton').setHidden(false);//保存
//				}
//				if(deal_flag == 1){
//					Ext.getCmp('showInfoBtn').setHidden(false);//显示详情
//					form.getComponent('pizhunButton').setHidden(false);//批准
//					form.getComponent('bohuiButton').setHidden(false);//驳回
//				}
//				if(deal_flag == 0){
//					Ext.getCmp('showInfoBtn').setHidden(false);//显示详情
//				}
			}else{
				form.query('#preButton')[0].setHidden(false);
				form.query('#nextButton')[0].setHidden(false);
				form.query('#nextButton')[0].setWidth('40%');
				form.query('#submitButton')[0].setHidden(true);//保存
			}
		}
		
//		if (pagenum == 1) {
//			form.query('#submitButton')[0].setHidden(false);
//		}else if (pagenum > 0 && pageno == 1) {
//			form.query('#nextButton')[0].setHidden(false);
//		}else if (pageno < pagenum-1) {
//			form.query('#preButton')[0].setHidden(false);
//			form.query('#nextButton')[0].setHidden(false);
//		}else if (pageno == pagenum-1) {
//			form.query('#preButton')[0].setHidden(false);
//			form.query('#submitButton')[0].setHidden(false);
//		}
	},
	/**
	 * 字符型指标组件
	 * code_id 代码项号
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  单元格信息描述  
	 * text 指标名称
	 * value 指标后台保存数据
	 * placeHolder 提示信息
	 * value_view  指标前台显示数据
	 * lineStyle 组件样式
	 */
	setZiFuFieldItem:function(code_id,notEditable,commonName,text,value,placeHolder,value_view,lineStyle,item_length,isSingleLevel){
		var fieldCmp;
		var me = this;
		var currentid;
		//文本类型
		if(code_id!=0){//代码型指标
			if(!notEditable){//可编辑
				if(code_id == 'UN' || code_id == 'UM' || code_id == '@K'){
					//机构默认值情况下，级联 操作   wangb 20190530
					if(code_id == 'UN'){
						currentid = me.config.org=='UN'? '':me.config.org;
						me.B0110 = currentid;
					}else if(code_id == 'UM'){
						me.E0122 = me.B0110? me.B0110:'';
						currentid = me.E0122;
					}else{
						me.E01A1 = me.E0122? me.E0122:me.B0110? me.B0110:'';
						currentid = me.E01A1;
					}
					
					fieldCmp = {
							xtype:'codeselectfield',
							name:commonName+"`"+code_id+"`"+isSingleLevel,
							//id:code_id+"_1",
							label:text,
							realValue:value,
							labelWrap:true,labelWidth:'35%',autoSelect:false,usePicker:true,placeHolder:placeHolder,
							codesetid:code_id,readOnly:notEditable,
							value:value_view,
							onlySelectCodeset:true,
							nodeLevel:!isSingleLevel,
							labelCls:'labelTextCls',
							currentid:currentid,
							cls:'x-field-select x-component-outer x-field-input x-clear-icon x-code-field-input',//处理代码型不出 箭头问题
							style:lineStyle,
							selectedFn:this.connectionSelect,
							ctrltype:"0"
					};
				}else{
					fieldCmp = {
							xtype:'codeselectfield',
							name:commonName+"`"+code_id+"`"+isSingleLevel,
							label:text,
							realValue:value,
							labelWrap:true,labelWidth:'35%',autoSelect:false,usePicker:true,placeHolder:placeHolder,
							codesetid:code_id,readOnly:notEditable,
							value:value_view,
							onlySelectCodeset:true,
							nodeLevel:!isSingleLevel,
							labelCls:'labelTextCls',
							cls:'x-field-select x-component-outer x-field-input x-clear-icon x-code-field-input',//处理代码型不出 箭头问题
							style:lineStyle,
							//ctrltype:"0" 非机构代码 需要权限控制过滤  有效期和无效的代码
					};
				}
				
			}else{
				fieldCmp = {
						xtype:'templatetextfield',
						name:commonName,
						label:text,
						labelWrap:true,
						labelWidth:'35%',
						placeHolder:placeHolder,
						value:value_view,
						labelCls:'labelTextCls',
						readOnly:notEditable,
						style:lineStyle
				};
			}
		}else {
			fieldCmp = {
					xtype:'templatetextfield',
					name:commonName+"`"+''+"`"+item_length,
					value:value,
					label:text,
					labelWrap:true,
					labelWidth:'35%',
					placeHolder:placeHolder,
					clearIcon:true,
					readOnly:notEditable,
					//minHeight:30,
					labelCls:'labelTextCls',
					maxLength:item_length,
					style:lineStyle,
					listeners:{
						blur:function(){//过滤前后空格
							var value = this.getValue();
							var pattern = new RegExp("[~!#$%^&*()_+：|\<\>=,`\‘\’/?\-\]","g");//因为邮箱的缘故把@和.放开了
							var value = value.replace(pattern,'');
							value = value.replace(/[\{\}\[\]]/g,'');
							value = value.replace(/}/g,'');
							this.setValue(Ext.util.Format.trim(value));
						}
					}
			
			};
		}
		return fieldCmp;
	},
	/**
	 * 数值型指标组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  单元格信息描述  
	 * text 指标名称
	 * value 指标后台保存数据
	 * placeHolder 提示信息
	 * lineStyle 组件样式
	 */
	setNumFieldItem:function(notEditable,commonName,text,value,placeHolder,lineStyle){
		var fieldCmp = {
				xtype:'templatetextfield',
				stepValue:0.01,
				name:commonName,
				value:value,
				label:text,
				labelWrap:true,
				labelWidth:'35%',
				placeHolder:placeHolder,
				readOnly:notEditable,
				style:lineStyle,
				listeners:{
					keyup:function(t){
						var reg = /^[0-9]*$/;
						var val = t.getValue().replace(/[^0-9|.]/ig,"");
						t.setValue(val);
					}
				}
	
		};
		return fieldCmp;
	},
	/**
	 * 大文本型指标组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  单元格信息描述  
	 * text 指标名称
	 * value 指标后台保存数据
	 * placeHolder 提示信息
	 * lineStyle 组件样式
	 */
	setMaxTextFieldItem:function(notEditable,commonName,text,value,placeHolder){
		var fieldCmp = {
				xtype:'templatetextareafield',
				name:commonName,
				value:value,
				label:text,labelWrap:true,labelWidth:'35%',
				placeHolder:placeHolder,
				maxRows:6,clearIcon:true,
				labelCls:'template_label_area',
				readOnly:notEditable,
				autoCapitalize:true,
				style:'border-bottom:1px solid #ccc;font-size:16px;'
		};
		return fieldCmp;
	},
	/**
	 * 日期型指标组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  单元格信息描述  
	 * text 指标名称
	 * value 指标后台保存数据
	 * placeHolder 提示信息
	 * lineStyle 组件样式
	 * format 日期格式
	 */
	setDateFieldItem:function(notEditable,commonName,text,value,placeHolder,lineStyle,format){
		var fieldCmp;
		if(notEditable){
			fieldCmp = {
					xtype:'templatetextareafield',
					name:commonName,
					value:value,
					labelCls:'labelTextCls',
					label:text,labelWrap:true,labelWidth:'35%',
					placeHolder:placeHolder,clearIcon:true,readOnly:notEditable,style:lineStyle
			};
		}else{
//			format = field.format;
			if('yyyy.MM.dd'==format){
				format = 'Y.m.d';
			} else if('yyyy.MM.dd hh:mm'==format){
				format = 'Y.m.d H:i';
			} else if('yyyy'==format){
				format = 'Y';
			} else if('yyyy.MM'==format){
				format = 'Y.m';
			}else if('yyyy.MM.dd hh'==format){
				format = 'Y.m.d H';
			}
			fieldCmp = {
					xtype:'superdatetimepickerfield',
					name:commonName+"`"+format,
					value:value,
					labelCls:'labelTextCls',
					dateFormat:format,
					label:text,labelWrap:true,labelWidth:'35%',placeHolder:placeHolder,readOnly:notEditable,style:lineStyle

			};
		}
		return fieldCmp;
	},
	/**
	 * 附件型指标组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  单元格信息描述  
	 * text 指标名称
	 * value 附件数据集合
	 * item_id附件 类型号
	 * gridno 单元格号  
	 * state 状态  save 上一页，下一页切换保存临时数据状态 
	 * filesort  附件分类
	 */
	setFileFieldItem:function(notEditable,commonName,text,value,item_id,gridno,state,isrequired,filesort){
		var me = this;
		var fileItems = [];
		if(state != 'save'){
			for(var i = 0 ; i < value.length ; i++){
				fileItems.push({imgId:value[i].file_id,name:value[i].name,filetype:value[i].type,url:value[i].path});
			}
		}else{
			fileItems = value;
		}
		var html = '<span style="font-weight:bold!important;font-size: 16px!important;color:#4C4C4C;font-family:"微软雅黑";" >'+text+'</span>';
		if(isrequired){
			html = '<div><span style="font-weight:bold!important;font-size: 16px!important;color:#4C4C4C;font-family:"微软雅黑";" >'+text+'</span><span class="required">*</span></div>';
		}
		var fieldCmp = {
				xtype:'panel',
				width:'100%',
				
				layout:{
					type:'vbox'
				},
				items:[
				{
					xtype:'panel',
					width:'100%',
					layout:{
						type:'hbox',
						align:'center'
					},
					items:[
						{
							xtype:'component',
							margin:'0 0 0 6',
							width:'35%',
							html:html,
						},{
							xtype:'hiddenfield',
							name:commonName+"`"+item_id
						},{
							xtype:'photoselector',
							itemId:item_id+gridno,
							maxPhone:9,
							flex:1,
							fileType:'F',
							filesort:filesort,
							photos:fileItems,
							direction:'vertical', //跟layout 布局同步 hbox 值为horizontal，  vbox 值为vertical
							layout:'vbox',
//							notEditable:notEditable //是否可以 新增 删除附件
						 }
					]
				},{
					xtype:'component',
					style:'border-bottom:1px solid #ccc;'
				}
				]
		};
		return fieldCmp
	},
	/**
	 * 图片型指标组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  单元格信息描述  
	 * text 指标名称
	 * value 附件数据集合
	 * item_id 图片 类型号
	 * gridno 单元格号   
	 * state 状态  save 上一页，下一页切换保存临时数据状态 
	 */
	setPhotoFieldItem:function(notEditable,commonName,text,value,item_id,gridno,state,isrequired){
		var me = this;
		var photoItems = [];
		var tiemstamp = new Date().getTime();
		var html = '<span style="font-weight:bold!important;font-size: 16px!important;color:#4C4C4C;font-family:"微软雅黑";" >'+text+'</span>';
		if(isrequired){
			html = '<div><span style="font-weight:bold!important;font-size: 16px!important;color:#4C4C4C;font-family:"微软雅黑";" >'+text+'</span><span class="required">*</span></div>';
		}
		if(state != 'save'){
			for(var i = 0 ; i < value.length ; i++){
				var imgId = 'img'+tiemstamp+i;
				var name = value[i].file_name;
				var filetype = value[i].file_name.substring(value[i].file_name.indexOf('.')+1);
				var url = value[i].path;
				photoItems.push({imgId:imgId,name:name,filetype:filetype,url:url});
			}
		}else{
			photoItems = value;
		}
		var fieldCmp = {
				xtype:'panel',
				width:'100%',
				layout:{
					type:'vbox',
				},
				
				items:[{
					xtype:'panel',
					width:'100%',
					layout:{
						type:'hbox',
						align:'center'
					},
					items:[{
						xtype:'component',
						margin:'0 0 0 8',
						width:'35%',
						html:html,
					},{
						xtype:'hiddenfield',
						name:commonName+"`"+item_id,
						hidden:true
					},{
						xtype:'photoselector',
						itemId:item_id+gridno,
						flex:1,
						maxPhone:1,
						fileType:'P',
						photos:photoItems,
						direction:'vertical', //跟layout 布局同步 hbox 值为horizontal，  vbox 值为vertical
						layout:'vbox',
//						notEditable:false //是否可以 新增 删除附件
					 }]
				},{
					xtype:'component',
					style:'border-bottom:1px solid #ccc;'
				}]
		};
		return fieldCmp;
	},
	/**
	 * 子集型 指标组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * commonName  子集单元格信息描述  
	 * text 子集名称
	 * sub_data 子集指标 数据信息//set_id子集编号
	 * set_domain 子集指标集合
	 * value 子集数据集合
	 * gridno 单元格号   
	 * state 状态  save 上一页，下一页切换保存临时数据状态 
	 */
	setFieldSetItem:function(notEditable,commonName,text,sub_data,sub_domain,value,gridno,state,mustfillrecord){
		var me = this;
		var text = text.replace(/[\{\}]/g,'');//去除{}号  
		var html = text
		if(mustfillrecord =='true'){
			html = '<div><span  >'+text+'</span><span class="required">*</span></div>';
		}
		//子集标题
		var fieldSetTitleCmp = me.setFieldSetTitleCmp(html,sub_data.set_id);
		//子集记录
	    var fieldArrayCmp = me.setFieldArrayCmp(notEditable,text,sub_data,sub_domain,value,gridno,state);
		var fieldSetCmp = {
				xtype:'panel',
				width:'100%',
				layout:{
					type:'vbox',
					pack:'center'
				},
				items:[{
					xtype:'hiddenfield',
					name:commonName+"`"+mustfillrecord
				},fieldSetTitleCmp,{
					xtype:'panel',
					hidden:false,
					itemId:sub_data.set_id,
					items:fieldArrayCmp
				}]
		}
		return fieldSetCmp;
	},
	/**
	 * 子集记录 
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * text 子集名称
	 * sub_data 子集信息 //set_id 子集编号
	 * set_domain 子集指标集合
	 * value 子集数据集合
	 * gridno 单元格号   
	 * state 状态  save 上一页，下一页切换保存临时数据状态 
	 */
	setFieldArrayCmp:function(notEditable,text,sub_data,sub_domain,value,gridno,state){
		var me = this;
		var fieldRecordArrayCmp = [];
//		if(!state && value){
		if(value){//子集有数据情况
			for(var i = 0 ; i < value.length ; i++){
				if(value[i].state =='D'){
					continue;
				}
				//子集记录title
				var fieldTitleCmp = me.setFieldTitleCmp(notEditable,i+1,value[i].I9999/*,fieldRecordArrayCmp*/,sub_data.set_id);
				//子集记录指标
				var fieldCmp = me.setFieldCmp(notEditable,text,sub_data,sub_domain,value[i],gridno,state);
				
				var fieldRecordCmp = {
						xtype:'panel',
						width:'100%',
						layout:'vbox',
						items:[fieldTitleCmp,{
							xtype:'panel',
							items:fieldCmp
						}]
				}
				fieldRecordArrayCmp.push(fieldRecordCmp);
			}
		}else{
			//子集记录title
			var fieldTitleCmp = me.setFieldTitleCmp(notEditable,1,-1/*,fieldRecordArrayCmp*/,sub_data.set_id);
			//子集记录指标
			var fieldCmp = me.setFieldCmp(notEditable,text,sub_data,sub_domain,undefined,gridno,state);
				
			var fieldRecordCmp = {
					xtype:'panel',
					width:'100%',
					layout:'vbox',
					items:[fieldTitleCmp,{
						xtype:'panel',
						items:fieldCmp
					}]
				}
			fieldRecordArrayCmp.push(fieldRecordCmp);
		}
/*		}else if(state =='save' && value){
			for(var i = 0 ; i < value.length ; i++){
				if(value[i].state =='D'){
					continue;
				}
				//子集记录title
				var fieldTitleCmp = me.setFieldTitleCmp(notEditable,i+1,value[i].I9999,fieldRecordArrayCmp,set_id);
				//子集记录指标
				var fieldCmp = me.setFieldCmp(notEditable,set_id,sub_domain,value[i],gridno,state);
				
				var fieldRecordCmp = {
						xtype:'panel',
						width:'100%',
						layout:'vbox',
						items:[fieldTitleCmp,{
							xtype:'panel',
							items:fieldCmp
						}]
				}
				fieldRecordArrayCmp.push(fieldRecordCmp);
			}
		}*/
		//添加子集记录按钮组件
		var addfieldRecordBtn ={
			xtype:'panel',
			width:'100%',
			layout:'vbox',
			hidden:notEditable,
			//hidden:notEditable,
			style:'text-align:center;color:#2a92fa;margin-top:10px;padding-bottom:10px;border-bottom:1px solid #ccc;',
//			html:'<font style="font-size:16px;">继续添加'+text+'记录</font>',
			html:'<font style="font-size:16px;">继续添加</font>',
			listeners:{
				element:'element',
				tap:function(){
					var index = this.parent.getItems().length;
					me.addFieldCmp(notEditable,text,index,sub_data,sub_domain,this.parent,gridno);
				},
				scope:addfieldRecordBtn
			}
		}
		fieldRecordArrayCmp.push(addfieldRecordBtn);
		return fieldRecordArrayCmp;
	},
	/**
	 * 添加子集记录
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * text 子集名称
	 * set_id 子集编号
	 * set_domain 子集指标集合
	 * fieldRecordArrayCmp 子集组件
	 * gridno 单元格号   
	 */
	addFieldCmp:function(notEditable,text,index,sub_data,sub_domain,fieldRecordArrayCmp,gridno){
		var me = this;
		//添加子集记录title
		var fieldTitleCmp = me.setFieldTitleCmp(notEditable,index,'-1'/*,fieldRecordArrayCmp*/,sub_data.set_id);
		//添加子集记录内容
		var fieldCmp = me.setFieldCmp(notEditable,text,sub_data,sub_domain,undefined,gridno,undefined);
		
		var fieldRecordCmp = {
				xtype:'panel',
				width:'100%',
				layout:'vbox',
				items:[fieldTitleCmp,{
					xtype:'panel',
					items:fieldCmp
				}]
		}
		fieldRecordArrayCmp.insert(fieldRecordArrayCmp.getItems().length-1,fieldRecordCmp);
	},
	/**
	 * 子集记录 指标 组件
	 * notEditable 是否编辑  true 不可编辑  false 可编辑
	 * text 子集名称
	 * sub_data 子集数据 //set_idset_id 子集编号
	 * set_domain 子集指标集合
	 * fieldvalues 一条记录数据 
	 * gridno 单元格号   
	 * state 状态  save 上一页，下一页切换保存临时数据状态 
	 */
	setFieldCmp:function(notEditable,text,sub_data,sub_domain,fieldvalue,gridno,state){
		var me  = this;
		var lineStyle = 'font-size:16px;border-bottom:1px solid #ccc;line-height:24px;';
		var fieldArrayCmp = [];
		for(var i = 0 ; i < sub_domain.length ; i++){
			var fieldCmp = undefined;
//			var notEditable = true;
			var field = sub_domain[i];
//			var priv = field.priv; //// 0：无读写权限 1：读权限  2：写权限
//			var chgstate = field.chgstate;//// 1：变化前指标  2：变化后指标
//			if (priv == 0){
//				continue;
//			}
//			if(field.chgstate == 2){//是变化后指标
//				if (priv == 2) {//
//					notEditable = false; //编辑`
//				}
//			}
			var itemtype = field.item_type; //指标类型
			if(!itemtype || field.item_id =="attach"){//子集附件 没有item_type 属性
				itemtype = 'F';
				field.item_name = !field.item_name? "附件":field.item_name;
			}
			var code_id = field.code_id; ////指标关联代码
			var isSingleLevel = true;
			for(var s= 0;s<me.codes.length;s++){
				var codeitem = me.codes[s];
				if(codeitem.codesetid == code_id){
					isSingleLevel = codeitem.isSingleLevel;//代码指标  是否是多层级  多层级true 单层级 false 默认多层级
				}
			}
			if(!code_id)
				code_id = 0;
			var isRequired = '';
			var isrequired = false;
			if ("true" == field.fillable) {
				isRequired = '（必填）';
				isrequired = true;
			}
//			var value = field.value; //值
//			if (value == undefined) {
//				value = '';
//			}
			var placeHolder = ""; //输入框为空时显示的文字
			if(!notEditable){
				if((code_id!=0||"D"==itemtype)&&"M"!=itemtype){
					placeHolder = "请选择"+isRequired;
				}else{
					placeHolder = "请输入"+isRequired;
				}
			}
			var value = undefined;
			var	commonName = '';
			var view_value ='';
			if(fieldvalue){
				value = fieldvalue[field.item_id.toUpperCase()];
				if(code_id!=0){
					if(!state){
						value = value.replace(/=/g,":");
						var temp = Ext.decode(value);
						value = temp.realValue;
						view_value = temp.view_value;
					}else{
						var temp = value.split("`");
						view_value = temp[1];
						value = temp[0];
					}
				}
				//单元格id 子集编号  排序号   指标类型  指标编号   label值     为空时显示值    是否只读
				commonName = gridno+"`"+"child"+"`"+sub_data.set_id+"`"+fieldvalue.I9999+"`"+itemtype+"`"+field.item_id+"`"+text+'子集'+field.item_name+"指标"+"`"+placeHolder+"`"+notEditable+"`"+isrequired;
			}else{
				value = '';
				//单元格id 子集编号  排序号   指标类型  指标编号   label值     为空时显示值    是否只读
				commonName = gridno+"`"+"child"+"`"+sub_data.set_id+"`"+"-1"+"`"+itemtype+"`"+field.item_id+"`"+text+'子集'+field.item_name+"指标"+"`"+placeHolder+"`"+notEditable+"`"+isrequired;
			}
//			var subflag  = field.subflag;//1 是子集 0不是子集
			var fieldName = field.item_name;
			if ("A" == itemtype) {//字符型 和 代码型
				fieldCmp = me.setZiFuFieldItem(code_id,notEditable,commonName,fieldName,value,placeHolder,view_value,lineStyle,undefined,isSingleLevel);
			}else if("P" == itemtype){//照片
				fieldCmp = me.setPhotoFieldItem(notEditable,commonName,fieldName,value,field.item_id,gridno,state,isrequired);
			}else if("F" == itemtype){//附件
				fieldCmp = me.setFileFieldItem(notEditable,commonName,fieldName,value,field.item_id,gridno,state,isrequired,sub_data.file_type);
			}else if("N"==itemtype){//数值
				fieldCmp = me.setNumFieldItem(notEditable,commonName,fieldName,value,placeHolder,lineStyle);
			}else if("M"==itemtype){//大文本
				fieldCmp = me.setMaxTextFieldItem(notEditable,commonName,fieldName,value,placeHolder);
			}else if("D"==itemtype){//日期型
				fieldCmp = me.setDateFieldItem(notEditable,commonName,fieldName,value,placeHolder,lineStyle,'Y.m.d');
			}
			if(fieldCmp)
				if(itemtype=='D'||itemtype=='M'||itemtype=='A'||itemtype=='N'){
					if(isrequired&&!notEditable){
						fieldCmp.required = true;//如果是必须填 则不允许为空
					}else{
						fieldCmp.required = false;
					}
				}
				fieldArrayCmp.push(fieldCmp);
		}
		return fieldArrayCmp;
	},
	/**
	 * 子集记录title
	 * index 子集记录当前下标
	 * i9999 子集记录
	 * fieldArrayCamp 子集组件
	 */
	setFieldTitleCmp:function(notEditable,index,i9999/*,fieldArrayCmp*/,set_id){
		var me = this;
		var fieldTitleCmp = {
				xtype:'panel',
				width:'100%',
				layout:'vbox',
				items:[{
					xtype:'panel',
					layout:'hbox',
					height:40,
					items:[{
						xtype:'label',
						width:'100%',
						//height:20,
						style:'text-align:center;margin-top:10px;',
						html:'<font style="font-size:16px;">('+index+')</font>'
					},{
						xtype:'button',
						hidden:notEditable,
						right:20,
						border:0,
						i9999:index-1,//当前下标
						style:'margin-top:6px',
						html:'<font style="font-size:16px;color:#2a92fa">删除</font>',
						listeners:{
							tap:function(t){
								var fieldArrayCmp = me.query('#'+set_id)[0];
								fieldArrayCmp.removeAt(t.config.i9999);
								for(var i = t.config.i9999 ; i <fieldArrayCmp.getItems().length-1 ; i++){
									fieldArrayCmp.getItems().items[i].query('label')[0].setHtml('('+(i+1)+')');
									fieldArrayCmp.getItems().items[i].query('button')[0].config.i9999=i;
								}
							}
						}
					}]
				},{
					xtype:'component',
					style:'border-bottom:1px solid #ccc;line-height:24px',
				}]
		}
		return fieldTitleCmp;
	},
	
	/**
	 * 子集型指标 title
	 * text 子集名称
	 * set_id 子集编号 
	 */
	setFieldSetTitleCmp:function(text,set_id){
		var filedSetTitleCmp = {
				xtype:'panel',
				width:'100%',
				height:40,
				setid:set_id,
				layout:{
					type:'hbox',
					align:'center'
				},
				style:'margin-bottom:4px;background-color:#F8F8F8',
				items:[{
					xtype:'component',
					margin:'0 0 0 6',
					style:'font-size:16px;font-weight:bold;',
					html:text,
				},{
					xtype:'image',
					right:20,
					top:2,
					width:32,
					height:32,
					src:'/components/mobleTemplate/images/down.png'//点击即可隐藏
				}],
				listeners:{
					element:'element',
					tap:function(){
						if(this.child('image').getSrc().indexOf('up.png') != -1){
							this.child('image').setSrc('/components/mobleTemplate/images/down.png');
							this.parent.query('#'+this.config.setid)[0].show();
						}else{
							this.child('image').setSrc('/components/mobleTemplate/images/up.png');
							this.parent.query('#'+this.config.setid)[0].hide();
						}
					},scope:filedSetTitleCmp
				}
			};
		return filedSetTitleCmp;
	},
	// 单位，部门，岗位 级联
	// flag 标识   值为 empty表示 清空 当前机构和关联的下级机构
	connectionSelect:function(codesetid,realValue,flag){
		if(flag == 'empty'){
			realValue = realValue.replace('`',',');
			if(!codesetid=="UN"&&!codesetid=="UM"&&codesetid=="@K")
				return;
			if(codesetid=='UN'){
				var comp=Ext.ComponentQuery.query('codeselectfield');
				for(var i in comp){
					//级联 UM
					if('UM'==comp[i]._codesetid||'@K'==comp[i]._codesetid){
						changedCmp=comp[i];
						changedCmp.reloadTree(realValue.split(',')[0]);
						changedCmp.setValue('');
						changedCmp.realValue='';
						changedCmp.config.realValue='';
					}
				}
			}else if(codesetid == 'UM'){
				var comp=Ext.ComponentQuery.query('codeselectfield');
				for(var i in comp){
					if('@K'==comp[i]._codesetid){
						changedCmp=comp[i];
						changedCmp.reloadTree(realValue.split(',')[0]);
						changedCmp.setValue('');
						changedCmp.realValue='';
						changedCmp.config.realValue='';
					}
				}
			}
			return;
		}
		realValue = realValue.replace('`',',');
		if(!codesetid=="UN"&&!codesetid=="UM"&&codesetid=="@K")
			return;
		//单位
		if(codesetid=="UN"){
			var comp=Ext.ComponentQuery.query('codeselectfield');
			for(var i in comp){
				//级联 UM
				if('UM'==comp[i]._codesetid||'@K'==comp[i]._codesetid){
					changedCmp=comp[i];
					changedCmp.reloadTree(realValue.split(',')[0]);
					changedCmp.setValue('');
					changedCmp.setCurrentid(realValue.split(',')[0]);
					changedCmp.realValue='';
					changedCmp.config.realValue='';
//					if(Ext.getCmp("#"+changedCmp.getName()+"_1"))
//						Ext.getCmp("#"+changedCmp.getName()+"_1").setValue('');
				}
			}
		}
		//部门
		if(codesetid=="UM"){
			var comp=Ext.ComponentQuery.query('codeselectfield');
			for(var i in comp){
				//级联 @K
				if('@K'==comp[i]._codesetid){
					changedCmp=comp[i];
					changedCmp.reloadTree(realValue.split(',')[0]);
					changedCmp.setValue('');
					changedCmp.realValue='';
					changedCmp.setCurrentid(realValue.split(',')[0]);
					changedCmp.config.realValue='';
				}
				//级联 父机构
				if('UN'==comp[i]._codesetid){
					var parentUN='',parentUNDesc='';
					var vo = new HashMap();
					vo.put('codesetid','UM');
					vo.put('itemid',realValue);
					vo.put('searchlevel','UN');
					Rpc({functionId:'SYS0000002011',async:false,failure:function(){},success:function(res){
						var result = Ext.decode(res.responseText);
						var returnlist = result.returnlist;
						for(var i in returnlist){
							if(returnlist[i].codesetid=='UN'){
								parentUN=returnlist[i].codeitemid;
								parentUNDesc=returnlist[i].codeitemdesc;
								break;
							}
						}
					}},vo);
					changedCmp=comp[i];
					changedCmp.setValue(parentUNDesc);
					changedCmp.realValue=parentUN;
					changedCmp.config.realValue=parentUN;
				}
			}
		}
		//岗位
		if(codesetid=="@K"){
			var parentUN='',parentUNDesc='';
			var parentUM='',parentUMDesc='',UMlevelName;
			var UNflag=true,UMflag=true;
			var vo = new HashMap();
			vo.put('codesetid','@K');
			vo.put('itemid',realValue);
			vo.put('searchlevel','UN,UM,');
			vo.put('showLevelDept',true);
			Rpc({functionId:'SYS0000002011',async:false,failure:function(){},success:function(res){
				var result = Ext.decode(res.responseText);
				var returnlist = result.returnlist;
				for(var i in returnlist){
					if(!UNflag&&!UMflag)
						break;
					if(returnlist[i].codesetid=='UN'&&UNflag){
						parentUN=returnlist[i].codeitemid;
						parentUNDesc=returnlist[i].codeitemdesc;
						UNflag=false;
					}
					if(returnlist[i].codesetid=='UM'&&UMflag){
						parentUM=returnlist[i].codeitemid;
						if(returnlist[i].levelName&&returnlist[i].levelName!='')
							UMlevelName=returnlist[i].levelName;
							//parentUMDesc=returnlist[i].levelName;
						//else
							parentUMDesc=returnlist[i].codeitemdesc;
						UMflag=false;
					}
				}
			}},vo);
			var comp=Ext.ComponentQuery.query('codeselectfield');
			for(var i in comp){
				if('UN'==comp[i]._codesetid){
					changedCmp=comp[i];
					changedCmp.setValue(parentUNDesc);
					changedCmp.realValue=parentUN;
					changedCmp.config.realValue=parentUN;
				}
				if('UM'==comp[i]._codesetid){
					changedCmp=comp[i];
					//xus 18/3/14 部门多层级 
//					if(UMlevelName!=''&&Ext.getCmp(changedCmp.getName()+"_1")){
//						var levelDept=UMlevelName.substring(0,UMlevelName.lastIndexOf('/'));
//						levelDept=levelDept!=''?'<div style="margin-top:10px;">'+levelDept+'</div>':levelDept;
//						Ext.getCmp(changedCmp.getName()+"_1").setHtml(levelDept);
//					}
					changedCmp.setValue(parentUMDesc);
					changedCmp.realValue=parentUM;
					changedCmp.config.realValue=parentUM;
				}
			}
		}
	}
});