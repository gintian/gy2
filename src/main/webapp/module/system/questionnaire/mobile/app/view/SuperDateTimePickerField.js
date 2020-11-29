 Ext.define('Questionnaire.view.SuperDateTimePickerField', {
       extend: 'Ext.field.Text',
       alternateClassName: 'superdatetimepickerfield',
       xtype:'superdatetimepickerfield',
       requires: [
           'Questionnaire.view.SuperDateTimePicker'
       ],
      /**
      * @event change
      * Fires when a date is selected
      * @param {Ext.field.DatePicker} this
      * @param {Date} newDate The new date
      * @param {Date} oldDate The old date
      */
      config: {
          ui: 'select',
          /**
          * @cfg {Object/superdatetimepickerfield} picker
          * An object that is used when creating the internal {@link superdatetimepickerfield} component or a direct instance of {@link superdatetimepickerfield}.
          * @accessor
          */
          picker: true,
          /**
          * @cfg {Boolean}
          * @hide
          * @accessor
          */
          clearIcon: false,
          /**
          * @cfg {Object/Date} value
          * Default value for the field and the internal {@link superdatetimepickerfield} component. Accepts an object of 'year',
          * 'month' and 'day' values, all of which should be numbers, or a {@link Date}.
          * Example: {year: 1989, day: 1, month: 5} = 1st May 1989 or new Date()
          * @accessor
          */
          /**
          * @cfg {Boolean} destroyPickerOnHide
          * @accessor
          */
          destroyPickerOnHide: false,
          /**
          */
          dateFormat: 'Y-m-d',
          /**
          * @cfg {Object}
          * @hide
          */
          component: {
              useMask: true
          },
          placeHolder:'不填默认为不知道该选项'
      },
      initialize: function () {
          var me = this,
              component = me.getComponent();
          me.callParent();
          component.on({
              scope: me,
              masktap: 'onMaskTap'
          });
          component.doMaskTap = Ext.emptyFn;
          if (Ext.browser.is.AndroidStock2) {
              component.input.dom.disabled = true;
          }
      },
      syncEmptyCls: Ext.emptyFn,
      applyValue: function (value) {
          if (!Ext.isDate(value) && !Ext.isObject(value)) {
              return null;
          }
          if (Ext.isObject(value)) {
              return new Date(value.year, value.month - 1, value.day);
          }
          return value;
      },
     updateValue: function (newValue, oldValue) {
         var me = this,
             picker = me._picker;
         if (picker && picker.isPicker) {
             picker.setValue(newValue);
         }
         // Ext.Date.format expects a Date
         if (newValue !== null) {
             me.getComponent().setValue(Ext.Date.format(newValue, me.getDateFormat() || Ext.util.Format.defaultDateFormat));
         } else {
             me.getComponent().setValue('');
         }
         if (newValue !== oldValue) {
             me.fireEvent('change', me, newValue, oldValue);
         }
     },
     /**
     * Updates the date format in the field.
     * @private
     */
     updateDateFormat: function (newDateFormat, oldDateFormat) {
         var value = this.getValue();
         if (newDateFormat != oldDateFormat && Ext.isDate(value)) {
             this.getComponent().setValue(Ext.Date.format(value, newDateFormat || Ext.util.Format.defaultDateFormat));
         }
     },
     /**
     * Returns the {@link Date} value of this field.
     * If you wanted a formated date
     * @return {Date} The date selected
     */
     getValue: function () {
         if (this._picker && this._picker instanceof superdatetimepickerfield) {
             return Ext.Date.format(this._picker.getValue(), "Y-m-d" || Ext.util.Format.defaultDateFormat);
         }
         return Ext.Date.format(this._value, "Y-m-d" || Ext.util.Format.defaultDateFormat);
     },
 
     /**
     * Returns the value of the field formatted using the specified format. If it is not specified, it will default to
     * {@link #dateFormat} and then {@link Ext.util.Format#defaultDateFormat}.
     * @param {String} format The format to be returned.
     * @return {String} The formatted date.
     */
     getFormattedValue: function (format) {
         var value = this.getValue();
         return (Ext.isDate(value)) ? Ext.Date.format(value, format || this.getDateFormat() || Ext.util.Format.defaultDateFormat) : value;
     },
     applyPicker: function (picker, pickerInstance) {
         if (pickerInstance && pickerInstance.isPicker) {
             picker = pickerInstance.setConfig(picker);
         }
         return picker;
     },
     getPicker: function () {
         var picker = this._picker,
             value = this.getValue();
             if(value==null||value==""){
             	var date = new Date();
             	value = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
             }
         if (picker && !picker.isPicker) {
             picker = Ext.factory({dateFormat:this.getDateFormat()}, Questionnaire.view.SuperDateTimePicker);
         }
         var dateArr = value.split('-');
         picker.setValue(new Date(parseInt(dateArr[0],10),(parseInt(dateArr[1],10)-1),parseInt(dateArr[2],10)));
         picker.on({
             scope: this,
             change: 'onPickerChange',
             hide: 'onPickerHide'
         });
         this._picker = picker;
         return picker;
     },
     /**
     * @private
     * Listener to the tap event of the mask element. Shows the internal DatePicker component when the button has been tapped.
     */
     onMaskTap: function () {
         if (this.getDisabled()) {
             return false;
         }
         this.onFocus();
         return false;
     },
     /**
     * Called when the picker changes its value.
     * @param {superdatetimepickerfield} picker The date picker.
     * @param {Object} value The new value from the date picker.
     * @private
     */
     onPickerChange: function (picker, value) {
         var me = this,
             oldValue = me.getValue();
         me.setValue(value);
         me.fireEvent('select', me, value);
         me.onChange(me, value, oldValue);
     },
     /**
     * Override this or change event will be fired twice. change event is fired in updateValue
     * for this field. TOUCH-2861
     */
     onChange: Ext.emptyFn,
     /**
     * Destroys the picker when it is hidden, if
     * {@link Ext.field.DatePicker#destroyPickerOnHide destroyPickerOnHide} is set to `true`.
     * @private
     */
     onPickerHide: function () {
         var me = this,
             picker = me.getPicker();
         if (me.getDestroyPickerOnHide() && picker) {
             picker.destroy();
             me._picker = me.getInitialConfig().picker || true;
         }
     },
     reset: function () {
         this.setValue(this.originalValue);
     },
     onFocus: function (e) {
         var component = this.getComponent();
         this.fireEvent('focus', this, e);
 
         if (Ext.os.is.Android4) {
             component.input.dom.focus();
         }
         component.input.dom.blur();
         if (this.getReadOnly()) {
             return false;
         }
         this.isFocused = true;
         
         //component.setValue(Ext.Date.format(new Date(), me.getDateFormat() || Ext.util.Format.defaultDateFormat))
         var aaap = this.getPicker();
         aaap.show();
         aaap.show();
     },
     // @private
     destroy: function () {
         var picker = this._picker;
         if (picker && picker.isPicker) {
             picker.destroy();
         }
         this.callParent(arguments);
     }
     //<deprecated product=touch since=2.0>
 }, 
 function () {
     /*this.override({
         getValue: function (format) {
             if (format) {
                 Ext.Logger.deprecate("format argument of the getValue method is deprecated, please use getFormattedValue instead", this);
                 return this.getFormattedValue(format);
             }
             return this.callOverridden();
         }
     });
     */
     /**
     * @method getDatePicker
     * @inheritdoc Ext.field.DatePicker#getPicker
     * @deprecated 2.0.0 Please use #getPicker instead
     */
     //Ext.deprecateMethod(this, 'getDatePicker', 'getPicker');
     //</deprecated>
 });