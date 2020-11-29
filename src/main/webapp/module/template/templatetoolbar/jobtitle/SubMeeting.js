/**
 * 上会-选择评审会议
 */
Ext.define('JobtitleUL.SubMeeting',{
        win:'',	//页面要生成的窗口
        tabid:'0',
		ins_id:'0',
		taskid:'0',
		sp_batch:'0',
		batch_task:'0',
		templPropety:'',
		newFlag:false,
        constructor:function(config){ 
            subMeeting_me=this;
			if (config.templPropety){//新人事异动调用
			   this.newFlag=true;
			   this.templPropety=config.templPropety;
               this.tabid =config.templPropety.tab_id+"";
               this.taskid =config.templPropety.task_id+"";
			}
			else {
				this.ins_id =config.ins_id+"";
				this.tabid =config.tabid+"";
				this.taskid =config.taskid+"";
				this.sp_batch =config.sp_batch+"";
				this.batch_task =config.batch_task+"";
			}
			this.createWindow();				
        },
		 createWindow:function()  
		 {
			 var meettingStore = this.getMeettingStore();
			 /*评审会议列表*/
        	var dateSelect = Ext.widget('combobox', {
        		id:"meettingCombo",
        		width:350,
        		labelAlign:'left',
        		labelWidth:75,
        	    fieldLabel: '评审会议：',
        	    store: meettingStore,
        	    editable:false,
        	    queryMode: 'local',
        	    displayField: 'w0303',
        	    valueField: 'w0301',
        	    renderTo: Ext.getBody(),
        	    listeners:{
        	    	afterrender:function(combo){
        	    		if(meettingStore.getCount()>0)
        	    			combo.setValue(meettingStore.getAt(0))
        	    	}
        	    }
        	});
        	
	   		win=Ext.widget("window",{
	   		  id:'subMeetingWin',
	          title:'评审会议',  
	          height:150,  
	          width:400,
			  modal:true,			  
			  layout: {
        	        type: 'vbox',
        	        align: 'stretch',
        	        align: 'center',//水平居中 haosl 20160822
        	        pack :'center'
        	    },
	          items: [{
	        	  xtype:'panel',
	        	  id:'commiteePanelId',
	        	  border:false,
	        	  layout:{  
	             	type:'vbox',  
	              	pack:'center'
	              },
	              defaults:{  
	    	             margins:'15,0,0,0'  
	    	      },
	              items:[dateSelect]
	              }],
			  bbar:[
		          		{xtype:'tbfill'},
		          		{
		          			text:common.button.ok,
		          			margin:'0 10 0 0',
		          			scope:this,
		          			handler:function(){
		          				Ext.MessageBox.wait('正在处理上会数据...', common.msg.wait)
		          				var dataValue = dateSelect.getValue();
		          				if (!dataValue||dataValue==""){
		          					Ext.showAlert('请选择评审会议!');
									return;
		          				}
		          				var map = new HashMap();
								map.put("w0301",dataValue+"");
								var selecRec = dateSelect.getSelection();
//								if(selecRec.committee_id)
//									map.put("committee_id",selecRec.committee_id);
								if(selecRec.data.sub_committee_id)
									map.put("sub_committee_id",selecRec.data.sub_committee_id);
								if (subMeeting_me.newFlag){
								    initPublicParam(map,subMeeting_me.templPropety);
								}
								else {
									map.put("tabid",this.tabid);
									map.put("ins_id",this.ins_id);
									map.put("taskid",this.taskid);
									map.put("sp_batch",this.sp_batch);
									map.put("batch_task",this.batch_task);
								}
							    Rpc({functionId:'MB00002001',success:function(form,action){
								 		Ext.MessageBox.hide();
							    		var result = Ext.decode(form.responseText);
										if(result.succeed){
											win.close();
											Ext.showAlert('提交成功！');
										}else{
											Ext.showAlert(result.message);
										}
								 }},map);
		          			}
		          		},
		          		{
		          			text:common.button.cancel,
		          			handler:function(){
		          				win.close();
		          			}
		          		},
		          		{xtype:'tbfill'}
		           ]
	    });                               
	    win.show();  
	 },
	 getMeettingStore:function(){
		 /*评审会议store*/
		 var meettingStore =  Ext.create('Ext.data.Store',{
			 id:"meetingStore",
			 fields: ['w0301', 'w0303',"sub_committee_id"]//"committee_id"
		 });
		 Rpc({functionId:'MB00002002', async:false, success:function(form){
			 var response = Ext.decode(form.responseText);
			 var meettingList  = response.datelist;
			 meettingStore.loadData(meettingList,false);
		 },scope:this},new HashMap());
		 return meettingStore;
	 }
 });
