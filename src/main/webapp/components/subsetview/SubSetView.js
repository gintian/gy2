Ext.define('EHR.subsetview.SubSetView',{
	extend:'Ext.panel.Panel',
	requires:['EHR.tableFactory.TableBuilder','EHR.subsetview.SetItemEditor','EHR.commonQuery.CommonQuery'],
	config:{
		setName:'',
		nbase:'',
		currentObject:'',
		privType:'',
		subModuleId:'',//必填
		/**
		 * ctrltype
		 * 过滤类型  如果codesetid 为机构（UN、UM、@K）
		 *         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
		 *         默认值为1
		 *  如果是普通代码类 
		 *         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
		 */
		ctrltype:'1',
		nmodule:'0',
		functionPriv :{},/* 按菜单权限显示按钮
							{ 
							  add:'xxxxx', 		//新增
							  update:'xxxxx',	//修改
							  batchUpdate:'xxxxx' //批量修改
							  del:'xxxxxx',		//删除
							  exp:'xxxxxx'	//导出
							}
		 				*/
		funcParam:{},/*权限指标信息（默认按B0110查询）
						{
							table:A01,
							item:B0110
						}
		 				*/
		buttonList:[],/*按钮栏按钮配置  [{text:'导入',functype:'subSetViewTableGlobal.createSetView'},{...},...]*/
		columnRender:undefined,
		extraColumns:undefined,
		lockColumns:undefined, //锁列参数  类型：'A0109,E0122......'
		filterColumn:undefined,//过滤参数  类型：'A0109' 只支持单列
		tableTitle:'',         //自定义标题，不设置时为子集名称
		schemeItemKey:'',      //栏目设置添加指标 key
		queryItem:'',          //查询指标   不配置只查询姓名，配置了走配置的指标 例：'A0101,A0109...'
		isScheme:true,		   //是否显示栏目设置
		personPickerNbase:undefined, //新增人员Nbase 若为空则获取nbase属性
		pickerIsPrivExpression:true,  //人员选择组件是否受权限控制
		publicPlan:false, //显示共有方案设置(栏目设置：保存为默认方案功能)
		customFilterCond:undefined, //自定义过滤条件
		defaultQueryFields:undefined  //筛选条件中默认查询指标
	},
	constructor:function(){
		window.subSetViewTableGlobal=this;
		this.callParent(arguments);
	},
	initComponent:function(){
		this.callParent();
		if(this.getSubModuleId()==''){
			Ext.MessageBox.alert('提示信息','请配置表格控件的subModuleId！');
			return;
		};
		if(this.getCurrentObject()==''&&this.getPrivType()==''){
			Ext.MessageBox.alert('提示信息','权限配置有误！');
			return;
		}
		this.loadData();
	},
	//加载数据
	loadData:function(){
		var me=this;
		if(!this.judgeIfLoad())
			return;
		var map = new HashMap();
		map.put("setName",this.getSetName());
		map.put("nbase",this.getNbase());
		map.put("currentObject",this.getCurrentObject());
		map.put("privType",this.getPrivType());
		map.put("subModuleId",this.getSubModuleId());
		map.put("functionPirv",this.getFunctionPriv());
		map.put("title",this.getTableTitle());
		map.put("lockColumns",this.getLockColumns());
		map.put("filterColumn",this.getFilterColumn());
		map.put("schemeItemKey",this.getSchemeItemKey());
		map.put("queryItem",this.getQueryItem());
		map.put("isScheme",this.getIsScheme());
		map.put("funcParam",this.getFuncParam());
		map.put("buttonList",this.getButtonList());
		map.put("customFilterCond",this.getCustomFilterCond());
	    Rpc({functionId:'ZJ100000250',success:me.beforeBuildComp,scope:me},map);
	},
	//判断是否加载数据
	judgeIfLoad:function(){
		return this.getSetName()!=''
	},
	//传入创建表格之前的表格构建参数
	beforeBuildComp:function(res){
		var me=this;
		var map	 = Ext.decode(res.responseText);
		var conditions=map.tableConfig;
		var tableConfig = Ext.decode(conditions);
		//显示共有方案设置
		tableConfig['showPublicPlan']=me.getPublicPlan();
//		tableConfig.handlerScope=me;
		this.table = Ext.create('EHR.tableFactory.TableBuilder',tableConfig);
		
		//xus 19/9/5  添加筛选条件
		var commonQuery = Ext.create("EHR.commonQuery.CommonQuery",{
	        subModuleId:this.getSubModuleId(),
	        ctrltype:this.getCtrltype(),
	        nmodule:this.getNmodule(),
	        defaultQueryFields:this.getDefaultQueryFields(),
	        optionalQueryFields: map.optionalQueryFields,
	        beforeFieldRender:function(field){
//	        	if(field.itemid=='Z0103'){
//	        		field.codeData=<%=batchQuery%>;
//	        	}
//	        	if(field.itemid=='z0319'){
//	        		field.codeData=<%=appStatus%>;
//	        	}
	        },
	         doQuery:function(items){
	        	var map = new HashMap();
	 			map.put("setName",subSetViewTableGlobal.getSetName());
	 			map.put("nbase",subSetViewTableGlobal.getNbase());
	 			map.put("currentObject",subSetViewTableGlobal.getCurrentObject());
	 			map.put("privType",subSetViewTableGlobal.getPrivType());
	 			map.put("subModuleId",subSetViewTableGlobal.getSubModuleId());
	 			map.put("functionPirv",subSetViewTableGlobal.getFunctionPriv());
	 			map.put("title",subSetViewTableGlobal.getTableTitle());
	 			map.put("isScheme",this.getIsScheme());
	 			map.put("shiftItems", items);
	         	Rpc({functionId:'ZJ100000250',async : false,success:subSetViewTableGlobal.table.dataStore.reload()}, map);
	         },
	         scope:this,
//	         fieldPubSetable:<%=hasTheFunction %>
	    	});
		this.table.insertItem(commonQuery,0);
		
		var tablePanel = subSetViewTableGlobal.table.getMainPanel();
		new Ext.Viewport( {
			id:"subSetPort",
			layout : "fit",
			items:[tablePanel]
		});
//		subSetViewTableGlobal.table.dataStore.load();
//		console.log(subSetViewTableGlobal.table.dataStore.load());
//		subSetViewTableGlobal.table.getStore().load();
		subSetViewTableGlobal.loadComplete();
	},
//	setEditBtn:function(A0101,cell,record){
//		console.log(arguments);
//		var key='';
//		if(record.data.a0100_e)
//			key = record.data.a0100_e;
//		else if(record.data.b0110_e)
//			key = record.data.b0110_e;
//		else if(record.data.e0122_e)
//			key = record.data.e0122_e;
//		return "<a href='javascript:subSetViewTableGlobal.editSetView(\""+key+"\");' >"+A0101+"</a>";
//	},
	//修改
	editSetView:function(key){
		var me = this;
		var selections = subSetViewTableGlobal.table.tablePanel.getSelectionModel().getSelection();
		if(selections.length==0){
			Ext.MessageBox.alert('提示信息','未选中数据！');
			return;
		}else if(selections.length>1){
			Ext.MessageBox.alert('提示信息','修改只会修改单条记录！请重新选择');
			return;
		}
		var nbase='';
		if(selections[0].data.nbase){
			nbase=selections[0].data.nbase;
		}
		var key='',dataIndex=1;
		if(selections[0].data.a0100_e)
			key = selections[0].data.a0100_e;
		else if(selections[0].data.b0110_e)
			key = selections[0].data.b0110_e;
		else if(selections[0].data.e0122_e)
			key = selections[0].data.e0122_e;
		if(selections[0].data.dataIndex)
			dataIndex=selections[0].data.dataIndex;
		else if(selections[0].data.i9999)
			dataIndex=selections[0].data.i9999;
		var window=Ext.create('Ext.window.Window', {
			    title: '修改',
			    height: 500,
			    width: 700,
			    layout: 'fit',
			    modal:true,
			    draggable:{
			    	listeners:{
			    		//拖动时将picker收回，否则位置错乱
			    		mousedown:function(){
			    			var picker = this.comp.query('pickerfield');
			    			for(var i=0;i<picker.length;i++){
			    				picker[i].collapse();
			    			}
			    			return true;
			    		}
			    	}
			    },
			    items:{
			    	xtype:'setitemeditor',
					setName:subSetViewTableGlobal.getSetName(),
//					nbase:subSetViewTableGlobal.getNbase(),
					nbase:nbase,
					currentObject:key,
					dataIndex:dataIndex,
					ctrltype:subSetViewTableGlobal.getCtrltype(),
					nmodule:subSetViewTableGlobal.getNmodule(),
					oncomplete:function(action){
						subSetViewTableGlobal.saveComplete(me);
						  if(action=='ok'){
							  Ext.MessageBox.alert('提示信息','保存成功');
							  subSetViewTableGlobal.table.dataStore.reload();
					    	  window.close();
					      }
					      else if(action=='cancel')
					    	  window.close();
					},
					listeners:{
						beforedestroy:function(panel){
							var picker = Ext.getCmp('person_picker_single_view');
							if(picker)
								picker.close();
						},
						
						beforesave:function(e,value,oldValue){
							return subSetViewTableGlobal.fireEvent("beforesave",e,value,oldValue);
						},
						scope:me
					}
				}
			}).show();
	},
	//批量修改
	batchUpdate:function(){
		var setName = subSetViewTableGlobal.setName;
		var selections = subSetViewTableGlobal.table.tablePanel.getSelectionModel().getSelection();
		// 增加回调callback
		Ext.require('EHR.subsetview.BatchUpdate',function(){
			Ext.create("EHR.subsetview.BatchUpdate",{setName:setName,nbase:subSetViewTableGlobal.nbase,selections:selections
				,callback:function(){subSetViewTableGlobal.table.dataStore.reload();}});
		});
	},
	//新增
	createSetView:function(){
		var me = this;
			var window=Ext.create('Ext.window.Window', {
			    title: '新建',
			    height: 500,
			    width: 700,
			    layout: 'fit',
			    modal:true,
			    draggable:{
			    	listeners:{
			    		//拖动时将picker收回，否则位置错乱
			    		mousedown:function(){
			    			var picker = this.comp.query('pickerfield');
			    			for(var i=0;i<picker.length;i++){
			    				picker[i].collapse();
			    			}
			    			//关闭人员组件
			    			if(Ext.getCmp("person_picker_single_view")){
			    				Ext.getCmp("person_picker_single_view").close();
			    			}
			    			return true;
			    		}
			    	}
			    },
			    items:{
			    	xtype:'setitemeditor',
					setName:subSetViewTableGlobal.getSetName(),
					nbase:subSetViewTableGlobal.getNbase(),
					ctrltype:subSetViewTableGlobal.getCtrltype(),
					nmodule:subSetViewTableGlobal.getNmodule(),
					personPickerNbase:subSetViewTableGlobal.getPersonPickerNbase()?subSetViewTableGlobal.getPersonPickerNbase():subSetViewTableGlobal.getNbase(),
					pickerIsPrivExpression:subSetViewTableGlobal.getPickerIsPrivExpression(),
					oncomplete:function(action){
						subSetViewTableGlobal.saveComplete(me);
					      if(action=='ok'){
					    	  Ext.MessageBox.alert('提示信息','保存成功');
					    	  subSetViewTableGlobal.table.dataStore.reload();
					    	  window.close();
					      }
					      else if(action=='cancel')
					    	  window.close();
					      else if(action=='continue'){
					    	  Ext.MessageBox.alert('提示信息','保存成功');
					    	  subSetViewTableGlobal.table.dataStore.reload();
					      }
					},
					listeners:{
						beforedestroy:function(panel){
							var picker = Ext.getCmp('person_picker_single_view');
							if(picker)
								picker.close();
						},
						beforesave:function(e,value){
							return subSetViewTableGlobal.fireEvent("beforesave",e,value);
						},
						scope:me
					}

				}
			}).show();
	},
	//删除
	delSetView:function(){
		var me=this;
		var selections = subSetViewTableGlobal.table.tablePanel.getSelectionModel().getSelection();
		if(selections.length==0){
			Ext.MessageBox.alert('提示信息','未选中数据！');
			return;
		}
		Ext.Msg.confirm("提示","确定删除选中数据？",
				function(btn){
			if(btn=='no')
				return;
			var keys='';
			var dataIndexs='';
			var dataInfo=[];
			for(var i=0;i<selections.length;i++){
				if(selections[i].data.a0100_e)
					key = selections[i].data.a0100_e;
				else if(selections[i].data.b0110_e)
					key = selections[i].data.b0110_e;
				else if(selections[i].data.e0122_e)
					key = selections[i].data.e0122_e;
				if(selections[i].data.dataIndex)
					dataIndex = selections[i].data.dataIndex;
				else if(selections[i].data.i9999)
					dataIndex = selections[i].data.i9999;
				dataInfo.push({'key':key,'dataIndex':dataIndex});
			}
			var map = new HashMap();
			map.put("setName",subSetViewTableGlobal.getSetName());
			map.put("nbase",subSetViewTableGlobal.getNbase());
			map.put("currentObject",subSetViewTableGlobal.getCurrentObject());
			map.put("privType",subSetViewTableGlobal.getPrivType());
			map.put("subModuleId",subSetViewTableGlobal.getSubModuleId());
			map.put("functionPirv",subSetViewTableGlobal.getFunctionPriv());
			map.put("type","del");
			map.put("dataInfo",dataInfo);
			map.put("title",subSetViewTableGlobal.getTableTitle());
		    Rpc({functionId:'ZJ100000250',async : false,success:subSetViewTableGlobal.table.dataStore.reload(),scope:me},map);
			
		})
	},
	//增加附件
	addAttachment:function(A0101,cell,record,row){
		var key='',dataIndex=1,nbase='';
		if(record.data.a0100_e)
			key = record.data.a0100_e;
		else if(record.data.b0110_e)
			key = record.data.b0110_e;
		else if(record.data.e0122_e)
			key = record.data.e0122_e;
		if(record.data.dataIndex)
			dataIndex=record.data.dataIndex;
		else if(record.data.i9999)
			dataIndex=record.data.i9999;
		if(record.data.nbase)
			nbase=record.data.nbase;
		var str="";
		if(record.data.imgpic=='true')
			str="<div align='center'><img id='attachment_"+row+"' src='/images/subsetview/attachment.png' onclick='javascript:subSetViewTableGlobal.showAttachment(\"attachment_"+row+"\",\""+key+"\",\""+dataIndex+"\",\""+nbase+"\");' /></div>";
		return str;
	},
	//显示附件框
	showAttachment:function(row,key,dataIndex,nbase){
		var map=new HashMap();
		map.put('setName',this.getSetName());
//		map.put('nbase',this.getNbase());
		map.put('nbase',nbase);
		map.put('currentObject',key);
		map.put('dataIndex',dataIndex);
		Rpc({
			functionId : 'ZJ100000251',
			async : false,
			success:function(res){
				var respon = Ext.decode(res.responseText);
				var fileList = respon.fileList;
//				if(fileList.length==0)
//					return;
				var array=[];
				var list=[];
				for(var i=0;i<fileList.length;i++){
					array.push(fileList[i].filepath,fileList[i].fileext,fileList[i].srcfilename,fileList[i].filename);
					var src='';
					
					if(fileList[i].fileext=='.jpg'||fileList[i].fileext=='.JPG'||fileList[i].fileext=='.jpeg'||fileList[i].fileext=='.png'||fileList[i].fileext=='.bmp'){
						src="/images/img.png";
					}else if(fileList[i].fileext=='.doc'||fileList[i].fileext=='.docx'){
						src = "/images/word.png";
					}else if(fileList[i].fileext=='.xls'||fileList[i].fileext=='.xlsx'){
						src = "/images/excell.png";
					}else if(fileList[i].fileext=='.ppt'||fileList[i].fileext=='.pptx'){
						src = "/images/ppt.png";
					}else if(fileList[i].fileext=='.pdf'){
						src = "/images/PDF.png";
					}else if(fileList[i].fileext == ".zip" || fileList[i].fileext == ".rar")
		             	src = "/images/zip.png";
					else if(fileList[i].fileext == ".txt")
		             	src = "/images/txt.png";
					else{
						src='/images/othertype.png';
					}
					list.push({'src':src ,'desc':fileList[i].srcfilename  ,'Fn':'subSetViewTableGlobal.downloadFile("'+fileList[i].fileid+'")',scope:subSetViewTableGlobal  ,params:array  });
				}
				Ext.require('EHR.photoViewer.PhotoViewer',function(){
					subSetViewTableGlobal.photoView = new EHR.photoViewer.PhotoViewer({
						dataList:list,
						connEle:row,
						config:{
							ImgNum:4,
							floatImgWidth:40,//展示图片宽
							floatImgHeight:40//展示图片高
						 }
					})
				});
			}
		}, map);
	},
	//下载附件
	downloadFile:function(fileid){
		//下载
		Ext.Msg.confirm('提示信息','确认下载此文件？',function(op){
			if(op == 'yes'){
				var win=open("/servlet/vfsservlet?fileid="+fileid+"&fromjavafolder=true");
				// var win=open("/servlet/DisplayOleContent?openflag=true&filePath="+filepath);
			}else{
				return;
			}
		});
	},
	//加载回调
	loadComplete:function(){
	},
	//保存回调
	saveComplete:function(me){
	}
	
	
})