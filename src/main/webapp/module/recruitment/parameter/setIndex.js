/**
 * 招聘简历模板子集 setIndex.js
 * */
Ext.define('setIndexUL.setIndex',{
    constructor:function(config) {
        setIndex_me=this;
        this.i = 0;
        setIndex_me.fields='';
        setIndex_me.funcOnly='';
        this.init();
    },
    init:function() {
        var map = new HashMap();
        map.put("a_tab","fieldpriv");
        Rpc({functionId:'ZP0000002352',async:false,success:setIndex_me.initComponent},map);
    },
    initComponent:function(form){//初始化界面
        var result = Ext.decode(form.responseText);
        var columnJson =result.columnJson;
        setIndex_me.fields = result.fields;
        var storeJson = result.storeJson;
        setIndex_me.funcOnly= result.funcOnly;
        
        if(setIndex_me.gridPanel){
            setIndex_me.gridPanel.destroy();
        }
        
        setIndex_me.store = Ext.create('Ext.data.TreeStore', {
            fields:setIndex_me.fields.split(','),
            data:Ext.decode(storeJson)
        });
        
        function tree_event(node,event)
        {
             var datas = event.data;
                 var flag = true;
                 for(var obj in datas){
                     var value = event.get(obj);
                     if(value !='-1' && value !='-2' && value !='-3' && value !='-4')
                         flag = true;
                 } 
                 return flag;
        };

        
        setIndex_me.gridPanel = Ext.create('Ext.tree.Panel', {
            title:"<div style='float:left' id='titleId'>请设置应聘简历指标</div><div style='float:right;padding-right:10px;' id='titilPanel' style='font-weight:normal'><a href='javascript:void(0);' onclick='setIndex_me.combinePrivString();' >保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);' onclick='setIndex_me.resetValue();' >重置</a></div>",
            width: '100%',
            height: '100%',
            bufferedRenderer: false,
            useArrows:true,
            rootVisible: false, //是否隐藏根节点
            autoScroll:true,
            margin:false,  
            draggable:false,//不允许拖动
            enableDD:false,
            animate:true, //动画效果
            store:setIndex_me.store,
            collapsible: false,
            sortableColumns:false,
            multiSelect: false, 
            rowLines: true,
            columnLines: true,
            stripeRows:true,
            columns:Ext.decode(columnJson),
            
            plugins: [  
                Ext.create('Ext.grid.plugin.CellEditing', {  
                    clicksToEdit: 1,  
                    listeners: {  
                        'beforeedit':function(editor,context,eOpts ){
                            var record = context.record;
                            var datas = record.data;
                            var flag = false;//true:可编辑的，false:不可编辑的
                            for(var obj in datas){
                                if(obj == "depth")
                                    continue;
                                
                               if(obj == "index")
                                    continue;
                                
                                var value = record.get(obj);
                                
                                if(value > 1 && value < 12){
                                    flag = true;
                                    break;
                                }
                            } 
                            return flag;
                        }  
                    }  
     
                })  
           ],
        });
           Ext.widget('viewport',{
               layout:'fit',
               padding:"0 5 0 5",  
               style:'backgroundColor:white',
               items:[setIndex_me.gridPanel]
           });
           Ext.on('resize', function (width, height) {
               setIndex_me.gridPanel.setWidth(width*0.8);
               setIndex_me.gridPanel.setHeight(height*0.8);
            });
           
    },
    
    changestatus:function(value,obj,row,colName){//有效和必填要么都不选，要么只选一个 -1：都没选，1：有效，2：必填
        var type=-1;
        var objs = document.getElementsByName(obj.name);
        var record = setIndex_me.store.getAt(row);//获得操作行的下标
        var setId = record.data.parentId;
        setId = setId.substring(0,3)
        status:if(row>=0){
            if("A01"==setId||"a01"==setId){//人员基本信息必选
                 if(value=='2'){
                    if(objs[1].checked){
                        objs[0].checked = true;
                        type = '10';
                    }else{
                        type = '9';
                    }
                    record.set(colName,type);
                }else if(value=='1'){
                        if(objs[0].checked){
                            type = '9';
                        }else{
                            objs[1].checked = false;
                            type = '-3';
                        }
                        record.set(colName,type);
                }
                break status;
            }
            if(value=='1'){
                if(!objs[0].checked){
                    objs[1].checked = false;
                    objs[2].checked = false;
                    type = '-2';
                }else{
                    type = '7';
                }
                
            }
            if(value=='2'){
                if(objs[1].checked){
                    objs[0].checked = true;
                    if(objs[2].checked){
                        type = '5';
                    }else
                        type = '4';
                }else{
                    if(objs[2].checked){
                        type = '6';
                    }else
                        type = '7';
                }
            }
            
            if(value=='3'){
                if(objs[2].checked){
                    objs[0].checked = true;
                    if(objs[1].checked){
                        type = '5';
                    }else
                        type = '6';
                }else{
                    if(objs[1].checked){
                        type = '4';
                    }else
                        type = '7';
                }
            }
            
        }
        var record = setIndex_me.store.getAt(row);//获得操作行的下标
        record.set(colName,type);//重新赋值
    },
    
    backStr:function(value, metaData, record, rowIndex, colIndex){//根据数据渲染复选框按钮
        var colName=metaData.column.dataIndex;
        var id = record.getId()+colName;
        var backStr;
        //当value==11时，复选框渲染为有效，必填，唯一都勾选
        if(value==-1)
        	 backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"'  checked=true  />有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox' checked=true />必填";
        else if(value==-2)
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"'/>有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")'  />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")'/>列表";
        else if(value==-3)
            backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"'/>有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox'/>必填";
        else if(value==-4)
            backStr = "";
        else if(value==4)
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' checked=true />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")' />列表";
        else if(value==5)  
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' checked=true />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")' checked=true />列表";
        else if(value==6)  
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")'  />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")' checked=true />列表";
        else if(value==7)  
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")'  />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")'/>列表";
        else if(value==9)
            backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"' checked=true />有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox'/>必填";
        else if(value==10)
            backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"'  checked=true  />有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox' checked=true />必填";

        return backStr;
    },
    
    combinePrivString:function(){
        var setIndexStore = JSON.stringify(setIndex_me.store.config.data);
        var map = new HashMap();
        if(setIndex_me.fields == null){
        	setIndex_me.fields = "";
        }
        
        map.put("tab_name","fieldpriv");
        map.put("fields",setIndex_me.fields);
        map.put("fieldStore",setIndexStore.toString());
        Rpc({functionId:'ZP0000002353',async:false,success:setIndex_me.saveSuccess},map);
    },

    resetValue:function(){
        var map = new HashMap();
        map.put("a_tab","fieldpriv");
        map.put("flag","resetvalue");
        Rpc({functionId:'ZP0000002352',async:false,success:setIndex_me.initComponent},map);
    },

    processname:function(value){
        return value.split("'")[0];
    },
    
    saveSuccess:function(value){
        setIndex_me.gridPanel.getStore().commitChanges(); 
        Ext.Msg.alert(PROMPT_INFORMATION, INDEX_SAVE_FINISH); 
    }
    
    
});
/**
 * 招聘简历模板子集 setIndex.js
 * */
