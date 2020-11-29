/**
 *人事异动-新增照片
 */
Ext.Loader.setPath("SYSF",rootPath+"/components/fileupload");
Ext.define('TemplateCardUL.AddPhoto',{
		requires:['SYSF.FileUpLoad'],//加载上传控件js
        win:'',//页面要生成的窗口
        tab_id:'',
        templPropety:'',
        object_id:'',
        img_id:'',
        file_name:'',
        constructor:function(config){
			addPhoto_me = this;
			this.templPropety=config.templPropety;
			this.tab_id=config.templPropety.tab_id;
			this.object_id=config.object_id;
			this.img_id=config.img_id;
        	this.createSalary(); 
        },
		 createSalary:function()  
		 {
        	var eleImg =Ext.getDom(this.img_id);
        	var imgsrc= rootPath+'/images/photo.jpg';
        	if (eleImg!=null){
        		imgsrc=eleImg.src;
        	}
        	var personImg = Ext.create('Ext.Img', {
				id:'pho_temp',
			    src: imgsrc,
			    width:80,
			    height:120,
			    padding:0,
			    //margin:'0 0 0 50',//上，左，下，右 
				listeners: {}
			});
        	//上传控件
        	//人事 异动图片存储模板中 上传文件为临时文件
		   	var uploadObj = Ext.create("SYSF.FileUpLoad",{
		   				upLoadType:1,
		   				fileExt:"*.jpg;*.jpeg;*.bmp",
		   				uploadUrl:"/case/",
		   				height: 30,
		   				isTempFile:false,
						VfsFiletype:VfsFiletypeEnum.doc,
						VfsModules:VfsModulesEnum.RS,
						VfsCategory:VfsCategoryEnum.other,
						CategoryGuidKey:'',
		   				error:function(){
			   				Ext.showAlert(common.msg.uploadFailed+"！");
		   				},
		   				success:function(list){
		   					var fileid = list[0].fileid;
		   					var filename = list[0].filename;
							var src='/servlet/vfsservlet?fromjavafolder=true&fileid='+fileid;
		   					var eleImg =Ext.getDom("pho_temp");
		   					if (eleImg){
			   					eleImg.src=src;
                                addPhoto_me.file_name=fileid;
                                addPhoto_me.fileName=filename;
		   					}
		   					//var path = list[0].path;
		   					/*var map = new HashMap();
							map.put("type","getEncryFileName");
							map.put("filename",filename);
							map.put("fileid",fileid);
						    Rpc({functionId:'MB00004003',async:false,success:function(form,action){
						    	var result = Ext.decode(form.responseText);
								if(result.succeed){
									var filename=result.filename;
									var src='/servlet/DisplayOleContent?filename='+filename;
				   					var eleImg =Ext.getDom("pho_temp");
				   					if (eleImg){
					   					eleImg.src=src;
	                                    addPhoto_me.file_name=filename;
				   					}
				   					
								}else{
									addPhoto_me.file_name="";
								}
						    }},map);*/
		   				}
	   		});
		   	
		  //上传导入弹出框
	   		win=Ext.widget("window",{
	   			title: '选择照片',
	            modal:true,
	            border:false,
            	width:380,
	   			height: 250,
	            closeAction:'destroy',
	            items:[{
	                xtype: 'panel',
	                border:false,
	                width:'100%',
		   			height: '100%',
	         		layout:{  
    	             	type:'vbox',  
    	             	padding:'15 0 0 35', //上，左，下，右 
    	             	pack:'center',  
    	              	align:'stretchmax'  
    	            },
	                items:[{
	                	xtype:'container',
	                	layout:{  
	    	             	type:'hbox',  
	    	             	pack:'left',  
	    	              	align:'start'  
	    	            },
	                	items:personImg
	                },uploadObj]
	            }],
				  bbar:[
		          		{xtype:'tbfill'},
		          		{
		          			text:common.button.ok,
		          			scope:this,
		          			handler:function(){
			          			var map = new HashMap();
								map.put("type","");
							    if(addPhoto_me.file_name=="")
								{
									Ext.showAlert("请选择照片文件！");
									return;
								}
								
								map.put("fileid",addPhoto_me.file_name);
								map.put("filename",addPhoto_me.fileName);
								map.put("object_id",addPhoto_me.object_id);
								initPublicParam(map,addPhoto_me.templPropety);
								map.put("task_id",templateCard_me.cur_task_id);
							    Rpc({functionId:'MB00004003',async:false,success:function(form,action){
							    	var result = Ext.decode(form.responseText);
									if(result.succeed){
										var filename=result.fileid;
										var src='/servlet/vfsservlet?fromjavafolder=true&fileid='+filename;
					   					var eleImg =Ext.getDom(addPhoto_me.img_id);
					   					eleImg.src=src;
					   					win.close();
									}
							    }},map);
		          			
		          			}
		          		},
		          		{
		          			text:common.button.cancel,
		          			handler:function(){
		          				win.close();
		          			}
		          		},
		          		{xtype:'tbfill'}
		           ]
	    }); 
	   	win.show();
	}
 });
