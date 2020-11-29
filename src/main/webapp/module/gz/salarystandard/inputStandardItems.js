/**
 * 薪资标准导入
 * @createtime 2017-2-23
 * @author zhanghua	
 * 
 * */

Ext.Loader.setPath("SYSF","../../../components/fileupload");
Ext.define('Salarybase.inputStandardItems',{
	requires:['SYSF.FileUpLoad'],//加载上传控件js
	constructor:function(config) {
		var url=config.url;
		inputStandardItems = this;
		inputStandardItems.fileName="";
		inputStandardItems.init(url);
		inputStandardItems.exportfileName="";
		inputStandardItems.filePath="";
	},
	init:function(url){
		
		var item=inputStandardItems.getRequest(url);
		//上传控件
	   	var uploadObj = Ext.create("SYSF.FileUpLoad",{
	   				upLoadType:1,
	   				fileExt:"*.xls;*.xlsx",
	   				height: 30,
	   				//回调方法，失败
	   				error:function(){

	   				},
	   				success:function(list){
                        var filename = list[0].filename;
                        var filePath= list[0].path;
                        inputStandardItems.filePath=filePath;
		   				var map = new HashMap();
		   				map.put("pkgid", item.pkg_id);
		   				map.put("standardID",item.standardID);
		   				map.put("fileName",filename);
		   				map.put("filePath",filePath);
		   				map.put("type","0");//导入为0
			    		Rpc({functionId:'GZ00000601',success:function(response,action){
			    			var result = Ext.decode(response.responseText);
			    			var success = result.succeed;
			    			if(success){
			    				inputStandardItems.fileName=result.fileName;
			    				if(result.errorMsg==""){
			    					window.blur();
			    					window.close();
			    					window.opener.importExcelReload('y');
			    					
			    				}else if(result.fileName!=""){
			    					Ext.getCmp('spanel').setHidden(false);
			    					Ext.getCmp('fbtext').setText("<font color='#FF0000'>"+result.errorMsg+"，请点击</font>"+"&nbsp<a href='javascript:inputStandardItems.exportExcel()'>导出详情...</a>");
			    					
			    				}else{

			    					Ext.getCmp('tbtext').setText(result.errorMsg);
			    				}
			    				
			    			}else {
			    				Ext.showAlert(result.message);
			    			}
			    			
			    		}},map); 
	   				}
   				});
		
		var win = Ext.create('Ext.panel.Panel', {
	    height: 145,
	    resizable:false,
	    id:'mainWin',
	    width: 400,
	    renderTo:Ext.getBody(),
			layout:{  
             	type:'vbox',  
             	align: 'stretch',
    	        pack :'center'
            },
	    items:[{
            xtype: 'tbtext',
            id:'tbtext',
            padding:'0 0 0 32', //上，左，下，右 
            text: gz.label.importComment+'！ '
        },{
            xtype: 'panel',
            id:'uploadPanel',
            border:false,
     		layout:{  
             	type:'vbox',  
             	padding:'0 0 0 30', //上，左，下，右 
             	pack:'center',  
              	align:'middle'  
            },
            items:[uploadObj]
        },{
            xtype: 'panel',
            border:false,
            height:40,
            id:'spanel',
            width:400,
            hidden:true,
     		layout:{  
             	type:'hbox',  
//             	padding:'0 0 0 30', //上，左，下，右 
//             	pack:'center',  
              	align:'middle'  
            },
            items:[{
	            xtype: 'tbtext',
	            id:'fbtext',
	            padding:'0 0 0 32', //上，左，下，右 
	            width:280,
	            
	            text: ''
	        }
            ]
        }]
	});
		

	},
	
	//导出excel
	exportExcel:function(){
		if(inputStandardItems.exportfileName!=""){
			var fieldName = getDecodeStr(inputStandardItems.exportfileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
		}else{
			var map = new HashMap();
			map.put("pkgid", " ");
			map.put("standardID"," ");
			map.put("fileName",inputStandardItems.fileName);
			map.put("filePath",inputStandardItems.filePath);
			map.put("type","1");//导出为1
			Rpc({functionId:'GZ00000601',success:function(response,action){
					var result = Ext.decode(response.responseText);
					var success = result.succeed;
					if(success){
						inputStandardItems.exportfileName=result.fileName;
						var fieldName = getDecodeStr(result.fileName);
						window.location.target="_blank";
						window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
					}
			}},map);
		}
	},
	getRequest:function(url) {  
		var theRequest = new Object();
		if (url.indexOf("?") != -1) {
		   var str = url.substr(1);
			strs = str.split("&");
		    for(var i = 0; i < strs.length; i++) {
		    	var param = strs[i];
		    	var params=param.split("=");
		    	if (params.length>1)
		            theRequest[params[0]]=params[1]
		        else      
		        	theRequest[params[0]]="";  
	    	}
	    }

	    return theRequest;
	 }
})