/**
 * hej add 2015/12/9
 * 盒式报表
 */
Ext.define("BoxReportURL.BoxReport",{
	tableObj:undefined,
	viewport:undefined,
	constructor:function(config) {
		boxreport_me = this;
		this.init();
	},
	/**
	 * 初始加载页面
	 */
	init:function() {
	    Rpc({functionId:'ZJ100000111',async:false,success:this.getTableOK,scope:this},new HashMap());
	},
	/**
	 * 加载表单
	 */
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		boxreport_me.editflag = result.editflag;
		var obj = Ext.decode(conditions);
		boxreport_me.tableObj = new BuildTableObj(obj);
	},
	/**
	 * 编辑操作
	 * @param {} value
	 * @param {} meta
	 * @param {} record
	 * @return {}
	 */
	actionRender:function(value,meta,record){
		var html="";
		if(boxreport_me.editflag=='1'){
			html+="<a href='javascript:boxreport_me.editBoxreport(\""+record.get("box_id")+"\",1);'>编辑&nbsp;&nbsp;<a/>";
		}
		html+="<a href='javascript:boxreport_me.analysisBoxreport(\""+record.get("box_id")+"\",true);'>分析<a/>";
		return html;
    },
    /**
     * 点击名称显示详细
     * @param {} value
     * @param {} meta
     * @param {} record
     * @return {}
     */
    boxReportInfo:function(value,meta,record){
    	var name = record.get("name");
    	var html="<a href='javascript:boxreport_me.editBoxreport(\""+record.get("box_id")+"\",1);'>"+name+"<a/>&nbsp;&nbsp;&nbsp;";
    	return html;
    },
    /**
     * 编辑页面
     */
    editBoxreport:function(id,flag){
	    var configPanel = Ext.require('BoxReportURL.AddBoxReport', function(){
    	var bodyPanel = boxreport_me.tableObj.bodyPanel;
    	var storeid="boxreport_dataStore";
    	var viewport = Ext.getCmp('boxreport_mainPanel');
    	var store=Ext.data.StoreManager.lookup(storeid);
		var configPanel = Ext.create("BoxReportURL.AddBoxReport",{viewport:viewport,tablepanel:bodyPanel,flag:flag,editcasseid:id,tablestore:store});
		viewport.removeAll(false);
		viewport.add(configPanel);
	    });
    },
    /**
     * 新建页面
     */
    addBoxReport:function(){
    	var flag=0;
	    var configPanel = Ext.require('BoxReportURL.AddBoxReport', function(){
    	var bodyPanel = boxreport_me.tableObj.bodyPanel;
    	var storeid="boxreport_dataStore";
    	var viewport = Ext.getCmp('boxreport_mainPanel');
    	var store=Ext.data.StoreManager.lookup(storeid);
		var configPanel = Ext.create("BoxReportURL.AddBoxReport",{viewport:viewport,tablepanel:bodyPanel,flag:flag,tablestore:store});
		viewport.removeAll(false);
		viewport.add(configPanel);
	    });
    },
    /**
     * 删除
     */
    delBoxReport:function(){
    	var tablePanel=boxreport_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.Msg.alert('提示信息',"请选择删除数据！");
			return;
		}
		var list  ='';
		for(var i=0;i<records.length;i++){
			var record =  records[i];
			var data = record.data;
			var cassette_id = data.box_id;
			list+=cassette_id+',';
		}
		if(list!=''){
			list = list.substring(0,list.length-1);
		}
		 var vo = new HashMap();
	    	     vo.put("idlist", list);
	 	if(!confirm("确定要删除吗？"))
		   return;	
		Rpc({functionId:'ZJ100000114',async:false,success:function(res){
				var storeid="boxreport_dataStore";
	        	var store=Ext.data.StoreManager.lookup(storeid);
				store.remove(records);
		},scope:this
		},vo);
    },
    /**
     * 分析
     */
    analysisBoxreport:function(id){
    	var configPanel = Ext.require('BoxReportURL.SodukuItem', function(){
	    	var bodyPanel = boxreport_me.tableObj.bodyPanel;
	    	var storeid="boxreport_dataStore";
	    	var viewport = Ext.getCmp('boxreport_mainPanel');
	    	var store=Ext.data.StoreManager.lookup(storeid);
			var configPanel = Ext.create("BoxReportURL.SodukuItem",{casseid:id,enterflag:'analysis',
			viewport:viewport,tablepanel:bodyPanel,
			tablestore:store});
			viewport.removeAll(false);
			viewport.add(configPanel);
		 });
    }
});