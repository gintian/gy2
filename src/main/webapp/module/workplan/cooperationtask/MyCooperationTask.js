/**
	@title 我的协办任务
	@author liubq
	@time 2016-06-13 14:17:54
*/
Ext.define("MyCooperationTask.MyCooperationTask",{
	xtype:'cooperationtaskapprove',
	border:false,
	constructor:function(){
		this.callParent(arguments);
		myCoopTask_me = this;
		myCoopTask_me.myCoopParams ={};
		myCoopTask_me.myCoopParams.status=0;
		myCoopTask_me.myCoopParams.type=4;
		this.initTableData();
    },
	initTableData:function(){
		var vo = new HashMap();
		Rpc({functionId:'WP00001003',success:this.initTableArea},vo);
    },
    initTableArea:function(res){
    	var result = Ext.decode(res.responseText);
    	var tableConfig=result.tableConfig;
		var obj = Ext.decode(tableConfig);
		myCoopTask_me.tableObj = new BuildTableObj(obj);
		var tooltbar = Ext.widget("toolbar",{
			   border:false,
	    	   items:['<span>审批情况&nbsp;&nbsp;:&nbsp;&nbsp;</span>',
	    	   		 '<a class="myselect" name="0" href="javascript:myCoopTask_me.queryCoopTask(1,0,0)">全部</a>&nbsp;&nbsp;',
	    	   		 '-',
	    	   		 '<a class="myselect1" name="1" href="javascript:myCoopTask_me.queryCoopTask(0,1,0)">待批</a>&nbsp;&nbsp;',
	    	   		 '<a class="myselect1" name="2" href="javascript:myCoopTask_me.queryCoopTask(0,2,0)">已批</a>&nbsp;&nbsp;',
	    	   		 '<a class="myselect1" name="3" href="javascript:myCoopTask_me.queryCoopTask(0,3,0)">已退回</a>&nbsp;&nbsp;',
	    	   		 '-',
	    	   		 '<a class="myselect2" name="4" href="javascript:myCoopTask_me.queryCoopTask(0,0,4)">我发起的</a>&nbsp;&nbsp;',
	    	   		 '<a class="myselect2" name="5" href="javascript:myCoopTask_me.queryCoopTask(0,0,5)">我收到的</a>&nbsp;&nbsp;'
	    	   		 ]
	    });
		myCoopTask_me.tableObj.insertItem(tooltbar,0);
		myCoopTask_me.queryCoopTask(0,0,4);
    },
    queryCoopTask:function(all,status,tasktype){
		var vo = new HashMap();
		if(parseInt(all,10)!=0){
			myCoopTask_me.myCoopParams.status=0;
			myCoopTask_me.myCoopParams.type=0;
		}
		if(parseInt(status,10)!=0)
			myCoopTask_me.myCoopParams.status = status;
		if(parseInt(tasktype,10)!=0)
			myCoopTask_me.myCoopParams.type = tasktype;
		vo.put("all", all);
		vo.put("status", myCoopTask_me.myCoopParams.status);
		vo.put("tasktype", myCoopTask_me.myCoopParams.type);
		vo.put("subModuleId",myCoopTask_me.tableObj.config.subModuleId);
		Rpc({functionId : 'WP00001007',success :myCoopTask_me.refreshMyCoopTask}, vo);
		var allSelect = Ext.query(".myselect");
		var firstSelect = Ext.query(".myselect1");
		var secondSelect = Ext.query(".myselect2");
		if(parseInt(all,10)==1){
			allSelect[0].style.textDecoration = "underline";
			for (var i = 0; i < firstSelect.length; i++) {
				firstSelect[i].style.textDecoration = "none";
			}
			for (var i = 0; i < secondSelect.length; i++) {
				secondSelect[i].style.textDecoration = "none";
			}
		}else if(parseInt(status,10)!=0){
			allSelect[0].style.textDecoration = "none";
			for (var i = 0; i < firstSelect.length; i++) {
				if (firstSelect[i].name == status) 
					firstSelect[i].style.textDecoration = "underline";
				else
					firstSelect[i].style.textDecoration = "none";
			}
		}else if(parseInt(tasktype,10)!=0){
			allSelect[0].style.textDecoration = "none";
			for (var i = 0; i < secondSelect.length; i++) {
				if (secondSelect[i].name == tasktype)
					secondSelect[i].style.textDecoration = "underline";
				else
					secondSelect[i].style.textDecoration = "none";
			}
		}
	},
	refreshMyCoopTask:function(){
		myCoopTask_me.tableObj.tablePanel.getStore().reload();
	},
	coopRemind:function(){
		var selections = myCoopTask_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selections.length == 0){
			Ext.Msg.alert(common.button.promptmessage, "没有选中数据！");
			return ;
		}
		var selectedArr = new Array();
		for(var p in selections) {
			if(selections[p].data.p1019!="待批"){
				Ext.Msg.alert(common.button.promptmessage, "选中了已批或已退回任务不能发送提醒！");
				return ;
			}
			selectedArr.push(selections[p].data.p1001);
		}
		var vo = new HashMap();
		vo.put("selectedArr",selectedArr);
		Rpc({functionId : 'WP00001005',success :Ext.Msg.alert("提示",'发送消息成功!')}, vo);
	}
});