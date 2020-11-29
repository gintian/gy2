/**
 * title 考勤日历组件
 * description 考勤日历组件
 * function 
 * 		1、选中按钮执行方法 onMonthSelected
 * 		2、修改单月状态方法 changeMonthState
 * 		3、重新加载页面方法 reloadComp
 * creater xus
 */
Ext.Loader.loadScript({url:'/components/attendanceMonth/monthComp.css',scope:this});
Ext.define('EHR.attendanceMonth.AttendanceMonthComp',{
	extend:'Ext.panel.Panel',
	xtype:'attendancemonthcomp',
	config:{
		layout:'vbox',
		style:'border:1px',
		width:258,
		align:'fit',
		//totalData数据类型：[{year:'2018',monthOrder:1,desc:'1月',state:0},...]//state状态：0：已办；1：进行中；2：代办
		totalData:[],
		currentYear:'',//当前年
		currentYearData:[],
		currentMonth:undefined,//当前月（传year_order属性）
		yearList:[] //考勤年数组
	},
	//初始化
	initComponent:function(){
		this.callParent(arguments);
		this.initCurrentYear();
		this.reloadComp();
	},
	//初始化当前年份
	initCurrentYear:function(){
		var currentYear = '';
		if(this.getCurrentYear())
			currentYear = this.getCurrentYear();
		else{
			var myDate = new Date();
			currentYear = myDate.getFullYear()+"";
		}
		//判断当前年份是否在数据中，若不在，则取数据中最近的年份
		var yearList = this.getAllYear();
		var yearLists = yearList.split(',');
		yearLists =this.bubleCounting(yearLists);
		this.setYearList(yearLists);
		if(Ext.Array.indexOf(yearLists,currentYear)==-1){
			currentYear = yearLists[yearLists.length-1];
		}
		this.setCurrentYear(currentYear);
	},
	//获取数据中的所有年份
	getAllYear:function(){
		var totalData = this.getTotalData();
		var yearList = '';
		for(var i = 0;i<totalData.length;i++){
			if(yearList==''||yearList.indexOf(totalData[i].year)==-1){
				if(yearList!='')
					yearList += ',';
				yearList += totalData[i].year;
			}
		}
		return yearList;
	},
	//设置当前年的月份
	getCurrentYearData:function(year){
		var totalData = this.getTotalData();
		var yearDataList=[];
		var maxMonth=12;
		//获取当前年的数据
		for(var i=0;i<totalData.length;i++){
			if(totalData[i].year==year){
				yearDataList.push(totalData[i]);
				//得到最大的月份
				if(totalData[i].monthOrder>maxMonth)
					maxMonth=totalData[i].monthOrder;
			}
			
		}
		//排序
		yearDataList = this.countingSort(yearDataList,maxMonth);
		return yearDataList;
	},
	//冒泡排序法
	bubleCounting:function(arr){
		for(var i =0;i<arr.length-1;i++) { 
            for(var j=0;j<arr.length-i-1;j++) {//-1为了防止溢出
                if(arr[j]>arr[j+1]) {
                    var temp = arr[j];
                     
                    arr[j]=arr[j+1];
                     
                    arr[j+1]=temp;
            }
            }    
        }
		return arr;
	},
	//计数排序法
	countingSort:function (arr, maxValue) {
	    var bucket = new Array(maxValue + 1),
	        sortedIndex = 0;
	        arrLen = arr.length,
	        bucketLen = maxValue + 1;
	 
	    for (var i = 0; i < arrLen; i++) {
	        if (!bucket[arr[i].monthOrder]) {
	            bucket[arr[i].monthOrder] = arr[i];
	        }
	    }
	 
	    for (var j = 0; j < bucketLen; j++) {
	        if(bucket[j] != undefined) {
	            arr[sortedIndex++] = bucket[j];
	        }
	    }
	 
	    return arr;
	},
	//初始化
	initPickerPanel:function(){
		var me = this;
		var titlePanel = Ext.create('Ext.container.Container',{
			height:30,
			width:258,
			layout:'column',
			items:[{
				xtype:'container',
				columnWidth:0.4,
				margin:'8 0 8 0',
				layout : {
	                type : 'vbox',
	                align : 'right'
	            },
				items:{
					xtype:'container',
					border:0,
					width:18,
					height:18,
					style:'background-image:url(/images/components/left.png)!important;',
					listeners:{
						el:{
							click:function( own, e, eOpts ){
								var currentYear = me.getCurrentYear();
								currentYear = me.getYearList()[Ext.Array.indexOf(me.getYearList(),currentYear+'')-1];
								me.setCurrentYear(currentYear+"");
								me.reloadMonthPanel();
								if(me.getCurrentMonth())
									me.refreshArrow(me.getCurrentYear()+'_'+me.getCurrentMonth());
							}
						}
					}
				}
			},{
				xtype:'container',
				columnWidth:0.2,
				html:'<div style="text-align:center;line-height:30px;"><b>'+this.getCurrentYear()+'</b></div>'
			},{
				xtype:'container',
				columnWidth:0.4,
				margin:'8 0 8 0',
				layout : {
	                type : 'vbox',
	                align : 'left'
	            },
				items:{
					xtype:'container',
					border:0,
					width:18,
					height:18,
					style:'background-image:url(/images/components/right.png)!important;',
					listeners:{
						el:{
							click:function( own, e, eOpts ){
								var currentYear = me.getCurrentYear();
								currentYear = me.getYearList()[Ext.Array.indexOf(me.getYearList(),currentYear+'')+1];
								me.setCurrentYear(currentYear+"");
								me.reloadMonthPanel();
								if(me.getCurrentMonth())
									me.refreshArrow(me.getCurrentYear()+'_'+me.getCurrentMonth());
							}
						}
					}
				}
			}]
		});
		
		var monthPanel = Ext.create('Ext.container.Container',{
			layout:'vbox'
		});
		var lineList = this.getMonthHtml();
		for(var i = 0;i<lineList.length;i++){
			monthPanel.add(lineList[i]);
		}
		this.add(titlePanel,monthPanel);
	},
	//获取月份html
	getMonthHtml:function(){
		var me = this;
		var yearData = this.getCurrentYearData(this.getCurrentYear());
		var lineList=[];
		for(var i = 0;i<yearData.length;i++){
			if(i%4==0){
				lineList.push(new Ext.container.Container({layout:'hbox'}));
			};
			var index = parseInt(i/4);
			var panel = lineList[index];
			var backgroundColor='#40a0db';
			var fontColor='#FFFFFF';
			if(yearData[i].state==0){
				backgroundColor='#F0F0F0';
				fontColor='#646464';
			}else if(yearData[i].state==1){
				backgroundColor='#07b107';
			}
			panel.add({
				xtype:'container',
				height:44,
				items:[
					{
					xtype:'container',
					width:60,
					height:40,
					border:0,
					margin:'2 2 2 2',
					dataInfo:yearData[i],
					style:'background-color:'+backgroundColor+';cursor:pointer;',
					html:'<div dataInfo="'+yearData[i]+'" style="text-align:center;line-height:40px;"><font color='+fontColor+'><b>'+yearData[i].desc+'</b></font></div>',
					listeners:{
						'afterrender': function(panel) {
							panel.el.on('click', function() {//在这里关联
								me.onMonthSelected(panel.dataInfo );
								me.setCurrentMonth(panel.dataInfo.monthOrder);
								me.reloadComp();
//								//xus 测试修改单月数据方法
//								me.changeMonthState(own.dataInfo.year,own.dataInfo.monthOrder,2);
							});
						}
					}
				},{xtype:'container',id:'calander-arrow'+yearData[i].year+'_'+yearData[i].monthOrder,hidden:true,cls:'calendar-selected'}]
			});
		}
		return lineList;
	},
	//重新加载月份
	reloadMonthPanel:function(){
		this.items.items[1].removeAll(true,true);
		var lineList = this.getMonthHtml();
		for(var i = 0;i<lineList.length;i++){
			this.items.items[1].add(lineList[i]);
		}
		this.refreshTitlePanel();
	},
	//重新加载标题栏
	refreshTitlePanel:function(){
		var currentyear = this.getCurrentYear();
		//去年按钮显示、隐藏状态修改
		if(Ext.Array.indexOf(this.getYearList(),currentyear)==0){
			this.items.items[0].items.items[0].items.items[0].setHidden(true);
		}else{
			this.items.items[0].items.items[0].items.items[0].setHidden(false);
		};
		//明年按钮显示、隐藏状态修改
		if(Ext.Array.indexOf(this.getYearList(),currentyear)+1==this.getYearList().length){
			this.items.items[0].items.items[2].items.items[0].setHidden(true);
		}else{
			this.items.items[0].items.items[2].items.items[0].setHidden(false);
		};
		//重新加载标题
		this.items.items[0].items.items[1].setHtml('<div style="text-align:center;line-height:30px;"><b>'+currentyear+'</b></div>');
	},
	//选中按钮执行方法
	onMonthSelected:function(data){
	},
	//修改单月状态
	changeMonthState:function(year,month,state){
		var returnFlag = false;
		var monthList = this.getCurrentYearData(year);
		if(monthList.length==0){
			Ext.msg.alert( "提示", "当前年无月份数据！"); 
			return returnFlag;
		}
		for(var i=0;i<this.getTotalData().length;i++){
			if(this.getTotalData()[i].monthOrder==month&&this.getTotalData()[i].year==year){
				returnFlag = true;
				this.getTotalData()[i].state=state;
			}
		}
		if(returnFlag){
			this.reloadComp();
		}
		return returnFlag;
	},
	//重新加载页面
	reloadComp:function(){
		this.removeAll(true,true);
		this.initPickerPanel();
		this.refreshTitlePanel();
		if(this.getCurrentMonth())
			this.refreshArrow(this.getCurrentYear()+'_'+this.getCurrentMonth());
	},
	//重新加载当前月
	refreshArrow:function(key){
		if(Ext.getCmp('calander-arrow'+key))
			Ext.getCmp('calander-arrow'+key).setHidden(false);
	}
	
})