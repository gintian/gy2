/**
 * 导出签章
 */
/* 
 * 签章需要引入的js
 * **/
Ext.Loader.loadScript({url:'/module/template/signature/encryptionlock/websocket.js'});
Ext.Loader.loadScript({url:'/module/template/signature/encryptionlock/RockeyArmCtrl.js'});
Ext.Loader.loadScript({url:'/module/template/signature/encryptionlock/base64.js'});
Ext.Loader.loadScript({url:'/module/template/signature/encryptionlock/Signature_lock.js'});
//签章需要的全局参数
var ctrl = null;
var websock = true;
var b;
var ArmHandle;	//加密锁句柄
var FileID,ReadLength,FileOffset,Handle,Index,DongleInfoNum,Offset;  
Ext.define('EHR.signatureFile.SignatureFile',{
	extend:'Ext.Component',
	xtype:'signatureFile',
	currentUser:undefined,//签章用户名 不传为当前用户 自助用户传nbase+A0100
	photolist:undefined,
	onsuccess:undefined,
	isShowError:undefined,//是否弹出非必要错误信息
	isGetMarkID:undefined,//判断是取签章ID 还是获取签章图片流
	onerror:undefined,
	constructor:function(config){
		this.callParent()
		Ext.apply(this,config)
		var map=new HashMap()
		if(!this.currentUser){
			this.currentUser=""
		}
		if(!this.isGetMarkID){
			this.isGetMarkID=false;
		}
		map.put("currentUser",this.currentUser)
		map.put("flag","checkSignature");
		Rpc({functionId:'ZJ100000400',async:false,success:this.init,scope:this},map)
		
	},
	init:function(res){
		res=Ext.decode(res.responseText)
		//校验是否设置选择电子签章服务商以及是否启用U盾签章
		if(res.type){
			if(res.signature_usb){
				this.currentUser=res.currentUser
				var SignatureControl=document.getElementById('ctrl');
				if(Ext.isIE){
					if(SignatureControl){
						SignatureControl.parentNode.removeChild(SignatureControl);
						this.createSignObject();
					}else{
						this.createSignObject();
					}
				}else{
						this.createSignObject();
				}
			}else{
				var map=new HashMap()
				map.put("currentUser",this.currentUser);
				map.put("flag","onlyCheckPassword")
				Rpc({functionId:'ZJ100000400',async:false,success:function(res){
					res=Ext.decode(res.responseText)
					if(!this.currentUser){
						this.currentUser=res.currentUser
					}
					this.createWidow(res)
				},scope:this},map)
			}
		}else{
			// 52441 没有设置签章的 还是默认导出excel 没有签章
			Ext.callback(this.onerror,this)
		}
	},
	createSignObject:function(){ /***创建锁对象*/
		var me=this
		if(Ext.isIE){
			var signObjdiv = document.createElement("div");
			var innerHtml = '<OBJECT id="ctrl" classid="clsid:33020048-3E6B-40BE-A1D4-35577F57BF14" VIEWASTEXT width="0" height="0"></OBJECT>'; 
			signObjdiv.innerHTML = innerHtml;
			document.body.appendChild(signObjdiv);
		}
		b = new Base64();
		try{
			ctrl = new AtlCtrlForRockeyArm("{33020048-3E6B-40BE-A1D4-35577F57BF14}"); 
		}catch (e){
			ctrl = null;
			websock = false;
		}
		setTimeout(function(){
			me.checkPermission()
		},1000);
		
	},
	checkPermission:function(){//校验权限
		var me=this
		if(websock&&ctrl!=null){//非IE
			me.checkClockForSignature()
		}else{
			var PID = Arm_GetDongleInfo(0);
			if(PID!=''){
				Arm_Open();
			    //1、首先判断是不是发布的锁，不是，给出提示。
			    //读取0~2048位置存储的des加密的硬件id
				var PID_en = Arm_ReadData(0,16);
				//给PID_en解密 并判断PID_en与PID是否相同
				var map=new HashMap();
				map.put("data_en",PID_en)
				map.put("data",PID)
				map.put("flag","comparePID");
				map.put("currentUser",this.currentUser);
				Rpc({functionId:'ZJ100000400',async:false,success:function(res){
					res=Ext.decode(res.responseText)
					if(res.flag){
						//2、读取绑定信息“用户名称+硬件ID”加密
						var BaningID = Arm_ReadData(2048,128);
						var map=new HashMap()
						map.put("BaningID",BaningID)
						map.put("flag","baningID");
						map.put("currentUser",this.currentUser);
						Rpc({functionId:'ZJ100000400',async:false,success:function(res){
							res=Ext.decode(res.responseText)
							if(res.flag){
								this.createWidow(res);
							}else{
								Ext.showAlert("检测到本锁未与当前登录用户绑定，无法进行签章操作！",function(){
									Arm_Close();
									return false;
								});
							}
						},scope:this},map)
					}else{
						Ext.showAlert(res.message,function(){
							Arm_Close();
							return false;
						});
					}
				},scope:this},map)
			}
		}
	},
	createWidow:function(res){
		var me=this
		var data=res.data;
		if(!data||data==0){//没有印章图片时直接返回
			Ext.callback(me.onerror,me,[])
			return
		}
		var checkpassword=res.checkpassword
		//只有一个印章且没有密码时
		if(data&&data.length==1&&!checkpassword){
			if(me.isGetMarkID){
				var obj=[{'username':me.currentUser,'MarkID':data[0].MarkID,'signatureID':res.signatureID}]
				Ext.callback(me.onsuccess,me,[obj])
			}else{
				var map=new HashMap();
				map.put('flag',"getphoto")
				map.put("MarkID",data[0].MarkID)
				map.put("isGetMarkID",me.isGetMarkID);
				map.put('currentUser',me.currentUser)
				Rpc({functionId:'ZJ100000400',async:false,success:function(res){
										res=Ext.decode(res.responseText);
										if(res.flag){
											me.photolist=res.photolist
											Ext.callback(me.onsuccess,me,[me.photolist])
										}else{
											Ext.showAlert(res.errorMsg)
										}
									},scope:this},map)
			}
			
		}else{
			Ext.widget('window',{
				title:'提示',
				modal:true,
				layout:'vbox',
				id:'passwordWindow',
				bodyPadding:'30 0 0 30',
				resizable:false,
				items:[
					me.createCombo(data),
					me.createText(checkpassword)
					],
					width:300,
					height:checkpassword?200:150,
							buttonAlign:'center',
							buttons:[
								{text:'确定',handler:function(){
									var map=new HashMap()
									map.put("currentUser",me.currentUser);
									if(checkpassword){
										var value=Ext.getCmp("passwordValue").value;
										map.put('value',value)
										map.put('flag',"checkpassword")
									}else{
										map.put('flag',"getphoto")
									}
									map.put("isGetMarkID",me.isGetMarkID);
									var markID='';
									var com=Ext.getCmp('combox_markID');
									if(com){
										var record=com.getSelection()
										markID=record.get("MarkID")
									}
									map.put("MarkID",markID);
									Rpc({functionId:'ZJ100000400',async:false,success:function(res){
										res=Ext.decode(res.responseText);
										if(res.flag){
											me.photolist=res.photolist
											Ext.getCmp("passwordWindow").destroy()
											Ext.callback(me.onsuccess,me,[me.photolist])
										}else{
											Ext.showAlert(res.errorMsg)
										}
									},scope:this},map)
									
								}},
								{text:'取消',handler:function(){
									Ext.getCmp("passwordWindow").destroy()
								}},
								]
			}).show()
		}

	},
	checkClockForSignature:function(){
		var me=this
		ctrl.Arm_Enum(function(result, response){
			me.ctrlArm_Enum(result, response,me)
		})
	},
	ctrlArm_Enum:function(result, response,me){
		if (!result){
			Ext.showAlert("Arm_Enum error. " + response);
		}else{
			var index = response;
    		if(index<0){
    			index=0;
    		}
    		if(index>1){
    			if(clockflag==1){
    				Ext.showAlert("不支持多个锁同时绑定，请依次插入锁进行绑定操作！",function(){
    					return;
    				});
    			}else if(clockflag==0){
    				Ext.showAlert("检测到多个锁，系统仅支持一个用户操作一把锁！",function(){
    					return;
    				});
    			}
    		}
    		if(index==0){
    			Ext.showAlert("没有检测到有效的锁！",function(){
    				return;
    			});
    		}
    		DongleInfoNum = 2;	//硬件ID
    		if(index>0){
    			Index = index-1;
    			ctrl.Arm_GetDongleInfo(function(result, response){
    				if (!result){
						Ext.showAlert("Arm_GetDongleInfo error. " + response);
    				}else{
    					var PID = response;
    					me.PID=PID;
		        		if(PID!=''){
		        			ctrl.Arm_Open(function(result, response){
		        				me.ctrlArm_Open(result, response,me)
		        			})
		        		}
    				}
    			})
    		}
		}
	},
	ctrlArm_Open:function(result, response,me){
		if (!result){
			Ext.showAlert("Arm_Open error. " + response);
		}else{
			ArmHandle = response;
			Offset = 0;
			ReadLength = 16;
			Handle = ArmHandle;
			ctrl.Arm_ReadData(function(result,response){
				if(!result){
					Ext.showAlert("Arm_ReadData error. " + response);
				}else{
					var PID_en = response;
					var map=new HashMap();
					map.put("data_en",PID_en)
					map.put("data",me.PID)
					map.put("flag","comparePID");
					map.put("currentUser",this.currentUser);
					Rpc({functionId:'ZJ100000400',async:false,success:function(res){
						me.ctrlArm_ReadData(res,me)
					},scope:me},map)
				}
			})
		}
	},
	ctrlArm_ReadData:function(res,me){
		res=Ext.decode(res.responseText)
		if(res.flag){
			//2、读取绑定信息“用户名称+硬件ID”加密
			Offset = 2048;
			ReadLength = 128;
			ctrl.Arm_ReadData(function(result,response){
				if(!result){
					Ext.showAlert("Arm_ReadData error. " + response);
				}else{
					var BaningID = response;
					var map=new HashMap()
					map.put("BaningID",BaningID)
					map.put("flag","baningID");
					map.put("currentUser",me.currentUser);
					Rpc({functionId:'ZJ100000400',async:false,success:function(res){
						res=Ext.decode(res.responseText)
						if(res.flag){
							me.createWidow(res);
						}else{
							Ext.showAlert("检测到本锁未与当前登录用户绑定，无法进行签章操作！",function(){
								return false;
							});
						}
					},scope:me},map);
				}
			})
		}else{
			Ext.showAlert(res.message,function(){
				Arm_Close();
				return false;
			});
		}
	},
	createCombo:function(data){
		var states = Ext.create('Ext.data.Store', {
		    fields: ['MarkID', 'imgname']
		});
		if(data){
			for(var i=0;i<data.length;i++){
				states.add({"MarkID":data[i].MarkID,"imgname":data[i].imgname})
			}
		}
		var com=Ext.create('Ext.form.ComboBox', {
				    fieldLabel: '用户印章:',
				    id:'combox_markID',
				    store: states,
				    width:230,
				    labelWidth:70,
				    queryMode: 'local',
				    displayField: 'imgname',
				    valueField: 'MarkID',
				    listeners:{
				    	  afterRender : function() {
				    		if(states.getData().length>0){
				    			var record=states.getAt(0)
				    			com.setSelection(record)
				    		}
				         }
				    }
				});
		return com;
	},
	createText:function(checkpassword){
		var text=Ext.widget('textfield',{
				    style:'margin-top:20px',
					labelWidth:70,
					hidden:!checkpassword,
					id:'passwordValue',
					width:230,
					fieldLabel:'印章密码:',
					inputType:'password'
		})
		return text;
	}
})