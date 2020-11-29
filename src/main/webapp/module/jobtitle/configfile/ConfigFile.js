Ext.define('ConfigFileURL.ConfigFile',{
	pageWidth:document.documentElement.clientWidth,
	constructor:function(){
		configFile_me=this;

		//屏蔽浏览器右键菜单
		Ext.getDoc().on("contextmenu", function(e){
			e.stopEvent();
		});
		//每次点击超链接都会加载一次类，所以要在这赋值
		this.width=document.documentElement.clientWidth*0.7;//panel的宽度
		this.height=document.documentElement.clientHeight*0.3;//panel的长度
		
		if(this.width>"670"){
		    this.size="2";//页面字体的大小
		}else if(this.width<="670"){
			this.size="1";
		}
		//初始化map
		var map = new HashMap();
		map.put("flagid", "");//业务模版类标识，此为初始化，不需要
		map.put("value","");//模版的值，此为初始化，不需要
		map.put("flagmap", "initmap");//标识，判断是初始化业务模版，还是存储，此为初始化
		Rpc({functionId:'ZC00004002',async:false,success:this.initParams},map);//从数据库中查询数据，给全局变量value_map初始化
		
		this.htmlvalue();//初始化panel内容
		
		this.init();
	},
	/**
	 * 初始化panel
	 * **/
init:function(){
		
			//职称认定
			var zhichengPn=new Ext.Panel({  
			    id:'zhichengPn',
			    width:this.width,
			    border:false,
			    html:pn11             
			});
			//考试认定
			var kaoshiPn=new Ext.Panel({  
			    id:'kaoshiPn',
			    width:this.width,
			    border:false,
			    html:pn12             
			});
			
			//认定业务模板
		    var zhiTitle=new Ext.Panel({
		    	id:'zhiTitlePn',
			    border:false,
			    width:this.width,
			    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="20%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+zc.label.rendingPn+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'             
		    });
			//认定业务模版
			var rendingPn=new Ext.Panel({  
				//title:"<font size="+this.size+">"+zc.label.shenbaoPn+"</font>",
				id:'rendingPn',
				width:this.width,
				border:false,
				items:[
                   zhiTitle,zhichengPn,kaoshiPn
                   ]
			});
			//免试备案
			var mianshiPn=new Ext.Panel({  
				id:'mianshiPn',
				width:this.width,
				border:false,
				html:pn21             
			});
			//破格认定
			var pogePn=new Ext.Panel({  
				id:'pogePn',
				width:this.width,
				border:false,
				html:pn22             
			});
			//免试备案、破格业务模版-标题
		    var beiTitle=new Ext.Panel({
		    	id:'beiTitlePn',
			    border:false,
			    width:this.width,
			    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="15%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+zc.label.beianPn+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'             
		    });
			//面试备案、破格业务模版
			var beianPn=new Ext.Panel({  
				//title:"<font size="+this.size+">"+zc.label.beianPn+"</font>",
				id:'beianPn',
				width:this.width,
				border:false,
				items:[
                   beiTitle,mianshiPn,pogePn
                   ]
			}); 
			//论文送审业务模版-标题
		    var lunTitle=new Ext.Panel({
		    	id:'lunTitlePn',
			    border:false,
			    width:this.width,
			    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="15%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+zc.label.lunwenPn+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'             
		    });
			//论文送审业务模版
			var lunwenPn=new Ext.Panel({  
				//title:"<font size="+this.size+">"+zc.label.lunwenPn+"</font>",
				id:'lunwenPn',
				width:this.width,
				border:false,
				html:pn3             
			});
			var lunPn=new Ext.Panel({
				id:'lun',
				border:false,
				items:[lunTitle,lunwenPn]
			});
			//职称申报业务模版-标题
		    var shenTitle=new Ext.Panel({
			    border:false,
			    id:'shenTitlePn',
			    width:this.width,
			    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="15%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+zc.label.shenbaoPn+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'             
		    });
		    //报名申请业务模板
			var baomingPn=new Ext.Panel({  
				id:'baomingPn',
				width:this.width,
				border:false,
				html:pn41             
			});
			//职称申报业务模版
			var shenbaoPn=new Ext.Panel({  
				id:'shenbaoPn',
				width:this.width,
				border:false,
				html:pn4             
			}); 
			var shenPn=new Ext.Panel({
				border:false,
				items:[shenTitle,baomingPn,shenbaoPn]
			});
			// 测评表配置
		    var assmentTitle=new Ext.Panel({
				id:'assmentPn',
			    border:false,
			    width:this.width,
			    html:'<table style="margin:15px 0px 0px 0px;font-size:14px;font-family:Microsoft YaHei;" width="100%" height="15%" align="center" border="0"><tr height="25"><td align="left" width="100"><font >'+zc.label.assessmentTablePn+'</font></td></tr></table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'
		    });
		    var assmentBody = new Ext.Panel({  
				id:'assmentBodyPn',
				width:this.width,
				border:false,
				html:assmenthtml            
			});  
			var assmentPn=new Ext.Panel({
				margin:'16 0 0 0',
				border:false,
				items:[assmentTitle,assmentBody]
			});
			//xus 18/4/3投票方式radio
			var voteWayRadio=new Ext.form.RadioGroup({
				layout:'hbox',
				width:'80%',
				id:'voteWayRadio',
				value:{voteWayRadioComp:value_params.votetype},
		        items: [
		            { boxLabel: zc.label.radioCard,width:'60px', name: 'voteWayRadioComp', inputValue: '1' },
		            { boxLabel: zc.label.radioList, name: 'voteWayRadioComp', inputValue: '2'}
		        ],
		        listeners: {
		            change:function( me, newValue, oldValue, eOpts ) {
		            	if(newValue.voteWayRadioComp=='1')
		            		document.getElementById('voteWaypeizhiLink').style.display ="none";
		            	else
		            		document.getElementById('voteWaypeizhiLink').style.display ="";
		            	configFile_me.saveVoteType(newValue.voteWayRadioComp,"false");
		            }
		        }
			})
			var voteWaypeizhiLinkDisplay=value_params.votetype=="2"?"":"none";
			//xus 18/4/3投票方式
			var voteWayPn=new Ext.Panel({
				id:'voteWayPn',
				layout:{
					type:'hbox'
				},
				margin:'5 0 0 0',//16
				width:this.width,
				border:false,
				items:[{
					xtype:'component',
					id:'voteWayText',
					width:'13%',
					html:'<span style="font-size:14px;font-family:Microsoft YaHei;">'+zc.label.voteWay+'</span>'
				},voteWayRadio,{
					xtype:'component',
					id:'voteWaypeizhiBtnComp',
					width:'7%',
					html:'<div align="right"><a id="voteWaypeizhiLink" style="display:'+voteWaypeizhiLinkDisplay+'" href=javascript:configFile_me.showWin("9")>'+zc.label.peizhiBtn+'</a><div>'
					
				}]
			})
			//评审结果归档方案
		    var guiTitle=new Ext.Panel({
				id:'guiTitlePn',
			    border:false,
			    width:this.width,
			    html:'<table cellspacing="0" cellpadding="0" style="font-size:14px;font-family:Microsoft YaHei;" width="100%" height="15%" align="center" border="0">'
				    	+'<tr height="35">'+
						    '<td align="left" width="10%">'+zc.label.guidangPn+'</td>'+
						    '<td align="right" width="10%" style="white-space:nowrap"><a style="margin-right:3px;" href=javascript:configFile_me.showWin("7")>'+zc.label.peizhiBtn+'</a></td>'+
					    '</tr>'
				    +'</table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'             
		    });
			var guidangPn=new Ext.Panel({
				border:false,
				items:[guiTitle]
			});
			//代表作导出规则
			var masterpieceTitle=new Ext.Panel({
				id:'masterpiecePn',
			    border:false,
			    width:this.width,
			    html:'<table cellspacing="0" cellpadding="0" style="font-size:14px;font-family:Microsoft YaHei;" width="100%" height="15%" align="center" border="0">'
				    	+'<tr height="35">'+
						    '<td align="left" width="10%">'+zc.label.masterpiecePn+'</td>'+
						    '<td align="right" width="10%" style="white-space:nowrap"><a style="margin-right:3px;" href=javascript:configFile_me.showWin("11")>'+zc.label.peizhiBtn+'</a></td>'+
					    '</tr>'
				    +'</table><hr style="border-top:1px solid #EEEEEE; border-bottom:0px; border-left:0px; ">'             
		    });
			var masterpiecePn=new Ext.Panel({
				border:false,
				items:[masterpieceTitle]
			});
			
			
			//配置信息
			var peizhiPn=new Ext.Panel({
				
				//title:zc.label.peizhiPn,
				//width:document.getElementById("divid").width,
				border:false,
				scrollable:false,
				//renderTo:Ext.getBody(),
				layout:{
					type:'vbox',
					padding:'10 100 110 110',//为所有当前布局管理的子项('items')设置padding值
					align:'stretch'// 各子组件的宽度拉伸至与容器的宽度相等.
				},
				items:[
				       rendingPn,
				       beianPn,
				       lunPn,
				       shenPn,
				       assmentPn,
				       voteWayPn,
				       guidangPn,
				       masterpiecePn,
				       {
							xtype:'container',
							layout:{
								type:'hbox'
							},
							defaults:{
								margin:'0 20 0 0'
							},
							items:[/*{
						       		xtype:'checkbox',//评审环节支持学院聘任组
						       		boxLabel:zc.config.isEngageGroup,
						       		value:value_params.get('college_eval'),
						       		id:'college_eval',
						       		listeners:{
						       			'change':{
							       				fn:configFile_me.saveParamConfig
						       			}
						       		}
						       },*/
						       {
						       		xtype:'checkbox',//评审环节支持学院聘任组
						       		boxLabel:zc.config.isShowValidatecode,
						       		value:value_params.get('show_Validatecode'),
						       		id:'show_Validatecode',
						       		listeners:{
						       			'change':{
							       				fn:configFile_me.saveParamConfig
						       			}
						       		}
						       },
						       {
						       		xtype:'checkbox',//评审环节支持材料审核
						       		boxLabel:'评审环节支持材料审核',
						       		value:value_params.get('support_checking'),
						       		id:'support_checking',
						       		listeners:{
						       			'change':{
							       				fn:configFile_me.saveParamConfig
						       			}
						       		}
							}/*,{//该参数（isshowrefresh）改到system.property中，代码先不删除以防以后启用。chent 20170720
					       		xtype:'checkbox',//投票后是否显示刷新
					       		boxLabel:'投票后是否显示刷新',
					       		value:value_params.get('isshowrefresh'),
					       		id:'isshowrefresh',
					       		listeners:{
					       			'change':{
						       				fn:configFile_me.saveParamConfig
					       			}
					       		}
						}*/,
						       {
						       		xtype:'checkbox',//公示、投票环节显示申报材料表单上传的word模板内容
						       		boxLabel:'公示、投票环节显示申报材料表单上传的word模板内容',
						       		value:value_params.get('support_word'),
						       		id:'support_word',
						       		listeners:{
						       			'change':{
							       				fn:configFile_me.saveParamConfig
						       			}
						       		}
							}]
				       }
				]
			});
			var mainPanel = new Ext.Panel({
				title:'配置信息',
				border:false,
				scrollable:true,
				layout:{
					type:'vbox'//,
					//align:'stretch'
				},
				items:[peizhiPn],
				listeners:{
					'resize':function(){
						//页面大小改变时，页面自适应 haosl add 20170426 
						configFile_me.width = document.documentElement.clientWidth*0.7;
						var voteWayRadio = Ext.getCmp("voteWayRadio");
						if(voteWayRadio){
							Ext.getCmp("voteWayText").setWidth(configFile_me.width*0.13);
							voteWayRadio.setWidth(configFile_me.width*0.8-3);
							Ext.getCmp("voteWaypeizhiBtnComp").setWidth(configFile_me.width*0.07);
						}
						var comp = Ext.all("*[id$=Pn]",peizhiPn);//获得所有id以Pn结尾的组件
						for(var index in comp){
							comp[index].setWidth(configFile_me.width);//设置组件的宽度
						}
						
					}
				}
			});
			
			Ext.create('Ext.container.Viewport', {
				style:'backgroundColor:white',
				layout:'fit',
				items:[mainPanel]
		    });
			
	},
	/**
	 * 初始化每个panel的内容
	 * **/
htmlvalue:function(){
		//从全局变量value_map中取值
		value11=value_params.get("1");
		value12=value_params.get("2");
		value21=value_params.get("3");
		value22=value_params.get("4");
		value3=value_params.get("5");
		value4=value_params.get("6");
		value5=value_params.get("7");
		value41=value_params.get("8");
		assementTables=value_params.get("10");
		//给panel赋值
		pn11=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.zhichengPn+
		      '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value11+
		      '</td><td align="right" width="10%" style="white-space:nowrap"> '+
	     	  '<a href=javascript:configFile_me.showWin("1")>'+zc.label.peizhiBtn+'</a></td></tr></table>'];
		pn12=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.kaoshiPn+
		      '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value12+
		      '</td><td align="right" width="10%" style="white-space:nowrap"> '+
	     	  '<a href=javascript:configFile_me.showWin("2")>'+zc.label.peizhiBtn+'</a></td></tr></table>'];
		pn21=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.mianshiPn+
		      '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value21+
		      '</td><td align="right" width="10%" style="white-space:nowrap"> '+
	     	  '<a href=javascript:configFile_me.showWin("3")>'+zc.label.peizhiBtn+'</a></td></tr></table>'];
		pn22=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.pogePn+
		      '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value22+
		      '</td><td align="right" width="10%" style="white-space:nowrap"> '+
	     	  '<a href=javascript:configFile_me.showWin("4")>'+zc.label.peizhiBtn+'</a></td></tr></table>'];
		pn3=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.lunwenSongPn+
		     '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value3+
	        '</td><td style="text-align:right;" align="right" width="10%" style="white-space:nowrap">'+
	     	 '<a href=javascript:configFile_me.showWin("5")>'+zc.label.peizhiBtn+'</a></td></tr>' +
	     	 '<tr><td></td><td style="text-align:left;" width="8%"><a href=javascript:configFile_me.noticeConfig("5")>材料配置</a></td></tr></table>'];
	    
	    pn41=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.baomingPn+
		     '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value41+
	         '</td><td align="right" width="10%" style="white-space:nowrap">'+
	     	 '<a href=javascript:configFile_me.showWin("8")>'+zc.label.peizhiBtn+'</a></td></tr></table>'];
		
		pn4=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td style="text-align:right;white-space:nowrap;" width="13%">'+zc.label.shenbaoCaiPn+
		     '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+value4+
	         '</td><td style="text-align:right;white-space:nowrap" align="right" width="10%">'+
	     	 '<a href=javascript:configFile_me.showWin("6")>'+zc.label.peizhiBtn+'</a></td></tr>' +
	     	 '<tr><td></td><td style="text-align:left;" width="8%"><a href=javascript:configFile_me.noticeConfig("6")>材料配置</a></td></tr></table>'];
		assmenthtml=['<table style="font-size:12px;font-family:Microsoft YaHei;" width="100%" height="15%" align="left" ><tr height="25"><td align="right" width="13%" style="white-space:nowrap">'+zc.label.assessmentTablePn+
		      '&nbsp;&nbsp;&nbsp;</td><td style="color:#666666;" width="80%" align="left">'+assementTables+
		      '</td><td align="right" width="10%" style="white-space:nowrap"> '+
	     	  '<a href=javascript:configFile_me.showWin("10")>'+zc.label.peizhiBtn+'</a></td></tr></table>'];
	},
	/**
	 * 点击超链接
	 * 根据标识id判断有点击事件的panel
	 * **/
