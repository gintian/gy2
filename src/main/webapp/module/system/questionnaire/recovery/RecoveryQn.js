Ext.define("QuestionnaireRecovery.RecoveryQn", {
	requires:["EHR.extWidget.proxy.TransactionProxy"],
	extend : 'Ext.panel.Panel',
	planId : '',
	qnName : undefined,
	qnId : '',
	anName:'',
	code:'root',
	questionres:undefined,
	formpanel:undefined,
	transmissionWin:undefined,
	leftpanel:undefined,
	layout : 'fit',
	border:false,
	onRender : function() {
		this.callParent();
		this.qnPublishdata();
	},
	//获得数据
	qnPublishdata : function() {
		var me = this;
		var map = new HashMap();
		map.put("planid", me.planId);
		map.put("qnid", me.qnId);
		Rpc({functionId : 'QN50000001',success : function(response) {
						var value = response.responseText;
						var valuetext = Ext.decode(value);
						var shareurl = valuetext.planid;
						var index = document.location.href.indexOf("module/system/questionnaire");
						var url = document.location.href.substring(0, index);
						var drcodeurl = url+ "module/system/questionnaire/template/AnswerQn.jsp"+ "?suerveyid=" + shareurl;
						var drcodeurltwo = url+ "module/system/questionnaire/mobile/index.jsp?planid=" + shareurl;//"手机端答题页面还在研发中，敬请期待！";//暂适用于二维码
						//生成页面
						me.qnPublishBegin(drcodeurl,value,drcodeurltwo);
					}
				}, map);
	},
	//开始创建
	qnPublishBegin : function(drcodeurl,value,drcodeurltwo) {
		var me = this;
		me.leftPanel = me.createLeftpanel(drcodeurl,value,drcodeurltwo);
		var valuetext = Ext.decode(value);
		var qnset = valuetext.qnset;
		var oneip='';
        var onlyone='';
        var enddateselected='';
        var enddatevalue='';
        var autoclosevalue='';
        var requiredlogin='';
        var searchanswer='';
        var autocloseselected='';
        if(qnset==null||qnset==''||!qnset){

        }else{
        	oneip=qnset.oneip;
	        onlyone=qnset.onlyone;
	        enddateselected=qnset.enddateselected;
	        enddatevalue=qnset.enddatevalue;
	        autoclosevalue=qnset.autoclosevalue;
	        requiredlogin=qnset.requiredlogin;
	        searchanswer=qnset.searchanswer;
	        autocloseselected=qnset.autocloseselected;
        }
		me.formpanel = Ext.widget('panel', {
			layout : 'border',
			border : false,
			items : [me.leftPanel,{
				xtype : 'panel',region:'east',
				border:0,
				items:[
				{
								xtype : 'image',height : 36,width : 36,border : 0,margin:'15 0 0 0 ',title:QN.recovery.hidecollectsetting,
								src : rootPath+'/module/system/questionnaire/images/directingright.png',
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var settings = Ext.getCmp('settings');
											if(settings.isVisible()){
												settings.setVisible(false);
												this.setSrc(rootPath+'/module/system/questionnaire/images/directingleft.png');
											}else{
												settings.setVisible(true);
												this.setSrc(rootPath+'/module/system/questionnaire/images/directingright.png');
											}
										}, this);
									}
								}
				}]
				},{
				xtype : 'panel',region:'east',
			    header:false,id : 'settings',border : 1,bodyStyle:"border-top:none;"
				,items : [{
				xtype:'form',border:0,
				          title : QN.recovery.collectionconfiguration,
				          bodyPadding :10,
				          items:[{
							xtype : 'panel',layout : 'hbox',border : 0,
							padding : '8 0 8 0',
						    items : [{
								xtype : 'checkbox',name : 'oneip',value:oneip,inputValue : '1',uncheckedValue:'0',flex:10,
								id : 'checkboxoneip'
						}, {xtype : 'label',padding : '0 0 0 5',text : QN.recovery.onlyipone,style:'font-size:14px !important;'}]
					}, {
						xtype : 'panel',layout : 'hbox',border : 0,padding : '0 0 8 0',
						items : [{
							xtype : 'checkbox',name : 'requiredlogin',value:requiredlogin,inputValue : '1',
							uncheckedValue:'0',id : 'checkboxreq'
						}, {xtype : 'label',padding : '0 0 0 5',text : QN.recovery.nologinnoanswer, style:'font-size:14px !important;'}]
					}, {
						xtype : 'panel',layout : 'hbox',border : 0,padding : '0 0 8 0',
						hidden:true,
						items : [{
							xtype : 'checkbox',name : 'onlyone',value:onlyone,inputValue : '1',
							uncheckedValue:'0',id : 'checkboxonly'
						}, {xtype : 'label',padding : '0 0 0 5',text : QN.recovery.everytemonlyone, style:'font-size:14px !important;'}]
					}, {
						xtype : 'panel',layout : 'hbox',border : 0,padding : '0 0 8 0',
						items : [{
							xtype : 'checkbox',name : 'enddateselected',value:enddateselected,
							inputValue : '1',uncheckedValue:'0',id : 'checkboxend'
						}, {xtype : 'label',padding : '0 0 0 5',html : QN.recovery.questionnairevalidity, style:'font-size:14px !important;'
						}, {id : "enddatevalue",xtype : 'numberfield',value:enddatevalue,width : 50,minValue: 1
						}, {xtype : 'label',html : QN.recovery.dayend, style:'font-size:14px !important;'
						}]
					}, {
						xtype : 'panel',layout : 'hbox',border : 0,padding : '0 0 8 0',
						items : [{
							xtype : 'checkbox',name : 'searchanswer',value:searchanswer,
							inputValue : '1',uncheckedValue:'0',id : 'checkboxsea'
						}, {xtype : 'label',padding : '0 0 0 5',text : QN.recovery.answerviewresult, style:'font-size:14px !important;'}]
					}, {
						xtype : 'panel',layout : 'hbox',border : 0,padding : '0 0 0 0',
						items : [{
							xtype : 'checkbox',name : 'autocloseselected',value:autocloseselected,
							inputValue : '1',uncheckedValue:'0',id : 'checkboxauto'
						}, {xtype : 'label',padding : '0 0 0 5',html : QN.recovery.collect, style:'font-size:14px !important;'
						}, {id : "autoclosevalue",xtype : 'numberfield',value:autoclosevalue,width : 50,minValue: 1
						}, {xtype : 'label',html : QN.recovery.automaticquestionnaire, style:'font-size:14px !important;'
						}]
					}],
					buttonAlign:'center',
					bbar:[{xtype:'toolbar',
					               border:0,items:[{
							        text:QN.template.questionnaireSave,
							    	id:'subbutton',
							    	margin:'0 120 10 120',
							    	handler:function(){
							    		var map = new HashMap();
											me.checkboxsets();
											var questionres= Ext.encode(me.questionres);
											var flagarray = me.getselectpanelid();
											map.put("qnid", me.qnId);
											map.put("qnName", me.qnName);
											map.put('questionres',questionres);
											map.put("flagarray",Ext.encode(flagarray));
											Rpc({functionId : 'QN50000002',success : function(){
														Ext.Msg.alert('提示信息','保存成功！');
											}}, map);
						}}]}]
				}]
			}]
		});
		me.createselectpanel(value);
		this.add(this.formpanel);
	},
	checkboxsets:function(){
		var me = this;
		me.questionres = me.formpanel.items.get(2).query('form')[0].getValues(false,false,true,false);
	},
	//创建参数配置左边页面
	createLeftpanel:function(drcodeurl,value,drcodeurltwo){
		var me = this;
		 return me.leftpanel=Ext.widget('panel',{flex:1,region:'center', autoScroll:true,
				border : 1,bodyStyle:"border-top:none;border-right:none;border-left:none;",
				items : [{
					xtype : 'panel',border : 0,width:800,
					items : [{
							xtype : 'panel',layout :{ type:'vbox',align:'stretch'},border :0,
							items : [{
							xtype:'panel',layout:'hbox',border:0,items:[{xtype:'label',
	    			        style:'color:#999999;font-size:20px !important;',
	    			        text:QN.recovery.questionnaireAccessLink,
							margin : '20 0 10 50',flex:10
						}]}, {xtype : 'label',padding : '0 0 0 70',text : QN.recovery.questionnaireTip, style:'font-size:14px !important;'
						}, {xtype:'container',layout:'hbox',border:0,padding : '0 0 0 70',items:[{
							xtype : 'textfield',width : 600,id : 'text1',value:drcodeurl,//height:22,
							readOnly:true,fieldStyle:'background:#f8f8f8'
						},{xtype:'button',margin : '0 0 0 8',text:common.button.copy,hidden:!Ext.isIE,handler:function(){
							    var text1 = Ext.getCmp('text1').getValue();
								me.copycode(text1);
						}}]}]
					}, {
						xtype : 'panel',layout : 'vbox',border : 0,
							items : [{xtype:'label',
	    			        style:'color:#999999;font-size:20px !important;',
	    			        text:QN.recovery.twodimension,
							margin : '20 0 10 50'
						}, {
							xtype : 'panel',layout : 'hbox',border : 0,
							items : [{
										xtype : 'image',id : 'decode',margin : '0 0 0 70',
										src:'/servlet/TwodimensionCodeServlet?url='+ encodeURI(encodeURI(drcodeurltwo))+'&width=118&height=118',
										height : 118,width : 118
									}, {xtype : 'label',padding : '0 0 0 10',text : QN.recovery.wechatshare, style:'font-size:14px !important;'
									}]
						},{
						    xtype:'label',text:QN.recovery.mobileAccessLink,margin : '20 0 0 70'
						},{
							xtype:'container',layout:'hbox',
							items:[{
							    xtype : 'textfield',width : 600,margin : '0 0 10 70',readOnly:true,fieldStyle:'background:#f8f8f8',
							    value:drcodeurltwo,height:22
							},{
								xtype:'button',margin : '0 0 0 8',text:common.button.copy,hidden:!Ext.isIE,handler:function(){
								    var text1 = this.ownerCt.items.items[0].getValue();
									me.copycode(text1);
								}		
							}]
						}]
					}
					/***
					, {
						xtype : 'panel',layout : 'vbox',border : 0,
							items : [{xtype:'label',
	    			        style:'color:#999999;font-size:20px !important;',
	    			        text:QN.recovery.socialshare,
							margin : '20 0 10 50'
						}, {
							xtype : 'textarea',padding : '0 0 0 70',id : 'textarea1',
							height : 110,width : 600
						}, {
							xtype : 'panel',padding : '0 0 0 70',border : 0,
							items : [
							{
								xtype : 'image',height : 17,width : 17,border : 0,
								margin : '5 5 0 5',title : QN.recovery.turntosinaweibo,
								src : rootPath+'/module/system/questionnaire/images/weibo.png',
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var title = Ext.getCmp('textarea1').getValue();
											me.sharePlugin('sina', '', title,drcodeurl);
										});
									}
								}
							}, {
								xtype : 'image',height : 17,width : 17,border : 0,
								margin : '5 5 0 5',src : rootPath+'/module/system/questionnaire/images/renren.png',
								title:QN.recovery.turntorenren,
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var title = Ext.getCmp('textarea1').getValue();
											me.sharePlugin('renren', '', title,drcodeurl);
										});
									}
								}
							}, {
								xtype : 'image',height : 17,width : 17,border : 0,
								margin : '5 5 0 5',src : rootPath+'/module/system/questionnaire/images/tengxunweibo.png',
								title:QN.recovery.turntotencentweibo,
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var title = Ext.getCmp('textarea1').getValue();
											me.sharePlugin('tqq', '', title,drcodeurl);
										});
									}
								}
							}, {
								xtype : 'image',height : 17,width : 17,border : 0,
								margin : '5 5 0 5',src : rootPath+'/module/system/questionnaire/images/kongjian.png',
								title:QN.recovery.turntoqzone,
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var title = Ext.getCmp('textarea1').getValue();
											me.sharePlugin('qzone', '', title,drcodeurl);
										});
									}
								}
							}, {
								xtype : 'image',height : 17,width : 17,border : 0,
								margin : '5 5 0 5',src : rootPath+'/module/system/questionnaire/images/weixinfenxiang.png',
								title:QN.recovery.turntopengyouquan,
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var title = Ext.getCmp('textarea1').getValue();
											me.sharePlugin('weixin', '', title,drcodeurltwo);
										});
									}
								}
							}]
						}]
					}
					***/
					,{xtype : 'panel',layout : 'vbox',border : 0,
							items : [{xtype:'label',
	    			        style:'color:#999999;font-size:20px !important;',
	    			        text:QN.recovery.staffshared,
							margin : '15 0 20 50'
						}, {
							xtype : 'panel',padding : '0 0 0 70',border:0,layout:'hbox',items:[
								{xtype:'panel',selectflag:'selectflag',border:1,height : 110,width : 600,
								    front:true,
					   				floating:false,
					   				shadow:false,
					   				padding:'1 0 0 0',
					   				scrollFlags:{overflowX:'',overflowY:''},
					   				layout:{
					   					type:"column"
					   				}}
								]
						},{xtype:'container',border:0,layout:'hbox',items:[{xtype : 'panel',padding : '0 0 0 65',border : 0,
							items : [{
								xtype : 'image',height : 17,width : 17,border : 0,
								margin : '5 5 0 5',title:QN.recovery.selectiontransmissionrange,
								src : rootPath+'/module/system/questionnaire/images/u893.png',
								style : {cursor : 'pointer'},
								listeners : {
									render : function() {
										this.getEl().on('click', function() {
											var selectpanelarry = me.leftpanel.query("panel[selectflag=selectflag]")[0].items.items;
											var idarray = new Array();
											if(selectpanelarry.length>0){
												for(var i=0;i<selectpanelarry.length;i++){
													var panel = selectpanelarry[i];
													var id = panel.id;
													idarray.push(id);
													}
											}
											me.selectTransmission(idarray);
										});
									}
								}
							}]},{xtype:'button',margin : '5 5 0 540',text:QN.recovery.turn,
							handler:function(){
								var selectpanelarry = me.leftpanel.query("panel[selectflag=selectflag]")[0].items.items;
								me.checkboxsets();
								var questionres= Ext.encode(me.questionres);
								if(selectpanelarry.length>0){
									var map = new HashMap();
									var flagarray = new Array();
									for(var i=0;i<selectpanelarry.length;i++){
										var panel = selectpanelarry[i];
										var flag = panel.flag;
										flagarray.push(flag);
										}
										map.put('flagarray',Ext.encode(flagarray));
										map.put('content',drcodeurl);
										map.put('qnid',me.qnId);
										map.put("planid", me.planId);
										map.put("qnName", me.qnName);
										map.put('questionres',questionres);
										Rpc({functionId : 'QN50000004',success : function(res){
											var value = Ext.decode(res.responseText);
											if(value.flag=='ok'){
												Ext.Msg.alert('提示信息','推送成功！');
											}
										}}, map);
								}
							}}]}
							]}]
				}
				]
			});
	},
    //分享
	sharePlugin : function(name, img, title, url) {
		var purl = encodeURIComponent(url);
		switch (name) {
			case 'sina' :
				var url = 'http://service.t.sina.com.cn/share/share.php?url='+ purl + '&appkey=2858164115&title='+ encodeURIComponent("" + title) + '&pic='+ encodeURIComponent(img)+ '&ralateUid=&searchPic=false';
				window.open(url);
				break;
			case 'tqq' :
				var url = 'http://v.t.qq.com/share/share.php?title='+ encodeURIComponent("" + title) + '&url=' + purl+ '&appkey='+ encodeURI("8675d8896e054316bc69755118dea3c9")+ '&site=' + purl + '&pic=' + img;
				window.open(url);
				break;
			case 'kaixin' :
				var url = 'http://www.kaixin001.com/repaste/share.php?rurl='+ purl + '&rcontent=' + encodeURIComponent("" + title)+ '&rtitle=' + encodeURIComponent("欣和食与家");
				window.open(url);
				break;
			case 'renren' :
				var url = 'http://widget.renren.com/dialog/share?resourceUrl='+ purl + '&title=' + title + '&description=' + ''+ '_blank';
				window.open(url);
				break;
			case 'douban' :
				var url = 'http://www.douban.com/recommend/?url=' + purl+ '&title=' + encodeURIComponent("" + title);
				window.open(url);
				break;
			case 'qzone' :
				var url = 'http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url='+ purl+ '&title='+ encodeURIComponent("" + title)+ '&pics='+ img;
				window.open(url);
				break;
			case 'weixin' :
                var weixinpanel = Ext.widget('window',{
                     height:238,
                     modal:true,
                     width:268,
                     bodyStyle : 'overflow-x:hidden; overflow-y:auto;background-color:#FFFFFF',
                     layout:'vbox',
                     items:[{xtype:'label',text:QN.recovery.turntopengyouquan},{xtype:'image',height : 118,width : 118,margin:'5 75 5 70',src:'/servlet/TwodimensionCodeServlet?url='+ encodeURI(encodeURI(url))},{xtype:'label',text:QN.recovery.openweixin},{xtype:'label',text:QN.recovery.usesaoyisao}]
                });
                weixinpanel.show();
			    break;
		}
	},
	//复制操作
	copycode:function(text){
		if (window.clipboardData) 
        {
            window.clipboardData.setData("Text", text)
        } 
        else 
        {
            var flashcopier = 'flashcopier';
            if(!document.getElementById(flashcopier)) 
            {
              var divholder = document.createElement('div');
              divholder.id = flashcopier;
              document.body.appendChild(divholder);
            }
            document.getElementById(flashcopier).innerHTML = '';
            var divinfo = '<embed src="/module/system/questionnaire/images/_clipboard.swf" FlashVars="clipboard='+encodeURIComponent(text)+'" width="0" height="0" type="application/x-shockwave-flash"></embed>';
            document.getElementById(flashcopier).innerHTML = divinfo;
        }
     // Ext.Msg.alert('提示信息','复制成功！');
	},
	//创建选人窗体
	selectTransmission:function(idarray){
		var me=this;
		me.transmissionWin = Ext.widget('window',{
			title:QN.recovery.selectiontransmissionrange,
			buttonAlign:'center', 
			modal:true,
			resizable:false,
			height:500,
			width:500,
			bodyStyle : 'overflow-x:hidden; overflow-y:auto;background-color:#FFFFFF',
			layout:{type:'vbox',align:'stretch'},
			items:[{xtype:'panel',aroundflag:'aroundflag',border:1,width:590,height:230,header:false,//margin:'0 0 5 0',
   				front:true,
   				floating:false,
   				shadow:false,
   				padding:'1 0 0 0',
   				flex:0.25,
   				scrollFlags:{overflowX:'',overflowY:''},
   				layout:{
   					type:"column"
   				},
   				style:{
   					zIndex:99999
   				},listeners:{
	   					render:function(){
	   						this.setPosition(16,0);
	   					}
	   				}
   				},
				{xtype:'tabpanel',border:1,
				    flex:0.65,
				    items: [{
				    	xtype:'treepanel',
						title:QN.recovery.organization,
			    		width:170,
			    		rootVisible:false,
			    		split:false,
			    		region:'west',
			    		border:false,
			    		bodyStyle:"border-top:none",
			    		store:{
			    			fields:['codesetid','id','text','nbase'],
			    			proxy:{
			        			type:'transaction',
			        			functionId:'QN50000003',
			        			extraParams:{
			        				codesetid:'UN',
			        				value:idarray,
			        				autoCheck:true
			        			}
			        		}
			    		},
    		listeners:{
				beforecheckchange:function(){
					var orgtreearray = me.transmissionWin.query("panel[aroundflag=aroundflag]")[0].items.items;//已经选择的
					if(orgtreearray.length>=25){
						Ext.showAlert("最多选择25项！");
						return false;
					}
				},
    			checkchange:function(node,checked, eOpts){
    				var hi = this;
    				var flag='0';
    				if(checked==true){
    					me.dochangecheck(node,flag);
    				}
    				else{
    					me.docanclecheck(node,flag);
    				}
    			}
    		}
    	}
    	,{xtype:'treepanel',title: QN.recovery.role,width:170,
			    		rootVisible:false,
			    		split:false,
			    		region:'west',
			    		border:false,
			    		bodyStyle:"border-top:none",
			    		store:{
			    			fields:['flag','id','text'],
			    			proxy:{
			        			type:'transaction',
			        			functionId:'QN50000003',
			        			extraParams:{
			        				codesetid:'ROLE',
			        				value:idarray,
			        				autoCheck:true
			        			}
			        		}
			    		},listeners:{
								beforecheckchange:function(){
									var orgtreearray = me.transmissionWin.query("panel[aroundflag=aroundflag]")[0].items.items;//已经选择的
									if(orgtreearray.length>=25){
										Ext.showAlert("最多选择25项！");
										return false;
									}
								},
    			checkchange:function(node,checked, eOpts){
    				var hi = this;
    				var flag = '1';
    				if(checked==true){
    					me.dochangecheck(node,flag);
    				}
    				else{
    					me.docanclecheck(node,flag);
    				}
    			}
    		}}
    	]}],
			    buttons:[{
		    	text:common.button.ok,
		    	margin:'0 10 10 0',
		    	handler:function(){
		    		var orgtreearray = me.transmissionWin.query("panel[aroundflag=aroundflag]")[0].items.items;//弹出的选人框
		    		var selecttreearray = me.leftpanel.query("panel[selectflag=selectflag]")[0].items.items;//参数配置处的选人框
		    		var orgtree = new Array();
		    		if(selecttreearray.length>0){
		    			for(var k=0;k<selecttreearray.length;k++){
		    				var selectpanel = selecttreearray[k];
		    				var selectid = selectpanel.id;
		    				orgtree.push(selectid);
		    			}
		    		}
		    		
		    		if(orgtreearray.length>0){
			    		for(var j=0;j<orgtreearray.length;j++){
			    			var panel = orgtreearray[j];
			    			var textvalue = panel.items.items[0].names;
			    			var id = panel.ids;
			    			var codesetid = panel.codesetids;
			    			var nbase = panel.nbases;
			    			me.addindexof();
			    			if(orgtree.indexOf(id)==-1){
			    				me.selectpanel=me.creatselectpanel(id,textvalue,codesetid,nbase);
			    				me.leftpanel.query("panel[selectflag=selectflag]")[0].add(me.selectpanel);
			    			}
			    		}
		    		}
		    		me.transmissionWin.close();
		    	}			    		
	    },{
		    	text:common.button.cancel,
		    	margin:'0 0 10 10',
		    	handler:function(){
		    		me.transmissionWin.items.items[0].removeAll();
		    		me.transmissionWin.close();
		    	}			    		
	    }]
		}).show();
		var selectpanelarry = me.leftpanel.query("panel[selectflag=selectflag]")[0].items.items;
		if(selectpanelarry.length>0){//证明原来选过
			for(var i=0;i<selectpanelarry.length;i++){
				var panel = selectpanelarry[i];
				var ids=panel.id;
				var nbases = panel.nbase;
				var codesetids = panel.codesetid;
				var textvalue = panel.items.items[0].nameful;
			    me.transmissionWin.query("panel[aroundflag=aroundflag]")[0].add(me.creatstaffpanel(ids,textvalue,codesetids,nbases));
			}
		}
	},
	//树节点选中
	dochangecheck:function(treepanel,flag){
		var me = this;	
		var orgtreearray = me.transmissionWin.query("panel[aroundflag=aroundflag]")[0].items.items;//已经选择的
		if(flag=='0'){//组织机构树 选择
			this.code=treepanel.data.id;
			var nbase = treepanel.data.nbase;//人员库前缀
			var textValue = treepanel.data.text;
    		var codesetid = treepanel.get('codesetid');
			var idarray = new Array();
			if(orgtreearray.length>0){
				for(var i=0;i<orgtreearray.length;i++){
			    var aroundpanel = orgtreearray[i];
			    var id=aroundpanel.ids;
			    idarray.push(id);
				}
				me.addindexof();
				if(treepanel.data.expanded==false&&treepanel.data.leaf==false){
					me.expandtree(treepanel);
				}
				me.selectParent(idarray,treepanel,this.code,textValue,codesetid,nbase);
				if(treepanel.data.leaf==false){//如果有子节点
				 	me.uncheckNode(treepanel);
				 	me.canclechild(treepanel,idarray);
			 	}
			}else{
				if(treepanel.data.expanded==false&&treepanel.data.leaf==false){
					me.expandtree(treepanel);
				}
				if(treepanel.data.leaf==false){//如果有子节点
					me.uncheckNode(treepanel);
				}
				me.staffpanel = me.creatstaffpanel(this.code,textValue,codesetid,nbase);
    		    me.transmissionWin.items.items[0].add(me.staffpanel);
			}
		}
		if(flag=='1'){
			this.code=treepanel.data.id;
    		var textValue = treepanel.data.text;
    		var nbase = "";
    		var codesetid = treepanel.get('flag');
    		var idarray = new Array();
			if(orgtreearray.length>0){
				for(var i=0;i<orgtreearray.length;i++){
			    var aroundpanel = orgtreearray[i];
			    var id=aroundpanel.ids;
			    idarray.push(id);
				}
			me.addindexof();
				 if(idarray.indexOf(this.code)==-1&&this.code!='-1'){
			  	 	me.staffpanel = me.creatstaffpanel(this.code,textValue,codesetid,nbase);
    		        me.transmissionWin.items.items[0].add(me.staffpanel);
			  	 } 
			}else{
				if(this.code!='-1'){
					me.staffpanel = me.creatstaffpanel(this.code,textValue,codesetid,nbase);
    		        me.transmissionWin.items.items[0].add(me.staffpanel);
				}   
			}
		}
    	if(orgtreearray.length>25){//不能超过5排
    		var staffpanel = Ext.getCmp(this.code+'st');
			me.transmissionWin.items.items[0].remove(staffpanel,true);
    	}	
	},
	//展开树节点
	expandtree:function(node){
		var me = this;
		node.expand(); 
		node.eachChild(function(child) {     
	        if(child.data.expanded==false&&child.data.leaf==false){
				me.expandtree(child);
			}
		}); 
	},
	//删除选中区域中的panel
	canclechild:function(node,idarray){
		var me = this;
		node.eachChild(function(child) {
		 		var childid = child.data.id;//子节点id
		 		if(idarray.indexOf(childid)!=-1){
		 			var staffpanel = Ext.getCmp(childid+'st');
		            me.transmissionWin.items.items[0].remove(staffpanel,true);
		 		}
		 		me.canclechild(child,idarray);
		});
	},
	//树节点取消选中
	docanclecheck:function(node,flag){
		var me = this; 
		var orgtreearray = me.transmissionWin.query("panel[aroundflag=aroundflag]")[0].items.items;//已经选择的
		if(flag=='0'){
			me.uncheckNode(node);
			this.code=node.data.id;
			var staffpanel = Ext.getCmp(this.code+'st');
			me.transmissionWin.items.items[0].remove(staffpanel,true);
			
			this.code=node.data.id;
			var nbase = node.data.nbase;//人员库前缀
			var textValue = node.data.text;
    		var codesetid = node.get('codesetid');
			var idarray = new Array();
			if(orgtreearray.length>0){
				for(var i=0;i<orgtreearray.length;i++){
			    var aroundpanel = orgtreearray[i];
			    var id=aroundpanel.ids;
			    idarray.push(id);
				}
			me.canclechild(node,idarray);
			}
		}
		if(flag=='1'){
			this.code=node.data.id;
			var staffpanel = Ext.getCmp(this.code+'st');
			me.transmissionWin.items.items[0].remove(staffpanel,true);
		}
		
	},
	//取消子节点选中
	uncheckNode:function(node) {
	   var me = this;
	   if (node.data.leaf==false) {
	     for ( var i = 0; i < node.childNodes.length; i++) {
	     	node.childNodes[i].set("checked",false);
	     	me.uncheckNode(node.childNodes[i]);
	    	}
	   	}
	 },
	 //判断父节点是否选中
	 selectParent:function(idarray,treepanel,code,textValue,codesetid,nbase){
	 	var me = this;
	 	me.addindexof();
	 	if(treepanel.parentNode!=null){	
		var pcode=treepanel.parentNode.data.id;
    		var staff = Ext.getCmp(code+'st');
    		if(idarray.indexOf(pcode)==-1){//父节点没有选中
				 	if(idarray.indexOf(code)==-1){
				 		if(typeof staff =='undefined'){
				 			me.staffpanel = me.creatstaffpanel(code,textValue,codesetid,nbase);
	    		        	me.transmissionWin.items.items[0].add(me.staffpanel);
				 		}
				  	 }
				  	 me.selectParent(idarray,treepanel.parentNode,code,textValue,codesetid,nbase);
			}else{
				if(typeof staff !='undefined'){
					me.transmissionWin.items.items[0].remove(staff,true);
				}
			}
		}
	 },
	//创建选人框中的panel
	creatstaffpanel:function(code,textValue,codesetid,nbase){
		var me  = this;
		var staffpanel =  Ext.widget('container',{
			ids:code,
			nbases:nbase,
			id:code+'st',
			codesetids:codesetid,
			myTextValue:textValue,
			
		  style:{border:"solid #c5c5c5 1px",backgroundColor:'#f8f8f8'},
          margin:1,
          width:80,
          height:19,
          layout:'hbox',
          items:[{xtype:'component',flex:10,
          	names:textValue,
          	autoEl: {
		        tag: 'div',
		        style:'white-space:nowrap; text-overflow:ellipsis;overflow: hidden;padding-left:2px',
		        html:textValue,
		        title:textValue
		    }
          },{
             xtype:'component',width:19,height:17,padding:'1 0 0 6',
             html:'X',
             style:'color:#e4393c',
             listeners:{
          		render:function(){
          		    var delButton = this;
          			this.getEl().on('click',function(){
          			   delButton.ownerCt.destroy();
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
		return staffpanel;
	},
	setImgSrc:function(){
		this.items.items[1].setWidth(20);
		this.items.items[0].setWidth(this.items.items[0].getWidth() - 20);
		
		// 设置边框为红色
		this.getEl().setStyle("border","solid #ff8c26 1px");
		this.setBodyStyle("background-color","#feefe5");
		this.items.items[0].setBodyStyle("background-color","#feefe5");
		this.items.items[0].getEl().setStyle("margin","0 0 0 0");
	},
	clearImgSrc:function(){
		this.items.items[1].setWidth(0);
		this.items.items[0].setWidth(this.items.items[0].getWidth() + 20);
		
		// 设置边框为灰色
		this.getEl().setStyle("border","solid #c5c5c5 1px");
		this.setBodyStyle("background-color","#f8f8f8");
		this.items.items[0].setBodyStyle("background-color","#f8f8f8");
		this.items.items[0].getEl().setStyle("margin","0 5 0 0");
	},
	//解决ie数组indexof报错问题
	addindexof:function(){
		if (!Array.prototype.indexOf)
			{
			  Array.prototype.indexOf = function(elt /*, from*/)
			  {
			    var len = this.length >>> 0;
			    var from = Number(arguments[1]) || 0;
			    from = (from < 0)
			         ? Math.ceil(from)
			         : Math.floor(from);
			    if (from < 0)
			      from += len;
			    for (; from < len; from++)
			    {
			      if (from in this &&
			          this[from] === elt)
			        return from;
			    }
			    return -1;
			  };
			}
	},
	creatselectpanel:function(code,textValue,codesetid,nbase){
		var me  = this;
		var staffpanel =  Ext.widget('container',{
			id:code,
			codesetid:codesetid,
			nbase:nbase,
			flag:codesetid+nbase+code,
			myTextValue:textValue,
		  style:{border:"solid #c5c5c5 1px",backgroundColor:'#f8f8f8'},
          margin:1,
          width:80,
          height:19,
          layout:'hbox',
          items:[{xtype:'component',flex:10,
          	nameful:textValue,
          	autoEl: {
		        tag: 'div',
		        style:'white-space:nowrap; text-overflow:ellipsis;overflow: hidden;padding-left:2px',
		        html:textValue,
		        title:textValue
		    }
          },{
             xtype:'component',width:19,height:17,padding:'1 0 0 6',
             html:'X',
             style:'color:#e4393c',
             listeners:{
          		render:function(){
          		    var delButton = this;
          			this.getEl().on('click',function(){
          			   delButton.ownerCt.destroy();
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
		return staffpanel;
	},
	//获得分享内部员工框中的id
	getselectpanelid:function(){
		var me=this;
		var selectpanelarry = me.leftpanel.query("panel[selectflag=selectflag]")[0].items.items;
		var flagarray = new Array();
		if(selectpanelarry.length>0){
			var map = new HashMap();
			for(var i=0;i<selectpanelarry.length;i++){
				var panel = selectpanelarry[i];
				var flag = panel.flag;
				flagarray.push(flag);
				}
		}
		return flagarray;
	},
	//起初加载数据
	createselectpanel:function(value){
		var me=this;
		var valuetext = Ext.decode(value);
		var qnset = valuetext.qnset;
		var pushids='';
		if(Ext.encode(qnset).length==2){

        }else{
        	pushids = qnset.pushids;//UM:010101:集团领导,PE:Usr:00000028:杨柳,PE:Usr:00000035:秦学海
        	if(pushids==''){//this.code,textValue,codesetid,nbase
        	}else{
        		var pushidsarr = pushids.split(',');
		        var selectitems = [];
		        for(var i=0;i<pushidsarr.length;i++){
		        	var pushid=pushidsarr[i];
		        	if(pushid.indexOf('PE')!=-1){
		        		var pusharry = pushid.split(':');
			        	for(var j=0;j<pusharry.length;j++){
				        	var panel = me.creatselectpanel(pusharry[2],pusharry[3],pusharry[0],pusharry[1]);
			        	    selectitems.push(panel);
			        	}
		        	}else{
		        		var pusharry = pushid.split(':');
			        	for(var j=0;j<pusharry.length;j++){
				        	var panel = me.creatselectpanel(pusharry[1],pusharry[2],pusharry[0],"");
			        	    selectitems.push(panel);
			        	}
		        	}
		        	
		        }
		        me.leftpanel.query("panel[selectflag=selectflag]")[0].add(selectitems);
        	}
	    }
	}
	
});