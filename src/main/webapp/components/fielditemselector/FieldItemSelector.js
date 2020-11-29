/*
此组件继承自Ext.panel.Panel，可当做panel进行布局使用

参数说明：
1、source:指标源，支持配置多个，用`隔开。具体格式：
		  1、业务字典表 = Y:表名，例如Y:z01
		  2、信息集群 = A|B|K|H ，分别代表人员信息群、单位信息群、岗位信息群、基准岗位信息群
		  3、指定具体子集，如A01
      示例：source:'A`B01`Y:z01'  代表可选指标范围为人员所有信息集指标+B01子集指标+业务字典z01表指标
2、multiple: 是否可以多选，参数为布尔类型，true/false

3、filterItems: 不可选指标，多个用逗号隔开。例如'a0101,b0110,z0102'。
4、filterTypes: 不可选择指标类型，多个用逗号隔开    A,N,D       A 字符型 ，代码型   N 数值型  D 日期型  M 备注型
5、okBtnText:确定按钮文字
6、cancelBtnText:取消按钮问题
7、searchEmptyText：查询框提示信息
事件说明：
	selectend：点击确定按钮后触发此事件，参数：1、本组件对象 2、选中指标（Array）
	cancel:点击取消后出发此事件，参数：1、本组件对象

*/
Ext.define("EHR.fielditemselector.FieldItemSelector",{
	extend:'Ext.panel.Panel',
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	xtype:'fielditemselecor',
	layout:'fit',
	source:undefined,
	multiple:false,
	codeFilter:true,//当过滤指标类型有字符型时  是否过滤代码型指标 true 过滤 false 不过滤
	filterItems:'',
	filterTypes:undefined,//过滤指标类型  wangb 2019-02-20    
	okBtnText:'OK',
	cancelBtnText:'Cancel',
	searchEmptyText:'Searching...',
	
	initComponent:function(){
		if(!Ext.util.CSS.getRule('.fielditemsearchcls'))
    	  		Ext.util.CSS.createStyleSheet('.fielditemsearchcls{border:none !important;}');
		this.tbar = [{
			xtype:'container',
			layout:'hbox',
			style:'border:1px solid #c5c5c5;background:url(/images/hcm/themes/gray/search_fdj2.png) no-repeat center left;',
			items:{
				xtype:'textfield',width:200,emptyText:this.searchEmptyText,margin:'0 0 0 20',
				inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap fielditemsearchcls',
				enableKeyEvents:true,
				listeners:{
					keyup:this.doItemQuery,
					scope:this
				}
			}
		}],
		this.items = [{
			xtype:'treepanel',
			rootVisible:false,
			border:false,
			bodyStyle:'border:0px;',
			store:{
				fields:["itemdesc","fieldsetid","fieldsetdesc","itemtype"],
				proxy:{
					type:'transaction',
					functionId:'ZJ100000301',
					extraParams:{
						source:this.source,
						multiple:this.multiple,
						filterItems:this.filterItems,
						filterTypes:this.filterTypes,
						codeFilter:this.codeFilter
					}
				}
			},
			buttonAlign:'center',
			buttons:[{
				text:this.okBtnText,
				handler:this.doSelect,
				scope:this
			},{
				text:this.cancelBtnText,
				handler:this.doCancel,
				scope:this
			}]
		}];
		this.callParent(arguments);
	},
	doItemQuery:function(input){
		var store = this.query('treepanel')[0].getStore();
		store.getProxy().extraParams.querykey = input.value;
		store.load();
	},
	
	doSelect:function(){
		var tree = this.query('treepanel')[0];
		var selectItem = [];
		if(this.multiple){
			selectItem = tree.getChecked();
		}else{
			selectItem = tree.getSelection( );
			if(selectItem.length<1 || !selectItem[0].data.leaf)
				return;
		}
		this.fireEvent("selectend",this,selectItem);
	},
	doCancel:function(){
		this.fireEvent("cancel",this);
	}

});