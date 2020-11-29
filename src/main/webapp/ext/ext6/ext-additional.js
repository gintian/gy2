Ext.additionLoaded = true;

//修改ext默认图标src。防止连接www.sencha.com
Ext.BLANK_IMAGE_URL='data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';

//window控件一些公共的属性，为了统一样式
Ext.define('Ext.additional.window.Window',{
	override:'Ext.window.Window',
	constrain:true,
	border:false,
	shadow:false,
	bodyStyle:"background-color:white;",
	resizable:true
});


//Ext提示弹框添加警告图标
Ext.define("Ext.additional.window.MessageBox",{
	override:"Ext.window.MessageBox",
	alert: function(cfg, msg, fn, scope) {
        if (Ext.isString(cfg)) {
            cfg = {
                title : cfg,
                msg : msg+"&nbsp;",
                icon:this.INFO,
                buttons: this.OK,
                fn: fn,
                scope : scope,
                minWidth: this.minWidth
            };
        }else{
            cfg+="&nbsp;";
        }
        return this.show(cfg);
    }
});

/*半角转全角 数字字母不转*/
Ext.SBCtoDBC = function(str){
    var result = "";
    var len = str.length;
    for(var i=0;i<len;i++)
    {
        var cCode = str.charCodeAt(i);
        //0~9、a~z、A~Z 不转全角
        if((cCode>47 && cCode<58) || (cCode>64 && cCode<91) || (cCode>96 && cCode<123)){
        		result += String.fromCharCode(cCode);
        		continue;
        }
        //全角与半角相差（除空格外）：65248(十进制)
        cCode = (cCode>=0x0021 && cCode<=0x007E)?(cCode + 65248) : cCode;
        //处理空格
        cCode = (cCode==0x0020)?0x03000:cCode;
        result += String.fromCharCode(cCode);
    }
    return result;
};

Ext.showAlert=function(msg,fn,scope){
	var myMsg = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
			title: '提示信息',
			message: Ext.isIE?"<div style='margin:0px 0px 14px 0px;'>"+msg+"</div>":msg,
			buttons: Ext.Msg.OK,
			icon: Ext.Msg.INFO,
			fn: fn,
            scope : scope,
		    closeAction: 'destroy'
	    });
};

Ext.showConfirm=function(message, fn, scope){
	var myMsg = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
			title: '提示信息',
			icon: Ext.Msg.QUESTION,
            message: Ext.isIE?"<div style='margin:0px 0px 14px 0px;'>"+message+"</div>":message,
            buttons: Ext.Msg.YESNO,
			callback: fn,
            scope : scope,
		    closeAction: 'destroy'
	    });
};

/*表格锁列菜单文字*/
Ext.define("Ext.additional.zh_CN.grid.locking.Lockable",{
	override:"Ext.grid.locking.Lockable",
	lockText:'锁定',
	unlockText:'解锁'
});

/*分页组件*/
Ext.define("Ext.additional.toolbar.Paging",{
	override:"Ext.toolbar.Paging",
	simpleModel:false,
	displayInfo:true,
    limitPageSize:true,
    showDisplayInfo:false,//分页设置简单模式时，是否显示分页详情 例如 显示1-3条，总20条 默认不显示
	initComponent:function(){
		if(this.simpleModel){ 
		    this.displayInfo = this.showDisplayInfo;
		    this.displayMsg = this.displayMsg.split("，").length==2?this.displayMsg.split("，")[1]:this.displayMsg; 
		    this.beforePageText='第';
		    this.afterPageText='页';
		}
		this.callParent(arguments);
	},
    getPagingItems: function() {
         var items =  this.callParent(arguments);
         //add by xiegh on date20180109 bug:33769
         items[4].width = 37;
         if(this.simpleModel){
        	items[4].width = 30;
            items.splice(items.length-1,1);
            return items;
         }
         
         var me = this;
         items.splice(items.length-2,0,{
            xtype: 'numberfield',
            itemId: 'inputCount',
            name: 'inputCount',
            cls: Ext.baseCSSPrefix + 'tbar-page-number',
            allowDecimals: false,
            minValue: 1,
            maxValue:me.limitPageSize?10000:500,//每页最大显示数为500  wangb 30652
            oldValue:me.pageSize,
            margin:'0 5 0 5',
            fieldLabel:'每页',
            labelWidth:30,
            keyHandlers: {
             ENTER: 'onEnterKey'
         	},
         	onEnterKey:function(){
         	  me.doRefresh();
         	},
            listeners:{
	            	blur:function(){
	            		
	            		if(!Ext.isNumeric(this.value)||this.value<1){
	            			this.setValue(this.oldValue);
	            		}
	            	}
            },
            width:70,
            hideTrigger: true,
            value:me.pageSize
         });
         return items;
    },
    onLoad:function(){
       this.callParent(arguments);
       if(!this.simpleModel)
       	   this.child('#inputCount').setValue(this.store.pageSize);
    },
    onPagingBlur : function(){
       return;
    },
    doRefresh : function(){
	    	var me = this,
	        store = me.store;
	    	var pageRowCount = me.child('#inputCount').getValue();
	    	if(!Ext.isNumeric(pageRowCount)||pageRowCount<1){
	    		return;
	    	}
	    	if(!me.limitPageSize){
                if(pageRowCount > 500){//每页最大显示记录超过500，刷新时只显示500条记录  wangb 30652
                    pageRowCount = 500;
                }
            }
	    	if(!this.simpleModel)
	    		me.child('#inputCount').oldValue = pageRowCount;
	    	store.pageSize = pageRowCount;
	    	var inputItem = this.getInputItem();
	    	store.currentPage = inputItem?inputItem.getValue():store.currentPage;
	    	var total = store.getTotalCount();
	    	if(total>pageRowCount*(store.currentPage-1)){
	    	    store.loadPage(store.currentPage);
	    	}else{
		    	this.moveFirst();
	    	}
	    return true;
    }
    
});

