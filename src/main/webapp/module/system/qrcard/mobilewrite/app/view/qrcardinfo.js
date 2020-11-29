Ext.define("QRCard.view.qrcardinfo", {
	extend: 'Ext.Panel',
	id:'qrcardinfo',
	config: {
		autoScroll: true,
		bodyStyle: "overflow-x:hidden;overflow-y:hidden",
		height: '100%',
		width: '100%',
	},
	initialize: function() {
		var me = this;
		me.loadFlag = true;
		me.qrSaveValueData = new HashMap();//模板数据值
		me.qrSaveFieldData = new HashMap();//
		me.saveOrgindata = new HashMap();
		me.dataBlank = false;
		me.index = 0;
		me.subMap = new HashMap();//存储子集记录
		me.pageid = 0;//0表示第一页
		me.loadData();
		/*var panel = Ext.create('Ext.Panel',{
			margin:'300 0 0 0',
			height: '100%',
			width: '100%',
		})
		if((me.autoCirculation!="0"||me.sp=="0")&&me.personnelTransfer=="1"){
			flag = false;
			panel.setHtml('<div width="100%" style="text-align:center"><span  style="font-size:18px;">仅支持自动流转模式的审批模板，请联系管理员！</span></div>');
			me.removeAll(true);
			me.add(panel);
		}else
		if(me.personnelTransfer=="0"){
			flag = false;
			panel.setHtml('<div width="100%" style="text-align:center"><span  style="font-size:18px;">仅支持人员调入型模版，请联系管理员！</span></div>');
			me.removeAll(true);
			me.add(panel);
		}else
		if(me.dataBlank){
			flag = false;
			panel.setHtml('<div width="100%" style="text-align:center"><span  style="font-size:18px;">没有此模板数据，请联系系统管理员，指定模板的【提交时目标库】！</span></div>');
			me.removeAll(true);
			me.add(panel);
		}*/
		
		
	},
	//创建显示面板
	createFormPanel: function() {
		var me = this;
		var template = Ext.create('EHR.mobleTemplate.Template', {
			maxsize:me.maxsize/1024,
			org:me.config.org //模板权限的组织机构
		})
		template.setFormItem(me.qrData);
		me.add(template);
	},
	//加载页面数据
	loadData: function(type,templatePanel) {
		var me = this;
/*		var vo = new HashMap();
		vo.put("tabid",me.config.tabid+'');
		Rpc({
			functionId: 'SYS0000002010',
			success: function(form) {
				var result = Ext.decode(form.responseText);
				me.autoCirculation = result.autoCirculation;
				me.personnelTransfer = result.personnelTransfer;
				me.sp = result.sp;
			},
			async: false
		}, vo);
		if(me.autoCirculation!="0"||me.personnelTransfer=="0"){
			return;
		}*/

		var param = {};
		param.tabid = me.config.tabid+'';
		param.isEdit = '1';
		param.taskid = '0';
		param.ins_id = '0';
		param.fromMessage = '0';
//		param.object_id = 'Usr00000009';
		param.object_id = me.config.base+me.config.a0100;
		param.info_type = 'normal';
		param.page_no = me.pageid+'';
		var jsonStr = JSON.stringify(param);
//		var str = "{\"tabid\":\"" + me.config.tabid + "\",\"isEdit\":\"1\",\"taskid\":\"0\",\"ins_id\":\"0\",\"fromMessage\":\"0\",\"object_id\":\""+param.object_id+"\",\"info_type\":\"normal\",\"page_no\":\"" + me.pageid + "\"}";
		var map = new HashMap();
		map.put("param", jsonStr);
//		map.put("param", str);
		map.put('transType', 'apply');
		Rpc({
			functionId: 'SYS0000002006',
			success: function(form) {
				var result = Ext.decode(form.responseText);
				me.maxsize = result.maxsize;//照片最大大小  单位k
				if(result.flag=="false"&&result.msg!=""){
					var panel = Ext.create('Ext.Panel',{
					margin:'300 0 0 0',
					height: '100%',
					width: '100%',
					})
					panel.setHtml('<div width="100%" style="text-align:center"><span style="font-size:18px;">'+result.msg+'</span></div>');
					me.removeAll(true);
					me.add(panel);
					me.loadFlag = false;
					return;
				}
				if(!result.data){
					me.dataBlank = true;
					me.loadFlag = false;
					return;
				}
				me.orgindata = Ext.decode(result.data); //返回前端模板全部数据
				var map = new HashMap();
				map.put("data",me.orgindata);
				Rpc({
					functionId: 'SYS0000002008',
					success: function(form) {
						var result = Ext.decode(form.responseText);
						me.qrData =result.data;
						me.pageid= me.qrData.pageIndexList[0];
						if(type =='next'){
							me.pageid = me.qrData.pageIndexList[me.index]
						}
						if(!templatePanel){
							me.createFormPanel();
						}else{
							templatePanel.nextTempalateDataPanel();
						}
					}
				}, map);
			}
		}, map);
		
	}
});