Ext.define("OfficerMange.Setting",{
	requires:['OfficerMange.ComFieldItemSelect'],
	dbList:null,
	setList:null,
	fieldSetStore:null,
	bWStore:null,
	idArry:null,
	office_constant:null,
	constructor:function(config){
	     var me =this;
	     var map=new HashMap();
		 Rpc({functionId:'OM000000001',success:me.init,scope:me},map);
	},
	init:function(res){
		var me=this;
		 me.idArrys();
		res=Ext.decode(res.responseText);
		var property=res.property;
		me.dbList=property.dblist;
		me.setList=property.setList;
		me.fieldMap=property.fieldMap;
		me.office_constant=res.office_constant;
		me.createMainPanel();
	},idArrys:function(){
		var me=this;
		//cc_type==2 只有存储  1 自定义指标
		me.idArry=[{"id":'sex',"desc":"性别","cc_desc":"proc_officer_sex","type":"1"},
	    	 {"id":'birthdate',"desc":"出生年月","cc_desc":"proc_officer_birthdate","type":"1"},
	    	 {"id":'nation',"desc":"民族","cc_desc":"proc_officer_nation","type":"1"},
	    	 {"id":'nativeplace',"desc":"籍贯","cc_desc":"proc_officer_nativeplace","type":"1"},
	    	 {"id":'birthplace',"desc":"出生地","cc_desc":"proc_officer_birthplace","type":"1"},
	    	 {"id":'joinjobdate',"desc":"参加工作时间","cc_desc":"proc_officer_joinjobdate","type":"1"},
	    	 {"id":'health',"desc":"健康状况","cc_desc":"proc_officer_health","type":"1"},
	    	 {"id":'joinpartydate',"desc":"入党时间","cc_desc":"proc_officer_joinpartydate","type":"2"},
	    	 {"id":'majorpost',"desc":"专业技术职务","cc_desc":"proc_officer_majorpost","type":"2"},
	    	 {"id":'majorspecialty',"desc":"熟悉专业专长","cc_desc":"proc_officer_majorspecialty","type":"2"},
	    	 {"id":'education',"desc":"学历学位","cc_desc":"proc_officer_education","type":"2"},
	    	 {"id":'currentpost',"desc":"现任职务","cc_desc":"proc_officer_currentpost","type":"2"},
	    	 {"id":'preparepost',"desc":"拟任职务","cc_desc":"proc_officer_preparepost","type":"2"},
	    	 {"id":'terminalpost',"desc":"拟免职务","cc_desc":"proc_officer_terminalpost","type":"2"},
	    	 {"id":'resume',"desc":"简历","cc_desc":"proc_officer_resume","type":"2"},
	    	 {"id":'rewardsandpenalties',"desc":"奖惩情况","cc_desc":"proc_officer_rewards_penalties","type":"2"},
	    	 {"id":'assessment',"desc":"年度考核结果","cc_desc":"proc_officer_assessment","type":"2"},
	    	 {"id":'postreason',"desc":"任免理由","cc_desc":"proc_officer_postreason","type":"2"},
	    	 {"id":'familyandrelation',"desc":"家庭主要成员及重要社会关系","cc_desc":"proc_officer_familyandrelation","type":"2"}]; 
	},
	createMainPanel:function(){
		var me=this;
		var dbItem=[];
		for(var i=0;i<me.dbList.length;i++){
			dbItem.push({boxLabel:me.dbList[i].dbname,name:'dbPre',style:'margin-left:5px;float:left',inputValue:me.dbList[i].id});
		}
		me.fieldSetStore=Ext.create('Ext.data.Store',{
								fields:["fieldsetdesc","fieldsetid"],
								data:Ext.decode(me.setList)
						});
		var headPanel=Ext.create('Ext.container.Container',{
						border:1,
						anchor:'100%',
						style:'padding-left:10%;padding-top:20px',
						layout:{type:'vbox',align:'left'},
						items:[
							{xtype: 'label',labelWith:200,labelAlign:'right',
							 style:'margin-bottom:10px',
							 html:'<div style="border-left:5px solid #337EC4;font-size:12px;font-weight:bold;font-family:宋体">&nbsp;名册信息规则<div>'
							},
							{
							 xtype:'combo',id:'fieldSetIdCom',labelWith:200,labelAlign:'right',style:'margin-top:5px',
							 store:me.fieldSetStore,valueField:"fieldsetid",
							 displayField:"fieldsetdesc",fieldLabel:'职务子集:',
							 queryMode: 'local',
							 listeners:{change:me.creaFieldItemStore}
						    },
							{
						     xtype:'combo',id:'itemidCom',displayField:"itemDesc",valueField:"itemid",
						     labelWith:200,labelAlign:'right',fieldLabel:'任职单位:', queryMode: 'local'
						    },
							{
						    	xtype:'fieldcontainer',labelWith:260,labelAlign:'right',fieldLabel:'任职状况',layout:{type:'hbox',align:'middle'},
								items:[{xtype:'combo',id:'postStateId',
									    valueField:"itemid",
									    displayField:"itemDesc",
									    width:150, queryMode: 'local'
									    },
										{xtype:'displayfield',value:'(关联BW代码指标)'}]
							},
							{
								xtype:'combo',id:'jobNameId',displayField:"itemDesc",valueField:"itemid",
							    labelWith:200,labelAlign:'right',fieldLabel:'职务名称:', queryMode: 'local'
							},
							{
								xtype:'fieldcontainer',
								id:'dbPerCheckId',
								labelWith:200,
								labelAlign:'right',
								width:'90%',
								margin:'0 0 10 0',
								fieldLabel:'人员库', 
								defaultType: 'checkboxfield',
								items:dbItem
							},
							{
							 xtype:'fieldcontainer',
							 lableWidth:200,
							 height:25,
							 margin:'10 0 0 0',
							 labelAlign:'right',
							 fieldLabel:'干部筛选条件',
							 layout:'hbox',
							 items:[{
								 	 xtype:'button',
								     id:'filterExpr',
								     height:23,
								     text:'设置',valueObj:'',
								 	 listeners:{click:me.filterSet}
							        }]
							}
							
					   ]
					});
		
		if(me.office_constant!=null&&me.office_constant!=''){//设置不为空 显示对应内容
			Ext.getCmp("fieldSetIdCom").setValue(me.office_constant.setid);
			Ext.getCmp("itemidCom").setValue(me.office_constant.postOrg);
			Ext.getCmp("postStateId").setValue(me.office_constant.postState);
			Ext.getCmp("jobNameId").setValue(me.office_constant.jobname);
			var dbCheck=Ext.getCmp("dbPerCheckId").items.items;
			for(var i=0;i<dbCheck.length;i++){
				if(me.office_constant.nbase.indexOf(dbCheck[i].inputValue)>-1){
					dbCheck[i].setValue(true);
				}
			}
			Ext.getCmp("filterExpr").valueObj=me.office_constant.expr;
		}
		var mainPanel=Ext.create('Ext.panel.Panel',{
						 title:'<div style="float:left">配置</div><div style="float:right;padding-right:10px;"><a href="javascript:officeSetGoal.saveSetting()">保存</a></div>',
						 width:'100%',
						 height:'100%',
						 id:'mainPanelID',
						 scrollable:true,
						 bodyStyle:{
							 border:1,
							 marginTop:'1px'
						 },
						 layout:{type:'anchor',align:'center'},
						 items:[headPanel],
						 listeners:{
							   beforerender:function(){
										                if(me.office_constant!=null&&me.office_constant!="")
										                  me.renderData(me.office_constant);
								                      }
						 		  }
						 
					});
		
		mainPanel.add(me.createTabPanel());
		
		Ext.create('Ext.container.Viewport',{
			layout:'fit',
			id:'viewPortID',
			autoScroll:false,
			items:[mainPanel]
		});
	},//任免
	 createTabPanel:function(){
		 var me=this;
		 var bottomPanel=Ext.create('Ext.container.Container',{
				anchor:'80%',
				style:'padding-left:10%',
				layout:{
						type:'vbox',
						align:'left'
						},
				items:[
					{
						xtype: 'label',
						labelWith:200,
						labelAlign:'right',
						style:'margin-top:25px;margin-bottom:25px',
						html:'<div style="border-left:5px solid #337EC4;font-size:12px;font-weight:bold;font-family:宋体">&nbsp;任免表信息规则<div>'}
					]
		 });

		var tablePanel=Ext.create('Ext.container.Container',{
						width:(Ext.isIE?'80%':'100%'),
						style:'margin-left:15px',
						id:'tableContainer_id',
						layout:'column',
						items:[
							{
							 xtype:'container',columnWidth:1,id:'titleRow',height:40,layout:{type:'hbox',align:'center'},
							 items:
								 [
								     {html:'任免表指标',bodyStyle:'border-width:1px 0 1px 1px;text-align:center;background-color:#DDDDDD;padding-top:10px',width:"30%",height:40},
									 {html:'数据来源',bodyStyle:'border-width:1px 0 1px 1px;text-align:center;background-color:#DDDDDD;padding-top:10px',width:"30%",height:40},
									 {html:'值',bodyStyle:'border-width:1px 1px 1px 1px;text-align:center;background-color:#DDDDDD;padding-top:10px',width:"39%",height:40}
							     ]
							}
						]
		});
		
		var centerStore=Ext.create('Ext.data.Store',{
									fields:["desc","value"],
									data:[{"desc":"HR指标","value":"1"},
										  {"desc":"存储过程","value":"2"}]
		 				});		
		for(var i=0;i< me.idArry.length;i++){
			var obj=me.idArry[i];
			 tablePanel.add(me.creaTableItem(obj,centerStore));
		}
		
		//me.creaTableItem(tablePanel);
		var imgItem={xtype:'container',
				    width:50,
				    height:50,
				    border:false,
				    style:'margin-left:10px;background:url(/images/new_module/nocycleadd.png) no-repeat center left;',
				    listeners:{ 
				    	   click:{
				    		   element:'el',
				    		   fn:function(){
				    			   var d=Ext.getCmp("mainPanelID").body.dom;
				    			   var top=d.scrollTop+40;
				    			   tablePanel.add(me.creaTableItem({"customFlag":true,"type":"3"},centerStore));
				    			   d.scrollTop=top;
				    		   		}
				    	   	     }
				    		 }
		            };
		
		bottomPanel.add(tablePanel);
		bottomPanel.add(imgItem);
		return bottomPanel;
	 },//customFlag true  false 自定义添加标识
	 creaTableItem:function(obj,centerStore,customObj){
		    var me=this;
		    
		    var descChildDesc={
				    	    	xtype:'container',
				    	    	html:obj.desc
		    				  };
		    var descChidText={
				    	    	xtype:'textfield',
				    	    	itemId:'nameValue',
				    	    	style:'margin-top:2px',
				    	    	maxLength:25,
				    	    	value:(obj.type=='3'&&customObj)?customObj.name:"",
				    	    	columnId:customObj?customObj.columnid:"",		
				    	    	emptyText:'请输入名称...'
		    				 };
		    
        	var descItem={
        			       width:"30%",
        			       height:'100%',
        			       layout:'center',
        			       id:obj.id,
        			       bodyStyle:'border-width:0px 0px 1px 1px',
        			      // bodyStyle:'border:0 0 0 0;',
        			       items:[obj.type!='3'?descChildDesc:descChidText]
        	             };//描述信息
        	
        	var centerCom={
		        			 xtype:'combo',
			   				 store:centerStore,
			   				 queryMode:'local',
			   				 value:customObj?customObj.type:"1",
			   				 itemId:'centerComID',
			   				 width:'50%',
			   				 displayField:'desc',
			   				 valueField:'value',style:'margin-top:2px',
			   				 listeners:{
			   					change:function(){
			       						var rightItem=this.ownerCt.ownerCt.child('#parentLabel');
			       						var cc= rightItem.child('#childCCValue');
			       						var itemCom=rightItem.child('#childItemID');
			       						if(this.value=='1'){
			       						    cc.setHidden(true);
			       							itemCom.setHidden(false)
			       						}else{
			       							itemCom.setHidden(true);
			       							cc.setHidden(false);
			       						}
			   					   }
			   				  }
        				};
        	var centerDesc={
			        		 xtype:'container',
			   				 html:"存储过程",
			   				 value:"2"
							};
        	var centerItem={
	        					height:'100%',
	        					width:"30%",
	        					layout:'center',
	        					bodyStyle:'border-width:0 0 1px 1px',
	        					items:[obj.type!='2'?centerCom:centerDesc]
        					};
        	
        	var rightDesc={
			  				  xtype:'container',
			  				  style:'margin-left:20px',
			  				  itemId:'childCCValue',
			  				  hidden:obj.type=='1'?true:false,
			  				  html:obj.cc_desc,
			  				  value:obj.cc_desc
        					};
        	
        	var rightText={
		        			xtype:'textfield',
			    	    	itemId:'childCCValue',
			    	    	width:'70%',
			    	    	style:'margin-top:2px;margin-left:20px', 
			    	    	hidden:customObj&&customObj.type=='2'?false:true,
			    	    	value:customObj&&customObj.type=='2'?customObj.value:"",
			    	    	allowBlank:false,
			    	    	emptyText:'请输入存储过程名称...'
        				  };
        	var rightCom={
			  			      xtype:'comFieldItemSelect',
						      itemId:'childItemID',
						      width:'70%',
						      hidden:customObj&&customObj.type=='2'?true:false,
							  style:'margin-top:2px;margin-left:20px',
							  value:customObj&&customObj.type=='1'?customObj.desc:null,
							  valueObj:customObj&&customObj.type=='1'?{"id":customObj.value}:{}
						};
        	
        	var delImg={
						xtype:'container',
					    width:15,
					    height:10,
					    border:false,
					    heidden:obj.customFlag?false:true,
					    style:'background:url(/images/del.gif) no-repeat center left;',
					    listeners:{ 
					    	   click:{
					    		   element:'el',
					    		   fn:function(){
					    			   var d=Ext.getCmp("mainPanelID").body.dom;
					    			   var top=d.scrollTop-40;
					    			   Ext.getCmp("tableContainer_id").remove(Ext.getCmp(this.id).ownerCt.ownerCt);
					    			   d.scrollTop=top;
					    		   		}
					    	   	     }
					    		 }
        				};
        	
        	var rightItem={
		        			height:'100%',
		        			width:"39%",
		        			bodyStyle:'border-width:0 1px 1px 1px',
		        			itemId:'parentLabel',
		        			layout:{type:'hbox',align:'center'},
		        			items:obj.type=='2'?[rightDesc]:(obj.type=='1'?[rightCom,rightDesc]:[rightCom,rightText,delImg])
        		};
        	 var item={
	        		   xtype:'container',
	        		   columnWidth:'1',
	  				   height:40,
	  				   layout:{type:'hbox',align:'center'},
	  				   style:'margin-top:0;border-top:0',
	  				   items:[descItem,centerItem,rightItem]
  				   };
  		  
		return item;
	 },
	 renderData:function(office_constant){
		 var me=this;
			var items=Ext.getCmp("tableContainer_id").items.items;
			var arry1="sex,birthdate,nation,nativeplace,birthplace,joinjobdate,health";
			var arry2="joinpartydate,majorpost,majorspecialty,education,currentpost,preparepost,terminalpost,resume,rewardsandpenalties,assessment,postreason,familyandrelation";
			for(var i=0;i<items.length;i++){
				if(items[i].id=="titleRow")
					continue;
				var col1=items[i].items.items[0];//第一列
				var col2=items[i].items.items[1];//第二列
				var col3=items[i].items.items[2];//第三列
				var id=items[i].items.items[0].id;
				if(arry1.indexOf(id)>-1||arry2.indexOf(id)>-1){
					if(arry1.indexOf(id)>-1){
						if(office_constant.mainfields[id].type=="2")
							col2.child('#centerComID').setValue(office_constant.mainfields[id].type);
						else if(office_constant.mainfields[id].type=="1"){//指标   Ext.getCmp(col3.id).ownerCt.child('#parentLabel')child('#childCCValue');;
							var com=Ext.getCmp(col3.id).ownerCt.child('#parentLabel').child('#childItemID');
							com.setValue(office_constant.mainfields[id].desc);
							com.valueObj={"id":office_constant.mainfields[id].value};
						}
					}
				}
			}
			var centerStore=Ext.create('Ext.data.Store',{
				fields:["desc","value"],
				data:[{"desc":"HR指标","value":"1"},
					  {"desc":"存储过程","value":"2"}]
			});		
			var tablePanel=Ext.getCmp("tableContainer_id");
			
			//自定义行
			var customArry=office_constant.customfields;
			for(var i=0;i<customArry.length;i++){
				tablePanel.add(me.creaTableItem({"customFlag":true,"type":"3"},centerStore,customArry[i]));
			}
			
			
    },
	saveSetting:function(){
		
		var me=this;
		var map=new HashMap();
		//职务子集可不选 默认为空
		var postSet=Ext.getCmp("fieldSetIdCom").getValue();//职务子集
		/*if(postSet==undefined||postSet==''){
			Ext.showAlert("职务子集不可为空");
			return;
		}*/
		map.put("postSet",postSet?postSet:'');
		var postOrg=Ext.getCmp("itemidCom").getValue();//任职单位指标
		/*if(postOrg==undefined||postOrg==''){
			Ext.showAlert("任职单位不可为空");
			return;
		}*/
		map.put("postOrg",postOrg?postOrg:'');
		var postState=Ext.getCmp("postStateId").getValue();//任职状况指标
		/*if(postState==undefined||postState==''){
			Ext.showAlert("任职状况不可为空");
			return;
		}*/
		map.put("postState",postState?postState:'');
		
		var jobName=Ext.getCmp("jobNameId").getValue();//
		/*if(jobName==undefined||jobName==''){
			Ext.showAlert("职务名称不可为空");
			return;
		}*/
		map.put("jobname",jobName?jobName:'');
		
		var filterExpr=Ext.getCmp("filterExpr").valueObj;//干部筛选条件
		map.put("filterExpr",filterExpr?filterExpr:'');
		
		var dbitems=Ext.getCmp("dbPerCheckId").items.items;
		var nbase="";
		for(var i=0;i<dbitems.length;i++){
			if(dbitems[i].value){
				nbase+=dbitems[i].inputValue+",";
			}
		}
		if(nbase==''){
			Ext.showAlert('请选择人员库！');
			return;
		}
		map.put("nbase",nbase.substring(0,nbase.length-1));
		
		var mainfields=new HashMap();
		var customfields=[];
		var items=Ext.getCmp("tableContainer_id").items.items;
		
		var arry1="sex,birthdate,nation,nativeplace,birthplace,joinpartydate,joinjobdate,health,";
		var arry2="majorpost,majorspecialty,education,currentpost,preparepost,terminalpost,resume,rewardsandpenalties,assessment,postreason,familyandrelation,";
		var cc_Value="proc_officer_sex,proc_officer_birthdate,proc_officer_nation,proc_officer_nativeplace,proc_officer_birthplace,proc_officer_joinpartydate," +
				"proc_officer_joinjobdate,proc_officer_health,proc_officer_majorpost,proc_officer_majorspecialty,proc_officer_education,proc_officer_currentpost," +
				"proc_officer_preparepost,proc_officer_terminalpost,proc_officer_resume,proc_officer_rewards_penalties,proc_officer_assessment,proc_officer_postreason,proc_officer_familyandrelation,";
		var tempCustom="";
		for(var i=0;i<items.length;i++){
			if(items[i].id=="titleRow")
				continue;
			
			var col1=items[i].items.items[0];//第一列
			var col2=items[i].items.items[1];//第二列
			var col3=items[i].items.items[2];//第三列
			var id=items[i].items.items[0].id;
			var itemid="";
			var desc="";
			if(arry1.indexOf(id)>-1||arry2.indexOf(id)>-1){
				if(arry1.indexOf(id)>-1){//支持修改存储/取得存储名
					var combo=col2.items.items[0];
					var type=combo.value;
					var rightItem=Ext.getCmp(combo.id).ownerCt.ownerCt.child('#parentLabel');
					
					if(type=='1'){//取指标
						var itemCom=rightItem.child('#childItemID');
						itemid=itemCom.valueObj.id;
						
					}else{//取存储
						var cc= rightItem.child('#childCCValue');
						itemid=cc.value;
					}
					if(!itemid)
						itemid="";
					mainfields.put(id,{"name":id,"type":type,"value":itemid});
				}
				if(arry2.indexOf(id)>-1){//固定为存储值
					var rightItem=Ext.getCmp(col3.id).ownerCt.child('#parentLabel');
					var value=rightItem.child('#childCCValue').value;
					if(!value)
						value="";
					itemid=value;
					mainfields.put(id,{"name":id,"type":"2","value":itemid});
				}
				
			}else{//取自定义内容
				var desc=col1.child('#nameValue').value;//描述信息
				var columnid=col1.child('#nameValue').columnId;//自定义列id
				var type=col2.child('#centerComID').value;
				var rightItem=Ext.getCmp(col3.id).ownerCt.child('#parentLabel');
				var value="";
				if(type=='1'){//取指标
					var itemCom=rightItem.child('#childItemID');
					if(itemCom.valueObj!=null)
						value=itemCom.valueObj.id;
					if(!value||value==""){
						Ext.showAlert(desc+"对应值不可为空");
						return;
					}
					if(desc.length>25){
						Ext.showAlert(desc+"：名称长度超出限制！");
						return;
					}
					if(tempCustom.indexOf(value)>-1){
						Ext.showAlert("自定义指标:"+itemCom.valueObj.text+"不可重复！");
						return;
					}
					tempCustom+=','+value;
					//columnid=value;
				}else{//取存储
					var cc= rightItem.child('#childCCValue');
					if(cc.value)
						value=cc.value;
					if(cc_Value.indexOf(value+",")>-1){
						Ext.showAlert("自定义内容存储过程不可与固定存储过程相同！");
						return ;
					}
					if(value==""){
						Ext.showAlert(desc+"对应值不可为空");
						return ;
					}
				}
				customfields.push({"name":desc,"type":type,"value":value,"columnid":columnid})
			}
			
			
		}
		
		
		map.put("mainfields",mainfields);
		//获取自定义信息内容
	  
	    
		map.put("customfields",customfields);
		map.put("flag","save");
		Rpc({functionId:'OM000000001',success:function(res){
			var res=Ext.decode(res.responseText);
			if(res.typeFlag){
				Ext.showAlert("保存成功！");
				var map=new HashMap();
				Ext.getCmp("viewPortID").destroy();
				Rpc({functionId:'OM000000001',success:me.init,scope:me},map);
			}else{
				Ext.showAlert(res.msg);
			}
		},scope:me},map);
	},
	creaFieldItemStore:function( opt, newValue, oldValue,opts){
		 var map=new HashMap();
		 var setId=this.value;
		 Ext.getCmp("itemidCom").setValue();
		 map.put("flag","fieldItem");
		 if(!setId){
			 setId="";
		 }
		 map.put("setId",setId);
		 Rpc({functionId:'OM000000001',success:function(res){
			 res=Ext.decode(res.responseText);
			 var itemStore=Ext.create('Ext.data.Store',{
					fields:["itemid","itemDesc","type","codesetId"],
			 		data:Ext.decode(res.fieldSet)
			 		});
			 var bWStore=Ext.create('Ext.data.Store',{
					fields:["itemid","itemDesc","type","codesetId"],
			 		data:Ext.decode(res.bWList)
			 		});
			 var fieldItem=Ext.create('Ext.data.Store',{
					fields:["itemid","itemDesc","type","codesetId"],
			 		data:Ext.decode(res.fieldItem)
			 		});
			 
			 Ext.getCmp("itemidCom").setStore(itemStore);
			 Ext.getCmp("postStateId").setStore(bWStore);
			 Ext.getCmp("jobNameId").setStore(fieldItem);
			 
		 },scope:this},map);
		
	},
	//createBottomPanel:function(obj){},
	filterSet:function(){
		var map=new HashMap();
		map.put("express",/*this.valueObj*/decode(this.valueObj));
		Ext.require('EHR.complexcondition.ComplexCondition',function(){
     		Ext.create("EHR.complexcondition.ComplexCondition",{dataMap:map,inforkindFlag:'A',imodule:"3",opt:"1",title:'检索条件',callBackfn:function(c_expr){
     			Ext.getCmp('filterExpr').valueObj=c_expr;
     		}});
     	});
	}
	
});