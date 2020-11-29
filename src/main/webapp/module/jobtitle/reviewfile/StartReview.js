Ext.define('StartReviewURL.StartReview',{
	idlist:'',
	type:'',
	isHiddenRadio1:true,
	isHiddenRadio2:true,
	isHiddenRadio3:true,
	isHiddenRadio4:true,
	method:"",//操作类型
	//w0555:"",//评审阶段
	constructor:function(config){
		startreview_me = this;
		this.idlist = config.dataList;//获取前台选中或全选后反选的会议id与申请人id
		this.isHiddenRadio1 =  config.isHiddenRadio1;
		this.isHiddenRadio2 =  config.isHiddenRadio2;
		this.isHiddenRadio3 =  config.isHiddenRadio3;
		this.isHiddenRadio4 = config.isHiddenRadio4;
		if(this.isHiddenRadio1
				&&this.isHiddenRadio2
				&&this.isHiddenRadio3
				&&this.isHiddenRadio4){
			Ext.showAlert("该会议没有启用任何阶段！");
			return;
		}
		this.method = config.method;
		//this.w0555 = config.w0555;
     	//对引入方式初始化赋值，年、月为业务日期
		this.init();
	},
	init:function(){
		var temp = "";
		if(this.method=='examine')
			temp = "审核";
		else
			temp="评审";
		var win = Ext.create('Ext.window.Window',{
	  		title:'选择'+temp+'阶段',
	  		closeToolText : '',
	 		id:'checklist',
	 		//minWidth: 360,  
	        height: 150,  
	        resizable: false,  
	        modal: true,
	        border:false,
	       	buttonAlign:'center',
	       	bodyStyle: 'background:#ffffff;',
	       	items:[{
	            xtype: 'fieldcontainer',
	            defaultType: 'radio',
	            layout:'table',
	            id:'radiofield',
	            padding:'0 10 0 10',
	            items: [
	           		{
	                    boxLabel  : zc.reviewfile.step4showtext,//显示二级单位评议阶段
	                    name      : 'topping',
	                    inputValue: '4',
	                    id        : 'radio4',
	                  //  checked   : this.w0555==4?true:false,
	                    padding   : '20 10 0 5',
	                    hidden    : this.isHiddenRadio4
	                },{
	                    boxLabel  : zc.reviewfile.step3showtext,//显示同行专家阶段
	                    name      : 'topping',
	                    inputValue: '3',
	                    id        : 'radio3',
						//checked   : this.w0555==3?true:false,	
	                    padding   : '20 10 0 5',
	                    hidden    : this.method == 'examine' || (this.method == 'vote' && this.isHiddenRadio3)
	                }, {
	                    boxLabel  : zc.reviewfile.step2showtext,//显示学科组阶段
	                    name      : 'topping',
	                    inputValue: '2',
	                    id        : 'radio2',
	                   // checked   : this.w0555==2?true:false,
	                    padding   : '20 10 0 5',
	                    hidden    : this.isHiddenRadio2
	                },{
	                    boxLabel  : zc.reviewfile.step1showtext,//显示评委会阶段
	                    name      : 'topping',
	                    inputValue: '1',
	                    id        : 'radio1',
	                   // checked   : this.w0555==1?true:false,
	                    padding   : '20 10 0 5',
	                    hidden    : this.isHiddenRadio1
	                }
	            ]
	        }],
	    	bbar:[{xtype:'tbfill'},{
	        	text:'确定',
	        	margin:'0 10 0 0',
	        	listeners:{
	        		'click':function(){
		        		startreview_me.radio1=Ext.getCmp('radio1').getValue();
		        		startreview_me.radio2=Ext.getCmp('radio2').getValue();
		        		startreview_me.radio3=Ext.getCmp('radio3').getValue();
		        		startreview_me.radio4=Ext.getCmp('radio4').getValue();
		        		if(!(startreview_me.radio1||startreview_me.radio2||startreview_me.radio3||startreview_me.radio4)){
		        			Ext.showAlert("请"+win.getTitle()+"！");
		        			return;
		        		}
		        		
		        		if(startreview_me.method == 'examine' || startreview_me.radio3){// 同行专家投票不分组 chent 20180130 add
		        			startreview_me.startReview();
		        			
		        		}else if(startreview_me.method == 'vote'){
		        			var review_links = '';
		        			if(startreview_me.radio1){
		        				review_links = '1';
		        			}else if(startreview_me.radio2){
		        				review_links = '2';
		        			}else if(startreview_me.radio3){
		        				review_links = '3';
		        			}else if(startreview_me.radio4){
		        				review_links = '4';
		        			}
		        			
			        		var obj = new Object();
							obj.w0301_e = JobTitleReviewFile.schemeStateArray[2].substring(6);
							obj.review_links = review_links;
							obj.startReviewFunc = startreview_me.startReview;
							Ext.require('ReviewFileURL.ReviewDiff', function(){
								RevewFileGlobal = Ext.create("ReviewFileURL.ReviewDiff", obj);
							});
							if(Ext.getCmp('checklist')){
								Ext.getCmp('checklist').close();
							}
		        		}
	        		}
	        	}
	        },{
	        	text:zc.label.cancel,
	        	listeners:{
	        		'click':function(){
	        			win.close();
	        		}
	        	}
	        },{xtype:'tbfill'}]
	       
		});
		win.show();
    },
    startReview : function(idlist, categories_id){
    	var map = new HashMap();
		map.put("inReview",startreview_me.radio1);
		map.put("inExpert",startreview_me.radio2);
		map.put("exExpert",startreview_me.radio3);
		map.put("inCollege",startreview_me.radio4);
		map.put("method",startreview_me.method);
		if(idlist != undefined){
			map.put("idlist", idlist);
			map.put("categories_id", categories_id);
		} else {
			map.put("idlist", startreview_me.idlist);
		}
			
		Rpc({functionId:'ZC00003006',async:false,success: function(form){
			var data = Ext.decode(form.responseText);
				if(data.msg)
					Ext.showAlert(data.msg);
				else
					Ext.showAlert(data.message);
				if(Ext.getCmp('checklist')){
					Ext.getCmp('checklist').close();
				}
				var store = Ext.data.StoreManager.lookup('reviewFile_dataStore');
				store.load();
		}},map);
    }
});