/**
 * 将html代码转成文本直接输出
 */
Ext.define("Ext.my.grid.column.Column",{
	override:'Ext.grid.column.Column',
	defaultRenderer:function(value){
		if(Ext.isString(value))
			return value.replace(/</g,"&lt;");
		return value;
	},
	cancelMark:function(){
    		if(!this.filtered)
	    	   return;
		var columnDom = this.getEl().child('.x-column-header-inner').dom;
			columnDom.removeChild(columnDom.getElementsByTagName("img")[0]);
			this.filtered = false;
	},
	filterMark:function(){
  	    if(this.filtered)
  	    	   return;
  	    this.getEl().child('.x-column-header-inner').insertFirst({tag:'img',flag:'filter',style:'position:absolute;left:2px',src:'/components/tableFactory/tableGrid-theme/images/filter2.png'});
  	    var filteredColumn = this.up('gridpanel[filterable=true]').query("gridcolumn[filtered=true]")[0];
  	    if(filteredColumn){
  	    		var columnDom = filteredColumn.getEl().child('.x-column-header-inner').dom;
  	    		columnDom.removeChild(columnDom.getElementsByTagName("img")[0]);
  	    		filteredColumn.filtered = false;
  	    }
  	    this.filtered=true;
    }
});

/**
 * 因date组件不支持时间填写， 这里复写 Ext.grid.column.Date，使此component能和时间编辑器数据对接
 */
Ext.define('Ext.my.grid.column.Date',{
	override:'Ext.grid.column.Date',
	defaultRenderer: function(value){
	   return Ext.isDate(value) ? Ext.Date.dateFormat(value, this.format) : value;
        
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
 * 自定义 column类型：combox 下拉列表类型，用于代码型指标
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
    	if(value){
    		return value.replace(/\n/g,'<br>');
    	}
    } 
    
});

/*
 * 表格分页工具，添加可以调每页条数功能
 */
Ext.define('TG.view.plugins.PagingTool',{
	override:'Ext.toolbar.Paging',
	getPagingItems: function() {
        var me = this,
            inputListeners = {
                scope: me,
                blur: me.onPagingBlur
            };
        
        inputListeners[Ext.EventManager.getKeyEvent()] = me.onPagingKeyDown;
        
        return [{
            itemId: 'first',
            tooltip: me.firstText,
            overflowText: me.firstText,
            iconCls: Ext.baseCSSPrefix + 'tbar-page-first',
            disabled: true,
            handler: me.moveFirst,
            scope: me
        },{
            itemId: 'prev',
            tooltip: me.prevText,
            overflowText: me.prevText,
            iconCls: Ext.baseCSSPrefix + 'tbar-page-prev',
            disabled: true,
            handler: me.movePrevious,
            scope: me
        },
        '-',
        me.beforePageText,
        {
            xtype: 'numberfield',
            itemId: 'inputItem',
            name: 'inputItem',
            cls: Ext.baseCSSPrefix + 'tbar-page-number',
            allowDecimals: false,
            minValue: 1,
            hideTrigger: true,
            enableKeyEvents: true,
            keyNavEnabled: false,
            selectOnFocus: true,
            submitValue: false,
            // mark it as not a field so the form will not catch it when getting fields
            isFormField: false,
            width: me.inputItemWidth,
            margins: '-1 2 3 2',
            listeners: inputListeners
        },{
            xtype: 'tbtext',
            itemId: 'afterTextItem',
            text: Ext.String.format(me.afterPageText, 1)
        },
        '-',
        {
            itemId: 'next',
            tooltip: me.nextText,
            overflowText: me.nextText,
            iconCls: Ext.baseCSSPrefix + 'tbar-page-next',
            disabled: true,
            handler: me.moveNext,
            scope: me
        },{
            itemId: 'last',
            tooltip: me.lastText,
            overflowText: me.lastText,
            iconCls: Ext.baseCSSPrefix + 'tbar-page-last',
            disabled: true,
            handler: me.moveLast,
            scope: me
        },
        '-',
        {
        	xtype: 'numberfield',
            itemId: 'inputCount',
            name: 'inputCount',
            cls: Ext.baseCSSPrefix + 'tbar-page-number',
            allowDecimals: false,
            minValue: 1,
            oldValue:me.pageSize,
            fieldLabel:'每页',
            labelWidth:30,
            listeners:{
            	blur:function(){
            		if(!Ext.isNumeric(this.value)){
            			this.setValue(this.oldValue);
            		}
            	}
            },
            width:70,
            hideTrigger: true,
            value:me.pageSize
        },
        {
            itemId: 'refresh',
            tooltip: me.refreshText,
            overflowText: me.refreshText,
            iconCls: Ext.baseCSSPrefix + 'tbar-loading',
            disabled: me.store.isLoading(),
            handler: me.doRefresh,
            scope: me
        }];
    },
    doRefresh : function(){
	    	var me = this,
	        store = me.store;
	        //current = store.currentPage;
	    	
	    	var pageRowCount = me.child('#inputCount').getValue();
	    	me.child('#inputCount').oldValue = pageRowCount;
	    	store.pageSize = pageRowCount;
	    	store.currentPage=1;
	    	if(store.cleanSelectData)//如果有此属性（表格控件中使用，保存表格的selModel），清空选中缓存
	    		store.cleanSelectData(false);
	    store.reload({params:{page:1,limit:pageRowCount}});
	    return true;

        //if (me.fireEvent('beforechange', me, current) !== false) {
        //    store.loadPage(current);
        //    return true;
        //}
        //return false;
    }
});	

