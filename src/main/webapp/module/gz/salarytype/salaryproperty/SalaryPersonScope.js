/**
 * 薪资类别-薪资属性-适用范围
 * lis 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.SalaryPersonScope',{
        constructor:function(config){
			salaryPersonScope_me = this;
			salaryPersonScope_me.simpleCexpr = "";
			salaryPersonScope_me.complexCexpr = "";
			salaryPersonScope_me.salaryid = config.salaryid;
			var result = config.result;
			salaryPersonScope_me.dbList = result.dbList;//人员库
			salaryPersonScope_me.personScope = result.personScope;//人员范围0:简单条件1：复杂条件
			salaryPersonScope_me.priv_mode = result.priv_mode;//限制用户管理范围
			salaryPersonScope_me.priv_mode_func = result.priv_mode_func;//限制用户管理范围 有权限才能修改 0:没权限 1：有权限 zhaoxg add 2016-12-19
			salaryPersonScope_me.condStr = result.condStr;//复杂条件，简单条件表达式
			salaryPersonScope_me.cexpr = result.cexpr;//简单公式时：1*2
			if(result.personScope == 0)
				salaryPersonScope_me.simpleCexpr = result.condStr;//简单查询
			else
				salaryPersonScope_me.complexCexpr = result.condStr;//复杂查询
			salaryPersonScope_me.isShare = result.isShare;//共享方式
			salaryPersonScope_me.manager = result.manager;//管理员
			salaryPersonScope_me.managerid=result.managerid;
			//salaryPersonScope_me.selectedlist = result.selectedlist;
			salaryPersonScope_me.createSalary();
			personScopeCh=0;//区分点击条件
        },
		 createSalary:function()  
		 {
       	    var checkboxgroup = Ext.widget({
       			xtype     : 'checkboxgroup',
				columns   : salaryPersonScope_me.dbList.length>4?2:1,
				id:'checkboxdbValue',
				width     : 400,
				vertical  : true
           	});
           	
    		//人员库的panel
        	var grid = Ext.widget({
        		xtype:'panel',
		    	border:false,
		    	height: 100,
		    	scrollable:true,
		    	items:[checkboxgroup]
        	});
        	Ext.each(salaryPersonScope_me.dbList,function(obj,index){
        		var checkbox = Ext.widget({
        			xtype     : 'checkbox',
					boxLabel  : obj.dbname,
					name:'dbValue',
					checked      : obj.isSelected==1?true:false,
                    inputValue: obj.pre,
                    id:obj.pre
            	});
        		checkboxgroup.add(checkbox);
        	})
        	
        	//人员库
        	var dbaseFieldset = Ext.widget({
				xtype:'fieldset',
				height: 135,
				title:gz.label.daName,//人员库
				padding:'5 0 0 10',
				items:grid
			});
        	
        	//人员范围
        	var scopeFieldset = Ext.widget({
				xtype:'fieldset',
				height: 90,  
				title:gz.label.personScope,//'人员范围'
				padding:'10 0 0 10',
				items:[{
					xtype:'panel',
					border:false,
					itemId:'conditionId',
					layout:'hbox',
					items:[{
						xtype:'radio',
						name:'personScope',
						id:'simpleRadio',
						boxLabel:gz.label.simpleCon,//简单条件
						inputValue:'0',
						width:80,
						checked:salaryPersonScope_me.personScope==0?true:false
					},{
						xtype:'button',
						margin:'0 50 0 0',
						id:'simpleButton',
						text:'...',
						handler:function(){
							scopeFieldset.queryById('simpleRadio').setValue(true);
							personScopeCh=0;
							salaryPersonScope_me.simpleCondition();//弹出简单条件
						}
					},{
						xtype     :'radio',
						name      :'personScope',
						id:'complexRadio',
						boxLabel  :gz.label.complexCon,//复杂条件
						inputValue:'1',
						width:80,
						checked:salaryPersonScope_me.personScope==1?true:false
					},{
						xtype:'button',
						margin:'0 100 0 0',
						id:'complexButton',
						text:'...',
						handler:function(){//弹出复杂条件
							scopeFieldset.queryById('complexRadio').setValue(true);
							var condStr = salaryPersonScope_me.condStr;
							personScopeCh=1;
							if(salaryPersonScope_me.personScope==0){//如果是简单条件
								condStr = "";
							}
							
							var map = new HashMap();
				        	map.put("express",decode(salaryPersonScope_me.complexCexpr));
				        	map.put("itemType","4");
				        	map.put("salaryid",salaryPersonScope_me.salaryid);
				         	Ext.require('EHR.complexcondition.ComplexCondition',function(){
				         		Ext.create("EHR.complexcondition.ComplexCondition",{dataMap:map,imodule:"3",opt:"1",title:gz.label.searchCon,callBackfn:"salaryPersonScope_me.saveCond"});
				         	});
						}
					},{
						xtype:'button',
						text:gz.label.clear,//清空
						handler:function(){
							salaryPersonScope_me.clearCondition();
						}
					}]
				},{
					xtype:'panel',
					border:false,
					layout:'hbox',
					margin:'10 0 0 0',
					items:[{
						xtype     : 'checkbox',
						boxLabel  : gz.label.limitUserManager,//限制用户管理范围
	                    name      : 'priv_mode',
	                    inputValue: '1',
	                    id:'mode_priv',
	                    disabled:salaryPersonScope_me.priv_mode_func==0?true:false,
	                    checked:salaryPersonScope_me.priv_mode==1?true:false
					}]
				}]
			});
        	
        	var cexpr = Ext.widget({
    			xtype     : 'textfield',
				name:'cexpr',
				hidden:true,
                value: salaryPersonScope_me.cexpr
        	});
        	var condStr = Ext.widget({
    			xtype     : 'textfield',
				name:'condStr',
				hidden:true,
				value: salaryPersonScope_me.condStr
        	});
        	scopeFieldset.queryById('conditionId').add(cexpr);
        	scopeFieldset.queryById('conditionId').add(condStr);
        	//共享方式
        	var shareFieldset = Ext.widget({
				xtype:'fieldset',
				title:gz.label.shareType,//'共享方式'
				padding:'10 0 0 10',
				items:[{
					xtype:'panel',
					border:false,
					layout:'hbox',
					height: 40,
					items:[{
						xtype:'radio',
						name:'share',
						boxLabel:gz.label.noShare,//'不共享'
						inputValue:'0',
						width:80,
						checked:salaryPersonScope_me.isShare==0?true:false
					},{
						xtype:'radio',
						name:'share',
						boxLabel:gz.label.share,//共享
						inputValue:'1',
						width:80,
						checked:salaryPersonScope_me.isShare==1?true:false,
						listeners:{
							'change':function(radio,newValue,oldValue){
								if(newValue){
									shareFieldset.queryById('isShare').show();
									shareFieldset.queryById('managerId').show();
									
								}else{
									shareFieldset.queryById('isShare').hide();
									shareFieldset.queryById('managerId').hide();
									Ext.getCmp('manageNameId').setText("");//判断共享与非共享是用是否设置管理员来区分的，所以设置了非共享则管理员要清掉 zhaoxg add 2016-5-16
									Ext.getCmp('manageNameId2').setValue("");
								}
							}
						}
					},{
						xtype:'panel',
						border:false,
						padding:'3 20 20 0',
						width:190,
						id:'isShare',
						hidden:salaryPersonScope_me.isShare==1?false:true,
						items:[{
							xtype:'label',
							margin:'15 0 0 0',
							text:gz.label.manager+"："//'管理员'
						},{
							xtype:'label',
							margin:'15 0 0 0',
							id:'manageNameId',
							text:salaryPersonScope_me.manager//'管理员'
						},{
							xtype:'textfield',
							id:'manageNameId2',
							margin:'15 0 0 0',
							name:'manager',
							hidden:true,
							value:salaryPersonScope_me.managerid//'管理员'
						}]
					},
					{
							xtype:'button',
							margin:'0 10 5 7',
							id:'managerId',
							text:gz.label.set,//'设置'
							hidden:salaryPersonScope_me.isShare==0?true:false,
							handler:function(){
								salaryPersonScope_me.setManager();
							}
					}]
				}]
			});
        	var scopePanel = Ext.widget({
        		xtype:'panel',
        		border:false,
        		items:[dbaseFieldset,scopeFieldset,shareFieldset]
        	});
        	//将当前panel渲染到tab页
        	salaryProperty_me.tabs.child('#scopeId').add(scopePanel);
        	//ie浏览器下checkbox，disabled失效问题
        	if(salaryPersonScope_me.priv_mode_func==0 && Ext.isIE) {
        		Ext.getDom('mode_priv-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
        		Ext.getDom('mode_priv-boxLabelEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
        	}
		},
		
		//保存公式
    	saveCond:function(c_expr){
			c_expr = decode(c_expr);
			if(personScopeCh=="0"){//简单条件
				salaryPersonScope_me.personScope=0;
				salaryPersonScope_me.simpleCexpr = getEncodeStr(c_expr.split("|")[1]);
				salaryPersonScope_me.cexpr = c_expr.split("|")[0];
			}else{//复杂条件
				salaryPersonScope_me.personScope=1;
				salaryPersonScope_me.complexCexpr = getEncodeStr(c_expr)
			}
    	},
    	
    	//清空条件
    	clearCondition:function(){
    		Ext.showConfirm(
    				gz.msg.operClearAllCondition,//"该操作将会清除已定义好的简单条件和复杂条件！"
    				function(but){
    					if(but == 'yes'){
    						var map = new HashMap();
    			    		map.put("salaryid",salaryPersonScope_me.salaryid);
    			    	    Rpc({functionId:'GZ00000234',async:false,success:function(form,action){
    			    	    	var success = Ext.decode(form.responseText).succeed;  	
    			    			 if (!success) { 
    			    				 Ext.showAlert(response.responseText.message);
    			    			 }else{
    			    				 salaryPersonScope_me.condStr = "";
    			    				 salaryPersonScope_me.cexpr = "";
                                     salaryPersonScope_me.simpleCexpr="";
                                     salaryPersonScope_me.complexCexpr="";
    			    				 //salaryPersonScope_me.selectedlist = "";
    			    			 }
    			    		}},map);
    					}
    				},
    				salarytype_me
    		);
    	},
    	
    	//共享时设置管理员
    	setManager:function(){
    		var f = document.getElementById("managerId");
			var p = new PersonPicker({
				multiple: false,
				isSelfUser:false,//是否选择自助用户
				header:true,
				selfUserIsExceptMe:false,
				isMiddle:true,//是否居中显示
				//不用控制了，自动附上资源权限的
				//extend_str:"salary/"+salaryPersonScope_me.salaryid,//薪资选人控件个性化标注，用于控件中薪资权限的控制
				callback: function (c) {
					var managerId = c.id;
					Ext.getCmp('manageNameId').setText(c.name);
					Ext.getCmp('manageNameId2').setValue(c.id);
				}
			}, f);
			p.open();	
    	},
    	
    	//弹出简单条件
    	simpleCondition:function(){
    		 var map = new HashMap();
			 map.put(1, "a,b,k");//要显示的子集
			 var map2 = new HashMap();
			 map2.put('salaryid', salaryPersonScope_me.salaryid);
			 map2.put('condStr', salaryPersonScope_me.simpleCexpr);
			 map2.put('cexpr', salaryPersonScope_me.cexpr);
			 map2.put('path', "2306514");
			 map2.put('buttonText', common.button.ok);
			 Ext.require('EHR.selectfield.SelectField',function(){
				 //设置flag表示添加的时候是否去掉左边的指标
				 Ext.create("EHR.selectfield.SelectField",{imodule:'0',flag:'1',type:'1',comBoxDataInfoMap:map,dataMap:map2,title:gz.label.selectField,saveCallbackfunc:salaryPersonScope_me.saveCond});
			 })
    	}
 });