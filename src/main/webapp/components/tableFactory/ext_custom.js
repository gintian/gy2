/**
 * 
 */
Ext.override(Ext.grid.plugin.CellEditing,{
    beforeEdit:function(context){
       if(context.column.editableValidFunc){
    	         var fn = eval(context.column.editableValidFunc);
    	         return fn(context.record);
       }
         // return Ext.callback(context.column.editableValidFunc,window,[context.record]);
       return true;
    }
});

/**
 * 为column 添加 特定功能支持
 */
Ext.override(Ext.grid.column.Column,{
	renderTpl: [
        '<div id="{id}-titleEl" data-ref="titleEl" role="presentation"',
            '{tipMarkup}class="', Ext.baseCSSPrefix, 'column-header-inner<tpl if="!$comp.isContainer"> ', Ext.baseCSSPrefix, 'leaf-column-header</tpl>',
            '<tpl if="empty"> ', Ext.baseCSSPrefix, 'column-header-inner-empty</tpl>">',
            '<div id="{id}-textContainerEl" data-ref="textContainerEl" role="presentation" class="', Ext.baseCSSPrefix, 'column-header-text-container">',
                '<div role="presentation" class="', Ext.baseCSSPrefix, 'column-header-text-wrapper"  <tpl if="Ext.isIE && $comp.level && $comp.level.level &gt; 1">style="height:{$comp.level.level*26}px;"</tpl>>',//<tpl if="Ext.isIE8m">style="vertical-align:top;"</tpl>
                    '<div id="{id}-textEl" data-ref="textEl" role="presentation" class="', Ext.baseCSSPrefix, 'column-header-text',
                        '{childElCls}" <tpl if="$comp.nowrap">style="white-space:nowrap"</tpl> >',
                        '<span role="presentation" class="', Ext.baseCSSPrefix, 'column-header-text-inner">{text}</span>',
                    '</div>',
                '</div>',
            '</div>',
            '<tpl if="!menuDisabled">',
                '<div id="{id}-triggerEl" data-ref="triggerEl" role="presentation" class="', Ext.baseCSSPrefix, 'column-header-trigger',
                '{childElCls}" style="{triggerStyle}"></div>',
            '</tpl>',
        '</div>',
        '{%this.renderContainer(out,values);%}'
    ],
    filtered:false,
	cancelMark:function(){
    		if(!this.filtered)
	    	   return;
		var columnDom = this.getEl().child('.x-column-header-inner').dom;
			columnDom.removeChild(columnDom.getElementsByTagName("img")[0]);
			this.filtered = false;
		delete this.filterParam;
	},
	filterMark:function(grid){
  	    if(this.filtered)
  	    	   return;
  	    var header = this.getEl().child('.x-column-header-inner');
  	    var height = header.getHeight()/2-6;
  	    header.insertFirst({tag:'img',flag:'filter',style:'position:absolute;left:2px;top:'+height+'px',src:'/components/tableFactory/tableGrid-theme/images/filter2.png'});
  	    var filteredColumn = grid.query("gridcolumn[filtered=true]")[0];
  	    if(filteredColumn){
  	    		var columnDom = filteredColumn.getEl().child('.x-column-header-inner').dom;
  	    		columnDom.removeChild(columnDom.getElementsByTagName("img")[0]);
  	    		filteredColumn.filtered = false;
  	    		delete filteredColumn.filterParam;
  	    }
  	    this.filtered=true;
    },
    /* 列对象 排序处理 */
    sort: function(direction) {
    		var me = this,
            grid = me.up('tablepanel'),
            store = grid.store;
    	var updates = store.getModifiedRecords();
    	if(updates.length>0){
    	    Ext.showAlert("有未保存数据，请保存后排序!");
    	    return;
        }
        //store请求后台参数追加排序类型 py=拼音；bh=笔画
		if(this.showSortType && store.getProxy()){
			store.getProxy().extraParams.sortType=this.sortType||'bh';
		}
        this.callParent(arguments);
    }
});

/**
 * 下拉数据列
 */
Ext.define('Ext.my.grid.column.OperationColumn',{
	extend:'Ext.grid.column.Column',
	alias: ['widget.operationcolumn'],
	defaultRenderer: function(value){
		for(var i=0;i<this.operationData.length;i++){
			if(value==this.operationData[i].dataValue)
				return this.operationData[i].dataName;
		}
		return '';
    }
});

/**
 * 代码数据列
 */
