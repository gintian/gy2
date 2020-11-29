Ext.define('Home.view.SVGIcon',{
   extend:'Ext.Component',
   xtype:'svgicon',
   iconCls:'',
   iconName:'',
   renderTpl:[
        '<svg class="icon {$comp.iconCls}" aria-hidden="true">',
        '<use xlink:href="#{$comp.iconName}"></use>',
        '</svg>'
   ]
});