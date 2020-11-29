Ext.define('KqSchemeURL.SchemeList',{
	constructor: function(config) {
		SchemeList = this;
		SchemeList.scheme_id = config.scheme_id;
		SchemeList.init();
    },
    init: function() {
    	Ext.QuickTips.init();
    	SchemeList.currentPage = 1;
    	SchemeList.pageSize = 5;
    	var json = {};
		json.type = "list";
		json.currentPage = SchemeList.currentPage;
		json.pageSize = SchemeList.pageSize;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00020201',async:false,success:SchemeList.getTableOK},map);
    },
    getTableOK: function(form,action) {
    	var column_define = "";
    	column_define += "[{";
    	column_define += "xtype: 'gridcolumn',sortable: false,menuDisabled: true,flex: 0.2,renderer: SchemeList.renderCheck,dataIndex: 'encrypt_scheme_id',align: 'center'";
    	column_define += "},{";
    	column_define += "xtype: 'treecolumn',";
    	column_define += "text: '"+kq.label.name+"',";//名称
    	column_define += "sortable: false,menuDisabled: false,menuDisabled: true,flex: 3,expanded: true,renderer: SchemeList.renderTree,dataIndex: 'name',iconCls: 'no-icon'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.range+"',";//应用范围
    	column_define += "flex: 2,title:true,sortable: false,menuDisabled:true,align:'left',dataIndex: 'org_scope',renderer: SchemeList.renderOrg";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.workName+"',";//考勤员
    	column_define += "flex: 1,sortable: false,menuDisabled:true,align:'left',dataIndex: 'clerk_username',renderer: SchemeList.renderClerk";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.checkName+"',";//审核人
    	column_define += "flex: 0.8,sortable: false,menuDisabled:true,renderer: SchemeList.renderReview,align:'left',dataIndex: 'reviewer_imgPath'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.organization+"',";//所属机构
    	column_define += "flex: 1.3,sortable: false,menuDisabled:true,align:'left',dataIndex: 'b0110'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.status+"',";//状态
    	column_define += "flex: 0.6,sortable: false,menuDisabled:true,renderer: SchemeList.renderStatus,align:'center',dataIndex: 'is_validate'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.createPerson+"',";//创建人
    	column_define += "flex: 0.8,sortable: false,menuDisabled:true,align:'left',dataIndex: 'create_user'";
    	column_define += "},{";
    	column_define += "xtype: 'gridcolumn',";
    	column_define += "text: '"+kq.scheme.createTime+"',";//创建时间
    	column_define += "flex: 1.1,sortable: false,menuDisabled:true,align:'left',dataIndex: 'create_time'";
    	column_define += "}]";
    	var result = Ext.decode(form.responseText);
    	SchemeList.data = result.returnStr.return_data.data;
	    SchemeList.totalCount = result.returnStr.return_data.totalCount;
		SchemeList.store = Ext.create('Ext.data.TreeStore', {
            root: {
            	// 根节点的文本
				id:'root',				
				expanded: true,
				children:SchemeList.data
            },
    	    pageSize: 5
        });
    	
    	var json = {};
		json.type = "list";
		json.currentPage = 1;
		json.pageSize = SchemeList.pageSize;
		var SearchBox = Ext.create("EHR.querybox.QueryBox",{
    		hideQueryScheme:true,
			emptyText:kq.scheme.wirteName,
			customParams:json,
            funcId:"KQ00020201",
            success:function(result){
            	SchemeList.inputValues = result.inputValues;
            	SchemeList.currentPage = 1;
            	SchemeList.totalCount = result.returnStr.return_data.totalCount;
            	Ext.getCmp("show").setHtml('显示&nbsp;' + (SchemeList.pageSize*(SchemeList.currentPage-1)+1) + '&nbsp;-&nbsp;' + (SchemeList.pageSize*SchemeList.currentPage>SchemeList.totalCount?SchemeList.totalCount:SchemeList.pageSize*SchemeList.currentPage) + '条，共&nbsp;' + SchemeList.totalCount + '&nbsp;条');
            	Ext.getCmp("then").setValue(SchemeList.currentPage);
            	Ext.getCmp("totalPage").setHtml(kq.scheme.page.replace("{0}",Math.ceil(SchemeList.totalCount/SchemeList.pageSize)));
            	var root = Ext.create('Ext.data.TreeModel', {
                    expanded: true,
                    children: result.returnStr.return_data.data
                });
    			SchemeList.store.setRoot(root);
    			SchemeList.store.commitChanges();
    			SchemeList.showPage();
            }
		});
    	
		var srcFirst = "";
		var srcPrev = "";
		var srcNext = "";
		var srcLast = "";
		var flagF = false;
		var flagL = false;
		if(SchemeList.currentPage == 1) {
			srcFirst = "/ext/ext6/resources/images/grid/page-first-disabled.gif";
			srcPrev = "/ext/ext6/resources/images/grid/page-prev-disabled.gif";
			flagF = true;
			if(SchemeList.totalCount <= SchemeList.pageSize) {
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
		    		SchemeList.currentPage = 1;
		    		SchemeList.changePage();
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
		    		SchemeList.currentPage = Number(SchemeList.currentPage) - 1;
		    		SchemeList.changePage();
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
			style: 'background-position:center;margin-left:2px !important;border: 1px solid #c5c5c5;',
		    id: 'page_next',
		    listeners: {
		    	click: function() {
		    		SchemeList.currentPage = Number(SchemeList.currentPage) + 1;
		    		SchemeList.changePage();
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
		    		SchemeList.currentPage = Math.ceil(Number(SchemeList.totalCount)/Number(SchemeList.pageSize));
		    		SchemeList.changePage();
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
		    		if((Number(SchemeList.currentPage)-1)*Number(SchemeList.pageSize) >= Number(SchemeList.totalCount)) {
		    			SchemeList.currentPage = 1;
		    		}
		    		SchemeList.changePage();
		    	}
		    }
		});
		
    	var pagingToolbar = Ext.widget('panel',{
			border: false,
			layout:'hbox',
			items: [imgPageFist,imgPagePrev,{
				xtype: 'textfield',
				fieldLabel: kq.scheme.pageInfo.split("%")[0],
				width: 50,
				labelWidth: 10,
				id: 'then',
				padding: '0 0 0 7',
				labelAlign: 'left',
				value: SchemeList.currentPage?SchemeList.currentPage:1,
				listeners: {
	            	change: function(el) {
	            		SchemeList.currentPage = el.value;
	            	}
	            }
			},{
				xtype: 'panel',
				border: false,
				id: 'totalPage',
				width: 70,
				padding: '3 0 0 5',
				html: kq.scheme.page.replace("{0}",Math.ceil(SchemeList.totalCount/SchemeList.pageSize))//页,共 {0}页
			},imgPageNext,imgPageLast,{
				xtype: 'textfield',
				fieldLabel: kq.scheme.pageInfo.split("%")[1],
				width: 70,
				padding: '0 0 0 5',
				labelWidth: 30,
				labelAlign: 'left',
				value: SchemeList.pageSize,
				listeners: {
	            	change: function(el) {
	            		SchemeList.pageSize = el.value;
	            	}
	            }
			},refresh,{
				xtype: 'panel',
				style: 'float: right;',
				id: 'show',
				border: false,
				padding: '3 0 0 5',
				html: kq.scheme.showDayDetail.substring(0,2) + '&nbsp;' + (SchemeList.pageSize*(SchemeList.currentPage-1)+1) + '&nbsp;-&nbsp;' + (SchemeList.pageSize*SchemeList.currentPage>SchemeList.totalCount?SchemeList.totalCount:SchemeList.pageSize*SchemeList.currentPage) + '条,&nbsp;共&nbsp;' + SchemeList.totalCount + '&nbsp;条'
			}]
    	});
    	//分页自定义start
    	
    	SchemeList.tree = Ext.create('Ext.tree.Panel', {
    	    rootVisible: false,
    	    columnLines: true,
            stripeRows: true,
            rowLines: true,
            id: 'treePanel',
            store: SchemeList.store,
            useArrows: true,// 不使用Vista风格的箭头代表节点的展开/折叠状态
            tbar: [{
	           	 xtype: 'button',
	             text: kq.label.add,//新增
	             height: 22,
	             listeners: {
	             	'click': function(el) {
	             		var obj = new Object();
	             		Ext.require('KqSchemeURL.SchemeDetails', function(){
	            			Ext.create("KqSchemeURL.SchemeDetails", obj);
	            		});
	             	}
	             }
	        },{
	        	xtype: 'button',
	            text: kq.label.del,//删除
	            height: 22,
	            listeners: {
	            	'click': function(el) {
	            		var checkboxs = Ext.query("*[name=schemekq]");
	            		var ids = "";
	            		var count = 0;
	                    Ext.each(checkboxs,function(checkbox,index){
	                       if(checkbox.checked == true){
	                    	   ids += "," + checkbox.id;
	                    	   count++;
	                       }
	                    })
	                    if(count == 0) {
	                    	Ext.showAlert(kq.scheme.choosePerson);
	                    	return;
	                    }
	                    Ext.showConfirm(kq.label.confirmDelete.replace("{0}",count), function (value) {
	                    	if(value=='yes') {   
	                    		var json = {};
			                    json.type = "delete";
			            		json.ids = ids.substring(1);
			            		var map = new HashMap();
			            		map.put("jsonStr", JSON.stringify(json));
			            		Rpc({functionId:'KQ00020201',async:false,success:function(form,action){
			            			var result = Ext.decode(form.responseText);
			            			if(result.succeed == true) {
				            			Ext.showAlert(kq.label.deleteSuccess);
				            			SchemeList.totalCount = SchemeList.totalCount - count;
				            			var last_page = Math.ceil(Number(SchemeList.totalCount)/Number(SchemeList.pageSize));
				    					if(SchemeList.currentPage>last_page)
				    						SchemeList.currentPage = last_page;
				    					
				            			SchemeList.changePage();
			            			}else {
			            				Ext.showAlert(kq.label.deleteFail);
			            			}
			            		}},map);
	                    	}
	                    });
	            	}
	            }
	        },SearchBox],
	        bbar:[pagingToolbar],
            columns: Ext.decode(column_define)
    	});
    	
    	Ext.create('Ext.container.Viewport',{
            style: 'backgroundColor:white',
            id: 'spviewport',
            layout: 'fit',
            items:[{
            	xtype: 'panel',
                title: kq.scheme.scheme,
                border: false,
                bodyBorder: false,
                autoScroll: true,
                layout: 'fit',
                items: [SchemeList.tree]
            }]
    	});
    	if(Ext.isGecko) {
    	var top = Ext.getDom("treePanel").childNodes[1].style.top;
    	Ext.getDom("treePanel").childNodes[1].style.top = (top.split("px")[0]-1) + "px";
    	}
    },
	
    //改变分页
    changePage: function() {
    	Ext.getCmp("show").setHtml('显示&nbsp;' + (SchemeList.pageSize*(SchemeList.currentPage-1)+1) + '&nbsp;-&nbsp;' + (SchemeList.pageSize*SchemeList.currentPage>SchemeList.totalCount?SchemeList.totalCount:SchemeList.pageSize*SchemeList.currentPage) + '条,&nbsp;共&nbsp;' + SchemeList.totalCount + '&nbsp;条');
    	Ext.getCmp("then").setValue(SchemeList.currentPage);
    	Ext.getCmp("totalPage").setHtml(kq.scheme.page.replace("{0}",Math.ceil(SchemeList.totalCount/SchemeList.pageSize)));
		var json = {};
		json.type = "list";
		json.currentPage = SchemeList.currentPage;
		json.pageSize = SchemeList.pageSize;
		json.inputValues = SchemeList.inputValues;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00020201',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			var root = Ext.create('Ext.data.TreeModel', {
                expanded: true,
                children: result.returnStr.return_data.data
            });
			SchemeList.store.setRoot(root);
			SchemeList.store.commitChanges();
			SchemeList.showPage();
		}},map);	
    },
    //改变图标的状态
    showPage: function() {
    	var page = Math.ceil(SchemeList.totalCount/SchemeList.pageSize);
    	if(SchemeList.currentPage > 1) {
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
    	
    	if(page == SchemeList.currentPage) {
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
    renderTree: function(value, record, store) {
    	if(store.data.encrypt_scheme_id) {
    		return "<a style='color:#0089ff;' onclick='SchemeList.openScheme_detail(\"" + store.data.encrypt_scheme_id + "\")'>" + value + "</a>";
    	}else {
    		return value;
    	}
    },
    
    openScheme_detail: function(scheme_id) {
    	var obj = new Object();
		obj.scheme_id = scheme_id;
 		Ext.require('KqSchemeURL.SchemeDetails', function(){
			Ext.create("KqSchemeURL.SchemeDetails", obj);
		});
    },
    
    //渲染选择按钮
    renderCheck: function(value, record, store) {
    	if(store.data.encrypt_scheme_id) {
    		return "<input type='checkbox' name='schemekq' id='" + store.data.encrypt_scheme_id + "'/>";
    	}
    },
    //渲染状态按钮
    renderStatus: function(value,record,store) {
    	if(store.data.encrypt_scheme_id) {
    		var src = "";
    		if(store.data.is_validate == 0) {
    			src = "/module/kq/images/kq_off.png";
    		}else {
    			src = "/module/kq/images/kq_on.png";
    		}
    		return "<img style='cursor:pointer;' id='state_" + store.data.encrypt_scheme_id + "' onclick='SchemeList.changeState(\"" + store.data.encrypt_scheme_id + "\")' src='" + src + "' width=40 height=20/>";
    	}
    },
    
    changeState: function(scheme_id) {
    	var state = 0;
    	var src = Ext.getDom("state_" + scheme_id).src.split("/");
    	var img = src[src.length-1];
		if(img == "kq_on.png") {
			state = 0;
		}else {
			state = 1;
		}
    	var json = {};
		json.type = "changeState";
		json.scheme_id = scheme_id;
		json.state = state; 
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00020201',async:false,success:function(form,action){
			if(state == 0) {
				Ext.getDom("state_" + scheme_id).src = "/module/kq/images/kq_off.png";
			}else {
				Ext.getDom("state_" + scheme_id).src = "/module/kq/images/kq_on.png";
			}
		}},map);
    }, 
    //渲染审核人
    renderReview: function(value, metaData, record, rowIndex, colIndex) {
    	var items = SchemeList.store.data.items;
    	var scheme_id = "";
    	//对于进行中的不能修改
    	var reviewer_id = "";
    	if(record.data.encrypt_scheme_id == undefined) {
	    	for(var i = 0; i < items.length; i++) {
	    		if(record.data.parentId == items[i].data.id) {
	    			scheme_id = items[i].data.encrypt_scheme_id;
	    			break;
	    		}
	    	}
	    	reviewer_id = record.data.reviewer_id;
    	}else {
    		scheme_id = record.data.encrypt_scheme_id;
    		reviewer_id = record.data.reviewer_id_;
    	}
    	var org_id = record.data.y_org_id;
    	var org_id_e = record.data.org_id_e;
    	var returnValue = "<div style='text-align:center'><div style='width:40px;position:relative;margin:0 auto;'><img id='list_review_" + rowIndex + "' title='" + record.data.reviewer + "' src='" + value + "' width=25 height=25" +
    			" style='cursor:pointer;border-radius: 50%;";
    	if(Ext.isEmpty(record.data.reviewer))
    		returnValue += "display:none;";
    	
    	returnValue += "' usrName='" + reviewer_id + "' onclick='SchemeList.changeClerk(" +
		    						"\"list_review_" + rowIndex  + "\"," +
		    						"true," +
		    						"\"list_review_" + rowIndex + "\"," +
		    						"\"" + (org_id?org_id:"") + "\"," +
		    						"\"" + scheme_id + "\")' " +
		    	" onmouseover='SchemeList.showOrHiddenDel(\"list_review_" + rowIndex  + "\",\"l_del_review_" + rowIndex + "\",false, \"\")'" +
		    	" onmouseout='SchemeList.showOrHiddenDel(\"list_review_" + rowIndex  + "\",\"l_del_review_" + rowIndex + "\",true, \"\")'/>" +
    			"<img src='/workplan/image/remove.png' title='" + kq.label.del + "' id='l_del_review_" + rowIndex + "' " +
    				"onclick='SchemeList.delReview(" +
    								"\"list_review_" + rowIndex  + "\"," +
    								"\"" + (org_id?org_id:"") + "\"," +
    								"\"" + scheme_id + "\"," +
									"\"" +(org_id_e?org_id_e:"")+"\")'" +
    			" style='cursor:pointer;position:absolute;top:-2px;left:26px;width:15px;height:15px;display:none;'" +
    			" onmouseover='SchemeList.showOrHiddenDel(\"list_review_" + rowIndex  + "\",\"l_del_review_" + rowIndex + "\",false, \"block\")'" +
		    	" onmouseout='SchemeList.showOrHiddenDel(\"list_review_" + rowIndex  + "\",\"l_del_review_" + rowIndex + "\",true, \"none\")' />";
    	returnValue += "<a href='###' id='a_" + rowIndex + "'";
    	if(!Ext.isEmpty(record.data.reviewer))
    		returnValue += "style='display:none;'";
    	
    	returnValue += " onclick='SchemeList.changeClerk(\"list_review_" + rowIndex  + "\",true," +
			"\"list_review_" + rowIndex + "\",\"" + (org_id?org_id:"") + "\"," +
			"\"" + scheme_id + "\")' >无</a>";
    	returnValue += "</div></div>";
    	
    	return returnValue;
    },
    showOrHiddenDel: function(title_id, id, flag, style) {
    	if(!Ext.isEmpty(style)) {
    		Ext.getDom(id).style.display = style;
    	}else {
    		if(Ext.getDom(title_id).title != '') {//解决删除了之后，删除按钮仍然显示问题
		    	if(flag) {
		    		if(SchemeList.quart)
		    			clearTimeout(SchemeList.quart);
		    		Ext.getDom(id).style.display = "none";
		    	}else {
		    		SchemeList.quart = setTimeout(function () {
		    			Ext.getDom(id).style.display = "block";
		    		}, 500);
		    	}
	    	}
    	}
    },
    //应用范围长度超出了，加title
    renderOrg: function(value, metaData, record, rowIndex, colIndex) {
    	if(value && value != '' && value.replace(/[^\x00-\xff]/g,"xx").length > 40) {
    		metaData.tdAttr = 'title="'+value+'"';
    	}else {
    		metaData.tdAttr = "";
    	}
    	
    	return value;
    },
    renderClerk: function(value, metaData, record, rowIndex, colIndex) {
    	var val = value.split("(")[1];
    	var items = SchemeList.store.data.items;
    	var canEdit = false;
    	var scheme_id = "";
    	var clerk = "";
    	//对于进行中的不能修改
    	if(!record.data.encrypt_scheme_id) {
	    	for(var i = 0; i < items.length; i++) {
	    		if(record.data.parentId == items[i].data.id) {
	    			canEdit = items[i].data.canEdit;
	    			scheme_id = items[i].data.encrypt_scheme_id;
	    			break;
	    		}
	    	}
	    	clerk = record.data.y_clerk_username;
    	}else {
    		scheme_id = record.data.encrypt_scheme_id;
    		clerk = value.split("(")[0].replace(/\s+/g,"");
    	}
    	var org_id = record.data.y_org_id;
    	metaData.tdAttr = "id='clerk_title_" + rowIndex + "' title='"+kq.scheme.fullname + value.split("(")[0]+"'";
    	return "<div style='text-align:left;cursor:pointer;color:#0089ff;' id='list_clerk_" + rowIndex + "' usrName='" + clerk + "'" +
    			"onclick='SchemeList.changeClerk(\"list_clerk_" + rowIndex  + "\",false,\"clerk_title_" + rowIndex + "\",\"" + (org_id?org_id:"") + "\"," +
    					"\"" + scheme_id + "\")'>" + val.substring(0,val.length-1) + "<div style='text-align:center;'>";
    },
    
    //flag: true: 审核员，false:考勤员,修改审核人和考勤员
    changeClerk: function(id, flag, id_title, org_id, scheme_id) {
		var picker = new PersonPicker({
            multiple: false,//因为只能选择一个人，不需要多选框
            isSelfUser: flag,//是否选择自助用户
            selfUserIsExceptMe: false,
            isMiddle: true,//是否居中显示
            isPrivExpression: flag,
            callback: function (c) {
            	var username = "";
            	var fullname = "";
            	var photo = "";
            	var name = "";
            	if(flag) {
            		username = c.id; 
            		fullname = c.name; 
            		photo = c.photo;
            		name = c.name;
            	}else {
            		username = c.userName; 
            		fullname = c.name; 
            		photo = c.name;
            		name = kq.scheme.fullname + c.userName;
            	}
            	
            	var old_name = document.getElementById(id).getAttribute("usrname");
            	SchemeList.operate(id, id_title, org_id, scheme_id, old_name, username, fullname, photo, name, flag);
            }
		});
		picker.open();
	},
	
	//删除审核员
	delReview: function(id, org_id, scheme_id, org_id_e) {
		
		Ext.showConfirm((kq.scheme.delReviewConfirm), function (value) {
			if('yes' == value) {
				var old_name = document.getElementById(id).getAttribute("usrname");
				// 55945 删除前先校验  所删除用户是否存在待办  如果存在则不允许操作
				var json = {};
				json.type = "checkReviewPerson";
				json.scheme_id = scheme_id;
		    	json.old_name = old_name;
		    	json.org_id = org_id_e;
		    	var map = new HashMap();
				map.put("jsonStr", JSON.stringify(json));
				Rpc({functionId:'KQ00020201',async:false,success:function(form,action){
					var result = Ext.decode(form.responseText);
					if("success" == result.returnStr.return_code) {
						//操作的方法单独写吧，防止删除以后有啥操作可以扩展
						SchemeList.operate(id, id, org_id, scheme_id, old_name, "", "", "/images/photo.jpg", "", true);
					}else{
						if("-1" == result.returnStr.return_msg){
							Ext.showAlert(kq.scheme.delReviewMsg);
							return;
						}
					}
				}},map);
			}
		});
	},
	
	//操作考勤员和审核员
	operate: function(id, id_title, org_id, scheme_id, old_name, username, fullname, photo, name, flag) {
		var json = {};
		json.type = "changePerson";
		json.scheme_id = scheme_id;
		json.username = username; 
		json.fullname = fullname; 
    	json.old_name = old_name;
    	json.org_id = org_id;
    	
    	json.flag = flag;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		Rpc({functionId:'KQ00020201',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			var rowId = id.split("_")[2];
			if(result.returnStr.return_code == "success") {
				if(flag) {
					Ext.getDom(id).src = photo;
					if(Ext.isEmpty(name)){
						Ext.getDom(id).style.display="none";
						Ext.getDom("a_" + rowId).style.display="";
					} else {
						Ext.getDom(id).style.display="";
						Ext.getDom("a_" + rowId).style.display="none";
					}
				}else {
					Ext.getDom(id).innerText = photo;
				}
				Ext.getDom(id_title).title = name;
				document.getElementById(id).setAttribute("usrname", username);
				//
				var data=SchemeList.store.data.items[rowId].data;
				data.reviewer=name;
				data.reviewer_id_=username;
				data.reviewer_imgPath=photo;
			}else {
				Ext.showAlert(result.returnStr.return_msg);
			}
		}},map);
	}
})