/*
 * 锁列类，重写 锁列事件方法，支持多列同时锁
 */
Ext.define('Ext.my.grid.locking.Lockable',{
	override:'Ext.grid.locking.Lockable',
	onLockMenuClick:function(){
		var me            = this,
		    activeHd      = activeHd || this.normalGrid.headerCt.getMenu().activeHeader,
		    normalColumns = me.normalGrid.getColumnManager().getColumns();
		
		for(var i in normalColumns){
			if(normalColumns[i].dataIndex == activeHd.dataIndex)
				break;
			me.lock(normalColumns[i]);
		}
	    
		this.lock();
		me.fireEvent('lockcolumn', me, activeHd);
	},
	
    
	lock: function(activeHd, toIdx, toCt) {
        var me         = this,
            normalGrid = me.normalGrid,
            lockedGrid = me.lockedGrid,
            normalView = normalGrid.view,
            lockedView = lockedGrid.view,
            normalHCt  = normalGrid.headerCt,
            refreshFlags,
            ownerCt;

        activeHd = activeHd || normalHCt.getMenu().activeHeader;
        toCt = toCt || lockedGrid.headerCt;
        ownerCt = activeHd.ownerCt;

        // isLockable will test for making the locked side too wide
        if (!activeHd.isLockable()) {
            return;
        }

        // if column was previously flexed, get/set current width
        // and remove the flex
        if (activeHd.flex) {
            activeHd.width = activeHd.getWidth();
            activeHd.flex = null;
        }

        Ext.suspendLayouts();

        // We decide which views to refresh. Do not let the grids do it in response to column changes
        normalView.blockRefresh = lockedView.blockRefresh = true;
        ownerCt.remove(activeHd, false);
        activeHd.locked = true;

        // Flag to the locked column add listener to do nothing
        me.ignoreAddLockedColumn = true;
        if (Ext.isDefined(toIdx)) {
            toCt.insert(toIdx, activeHd);
        } else {
            toCt.add(activeHd);
        }
        me.ignoreAddLockedColumn = false;
        normalView.blockRefresh = lockedView.blockRefresh = false;

        refreshFlags = me.syncLockedWidth();
        if (refreshFlags[0]) {
            lockedGrid.getView().refresh();
        }
        if (refreshFlags[1]) {
            normalGrid.getView().refresh();
        }
        Ext.resumeLayouts(true);
    },
    
    
    onUnlockMenuClick: function() {
		var me        = this,
		activeHd      = activeHd || me.lockedGrid.headerCt.getMenu().activeHeader;
		lockedColumns = me.lockedGrid.getColumnManager().getColumns();
		
		for(var i= lockedColumns.length-1;i>0;i--){
			if(lockedColumns[i].dataIndex == activeHd.dataIndex)
				break;
			me.unlock(lockedColumns[i]);
		}
		
        this.unlock();
        
        me.fireEvent('unlockcolumn', me, activeHd);
    },
    unlock: function(activeHd, toIdx, toCt) {
        var me         = this,
            normalGrid = me.normalGrid,
            lockedGrid = me.lockedGrid,
            normalView = normalGrid.view,
            lockedView = lockedGrid.view,
            lockedHCt  = lockedGrid.headerCt,
            refreshFlags;

        // Unlocking; user expectation is that the unlocked column is inserted at the beginning.
        if (!Ext.isDefined(toIdx)) {
            toIdx = 0;
        }
        activeHd = activeHd || lockedHCt.getMenu().activeHeader;
        toCt = toCt || normalGrid.headerCt;

        Ext.suspendLayouts();

        // We decide which views to refresh. Do not let the grids do it in response to column changes
        normalView.blockRefresh = lockedView.blockRefresh = true;
        activeHd.ownerCt.remove(activeHd, false);
        activeHd.locked = false;
        toCt.insert(toIdx, activeHd);
        normalView.blockRefresh = lockedView.blockRefresh = false;

        // syncLockedWidth returns visible column counts for both grids.
        // only refresh what needs refreshing
        refreshFlags = me.syncLockedWidth();

        if (refreshFlags[0]) {
            lockedGrid.getView().refresh();
        }
        if (refreshFlags[1]) {
            normalGrid.getView().refresh();
        }
        Ext.resumeLayouts(true);
    }
});


