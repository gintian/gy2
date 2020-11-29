锘匡豢/** create by guodd
 * 2014-10-23
 *
 * 鏍规嵁闇�姹傝嚜瀹氫箟涓�浜沞xt缁勪欢
 *
 * 1.鑷畾涔塂ateTime閫夋嫨鍣細
 * 甯︽椂闂撮�夋嫨鐨勬棩鍘嗛�夋嫨鍣�
 */
Ext.define('SYSP.picker.DateTimePicker', {
    extend: 'Ext.picker.Date',//缁ф壙浜� Ext.picker.Date
    alias: 'widget.dateptimeicker',//娣诲姞xtype dateptimeicker
    requires: ['SYSP.picker.MonthPicker'],
    okText:'纭畾',//纭鎸夐挳鏂囧瓧鎻忚堪

    renderTpl: [
        '<div id="{id}-innerEl" role="grid">',
            '<div role="presentation" class="{baseCls}-header">',
                '<a id="{id}-prevEl" class="{baseCls}-prev {baseCls}-arrow" href="#" role="button" title="{prevText}" hidefocus="on" ></a>',
                '<div class="{baseCls}-month" id="{id}-middleBtnEl">{%this.renderMonthBtn(values, out)%}</div>',
                '<a id="{id}-nextEl" class="{baseCls}-next {baseCls}-arrow" href="#" role="button" title="{nextText}" hidefocus="on" ></a>',
            '</div>',
            '<table id="{id}-eventEl" class="{baseCls}-inner" cellspacing="0" role="presentation">',
                '<thead role="presentation"><tr role="presentation">',
                    '<tpl for="dayNames">',
                        '<th role="columnheader" class="{parent.baseCls}-column-header" title="{.}">',
                            '<div class="{parent.baseCls}-column-header-inner">{.:this.firstInitial}</div>',
                        '</th>',
                    '</tpl>',
                '</tr></thead>',
                '<tbody role="presentation"><tr role="presentation">',
                    '<tpl for="days">',
                        '{#:this.isEndOfWeek}',
                        '<td role="gridcell" id="{[Ext.id()]}">',
                           '<a role="presentation" hidefocus="on" class="{parent.baseCls}-date" href="#"></a>',
                        '</td>',
                    '</tpl>',
                '</tr></tbody>',
            '</table>',

            //鎸囧畾鏃跺垎绉掓覆鏌撴鏋�
            '<table id="{id}-timeEl" style="table-layout:auto;width:auto;margin-bottom:1px" class="x-datepicker-inner"  cellpadding="0" cellspacing="0">',
                '<tbody><tr>',
                    '<td nowrap>{%this.renderHourBtn(values,out)%}</td>',
                    '<td>{%this.renderMinuteBtn(values,out)%}</td>',
                    '<td>{%this.renderSecondBtn(values,out)%}</td>',
                '</tr></tbody>',
            '</table>',

            '<tpl if="showToday">',
                //娣诲姞涓�涓‘璁ゆ寜閽覆鏌�
                '<div id="{id}-footerEl" role="presentation" class="{baseCls}-footer">{%this.renderOkBtn(values, out)%}{%this.renderTodayBtn(values, out)%}</div>',
            '</tpl>',
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

            //鎸囧畾娓叉煋鏂规硶璋冪敤
            renderHourBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.hourBtn.getRenderTree(), out);//鏍规嵁缁勪欢鑾峰緱缁勪欢鐨刪tml杈撳嚭
            },
            renderMinuteBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.minuteBtn.getRenderTree(), out);
            },
            renderSecondBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.secondBtn.getRenderTree(), out);
            },
            renderOkBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.okBtn.getRenderTree(), out);
            }
        }
    ],

    beforeRender: function () {
    	datepicker = this;
    	//鏃跺垎绉掓槸鍚︽樉绀�
        hourHide = true;
        minHide = true;
        secHide = true;
        if(this.format.toUpperCase().indexOf('H')>-1){
           hourHide = false;
           minHide = false;
           secHide = false;
        }
        var me = this,_$Number=Ext.form.field.Number;
        //鍦ㄧ粍浠舵覆鏌撲箣鍓嶏紝灏嗚嚜瀹氫箟娣诲姞鐨勬椂銆佸垎銆佺鍜岀‘璁ゆ寜閽繘琛屽垵濮嬪寲
        //缁勪欢瀹藉害鍙兘闇�瑕佽皟鏁翠笅锛屾牴鎹娇鐢ㄧ殑theme涓嶅悓锛屽搴﹂渶瑕佽皟鏁�
        me.hourBtn=new _$Number({
        	ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            minValue:0,
            maxValue:23,
            hidden:hourHide,//鏃舵槸鍚︽樉绀�
            step:1,
            width:51,
            border:1
        });
        me.minuteBtn=new _$Number({
        	ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            minValue:0,
            maxValue:59,
            hidden:minHide,//鍒嗘槸鍚︽樉绀�
            step:1,
            width:60,
            labelWidth:3,
            fieldLabel:'&nbsp;'
        });
        me.secondBtn=new _$Number({
        	ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            minValue:0,
            maxValue:59,
            hidden:secHide,//绉掓槸鍚︽樉绀�
            step:1,
            width:59,
            labelWidth:3,
            fieldLabel:'&nbsp;'//鍦ㄧ粍浠朵箣鍓嶆覆鏌� ':'
        });

        me.okBtn = new Ext.button.Button({
            ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            text: me.okText,
            tooltip: me.okTip,
            tooltipType:'title',
            handler:me.okHandler,//纭鎸夐挳鐨勪簨浠跺鎵�
            scope: me
        });
        me.callParent();
    },
    
    finishRenderChildren: function () {
        var me = this;
        //缁勪欢娓叉煋瀹屾垚鍚庯紝闇�瑕佽皟鐢ㄥ瓙鍏冪礌鐨刦inishRender锛屼粠鑰岃幏寰椾簨浠剁粦瀹�
        me.hourBtn.finishRender();
        me.minuteBtn.finishRender();
        me.secondBtn.finishRender();
        me.okBtn.finishRender();
        me.callParent();
    },

    handleDateClick : function(e, t){
        var me = this,
            handler = me.handler;

        e.stopEvent();
        if(!me.disabled && t.dateValue && !Ext.fly(t.parentNode).hasCls(me.disabledCellCls)){
            me.doCancelFocus = me.focusOnSelect === false;
            me.setValue(new Date(t.dateValue));
            delete me.doCancelFocus;
            me.fireEvent('select', me, me.value); //涓轰簡璁╃偣鍑绘棩鏈熸椂鎺т欢闈㈡澘涓嶆秷澶憋紝娉ㄩ噴鎺夋琛屻�傚洜涓鸿繕瑕侀�夋椂鍒嗙
            if (handler) {
                handler.call(me.scope || me, me, me.value);
            }
            me.onSelect();
        }
    },
    
    /**
     * 纭 鎸夐挳瑙﹀彂鐨勮皟鐢�
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

    /**
     * 瑕嗙洊浜嗙埗绫荤殑鏂规硶锛屽洜涓虹埗绫讳腑鏄牴鎹椂闂寸殑getTime鍒ゆ柇鐨勶紝鍥犳闇�瑕佸鏃躲�佸垎銆佺鍒嗗埆鍊间负0鎵嶈兘淇濊瘉褰撳墠鍊肩殑鏃ユ湡閫夋嫨
     * @private
     * @param {Date} date The new date
     */
    selectedUpdate: function(date){
        this.callParent([Ext.Date.clearTime(date,true)]);
    },

    /**
     * 鏇存柊picker鐨勬樉绀哄唴瀹癸紝闇�瑕佸悓鏃舵洿鏂版椂銆佸垎銆佺杈撳叆妗嗙殑鍊�
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
     * @private
     */
    selectToday : function(){
        var me = this,
            btn = me.todayBtn,
            handler = me.handler;

        if(btn && !btn.disabled){
            //me.setValue(Ext.Date.clearTime(new Date()));//clearTime浼氭竻鎺夋椂鍒嗙锛岀洿鎺ラ噰鐢╪ew Date()
        	me.setValue(new Date(), true);
            me.fireEvent('select', me, me.value);
            if (handler) {
                handler.call(me.scope || me, me, me.value);
            }
            me.onSelect();
        }
        return me;
    },
    
    
    /**
     * 浠巔icker閫変腑鍚庯紝璧嬪�兼椂锛岄渶瑕佷粠鏃躲�佸垎銆佺涔熻幏寰楀綋鍓嶅��
     * datetimefield涔熶細璋冪敤杩欎釜鏂规硶瀵筽icker鍒濆鍖栵紝鍥犳娣诲姞涓�涓猧sfixed鍙傛暟銆�
     * @param {Date} date The new date
     * @param {Boolean} isfixed True 鏃讹紝蹇界暐浠庢椂鍒嗙涓幏鍙栧��
    */
    setValue : function(date, isfixed){
        var me = this;
        if(isfixed!==true){
            date.setHours(me.hourBtn.getValue());
            date.setMinutes(me.minuteBtn.getValue());
            date.setSeconds(me.secondBtn.getValue());
        }
        me.value=date;
        me.update(me.value);
        return me;
    },

    // @private
    // @inheritdoc
    beforeDestroy : function() {
        var me = this;

        if (me.rendered) {
            //閿�姣佺粍浠舵椂锛屼篃闇�瑕侀攢姣佽嚜瀹氫箟鐨勬帶浠�
            Ext.destroy(
                me.hourBtn,
                me.minuteBtn,
                me.secondBtn,
                me.okBtn
            );
        }
        me.callParent();
    },
    
    /**
     * 浣跨敤鑷畾涔� monthpicker
     * @returns
     */
    createMonthPicker: function(){
        var me = this,
            picker = me.monthPicker;

        if (!picker) {
            me.monthPicker = picker = new SYSP.picker.MonthPicker({
            	ownerCt: me,
                ownerLayout: me.getComponentLayout(),
                renderTo:document.body,
                floating: true,
                padding: me.padding,
                shadow: false,
                small: me.showToday === false,
                listeners: {
                    scope: me,
                    cancelclick: me.onCancelClick,
                    okclick: me.onOkClick,
                    yeardblclick: me.onOkClick,
                    monthdblclick: me.onOkClick
                }
            });
            if (!me.disableAnim) {
                // hide the element if we're animating to prevent an initial flicker
                picker.el.setStyle('display', 'none');
            }
            picker.hide();
            me.on('beforehide', me.doHideMonthPicker, me);
        }
        return picker;
    }
}/*,
function() {
    var proto = this.prototype,
        date = Ext.Date;

    proto.monthNames = date.monthNames;
    proto.dayNames   = date.dayNames;
    proto.format     = date.defaultFormat;
}*/);
