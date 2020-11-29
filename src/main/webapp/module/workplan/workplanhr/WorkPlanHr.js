/**
 * 工作计划监控
 * @Date 2017-10-31
 * @author haosl
 */
Ext.define("WorkPlanHrUL.WorkPlanHr",{
	id:'workplanhr_main',
	requires:["EHR.commonQuery.CommonQuery",
		"PeriodCascadeForTrackUL.PeriodCascadeForTrack"],
	/** 计划监控类型 1=个人  2=部门*/
	plantype : "1",
	constructor : function(config){
		this.defaultQueryFields=[];//公共查询组件默认查询字段
		WorkPlanhr_me = this;
		if (!Ext.isEmpty(config.submittype)){
            this.submittype = config.submittype;
        }else{
            this.submittype = "0";//默认选中 应报
        }
        this.currentPage = 1
        if (!Ext.isEmpty(config.currentPage)){
		    this.currentPage = config.currentPage;
        }
		this.followerMap = "";//计划关注人
		this.rankMap = "";//权重
		this.b0110Map = "";//部门计划下的单位信息
		//应用配置参数
		this.plantype = config.plantype;
		this.createStyleCss();
		this.init();
	},
	//初始化
	init : function(){
		var map = new HashMap();
		map.put("type","9");
		map.put("plantype",this.plantype);
	    Rpc({functionId:'WP50000001',async:false,success:this.getTableOK,scope:this},map);
	},
	//加载主页面
	getTableOK : function(form){
		var result = Ext.decode(form.responseText);
		var jsonData = result.tableConfig;
		this.paramlist = result.plantypejson;//计划期间配置
		var obj = Ext.decode(jsonData);
        if (this.currentPage>1){
            obj.beforeBuildComp=function (grid) {
                grid.storeConfig.currentPage=this.currentPage;
            }
        }
		this.tableObj = new BuildTableObj(obj);
		this.saveQuery = result.saveQuery;//当前登录人是否有设置公共查询组件的公共方案权限
		this.canCreateOrg = result.canCreateOrg;//当前登录人是否有部门制定计划的权限
		this.canCreatePerson = result.canCreatePerson;//当前登录人是否有制定个人计划的权限
		this.defaultQueryFields = result.defaultQuery;//公共查询组件默认查询字段
		this.curUsername = result.curUsername;
		//初始化计划区间相关参数
		this.initPeriodParams();
		
		var complexPanel = this.getComplexPanel();
		var searchSchemePanel = this.searchSchemeView();
		this.tableObj.insertItem(searchSchemePanel,0);// 插入方案查询
        if(this.plantype=="1")//部门计划监控屏蔽公共查询组件
			this.tableObj.insertItem(complexPanel,0);// 插入公共查询组件
			
		loadMask = new Ext.LoadMask(Ext.getCmp('workplan_hr_mainPanel'), {
			msg:"请稍候…"
		});
		loadMask.show();
	},createStyleCss:function(){
		if(!Ext.util.CSS.getRule('.link-visited')) {
		 Ext.util.CSS.createStyleSheet(".link-visited{background:#EEEEEE;font-weight:bolder;}");
	 	}
		/** 名称和部门名称链接样式 **/
		Ext.util.CSS.createStyleSheet(".a0100Andorg{float:left;color:#2C33FF;cursor:pointer;}");
		Ext.util.CSS.createStyleSheet(".dealto-img{float:right;cursor:pointer;}");
		Ext.util.CSS.createStyleSheet(".followercls{font-size:12px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}");
		Ext.util.CSS.createStyleSheet("#yingbao span:hover,#weibao span:hover,#yibao span:hover,#weipi span:hover,#yipi span:hover,#yibiangeng span:hover{background:#EEEEEE;font-weight:bolder;}");
	},
	//查询方案布局
	searchSchemeView : function(){
		var me = this;
		//计划选择控件
		var style = "style='color:#1B4A98;cursor:pointer;margin:0 10px 0 0;'";
		var periodCascade = me.getPeriodCascade();
		var schemePanel = Ext.create('Ext.container.Container',{ 
			id:'workplanhr_schemePanel',
			width:'100%',
			margin:'4 0 0 0',
			layout: {
		        type: 'hbox'
		    },
			border:false,
			items:[{
					xtype : 'label',
					margin:'0 5 0 3',
		            text: wp.hr.searchScheme+'：'
	            },periodCascade,{
	            	xtype : 'label',
	            	style:'color:#C5C5C5;',
	            	margin:'0 10 0 0',
	            	text:"|"
	            },{
	            	xtype : 'label',
	            	id:'yingbao',
	            	width:95,
	                html:wp.hr.yingbao+"：<span id ='yingbaoNum' sublabel=sublabel onclick='WorkPlanhr_me.searchScheme(\"0\", this);' "+style+">0人</span>"//应报
	            },{
	            	xtype : 'label',
	            	id:'weibao',
	            	width:95,
	                html:wp.hr.weibao+"：<span id ='weibaoNum' sublabel=sublabel onclick='WorkPlanhr_me.searchScheme(\"1\", this);' "+style+">0人</span>"//未报
	            },{
	            	xtype : 'label',
	            	id:'yibao',
	            	width:95,
	                html:wp.hr.yibao+"：<span id ='yibaoNum' sublabel=sublabel onclick='WorkPlanhr_me.searchScheme(\"2\", this);' "+style+">0人</span>"//已报
	            },{
	            	xtype : 'label',
	            	id:'weipi',
	            	width:80,
	                html:wp.hr.weipi+"：<span id ='weipiNum' sublabel=sublabel onclick='WorkPlanhr_me.searchScheme(\"3\", this);' "+style+">0人</span>"//未批
	            },{
	            	xtype : 'label',
	            	id:'yipi',
	            	width:95,
	                html:wp.hr.yipi+"：<span id ='yipiNum' sublabel=sublabel onclick='WorkPlanhr_me.searchScheme(\"4\", this);' "+style+">0人</span>"//已批
	            },{
	            	xtype : 'label',
	            	id:'yibiangeng',
	            	width:95,
	                html:wp.hr.yibiangeng+"：<span id ='yibiangengNum' sublabel=sublabel onclick='WorkPlanhr_me.searchScheme(\"5\", this);' "+style+">0人</span>"//已变更
	            }]
	         
		});
		return schemePanel;
	},
	/**periodType 期间类型
	 * periodYear  当前年
	 * 
	 * periodMonth 当前月
	 * 
	 * type = 3 是方案查询
	 * 
	 */
	searchScheme :function(submittype,dom){
		
		/**
		 * schemeTypeArray格式：[期间类型，当前年，当前月(季度 半年)，人员提交情况]
		 * 
		 * submitCase 应报已报标识 0：应报 1：未报 2：已报 3：未批 4：已批 5：已变更
		 */
		var me = this;
		this.addVisitedCss(dom);

		//默认查应报
		if(Ext.isEmpty(submittype))
			submittype = "0";
		me.submittype = submittype;//存储当前选中的应报已报标识
		var map = new HashMap();
		map.put("type","3");
		map.put("plantype",me.plantype);
		map.put("periodtype",me.periodtype);
		map.put("periodyear",me.periodyear);
		map.put("periodmonth",me.periodmonth);
		map.put("periodweek",me.periodweek);
		map.put("submittype",me.submittype);
		Rpc( {
      		functionId : 'WP50000001',
      		success :function(form){
      			var result = Ext.decode(form.responseText)
      			var errorcode = result.errorcode;
				if (errorcode == 0) {
					me.followerMap = result.followerMap;//计划关注人
					me.rankMap = result.rankMap;//权重
					me.b0110Map =result.b0110Map;//部门计划下的单位信息
					var store = Ext.data.StoreManager.lookup('workplan_hr_dataStore');
					store.load();
				}
      		},async : true
		}, map);
	},
	//复杂查询组件
	getComplexPanel : function(){
		return Ext.create("EHR.commonQuery.CommonQuery",{
			id:"hrcommonQueryId",	            	
            subModuleId:'hr_commonQuery_subModuleId',
            fieldPubSetable:WorkPlanhr_me.saveQuery,
            defaultQueryFields:WorkPlanhr_me.defaultQueryFields,
            optionalQueryFields:'A', 
            ctrltype:'3',
            nmodule:'5',//OKR 走绩效模板号
            doQuery:function(items){
            	var map = new HashMap();
            	map.put("type","14");
              	map.put("items", items);
              	Rpc( {
              		functionId : 'WP50000001',
              		success :function(outparamters){
              			var result = Ext.decode(outparamters.responseText);
              			if(result.errorcode!=0){
              				Ext.showAlert(result.message);
              				return;
              			}
              			WorkPlanhr_me.loadData();
              		}
              	}, map);
              }             
        
        });
	},
	//计划区间组件
	getPeriodCascade : function(){
		var me = this; 
		var obj = new Object();
		obj.defaltperiodtype = me.periodtype;
		obj.params = Ext.decode(me.paramlist);
		obj.callbackFn = WorkPlanhr_me.queryNum;
		obj.scope = WorkPlanhr_me;
		return Ext.create("PeriodCascadeForTrackUL.PeriodCascadeForTrack", obj)
	},
	//个人计划名称列渲染
	a0101: function(val,meteData,rec){
		var html = val;
		if(!Ext.isEmpty(val)){
			var onclick = "";
			var objectid = rec.data.objectid_url_e;
			var p0723 = "VSx113s2mibhuiaSibWzGW2ag";
			var dept_leader = "";
			html = "<span title='查看计划' class='a0100Andorg' onclick='WorkPlanhr_me.loadPeoplePlan(\""+objectid+"\",\""+dept_leader+"\",\""+p0723+"\",\"hr\")'>"+val+"</span>";
			//制定计划权限控制
			if(WorkPlanhr_me.canCreatePerson){
				html+="<img title='制定计划' class='dealto-img' onclick='WorkPlanhr_me.loadPeoplePlan(\""+objectid+"\",\""+dept_leader+"\",\""+p0723+"\",\"hr_create\")' src='/images/new_module/dealto.gif'/>"
			}
		}
		return html;
	},
	//部门计划的部门名称列渲染
	orgrender : function(val,meteData,rec){
		if(!Ext.isEmpty(val)){
			val = val.split("`")[1];
			var objectid = rec.data.objectid_url_e;
			var dept_leader = rec.data.dept_leader_id_e;
			var p0723 = "qAehEzFZE1YZLcuMbBpkxg";
			var html = "<table style='width:100%;table-layout:fixed;'>" +
						"<tr>" +
							"<td><div onclick='WorkPlanhr_me.loadPeoplePlan(\""+objectid+"\",\""+dept_leader+"\",\""+p0723+"\",\"hr\")' title='"+val+"'class='followercls' style='color:#2C33FF;cursor:pointer;'>"+val+"</div></td>" +
							"<td width='20px'>";
			//制定计划权限控制
			if(WorkPlanhr_me.canCreateOrg){
				html+="<img title='制定计划' class='dealto-img' onclick='WorkPlanhr_me.loadPeoplePlan(\""+objectid+"\",\""+dept_leader+"\",\""+p0723+"\",\"hr_create\")' src='/images/new_module/dealto.gif'/>";
			}
								
			html+="</td></tr></table>";
			return html;
		}else
			return "";
	},
	/**
	 * 查看计划详情
	 * @returns
	 */
	loadPeoplePlan:function(objectid,dept_leader,p0723,fromflag){
        var pagebar = this.tableObj.tablePanel.getDockedItems('toolbar[dock="bottom"]')[0]
        var currentPage = pagebar.getPageData().currentPage;
		//这里的type 在监控页面取值为1 | 2  但是在计划页面取值是person |org 需要兼容下
		 var returnurl ="/module/workplan/workplanhr/WorkPlanHr.html?type="+WorkPlanhr_me.plantype+"&submittype="+WorkPlanhr_me.submittype;
        returnurl +="&currentPage="+currentPage;
         returnurl = getEncodeStr(returnurl); 
         var type = "person";
 		 if(WorkPlanhr_me.plantype == '2'){
 			type = "org";
 		 }
	    var url ="/workplan/work_plan.do?br_query=link&type="+type+"&objectid="+objectid
	             +"&p0723="+p0723
	             +"&periodtype="+WorkPlanhr_me.periodtype
	             +"&periodyear="+WorkPlanhr_me.periodyear
	             +"&periodmonth="+WorkPlanhr_me.periodmonth
	             +"&periodweek="+WorkPlanhr_me.periodweek
	             +"&deptleader="+dept_leader
	             +"&fromflag="+fromflag
	             +"&returnurl="+returnurl
	             ; 
	      location.href =url; 
	},
	/**
	 * 审批人列
	 */
	p0735 : function(val,metaData,record){
		var p0700 = record.data.p0700_e;
		var p0719 = record.data.p0719;
		if(p0719 != "1")
			return val;
		var html = "";
		html = val+"<img style='margin-left:10px;float:right;cursor:pointer;' title='重新指派' onclick='WorkPlanhr_me.assignApprover(\""+p0700+"\",this)' src='/images/new_module/dealto.gif'/>";
		return html;
	},
	/**
	 * 关注人列
	 */
	follower : function(val,metadata,record){
		var p0719 = record.data.p0719;
		var p0700 = record.data.p0700_e;
		
		var objectid_safe_e = record.data.objectid_safe_e;
		var followers = WorkPlanhr_me.followerMap;
		var value = "";
		if(!Ext.isEmpty(followers)){
			value = followers[objectid_safe_e]?followers[objectid_safe_e]:"";
		}
		if(!Ext.isEmpty(value)){
			var arr = value.split('、');
			value = '';
			Ext.each(arr, function(obj){
				var name = obj.split('_')[0];
				value += name+'、';
			});
			value = value.substring(0, value.length-1);
		}
		
		if(Ext.isEmpty(p0719) || p0719==0)//未提交的不可以设置关注人
			return value;
		var html = "";
		var title = Ext.isEmpty(value)?"":"title='"+value+"'";
			html = "<table style='width:100%;table-layout:fixed;'>" +
						"<tr>" +
							"<td><div id='flower_"+objectid_safe_e+"' "+title+" class='followercls'>"+value+"</div></td>" +
							"<td width='20px'>" +
								"<img title='指定关注人' class='dealto-img' onclick='WorkPlanhr_me.assignFollowers(\""+p0700+"\",this,\""+objectid_safe_e+"\")' src='/workplan/image/ait.jpg'/>"+
							"</td>" +
						"</tr>" +
					"</table>";
		return html;
	},
	/**
	 * 功能操作列
	 */
	operations:function(val,meta,rec){
		/**
		 * status
		 * =0 未提交
		 * =1 未批
		 * =2 已批
		 * =3 驳回（提醒写计划）
		 */
		var clickfunc = "";
		var p0719 = rec.data.p0719;
		var objectid= rec.data.objectid_safe_e;
		var html = '<span style="cursor:pointer;color:#2C33FF;" onclick="{function}">';
		if(Ext.isEmpty(p0719) || p0719==0 || p0719==3){
			html+="提醒写工作计划";
			
			clickfunc = "WorkPlanhr_me.noticeWriteOrApprove(0,'"+objectid+"')";
		}else if(p0719==1){
			html+="提醒批准工作计划";
			clickfunc = "WorkPlanhr_me.noticeWriteOrApprove(1,'"+objectid+"')";
		}else if(p0719==2){
		    var relatePlanid = rec.data.relate_planid;
			if(!Ext.isEmpty(relatePlanid)){
				html+="更新目标卡";
				clickfunc = 'WorkPlanhr_me.updateCard(\''+objectid+'\')';
			}else{
				html+="关联到考核计划";
				clickfunc = 'WorkPlanhr_me.relatePlanAll(\''+objectid+'\')';
			}
		}
		html+="</span>";
		html = html.replace("{function}",clickfunc);
		return html;
	},	
	// 获取显示人数
	queryNum : function(periodtype, periodyear, periodmonth, periodweek){
		var me = this;
		me.periodtype = periodtype;
		me.periodyear = periodyear;
		me.periodmonth = periodmonth;
		me.periodweek = periodweek;
		
		var map = new HashMap();
		map.put("type", '8');
		map.put("plantype", me.plantype);
		map.put("periodtype", periodtype);
		map.put("periodyear", periodyear);
		map.put("periodmonth", periodmonth);
		map.put("periodweek", periodweek);
		Rpc({functionId : 'WP50000001',async : true,success : function(form){
			var response = Ext.decode(form.responseText);
			var numMap = response.querynum;
			var sum_count = numMap.sum_count;
			var unsubmit_count = numMap.unsubmit_count;
			var submit_count = numMap.submit_count;
			var unapprove_count = numMap.unapprove_count;
			var approve_count = numMap.approve_count;
			var change_count = numMap.change_count;
			Ext.getDom('yingbaoNum').innerHTML = sum_count+'人';
			Ext.getDom('weibaoNum').innerHTML = unsubmit_count+'人';
			Ext.getDom('yibaoNum').innerHTML = submit_count+'人';
			Ext.getDom('weipiNum').innerHTML = unapprove_count+'人';
			Ext.getDom('yipiNum').innerHTML = approve_count+'人';
			Ext.getDom('yibiangengNum').innerHTML = change_count+'人';
			var domId = "yingbaoNum";
			if (me.submittype == 1)
                domId = "weibaoNum";
			else if (me.submittype == 2)
                domId = "yibaoNum";
            else if (me.submittype == 3)
                domId = "weipiNum";
            else if (me.submittype == 4)
                domId = "yipiNum";
            else if (me.submittype == 5)
                domId = "yibiangengNum";
            Ext.getDom(domId).click();
			var store = Ext.data.StoreManager.lookup('workplan_hr_dataStore');
			store.on('load', function(){
				loadMask.hide();
			});

		},scope:this}, map);
	},
	//提醒大家写计划
	noticeAllWrite : function(){
		WorkPlanhr_me.noticeWriteOrApprove(0);
	},
	//提醒批准计划
	noticeApprove : function(record){
		WorkPlanhr_me.noticeWriteOrApprove(1);
	},
	noticeWriteOrApprove:function(submit_type, objectid){
		var objectids = new Array();
		
		if(Ext.isEmpty(objectid)){
			var selectData = WorkPlanhr_me.tableObj.tablePanel.getSelectionModel().getSelection();
			var noRelateMsg = "";
			var num = 0;
			for(var i=0; i<selectData.length; i++){
				var record = selectData[i];
				//未提交的无需提醒批准
				if((record.data.p0719=="2"||record.data.p0719=="1") && submit_type==0){
					num++;
					if(num>5)
						break;
					if(WorkPlanhr_me.plantype=="1"){
						if(!Ext.isEmpty(record.data.a0101))
							noRelateMsg=noRelateMsg+record.data.a0101+"、";
					}else{
						if(!Ext.isEmpty(record.data.e0122)){
							var e0122 = record.data.e0122.split("`")[1];
							noRelateMsg=noRelateMsg+e0122+"、";
						}
					}
				}else if(record.data.p0719!="1" && submit_type==1){
					num++;
					if(num>5)
						break;
					if(WorkPlanhr_me.plantype=="1"){
						if(!Ext.isEmpty(record.data.a0101))
							noRelateMsg=noRelateMsg+record.data.a0101+"、";
					}else{
						if(!Ext.isEmpty(record.data.e0122)){
							var e0122 = record.data.e0122.split("`")[1];
							noRelateMsg=noRelateMsg+e0122+"、";
						}
					}
				}else{
					objectids.push(selectData[i].data.objectid_safe_e);
				}
			}
			if(num>0){
				var msg = WorkPlanhr_me.plantype=="1"?"人员":"部门";
				noRelateMsg = noRelateMsg.substring(0,noRelateMsg.length-1);
				if(num>5)
					noRelateMsg +="等";
				if(submit_type==0)
					msg = msg+"【"+noRelateMsg+"】的计划已提交!";
				else
					msg = msg+"【"+noRelateMsg+"】的计划未提交或已批准!";
				Ext.showAlert(msg);
				return;
			}
		}else{
			objectids.push(objectid);
		}
		var confirmMsg = "";
		var temp = WorkPlanhr_me.plantype=="1"?"人员":"部门";
		if(submit_type == 0){
			confirmMsg = objectids.length==0 ?"确定提醒大家写计划吗？":"确定提醒选中的"+temp+"写计划吗？";
		}else{
			confirmMsg = objectids.length==0 ?"确定提醒大家批准计划吗？":"确定提醒批准选中"+temp+"的计划吗？";
		}
		Ext.showConfirm(confirmMsg,function(flag){
			if(flag != "yes")
				return;
			var map = new HashMap();
			map.put("submit_type", submit_type);
			map.put("type","4");
			map.put("objectids",objectids.join(','));
			map.put("plantype",WorkPlanhr_me.plantype);
			map.put("periodtype",WorkPlanhr_me.periodtype);
			map.put("periodyear",WorkPlanhr_me.periodyear);
			map.put("periodmonth",WorkPlanhr_me.periodmonth);
			map.put("periodweek",WorkPlanhr_me.periodweek);
			Rpc({functionId : 'WP50000001',async : true,success : function(form){
				var result = Ext.decode(form.responseText);
				if(result.errorcode==1){
					Ext.showAlert(result.message);
				}else{
					Ext.showAlert("提醒成功！");
				}
			}}, map);
		});
	},
	updateCard_btn:function(){
		WorkPlanhr_me.updateCard();
	},
	//更新目标卡
	updateCard : function(objectid){
		var objectids = new Array();
		if(Ext.isEmpty(objectid)){
			var selectData = WorkPlanhr_me.tableObj.tablePanel.getSelectionModel().getSelection();
			/*var noRelateMsg = "";
			var num = 0;*/
			for(var i=0; i<selectData.length; i++){
				var record = selectData[i];
				/*if(!(record.data.p0719=="2"&&record.data.changeflag==1)){
					num++;
					if(num>5)
						break;
					if(WorkPlanhr_me.plantype=="1"){
						if(!Ext.isEmpty(record.data.a0101))
							noRelateMsg=noRelateMsg+record.data.a0101+"、";
					}else{
						if(!Ext.isEmpty(record.data.e0122)){
							var e0122 = record.data.e0122.split("`")[1];
							noRelateMsg=noRelateMsg+e0122+"、";
						}
					}
				}else{*/
					objectids.push(record.data.objectid_safe_e);
				// }
			}
            //提示不满足条件的人
			/*if(num>0){
				var msg = WorkPlanhr_me.plantype=="1"?"人员":"部门";
				noRelateMsg = noRelateMsg.substring(0,noRelateMsg.length-1);
				if(num>5)
					noRelateMsg +="等";
				msg = msg+"【"+noRelateMsg+"】的计划未提交或未变更!";
				Ext.showAlert(msg);
				return;
			}*/
		}else{
			objectids.push(objectid);
		}
		var temp = WorkPlanhr_me.plantype=="1"?"人员":"部门";
		var confirmMsg = objectids.length==0 ?"是否要为所有已关联计划的"+temp+"更新目标卡？":"是否要为选中已关联计划的"+temp+"更新目标卡？";
		Ext.showConfirm(confirmMsg,function(flag){
			if(flag != "yes")
				return;
			var returnMap = WorkPlanhr_me.checkIsScoring(objectids);
            if (!Ext.isEmpty(returnMap.msg)){
                Ext.showConfirm(returnMap.msg,function (flag) {
                    if(flag != "yes")
                        return;
                    var map = new HashMap();
                    map.put("type","6");
                    map.put("objectids",objectids.join(","));
                    map.put("plantype",WorkPlanhr_me.plantype+"");
                    map.put("periodtype",WorkPlanhr_me.periodtype+"");
                    map.put("periodyear",WorkPlanhr_me.periodyear+"");
                    map.put("periodmonth",WorkPlanhr_me.periodmonth+"");
                    map.put("periodweek",WorkPlanhr_me.periodweek+"");
                    map.put("clearObjMap",returnMap.clearIds);
                    Rpc({functionId : 'WP50000001',async : true,success : function(form){
                        var result = Ext.decode(form.responseText);
                        if(result.errorcode==1){
                            Ext.showAlert(result.message);
                        }else{
                            Ext.showAlert("更新目标卡成功！");
                        }
                    }},map);
                })
            }else{
                var map = new HashMap();
                map.put("type","6");
                map.put("objectids",objectids.join(","));
                map.put("plantype",WorkPlanhr_me.plantype+"");
                map.put("periodtype",WorkPlanhr_me.periodtype+"");
                map.put("periodyear",WorkPlanhr_me.periodyear+"");
                map.put("periodmonth",WorkPlanhr_me.periodmonth+"");
                map.put("periodweek",WorkPlanhr_me.periodweek+"");
                Rpc({functionId : 'WP50000001',async : true,success : function(form){
                    var result = Ext.decode(form.responseText);
                    if(result.errorcode==1){
                        Ext.showAlert(result.message);
                    }else{
                        Ext.showAlert("更新目标卡成功！");
                    }
                }},map);
            }
		})
	},
    /**
     * 检查考核对象是否已经开始评分
     */
    checkIsScoring:function(objectids){
        var returnMap;
        var map = new HashMap();
        map.put("type","15");
        map.put("objectids",objectids.join(","));
        map.put("plantype",WorkPlanhr_me.plantype+"");
        map.put("periodtype",WorkPlanhr_me.periodtype+"");
        map.put("periodyear",WorkPlanhr_me.periodyear+"");
        map.put("periodmonth",WorkPlanhr_me.periodmonth+"");
        map.put("periodweek",WorkPlanhr_me.periodweek+"");
        Rpc({functionId : 'WP50000001',async : false,success : function(form){
            var result = Ext.decode(form.responseText);
            returnMap = result.returnMap;
        }},map);
        return returnMap;
    },
	//添加选中样式
	addVisitedCss:function(dom){
		var me = this;
		me.removeVisitedCss();
		var clsName = dom.className;
    	if(dom)
    		dom.className = 'link-visited';
    },
    //清除选中样式
    removeVisitedCss:function(){
    	var submitCase = Ext.query('span[sublabel=sublabel]');//提交情况
    	Ext.each(submitCase, function(dom){
    		dom.className = '';
    	});
    },
    //获得默认的期间类型
    initPeriodParams:function(){
    	var me = this;
    	var params = Ext.decode(me.paramlist);
    	if(params.length==0){
    		Ext.showAlert("未启用任何类型的计划!");
    		return;
    	}
		var periodtype = getCookieValue(me.curUsername+"_type");
		if(periodtype){
			me.periodtype = periodtype;
            setCookie(WorkPlanhr_me.curUsername+"_type",periodtype);
		}else{
			for(var p in params){
	            var param = params[p];
	            if(param['p0'] != undefined ){
	            	me.periodtype = "1";
	            }else if(param['p1'] != undefined ){
	            	me.periodtype = "2";
	            }else if(param['p2'] != undefined ){
	            	me.periodtype = "3";
	            }else if(param['p3'] != undefined ){
	            	me.periodtype = "4";
	            }else if(param['p4'] != undefined ){
	            	me.periodtype = "5";
	            }
			}
		}
		if ("2" == periodtype){
            me.periodmonth =getCookieValue(me.curUsername+"_halfyearnum");
        }else if ("3" == periodtype){
            me.periodmonth =getCookieValue(me.curUsername+"_quarternum");
        }else if ("4" == periodtype || "5" == periodtype){
            me.periodmonth =getCookieValue(me.curUsername+"_month");
        }
        me.periodyear = getCookieValue(me.curUsername+"_year");
        me.periodweek = getCookieValue(me.curUsername+"_weeknum");

        var map = new HashMap();
        map.put("periodtype",me.periodtype);
        map.put("type","10");
        Rpc({functionId : 'WP50000001',async : false,success : function(form){
            var result = Ext.decode(form.responseText);
            if (Ext.isEmpty(me.periodyear)){
                me.periodyear = result.periodyear;
                setCookie(WorkPlanhr_me.curUsername+"_year",me.periodyear);
            }
            if (Ext.isEmpty(me.periodmonth)) {
                me.periodmonth = result.periodmonth;
                setCookie(WorkPlanhr_me.curUsername + "_month", (me.periodmonth<10?"0":"")+me.periodmonth);
            }
            if (Ext.isEmpty(me.periodweek)) {
                me.periodweek = result.periodweek;
                setCookie(WorkPlanhr_me.curUsername + "_weeknum", me.periodweek);
            }
            if (Ext.isEmpty(me.weeknum)) {
                me.weeknum = result.weeknum;
            }
        }},map);
    },
    /**
     * 计划状态
     */
    p0719:function(value,metadate,record){
    	var status = "";
    	if(Ext.isEmpty(value))
    		status = "未提交";
    	else{
    		if("0"==value){
    			status = "未提交";
    		}else if("1"==value){
    			status = "已提交";
    		}else if("2"==value){
    			var changeflag = record.data.changeflag;//变更标记  =1修改过
    			if(changeflag==1){
    				status = "已变更";
    			}else{
    				status = "已批准";
    			}
    		}else if("3"==value){
    			status = "已退回";
    		}
    	}
    	return status;
    },
    /**
    打开关联考核计划界面
    */
    selectPlan : function(objectids) {
    	retvo = "";
        var thecodeurl ="/workplan/relate_plan.do?b_query=link"
            +"&plantype="+WorkPlanhr_me.plantype
            +"&periodtype="+WorkPlanhr_me.periodtype+"&periodyear="+WorkPlanhr_me.periodyear
                 +"&periodmonth="+WorkPlanhr_me.periodmonth+"&periodweek="+WorkPlanhr_me.periodweek; 
        Ext.create("Ext.window.Window",{
        	id:'selectplan',
        	width:850,
        	height:410,
        	title:'关联考核计划',
        	resizable:'no',
        	modal:true,
        	autoScroll:true,
        	autoShow:true,
        	autoDestory:true,
        	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+thecodeurl+"'></iframe>",
        	renderTo:Ext.getBody(),
        	listeners:{
        		'close':function(){
        			if(retvo.success=="1")  {
        	        	//开始关联
        				WorkPlanhr_me.checkPlan(retvo.plan_id,objectids);
        	        }
        		}
        	}
        })	
        
       
    },
    /**
    检查当前选中的计划是否可以关联
    */
    checkPlan : function(plan_id,objectids) { 
    	var me = this;
        var hashvo = new HashMap();
        //检查主体类别
        hashvo.put("type","11");
        hashvo.put("plantype", me.plantype+""); 
        hashvo.put("periodtype", me.periodtype+"");
        hashvo.put("periodyear", me.periodyear+"");
        hashvo.put("periodmonth", me.periodmonth+"");   
        hashvo.put("periodweek", me.periodweek+"");   
        hashvo.put("objectids", objectids.join(","));
        hashvo.put("planId", plan_id);
        Rpc( {functionId : 'WP50000001',async: false,
            success: function(response) {
                    var result = Ext.JSON.decode(response.responseText);
    		        var errorcode = result.errorcode;
    		        if (errorcode=="1"){
    		        	Ext.showConfirm('此计划没有设置'+ result.info+'主体类别，是否关联？',function(flag){
    		        		if(flag == "yes"){
    		        			me.checkIsRelated(objectids.join(","), plan_id);
    		        		}else {
    		        			return ;
    		        		}
    		        		
    		        	});
    		        } else {
    		        	me.checkIsRelated(objectids.join(","), plan_id);
    		        }
                } 
            }, hashvo);
    },
    //检查是所选人员是否已关联过
    checkIsRelated:function(objectids, plan_id){
    	var me = this;
    	
        var reRelalte="false";    //已关联的人是否重新关联
        var hashvo = new HashMap();
        hashvo.put("type","12");
        hashvo.put("plantype", me.plantype+""); 
        hashvo.put("periodtype", me.periodtype+"");
        hashvo.put("periodyear", me.periodyear+"");
        hashvo.put("periodmonth", me.periodmonth+"");   
        hashvo.put("periodweek", me.periodweek+"");   
        hashvo.put("objectids", objectids);
        hashvo.put("planId", plan_id);
        Rpc( {functionId : 'WP50000001',async: false,
             success: function(response) {
            	 		var result = Ext.JSON.decode(response.responseText);
        	 			var errorcode = result.errorcode;
    			        var name = result.objectName;
    			        var count = result.objectCount;
    			        if(errorcode == "0"){
    			        	me.checkTemplate(reRelalte, objectids, plan_id);
    			        }else {
    			        	var msg = '';
    			            if (Ext.isEmpty(count)){
    			            	msg = name+"已经关联考核计划，是否继续关联新考核计划中？"
    			            }
    			            else {
    			            	msg = name+"等"+count+"人已经关联考核计划，是否继续关联新考核计划中？";
    			            }
			            	Ext.showConfirm(msg, function(flag){
			            		if(flag == "yes"){
			            			reRelalte="true";
			            			me.checkTemplate(reRelalte, objectids, plan_id);
			            		} else {
			            			return ;
			            		}
			            	});
    			        }
                    }  
                }, hashvo);
    },
    checkTemplate:function(reRelalte, objectids, plan_id){
    	var me = this;
    	
    	//任务模板需要修改 但不能修改
    	var hashvo = new HashMap();
        hashvo.put("type","13");
        hashvo.put("plantype", me.plantype+""); 
        hashvo.put("periodtype", me.periodtype+"");
        hashvo.put("periodyear", me.periodyear+"");
        hashvo.put("periodmonth", me.periodmonth+"");   
        hashvo.put("periodweek", me.periodweek+"");   
        hashvo.put("objectids", objectids);
        hashvo.put("planId", plan_id);
        hashvo.put("reRelalte", reRelalte);
        Rpc( {functionId : 'WP50000001',async: false,
                 success: function(response) {
    			        var map = Ext.JSON.decode(response.responseText);
    			        var info = map.errorcode;
    			        var task_desc = map.taskDesc;
    			        if (info=="1"){
    			            Ext.showAlert('任务分类['+task_desc+']在考核模板中不存在，且此模板已经被其他考核计划使用，不能关联！');
    			           return;
    			        } else {
    			        	me.relatePlan(reRelalte, objectids, plan_id);
    			        }
                    }
                 }, hashvo);
    },
    // 开始关联
    relatePlan:function(reRelalte, objectids, plan_id){
    	var me = this;
    	
    	var hashvo = new HashMap();
        hashvo.put("type","5");
        hashvo.put("plantype", me.plantype+""); 
        hashvo.put("periodtype", me.periodtype+"");
        hashvo.put("periodyear", me.periodyear+"");
        hashvo.put("periodmonth", me.periodmonth+"");   
        hashvo.put("periodweek", me.periodweek+"");   
        hashvo.put("objectids", objectids);
        hashvo.put("planid", plan_id);
        hashvo.put("rerelalte", reRelalte);
        Rpc( {functionId : 'WP50000001',async: false,
                 success: function(response) {
                    var map = Ext.JSON.decode(response.responseText);
                    var errorcode = map.errorcode;
                    if (errorcode=="0"){
                       Ext.showAlert('已关联到考核计划！');             
    	               me.loadData();//刷新表格数据
                    }else{
                    	Ext.showAlert(map.message);
                    }
                }  }, hashvo);
    },
    loadData : function(){
    	var storeid = "workplan_hr_dataStore";
    	var store = Ext.data.StoreManager.lookup(storeid);
    	if(store)
    		store.load();
    },
    relatePlanAll_btn:function(){
    	WorkPlanhr_me.relatePlanAll();
    },
    relatePlanAll :function(objectid){
    	if (WorkPlanhr_me.periodtype==5){
    	      Ext.showAlert("周计划不能关联！");
    	      return;
	    }
	    var objectids = new Array();
	    if(Ext.isEmpty(objectid)){
			var selectData = WorkPlanhr_me.tableObj.tablePanel.getSelectionModel().getSelection();
			var noRelateMsg = "";
			var num = 0;
			for(var i=0; i<selectData.length; i++){
				var record = selectData[i];
				if(record.data.p0719!="2"){
					num++;
					if(num>5)
						break;
					if(WorkPlanhr_me.plantype=="1"){
						if(!Ext.isEmpty(record.data.a0101))
							noRelateMsg=noRelateMsg+record.data.a0101+"、";
					}else{
						if(!Ext.isEmpty(record.data.e0122)){
							var e0122 = record.data.e0122.split("`")[1];
							noRelateMsg=noRelateMsg+e0122+"、";
						}
					}
				}else{
					objectids.push(selectData[i].data.objectid_safe_e);
				}
			}
			if(num>0){
				var msg = WorkPlanhr_me.plantype=="1"?"人员":"部门";
				noRelateMsg = noRelateMsg.substring(0,noRelateMsg.length-1);
				if(num>5)
					noRelateMsg +="等"; 
				msg = msg+"【"+noRelateMsg+"】的计划未批准，不能关联!";
				Ext.showAlert(msg);
				return;
			}
		}else{
			objectids.push(objectid);
		}
	    var objecttype="人员";
	    if (WorkPlanhr_me.plantype=="2"){
	        objecttype="部门";
	    }
	    if (objectids.length==0){
	        //判断是否有已批准的计划，如果都未批准则无需关联
		    var hashvo = new HashMap();
		    hashvo.put("oprType","checkIsApproved");
		    hashvo.put("planType", WorkPlanhr_me.plantype+"");
		    hashvo.put("periodType", WorkPlanhr_me.periodtype+"");
		    hashvo.put("periodYear", WorkPlanhr_me.periodyear+"");
		    hashvo.put("periodMonth", WorkPlanhr_me.periodmonth+"");
		    hashvo.put("periodWeek", WorkPlanhr_me.periodweek+"");
		    hashvo.put("queryType", WorkPlanhr_me.querytype+"");
		    Rpc( {functionId : '9028000704',async: false,
		        success: function(response) {
		                var map = Ext.JSON.decode(response.responseText);
		                var info = map.info;
		                if (info=="false"){
		                	Ext.showAlert('当前查询列表没有已批准的计划，无需关联！');
		                	return;
		                }
		                Ext.showConfirm('是否要为所有'+objecttype+'关联考核计划？',function(flag){
		    		    	if(flag=="yes")
		    		    		 WorkPlanhr_me.selectPlan(objectids);
		    		    })
		            }
		        }, hashvo);
	    }
	    else {
	    	 Ext.showConfirm('是否要为当前选择'+objecttype+'关联考核计划？',function(flag){
	    		 if(flag=="yes")
		    		 WorkPlanhr_me.selectPlan(objectids);
 		    })
	    }
    },
    //指派审批人
    assignApprover : function(p0700,el){
    	var picker = new PersonPicker({
			multiple : false,
			titleText : "选择审批人",
			isPrivExpression:false,//不启用高级权限
			callback : function(c) {
				var objectid = c.id;
				
				var map = new HashMap();
				map.put("type","7");
				map.put("p0700", p0700);
				map.put("objectid", objectid);
				Rpc( {
		      		functionId : 'WP50000001',
		      		success :function(form){
		      			var result = Ext.decode(form.responseText)
		      			var errorcode = result.errorcode;
						if (errorcode == 0) {
							WorkPlanhr_me.loadData();
						}
		      		}
				}, map);
				
			}
		}, el.parentNode);
    	picker.open();
    },
    //添加计划关注人
    assignFollowers:function(p0700,el,objectid_safe_e){
    	var me = this;
    	// 增加默认已选的关注人 chent add 20171218 start
    	var defaultSelected = [];
    	var followers = WorkPlanhr_me.followerMap;
		var value = "";
		if(!Ext.isEmpty(followers)){
			value = followers[objectid_safe_e]?followers[objectid_safe_e]:"";
		}
		if(!Ext.isEmpty(value)){
			var arr = value.split('、');
			Ext.each(arr, function(obj){
				var nbsa0100_e = obj.split('_')[1];
				defaultSelected.push(nbsa0100_e);
			});
		}
    	// 增加默认已选的关注人 chent add 20171218 end
    	var picker = new PersonPicker({
			multiple : true,
			titleText : "选择关注人",
			isPrivExpression:false,//不启用高级权限
			defaultSelected : defaultSelected,//默认已选
			callback : function(c) {
				// 先删除原关注人
				var hashvo = new HashMap();
				hashvo.put("oprType", "delFollower");
				hashvo.put("plan_id", p0700);
				Rpc({
					functionId : '9028000702',
					async : false,
					success : delete_ok
				}, hashvo);
				
				// 添加新关注人
				function delete_ok(response) {
					if(c.length==0){
						var flowerId='flower_'+objectid_safe_e;
						Ext.getDom(flowerId).innerText = "";
						if(WorkPlanhr_me.followerMap[objectid_safe_e]){
							WorkPlanhr_me.followerMap[objectid_safe_e] = "";
						}
						return;
					}
					for (var i = 0; i < c.length; i++) {
						var objectid = c[i].id;
						me.addFollower(objectid, p0700);
					}
				}
			}
		}, el);
    	picker.open();
    },
    // 添加关注人
    addFollower : function(id, p0700) {
    	var me = this;
		var hashvo = new HashMap();
		hashvo.put("oprType", "addFollower");
		hashvo.put("plan_id", p0700);
		hashvo.put("followerId", id);
		Rpc({
			functionId : '9028000702',
			async : false,
			success : search_ok
		}, hashvo);

		function search_ok(response) {
			var map = new HashMap();
			map.put("type","3");
			map.put("plantype",me.plantype);
			map.put("periodtype",me.periodtype);
			map.put("periodyear",me.periodyear);
			map.put("periodmonth",me.periodmonth);
			map.put("periodweek",me.periodweek);
			map.put("submittype",me.submittype);
			Rpc( {
	      		functionId : 'WP50000001',
	      		success :function(form){
	      			var result = Ext.decode(form.responseText)
	      			var errorcode = result.errorcode;
					if (errorcode == 0) {
						me.followerMap = result.followerMap;
						WorkPlanhr_me.loadData();
					}
	      		}
			}, map);
		}

	},
    totalRank : function(val,metaData,rec){
    	var id = rec.data.objectid_safe_e;
    	var rankMap = WorkPlanhr_me.rankMap;
    	var value = "0%";
    	if(!Ext.isEmpty(rankMap) && !Ext.isEmpty(rankMap[id]))
    		value = rankMap[id];
    	return value;
    },
    b0110 : function(val,metaData,rec){
    	var id = rec.data.objectid_safe_e;
    	var b0110Map = WorkPlanhr_me.b0110Map;
    	var value = "";
    	if(!Ext.isEmpty(b0110Map) && !Ext.isEmpty(b0110Map[id]))
    		value = b0110Map[id];
    	return value;
    },
    deptLeader : function(val,metaData,rec){
    	var id = rec.data.objectid_safe_e;
    	var deptLeaderMap = WorkPlanhr_me.deptLeaderMap;
    	var value = "";
    	if(!Ext.isEmpty(deptLeaderMap) && !Ext.isEmpty(deptLeaderMap[id]))
    		value = deptLeaderMap[id];
    	return value;
    }
});