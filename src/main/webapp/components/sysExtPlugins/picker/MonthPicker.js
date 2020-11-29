/**
 * guodd
 * 时间选择器中的年月选择器重新定义一下
 * 此版本Ext.picker.Month 有bug，作为gridpanel editor时有问题，重新定义一下
 */
Ext.define('SYSP.picker.MonthPicker', {
    extend: 'Ext.picker.Month',
    
    renderTpl: [
                '<div id="{id}-bodyEl" class="{baseCls}-body">',
                  '<div id="{id}-monthEl" class="{baseCls}-months">',
                      '<tpl for="months">',
                          '<div class="{parent.baseCls}-item {parent.baseCls}-month">',
                              // the href attribute is required for the :hover selector to work in IE6/7/quirks
                              '<a style="{parent.monthStyle}" role="button" hidefocus="on" class="{parent.baseCls}-item-inner" href="#">{.}</a>',
                          '</div>',
                      '</tpl>',
                      '<div style="margin-bottom:15px">&nbsp;</div>',
                  '</div>',
                  '<div id="{id}-yearEl" class="{baseCls}-years">',
                      '<div class="{baseCls}-yearnav">',
                          '<div class="{baseCls}-yearnav-button-ct">',
                              // the href attribute is required for the :hover selector to work in IE6/7/quirks
                              '<a id="{id}-prevEl" class="{baseCls}-yearnav-button {baseCls}-yearnav-prev" href="#" hidefocus="on" role="button"></a>',
                          '</div>',
                          '<div class="{baseCls}-yearnav-button-ct">',
                              // the href attribute is required for the :hover selector to work in IE6/7/quirks
                              '<a id="{id}-nextEl" class="{baseCls}-yearnav-button {baseCls}-yearnav-next" href="#" hidefocus="on" role="button"></a>',
                          '</div>',
                      '</div>',
                      '<tpl for="years">',
                          '<div class="{parent.baseCls}-item {parent.baseCls}-year">',
                              // the href attribute is required for the :hover selector to work in IE6/7/quirks
                              '<a hidefocus="on" class="{parent.baseCls}-item-inner" role="button" href="#">{.}</a>',
                          '</div>',
                      '</tpl>',
                  '</div>',
                  '<div class="' + Ext.baseCSSPrefix + 'clear"></div>',
                  '<tpl if="showButtons">',
                      '<div class="{baseCls}-buttons" >{%',
                          'var me=values.$comp, okBtn=me.okBtn, cancelBtn=me.cancelBtn;',
                          'okBtn.ownerLayout = cancelBtn.ownerLayout = me.componentLayout;',
                          'okBtn.ownerCt = cancelBtn.ownerCt = me;',
                          'Ext.DomHelper.generateMarkup(okBtn.getRenderTree(), out);',
                          'Ext.DomHelper.generateMarkup(cancelBtn.getRenderTree(), out);',
                      '%}</div>',
                  '</tpl>',
                '</div>'
            ],
    
    onDestroy: function() {
        Ext.destroyMembers(this, 'okBtn', 'cancelBtn');
        this.callParent();
    }
    
});