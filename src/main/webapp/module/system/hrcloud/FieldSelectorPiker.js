Ext.define('FieldSelectorPiker',{
	extend:'Ext.form.field.Picker',
	xtype:'FieldSelectorPiker',
	config:{
		source:undefined
	},
	createPicker:function(){
        //不可选择类型
        var filterTypes = undefined;
        var filterCode = undefined;
        if (this.itemid != "gender"){
            filterTypes = ",A,N,M,D";
            //xus 19/11/25邱凤涛提：备注型指标可以对应字符型指标
            if("M" == this.type){
            	filterTypes = ",N,D";
            	filterCode="CODE";
            }else{
            	filterTypes = filterTypes.replace(","+this.type,"");
            	filterTypes = filterTypes.substring(1);
            	if (Ext.isEmpty(this.codesetid) || "0" == this.codesetid){
            		if("A" == this.type){
            			filterTypes = ",N,D";
            		}
            		filterCode="CODE";
            	}else{
            		filterCode="STRING";
            	}
            }
        }
		var me = this;
		return Ext.widget('fielditemselecor',{
			source:me.source,
			floating:true,
			height:400,width:600,
			style:'z-index:100000',
			minWidth:300,
			okBtnText:'确定',
			cancelBtnText:'取消',
            filterTypes:filterTypes,
            filterCode:filterCode,
			searchEmptyText:'输入指标名称或id查询...',
			renderTo:document.body,
			listeners:{
				selectend:function(selectorCmp,fields){
					if(fields.length>0){
						selectorCmp.ownerCmp.hritemid = fields[0].data.id;
						selectorCmp.ownerCmp.setItemValue(fields[0].data.id+'`'+fields[0].data.itemdesc);
					}
					me.collapse();
				},
				cancel:function(){
					me.collapse();
				}
			}
		});
		
	},
	changeSource:function(source){
		this.source = source;
		if(!this.picker)
			return;
		this.picker.source = source;
		var store = this.picker.query('treepanel')[0].getStore();
		store.getProxy().extraParams.source = source;
	},
	onExpand:function(){
		if(!this.picker)
			return;
		this.picker.show();
		//重置treepanel的滚动条位置
		var treepanel = this.picker.query('treepanel')[0];
		treepanel.getView().getEl().setScrollTop(0);
	},
	onCollapse:function(){
		if(!this.picker)
			return;
		this.picker.hide();
	},
	setItemValue:function(value){
		value = value?value:'';
        if(value.indexOf('`')==-1){
    		this.setValue(value);
    		this.hritemid = '';
            return;  
        }
        //分离代码id和描述显示
        this.setValue(value.split('`')[1]);
        this.hritemid = value.split('`')[0];
	}
});