Ext.define("KqDataURL.KqDataAppeal",{
	requires:['KqDataURL.KqDataMx'],
	constructor: function(config) {
		KqDataAppeal = this;
		KqDataAppeal.config = config;
		KqDataAppeal.init();
    },
	init:function(){
		KqDataAppeal.currentPage = 1;
		KqDataAppeal.pageSize = 20;
		KqDataAppeal.status = '00';
		
		KqDataAppeal.scheme_id =!KqDataAppeal.config.schemeId? '':KqDataAppeal.config.schemeId;
		KqDataAppeal.kq_year =!KqDataAppeal.config.kq_year? '':KqDataAppeal.config.kq_year;
		KqDataAppeal.kq_duration =!KqDataAppeal.config.kq_duration? '':KqDataAppeal.config.kq_duration;
		KqDataAppeal.org_id =!KqDataAppeal.config.org_id? '':KqDataAppeal.config.org_id;
		var json = {};
		json.type = 'main';
		json.status = '00';
		json.scheme_id = KqDataAppeal.scheme_id;
		json.kq_year =  KqDataAppeal.kq_year;
		json.kq_duration = KqDataAppeal.kq_duration;
		json.org_id =  KqDataAppeal.org_id;
		json.currentPage = KqDataAppeal.currentPage;
		json.pageSize = KqDataAppeal.pageSize;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00021101',async:false,success:KqDataAppeal.getTableOK},map);
	},
	getTableOK:function(resp){
		//解决firefox下此页面表格列头下方缺线问题
		if(Ext.browser.is('Firefox') && Ext.util.CSS.getRule(".x-docked-top")){
			Ext.util.CSS.updateRule(".x-docked-top","border-bottom-width","1px");
		} 
		var result = Ext.decode(resp.responseText);
    	if(result.returnStr.return_data.msg && result.returnStr.return_data.msg == 'nodata'){
    		Ext.create('Ext.container.Viewport',{
	            style: 'backgroundColor:white',
	            layout: 'fit',
	            items:[{
	            	xtype:'component',
	            	 style: 'text-align:center;margin-top:50px',
	            	html: '<div style="height:135px;"><img src="../../../images/nodata.png"/></div><div style="font-weight:bold;color:#7c7c7c;font-size:16px;font-family' + kq.fontfamily + '">' + kq.dataAppeal.nodata + '</div>'
	            }]
	    	});
    		// 如果有错误信息则提示
    		if(result.returnStr.return_data.erro_msg){
    			Ext.showAlert(result.returnStr.return_data.erro_msg);
    		}
    		return;
    	}
		var column_define = "";
		column_define += '[{';
		column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.dataAppeal.scheme+"',";//考勤方案
    	column_define += "flex: 3,title:true,sortable: false,menuDisabled:false,align:'left',dataIndex: 'scheme_name'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.dataAppeal.orgName+"',";//机构名称
    	column_define += "flex: 3,title:true,sortable: false,menuDisabled:false,renderer: KqDataAppeal.renderClick,align:'left',dataIndex: 'org_name'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.period.name+"',";//考勤期间
    	column_define += "flex: 2,title:true,sortable: false,menuDisabled:false,align:'left',dataIndex: 'kq_duration'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.dataAppeal.joinKqNum+"',";//参与考勤人数
    	column_define += "title:true,sortable: false,menuDisabled:false,align:'right',dataIndex: 'participants'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.dataAppeal.staffIndeedNum+"',";//员工确定人数
    	column_define += "title:true,sortable: false,menuDisabled:false,align:'right',dataIndex: 'confirms'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.dataAppeal.flag+"',";//状态
    	column_define += "flex: 1,title:true,sortable: false,menuDisabled:false,align:'center',dataIndex: 'status_name',";
    	column_define += "renderer:KqDataAppeal.flagRender";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '" + kq.dataAppeal.approveUser + "',";//状态
    	column_define += "flex: 2,title:true,sortable: false,menuDisabled:false,align:'left',dataIndex: 'approveUser',";
    	column_define += "renderer:KqDataAppeal.approveUserRender";
    	if (result.returnStr.return_data.approvalMessage=="1") {
    		column_define += "},{";
        	column_define += "xtype: 'gridcolumn',";
        	column_define += "text: '" + kq.dataAppeal.appProcess + "',";//上报过程
        	column_define += "flex: 2,title:true,sortable: false,menuDisabled:false,align:'center',dataIndex: 'sp_process',";
        	column_define += "renderer:KqDataAppeal.processRender";
		}
    	column_define += "}]";
    	
    	KqDataAppeal.kq_year = result.returnStr.return_data.kq_year;
    	if(!KqDataAppeal.kq_year)
    		KqDataAppeal.kq_year =  new Date().getFullYear();
    	
    	KqDataAppeal.data =result.returnStr.return_data.org_list;
    	KqDataAppeal.totalCount =result.returnStr.return_data.totalCount;
    	KqDataAppeal.kq_yearList = [];
    	KqDataAppeal.kq_yearList = result.returnStr.return_data.year_list;
    	KqDataAppeal.optRole = result.returnStr.return_data.optRole;
    	KqDataAppeal.store = Ext.create('Ext.data.Store', {
    		storeId: 'schemeStore',
		    fields:[ 'scheme_id ','scheme_name','org_id','org_name','kq_duration','participants','confirms', 'status','status_name','approveUser','operation'],
		    data: KqDataAppeal.data
        });
        var json = {};
		json.type = "main";
		json.currentPage = 1;
		json.pageSize = KqDataAppeal.pageSize;
        
        var srcFirst = "";
		var srcPrev = "";
		var srcNext = "";
		var srcLast = "";
		var flagF = false;
		var flagL = false;
		if(KqDataAppeal.currentPage == 1) {
			srcFirst = "/ext/ext6/resources/images/grid/page-first-disabled.gif";
			srcPrev = "/ext/ext6/resources/images/grid/page-prev-disabled.gif";
			flagF = true;
			if(KqDataAppeal.totalCount <= KqDataAppeal.pageSize) {
				srcNext = "/ext/ext6/resources/images/grid/page-next-disabled.gif";
				srcLast = "/ext/ext6/resources/images/grid/page-last-disabled.gif";
				flagL = true;
			}else {
				srcNext = "/ext/ext6/resources/images/grid/page-next.gif";
				srcLast = "/ext/ext6/resources/images/grid/page-last.gif";
			}
		}
		
		
		//分页自定义start
		//首页
		var imgPageFist = Ext.widget({
			xtype: 'button',
			width: 20,
			height: 22,
			icon: srcFirst,
			disabled: flagF,
			style: 'background-position:center;margin-left:2px !important;border: 1px solid #c5c5c5;',
		    id: 'page_first',
		    listeners: {
		    	click: function() {
		    		KqDataAppeal.currentPage = 1;
		    		KqDataAppeal.changePage();
		    	}
		    }
		});
		
		//上一页
		var imgPagePrev = Ext.widget({
			xtype: 'button',
			width: 20,
			height: 22,
			icon: srcPrev,
			disabled: flagF,
			style: 'background-position:center;margin-left:2px !important;border: 1px solid #c5c5c5;',
		    id: 'page_prev',
		    listeners: {
		    	click: function() {
		    		KqDataAppeal.currentPage = Number(KqDataAppeal.currentPage) - 1;
		    		KqDataAppeal.changePage();
		    	}
		    }
		});
		//下一页
		var imgPageNext = Ext.widget({
			xtype: 'button',
			width: 20,
			height: 22,
			icon: srcNext,
			disabled: flagL,
			style: 'background-position:center;margin-left:5px !important;border: 1px solid #c5c5c5;',
		    id: 'page_next',
		    listeners: {
		    	click: function() {
		    		KqDataAppeal.currentPage = Number(KqDataAppeal.currentPage) + 1;
		    		KqDataAppeal.changePage();
		    	}
		    }
		});
		//末页
		var imgPageLast = Ext.widget({
			xtype: 'button',
			width: 20,
			height: 22,
			icon: srcLast,
			disabled: flagL,
			style: 'background-position:center;margin-left:2px !important;border: 1px solid #c5c5c5;',
		    id: 'page_last',
		    listeners: {
		    	click: function() {
		    		KqDataAppeal.currentPage = Math.ceil(Number(KqDataAppeal.totalCount)/Number(KqDataAppeal.pageSize));
		    		KqDataAppeal.changePage();
		    	}
		    }
		});
		
		//刷新
		var refresh = Ext.widget({
			xtype: 'button',
			width: 20,
			icon: '/ext/ext6/resources/images/grid/refresh.gif',
			height: 22,
			style: 'background-position:center;margin:1px 0 0 5px !important;border: 1px solid #c5c5c5;',
		    id: 'refresh',
		    listeners: {
		    	click: function() {
		    		if((Number(KqDataAppeal.currentPage)-1)*Number(KqDataAppeal.pageSize) >= Number(KqDataAppeal.totalCount)) {
		    			KqDataAppeal.currentPage = 1;
		    		}
		    		KqDataAppeal.changePage();
		    	}
		    }
		});
        
        
        var pagingToolbar = Ext.widget('panel',{
			border: false,
			layout:'hbox',
			width: '100%',
			items: [imgPageFist,imgPagePrev,{
				xtype: 'textfield',
				fieldLabel: kq.scheme.pageInfo.split("%")[0],
				width: 50,
				regex:/^[+]{0,1}(\d+)$/,
				regexText:"只能够输入正整数",
				labelWidth: 10,
				id: 'then',
				padding: '0 0 0 7',
				labelAlign: 'left',
				value: KqDataAppeal.currentPage?KqDataAppeal.currentPage:1,
				listeners: {
	            	change: function(el) {
	            		KqDataAppeal.currentPage = el.value;
	            	}
	            }
			},{
				xtype: 'panel',
				border: false,
				id: 'totalPage',
				//width: 65,
				padding: '3 0 0 5',
				html: kq.scheme.page.replace("{0}",Math.ceil(KqDataAppeal.totalCount/KqDataAppeal.pageSize)==0? 1:Math.ceil(KqDataAppeal.totalCount/KqDataAppeal.pageSize))
			},imgPageNext,imgPageLast,{
				xtype: 'textfield',
				fieldLabel: kq.scheme.pageInfo.split("%")[1],
				width: 70,
				padding: '0 0 0 5',
				labelWidth: 30,
				labelAlign: 'left',
				regex:/^[+]{0,1}(\d+)$/,
				regexText:"只能够输入正整数",
				value: KqDataAppeal.pageSize,
				listeners: {
	            	change: function(el) {
	            		KqDataAppeal.pageSize = el.value;
	            	}
	            }
			},refresh
			,{
				xtype: 'panel',
				style: 'float: right;',
				id: 'show',
				width: 160,
				border: false,
				padding: '3 0 0 5',
				html: kq.scheme.showDayDetail.substring(0,2) + '&nbsp;' + (KqDataAppeal.pageSize*(KqDataAppeal.currentPage-1)? 0:(KqDataAppeal.pageSize*(KqDataAppeal.currentPage-1)+1)) + '&nbsp;-&nbsp;' + (KqDataAppeal.pageSize*KqDataAppeal.currentPage>KqDataAppeal.totalCount?KqDataAppeal.totalCount:KqDataAppeal.pageSize*KqDataAppeal.currentPage) + '条，共&nbsp;' + KqDataAppeal.totalCount + '&nbsp;条'
			}]
    	});
        
        
        //分页自定义start
    	
    	KqDataAppeal.grid = Ext.create('Ext.grid.Panel', {
    	    rootVisible: false,
    	    columnLines: true,
            stripeRows: true,
            rowLines: true,
            id: 'kqDataAppealgridPanel',
            store: KqDataAppeal.store,
            bodyStyle:'z-index:2',//处理放大缩小浏览器 缺线问题， wangb 20190524
            tbar: [{
            	xtype:'component',
            	style:'margin-left:10px',
            	html:kq.dataAppeal.queryProgram
	        },{
	        	xtype:'panel',
	        	id:'kq_year',
	        	border:0,
	        	width:55,
	        	style:'margin-left:4px;padding-right:10px;background:url(/workplan/image/jiantou.png) no-repeat right center;',
	        	html: "<a style='color:#549FE3;cursor:pointer;'>"+KqDataAppeal.kq_year+kq.dataAppeal.year+"</a>",
	        	listeners:{
	        		'element':'el',
	        		'click':KqDataAppeal.showKqYear,
	        		scope:KqDataAppeal
	        	}
	        },{
	        	xtype:'component',
	        	width:1,
	        	height:14,
	        	style:'background-color:#c5c5c5;margin-left:4px;margin-right:4px;'
	        },{
	        	xtype:'panel',
	        	border:0,
	        	id:'kqdata_all',
	        	style:KqDataAppeal.status=='00'? "color:#549FE3;text-decoration:underline":"color:#549FE3;",
	        	html: "<a style='color:#549FE3;cursor:pointer;'>"+kq.dataAppeal.all+"</a>",
	        	listeners:{
	        		'element':'el',
	        		'click':function(){
	        			KqDataAppeal.status='00';
	        			Ext.getCmp('kqdata_all').setStyle({'text-decoration':'underline'});
	        			Ext.getCmp('kqdata_upcoming').setStyle({'text-decoration':'none'});
	        			KqDataAppeal.grid.destroy();
    					var selectBox = document.getElementById('selectBox');
				    	selectBox.innerHTML = "";
				    	var json = {};
						json.type = 'main';
						json.status = KqDataAppeal.status;

						json.scheme_id = KqDataAppeal.scheme_id;
						json.kq_duration = KqDataAppeal.kq_duration;
						json.org_id =  KqDataAppeal.org_id;
						json.kq_year = ''+KqDataAppeal.kq_year;
						KqDataAppeal.currentPage = 1;
						json.currentPage = KqDataAppeal.currentPage;
						json.pageSize = KqDataAppeal.pageSize;
						var map = new HashMap();
						map.put("jsonStr", JSON.stringify(json));
						Rpc({functionId:'KQ00021101',async:false,success:KqDataAppeal.getTableOK},map);
	        		}
	        	}
	        },{
	        	xtype:'panel',
	        	id:'kqdata_upcoming',
	        	border:0,
	        	style:KqDataAppeal.status=='01'?'color:#549FE3;margin-left:10px;text-decoration:underline':'color:#549FE3;margin-left:10px;',
	        	html:"<a  style='color:#549FE3;cursor:pointer;'>"+kq.dataAppeal.upcoming+"</a>",
	        	listeners:{
	        		'element':'el',
	        		'click':function(){
	        			KqDataAppeal.status='01';
	        			Ext.getCmp('kqdata_all').setStyle({'text-decoration':'none'});
	        			Ext.getCmp('kqdata_upcoming').setStyle({'text-decoration':'underline'});
	        			KqDataAppeal.grid.destroy();
    					var selectBox = document.getElementById('selectBox');
				    	selectBox.innerHTML = "";
				    	var json = {};
						json.type = 'main';
						json.status = KqDataAppeal.status;
						json.kq_year = ''+KqDataAppeal.kq_year;
						KqDataAppeal.currentPage = 1;
						json.currentPage = KqDataAppeal.currentPage;
						json.pageSize = KqDataAppeal.pageSize;
						json.scheme_id = KqDataAppeal.scheme_id;
						json.kq_duration = KqDataAppeal.kq_duration;
						json.org_id =  KqDataAppeal.org_id;
						var map = new HashMap();
						map.put("jsonStr", JSON.stringify(json));
						Rpc({functionId:'KQ00021101',async:false,success:KqDataAppeal.getTableOK},map);
	        		}
	        	}
	        }],
	        bbar:[pagingToolbar],
            columns:Ext.decode(column_define),
            listeners:{
                celldblclick:function ( t, td, cellIndex, record) {
                    KqDataAppeal.openKqDataMx(record.data.scheme_id,record.data.kq_duration,record.data.org_id, record.data.operation,record.data.hasNextApprover,record.data.status);
					
                }
			}
    	});
    	
    	Ext.create('Ext.container.Viewport',{
            style: 'backgroundColor:white',
            id: 'spviewport',
            layout: 'fit',
            items:[{
            	xtype: 'panel',
                title: kq.dataAppeal.dataReport,
                border: false,
                bodyBorder: false,
                autoScroll: true,
                layout: 'fit',
                items: [KqDataAppeal.grid]
            }]
    	});
    	
	},
	//改变分页
    changePage: function() {
    	Ext.getCmp("show").setHtml('显示&nbsp;' + (KqDataAppeal.pageSize*(KqDataAppeal.currentPage-1)+1) + '&nbsp;-&nbsp;' + (KqDataAppeal.pageSize*KqDataAppeal.currentPage>KqDataAppeal.totalCount?KqDataAppeal.totalCount:KqDataAppeal.pageSize*KqDataAppeal.currentPage) + '条，共&nbsp;' + KqDataAppeal.totalCount + '&nbsp;条');
    	Ext.getCmp("then").setValue(KqDataAppeal.currentPage);
    	Ext.getCmp("totalPage").setHtml(kq.scheme.page.replace("{0}",Math.ceil(KqDataAppeal.totalCount/KqDataAppeal.pageSize)));
		var json = {};
		json.type = 'main';
		json.status = KqDataAppeal.status;
		json.scheme_id = KqDataAppeal.scheme_id;
		json.kq_duration = KqDataAppeal.kq_duration;
		json.org_id =  KqDataAppeal.org_id;
		json.kq_year = ''+KqDataAppeal.kq_year ;
		json.currentPage = KqDataAppeal.currentPage;
		json.pageSize = KqDataAppeal.pageSize;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00021101',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			KqDataAppeal.store.setData([]);
			KqDataAppeal.store.add(result.returnStr.return_data.org_list);
			KqDataAppeal.store.commitChanges();
			KqDataAppeal.showPage();
		}},map);	
    },
    //改变图标的状态
    showPage: function() {
    	var page = Math.ceil(KqDataAppeal.totalCount/KqDataAppeal.pageSize);
    	if(KqDataAppeal.currentPage > 1) {
    		Ext.getCmp("page_first").setDisabled(false);
			Ext.getCmp("page_prev").setDisabled(false);
			Ext.getCmp("page_first").setIcon('/ext/ext6/resources/images/grid/page-first.gif');
    		Ext.getCmp("page_prev").setIcon('/ext/ext6/resources/images/grid/page-prev.gif');
    	}else {
    		Ext.getCmp("page_first").setDisabled(true);
			Ext.getCmp("page_prev").setDisabled(true);
			Ext.getCmp("page_first").setIcon('/ext/ext6/resources/images/grid/page-first-disabled.gif');
    		Ext.getCmp("page_prev").setIcon('/ext/ext6/resources/images/grid/page-prev-disabled.gif');
    	}	
    	
    	if(page == KqDataAppeal.currentPage) {
			Ext.getCmp("page_last").setDisabled(true);
    		Ext.getCmp("page_next").setDisabled(true);
    		Ext.getCmp("page_last").setIcon('/ext/ext6/resources/images/grid/page-last-disabled.gif');
    		Ext.getCmp("page_next").setIcon('/ext/ext6/resources/images/grid/page-next-disabled.gif');
		}else {
			Ext.getCmp("page_last").setDisabled(false);
    		Ext.getCmp("page_next").setDisabled(false);
    		Ext.getCmp("page_last").setIcon('/ext/ext6/resources/images/grid/page-last.gif');
    		Ext.getCmp("page_next").setIcon('/ext/ext6/resources/images/grid/page-next.gif');
		}
    },
    showKqYear:function(){
    	if(!this.kq_yearList.length)
    		return;
    	var selectYear = document.getElementById('selectYear');
    	if(selectYear){
    		selectYear.style.display=block;
			return;
    	}
    	var kq_year = document.getElementById('kq_year');
    	var top = 44 + parseInt(kq_year.offsetHeight);
    	var left =  parseInt(kq_year.offsetLeft);
    	var style = "style='display:block;position:absolute;top:"+top+";left:"+left+"'";
		var strHtml = "" ;
		for(var i =0 ; i < this.kq_yearList.length ; i ++){
			strHtml += "<li style='padding-left:4px;cursor:pointer' onclick='KqDataAppeal.loadKqData("+this.kq_yearList[i]+")' onmouseover='this.style.backgroundColor=\"#EDEDED\"' onmouseout='this.style.backgroundColor=\"white\"'><a style='color:#1B4A98;line-height:24px;'>"+ this.kq_yearList[i]+kq.dataAppeal.year+"</a></li>";
		}
		strHtml = "<ul style='list-style:none;margin:0px;padding:0px;'>" + strHtml + "</ul>";  	
		var selectBox = document.getElementById('selectBox');
		
		selectBox.style.position="absolute";
		
		selectBox.style.border="1px #c5c5c5 solid";
		selectBox.style.left=left+"px";
		selectBox.style.top = top+"px";
		selectBox.style.display ="block";
		selectBox.style.width = '80px';
		selectBox.style.zIndex='9999';
		if(this.kq_yearList.length >6){
			selectBox.style.height = '120px';
		}
		selectBox.style.backgroundColor='white';
		selectBox.innerHTML=strHtml;

		document.onclick=function(){
         	selectBox.style.display='none';
        }
    },
    /**加载考勤数据上报数据*/
    loadKqData:function(kq_year){
    	KqDataAppeal.grid.destroy();
    	var selectBox = document.getElementById('selectBox');
        selectBox.style.display='none';
    	selectBox.innerHTML = "";
    	var json = {};
		json.type = 'main';
		json.status = KqDataAppeal.status;
		json.scheme_id = KqDataAppeal.scheme_id;
		json.kq_duration = KqDataAppeal.kq_duration;
		json.org_id =  KqDataAppeal.org_id;
		json.kq_year = ''+kq_year ;
		KqDataAppeal.currentPage = 1;
		json.currentPage = KqDataAppeal.currentPage;
		json.pageSize = KqDataAppeal.pageSize;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00021101',async:false,success:KqDataAppeal.getTableOK},map);
    },
    renderClick:function(value, record, store){
    	if(store.data.scheme_id) {
    		return "<a style='color:#0089ff;cursor:pointer;' onclick='KqDataAppeal.openKqDataMx(\"" + store.data.scheme_id +"\",\""+store.data.kq_duration+"\",\""+store.data.org_id+"\",\""+ store.data.operation+"\",\""+store.data.hasNextApprover+"\",\""+store.data.status+"\")'>" + value + "</a>";
    	}else {
    		return value;
    	}
    },
    /**跳转至 月考勤页面*/
    openKqDataMx:function(scheme_id,kq_duration,org_id,operation,hasNextApprover,status){
    	Ext.create('KqDataURL.KqDataMx',{
    		kqYear:kq_duration.split('.')[0],
    		kqDuration:kq_duration.split('.')[1],
    		schemeId:scheme_id,
    		orgId:org_id,
    		viewType:'0',
    		showMx:'true',// 显示日明细 先暂时写死
    		operation:operation,
    		optRole:KqDataAppeal.optRole,
            hasNextApprover:hasNextApprover,
			sp_flag:status,
    		callBackFunc:eval(KqDataAppeal.loadStore)
    	});
    },
    /**
     *刷新表格数据
     */
     loadStore:function() {
    	// 46637这里分页与条数不需要重新赋值
//        KqDataAppeal.currentPage = 1;
//        KqDataAppeal.pageSize = 20;
//        KqDataAppeal.status = '00';
        var json = {};
        json.type = 'main';
        json.status = KqDataAppeal.status;
        json.scheme_id = KqDataAppeal.scheme_id;
        json.kq_duration = KqDataAppeal.kq_duration;
        json.org_id = KqDataAppeal.org_id;
        json.kq_year = KqDataAppeal.kq_year;
        json.currentPage = KqDataAppeal.currentPage;
        json.pageSize = KqDataAppeal.pageSize;
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        Rpc({
            functionId: 'KQ00021101', async: false, success: function (resp) {
                var result = Ext.decode(resp.responseText);
                KqDataAppeal.data = result.returnStr.return_data.org_list;
                KqDataAppeal.store.loadData(KqDataAppeal.data);
            }
        }, map);
    },
	//审批状态颜色
    flagRender:function (value, record, store) {
     	var color="";
     	switch(value){
			case kq.label.SpflagNoAppeal :{//未提交
				color="#9299A9";
			}break;
            case kq.label.notcreate :{//未创建
				color="#9299A9";
            }break;
            case kq.label.SpflagAppealed :{//已提交
                color="#39AB38";
            }break;
            case kq.label.SpflagApprove :{//已批准
				color = "#F6A623";
            }break;
            case kq.label.SpflagBack :{//退回
                color="#FF0000";
            }break;
            case kq.label.SpflagSubmit :{//已归档
                color="#9299A9";
            }break;
            case kq.label.pendingApproval :{//待批
                color="#39AB38";
            }break;
			default:color="#000000";
		}
     	// 47006 状态下增加渲染事件
    	if(store.data.scheme_id) {
    		value = "<span style='color:"+color+";cursor:pointer;' " +
    				"onclick='KqDataAppeal.openKqDataMx(\"" + store.data.scheme_id +"\",\""+store.data.kq_duration+"\",\""+store.data.org_id+"\"" +
    						",\""+ store.data.operation+"\",\""+store.data.hasNextApprover+"\",\""+store.data.status+"\")'>" + value + "</span>";
    	}else {
    		value = "<span style='color: "+color+"'>"+value+"</span>";
    	}
		return value;
    },
    approveUserRender:function(value){
         if(Ext.isEmpty(value))
             return "";
         return "<span title='"+value+"'>"+value+"</span>"
    },
    processRender: function (value, metaData, record, rowIndex, colIndex, store, view) {//审批过程渲染拦截器
        var map = new HashMap();
        var kq_duration=record.data.kq_duration;
        if(kq_duration.length>2){
        	kq_duration=kq_duration.substr(5,2);
        }
	     var json = {};
	     json.type="getProcess";
	     json.org_id=record.data.org_id;
	     json.scheme_id =record.data.scheme_id;
	     json.kq_duration = kq_duration;
	     json.kq_year = record.data.kq_year;
	    map.put("jsonStr", Ext.encode(json));
        var value = [];
        Rpc({
            functionId: 'KQ00021201', success: function (res) {
            	var result = Ext.decode(res.responseText);
                var returnStr = eval(result.returnStr);
                var return_code = returnStr.return_code;
                if (return_code == "success") {
                    value = returnStr.return_data.list;
                } else {
                    Ext.Msg.alert(kq.dataAppeal.tip,kq.dataAppeal.appProcessErr);
                    return;
                }
            }, scope: this, async: false
        }, map);
        var html = '';
        if(value.length == 0){
            html = '<img style="cursor: pointer;width:28px;height:28px; "title="' + kq.dataAppeal.appProcessErr + '" src="../images/noApprove.png" />';
        }else{
            html = '<img style="cursor: pointer;width:28px;height:28px;" title="' + kq.dataAppeal.appProcess + '" src="../images/approve.png" ' +
                'onclick="KqDataAppeal.approvalProcessClick(\'' + rowIndex + '\')"/>';
        }
        return html;
    },
    approvalProcessClick: function (rowIndex) {//上报过程点击事件
    	 var map = new HashMap();
    	 record=KqDataAppeal.store.data.items[rowIndex];
         var kq_duration=record.data.kq_duration;
         if(kq_duration.length>2){
         	kq_duration=kq_duration.substr(5,2);
         }
 	     var json = {};
 	     json.type="getProcess";
 	     json.org_id=record.data.org_id;
 	     json.scheme_id =record.data.scheme_id;
 	     json.kq_duration = kq_duration;
 	     json.kq_year = record.data.kq_year;
 	    map.put("jsonStr", Ext.encode(json));
         var value = [];
         Rpc({
             functionId: 'KQ00021201', success: function (res) {
             	var result = Ext.decode(res.responseText);
                 var returnStr = eval(result.returnStr);
                 var return_code = returnStr.return_code;
                 if (return_code == "success") {
                     value = returnStr.return_data.list;
                 } else {
                     Ext.Msg.alert(kq.dataAppeal.tip,kq.dataAppeal.appProcessErr);
                     return;
                 }
             }, scope: this, async: false
         }, map);
        //展示审批信息容器
        var fieldCmp = Ext.create("EHR.processViewer.ProcessViewer", {
        	processData:value,
        });
        //审批过程弹窗
        var approvalProcessWin = Ext.create("Ext.window.Window", {
            title: kq.dataAppeal.competitivePosition,
            height: 520,
            //autoHeight:true,
            width: 460,
            modal: true,
            resizable: false,
            items: [fieldCmp],
        });
        approvalProcessWin.show();
    }
});