//修改view加载数据时的
Ext.define("Ext.additional.view.AbstractView", {
    override: "Ext.view.AbstractView",
    loadingText: "读取中..."
});

// sting 获取字节长度（中文按2个长度计算）
Ext.getStringByteLength = function(value){
	var len = value.replace(/[^\x00-\xff]/gi, "--").length;
	return len;
};


/*解决 gridpanel 带选框时 多选时点错了会全部清掉已选择的问题*/
Ext.define("Ext.additional.selection.CheckboxModel",{
	override:'Ext.selection.CheckboxModel',
    selectWithEventMulti:function(a,b,c){
		var cls = b.target.getAttribute("class");
		if(!cls){
		    this.callParent(arguments);
		    return;
		}
		if(cls.indexOf("x-grid-cell-row-checker")>-1){
			if(c)
				this.deselect([a],true);
			else
				this.select([a],true);
			b.stopEvent();return;
			
		}
		var divEles = b.target.getElementsByTagName("div");
		if(cls=='x-grid-cell-inner ' && divEles.length>0 && divEles[0].getAttribute("class")=='x-grid-row-checker'){
			if(c)
				this.deselect([a],true);
			else
				this.select([a],true);
			b.stopEvent();return;
	    }
		
		this.callParent(arguments);
	}
});

/* form field 组件默认 label和文本框分隔符为空*/
Ext.define("Ext.additional.form.field.Base",{
	override:'Ext.form.field.Base',
	labelSeparator:''
});

/*解决ie下 grid 编辑有时点击不触发的问题*/
Ext.define("Ext.additional.grid.plugin.CellEditing",{
    override:'Ext.grid.plugin.CellEditing',
    onEditComplete:function(ed){
        this.callParent(arguments);
    		//ed.context.view.actionableMode = false;
    }
    
});

/* window 层叠显示时ie下顺序错乱问题  BEGIN */
//销毁旧的 window zindex堆栈对象
Ext.WindowManager.zIndexStack.destroy( );
//新建 window zindex堆栈对象，排序规则添加按时间排序
Ext.WindowManager.zIndexStack = new Ext.util.Collection({
        sorters: {
            sorterFn: function(comp1, comp2) {
                var ret = (comp1.alwaysOnTop || 0) - (comp2.alwaysOnTop || 0);
                if (!ret) {
                   //如果两个是window时，使用window显示时间排序
                	   if(comp1.xtype=='window' && comp2.xtype=='window'){
                		  return  comp1.showDate>comp2.showDate;
                	   }
                   ret = comp1.getActiveCounter() - comp2.getActiveCounter();
                }
                
                return ret;
            }
        },
        filters: {
            filterFn: function(comp) {
                return comp.isVisible();
            }
        }
});

//为window堆栈对象添加 监听对象
Ext.WindowManager.zIndexStack.addObserver(Ext.WindowManager);

//window 显示时添加时间
Ext.override(Ext.window.Window,{
    		show:function(){
    			this.showDate = new Date();
    			return this.callParent();
    		}
});
/* window 层叠显示时ie下顺序错乱问题  END 

Ext.override(Ext.tip.QuickTip,{shadow:false,bodyStyle:'border:1px solid #c5c5c5;background:white; '}); 
*/
 
Ext.override(Ext.tab.Panel,{
    		bodyStyle:'border-top:none;'
});


