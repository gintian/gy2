/**
 * 
 * 查看计划制定
 * 
 */
 Ext.define('YearPlan.YearPlan',{
       funcpriv:undefined,
       viewport:undefined,
       container:undefined,
       yearlist:[],
       columns:undefined,
       constructor:function(config){
         Ext.apply(this,config);
         yearPlan_me=this;
         this.createMainPanel();
       },createMainPanel:function(year){
       	var me=this;
       	var map=new HashMap();
       	if(year!=null&&year!=''){
            map.put("year",year);        		
       	}
       	   Rpc({functionId:'WP00002001',async:false,success:me.getConfig,scope:me},map);
      },getConfig:function(res){
             var me = this;
            var param = Ext.decode(res.responseText);
            var conditions=param.tableconfig;
            var obj = Ext.decode(conditions);
            obj.beforeBuildComp=function(config){
                config.tableConfig.viewConfig.plugins={
                    ptype: 'gridviewdragdrop',
                    dragText: '拖拽完成排序'
                };
                config.tableConfig.viewConfig.listeners={
                        drop: yearPlan_me.drop
                };
                config.tableConfig.sortableColumns=false;
            };
           // me.container.remove(me.mainPanel);
            me.yearlist=param.yearlist;
            me.tableObj = new BuildTableObj(obj);
               var menu= Ext.create('Ext.menu.Menu', {
                    width: 50,
                    plain: true,
                    items:[]
                });
              for(var i=0;i<me.yearlist.length;i++){
              	var str=me.yearlist[i];
                   menu.add({text:me.yearlist[i]+'年',width:30,handler:function(item){
                   	 var map=new HashMap();
                   	 map.put("year",item.text.substring(0,4));
                     Rpc({functionId:'WP00002003',async:false,success:function(){
                           Ext.getCmp('year').setText(item.text);
                     	   Ext.getCmp('year').name=item.text;
                           me.tableObj.tablePanel.getStore().reload();
                     },scope:me},map);   
                   }});
              }  
                
            me.tableObj.toolBar.insert(0,{xtype:'button',id:'year',name:param.year+'年',text:param.year+'年',menu:menu});
            yearPlan_me.columns = me.tableObj.tablePanel.columns;
       },drop:function(node,data,model,dropPosition,dropHandlers){
       	  var me=this;
          var panel= yearPlan_me.tableObj.tablePanel;
          if(data.records.length>1){
            Ext.Msg.alert("提示信息","禁止多行拖动！")
            panel.getStore().load();
          }else{
            var orp_id=data.records[0].get('p1700')+'';
            var orp_41=data.records[0].get('p1741')+'';
            var toP_id=model.get("p1700")+'';
            var toP_41=model.get("p1741")+'';
            var map=new HashMap();
            map.put("orP_id",orp_id);
            map.put("orP_41",orp_41);
            map.put("toP_id",toP_id);
            map.put("toP_41",toP_41);
            map.put("dropPosition",dropPosition);
            map.put("operaflag","move");//操作标志
            map.put("year",Ext.getCmp('year').name.substring(0,4));
             Rpc({functionId:'WP00002001',async:false,success:function(res){
                                   var res= Ext.decode(res.responseText);
                                         if(!res.succeed){
                                             //   YearplanGlobal.tableObj.tablePanel.store.remove(selections);//前台移除表格控件内需要移除的数据
                                                Ext.Msg.alert("提示信息","移动失败！");
                                                YearplanGlobal.tableObj.tablePanel.getStore().reload();
                                         }else{
                                               /* var datalist=res.datalist;//移动成功，更新界面seq
                                                var store= panel.getStore();
                                                var records = store.data.items;
                                                for(var i=0;i<datalist.length;i++){
                                             	var record=store.findRecord('p1700',datalist[i].p1700);
                                                	console.log(datalist[i].p1741);
                                                	record.data.p1741=datalist[i].p1741;
                                                	console.log(record);
                                                	
                                                	//store.findRecord('p1700',data[i].p1700).data.p1741=data[i].p1741;//('p1741',data[i].p1741);
//                                                    store.findRecord('p1700',data[i].p1700).data.p1741 = data[i].p1741;
//                                                    store.findRecord('p1700',datalist[i].p1700).set('p1741',datalist[i].p1741);
                                                }*/
                                                    YearplanGlobal.tableObj.tablePanel.getStore().load();
                                             }
                               },scope:me},map);
            
          
          }
       },
       createYearPlan:function(type,id,flag){//跳转到新建页面
       	var year=Ext.getCmp('year').name.substring(0,4);
       	//me.container.remove(me.mainPanel);
       	var editflag=false; //false 为可读 可编辑  true为 不可编辑 只可读
       	if(flag=='0'){
       	    editflag=false;
       	}else if(flag=='1'){
       	    editflag=true
       	}
       	YearplanGlobal.tableObj.getMainPanel().removeAll(false);
//       	YearplanGlobal.tableObj.mainPanel.hide();
            Ext.require('YearPlan.CreateYearPlan',function(){
            	if(!CreatePlan){
                  CreatePlan=Ext.create("YearPlan.CreateYearPlan",{
                  	depredutyUion:[],
                  	depreleadUion:[],
                  	depreleader:[],
                  	depreAppor:[],
                  	deperResponse:[],//责任人
                    deperAuditor:[],//审核人
                    year:year,
                    orgid:'',
                    editflag:editflag,
                    p1700:(type=='edit'||type=='taskAssign')?id:'',
                    typeflag:(type=='edit'||type=='taskAssign')?type:''//添加是否是创建进入还是编辑进入新建页面
                  });
            	}else{
            	   CreatePlan.destroy();//返回再次进入新建界面首先销毁之前的新建界面
            	   CreatePlan=Ext.create("YearPlan.CreateYearPlan",{
            	   	depredutyUion:[],
                    depreleadUion:[],
                    depreleader:[],
                    depreAppor:[],
                    deperResponse:[],//责任人
                    deperAuditor:[],//审核人
                    year:year,
                    orgid:'',
                    editflag:editflag,
                    p1700:(type=='edit'||type=='taskAssign')?id:'',
                    typeflag:(type=='edit'||type=='taskAssign')?type:''
                  });
            	}
            });
       },delYearPlan:function(){//删除计划制定
       	       var me=this;
               var selections= YearplanGlobal.tableObj.tablePanel.getSelectionModel().getSelection();  //record
               var list=[];
               var map=new HashMap();
               if(selections.length>0){
               	var flag=true;
               	    for(var i=0;i<selections.length;i++){
                                  var id= selections[i].data.p1700;//唯一性id        	    
                                  var p1743=selections[i].data.p1743;
                                  if(p1743=="起草中"||p1743=="已暂停"){//可删除的计划状态  01起草 09 暂停
                                     list.push(id);
                                  }else if(p1743=='已发布'||p1743=='执行中'||p1743=='已完成'){//04发布 //05 执行中 06完成
                                    flag=false;
                                  }
               	    }
               	 if(flag){
               	 	map.put("operaflag","delete");   
                    map.put("list",list);
                    Ext.showConfirm("确认删除？",function(btn){
                        if(btn=='yes'){
                             Rpc({functionId:'WP00002001',async:false,success:function(res){
                                   var res= Ext.decode(res.responseText);
                                         if(res.flag){
                                                YearplanGlobal.tableObj.tablePanel.store.remove(selections);//前台移除表格控件内需要移除的数据
                                                Ext.Msg.alert("提示信息","删除成功！");
                                         }else{
                                            Ext.Msg.alert("提示信息","删除失败！");
                                            YearplanGlobal.tableObj.tablePanel.getStore().reload();
                                         }
                               },scope:me},map);
                        }
                    });
                   
               	 }else{
               	    Ext.Msg.alert("提示信息","只可删除起草或暂停的任务！");
               	 } 
               	 
               }else{
                 Ext.Msg.alert("提示信息","请选择要删除的任务！");
               }
       },releaseYearPlan:function(){//发布计划
              var me=this;
               var selections= YearplanGlobal.tableObj.tablePanel.getSelectionModel().getSelection();
               var list=[];
               var map=new HashMap();
               if(selections.length>0){
                    var flag=true;
                    for(var i=0;i<selections.length;i++){
                                  var id= selections[i].data.p1700;//唯一性id 
                                  var p1743=selections[i].data.p1743;
                                  if(p1743=='起草中'||p1743=='已暂停'){
                                         list.push(id);
                                  }else if(p1743=='已发布'||p1743=='执行中'||p1743=='已完成'){
                                        flag=false;
                                  }
                    }
                    if(flag){
                    	//haosl add 20170309 发布前判断是否进行过任务指派
                    	 map.put("operaflag","hasPlanAssigned");
                         map.put("list",list);
                         Rpc({functionId:'WP00002001',async:false,success:function(res){
                        	 var res= Ext.decode(res.responseText);
                        	 if(res.ids.length>0){
                        		 Ext.showConfirm("是否将已进行过  任务指派  的任务状态置为“执行中”？",function(flag){
                        			 map.put("operaflag","release");
                        			 if(flag=="yes"){
                        				 map.put("assignIds",res.ids);
                        				 map.put("isAssign",true);
                        			 }else{
                        				 map.put("isAssign",false);
                        			 }
                        			 Rpc({functionId:'WP00002001',async:false,success:function(res){
                                         
                                         var res= Ext.decode(res.responseText);
                                               if(res.flag){
                                                      Ext.Msg.alert("提示信息","任务发布成功！");
                                               }else{
                                                  Ext.Msg.alert("提示信息","任务发布失败！");
                                               }
                                           YearplanGlobal.tableObj.tablePanel.getStore().reload();
                               
                                     },scope:me},map);
                        		 });
                        	 }else{
                            	map.put("operaflag","release"); 
                                map.put("isAssign",false);//是否对已经指派的任务，置为“执行中状态”。
                                Rpc({functionId:'WP00002001',async:false,success:function(res){
                                    
                                    var res= Ext.decode(res.responseText);
                                          if(res.flag){
                                                 Ext.Msg.alert("提示信息","任务发布成功！");
                                          }else{
                                             Ext.Msg.alert("提示信息","任务发布失败！");
                                          }
                                      YearplanGlobal.tableObj.tablePanel.getStore().reload();
                          
                                },scope:me},map);
                        	 }
                         },scope:me},map);
                         //haosl end 
                        
                    }else{
                              Ext.Msg.alert("提示信息","只可发布起草或暂停的任务");
                    }
               }else{
                 Ext.Msg.alert("提示信息","请选择要发布的任务！");
               }
       },stopYearPlan:function(){//暂停计划
               var me=this;
               var selections= YearplanGlobal.tableObj.tablePanel.getSelectionModel().getSelection();
               var list=[];
               var map=new HashMap();
               var flag=true;
               if(selections.length>0){
                    for(var i=0;i<selections.length;i++){
                                  var id= selections[i].data.p1700;//唯一性id              
                                  var p1743=selections[i].data.p1743;
                                  if(p1743=='已发布'||p1743=='执行中'){
                                     list.push(id);
                                  }else if(p1743=='起草中'||p1743=='已完成'||p1743=='已暂停'){
                                  	 flag=false;
                                  } 
                    }
                      if(flag){
                            map.put("operaflag","stop");   
                             map.put("list",list);
                            Rpc({functionId:'WP00002001',async:false,success:function(res){
                                   var res= Ext.decode(res.responseText);
                                         if(res.flag){
                                               // YearplanGlobal.tableObj.tablePanel.store.remove(selections);//前台移除表格控件内需要移除的数据
                                                Ext.Msg.alert("提示信息","任务暂停成功！");
                                         }else{
                                            Ext.Msg.alert("提示信息","任务暂停失败！");
                                         }
                                        YearplanGlobal.tableObj.tablePanel.getStore().reload();
                            	
                                },scope:me},map);
                      }else{
                             Ext.Msg.alert("提示信息","只可暂停发布状态的任务");                      
                      }  
                 
               }else{
                 Ext.Msg.alert("提示信息","请选择要暂停的任务！");
               }
       },doneYearPlan:function(){//完成
       	      var me=this;
               var selections= YearplanGlobal.tableObj.tablePanel.getSelectionModel().getSelection();
               var list=[];
               var map=new HashMap();
               var flag=true;
               if(selections.length>0){
                    for(var i=0;i<selections.length;i++){
                                  var id= selections[i].data.p1700;//唯一性id              
                                  var p1743=selections[i].data.p1743;
                                  if(p1743=='执行中'){
                                     list.push(id);
                                  }else if(p1743=='起草中'||p1743=='已发布'||p1743=='已完成'||p1743=='已暂停'){
                                     flag=false;
                                  } 
                    }
                      if(flag){
                            map.put("operaflag","done");   
                             map.put("list",list);
                            Rpc({functionId:'WP00002001',async:false,success:function(res){
                                   var res= Ext.decode(res.responseText);
                                         if(res.flag){
                                               // YearplanGlobal.tableObj.tablePanel.store.remove(selections);//前台移除表格控件内需要移除的数据
                                                Ext.Msg.alert("提示信息","任务完成成功！");
                                         }else{
                                            Ext.Msg.alert("提示信息","任务完成失败！");
                                         }
                                        YearplanGlobal.tableObj.tablePanel.getStore().reload();
                                
                                },scope:me},map);
                      }else{
                             Ext.Msg.alert("提示信息","只可修改执行中的任务");                      
                      }  
                 
               }else{
                 Ext.Msg.alert("提示信息","请选择要完成的任务！");
               }
       },taskAssignment:function(){//任务指派
       	      var selections= YearplanGlobal.tableObj.tablePanel.getSelectionModel().getSelection();
       	      var id;
       	      if(selections.length>1){
       	      	     Ext.Msg.alert("提示信息","只能勾选一个任务进行任务指派！");
       	      }else if(selections.length==1){	     
       	      	 id=selections[0].data.p1700+'';
       	      	   var p1743=selections[0].data.p1743;
       	      	   if(p1743=='已发布'||p1743=='执行中')
                        YearplanGlobal.createYearPlan("taskAssign",id);
                   else
                    Ext.Msg.alert("提示信息","当前任务未发布不允许指派！");
       	      }else{
       	          Ext.Msg.alert("提示信息","请选择要指派的任务！");
       	      }
       },editPlan:function(p1705,cell,record){//编辑
       	       // if(record.data.p1743!='完成')//完成状态的任务只可浏览不可修改
       	        var flag=0;
       	        if(record.data.p1743=='已完成'||record.data.p1743=='已发布'||record.data.p1743=='执行中')
       	            flag=1;
       	        var html="<a href='javascript:YearplanGlobal.createYearPlan(\"edit\",\""+record.data.p1700+"\",\""+flag+"\")';>"+p1705+"</a>";
                return html;
       },sawCompletion:function(rowname,cell,record,rowIndex,colIndex){//查看各季度完成情况
           var colId = cell.column.dataIndex;
            var quater = "";
            if(colId=="p1709")
                quater = "1";
            else if(colId=="p1711")
                quater = "2";
            else if(colId=="p1713")
                quater = "3";
            else if(colId=="p1715")
                quater = "4";
   	        var html="";
       	    var startTime=record.data.p1745;
            var endTime=record.data.p1747;
            if(!startTime){
                if(!endTime){
                    
                }else{
                    endTime = endTime.replace(/\-/g,'/');
                    var endMoth = new Date(endTime).getMonth()+1;//1-12
                    var endQ =  Math.ceil(endMoth/3);
                    if(quater>endQ)//不需要填写的季度
                        return "--";
                }
            }else{
                startTime = startTime.replace(/\-/g,'/');
                var startMoth = new Date(startTime).getMonth()+1//1-12;
                var startQ = Math.ceil(startMoth/3);
                if(!endTime){
                    if(quater<startQ)//不需要填写的季度
                        return "--";
                }else {
                    endTime = endTime.replace(/\-/g,'/');
                    var endMoth = new Date(endTime).getMonth()+1;//1-12
                    var endQ =  Math.ceil(endMoth/3);
                    if(quater<startQ || quater>endQ)//不需要填写的季度
                        return "--";
                }
            }
   	        if(record.data[colId]!=null&&record.data[colId]!="")
   	        {   
   	        	if(record.data[colId+"state"]=="03"){
   	        		html="<a href='javascript:YearplanGlobal.showCompletion(\""+record.data.p1700+"\",\""+colId+"\")';>查看</a>";
   	        	}else{
   	        		html="汇报中";
   	        	}
   	        }else
                 html="未填";
   	        return html;
       },showCompletion:function(id,column){//展现左侧查看某季度完成情况
       	  var me=this;
       	  var items=YearplanGlobal.tableObj.tablePanel.store.getData().items;
       	  var p17={};
       	  for(var i=0;i<items.length;i++){
       	        if(items[i].data.p1700==id){
       	            p17=items[i].data;
       	        }
       	  }
       	var html=p17[column].replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/' '/g,'&nbsp;');  
       	var processHtml='';
       	processHtml+='<span>责任单位:<font color="#85A6DB">'+((!p17.p1717)?'无':p17.p1717)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
       	processHtml+='<span>牵头单位:<font color="#85A6DB">'+((!p17.p1719)?'无':p17.p1719)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
        processHtml+='<span>公司领导:<font color="#85A6DB">'+((!p17.p1721)?'无':p17.p1721)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
        processHtml+='<span>审批人:<font color="#85A6DB">'+((!p17.p1720)?'无':p17.p1720)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
        processHtml+='<span>审核人:<font color="#85A6DB">'+((!p17.p1723)?'无':p17.p1723)+'</font>&nbsp;&nbsp;&nbsp;&nbsp;</span>';
        processHtml+='<span>责任人:<font color="#85A6DB">'+((!p17.p1731)?'无':p17.p1731)+'</font></span>';  
       	  
        
        var title;
        if(column=='p1709'){
           title='一季度完成情况';
        }else if(column=='p1711'){
            title='二季度完成情况';
        }else if(column=='p1713'){
            title='三季度完成情况';
        }else if(column=='p1715'){
            title='四季度完成情况';
        }
       	 var mainpanel=Ext.widget('panel',{
       	    	title:p17.p1705+"("+title+")",
       	        layout:{
       	            type:'vbox',
       	            align:'center'
       	        },
       	        x:document.body.clientWidth * 0.5,
       	        id:'mainPanelId',
       	        y:0,
                width : "50%",
                floating : true,
                height:document.body.clientHeight,
                closeAction:'destroy',
                items:[{
                            id:'ckeditorid',
                            border:0,
                            width:'100%',
                            padding:'5 0 5 10',
                            autoScroll:true,
                            hidden:false,
                            html:html,
                            flex:4,
                         },{
                             xtype:'panel',
                             id:'processPanel',
                             scrollable:true,
                             collapsible:false,
                             collapsed:true,
                             width:'100%',
                             minHeight:75,
                             maxHeight:120,
                             title:'<label id="processTitle">任务责任信息</label><img id="processImg" style="cursor:pointer;position:relative;top:2px;margin-left:5px;" src="/images/new_module/expand.png" onclick="yearPlan_me.clickTitleIcon(\'processPanel\')"/>',
                             html:'<div style="margin:8px 10px 0px 10px;">'+processHtml+'</div>',
                             listeners:{
                                     'beforeexpand' : function(o) {
                                         yearPlan_me.setIcon('processImg','up');// 收起按钮
                                     },
                                     'beforecollapse' : function(o) {
                                         yearPlan_me.setIcon('processImg','down');// 展开按钮
                                     }
                                 }
                         }
                       
                ],tools:[
                            {
                                type:'close',
                                handler:function(){
                                	this.up('panel').close();
                                }
                            }
                        ],
       	    }).show(); 
       	  Ext.on('resize', function (width, height)
              {
              	 var mainpanel=Ext.getCmp('mainPanelId');
              	if(mainpanel){
                    mainpanel.setHeight(height);
                    mainpanel.setWidth(width*0.5);
                    mainpanel.setX(width*0.5);
                    Ext.getCmp('ckeditorid').setHeight(height*0.8);
              	}
              });  
       	Ext.getDoc().on('mousedown',me.clickPanel,me);    
       	    
       },clickTitleIcon:function(id){
             var panel = Ext.getCmp(id);
        panel.toggleCollapse();
       },setIcon : function(imgId,state) {
        var img = Ext.getDom(imgId);
        var src = '/images/new_module/expand.png';
        if (state == 'up') {
            src = '/images/new_module/collapse.png';

        }
        img.src = src;
    },clickPanel:function(e,t,o){
           var mainPanle=Ext.getCmp('mainPanelId');
           if(mainPanle&&!mainPanle.owns(t)){
           	    mainPanle.close();
           }else
                return ;
   /**
    *导出年计划
    */    
    },exportExcel:function(){
    	var map = new HashMap();
    	map.put("subModuleId","yearplan_00001");
    	Rpc({functionId:'WP00002007',async:false,success:function(form){
    		var result = Ext.decode(form.responseText);
    		if(result.flag){
    			window.location.target="_blank";
				window.location.href = "/servlet/DisplayOleContent?filename="+result.fileName;
    		}else{
    			Ext.MessageBox.show({  
					title : "提示信息",  
					msg : result.message, 
					icon: Ext.MessageBox.INFO  
				})
    		}
    	},scope:this},map);
    }
 })