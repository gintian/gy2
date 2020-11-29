Ext.define('EHR.mobleTemplate.TemplateForm', {
	extend : 'Ext.form.Panel',
	xtype : 'templateForm',
	config : {
		items:[{
			id:'fieldsetid',
			xtype:'panel',
			defaults : {
				labelWidth:'30%',
				labelCls:'template_label',
				inputCls:'template_input'
			},
			items : []
		},{
			xtype:'container',
			docked:'bottom',
			id:'buttonContainer',
			layout:{
				type:'hbox',
				pack:'center',
				align:'center'
			},
			items:[
				{
					xtype:'button',
					itemId:'preButton',
					hidden:true,
					style:'background-color:#0099ff;',
					margin:'10 10 10 10',
					width: '40%',
					height: 40,
//					docked:'bottom',
					text: '<div width="100%" style="font-size:20px;color:#ffffff;text-align:center;">上一页</div>',
					listeners: {
						tap: function(btn) {
							//该交易只为校验与服务器之前通信是否正常  wangb 20190529
							var vo = new HashMap();
							vo.put('qrid','-1');
							Rpc({functionId:'SYS0000002005',success:function(resp){
								var me = btn.parent.parent;
								me.getScrollable().getScroller().scrollTo(0,0);
								me.saveData('pre')//保存当页数据
								me.showPanel.index--;
								if(me.showPanel.qrData.pageIndexList){
									me.showPanel.pageid =me.showPanel.qrData.pageIndexList[me.showPanel.index] ;
								}
								me.refreshPanel();//刷新页面
							}},vo);
						}
					}
				},{
					xtype:'button',
					itemId:'nextButton',
					hidden:true,
					style:'background-color:#0099ff',
					margin:'10 10 10 10',
					width: '40%',
					height: 40,
//					docked:'bottom',
					text: '<div width="100%" style="font-size:20px;color:#ffffff;text-align:center;">下一页</div>',
					listeners: {
						tap: function(btn) {
							//该交易只为校验与服务器之前通信是否正常  wangb 20190529
							var vo = new HashMap();
							vo.put('qrid','-1');
							Rpc({functionId:'SYS0000002005',success:function(resp){
								var me = btn.parent.parent;
								me.getScrollable().getScroller().scrollTo(0,0);
								if(!me.saveData('next')){
									return;
								}
								me.showPanel.index++;
								if(me.showPanel.qrData.pageIndexList){
									me.showPanel.pageid =me.showPanel.qrData.pageIndexList[me.showPanel.index] ;
								}
								me.refreshPanel();
							}},vo);
						}
					}
				},{
					xtype:'button',
					itemId:'submitButton',
					hidden:true,
					style: 'background-color:#0099ff;',
					margin:'10 10 10 10',
					width: '40%',
					height: 40,
//					docked:'bottom',
					text: '<div width="100%" style="font-size:20px;color:#ffffff;text-align:center;">提交</div>',
					listeners: {
						tap: function(btn) {
							var me = btn.parent.parent;
							var valueMap = new HashMap();
							if(!me.saveData('next')){
								return;
							}
							Ext.Msg.confirm('提示','确定提交？',function(opt){
								if(opt == 'yes'){
									if(Ext.Viewport)
	       								Ext.Viewport.setMasked({xtype:'loadmask',message:'提交中...',showAnimation:'slideIn'});
									var pfArray = new Array();
									var copyPfArray = new Array();
									var page_num = me.showPanel.qrData.page_num;
									for(var k = 0; k<page_num; k++){
										var j=0;
										var pagid = me.showPanel.qrData.pageIndexList[parseInt(k)];
										var saveParam = {};
										saveParam.tabname = me.showPanel.qrData.tabname;
										saveParam.tabid = me.showPanel.qrData.tabid;
										saveParam.ins_id = me.showPanel.qrData.ins_id;
										saveParam.taskid = me.showPanel.qrData.taskid;
										saveParam.object_id = me.showPanel.config.base+me.parent.parent.config.a0100;
										saveParam.fromMessage = '0';
										saveParam.info_type = 'normal';
										saveParam.page_no = pagid+'';
										var fieldList = new Array();
										var values = me.showPanel.qrSaveValueData.get(pagid);
										var isrequired = false;//默认不必填
										for ( var p in values ){
											var ps=new Array()
										  	ps = p.split("`");
										  	/*
											if(values[p]==null||values[p]=='null'){
												values[p] ="";
											}*/
											var value = values[p];
											if('sub' == ps[1]){//子集
												value = me.filterSubChild(ps[0],values).get('fieldValueArray');
												var subBlank = me.checksubrecord(ps[0],values,p,false);//单元格编号
												if(subBlank){
													continue;
												}
												var array = new Array();
												for(var i =0;i<value.length;i++){
				/*									var temp = Ext.encode(value[i]);
													array.push(temp);*/
													if(value[i].ATTACH){//处理子集附件

														var attachArray = new Array();
														for(var s=0;s<value[i].ATTACH.length;s++){
															var temp = new HashMap();
															var attach = value[i].ATTACH[s];
															temp.put('file_name',attach.imgId+"."+attach.filetype);
															temp.put('name',attach.name);
															temp.put('file_type',attach.filesort);
															temp.put('state','');
															attachArray.push(temp);
														}
														if(value[i].ATTACH && value[i].ATTACH.length > 0){
															pfArray.push(value[i].ATTACH);
															copyPfArray.push(value[i].ATTACH);
														}
														value[i].ATTACH = attachArray;
													}
													array.push(value[i]);

												}
//												value = '['+array.toString()+']'
//												me.compareSub(ps[0],page_num,array);
												value = array;
											}
											if('P' == ps[1]||'P' == ps[5]||'F' == ps[1] || 'F' ==ps[5]){
												if(value.length>0){
													pfArray.push(value);
													copyPfArray.push(value);
												}
											}
											if('N' == ps[1]){//如果是数值型转换成字符串  解决后台格式不统一 问题 long→string
												value = value+'';
											}
											if('P' == ps[1]||'P' == ps[5]){
//												var array = new Array();
//												for(var i = 0;i<value.length;i++){
												var temp = new HashMap();
													if(value.length>0){
														temp.put('file_name',value[0].imgId+'.'+value[0].filetype);
													}else{
														temp.put('file_name','');
													}
//													array.push(Ext.encode(temp));
//													array.push(temp);
//												}
//												value = '['+array.toString()+']';
												value = temp;
											}
											if('F' == ps[1] || 'F' ==ps[5]){

												var array = new Array();
												for(var i = 0;i<value.length;i++){
													var temp = new HashMap();
													temp.put('file_name',value[i].imgId+"."+value[i].filetype);
													temp.put('name',value[i].name);
												    temp.put('file_type',value[i].filesort);
													temp.put('state',"");
													temp.put('item_id',ps[6]);//个人附件或者公共附件
//													array.push(Ext.encode(temp));
													array.push(temp);
												}
//												value = '['+array.toString()+']';
												value = array;
											}
										    if(ps[1] =="A"){
										    	if(ps[5]&&ps[5]!=0){
										    		var temp = value.split('`');
										    		value = temp[0];
										    	}
										    }
										    if(ps[1]!='child'&&ps[0]!='put'&&ps[0]!='get'){
//										    	if(me.isChange(ps[0],ps[1],value)){//只校验普通指标值是否改变
										    		var jsonObejct = {};
										    		jsonObejct.gridno=ps[0];
										    		if(ps[1] =='F'){
										    			jsonObejct.item_id=ps[6];
										    		}
										    		jsonObejct.value = value;
										    		jsonObejct.pageid = pagid+'';
//										    		jsonObejct.pageid = ';
										    		fieldList.push(jsonObejct);
//										    		j++;
										    	}
//										    }


										}
//										saveParam+="]}";
										saveParam.fieldList = fieldList;
										var jsonStr = JSON.stringify(saveParam);
										valueMap.put(pagid,jsonStr);

									}
									//保存图片和附件数据到临时目录下
									if(copyPfArray.length>0){//存在上传图片，先执行图片上传在执行数据提交
										me.saveFileToTempdir(copyPfArray,valueMap);
									}else{//不存在图片上传，直接提交数据
										me.submitData(valueMap);
									}

								}else{
									return;
								}
							})


						}
					}
				},
			]

		},{
			xtype:'spacer',
			height:'10px'
		}]
	},initialize: function() {
		this.callParent();
		this.submitShowPanel();
		var me = this;
		me.showPanel = Ext.getCmp('qrcardinfo');
		me.qrSaveValueData = me.showPanel.qrSaveValueData;
		me.qrSaveFieldData = me.showPanel.qrSaveFieldData;
		me.saveOrgindata = me.showPanel.saveOrgindata;
	},
	/*提交处理图片或附件上传添加提示信息 */
	submitShowPanel:function(){
		var showPanel = Ext.create('Ext.Panel',{
				id:'showPanel',
				width:'100%',
				height:'100%',
				hidden:true,
				style:'position:absolute;background-color:black;opacity:0.5;-moz-opacity:0.5;-khtml-opacity: 0.5;top:0px;',
				renderTo:Ext.getBody()
		});
		var showText = Ext.create('Ext.Panel',{
				id:'showText',
				width:'80%',
				height:100,
				hidden:true,
				style:'position:absolute;top:40%;background-color:white;left:10%;padding-top:40px;border-radius:10px;z-index:99999;text-align:center;',
				html:'正在处理图片或附件，请等待...',
				renderTo:Ext.getBody()
		});
	},
	submitData:function(valueMap){
		var me =this;
		var map = new HashMap();
		map.put("transType","save");
		map.put("pageParam",valueMap);
		map.put("saveTypeFlag",0);//0表示一次性保存  1表示多次保存
		Rpc({functionId:'SYS0000002012',success:function(res){
				var result = Ext.decode(res.responseText);
    			if(result.flag=="false"){//保存模板数据失败前台提示
    				if(Ext.Viewport)
	       				Ext.Viewport.setMasked(false);
     				Ext.getCmp('showPanel').setHidden(true);
     				Ext.getCmp('showText').setHidden(true);
     				Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>',result.msg);
     				return;
    			}
				var param ={};
				param.tabid = me.showPanel.qrData.tabid;
				param.ins_id = me.showPanel.qrData.ins_id;
				param.taskid = me.showPanel.qrData.taskid;
				param.fromMessage = '0';
				param.content = '';
				param.opt = '1';
				param.actorid = '';
				var paramStr = JSON.stringify(param);
				var vo = new HashMap();
				vo.put("transType","deal");
				vo.put("param",paramStr);
				Rpc({functionId:'SYS0000002012',success:function(res){
							if(Ext.Viewport)
	       						Ext.Viewport.setMasked(false);
							var result = Ext.decode(res.responseText);
							Ext.getCmp('showPanel').setHidden(true);
							Ext.getCmp('showText').setHidden(true);
							if(result.flag=="true"){
									Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>','提交成功',function(){
											window.location.href = location.href+'&'+((new Date()).getTime()); //安卓手机 不刷新问题    wangb 20190728 bug 51074
									});
							}else{
									Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>',result.msg);
							}
				}},vo);
		}},map);
	},
	saveData:function(flag){//保存数据
		var me = this;
		var pageData = new HashMap();
		var pageid = me.showPanel.pageid;
		var qrData = me.showPanel.qrData;
		var orginData = me.showPanel.orgindata;
		var fieldValue = me.getValues(true, false);
		for (var p in fieldValue) {
			var ps = new Array();
			ps = p.split("`");
			if(ps[1] && (ps[1] == 'P' || ps[1] == "F")){//照片  附件
				var  itemid = '#'+ps[6]+ps[0];
				pageData.put(p,me.query(itemid)[0].fileList);
			}else if(ps[4] && (ps[4] == 'p' || ps[4] == 'F')){//子集附件 处理
				var itemid = '#'+ps[ps.length-1]+ps[0];
				var files = me.query(itemid);
				var fileArray = new Array();
				for(var i = 0 ; i < files.length ; i++){//子集多条记录 多个子集附件集合
					fileArray.push(files[i].fileList);
				}
				pageData.put(p,fileArray);
			}else{//普通指标
				pageData.put(p,fieldValue[p]);
			}
		}
		if(flag=='next'){//只有点击下一页时才进行必填项指标校验
			if(!me.check(pageData)){
				return false;
			}
		}
		me.qrSaveValueData.put(pageid, pageData);
		if (!me.qrSaveFieldData.get(pageid)) {
			me.qrSaveFieldData.put(pageid, qrData);
		}
		if (!me.saveOrgindata.get(pageid)) {
			me.saveOrgindata.put(pageid, orginData);
		}
		return true;
	},
	/**
	 * array 所有图片集合
	 * valueMap 模板保存数据
	 */
	saveFileToTempdir:function(array,valueMap){
		var me =this;
		var map = new HashMap();
		Ext.getCmp('showPanel').setHidden(false);
		var showText = Ext.getCmp('showText');
		showText.setHidden(false);
		var flag = false;
		for(var i = 0 ; i < array.length ; i++){
			var item = array[i];
			if(i == array.length - 1){
				flag = true;
			}
			me.uploadFile(valueMap,item,flag);
		}
		//me.submitData(valueMap);


	},
	/**
	 * valueMap 模板保存数据
	 * item 单个附件信息
	 * flag 标识  true 最后一个附件指标
	 */
	uploadFile:function(valueMap,item,flag){
		var me = this;
		for(var j = 0 ; j < item.length ; j++){
			(function(j,flag){
				//创建同步ajax对象
				var formData = new FormData();
				formData.append("fileId",item[j].imgId);
				formData.append("file",item[j].file);
				formData.append("VfsFiletype", "multimedia");
				formData.append("VfsModules", "RS");
				formData.append("VfsCategory", "other");
				$.ajax({
			    	type: "post",
			    	url: '/servlet/vfsservlet',
			    	data:formData,
			      	async: false,
			      	cache:false,
			      	processData: false,
			      	contentType: false,
			      	success: function(result) {
			    	  /*
		            	 返回数据参数如下：
		            	 1.success 上传文件成功
		            	 2.fail 上传文件失败
		            	 	fail`filetype 上传文件类型不正确
		            	 	fail`filesize 上传文件数据为空
		            	 	fail`file  上传文件错误，无法解析
		            	*/
						var breakFlag = false;
		            	var text = result;
						var textObj = Ext.decode(text);
						for(var key in valueMap){
							if (parseInt(key) !== NaN) {
								var oneValueInfo = Ext.decode(valueMap[key]);
								var fieldList =oneValueInfo.fieldList
								if (!fieldList) {
									continue;
								}
								for (var i = 0; i < fieldList.length; i++) {
									var values = fieldList[i].value;
									if (values && !(typeof values === 'string')) {
										if (Array.isArray(values)) {
											for (var k = 0; k < values.length; k++) {
												var value = values[k];
												if(value.ATTACH){//子集附件
													for (var l = 0; l < value.ATTACH.length; l++) {
														var oneAttach = (value.ATTACH)[l];
														if (oneAttach.file_name === item[j].imgId + "." + item[j].filetype) {
															oneAttach.fileId = textObj.fileid;
															breakFlag = true;
															valueMap[key] = JSON.stringify(oneValueInfo);
															break;
														}
													}
												}else{
													if (value.file_name === item[j].imgId + "." + item[j].filetype) {
														value.fileId = textObj.fileid;
														valueMap[key] = JSON.stringify(oneValueInfo);
														breakFlag = true;
														break;
													}
												}
												
												
												if(breakFlag){
													break;
												}
											}
											if(breakFlag){
												break;
											}
										} else {
											if (values.file_name === item[j].imgId + "." + item[j].filetype) {
												values.fileId = textObj.fileid;
												valueMap[key] = JSON.stringify(oneValueInfo);
												breakFlag = true;
												break;
											}
										}
									}
									if(breakFlag){
										break;
									}
								}
								if(breakFlag){
									break;
								}
							}
						}
	                    var errorDesc = "";
	                    if(text.indexOf('fail')>-1){
	                        if(Ext.Viewport){
	                            Ext.Viewport.setMasked(false);
	                        }
	                        if(text.indexOf('filetype')>-1){//文件类型错误
	                            errorDesc = item[j].name + zxdeclare.uploadFileTypeError;
	                        }else if(text.indexOf('filesize')>-1){//文件数据为空
	                            errorDesc = item[j].name + zxdeclare.uploadFileDataError;
	                        }else{//上传文件错误  无法解析
	                            errorDesc = item[j].name + zxdeclare.uploadFileError;
	                        }
	                        if(errorDesc){
	                            Ext.Msg.alert(zxdeclare.remain,errorDesc);
	                            uploadFlag = false;
	                        }
	                    }
	                    if(j == item.length -1 && flag){
	                    	me.submitData(valueMap);
	                    }
			      	},
			      	error: function(){}
				});
			})(j,flag);
		}
	},
	/*
	 * 刷新显示页面*/
	refreshPanel: function() {
		var me = this;
		me.getComponent('fieldsetid').removeAll(false,false);
		if (me.qrSaveFieldData.get(me.showPanel.pageid)) { //如果已经加载过表格数据
			var values = me.qrSaveValueData.get(me.showPanel.pageid);
			var data = me.qrSaveFieldData.get(me.showPanel.pageid);
			 me.parseValueCreatePanel(values,data);
		} else {
			me.template = Ext.getCmp('template');
			me.showPanel.loadData('next',me);
		}
	},
	nextTempalateDataPanel:function(){
		this.template.setFormItem(this.showPanel.qrData);
		this.showPanel.add(this.template);
	},
	parseValueCreatePanel:function(values,data){
		var me = this ;
		var fieldCmpArray = new Array();
		var template = Ext.getCmp('template');
		var lineStyle = 'font-size:14px;border-bottom:1px solid #ccc;line-height:24px;';
		//创建其余普通项
			for (var p in values) {
				var ps = new Array();
				var valueArray = new Array();
				var value_view;
				var subflag = 0;//子集标志位
				var set_id;//子集编号
				var addflag;//子集记录是否新增标志位
				var subchild = false;
				ps = p.split("`");
				/*
				if (values[p] == null || values[p] == 'null') {
					values[p] = "";
				}*/
				var fieldCmp = undefined;
				var gridno = ps[0];//单元格编号
				var itemtype = ps[1]; //指标类型
				var text = ps[2];//label
				var placeHolder = ps[3];
				var notEditable =true;
				var format ;//日期型格式
				if('sub' == ps[1]){//子集
					subflag = 1;
					set_id = ps[2];
					text = ps[3];
				}
				if('child'==ps[1]){
					subchild = true;//表示是子集的记录

				}
				if(ps[4] =="false"){
					notEditable = false;
				}
				if(!notEditable){
					format = ps[6];
				}
				var code_id = 0;
				var value = values[p]; //值
				var isrequried = ps[5];
				var isSingleLevel;
				if("A" == itemtype){
					if(ps[6]){
						code_id = ps[6]; ////指标关联代码
						if(code_id!=0){
							var temp =ps[7];
							if(temp=="true"){
								isSingleLevel = true;
							}else{
								isSingleLevel = false;
							}
						}
						valueArray = values[p].split("`");
						value = valueArray[0];
						value_view = valueArray[1];
						if(value_view=='undefined'){
							value_view='';
						}
					}
				}
				if (value == undefined) {
					value = '';
				}
				if(ps[1] !='child'){
				if ("A" == itemtype) { //文本类型
					fieldCmp = template.setZiFuFieldItem(code_id,notEditable,p,text,value,placeHolder,value_view,lineStyle,ps[7],isSingleLevel);
				}else if("N" == itemtype){//数值
					fieldCmp = template.setNumFieldItem(notEditable,p,text,value,placeHolder,lineStyle);
				}else if("M" == itemtype){
					fieldCmp = template.setMaxTextFieldItem(notEditable,p,text,value,placeHolder);
				}else if("D" == itemtype){
					fieldCmp = template.setDateFieldItem(notEditable,p,text,value,placeHolder,lineStyle,format);
				}else if("P" == itemtype){
					var temp = ps[5];
					var isrequired = false;
					if(temp=='true'){
						isrequired =true;
					}
					fieldCmp = template.setPhotoFieldItem(notEditable,p,text,value,ps[6],gridno,'save',isrequired);
				}else if("F" == itemtype){
					var itemid = ps[6];
					var temp = ps[5];
					var isrequired = false;
					if(temp=='true'){
						isrequired =true;
					}
					fieldCmp = template.setFileFieldItem(notEditable,p,text,value,itemid,gridno,'save',isrequired);
				}else if(subflag == 1){
					var sub_domain = me.getSubdomian(gridno,data);
					var param = me.filterSubChild(gridno,values);
					var subValues = param.get('fieldArray');
					var mustfillrecord = ps[5];
					var sub_data={};sub_data.set_id = set_id;
					fieldCmp = template.setFieldSetItem(notEditable,p,text,sub_data,sub_domain,subValues,gridno,'save',mustfillrecord);
				}
				if (fieldCmp) {
					if(isrequried=="true"&&!notEditable){
						fieldCmp.required = true;
					}else{
						fieldCmp.required = false;
					}
					fieldCmpArray.push(fieldCmp);
				}
				}
			}
			template.choseButton(fieldCmpArray,data);
	},
	/**
	 * 根据gridno筛选出对应的子集记录
	 */
	filterSubChild:function(gridno,values){
		var fieldArray = new Array();
		var fieldValueArray = new Array();
		var valueMap = new HashMap();
		var value = new HashMap();
		var param = new HashMap();
		for (var p in values) {
			if(p == 'put')
				continue;
			var ps = p.split('`');
			if(ps[0] != gridno)
				continue;
			if(ps[1] != 'child')
				continue;

			var itemid = ps[5];
			value.put(ps[3],'');

			var itemMap = new HashMap();
			if(itemid == 'attach'){//子集附件  单独处理
				itemMap.put(ps[3],values[p]);
				valueMap.put(itemid,itemMap);
			}else{
				valueMap.put(itemid,ps[3]+','+values[p]);
			}
		}
		var addValue = [];
		var addRealValue = [];
		for(var p in value){
			if(p == '-1'){//新增的
				var max = 0;
				for(var v in valueMap){//获得最多有几个值
					if(v == 'put' || v == 'get')
						continue;
					if(v == 'attach'){
						var vs = valueMap[v].get(p);
						if(!vs)
							continue;
						if(max < vs.length+1)
							max = vs.length +1;
					}else{
						var vs = valueMap[v].split(',');
						if(vs[0] != p)
							continue;
						if(max < vs.length){
							max = vs.length;
						}

					}
				}
				for(var i = 0 ; i < max-1 ; i++){//分成几个分组  即一个分组代表子集value中的一个value
					var timestamp = new Date().getTime();
					addValue[i] = new HashMap();
					addRealValue[i] = new HashMap();
					addValue[i].put('I9999','-1');
					addValue[i].put('state','');
					addValue[i].put('timestamp',timestamp);
					addRealValue[i].put('I9999','-1');
					addRealValue[i].put('state','');
					addRealValue[i].put('timestamp',timestamp);
				}
				for(var v in valueMap){
					if(v == 'put' || v == 'get')
						continue;
					if(v == 'attach'){
						var vs = valueMap[v].get(p);
						if(!vs)
							continue;
						for(var i=0 ; i < max-1 ; i++){
							if(vs[i]){
								addValue[i].put(v.toUpperCase(),vs[i]);
								addRealValue[i].put(v.toUpperCase(),vs[i]);
							}else{
								addValue[i].put(v.toUpperCase(),'');
								addRealValue[i].put(v.toUpperCase(),'');
							}
						}
					}else{
						var vs = valueMap[v].split(',');
						if(vs[0] != p)
							continue;
						for(var i = 1 ; i < max ; i++){
							if(vs[i]){
								var temp = vs[i].split("`");
								addValue[i-1].put(v.toUpperCase(),vs[i]);
								addRealValue[i-1].put(v.toUpperCase(),temp[0]);
							}
							else{
								addValue[i-1].put(v.toUpperCase(),'');
								addRealValue[i-1].put(v.toUpperCase(),'');
							}
						}
					}
				}
			}else{//原来有的
				var orginValueMap = new HashMap();
				var orginRealValueMap = new HashMap();
				orginValueMap.put('I9999',p);
				orginValueMap.put('state','');
				orginValueMap.put('timestamp',timestamp);
				orginRealValueMap.put('I9999',p);
				orginRealValueMap.put('state','');
				orginRealValueMap.put('timestamp',timestamp);
				if(p=='put'||p=='get'){
					continue;
				}
				for (var pp in values) {
					if(pp == 'put')
						continue;
					var ps = pp.split('`');
					if(ps[0] != gridno)
						continue;
					if(ps[1] != 'child')
						continue;
					if(ps[3]!=p)
						continue;
					var itemid = ps[5];
					orginValueMap.put(itemid.toUpperCase(),values[pp]);
					var temp = values[pp].split("`");
					orginRealValueMap.put(itemid.toUpperCase(),temp[0]);
				}
					fieldArray.push(orginValueMap);
					fieldValueArray.push(orginRealValueMap);
			}
		}
		fieldArray = fieldArray.concat(addValue);
		fieldValueArray = fieldValueArray.concat(addRealValue);
		param.put('fieldArray',fieldArray);
		param.put('fieldValueArray',fieldValueArray);
		return param;
	},
	/**
	 * 获取对应子集单元格的sub_domian信息
	 */
	getSubdomian:function(gridno,data){
		var fieldList = data.fieldList;
		for(var i = 0;i<fieldList.length;i++){
			var field = fieldList[i];
			if(field.gridno == gridno){
				return field.sub_domain
			}
		}
	},
	/**
	 * 检查指标的值是否发生改变
	 */
	isChange:function(gridno,itemtype,value){
		var me = this;
		var page_num = me.showPanel.qrData.page_num;
		for(var i = 0; i<page_num; i++){
			var index = me.showPanel.qrData.pageIndexList[parseInt(i)];
			if(!me.showPanel.qrSaveFieldData.get(index)){
				continue;
			}
			var fieldList = me.showPanel.qrSaveFieldData.get(index).fieldList;
			if('A' == itemtype||'N' == itemtype||'M' == itemtype||'D' == itemtype){
				for(var i = 0;i<fieldList.length;i++){
					var field = fieldList[i]
					if(gridno == field.gridno){
						if(value != field.value ){
							return true;
						}
					}
				}
			}else{
				return true;
			}
		}
	},
	/**
	 *
	 */
	compareSub:function(gridno,pagenum,valueArray){
		var me = this;
		var subValue = [];
		var temp = [];
		for(var i = 0;i<pagenum;i++){
			var fieldData = me.showPanel.saveOrgindata.get(i);
			var fieldList ='';
			if(fieldData){
				fieldList = fieldData.fieldList;
			}
			for (var i = 0; i < fieldList.length; i++) {
				var field  = fieldList[i];
				if(field.gridno = gridno&&field.subflag==1){
					subValue = field.value;
					break;
				}
			}
		}
		me.diff(valueArray,subValue);
	},
	diff:function(arr1,arr2){
		var temp  = new Array();
		var temp2 = new Array();
		var deleteSub = '';
		var deleteArray = new Array();
		if(arr1){
		 for(var i = 0;i<arr1.length;i++){
			temp.push(arr1[i].get("I9999"));
		 }
		}
		if(arr2){
		 for(var i =0;i<arr2.length;i++){
			temp2.push(arr2[i].I9999);
		 }
		}

		  var newArr = [];
		  var arr3 = [];
		  for (var i=0;i<temp.length;i++) {
		    if(temp2.indexOf(temp[i]) === -1)
		    	temp2.push(temp[i]);
		  }
		  var arr4 = [];
		  for (var j=0;j<temp2.length;j++) {
		    if(temp.indexOf(temp2[j]) === -1)
		      arr4.push(temp2[j]);
		  }
		   newArr = arr3.concat(arr4);
		  for(var i = 0;i<newArr.length;i++){
			  for(var j= 0;j<arr2.length;j++){
				  if(arr2[j].I9999 == newArr[i]){
					  arr2[j].state = 'D';
					  if(arr1.indexOf(arr2[j])==-1){
						  arr1.push(arr2[j]);
					  }
				  }
			  }
		  }
	},

	check:function(pagedata){
		var me = this;
		var flag = true;
		for(var name in pagedata){
			var ps = name.split("`");
			var notEditable = ps[4];
			var isrequired = ps[5];
			var itemtype = ps[1];
			var text = ps[2];
			var value = pagedata[name];
			if(itemtype !='child'&& itemtype!='sub'){//该指标为必填指标项
				if(!me.checkItem(itemtype,notEditable,isrequired,value,text)){
					flag =  false;
					break;
				}
			}else if(itemtype == 'child'){//校验子集指标必填指标项
				itemtype = ps[4];
				notEditable = ps[8];
				isrequired = ps[9];
				text = ps[6];
				if(!me.checkItem(itemtype,notEditable,isrequired,value,text)){
					flag = false;
					break;
				}
			}else if(itemtype == 'sub'){
				var mustfillrecord = ps[5];
				var gridno = ps[0];
				if(mustfillrecord == 'true'){
					if(!this.checksubrecord(gridno,pagedata,name,true)){
						flag = false;
					}
				}
			}
		}
		return flag;


	},
	checkItem:function(itemtype,notEditable,isrequried,value,text){
		//alert 弹窗点确定没有正常关闭处理
 		Ext.Msg.defaultAllowedConfig.showAnimation = false;
		Ext.Msg.defaultAllowedConfig.hideAnimation = false;
		if(notEditable=='false'&&isrequried=='true'){
			if(itemtype=='D'||itemtype=='A'||itemtype=='M'||'N' == itemtype){//附件和照片类型另做判断
				if(value=='`undefined'||!value||value=='`'){
					Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>','“ '+text+' ”'+'不能为空！');
					return false
				}
				if(Object.prototype.toString.call(value)=="[object Array]"){
					for(var s=0;s<value.length;s++){
						var signvalue = value[s];
						if(signvalue=='`undefined'||!signvalue||signvalue=='`'){
					Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>','“ '+text+' ”'+'不能为空！');
					return false
				}
					}}
			}else if(itemtype =='P'||itemtype =='F'){
				if(value.length<=0){
					Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>','“ '+text+' ”'+'不能为空！');
					return false;
				}
				for(var i =0 ; i<value.length;i++){
					if(value[i].length==0){
						Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>','“'+text+'”'+'不能为空！');
						return false;
					}
				}
			}
		}
		return true;
	},
	checksubrecord:function(gridno,pagedata,subname,check){//check:true 为校验子集必填项  false为查看此子集记录是否全为空
		var subarray = subname.split("`");
		var subtext = subarray[3].replace(/[\{\}]/g,'')
		var flag = true;
		var subflag =true;
		var count = 0;
		var valueFlag = 0;
		for(var name in pagedata){
			var array = name.split("`");
			var gridnotemp = array[0];
			var itemtype = array[1];
			if(itemtype == 'child'&&gridnotemp == gridno){//是当前的子集记录
				count++;
				var value = pagedata[name];
				var ps = name.split("`");
				itemtype = ps[4];
				var notEditable = ps[8];
				var isrequired = ps[9];
				var text = ps[6];
				if(itemtype=='D'||itemtype=='A'||itemtype=='M'||'N' == itemtype){//附件和照片类型另做判断
					if(value&&value!='`undefined'&&value!='`'){
						valueFlag++;
					}
				}else if(itemtype =='P'||itemtype =='F'){
					if(value.length>0&&value[0].length>0){//照片数组没有值【】 length=1 所以校验子数组
						valueFlag++;
					}
				}
			}
		}
		if(check){
			if(count == 0||valueFlag<=0){
				Ext.Msg.alert('<span style="font-size:15px;font-weight:bold;font-family:微软雅黑;">提示</span>','“ '+subtext+' ”'+'不能为空！');
				flag = false;
			}
		}else if(valueFlag>0){
			flag =false;//表示整个子集记录所有指标项值都为空
		}
		return flag;
	},
	getValues: function(enabled, all) {
        var fields = this.getFields(),
            values = {},
            isArray = Ext.isArray,
            field, value, addValue, bucket, name, ln, i;

        // Function which you give a field and a name, and it will add it into the values
        // object accordingly
        addValue = function(field, name) {
            if (!all && (!name || name === 'null') || field.isFile) {
                return;
            }

            if (field.isCheckbox) {
                value = field.getSubmitValue();
            } else {
                value = field.getValue();
            }


            if (!(enabled && field.getDisabled())) {
                // RadioField is a special case where the value returned is the fields valUE
                // ONLY if it is checked
                if (field.isRadio) {
                    if (field.isChecked()) {
                        values[name] = value;
                    }
                } else {
                    // Check if the value already exists
					var flag = false;
					for(var key in values){//子集获取数据时，数组长度与子集个数不一致处理  wangb 2018-12-06
						if(key == name){
							flag = true;
							break;
						}
					}
					if(!flag){//Ext 流程
                        bucket = values[name];
                        if (!Ext.isEmpty(bucket)) {
                            // if it does and it isn't an array, we need to make it into an array
                            // so we can push more
                            if (!isArray(bucket)) {
                                bucket = values[name] = [bucket];
                            }

                            // Check if it is an array
                            if (isArray(value)) {
                                // Concat it into the other values
                                bucket = values[name] = bucket.concat(value);
                            } else {
                                // If it isn't an array, just pushed more values
                                bucket.push(value);
                            }
                        } else {
                            values[name] = value;
                        }
					}else{//子集获取数据格式不对处理
                        if(isArray(values[name])){
                        	values[name].push(value);
						}else{
                        	values[name] = [values[name],value];
						}
					}


                }
            }
        };

        // Loop through each of the fields, and add the values for those fields.
        for (name in fields) {
            if (fields.hasOwnProperty(name)) {
                field = fields[name];

                if (isArray(field)) {
                    ln = field.length;
                    for (i = 0; i < ln; i++) {
                        addValue(field[i], name);
                    }
                } else {
                    addValue(field, name);
                }
            }
        }
        return values;
    }
	});
