Ext.define('ServiceClient.serviceHome.ServicePlatform',{
	extend:'Ext.panel.Panel',
	requires:['EHR.portal.PortalPanel','ServiceClient.serviceHome.ServiceNoticeWindow','EHR.extWidget.field.DateTimeField','EHR.extWidget.field.CodeTreeCombox','EHR.commonQuery.QueryFieldsSelector'],
	id:'servicePlatformCmp',
	scrollable:'y',
	servicesData:undefined,
	serviceName:undefined,
	serviceUrl:undefined,
	loadType:undefined,//默认为undefined
	ip:undefined,
	initComponent:function(){
		this.initFrameConfig();
		this.callParent();
		this.initServices(this.servicesData);
	},
	//初始化框架参数
	initFrameConfig:function(){
		var me = this;
		//创建分类按钮和分类容器
		me.items = [{	
			xtype:'panel',
			id:'dragPanel',
			flex:1,
			border:false,
			items:[{
				//panel使用layout对象为column，需定义列容器
				xtype:'container',
    			padding:me.loadType=='preview'?'15 60 0 60':'15 150 0 150',
				columnWidth:1,
				itemId:'groupBox'
			}],
			listeners:{
			    render:function(){
                    var divObj=document.getElementById("dragPanel");
                    var moveFlag=false;
                    //拖拽函数
                    divObj.onmousedown=function(e){//按下
                    	//清除定时
                    	ServiceClientSecurity.resetTime();
                        moveFlag=true;
                        var clickEvent=window.event||e;
                        var beginClientY = clickEvent.clientY;
                        divObj.onmousemove=function(e){//移动
                            var moveEvent=window.event||e;
                            if(moveFlag){
                                var directionFlag = moveEvent.clientY-beginClientY;//根据正负判断向上还是向下拖动
                                var servicePlatformCmpEl = Ext.getCmp("servicePlatformCmp").body;
                                var absTopDistance = Math.abs(moveEvent.clientY-beginClientY);//y偏移的绝对值
                                if(directionFlag<0){
                                    servicePlatformCmpEl.scroll('bottom',absTopDistance,false);//小于0滚动条向下
                                }else{
                                    servicePlatformCmpEl.scroll('top',absTopDistance,false);//大于0滚动条向上
                                }
                                divObj.onmouseup=function(){//结束
                                    moveFlag=false;
                                }
                            }
                        }
                        divObj.onmouseup=function(){//结束
                            moveFlag=false;
                        }
                    };
			    }
			}
		}]
	},
	/**
	 *渲染服务数据,数据格式参考设计文档
	 */
	initServices:function(servicesData){
		var me =this;
		me.groupBox = me.query('#groupBox')[0];
		if(servicesData){
			for(var i=0;i<servicesData.length;i++){
				//创建分类面板
				var group = me.createGroupCmp(servicesData[i]);
				me.groupBox.add(group);
			}
		}else{
			Ext.Msg.alert(sc.home.tip, sc.home.incompatibleServiceTip);//sc.home.incompatibleServiceTip  没有符合要求的服务！！
			
		}
	},
	/**
	 * 点击打印服务处理方法
	 * serviceId:服务id
	 * 创建PrintService.js对象并加载到页面
	 * */
	printServiceClick:function(serviceConfig){
	    var me = this;
	    var ip = Ext.getCmp('servicePlatformCmp').ip;
	    var ifCheck = serviceConfig.notice_enable;
	    var serviceId = serviceConfig.serviceId;
	    var config_input_enable = serviceConfig.config_input_enable;
	    if(ifCheck == "1" || config_input_enable == "1"){
	    	var map = new HashMap();
			map.put('serviceId',serviceId);
	    	Rpc({functionId:'SC000000006',async:false,success:function(res){
	    		var info = Ext.decode(res.responseText);
	    		var serviceParamData = info.serviceParamData;
		    	Ext.create("ServiceClient.serviceHome.ServiceNoticeWindow", { 
		    		ip:ip,
		    		serviceConfig:serviceConfig,
		    		serviceParamData:serviceParamData
		    	}).show();
			},scope:this},map);
		}else{
    	    var serviceMainBox = Ext.getCmp('serviceMainBox');
    	    serviceMainBox.removeAll(false);
			//自助终端配置服务须知未启用直接跳转
            Ext.MessageBox.show({   
                title:sc.home.tip,   
                msg:sc.home.loadTip, //正在加载,请稍候...  
                progress:true,   
                width:300,   
                wait:true,   
                waitConfig:{interval:600},   
                closable:true 
            });
            var printService = Ext.create("ServiceClient.serviceHome.PrintService", {
                serviceId: serviceConfig.serviceId,
                ins_id: serviceConfig.ins_id,
                task_id: serviceConfig.task_id,
                ifCheck:ifCheck,//是否启用服务须知
                templateId: serviceConfig.templateId,
                ip: this.ip,
                //symbol:true,
                templateType: serviceConfig.templateType
            });
            serviceMainBox.add(printService);
		}
	},
	/**
	 * 点击其他服务处理方法
	 * serviceId:服务id
	 * serviceName:服务名称
	 * serivceUrl:服务地址
	 * 创建ViewService.js对象并加载到页面
	 */
	viewServiceClick:function(serviceConfig,serviceId,serviceName,serviceUrl){
		var me = this;
		var otherService = Ext.getCmp('serviceMainBox');
		var ifCheck = serviceConfig.notice_enable;
        if (ifCheck == "1") {//自助终端配置服务协议启用
        	var serviceGuidelines = new Ext.window.Window({
        		modal:true,
                layout: 'vbox',
                title: sc.home.serviceGuideline,//服务须知
                resizable: false,
                buttonAlign: 'center',
                width: 700,
                items: [{
                    xtype: 'panel',
                    scrollable:'y',
                    height: 272,
                    width: 600,
                    margin:'30 0 0 48',
                    html: serviceConfig.description
                }, {
                    xtype: 'checkboxgroup',
                    id: 'agreeServiceNotice',
                    items: [{
                        boxLabel: sc.home.serviceGuidelines,
                        style:'margin-left:44px',
                        inputValue: '1',
                        checked: false
                    }],
                    listeners:{
                        element:'el',
                        click:function(){
                            var check = Ext.getCmp('agreeServiceNotice').getValue().agreeServiceNotice;//是否勾选服务须知
                            var okBtn = serviceGuidelines.query('#agreeGuidelines')[0];//确定按钮
                            if(check){
                                okBtn.setDisabled(false);
                            }else{
                                okBtn.setDisabled(true);
                            }
                        },
                        scope:this
                    }
                }],
                buttons: [{
                    text: sc.setting.ok,
                    itemId:'agreeGuidelines',
                    formBind: true,
                    listeners:{
                        render:function(btn){
                            btn.setDisabled(true);
                        }
                    },
                    handler: function (btn) {
                    	btn.setDisabled(true);//防止重复点击
                        this.up('window').destroy();
                    	otherService.removeAll(false);
                        var otherSer = Ext.create("ServiceClient.serviceHome.ViewService",{
                            serviceName:serviceName,
                            serviceUrl:serviceUrl
                        });
                        otherService.add(otherSer);
                    }
                }]
            });
            serviceGuidelines.show();
        }else{
        	otherService.removeAll(false);
            var otherSer = Ext.create("ServiceClient.serviceHome.ViewService",{
                serviceName:serviceName,
                serviceUrl:serviceUrl
            });
            otherService.add(otherSer);
        }
	},
	//初始化框架参数
	createGroupCmp:function(groupConfig){
		var me = this;
		var groupBoxId = Ext.id(undefined,"serviceBox_");
		var groupPanel = Ext.widget('panel',{
			xtype:'panel',
			border:false,
			padding:'30 0 0 0',
			groupId:groupConfig.groupId,
			minHeight:35,
			items:[{
				xtype:'container',layout:'hbox',
				items:[{
					itemId:'groupName',
					xtype:'label',
					groupBoxId:groupBoxId,
					groupId:groupConfig.groupId,
					text:groupConfig.name,
					style:'color:rgb(13,141,252);font-size:20px;',
					height:30
				}]
			},{
				//此容器为显示 服务 的容器
				xtype:'container',
				id:groupBoxId,
				padding:'0 0 5 0'
			}]
		});
		var groupBox = Ext.getCmp(groupBoxId);
		if(groupConfig.services){
    		for(var i=0;i<groupConfig.services.length;i++){
    			var serviceCmp = me.createServiceCmp(groupConfig.services[i]);
    			groupBox.add(serviceCmp);
    		}
    	}
		return groupPanel;
	},
	createServiceCmp:function(serviceConfig){
        var me = this;
        var iconSrc = sc.setting.iconurl + serviceConfig.icon;
        var point = this.loadType!='preview'?'cursor:pointer;':'';
        var serviceHtml = '<table id="'+serviceConfig.serviceId+'" cmptype="service" serviceid="'+serviceConfig.serviceId+'" border=0 width=175 height=90 cellpadding=0 cellspacing=0 style="'+point+'margin:10px 0px 0px 10px;">'+
          '<tr>'+
	          '<td  style="padding:0 0 0 0;">'+
		      	'<img src="'+iconSrc+'" width=175px height=90px>'+
		      '</td>'+
          '</tr>'+
        '</table>';
        var serviceCmp = Ext.widget('container',{
            scope:me,
            width:185,
            height:110,
            style:"float:left",
            itemId:"serviceContainer"+serviceConfig.serviceId,
            items:[{
                xtype:'container',
                width:175,
                height:90,
                itemId:'serviceHtml'+serviceConfig.serviceId,
                html:serviceHtml
            },{
                xtype:'panel',
                width:95,
                height:44,
                itemId:'nameHtml'+serviceConfig.serviceId,
                border:false,
                bodyStyle:'background:transparent;',
				html:"<div style='word-break:break-all;color:white;"+point+"font-size:16px;'>"+serviceConfig.name+"</div>",
				style:'position:absolute;right:6px;top:34px;'
            }],
            listeners:{
                element:'el',
                click:function(evt,tableEle){
                    if('preview'!=me.loadType){
                        menuid = tableEle.getAttribute("menuid");
                        if(serviceConfig.type==1){
                        	me.printServiceClick(serviceConfig);
                        }else if(serviceConfig.type==2){
                            me.viewServiceClick(serviceConfig,serviceConfig.serviceId,serviceConfig.name,serviceConfig.url)
                        }
                    }
                }   
            }
        });
        return serviceCmp;
    }
});