/*解决 表格翻页、刷新后滚动条不会到重置到最上方*/
Ext.override(Ext.view.Table,{
	refreshView: function() {
		var me = this,
		blocked = me.blockRefresh || !me.rendered || me.up('[collapsed],[isCollapsingOrExpanding],[hidden]');
			
		if (blocked) {
			me.refreshNeeded = true;
		} else {
			if (me.bufferedRenderer) {
			    me.bufferedRenderer.refreshView();
			} else {
			    me.refresh();
			    //刷新时滚动到最上方
			    me.setScrollY(0);
			}
		}
	}
});
/*解决显示遮罩层时错误问题*/
Ext.override(Ext.ZIndexManager,{
	showModalMask: function(comp) {
        var me = this,
            compEl = comp.el,
            zIndex = compEl.getStyle('zIndex') - 4,
            maskTarget = comp.floatParent ? comp.floatParent.getTargetEl() : comp.container,
            mask = me.mask,
            shim = me.maskShim,
            viewSize;
        if (!mask || !mask.maskTarget) {
            me.mask = mask = Ext.getBody().createChild({
                'data-sticky': true,
                role: 'presentation',
                cls: Ext.baseCSSPrefix + 'mask ' + Ext.baseCSSPrefix + 'border-box',
                style: 'height:0;width:0'
            });
            mask.setVisibilityMode(Ext.Element.DISPLAY);
            mask.on('click', me.onMaskClick, me);
        }
        else {
            me.hideModalMask();
        }

        mask.maskTarget = maskTarget;
        viewSize = me.getMaskBox();

        if (shim) {
            shim.setStyle('zIndex', zIndex);
            shim.show();
            shim.setBox(viewSize);
        }
        mask.setStyle('zIndex', zIndex);
        maskTarget.saveTabbableState({
            excludeRoot: compEl
        });
        
        mask.show();
        mask.setBox(viewSize);
    }
});
/*panel的拖动支持自定义拖动按钮*/
Ext.override(Ext.panel.DD,{
	setupEl: function(panel){
        var me = this,
            header = panel.header,
            el = panel.body;
            
        if (header) {
            me.setHandleElId(header.id);
            el = header.el;
        }
        //如果指定特定拖动元素，使用此元素触发拖动
        if(this.dragTargetSelector){
        		var coms = panel.query(this.dragTargetSelector);
        		if(coms.length>0){
        			me.setHandleElId(coms[0].id);
        			el = Ext.get(coms[0].id);
        		}
        }
        
        if (el) {
            el.setStyle('cursor', 'move');
            me.scroll = false;
        } else {
            panel.on('boxready', me.setupEl, me, {single: true});
        }
    }
});
//日期选择控件选月份时，鼠标指向十一月、十二月时，换行显示导致显示错乱。在月份div上添加样式控制不允许换行
Ext.override(Ext.picker.Month,{
	renderTpl: [
        '<div id="{id}-bodyEl" data-ref="bodyEl" class="{baseCls}-body">',
          '<div id="{id}-monthEl" data-ref="monthEl" class="{baseCls}-months">',
              '<tpl for="months">',
              		//此div添加style样式控制不允许换行，整个renderTpl只改动了此行代码
                  '<div class="{parent.baseCls}-item {parent.baseCls}-month" style="white-space:nowrap;">',
                      '<a style="{parent.monthStyle}" role="button" hidefocus="on" class="{parent.baseCls}-item-inner">{.}</a>',
                  '</div>',
              '</tpl>',
          '</div>',
          '<div id="{id}-yearEl" data-ref="yearEl" class="{baseCls}-years">',
              '<div class="{baseCls}-yearnav">',
                  '<div class="{baseCls}-yearnav-button-ct">',
                      '<a id="{id}-prevEl" data-ref="prevEl" class="{baseCls}-yearnav-button {baseCls}-yearnav-prev" hidefocus="on" role="button"></a>',
                  '</div>',
                  '<div class="{baseCls}-yearnav-button-ct">',
                      '<a id="{id}-nextEl" data-ref="nextEl" class="{baseCls}-yearnav-button {baseCls}-yearnav-next" hidefocus="on" role="button"></a>',
                  '</div>',
              '</div>',
              '<tpl for="years">',
                  '<div class="{parent.baseCls}-item {parent.baseCls}-year">',
                      '<a hidefocus="on" class="{parent.baseCls}-item-inner" role="button">{.}</a>',
                  '</div>',
              '</tpl>',
          '</div>',
          '<div class="' + Ext.baseCSSPrefix + 'clear"></div>',
          '<tpl if="showButtons">',
              '<div class="{baseCls}-buttons">{%',
                  'var me=values.$comp, okBtn=me.okBtn, cancelBtn=me.cancelBtn;',
                  'okBtn.ownerLayout = cancelBtn.ownerLayout = me.componentLayout;',
                  'okBtn.ownerCt = cancelBtn.ownerCt = me;',
                  'Ext.DomHelper.generateMarkup(okBtn.getRenderTree(), out);',
                  'Ext.DomHelper.generateMarkup(cancelBtn.getRenderTree(), out);',
              '%}</div>',
          '</tpl>',
        '</div>'
    ]
	
});