//guodd 时间控件支持十分秒
Ext.define('EHR.extWidget.field.DateTimeField', {
    extend:'Ext.form.field.Date',
    alias: 'widget.datetimefield',
    requires:["EHR.extWidget.picker.DateTimePicker"],
 
    //xiegh 20170413 24026 重写父类中safeParse方法 add 方法：autoAppendChar 功能：自动补全输入的日期
    safeParse : function(value, format) {
        var me = this,
            utilDate = Ext.Date,
            result = null,
            strict = me.useStrict,
            parsedDate;
        if (utilDate.formatContainsHourInfo(format)) {
            result = utilDate.parse(value, format, strict);
        } else {
            parsedDate = utilDate.parse(value + ' ' + me.initTime, format + ' ' + me.initTimeFormat, strict);
            if (parsedDate) {
                result = utilDate.clearTime(parsedDate);
            }
        }
        result = me.autoAppendChar(value,format,result);
        return result;
    },
    
    autoAppendChar: function(value,format,result){
    	if(result!=null)
    		return result;
       //正则表达式：将传入的日期分割成：[...年,月,日...]数组   
    	var dates = value.match(/^ *(\d{4}){0,1}[-|.|年]{0,1}(\d{1,2}){0,1}[-|.|月]{0,1}(\d{1,2}){0,1}[日|]{0,1} ?(\d{1,2}){0,1}[:]{0,1}(\d{1,2}){0,1}[:]{0,1}(\d{1,2}){0,1}[:]{0,1} *$/);
    	//解析的时间数组为空时，终止
    	if(dates == null)
    		return null;
    	var utilDate = Ext.Date;
    	//年为null或者不是4位时，则终止补全逻辑
    	if(!dates[1] || dates[1].length!=4)
    		return null;
    	
    	var str = '';
    	for (var i = 1; i < dates.length; i++) {
    		if(i==1){//补全年份
    			str = str + dates[i] + '-';
    			continue;
    		}
    		if(i==2){//补全月份
    			if (!dates[i] || dates[i] =='') {
					str = str + '01-';
				}
    			else if (dates[i].length == 1) {
					str = str + '0' + dates[i] + '-';
				} else if (dates[i].length == 2) {
					str = str + dates[i] + '-';
				}else{
					str = str + '01-';
				}
    			continue;
    		}
    		
    		if (dates[i] == null || dates[i] =='') {//补全日
				str = str + '01';
			}
    		else if (dates[i].length == 1) {
				str = str + '0' + dates[i];
			} else if (dates[i].length == 2) {
				str = str + dates[i];
			}else{
				str = str + '01';
			}
    		break;
		}
    	if(utilDate.parse(str,"Y-m-d"))//y.m.d格式的日期转换后台模板设定的格式日期
    	return utilDate.clearTime(utilDate.parse(str,"Y-m-d"));//创建这个日期的克隆，清除时间并返回
    },
    
    createPicker:function(){
    
    		var me = this,
            format = Ext.String.format;
        return Ext.widget('datetimepicker',{
            pickerField: me,
            floating: true,
            preventRefocus: true,
            hidden: true,
            minDate: me.minValue,
            maxDate: me.maxValue,
            disabledDatesRE: me.disabledDatesRE,
            disabledDatesText: me.disabledDatesText,
            ariaDisabledDatesText: me.ariaDisabledDatesText,
            disabledDays: me.disabledDays,
            disabledDaysText: me.disabledDaysText,
            ariaDisabledDaysText: me.ariaDisabledDaysText,
            format: me.format,
            showToday: me.showToday,
            startDay: me.startDay,
            minText: format(me.minText, me.formatDate(me.minValue)),
            ariaMinText: format(me.ariaMinText, me.formatDate(me.minValue, me.ariaFormat)),
            maxText: format(me.maxText, me.formatDate(me.maxValue)),
            ariaMaxText: format(me.ariaMaxText, me.formatDate(me.maxValue, me.ariaFormat)),
            listeners: {
                scope: me,
                select: me.onSelect,
                tabout: me.onTabOut
            },
            keyNavConfig: {
                esc: function() {
                    me.inputEl.focus();
                    me.collapse();
                }
            }
        
        });
    
    },
    validator:function(value){
         if(!this.allowBlank && Ext.isEmpty(value) )
            return undefined;
         return true;   
             
    },
    onExpand: function() {
        //多传一个参数，从而避免时分秒被忽略。
        this.picker.setValue(Ext.isDate(this.value) ? this.value : new Date(), true);
    },
    /*formpanel的getValue()方法 获取的是getRawValue()值，改为getValue()*/
    getSubmitValue:function(){
    	return this.getValue();
    },
    getValue:function(){
        var val = this.callParent(arguments);
        if(!val)//xiegh  20170329 24640 （当val变化或者为null时会给出编辑标识，当val为null时，则将其变成''）
        	return '';
        return this.formatDate(val);
    }
    
});