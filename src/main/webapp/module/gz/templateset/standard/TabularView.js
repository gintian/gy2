Ext.define('Standard.TabularView', {
    extend: 'Ext.view.Table',
    xtype: 'tabularview',
    separator: '>', // 不同级别分割符
    sameLevelSeparator: '|', // 同级别分割符
    __rowspans: null, // 缓存要合并的列中每个单元格的rowspan, refresh时会删除重新计算
    __merge_columns: null, // 缓存要合并的列, refresh时会删除重新获取
    tpl: [
        '{%',
        'view = values.view;',
        'if (!(columns = values.columns)) {',
        'columns = values.columns = view.ownerCt.getVisibleColumnManager().getColumns();',
        '}',
        'values.fullWidth = 0;',
        // Stamp cellWidth into the columns
        'for (i = 0, len = columns.length; i < len; i++) {',
        'column = columns[i];',
        'values.fullWidth += (column.cellWidth = column.lastBox ? column.lastBox.width : column.width || column.minWidth);',
        '}',


        // Add the row/column line classes to the container element.
        'tableCls=values.tableCls=[];',
        '%}',
        '<div class="' + Ext.baseCSSPrefix + 'grid-item-container" role="presentation" style="width:{fullWidth}px">',
        '{[view.renderTHead(values, out, parent)]}',
        '<table id="{view.id}-table" class="{[tableCls]}" border="0" cellspacing="0" cellpadding="0" style="{tableStyle};table-layout:fixed;" {ariaTableAttr}>',
        '<tbody id="{view.id}-body" {ariaTbodyAttr}>',
        '{%',
        'view.renderRows(values.rows, values.columns, values.viewStartIndex, out);',
        '%}',
        '</tbody>',
        '</table>',
        //'{[view.renderTFoot(values, out, parent)]}',
        '</div>',
        // This template is shared on the Ext.view.Table prototype, so we have to
        // clean up the closed over variables. Otherwise we'll retain the last values
        // of the template execution!
        '{% ',
        'view = columns = column = null;',
        '%}',
        {
            definitions: 'var view, tableCls, columns, i, len, column;',
            strict: true,
            priority: 0
        }
    ],
    outerRowTpl: [
        '{%',
        'this.nextTpl.applyOut(values, out, parent)',
        '%}'
    ],
    rowTpl: [
        '{%',
        'var dataRowCls = values.recordIndex === -1 ? "" : " ' + Ext.baseCSSPrefix + 'grid-row";',
        '%}',
        '<tr id="{rowId}" class="{[values.itemClasses.join(" ")]} {[values.rowClasses.join(" ")]} {[dataRowCls]}"',
        ' data-boundView="{view.id}"',
        ' data-recordId="{record.internalId}"',
        ' data-recordIndex="{recordIndex}"',
        ' role="{rowRole}" {rowAttr:attributes}>',
        '<tpl for="columns">' +
        '{%',
        'parent.view.renderCell(values, parent.record, parent.recordIndex, parent.rowIndex, xindex - 1, out, parent)',
        '%}',
        '</tpl>',
        '</tr>',
        {
            priority: 0
        }
    ],
    cellTpl: [
        '<td class="{tdCls}" role="{cellRole}" {tdAttr} {cellAttr:attributes} rowspan="{rowspan}" <tpl if="hidden">style="display:none"</tpl>',
        ' style="width:{column.cellWidth}px;border-width: 0 1px 1px 0 ;border-bottom-color:#ededed;<tpl if="tdStyle">{tdStyle}</tpl>"',
        ' tabindex="-1" data-columnid="{[values.column.getItemId()]}">',
        '<div {unselectableAttr} class="' + Ext.baseCSSPrefix + 'grid-cell-inner {innerCls}" ',
        'style="text-align:{align};<tpl if="style">{style}</tpl>" ',
        '{cellInnerAttr:attributes}>{value}</div>',
        '</td>',
        {
            priority: 0
        }
    ],
    overItemCls: '',
    // Outer table
    bodySelector: 'div.' + Ext.baseCSSPrefix + 'grid-item-container table',


    // Element which contains rows
    nodeContainerSelector: 'div.' + Ext.baseCSSPrefix + 'grid-item-container tbody',


    // view item. This wraps a data row
    itemSelector: 'tr.' + Ext.baseCSSPrefix + 'grid-row',


    // Grid row which contains cells as opposed to wrapping item.
    rowSelector: 'tr.' + Ext.baseCSSPrefix + 'grid-row',


    // cell
    cellSelector: 'td.' + Ext.baseCSSPrefix + 'grid-cell',


    // Select column sizers and cells.
    // This may target `<COL>` elements as well as `<TD>` elements
    // `<COLGROUP>` element is inserted if the first row does not have the regular cell patten (eg is a colspanning group header row)
    sizerSelector: '.' + Ext.baseCSSPrefix + 'grid-cell',


    innerSelector: 'div.' + Ext.baseCSSPrefix + 'grid-cell-inner',
    initComponent: function () {
        var me = this;

        if (me.columnLines) {
            me.addCls(me.grid.colLinesCls);
        }
        if (me.rowLines) {
            me.addCls(me.grid.rowLinesCls);
        }

        /**
         * @private
         * @property {Ext.dom.Fly} body
         * A flyweight Ext.Element which encapsulates a reference to the view's main row containing element.
         * *Note that the `dom` reference will not be present until the first data refresh*
         */
        me.body = new Ext.dom.Fly();
        me.body.id = me.id + 'gridBody';

        // If trackOver has been turned off, null out the overCls because documented behaviour
        // in AbstractView is to turn trackOver on if overItemCls is set.
        if (!me.trackOver) {
            me.overItemCls = null;
        }

        me.headerCt.view = me;

        // Features need a reference to the grid.
        // Grid needs an immediate reference to its view so that the view can reliably be got from the grid during initialization
        me.grid.view = me;
        me.initFeatures(me.grid);

        me.itemSelector = me.getItemSelector();
        me.all = new Ext.view.NodeCache(me);

        me.callParent();
    },
    getRowByRecord: function (record) {
        return this.retrieveNode(this.getRowId(record), false);
    },
    getRowFromItem: function (item) {
        var rows = Ext.getDom(item),
            len = 1,
            i;
        for (i = 0; i < len; i++) {
            if (Ext.fly(rows).is(this.rowSelector)) {
                return rows;
            }
        }
    },
    /**
     * Returns the table row given the passed Record, or index or node.
     * @param {HTMLElement/String/Number/Ext.data.Model} nodeInfo The node or record, or row index.
     * to return the top level row.
     * @return {HTMLElement} The node or null if it wasn't found
     */
    getRow: function (nodeInfo) {
        var fly;

        if ((!nodeInfo && nodeInfo !== 0) || !this.rendered) {
            return null;
        }

        // An event
        if (nodeInfo.target) {
            nodeInfo = nodeInfo.target;
        }
        // An id
        if (Ext.isString(nodeInfo)) {
            return Ext.fly(nodeInfo).down(this.rowSelector, true);
        }
        // Row index
        if (Ext.isNumber(nodeInfo)) {
            fly = this.all.item(nodeInfo);
            return fly && fly.down(this.rowSelector, true);
        }
        // Record
        if (nodeInfo.isModel) {
            return this.getRowByRecord(nodeInfo);
        }
        fly = Ext.fly(nodeInfo);

        // Passed an item, go down and get the row
        if (fly.is(this.itemSelector)) {
            return this.getRowFromItem(fly);
        }

        // Passed a child element of a row
        return fly.findParent(this.rowSelector, this.getTargetEl()); // already an HTMLElement
    },

    getRowId: function (record) {
        return this.id + '-record-' + record.internalId;
    },
    /**
     * Get the cell (td) for a particular record and column.
     * @param {Ext.data.Model} record
     * @param {Ext.grid.column.Column} column
     * @private
     */
    getCell: function (record, column) {
        var row = this.getRow(record);
        return Ext.fly(row).down(column.getCellSelector());
    },
    /**
     * @private
     * Emits the HTML representing a single grid cell into the passed output stream (which is an array of strings).
     *
     * @param {Ext.grid.column.Column} column The column definition for which to render a cell.
     * @param {Number} recordIndex The row index (zero based within the {@link #store}) for which to render the cell.
     * @param {Number} rowIndex The row index (zero based within this view for which to render the cell.
     * @param {Number} columnIndex The column index (zero based) for which to render the cell.
     * @param {String[]} out The output stream into which the HTML strings are appended.
     */
    renderCell: function (column, record, recordIndex, rowIndex, columnIndex, out) {
        var me = this,
            fullIndex,
            selModel = me.selectionModel,
            cellValues = me.cellValues,
            classes = cellValues.classes,
            fieldValue = record.data[column.dataIndex],
            cellTpl = me.cellTpl,
            value, clsInsertPoint,
            lastFocused = me.navigationModel.getPosition(),
            rowspans = me.getRowspans();
        var rowspan = (rowspans[recordIndex] || {})[column.dataIndex];
        cellValues.record = record;
        // column.cellWidth = column.cellWidth-1;
        cellValues.column = column;
        cellValues.recordIndex = recordIndex;
        cellValues.rowIndex = rowIndex;
        cellValues.columnIndex = cellValues.cellIndex = columnIndex;
        cellValues.align = column.align;
        cellValues.innerCls = column.innerCls;
        cellValues.tdCls = cellValues.tdStyle = cellValues.tdAttr = cellValues.style = "";
        cellValues.unselectableAttr = me.enableTextSelection ? '' : 'unselectable="on"';

        // Begin setup of classes to add to cell
        classes[1] = column.getCellId();

        // On IE8, array[len] = 'foo' is twice as fast as array.push('foo')
        // So keep an insertion point and use assignment to help IE!
        clsInsertPoint = 2;

        if (column.renderer && column.renderer.call) {
            fullIndex = me.ownerCt.columnManager.getHeaderIndex(column);
            value = column.renderer.call(column.usingDefaultRenderer ? column : column.scope || me.ownerCt, fieldValue, cellValues, record, recordIndex, fullIndex, me.dataSource, me);
            if (cellValues.css) {
                // This warning attribute is used by the compat layer
                // TODO: remove when compat layer becomes deprecated
                record.cssWarning = true;
                cellValues.tdCls += ' ' + cellValues.css;
                cellValues.css = null;
            }

            // Add any tdCls which was added to the cellValues by the renderer.
            if (cellValues.tdCls) {
                classes[clsInsertPoint++] = cellValues.tdCls;
            }
        } else {
            value = fieldValue;
        }

        cellValues.value = (value == null || value === '') ? column.emptyCellText : value;

        if (column.tdCls) {
            classes[clsInsertPoint++] = column.tdCls;
        }
        if (me.markDirty && record.dirty && record.isModified(column.dataIndex)) {
            if(!record.get(column.dataIndex)&&!record.modified[column.dataIndex]){
            }else{
                classes[clsInsertPoint++] = me.dirtyCls;
            }
        }
        if (column.isFirstVisible) {
            classes[clsInsertPoint++] = me.firstCls;
        }
        if (column.isLastVisible) {
            classes[clsInsertPoint++] = me.lastCls;
        }
        if (!me.enableTextSelection) {
            classes[clsInsertPoint++] = me.unselectableCls;
        }
        if (selModel && (selModel.isCellModel || selModel.isSpreadsheetModel) && selModel.isCellSelected(me, recordIndex, column)) {
            classes[clsInsertPoint++] = me.selectedCellCls;
        }
        if (lastFocused && lastFocused.record.id === record.id && lastFocused.column === column) {
            classes[clsInsertPoint++] = me.focusedItemCls;
        }

        // Chop back array to only what we've set
        classes.length = clsInsertPoint;

        cellValues.tdCls = classes.join(' ');
        // cellTpl采用了display:none而不是不生成td, 因为若不生成td在使用rowediting时会出错
        cellValues.hidden = rowspan === 0;
        cellValues.rowspan = rowspan;
        cellTpl.applyOut(cellValues, out);

        // Dereference objects since cellValues is a persistent var in the XTemplate's scope chain
        cellValues.column = cellValues.record = null;
    },
    handleUpdate: function (store, record, operation, changedFieldNames) {
        operation = operation || Ext.data.Model.EDIT;
        var me = this,
            recordIndex = me.store.indexOf(record),
            rowTpl = me.rowTpl,
            markDirty = me.markDirty,
            dirtyCls = me.dirtyCls,
            clearDirty = operation !== Ext.data.Model.EDIT,
            columnsToUpdate = [],
            hasVariableRowHeight = me.variableRowHeight,
            updateTypeFlags = 0,
            ownerCt = me.ownerCt,
            cellFly = me.cellFly || (me.self.prototype.cellFly = new Ext.dom.Fly()),
            oldItemDom, oldDataRow,
            newItemDom,
            newAttrs, attLen, attName, attrIndex,
            overItemCls,
            columns,
            column,
            len, i,
            cellUpdateFlag,
            cell,
            fieldName,
            value,
            defaultRenderer,
            scope,
            elData,
            emptyValue;

        if (me.viewReady) {
            // Table row being updated
            oldItemDom = me.getNodeByRecord(record);

            // Row might not be rendered due to buffered rendering or being part of a collapsed group...
            if (oldItemDom) {
                overItemCls = me.overItemCls;
                columns = me.ownerCt.getVisibleColumnManager().getColumns();

                // Collect an array of the columns which must be updated.
                // If the field at this column index was changed, or column has a custom renderer
                // (which means value could rely on any other changed field) we include the column.
                for (i = 0, len = columns.length; i < len; i++) {
                    column = columns[i];

                    // We are not going to update the cell, but we still need to mark it as dirty.
                    if (column.preventUpdate) {
                        cell = Ext.fly(oldItemDom).down(column.getCellSelector(), true);

                        // Mark the field's dirty status if we are configured to do so (defaults to true)
                        if (!clearDirty && markDirty) {
                            cellFly.attach(cell);
                            if (record.isModified(column.dataIndex)) {
                                cellFly.addCls(dirtyCls);
                            } else {
                                cellFly.removeCls(dirtyCls);
                            }
                        }
                    } else {
                        // 0 = Column doesn't need update.
                        // 1 = Column needs update, and renderer has > 1 argument; We need to render a whole new HTML item.
                        // 2 = Column needs update, but renderer has 1 argument or column uses an updater.
                        cellUpdateFlag = me.shouldUpdateCell(record, column, changedFieldNames);

                        if (cellUpdateFlag) {
                            // Track if any of the updating columns yields a flag with the 1 bit set.
                            // This means that there is a custom renderer involved and a new TableView item
                            // will need rendering.
                            updateTypeFlags = updateTypeFlags | cellUpdateFlag; // jshint ignore:line

                            columnsToUpdate[columnsToUpdate.length] = column;
                            hasVariableRowHeight = hasVariableRowHeight || column.variableRowHeight;
                        }
                    }
                }

                // Give CellEditors or other transient in-cell items a chance to get out of the way
                // if there are in the cells destined for update.
                me.fireEvent('beforeitemupdate', record, recordIndex, oldItemDom, columnsToUpdate);
                // If there's no data row (some other rowTpl has been used; eg group header)
                // or we have a getRowClass
                // or one or more columns has a custom renderer
                // or there's more than one <TR>, we must use the full render pathway to create a whole new TableView item
                if (me.getRowClass || !me.getRowFromItem(oldItemDom) ||
                    (updateTypeFlags & 1) || // jshint ignore:line
                    (oldItemDom.childNodes.length > 1)) {
                    elData = oldItemDom._extData;
                    newItemDom = me.createRowElement(record, me.indexOfRow(record), columnsToUpdate);
                    if (Ext.fly(oldItemDom, '_internal').hasCls(overItemCls)) {
                        Ext.fly(newItemDom).addCls(overItemCls);
                    }

                    // Copy new row attributes across. Use IE-specific method if possible.
                    // In IE10, there is a problem where the className will not get updated
                    // in the view, even though the className on the dom element is correct.
                    // See EXTJSIV-9462
                    if (Ext.isIE9m && oldItemDom.mergeAttributes) {
                        oldItemDom.mergeAttributes(newItemDom, true);
                    } else {
                        newAttrs = newItemDom.attributes;
                        attLen = newAttrs.length;
                        for (attrIndex = 0; attrIndex < attLen; attrIndex++) {
                            attName = newAttrs[attrIndex].name;
                            if (attName !== 'id') {
                                oldItemDom.setAttribute(attName, newAttrs[attrIndex].value);
                            }
                        }
                    }

                    // The element's data is no longer synchronized. We just overwrite it in the DOM
                    if (elData) {
                        elData.isSynchronized = false;
                    }

                    // If we have columns which may *need* updating (think locked side of lockable grid with all columns unlocked)
                    // and the changed record is within our view, then update the view.
                    if (columns.length && (oldDataRow = me.getRow(oldItemDom))) {
                        me.updateColumns(oldDataRow, Ext.fly(newItemDom).dom, columnsToUpdate);
                    }

                    // Loop thru all of rowTpls asking them to sync the content they are responsible for if any.
                    while (rowTpl) {
                        if (rowTpl.syncContent) {
                            // *IF* we are selectively updating columns (have been passed changedFieldNames), then pass the column set, else
                            // pass null, and it will sync all content.
                            if (rowTpl.syncContent(oldItemDom, newItemDom, changedFieldNames ? columnsToUpdate : null) === false) {
                                break;
                            }
                        }
                        rowTpl = rowTpl.nextTpl;
                    }
                }

                // No custom renderers found in columns to be updated, we can simply update the existing cells.
                else {
                    // Loop through columns which need updating.
                    for (i = 0, len = columnsToUpdate.length; i < len; i++) {
                        column = columnsToUpdate[i];

                        // The dataIndex of the column is the field name
                        fieldName = column.dataIndex;

                        value = record.get(fieldName);
                        cell = Ext.fly(oldItemDom).down(column.getCellSelector(), true);
                        cellFly.attach(cell);

                        // Mark the field's dirty status if we are configured to do so (defaults to true)
                        if (!clearDirty && markDirty) {
                            if (record.isModified(column.dataIndex)) {
                                cellFly.addCls(dirtyCls);
                            } else {
                                cellFly.removeCls(dirtyCls);
                            }
                        }

                        defaultRenderer = column.usingDefaultRenderer;
                        scope = defaultRenderer ? column : column.scope;

                        // Call the column updater which gets passed the TD element
                        if (column.updater) {
                            Ext.callback(column.updater, scope, [cell, value, record, me, me.dataSource], 0, column, ownerCt);
                        } else {
                            if (column.renderer) {
                                value = Ext.callback(column.renderer, scope,
                                    [value, null, record, 0, 0, me.dataSource, me], 0, column, ownerCt);
                            }

                            emptyValue = value == null || value === '';
                            value = emptyValue ? column.emptyCellText : value;

                            // Update the value of the cell's inner in the best way.
                            // We only use innerHTML of the cell's inner DIV if the renderer produces HTML
                            // Otherwise we change the value of the single text node within the inner DIV
                            // The emptyValue may be HTML, typically defaults to &#160;
                            if (column.producesHTML || emptyValue) {
                                cellFly.down(me.innerSelector, true).innerHTML = value;
                            } else {
                                cellFly.down(me.innerSelector, true).childNodes[0].data = value;
                            }
                        }

                        // Add the highlight class if there is one
                        if (me.highlightClass) {
                            Ext.fly(cell).addCls(me.highlightClass);

                            // Start up a DelayedTask which will purge the changedCells stack, removing the highlight class
                            // after the expiration time
                            if (!me.changedCells) {
                                me.self.prototype.changedCells = [];
                                me.prototype.clearChangedTask = new Ext.util.DelayedTask(me.clearChangedCells, me.prototype);
                                me.clearChangedTask.delay(me.unhighlightDelay);
                            }

                            // Post a changed cell to the stack along with expiration time
                            me.changedCells.push({
                                cell: cell,
                                cls: me.highlightClass,
                                expires: Ext.Date.now() + 1000
                            });
                        }
                    }
                }

                // If we have a commit or a reject, some fields may no longer be dirty but may
                // not appear in the modified field names. Remove all the dirty class here to be sure.
                if (clearDirty && markDirty && !record.dirty) {
                    Ext.fly(oldItemDom, '_internal').select('.' + dirtyCls).removeCls(dirtyCls);
                }

                // Coalesce any layouts which happen due to any itemupdate handlers (eg Widget columns) with the final refreshSize layout.
                if (hasVariableRowHeight) {
                    Ext.suspendLayouts();
                }

                // Since we don't actually replace the row, we need to fire the event with the old row
                // because it's the thing that is still in the DOM
                me.fireEvent('itemupdate', record, recordIndex, oldItemDom);

                // We only need to update the layout if any of the columns can change the row height.
                if (hasVariableRowHeight) {
                    if (me.bufferedRenderer) {
                        me.bufferedRenderer.refreshSize();

                        // Must climb to ownerGrid in case we've only updated one field in one side of a lockable assembly.
                        // ownerGrid is always the topmost GridPanel.
                        me.ownerGrid.updateLayout();
                    } else {
                        me.refreshSize();
                    }

                    // Ensure any layouts queued by itemupdate handlers and/or the refreshSize call are executed.
                    Ext.resumeLayouts(true);
                }
            }
        }
    },
    updateColumns: function (oldRowDom, newRowDom, columnsToUpdate) {
        var me = this,
            newAttrs, attLen, attName, attrIndex,
            colCount = columnsToUpdate.length,
            colIndex,
            column,
            oldCell, newCell,
            cellSelector = me.getCellSelector();

        // Copy new row attributes across. Use IE-specific method if possible.
        // Must do again at this level because the row DOM passed here may be the nested row in a row wrap.
        if (oldRowDom.mergeAttributes) {
            oldRowDom.mergeAttributes(newRowDom, true);
        } else {
            newAttrs = newRowDom.attributes;
            attLen = newAttrs.length;
            for (attrIndex = 0; attrIndex < attLen; attrIndex++) {
                attName = newAttrs[attrIndex].name;
                if (attName !== 'id') {
                    oldRowDom.setAttribute(attName, newAttrs[attrIndex].value);
                }
            }
        }

        // Replace changed cells in the existing row structure with the new version from the rendered row.
        for (colIndex = 0; colIndex < colCount; colIndex++) {
            column = columnsToUpdate[colIndex];

            // Pluck out cells using the column's unique cell selector.
            // Becuse in a wrapped row, there may be several TD elements.
            cellSelector = me.getCellSelector(column);
            oldCell = Ext.fly(oldRowDom).selectNode(cellSelector);
            newCell = Ext.fly(newRowDom).selectNode(cellSelector);

            // Carefully replace just the *contents* of the cell.
            Ext.fly(oldCell).syncContent(newCell);
        }
    },
    /**
     * @private
     * Create the DOM element which enapsulates the passed record.
     * Used when updating existing rows, so drills down into resulting structure.
     */
    createRowElement: function (record, index, updateColumns) {
        var me = this,
            div = me.renderBuffer,
            tplData = me.collectData([record], index);

        tplData.columns = updateColumns;
        me.tpl.overwrite(div, tplData);

        // We don't want references to be retained on the prototype
        me.cleanupData();

        // Return first element within node containing element
        return Ext.fly(div).down(me.getNodeContainerSelector(), true).firstChild;
    },
    getRowspans: function () {
        var me = this;

        // 已经计算过直接返回
        var rowspans = me.__rowspans;
        if (rowspans != null) {
            return rowspans;
        }

        // 计算rowspan
        rowspans = [];
        var store = me.dataSource,
            mergeColumns = me.getMergeColumns();
        var setSameLevelRowspan = function (rowspans, rowIndex, columns, rowspan) {// 设置同级其他列的rowspan
            var i, temp, len = columns.length;
            for (i = 1; i < len; i++) {
                temp = columns[i];
                rowspans[rowIndex][temp] = rowspan;
            }
        };
        var calculateRowspans = function (rowspans, mergeColumns, currentColumnIndex, store, from, to) {
            if (currentColumnIndex >= mergeColumns.length) {
                return;
            }

            var columns = mergeColumns[currentColumnIndex],
                i, current, prev, mergeStart = 0;
            var column = columns[0];

            try {
                for (i = from; i < to + 1; i++) {
                    current = store.getAt(i).get(column);
                    if (current !== prev) {
                        rowspans[i] = rowspans[i] || {};
                        rowspans[i][column] = 1;
                        setSameLevelRowspan(rowspans, i, columns, 1);

                        // 递归获取子列
                        if (prev != null) {
                            calculateRowspans(rowspans, mergeColumns, currentColumnIndex + 1, store, mergeStart, i - 1);
                        }

                        prev = current;
                        mergeStart = i;
                    } else {
                        rowspans[mergeStart][column]++;
                        setSameLevelRowspan(rowspans, mergeStart, columns, rowspans[mergeStart][column]);

                        rowspans[i] = rowspans[i] || {};
                        rowspans[i][column] = 0;
                        setSameLevelRowspan(rowspans, i, columns, 0);
                    }
                }
                if (i > mergeStart) {
                    calculateRowspans(rowspans, mergeColumns, currentColumnIndex + 1, store, mergeStart, i - 1);
                }
            } catch (e) {
                if (console) {
                    console.error(e);
                }
            }
        };
        calculateRowspans(rowspans, mergeColumns, 0, store, 0, store.data.length - 1);

        // 缓存
        me.__rowspans = rowspans;

        return rowspans;
    },
    getMergeColumns: function () {
        var me = this;
        var columns = me.__merge_columns;
        if (columns != null) {
            return columns;
        }

        // 未配置时直接返回
        var mergeColumns = me.mergeColumns;
        if (Ext.isEmpty(mergeColumns)) {
            return [];
        }

        // 转换合并规则
        var separator = me.separator,
            sameLevelSeparator = me.sameLevelSeparator;
        columns = mergeColumns.split(separator);
        var i, len = columns.length;
        for (i = 0; i < len; i++) {
            columns[i] = columns[i].split(sameLevelSeparator);
        }

        // 缓存
        me.__merge_columns = columns;

        return columns;
    },
    renderRows: function (rows, columns, viewStartIndex, out) {
        var me = this,
            rowValues = me.rowValues,
            rowCount = rows.length,
            i;

        rowValues.view = me;
        rowValues.columns = columns;

        // The roles are the same for all data rows and cells
        rowValues.rowRole = me.rowAriaRole;
        me.cellValues.cellRole = me.cellAriaRole;

        for (i = 0; i < rowCount; i++, viewStartIndex++) {
            rowValues.itemClasses.length = rowValues.rowClasses.length = 0;
            me.renderRow(rows[i], viewStartIndex, out);
        }

        // Dereference objects since rowValues is a persistent on our prototype
        rowValues.view = rowValues.columns = rowValues.record = null;
    },
    /**
     * Refreshes the grid view. Sets the sort state and focuses the previously focused row.
     */
    refresh: function () {
        var me = this,
            scroller;
        if (me.destroying) {
            return;
        }
        delete me.__rowspans;
        me.callParent(arguments);

        me.headerCt.setSortState();

        // Create horizontal stretcher element if no records in view and there is overflow of the header container.
        // Element will be transient and destroyed by the next refresh.
        if (me.touchScroll && me.el && !me.all.getCount() && me.headerCt && me.headerCt.tooNarrow) {
            scroller = me.getScrollable();
            if (scroller && scroller.isTouchScroller) {
                scroller.setSize({
                    x: me.headerCt.getTableWidth(),
                    y: scroller.getSize().y
                });
            }
        }
    }
});