/**
 * 工作监控计划区间插件
 * 
 * 半年、季度、周 排列显示，不采用下拉形式
 */
Ext.define('PeriodCascadeForTrackUL.PeriodCascadeForTrack',{
	extend:'Ext.container.Container',
	id:"periodtbar",
	callbackFn:Ext.emptyFn,
	scope:{},
	height:25,
    border:false,
    layout:{
        type: 'hbox'
    },
	constructor:function(config) {
		periodCascadeForTrack = this;

		this.config = config;
		this.callParent(arguments);
		
		this.type = this.defaltperiodtype;
		this.year = Ext.Date.format(Ext.Date.add(new Date(), Ext.Date.YEAR, 0), "Y");
        this.month = Ext.Date.format(Ext.Date.add(new Date(), Ext.Date.MONTH, 0), "m");
		this.halfyearnum = '';
		this.quarternum = '';
		this.weeknum = '';

		this.createStyleCss();
		this.getCurrentDateInfo();
		this.init();
	},
	// 计划区间
	init:function(){
		this.add([this.creatPlanTypeCombo(), {// 计划区间切换
                xtype:'container',// 年月切换
                id:'yearormonthcontainer',
                padding:0,
                layout:'fit',
                items:[]
            },{
                xtype:'container',// 半年、季度、周切换
                id:'optioncontainer',
                items:[]
            }]); 
	},
	// 获取当前日期的信息
	getCurrentDateInfo : function() {
		var me = this;
		var map = new HashMap();
        var year = getCookieValue(WorkPlanhr_me.curUsername+"_year");
        var week = getCookieValue(WorkPlanhr_me.curUsername+"_week");
        me.cur = {};
        me.cur.curyear = year;
        me.year = year;
        if ("2"==me.type){
            var halfyearnum = getCookieValue(WorkPlanhr_me.curUsername+"_halfyearnum");
            me.cur.curhalfyear = halfyearnum;
        }else if ("3" == me.type){
            var quarternum = getCookieValue(WorkPlanhr_me.curUsername+"_quarternum");
            me.cur.curquarter = quarternum;
        }else if ("4" == me.type){
            var month = getCookieValue(WorkPlanhr_me.curUsername+"_month");
            me.cur.curmonth = month;
            me.month = month;
        }else if ("5" == me.type){
            var month = getCookieValue(WorkPlanhr_me.curUsername+"_month");
            me.cur.curmonth = month;
            me.cur.curweek = week;
            me.month = month;
        }
		map.put("getcurrentinfo", true);
		map.put("type", "10");
		Rpc({
					functionId : 'WP50000001',
					async : false,
					success : function(form) {
						var result = Ext.decode(form.responseText);
						var curyear = result.curyear;
						var curmonth = result.curmonth;
						var curhalfyear = result.curhalfyear;// 当前上半年还是下半年
						var curquarter = result.curquarter;// 当前处于第几季度
						var curweek = result.curweek;// 当前处于第几周
						if(Ext.isEmpty(me.cur.curyear)){
                            me.cur.curyear = curyear;
                        }
                        if(Ext.isEmpty(me.cur.curmonth)){
                            me.cur.curmonth = (curmonth<10?"0":"")+curmonth;
                        }
                        if(Ext.isEmpty(me.cur.curhalfyear)){
                            me.cur.curhalfyear = curhalfyear;
                        }
                        if(Ext.isEmpty(me.cur.curquarter)){
                            me.cur.curquarter = curquarter;
                        }
                        if(Ext.isEmpty(me.cur.curweek)){
                            me.cur.curweek = curweek;
                        }
					}
				}, map);
	},
	creatPlanTypeCombo:function(){
		var me = this;
		
		return {
		    	xtype:'combo',
		    	fieldLabel: '',
		    	labelSeparator:'',
		    	store:this.getComboStore(),
		    	width:75,
		        forceSelection :true,
		        valueField: 'myId',
		        displayField: 'displayText',
		        editable:false,
		        labelAlign:'right',
		        allowBlank: false,
		        margin:'-3 5 0 0',
		        cls:'noBorder',
		        listeners: {  
					afterRender: function(combo) {
		    			if(combo.isVisible()){
		    				var record = {};
		    				
		    				var store = combo.getStore();
		    				if(!Ext.isEmpty(me.type)){// 有默认区间
			    				record = store.findRecord('myId', me.type);
			    				
		    				} else {// 无默认，定位第一条
		    					record = store.data.items[0];
		    				}
		    				
		    				combo.select(record);
							combo.fireEvent("select", combo, record);
		    			}
		            },
		            change:function(combo, value, oldValue){
		            	
		            	me.type = value;
		            	// set cookie
		            	setCookie(WorkPlanhr_me.curUsername+"_type", me.type);
		            	
		            	// 年、半年、季度计划要选择年份; 月、周计划要选择月份
		            	var yearormonthcontainer = Ext.getCmp('yearormonthcontainer');
		            	var optioncontainer = Ext.getCmp('optioncontainer');
		            	yearormonthcontainer.removeAll();
		            	optioncontainer.removeAll();
		            	if(value == '1' || value == '2' || value == '3'){
		            		yearormonthcontainer.add(me.createYearPicker());
		            		
		            	} else if(value == '4' || value == '5'){
		            		yearormonthcontainer.add(me.createMonthPicker());
		            	}
		            }
		        }
		    }
	},
	// 选择年份
	createYearPicker:function(){
		var me = this;
		
		var storeArray = [];
		for(var j=-4;j<3;j++){
            var yearmap =  new HashMap();
            var date = Ext.Date.format(Ext.Date.add(new Date(), Ext.Date.YEAR, j), "Y");
            
            storeArray.push([date, date+"年度"]);
        }
		
		var store = new Ext.data.ArrayStore({
            fields: ['myId','displayText'],
            data: storeArray
        });
		
		return {
		    	xtype:'combo',
		    	fieldLabel: '',
		    	labelSeparator:'',
		    	store:store,
		    	width:80,
		        forceSelection :true,
		        valueField: 'myId',
		        displayField: 'displayText',
		        editable:false,
		        labelAlign:'right',
		        allowBlank: false,
		        margin:'-3 10 0 0',
		        cls:'noBorder',
		        listeners: {  
					afterRender: function(combo) {
		    			if(combo.isVisible()){
		    				var record = {};
		    				
		    				var store = combo.getStore();
		    				if(!Ext.isEmpty(me.year)){
                                setCookie(WorkPlanhr_me.curUsername+"_year", me.year);
			    				record = store.findRecord('myId', me.year);
			    				
		    				} else {// 无默认，定位第一条
		    					record = store.data.items[0];
		    				}
		    				
							combo.select(record);
							combo.fireEvent("select", combo, record);
		    			}
		            },
		            change:function(combo, value){
		            	me.year = value;
                        setCookie(WorkPlanhr_me.curUsername+"_year", me.year);
		            	if(me.type == '1'){// 年计划时，切换年份直接回调
				        	me.callBack();
				        	return ;
				        }
				        
				        // 半年、季度计划时添加右侧切换
	            		if(me.type == '2' || me.type == '3'){
			            	var optioncontainer = Ext.getCmp('optioncontainer');
			            	optioncontainer.removeAll();
	            			optioncontainer.add(me.createOptionPicker(me.type));
	            		}
		            }
		        }
		    };
		
	},
	// 选择月份
	createMonthPicker:function(){
		var me = this;
		if (this.type == '5'){// 周计划时添加右侧切换
        	var map = new HashMap();
			map.put("gettotalnum", true);
			map.put("year", this.year);
			map.put("month", parseInt(this.month,10));
			map.put("type","10");
			Rpc({functionId : 'WP50000001',async : false,success : function(form){
				var result = Ext.decode(form.responseText);
				var totalweeknum = result.num;
	        	
	        	var optioncontainer = Ext.getCmp('optioncontainer');
	        	optioncontainer.removeAll();
				optioncontainer.add(me.createOptionPicker(me.type, totalweeknum));
			}},map);
		}
		
		// 月计划，直接回调
        if(me.type == '4'){
        	me.callBack();
        }
		
		return {
                xtype:'container',
                id:'timeid',
                width:90,
                margin: '0 15 0 0',
                html:"<a id = 'asd' href='javascript:periodCascadeForTrack.clickMonthPicker();' ><span id='monthYtitle'>"
                    +this.year+ "</span>年 <span id='monthtitle'>"
                    +this.month+ "</span>月 <img src='/workplan/image/jiantou.png' /></a>"
            };
	},
	//  月份下拉
    clickMonthPicker : function (){
    	var me = this;
        // 显示日期
    	var value = new Date();
		if(!Ext.isEmpty(this.year) && !Ext.isEmpty(this.month)){
			//xus 19/9/2 parseInt(string, radix)函数，不配置radix（进制）参数时默认为0,这种情况下的'08'、'09'转换为int型时值均为0
			//已改为radix=10 （10进制）
		    var month = parseInt(this.month,10)-1;
		    value = [month, parseInt(this.year,10)];
			
		}
    	
/*        if(Ext.getCmp('win')){
        	Ext.getCmp('win').show();
        	return ;
        }*/
		var x = Ext.get("asd").getX() - 65;
		var y = Ext.get("asd").getY() + 20;
        var win = Ext.create('Ext.window.Window', {
            id : 'win',
            x : x,
            y : y,
            header : false,
            resizable : false,
            padding:0,
            items:[{
			    xtype: 'monthpicker',
			    id:'monthpicker',
			    value: value,
			    onSelect: function(picker, selected) {
			        var month = selected[0]+1;
			        me.year = selected[1];
                    me.month = (month<10?"0":"")+month;
                    setCookie(WorkPlanhr_me.curUsername+"_year", me.year);
                    setCookie(WorkPlanhr_me.curUsername+"_month", me.month);
			        Ext.getDom('monthYtitle').innerHTML = me.year;
			        Ext.getDom('monthtitle').innerHTML = me.month;
			        
			        // 月计划，直接回调
			        if(me.type == '4'){
			        	
			        	me.callBack();
			        	return ;
			        }else if (me.type == '5'){// 周计划时添加右侧切换
			        	var map = new HashMap();
						map.put("year", me.year);
						map.put("month", parseInt(me.month,10));
						map.put("type","10");
						Rpc({functionId : 'WP50000001',async : false,success : function(form){
							var result = Ext.decode(form.responseText);
							var totalweeknum = result.num;
				        	var optioncontainer = Ext.getCmp('optioncontainer');
				        	optioncontainer.removeAll();
							optioncontainer.add(me.createOptionPicker(me.type, totalweeknum));
						}},map);
					}
			    },
			    listeners: {
			        okclick: 'onSelect',
			        monthdblclick: 'onSelect',
			        yeardblclick: 'onSelect',
			        cancelclick: function () {
			            this.setValue(new Date());
			        }
			    }
			}],
			listeners:{
		        //根据月计划勾选的月份，显示月份下拉  haosl update 2018-6-27
		       show:function(){
			       	var picker = Ext.getCmp("monthpicker");
			       	if(!picker){
			       		return;
			       	}
		       	 	var monthELs = picker.months.elements;
		       		if(me.type != '4'){
		       			return;
		       		}
		       		//月计划启用的月份
			       	var config_cycle = me.p3cycle;
			       	if(config_cycle.length>0){
			       		for(j = 0,len=monthELs.length; j < len; j++) {
		       				var monthEl = monthELs[j];
		       				var months = config_cycle.split(",");
		       				//月计划配置参数是否启用了该月份
		       				var flag = true;
				       		for(var i=0;i<months.length;i++){
				       			//将数字转换成月份  如 1-> 一月
				       			var monthStr = me.covertStr(months[i]);
				       			if(monthStr==monthEl.innerHTML){
									flag = false;
									break;
								}
				       		}
				       		//未启用该月份则月份控件中不可点击该月份（通过替换Ext生成的node节点 实现） haosl 
				       		if(flag){
				       			var html = monthEl.innerHTML;
				       			var div = document.createElement("div");
				       			div.className = "myMonthCls";
				       			div.title='计划未启用';
				       			var span = document.createElement("span");
				       			span.innerHTML = html;
				       			div.appendChild(span)
				       			monthEl.parentNode.parentNode.replaceChild(div,monthEl.parentNode);
				       		}
						}
			       	}
		        }
			}
        }).show();
        // 日期控件关闭
        Ext.getBody().addListener('click', function(evt, el) {
            if (!win.hidden && "asd" != el.id && el.id.indexOf('monthpicker')<0) {
                win.destroy();
            }
        });
    },
	// 切换区间 2：半年 3：季度 5：周
	createOptionPicker:function(type, totalweeknum){
	
		var option = "option='option'";
		var style = "style='margin-right:10px;'";
		var selectedClass1="class='link-visited'";
		var selectedClass2="class='link-visited'";
		var selectedClass3="class='link-visited'";
		var selectedClass4="class='link-visited'";
		var selectedClass5="class='link-visited'";
		
		var html = '';
		
		if(type == '2'){
            this.halfyearnum = 1;
            if(this.year == this.cur.curyear){
            	this.halfyearnum = this.cur.curhalfyear;
            }
            if(this.halfyearnum == 1){
            	selectedClass1="class='link-visited'";
            	selectedClass2="";
            } else if(this.halfyearnum == 2){
            	selectedClass1="";
            	selectedClass2="class='link-visited'";
            }
            
        	var config_cycle = this.p1cycle;
            html = "<div>";
            	if(config_cycle.indexOf('1') > -1){
                	html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"1\")' "+option+style+selectedClass1+">上半年</a>";
            	}
            	if(config_cycle.indexOf('2') > -1){
                    html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"2\")' "+option+style+selectedClass2+">下半年</a>";
            	}
        	html += "</div>";
            setCookie(WorkPlanhr_me.curUsername+"_halfyearnum",this.halfyearnum);
                	
		} else if(type == '3'){
			this.quarternum = 1;
            if(this.year == this.cur.curyear){
            	this.quarternum = this.cur.curquarter;
            }
            if(this.quarternum == 1){
            	selectedClass1="class='link-visited'";
            	selectedClass2="";
            	selectedClass3="";
            	selectedClass4="";
            } else if(this.quarternum == 2){
            	selectedClass1="";
            	selectedClass2="class='link-visited'";
            	selectedClass3="";
            	selectedClass4="";
            } else if(this.quarternum == 3){
            	selectedClass1="";
            	selectedClass2="";
            	selectedClass3="class='link-visited'";
            	selectedClass4="";
            } else if(this.quarternum == 4){
            	selectedClass1="";
            	selectedClass2="";
            	selectedClass3="";
            	selectedClass4="class='link-visited'";
            }
            
			var config_cycle = this.p2cycle;
            html = "<div>";
            	if(config_cycle.indexOf('1') > -1){
            		html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"1\")' "+option+style+selectedClass1+">第一季度</a>";
            	}
            	if(config_cycle.indexOf('2') > -1){
            		html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"2\")' "+option+style+selectedClass2+">第二季度</a>";
            	}
        		if(config_cycle.indexOf('3') > -1){
            		html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"3\")' "+option+style+selectedClass3+">第三季度</a>";
        		}
        		if(config_cycle.indexOf('4') > -1){
            		html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"4\")' "+option+style+selectedClass4+">第四季度</a>";
        		}
    		html += "</div>";
            setCookie(WorkPlanhr_me.curUsername+"_quarternum",this.quarternum);
		} else if(type == '5'){
			this.weeknum = 1;
            if(this.year == this.cur.curyear && this.month == this.cur.curmonth){
            	this.weeknum = this.cur.curweek;
            }
			
             if(this.weeknum == 1){
            	selectedClass1="class='link-visited'";
            	selectedClass2="";
            	selectedClass3="";
            	selectedClass4="";
            	selectedClass5="";
            } else if(this.weeknum == 2){
            	selectedClass1="";
            	selectedClass2="class='link-visited'";
            	selectedClass3="";
            	selectedClass4="";
            	selectedClass5="";
            } else if(this.weeknum == 3){
            	selectedClass1="";
            	selectedClass2="";
            	selectedClass3="class='link-visited'";
            	selectedClass4="";
            	selectedClass5="";
            } else if(this.weeknum == 4){
            	selectedClass1="";
            	selectedClass2="";
            	selectedClass3="";
            	selectedClass4="class='link-visited'";
            	selectedClass5="";
            } else if(this.weeknum == 5){
            	selectedClass1="";
            	selectedClass2="";
            	selectedClass3="";
            	selectedClass4="";
            	selectedClass5="class='link-visited'";
            }
            
			html = "<div>" +
		                "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"1\")' "+option+style+selectedClass1+">第一周</a>"+
		            	"<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"2\")' "+option+style+selectedClass2+">第二周</a>"+
		            	"<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"3\")' "+option+style+selectedClass3+">第三周</a>"+
		            	"<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"4\")' "+option+style+selectedClass4+">第四周</a>";
		            	if(totalweeknum == 5){
		            		html += "<a href='###' onclick='periodCascadeForTrack.selectOption(this,\"5\")' "+option+style+selectedClass5+">第五周</a>";
		            	}
             html += "</div>";
            setCookie(WorkPlanhr_me.curUsername+"_month",this.month);
            setCookie(WorkPlanhr_me.curUsername+"_weeknum",this.weeknum);
		}
		this.callBack();
		return {
                xtype:'container',
                margin: '0 15 0 0',
                html:html
            }
	},
    // 切换区间
    selectOption:function(dom, num){
    	
    	// 选中样式
		this.removeVisitedCss();
    	dom.className = 'link-visited';
    	var flag = false;//是否执行回调
    	// 区间设置
    	if (this.type == '2' && this.halfyearnum != num){// 半年
    		this.halfyearnum = num;
            setCookie(WorkPlanhr_me.curUsername+"_halfyearnum",num);
    		flag = true;
    		
    	} else if(this.type == '3' && this.quarternum != num){// 季度
    		this.quarternum = num;
            setCookie(WorkPlanhr_me.curUsername+"_quarternum",num);
    		flag = true;
    		
    	} else if(this.type == '5' && this.weeknum != num) {// 周
    		this.weeknum = num;
            setCookie(WorkPlanhr_me.curUsername+"_month", this.month);
            setCookie(WorkPlanhr_me.curUsername+"_weeknum",num);
    		flag = true;
    	}
    	if(flag)//重复点击时，不执行回调方法
    		this.callBack()
    },
	// 获取计划区间
	getComboStore:function(){
		var me = this;

		var dataArray = [];
         for(var p in me.config.params){
            var parammap =  new HashMap();
            var obj = me.config.params[p];
	        if(obj['p0'] != undefined ){
	            dataArray.push([1, '年计划']);
	            
	        }else if(obj['p1'] != undefined ){
	            dataArray.push([2, '半年计划']);
	            me.p1cycle = obj['p1'].cycle;
	            
	        }else if(obj['p2'] != undefined ){
	        	dataArray.push([3, '季度计划']);
	        	me.p2cycle = obj['p2'].cycle;
	        	
	        }else if(obj['p3'] != undefined ){
	        	dataArray.push([4, '月计划']);
	        	me.p3cycle = obj['p3'].cycle;
	        }else if(obj['p4'] != undefined ){
	        	dataArray.push([5, '周计划']);
	        	
	        }
         }
         
        return new Ext.data.ArrayStore({
            fields: ['myId','displayText'],
            data: dataArray
        });
	},
    // 去除选中样式
    removeVisitedCss:function(){
    	var aArray = Ext.query('a[option=option]');
		if(aArray.length > 0){
			for(var i=0; i<aArray.length; i++){
				var a = aArray[i];
				a.className = '';
			}
		}	
    },
	// 自定义样式
	 createStyleCss:function(){
	 	if(!Ext.util.CSS.getRule('.link-visited')) {
		 Ext.util.CSS.createStyleSheet(".link-visited{background:#EEEEEE;font-weight:bolder;}");
	 	}
		 
		 if(!Ext.util.CSS.getRule('.noBorder div')){
        	//Ext.util.CSS.createStyleSheet(".noBorder div{border-color:#ffffff;}","card_css");
		}
        if(!Ext.util.CSS.getRule('.noBorder .x-form-text-default')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-default{color:#1B4A98;}","card_css");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-default')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-default{background-image: url('/workplan/image/jiantou.png') !important;background-position:0px 0px;border-color:#ffffff;position:absolute;top:8px;right:3px;width:16px;height:16px;}","aaa");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-over')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-over{background-image: url('/workplan/image/jiantou.png') !important;background-position:0px 0px !important;border-color:#ffffff;position:absolute;top:8px;right:3px;width:16px;height:16px;}","bbb");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-focus')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-focus{background-image: url('/workplan/image/jiantou.png') !important;background-position:0px 0px !important;border-color:#ffffff;position:absolute;top:8px;right:3px;width:16px;height:16px;}","ccc");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-text-wrap-default')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-wrap-default{border-color:#ffffff;}","card_css");
        }
		if(!Ext.util.CSS.getRule('.noBorder .x-form-text-wrap-focus')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-wrap-focus{border-color:#ffffff;}","card_css");
		}
		if(!Ext.util.CSS.getRule('.myMonthCls')){
			Ext.util.CSS.createStyleSheet(".myMonthCls{height:18px;width:43px;float:left;text-align:center;margin:5px 0 4px;font:normal 11px 微软雅黑,宋体,tahoma,arial,verdana,sans-serif}","card_css");
			Ext.util.CSS.createStyleSheet(".myMonthCls span{color:#a7a7a7}","card_css");
		}
	},
	// 回调
	callBack:function(){
		var periodtype = this.type; 
		var periodyear = this.year; 
		var periodmonth = parseInt(this.month,10);
		var periodweek = this.weeknum;
		if(this.type == '2' ){
			periodmonth = this.halfyearnum ;
		} else if(this.type == '3'){
			periodmonth = this.quarternum; 
		}
		Ext.callback(this.callbackFn, this.scope, [periodtype, periodyear, periodmonth, periodweek]);
	},
	//将数字转换成月份  如 1-> 一月
	covertStr:function(month){
		var monthStr = "";
		switch(month){
			case "1":
				monthStr="一月";
				break;
			case "2":
				monthStr="二月";
				break;
			case "3":
				monthStr="三月";
				break;
			case "4":
				monthStr="四月";
				break;
			case "5":
				monthStr="五月";
				break;
			case "6":
				monthStr="六月";
				break;
			case "7":
				monthStr="七月";
				break;
			case "8":
				monthStr="八月";
				break;
			case "9":
				monthStr="九月";
				break;
			case "10":
				monthStr="十月";
				break;
			case "11":
				monthStr="十一月";
				break;
			case "12":
				monthStr="十二月";
				break;
		}
		
		return monthStr;
	}
});