/**
 * 颜色选择下拉列表组件
 * 使用方法和普通Field组件一致
 * 参照 module/kq/config/item/item.js
 * zhanghua 2018-10-18
 *
 */

Ext.define('EHR.extWidget.field.ColorPickField', {
    extend:'Ext.form.field.Picker',
    alias:'widget.colorfield',
    requires:['Ext.picker.Color'],
    xtype: 'colorPickField',
    triggerCls:'x-form-color-trigger',
    matchFieldWidth:false,
    colorValue:'',
    createPicker:function () {
        var me = this;
        me.picker=Ext.create('Ext.picker.Color', {
            pickerField:me,
            renderTo:document.body,
            floating:true,
            hidden:true,
            focusOnShow:true,
            listeners:{
                select:function (picker, selColor) {
                    var color=selColor;
                    if(color.substring(0,1)!='#'){
                        color='#'+color;
                    }
                    me.setValue(color);
                    //me.colorValue=selColor;
                    // 实现根据选择的颜色来改变背景颜色,根据背景颜色改变字体颜色,防止看不到值
                    var r = parseInt(selColor.substring(0,2),16);
                    var g = parseInt(selColor.substring(2,4),16);
                    var b = parseInt(selColor.substring(4,6),16);
                    var a = new Ext.draw.Color(r,g,b);
                    var l = a.getHSL()[2];


                    if (l > 0.5) {
                        me.setFieldStyle('background:' + color + ';color:'+color);
                    }
                    else{
                        me.setFieldStyle('background:' + color + ';color:'+color);
                    }
                }
            }
        });
        return me.picker;
    },
    listeners:{
        focus :function (e) {
            var value=this.getValue();
            if(value.substring(0,1)!='#'){
                value='#'+value;
            }
            if(value!=''&&value!='#') {
                this.setFieldStyle('background:' + value + ';color:transparent');
            }
            if(this.picker!=undefined){
                this.picker.clear();
            }


        }
    }
});