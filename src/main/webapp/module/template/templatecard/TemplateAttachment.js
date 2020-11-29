/**
 * 人事异动-人事异动卡片-公共、个人附件
 */
Ext.define('TemplateCardUL.TemplateAttachment',{
	extend:'Ext.panel.Panel',
	requires:["SYSF.FileUpLoad"],
	isDbUrl:false,//用于区分是否从代表进来的，根据url是否含有etoken参数进行判断
    constructor:function(config){
		var templateAttachment_me = this;
		templateAttachment_me.templPropety = config.map;
		templateAttachment_me.uniqueId = templateAttachment_me.templPropety.uniqueId;
		if(templateAttachment_me.templPropety.object_id){
			//生成唯一id，将组件注册到 ext组件管理对象中，方便后面调用
			this.templateAttachment_id = Ext.id(this,"ext-templateattachment");
			Ext.ComponentManager.register(this);
			templateAttachment_me.multimedia_maxsize = '50m';
			templateAttachment_me.init();
		}else{
			var gridId = "attachmentgrid_" + templateAttachment_me.uniqueId;
			var attachment_grid = Ext.getCmp(gridId);
			if(attachment_grid){
				//偶尔出现p的destroy方法丢失,暂时原因未知
				try{
					attachment_grid.destroy();
				}catch(e){}
			}
		}
		/**获取当前url链接*/
		var url = window.location.search;
		var params= getRequest(url);
		/**判断url是否含有etoken参数*/
		if(params.etoken){
			templateAttachment_me.isDbUrl=true;
		}
    },
    
    init:function(){
    	var templateAttachment_me = this;
    	var rwPriv = templateAttachment_me.templPropety.rwPriv;//2可编辑，1只读
		var ins_id = templateAttachment_me.templPropety.ins_id;
		if(rwPriv == "2"){
			templateAttachment_me.uploadattach = "1";
		}else{
			templateAttachment_me.uploadattach = "0";
		}
		
		var map = new HashMap();
    	map.put("ins_id",ins_id);	
		map.put("tabid",templateAttachment_me.templPropety.tabid);		
		map.put("object_id",templateAttachment_me.templPropety.object_id);		
		map.put("attachmenttype",templateAttachment_me.templPropety.attachmenttype);	
		map.put("module_id",templateAttachment_me.templPropety.module_id);//传递模块号：9是自助申请。
		map.put("rwPriv",rwPriv);
		map.put("uniqueId",templateAttachment_me.uniqueId);
		map.put("task_id",templateMain_me.templPropety.task_id);	
		map.put("allNum",templateTool_me.getTotalCount());
	//	map.put("sp_batch",templateAttachment_me.templPropety.sp_batch); //20160905 dengcan 无用了，ins_id表示当前选中记录的单号
		var functionid = "MB00006014";
		var isarchive = getTemplPropetyOthParam("isarchive");
		if(isarchive=='0'){
			functionid = "MB00008006";
			var record_id = getTemplPropetyOthParam("record_id");
			var archive_year = getTemplPropetyOthParam("archive_year");
			var archive_id = getTemplPropetyOthParam("archive_id");
			map.put("record_id",record_id);
			map.put("archive_year",archive_year);
			map.put("archive_id",archive_id);
		}
	    Rpc({functionId:functionid,async:false,success:function(form){
	    	var result = Ext.decode(form.responseText);	
			if(result.succeed){
				templateAttachment_me.multimedia_maxsize = result.multimedia_maxsize;
				templateAttachment_me.showRefreshBtn=result.showRefreshBtn; //是否显示刷新按钮。
				templateAttachment_me.initGrid(result.attachmentList); 
	        }else{
	        	var message = result.message;
				if(message&&message.indexOf("拆分审批")!=-1){
					templateTool_me.checkSpllit(message);
				}else
					Ext.showAlert(result.message);
	        } 
	    },scope:templateAttachment_me},map);
    },
    
    //初始化表格
    initGrid:function(attachmentList){
		var templateAttachment_me = this;
		var closebutton = undefined;
		var toolbar = Ext.widget({
			xtype:'toolbar',
			dock: 'bottom'
			});
		var store = Ext.create('Ext.data.Store', {
		    fields:['file_id', 'attachmentname','sortname', 'ext','ins_id','fullname','create_time','candelete']
		});
		Ext.each(attachmentList,function(record, index){
					store.insert(index,record);
		})
		var gridId = "attachmentgrid_" + templateAttachment_me.uniqueId;
		var attachment_grid = Ext.getCmp(gridId);
		if(attachment_grid){
			//偶尔出现p的destroy方法丢失,暂时原因未知
			try{
				attachment_grid.destroy();
			}catch(e){}
		}
		if(templateAttachment_me.uploadattach == "1"){
			var uploadbutton =Ext.widget({
						xtype:"button",
						text:MB.LABLE.attachment,//上传附件
						handler:function(){
							var map = new HashMap();
					    	map.put("ins_id",templateAttachment_me.templPropety.ins_id);	
							map.put("tabid",templateAttachment_me.templPropety.tabid);		
							map.put("object_id",templateAttachment_me.templPropety.object_id);		
							map.put("attachmenttype",templateAttachment_me.templPropety.attachmenttype);//传递模块号：9是自助申请。
							map.put("type","upload");
							map.put("uniqueId",templateAttachment_me.uniqueId);
							map.put("allNum",templateTool_me.getTotalCount());
							map.put("task_id",templateMain_me.templPropety.task_id);	
					//		map.put("sp_batch",templateAttachment_me.templPropety.sp_batch); //20160905 dengcan 无用了，ins_id表示当前选中记录的单号
						    Rpc({functionId:'MB00006015',async:false,success:function(form){
						    	var result = Ext.decode(form.responseText);	
								if(result.succeed){
									templateAttachment_me.showUploadFile(result);
						        }else{
						        	var message = result.message;
									if(message&&message.indexOf("拆分审批")!=-1){
										templateTool_me.checkSpllit(message);
									}else
										Ext.showAlert(result.message);
						        } 
						    },scope:templateAttachment_me},map);
						}
					});
			var deleteButton = Ext.widget({
						xtype:"button",
						text:common.button.todelete,//删除附件
						handler:function(){
							templateAttachment_me.deleteFile();
						}
					});
			toolbar.add(uploadbutton);
			toolbar.add(deleteButton);
		}
		if(templateAttachment_me.templPropety.isshowwin=='1'){
			closebutton = {xtype:"button",
					text:common.button.close,
					handler:function(){
						var win = Ext.getCmp("attachment_"+templateAttachment_me.uniqueId);
						if(win)
							win.close();
					}};
			toolbar.add(closebutton);
		}
		if(templateMain_me.templPropety.view_type=='card'&&templateAttachment_me.uploadattach == "0"){
			toolbar = undefined;
		}
		//暂时去掉 影响别的表格修改标识的样式 这里也没有具体的作用
		//if(Ext.util.CSS.getRule(".x-grid-dirty-cell"))//如果x-grid-dirty-cell存在,
		//	Ext.util.CSS.updateRule(".x-grid-dirty-cell","background","fixed");//防止出现红三角
		
		var checkColumnhidden = templateAttachment_me.uploadattach == "0"?true:false;
		var sm = Ext.create('Ext.selection.CheckboxModel',{
								renderer:function(value,metaData,record){//渲染每行是否显示多选框
									var candelete = record.data.candelete;
									if(candelete == "1")
										 return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="button" tabIndex="0">&#160;</div>';
								    else
									 	return "";
								}
		});   
		var topToolbar=null;
		if(templateAttachment_me.templPropety.attachmenttype==1&&templateAttachment_me.showRefreshBtn){//如果是个人附件且显示刷新按钮，创建刷新按钮。
			var topItems=[];
			var isCreate = false;
			var refreshImg = undefined;
			var confirmMsg = "此操作是同步信息库的数据到当前附件，会覆盖当前修改的一些数据，但不会覆盖新增的附件记录，确认刷新么？";
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
			                	templateAttachment_me.refresh();
			                } else {
			                   return;
			                }
		                  }, this);
						}
			        }
			    }
			});
			topItems.push({ xtype: 'tbfill' });
			topItems.push(refreshImg);
			topToolbar = Ext.widget({
				xtype:'toolbar',
				dock: 'top',
				hidden:true,
				itemId: 'topToolbar',
				items:topItems
			});
		}
		templateAttachment_me.grid = Ext.create('Ext.grid.Panel', {
		    store:store,
		    id:gridId,
		    selModel: checkColumnhidden==false?sm:'',
		    columns: [
		       /* {
		        	xtype: 'checkcolumn',text: '<span id="checked_'+templateAttachment_me.templateAttachment_id+'"></span>',dataIndex: 'selected',width: 40,hidden:checkColumnhidden,
		        	stopSelection: false,menuDisabled:true,renderer:templateAttachment_me.addCheckBoxGridCss,sortable: false
		        },*/
		        { text: MB.LABLE.name,menuDisabled:true,sortable:false, dataIndex: 'attachmentname', flex: 4,
		        	renderer:function(value, metaData, record, rowIndex){
		        	var file_id = record.get('file_id');
		        	var htm = "";
		        	htm = '<a href="javascript:void(0);" onclick="Ext.getCmp(\''+templateAttachment_me.templateAttachment_id+'\').downLoadFile(\''+file_id+'\')">'+value+'</a>'
		        	return htm;
		        }},
		        { text: MB.LABLE.sortname,menuDisabled:true,sortable:false, dataIndex: 'sortname',align:'center', flex: 1 ,hidden:templateAttachment_me.templPropety.attachmenttype=='1'?false:true},
		        { text: MB.LABLE.createUser,menuDisabled:true,sortable:false, dataIndex: 'fullname',align:'center', flex: 1 },
		        { text: MB.LABLE.createDate,menuDisabled:true,sortable:false, dataIndex: 'create_time',align:'center', flex: 1 }
		    ],
		    height: '100%',
		    width:  '100%',
		    columnLines:true,
	   		rowLines:true,
	    	border:1,
	    	dockedItems:[topToolbar,toolbar],
		    renderTo: templateAttachment_me.templPropety.isshowwin=='1'?'':templateAttachment_me.uniqueId,
		    		listeners:{
						afterrender:function(panel,e){
								if(toolbar){
									var topToolbar = undefined;
									topToolbar = panel.getComponent('topToolbar');
									panel.getEl().on("mouseenter",function(){//解决谷歌浏览器显示不对
										if(topToolbar){
											topToolbar.show();
										}
									});
								}
						}
		    		}
		});
		if(templateAttachment_me.templPropety.isshowwin=='1'){
			var win =Ext.getCmp("attachment_"+templateAttachment_me.uniqueId)
			if(win){
				win.removeAll(true); 
				win.add(templateAttachment_me.grid);
			}else{
				var attachwin = Ext.widget('window',{
					id:"attachment_"+templateAttachment_me.uniqueId,
					title:templateAttachment_me.templPropety.title,  
					height:400,  
					width:800,
					layout:'fit',             //当窗口只有一个元素是，使用fit布局将整个窗口填充满
					modal:true,               //遮罩效果
					closeAction:'destroy',		//当窗口关闭时，hide为隐藏窗口，destroy为销毁窗口  
					items: [templateAttachment_me.grid]     
				});
				attachwin.show();
			}
		}
	},

	/* 选择框样式 
	addCheckBoxGridCss:function(value, cell, record, rowIndex, columnIndex, store) {
		if (record.get('candelete') == "1") {
			return (new Ext.grid.column.CheckColumn).renderer(value);
		} else {
			return "";
		}
	},*/
	
	//删除附件
	deleteFile:function(file_id){
		var templateAttachment_me = this;
		var selRecords = templateAttachment_me.grid.getSelectionModel().getSelection();
		var file_ids = "";
    	Ext.each(selRecords,function(record,index){
    		var candelete = record.get("candelete");
    		if(candelete == "1"){
    			var file_id = record.get('file_id');
    			file_ids += "," + file_id;
    		}
    	});
    	
    	if(file_ids == ""){
    		Ext.showAlert(common.msg.selectData);
    		return;
    	}
    	
		Ext.showConfirm(common.msg.isDelete,
                function(btn){
                    if(btn=="yes"){
                    	var map = new HashMap();
                		map.put("file_ids",file_ids.substring(1));
                		map.put("allNum",templateTool_me.getTotalCount());
                		map.put("task_id",templateMain_me.templPropety.task_id);	
                		map.put("module_id",templateMain_me.templPropety.module_id);
                	    Rpc({functionId:'MB00006015',async:false,success:function(form){
                	    	var result = Ext.decode(form.responseText);	
                			if(result.succeed){
                				if(result.ok == "0"){
                					Ext.showAlert(MB.MSG.deleteFailed);
                				}
                				else{
                					templateAttachment_me.storeLoad();
                				}
                	        }else{
                	        	var message = result.message;
								if(message&&message.indexOf("拆分审批")!=-1){
									templateTool_me.checkSpllit(message);
								}else
									Ext.showAlert(result.message);
                	        } 
                	    },scope:templateAttachment_me},map);
                    }
                   
                } 
        );
	},
	
	//下载文件
	downLoadFile:function(file_id){
		var templateAttachment_me = this;
    	var map = new HashMap();
    	map.put("file_id",file_id);	
    	map.put("type","download");
    	map.put("tabid",templateAttachment_me.templPropety.tabid);
    	map.put("allNum",templateTool_me.getTotalCount());
		map.put("task_id",templateMain_me.templPropety.task_id);
    	if(Ext.isIE)
    		map.put("isIE","true");	
    	else
    		map.put("isIE","false");	
    	var functionid = "MB00006015";
		var isarchive = getTemplPropetyOthParam("isarchive");
		if(isarchive=='0'){
			functionid = "MB00008007";
			var record_id = getTemplPropetyOthParam("record_id");
			var archive_year = getTemplPropetyOthParam("archive_year");
			var archive_id = getTemplPropetyOthParam("archive_id");
			map.put("record_id",record_id);
			map.put("archive_year",archive_year);
			map.put("archive_id",archive_id);
			map.put("attachmenttype",templateAttachment_me.templPropety.attachmenttype);
		}
        Rpc({functionId:functionid,async:false,success:function(form){
        	var result = Ext.decode(form.responseText);	
    		if(result.succeed){
    			if(result.ok == "0"){
					Ext.showAlert(MB.MSG.downLoadFailed);
				}
				else{
					var filePath = result.filePath;	
	            	var srcfilename = result.displayfilename;
	            	var ext = result.ext;
            		//var imgWidth = result.imgWidth;
            		//var imgHeight = result.imgHeight;
            		
	            	if(ext == ".jpg" || ext == ".jpeg" || ext == ".png" || ext == ".bmp"){//后台已判断是图片
	            		var obj = new Object();
						obj.filePath = filePath;
						//obj.imgWidth = imgWidth;
						//obj.imgHeight = imgHeight;
						obj.srcfilename = srcfilename;
						Ext.require('EHR.imgshow.ImgShow',function(){
							Ext.create("EHR.imgshow.ImgShow",obj);
						})
	            	}else if(ext == ".pdf"){
		            	if (filePath=="" ) return;
		                var url = "/servlet/vfsservlet?fileid="+filePath;
            			var win=open(url,"pdf");
	            	}
	            	/* 暂时注释掉ieofficer插件预览功能，
	            	 * else if(Ext.isIE && (ext == ".xlsx" || ext == ".xls" || ext == ".doc" || ext == ".docx" || ext == ".dot" || ext == ".ppt" || ext == ".pptx")){
	            		if (filePath=="" ) return;
		                var win=open("/system/options/customreport/displayFile.jsp?filename="+$URL.encode(srcfilename)+"&filepath="+$URL.encode(filePath) ,"");
	            	}*/else{
		            	if (filePath=="" ) return;
		            	var url = "/servlet/vfsservlet?fileid="+filePath;
            			var win=open(url,"");
	            	}
				}
            }else{
            	Ext.showAlert(result.message);
            } 
        }},map);
	},
	
	
	//上传附件
	showUploadFile:function(result){
		var templateAttachment_me = this;
    	var attachmenttype = templateAttachment_me.templPropety.attachmenttype;//0是公共附件，1是个人附件
		var mediasortList = result.mediasortList;
		var isOnlyOneType = result.isOnlyOneType;
    	var win = Ext.create('Ext.window.Window',{
			title:MB.LABLE.selectFile,//选择导入文件
	 		width:450,
	 		height:180,
	 		resizable: false,  
	        modal: true,
	        border:false,
	       	bodyStyle: 'background:#ffffff;',
	       	layout: {
	            type: 'vbox',
	            align: 'left',
	            pack:'center'
	        },
	        buttonAlign:'center',
	        bbar:[{xtype:'tbfill'},{
		        xtype: 'button', 
		        text: common.button.upLoad,//'上传'
		        handler:function(){
		        	if(templateAttachment_me.list && templateAttachment_me.list.length > 0){
		        		var fileTypeValue = "";
		        		if(fileType){
		        			fileTypeValue = fileType.getValue();
		        		}
		        		if(templateAttachment_me.templPropety.attachmenttype==1&&(fileTypeValue==null||fileTypeValue=='underfined'||fileTypeValue=='')){//50901
		        			Ext.showAlert(MB.MSG.noSelectType);//"强选择多媒体分类！"bug 50806
		        			return;
		        		}
		        		templateAttachment_me.uploadFile(templateAttachment_me.list,fileTypeValue);
			        	win.close();
		        	}else{
						Ext.showAlert(MB.MSG.selectUploadFile);//"请选择要导入的文件！"
					}
		        }
	        },{
		        xtype: 'button', 
		        text: common.button.close,//'关闭'
		        handler:function(){
		        	win.close();
		        }
	        },{xtype:'tbfill'}],
	        listeners:{
	        	//liuyz 关闭窗口时清空list列表，否则下次点击上传会不提示。
	        	close:function(){
	        		templateAttachment_me.list = [];
	        	}
	        }
		});
		win.show();
		if(attachmenttype == "1"){
			var fileTypeContainer = Ext.widget({
	       		xtype:'container',
	       		margin: '0 0 0 40',
	       		layout:'hbox'
			});
			var states = Ext.create('Ext.data.Store', {
			    fields: ['dataValue', 'dataName']
			});
			
			Ext.each(mediasortList,function(obj,index){
				states.insert(index,obj);
			});
			
			var fileType = Ext.create('Ext.form.ComboBox', {
			    fieldLabel: MB.LABLE.mediaType,//'多媒体分类'
			    store: states,
			    width:200,
			    queryMode: 'local',
			    displayField: 'dataName',
			    valueField: 'dataValue',
			    value:result.mediasortid,
			    labelWidth:60,
			    labelAlign:'right',
			});
			fileTypeContainer.add(fileType);
			if(isOnlyOneType==true){
				fileType.hide();
			}
			win.add(fileTypeContainer);
		}
		templateAttachment_me.fileUpLoadContainer = Ext.widget({
	        	xtype:'container',
	        	margin: attachmenttype == "0"?'0 0 0 50':'10 0 0 50',
	        	layout:'hbox',
	       		items:[
	       			{
	       				xtype: 'textfield',
				        itemId:'fileName',
				        fieldLabel: MB.LABLE.file,//'文件'
				        labelWidth:50,
				        width:280,
				        margin:'0 3 0 0',
				        labelAlign:'right',
				        readOnly:true           //bug 32989 上传文件名称应不允许用户编辑。
				        
		        	},{
		           	   xtype:"button",
		           	   width:40,
		           	   height:22,
		           	   //id:this.id+"_browse",
		           	   text:common.button.view,//浏览
		           	   listeners:{
					   afterrender : function(btn){
						    var xdvar=20;
						    /**判断是否从代办进来的*/
						    if(templateAttachment_me.isDbUrl){
						    	xdvar=5;
						    }
							Ext.widget("fileupload",{
			  	   					upLoadType:3,
			  	   					height:20,width:40, 
			  	   					style:'position:relative;top:-'+xdvar+'px', 
			  	   					buttonText:'',
			  	   					fileNameMaxLength:180,
			  	   					//调用vfs取消传最大文件大小参数
			  	   					//fileSizeLimit:templateAttachment_me.multimedia_maxsize,//47951
			  	   					fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.pptx;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.pdf;",
			  	   					renderTo:btn.id,
				  	   				isTempFile:false,
									VfsFiletype:VfsFiletypeEnum.multimedia,
									VfsModules:VfsModulesEnum.RS,
									VfsCategory:VfsCategoryEnum.other,
									CategoryGuidKey:'',
			  	   					error:function(){
						   				//Ext.showAlert(common.msg.uploadFailed+"！");
					   				},
			  	   					success:function(list){
			  	   						templateAttachment_me.list = list;
			  	   						if(list.length > 0){
			  	   						var map=new HashMap();
			  	   						map.put('flag',"true");
			  	   						map.put("file_name",list[0].filename)
			  	   						Rpc({functionId:'MB00006014',async:false,success:function(form){
			  	   							var result = Ext.decode(form.responseText);	
			  	   							var fileName=result.file_name
				  	   						var fileText = templateAttachment_me.fileUpLoadContainer.queryById('fileName');
				  	   						if(!list[0].successed){
				  	   							templateAttachment_me.list=[];
				  	   							fileText.setValue("");
				  	   						}else{
					  	   						var localname = fileName;  //原始文件名 
					  	   						fileText.setValue(localname.substring(0,localname.lastIndexOf(".")));
				  	   						}
			  	   						 }},map);
			  	   						}
					   				},
			  	   					callBackScope:'',
			  	   					//savePath:'',
			  	   					uploadUrl:"/case/"
			  	   				});
					}
				}}]
		})
		win.add(templateAttachment_me.fileUpLoadContainer);
	},
	//上传附件
	/**
	 * @param {} list 上传文件
	 * @param {} fileType 文件分类
	 */
	uploadFile:function(list,fileType){
		var templateAttachment_me = this;
		var fileText = templateAttachment_me.fileUpLoadContainer.queryById('fileName');
		if(list.length!=0){
		    var valuestr='';
		    for(var m=0;m<list.length;m++){
		    	var fileid = list[m].fileid;
		    	var filename= list[m].filename;
				/*var filename = list[m].filename;  //编码后文件名
				var id = list[m].id;              //文件唯一标识      
				//var localname = fileText.getValue();  //原始文件名
				var localname =list[0].localname;  //liuyz bug26658带.的文件名，文件名被截断
				localname=replaceAll(localname,",","，");// 由于","是字符串拼接符,如果文件名中包含",",需要将其转成全角"，"
				//localname = localname.substring(0,localname.lastIndexOf("."));//去掉后缀
				var size = list[m].size.replace("\r","").replace("\n","").replace("\r\n","");          //文件大小
				var path = list[m].path;          //文件上传路径
				var text=filename+'|'+path+'|'+localname+'|'+fileType ;*/
		    	var text=fileid+"|"+filename+"|"+fileType;
                valuestr+= ',' + text;
			}
			valuestr=valuestr.substring(1);
			templateAttachment_me.saveAttachement(valuestr);
		}
	},
	
	//保存附件
	saveAttachement:function(fileValues){
		var templateAttachment_me = this;
    	var map = new HashMap();
    	map.put("ins_id",templateAttachment_me.templPropety.ins_id);	
    	map.put("tabid",templateAttachment_me.templPropety.tabid);		
		map.put("object_id",templateAttachment_me.templPropety.object_id);		
		map.put("attachmenttype",templateAttachment_me.templPropety.attachmenttype);//0公共附件，1个人附件	
		map.put("module_id",templateAttachment_me.templPropety.module_id);//传递模块号：9是自助申请。
	//	map.put("rwPriv",templateAttachment_me.templPropety.rwPriv);  //20160905 dengcan 后台没有调用
	//	map.put("sp_batch",templateAttachment_me.templPropety.sp_batch); // 20160905 dengcan 无用了，ins_id表示当前选中记录的单号
        map.put('infor_type', templateAttachment_me.templPropety.infor_type);//1：人事异动
        map.put('fileValues', fileValues);//上传文件
        map.put("allNum",templateTool_me.getTotalCount());
        map.put("task_id",templateMain_me.templPropety.task_id);	
        Rpc({functionId:'MB00006016',async:false,success:function(form){
        	var result = Ext.decode(form.responseText);	
			if(result.succeed){
				if(templateMain_me.templPropety.view_type == "card")
			    {
			   		templateCard_me.isHaveChange=true;
			    }
				templateAttachment_me.storeLoad();
	        }else{
	        	var message = result.message;
				if(message&&message.indexOf("拆分审批")!=-1){
					templateTool_me.checkSpllit(message);
				}else
					Ext.showAlert(result.message);
	        } 
        }},map);
	},
	
	storeLoad:function(){
		var templateAttachment_me = this;
		templateAttachment_me.init();
	},
	refresh:function(){//刷新
		var templateAttachment_me = this;
		var map = new HashMap();
        map.put('infor_type', templateAttachment_me.templPropety.infor_type);//1：人事异动
        map.put("ins_id",templateAttachment_me.templPropety.ins_id);	
		map.put("tabid",templateAttachment_me.templPropety.tabid);		
		map.put("object_id",templateAttachment_me.templPropety.object_id);		
		map.put("attachmenttype",templateAttachment_me.templPropety.attachmenttype);	
		map.put("module_id",templateAttachment_me.templPropety.module_id);//传递模块号：9是自助申请。
		map.put("rwPriv",templateAttachment_me.templPropety.rwPriv);
		map.put("uniqueId",templateAttachment_me.uniqueId);
		map.put("task_id",templateMain_me.templPropety.task_id);	
		map.put("allNum",templateTool_me.getTotalCount());
        Rpc({functionId:'MB00006021',async:false,success:function(form){
        	var result = Ext.decode(form.responseText);	
			if(result.succeed){
				var attachmentList=result.attachmentList
				if(attachmentList){
					var gridId = "attachmentgrid_" + templateAttachment_me.uniqueId;
					var attachment_grid = Ext.getCmp(gridId);
					var store=attachment_grid.getStore();
					if(store){
						store.removeAll();
						Ext.each(attachmentList,function(record, index){
							store.insert(index,record);
						})
					}else{
						templateAttachment_me.storeLoad();
					}
				}else{
					templateAttachment_me.storeLoad();
				}
	        }else{
	        	var message = result.message;
				if(message&&message.indexOf("拆分审批")!=-1){
					templateTool_me.checkSpllit(message);
				}else
					Ext.showAlert(result.message);
	        } 
        }},map);
	}
});