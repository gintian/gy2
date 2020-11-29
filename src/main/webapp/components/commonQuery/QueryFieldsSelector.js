/**
	必需的参数：
	subModuleId
	defaultQueryFields
*/
Ext.define("EHR.commonQuery.QueryFieldsSelector",{
    extend:'EHR.fielditemmultiselector.Selector',
    xtype:'queryfieldsselector',
	
	initComponent:function(){
		this.funTools = this.commonQueryTools();
		this.callParent(arguments);
	},
	//创建查询指标控件bbar区域（底部tools）
	commonQueryTools:function(){
		var me = this;
		//自定义按钮数组
		var functools = new Array();
		//是否是公共模板选项
		var isSaved = {
            xtype: 'fieldcontainer',
            defaultType: 'checkboxfield',
            hidden:true,
            items: [
                {
                	boxLabel  : '保存为公共默认模板',
                    name      : 'isSaved',
                    inputValue: 'true',
                    id        : 'isSaved'
                }
            ]
        };
        //恢复默认模板按钮
        var resetButton = {
			xtype:'button',
			text:'恢复默认查询模板',
			handler : me.resetBtnFunc.bind(me)
        };
        //确认按钮
        var confirmButton = {
        	xtype:'button',
			text:'确 定',
			handler : me.confirmBtnFunc.bind(me)
        };
        //取消按钮
        var cancelButton = {
        	xtype:'button',
			text:'取 消',
			handler : me.cancelBtnFunc.bind(me)
        };
        
        if(this.fieldPubSetable)
        	isSaved.hidden = false;
        else
        	isSaved.hidden = true;
        functools.push(isSaved);
        functools.push("->");
        functools.push(resetButton);
        functools.push("-");
        functools.push(confirmButton);
        functools.push(cancelButton);
        return functools;
	},
	//恢复默认模板按钮事件
	resetBtnFunc:function(){
		var win = this.window;
		var param = new HashMap();
        param.put("subModuleId",this.subModuleId);
        param.put("default",this.defaultQueryFields);
        param.put("isReset","1");
        Rpc({functionId:'ZJ100000141',success:this.resetFunc,scope:this},param);
	},
	//确认按钮点击事件
	confirmBtnFunc:function(){
		var win = this.window;
		var isSaved = Ext.getCmp('isSaved').checked;
		var jsonData = Ext.pluck(Ext.getCmp('selectGrid').getStore().data.items, 'data'); 
		var redata = this.conFirm(jsonData,isSaved);
		win.close();
	},
	//取消按钮点击事件
	cancelBtnFunc:function(){
		var win = this.window;
		win.close();
	},
	//确认保存事件
	conFirm:function(data,is_share){
		var param = new HashMap();
		param.put("subModuleId",this.subModuleId);
		param.put("planItems",data);
		if(is_share){
			param.put("is_share",1);
		}else{
			param.put("is_share",0);
		}
		Rpc({functionId:'ZJ100000142',success:this.afterFunction,scope:this},param);
	},
	//恢复默认模板
	resetFunc:function(res){
		var result =  Ext.decode(res.responseText);
		//回传的默认模板数组
		var planItems =  result.planItems;
		//已选指标store
		var selectedStore = this.selectedPanel.getStore();
		selectedStore.each(function(record){
			var element = document.getElementById('add_'+record.data.itemid);
			//判断可选指标中是否存在与可选指标相对应的指标
			if(element!=null){
				var flag = element.innerHTML;
				if(flag!='添加'){
					document.getElementById('add_'+record.data.itemid).innerHTML="添加";
				}
			}
		});
		//清空已选指标的store
		selectedStore.removeAll();
		//将查询到的默认模板store放入可选指标中
		for(var i =0;i<planItems.length;i++){
			var flag = null;
			if(document.getElementById('add_'+planItems[i].itemid)!=null){
				flag = document.getElementById('add_'+planItems[i].itemid).innerHTML;
			}
			if(flag!='已添加'){
				selectedStore.add({
					itemid:planItems[i].itemid,
					itemdesc:planItems[i].itemdesc,
					itemtype:planItems[i].itemtype,
					fieldsetid:planItems[i].fieldsetid,
					codesetid:planItems[i].codesetid
				});
				if(document.getElementById('add_'+planItems[i].itemid)!=null){
					document.getElementById('add_'+planItems[i].itemid).innerHTML="已添加";
				}
			}
		}
	},
	//指标选择控件回调函数
	afterFunction:function(res){
		var result =  Ext.decode(res.responseText);
		if(this.afterfunc)
		     this.afterfunc(result.planItems);
	}
});