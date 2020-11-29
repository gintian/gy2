/**
 * linbz add 2016/10/17
 * 考勤日历
 */
Ext.define("KqEmpCalURL.KqEmpCal",{

	constructor:function(config) {
		empcal_me = this;
		this.nowDuration = null;//当前考勤期间
		this.sessionlist = null;//未封存的考勤期间集合
		this.datelist = null;//一个期间内所有的日期
		this.dailyInfo = null;//每天的出勤情况
		this.sumjson = null;
		this.num = 0;
		this.classinfo = null;//某天的班次信息
		this.cardtime = null;//刷卡时间
		this.oneday = null;//选中的某天
		this.classname = null;//班次名称
		this.leaveTemplates = null;//请假模板
		this.overtimeTemplates = null;//加班模板
		this.officeleaveTemplates = null;//公出模板
		this.cardTemplates = null;//刷卡模板
		this.cardToclass = null;//刷卡点信息
		this.barflag = null;//是否显示申请模板工具条=1显示
		this.version = null;//是否是新版本=0新 =1旧
		this.privs = null;//自助  补签加班请假公出的功能权限
		this.leaveApps = null;//请假单据
		this.overtimeApps = null;//加班单据
		this.officeleaveApps = null;//公出单据
		
		this.qxjQ15Templates = null;//请假-销假模板
		this.qxjQ11Templates = null;//加班-销假模板
		this.qxjQ13Templates = null;//公出-销假模板
		
		this.init();
	},
	//初始加载页面
	init:function() {
		var map = new HashMap();
	    map.put("flag","all");
	    map.put("self","self");
	    Rpc({functionId:'KQ10000001',async:false,success:this.getEmpCalOK,scope:this},map);
		
	},
	
	//切换考勤期间
	getSessiondata:function (form){
	      var sessiondata = document.getElementById('nowDuration');

	      var datavalue = sessiondata.innerHTML;
	      
	      if(form == 0){
	    	  empcal_me.num++;
	    	  if(empcal_me.num<empcal_me.sessionlist.length || empcal_me.num==empcal_me.sessionlist.length){
	    		  datavalue = empcal_me.sessionlist[empcal_me.num].duration;
	    	  }
	      }else if(form == 1){
	    	  if(empcal_me.num==0)
	    		  return;
	    	  empcal_me.num--;
	    	  if(empcal_me.num>0 || empcal_me.num==0){
	    		  datavalue = empcal_me.sessionlist[empcal_me.num].duration;
	    	  }
	       }
	      document.getElementById('nowDuration').innerHTML = datavalue;

	      var map = new HashMap();
	      map.put("nowDuration",datavalue);
	      map.put("flag","duration,summary,applyData");
	      map.put("self","self");
//	      map.put("startday",startday);
//	      map.put("endday",endday);
	      
	      Rpc({functionId:'KQ10000001',async:false,success:empcal_me.getDuration,scope:this},map);
	  },
	
	//刷新日历汇总数据
	getDuration: function (form){
//		  Ext.util.CSS.swapStyleSheet("emelCss","kqempcal.css");
		  var result = Ext.decode(form.responseText);
			empcal_me.nowDuration = result.nowDuration;
			empcal_me.sessionlist = result.durationsjson;
			empcal_me.datelist = result.datesjson;
			empcal_me.dailyInfo = result.dailyInfojson;
			empcal_me.sumjson = result.sumjson;
			empcal_me.barflag = result.barflag;
			
			empcal_me.leaveApps = result.applyData.leaveApps;//请假单据
			empcal_me.overtimeApps = result.applyData.overApps;//加班单据
			empcal_me.officeleaveApps = result.applyData.officeleaveApps;//公出单据
			
			Ext.getCmp('calview').setHtml(empcal_me.calview());
			var pieiditems = Ext.getCmp('pieid').items;
			Ext.getCmp('pieid').removeAll();
			Ext.getCmp('pieid').add(empcal_me.createPieChart());
			Ext.getCmp('leaveid').removeAll();
			Ext.getCmp('leaveid').add(empcal_me.leavePanel());
			Ext.getCmp('officeleaveid').removeAll();
			Ext.getCmp('officeleaveid').add(empcal_me.officeleavePanel());
			Ext.getCmp('overtimeid').removeAll();
			Ext.getCmp('overtimeid').add(empcal_me.overtimePanel());
	  },
	/**
	 *  初始回调函数
	 */  
	getEmpCalOK: function (form){
		var mainpanel = Ext.getCmp('mainPanel');
		if(mainpanel)
			mainpanel.destroy();
			
		var result = Ext.decode(form.responseText);
		empcal_me.nowDuration = result.nowDuration;
		empcal_me.sessionlist = result.durationsjson;
		empcal_me.datelist = result.datesjson;
		empcal_me.dailyInfo = result.dailyInfojson;
		empcal_me.sumjson = result.sumjson;
		empcal_me.classinfo = result.classinfo;
//		empcal_me.cardtime = result.cardtime;
		empcal_me.classname = result.classname;
		empcal_me.leaveTemplates = result.leaveTemplates;
		empcal_me.overtimeTemplates = result.overtimeTemplates;
		empcal_me.officeleaveTemplates = result.officeleaveTemplates;
		empcal_me.cardTemplates = result.cardTemplates;
		empcal_me.cardToclass = result.cardToclass;
		empcal_me.barflag = result.barflag;
		empcal_me.version = result.version;
		empcal_me.privs = result.privs;
		
		empcal_me.leaveApps = result.applyData.leaveApps;//请假单据
		empcal_me.overtimeApps = result.applyData.overApps;//加班单据
		empcal_me.officeleaveApps = result.applyData.officeleaveApps;//公出单据
		
		empcal_me.itemsMap = result.itemsMap;
		
		empcal_me.qxjQ15Templates = result.qxjQ15Templates;//请假-销假模板
		empcal_me.qxjQ11Templates = result.qxjQ11Templates;//加班-销假模板
		empcal_me.qxjQ13Templates = result.qxjQ13Templates;//公出-销假模板
		
		var curDate = new Date();
		var time=Ext.Date.format(curDate, 'Y.m.d');
		empcal_me.oneday = time;
		
		// 班次起止时间
		empcal_me.onduty = Ext.isEmpty(empcal_me.itemsMap.onduty)?"00:00":empcal_me.itemsMap.onduty;
		empcal_me.offduty = Ext.isEmpty(empcal_me.itemsMap.offduty)?"23:59":empcal_me.itemsMap.offduty;
    	empcal_me.offdate = empcal_me.oneday;
    	if(empcal_me.onduty>empcal_me.offduty){
    		var dt = new Date(empcal_me.oneday.replace(/-|\./g,"/"));
    		var dateOff = Ext.Date.add(dt, Ext.Date.DAY, +1);   
    		empcal_me.offdate = Ext.Date.format(dateOff, 'Y.m.d');
    	}
		
		empcal_me.filterPanel = Ext.create('Ext.panel.Panel', {
			id: 'panelid',
		    title: empcal_me.calviewTitle(),
		    border: false,
		    layout:'vbox',//垂直布局
		    autoScroll: true,
		    minWidth: 800,
		    autoHeight: true,//自动高度 
		    items: [{
//	        		id: 'caltitle',
//	        		xtype : 'label',
//	        		width: '100%',
//	        		border: false,
//	        		margin: '0 0 0 10',
//	        		html:empcal_me.calviewTitle()
//        		}
//		    	, {
//			        width: '100%',
//			        height: 5,
//			        border: false,
//		        	xtype : 'label',
//					html : "<div style='position: absolute;height:0;width:100%;border:solid 1px #EBEBEB;margin:0 0 0 0;'></div>  "
//		    	}
//		    	,{
			        width: '100%',//,Ext.getBody().getWidth()*0.5 
			        layout:'hbox',
			        border: false,
		        	items:[{
		        		id: 'calview',
		        		xtype : 'label',
		        		width: '50%',
		        		minWidth: 400,
		        		border: false,
		        		html:empcal_me.calview()
		        		}
		        	,{
		        		xtype:'panel',	
		        		id: 'pieid',
		        		width: '50%',
		        		minWidth: 400,
		        		border: false,
		        		margin: '50 0 0 0',
		    			items:[empcal_me.createPieChart()]
		        	}]
		    	}
		    	,{
	        		id: 'dateclassTitle',
	        		xtype : 'label',
	        		width: '100%',
	        		border: false,
	        		margin: '20 0 0 10',
	        		html: empcal_me.dateclassTitle("","","", empcal_me.classname.name)
        		},{
	        		id : 'timeline',
	        		xtype: 'label',
	        		margin: '60 0 0 40',
//	        		width: '90%',
	        		height : 90,
	        		border: false,
				    html  : empcal_me.timeline()
	        	},{
	        		id: 'leaveid',
	        		xtype : 'panel',
	        		width: '100%',
//	        		height : 344,
	        		border: false,
	        		items :[empcal_me.leavePanel()]
        		},{
	        		id: 'officeleaveid',
	        		xtype : 'panel',
	        		width: '100%',
	        		margin: '20 0 0 0',
//	        		height : 344,
	        		border: false,
	        		items :[empcal_me.officeleavePanel()]
        		},{
	        		id: 'overtimeid',
	        		xtype : 'panel',
	        		width: '100%',
	        		margin: '20 0 0 0',
//	        		height : 344,
	        		border: false,
	        		items :[empcal_me.overtimePanel()]
        		}
        		,{
	        		xtype : 'panel',
	        		width: '100%',
	        		height : 20,
	        		border: false,
	        		items :[]
        		}
	    	],
		    renderTo: Ext.getBody()
		});
		this.mainPanel  = Ext.widget("viewport",{
			  layout:'fit',
			  id:"mainPanel",
			  items:empcal_me.filterPanel
			});
	},
	/**
	 *  选择销假类型（按天、小时、区间）
	 *  flag =0天 =1小时 =2区间
	 *  apptype =1 销假 =Q15请假 =Q13公出 =Q11加班 
	 */
	qXJchangeType:function(flag, apptype){
		
		if(flag==0){
			document.getElementById("rdo1").checked=true;
			document.getElementById("rdo2").checked=false;
			document.getElementById("rdo3").checked=false;
			
			document.getElementById("daydivid").style.display='block';
			document.getElementById("hourdivid").style.display='none';
			if(apptype!="1"){
				document.getElementById("startimepul1").style.display='block';
				document.getElementById("startimepul").style.display='none';
				document.getElementById("endtimepul1").style.display='block';
				document.getElementById("endtimepul").style.display='none';
				if(apptype=="Q11")
					document.getElementById("classdivid").style.display='block';
			}
		}else if(flag==1){
			document.getElementById("rdo1").checked=false;
			document.getElementById("rdo2").checked=true;
			document.getElementById("rdo3").checked=false;
			
			document.getElementById("daydivid").style.display='none';
			document.getElementById("hourdivid").style.display='block';
			if(apptype!="1"){
				document.getElementById("startimepul1").style.display='block';
				document.getElementById("startimepul").style.display='none';
				document.getElementById("endtimepul1").style.display='block';
				document.getElementById("endtimepul").style.display='none';
				// 加班申请单按小时申请不需要参考班次
				if(apptype=="Q11")
					document.getElementById("classdivid").style.display='none';
			}
		}else if(flag==2){
			document.getElementById("rdo1").checked=false;
			document.getElementById("rdo2").checked=false;
			document.getElementById("rdo3").checked=true;
			
			document.getElementById("daydivid").style.display='none';
			document.getElementById("hourdivid").style.display='none';
			if(apptype!="1"){
				document.getElementById("startimepul1").style.display='none';
				document.getElementById("startimepul").style.display='block';
				document.getElementById("endtimepul1").style.display='none';
				document.getElementById("endtimepul").style.display='block';
				if(apptype=="Q11")
					document.getElementById("classdivid").style.display='none';
			}
		}
		
	},
	// 校验时间是否在合理范围内
	checkTime:function(qnum, qstate03, tableflag, reMsg){
		var map = new HashMap();
		map.put("tableflag", tableflag);
	    map.put("q1501", qnum);
	    map.put("q1503", qstate03);
	    
	    var rdo1 = document.getElementById('rdo1').checked;
		var rdo2 = document.getElementById('rdo2').checked;
		var rdo3 = document.getElementById('rdo3').checked;
		var app_way = "0";
		if(rdo2)app_way = "1";
		else if(rdo3)app_way = "2";
		
		map.put("app_way", app_way);
		
		if(!reMsg){
			map.put("reMsg",reMsg);
		}else{
			map.put("reMsg",reMsg);
		}
		
		var regNum =/^\d*$/;
		if(app_way == "0"){
			var date_count = document.getElementById("date_count").value;
			if(!date_count || !regNum.test(date_count)){
				Ext.showAlert('天数不是数字类型！');
				document.getElementById("date_count").value = '1';
				return false;
			}
			map.put("date_count",date_count);
		}else if(app_way == "1"){
			var time_count = document.getElementById("time_count").value;
			if(!time_count || !regNum.test(time_count)){
				Ext.showAlert('小时数不是数字类型！');
				document.getElementById("time_count").value = '1';
				return false;
			}
			map.put("time_count",time_count);
		}else if(app_way == "2"){
			var xz1v = document.getElementById("xz1v").value;
			var xz3v = document.getElementById("xz3v").value;
//			if(!isDate(z1,"yyyy-MM-dd HH:mm") || !isDate(z3,"yyyy-MM-dd HH:mm")){
//			 	return false;
//			}
			hashvo.setValue("z1",z1);
			hashvo.setValue("z3",z3);
			
			map.put("z1", xz1v);
			map.put("z3", xz3v);
		}
    	Rpc({functionId:'1510020027',async:false,success:empcal_me.setStartTime,scope:this},map);
	},
	
	setStartTime:function (form){
		var result = Ext.decode(form.responseText);
	   	var st_date = result.z1;
	   	var end_date = result.z3;
	   	var err = result.err;
	   	var app_way = result.app_way;
	   	
//		if(app_way == "2"){
//			var hz1_obj=document.getElementById("xz1v");
//	   		var hz3_obj=document.getElementById("xz3v");
//			hz1_obj.value =st_date;
//			hz3_obj.value = end_date;
//		}else{
//		    var z1_obj=document.getElementById("xz1v");
//	   		var z3_obj=document.getElementById("xz3v");
//			z1_obj.value = st_date;
//			z3_obj.value = end_date;
//		}
	   	if(!Ext.isEmpty(err)){
//	   		returnValue = false;
	   		document.getElementById("date_count").value = '1';
	   		document.getElementById("time_count").value = '1';
	   		Ext.showAlert(err);
	   	}else{
	   		document.getElementById("xz1v").value =st_date;
	   		document.getElementById("xz3v").value = end_date;
//	    	returnValue = true;
	    }
	},
	
   	// 销假窗口
	getQXJWin:function(tableInfo, qxobj){
		//qnum, qz1, qz3, qstate, xnum, xreason, xz1, xz3, xz5, xspz5, qstate03, tableflag){
		var backWin = Ext.getCmp('qxjWinid');
	    if(backWin)
	    	backWin.close(); 
	    
		var spflag = "black";
		var appway = "none";
		var disabled = "disabled='disabled'";
		var readonly = "readonly='readonly'";
		// 39354 考勤自助销假单驳回后仍可继续报批
		if(qxobj.xspz5 == '07' || qxobj.xspz5 == '01' || Ext.isEmpty(qxobj.xz5)){
			spflag = "none";
			disabled = '';
			readonly = '';
			appway = "inline";
		}
		
		backWin = Ext.create('Ext.window.Window', {
			id : 'qxjWinid',
			modal : true,
			title: "销假申请单",
			height: 430,
			width: 500,
			border: false,
			layout:{
				type:'vbox',
				align:'left'
			},
			items: [{
					xtype: 'panel',
					height: 330,
					width: 500,
					border: false,
					html:"<div style='height:330px;width:400px;margin:14px 0 0 20px;font-size:15px;'>" +
							"<div style='height:30px;'>" +
								"<div style='position: absolute;height:18px;width:0;border:solid 3px #78C5FF;'></div>" +
								"<b><font style='position: absolute;margin:0 0 0 10px;font-size:16px;'>原始假单</font></b>" +
							"</div>" +
								"<div style='margin:6px 0 0 10px;'>"+tableInfo+"类型 "+qxobj.qstate+"</div>" +
								"<div style='margin:6px 0 0 10px;'>"+tableInfo+"起止 "+qxobj.qz1+"~"+qxobj.qz3+"</div>" +
							"<div style='height:30px;margin:26px 0 0 0;'>" +
								"<div style='position: absolute;height:18px;width:0;border:solid 3px #78C5FF;'></div>" +
								"<b><font style='margin:0 0 0 10px;font-size:16px;'>销假信息</font></b>" +
							"</div>" +
							"<div style='display:"+appway+";'> " +
								"<div style='margin:5px 0 0 10px;'>按 " +
									"<input id='rdo1' type='radio'  checked='checked' onclick='empcal_me.qXJchangeType(0,\"1\")' />天数  " +
									"<input id='rdo2' type='radio'  onclick='empcal_me.qXJchangeType(1,\"1\")'/>小时  " +
									"<input id='rdo3' type='radio'  onclick='empcal_me.qXJchangeType(2,\"1\")'/>区间  " +
								"</div>" +
								"<div id='daydivid' style='display: black;margin:8px 0 0 10px;'>" +
									"销假天数 <input id='date_count' onchange='empcal_me.checkTime(\""+qxobj.qnum+"\",\""+qxobj.qstate03+"\",\""+qxobj.tableflag+"\");' type='text' class='hj-zm-cj-xqbm' style='text-align:right;width:90px;' maxlength='2' size=8 value='1' />（天） " +
								"</div>" +
								"<div id='hourdivid' style='display: none;margin:8px 0 0 10px;'>" +
									"销假小时 <input id='time_count' onchange='empcal_me.checkTime(\""+qxobj.qnum+"\",\""+qxobj.qstate03+"\",\""+qxobj.tableflag+"\");' type='text' class='hj-zm-cj-xqbm' style='text-align:right;width:90px;' maxlength='2' size=8 value='1' /> 小时 " +
								"</div>" +
							"</div>" +
							"<div  id='timepul' style='margin:10px 0 0 10px;'>" +
								"销假起止 " +//'+qnum+'
								"<input name='xz1v' "+disabled+" type='text' id='xz1v' class='hj-zm-cj-xqbm'  style='width:130px' value='"+qxobj.xz1+"'/>"+
								"<img id='xz1vtime' class='img-middle' style='margin-left:-1px;display:"+appway+";height:24px;'"+
								"plugin='datetimeselector' inputname='xz1v' src='/module/recruitment/image/TIME.bmp' format='Y-m-d H:i' />"+
								
								 " ~ " +
								 
								 "<input name='xz3v' "+disabled+" type='text' id='xz3v' class='hj-zm-cj-xqbm'  style='width:130px' value='"+qxobj.xz3+"' />"+
								 "<img id='xz3vtime' class='img-middle' style='margin-left:-1px;display:"+appway+";height:24px;'"+
								 "plugin='datetimeselector' inputname='xz3v' src='/module/recruitment/image/TIME.bmp' format='Y-m-d H:i' >"+
									
							"</div>" +
							"<div style='margin:15px 0 0 10px;'>" +
								"销假事由 <textarea id='xreason' "+readonly+" rows='4' cols='40' style='vertical-align:top;border:1px #c5c5c5 solid; color:#333;resize:none;width:280px;height:73px;'>"+qxobj.xreason+"</textarea>" +
							"</div>" +
							"<div style='margin:20px 0 0 0;display:"+spflag+";'>" +
								"<div style='position: absolute;margin:5px 0 0 0;height:18px;width:0;border:solid 3px #78C5FF;'></div>" +
								"<b><font style='margin:2px 0 0 10px;font-size:16px;'>审批状态</font><font style='margin:5px 0 0 20px;font-size:20px;color:#78C5FF;'>"+qxobj.xz5+"</font></b>" +
							"</div>" +
						"</div>"
			        }
				,{
					xtype: 'panel',
					height: 30,
					width: 500,
					border: false,
					hidden: (spflag=='black')?true:false,
					margin : '16 0 0 0',
					items:[{
							xtype:'button',
							margin : '0 0 0 190',
							text:"报批",
							handler : function() {
								var rdo1 = document.getElementById('rdo1').checked;
								var rdo2 = document.getElementById('rdo2').checked;
								var rdo3 = document.getElementById('rdo3').checked;
								var app_way = "0";
								if(rdo2)app_way = "1";
								else if(rdo3)app_way = "2";
								//tableInfo, qnum, qz1, qz3, qstate, xnum, xreason, xz1, xz3, //xz5, xspz5
								var xz1Value = document.getElementById('xz1v').value;
								var xz3Value = document.getElementById('xz3v').value;
								var xreasonValue = document.getElementById('xreason').value;
								
								var map = new HashMap();
								map.put("flag", "kqemp");
								map.put("smflag", "02");
								map.put("id", qxobj.qnum);
								map.put("scope_start_time",xz1Value);
								map.put("scope_end_time", xz3Value);
								map.put("app_way", app_way);
								map.put("xreason", xreasonValue);
								map.put("tableflag", qxobj.tableflag);
								Rpc({functionId:'1510020021',async:false,success:function(form){
									var result = Ext.decode(form.responseText);
									if(result.succeed)
										Ext.showAlert("报批成功！");
									else
										Ext.showAlert(result.message);
									
									backWin.close();
									empcal_me.refreshAppData(qxobj.tableflag);
							      },scope:this},map);
							}
						},{
							xtype:'button',
							margin : '0 0 0 20',
							text:"取消",
							handler : function() {
								backWin.close();
							}
						}]
				}
			],
			listeners:{
				render:function(){
					// 重新加载时间控件
					empcal_me.datetimePull();
				}
			}
		});
		backWin.show();
	},
	   
	/**
	 * 销假申请单
	 */
	backHols:function(qz1, qz3, qstate, tableflag, xz1, xz3, xspz5, xz5, xreason, qnum, xnum, qspz5, qstate03, xdate){
//		console.log(qz1+"---"+ qz3+"---"+ qstate+"---"+ tableflag+"---"+ xz1+"---"+ xz3+"---"+ xspz5+"---"+ xz5+"---"+ xreason+
//		"---"+ qnum+"---"+ xnum+"---"+ qspz5+"---"+ qstate03+"---"+ xdate);
		
		// 若销假数据为空 则赋值原始单数据
		xdate = Ext.isEmpty(xdate)?empcal_me.oneday:xdate;
    	xz1 = Ext.isEmpty(xz1)?qz1:xz1;
    	xz3 = Ext.isEmpty(xz3)?qz3:xz3;
    	// 拼接json串对象
    	var qxjson = '{qnum:"'+qnum+'",qz1:"'+qz1+'",qz3:"'+qz3+'",qstate03:"'+qstate03+'",xnum:"'+xnum+'",xreason:"'+xreason
				+'",xz1:"'+xz1+'",xz3:"'+xz3+'",xdate:"'+xdate+'",qstate:"'+qstate+'",tableflag:"'+tableflag
				+'",xspz5:"'+xspz5+'",xz5:"'+xz5+'",qspz5:"'+qspz5+'"}';
    	
		var qxobj = Ext.decode(qxjson);
    	// 如果是已报批状态，则只显示出销假信息，不需显示销假模板
    	var xspz5flag = false;
		if(xspz5 == '02'){
			xspz5flag = true;
		}
		
		var tableInfo = "";
		if(tableflag == 'q11'){
			tableInfo = "加班";
			// 功能权限
			if(empcal_me.qxjQ11Templates.length == 0 && empcal_me.privs.overpxj==0){
				Ext.showAlert("您没有撤销加班申请权限！");
	        }
			else if(empcal_me.qxjQ11Templates.length == 0  || xspz5flag){
	        	
//	        	empcal_me.getQXJWin(tableInfo, qnum, qz1, qz3, qstate, xnum, xreason, xz1, xz3, xz5, xspz5, qstate03, tableflag);
	        	empcal_me.getQXJWin(tableInfo, qxobj);
			}else if(empcal_me.qxjQ11Templates.length == 1){//
				
				var valueid = empcal_me.qxjQ11Templates[0].temp.split(':')[0];
				var value = empcal_me.qxjQ11Templates[0].temp.split(':')[2];
				var other = empcal_me.getQXJOtherParams(value, qxobj);
				empcal_me.fill_out(valueid, other, xspz5, xnum);
			}
			else if(empcal_me.qxjQ11Templates.length > 1){
				
				empcal_me.dropdownDatas("xjq11", qnum, qxobj);
			}
		}else if(tableflag == 'q13'){
			tableInfo = "公出";
			// 功能权限
			if(empcal_me.qxjQ13Templates.length == 0 && empcal_me.privs.officepxj==0){
				Ext.showAlert("您没有撤销公出申请权限！");
	        }
			else if(empcal_me.qxjQ13Templates.length == 0 || xspz5flag){
	        	
				empcal_me.getQXJWin(tableInfo, qxobj);
			}else if(empcal_me.qxjQ13Templates.length == 1){//
				
				var valueid = empcal_me.qxjQ13Templates[0].temp.split(':')[0];
				var value = empcal_me.qxjQ13Templates[0].temp.split(':')[2];
				var other = empcal_me.getQXJOtherParams(value, qxobj);
				empcal_me.fill_out(valueid, other, xspz5, xnum);
			}
			else if(empcal_me.qxjQ13Templates.length > 1){
				
				empcal_me.dropdownDatas("xjq13", qnum, qxobj);
			}
		}else if(tableflag == 'q15'){
			tableInfo = "请假";
//			var leavehtml = "";
			//var leavePriv = false;
			// 功能权限
			if(empcal_me.qxjQ15Templates.length == 0 && empcal_me.privs.leavepxj==0 ){
				Ext.showAlert("您没有撤销请假申请权限！");
	        }
			else if(empcal_me.qxjQ15Templates.length == 0 || xspz5flag){
	        	
        		empcal_me.getQXJWin(tableInfo, qxobj);
			}else if(empcal_me.qxjQ15Templates.length == 1){//if(empcal_me.qxjQ15Templates.length == 1)
				
				var valueid = empcal_me.qxjQ15Templates[0].temp.split(':')[0];
				var value = empcal_me.qxjQ15Templates[0].temp.split(':')[2];
				var other = empcal_me.getQXJOtherParams(value, qxobj);
				empcal_me.fill_out(valueid, other, xspz5, xnum);
			}
			else if(empcal_me.qxjQ15Templates.length > 1){
				empcal_me.dropdownDatas("xjq15", qnum, qxobj);
			}
		}
	},
	/**
     *  获得每个单据的小窗
     */
	getAppPanel:function(one){
		var tableInfo = "";
		var qxjprive = true;
		if(one.tableflag == 'q11'){
			tableInfo = "加班";
			if(empcal_me.qxjQ11Templates.length == 0 && empcal_me.privs.overpxj==0)
				qxjprive = false;
		}else if(one.tableflag == 'q13'){
			tableInfo = "公出";
			if(empcal_me.qxjQ13Templates.length == 0 && empcal_me.privs.officepxj==0)
				qxjprive = false;
		}else if(one.tableflag == 'q15'){
			tableInfo = "请假";
			if(empcal_me.qxjQ15Templates.length == 0 && empcal_me.privs.leavepxj==0)
				qxjprive = false;
		}
		var qxjflag = "none";
		if(one.qspz5=='03' && one.xspz5!='03'){
			qxjflag = "inline";
		}
		// 既没有模板权限也没有自主销假权限，则不显示销假按钮  || 已封存的不能销假
		if(!qxjprive || empcal_me.barflag==0)
			qxjflag = "none";
//		var qxjson = '{qnum:"'+one.qnum+'",qz1:"'+one.qz1+'",qz3:"'+one.qz3+'",qstate03:"'+one.qstate03+'",xnum:"'+one.xnum+'",xreason:"'+one.xreason
//						+'",xz1:"'+one.xz1+'",xz3:"'+one.xz3+'",xdate:"'+one.xdate+'",qstate:"'+one.qstate+'",tableflag:"'+one.tableflag
//						+'",xspz5:"'+one.xspz5+'",xz5:"'+one.xz5+'",qspz5:"'+one.qspz5+'"}';
		var newReason = one.qreason;
		if(one.qreason.length > 26)
			newReason = one.qreason.substring(0, 24)+"..."; 
		
		return {
			   xtype: 'label',
			   border: false,
			   height: 170,
			   width: 300,
			   margin:'20 0 0 20',
			   layout:'hbox',//&nbsp; text-align: center; word-spacing:8px; align-items: center; justify-content: center; line-height:16px;
			   html  : "<div style='height:170px;width:300px;border:solid 1px #78C5FF;margin:0 0 0 0;'>" +
				   	"<div style='width:260px;font-size: 15px;color:#78C5FF;margin:12px 0 0 20px;'>"+one.qstate+"（"+one.qz5+"）" +
				   	
					"<div  style='display:"+qxjflag+";float:right;height:26px;width:50px;text-align:center;background:#FFAC00;margin:-4px 0 0 0;cursor:pointer;' " +
					//销假事件
					"onclick='empcal_me.backHols(\""+one.qz1+"\",\""+one.qz3+"\",\""+one.qstate+"\",\""+one.tableflag+"\"," +
							"\""+one.xz1+"\",\""+one.xz3+"\",\""+one.xspz5+"\",\""+one.xz5+"\",\""+one.xreason+"\",\""+one.qnum+"\"," +
							"\""+one.xnum+"\",\""+one.qspz5+"\",\""+one.qstate03+"\",\""+one.xdate+"\")' >" +
					"<font id='"+one.qnum+"' style='line-height:26px;color:#FFFFFF;font-size:15px;'>销假</font></div>" +
					"</div>"+		
					"<table border='0' cellspacing='0'  align='left' cellpadding='0' style='margin:10px 10px 0 20px;font-size: 14px;color:#979797;' >" +
					"<tr><td style='width:60px;'>开始时间</td><td>：</td><td>"+one.qz1+"</td></tr>"+
					"<tr><td>结束时间</td><td>：</td><td>"+one.qz3+"</td></tr>"+
					"<tr><td>"+tableInfo+"时长</td><td>：</td><td>"+one.qtimelen+"</td></tr>"+
					"<tr><td valign='top'>"+tableInfo+"原因</td><td valign='top'>：</td><td><font title=\""+one.qreason+"\" >"+newReason+"</font></td></tr>"+
					"<tr><td ><font style='margin:0 6px 0 0;'>审</font><font style='margin:0 7px 0 0;'>批</font>人</td><td>：</td><td>"+one.qapprover+"</td></tr>"+
					"</table>"
					+"</div>"
		};
		
	},
	/**
     *  展开收缩给类别单据面板
     */
	setIcon : function(panelid, imgid) {
//		var img = Ext.getDom(imgid);
//		var src = '../../../kq/images/kq_expand.png';
		if(Ext.getCmp(panelid).hidden){
//			src = '../../../kq/images/kq_contract.png';
			Ext.getCmp(panelid).setHidden(false)
		} 
		else{
			Ext.getCmp(panelid).setHidden(true);
		}
		// 取消右侧下拉图片
//		img.src = src;
	},
	
	/**
     *  模板下拉菜单小窗口
     */
    clickDataTask : function (strhtml, heightvalue, showid){
    	
        var win = Ext.getCmp('winTemp');
        if(win)
            win.close();
        var x=0;
        var y=0;
        // 补刷卡点暂未设置id 故特殊处理下
        if(showid == 'card'){
        	var e = event || window.event;
            x = e.clientX;
            y = e.clientY;
        }else{
        	x = Ext.get(showid).getX() - 14;
            y = Ext.get(showid).getY() + 24;
        }
        
        win = Ext.create('Ext.window.Window', {
            id : 'winTemp',
            header : false,
            resizable : false,
            x : x,
            y : y,
            minWidth : 100,
            height : heightvalue,
            html : strhtml+"" 
        });
        win.show();
        // 点击window外
        Ext.getBody().addListener('click', function(evt, el) {
        	var t = event || window.event;
            var cardflag = showid=='card'?(showid=='card' && x!=t.clientX && y!=t.clientY):true;
        	if (!win.hidden && showid!=el.id && cardflag)
        		win.close();
        });
    },
    /**
     *  拼接销假的其他参数
     */
    getQXJOtherParams:function(iniValues, qxobj){
    	//QXJ01`A0C02_2,QXJ05`A0C03_2,QXJZ1`A0C04_2,QXJZ3`A0C05_2,QXJ07`A0C06_2,
        //QXJ01_O`A0C09_2,QXJ03_O`A0C10_2,QXJZ1_O`A0C07_2,QXJZ3_O`A0C08_2
//    	console.log(qnum+"---"+ qz1+"---"+ qz3+"---"+ qstate03+"---"+ xnum+"---"+ xreason+"---"+ xz1+"---"+ xz3+"---"+ xdate);
//    	str[2], qxobj.qnum, qxobj.qz1, qxobj.qz3,
//		qxobj.qstate03, qxobj.xnum, qxobj.xreason, qxobj.xz1, qxobj.xz3, qxobj.xdate
    	
    	var otherparam = "";
    	if(iniValues.indexOf(',')!=-1){
    		var list = iniValues.split(",");
    		for(var i=0;i<list.length;i++){
    			var str = list[i];
    			if(str.indexOf('`')!=-1){
    				var indexid = str.split("`")[0];
    				var tempid = str.split("`")[1];
    				// QXJ销假标识
    				if(indexid.indexOf("QXJ")!=-1){
    					// 单据序号
    					if(indexid == "QXJ01"){
    						//销假单号由后台提供，前台不再传值。bug 56853 syl
    						//otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.xnum+"@INIT@";
    					}else if(indexid == "QXJ05"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.xdate+"@INIT@";
    					}else if(indexid == "QXJZ1"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.xz1+"@INIT@";
    					}else if(indexid == "QXJZ3"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.xz3+"@INIT@";
    					}else if(indexid == "QXJ07"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.xreason+"@INIT@";
    					}else if(indexid == "QXJ01_O"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.qnum+"@INIT@";
    					}else if(indexid == "QXJ03_O"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.qstate03+"@INIT@";
    					}else if(indexid == "QXJZ1_O"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.qz1+"@INIT@";
    					}else if(indexid == "QXJZ3_O"){
    						otherparam = otherparam + "i_"+tempid+"@KEY@"+qxobj.qz3+"@INIT@";
    					}
    					
    				}
    			}
			}
    	}
    	// otherparam 销假-拼接其他参数
    	otherparam = "iniValue="+otherparam;
    	return otherparam;
    },
    // 拼接请假、公出、加班模板的其他参数	    
    getOtherParams:function(iniValues){
    	
    	var otherparam = "";
    	if(iniValues.indexOf(',')!=-1){
    		var list = iniValues.split(",");
    		for(var i=0;i<list.length;i++){
    			var str = list[i];
    			if(str.indexOf('`')!=-1){
    				var indexid = str.split("`")[0];
    				var tempid = str.split("`")[1];
    				// QXJ销假标识
    				if(indexid.indexOf("QXJ")!=-1){
    					
    				}else{
    					// 加班的起止时间 特殊处理
						if(indexid=='Q11Z1'){
							// 若没有班次则加班的起始时间为当天的0点开始，有班次则按照当天班次的下班时间作为加班的起始时间
							var q11StartTime = empcal_me.classinfo.length == 0 ? "00:00" : empcal_me.offduty;							
							otherparam = otherparam + "i_"+tempid+"@KEY@"+empcal_me.offdate+" "+q11StartTime+"@INIT@";
						}
						else if(indexid.indexOf("Z1")!=-1)
							otherparam = otherparam + "i_"+tempid+"@KEY@"+empcal_me.oneday+" "+empcal_me.onduty+"@INIT@";
		
						if(indexid=='Q11Z3')
							otherparam = otherparam + "i_"+tempid+"@KEY@"+empcal_me.offdate+" 23:59@INIT@";
						else if(indexid.indexOf("Z3")!=-1)
							otherparam = otherparam + "i_"+tempid+"@KEY@"+empcal_me.offdate+" "+empcal_me.offduty+"@INIT@";
						
						// 申请类型
						if(indexid.indexOf("03")!=-1){
							var itemidvalue = "";
							if(indexid=='Q1103'){
								itemidvalue = empcal_me.itemsMap.q11items[0].itemid;
							}else if(indexid=='Q1303'){
								itemidvalue = empcal_me.itemsMap.q13items[0].itemid;
							}else if(indexid=='Q1503'){
								//itemidvalue = empcal_me.itemsMap.q15items[0].itemid;
							}
							otherparam = otherparam + "i_"+tempid+"@KEY@"+itemidvalue+"@INIT@";
						}
    				}
    			}
			}
    	}
    	// otherparam 拼接其他参数
    	otherparam = "iniValue="+otherparam;
    	return otherparam;
    },
    /**
     * 模板下拉菜单
     */
    dropdownDatas:function(flag, showid, qxobj){
    	// 已封存考勤期间直接返回
		if(empcal_me.barflag == 0)
			return;
		
    	var strlihtml1 = '<div style="height:22px;display:block;float:left;margin-left:10px;margin-right:10px;margin-top:4px;cursor:pointer">'+
    						'<a style="" id="';
        var strlihtml2 = '" onclick="' + 'empcal_me.fill_out(' 
        var strlihtml3 = ')' + '" href="javascript:void(0)">'
        var strlihtml4 = '</a> </div>';
        
        var strhtml = "";
        var height = 0 ;
        var other_param = "";
        var id = "";
		var list;
		var qxjflag = false;
		
        if(flag == 'q15'){
        	id = "q15appid";
    		list = empcal_me.leaveTemplates;
        }else if(flag == 'q13'){
        	id = "q13appid";
    		list = empcal_me.officeleaveTemplates;
		}else if(flag == 'q11'){
			id = "q11appid";
    		list = empcal_me.overtimeTemplates;
		}
        // 补刷卡 暂未用
		else if(flag == 'qcard'){
			id = "card";
    		list = empcal_me.cardTemplates;
		}
        // 销假
		else if(flag == 'xjq15'){
			id = showid;
			qxjflag = true;
    		list = empcal_me.qxjQ15Templates;
		}else if(flag == 'xjq13'){
			id = showid;
			qxjflag = true;
    		list = empcal_me.qxjQ13Templates;
		}else if(flag == 'xjq11'){
			id = showid;
			qxjflag = true;
    		list = empcal_me.qxjQ11Templates;
		}
//        other_param = empcal_me.getQXJOtherParams(str[2], qxobj.qnum, qxobj.qz1, qxobj.qz3,
//				qxobj.qstate03, qxobj.xnum, qxobj.xreason, qxobj.xz1, qxobj.xz3, qxobj.xdate);
        
        for(var i=0;i<list.length;i++){
			var str = list[i].temp.split(':');
			var strid = str[0]; 
			if(qxjflag)
				other_param = empcal_me.getQXJOtherParams(str[2], qxobj);
			else
				other_param = empcal_me.getOtherParams(str[2]);
			
			strhtml = strhtml 
			+"<tr><td>"
	            + strlihtml1
	            + i
	            + strlihtml2 
	            + str[0]+",\'"+other_param+"\'";
			// 销假增加审批状态和单号
	            if(qxjflag){
					strhtml = strhtml
					+",\'"+qxobj.xspz5+"\'"+",\'"+qxobj.xnum+"\'";
		        }
			
			strhtml = strhtml
				+ strlihtml3
	            + str[1]
	            + strlihtml4;
			+"</td></tr>"
			height = height + 30;
		}
        
        if(strhtml.length>0){
        	strhtml = "<table cellspacing='0' cellpadding='0'>"+strhtml+"</table>";
            empcal_me.clickDataTask(strhtml, height, id);
       }
	},
	// 请假单据
	leavePanel:function(){
		var list = [];
		for(var i=0;i<empcal_me.leaveApps.length;i++){
			var one = empcal_me.leaveApps[i];
			list.push(empcal_me.getAppPanel(one));
		}
		//请假q15
		var leavehtml = "";
		var leavePriv = false;
		
		if(empcal_me.leaveTemplates.length == 0 && empcal_me.privs.leavep==0){
            leavePriv = true;
        }else if(empcal_me.leaveTemplates.length == 0 && empcal_me.privs.leavep==1){
        	leavehtml = "<font id='q15appid' onclick='empcal_me.templateView(\"Q15\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}else if(empcal_me.leaveTemplates.length == 1){
			var leavalue = empcal_me.leaveTemplates[0].temp.split(':')[0];
			var other = empcal_me.getOtherParams(empcal_me.leaveTemplates[0].temp.split(':')[2]);
			leavehtml = "<font id='q15appid' onclick='empcal_me.fill_out(\""+leavalue+"\",\""+other+"\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}else if(empcal_me.leaveTemplates.length > 1){
			leavehtml = "<font id='q15appid' onclick='empcal_me.dropdownDatas(\"q15\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}
		
		var panel = Ext.create("Ext.panel.Panel",{
//			id:'pie_panel',
			title : false,
			border: false,
			layout:'vbox',
			margin:'0 0 0 10',
			items:[
			       {
			    	   xtype: 'label',
					   border: false,
					   width:'100%',
					   html  : "<div style='position: absolute;height:22px;width:0;border:solid 3px #78C5FF;margin:1px 0 0 0;'></div>" +
								"<b><font style='font-size: 18px;color:#979797;margin:0 0 0 10px;cursor:pointer;' onclick='empcal_me.setIcon(\"leaveDataid\",\"leaimgid\")' >请假</font>  " +
								leavehtml +
								"</b> " 
//								+"<img id='leaimgid' style='float:right;display:block;cursor:pointer;' onclick='empcal_me.setIcon(\"leaveDataid\",\"leaimgid\")' " +
//								" src='../../../kq/images/kq_contract.png' />"
			       },{
			    	   xtype: 'panel',
			    	   id:'leaveDataid',
					   border: false,
					   width:'100%',
					   layout : 'column',  
					   margin:'0 0 0 20',
					   items  :list
			       }
		        ]
		});
		return panel;
	},
	//公出单据
	officeleavePanel:function(){
		var list = [];
		for(var i=0;i<empcal_me.officeleaveApps.length;i++){
			var one = empcal_me.officeleaveApps[i];
			list.push(empcal_me.getAppPanel(one));
		}
		// 公出q13
		var officehtml = "";
		var officePriv = false;
		
		if(empcal_me.officeleaveTemplates.length == 0 && empcal_me.privs.officep==0){
			officePriv = true;
        }else if(empcal_me.officeleaveTemplates.length == 0 && empcal_me.privs.officep==1){
        	officehtml = "<font id='q13appid' onclick='empcal_me.templateView(\"Q13\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}else if(empcal_me.officeleaveTemplates.length == 1){
			var offvalue = empcal_me.officeleaveTemplates[0].temp.split(':')[0];
			var other = empcal_me.getOtherParams(empcal_me.officeleaveTemplates[0].temp.split(':')[2]);
			officehtml = "<font id='q13appid' onclick='empcal_me.fill_out(\""+offvalue+"\",\""+other+"\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}else if(empcal_me.officeleaveTemplates.length > 1){
			officehtml = "<font id='q13appid' onclick='empcal_me.dropdownDatas(\"q13\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}
		
		var panel = Ext.create("Ext.panel.Panel",{
			title : false,
			border: false,
			layout:'vbox',
			margin:'0 0 0 10',
			items:[
			       {
			    	   xtype: 'label',
					   border: false,
					   width:'100%',
					   html  : "<div style='position: absolute;height:22px;width:0;border:solid 3px #78C5FF;margin:1px 0 0 0;'></div>" +
								"<b><font style='font-size: 18px;color:#979797;margin:0 0 0 10px;cursor:pointer;' onclick='empcal_me.setIcon(\"officeDataid\")'>公出</font>  " +
								officehtml +
								"</b> " 
//								+"<img id='offimgid' style='float:right;display:block;cursor:pointer;' onclick='empcal_me.setIcon(\"officeDataid\",\"offimgid\")' " +
//								" src='../../../kq/images/kq_contract.png' />"
			       },{
			    	   xtype: 'panel',
			    	   id : 'officeDataid',
					   border: false,
					   width:'100%',
					   layout : 'column',  
					   margin:'0 0 0 20',
					   items  :list
			       }
		        ]
		});
		
		return panel;
		
	},
	//加班单据
	overtimePanel:function(){
		var list = [];
		for(var i=0;i<empcal_me.overtimeApps.length;i++){
			var one = empcal_me.overtimeApps[i];
			list.push(empcal_me.getAppPanel(one));
		}
		
		// 加班q11
		var overhtml = "";
		var overPriv = false;
		
		if(empcal_me.overtimeTemplates.length == 0 && empcal_me.privs.overp==0){
			overPriv = true;
        }else if(empcal_me.overtimeTemplates.length == 0 && empcal_me.privs.overp==1){
        	overhtml = "<font id='q11appid' onclick='empcal_me.templateView(\"Q11\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}else if(empcal_me.overtimeTemplates.length == 1){
			var overvalue = empcal_me.overtimeTemplates[0].temp.split(':')[0];
			var other = empcal_me.getOtherParams(empcal_me.overtimeTemplates[0].temp.split(':')[2]);
			overhtml = "<font id='q11appid' onclick='empcal_me.fill_out(\""+overvalue+"\",\""+other+"\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}else if(empcal_me.overtimeTemplates.length > 1){
			overhtml = "<font id='q11appid' onclick='empcal_me.dropdownDatas(\"q11\")' style='cursor:pointer;font-size: 18px;color:#78C5FF;' > 我要申请</font> ";
		}
		
		var panel = Ext.create("Ext.panel.Panel",{
//			id:'pie_panel',
			title : false,
			border: false,
			layout:'vbox',
			margin:'0 0 0 10',
			items:[
			       {
			    	   xtype: 'label',
					   border: false,
					   width:'100%',
					   html  : "<div style='position: absolute;height:22px;width:0;border:solid 3px #78C5FF;margin:1px 0 0 0;'></div>" +
								"<b><font style='font-size: 18px;color:#979797;margin:0 0 0 10px;cursor:pointer;' onclick='empcal_me.setIcon(\"overDataid\")'>加班</font>  " +
								overhtml +
								"</b> " 
//								+"<img id='overimgid' style='float:right;display:block;cursor:pointer;' onclick='empcal_me.setIcon(\"overDataid\",\"overimgid\")' " +
//								" src='../../../kq/images/kq_contract.png' />"
			       },{
			    	   xtype: 'panel',
			    	   id : 'overDataid',
					   border: false,
					   width:'100%',
					   layout : 'column',  
					   margin:'0 0 0 20',
					   items  :list
			       }
		        ]
		});
		return panel;
	},
	
	dateclassTitle : function(year, month, day, classname){
		if(year==null || year.length==0){
			var curDate = new Date();
			var time=Ext.Date.format(curDate, 'Y.m.d');
			year = time.substring(0, 4);
			month = time.substring(5, 7);
			day = time.substring(8);
		}
		var dcthtml = "<div style='position: absolute;height:22px;width:0;border:solid 3px #78C5FF;margin:0 0 0 0;'></div>" +
				"<p  style='font-size: 18px;color:#979797;margin:0 0 0 10px;'><b>"+year+"年"+month+"月"+day+"日</b>（"+classname+"）</p>  ";
		return dcthtml;
	},

	/**
	 *  获取申请单 申请结束日期
	 */
	setEndDate : function (tableflag, spflag)
    {
		var typeid = document.getElementById("typeid").value;
		if(Ext.isEmpty(typeid)){
			Ext.showAlert("申请类型不能为空！");
			return;
		}
		var map = new HashMap();
		map.put("table", tableflag);
		
		var rdo1 = document.getElementById('rdo1').checked;
		var rdo2 = document.getElementById('rdo2').checked;
		var rdo3 = document.getElementById('rdo3').checked;
		var app_way = "0";
		var start_time = document.getElementById("startime1").value;
		var end_time = document.getElementById("endtime1").value;
		if(rdo2)app_way = "1";
		else if(rdo3){
			app_way = "2";
			start_time = document.getElementById("startime").value;
			end_time = document.getElementById("endtime").value;
		}
		
		map.put("app_way", app_way);
		map.put("sels", typeid);// 申请类型
		map.put("start_d", start_time);
		var times = Ext.getCmp('time_field').getValue();
		var timeFormat = Ext.Date.format(times,'H:i');
		
		map.put("start_time_h", timeFormat.split(':')[0]);
		map.put("start_time_m", timeFormat.split(':')[1]);
		map.put("date_count", document.getElementById("date_count").value);
		map.put("time_count", document.getElementById("time_count").value);
		
		if(tableflag == "Q11" && rdo1)
			map.put("class_id", document.getElementById("classid").value);
		
		
		map.put("scope_start_time", start_time);
		map.put("scope_end_time", end_time);
		map.put("reflag", "0");
		if("1" == spflag)
			map.put("reflag", "1");
		var mess = "";
		Rpc({functionId:'1510020018',async:false,success:function(form){
					var result = Ext.decode(form.responseText);
					var err = result.err_message;
					if(!Ext.isEmpty(err)){
						mess = err;
						Ext.showAlert(err);
						return err;
					}
					if(!result.succeed){
						Ext.showAlert(result.message);
						return;
					}
					var endDate = result.endDate;
					if(Ext.isEmpty(endDate))
						return;
					document.getElementById("endtime1").value = endDate;
					document.getElementById("endtime").value = endDate;
				},scope:this},map);
		
		return mess;
    },
    
	/**
	 * 没有定义模板时 
	 */
	templateView : function(tableid){
		// 已封存考勤期间不允许申请
		if(empcal_me.barflag == 0)
			return;
		// new App Module
		var tableInfo = "";
		// 班次显示隐藏
		var classdiv = "none";
		var classSelecthtml = "";
		// 是否调休
		var restdiv = "none";
		var restSelecthtml = "";
		// 申请类型下拉数据
		var selecthtml = "<select id='typeid' onchange='empcal_me.setEndDate(\""+tableid+"\");' name='typeid' size='1' style='height:22px;width:128px' class='hj-zm-cj-xqbm'><option>  </option>";
		// empcal_me.itemsMap.q11items   {itemid: "12", itemdesc: "平时加班", parentid: "1"}
		// 结合班次起止时间
		var starDateValue = empcal_me.oneday.replace(/\./g,"-")+" "+empcal_me.onduty;
		var endDateValue = empcal_me.offdate.replace(/\./g,"-")+" "+empcal_me.offduty;
		if(tableid == 'Q13'){
			tableInfo = "公出";
			for(var i=0;i<empcal_me.itemsMap.q13items.length;i++){
				var str = empcal_me.itemsMap.q13items[i];
				selecthtml += "<option value='"+str.itemid+"'>"+str.itemdesc+"</option>";
			}
		}else if(tableid == 'Q11'){
			tableInfo = "加班";
			classdiv = "block";
			classSelecthtml = "<select id='classid' onchange='empcal_me.setEndDate(\""+tableid+"\");' name='classid' " +
					"size='1' style='height:22px;width:150px' " +
					"class='hj-zm-cj-xqbm'>";
			// 参考班次集合 empcal_me.itemsMap.classList
			for(var i=0;i<empcal_me.itemsMap.classList.length;i++){
				var str = empcal_me.itemsMap.classList[i];
				classSelecthtml += "<option value='"+str.dataValue+"'>"+str.dataName+"</option>";
			}
			classSelecthtml += "</select>";
			
			for(var i=0;i<empcal_me.itemsMap.q11items.length;i++){
				var str = empcal_me.itemsMap.q11items[i];
				selecthtml += "<option value='"+str.itemid+"'>"+str.itemdesc+"</option>";
			}
			// 是否调休校验
			if("1" == empcal_me.itemsMap.isExistIftoRest){
				restdiv = "block";
				restSelecthtml = "<select id='restSelectid' size='1' style='height:22px;width:60px' class='hj-zm-cj-xqbm'>" +
									"<option value=''></option>" +
									"<option value='1'>是</option>" +
									"<option value='2'>否</option>" +
								"</select>";
			}
			starDateValue = empcal_me.offdate.replace(/\./g,"-")+" "+empcal_me.offduty;
			endDateValue = empcal_me.offdate.replace(/\./g,"-")+" 23:59";
		}else if(tableid == 'Q15'){
			tableInfo = "请假";
			for(var i=0;i<empcal_me.itemsMap.q15items.length;i++){
				var str = empcal_me.itemsMap.q15items[i];
				selecthtml += "<option value='"+str.itemid+"'>"+str.itemdesc+"</option>";
			}
		}
		selecthtml += "</select>";
		
		var appWin = Ext.getCmp('appWinid');
	    if(appWin)
	    	appWin.close();
		
		appWin = Ext.create('Ext.window.Window', {
			id : 'appWinid',
			modal : true,
			title: "申请单",
			height: 410,
			width: 500,
			border: false,
			layout:{
				type:'vbox',
				align:'left'
			},
			items: [{
					xtype: 'panel',
					height: 330,
					width: 500,
					border: false,
					html:"<div style='height:330px;width:450px;margin:14px 0 0 20px;font-size:15px;'>" +
//							"<div style='position: absolute;height:18px;width:0;border:solid 3px #78C5FF;'></div>" +
							"<div style='margin:6px 0 0 10px;'>"+tableInfo+"类型 "+selecthtml+"</div>" +
//							"<div style=''> " +//appway  display:"+"black"+";
								"<div style='margin:8px 0 0 10px;'>按 " +
									"<input id='rdo1' type='radio' checked='checked' onclick='empcal_me.qXJchangeType(0,\""+tableid+"\");empcal_me.setEndDate(\""+tableid+"\");' />天数  " +
									"<input id='rdo2' type='radio' onclick='empcal_me.qXJchangeType(1,\""+tableid+"\");empcal_me.setEndDate(\""+tableid+"\");'/>小时  " +
									"<input id='rdo3' type='radio' onclick='empcal_me.qXJchangeType(2,\""+tableid+"\");empcal_me.setEndDate(\""+tableid+"\");'/>区间  " +
								"</div>" +
							// 申请单按区间，日期格式为Y-m-d H:i
							"<div  id='startimepul' style='margin:10px 0 0 10px;display:none;'>" +
								"起始日期 " +//'+qnum+'
								"<input name='startime'  type='text' id='startime' class='hj-zm-cj-xqbm' style='width:130px' value='"+starDateValue+"'/>"+
								"<img id='startimeimg' class='img-middle' style='margin-left:-1px;height:24px;'"+
								"plugin='datetimeselector' inputname='startime' src='/module/recruitment/image/TIME.bmp' format='Y-m-d H:i' />" +
								" (日期格式：2017-12-15 11:25)"+
							"</div>" +
							// 申请单按天、小时，日期格式为Y-m-d
							"<div  id='startimepul1' style='margin:10px 0 0 10px;display:black;'>" +
								"起始日期 " +//'+qnum+'
								"<input name='startime1'  type='text' id='startime1' class='hj-zm-cj-xqbm' style='width:130px' value='"+empcal_me.oneday.replace(/\./g,"-")+"' />"+
								"<img id='startime1img' class='img-middle' style='margin-left:-1px;height:24px;'"+
								"plugin='datetimeselector' inputname='startime1' src='/module/recruitment/image/TIME.bmp' format='Y-m-d' />" +
								" (日期格式：2017-12-15)"+
							"</div>" +
							"<div id='daydivid' style='display: black;margin:8px 0 0 10px;'>" +
								"申请天数 <input id='date_count' onchange='empcal_me.setEndDate(\""+tableid+"\");' type='text' class='hj-zm-cj-xqbm' style='text-align:right;width:90px;' maxlength='2' size=8 value='1' />（天） " +
							"</div>" +
							"<div id='hourdivid' style='display: none;margin:8px 0 0 10px;'>" +
								"<div style=''>" + 
									"起始时间<div id='strtime' style='margin:-19px 0 0 66px;' onchange='empcal_me.setEndDate(\""+tableid+"\");'></div>" +
								"</div>" +
								"<div style='margin:8px 0 0 0;'>" +
									"申请时长 <input id='time_count' onchange='empcal_me.setEndDate(\""+tableid+"\");' type='text' class='hj-zm-cj-xqbm' style='text-align:right;width:90px;' maxlength='2' size=8 value='1' /> 小时 " +
								"</div>" +
							"</div>" +
//							"</div>" +
							
							 "<div  id='endtimepul' style='margin:10px 0 0 10px;display:none;'>" +	 
							 	"结束日期 " +
								 "<input name='endtime'  type='text' id='endtime' class='hj-zm-cj-xqbm'  style='width:130px' value='"+endDateValue+"' />"+
								 "<img id='endtimeimg' class='img-middle' style='margin-left:-1px;height:24px;'"+
								 "plugin='datetimeselector' inputname='endtime' src='/module/recruitment/image/TIME.bmp' format='Y-m-d H:i' >"+
							"</div>" +
							
							"<div  id='endtimepul1' style='margin:10px 0 0 10px;display:black;'>" +	 
							 	"结束日期 " +
								 "<input name='endtime1'  type='text' id='endtime1' class='hj-zm-cj-xqbm'  style='width:130px' value='' />"+
								 "<img id='endtime1img' class='img-middle' style='margin-left:-1px;height:24px;'"+
								 "plugin='datetimeselector' inputname='endtime1' src='/module/recruitment/image/TIME.bmp' format='Y-m-d' >"+
							"</div>" +
							"<div id='classdivid' style='margin:6px 0 0 10px;display:"+classdiv+";'>参考班次 "+classSelecthtml+"</div>" +
							"<div id='iftoRestid' style='margin:6px 0 0 10px;display:"+restdiv+";'>是否调休 "+restSelecthtml+"</div>" +
							"<div style='margin:15px 0 0 10px;'>" +
								tableInfo+"事由 <textarea id='xreason'  style='vertical-align:top;border:1px #c5c5c5 solid; color:#333;resize:none;' rows='6' cols='42' ></textarea>" +//"+qxobj.xreason+"
							"</div>" +
						"</div>"
			        }
				,{
					xtype: 'panel',
					height: 30,
					width: 500,
					border: false,
					margin : '6 0 0 0',
					items:[{
							xtype:'button',
							margin : '0 0 0 190',
							text:"报批",
							handler : function() {
								
								if(Ext.isEmpty(tableid))
									return;
								// 申请类型
								var sels = document.getElementById("typeid").value;
								if(Ext.isEmpty(sels)){
									Ext.showAlert("申请类型不能为空！");
									return;
								}
									
								// 报批之前需再次校验结束日期是否正确
								var messg = empcal_me.setEndDate(tableid, "1");
								if(!Ext.isEmpty(messg))
									return;
								
								var map = new HashMap();
								map.put("table", tableid);
								map.put("sels", sels);
								var rdo1 = document.getElementById('rdo1').checked;
								var rdo2 = document.getElementById('rdo2').checked;
								var rdo3 = document.getElementById('rdo3').checked;
								var app_way = "0";
								var start_time = document.getElementById("startime1").value;
								var end_time = document.getElementById("endtime1").value;
								if(rdo2)app_way = "1";
								else if(rdo3){
									app_way = "2";
									start_time = document.getElementById("startime").value;
									end_time = document.getElementById("endtime").value;
								}
								// 校验日期
								if(end_time<=start_time){
									Ext.showAlert("结束日期早于或等于起始日期，请重新设置！");
									return;
								}
								map.put("app_way", app_way);
								map.put("start_d", start_time);
								map.put("end_d", end_time);
								var times = Ext.getCmp('time_field').getValue();
								var timeFormat = Ext.Date.format(times,'H:i');
								map.put("start_time_h", timeFormat.split(':')[0]);
								map.put("start_time_m", timeFormat.split(':')[1]);
								if(tableid == "Q11"){
									var classid = document.getElementById("classid").value;
									if(Ext.isEmpty(classid) && rdo1){
										Ext.showAlert("参考班次不能为空！");
										return;
									}
									map.put("class_id", classid);
									// 是否调休IftoRest
									if("1" == empcal_me.itemsMap.isExistIftoRest){
										var restValue = document.getElementById("restSelectid").value;
										if(Ext.isEmpty(restValue)){
											Ext.showAlert("请选择是否调休！");
											return;
										}
										map.put("IftoRest", restValue);
									}
								}
								
								map.put("scope_start_time", start_time);
								map.put("scope_end_time", end_time);
								// 报批
								map.put("sub_flag", "02");
								var reason = document.getElementById('xreason').value;
								if(Ext.isEmpty(reason)){
									Ext.showAlert("申请原因不能为空！");
									return;
								}
								map.put("app_reason", reason);
								// 加班原因代码项
//								map.put("appReaCodesetid", "");
								
								Rpc({functionId:'1510020019',async:false,success:function(form){
									var result = Ext.decode(form.responseText);
									if(result.succeed){
										Ext.showAlert("报批成功！");
										appWin.close();
										empcal_me.refreshAppData(tableid);
									}else{
										Ext.showAlert(result.message);
										return;
									}
							      },scope:this},map);
							}
						},{
							xtype:'button',
							margin : '0 0 0 20',
							text:"取消",
							handler : function() {
								appWin.close();
							}
						}]
				}
			],
			listeners:{
				render:function(){
					// 重新加载时间控件
					empcal_me.datetimePull();
				}
			}
		});
		appWin.show();
		
		createTimeField('time_field', 'strtime', "");
		
	},
	/**
	 * 报批单据后 回调函数
	 */
	
	refreshAppData:function(tableid){
		var map = new HashMap();
		map.put("flag", "applyData");
		map.put("self","self");
		map.put("startday", empcal_me.datelist[0].date);
		map.put("endday", empcal_me.datelist[empcal_me.datelist.length-1].date);
		
		Rpc({functionId:'KQ10000001',async:false,success:function(form){
			var result = Ext.decode(form.responseText);
			
			empcal_me.leaveApps = result.applyData.leaveApps;//请假单据
			Ext.getCmp('leaveid').removeAll();
			Ext.getCmp('leaveid').add(empcal_me.leavePanel());
			empcal_me.officeleaveApps = result.applyData.officeleaveApps;//公出单据
			Ext.getCmp('officeleaveid').removeAll();
			Ext.getCmp('officeleaveid').add(empcal_me.officeleavePanel());
			empcal_me.overtimeApps = result.applyData.overApps;//加班单据
			Ext.getCmp('overtimeid').removeAll();
			Ext.getCmp('overtimeid').add(empcal_me.overtimePanel());
			
	      },scope:this},map);
	},
	//提交完之后关闭申请单窗口
	viewClose : function (){
		empcal_me.view.close();
		empcal_me.getClassCardtime(empcal_me.oneday);
	},
	//提交完之后关闭补刷卡窗口
	cardClose : function (){
		empcal_me.cardWin.close();
		empcal_me.getClassCardtime(empcal_me.oneday);
	},
	//有申请模板时
	fill_out:function(tabid, other_param, xspz5, qxjnum){
		// 已封存考勤期间直接返回
		if(empcal_me.barflag == 0)
			return;
		var task_id = "0";
		// 销假被驳回时校验task_id 若存在于流程中 仍走原来的人事异动业务流程
		if("07" == xspz5){
			var map = new HashMap();
			map.put("flag","rejectAppData");
			map.put("self","self");
			map.put("tabId", tabid+"");
			map.put("rejectNum", qxjnum);
			Rpc({functionId:'KQ10000001',async:false,success:function(form){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					task_id = result.task_id;
				}
			},scope:this},map);
		}
		// 销假驳回后，再次报批走不同的模板时给出提示
		if("07" == xspz5 && "0" == task_id){
			Ext.showAlert("当前时段的销假在别的表单已被驳回，不能发起新的销假申请！");
			return;
		}
		
		var win = Ext.getCmp('winTemp');
        if(win)
            win.close();
		if(empcal_me.version == 0){
			
			if(empcal_me.cardWins)
				empcal_me.cardWins.close();
			
			var templateBean = new Object();
			templateBean.sys_type="1";
			templateBean.tab_id=tabid+"";
			templateBean.return_flag="8";
			templateBean.module_id="9";
			templateBean.approve_flag="1";
			templateBean.task_id=task_id;
	        templateBean.card_view_type="1";
		    templateBean.view_type="card";
			templateBean.callBack_init="empcal_me.tempFunc";
			templateBean.callBack_close="empcal_me.goBack";
			templateBean.other_param=other_param;
			// 调用人事异动模板 
			createTemplateForm(templateBean);
		}else{
			//旧
			window.open("/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=12&tabid="+tabid);
		}
		
	},

	tempFunc : function (){
		Ext.getCmp("mainPanel").removeAll(false);
		Ext.getCmp("mainPanel").add(templateMain_me.mainPanel);
	},
	
	goBack:function(){
		Ext.getCmp("mainPanel").removeAll(false);
		Ext.getCmp("mainPanel").add(empcal_me.filterPanel);
		empcal_me.refreshAppData("");
	},
	
	// 新补刷卡窗口
	cardWindow : function(classtime){
	   
		if(Ext.isEmpty(classtime) || 'undefined'==classtime || 'null'==classtime){
	       classtime = empcal_me.oneday+" 09:00";
		}
		// new cardm
		var cardWin = Ext.getCmp('cardWinid');
	    if(cardWin)
	    	cardWin.close();
		
		cardWin = Ext.create('Ext.window.Window', {
			id : 'cardWinid',
			modal : true,
			title: "补签",
			height: 300,
			width: 500,
			border: false,
			layout:{
				type:'vbox',
				align:'left'
			},
			items: [{
					xtype: 'panel',
					height: 210,
					width: 500,
					border: false,
					html  : "<div align='center' style='height:210px;width:400px;border:solid 0px #78C5FF;margin:0 0 0 0;'>" +
							"<table border='0' align='left' style='margin:10px 0 0 20px;font-size: 15px;' >" +
								"<tr height='30px'><td style='text-align: right;'>日期</td><td width='10px'></td>" +
									"<td style='text-align: left;'>"+
										"<input name='carddate'  type='text' id='carddate' class='hj-zm-cj-xqbm' style='width:100px' value='"+empcal_me.oneday.replace(/\./g,"-")+"' />"+
										"<img id='carddateimg' class='img-middle' style='margin-left:-1px;height:24px;'"+
										"plugin='datetimeselector' inputname='carddate' src='/module/recruitment/image/TIME.bmp' format='Y-m-d' />" +
									"</td>" +
								"</tr>"+
								"<tr height='40px'><td style='text-align: right;'>时间</td><td width='10px'></td><td><div id='cardtime' ></div></td></tr>"+
								"<tr><td>补刷原因</td><td width='10px'></td><td><textarea id='cardreason'  style='vertical-align:top;border:1px #c5c5c5 solid; color:#333;resize:none;' rows='6' cols='38' ></textarea></td></tr>"+
							"</table>"+
							"</div>"
			        }
				,{
					xtype: 'panel',
					height: 30,
					width: 500,
					border: false,
					margin : '16 0 0 0',
					items:[{
							xtype:'button',
							margin : '0 0 0 190',
							text:"报批",
							handler : function() {
								var oper_cause = document.getElementById('cardreason').value;
								if(oper_cause.length > 250){
									Ext.showAlert("补刷卡原因字数不能超过250个字符！");
									return;
								}
								var times = Ext.getCmp('cardtime_field').getValue();
								var timeFormat = Ext.Date.format(times,'H:i');
								
								var map = new HashMap();
//								map.put("app_account",getEncodeStr(app_account));
								map.put("ip_adr","");
								map.put("z5","02");
								map.put("oper_cause",getEncodeStr(oper_cause));
								map.put("inout_flag","0");
								map.put("makeup_date", document.getElementById('carddate').value);
								map.put("makeup_time", timeFormat);
								
								Rpc({functionId:'15502110211',async:false,success:function(form){
									var result = Ext.decode(form.responseText);
									if(result.succeed)
										Ext.showAlert("报批成功！");
									else
										Ext.showAlert(result.message);
									cardWin.close();
									empcal_me.getClassCardtime(empcal_me.oneday);
							      },scope:this},map);
							}
						},{
							xtype:'button',
							margin : '0 0 0 20',
							text:"取消",
							handler : function() {
								cardWin.close();
							}
						}]
				}
			],
			listeners:{
				render:function(){
					// 重新加载时间控件
					empcal_me.datetimePull();
				}
			}
		});
		cardWin.show();
		// 为小时分钟组件赋值
		createTimeField('cardtime_field', 'cardtime', classtime.substring(11));
	},
	
	// 补刷卡
	creCard : function(obj, classtime){
		
		if(empcal_me.barflag==0 || empcal_me.privs.cardp==0)
			return;
		empcal_me.cardWins = Ext.create('Ext.window.Window', {
			modal : true,
			title: '补刷卡',
			height: 200,
			width: 300,
			border: false,
			layout:{
				type:'vbox',
				align:'left'
			},
			items: []
		});
		
		if(empcal_me.cardTemplates.length==0  && empcal_me.privs.cardp==0){
			Ext.showAlert("您没有补签权限！");
		}else if(empcal_me.cardTemplates.length==0  && empcal_me.privs.cardp==1){
			empcal_me.cardWindow(classtime);
		}else if(empcal_me.cardTemplates.length == 1){
			var cardstr = empcal_me.cardTemplates[0].temp.split(':')[0];
			var other = empcal_me.getOtherParams(empcal_me.cardTemplates[0].temp.split(':')[2]);
			empcal_me.fill_out(cardstr, other);
		}else {
			empcal_me.dropdownDatas("qcard");
		}
		
	},

	//获取时间差
	getLineNum : function(time1, time2){
		var dt1 = Ext.Date.parse(time1, "Y.m.d H:i");
		var d1 = dt1.getTime();
		
		var dt2 = Ext.Date.parse(time2, "Y.m.d H:i");
		var d2 = dt2.getTime();
		
		var minute = Math.ceil((d2-d1)/(1000*60*30))-1;//以30分钟为一个单位parseInt()
		return minute;
	},
	//提示信息
	mouseover: function(){
		if(empcal_me.barflag==0 || empcal_me.privs.cardp==0)
			return;
		var tip = Ext.create('Ext.tip.ToolTip', {
		    target: 'addcard',
		    html: '补刷卡'
		});
		return tip;
	},	
	// 拼接 时间轴
	timeline:function(){
		var timehtml = "";
			
		var normallen = "<div style='float:left;'><div style='height:0;width:20px;border:solid 1px #35baf6;'></div></div>";
		var normallen6 = "<div style='float:left;'><div style='height:0;width:6px;border:solid 1px #35baf6;'></div></div>";
		var phtOne = "<img title='" + kq.empcal.crecard + "' style='float:left;margin:-4px 0 0 0;cursor:pointer;' onclick='empcal_me.creCard(this,\"";
		var stratline = "<div style='float:left;' >";
		var classOne = "<div style='float:left;height:0;width:10px;border:solid 1px #35baf6;'></div>";
		var classTwo = "<div style='height:0;width:120px;border:solid 1px #35baf6;' ></div>";
		var classTh = "<div style='float:center;margin:20px 0 0 0;font-size: 12px;color: #3E3E3E;'>";
		var classPht1 = "<img title='" + kq.empcal.crecard + "' style='float:left;margin:-4px 0 0 0;cursor:pointer;' onclick='empcal_me.creCard(this,\"";
		var classPht2 = "\");' src='../../../kq/images/kq_class_time.png' />";
		var classinfoOne = "<div style='margin:20px 0 0 0;font-size:12px;color:#3e3e3e' >";
		var cardEqualsInfo = "<div style='float:left;margin:-50px 0 0 0;font-size: 12px;color: #35baf6;'>";
		var cardinfoOne = "<div style='float:left;margin:-50px 0 0 0;font-size:12px;color:#35baf6' >";
		var cardTh = "<div style='height:0;width:70px;border:solid 1px #35baf6;'></div>";
		var cardTh2 = "<div style='height:0;width:120px;border:solid 1px #35baf6;'></div>";
		var cardPht1 = "<img title='" + kq.empcal.crecard + "' style='float:left;margin:-2px 0 0 0;cursor:pointer;' onclick='empcal_me.creCard(this,\"";
		var cardPht2 = "\");' src='../../../kq/images/kq_card_time.png' />";
		var cardspot = "\");' src='../../../kq/images/kq_cardspot.png' />";
		
		var divend = "</div>";
		var brhtml = "<br>";
			
		var classlen = empcal_me.classinfo.length;
		var cardlen = empcal_me.cardToclass.length;
		
		var cardtime = "";//刷卡时间
		var onduty_start = "";//上班时间起
		var onduty = "";//上班
	    var offduty = "";//下班
	    var offduty_end = "";//下班时间止
	    
	    timehtml += normallen6+normallen6;
	  //------------------------------------------------------------------------------------------------------------------
	    
	    var cardlist = new Array();
	    for(var i=0;i<cardlen;i++){
	    	cardtime = empcal_me.cardToclass[i].card;
	    	var carddate = empcal_me.cardToclass[i].cardDate;
	    	var cardmap =  new HashMap();
	    	if(!Ext.isEmpty(carddate) && !Ext.isEmpty(cardtime)){
	    		cardtime = carddate+" "+cardtime;
	    		cardmap.put("cardm",cardtime);
	    		cardmap.put("info",getKqCardTimeInfo(empcal_me.cardToclass[i].info));
	    		cardmap.put("spinfo",!Ext.isEmpty(empcal_me.cardToclass[i].spinfo)?"（"+empcal_me.cardToclass[i].spinfo+"）":"");
				cardlist.push(cardmap);
	    	}
	    }
	    
	    var listall = new Array();
	
	    for(var j=0;j<classlen;j++){
	    	var classmap =  new HashMap();
	    	var classt = empcal_me.classinfo[j].classTime;
	    	if(!Ext.isEmpty(classt)){
		    	classmap.put("classm", classt);
		    	classmap.put("info", empcal_me.classinfo[j].info);
		    	listall.push(classmap);
	    	}
	    }
	    for(var i=0;i<cardlist.length;i++){
	    	listall.push(cardlist[i]);
	    }
	    // 排序
	    var temp;  
	    for(var i=0;i<listall.length-1;i++){
	    	for(var j=0;j<listall.length-1-i;j++){
	    		var cardl = listall[j].get("cardm")==null?listall[j].get("classm"):listall[j].get("cardm");
	    		var cardh = listall[j+1].get("cardm")==null?listall[j+1].get("classm"):listall[j+1].get("cardm");
	    		if(cardl > cardh){
	    			temp = listall[j];
		            listall[j] = listall[j+1];
		            listall[j+1]=temp;
	    		}
	    	}
	    }
	    // 休息并无刷卡 不显示时间轴
		if(listall.length == 0){
			timehtml = "<p  style='font-size: 25px;color:#979797;margin:0 0 0 120px;'>当日无刷卡数据</p>";
			return timehtml;
		}
		// 未排班但是有刷卡信息时，时间轴只显示刷卡信息及审批状态
		if(classlen==0 && cardlen>0){
			var time0 = empcal_me.oneday + " 00:00";
			
			timehtml += normallen6;
			timehtml += stratline+classOne+phtOne+time0+classPht2+classTwo+classinfoOne+"00:00"+brhtml+divend+divend;
			for(var i=0;i<10;i++){
				timehtml += normallen6;
			}
			for(var j=0;j<listall.length;j++){
				
				if(listall[j].get("cardm") != null){
					
					var cardtime = listall[j].get("cardm");
					var cardinfo = listall[j].get("info");
					var cardspinfo = listall[j].get("spinfo");
					timehtml += normallen6;
					timehtml += stratline+cardinfoOne+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend+cardPht1+cardtime+cardPht2+cardTh+divend;
					
					if(listall.length>j+1){
						if(listall[j+1].get("cardm") != null){
							var cardtime1 = listall[j+1].get("cardm");
							var num = empcal_me.getLineNum(cardtime, cardtime1);
							num = num>5?5:num;
							for(var i=0;i<num;i++){
								timehtml += normallen6;
							}
						}
					}
				}
		    }
			var time9 = empcal_me.oneday + " 24:00";
			for(var i=0;i<10;i++){
				timehtml += normallen6;
			}
			timehtml += stratline+classOne+phtOne+time9+classPht2+classTwo+classinfoOne+"24:00"+brhtml+divend+divend;
			return timehtml;
		}
		
		var classtime = "";
		var classInfo = "";
		// 有刷卡数据时
		if(cardlist.length > 0){
			var bool = true;
			// 防止无用的刷卡数据过多 这里合并显示
			var infomsgs = "";
			
			var equalshtml1 = "";
			var equalshtml2 = "";
			var equalstime = "";
			var equalsinfo = "";
			
			for(var j=0;j<listall.length;j++){
				var cardinfo = "";
				var nextinfo = "";
				var cardspinfo = "";
				var nextspinfo = "";
				var next = "";
				var num = 0;
				var addline = "";
				// 本次时间点与上次相同时，判断是否后面还有时间点，若有添加相应时间轴长度
				if(!bool){
					if(listall[j].get("cardm") == null){
//						console.log("class  equales --class+++++++++++++++++++++++-");
	//					classtime = listall[j].get("classm");
	//					timehtml += outerone+content+timeline+divend+divend;
	//					timehtml += equalshtml1+equalstime.substring(11)+equalshtml2;
	//					if(listall.length>j+1){
	//						if(listall[j+1].get("cardm") == null){
	//							next = listall[j+1].get("classm");
	//							num = empcal_me.getLineNum(classtime, next);
	//							num = num>5?5:num;
	//							for(var i=0;i<num;i++){
	//								timehtml += outertwo+content+timeline+divend+divend;
	//							}
	//						}else{
	//							next = listall[j+1].get("cardm");
	//							num = empcal_me.getLineNum(classtime, next);
	//							num = num>5?5:num;
	//							for(var i=0;i<num;i++){
	//								timehtml += outertwo+content+timeline+divend+divend;
	//							}
	//						}
	//					}
					}else{
						cardtime = listall[j].get("cardm");
						cardinfo = listall[j].get("info");
						cardspinfo = listall[j].get("spinfo");
						
						if(listall.length>j+1){
							if(listall[j+1].get("cardm") == null){
								timehtml += normallen6;
								timehtml += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend
											+phtOne+cardtime+cardspot
											+equalshtml1+divend;
								next = listall[j+1].get("classm");
								num = empcal_me.getLineNum(cardtime, next);
								num = num>5?5:num;
								for(var i=0;i<num;i++){
									timehtml += normallen6;
								}
							}else{
								next = listall[j+1].get("cardm");
								nextinfo = listall[j+1].get("info");
								nextcardspinfo = listall[j+1].get("spinfo");
								if(equalsinfo.length > 0){
									timehtml += normallen6;
									timehtml += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend
												+phtOne+cardtime+cardspot
												+equalshtml1+divend;
								}else if(equalsinfo.length == 0 && (nextinfo.length > 0 || nextcardspinfo.length > 0)){
									timehtml += normallen6;
									timehtml += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend
												+phtOne+cardtime+cardspot
												+equalshtml1+divend;
								}// 59435 合并已批的无效的刷卡数据
								else if(equalsinfo.length == 0 && nextinfo.length == 0
										&& cardspinfo.length == 0 && nextcardspinfo.length == 0){
	//								timehtml += outerlow+content+class_time+time+equalstime+" &nbsp;&nbsp;&nbsp;"+divend+timeline+divend+divend;
									infomsgs += equalstime.substring(11)+",";
									bool = true;
									continue;
								}
								
								num = empcal_me.getLineNum(cardtime, next);
								num = num>5?5:num;
								for(var i=0;i<num;i++){
									timehtml += normallen6;
								}
							}
						}else{
							// 若班次时间与刷卡时间相等时为最后结束段
							timehtml += normallen6;
							timehtml += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend
										+phtOne+cardtime+cardspot
										+equalshtml1+divend;
						}
						
					}
					equalshtml1 = "";
					equalshtml2 = "";
					equalstime = "";
					equalsinfo = "";
					bool = true;
					continue;
				}
	
				if(listall[j].get("cardm") == null){
					classtime = listall[j].get("classm");
					classInfo = listall[j].get("info");
					addline += stratline+classOne+phtOne+classtime+classPht2+classTwo+classinfoOne+classtime.substring(11)+brhtml+classInfo+divend+divend;
					
					if(listall.length>j+1){
						if(listall[j+1].get("cardm") == null){
							next = listall[j+1].get("classm");
							num = empcal_me.getLineNum(classtime, next);
							num = num>5?5:num;
							for(var i=0;i<num;i++){
								addline += normallen6;
							}
						}else{
							next = listall[j+1].get("cardm");
							nextinfo = listall[j+1].get("info");
							nextspinfo = listall[j+1].get("spinfo");
							equalsinfo = nextspinfo+listall[j+1].get("info");
							num = empcal_me.getLineNum(classtime, next);
							num = num>5?5:num;
							for(var i=0;i<num;i++){
								addline += normallen6;
							}
						}
					}
					if(num == -1){
						equalstime = classtime;//info+msg  cardinfo
//						equalshtml1 = outerlow+content+class_time1+classtime+class_time2+card_time1+classtime+card_time2+time+classInfo+classtime.substring(11)+"&nbsp;&nbsp;&nbsp;"+divend+timeline+info+msg+"&nbsp;&nbsp;";
						equalshtml1 = classTwo+classTh+classtime.substring(11)+brhtml+classInfo+divend;//stratline
						equalshtml2 = divend;
	
						bool = false;
					}else{
						bool = true;
						timehtml += addline;
						
					}
				}else{
					cardtime = listall[j].get("cardm");
					cardinfo = listall[j].get("info");
					cardspinfo = listall[j].get("spinfo");
					
					if(listall.length>j+1){
						if(listall[j+1].get("cardm") == null){
							if(cardinfo.length > 0 || cardspinfo.length > 0){
								addline += normallen6;
								addline += stratline+cardinfoOne+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend+cardPht1+cardtime+cardPht2+cardTh+divend;
							}else if(cardinfo.length == 0){
								
								infomsgs += cardtime.substring(11);
								addline += normallen6;
								var showmore = "";
								if(infomsgs.split(",").length > 3){
									showmore = infomsgs.split(",")[0]+","+infomsgs.split(",")[1]+","+infomsgs.split(",")[2]+"...";
									infomsgs = showmore;
								}
								if(equalstime.length > 0){
									// 59435 合并已批的无效的刷卡数据
//									addline += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend
									addline += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+infomsgs+divend
												+phtOne+cardtime+cardspot
												+equalshtml1+divend;
									equalshtml1 = "";
									equalshtml2 = "";
									equalstime = "";
									equalsinfo = "";
								}else{
//									addline += stratline+cardinfoOne+cardspinfo+brhtml+cardtime.substring(11)+divend+cardPht1+cardtime+cardPht2+cardTh+divend;
									// 59435 合并已批的无效的刷卡数据
									addline += stratline+cardinfoOne+cardspinfo+brhtml+infomsgs+divend+cardPht1+cardtime+cardPht2;
									if(infomsgs.split(",").length > 2){
										addline += cardTh2;
									}else{
										addline += cardTh;
									}
									addline += divend;
								}
							}
							next = listall[j+1].get("classm");
							var onecard = "";
							num = empcal_me.getLineNum(cardtime, next);
							num = num>5?5:num;
							for(var i=0;i<num-1;i++){
								addline += normallen6;
							}
							infomsgs = "";
						}else{
							next = listall[j+1].get("cardm");
							nextinfo = listall[j+1].get("info");
							nextspinfo = listall[j+1].get("spinfo");
							if(cardinfo.length > 0 || cardspinfo.length > 0 ){
								addline += normallen6;
								addline += stratline+cardinfoOne+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend+cardPht1+cardtime+cardPht2+cardTh+divend;
							}else if(cardinfo.length == 0 && (nextinfo.length > 0 || nextspinfo.length > 0)){
								var showmore = "";
								infomsgs += cardtime.substring(11);
								addline += normallen6;
								if(infomsgs.split(",").length > 3){
									showmore = infomsgs.split(",")[0]+","+infomsgs.split(",")[1]+","+infomsgs.split(",")[2]+"...";
									infomsgs = showmore;
								}
								if(equalstime.length > 0){
//									addline += equalshtml1+infomsgs+equalshtml2;
//									addline += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+cardtime.substring(11)+divend
									// 59435 合并已批的无效的刷卡数据
									addline += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+infomsgs+divend
												+phtOne+cardtime+cardspot
												+equalshtml1+divend;
									equalshtml1 = "";
									equalshtml2 = "";
									equalstime = "";
									equalsinfo = "";
								}else{
									// 59435 合并已批的无效的刷卡数据
									addline += stratline+cardinfoOne+cardspinfo+brhtml+infomsgs+divend+cardPht1+cardtime+cardPht2;
									if(infomsgs.split(",").length > 2){
										addline += cardTh2;
									}else{
										addline += cardTh;
									}
									addline += divend;
								}
								infomsgs = "";
							}else if(cardinfo.length == 0 && nextinfo.length == 0){
								if(cardspinfo.length == 0 && nextspinfo.length == 0){
									// 59435 合并已批的无效的刷卡数据
									infomsgs += cardtime.substring(11)+",";
									continue;
								}else{
									var showmore = "";
									infomsgs += cardtime.substring(11);
									addline += normallen6;
									if(infomsgs.split(",").length > 3){
										showmore = infomsgs.split(",")[0]+","+infomsgs.split(",")[1]+","+infomsgs.split(",")[2]+"...";
										infomsgs = showmore;
									}
									if(equalstime.length > 0){
										// 59435 合并已批的无效的刷卡数据
										addline += stratline+cardEqualsInfo+cardinfo+cardspinfo+brhtml+infomsgs+divend
													+phtOne+cardtime+cardspot
													+equalshtml1+divend;
										equalshtml1 = "";
										equalshtml2 = "";
										equalstime = "";
										equalsinfo = "";
									}else{
										// 59435 合并已批的无效的刷卡数据
										addline += stratline+cardinfoOne+cardspinfo+brhtml+infomsgs+divend+cardPht1+cardtime+cardPht2;
										if(infomsgs.split(",").length > 2){
											addline += cardTh2;
										}else{
											addline += cardTh;
										}
										addline += divend;
									}
									infomsgs = "";
								}
							}
							num = empcal_me.getLineNum(cardtime, next);
							num = num>5?5:num;
							for(var i=0;i<num;i++){
								addline += normallen6;
							}
						}
					}
					
					if(num == -1){
						bool = false;
						timehtml += outerlow+content+class_time+card_time1+cardtime+card_time2+time+cardtime.substring(11)+"&nbsp;&nbsp;&nbsp;"+divend+timeline+info+msg+"&nbsp;&nbsp;"+cardtime.substring(11)+"&nbsp;&nbsp;&nbsp;"+cardinfo+divend+divend+divend+divend;
					}else{
						bool = true;
						timehtml += addline;
					}
				}
			}
		}else{//没有刷卡数据时
			//按班次时间比例显示
			for(var j=0;j<listall.length;j++){
				var next = "";
				var num = 0;
				var addline = "";
				classtime = listall[j].get("classm");
				classInfo = listall[j].get("info");
	//			addline += outerlow+content+class_time1+classtime+class_time2+time+classInfo+classtime.substring(11)+" &nbsp;&nbsp;&nbsp;"+divend+timeline+divend+divend;
				addline += stratline+classOne+phtOne+classtime+classPht2+classTwo+classTh+classtime.substring(11)+brhtml+classInfo+divend+divend;
				if(listall.length>j+1){
						next = listall[j+1].get("classm");
						num = empcal_me.getLineNum(classtime, next);
						num = num>5?5:num;
						for(var i=0;i<num;i++){
							addline += normallen;
						}
				}
				timehtml += addline;
			}
				
		}
		return timehtml;
	},
	
	//获取考勤期间第一天是星期几
	getFirstDay:function(date){
		var dt = new Date(date.replace(/-|\./g,"/"));
		var dayNum = dt.getDay();
		return dayNum;
	},
	
	//查看某日的考勤情况
	getClassCardtime: function(datevalue){
		empcal_me.oneday = datevalue;
		var map = new HashMap();
	      map.put("datevalue",datevalue);
	      map.put("flag","detailinfo,applyData");
	      map.put("nowDuration",empcal_me.nowDuration);
	      map.put("self","self");
	      map.put("startday", empcal_me.datelist[0].date);
	      map.put("endday", empcal_me.datelist[empcal_me.datelist.length-1].date);
	      
	      Rpc({functionId:'KQ10000001',async:false,success:empcal_me.loadTimeLine,scope:this},map);
	},
	
	loadTimeLine : function(form){
		var result = Ext.decode(form.responseText);
		empcal_me.classinfo = result.classinfo;
//		empcal_me.cardtime = result.cardtime;
		empcal_me.classname = result.classname;
		empcal_me.cardToclass = result.cardToclass;
		empcal_me.barflag = result.barflag;
		
		Ext.getCmp('timeline').setHtml(empcal_me.timeline());
		Ext.getCmp('calview').setHtml(empcal_me.calview());
		
		var year = empcal_me.oneday.substring(0, 4);
		var month = empcal_me.oneday.substring(5, 7);
		var day = empcal_me.oneday.substring(8);
		
		empcal_me.itemsMap = result.itemsMap;
		empcal_me.onduty = Ext.isEmpty(empcal_me.itemsMap.onduty)?"00:00":empcal_me.itemsMap.onduty;
		empcal_me.offduty = Ext.isEmpty(empcal_me.itemsMap.offduty)?"00:00":empcal_me.itemsMap.offduty;
    	empcal_me.offdate = empcal_me.oneday;  
    	if(empcal_me.onduty>empcal_me.offduty){
    		var dt = new Date(empcal_me.oneday.replace(/-|\./g,"/"));
    		var dateOff = Ext.Date.add(dt, Ext.Date.DAY, +1);   
    		empcal_me.offdate = Ext.Date.format(dateOff, 'Y.m.d');
    	}
		
		//选中的日期改变下标示  border_bottom
		if(document.getElementById(empcal_me.oneday))
		      document.getElementById(empcal_me.oneday).setAttribute("class", "border_bottom");
		
		Ext.getCmp('dateclassTitle').setHtml(empcal_me.dateclassTitle(year, month, day, empcal_me.classname.name));
		
		// 由于对应模板参数对应所选的日期要更新 故重新加载单据，更新日期参数
		empcal_me.leaveApps = result.applyData.leaveApps;//请假单据
		Ext.getCmp('leaveid').removeAll();
		Ext.getCmp('leaveid').add(empcal_me.leavePanel());
		empcal_me.officeleaveApps = result.applyData.officeleaveApps;//公出单据
		Ext.getCmp('officeleaveid').removeAll();
		Ext.getCmp('officeleaveid').add(empcal_me.officeleavePanel());
		empcal_me.overtimeApps = result.applyData.overApps;//加班单据
		Ext.getCmp('overtimeid').removeAll();
		Ext.getCmp('overtimeid').add(empcal_me.overtimePanel());
		
	},
	/**
	 * 页面title 考勤期间切换
	 */
	calviewTitle:function(){
		var calTitlehtml = "<p align='left' style='font-size: 22px;margin:5px 0 0 0;' >" +//&nbsp;&nbsp;
								"<a style='cursor:pointer;font-size: 22px;color:#979797;' onclick='empcal_me.getSessiondata(0);'> < </a>" +
								"<span id='nowDuration' style='font-size: 22px;color:#FF4474;'>"+empcal_me.nowDuration+"</span>" +
								"<a style='cursor:pointer;font-size: 22px;color:#979797;' onclick='empcal_me.getSessiondata(1);'> > </a>" +
							"</p>";
		return calTitlehtml;
	},
	calviewTail:function(){
		var calTailhtml = "";
		calTailhtml += "<div align='center' style='width:100%;' >" +
					"<table width='50%' border='0' cellspacing='0'  align='center' cellpadding='0' style='margin:10px 0 0 0;font-size: 16px;color:#979797;'>" +
					"<tr align='center' >" +
					"<td><img alt='' src='../../../kq/images/kq_normal_small.png'>&nbsp;&nbsp;正常</td>" +
					"<td><img alt='' src='../../../kq/images/kq_except_small.png'>&nbsp;&nbsp;异常</td>" +
					"<td><img alt='' src='../../../kq/images/kq_lt_samll.png'>&nbsp;&nbsp;请假公出</td>" +
					"<td><img alt='' src='../../../kq/images/kq_ot_small.png'>&nbsp;&nbsp;加班</td>" +
					"</tr></table>";
		calTailhtml += "</div>";
		return calTailhtml;
	},
	//考勤日历
	calview:function(){
		//min-width='100px' min-height='100px'  <thead> </thead>
		
		var calhtml = "";
		calhtml += "<div align='center' style='width:100%;' >" +
//				"<p align='center' style='font-size: 22px;margin:20px 0 0 0;' ><a style='cursor:pointer;font-size: 22px;color:#979797;' onclick='empcal_me.getSessiondata(0);'> < </a>" +
//				"<span id='nowDuration' style='font-size: 22px;color:#FF4474;'>"+empcal_me.nowDuration+"</span>" +
//						"<a style='cursor:pointer;font-size: 22px;color:#979797;' onclick='empcal_me.getSessiondata(1);'> > </a></p>" +
						"";
		calhtml += "<table  border='0' cellspacing='0'  align='center' cellpadding='0'  class='ListTable'>";
		calhtml += "<tr> ";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >日</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >一</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >二</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >三</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >四</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >五</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >六</td>";
		calhtml += " </tr> ";

		var curDate = new Date();
		var time=Ext.Date.format(curDate, 'Y.m.d');
		var theRows = parseInt(empcal_me.datelist.length / 7);
		var mod = empcal_me.datelist.length % 7;
        if (mod > 0) {
            theRows = theRows + 1;
        }
        var theFirstDay = empcal_me.getFirstDay(empcal_me.datelist[0].date);
        var theMonthLen = theFirstDay + empcal_me.datelist.length;
        if (7 - theFirstDay < mod)
            theRows = theRows + 1;
        if(empcal_me.datelist.length==28 && theFirstDay!=0)
        	theRows = theRows + 1;
        
        var n = 0;
        var day = 0;
        var day_str = "";
        var day_state = "";
        for (var i = 0; i < theRows; i++) {
        	calhtml += "<tr>";
            for (var j = 0; j < 7; j++) {
                n++;
                if (n > theFirstDay && n <= theMonthLen) {
                    day = n - theFirstDay - 1;
                    var date = empcal_me.datelist[day].date;
                    day_str = date.substring(8);
                    if(empcal_me.dailyInfo[day]){
                    	day_state = empcal_me.dailyInfo[day].state;
                    }
                    
                    // 38745 在当前日期之后的出勤情况 只显示与请假公出有关的出勤状态 并显示为请假公出的颜色状态
                    if(date > time){
                    	if(day_state=="lt" || day_state=="ltotexp" || day_state=="ltotnor" || day_state=="ltot" || day_state=="ltexp" || day_state=="ltnor")
                    		calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    	else
                    		calhtml += " <td  align='center'  class='TableRow' style='font-size:15px;color:#6F6F6F;'  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }
                    else if(date == time){
                    	calhtml += " <td align='center'  class='TableRow'   style='font-size:15px;color:#6F6F6F;background:url(../../../kq/images/kq_curdate.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "normal"){
                    	calhtml += " <td align='center'  class='TableRow'   style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_normal.png) no-repeat;background-position:center; ' ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "except"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_except.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "lt"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "ot"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_ot.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "exceptot"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_except_ot.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "ltot"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt_ot.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "normalot"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_normal_ot.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "ltotexp"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt_ot_ex.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "ltotnor"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt_ot_nor.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "ltexp"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt_exc.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "ltnor"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#FFFFFF;background:url(../../../kq/images/kq_lt_nor.png) no-repeat;background-position:center; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else if(day_state == "rest"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#6F6F6F; '  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }else{
                    	calhtml += " <td  align='center'  class='TableRow' style='font-size:15px;color:#6F6F6F;'  ><div  style='position: relative;margin:15px 0 0 0;'><a style='cursor:pointer;' onclick='empcal_me.getClassCardtime(\""+date+"\");' >"+day_str+" </a><div id='"+date+"' style='position: relative;width:30px;height:15px;border-bottom:solid 2px #FFFFFF;'></div></div></td>";
                    }
                } else {
                	calhtml += " <td align='center'  class='TableRow' style='border-left:none;'  > </td> ";
                }
            }
            calhtml += "</tr>";
        }
		
		calhtml += "</table>";
//		calhtml += "<br>";
		calhtml += "<table width='90%' border='0' cellspacing='0'  align='center' cellpadding='0' style='margin:10px 0 0 0;font-size: 16px;color:#979797;'><tr align='center' >" +
				"<td><img alt='' src='../../../kq/images/kq_normal_small.png'>&nbsp;&nbsp;正常</td>" +
				"<td><img alt='' src='../../../kq/images/kq_except_small.png'>&nbsp;&nbsp;异常</td>" +
				"<td><img alt='' src='../../../kq/images/kq_lt_samll.png'>&nbsp;&nbsp;请假公出</td>" +
				"<td><img alt='' src='../../../kq/images/kq_ot_small.png'>&nbsp;&nbsp;加班</td>" +
				"</tr></table>";
		
		calhtml += "</div>";
		
		return calhtml;
	},
	//本月出勤饼状图 汇总数据
	createPieChart:function(){	
		
	var except = empcal_me.sumjson.be_late+empcal_me.sumjson.leave_early+empcal_me.sumjson.absent;
	var leave_out = empcal_me.sumjson.leave+empcal_me.sumjson.office_leave;
	
	var allNum = Number(empcal_me.sumjson.normal) + Number(except) + Number(leave_out) + Number(empcal_me.sumjson.overtime);
			
	if(allNum == 0){//没有汇总数据时
		var nodate = "<div align='center'><table  height='200px' border='0' cellspacing='0'  align='center' cellpadding='0' style='font-size: 25px;color:#979797;'>" +
					"<tr><td>本月暂无汇总数据</td></tr></table></div>"; 

		var panel = Ext.create("Ext.panel.Panel",{
			border: false,
			items:[
			       {
			    	   xtype: 'label',
			    	   layout:{
							align:'middle'
						},
					   html  : nodate
			       }]
		});
		return panel;
	}else{
		var normalto = Number(empcal_me.sumjson.normal)==0 ? 0 : empcal_me.sumjson.normal.toFixed(2);
		var excepto = Number(except)==0 ? 0 : except.toFixed(2);
		var leave_outo = Number(leave_out)==0 ? 0 : leave_out.toFixed(2);
		var overto = Number(empcal_me.sumjson.overtime)==0 ? 0 : empcal_me.sumjson.overtime.toFixed(2);
		
			var store = Ext.create("Ext.data.Store",{
				fields:['dataname','datavalue'],
				data:[]
//				data:[{'dataname':'正常', 'datavalue':empcal_me.sumjson.normal},{'dataname':'异常', 'datavalue':except},{'dataname':'请假公出', 'datavalue':leave_out},{'dataname':'加班', 'datavalue':empcal_me.sumjson.overtime}]
			});
//			var colors = ['#52D48C','#FF4474','#5FB5FF','#FFD04C'];  
			var colors = [];
			
			if(normalto != 0){
				store.add({'dataname':'正常', 'datavalue':normalto});colors.push('#52D48C');
			}
			if(excepto != 0){
				store.add({'dataname':'异常', 'datavalue':excepto});colors.push('#FF4474');
			}
			if(leave_outo != 0){	
				store.add({'dataname':'请假公出', 'datavalue':leave_outo});colors.push('#5FB5FF');
			}
			if(overto != 0){
				store.add({'dataname':'加班', 'datavalue':overto});colors.push('#FFD04C');
			}
			
			//饼状图
			var percent = 0;
			var pieChart = Ext.create("Ext.chart.PolarChart",{
				width:'65%',
				height:200,
				border: false,
				insetPadding:2,
				innerPadding:2,
				animation:true,
				store:store,
				colors : colors,
				style:{
					
				},
				interactions:['rotate'],
				series:[{
					type:'pie',
					angleField:'datavalue',
					tooltip:{
						trackMouse:true,
						shadow:false,
//						bodyStyle:"background-color:white;border:1px solid #c5c5c5;",Number( )
						renderer:function(tip, record){
						   var showType = Ext.getCmp('pie_panel').showType;
						   if(showType=="data"){
						   
							    tip.update(record.get("dataname")+"<br>值:"+record.get("datavalue"));
						   } else {
								var total = 0;
								store.each(function(rec){
									total += Number(rec.get('datavalue'));
								});
								percent = Math.round(record.get('datavalue')/total*100);
								tip.update(record.get('dataname')+"<br>占比:"+percent+"%");
							}
						}
					},/**/
					highlight:false
				}]
			});
			//本月汇总数据 //Math.round(except*480)+"分钟Math.round(empcal_me.sumjson.overtime*8)+"小时 a.toFixed(2)

			var summarydata = "<table width='150px' height='160px' border='0' cellspacing='0'  align='center' cellpadding='0' style='font-size: 16px;color:#979797;'>" +
						"<tr ><td style='font-size: 18px;'>本月考勤汇总</td></tr>" +
						"<tr ><td><img alt='' src='../../../kq/images/kq_normal_small.png'>&nbsp;&nbsp;正常"+normalto+"天</td></tr>" +
						"<tr><td><img alt='' src='../../../kq/images/kq_except_small.png'>&nbsp;&nbsp;异常"+excepto+"天</td></tr>" +
						"<tr><td><img alt='' src='../../../kq/images/kq_lt_samll.png'>&nbsp;&nbsp;请假公出"+leave_outo+"天</td></tr>" +
						"<tr><td><img alt='' src='../../../kq/images/kq_ot_small.png'>&nbsp;&nbsp;加班"+overto+"天</td></tr></table>"; 
	
			//有数据时
			var panel = Ext.create("Ext.panel.Panel",{
				id:'pie_panel',
				border: false,
				chartType:'pie',//统计图类型column、line、pie
				showChart:true,//是否显示统计图
				showType:'percentage',//=data提示显示数据，=percentage显示百分比
				layout:{
					type:'hbox',
					align:'center'
				},
				defaults:{
					margin:'20px 0 0 0'
				},
				items:[
				       pieChart,
				       {
				    	   xtype: 'label',
						   border: false,
						   html  : summarydata
				       }]
			});
			return panel;
		}
	},
	/**
	 * 重新加载时间控件
	 */
	datetimePull:function(){
		var imgEles = Ext.query('img[plugin=datetimeselector]');
		var selector = new DateTimeSelector();
		for(var i=0;i<imgEles.length;i++){
			 var ele       = imgEles[i],
			     inputName = ele.getAttribute("inputname"),
			     format    = ele.getAttribute("format"),
			     afterfunc = ele.getAttribute("afterfunc"),
			     spaceselect = ele.getAttribute("spaceselect"),
			     viewEles  = document.getElementsByName(inputName);
			     
			 if(viewEles.length<1)
				 continue;
			 ele.style.cursor='pointer';	 
			 //绑定鼠标点击事件
			 Ext.EventManager.addListener(ele,'click','showSelector',selector,[inputName,format,afterfunc,spaceselect,viewEles[0]]);
		 }
		 //初始化事件
		selector.initEvent(); 
	}
	
});