//支持调整列顺 跨锁列和不锁列时触发列位置调整监听
Ext.override('Ext.my.grid.header.DropZone',{
	override:'Ext.grid.header.DropZone',
	onNodeDrop: function(node, dragZone, e, data) {
        // Note that dropLocation.pos refers to whether to drop the header before or after the target node!
        if (this.valid) {
            var dragHeader     = data.header,
                dropLocation   = data.dropLocation,
                targetHeader   = dropLocation.header,
                fromCt         = dragHeader.ownerCt,
                toCt           = targetHeader.ownerCt,
                sameCt         = fromCt === toCt,
                // Use the items collection here, the indices we want are for moving the actual items in the container.
                // The HeaderContainer translates this to visible columns for informing the view and firing events.
                localFromIdx   = fromCt.items.indexOf(data.header),
                localToIdx     = toCt.items.indexOf(targetHeader),
                headerCt       = this.headerCt,
                // Use the full column manager here, the indices we want are for moving the actual items in the container.
                // The HeaderContainer translates this to visible columns for informing the view and firing events.
                columns        = headerCt.visibleColumnManager,
                visibleFromIdx = columns.getHeaderIndex(dragHeader),
                // Group headers need to lookup the column index in the items collection NOT the leaf-only full column manager!
                visibleToIdx   = targetHeader.isGroupHeader ? toCt.items.indexOf(targetHeader) : columns.getHeaderIndex(targetHeader),
                colsToMove     = dragHeader.isGroupHeader ? dragHeader.query(':not([hidden]):not([isGroupHeader])').length : 1,
                // We really only need to know the direction for when dragging the last header of a group out of its grouping.
                // `true` === dragged to the right, `false` === dragged to the left.
                // Also, the direction is considered `true` (to the right) if the header is dropped directly adjacent to the group
                // in the 'after' position.
                direction      = targetHeader.isGroupHeader ? (dropLocation.pos === 'after') : columns.getHeaderIndex(targetHeader) > columns.getHeaderIndex(dragHeader),
                scrollerOwner, savedWidth;
            // Drop position is to the right of the targetHeader, increment the toIdx correctly. This is important
            // to allow the drop after the last header, for instance, else it would not be possible.
            if (dropLocation.pos === 'after') {
                localToIdx++;

                // Always increment the visibleToIdx index as this is used to swap the columns. Since the column swap uses
                // the inserBefore dom method, it must be incremented so it's one more than the slot for the new column.
                visibleToIdx += targetHeader.isGroupHeader ? targetHeader.query(':not([hidden]):not([isGroupHeader])').length : 1;
            }

            // If we are dragging in between two HeaderContainers that have had the lockable
            // mixin injected we will lock/unlock headers in between sections, and then continue
            // with another execution of onNodeDrop to ensure the header is dropped into the correct group
            
            if (data.isLock) {
                scrollerOwner = fromCt.up('[scrollerOwner]');
                scrollerOwner.lock(dragHeader, localToIdx, toCt);
                ////////////////////
                //添加自定义事件，表格里有锁列的时候，拖动列条顺序如果跨锁列和不锁列时，不会触发columnmove事件，此处手动触发 自定义事件
                headerCt.up("gridpanel").up("gridpanel").fireEvent("columnlockmove",headerCt,dragHeader);
            } else if (data.isUnlock) {
                scrollerOwner = fromCt.up('[scrollerOwner]');
                scrollerOwner.unlock(dragHeader, localToIdx, toCt);
                ////////////////////
                //添加自定义事件，表格里有锁列的时候，拖动列条顺序如果跨锁列和不锁列时，不会触发columnmove事件，此处手动触发 自定义事件
                headerCt.up("gridpanel").up("gridpanel").fireEvent("columnlockmove",headerCt,dragHeader);
            }

            // This is a drop within the same HeaderContainer.
            else {
                this.invalidateDrop();
                // Cache the width here, we need to get it before we removed it from the DOM
                savedWidth = dragHeader.getWidth();
                
                // Dragging within the same container.
                if (sameCt) {
                    // If dragging rightwards, then after removal, the insertion index will be less.
                    if (localToIdx > localFromIdx) {
                        localToIdx -= 1;
                    }
                    // A no-op. This can happen when cross lockable drag operations recurse (see above).
                    // If a drop was a lock/unlock, and the lock/unlock call placed the column in the
                    // desired position (lock places at end, unlock places at beginning) then we're done.
                    if (localToIdx === localFromIdx) {
                        // We still need to inform the rest of the components so that events can be fired.
                        headerCt.onHeaderMoved(dragHeader, colsToMove, visibleFromIdx, visibleToIdx);
                        return;
                    }
                }
                
                // Suspend layouts while we sort all this out.
                Ext.suspendLayouts();
                
                if (sameCt) {
                    toCt.move(localFromIdx, localToIdx);
                } else {
                    // Do a sanity!
                    //
                    // After the offsets are calculated, the visibleToIdx and the localToIdx indices should not be equal
                    // for when the header is dragged to the right. This can happen, however, when the header that is moved
                    // is the last in a grouped header and it's moved directly to the right of the group in which it's
                    // contained (the drag position doesn't matter, either 'before' or 'after'). Therefore, we must decrement
                    // the localToIdx index otherwise the header will be +1 offset from its data column.
                    if (direction && (visibleToIdx === localToIdx)) {
                        localToIdx -= 1;
                    }

                    // When removing and then adding, the owning gridpanel will be informed of column mutation twice
                    // Both remove and add handling inform the owning grid.
                    // The isDDMoveInGrid flag will prevent the remove operation from doing this.
                    // See Ext.grid.header.Container#onRemove
                    fromCt.isDDMoveInGrid = toCt.isDDMoveInGrid = !data.crossPanel;
                    fromCt.remove(dragHeader, false);
                    toCt.insert(localToIdx, dragHeader);
                    fromCt.isDDMoveInGrid = toCt.isDDMoveInGrid = false;
                }
                
                // Group headers skrinkwrap their child headers.
                // Therefore a child header may not flex; it must contribute a fixed width.
                // But we restore the flex value when moving back into the main header container
                if (toCt.isGroupHeader) {
                    // Adjust the width of the "to" group header only if we dragged in from somewhere else.
                    if (!sameCt) {
                        dragHeader.savedFlex = dragHeader.flex;
                        delete dragHeader.flex;
                        dragHeader.width = savedWidth;
                    }
                } else {
                    if (dragHeader.savedFlex) {
                        dragHeader.flex = dragHeader.savedFlex;
                        delete dragHeader.width;
                    }
                }

                Ext.resumeLayouts(true);
                
                // If moving within the same container, the container's onMove method will have ensured that the top level
                // headerCt's onHeaderMoved.
                if (!sameCt) {
                    headerCt.onHeaderMoved(dragHeader, colsToMove, visibleFromIdx, visibleToIdx);
                    
                    //alert(1);
                    //headerCt.up("gridpanel").fireEvent("columnmove",[headerCt,dragHeader]);
                }

                // Ext.grid.header.Container will handle the removal of empty groups, don't handle it here
                
            }
        }
    }
});
Ext.define('Ext.my.form.field.Text',{
	override:'Ext.form.field.Text',
	initComponent:function(){
		this.callParent([arguments]);
		if(!this.allowBlank)
			this.allowOnlyWhitespace=false;
	}
});

