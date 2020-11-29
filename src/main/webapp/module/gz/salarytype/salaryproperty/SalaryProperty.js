/**
 * 薪资类别-薪资属性弹出框
 * lis 2015-12-08
 */
//引入指标选择js
Ext.Loader.setPath("selectfield","../../../components/selectfield");
Ext.define('SalaryTypeUL.salaryproperty.SalaryProperty',{
        constructor:function(config){
			salaryProperty_me = this;
			salaryProperty_me.salaryid = config.salaryid;
			salaryProperty_me.commissionFlag = config.commissionFlag;//xiegh 20170412 add提成标志
			salaryProperty_me.cname = config.cname;
			salaryProperty_me.imodule=config.imodule;// 0:薪资  1:保险
			//弹出等待
			var mainPanel = salarytype_me.tableObj.getMainPanel();
			var myMask = new Ext.LoadMask({
			    target : mainPanel
			});
			
			myMask.show();
			
			//获取薪资属性数据
			var map = new HashMap();
			map.put("salaryid",salaryProperty_me.salaryid);
			//传入对应的模块 0:薪资  1:保险， 否则全部走薪资了
			map.put("gz_module",salarytype_me.imodule);
			map.put("nmodule","5");
			
			Rpc({functionId:'GZ00000231',success:function(response,action){
				var result = Ext.decode(response.responseText);
				myMask.hide();
				if (result.succeed) { 
					salaryProperty_me.createSalary(result.propertyData);   
				} else {  
					Ext.showAlert(result.message+"！");
				}
			}},map);
        },
		 createSalary:function(result)  
		 {
			salaryProperty_me.isShare = result.isShare;//共享方式,如果设置了应用机构切换共享方式的时候需要提示  共享方式的切换会将设置了应用机构的所有参数都清除掉，是否继续？
			salaryProperty_me.haveAppliOrg = result.haveAppliOrg;//是否设置了应用该机构
			var css_template_tab="#propertyTab-body {border-width: 0px 1px 1px 1px;}";
			Ext.util.CSS.createStyleSheet(css_template_tab,"tab_css");
        	salaryProperty_me.tabs = Ext.create('Ext.tab.Panel', {
        		width: 510,
        		height: 450, 
        	    activeTab: 0,
        	    id:'propertyTab',
        	    plain: true,
        	    items: [
        	        {
        	            title: gz.label.useredScope,//适用范围
        	            itemId: 'scopeId',
        	            bodyPadding: 10
        	        },
        	        {
        	        	title: gz.label.TaxParam,//适用范围
        	        	itemId: 'maxId',
        	        	hidden:salaryProperty_me.imodule=='0'?false:true,
        	        	bodyPadding: 10
        	        },
        	        {
        	        	title: gz.label.SpMode,//审批方式
        	        	itemId: 'spId',
        	        	bodyPadding: 5
        	        },
        	        {
        	        	title: gz.label.subDataType,//数据提交方式
        	        	itemId: 'dataSubmitId',
        	            bodyPadding: 10
        	        },
        	        {
        	        	title: gz.label.otherParam,//其他参数
        	        	itemId: 'otherId',
        	            bodyPadding: 10
        	        }
        	    ],
        	    renderTo : Ext.getBody()
        	});
        	//薪资属性-适用范围页面
        	Ext.require('SalaryTypeUL.salaryproperty.SalaryPersonScope', function(){
        		var scopePanel = Ext.create("SalaryTypeUL.salaryproperty.SalaryPersonScope",{salaryid:salaryProperty_me.salaryid,result:result});
    		});
        	 
        	//薪资属性-计税参数
    		
        	Ext.require('SalaryTypeUL.salaryproperty.SalaryTaxParam', function(){
        		var taxParam = Ext.create("SalaryTypeUL.salaryproperty.SalaryTaxParam",{salaryid:salaryProperty_me.salaryid,result:result});
    		});
        	
        	//薪资属性-审批方式
        	Ext.require('SalaryTypeUL.salaryproperty.SalarySpMode', function(){
        		var spMode = Ext.create("SalaryTypeUL.salaryproperty.SalarySpMode",{salaryid:salaryProperty_me.salaryid,result:result});
    		});
        	
        	//薪资属性-数据提交方式
        	Ext.require('SalaryTypeUL.salaryproperty.DataSubmitType', function(){
        		var spMode = Ext.create("SalaryTypeUL.salaryproperty.DataSubmitType",{salaryid:salaryProperty_me.salaryid,result:result});
    		});
        	
        	//薪资属性-其他参数
        	Ext.require('SalaryTypeUL.salaryproperty.SalaryOtherParam', function(){
        		var spMode = Ext.create("SalaryTypeUL.salaryproperty.SalaryOtherParam",{salaryid:salaryProperty_me.salaryid,result:result,imodule:salaryProperty_me.imodule,commissionFlag:salaryProperty_me.commissionFlag});
    		});
        	
        	var form = Ext.widget({
        		xtype:'form',
        		border:false,
        		items:salaryProperty_me.tabs,
        		minButtonWidth:50,
        		buttons:[
	                     {xtype:'tbfill'},
			          		{
			          			text:common.button.ok,//保存
			          			style:'margin-right:5px',
			          			handler:function(){
		                    	 	var form = this.up('form').getForm().getValues();
		                    	 	var isHavePersonScope=false;
		                    	 	Ext.each(Ext.getCmp('checkboxdbValue').items.items,function(obj,index){
		                    	 		if(obj.checked)
		                    	 			isHavePersonScope=true;
		                    	 	});
		                    	 	if(!isHavePersonScope){
		                    	 		Ext.showAlert('请选择人员库！');
		                    	 		return;
		                    	 	}
		                    	 	if(form.manager.length==0&&form.share=='1'){
										Ext.showAlert('共享类别需设定管理员！');
										return;
									}
		                    	 	if(form.disability.length > 0 && form.percent.length == 0) {
		                    	 		Ext.showAlert(gz.label.choosePercent);//选择残疾人指标后需填写减征比例！
										return;
		                    	 	}
		                    	 	//如果设置了应用机构，并且切换了共享和非共享，提示
		                    	 	if(salaryProperty_me.haveAppliOrg == true && salaryProperty_me.isShare != form.share) {
		                    	 		Ext.showConfirm("共享方式的变换会将设置了应用机构的所有参数都清除掉，是否继续？",function (v) {
		                                    if (v == 'yes') {
		                                    	//salaryProperty_me.
		                                    	salaryProperty_me.save(form);
		                                    	win.close();
		                                    } else {
		                                        return;
		                                    }
		                    	 		});
		                    	 	}else {
		                    	 		salaryProperty_me.save(form);
                                    	win.close();
		                    	 	}
	                     		}
			          		},
			          		{
			          			text:common.button.cancel,//取消
			          			handler:function(){
			          				win.close();
			          			}
			          		},{xtype:'tbfill'}
			    ]
        	});
        	
	 		//生成弹出得window
	   		var win=Ext.widget("window",{
	   		  title : salaryProperty_me.cname,
	   		  minButtonWidth:45,
	   		  resizable:false,
		      border:false,
			  modal:true,
			  closeAction:'destroy',				  
	          items: [form],
          	  listeners:{
       			'beforeclose':function(){
					var win = Ext.getCmp('person_picker_single_view');
					if(win) {//关闭窗口之前，先判断是否有选人控件，有则关闭
						win.close();
					}
       			}
       		  }
	    });                               
	   		win.show();  
		},
		
		save:function(form){
			if(form.personScope == 0)//因为简单查询和复杂查询用的是一个字段，原本简单条件有值，点击到了复杂查询，这时候会认为简单条件的值是复杂条件的值，导致错误
				salaryPersonScope_me.condStr = salaryPersonScope_me.simpleCexpr;//简单查询
			else
				salaryPersonScope_me.condStr = salaryPersonScope_me.complexCexpr;//复杂查询
			//简单条件
			form.condStr = salaryPersonScope_me.condStr;
			form.cexpr = salaryPersonScope_me.cexpr;
			//可能在某些环境下+传到后台出现为空格的情况，导致简单条件的逻辑运算符保存不了（+代表或，*代表且），这里转为特殊字符，后台再转回来
			if(form.cexpr.indexOf("+") != -1)
				form.cexpr = form.cexpr.replace(/\+/g,"convert");
			form.rightvalue = salarySpMode_me.rightvalue;
			form.addrightvalue = salarySpMode_me.addrightvalue;
			form.delrightvalue = salarySpMode_me.delrightvalue;
			if(salaryPersonScope_me.priv_mode_func!=null && salaryPersonScope_me.priv_mode_func=='0')//如果没有限制用户管理权限，则取当前的pri_mode就行
				form.priv_mode=salaryPersonScope_me.priv_mode;
			else if(!!!form.priv_mode)//限制用户管理范围
				form.priv_mode = "0";
			//审批方式
			if(!!!form.smsNotice)//短信通知
				form.smsNotice = "0";
			if(!!!form.mailNotice)//邮件通知
				form.mailNotice = "0";
			//数据提交
			var confirm_type = "";
			//A01`2;A58`0;A10`0;A99`2;
			dataSubmitType_me.gridpanel.getStore().each(function(record,index){
				confirm_type += record.get('setid')+"`"+record.get('type') +";"
			})
			form.confirm_type = confirm_type;
			if(!!dataSubmitType_me.updateObj)
				form.updateObj = dataSubmitType_me.updateObj;
			else form.updateObj = null;
			form.buf = dataSubmitType_me.buf;//提交数据-高级按钮
			if(!!!form.subNoShowUpdateFashion)//提交时不显示数据操作方式设置
				form.subNoShowUpdateFashion = "0";
			if(!!!form.subNoPriv)//数据提交入库不判断子集及指标权限
				form.subNoPriv = "0";
			if(!!!form.allowEditSubdata)//允许修改已归档数据
				form.allowEditSubdata = "0";
			
			//其他参数
			if(!!!form.amount_ctrl)//是否进行总额控制
				form.amount_ctrl = "0";
			if(!!!form.amount_ctrl_ff)//控制薪资发放
				form.amount_ctrl_ff = "0";
			if(!!!form.amount_ctrl_sp)//控制薪资审批
				form.amount_ctrl_sp = "0";
			if(!!!form.verify_ctrl)//是否进行审核公式控制
				form.verify_ctrl = "0";
			if(!!!form.verify_ctrl_ff)//控制薪资发放
				form.verify_ctrl_ff = "0";
			if(!!!form.verify_ctrl_sp)//控制薪资发放
				form.verify_ctrl_sp = "0";
			if(!!!form.a01z0Flag)//是否显示停发标识
				form.a01z0Flag = "0";
			if(!!!form.field_priv)//非写指标参与计算
				form.field_priv = "0";
			if(!!!form.read_field)//读权限指标允许重新导入
				form.read_field = "0";
			if(!!!form.royalty_valid)//提成薪资
				form.royalty_valid = "0";
			if(!!!form.priecerate_valid)//计件薪资
				form.priecerate_valid = "0";
			
			//提成薪资
			form.strExpression = salaryOtherParam_me.strExpression;
			form.royalty_setid = salaryOtherParam_me.royalty_setid;
			form.royalty_date = salaryOtherParam_me.royalty_date;
			form.royalty_period = salaryOtherParam_me.royalty_period;
			form.royalty_relation_fields = salaryOtherParam_me.royalty_relation_fields;
			 
			//计件薪资
			form.priecerateFields = salaryOtherParam_me.priecerateFields;
			form.priecerate_expression_str = salaryOtherParam_me.priecerate_expression_str;
			form.priecerate_period = salaryOtherParam_me.priecerate_period;
			form.priecerate_firstday = salaryOtherParam_me.priecerate_firstday;
			 
			var map = new HashMap();
			map.put("form",form);
			map.put("salaryid",salaryProperty_me.salaryid);
			map.put("gz_module",salaryProperty_me.imodule);
			Rpc({functionId:'GZ00000235',success:function(response,action){
				var result = Ext.decode(response.responseText);
				if (result.succeed) { 
					  
				} else {  
					Ext.showAlert(result.message+"！");
				}
			}},map);
		}
 });