showWin:function(id){
		var obj=new Object();
		obj.flag=id;//区分哪个业务类的超链接
		if(id=="1"&&value11.length>0){//根据标识选择传递值
			obj.value=value11;                 //页面上已有的值，弹出框中会有选中状态
		}else if(id=="2"&&value12.length>0){
			obj.value=value12;
		}else if(id=="3"&&value21.length>0){
			obj.value=value21;
		}else if(id=="4"&&value22.length>0){
			obj.value=value22;
		}else if(id=="5"&&value3.length>0){
			obj.value=value3;
		}else if(id=="6"&&value4.length>0){
			obj.value=value4;
		}else if(id=="8"&&value41.length>0){
			obj.value=value41;
		}else{
			obj.value=" ";
		}
		
		if(id=="11"){
			Ext.require('ConfigFileURL.MasterPieceSetting',function(){
				SalaryTemplateGlobal=Ext.create('ConfigFileURL.MasterPieceSetting',{}).show();
			});
			return;
		}
		
		if(id=="7"){
			Ext.require('ConfigFileURL.ResultsArchiving', function(){
				SalaryTemplateGlobal = Ext.create("ConfigFileURL.ResultsArchiving", '');
			});
			return ;
		}
		if(id=="9"){
			Ext.require('ConfigFileURL.VoteWaySetting', function(){
				SalaryTemplateGlobal = Ext.create("ConfigFileURL.VoteWaySetting", '');
			});
			return ;
		}
		// 测评表配置
		if(id=="10"){
			obj.value = value_params.get('per_templates');
			obj.success = configFile_me.perTemplatesCallback;
			Ext.require('ConfigFileURL.AssessmentTable', function(){
				SalaryTemplateGlobal = Ext.create("ConfigFileURL.AssessmentTable", obj);
			});
			return ;
		}
		Ext.require('ConfigFileURL.Comp', function(){
			SalaryTemplateGlobal = Ext.create("ConfigFileURL.Comp", obj);
		});
	},
	/**
	 * 初始化全局变量
	 * **/
