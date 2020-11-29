Ext.define('ConfigFileURL.OtherMaterialSettings',{
	extend:'Ext.container.Container',
	width:700,
	height:500,
	margin:'10 45 10 45',
	xtype:'othermaterialsettings',
	constructor:function(config) {
		this.callParent();
		othermaterial_me = this;
		this.init();
	},
	listeners:{
		beforedestroy:function(scope,opt){
			var map=new HashMap();
			map.put("flag","fourthStep");
			map.put("type","1");
			map.put("othermaterialsobj",othermaterial_me.othermaterialsobj==null?{}:othermaterial_me.othermaterialsobj);
				Rpc({functionId:'ZC00004011',async:false,success:function(res){
   				var res=Ext.decode(res.responseText);
   				if(res.status){
   					return;
   				}else{
   					Ext.showAlert(res.errMsg);
   				}
   			},scope:masterpiece},map);
		},
		afterrender:function(){
			
		}
	},
	init:function(){
		var hashvo = new HashMap();
		hashvo.put("flag","fourthStep");
		hashvo.put("type","0");
		Rpc({functionId:'ZC00004011',async:false,success:function(form,action){
			   var result = Ext.decode(form.responseText);
			   if(!result.succeed){
				   Ext.showAlert(result.message);
				   return;
			   }else{
				   othermaterial_me.othermateria = result.othermateria;
				   othermaterial_me.othermaterialsobj = result.other_materialsmap;
			   }
		}}, hashvo);
		var store = Ext.create('Ext.data.Store', {
		    fields:['tabname', 'tabid','pagedesc','operation','pageid','ids']
		});
		for(var key in othermaterial_me.othermateria){
			var strRecord ={};
			var pageid = othermaterial_me.othermateria[key];
			var tabid = key.split(".")[0];
			var pagedesc = "";
			var ids = "";
			if(othermaterial_me.othermaterialsobj){
				var value = othermaterial_me.othermaterialsobj[tabid];
				if(value){
					pagedesc = value.split("`")[1];
					ids = value.split("`")[0];
				}
			}
			strRecord["tabname"]=key;
			strRecord["tabid"]=tabid;
			strRecord["pagedesc"]=pagedesc;
			strRecord["pageid"]=pageid;
			strRecord["ids"]=ids;
			store.add(strRecord);
		}
		
		var grid = Ext.create('Ext.grid.Panel', {
			id:'othermateris',
			store:store,
		    selModel: '',
		    columns: [
		        {text: '模板名称',menuDisabled:true,sortable:false, dataIndex: 'tabname', flex: 1},
		        {text: '页签序号',menuDisabled:true,sortable:false, dataIndex: 'pagedesc',align:'center', flex: 1
		        },{text:'操作',menuDisabled:true,sortable:false, dataIndex: 'operation',align:'center', flex: 1,
		        	renderer:function(value, metaData, record, rowIndex){
		        		var pageid = record.data.pageid;
		        		var pagedesc = record.data.pagedesc;
		        		var ids = record.data.ids;
		        		var tabid = record.data.tabid;
		        		var htm = "<a href=javascript:othermaterial_me.othermaterial('"+pageid+"','"+rowIndex+"','"+pagedesc+"','"+ids+"','"+tabid+"');><img src="+rootPath+"/images/table.gif border=0></a>"; 
		        		return htm;
			        }
		        },{
		        	text: '隐藏页签',menuDisabled:true,sortable:false, dataIndex: 'pageid', flex: 1,hidden:true
		        },
		        {
		        	text: '隐藏页签',menuDisabled:true,sortable:false, dataIndex: 'ids', flex: 1,hidden:true
		        },
		        {
		        	text: '隐藏页签',menuDisabled:true,sortable:false, dataIndex: 'tabid', flex: 1,hidden:true
		        }],
		        viewConfig:{
			    	markDirty:false
			    },
			    plugins:[
			         Ext.create('Ext.grid.plugin.CellEditing',{
			        	 clicksToEdit:1
			         })
			    ],
			    height: 420,
			    width:  700,
			    columnLines:true,
		   		rowLines:true,
		    	border:1
		});
		this.add({xtype:'label',height:30,text:'选择申报材料模板要输出的页签',style:'font-size:14px;line-height:30px;'});
		this.add(grid);
		/*masterpiece.fourthStep = Ext.widget('container',{
			width:700,
			height:500,
			margin:'10 45 10 45',
			items:[{xtype:'label',text:'选择申报材料模板要输出的页签'},grid]
		})
		if(othermaterial_me.callBackFunc){
            Ext.callback(eval(othermaterial_me.callBackFunc),null,[masterpiece.fourthStep]);
		}*/
	},
	othermaterial:function(pageid,rowIndex,pagedesc,ids,tabid){
		var pagetitle = pageid.split(",");
		var store = Ext.create('Ext.data.Store', {
		    fields:['name', 'id']
		});

		for(var i=0;i<pagetitle.length;i++){
			var strRecord ={};
			var value = pagetitle[i];
			strRecord["name"]=value.split("`")[1];
			strRecord["id"]=value.split("`")[0];
			store.add(strRecord);
		}
		var sm = Ext.create('Ext.selection.CheckboxModel',{
			renderer:function(value,metaData,record){//渲染每行是否显示多选框
				return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="button" tabIndex="0">&#160;</div>';
			}
		});
		var grid = Ext.create('Ext.grid.Panel', {
			store:store,
		    selModel: sm,
		    columns: [
		        {text: '页签名称',menuDisabled:true,sortable:false, dataIndex: 'name', flex: 1},
		        {text: '页签序号',menuDisabled:true,sortable:false, dataIndex: 'id', flex: 1,hidden:true}
		        ],
		        viewConfig:{
			    	markDirty:false
			    },
			    plugins:[
			         Ext.create('Ext.grid.plugin.CellEditing',{
			        	 clicksToEdit:1
			         })
			    ],
			    height: 420,
			    width:  250,
			    columnLines:true,
		   		rowLines:true,
		    	border:1
		});
		var win = Ext.widget('window',{
			title:'选择页签',
			items:[grid],
			modal:true,
			buttonAlign:'center',
			buttons:[{
				text:"确认",
				handler: function() {
						var record = grid.getSelectionModel().getSelection();
						var pagedesc = "";
						var pageid = "";
						for(var i=0;i<record.length;i++){
							var name = record[i].data.name;
							var id = record[i].data.id;
							if(i==0){
								pagedesc = name;
								pageid = id;
							}else{
								pagedesc += ","+name;
								pageid += ","+ id;
							}
						}
						var record = Ext.getCmp('othermateris').getStore().getAt(rowIndex);
						record.set('pagedesc',pagedesc);
						record.set('ids',pageid);
						if(!othermaterial_me.othermaterialsobj){
							othermaterial_me.othermaterialsobj = {};
						}
						othermaterial_me.othermaterialsobj[tabid]=pageid+"`"+pagedesc;
						win.close();
					}
			}]
		});
		win.show();
		var selected = [];
		grid.getStore().each(function(r){
		   if(ids.indexOf(r.get('id'))>-1)
		   selected.push(r);
		      
		});
		var model = grid.getSelectionModel();
		model.select(selected);
	}
})