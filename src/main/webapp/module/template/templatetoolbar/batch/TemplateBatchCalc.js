/**
 * 批量计算类 TemplateBatchCalc.js 
 */
Ext.define('TemplateBatchUL.TemplateBatchCalc', {
	win : '', //临时变量赋值窗口。
	templPropety:'',
	editMidInput:undefined,//临时变量input框
	strMidItems:'',//临时变量指标列表 以逗号分隔。
	iMidLength:0,//临时变量的总数
	iMidIndex:0,//目前正在处理的临时变量 index
	midValue:'',//最后为临时变量赋的值，以逗号分隔传到后台
	constructor:function(config) {	
		templateBatchCalc_me=this;		
		this.templPropety=config.templPropety;
		this.allNum = config.allNum;
		this.calc();
	},

	/**
	 * 计算
	 */
	calc:function() {
		var map = new HashMap();
		initPublicParam(map,this.templPropety); 
		map.put("allNum",this.allNum+"");
		Ext.MessageBox.wait("正在计算中,请稍候...", "等待");//liuyz 增加进度条。
	    Rpc({functionId:'MB000020011',async:true,success:this.calcOK,scope:this},map);
	},
	calcOK:function(form,action){
		Ext.MessageBox.close();
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
			var message = result.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
				return;
        	}else{
        		Ext.showAlert(result.message);
        		return;
        	}
        }
		Ext.showAlert("计算完成！");
		templateTool_me.refreshCurrent();
	}
	
	/*
    init:function() {
        var map = new HashMap();
        map.put("transType","init");        
        map.put("tab_id",this.templPropety.tab_id);     
        map.put("ins_id",this.templPropety.ins_id);     
        map.put("task_id",this.templPropety.task_id);       
        Rpc({functionId:'MB000020011',async:false,success:this.initOK,scope:this},map);
    },
    initOK:function(form,action){
        var result = Ext.decode(form.responseText);     
        var message =result.midVarMessage;
        if(message.length>0){//有需要临时赋值的临时变量     
            this.createWindow();
            this.strMidItems=getDecodeStr(message);
            this.iMidIndex=0;
            this.dealMidItem(this.iMidIndex,message);
        }
        else {//批量计算
            this.calc("");
        }
        
    },
    */  
	/**
	 * 处理临时变量 弹出临时变量框
	 */
	 /*
	dealMidItem:function(midIndex) {
		var me =templateBatchCalc_me;
		var midItems=me.strMidItems.split(",");
		me.iMidLength=midItems.length;
		for(var i=0;i<midItems.length;i++)
		{
			if (i!=me.iMidIndex) {//当前正在处理的临时变量
				continue;
			}
			
			var temp=midItems[i].split(":");			
		    var midName = getDecodeStr(temp[0]);
		    var type = temp[1];
		    var length = temp[2];
		    var editLabel = "";
		    var defaultHint = "";
		    if (type=="A") {
		    	editLabel = "请输入字符型变量值(直接输入字符串)";
		        defaultHint = "缺省值为空";
		    } else if (type=="D") {
		    	editLabel = "请输入日期型变量值(格式:YYYY.MM.DD)";
		        defaultHint = "缺省值为系统日期";
		    } else if (type=="N") {
		    	editLabel = "请输入数值型变量值";
		        defaultHint = "缺省值为0";
		    }
		    var title= "变量"+midName+"的值("+defaultHint+")"
		    me.win.setTitle(title);			
			me.editMidInput.setFieldLabel(editLabel);
		    me.win.show();
		}
	},
*/
	/**
	 * 生成临时变量输入框
	 */
	 /*
	createWindow : function() {
		var editor = Ext.create('Ext.form.Text', {
			id : "midVarInput",
			fieldLabel : "请输入数值型变量值",
			height : '30px',
			width : 400,
			labelWidth : 150,
			labelAlign : 'left',
			allowBlank : false,
			maxLength : 100,
			blankText : ''
		});
		this.editMidInput=editor;

		this.win = Ext.widget("window", {
			title : '临时变量',
			height : 150,
			width : 420,
			modal : true,
			closeAction : 'destroy',
			layout : {
				type : 'vbox',
				align : 'stretch',
				pack : 'center'
			},
			items : [ {
				xtype : 'panel',
				border : false,
				layout : {
					type : 'hbox',
					pack : 'center'
				},
				defaults : {
					margins : '15,0,0,0'
				},
				items : [ editor ]
			} ],
			bbar : [ {
				xtype : 'tbfill'
			}, {
				text : common.button.ok,
				scope : this,
				handler : function() {//确定
					var me =templateBatchCalc_me;
					me.midValue=me.midValue+","+editor.value+":"+"";
					me.iMidIndex=me.iMidIndex+1;
					if (me.iMidIndex<me.iMidLength){//继续处理下一个
						me.win.hide();						
						me.dealMidItem(templateBatchCalc_me.iMidIndex);
					} else {//开始批量计算
						me.win.close();
						this.calc("");
					};
					
				}
			}, {
				text : common.button.cancel,
				handler : function() {//取消
					templateBatchCalc_me.win.close();
				}
			}, {
				xtype : 'tbfill'
			} ]
		});
	}
	*/
});
