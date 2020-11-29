Ext.define("EHR.querybox.QueryField",{
    extend:'Ext.form.field.Picker',
    xtype:'queryfield',
    subModuleId:undefined,
    customParams:undefined,
    success:Ext.emptyFn,
    callBackScope:undefined,
    fieldsArray:[],
    fieldsMap:{},
    margin:0,
    queryPlanPanel:undefined,
    queryCondPanel:undefined,
    itemStore:undefined,
    
    listeners:{
    	    render:function(){
    	    	//30561 fieldsArray为null时直接返回
    	    	if(null == this.fieldsArray)
    	    	  return;
    	    	  
    	        for(var i=0;i<this.fieldsArray.length;i++){
	    	       if(this.fieldsArray[i].type=='M'){
	    	           this.fieldsArray.splice(i,1);
	    	           i--;
	    	           continue;
	    	       }
	    		   this.fieldsArray[i].get = function(key){return this[key];};
	    		   this.fieldsMap[this.fieldsArray[i].itemid] = this.fieldsArray[i];
	    	   }
    	       
    	    }
    },
    
    createPicker:function(){
       var me = this,
       pickerConfig = {
		  xtype:'container',
       	  floating:true,
       	  layout:{type:'vbox',align:'stretch'},
       	  style:'background-color:white;border:1px solid #c5c5c5;',
          shadow:false,
          padding:10,
          minHeight:300,
          focusable:true,
          tabIndex:-1
       },
       itemsArray = [{
          xtype:'box',
          html:'<img style="float:left" src="/components/querybox/images/wuxing.png"/><div style="float:left;line-height:16px;">查询方案</div>'
       }];
       
       me.queryPlanPanel = Ext.widget('container',{
          layout:{type:'vbox',align:'stretch'},
          margin:'3 0 3 15',
          minHeight:50,
          maxHeight:100,
          scrollable:true,
          defaults:{
        	    height:16,
        	  	listeners:{
                  render:function(){
  					this.getEl().on({
  					   mouseover:me.planMouseOver,
  					   mouseout:me.planMouseOut,
  					   click:me.loadPlanData,
  					   dblclick:me.planQuery,
  					   scope:me,
  					   param:this
  					});              
                  }
               }
          },
          items:{
             xtype:'container',
             conds:'',
             expr:'',
             layout:'hbox',
             items:[{xtype:'box',html:'全部',flex:10},{xtype:'box'}]
          }
       });
       
       me.loadPlan();
       
       itemsArray.push(me.queryPlanPanel);
       itemsArray.push({xtype:'box',style:'border-top:1px solid #c5c5c5;margin:5px 0px 5px 0px;'});
       
       itemsArray.push({
          xtype:'box',
          html:'<img style="float:left" src="/components/querybox/images/wuxing.png"/><div style="float:left;line-height:16px;">条件查询</div>'
       });
       

       
       itemsArray.push(me.createQueryCondPanel());
       
       itemsArray.push({
         xtype:'container',
         items:{
            xtype:'image',src:'/images/add.gif',style:'margin-left:15px;cursor:pointer;',width:16,height:16,
       		listeners:{
                render:function(){ this.getEl().on('click',me.addCondItem,me); }
            }
         }
       });
       
       itemsArray.push({
           xtype:'container',
           margin:'5 0 10 15',
           items:[{
              xtype:'button',text:'应用',handler:me.doQuery,scope:me
           }]
         });
       
       itemsArray.push({
          xtype:'container',
          layout:'hbox',
          items:[{
        	     xtype:'image',
        	     width:16,height:16,
        	     style:'cursor:pointer',
        	     src:'/components/querybox/images/right_sign.png',
        	     listeners:{
        	    	    render:function(){
        	    	    		this.getEl().on("click",function(){
	        					if (me.savePanel.isVisible()) {
	        						me.savePanel.setVisible(false);
	        						this.setSrc("/components/querybox/images/right_sign.png");
	        					} else {
	        						me.savePanel.setVisible(true);
	        						me.savePanel.child('textfield').focus();
	        						this.setSrc("/components/querybox/images/down_sign.png");
	        					}
	        			},this);
        	    	    	
        	    	    }
        	     }
          },
          //添加点击事件，效果等同点击图片事件  wangb1 27583 20170510
          {xtype:'label',text:'保存为查询方案',style:'cursor:pointer',listeners:{
          		render:function(){
          			this.getEl().on("click",function(){
          				if (me.savePanel.isVisible()) {
	        				me.savePanel.setVisible(false);
	        				this.previousSibling().setSrc("/components/querybox/images/right_sign.png");
	        			} else {
	        				me.savePanel.setVisible(true);
	        				me.savePanel.child('textfield').focus();
	        				this.previousSibling().setSrc("/components/querybox/images/down_sign.png");
	        			}
          			},this);
          			
          		}
          	}
          
          }]
       });
       
       me.savePanel = Ext.widget("container",{
			    layout:"hbox",
				border:0,
				collapseMode:'mini',
				header:false,
				hidden:true,
				margin:'10 0 0 15',
				items:[
					{
						xtype:'textfield',
						emptyText:"请输入方案名称",
						validator:function(value) {
							var len = value.replace(/[^\x00-\xff]/gi, "--").length;
							if (Ext.isEmpty(value) || len == 0) {
							
								return "方案名称不能为空";
							}
							
							if (len > 0 && len <= 200) {
								return true;
							} else {
								return "方案名称过长，无法保存！";
							}
						}	
					},
					{
						xtype: 'button',
						text:'保存',
						margin:'0 0 10 10',
						handler: me.savePlanData,
						scope:me
					}
				]
			});
       itemsArray.push(me.savePanel);
       pickerConfig.items = itemsArray;
       return Ext.widget(pickerConfig);
       
    },
    onExpand:function(){
        this.picker.setWidth(360);
    },
    loadPlan:function(){
    		var me = this;
		var map = new HashMap();
		map.put("type","1");//1为查询，2为保存,3为删除
		map.put("subModuleId",me.subModuleId);
		Rpc({functionId:"ZJ100000051",success:me.setPlan,scope:me},map);
    	
    },
    setPlan:function(responseValue){
    		var me = this;
		var value=responseValue.responseText;
		var map=Ext.decode(value);
		var planDatas = map.querySchemeData;
		if(planDatas.length>0){//xiegh add 非空判断
		    for(var i=0;i<planDatas.length;i++){
		    	   var plan = planDatas[i];
		    	   me.queryPlanPanel.add({
		    		   xtype:'container',
		    		   planId:plan.id,
		    		   expr:plan.exp,
		    		   conds:plan.cond,
		    		   layout:'hbox',
		    		   items:[{xtype:'box',html:plan.name,flex:10},
		    		       {xtype:'image',width:16,height:16,hidden:true,src:'/images/del.gif',
		    			       listeners:{
				    			   render:function(){
				    				   this.getEl().on('click',me.deletePlan,me,this);
				    			   }
		    		           }
		    		       }
		    		   ]
		    	   });
		    }
		}
    	
    },
    deletePlan:function(a,b,c){
    	     var planCom = c.ownerCt;
    	     var map = new HashMap();
    	     map.put("id",planCom.planId);
    	     map.put("type",'3');
    	     map.put("subModuleId",this.subModuleId);
    	     Rpc({functionId:"ZJ100000051",success:function(){
    	    	     planCom.ownerCt.remove(planCom);
    	     }},map);
    },
    createQueryCondPanel:function(){
        var me = this;
        me.itemStore = Ext.create('Ext.data.Store',{
           fields:['type','itemid','itemdesc','codesetid','format','codesource','ctrltype','nmodule','parentid','operationData','codesetValid'],
           data:me.fieldsArray
        });
        me.exprStore = Ext.create('Ext.data.Store',{
           fields:['expr'],
           data:[{expr:'='},{expr:'<>'},{expr:'>'},{expr:'>='},{expr:'<'},{expr:'<='}]
        });
    		me.queryCondPanel = Ext.widget('container',{
        	   layout:'vbox',
        	   margin:'5 0 5 0',//xiegh 20170407 bug26933 add设置container高度最大300，超出显示滚动条
           	   maxHeight:200,
    	       scrollable:true,
           defaults:{
             xtype:'container',
             layout:'hbox',
           	 margin:'5 0 5 0'
           },
           items:[{
           	  items:[{xtype:'box',width:18},{
           	     xtype:'combo',
           	     width:100,
           	     height:22,
                 displayField:'itemdesc',
                 valueField:'itemid',
                 editable:false,
                 store:me.itemStore,
                 listeners:{
                     select:me.condSelected,
                     scope:me
                 }
           	  },{
                 xtype:'combo',
                 width:50,
                 height:22,
                 editable:false,
                 margin:'0 0 0 10',
                 value:'=',
                 displayField:'expr',
                 valueField:'expr',
                 store:me.exprStore
              },{
                width:100,
                itemId:'valueBox',
                margin:'0 0 0 10',
                xtype:'textfield'
              },{xtype:'box'}]
           }]
        });
        
        return me.queryCondPanel;
    },
    
    addCondItem:function(config){
        var me = this;
        config = config||{};
        this.queryCondPanel.add({
        		items:[(config.index==0||me.queryCondPanel.items.length==0)?{xtype:'box',width:18}:{//changxy 2016-05-19 全部删除后创建第一个查询panel时设置逻辑运算为空
              	 xtype:'label',width:18,text:config.operator=='+'?'或':'且',value:config.operator||'*', 
              	 style:'margin-top:3px;cursor:pointer;padding-left:3px;background:url(/components/tableFactory/tableGrid-theme/images/grid/dirty.gif) no-repeat',
              	 listeners:{
              		 render:function(){
              			 this.getEl().on('click',function(){
              				var scrollY = me.queryCondPanel.getScrollY();//xiegh  20170412 26933 报批状态选择滚动条定位
              				 if(this.text=='且'){ 
              					 this.setText('或'); this.value='+';
              				 }else{
              					this.setText('且'); this.value='*';
              				 } 
              		        me.queryCondPanel.setScrollY(scrollY);
              			 },this);
              		 }
        		     }
              },{
                 xtype:'combo',
                 width:100,
                 displayField:'itemdesc',
                 valueField:'itemid',
                 editable:false,
                 store:me.itemStore,
                 value:config.itemid,
                 record:config.record,
                 listeners:{
                     select:me.condSelected,
                     scope:me
                 }
              },{
                 xtype:'combo',
                 width:50,
                 margin:'0 0 0 10',
                 editable:false,
                 value:config.char?config.char:'=',
                 displayField:'expr',
                 valueField:'expr',
                 store:me.exprStore
              },config.record?me.getCondEditor(config.record,config.value):{
                width:100,
                itemId:'valueBox',
                margin:'0 0 0 10',
                xtype:'textfield'
              },{
                xtype:'image',src:'/images/del.gif',style:'margin-top:3px;cursor:pointer;',height:16,width:16,listeners:{
                   render:function(){ this.getEl().on('click',this.removeCondItem,this);}
                },
                removeCondItem:function(){
                    this.ownerCt.ownerCt.remove(this.ownerCt);
                }
              }]
        });
      var id = me.queryCondPanel.getId();
      document.getElementById(id).scrollTop = document.getElementById(id).scrollHeight+30;//xiegh 20170411 26933 add新增操作滚动条定位
    },
    condSelected:function(combo,record){
    	me = this;
    	var scrollY = me.queryCondPanel.getScrollY();//xiegh  20170412 26933 报批状态选择滚动条定位
        var editor = this.getCondEditor(record);    
        combo.ownerCt.remove(combo.ownerCt.child('#valueBox'),true);
        combo.ownerCt.insert(3,editor);
        combo.record = record;
        me.queryCondPanel.setScrollY(scrollY);
    },
    getCondEditor:function(record,value){
    		var editor,
            operationData = record.get("operationData"),
            type = record.get("type"),
            codesetid = record.get("codesetid");
        if(operationData){
            editor = Ext.widget("combo",{
            		itemId:'valueBox',
            		margin:'0 0 0 10',
               width:100,
               value:value,
               displayField:'dataName',
               valueField:'dataValue',
               store:{
                  fields:['dataName','dataValue'],
                  data:record.get("operationData")
               }
            });
        }else if(type=='A' && codesetid && codesetid.length>0 && codesetid!='0'){
                editor = Ext.widget('codecomboxfield',{
            		itemId:'valueBox',
            		margin:'0 0 0 10',
            		value:value,
            		 width:100,
                 codesetid:codesetid,
                 nmodule:record.get('nmodule'),
                 ctrltype:record.get('ctrltype'),
                 codesource:record.get('codesource'),
                 onlySelectCodeset:record.get('codesetValid')==undefined?true:record.get('codesetValid')
            });
            if(value && value.length>0){
	            	var map = new HashMap();
	        		map.put("type","4");//1为查询，2为保存,3为删除
	        		map.put("subModuleId",this.subModuleId);
	        		map.put("objs",[{codeset:record.get("codesetid"),value:value,cond:'key'}]);
	        		Rpc({functionId:"ZJ100000051",success:function(resp){
	        			editor.setValue(Ext.decode(resp.responseText).map.key);
	        		}},map);
            }    
        }else if(type=='D'){
            editor = Ext.widget('datetimefield',{
            		itemId:'valueBox',
            		margin:'0 0 0 10',
            		value:value,
            		width:100,
                 format:record.get('format')
            });
        }else if(type=='N'){
            editor = Ext.widget('numberfield',{itemId:'valueBox',margin:'0 0 0 10',width:100,value:value});
        }else
            editor = Ext.widget('textfield',{itemId:'valueBox',margin:'0 0 0 10',width:100,value:value});
            
        return editor;
    },
    planMouseOver:function(event,element,o){
       if(o.param.selected)
    	       return;
       o.param.setStyle('background-color','rgb(255,248,210)');
       o.param.items.items[1].setVisible(true);
    },
    planMouseOut:function(event,element,o){
    	   if(o.param.selected)
 	       return;
    	   o.param.setStyle('background-color','white');
    	   o.param.items.items[1].setVisible(false);
    },
    loadPlanData:function(event,element,o){
    	o.param.ownerCt.items.each(function(p){
    	   	  p.selected = false;
    	   	  p.setStyle('background-color',"white");
    	   	  p.items.items[1].setVisible(false);
       });
       o.param.setStyle('background-color',"#f0f0f0");
       o.param.items.items[1].setVisible(true);
       o.param.selected=true;
       this.queryCondPanel.removeAll(true);
       if(o.param.conds.length<1){
    	       this.queryCondPanel.add({
            	  items:[{xtype:'box',width:18},{ 
            	     xtype:'combo',
            	     width:100,
                  displayField:'itemdesc',
                  valueField:'itemid',
                  store:this.itemStore,
                  listeners:{
                      select:this.condSelected,
                      scope:this
                  }
            	  },{
                  xtype:'combo',
                  width:50,
                  margin:'0 0 0 10',
                  value:'=',
                  editable:false,
                  displayField:'expr',
                  valueField:'expr',
                  store:this.exprStore
               },{
                 width:100,
                 itemId:'valueBox',
                 margin:'0 0 0 10',
                 xtype:'textfield'
               },{xtype:'box'}]
            });
    	      return;
       }
       var conds = o.param.conds.split('`');
       var expr = o.param.expr;
       expr=getDecodeStr(expr).replace(/＋/g, "+").replace(/＊/g, "*");
       var itemid,char,value,operator;
       /**
        * author xiegh
        * date 20170519
        * array:存放且或（* or +）的数组
        * 
        */
       var array = [];
       for(var i = 1;i<expr.length;i++){
    	   if('+'==expr[i] || '*'==expr[i])
    		   array.push(expr[i]);
       }
       
       for(var i=0;i<conds.length;i++){
    	       if(conds[i].length<1)
    	    	      continue;
    	       char = '=';
    	       if(conds[i].indexOf('<>')>-1)
    	    	        char = "<>";
    	       else if(conds[i].indexOf('>=')>-1)
    	    	   		char = ">=";
    	       else if(conds[i].indexOf('<=')>-1)
    	    	   		char = "<=";
    	       else if(conds[i].indexOf('>')>-1)
    	    	   		char = ">";
    	       else if(conds[i].indexOf('<')>-1)
    	    	   		char = "<";
    	       
    	       itemid = conds[i].split(char)[0];
    	       value = conds[i].split(char)[1];
    	       if(i==0)
    	    	   operator ='';
    	       else
    	    	   operator = array[i-1]
    	   
    	       var config = {
    	    		   operator:operator,
    	    		   itemid:itemid,
    	    		   value:value,
    	    		   char:char,
    	    		   record:this.fieldsMap[itemid],
    	    		   index:i
    	       };
    	       this.addCondItem(config);
       }
       
       
    },
    
    savePlanData:function(){
    	    var me = this;
    	    var planName = me.savePanel.child('textfield').getValue();
    	    if(planName.length<1){
    	    	me.savePanel.child('textfield').focus();
    	    	   return;
    	    }
    	    	  
    	    var data = me.getQueryData();
    	    if(data.conds.length<1){
    	          Ext.Msg.alert("提示信息","请添加查询条件！");
    	    	   return;
    	    }
    		var map = new HashMap();
    		map.put("type","2");//2为保存，1为查询,3为删除
    		map.put("subModuleId",me.subModuleId);
    		map.put("name",planName);
    		map.put("exp",getEncodeStr(data.expr));
    		map.put("cond",data.conds);
    		Rpc({functionId:"ZJ100000051",success:me.savePlanSuccess,scope:me},map);
    },
    savePlanSuccess:function(response){
    	    var me = this;
    	    var param = Ext.decode(response.responseText);
    	    //验证保存时名称是否重名，重名返回true    25449   wangb1  20170505
    	    if(param.exist){
    	    	Ext.Msg.alert("提示信息","名称已存在！");
    	    	return;
    	    }
    	    me.savePanel.child('textfield').setValue("");
    	    this.queryPlanPanel.add({
	    		   xtype:'container',
	    		   planId:param.id,
	    		   expr:getDecodeStr(param.exp),
	    		   conds:param.cond,
	    		   layout:'hbox',
	    		   items:[{xtype:'box',html:param.name,flex:10},
	    		       {xtype:'image',width:16,height:16,hidden:true,src:'/images/del.gif',
	    			       listeners:{
			    			   render:function(){
			    				   this.getEl().on('click',me.deletePlan,me,this);
			    			   }
	    		           }
	    		       }
	    		   ]
	    	   });
    },
    getQueryData:function(){
    	    var me = this;
    	    var conds = "";
	    var expr = "";
	    var i=1;
	    me.queryCondPanel.items.each(function(c,index){
	    	    if(!c.items.items[1].getValue() || c.items.items[1].getValue().length<1){
	    	    	   return;
	    	    }
	    	    
	    	    if(index==0)
	    	    	   expr+=i;
	    	    else{
	    	    	   expr+=c.items.items[0].value+""+(index+1);
	    	    }
	    	    conds += c.items.items[1].getValue()+c.items.items[2].getValue();
	    	    var record = c.items.items[1].record;
	    	    if(record.get("operationData")){
	    	    	   conds +=c.items.items[3].getValue();
	    	    }
	    	    else if(record.get("codesetid") && record.get("codesetid").length>1 && record.get){
	    	       conds +=c.items.items[3].getValue().split('`')[0];
	    	    }else{
	    	    	   conds +=c.items.items[3].getValue();
	    	    }
	    	    conds+='`';
	    	    i++;
	    });
	    
	    return {expr:expr,conds:conds};
    	
    },
    planQuery:function(a,b,c){
    	     var data = {};
	    	 data.conds = c.param.conds;
	    	 data.expr = c.param.expr;
	    	 var map = new HashMap();
			 map.put("type","2");//1为输入查询，2为方案查询
			 map.put("subModuleId",this.subModuleId);
			 map.put("customParams", this.customParams);
				
			map.put("exp",getEncodeStr(data.expr));
			map.put("cond",getEncodeStr(data.conds));
			Rpc({functionId:this.funcId,success:this.queryResult,scope:this},map);
			this.ownerCt.removeAllKeys(true);
    },
    doQuery:function(){
    	    	 var data = this.getQueryData();
    	  if(data.conds.length<1){
    	          Ext.Msg.alert("提示信息","请添加查询条件！");
    	    	  return;
    	    }
         var map = new HashMap();
		 map.put("type","2");//1为输入查询，2为方案查询
		 map.put("subModuleId",this.subModuleId);
		 map.put("customParams", this.customParams);
			
		 if (!Ext.isEmpty(data.expr) && !Ext.isEmpty(data.conds)) {
				map.put("exp",getEncodeStr(data.expr));
				map.put("cond",getEncodeStr(data.conds));
				Rpc({functionId:this.funcId,success:this.queryResult,scope:this},map);
				this.ownerCt.removeAllKeys(true);
		 }
    },
    
    queryResult:function(responseValue){
    		var me = this;
		var value=responseValue.responseText;
		var map=Ext.decode(value);
		if (me.success)
				Ext.callback(me.success, me.callBackScope,[map]);
		me.collapse();
    }
});


