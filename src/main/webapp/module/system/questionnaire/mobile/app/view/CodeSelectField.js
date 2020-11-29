Ext.define('Questionnaire.view.CodeSelectField',{
	extend:'Ext.field.Text',
	xtype:'codeselectfield',
	config:{
		codesetid:undefined,
		/**=1单选, =2多选**/
		checkType:1,
		displayField:'text',
		valueField:'value',
		labelAlign:'left',
		store:null,
		clearIcon:true,
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
		var me = this,
			component = me.getComponent();
		
		me.callParent();
		component.on({
			scope:me,
			masktap:'onMaskTap',
			clearicontap:'onClearIconTap'
		});
	},
	onMaskTap:function(){
		var me = this,
			checkType = me.getCheckType(),
			value = me.getValue(),
			codesetid = me.getCodesetid(),
			component = me.getComponent(),
			xtype;
		component.input.dom.blur();
		if(!me.getCodesetid()){
			Ext.Msg.alert('提示信息','codesetid为空，控件初始化失败！');
			return;
		}
		codesetid = codesetid.toLowerCase();
		if(checkType==1)
			xtype = 'radiofield';
		else
			xtype = 'checkboxfield';
		if(!me.treePanel){
			var list = new Array(),
				vo = new HashMap();
			vo.put("codesetid", codesetid);
			vo.put("codeitemid", "");
			vo.put("transType", "codeset");
			Rpc({functionId:'QN70000001',success:function(res){
				var resultObj = Ext.decode(res.responseText),
					succeed = resultObj.succeed;
				if(succeed){
					var data = resultObj.data;
					for ( var i = 0; i < data.length; i++) {
						list.push({
							xtype:'container',
							itemId:me.getCodesetid()+'_'+data[i].itemid,
							layout:'hbox',
							items:[{
								xtype:'image',
								codeitemid:data[i].itemid,
								closed:true,
								width:40,
								cls:'codeselect-image',
								src:'/module/system/questionnaire/images/closed.png',
								listeners:{
									tap:function(){
										me.imageTap(this);
									}
								}
							},{
								xtype:xtype,
								labelCls:'codeselect-label',
								flex:1,
								labelWrap:true,
								name:'codeselectfield_name',
								checked:value.indexOf('`'+data[i].itemid+'`')!=-1 ? true:false,
								value:data[i].itemid,
								label:data[i].itemdesc,
								labelWidth:'70%'
							}]
						});
					}
					me.treePanel = Ext.create('Ext.form.Panel',{
						width:'100%',
						style:'background-color:#fff;',
						height:'100%',
						layout:'vbox',
						zIndex:100,
						items:list
					});
					if(codesetid=='un'||codesetid=='um'||codesetid=='@k'){
						for ( var i = 0; i < list.length; i++) {
							me.treePanel.getComponent(list[i].itemId).getComponent(0).fireEvent('tap',null);
						}
					}
					me.treePanel.add({
						xtype:'toolbar',
						docked:'top',
						items:[{
							xtype:'button',
							text:'取消',
							handler:function(){
								me.treePanel.hide(me.getHideAnimation());
							}
						},{
							xtype:'spacer'
						},{
							xtype:'button',
							text:'确定',
							handler:function(){
								me.downButtonTap();
							}
						}]
					});
					
					if(!me.treePanel.getParent())
						Ext.Viewport.add(me.treePanel);
					me.treePanel.show(me.getShowAnimation());
				} else {
					Ext.Msg.alert('提示信息', resultObj.message);
				}
			}}, vo);
		} else {
			me.treePanel.show(me.getShowAnimation());
		}
	},
	onClearIconTap:function(me){
		this.setValue(undefined);
		this.getValue();
		if(this.getClearIcon()){
			this.element.removeCls(Ext.baseCSSPrefix + 'field-clearable');
			if(this.treePanel){
				var xtype, field;
				if(this.getCheckType()==1)
					xtype = 'radiofield';
				else
					xtype = 'checkboxfield';
				field = this.treePanel.query(xtype);
				for ( var i = 0; i < field.length; i++) {
					if(field[i].isChecked())
						field[i].uncheck();
				}
			}
		}
	},
	imageTap:function(image){
		var me = this,
			container = image.getParent(),
			checkType = me.getCheckType(),
			value = me.getValue(),
			xtype;
		if(checkType==1)
			xtype = 'radiofield';
		else
			xtype = 'checkboxfield';
		if(image.config.closed){
			if(container.childlist){
				me.oprateChild(image, container, false);
			} else {
				image.setSrc('/module/system/questionnaire/images/expend.png');
				image.config.closed = false;
				var list = new Array(),
					vo = new HashMap();
				vo.put("codesetid", me.getCodesetid());
				vo.put("codeitemid", image.config.codeitemid);
				vo.put("transType", "codeset");
				Rpc({functionId:'QN70000001',success:function(res){
					var resultObj = Ext.decode(res.responseText),
						succeed = resultObj.succeed;
					if(succeed){
						var data = resultObj.data,
							index = me.treePanel.indexOf(container),
							width = container.getComponent(0).getWidth();
						
						if(data&&data.length>0){
							for ( var i = 0; i < data.length; i++) {
								list.push({
									xtype:'container',
									itemId:me.getCodesetid()+'_'+data[i].itemid,
									layout:'hbox',
									items:[{
										xtype:'image',
										codeitemid:data[i].itemid,
										closed:true,
										width:25+width,
										cls:'codeselect-image',
										src:'/module/system/questionnaire/images/closed.png',
										listeners:{
											tap:function(){
												me.imageTap(this);
											}
										}
									},{
										xtype:xtype,
										labelCls:'codeselect-label',
										flex:1,
										labelWrap:true,
										name:'codeselectfield_name',
										checked:value.indexOf('`'+data[i].itemid+'`')!=-1 ? true:false,
										value:data[i].itemid,
										label:data[i].itemdesc,
										labelWidth:'70%'
									}]
								});
							}
							container.childlist = list;
							me.treePanel.insert(index+1, list);
						} else {
							container.getComponent(0).setSrc('/module/system/questionnaire/images/block.png');
							container.getComponent(0).clearListeners();
						}
					}
				}}, vo);
			}
		} else {
			me.oprateChild(image, container, true);
		}
	},
	/**
	 * @param image
	 * @param container
	 * @param hidden =true 
	 * @param flag 
	 * 	 
	 */
	oprateChild:function(image, container, hidden, flag){
		var list = container.childlist;
		if(list){
			for ( var i = 0; i < list.length; i++) {
				this.oprateChild(null, this.treePanel.getComponent(list[i].itemId), hidden, true);
			}
		}
		if(flag){
			container.setHidden(hidden);
		} else {
			if(hidden){
				image.setSrc('/module/system/questionnaire/images/closed.png');
				image.config.closed = true;
			} else {
				image.setSrc('/module/system/questionnaire/images/expend.png');
				image.config.closed = false;
			}
		}
	},
	downButtonTap:function(){
		var me = this,
			data = me.treePanel.getValues(true, false),
			values = data.codeselectfield_name,
			store = me.getStore(),
			displayField = me.getDisplayField(),
			valueField = me.getValueField(),
			obj = {},
			record, xtype, text, value, index;
		
		if(this.getCheckType()==1)
			xtype = 'radiofield';
		else
			xtype = 'checkboxfield';
		var field = this.treePanel.query(xtype);
		if(Ext.isArray(values)){
			for ( var i = 0; i < values.length; i++) {
				for ( var j = 0; j < field.length; j++) {
					if(values[i]==field[j].config.value){
						if(value){
							value += '`'+values[i];
							text += ','+field[j].config.label;
						} else {
							value = '`'+values[i];
							text = field[j].config.label;
						}
						break;
					}
				}
			}
		} else {
			for ( var i = 0; i < field.length; i++) {
				if(field[i].config.value==values){
					value = values;
					text = field[i].config.label;
					break;
				}
			}
		}
		value = value+'`';
		obj[displayField] = text;
		obj[valueField] = value;
		if(!store){
			store = Ext.create('Ext.data.Store',{
				fields:[displayField,valueField],
				data:[]
			});
			me.setStore(store);
		}
		store.setData([obj]);
		index = store.find(valueField, value, 0, null, null, true);
		if(index == -1){
			index = store.find(displayField, value, 0, null, null, true);
		}
		record = store.getAt(index);
		me.setValue(record);
		me.treePanel.hide(me.getHideAnimation());
	},
	updateValue:function(newValue, oldValue){
		this.record = newValue;
		this.callParent([(newValue && newValue.isModel) ? newValue.get(this.getDisplayField()) : '']);
	},
	getValue:function(){
		var record = this.record;
		return (record && record.isModel) ? record.get(this.getValueField()) : '';
	},
	setValue:function(value){
		var me = this,
			record = value,
			displayField = me.getDisplayField(),
			valueField = me.getValueField();
		
		if(value!=undefined&&value!=''&&!value.isModel){
			var store = me.getStore(),
				index;
			if(!store){
				store = Ext.create('Ext.data.Store',{
					fields:[displayField,valueField],
					data:[]
				});
				me.setStore(store);
			}
			var vo = new HashMap();
			vo.put("codesetid", me.getCodesetid());
			vo.put("param", value);
			vo.put("transType", "codevalue");
			Rpc({functionId:'QN70000001',success:function(res){
				var resultObj = Ext.decode(res.responseText),
					succeed = resultObj.succeed;
				if(succeed){
					if(resultObj.data.value){
						store.setData([resultObj.data]);
						index = store.find(valueField, resultObj.data.value, null, null, null, true);
						if(index == -1){
							index = store.find(displayField, resultObj.data.value, null, null, null, true);
						}
						record = store.getAt(index);
						me.updateValue(record, null);
					}
				} else {
					Ext.Msg.alert('提示信息', resultObj.message);
				}
			}}, vo);
		} else {
			me.callParent(arguments);
		}
	}
});