Ext.define('Ext.my.grid.column.CodeColumn',{ 
	extend: 'Ext.grid.column.Column',
    alias: ['widget.codecolumn'],
    defaultRenderer: function(value){
	    	if(value){
	           return value.split('`')[1];
	    	}
    } 
            
}); 

/**
 * 大文本类型字段 自动换行，将\n替换为<br/>
 */
Ext.define('Ext.my.grid.column.bigtext',{
	extend: 'Ext.grid.column.Column',
	alias: ['widget.bigtextcolumn'],
	defaultRenderer: function(value){
	    	if(value)
	    		return value.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/\s+/g,'&nbsp;');
	    	
    } 
    
});

/*
 * 锁列类，重写 锁列事件方法，支持多列同时锁
 */
Ext.override(Ext.grid.locking.Lockable,{
	onLockMenuClick:function(){
		var me            = this,
		    activeHd      = this.normalGrid.headerCt.getMenu().activeHeader,
		    normalColumns = me.normalGrid.getColumnManager().getColumns();
		    
		var batchColumns = [];
		for(var i in normalColumns){
			batchColumns.push(normalColumns[i]);
			if(normalColumns[i].dataIndex == activeHd.dataIndex){
				break;
			}
		}
		//调用自定义批量锁列方法，不掉用Ext的锁列
		me.batchLock(batchColumns);
		me.fireEvent('grouplockcolumn', me, activeHd,'lock');
	},
    onUnlockMenuClick: function() {
		var me        = this,
		activeHd      = me.lockedGrid.headerCt.getMenu().activeHeader;
		lockedColumns = me.lockedGrid.getColumnManager().getColumns();
		
		var batchColumns = [];
		for(var i= lockedColumns.length-1;i>=0;i--){
			batchColumns.push(lockedColumns[i]);
			if(lockedColumns[i].dataIndex == activeHd.dataIndex)
				break;
		}
        me.batchUnlock(batchColumns);
        me.fireEvent('groupunlockcolumn', me, activeHd,'unlock');
    },
    
    //批量锁列，参考的lock()方法
    batchLock: function(columns) {
        var me         = this,
            normalGrid = me.normalGrid,
            lockedGrid = me.lockedGrid,
            normalView = normalGrid.view,
            lockedView = lockedGrid.view,
            normalScroller = normalView.getScrollable(),
            lockedScroller = lockedView.getScrollable(),
            normalHCt  = normalGrid.headerCt,
            refreshFlags,
            ownerCt,
            hadFocus,
            normalScrollY = normalView.getScrollY();

        activeHd = normalHCt.getMenu().activeHeader;
        hadFocus = activeHd.hasFocus;
        toCt = lockedGrid.headerCt;
        ownerCt = activeHd.ownerCt;

        if (ownerCt && !activeHd.isLockable()) {
            return;
        }

        if (activeHd.flex && lockedGrid.shrinkWrapColumns) {
            activeHd.width = activeHd.getWidth();
            activeHd.flex = null;
        }

        Ext.suspendLayouts();
        if (normalScroller) {
            normalScroller.suspendPartnerSync();
            lockedScroller.suspendPartnerSync();
        }

        if (lockedGrid.hidden) {

            if (!lockedGrid.componentLayoutCounter) {
                if (lockedView.bufferedRenderer) {
                    lockedView.bufferedRenderer.onViewResize(lockedView, 0, normalView.getHeight());
                }
            }
            lockedGrid.show();
        }

        lockedGrid.reconfiguring = normalGrid.reconfiguring = true;

        activeHd.ownerCmp = activeHd.ownerCt;
        
		for(var i=0;i<columns.length;i++){
		        if (ownerCt) {
		            ownerCt.remove(columns[i], false);
		        }
		        columns[i].locked = true;
		        toCt.add(columns[i]);
		}
		
		
        lockedGrid.reconfiguring = normalGrid.reconfiguring = false;

        activeHd.ownerCmp = null;

        refreshFlags = me.syncLockedWidth();

        if (refreshFlags[1]) {
            normalGrid.getView().refreshView();
        }

        if (refreshFlags[0]) {
            lockedGrid.getView().refreshView();
        }
        me.fireEvent('lockcolumn', me, activeHd);
        Ext.resumeLayouts(true);
        if (normalScroller) {
            normalScroller.resumePartnerSync(true);
            lockedScroller.resumePartnerSync();
        }

        if (normalScrollY) {
            lockedView.setScrollY(normalScrollY);
            normalView.setScrollY(normalScrollY);
        }
        if (hadFocus) {
            activeHd.focus();
        }
    },
    batchUnlock: function(columns) {
        var me         = this,
            normalGrid = me.normalGrid,
            lockedGrid = me.lockedGrid,
            normalView = normalGrid.view,
            lockedView = lockedGrid.view,
            lockedHCt  = lockedGrid.headerCt,
            refreshFlags,
            hadFocus;

        activeHd = lockedHCt.getMenu().activeHeader;
        hadFocus = activeHd.hasFocus;
        toCt = normalGrid.headerCt;

        Ext.suspendLayouts();

        lockedGrid.reconfiguring = normalGrid.reconfiguring = true;

        activeHd.ownerCmp = activeHd.ownerCt;

        for(var i=0;i<columns.length;i++){
	        if (columns[i].ownerCt) {
	            columns[i].ownerCt.remove(columns[i], false);
	        }
	        columns[i].locked = false;
	        toCt.insert(0, columns[i]);
        }
        lockedGrid.reconfiguring = normalGrid.reconfiguring = false;

        activeHd.ownerCmp = null;

        refreshFlags = me.syncLockedWidth();

        if (refreshFlags[1]) {
            normalGrid.getView().refreshView();
        }
        if (refreshFlags[0]) {
            lockedGrid.getView().refreshView();
        }
        me.fireEvent('unlockcolumn', me, activeHd);
        Ext.resumeLayouts(true);

        if (hadFocus) {
            activeHd.focus();
        }
    }
});

