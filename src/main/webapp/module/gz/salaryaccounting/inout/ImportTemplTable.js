/**
 * 薪资发放-导入模板数据-弹出上传文件框
 */
Ext.Loader.setPath("SYSF","../../../components/fileupload");
Ext.define('SalaryUL.inout.ImportTemplTable',{
		requires:['SYSF.FileUpLoad'],//加载上传控件js
        constructor:function(config){
			importTemp_me = this;
			importTemp_me.flag = config.flag;
			importTemp_me.appdate = config.appdate;
			importTemp_me.count = config.count;
        	this.createSalary(config); 
        },
		 createSalary:function(config)  
		 {
        	//上传控件
		   	var uploadObj = Ext.create("SYSF.FileUpLoad",{
				   		isTempFile:true,
						VfsModules:VfsModulesEnum.GZ,
						VfsFiletype:VfsFiletypeEnum.other,
						VfsCategory:VfsCategoryEnum.other,
						CategoryGuidKey:'',
		   				upLoadType:1,
		   				fileExt:"*.xls;*.xlsx",
		   				height: 30,
		   				//回调方法，失败
		   				error:function(){
							Ext.showAlert(common.msg.uploadFailed+"！");
		   				},
		   				success:function(list){
                            var fileid = list[0].fileid;
			   				var map = new HashMap();
				    		map.put("salaryid",config.salaryid);
				    		map.put("fileid",fileid);
				    		map.put("flag",importTemp_me.flag);
				    		map.put("appdate",importTemp_me.appdate);
							map.put("count",importTemp_me.count);
				    		Rpc({functionId:'GZ00000052',success:function(response,action){
				    			var result = Ext.decode(response.responseText);
				    			var success = result.succeed;
				    			var okCount =result.okCount;
				    			if (success) {  
				    				Ext.showAlert(common.msg.successImport+okCount+common.msg.dataNums+'！',function(){
				    					win.close();
				    					ImportAsTemplateScope.back();
                                        ImportAsTemplateScope.winClose();
				    				});
				    				if(result.errorFileName != ''){
				    					var errorFileName = getDecodeStr(result.errorFileName);
				    					window.location.target="_blank";
				    					window.location.href = "/servlet/vfsservlet?fileid="+ outName +"&fromjavafolder=true";
				    				}
				    			} else {  
				    				win.close();
				    				/*Ext.MessageBox.show({  
				    					title : common.button.promptmessage,  
				    					msg : Ext.decode(response.responseText).message, 
				    					buttons: Ext.Msg.OK,
				    					icon: Ext.MessageBox.INFO  
				    				}); */ 
				    				Ext.showAlert(Ext.decode(response.responseText).message);
				    			}
				    		}},map); 
		   				}
	   				});
		   	
		  //上传导入弹出框
	   		var win=Ext.widget("window",{
	   			title: common.label.selectImportFile,
	            modal:true,
	            border:false,
//	            alwaysOnTop:true,
            	width:380,
	   			height: 140,
	   			layout:{  
	             	type:'vbox',  
	             	align: 'stretch',
        	        pack :'center'
	            },
	            defaults:{  
	   	             margin:'0 0 10 10'  
	   	        },
	            closeAction:'destroy',
	            items:[{
	                xtype: 'tbtext',
	                text: gz.label.importComment+'！ '
	            },{
	                xtype: 'panel',
	                border:false,
	         		layout:{  
    	             	type:'vbox',  
    	             	padding:'0 0 0 30', //上，左，下，右 
    	             	pack:'center',  
    	              	align:'middle'  
    	            },
	                items:[uploadObj]
	            }]
	    }); 
	   	win.show();
		 }
 });