initParams:function(form,action){
		var result = Ext.decode(form.responseText);//获取后台返回对象
			//放入全局变量中
		var li=result.li;
		var value_par=result.value_params;
		//var value_para=Ext.decode(value_par);
		for(var a=0;a<li.length;a++){
			aa=li[a];
			value_params.put(aa,value_par[aa]);
		}
		// 参数回显
		value_params.put('college_eval',value_par['college_eval']);//是否支持学院聘任组
		value_params.put('support_checking',value_par['support_checking']);//是否支持审核
		value_params.put('support_word',value_par['support_word']);//公示、投票环节显示申报材料表单上传的word模板内容
		value_params.put('isshowrefresh',value_par['isshowrefresh']);//投票后是否显示刷新
		value_params.put('votecolumns',value_par['votecolumns']);//
		value_params.put('votetype',value_par['votetype']);//
		
		value_params.put('per_templates',value_par['per_templates']);//测评表
		var showStr = "";
		if(!Ext.isEmpty(value_par['per_templates_text'])){
			var tabids = value_par['per_templates_text'].split(",");
			for(var i=0;i<tabids.length;i++){
				var temps = tabids[i].split("|");
				showStr+="【"+temps[0]+"】"+temps[1];
				if(i<tabids.length-1){
					showStr+=",";
				}
					
			}
		}
		value_params.put('10', showStr);
		
		
		value_params.put('show_Validatecode',value_par['show_Validatecode']);//是否启用二维码
	},
	/**
	 * 根据不同的业务类执行相应的操作
	 * **/
