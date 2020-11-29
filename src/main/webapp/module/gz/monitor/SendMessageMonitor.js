/**
 * 薪资流程发送通知提醒
 * sunjian 2018-8-10
 */

Ext.define('Salarybase.monitor.SendMessageMonitor',{
    constructor:function(config){
    	sendMonitor_me = this;
    	sendMonitor_me.salaryid = config.salaryid;
    	sendMonitor_me.fullname = config.fullname;
    	sendMonitor_me.username = config.username;
    	sendMonitor_me.sp_flag_code = config.sp_flag_code;
    	sendMonitor_me.imodule = config.imodule;
    	sendMonitor_me.enableModes = config.enableModes;
    	sendMonitor_me.getTableOK();
    },
	getTableOK:function(){
		var id = "";
		if(sendMonitor_me.username) {
			id = sendMonitor_me.username;
		}else {
			id = sendMonitor_me.fullname;
		}
		var usernameArray = id.split(",");
		var html = "";
		//可能有多个接收人，当有多个接受人的时候可以进行删除操作
		for(var i = 0; i < usernameArray.length; i++) {
			var userName = usernameArray[i];
			var style = "";
			if(sendMonitor_me.sp_flag_code == '02') {
				style = "onMouseOver='sendMonitor_me.showHideDel(1,\"del_username_" + userName + "\");' onMouseOut='sendMonitor_me.showHideDel(2,\"del_username_" + userName + "\");'"; 
			}
			var fullname = sendMonitor_me.fullname.split(",")[i];
			var length = sendMonitor_me.getStrLength(fullname);
			var left = 0;
			for(var j = 0; j < length; j++) {
				left += 5;
			}
			html += "<span style='position:relative;' " + style + " id='A_" + userName + "' >" + fullname + 
		                 "&nbsp;&nbsp;<img id='del_username_" + userName + "' onclick='sendMonitor_me.delPerson(\""+userName+"\",\"" + sendMonitor_me.fullname.split(",")[i] + "\");' src='/workplan/image/remove.png' style='width:15px;height:15px;cursor:pointer;position:absolute;top:0;left:" + left + "px;display:none;'></span>";
		}
		
		var left = 138;
		if(sendMonitor_me.enableModes.smsflag) {
			left = left - 51;
		}
		if(sendMonitor_me.enableModes.weixinflag) {
			left = left - 51;
		}
		if(sendMonitor_me.enableModes.ddflag) {
			left = left - 51;
		}
		var win = Ext.create('Ext.window.Window',{
			title:gz.label.message,
			id:'meetId',
			width:580,
			height:395,  
		    resizable: false,  
		    modal: true,
		    border:false,
		    buttonAlign:'left',
		   	items:[
		   		{
			   		xtype:'panel',
	                id:'accept',
	                border:false,
	                height:27,
	                html:"<div>" + gz.label.accept + html + "</div>",
	                style:'margin-top:5px;padding-left:15px;'
			   	},{
			   		xtype:'textareafield',
	                id:'contenId',
	                width: 558,
	                height:285,
	                style:'padding-left:15px;'
			   	}],
		    buttons:[
		    	 {
			   		xtype: 'panel',
		            defaultType: 'checkboxfield',
		            id:'sendradio',
		            height:20,
		            border:false,
		            layout:'hbox',//水平布局
		            padding:'0 0 0 8',
		            style:'left:-100px;',
		            items: [
		                {
		                    boxLabel  : gz.label.mailNotice.substring(0,2),
		                    checked: true,
		                    id: 'email',
		                    margin:'0 10 0 0'
		                },{
		                    boxLabel  : gz.label.smsNotice.substring(0,2),
		                    name      : 'sms',
		                    id        : 'sms',
		                    hidden: !sendMonitor_me.enableModes.smsflag,
		                    checked: sendMonitor_me.enableModes.smsflag,
		                    margin:'0 10 0 0'
		                },{
		                    boxLabel  : gz.label.wx,
		                    name      : 'wx',
		                    id        : 'wx',
		                    hidden: !sendMonitor_me.enableModes.weixinflag,
		                    checked: sendMonitor_me.enableModes.weixinflag,
		                    margin:'0 10 0 0'
		                },{
		                    boxLabel  : gz.label.dingDing,
		                    name      : 'dingding',
		                    id        : 'dingding',
		                    hidden: !sendMonitor_me.enableModes.ddflag,
		                    checked: sendMonitor_me.enableModes.ddflag,
		                    margin:'0 10 0 0'
		                }]
				 },
		    	 {
	        	   text:common.button.sms.send,
	        	   style:'margin-left:' + left + 'px;',
	        	   handler:function () 
	    	   		{
	        		 //消息内容contenId
	 	        		var contenMsg = Ext.getCmp('contenId').getRawValue();
	 	        		contenMsg = contenMsg.replace(/\n/g, '<br>').replace(/ /g, '&nbsp;').replace(/\t/g, '&emsp;');
	 	        		
	 	        		if(Ext.isEmpty(contenMsg)){
	 	        			Ext.showAlert(gz.label.cannotNull);
	 	        			return ;
	 	        		}
	 	        		if(!Ext.getCmp("email").value && !Ext.getCmp("sms").value && !Ext.getCmp("wx").value && !Ext.getCmp("dingding").value) {
	 	        			Ext.showAlert(gz.label.chooseSendWay);
	 	        			return ;
	 	        		}
	 	        		//获取发送的方式，后台根据这个进行识别
	 	        		var sendlist = [];
	 	        		var email = Ext.getCmp("email").getValue();
 	        			var emailValue = Ext.getCmp("email").boxLabel;//发送方式邮件
 	        			if(email){
 	        				sendlist.push(emailValue);
 	        			}
 	        			var sms = Ext.getCmp("sms").getValue();
 	        			var smsValue = Ext.getCmp("sms").boxLabel;//发送方式短信
 	        			if(sms){
 	        				sendlist.push(smsValue);
 	        			}
 	        			var wx = Ext.getCmp("wx").getValue();
 	        			var wxValue = Ext.getCmp("wx").boxLabel;//发送方式微信
 	        			if(wx){
 	        				sendlist.push(wxValue);
 	        			}
 	        			var dingding = Ext.getCmp("dingding").getValue();
 	        			var dingdingValue = Ext.getCmp("dingding").boxLabel;//发送方式钉钉
 	        			if(dingding){
 	        				sendlist.push(dingdingValue);
 	        			}
	        		    var map = new HashMap();
				    	map.put("salaryid",sendMonitor_me.salaryid);
				    	map.put("username",sendMonitor_me.username);
				    	map.put("sp_flag",sendMonitor_me.sp_flag_code);
				    	map.put("imodule",sendMonitor_me.imodule);
				    	map.put("fullname",sendMonitor_me.fullname);
				    	map.put("content",contenMsg);
				    	map.put("sendlist",sendlist);
				    	Ext.MessageBox.wait("<p style='text-align:center;'>" + gz.label.sending + "</p>", gz.label.wait);
				    	Rpc({functionId:'GZ00000241',async:true,success:sendMonitor_me.getSendOk},map);
	       			}
    			},
 	           {text:common.button.cancel,handler:function () {win.close()}}
	           ]
			});
			win.show();
	},
	
	showHideDel:function(state,id) {
		//如果只有一个人，就不让删除了，没意义
		var trueLength = 0;
		var len = sendMonitor_me.username.split(",");
		for(var i = 0; i < len.length; i++) {
			if(len[i] != '')
				trueLength++;
		}
		if(trueLength == 1) {
			return;
		}
		if(Ext.getDom(id)){
			if(state == 1){
				Ext.getDom(id).style.display = 'block';
			} else if(state == 2){
				Ext.getDom(id).style.display = 'none';
			}
		}
	},
	
	delPerson:function(data,fullname){
		Ext.showConfirm(gz.msg.sureDelRecard.substring(0,4)+gz.label.accept+fullname+"?", function(btn){
			if(btn == 'yes') {
				Ext.getDom("A_"+data).innerHTML = '';
				if(sendMonitor_me.username.indexOf("," + data + ",") == -1)
					sendMonitor_me.username = sendMonitor_me.username.replace(data,"");
				else 
					sendMonitor_me.username = sendMonitor_me.username.replace(data + ",","");
				
				if(sendMonitor_me.fullname.indexOf("," + fullname + ",") == -1)
					sendMonitor_me.fullname = sendMonitor_me.fullname.replace(fullname,"");
				else
					sendMonitor_me.fullname = sendMonitor_me.fullname.replace(fullname + ",","");
			}
		});
	},
	
	//将所有双字节变成2个单字节
	getStrLength:function(str){
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    },
	
    getSendOk:function(form){
    	var result = Ext.decode(form.responseText);
        var msg = result.msg;
        Ext.MessageBox.close();
        if(msg == "") {
        	Ext.showAlert(gz.label.sendok);
        	Ext.getCmp("meetId").close();
        }else {
        	Ext.showAlert(msg);
        }
    }
})