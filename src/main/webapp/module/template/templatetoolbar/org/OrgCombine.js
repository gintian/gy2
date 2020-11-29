/**
 * 人事异动-机构合并和划转 lis 20160726
 */
Ext.define('TemplateToolBarUL.org.OrgCombine',{
        constructor:function(config){
        	var me = this;
			me.operationType = config.operationType;//8是合并，9是划转
			me.maxstartdate = config.maxstartdate;//机构合并中最大日期
			me.infor_type = config.infor_type;//2是机构，3是岗位
			me.tab_id = config.tab_id;//模板号
			me.table_name = config.table_name;//模板名称
			me.init();
        },
        init:function(){
        	var me = this;
        	var map = new HashMap();
	   		map.put("tabid",me.tab_id);
	   		map.put("table_name",me.table_name);
	   		map.put("infor_type",me.infor_type);
	   		var functionId = 'MB00002021';//合并初始化
	   		if(me.operationType == "9"){//划转初始化
	   			functionId = 'MB00002023';
	   		}
	   		Rpc({functionId:functionId,async:true,success:function(response,action){
	   			var result = Ext.decode(response.responseText);
	   			var success = result.succeed;
	   			var msg=result.msg;//bug 40628 合并模板，未插入组织单元类型、上级组织单元名称时，合并选择新代码，会报错。提示语没有提示
	   			if(result.succeed){
	   				if(msg!=null&&msg.length>0){
	   					Ext.showAlert(replaceAll(replaceAll(replaceAll(msg,'\\r\\n','<br />'),'\\n','<br />'),'\\r','<br />'));
	   				}else{
	   					me.initOk(result)	   					
	   				}
	   			}
	   		}},map);
        },
        initOk:function(result){
        	var me = this;
			var afterCodeId = "";
        	var codeitemlist = "";//合并后编码
        	var orgId = "";//合并后编码
        	var title = "";
        	
        	if(me.operationType == "9"){//划转
        		title = "机构划转"
        		if(me.infor_type == "3")//岗位
        			title = "岗位划转"
        	}else{//合并
        		title = "机构合并";
        		codeitemlist = result.codeitemlist;
        		orgId = result.orgId;
				afterCodeId = codeitemlist[0].dataValue;
        		if(me.infor_type == "3"){//岗位
        	   		 title = "岗位合并";
        		}
        	}
        		
			//显示内容的面板
			var panel = Ext.create('Ext.form.Panel', {
			    bodyPadding: 5,
			    border:false,
			    width: 500,
			    height: 150,
			    layout: {
			        type: 'vbox',
			        align: 'center',
			        pack:'center'
			    },
			    renderTo: Ext.getBody(),
			    bbar: [
				  { xtype: 'tbfill'},
				  { xtype: 'button', text: '确定',margin:'0 10 0 0' ,handler:function(){
						   var form = this.up('form').getForm();
				            if (form.isValid()) {
				            	var values = form.getValues();
				            	if(me.operationType == "8"){//合并
				            		me.combineOrg(values);
				            	}else{
				            		 me.transferOrg(values);
				            	}
				            }else{
				            	if(me.operationType == "9")
				            		Ext.showAlert("目标机构名不能为空!");
				            }
				            	
				  }},
				  { xtype: 'button', text: '关闭',handler:function(){
				  		me.win.close();
				  } },
				  { xtype: 'tbfill'}
				]
			});
			
			//弹出窗口
        	me.win = Ext.create('Ext.window.Window', {
			    title: title,
			    layout: 'fit',
			    modal:true,//模态窗口,窗口遮住的页面不可编辑
			    items:panel
			}).show();
			
			var end_date = Ext.widget({
				xtype:'datefield',
		        fieldLabel: '有效日期',
		        name:"end_date",
		        labelAlign:'right',
		        labelWidth:60,
				width:300,
			    editable:false,
			    allowBlank:false,//不加这个，当点击日期编辑框而不选择的话，再点其他类型编辑框会报错
			    format:'Y-m-d',
			    minValue:me.maxstartdate,
			    value:new Date().getDate()-1
			});
			
			var states = Ext.create('Ext.data.Store', {
			    fields: ['dataName', 'dataValue']
			});
			Ext.each(codeitemlist,function(obj,index){
				states.insert(index,obj);
			});
			
			var combinecodeitemid = Ext.widget({
			    	xtype:'combobox',
			        fieldLabel: '合并后编码',
			        name:"combinecodeitemid",
			        labelAlign:'right',
			        hidden:me.operationType == "9"?true:false,
			        labelWidth:60,
			        width:300,
				    store: states,
				    queryMode: 'local',
				    displayField: 'dataName',
				    valueField: 'dataValue',
				    margin:'10 0 0 0',
				    value:afterCodeId,
				    editable:false,
				    renderTo: Ext.getBody()
		    });
			
			var combineorgname = Ext.widget({
			        xtype: 'textfield',
			        name: 'combineorgname',
			        width:300,
			        fieldLabel: '合并后名称',
			        hidden:me.operationType == "9"?true:false,
			        labelAlign:'right',
			        labelWidth:60,
			        margin:'10 0 0 0',
			        allowBlank: false  // 表单项非空
		    });
			
		    var imodule="8";//岗位管理模块
		    if(me.infor_type == "2")//机构
		    	imodule = "7";//机构管理模块
	    	
		    	
			var transfercodeitemid = Ext.widget({
			        xtype: 'codecomboxfield',
			        name: 'transfercodeitemid',
			        id:'transfercodeitem_id',
			        onlySelectCodeset:false,//是否限制只能选和codesetid相同的代码
			        width:300,
			        fieldLabel: '目标机构名',
			        hidden:me.operationType == "8"?true:false,
			        labelAlign:'right',
			        labelWidth:60,
			        margin:'10 0 0 0',
			        allowBlank: false,  // 表单项非空
			        nmodule:imodule,
				    ctrltype:"3",
			        codesetid: "UM"  // 表单项非空
		    });
			
			panel.add(end_date);
			if(me.operationType == "8"){//合并
				panel.add(combinecodeitemid);
				panel.add(combineorgname);
			}else{
				panel.add(transfercodeitemid);
			}
    	},
        
        /**
         *   机构划转
         *   @param {} values
         */
        transferOrg:function(values){
            var me = this;
       		var info = "确定要划转组织机构吗？";
       	    if(me.infor_type == "3"){//岗位
			 	 info="确定要划转岗位吗？";
       		 }
       		 Ext.showConfirm(info,function(button, text) {  
 	               if (button == "yes") {  
 	               		var map = new HashMap();
				   		map.put("table_name",me.table_name);
				   		map.put("infor_type",me.infor_type);
				   		cmp = Ext.getCmp("transfercodeitem_id"),
						value =  cmp.getValue(); 
				   		map.put("transfercodeitemid",value);
				   		map.put("end_date",values.end_date);
				   		Rpc({functionId:'MB00002024',async:true,success:function(response,action){
				   			var result = Ext.decode(response.responseText);
				   			var success = result.succeed;
				   			if(result.succeed){
				   				me.win.close();
				   				me.combineOk();
				   			}else{
				   				Ext.showAlert(replaceAll(replaceAll(replaceAll(result.message,'\\r\\n','<br />'),'\\n','<br />'),'\\r','<br />'));//显示错误信息没有换行
				   			}
				   		}},map);
 	               }
       		 });
        },
        
        /**
         *  合并机构
         * @param {} values
         */
        combineOrg:function(values){
       			var me = this;
            	var map = new HashMap();
		   		map.put("table_name",me.table_name);
		   		map.put("infor_type",me.infor_type);
		   		map.put("tarcodeitemdesc",values.combineorgname);
		   		map.put("combinecodeitemid",values.combinecodeitemid);
		   		map.put("end_date",values.end_date);
		   		Rpc({functionId:'MB00002022',async:true,success:function(response,action){
		   			var result = Ext.decode(response.responseText);
		   			var success = result.succeed;
		   			if(result.succeed){
		   				me.win.close();
		   				me.combineOk();
		   			}else{
		   				Ext.showAlert(replaceAll(replaceAll(replaceAll(result.message,'\\r\\n','<br />'),'\\n','<br />'),'\\r','<br />'));//显示错误信息没有换行
		   			}
		   		}},map);
        },
        combineOk:function(){
        	var tablePanel=null;
        	if(templateTool_me.templPropety.view_type=="list"){
	    		tablePanel=templateList_me.templateListGrid.tablePanel;
		  		templateTool_me.listRemove(tablePanel);
	       	}else {
		    	tablePanel=templateCard_me.personListGrid.tablePanel
	       		templateTool_me.cardRefresh(tablePanel,"true");
	       	}  
        }
 });