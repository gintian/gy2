//上传代表作模板
Ext.Loader.setPath("SYSF","../../../components/fileupload");
Ext.define("ConfigFileURL.MasterPieceFile",{
	extend:'Ext.container.Container',
	requires:['SYSF.FileUpLoad'],//加载上传控件js
	xtype:'masterPieceFile',
	width:'100%',
	height:'89%',
	margin:'10 45 10 45',
	ids:undefined,
	application_fileName:"",
	representative_fileName:"",
	valueObj:"",
	scrollable:true,
	constructor:function(){
		this.callParent();
		var me=this;
		var map=new HashMap();
		map.put("flag","firstStep");
		map.put("type","4");
		Rpc({functionId:'ZC00004011',async:false,success:function(res){
			var res=Ext.decode(res.responseText);
			if(res.status){
				this.ids=res.ids;
				if(this.ids){
					for(var i=0;i<this.ids.length;i++){
						this.valueObj+=","+this.ids[i];
					}
				}
				this.application_fileName=res.application_fileName;
				this.representative_fileName=res.representative_fileName;
				me.init();
			}else{
				Ext.showAlert(res.errMsg);
			}
		},scope:me},map);
		
	},
	init:function(){
		var me=this;
		me.add(me.adddescPanel());
		me.add(me.addMainCenter());
		if(this.ids){
			for(var i=0;i<this.ids.length;i++){
				Ext.getCmp("template_id").add(this.createtemPanel(this.ids[i]));
			}
		}
	},
	listeners:{
		beforedestroy:function(scope,opt){
			var me=this;
				var map=new HashMap();
					map.put("flag","firstStep");
					map.put("type","3");
					map.put("tempid",Ext.getCmp("template_id").valueObj);
 				Rpc({functionId:'ZC00004011',async:false,success:function(res){
	   				var res=Ext.decode(res.responseText);
	   				if(res.status){
	   					return;
	   				}else{
	   					Ext.showAlert(res.errMsg);
	   				}
	   			},scope:me},map);
		},
		afterrender:function(){
			
		}
	},
	adddescPanel:function(){
		return	Ext.create('Ext.container.Container',{
			width:'100%',
			height:45,
			style:'font-size:14px;',
			html:'<div>说明：本操作依据导入模板生成云平台代表作与HR系统指标匹配关系</div>'
		});
		
	},
	imgPanel:function(value){
		return Ext.widget('container',{
					border:0,
					html:'<div style="width:48px;height:48px;float:left;background:url(../images/reviewmeeting/yuan1.png);text-align:center;line-height:48px;color:white;"><span style=\"font-size:16pt\">'+value+'</span></div>'
				});
	},
	firstPanel:function(){
		var me=this;
		var first=Ext.widget('container',{
			width:'100%',
			height:'20%',
		    html:'<div style="margin-left:20px;float:left;line-height:48px;text-align:center;">请通过云平台同行评议下载 申报人和代表作模板</div>'
		});
		return first;
	},
	secPanel:function(){
		var me=this;
		var sec=Ext.widget('container',{
			width:'100%',
			height:'20%',
			layout:'vbox',
			items:[
				{
					border:0,
					html:'<div style="margin-left:20px;float:left;line-height:20px;text-align:center;">上传申报人基础信息模板</div>'
				},
				{
				layout:'hbox',
				border:0,
				height:30,
				style:'margin-left:20px',
				items:[
					{xtype:'textfield',width:'200',id:'application_file',editable:false },
					{xtype:'button',
					 height:22,
					 id:'application_id',
					 style:'margin-left:20px',
					 text:zc.label.masterpieceChoseFile,
					 listeners:{
						 afterrender:function(scope,e,opt){
							 Ext.widget("fileupload",{
			  	   					upLoadType:3,
			  	   					height:20,width:40,
			  	   					buttonText:'',
			  	   					style:'position:relative;top:-20px',
			  	   					fileExt:"*.xls;*.xlsx",//添加对上传文件类型控制
			  	   					renderTo:"application_id",
			  	   					success:function(list){
			  	   						var map=new HashMap();
			  	   						Ext.getCmp("application_file").setValue(list[0].localname);
			  	   						map.put("flag","firstStep");
			  	   						map.put("type","1");
			  	   						map.put("fullpath",list[0].fullpath);
			  	   						map.put("localname",list[0].localname);
				  	   					Rpc({functionId:'ZC00004011',async:false,success:function(res){
				  	   					var res=Ext.decode(res.responseText);
				  	   					if(res.status){
				  	   						return;
				  	   					}else{
				  	   						Ext.showAlert(res.errMsg);
				  	   					}
				  	   				},scope:me},map);
			  	   						//
			  	   					},
			  	   					callBackScope:'',
			  	   					savePath:''
			  	   				});
						 }
					 }
					}
				]
				}
			]
		});
		if(this.application_fileName!=""){
			Ext.getCmp("application_file").setValue(this.application_fileName);
		}
		return sec;
	},
	thirdPanel:function(){
		var me=this;
		var third=Ext.widget('container',{
			width:'100%',
			height:'20%',
			layout:'hbox',
			items:[
				{
					layout:'vbox',
					border:0,
					items:[
						{border:0,html:'<div style="margin-left:20px;float:left;line-height:20px;text-align:center;">上传代表作摘要模板</div>'},
						{
							layout:'hbox',
							border:0,
							height:30,
							style:'margin-left:20px',
							items:[
								{xtype:'textfield',width:'200',id:'representative_file',editable:false },
								{xtype:'button',
								 id:'representative_id',
								 style:'margin-left:20px',
								 height:22,
								 text:zc.label.masterpieceChoseFile,
								 listeners:{
									 afterrender:function(scope,e,opt){
										 Ext.widget("fileupload",{
						  	   					upLoadType:3,
						  	   					height:20,width:40,
						  	   					buttonText:'',
						  	   					style:'position:relative;top:-20px',
						  	   					fileExt:"*.xls;*.xlsx",//添加对上传文件类型控制
						  	   					renderTo:"representative_id",
						  	   					success:function(list){
						  	   					Ext.getCmp('representative_file').setValue(list[0].localname);
								  	   				var map=new HashMap();
						  	   						map.put("flag","firstStep");
						  	   						map.put("type","2");
						  	   						map.put("fullpath",list[0].fullpath);
						  	   						map.put("localname",list[0].localname);
							  	   					Rpc({functionId:'ZC00004011',async:false,success:function(res){
							  	   					var res=Ext.decode(res.responseText);
							  	   					if(res.status){
							  	   						return;
							  	   					}else{
							  	   						Ext.showAlert(res.errMsg);
							  	   					}
							  	   				},scope:me},map);
						  	   				},
						  	   					callBackScope:'',
						  	   					savePath:''
						  	   				});
									 }
								 }}
							]
						}
					]
				}
			]
		});
		if(this.representative_fileName!=""){
			Ext.getCmp("representative_file").setValue(this.representative_fileName);
		}
		return third;
	},
	fourthPanel:function(){
		var me=this;
		var fourth=Ext.widget('container',{
			width:'100%',
			height:'50%',
			layout:'vbox',
			items:[

				{
					border:0,
					layout:'hbox',
					items:[
						{
							border:0,
							html:'<div style="margin-left:20px;float:left;line-height:20px;text-align:center;">选择HR系统代表作申报材料表</div>'
						},
						{
							 xtype:'button',
							 width:Ext.isIE?40:45,
							 style:Ext.isIE?'margin-left:10px;':'margin-left:60px;',
							 listeners:{
								click:function(){
									me.createSelectWindow();
								} 
							 },
							 text:zc.label.masterpieceChoseFile
						}
					]
				},
				{
					 xtype:'container',
					 border:1,
					 id:'template_id',
					 style:'margin-left:20px;',
					 valueObj:this.valueObj,
					 minHeight:50,
					 width:200
				}
			
			]
		});
		return fourth;
	},
	addMainCenter:function(){//主要内容
		var me=this;
		var containPanel=Ext.widget("container",{
			width:'95%',
			minHeight:400,
			layout:{
				type: 'vbox',
			    align: 'center'
			}
		});
		
		var mainPanel=Ext.widget("container",{
			width:"50%",
			minHeight:400,
			layout:{
				type:'table',
				tdAttrs:{
					valign:'top'
				},
				columns:2
			},
			items:[
				me.imgPanel(1),me.firstPanel(),
				{height:20,border:0},{height:20,border:0},
				me.imgPanel(2),me.secPanel(),
				{height:20,border:0},{height:20,border:0},
				me.imgPanel(3),me.thirdPanel(),
				{height:20,border:0},{height:20,border:0},
				me.imgPanel(4),me.fourthPanel()
			]
				
		});
		containPanel.add(mainPanel);
			return containPanel
	},
	createSelectWindow:function(){
		var me=this;
		var window=Ext.create('Ext.window.Window',{
			width:400,
			modal:true,
			title:'选择模板',
			height:500,
			layout:'fit',
			items:[
				{
					xtype:'templateselector',
					width:'100%',
					scrollable:true,
					height:'100%',
					border:false,
					dataType:1,
					listeners:{
						 itemclick:function(obj, record, item, index, e, eOpts){
							 if(record.get("id") == "root")
			       					return;
			       			 if (record.get("leaf")!=null&&!record.get("leaf")){
			       			         return;
			       			 }
			       			 var temp=Ext.getCmp("template_id");
			       			 var value=record.get('text').split(".")[0]+":"+record.get('text').split(".")[1];
			       			if(temp.valueObj.indexOf(","+value)>-1){//输入有相同模板提示
		       					Ext.showAlert("已存在相同申报表，请重新选择");
		       					return;
		       				}
			       			if(temp.valueObj==''){
			       				temp.valueObj=","+value;
			       			}else
			       				temp.valueObj=temp.valueObj+","+value;
			       			temp.add(me.createtemPanel(value));
			  	   			/*var map=new HashMap();
	  	   					map.put("flag","firstStep");
	  	   					map.put("type","3");
	  	   					map.put("tempid",value);
		  	   				Rpc({functionId:'ZC00004011',async:false,success:function(res){
			  	   				var res=Ext.decode(res.responseText);
			  	   				if(res.status){
			  	   					return;
			  	   				}else{
			  	   					Ext.showAlert(res.errMsg);
			  	   				}
			  	   			},scope:me},map);*/
			       			 			
						 }
					 }
				}
				]
		}).show();
	},
	createtemPanel:function(value){
		var me=this;
		var con = Ext.widget('container',{
	          style:{border:"solid #c5c5c5 1px",backgroundColor:'#f8f8f8'},
	          margin:1,
	          width:200,
	          height:19,
	          value:value,
	          layout:'hbox',
	          items:[{xtype:'component',flex:10,
	          	autoEl: {
			        tag: 'div',
			        style:'white-space:nowrap; text-overflow:ellipsis;overflow: hidden;padding-left:2px',
			        html:value,
			        title:value
			    }
	          },{
	             xtype:'component',width:19,height:17,padding:'1 0 0 6',
	             html:'X',
	             style:'color:#e4393c;cursor:pointer;',
	             listeners:{
	          		render:function(){
	          		    var delButton = this;
	          			this.getEl().on('click',function(){
	          			   me.removeQueryKey(delButton.ownerCt);
	          			},this);
	          			this.getEl().on('mouseover',function(){
	          			   delButton.ownerCt.hadFocus();
	          			   this.dom.style.backgroundColor='#e4393c';
	          			   this.dom.style.color='white';
	          			});
	          			this.getEl().on('mouseout',function(){
	          				delButton.ownerCt.lostFocus();
	          			   this.dom.style.backgroundColor='#f8f8f8';
	          			   this.dom.style.color='#e4393c';
	          			});
	          		}
	          	}
	          	
	          }],
	          hadFocus:function(){
			     this.setStyle({
			     	border:"solid #e4393c 1px"
			     });
	          },
	          lostFocus:function(){
	          	this.setStyle({
			     	border:"solid #c5c5c5 1px"
			     });
	          }
	          
	       });
		return con;
	},
	removeQueryKey:function(keyItem){
		  var con=Ext.getCmp("template_id");//.valueObj;
		  con.valueObj=con.valueObj.replace(","+keyItem.value,"")
	   	  keyItem.destroy();
	}
});