/**
 * gird支持跨锁列和不锁列之间拖动 column触发 columnlockmove事件
 */
Ext.override(Ext.grid.header.DropZone,{
	onNodeDrop: function(node, dragZone, e, data) {
		var headerCt       = this.headerCt,
		    dragHeader     = data.header;
        this.callParent(arguments);
        if (!this.valid) {
            return;
        }
        
        //添加自定义事件，表格里有锁列的时候，拖动列条顺序如果跨锁列和不锁列时，不会触发columnmove事件，此处手动触发 自定义事件
        if(data.isLock || data.isUnlock){
            headerCt.up("gridpanel[topGrid=true]").fireEvent("columnlockmove",headerCt,dragHeader);
        }
    }
	
});

//sting 获取字节长度（中文按2个长度计算）
Ext.getStringByteLength = function(value){
	var len = value.replace(/[^\x00-\xff]/gi, "--").length;
	return len;
};

/**
 *ext自带的方法与程序框架js有冲突，重新定义一下
 */
Ext.String.format=function(){
   var str = arguments[0];
   for(var i=1;i<arguments.length;i++){
       str = str.replace("{"+(i-1)+"}",arguments[i]);
   }
   return str;
};

/*当同时设置selModel和enableLocking时，如果没有列被锁，选择列出不来。*/
Ext.override(Ext.grid.Panel,{
    initComponent:function(){
        this.callParent(arguments);
        if(this.lockedGrid){
        		this.lockedGrid.setVisible(true);
        		//this.lockedGrid.setScrollable("x");
        	}
    }
});

/**
 * 添加列的过滤菜单
 */
