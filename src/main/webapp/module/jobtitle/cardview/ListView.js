/**
 * 资格评审_在线投票系统列表
 * 
 * */
Ext.define('JobtitleListView.ListView',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	tabName : '',//当前显示的模板名称，弹出window的标题用
	pageHeight : Ext.getBody().getViewSize().height,//兼容手机浏览器
	pageWidth : Ext.getBody().getViewSize().width,
	store : undefined,
	constructor : function(config) {
		listView_me = this;
		listView_me.useType = config.useType;//1|null：材料评审  2：投票
		listView_me.type = config.type;//1：评委会2：学科组成员3：同行专家4：二级单位
		listView_me.showItem = config.showItem,//可能是不同的列，先获取有哪些列
		listView_me.queue = config.queue;//应该展示的批次
		listView_me.itemDescList = config.itemDescList;//需要展示的列名称
		listView_me.showQueueMap = new HashMap();//用来存储当前显示的cate_id和queue
		listView_me.categorieslist = new Array();//为了在点击投票的之后页面显示出有哪几个申报人分组
		listView_me.sortItemId='';
		listView_me.isIE = Ext.isIE;
		this.support_word = this.getSupportWord();// 公示、投票环节显示申报材料表单上传的word模板内容
		this.categoriesnummap = this.getCategoriesnummap();
		this.getWidth();//获取列的宽度，因为要锁定最后一列，最后一列是一个单独的table，这样在列很少的时候会出现空隙
		this.init();
	},
	init:function(){
		Ext.util.CSS.createStyleSheet(".arrow{display: inline-block;vertical-align: middle;width: 0;height: 0;margin-left: 5px;}","card_css");
		Ext.util.CSS.createStyleSheet(".arrow.asc{border-left: 4px solid transparent;border-right: 4px solid transparent;border-bottom: 4px solid #000;}","card_css");
		Ext.util.CSS.createStyleSheet(".arrow.desc{border-left: 4px solid transparent;border-right: 4px solid transparent;border-top: 4px solid #000;}","card_css");
		Ext.util.CSS.createStyleSheet("table[style*='border-collapse'][id$='answerTable'] td{border:1px solid #c5c5c5;padding:5px}","table_css");
		this.store = this.getListViewStore();//获取store
		var map = new HashMap();
		var text = "";
		Ext.widget('panel',{
			id:'listPanel',
			renderTo: 'cardview',
			border:false,
			height:this.pageHeight,
			html:'<div id="personListDiv" onmouseover="listView_me.resetScrollParam()" style="width:'+(this.pageWidth-15)+'px;height:'+(this.pageHeight-50)+'px;overflow-y:scroll,overflow-x:scroll,margin-top:15px;"></div>',
			buttonAlign:'center',
			buttons:[
				{
			        id:'listviewsubmit',
					text:'<span style="color:#FFFFFF;font-size:large;line-height:30px;">'+zc.label.vote+'</span>',
					cls:'submitbtn',
					hidden:true,
					width:287,
					height:40,
					style: 'left: ' + (this.pageWidth/2-145) + 'px;',
					border:false,
			        handler:function(){
			        	//listView_me.store.on('load', listView_me.drowPage, undefined, {single:true});//初始化页面
			        	listView_me.store.on('load', listView_me.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
			    		listView_me.store.on('load', listView_me.submitCheck, undefined, {single:true});//初始化页面
			    		listView_me.store.load();
					}
				}/*,{
					//刷新暂时去掉，面板有总的刷新按钮
			        id:'listviewrefresh',
					text:'<span style="color:#FFFFFF;font-size:large;line-height:30px;">'+zc.label.refresh+'</span>',
					cls:'submitbtn',
					hidden:true,
					width:287,
					height:40,
					border:false,
					style: 'left: ' + (this.pageWidth/2-145) + 'px;',
			        handler:function(){
			        	this.refreshPage(true);
					},
					scope:this
				}*/],
				listeners:{
					afterrender:{
						fn:function(p){
							listView_me.store_load();
						},
						scope:this
					}
				}
		});
		
		// 重新定位，页面resize后
		Ext.EventManager.onWindowResize(function(w,h){ 
			var task = new Ext.util.DelayedTask(function(){
				this.pageHeight = window.parent.frameElement.clientHeight - 80;
				this.pageWidth = Ext.getBody().getViewSize().width;
				// 同时修改外层的iframe
				window.parent.Ext.getDom('khframe').height = this.pageHeight;
				
				// 重置名片区域宽和高
				var cardPanel = Ext.getCmp('listPanel');
				if(cardPanel){
					cardPanel.setHeight(this.pageHeight);
					//设置整体的高宽
					Ext.getDom("personListDiv").style.width = (this.pageWidth - 15) + "px";
					Ext.getDom("personListDiv").style.height = (this.pageHeight - 50) + "px";
					
					Ext.getDom("personListDiv").innerHTML = "";
					// 重新加载页面，因为多个table写死了高宽，只能重新加载页面
					listView_me.drowPage();
					listView_me.setScroll();
					if(Ext.getCmp("tipPanel")) {
						Ext.getCmp("tipPanel").setHeight(this.pageHeight-50);
						Ext.getCmp("tipPanel").setWidth(200);
					}
				}
				
				// 重新设置问卷页面
				var qnWin = Ext.getCmp('qnWin');
				if(qnWin){
					qnWin.setHeight(this.pageHeight);
					qnWin.setWidth(this.pageWidth);
				}
				//重置确认框宽和高
				var resultWindow = Ext.getCmp("resultWindow");
				if(resultWindow){
					resultWindow.setWidth(this.pageWidth*0.5);
					resultWindow.setHeight(this.pageHeight*0.7);
					
				}
			}, this);
			task.delay(200);
		},this); 
		
		window.setInterval(function(){//10分钟后台请求一次空交易，避免会话超时
			var map = new HashMap();
			map.put("type", '3');
		    Rpc({functionId:'ZC00003009',async:false,success:function(){
		    	return;
		    },scope:this},map);            
        }, 600000);
	},
	// 设置表格滑动监听
	setScroll: function() {
		this.store.load();
		this.store.on('load', function(){
			Ext.getDom("MyTable_tableData").onscroll = function() {
				if(!listView_me.scroll_type || (listView_me.isIE && listView_me.scroll_type == 'data')) {
					listView_me.scroll_type = "data";
					Ext.getDom("MyTable_tableColumn").scrollTop = Ext.getDom("MyTable_tableData").scrollTop;
					Ext.getDom("MyTable_tableHead").scrollLeft = Ext.getDom("MyTable_tableData").scrollLeft;
					Ext.getDom("MyTable_hTMLLastVal").scrollTop = Ext.getDom("MyTable_tableData").scrollTop;
				}
			};
			Ext.getDom("MyTable_tableHead").onscroll = function() {
				listView_me.scroll_type = undefined;
			};
			Ext.getDom("MyTable_tableColumn").onscroll = function() {
				if(!listView_me.scroll_type || (listView_me.isIE && listView_me.scroll_type == 'column')) {
					listView_me.scroll_type = "column";
					Ext.getDom("MyTable_tableData").scrollTop = Ext.getDom("MyTable_tableColumn").scrollTop;
					Ext.getDom("MyTable_hTMLLastVal").scrollTop = Ext.getDom("MyTable_tableColumn").scrollTop;
				}else if(listView_me.scroll_type == "lastVal") {
					listView_me.scroll_type = undefined;
				}
			};
			Ext.getDom("MyTable_hTMLLastVal").onscroll = function() {
				if(!listView_me.scroll_type || (listView_me.isIE && listView_me.scroll_type == 'lastVal')) {
					listView_me.scroll_type = "lastVal";
					Ext.getDom("MyTable_tableData").scrollTop = Ext.getDom("MyTable_hTMLLastVal").scrollTop;
					Ext.getDom("MyTable_tableColumn").scrollTop = Ext.getDom("MyTable_hTMLLastVal").scrollTop;
				}else {
					listView_me.scroll_type = undefined;
				}
			}
		}, undefined, {single:true});
	},
	resetScrollParam: function() {
		//重置参数
		listView_me.scroll_type = undefined;
	},
	
	store_load:function() {
		this.store.on('load', this.drowPage, undefined, {single:true});//初始化页面
		this.store.on('load', this.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
		listView_me.setScroll();
	},
	createTipPanel: function() {
		var list_cate = listView_me.getListCate();
		Ext.widget('panel',{
			id:'tipPanel',
			renderTo: 'cardTipView',
			border:false
		});
		listView_me.tipStore = new Ext.data.Store({
			storeId: 'simpsonsStore',
		    fields: ['categories_name'],
		    data: list_cate
		});
		
		var gridPanel = Ext.create('Ext.grid.Panel', {
			rowLines: true,
            store: listView_me.tipStore,
            border: false,
            width: 200,
            height: this.pageHeight-50,
            style: 'border-top:1px solid #c5c5c5;border-bottom:1px solid #c5c5c5;border-left:1px solid #c5c5c5;',
            columns: [{
                dataIndex: 'categories_name',
                height: 29,
                text: zc.cardview.groupName + "<img style='float: right;cursor: pointer;' onclick='listView_me.reloadPage()' title='" + zc.label.refresh + "' src='/images/new_module/refresh.png'>",
                flex: 2,
                menuDisabled: true,
                border: false,
                sortable: false,
                renderer: function (value, data, record, rowIndex) {
                	var name = record.data.categories_name;
                	var nameLength = listView_me.getStrLength(name);
                	var title = "";
                	var top = 0;
                	if(nameLength > 42) {
                		top = rowIndex > 0?15:10;
                	}else {
                		top = rowIndex > 0?22:16;
                	}
                	//字自适应
                	if(nameLength > 42){
                		title = name;
                		name = name.substr(0,21)+"...";
                	}
                	var img = "";
                	//审核账号不显示图片
                	if(listView_me.useType == "2") {
                		img = "<img style='float: right;padding-top: " + (rowIndex > 0?19:13)+ "px;width:23px;' id='tipPanel_" + record.data.categories_id + "' src='" + (record.data.isSubmit?"/images/new_module/finish.png":"/images/new_module/unfinish.png") + "'>";
                	}
                	return "<div style='height: " + (rowIndex > 0?57:46)+ "px;width:145px;white-space:normal;word-break:break-all;word-wrap:break-word;" +
                			"padding-top: " + top + "px;float: left;font-size: 13px;color: #2d8cf0;' title='" + title + "'>" + name + "</div>" + img;
                },
                listeners: {
                	'click': function(record, dom, index, rowIndex, currRecord) {
                		listView_me.selectGroup = currRecord.record.data.categories_id;
                		listView_me.reloadPage();
                		if(listView_me.oldDom) {
                			listView_me.oldDom.style.background = "";
                		}
                		dom.style.background = "#fff8d2";
                		listView_me.oldDom = dom;
                	}
                }
            }]
		});
		Ext.getCmp("tipPanel").add(gridPanel);
	},
	
	getListCate: function() {
		var list_cate = new Array();
		var items = listView_me.store.data.items;
		var flag = false;
		var map_temp = new HashMap();
		for(var i = 0; i < items.length; i++) {
			var record = items[i].data;
			if(record.itemid == 0) {
				map_temp = new HashMap();
				map_temp.put("categories_id", record.categories_id);
				map_temp.put("categories_name", record.name);
				flag = true;  
			}
			if(record.itemid != 0 && flag) {
				if(record.expert_state!=3) {
					map_temp.put("isSubmit", false);
					flag = false;
				}else {
					map_temp.put("isSubmit", true);
				}
				list_cate.push(map_temp);
			}
		}
		return list_cate;
	},
	// 获取数据集
    getListViewStore : function() {
    	var store = Ext.create('Ext.data.Store', {
    		fields:[listView_me.showItem],
			proxy:{
				type: 'transaction',
		        functionId:'ZC00003025',
				extraParams:{
					type: '1',
					sortItemId: listView_me.sortItem//排序指标和什么顺序
				},
				reader: {
					type: 'json',
					root: 'personDataList'         	
				}
			},
			autoLoad: true
		});
		return store;
	},
	getCategoriesnummap : function(){
		var categoriesnummap = '';
		
		var map = new HashMap();
		map.put("type", '4');
		map.put("useType",listView_me.useType);
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categoriesnummap = result.categoriesnummap;
	    }},map);
	    
	    return categoriesnummap;
	},
	getWidth : function() {
		listView_me.widthStyle = "width:120px;";
		listView_me.isOverLength = true;
		if(listView_me.showItem.split(",").length < 11) {//如果有11列以下，这样就会出现空隙，对于table_layout:fixed,只要不设置宽度就行，最后一列固定宽度
			listView_me.widthStyle = "";
			listView_me.isOverLength = false;
		}
	},
	
	getNowData: function() {
		var personinfo = listView_me.store.data.items;
		//如果没有数据的时候提示出来
		if(personinfo.length == 0) {
			Ext.showAlert(zc.label.canNotVote, function(){
				listView_me.goToLogon();
			});
		}
		var personinfo_temp = new Array();//优化职称，不再显示多个组了，一组一组显示，左侧可以选择组，这里找到对应的组就行
		if(listView_me.flag_coming)
			listView_me.flag_coming = undefined;
		
		//把应该显示的组显示出来
		var now_categories_id = listView_me.selectGroup;
		if(!listView_me.selectGroup) {
			for(var k = 0; k < personinfo.length; k++) {
				var record = personinfo[k].data;
				if(record.itemid != 0 && record.expert_state != 3) {
					now_categories_id = record.categories_id;
					break;
				}
			}
		}
		now_categories_id = now_categories_id?now_categories_id: personinfo[0].data.categories_id;
		for(var k = 0; k < personinfo.length; k++) {
			var info = personinfo[k].data;
			var categories_id = info.categories_id;
			if(now_categories_id == categories_id) {
				personinfo_temp.push(personinfo[k]);
			}
		}
		//过程中暂停了某个分组，面板点击该分组刷新，会出现personinfo_temp的长度为0
		if(personinfo_temp.length == 0) {
			if(listView_me.flag_coming) {
				listView_me.goToLogon();
			}else {
				listView_me.selectGroup = undefined;
				listView_me.now_categories_id = undefined;
				listView_me.flag_coming = true;
				personinfo_temp = listView_me.getNowData();
				listView_me.flag_coming = false;
			}
		}
		return personinfo_temp;
	},
	// 生成页面元素
	drowPage:function(){
		var cardviewDiv = Ext.getDom("personListDiv");//先移除所有已有数据
		
		var onlyready = false;// 是不是审查账号
							
		var personinfo_temp = listView_me.getNowData();//优化职称，不再显示多个组了，一组一组显示，左侧可以选择组，这里找到对应的组就行
		
		listView_me.overHeight = 0;//不为0超出了
		var height = 0;
		for(var k = 0; k < personinfo_temp.length; k++) {//为了算出来高度是否超出的页面的长度，这样所有的位置移动，流出滚动条显示，否则在投票的table位置挡住了滚动条
			var info = personinfo_temp[k].data;
			var lqueue = info.itemid;
			if(lqueue == 0) {
				height += 20;
			}else {
				height += 60;
			}
			if(height > (listView_me.pageHeight-118)) {
				listView_me.overHeight = 17;
				break;
			}
		}
		//如果只有一个分组，则将分组放在最上面
		//var isHasSingleCate = personinfo[0].data.categories_id == personinfo[personinfo.length-1].data.categories_id;
			
		var div = document.createElement("div");
		var innerHTMLValue = '';
		if(!listView_me.isOverLength) {//如果宽度超出了
			innerHTMLValue += "<div style='width:"+listView_me.pageWidth+"px;height:"+(listView_me.pageHeight-67)+"px;overflow: hidden;border: 1px solid #ccc;border-top-width: 0px;'>";
		}
		innerHTMLValue += "<div id='MyTable_tableData' style='display: block;overflow: scroll;width:"+(listView_me.pageWidth+17-listView_me.overHeight)+"px; height:"+(listView_me.pageHeight-50)+"px; position: relative; z-index: 35; top: 0px; left: -1px;'><table id='tabids' cellspacing='0' cellpadding='0' align='center' style='border-collapse: collapse;margin:0;table-layout:fixed;font-size:16px;margin:auto;width:"+(listView_me.pageWidth-listView_me.overHeight)+"px;text-align:center;'>";
		//用于固定某一列，思路：新建一个固定的table，遮在上面，使该列固定,前两列
		listView_me.hTMLVal = '<div style="overflow: hidden;width: 241px;position: relative; z-index: 40; top: -'+(listView_me.pageHeight-50)+'px; left: -1px;"><div id="MyTable_tableColumn" style="width: 261px; '+
				'overflow-y: scroll;overflow-x: hidden; height:'+(listView_me.pageHeight-67)+'px; "><table style="font-size:16px;margin:0;text-align:center;border-collapse: collapse;table-layout:fixed;width:'+(listView_me.pageWidth-listView_me.overHeight)+'px;">';
		
		var margin = "0px";
		if(Ext.isGecko) {
			margin = "0 0 0 1px;"
		}
		
		//现在页面上只可能有一个组，这样样式固定就行
		var isHasSingleCate = true;
		//如果只有一个分组，则将分组放在最上面,对应的各div table的位置改变
		listView_me.headHTMLVal = '<div id="MyTable_tableHead" style="width: '+(listView_me.pageWidth-listView_me.overHeight)+'px; overflow: hidden; height:81px; position: relative; z-index: 45; background-color: #f5f3f3; top: -'+((listView_me.pageHeight-61)*3)+'px; left: -1px;"><table style="font-size:16px;margin:0;text-align:center;border-collapse: collapse;table-layout:fixed;width:'+(listView_me.pageWidth-listView_me.overHeight)+'px;">';
		
		listView_me.tableFixHTMLVal = '<div id="MyTable_tableFix" style="width: 241px; overflow: hidden; height:81px; position: relative; z-index: 50; background-color: #f5f3f3; top: -'+((listView_me.pageHeight-34)*3)+'px; left: -1px;"><table style="font-size:16px;margin:0;text-align:center;border-collapse: collapse;table-layout:fixed;width:'+(listView_me.pageWidth-listView_me.overHeight)+'px;">';
		var zIndex = 50;
		if(listView_me.useType != "2") {
			zIndex = 20;
		}
		
		var fireFox = 0;
		if(Ext.isGecko) {
			fireFox = 1;
		}
		listView_me.hTMLLastVal = '<div id="hTMLLastVal_div" style="overflow: hidden;width: 221px;position: relative; z-index: ' + zIndex + '; top: -'+((listView_me.pageHeight-58)*2)+'px; left: '+(listView_me.pageWidth-222-listView_me.overHeight-fireFox)+'px;">'+
				'<div id="MyTable_hTMLLastVal" style="width: 241px;overflow-y: scroll;overflow-x: hidden;height:'+(listView_me.pageHeight-147)+'px;" >'+
				'<table style="font-size:16px;margin:'+margin+';text-align:center;border-collapse: collapse;table-layout:fixed;">';
		
		listView_me.hTMLLastHeadVal = '<div id="MyTable_hTMLLastHeadVal" style="width: 221px; overflow: hidden; height:81px; position: relative; z-index: 60; top: -'+((listView_me.pageHeight-59)*2+1-fireFox)+'px; left: '+(listView_me.pageWidth-222-listView_me.overHeight)+'px;"><table style="font-size:16px;margin:' + margin + ';text-align:center;border-collapse: collapse;table-layout:fixed;">';
		//拼装显示的名称
		var temp_html = "";
		var showItemArray = listView_me.showItem.split(",");
		//显示的内容
		var oldqueue = 1;
		var newqueue = 0;
		var tabCate_tr = "";
		var display = "";
		var needShowQueue = 0;
		for(var i=0; i<personinfo_temp.length; i++){
			var info = personinfo_temp[i].data;
			newqueue = info.itemid;
			var approvalState = info.approvalState;
			var expert_state = info.expert_state;
			if(newqueue == 0) {//展示申报人分组名称以及批次
				oldqueue = 1;//因为有多个申报人分组，每个申报人分组又有多个批次
				listView_me.currentCate_id = info.categories_id;
				//分组头--start
				if(isHasSingleCate) {
					var width = 0;
					if(personinfo_temp[1].data.usetype != 1){//只有投票账号的时候加上投票列表长度
						width = 221;
					}
					//同行和学科组不需要显示这个图片
					var img = (listView_me.type == 4 || listView_me.type == 1)?"<img title='" + zc.cardview.showGroup + "' onclick='listView_me.showTipPanel()' style='cursor:pointer;width:26px;padding-right:8px;vertical-align: middle;height:20px;' src='/images/new_module/expand_.png'/>":"";
					//不要批次了，这样这里bottom只要-4px
					temp_html += "<tbody><div style='width:"+(listView_me.isOverLength?((showItemArray.length)*120+width):listView_me.pageWidth)+"px;" +
									"background-color: #fff;white-space:normal;border:0px solid #c5c5c5;padding:0 0 2px 5px;text-align:left;vertical-align:middle;" +
									"height:30px;color:#007aff;\'><span style='position:relative;font-weight:bold;bottom:-8px;font-family:SimSun;font-size:15px;'>" +
							img + "<span style='padding-top: 2px;vertical-align: middle;height:20px;width: " + (listView_me.pageWidth-50) + "px;'>" + info.name + '</span></span>';
				}else {
					temp_html += '<tbody><tr><td colspan="' + (listView_me.isOverLength?(showItemArray.length+1):showItemArray.length) + '" style=\"background-color: #fff;white-space:normal;border:0px solid #c5c5c5;padding-left:5px;text-align:left;vertical-align:middle;width:120px;height:30px;color:#007aff;\"><span style="position:relative;font-weight:bold;bottom:3px;font-family:SimSun;font-size:15px;"><img src=\"/images/new_module/listview.png\">' + info.name + '</span>';
				}
				//不需要批次了
				needShowQueue = parseInt(info.queueMax);
				var style = needShowQueue==oldqueue?"":"style='display:none;'";
				if(isHasSingleCate) {//如果是单个组的时候先展示分组名，批次，在展示标题，这里是循环展示标题
					temp_html += "</div></tbody>";
					temp_html += '<tr>';
					for(var k = 0; k < listView_me.itemDescList.length; k++) {//js用for-each会多出一个长度，是自己的数组
						var itemdesc = listView_me.itemDescList[k];
						var length = listView_me.getStrLength(itemdesc);
						//数值型的添加排序
						if(info[showItemArray[k]].substring(0,1)=="0") {
							temp_html += "<td "+ (length>24?"title='"+itemdesc+"'":"")+" style='border:1px solid #c5c5c5;"+listView_me.widthStyle+"height:50px;background-color:#f5f3f3;' onclick='javascript:listView_me.orderClick(\""+showItemArray[k]+"\")'>"+
							(length > 24?(itemdesc.substr(0,listView_me.getSubStrLength(itemdesc))+"..."):itemdesc)+"<span id='arrow_"+showItemArray[k]+"' "+((listView_me.sortItemId==showItemArray[k])?"class='"+listView_me.className+"'":"")+" ></span></td>";
						}else 
							temp_html += '<td '+ (length>24?'title="'+itemdesc+'"':'')+' style="border:1px solid #c5c5c5;'+listView_me.widthStyle+'height:50px;background-color:#f5f3f3;">'+(length > 24?(itemdesc.substr(0,listView_me.getSubStrLength(itemdesc))+'...'):itemdesc)+'</td>';
					}
				}else {
					temp_html += "</tr></tbody>";
				}
				//分组头--end
				
				//标题，
				if(isHasSingleCate) {
					//是否显示固定的投票列
					if(personinfo_temp[1].data.usetype != 1){//查看账号)
						listView_me.hTMLLastHeadVal += '<tbody><tr><td style=\"white-space:normal;border:0px solid #c5c5c5;width:210px;height:30px;color:#007aff;\"><span style="position:relative;font-weight:bold;font-family:SimSun;font-size:15px;"></span></td></tr></tbody>';
						temp_html += '<td style=\"border:1px solid #c5c5c5;width:220px;height:50px;background-color:#f5f3f3;\">'+zc.label.vote+'</td>';
						listView_me.hTMLLastHeadVal += '<tr><td style=\"border:1px solid #c5c5c5;width:220px;height:50px;background-color:#f5f3f3;\">'+zc.label.vote+'</td>';
						listView_me.hTMLLastHeadVal += '</tr>';
					}
					temp_html += '</tr>';
				}else {
					listView_me.hTMLLastVal += '<tbody><tr><td colspan="' + (listView_me.isOverLength?(showItemArray.length+1):showItemArray.length) + '" style=\"white-space:normal;border:0px solid #c5c5c5;padding-left:5px;text-align:left;vertical-align:middle;width:120px;height:30px;color:#007aff;\"><span style="position:relative;font-weight:bold;font-family:SimSun;font-size:15px;"></span></td></tr></tbody>';
				}
				
				listView_me.hTMLLastVal += "<tbody id='radio_" + info.categories_id + "_1' "+style+">";
				
				temp_html += "<tbody id='tab_" + info.categories_id + "_1' "+style+">";
				listView_me.showQueueMap.put(listView_me.currentCate_id,needShowQueue);
				listView_me.categorieslist.push(listView_me.currentCate_id);
			}else if(newqueue != oldqueue) {//当显示第二个批次的时候先隐藏
				oldqueue = newqueue;
				var style = needShowQueue==oldqueue?"":"style='display:none;'";
				temp_html += "</tbody>";
				temp_html += "<tbody id='tab_" + listView_me.currentCate_id + "_" + info.itemid + "' "+style+">";
				listView_me.hTMLLastVal += "</tbody><tbody id='radio_"+ listView_me.currentCate_id + "_" + info.itemid + "' "+style+">"
				temp_html += listView_me.getHtml(approvalState,info,showItemArray,expert_state,display);
			}else {
				
				temp_html += '<tr>';
				temp_html += listView_me.getHtml(approvalState,info,showItemArray,expert_state);
				temp_html += '<tr>';
			}
		}
		temp_html += '</tbody></table></div>';
		listView_me.hTMLVal += temp_html+'</div>';
		listView_me.headHTMLVal += temp_html;
		listView_me.tableFixHTMLVal += temp_html;
		listView_me.tableRighrHeadHTMLVal += temp_html;
		listView_me.tableVoteHTMLVal += temp_html;
		innerHTMLValue += temp_html;
		innerHTMLValue += listView_me.hTMLVal;
		innerHTMLValue += listView_me.hTMLLastHeadVal + '</tbody></table></div>';
		innerHTMLValue += listView_me.hTMLLastVal + '</tbody></table></div></div>';
		innerHTMLValue += listView_me.headHTMLVal;
		innerHTMLValue += listView_me.tableFixHTMLVal;
		if(!listView_me.isOverLength) {
			innerHTMLValue += '</div>';
		}
		var div = document.createElement("div");
		div.innerHTML = innerHTMLValue;
		cardviewDiv.appendChild(div);
		//在最后写了，如果左侧选择组是展开的，自动适应浏览器，不在上面加参数了，参数太多，看不懂
		if(Ext.getDom("cardTipView").style.display == "block") {
			listView_me.changeLeftWidth(-200, "block", false);
		}
		if(listView_me.tipStore) {
			var list_cate = listView_me.getListCate();
			listView_me.tipStore.setData(list_cate);
		}
		//再次刷新的时候让位置不变，还有排序的时候位置不变
		Ext.getDom("MyTable_tableColumn").scrollTop = listView_me.scrollTop;
		Ext.getDom("MyTable_tableHead").scrollLeft = listView_me.scrollLeft;
		Ext.getDom("MyTable_hTMLLastVal").scrollTop = listView_me.scrollTop;
		Ext.getDom("MyTable_tableData").scrollTop = listView_me.scrollTop;
		Ext.getDom("MyTable_tableData").scrollLeft = listView_me.scrollLeft;
		Ext.getDom("MyTable_tableData").scrollTop = listView_me.scrollTop;
	},
	
	getHtml:function(approvalState,info,showItemArray,expert_state) {
		var innerHTMLValue = "";
		var temp_itemid = "";
		var agreeCheckState = '', disaplayCheckState = '', giveupCheckState = '';
		var state = '0';//提交过的只能看，不能点击
		var agreeCheckBackgroud = 'background:url(/images/new_module/agree_unchecked.png) no-repeat;', disaplayCheckBackgroud = 'background:url(/images/new_module/disagree_unchecked.png) no-repeat;', giveupCheckBackgroud = 'background:url(/images/new_module/giveup_unchecked.png) no-repeat;';
		if(approvalState) {
			if(approvalState == 1){
				agreeCheckState = 'checked=checked';
				agreeCheckBackgroud = 'background:url(/images/new_module/agree_checked.png) no-repeat;';
			}else if(approvalState == 2){
				disaplayCheckState = 'checked=checked';
				disaplayCheckBackgroud = 'background:url(/images/new_module/disagree_checked.png) no-repeat;';
			}else if(approvalState == 3){
				giveupCheckState = 'checked=checked';
				giveupCheckBackgroud = 'background:url(/images/new_module/giveup_checked.png) no-repeat;';
			}
		}
		if(!Ext.isEmpty(expert_state) && expert_state == '3') {
			state = '1';//结束的不能点击
		}
		for(var k = 0; k < showItemArray.length; k++) {
			temp_itemid = showItemArray[k];
			var name = info[temp_itemid];
			var title = "";
			//对于长度超出的截断，给title
			if(listView_me.getStrLength(name)>28 && (temp_itemid != "w0535" && temp_itemid != "w0536" && temp_itemid != "w0537" && temp_itemid != "w0539" && temp_itemid != "w0541")) {
				title = name;
				name = name.substr(0,13)+'...';
			}
			if((k == 0 || k == 1) && (temp_itemid != "w0535" && temp_itemid != "w0536" && temp_itemid != "w0537" && temp_itemid != "w0539" && temp_itemid != "w0541")) {
				innerHTMLValue += '<td title=\"'+title+'\" style=\"white-space:normal;background-color:#f9f9f9;border:1px solid #c5c5c5;'+listView_me.widthStyle+'height:60px;\">'+name+'</td>';
			}else {
				innerHTMLValue += '<td title=\"'+title+'\" style=\"white-space:normal;border:1px solid #c5c5c5;'+listView_me.widthStyle+'height:60px;\">';
				if((temp_itemid == "w0535" || temp_itemid == "w0536") && (!Ext.isEmpty(info.w0535) || !Ext.isEmpty(info.w0536))){
					innerHTMLValue+="<a style='font-size:15px;' href=javascript:listView_me.checkfile('"+info.w0535+"','"+info.nbasea0100_safe+"','"+info.type+"','"+info.w0536+"');><img src='/images/new_module/salaryitem.gif' border='0'></a>";
				}else if(temp_itemid == "w0537" && !Ext.isEmpty(info.w0537)){
					innerHTMLValue+="<a style='font-size:15px;' href=javascript:listView_me.checkfile('"+info.w0537+"','"+info.nbasea0100_safe+"','"+info.type+"');><img src='/images/new_module/salaryitem.gif' border='0'></a>";
				}else if (temp_itemid == "w0539" || temp_itemid == "w0541"){
					var text = "";
					if(info.expert_state == 2 || info.expert_state == 3){//页面已提交、问卷已答
						text = '(已评)';
					} else {
						text = '(未评)';
					}
					if(listView_me.type == 1 || listView_me.type == 2 || listView_me.type == 4){
						if(temp_itemid == "w0539" && !Ext.isEmpty(info.w0539) && !Ext.isEmpty(info.w0539_qnid)){
							innerHTMLValue+="<a id='ques_"+info.w0501+"' style='font-size:15px;' href=javascript:listView_me.questionnaire('"+listView_me.type+"','"+info.w0539+"','"+info.w0541+"','"+info.w0539_qnid+"','"+info.w0541_qnid+"','"+info.w0501+"','"+info.w0301+"','"+info.expert_state+"','"+info.expertName+"','"+info.subObject+"','"+info.categories_id+"');>评审意见</a><span style='font-size:12px;' id='span_"+info.w0501+"'>"+text+"</span>";
						}else {
							innerHTMLValue+="&nbsp;";
						}
					}else if(listView_me.type == 3){
						if(temp_itemid == "w0541" && !Ext.isEmpty(info.w0541) && !Ext.isEmpty(info.w0541_qnid)){
							innerHTMLValue+="<a id='ques_"+info.w0501+"' style='font-size:15px;' href=javascript:listView_me.questionnaire('"+info.type+"','"+info.w0539+"','"+info.w0541+"','"+info.w0539_qnid+"','"+info.w0541_qnid+"','"+info.w0501+"','"+info.w0301+"','"+info.expert_state+"','"+info.expertName+"','"+info.subObject+"','"+info.categories_id+"');>"+zc.label.checkcomment+"</a><span style='font-size:12px;' id='span_"+info.w0501+"'>"+text+"</span>";
						}else {
							innerHTMLValue+="&nbsp;";
						}
					}
				}
				else {
					innerHTMLValue+=name;
				}
				innerHTMLValue += '</td>';
			}
		}
		if(info.usetype != 1) {
			var left = '-1px';
			listView_me.hTMLLastVal += '<tr>';
			listView_me.hTMLLastVal += "<td style=\"background-color:#ffffff;border:1px solid #c5c5c5;width:220px;height:60px;font-size:13px;\">";
			listView_me.hTMLLastVal += "<div><label style='position: relative;margin-left:11px;' for='listView_"+info.w0501+"_1'><label id='label_"+info.w0501+"_1' style='height:20px;width:20px;position: absolute;left:"+left+";top:-2px;background-size:cover;"+agreeCheckBackgroud+"' for='listView_"+info.w0501+"_1'></label>" +
					"<input type='radio' style='display:inline-block;opacity:0;background-size:cover;' " +agreeCheckState+ " id='listView_" + info.w0501 + "_1' value='1' onclick='listView_me.updateCommentRs(this.value,\""+info.w0501+"\",\""+info.w0301+"\",\""+info.categories_id+"\",\""+state+"\")' name='listViewRadio_" + info.categories_id + "_" + info.w0501 + "' >"+info.agreetext+"</label>";
			
			listView_me.hTMLLastVal += "<label style='position: relative;margin-left:11px;' for='listView_"+info.w0501+"_2'><label id='label_"+info.w0501+"_2' style='height:20px;width:20px;position: absolute;left:"+left+";top:-2px;background-size:cover;"+disaplayCheckBackgroud+"' for='listView_"+info.w0501+"_2'></label>" +
					"<input type='radio' style='display:inline-block;opacity:0;background-size:cover;' " +disaplayCheckState+ " id='listView_" + info.w0501 + "_2' value='2' onclick='listView_me.updateCommentRs(this.value,\""+info.w0501+"\",\""+info.w0301+"\",\""+info.categories_id+"\",\""+state+"\")' name='listViewRadio_" + info.categories_id + "_" + info.w0501 + "' >"+info.disagreetext+"</label>";
			
			listView_me.hTMLLastVal += "<label style='position: relative;margin-left:11px;' for='listView_"+info.w0501+"_3'><label id='label_"+info.w0501+"_3' style='height:20px;width:20px;position: absolute;left:"+left+";top:-2px;background-size:cover;"+giveupCheckBackgroud+"' for='listView_"+info.w0501+"_3'></label>" +
					"<input type='radio' style='display:inline-block;opacity:0;background-size:cover;' " +giveupCheckState+ " id='listView_" + info.w0501 + "_3' value='3' onclick='listView_me.updateCommentRs(this.value,\""+info.w0501+"\",\""+info.w0301+"\",\""+info.categories_id+"\",\""+state+"\")' name='listViewRadio_" + info.categories_id + "_" + info.w0501 + "' >"+info.giveuptext+"</label></div></td>";
			listView_me.hTMLLastVal += '</tr>';
		}
		return innerHTMLValue;
	},
	//点击其他批次的时候
	findQueue:function(categories_id,queue) {
		//替换内容
		var nowQueue = listView_me.showQueueMap[categories_id];
		var newShowId = "tab_" + categories_id + "_" + queue;
		var oldHiddenId = "tab_" + categories_id + "_" + nowQueue;
		if(newShowId != oldHiddenId) {
			
			var oldShow = Ext.query('tbody[id=tab_' + categories_id + "_" + nowQueue+']');
			var newShow = Ext.query('tbody[id=tab_' + categories_id + "_" + queue+']');
			var oldImgShow = Ext.query('img[id=img_' + categories_id + "_" + nowQueue+']');
			var newImgShow = Ext.query('img[id=img_' + categories_id + "_" + queue+']');
			for(var i = 0; i < newShow.length; i++) {
				newShow[i].style.display = "";
			}
			for(var i = 0; i < oldShow.length; i++) {
				oldShow[i].style.display = "none";
			}
			for(var i = 0; i < newImgShow.length; i++) {
				newImgShow[i].src = '/images/new_module/checked.png';
			}
			for(var i = 0; i < oldImgShow.length; i++) {
				oldImgShow[i].src = '/images/new_module/unchecked.png';
			}
			Ext.getDom('radio_' + categories_id + "_" + nowQueue).style.display = 'none';
			Ext.getDom('radio_' + categories_id + "_" + queue).style.display = '';
			listView_me.showQueueMap.put(categories_id,queue);//重新赋值
			listView_me.store.on('load', listView_me.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
			listView_me.store.load();
		}
	},
	//排序前的操作
	orderClick:function(itemId) {
		listView_me.sortItemId = itemId;
		var classname = Ext.getDom("arrow_"+itemId).className;
		if(Ext.isEmpty(classname)) {//如果没有排过序，正序排
			listView_me.className = "arrow asc";
			listView_me.sortItem = itemId + " asc";
		}else if(classname == "arrow asc") {//上一次是正序，这一个反序
			listView_me.className = "arrow desc";
			listView_me.sortItem = itemId + " desc";
		}else if(classname == "arrow desc") {
			listView_me.className = "arrow asc";
			listView_me.sortItem = itemId + " asc";
		}
		this.refreshPage(true,itemId);
	},
	// 页面初始化校验//主要是一些按钮的显示问题
	initCheck:function(){
		var items = listView_me.getNowData();//优化职称，不再显示多个组了，一组一组显示，左侧可以选择组，这里找到对应的组就行
		//var items = listView_me.store.data.items;
		
		if(items.length == 0){//没有
			//因为有分组面板，所以在进行中暂停某个分组，这样刷新该分组时会导致退出
			if(!listView_me.isshowrefresh && (listView_me.type == 2 || listView_me.type == 3)){
				Ext.showAlert(zc.cardview.haveSubmited, function(){
					listView_me.goToLogon();
				});
			}else {
				var listviewsubmit = Ext.getCmp('listviewsubmit');
				//刷新暂时去掉，面板有总的刷新按钮
				//var listviewrefresh = Ext.getCmp('listviewrefresh');
				if(listviewsubmit){
					listviewsubmit.hide();
				}
				/*if(listviewrefresh){
					listviewrefresh.show();
				}*/
			}
			return ;
		}
		
		// 校验是否已提交 ：判断全部数据，都是已提交了才算已提交
		var isSubmit = true;
		var onlyready = false;
		
		for(var key in listView_me.showQueueMap) {
			for(var i=0; i<items.length; i++) {
				var record = items[i];
				var expertState = record.data.expert_state;
				var categories_id = record.data.categories_id;
				if(categories_id == key && record.data.itemid == listView_me.showQueueMap[key]) {
					var type = record.data.type;
					if(isSubmit && expertState == '3'){//3:已提交
						isSubmit = true;
					}else {//多个分组的时候，来回切换的时候，其中一个分组投完了，另一个没有投票
						isSubmit = false;
					}
					
					var usetype = record.data.usetype;
					if(usetype == 1){//查看账号
						onlyready = true;
					}
					
					if(type == 3){//外部专家时，不区分账号类型，全部可以投票
						onlyready = false;
					}
				}
			}
		}
		// 如果都是已提交状态则 ： 1、不显示【保存】【提交】按钮  2、赞成反对弃权不可修改。
		if(isSubmit){
			// 【保存】【提交】按钮隐藏，【刷新】显示
			var listviewsubmit = Ext.getCmp('listviewsubmit');
			//刷新暂时去掉，面板有总的刷新按钮
			//var listviewrefresh = Ext.getCmp('listviewrefresh');
			if(listviewsubmit){
				listviewsubmit.hide();
			}
			/*if(listviewrefresh){
				listviewrefresh.show();
			}*/
			// 页面上的radio不可用
			/*var radlioArray = Ext.query('input[type=radio]');
			if(radlioArray.length > 0){
				for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					Ext.getDom(radio.id).disabled = true;
				}
			}*/
			return;
		}
		
		// 查看账号时：1、不显示问卷 2、不显示赞成反对弃权 3、不显示投票按钮。
		if(onlyready){
			// 1、不显示问卷 2、不显示赞成反对弃权
			var liArray = Ext.query('td[type=agreeli]');
			if(liArray.length > 0){
				for(var i=0; i<liArray.length; i++){
					var li = liArray[i];
					Ext.getDom(li).style.display = 'none';
				}
			}

			// 3、不显示投票按钮，不显示刷新按钮
			var listviewsubmit = Ext.getCmp('listviewsubmit');
			//刷新暂时去掉，面板有总的刷新按钮
			//var listviewrefresh = Ext.getCmp('listviewrefresh');
			if(listviewsubmit){
				listviewsubmit.hide();
			}
			/*if(listviewrefresh){
				listviewrefresh.hide();
			}*/
		} else {
			// 显示投票按钮，不显示刷新按钮
			var listviewsubmit = Ext.getCmp('listviewsubmit');
			//刷新暂时去掉，面板有总的刷新按钮
			//var listviewrefresh = Ext.getCmp('listviewrefresh');
			if(listviewsubmit){
				listviewsubmit.show();
			}
			/*if(listviewrefresh){
				listviewrefresh.hide();
			}*/
		}
	},
	// 点击【投票，提交】按钮时校验
	submitCheck:function(){
		// 校验赞成人数
		if(!listView_me.checkAgreeNumForSubmit()){
			return ;
		}
		
		// 校验提交情况
		var isDone = true;
		//var items = listView_me.store.data.items;
		var items = listView_me.getNowData();//优化职称，不再显示多个组了，一组一组显示，左侧可以选择组，这里找到对应的组就行
		var state = '';
		var msg = '';
		var count = 0;
		var oldCateId = "";
		var newCateId = "";
		var cateIdWithCountMap = new HashMap();//记录下名字和id的，后面的显示用
		var cateIdWithNameMap = new HashMap();//记录下名字和id的，后面的显示用
		/*for(var i = 0; i < items.length; i++) {
			var record = items[i];
			var approvalState = record.data.approvalState;
			var categories_id = record.data.categories_id;
			var queue = record.data.itemid;
			var w0511 = record.data.name;
			newCateId = categories_id;
			if(newCateId != oldCateId) {//这个没必要这样循环写，可以通过后台直接传就行，以后修改
				oldCateId = newCateId;
				cateIdWithNameMap.put(categories_id,w0511);
				continue;
			}
			var expert_state = record.data.expert_state;
			//判断有哪些人没有选择
			if(expert_state != null && expert_state != '3'){//只要不是已经提交的，就说明选择的是这个批次的，只对这个批次的进行判断
				//listView_me.showQueueMap.put(categories_id,queue);
				state = '1';
			}
		}*/
		//if(msg != '') {//多个分组的时候有分组没有点击
		//	Ext.showAlert(msg.substring(1)+zc.cardview.unFinish, function(){
				//listView_me.refreshPage();
		//	});
		//	return;
		//}
		/*if(state == '') {
			Ext.showAlert('请给'+items[0].data.w0511+'投票！');
			return;
		}*/
		for(var key in listView_me.showQueueMap) {
			count = 0;
			for(var i=0; i<items.length; i++) {
				var record = items[i];
				var categories_id = record.data.categories_id;
				var queue = record.data.itemid;
				var expert_state = record.data.expert_state;
				var w0511 = record.data.name;
				newCateId = categories_id;
				if(newCateId != oldCateId) {
					oldCateId = newCateId;
					cateIdWithNameMap.put(categories_id,w0511);
					continue;
				}
				//只需要校验某个申报人分组下的一个批次
				if(categories_id == key && queue == listView_me.showQueueMap[key]) {
					count = count+1;
					cateIdWithCountMap.put(categories_id,count);
					if(expert_state != '3') {
						var w0511 = record.data.w0511;
						// 校验赞成、反对、弃权项是否填写
						var approvalState = record.data.approvalState;
						if(Ext.isEmpty(approvalState)){
							isDone = false;
							msg += w0511+',';
							continue ;
						}
					}
				}
			}
		}
		
		// 校验后，通过：1、显示最终确认单  2、则更新专家状态标识（expert_state）为已提交（3）
		if(isDone){
			
			var container = Ext.widget('container', {
				layout:{
					type:'vbox'
				},
				scrollable : 'y',
				items:[]
			});
			//var queue = showItemArray[2];//批次的itemId
			for(var key in cateIdWithNameMap) {
				if(key == 'put' || key == 'get') 
					continue;
				var innerHTMLValue = '';
				innerHTMLValue += cateIdWithNameMap[key]+'：';
				innerHTMLValue += zc.label.declare+' ';
				innerHTMLValue += cateIdWithCountMap[key] + zc.label.person + '。';
				var titlelabel = Ext.widget('label', {
					text:innerHTMLValue,
					width:'100%',
					margin:'0 0 8 5',
					padding:'8 0 8 0',
					style:'font-size:14px;color:#007aff;background-color:#F7F7F7;'
						
				});
				container.add(titlelabel);
				for(var i=0; i<items.length; i++) {
					
					var record = items[i].data;
					//只统计当前批次的人，其他批次的不进行统计
					if(record.categories_id != key || record.itemid == "0" || listView_me.showQueueMap[record.categories_id] != record.itemid)
						continue;
					
					var perContainer = Ext.getCmp('perCon_'+record.categories_id+record.c_level);
					if(!perContainer){
						perContainer = Ext.widget('container', {
							id:'perCon_'+record.categories_id+record.c_level,
							width:'100%',
							layout:{
								type:'table',
								columns: 2,
								tableAttrs: {
							            style: {
							                width: '100%'
							            }
						        },
						        tdAttrs : {
						        	style: {
						                width: '50%'
						            }
						        }
							},
							border:false,
							scrollable : 'y',
							items:[]
						});
						container.add(perContainer);
					}
					
					var fieldcontainer = listView_me.getSubmitPagefieldContainer(record);
					perContainer.add(fieldcontainer);
				}
			}
			
			var confirmWindow = Ext.widget('window', {
				id:'resultWindow',
				title:zc.label.confirmResult,
				closeToolText : '',
				width:listView_me.pageWidth*0.5,
				minWidth:500,
				height:listView_me.pageHeight*0.7,  
				resizable: false,  
				modal: true,
				border:false,
				bodyStyle: 'background:#ffffff;',
				layout: {
		            type: 'fit'
		        },
		        items:[container],
		        buttonAlign:'center',
		        buttons:[{
		        	id:'okbtn',
		        	text:'<span style="border:0px;font-size:16px;line-height:20px;color:#FFFFFF">'+zc.label.confirm+'</span>',
		        	cls:'okbtn',
		        	width:137,
					height:40,
					border:false,
		        	handler:function(){
		        		listView_me.submitResult();
		        	}
		        },{
		        	id:'cancelbtn',
		        	text:'<span style="border:0px;font-size:16px;line-height:20px;color:#FFFFFF">'+zc.label.cancel+'</span>',
		        	cls:'cancelbtn',
		        	width:137,
					height:40,
					border:false,
		        	handler:function(){
		        		confirmWindow.close();
		        	}
		        }],
		        listeners:{
		        	close:function(){
		        		//listView_me.updateRadioState();
		        	}
		        }
			}).show();
		} else {
			Ext.showAlert(msg.substring(0, msg.length-1)+zc.cardview.unFinish, function(){
				//listView_me.refreshPage();
			});
		}
	},
	getSubmitPagefieldContainer : function(record){
		var w0511 = record.w0511;
		var approvalState = record.approvalState;
		var w0501 = record.w0501;
		var w0301 = record.w0301;
		var expertState = record.expert_state;
		var categories_id = record.categories_id;
		var c_level = record.c_level;
		return  Ext.widget('container', {
            style:'margin:0 auto;',
            margin:'0 0 0 10',
            height:55,
			layout:{
            	type:'vbox',
            	align:'left'
            },
            items : [{
				xtype : 'label',
				text : w0511 + "",
				width : 150,
				style : {
					fontSize : '16px'
				}
					// padding:'3 10 0 0'
				}, {
				xtype : 'form',
				border:false,
				items : [{
					xtype : 'fieldcontainer',
					defaultType : 'radio',
					layout : {
						type : 'hbox'
					},
					border : 1,
					items : [{
						xtype : 'radio',
						id : 'f_agree_' + w0501,
						boxLabel : '<span style="font-size:14px;position:relative;top:-2px;">'+record.agreetext+'</span>',
						inputValue : '1',
						disabled : expertState == '3' ? true : false,
						checked : approvalState == '1' ? true : false,
						padding : '0 20 0 0',
						listeners : {
							change : function() {
								if (!arguments[1]) {
									return;
								}
								var hiddenfieldArray = Ext
										.getCmp(arguments[0].id)
										.up('container').query('hiddenfield');
								var w0501 = hiddenfieldArray[0].getValue();
								var w0301 = hiddenfieldArray[1].getValue();
								var categories_id = hiddenfieldArray[2]
										.getValue();
								listView_me.updateCommentRs('1', w0501, w0301,
										categories_id,'0');
							}
						}
					}, {
						xtype : 'radio',
						id : 'f_against_' + w0501,
						boxLabel : '<span style="font-size:14px;position:relative;top:-2px;">'+record.disagreetext+'</span>',
						inputValue : '2',
						disabled : expertState == '3' ? true : false,
						checked : approvalState == '2' ? true : false,
						padding : '0 20 0 0',
						listeners : {
							change : function() {
								if (!arguments[1]) {
									return;
								}
								var hiddenfieldArray = Ext
										.getCmp(arguments[0].id)
										.up('container').query('hiddenfield');
								var w0501 = hiddenfieldArray[0].getValue();
								var w0301 = hiddenfieldArray[1].getValue();
								var categories_id = hiddenfieldArray[2]
										.getValue();
								listView_me.updateCommentRs('2', w0501, w0301,
										categories_id,'0');
							}
						}
					}, {
						xtype : 'radio',
						id : 'f_abstentions_' + w0501,
						boxLabel : '<span style="font-size:14px;position:relative;top:-2px;">'+record.giveuptext+'</span>',
						inputValue : '3',
						disabled : expertState == '3' ? true : false,
						checked : approvalState == '3' ? true : false,
						listeners : {
							change : function() {
								if (!arguments[1]) {
									return;
								}
								var hiddenfieldArray = Ext
										.getCmp(arguments[0].id)
										.up('container').query('hiddenfield');
								var w0501 = hiddenfieldArray[0].getValue();
								var w0301 = hiddenfieldArray[1].getValue();
								var categories_id = hiddenfieldArray[2]
										.getValue();
								listView_me.updateCommentRs('3', w0501, w0301,
										categories_id,'0');
							}
						}
					}, {
						xtype : 'hiddenfield',
						value : w0501
					}, {
						xtype : 'hiddenfield',
						value : w0301
					}, {
						xtype : 'hiddenfield',
						value : categories_id
					}]
				}]
			}]
		});
	},
	// 更新审批状态 state:是1，赞成2，反对还是3，弃权的，type：1：结束不能点击
	updateCommentRs:function(state, w0501, w0301, categories_id, type){
		var expert_state = '';
		//var items = listView_me.store.data.items;
		var items = listView_me.getNowData();
		var c_level = '';
		for(var i=0; i<items.length; i++){
			var record = items[i];
			if(record.data.w0501 == w0501 && record.data.w0301==w0301 && record.data.categories_id==categories_id){
				c_level = record.data.c_level;
				expert_state = record.data.expert_state;
				if(state == '1') {//记录下store中的approvalState，listView_me.approvalState是为了在赞成名额超出了返回给原先的approvalState赋值过去
					listView_me.approvalState = record.data.approvalState;
				}
			}
		}
		if(state == "0" || type == "1" || expert_state == '3'){//再次进来的只能看以前选择的
			return;
		}
				
		for(var i = 1; i < 4; i++) {
			if(i == 1) {
				if(i == state) {
					Ext.getDom("label_"+w0501+"_"+i).style.background = "url(/images/new_module/agree_checked.png) no-repeat";
				}else {
					Ext.getDom("label_"+w0501+"_"+i).style.background = "url(/images/new_module/agree_unchecked.png) no-repeat";
				}
			}else if(i == 2) {
				if(i == state) {
					Ext.getDom("label_"+w0501+"_"+i).style.background = "url(/images/new_module/disagree_checked.png) no-repeat";
				}else {
					Ext.getDom("label_"+w0501+"_"+i).style.background = "url(/images/new_module/disagree_unchecked.png) no-repeat";
				}
			}else if(i == 3) {
				if(i == state) {
					Ext.getDom("label_"+w0501+"_"+i).style.background = "url(/images/new_module/giveup_checked.png) no-repeat";
				}else {
					Ext.getDom("label_"+w0501+"_"+i).style.background = "url(/images/new_module/giveup_unchecked.png) no-repeat";
				}
			}
		}
		var map = new HashMap();
		map.put("type", "1");//审批状态
		map.put("state", state);
		map.put("w0501", w0501+"");
		map.put("w0301", w0301+"");
		map.put("categories_id", categories_id+"");
	    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
	    	if(state == '1'){
	    		listView_me.w0501 = w0501;
	    		listView_me.w0301 = w0301;
	    		listView_me.categories_id = categories_id;
	    		listView_me.categoriesnum_key = categories_id+'_'+c_level;
	    		
	    		listView_me.store.on('load', listView_me.checkAgreeNum, undefined, {single:true});//赞成人数校验
	    	}
	    	listView_me.store.load();
    	}},map);
	},
	//对每个分组进行校验
	checkAgreeNum :function(){
		// 差额投票校验
		var msg = '';
		var items = listView_me.store.data.items;
		//找到对应的可赞成名额
		var config_num = listView_me.categoriesnummap[listView_me.categoriesnum_key].split('_')[0];
		//列表界面不需要[2]，因为列表界面items是全部的数据，直接循环全部数据就能找到其他批次已经赞成的人数
		var config_showname = listView_me.categoriesnummap[listView_me.categoriesnum_key].split('_')[1];
		if(config_num == '')//没有设置可以任意选择
			return;
		//找出所有数据中对应的已赞成名额数量
		var num = 0;
		var agreetext = '';
		for(var i=0; i<items.length; i++){
			var record = items[i];
			var categories_id = record.data.categories_id;
			var w0511 = record.data.w0511;
			agreetext = record.data.agreetext;
			var approvalState = record.data.approvalState;
			if(listView_me.categories_id==categories_id){
				if(approvalState=='1'){
					num ++;
				}
			}
		}
		var level_name = config_showname;
		if(config_showname.indexOf('-') > -1){
			level_name = config_showname.split('-')[1];
		}
		if(num > config_num) {
			//msg += ('【'+config_showname+'】要求赞成人数'+config_num+"，您赞成人数"+num+"。<br>");
			msg += level_name+/*'（'+nameStr+'）'+*/zc.cardview.more+config_num+zc.cardview.doVote + agreetext + zc.cardview.vote;
		}
		if(!Ext.isEmpty(msg)){
			//var msgpre = '您所赞成的人数不符合要求，如下：<br>';
			if(!Ext.isEmpty(listView_me.approvalState)){
				var idpre = 'agree_';
				if(listView_me.approvalState == '1'){
					idpre = 'agree_';
				} else if(listView_me.approvalState == '2'){
					idpre = 'against_';
				} else if(listView_me.approvalState == '3'){
					idpre = 'abstentions_';
				}
				var label = Ext.get(idpre+listView_me.w0501);
				if(label){
					if(listView_me.approvalState == '1'){
						label.dom.checked = false; 
					}else{
						label.dom.checked = true; 
					}
				}
				var f_label = Ext.getCmp('f_'+idpre+listView_me.w0501);
				if(f_label){
					if(listView_me.approvalState == '1'){
						f_label.setValue(false);
					}else{
						f_label.setValue(true);
					}
				}
			}else{
				var label = Ext.get('agree_'+listView_me.w0501);
				if(label){
					label.dom.checked = false; 
				}
				var f_label = Ext.getCmp('f_'+'agree_'+listView_me.w0501);
				if(f_label){
					f_label.setValue(false);
				}
			}
			if(listView_me.approvalState == '1'){
				listView_me.approvalState = '';
			}
			listView_me.updateCommentRs(listView_me.approvalState, listView_me.w0501, listView_me.w0301, listView_me.categories_id);
			Ext.showAlert(/*msgpre + */msg);
			return ;
		}
	
	},
	//提交的时候总的校验
	checkAgreeNumForSubmit :function(){
		// 差额投票校验
		var msg = '';
		//var items = listView_me.store.data.items;
		var items = listView_me.getNowData();//优化职称，不再显示多个组了，一组一组显示，左侧可以选择组，这里找到对应的组就行
		for(var p in listView_me.categoriesnummap){
			var config_categories_id = p.split('_')[0];
			var config_c_level = p.split('_')[1];
			var config_num = listView_me.categoriesnummap[p].split('_')[0];
			var config_showname = listView_me.categoriesnummap[p].split('_')[1];
			if(msg == '' && config_num == '')
				continue;
			var nameStr = '';
			var num = 0;
			var agreetext = '';
			for(var i=0; i<items.length; i++){
				var record = items[i];
				var categories_id = record.data.categories_id;
				var c_level = record.data.c_level;
				var w0511 = record.data.w0511;
				agreetext = record.data.agreetext;
				var approvalState = record.data.approvalState;
				if(config_categories_id==categories_id && config_c_level==c_level){
					nameStr += (w0511+'，');
					if(approvalState=='1'){
						num ++;
					}
				}
			}
			if(!Ext.isEmpty(nameStr)){
				nameStr = nameStr.substring(0, nameStr.length-1);
			}
			var level_name = config_showname;
			if(config_showname.indexOf('-') > -1){
				level_name = config_showname.split('-')[1];
			}
			if(num > config_num) {
				//msg += ('【'+config_showname+'】要求赞成人数'+config_num+"，您赞成人数"+num+"。<br>");
				msg += level_name+/*'（'+nameStr+'）'+*/zc.cardview.more+config_num+zc.cardview.doVote + agreetext + zc.cardview.vote+'<br>';
			}
		}
		if(!Ext.isEmpty(msg)){
			Ext.showAlert(/*msgpre + */msg);
			return false;
		}
		return true;
	
	},
	refreshPage:function(isShowMsg,srotItemId){
		if(!Ext.isEmpty(srotItemId)) {
			this.store = this.getListViewStore();//获取store
			//刷新之前获取对应的位置，使得刷新的时候位置不变
			listView_me.scrollTop = Ext.getDom("MyTable_tableData").scrollTop;
			listView_me.scrollLeft = Ext.getDom("MyTable_tableData").scrollLeft;
			listView_me.scrollTop = Ext.getDom("MyTable_tableData").scrollTop;
		}
		if(this.checkIsHaveNewData() || !Ext.isEmpty(srotItemId)){
			listView_me.reloadPage();
    	} else {
    		if(!!isShowMsg){
    			Ext.showAlert(zc.cardview.haveNotNew);
    		}
    	}
	},
	
	reloadPage: function() {
		Ext.getDom("personListDiv").innerHTML = "";
		listView_me.store_load();
	},
	
	checkIsHaveNewData : function(){
		var isExist = false;
		
		var map = new HashMap();
		map.put("type", '2');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	isExist = result.isexist;
	    },scope:this},map);
	    
	    return isExist;
	},
	// 对页面数据进行提交
	submitResult:function(){
		
		//var items = listView_me.store.data.items;
		//优化职称，不再显示多个组了，一组一组显示，左侧可以选择组，这里找到对应的组就行
		var items = listView_me.getNowData();
		var objArr = [];
		var categories_id_submit = "";
		for(var i=0; i<items.length; i++) {
			var record = items[i];
			var w0501 = record.data.w0501;
			var w0301 = record.data.w0301;
			var queue = record.data.itemid;
			var categories_id = record.data.categories_id;
			if(queue != listView_me.showQueueMap[categories_id]) {//只提交当前组的
				continue;
			}
			var m = new HashMap();
			m.put('w0501', w0501);
			m.put('w0301', w0301);
			m.put('categories_id', categories_id);
			categories_id_submit = categories_id;
			objArr.push(m);
		}
		
		var map = new HashMap();
		map.put("type", '2');//更新“专家状态标识(expert_state)”
		map.put("state", '3');//页面已提交
		map.put("objArr", objArr);
	    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
	    	
	    	// 【保存】【提交】按钮隐藏，【刷新】显示
			var cardviewsave = Ext.getCmp('cardviewsave');
			var cardviewsubmit = Ext.getCmp('cardviewsubmit');
			//var cardviewrefresh = Ext.getCmp('cardviewrefresh');
			if(cardviewsave){
				cardviewsave.hide();
			}
			if(cardviewsubmit){
				cardviewsubmit.hide();
			}
			/*if(cardviewrefresh && listView_me.isshowrefresh){
				cardviewrefresh.show();
			}*/
			
			// 页面上的radio不可用
			var radlioArray = Ext.query('input[type=radio]');
			if(radlioArray.length > 0){
				/*for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					Ext.getDom(radio.id).disabled = true;
				}*/
				
				Ext.showAlert(zc.label.submitSuccess, function(){
					//if(!this.isshowrefresh){
					//	this.goToLogon();
					//}else{
					var resultWindow = Ext.getCmp('resultWindow');
					if(resultWindow){
						resultWindow.close();
					}
					var flag = false;
					var list_cate = listView_me.getListCate();
					//直接进入下一组了，不直接刷新界面
					for(var i = 0; i < list_cate.length; i++) {
						if(flag && !list_cate[i].isSubmit) {
							listView_me.selectGroup = list_cate[i].categories_id;
							flag = false;
							break;
						}
						if(list_cate[i].categories_id == categories_id) {
							flag = true;
						}
					}
					
					if(Ext.getDom("tipPanel_" + categories_id_submit)) {
						Ext.getDom("tipPanel_" + categories_id_submit).src = "/images/new_module/finish.png";
					}
					
					
					if(flag) {
						this.store.on('load', this.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
						this.store.load();
					}else {
						listView_me.reloadPage();
					}
						/*this.store.on('load', this.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
						this.store.load();*/
					//}
				}, listView_me);
			}
	    }},map);
		
		
	},
	goToLogon:function(){
		window.top.location.href = '../hcmlogon.html'
	},
	// 评审材料
	checkfile:function(path, nbasea0100, type, w0536){
		if(this.support_word && w0536 != 'undefined' && w0536 != 'null' && !Ext.isEmpty(w0536)){// 支持WORD模板
			var servletpath = '/servlet/DisplayOleContent?filePath='+w0536+'&bencrypt=true'+'&openflag=true';
			
			var height = this.pageHeight;
			var width = this.pageWidth;
			Ext.create('Ext.window.Window',{
		  		title:'申报材料',
		       	layout:'fit',
		        modal: true,
		        resizable: false,  
		        border:false,
		  		closeToolText : '',
		       	items:[{
		            xtype: 'panel',
		            border:false,
		           	html:'<iframe src="'+servletpath+'" width="'+(width-10)+'" height="'+(height-40)+'"></iframe>'
		        }]
			}).show();
		} else {
			if(Ext.isEmpty(path)) {
				Ext.showAlert('没有申报对应的材料！');
				return;
			}
			/** 解析path中的参数 */
			var tabid = "";
			var taskid = "";
			var taskid_validate = "";
			var index = path.indexOf("?");
			var paramStr =  path;
			if(index > -1){
				paramStr = path.substring(index+1);
			}
			var paramArray = new Array();
			paramArray = paramStr.split('&');
			for(var i=0; i<paramArray.length; i++){
				var param = paramArray[i];
				var key = param.split('=')[0];
				if(key == 'tabid'){
					tabid = param.split('=')[1];
				} else if(key == 'taskid'){
					taskid = param.split('=')[1];
				} else if(key == 'taskid_validate'){
					taskid_validate = param.split('=')[1];
				}
			}
			if(Ext.isEmpty(taskid_validate)){// 获取taskid的校验code
				var map = new HashMap();
				map.put("type", '2');
				map.put("taskid", taskid);
				Rpc({functionId:'ZC00003022',async:false,success:function(res){
					var result = Ext.decode(res.responseText);
					taskid_validate = result.taskid_validate;
				
				}},map);
			}
			// 配置参数 
			var obj={};
			obj.module_id="11";////调用模块标记：职称模块
			obj.return_flag="14";//返回模块标记：不需要返回关闭按钮
			obj.tab_id=tabid;//模板号
			obj.task_id=taskid;//任务号 除0以外需加密
			obj.approve_flag="0";//不启用审批
			obj.view_type="card";//卡片模式
			obj.card_view_type="1";//卡片模式下不要显示左边导航树
			obj.other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100+"`taskid_validate="+taskid_validate;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
			obj.callBack_init="listView_me.showView";
			//获取业务模板名称
			var map = new HashMap();
			map.put("tabId", tabid);
			map.put("getconfig", true);//获取材料公示配置信息
			map.put("type", type);//0：公示 1：聘委会 2：学科组 3：同行专家 4：二级单位
		    Rpc({functionId:'ZC00003018',async:false,success:function(){
		    	var result = Ext.decode(arguments[0].responseText);
		    	this.tabName = result.tabName;
		    	var configStr = result.configStr;
		    	if(!Ext.isEmpty(configStr)){
		    		obj.other_param += ('`noshow_pageno='+configStr)
			    }
				// 调用人事异动模板 
				createTemplateForm(obj);
		    },scope:this},map);
		}
	},
	// "公示、投票环节显示申报材料表单上传的word模板内容"参数
	getSupportWord : function(){
		var support_word = false;
		
		var map = new HashMap();
		map.put("type", '3');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	support_word = result.support_word;
	    },scope:this},map);
	    
	    return support_word;
	},
	// 显示人事异动模板
	showView:function(){
        var container = Ext.create('Ext.container.Container', {
        	region: 'center',
		    layout: 'fit',
		    border: false,
		    items: [templateMain_me.mainPanel]
		});
		var showfile_win = Ext.create('Ext.window.Window', {
				title:listView_me.tabName,
				id:'reviewfile_listview_showfile_win',
				layout: 'border',
				modal: true,
				width:listView_me.pageWidth,
				height:listView_me.pageHeight,
				border:false,
				autoScroll:false,
				closable:false,
				tools: [{
						xtype:'button',
						text:zc.label.back,
						handler:function(){
							Ext.getCmp('reviewfile_listview_showfile_win').close();
							if(Ext.util.CSS.getRule(".x-grid-cell-inner"))
			    	    		Ext.util.CSS.updateRule(".x-grid-cell-inner","max-height","");
						},
						scope:this
					}],
			    items: [container]//,
			}).show();
		//人事异动模板展示自适应
		if(showfile_win){
			window.onresize=function(){
				var height =Ext.getBody().getViewSize().height;
				var width =Ext.getBody().getViewSize().width;
				showfile_win.setWidth(width);
				showfile_win.setHeight(height);
			}
		}
	},
	//将所有双字节变成2个单字节
	getStrLength:function(str){
		
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    },
    //记下单个字符串的长度，和字符串变成双字节的长度，这样可以有效的截取中英文都存在的情况下长度截取问题
    getSubStrLength:function(str) {
    	var singleString = 0;//记下循环的字符串长度
		var doubleString = 0;//双字节，如果长度超出24的，截取单个字节的singleString长度
		for (var i = 0; i < str.length; i++) {//遍历字符串
		      if(/[^\u0000-\u00ff]/g.test(str[i])) {
		    	  doubleString = doubleString + 2;
		      }else {
		    	  doubleString++;
		      }
		      if(doubleString <= 24)
		    	  singleString++;
		      else
		    	  break;
	    }
		return singleString;
    },
    
    showTipPanel: function() {
    	if(Ext.getDom("cardTipView").style.display == "block") {
    		listView_me.changeLeftWidth(200, "none", false);
    	}else {
    		Ext.getDom("cardTipView").style.height = this.pageHeight + "px";
    		listView_me.changeLeftWidth(-200, "block", true);
    	}
    },
    //改变长度flag:true:需要慢慢张开，false：不需要这样张开，只是刷新
    changeLeftWidth: function(length, state, flag) {
    	//只有二级单位和评委会需要有分组面板
    	if(listView_me.type == 4 || listView_me.type == 1) {
    		if(!Ext.getCmp("tipPanel")) {//先创建panel面板
        		listView_me.createTipPanel();
        	}
    		
	    	Ext.getDom("cardTipView").style.display = state;
	    	listView_me.length = 0;
	    	listView_me.flag_disp = state;
	    	listView_me.last_left = Ext.getDom("hTMLLastVal_div").style.left;
	    	//投票列直接变位置，其他通过setInterval定时，显示出慢慢展开的效果
			listView_me.lastHead_left = Ext.getDom("MyTable_hTMLLastHeadVal").style.left;
			listView_me.data_width = Ext.getDom("MyTable_tableData").style.width;
			listView_me.head_width = Ext.getDom("MyTable_tableHead").style.width;
			Ext.getDom("listPanel-body").style.width = this.pageWidth + "px";
			if(flag) {
				listView_me.timer = setInterval(listView_me.run_slow, 20);
			}else {
				Ext.getDom("cardTipView").style.width = Math.abs(length) + "px";
				listView_me.changeLength(length);
			}
    	}else {
    		Ext.getDom("cardTipView").style.display = "none";
    	}
    },
    run_slow: function() {
    	//宽度慢慢加25
    	if(listView_me.flag_disp == "block")
    		listView_me.length += -25;
    	else
    		listView_me.length += 25;
    	Ext.getDom("cardTipView").style.width = Math.abs(listView_me.length) + "px";
    	listView_me.changeLength(listView_me.length);
		if(Math.abs(listView_me.length) == 200) {
			clearInterval(listView_me.timer);
		}
    },
    changeLength: function(length) {
    	Ext.getDom("hTMLLastVal_div").style.left = (Number(listView_me.last_left.substring(0,listView_me.last_left.length-2)) + length) + "px";
		Ext.getDom("MyTable_hTMLLastHeadVal").style.left = (Number(listView_me.lastHead_left.substring(0,listView_me.lastHead_left.length-2)) + length) + "px";
		Ext.getDom("MyTable_tableData").style.width = (Number(listView_me.data_width.substring(0,listView_me.data_width.length-2)) + length) + "px";
		Ext.getDom("MyTable_tableHead").style.width = (Number(listView_me.head_width.substring(0,listView_me.head_width.length-2)) + length) + "px";
		//修改table的宽度，在左侧有伸张栏的时候
		var radlioArray = Ext.query('div[id=personListDiv] table');
		for(var i=0; i<radlioArray.length; i++){
			var radio = radlioArray[i];
			if(radio.style.width!='') {
				radio.style.width = (Number(listView_me.head_width.substring(0,listView_me.head_width.length-2)) + length) + "px";
			}
		}
		if(Math.abs(length) == 200) {
			if(listView_me.flag_disp == "none"){
				Ext.getDom('listviewsubmit').style.left = ((this.pageWidth)/2-140)+ "px";
			}else {
				Ext.getDom('listviewsubmit').style.left = ((this.pageWidth)/2-240)+ "px";
			}
		}
    },
    
 // 打开鉴定意见
	questionnaire:function(type, w0539, w0541, w0539_qnid, w0541_qnid, w0501, w0301, expertState, expertName, subObject, categories_id){
		
		/**
		 * 关键参数说明：
		 * type:专家类型 1：评委会专家 2：学科组 3：外部鉴定专家
		 * expertState：专家状态标识 0|1|null|''：待审 2：已审 3：已提交
		 * w0539:内部评审问卷计划号：聘委会、学科组专家用
		 * w0541:专家鉴定问卷计划号：外部鉴定专家用
		 * w0539_qnid：问卷计划号对应的问卷号
		 * w0541_qnid：问卷计划号对应的问卷号
		 * chent
		 **/
		
		// 如果专家类型都没有，直接不让答题
		if(type == "" || type == null){
			Ext.showAlert(zc.cardview.notDraft);
			return ;
		}
		
		listView_me.w0501 = w0501;// 记录当前操作时的申请人，答完卷时回调用
		listView_me.w0301 = w0301;// 记录当前操作时的申请人的会议，答完卷时回调用
		listView_me.categories_id_current_ques = categories_id;
		// 配置问卷的信息
		var suerveyid = "";
		var qnId = "";
		var title = zc.cardview.checkView;
		if(type == "1" || type == "2" || type == "4"){// 评委会、学科组、学院任聘组的问卷配置
			if(w0539 == "" || w0539 == null){
				Ext.showAlert(zc.cardview.notDraft);
				return ;
			}
			suerveyid = w0539;
			qnId = w0539_qnid;
			title = zc.cardview.reviewView;
		} else {// 外部鉴定专家问卷配置
			if(w0541 == "" || w0541 == null){
				Ext.showAlert(zc.cardview.notDraft);
				return ;
			}
			suerveyid = w0541;
			qnId = w0541_qnid;
		}
		if(expertState == "2" || expertState == "3"){//已审
			suerveyid = undefined;//已审不需要配置此项
			
		}else if(Ext.isEmpty(expertState) || expertState == "1" || expertState == "0"){//待审
			qnId = undefined;//待审不需要配置此项
		}
		// 请求问卷
		Ext.require("QuestionnaireTemplate.PreviewTemplate",function(){
			var re = Ext.create("QuestionnaireTemplate.PreviewTemplate",{
				//height:757,
				panwidth:listView_me.pageWidth-100,
				border:true,
				qnId:qnId,
				suerveyid:suerveyid,
				mainObject:expertName,//调研对象唯一标志
				subObject:subObject,//被调研对象唯一标志
				callback:listView_me.questionRs
			});
			var qnWin = Ext.create('Ext.window.Window', {
				id : 'qnWin',
			    title: title,
				modal: true,
				width:listView_me.pageWidth,
				height:listView_me.pageHeight,
				border:false,
				autoScroll:true,
				closeAction:'destroy',
				bodyStyle:'background-color:#ffffff;',
				draggable:false,
				scrollable:'y',
			    layout:{
			    	type: 'vbox',
                    align: 'center'
			    },
			    items: re//,
			}).show();
		});
	},
	// 调查问卷回调，更新专家状态标识
	questionRs:function(state,forwardFlag){
		if(state == "" || state == "0"){
		    Ext.getCmp('qnWin').close();
			return ;
		} else if(state == "1"){//交卷
			var map = new HashMap();
			map.put("type", "2");//专家状态标识
			map.put("state", "2");
			map.put("w0501", listView_me.w0501);
			map.put("w0301", listView_me.w0301);
			map.put("categories_id", listView_me.categories_id_current_ques);
		    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
		    	
		    	listView_me.store.on('load', function(){
					
					// 未评=》已评
					var span = Ext.getDom('span_'+listView_me.w0501);
					if(span){
						span.innerHTML = zc.cardview.assess;
					}
					// 更新问卷链接
					var ques = Ext.get('ques_'+listView_me.w0501);
					if(ques){
						var personinfo = this.data.items;
						var info = '';
						for(var i=0; i<personinfo.length; i++){
							var data = personinfo[i].data;
							if(data.w0501 == listView_me.w0501){
								info = data;
							}
						}
						var type= info.type;
						var w0539= info.w0539;
						var w0541= info.w0541;
						var w0539_qnid= info.w0539_qnid;
						var w0541_qnid= info.w0541_qnid;
						var w0501= info.w0501;
						var w0301= info.w0301;
						var expertState= info.expert_state;
						var expertName= info.expertName;
						var subObject= info.subObject;
						var href = "javascript:listView_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301+"','"+expertState+"','"+expertName+"','"+subObject+"','"+info.categories_id+"')";
						ques.dom.href = href; 
					}
				},undefined,{single:true});
				
				listView_me.store.load();
				if('true'==forwardFlag)
					Ext.getCmp('qnWin').close();
				return ;
		    }},map);
		}
	}
    
});