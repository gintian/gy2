/**
 * 申报人信息匹配
 */
Ext.define('ConfigFileURL.MatchingInfoConfig',{
	extend:'Ext.container.Container',
	width:700,
	height:500,
	margin:'10 45 10 45',
	xtype:'matchinginfoconfig',
	constructor:function(config) {
		this.callParent();
		matchinginfo = this;
		this.init();
	},
	listeners:{
		beforedestroy:function(scope,opt){
			var map=new HashMap();
			map.put("flag","secStep");
			map.put("type","2");
			map.put("fieldmappingmap",matchinginfo.fieldmappingmap==null?{}:matchinginfo.fieldmappingmap);
			map.put("codesetmap",matchinginfo.codesetmap==null?{}:matchinginfo.codesetmap);
				Rpc({functionId:'ZC00004011',async:false,success:function(res){
   				var res=Ext.decode(res.responseText);
   				if(res.status){
   					return;
   				}else{
   					Ext.showAlert(res.errMsg);
   				}
   			},scope:masterpiece},map);
		},
		destroy:function(e,opt){
			var map=matchinginfo.fieldmappingmap;
				if (map["姓名*"] == null || map["姓名*"] == ''
					|| map["一级学科*"] == null
					|| map["一级学科*"] == ''
					|| map["申报级别*"] == null
					|| map["申报级别*"] == ''
					|| map["现专业技术职务*"] == null
					|| map["现专业技术职务*"] == ''
					|| map["申报级别*"] == null
					|| map["申报级别*"] == ''
					|| map["申报专业技术职务*"] == null
					|| map["申报专业技术职务*"] == ''
					|| map["Email*"] == null
					|| map["Email*"] == '') {
					 Ext.showAlert(zc.label.masterErrorMsg)
			}
		},
		afterrender:function(){
			
		}
	},
	init:function(){
		var store = Ext.create('Ext.data.Store', {
		    fields:['hjcloudfieldname', 'systemfield','matching','fieldvalue','codesetid','fieldtype']
		});
		if(!matchinginfo.fieldmappingmap){
			matchinginfo.fieldmappingmap = {};
		}
		if(!matchinginfo.codesetmap){
			matchinginfo.codesetmap = {};
		}
		//从第一步得到选择的模板号
		var hashvo = new HashMap();
		hashvo.put("flag","secStep");
		hashvo.put("type","0");
		Rpc({functionId:'ZC00004011',async:false,success:function(form,action){
			   var result = Ext.decode(form.responseText);
			   if(!result.succeed){
				   Ext.showAlert(result.message);
				   return;
			   }else{
				   matchinginfo.fieldlist = result.list;
				   matchinginfo.gridlist = result.gridlist;
				   matchinginfo.codemap = result.codemap;
				   matchinginfo.fieldmappingmap = result.fieldmappingmap;
				   matchinginfo.codesetmap = result.codesetmap;
			   }
		}}, hashvo);
		var fieldStore = Ext.create("Ext.data.Store", {
		    fields: ["fieldname", "fieldvalue",'fieldtype','codesetid']
		});
		var idNum_Store = Ext.create("Ext.data.Store", {
		    fields: ["fieldname", "fieldvalue",'fieldtype','codesetid']
		});
		Ext.each(matchinginfo.fieldlist,function(obj,index){
			fieldStore.insert(index,{fieldname:obj.fieldname,fieldvalue:obj.fieldvalue+"`"+obj.fieldtype+"`"+obj.codesetid});
		});
		var idNum_flag=true;
		Ext.each(matchinginfo.fieldlist,function(obj,index){
			if(obj.fieldtype=="A"&&obj.codesetid=='0'){
				if(obj.fieldname=="唯一标识"){
					idNum_flag=false;
				}
				idNum_Store.insert(index,{fieldname:obj.fieldname,fieldvalue:obj.fieldvalue+"`"+obj.fieldtype+"`"+obj.codesetid});
			}
		});
		if(idNum_flag){//工号没有唯一标识指标添加虚拟唯一标识参数
			idNum_Store.insert(0,{"fieldname":"唯一标识", "fieldvalue":"唯一标识",'fieldtype':"",'codesetid':"0"});
		}
		Ext.each(matchinginfo.gridlist,function(obj,index){
			var strRecord ={};
			strRecord["hjcloudfieldname"]=obj;
			var fieldmapping = "";
			var systemfield = "";
			var fieldvalue = "";
			var codesetid = "";
			var fieldtype = "";
			if(matchinginfo.fieldmappingmap){
				fieldmapping = matchinginfo.fieldmappingmap[obj];
				if(fieldmapping){
					systemfield = fieldmapping.split("`")[0];
					fieldvalue = fieldmapping.split("`")[1];
					fieldtype = fieldmapping.split("`")[2];
					codesetid = fieldmapping.split("`")[3];
				}
			}
			strRecord["systemfield"]=systemfield;
			strRecord["matching"]='';
			strRecord["fieldvalue"]=fieldvalue;
			strRecord["codesetid"]=codesetid;
			strRecord["fieldtype"]=fieldtype;
			store.add(strRecord);
		});
		var grid = Ext.create('Ext.grid.Panel', {
		    store:store,
		    selModel: '',
		    columns: [
		        {text: '云指标名称',menuDisabled:true,sortable:false, dataIndex: 'hjcloudfieldname', flex: 1},
		        {text: '系统指标',menuDisabled:true,sortable:false, dataIndex: 'systemfield',align:'center', flex: 1,
		        	editor:{xtype:'combobox',
		                store: fieldStore,
		                editable: true,
		                displayField: "fieldname",
		                valueField: "fieldvalue",
		                queryMode: "local",
		                listeners:{
		       				select:function(combo,record){
		       					matchinginfo.fieldname =  record.data.fieldname;
		       					if(record.get("fieldname")=="唯一标识"){
		       						matchinginfo.fieldtype="0";
		       						matchinginfo.codesetid="0";
		       						matchinginfo.fieldvalue="唯一标识";
		       					}else{
		       						var value = record.data.fieldvalue;
			       					matchinginfo.fieldtype = value.split("`")[1];
			       					matchinginfo.codesetid = value.split("`")[2];
			       					matchinginfo.fieldvalue = value.split("`")[0];
		       					}
		       					
		       				}
		       			}
		        	},
		        	renderer:function(value, metaData, record, rowIndex){
		        		if(value!=''&&value!=null&&(record.get("hjcloudfieldname")!="工号"||value!="唯一标识")){
		        			var hjcloudfieldname = record.data.hjcloudfieldname;//当前行第一列的值
       						if(hjcloudfieldname.indexOf("*")!=-1){
       							hjcloudfieldname = hjcloudfieldname.substring(0,hjcloudfieldname.length-1);
       						}
       						var cloudcodelist = matchinginfo.codemap[hjcloudfieldname];
		        			if(value.indexOf("`")!=-1){
	       						if(matchinginfo.codesetid!='0'&&cloudcodelist&&cloudcodelist.length>0){//云平台代码项
	       							matchinginfo.rowIndex = rowIndex;
	       							if(!matchinginfo.fieldmappingmap){
	    								matchinginfo.fieldmappingmap = {};
	    							}
	       							record.data.codesetid = matchinginfo.codesetid;
	       							record.data.fieldvalue = matchinginfo.fieldvalue;
	       							record.data.fieldtype = matchinginfo.fieldtype;
	       							matchinginfo.fieldmappingmap[record.data.hjcloudfieldname]=matchinginfo.fieldname+"`"+record.data.fieldvalue+"`"+record.data.fieldtype+"`"+record.data.codesetid;
	       						}else{
	       							record.data.codesetid = matchinginfo.codesetid;
	       							record.data.fieldvalue = matchinginfo.fieldvalue;
	       							record.data.fieldtype = matchinginfo.fieldtype;
	       							matchinginfo.fieldmappingmap[record.data.hjcloudfieldname]=matchinginfo.fieldname+"`"+record.data.fieldvalue+"`"+record.data.fieldtype+"`"+record.data.codesetid;
	       							matchinginfo.rowIndex = -1;
	       						}
	       					}else{
	       						matchinginfo.rowIndex = -1;
	       					}
		        			if(matchinginfo.fieldname){
		        				return matchinginfo.fieldname;
		        			}else{
		        				if(cloudcodelist&&cloudcodelist.length>0)
		        					matchinginfo.rowIndex = rowIndex;
		        				else
		        					matchinginfo.rowIndex = -1;
		        				return value;
		        			}
		        		}else if(record.get("hjcloudfieldname")=="工号"&&value=="唯一标识"){
		        			matchinginfo.fieldmappingmap["工号"]="唯一标识`唯一标识`0`0";
		        			return value;
		        		}else{
		        			matchinginfo.rowIndex = -1;
		        			if(value==null){
		        				matchinginfo.fieldmappingmap[record.get("hjcloudfieldname")]="``0`0";
		        				value="";
		        			}
		        			return value;
		        		}
			        }
		        },
		        { text: '代码匹配',menuDisabled:true,sortable:false, dataIndex: 'matching',align:'center', flex: 1, 
		          renderer:function(value, metaData, record, rowIndex){
		        	  var htm = "";
		        	  var hjcloudfieldname = record.data.hjcloudfieldname;//当前行第一列的值
		        	  if(hjcloudfieldname.indexOf("*")!=-1){
						  hjcloudfieldname = hjcloudfieldname.substring(0,hjcloudfieldname.length-1);
					  }
		        	  var cloudcodelist = matchinginfo.codemap[hjcloudfieldname];
		        	  if(matchinginfo.rowIndex==rowIndex&&cloudcodelist.length>0){
		        		  var codesetid = record.data.codesetid;
		        		  htm = "<a href=javascript:matchinginfo.matchingInfo('"+hjcloudfieldname+"','"+codesetid+"');><img src="+rootPath+"/images/table.gif border=0></a>"; 
		        	  }
		        	  return htm;
		          }
		        },
		        { text: '系统指标code',menuDisabled:true,sortable:false, dataIndex: 'fieldvalue', flex: 1,hidden:true},
		        { text: '代码',menuDisabled:true,sortable:false, dataIndex: 'codesetid', flex: 1,hidden:true},
		        { text: '指标类型',menuDisabled:true,sortable:false, dataIndex: 'fieldtype', flex: 1,hidden:true}
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
		    width:  700,
		    columnLines:true,
	   		rowLines:true,
	    	border:1,
	    	listeners:{
	    		beforeedit:function(editor, e, eOpts){
	    			var com=e.column.getEditor();
	    			var store=com.getStore();
	    			if(e.record.get("hjcloudfieldname")==="工号"){
	    				com.setStore(idNum_Store);
	    			}else{
	    				com.setStore(fieldStore);
	    				if(com.getStore().getAt(0).get("fieldname")==="唯一标识"){
	    					com.getStore().removeAt(0);
	    				}
	    			}
	    		}
	    	}
		});
		this.add({xtype:'label',height:30,text:'申报人字段匹配',style:'font-size:14px;line-height:30px;'});
		this.add(grid);
		/*masterpiece.secStep = Ext.widget('container',{
			width:700,
			height:500,
			margin:'10 45 10 45',
			items:[{xtype:'label',text:'申报人字段匹配'},grid]
		})
		if(matchinginfo.callBackFunc){
            Ext.callback(eval(matchinginfo.callBackFunc),null,[masterpiece.secStep]);
		}*/
	},
	matchingInfo:function(cloudfieldname,codesetid){
		var store = Ext.create('Ext.data.Store', {
		    fields:['hjcloudfieldname', 'systemcode']
		});
		var cloudcodelist = matchinginfo.codemap[cloudfieldname];
		var systemfieldlist = new Array();
		if(matchinginfo.codesetmap){
			systemfieldlist = matchinginfo.codesetmap[codesetid];
		}
		Ext.each(cloudcodelist,function(obj,index){
			var strRecord ={};
			var n = parseInt(obj.split("`")[0]);
			var kongge = "";
			for(var i=0;i<(n-1)*2;i++){
				kongge+="&nbsp;";
			}
			var hjcloudfieldname = kongge+obj.split("`")[1];
			strRecord["hjcloudfieldname"]=hjcloudfieldname;
			var systemfield = "";
			if(systemfieldlist){
				for(var j=0;j<systemfieldlist.length;j++){
					var systemfieldmap = systemfieldlist[j];
					var value = systemfieldmap[hjcloudfieldname];
					if(value){
						systemfield = value;
						break;
					}
				}
			}
			strRecord["systemcode"]=systemfield;
			store.add(strRecord);
		});
		
		var hashvo = new HashMap();
		hashvo.put("codesetid",codesetid);
		hashvo.put("flag","secStep");
		hashvo.put("type","1");
		Rpc({functionId:'ZC00004011',async:false,success:function(form,action){
			   var result = Ext.decode(form.responseText);
			   if(!result.succeed){
				   Ext.showAlert(result.message);
				   return;
			   }else{
				   matchinginfo.codelist=result.list;
			   }
		}}, hashvo);
		/*var fieldStore = Ext.create("Ext.data.Store", {
		    fields: ["codeitemdesc", "codeitemid"]
		});
		
		Ext.each(matchinginfo.codelist,function(obj,index){
			fieldStore.insert(index,{codeitemdesc:obj.codeitemdesc,codeitemid:obj.codeitemid});
		});*/
		var grid = Ext.create('Ext.grid.Panel', {
			id:'matchinggrid',
			store:store,
		    selModel: '',
		    columns: [
		        {text: '云指标名称',menuDisabled:true,sortable:false, dataIndex: 'hjcloudfieldname', flex: 1},
		        {text: '系统代码',menuDisabled:true,sortable:false, dataIndex: 'systemcode',align:'center', flex: 1,
		        	editor:{
		             	xtype:"codecomboxfield",codesetid:codesetid,ctrltype:'0'
		          },
		        	renderer:function(value, metaData, record, rowIndex){
		        		if(value==undefined||value==''){
       						var hjcloudfieldname = record.data.hjcloudfieldname;
       						//得到层级
       						var layer = "1";
	    					if(hjcloudfieldname.indexOf("&nbsp;")==-1) {
	    						layer = "1";
	    					}else {
	    						var c = "&nbsp;";
	    						var ch = hjcloudfieldname.split("&nbsp;");
	    						var t = 0;
	    						for (var j = 0; j < ch.length; j++) {
	    						    var s = ch[j];
	    						    if (s=="") {
	    						        t++;
	    						    }
	    						}
	    						if(t%2==0) {
	    							layer=(t/2+1)+"";
	    						}
	    					}
       						if(matchinginfo.codelist){
       							for(var i=0;i<matchinginfo.codelist.length;i++){
           							var codeitemdesc = matchinginfo.codelist[i].codeitemdesc;
           							var codeitemid = matchinginfo.codelist[i].codeitemid;
           							var layer_ = matchinginfo.codelist[i].layer;
           							hjcloudfieldname = replaceAll(hjcloudfieldname,"&nbsp;","");
           							if(hjcloudfieldname==codeitemdesc&&layer==layer_){
           								value = codeitemdesc;
           								record.data.systemcode = codeitemid+"`"+codeitemdesc;
           								break;
           							}
           						}
       						}
		        			return value;
		        		}else{
		        			return value.split("`")[1];
		        		}
			        }
		        }],
		        viewConfig:{
			    	markDirty:false
			    },
			    plugins:[
			         Ext.create('Ext.grid.plugin.CellEditing',{
			        	 clicksToEdit:1
			         })
			    ],
		        height: 500,
			    width:  400,
			    columnLines:true,
		   		rowLines:true,
		    	border:1
		});
		var matchingwin = Ext.widget('window',{
			title:'申报类型匹配',
			items:[grid],
			modal:true,
			buttonAlign:'center',
			buttons:[{
				text:"确认",
				handler: function() {
					Ext.showConfirm("请确认申报材料的"+cloudfieldname+"代码与云平台相匹配!",function(optional){
						if(optional=='yes'){
							var store = Ext.getCmp('matchinggrid').getStore();
							var items = store.data.items;
							if(!matchinginfo.codesetmap){
								matchinginfo.codesetmap = {};
							}
							var list = new Array();
							for(var i=0;i<items.length;i++){
								var data = items[i].data;
								var hjcloudfieldname = data.hjcloudfieldname;
								var systemcode = data.systemcode;
								var map = {};
								if(systemcode!=undefined&&systemcode!=''){
									map[hjcloudfieldname]=systemcode;
									list.push(map);
								}
							}
							if(list.length>0){
								matchinginfo.codesetmap[codesetid]=list;
							}
							matchingwin.close();
						}else
							return;
					})
				}
			}]
		});
		matchingwin.show();
	}
})