/**
 * 添加列的过滤菜单
 */
Ext.override(Ext.grid.header.Container,{
	
	getMenuItems: function() {
		var me = this,
		    menuItems = this.callParent([arguments]);
        //查询启用过滤功能的gridpanel
        var gridpanel = me.up('gridpanel[filterable=true]',2);
        //没找找到，返回默认菜单
        if(gridpanel){
	        //找到了，插入 过滤菜单项目
	        menuItems.push({
	            itemId: 'filterItem',
	            text: '过滤',
	            icon:'/components/tableFactory/tableGrid-theme/images/filter.png',
	            hideOnClick:true,
	            handler:function(){
	            	    var column = me.menu.activeHeader;
	            	    //调用过滤函数
	            	    Ext.callback(gridpanel.columnFilterHandler,gridpanel.scope,[column]);
	            	},
	            	listeners:{
	            		render:function(){
	            			var column = me.menu.activeHeader;
	            			if(!column.filterable){
	            				this.setVisible(false);
	            			}
	            			this.parentMenu.on('beforeshow',function(){
	        					var c = this.activeHeader;
	        					if(c.filterable){
		        					this.child("#filterItem").setVisible(true);
	        						this.child("#cancleFilter").setVisible(true);
	        					}else{
	        						this.child("#filterItem").setVisible(false);
	        						this.child("#cancleFilter").setVisible(false);
	        					}
	        				});
	            		}
	            	}
	        },{
	        	    text:'取消过滤',
	        	    itemId:'cancleFilter',
	        	    icon:'/components/tableFactory/tableGrid-theme/images/noFilter.png',
	        	    handler:function(){
	        	    		var column = me.menu.activeHeader;
	        	    		if(!column.filtered)
	        	    			return;
	        	    		Ext.callback(gridpanel.scope.cancelFilter,gridpanel.scope,[column]);
	        	    },
	        	    listeners:{
	        	    	     render:function(){
	        	    	    	 var column = me.menu.activeHeader;
	 	            			if(!column.filterable){
	 	            				this.setVisible(false);
	 	            			}
	        	    	     }
	        	    }
	        });
        }
        //添加统计菜单
        var gridpanel2 = me.up('gridpanel[analysable=true]',2);
        if(gridpanel2){
	        	menuItems.push({xtype:'menuseparator',itemId:'split'},{
	        		text:'统计',
	        		itemId:'analyse',
	        		handler:function(){
	        			var column = me.menu.activeHeader;
	        			var scope = this.up('gridpanel[analysable=true]').scope;
	        			Ext.callback(scope.singleItemAnalyse,scope,[{column:column}]);
	        		},
	        		listeners:{
	        			render:function(){
	        				var column = me.menu.activeHeader;
	        				if(column.xtype != 'codecolumn' || (column.editor && (column.editor.xtype=='codeselectfield' || column.editor.codesource))){
	        					this.setVisible(false);
	        					this.parentMenu.child('#split').setVisible(false);
	        				}
	        				this.parentMenu.on('beforeshow',function(){
	        					var c = this.activeHeader;
	        					if(c.xtype != 'codecolumn' || (c.editor && (c.editor.xtype=='codeselectfield' || c.editor.codesource))){
		        					this.child("#split").setVisible(false);
	        						this.child("#analyse").setVisible(false);
	        					}else{
	        						this.child("#split").setVisible(true);
		        					this.child("#analyse").setVisible(true);
	        					}
	        				});
	        			}
	        			
	        		}
	        	});
        }
        
        return menuItems;
    }

});

