/**
 * 招聘简历模板子集 setNbase.js
 * */
Ext.define('SetNbaseUL.setNbase',{
	constructor:function(config) {
		setNbase_me=this;
		this.i = 0;
		setNbase_me.fields='';
   		this.init();
	},
	init:function() {
		var map = new HashMap();
		map.put("a_tab","tablepriv");
	    Rpc({functionId:'ZP0000002352',async:false,success:setNbase_me.initComponent},map);
	},
    initComponent:function(form){//初始化界面
    	var result = Ext.decode(form.responseText);
    	var columnJson =result.columnJson;
    	setNbase_me.fields = result.fields;
     	var storeJson = result.storeJson;
     	setNbase_me.panelStore = Ext.create('Ext.data.Store', {
            fields:setNbase_me.fields.split(','),
            data:Ext.decode(storeJson)
        });
    	setNbase_me.gridPanel = Ext.create('Ext.grid.Panel', {
    		   title:"<div style='float:left' id='titleId'>请设置应聘简历子集</div><div style='float:right;padding-right:10px;' id='titilPanel' style='font-weight:normal'><a href='javascript:void(0);' onclick='setNbase_me.combinePrivString();' >保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);' onclick='setNbase_me.resetValue();' >重置</a></div>",
  	           store: setNbase_me.panelStore,
  	           columns:Ext.decode(columnJson),
  	           forceFit: true,
  	           enableLocking:true,
  	           sortableColumns:false,
  	           enableColumnMove:false,
  	           padding:'0 0 7 0',
  	           //scrollOffset: 1,
  	           //可能和列locked属性冲突
//  	           scrollable:true,
  	           columnLines:true,
  	           //解决子集太多加载不全的问题
  	           bufferedRenderer: false,
  	           width:'90%',
	           height:'90%',
  	           selModel: 'cellmodel',
  	           plugins: {
  	               ptype: 'cellediting',
  	               clicksToEdit: 1,
  	               listeners:{
  	                   beforeedit:function(editor,context){
  	                   	var record = context.record;
  	                   	var datas = record.data;
  		                	var flag = false;//true:可编辑的，false:不可编辑的
  		                	for(var obj in datas){
  		                		var value = record.get(obj);
  		                		if(value=='1'||value=='2')
  		                			flag = true;
  		                	} 
  		                	return flag;
  	                   }
  	               }
  	           }
  	       });
	       Ext.widget('viewport',{
	           layout:'fit',
	           padding:"0 5 0 5",  
	           style:'backgroundColor:white',
	           items:[setNbase_me.gridPanel]
	       });
	       Ext.on('resize', function (width, height) {
	    	   setNbase_me.gridPanel.setWidth(width*0.8);
	    	   setNbase_me.gridPanel.setHeight(height*0.8);
	        });
    },
    changestatus:function(value,obj,row,colName){//可选和必填要么都不选，要么只选一个 -1：都没选，1：可选，2：必填
    	var type=-1;
    	var objs = document.getElementsByName(obj.name);
    	var record = setNbase_me.panelStore.getAt(row);//获得操作行的下标
    	var setId = record.data.name.split("'")[1];
    	status:if(row>=0){
    		if("A01"==setId||"a01"==setId){//人员基本信息必选
	    		type = '2';
	    		record.set(colName,type);//重新赋值
	    		setNbase_me.panelStore.reload();//刷新store
	    		break status;
    		}
	    	if(value=='1'&&objs[0].checked){
	    		objs[1].checked = false;
	    		type = '1';
	    	}
	    	if(value=='2'&&objs[1].checked){
	    		objs[0].checked = false;
	    		type = '2';
	    	}
	    	if((value=='1'||value=='2')&&!objs[1].checked&&!objs[0].checked){
	    		objs[0].checked = false;
	    		type = '-1';
	    	}
    	}
    	var record = setNbase_me.panelStore.getAt(row);//获得操作行的下标
    	record.set(colName,type);//重新赋值
    },
    backStr:function(value, metaData, record, rowIndex, colIndex){//根据数据渲染复选框按钮
    	var id = record.getId()+this.i;
        var  colName=metaData.column.dataIndex;
        this.i++;
    	var backStr;
        if(value==1)  
        	backStr = "<input type='checkbox'  onclick='setNbase_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"_a01' checked=true />可选 <input name='"+id+"_a01' onclick='setNbase_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox' />必填";
      	else if(value==-1)
    	   backStr = "<input type='checkbox' onclick='setNbase_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"_b01'/>可选 <input name='"+id+"_b01'  onclick='setNbase_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='checkbox'/>必填";
        else if(value==2)
        	backStr = "<input type='checkbox' onclick='setNbase_me.changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"_c01'  />可选 <input name='"+id+"_c01' type='checkbox'  onclick='setNbase_me.changestatus(2,this,"+rowIndex+", \""+colName+"\")' checked=true />必填";
        return backStr;
    },
    
    combinePrivString:function(){
    	var map = new HashMap();
    	map.put("tab_name","tablepriv");
    	map.put("fields",setNbase_me.fields);
    	map.put("nbasestore",setNbase_me.panelStore.config.data);
    	Rpc({functionId:'ZP0000002353',async:false,success:setNbase_me.saveSuccess},map);
    },
    
    resetValue:function(){
    	var map = new HashMap();
		map.put("a_tab","tablepriv");
		map.put("flag","resetvalue");
	    Rpc({functionId:'ZP0000002352',async:false,success:setNbase_me.initComponent},map);
    },
    
    processname:function(value){
    	return value.split("'")[0];
    },
    
   saveSuccess:function(value){
	   setNbase_me.init();
	   Ext.Msg.alert(PROMPT_INFORMATION, SUBSET_SAVE_FINISH);
    }
    
    
});