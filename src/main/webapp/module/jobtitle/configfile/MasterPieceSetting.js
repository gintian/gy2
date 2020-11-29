//代表作导出规则配置
Ext.define("ConfigFileURL.MasterPieceSetting",{
	extend:'Ext.window.Window',
	width:800,
	height:600,
	modal:true,
	resizable:false,
	buttonAlign : 'center',
	buttons:[{
			text:'上一步',
			id:'last',
			handler:function(){
				masterpiece.stepview.previousStep();
		}},{
			text:'下一步',
			id:'next',
			handler:function(button,e){
				if(button.text=='保存'){
					//保存设置
					var map=new HashMap();
					map.put("flag","fourthStep");
					map.put("type","2");
					map.put("othermaterialsobj",othermaterial_me.othermaterialsobj);
					Rpc({functionId:'ZC00004011',async:false,success:function(res){
		   				var res=Ext.decode(res.responseText);
		   				if(res.status){
		   					return;
		   				}else{
		   					Ext.showAlert(res.errMsg);
		   				}
		   			},scope:masterpiece},map);
					masterpiece.close();
				}
				masterpiece.stepview.nextStep();
			}
		}],
	listeners:{
		destroy:function(scope,opt){
			var map=new HashMap();
			map.put("flag","otherStep");
			Rpc({functionId:'ZC00004011',async:false,success:function(res){
   				var res=Ext.decode(res.responseText);
   				if(res.status){
   					return;
   				}else{
   					Ext.showAlert(res.errMsg);
   				}
   			},scope:masterpiece},map);
		}
	},
	title:zc.label.masterpieceTitle,
	constructor:function(){
		masterpiece = this;
		this.callParent();
		this.init();
	},
	init:function(){
		masterpiece.stepview = Ext.widget("stepview",{
			margin:'0 0 0 40',
			id:'stepview_setting',
			listeners:{
				stepchange:function(stepview,step,index){
					if(masterpiece.getComponent(1))
						masterpiece.getComponent(1).destroy();
					masterpiece.remove(masterpiece.getComponent(1));
					if(index==0){//第一步
						masterpiece.renderfirst();
					}
					if(index==1){//第二步
						masterpiece.renderSec();
					}
					if(index==2){//第三步
						masterpiece.renderThird();
					}
					if(index==3){//第四步
						masterpiece.renderFourth();
					}
				}	
			},
			currentIndex:0,
			freeModel:true,
			stepData:[{name:'第一步',desc:'上传代表作模板'},
				{name:'第二步',desc:'申报人信息匹配'},
				{name:'第三步',desc:'代表作摘要信息匹配'},
				{name:'第四步',desc:'其他材料设置'}]	
		});
		this.add(masterpiece.stepview);
		
		Ext.require('ConfigFileURL.MasterPieceFile', function(){
			var firstStep=Ext.widget('masterPieceFile',{id:'firstStep',layout:'fit'});
			masterpiece.add(firstStep);
		});
		if(masterpiece.stepview.currentIndex=='0'){
			Ext.getCmp("last").setDisabled(true);
		}
	},
	renderfirst:function(){
		Ext.getCmp("last").setDisabled(true);
		Ext.getCmp("next").setText("下一步");
		Ext.require('ConfigFileURL.MasterPieceFile', function(){
			var firstStep=Ext.widget('masterPieceFile',{id:'firstStep',layout:'fit'});
			masterpiece.add(firstStep);
		});
	},
	renderSec:function(){
		Ext.getCmp("last").setDisabled(false);
		Ext.getCmp("next").setText("下一步");
		Ext.require('ConfigFileURL.MatchingInfoConfig', function(){
			var secStep = Ext.create('ConfigFileURL.MatchingInfoConfig');
			masterpiece.add(secStep);
		});
	},
	renderThird:function(){
		Ext.getCmp("last").setDisabled(false);
		Ext.getCmp("next").setText("下一步");
		Ext.require('ConfigFileURL.Representative', function(){
			var thirdStep=Ext.widget('representative',{id:'thirdStep',layout:'fit'});
			masterpiece.add(thirdStep);
		});
	},
	renderFourth:function(){
		Ext.getCmp("last").setDisabled(false);
		Ext.getCmp("next").setText("保存");
		Ext.require('ConfigFileURL.OtherMaterialSettings', function(){
			var fourthStep = Ext.create('ConfigFileURL.OtherMaterialSettings');
			masterpiece.add(fourthStep);
		});
	}
});