/**
 * 领导桌面
 * zhangh 2019-11-11
 *
 **/
Ext.define('BI.view.MainView',{
    extend:'Ext.container.Viewport',
    requires:['BI.view.MenuItem'],
    layout:'border',
    //菜单展示类型，float：浮动；left：左侧菜单展示
    menutype:'',
    //鼠标是否移出一级菜单
    firLeave:false,
    //鼠标是否移入二级菜单
    secMove:false,
    items:[{
        //头部区域，显示集团logo和一级菜单
        xtype:'container',
        height:70,
        style:'background:' + themeConfig.headerBackgroundColor + ";padding:0",
        border:false,
        region:'north',
        items:[
            {
                xtype:'image',
                border:2,
                height:50,
                src:themeConfig.logoPath,
                style:'position:absolute;left:20px;top:10px;'
            },{
                xtype:'toolbar',
                id:'header',
                height:70,
                style:'background:' + themeConfig.headerBackgroundColor + ";padding:0",
                border:false
            }
        ]
    },{
        //左侧菜单区域，初始化隐藏，根据菜单显示类型控制是否显示
        xtype:'container',
        id:'leftMenu',
        //宽度太宽，缩小一些
        width:160,
        scrollable:false,
        style:'overflow:hidden;background:'  + themeConfig.leftBackgroundColor,
        region:'west',
        hidden:false,
        /*
        layout:{
            type:'vbox',
            align:'stretch'
        },
         */
        items:[
            //显示二级以上菜单
        ],
        listeners:{
            mousewheel:{
                element:'el',
                fn:function (evt,ele) {
                    var dom = this;
                    if(evt.getWheelDelta()<0){
                        this.setScrollTop(this.getScrollTop()+60);
                    }else{
                        this.setScrollTop(this.getScrollTop()-60);
                    }
                }
            }
        }
    },{
        //具体功能显示区域，iframe连接跳转
        xtype:'component',
        region:'center',
        id:'center-iframe',
        style:'background:' + themeConfig.centerBackground,
        autoEl:{
            tag:'iframe',
            name:'center-iframe',
            frameborder:'0'
        }
    }],
    firstMenuClick:function (evt,ele) {
        for (var num =0; num<this.fir_data.length; num++) {
            Ext.getElementById("fir_" + num).style.border ='none';
        }
        var index = ele.getAttribute('menu-index');
        //点击之后设置点击之后的样式,之前写法不支持IE兼容模式
        Ext.getElementById("fir_" + index).style.borderBottom = '2px solid #FFEFD5';
        this.loadSecondMenu(index);
    },
    firstMenuMove:function (evt,ele) {
        if(this.menutype=='left'||this.menutype==''){
            return;
        }
        this.firLeave = false;
        var index = ele.getAttribute('menu-index');
        var secData = this.fir_data[index].children;
        var secMenuCom;
        secMenuCom = Ext.getCmp('secPanel');
        secMenuCom.removeAll();
        for(var i=0;i<secData.length;i++){
            secMenuCom.add({
                xtype:'bimenuitem',
                menuItem:secData[i],
                actionFrameId:'center-iframe'
            });
        }
        var headerBar = Ext.getCmp('header');
        var compIndex = parseInt(index)+2;
        //解决浮动情况下，二级菜单没有展示在对应的一级菜单下的问题
        secMenuCom.showBy(headerBar.items.items[compIndex-1],"tl-bl?",[0,0]);
    },
    initUrl:function(data){
        var urlStr = "";
        if(data.url&&data.url.length>0){
            urlStr = data.url
        }else{
            urlStr = this.initUrl(data.children[0]);
        }
        return urlStr;
    },
    //加载二级菜单
    loadSecondMenu:function(index){
        //zhangh 2019-11-25 解决二级菜单是目录，而不是具体链接的问题
        var url = this.initUrl(this.fir_data[index]);
        var secData = this.fir_data[index].children;
        var centerIframe = Ext.getCmp("center-iframe");
        if(centerIframe.el){
            centerIframe.el.dom.src=url;
        }else{
            centerIframe.autoEl.src=url;
        }
        //没有二级菜单时，隐藏左侧菜单栏
        if(this.menutype=="left"&&secData&&secData.length>0){
            Ext.getCmp('leftMenu').show();
        }else{
            Ext.getCmp('leftMenu').hide();
        }
        var secMenuCom;
        if(this.menutype=="left"){
            secMenuCom =Ext.getCmp('leftMenu');
            secMenuCom.removeAll();
            for(var i=0;i<secData.length;i++){
                secMenuCom.add({
                    xtype:'bimenuitem',
                    menuItem:secData[i],
                    actionFrameId:'center-iframe'
                });
            }
        }
    },
    initComponent:function(){
        //获取URL信息，解析传入的菜单展示类型参数menutype
        var url = window.location.search;
        if(url.indexOf('menutype')>0){
            var theRequest = new Object();
            if (url.indexOf("?") != -1) {
                var str = url.substr(1);
                strs = str.split("&");
                for(var i = 0; i < strs.length; i ++) {
                    var param = strs[i];
                    var params=param.split("=");
                    if(params.length>2){
                        for(var p =2;p<params.length;p++){
                            params[1] += ("="+params[p]);
                        }
                    }
                    if (params.length>1){
                        theRequest[params[0]]=params[1];
                    }else
                        theRequest[params[0]]="";
                }
            }
            this.menutype = theRequest.menutype;
        }else{
            this.menutype = "left";
        }

        this.callParent();
        //调用交易类，初始化数据，渲染菜单
        Rpc({functionId:'SYS00006001',async:false,success:this.initMenu,scope:this},new HashMap());
    },
    initMenu:function(res) {
        var me = this;
        if(this.menutype!="left"){
            Ext.getCmp("leftMenu").hide();
        }
        var respon = Ext.decode(res.responseText);
        var menuData = eval("("+respon.menuData+")");
        //初始化菜单失败或者没有模块权限、点数不够用时给出提示信息
        if(menuData.return_code=='fail'){
            Ext.MessageBox.alert("提示",menuData.return_msg);
            return;
        }
        var redPoint = "";
        //判断当前用户是否有待办
        if(menuData.haveTask==true){
            //有待办时，业务平台图标加小红点
            redPoint = "<img src='/module/bi_toolbox/images/redPoint.png' align='top' width='5px' height='5px' style='position: absolute;top:30px' >";
        }
        //菜单名称
        var name;
        //菜单链接
        var url;
        //菜单号
        var menuid;
        //一级菜单数据
        var fir_data = this.fir_data = menuData.return_data.menuList[0].children;
        var headerBar = Ext.getCmp('header');
        headerBar.add('->');
        for (var num =0; num<fir_data.length; num++) {
            name = fir_data[num].name;
            url = fir_data[num].url;
            menuid = fir_data[num].menuid;
            headerBar.add({
                xtype: 'component',
                height:70,
                html: "<div id='fir_"+num+"'menu-index='" + num + "'>" + name + "</div>",
                style: 'cursor:pointer;color:'+themeConfig.headerFontColor+';font-size:'+themeConfig.headerFontSize+';padding:0px 20px;line-height:68px;' ,
                listeners: {
                    click: {
                        element: 'el',
                        delegate: 'div[menu-index]',
                        fn: this.firstMenuClick,
                        scope: this
                    },
                    mouseover: {
                        element: 'el',
                        delegate: 'div[menu-index]',
                        fn: this.firstMenuMove,
                        scope: this
                    },
                    mouseleave:{
                        element:'el',
                        fn:function(evt,ele){
                            me.firLeave = true;
                            if(me.menutype=='float'){
                                //鼠标移出一级菜单时，判断鼠标是否在二级菜单上，没有则隐藏二级菜单
                                setTimeout(function(){
                                    //解决之前二级菜单显示、隐藏切换不好使的问题
                                    if(me.firLeave && !me.secMove){
                                        Ext.getCmp("secPanel").hide();
                                    }
                                },20)
                            }
                        }
                    },
                }
            });

        }
        headerBar.add({
            xtype:'component',
            height:70,
            html:"<div><a href='/templates/top/hcm_header.jsp' target='_blank' style='color:"+themeConfig.headerFontColor+" !important;font-size:"+themeConfig.headerFontSize+"' >业务平台"+redPoint+"</a></div>",
            style:'padding:0px 20px;line-height:70px'
        });
        headerBar.add('->');
        if(this.menutype=="left"){
            //Ext.getElementById("fir_0").style="border:none";
            this.loadSecondMenu(0);
        }else{
            var centerIframe = Ext.getCmp("center-iframe");
            var url = this.initUrl(this.fir_data[0]);
            if(centerIframe.el){
                centerIframe.el.dom.src=url;
            }else{
                centerIframe.autoEl.src=url;
            }
            Ext.widget('container',{
                id:'secPanel',
                floating:true,
                width:160,
                shadow:false,
                scrollable:false,
                style:'overflow:hidden;background:'+themeConfig.leftBackgroundColor,
                listeners:{
                    mouseover:{
                        element:'el',
                        fn:function(evt,ele){
                            //me.movetype = false;
                            me.secMove = true;
                        }
                    },
                    mouseleave:{
                        element:'el',
                        fn:function(evt,ele){
                            this.component.hide();
                            //me.movetype = true;
                            me.secMove = false;
                        }
                    },
                    //鼠标滑轮滚动时，二级菜单跟着滚动
                    mousewheel:{
                        element:'el',
                        fn:function (evt,ele) {
                            if(Ext.getCmp('header').getHeight() + this.getHeight()>document.body.clientHeight){
                                if(evt.getWheelDelta()<0){
                                    if(this.getHeight()>document.body.clientHeight-this.getTop()){
                                        this.setTop(this.getTop()-60);
                                    }
                                }else{
                                    if(this.getTop()<Ext.getCmp('header').getHeight()){
                                        this.setTop(this.getTop()+60);
                                    }else{
                                        this.setTop(Ext.getCmp('header').getHeight());
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }
});