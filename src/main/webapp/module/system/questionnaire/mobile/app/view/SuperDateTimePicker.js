Ext.define('Questionnaire.view.SuperDateTimePicker', {
     extend: 'Ext.picker.Picker',
     xtype: 'superdatetimepicker',
     alternateClassName: 'superdatetimepicker',
     alias:'widget.superdatetimepicker',
     /**
     * @event change
     * Fired when the value of this picker has changed and the done button is pressed.
     * @param {Ext.picker.Date} this This Picker
     * @param {Date} value The date value
     */
     config: {
          /**
          * @cfg {Number} yearFrom
          * 开始年份,如果他比yearTo大，则选择顺序颠倒
          * @accessor
          */
          yearFrom: (new Date().getFullYear()-100),
          /**
          * @cfg {Number} [yearTo=new Date().getFullYear()]
          * 结束年份
          * @accessor
          */
          yearTo:(new Date().getFullYear()+100),
          /**
          * @cfg {String} monthText
          * 月显示值
          * @accessor
          */
          monthText: '月',
          /**
          * @cfg {String} dayText
          * 日显示值
          * @accessor
          */
          dayText: '日',
          /**
          * @cfg {String} yearText
          * 年显示值
          * @accessor
          */
          yearText: '年',
          /**
          * @cfg {Array} slotOrder
          * 默认的选项列表
          * @accessor
          */
          slotOrder: ['year', 'month', 'day'], //
          useTitles: true,
          cancelButton:'取消',
		  doneButton:'确定'
      },
      platformConfig: [{
          theme: ['Windows']
      }],
      initialize: function () {
         this.callParent();
         this.on({
             scope: this,
             delegate: '> slot',
             slotpick: this.onSlotPick
         });
     },
     setValue: function (value, animated) {
         if (Ext.isDate(value)) {
             value = {
             	year: value.getFullYear()-1,
             	month: value.getMonth(),
                day: value.getDate()-1
             };
         }
         this.callParent([value, animated]);
         this.onSlotPick();
     },
     /***
     //获取值
     getValue: function (useDom) {
         var values = {},
             items = this.getItems().items,
             ln = items.length,
             daysInMonth, day, month, year, item, i;
         for (i = 0; i < ln; i++) {
             item = items[i];
             if (item instanceof Ext.picker.Slot) {
                 values[item.getName()] = item.getValue(useDom);
             }
         }

         //if all the slots return null, we should not return a date

         if (values.year === null && values.month === null && values.day === null) {
             return null;
         }
         year = Ext.isNumber(values.year) ? values.year : 1;
         month = Ext.isNumber(values.month) ? values.month : 1;
         day = Ext.isNumber(values.day) ? values.day : 1;

         if (month && year && month && day) {
            daysInMonth = this.getDaysInMonth(month, year);
         }

         day = (daysInMonth) ? Math.min(day, daysInMonth) : day;
		 
         return new Date(year, month - 1, day);
     },
     ****/
     /**
     * Updates the yearFrom configuration
     */
     updateYearFrom: function () {
         if (this.initialized) {
             this.createSlots();
         }
     },
     /**
     * Updates the yearTo configuration
     */
     updateYearTo: function () {
         if (this.initialized) {
             this.createSlots();
         }
     },
     /**
     * Updates the monthText configuration
     */
     updateMonthText: function (newMonthText, oldMonthText) {
         var innerItems = this.getInnerItems,
             ln = innerItems.length,
             item, i;
         //loop through each of the current items and set the title on the correct slice
         if (this.initialized) {
             for (i = 0; i < ln; i++) {
                 item = innerItems[i];
                 if ((typeof item.title == "string" && item.title == oldMonthText) || (item.title.html == oldMonthText)) {
                     item.setTitle(newMonthText);
                 }
             }
         }
     },
     /**
     * Updates the {@link #dayText} configuration.
     */
     updateDayText: function (newDayText, oldDayText) {
         var innerItems = this.getInnerItems,
             ln = innerItems.length,
             item, i;
         //loop through each of the current items and set the title on the correct slice
         if (this.initialized) {
             for (i = 0; i < ln; i++) {
                 item = innerItems[i];
                 if ((typeof item.title == "string" && item.title == oldDayText) || (item.title.html == oldDayText)) {
                     item.setTitle(newDayText);
                 }
             }
         }
     },
     /**
     * Updates the yearText configuration
     */
     updateYearText: function (yearText) {
         var innerItems = this.getInnerItems,
             ln = innerItems.length,
             item, i;
         //loop through each of the current items and set the title on the correct slice
         if (this.initialized) {
             for (i = 0; i < ln; i++) {
                 item = innerItems[i];
                 if (item.title == this.yearText) {
                     item.setTitle(yearText);
                 }
             }
         }
     },
     // @private
     constructor: function () {
         this.callParent(arguments);
         this.createSlots();
     },
     /**
     * Generates all slots for all years specified by this component, and then sets them on the component
     * @private
     */
     createSlots: function () {
         var me = this,
             slotOrder = me.getSlotOrder(),
             yearsFrom = me.getYearFrom(),
             yearsTo = me.getYearTo(),
             years = [],
             days = [],
             months = [],
             reverse = yearsFrom > yearsTo,
             ln, i, daysInMonth;
         //填充年列表
         while (yearsFrom) {
             years.push({
                 text: yearsFrom,
                 value: yearsFrom
             });
             if (yearsFrom === yearsTo) {
                 break;
             }
             if (reverse) {
                 yearsFrom--;
             } else {
                 yearsFrom++;
             }
         }
         //填充天列表
         daysInMonth = me.getDaysInMonth(1, new Date().getFullYear());
         for (i = 0; i < daysInMonth; i++) {
             days.push({
                 text: i+1,
                 value: i+1
             });
         }
         //填充月列表
         for (i = 0, ln = Ext.Date.monthNames.length; i < ln; i++) {
             months.push({
                 text: Ext.Date.monthNames[i],
                 value: i+1
             });
         }
         var slots = [];
         slotOrder.forEach(function (item) {
         	 var slot = me.createSlot(item, days, months, years);
         	 //slot.setValue();
             slots.push(slot);
         });
         me.setSlots(slots);
     },
     /**
     * Returns a slot config for a specified date.
     * @private
     */
     createSlot: function (name, days, months, years) {
         switch (name) {
             case 'year':
                 return {
                     name: 'year',
                     align: 'center',
                     data: years,
                     title: this.getYearText(),
                     flex: 3
                 };
             case 'month':
                 return {
                     name: name,
                     align: 'center',
                     data: months,
                     title: this.getMonthText(),
                     flex: 4
                 };
             case 'day':
                 return {
                     name: 'day',
                     align: 'center',
                     data: days,
                     width: '1px',
                     title: this.getDayText(),
                     flex: 2
                 };
         }
     },
     onSlotPick: function () {	
         var value = this.getValue(true),
             slot = this.getDaySlot(),
             year = value.year,
             month = value.month,
             days = [],
             daysInMonth, i;
         if (!value || !Ext.isDate(value) || !slot) {
             return;
         }
         this.callParent(arguments);
         //get the new days of the month for this new date
         daysInMonth = this.getDaysInMonth(month + 1, year);
         for (i = 0; i < daysInMonth; i++) {
             days.push({
                 text: i+1,
                 value: i+1
             });
         }
         // We don't need to update the slot days unless it has changed
         if (slot.getStore().getCount() == days.length) {
             return;
         }
         slot.getStore().setData(days);
         // Now we have the correct amount of days for the day slot, lets update it
         var store = slot.getStore(),
             viewItems = slot.getViewItems(),
             valueField = slot.getValueField(),
             index, item;
         index = store.find(valueField, value.getDate());
         if (index == -1) {
             return;
         }
         item = Ext.get(viewItems[index]);
         slot.selectedIndex = index;
         slot.scrollToItem(item);
         slot.setValue(slot.getValue(true));
     },
     getDaySlot: function () {
         var innerItems = this.getInnerItems(),
             ln = innerItems.length,
             i, slot;
         if (this.daySlot) {
             return this.daySlot;
         }
         for (i = 0; i < ln; i++) {
             slot = innerItems[i];
             if (slot.isSlot && slot.getName() == "day") {
                 this.daySlot = slot;
                 return slot;
             }
         }
         return null;
     },
     // @private
     getDaysInMonth: function (month, year) {
         var daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
         return month == 2 && this.isLeapYear(year) ? 29 : daysInMonth[month - 1];
     },
     // @private
     isLeapYear: function (year) {
         return !!((year & 3) === 0 && (year % 100 || (year % 400 === 0 && year)));
     },
     onDoneButtonTap: function () {
         var oldValue = this._value,
         	 value = this.getValue(true),
             newValue = new Date(parseInt(value.year,10), parseInt(value.month - 1,10), parseInt(value.day,10)),
             testValue = newValue;
         if (Ext.isDate(newValue)) {
             testValue = newValue.toDateString();
         }
         if (Ext.isDate(oldValue)) {
             oldValue = oldValue.toDateString();
         }
         if (testValue != oldValue) {
             this.fireEvent('change', this, newValue);
         }
         this.hide();
         if(this.inputBlocker)
        	this.inputBlocker.unblockInputs();
     },
     pad2: function (number) {
         return (number < 10 ? '0' : '') + number;
     }
 });