/**
    查询组件参数：
	    subModuleId:功能唯一id（必须)
	    fieldSetable:是否可以设置查询指标,默认true
	    fieldPubSetable:是否可以设置查询指标公共方案,默认true
	    
	    optionalQueryFields:设置查询字段时的备选指标（必须）
	    defaultQueryFields:默认查询指标（必须）
	    doQuery:执行查询回调方法,
	    scope:doQuery的上下文环境,

**/

Ext.define("EHR.commonQuery.CommonQuery",{
    extend:'Ext.container.Container',
    xtype:'commonquery',
    requires:["EHR.extWidget.field.DateTimeField","EHR.extWidget.field.CodeTreeCombox","EHR.commonQuery.QueryFieldsSelector"],
    
    //功能唯一id
    subModuleId:undefined,
    //是否可以设置查询指标
    fieldSetable:true,
    //是否可以设置查询指标公共方案
    fieldPubSetable:true,
    //备选指标
    optionalQueryFields:undefined,
    //自定义备选指标交易类
    fieldsFunctionId:undefined,
    //默认查询指标
    defaultQueryFields:undefined,
    //查询时调用的方法
    doQuery:undefined,
    //doQuery
    scope:undefined,
    
    beforeFieldRender:undefined,
    
    bodyPadding:5,
    
    commonQueryPanel:undefined,
    
    //筛选条件 区域对象
    filtersArea:undefined,
    //查询条件 区域对象
    queryFieldsArea:undefined,
    //权限控制方式 0：不控制 1：人员范围 2：操作单位 3：业务范围（必须设置nmodule值，否则没数据）
    ctrltype:'1',
    //业务模块号
    nmodule:undefined,
    
    layout:{
       type:'vbox',
       align:'stretch'
    },
    
    //保存日期选择格式的元素id
    selectedDateFieldId:undefined,
    
    filterItemStore:undefined,
    
    //初始化组件
    initComponent:function(){
        this.callParent(arguments);
        if(!this.subModuleId && this.fieldSetable)
               throw new Error(" subModuleId is undefined! ");
        
        this.initDisplayController();
        this.initFiltersArea();
        this.initQueryFieldsArea();
        this.initDateSelectBox();
        this.initImpData();
        
    },
    
    initDisplayController:function(){
        Ext.util.CSS.createStyleSheet(".x-queryCollapsed{border:none !important; background-color:white !important;height:0px !important}");
    	this.commonQueryPanel = Ext.widget('panel',{
              flex:10,
              border:false,
              collapsed:true,
              bodyPadding:'10 0 0 0',
              collapseMode:'mini',
              collapsedCls:'queryCollapsed',
              layout:{
			       type:'vbox',
			       align:'stretch'
			  }
        });
    	this.displayButton = Ext.widget("image",{
    		xtype:'image',src:rootPath+"/components/querybox/images/downbig.png",width:100,height:8,
    		style:'cursor:pointer',
    		listeners:{
    		     click:{
    		         element:'el',
    		         fn:function(){
    		           if(this.commonQueryPanel.collapsed){
    		             this.commonQueryPanel.expand();
    		             this.displayButton.setSrc(rootPath+"/components/querybox/images/upbig.png");
    		           }else{
    		           	 this.commonQueryPanel.collapse();
    		           	  this.displayButton.setSrc(rootPath+"/components/querybox/images/downbig.png");
    		           }
    		             
    		         },
    		         scope:this
    		     }
    		}
    });
        this.add(this.commonQueryPanel);
        this.add({
        	xtype:'container',
        	layout:{
        		type:'hbox',
		    	pack:'center'
        	},
        	items:this.displayButton
        });
    },
    //创建筛选区域
    initFiltersArea:function(){
    	  var me = this;
    	 me.filterItemStore = {};
    	  var filterDiv = document.createElement("div");
    	  filterDiv.style.height="100%";
    	  filterDiv.style.width="100%";
    	  filterDiv.style.paddingTop="5px";
    	  
          this.filtersArea = Ext.widget('container',{
          		 flex:1,
                 layout:'border',
                 style:'border:solid #c5c5c5 1px;background-color:#f9f9f9;',
                 height:34,
                 items:[{xtype:'label',region:'west',text:'筛选条件>',style:'color:#333333;font-size:14px;font-family:微软雅黑;font-weight:bolder;height:22px;line-height:22px;',margin:5},
                 {xtype:'container',region:'center',contentEl:filterDiv},
                 {
                 		xtype:'container',layout:'hbox',region:'east',
                     	items:[me.fieldSetable?{
                     		xtype:'image',height:17,width:17,style:'cursor:pointer;',margin:8,src:'/components/tableFactory/tableGrid-theme/images/Settings.png',listeners:{
                          	     click: {
							            element: 'el',
							            fn: function(){
							                var selectorConfig = {
							                      subModuleId:this.subModuleId,
							                      items:this.queryFields,
							                      fieldset:undefined,
							                      functionId:this.fieldsFunctionId,
							                      fieldPubSetable:this.fieldPubSetable,
							                      customOptionalFields:undefined,
							                      defaultQueryFields:this.defaultQueryFields,
							                      afterfunc:me.setFields.bind(me)
							                
							                };
							                if(Ext.isArray(this.optionalQueryFields)){
							                      selectorConfig.customOptionalFields = this.optionalQueryFields;
							                }else
							                      selectorConfig.fieldset = this.optionalQueryFields;
							                
							            	var selector = Ext.widget("queryfieldsselector",selectorConfig);
							            },
							            scope:me
							     }
                       }
                	}:undefined,{
                     		xtype:'button',text:'清空',
                     		margin:'5 2 5 0',
                     		height:22,width:50,
                     	    handler:function(){
                     	    	 var num = 0; 
                     	         for(var key in this.filterItemStore){
                     	                Ext.destroy(this.filterItemStore[key]);
                     	                Ext.getCmp('queryField_'+key).setVisible(true);
                     	                delete this.filterItemStore[key];
                     	                num++;
                     	         }
                     	         this.resetFilterAreaSize(34);
                     	         if(num>0){
                     	        	 this.executeDoQuery();
                     	         }
                     	    },
                     	    scope:me
                     	}]
                  }]
          
          });
          
          //this.add(this.filtersArea);
          this.commonQueryPanel.add(this.filtersArea);
    },
    //创建查询区域
    initQueryFieldsArea:function(fields){
    	 var maxHeight = Ext.isIE?176:168;
         this.queryFieldsArea = Ext.widget('container',{
        	 maxHeight:maxHeight,
        	 scrollable:true,
               layout:{
			       type:'vbox',
			       align:'stretch'
			   }
         });
         //this.add(this.queryFieldsArea);
         this.commonQueryPanel.add(this.queryFieldsArea);
    },
    
    //创建选择时间类型的对象
    initDateSelectBox:function(){
        var me = this;
    	me.dateTypeSelectBox = Ext.widget('container',{
							                     floating:true,
							                     style:'border:1px #c5c5c5 solid;background:white;z-index:10000000000',
							                     shadow:false,
							                     width:144,
							                     layout:{type:'vbox',align:'stretch'},
							                     hidden:true,
							                     listeners:{
							                         mouseout:function(e){
							                             var ele = e.relatedTarget?e.relatedTarget:Ext.get(e.toElement);
							                             
							                              if(this.dateTypeSelectBox.owns(ele))
								                                 return;
								                          this.dateTypeSelectBox.setVisible(false);
							                         },
							                         scope:me,
							                         element:'el'
							                     },
							                     defaults:{
							                         padding:'2 0 2 5',
							                         listeners:{
							                             mouseover:me.mouseOverColor,
							                             mouseout:me.mouseOutColor,
							                             click:me.dateTypeSelected,
							                             scope:me,
							                             element: 'el'
							                         }
							                     },
							                     items:[{id:'dateTypeArea',xtype:'label',text:'时间段'},
							                     {id:'dateTypeYear',xtype:'label',text:'年限'},
							                     {id:'dateTypeMonth',xtype:'label',text:'月'},
							                     {id:'dateTypeDay',xtype:'label',text:'日'}],
							                     renderTo:document.body
		});
    },
    
    //加载数据
    initImpData:function(){
          var param = new HashMap();
          param.put("subModuleId",this.subModuleId);
          param.put("default",this.defaultQueryFields);
          Rpc({functionId:'ZJ100000141',success:this.beginView,scope:this},param);
    },
    
    //开始渲染查询字段
    beginView:function(res){
        var param =  Ext.decode(res.responseText);
        this.queryFields = param.planItems;
        //将查询字段渲染到查询区域
    	this.fillQueryFields(this.queryFields);
    },
    
    
    //渲染查询字段
    fillQueryFields:function(fields){
           //先清空所有以前的元素
           this.queryFieldsArea.removeAll(true);
           
           //将查询字段循环插入到 查询区域
           for(var i=0;i<fields.length;i++){
               //根据字段类型创建不同的查询对象
               var cmp =  this.createQueryField(fields[i]);
               // 重新设置指标后 如果是已选的筛选条件则隐藏
               for(var key in this.filterItemStore){
	               if(key==fields[i].itemid){
	            	   cmp.setVisible(false);
	               }
               }
               this.queryFieldsArea.add(cmp);
           }
    
    
    },
    
    //创建查询字段对象
    createQueryField:function(field){
        var me = this,queryField;
        if(me.beforeFieldRender)
             me.beforeFieldRender(field);
          if(field.itemtype=='N'){
               queryField = this.numberQueryField(field);
          }else if(field.itemtype=='D'){
               queryField = this.dateQueryField(field);
          }else if((field.codesetid && field.codesetid!='0') ||  field.codeData){
               queryField = this.codeQueryField(field);
          }else{
          	   queryField = this.textQueryField(field);
          }
          return queryField;
    
    },
    
    //数字类型 对象
    numberQueryField:function(field){
        var me = this;
        var queryField = Ext.widget('toolbar',{
                       id:'queryField_'+field.itemid,
                       field:field,
                       height:35,
                       style:'border:none;border-bottom:1px #d1d1d1 dashed',
                       items:[{
                           xtype:'label',text:field.itemdesc,width:144,style:'text-align:right;margin-right:20px'
                       },{
                            xtype:'numberfield',hideTrigger:true,decimalPrecision:field.formatlength,
                            listeners:{
                               specialkey: function(field, e){
				                    if (e.getKey() == e.ENTER) {
				                        field.ownerCt.items.items[3].focus();
				                    }
				                }
                            }
                       },'~',{
                            xtype:'numberfield',hideTrigger:true,decimalPrecision:field.formatlength,
                            listeners:{
                               specialkey: function(field, e){
				                    if (e.getKey() == e.ENTER||e.getKey() == e.TAB) {
				                        me.beginQuery(field);
				                    }
				                }
                            }
                       },'->',{
                            xtype:'button',
                            text:'√ 确定',
                            handler:this.beginQuery,
                            scope:this
                       }]
               });
               
               return queryField;
    },
    //日期类型对象
    dateQueryField:function(field){
        var me = this;
        
        var dateFormat = "Y-m-d";
        if(field.formatlength==4)
             dateFormat = "Y";
        else if(field.formatlength==7)
             dateFormat = "Y-m";
        else if(field.formatlength==16)
        	 dateFormat = "Y-m-d H:i";
        else if(field.formatlength==18)
        	 dateFormat = "Y-m-d H:i:s";
        field.dateFormat = dateFormat;
        
    	var queryField = Ext.widget('toolbar',{
               		   id:'queryField_'+field.itemid,
               		   field:field,
               		   height:35,
               		   dateType:'area',
               		   style:'border:none;border-bottom:1px #d1d1d1 dashed',
                       items:[{
                           xtype:'container',
                           width:144,
                           style:'margin-right:20px',
                           layout:{
                       		type:'hbox',
               		    	pack:'end'
                           },
                           items:[{
                           		xtype:'label',text:field.itemdesc,width:140,style:'text-align:right'
                           },{
	                           	xtype:'image',src:rootPath+'/workplan/image/jiantou.png',width:8,height:8,margin:'5 0 0 3',
	                           	listeners:{
	                           	     click: {
								            element: 'el',
								            fn: function(){
								                me.selectedDateFieldId = 'queryField_'+field.itemid;
								                me.dateTypeSelectBox.show();
								                me.dateTypeSelectBox.alignTo(arguments[1],'tl-bl?',undefined);
								            }
								     }
	                           	}
                           
                           }]
                           
                       },
                       //时间段对象，默认显示，当切换时间查询类型时,动态设置 时间段对象和年限对象、月日对象的显示和隐藏
                       {
                       		xtype:'container',layout:'hbox',
                       		items:[{
                       			xtype:'datetimefield',format:dateFormat,
                       		    height:22,
                       			listeners:{
                              	    specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER) {
					                        this.ownerCt.items.items[2].focus();
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'至',padding:'2 5 2 5'},{
                       			xtype:'datetimefield',format:dateFormat,
                       			height:22,
                       			listeners:{
                              	    specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER||e.getKey() == e.TAB) {
					                        me.beginQuery(field.ownerCt);
					                    }
					                }
	                            }
                       		}]
                       },
                       //年限对象
                       {xtype:'container',hidden:true,layout:'hbox',
                       		items:[{
                       			xtype:'numberfield',hideTrigger:true,allowDecimals:false,
                       			//value:new Date().getFullYear( ),
                       			listeners:{
	                               specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER) {
					                        field.ownerCt.items.items[2].focus();
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'年 至',padding:'2 5 2 0'},{
                       			xtype:'numberfield',hideTrigger:true,allowDecimals:false,
                       			//value:new Date().getFullYear( ),
                       			listeners:{
                              	    specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER||e.getKey() == e.TAB) {
					                        me.beginQuery(field.ownerCt);
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'年',padding:'2 5 2 0'}]
                       	
                       	}, 
                       //月对象
                       {
                          xtype:'container',hidden:true,layout:'hbox',
                          items:[{
                       			xtype:'numberfield',hideTrigger:true,allowDecimals:false,
                       			//value:new Date().getMonth( )+1,
                       			minValue:1,
                       			maxValue:12,
                       			listeners:{
	                               specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER) {
					                        field.ownerCt.items.items[2].focus();
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'月 至',padding:'2 5 2 0'},{
                       			xtype:'numberfield',hideTrigger:true,allowDecimals:false,
                       			//value:new Date().getMonth( )+1,
                       			minValue:1,
                       			maxValue:12,
                       			listeners:{
                              	    specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER||e.getKey() == e.TAB) {
					                        me.beginQuery(field.ownerCt);
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'月',padding:'2 5 2 0'}]
                       },
                       //日对象
                       {
                          xtype:'container',hidden:true,layout:'hbox',
                          items:[{
                       			xtype:'numberfield',hideTrigger:true,allowDecimals:false,
                       			//value:new Date().getDate( ) ,
                       			minValue:1,
                       			maxValue:31,
                       			listeners:{
	                               specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER) {
					                        field.ownerCt.items.items[2].focus();
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'日 至',padding:'2 5 2 0'},{
                       			xtype:'numberfield',hideTrigger:true,allowDecimals:false,
                       			//value:new Date().getDate( ),
                       			minValue:1,
                       			maxValue:31,
                       			listeners:{
                              	    specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER||e.getKey() == e.TAB) {
					                        me.beginQuery(field.ownerCt);
					                    }
					                }
	                            }
                       		},{xtype:'label',text:'日',padding:'2 5 2 0'}]
                       },'->',
                       {
                            xtype:'button',
                            text:'√ 确定',
                            handler:this.beginQuery,
                            scope:this
                       }]
               });
               
               return queryField;
    },
    //代码类型对象
    codeQueryField:function(field){
    	 var me = this;
    	 var queryField;
    	 //如果有codeData说明是单层级代码
    	 if(field.codeData){
    	         //为了实现自动换行布局，采用dom方式实现
    	         //主div
                  var mainDiv = document.createElement("div");
						mainDiv.style.height="100%";
						mainDiv.style.width="100%";
						
				 //单选div，默认显示,id格式是“singleCode_”+itemid
                  var singleDiv = document.createElement("div");
                  singleDiv.style.display='block';
                  singleDiv.id = "singleCode_"+field.itemid;
                  singleDiv.setAttribute("flag","singleCode");
                  
                  //多选div，默认隐藏，id格式是“multipleCode_”+itemid
                  var multipleDiv = document.createElement("div");
                  multipleDiv.style.display='none';
                  multipleDiv.id = "multipleCode_"+field.itemid;
                  
                  //循环代码生成dom插入 单选div和多选div中
                  for(var i=0;i<field.codeData.length;i++){
                     var data = field.codeData[i];
                     //创建单选 元素 <a>标签
                     var item = document.createElement('a');
                     item.innerHTML=data.codeitemdesc;
                     item.setAttribute("realValue",data.codeitemid);
                     item.setAttribute("field",field.itemid);
                     item.style.float="left";
                     item.style.margin="3px 30px 7px 0px";
                     item.href="###";
                     //给a标签设置click监听,并绑定所属域
                     item.onclick = me.codeFieldSelect.bind(me);
                     //将a标签插入单选 div中
                     singleDiv.appendChild(item);
                     
                     //创建单选 元素 <div>标签
                     var div = document.createElement('span');
                     div.innerHTML='<input name=checkbox_'+field.itemid+' type="checkbox" value='+data.codeitemid+'>'+data.codeitemdesc;
                     div.style.float="left";
                     div.style.margin="3px 30px 7px 0px";
                     //将div标签插入单选 div中
                     multipleDiv.appendChild(div);
                     
//                     创建多选checkbox对象，name为“checkbox_”+itemid，并渲染到 多选div中
//                     Ext.widget("checkbox",{boxLabel:data.codeitemdesc,name:'checkbox_'+field.itemid,realValue:data.codeitemid,renderTo:multipleDiv,style:'float:left;padding-right:30px;'});
                  }
                  
                  //将多选和单选div 插入到 主显示div中
                  mainDiv.appendChild(singleDiv);
                  mainDiv.appendChild(multipleDiv);
                  
                  //创建查询字段对象，将div放到对象中（通过contentEl属性实现）
                  queryField = Ext.widget('container',{
                     id:'queryField_'+field.itemid,
                     layout:'border',
                     height:35,
                     style:'border:0px #c5c5c5 solid;padding:6px 2px 4px 2px;background-color:white;vertical-align:middle;border:none;border-bottom:1px #d1d1d1 dashed',
                     field:field,
                     items:[{
                        xtype:'container',region:'west',margin:'3 20 0 0',html:'<div style="text-align:right;">'+field.itemdesc+'</div>',width:144
                     },{
                     	xtype:'container',contentEl:mainDiv,region:'center',scrollable:false
                     },{
                     	xtype:'container',layout:'hbox',region:'east',
                     	items:[{
                     		xtype:'button',text:'更多',type:'less',//type为自定义属性，用于标记是展开还是收缩状态
                     		listeners:{
                     			afterrender:{
                     				fn:function(){
                     					var hh = this;
                     					var task = new Ext.util.DelayedTask(function(){
    		                     			var height = hh.ownerCt.ownerCt.items.items[1].getEl().dom.scrollHeight;
    		                     	        if(height<35){
    		                     	        	hh.setHidden(true);
    		                     	            return;
    		                     	        }
                     					});
                     					task.delay(1);
                     				}
                     			}
                     		},
                     	    handler:function(){
                     	      if(this.type=='less'){//展开更多
                     	        var height =  this.ownerCt.ownerCt.items.items[1].getEl().dom.scrollHeight;
                     	        if(height<35){
                     	            this.setDisabled(true);
                     	            return;
                     	        }
                     	        if(height>200)
                     	          this.ownerCt.ownerCt.setHeight(200);
                     	        else
                     	          this.ownerCt.ownerCt.setHeight(height+15);
                     	        this.ownerCt.ownerCt.items.items[1].setScrollable("y");
                     	        this.type='more';
                     	        this.setText('收起');
                     	      }else{//收缩起来
                     	        this.ownerCt.ownerCt.setHeight(35);
                     	        this.ownerCt.ownerCt.items.items[1].setScrollable(false);
                     	        this.type='less';
                     	        this.setText('更多');
                     	      }
                     	
                     	   }
                     	 
                     	},{xtype:'box',width:10},{
                     		xtype:'button',text:'+ 多选',
                     		handler:function(){
                     		//点击多选时隐藏  更多 和 多选 按钮
                     	      this.ownerCt.setVisible(false);
                     	
                     	       //通过找上级找到 此查询指标对象，获取itemid
                     	       var itemid = this.ownerCt.ownerCt.field.itemid;
                     	       //通过id过去 单选div 和多选div
                     	       var single = Ext.getDom("singleCode_"+itemid);
                     	       var multiple = Ext.getDom("multipleCode_"+itemid);
                     	       
                     	       //隐藏单选div，显示多选div
                     	       		single.style.display='none';
                     	       		multiple.style.display='block';
                     	       		var height =  this.ownerCt.ownerCt.items.items[1].getEl().dom.scrollHeight;
                     	       		if(height>200)
	                     	          this.ownerCt.ownerCt.setHeight(200);
	                     	        else
	                     	          this.ownerCt.ownerCt.setHeight(height+50);
                     	       		this.ownerCt.ownerCt.items.items[1].setScrollable("y");
                     	       		//显示下面定义的确定取消按钮
                     	       		this.ownerCt.ownerCt.items.items[3].setVisible(true);
                     	        
                     		}
                     	}]
                     },{
                     	xtype:'toolbar',style:'border:none',hidden:true,region:'south',
                     	items:['->',{
                     		xtype:'button',text:'全选',handler:me.selectAll,scope:me
                     	},{
                     		xtype:'button',text:'全撤',handler:me.removeAll,scope:me
                     	},{
                     		xtype:'button',text:'确定',handler:me.multipleSelect,scope:me
                     	},{
                     		xtype:'button',text:'取消',
                          	handler:function(){
                               // 显示 更多 和 多选 按钮
                               this.ownerCt.ownerCt.items.items[2].setVisible(true);
                               //充值 更多 按钮状态为收缩
                               this.ownerCt.ownerCt.items.items[2].items.items[0].type='less';
                               
                               //通过id找到单选div和多选div
                               var itemid = this.ownerCt.ownerCt.field.itemid;
                     	       var single = Ext.getDom("singleCode_"+itemid);
                     	       var multiple = Ext.getDom("multipleCode_"+itemid);
                     	       
                     	       //显示单选div，隐藏多选div
                     	       single.style.display='block';
                     	       multiple.style.display='none';
                     	       this.ownerCt.ownerCt.setHeight(35);
                     	       this.ownerCt.ownerCt.items.items[1].setScrollable(false);
                     	       
                     	       //隐藏 确定 和取消按钮
                     	       this.ownerCt.setVisible(false);
                          
                          	}
                     	},'->'
                     	]
                     }]
                  });
                }else{//多层级代码
                    queryField = Ext.widget("toolbar",{
                    	id:'queryField_'+field.itemid,
                    	field:field,
                    	height:35,
                    	style:'border:none;border-bottom:1px #d1d1d1 dashed',
                    	items:[{
                    		xtype:'label',text:field.itemdesc,width:144,style:'text-align:right;margin-right:20px'
                       },{
                            //单选树
                            xtype:'codecomboxfield',codesetid:field.codesetid,width:321,ctrltype:this.ctrltype,nmodule:this.nmodule,
                            listeners:{
                                 select:function(a,b){
                                     var values = [];
                                     values.push({id:b.get('id'),text:b.get('text')});
                                     me.highLevelCodeQuery(values,this.ownerCt.field);
                                     this.setValue(null);
                                 }
                            }
                       },{
                            //多选树
                       		xtype:'codecomboxfield',codesetid:field.codesetid,multiple:true,hidden:true,width:321,ctrltype:this.ctrltype,nmodule:this.nmodule,
                       		listeners:{
                       		    multiplefinish:function(values){
                       		    	this.collapse();
                       		        me.highLevelCodeQuery(values,this.ownerCt.field);
                       		        this.setValue(null);
                       		    }
                       		}
                            
                       },'->',{
                            xtype:'checkbox',
                            boxLabel:'多选',
                            margin:'0 8 0 0',
                            listeners:{
                                change:function(c, newValue){
                                    if(newValue){//当newValue为true时，说明是多选,隐藏单选树，显示多选树，并展开多选树
                                          this.ownerCt.items.items[1].setVisible(false);
                                          this.ownerCt.items.items[2].setVisible(true);
                                          this.ownerCt.items.items[2].expand();
                                    }else{//当newValue为false时，说明是单选,隐藏多选树，显示单选树
                                    	  this.ownerCt.items.items[1].setVisible(true);
                                          this.ownerCt.items.items[2].setVisible(false);
                                    }
                                }
                            }
                       }]
                    });
                
                
                
                }
                
                
                return queryField;
    },
    
    //字符型和文本型 字段对象
    textQueryField:function(field){
        var me = this;
    	var queryField = Ext.widget('toolbar',{
          	           id:'queryField_'+field.itemid,
          	           field:field,
          	           height:35,
          	           style:'border:none;border-bottom:1px #d1d1d1 dashed',
                       items:[{
                           xtype:'label',text:field.itemdesc,width:144,style:'text-align:right;margin-right:20px;'
                       },{
                            xtype:'textfield',
                            width:321,
                            listeners:{
                              	    specialkey: function(field, e){
					                    if (e.getKey() == e.ENTER||e.getKey() == e.TAB) {
					                        me.beginQuery(field);
					                    }
					                }
	                        }
                       },'->',{
                            xtype:'button',
                            text:'√ 确定',
                            handler:this.beginQuery,
                            scope:this
                       }]
               });
        return queryField;
    },
    
    //数值型、日期型、字符型 点确定后收集数据，并在筛选区域添加 条件
    beginQuery:function(childComp){
         var fieldComp = childComp.ownerCt;
         var field = fieldComp.field;
         var text;
         if(field.itemtype=='N'){
               var begin = fieldComp.items.items[1].getValue();
               var end = fieldComp.items.items[3].getValue();
               begin= begin==null?'*':begin;
               end= end==null?'*':end;
               
               if((begin+'')=='*' && (end+'')=='*')
                   return;
               if(Ext.isNumber(begin+end) && end<begin){
                   Ext.Msg.alert("提示信息","结束值不能小于开始值！");
                   return;
               }
               if("*"==begin){
            	   text=end+"以下";
               }else if("*"==end){
            	   text=begin+"以上";
               }else{
            	   text = begin+"~"+end;
               }
               field.value = begin+"~"+end;
               fieldComp.items.items[1].setValue(null);
               fieldComp.items.items[3].setValue(null);
         }else if(field.itemtype=='D'){
               field.type=fieldComp.dateType;
               if(fieldComp.dateType=='area'){
                   var begin = fieldComp.items.items[1].items.items[0].getValue();
              	   var end = fieldComp.items.items[1].items.items[2].getValue();
              	   begin= begin==(null||"")?'*':begin;
              	   end= end==(null||"")?'*':end;
              	   begin = begin.replace(/\./g, "-");
              	  
	               if((begin+'')=='*' && (end+'')=='*')
	                   return;
	               var startDate = Ext.Date.parse(begin,field.dateFormat),
	                    endDate = Ext.Date.parse(end,field.dateFormat);
	               if(startDate && endDate && endDate<startDate){
	                  Ext.Msg.alert("提示信息","结束日期不能小于开始日期！");
                      return;
	               }
//	               var begins = begin.split("-",3);
//	               if(undefined==begins[2]&&begin!="*"){
//	            	   begin+="-01";
//	               }
//	               var ends = end.split("-",3);
//	               if(undefined==ends[2]&&end!="*"){
//	            	   end+="-31";
//	               }
	               if("*"==begin){
	            	   text=end+"以前";
	               }else if("*"==end){
	            	   text=begin+"以后";
	               }else{
	            	   text = begin+"~"+end;
	               }
              	   field.value = begin+"~"+end;
                   fieldComp.items.items[1].items.items[0].setValue(null);
                   fieldComp.items.items[1].items.items[2].setValue(null);
               }else if(fieldComp.dateType=='year'){
               	   var begin = fieldComp.items.items[2].items.items[0].getValue();
              	   var end = fieldComp.items.items[2].items.items[2].getValue();
              	   begin= begin==null?'*':begin;
              	   end= end==null?'*':end;
	               if((begin+'')=='*' && (end+'')=='*')
	                   return;
	               if(Ext.isNumber(begin+end) && end<begin){
	                   Ext.Msg.alert("提示信息","结束值不能小于开始值！");
	                   return;
	               }
                   field.value = begin+"~"+end;
                   if("*"==begin){
	            	   text=end+"年以前";
	               }else if("*"==end){
	            	   text=begin+"年以后";
	               }else{
	            	   text = begin+"年~"+end+"年";
	               }
//                   text = field.value+"年";
                   fieldComp.items.items[2].items.items[0].setValue(null);
                   fieldComp.items.items[2].items.items[2].setValue(null);
               }else if(fieldComp.dateType=='month'){
                   var begin = fieldComp.items.items[3].items.items[0].getValue();
                   var end = fieldComp.items.items[3].items.items[2].getValue();
                   begin= begin==null?'*':begin;
              	   end= end==null?'*':end;
	               if((begin+'')=='*' && (end+'')=='*')
	                   return;
	               if((Ext.isNumber(begin) && begin<1) || (Ext.isNumber(end) && end<1)){
	            	   Ext.Msg.alert("提示信息","月份最小为1月！");
	            	   return;
	               }
	               if((Ext.isNumber(begin) && begin>12) || (Ext.isNumber(end) && end>12)){
	               	   Ext.Msg.alert("提示信息","月份最大为12月！");
	                   return;
	               }
	                   
	               if(Ext.isNumber(begin+end) && end<begin){
	                   Ext.Msg.alert("提示信息","结束值不能小于开始值！");
	                   return;
	               }
                   field.value = begin+"~"+end;
                   if("*"==begin){
	            	   text=end+"月以前";
	               }else if("*"==end){
	            	   text=begin+"月以后";
	               }else{
	            	   text = begin+"月 ~"+end+"月";
	               }
//                   text = field.value+"月";
                   fieldComp.items.items[3].items.items[0].setValue(null);
                   fieldComp.items.items[3].items.items[2].setValue(null);
               }else if(fieldComp.dateType=='day'){
               	   var begin = fieldComp.items.items[4].items.items[0].getValue();
                   var end = fieldComp.items.items[4].items.items[2].getValue();
                   begin= begin==null?'*':begin;
              	   end= end==null?'*':end;
	               if((begin+'')=='*' && (end+'')=='*')
	                   return;
	               if((Ext.isNumber(begin) && begin<1) || (Ext.isNumber(end) && end<1)){
	            	   Ext.Msg.alert("提示信息","日期最小为1号！");
	            	   return;
	               }
	               if((Ext.isNumber(begin) && begin>31) || (Ext.isNumber(end) && end>31)){
	               	   Ext.Msg.alert("提示信息","日期最大为31号！");
	                   return;
	               }
	                   
	               if(Ext.isNumber(begin+end) && end<begin){
	                   Ext.Msg.alert("提示信息","结束值不能小于开始值！");
	                   return;
	               }
                   field.value = begin+"~"+end;
                   if("*"==begin){
	            	   text=end+"号以前";
	               }else if("*"==end){
	            	   text=begin+"号以后";
	               }else{
	            	   text = begin+"日 ~"+end+"日";
	               }
//                   text = field.value+"号";
                   fieldComp.items.items[4].items.items[0].setValue(null);
                   fieldComp.items.items[4].items.items[2].setValue(null);
               }
               
         }else{
               var value = fieldComp.items.items[1].getValue();
               if(!value || Ext.String.trim(value).length<1)
                     return;
               field.value = value;
               text = field.value;
               fieldComp.items.items[1].setValue('');
         }
         
         //将筛选条件添加到 筛选区域中
         this.addFilterItem(field,text);
         
         fieldComp.setVisible(false);
         
    },
    
     //单层级代码 单选 处理
    codeFieldSelect:function(e){
    	var itemEle;
    	var eventMyself = e?e:event;
    	if(eventMyself.target)
               itemEle = eventMyself.target;
        else 
               itemEle = eventMyself.srcElement;
    	var itemid = itemEle.getAttribute("field");
    	var codeid = itemEle.getAttribute("realValue");
    	var codedesc = itemEle.innerHTML;
    	
    	var fieldComp = Ext.getCmp("queryField_"+itemid);
    	var field = fieldComp.field;
    	field.value = codeid;
    	
    	
    	//将筛选条件添加到 筛选区域中
         this.addFilterItem(field,codedesc);
         
         fieldComp.setVisible(false);
    },
    //单层代码全选
    selectAll:function(button){
    	var field = button.ownerCt.ownerCt.field;
        var checkboxs = document.getElementsByName("checkbox_"+field.itemid);
        var valuestr = '';
        var valuedescstr = '';
        for(var i=0;i<checkboxs.length;i++){
  		  checkboxs[i].checked=true;
        }
    	
    },
  //单层代码全选
    removeAll:function(button){
    	var field = button.ownerCt.ownerCt.field;
        var checkboxs = document.getElementsByName("checkbox_"+field.itemid);
        var valuestr = '';
        var valuedescstr = '';
        for(var i=0;i<checkboxs.length;i++){
  		  checkboxs[i].checked=false;
        }
    	
    },
    
    //单层级代码 多选 数据收集处理
    multipleSelect:function(button){
          var field = button.ownerCt.ownerCt.field;
          var checkboxs = document.getElementsByName("checkbox_"+field.itemid);
          var valuestr = '';
          var valuedescstr = '';
          for(var i=0;i<checkboxs.length;i++){
        	  if(checkboxs[i].checked){
        		  checkboxs[i].checked=false;
        		  valuestr+=checkboxs[i].value+",";
        		  valuedescstr+=checkboxs[i].nextSibling.nodeValue+","; 
        	  }
        	  
//          var checkboxs = Ext.ComponentQuery.query("checkbox[name=checkbox_"+field.itemid+"]");
//             if(checkboxs[i].getValue()){
//                 checkboxs[i].setValue(false);
//                 valuestr+=checkboxs[i].realValue+",";
//                 valuedescstr+=checkboxs[i].boxLabel+",";
//             }
          
          }
        field.value = valuestr;
        if(valuedescstr!=""){
        	valuedescstr = valuedescstr.substring(0, valuedescstr.length-1);
        //将筛选条件添加到 筛选区域中
         this.addFilterItem(field,valuedescstr);
        }
         
         //重置为单选状态
         var queryField = Ext.getCmp("queryField_"+field.itemid);
         queryField.items.items[2].setVisible(true);
                               queryField.items.items[2].items.items[0].type='less';
                     	       var single = Ext.getDom("singleCode_"+field.itemid);
                     	       var multiple = Ext.getDom("multipleCode_"+field.itemid);
                     	       single.style.display='block';
                     	       multiple.style.display='none';
                     	       queryField.setHeight(35);
                     	       queryField.items.items[3].setVisible(false);
                     	       queryField.items.items[1].setScrollable(false);
      if(valuedescstr!=""){
    	  queryField.setVisible(false);
         }
    },
    
    //多级代码 收集数据 
    highLevelCodeQuery:function(values,field){
        var valuestr = '';
        var valuedescstr = '';
        for(var i=0;i<values.length;i++){
              valuestr+=values[i].id+",";
              valuedescstr+=values[i].text+",";
        }
        field.value = valuestr;
        if(valuedescstr!=""){
        	valuedescstr = valuedescstr.substring(0, valuedescstr.length-1);
        //将筛选条件添加到 筛选区域中
         this.addFilterItem(field,valuedescstr);
         Ext.getCmp("queryField_"+field.itemid).setVisible(false);
        }
    },
    
    //日期类型选择 鼠标滑过样式 
    mouseOverColor:function(){
    	arguments[1].style.backgroundColor='gray';
    },
    
    //日期类型选择 鼠标滑过样式 
    mouseOutColor:function(){
    	arguments[1].style.backgroundColor='white';
    },
    
    //日期类型选择后页面变动处理
    dateTypeSelected:function(){
          this.dateTypeSelectBox.hide();
          var dateType = arguments[1].id;
          var dateField = Ext.getCmp(this.selectedDateFieldId);
          
          if(dateType == 'dateTypeArea'){
               dateField.dateType='area';
               dateField.items.items[0].items.items[0].setText(dateField.field.itemdesc);
               dateField.items.items[1].setVisible(true);
               dateField.items.items[2].setVisible(false);
               dateField.items.items[3].setVisible(false);
               dateField.items.items[4].setVisible(false);
          }else if(dateType == 'dateTypeYear'){
               dateField.dateType='year';
               dateField.items.items[0].items.items[0].setText("年限("+dateField.field.itemdesc+")");
               dateField.items.items[1].setVisible(false);
               dateField.items.items[2].setVisible(true);
               dateField.items.items[3].setVisible(false);
               dateField.items.items[4].setVisible(false);
          }else if(dateType == 'dateTypeMonth'){
          	   dateField.dateType='month';
          	   dateField.items.items[0].items.items[0].setText("月("+dateField.field.itemdesc+")");
               dateField.items.items[1].setVisible(false);
               dateField.items.items[2].setVisible(false);
               dateField.items.items[3].setVisible(true);
               dateField.items.items[4].setVisible(false);
          }else{
               dateField.dateType='day';
               dateField.items.items[0].items.items[0].setText("日("+dateField.field.itemdesc+")");
               dateField.items.items[1].setVisible(false);
               dateField.items.items[2].setVisible(false);
               dateField.items.items[3].setVisible(false);
               dateField.items.items[4].setVisible(true);
          }
    },
    
    addFilterItem:function(field,text){
         var me = this,
              filterDiv = this.filtersArea.items.items[1].contentEl;
              
		Ext.util.CSS.createStyleSheet(".divcss:hover{background-color:#e4393c;color:#FFFFFF!important}");
        var mainDiv = document.createElement('div');
        var textSpan = document.createElement('span');
        textSpan.style.whiteSpace='nowrap'; 
        textSpan.style.textOverflow='ellipsis';
        textSpan.style.overflow='hidden';
        textSpan.title=field.itemdesc+"："+text;
        textSpan.innerHTML=field.itemdesc+"："+text;
        textSpan.style.float='left';
        textSpan.style.padding='2px';
        textSpan.style.maxWidth='300px';
        mainDiv.appendChild(textSpan);
//        var imgEl = document.createElement('img');
//        imgEl.src = rootPath+"/components/querybox/images/hongcha.png";
//        imgEl.style.float='left';
//        imgEl.style.cursor = 'pointer';
//        imgEl.width=20;imgEl.height=20;
//        imgEl.setAttribute('itemid',field.itemid);      
        var imgEl = document.createElement('div');
        imgEl.innerHTML='x';
        imgEl.setAttribute('class','divcss');
        imgEl.setAttribute('style','cursor:pointer;float:left;width:20px;height:20px;line-height:18px;text-align:center;font-size:15px;color:#e4393c');      
        imgEl.setAttribute('itemid',field.itemid);
        imgEl.onmouseover=(function(){
        	Ext.getCmp("filterItem_"+field.itemid).setStyle("border-color","#e4393c");
        });
        imgEl.onmouseout=(function(){
        	Ext.getCmp("filterItem_"+field.itemid).setStyle("border-color","#c5c5c5");
        });
        imgEl.onclick = (function(e){
            var itemid;
            var event = window.event||e;
            if(event.target)
               itemid = event.target.getAttribute('itemid');
            else 
               itemid = event.srcElement.getAttribute('itemid');
            var box = this.filterItemStore[itemid];
            Ext.destroy(box);
            delete this.filterItemStore[itemid];
            Ext.getCmp("queryField_"+itemid).setVisible(true);
            this.resetFilterAreaSize();
            this.executeDoQuery();
        }).bind(me);
        mainDiv.appendChild(imgEl);
        
    	//将筛选条件添加到 筛选区域中
        var box = Ext.widget('box',{
         	style:'float:left;border:1px #c5c5c5 solid;height:auto;width:auto',
         	field:field,
         	height:22,
         	margin:'0 5px 5px 0px',
         	id:'filterItem_'+field.itemid,
         	renderTo:filterDiv,
         	contentEl:mainDiv
         });
        
         this.filterItemStore[field.itemid] = box;
         this.resetFilterAreaSize();
         //收集 this.filtersArea 中 插入的 筛选条件对象中的 field 属性,执行查询
         this.executeDoQuery();
    },
    resetFilterAreaSize:function(height){
       if(!height)
          height = this.filtersArea.items.items[1].getEl().dom.scrollHeight+(Ext.isIE?0:2);
        
    	this.filtersArea.setHeight(height<34?34:height);
    },
    executeDoQuery:function(){
          var items = [];
          var displayItem = new Array();
          
		  for(var n=0;n<this.queryFieldsArea.items.length;n++){
			  displayItem.push(this.queryFieldsArea.items.getAt(n));
		  }
		  
          for(var key in this.filterItemStore){
        	  items.push(this.filterItemStore[key].field);
        	  for(var n=0;n<this.queryFieldsArea.items.length;n++){
            	  if(this.queryFieldsArea.items.getAt(n).id=="queryField_"+key&&Ext.Array.contains(displayItem,this.queryFieldsArea.items.getAt(n))){
            		  Ext.Array.remove(displayItem,this.queryFieldsArea.items.getAt(n));
            	  }
              }
          }
          
//          for(var n=0;n<displayItem.length;n++){
//        	  if(n%2==0){
//        		  displayItem[n].setStyle({backgroundColor: '#f9f9f9'});
//                 }else{
//                	 displayItem[n].setStyle({backgroundColor: 'white'});
//                 }
//          }
           //收集数据，执行查询
           Ext.callback(this.doQuery,this.scope,[items]);
    },
    
    //销毁组件时将内置对象一起销毁
    beforeDestroy:function(){
        Ext.destroy(this.filtersArea,this.queryFieldsArea,this.dateTypeSelectBox);
        this.callParent(arguments);
    },
    
    //设置可选择条件选项
    setFields:function(data){
         this.queryFields = data;
         this.fillQueryFields(data);
    }

});