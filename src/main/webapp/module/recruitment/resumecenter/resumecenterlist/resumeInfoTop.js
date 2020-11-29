/*****
 * 简历详情头部页面
 */
Ext.define('ResumeTemplateUL.resumeInfoTop',{
	resume_me:'',
	resumeInfo:'',//根据后台查询出的相关信息
	//parametersInfo:'',//直接传入的参数信息
	now_zp_pos_id:'',
	filerootPath:'',
	operateName:'',
	operatediv:'',
	constructor:function(config) {
		resume_me = this;
		parametersInfo = config;
		now_zp_pos_id = parametersInfo.zp_pos_id;
		if(parametersInfo.link_id && !parametersInfo.old_link_id)
			parametersInfo.old_link_id = parametersInfo.link_id
		this.init();
	},
	// 初始化函数
	init:function() {
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'EHR.ToolTipUL.ToolTip': rootPath+'/module/recruitment/js',
				'sendEmailUL': rootPath+'/module/recruitment/js/sendEmail.js'
			}
		});
		Ext.util.CSS.createStyleSheet(".x-mask{filter: alpha(opacity = 50)!important;opacity: .5!important;cursor: default!important;}");
		Ext.util.CSS.createStyleSheet(".x-nbr .x-window-default{background-color : white!important}");
		var map = new HashMap();
		map.put("nbase",parametersInfo.nbase);
		map.put("a0100",parametersInfo.a0100);
		map.put("zp_pos_id",parametersInfo.zp_pos_id);
		map.put("link_id",parametersInfo.link_id);
		map.put("from",parametersInfo.from);
		map.put("current",parametersInfo.current);
		map.put("pagesize",parametersInfo.pagesize);
		map.put("rowindex",parametersInfo.nextRowindex);
		map.put("z0381",parametersInfo.z0381);
		if(parent.Ext.getCmp("pagehtmltop")){
			Rpc({functionId:'ZP0000002120',async:false,success:resume_me.replaceResumeInfo},map);
		}else
			Rpc({functionId:'ZP0000002120',async:false,success:resume_me.showAllMessage},map);
	},
	//生成页面信息
	showAllMessage:function(form,action){
		var info = Ext.decode(form.responseText);
		resumeInfo = info.resumeInfo;
		Ext.util.CSS.createStyleSheet(".hj-zm-cplc-all-two-top ul{padding-left:0;}","underline1");
		/*********************************页面顶部内容填充******************************************/
		var tophtml = resume_me.createTop();
		/*********************************页面分层布局******************************************/
		var pagehtml = Ext.widget('window', {
		    layout: 'border',
		    header:false,
		    border:false,
		    padding:0,
		    maximized:true,
		    minWidth:900,
		    id:'pagehtmltop',
		    items: [tophtml,{
				border:false,
				layout: 'border',	
				region: 'center', 
				bodyStyle: 'background:white;',
	            html: '<iframe name="ifra" src=""  width="100%" id="mainBody" frameborder=0 height="100%"></iframe>'
		    }]
		}).show();
		
		resume_me.displayPosition();
	   /*********************************设置当前显示页面******************************************/
   	   if(parametersInfo.zp_pos_id==""||parametersInfo.zp_pos_id==null)
   	   {
   	   		//如果没有默认职位，则默认显示第一志愿
		    if(resumeInfo.first!=0)
		    {
	   	   		parametersInfo.zp_pos_id = resumeInfo.first.zp_pos_id;
	   	   		Ext.getDom(resumeInfo.first.positionno).style.color = "#ba2636";
		   		Ext.getDom(resumeInfo.first.positionno).style.textDecoration="underline";
		    }
   	   }
   	   Ext.getDom("resumeInfo").style.background="#E4E4E4";
   	   
	   /*********************************设置应该显示的操作******************************************/
   	   resume_me.createResumeInfo(info);
	   window.status = "";
	},
	createTop:function(){
		var tophtml = Ext.widget("panel",{
			  region: 'north',
			  height:115,
			  id:'topPanel',
			  closable:false,
			  autoScroll:false,
			  header: false,
			  style:'background-color:white',
			  padding:'8 3 0 4',
			  border:false,
	          items:resume_me.getTopItems()
			});
		return tophtml;
	},
	getTopItems:function(){
		var status = resumeInfo.first.status;
		if(resumeInfo.first=="0")
			status = "未申请职位";
		
		var html = '<div class="hj-zm-cplc-all-two" style="word-break:break-all;">'
					+'<div class="hj-zm-cplc-all-two-top"><ul style="margin-top: 0px;">';
		if(parametersInfo.zp_pos_id&&parametersInfo.zp_pos_id!="2iIeo7kAcbUPAATTP3HJDPAATTP")
			html += '<li id="positionLi"><a href="javascript:void(0);" name="pagea" onclick="resume_me.clickPage(this)" id="positionInfo" style="border-right:none;border-bottom: none;">职位详情</a></li>';
		
		html += '<li><a href="javascript:void(0);" name="pagea" onclick="resume_me.clickPage(this)" id="resumeInfo" style="border-bottom: none;">候选人简历</a></li>'
			+'</ul></div></div>'
			+'<div style="float:right;padding-right:20px;display:none;" id="operatediv"></div>'
			+'<div style="position: absolute;top: 34px;margin:0;padding:0; width:99%;height:1px;background-color:#B5B5B5;overflow:hidden;word-break:break-all;float:left;">'
			+'</div> ';
		
		var items = [{
	          	xtype:'panel',
				border:false,
				html: '<label style="font-size: 16px;"><b>'+resumeInfo.name+'</b></label><label id="positionname">&nbsp;</label>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<label id="positionstatus">'+status+'</label>&nbsp;&nbsp;&nbsp;<label id="tipId"><img width="12px" id="imgId" style="display:none"  onclick="resume_me.clickImgId();" height="12px" src="../../../module/recruitment/image/feedback.png"/></label>'
	          },{
	          	xtype:'panel',
	          	id:'first',
				border:false,
				padding:'5 3 3 0',
				layout:'hbox',
				items:[{
		             	xtype:'label',
		             	text:'第一志愿职位:',
		             	height:20,
		             	width:90
		             	},{
		             	xtype:'panel',
		             	id:'firstposition',
		             	width:500,
		             	height:20,
		             	border:false,
		             	html:'&nbsp;'
		             	}]
	          },{
	          	xtype:'panel',
	          	id:'other',
				border:false,
				padding:'0 3 3 0',
				layout:'hbox',
				items:[{
		             	xtype:'label',
		             	text:'其它志愿职位:',
		             	height:16,
		             	width:90
		             	},{
		             	xtype:'panel',
		             	id:'otherposition',
		             	width:800,
		             	height:16,
		             	border:0,
		             	html:'&nbsp;'
		             	}]
	          },{
	          	xtype:'panel',
				border:false,
				height:41,
				html: html
	          }];
	          return items;
	},
	displayPosition:function(temp){
		var obj = parent.Ext.getCmp("pagehtmltop");
		if(temp){
			resumeInfo = temp;
			var map = new HashMap();
			map.put("a0100", parametersInfo.a0100);
			map.put("z0301", parametersInfo.zp_pos_id);
			map.put("select","select");
			resume_me.searchOperationLog(map);
		}
		   /*********************************设置第一志愿******************************************/
		if(resumeInfo.first!=0&&!obj)
		{
			if(resumeInfo.first.priv=="0" || parametersInfo.from=="process")
				Ext.getCmp("firstposition").body.update(resumeInfo.first.position);
			else
				Ext.getCmp("firstposition").body.update('<a href="javascript:void(0);" name="positiona" id=\''+resumeInfo.first.positionno+'\' onclick="resume_me.clickPosion(\''+resumeInfo.first.status+'\',\''+resumeInfo.first.positionno+'\',\''+resumeInfo.first.zp_pos_id+'\',\''+resumeInfo.first.z0319+'\',\''+resumeInfo.first.description+'\')" >'+resumeInfo.first.position+'</a>');
		
			if(resumeInfo.colorflg == resumeInfo.first.positionno && resumeInfo.first.description==1)
				Ext.get('imgId').setDisplayed(true);
			else
				Ext.get('imgId').setDisplayed(false);
			   
		}else if(resumeInfo.first!=0&&obj){
			if(resumeInfo.first.priv=="0"  || parametersInfo.from=="process")
				parent.Ext.getCmp("firstposition").body.update(resumeInfo.first.position);
			else
				parent.Ext.getCmp("firstposition").body.update('<a href="javascript:void(0);" name="positiona" id=\''+resumeInfo.first.positionno+'\' onclick="resume_me.clickPosion(\''+resumeInfo.first.status+'\',\''+resumeInfo.first.positionno+'\',\''+resumeInfo.first.zp_pos_id+'\',\''+resumeInfo.first.z0319+'\',\''+resumeInfo.first.description+'\')" >'+resumeInfo.first.position+'</a>');
		
			if(resumeInfo.colorflg == resumeInfo.first.positionno && resumeInfo.first.description==1)
				parent.Ext.get('imgId').setDisplayed(true);
			else
				parent.Ext.get('imgId').setDisplayed(false);
		}
	
		/*********************************设置其它志愿******************************************/
		if(resumeInfo.other!=0)
		{
			var otherhtml = "";
			var otherlist = resumeInfo.other;
			for(var i=0;i<otherlist.length;i++)
			{
				var otherposition = otherlist[i];
				if(otherposition.priv=="0"  || parametersInfo.from=="process")
					otherhtml += otherposition.position+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
				else
					otherhtml += '<a href="javascript:void(0);" name="positiona" id=\''+otherposition.positionno+'\' onclick="resume_me.clickPosion(\''+otherposition.status+'\',\''+otherposition.positionno+'\',\''+otherposition.zp_pos_id+'\',\''+''+'\',\''+otherposition.description+'\')">'+otherposition.position+'</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
			
				if( resumeInfo.colorflg == otherposition.positionno && obj){
					if( otherposition.description==1)
						parent.Ext.get('imgId').setDisplayed(true);
					else
						parent.Ext.get('imgId').setDisplayed(false);
				}else if( resumeInfo.colorflg == otherposition.positionno){
					if(resumeInfo.colorflg == otherposition.positionno && otherposition.description==1)
						Ext.get('imgId').setDisplayed(true);
					else
						Ext.get('imgId').setDisplayed(false);
				}
			}
			if(obj)
				parent.Ext.getCmp("otherposition").body.update(otherhtml);
			else
				Ext.getCmp("otherposition").body.update(otherhtml);
		}
		/*********************************设置当前选中职位******************************************/
		if(resumeInfo.colorflg!=null&&resumeInfo.colorflg!=""&&parametersInfo.from!="process")
		{
			if(obj){
				parent.Ext.getDom(resumeInfo.colorflg).style.color = "#ba2636";
				parent.Ext.getDom(resumeInfo.colorflg).style.textDecoration="underline";
			}else{
				Ext.getDom(resumeInfo.colorflg).style.color = "#ba2636";
				Ext.getDom(resumeInfo.colorflg).style.textDecoration="underline";
			}
		}
		
	},
	createResumeInfo:function(info){
		var html = "";
	   if(parametersInfo.from=="resumeCenter"||parametersInfo.from=="talents")
	   {
		    document.getElementById("mainBody").contentWindow.location = "/recruitment/resumecenter/searchresume.do?b_search=link&resumeid="+parametersInfo.a0100+"&zp_pos_id="+parametersInfo.zp_pos_id+"&current="+0+"&pagesize="+parametersInfo.pagesize+"&rowindex="+20+"&nbase="+parametersInfo.nbase+"&schemeValues="+parametersInfo.schemeValues+"&from="+parametersInfo.from;
		    resume_me.showtip(parametersInfo.zp_pos_id);
	   		Ext.get("operatediv").setHtml(resume_me.createoperateHtml());
	   }else if(parametersInfo.from=="process")
	   {
	   		Ext.getDom("mainBody").src = "/recruitment/resumecenter/searchresume.do?b_search=link" +
	   				"&resumeid="+parametersInfo.a0100+"&zp_pos_id="+parametersInfo.zp_pos_id+
	   				"&link_id="+parametersInfo.link_id+"&resume_flag="+$URL.encode(parametersInfo.resume_flag)+
	   				"&nbase="+parametersInfo.nbase+"&c0102="+parametersInfo.email+
	   				"&z0381="+parametersInfo.z0381+"&resume_name="+$URL.encode(parametersInfo.resume_name)+
	   				"&from=process&page="+$URL.encode(parametersInfo.pagesize+"`"+parametersInfo.current);
	   		
	   		Ext.get("operatediv").setHtml(resume_me.createProcessHtml(info));
	   		resume_me.showtip(parametersInfo.zp_pos_id);
	   		Ext.getDom("operatediv").style.display = "none";
	   }
	},
	createoperateHtml:function(){
		var html = "";
		//从简历中心进入显示职位操作按钮
   		var operate = resumeInfo.operate;
   		for(var i=0;i<operate.length;i++)
   		{
   			if(operate[i].text=="加入人才库"){
   				html += "<a href='javascript:void(0);' ";
   				if(operate[i].id){
   					html += "id='"+operate[i].id+"'";
   				}
   				if(operate[i].flag!="true"){
   					html += " style='padding-right:20px;display:none;'";
	   			}else{
	   				html += " style='padding-right:20px;' ";
	   			}
   				html += " onclick="+operate[i].fn+">"+operate[i].text+"</a>";
   				continue;
   			}
   			if(operate[i].text=="移出人才库"){
   				html += "<a href='javascript:void(0);' ";
   				if(operate[i].id){
   					html += "id='"+operate[i].id+"'";
   				}
   				if(operate[i].flag!="true"){
   					html += " style='padding-right:20px;display:none;'";
   				}else{
   					html += " style='padding-right:20px;' ";
   				}
   				html += " onclick="+operate[i].fn+">"+operate[i].text+"</a>";
   				continue;
   			}
   			if(operate[i].flag!="true"){
   				continue;
   			}

   			if(operate[i].able=="true")
   			{
   				if(operate[i].fn=="resume_me.printAX()"&&!Ext.isIE){
   					continue;
   				}
   				html += "<a href='javascript:void(0);' ";
		   		if(operate[i].flag=="true")
		   		{
			   		html += " style='padding-right:20px;' id="+operate[i].fn+" onclick="+operate[i].fn;
		   		}else{
		   			html += " style='color:gray !important;cursor: default;padding-right:20px;'";
		   		}
		   		html += ">"+operate[i].text+"</a>";
   			}
   		}
   		html += "<a href='javascript:void(0);' onclick='resume_me.goBack()'>返回</a>";
   		return html;
	},
	createProcessHtml:function(info){
		var html = "";
		//从候选人进入显示环节操作按钮
   		var operate = resumeInfo.operate;
   		for(var i=0;i<operate.length;i++)
   		{
   				filerootPath = resumeInfo.rootPath;
		   		html += "<div style='padding-right:20px;float:left;' id='"+operate[i].function_str+"' >";
		   		if(operate[i].function_str!="uploadingAffix"&&operate[i].function_str!="uploadingResult")
		   		{
				   	html += "<a href='javascript:void(0);' onclick=resume_me.operation(\""+operate[i].function_str+"\"" +
				   			",\""+parametersInfo.link_id+"\",\""+parametersInfo.email+"\",\""+parametersInfo.zp_pos_id+"\"" +
				   					",\""+operate[i].custom_name+"\",\""+parametersInfo.nbase+"\",\""+parametersInfo.a0100+"\")>"+operate[i].custom_name+"</a>";
		   		}else{
			   		operateName = operate[i].custom_name;
		   		}
		   		html += "</div>";
   		}
   		resume_me.nextCandidate = info.nextCandidate;
   		resume_me.lastCandidate = info.lastCandidate;
   		status = resumeInfo.link_name+"&nbsp;(&nbsp;"+parametersInfo.resume_name+"&nbsp;)";
   		Ext.get("positionstatus").setHtml(status);
   		if(resume_me.lastCandidate!=undefined&&resume_me.lastCandidate.nextResumeid!=undefined){
	   		html += "<div style='padding-right:20px;float:left;'>";
	   		html += "<a id='nextResume' href='javascript:void(0)' onclick='javascript:Global.qureyResume(\""+resume_me.lastCandidate.nextNbase+"\",\""+resume_me.lastCandidate.nextResumeid+"\",\""+resume_me.lastCandidate.zp_pos_id+"\",\"process\",\""+resume_me.lastCandidate.nextCurrent+"\",\""+resume_me.lastCandidate.nextPagesize+"\",\""+resume_me.lastCandidate.link_id+"\",\""+resume_me.lastCandidate.resume_flag+"\",\""+resume_me.lastCandidate.email+"\",\""+resume_me.lastCandidate.z0381+"\",\""+resume_me.lastCandidate.resume_name+"\",\""+resume_me.lastCandidate.nextRowindex+"\");' target='_self'>上一个</a>";
	   		html += "</div>";
	   		html += "<div style='padding-right:20px;float:left;'>";
	   		html += "<a id='nextResume' href='javascript:void(0)' onclick='javascript:Global.qureyResume(\""+resume_me.nextCandidate.nextNbase+"\",\""+resume_me.nextCandidate.nextResumeid+"\",\""+resume_me.nextCandidate.zp_pos_id+"\",\"process\",\""+resume_me.nextCandidate.nextCurrent+"\",\""+resume_me.nextCandidate.nextPagesize+"\",\""+resume_me.nextCandidate.link_id+"\",\""+resume_me.nextCandidate.resume_flag+"\",\""+resume_me.nextCandidate.email+"\",\""+resume_me.nextCandidate.z0381+"\",\""+resume_me.nextCandidate.resume_name+"\",\""+resume_me.nextCandidate.nextRowindex+"\");' target='_self'>下一个</a>";
	   		html += "</div>";
   		}
   		html += "<a href='javascript:void(0);' onclick='resume_me.goBack()'>返回</a>";
   		return html;
	},
	replaceResumeInfo:function(form,action){
		var info = Ext.decode(form.responseText);
		resumeInfo = info.resumeInfo;
		parent.parametersInfo = parametersInfo;
		Ext.util.CSS.createStyleSheet(".hj-zm-cplc-all-two-top ul{padding-left:0;}","underline1");
		/*********************************页面顶部内容填充******************************************/
		if(parametersInfo.from=="resumeCenter"||parametersInfo.from=="talents")
		{
			parent.Ext.getCmp("topPanel").removeAll();
			parent.Ext.getCmp("topPanel").add(resume_me.getTopItems());
			parent.Ext.getDom("resumeInfo").style.background="#E4E4E4";
			parent.document.getElementById("mainBody").contentWindow.location = "/recruitment/resumecenter/searchresume.do?b_search=link" +
			"&resumeid="+parametersInfo.a0100+"&zp_pos_id="+parametersInfo.zp_pos_id+"&current="+0+"&pagesize="+parametersInfo.pagesize+"" +
			"&rowindex="+20+"&nbase="+parametersInfo.nbase+"&schemeValues="+parametersInfo.schemeValues+"&from="+parametersInfo.from;
//			resume_me.showtip(parametersInfo.zp_pos_id);
			parent.Ext.get("operatediv").setHtml(resume_me.createoperateHtml());
			resume_me.displayPosition();
		}else if(parametersInfo.from=="process")
		{
			parent.Ext.getDom("mainBody").src = "/recruitment/resumecenter/searchresume.do?b_search=link" +
			"&resumeid="+parametersInfo.a0100+"&zp_pos_id="+parametersInfo.zp_pos_id+
			"&link_id="+parametersInfo.link_id+"&resume_flag="+$URL.encode(parametersInfo.resume_flag)+
			"&nbase="+parametersInfo.nbase+"&c0102="+parametersInfo.email+
			"&z0381="+parametersInfo.z0381+"&resume_name="+$URL.encode(parametersInfo.resume_name)+
			"&from=process&page="+$URL.encode(parametersInfo.pagesize+"`"+parametersInfo.current);
		
			parent.Ext.get("operatediv").setHtml(resume_me.createProcessHtml(info));
			Ext.getDom("operatediv").style.display = "none";
		}		
	},
	/*********************************点击职位******************************************/
	clickPosion:function(status,positionno,zp_pos_id,z0319,description){
		Ext.getDom("operatediv").style.display = "none";
		//渲染信息分类面板
		var pas = document.getElementsByName("pagea");
		for(var i=0;i<pas.length;i++)
		{
			pas[i].style.background="";
		}
		Ext.getDom("positionInfo").style.background="#E4E4E4";
		//渲染当前职位
		var as = document.getElementsByName("positiona");
		for(var i=0;i<as.length;i++)
		{
			as[i].style.color = "#1b4a98";
			as[i].style.textDecoration="none";
		}
		Ext.getDom(positionno).style.color = "#ba2636";
		Ext.getDom(positionno).style.textDecoration="underline";
		parametersInfo.z0319 = z0319;
		parametersInfo.zp_pos_id = zp_pos_id;
		if(description==1){
	   		   Ext.get('imgId').setDisplayed(true);
	   	   }else{
	   		Ext.get('imgId').setDisplayed(false);
	   	   }
		resume_me.showtip(zp_pos_id);
		Ext.getDom("mainBody").src = "/recruitment/position/position.do?b_toedit=link&z0319="+z0319+"&z0301="+zp_pos_id+"&from="+parametersInfo.from;
		if(parametersInfo.from=="process"){
			if(now_zp_pos_id!=zp_pos_id)
			{
				//当选中职位不是当前职位时，不显示按钮
				var html = "";
				html += "<a href='javascript:void(0);' onclick='resume_me.goBack()'>返回</a>";
	   			Ext.get("operatediv").setHtml(html);
			}else{
				resume_me.queryOperationList();
			}
		}else{
				resume_me.queryOperationList();
		}
		Ext.get("positionstatus").setHtml(status);
		window.status = "";
	},
	/*********************************点击信息页******************************************/
	clickPage:function(obj){
		if(!parametersInfo.zp_pos_id||parametersInfo.zp_pos_id=="2iIeo7kAcbUPAATTP3HJDPAATTP")
			return;
		
		if("positionInfo"==obj.id)
		{
			Ext.getDom("mainBody").src = "/recruitment/position/position.do?b_toedit=link" +
					"&z0319="+parametersInfo.z0319+"&z0301="+parametersInfo.zp_pos_id+"&from="+parametersInfo.from;
		}else if("resumeInfo"==obj.id)
		{
			if(parametersInfo.from=="resumeCenter"||parametersInfo.from=="talents")
			{
				Ext.getDom("mainBody").src = "/recruitment/resumecenter/searchresume.do?b_search=link" +
					"&resumeid="+parametersInfo.a0100+"&zp_pos_id="+parametersInfo.zp_pos_id+
					"&current="+0+"&pagesize="+1+"&rowindex="+20+"&nbase="+parametersInfo.nbase+
					"&schemeValues="+0+"&from="+parametersInfo.from;

			}else if(parametersInfo.from=="process"){
				Ext.getDom("mainBody").src = "/recruitment/resumecenter/searchresume.do?b_search=link" +
	   				"&resumeid="+parametersInfo.a0100+"&zp_pos_id="+parametersInfo.zp_pos_id+
	   				"&link_id="+parametersInfo.link_id+"&resume_flag="+$URL.encode(parametersInfo.resume_flag)+
	   				"&nbase="+parametersInfo.nbase+"&c0102="+parametersInfo.email+
	   				"&z0381="+parametersInfo.z0381+"&resume_name="+$URL.encode(parametersInfo.resume_name)+
	   				"&from=process&page="+$URL.encode(parametersInfo.pagesize+"`"+parametersInfo.current);
			}
		}
		var as = document.getElementsByName("pagea");
		for(var i=0;i<as.length;i++)
		{
			as[i].style.background="";
		}
		obj.style.background="#E4E4E4";
		
		if("resumeInfo"!=obj.id)
		{			
			Ext.getDom("operatediv").style.display = "none";
		}
		window.status = "";
	},
	/*********************************查询当前职位下可操作按钮******************************************/
	queryOperationList:function(){
		var map = new HashMap();
		map.put("nbase",parametersInfo.nbase);
		map.put("a0100",parametersInfo.a0100);
		map.put("zp_pos_id",parametersInfo.zp_pos_id);
		map.put("link_id",parametersInfo.link_id);
		map.put("z0381", parametersInfo.z0381);
	    Rpc({functionId:'ZP0000002121',async:false,success:resume_me.showOperateList},map);
	},
	/*********************************渲染操作按钮******************************************/
	showOperateList:function(form){
		var info = Ext.decode(form.responseText);
   		var html = "";
		if(parametersInfo.from=="resumeCenter"||parametersInfo.from=="talents")
	    {
	   		//从简历中心进入显示职位操作按钮
	   		var operate = info.operate;
	   		for(var i=0;i<operate.length;i++)
	   		{
	   			if(operate[i].able=="true")
	   			{
			   		html += "<a href='javascript:void(0);' ";
			   		if(operate[i].flag=="true")
			   		{
				   		html += " style='padding-right:20px;' id="+operate[i].fn+" onclick="+operate[i].fn;
			   		}else{
			   			html += " style='color:gray !important;cursor: default;padding-right:20px;'";
			   		}
			   		html += ">"+operate[i].text+"</a>";
	   			}
	   		}
	   }else if(parametersInfo.from=="process")
	   {
	   		//从候选人进入显示环节操作按钮
	   		var operate = info.operate;
	   		for(var i=0;i<operate.length;i++)
	   		{
			   		html += "<div style='padding-right:20px;float:left;' id='"+operate[i].function_str+"' >";
			   		if(operate[i].function_str!="uploadingAffix"&&operate[i].function_str!="uploadingResult")
			   		{
					   	html += "<a href='javascript:void(0);' onclick=resume_me.operation(\""+operate[i].function_str+"\"" +
					   			",\""+parametersInfo.link_id+"\",\""+parametersInfo.email+"\",\""+parametersInfo.zp_pos_id+"\"" +
					   					",\""+operate[i].custom_name+"\",\""+parametersInfo.nbase+"\",\""+parametersInfo.a0100+"\")>"+operate[i].custom_name+"</a>";
			   		}else{
				   		operateName = operate[i].custom_name;
			   		}
			   		html += "</div>";

	   		}
	   		status = info.link_name+"&nbsp;(&nbsp;"+parametersInfo.resume_name+"&nbsp;)";
	   }
		if(resume_me.lastCandidate!=undefined&&resume_me.lastCandidate.nextResumeid!=undefined){
			html += "<div style='padding-right:20px;float:left;'>";
			html += "<a id='nextResume' href='javascript:void(0)' onclick='javascript:Global.qureyResume(\""+resume_me.lastCandidate.nextNbase+"\",\""+resume_me.lastCandidate.nextResumeid+"\",\""+resume_me.lastCandidate.zp_pos_id+"\",\"process\",\""+resume_me.lastCandidate.nextCurrent+"\",\""+resume_me.lastCandidate.nextPagesize+"\",\""+resume_me.lastCandidate.link_id+"\",\""+resume_me.lastCandidate.resume_flag+"\",\""+resume_me.lastCandidate.email+"\",\""+resume_me.lastCandidate.z0381+"\",\""+resume_me.lastCandidate.resume_name+"\",\""+resume_me.lastCandidate.nextRowindex+"\");' target='_self'>上一个</a>";
			html += "</div>";
			html += "<div style='padding-right:20px;float:left;'>";
	   		html += "<a id='nextResume' href='javascript:void(0)' onclick='javascript:Global.qureyResume(\""+resume_me.nextCandidate.nextNbase+"\",\""+resume_me.nextCandidate.nextResumeid+"\",\""+resume_me.nextCandidate.zp_pos_id+"\",\"process\",\""+resume_me.nextCandidate.nextCurrent+"\",\""+resume_me.nextCandidate.nextPagesize+"\",\""+resume_me.nextCandidate.link_id+"\",\""+resume_me.nextCandidate.resume_flag+"\",\""+resume_me.nextCandidate.email+"\",\""+resume_me.nextCandidate.z0381+"\",\""+resume_me.nextCandidate.resume_name+"\",\""+resume_me.nextCandidate.nextRowindex+"\");' target='_self'>下一个</a>";
	   		html += "</div>";
		}
   		html += "<a href='javascript:void(0);' onclick='resume_me.goBack()'>返回</a>";
   		Ext.get("operatediv").setHtml(html);
   		resume_me.uploadFile();
	},
	/*********************************返回******************************************/
	goBack:function(){
		var href = "";
		if(parametersInfo.from=="resumeCenter"||parametersInfo.from=="talents")
		{
			Ext.getCmp("pagehtmltop").close();
		}else if(parametersInfo.from=="process"){
			href = "/recruitment/position/position.do?b_search=link" +
					"&z0301="+now_zp_pos_id+"&z0381="+parametersInfo.z0381+
					"&page="+$URL.encode(parametersInfo.pagesize+"`"+parametersInfo.current)+
					"&node_id="+parametersInfo.resume_flag.split('`')[0]+
					"&link_id="+parametersInfo.old_link_id+"&sign=2&from="+parametersInfo.from+"&back=true";
			window.location = href;
			parent.window.location = href;
		}
	},
	setStatus:function(status){
		Ext.getDom("positionstatus").innerHTML=status;
	},
	uploadFile:function(){
		var upLoadFile = Ext.getDom("upLoadFile");
		if(upLoadFile!=undefined)
		{
			return;
		}
		var file = Ext.getDom("uploadingResult");
		var name = "uploadingResult";
		if(Ext.isEmpty(file))
		{
			file = Ext.getDom("uploadingAffix");
			name = "uploadingAffix";
		}
		if(!Ext.isEmpty(file)){
		   Ext.create("SYSF.FileUpLoad",{
		   		id:'upLoadFile',
				renderTo:name,
				upLoadType:3,
				buttonText:'<a href="javascript:void(0);">'+operateName+'</a>',
				success:window.frames['ifra'].uploadSuccess,
				fileSizeLimit:'20MB',
				fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx;*.pdf",
				width:105,
				height:15,
				isTempFile:false,
	            VfsModules:VfsModulesEnum.ZP,
	            VfsFiletype:VfsFiletypeEnum.other,
	            VfsCategory:VfsCategoryEnum.other
			});
		}
	},
	showtip:function(zp_pos_id){
		Ext.require('EHR.ToolTipUL.ToolTip.feedback', function(){
			Ext.create("EHR.ToolTipUL.ToolTip.feedback",{nbase:parametersInfo.nbase,a0100:parametersInfo.a0100,zp_pos_id:zp_pos_id,tipId:'tipId',afterfunc:''});
		});
	},
	//接受职位申请
	acceptPositionApply:function() {
		var node_flag = window.frames['ifra'].document.getElementById("node_flag").value;
		var zp_pos_id_input = window.frames['ifra'].document.getElementById("zp_pos_id");
		var zp_pos_id = zp_pos_id_input.value;
		if(node_flag=="1")
		{
			Ext.Msg.alert('提示信息',"当前人员处于已终止或已入职状态，不允许接受职位申请！");return;
		}
		Ext.require('sendEmailUL', function(){
			Ext.create("sendEmailUL.sendEmail", {
				sub_module:"7",
				nModule:"10",
				z0301:zp_pos_id,
				a0100s:parametersInfo.a0100,
				a0101s:resumeInfo.name,
				title:"接受职位申请",
				fuId:"ZP0000002102",
				function_str:"acceptPositionApply",
				executionMethod:function(obj){
					Global.operateResult(obj);
				}
			});
		});
	},
	// 拒绝职位申请
	rejectPositionApply:function(){
		var node_flag = window.frames['ifra'].document.getElementById("node_flag").value;
		var zp_pos_id_input = window.frames['ifra'].document.getElementById("zp_pos_id");
	    var zp_pos_id = zp_pos_id_input.value;
		if(node_flag=="1")
		{
			Ext.Msg.alert('提示信息',"当前人员处于已终止或已入职状态，不允许拒绝职位申请！");
			return;
		}
		Ext.require('sendEmailUL', function(){
			Ext.create("sendEmailUL.sendEmail", {
				sub_module:"7",
				nModule:"11",
				z0301:zp_pos_id,
				a0100s:parametersInfo.a0100,
				a0101s:resumeInfo.name,
				title:"拒绝职位申请",
				fuId:"ZP0000002102",
				function_str:"rejectPositionApply",
				executionMethod:function(obj){
					Global.operateResult(obj);
				}
			});
		});
	    
	},
	exportResumeZip:function (param){
		var map = Ext.decode(param.responseText);
		
		if(map.succeed){
			var infor = map.infor;
			if("ok" == infor) {
				var zipName = map.zipname;
				window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+zipName+"&openflag=true","_blank");
			} else {
				Ext.showAlert(infor);
			}
		}else{
			if(map.message){
				Ext.Msg.alert('提示信息', map.message);
			}
		}
	},
	//导出简历PDF
	exportResumePDF:function () {
		var nbase="", a0100="", a0101s="";
		var map = new HashMap();
		map.put("a0100s", parametersInfo.a0100);
		map.put("nbase", parametersInfo.nbase);
		map.put("a0101s", resumeInfo.name);
		map.put("z0301", parametersInfo.zp_pos_id);
		Rpc( {
			functionId : 'ZP0000002107',
			success : resume_me.exportResumeZip
		}, map);
	},
	showPrint:function (outparamters)
	{
		var param = Ext.decode(outparamters.responseText);
		if(!param.succeed){
			Ext.showAlert(param.message);
			return;
		}
		var personlist=param.personlist;
		var nbase = param.nbase;
		var cardid = param.cardid;
		
		var obj = window.frames['ifra'].document.getElementById('CardPreview1');
		if(obj==null)
		{
			Ext.Msg.alert('提示信息', '没有下载打印控件，请设置IE重新下载！');
			return;
		}
		try {
			   window.frames['ifra'].Global.initCard();
			   obj.SetCardID(cardid);
			   obj.SetDataFlag("<SUPER_USER>1</SUPER_USER>");
			   obj.SetNBASE(nbase);
			   obj.ClearObjs();   
			   if(personlist!=null&&personlist.length>0)
			   {
			     for(var i=0;i<personlist.length;i++)
			     {
			       obj.AddObjId(personlist[i].dataValue);
			     }
			   }
			   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
		   	   obj.ShowCardModal();
		}catch (e) {

		}
	   
	},
	//打印简历
	printAX:function () {
		if(!Ext.isIE){
			Ext.Msg.alert('提示信息', '该功能仅支持IE浏览器！');
			return;
		}
		var a0100s = parametersInfo.nbase+"`"+parametersInfo.a0100;
		var pers = new Array();
		pers[0] = a0100s;
		var map = new HashMap();
		map.put("inforkind", "1");
		map.put("pers", pers);
		Rpc( {
			functionId : 'ZP0000002108',
			success : resume_me.showPrint
		}, map);
	},
	operation:function(function_str,link_id,c0102,z0301,custom_name,nbase,a0100){
		Ext.util.CSS.swapStyleSheet("emailCss","/ext/ext6/resources/ext-theme.css");
	    var node_id=window.frames['ifra'].Ext.getDom("node_id").value;
	    var nodeid= node_id +",";
	    
		Global.active(function_str,link_id,node_id,nodeid,parametersInfo.resume_name,z0301,a0100,nbase,resumeInfo.name,c0102,custom_name,"resume");
	},
	searchOperationLog:function(map){
		Rpc({asynchronous:true,functionId : 'ZP0000002004',success:function(out){
			var result = Ext.decode(out.responseText);
			var searchLog = result.searchLog;
			var html="<table style ='border-collapse:separate; border-spacing:8px;' width='90%'>";
			Ext.Array.each(searchLog, function(obj, index) {
				if(index!=0){
					html+="<td colspan='3'><div style='border-bottom:1px #c5c5c5 dashed;'></div></td>"
				}
				html += "<tr>" +
						"<td nowrap='nowrap' style='vertical-align: middle;padding-top: 5px' align='left' width='50px'>" +
						"<span>"+obj.create_fullname+"</span></td>" +
						"<td align='left' style='padding-top: 5px;padding-left: 15px' >" +
						"<div style='float: left;width: 145px;'>"+obj.Create_time+"</div></td>" +
						"<td align='left' style='padding-top: 5px;word-break: break-all' width='80%'>" +
						"<div style='float: left;font-weight:bold;'>"+obj.link_name+"</div>" +
						"<div style='float: left;padding-left:10px;'>"+obj.Log_info+"</div></td>" +
						"</tr>";
				if(obj.Description)
					html += "<tr>" +
							"<td style='vertical-align: middle;padding-top: 10px' align='left' width='100px'></td>" +
							"<td align='left' style='padding-left: 15px;word-break: break-all' width='80%' colspan='2'>" +
							"<div style='float: left;'>"+obj.Description+"</div>" +
							"</td></tr>";
			});
			html += "</table>";
			window.frames['ifra'].Ext.getDom("logdiv").innerHTML=html;
		}},map);
	},
	
	clickImgId: function(){
		Ext.getCmp('tool').hide();
		return;
	},
	
	//推荐职位
	recommendOtherPosition:function(a0100,nbase,c0102){
		var node_flag = window.frames['ifra'].document.getElementById("node_flag").value;
		if(node_flag=="1")
		{
			Ext.Msg.alert('提示信息',"当前人员处于已终止或已入职状态，不允许推荐职位！");return;
		}
		 var a0100s = "{\"a0100\":[";
		 a0100s+="{\"a0100\":\""+a0100+"\",\"z0301\":\"\",\"a0101\":\"\"},";
		 if(a0100s.length>1)
		 	a0100s=a0100s.substring(0, a0100s.length-1)+"],\"nbase\":\""+nbase+"\"}";
		 
		 var pageDescFro = a0100+"`"+nbase+"`"+c0102+"`"+now_zp_pos_id+"`"+parametersInfo.current+"`"+parametersInfo.pagesize+"`"+parametersInfo.schemeValues+"`"+parametersInfo.rowindex;
		 Global.oldfromModule = Global.fromModule;
		 Global.fromModule = "resumeInfo";
		 var map = new HashMap();
	    map.put("a0100s",a0100s);
	    map.put("pageDescFro",pageDescFro);
	    Rpc({
			functionId : 'ZP0000002082',
			success :Global.recommendPosition
		}, map);	
	}
});