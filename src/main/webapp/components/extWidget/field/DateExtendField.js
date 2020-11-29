/**create by hej
 * 2016-1-5
 * 日期下拉控件 支持年、季度、月
 * format 格式包括
 * 'Y'-- 年
 * 'Q'-- 与年、季度 相同
 * 'M'-- 与年、月 相同
 * 'Q,M'-- 与年、季度、月 相同
 * 'Y,Q'-- 年、季度
 * 'Y,Q,M'-- 年、季度、月
 * 'Y,M'-- 年、月
 */
Ext.define('EHR.extWidget.field.DateExtendField', {
	extend : "Ext.form.field.Picker",
	xtype:"dateextendfield",
	requires:["EHR.extWidget.picker.DateExtendPicker"],
    datechecked:undefined,
    scope:undefined,
    initComponent : function(){
        date_me = this;
        Ext.apply(date_me,{
            fieldLabel : date_me.fieldLabel,
            labelWidth : date_me.labelWidth,
            checkflag:date_me.checkflag
        });
        date_me.callParent();
        date_me.setValue();
    },
    /**
     * 给文本框赋值
     */
    setValue:function(){
    	var myDate = new Date();       
		var year = myDate.getFullYear();
		var month = parseInt(myDate.getMonth())+1;
		var currQuarter = Math.floor( ( month % 3 == 0 ? ( month / 3 ) : ( month / 3 + 1 ) ) );
		var date = '';
		if(date_me.format=='Y'||date_me.format==''){
			date = year+'年';
		}
		else if(date_me.format=='Y,M'||date_me.format=='Y,Q,M'||date_me.format=='Q,M'||date_me.format=='M'){
			date = year+'年 '+month+'月';
		}
		else if(date_me.format=='Y,Q'||date_me.format=='Q'){
			date = year+'年 '+currQuarter+'季度';
		}
		date_me.setRawValue(date);
    	
    },
    createPicker:function(){
    	var me = this;
    	date_me.picker = Ext.create("EHR.extWidget.picker.DateExtendPicker",{
    		format:date_me.format,
    		dateSelected:function(text,year,quarter,month,checkflag){
    			me.setRawValue(text);
    			me.checkflag = checkflag;
    			Ext.callback(me.datechecked,me.scope,[year,quarter,month]);
    			this.collapse();
    		},
    		scope:me
    	});
    	return date_me.picker;
    },
    onExpand: function() {
    	var currentvalue = this.getRawValue();
    	var myeartitle = this.picker.getEl().query("input[name='myeartitle']")[0];
    	myeartitle.value = currentvalue.split('年')[0];
    	if(this.format=='Y'||this.format==''){
    		var years = Ext.getDom('years');
			for(var i=11;i>=0;i--){
				var newa;
				var aa=currentvalue.split('年')[0]-i;
				years.removeChild(years.childNodes[i]);
				var newli = document.createElement("li");
				if(aa==currentvalue.split('年')[0])
				    newa = "<a href='###' onclick='date_me.selectPeriodYear("+aa+")' style='background-color:rgb(238, 238, 238)'>&nbsp;"+aa+"年</a>";
				else
					newa = "<a href='###' onclick='date_me.selectPeriodYear("+aa+")' >&nbsp;"+aa+"年</a>";
				newli.innerHTML=newa;
				years.appendChild(newli);
			}
    	}
    	else if(date_me.format=='Y,M'||date_me.format=='M'){//年月
    		var months = Ext.getDom('months');
    		currentvalue = currentvalue.replace(/\s+/g,"");
    		var monthvalue = currentvalue.split('年')[1];
    		for(var i=11;i>=0;i--){
				var a = months.childNodes[i].childNodes[0];
				var avalue = a.innerText;
				avalue= avalue.replace(/\s+/g,"");
				if(avalue==monthvalue)
					a.setAttribute('style','background-color:rgb(238, 238, 238)');
				else
					a.removeAttribute("style");
			}
    	}
    	else if(date_me.format=='Y,Q,M'||date_me.format=='Q,M'||date_me.format=='Y,Q'||date_me.format=='Q'){//年季月 -- 年季
    		var yearquartermon;
    		currentvalue = currentvalue.replace(/\s+/g,"");//X年X月 X年X季度
    		var monthvalue = currentvalue.split('年')[1];
    		var i;
    		if(date_me.format=='Y,Q,M'||date_me.format=='Q,M'){
    			i=15;
    			yearquartermon = Ext.getDom('yearquartermon');
    		}
    		if(date_me.format=='Y,Q'||date_me.format=='Q'){
    			i=3;
    			yearquartermon = Ext.getDom('yearquarter');
    		}
    		if(monthvalue.indexOf('季度')){
    			monthvalue = monthvalue.split('季度')[0];
    			if(monthvalue==4){
    				monthvalue = '四季度';
    			}
    			else if(monthvalue==3){
    				monthvalue = '三季度';
    			}
    			else if(monthvalue==2){
    				monthvalue = '二季度';
    			}
    			else if(monthvalue==1){
    				monthvalue = '一季度';
    			}
    		}
    		for(i;i>=0;i--){
				var a = yearquartermon.childNodes[i].childNodes[0];
				var avalue = a.innerText;
				avalue= avalue.replace(/\s+/g,"");
				if(avalue==monthvalue)
					a.setAttribute('style','background-color:rgb(238, 238, 238)');
				else
					a.removeAttribute("style");
			}
    	}
    }
});