Ext.override(Ext.grid.header.Container,{
	
	getMenuItems: function() {
		var me = this,
		    menuItems = me.callParent(arguments),
		    filterable = me.grid.viewConfig.filterable,
		    operaScope = me.grid.viewConfig.operaScope;
		//如果开启过滤功能
        if(filterable){
	        //插入过滤菜单
	        menuItems.push({
	            itemId: 'filterItem',
	            text: '过滤',
	            icon:'/components/tableFactory/tableGrid-theme/images/filter.png',
	            hideOnClick:true,
	            handler:function(){
	            	    var column = me.menu.activeHeader;
	            	    //调用过滤函数
	            	    operaScope.doColumnFilter(column);
	            	}
	        },{
	        	    text:'取消过滤',
	        	    itemId:'cancelFilter',
	        	    icon:'/components/tableFactory/tableGrid-theme/images/noFilter.png',
	        	    handler:function(){
	        	    		var column = me.menu.activeHeader;
	        	    		if(!column.filtered)
	        	    			return;
	        	    		operaScope.cancelColumnFilter(column);
	        	    }
	        });
        }
        //添加统计菜单
        var analysable = me.grid.viewConfig.analysable;
        if(analysable){
	        	menuItems.push({xtype:'menuseparator',itemId:'split'},{
	        		text:'统计',
	        		itemId:'analyse',
	        		handler:function(){
	        			var column = me.menu.activeHeader;
	        			operaScope.singleItemAnalyse({column:column});
	        		}
	        	});
        }
        
        //添加排序类型菜单
        if(me.sortable){
        		menuItems.splice(0,0,{
	        		xtype:'menucheckitem',
	        		itemId:'bhSort',
	        		text:'笔画',
	        		group:'sort',
	        		checked:true,
	        		handler:me.onDirectionCheck,
	        		sortType:'bh',
	        		scope:me
	        });
	        menuItems.splice(1,0,{
	        		xtype:'menucheckitem',
	        		itemId:'pySort',
	        		text:'拼音',
	        		group:'sort',
	        		handler:me.onDirectionCheck,
	        		sortType:'py',
	        		scope:me
	        });
        }
        return menuItems;
    },
    /*排序类型 菜单监听*/
    onDirectionCheck:function(item){
    		var menu = this.getMenu(),
            activeHeader = menu.activeHeader;
        activeHeader.sortType=item.sortType;
        activeHeader.sort(activeHeader.sortState||'ASC');
    },
    
 	/*自定义菜单根据列对象参数控制是否显示 */
    showMenuBy: function(clickEvent, t, header) {
        var menu = this.getMenu();
  
  		//过滤菜单显示控制
		var filterItem = menu.child("#filterItem");
		var cancelFilter = menu.child("#cancelFilter");
        if(filterItem){
        		filterItem.setVisible(header.filterable);
        		cancelFilter.setVisible(header.filterable);
        }
        
        //统计菜单显示控制
        var hideAnalyse = header.xtype != 'codecolumn' || (header.editor && (header.editor.xtype=='codeselectfield' || header.editor.codesource));
        var analyseItem = menu.child("#analyse");
        var splitItem = menu.child("#split");
        if(analyseItem){
        		analyseItem.setVisible(!hideAnalyse);
        		splitItem.setVisible(!hideAnalyse);
        }
        
        //排序类别菜单显示控制
    		var bhSort = menu.down('#bhSort');
        var pySort = menu.down('#pySort');
        if (bhSort) {
            bhSort.setVisible(header.sortable && header.showSortType);
            pySort.setVisible(header.sortable && header.showSortType);
        }
        
        this.callParent(arguments);
    }

});

/*解决表格控件列多时拖动导致表头和列错位的问题*/
Ext.override(Ext.grid.ColumnLayout,{
    publishInnerCtSize: function(ownerContext) {
        var me = this,
            owner = me.owner,
            cw = ownerContext.peek('contentWidth'),
            widthOffset = 0;
        if (cw != null && owner.isRootHeader) {
            /*主要就是这行代码，下面的都是Ext原来的代码*/
            widthOffset = -17;
        }
        var me = this,
            state = ownerContext.state,
            names = ownerContext.boxNames,
            heightName = names.height,
            widthName = names.width,
            align = ownerContext.boxOptions.align,
            padding = me.padding,
            plan = state.boxPlan,
            targetSize = plan.targetSize,
            height = plan.maxSize,
            needsScroll = state.needsScroll,
            innerCtContext = ownerContext.innerCtContext,
            innerCtWidth, innerCtHeight;

        if (ownerContext.parallelSizeModel.shrinkWrap || (plan.tooNarrow && state.canScroll)) {
            innerCtWidth = state.contentWidth - ownerContext.targetContext.getPaddingInfo()[widthName];
        } else {
            innerCtWidth = targetSize[widthName];
            if (needsScroll && needsScroll.perpendicular) {
                innerCtWidth -= Ext.getScrollbarSize()[widthName];
            }
        }
        innerCtWidth -= widthOffset;
        me.owner.tooNarrow = plan.tooNarrow;

        if (align.stretch) {
            innerCtHeight = height;
        } else {
            innerCtHeight = plan.maxSize + padding[names.beforeY] + padding[names.afterY] + innerCtContext.getBorderInfo()[heightName];

            if (!ownerContext.perpendicularSizeModel.shrinkWrap && (align.center || align.bottom)) {
                innerCtHeight = Math.max(targetSize[heightName], innerCtHeight);
            }
        }
        innerCtContext[names.setWidth](innerCtWidth);
        innerCtContext[names.setHeight](innerCtHeight);

        ownerContext.targetElContext.setWidth(ownerContext.innerCtContext.props.width -
            (me.vertical ? 0 : (widthOffset || 0)));

        if (isNaN(innerCtWidth + innerCtHeight)) {
            me.done = false;
        }
    }
});
