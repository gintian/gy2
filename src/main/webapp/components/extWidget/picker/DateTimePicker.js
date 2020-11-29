/** create by guodd
 * 2014-10-23
 *
 * 根据需求自定义一些ext组件
 *
 * 1.自定义DateTime选择器：
 * 带时间选择的日历选择器
 */
Ext.define('EHR.extWidget.picker.DateTimePicker', {
    extend: 'Ext.picker.Date',
    alias: 'widget.datetimepicker',
    okText:'确定',
    todayTip:'',
    //复写 tpl 添加 时分秒 和 确定按钮 渲染
    renderTpl: [
        '<div id="{id}-innerEl" data-ref="innerEl" role="presentation">',
            '<div class="{baseCls}-header">',
                '<div id="{id}-prevEl" data-ref="prevEl" class="{baseCls}-prev {baseCls}-arrow" role="presentation" title="{prevText}"></div>',
                '<div id="{id}-middleBtnEl" data-ref="middleBtnEl" class="{baseCls}-month" role="heading">{%this.renderMonthBtn(values, out)%}</div>',
                '<div id="{id}-nextEl" data-ref="nextEl" class="{baseCls}-next {baseCls}-arrow" role="presentation" title="{nextText}"></div>',
            '</div>',
            '<table role="grid" id="{id}-eventEl" data-ref="eventEl" class="{baseCls}-inner" cellspacing="0" tabindex="0">',
                '<thead>',
                    '<tr role="row">',
                        '<tpl for="dayNames">',
                            '<th role="columnheader" class="{parent.baseCls}-column-header" aria-label="{.}">',
                                '<div role="presentation" class="{parent.baseCls}-column-header-inner">{.:this.firstInitial}</div>',
                            '</th>',
                        '</tpl>',
                    '</tr>',
                '</thead>',
                '<tbody>',
                    '<tr role="row">',
                        '<tpl for="days">',
                            '{#:this.isEndOfWeek}',
                            '<td role="gridcell">',
                                '<div hidefocus="on" class="{parent.baseCls}-date"></div>',
                            '</td>',
                        '</tpl>',
                    '</tr>',
                '</tbody>',
            '</table>',
            
            //指定时分秒渲染框架
            '<table id="{id}-timeEl" style="table-layout:auto;width:auto;margin-bottom:1px" class="x-datepicker-inner"  cellpadding="0" cellspacing="0">',
                '<tbody><tr>',
                    '<td nowrap >{%this.renderHourBtn(values,out)%}</td>',
                    '<td >{%this.renderMinuteBtn(values,out)%}</td>',
                    '<td >{%this.renderSecondBtn(values,out)%}</td>',
                '</tr></tbody>',
            '</table>',
            
            '<tpl if="showToday">',
                '<div id="{id}-footerEl" data-ref="footerEl" role="presentation" class="{baseCls}-footer">',
                		'{%this.renderOkBtn(values, out)%}{%this.renderTodayBtn(values, out)%}',
                	'</div>',
            '</tpl>',
            // These elements are used with Assistive Technologies such as screen readers
            '<div id="{id}-todayText" class="' + Ext.baseCSSPrefix + 'hidden-clip">{todayText}.</div>',
            '<div id="{id}-ariaMinText" class="' + Ext.baseCSSPrefix + 'hidden-clip">{ariaMinText}.</div>',
            '<div id="{id}-ariaMaxText" class="' + Ext.baseCSSPrefix + 'hidden-clip">{ariaMaxText}.</div>',
            '<div id="{id}-ariaDisabledDaysText" class="' + Ext.baseCSSPrefix + 'hidden-clip">{ariaDisabledDaysText}.</div>',
            '<div id="{id}-ariaDisabledDatesText" class="' + Ext.baseCSSPrefix + 'hidden-clip">{ariaDisabledDatesText}.</div>',
        '</div>',
        {
            firstInitial: function(value) {
                return Ext.picker.Date.prototype.getDayInitial(value);
            },
            isEndOfWeek: function(value) {
                // convert from 1 based index to 0 based
                // by decrementing value once.
                value--;
                var end = value % 7 === 0 && value !== 0;
                return end ? '</tr><tr role="row">' : '';
            },
            renderTodayBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.todayBtn.getRenderTree(), out);
            },
            renderMonthBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.monthBtn.getRenderTree(), out);
            },
            renderHourBtn:function(values,out){
            		Ext.DomHelper.generateMarkup(values.$comp.hourBtn.getRenderTree(), out);
            		Ext.DomHelper.generateMarkup(values.$comp.hourLabel.getRenderTree(), out);// 添加时 label 控件 渲染 29351 wangb 20170718
            },
            renderMinuteBtn:function(values,out){
            		Ext.DomHelper.generateMarkup(values.$comp.minuteBtn.getRenderTree(), out);
            		Ext.DomHelper.generateMarkup(values.$comp.minuteLabel.getRenderTree(), out);// 添加分 label 控件 渲染 29351 wangb 20170718
            },
            renderSecondBtn:function(values,out){
            		Ext.DomHelper.generateMarkup(values.$comp.secondBtn.getRenderTree(), out);
            		Ext.DomHelper.generateMarkup(values.$comp.secondLabel.getRenderTree(), out);// 添加秒 label 控件 渲染 29351 wangb 20170718
            },
            renderOkBtn:function(values,out){
            		Ext.DomHelper.generateMarkup(values.$comp.okBtn.getRenderTree(), out);
            }
        }
    ],
    
    initComponent:function(){
       this.callParent(arguments);
       var me = this,
          hourHide = true,
          minHide = true,
          secHide = true,
          btnWidth;// 设置时分秒 控件的 宽度   wangb  20170718  29351
          me.showTime = false;
       if(this.format.toUpperCase().indexOf(':S')>-1){
          secHide = false;
          hourHide = false;
          minHide = false;
          me.showTime = true;
          btnWidth = '70%'; // 显示 时分秒 控件时 宽度设置  wangb  20170718  29351
       }else if(this.format.toUpperCase().indexOf('H:I')>-1){
          hourHide = false;
          minHide = false;
          me.showTime = true;
		  btnWidth = '80%';// 显示 时分 控件时 宽度设置  wangb  20170718  29351
       }
       
       
       me.hourBtn=Ext.widget('numberfield',{
        		ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            minValue:0,
            maxValue:23,
            style:'float:left',
            hidden:hourHide,
            step:1,
            width:btnWidth,// 时  控件 宽度属性   wangb 20170718 29351
            border:1,
            listeners:{
               render:function(){
                   this.getEl().on("click",function(){this.focus();},this);
               }
            }
        });
       // 提示小时的  label控件  wangb 20170718 29351 
       me.hourLabel = Ext.widget("label",{text:'时',hidden:hourHide,width:10,style:'float:left;marginTop:3px;marginLeft:3px'});
       
       me.minuteBtn=Ext.widget('numberfield',{
        		ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            minValue:0,
            maxValue:59,
            style:'float:left',
            hidden:minHide,
            step:1,
            width:btnWidth,// 分  控件 宽度属性   wangb 20170718 29351
            listeners:{
               render:function(){
                   this.getEl().on("click",function(){this.focus();},this);
               }
            }
        });
       // 提示分钟的  label控件  wangb 20170718 29351 
       me.minuteLabel = Ext.widget("label",{text:'分',hidden:minHide,width:10,style:'float:left;marginTop:3px;marginLeft:3px'});
        
       me.secondBtn=Ext.widget('numberfield',{
        		ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            minValue:0,
            maxValue:59,
            hidden:secHide,
            step:1,
            width:btnWidth,// 秒  控件 宽度属性   wangb 20170718 29351
            style:'float:left',
            listeners:{
               render:function(){
                   this.getEl().on("click",function(){this.focus();},this);
               }
            }
        });
        // 提示秒的  label控件  wangb 20170718 29351
	    me.secondLabel = Ext.widget("label",{text:'秒',hidden:secHide,width:10,style:'float:left;marginTop:3px;marginLeft:3px'});
        
        me.okBtn = new Ext.button.Button({
            ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            text: me.okText,
            hidden:!me.showTime,
            tooltip: me.okTip,
            tooltipType:'title',
            handler:me.okHandler,//确认按钮的事件委托
            scope: me
        });
    },
    
    finishRenderChildren: function () {
        var me = this;
        //组件渲染完成后，需要调用子元素的finishRender，从而获得事件绑定
        me.hourBtn.finishRender();
        me.hourLabel.finishRender();// 小时  label 控件  页面是否显示  wangb 20170718
        me.minuteBtn.finishRender();
        me.minuteLabel.finishRender();// 分  label 控件  页面是否显示  wangb 20170718
        me.secondBtn.finishRender();
        me.secondLabel.finishRender();// 秒  label 控件  页面是否显示  wangb 20170718
        me.okBtn.finishRender();
        me.callParent();
    },
    
    /**
     * 确认 按钮触发的调用
     */
    okHandler : function(){
        var me = this,
            btn = me.okBtn;

        if(btn && !btn.disabled){
            me.setValue(this.getValue());
            me.fireEvent('select', me, me.value);
            me.onSelect();
        }
        return me;
    },
    onRender: function(container, position) {
        this.callParent(arguments);
        this.mon(this.eventEl, {
            scope: this,
            dblclick:{
            		fn: this.handleDateClick,
            		delegate: 'div.' + this.baseCls + '-date'
            }
        });
    },
    handleDateClick : function(e, t){
        var me = this,
            handler = me.handler;
        e.stopEvent();
        if(!me.disabled && t.dateValue && !Ext.fly(t.parentNode).hasCls(me.disabledCellCls)){
            me.setValue(new Date(t.dateValue));
            //如果需要选择时间，那么单击日期不触发日期选择事件，因为还要选时间。如果双击日期则触发日期选择事件
            //这里进行判断，如果不显示时间，直接触发。如果是双击，也直接触发
            if(!me.showTime || e.type=='dblclick')
               me.fireEvent('select', me, me.value);
            if (handler) {
                handler.call(me.scope || me, me, me.value);
            }
            me.onSelect();
        }
    },
    
    /**
     * 从picker选中后，赋值时，需要从时、分、秒也获得当前值
     * datetimefield也会调用这个方法对picker初始化，因此添加一个isfixed参数。
     * @param {Date} date The new date
     * @param {Boolean} isfixed True 时，忽略从时分秒中获取值
    */
    setValue : function(date, isfixed){
        var me = this;
        if(isfixed!==true){
        	/*添加时间数据格式校验 guodd 2019-08-01*/
        	var hour = me.hourBtn.getValue();
        	var min = me.minuteBtn.getValue();
        	var sec = me.secondBtn.getValue();
        	
        	if(!Ext.isNumber(hour))
        		hour = 0;
        	if(!Ext.isNumber(min))
        		min = 0;
        	if(!Ext.isNumber(sec))
        		sec = 0;
        	hour = hour>23?23:hour;
        	min = min>59?59:min;
        	sec = sec>59?59:sec;
        	
            date.setHours(hour);
            date.setMinutes(min);
            date.setSeconds(sec);
        }
        me.value=date;
        me.update(me.value);
        return me;
    },
    
    /**
     * 更新picker的显示内容，需要同时更新时、分、秒输入框的值
     * @private
     * @param {Date} date The new date
     * @param {Boolean} forceRefresh True to force a full refresh
     */
    update : function(date, forceRefresh){
        var me = this;
        me.hourBtn.setValue(date.getHours());
        me.minuteBtn.setValue(date.getMinutes());
        me.secondBtn.setValue(date.getSeconds());
        return this.callParent(arguments);
    },
    
    /**
     * 覆盖了父类的方法，因为父类中是根据时间的getTime判断的，因此需要对时、分、秒分别值为0才能保证当前值的日期选择
     * @private
     * @param {Date} date The new date
     */
    selectedUpdate: function(date){
        this.callParent([Ext.Date.clearTime(date,true)]);
    },
    
    /**
     * @private
     */
    selectToday : function(){
        var me = this,
            btn = me.todayBtn,
            handler = me.handler;

        if(btn && !btn.disabled){
            //me.setValue(Ext.Date.clearTime(new Date()));//clearTime会清掉时分秒，直接采用new Date()
          	me.setValue(new Date(), true);
            me.fireEvent('select', me, me.value);
            if (handler) {
                handler.call(me.scope || me, me, me.value);
            }
            me.onSelect();
        }
        return me;
    },
    
    
    beforeDestroy : function() {
        var me = this;

        if (me.rendered) {
            //销毁组件时，也需要销毁自定义的控件
            Ext.destroy(
                me.hourBtn,
                me.minuteBtn,
                me.secondBtn,
                me.okBtn
            );
        }
        me.callParent();
    },
    
    initEvents: function() {
        var me = this,
            pickerField = me.pickerField,
            eDate = Ext.Date,
            day = eDate.DAY,
            afterRenderEvents = me.afterRenderEvents,
            afterRenderEvent, el, property, index, len;

        if (afterRenderEvents) {
            for (property in afterRenderEvents) {
                el = me[property];

                if (el && el.on) {
                    afterRenderEvent = afterRenderEvents[property];

                    for (index = 0, len = afterRenderEvent.length ; index < len ; ++index) {
                        me.mon(el, afterRenderEvent[index]);
                     }
                 }
            }
        }
        
        if (me.focusable) {
            me.initFocusableEvents();
        }

        if (pickerField) {
            me.el.on('mousedown', me.onMouseDown, me);
        }

        // Month button is pointer interactive only, it should not be allowed to focus.
        me.monthBtn.el.on('mousedown', me.onMouseDown, me);

        me.prevRepeater = new Ext.util.ClickRepeater(me.prevEl, {
            handler: me.showPrevMonth,
            scope: me,
            mousedownStopEvent: true
        });

        me.nextRepeater = new Ext.util.ClickRepeater(me.nextEl, {
            handler: me.showNextMonth,
            scope: me,
            mousedownStopEvent: true
        });

        me.keyNav = new Ext.util.KeyNav(me.eventEl, Ext.apply({
            scope: me,

            left: function(e) {
                if (e.ctrlKey) {
                    e.preventDefault();
                    me.showPrevMonth();
                } else {
                    me.update(eDate.add(me.activeDate, day, -1));
                }
            },

            right: function(e){
                if (e.ctrlKey) {
                    e.preventDefault();
                    me.showNextMonth();
                } else {
                    me.update(eDate.add(me.activeDate, day, 1));
                }
            },

            up: function(e) {
                if (e.ctrlKey) {
                    me.showNextYear();
                } else {
                    me.update(eDate.add(me.activeDate, day, -7));
                }
            },

            down: function(e) {
                if (e.ctrlKey) {
                    me.showPrevYear();
                } else {
                    me.update(eDate.add(me.activeDate, day, 7));
                }
            },

            pageUp: function(e) {
                if (e.ctrlKey) {
                    me.showPrevYear();
                } else {
                    me.showPrevMonth();
                }
            },

            pageDown: function(e) {
                if (e.ctrlKey) {
                    me.showNextYear();
                } else {
                    me.showNextMonth();
                }
            },

            tab: function(e) {
                // When the picker is floating and attached to an input field, its
                // 'select' handler will focus the inputEl so when navigation happens
                // it does so as if the input field was focused all the time.
                // This is the desired behavior and we try not to interfere with it
                // in the picker itself, see below.
                me.handleTabKey(e);
                
                // Allow default behaviour of TAB - it MUST be allowed to navigate.
                return true;
            },

            enter: function(e) {
                me.handleDateClick(e, me.activeCell.firstChild);
            },

            space: function(e) {
                e.stopEvent();
                /*me.setValue(new Date(me.activeCell.firstChild.dateValue));
                var startValue = me.startValue,
                    value = me.value,
                    pickerValue;

                if (pickerField) {
                    pickerValue = pickerField.getValue();
                    if(pickerValue){
                    		pickerValue  = Ext.Date.parse(pickerValue, me.format);
                    }
                    if (pickerValue && startValue && pickerValue.getTime() === value.getTime()) {
                        pickerField.setValue(startValue);
                    } else {
                        pickerField.setValue(value);
                    }
                }*/
                me.selectToday();
            },

            home: function(e) {
                me.update(eDate.getFirstDateOfMonth(me.activeDate));
            },

            end: function(e) {
                me.update(eDate.getLastDateOfMonth(me.activeDate));
            }
        }, me.keyNavConfig));

        if (me.disabled) {
            me.syncDisabled(true, true);
        }
        me.update(me.value);
    },
    
    onMouseDown: function(e) {
        if(this.hourBtn.owns(e.target) || this.minuteBtn.owns(e.target) || this.secondBtn.owns(e.target))
           return;
        e.preventDefault();
    }
 });
