/**
 * 自定义审批流程 cuibl 20170807
 * 
 */

Ext.define('SelfDefineFlowUL.SelfDefineFlow', {
	constructor : function(config) {
		selfDefineFlow_me = this;
		selfDefineFlow_me.flowId = [];//保存审批人的数组  格式为list[list[map]]
		selfDefineFlow_me.reportId = [];//保存报备人的id   查询人员库时过滤的人员id
		selfDefineFlow_me.reportArr = [];//查询人员库时过滤的人员id

		selfDefineFlow_me.rowIndex = 1;//动态生成行的唯一标示
		selfDefineFlow_me.reportIndex = 0;//防止报备层id重复
		selfDefineFlow_me.reportFlag = '1';//动态增加或删除报备行的下边框的标示
		
		selfDefineFlow_me.tabid = config.tabid;
		selfDefineFlow_me.orgid = config.orgid;
		selfDefineFlow_me.createSelfCss();//动态引入,复写样式
		selfDefineFlow_me.init();//初始化界面
		selfDefineFlow_me.initFlow();//初始化数据
	},
	/**
	 * 初始化界面
	 */
	init : function() {
		var flowPanel = selfDefineFlow_me.createRow('1');
		var reportPanel = selfDefineFlow_me.createRow('2');
	    var win = Ext.widget("window", {
			id:'flowWin',
			title : '自定义审批流程',
			height : document.body.clientHeight*0.8,
			width : 800,
			minButtonWidth : 40,
			layout : 'fit',
			bodyStyle : 'background:#ffffff;',
			modal : true,
			resizable : false,
			closeAction:'destroy',
			items : [{xtype:'panel',id:'winPanel',scrollable:true,items:[flowPanel,reportPanel]}],
			buttons : [ {
				xtype : 'tbfill'
			}, {
				text : '确定',// 关闭
				handler : function() {
					var map = new HashMap();
					map.put("flowId",selfDefineFlow_me.flowId);
					map.put("reportId",selfDefineFlow_me.reportId);
					map.put("tabid",selfDefineFlow_me.tabid);
					map.put("oprflag","saveFlow");
				    Rpc({
				    	functionId:'MB00002028',
				    	async:false,
				    	success: function(){
				    			Ext.getCmp('flowWin').destroy();
				    			Ext.util.CSS.removeStyleSheet("delImg");//自定义样式回收
				    		}
				    	},map);
				}
			}, {
				text : '关闭',// 关闭
				handler : function() {
					Ext.util.CSS.removeStyleSheet("delImg");
					win.destroy();
				}
			}, {
				xtype : 'tbfill'
			} ]
		});
		win.show();
	},
	/**
	 * 初始化数据
	 */
	initFlow : function() {
		var map = new HashMap();
		map.put("oprflag","initFlow");
		map.put("tabid",selfDefineFlow_me.tabid);
	    Rpc({
	    	functionId:'MB00002028',
	    	async:false,
	    	success: function(form){
	    			var result = Ext.decode(form.responseText);
	    			var listFlow = result.listFlow;
	    			var listReport = result.listReport;
	    			selfDefineFlow_me.deprecate = result.deprecate;
	    			//初始化审批人
	    			if(listFlow.length>0){
    	    			for(var i=0;i<listFlow.length;i++){
    	    				var personListP = Ext.getCmp('winPanel').items.items[i].items.items[1];//遍历得到存审批人的每个层级
    	    				selfDefineFlow_me.addRow(personListP);
    	    				var tempList = listFlow[i];//层级
    	    				var addApproves = [];//每个层级中人员的items
    	    				for(var j=0;j<tempList.length;j++){
    	    					var id = tempList[j].actorid;
        						var name = tempList[j].actorname;
        						var imgSrc = tempList[j].imgSrc;
        						var personPanel = selfDefineFlow_me.createPersonP(id,name,imgSrc,'1');
    	    					addApproves.push(personPanel);
    	    					var num = personListP.pId.split('_')[1];
    	    					var tempName = getEncodeStr(name);//加密后的name
        						if(selfDefineFlow_me.flowId.length < num){//判断是否为新添加的审批
        							var map = new HashMap();
        							map.put("id",id);
        							map.put("name",tempName);
        							var tempArr = [map];
        							selfDefineFlow_me.flowId.push(tempArr);
        						}else{
        							var tempArr = selfDefineFlow_me.flowId[num-1];//从数组中获取对应的值,当前层级数组
        							var tempMap = new HashMap();
        							tempMap.put("id",id);
        							tempMap.put("name",tempName);
        							selfDefineFlow_me.flowId[num-1].push(tempMap);
        						}
    	    				}
    	    				personListP.insert(personListP.items.length-1,addApproves);//将新添加的审批人的集合插入到该审批层级
    	    				selfDefineFlow_me.rowHeight(personListP);//改变行panel的高
    	    			}
	    			}
	    			//初始化报备人
	    			if(listReport.length>0){
	    				var reportListP = Ext.getCmp('reportPanel'+selfDefineFlow_me.reportIndex).items.items[1];
	    				var addReports = [];
	    				for(var i2=0; i2<listReport.length; i2++){
	    					var id = listReport[i2].actorid;
	    					var name = listReport[i2].actorname;
	    					var imgSrc = listReport[i2].imgSrc;
	    					var personPanel = selfDefineFlow_me.createPersonP(id,name,imgSrc,'2');
	    					addReports.push(personPanel);
	    					var tempName = getEncodeStr(name);//加密后的name
	    					selfDefineFlow_me.reportArr.push(id);//将添加对象的id放入数组,再次添加时不会被查询
	    					var tempMap = new HashMap();
	    					tempMap.put("id",id);
	    					tempMap.put("name",tempName);
							selfDefineFlow_me.reportId.push(tempMap);//将添加对象放入数组
	    				}
	    				reportListP.insert(reportListP.items.length-1,addReports);//将新添加的审批人的集合插入到该审批层级
	    				selfDefineFlow_me.rowHeight(reportListP);//改变行panel的高
	    				//判断是否删除报备人的border-bottom
						var winPanel = Ext.getCmp('winPanel');
						selfDefineFlow_me.bottomLine(winPanel);
	    			}
	    		}
	    	},map);
	},
	/**
	 * 创建行panel并返回
	 */
	createRow : function (flag){
		var rowText = '';
		if(flag=='1'){
			rowText = selfDefineFlow_me.transLevel(selfDefineFlow_me.rowIndex);
		}else{
			rowText = '报备';
		}
		var rowTextP = Ext.widget('panel',{
			layout: {
			    type: 'hbox',
			    pack: 'center',
			    align:'middle'
			},
			height : 90,
			width : 150,
			border : false,
			items:[{xtype:'label',text:rowText}]
		});
		var rowPanel = '';
		if(flag=='1'){
			var personListP = Ext.widget('panel', {
				pId : 'personListP_'+selfDefineFlow_me.rowIndex,
				layout:{type:'table',tdAttrs:{width:75},columns: 8},
				//scrollable:true,
				bodyStyle: 'border-top:none;border-bottom:none;border-right:none;',
				height : 90,
				width : 640,
				items:[{xtype:'label',margin:'0 0 0 10',html:'<a href="javascript:void(0)" style=" line-height:90px;white-space: nowrap;" >添加审核人</a>', listeners:{
					render:function(){
							var curPer = this.ownerCt;
							this.getEl().on('click',function(){
								selfDefineFlow_me.addFlow(curPer);
		          			});
						}
					}
				}]
			});
			rowPanel = Ext.widget('panel', {
				fId : "flowPanel_"+selfDefineFlow_me.rowIndex,
				height : 90,
				layout : 'hbox',
				bodyStyle: 'border-top:none;border-left:none;border-right:none;',
				items : [rowTextP,personListP]
			});
		}else{
			var reportListP = Ext.widget('panel', {
				layout:{type:'table',tdAttrs:{width:75},columns: 8},
				//scrollable:true,
				bodyStyle: 'border-top:none;border-bottom:none;border-right:none;',
				height : 90,
				width : 640,
				items:[{xtype:'label',margin:'0 0 0 10',html:'<a href="javascript:void(0)" style=" line-height:90px;white-space: nowrap;" >添加报备人</a>', listeners:{
					render:function(){
							var curPer = this.ownerCt;
							this.getEl().on('click',function(){
								selfDefineFlow_me.addReport(curPer);
		          			});
						}
					}
				}]
			});
			rowPanel = Ext.widget('panel', {
				id: 'reportPanel'+(++selfDefineFlow_me.reportIndex),
				bodyStyle: 'border-top:none;border-left:none;border-right:none;',
				height : 90,
				layout : 'hbox',
				items : [rowTextP,reportListP]
			});
		}
		return rowPanel;
	},
	/**
	 * 创建选中人的panel并返回该panel对象
	 */
	createPersonP : function (id,name,imgSrc,flag){
		var backgroundImage = "";
		if(imgSrc.indexOf("photo.jpg")!=-1){//人员没有图片的情况
			backgroundImage = 'url('+imgSrc+');background-size:50px 50px;';
			if(Ext.isIE)
				backgroundImage = 'url('+imgSrc+');background-size:cover;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='+imgSrc+',  sizingMethod="scale");';
		}else{
			imgSrc = imgSrc+'&imageResize='+$URL.encode("55`55");
			imgSrc = imgSrc+'&fromjavafolder=true';
			backgroundImage = 'url('+imgSrc+');background-size:50px 50px;';
		}
		var delImg = Ext.create('Ext.Img', {
			src: "/workplan/image/remove.png",
			style:'cursor:pointer;',
			width:20,
			height:20,
			hidden:true,
			cls:'delImg',//定位样式、该样式在下面手动引入
			listeners:{
				render:function(){
					var curPer = this.ownerCt;
					this.getEl().on('click',function(){
						if(flag == '1'){
							selfDefineFlow_me.delApprove(curPer,curPer.ownerCt);//flag为1,绑定删除审批方法
						}else if(flag == '2'){
							selfDefineFlow_me.delReport(curPer,curPer.ownerCt);//flag为2,绑定删除报备方法
						}
					});
				}
			}
		});
		var tempName = name;
		if(name.length>5){//如果名字的长度超过5个,将超过的字用..替代
			tempName = name.substring(0,5)+'...';
		}
		var personImg = Ext.create('Ext.Img',{
			margin:'15 0 0 0',
			title:name,
			style:{
				backgroundImage:backgroundImage
			},
			src:'/module/system/personalsoduku/images/55.png',
		    width:50,
		    height:50
		});
		var personPanel = Ext.widget('panel',{
			height:90,
			width:75,
			pId:id,
		    layout:{
		        type:'vbox',//竖直布局、保证图片文字上下罗列
		        align:'center'//文字居中
		    },
		    border:false,
		    items:[personImg, {xtype: 'label',text: tempName}, delImg],
		    listeners: {
		    	render:function(){
		    		var del = this.items.items[2];//获取删除图标对象
          			this.getEl().on('mouseover',function(){
          				del.show();
          			});
          			this.getEl().on('mouseout',function(){
          				del.hide();
          			});
          		}
		    }
		});
		return personPanel;
	},
	/**
	 * 改变行panel的height
	 */
	rowHeight : function(panel){
		if(panel.items != null){//如果删除当前审批人为是最后一个,跳出方法,不进行下面的操作
			var len = panel.items.length;
			var heightNum = len%8==0?len/8:Math.ceil(len/8);//行panel中有几行数据
			var parentPanel = panel.ownerCt;//获取父panel
			parentPanel.setHeight(heightNum * 90);//动态设置父panel的高
			for(var i=0;i<parentPanel.items.length;i++){
				parentPanel.items.items[i].setHeight(heightNum * 90);//设置行panel所有子panel的高
			}
		}
	},
	/**
	 * 添加审批人
	 */
	addFlow : function (personListP){
		var num = personListP.pId.split('_')[1];
		var tempFlowLevel = [];
		var temp = selfDefineFlow_me.flowId[num-1];
		if(temp != null && temp.length>0){
			for(var i=0;i<temp.length;i++){
				tempFlowLevel.push(temp[i].id);
			}
		}
		if(num==1){//一级审批人
			//判断当前登录人是否在排除的人员中
			if(!selfDefineFlow_me.isExistInDepre(tempFlowLevel))//不存在当前登录人
				tempFlowLevel.push(selfDefineFlow_me.deprecate);
		}
		//调用选人控件
		var p = new PersonPicker({
			multiple: true,//为true可以多选
			isPrivExpression:false,//是否启用人员范围（含高级条件）
			validateSsLOGIN:false,//是否启用认证库
			selectByNbase:true,//是否按不同人员库显示
			//orgid:selfDefineFlow_me.orgId,//暂时不使用人员范围
			deprecate: tempFlowLevel,
			text: "确定",
			callback: function (cm) {
				if(cm.length>0){
					selfDefineFlow_me.addRow(personListP);//判断是否应该添加一行新的row
					var addApproves = [];//添加审批人的集合
					for(var i=0;i<cm.length;i++){
						var c = cm[i];
						var personPanel = selfDefineFlow_me.createPersonP(c.id,c.name,c.photo,'1');
						addApproves.push(personPanel);
						//下面是数据集合的操作
						var tempName = getEncodeStr(c.name);//加密后的name
						if(selfDefineFlow_me.flowId.length < num){//判断是否为新添加的审批层级
							var map = new HashMap();
							map.put("id",c.id);
							map.put("name",tempName);
							var tempArr = [map];
							selfDefineFlow_me.flowId.push(tempArr);
						}else{
							var tempArr = selfDefineFlow_me.flowId[num-1];//从数组中获取对应的值,当前层级数组
							var map = new HashMap();
							map.put("id",c.id);
							map.put("name",tempName);
							selfDefineFlow_me.flowId[num-1].push(map);
						}
					}
					personListP.insert(personListP.items.length-1,addApproves);//将新添加的审批人的集合插入到该审批层级
					selfDefineFlow_me.rowHeight(personListP);//改变行panel的高
					//判断是否删除报备人的border-bottom
					var winPanel = Ext.getCmp('winPanel');
					selfDefineFlow_me.bottomLine(winPanel);
				}
			}
		}, personListP);
		p.open();
	},
	/**
	 * 添加报备人
	 */
	addReport : function (reportListP){
		//调用选人控件
		var p = new PersonPicker({
			multiple: true,//为true可以多选
			isPrivExpression:false,//是否启用人员范围（含高级条件）
			validateSsLOGIN:false,//是否启用认证库
			selectByNbase:true,//是否按不同人员库显示
			deprecate: selfDefineFlow_me.reportArr,
			text: "确定",
			callback: function (cm) {
				if(cm.length>0){
					var addReports = [];//添加报备人的集合
					for(var i=0;i<cm.length;i++){
						var c = cm[i];
						var personPanel = selfDefineFlow_me.createPersonP(c.id,c.name,c.photo,'2');
						addReports.push(personPanel);
						//下面是数据集合的操作
						var tempName = getEncodeStr(c.name);//加密后的name
						selfDefineFlow_me.reportArr.push(c.id);//将添加对象的id放入数组,再次添加时不会被查询
						var map = new HashMap();
						map.put("id",c.id);
						map.put("name",tempName);
						selfDefineFlow_me.reportId.push(map);;//将添加对象放入数组
					}
					reportListP.insert(reportListP.items.length-1,addReports);//将新添加的报备人的集合插入
					selfDefineFlow_me.rowHeight(reportListP);//改变行panel的高
					//判断是否删除报备人的border-bottom
					var winPanel = Ext.getCmp('winPanel');
					selfDefineFlow_me.bottomLine(winPanel);
				}
			}
		}, reportListP);
		p.open();
	},
	/**
	 删除审批人
	*/
	delApprove : function(curPer,personListP){//curPer:当前panel,personListP:当前panel所在的panel
		Ext.showConfirm('是否删除当前审批人?',
				function(btn){
					if(btn=='yes'){
						personListP.remove(curPer);
						var num = personListP.pId.split('_')[1];
						var id = curPer.pId;
						//存储用户的数组发生变化
						var tempArr = selfDefineFlow_me.flowId[num-1];//从数组中获取对应的值,当前层级数组
						var temp = [];//定义临时变量,存储删除完后的数组
						for ( var int = 0; int < tempArr.length; int++) {
							if(id != tempArr[int].id){
								temp.push(tempArr[int]);
							}
						}
						selfDefineFlow_me.flowId[num-1] = temp;//替换数组
						if(personListP.items.length == 1){//如果td中只含有最后一个a标签,对该row进行删除
							selfDefineFlow_me.delRow(personListP.ownerCt);
						}
						selfDefineFlow_me.rowHeight(personListP);//改变行panel的高
						//判断是否删除报备人的border-bottom
						var winPanel = Ext.getCmp('winPanel');
						selfDefineFlow_me.bottomLine(winPanel);
					}
		});
	},
	/**
	 删除报备人
	*/
	delReport : function(curPer,reportListP){//curPer:当前panel,reportListP:当前panel所在的panel
		Ext.showConfirm('是否删除当前报备人?',
				function(btn){
					if(btn=='yes'){
						reportListP.remove(curPer);
						var id = curPer.pId;
						//选人控件所用的数组
						var tArr = [];
						for ( var int = 0; int < selfDefineFlow_me.reportArr.length; int++) {
							if(id != selfDefineFlow_me.reportArr[int]){
								tArr.push(selfDefineFlow_me.reportId[int].id);
							}
						}
						selfDefineFlow_me.reportArr = tArr;
						var temp = [];//定义临时变量
						for ( var int = 0; int < selfDefineFlow_me.reportId.length; int++) {
							if(id != selfDefineFlow_me.reportId[int].id){
								temp.push(selfDefineFlow_me.reportId[int]);
							}
						}
						selfDefineFlow_me.reportId = temp;
						selfDefineFlow_me.rowHeight(reportListP);//改变行panel的高
						//判断是否删除报备人的border-bottom
						var winPanel = Ext.getCmp('winPanel');
						selfDefineFlow_me.bottomLine(winPanel);
					}
		});
	},
	/**
	  动态删除row
	 */
	delRow : function(flowPanel){
		var winPanel = flowPanel.ownerCt;
		var itemLength = winPanel.items.length;
		if(itemLength > 2){
			winPanel.remove(flowPanel);
			selfDefineFlow_me.rowIndex = 1;
			for (var i = 1; i<winPanel.items.length; i++) {
				winPanel.items.items[i-1].items.items[0].items.items[0].setText(selfDefineFlow_me.transLevel(i));//动态遍历几级层级
				winPanel.items.items[i-1].items.items[1].pId="personListP_"+i;//动态修改pid
				winPanel.items.items[i-1].fId="flowPanel_"+i;
				if (i != 1){
					selfDefineFlow_me.rowIndex++;
				}
			}
			var temp = [];
			for( var i = 0; i < selfDefineFlow_me.flowId.length; i++){
				var tempArr = selfDefineFlow_me.flowId[i];
				if(tempArr.length > 0){
					temp.push(tempArr);
				}
			}
			selfDefineFlow_me.flowId = temp;
			//判断是否删除报备人的border-bottom
			selfDefineFlow_me.bottomLine(winPanel);
		}
	},
	/**
	 * 判断是否创建新的审批行panel
	 */
	addRow : function(personListP){
		if(personListP.items.length==1 && selfDefineFlow_me.rowIndex==personListP.pId.split('_')[1]){
			selfDefineFlow_me.rowIndex++;
			var flowPanel = selfDefineFlow_me.createRow('1');
			var winPanel = Ext.getCmp('winPanel');
			winPanel.insert(winPanel.items.length-1,flowPanel);
			//动态判断是否删除报备行的border-bottom
			selfDefineFlow_me.bottomLine(winPanel);
		}
	},
	/**
	 * 动态增加或删除报备行的border-bottom
	 */
	bottomLine : function(winPanel){
		var flag = selfDefineFlow_me.reportFlag;
		var totalHeight = 0;//行panel的总高度
		for(var i = 0; i<winPanel.items.length; i++){
			totalHeight += winPanel.items.items[i].getHeight();
		}
		if(totalHeight>winPanel.getHeight()){//如果行panel的总高度大于winPanel的总高度,报备的border-bottom删除
			selfDefineFlow_me.reportFlag = '2';
			if(flag == '2'){
				return;
			}
		}else{
			selfDefineFlow_me.reportFlag = '1';
			if(flag == '1'){
				return;
			}
		}
		var reportPanel = Ext.getCmp('reportPanel'+selfDefineFlow_me.reportIndex);
		var reportItems = reportPanel.items.items;
		var height = reportPanel.getHeight();//获取panel之前的高度
		if(selfDefineFlow_me.reportFlag == '1'){
			borderStyle = 'border-top:none;border-left:none;border-right:none;';
		}else{
			borderStyle = 'border-top:none;border-bottom:none;border-left:none;border-right:none;';
		}
		var rowPanel = Ext.widget('panel', {
			id : 'reportPanel'+(++selfDefineFlow_me.reportIndex),
			bodyStyle: borderStyle,
			height : height,
			layout : 'hbox',
			items : reportItems
		});
		winPanel.remove(reportPanel);
		winPanel.insert(winPanel.items.length,rowPanel);
	},
	/**
	 * 将数字转换为中文层级
	 */
	transLevel : function(level){
		var num = ["零","一","二","三","四","五","六","七","八","九"];
		var unit = ["","十","百","千","万","亿"];
		var length = level+"";
		var output = "";
		//转换数字
	    for (var i=0; i<length.length; i++) {
	            output+=num[length.charAt(i)];
	    }
	    var str="";
	    var result="";
	    for (var i=0; i<output.length; i++) {
	            if (output.length-i-1==0) {
	                    str=""+output.charAt(i);
	            }
	            else if ((output.length-i-1+4)%8==0) {
	                    str=output.charAt(i)+unit[4];
	            }
	            else if ((output.length-i-1)%8==0) {
	                    str=output.charAt(i)+unit[5];
	            }
	            else {
	                    str=output.charAt(i)+unit[(output.length-i-1)%4];
	            }
	            result+=str;
	    }
	    //格式化成中文习惯表达
	    result=result.replace(/零[千百十]/, '零');
	    result=result.replace(/亿零+万/, '亿零');
	    result=result.replace(/万零+亿/, '万亿');
	    result=result.replace(/零+/, '零');
	    result=result.replace(/零万/, '万');
	    result=result.replace(/零亿/, '亿');
	    result=result.replace(/^一十/, '十');
	    result=result.replace(/零$/, '');
	    return result+"级审批";
	},
	/**
	 * 复写样式，不影响总体Css
	 */
	createSelfCss : function(){
		Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:47px !important; top:7px !important; }","delImg");
	},
	/**
	 * 判断当前登录人是否在排除的人员中
	 */
	isExistInDepre:function(tempFlowLevel){
		 for(var i = 0; i < tempFlowLevel.length; i++){
	         if(selfDefineFlow_me.deprecate == tempFlowLevel[i]){
	            return true;
	         }
		 }
		 return false;
	}
});
