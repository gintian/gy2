/**
 * 虚拟角色维护界面
 * changxy      
 * 20161121
 */
Ext.util.CSS.updateRule(".x-tree-icon-parent","background-image","url(images/tree/leaf.gif)")//修改树节点图标样式
Ext.util.CSS.updateRule(".x-tree-icon-parent-expanded","background-image","url(images/tree/leaf.gif)")


 Ext.define('Virtualorg.VirturalRoleTrans',{
    requires:["EHR.extWidget.proxy.TransactionProxy"],
    constructor:function(){
    	  this.initConfig();
    },initConfig:function(){
    	  var me =this;
      var orgMap=new HashMap()
    	  orgMap.put("searchOrg","searchOrg");
    	  Rpc({functionId:'ORG0000005',async:false,scope:me,success:function(res){
    	     var objs=Ext.decode(res.responseText);
    	     me.orgdesc=objs.searchOrg;
    	  }},orgMap);
    	  me.createWindow();      
    },createWindow:function(){
    	var me=this;
    	me.treestore=Ext.create('Ext.data.TreeStore',{
            fields: ['text','id'], 
            proxy: Ext.create("EHR.extWidget.proxy.TransactionProxy",{   
                extraParams:{
                    codesetid  :'83',
                    codesource : me.codesource,
                    nmodule    : me.nmodule,
                    parentid   : me.parentid,
                    level:'1'
                },
               reader:{
               type:'json',
               root:'children'
            },
            functionId:'ORG0000005'
            })
        });
            var treegrid=Ext.create({
            xtype: 'treepanel',
            id:'treePanel',
            height: 300,
            width: 230,
            sortableColumns:false,//列设置操作
            rootVisible: false,
            useArrows : true,//箭头样式展开
            scrollable:true,
            stripeRows : true,// 隔行换色
            multiSelect : false,
            store:me.treestore,
           /* selModel: {//复选框
                type: 'checkboxmodel'
            },*/
            columns: [{
                xtype: 'treecolumn',
                text: '角色维护',
                dataIndex: 'text',
                allowBlank: false,
                flex: 1,
                editor:{
                    xtype:'textfield',
                    allowBlank:false
                	
                },
                renderer:function(value,meta,record){
                	return value+"("+(record.get('orgdesc')==''?me.orgdesc:record.get('orgdesc'))+")";
                }
            },{
                xtype:'treecolumn',//组织机构描述信息
                dataIndex:'orgdesc',
                hidden:true
                }],
             plugins: {
                ptype: 'cellediting',
               clicksToEdit: 2,
               listeners:{
                    edit:me.editor
               }
            },listeners:{
                    beforecellclick:function( me, td , cellIndex , record , tr , rowIndex , e , eOpts){//选中事件 第二次点击取消选中
                            var treePanel=Ext.getCmp('treePanel');
                            if(treePanel.getSelectionModel().getLastSelected()){
                                   if(record.id==treePanel.getSelectionModel().getLastSelected().id){
                                              var d = new Ext.util.DelayedTask(function() {
                                                    treePanel.getSelectionModel().deselectAll(true);
                                                    treePanel.getSelectionModel().clearSelections();
                                              });
                                              d.delay(300);
                                     }
                            	}
                    }
            }
        });
               
       var windows=Ext.widget('window',{
            title:'虚拟角色',
            id:'window',
            width:240,
            height:350,
            modal:true,
            resizable:false,
            layout:'fit',
            items:[treegrid],
            dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'bottom',
                    layout:{
                        pack:'center'
                    	},
                    items: [
                       {xtype:'button',text:'新增',
                       listeners:{
                                click:{
                                    element:'el',
                                    fn:function(){
                                    	   me.beforeAddRole();
                                    }
                                }}},
                       {xtype:'button',text:'保存',
                       listeners:{
                        click:{
                            element:'el',
                            fn:function(){
                                me.saveRole();
                            }
                        }
                       }
                       
                       },
                       {xtype:'button',text:'删除',
                        listeners:{
                                click:{
                                    element:'el',
                                    fn:function(){
                                        me.delRole();
                            }
                        }
                          }
                       }
                    ]
                }]
       }).show();
        

       
    },addRole:function(){//新增：分为选中节点添加（子节点）  不选中节点添加（父节点）
    	   var me=this;
    	   var treePanel=Ext.getCmp('treePanel');
    	   var node= Ext.create('Ext.data.NodeInterface',{
                   text:'',
                   orgdesc:'',
                   id:'',
                   type:'',
                   layer:'',
                   parentid:'',
                   flag:''
               });
    	   var map=new HashMap();
    	   if(treePanel.getSelection().length>0){
    	   	   //treePanel.getSelection()[0].expand(true);
    	       var parentid=treePanel.getSelection()[0].data.parentId;//root或codeitemid
    	       var codeitemid=treePanel.getSelection()[0].data.id;//选中节点的codeitemid
    	       map.put("parentid",parentid);
    	       map.put("codeitemid",codeitemid);
    	       map.put("types","add");//区分是修改，添加 删除
    	      var parentNode=treePanel.getSelectionModel().getSelected().items[0];//父节点 
    	      var lastChildNodes=treePanel.getSelectionModel().getSelected().items[0].lastChild;//展开的最后一个子节点
    	      if(lastChildNodes){      //选中的父节点下有子节点
    	              var layer=lastChildNodes.data.depth;// 当前层级
    	              var id=lastChildNodes.data.id;
    	              if((parseInt(lastChildNodes.data.id.substring(id.length-2,id.length),10)+1)<10){
    	                   node.id=lastChildNodes.data.id.substring(0,id.length-2)+'0'+(parseInt(lastChildNodes.data.id.substring(id.length-2,id.length),10)+1);
    	              }else{
    	                   node.id=lastChildNodes.data.id.substring(0,id.length-2)+(parseInt(lastChildNodes.data.id.substring(id.length-2,id.length),10)+1);
    	              }
    	              node.parentid=lastChildNodes.data.parentid==''?lastChildNodes.data.parentId:lastChildNodes.data.parentid;
    	              node.text='';
    	              node.orgdesc=parentNode.data.orgdesc;
    	              node.layer=lastChildNodes.data.depth;
    	              node.flag='insert';
    	              if(parentNode.data.type=='1')
    	                       node.type='2';//当前新建节点建立在真实存在的节点
    	               else if(parentNode.data.type=='2')
    	                       node.type='3';//当前节点建立在虚拟节点下
    	                else if(parentNode.data.type=='3')
                               node.type='3';       
    	              parentNode.insertChild(parentNode.childNodes.length+1,node);
    	      }else{//选中的节点下没有子节点
    	      	   var id='';
    	      	  // console.log(parentNode.data.id+"(parseInt(parentNode.data.id,10)+1)==="+(parseInt(parentNode.data.id,10)+1));
    	           //if((parseInt(parentNode.data.id,10)+1)<10)
                       id=parentNode.data.id+'0'+1;
                //  else
                  //     id=parentNode.data.id+(parseInt(parentNode.data.id,10)+1);
                 //  console.log(id);    
    	           node.layer=parentNode.data.depth+1;
    	           node.parentid=parentNode.data.id;
    	           node.id=id;
    	           node.text='';
    	           node.orgdesc=parentNode.data.orgdesc;
    	           node.flag='insert';
    	              if(parentNode.data.type=='1')
                               node.type='2';//当前新建节点建立在真实存在的节点
                       else if(parentNode.data.type=='2')
                               node.type='3';//当前节点建立在虚拟节点下
                        else if(parentNode.data.type=='3')
                               node.type='3';               
    	           parentNode.insertChild(0,node);
    	      }   
    	   }else{
    	   	   var rootNode=treePanel.getRootNode();
    	   	   if(rootNode.childNodes.length>0){//同级根节点下有数据
    	   	       var data=rootNode.childNodes[rootNode.childNodes.length-1].data;//取到根节点下最后一条子节点
    	   	       var id='';
    	   	      if((parseInt(data.id,10)+1)<10)
    	   	           id='0'+(parseInt(data.id,10)+1)
    	   	      else
    	   	           id=(parseInt(data.id,10)+1);
    	   	      //node.text='第三个';
    	   	      node.id=id;  //将设置生成的id序列添加到新节点中
    	   	      node.parentid=id;
    	   	      node.text='';
    	   	      node.layer=1;
    	   	      node.type='2',
    	   	      node.flag='insert';
    	   	      node.orgdesc=data.orgdesc;
    	   	       rootNode.insertChild((rootNode.childNodes.length+1),node);//将创建的节点添加到树中
    	   	   }else{
    	   	       node.id='01';
    	   	       node.orgdesc='';
    	   	       node.parentid='01';
    	   	       node.type='2',
    	   	       node.layer=1;
    	   	       node.text='';
    	   	       node.flag='insert';
    	   	     rootNode.insertChild(0,node);
    	   	   }
    	      
    	   }
    	   
    },saveRole:function(){//保存操作
            var me =this;
            var treePanel=Ext.getCmp('treePanel');//取到节点选中的数据
            if(treePanel.getStore().getUpdatedRecords().length>0){
            	var map=new HashMap();
            	map.put("types","update");
                var upStore=treePanel.getStore().getUpdatedRecords();
                var flagList=new Array();
            	var list=new Array();
            	var insertList=new Array();
            	var AddFlag=false;
                for(var i=0;i<upStore.length;i++){
                        var mapArray=new HashMap();
                        var insertArray=new HashMap();
                        if(upStore[i].data.text&&upStore[i].data.flag&&!(upStore[i].data.text=='')&&(upStore[i].data.flag=='update')){//名称不能为空，否则不能保存
                        	    mapArray.put("id",upStore[i].data.id);
                                mapArray.put("text",upStore[i].data.text);
                                mapArray.put("type",upStore[i].data.type);
                                list.push(mapArray);//修改后的信息集合
                        }
                  
                           // mapArray.put("layer",upStore[i].data.layer);      
                        if(upStore[i].data.flag&&(upStore[i].data.flag=='insert')){//标记为插入的记录
                        	   insertArray.put("layer",upStore[i].data.layer);      
                        	   if(upStore[i].data.parentid=='root'||upStore[i].data.parentId=='root')
                                   insertArray.put("parentid",upStore[i].data.id);
                               else
                                   insertArray.put("parentid",  upStore[i].data.parentid?upStore[i].data.parentid:upStore[i].data.parentId);
                               insertArray.put("id",upStore[i].data.id);
                               insertArray.put("text",upStore[i].data.text);
                               insertArray.put("type",upStore[i].data.type);
                            if(upStore[i].data.text==null||upStore[i].data.text=='')
                                    flagList.push(true);
                               insertList.push(insertArray);
                        } 
                    }
                map.put("updatemapList",list);//存储修改后的信息   
                map.put("insertmapList",insertList)
                if(flagList.length>0)
                    for(var i=0;i<flagList.length;i++){
                        if(flagList[i])
                            AddFlag=true;
                    }
                    
                if(!AddFlag){
                         Rpc({functionId:'ORG0000006',async:false,scope:me,success:function(res){
                              var res= Ext.decode(res.responseText);
                              if(res.flag){
                              	var records=treePanel.getStore().getModifiedRecords();
                              	for(var i=0;i<records.length;i++){
                              		records[i].data.type='1'; 
                              		records[i].data.flag='';
                                     records[i].commit();   
                              	}
                              }else{
                                Ext.Msg.alert('提示信息',"操作失败!");
                              }
                           }},map); 
                }
                  else{
                            Ext.Msg.alert('提示信息',"新增角色名称不能为空！");
                  }         
            }
            
    },delRole:function(){
        	     var me=this;
                 var treePanel=Ext.getCmp('treePanel');
                 var map=new HashMap();
                 if(treePanel.getSelection().length>0){
                 	var codeitemid=treePanel.getSelection()[0].data.id;
                    map.put("types","del");
                    map.put("codeitemid",codeitemid);
                  Rpc({functionId:'ORG0000006',async:false,scope:me,success:function(res){
                          var res= Ext.decode(res.responseText);
                          if(res.flag){
                             treePanel.getStore().load();   
                          }else{
                            Ext.Msg.alert('提示信息',"操作失败!");
                          }
                       }},map);
                 }else{
                    Ext.Msg.alert('提示信息',"请选择需要删除的记录!");
                 }
    },editor:function(editor, context, eOpts){
    	var data=context.record.data;
    	if(!data.flag)
    	   data.flag='update';//修改添加修改标记
    	return true;
    },beforeAddRole:function(){
        var me=this;
        var treePanel=Ext.getCmp('treePanel');
         if(treePanel.getSelection().length>0){
               treePanel.getSelection()[0].expand(true,function(){
                    me.addRole();
               },me);
         }else{
             me.addRole();
         }     
    }    
        
 })