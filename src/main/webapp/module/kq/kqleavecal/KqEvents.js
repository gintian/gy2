Ext.define('EHR.kq.KqEvents', {
    alias: 'widget.KqMonthview',
    config: {
        data: {},
        checkboxGroup: {},

    },

    constructor: function() {
        a = {};
        eventDatas = this;
        var myDate = new Date();
        var nowYear = myDate.getFullYear();        
        var nowMonth = myDate.getMonth() + 1;　　
        if (nowMonth >= 1 && nowMonth <= 9) {
        	　　nowMonth = "0" + nowMonth;　　
        }
        var nowData = nowYear + "-" + nowMonth ;
        eventDatas.data = eventDatas.getDatas("initialization", "1", nowData);
    },

    makeDate: function(d, h, m, s) {
        var today = Ext.Date.clearTime(new Date()),
        d = d * 86400;
        h = (h || 0) * 3600;
        m = (m || 0) * 60;
        s = (s || 0);
        return Ext.Date.add(today, Ext.Date.SECOND, d + h + m + s);
    },

    getDatas: function(leaveTypes, scope, month) {   	
        var evts = "{'evts' : [";
        var myCheckboxGroup = {};
        var today = Ext.Date.clearTime(new Date());
        var map = new HashMap(); 
        map.put("leaveTypes", leaveTypes);
        map.put("scope", scope);
        map.put("month", month);
        Rpc({
            async: false,
            functionId: 'KQ90000001',
            success: function(form, action) {
                var result = Ext.decode(form.responseText);
                var leaveType = result.leaveType;
                var leaveInfo = result.leaveInfo;
                var myCheckboxItems = [];            
                for (var i = 0; i < leaveType.length; i++) {
                    var name = leaveType[i].name;
                    var id = leaveType[i].id;
                    var colors = leaveType[i].color;
                    var css = ".font-color-" + id + "{border-collapse:separate;box-sizing:border-box;color: " + colors + ";cursor:default;display:inline-block;" 
                    + "font-family:微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;font-size:12px;font-weight:900;height:16px;" 
                    + "margin-top:4px;text-align:center;-webkit-font-smoothing:antialiased;}";
                    Ext.util.CSS.removeStyleSheet("color" + id);
                    Ext.util.CSS.createStyleSheet(css, "color" + id);

                    if (leaveTypes == "initialization") {
                        myCheckboxItems.push({
                            xtype: 'checkbox',
                            boxLabel: name,
                            boxLabelCls: "font-color-" + id,
                            inputValue: id,
                            width: name.length * 12 + 35,
                            id: id,
                            checked: true
                        });                        
                    }
                }
                if (leaveTypes == "initialization") {
                    myCheckboxGroup = new Ext.form.CheckboxGroup({
                        xtype: 'checkboxgroup',                     
                        id: "myGroups",
                        items: myCheckboxItems,
                        listeners: {
                            'change': function() {   
                            	Ext.getCmp('app-calendar').mask('loading....');
                            	setTimeout(function() {
	                                var holidayCheck = Ext.getCmp('myGroups').items;
	                                var holiday = '';
	                                for (var i = 0; i < holidayCheck.length; i++) {
	                                    if (holidayCheck.get(i).checked) {
	                                        holiday += ',';
	                                        holiday += holidayCheck.get(i).inputValue;
	                                    }
	                                }
	
	                                holiday = holiday.substring(1);
	                                if (Ext.getCmp('subordinate').checked) {
	                                    eventDatas.getDatas(holiday, "2", document.getElementById('dateText').innerHTML);
	                                } else {
	                                    eventDatas.getDatas(holiday, "1", document.getElementById('dateText').innerHTML);
	                                }  
                                },1);
                            }

                        }
                    });
                }

                for (var i = 0; i < leaveInfo.length; i++) {
                    if (i > 0) 
                    	evts += ",";
                    var nbase = leaveInfo[i].nbase;
                    var id = leaveInfo[i].id;
                    var a0100 = leaveInfo[i].a0100;
                    var reason = leaveInfo[i].reason;
                    var applyTime = "";
                    if (leaveInfo[i].applyTime != null) 
                    	applyTime = leaveInfo[i].applyTime.substring(0, 16);
                    var beginTime = leaveInfo[i].beginTime.substring(0, 16);
                    var endTime = leaveInfo[i].endTime.substring(0, 16);
                    var type = leaveInfo[i].type;
                    var typeName = leaveInfo[i].typeName;
                    var unit = leaveInfo[i].unit;
                    var a0101 = leaveInfo[i].a0101;
                    var e01A1 = leaveInfo[i].e01A1;
                    var timeLen = leaveInfo[i].timeLen;
                    var b0110 = leaveInfo[i].b0110;
                    if(leaveInfo[i].Q15Z5=="03"){
                    	var Q15Z5 = "已批";
                    }else{
                    	var Q15Z5 = "待批";
                    }                    
                    var newList = [];
                    var color = '';

                    for (var j = 0; j < leaveType.length; j++) {
                        var colorid = leaveType[j].id;
                        if (type == colorid) 
                        	color = leaveType[j].color;
                    }
                    evts += "{'cid' : '" + color + "'";
                    evts += ",'nbase' :'" + nbase + "'";
                    evts += ",'id' :'" + id + "'";
                    evts += ",'reason' :'" + reason + "'";
                    evts += ",'type' :'" + type + "'";
                    evts += ",'typeName' :'" + typeName + "'";
                    evts += ",'a0100' :'" + a0100 + "'";
                    evts += ",'title' :'" + a0101 + "'";
                    evts += ",'applyTime' :'" + applyTime + "'";
                    evts += ",'start' :'" + beginTime + "'";
                    evts += ",'end' : '" + endTime + "'";
                    evts += ",'unit' :'" + unit + "'";
                    evts += ",'e01A1' :'" + e01A1 + "'";
                    evts += ",'timeLen' :'" + timeLen + "'";
                    evts += ",'Q15Z5' :'" + Q15Z5 + "'";
                    evts += ",'b0110' :'" + b0110 + "'";
                    evts += ",'ad' : true}";
                }
            }
        },
        map);
        evts += "]}";  
        if (leaveTypes == "initialization") {
            eventDatas.checkboxGroup = myCheckboxGroup;
            return Ext.decode(evts);
        } else {
        	    var dataDays = new Array();
        	    leavecal.eventStore.removeAll();
        	    leavecal.eventStore = Ext.create('Ext.calendar.data.MemoryEventStore', {
        	        data: Ext.decode(evts)
        	    });
        	    Ext.getCmp('app-calendar-month').setStore(leavecal.eventStore, false);        	  
        	    var dataList = leavecal.eventStore.config.data.evts;
        	    for (var i = 0; i < dataList.length; i++) {
        	        if (dataList[i]) {        	        
        	            var startTime = dataList[i].start.substring(0, 10);
        	            var endTime = dataList[i].end.substring(0, 10);
        	            startTime = startTime.replace(/-/g, "");       	          
        	            endTime = endTime.replace(/-/g, "");       	        
        	            var dayTime = startTime;
        	            for (var j = startTime; j <= endTime; j++) {          	            	
         	                if (Ext.Array.indexOf(dataDays, j, 0) == -1) {
         	                    dataDays.push(j);         	                   
         	                }    	               
        	            }
        	        }
        	    }

        	    for (var y = 0; y < dataDays.length; y++) {
        	        dayId = 'app-calendar-month-day-' + dataDays[y] ;
        	        evdayId = 'app-calendar-month-ev-day-' + dataDays[y] ;
        	        moredayId='ext-cal-ev-more-' + dataDays[y] ;       	              	      
        	        if (document.getElementById(dayId)) {
        	        document.getElementById(dayId).style.cursor = 'pointer'; 
        	        }
        	        if (document.getElementById(evdayId)) {
        	        document.getElementById(evdayId).style.cursor = 'pointer';
        	        }
        	        if (document.getElementById(moredayId)) {
        	        	document.getElementById(moredayId).style.cursor = 'pointer';
    	            }
        	        
        	        for (var x = 0; x < 8; x++) {
        	            emptyId = 'app-calendar-month-empty-' + x + '-day-' + dataDays[y];
        	            if (document.getElementById(emptyId)) {
        	                document.getElementById(emptyId).style.cursor = 'pointer';
        	            }
        	        }
        	    }        	    
        	    Ext.getCmp('app-calendar').unmask();
        	}
    },
  
    getData: function() {
        return eventDatas.data;
    },
    getCheckbox: function() {
        return eventDatas.checkboxGroup;
    }

});