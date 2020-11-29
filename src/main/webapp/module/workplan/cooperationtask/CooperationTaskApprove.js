/**
	@title 协办任务审批
	@author liubq
	@time 2016-06-8 14:17:54
*/
Ext.define("CooperationTask.CooperationTaskApprove",{
	xtype:'cooperationtaskapprove',
	border:false,
	constructor:function(){//
		this.callParent(arguments);
		coopTaskApprove_me = this;
		coopTaskApprove_me.prinMouse = null;
		coopTaskApprove_me.prinId = "";
		this.initTableData();
    },
    /**
     *	通过参数名称获取链接里的参数值
     */
    getQueryString:function(name){
     	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     	var r = window.location.search.substr(1).match(reg);
     	if(r!=null)return  unescape(r[2]); return null;
	},
    /**
     *  获取初始化数据信息
     */
    initTableData:function(){
    	var vo = new HashMap();
    	vo.put("guidKey",this.getQueryString('guidKey'));
		Rpc({functionId:'WP00001001',success:this.initTableArea},vo);
    },
    /**
     *  加载表格控件显示数据
     */
    initTableArea:function(res){
    	var result = Ext.decode(res.responseText);
    	var tableConfig=result.tableConfig;
		var obj = Ext.decode(tableConfig);
		coopTaskApprove_me.tableObj = new BuildTableObj(obj);
    },
    /**
     *	批准和退回按钮
     */
    approveCoop:function(){
    	if(coopTaskApprove_me.judgeSelection()){
    		Ext.Msg.alert(common.button.promptmessage, "没有选中数据！");
    		return;
    	}
    	Ext.Msg.confirm('提示','确定要批准该协办任务申请吗？',
      	  function(btn){
        	if(btn=='yes'){
          	  coopTaskApprove_me.abc(1);							
        	}
      	},this);
    },
    backCoop:function(){
    	if(coopTaskApprove_me.judgeSelection()){
    		Ext.Msg.alert(common.button.promptmessage, "没有选中数据！");
    		return;
    	}
    	Ext.Msg.confirm('提示','确定要退回该协办任务申请吗？',
      	  function(btn){
        	if(btn=='yes'){
          	  coopTaskApprove_me.abc(2);							
        	}
      	},this);
    },
    judgeSelection:function(){
    	var selections = coopTaskApprove_me.tableObj.tablePanel.getSelectionModel().getSelection();
    	if(selections.length == 0)
			return true;
		else
			return false;
    },
    abc:function(type){
    	var selections = coopTaskApprove_me.tableObj.tablePanel.getSelectionModel().getSelection();
		var selectedArr = new Array();
		for(var p in selections) {
			selectedArr.push(selections[p].data.p1001_e);
		}
		var vo = new HashMap();
		vo.put("selectedArr",selectedArr);
		vo.put("type",type);
		Rpc({functionId:'WP00001004',success:coopTaskApprove_me.refreshCoopTaskApp,scope:this},vo);
    },
    /**
     *  刷新数据
     */
    refreshCoopTaskApp:function(){
		coopTaskApprove_me.tableObj.tablePanel.getStore().reload();
	},
    /**
     *  变更协办人
     */
     changeCooper:function(p1015, cell, record){
	    var element =  "<div style='height:30px;width:100%;' onmouseout=coopTaskApprove_me.prinMouseOut('";
	        element += record.data.p1001_e;
	        element += "') onmouseover=coopTaskApprove_me.prinMouseOver('";
	        element += record.data.p1001_e;
	        element += "')>";
	        element += "<span id='coopPerson"+record.data.p1001_e+"' style='height:30px;z-index:5555;height:30px;padding-top:0px;padding-bottom:0px;line-height:30px;'>"+p1015+"</span>";
	        element += "<div id="+record.data.p1001_e+" onclick=coopTaskApprove_me.changePrin('"+record.data.p1001_e+"','"+record.data.guidke_owner+"','"+record.data.guidke_creater+"','"+record.data.guidke_creater_sp+"','"+record.data.p0800+"') style='height:30px;position:absolute;z-index:5555;height:30px;padding-top:0px;padding-bottom:0px;line-height:30px;cursor:pointer;display:none;'><font style='color:#549fe3'>&nbsp;&nbsp;转给他人负责&nbsp;&nbsp;</font></div>";
     	    element += "</div>";
     	return element;
     },
     /**
      *  鼠标移出事件
      */
     prinMouseOut:function(id){
     	if (prinMouse && (coopTaskApprove_me.prinId == (id))) {
			return false;
		}
		try {
			if (prinMouse) {
				clearTimeout(prinMouse);
			}
			var zzDiv = document.getElementById(id);
			if(zzDiv)
				zzDiv.style.display = "none";
		} catch (err) {
		}
     },
     /**
      *  鼠标移入事件
      */
     prinMouseOver:function(id){
     	if (coopTaskApprove_me.prinId == (id)) {
			coopTaskApprove_me.prinId = "";
			return false;
		}
		prinMouse = setTimeout(function() {
				var zzDiv = document.getElementById(id);
				if(zzDiv)
					zzDiv.style.display = "inline";
				coopTaskApprove_me.prinId = id;
			}, 500);
     },
     //变更协办人
     changePrin:function(rowIndex,guidkey,creater,creater_sp,p0800){
		if (prinMouse) {
			clearTimeout(prinMouse);
		}
		var btn = document.getElementById(rowIndex);
		var picker = new PersonPicker({
				multiple: false,
				isMiddle:true,//是否居中显示
                text : "选择",
                titleText : "选择",
                callback : function(c) {
                	var vo = new HashMap();
                	vo.put("p1001",rowIndex);
                	vo.put("p0800",p0800);
                	vo.put("id",c.id);
                	vo.put("name",c.name);
                	vo.put("guidkey",guidkey);
                	vo.put("creater",creater);
                	vo.put("creater_sp",creater_sp);
                	Rpc({functionId:'WP00001006',success:function(res){
                		var result = Ext.decode(res.responseText);
                		var message = result.flag;
                		if(message=="same"){
                			return;
                		}else if(message=="different"){
                			coopTaskApprove_me.refreshCoopTaskApp();
                		}
                	},scope:this},vo);
                }
            }, btn);
    	picker.open();
	}
});