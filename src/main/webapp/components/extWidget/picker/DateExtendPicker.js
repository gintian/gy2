/**create by hej
 * 2016-1-5
 * 自定义的日期选择控件 包括年、季度、月 几种形式显示
 * format 格式包括
 * 'Y'-- 年
 * 'Q'-- 与年、季度 相同
 * 'M'-- 与年、月 相同
 * 'Q,M'-- 与年、季度、月 相同
 * 'Y,Q'-- 年、季度
 * 'Y,Q,M'-- 年、季度、月
 * 'Y,M'-- 年、月
 */
Ext.define('EHR.extWidget.picker.DateExtendPicker', {
	extend : 'Ext.panel.Panel',
	xtype:"dateextendpicker",
    date_me:'',
    dateSelected:undefined,
    scope:undefined,
    picker:undefined,
    checkflag:undefined,
    initComponent : function(){
        date_me = this;
        date_me.callParent();
        date_me.initPanel();
    },
    initPanel:function(){
    	var myDate = new Date();       
		var year = myDate.getFullYear();
		var month = parseInt(myDate.getMonth())+1;
		var html='';
		html+="<div class='hj-wzm-clock dropdownlist' style='border:0px' id='monthlist' >" +
 			"<ul style='text-align:center;width:205px;'>" +
 			"<span style='color:#549FE3;'>" +
 			"<a dropdownName='monthbox' href='javascript:date_me.yearchange(-1);'><img dropdownName='monthbox' src='/workplan/image/left2.gif' />&nbsp;</a>" +
 			"<span><input name='myeartitle' style='color:#549FE3;width:33px;border:1px #8db3e3 solid;' type='text' value='"+year+"' onChange='javascript:date_me.onBlur(this.value)'>&nbsp;年&nbsp;</span>" +
 			"<a  dropdownName='monthbox' href='javascript:date_me.yearchange(1);'><img dropdownName='monthbox' src='/workplan/image/right2.gif' /></a>" +
 			"</span></ul>";
		if(date_me.format=='Y'||date_me.format==''){//年
			html+="<ul id='years' style='width:210px;'>";
			for(var i=11;i>=0;i--){
				var aa=year-i;
				html+="<li ><a href='###' onclick='date_me.selectPeriodYear("+aa+")' >&nbsp;"+aa+"年</a></li>";
			}
			html+="</ul></div>";
		}
		else if(date_me.format=='Y,M'||date_me.format=='M'){//年月
        
		html+="<ul id='months' style='width:205px;'>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(1)' >&nbsp;1月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(2)' >&nbsp;2月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(3)' >&nbsp;3月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(4)' >&nbsp;4月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(5)' >&nbsp;5月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(6)' >&nbsp;6月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(7)' >&nbsp;7月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(8)' >&nbsp;8月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(9)' >&nbsp;9月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(10)' >10月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(11)' >11月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(12)' >12月</a></li>" +
 			"</ul></div>";
		}
		else if(date_me.format=='Y,Q,M'||date_me.format=='Q,M'){//年季月
			html+="<ul id='yearquartermon'>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodQuarter(1)' style='text-align:left;'>&nbsp;一季度</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodQuarter(2)' style='text-align:left;'>&nbsp;二季度</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodQuarter(3)' style='text-align:left;'>&nbsp;三季度</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodQuarter(4)' style='text-align:left;'>&nbsp;四季度</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(1)' >&nbsp;1月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(2)' >&nbsp;2月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(3)' >&nbsp;3月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(4)' >&nbsp;4月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(5)' >&nbsp;5月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(6)' >&nbsp;6月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(7)' >&nbsp;7月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(8)' >&nbsp;8月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(9)' >&nbsp;9月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(10)' >&nbsp;10月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(11)' >&nbsp;11月</a></li>" +
 			"<li ><a href='###' onclick='date_me.selectPeriodMonth(12)' >&nbsp;12月</a></li>" +
 			"</ul></div>";
		}
		else if(date_me.format=='Y,Q'||date_me.format=='Q'){//年季
			html+="<ul id='yearquarter'>" +
 			"<li style='width:50px;'><a href='###' onclick='date_me.selectPeriodQuarter(1)' >&nbsp;一季度</a></li>" +
 			"<li style='width:50px;'><a href='###' onclick='date_me.selectPeriodQuarter(2)' >&nbsp;二季度</a></li>" +
 			"<li style='width:50px;'><a href='###' onclick='date_me.selectPeriodQuarter(3)' >&nbsp;三季度</a></li>" +
 			"<li style='width:50px;'><a href='###' onclick='date_me.selectPeriodQuarter(4)' >&nbsp;四季度</a></li>" +
 			"</ul></div>";
		}
        date_me.picker = new Ext.Panel({
                height : 138,
                html:html,
                autoScroll : true,
                width:234,
                floating : true,
                focusOnToFront : false,
                shadow : true,
                ownerCt : this.ownerCt,
                useArrows : true,
                rootVisible : false
        });
        if(date_me.format=='Y,Q,M'||date_me.format=='Q,M'){
        	date_me.picker.height=150;
        }
        if(date_me.format=='Y,Q'||date_me.format=='Q'){
        	date_me.picker.height=80;
        }
        return date_me.picker;
    },
    /**
     * 选择月
     * @param {} a
     */
    selectPeriodMonth:function(a){
    	var year = date_me.picker.getEl().query("input[name='myeartitle']")[0];
    	var text = Number(year.value)+'年 '+a+'月';
    	date_me.checkflag = true;
    	Ext.callback(date_me.dateSelected,date_me.scope,[text,year.value,'',a+'',date_me.checkflag]);
    },
    /**
     * 选择年
     * @param {} a
     */
    selectPeriodYear:function(a){
    	var text = a+'年';
    	date_me.checkflag = true;
    	Ext.callback(date_me.dateSelected,date_me.scope,[text,a+'','','',date_me.checkflag]);
    },
    /**
     * 选择季度
     * @param {} a
     */
    selectPeriodQuarter:function(a){
    	var year = date_me.picker.getEl().query("input[name='myeartitle']")[0];
    	var text = Number(year.value)+'年 '+a+'季度';
    	date_me.checkflag = true;
    	Ext.callback(date_me.dateSelected,date_me.scope,[text,year.value,a+'','',date_me.checkflag]);
    },
    /**
     * 年变化
     * @param {} a
     */
    yearchange:function(a){
    	var year = date_me.picker.getEl().query("input[name='myeartitle']")[0];
		year.value = Number(year.value)+a;
		if(date_me.format=='Y'||date_me.format==''){//只有年
			var years = Ext.getDom('years');
			if(a==1){
				years.removeChild(years.childNodes[0]);
				var newli = document.createElement("li");
				var newa = "<a href='###' onclick='date_me.selectPeriodYear("+Number(year.value)+")' >&nbsp;"+Number(year.value)+"年</a>";
				newli.innerHTML=newa;
				years.appendChild(newli);
			}else{
				var year11 = Number(year.value)-11;
				var nodeyear = years.childNodes[0];
				years.removeChild(years.childNodes[11]);
				var newli = document.createElement("li");
				var newa = "<a href='###' onclick='date_me.selectPeriodYear("+year11+")' >&nbsp;"+year11+"年</a>";
				newli.innerHTML=newa;
				years.insertBefore(newli,nodeyear);
			}
		}
    },
    /**
     * 文本框失去焦点事件
     */
    onBlur:function(a){
    	var year = date_me.picker.getEl().query("input[name='myeartitle']")[0];
    	var value = year.value;
    	if(date_me.format=='Y'||date_me.format==''){//只有年
			var years = Ext.getDom('years');
			for(var i=11;i>=0;i--){
				var aa=value-i;
				years.removeChild(years.childNodes[i]);
				var newli = document.createElement("li");
				var newa = "<a href='###' onclick='date_me.selectPeriodYear("+aa+")' >&nbsp;"+aa+"年</a>";
				newli.innerHTML=newa;
				years.appendChild(newli);
			}
		}
    }
});