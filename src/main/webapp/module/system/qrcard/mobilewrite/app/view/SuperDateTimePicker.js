/**
 * 日期控件
 */
Ext.define('QRCard.view.SuperDateTimePicker',{
	extend:'Ext.picker.Picker',
	alias:'widget.superdatetimepicker',
	/**
	 * @event change
	 * Fired when the value of this picker has changed and the done button is pressed.
	 * @param {Ext.picker.Date} this This Picker
	 * @param {Date} value The date value
	 */
	config:{
		/**
		 * 开始年份,如果他比yearTo大，则选择顺序颠倒
		 */
		yearFrom:(1949),
		/**
		 * 结束年份
		 */
		yearTo:(new Date().getFullYear()+5),
		/**
		 * 年显示值
		 */
		yearText:'年',
		/**
		 * 月显示值
		 */
		monthText:'月',
		/**
		 * 日显示值
		 */
		dayText:'日',
		/**
		 * 小时显示值
		 */
		hourText:'时',
		/**
		 * 分显示值
		 */
		minuteText:'分',
		/**
		 * 秒显示值
		 */
		secondText:'秒',
		/**
		 * 季显示值
		 */
		seasonText:'季',
		/**
		 * 周显示值
		 */
		weekText:'周',
		
		dateFormat:'y.m.d',
		/**
		 * 是否显示标题
		 */
		useTitles:true,
		
		cancelButton:'<font style="font-size:18px;">取消</font>',
		
		doneButton:'<font style="font-size:18px;">确定</font>',
		
		defaultValue:undefined,
		/**
		 * 动画
		 * type:slide（滑动），flip（翻转），pop（弹出），fade（淡入淡出）
		 * diretion:方向 up,down,left,right
		 * duration:动画时间
		 */
		animation:{
			type:'slide',
			direction:'up',
			duration:250
		},
		hideOnMaskTap:true
	},
	constructor:function(){
		this.callParent(arguments);
		this.createSlots();
	},
	initialize:function(){
		this.callParent();
		
		this.on({
			scope:this,
			delegate:'pickerslot',
			slotpick:'onSlotPick'
		});
		
		this.on({
			scope:this,
			show:'toValue'
		});
	},
	toValue:function(){
		var format = this.getDateFormat().toLowerCase(),
			defaultValue = this.getDefaultValue(),
			values;
		if(this.getDefaultValue()){
			values = defaultValue;
		} else {
			values = '';
			var date = new Date();
			if(format.indexOf('y')!=-1)
				values += ''+date.getFullYear();
			if(format.indexOf('m')!=-1)
				values += '.'+(date.getMonth()+1);
			if(format.indexOf('d')!=-1)
				values += '.'+date.getDate();
			if(format.indexOf('h')!=-1)
				values += ' '+date.getHours();
			if(format.indexOf('i')!=-1)
				values += ':'+date.getMinutes();		
			if(format.indexOf('s')!=-1)
				values += ':'+date.getSeconds();		
			if(format.indexOf('q')!=-1)
				values += '.'+this.getSeason(date.getMonth()+1);		
			if(format.indexOf('w')!=-1)
				values += '.'+Math.ceil((date.getDate()+6-date.getDay())/7);
		}
		this._defaultValue = values;
		this.setValue(values);
	},
	/**
	 * @param value 值
	 * @param animated 动画
	 */
	setValue:function(values,animated){
		var me = this,
			format = me.getDateFormat().toLowerCase(),
			value = {}, param;
		if(!values)
			return;
		me._defaultValue = values;
		if(Ext.isObject(values)){
			for ( var str in values) {
				if(str == 'year')
					param = values[str];
				if(str == 'month')
					param += '.'+values[str];
				if(str == 'day')
					param += '.'+values[str];
				if(str == 'hour')
					param += ' '+values[str];
				if(str == 'minute')
					param += ':'+values[str];
				if(str == 'second')
					param += ':'+values[str];
				if(str == 'season')
					param += '.'+values[str];
				if(str == 'week')
					param += '.'+values[str];
			}
		} else {
			param = values;
		}
		values = param.split(/[.\s:\-\/]/);
		
		if(format.indexOf('y')!=-1)
			value.year = parseInt(values[0]);
		if(format.indexOf('m')!=-1)
			value.month = parseInt(values[1]);
		if(format.indexOf('d')!=-1)
			value.day = parseInt(values[2]);
		if(format.indexOf('h')!=-1)
			value.hour = parseInt(values[3]);
		if(format.indexOf('i')!=-1)
			value.minute = parseInt(values[4]);
		if(format.indexOf('s')!=-1)
			value.second = parseInt(values[5]);
		if(format.indexOf('q')!=-1)
			value.season = parseInt(values[1]);
		if(format.indexOf('w')!=-1)
			value.week = parseInt(values[2]);
		
		this.callParent([value, animated]);
		this.onSlotPick();
	},
	
	//获取值
	getValue:function(useDom){
		var values = {},
		items = this.getItems().items,
		ln = items.length,
		item, i, returnvalue,
		dateFormat = this.getDateFormat().toLowerCase();
		format = dateFormat.replace(/\s+/g,'').replace(/-/g,'.').replace(/\//g,'.');
		
		for(i = 0; i < ln; i++){
			item = items[i];
			if(item instanceof Ext.picker.Slot){
				values[item.getName()] = this.pad2(item.getValue(false));
			}
		}
		if(values.year=='' || values.year==0)
			return "";
		if(format == 'y.m.d'){//年月日
			returnvalue = values.year+'.'+values.month+'.'+values.day;
		} else if(format == 'y.m.dh:i'){//年月日时分
			returnvalue = values.year+'.'+values.month+'.'+values.day+' '+values.hour+':'+values.minute;
		} else if(format == 'y.m.dh:i:s'){//年月日时分秒
			returnvalue = values.year+'.'+values.month+'.'+values.day+' '+values.hour+':'+values.minute+':'+values.second;
		} else if(format == 'y'){//年
			returnvalue = values.year;
		} else if(format == 'y.m'){//年月
			returnvalue = values.year+'.'+values.month;
		} else if(format == 'y.q'){//年季
			returnvalue = values.year+'.'+values.season;
		} else if(format == 'y.m.w'){//年月周
			returnvalue = values.year+'.'+values.month+'.'+values.week;
		}
		if(dateFormat.indexOf('-')!=-1)
			returnvalue = returnvalue.replace(/\./g,'-');
		else if(dateFormat.indexOf('/')!=-1)
			returnvalue = returnvalue.replace(/\./g,'/');
		return returnvalue;
	},
	/**
	 * 修改年最大值
	 */
	updateYearFrom: function () {
		if (this.initialized) {
			this.createSlots();
		}
	},
	/**
	 * 修改年最小值
	 */
	updateYearTo:function(){
		if(this.initialized){
			this.createSlots();
		}
	},
	/**
	 * 修改月显示值
	 */
	updateMonthText:function(newMonthText){
		var innerItems = this.getInnerItems(),
			ln = innerItems.length,
			item, i;
		if(this.initialized){
			for(i = 0; i < ln; i++){
				item = innerItems[i];
				if(item.getName() == 'month'){
					item.setTitle(newMonthText);
				}
			}
		}
	},
	/**
	 * 修改日显示值
	 */
	updateDayText:function(newDayText){
		var innerItems = this.getInnerItems(),
			ln = innerItems.length,
			item, i;
		if(this.initialized){
			for(i = 0; i < ln; i++){
				item = innerItems[i];
				if(item.getName() == 'day'){
					item.setTitle(newDayText);
				}
			}
		}
	},
	/**
	 * 修改年显示值
	 */
	updateYearText:function(yearText){
		var innerItems = this.getInnerItems(),
			ln = innerItems.length,
			item, i;
		if(this.initialized){
			for(i = 0; i < ln; i++){
				item = innerItems[i];
				if(item.getName() == 'year'){
					item.setTitle(yearText);
				}
			}
		}
	},
	/**
	 * 创建slots
	 * @private
	 */
	createSlots:function(){
		var me = this,
			format = me.getDateFormat().toLowerCase(),
			slots = [],
			date = new Date();
		if(format.indexOf('y')!=-1){//年
			slots.push(me.createSlot('year', me.createYearData()));
		}
		if(format.indexOf('m')!=-1){//月
			slots.push(me.createSlot('month', me.createMonthData()));
		}
		if(format.indexOf('d')!=-1){//日
			slots.push(me.createSlot('day', me.createDayData()));
		}
		if(format.indexOf('h')!=-1){//时
			slots.push(me.createSlot('hour', me.createHourData()));
		}
		if(format.indexOf('i')!=-1){//分
			slots.push(me.createSlot('minute', me.createISData()));
		}
		if(format.indexOf('s')!=-1){//秒
			slots.push(me.createSlot('second', me.createISData()));
		}
		if(format.indexOf('q')!=-1){//季
			slots.push(me.createSlot('season', me.createSeasonData()));
		}
		if(format.indexOf('w')!=-1){//周
			slots.push(me.createSlot('week', me.createWeekData()));
		}
		me.setStyle("font-size:16px;");
		me.setSlots(slots);
	},
	/**
	 * 创建slot
	 * @param name
	 * @param datas
	 * @param activeData
	 * @returns
	 */
	createSlot:function(name, datas){
		switch(name){
			case 'year'://年
				return {name:'year',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getYearText()+"</font>",flex:4};
			case 'month'://月
				return {name:'month',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getMonthText()+"</font>",flex:2};
			case 'day'://日
				return {name:'day',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getDayText()+"</font>",flex:2};
			case 'hour'://时
				return {name:'hour',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getHourText()+"</font>",flex:2};
			case 'minute'://分
				return {name:'minute',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getMinuteText()+"</font>",flex:2};
			case 'second'://秒
				return {name:'second',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getSecondText()+"</font>",flex:2};
			case 'season'://季
				return {name:'season',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getSeasonText()+"</font>",flex:2};
			case 'week'://周
				return {name:'week',align:'center',data:datas,title:'<font style="font-size:16px;">'+this.getWeekText()+"</font>",flex:8};
		}
	},
	getSeason:function(month){
		if(month<=3&&month>=1)
			return 1;
		else if(month<=6&&month>=4)
			return 2;
		else if(month<=9&&month>=7)
			return 3;
		else if(month<=12&&month>=10)
			return 4;
	},
	/**
	 * 创建年数据
	 * @returns {Array}
	 */
	createYearData:function(){
		var me = this,
			yearsFrom = me.getYearFrom(),
			yearsTo = me.getYearTo(),
			years = [],
			reverse = yearsFrom > yearsTo;
			years.push({text:'',value:''});
		while(yearsFrom){
			years.push({
				text: yearsFrom,
				value: yearsFrom
			});
			if(yearsFrom === yearsTo){
				break;
			}
			if(reverse){
				yearsFrom--;
			} else {
				yearsFrom++;
			}
		}
		return years;
	},
	/**
	 * 创建月数据
	 * @returns {Array}
	 */
	createMonthData:function(){
		var months = [];
		for ( var i = 1; i < 13; i++) {
			months.push({text:i,value:i});
		}
		return months;
	},
	/**
	 * 创建天数据
	 * @returns {Array}
	 */
	createDayData:function(date){
		if(!date)
			date = new Date();
		var me = this,days = [],
			daysInMonth = me.getDaysInMonth(date.getMonth()+1, date.getFullYear());
		for ( var i = 0; i < daysInMonth; i++) {
			days.push({text:i+1,value:i+1});
		}
		return days;
	},
	/**
	 * 创建时数据
	 * @returns {Array}
	 */
	createHourData:function(){
		var me = this,
			hours = [];
		for ( var i = 0; i < 24; i++) {
			hours.push({text:me.pad2(i),value:i});
		}
		return hours;
	},
	/**
	 * 创建分（秒）数据
	 * @returns {Array}
	 */
	createISData:function(){
		var me = this,
			minutes = [];
		for ( var i = 0; i < 60; i++) {
			minutes.push({text:me.pad2(i),value:i});
		}
		return minutes;
	},
	/**
	 * 创建季数据
	 * @returns {Array}
	 */
	createSeasonData:function(){
		var seasons = [];
		for ( var i = 1; i < 5; i++) {
			seasons.push({text:i,value:i});
		}
		return seasons;
	},
	/**
	 * 创建周数据
	 * @param date 日期
	 * @returns {Array}
	 */
	createWeekData:function(date){
		if(!date)
			date = new Date();
		
		var weekFlag = 3;
		var me = this,weeks = [],
			month = date.getMonth()+1,
			year = date.getFullYear(),
			daysInMonth = me.getDaysInMonth(month, year),
			lastMonthDays;
			
		if(month == 0)
			lastMonthDays = me.getDaysInMonth(12, year-1);
		else
			lastMonthDays = me.getDaysInMonth(month-1, year);
		
		
		date.setDate(1);
		//本月一号是周几
		var firstnum = date.getDay();
		date.setDate(daysInMonth);
		//本月最后一天是周几
		var lastnum = date.getDay();
		//本月共有几周
		var week = Math.ceil((date.getDate()+6-date.getDay())/7);
		
		if(firstnum>weekFlag){
			week=week-1;
		}
		if(lastnum<weekFlag){
			week=week-1;
		}
		for ( var i = 0; i < week; i++) {
			var text = '';
			if(i===0){
				if(firstnum>1&&firstnum<=weekFlag){
					if(month===1)
						text = '（'+(year-1)+'/12/'+((31+1)-(firstnum-1))+' - 01/'+me.pad2(7-firstnum+1)+'）';
					else
						text = '（'+me.pad2(month-1)+'/'+((lastMonthDays+1)-(firstnum-1))+' - '+me.pad2(month)+'/'+me.pad2(7-firstnum+1)+'）';
				} else if(firstnum===1){
					text = '（'+me.pad2(month)+'/1'+' - 7'+'）';
				} else if(firstnum===0){
					text = '（'+me.pad2(month)+'/2'+' - 8'+'）';
				} else {
					text = '（'+me.pad2(month)+'/'+(7-(firstnum-1)+1)+' - '+me.pad2(7-(firstnum-1)+1+6)+'）';
				}
			} else if(i===week-1){
					if(lastnum<weekFlag){
						if(month===12)
							text = '（12/'+(daysInMonth-6-lastnum)+' - '+me.pad2(daysInMonth-lastnum)+'）';
						else
							text = '（'+me.pad2(month)+'/'+(daysInMonth-lastnum-6)+' - '+me.pad2(daysInMonth-lastnum)+'）';
					}else{
						if(month===12)
							text = '（12/'+(daysInMonth-lastnum+1)+' - '+(year+1)+'/1/'+me.pad2(7-lastnum)+'）';
						else
							text = '（'+me.pad2(month)+'/'+(daysInMonth-lastnum+1)+' - '+me.pad2(month+1)+'/'+me.pad2(7-lastnum)+'）';
					}
			} else {
				if(firstnum===0){
					text = '（'+me.pad2(month)+'/'+me.pad2((i*7+1)-(firstnum-1))+'/'+' - '+me.pad2(((i)*7+1)-(firstnum-1)+6)+'）';
				}else if(firstnum>0&&firstnum<=3){
					text = '（'+me.pad2(month)+'/'+me.pad2((i*7+1)-(firstnum-1))+' - '+me.pad2(((i)*7+1)-(firstnum-1)+6)+'）';
				}else{
					text = '（'+me.pad2(month)+'/'+((i+1)*7-(firstnum-1)+1)+' - '+me.pad2((i+1)*7-(firstnum-1)+1+6)+'）';
				}
			}
			weeks.push({text:(i+1)+text,value:i+1});
		}
//		console.log(weeks);
		return weeks;
	},
	
	onSlotPick:function(){
		var value = this.getValue(true),
			format = this.getDateFormat().toLowerCase(),
			slot, date = new Date(), param, isnull;
		
		this.callParent(arguments);
		
		if(format.indexOf('y')!=-1){//年
			slot = this.query('pickerslot[name=year]')[0];
			param = slot.getValue(true);
			if(param=='')
				isnull = true;
			date.setFullYear(param);
			slot.doSetValue(param);
		}
		if(format.indexOf('m')!=-1){//月
			slot = this.query('pickerslot[name=month]')[0];
			if(isnull){
				slot.getStore().setData([{text:'',value:''}]);
			} else {
				slot.getStore().setData(this.createMonthData());
				param = slot.getValue(true);
				date.setMonth(param-1);
			}
			slot.doSetValue(param);
		}
		if(format.indexOf('d')!=-1){//日
			slot = this.query('pickerslot[name=day]')[0];
			if(isnull)
				slot.getStore().setData([{text:'',value:''}]);
			else
				slot.getStore().setData(this.createDayData(date));
			slot.doSetValue(slot.getValue(true));
		}
		if(format.indexOf('h')!=-1){//时
			slot = this.query('pickerslot[name=hour]')[0];
			if(isnull)
				slot.getStore().setData([{text:'',value:''}]);
			else
				slot.getStore().setData(this.createHourData());
			slot.doSetValue(slot.getValue(true));
		}
		if(format.indexOf('i')!=-1){//分
			slot = this.query('pickerslot[name=minute]')[0];
			if(isnull)
				slot.getStore().setData([{text:'',value:''}]);
			else
				slot.getStore().setData(this.createISData());
			slot.doSetValue(slot.getValue(true));
		}
		if(format.indexOf('s')!=-1){//秒
			slot = this.query('pickerslot[name=second]')[0];
			if(isnull)
				slot.getStore().setData([{text:'',value:''}]);
			else
				slot.getStore().setData(this.createISData());
			slot.doSetValue(slot.getValue(true));
		}
		if(format.indexOf('q')!=-1){//季
			slot = this.query('pickerslot[name=season]')[0];
			if(isnull)
				slot.getStore().setData([{text:'',value:''}]);
			else
				slot.getStore().setData(this.createSeasonData());
			slot.doSetValue(slot.getValue(true));
		}
		if(format.indexOf('w')!=-1){//周
			slot = this.query('pickerslot[name=week]')[0];
			if(isnull){
				slot.getStore().setData([{text:'',value:''}]);
			} else {
				slot.getStore().setData(this.createWeekData(date));
				param = slot.getValue(true);
			}
			param = slot.getValue(true);
			slot.doSetValue(param);
		}
	},
	/**
	 * 
	 * @param month
	 * @param year
	 * @returns
	 */
	getDaysInMonth:function(month, year){
		var daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
		return month == 2 && this.isLeapYear(year) ? 29 : daysInMonth[month - 1];
	},
	/**
	 * 
	 * @param year
	 * @returns {Boolean}
	 */
	isLeapYear:function(year){
		return !!((year & 3) === 0 && (year % 100 || (year % 400 === 0 && year)));
	},
	pad2:function(number){
		return (number < 10 ? '0' : '') + number;
	},
	show:function(animations){
		if(animations)
			this.callParent([animations]);
		else
			this.callParent([this.animation]);
	},
	onDoneButtonTap: function() {
        var oldValue = this._value,
            newValue = this.getValue(true);

        if (newValue != oldValue) {
            this.fireEvent('change', this, newValue);
        }
        this.onDoneSelected(newValue);
        this.hide();
        if(this.inputBlocker)
        	this.inputBlocker.unblockInputs();
        
    },
	onDoneSelected:function(value){}
    
});