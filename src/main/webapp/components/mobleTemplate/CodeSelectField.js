/**
 * 代码选择控件
 * 1、nodeLevel 代码层级  当代码层级是1级的时候  使用 CodeSelectPicker 组件    多层级时，使用 CodeSelect 代码树组件
 * 2、checkType 代码树组件 参数
 * 3、ctrltype 权限参数
 * 4、nmodule 业务模板权限
 */
Ext.define('EHR.mobleTemplate.CodeSelectField',{
	extend:'Ext.field.Text',
	requires:['EHR.mobleTemplate.CodeSelector','EHR.mobleTemplate.CodeSelectPicker'],
	xtype:'codeselectfield',
	config:{
		ui:'select',
		codesetid:undefined,
		/**=1：单选, =2：多选**/
		checkType:1,
		ctrltype:'',
		nmodule:'',
		currentid:'',
		nodeLevel:undefined,
		onlySelectCodeset:false,
		clearIcon:false,
		component:{
			useMask:true
		},
		showAnimation:{
			type:'slide',
			direction:'up',
			duration:250
		},
		hideAnimation:{
			type:'slideOut',
			direction:'down',
			duration:250
		}
	},
	initialize:function(){
		var me = this;
        component = me.getComponent();
		me.realValue = me.config.realValue;
		me.value = me.config.value;
	    me.callParent();
	
	    component.on({
	        scope: me,
	        masktap: 'onMaskTap'
	    });
	
	    component.doMaskTap = Ext.emptyFn;
	
	    if (Ext.browser.is.AndroidStock2) {
	        component.input.dom.disabled = true;
	    }
	
	    if (Ext.theme.is.Blackberry) {
	        this.label.on({
	            scope: me,
	            tap: "onFocus"
	        });
	    }
	},
	
	onFocus: function(e) {
		var buttonContainer = Ext.getCmp('buttonContainer')
		if(buttonContainer){
			buttonContainer.setHidden(true)
		}
        if (this.getDisabled()) {
            return false;
        }

        var component = this.getComponent();
        this.fireEvent('focus', this, e);

        if (Ext.os.is.Android4) {
            component.input.dom.focus();
        }
        component.input.dom.blur();

        this.isFocused = true;

        this.showPicker();
    },
    onMaskTap: function() {
        this.onFocus();

        return false;
    },
    showPicker:function(){
		var me = this;
		if(me.codePanel){
			if(!me.config.nodeLevel){
				var vo  = new HashMap();
				vo.put(me.getCodesetid(),me.realValue);
				me.codePanel.setValue(vo);
			}
			me.codePanel.show();
			return;
		}
		if(!me.config.nodeLevel){//一级
			me.codePanel = Ext.create('EHR.mobleTemplate.CodeSelectPicker',{
	    		codesetid:this.getCodesetid(),
	    		ctrltype:this.getCtrltype(),
	    		nmodule:this.getNmodule(),
	    		listeners:{
	    			change:function(t,value){
	    				for(var i = 0 ; i < t.config.data.length; i++){
	    					if(t.config.data[i].value == value[this.getCodesetid()]){
	    						me.setValue(t.config.data[i].text);
	    						me.value=t.config.data[i].text;
	    						me.getComponent().setValue(t.config.data[i].text);
	    						break;
	    					}
	    				}
	    				me.realValue = value[this.getCodesetid()];
	    				me.getComponent().realValue = value[this.getCodesetid()];
	    			},
	    			erased:function(){
	    				Ext.getCmp('buttonContainer').setHidden(false);
	    				me.setValue(me.value);
	    			}
	    		}
			});
		}else{//多级
			me.codePanel = Ext.create('Ext.Panel',{
			    fullscreen:true,
			    width:'100%',
			    height:'100%',
				layout:'vbox',
			   items:[{
		    		xtype:'titlebar',docked:'top',title:me.getLabel(),style:'border-bottom:1px solid #c5c5c5;height:26px;',
		    		
		    		items:[/*{
		    			html:'<font style="font-size:18px;">清空</font>',handler:function(btn){
		    				me.realValue='';
		    				me.value='';
		    				me.setValue('');
		    				var dataSelects = me.codePanel.query('#dataSelect');
        		    		for(var i = 0 ; i < dataSelects.length; i++){
        		    			if(!dataSelects[i].config.dataSelect)
        		    				continue;
        		    			dataSelects[i].setStyle({'background-color':''});
        		    			dataSelects[i].config.dataSelect = false;
        		    			dataSelects[i].setChecked(false);
        		    		}
		    				me.codePanel.hide();
		    				Ext.getCmp('buttonContainer').setHidden(false);
		    				Ext.callback(me.config.selectedFn,me.scope,[me.getCodesetid(),me.getCurrentid(),'empty']);
		    			}
		    		},*/{
		    			html:'<font style="font-size:18px;">取消</font>',align:'left',handler:function(){me.codePanel.hide();Ext.getCmp('buttonContainer').setHidden(false);}
		    		},{
		    			html:'<font style="font-size:18px;">确定</font>',align:'right',handler:function(){
							var value = me.codePanel.child('codeselector').getValues();
							var name = value.codeselector.split('`')[1];
							if(me.searchtext){
								var length = name.lastIndexOf('(');
								name = name.substring(0,length);
							}
		    			    if(value.codeselector){
		    			       me.realValue = value.codeselector.split('`')[0];
		    			       me.value = name;
		    			       me.setValue(name);
		    			    }
		    			    me.codePanel.hide();
		    			    Ext.getCmp('buttonContainer').setHidden(false);
		    			    //xus 18/3/13 解决 部门，岗位 为空时，上级也跟着级联为空。的问题 
		    			    if(value.codeselector)
		    			    	Ext.callback(me.config.selectedFn,me.scope,[me.getCodesetid(),me.getValue()]);
							}
		    		}]
		    	},{
		    		xtype:'textfield',
		    		placeHolder:'模糊检索',
		    		style:'border-bottom:1px #ccc solid;margin:0px auto;font-size:14px;',
		    		width:'96%',
		    		height:46,
		    		listeners:{
		    			keyup:function(t){
		    				setTimeout(function () {
			    				if(me.searchtext == t.getValue())
			    					return;
			    				me.searchtext = t.getValue();
			    				var codeselector = me.codePanel.child('codeselector');
			    				codeselector.setSearchtext(t.getValue());
			    				codeselector.removeAll(false,false);
		    					codeselector.loadData('');
　　　						    }, 1000);
		    			},
		    			clearicontap:function(){
		    				var codeselector = me.codePanel.child('codeselector');
		    				me.searchtext = '';
		    				codeselector.setSearchtext('');
		    				codeselector.removeAll(false,false);
		    				codeselector.loadData('');
		    			}
		    			
		    		}
		    	},{
		    		xtype:'codeselector',
		    		flex:1,
		    		codesetid:this.getCodesetid(),
		    		currentid:this.getCurrentid(),
		    		ctrltype:this.getCtrltype(),
		    		nmodule:this.getNmodule(),
		    		searchtext:'', //检索才使用此参数
		    		onlySelectCodeset:this.getOnlySelectCodeset()
		    	}/*,{
		    		xtype:'button',
		    		width:'80%',
		    		height:40,
		    		style:'background-color:#0099ff;margin:10px auto;font-size:20px;',
	    			//text:'<div width="100%" style="font-size:20px;text-align:center;>确定</div>',//,align:'right',
	    			text:'<font style="color:#ffffff;">确定</font>',
		    		handler:function(btn){
	    			    var value = btn.parent.child('codeselector').getValues();
	    			    if(value.codeselector){
	    			       me.realValue = value.codeselector.split('`')[0];
	    			       me.value = value.codeselector.split('`')[1];
	    			       me.setValue(value.codeselector.split('`')[1]);
	    			    }
	    			    me.codePanel.hide();
	    			    Ext.getCmp('buttonContainer').setHidden(false);
	    			    //xus 18/3/13 解决 部门，岗位 为空时，上级也跟着级联为空。的问题 
	    			    if(value.codeselector)
	    			    	Ext.callback(me.config.selectedFn,me.scope,[me.getCodesetid(),me.getValue()]);
	    			},
	    			docked:'bottom',
	    			scope:me
	    		}*/]
			});
		}
		me.codePanel.show();
	},

	getValue:function(){
		if(!this.value){
			this.value = this._value; //解决单位不选 部门选   点下一页再点上一页 单位值为空。
		}
		return this.realValue+'`'+this.value;
	},	
	//xus 18/3/1 重新加载机构树
	reloadTree:function(currentid){
		this.setCurrentid(currentid);
		if(this.codePanel){
			this.codePanel.down('codeselector').setCurrentid(currentid);
			this.codePanel.down('codeselector').removeAll(false,false);
			this.codePanel.down('codeselector').loadData('');
		}
	},
	doClearIconTap:function(){//代码型指标不让删除
	}
});