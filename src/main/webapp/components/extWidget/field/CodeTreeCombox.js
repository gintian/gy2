Ext.define('EHR.extWidget.field.CodeTreeCombox', {
    requires:["EHR.extWidget.proxy.TransactionProxy"],
    extend: 'Ext.form.field.Picker',
    xtype: 'codecomboxfield',
    //需要的参数
    config:{
    		//代码类（必须）
        codesetid:undefined,
        //虚拟机构
        vorg:undefined,
        //自定义代码类路径
        codesource:undefined,
        //权限控制方式 0：不控制 1：人员范围 2：操作单位 3：业务范围（必须设置nmodule值，否则没数据）
        ctrltype:'1',
        //业务模块号
        nmodule:undefined,
        //父节点id
        parentid:undefined,
        //动态获取父节点id函数
        parentidFn:undefined,
        //选择完毕后调用的函数
    		afterCodeSelectFn:undefined,
    		//是否只能选设置的codesetid一致的项（例如codesetid为岗位代码，加载时会加载单位部门，如果设置为true则只能选岗位，如果为false则单位部门都可以选）
    		//如果是普通代码，则只能选叶子节点
    		onlySelectCodeset:true,
    		//代码选择校验函数
    		selectValidator:undefined,
    		inputable:false,
    		//是否多选
    		multiple:false
    },
    initComponent:function(){
    		this.callParent();
    		this.createStore();
    		Ext.tip.QuickTipManager.init();
    },
    listeners: {
    		//enter键快捷补全  xiegh
    	   specialkey: function(field, e){
    	        if (e.keyCode==13) {
    	        	if(!this.treeStore.proxy.extraParams.searchtext)
    	        		return;
    		       	 var record = this.treeStore.getData(0).items[0].data;
    		       	 if(this.treeStore.getData(0).items.length ==1){//模糊加载数据为一条时，才做补全
	    		       	 this.setValue(record.id+"`"+record.text,true);
	    		       	 return;
    		       	 }
    	        }
    	    }
    },
 
    
    pickerfocusable:true,
    /*创建treestore*/
    createStore:function(){
	    	var me = this;
	    	var parentid = this.getParentId();
	    	var extraParams = {
	    				codesource:me.codesource,
					ctrltype:me.ctrltype,
					nmodule:me.nmodule,
					codesetid:me.codesetid,
					parentid:parentid,
					multiple:me.multiple,
					vorg:me.vorg,
					onlySelectCodeset:me.onlySelectCodeset
	    	};
	    	me.treeStore = Ext.create('Ext.data.TreeStore',{
	        	fields: ['text','id','codesetid','orgtype','selectable'], 
	            proxy: {   
	                type:'transaction',
	                extraParams:extraParams,
	                functionId:'ZJ100000131'
	            }  
	     });
	    	
    },
    
    /*创建选择下拉框*/
	createPicker: function() {
		var me = this;
		var docks = me.multiple?{
			xtype:'toolbar',dock:'bottom',
			items:[
			    '->',    
				{xtype:'button',text:'确定',handler:me.multipleSelected,scope:me,style:{background:'white',border:'1',/*borderColor: '#98c0f4',borderStyle:'solid',*/height:'21',width:'38'}},
				{xtype:'button',text:'取消',handler:me.cancelSelect,scope:me,style:{background:'white',border:'1',/*borderColor: '#98c0f4',borderStyle:'solid',*/height:'21',width:'38'}},
				'->'
			]
		}:undefined;
		var picker = Ext.create('Ext.tree.Panel',{
	            dockedItems:docks,
                store:me.treeStore,
                floating: true,
                hidden: true,
                minWidth:250,
                height:250,
                shadow: false,
                rootVisible:false,
                style:'overflow:unset;z-index:9999999999',
                listeners: {
                    itemclick:me.multiple?Ext.emptyFn:me.doSelect,
                    scope:me
                },
                viewConfig: {
                		focusable:me.pickerfocusable
                }
            });
		me.pickerid = picker.id;
        return picker;
	
	},
	/*单选模式选中处理*/
	doSelect:function(view, record, node, rowIndex, e){
	    var me = this;
	    if(this.codesetid == 'UN' || this.codesetid == 'UM' || this.codesetid == '@K'){
			if(this.onlySelectCodeset && record.data.codesetid != this.codesetid){//特殊处理UM UN @K 走原参数控制  
				return;
			}
		}else{
		    if(record.data.selectable == 'false'){
		    	// if(me.isExt6()){
				 //  	Ext.Msg.show({
	             //        title:"提示信息",
	             //        msg: "此代码类中只有末级代码项可以选！",
	             //        buttons: Ext.Msg.YES,
	             //        buttonText: {
	    		// 				yes: '确认'
					// 		}
	             //    });
		    	// }else alert("此代码类中只有末级代码项可以选！");
		    	
				return;
		    }
		}
	    if(this.selectValidator){
	        var fn = Ext.isFunction(me.selectValidator)?me.selectValidator:eval(me.selectValidator);
    	    var result = fn(record);
    	    if(result!=undefined && !result)
    	        return;
	    }
        this.setValue(record.get('id')+"`"+record.get('text'),true);
        me.fireEvent('select', me, record);
        if(me.afterCodeSelectFn){
        		var fn = Ext.isFunction(me.afterCodeSelectFn)?me.afterCodeSelectFn:eval(me.afterCodeSelectFn);
        		fn(me.dataIndex,record.get('id'));
        }
        me.collapse();
	},
	/*多选模式选中处理*/
	multipleSelected:function(){
		var checked = this.picker.getChecked( );
        var values = [];
        var ids = "";
        var texts = "";
        for(var i=0;i<checked.length;i++){
              //回调数据拼装：勾选的机构id、机构描述、机构parentId(用于校验是否是同级机构等)
              values.push({id:checked[i].get('id'),text:checked[i].get('text'),parentId:checked[i].parentNode.id});
              ids+=checked[i].get('id')+",";
              texts+=checked[i].get('text')+",";
        }
        ids = ids.substring(0,ids.length-1);
        texts = texts.substring(0,texts.length-1);
        this.setValue(ids+"`"+texts);
        this.fireEvent("multiplefinish",values);
        this.collapse();
	},
	/*取消选择按钮*/
	cancelSelect:function(){
       this.collapse();
    },
    /*动态获取parentid*/
    getParentId:function(){
         if(!this.parentidFn)
         	return this.parentid;
         	
         var fn = Ext.isFunction(this.parentidFn)?this.parentidFn:eval(this.parentidFn);
         return fn(this.dataIndex,this.column.currentRecord);
    },
    /*设置初始值
     *params:
     *    value:初始显示值，格式为  代码id`代码描述
     *    doSelect:是否是此组件调用此方法
     *
     */
    setValue:function(value,doSelect){
        /*如果是外部调用的此方法，保存一下初始值，初始化一下store*/
    		if(!doSelect){
    		    this.orignValue = value;
    		    if(this.treeStore){
	    		    this.treeStore.proxy.extraParams.searchtext = '';
	    			this.treeStore.proxy.extraParams.parentid = this.getParentId();
	    			this.treeStore.load();
	    		}
    		}
        
        //如果value格式不对，当做空处理
        value = value?value:'';
        if(value.indexOf('`')==-1){
            if(this.inputable)
               this.setRawValue(value);
            else{
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
    getSubmitValue:function(){
    	return this.getValue();
    },
    /*获取选择后的值*/
    getValue:function(){
        var value = this.value;
        var rawValue = this.getRawValue();
        value = value?value:'';
        rawValue = rawValue?rawValue:'';
        //清空选项
        if(rawValue.length<1){
            return "";
        }else if(value.length<1 && rawValue.length>0){//搜索状态，有输入值，没有选择值，返回原值
            if(this.inputable)
                return rawValue;
            else
            	return this.orignValue;
        }else //返回选中的代码值
        		return this.value+"`"+this.getRawValue();
    },
    /*当值改变时执行搜索*/
    onFieldMutation:function(e){
         var me = this;
         /*只有在输入的时候才执行搜索 按键e.type为keyup 谷歌下中文输入为e.type为input
           判断规则：是键盘输入(keyup事件判断)，并且不是特殊控制键(enter\shift\ctrl\tab)时，执行搜索
         */
         if(e.type!='keyup'&&e.type!='input'){
             return;
         }
         if(e.isSpecialKey() && e.getKey()!=e.BACKSPACE)
             return;
        /*【58830】ie 初始化给value默认值也会调用此函数，并且event type 是input，此处处理下，不执行搜索  guodd 2020-04-29*/
        if(e.type=='input' && Ext.isIE){
            return;
        }
        /* guodd 2020-04-29
        搜索频率过快会导致搜索结果不准确，且浪费网络资源。通过定时器实现减少搜索频率，持续输入期间不执行搜索。
        持续输入期间按键是连续的，通过判断按键后500毫秒内没有在按键认为输入完毕
        */
        //清除上次定时器，重新定时。如果两次打字间隔不超过500毫秒时，上次的定时会被清除，搜索也不会执行，此时重新创建定时器
        clearTimeout(me.timer);
        //创建定时器，延时500毫秒执行搜索
        me.timer = setTimeout(function(){
         //搜索时重置数据
         //18/9/14 xus复制文本框值的时候也会清空value，不对
//         this.value = '';
         if(me.config.editable === false)//处理ie11非兼容下直接创建组件时调用交易类  bug 58738 wangb 2020-03-24
        	 return;
            me.treeStore.proxy.extraParams.searchtext = encodeURI(me.getRawValue());
            me.treeStore.load();

         //更新picker的焦点属性
            me.updatePickerFocus();

         if(!me.isExpanded && me.treeStore.proxy.extraParams.searchtext){//xiegh 20170517
             me.expand(true);
         }
        },500);
    },
    updatePickerFocus:function(){
    		if(this.getRawValue()&&this.getRawValue().length>0){
	         if(!this.picker)
	            this.pickerfocusable = false;
	         else
	         	this.picker.view.focusable = false;
	     }else{
	     	if(!this.picker)
	            this.pickerfocusable = true;
	         else
	         	this.picker.view.focusable = true;
	     }
    },
    onExpand:function(keepsearch){
    	if(!keepsearch){
    		this.treeStore.proxy.extraParams.searchtext="";
    		this.treeStore.load();
    	}
    	//处理ie下，echarts统计图线在picker里显示问题 wangbo 2020-01-20
    	var pickerDom = document.getElementById(this.pickerid);
    	if(pickerDom){
    		pickerDom.style.zIndex="9999999999";
    	}
    },
    onCollapse:function(){
         this.picker.view.focusable = true;
    },
    doAlign: function(){
    		var clientWidth = document.body.clientWidth;
            this.picker.setPosition(0,0);
        var me = this,
            picker = me.picker,
            aboveSfx = '-above',
            isAbove,
            newAlign=me.pickerAlign;
            //针对IE8做一下处理
            if(Ext.isIE8m && me.triggerWrap.getX()+250>clientWidth)
        			newAlign = "tr-br";
        me.picker.alignTo(me.triggerWrap, newAlign, me.pickerOffset);
        isAbove = picker.el.getY() < me.inputEl.getY();
        me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
        picker[isAbove ? 'addCls' : 'removeCls'](picker.baseCls + aboveSfx);
        
    },
    isExt6 : function(){
		var version = Ext.getVersion() + "";
		var flag = false;
		if(version)
			flag = version.substring(0,1) == '6'? true:false;
		return flag
	}
    

});