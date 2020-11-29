/**
 * 2.自定义DateTime Field输入控件。
 * 带时间的日期输入控件
 * 转载请注明来自于gogo1217.iteye.com
 */
Ext.define('SYSP.DateTimeField', {
    extend:'Ext.form.field.Date',
    alias: 'widget.datetimefield',
    requires: ['SYSP.picker.DateTimePicker'],

    /**
     * @cfg {String} format
     * The default date format string which can be overriden for localization support. The format must be valid
     * according to {@link Ext.Date#parse}.
     */
    format : "Y-m-d H:i:s",
 
    /**
     * @cfg {String} altFormats
     * Multiple date formats separated by "|" to try when parsing a user input value and it does not match the defined
     * format.
     */
    altFormats : "Y-m-d H:i:s",
    createPicker: function() {
        var me = this,
            format = Ext.String.format;

        //修改picker为自定义picker
        return Ext.create('SYSP.picker.DateTimePicker',{
            pickerField: me,
            ownerCt: me.ownerCt,
            renderTo: document.body,
            floating: true,
            hidden: true,
            focusOnShow: true,
            minDate: me.minValue,
            maxDate: me.maxValue,
            disabledDatesRE: me.disabledDatesRE,
            disabledDatesText: me.disabledDatesText,
            disabledDays: me.disabledDays,
            disabledDaysText: me.disabledDaysText,
            format: me.format,
            showToday: me.showToday,
            startDay: me.startDay,
            minText: format(me.minText, me.formatDate(me.minValue)),
            maxText: format(me.maxText, me.formatDate(me.maxValue)),
            listeners: {
                scope: me,
                select: me.onSelect
            },
            keyNavConfig: {
                esc: function() {
                    me.collapse();
                }
            }
        });
    },
    getValue:function(){
    	return this.formatDate(this.value);
    },
    onRender:function(){
    	this.callParent(arguments);
    	var invalidText ="{0}是无效的日期，必须符合格式：";
    	if(this.format=='Y')
    		invalidText +=" YYYY";
    	else if(this.format=='Y-m')
    		invalidText +=" YYYY-MM";
    	else if(this.format=='Y-m-d')
    		invalidText +=" YYYY-MM-DD";
    	else if(this.format=='Y-m-d H:i')
    		invalidText +=" YYYY-MM-DD hh-mm";
    	else if(this.format=='Y-m-d H:i:s')
    		invalidText +=" YYYY-MM-DD hh-mm-ss";
    		
    	this.invalidText = invalidText;
    },
    /**
     * @private
     */
    onExpand: function() {
    	this.value = Ext.Date.parse(this.value,this.format);
        //多传一个参数，从而避免时分秒被忽略。
        this.picker.setValue(Ext.isDate(this.value) ? this.value : new Date(), true);
    }
    
});