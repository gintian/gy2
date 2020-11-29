Ext.define('ServiceClient.serviceSetting.AddClient', {
	extend:'Ext.window.Window',
	layout:'fit',
	id:'clientAdd',
	title:sc.setting.add,
	width:450,
	height:290,
	modal : true,//遮罩
	border : false,//无边框
	resizable:false,//禁止拉伸
	closable:true,//允许关闭按钮
	handler:Ext.emptyFn,
	clientList:'',
	constructor:function(config){
		this.handler = config.handler;
		this.clientList = config.clientList;
		this.callParent();
		this.init();
	},
	init:function(){
		var form = this.getMainPanel();
		this.add(form);
	},
	getMainPanel:function(){
		var me = this;
		return formPanel = Ext.create('Ext.form.Panel',{
			margin : '15 0 0 0',
			border:false,
			items:[{
			        xtype : 'textfield',
			        id  : 'name',
			        fieldLabel: sc.setting.clientname,//终端名称
			        editable : true,
			        margin:'0 0 20 0',
			        allowBlank:false,
			        beforeLabelTextTpl:"<font color='red'> * </font>",
			        labelAlign:'right',
			        width:'90%',
			        validator : function() {
			        	if(!me.clientList || me.clientList.length==0){
			        		return true;
			        	}
			        	for(var i=0;i<me.clientList.length;i++){
			        		var oldName = me.clientList[i].name;
			        		var newName = Ext.getCmp('name').getValue();
			        		var newNameTrim = Ext.util.Format.trim(newName);
		    				if(oldName==newNameTrim){
		    					  return sc.setting.errname ;
		    				}
			        	}
			        	return true;
			        },
				listeners:{
			        	focusleave:function(){
			        		var charnum = 0;//字节数
			        		var varlength = 0;//字符长度
		        		    for (var i = 0; i < this.value.length; i++) {
		        		        var a = this.value.charAt(i);
		        		        if (a.match(/[^\x00-\xff]/ig) != null) {//如果是汉字
		        		          charnum = charnum+2;//一个汉字占两个字节
		        		          varlength = varlength+1;
		        		        }
		        		        else {
		        		          charnum =charnum+1;//字母数字等占一个字节
		        		          varlength = varlength+1;
		        		        }
		        		        if(charnum==51||charnum==52){
		        		        	Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.overlength);//提示信息   请输入50个以内的字节（一个汉字占两个字节）
			    					this.setValue(this.value.substring(0,varlength-1));
		        		        }
		        		    }
			        	}
			        }
			    },{
			        xtype : 'textfield',
			        id  : 'ip_address',
			        fieldLabel: sc.setting.IP,
			        labelAlign:'right',
			        allowBlank: false,
			        beforeLabelTextTpl:"<font color='red'> * </font>",
			        width:'90%',
			        margin:'0 0 20 0',
			        validator : function() {
			        	if(!me.clientList || me.clientList.length==0){
			        		return true;
			        	}
			        	for(var i=0;i<me.clientList.length;i++){
			        		var oldIp = me.clientList[i].ip_address;
			        		var newIp = Ext.getCmp('ip_address').getValue();
			        		var newIpTrim = Ext.util.Format.trim(newIp);
		    				if(oldIp==newIpTrim){
		    					  return sc.setting.errip ;
		    				}
			        	}
			        	return true;
			        }
			       
			    },{
			        xtype : 'numberfield',
			        id  : 'pageCount',
			        fieldLabel: sc.setting.pagecount,
			        labelAlign:'right',
			        allowBlank: false,
			        width:'90%',
			        beforeLabelTextTpl:"<font color='red'> * </font>",
			        margin:'0 0 20 0',
			        minValue:0,
			        maxValue:500,
			        allowDecimals:false,
			        labelAlign:'right',
			        allowBlank: false
			    },{
			        xtype : 'textareafield',
			        id  : 'description',
			        fieldLabel: sc.setting.clientdescription,
			        width:'90%',
			        labelAlign:'right'
			    }], 	
		    	buttonAlign:'center',
		    	buttons: [
		    		{
		    		text: sc.setting.ok,
		    		formBind:true,//验证通过，则确定亮起
		    		handler: function(btn) {
		        		var nameValue = Ext.getCmp('name').value;
		        		var ipValue = Ext.getCmp('ip_address').value;
		        		var nameLength = Ext.util.Format.trim(nameValue).length;//字符串去头尾空格后的长度
		        		var ipLength = Ext.util.Format.trim(ipValue).length;
		        		
	        			if(nameLength==0){
		    				Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.writeclientname);//提示信息  请填写终端机名称
		        			return;
		    			}
		        		if(ipLength==0){
		        			Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.writeip);//提示信息  请填写IP地址
		        			return;
		        		}
		    			var value = btn.up('form').getValues();
		    			var map = new HashMap();
		    			var noseMap = {};
		    			noseMap.name = value['name-inputEl']
		    			noseMap.ip_address = value['ip_address-inputEl'];
		    			noseMap.description = value['ip_address-inputEl']
		    			noseMap.pageCount = value['pageCount-inputEl']
		    			map.put('name',value['name-inputEl']);
		    			map.put('ip_address',value['ip_address-inputEl']);
		    			map.put('description',value['description-inputEl']);
		    			map.put('pageCount',value['pageCount-inputEl']);
		    			map.put('transType','addClient');
		    			me.checkOrSaveClientData(map,noseMap,btn);
		    			
		    		}, scope:this
			    },{
			        text: sc.setting.cancel,
			        handler: function(btn) {
			            btn.up('window').close();
			        }
			    }]
			});
		},
	checkOrSaveClientData:function(map,noseMap,btn){
		btn.setDisabled(true);//点击之后将按钮置灰不可点击
		var me = this;
		Rpc({functionId:'SC000000003',success:function(res){
			var resData = Ext.decode(res.responseText);
			var checkMap = resData.checkMap;
			if(checkMap){
				var name=checkMap.name;
				var ip = checkMap.ip;
				var message = "";
				if(name && ip){
					message = sc.setting.clientIdAndNameRepeat;
				}else if(name){
					message = sc.setting.clientNameRepeat;
				}else if(ip){
					message = sc.setting.clientIdRepeat;
				}
				if(name){
					Ext.getCmp('name').markInvalid(sc.setting.errname);
				}
				if(ip){
					Ext.getCmp('ip_address').markInvalid(sc.setting.errip);
				}
				Ext.Msg.alert(sc.home.tip,message);
				btn.setDisabled(false);
			}else{
				var clientId = resData.clientId;
				map.put("clientId",clientId+'');
				Ext.callback(me.handler,Ext.getCmp('controlanalyse'),[map,noseMap]);
				btn.up('window').close();
			}
		}},map);
	}
});
