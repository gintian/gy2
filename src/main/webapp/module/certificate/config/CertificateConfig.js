/**
 * 证书信息集配置主页面
 */
Ext.define('CertificateUL.CertificateConfig',{
    constructor:function(config){
    	certificate = this;
        var myCheckboxGroup;
        var nbaseCheck = new Array();
    	this.init();
    },
  

	showPanel: function(){
		
	    var formPanel = Ext.create('Ext.form.Panel', {
	    	 width:'100%',
	    	 border: 0,    //无边框
	         items: [{
	         	 xtype:'panel',
	             width:'100%',
	             margin:'20 0 10 0',
	             border:0,
	             layout:{
	                 type:'vbox'
	             },
	             items:[{
		            	 xtype:'component',
		            	 html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">选择证书人员库</div>'
	            		 },
	            		 myCheckboxGroup]
	         },{
	        	 xtype:'component',
	        	 html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">选择证书代码类</div>',
	    	 },{
	         	 xtype:'panel',
	             width:'100%',
	             margin:'20 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书类别代码类</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode:'local', 
	                 id:'certCategoryCode',
	                 name:'certCategoryCode',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value:'',
	                 emptyText:'请选择...',
	             },{
	 		    	xtype: 'label',
			        html: '<span style="color:red;">*</span>',
			        margin: '0 0 0 5'
			    }]
	         },{
	        	 xtype:'component',
	        	 html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">选择证书信息集</div>',
	    	 },{
	             xtype:'panel',
	             width:'100%',
	             margin:'20 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                     xtype:'component',
	                     width:160,
	                     html:'<div style="float:right;padding-right:10px;">信息集</div>'
	                 },{
	                     xtype: 'combobox',
	                     width:300,
	                     height:23,
	                     queryMode: 'local', 
	                     id:'certSubset',
	                     name:'certSubset',
	                     displayField:'dataName',
	                     valueField:'dataValue',
	                     value: '',
	                     emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 支持附件的人员信息集 )</div>',
				        margin: '0 0 0 5'
				 }]
	         },{
	             xtype:'panel',
	             width:'100%',
	             margin:'0 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书类别</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode:'local', 
	                 id:'certCategory',
	                 name:'certCategory',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 关联证书类别的代码类指标 )</div>',
				        margin: '0 0 0 5'
				 }]
	         },{
	             xtype:'panel',
	             width:'100%',
	             margin:'0 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书编号</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode:	'local', 
	                 id:'certNOItemId',
	                 name:'certNOItemId',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 字符型指标 )</div>',
				        margin: '0 0 0 5'
				 }]
	         },{
	             xtype:'panel',
	             width:'100%',
	             margin:'0 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书名称</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode: 'local', 
	                 id:'certName',
	                 name:'certName',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 字符型指标 )</div>',
				        margin: '0 0 0 5'
				    }]
	         },{
	             xtype:'panel',
	             width:'100%',
	             margin:'0 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书到期日期</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode: 'local', 
	                 id:'certEndDate',
	                 name:'certEndDate',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 日期型指标 )</div>',
				        margin: '0 0 0 5'
				    }]
	         },{
	             xtype:'panel',
	             width:'100%',
	             margin:'0 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书状态</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode: 'local', 
	                 id:'certStatus',
	                 name:'certStatus',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 关联代码类83的指标)</div>',
				        margin: '0 0 0 5'
				    }]
	         },{
	             xtype:'panel',
	             width:'100%',
	             margin:'0 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书所属组织</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode: 'local', 
	                 id:'certOrganization',
	                 name:'certOrganization',
	                 displayField:'dataName',
	                 valueField:'dataValue',
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 关联代码类UN,UM的指标 )</div>',
				        margin: '0 0 0 5'
				    }]
	         },{
	        	 xtype:'panel',
	        	 width:'100%',
	        	 margin:'0 0 10 0',
	        	 border:0,
	        	 layout:{
	        		 type:'hbox',
	        		 align:'middle'
	        	 },
	        	 items:[{
	        		 xtype:'component',
	        		 width:160,
	        		 html:'<div style="float:right;padding-right:10px;">证书是否借出</div>'
	        	 },{
	        		 xtype: 'combobox',
	        		 width:300,
	        		 height:23,
	        		 queryMode: 'local', 
	        		 id:'certBorrowStateId',
	        		 name:'certBorrowState',
	        		 displayField:'dataName',
	        		 valueField:'dataValue',
	        		 value: '',
	        		 emptyText:'请选择...'
	        	 },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span><div style="float:right;padding-left:10px;">(说明: 关联代码类45的指标)</div>',
				        margin: '0 0 0 5'
				    }]
	         },{
	        	 xtype:'component',
	        	 html:'<div style="font-weight:bold;margin-left:20px;padding-left:5px;border-left: 5px solid #5190ce;text-align:left;">选择证书借阅信息集</div>',
	    	 },{
	         	 xtype:'panel',
	             width:'100%',
	             margin:'20 0 10 0',
	             border:0,
	             layout:{
	                 type:'hbox',
	                 align:'middle'
	             },
	             items:[{
	                 xtype:'component',
	                 width:160,
	                 html:'<div style="float:right;padding-right:10px;">证书借阅信息集</div>'
	             },{
	                 xtype: 'combobox',
	                 width:300,
	                 height:23,
	                 queryMode:'local', 
	                 id:'certBorrowSubset',
	                 name:'certBorrowSubset',
	                 displayField:'dataName',
	                 valueField:'dataValue',
//	                 editable : false,//不允许编辑 
	                 value: '',
	                 emptyText:'请选择...'
	             },{
		 		    	xtype: 'label',
				        html: '<span style="color:red;">*</span>',
				        margin: '0 0 0 5'
				    }]
	         },{
	        	 xtype:'component',
	        	 id:'insertid',
	        	 margin:'0 0 20 0',
	        	 html:'<div style="font-weight:bold;margin-left:20px;padding-left:120px;text-align:left;">'
	        		 +'如果还没有借阅信息集，请点击 <a href="javascript:void(0);" onclick="certificate.insertSubset()">这里</a> 系统将为您自动创建</div>',
	    	 }]
	     });
	    
	    var titlePanel =  Ext.create('Ext.Panel', {
	        border: 0,
	        title:'<div style="float:left;">配置</div>'
	        	+'<div style="float:right;padding-right:10px;font-size:16px;"><a href="javascript:void(0);" onclick="certificate.save(1);" >保存</a></div>',
	        layout: {
	            type: 'vbox',
	            align: 'stretch'
	        },
	        renderTo: document.body,
	        items: [{
	            xtype: 'panel',
	            border: 0, 
	            width:'100%',
	            autoScroll: true,
	            items:formPanel,
	            flex: 10
	        }
//	        ,{
//	            xtype: 'panel',
//	            width:'100%',
//	            height:40,
//	            items: [{
//	    		    xtype: 'button',
//	    		    text: '保存',
//	    		    margin:'8 0 0 10',
//	    		    handler: function(){
//	    		    	certificate.save(1);
//	    		    }
//	    		}],
//	        }
	        ]
	    });
	    
	    new Ext.Viewport( {
			layout : "fit",
			items:[titlePanel]
		});
	    
	   
	},
	   
	
	certCategoryCodeChange:function(t,newValue,oldValue){
		
		if("请选择..." == newValue || Ext.isEmpty(newValue)){
			Ext.getCmp('certCategory').setValue('');
			Ext.getCmp('certCategory').disable();  
			return;
		}else{
			Ext.getCmp('certCategory').enable();
			Ext.getCmp('certCategory').setValue('');
		}
		var certSubsets =  Ext.getCmp("certSubset").getValue();
		if("请选择..." == certSubsets  || Ext.isEmpty(certSubsets))
			certSubsets = "";
		
		var vo = new HashMap();
		vo.put("flag","4");
		vo.put("certSubsetid",certSubsets);
	    vo.put('certCategoryCode',newValue);
	    Rpc({functionId:'CF01010001',success:function(resp){
	    	var resultObj = resp.responseText;
	    	resultObj = JSON.parse(resultObj);
	    	
	    	var certCategoryStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:resultObj.certCategorylist
	        });
		    
	    	var certCategory = Ext.getCmp("certCategory"); 
	    	if(certCategory != ""){
	    		Ext.getCmp('certCategory').enable(); 
	    	}
		    certCategory.setStore(certCategoryStore);
	    }},vo);	
	},
	/**
	 * 借阅子集下拉 监听事件
	 */
	certBorrowSubsetChange:function(t,newValue,oldValue){

		var htmlf = '<div style="font-weight:bold;margin-left:20px;padding-left:120px;text-align:left;">如果还没有借阅信息集，请点击 ';
		var htmle = ' 系统将为您自动创建</div>';
		
		if(newValue == "" || newValue == "请选择..."){
			var html = htmlf + '<a href="javascript:void(0);" onclick="certificate.insertSubset()">这里</a>'+ htmle;
			Ext.getCmp('insertid').setHtml(html);
		}else{
			var html = htmlf + '<font style="color:#979797;">这里</font>'+ htmle;
			Ext.getCmp('insertid').setHtml(html);
		}
	},
	
	//添加证照借阅子集
	insertSubset:function(){
		var certBorrowSubsetValue = Ext.getCmp("certBorrowSubset").getValue();
		var certCategoryCodeValue = Ext.getCmp("certCategoryCode").getValue();
		if("请选择..." == certBorrowSubsetValue)
			certBorrowSubsetValue = "";
		
		if("请选择..." == certCategoryCodeValue)
			certCategoryCodeValue = "";

		if(!Ext.isEmpty(certBorrowSubsetValue)){
			Ext.showAlert("借阅信息集已存在！");
			return;
		}
		
		if(Ext.isEmpty(certCategoryCodeValue)){
			Ext.showAlert(cf.msg.fillinCfCode);
			return;
		}
		// 点击这里后，马上置灰该按钮
		var html = '<div style="font-weight:bold;margin-left:20px;padding-left:120px;text-align:left;">'
   		 		+'如果还没有借阅信息集，请点击 <font style="color:#979797;">这里</font> 系统将为您自动创建</div>';
		Ext.getCmp('insertid').setHtml(html);
		
		var vo = new HashMap();
		vo.put('certCategoryCode',certCategoryCodeValue);
		vo.put("flag","3");
	    Rpc({functionId:'CF01010001',success:function(resp){
	    	var resultObj = resp.responseText;
	    	resultObj = JSON.parse(resultObj);
	    	var msg = resultObj.msg;
	    	// 证书借阅信息集store
	    	var certBorrowSubsetStore = Ext.create('Ext.data.Store',{
	         	fields:['dataValue','dataName'],
	         	data:resultObj.subsetAlllist
	         });
	    	
	 	    var certBorrowSubset = Ext.getCmp("certBorrowSubset");
	 	    certBorrowSubset.setStore(certBorrowSubsetStore);
	 	    certBorrowSubset.setValue(resultObj.certBorrowSubset);
	    	
	    	if(msg == "true"){
	    		Ext.showAlert(cf.msg.addSubset);
	    		return;
	    	}
	    }},vo);
	},
	
	//选中证书信息集加载对应指标
	certSubsetChange:function(t,newValue,oldValue){
		
		if("请选择..." == newValue || Ext.isEmpty(newValue))
			newValue = "";
		var certCategoryCodeValue = Ext.getCmp("certCategoryCode").getValue();
		if(Ext.isEmpty(certCategoryCodeValue) || "请选择..." == certCategoryCodeValue)
			certCategoryCodeValue = ""
				
		var vo = new HashMap();
		vo.put("flag","5");
		vo.put('certCategoryCode',certCategoryCodeValue);
	    vo.put('certSubsetid',newValue);
	    Rpc({functionId:'CF01010001',success:function(resp){
	    	
	    	var resultObj = resp.responseText;
	    	resultObj = JSON.parse(resultObj);
	    	
	    	// 字符型指标
			var certStrStore = Ext.create('Ext.data.Store',{
		        fields:['dataValue','dataName'],
		        data:resultObj.certStrlist
		    });
			// 日期型指标
			var certDateStore = Ext.create('Ext.data.Store',{
		        fields:['dataValue','dataName'],
		        data:resultObj.certDatelist
		    });
	    	// 证书类别下拉列表
	    	var certCategoryStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:resultObj.certCategorylist
	        });
	    	
	    	var certCategoryCode = Ext.getCmp("certCategoryCode");
	    	var certCategoryCodeValue = certCategoryCode.getValue();
	    	var certCategory = Ext.getCmp("certCategory"); 
	    	if(certCategoryCodeValue != ""){
	    		certCategory.setStore(certCategoryStore);
	    		certCategory.setValue('');
	    		Ext.getCmp('certCategory').enable(); 
	    	}else{
	    		certCategory.setStore(certCategoryStore);
	    		certCategory.setValue('');
	    	}
		    
		    var certNOItemId =Ext.getCmp("certNOItemId"); 
		    certNOItemId.setStore(certStrStore);
		    certNOItemId.setValue('');
		        			
		    var certName =Ext.getCmp("certName"); 
		    certName.setStore(certStrStore);
		    certName.setValue('');
		    
		    var certEndDate =Ext.getCmp("certEndDate"); 
		    certEndDate.setStore(certDateStore);
		    certEndDate.setValue('');
		    
		    var  certStatus= Ext.getCmp("certStatus"); 
		    var certStatusStore = Ext.create('Ext.data.Store',{
		    	fields:['dataValue','dataName'],
		        data:resultObj.certStatuslist
		    });
		    certStatus.setStore(certStatusStore);
		    certStatus.setValue('');
		        				
		    var certOrganization = Ext.getCmp("certOrganization");
		    var certOrganizationStore = Ext.create('Ext.data.Store',{
		   	 	fields:['dataValue','dataName'],
		   		data:resultObj.certOrganizationlist
		    });
		    
		    certOrganization.setStore(certOrganizationStore);
		    certOrganization.setValue('');
		    
		    var certBorrowStore = Ext.create('Ext.data.Store',{
		    	fields:['dataValue','dataName'],
		    	data:resultObj.certBorrowlist
		    });
		    
		    var certBorrowState = Ext.getCmp("certBorrowStateId");
		    certBorrowState.setStore(certBorrowStore);
		    certBorrowState.setValue('');
	    }},vo);	
		
	},
	
	
	//显示证书信息集配置配置参数指标
	showParamField:function(resultObj){
		// 字符型指标
		var certStrStore = Ext.create('Ext.data.Store',{
	        fields:['dataValue','dataName'],
	        data:resultObj.certStrlist
	    });
		// 日期型指标
		var certDateStore = Ext.create('Ext.data.Store',{
	        fields:['dataValue','dataName'],
	        data:resultObj.certDatelist
	    });
		// 证书类别下拉列表
		var certCategorylistStore = Ext.create('Ext.data.Store',{
			fields:['dataValue','dataName'],
	        data:resultObj.certCategorylist
	    });
		
	    var certCategory = Ext.getCmp("certCategory"); 
	    certCategory.setStore(certCategorylistStore );
	    certCategory.setValue(resultObj.certMap.cert_category_itemid);
	    
	    var certNOItemId =Ext.getCmp("certNOItemId"); 
	    certNOItemId.setStore(certStrStore);
	    certNOItemId.setValue(resultObj.certMap.cert_no_itemid);
	        			
	    var certName =Ext.getCmp("certName"); 
	    certName.setStore(certStrStore);
	    certName.setValue(resultObj.certMap.cert_name);
	    
	    var certEndDate =Ext.getCmp("certEndDate"); 
	    certEndDate.setStore(certDateStore);
	    certEndDate.setValue(resultObj.certMap.cert_enddate_itemid);
	    
	    var  certStatus= Ext.getCmp("certStatus"); 
	    var certStatusStore = Ext.create('Ext.data.Store',{
	    	fields:['dataValue','dataName'],
	        data:resultObj.certStatuslist
	   });
	   certStatus.setStore(certStatusStore);
	   certStatus.setValue(resultObj.certMap.cert_status);
	        				
	   var certOrganization = Ext.getCmp("certOrganization");
	   var certOrganizationStore = Ext.create('Ext.data.Store',{
	   		fields:['dataValue','dataName'],
	   		data:resultObj.certOrganizationlist
	   });
	   certOrganization.setStore(certOrganizationStore);
	   certOrganization.setValue(resultObj.certMap.cert_organization);
	   
	   var certBorrowStore = Ext.create('Ext.data.Store',{
	    	fields:['dataValue','dataName'],
	    	data:resultObj.certBorrowlist
	    });
	    
	    var certBorrowState = Ext.getCmp("certBorrowStateId");
	    certBorrowState.setStore(certBorrowStore);
	    certBorrowState.setValue(resultObj.certMap.cert_borrow_state);
	},
	
	
	save:function(msgflag){
		
		var certMap = new HashMap();
		var categoryCodeValue = Ext.getCmp("certCategoryCode").getValue();
		if(Ext.isEmpty(categoryCodeValue)){
			Ext.showAlert("证书类别代码类不能为空！");
			return;
		}
		
		var certSubset = Ext.getCmp("certSubset").getValue();
		if(Ext.isEmpty(certSubset)){
			Ext.showAlert("信息集不能为空！");
			return;
		}
		
		var certCategory = Ext.getCmp("certCategory").getValue();
		if(Ext.isEmpty(certCategory)){
			Ext.showAlert("证书类别指标不能为空！");
			return;
		}
		
		var certNOItemId = Ext.getCmp("certNOItemId").getValue();
		if(Ext.isEmpty(certNOItemId)){
			Ext.showAlert("证书编号指标不能为空！");
			return;
		}
		
		var certName = Ext.getCmp("certName").getValue();
		if(Ext.isEmpty(certName)){
			Ext.showAlert("证书名称指标不能为空！");
			return;
		}
		
		var certEndDate = Ext.getCmp("certEndDate").getValue();
		if(Ext.isEmpty(certEndDate)){
			Ext.showAlert("证书到期日期指标不能为空！");
			return;
		}
		
		var certStatus = Ext.getCmp("certStatus").getValue();
		if(Ext.isEmpty(certStatus)){
			Ext.showAlert("证书状态指标不能为空！");
			return;
		}
		
		var certOrganization = Ext.getCmp("certOrganization").getValue();
		if(Ext.isEmpty(certOrganization)){
			Ext.showAlert("证书所属组织指标不能为空！");
			return;
		}
		
		var certBorrowSubset = Ext.getCmp("certBorrowSubset").getValue();
		if(Ext.isEmpty(certBorrowSubset)){
			Ext.showAlert("证书借阅子集不能为空！");
			return;
		}
		
		var certBorrowStateId = Ext.getCmp("certBorrowStateId").getValue();
		if(Ext.isEmpty(certBorrowStateId)){
			Ext.showAlert("证书是否借出指标不能为空！");
			return;
		}
		
	    certMap.put("cert_category_code", categoryCodeValue);
	 	certMap.put("cert_subset", certSubset);
	 	certMap.put("cert_category_itemid", certCategory);
	 	certMap.put("cert_no_itemid", certNOItemId);
	 	certMap.put("cert_name", certName);
	 	certMap.put("cert_enddate_itemid", certEndDate);
	 	certMap.put("cert_status", certStatus);
	 	certMap.put("cert_organization", certOrganization);
	 	certMap.put("cert_borrow_subset", certBorrowSubset);
	 	certMap.put("cert_borrow_state", Ext.getCmp("certBorrowStateId").getValue());
	 	// 校验档案管理栏目设置
	 	certMap.put("check_table_scheme", "1");
	 	
	    var checkboxgroup = Ext.getCmp('checkboxgroup').getChecked();
	    nbaseCheck = [];
	    Ext.Array.each(checkboxgroup, function(item){
	    	nbaseCheck.push(item.name);
	    });
		certMap.put("cert_nbase", nbaseCheck);
		var certMapJson = JSON.stringify(certMap);
		var vo = new HashMap();
		vo.put("flag","2");
	    vo.put("certMapJson",certMapJson);
	    Rpc({functionId:'CF01010001',success:function(resp){
			if(msgflag == 1){
				var resultObj = resp.responseText;
		    	resultObj = JSON.parse(resultObj);
		    	var msg = resultObj.msg;
		    	if(msg == "true"){
		    		Ext.showAlert(cf.msg.saveSucess);
		    		return;
		    	}else{
		    		Ext.showConfirm(msg, function(flag){
		                if("yes" == flag){
		                	// 如果确定删除档案管理栏目设置 则不需要再校验
		                	certMap.put("check_table_scheme", "0");
		                	var certMapJson = JSON.stringify(certMap);
		            		var vo = new HashMap();
		            		vo.put("flag","2");
		            	    vo.put("certMapJson",certMapJson);
		            	    Rpc({functionId:'CF01010001',success:function(resp){
		            	    	if(msgflag == 1){
		            	    		var resultObj = resp.responseText;
		            		    	resultObj = JSON.parse(resultObj);
		            		    	var msg = resultObj.msg;
		            		    	if(msg == "true"){
		            		    		Ext.showAlert(cf.msg.saveSucess);
		            		    		return;
		            		    	}
		            	    	}
		            	    }},vo);	
		                }
		            });
		    	}
			}
		}},vo);	
	},
	//初始化加载数据方法
	init:function(){
		var vo = new HashMap();
		vo.put("flag","1");
		Rpc({functionId:'CF01010001',success:function(resp){
			
			var resultObj = resp.responseText;
			resultObj = JSON.parse(resultObj);
		    var myCheckboxItems = [];
		    if(resultObj.certMap && resultObj.certMap.cert_nbase){
		    	nbaseCheck = resultObj.certMap.cert_nbase;
		    }else{
		    	nbaseCheck = [];
		    }
		    var nbaseList=resultObj.nbaseList;
	        for (var i = 0; i < nbaseList.length; i++) {
	            var nbaseMap = nbaseList[i];
	
	            for (var key in nbaseMap) {
	                var boxLabel = key;
	                var name = nbaseMap[key];
	
	                if (Ext.Array.contains(nbaseCheck, name)) {
	                    myCheckboxItems.push({
	                        boxLabel: boxLabel,
	                        name: name,
	                        checked: true,
	                    });
	                } else {
	                    myCheckboxItems.push({
	                        boxLabel: boxLabel,
	                        name: name,
	                        checked: false,
	                    });
	                }
	            }
		    }
		    
			 myCheckboxGroup = new Ext.form.CheckboxGroup({
		        xtype : 'checkboxgroup',
		        id: 'checkboxgroup',
		        margin:'10 0 20 50',
		        width: 800,
		        columns : 5,
		        items : myCheckboxItems
		    });
		    
		    certificate.showPanel();
			
		    var certCategoryCodeStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:resultObj.certCategoryCodeList
	        });
		    
		    var certCategoryCode = Ext.getCmp("certCategoryCode");
		    certCategoryCode.setStore(certCategoryCodeStore);
		    // 证书信息集store
		    var certSubsetStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:resultObj.subsetlist
	        });
			
	        var certSubset = Ext.getCmp("certSubset");
	        certSubset.setStore(certSubsetStore);
	        // 证书借阅信息集store
	        var certBorrowSubsetStore = Ext.create('Ext.data.Store',{
	        	fields:['dataValue','dataName'],
	        	data:resultObj.subsetAlllist
	        });
		    
		    var certBorrowSubset = Ext.getCmp("certBorrowSubset");
		    certBorrowSubset.setStore(certBorrowSubsetStore);
		    
		    var certBorrowStore = Ext.create('Ext.data.Store',{
		    	fields:['dataValue','dataName'],
		    	data:resultObj.certBorrowlist
		    });
		    
		    var certBorrowState = Ext.getCmp("certBorrowStateId");
		    certBorrowState.setStore(certBorrowStore);
		    
		    if(resultObj.certMap && resultObj.certMap.cert_category_code){
		    	certCategoryCode.setValue(resultObj.certMap.cert_category_code);
		    }else{
		    	Ext.getCmp('certCategory').disable(); 
		    }
	        
	        if(resultObj.certMap && resultObj.certMap.cert_subset){
	        	certSubset.setValue(resultObj.certMap.cert_subset);
	        	certificate.showParamField(resultObj);
	        }
	        
	        certSubset.on('change',certificate.certSubsetChange);
	        certCategoryCode.on('change',certificate.certCategoryCodeChange);
	        
	        certBorrowSubset.on('change',certificate.certBorrowSubsetChange);
	        if(resultObj.certMap == null)
		    	return;  
	        certBorrowSubset.setValue(resultObj.certMap.cert_borrow_subset);
	        
	    }},vo);
	}
});
