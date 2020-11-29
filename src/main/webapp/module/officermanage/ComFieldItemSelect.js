/**
 * 干部管理设置选择指标界面 与干部管理编辑界面修改家庭成员信息代码选择功能
 */
Ext.define('OfficerMange.ComFieldItemSelect',{
	extend: 'Ext.form.field.Picker',
	requires:['EHR.fielditemselector.FieldItemSelector','OfficerMange.OfficerCodeSelect'],
	xtype:'comFieldItemSelect',
	multiSelect : true,
	codesetId:undefined,//家庭成员子集称谓与政治面貌代码项
	customTree:undefined,//兼容家庭成员展现grid 选择称谓与政治面貌代码项
	constructor:function(config){
		this.callParent([config])
		if(!this.customTree||this.customTree==''){
			this.customTree=false
		}
	},
	createPicker : function(){
		var me = this;
		var select;
		if(me.customTree){
		  select=Ext.create('OfficerMange.OfficerCodeSelect',{
			    renderId:me.codesetId,
			    height:'100%',
			    customTree:me.customTree
			})
			select.on('itemclick',function(view,record,item,index){
	     		 if (record.get("isCategory")!=null && record.get("isCategory")=="1"){
			         return
			      }
	     		 var text=record.get("text");
				me.setValue(text);
				//me.valueObj=record.data;
				select.setHidden(true);
	     	})
		}else{
		  select=Ext.widget('fielditemselecor',{
				source:'A',
				filterItems:'a0101',
				height:230,
				floating: true,
				multiple:false,
				okBtnText:common.label.okBtn,
				cancelBtnText:common.label.cancelBtn,
				searchEmptyText:common.label.searchText,
				listeners:{
					selectend:function(selectorCmp,fields){
						me.setValue(fields[0].data.itemdesc);
						me.valueObj=fields[0].data;
						
						select.setHidden(true);
					},
					cancel:function(selectorCmp){
						select.setHidden(true);
//						select.destroy();
					}
				}
			});
		  var treepanel=select.items.items[0];
			treepanel.on('itemdblclick',function(view,record,item,index,e,eopt){
				if(!record||!record.data.leaf){
					return;
				}
				me.setValue(record.data.itemdesc);
				me.valueObj=record.data;
				select.setHidden(true);
			});
		}
		return select;
	},
	
	onItemClick: function(picker, record){
        var me = this,
            selection = me.picker.getSelectionModel().getSelection(),
            valueField = me.valueField;
        if (!me.multiSelect && selection.length) {
            if (record.get(valueField) === selection[0].get(valueField)) {
                // Make sure we also update the display value if it's only partial
                me.displayTplData = [record.data];
                me.setRawValue(me.getDisplayValue());
                me.collapse();
            }
        }
    }
});
