/*
 * 印章管理  hej add 20181116
 */
Ext.define("SignatureListURL.SignatureList",{
	constructor:function(config) {
		signature_me = this;
		signature_me.callBackFunc = config.callBackFunc;
   		this.init(config);
	},
	init:function(config){
		var map = new HashMap();
		map.put("flag",'0');
		Rpc({functionId:'MB00007001',async:false,success:this.getTableOK,scope:this},map);
	},
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		signature_me.ext_paramMap = new HashMap();
		var conditions=result.tableConfig;
		signature_me.fieldsMap = result.fieldsMap;
		signature_me.fieldsArray = result.fieldsArray;
		signature_me.imghwlist = getDecodeStr(result.imghwlist);
		signature_me.signature_usb = result.signature_usb;
		signature_me.currentUser = result.currentUser;
		var obj = Ext.decode(conditions);
		obj.beforeBuildComp=function(config){
			config.tableConfig.viewConfig.getRowClass=function(record,rowIndex,rowParams,store){
				return 'x-grid-row custom-grid-row';
			};
		};
		signature_me.tableObj = new BuildTableObj(obj);
		signature_me.mainPanel = signature_me.tableObj.getMainPanel();
		signature_me.subModuleId = signature_me.tableObj.subModuleId;
		signature_me.addTools();
		if(Ext.util.CSS.getRule(".x-grid-cell-inner"))
    		Ext.util.CSS.updateRule(".x-grid-cell-inner","max-height","");
		signature_me.mainPanel.on("render",function(view){ 
			Ext.create('Ext.tip.ToolTip', {
			    target: view.id,
			    delegate:"td > div.x-grid-cell-inner",
			    shadow:false,
			    trackMouse: true,
			    renderTo: Ext.getBody(),
			    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
			    listeners: {
			        beforeshow: function updateTipBody(tip) {
			        	    var div = tip.triggerElement;
			        	    var id = div.childNodes[0].id;
			        	    if(id!=undefined&&id.indexOf("signature_")!=-1){
			        	    	var signatureid = id.split("_")[1];
				        	    var imgarr = signature_me.imghwlist.split(",");
				        		var imgheight,imgwidth,markpath;
				        		for(var i=0;i<imgarr.length;i++){
				        			var imghw = imgarr[i];
				        			var imghwarr = imghw.split("`");
				        			var signatureid_ = imghwarr[0];
				        			if(signatureid==signatureid_){
				        				imgheight = imghwarr[1];
				        				imgwidth = imghwarr[2];
				        				markpath = imghwarr[3];
				        				break;
				        			}
				        		}
				        	    if (Ext.isEmpty(div))
				        	    	return false;
					        	if(div.offsetWidth < 90 || div.offsetHeight < 60){
					        		var src = "";
					        		var html = "";
					        		if(markpath!=""){
					        			src = 'data:image/png;base64,'+markpath;
						        		html = '<img src='+src+' border=0 height='+imgheight+' width='+imgwidth+'>';
						        		tip.update(html);
					        		}
					        	}else
					        		return false;
			        	    }else
			        	    	return false;
			        }
			    }
    		}); 
         },signature_me);
		if(signature_me.callBackFunc){
            Ext.callback(eval(signature_me.callBackFunc),null,[signature_me.mainPanel]);
		}
	},
	/**
	 * 添加工具栏
	 */
	addTools:function(){
		var toolBar = Ext.getCmp("signature_toolbar");
		var map = new HashMap();
		map.put('fieldsMap',signature_me.fieldsMap);
		signature_me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
            emptyText:MB.LABEL.SEARCHTEXT,
            subModuleId:signature_me.subModuleId,
            customParams:map,
            funcId:"MB00007004",
            fieldsArray:signature_me.fieldsArray,
            success:signature_me.searchBoxOK
        });
		toolBar.insert(2,signature_me.SearchBox);
	},
	/**
	 * 复杂查询
	 */
	searchBoxOK:function(config){
		var condsql=config.condsql;
		if(Ext.isEmpty(condsql) || 'undefined' == condsql){
              condsql="";
        }
		signature_me.condsql = getEncodeStr(condsql);
        signature_me.query();
    },
	query:function(){
		var map = new HashMap();
		map.put('queryflag','0');
		map.put('condsql',signature_me.condsql);
		map.put("flag",'0');
		Rpc({functionId:'MB00007001',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){ 
				var store = Ext.data.StoreManager.lookup('signature_dataStore');
				store.reload();
	  		}else{
				Ext.showAlert(result.message);
			}
		},scope:this},map);
	},
	/**
	 * 渲染删除和编辑按钮
	 */
	actionRender:function(value,meta,record){
		var fullname = record.get("fullname");
		var password = record.get("password");
		var usertype = record.get("usertype");
		var signatureid = record.get("signatureid");
		var username_e = record.get("username_e");
		signature_me.ext_paramMap.put(signatureid,getEncodeStr(record.get("ext_param")));
		var hardwareid = record.get("hardwareid");
		var paramvalue = fullname+"`"+password+"`"+usertype+"`"+signatureid+"`"+hardwareid+"`"+username_e;
		paramvalue = getEncodeStr(paramvalue);
		var html="";
		html+="<a href='javascript:signature_me.addSignature(1,\""+paramvalue+"\");'>"+MB.LABEL.EDIT+"&nbsp;&nbsp;<a/>";
		html+="<a href='javascript:signature_me.delSignature(1,\""+record.get("signatureid")+"\");'>"+MB.LABEL.DELETE+"<a/>";
		return html;
    },
    /**
     * 渲染照片列
     */
    showSignatureImgRender:function(value,meta,record){
    	var markpath = "";
		var signatureid = record.get("signatureid");
		var imgarr = signature_me.imghwlist.split(",");
		var imgheight,imgwidth;
		for(var i=0;i<imgarr.length;i++){
			var imghw = imgarr[i];
			var imghwarr = imghw.split("`");
			var signatureid_ = imghwarr[0];
			if(signatureid==signatureid_){
				imgheight = imghwarr[1];
				imgwidth = imghwarr[2];
				markpath = imghwarr[3];
				break;
			}
		}
		var src = "";
		var html = "";
		if(markpath!=""){
			src = 'data:image/png;base64,'+markpath;
			if(imgheight>60){
				imgheight=58;
			}
			if(imgwidth>78){
				imgwidth=78;
			}
			html = "<a id=signature_"+signatureid+" href=javascript:void(0);><img src="+src+" border=0 height="+imgheight+" width="+imgwidth+"></a>";
		}
		return html;
    },
    /**
     * 新建页面
     */
    addSignature:function(flag,paramvalue){
    	var fullname = '';
		var password = '';
		var markname = '';
		var usertype = '';
		var signatureid = '';
		var hardwareid = '';
		signature_me.username_b = '';
    	if(flag==1){
    		signature_me.password_modify = 'false';
    		signature_me.file_modify = 'false';
    		signature_me.delMarkId="";//记录删除的markid，以逗号分隔
    		signature_me.filename_modify = 'false';
    		signature_me.hardware_modify = 'false';
    		paramvalue = getDecodeStr(paramvalue);
    		var paramvaluearr = paramvalue.split('`'); 
    		fullname = paramvaluearr[0];
    		password = paramvaluearr[1];
    		usertype = paramvaluearr[2];
    		signatureid = paramvaluearr[3];
    		hardwareid = paramvaluearr[4];
    		usertype=usertype=='1'?MB.LABEL.BUSINESSUSER:MB.LABEL.SELFSERVICEUSER;
    		signature_me.id = paramvaluearr[5];
    	}else{
    		signature_me.ext='';
			signature_me.valuestr='';
			signature_me.name='';
			signature_me.id='';
			signature_me.b0110='';
			signature_me.e01a1='';
			signature_me.e0122='';
			signature_me.usertype='';
    	}
    	var backgroundcolor = "";
    	if(flag==1){
    		backgroundcolor = "background-color:#c5c5c5;";
    	}
    	var tableItems = [];
    	var name = Ext.widget('container',{
    		margin:'20 0 5 40',
    		layout:'hbox',
    		items:[{xtype:'textfield',
    			fieldLabel: MB.LABEL.USERNAME,
    			id:'username',
    			labelAlign:'right',
        		labelWidth:50,
        		value:fullname,
    			fieldStyle:'height:20px;'+backgroundcolor,
    			readOnly:true
    			},{
    				xtype:'button',
    				text:MB.LABEL.SELECT,
    				id:'',
    				margin:'0 0 0 5',
    				hidden:flag==1?true:false,
    				width:60,
    				menu:{
    					items:[
    						{
    							text:MB.LABEL.BUSINESSUSER,
    							handler:function(){
    								signature_me.usertype = "1";
    								signature_me.persionpicker('1');
    							},
    							id:'businessuser'
    						},{
    							text:MB.LABEL.SELFSERVICEUSER,
    							handler:function(){
    								signature_me.usertype = "2";
    								signature_me.persionpicker('2');
    							},
    							id:'selfserviceuser'
    						}
    					]
    				}
    			}]
    	});
    	var password = Ext.widget('textfield',{
    		fieldLabel: MB.LABEL.SIGNATUREPASSWORD,
    		id:'password',
    		labelWidth:50,
    		value:password,
    		labelAlign:'right',
    		inputType:'password',
    		margin:'10 0 5 40',
			fieldStyle:'height:20px;',
			listeners:{
				change:function(textfield,newValue,oldValue,eOpts){
					signature_me.password_modify = 'true';
				} 
			}
    	});
    	/*var toolbar = Ext.widget({
			xtype:'toolbar',
			dock: 'bottom'
		});*/
    	var uploadbutton ={
			xtype:"button",
			width:80,
        	height:22,
			text:MB.LABEL.UPLOADIMG,//上传附件
			listeners:{
				afterrender : function(btn){
					Ext.widget("fileupload",{
		   					upLoadType:3,
		   					height:22,width:80,
	  	   					style:'position:relative;top:-22px',
	  	   					buttonText:'',
	  	   					fileNameMaxLength:180,
		   					fileExt:"*.jpg;*.jpeg;*.png;",
		   					fileSizeLimit:"50KB",
		   					isTempFile:false,
							VfsFiletype:VfsFiletypeEnum.multimedia,
							VfsModules:VfsModulesEnum.RS,
							VfsCategory:VfsCategoryEnum.other,
							CategoryGuidKey:'',
		   					renderTo:btn.id,
		   					error:function(){
		   						Ext.showAlert(common.msg.uploadFailed+"！");
		   					},
		   					success:function(list){
		   						signature_me.list = list;
		   						if(list.length > 0){
		   							signature_me.uploadFile(signature_me.list);
		   						}
		   					},
		   					callBackScope:'',
		   					uploadUrl:"/case/"
		   			});
				}
			}
		};
		var deleteButton = {
			width:80,
			xtype:"button",
			height:22,
			margin:'0 0 0 5',
			text:common.button.todelete,//删除附件
			handler:function(){
				signature_me.deleteFile();
			}
		};
		//toolbar.add(uploadbutton);
		//toolbar.add(deleteButton);
    	var store = Ext.create('Ext.data.Store', {
		    fields:['markid', 'markname','marktype', 'marksize','filename','localname','path','flag']
		});
    	if(flag==1){
    		//解析xml
    		var ext_param = "";
    		for(var key in signature_me.ext_paramMap){
			   if(key==signatureid){
				  ext_param = getDecodeStr(signature_me.ext_paramMap[key]);
				  break;
			   }
    		}
    		var xmlList = signature_me.analysisXML(ext_param);
    		if(xmlList.length>0){
    			Ext.each(xmlList,function(record, index){
    				store.insert(index,record);
    			})
    		}
    	}
		
    	var sm = Ext.create('Ext.selection.CheckboxModel',{
			renderer:function(value,metaData,record){//渲染每行是否显示多选框
				return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="button" tabIndex="0">&#160;</div>';
			}
    	});
    	signature_me.grid = Ext.create('Ext.grid.Panel', {
		    store:store,
		    id:"markimg",
		    margin:'0 0 0 -1',
		    selModel:sm,
		    columns: [
		        { text: MB.LABEL.IMGNAME,menuDisabled:true,sortable:false, dataIndex: 'markname', flex: 3,editor:{xtype:'textfield',listeners:{
		        	change:function( e, newValue, oldValue, eOpts ){
		        		if(newValue!=oldValue){
		        			signature_me.filename_modify = 'true';
		        		}
		        	}
		        }}},
		        { text: MB.LABEL.IMGTYPE,menuDisabled:true,sortable:false, dataIndex: 'marktype',align:'center', flex: 2 },
		        { text: MB.LABEL.IMGSIZE,menuDisabled:true,sortable:false, dataIndex: 'marksize',align:'center', flex: 2 }
		    ],
		    height: 150,
		    width: 350,
		    columnLines:true,
	   		rowLines:true,
	    	border:1,
	    	buttonAlign:'left',
	    	plugins:[Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1})],
	    	//buttons:[uploadbutton,deleteButton]
		});
    	//上传图片
    	var fileUpLoadContainer = Ext.widget({
        	xtype:'container',
        	margin:'10 0 0 40',
        	layout:'hbox',
       		items:[
       			{xtype:'label',
	        		text:MB.LABEL.SIGNATUREFILE,
	        		width:50,
	        		margin:'0 5 0 2'
	        	},signature_me.grid]
    	});
    	var uploadOrdeletebutton = Ext.widget({
    		xtype:'container',
    		margin:'10 0 0 96',
    		layout:'hbox',
    		items:[uploadbutton,deleteButton]
    	});
    	var backcolor = "";
    	if(hardwareid!=''){
    		backcolor = "background-color:#c5c5c5;";
    	}
    	//U盾
    	var udunpanel = Ext.widget({
    		xtype:'container',
    		margin:'10 0 0 40',
    		layout:'hbox',
    		items:[
    			{
    				xtype:'textfield',
    				id:'udunid',
        			fieldLabel: MB.LABEL.UDUNID,
        			labelAlign:'right',
            		labelWidth:50,
        			fieldStyle:'height:20px;'+backcolor,
        			readOnly:true,
        			emptyText:MB.LABEL.PLEASEBINDINGUDUN,
        			value:hardwareid
    			},{
    				xtype:'button',
    				margin:'0 0 0 5',
    				text:MB.LABEL.BINDINGUDUN,
    				handler:function(){
    					//绑定U盾：
    					if(signature_me.id==undefined||signature_me.id==null||signature_me.id==''){
							Ext.showAlert(MB.LABEL.PLEASEADDUSERFORBANDING,function(){
								return;
							});
						}else{
	    					if(websock&&ctrl!=null){//非ie浏览器
	    						signature_me.bandingDisk(1);
	    					}else{
	        					var PID = Arm_GetDongleInfo(1);
	        					if(PID!=""){
	        						Arm_Open();
	        					    //1、首先判断是不是发布的锁，不是，给出提示。
	        					    //读取0~2048位置存储的des加密的硬件id
	        						var PID_en = Arm_ReadData(0,16);
	        						var BaningID = Arm_ReadData(2048,128);
	        						//给PID_en解密 并判断PID_en与PID是否相同
	        						var map = new HashMap();
	        						map.put('type','1');//=0 加密 =1 解密
	        						map.put('data_en',PID_en);
	        						map.put('data',PID);
	        						map.put('username',signature_me.id);
	        						map.put('bandingid',BaningID);
	        						Rpc({functionId:'MB00007006',async:false,success:function(form,action){
	        							var result = Ext.decode(form.responseText);
	        					    	var flag=result.succeed;
	        							if(flag==true){ 
	        								var data_de = result.data_de;
	        								var username = result.username;
	        								var username_e = result.username_e;
	        								var username_b = result.username_b;
	        								if(data_de==PID){
	        									//判断锁是否绑定过用户
	        									if(username_b!=''&&username_b!=username_e){
	        										Ext.showConfirm(MB.LABEL.ALREADLYBANDINGUSER,function(optional){
	        											if(optional=='yes'){
	        												//2、然后将“用户名称+硬件ID”加密，将加密值存储到U盾的数据安全区。存储区域到前半区的这段位置(2049-4096)。则完成绑定，将硬件ID存储到印章管理表记录。
	        	    	    	        					Arm_WriteData(2048,username_e+PID);
	        	    	    	        					Ext.getCmp('udunid').setValue(PID);
	        	    	    	        					signature_me.username_b = username_b;//记录之前绑定的用户
	        	    	    	        					signature_me.hardware_modify = 'true';
	        	    	    	        					Arm_Close();
	        											}else{
	        												
	        											}
	        										})
	        									}else{
	        										//2、然后将“用户名称+硬件ID”加密，将加密值存储到U盾的数据安全区。存储区域到前半区的这段位置(2049-4096)。则完成绑定，将硬件ID存储到印章管理表记录。
	        	    	        					Arm_WriteData(2048,username_e+PID);
	        	    	        					Ext.getCmp('udunid').setValue(PID);
	        	    	        					signature_me.hardware_modify = 'true';
	        	    	        					Arm_Close();
	        									}
	        								}else{
	        									Ext.showAlert(MB.LABEL.CHECKNOHJSOFTCLOCKNOBANDING,function(){
	        										Arm_Close();
	        									});
	        								}
	        					  		}else{
	        								Ext.showAlert(result.message,function(){
	        									Arm_Close();
	        								});
	        							}
	        						},scope:this},map);
	        					}
	    					}
    					}
    				}
    			}
    		]
    	});
    	//印章图片存储到U盾勾选
    	/*var udunCheckbox = Ext.widget({
    		xtype:'checkbox',
    		margin:'5 0 0 96',
    		boxLabel:MB.LABEL.SAVETOUDUN,
    		id:'checkbox_udun',
    		value:issavetodisk=='0'?true:false,
    		listeners:{
    			change:function(e, newValue, oldValue, eOpts ){
    				if(newValue){
    					if(Ext.getCmp('udunid').getValue()==''){
    						Ext.showAlert(MB.LABEL.PLEASEBINDINGUDUNFIRST,function(){
    							udunCheckbox.setValue(false);
    						});
    					}else{
    						//判断上传的图片是否超出9kb
    						var filesize = 0;
    	        			for(var i=0;i<signature_me.grid.store.getCount();i++){
    	        				var marksize = signature_me.grid.store.data.items[i].data.marksize;
    	        				if(marksize.toLowerCase().indexOf("mb")>-1){
    	        					marksize=marksize.substring(0,marksize.length-2);
    	        					marksize=marksize*1024*1024;
                                }else if(marksize.toLowerCase().indexOf("gb")>-1){
                                	marksize=marksize.substring(0,marksize.length-2);
                                	marksize=marksize*1024*1024*1024;
                                }else if(marksize.toLowerCase().indexOf("kb")>-1){
                                	marksize=marksize.substring(0,marksize.length-2);
                                	marksize=marksize*1024;
                                }
    	        				filesize+=marksize;
    	        			}
    	        			if(filesize>9*1024){
    	        				Ext.showAlert(MB.LABEL.FILESIZENOTOVER9KB,function(){
    	        					udunCheckbox.setValue(false);
    	        					return;
    	        				});
    	        			}else
    	        				signature_me.file_modify='true';
    					}
    				}else
    					signature_me.file_modify='true';
    			}
    		}
    	});*/
    	tableItems.push(name);
    	tableItems.push(password);
    	tableItems.push(fileUpLoadContainer);
    	tableItems.push(uploadOrdeletebutton);
    	if(signature_me.signature_usb){
    		tableItems.push(udunpanel);
        	//tableItems.push(udunCheckbox);
    	}
    	var win = Ext.widget('window',{
    		id:'addsignaturewin',    
			title:flag==0?MB.LABEL.ADDSIGNATURE:MB.LABEL.EDITSIGNATURE,  
			height:signature_me.signature_usb?390:360,
			width:500,
			layout:'fit',
			modal:true,           
			closeAction:'destroy',
			resizable:false,
			items: [{xtype:'container',width:490,items:tableItems}],
			buttonAlign:'center',
    		buttons:[{text:MB.LABEL.SURE,handler: function() {
    			var map = new HashMap();
    			if(flag==1){//更新
    				map.put('flag',flag+'');
    				if(signature_me.file_modify=='true'){
    					map.put("delMarkId",signature_me.delMarkId);
    					map.put('file_modify',signature_me.file_modify);
    					var records = [];
	        			for(var i=0;i<signature_me.grid.store.getCount();i++){
	        				records.push(signature_me.grid.store.data.items[i].data);
	        			}
	        			map.put('imgList',records);
	        			if(signature_me.signature_usb)
	        				map.put('hardwareid',Ext.getCmp('udunid').getValue());
	        			var index = signature_me.grid.store.getCount();
	    				if(index==0){//签章文件没有添加
	    					//Ext.showAlert(MB.LABEL.NOFILENOEDIT);
	    					//return;
	    				}
    				}
					if(signature_me.password_modify=='true'){
						map.put('password_modify',signature_me.password_modify);
						map.put('password',Ext.getCmp('password').getValue());
					}
					if(signature_me.filename_modify=='true'){
						var records = [];
	        			for(var i=0;i<signature_me.grid.store.getCount();i++){
	        				records.push(signature_me.grid.store.data.items[i].data);
	        			}
	        			if(signature_me.signature_usb)
	        				map.put('hardwareid',Ext.getCmp('udunid').getValue());
	        			map.put('imgList',records);
						map.put('filename_modify',signature_me.filename_modify);
					}else{
						if(signature_me.signature_usb){
	        				var udunvalue = Ext.getCmp('udunid').getValue();
	            			if(udunvalue==null||udunvalue==''){
	            				Ext.showAlert(MB.LABEL.PLEASEBINDINGUDUN+'!');
	        					return;
	            			}
	        			}
					}
					map.put('signatureid',signatureid);
					if(signature_me.hardware_modify=='true'){
						map.put('hardwareid',Ext.getCmp('udunid').getValue());
						map.put('username_b',signature_me.username_b);
						map.put('hardware_modify',signature_me.hardware_modify);
						var records = [];
	        			for(var i=0;i<signature_me.grid.store.getCount();i++){
	        				records.push(signature_me.grid.store.data.items[i].data);
	        			}
	        			map.put('imgList',records);
					}
    			}else{//新增
    				map.put('flag',flag+'');
        			map.put('password',Ext.getCmp('password').getValue());
        			map.put('name',signature_me.name);
        			map.put('userid',signature_me.id);
        			map.put('b0110',signature_me.b0110);
        			map.put('e01a1',signature_me.e01a1);
        			map.put('e0122',signature_me.e0122);
        			map.put('usertype',signature_me.usertype);
        			var records = [];
        			for(var i=0;i<signature_me.grid.store.getCount();i++){
        				records.push(signature_me.grid.store.data.items[i].data);
        			}
        			map.put('imgList',records);
        			if(signature_me.signature_usb){
        				var udunvalue = Ext.getCmp('udunid').getValue();
            			if(udunvalue&&udunvalue!=null&&udunvalue!=''){
            				map.put('hardwareid',Ext.getCmp('udunid').getValue());
            			}else{
            				if(signature_me.id&&signature_me.id!=null&&signature_me.id!=''){
            					Ext.showAlert(MB.LABEL.PLEASEBINDINGUDUN+'!');
            					return;
            				}
            			}
        			}
        				
        			map.put('username_b',signature_me.username_b);
    			}
    			if(flag==1&&signature_me.password_modify == 'false'&&signature_me.file_modify == 'false'&&signature_me.filename_modify=='false'&&signature_me.hardware_modify=='false'){
    				win.close();
    				return;
    			}
    			if(flag==0){//新增
    				if(signature_me.id==undefined||signature_me.id==null||signature_me.id==''){//签章用户
    					Ext.showAlert(MB.LABEL.NOUSERNOADD);
    					return;
    				}
    				var index = signature_me.grid.store.getCount();
    				if(index==0){//签章文件没有添加
    					//Ext.showAlert(MB.LABEL.NOFILENOADD);
    					//return;
    				}
    			}
    			Rpc({functionId:'MB00007002',async:false,success:function(form,action){
    				var result = Ext.decode(form.responseText);
    				signature_me.imghwlist = getDecodeStr(result.imghwlist);
    				if(result.succeed){
    					if(signature_me.signature_usb){
    						//var checkbox_udun = Ext.getCmp('checkbox_udun').getValue();
    						/*if(checkbox_udun){//选择了将图片存储到U盾中
        						var savetodisklist = getDecodeStr(result.savetodisklist);
        						if(savetodisklist.length>0){
        							if(websock&&ctrl!=null){//非ie浏览器
        								signature_me.saveFileToDisk(0,savetodisklist);
        							}else{
        								var PID = Arm_GetDongleInfo(0);
            							if(PID!=''){
            								Arm_Open();
                							var FileAttr = Arm_Set_DATA_FILE_ATTR();
                							//创建文件之前先删除文件
                							for(var k=0;k<9;k++){
                								Arm_DeleteFile("000"+k);
                							}
                    						//根据文件大小判断创建几个文件  一个文件最大4096
                							var savetodiskarr = savetodisklist.split(",");
                							var filenum = 1;
                							for(var i=0;i<savetodiskarr.length;i++){
                								var text = savetodiskarr[i];
                								var textarr = text.split('`');
                								for(var j=0;j<textarr.length;j++){
                									//创建文件之前先删除文件
                									Arm_DeleteFile("000"+filenum);
                									Arm_CreateFile(FileAttr,"000"+filenum);
                									Arm_WriteFile(b.encode(textarr[j]),"000"+filenum);
                									filenum++;
                								}
                							}
                							Arm_Close();
            							}
        							}
        						}
        					}*/
    					}
    					win.close();
        				var store = Ext.data.StoreManager.lookup('signature_dataStore'); 
        				store.load();
        				if(flag==1){
        		    		signature_me.password_modify = 'false';
        		    		signature_me.file_modify = 'false';
        		    		signature_me.filename_modify = 'false';
        		    		signature_me.hardware_modify = 'false';
        				}
    				}else{
    					Ext.showAlert(result.message);
    					return;
    				}
    			},scope:this},map);
		    }},{
		    	text:MB.LABEL.CANCEL,
		    	handler: function() {
		    		win.close();
			    }
		    }],
		    listeners:{
		    	close:function(){
		    		if(Ext.getCmp('person_picker_single_view'))
		    			Ext.getCmp('person_picker_single_view').close();
		    	}
		    }
    	});
    	win.show();
    },
    /**
     * 将图片存储到U盾
     */
    saveFileToDisk:function(flag,savetodisklist){
    	ctrl.Arm_Enum(function(result, response){
			if (!result){
				Ext.showAlert("Arm_Enum error. " + response);
			}else{
        		var index = response;
        		if(index>1){
        			if(flag==1){
        				Ext.showAlert(MB.LABEL.NOMORECLOCKBANDING,function(){
        					return;
        				});
        			}else if(flag==0){
        				Ext.showAlert(MB.LABEL.CHECKMORECLOCK,function(){
        					return;
        				});
        			}
        		}
        		if(index==0){
        			Ext.showAlert(MB.LABEL.CHECKNOCLOCK,function(){
        				return;
        			});
        		}
        		DongleInfoNum = 2;	//硬件ID
        		if(index>0){
        			Index = index-1;
    				ctrl.Arm_GetDongleInfo(function(result, response){
    					if (!result){
    						Ext.showAlert("Arm_GetDongleInfo error. " + response);
    					}else{
    		        		var PID = response;
    		        		if(PID!=''){
    		        			ctrl.Arm_Open(function(result, response){
    		        				if (!result){
    		        					Ext.showAlert("Arm_Open error. " + response);
    		        				}else{
    		        					ArmHandle = response;
    		        					Size = 4096;	//数据文件长度
    		        					ReadPriv = 2;	//读权限，0为最小匿名权限，1为最小用户权限，2为最小开发商权限
    		        					WritePriv = 2;	//写权限
    		        					Handle = ArmHandle;
    		        					ctrl.Arm_Set_DATA_FILE_ATTR(function(result, response){
    		        						if (!result){
    		        							Ext.showAlert("Arm_Set_DATA_FILE_ATTR error. " + response);
    		        						}else{
    		        							FileAttr = response;
    		        							if(FileAttr == null){
    												Ext.showAlert(MB.LABEL.PLEASESETFILE);
    												return;
    											}
    		        							FileType = 1;
    		        							//根据文件大小判断创建几个文件  一个文件最大4096
    		        							var FileID_1='0001';
    		        							signature_me.clockfiletext_1='',signature_me.clockfiletext_2='',signature_me.clockfiletext_3='',signature_me.clockfiletext_4='',
    		        							signature_me.clockfiletext_5='',signature_me.clockfiletext_6='',signature_me.clockfiletext_7='',signature_me.clockfiletext_8='',
    		        							signature_me.clockfiletext_9='';
    		        							var savetodiskarr = savetodisklist.split(",");
    		        							var filenum = 1;
    		        							for(var i=0;i<savetodiskarr.length;i++){
    		        								var text = savetodiskarr[i];
    		        								var textarr = text.split('`');
    		        								for(var j=0;j<textarr.length;j++){
    		        									if(filenum==1){
    		        										signature_me.clockfiletext_1 = textarr[j];
    		        									}else if(filenum==2){
    		        										signature_me.clockfiletext_2 = textarr[j];
    		        									}else if(filenum==3){
    		        										signature_me.clockfiletext_3 = textarr[j];
    		        									}else if(filenum==4){
    		        										signature_me.clockfiletext_4 = textarr[j];
    		        									}else if(filenum==5){
    		        										signature_me.clockfiletext_5 = textarr[j];
    		        									}else if(filenum==6){
    		        										signature_me.clockfiletext_6 = textarr[j];
    		        									}else if(filenum==7){
    		        										signature_me.clockfiletext_7 = textarr[j];
    		        									}else if(filenum==8){
    		        										signature_me.clockfiletext_8 = textarr[j];
    		        									}else if(filenum==9){
    		        										signature_me.clockfiletext_9 = textarr[j];
    		        									}
    		        									filenum++;
    		        								}
    		        							}
    		        							//创建文件并写文件
    		        							FileID = FileID_1;
    		        							signature_me.clockfilenum = 1;
    		        							signature_me.CreateAndWriteFile();
    		        							//Arm_Close();
    		        						}
    		        					})
    		        				}
		        				})
	        				}
        				}
    				})
				}
			}
    	})
    },
    /**
     * 递归调用
     */
    CreateAndWriteFile:function(){
		ctrl.Arm_DeleteFile(function(result,response){
			AttrBuffer = FileAttr;
			ctrl.Arm_CreateFile(function(result, response){
				if(!result){
					Ext.showAlert("Arm_CreateFile error. " + response);
				}else{
					var rtn = response;
					if(0 == rtn){
						FileOffset = 0;
						var text = '';
						if(signature_me.clockfilenum==1){
							text = signature_me.clockfiletext_1;
						}else if(signature_me.clockfilenum==2){
							text = signature_me.clockfiletext_2;
						}else if(signature_me.clockfilenum==3){
							text = signature_me.clockfiletext_3;
						}else if(signature_me.clockfilenum==4){
							text = signature_me.clockfiletext_4;
						}else if(signature_me.clockfilenum==5){
							text = signature_me.clockfiletext_5;
						}else if(signature_me.clockfilenum==6){
							text = signature_me.clockfiletext_6;
						}else if(signature_me.clockfilenum==7){
							text = signature_me.clockfiletext_7;
						}else if(signature_me.clockfilenum==8){
							text = signature_me.clockfiletext_8;
						}else if(signature_me.clockfilenum==9){
							text = signature_me.clockfiletext_9;
						}
						if(text!=''){
							DataInput = b.encode(b.encode(text));
							ctrl.Arm_WriteFile(function(result, response){
								if(!result){
									Ext.showAlert("Arm_WriteFile error. " + response);
								}else{
									var rtn = response;
									if(0 == rtn){
										signature_me.clockfilenum ++;
										if(signature_me.clockfilenum>9)
											return;
										FileID = "000"+signature_me.clockfilenum;
										signature_me.CreateAndWriteFile();
									}else{
										//Ext.showAlert(rtn);
									}
								}
							})
						}
					}else{
						//Ext.showAlert(rtn);
					}
				}
			})
		})
    },
    /**
     * 绑定锁 非ie
     */
    bandingDisk:function(flag){
    	ctrl.Arm_Enum(function(result, response){
			if (!result){
				Ext.showAlert("Arm_Enum error. " + response);
			}else{
        		var index = response;
        		if(index<0){
        			index=0;
        		}
        		if(index>1){
        			if(flag==1){
        				Ext.showAlert(MB.LABEL.NOMORECLOCKBANDING,function(){
        					return;
        				});
        			}else if(flag==0){
        				Ext.showAlert(MB.LABEL.CHECKMORECLOCK,function(){
        					return;
        				});
        			}
        		}
        		if(index==0){
        			Ext.showAlert(MB.LABEL.CHECKNOCLOCK,function(){
        				return;
        			});
        		}
        		DongleInfoNum = 2;	//硬件ID
        		if(index>0){
        			Index = index-1;
    				ctrl.Arm_GetDongleInfo(function(result, response){
    					if (!result){
    						Ext.showAlert("Arm_GetDongleInfo error. " + response);
    					}else{
    		        		var PID = response;
    		        		if(PID!=''){
    		        			ctrl.Arm_Open(function(result, response){
    		        				if (!result){
    		        					Ext.showAlert("Arm_Open error. " + response);
    		        				}else{
    		        					ArmHandle = response;
    		        					Offset = 0;
    		        					ReadLength = 16;
    		        					Handle = ArmHandle;
    		        					ctrl.Arm_ReadData(function(result,response){
    		        						if(!result){
    		        							Ext.showAlert("Arm_ReadData error. " + response);
    		        						}else{
    		        							Offset = 2048;
    	    		        					ReadLength = 128;
    	    		        					var PID_en = response;
    	    		        					ctrl.Arm_ReadData(function(result,response){
    	    		        						if(!result){
    	    		        							Ext.showAlert("Arm_ReadData error. " + response);
    	    		        						}else{
    	    		        							var BaningID = response;
    	    		        							var map = new HashMap();
    	    		            						map.put('type','1');//=0 加密 =1 解密
    	    		            						map.put('data_en',PID_en);
    	    		            						map.put('data',PID);
    	    		            						map.put('username',signature_me.id);
    	    		            						map.put('bandingid',BaningID);
    	    		            						Rpc({functionId:'MB00007006',async:false,success:function(form,action){
    	    		            							var result = Ext.decode(form.responseText);
    	    		            					    	var flag=result.succeed;
    	    		            							if(flag==true){ 
    	    		            								var data_de = result.data_de;
    	    		            								var username = result.username;
    	    		            								var username_e = result.username_e;
    	    		            								var username_b = result.username_b;
    	    		            								if(data_de==PID){
    	    		            									if(username_b!=''&&username_b!=username_e){
    	    		            										Ext.showConfirm(MB.LABEL.ALREADLYBANDINGUSER,function(optional){
    	    		            											if(optional=='yes'){
    	    		            												 //2、然后将“用户名称+硬件ID”加密，将加密值存储到U盾的数据安全区。存储区域到前半区的这段位置(2049-4096)。则完成绑定，将硬件ID存储到印章管理表记录。
    	        	    		            									Offset = 2048;
    	        	    		            									DataInput = b.encode(username_e+PID);
    	        	    		            									ctrl.Arm_WriteData(function(result,response){
    	        	    		            										if(!result){
    	        	    		            											Ext.showAlert("Arm_WriteData error. " + response);
    	        	    		            										}else{
    	        	    		            											var rtn = response;
    	        	    		            											if(0 == rtn){
    	        	    		            												Ext.showAlert(MB.LABEL.BANDINGSUCCESS);
    	        	    		            												Ext.getCmp('udunid').setValue(PID);
    	        	    		            												signature_me.username_b = username_b;//记录之前绑定的用户
    	        	    		    		        	    	        					signature_me.hardware_modify = 'true';
    	        	    		    		        	    	        					Arm_Close();
    	        	    		            											}else{
    	        	    		            												//Ext.showAlert(rtn);
    	        	    		            											}
    	        	    		            										}
    	        	    		            									})
    	    		            											}else{}
    	    		            										})
    	    		            									}else{
    	    		            										 //2、然后将“用户名称+硬件ID”加密，将加密值存储到U盾的数据安全区。存储区域到前半区的这段位置(2049-4096)。则完成绑定，将硬件ID存储到印章管理表记录。
        	    		            									Offset = 2048;
        	    		            									DataInput = b.encode(username_e+PID);
        	    		            									ctrl.Arm_WriteData(function(result,response){
        	    		            										if(!result){
        	    		            											Ext.showAlert("Arm_WriteData error. " + response);
        	    		            										}else{
        	    		            											var rtn = response;
        	    		            											if(0 == rtn){
        	    		            												Ext.showAlert(MB.LABEL.BANDINGSUCCESS);
        	    		            												Ext.getCmp('udunid').setValue(PID);
        	    		    		        	    	        					signature_me.hardware_modify = 'true';
        	    		    		        	    	        					Arm_Close();
        	    		            											}else{
        	    		            												//Ext.showAlert(rtn);
        	    		            											}
        	    		            										}
        	    		            									})
    	    		            									}
    	    		            								}else{
    	    		            									Ext.showAlert(MB.LABEL.CHECKNOHJSOFTCLOCKNOBANDING,function(){
    	    		            										Arm_Close();
    	    		            									});
    	    		            								}
    	    		            					  		}else{
    	    		            								Ext.showAlert(result.message,function(){
    	    		            									Arm_Close();
    	    		            								});
    	    		            							}
    	    		            						},scope:this},map);
    	    		        						}
    	    		        					})
    		        						}
    		        					})
    		        				}
    		        			})
    		        		}
    					}
    				})	
        		}
			}
		})
    },
    /**
     * 解析xml
     */
    analysisXML:function(ext_param){
    	var list = new Array();
    	if(ext_param!=""){
    		ext_param = signature_me.replaceXml(ext_param);
        	var XMLDoc = signature_me.loadXMLString(ext_param);  
        	XMLDoc.async=false;
        	var rootNode = XMLDoc.documentElement;
        	var recordnode = rootNode.childNodes;
        	for(var i=0;i<recordnode.length;i++){
        		if(recordnode[i].nodeType==1){
        			var MarkID = recordnode[i].getAttribute("MarkID");
        			var MarkName = recordnode[i].getAttribute("MarkName");
        			var MarkType = recordnode[i].getAttribute("MarkType");
        			var MarkSize = recordnode[i].getAttribute("MarkSize");
        			var MarkData = recordnode[i].getAttribute("MarkData");
        			var strRecord ={};
        			strRecord["markid"]=MarkID;
        			strRecord["markname"]=MarkName;
        			strRecord["marktype"]=MarkType;
        			strRecord["marksize"]=MarkSize;
        			strRecord["filename"]=MarkID+MarkType;
        			strRecord["localname"]=MarkName+MarkType;
        			strRecord["path"]=MarkData;
        			strRecord["flag"]="1";
        			list.push(strRecord);
        		}
        	}
    	}
    	return list;
    },
    /**
     * 删除图片
     */
    deleteFile:function(){
    	var selRecords = signature_me.grid.getSelectionModel().getSelection();
    	if(selRecords.length == 0){
    		Ext.showAlert(common.msg.selectData);
    		return;
    	}
    	Ext.showConfirm(common.msg.isDelete,function(btn){
             if(btn=="yes"){
            	 for(var i=0;i<selRecords.length;i++){
            		 if(selRecords[i].data.markid!==""){
            			 signature_me.delMarkId +=","+selRecords[i].data.markid;
            		 }
            	 }
            	 signature_me.grid.store.remove(selRecords);
            	 signature_me.file_modify = 'true';
             }
        });
    },
    /**
     * 上传图片
     */
    uploadFile:function(list){
		if(list.length!=0){
			for(var i=0;i<list.length;i++){
				var localname = list[i].localname; 
				var filename = list[i].filename;
				var id = list[i].fileid;    
				var localname =list[i].localname;  
				localname=replaceAll(localname,",","，");
				var markname = localname.substring(0,localname.lastIndexOf("."));
				var marktype = localname.substring(localname.lastIndexOf("."));
				var size = list[i].size.replace("\r","").replace("\n","").replace("\r\n","");
				var path = list[i].path;
				var strRecord ={};
				strRecord["markid"]=id;
				strRecord["markname"]=markname;
				strRecord["marktype"]=marktype;
				strRecord["marksize"]=size;
				strRecord["filename"]=filename;
				strRecord["localname"]=localname;
				strRecord["path"]=path;
				strRecord["flag"]="0";
				var index = signature_me.grid.store.getCount();	
				signature_me.grid.store.insert(index,strRecord);
			}
			signature_me.file_modify = 'true';
		}
    },
    /**
     * 选人
     */
    persionpicker:function(flag){
    	//调用选人控件        	
		var p = new PersonPicker({
			isSelfUser:flag=='2'?true:false,//是否选择自助用户
			multiple: false,//为true可以多选
			isPrivExpression:false,//是否启用人员范围（含高级条件）
			validateSsLOGIN:true,//是否启用认证库
			selectByNbase:flag=='2'?true:false,//是否按不同人员库显示
			selfUserIsExceptMe:false,//业务用户时是否排除自己
			text: MB.LABEL.SURE,
			callback: function (c) {
				signature_me.name = c.name;
				signature_me.id=c.id;
				signature_me.b0110=c.b0110;
				signature_me.e01a1=c.e01a1;
				signature_me.e0122=c.e0122;
				signature_me.nbase=c.nbase;
				var username = Ext.getCmp('username');
				username.setValue(signature_me.name);
				signature_me.username_modify = 'true';
			}
		}, Ext.getCmp('username').getEl());
		p.open();
    },
    /**
     * 删除签章信息
     */
    delSignature:function(flag,signatureid){
		if(flag==1){
			Ext.showConfirm(MB.LABEL.SUREDELETESIGNATURE, function(optional){
	    		if(optional=='yes'){
    				var map = new HashMap();
    				map.put('signatureid',signatureid);
    				map.put('flag',flag+'');
    				Rpc({functionId:'MB00007003',async:false,success:function(){
    					var store = Ext.data.StoreManager.lookup('signature_dataStore'); 
    					store.load();
    				},scope:this},map);
	    		}else
	    			return;
			});
		}else{//批量删除
			var selectRecord = signature_me.tableObj.tablePanel.getSelectionModel().getSelection();
			if(selectRecord.length<1){
				Ext.showAlert(MB.LABEL.NOSELECTRECORD);
				return;
			}
			var records = [];
			for(var i=0;i<selectRecord.length;i++){
				records.push(selectRecord[i].data.signatureid+'');
			}
			Ext.showConfirm(MB.LABEL.SUREDELETESIGNATURE, function(optional){
	    		if(optional=='yes'){
    				var hashvo = new HashMap();
    				hashvo.put("selectdata",records);
    				hashvo.put('flag',flag+'');
    				Rpc({functionId:'MB00007003',async:false,success:function(){
    					var store = Ext.data.StoreManager.lookup('signature_dataStore'); 
    					store.load();
    				},scope:this},hashvo);
	    		}else
	    			return;
			});
		}
	},
	replaceXml:function(signXml){
		signXml=replaceAll(signXml,"＜","<");
		signXml=replaceAll(signXml,"＞",">");
		signXml=replaceAll(signXml,"＇","'");
		signXml=replaceAll(signXml,"＂",'"');
		signXml=replaceAll(signXml,"&","");
		return signXml;
	},
	loadXMLString:function(dname){
		  try{
			  xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
			  xmlDoc.async="false";
			  xmlDoc.loadXML(dname);
		  }
		  catch(e){
			  try{
				    parser=new DOMParser();
				    xmlDoc=parser.parseFromString(dname,"text/xml");
			  }
			  catch(e) {
			  	Ext.showAlert(e.message);
			  }
		  }
		  return xmlDoc;
	}
});