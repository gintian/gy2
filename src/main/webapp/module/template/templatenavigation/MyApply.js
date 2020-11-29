/**
* 我的申请 页签生成js
* zhaoxg 2016-3-8
*/
Ext.define('TemplateNavigation.MyApply',{
   constructor:function(config){
   		if(Ext.getCmp("myapply1_toolbar")){//由于点击页签重新加载表格工具，而页签的removeAll（true）未能销毁之前曾经加载过的，导致对象冲突，故有此判断
    		Ext.getCmp("myapply1_toolbar").destroy();
    	}
    	MyApplyScope = this;
    	MyApplyScope.callBackFunc = config.callBackFunc;//回调函数（itemid，panel）  可用于该组件之渲染
    	MyApplyScope.itemid = config.itemid;//配合回调函数把该组件渲染到的位置
    	MyApplyScope.clienth = config.clienth;
    	MyApplyScope.module_id = config.module_id;
    	MyApplyScope.tabid = config.tabid;
    	MyApplyScope.serviceHallFlag = config.serviceHallFlag;
    	
    	MyApplyScope.bs_flag = "";
    	MyApplyScope.query_type = "";
    	var map = new HashMap();
    	map.put("flag","0");//0:首次进入 1：查询进入
    	map.put("module_id",MyApplyScope.module_id); 
		map.put("query_type","1"); 
		map.put("tabid",MyApplyScope.tabid);
		if(config.fromflag){//首页进入我的申请标记
			map.put("fromflag",config.fromflag);
		}
		map.put("sp_flag","1");
		if(config.query_type==="0"){
			MyApplyScope.query_method = "0";
    		map.put("query_method","0");
    	}
	    Rpc({functionId:'MB00006004',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				var templatejson=result.templatejson;
				MyApplyScope.templatejson = templatejson;
		    	var conditions=result.tableConfig;
				var obj = Ext.decode(conditions);
				var templateObj = new BuildTableObj(obj);
				MyApplyScope.templateObj = templateObj;
	  		  	MyApplyScope.init(templateObj);
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
    },
   	init:function(templateObj){

		var radioPanel = Ext.create('Ext.container.Container', {
			border : false,
			layout: 'hbox',
			//margin:'0 0 0 10',
			items:[	          	
				{
        			xtype      : 'fieldcontainer',
		            defaultType: 'radiofield',
		            layout: 'hbox',
		            fieldLabel: '任务类型',
		            labelSeparator:'',
		            labelWidth:50,
	            	items: [
					    {
		                    boxLabel  : MB.LABLE.running,//运行中
		                    name      : 'myapplymode',
		                    inputValue: '1',
		                    width     :  60,
		                    checked   : MyApplyScope.query_method=="0"?false:true,
		                    id        : 'myapplyradio0',
		                    listeners:{
				            	'change':function(th,newvalue){
				   					if(newvalue){
				   						/*var recallbutton = Ext.getCmp("recallbutton");
				   						if(recallbutton)
				   							recallbutton.show();
				   						this.ownerCt.ownerCt.setMargin('0 0 0 10');*/
				   						MyApplyScope.query_method = "1";
				   						MyApplyScope.query();
				   					}
				            	}
				            }
		                },
		                {
		                    boxLabel  : MB.LABLE.hasEnded,//终止
		                    name      : 'myapplymode',
		                    inputValue: '3',
		                    width     :  60,
		                    id        : 'myapplyradio2',
		                    listeners:{
				            	'change':function(th,newvalue){
				   					if(newvalue){
				   						/*var recallbutton = Ext.getCmp("recallbutton");
				   						if(recallbutton)
				   							recallbutton.hide();
				   						this.ownerCt.ownerCt.setMargin('0 0 0 0');*/
				   						MyApplyScope.query_method = "3";
				   						MyApplyScope.query();
				   					}
				            	}
				            }
		                },
		                {
		                    boxLabel  : MB.LABLE.finish,//'结束'
		                    name      : 'myapplymode',
		                    inputValue: '2',
		                    width     :  60,
		                    id        : 'myapplyradio1',
				            listeners:{
				            	'change':function(th,newvalue){
				   					if(newvalue){
				   						/*var recallbutton = Ext.getCmp("recallbutton");
				   						if(recallbutton)
				   							recallbutton.hide();
				   						this.ownerCt.ownerCt.setMargin('0 0 0 0');*/
				   						MyApplyScope.query_method = "2";
				   						MyApplyScope.query();
				   					}
				            	}
				            }
		                }
		            ]
		         }
		     ]
		})
		var tools = undefined;//add by xiegh on date 20171220 bug:33422 陈总提：7x 首页快捷进入模板的时候，没有返回按钮
		if(MyApplyScope.serviceHallFlag){//如果true，是从服务大厅进入我的申请，加载关闭图标，反之，则不用加载关闭图标
			tools = [{
			    type:'close',
			    tooltip: '关闭',
			    handler: function(event, toolEl, panelHeader) {
			        Ext.destroy(parent.Ext.getCmp('serviceHallWin'));
			    	
			    	window.location.href="/templates/index/hcm_portal.do?b_query=link";//add by xiegh on date20180118 bug34023
			    }
			}];
		}
		MyApplyScope.MyApplyPanel = Ext.create('Ext.panel.Panel', {
			id:'myapplyId',		
			border : false,
			height:MyApplyScope.clienth,
			scrollable:false,
			margin:"0 0 0 2",//lis 20160513
			layout:'fit',
			items:[templateObj.getMainPanel()],
			tools:tools
		})
		if(MyApplyScope.serviceHallFlag)//add by xiegh on 20170831 这个是从服务大厅传来的参数标识，为了给这个panel加标题
			MyApplyScope.MyApplyPanel.setTitle('我的申请');
		var toolBar = Ext.getCmp("myapply1_toolbar");
		toolBar.add(radioPanel);
		
		
		if(MyApplyScope.callBackFunc){
            Ext.callback(eval(MyApplyScope.callBackFunc),null,[MyApplyScope.itemid,MyApplyScope.MyApplyPanel]);
		}
   	},
	getSploop:function(value, metaData, Record){//审批过程
	   	var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id_e;
		return "<a href='javascript:void(0);' onclick=MyApplyScope.showCard('"+tabid+"','"+ins_id+"','"+task_id+"')><img src='/images/view.gif' width='16' height='16' border='0'></a>";
	},
	
	getTopic:function(value, metaData, Record){//主题
   		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id_e;
		var recallflag = Record.data.recallflag;
		var actor_type = Record.data.actortype;
		var name = Record.data.name;
		name = getEncodeStr(name);
		var finished = Record.data.finished;
		var finish=finished.split('`')[0];
		if(finish=='4'||finish=='5'||finish=='6'){//终止和结束的不需要撤回按钮
			recallflag = "0";
		}
		var html="<a href=\"javascript:MyApplyScope.showPrint('"+tabid+"','"+ins_id+"','"+task_id+"','"+recallflag+"','"+actor_type+"','0','"+name+"');\" >"+value+"<a/>";
		return html;
	},
	
	showCard:function(tabid,ins_id,task_id){
		var obj = new Object();
		 obj.tabid=tabid; 
	     obj.task_id=task_id;
	     if(MyApplyScope.module_id=='7')
	    	  obj.infor_type="2";
	     else	 if(MyApplyScope.module_id=='8')
	    	  obj.infor_type="3";
	     else
	    	  obj.infor_type="1"; 
		 obj.return_flag="3";  
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateViewProcessUL':rootPath+'/module/template/templatetoolbar/viewprocess'
			}
		});
	   	Ext.require('TemplateViewProcessUL.TemplateViewProcess',function(){
			Ext.create("TemplateViewProcessUL.TemplateViewProcess",obj);
		});
	},
	getBrowsePrint:function(value, metaData, Record){
		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id_e;
		var recallflag = Record.data.recallflag;
		var actor_type = Record.data.actortype;
		var name = Record.data.name;
		name = getEncodeStr(name);
		var finished = Record.data.finished;
		var finish=finished.split('`')[0];
		if(finish=='4'||finish=='5'||finish=='6'){//终止和结束的不需要撤回按钮
			recallflag = "0";
		}
		return "<a href='javascript:void(0);' onclick=MyApplyScope.showPrint('"+tabid+"','"+ins_id+"','"+task_id+"','"+recallflag+"','"+actor_type+"','1','"+name+"')><img src='/images/new_module/row_view.png' width='16' height='16' border='0'></a>";
	},
	query:function(){
	    var map = new HashMap();
	    map.put("flag","1");//0:首次进入 1：查询进入
	    map.put("tabid",MyApplyScope.tabid+"");
    	map.put("module_id",MyApplyScope.module_id);
		map.put("query_method",MyApplyScope.query_method);
		map.put("query_type","1");
		map.put("days","30");
		map.put("start_date","");
		map.put("end_date","");
		map.put("sp_flag","1");
	    Rpc({functionId:'MB00006004',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
		    	MyApplyScope.loadTable();
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	loadTable:function(){
		var store = Ext.data.StoreManager.lookup('myapply1_dataStore');
		store.currentPage=1;
		store.load();
	},
	showPrint:function(tabid,ins_id,task_id,recallflag,actor_type,browseprint,name){
		MyApplyScope.myMask = MyApplyScope.tabid==undefined?Ext.getCmp("maskId"):Ext.getCmp("maskId_portal");
      	if(!!!MyApplyScope.myMask){
	      	MyApplyScope.myMask = new Ext.LoadMask({
	      		id:MyApplyScope.tabid==undefined?"maskId":"maskId_portal",
			    target : MyApplyScope.tabid==undefined?Ext.getCmp("template"):Ext.getCmp("template_portal")
			});
		}
		MyApplyScope.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="3";
		//[66764]
		templateObj.module_id=MyApplyScope.module_id;
		templateObj.approve_flag="0";
		templateObj.task_id=task_id;
		templateObj.sp_flag="1";
		templateObj.ins_id=ins_id;
		templateObj.callBack_init="MyApplyScope.tempFunc";
		templateObj.callBack_close="MyApplyScope.goBack";
		templateObj.other_param="recallflag="+recallflag+"`browseprint="+browseprint;
		var isDelete = false;
		name = getDecodeStr(name);
		if(name.indexOf(MB.MSG.bydelete)!=-1&&MyApplyScope.query_method == "3"){
			isDelete = true;
			templateObj.other_param="isDelete="+isDelete+"";
		}
/*		if(MyApplyScope.tabid)
			templateObj.other_param="visible_title=0";*/
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
	tempFunc:function(){
		MyApplyScope.myMask.hide();
		if(MyApplyScope.tabid!=undefined){
			Ext.getCmp("template_portal").removeAll(false);
			Ext.getCmp("template_portal").add(templateMain_me.mainPanel);
		}else{
			Ext.getCmp("template").removeAll(false);
			Ext.getCmp("template").add(templateMain_me.mainPanel);
		}
	},
	goBack:function(){
		if(MyApplyScope.tabid!=undefined){
			Ext.getCmp("template_portal").removeAll(false);
			Ext.getCmp("template_portal").add(MyApplyScope.MyApplyPanel);
		}else{
			Ext.getCmp("template").removeAll(false);
			Ext.getCmp("template").add(templatenavigation.tabs);
		}
		MyApplyScope.loadTable();
	},
	//显示审批人角色详细
	getRoleInfo:function(value, metaData, Record){
		var tabid = Record.data.tabid;
		var actorname = Record.data.actorname;
		var task_id = Record.data.task_id_e;
		var actor_type = Record.data.actor_type;
		if(actor_type=='2')
			return "<a href='javascript:void(0);' onclick=MyApplyScope.showRoleInfo(this,\""+tabid+"\",\""+task_id+"\")>"+actorname+"<a/>";
		else if(actor_type=='5'){
			return "<a href='javascript:void(0);' onclick=MyApplyScope.showRoleInfo(this,\""+tabid+"\",\""+task_id+"\")>"+actorname+"<a/>";
		}else
		    return actorname;
	},
	showRoleInfo:function(e,tabid,task_id){
	    Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateNavigationOther': rootPath+'/module/template/templatenavigation/other'
			}
		});
		Ext.require('TemplateNavigationOther.DisplayRoleInfo',function(){
			Ext.create("TemplateNavigationOther.DisplayRoleInfo",{element:e,tabid:tabid,task_id:task_id});
		});
	},
	/**
	 * 撤回申请
	 */
	recallTask:function(recordindex){
		var map = new HashMap();
		var records = [];
		if(recordindex!=-1){
			var store = Ext.data.StoreManager.lookup('myapply1_dataStore');
			records.push(store.data.items[recordindex].data);
		}else{
			var selectRecord = MyApplyScope.templateObj.tablePanel.getSelectionModel().getSelection();
			var isHaveNoRecall = false;
			for(var i=0;i<selectRecord.length;i++){
				var recallflag = selectRecord[i].data.recallflag;
				if(recallflag=='0')
					isHaveNoRecall = true;
				else
					records.push(selectRecord[i].data);
			}
			if(isHaveNoRecall){
				Ext.showAlert("选中的记录中存在不可撤回的记录!");
				return;
			}
			if(records.length==0){
				Ext.showAlert("请选择需要撤回的记录!");
				return;
			}
		}
		Ext.showConfirm("确定执行撤回操作吗?",function(optional){
			if(optional=='yes'){
				map.put("recallList",records);
				map.put("module_id",MyApplyScope.module_id);
				map.put("ischeck","1");
			    Rpc({functionId:'MB00006018',async:false,success:function(form,action){
			    	var result = Ext.decode(form.responseText);
			    	var flag=result.succeed;
					if(flag==true){
						var notRecallName = result.notRecallName;
						if(notRecallName!=undefined&&notRecallName.length>0){
							Ext.showAlert("《"+notRecallName+"》表单已被查看或者已被处理,不可撤回!",function(){
								MyApplyScope.loadTable();
							});
							return;
						}
						var recallname = result.recallname;
						if(recallname!=undefined&&recallname.length>0){
							Ext.showConfirm("《"+recallname+"》表单中存在未提交的数据,撤回将被覆盖,确定撤回吗\?", function(optional){
					    		if(optional=='yes'){
					    			var map = new HashMap();
					    			map.put("recallList",records);
					    			map.put("module_id",MyApplyScope.module_id);
					    			map.put("ischeck","0");
					    			Rpc({functionId:'MB00006018',async:false,success:function(form,action){
					    				var result = Ext.decode(form.responseText);
					    				if(result.succeed){
					    					MyApplyScope.loadTable();
					    				}else{
					    					Ext.showAlert(result.message);
					    				}
					    			}},map);
					    		}else{
					    			return;
					    		}
					    	});
						}else{
							MyApplyScope.loadTable();
						}
			  		}else{
						Ext.showAlert(result.message);
					}
			    }},map);
			}else
				return;
		});
	},
	showRecallFlag:function(value, metaData, Record){
		var recallflag = Record.data.recallflag;
		var finished = Record.data.finished;
		var task_id = Record.data.task_id_e;
		var recordIndex = metaData.recordIndex;
		var finish=finished.split('`')[0];
		if((finish=='2'||finish=='3')&&recallflag=='1')
			return "<div style='vertical-align:middle;'><span style='padding:0px 3px 0px 0px;'>"+finished.split("`")[1]+"</span><img src='/images/new_module/recall.png" +
					"' onclick=MyApplyScope.recallTask(\""+recordIndex+"\") width='16' height='16' style='vertical-align:bottom;cursor:pointer;' title='撤回单据' border='0'></div>";
		else
		    return finished.split("`")[1];
	}
})