/**
 * 统计插件 支持 后台动态统计
 */
Ext.override(Ext.grid.feature.Summary,{
	
	createSummaryRecord: function (view) {
        var columns = view.headerCt.getVisibleGridColumns(),
            info = {
                records: view.store.getRange()
            },
            colCount = columns.length, i, column,
            summaryRecord = this.summaryRecord || (this.summaryRecord = new view.store.model(null, view.id + '-summary-record')),
            dataIndex, summaryValue;

        // Set the summary field values
        summaryRecord.beginEdit();
        
        /**
         * 支持后台计算功能
         */
        var summaryData = undefined;
        if(this.remoteRoot)
        	   summaryData = view.store.proxy.reader.jsonData[this.remoteRoot];//获取后台计算数据
        for (i = 0; i < colCount; i++) {
            column = columns[i];

            // In summary records, if there's no dataIndex, then the value in regular rows must come from a renderer.
            // We set the data value in using the column ID.
            dataIndex = column.dataIndex || column.id;

            if(summaryData)//如果有后台数据，获取后台数据
            		summaryValue = summaryData[dataIndex];
            else//没有计算当页数据
            		summaryValue = this.getSummary(view.store, column.summaryType, dataIndex, info);
            summaryRecord.set(dataIndex, summaryValue);

            // Capture the columnId:value for the summaryRenderer in the summaryData object.
            this.setSummaryData(summaryRecord, column.id, summaryValue);
        }

        summaryRecord.endEdit(true);
        // It's not dirty
        summaryRecord.commit(true);
        summaryRecord.isSummary = true;
        return summaryRecord;
    }
});

//window控件一些公共的属性，为了统一样式
Ext.define('Ext.additional.window.Window',{
	override:'Ext.window.Window',
	constrain:true,
	border:false,
	shadow:false,
	bodyStyle:"background-color:white;",
	resizable:true
});

//sting 获取字节长度（中文按2个长度计算）
Ext.getStringByteLength = function(value){
	var len = value.replace(/[^\x00-\xff]/gi, "--").length;
	return len;
};