Ext.define('setIndexUL.setIndex',{
    constructor:function(config) {
        setIndex_me=this;
        this.i = 0;
        setIndex_me.fields='';
        this.init();
    },
    init:function() {
        var map = new HashMap();
        map.put("a_tab","fieldpriv");
        Rpc({functionId:'ZP0000002352',async:false,success:setIndex_me.initComponent},map);
    },
    initComponent:function(form){//初始化界面
        var result = Ext.decode(form.responseText);
        var columnJson =result.columnJson;
        setIndex_me.fields = result.fields;
        var storeJson = result.storeJson;
        
        if(setIndex_me.gridPanel){
            setIndex_me.gridPanel.destroy();
        }
        
        setIndex_me.store = Ext.create('Ext.data.TreeStore', {
            fields:setIndex_me.fields.split(','),
            data:Ext.decode(storeJson)
        });
        
        function tree_event(node,event)
        {
             var datas = event.data;
                 var flag = true;
                 for(var obj in datas){
                     var value = event.get(obj);
                     if(value !='-1' && value !='-2' && value !='-3' && value !='-4')
                         flag = true;
                 } 
                 return flag;
        };

        
        setIndex_me.gridPanel = Ext.create('Ext.tree.Panel', {
            title:"<div style='float:left' id='titleId'>请设置应聘简历指标</div><div style='float:right;padding-right:10px;' id='titilPanel' style='font-weight:normal'><a href='javascript:void(0);' onclick='setIndex_me.combinePrivString();' >保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);' onclick='setIndex_me.resetValue();' >重置</a></div>",
            width: '100%',
            height: '100%',
            bufferedRenderer: false,
            useArrows:true,
            rootVisible: false, //是否隐藏根节点
            autoScroll:true,
            margin:false,  
            draggable:false,//不允许拖动
            enableDD:false,
            animate:true, //动画效果
            store:setIndex_me.store,
            collapsible: false,
            sortableColumns:false,
            multiSelect: false, 
            rowLines: true,
            columnLines: true,
            stripeRows:true,
            columns:Ext.decode(columnJson),
            
            plugins: [  
                Ext.create('Ext.grid.plugin.CellEditing', {  
                    clicksToEdit: 1,  
                    listeners: {  
                        'beforeedit':function(editor,context,eOpts ){
                            var record = context.record;
                            var datas = record.data;
                            var flag = false;//true:可编辑的，false:不可编辑的
                            for(var obj in datas){
                                if(obj == "depth")
                                    continue;
                                
                               if(obj == "index")
                                    continue;
                                
                                var value = record.get(obj);
                                
                                if(value > 1 && value < 12){
                                    flag = true;
                                    break;
                                }
                            } 
                            return flag;
                        }  
                    }  
     
                })  
           ],
        });
           Ext.widget('viewport',{
               layout:'fit',
               padding:"0 5 0 5",  
               style:'backgroundColor:white',
               items:[setIndex_me.gridPanel]
           });
           Ext.on('resize', function (width, height) {
               setIndex_me.gridPanel.setWidth(width*0.8);
               setIndex_me.gridPanel.setHeight(height*0.8);
            });
           
    },
    
    changestatus:function(value,obj,row,colName){//有效和必填要么都不选，要么只选一个 -1：都没选，1：有效，2：必填
        var type=-1;
        var objs = document.getElementsByName(obj.name);
        var record = setIndex_me.store.getAt(row);//获得操作行的下标
        var setId = record.data.parentId;
        setId = setId.substring(0,3)
        status:if(row>=0){
            if("A01"==setId||"a01"==setId){//人员基本信息必选
                 if(value=='2'){
                    if(objs[1].checked){
                        objs[0].checked = true;
                        type = '10';
                    }else{
                        type = '9';
                    }
                    record.set(colName,type);
                }else if(value=='1'){
                        if(objs[0].checked){
                            type = '9';
                        }else{
                            objs[1].checked = false;
                            type = '-3';
                        }
                        record.set(colName,type);
                }
                break status;
            }
            if(value=='1'){
                if(!objs[0].checked){
                    objs[1].checked = false;
                    objs[2].checked = false;
                    type = '-2';
                }else{
                    type = '7';
                }
                
            }
            if(value=='2'){
                if(objs[1].checked){
                    objs[0].checked = true;
                    if(objs[2].checked){
                        type = '5';
                    }else
                        type = '4';
                }else{
                    if(objs[2].checked){
                        type = '6';
                    }else
                        type = '7';
                }
            }
            
            if(value=='3'){
                if(objs[2].checked){
                    objs[0].checked = true;
                    if(objs[1].checked){
                        type = '5';
                    }else
                        type = '6';
                }else{
                    if(objs[1].checked){
                        type = '4';
                    }else
                        type = '7';
                }
            }
            
        }
        var record = setIndex_me.store.getAt(row);//获得操作行的下标
        record.set(colName,type);//重新赋值
    },
    
    backStr:function(value, metaData, record, rowIndex, colIndex){//根据数据渲染复选框按钮
        var colName=metaData.column.dataIndex;
        var id = record.getId()+colName;
        var backStr;
        //当value==11时，复选框渲染为有效，必填，唯一都勾选
        if(value==-1)
        	 backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"'  checked=true  />有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox' checked=true />必填";
        else if(value==-2)
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"'/>有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")'  />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")'/>列表";
        else if(value==-3)
            backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"'/>有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox'/>必填";
        else if(value==-4)
            backStr = "";
        else if(value==4)
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' checked=true />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")' />列表";
        else if(value==5)  
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' checked=true />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")' checked=true />列表";
        else if(value==6)  
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")'  />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")' checked=true />列表";
        else if(value==7)  
            backStr = "<input type='checkbox'  onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"' checked=true />有效 <input name='"+id+"' type='checkbox' onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")'  />必填 <input name='"+id+"' type='checkbox'  onclick='setIndex_me.changestatus(3,this,"+rowIndex+", \""+colName+"\")'/>列表";
        else if(value==9)
            backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"' checked=true />有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox'/>必填";
        else if(value==10)
            backStr = "<input type='checkbox' onclick='setIndex_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"'  checked=true  />有效 <input name='"+id+"'  onclick='setIndex_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox' checked=true />必填";

        return backStr;
    },
    
    processname:function(value){
        return value.split("'")[0];
    }
    
    
});