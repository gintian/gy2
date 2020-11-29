/**
 * 创建问卷
 * 
 **/
Ext.define('QuestionnairePlan.CreatePlan',{
	requires:["QuestionnaireTemplate.QuestionnaireBuilder"],
	container:undefined,
	qnid:undefined,
	cookie:'',
	dbname:'',
	funcpriv:undefined,
	viewport:undefined,
	constructor:function(config) {
		Ext.apply(this,config);
		this.createMainPanel();
	},
	//创建问卷主界面
	createMainPanel:function(){
		var me = this;
		me.mainPanel = Ext.create('Ext.form.Panel', {
			title: '创建问卷',
			tools:[{xtype:'button',text:'返回',handler:me.goBackFn,scope:me}],
	        width: '100%',
	        bodyPadding: 10,
	        border: 0,
	        bodyStyle: {
	        	background: 'white'
	        },
	        items: [{
	            border: 0,
	            items: [{
		            id: 'qnname',
            		xtype: "textfield",
            		labelWidth: 55,
            		fieldLabel: "问卷名称",
            		width:300,
            		allowBlank:false,
            		allowOnlyWhitespace:false
	        	}]
	        },{
	            border: 0,
	            padding: '0 0 0 60',
	            id: 'qntype',
        		items : [
        			new Ext.form.Radio({
						id: 'blank',
						name: 'type',
						checked: true,
						boxLabel : '从空白问卷开始',
						inputValue:'1'
					}),
					new Ext.form.Radio({
						id: 'template',
						name: 'type',
						boxLabel: '从问卷模板开始',
						inputValue:'2'
					})
				]
	        },{
	            border: 0,
	            padding: '0 0 0 55',
	            id: 'qncreate',
	            buttonAlign:'left',
	            buttons : [{
					xtype : 'button',
					text : "创建问卷",
					handler :me.createqn,
					formBind:true,
					scope:me
				}]
	        }]
    	});
		me.container.add(me.mainPanel);
	},
	//点击创建问卷，判断数据
	createqn:function(){
		var me = this;
		var qnname = Ext.getCmp('qnname').getValue();
		var flag = Ext.getCmp('blank').getValue();
		if(flag){
			me.container.remove(me.mainPanel,true);
			QN_global.funcpriv = me.funcpriv;
		    var QuestionnaireBuilder = Ext.create("QuestionnaireTemplate.QuestionnaireBuilder",{
		    		qnName:qnname,
		    		title:qnname,
		    		backButtonFn:function(){
    					QN_global.viewport.remove(QN_global.viewport.child('questionnairebuilder'),true);
    					QN_global.viewport.add(QN_global.myQuestionnaire.tableObj.getMainPanel());
    					QN_global.myQuestionnaire.tableObj.tablePanel.getStore().reload();
    				}
		    	});
		    me.container.add(QuestionnaireBuilder);
		}else{
			QN_global.qnname = qnname;
			QN_global.funcpriv = me.funcpriv;
			Rpc({functionId:'QN20000001',async:false,success:me.createOk,scope:me},new HashMap());
		}
	},
	//创建成功
	createOk:function(res){
		var me = this;
		var param = Ext.decode(res.responseText);
		me.container.remove(me.mainPanel);
		var tableObj = new BuildTableObj(param.configStr);
		var toolBar = Ext.getCmp("qnLib_toolbar");
		/*var saveButton = Ext.widget('button',{
			margin:'0 0 0 10',
			text:QN.template.questionnaireSave,
			handler:function(){
				me.saveLibrary();
			}
		});
		toolBar.add(saveButton);*/
		tableObj.bodyPanel.addTool({xtype:'button',text:'返回',handler:function(){
		     me.container.remove(tableObj.getMainPanel());
		     var plan = Ext.create("QuestionnairePlan.CreatePlan",{container:QN_global.viewport,funcpriv:param.funcpriv,
	    				   goBackFn:function(){
	    				   		QN_global.viewport.remove(this.mainPanel,true);
	    						QN_global.viewport.add(QN_global.myQuestionnaire.tableObj.getMainPanel());
	    				   }
	    				});
		     me.container.add(plan.mainPanel);
		},scope:me});
		me.container.add(tableObj.getMainPanel());
		//创建/从问卷模板开始，输完问卷名称后，点击创建问卷，此时进入的界面中默认显示全部的问卷模板，问卷分类下的“全部”应用下划线标出
		Ext.getDom('template_all').style.textDecoration='underline';
	}/*,
	saveLibrary:function(){
		var store = Ext.data.StoreManager.lookup("qnLib_dataStore");
		var updateList = store.getModifiedRecords();//修改过的数据
		if(updateList.length==0)
    	   return;
    	var updaterecord = [];
    	if(updateList.length>0){
    		for(var i=0;i<updateList.length;i++){
				var record = updateList[i].data;
				var qnname = record.qnname;
				if(qnname == ""){
					Ext.Msg.alert('提示信息',"问卷模板名称不能为空！");
					return;
				}
				updaterecord.push(record);
			}
    	}
    	var hashvo = new HashMap();
        hashvo.put("updaterecord",updaterecord);
        Rpc({functionId:'QN20000003',scope:this,success:function(res){
				var resultObj = Ext.decode(res.responseText);
				if(resultObj.result!=undefined && !resultObj.result){
					Ext.Msg.alert('提示信息',"保存失败！");
					return;
				}
				Ext.Msg.alert('提示信息',"保存成功！");
			    var store = Ext.data.StoreManager.lookup('qnLib_dataStore');
			    store.reload();
			}},hashvo);   
		
	}*/
});