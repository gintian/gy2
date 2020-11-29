
Ext.define('BI.view.MenuItem', {
    extend: 'Ext.container.Container',
    xtype:'bimenuitem',
    menuItem:undefined,
    loaded:false,
    expanded:false,
    actionFrameId:'',
    clickFlag:false,
    initComponent:function(){
        me = this;

        var menu = this.menuItem;
        var tip = '';
        if(this.menuItem.children && this.menuItem.children.length>0){
            //初始化时，有下级菜单的菜单增加图标
            //tip = '∨';
            tip = "<img src='/module/bi_toolbox/images/down.png' style='float: right' width='15px' height='15px'>";
        }
        this.items = [{
            xtype:'box',
            style:'cursor:pointer;padding:20px 20px;color:'+themeConfig.menuFontColor +';font-size:' + themeConfig.menuFontSize,
            html:menu.name + tip,
            listeners:{
                click:{
                    element:'el',
                    fn:function (evt,ele) {
                        var sd = document.getElementsByClassName("x-component");
                        for (var num =0; num<sd.length; num++) {
                            sd[num].style.background="transparent";
                        }
                        ele.style.background=themeConfig.menuBackground;
                        me.clickFlag  = true;
                        this.expandOrToggle();
                    },
                    scope:this
                },
                mouseover:{
                    element: 'el',
                    fn:function (evt,ele) {
                        me.clickFlag  = false;
                        ele.style.background=themeConfig.menuBackground;
                    }
                },
                mouseleave:{
                    element: 'el',
                    fn:function (evt,ele) {
                        if(!me.clickFlag){
                            ele.style.background="transparent";
                        }
                    }
                }
            }
        },{
            xtype:'container',
            margin:'0 0 0 20'
        }];
        this.callParent();
    },
    expandOrToggle:function () {
        var tip = '';
        if(this.menuItem.children && this.menuItem.children.length>0){
            if(this.expanded){
                tip = "<img src='/module/bi_toolbox/images/down.png' style='float: right' width='15px' height='15px'>";
            }else{
                tip = "<img src='/module/bi_toolbox/images/up.png' style='float: right' width='15px' height='15px'>";
            }
            this.items.items[0].el.dom.innerHTML=this.menuItem.name + tip;
        }
        if(!this.menuItem.children || this.menuItem.children.length==0){
            this.popup(this.menuItem.url);
            return;
        }

        if(!this.expanded){
            if(!this.loaded) {
                for (var i = 0; i < this.menuItem.children.length; i++) {
                    this.items.items[1].add({
                        xtype: 'bimenuitem',
                        menuItem: this.menuItem.children[i],
                        actionFrameId:this.actionFrameId
                    })
                }
                this.loaded = true;
            }else{
                this.items.items[1].show();
            }
            this.expanded = true;
        }else{
            this.items.items[1].hide();
            this.expanded = false;
        }
    },
    popup:function(url){
       Ext.getDom(this.actionFrameId).src = url;
    }
});
