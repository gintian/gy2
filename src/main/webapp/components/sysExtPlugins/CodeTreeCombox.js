Ext.define('SYSP.CodeTreeCombox', {
    extend: 'Ext.form.field.Picker',
    xtype: 'codecomboxfield',
    triggerCls: Ext.baseCSSPrefix + 'form-arrow-trigger',
    config: {
        displayField: null,
        columns: null,
        rootVisible: false,
        selectOnTab: true,
        firstSelected: false,
        maxPickerWidth: 300,
        maxPickerHeight: 300,
        minPickerHeight: 200,
        enableKeyEvents:true
    },
    //是否限制只能选和codesetid相同的代码，例如codesetid=UN,不能选UM...
    onlySelectCodeset:true,
    autoShow:true,
    valueField: 'id',  
    displayField: 'text',
    //初始值，用于取消操作时
    keepObj:'`',
    //选中树节点的值的 text
    selectText:'',
    //editable: false,
    initComponent: function() {
        var me = this;
        me.callParent(arguments);
        this.addEvents('select');
        this.on("afterrender",this.callSearch,this);
    },
    parentid:'root',
    parentidFn:'Ext.emptyFn',
    afterCodeSelectFn:'Ext.emptyFn',
    //是否不受代码限制，可以自由输入想要的值
    inputable:false,
    createPicker: function() {
        var me = this;
        me.createStore();
        var picker = Ext.create('Ext.tree.Panel',{
        	    //pickerField: me,
	        	//ownerCt: me,
	            //ownerLayout: me.getComponentLayout(),
        		title:'代码',
                store: me.store,
                floating: true,
                hidden: true,
                width: me.maxPickerWidth,
                displayField: me.displayField,
                columns: me.columns,
                height:300,
                //maxHeight: me.maxPickerHeight,
                //minHeight:me.minPickerHeight,
                shadow: false,
                rootVisible: me.rootVisible,
                manageHeight: true,
                listeners: {
                    itemclick: Ext.bind(me.onItemClick, me),
                    hide:function(){
                           //设置为不接收输入状态 并且失去焦点且没有选中任何项 的时候还原值
                    	 if(!me.inputable){
                    	   if(me.selectText && me.getRawValue()!=me.selectText){me.setRawValue(me.selectText);}
                    	   if(!me.selectText)me.setRawValue(me.keepObj.split('`')[1]);
                    	 }
                    	   
                    	}
                    
                },
                viewConfig: {
                    listeners: {
                        render: function(view) {
                            view.getEl().on('keypress', me.onPickerKeypress, me);
                        }
                    }
                }
            });
            view = picker.getView();
            //xuj add 异步获取treepanel的title既代码类的desc
            Ext.Ajax.request({
                url: '/servlet/gridtable/GetCodeTreeServlet?codesetid='+me.codesetid,
                params: {
                	codesetid:me.codesetid,
                    istitle:'true'
                },
                success: function(response){
                    var text = response.responseText;
                    picker.setTitle(text);
                }
            });
        view.on('render', me.setPickerViewStyles, me);
        if (Ext.isIE9 && Ext.isStrict) {
            view.on('highlightitem', me.repaintPickerView, me);
            view.on('unhighlightitem', me.repaintPickerView, me);
            view.on('afteritemexpand', me.repaintPickerView, me);
            view.on('afteritemcollapse', me.repaintPickerView, me);
        }
        return picker;
    },
    setPickerViewStyles: function(view) {
        view.getEl().setStyle({
            'min-height': this.minPickerHeight + 'px',
            'max-height': this.maxPickerHeight + 'px'
        });
    },
    repaintPickerView: function() {
        var style = this.picker.getView().getEl().dom.style;
        style.display = style.display;
    },
    alignPicker: function() {
        var me = this,
            picker;

        if (me.isExpanded) {
            picker = me.getPicker();
            if (me.matchFieldWidth) {
                picker.setWidth(this.picker.getWidth());
            }
            if (picker.isFloating()) {
                me.doAlign();
            }
        }
    },
    onItemClick: function(view, record, node, rowIndex, e) {
    	if(record.data.codesetid!=this.codesetid && this.onlySelectCodeset)
    		return;
        this.selectItem(record);
    },
    onPickerKeypress: function(e, el) {
        var key = e.getKey();

        if(key === e.ENTER || (key === e.TAB && this.selectOnTab)) {
            this.selectItem(this.picker.getSelectionModel().getSelection()[0]);
        }
    },
    selectItem: function(record) {
        var me = this;
        me.setTreeValue(record.get(this.valueField || 'id'));
        //me.picker.hide();
        me.inputEl.focus();
        me.fireEvent('select', me, record);
        var fn = eval(me.afterCodeSelectFn);
        fn(me.dataIndex,me.value);
        me.collapse();
    },
    /*
     * 按值自动选中，暂时不用
    onExpand: function() {
        var me = this,
            picker = me.picker,
            store = picker.store,
            value = me.value;
        if(value) {
        	var node = store.getNodeById(value);
        	if(node)
            	picker.selectPath(node.getPath());
        } else {
        	var hasOwnProp = me.store.hasOwnProperty('getRootNode');
        	if(hasOwnProp)
            	picker.getSelectionModel().select(store.getRootNode());
        }

        Ext.defer(function() {
            picker.getView().focus();
        }, 1);
    },*/
    
    setTreeValue:function(value){
    	var me = this,record;
    	me.value = value;
    	
    	if (me.store.loading) {
            return me;
        }
        try{
        	var hasOwnProp = me.store.hasOwnProperty('getRootNode');
        	record = value ? me.store.getNodeById(value) : (hasOwnProp ? me.store.getRootNode() : null);
        	me.setRawValue(record ? record.get(this.displayField) : '');
        	me.selectText = record.get(this.displayField);
        }catch(e){
        	me.setRawValue('');
        }
        return me;
    },
    
    
    setValue: function(obj) {
        var me = this;
        if(me.inputable && obj && obj.split('`').length<2){
        	    me.value='',
        	    me.setRawValue(obj);
        	    me.keepObj = obj;
        }else{
	        if(!obj)
	        	obj='`';
		    me.keepObj = obj;	
		    me.selectText = '';
		    me.value = obj.split('`')[0];
		    me.setRawValue(obj.split('`')[1]);
        }
        if(me.picker){
          me.picker.collapseAll();
        }
        return me;
    },
    getValue: function() {
    	var rawValue = this.getRawValue();
    	var selectObj = this.value+"`"+rawValue;
    	if(this.inputable && rawValue!=this.keepObj && rawValue!=this.selectText){
    		return this.getRawValue();
    	}

    	if(rawValue == this.selectText)
    		return selectObj;
    	else
    		return this.keepObj;
    		
    },
    onLoad: function(store,node,records) {
        var value = this.value;
        if (value) {
            this.setValue(value);
        }else{
        	if(this.firstSelected){
	        	if(records && records.length > 0){
	        		var record = records[0];
	        		this.setValue(record.get(this.valueField));
	        	}
        	}
        }
        
    },
    getSubmitData: function() {
        var me = this,
            data = null;
        if (!me.disabled && me.submitValue) {
            data = {};
            data[me.getName()] = '' + me.getValue();
        }
        return data;
    },
    onTriggerClick: function() {
        var me = this;
        if (!me.readOnly && !me.disabled) {
            if (me.isExpanded) {
                me.collapse();
            } else {
            	if(me.store){
            		this.store.proxy.extraParams.searchtext=undefined;
            		var parentid = this.getParentId();
            	    this.store.proxy.extraParams.parentid = parentid;
        			this.store.load();//xuj update 重新显示代码项恢复默认展开状态 2015-1-21
            	}
                me.expand();
            }
            me.inputEl.focus();
        }
    },
    createStore:function(searchtext){
    	var me = this;
    	if(me.store){
    		return;
    	}
    	var parentid = this.getParentId();
    	var extraParams = {
    			codesource:me.codesource,
				ctrltype:me.ctrltype,
				nmodule:me.nmodule,
				codesetid:me.codesetid,
				parentid:parentid
    	};
    	if(searchtext)
    		extraParams.searchtext=searchtext;
    	var store = Ext.create('Ext.data.TreeStore',{
        	fields: ['text','id','codesetid'], 
            /*root: {  
                text:me.codedesc,  
                id:me.parentid,  
                expanded: true
            },*/
            proxy: {   
                type: 'ajax',   
                extraParams:extraParams,
                url: '/servlet/gridtable/GetCodeTreeServlet'
            }  
        });
    	
    	me.store = store;
    },
    callSearch:function(){
        	  this.inputEl.on("keyup",function(){
        		if(this.getRawValue().length==0){
        			this.setRawValue('`');
        			this.setValue('`');
        			this.collapse();
        		}else{
        			if(this.store){
        				if(this.store.proxy.extraParams.searchtext==encodeURI(this.getRawValue()) && this.isExpanded)
                  		  return;
        				var parentid = this.getParentId();
            			this.store.proxy.extraParams={
            					codesource:this.codesource,
            					ctrltype:this.ctrltype,
            					nmodule:this.nmodule,
            					codesetid:this.codesetid,
            					parentid:parentid,
            					searchtext:encodeURI(this.getRawValue())
            			};
            			this.store.load();

            		}else{
            			this.createStore(encodeURI(this.getRawValue()));
            		}
            		this.expand();
            		this.picker.show();
        		    this.focus();
        		}
        	},this);
    },
    getParentId:function(){
    	 var fn = eval(this.parentidFn);
    	 var parentid = fn(this.dataIndex);
    	 if(!parentid)
    		 return this.parentid;
    	 return parentid;
    }
});