/*
 * This calendar application was forked from Ext Calendar Pro
 * and contributed to Ext JS as an advanced example of what can 
 * be built using and customizing Ext components and templates.
 * 
 * If you find this example to be useful you should take a look at
 * the original project, which has more features, more examples and
 * is maintained on a regular basis:
 * 
 *  http://ext.ensible.com/products/calendar
 */
Ext.define('EHR.kq.KqLeaveCal', {
    requires: ['Ext.Viewport', 'Ext.layout.container.Border', 'Ext.picker.Date', 'Ext.calendar.util.Date', 'Ext.calendar.CalendarPanel', 'Ext.calendar.data.MemoryCalendarStore', 'Ext.calendar.data.MemoryEventStore', 'Ext.calendar.data.Calendars', 'Ext.calendar.form.EventWindow', 'EHR.kq.KqEvents'],

    constructor: function() {
        leavecal = this;
        this.overRideMore();
        // Minor workaround for OSX Lion scrollbars
        this.checkScrollOffset();
     
        // This is an example calendar store that enables event color-coding
        this.calendarStore = Ext.create('Ext.calendar.data.MemoryCalendarStore', {
            data: Ext.calendar.data.Calendars.getData()
        });
        // A sample event store that loads static JSON from a local file. Obviously a real
        // implementation would likely be loading remote data via an HttpProxy, but the
        // underlying store functionality is the same.
        var Events = Ext.create('EHR.kq.KqEvents');
        this.eventStore = Ext.create('Ext.calendar.data.MemoryEventStore', {
            data: Events.getData()
        });
       // this.eventStore.reload();
  
        KqLeaveCal = Ext.create('Ext.calendar.CalendarPanel', {
            eventStore: this.eventStore,
            calendarStore: this.calendarStore,
            border: false,
            showNavBar: false,
            id: 'app-calendar',
            region: 'center',
            enableSortEventRecordsForDay: false, 
            activeItem: 3,
            // month view
            monthViewCfg: {
                showHeader: true,
                showWeekLinks: true,
                showWeekNumbers: true
            },
            listeners: {
                'eventclick': {
                    fn: function(vw, rec, el) {
                        this.showEditWindow(rec, el);
                        this.clearMsg();
                    },
                    scope: this
                },
                'eventover': function(vw, rec, el) {
                    //console.log('Entered evt rec='+rec.data.Title+', view='+ vw.id +', el='+el.id);
                },
                'eventout': function(vw, rec, el) {
                    //console.log('Leaving evt rec='+rec.data.Title+', view='+ vw.id +', el='+el.id);
                },
                'eventadd': {
                    fn: function(cp, rec) {
                        this.showMsg('Event ' + rec.data.Title + ' was added');
                    },
                    scope: this
                },
                'eventupdate': {
                    fn: function(cp, rec) {
                        win.hide();
                        this.showMsg('Event ' + rec.data.Title + ' was updated');
                    },
                    scope: this
                },
                'eventcancel': {
                    fn: function(cp, rec) {
                        // edit canceled
                    },
                    scope: this
                },
                'viewchange': {
                    fn: function(p, vw, dateInfo) {
                        // 	this.eventStore=Ext.getCmp('app-calendar').eventStore;                 
                        if (this.editWin) {
                            this.editWin.hide();
                        }
                        if (dateInfo) {
                            // will be null when switching to the event edit form so ignore
                            //Ext.getCmp('app-nav-picker').setValue(dateInfo.activeDate);
                            //this.updateTitle(dateInfo.viewStart, dateInfo.viewEnd);
                        }
                    },
                    scope: this
                },
                'dayclick': {
                    fn: function(vw, dt, ad, el) {
                        this.showEditWindow({
                            StartDate: dt,
                            IsAllDay: ad                         
                        },
                        el);
                        this.clearMsg();
                    },
                    scope: this
                },
                'rangeselect': {
                    fn: function(win, dates, onComplete) {
                        this.showEditWindow(dates);
                        // 44442 增加对象校验
                        if(this.editWin){
                        	this.editWin.on('hide', onComplete, this, {
                        		single: true
                        	});
                        }
                        this.clearMsg();
                    },
                    scope: this
                },
                'eventmove': {
                    fn: function(vw, rec) {
                        var mappings = Ext.calendar.data.EventMappings,
                        time = rec.data[mappings.IsAllDay.name] ? '': ' \\a\\t g:i a';
                        rec.commit();
                        this.showMsg('Event ' + rec.data[mappings.Title.name] + ' was moved to ' + Ext.Date.format(rec.data[mappings.StartDate.name], ('F jS' + time)));
                    },
                    scope: this
                },
                'eventresize': {
                    fn: function(vw, rec) {
                        rec.commit();
                        this.showMsg('Event' + rec.data.Title + ' was updated');
                    },
                    scope: this
                },
                'eventdelete': {
                    fn: function(vw, rec) {
                        this.eventStore.removed = rec;
                        this.eventStore.sync();
                    },
                    scope: this
                },
                'initdrag': {
                    fn: function(vw) {
                        if (this.editWin && this.editWin.isVisible()) {
                            this.editWin.hide();
                        }
                    },
                    scope: this
                }
            }
        });

        // This is the app UI layout code.  All of the calendar views are subcomponents of
        // CalendarPanel, but the app title bar and sidebar/navigation calendar are separate
        // pieces that are composed in app-specific layout code since they could be omitted
        // or placed elsewhere within the application.
        var checkBox = Events.getCheckbox();
        var Viewp = Ext.create('Ext.Viewport', {
            layout: 'border',
            id: 'Viewports',
            border: false,
            items: [{
                xtype: 'panel',
                id: 'app-header',
                border: false,
                region: 'north',
                border: false,
                items: [{
                    xtype: 'toolbar',
                    id: 'app-header-tools',
                    border: false,                                      
                    items: [
                    	"<span style='cursor:pointer;font-size: 22px;color:#979797;' onclick='leavecal.butttomLeft();'> < </span>" +
                        "<span id='dateText' style='font-size: 22px;color:#FF4474;'>" + Ext.util.Format.date(KqLeaveCal.startDate, 'Y-m') + "</span>" +
    					"<span style='cursor:pointer;font-size: 22px;color:#979797;' onclick='leavecal.butttomRight();'> > </span>",                                        	
                        "->", checkBox, "-",
                    {
                    	xtype: 'checkboxfield',
                    	id: 'subordinate',
                    	checked: false,
                    	padding: '0 5 0 10',
                    	boxLabel: "包含下属的下属",
                    	inputValue: "1",
                    	listeners: {
                            'change': function(el, checked) {
                                Ext.getCmp('app-calendar').mask('Loading....');                              
                                setTimeout(function() {
                                	leavecal.setData();
                                },1);                          
                            }
                        }
            		}]
                }]
            },
            {
                id: 'app-center',
                //title: 'aaa', // will be updated to the current view's date range
                region: 'center',
                layout: 'border',
                border: false,
                listeners: {
                    'afterrender': function() {
                        //Ext.getCmp('app-center').header.addCls('app-center-header');
                    }
                },
                items: [KqLeaveCal]
            }]       
        });
        var dataList = this.eventStore.config.data.evts;
        var dataDays = new Array();	   
	    for (var i = 0; i < dataList.length; i++) {
	        if (dataList[i]) {
	            var startTime = dataList[i].start.substring(0, 10);
	            var endTime = dataList[i].end.substring(0, 10);
	            startTime = startTime.replace(/-/g, "");       	          
	            endTime = endTime.replace(/-/g, "");  
	            var dayTime = startTime
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
    },

    // The edit popup window is not part of the CalendarPanel itself -- it is a separate component.
    // This makes it very easy to swap it out with a different type of window or custom view, or omit
    // it altogether. Because of this, it's up to the application code to tie the pieces together.
    // Note that this function is called from various event handlers in the CalendarPanel above.
    //鐐瑰嚮缂栬緫浜嬩欢
    showEditWindow: function(rec, animateTarget) {     
        this.gridWindow(rec.StartDate,rec.data);        
    },

    // The CalendarPanel itself supports the standard Panel title config, but that title
    // only spans the calendar views.  For a title that spans the entire width of the app
    // we added a title to the layout's outer center region that is app-specific. This code
    // updates that outer title based on the currently-selected view range anytime the view changes.
    updateTitle: function(startDt, endDt) {
        var p = Ext.getCmp('app-center'),
        fmt = Ext.Date.format;

        if (Ext.Date.clearTime(startDt).getTime() === Ext.Date.clearTime(endDt).getTime()) {
            p.setTitle(fmt(startDt, 'F j, Y'));
        } else if (startDt.getFullYear() === endDt.getFullYear()) {
            if (startDt.getMonth() === endDt.getMonth()) {
                p.setTitle(fmt(startDt, 'F j') + ' - ' + fmt(endDt, 'j, Y'));
            } else {
                p.setTitle(fmt(startDt, 'F j') + ' - ' + fmt(endDt, 'F j, Y'));
            }
        } else {
            p.setTitle(fmt(startDt, 'F j, Y') + ' - ' + fmt(endDt, 'F j, Y'));
        }
    },

    // This is an application-specific way to communicate CalendarPanel event messages back to the user.
    // This could be replaced with a function to do "toast" style messages, growl messages, etc. This will
    // vary based on application requirements, which is why it's not baked into the CalendarPanel.
    showMsg: function(msg) {
        //   Ext.fly('app-msg').update(msg).removeCls('x-hidden');
    },
    clearMsg: function() {
        // Ext.fly('app-msg').update('').addCls('x-hidden');
    },

    // OSX Lion introduced dynamic scrollbars that do not take up space in the
    // body. Since certain aspects of the layout are calculated and rely on
    // scrollbar width, we add a special class if needed so that we can apply
    // static style rules rather than recalculate sizes on each resize.
    checkScrollOffset: function() {
        var scrollbarWidth = Ext.getScrollbarSize ? Ext.getScrollbarSize().width: Ext.getScrollBarWidth();
        // We check for less than 3 because the Ext scrollbar measurement gets
        // slightly padded (not sure the reason), so it's never returned as 0.
        if (scrollbarWidth < 3) {
            Ext.getBody().addCls('x-no-scrollbar');
        }

        if (Ext.isWindows) {
            Ext.getBody().addCls('x-win');
        }
    },
  
    gridWindow: function(dt, recdata) {
        var name;
        if (this.editWin && !this.editWin.isHidden()) {
            name = this.editWin.name;
            this.editWin.close();
            if (dt) {
                if (name == Ext.util.Format.date(dt, 'Y-m-d')) {
                    return;
                }
            } else {
                if (name == recdata.Title + recdata.start) {
                    return;
                }
            }
        }
        if (this.editWin) {
        	this.editWin.close();
        }
        var data = [];
        var store = Ext.create('Ext.data.Store', {
            storeId: 'simpsonsStore',
            fields: ['a0101', 'typeName', 'reason', 'beginTime', 'endTime', 'timeLen'],
        });
        if (dt) {
            var dataList = this.eventStore.config.data.evts;
            var exist = false;
            for (var i = 0; i < dataList.length; i++) {
                if (dataList[i]) {
                    data = [{
                        'a0101': dataList[i].title,
                        'typeName': dataList[i].typeName,
                        'reason': dataList[i].reason,
                        'beginTime': dataList[i].start,
                        'endTime': dataList[i].end,
                        'Q15Z5': dataList[i].Q15Z5,
                        'timeLen': dataList[i].timeLen
                    }];
                    var dayTime = Ext.util.Format.date(dt, 'Y-m-d');
                    var startTime = dataList[i].start.substring(0, 10);
                    var endTime = dataList[i].end.substring(0, 10);

                    if (startTime <= dayTime && dayTime <= endTime) {
                        store.loadData(data, true);
                        exist = true;
                    }
                }
            }
            name = Ext.util.Format.date(dt, 'Y-m-d');
            if (!exist)
            	return false;
        } else {
            if (recdata) {
                data = [{
                    'a0101': recdata.Title,
                    'typeName': recdata.typeName,
                    'reason': recdata.reason,
                    'beginTime': recdata.start,
                    'endTime': recdata.end,
                    'Q15Z5': recdata.Q15Z5,
                    'timeLen': recdata.timeLen
                }];
                store.loadData(data, true);
                name = recdata.Title + recdata.start;
            }
        }

        Ext.tip.QuickTipManager.init();

        var grid = Ext.create('Ext.grid.Panel', {
            store: store,
            scroll: false,
            viewConfig: {
                style: {
                    overflow: 'auto',
                    overflowX: 'hidden'
                }
            },
            columns: [{
                text: '姓名',
                width: 100,
                dataIndex: 'a0101',
                type: 'string'
            },
            {
                text: '请假类型',
                width: 70,
                dataIndex: 'typeName',
                type: 'string'
            },
            {
                text: '请假事由',
                width: 200,
                dataIndex: 'reason',
                type: 'string',
                renderer: function(value, metaData, record, colIndex, store, view) {
                    if (value.length > 15) {
                        metaData.tdAttr = 'data-qtip="' + value + '"';
                    }
                    return value;
                }
            },
            {
                text: '请假开始时间',
                width: 120,
                dataIndex: 'beginTime',
                type: 'string'
            },
            {
                text: '请假结束时间',
                width: 120,
                dataIndex: 'endTime',
                type: 'string'
            },
            {
                text: '请假时长（天）',
                // align:'right',              
                width: 110,
                dataIndex: 'timeLen',
                type: 'float'
            },
            {
                text: '审批标志',
                width: 70,
                dataIndex: 'Q15Z5',
                type: 'string'
            }],
            rowLines: true,
            columnLines: true,
            bodyStyle: 'overflow-x:hidden',
            maxHeight: 200,
            width: 790
        });

        this.editWin = Ext.create('Ext.window.Window', {
            id: 'showDataWin',
            name: name,
            title: '请假明细 ' + Ext.util.Format.date(dt, 'Y-m-d'),
            autoHeight: true,
            autoWidth: true,
            resizable: false,
            border: false,
            items: [grid]
        });
        this.editWin.show();
    },


 
    setData: function() {
        var holidayCheck = Ext.getCmp('myGroups').items;
        var holiday = '';
        for (var i = 0; i < holidayCheck.length; i++) {
            if (holidayCheck.get(i).checked) {
                holiday += ',';
                holiday += holidayCheck.get(i).inputValue;
            }
        };
        holiday = holiday.substring(1);
        if (Ext.getCmp('subordinate').checked) {
            eventDatas.getDatas(holiday, "2", document.getElementById('dateText').innerHTML);
        } else {
            eventDatas.getDatas(holiday, "1", document.getElementById('dateText').innerHTML);
        }
    },

   butttomRight: function() {   
	    Ext.getCmp('app-calendar').mask('Loading....');  
        setTimeout(function() {
            KqLeaveCal.onNextClick();
            var date = Ext.util.Format.date(KqLeaveCal.layout.getActiveItem().getStartDate(), 'Y-m');
            document.getElementById('dateText').innerHTML = date;
            leavecal.setData();
        },
        1);
    },  
    
    butttomLeft: function() {
        Ext.getCmp('app-calendar').mask('Loading....');       
        setTimeout(function() {
            KqLeaveCal.onPrevClick();
            var date = Ext.util.Format.date(KqLeaveCal.layout.getActiveItem().getStartDate(), 'Y-m');
            document.getElementById('dateText').innerHTML = date;
            leavecal.setData();
        },
        1);          
    },
    
    overRideMore: function() {
        Ext.override(Ext.calendar.view.Month, {
            onMoreClick: function(dt) {            
            	leavecal.gridWindow(dt,'');              
            }
        });
    }
});