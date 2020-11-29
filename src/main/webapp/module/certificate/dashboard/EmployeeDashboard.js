/**
 * 证书管理 自助用户登录门户
 */
/**
 * 借阅证书窗口显示必填项
 */
Ext.override(Ext.form.field.Base,{
    initComponent:function(){
        if(this.required!==undefined && this.required){
            if(this.fieldLabel){
            	this.fieldLabel = '<font color=red>*</font>' + this.fieldLabel;
            }
        }
        this.callParent(arguments);
    }
});

Ext.define('DashboardURL.EmployeeDashboard',{
	
	constructor:function(config) {
		employeeDashboard = this;
		// manager:管理员；employee:员工
		employeeDashboard.roleType = config.roleType;
		employeeDashboard.conditionitems = new Array();//条件列表部分
		employeeDashboard.tableObj = null;
		employeeDashboard.cerFieldsetid = null;
		employeeDashboard.vehicle = "2";
		var win;
	    this.init();
	},
	// 初始化函数
	init: function() {
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt",employeeDashboard.roleType);
		map.put("flag",",all,");
	    Rpc({functionId:'CF01030001',success:employeeDashboard.loadeTable},map);
		
	},
	
	loadeTable: function(form){
		var result = Ext.decode(form.responseText);
		if(!result.succeed || "1"==result.cerFlag){
			Ext.showAlert("请配置证书相关参数！");
			return;
		}
		
		var certSubetMsg = new Array();	
		certSubetMsg = result.certSubetMsg;
		employeeDashboard.cerFieldsetid = result.cerFieldsetid;
		employeeDashboard.userFullName = result.userFullName;
		employeeDashboard.userInfo = result.userInfo;
		
		employeeDashboard.fieldItems = result.fieldItems;
		
		var allPanel = Ext.create('Ext.panel.Panel', {
			id: 'panelid',
		    title: false,
		    border: false,
//		    layout:'vbox',//垂直布局
		    layout:'border',
		    autoScroll: true,
		    autoHeight: true,//自动高度 
		    bodyStyle:'background:#f0f2f5',//338DC9
		    items: [{
		    	region:'north',
		        width: '99%',//,Ext.getBody().getWidth()*0.5 
		        layout:'hbox',
		        height : 210,
		        margin: '10 10 0 10',
		        border: false,
		        bodyStyle:'background:#f0f2f5',//DBDBDB
	        	items:[{
//	        		id: 'calview',
	        		xtype : 'panel',
	        		width: '50%',
	        		height : '100%',
	        		margin: '0 5 0 0',
//	        		minWidth: 400,
	        		border: false,
	        		html:employeeDashboard.getEmpTotalPanel(certSubetMsg)
	        		}
	        	,{
	        		xtype:'panel',	
//	        		id: 'pieid',
	        		width: '50%',
	        		height : '100%',
	        		margin: '0 0 0 5',
	        		html  : "<div style='height:20%;width:100%;border:0px;margin:0 0 0 0;color:#979797;font-size: 18px;font-family:宋体;'>" 
					   	+"<div style='height:50px;width:100%;margin:10px 0 0 20px;color:#000000;'>"
					   		+"管理制度"
						+"</div>"
						+"</div>",
	        		
					layout:'hbox', 
	        		border: false,
	        		bodyStyle:'overflow-y:auto;overflow-x:hidden',
	        		items:[{
	        			layout: {
		                    type: 'table',
		                    columns: 7
		                },
		                margin: '30 0 0 20',
		                border:false,
		                items:employeeDashboard.getQualificationsPanel(),
	        		}]
	        	}]
		    }
		    ,{
		    	region:'center',
        		xtype : 'panel',
        		width: '99%',
        		margin: '10 10 10 10',
        		border: false,
        		items :[employeeDashboard.getEmpCertifcPanel(Ext.getBody().getHeight()-290)]
    		}],
    		listeners:{
    			'resize':function(){
    				Ext.getCmp('tablePanelid').setHeight(Ext.getBody().getHeight()-290);
    			}
    		},
		    renderTo: Ext.getBody()
		});
		
		this.mainPanel = Ext.widget("viewport",{
			  layout:'fit',
			  id:"mainPanel1",
			  items:allPanel
			});
		
		// 加载借阅证书列表
		employeeDashboard.borrowedCertificates();
	},
	/**
	 * 证书各种情况panel
	 */
	getEmpCertifcPanel:function(heightNum){
		
		var certifcPanel = Ext.create("Ext.container.Container",{
			height : '100%',
			layout:'vbox',
			items:[
				employeeDashboard.getEmpTopTitleTags()
				
				,employeeDashboard.titleTagsLine()
				
				// 表格控件
				,employeeDashboard.createTableObj(heightNum)
			]
		});
		
		return certifcPanel;
	},
	/**
	 * 
	 */
	titleTagsLine:function(){
		var widthall = Ext.getBody().getWidth();
		//<div style='position: absolute;height:22px;width:0;border:solid 3px #78C5FF;margin:0 0 0 0;'></div>
		var line1 = "<div style='position: absolute;height:0;width:"+widthall+"px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		var line2 = "<div style='position: absolute;height:0;width:30px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		var line3 = "<div style='position: absolute;height:0;width:90px;border:solid 1px #78C5FF;margin:0 0 0 0;'></div>";
		var line3_1 = "<div style='position: absolute;height:0;width:90px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		
		var line4 = "<div style='position: absolute;height:0;width:46px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		var line5 = "<div style='position: absolute;height:0;width:74px;border:solid 1px #78C5FF;margin:0 0 0 0;'></div>";
		var line5_1 = "<div style='position: absolute;height:0;width:74px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		
		var panel = Ext.create("Ext.panel.Panel",{
			border: false,
			width:'100%',
    		height : 2,
    		layout:'hbox',
			items:[{
			    	   xtype: 'label',
			    	   width:30,
			    	   height : 2,
			    	   border: false,
					   html  : line2
			       },{
			    	   id : 'browlineFF',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : false,
					   html  : line3
			       },{
			    	   id : 'browlineDB',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : true,
					   html  : line3_1
			       },{
			    	   xtype: 'label',
			    	   width:46,
			    	   height : 2,
			    	   border: false,
					   html  : line4
			       },{
			    	   id : 'yibrowlineFF',
			    	   xtype: 'label',
			    	   width:74,
			    	   height : 2,
			    	   border: false,
			    	   hidden : true,
					   html  : line5
			       },{
			    	   id : 'yibrowlineDB',
			    	   xtype: 'label',
			    	   width:74,
			    	   height : 2,
			    	   border: false,
			    	   hidden : false,
					   html  : line5_1
			       },{
			    	   xtype: 'label',
			    	   width:100,
			    	   height : 2,
			    	   border: false,
					   html  : line1
			       }]
		});
		return panel;
	},
	/**
	 * 获取对应的表格内容
	 */
	createTableObj:function(heightNum){
		
		var tableObj = Ext.create("Ext.container.Container",{
			id : 'tablePanelid',
			border:1,
			width:'100%',
    		height : heightNum,//350,//
    		margin: '5 10 0 10',
			layout:'fit',
			item:[]
		});
		
		return tableObj;
	},
	/**
	 * 权限范围内的借阅证书
	 */
	borrowedCertificates:function(){
		 var map = new HashMap();
		 // manager:管理员；employee:员工
		 map.put("opt",employeeDashboard.roleType);
		 map.put("flag",",borrowCerts,");
		 map.put("canBowFlag", employeeDashboard.vehicle);
		 Rpc({functionId:'CF01030001',success:employeeDashboard.borrowCertsOK,scope:this},map);
	},
	
	/**
	 * 借阅证书列表panel
	 */
	borrowCertsOK:function(form){
		
		var result = Ext.decode(form.responseText);
		var tableConfig=result.tableConfig;
		var obj = Ext.decode(tableConfig);
		var tableObjGrid = new BuildTableObj(obj);
		employeeDashboard.tableObj = tableObjGrid;
		var tablePanel = tableObjGrid.getMainPanel();
        Ext.getCmp("tablePanelid").removeAll();
        Ext.getCmp("tablePanelid").add(tablePanel);        
	},
	/**
	 * 切换证书分布情况页签
	 */
	clickBution:function(flag){
		// 借阅证书办理
		if(1 == flag){
			
			Ext.getCmp("browlineFF").setHidden(false);
			Ext.getCmp("browlineDB").setHidden(true);
			Ext.getCmp("yibrowlineFF").setHidden(true);
			Ext.getCmp("yibrowlineDB").setHidden(false);
			
			Ext.getCmp("showBorrowingid").setHidden(false);
			Ext.getCmp("borrowOperaid").setHidden(false);
			employeeDashboard.vehicle = "2";
			employeeDashboard.borrowedCertificates();
			document.getElementById('vehicle').checked = true;
		}
		// 已借阅证书
		else if(2 == flag){
			
			Ext.getCmp("browlineFF").setHidden(true);
			Ext.getCmp("browlineDB").setHidden(false);
			Ext.getCmp("yibrowlineFF").setHidden(false);
			Ext.getCmp("yibrowlineDB").setHidden(true);
			
			Ext.getCmp("showBorrowingid").setHidden(true);
			Ext.getCmp("borrowOperaid").setHidden(true);
			
			employeeDashboard.alreadyBorrowedCertificate();
		}
	},
	/**
	 * 已借阅证书列表
	 */
	alreadyBorrowedCertificate:function(){
		 var map = new HashMap();
		 // manager:管理员；employee:员工
		 map.put("opt",employeeDashboard.roleType);
		 map.put("flag",",alreadyBorrowCert,");
		 Rpc({functionId:'CF01030001',success:employeeDashboard.showBorrowedCert},map);
	},
	
	/**
	 * 已借阅证书列表panel
	 */
	showBorrowedCert:function(form){
		var result = Ext.decode(form.responseText);
		var tableConfig=result.tableConfig;
		var obj = Ext.decode(tableConfig);
		var tableObj = new BuildTableObj(obj);
        var tablePanel = tableObj.getMainPanel();
        Ext.getCmp("tablePanelid").removeAll();
        Ext.getCmp("tablePanelid").add(tablePanel);        
	},
	
	/**
	 * 各种情况title展现
	 */
	getEmpTopTitleTags:function(){
		//Ext.container.Container
		var titleTags = Ext.create("Ext.Toolbar",{
			id:'titleTags',
			margin:'5 0 0 5',
			width:'100%',
			layout:'hbox',
			region:'north',
			border:false,
			defaultType:'container',
			items:[{// 借阅证书办理
				id:"curveTitle",
				width:112,
				height:26,
				margin:'0 10 0 15',
				html:'<div id="borrowingTitleid" style="height:25px;position:relative;cursor:pointer;font-family:宋体;">'
					+'<a style="font-size: 18px;" onclick="employeeDashboard.clickBution(1)">借阅证书办理</a>'
					+'</div>'
			},{// 已借阅证书
				id:"borrowTitle",
				margin:'0 10 0 15',
				width:98,
				height:26,
				html:'<div id="alreadyBrowTitleid" style="height:25px;cursor:pointer;font-family:宋体;" >'
					+'<a style="font-size: 18px;"onclick="employeeDashboard.clickBution(2)" >已借阅证书</a></div>'
			},
			'->',
			
            { 
				id:"showBorrowingid",
				margin:'0 0 0 0',
				width:122,
				height:26,
				html:'<div style="font-family:宋体;height:25px;" >'//
					+'<input type="checkbox" checked="checked" style="cursor:pointer;vertical-align:middle;"'
					+'id="vehicle" name="vehicle" onclick="employeeDashboard.canBorrowCers()"/>'
					+'<span style="line-height:25px;">'
					+'仅显示可借阅证书</span></div>'
			},{ 
				id:"borrowOperaid",
				margin:'0 0 0 15',
				width:112,
				height:26,
				html:'<div style="height:25px;font-family:宋体;color:#78C5FF;" >'
					+'<span onclick="employeeDashboard.borrowingCers()" style="position:relative;cursor:pointer;line-height:25px;">借阅</span></div>'
			}]
		});
		
		return titleTags;
	},
	/**
	 * 借阅 操作列渲染函数
	 */
	borrowingOneCer:function(value, metaData, Record){
//		console.log(Record.data);
		// 证书状态 不可用就不显示借阅二字  01`可用
		var cerState = employeeDashboard.cerFieldsetid.cerState;
		// 到期日期指标
		var certEndDate = employeeDashboard.cerFieldsetid.certEndDate;
		var certEndDateValue = eval("Record.data."+certEndDate);
		var dt = Ext.Date.parse(certEndDateValue, "Y-m-d");
		var curDate = new Date();
		// 是否已借出
		var certBorrowState = employeeDashboard.cerFieldsetid.certBorrowState;
		/**
		 * 校验 是否可用  是否借出  是否到期
		 */
		if(!(eval("Record.data."+cerState).indexOf('01') != -1
				|| eval("Record.data."+cerState).indexOf('`可用') != -1)
				|| (eval("Record.data."+certBorrowState).indexOf('1') != -1
				|| eval("Record.data."+certBorrowState).indexOf('是') != -1)
				|| (!(dt > curDate) && !Ext.isEmpty(certEndDateValue)))
			return "";
		var cerNum = employeeDashboard.cerFieldsetid.cerNum;
		var cerName = employeeDashboard.cerFieldsetid.cerName;
	    var cerType = employeeDashboard.cerFieldsetid.cerType;
	    
	    var cerNum = eval("Record.data."+cerNum);
	    var cerName = eval("Record.data."+cerName);
	    var cerType = eval("Record.data."+cerType);
	    var a0101 = Record.data.a0101;
	    var nbase_e = Record.data.nbase_e;
	    var a0100_e = Record.data.a0100_e;
	    
		var html = "<a href=javascript:void(0); " +
				"onclick=employeeDashboard.borrowingOneCerOK('"+cerNum+"','"+cerName+"','"+cerType+"'" 
						+",'"+a0101+"','"+nbase_e+"','"+a0100_e+"','"+certEndDateValue+"');>" +
						"借阅</a>"; 
		return html;
	},
	/**
	 * 借阅 单个操作点击事件
	 */
	borrowingOneCerOK:function(cerNum, cerName, cerType, a0101, nbase_e, a0100_e, certEndDate){
		
		var records = [];
    	var map = new HashMap();
		map.put("cerNum", cerNum);
		map.put("cerName", cerName);
		map.put("cerType", cerType);
		map.put("cerPerName", a0101);
		map.put("nbase", nbase_e);
		map.put("a0100", a0100_e);
		map.put("remind", 0);
		map.put("certEndDate", certEndDate);
		
		records.push(map);
		employeeDashboard.borrowGridRecords = records;
	    employeeDashboard.showBorrowWin(records);
	},
	
	/**
     *  我的证书
     */
	getEmpTotalPanel:function(certSubetMsg){
		var tableInfo = "";
		var certMsg = "";
		var qxjprive = true;
	    for (var i = 0; i < certSubetMsg.length; i++) {
            var certMap = certSubetMsg[i];
            var certNameValue = certMap.certNameValue;
            var certOrganizationValue = certMap.certOrganizationValue=="" ? "无" :certMap.certOrganizationValue;
            var certEndDateItemIdValue = certMap.certEndDateItemIdValue;
            var certNOItemIdValue = certMap.certNOItemIdValue;
            var number = certMap.number;
            var isBorrow = "2"==certMap.returnFlag ? "已借出" : "未借出";
	       	 	
	       	certMsg = certMsg + "<tr><td align='left' style='padding-right: 10px;padding-bottom: 10px;' >"+certNameValue+"</td>"
	 		+ "<td style='padding-right: 10px;padding-bottom: 10px;'>"+certOrganizationValue+"</td><td style='padding-bottom: 10px;'>有效期至：</td>" +
	 				"<td style='padding-right: 10px;padding-bottom: 10px;'>"+certEndDateItemIdValue+"</td>" +
	 						"<td  style='padding-right: 10px;padding-bottom: 10px;' >"+isBorrow+"</td>"
	 		+ "<td style='padding-right: 10px;padding-bottom: 10px;' >" +
	 				"<a href='javascript:;' onclick='employeeDashboard.showMsg(\""+certNOItemIdValue+"\","+number+")'>借阅"+number+"次</font>"
	 		+ "</td></tr>";
	    }
	    
	    var strhtml = "<div style='overflow-y:auto;height:140px;width:100%;border:1px;text-align:center;font-size: 15px;'> " 
						+"<table border='0' cellspacing='4'  align='left' cellpadding='0' style='margin:0 10px 0 40px;font-size: 14px;' >"
							+certMsg
						+"</table>"
					+"</div>";
				
	    var strlen = "";
		if(0 != certSubetMsg.length){
			strlen = "（"+certSubetMsg.length+"本）";
		}else{
			strhtml = "<div style='font-size:25px;text-align:center;color:#979797;'><h2>暂无</h2></div>";
		}
		
	    return {
			   xtype: 'label',
			   border: false,
			   height: 210,
			   width: '100%',
			   margin:'20 0 0 20',
			   bodyStyle:'overflow-y:auto;overflow-x:auto',
			   layout:'hbox',
			   html  : "<div style='height:210px;width:100%;border:0px;color:#979797;font-size: 18px;font-family:宋体;'>" 
				   	+"<div style='height:40px;width:490px;margin:10px 0 0 20px;color:#000000;'>我的证书"+strlen+"</div>"
				   	+strhtml
					+"</div>"
		};
	},
	/**
     *  管理制度
     */
	getQualificationsPanel:function(){
		var tableInfo = "";
		var qxjprive = true;
		
		var conditionid = "";
 		var conditionitemsid = "conditions0";
		var map = new HashMap();
		map.put("width",500);
		map.put("pageNum",1);
		map.put("totalPageNum",0);
		map.put("conditionitemsid",conditionitemsid);
		map.put("conditionid",conditionid);
		map.put("fromUrl",false);
		map.put("module_type","2");
	    Rpc({functionId:'ZJ100000200',async:false, success:employeeDashboard.setPanelValue,scope:this},map);
	    return employeeDashboard.conditionitems
	},
	
	/**
     *  展现我的证书
     */
	setPanelValue:function(form, action){
		this.conditionObj = Ext.decode(form.responseText);
		if(this.conditionObj.isAdd!=null){
			qualificationGloble.isAdd = this.conditionObj.isAdd;
		}
		this.totalPageNum=this.conditionObj.totalPageNum,
 		this.pageNum=this.conditionObj.pageNum;
		lengths=this.conditionObj.conditions.length;
		
		for(var i=0;i<lengths;i++){
			var src = "/images/new_module/qualificationsUNselect.png";
			var zc_series = "";
			var condition_id = "";
			zc_series = this.convertStr(this.conditionObj.conditions[i].zc_series);
			condition_id =  this.conditionObj.conditions[i].condition_id;
			
			employeeDashboard.conditionitems.push({
				id:condition_id,
				height:90,
				xtype:'container',
				width:80,
				margin:'0 15 0 0',
				layout:
				{
				type:'vbox',
				align:'center'
				},
				items:[
				{xtype:'image',id:'img_'+this.conditionObj.conditions[i].condition_id,margin:'10 0 0 0'
					,height:40,width:40,src:src,title:''+this.conditionObj.conditions[i].zc_series,border:false},
				{xtype: 'label',height:40,width:80,style:{"text-align":"center"},text: zc_series,margin:'0 0 0 0'}
				],
				border:false,
				listeners : {
						render : function(obj) {
								this.getEl().on('click', function() {
								Ext.Loader.setConfig({
									enabled: true,
									paths: {
										'QualificationsURL' : '../../../module/qualifications',//主界面
										'SYSF' : '../../../components/fileupload'
									}
								});
								Ext.onReady(function(){
									var url = '?b_query=link&='+obj.id;
										// 请求只读页面
										Ext.require('QualificationsURL.QualificationsForHome', function(){
											 QualificationsGlobal = Ext.create("QualificationsURL.QualificationsForHome",{url : url});
										});
								});
								},this);
							}
				},
				style : {cursor : 'pointer'}
				}
			);	
		}
		
	},
	
	/**
     *  展现我的证书
     */
	showMsg:function(certNOItemIdValue,number){
		 if(number == 0)
			 return
			
		 var cerMsg  = "";
		 var map = new HashMap();
		 // manager:管理员；employee:员工
		 map.put("opt",employeeDashboard.roleType);
		 map.put("flag",",borrow,");
		 map.put("certNOItemIdValue",certNOItemIdValue);
		 Rpc({functionId:'CF01030001',success:employeeDashboard.showBorrowedMsg},map);
		 
	},
	
	showBorrowedMsg:function(form){
		var result = Ext.decode(form.responseText);
		var certBorrowSubetMsg = new Array();
		var cerMsg ="";
		var html="<table width=100% cellspacing='0'rowspacing='0'cellpadding='0' class='workflow-tuli'>";
		certBorrowSubetMsg = result.certBorrowSubetMsg;
		 for (var i = 0; i < certBorrowSubetMsg.length; i++) {
	            var nbaseMap = certBorrowSubetMsg[i];
	            var BorrowerName = nbaseMap.borrowName;
	            certificateName =  nbaseMap.certificateName;
	            var returnDate = nbaseMap.returnFlag==false ? "至今未还" :nbaseMap.returnDate + " 还 ";
	            var borrowDate = nbaseMap.borrowDate;
	            var borrowReason = nbaseMap.borrowReason;
	            var borrowTime="<tr><td>"+BorrowerName+"&nbsp;&nbsp;"+borrowDate+"借 ~"+returnDate+"<br>"+borrowReason+"</td></tr>";
	         
				html += "<tr><td width='5px'></td>" +
						"<td width='12px'><div class='workflow-timeLine-shortline'></div></td><td></td>" +
						"</tr>"
						+"<tr><td ><div class='workflow-approver'></div></td>" +
							"<td width='12px' class='workflow-timeLine-longline'>" +
							"<div class='workflow-timeLine-point'></div>"+
							"</td>" +
							"<td style='padding-left:10px;padding-right:20px;'>" +
							"<div class='workflow-timeline-textarr'>" +
							"<div class='arrow'><em></em><span></span></div>" +
							"<table width='100%'><tr><td>"+borrowTime+"</table>" +
							"</div>" +
							"</td></tr>"
							+"<tr><td ></td><td width='12px'>" +
							"<div class='workflow-timeLine-shortline'></div>" +
						"</td><td></td></tr>";
	     } 
		 html +="</table>";
		 if(this.win)
			 this.win.close();
		 
		 this.win = Ext.create('Ext.window.Window',{
		        layout: 'fit',  
		        autoScroll:true,
		        title:certificateName,
		        id:'winShow',
		        height: 500,    //初始高度
		        width: 400,  //初始宽度
		        border: 0,    //无边框
		        frame: false,
		        html  : "<div style='width:100%;border:0px;margin:0 0 20 0;color:#979797;font-size: 18px;'>" 
					+"<div style='width:100%;font-size: 15px;'> "+html+"</div>"
					+"</div>"   
		    });
		    win.show();    //显示窗口
		    
		    Ext.getBody().addListener('click', function(evt, el) {
				if (!win.hidden && "winShow" != el.id && "winShow-body" != el.id )
					win.close();
			});
		
	},
	/**
	 * 仅显示可借阅证书 事件
	 */
	canBorrowCers:function(){
		
		var vehicle = document.getElementById('vehicle').checked;
		// =1显示全部证书  =2显示可借阅证书
		if(true == vehicle)
			employeeDashboard.vehicle = "2";
		else
			employeeDashboard.vehicle = "1";
		employeeDashboard.borrowedCertificates();
	},
	
	borrowingCers:function(){

		var selectData = employeeDashboard.tableObj.tablePanel.getSelectionModel().getSelection();
	    var records = [];
	    var cerNum = employeeDashboard.cerFieldsetid.cerNum;
	    var cerName = employeeDashboard.cerFieldsetid.cerName;
	    var cerType = employeeDashboard.cerFieldsetid.cerType;
	    // 证书状态 不可用就不显示借阅二字  01`可用
	    var cerState = employeeDashboard.cerFieldsetid.cerState;
	    // 是否已借出
		var certBorrowState = employeeDashboard.cerFieldsetid.certBorrowState;
		// 到期日期指标
		var certEndDate = employeeDashboard.cerFieldsetid.certEndDate;
		var num = 0;
		var msg = "";
		var datenum = 0;
		var datemsg = "";
	    var map = new HashMap();
	    var curDate = new Date();
	    for(var i=0; i<selectData.length; i++){
	    	
	    	var cerNameValue = eval("selectData[i].data."+cerName);
			if(!(eval("selectData[i].data."+cerState).indexOf('01') != -1
					|| eval("selectData[i].data."+cerState).indexOf('`可用') != -1)
					|| (eval("selectData[i].data."+certBorrowState).indexOf('1') != -1
					|| eval("selectData[i].data."+certBorrowState).indexOf('是') != -1)){
				num++;
				msg = msg + cerNameValue + "、";
				continue;
			}
			var certEndDateValue = eval("selectData[i].data."+certEndDate);
			var dt = Ext.Date.parse(certEndDateValue, "Y-m-d");
			if(!(dt > curDate)){
				datenum++;
				datemsg = datemsg + cerNameValue + "、";
				continue;
			}
	    	map = new HashMap();
			map.put("cerNum", eval("selectData[i].data."+cerNum));
			map.put("cerName", cerNameValue);
			map.put("cerType", eval("selectData[i].data."+cerType));
			map.put("cerPerName", selectData[i].data.a0101);
			map.put("certEndDate", certEndDateValue);
			map.put("nbase", selectData[i].data.nbase_e);
			map.put("a0100", selectData[i].data.a0100_e);
			map.put("remind", i);
			records.push(map);
	    }
	    // 如果存在已借出的证书则给出提示
	    if(!Ext.isEmpty(msg) && num>0){
	    	msg = msg.substring(0, msg.length-1);
	    	msg = msg + "等"+num+"本证书已经被借出或证书状态不是可用，请重新选择！"
	    	Ext.showAlert(msg);
	    	return;
	    }
	    // 如果存在已到期的证书则给出提示
	    if(!Ext.isEmpty(datemsg) && datenum>0){
	    	datemsg = datemsg.substring(0, datemsg.length-1);
	    	datemsg = datemsg + "等"+datenum+"本证书已经到期，请重新选择！"
	    	Ext.showAlert(datemsg);
	    	return;
	    }
	    employeeDashboard.borrowGridRecords = records;
	    employeeDashboard.showBorrowWin(records);
	},
	/**
	 * 获取数据store
	 */
	getBorrowData : function(borrowCertificateData) {

		return Ext.create('Ext.data.Store', {
					//			id:'storey',
					fields : ['cerNum', 'cerName', 'cerType', 'cerPerName','nbase','a0100','certEndDate','remind'],
//					groupField : 'perName',
					clearRemovedOnLoad :true,
					data : employeeDashboard.borrowGridRecords
				});
	},
	/**
	 * 借阅窗口
	 */
	showBorrowWin: function (records){
		
		if(records.length == 0){
			Ext.showAlert("请选择要借阅的证书！");
			return;
		}
		
		var win = Ext.getCmp("borrowWinId");
		if(win)
			win.close();
		// 借阅人
		var borrowPerson = Ext.create('Ext.panel.Panel', {
			height: 30,
		    width: 600,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
		    	xtype: 'label',
		        forId: 'myFieldId',
		        width: 90,
		        html: '<div style="text-align:right;"><font color=red>*</font>借阅人</div>',
		        margin: '5 5 0 0'
		    },{
				xtype:'panel',
				id:'borrowPersonId',
				border:false,
				html:'',
				margin: '5 5 0 0'
			},{
				xtype:'label',
				html:employeeDashboard.userFullName+"（"+employeeDashboard.userInfo+"）" ,
				margin: '5 0 0 0'
			}]
		});
		// 借阅日期
		var borrowDate = Ext.create('Ext.form.field.Date', {
			id:'borrowDateId',
			fieldLabel: '<font color=red>*</font>借阅日期',
			labelAlign:'right',
			labelWidth:90,
			value: new Date(),
			format:'Y-m-d',
			allowBlank:false
			
		});
		// 预计归还日期
		var estimateReturnDate = Ext.create('Ext.form.field.Date', {
			id:'returnDateId',
			fieldLabel: '<font color=red>*</font>预计归还日期',
			labelAlign:'right',
			labelWidth:90,
			value: new Date(),
			format:'Y-m-d',
			margin: '0 0 0 100',
			allowBlank:false
			
		});
		// 借阅事由
		var borrowDesc = Ext.create('Ext.form.field.TextArea', {
			id:'borrowDescId',
			fieldLabel: '<font color=red>*</font>借阅事由',
			labelWidth:90,
			labelAlign:'right',
			width: 500,
			margin: '5 5 0 0',
			allowBlank:false
			
		});
		// 非系统指标项
		var fieldItemsPanel = Ext.create('Ext.panel.Panel', {
			id:'fieldItemsPanel',
			margin: '5 5 0 0',
			border:0,
			width:680,
			defaults:{
				labelWidth:90
			},
			scrollable:true,
			layout:{
				type:'table',
				columns:2,
				tdAttrs:{
					align:'left'
				},
				tableAttrs:{
					width:'100%'
				}
			},
			items:[]
		});

    	// 所选的借阅证书表格
	    var gridPanel = Ext.create('Ext.grid.Panel', {
    		id : 'borrowGridId',
			title : false,
			border : 1,
//			width : '100%',
			height: 150,//278,
			width: 550,
			store : employeeDashboard.getBorrowData(records),
			columns : [{
						header : '证书编号',
						dataIndex : 'cerNum',
						menuDisabled : true,
						sortable : false,
						flex : 2
					}, {
						header : '证书名称',
						dataIndex : 'cerName',
						menuDisabled : true,
						sortable : false,
						flex : 2
					},{
						header : '证书类别',
						dataIndex : 'cerType',
						menuDisabled : true,
						sortable : false,
						renderer :function(value) {
							var str = "";
							if(!Ext.isEmpty(value))
								str = value.split('`')[1];
							return str;
						},
						flex : 2
					}, {
						header : '证书持有人',
						dataIndex : 'cerPerName',
						menuDisabled : true,
						sortable : false,
						flex : 1.5
					}, {
						header : '到期日期',
						dataIndex : 'certEndDate',
						menuDisabled : true,
						sortable : false,
						flex : 1.5
//						hidden : true
					}
					,{
						header : '操作',
						dataIndex : 'remind',
						renderer :function(index) {
							return "<a href=javascript:void(0); onclick='employeeDashboard.cancelBowCer("+index+")'>取消</>";
						},
						flex : 1 
					}]
		});
    	// 证书列表
		var panel = Ext.create('Ext.panel.Panel', {
		    height: 160,
		    width: 680,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
			margin: '5 5 0 0',
		    items:[{
			    	xtype: 'label',
			        forId: 'myFieldId',
			        width: 90,
			        html: '<div style="text-align:right;">证书列表</div>',
			        margin: '5 5 0 0'
			    }
		    	,gridPanel]
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'borrowWinId',
		    title: '证书借阅登记',
		    autoHeight: true,
		    maxHeight: 500,
		    width: 720,
		    scrollable:true,
		    modal:true,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'vbox'
			},
			items:[borrowPerson
				, {
				border:false,
				layout: {
			        align: 'top',
			        type: 'hbox'
				},
				items:[
					borrowDate
					, estimateReturnDate
				]}
				, borrowDesc
				, fieldItemsPanel
				, panel],
			buttonAlign: 'center',
			buttons: [{
		    	text: '提交',
		    	handler:employeeDashboard.commitBowCer
			},{
		    	text: '取消',
		    	handler:function(){
		    		win.close();
			    }
			}]
		});
		employeeDashboard.setFieldItemsPanel(employeeDashboard.fieldItems);
		win.show();
	},
	
	/**
	 * 借阅提交操作
	 */
	commitBowCer:function(){
		
		var returnDate = Ext.getCmp('returnDateId').getValue();
		if(Ext.isEmpty(returnDate)){
			Ext.showAlert("请填写预计归还日期！");
			return;
		}
		var returnDateFormat = Ext.Date.format(returnDate, 'Y-m-d');
		// 校验提交 的  预归还日期 是否超过 某些证书的到期日期
		var bool = true;
		var store = Ext.getCmp('borrowGridId').getStore();
		var records = [];
		store.each(function(record,index){
			records.push(record.data);
			// 校验到期日期不为空的证书
			if(!Ext.isEmpty(record.data.certEndDate) && !(record.data.certEndDate > returnDateFormat))
				bool = false;
		});
		
		if(!bool){
			Ext.showAlert("您设置的预计归还日期超过所选证书的到期日期，请重新选择！");
			return ;
		}
		
		var borrowDate = Ext.getCmp('borrowDateId').getValue();
		if(Ext.isEmpty(borrowDate)){
			Ext.showAlert("请填写借阅日期！");
			return;
		}
		var borrowDateFormat = Ext.Date.format(borrowDate, 'Y-m-d');
		
		if(!(returnDate > borrowDate)){
			Ext.showAlert("预计归还日期小于或等于借阅日期，请重新填写！");
			return;
		}
		
		var browReason = Ext.getCmp('borrowDescId').getRawValue();
		if(Ext.isEmpty(browReason)){
			Ext.showAlert("请填写借阅事由！");
			return;
		}
		// 校验其他指标 用户自定义其他的指标
		var list = new Array();
		var map = new HashMap();
		for(var i=0;i<employeeDashboard.fieldItems.length;i++){
			var field = employeeDashboard.fieldItems[i];
			var itemid = field.itemid;
			var idvalue = Ext.getCmp(itemid+'id').getValue();
			// 是否必填
			if(field.allowblank){
				if(Ext.isEmpty(idvalue)){
					Ext.showAlert(field.itemdesc+"为必填项，请重新填写！");
					return;
				}					
			}
			map = new HashMap();
			map.put("itemid", itemid);
			map.put("itemtype", field.itemtype);
			map.put("codesetid", field.codesetid);
			map.put("value", idvalue+"");
//			console.log(map);
			list.push(map);
		}
		
		Ext.showConfirm('确定提交该借阅记录吗？', function(btn) {
			if (btn == 'yes') {
				
				var map = new HashMap();
				map.put("opt", employeeDashboard.roleType);
				map.put("flag", ",commit,");
				map.put("browDate", borrowDateFormat);
				map.put("retunDate", returnDateFormat);
				map.put("browReason", browReason);
				map.put("browStoreData", records);
				map.put("fieldsData", list);
				Rpc({functionId:'CF01030001',async:false,success:function(form){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						var win = Ext.getCmp("borrowWinId");
						if(win)
							win.close();
						employeeDashboard.clickBution(1);
					}
					else
						Ext.showAlert(result.message);
				},scope:this},map);
			}
		}, this);
		
	},
	/**
	 * 添加借阅子集其他指标
	 */
	setFieldItemsPanel:function(fieldList){
		
		var fieldItemsPanel=Ext.getCmp('fieldItemsPanel');
		fieldItemsPanel.removeAll ( true );
		//判断是否是同一行第一个子集
		var isFirstItem=false;
		var linePanel;
//		this.respon=respon;//respon.//respon.
		for(var i=0;i<fieldList.length;i++){
			var field=fieldList[i];
			var margin="5 0 5 0";
			isFirstItem=!isFirstItem;
			if(field.itemtype=="A"){
				if(field.codesetid=="0"){
					fieldItemsPanel.add({
						xtype:'textfield',
						id:field.itemid+"id",
						margin:margin,
						name:field.itemid,
						maxLength:field.itemlength,
//						allowBlank:field.allowblank,
						required:field.allowblank,
						fieldLabel:field.itemdesc,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
//								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}else{
					fieldItemsPanel.add({
						xtype:'codecomboxfield',
						id:field.itemid+"id",
						margin:margin,
						name:field.itemid,
						required:field.allowblank,
						ctrltype:'1',//this.getCtrltype(),
						nmodule:'0',//this.getNmodule(),
//						allowBlank : false, 
						fieldLabel:field.itemdesc,
						codesetid:field.codesetid,
						onlySelectCodeset:false,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
//								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}
			}else if(field.itemtype=="M"){
				if(!isFirstItem){
					fieldItemsPanel.add({
						xtype:'container'
					})
				};
				fieldItemsPanel.add({
					xtype:'textarea',
					id:field.itemid+"id",
					name:field.itemid,
					colspan:2,
					style:'',
					required:field.allowblank,
					maxLength:field.itemlength==10?Number.MAX_VALUE:field.itemlength,
//					allowBlank:field.allowblank,
					fieldLabel:field.itemdesc,
					msgTarget :'under',
					border:0,
					width:500,//'90%',
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
				isFirstItem=false;
			}else if(field.itemtype=="N"){
				fieldItemsPanel.add({
					xtype:'numberfield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					decimalPrecision:field.demicallength,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else if(field.itemtype=="D"){
				var format='Y';
				if(field.itemlength=='4'){
					format='Y';
				}else if(field.itemlength=='7'){
					format='Y-m';
				}else if(field.itemlength=='10'){
					format='Y-m-d';
				}else if(field.itemlength=='16'){
		          	format = 'Y-m-d H:i';
				}else if(field.itemlength=='18'){
		            format = 'Y-m-d H:i:s';
				}
				fieldItemsPanel.add({
					xtype:'datetimefield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					format:format,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else{
				fieldItemsPanel.add({
					xtype:'textfield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}
		}
	},
	/**
	 * 删除单个操作
	 */
	cancelBowCer:function(index){
		
		Ext.showConfirm('确定取消该证书吗？', function(btn) {
			if (btn == 'yes') {
				
				var newRecords = [];
				var records = employeeDashboard.borrowGridRecords;
				var num = 0;
				for(var i=0; i<records.length; i++){
					if(index == i)
						continue;
					
					records[i].remind = num;
					newRecords.push(records[i]);
					num++;
				}
				employeeDashboard.borrowGridRecords = newRecords;
				
				var store = Ext.getCmp('borrowGridId').getStore();
				store.loadData(employeeDashboard.borrowGridRecords);
			}
		}, this);		
	},
	
	// 判断是否是字母或数字
	checknum : function(value) {
        var Regx = /^[A-Za-z0-9]*$/;
        if (Regx.test(value)) {
            return true;
        }
        else {
            return false;
        }
    },
	
	// 把字符串转化成后面带省略号形式
	convertStr : function(str){
		var reStr = str;
		
		var maxwidth = 23;//字母排列的话最多占的个数
		var index = 0;
		var useWidth = 0;
		for(i=0; i<str.length; i++){
			var code = str.charAt(i);
			 if(this.checknum(code)) {//字母或数字
				 //大写字母占得位置更多
				 if(/^[A-Z]*$/.test(code))
					 useWidth+=1.4;
				 else
					 useWidth += 1;
			 } else {//汉字
			 	useWidth += 2;//每个汉字占宽度约为字母的2倍
			 }
			 if(useWidth >= maxwidth && index == 0){
			 	index = i;
			 }
		}
		//checknum
		if(useWidth > maxwidth){
			reStr = str.substring(0, index);
			reStr += '...';
		}
		return reStr;
	},	
	
	/**
	 * 关闭窗口
	 */
	closeBowWin:function(){
		var bowWin = Ext.getCmp('bowWinid');
	    if(bowWin)
	    	bowWin.close();
	    
//	    employeeDashboard.clickBution(1);
	}
	
});