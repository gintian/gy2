Ext.define("EHR.homewidget.ServiceHall",{
	extend:'Ext.panel.Panel',
	xtype:'servicehall',
	//标题添加图片 wangb 20170815 30291
	title:'<div style="font-size:14px"><div style="float:left;width:16px;height:16px;margin:0px 3px 0px 1px;background:url(../../images/hcm/themes/default/icon/icon_business.png) no-repeat -16px 0px;"></div>服务大厅</div>',
	minHeight:100,
	collapsible:true,
	id:"serviceHallId",
	titleCollapse:true,
	style:'background:white',
	margin:'10 10 0 10',
	layout:{
		type:'vbox',
		align:'center'
	},
	initComponent:function(){
		this.loadTemplates();
		this.callParent();
	},
	//加载数据
	loadTemplates:function(){
		Rpc({functionId:'ZJ100000166',async:true,success:this.zuzhuangTemp,scope:this},new HashMap());
	},
	//生成服务大厅
	zuzhuangTemp:function(res){
		var errormessage = Ext.decode(res.responseText).errormessage;
		if(errormessage){
			this.destroy();
		}
		var mergeList = Ext.decode(res.responseText).mergeList;
		if(!mergeList||mergeList.length<=0){
			this.destroy();
			return;
		}
		/*
			服务大厅加载慢，规则更改：
			1.第一个分类个数超过10个，只加载第一个分类数据
			2.当个数为第10个且不是最后一个时，该分类都不加载
			点击展开，加载全部分类
		*/
		
		
		if(mergeList.length<2){
			this.add(this.generateMergeTemp(mergeList));
			return;
		}
		
		if(mergeList[0].temps.length >= 10){
			this.add(this.generateMergeTemp([mergeList[0]]));
			this.add(this.spreadMergeTemp(mergeList));
			return;
		}
		
		var tempCount = 0;//当前个数
		var index = 0;//加载分类的下标 
		for(var i = 0 ; i < mergeList.length ; i ++){
			tempCount += mergeList[i].temps.length ;
			if(tempCount == 10){
				index = i;
				break;
			}
			if(tempCount > 10){
				index = i - 1;
				break;
			}
		}
		if(!index){//分类总个数没有达到10个 32004 wangb 20171009
			this.add(this.generateMergeTemp(mergeList));
			return;
		}
		var firstList = [];
		for(var i = 0 ; i <= index ; i++){
			firstList.push(mergeList[i]);
		}
		
		this.add(this.generateMergeTemp(firstList));
		this.add(this.spreadMergeTemp(mergeList));
	},
	// 生成合并的单个分类模板区域
	generateMergeTemp : function(mergeList) {
		if (!mergeList || mergeList.length <= 0)
			return "请添加模版";
		var html = "";
		for (var o = 0; o < mergeList.length; o++) {
			var mergeLittleObject = mergeList[o];
			if (!mergeLittleObject.temps || (mergeLittleObject.temps).length <= 0) continue;

			html += "<div style=\"float:left\">";
			html += "<div style=\"font-size:15px;width:144px;color:black;margin-left:11px;margin-top:5px;margin-bottom:5px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;\" title='"+mergeLittleObject.name+"'>"
					+ mergeLittleObject.name + "</div>";
			for (var p in mergeLittleObject.temps) {
			
				var type = (mergeLittleObject.temps)[p].type;
				var tabname = (mergeLittleObject.temps)[p].tabname; // 模板名称
				tabname = tabname.replace(/(\n)/g, "");    
				tabname = tabname.replace(/(\t)/g, "");    
				tabname = tabname.replace(/(\r)/g, "");    
				tabname = tabname.replace(/<\/?[^>]*>/g, "");    
				tabname = tabname.replace(/\s*/g, "");  
				var tabid = (mergeLittleObject.temps)[p].tabid; // 模板ID
				var icon = (mergeLittleObject.temps)[p].icon; // 图标
				if (!icon)
					icon = "fuwudating.png";
				icon = "../../components/homewidget/images/serviceicon/" + icon;
				if(type==2){
					html += "<div servicetype='2' tab_id=\""+ tabid+ "\" id='"+ tabid+ "' style=\"float:left;cursor:pointer;padding-top:20px;background:#FFFFFF;float:left;height:75px;width:145px;margin-left:10px;margin-bottom:10px;\">";
					html += "<img src=\"" + icon + "\" tab_id=\""+ tabid+ "\" width='36' height='36' servicetype='2' tab_id=\""+ tabid+ "\" style='float:left;margin-left:10px;'>";
					html += "<div servicetype='2' tab_id=\""+ tabid+ "\" style=\"font-size:14px;float:left;margin-left:5px;width:83px;height:35px;line-height:17px;overflow:hidden;\" title=\""+tabname+"\">"+ tabname + "</div>";
					html += "</div>";
					continue;
				}
				
				if(type==3){
					html += "<a href='"+(mergeLittleObject.temps)[p].linkurl+"' target='_self' style='color:black;'>";
					html += "<div id='service-3' roleid='service-3' style=\"float:left;cursor:pointer;padding-top:20px;background:#FFFFFF;float:left;height:75px;width:145px;margin-left:10px;margin-bottom:10px;\">";
					html += "<div roleid='service-3' style=\"text-align:right;float:left;height:55px;width:42px;\">";
					html += "<img roleid='service-3' src=\"" + icon + "\" width='36' height='36' >";
					html += "</div>";
					html += "<div roleid='service-3' style=\"font-size:14px;float:left;margin-left:5px;width:83px;height:35px;line-height:17px;overflow:hidden;\" title=\""+tabname+"\">"+ tabname + "</div>";
					html += "</div>";
					html += "</a>";
					continue;
				}
			
				var rejectingTasks = (mergeLittleObject.temps)[p].rejectingTasks;// 驳回的任务号
				var applyedNum = (mergeLittleObject.temps)[p].applyedNum;// 申请单据数
				var ins_id = (mergeLittleObject.temps)[p].ins_id;// 最近一次申报的实例ID
				var task_id = (mergeLittleObject.temps)[p].task_id;// 最近一次申报的加密任務ID
				
				html += "<div id='"+ tabid+ "' servicetype='1' role='tabService' tabname=\""+ tabname+ "\" tab_id=\""+ tabid+ "\" type=\"jumpurl1\" style=\"float:left;cursor:pointer;background:#FFFFFF;float:left;height:75px;width:145px;margin-left:10px;margin-bottom:10px;\">";
				html += "<div id=\"temp_"+ tabid+ "\" tabname=\""+ tabname+ "\" tab_id=\""+ tabid+ "\" type=\"jumpurl2\" style=\"padding-top:5px;padding-right:5px;height:20px;width:145px;\">";
				if (applyedNum !== '0' && applyedNum != 0) {
/*					html += "<div id=\"historydataId\""+tabid+"  type=\"historydata\" tabname=\""+ tabname+ "\" tab_id=\""+ tabid+ "\" taskid=\""+ applyedNum
						 + "\" title=\"我的申请\" style=\"font-size:10px;float:right;color:#ffffff;text-align:center;width:16px;height:16px;background:url('/components/homewidget/images/serviceicon/historydata.png')\"></div>";*/
				}
				html += "</div>";
				html += "<div tabname=\""+ tabname+ "\" tab_id=\""+ tabid+ "\" type=\"jumpurl3\" style=\"text-align:right;float:left;height:55px;width:42px;\">";
				html += "<img src=\"" + icon + "\" width='36' height='36' tab_id=\""+ tabid+ "\">";
				html += "</div>";
				html += "<div class=\"clear:both\"></div>";
				html += "<div title=\"" + tabname + "\" tabname=\"" + tabname+ "\" tab_id=\"" + tabid+ "\" type=\"jumpurl4\" rejectingTasks="+ rejectingTasks + " applyedNum=" + applyedNum
						+ " ins_id=" + ins_id + " task_id=" + task_id+ " style=\"float:left;height:55px;width:98px;\">";
				html += "<div  id=\"template_"+tabid+"\"  tabname=\""+ tabname+ "\" tab_id=\""+ tabid+ "\" type=\"jumpurl\" rejectingTasks=\""+ rejectingTasks + "\" applyedNum='" + applyedNum + "' ins_id=\"" + ins_id + "\" task_id=\"" + task_id
				     + "\" style=\"font-size:14px;margin-left:5px;width:83px;height:35px;line-height:17px;overflow:hidden;\" title=\""+tabname+"\">"+ tabname + "</div>";
				html +="<div id=\"templ_"+tabid+"\">"
				if (rejectingTasks)
					html += "<div type=\"tododata\" tabname=\""+ tabname+ "\" tab_id=\""+ tabid+ "\"  style='color:red;float:right;font-size:15px;'>退回</div>";
				html +="</div>";
				html += "</div>";
				html += "</div>";
			}
			html += "</div>";
		}
		var items = {
			xtype : 'component',
			width : "99%",
			html : html,
			listeners : {
				click : this.tempTap,
				element : 'el',
				scope : this,
				mouseover:function(evt,menuTable){
					var id = menuTable.getAttribute("tab_id");
					if(!id)
						id = menuTable.getAttribute("roleid");
					
					if(!id)
						return;
						
					document.getElementById(id).style.background ="url(\"../../components/homewidget/images/serviceicon/service-bg.png\") no-repeat -3px 0px";
					document.getElementById(id).style.backgroundSize ="100% 100%";
		        },
		        mouseout:function(evt,menuTable){
		        	var id = menuTable.getAttribute("tab_id");
					if(!id)
						id = menuTable.getAttribute("roleid");
					
					if(!id)
						return;
		        	
					document.getElementById(id).style.background = "#FFFFFF";
	        	}
			}
		};
		
		return items;
	},
	/*展开剩余的分类项*/
	spreadMergeTemp:function(mergeList){
		var me = this;
		var surplusMergeTemp=Ext.create('Ext.Img',{
			width:85,
			height:10,
			style:'cursor:pointer;background:url(../../components/homewidget/images/serviceicon/zhankai.png) no-repeat center center;',
			listeners : {
            	el:{
                	click:function(){
                		me.removeAll();
						me.add(me.generateMergeTemp(mergeList));
                	}
            	}
        	}
		});
		return surplusMergeTemp;
	},
	tempTap : function(a, b, c) {
		var serviceType = b.getAttribute('servicetype');
		if(serviceType==2){
			var tabid = b.getAttribute('tab_id');
			window.location.href='/module/card/cardCommonSearch.jsp?a0100=self&callbackfunc=home5&inforkind=1&tabid='+tabid;
			return;
		}
	
		var vo = new HashMap();
		var type = b.getAttribute('type');
		var tabid = b.getAttribute('tab_id');
		var rejectingTasks = b.getAttribute('rejectingTasks');
		var ins_id = b.getAttribute('ins_id');
		var task_id = b.getAttribute('task_id');
		var htmlTem = "";
		var needEncryptParam = "";
		var closeableBoolean = false;
		var tabname = b.getAttribute('tabname');
		if (type && type === 'historydata')
			tabname = "我的申请";
		if (type && type === 'historydata') {// 我的申请
			closeableBoolean = true;
			htmlTem = '<iframe style="margin-top:-1px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/components/homewidget/TemplateHistoryData.html?b_query=link&tab_id='
					+ tabid
					+ '&module_id=9&other_param=visible_title=1&view_type=1';
		} else if(type && type === 'jumpurl'){
			if (rejectingTasks) {//驳回
				vo.put("taskid", rejectingTasks);
				htmlTem = '<iframe style="margin-top:-1px;margin-left:5px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/module/template/templatemain/templatemain.html?b_query=link&encryptParam=';
				needEncryptParam = '&tab_id='+ tabid + '&view_type=1&ins_id=0&other_param=visible_title=1&return_flag=11&module_id=9&approve_flag=1&view_type=card&callBack_close=function(){location.href=rootPath+"/templates/index/hcm_portal.do?b_query=link";}';
			} 
			if (task_id && ins_id) {//ins_id、task_id 属性有值时进入已报单据页面
				vo.put("taskid", task_id);
				htmlTem = '<iframe style="margin-top:-1px;margin-left:-1px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/module/template/templatemain/templatemain.html?b_query=link&encryptParam=';
				needEncryptParam = '&tab_id=' + tabid + '&module_id=9&approve_flag=0&sp_flag=1&ins_id='+ins_id+'&return_flag=3&view_type=card&other_param=visible_title=1&callBack_close=function(){window.parent.Ext.getCmp("serviceHallId").closeTempWindow();}';
			} 
			if (!rejectingTasks && !ins_id && !task_id) {//新增业务单据
				closeableBoolean = true;
				htmlTem = '<iframe style="margin-top:-1px;margin-left:-1px;" id="iframepage" frameborder="0" width="100%" height="100%" src="/module/template/templatemain/templatemain.html?b_query=link&encryptParam=';
				needEncryptParam='&tab_id='+b.getAttribute('tab_id')+'&task_id=0&view_type=1&module_id=9&approve_flag=1&ins_id=0&return_flag=6&view_type=card&other_param=visible_title=1&callBack_close=function(){window.parent.Ext.getCmp("serviceHallId").closeTempWindow();}';
			} 
		}else return;
		vo.put("type", "encryptParam");
		vo.put("needEncryptParam", needEncryptParam);
		vo.put("tabid", tabid);
		Rpc({
			functionId : 'ZJ100000166',
			async : true,
			success : function(res) {
				var html = Ext.decode(res.responseText).html;
				var newWindow = Ext.create("Ext.container.Container", {
					plain : true,
					border : false,
					frame : false,
					resizable : false,
					modal : true,
					floating : true,
					layout : 'fit',
					draggable : false,
					style : 'background:white;z-index:9999',
					height : '100%',
					width : '100%',
					id : 'serviceHallWin',
					renderTo : Ext.getBody(),
					scrollable : false,
					html : htmlTem + html + '" />',
					listeners : {
						'beforedestroy' : function() {
							var voo = new HashMap();
							voo.put("type", "beforedestroy");
							voo.put("tabid", tabid);
							Rpc({
								functionId : 'ZJ100000166',
								async : false,
								success : function(res) {
									var htmlTemp = "";
									var temp = Ext.decode(res.responseText).temp;
									if(temp){
										if (temp.rejectingTasks)
											htmlTemp += "<div type=\"tododata\" tabname=\""+ temp.tabname+ "\" tab_id=\""+ temp.tabid+ "\"  style='color:red;float:right;font-size:15px;'>退回</div>";
										document.getElementById("templ_" + b.getAttribute('tab_id')).innerHTML = htmlTemp;
										var historydataDom = Ext.getDom("historydataId"+temp.tabid);
										if(historydataDom && temp.applyedNum !='0' && temp.applyedNum !=0)
											Ext.getDom("template_"+tabid).setAttribute('applyedNum',temp.applyedNum);
										else if(!historydataDom && temp.applyedNum !='0' && temp.applyedNum !=0){
											htmlTemp = "";
/*											htmlTemp += "<div type=\"historydata\" tabname=\""+ temp.tabname+ "\" tab_id=\""+ temp.tabid+ "\" taskid=\""+ temp.applyedNum
												 + "\" title=\"我的申请\" style=\"font-size:10px;float:right;color:#ffffff;text-align:center;width:16px;height:16px;background:url('../../components/homewidget/images/serviceicon/historydata.png')\"></div>";
											document.getElementById("temp_" + b.getAttribute('tab_id')).innerHTML = htmlTemp;
*/										}
										
										Ext.getDom("template_"+tabid).setAttribute('rejectingTasks',temp.rejectingTasks);
										Ext.getDom("template_"+tabid).setAttribute('task_id',temp.task_id);
										Ext.getDom("template_"+tabid).setAttribute('ins_id',temp.ins_id);
									}
									window.top.myTask="false";//服务大厅返回 桌面显示申报界面
									window.location.href="/templates/index/hcm_portal.do?b_query=link";
								},
								scope : this
							}, voo);
						}
					}
				});
			},
			scope : this
		}, vo);
	},
	closeTempWindow : function() {// add by xiegh on 20171102 bug:32417
		var tempBox = Ext.getCmp("serviceHallWin");
		tempBox.on('hide', function() {// 先隐藏，后销毁；先销毁，程序认为iframe还在使用会报权限不足
			setTimeout(function() {
						tempBox.destroy();
					}, 1);
		});
		tempBox.hide();
	}
});