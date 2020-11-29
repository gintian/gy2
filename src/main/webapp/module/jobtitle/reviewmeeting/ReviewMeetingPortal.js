/**
 * haosl add 2015/12/09
 * 新版评审会议
 */
Ext.define("ReviewMeetingURL.ReviewMeetingPortal",{
	requires:[
		'EHR.extWidget.proxy.TransactionProxy',
		'ReviewMeetingURL.ReviewMeetingSetting'],
	schemeArray:null,
	statusMap:null,
	pagingNum:[1,10],//默认[0]显示第一页，[1]每页显示5条
	constructor : function(){
		this.init();
	},
	init:function(){
		
		ReviewMeetingPortal = this;
		
		this.statusMap = new HashMap();
		this.statusMap.put("01",zc.label.qicao);//起草
		this.statusMap.put("05",zc.label.running);//进行中
		this.statusMap.put("06",zc.label.finish);//结束
		this.statusMap.put("09",zc.label.stop);//暂停
		
		//默认查询方案
		this.schemeArray=this.schemeArray || ['all','0','0'];
		//查询权限信息
		var map = new HashMap();
		map.put("opt","8");
		//创建会议的权限
		Rpc({functionId:'ZC00002313',async:false,success:function(res){
	 		var result = Ext.decode(res.responseText);	
			if(result.succeed){
				ReviewMeetingPortal.createMeetingFunc = result.createMeetingFunc;
			}
	 	},scope:this},map);
		//查询会议
		this.getMeetingStoreSync();
		
		this.createCssStyle();
		this.getMainView();
		
		//默认选中全部以及本单位
		var cmp = Ext.getCmp("all");
		cmp.addCls('scheme-selected-cls');
		cmp = Ext.getCmp("selfunit");
		cmp.addCls('scheme-selected-cls');
	},
	/**
	 * 自定义样式复写
	 */
	createCssStyle:function(){
		/****************查询方案面板  start***********/
		if(!Ext.util.CSS.getRule('.scheme-selected-cls span')){
			Ext.util.CSS.createStyleSheet(".scheme-selected-cls span{text-decoration:underline !important;}","underline");
		}
		if(!Ext.util.CSS.getRule('#meetingPortal_schemePanel span')){
			Ext.util.CSS.createStyleSheet("#meetingPortal_schemePanel span{font-size:13px;color:#1B4A98 !important;cursor:pointer;}");
		}
		
		if(!Ext.util.CSS.getRule('#meetingPortal_schemePanel .x-form-text-default')){
			Ext.util.CSS.createStyleSheet('#meetingPortal_schemePanel .x-form-text-default{height:17px;font-size:13px;color:#1B4A98 !important;font-family:"微软雅黑"}');
		}
		if(!Ext.isIE){
			if(!Ext.util.CSS.getRule('#meetingPortal_schemePanel .x-form-text')){
				Ext.util.CSS.createStyleSheet("#meetingPortal_schemePanel .x-form-text{height:17px;font-size:13px;font-family:'微软雅黑'}");
			}
		}
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-default')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-default{background-image: url('/module/jobtitle/images/reviewmeeting/down.png') !important;background-position:0px 3px !important;border-width:0px;width:16px;height:25px;}","aaa");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-over')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-over{background-image: url('/module/jobtitle/images/reviewmeeting/down.png') !important;background-position:0px 3px !important;border-width:0px;width:16px;height:25px;}","bbb");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-trigger-focus')){
        	Ext.util.CSS.createStyleSheet(".noBorder .x-form-trigger-focus{background-image: url('/module/jobtitle/images/reviewmeeting/down.png') !important;background-position:0px 3px !important;border-width:0px;width:16px;height:25px;}","ccc");
        }
        if(!Ext.util.CSS.getRule('.noBorder .x-form-text-wrap-default')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-wrap-default{border-width:0px;}","card_css");
        }
		if(!Ext.util.CSS.getRule('.noBorder .x-form-text-wrap-focus')){
			Ext.util.CSS.createStyleSheet(".noBorder .x-form-text-wrap-focus{border-width:0px;}","card_css");
		}
		/****************查询方案面板  end***********/
	},
	/**
	 * 会议主界面
	 */
	getMainView:function(){
		var me = this;
		var viewport = Ext.create("Ext.container.Viewport",{
			id:"meetinglist_viewport",
			border:false,
			layout:'fit',
			renderTo:Ext.getBody(),
			items:[{
				xtype:'container',
				padding:'20 15 0 15',
				width:'100%',
				height:'100%',
				layout:{
					type: 'vbox',
				    align: 'center'
				},
				items:[me.searchSchemeView(),//查询方案面板
					me.getMeetingView(),//会议列表面板
					{
						//分页工具
				        xtype: 'pagingtoolbar',
				        width:'100%',
				        store:"meetingStore",
				        style:'border-width:0 1px 1px 1px;',
				        displayInfo: true
					}
				],
				listeners:{
				'resize':function(){
					var meetingListPanel = Ext.getCmp('meetingListPanel');
					if(meetingListPanel)
           			   meetingListPanel.setHeight(Ext.getBody().getHeight()-83);
					var bodyWidth = Ext.getBody().getWidth();
					var meetingNameWidth =  "300px";
					var meetingStatusLeft = "310px";
					var meeting_timerange = '480px';
					if(bodyWidth<=1000){
						meetingNameWidth =  "150px";	
						meetingStatusLeft = "160px";
						meeting_timerange = '250px';
					}
					var meeting_names = Ext.select(".meeting_name").elements;
					var meeting_status = Ext.select(".meeting_status").elements;
					var meeting_timeranges = Ext.select(".meeting_timerange").elements;
					for(var i=0;meeting_names && i<meeting_names.length;i++){
						meeting_names[i].style.width=meetingNameWidth;
						meeting_status[i].style.left=meetingStatusLeft;
						meeting_timeranges[i].style.left=meeting_timerange;
					}
				},
				'afterrender' : function(){
					var codeinput_view = Ext.getDom("codeinput_view");
					if(codeinput_view){
						codeinput_view.style.position='relative';
						codeinput_view.style.left='-55px';
						codeinput_view.style.top='0';
					}
				}
			}
			}]
		});
	},
	/**
	 * 查询方案区域
	 * */
	searchSchemeView:function(){
		var me = this;
		var panel = Ext.widget('panel',{ 
			id:'meetingPortal_schemePanel',
			width:'100%',
			border:false,
			layout: {
		        type: 'hbox'
		    },
			defaults: {
		        margin:'0 0 0 10'
		    },
			items:[{
					xtype : 'label',
					margin:0,
		            html:'<font style="font-size:13px">'+zc.label.searchScheme+'</font>'
	            },{
	            	id:'all',
	            	xtype : 'label',
	                html:"<span onclick='javascript:ReviewMeetingPortal.searchScheme(\"all\");'>"+zc.label.all+"</span>"//全部
	            },{
	            	xtype : 'label',
	            	style:'color:rgb(197, 197, 197)',
	            	text:"|"
	            },me.createYearPicker(),
            	{
	            	xtype : 'label',
	            	style:'color:rgb(197, 197, 197)',
	            	text:"|"
	            },{
		        	id:'qicao',
		        	xtype : 'label',
		            html:"<span onclick='javascript:ReviewMeetingPortal.searchScheme(\"qicao\");'>"+zc.label.qicao+"</span>"//起草
		        },{
		        	id:'running',
		        	xtype : 'label',
		            html:"<span onclick='javascript:ReviewMeetingPortal.searchScheme(\"running\");'>"+zc.label.running+"</span>"//执行中
		        },{
		        	id:'finish',
		        	xtype : 'label',
		            html:"<span onclick='javascript:ReviewMeetingPortal.searchScheme(\"finish\");'>"+zc.label.finish+"</span>"//结束
		        },{
		        	id:'stop',
		        	xtype : 'label',
		            html:"<span onclick='javascript:ReviewMeetingPortal.searchScheme(\"stop\");'>"+zc.label.stop+"</span>"//暂停
		        },{
	            	xtype : 'label',
	            	style:'color:rgb(197, 197, 197)',
	            	text:"|"
	            },{
	            	id:'selfunit',
	            	xtype : 'label',
	                html:"<span onclick='javascript:ReviewMeetingPortal.searchScheme(\"selfunit\");'>"+zc.meetingportal.selfunit+"</span>"//本单位
	            },{
	            	id:'subunit',
	            	xtype : 'label',
	                html:"<span id='subunitEl' targetID='subunitEl' name='subunitEl' plugin='codeselector' "
	                	+"codesetid='UM' onlySelectCodeset='false' inputname='codeinput_view' afterfunc='ReviewMeetingPortal.codeAfterFunc' "
	                	+"ctrltype='3' nmodule='9' isShowLayer='1' multiple=true"
	                	+" onclick='javascript:ReviewMeetingPortal.searchScheme(\"subunit\");'>"+zc.meetingportal.subunit+""
	                	+"</span>"//下属单位
	            },{
	            	id:'newmeeting',
	            	xtype : 'label',
	            	style:"position:static !important;float:right",
	            	hidden:!this.createMeetingFunc,
	                html:"<span onclick='javascript:ReviewMeetingPortal.editMeeting();'>"+zc.meetingportal.newmeeting+"&nbsp;&nbsp;&nbsp;&nbsp;</span>"//创建会议
	            }]
		});
		
		return panel
	},
	/**
	 * 创建会议列表视图
	 */
	getMeetingView : function(){
		var me = this;
		var bodyWidth = Ext.getBody().getWidth();
		var meetingNameWidth =  "width:300px;";
		var meetingStatusLeft = "left:310px;";
		var meeting_timerange = 'left:480px;';
		if(bodyWidth<=1000){
			meetingNameWidth =  "width:150px;";	
			meetingStatusLeft = "left:160px;";
			meeting_timerange = 'left:250px;';
		}
		
		var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="meeting_item_cls" style="outline-width: 0px !important;">',
                        '<div class="meeting_div_left"></div>',
                        '<div class="meeting_div_right"></div>',
                        '<div class="meeting_div_center">',
                        	'<div class="meeting_title_div">',//会议名称和状态
                        		'<tpl if="editMeetingFunc">',//权限控制
                        			'<div class="meeting_name" style="'+meetingNameWidth+'height:19px" title="{name}" onclick="ReviewMeetingPortal.editMeeting(\'{w0301}\',{readOnly},\'{meetingstate}\')">{name}</div>',//会议名称
                        		'<tpl else>',
                        			'<div class="meeting_name" style="'+meetingNameWidth+'height:19px" title="{name}" onclick="ReviewMeetingPortal.editMeeting(\'{w0301}\',true,\'{meetingstate}\')">{name}</div>',//会议名称
                        		'</tpl>',
                    			'<span class="meeting_status" style="'+meetingStatusLeft+'">{[this.getMeetingStatus(values.meetingstate)]}</span>',//会议状态
                    		'</div>',
        					'<tpl if="meetingstate == \'01\'">',//按钮操作
				               '<div style="position:absolute;top:6px;right:5px;">',
				               		'<tpl if="editMeetingFunc">',//权限控制
				               			'<span title="'+zc.meetingportal.edit+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.editMeeting(\'{w0301}\',{readOnly},\'{meetingstate}\')"><image style="width:22px;" src="/module/jobtitle/images/reviewmeeting/editmeeting.png"/></span>',
				               		'</tpl>',
				               		'<tpl if="startMeetingFunc">',//权限控制
				               			'<span title="'+zc.meetingportal.qidong+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.meetingOpt(\'3\',\'{w0301}\',{readOnly})"><image src="/module/jobtitle/images/reviewmeeting/start.png"/></span>',
				               		'</tpl>',
			               			'<tpl if="canDel && delMeetingFunc">',//权限控制
				               			'<span title="'+zc.meetingportal.shanchu+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.meetingOpt(\'1\',\'{w0301}\',{readOnly})"><image src="/module/jobtitle/images/reviewmeeting/delete.png"/></span>',
			               			'</tpl>',
			               		'</div>',	
					        '<tpl elseif="meetingstate == \'05\'">',//运行中
					        	'<tpl if="stopMeetingFunc">',//权限控制
					        		'<div style="position:absolute;top:6px;right:5px;">',
				                		'<span title="'+zc.meetingportal.zanting+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.meetingOpt(\'2\',\'{w0301}\',{readOnly})"><image src="/module/jobtitle/images/reviewmeeting/stop.png"/></span>',
				                	'</div>',	
				                '</tpl>',
				            '<tpl elseif="meetingstate == \'09\'">',//暂停
				            	'<div style="position:absolute;top:6px;right:5px;">',
					            	'<tpl if="editMeetingFunc">',//权限控制
					               		'<span title="'+zc.meetingportal.edit+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.editMeeting(\'{w0301}\',{readOnly},\'{meetingstate}\')"><image style="width:22px;" src="/module/jobtitle/images/reviewmeeting/editmeeting.png"/></span>',
					               	'</tpl>',
					               	'<tpl if="startMeetingFunc">',//权限控制
					               		'<span title="'+zc.meetingportal.qidong+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.meetingOpt(\'3\',\'{w0301}\',{readOnly})"><image src="/module/jobtitle/images/reviewmeeting/start.png"/></span>',
				               		'</tpl>',
				               		'<tpl if="canDel && delMeetingFunc">',
				               			'<span title="'+zc.meetingportal.shanchu+'" style="cursor:pointer;margin-right:5px;" onclick="ReviewMeetingPortal.meetingOpt(\'1\',\'{w0301}\',{readOnly})"><image src="/module/jobtitle/images/reviewmeeting/delete.png"/></span>',
			               			'</tpl>',
			               		'</div>',	
				            '</tpl>',
                    		//会议起止时间和归属单位
				            '<div class="meeting_timerange" style="'+meeting_timerange+'">{startd}&nbsp;'+zc.editmeeting.mainview.until+'&nbsp;{endd}&nbsp;&nbsp;&nbsp;&nbsp;'+zc.editmeeting.mainview.organization+'：{orgname}</div>',
			            	'<table class="segments_table"><tr>',
				            '<tpl for="segments">',//启用的评审环节
				            	'<td class="segments_table_td"><table width="100%" height="60">',
				            		'<tr height="25"><td style="background-image:url({[this.changeImgSrc("bgimg",values.state)]});background-repeat:no-repeat;" class="meeting_icon_xuhao" rowspan="3"><div><span class="seqment_seq">{seq}</span></div></td>',
					            		'<td width="200"><font style="font-size:13px;font-weight:bold;">{[this.getSegmentsName(values.flag)]}</font>&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-size:13px">{[this.getSegmentsStatus(values.state)]}</font></td>',
					            		'<td rowspan="2" class="meeting_icon_jiantou">{[this.changeImgSrc("jtimg",xindex==xcount)]}</td></tr>',
				            		'<tr height="25"><td width="200" style="font-size:13px;">'+zc.meetingportal.attendnumber+'&nbsp;{attendnumber}人&nbsp;&nbsp;'+zc.meetingportal.attendednumber+'&nbsp;{attendednumber}人</td></tr>',
				            		'<tr height="20"><td>', 
				            			'<tpl if="state == \'1\' && parent.meetingstate==\'05\'">',
				            				'<table class="meeting_running_href">',
				            				'<tr>',
				            				'<tpl if="attendnumber==0">',//没有申报人时，参会提醒置灰
				            					'<td width="80">',
					            					'<tpl if="parent.remindFunc">',
					            						'<span style="margin-left:-3px;cursor:auto;color:#7a7a7a;">'+zc.meetingportal.sendmessage+'</a>',
				            						'</tpl>',
			            						'</td>',
				            				'<tpl else>',
				            					'<td width="80">',
					            					'<tpl if="parent.remindFunc">',
					            						'<span style="margin-left:-3px;" onclick="ReviewMeetingPortal.sendMessage(\'{parent.w0301}\',\'{parent.name}\',\'{flag}\',\'{usertype}\',{parent.readOnly})">'+zc.meetingportal.sendmessage+'</span>',
				            						'</tpl>',
			            						'</td>',
		            						'</tpl>',
			            					
			            					'<td>',
			            						'<tpl if="parent.startReviewFunc">',
			            							'<span onclick="ReviewMeetingPortal.startReview(\'{parent.w0301}\',\'{flag}\',\'{evaluation_type}\',{parent.readOnly},\'{usertype}\',false,\'{parent.endSegment}\')">'+zc.meetingportal.startreview+'</span>',
			            						'</tpl>',
			            					'</td>',
				            				'</tr></table>',
				 						'<tpl elseif="state != \'0\'">',//结束状态的只能查看
				 							'<table class="meeting_running_href"><tr>',
					 							'<td width="80">',
					 								'<span onclick="ReviewMeetingPortal.startReview(\'{parent.w0301}\',\'{flag}\',\'{evaluation_type}\',{parent.readOnly},\'{usertype}\',true,\'{parent.endSegment}\')">'+zc.meetingportal.viewResult+'</span>',
					 							'</td>',
					 						'</tr></table>',
					 					'</tpl>',
				            		'</td></tr>',
				            	'</table></td>',
				            	 '<tpl if="xindex == xcount && xcount < 3">',//不足三个阶段的补足td占位，以保证阶段内容可以对齐
				            	 	'<tpl if="xcount==1">',
				            	 		'<td class="segments_table_td"></td><td class="segments_table_td"></td>',
			            	 		'<tpl elseif="xcount==2">',
			            	 			'<td class="segments_table_td"></td>',
				            		'</tpl>',
				            	 '</tpl>',
				            '</tpl>',
				            '</tr></table>',
                        '</div>',
                    '</div>',
                '</tpl>',
                    {
                        changeImgSrc:function (flag,opt){//显示计划的状态（颜色）
                        	var imgsrc = "";
                        	if(flag==="bgimg"){//环节序号圆圈背景
	                        	imgsrc = "'/module/jobtitle/images/reviewmeeting/yuan1.png'";
	                        	if(opt=="1"){
	                        		imgsrc = "'/module/jobtitle/images/reviewmeeting/yuan2.png'";
	                        	}
                        	}else{//箭头图标
                        		if(opt)//是否是最后一个评审环节，是则不显示箭头
                        			imgsrc="";
                        		else
                        			imgsrc = '<img src="/module/jobtitle/images/reviewmeeting/jiantou.png"/>';
                        	}
                        	
                            return imgsrc;
                        },
                        /**
                         * 会议运行状态
                         */
                        getMeetingStatus:function(status){
                        	if(Ext.isEmpty(status))
                        		status = "01";
                        	return me.statusMap[status];
                        },
                        /**
                         * 评审环节状态
                         */
                        getSegmentsStatus:function(state){
                        	var statename = "";
                       		switch(state){
                       			case "0":
                       				statename = zc.label.nostart;
                       				break;
                       			case "1":
                       				statename = zc.label.running;
                       				break;
                       			case "2":
                       				statename = zc.label.finish;
                       				break;
                       			default:
                       				statename = zc.label.nostart;
                       				break;
                       		}
                       		return statename;
                    	},getSegmentsName:function(flag){
                    		var segmentname = ""//环节名称
                    		switch(flag){
                       			case "1":
                       				segmentname = zc.label.inReview+zc.editmeeting.mainview.segmentsps;
                       				break;
                       			case "2":
                       				segmentname = zc.label.inExpert+zc.editmeeting.mainview.segmentsps;
                       				break;
                       			case "3":
                       				segmentname = zc.label.exExpert;
                       				break;
                       			case "4":
                       				segmentname = zc.label.inOther+zc.editmeeting.mainview.segmentsps;
                       				break;
                       			default:
                       				break;
                       		}
                       		
                       		return segmentname;
                    	}
                    }
        );
        var meetingView = Ext.create("Ext.view.View",{
            id:'meetingView',
            store:'meetingStore',
            itemSelector : 'div .meeting_item_cls',
            scrollable:'y',
            tpl:tpl,
            padding:'0 10 0 10',
            emptyText:"<div style='width:100%;text-align:center;'><img style='margin-top:50px' src='/module/jobtitle/images/reviewmeeting/nomeeting.png'></div>",
            multiSelect:false
        });
     return Ext.create("Ext.panel.Panel",{
            id:'meetingListPanel',
            height:Ext.getBody().getHeight()-83,
            width:'100%',
            border:true,
            layout:'fit',
            scrollable:false,
            items:meetingView
        });
	},
	/**
	 * 通过查询方案查询会议列表
	 */
	getMeetingStoreSync:function(){
		var me = this;
		Ext.create("Ext.data.Store",{
			id:'meetingStore',
			fields:['w0301','name','startd','endd','orgname','meetingstate','segments'],
			proxy:{
				type: 'transaction',
		        functionId : 'ZC00002311',
			 	reader: {
				  type : 'json',
				  totalProperty:'totalCount',
				  root : 'meetings'         	
				}
			},
			remoteSort:true,
			pageSize:me.pagingNum[1],
			autoLoad:true,
			listeners:{
				beforeload:function(store){
					//bug 38201   haosl  update
					var extraParams = {
						scheme:me.schemeArray.join(",")
					}
					Ext.apply(store.proxy.extraParams, extraParams);
					var meetingView = Ext.getCmp('meetingView');
					if(meetingView && meetingView.getEl().isScrollable()){
						Ext.getCmp('meetingView').getEl().scrollTo('top',0);
				    }
				}
			}
		})
		
	},
	/**
	 * 
	 * @param {} opt
	 * 			=1删除会议
	 * 			=2暂停会议
	 * 			=3启动会议
	 * @param {} meetingid
	 * @param {} seq 环节序号  参会提醒和发起评审用
	 * 
	 */
	meetingOpt:function(opt,meetingid,readOnly){
		
		if(readOnly){
			Ext.showAlert(zc.editmeeting.mainview.error.noprivmsg);
			return;
		}
		var me = this;
		var map = new HashMap();
		map.put("opt",opt);
		map.put("meetingid",meetingid);
		var confirmmsg = "";
		if(opt==="1"){
			confirmmsg = zc.meetingportal.delmsg;
		}else if(opt=="2"){
			confirmmsg = zc.meetingportal.stopmsg;
		}else if(opt=="3"){
			//校验会议信息是否完善
			var canStart = this.isCommitteConsummate(meetingid);
			if(!canStart)
				return;
			confirmmsg = zc.meetingportal.startmsg;
		}
			
		Ext.showConfirm(confirmmsg,function(flag){
			if(flag=="yes"){
				Rpc({functionId:'ZC00002312',async:false,success:function(res){
			 		var result = Ext.decode(res.responseText);	
					if(!result.succeed){
						Ext.showAlert(result.message);
					}
			 		var meetingStore = Ext.data.StoreManager.lookup("meetingStore");
				 		meetingStore.load({
						params:{
							scheme:me.schemeArray.join(",")
						}
					});
			 	},scope:this},map);
			}
		})
	},
	/**
	 * 启动会议之前需要校验会议信息是否完整
	 * @param {} w0301  会议id
	 * @param {} segments 启用的评审环节
	 */
	isCommitteConsummate : function(meetingid){
		var canStart = false;
		var map = new HashMap();
		map.put("opt","4");
		map.put("meetingid",meetingid);
		Rpc({functionId:'ZC00002312',async:false,success:function(res){
			var result = Ext.decode(res.responseText);
			if(result.msgList.length==0){
				canStart = true;
			}else{
				canStart = false;
				//提示不能启动会议
				var messageInfo = "";
				for(var i=0;i<result.msgList.length;i++){
					var msg = result.msgList[i];
					messageInfo+=(i==0?"":i+".  ")+msg+"<br/>";
					if(i==0)
						messageInfo+="<br/>";
				}
				Ext.showAlert(messageInfo);
			}
		},scope:this},map);
		
		return canStart;
	},
	// 选择年份
	createYearPicker:function(){
		var me = this;
		
		var storeArray = [];
		for(var j=1;j>-11;j--){
            var yearmap =  new HashMap();
            var date = Ext.Date.format(Ext.Date.add(new Date(), Ext.Date.YEAR, j), "Y");
            
            storeArray.push([date, date+zc.meetingportal.year]);
        }
		
		var store = new Ext.data.ArrayStore({
            fields: ['myId','displayText'],
            data: storeArray
        });
		
		return {
		    	xtype:'combo',
		    	fieldLabel: '',
		    	labelSeparator:'',
		    	store:store,
		    	width:80,
		        valueField: 'myId',
		        displayField: 'displayText',
		        editable:false,
		        allowBlank: false,
		        cls:'noBorder',
		        listeners: {  
					afterRender: function(combo) {
	    				var store = combo.getStore();
						if(store.getCount()>0){
		    				record = store.findRecord('myId',Ext.Date.format(new Date(),"Y"));
		    				//默认选中系统年份，没有则选中第一条记录
		    				if(record){
								combo.select(record);
							}else{
								combo.select(store.getAt(0))
							}
						}
		            },
		            change:function(combo, nVal){
		            	me.searchScheme("year",nVal);
		            }
		        }
		    };
	},
	/**
	 * 根据方案查询会议
	 * @param  flag 
	 */
	searchScheme : function(flag,year){
		var me = this;
		var removeClsArr = new Array();
		//添加选中样式
		if("year"!=flag)
			Ext.getCmp(flag).addCls('scheme-selected-cls');
		switch(flag){
			case 'all'://全部
				me.schemeArray = ['all','0','0'];
			
				removeClsArr[0]='qicao';
				removeClsArr[1]='running';
				removeClsArr[2]='finish';
				removeClsArr[3]='stop';
				removeClsArr[4]='subunit';
				
				Ext.getCmp("selfunit").addCls('scheme-selected-cls');
				break;
			case 'qicao'://起草
				me.schemeArray[0]='01';
				
				removeClsArr[0]='all';
				removeClsArr[1]='running';
				removeClsArr[2]='finish';
				removeClsArr[3]='stop';
				break;
			case 'running'://进行中
				me.schemeArray[0]='05';
				
				removeClsArr[0]='all';
				removeClsArr[1]='qicao';
				removeClsArr[2]='finish';
				removeClsArr[3]='stop';
				break;
			case 'finish'://完成
				me.schemeArray[0]='06';
				removeClsArr[0]='all';
				removeClsArr[1]='qicao';
				removeClsArr[2]='running';
				removeClsArr[3]='stop';
				break;
			case 'stop'://暂停
				me.schemeArray[0]='09';
				
				removeClsArr[0]='all';
				removeClsArr[1]='qicao';
				removeClsArr[2]='finish';
				removeClsArr[3]='running';
				break;
			case 'selfunit'://本单位
				me.schemeArray[2]='0';
				removeClsArr[0]='subunit';
				break;
			case 'subunit'://下属单位
				setEleConnect(['subunitEl']);
				removeClsArr[0]='selfunit';
				break;
			case 'year'://年度
				me.schemeArray[1] = year+"";
				break;
			default: 'all'
				me.schemeArray = ['all','0','0'];
				Ext.getCmp("selfunit").addCls('scheme-selected-cls');
				break;
		}
		//清除选中样式
		for(var i in removeClsArr){
			var obj = Ext.getCmp(removeClsArr[i]);
			if(obj)
				obj.removeCls('scheme-selected-cls');
		}
		//下属单位单独处理
		if("subunit"==flag)
			return;
		var meetingStore = Ext.data.StoreManager.lookup("meetingStore");
		meetingStore.load({
			params:{
				page:1,
				scheme:me.schemeArray.join(",")
			}
		});
		
	},
	codeAfterFunc:function(codeval){
		if(codeval.length==0)
			return;
		ReviewMeetingPortal.schemeArray[2] = codeval;
		Ext.data.StoreManager.lookup("meetingStore").load({
			params:{
				page:1,
				scheme:ReviewMeetingPortal.schemeArray.join(",")
			}
		});
	},
	/**w0301
	 * 创建或编辑会议
	 * 
	 * readOnly 所属机构的编辑权限
	 * w0321 会议状态
	 * 
	 * w0301 为空是新增，非空则是编辑
	 */
	editMeeting : function(w0301,readOnly,w0321){
		//权限范围匹配并且是起草或暂停的才可以编辑
		Ext.create("ReviewMeetingURL.ReviewMeetingSetting",{
			w0301:w0301,
			readOnly:readOnly,
			w0321:w0321
		}).show();
	},
	/**
	 * 参会提醒
	 * w0301 会议id
	 * 
	 * name 会议名称
	 */
	sendMessage:function(w0301,name,segment,usertype,readOnly){
		if(readOnly){
			Ext.showAlert(zc.editmeeting.mainview.error.noprivmsg);
			return;
		}
		Ext.require('SendMessageURL.SendMessage', function(){
			RevewFileGlobal = Ext.create("SendMessageURL.SendMessage", {
				w0301:w0301,
				meetName:name,
				usertype:usertype,
				segment:segment,
				isNewModule:true//新版调用
			});
		});
	},
	/**
	 * 发起评审
	 * @param {} w0301 会议id
	 * @param {} segment  当前评审环节
	 * evaluation_type : =1 投票 =2 评分
	 * readOnly ： true|false是否可编辑
	 * usertype  =1 随机账号 =2选择专家
	 * meetingname 会议名称
	 * isFinished 当前环节是否结束，结束了只能查看
	 * 评审类型：=1 投票  =2 评分
	 */
	startReview:function(w0301,segment,evaluation_type,readOnly,usertype,isFinished,endSegment){
		if(readOnly){
			Ext.showAlert(zc.editmeeting.mainview.error.noprivmsg);
			return;
		}
		if(Ext.isEmpty(isFinished))
			isFinished = false;
		var reviewConsole = Ext.create("ReviewMeetingURL.ReviewConsole",{
			w0301_e:w0301,
			evaluationType:evaluation_type,
			userType:usertype,
			endSegment:endSegment,
			review_links:segment,
			enterType:'2',//创建评审条件入口
			isFinished:isFinished
		})
		var title = ""//环节名称
		switch(segment){
   			case "1":
   				title = zc.label.inReview+(evaluation_type=="1"?zc.editmeeting.mainview.segmentsps:zc.editmeeting.mainview.score)+zc.editmeeting.mainview.consoleview;
   				break;
   			case "2":
   				title = zc.label.inExpert+(evaluation_type=="1"?zc.editmeeting.mainview.segmentsps:zc.editmeeting.mainview.score)+zc.editmeeting.mainview.consoleview;
   				break;
   			case "3":
   				title = zc.label.exExpert+zc.editmeeting.mainview.consoleview;
   				break;
   			case "4":
   				title = zc.label.inOther+(evaluation_type=="1"?zc.editmeeting.mainview.segmentsps:zc.editmeeting.mainview.score)+zc.editmeeting.mainview.consoleview;
   				break;
   			default:
   				break;
   		}
		var table_Grid = reviewConsole.getTableConfig();
		Ext.create("Ext.window.Window",{
				id:'startReviewWin',
				title:title+"（"+reviewConsole.meettingName+"）",
				maximized:true,
				layout:'fit',
				autoScroll:true,
				border:false,
				resizable :false,
				items:[table_Grid],
				listeners:{
					close:function(){
						//刷新会议数据
			     		var meetingStore = Ext.data.StoreManager.lookup("meetingStore");
			     		if(meetingStore){
			     			meetingStore.load({
							params:{
								scheme:ReviewMeetingPortal.schemeArray.join(",")
							}})
			     		}
					}
				}
			}).show();
	/*		
		if(evaluation_type == '1') {//投票状态，按钮向下移
			Ext.getDom("reviewdiffmenu_"+segment).style.top = "16px";
			Ext.getDom("newGroup_"+segment).style.top = "16px";
		}*/
	}
});