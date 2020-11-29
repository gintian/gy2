/**
 * 投票方式_配置_结果归档
 * @createtime 18/4/4
 * @author xus
 * 
 * */
Ext.define('ConfigFileURL.VoteWaySetting',{
	selectedColumns:'',
	totalData:[],
	leftData:[],
	rightData:[],
	itemMap:undefined,
	constructor:function(config) {
		//xus 18/4/11 解决 ie 不支持indexOf() 问题
		if(!Array.prototype.indexOf){    
		   Array.prototype.indexOf = function(val){    
		       var value = this;    
		       for(var i =0; i < value.length; i++){    
		          if(value[i] == val) return i;    
		       }    
		      return -1;    
		   };    
		} 
		votegloble=this;
		//左侧框数据
		leftStore = Ext.create('Ext.data.Store', {
		    fields:[ 'itemdesc'],
		    data:[]
		});
		//右侧框数据
		rightStore = Ext.create('Ext.data.Store', {
		    fields:[ 'name'],
		    data: []
		});
		var map = new HashMap();
		var data,columns;
		Rpc({
			functionId : 'ZC00004006',
			success:function(res){
				var respon = Ext.decode(res.responseText);
				votegloble.totalData=respon.fieldList;
				votegloble.selectedColumns=respon.voteColumns!=''?respon.voteColumns.toUpperCase():'';
				votegloble.setItemMap();
				votegloble.setCompData();
				votegloble.setStoreLists();
				votegloble.getMainWin();
			}
		}, map);
	},
	//设置itemMap
	setItemMap:function(){
		this.itemMap=new HashMap();
		for(var i=0;i<this.totalData.length;i++){
			this.itemMap.put(this.totalData[i].itemid.toUpperCase(),i);
		}
	},
	//根据selectedColumns获取右框数据
	setCompData:function(){
		this.leftData=this.totalData.slice(0);
		this.rightData=[];
		if(this.selectedColumns==''){
			this.rightData=[];
			return;
		}
		var columnsarr=this.selectedColumns.split(",");
		var currentindex=0;
		for(var ke=0;ke<columnsarr.length;ke++){
			var column=columnsarr[ke].toUpperCase();
			if(this.totalData[this.itemMap.get(column)]!=undefined){
				this.rightData[currentindex]=this.totalData[this.itemMap.get(column)];
				this.leftData.splice(this.leftData.indexOf(this.rightData[currentindex]),1);
				currentindex++;
			}
		}
	},
	//设置两个框中的数据
	setStoreLists:function(){
		rightStore.setData(this.rightData);
		leftStore.setData(this.leftData);
	},
	// 窗口配置页面
	getMainWin:function(){
		
		Ext.create('Ext.window.Window', {
			id:'VoteWaySetting',
			modal:true,
		    title: zc.msg.listinfo,
			modal: true,
			layout: {
			    type: 'hbox',
			    align : 'stretch',
			},
			width:560,
			height:420,
			border:false,
			buttonAlign:'center',
		    buttons: [{
		    	//保存按钮
		        text: zc.label.save,
		        handler: function() {
		        	votegloble.toSetColumns();
		        	var flag=configFile_me.saveVoteType('2',votegloble.selectedColumns);
		        	if(flag)
		        		Ext.showAlert(zc.msg.savesuccess,function(){  
					           Ext.getCmp('VoteWaySetting').close();
					    });
		        	else
		        		Ext.showAlert(zc.msg.savedefault);
		        },
		        scope:this
		    }],
		    items:[{
		    	//左侧材料汇总信息表框
		    	title:zc.msg.summaryinfotable,
				xtype:'gridpanel',
				id:'leftgridpanel',
				margin:'5 5 5 0',
				width:'44%',
				hideHeaders:true,
			    store: leftStore,
			    selModel:Ext.create('Ext.selection.RowModel',{mode:"SIMPLE"}),
			    columns: [
			        { text: 'itemdesc', dataIndex: 'itemdesc' ,flex: 1 }
			    ],
			    listeners:{
			    	celldblclick:function ( me, td, cellIndex, record, tr, rowIndex, e, eOpts ) {
			    		votegloble.addEvent(record.data.itemid);
			    	}
			    }
			},{
				xtype:'panel',
				width:'12%',
				border:0,
				layout: {
				    type: 'vbox',
				    align : 'middle',
				    pack: 'center'
				},
				items:[{
					//添加按钮
					xtype:'button',
					text:zc.label.add,
					margin:'0 0 5 0',
					width:'80%',
					height:28,
					listeners:{
						click:function ( me, e, eOpts ){
							if(Ext.getCmp('leftgridpanel').selModel.selected.items.length==0)
								return;
							var itemids='';
							for(var i=0 ;i<Ext.getCmp('leftgridpanel').selModel.selected.items.length;i++){
								if(itemids=='')
									itemids=Ext.getCmp('leftgridpanel').selModel.selected.items[i].data.itemid;
								else
									itemids+=','+Ext.getCmp('leftgridpanel').selModel.selected.items[i].data.itemid;
							}
							votegloble.addEvent(itemids);
						}
					}
				},{
					//删除按钮
					xtype:'button',
					text:zc.label.dele,
					width:'80%',
					height:28,
					listeners:{
						click:function ( me, e, eOpts ){
							if(Ext.getCmp('rightgridpanel').selModel.selected.items.length==0)
								return;
							var itemids='';
							for(var i=0; i< Ext.getCmp('rightgridpanel').selModel.selected.items.length;i++){
								if(itemids=='')
									itemids=Ext.getCmp('rightgridpanel').selModel.selected.items[i].data.itemid;
								else
									itemids+=','+Ext.getCmp('rightgridpanel').selModel.selected.items[i].data.itemid;
							}
							votegloble.delEvent(itemids);
						}
					}
				}]
			},{
				//右侧已选指标框
				title:zc.msg.selectedtarget,
				xtype:'gridpanel',
				id:'rightgridpanel',
				margin:'5 5 5 0',
				width:'44%',
				hideHeaders:true,
			    store: rightStore,
			    selModel:Ext.create('Ext.selection.RowModel',{mode:"SIMPLE"}),
			    columns: [
			        { text: 'itemdesc', dataIndex: 'itemdesc' ,flex: 1 }
			    ],
			    viewConfig: {  
		            plugins: {  
		                ptype: "gridviewdragdrop"
		            }  
		        },
			    listeners:{
				    celldblclick:function ( me, td, cellIndex, record, tr, rowIndex, e, eOpts ) {
			    		votegloble.delEvent(record.data.itemid);
			    	}
			    }
			}]
		}).show();
	},
	//触发添加事件
	addEvent:function(itemid){
		if(votegloble.selectedColumns!='')
			votegloble.selectedColumns+=","+itemid.toUpperCase();
		else
			votegloble.selectedColumns+=itemid.toUpperCase();
		votegloble.setCompData();
		votegloble.setStoreLists();
	},
	//触发删除事件
	delEvent:function(itemid){
		var itemids=itemid.split(',');
		var rightitemid;
		for(var i=0; i<itemids.length;i++){
			rightitemid=itemids[i].toUpperCase();
			if(votegloble.selectedColumns.indexOf(rightitemid)==-1)
				continue;
			var newstrarray;
			if(votegloble.selectedColumns.indexOf(rightitemid)==0){
				if(votegloble.selectedColumns!=rightitemid)
					newstrarray=votegloble.selectedColumns.split(rightitemid+',');
				else
					newstrarray=['',''];
			}else{
				newstrarray=votegloble.selectedColumns.split(','+rightitemid);
			}
			votegloble.selectedColumns=newstrarray[0]+newstrarray[1];
		}
		votegloble.setCompData();
		votegloble.setStoreLists();
	},
	//设置新的Columns参数
	toSetColumns:function(){
		var newData=Ext.getCmp('rightgridpanel').selModel.store.data.items;
		if(newData.length==0)
			return;
		var newColumns="";
		for(var i=0;i<newData.length;i++){
			newColumns+=newData[i].data.itemid.toUpperCase();
			if(i<newData.length-1)
				newColumns+=",";
		}
		votegloble.selectedColumns=newColumns;
	}
});