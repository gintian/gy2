 /**
 *按模板导入（导出）组件，download为下载按钮事件；see为浏览（上传）按钮事件
 */
 Ext.define('SalaryUL.ImportAsTemplate',{
        constructor:function(config){
        	ImportAsTemplateScope = this; 
        	ImportAsTemplateScope.download = config.download;  
        	ImportAsTemplateScope.see = config.see;     	
			ImportAsTemplateScope.init(); 
			ImportAsTemplateScope.returnBackFunc=config.returnBackFunc;
        },
		init:function()  
		{
		   	var	win=Ext.widget("window",{
		          title:'导入',  
		          height:200,  
		          width:300,
		          layout:'fit',
		          id:'winImportTemplate',
				  modal:true,
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
					        text: '1、下载模板文件'
		                },
		                {
		                    x:200,
		         			y:30,
					        xtype: 'button',
					        id:'download',
					        text: '下载',
					        handler:function(){
					        	Ext.callback(eval(ImportAsTemplateScope.download),null,[]);
					        }
					    },
		                {
		                    x:60,
		         			y:80,
					        xtype: 'label',
					        forId: 'myFieldId',
					        text: '2、请选择导入文件'
		                },
		                {
		                    x:200,
		         			y:80,
					        xtype: 'button',
					        id:'see',
					        text: '浏览',
					        handler:function(){
					        	Ext.callback(eval(ImportAsTemplateScope.see),null,[]);
					        }
					    }]
		          }]
		    });
		    win.show();  
		},
		winClose:function(){
			Ext.getCmp('winImportTemplate').close();
		},
		back:function(){
			Ext.callback(eval(ImportAsTemplateScope.returnBackFunc),null,[]);
		}
 });