sure:function(flagid,value){
			//根据弹出框中的选择，将模板类对应值放在全局变量中
			value_params.put(flagid,value);
			this.htmlvalue();//获取新值
			//根据弹出框选择，将数据存入数据库
			//职称认定
			if(flagid=="1"){
				this.saveParam("zhichengPn","1", value11,pn11);
			}
			//考试认定
			else if(flagid=="2"){
				this.saveParam("kaoshiPn","2",value12,pn12);
			}
			//免试备案
			else if(flagid=="3"){
				this.saveParam("mianshiPn","3",value21,pn21);
			}
			//破格认定
			else if(flagid=="4"){
				this.saveParam("pogePn","4",value22,pn22);
			}
			//论文送审业务模版
			else if(flagid=="5"){
				this.saveParam("lunwenPn","5",value3,pn3);
			}
			//职称申报业务模版
			else if(flagid=="6"){
				this.saveParam("shenbaoPn","6", value4,pn4);
			}
			//评审结果归档方案
			else if(flagid=="7"){
				this.saveParam("guidangPn","7", value5,pn5);
			}
			else if(flagid=="8"){
				this.saveParam("baomingPn","8", value41,pn41);
			}
	},
	saveParam:function(flagid,flag,value,pn){//存储数据
			var map = new HashMap();
			map.put("flagid",flag);//给业务模版类赋值id
			map.put("value",value);//放入模版值
			map.put("flagmap", "savemap");//标识，判断是初始化业务模版，还是存储，此为存储
			Rpc({functionId:'ZC00004002',async:false,success:false},map);//将参数传给交易类，执行存储数据库
			Ext.getCmp(flagid).body.update(pn);//点击确定后模版赋值
			location.reload();
	},
	// 保存"是否支持学院聘任组评审"参数配置
	saveParamConfig : function(combo, value) {
		var map = new HashMap();
		map.put("flagid", "");
		map.put("value", "");
		map.put("flagmap", combo.id);
		map.put(combo.id, value);
		map.put("isContinue",false);
		Rpc({
			functionId : 'ZC00004002',
			async : false,
			success:function(form,action){
				var msg = Ext.decode(form.responseText).msg;
				if(!Ext.isEmpty(msg)){
					combo.setValue(true);
					Ext.Msg.confirm("提示信息",msg,function(flag){
						if(flag=='yes'){
							map.put("isContinue",true);
							Rpc({functionId : 'ZC00004002',async : false,success:function(res){
								var data = Ext.decode(res.responseText)
								if(!data.succeed){
									Ext.showAlert(data.message);
								}else
									combo.setValue(false);
							}},map);
						}
					});
				}else{
					return ;
				}
			}
		}, map);
	},
	// 公示材料配置
	noticeConfig : function(key) {
		Ext.require('ConfigFileURL.NoticeConfig', function(){
			jobtitleNoticeConfigGloble = Ext.create("ConfigFileURL.NoticeConfig", {key:key});
		});
	},
	/**
	 * xus 18/4/8 保存投票方式
	 */
	saveVoteType:function(type,column){
		var flag=false;
		var votemap = type+"/"+column;
		var map = new HashMap();
		map.put("flagid", "");
		map.put("value", "");
		map.put("flagmap", "vote_type");
		map.put("vote_type", votemap);
		Rpc({
			functionId : 'ZC00004002',
			async : false,
			success:function(res){
				var respon = Ext.decode(res.responseText);
				flag= respon.succeed;
			}
		}, map);
		return flag;
	},
	/**
	 * 测评表回调函数	per_templates
	 * selectids 		选中的模板id  	Z004,Z005,Z015
	 * selectidTexts	id+模板名称 	Z004|[Z004]名称,Z005|[Z005]名称
	 */
	perTemplatesCallback:function(selectids, selectidTexts){
		
		var map = new HashMap();
		map.put('type', '3');// 保存
		map.put('tabids', selectids);//配置信息
		map.put('tabidNames', selectidTexts);//详细配置信息 id|名称
		Rpc({functionId:'ZC00004007',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			if(!Ext.isEmpty(result.noCancelTemplates)){
				Ext.showAlert(result.noCancelTemplates+"已经被使用，不允许取消！");
				return;
			}
			if(result.succeed){
				// 保存后已选的测评表配置
				value_params.put('per_templates', result.tabids);
				var showStr = "";
				var tabids = result.tabids.split(",");
				for(var i=0;i<tabids.length;i++){
					var temps = tabids[i].split("|");
					showStr+=temps[0]+"."+temps[1];
					if(i<tabids.length-1){
						showStr+=",";
					}
				}
				value_params.put('10', showStr);
				location.reload();
			}else
				Ext.showAlert(result.message);
			
		},scope:this},map);
	}
});
