//代表作摘要
Ext.define("ConfigFileURL.Representative",{
	extend:'Ext.container.Container',
	xtype:'representative',
	width:'100%',
	height:'89%',
	margin:'10 45 10 45',
	scrollable:true,
	temp_ids:undefined,//模板list
	temp_subCheckMap:undefined,//模板设置勾选子集 temp_id:[t_A04_1,....]
	subUnionMap:undefined,//{t_A04_1:['fieldid`fielddesc',...]} 设置选择的子集 并集
	temlate_Param:undefined,//模板对应子集 id:name : xxxx
	fieldMap:undefined,//代表作模板存储的list
	sub_filedMap:undefined,//存储字段匹配map {"t_"setid+"_"+change_flag(T_A04_1):{'工号'：'','序号'：'',...'},only_key2:{'工号'：'','序号'：'',...'}...}
	sub_filedColMap:undefined,//存储匹配字段列名{only_key1:XXX,only_key2:XXX}
	layout:'vbox',
	constructor:function(){
		this.callParent();
		representative=this;
		var me=this;
		var map=new HashMap();
		map.put("flag","thirdStep");
		map.put("type","0");
		Rpc({functionId:'ZC00004011',async:false,success:function(res){
			var res=Ext.decode(res.responseText);
			if(res.status){
				me.temp_ids=res.temp_ids;
				me.temlate_Param=res.temlate_Param;
				if(res.sub_filedMap){
					me.sub_filedMap=res.sub_filedMap;
				}
				me.fieldMap=res.fieldMap;
				me.init();
			}else{
				Ext.showAlert(res.eMsg);
			}
		},scope:me},map);
	},
	init:function(){
		var me=this;
		this.add(me.descPanel("选择代表作子集"),me.tempSubPanel(),me.descPanel("字段匹配"));
	},
	descPanel:function(value){
		return Ext.widget("container",{
			width:'100%',
			height:30,
			style:'font-size:14px;',
			html:'<div style="width:100%;height:100%;line-height:30px">'+value+'</div>'
		})
	},
	tempSubPanel:function(){
		var me=this;
		Ext.create('Ext.data.Store', {
		    storeId: 'templateStore',
		    fields:[ 'name', 'desc', 'img',"temp_id","temp_key","sub_ids"],
		    data: []
		});
		var store=Ext.data.StoreManager.lookup('templateStore');
		if(me.temp_ids&&me.temp_ids.length>0){
			for(var i=0;i<me.temp_ids.length;i++){
				store.insert(i,
						{
					'name':me.temp_ids[i].split(':')[1],
					"desc":'',
					"img":'',
					"temp_id":me.temp_ids[i].split(':')[0],
					"temp_key":me.temp_ids[i],
					"sub_ids":''//选中的子集指标
						}
				);
			}
		}
		var panel=Ext.create('Ext.grid.Panel', {
			    store: Ext.data.StoreManager.lookup('templateStore'),
			    columns: [
			        { text: '模板名称', dataIndex: 'name',width:'20%' },
			        { text: '代表作子集', dataIndex: 'desc',width:'72%' },
			        { 
			          text: '操作', 
			          dataIndex: 'img',
			          align:'center',
			          width:'7%', 
			          renderer:function(value, metaData,record,rowIndex,colIndex,store,view){
			        	  return "<img onclick='representative.openWindow(\""+record.get("temp_key")+"\",\""+rowIndex+"\");' src='../../../images/table.gif' style='cursor:pointer'>";
			          }
			        }
			    ],
			    height:150,
			    width:'89%',
			    columnLines:true,
			   	rowLines:true,
			    listeners:{
			    	afterrender:function( e, eOpts){
			    		var store=Ext.data.StoreManager.lookup('templateStore');
			    		var oldStore=Ext.data.StoreManager.lookup('template_field_Store');
			    		if(oldStore)
			    			oldStore.destroy();
			    		for(var i=0;i<store.getCount();i++){
			    			var temp_id=store.getAt(i).get("temp_key");
			    			var list=me.temlate_Param[temp_id];
			    			var desc="";
			    			var idArry=[];
			    			for(var j=0;j<list.length;j++){
			    				if(list[j]&&list[j].check_flag===true){
			    					desc+=list[j].set_desc+",";
			    					var sub_id="t_"+list[j].setid+"_"+list[j].change_flag;
			    					idArry.push(sub_id);
			    					me.comperSubFieldlist(sub_id,list[j].fieldlist);
			    				}
			    			}
			    			store.getAt(i).set("desc",desc);
			    			store.getAt(i).set("sub_ids",idArry);
			    			store.getAt(i).commit();
			    			me.renderComperData(temp_id);//字段匹配动态添加列
			    		}
			    	}
			    }
			});
		return panel;
	},
	openWindow:function(id,index){
		var me=this;
		var subArry;
		if(id in me.temlate_Param){
			subArry=me.temlate_Param[id];
		}
		Ext.create('Ext.data.Store', {
		    storeId: 'template_sub_Store',
		    fields:[ 'set_desc', 'setid','only_key','check_filed','fieldlist'],
		    data: []
		});
		var store=Ext.data.StoreManager.lookup('template_sub_Store');
	
		for(var i=0;i<subArry.length;i++){
			store.insert(i,
							{
							'set_desc':subArry[i].set_desc,
							"setid":subArry[i].setid,
							"change_flag":subArry[i].change_flag,//子集指标是变化前或者是变化后指标
							"only_key":subArry[i].only_key,
							"check_flag":subArry[i].check_flag,
							"fieldlist":subArry[i].fieldlist,
							"check_filed":subArry[i].check_filed?subArry[i].check_filed:'',//设置选择标识的指标
							"flag":subArry[i].flag//是否启用标识
							}
						);
		}
		
		
		var panel=Ext.create('Ext.grid.Panel', {
			    store: Ext.data.StoreManager.lookup('template_sub_Store'),
			    columns: [
			        { text: '子集名称', dataIndex: 'set_desc',width:'50%' },
			        { 
			          text: '代表作标识', 
			          dataIndex: 'check_filed',
			          align:'center',
			          width:'40%',
			          renderer:function(value, metaData,record,rowIndex,colIndex,store,view){
			        	  if(record.get("check_filed"))
			        		  return record.get("check_filed").split("`")[1];
			          },
			          editor: {xtype:'textfield'}
			        }
			    ],
			    height:'100%',
			    width:'100%',
			    columnLines:true,
			   	rowLines:true,
			    selModel: {
			          selType: 'checkboxmodel'
			    },
			    plugins: {
			    	ptype: 'cellediting',
			        clicksToEdit: 1
			    },
			    listeners:{
			    	deselect:function(e, record, index, eOpts){//取消选中时 获取其他已选中的相同子集 更新下拉选列表
			    		var store=Ext.data.StoreManager.lookup('templateStore');
			    		var index_sub="t_"+record.get("setid")+"_"+record.get("change_flag");
			    		var only_key=record.get("only_key");
			    		for(var i=0;i<store.getCount();i++){
			    			var sub_ids=store.getAt(i).get("sub_ids");
			    			if(sub_ids.join().indexOf(index_sub)>-1){
				    			me.updateSubFieldlist(only_key,store.getAt(i).get("temp_key"),index_sub,record.get("fieldlist"));
			    			}
			    		}
			    	},
			    	beforedestroy:function(p,opt){
			    		//对象销毁前 保存数据
			    		var oldArry=p.store.getData();
			    		for(var i=0;i<oldArry.getCount();i++){//未选中数据 check_flag标识为false
			    			p.store.getAt(i).set("check_flag",false);
			    			p.store.getAt(i).commit();
			    		}
			    		var arry=p.getSelection();
			    		var tempStore=Ext.data.StoreManager.lookup('templateStore');
			    		var desc="";
			    		var template_ids=[];
			    		for(var i=0;i<arry.length;i++){
			    			desc+=arry[i].get("set_desc")+",";
			    			arry[i].set("check_flag",true);
			    			var sub_id="t_"+arry[i].get("setid")+"_"+arry[i].get("change_flag");
			    			template_ids.push(sub_id);
			    			//选择多个子集，相同子集取子集指标交集作为字段对比下拉选项内容
			    			me.comperSubFieldlist(sub_id,arry[i].get("fieldlist"));
			    		}
			    		tempStore.getAt(index).set("sub_ids",template_ids);
			    		//修改代表作子集名称
			    		tempStore.getAt(index).set("desc",desc);
			    		tempStore.getAt(index).commit();
			    		//修改代表作子集名称
			    		var newArry=p.store.getData();
			    		me.temlate_Param[id]=[];
			    		for(var i=0;i<newArry.getCount();i++){
			    			var data=newArry.items[i].getData();
			    			delete data["id"];
			    			me.temlate_Param[id].push(data);
			    		}
			    		me.renderComperData(id);//字段匹配动态添加列
			    	},
			    	afterrender:function(scope,opt){
			    		var dataArry=panel.getStore().getData();
			    		if(dataArry&&dataArry.getCount()>0){
			    			for(var i=0;i<dataArry.getCount();i++){
			    				if(dataArry.items[i].getData().check_flag===true)
			    					panel.getSelectionModel().select(i,true)
			    			}
			    		}
			    	}
			    }  
			});
		panel.on("beforeedit",function( editor, e, eOpts){
			if(e.record.get("flag")!==true)
				return false;
			var store=Ext.create('Ext.data.Store',{
    			fields:['field','value']
    		})
    		var arry=e.record.get("fieldlist");
    		for(var i=0;i<arry.length;i++){
    			if(arry[i].split("`")[3]!="45")//非45号代码类指标不显示
    				continue;
    			store.insert(i,{
    				"field":arry[i],"value":arry[i].split("`")[1]
    			})
    		}
    		e.column.setEditor({
             	xtype:"combo",
             	store:store,
             	queryMode: 'local',
                displayField: 'value',
                valueField: 'field',
                listeners:{
    				focus:function(f, event, eOpts){
    					f.setValue(f.getValue());
    					if(f.getValue()&&f.getValue()!=null&&f.getValue().indexOf('`')>-1)
    						f.setRawValue(f.getValue().split("`")[1]);
    				}
    			}
            });
		
    	})
    	panel.on("edit",function(editor,e,opt){
    		e.record.set("check_filed",e.value);
			e.record.commit()
    	});
		var window=Ext.create('Ext.window.Window',{
						width:300,
						height:400,
						modal:true,
						title:'选择代表作信息集',
						layout:'fit',
						items:[panel],
						scrollable:true,
						buttonAlign : 'center',
						buttons:[{
							text:'确认',
							listeners:{
								click:function(){
									window.destroy();
								}
							}
						}]
						
		
					}).show();
	},
	updateSubFieldlist:function(dselectId,tempid,subName,fieldlist){//勾选后的子集取消勾选 更新子集map
		var me=this;
		var arry=me.temlate_Param[tempid];
		for(var i=0;i<arry.length;i++){
			if(subName==="t_"+arry[i].setid+"_"+arry[i].change_flag){
				if(dselectId===arry[i].only_key)
					continue;
				me.comperSubFieldlist("t_"+arry[i].setid+"_"+arry[i].change_flag,arry[i].fieldlist);
				
			}
		}
	},
	comperSubFieldlist:function(sub_id,fieldlist){//子集对比，取子集两个交集 返回一个新数组
		var me=this;
		if(me.subUnionMap==undefined||!me.subUnionMap[sub_id]){
			if(me.subUnionMap==undefined)
				me.subUnionMap={};
			me.subUnionMap[sub_id]=fieldlist;
		}else{
			var arry=me.subUnionMap[sub_id];
			var arryC=fieldlist;//对比数组
			var newArry=[];
			if(arry.length<=0){
				me.subUnionMap[sub_id]=arryC;
			}else{
				for(var j=0;j<arry.length;j++){
					if(arryC.join().indexOf(arry[j])>-1){//两个相同名称的子集取两个子集指标list共同的数据
						newArry.push(arry[j]);
					}
				}
				me.subUnionMap[sub_id]=newArry;
			}
		}
	},
	renderComperData:function(id){//字段匹配渲染勾选的子集指标
		var me=this;
		var arry=me.temlate_Param[id];
		var updateArry=[];//选中后的子集数组
		var unCheckObj={};//未被选中的子集对象 only_key:false
		for(var i=0;i<arry.length;i++){
			if(arry[i].check_flag===true){
				updateArry.push(arry[i]);
				//me.comperSubFieldlist(sub_id,arry[i].get("fieldlist"));
			}else{
				var map=arry[i];
				var key="t_"+map.setid+"_"+map.change_flag;
				unCheckObj[key]=false;
				//取消勾选后 获取其他模板设置的相同子集 更新下拉选map
			}
		}
		var oldStore=Ext.data.StoreManager.lookup('template_field_Store');
		//oldStore.destroy();//操作字段匹配重新创建store与panel
		var data=[];
		var rows=me.fieldMap.fieldlist;//获取行数
		var fields=['subName'];
		if(oldStore)
			fields=oldStore.config.fields;//原store field
		if(typeof(me.sub_filedMap)==undefined||!me.sub_filedMap)
			me.sub_filedMap={};
		if(typeof(me.sub_filedColMap)==undefined||!me.sub_filedColMap){
			me.sub_filedColMap={};
		}
		if(fields[1]=='desc'){//有desc列标识为空列代表未添加过子集指标
			fields=['subName'];
		}

		//由于涉及到合并模板子集指标 点击取消选择子集时 校验其他模板是否都已取消 若都取消则去除当前列及数据
		for(var i=0;i<fields.length;i++){
			var key=fields[i];
			if(unCheckObj[key]===false&&me.deleteCol(key)){
				fields[i]="";
			}
		}
		//添加子集列前 判断是否存在相同子集t_ A04_1/t_A04_2   setid+"_"+change_flag
		for(var i=0;i<updateArry.length;i++){
			
			var key="t_"+updateArry[i].setid+"_"+updateArry[i].change_flag;
			var desc=updateArry[i].set_desc;
			if(fields.join().indexOf(key)>-1){
				continue;
			}
			fields.push(key);
			if(!me.sub_filedMap[key])
				me.sub_filedMap[key]=me.setfieldMap(key);
			me.sub_filedColMap[key]=desc;
		}
		this.add(me.subfiledPanel(fields));
	},
	deleteCol:function(key){//字段匹配 取消勾选校验其他模板相同子集是否都取消勾选
		var me=this;
		if(me.temlate_Param){
			for(var index in me.temlate_Param){
				var list=me.temlate_Param[index];
				for(var j=0;j<list.length;j++){
					//存在勾选的 不删除字段匹配对应的列
					if("t_"+list[j].setid+"_"+list[j].change_flag===key&&list[j].check_flag===true){
						return false;
					}
				}
			}
		}
		return true;
	},
	getsubFieldlist:function(sub_id){//根据t_A04_1 查找模板所有子集 选中的子集 
		var me=this;
		if(me.temlate_Param){
			for(var index in me.temlate_Param){
				var list=me.temlate_Param[index];
				for(var j=0;j<list.length;j++){
					//存在勾选的 不删除字段匹配对应的列
					if("t_"+list[j].setid+"_"+list[j].change_flag===sub_id&&list[j].check_flag===true){
						return list[j].fieldlist;
					}
				}
			}
		}
		return null;
	},
	detilStoreData:function(key){
		var me=this;
		var map=me.sub_filedMap;
		var firstCol=me.fieldMap.fieldlist;//第一列默认数据 长度为行数
		var obj={};
		for(var i=0;i<firstCol.length;i++){
			obj={"subName":firstCol[i]};
			var valueMap=map[key];
			obj[key]=valueMap[firstCol[i]];
		}
	},
	setfieldMap:function(){//根据云代表摘要创建对应存储map 供sub_filedMap使用
		var me=this;
		var fields=me.fieldMap.fieldlist;
		var obj={};
		for(var i=0;i<fields.length;i++){
			obj[""+fields[i]+""]="";
		}
		return obj;
	},
	setCheckFlag:function(rowIndex){
		var store=Ext.data.StoreManager.lookup('template_sub_Store');
		if(store.getAt(rowIndex).get("flag")){
			store.getAt(rowIndex).set("flag",false);
		}else{
			store.getAt(rowIndex).set("flag",true);
		}
	},
	subfiledPanel:function(storeFields){//代表作摘要指标对比panel  //storeFields {setName,only_key.....}
		var me=this;
		var fields=[];
		if(!storeFields){//初始化状态
			fields=[ 'subName', 'desc'];
		}else{
			fields=storeFields;
		}
		
		var oldStore=Ext.data.StoreManager.lookup('template_field_Store');
		if(oldStore){
			oldStore.destroy();
			if(Ext.getCmp('template_field_panel'))
				Ext.getCmp('template_field_panel').destroy();
		}
		
		var store=Ext.create('Ext.data.Store', {
		    storeId: 'template_field_Store',
		    fields:fields,
		    data: []
		});
		var store=Ext.data.StoreManager.lookup('template_field_Store');
		var columns=[];
		var list=me.fieldMap.fieldlist;
		if(storeFields){
			for(var i=0;i<list.length;i++){//处理数据
				if(list[i]=='工号*'||list[i]=='序号*')//序号行 
					continue;
				var obj={};
				obj['subName']=list[i];
				for(var j=0;j<storeFields.length;j++){
					if(storeFields[j]===""||storeFields[j]==='subName')
						continue;
					var map=me.sub_filedMap[storeFields[j]];
					obj[storeFields[j]]=map[list[i]];
				}
				store.insert(i,obj);
			}
			columns=[];
			for(var j=0;j<storeFields.length;j++){//处理gridpanel column
				if(storeFields[j]==="")
					continue;
				if(storeFields[j]==='subName'){
					columns.push({ text: '云代表作摘要', dataIndex: 'subName',width:'20%' });
				}else{
					columns.push(
					{ 
					  text: me.sub_filedColMap[storeFields[j]], 
					  dataIndex:""+storeFields[j]+"",
					  width:'20%',
					  editor: {xtype:'textfield'},
					  renderer:function(value, metaData,record,rowIndex,colIndex,store,view){
						  if(value&&value.indexOf('`')>-1){
							  value=value.split("`")[1];
						  }
						  return value;
			          },
				     });
				}
				
			}
			
		}else{
			for(var i=0;i<list.length;i++){
				store.insert(i,
								{
								'subName':list[i],
								 width:'20%',
								"desc":''
								}
							);
			}
			columns=[ 
				{ text: '云代表作摘要', dataIndex: 'subName',width:'20%' },
		        { text: '子集指标', dataIndex: 'desc',width:'10%' }
				];
		}
		var gridPanel=Ext.create('Ext.grid.Panel', {
			    store: Ext.data.StoreManager.lookup('template_field_Store'),
			    columns:columns,
			    id:'template_field_panel',
			    columnLines:true,
		   		rowLines:true,
			    height:240,
			    width:'89%',
			    plugins: {
			    	ptype: 'cellediting',
			        clicksToEdit: 1
			    },
			    listeners:{
			    	edit:function(editor,e,opt){
			    		var map=me.sub_filedMap[e.field];
			    		map[e.record.get('subName')]=e.record.get(e.field);
			    		e.record.commit();
			    	},
			    	render:function(panel){
			    		var view = gridPanel.getView();
			    		 var tips = Ext.create('Ext.tip.ToolTip', {
			    		     target: panel.body,
			    		     delegate: view.itemSelector,
			    		     bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
			    		     trackMouse: true,
			    		     listeners: {
			    		         beforeshow: function updateTipBody(tip) {
			    		        	 var subName=view.getRecord(tip.triggerElement).get("subName");
			    		        	 if(subName=="影响因子"||subName=="他引次数"){
			    		        		  tips.update("<div style='white-space:nowrap;overflow:hidden;'>"+
	    		        					"<div>指标类型选择必须为数值型指标且指标内容大于0!</div>"
	    					        	+"</div>");
			    		        	 }else if(subName=="发表或出版时间"){
			    		        		 tips.update("<div style='white-space:nowrap;overflow:hidden;'>"+
			    		        					"<div>指标类型选择必须为日期型指标!</div>"
			    					        	+"</div>");
			    		        	 } else{
			    		        		 tips.update("<div style='white-space:nowrap;overflow:hidden;'>"+
			    		        					"<div>"+view.getRecord(tip.triggerElement).get("subName")+"</div>"
			    					        	+"</div>");
			    		        	 }
			    		         }
			    		     }
			    		 });
					},
			    	beforeedit:function(editor, e, eOpts){
			    		var colkey=e.field;
			    		var fieldlist=me.subUnionMap[colkey];
			    		if(fieldlist&&fieldlist.length>0){
			    			var store=Ext.create('Ext.data.Store',{
			    				fields:['field','value']
			    			})
			    			for(var i=0;i<fieldlist.length;i++){
			    				if(fieldlist[i].split("`")[0]=='attach'){
			    					continue;
			    				}
			    				if(e.record.get("subName").replace("*","")=="发表或出版时间"||
			    						e.record.get("subName").replace("*","")=="影响因子"||
			    						e.record.get("subName").replace("*","")=="他引次数"){
			    					if(e.record.get("subName").replace("*","")=="发表或出版时间"){
				    					if(fieldlist[i].split("`")[2]!="D"){
				    						continue;
				    					}
				    				}
			    					if(e.record.get("subName").replace("*","")=="影响因子"||
				    						e.record.get("subName").replace("*","")=="他引次数"){
				    					if(fieldlist[i].split("`")[2]!="N"){
				    						continue;
				    					}
				    				}
			    				}else{
			    					if(fieldlist[i].split("`")[2]=="D"||fieldlist[i].split("`")[2]=="N"){
			    						continue;
			    					}
			    				}
			    				
			    				store.insert(i,{
			    					"field":fieldlist[i],"value":fieldlist[i].split("`")[1]
			    				})
			    			}
			    			e.column.setEditor({
			    				xtype:"combo",
			    				store:store,
			    				queryMode: 'local',
			    				displayField: 'value',
			    				valueField: 'field',
			    				listeners:{
			    					focus:function(f, event, eOpts){
										f.setValue(f.getValue());
										if(f.getValue()&&f.getValue()!=null&&f.getValue().indexOf('`')>-1)
											f.setRawValue(f.getValue().split("`")[1]);
									}
			    				}
			    			});
			    		}else{
			    			Ext.showAlert('子集"'+me.sub_filedColMap[colkey]+'"勾选指标不一致，请调整设置！');
			    		}
			    		
			    	}
			    }
			});
		
		return gridPanel;
	
	},
	listeners:{
		beforedestroy:function(e,opt){
			var me=this;
			var map=new HashMap();
			map.put("flag","thirdStep");
			map.put("type","1");
			map.put("temlate_Param",me.temlate_Param);
			map.put("sub_filedMap",me.sub_filedMap);
			Rpc({functionId:'ZC00004011',async:false,success:function(res){
				var res=Ext.decode(res.responseText);
				if(res.status){
					return;
				}else{
					Ext.showAlert(res.eMsg);
				}
			},scope:me},map);
    	},
    	destroy:function(){
    		var me=this;
    		var map=me.sub_filedMap;
    		for(var key in map){
    			var obj=map[key];
    			if(obj["论文或专著名称*"]==null||obj["论文或专著名称*"]==""){
    				Ext.showAlert(zc.label.masterErrorMsg)
    			}
    		}
    		
    	}
	}
	
});