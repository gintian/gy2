/**
 * 声明本界面需要调用的类
 * */
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'SYSF': rootPath+'/components/fileupload'
	}
});
/**
 * 模板工具栏按钮类 TemplateToolBar.js 定义按钮调用的方法
 * */
Ext.define('TemplateUpDownUL.TemplateUpLoadData',{
	constructor:function(config){
		thisUpLoad=this;
		thisUpLoad.tab_id=config.id;
		thisUpLoad.ins_id=config.ins_id;
		thisUpLoad.task_id=config.task_id;
		thisUpLoad.infor_type=config.infor_type;
		thisUpLoad.view_type=config.view_type;
		thisUpload.allNum = config.allNum;
		thisUpLoad.uploadObj='',//上传控件
		thisUpLoad.win='',//弹窗
		thisUpLoad.init();
	},
	init:function(){
		//加载上传控件js
		Ext.require('SYSF.FileUpLoad',function(){
			//上传控件
			thisUpLoad.uploadObj = Ext.create("SYSF.FileUpLoad",{
		   				upLoadType:1,// 上传组件类型，1为单文件上传，2为多文件上传,3上传菜单（可以将上传按钮渲染到任何地方，只有上传按钮）
		   				fileExt:"*.xls;*.xlsx",// 文件的扩展名，限定上传文件的类型,默认是任意类型（*.*）,多个文件类型用分号隔开，例如*.jpg;*.jpeg;*.gif
		   				uploadUrl:"/case/",
		   				height: 30,
		   				//回调方法，失败
		   				error:function(){
			   				Ext.showAlert(common.msg.uploadFailed+"！");
		   				},
		   				success:function(list){
		   					var success = false;//是否上传成功
		   					var errorFileName = "";//导入数据失败提示excel
		   					var onlyname = "";//唯一标识
		   					var flag = '0';//上传程序是否执行完成
		   					var message = '';//报错提示信息
		   					var successNum=0;//导入成功的条数
		   					//console.log(list);//用ie的时候程序会被阻断
		   					var fileName = list[0].filename;
			   				var map = new HashMap();
				    		map.put("tabid",thisUpLoad.tab_id);
				    		map.put("fileName",fileName);
				    		map.put("ins_id",thisUpLoad.ins_id);
				    		map.put("task_id",thisUpLoad.task_id);
				    		map.put("infor_type",thisUpLoad.infor_type);
				    		Rpc({functionId:'MB00002009',async:true,success:function(response,action){
				    			var result = Ext.decode(response.responseText);
				    			success = result.succeed;
				    			if(!success){
				    				var message = result.message;
				    				if(message&&message.indexOf("拆分审批")!=-1){
				    					templateTool_me.checkSpllit(message);
				    				}
				    			}
				    			message = result.message;
				    			errorFileName =result.errorFileName;//导入数据失败提示excel
				    			onlyname =result.onlyname;
				    			flag = '1';
				    			successNum=result.updateCount;
				    			thisUpLoad.win.close();
				    		}},map); 
				    		var msgBox = Ext.MessageBox.show({
								title:'提示',
								//msg:'动态更新进度条和信息文字',
								modal:true,
								width:300,
								progress:true
							})
							var progressText='正在导入数据,请稍候...';//进度条信息
							var task = {
								run:function(){
									//进度条信息
									//更新信息提示对话框
									msgBox.updateProgress('',progressText,'');
									//完成上传文件，关闭更新信息提示对话框
									if(flag =='1'){
										Ext.TaskManager.stop(task);
										msgBox.hide();
					    				if(success){				 
					    					Ext.showAlert("成功导入"+successNum+"条数据。");
						    				templateTool_me.refreshAll("true");;//刷新列表
						    			}else{
						    				Ext.showAlert(message);
						    			}
									}
								},
								interval:1000//时间间隔
							}
							Ext.TaskManager.start(task);
		   				}
	   				});
		});
	  //弹窗
		thisUpLoad.win=Ext.widget("window",{
   			title: common.label.selectImportFile,
            modal:true,
            border:false,
        	width:380,
   			height: 140,
   			layout:{  
             	type:'vbox',  
             	align: 'stretch',
    	        pack :'center'
            },
            defaults:{  
   	             margins:'0 0 10 10'  
   	        },
            closeAction:'destroy',
            items:[{
                xtype: 'tbtext',
                text:'请用下载的Excel模板来导入数据！模板格式不允许修改！'
            },{
                xtype: 'panel',
                border:false,
         		layout:{  
	             	type:'vbox',  
	             	padding:'0 0 0 30', //内边距，顺时针
	             	pack:'center',  
	              	align:'middle'  
	            },
                items:[thisUpLoad.uploadObj]
            }]
		});
		thisUpLoad.win.show();
	}
});