/**
 * 薪资发放-导入数据-弹出上传文件框
 */
Ext.Loader.setPath("SYSF","../../../../components/fileupload");
Ext.define('SalaryUL.inout.ImportTable',{
		requires:['SYSF.FileUpLoad'],//加载上传控件js
        constructor:function(config){
			import_me = this;
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
		   				error:function(list){
		   					Ext.showAlert(list[0].msg);
		   				},
		   				success:function(list){
		   					var fileid= list[0].fileid;
			   				win.close();
	                    	Ext.require('SalaryUL.inout.SetImpRelation',function(){
	                    		Ext.create("SalaryUL.inout.SetImpRelation",{fileid:fileid,salaryid:config.salaryid,appdate:config.appdate,count:config.count,imodule:config.imodule,viewtype:config.viewtype,onlynamedesc:config.onlynamedesc});
	                    	})
		   				}
	   				});
		   	
		  //上传导入弹出框
	   		var win=Ext.widget("window",{
	   			title: common.label.selectImportFile,
	            modal:true,
	            border:false,
            	width:380,
	   			height: 120,
	            closeAction:'destroy',
	            items:[{
	                xtype: 'panel',
	                border:false,
	         		layout:{  
    	             	type:'vbox',  
    	             	padding:'15 0 0 35', //上，左，下，右 
    	             	pack:'center',  
    	              	align:'middle'  
    	            },
	                items:[uploadObj]
	            }]
	    }); 
	   	win.show();
		 }
 });
