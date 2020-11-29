Ext.define('Standard.SelectTreeFieldCombox', {
    requires: ["EHR.extWidget.proxy.TransactionProxy", "EHR.fielditemselector.FieldItemSelector"],
    extend: 'Ext.form.field.Picker',
    xtype: 'selectTreeFieldCombox',
    width:250,
    source: '',
    codeFilter: true,
    border:true,
    StructureDetail:undefined,
    refreshSoreIdtree:undefined,
    refreshSoreIdgrid:undefined,
    filterItems: '',
    filterTypes: undefined,//过滤指标类型  wangb 2019-02-20
    //选择完毕后调用的函数
    afterCodeSelectFn: undefined,
    inputable: false,
    //是否多选
    multiple: false,
    initComponent: function () {
        this.callParent();
        var me = this;
        Ext.tip.QuickTipManager.init();
        this.treeStore = Ext.create('Ext.data.TreeStore', {
            storeId: 'selectFieldStore',
            fields: ["itemdesc", "fieldsetid", "fieldsetdesc", "itemtype"],
            proxy: {
                type: 'transaction',
                functionId: 'GZ00001222',
                extraParams: {
                    source: me.source,
                    multiple: me.multiple,
                    filterItems: me.filterItems,
                    filterTypes: me.filterTypes,
                    codeFilter:me.codeFilter
                }
            }
        });
    },
    pickerfocusable: true,

    /*创建选择下拉框*/
    createPicker: function () {
        var me = this;
        var docks = me.multiple ? {
            xtype: 'toolbar', dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    text: '确定',
                    handler: me.multipleSelected,
                    scope: me,
                    style: {
                        background: 'white',
                        border: '1',/*borderColor: '#98c0f4',borderStyle:'solid',*/
                        height: '21',
                        width: '38'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消',
                    handler: me.cancelSelect,
                    scope: me,
                    style: {
                        background: 'white',
                        border: '1',/*borderColor: '#98c0f4',borderStyle:'solid',*/
                        height: '21',
                        width: '38'
                    }
                },
                '->'
            ]
        } : undefined;
        var picker = Ext.create('Ext.tree.Panel', {
            floating: true,
            hidden: true,
            minWidth: 220,
            height: 250,
            shadow: false,
            rootVisible: false,
            style: 'overflow:unset;',
            store: me.treeStore,
            listeners: {
                itemclick: me.multiple ? Ext.emptyFn : me.doSelect,
                scope: me
            }
        });
        return picker;

    },
    /*单选模式选中处理*/
    doSelect: function (view, record, node, rowIndex, e) {
        var me = this;
        if(!record.data.leaf){
        	return;
        }
        if (this.selectValidator) {
            var fn = Ext.isFunction(me.selectValidator) ? me.selectValidator : eval(me.selectValidator);
            var result = fn(record);
            if (result != undefined && !result)
                return;
        }
        this.setValue(record.get('id') + "`" + record.get('text'), true);
        me.fireEvent('select', me, record);
        if (me.afterCodeSelectFn) {
            var fn = Ext.isFunction(me.afterCodeSelectFn) ? me.afterCodeSelectFn : eval(me.afterCodeSelectFn);
            fn(me.dataIndex, record.get('id'));
        }
        me.collapse();
    },
    /*多选模式选中处理*/
    multipleSelected: function () {
        var checked = this.picker.getChecked();
        var values = [];
        var ids = "";
        var texts = "";
        for (var i = 0; i < checked.length; i++) {
            //回调数据拼装：勾选的机构id、机构描述、机构parentId(用于校验是否是同级机构等)
            values.push({id: checked[i].get('id'), text: checked[i].get('text'), parentId: checked[i].parentNode.id});
            ids += checked[i].get('id') + ",";
            texts += checked[i].get('text') + ",";
        }
        ids = ids.substring(0, ids.length - 1);
        texts = texts.substring(0, texts.length - 1);
        this.setValue(ids + "`" + texts);
        this.fireEvent("multiplefinish", values);
        this.collapse();
    },
    /*取消选择按钮*/
    cancelSelect: function () {
        this.collapse();
    },
    /*动态获取parentid*/
    getParentId: function () {
        if (!this.parentidFn)
            return this.parentid;

        var fn = Ext.isFunction(this.parentidFn) ? this.parentidFn : eval(this.parentidFn);
        return fn(this.dataIndex, this.column.currentRecord);
    },
    /*设置初始值
     *params:
     *    value:初始显示值，格式为  代码id`代码描述
     *    doSelect:是否是此组件调用此方法
     *
     */
    setValue: function (value) {
        //如果value格式不对，当做空处理
        value = value ? value : '';
        if (value.indexOf('`') == -1) {
            if (this.inputable)
                this.setRawValue(value);
            else {
                this.value = '';
                this.setRawValue('');
            }
            /*触发后续事件，例如数据校验等 */
            this.checkChange();
            return;
        }
        //分离代码id和描述显示
        this.value = value.split('`')[0];
        this.setRawValue(value.split('`')[1]);
        /*触发后续事件，例如数据校验等 */
        this.checkChange();
    },
    /*formpanel的getValue()方法 获取的是getRawValue()值，改为getValue()*/
    getSubmitValue: function () {
        return this.getValue();
    },
    /*获取选择后的值*/
    getValue: function () {
        var value = this.value;
        var rawValue = this.getRawValue();
        value = value ? value : '';
        rawValue = rawValue ? rawValue : '';
        //清空选项
        if (rawValue.length < 1) {
            return "";
        } else if (value.length < 1 && rawValue.length > 0) {//搜索状态，有输入值，没有选择值，返回原值
            if (this.inputable)
                return rawValue;
            else
                return this.orignValue;
        } else //返回选中的代码值
            return this.value + "`" + this.getRawValue();
    },
    /*当值改变时执行搜索*/
    onFieldMutation: function (e) {
        var me = this;
        /*只有在输入的时候才执行搜索
          判断规则：是键盘输入(keyup事件判断)，并且不是特殊控制键(enter\shift\ctrl\tab)时，执行搜索
        */
        if (e.type != 'keyup') {
            return;
        }

        if (e.isSpecialKey() && e.getKey() != e.BACKSPACE)
            return;
        if(!this.getRawValue()){
        	var refrshStoretree = Ext.data.StoreManager.lookup(me.refreshSoreIdtree);
        	var refrshStoregrid = Ext.data.StoreManager.lookup(me.refreshSoreIdgrid);
        	if(refrshStoretree){
        		refrshStoretree.proxy.extraParams.isEmpty = true;
        		refrshStoretree.load();
        	} else if(refrshStoregrid){
        		var childContainer = me.StructureDetail.query("#" + me.StructureDetail.prefix +'secondLevelContainer')[0];
            	childContainer.removeAll(true);
        	}
        }
        this.treeStore.proxy.extraParams.querykey = this.getRawValue();
        this.treeStore.load();

        //更新picker的焦点属性
        this.updatePickerFocus();

        if (!this.isExpanded && this.treeStore.proxy.extraParams.querykey) {
            this.expand(true);
        }
    },
    updatePickerFocus: function () {
        if (this.getRawValue() && this.getRawValue().length > 0) {
            if (!this.picker)
                this.pickerfocusable = false;
            else
                this.picker.view.focusable = false;
        } else {
            if (!this.picker)
                this.pickerfocusable = true;
            else
                this.picker.view.focusable = true;
        }
    },
//    onExpand:function(keepsearch){
//    	if(!keepsearch){
//    		this.treeStore.proxy.extraParams.searchtext="";
//    		this.treeStore.load();
//    	}
//    	
//    },
    onCollapse: function () {
        this.picker.view.focusable = true;
    },
    doAlign: function () {
        var clientWidth = document.body.clientWidth;

        var me = this,
            picker = me.picker,
            aboveSfx = '-above',
            isAbove,
            newAlign = me.pickerAlign;
        //针对IE8做一下处理
        if (Ext.isIE8m && me.triggerWrap.getX() + 250 > clientWidth)
            newAlign = "tr-br";
        me.picker.alignTo(me.triggerWrap, newAlign, me.pickerOffset);
        isAbove = picker.el.getY() < me.inputEl.getY();
        me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
        picker[isAbove ? 'addCls' : 'removeCls'](picker.baseCls + aboveSfx);

    },
    isExt6: function () {
        var version = Ext.getVersion() + "";
        var flag = false;
        if (version)
            flag = version.substring(0, 1) == '6' ? true : false;
        return flag
    }


});