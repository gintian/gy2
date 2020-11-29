/**
 * 通知提醒
 * linbz
 */
Ext.define('SendMessageURL.SendMessage',{
	isNewModule:false,//是否是新版评审会议调用
	w0301:'',//新版评审会议调用传参：会议id(加密)
	meetName:'',//新版评审会议调用传参： 会议名称
	usertype:null,//新版评审会议调用传参：阶段的账号类型
	segment:'',//新版评审会议调用传参：要通知的阶段
	constructor:function(config){
		semdMessage_me = this;
		semdMessage_me.enableModes = undefined;
		//haosl  add  start
		this.w0301 = config.w0301;
		this.meetName = config.meetName;
		this.isNewModule = config.isNewModule;
		this.segment = config.segment;
		this.usertype = config.usertype;
		//haosl  add  end
		this.init();
	},
	
	init:function(){
		var me = this;
		semdMessage_me.enableModes = semdMessage_me.getEnableModes();
		var enableModes = semdMessage_me.enableModes;
		var flag = false;
		if(enableModes){
			for(var i in enableModes){
				if(enableModes[i]){
					flag = true;
					break;
				}
					
			}
		}
		if(!flag){
			Ext.showAlert("未配置任何通讯平台参数,无法发送通知！")
			return;
		}
			
		//获取会议名称      id
		//haosl  add  start
		if(!me.isNewModule){
			var tablePanel=meeting_me.tableObj.tablePanel;
			var records=tablePanel.getSelectionModel().getSelection();
			me.meetName = records[0].data.w0303;
			me.w0301 = records[0].data.w0301_e;
		}
		//haosl  add  end
		semdMessage_me.tabs = Ext.create('Ext.tab.Panel', {
					border:true,
					activeTab:0,//默认学科组成员
					margin:'0 3 0 3',
					id:'sendmessagetab',
			   	    items: [{
			   	            title: zc.reviewconsole.applicant,//"申报人"
			   	            id:'declarer',
			   	            height:322,
			   	    		border:false,
			   	            listeners: { 
			   	    			activate:function (declarer) {
		   	    					if(declarer.items.length == 0) {//没有点击过
		   	    						declarer.add(semdMessage_me.createView(1));
		   	    						declarer.add(semdMessage_me.sendMode(1));
		   	    					} 
			   	    			} 
			   	    		}
			   	        }
			   	    ],
			   	    listeners:{
			   	    	afterrender:function(tabpanel){
			   	    		semdMessage_me.addTabs(tabpanel);
			   	    	}
			   	    }
			   	});
		
		var win = Ext.create('Ext.window.Window',{
	  		title:me.meetName,
	 		id:'meetId',
	 		width:650,
	 		height:425,  
	        resizable: false,  
	        modal: true,
	        border:false,
	       	buttonAlign:'center',
	       	bodyStyle: 'background:#ffffff;',
	       	items:[semdMessage_me.tabs],
	        buttons:[{
	 	        	   xtype:'button',
	 	        	   text:"发送",
	 	        	   handler:function () 
	 	        	   		{
		        				var tabId = Ext.getCmp('sendmessagetab').getActiveTab().id;//获取当前页面id
		        				//学科组group_id
		        				var subjectlist  = [];
		        				for(var i=0; i<Ext.query('.subjectgroup').length; i++){
			 	        			var checkbox = Ext.query('.subjectgroup')[i];
			 	        			var value = Ext.getCmp(checkbox.id).getValue();
			 	        			var subjectId = Ext.getCmp(checkbox.id).id;//学科组名称
			 	        			if(value){
			 	        				subjectlist.push(subjectId);
			 	        			}
			 	        		}
		        				if("subject" == tabId && subjectlist.length < 1){
			 	        			Ext.showAlert("请选择要发送的学科组！");
			 	        			return ;
		        				}
			 	        		//消息内容type
			 	        		var contenMsg = "";
			 	        		if(tabId=="declarer"){
			 	        			contenMsg = Ext.getCmp('contenId1').getRawValue();
			 	        		}else if(tabId=="subject"){
			 	        			contenMsg = Ext.getCmp('contenId2').getRawValue();
			 	        		}else if(tabId=="judges"){
			 	        			contenMsg = Ext.getCmp('contenId3').getRawValue();
			 	        		}else if(tabId=="subcommitte"){
			 	        			contenMsg = Ext.getCmp('contenId4').getRawValue();
			 	        		}
			 	        		contenMsg = contenMsg.replace(/\n/g, '<br>').replace(/ /g, '&nbsp;').replace(/\t/g, '&emsp;');
			 	        		
			 	        		if(Ext.isEmpty(contenMsg)){
			 	        			Ext.showAlert("消息内容不能为空！");
			 	        			return ;
			 	        		}
			 	        		
			 	        		//发送方式type
			 	        		var sendlist  = [];
			 	        		for(var i=0; i<Ext.query('.sendcls').length; i++){
			 	        			var checkbox = Ext.query('.sendcls')[i];
			 	        			var value = Ext.getCmp(checkbox.id).getValue();
			 	        			var sendValue = Ext.getCmp(checkbox.id).boxLabel;//发送方式
			 	        			if(value){
			 	        				sendlist.push(sendValue);
			 	        			}
			 	        		}
			 	        		if(sendlist.length < 1){
			 	        			Ext.showAlert("请选择发送方式！");
			 	        			return ;
			 	        		}
			 	        		var map = new HashMap();
			 	        		map.put("tabId", tabId);
			 	        		map.put("w0301",me.w0301);
			 	     		 	map.put("subjectlist",subjectlist);
			 	     		 	map.put("contenMsg",contenMsg);
			 	     		 	map.put("sendlist",sendlist);
			 	     		 	map.put("isNewModule",semdMessage_me.isNewModule);
			 	           		Rpc({functionId:'ZC00002310',async:false,success:function(data){
				 	           				var msg = Ext.decode(data.responseText).info;
						 	           		if(Ext.isEmpty(msg)){
						 	           			Ext.showAlert("通知提醒已发送！");
						 	           		}
						 	           		if(msg != ""){
						 	           			Ext.showAlert(msg);
						 	           		}
			 	           				}},map);
	 	           			}
	        			},
		 	           {xtype:'button',text:"取消",handler:function () {win.close()}}
			           ]
		});
		win.show();
	},
	//消息内容
	createView:function(type,flag){
	/*	var tablePanel=meeting_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		semdMessage_me.meetName = records[0].data.w0303;*/
		//修改ext css样式.x-form-text-wrap-default   height: (window.screen.availHeight-150)*0.32,width: Ext.getBody().getWidth()*0.56,
    	Ext.util.CSS.createStyleSheet("#contenId"+type+" .x-form-text-wrap-default{border-width:0px;}","card_css");
		var height = 293;
		if(type == '2' && flag){
			height=267;
		}
		
		var messageText = '    您好，'+this.meetName+'评审工作即将开始，请于xxxx年xx月xx日到xxxx报告厅参加职称评审工作会议，详细信息请登录xxx网站下载查看。'
		messageText+='\n\n人事部';
    	var conten = new Ext.form.FieldSet({
					xtype: 'fieldset',
			        title: '消息内容',
			        height: height,
			        margin:'0 6 3 6',
				    items :[
							{
								// .x-form-text-wrap-default
								xtype  :'textarea',
								id     :'contenId'+type,
								height:height-30,
								margin :'10 0 0 0',
								scrollable : 'y',
							    width  : 600,
							    value:messageText
							}
				           ] 
				});
		return conten;
	},
	//发送方式FieldContainer
	sendMode:function(type){
		var smsflag = semdMessage_me.enableModes.smsflag;       
		var emailflag = semdMessage_me.enableModes.emailflag;   
		var weixinflag =semdMessage_me.enableModes.weixinflag; 
		var ddflag = semdMessage_me.enableModes.ddflag;         
		
		return new Ext.form.FieldContainer({
			xtype: 'fieldcontainer',
            defaultType: 'checkboxfield',
            layout:'hbox',//水平布局
            id:'sendradio'+type,
            padding:'0 0 0 8',
            items: [
                {
                    boxLabel  : "邮件",
                    name      : 'topping',
                    inputValue: '1',
                    hidden : !emailflag,
                    checked:emailflag,
                    id        : 'radio1'+type,
                    cls       : 'sendcls',
                    margin:'0 10 0 0'
                }, {
                    boxLabel  : "短信",
                    name      : 'topping',
                    inputValue: '2',
                    hidden : !smsflag,
                    checked:smsflag,
                    id        : 'radio2'+type,
                    cls       : 'sendcls',
                    margin:'0 10 0 0'
                }, {
                    boxLabel  : "微信",
                    name      : 'topping',
                    inputValue: '3',
                    hidden : !weixinflag,
                    checked:weixinflag,
                    id        : 'radio3'+type,
                    cls       : 'sendcls',
                    margin:'0 10 0 0'
                }, {
                    boxLabel  : "钉钉",
                    name      : 'topping',
                    inputValue: '4',
                    hidden : !ddflag,
                    checked :ddflag,
                    id        : 'radio4'+type,
                    cls       : 'sendcls',
                    margin:'0 10 0 0'
                }]
		});
	},
	//学科组分类
	subjectClass:function(){
			var me = this;
			var subjectclass = null;
			var map=new HashMap();
			map.put("w0301",me.w0301);
			Rpc({functionId:'ZC00002210',async:false,success:function(data){
				var subjectslist = Ext.decode(data.responseText).subjectslist;
				if(subjectslist.length==0)
					return;
				subjectclass = new Ext.form.FieldContainer({
					xtype: 'fieldcontainer',
		            defaultType: 'checkboxfield',
		            layout:'hbox',//水平布局
		            padding:'3 0 -4 8',
		            items: []
				});
				
				for(var i=0;i<subjectslist.length;i++){
					var subject = subjectslist[i];
					var group_name = subject.group_name;
					var group_id = subject.group_id;
					
					subjectclass.add({
		                boxLabel  : '<div title="'+group_name+'">'+group_name+'</div>',
		                name      : 'topping',
		                inputValue: '1'+group_id,
		                id        : group_id,
		                cls       : 'subjectgroup',
/*		                maxWidth:100,
		                style:'white-space:nowrap; overflow:hidden;text-overflow:ellipsis;',
*/		                margin	  : '0 10 0 0'
		            });
				}
			}},map);
			
		return subjectclass;
	},
	getEnableModes:function(){
		var enableModes = "";
		var map = new HashMap();
		Rpc({functionId:'ZC00005009',async:false,success:function(data){
			var data = Ext.decode(data.responseText);
			enableModes = data.enableModes;
		}},map);
		
		return enableModes;
	},
	/**
	 * 显示隐藏tab
	 */
	addTabs:function(tabpanel){
		if(!this.isNewModule){
			//subcommitte judges subject declarer
			tabpanel.add([{
			   	        	title: zc.label.inExpert+zc.meetingportal.member,//"学科组成员"
			   	        	id: 'subject',
			   	        	height:322,
			   	        	border:false,
			   	        	listeners: {
			   	        		activate:function (subject) {
		   	    					if(subject.items.length == 0) {//没有点击过
		   	    						var subjectgroup = semdMessage_me.subjectClass();
		   	    						var flag =false;//是否有学科组
		   	    						if(subjectgroup){
		   	    							subject.add(subjectgroup);//加到items里
		   	    							flag =true;
		   	    						}
		   	    						subject.add(semdMessage_me.createView(2,flag));
		   	    						subject.add(semdMessage_me.sendMode(2));
		   	    					
		   	    					} 
			   	        		}
			   	        	}
			   	        },
			   	        {
			   	        	title:  zc.label.inReview+zc.meetingportal.member,//"评委会成员"
			   	        	id: 'judges',
			   	        	height:322,
			   	        	border:false,
			   	        	listeners: {
			   	    			activate:function (judges) {
		   	    					if(judges.items.length == 0) {//没有点击过
		   	    						judges.add(semdMessage_me.createView(3));
		   	    						judges.add(semdMessage_me.sendMode(3));
			   	    				}
			   	    			} 
				   	        }
			   	        },{
			   	        	title: zc.label.inOther+zc.meetingportal.member,//二级单位
			   	        	id: 'subcommitte',
			   	        	height:322,
			   	        	border:false,
			   	        	listeners: {
			   	    			activate:function (subcommitte) {
		   	    					if(subcommitte.items.length == 0) {//没有点击过
		   	    						subcommitte.add(semdMessage_me.createView(4));
		   	    						subcommitte.add(semdMessage_me.sendMode(4));
		   	    					} 
			   	    			} 
				   	        }
			   	        }]);
			}else{
			switch(this.segment){
				case '1'://评委会
					if(this.usertype!="2")//随机账号不用发送通知
						break;
					tabpanel.add( {
			   	        	title:  zc.label.inReview+zc.meetingportal.member,//"评委会成员"
			   	        	id: 'judges',
			   	        	height:322,
			   	        	border:false,
			   	        	listeners: {
			   	    			activate:function (judges) {
		   	    					if(judges.items.length == 0) {//没有点击过
		   	    						judges.add(semdMessage_me.createView(3));
		   	    						judges.add(semdMessage_me.sendMode(3));
			   	    				}
			   	    			} 
				   	        }
			   	        });
					break;
				case '2'://专业组
					if(this.usertype!="2")//随机账号不用发送通知
						break;
					tabpanel.add({
			   	        	title: zc.label.inExpert+zc.meetingportal.member,//"学科组成员"
			   	        	id: 'subject',
			   	        	height:322,
			   	        	border:false,
			   	        	listeners: {
			   	        		activate:function (subject) {
		   	    					if(subject.items.length == 0) {//没有点击过
		   	    						var subjectgroup = semdMessage_me.subjectClass();
		   	    						var flag =false;//是否有学科组
		   	    						if(subjectgroup){
		   	    							subject.add(subjectgroup);//加到items里
		   	    							flag =true;
		   	    						}
		   	    						subject.add(semdMessage_me.createView(2,flag));
		   	    						subject.add(semdMessage_me.sendMode(2));
		   	    					
		   	    					} 
			   	        		}
			   	        	}
			   	        });
					break;
				case '3'://同行只显示申报人就行
					break;
				case '4'://二级单位
					if(this.usertype!="2")//随机账号不用发送通知
						break;
					tabpanel.add({
			   	        	title: zc.label.inOther+zc.meetingportal.member,//二级单位
			   	        	id: 'subcommitte',
			   	        	height:322,
			   	        	border:false,
			   	        	listeners: {
			   	    			activate:function (subcommitte) {
		   	    					if(subcommitte.items.length == 0) {//没有点击过
		   	    						subcommitte.add(semdMessage_me.createView(4));
		   	    						subcommitte.add(semdMessage_me.sendMode(4));
		   	    					} 
			   	    			} 
				   	        }
			   	        });
					break;
			}
		}
	}
	
})